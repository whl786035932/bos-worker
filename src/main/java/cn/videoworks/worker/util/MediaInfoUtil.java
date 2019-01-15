package cn.videoworks.worker.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.client.MD5GearmanClient;
import cn.videoworks.worker.exception.MediaInfoException;

public class MediaInfoUtil {
	/**
	 * host gearmand的ip
	 * port  gearman的port
	 * url 是解析的视频的地址
	 * @param host
	 * @param port
	 * @param url
	 * @return    {
			    "id":"5c9d887e1aaa3364b195bc775b197856",
				"bitrate": 0.0,
				"width": "720",
				"height": "576",
				"size": 256676024
			}
	 * 
	 * @throws MediaInfoException
	 */
	public static  Map<String, Object> getMediaInfo(String host, String port, String url) throws MediaInfoException {
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
	/**
	 * url是文件的地址
	 * @param url
	 * @return  文件的MD5值
	 * @throws Exception
	 */
	public static  String getmd5Sum(String url) throws Exception {
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

	}
}
