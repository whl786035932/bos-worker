package bos_worker;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobPriority;
import org.gearman.GearmanJoin;
import org.gearman.GearmanServer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.client.MD5GearmanClient;
import cn.videoworks.worker.constant.WorkerFunctionConstant;
import cn.videoworks.worker.dto.AwsStorageDto;
import cn.videoworks.worker.dto.PublishImageDto;
import cn.videoworks.worker.dto.PublishMovieDto;
import cn.videoworks.worker.dto.PublishStorageDto;
import cn.videoworks.worker.dto.PublishWorkerDto;
import cn.videoworks.worker.util.FileUtil;

/**
 * Hello world!
 */
public class MD5Client {

	private static final Logger log = LoggerFactory.getLogger(MD5Client.class);

	public MD5Client() {
		super();
	}

	private Gearman gearman = null;
	private GearmanClient client = null;

	public MD5Client(String host, Integer port, String functionName) {
		gearman = Gearman.createGearman();

		client = gearman.createGearmanClient();
		GearmanServer server = gearman.createGearmanServer(host, port);

		client.addServer(server);
	}

	public void submitJob(String data,GearmanJobPriority prioprity) throws Exception {

		try {
			GearmanJoin<String> submitJob = client.submitJob("md5sum", data.getBytes("UTF-8"),
					prioprity, null, new CMSCallback());
			
			// 异步任务提交成功过
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}

	


	
/**
 * 异步调用
 * @param args
 */
//	public static void main(String[] args) {
//		try {
//			String host = FileUtil.readProperties("bos-worker.properties", "gearman.ip");
//			String port = FileUtil.readProperties("bos-worker.properties", "gearman.port");
//			MD5Client cmsGearmanClient = new MD5Client(host, Integer.valueOf(port),
//					WorkerFunctionConstant.WRITE_CMS);
//			HashMap<String, String> hashMap = new HashMap<String,String>();
//			String url = "/home/wanghl/svn.key";
//			hashMap.put("filepath", url);
//			String format = JsonConverter.format(hashMap);
//			GearmanJobPriority normalPriority = GearmanJobPriority.NORMAL_PRIORITY;
//			long currentTimeMillis = System.currentTimeMillis();
//			cmsGearmanClient.submitJob(data, prioprity);(format,normalPriority);
//			System.out.println("------------------------------"+currentTimeMillis);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
	/**
	 * 同步调用
	 * @param args
	 */
	
	public static void main(String[] args) {
		String host = FileUtil.readProperties("bos-worker.properties", "gearman.ip");
		String port = FileUtil.readProperties("bos-worker.properties", "gearman.port");
		MD5GearmanClient cdnGearmanClient = MD5GearmanClient.getCDNGearmanClient(host, Integer.valueOf(port), "md5sum");
		HashMap<String, String> hashMap = new HashMap<String,String>();
		String url = "/home/wanghl/svn.key";
		hashMap.put("filepath", url);
		String format = JsonConverter.format(hashMap);
		Map<String, Object> submitMd5Job = cdnGearmanClient.submitMd5Job(format, "md5sum");
		
		Map<String, Object> submitJob = cdnGearmanClient.submitMediaInfoJob(format,"mediainfo");
		System.out.println("======="+JsonConverter.format(submitJob));
		Map dataMap = (Map) submitJob.get("data");
		Integer height =Integer.valueOf(String.valueOf( dataMap.get("height")));
		Integer width =Integer.valueOf(String.valueOf( dataMap.get("width")));
		Double bitrate =Double.valueOf(String.valueOf( dataMap.get("bitrate")));
		Integer size =Integer.valueOf(String.valueOf( dataMap.get("size")));
		System.out.println(height);
		
	}

}
