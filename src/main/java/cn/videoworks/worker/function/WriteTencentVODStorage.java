package cn.videoworks.worker.function;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 注入到腾讯云的VOD
 * @author whl
 *
 */
public class WriteTencentVODStorage implements GearmanFunction{
	private static final Logger log = LoggerFactory.getLogger(WriteTencentVODStorage.class);
	
	@Override
	public byte[] work(String arg0, byte[] arg1, GearmanFunctionCallback arg2) throws Exception {
		return null;
	}

}
