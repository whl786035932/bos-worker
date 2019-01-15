package cn.videoworks.worker.client;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobEventType;
import org.gearman.GearmanJobPriority;
import org.gearman.GearmanJobReturn;
import org.gearman.GearmanServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.dto.MediaDataDto;
import cn.videoworks.worker.dto.MediaInfoDto;
import cn.videoworks.worker.dto.MediaVideoDto;
import cn.videoworks.worker.util.ResponseStatusCode;

/**
 * Hello world!
 */
public class MD5GearmanClient {

	private static final Logger log = LoggerFactory.getLogger(MD5GearmanClient.class);

	public MD5GearmanClient() {
		super();
	}

	private Gearman gearman = null;
	private GearmanClient client = null;
	private static MD5GearmanClient cdnGearmnClient = null;
	private String host;
	private Integer port;

	private Properties databaseConfig;

	public MD5GearmanClient(String host, Integer port, String functionName) {
		this.host = host;
		this.port = port;
		gearman = Gearman.createGearman();
		client = gearman.createGearmanClient();
		GearmanServer server = gearman.createGearmanServer(host, port);

		client.addServer(server);

	}

	public static synchronized MD5GearmanClient getCDNGearmanClient(String ip, int port, String functionName) {
		if (null == cdnGearmnClient) {
			cdnGearmnClient = new MD5GearmanClient(ip, port, functionName);
		}
		return cdnGearmnClient;
	}

	public Map<String, Object> submitMd5Job(String data,String functionName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			GearmanJobReturn jobReturn = client.submitJob(functionName, data.getBytes("UTF-8"),
					GearmanJobPriority.HIGH_PRIORITY);
			while (!jobReturn.isEOF()) {
				GearmanJobEvent event;
				try {
					event = jobReturn.poll();
					GearmanJobEventType eventType = event.getEventType();
					switch (eventType) {
						case GEARMAN_JOB_SUCCESS:
							String result = new String(event.getData(), "utf-8");
							System.out.println(result);
							Map returnMap = JsonConverter.parse(result, Map.class);
							String done_str =String.valueOf( returnMap.get("done"));
							Boolean done = Boolean.valueOf(done_str);
							if(done) {
								Map dataMap = JsonConverter.asMap(JsonConverter.format(returnMap.get("data")), String.class, String.class);
								String checksum  = String.valueOf(dataMap.get("checksum"));
								resultMap.put("check_sum", checksum);
								resultMap.put("statusCode", ResponseStatusCode.OK);
							}else {
								resultMap.put("statusCode", ResponseStatusCode.BAD_REQUEST);
								String errror = String.valueOf(returnMap.get("error"));
								resultMap.put("message", errror);
							}
							break;
						case GEARMAN_SUBMIT_FAIL:
							resultMap.put("statusCode", ResponseStatusCode.INTERNAL_SERVER_ERROR);
							break;
						case GEARMAN_JOB_FAIL:
							resultMap.put("statusCode", ResponseStatusCode.INTERNAL_SERVER_ERROR);
							break;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultMap;
	}
	
	public Map<String, Object> submitMediaInfoJob(String data,String functionName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			GearmanJobReturn jobReturn = client.submitJob(functionName, data.getBytes("UTF-8"),
					GearmanJobPriority.HIGH_PRIORITY);
			while (!jobReturn.isEOF()) {
				GearmanJobEvent event;
				try {
					event = jobReturn.poll();
					GearmanJobEventType eventType = event.getEventType();
					switch (eventType) {
						case GEARMAN_JOB_SUCCESS:
							String result = new String(event.getData(), "utf-8");
							System.out.println("result="+result);
							MediaInfoDto returnMap = JsonConverter.parse(result, MediaInfoDto.class);
							Boolean done = returnMap.getDone();
							if(done) {
								//---begin
								HashMap<String, Object> hashMap = new HashMap<String,Object>();
								 MediaDataDto data_map = returnMap.getData();
								 MediaVideoDto video =data_map.getVideo();
								hashMap.put("height",video.getHeight());
								hashMap.put("width",video.getWidth());
								hashMap.put("bitrate", video.getBitrate());
								hashMap.put("size", data_map.getSize());
								resultMap.put("data", hashMap);
								resultMap.put("statusCode", ResponseStatusCode.OK);
								//---end
								
							}else {
								resultMap.put("statusCode", ResponseStatusCode.BAD_REQUEST);
								resultMap.put("message", "get media info error");
							}
							break;
						case GEARMAN_SUBMIT_FAIL:
							resultMap.put("statusCode", ResponseStatusCode.INTERNAL_SERVER_ERROR);
							break;
						case GEARMAN_JOB_FAIL:
							resultMap.put("statusCode", ResponseStatusCode.INTERNAL_SERVER_ERROR);
							break;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultMap;
	}

}
