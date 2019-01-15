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
import cn.videoworks.worker.dto.YouKuV3Dto;
import cn.videoworks.worker.util.ApiResponse;
import cn.videoworks.worker.util.DateUtil;
import cn.videoworks.worker.util.MediaInfoUtil;
import cn.videoworks.worker.util.PropertiesUtil;
import cn.videoworks.worker.util.YouKuV3Util;

public class YouKuV3Function   implements GearmanFunction{
	private static final Logger log = LoggerFactory.getLogger(YouKuV3Function.class);
	private static String slice_task_id = null;// ip
	@Override
	public byte[] work(String arg0, byte[] data, GearmanFunctionCallback arg2)
			throws Exception {
		log.info("YouKuV3 funcation 收到的data=" + new String(data));
		HashMap<String, Object> dataReturn = new HashMap<String, Object>();
		Map<String, Object> extra =  new HashMap<String, Object>();
		ApiResponse response = null;
		try {
			response = null;
			String dataJson = new String(data);
			DataDto dto = JsonConverter.parse(dataJson, DataDto.class);
			if (dto == null) {
				log.info("参数为空!");
				response = new ApiResponse();
				response.setStatusCode(ResponseDictionary.ERROR);
				response.setMessage("参数转换失败");
				String returnStr = JsonConverter.format(response);
				return returnStr.getBytes();
			}
			YouKuV3Dto wokrerDto = null;
			dataReturn.put("publishId", dto.getPublishId());
			dataReturn.put("dataTransferStatus", dto.getDataTransferStatus());
			if(dto.getExtraData()!= null){
				// 生成上传参数
				log.info("开始转成media参数!");
				String json = JsonConverter.format(dto.getExtraData());
				wokrerDto = JsonConverter.parse(json,YouKuV3Dto.class);
				log.info("设置特殊media参数!");
				if (wokrerDto != null) {
					wokrerDto = bulidParams(wokrerDto);
				}
			}
			log.info("生成YouKuV3Dto:"+JsonConverter.format(wokrerDto));
			String fileSize = "";
			if (wokrerDto != null) {
				// 生成部分返回参数
				log.info("开始生成部分返回参数");
				extra = returnExtraData(wokrerDto);
				log.info("生成部分返回参数:"+JsonConverter.format(extra));
				dataReturn.put("extraData", extra);
				// 文件大小
				String filePath = wokrerDto.getFilePath();
				File file = new File(filePath);
				FileInputStream fis = new FileInputStream(file);
				Integer size = fis.available();
				fileSize = String.valueOf(size);
				// 初始化参数
				log.info("生成initParam参数");
				YouKuV3Util.initParam();
				String accessToken = YouKuV3Util.refreshAccessToken();
				// if(){}
				String createTaskResult = YouKuV3Util.createTask(wokrerDto,fileSize);
				if(createTaskResult==null){
					response = new ApiResponse();
					response.setStatusCode(ResponseDictionary.ERROR);
					response.setMessage("YouKuV3数据上传失败,失败原因:创建任务失败!");
					response.setStatusCode(ResponseDictionary.ERROR);
					String returnStr = JsonConverter.format(response);
					return returnStr.getBytes();
				}
				YouKuV3Util.saveTask(wokrerDto);
				String re = YouKuV3Util.create_file(wokrerDto, fileSize);
				if (re == null || re.equals("{}")) {
					response = new ApiResponse();
					response.setStatusCode(500);
					response.setMessage("YouKuV3数据上传失败,失败原因:创建文件失败!");
					String returnStr = JsonConverter.format(response);
					return returnStr.getBytes();
				}
				String result = YouKuV3Util.new_slice();
				Map<String, Object> map = new HashMap<>();
				map = JsonConverter.asMap(result, String.class, Object.class);
				slice_task_id = map.get("slice_task_id").toString();
				if (slice_task_id == null) {
					response = new ApiResponse();
					response.setStatusCode(500);
					response.setMessage("YouKuV3数据上传失败");
					String returnStr = JsonConverter.format(response);
					return returnStr.getBytes();
				}
				while (slice_task_id != null && !slice_task_id.equals("0")) {
					slice_task_id = YouKuV3Util.uploadSlice(wokrerDto);
				}
				Map<String, Object> checkResult = YouKuV3Util.check();
				if (checkResult != null) {
					if (checkResult.containsKey("status") && checkResult.get("checkResult").toString().equals("1")) {
						if (YouKuV3Util.commit(wokrerDto)) {
							response = new ApiResponse();
							response.setStatusCode(ResponseDictionary.SUCCESS);
							response.setMessage("YouKuV3数据上传成功");
						} else {
							response = new ApiResponse();
							response.setStatusCode(ResponseDictionary.ERROR);
							response.setMessage("YouKuV3数据上传失败");
						}

					}
				} else {
					response = new ApiResponse();
					response.setStatusCode(ResponseDictionary.ERROR);
					response.setMessage("YouKuV3数据上传失败");
				}
			} else {
				response = new ApiResponse();
				response.setStatusCode(ResponseDictionary.ERROR);
				response.setMessage("YouKuV3数据上传失败");
			}

		} catch (Exception e) {
			response = new ApiResponse();
			response.setStatusCode(ResponseDictionary.ERROR);
			response.setMessage("YouKuV3数据上传失败");
			String returnStr = JsonConverter.format(response);
			return returnStr.getBytes();
		}
		String returnStr = JsonConverter.format(response);
		return returnStr.getBytes();
	}

	/**
	 * 生成设置参数.
	 * 
	 * @param dto
	 * @return
	 */
	private YouKuV3Dto bulidParams(YouKuV3Dto dto) {
		log.info("生成设置参数");
		if (dto == null) {
			return null;
		}
		if (dto.getMedias() != null) {
			for (int i = 0; i < dto.getMedias().size(); i++) {
				dto.setFilePath(dto.getMedias().get(i).getSourceUrl());
				dto.setShow_id(dto.getMedias().get(i).getId());// 节目id
				break;
			}
		}
		log.info("getMedias");
		// 标签
		if (dto.getTag() != null) {
			String tags = "";
			for (int i = 0; i < dto.getTag().size(); i++) {
				tags += dto.getTag().get(i);
				if (i != dto.getTag().size() - 1) {
					tags += ",";
				}
			}
			dto.setTags(tags);
		}
		// 节目分类
		String classification = dto.getClassification();
		if (classification != null) {
			if (classification.equals("电视剧")) {
				dto.setClassificationA_int("2");
				dto.setStage("10");
			} else {
				dto.setClassificationA_int("1");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
				dto.setStage(sdf.format(new Date()));
			}
		} else {
			dto.setClassificationA_int("1");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
			dto.setStage(sdf.format(new Date()));
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
		dto.setDeployDate(DateUtil.getNowTime());// 部署时间
		//内容主线
		if (dto.getContent_main_thread() != null) {
			if (dto.getContent_main_thread().equals("人物")) {
				dto.setContent_main_thread_int("18868");
			}
			if (dto.getContent_main_thread().equals("事件")) {
				dto.setContent_main_thread_int("18869");
			}
		}
		// 周边剪辑方式
		if(dto.getVideo_cut_mode()!=null){
			if (dto.getVideo_cut_mode().equals("完整卡段")) {
				dto.setVideo_cut_mode_int("18873");
			}
			if (dto.getVideo_cut_mode().equals("作品纯享")) {
				dto.setVideo_cut_mode_int("18874");
			}
		}
		// 节目关系
		if(dto.getShow_relation()!=null){
			if(dto.getShow_relation().equals("分集")){
				dto.setShow_relation_int("18891");
			}
			if(dto.getShow_relation().equals("节目")){
				dto.setShow_relation_int("18892");
			}
		}
		// 视频二级标签
		if(dto.getRemain_video_type()!=null){
			if(dto.getRemain_video_type().equals("资讯报道")){
				dto.setRemain_video_type_int("18836");
			}
			if(dto.getRemain_video_type().equals("预告")){
				dto.setRemain_video_type_int("18893");
			}
			if(dto.getRemain_video_type().equals("片花")){
				dto.setRemain_video_type_int("18838");
			}
		}
		// 二级标签
		if(dto.getVtype_mark()!=null){
			switch (dto.getVtype_mark()) {
			case "探班采访":
				dto.setVtype_mark_int("18840");
				break;
			case "首映式":
				dto.setVtype_mark_int("18841");
				break;
			case "见面会":
				dto.setVtype_mark_int("18842");
				break;
			case "发布会":
				dto.setVtype_mark_int("18843");
				break;
			case "娱乐八卦":
				dto.setVtype_mark_int("18844");
				break;
			case "明星ID":
				dto.setVtype_mark_int("18845");
				break;
			case "分集预告":
				dto.setVtype_mark_int("18916");
				break;
			case "节目预告":
				dto.setVtype_mark_int("18917");
				break;
			case "周预告":
				dto.setVtype_mark_int("18851");
				break;
			case "看点拆条":
				dto.setVtype_mark_int("18918");
				break;

			default:
				break;
			}
		}
		return dto;
	}
	/**
	 * 生成返回参数.
	 * @throws Exception 
	 * 
	 */
	public Map<String, Object> returnExtraData(YouKuV3Dto dto) throws Exception {
		String host = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
		String port = PropertiesUtil.getPropertiesUtil().get("gearman.port");
		log.info("getMediaInfo开始");
		Map<String, Object> media = MediaInfoUtil.getMediaInfo(host, port, dto.getFilePath());
		log.info("getMediaInfo结束");
		CmsMovieDto cmsMovieDto = getCmsMovieDto(media);
		if (cmsMovieDto != null) {
			cmsMovieDto.setId(dto.getMedias().get(0).getId());
		}
		log.info("cmsMovieDto结束");
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
