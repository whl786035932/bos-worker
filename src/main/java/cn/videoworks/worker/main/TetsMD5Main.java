package cn.videoworks.worker.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TetsMD5Main {
	private static Logger logger = LoggerFactory.getLogger(TetsMD5Main.class);
	
	public static void main(String[] args) {
		String filePath = args[0];
		logger.info("计算MD5的文件路径是="+filePath);
		File file = new File(filePath);
		try {
			byte[] bytes = getByte(file);
			logger.info("计算的文件的字节大小是="+bytes.length);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static byte[] getByte(File file) throws Exception
    {
        byte[] bytes = null;
        if(file!=null)
        {
            InputStream is = new FileInputStream(file);
            int length = (int) file.length();
            if(length>Integer.MAX_VALUE)   //当文件的长度超过了int的最大值
            {
                System.out.println("this file is max ");
                return null;
            }
            bytes = new byte[length];
            int offset = 0;
            int numRead = 0;
            while(offset<bytes.length&&(numRead=is.read(bytes,offset,bytes.length-offset))>=0)
            {
                offset+=numRead;
            }
            //如果得到的字节长度和file实际的长度不一致就可能出错了
            if(offset<bytes.length)
            {
                System.out.println("file length is error");
                return null;
            }
            is.close();
        }
        return bytes;
    }

}
