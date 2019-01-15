package bos_worker;

import java.io.UnsupportedEncodingException;

import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobEventCallback;

public class CMSCallback implements GearmanJobEventCallback<String> {

	@Override
	public void onEvent(String arg0, GearmanJobEvent event) {
		try {
			String result = new String(event.getData(), "utf-8");
			long currentTimeMillis = System.currentTimeMillis();			
			System.out.println("收到的回调data="+result+";currentTimeMillis="+currentTimeMillis);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("---------------");
	}
	
	
	 public static String bytesToHexFun3(byte[] bytes) {
	        StringBuilder buf = new StringBuilder(bytes.length * 2);
	        for(byte b : bytes) { // 使用String的format方法进行转换
	            buf.append(String.format("%02x", new Integer(b & 0xff)));
	        }

	        return buf.toString();
	    }


}
