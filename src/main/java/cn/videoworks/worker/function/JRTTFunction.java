package cn.videoworks.worker.function;

import java.util.ArrayList;
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
import cn.videoworks.worker.dto.JRTTDto;
import cn.videoworks.worker.dto.MediaDto;
import cn.videoworks.worker.util.ApiResponse;
import cn.videoworks.worker.util.JRTTUtil;
import cn.videoworks.worker.util.MediaInfoUtil;
import cn.videoworks.worker.util.PropertiesUtil;

public class JRTTFunction implements GearmanFunction {
	private static final Logger log = LoggerFactory.getLogger(JRTTFunction.class);
	public byte[] work(String arg0, byte[] data, GearmanFunctionCallback arg2)throws Exception {
		log.info("今日头条 funcation 收到的data=" + new String(data));
		ApiResponse response = null;
		HashMap<String, Object> dataReturn = new HashMap<String, Object>();
		String dataJson = new String(data);
		DataDto dto = JsonConverter.parse(dataJson, DataDto.class);
		// 传输参数为空
		if (dto == null) {
			log.info("参数传输错误");
			return resopnse(response, ResponseDictionary.ERROR, "参数传输错误", null);
		}
		// 设置返回参数发布id
		dataReturn.put("publishId", dto.getPublishId());
		dataReturn.put("dataTransferStatus", dto.getDataTransferStatus());
		JRTTDto Jrrt = null;
		// getExtraData 为空
		if (dto.getExtraData() == null) {
			log.info("参数传输ExtraData错误");
			return resopnse(response, ResponseDictionary.ERROR, "参数传输ExtraData错误", dataReturn);
		}
		// 生成上传参数
		String json = JsonConverter.format(dto.getExtraData());
		Jrrt = JsonConverter.parse(json, JRTTDto.class);
		// 生成返回数据
		dataReturn.put("extraData", returnExtraData(Jrrt));
		// getMedias为空
		if (Jrrt.getMedias() == null || Jrrt.getMedias().size() == 0) {
			log.info("参数传输视频内容为空错误");
			return resopnse(response, ResponseDictionary.ERROR, "参数传输视频内容为空错误", dataReturn);
		}
		// 循环上传视频数据
		for (MediaDto d : Jrrt.getMedias()) {
			// 设置视频信息
			JRTTUtil.uniteMedia(d,Jrrt);
		}
		
		return resopnse(response, ResponseDictionary.SUCCESS, "今日头条数据上传成功", dataReturn);
	}

	/**
	 * 配置返回参数.
	 * 
	 * @param r
	 * @param status
	 * @param message
	 * @return
	 */
	public byte[] resopnse(ApiResponse r, Integer status, String message,HashMap<String, Object> dataReturn) {
		r = new ApiResponse();
		r.setStatusCode(status);
		r.setMessage(message);
		r.setData(dataReturn);
		String returnStr = JsonConverter.format(r);
		return returnStr.getBytes();
	}
	
	/**
	 * 生成返回参数.
	 * @throws Exception 
	 * 
	 */
	@SuppressWarnings("static-access")
	public Map<String, Object> returnExtraData(JRTTDto dtos) throws Exception {
		String host = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
		String port = PropertiesUtil.getPropertiesUtil().get("gearman.port");
		List<CmsMovieDto> medias = new ArrayList<>();
		// 视频
		if (dtos.getMedias() != null) {
			for (MediaDto dto : dtos.getMedias()) {
				Map<String, Object> media = MediaInfoUtil.getMediaInfo(host, port, dto.getSourceUrl());
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
	public CmsMovieDto getCmsMovieDto(Map<String,Object>p){
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
