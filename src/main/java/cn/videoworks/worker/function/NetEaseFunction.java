package cn.videoworks.worker.function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.client.MD5GearmanClient;
import cn.videoworks.worker.common.ParameterMap;
import cn.videoworks.worker.constant.ResponseDictionary;
import cn.videoworks.worker.dto.CommonExtraDataDto;
import cn.videoworks.worker.dto.CommonExtraMovieResponseDto;
import cn.videoworks.worker.dto.CommonExtraPosterResponseDto;
import cn.videoworks.worker.dto.CommonMediaDataDto;
import cn.videoworks.worker.dto.CommonPosterDto;
import cn.videoworks.worker.dto.CommonWorkerDto;
import cn.videoworks.worker.dto.NetEaseCategoryObj;
import cn.videoworks.worker.dto.NetEaseCategoryResult;
import cn.videoworks.worker.dto.NetEaseVideoReqeustDto;
import cn.videoworks.worker.exception.AreaCodeException;
import cn.videoworks.worker.exception.MediaInfoException;
import cn.videoworks.worker.exception.MediaNotExistsException;
import cn.videoworks.worker.exception.PosterNotExistException;
import cn.videoworks.worker.exception.SensitiveWordException;
import cn.videoworks.worker.util.ApiResponse;
import cn.videoworks.worker.util.DateUtil;
import cn.videoworks.worker.util.FileUtil;
import cn.videoworks.worker.util.Md5Util;
import cn.videoworks.worker.util.PropertiesUtil;
import cn.videoworks.worker.util.ResponseStatusCode;
import cn.videoworks.worker.util.WxMappingJackson2HttpMessageConverter;
/**
 * 网易
 * @author whl
 * worker 收到的任务参数格式：
 * {
	"publishId": "0ba2eaf9533d3e13824abcf88c38b302",
	"customerName": "capitalonline",
	"priority": "NORMAL_PRIORITY",
	"dataTransferStatus": 0,
	"extraData": {
		"broadCastTime": "2018-12-27 16:12:07",
		"medias": [{
			"id": "5c9d887e1aaa3364b195bc775b197856",
			"sourceUrl": "/mnt/mccdata/CCTV4/BJ/capitalonline/video_110.ts",
			"targetUrl": "",
			"duration": 1549240
		}],
		"assetId": "27",
		"channel": "CCTV4",
		"column": "国家记忆s",
		"source": "videoworks",
		"title": "台海纪事 共谋发展",
		"type": 1,
		"mediaAssetId": "fa942ee62e353615a556815e0356cbc5"
	}
}


返回结果：
	{
	"statusCode": 100000,
	"message": "首都在线worker成功------------------",
	"data": {
		"extraData": {
			"medias": [{
			    "id":"5c9d887e1aaa3364b195bc775b197856",
				"bitrate": 0.0,
				"width": "720",
				"height": "576",
				"size": 256676024
			}],
			"mediaAssetId": "fa942ee62e353615a556815e0356cbc5"
		},
		"dataTransferStatus": 0,
		"publishId": "0ba2eaf9533d3e13824abcf88c38b302"
	}
	
 *
 */
public class NetEaseFunction implements GearmanFunction {
	private static final Logger log = LoggerFactory.getLogger(NetEaseFunction.class);
	
	@Override
	public byte[] work(String arg0, byte[] data, GearmanFunctionCallback callback) throws Exception {
		ApiResponse returnResponse = new ApiResponse();
		
		String workerJson = new String(data, "UTF-8");
		
		log.info("网易的worker收到的发布参数："+workerJson);
		
		//组织返回结构
		String publishId = null;
		Integer dataTransferStatus =null;
		CommonWorkerDto commonWorkerDto = JsonConverter.parse(workerJson, CommonWorkerDto.class);
		if(commonWorkerDto!=null) {
			 CommonExtraDataDto extraData = commonWorkerDto.getExtraData();
			 publishId = commonWorkerDto.getPublishId();
			 dataTransferStatus = commonWorkerDto.getDataTransferStatus();
			 List<CommonPosterDto> posters = extraData.getPosters();
			 List<CommonMediaDataDto> medias = extraData.getMedias();
			 try {
				//拷贝文件本地磁盘
				 String video_local_disk_path= "";
				 String poster_local_disk_path ="";
				 
				 Map<String, String> pathMap = copyPostersAndMedias2LocalDisk(posters,medias,extraData);
				 if(pathMap!=null) {
					 video_local_disk_path= pathMap.get("videoUrl");
					 poster_local_disk_path = pathMap.get("posterUrl");
					 
					 //上报到网易的video 接口
					 log.info("网易上报的海报地址="+poster_local_disk_path);
					 log.info("网易上报的视频地址="+video_local_disk_path);
					 reportMediasToNetEase(video_local_disk_path,poster_local_disk_path,extraData);
				 }
				 
				 
				 HashMap<String, Object> dataMap = new HashMap<String,Object>();
				 dataMap.put("publishId", publishId);
				 dataMap.put("dataTransferStatus", dataTransferStatus);
				 HashMap<String,Object> returnExtraData= buildExtraData(posters,medias);
				 dataMap.put("extraData", returnExtraData);
				 returnResponse = buildResponse(ResponseDictionary.SUCCESS, "网易worker注入成功", dataMap, returnResponse);
				 
			} catch (Exception e) {
				e.printStackTrace();
				HashMap<String, Object> dataMap = new HashMap<String,Object>();
				dataMap.put("publishId", publishId);
				dataMap.put("dataTransferStatus", dataTransferStatus);
				returnResponse = buildResponse(ResponseDictionary.SERVEREXCEPTION, "网易worker注入失败:【"+e.getMessage()+"】", dataMap, returnResponse);
			}

		}else {
			returnResponse = buildResponse(ResponseDictionary.SERVEREXCEPTION, "网易worker解析任务参数失败", null, returnResponse);
		}
		String callbackStr = JsonConverter.format(returnResponse);
		log.info("网易worker返回给bos的数据="+callbackStr);
		return callbackStr.getBytes();
	}
	
	
	public ApiResponse buildResponse(Integer statusCode, String message, Map<String,Object> data, ApiResponse returnApiResponse) {
		returnApiResponse.setStatusCode(statusCode);
		returnApiResponse.setMessage(message);
		returnApiResponse.setData(data);
		return returnApiResponse;
	}
	/**
	 * 根据区域的名称获取区域码
	 * @param areaName
	 * @return
	 * @throws AreaCodeException 
	 */
	public  String   getAreaCode(String areaName) throws AreaCodeException {
		String areaCode = null;
		if(StringUtils.isNotBlank(areaName)) {
			areaName = areaName.trim();
			String areaCodeJson = getAreaCodeJson();
			Map areaCodeMap = JsonConverter.parse(areaCodeJson, Map.class);
			if(areaCodeMap!=null) {
				areaCode = String.valueOf(areaCodeMap.get(areaName));
			}
		}
		return areaCode;
	}
	
	/**
	 * 
	 * @return
	 * @throws AreaCodeException
	 */
	@SuppressWarnings("resource")
	public String getAreaCodeJson() throws AreaCodeException {
		String areaCodeJson = "";
		 try {
			String areaCode_filePath = PropertiesUtil.getPropertiesUtil().get("netease_areacode_json_file_path");
			File file = new File(areaCode_filePath);
			FileReader fileReader = new FileReader(file);
			BufferedReader bReader = new BufferedReader(fileReader);
			StringBuffer sb = new StringBuffer();
			String s = "";
			while((s=bReader.readLine())!=null) {
				sb.append(s+"\n");
			}
			bReader.close();
			areaCodeJson = sb.toString();
		} catch (Exception e) {
			throw new AreaCodeException("解析areaCode文件报错："+e.getMessage());
		}
		return areaCodeJson;
	}
	
	/**
	 * 将视频文件还有海报拷贝到本地磁盘
	 * @param posters
	 * @param medias
	 * @throws Exception 
	 */
	private Map<String,String> copyPostersAndMedias2LocalDisk(List<CommonPosterDto> posters, List<CommonMediaDataDto> medias,CommonExtraDataDto extraData) throws Exception {
		String broadCastTime = extraData.getBroadCastTime();
		Date date = DateUtil.getDate(broadCastTime);
		String year = date.getYear() + 1900 + "";
		int month = date.getMonth() + 1;
		String monthStr = month + "";
		if (month < 10) {
			monthStr = "0" + month;
		}
		int day = date.getDate();
		String dayStr = day + "";
		if (day < 10) {
			dayStr = "0" + day;
		}
		HashMap<String, String> localPathMap = new HashMap<String,String>();
		
		String localDateDir=year+monthStr+dayStr;
		String localDiskPath= PropertiesUtil.getPropertiesUtil().get("netease_video_local_address");
		for (CommonMediaDataDto media : medias) {
			String realCopyMedias = realCopyMedias(media, localDiskPath, localDateDir);
			localPathMap.put("videoUrl", realCopyMedias);
		}
		
		for (CommonPosterDto poster : posters) {
			String realCopyPosters = realCopyPosters(poster,localDiskPath, localDateDir);
			localPathMap.put("posterUrl", realCopyPosters);
		}
		
		return localPathMap;
		
	}
	/**
	 * 实际的拷贝路径
	 * @param media
	 * @param localDateDir
	 * @throws Exception 
	 */
	private String realCopyMedias(CommonMediaDataDto media,String localDiskPath,  String localDateDir) throws Exception {
		String videoHttpUrl = "";
		String lastLocalPath ="";
		String sourceUrl = media.getSourceUrl();
		String targetUrl = media.getTargetUrl();
		boolean exists = FileUtil.exists(sourceUrl);
		if(exists) {
			File file = new File(sourceUrl);
			String fileName = file.getName();
			 lastLocalPath= localDiskPath+File.separator+localDateDir;
			byte[] bytes = FileUtil.getByte(file);
			FileUtil.buildFile(bytes, lastLocalPath, fileName);
		
			String videoIp_port = PropertiesUtil.getPropertiesUtil().get("netease_video_http_url");
//			lastLocalPath = lastLocalPath+File.separator+fileName;
			videoHttpUrl = videoIp_port+"/"+localDiskPath+"/"+localDateDir+"/"+fileName;
		}else {
			throw new MediaNotExistsException(sourceUrl);
		}
		log.info("网易的视频的apache地址="+videoHttpUrl);
		return videoHttpUrl;
	}
	
	private String realCopyPosters(CommonPosterDto poster,String localDiskPath, String localDateDir) throws Exception {
		String posterHttpUrl = "";
		
		String lastLocalPath ="";
		String sourceUrl = poster.getSourceUrl();
		String targetUrl = poster.getTargetUrl();
		boolean exists = FileUtil.exists(sourceUrl);
		if(exists) {
			File file = new File(sourceUrl);
			String fileName = file.getName();
			 lastLocalPath= localDiskPath+File.separator+localDateDir;
			byte[] bytes = FileUtil.getByte(file);
			FileUtil.buildFile(bytes, lastLocalPath, fileName);
			String picIp_port = PropertiesUtil.getPropertiesUtil().get("netease_video_http_url");
//			lastLocalPath = lastLocalPath+File.separator+fileName;
			
			posterHttpUrl = picIp_port+"/"+localDiskPath+"/"+localDateDir+"/"+fileName;
		}else {
			throw new PosterNotExistException(sourceUrl);
		}
		log.info("网易的海报的apache地址="+posterHttpUrl);
		return posterHttpUrl;
	}
	/**
	 * 判断标题中是否包含领导的敏感词
	 * @param title
	 * @return
	 */
	public boolean containSenstiveWord(String title) {
		boolean  containsFlag = false;
		String [] sensitiveWords = new String[] {"习近平", "李克强", "栗战书", "汪洋", "王沪宁", "赵乐际", "韩正", "王毅","魏凤和", "何立峰", "陈宝生", "王志刚", "苗圩", "巴特尔", "赵克志", "陈文清", "杨晓渡","黄树贤", "傅政华", "刘昆", "张纪南", "陆昊", "李干杰", "王蒙徽", "李小鹏", "鄂竟平","韩长赋", "钟山", "雒树刚", "马晓伟", "易纲", "胡泽君", "王玉普", "孙绍骋", "蔡奇","陈吉宁"};
		for (String word : sensitiveWords) {
			if(title.contains(word)) {
				containsFlag  = true;
				break;
			}
		}
		return containsFlag;
	}
	/**
	 * 将视频文件上报到网易
	 * 组织post请求的body,
	 * 视频的url是本地拷贝后的path
	 * 海报的url是拷贝到本地目录的path
	 * 网易上报video的返回结果是{"statusCode":"OK","headers":{"Date":["Wed, 02 Jan 2019 07:24:08 GMT"],"Server":["nginx"],"Content-Length":["142"],"Set-Cookie":["NTESwebSI\u003d36B2CD51E118A8BDD23635C43287D208.hzabj-subscribe-tomcat2.server.163.org-8083; Path\u003d/; HttpOnly"],"X-Via":["1.1 PSzjhzjfrr192:3 (Cdn Cache Server V2.0), 1.1 tedianxin24:1 (Cdn Cache Server V2.0)"],"Connection":["keep-alive"]},"body":{"code":110012,"msg":"\\u0041\\u0043\\u0043\\u0045\\u0053\\u0053\\u005f\\u0054\\u004f\\u004b\\u0045\\u004e\\u5df2\\u8fc7\\u671f"}}
	 * @param medias
	 * @throws Exception 
	 */
	@Autowired
	public  void reportMediasToNetEase(String videoPath, String posterPath, CommonExtraDataDto extraData) throws Exception {
		NetEaseVideoReqeustDto requestBody = new NetEaseVideoReqeustDto();
		String title = extraData.getTitle();
		String column = extraData.getProgramType1();  //其实column的别名
		boolean containSenstiveWord = containSenstiveWord(title);
		if(containSenstiveWord) {
			throw new SensitiveWordException(title);
		}
		if(title.length()<11) {
			title = column+":"+title;
		}
		
		List<String> tags = extraData.getTag();
		if(tags==null||tags.size()<3) {
			throw new Exception("参数错误：tag不能为空,最少三个标签");
		}
		String tag = "";
		if(tags.size()>5) {
			List<String> subList = tags.subList(0, 5);
			tag=StringUtils.join(subList, ",");
		}else {
			tag = StringUtils.join(tags, ",");  //标签值， 最小三个，最多5个
		}
		String pic_url ="";
		
		pic_url = posterPath;
		Integer category_id = null;
		String url = "";
		category_id= getCategoryId(column);
		url = videoPath;
		
		String area =extraData.getArea();  //发布传过来的地区名称
		String area_code = null;
		if(StringUtils.isNotBlank(area)) {
			try {
				area_code = getAreaCode(area);
			} catch (AreaCodeException e) {
				e.printStackTrace();
				throw e;
			}
		}
		
		String access_token = getAccessToken();
		
		//请求
		String net_ease_video_form_url= PropertiesUtil.getPropertiesUtil().get("netease_publish_video_form_api");
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
		//组织请求的头信息
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		String bodyValTemplate = "title="+URLEncoder.encode(title, "utf-8")
								+"&tag="+URLEncoder.encode(tag, "utf-8")
								+"&full_view=0"
								+"&pic_url="+URLEncoder.encode(pic_url,"utf-8")
								+"&category_id="+category_id
								+"&url="+URLEncoder.encode(url, "utf-8")
								+"&isoriginal=0"
								+"&access_token="+access_token
		;
		if(StringUtils.isNotBlank(area_code)) {
			bodyValTemplate = bodyValTemplate+"&area_code="+area_code;
		}
		log.info("发送到网易的form参数="+bodyValTemplate);
		HttpEntity httpEntity = new HttpEntity<>(bodyValTemplate, headers);
		ResponseEntity<Map> postForEntity = restTemplate.exchange(net_ease_video_form_url, HttpMethod.POST, httpEntity, Map.class);
		String format = JsonConverter.format(postForEntity);
		log.info("网易新闻的上报video的返回结果：【"+postForEntity+"】");
		
		
		Map body = postForEntity.getBody();
		Integer code = Integer.valueOf(String.valueOf(body.get("code")));
		String msg  = String.valueOf(body.get("msg"));
		if(code.intValue()!=1) {
			throw new Exception("netEaseUpload sendForm except:【"+msg+"】");
		}
		
	}
	
	/**
	 * 获取accessToken------------------------TO DO
	 * @return
	 */
	public String getAccessToken() {
		String[] tokens = new String[] {"c25875e1a015102a5ec0e08dd8b89990","3e382e10166b83fa91545ad6585ba64b","a13826e4e4387783a29d3a19fba20f3f","97a8980241cb9ff815e63c88a128e0ef","6dea402de1bbb3170ed166d353ab9586","d1ebda5c13421800133f85bee5d85718","50e1cd6ba2ec9f29512abbf02cca0299"
	            ,"ee4c369f9d710e2f1f915c4f68e074e7","c3a8be94a60d6b42fd9a072f49a52228","87885126d64b417d1cf025f2635a3be6","ab99a5df0b2da4089f25b8b9125fbf8c","222a3fe633658ca807eaa8580aed9536","7d52b3444812c90e565d50720a3293bb","dfcb4cfa605f0a351be5ff7aae9af30a","2ed16ce8adb6e6502e7e05fd897bf382"
	            ,"6747bc99d0c4a8f634025145f930aaf2","cf7948c0eb839894245e3a9df690a94a","05f7e58ab00197e01c249c86aa81ea19","affab58b47f46f3b754e58587d050768","dbc59e19550f3a96a4294e7f6bfc0dbd","0351ed9cc5e2a832fd7989587ddb5fbb","fe752ef7473950bda69bc48d66e085cd","c6a7da95624e3cebdae96ecc2b5be1c2"
	            ,"4eb8926c54dd31ad837f13fe4a423163","db7a100432b7b11b61989ec12f396ed8","0736aa1fd027a26877a551c4c36529f0","769b0af8da4dc6469c00a88dc42c75f3","1a562f192ae9583932705399ee78a665","31727692cad712cd0a61875479d39702","67c1dea047c6dbf96ad80c9dbd566cd4","31681f79be0c63957c9230df16022ef0"
	            ,"53dd447ffa2c45c4d7d495549be3cdf1","4582248fc6de990d872e377532782a5d","2e542beb08ff9d3e338052526e971064","d8aae104c7113e59f4d70780ebcb06e7"};
		Random random = new Random();
		int nextInt = random.nextInt(35);
		return tokens[nextInt];
//		return "a0c610e5262dd4c0f55aba76afc65f13";
	}
	
	/**
	 * 根据传递的栏目的名称去获取网易的categoryId,
	 * 获取栏目要pid !=0 的并且cname==传过来的栏目名称，返回的值匹配的栏目的cid -------------TODO
	 * @param categoryName
	 * @return
	 * @throws Exception 
	 */
	public Integer getCategoryId(String categoryName) throws Exception {
		Integer category_id =null;
		if(StringUtils.isNotBlank(categoryName)) {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
			String netease_category_url = PropertiesUtil.getPropertiesUtil().get("netease_publish_video_category_api");
			ResponseEntity<NetEaseCategoryResult> reponse = restTemplate.getForEntity(netease_category_url, NetEaseCategoryResult.class);
			NetEaseCategoryResult result = reponse.getBody();
			List<NetEaseCategoryObj> categorylist = result.getCategorylist();
			for (NetEaseCategoryObj netEaseCategoryObj : categorylist) {
				Integer pid = netEaseCategoryObj.getPid();
				Integer cid = netEaseCategoryObj.getCid();
				String cname = netEaseCategoryObj.getCname();
				
				if(pid!=null) {
					if(categoryName.trim().equals(cname)) {
						category_id = cid;
						break;
					}
				}
			}
		}
		
		return category_id;
	}
	
	/**
	 * 请求海报和媒体的MD5和mediaInfo,组织worker的返回数据----TO DO
	 * @param posters
	 * @param medias
	 * @return
	 * @throws Exception 
	 */
	public HashMap<String,Object> buildExtraData(List<CommonPosterDto> posters, List<CommonMediaDataDto> medias) throws Exception{
		HashMap<String, Object> extraData = new HashMap<>();
		 String host = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
		 String port = PropertiesUtil.getPropertiesUtil().get("gearman.port");
		 ArrayList<CommonExtraMovieResponseDto> mediaDtos = new ArrayList<CommonExtraMovieResponseDto>();
		//组织medias
		for (CommonMediaDataDto commonMediaDataDto : medias) {
			String sourceUrl = commonMediaDataDto.getSourceUrl();
			Map<String, Object> mediaInfoMap = getMediaInfo(host, port, sourceUrl);
			String height = String.valueOf(mediaInfoMap.get("height"));
			String width = String.valueOf(mediaInfoMap.get("width"));
			Double bitrate = Double.valueOf(String.valueOf(mediaInfoMap.get("bitrate")));
			Integer size = Integer.valueOf(String.valueOf(mediaInfoMap.get("size")));
			CommonExtraMovieResponseDto extraMovie = new CommonExtraMovieResponseDto();
			extraMovie.setId(commonMediaDataDto.getId());
			extraMovie.setBitrate(bitrate);
			extraMovie.setWidth(width);
			extraMovie.setHeight(height);
			extraMovie.setSize(size);
			mediaDtos.add(extraMovie);
		}
		
		ArrayList<CommonExtraPosterResponseDto> posterDtos = new ArrayList<CommonExtraPosterResponseDto>();
		for(CommonPosterDto poster:posters) {
			CommonExtraPosterResponseDto posterDto = new   CommonExtraPosterResponseDto();
			posterDto.setId(poster.getId());
			posterDto.setWidth(poster.getWidth()+"");
			posterDto.setHeight(poster.getHeight()+"");
			posterDtos.add(posterDto);
		}
		
		extraData.put("medias", mediaDtos);
		extraData.put("posters",posterDtos);
		return extraData;
	}
	
	
	public Map<String, Object> getMediaInfo(String host, String port, String url) throws MediaInfoException {
		MD5GearmanClient cdnGearmanClient = MD5GearmanClient.getCDNGearmanClient(host, Integer.valueOf(port), "md5sum");
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("filepath", url);
		String format = JsonConverter.format(hashMap);
		Map<String, Object> submitJob = cdnGearmanClient.submitMediaInfoJob(format, "mediainfo");
		Map<String, Object> dataMap = new HashMap<>();
		if (submitJob != null) {
			Integer code = Integer.valueOf(String.valueOf(submitJob.get("statusCode")));
			if (code != ResponseStatusCode.OK) {
				throw new MediaInfoException("获取mediaInfo异常-" + JsonConverter.format(submitJob));
			} else {

				dataMap = (Map<String, Object>) submitJob.get("data");
			}
		}
		return dataMap;
//		 Map<String, Object> dataMap = new HashMap<String, Object>();
//		 dataMap.put("height", "200");
//		 dataMap.put("width", "200");
//		 dataMap.put("bitrate", "2");
//		 dataMap.put("size", "200");
//		 return dataMap;
	}
	
	
	public String getmd5Sum(String url) throws Exception {
		File file = new File(url);
		String check_sum = "";
		String host;

		host = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
		String port = PropertiesUtil.getPropertiesUtil().get("gearman.port");
		MD5GearmanClient cdnGearmanClient = MD5GearmanClient.getCDNGearmanClient(host, Integer.valueOf(port), "md5sum");
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("filepath", url);
		String format = JsonConverter.format(hashMap);
		Map<String, Object> submitJob = cdnGearmanClient.submitMd5Job(format, "md5sum");
		Integer statusCode = Integer.valueOf(String.valueOf(submitJob.get("statusCode")));
		if (statusCode != ResponseStatusCode.OK) {
			byte[] source = FileUtil.getByte(file);
			check_sum = Md5Util.getMD5(source);
		} else {
			check_sum = String.valueOf(submitJob.get("check_sum"));
		}
		return check_sum;
//		 return "34567679899md5";

	}
	
	public static void main(String[] args) throws Exception {
		ParameterMap.getParameterMap("D:\\bos-worker.properties"); //初始化配置文件
		
		NetEaseFunction netEaseFunction = new NetEaseFunction();
		
		//测试获取的栏目的id
//		Integer categoryId = netEaseFunction.getCategoryId("国内");
//		System.out.println(categoryId);
		
		//测试获取token
		String accessToken = netEaseFunction.getAccessToken();
		System.out.println(accessToken);
		
		//测试获取区域码
//		String areaCode = netEaseFunction.getAreaCode("");
//		System.out.println(areaCode);
//		boolean notBlank = StringUtils.isNotBlank(null);
		
		//测试上报video
		
		CommonExtraDataDto commonExtraDataDto = new CommonExtraDataDto();
		commonExtraDataDto.setBroadCastTime("2019-01-02 15:01:24");
		commonExtraDataDto.setAssetId("29");
		commonExtraDataDto.setChannel("CCTV4");
		commonExtraDataDto.setColumn("国内");
		commonExtraDataDto.setSource("videoworks");
		commonExtraDataDto.setTitle("台海纪事 共谋发展");
		commonExtraDataDto.setType(1);
		commonExtraDataDto.setMediaAssetId("fa942ee62e353615a556815e0356cbc5");
		
		ArrayList<CommonMediaDataDto> medias = new ArrayList<CommonMediaDataDto>();
		CommonMediaDataDto commonMediaDataDto = new CommonMediaDataDto();
		commonMediaDataDto.setId("5c9d887e1aaa3364b195bc775b197856");
		commonMediaDataDto.setSourceUrl("D:\\video_96.ts");
		medias.add(commonMediaDataDto);
		commonExtraDataDto.setMedias(medias);
		
		
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("国际1");
		tags.add("国内2");
		tags.add("国内3");
		tags.add("国内4");
		tags.add("国内5");
		tags.add("国内6");
		commonExtraDataDto.setTag(tags);
		netEaseFunction.reportMediasToNetEase("http://175.25.49.202:82/mnt/mccdata/JRTT/20190103/video_165.mp4", "http://175.25.49.202:82//mnt/mccdata/JRTT/20190103/video_165-1.jpg", commonExtraDataDto);
//		String tag ="";
//		if(tags.size()>5) {
//			List<String> subList = tags.subList(0, 5);
//			tag=StringUtils.join(subList, ",");
//		}else {
//			tag = StringUtils.join(tags, ",");  //标签值， 最小三个，最多5个
//		}
//		System.out.println(tag);
		
		
		
		
		
	}
}
