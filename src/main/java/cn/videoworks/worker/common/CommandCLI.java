package cn.videoworks.worker.common;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName:CommandCLI
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * apache CommandCLI工具类
 * @author   meishen
 * @version  
 * @since    Ver 1.1
 * @Date	 2018	2018年6月24日		下午3:05:55
 *
 * @see 	 
 */
public class CommandCLI {
	
	private static Logger logger = LoggerFactory.getLogger(CommandCLI.class);

	/**
	 * buildOptions:(定义启动参数)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月24日		下午3:06:20
	 * @return   
	 * @return Options    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	private static Options buildOptions() {
		Options options = new Options();
		Option config = new Option("config", true, "配置文件路径");
		options.addOption(config);
		return options;
	}
	
	/**
	 * getCommandLine:(获取commandLine命令行)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月24日		下午3:06:36
	 * @param args
	 * @return   
	 * @return CommandLine    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	public static CommandLine getCommandLine(String[] args) {
		Options ops = buildOptions();
		CommandLine comm = null;
		try {
		    comm = new DefaultParser().parse(ops, args);
		} catch (ParseException e) {
		    e.printStackTrace();
		    logger.error("解析参数失败，请输入正确的指令，指令包含：[-config]");
		}
		return comm;
	}
	
	/**
	 * valid:(验证命令行参数)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月24日		下午3:06:53
	 * @param args
	 * @return   
	 * @return boolean    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	public static boolean valid(String[] args) {
		CommandLine comm =  getCommandLine(args);
		if (comm.getOptions().length == 0) {
			logger.info("请指定参数，参数包含【-config 配置文件路径】");
			return false;
		}
		
		String configFilePath = null;
		if (comm.hasOption("config")) {
            configFilePath = comm.getOptionValue("config");
            try {
				File file = new File(configFilePath);
				if(!file.exists()) {
					logger.error("配置文件【"+configFilePath+"】，不存在！");
					return false;
				}
			} catch (Exception e) {
				logger.error("无法读取配置文件【"+configFilePath+"】，请确保路径是否正确");
				return false;
			}
        }else{
        	logger.error("请通过-config选项输入配置文件路径");
        	return false;
        }
        if (StringUtils.isBlank(configFilePath)) {
        	logger.error("请输入配置文件路径");
        	return false;
        }
        
		return true;
	}
}
