package cn.videoworks.worker.work;

import org.apache.commons.lang3.StringUtils;
import org.gearman.Gearman;
import org.gearman.GearmanServer;
import org.gearman.GearmanWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.worker.constant.WorkerFunctionConstant;
import cn.videoworks.worker.function.JRTTFunction;
import cn.videoworks.worker.function.YouKuFunction;
import cn.videoworks.worker.util.PropertiesUtil;

/**
 * 今日头条数据注入.
 * @author pei
 *
 */
public class JRTTWorker {
	private static Logger log = LoggerFactory.getLogger(JRTTWorker.class);
	/**
	 * 今日头条:(今日头条注入work)
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
			worker.addFunction(WorkerFunctionConstant.JRTT,new JRTTFunction());

			boolean b = worker.addServer(server);
			worker.getGearman();
			if (b)
				log.info("创建今日头条work成功！");
			else
				log.error("创建今日头条work失败！");

		} else {
			log.error("读取gearman配置信息失败");
		}
	}
}
