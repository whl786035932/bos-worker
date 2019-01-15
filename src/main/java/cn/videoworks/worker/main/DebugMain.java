package cn.videoworks.worker.main;

import cn.videoworks.worker.common.ParameterMap;
import cn.videoworks.worker.work.CapitalOnlineWork;
import cn.videoworks.worker.work.CmsWorker;
import cn.videoworks.worker.work.MinioStorageWork;
import cn.videoworks.worker.work.NetEaseWorker;
import cn.videoworks.worker.work.StorageWork;

public class DebugMain {

	/**
	 * main:(程序测试入口)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		下午6:52:27
	 * @param args   
	 * @return void    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	public static void main(String[] args) {
		ParameterMap.getParameterMap("D:\\gearman.properties"); //初始化配置文件
//		CmsWorker.registWorker();
//		StorageWork.registWorker();
//		CapitalOnlineWork.registWorker(); 
//		NetEaseWorker.registWorker();
		MinioStorageWork.registWorker();
	}
}
