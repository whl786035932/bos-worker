package cn.videoworks.worker.function;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.client.MD5GearmanClient;
import cn.videoworks.worker.constant.ResponseDictionary;
import cn.videoworks.worker.dto.CmsImageDto;
import cn.videoworks.worker.dto.CmsMovieDto;
import cn.videoworks.worker.dto.CmsStorageDto;
import cn.videoworks.worker.dto.PublishImageDto;
import cn.videoworks.worker.dto.PublishMovieDto;
import cn.videoworks.worker.dto.PublishStorageDto;
import cn.videoworks.worker.dto.PublishWorkerDto;
import cn.videoworks.worker.exception.PosterNotExistException;
import cn.videoworks.worker.util.ApiResponse;
import cn.videoworks.worker.util.DateUtil;
import cn.videoworks.worker.util.FileUtil;
import cn.videoworks.worker.util.Md5Util;
import cn.videoworks.worker.util.PropertiesUtil;
import cn.videoworks.worker.util.ResponseStatusCode;
import net.sourceforge.pinyin4j.PinyinHelper;

public class CmsFunction implements GearmanFunction {
	public String storageClient;

	private static final Logger log = LoggerFactory.getLogger(CmsFunction.class);

	@Override
	public byte[] work(String arg0, byte[] data, GearmanFunctionCallback callback) {
		log.info("CMS funcation 收到的data=" + new String(data));
		ApiResponse cmsStorage = null;
		String dataJson = new String(data);
		try {

			PublishWorkerDto wokrerDto = JsonConverter.parse(dataJson, PublishWorkerDto.class);
			PublishStorageDto publishDto = wokrerDto.getExtraData();
			String publishId = wokrerDto.getPublishId();
			Integer data_transfer_status = wokrerDto.getDataTransferStatus();
			// 调用cms的入库接口
			if (publishDto == null) {
				cmsStorage.setStatusCode(ResponseDictionary.SERVEREXCEPTION);
				cmsStorage.setMessage("解析cms数据失败");
			} else {
				cmsStorage = cmsStorage(publishId, data_transfer_status, publishDto);
			}
		} catch (Exception e) {
			cmsStorage = new ApiResponse();
			cmsStorage.setStatusCode(ResponseStatusCode.INTERNAL_SERVER_ERROR);
			cmsStorage.setMessage(e.getMessage());

		}
		String cmsStorageJsonstr = JsonConverter.format(cmsStorage);
		return cmsStorageJsonstr.getBytes();
	}

	/**
	 * cms的入库接口
	 * 
	 * @param cmsStorageDto
	 */
	public ApiResponse cmsStorage(String publishId, Integer data_transfer_status, PublishStorageDto publishDto) {
		ApiResponse apiResponse = new ApiResponse();

		RestTemplate restTemplate = new RestTemplate();
		String url;
		try {
			url = PropertiesUtil.get("cms_stroage_api");
			CmsStorageDto cmsStorageDto = cvtPubDto2StorageDto(publishDto);
			ResponseEntity<Map> postForEntity = restTemplate.postForEntity(url, cmsStorageDto, Map.class);
			Map restResponse = postForEntity.getBody();
			System.out.println("cms的接口返回结果=" + restResponse);
			apiResponse = buildResponseData(publishId, data_transfer_status, restResponse, cmsStorageDto, publishDto);

		}catch(PosterNotExistException e) {
			e.printStackTrace();
			apiResponse.setStatusCode(ResponseDictionary.EXTERNALINTERFACECALLSEXCEPTION);
			apiResponse.setMessage(e.getMessage());
			HashMap<String, Object> data = new HashMap<>();
			data.put("publishId", publishId);
			data.put("dataTransferStatus", data_transfer_status);
			apiResponse.setData(data);
		}catch (Exception e) {
			e.printStackTrace();
			apiResponse.setStatusCode(ResponseDictionary.EXTERNALINTERFACECALLSEXCEPTION);
			apiResponse.setMessage("请求cms入库接口报错：" + e.getMessage());
			HashMap<String, Object> data = new HashMap<>();
			data.put("publishId", publishId);
			data.put("dataTransferStatus", data_transfer_status);
			apiResponse.setData(data);
		}
		System.out.println("返回的数据=" + JsonConverter.format(apiResponse));
		return apiResponse;
	}

	public ApiResponse buildResponseData(String publishId, Integer data_transfer_status, Map restResponse,
			CmsStorageDto dto, PublishStorageDto publish) {
		HashMap<String, Object> data = new HashMap<>();
		ApiResponse apiResponse = new ApiResponse();
		Integer cmsStatuscode = Integer.valueOf(String.valueOf(restResponse.get("statusCode")));
		apiResponse.setStatusCode(cmsStatuscode);
		String message = String.valueOf(restResponse.get("message"));
		if (cmsStatuscode != ResponseDictionary.SUCCESS) {

			List cms_responseData = (List) restResponse.get("data"); // cms入库接口的返回结果
			message ="woker注入CMS失败："+message+ JsonConverter.format(cms_responseData);
		}
		apiResponse.setMessage(message);

		data.put("publishId", publishId);
		data.put("dataTransferStatus", data_transfer_status);
		// 组织额外的参数
		HashMap<String, Object> extraData = new HashMap<String, Object>();
		String mediaAssetId = publish.getMediaAssetId();
		extraData.put("mediaAssetId", mediaAssetId);

		List<CmsMovieDto> movies = dto.getMovies();
		extraData.put("medias", movies);

		List<CmsImageDto> images = dto.getImages();
		ArrayList<Map<String, Object>> posters = new ArrayList<Map<String, Object>>();
		for (CmsImageDto cmsImageDto : images) {
			HashMap<String, Object> posterMap = new HashMap<String, Object>();
			String id = cmsImageDto.getId();
			Integer size = cmsImageDto.getSize();
			posterMap.put("id", id);
			posterMap.put("size", size);
			posters.add(posterMap);

		}
		extraData.put("posters", posters);
		data.put("extraData", extraData);
		apiResponse.setData(data);
		return apiResponse;
	}

	public static String getPinYinHeadChar(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char word = str.charAt(i);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				sb.append(pinyinArray[0].charAt(0));
			} else {
				sb.append(word);
			}
		}
		return sb.toString();
	}

	/**
	 * 将发布过来的DTO转成CMS的入库Dto
	 * 
	 * @param publishDto
	 * @return
	 * @throws PosterNotExistException 
	 */
	private CmsStorageDto cvtPubDto2StorageDto(PublishStorageDto publishDto) throws PosterNotExistException {
		CmsStorageDto dto = new CmsStorageDto();
		dto.setAsset_id(publishDto.getAssetId());
		String title = publishDto.getTitle();
		dto.setTitle(title);
		String title_abbr = publishDto.getTitle_abbr();
		if (!StringUtils.isNotBlank(title_abbr)) {
			title_abbr = getPinYinHeadChar(title);
		}
		dto.setTitle_abbr(title_abbr);
		dto.setType(publishDto.getType());
		dto.setDescription(publishDto.getDescription());
		String broadCastTime = publishDto.getBroadCastTime();
		long publishTime = DateUtil.geTimeYMDHMSLong(broadCastTime);
		dto.setPublish_time(publishTime);
		List<String> tags = publishDto.getTags();
		tags = tags != null ? tags : new ArrayList<String>();
		dto.setTags(tags);

		List<String> classifications = publishDto.getClassifications();
		classifications = classifications != null ? classifications : new ArrayList<String>();
		dto.setClassifications(classifications);

		List<String> areas = publishDto.getAreas();
		areas = areas != null ? areas : new ArrayList<String>();
		dto.setAreas(areas);

		String source = publishDto.getSource();
		String cp = publishDto.getCp();
		cp = cp != null ? cp : source;

		dto.setCp(cp);

		dto.setSource(source);
		dto.setSource_channel(publishDto.getChannel());
		dto.setSource_column(publishDto.getColumn());

		List<CmsMovieDto> movies = convt2CmsMovie(publishDto.getMedias());
		List<CmsImageDto> images = convt2CmsImage(publishDto.getPosters());

		dto.setMovies(movies);
		dto.setImages(images);
		System.out.println("组织的数据=" + JsonConverter.format(dto));
		return dto;
	}

	public List<CmsMovieDto> convt2CmsMovie(List<PublishMovieDto> pubs) {
		ArrayList<CmsMovieDto> dtos = new ArrayList<CmsMovieDto>();
		String host ="";
		String port="";
		try {
			 host = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
			 port = PropertiesUtil.getPropertiesUtil().get("gearman.port");
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (PublishMovieDto publish : pubs) {

			CmsMovieDto cmsMovieDto = new CmsMovieDto();
			cmsMovieDto.setId(publish.getId());
			String sourceUrl = publish.getSourceUrl();
			cmsMovieDto.setUrl(publish.getSourceUrl());
			// 截取md5和file_name
			cmsMovieDto.setFile_name(getFileName(sourceUrl));
			cmsMovieDto.setDuration(publish.getDuration());
			cmsMovieDto.setType(1);
			cmsMovieDto.setCheck_sum(checkSum(publish.getTargetUrl()));
			
			// 获取比特率，宽高, size  ,从worker中获取
			Map<String,Object> dataMap = getMediaInfo(host,port,sourceUrl);
			String height =String.valueOf( dataMap.get("height"));
			String width=  String.valueOf( dataMap.get("width"));
			Double bitrate =Double.valueOf(String.valueOf( dataMap.get("bitrate")));
			Integer size =Integer.valueOf(String.valueOf( dataMap.get("size")));
			
			cmsMovieDto.setWidth(width);
			cmsMovieDto.setHeight(height);
			cmsMovieDto.setSize(size);
			cmsMovieDto.setBitrate(bitrate);
			
			dtos.add(cmsMovieDto);
		}

		return dtos;
	}
	
	
	public Map<String,Object> getMediaInfo(String host,String port,String url){
		MD5GearmanClient cdnGearmanClient = MD5GearmanClient.getCDNGearmanClient(host, Integer.valueOf(port), "md5sum");
		HashMap<String, String> hashMap = new HashMap<String,String>();
		hashMap.put("filepath", url);
		String format = JsonConverter.format(hashMap);
		Map<String, Object> submitJob = cdnGearmanClient.submitMediaInfoJob(format,"mediainfo");
		Map<String,Object> dataMap = (Map<String, Object>) submitJob.get("data");
		return dataMap;
	}

	public List<CmsImageDto> convt2CmsImage(List<PublishImageDto> pubs) throws PosterNotExistException {
		ArrayList<CmsImageDto> dtos = new ArrayList<CmsImageDto>();
		for (PublishImageDto pub : pubs) {
			CmsImageDto dto = new CmsImageDto();
			String sourceUrl = pub.getSourceUrl();
			dto.setId(pub.getId());
			dto.setUrl(sourceUrl);
			dto.setFile_name(getFileName(pub.getSourceUrl()));
			dto.setType(pub.getType());
			dto.setCheck_sum(checkSum(pub.getSourceUrl()));
			//// 获取，宽高, size，从worker中获取
			
			dto.setWidth(pub.getWidth());
			dto.setHeight(pub.getHeight());
			
			boolean exists = FileUtil.exists(sourceUrl);
			if(exists) {
				File file = new File(sourceUrl);
				try {
					byte[] source = FileUtil.getByte(file);
					dto.setSize(source.length);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				throw new PosterNotExistException(sourceUrl);
			}
			dto.setSize(20);
			
			dtos.add(dto);

		}
		return dtos;
	}

	public String checkSum(String url) {
		// String
		// url="http://bj.bcebos.com/videoworks-8/ce11b0572c9c0bbad15e29045823fc58";
		// String[] split = url.split("/");
		// String string = split[split.length-1];
		// return string;

		// 调用MD5 worker
		String check_sum = "";
		String host = FileUtil.readProperties("bos-worker.properties", "gearman.ip");
		String port = FileUtil.readProperties("bos-worker.properties", "gearman.port");
		MD5GearmanClient cdnGearmanClient = MD5GearmanClient.getCDNGearmanClient(host, Integer.valueOf(port), "md5sum");
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("filepath", url);
		String format = JsonConverter.format(hashMap);
		Map<String, Object> submitJob = cdnGearmanClient.submitMd5Job(format, "md5sum");
		Integer statusCode = Integer.valueOf(String.valueOf(submitJob.get("statusCode")));
		if (statusCode != ResponseStatusCode.OK) {
			File file = new File(url);
			byte[] source = null;
			try {
				source = FileUtil.getByte(file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			check_sum = Md5Util.getMD5(source);
		} else {
			check_sum = String.valueOf(submitJob.get("check_sum"));
		}
//		check_sum="3624612e2fab366ca98b90060716b1ff";

		return check_sum;

	}

	public String getFileName(String url) {
		// String url="D:\\test\\test.jpg";
		File file = new File(url);
		String fileName = file.getName();
		return fileName;

	}

}
