package bos_worker;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobPriority;
import org.gearman.GearmanJoin;
import org.gearman.GearmanServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.constant.WorkerFunctionConstant;
import cn.videoworks.worker.dto.CapitalMediaDataDto;
import cn.videoworks.worker.dto.CapitalOnLineWorkerDto;
import cn.videoworks.worker.dto.CapitalOnlineDto;
import cn.videoworks.worker.dto.CapitalOnlineExtraDataDto;
import cn.videoworks.worker.dto.PublishWorkerDto;
import cn.videoworks.worker.util.DateUtil;
import cn.videoworks.worker.util.FileUtil;

public class CapitalServer {
	private static final Logger log = LoggerFactory.getLogger(CapitalServer.class);
	private Gearman gearman = null;
	private GearmanClient client = null;

	public CapitalServer() {

	}

	public CapitalServer(String host, Integer port, String functionName) {
		gearman = Gearman.createGearman();

		client = gearman.createGearmanClient();
		GearmanServer server = gearman.createGearmanServer(host, port);

		client.addServer(server);
	}

	public void submitJob(String data, GearmanJobPriority prioprity) throws Exception {

		try {
			GearmanJoin<String> submitJob = client.submitJob(WorkerFunctionConstant.CAPITAL_ONLINE, data.getBytes("UTF-8"), prioprity, null,
					new CMSCallback());

			// 异步任务提交成功过
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		String host = FileUtil.readProperties("bos-worker.properties", "gearman.ip");
		String port = FileUtil.readProperties("bos-worker.properties", "gearman.port");
		CapitalServer captialServer = new CapitalServer(host, Integer.valueOf(port),
				WorkerFunctionConstant.CAPITAL_ONLINE);
		
		CapitalOnLineWorkerDto worker = new CapitalOnLineWorkerDto();
		worker.setPublishId("444444444444444444444");
		worker.setCustomerName("capitalonline");
		worker.setPriority("NORMAL_PRIORITY");
		
		CapitalOnlineExtraDataDto dto = new CapitalOnlineExtraDataDto();
		dto.setBroadCastTime("2016-12-23 16:27:07");
		ArrayList<CapitalMediaDataDto> medias = new ArrayList<CapitalMediaDataDto>();
		CapitalMediaDataDto capitalMediaDataDto = new CapitalMediaDataDto();
		capitalMediaDataDto.setId("ed1025fb35555");
//		capitalMediaDataDto.setSourceUrl("http://vdse.bdstatic.com//c28e7e9a5730e0dae8cc6b0fc5b85d00?authorization=bce-auth-v1%2Ffb297a5cc0fb434c971b8fa103e8dd7b%2F2017-05-11T09%3A02%3A31Z%2F-1%2F%2F6528af5e55ac29260b145e2080e9e2867dbd3026b21cebdd7196740a48ea7b49");
		capitalMediaDataDto.setSourceUrl("/mnt/mccdata/CCTV4/capitalonline/video_96.ts");
//		capitalMediaDataDto.setSourceUrl("/home/wanghl/测试首都在线/test.mp4");
//		capitalMediaDataDto.setSourceUrl("/home/wanghl/video_96.ts");
//		capitalMediaDataDto.setSourceUrl("C:\\Users\\whl\\Desktop\\video_96.ts");
//		capitalMediaDataDto.setSourceUrl("/usr/local/worker/video_96.ts");
		capitalMediaDataDto.setDuration(154900L);
		medias.add(capitalMediaDataDto);
		dto.setMedias(medias);
		
		dto.setAssetId("23");
		dto.setChannel("CCTV4");
		dto.setColumn("国家记忆");
		dto.setSource("videoworks");
		dto.setTitle("台海即使 冰雪初融");
		dto.setType(1);
		dto.setMediaAssetId("e0bb68b2ecbf3d969ckdddd");
		
		
		worker.setExtraData(dto);
		
		
		String data = JsonConverter.format(worker);
		data = new String(data.getBytes("utf-8"));
//		System.out.println("data="+data);
		
		captialServer.submitJob(data, GearmanJobPriority.NORMAL_PRIORITY);
	}
	
	public static void main1(String[] args) throws UnsupportedEncodingException {
		String url = "/home/wanghl/测试首都在线/test.mp4";
		url = stringToUnicode(url);
		System.out.println(url);
	}
	
	public static String stringToUnicode(String s) {  
	    try {  
	        StringBuffer out = new StringBuffer("");  
	        //直接获取字符串的unicode二进制  
	        byte[] bytes = s.getBytes("unicode");  
	        //然后将其byte转换成对应的16进制表示即可  
	        for (int i = 0; i < bytes.length - 1; i += 2) {  
	            out.append("\\u");  
	            String str = Integer.toHexString(bytes[i + 1] & 0xff);  
	            for (int j = str.length(); j < 2; j++) {  
	                out.append("0");  
	            }  
	            String str1 = Integer.toHexString(bytes[i] & 0xff);  
	            out.append(str1);  
	            out.append(str);  
	        }  
	        return out.toString();  
	    } catch (UnsupportedEncodingException e) {  
	        e.printStackTrace();  
	        return null;  
	    }  
	} 

}
