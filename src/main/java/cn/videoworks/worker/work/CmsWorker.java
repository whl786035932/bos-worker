package cn.videoworks.worker.work;

import org.apache.commons.lang3.StringUtils;
import org.gearman.Gearman;
import org.gearman.GearmanServer;
import org.gearman.GearmanWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.worker.constant.WorkerFunctionConstant;
import cn.videoworks.worker.function.CmsFunction;
import cn.videoworks.worker.util.PropertiesUtil;

public class CmsWorker {
	private static Logger log = LoggerFactory.getLogger(CmsWorker.class);
   
    @SuppressWarnings("static-access")
	public static void registWorker(){
    	Gearman gearman = Gearman.createGearman();
		String gearmanIp = "";
		int gearmanPort = -1;
		GearmanWorker worker=null;
		try {
			gearmanIp = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
			gearmanPort = Integer.valueOf(PropertiesUtil.getPropertiesUtil().get("gearman.port"));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("系统读取gearman work配置信息失败！");
			System.exit(-1);
		}
		
		if (StringUtils.isNotBlank(gearmanIp) && -1 != gearmanPort) {
			GearmanServer server = gearman.createGearmanServer(gearmanIp, gearmanPort);
			log.info("任务调度【gearman】服务地址：" + server.toString());
			worker = gearman.createGearmanWorker();
			worker.addFunction(WorkerFunctionConstant.WRITE_CMS, new CmsFunction());
			worker.addServer(server);
			worker.getGearman();
		} else {
			log.error("读取gearman work配置信息失败");
		}
    }
}
