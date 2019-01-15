package cn.videoworks.worker.function;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.constant.ResponseDictionary;
import cn.videoworks.worker.dto.CmsMovieDto;
import cn.videoworks.worker.dto.DataDto;
import cn.videoworks.worker.dto.YouKuDto;
import cn.videoworks.worker.util.ApiResponse;
import cn.videoworks.worker.util.DateUtil;
import cn.videoworks.worker.util.MediaInfoUtil;
import cn.videoworks.worker.util.PropertiesUtil;
import cn.videoworks.worker.util.YouKuUtil;

public class YouKuFunction implements GearmanFunction{
	private static final Logger log = LoggerFactory.getLogger(YouKuFunction.class);
	@SuppressWarnings("unused")
	@Override
	public byte[] work(String arg0, byte[] data, GearmanFunctionCallback arg2)throws Exception {
		log.info("YouKuV2 funcation 收到的data=" + new String(data));
		ApiResponse response = null;
		String fileSize = "";
		HashMap<String, Object> dataReturn = new HashMap<String, Object>();
		Map<String, Object> extra =  new HashMap<String, Object>();
		try {
			String dataJson = new String(data);
			DataDto dto = JsonConverter.parse(dataJson, DataDto.class);
			if (dto == null) {
				log.info("参数为空!");
				response = new ApiResponse();
				response.setStatusCode(ResponseDictionary.ERROR);
				response.setMessage("参数转换失败");
				response.setData(dataJson);
				String returnStr = JsonConverter.format(response);
				return returnStr.getBytes();
			}
			YouKuDto wokrerDto = null;
			dataReturn.put("publishId", dto.getPublishId());
			dataReturn.put("dataTransferStatus", dto.getDataTransferStatus());
			if (dto.getExtraData() != null) {
				// 生成上传参数
				log.info("开始转成media参数!");
				String json = JsonConverter.format(dto.getExtraData());
				wokrerDto = JsonConverter.parse(json, YouKuDto.class);
				log.info("设置特殊media参数!");
				if (wokrerDto != null) {
					wokrerDto = bulidParams(wokrerDto);
				}
			}
			if (wokrerDto != null) {
				// 生成部分返回参数
				extra = returnExtraData(wokrerDto);
				dataReturn.put("extraData", extra);
				// 文件大小
				String filePath = wokrerDto.getFilePath();
				File file = new File(filePath);
				FileInputStream fis = new FileInputStream(file);
				Integer size = fis.available();
				fileSize = String.valueOf(size);
				YouKuUtil.initParam();
				String accessToken = YouKuUtil.refreshToken();
				if(accessToken == null){
					response = new ApiResponse();
					response.setStatusCode(ResponseDictionary.ERROR);
					response.setMessage("YouKu数据上传失败,失败原因:刷新token失败");
					response.setData(dataReturn);
					String returnStr = JsonConverter.format(response);
					return returnStr.getBytes();
				}
				YouKuUtil.initial(accessToken,filePath,fileSize);
				String cutType = wokrerDto.getCutType();
				String title = wokrerDto.getProperTitle();
				if (cutType != null) {
					if (cutType.equals("0")) {
						title = wokrerDto.getColumn()+wokrerDto.getPlayyear()+wokrerDto.getPlaymonth()+wokrerDto.getPlaydate()+wokrerDto.getTitle()+" 高清";
					} else {
						title = wokrerDto.getCol_alias()+wokrerDto.getPlayyear()+wokrerDto.getPlaymonth()+wokrerDto.getPlaydate()+wokrerDto.getTitle()+" 高清";
					}
				}
				Map<String, String> params = new HashMap<>();
				params.put("title", title);
				params.put("tags", wokrerDto.getTags());
				params.put("description", wokrerDto.getTitle());
				String result = YouKuUtil.upload(params,wokrerDto);
				if (result != null) {
					response = new ApiResponse();
					response.setStatusCode(ResponseDictionary.SUCCESS);
					response.setData(dataReturn);
					response.setMessage("YouKu数据上传成功");
				} else {
					response = new ApiResponse();
					response.setStatusCode(ResponseDictionary.ERROR);
					response.setData(dataReturn);
					response.setMessage("YouKu数据上传失败");
				}
				
			} else {
				response = new ApiResponse();
				response.setStatusCode(ResponseDictionary.ERROR);
				response.setData(dataReturn);
				response.setMessage("YouKu数据上传失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("返回结果信息:" + JsonConverter.format(response));
		String returnStr = JsonConverter.format(response);
		return returnStr.getBytes();
	}
	
	/**
	 * 生成设置参数.
	 * 
	 * @param dto
	 * @return
	 */
	private YouKuDto bulidParams(YouKuDto dto) {
		if (dto == null) {
			return null;
		}
		if (dto.getMedias() != null) {
			for (int i = 0; i < dto.getMedias().size(); i++) {
				dto.setFilePath(dto.getMedias().get(i).getSourceUrl());
				//dto.setShow_id(dto.getMedias().get(i).getId());// 节目id
				break;
			}
		}
		if(dto.getBroadCastTime()!=null){
			// 获取年
			Date date = DateUtil.getDate(dto.getBroadCastTime());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			dto.setPlayyear(sdf.format(date));
			// 获取月
			SimpleDateFormat sdf1 = new SimpleDateFormat("MM");
			dto.setPlaymonth(sdf1.format(date));
			// 获取日
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd");
			dto.setPlaydate(sdf2.format(date));
		}
		return dto;
	}

	/**
	 * 生成返回参数.
	 * @throws Exception 
	 * 
	 */
	public Map<String, Object> returnExtraData(YouKuDto dto) throws Exception {
		//Map<String, Object> returnData = new HashMap<>();
		String host = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
		String port = PropertiesUtil.getPropertiesUtil().get("gearman.port");
		Map<String, Object> media = MediaInfoUtil.getMediaInfo(host, port, dto.getFilePath());
		CmsMovieDto cmsMovieDto = getCmsMovieDto(media);
		if (cmsMovieDto != null) {
			cmsMovieDto.setId(dto.getMedias().get(0).getId());
		}
		List<CmsMovieDto> medias = new ArrayList<>();
		medias.add(cmsMovieDto);
		HashMap<String, Object> extraData = new HashMap<String, Object>();
		extraData.put("medias", medias);
		extraData.put("mediaAssetId", dto.getMediaAssetId());
		return extraData;
	}
	public CmsMovieDto getCmsMovieDto(Map<String,Object>p){
		CmsMovieDto cms = new CmsMovieDto();
		String height = String.valueOf(p.get("height"));
		String width = String.valueOf(p.get("width"));
		Double bitrate = Double.valueOf(String.valueOf(p.get("bitrate")));
		Integer size = Integer.valueOf(String.valueOf(p.get("size")));
		//cms.setId(media.getId());
		cms.setWidth(width);
		cms.setHeight(height);
		cms.setSize(size);
		cms.setBitrate(bitrate);
		return cms;
		
	}
}

