package cn.videoworks.worker.client;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.worker.common.ParameterMap;
import cn.videoworks.worker.util.PropertiesUtil;
import io.minio.MinioClient;

public class MinioSDKClient {
	
	private static MinioClient minioClient = null;
	
	private static MinioSDKClient minioSDKClient = null;
	
	private static Logger logger = LoggerFactory.getLogger(MinioSDKClient.class);
	
	@SuppressWarnings("static-access")
	private MinioSDKClient() {
		try {
		      minioClient = new MinioClient(PropertiesUtil.getPropertiesUtil().get("aws_endpoint"), PropertiesUtil.getPropertiesUtil().get("aws_access_key_id"), PropertiesUtil.getPropertiesUtil().get("aws_secret_access_key"));
		    } catch(Exception e) {
		    	e.printStackTrace();
				logger.error("读取配置文件【gearman.properties】失败");
		    }
	}
	
	/**
	 * getAmazonS3Client:(单例)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		下午5:39:52
	 * @return   
	 * @return AWSS3Client    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	public static synchronized MinioSDKClient getMinioSDKClient() {
		if (minioSDKClient == null) { 
			synchronized(MinioSDKClient.class) {
				if(minioSDKClient ==  null) {
					minioSDKClient = new MinioSDKClient();  
				}
			}
        }    
       return minioSDKClient;  
	}
	
	/**
	 * createBucket:(创建bucket)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		上午11:56:00
	 * @param bucketName   
	 * @return void    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	public static void createBucket(String bucketName) {
		try {
			  boolean isExist = minioClient.bucketExists(bucketName);
		      if(!isExist) {
		    	  minioClient.makeBucket(bucketName);
		    	  StringBuilder builder = new StringBuilder();
		    	  builder.append("{\n");
		    	  builder.append("    \"Statement\": [\n");
		    	  builder.append("        {\n");
		    	  builder.append("            \"Action\": [\n");
		    	  builder.append("                \"s3:GetBucketLocation\",\n");
		    	  builder.append("                \"s3:ListBucket\"\n");
		    	  builder.append("            ],\n");
		    	  builder.append("            \"Effect\": \"Allow\",\n");
		    	  builder.append("            \"Principal\": \"*\",\n");
		    	  builder.append("            \"Resource\": \"arn:aws:s3:::"+bucketName+"\"\n");
		    	  builder.append("        },\n");
		    	  builder.append("        {\n");
		    	  builder.append("            \"Action\": \"s3:GetObject\",\n");
		    	  builder.append("            \"Effect\": \"Allow\",\n");
		    	  builder.append("            \"Principal\": \"*\",\n");
		    	  builder.append("            \"Resource\": \"arn:aws:s3:::"+bucketName+"/*\"\n");
		    	  builder.append("        }\n");
		    	  builder.append("    ],\n");
		    	  builder.append("    \"Version\": \"2012-10-17\"\n");
		    	  builder.append("}\n");
		    	  minioClient.setBucketPolicy(bucketName, builder.toString());
		    	  logger.info("bucket【"+bucketName+"】创建成功");
		      }
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("bucket【"+bucketName+"】创建失败");
		} 
	}
	
	/**
	 * putObject:(上传object)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		下午1:49:38
	 * @param bucketName
	 * @param key
	 * @param file   
	 * @return void    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	public static void putObject(String bucketName,String key,File file,boolean isPublic) {
		createBucket(bucketName);
		try {
			minioClient.putObject(bucketName, key, file.getAbsolutePath());
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("object【"+bucketName+"】【"+key+"】创建失败");
		}
	}
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		ParameterMap.getParameterMap("D:\\gearman.properties"); 

		MinioSDKClient.getMinioSDKClient().putObject("videoworks-11", "key99967", new File("D:\\test.mp4"), true);
//		MinioSDKClient.getMinioSDKClient().createBucket("0000000test10");
	}
}
