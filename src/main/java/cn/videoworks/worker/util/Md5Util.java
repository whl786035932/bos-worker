/**
 * Md5Util.java
 * cn.videoworks.publisher.util
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2015年4月23日 		meishen
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
*/

package cn.videoworks.worker.util;

import java.security.NoSuchAlgorithmException;


/**
 * ClassName:Md5Util
 *
 * @author   meishen
 * @version  
 * @since    Ver 1.0.0
 * @Date	 2015年4月23日		上午11:45:54
 *
 */
public class Md5Util {

	/**
     * 对字符串进行加密处理
     * @param strString
     * @return 加密后的字符串
     */
	 public static String getMD5(byte[] source) {  
	        String s = null;  
	        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
	                'a', 'b', 'c', 'd', 'e', 'f' };// 用来将字节转换成16进制表示的字符  
	        try {  
	            java.security.MessageDigest md = java.security.MessageDigest  
	                    .getInstance("MD5");  
	            md.update(source);  
	            byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，  
	            // 用字节表示就是 16 个字节  
	            char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16  
	            // 进制需要 32 个字符  
	            int k = 0;// 表示转换结果中对应的字符位置  
	            for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16  
	                // 进制字符的转换  
	                byte byte0 = tmp[i];// 取第 i 个字节  
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>  
	                // 为逻辑右移，将符号位一起右移  
	                str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换  
	  
	            }  
	            s = new String(str);// 换后的结果转换为字符串  
	  
	        } catch (NoSuchAlgorithmException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	        return s;  
	    }  
	 
	 	public static String getMD5(String source) {  
	        String s = null;  
	        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
	                'a', 'b', 'c', 'd', 'e', 'f' };// 用来将字节转换成16进制表示的字符  
	        try {  
	            java.security.MessageDigest md = java.security.MessageDigest  
	                    .getInstance("MD5");  
	            md.update(source.getBytes());  
	            byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，  
	            // 用字节表示就是 16 个字节  
	            char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16  
	            // 进制需要 32 个字符  
	            int k = 0;// 表示转换结果中对应的字符位置  
	            for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16  
	                // 进制字符的转换  
	                byte byte0 = tmp[i];// 取第 i 个字节  
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>  
	                // 为逻辑右移，将符号位一起右移  
	                str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换  
	  
	            }  
	            s = new String(str);// 换后的结果转换为字符串  
	  
	        } catch (NoSuchAlgorithmException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	        return s;  
	    }  
	 
	    public static void main(String[] args){  
	      
	        String test=Md5Util.getMD5("123456".getBytes()); //e10adc3949ba59abbe56e057f20f883e 
	        System.out.println(test);  
	  
	    }  
}

