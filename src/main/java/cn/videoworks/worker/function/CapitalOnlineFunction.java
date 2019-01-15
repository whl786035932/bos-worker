package cn.videoworks.worker.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.net.ftp.FTPClient;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.client.FTPUtilClient;
import cn.videoworks.worker.client.MD5GearmanClient;
import cn.videoworks.worker.constant.ResponseDictionary;
import cn.videoworks.worker.dto.AwsStorageDto;
import cn.videoworks.worker.dto.CapitalMediaDataDto;
import cn.videoworks.worker.dto.CapitalOnLineWorkerDto;
import cn.videoworks.worker.dto.CapitalOnlineDto;
import cn.videoworks.worker.dto.CapitalOnlineExtraDataDto;
import cn.videoworks.worker.dto.CmsImageDto;
import cn.videoworks.worker.dto.CmsMovieDto;
import cn.videoworks.worker.dto.CmsStorageDto;
import cn.videoworks.worker.dto.PublishStorageDto;
import cn.videoworks.worker.exception.MediaInfoException;
import cn.videoworks.worker.exception.PosterNotExistException;
import cn.videoworks.worker.util.ApiResponse;
import cn.videoworks.worker.util.DateUtil;
import cn.videoworks.worker.util.FileUtil;
import cn.videoworks.worker.util.Md5Util;
import cn.videoworks.worker.util.PropertiesUtil;
import cn.videoworks.worker.util.ResponseStatusCode;

/**
 * 注入到AWSS3存储
 * 
 * @author whl
 *
 */
public class CapitalOnlineFunction implements GearmanFunction {

	private static final Logger log = LoggerFactory.getLogger(CapitalOnlineFunction.class);

	public CapitalOnlineFunction() {

	}

	@SuppressWarnings({ "static-access", "deprecation" })
	@Override
	public byte[] work(String arg0, byte[] data, GearmanFunctionCallback callback) {
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		String dataJson = null;
		ApiResponse captialResonse = new ApiResponse();
		try {
			dataJson = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			captialResonse.setStatusCode(ResponseDictionary.SERVEREXCEPTION);
			captialResonse.setMessage("首都在线woker失败：" + e1.getMessage());
		}
		log.info("首都在线Work，收到参数：" + dataJson);
		CapitalOnLineWorkerDto workerDto = JsonConverter.parse(dataJson, CapitalOnLineWorkerDto.class);
		String publishId = workerDto.getPublishId();
		Integer dataTransferStatus = workerDto.getDataTransferStatus();
		CapitalOnlineExtraDataDto publishDto = workerDto.getExtraData();
		try {

			if (publishDto == null) {
				captialResonse.setStatusCode(ResponseDictionary.SERVEREXCEPTION);
				captialResonse.setMessage("首都在线worker解析数据失败");
			} else {
				captialResonse = capitalWork(publishId, dataTransferStatus, publishDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			HashMap<String, Object> dataReturn = new HashMap<String, Object>();
			dataReturn.put("publishId", publishId);
			dataReturn.put("dataTransferStatus", dataTransferStatus);
			captialResonse.setData(dataReturn);
			captialResonse.setStatusCode(ResponseDictionary.SERVEREXCEPTION);
			captialResonse.setMessage("首都在线woker失败：" + e.getMessage());

		}
		String capitalJsonStr = JsonConverter.format(captialResonse);
		log.info("返回的给bos的jsonStr=" + capitalJsonStr);
		return capitalJsonStr.getBytes();
	}

	/**
	 * 首都在线的worker实际工作
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public ApiResponse capitalWork(String publishId, Integer dataTransferStatus, CapitalOnlineExtraDataDto publishDto)
			throws Exception {
		ApiResponse apiResponse = new ApiResponse();
		List<CapitalMediaDataDto> medias = publishDto.getMedias();

		ArrayList<CmsMovieDto> mediaDtos = new ArrayList<CmsMovieDto>();
		String host = "";
		String port = "";
		try {

			for (CapitalMediaDataDto media : medias) {
				String url = media.getSourceUrl();
				// url ="/home/wanghl/test.mp4";
				// 获取md5
				String md5 = "";
				try {
					md5 = getmd5Sum(url);
				} catch (Exception e) {
				}

				// 解析broadCastTime ,获取年月日时分秒
				String broadCastTime = publishDto.getBroadCastTime();

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
				int hour = date.getHours();
				String hourStr = hour + "";
				if (hour < 10) {
					hourStr = "0" + hour;
				}

				int minute = date.getMinutes();
				String minuteStr = minute + "";
				if (minute < 10) {
					minuteStr = "0" + minute;
				}
				int second = date.getSeconds();
				String secondStr = second + "";
				if (second < 10) {
					secondStr = "0" + second;
				}

				String column_alias = publishDto.getColumn();
				String randomFlag = getRandomFlag();
				uploadFtp(md5, year, monthStr, dayStr, hourStr, minuteStr, secondStr, url, randomFlag);

				String assetId = publishDto.getAssetId();
				Integer videoType = publishDto.getType();
				mkMD5File(md5, year, monthStr, dayStr, hourStr, minuteStr, secondStr, url, column_alias, assetId,
						videoType, randomFlag);
			}
		} catch (Exception e) {
			e.printStackTrace();
			apiResponse.setStatusCode(ResponseDictionary.SERVEREXCEPTION);
			apiResponse.setMessage("首都在线worker失败" + e.getMessage());
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("publishId", publishId);
			data.put("dataTransferStatus", dataTransferStatus);
			apiResponse.setData(data);
			return apiResponse;
		}

		try {
			host = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
			port = PropertiesUtil.getPropertiesUtil().get("gearman.port");
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (CapitalMediaDataDto media : medias) {
			CmsMovieDto cmsMovieDto = new CmsMovieDto();
			String sourceUrl = media.getSourceUrl();
			// 获取比特率，宽高, size ,从worker中获取
			Map<String, Object> dataMap = getMediaInfo(host, port, sourceUrl);
			String height = String.valueOf(dataMap.get("height"));
			String width = String.valueOf(dataMap.get("width"));
			Double bitrate = Double.valueOf(String.valueOf(dataMap.get("bitrate")));
			Integer size = Integer.valueOf(String.valueOf(dataMap.get("size")));
			cmsMovieDto.setId(media.getId());
			cmsMovieDto.setWidth(width);
			cmsMovieDto.setHeight(height);
			cmsMovieDto.setSize(size);
			cmsMovieDto.setBitrate(bitrate);

			mediaDtos.add(cmsMovieDto);
		}
		// 组织返回的数据
		apiResponse.setStatusCode(ResponseDictionary.SUCCESS);
		apiResponse.setMessage("首都在线worker成功------------------");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("publishId", publishId);
		data.put("dataTransferStatus", dataTransferStatus);
		HashMap<String, Object> extraData = new HashMap<String, Object>();
		extraData.put("medias", mediaDtos);
		extraData.put("mediaAssetId", publishDto.getMediaAssetId());
		data.put("extraData", extraData);

		apiResponse.setData(data);
		return apiResponse;
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
		////
//		 Map<String, Object> dataMap = new HashMap<String, Object>();
//		 dataMap.put("height", "200");
//		 dataMap.put("width", "200");
//		 dataMap.put("bitrate", "2");
//		 dataMap.put("size", "200");
//		 return dataMap;
	}

	/**
	 * 将文件上传道ftp，并生成一个.md5文件
	 * 
	 * @param md5
	 * @param year
	 * @param month
	 * @param date
	 * @param hour
	 * @param minute
	 * @param second
	 * @param url
	 * @throws Exception
	 */
	public void uploadFtp(String md5, String year, String month, String date, String hour, String minute, String second,
			String url, String randomFlag) throws Exception {
		InputStream input = null;
		log.info("url===========" + url);
		File file = new File(url);
		String ftpRootDir = PropertiesUtil.getPropertiesUtil().get("ftp.rootDir");
		String dirName = year + "-" + month + "-" + date;

		String fileName = year + month + date + hour + minute + second + "_" + randomFlag +"."+ suffix(file.getName());
		String filePath = ftpRootDir + "/" + dirName;
		// 判读url
		if (url.contains("http://")) {
			input = FileUtil.getFileStream(url);

		} else {
			// 本地路径

			boolean exists = FileUtil.exists(url);
			if (!exists) {
				throw new FileNotFoundException("文件不存在："+url);
			}
			input = new FileInputStream(file);
		}
		log.info("获取到input`````````=" + input.available());
		FTPUtilClient ftpClient = FTPUtilClient.getInstance();
		if (ftpClient.connect()) {
			boolean upload = ftpClient.upload(fileName, filePath, input);
			System.out.println(upload);
		}

	}

	/**
	 * 生成一个md5文件
	 * 
	 * @param md5
	 * @param year
	 * @param month
	 * @param date
	 * @param hour
	 * @param minute
	 * @param second
	 * @param url
	 * @throws Exception
	 */
	@Autowired
	public void mkMD5File(String md5, String year, String month, String date, String hour, String minute, String second,
			String url, String column, String assetId, Integer videoType, String randomFlag) throws Exception {
		String ftpRootDir = PropertiesUtil.getPropertiesUtil().get("ftp.rootDir");
		String dirName = year + "-" + month + "-" + date;
		// String fileName = assetId + ".md5";
		String fileName = year + month + date + hour + minute + second + "_" + randomFlag + ".txt";
		String filePath = ftpRootDir;
		File file = new File(url);
		String video_fileName = year + month + date + hour + minute + second + "_" + randomFlag
				+"."+ suffix(file.getName());
		String file_content = md5 + "[INFO]" + column + "#" + year + month + date + "[INFO]" + videoType + "[INFO]"
				+ "/" + dirName + "/" + video_fileName;
		FTPUtilClient ftpClient = FTPUtilClient.getInstance();
		if (ftpClient.connect()) {
			try {
				ftpClient.writeFileToFtp(fileName, filePath, file_content);

			} catch (Exception e) {
				log.error("写MD5文件报错e=" + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	public String suffix(String fileName) {
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		return suffix;
	}

	public String getRandomFlag() {
		String[] charArr = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
				"p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
				"V", "W", "X", "Y", "Z" };
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 4; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(charArr[x % 0x3E]);
		}
		return shortBuffer.toString();
	}

	public static void main(String[] args) {
		// File file = new
		// File("http://bj.bcebos.com/videoworks-8/ce11b0572c9c0bbad15e29045823fc58");
		// String fileName = file.getName();
		// System.out.println("-----------" + fileName);
		// String height = String.valueOf(null);

	}

}
