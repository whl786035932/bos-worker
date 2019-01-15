package cn.videoworks.worker.main;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.BasicConfigurator;

import cn.videoworks.worker.common.CommandCLI;
import cn.videoworks.worker.common.ParameterMap;
import cn.videoworks.worker.work.YouKuV2Worker;

public class YouKuWorkerMain {
	/**
	 * main:(优酷v2入口)
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

			YouKuV2Worker.registWorker();
		}
	}
}
