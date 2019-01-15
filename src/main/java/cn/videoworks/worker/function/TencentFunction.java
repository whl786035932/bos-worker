package cn.videoworks.worker.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import cn.videoworks.worker.dto.MediaDto;
import cn.videoworks.worker.dto.PublishImageDto;
import cn.videoworks.worker.dto.TencentDto;
import cn.videoworks.worker.util.ApiResponse;
import cn.videoworks.worker.util.DateUtil;
import cn.videoworks.worker.util.MediaInfoUtil;
import cn.videoworks.worker.util.PropertiesUtil;
import cn.videoworks.worker.util.TencentUtil;

public class TencentFunction implements GearmanFunction {
	private static final Logger log = LoggerFactory.getLogger(TencentFunction.class);

	public byte[] work(String arg0, byte[] data, GearmanFunctionCallback arg2) throws Exception {
		ApiResponse response = null;
		HashMap<String, Object> dataReturn = new HashMap<String, Object>();
		String dataJson = new String(data);
		log.info("传输参数为:" + dataJson);
		DataDto dto = JsonConverter.parse(dataJson, DataDto.class);
		// 传输参数为空
		if (dto == null) {
			log.info("参数传输错误");
			return resopnse(response, ResponseDictionary.ERROR, "参数传输错误", null);
		}
		// 设置返回参数发布id
		dataReturn.put("publishId", dto.getPublishId());
		dataReturn.put("dataTransferStatus", dto.getDataTransferStatus());
		TencentDto tdto = null;
		// getExtraData 为空
		if (dto.getExtraData() == null) {
			log.info("参数传输ExtraData错误");
			return resopnse(response, ResponseDictionary.ERROR, "参数传输ExtraData错误", dataReturn);
		}
		// 生成上传参数
		String json = JsonConverter.format(dto.getExtraData());
		tdto = JsonConverter.parse(json, TencentDto.class);
		// 生成返回数据
		dataReturn.put("extraData", returnExtraData(tdto));
		// 开始数据准备
		log.info("开始数据准备");
		bulidParam(tdto);
		log.info("第一步开始");
		Map<String, Object> result = TencentUtil.InfoFileGenerator(tdto);
		if (!result.get("statusCode").equals(100000)) {
			return resopnse(response, ResponseDictionary.ERROR, "腾讯数据上传第一步失败", dataReturn);
		}
		// 第二步上传图片
		Map<String, Object> result2 = TencentUtil.BatchFtpUpload4Tencent(tdto);
		if (!result2.get("statusCode").equals(100000)) {
			return resopnse(response, ResponseDictionary.ERROR, "腾讯数据上传第二步失败,失败原因:" + result2.get("message"), dataReturn);
		}
		// 第三步 json注入数据
		Map<String, Object> result3 = TencentUtil.FtpDeployer(tdto);
		if (!result3.get("statusCode").equals(100000)) {
			return resopnse(response, ResponseDictionary.ERROR, "腾讯数据上传第三步失败,失败原因:" + result3.get("message"), dataReturn);
		}
		// 第四步上传视频
		Map<String, Object> result4 = TencentUtil.FtpDeployerFive(tdto);
		if (!result4.get("statusCode").equals(100000)) {
			return resopnse(response, ResponseDictionary.ERROR, "腾讯数据上传第四步失败,失败原因:" + result4.get("message"), dataReturn);
		}
		// 第五步上传视频json信息
		Map<String, Object> result5 = TencentUtil.uploadVideoJosn(tdto);
		if (!result5.get("statusCode").equals(100000)) {
			return resopnse(response, ResponseDictionary.ERROR, "腾讯数据上传第五步失败,失败原因:" + result5.get("message"), dataReturn);
		}
		return resopnse(response, ResponseDictionary.SUCCESS, "腾讯数据上传成功", dataReturn);
	}

	/**
	 * 生成参数.
	 */
	public TencentDto bulidParam(TencentDto dto) {
		String jsonMessage = "";
		List<String> videoJson = new ArrayList<>();
		switch (dto.getCtype()) {
		case "23":
			jsonMessage = bulidNewsJsonMessage(dto);
			if (dto.getMedias() != null && dto.getMedias().size() > 0) {
				for (MediaDto me : dto.getMedias()) {
					videoJson.add(JsonConverter.format(bulidNewsViodeJson(dto)));
				}
			}
			break;
		case "10":
			// 综艺
			jsonMessage = bulidVarietyJsonMessage(dto);
			if (dto.getMedias() != null && dto.getMedias().size() > 0) {
				for (MediaDto me : dto.getMedias()) {
					videoJson.add(JsonConverter.format(bulidVarietyViodeJson(dto)));
				}
			}
			break;
		default:
			jsonMessage = bulidOthersJsonMessage(dto);
			if (dto.getMedias() != null && dto.getMedias().size() > 0) {
				for (MediaDto me : dto.getMedias()) {
					videoJson.add(JsonConverter.format(bulidOthersViodeJson(dto)));
				}
			}
			break;
		}
		String targetPath = "";
		String nyr = "";// 年月日
		String nyrsfm = "";// 年月日时分秒
		SimpleDateFormat ymdhms= new SimpleDateFormat("yyyyMMddhhmmss");
		SimpleDateFormat ymdh = new SimpleDateFormat("yyyyMMdd");
		if (dto.getStartTime() != null) {
			Date date = DateUtil.getDate(dto.getStartTime());
			nyr = ymdh.format(date);
			nyrsfm = ymdhms.format(date);
		}
		if (dto.getLsbc() != null && !dto.getLsbc().equals("")) {
			targetPath += dto.getLsbc() + nyr;
		} else {
			if (dto.getRid() != null) {
				targetPath += dto.getRid();
			}
			if (dto.getChannel() != null) {
				targetPath += dto.getChannel();
			}
			targetPath += nyrsfm;
//			if (dto.getCollection() != null) {
//				targetPath += dto.getCollection();
//			}
		}
		dto.setJsonMessage(jsonMessage);
		dto.setVoideoMessages(videoJson);
		dto.setTargetPath(targetPath + File.separator);
		log.info("整合后参数为:" + JsonConverter.format(dto));
		return dto;
	}

	/**
	 * 新闻json
	 * 
	 * @return
	 */
	private String bulidNewsJsonMessage(TencentDto dto) {
		String result = "";
		Map<String, String> ma = new HashMap<>();
		ma.put("staff", dto.getStaff());
		ma.put("organization", dto.getOrganization());
		ma.put("ctype", dto.getCtype());
		ma.put("publish_date", dto.getPublish_date());
		ma.put("title", dto.getTitle());
		ma.put("columnname", dto.getColumnname());
		result = JsonConverter.format(ma);
		return result;
	}
	/**
	 * 新闻视频json
	 */
	private Object bulidNewsViodeJson(TencentDto dto){
		Map<String, Object> ma = new HashMap<>();
		ma.put("staff", dto.getStaff());
		ma.put("organization", dto.getOrganization());
		ma.put("ctype", dto.getCtype());
		ma.put("coverid", dto.getCoverid());
		ma.put("clip", dto.getClip());
		ma.put("desc", dto.getDesc());
		ma.put("playtime", dto.getPlaytime());
		ma.put("title", dto.getTitle());
		ma.put("tags", dto.getTags());
		ma.put("columnname", dto.getColumnname());
		ma.put("full", dto.getFull());
		ma.put("coverinfos", getPoster(dto));
		return ma;
	}
	/**
	 * 综艺json
	 */
	private String bulidVarietyJsonMessage(TencentDto dto) {
		String result = "";
		Map<String, String> ma = new HashMap<>();
		ma.put("staff", dto.getStaff());
		ma.put("organization", dto.getOrganization());
		ma.put("ctype", dto.getCtype());
		ma.put("publish_date", dto.getPublish_date());
		ma.put("title", dto.getTitle());
		ma.put("subject", dto.getSubject());
		ma.put("columnname", dto.getColumnname());
		result = JsonConverter.format(ma);
		return result;
	}
	
	/**
	 *  综艺视频json
	 */
	private Object bulidVarietyViodeJson(TencentDto dto){
		Map<String, Object> ma = new HashMap<>();
		ma.put("staff", dto.getStaff());
		ma.put("organization", dto.getOrganization());
		ma.put("ctype", dto.getCtype());
		ma.put("coverid", dto.getCoverid());
		ma.put("clip", dto.getClip());
		ma.put("desc", dto.getDesc());
		ma.put("playtime", dto.getPlaytime());
		ma.put("title", dto.getTitle());
		ma.put("tags", dto.getTags());
		ma.put("columnname", dto.getColumnname());
		ma.put("full", dto.getFull());
		ma.put("coverinfos", getPoster(dto));
		return ma;
	}

	/**
	 * 其他.
	 * 
	 */
	private String bulidOthersJsonMessage(TencentDto dto) {
		String result = "";
		Map<String, String> ma = new HashMap<>();
		ma.put("staff", dto.getStaff());
		ma.put("organization", dto.getOrganization());
		ma.put("ctype", dto.getCtype());
		ma.put("publish_date", dto.getPublish_date());
		ma.put("title", dto.getTitle());
		ma.put("second_title", dto.getSecond_title());
		ma.put("columnname", dto.getColumnname());
		result = JsonConverter.format(ma);
		return result;
	}
	/**
	 * 其他视频信息.
	 * @param dto
	 * @return
	 */
	private Object bulidOthersViodeJson(TencentDto dto){
		Map<String, Object> ma = new HashMap<>();
		ma.put("staff", dto.getStaff());
		ma.put("organization", dto.getOrganization());
		ma.put("ctype", dto.getCtype());
		ma.put("coverid", dto.getCoverid());
		ma.put("clip", dto.getClip());
		ma.put("desc", dto.getDesc());
		ma.put("playtime", dto.getPlaytime());
		ma.put("title", dto.getTitle());
		ma.put("second_title", dto.getSecond_title());
		ma.put("tags", dto.getTags());
		ma.put("columnname", dto.getColumnname());
		ma.put("full", dto.getFull());
		Map<String,Object>posters = getPoster(dto);
		posters.put("pos", -1);
		ma.put("coverinfos", posters);
		return ma;
	}
	/**
	 * 获取图片信息.
	 */
	public static Map<String,Object> getPoster(TencentDto dto) {
		// 拼接poster
		Map<String,Object> result = new HashMap<>();
		List<Map<String,Object>>coverimages = new ArrayList<>();
		if (dto.getPosters() != null) {
			for (PublishImageDto pid : dto.getPosters()) {
				Map<String, Object> p = new HashMap<>();
				p.put("ptype", 1);
				String fileName = "";
				String md5 = "";
				if (new File(pid.getSourceUrl()).exists()) {
					fileName = new File(pid.getSourceUrl()).getName();
					md5 = checksum_md5file(pid.getSourceUrl());
				}
				p.put("image", fileName);
				p.put("md5", md5);
				coverimages.add(p);
			}
		}
		result.put("coverimages", coverimages);
		return result;

	}
	public static String checksum_md5file(String path) {
		String bi = null;
		try {
			byte[] buffer = new byte[8192];
			int len = 0;
			MessageDigest md = MessageDigest.getInstance("MD5");
			File f = new File(path);
			FileInputStream fis = new FileInputStream(f);
			while ((len = fis.read(buffer)) != -1) {
				md.update(buffer, 0, len);
			}
			fis.close();
			byte[] b = md.digest();
			bi = encodeHex(b);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bi;
	}
	private static String encodeHex(byte[] data) {
		if (data == null) {
			return null;
		}
		  StringBuffer sb = new StringBuffer();  
          int length = data.length;
          for (int i = 0; i < length; i++) {  
              String hex = Integer.toHexString(data[i]&0xFF);  
              if (hex.length() == 1) {  
                  hex = '0' + hex;  
              }  
              sb.append(hex); 
          }  
          return sb.toString();  
	}
	/**
	 * 配置返回参数.
	 * 
	 * @param r
	 * @param status
	 * @param message
	 * @return
	 */
	public byte[] resopnse(ApiResponse r, Integer status, String message,
			HashMap<String, Object> dataReturn) {
		r = new ApiResponse();
		r.setStatusCode(status);
		r.setMessage(message);
		r.setData(dataReturn);
		String returnStr = JsonConverter.format(r);
		return returnStr.getBytes();
	}

	/**
	 * 生成返回参数.
	 * 
	 * @throws Exception
	 * 
	 */
	@SuppressWarnings("static-access")
	public Map<String, Object> returnExtraData(TencentDto dtos)
			throws Exception {
		String host = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
		String port = PropertiesUtil.getPropertiesUtil().get("gearman.port");
		List<CmsMovieDto> medias = new ArrayList<>();
		// 视频
		if (dtos.getMedias() != null) {
			for (MediaDto dto : dtos.getMedias()) {
				Map<String, Object> media = MediaInfoUtil.getMediaInfo(host,
						port, dto.getSourceUrl());
				CmsMovieDto cmsMovieDto = getCmsMovieDto(media);
				if (cmsMovieDto != null) {
					cmsMovieDto.setId(dto.getId());
				}
				medias.add(cmsMovieDto);
			}
		}
		HashMap<String, Object> extraData = new HashMap<String, Object>();
		extraData.put("medias", medias);
		extraData.put("mediaAssetId", dtos.getMediaAssetId());
		return extraData;
	}

	public CmsMovieDto getCmsMovieDto(Map<String, Object> p) {
		CmsMovieDto cms = new CmsMovieDto();
		String height = String.valueOf(p.get("height"));
		String width = String.valueOf(p.get("width"));
		Double bitrate = Double.valueOf(String.valueOf(p.get("bitrate")));
		Integer size = Integer.valueOf(String.valueOf(p.get("size")));
		cms.setWidth(width);
		cms.setHeight(height);
		cms.setSize(size);
		cms.setBitrate(bitrate);
		return cms;

	}
}
