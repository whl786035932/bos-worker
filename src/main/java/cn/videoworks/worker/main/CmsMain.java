package cn.videoworks.worker.main;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.BasicConfigurator;

import cn.videoworks.worker.common.CommandCLI;
import cn.videoworks.worker.common.ParameterMap;
import cn.videoworks.worker.work.CmsWorker;

/**
 * @author   meishen
 * @Date	 2018	2018年12月18日		下午2:15:15
 * @Description 方法描述: 注入CMS
 */
public class CmsMain {

	/**
	 * main:(注入CMS入口)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		下午6:52:27
	 * @param args   
	 * @return void    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		if (CommandCLI.valid(args)) {
			CommandLine comm = CommandCLI.getCommandLine(args);
			String configFilePath = comm.getOptionValue("config"); // 获取配置文件地址
			ParameterMap.getParameterMap(configFilePath); // 初始化配置文件

			CmsWorker.registWorker();
		}
	}
}
