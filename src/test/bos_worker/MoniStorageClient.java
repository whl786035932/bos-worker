package bos_worker;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobPriority;
import org.gearman.GearmanJoin;
import org.gearman.GearmanServer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.commons.util.json.JsonConverter;
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
public class MoniStorageClient {

	private static final Logger log = LoggerFactory.getLogger(MoniStorageClient.class);

	public MoniStorageClient() {
		super();
	}

	private Gearman gearman = null;
	private GearmanClient client = null;

	public MoniStorageClient(String host, Integer port, String functionName) {
		gearman = Gearman.createGearman();

		client = gearman.createGearmanClient();
		GearmanServer server = gearman.createGearmanServer(host, port);

		client.addServer(server);
	}

	public void submitJob(String data,GearmanJobPriority prioprity) throws Exception {

		try {
			GearmanJoin<String> submitJob = client.submitJob("WRITE_STORAGE", data.getBytes("UTF-8"),
					prioprity, null, new CMSCallback());
			
			// 异步任务提交成功过
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}

	


	

	public static void main(String[] args) {
		try {
			String host = FileUtil.readProperties("bos-worker.properties", "gearman.ip");
			String port = FileUtil.readProperties("bos-worker.properties", "gearman.port");
			MoniStorageClient cmsGearmanClient = new MoniStorageClient(host, Integer.valueOf(port),
					WorkerFunctionConstant.WRITE_STORAGE);
			List<AwsStorageDto> buildCmsStorageDto = buildAWSStorageDto();
			String format = JsonConverter.format(buildCmsStorageDto);
			GearmanJobPriority normalPriority = GearmanJobPriority.NORMAL_PRIORITY;
			cmsGearmanClient.submitJob(format,normalPriority);
			System.out.println("------------------------------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public  static 	List<AwsStorageDto> buildAWSStorageDto() {
		ArrayList<AwsStorageDto> dtos = new ArrayList<AwsStorageDto>();
	    AwsStorageDto awsStorageDto = new AwsStorageDto();
	    awsStorageDto.setId("111l");
	    awsStorageDto.setUrl("/home/wanghl/svn.key");
//	    awsStorageDto.setCheck_sum("f50c0013b15d3c2b6b6b7324b15cbfba");
	    awsStorageDto.setType(2);
	    
	    AwsStorageDto awsStorageDto2 = new AwsStorageDto();
	    awsStorageDto2.setId("222L");
	    awsStorageDto2.setUrl("/home/wangl/svn.key");
//	    awsStorageDto2.setCheck_sum("f50c0013b15d3c2b6b6b7324b15cbfba");
	    awsStorageDto2.setType(2);
	    dtos.add(awsStorageDto2);
	    dtos.add(awsStorageDto);
	    
		return dtos;
		
		
	}

}
