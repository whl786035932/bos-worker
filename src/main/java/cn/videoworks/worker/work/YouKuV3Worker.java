package cn.videoworks.worker.work;

import org.apache.commons.lang3.StringUtils;
import org.gearman.Gearman;
import org.gearman.GearmanServer;
import org.gearman.GearmanWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.worker.constant.WorkerFunctionConstant;
import cn.videoworks.worker.function.WriteBosStorage;
import cn.videoworks.worker.function.YouKuV3Function;
import cn.videoworks.worker.util.PropertiesUtil;

/**
 * ClassName:YouKuV3Worker 优酷v3注入work
 * 
 * @version
 * @since Ver 1.1
 * 
 * @see
 */
public class YouKuV3Worker {
	private static Logger log = LoggerFactory.getLogger(YouKuV3Worker.class);

	/**
	 * YouKuV3Worker:(优酷v3注入work)
	 * 
	 * @return void
	 * @throws
	 * @since Videoworks　Ver 1.1
	 */
	@SuppressWarnings("static-access")
	public static void registWorker() {
		Gearman gearman = Gearman.createGearman();

		String gearmanIp = "";
		int gearmanPort = -1;
		try {
			gearmanIp = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
			gearmanPort = Integer.valueOf(PropertiesUtil.getPropertiesUtil().get("gearman.port"));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("系统读取gearman配置信息失败！");
			System.exit(-1);
		}
		if (StringUtils.isNotBlank(gearmanIp) && -1 != gearmanPort) {
			GearmanServer server = gearman.createGearmanServer(gearmanIp,gearmanPort);
			log.info("服务地址：" + server.toString());
			GearmanWorker worker = gearman.createGearmanWorker();
			worker.addFunction(WorkerFunctionConstant.WRITE_YOUKUV3,new YouKuV3Function());

			boolean b = worker.addServer(server);
			worker.getGearman();
			if (b)
				log.info("创建优酷V3work成功！");
			else
				log.error("创建优酷V3work失败！");

		} else {
			log.error("读取gearman配置信息失败");
		}
	}
}
