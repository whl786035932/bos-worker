package cn.videoworks.worker.work;

import org.apache.commons.lang3.StringUtils;
import org.gearman.Gearman;
import org.gearman.GearmanServer;
import org.gearman.GearmanWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.worker.constant.WorkerFunctionConstant;
import cn.videoworks.worker.function.CapitalOnlineFunction;
import cn.videoworks.worker.util.PropertiesUtil;

/**
 * ClassName:StorageWork
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 *  首都在线的worker
 * @author   whl
 * @version  
 * @since    Ver 1.1
 * @Date	 2018	2018年12月04日		上午10:58
 *
 * @see 	 
 */
public class CapitalOnlineWork {
	
	private static Logger log = LoggerFactory.getLogger(CapitalOnlineWork.class);
	
    /**
     * registWorker:(注册work)
     *
     * @author   meishen
     * @Date	 2018	2018年6月17日		下午6:50:22   
     * @return void    
     * @throws 
     * @since  Videoworks　Ver 1.1
     */
    @SuppressWarnings("static-access")
	public static void registWorker(){
    	 GearmanWorker worker = null;
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
				;
			}
			
			if (StringUtils.isNotBlank(gearmanIp) && -1 != gearmanPort) {
				GearmanServer server = gearman.createGearmanServer(gearmanIp, gearmanPort);
				log.info("服务地址：" + server.toString());
				worker = gearman.createGearmanWorker();
				worker.addFunction(WorkerFunctionConstant.CAPITAL_ONLINE, new CapitalOnlineFunction());
				boolean b = worker.addServer(server);
				worker.getGearman();
				
			} else {
				log.error("读取gearman配置信息失败");
			}
    }
}
