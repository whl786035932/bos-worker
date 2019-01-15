package cn.videoworks.worker.function;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 注入到腾讯的cos对象存储
 * @author whl
 *
 */
public class WriteTencentCOSStorage implements GearmanFunction{
	private static final Logger log = LoggerFactory.getLogger(WriteTencentCOSStorage.class);
	@Override
	public byte[] work(String arg0, byte[] arg1, GearmanFunctionCallback arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
