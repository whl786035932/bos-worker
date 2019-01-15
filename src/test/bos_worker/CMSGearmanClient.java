package bos_worker;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobPriority;
import org.gearman.GearmanJoin;
import org.gearman.GearmanServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.constant.WorkerFunctionConstant;
import cn.videoworks.worker.dto.CmsImageDto;
import cn.videoworks.worker.dto.CmsMovieDto;
import cn.videoworks.worker.dto.PublishImageDto;
import cn.videoworks.worker.dto.PublishMovieDto;
import cn.videoworks.worker.dto.PublishStorageDto;
import cn.videoworks.worker.dto.PublishWorkerDto;
import cn.videoworks.worker.util.FileUtil;

/**
 * Hello world!
 */
public class CMSGearmanClient {

	private static final Logger log = LoggerFactory.getLogger(CMSGearmanClient.class);

	public CMSGearmanClient() {
		super();
	}

	private Gearman gearman = null;
	private GearmanClient client = null;

	public CMSGearmanClient(String host, Integer port, String functionName) {
		gearman = Gearman.createGearman();

		client = gearman.createGearmanClient();
		GearmanServer server = gearman.createGearmanServer(host, port);

		client.addServer(server);
	}

	public void submitJob(String data,GearmanJobPriority prioprity) throws Exception {

		try {
			GearmanJoin<String> submitJob = client.submitJob("CMS_INSTORAGE", data.getBytes("UTF-8"),
					prioprity, null, new CMSCallback());
			// 异步任务提交成功过
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}

	


	
//	
//	@Test
//	public void testMoniSendCMSTsk() {
//		try {
//			String host = FileUtil.readProperties("bos-worker.properties", "gearman.ip");
//			String port = FileUtil.readProperties("bos-worker.properties", "gearman.port");
//			CMSGearmanClient cmsGearmanClient = new CMSGearmanClient(host, Integer.valueOf(port),
//					WorkerFunctionConstant.CMS_INSTORAGE);
//			CmsStorageWorkerDto buildCmsStorageDto = buildCmsStorageDto();
//			String format = JsonConverter.format(buildCmsStorageDto);
//			GearmanJobPriority normalPriority = GearmanJobPriority.NORMAL_PRIORITY;
//			cmsGearmanClient.submitJob(format,normalPriority);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	public static void main(String[] args) {
		try {
			String host = FileUtil.readProperties("bos-worker.properties", "gearman.ip");
			String port = FileUtil.readProperties("bos-worker.properties", "gearman.port");
			CMSGearmanClient cmsGearmanClient = new CMSGearmanClient(host, Integer.valueOf(port),
					WorkerFunctionConstant.WRITE_CMS);
			PublishWorkerDto buildCmsStorageDto = buildCmsStorageDto();
			String format = JsonConverter.format(buildCmsStorageDto);
			GearmanJobPriority normalPriority = GearmanJobPriority.NORMAL_PRIORITY;
			cmsGearmanClient.submitJob(format,normalPriority);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public  static PublishWorkerDto buildCmsStorageDto() {
		PublishWorkerDto cmsStorageWorkerDto = new PublishWorkerDto();
		cmsStorageWorkerDto.setPublishId(1+"");
		cmsStorageWorkerDto.setCustomerName("客户whl");
		cmsStorageWorkerDto.setPriority("NORMAL_PRIORITY");
		cmsStorageWorkerDto.setDataTransferStatus(0);
		
		cmsStorageWorkerDto.setExtraData(null);
		
		PublishStorageDto cmsStorageDto = new PublishStorageDto();
		cmsStorageDto.setBianji("中央新闻");
		cmsStorageDto.setNumber(50);
		cmsStorageDto.setType(1);

		cmsStorageDto.setMediaAssetId("123456");
		ArrayList<String> language = new ArrayList<>();
		language.add("英文");
		cmsStorageDto.setLangeage(language);
		
		cmsStorageDto.setTitle("标题");
//		cmsStorageDto.setTitle_abbr("bzszm");
		
		ArrayList<String> tree = new ArrayList<String>();
		tree.add("a/b/c");
		cmsStorageDto.setTree(tree);
		cmsStorageDto.setAssetId(9);
		cmsStorageDto.setSource("视频工厂");
		cmsStorageDto.setDescription("测试cms入库的worker");

		//组织medias
		ArrayList<PublishMovieDto> movies = new ArrayList<>();
		PublishMovieDto cmsMovieDto = new PublishMovieDto();
		cmsMovieDto.setSourceUrl("http://bj.bcebos.com/videoworks-f/e509161c83338e3e361e4d74b4be309f.jpg");
		cmsMovieDto.setTargetUrl("http://bj.bcebos.com/videoworks-f/e509161c83338e3e361e4d74b4be309f.jpg");
		cmsMovieDto.setDuration(60l);
		cmsMovieDto.setId("ba1cfa3caa753979a3cb45a895bd4dde");
		movies.add(cmsMovieDto);
		cmsStorageDto.setMedias(movies);
		
		ArrayList<PublishImageDto> images = new ArrayList<PublishImageDto>();
		PublishImageDto cmsImageDto = new  PublishImageDto();
		cmsImageDto.setId("6acbf0bd48cd326dbf24fa5f7898b3b6");
		cmsImageDto.setSourceUrl("http://bj.bcebos.com/videoworks-f/e509161c83338e3e361e4d74b4be309f.jpg");
		cmsImageDto.setTargetUrl("http://bj.bcebos.com/videoworks-f/e509161c83338e3e361e4d74b4be309f.jpg");
		cmsImageDto.setType(1);
		cmsImageDto.setWidth(50);
		cmsImageDto.setHeight(30);
		cmsImageDto.setSequence(0);
		images.add(cmsImageDto);
		cmsStorageDto.setPosters(images);
		cmsStorageDto.setBroadCastTime("2018-11-20 09:20:12");
		
		
		//可选
		cmsStorageDto.setTags(Arrays.asList(new String[]{"推荐","国内"}));
		cmsStorageDto.setCp("内容提供商");
		cmsStorageDto.setChannel("CCTV");
		cmsStorageDto.setColumn("中央栏目");
		cmsStorageDto.setClassifications(new ArrayList<String>());
		cmsStorageDto.setAreas(new ArrayList<String>());
		
		cmsStorageWorkerDto.setExtraData(cmsStorageDto);
		return cmsStorageWorkerDto;
		
		
	}

}
