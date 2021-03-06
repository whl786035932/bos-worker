package cn.videoworks.worker.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.IOUtils;

import cn.videoworks.worker.common.ParameterMap;
import cn.videoworks.worker.util.PropertiesUtil;


public class MinioAWSS3Client {

	private static AmazonS3 s3 = null;
	
	private static TransferManager tx = null;
	
	private static MinioAWSS3Client aWSS3Client = null;
	
	private static Logger logger = LoggerFactory.getLogger(MinioAWSS3Client.class);
	
	@SuppressWarnings({ "static-access", "deprecation" })
	private MinioAWSS3Client() {
		try {
			AWSCredentials credentials = new BasicAWSCredentials(PropertiesUtil.getPropertiesUtil().get("aws_access_key_id"), PropertiesUtil.getPropertiesUtil().get("aws_secret_access_key"));
			ClientConfiguration clientConfig = new ClientConfiguration();

			clientConfig.setProtocol(Protocol.HTTP);
			
			s3 = new AmazonS3Client(credentials,clientConfig);
			s3.setEndpoint(PropertiesUtil.getPropertiesUtil().get("aws_endpoint"));
//			 Region usWest2 = Region.getRegion(Regions.CN_NORTHWEST_1);
//		        s3.setRegion(usWest2);
			tx = TransferManagerBuilder.standard().withS3Client(s3).build();
		} catch (Exception e) {
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
	public static synchronized MinioAWSS3Client getMinioAWSS3Client() {
		if (aWSS3Client == null) {    
			aWSS3Client = new MinioAWSS3Client();  
        }    
       return aWSS3Client;  
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
		if(!s3.doesBucketExistV2(bucketName)) {
			s3.createBucket(bucketName);
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
	    	  s3.setBucketPolicy(bucketName, builder.toString());
			logger.info("bucket【"+bucketName+"】创建成功");
		}
	}
	
	/**
	 * getBucket:(获取bucket 没有返回null)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		上午11:58:11
	 * @param bucketName
	 * @return   
	 * @return Bucket    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	public static Bucket getBucket(String bucketName) {
		List<Bucket> buckets = s3.listBuckets();
		if(buckets != null) {
			for(Bucket b : buckets) {
				if(b.getName().equals(bucketName)) 
					return b;
			}
		}
		return null;
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
//	public static void putObject(String bucketName,String key,File file,boolean isPublic) {
//		createBucket(bucketName);
//		 StringBuilder builder = new StringBuilder();
//		 //to minio  policy   must set policy   acl 不行
//		  builder.append("{\n");
//		  builder.append("    \"Statement\": [\n");
//		  builder.append("        {\n");
//		  builder.append("            \"Action\": [\n");
//		  builder.append("                \"s3:GetBucketLocation\",\n");
//		  builder.append("                \"s3:ListBucket\"\n");
//		  builder.append("            ],\n");
//		  builder.append("            \"Effect\": \"Allow\",\n");
//		  builder.append("            \"Principal\": \"*\",\n");
//		  builder.append("            \"Resource\": \"arn:aws:s3:::"+bucketName+"\"\n");
//		  builder.append("        },\n");
//		  builder.append("        {\n");
//		  builder.append("            \"Action\": \"s3:GetObject\",\n");
//		  builder.append("            \"Effect\": \"Allow\",\n");
//		  builder.append("            \"Principal\": \"*\",\n");
//		  builder.append("            \"Resource\": \"arn:aws:s3:::"+bucketName+"/*\"\n");
//		  builder.append("        }\n");
//		  builder.append("    ],\n");
//		  builder.append("    \"Version\": \"2012-10-17\"\n");
//		  builder.append("}\n");
//		  
//		 s3.setBucketPolicy(bucketName, builder.toString());
//		if(s3.doesObjectExist(bucketName, key)) {
//			logger.info("bucket【"+bucketName+"】+object【"+key+"】已经存在");
//		}
////		 else {
////			PutObjectRequest request = new PutObjectRequest(bucketName, key, file);
////			if(isPublic)
////				request.setCannedAcl(CannedAccessControlList.PublicRead);
////			PutObjectResult putObject = s3.putObject(bucketName, key, file);
////			System.out.println(putObject);
////			
////		}
//		
//		
//		else {
//			//--使用流
//			try {
//				FileInputStream input = new FileInputStream(file);
//				ByteArrayInputStream byteArrayInputStream = null;
//				ByteArrayOutputStream output = new ByteArrayOutputStream();
//				byte[] buffer = new byte[4096];
//				byte[] bytes = new byte[0];
//				int n=0;
//				
//				try {
////					bytes = IOUtils.toByteArray(input);
//					
//					while(-1!=(n = input.read(buffer))) {
//						output.write(buffer, 0, n);
//					}
//					bytes =output.toByteArray();
//					
//					logger.info("bytes.length="+bytes.length+";  "+input.available());
//					ObjectMetadata metaData = new ObjectMetadata();
//					metaData.setContentLength(bytes.length);
//					 byteArrayInputStream = new ByteArrayInputStream(bytes);
//					PutObjectRequest request = new PutObjectRequest(bucketName, key, byteArrayInputStream, metaData);
//					if(isPublic)
//						request.setCannedAcl(CannedAccessControlList.PublicRead);
//					PutObjectResult putObject = s3.putObject(bucketName, key, file);
//					logger.info("￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥"+putObject.toString());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}finally {
//					if(byteArrayInputStream!=null) {
//						byteArrayInputStream.close();
//					}
//					if(input!=null) {
//						input.close();
//					}
//					
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
	
	public static void putObject(String bucketName,String key,File file,boolean isPublic) {
		createBucket(bucketName);
		 StringBuilder builder = new StringBuilder();
		 //to minio  policy   must set policy   acl 不行
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
		  
		 s3.setBucketPolicy(bucketName, builder.toString());
		if(s3.doesObjectExist(bucketName, key)) {
			logger.info("bucket【"+bucketName+"】+object【"+key+"】已经存在");
		}
		 else {
			PutObjectRequest request = new PutObjectRequest(bucketName, key, file);
			if(isPublic)
				request.setCannedAcl(CannedAccessControlList.PublicRead);
			PutObjectResult putObject = s3.putObject(bucketName, key, file);
			System.out.println(putObject);
		}
	}
	
	/**
	 * putObjectWithTransferManager:(使用TransferManager上传文件，可获取传输进度，并且能够暂停或恢复上传和下载,单个)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		下午3:51:00
	 * @param bucketName
	 * @param key
	 * @param file
	 * @param isPublic
	 * @return   
	 * @return Upload    
	 * @throws FileNotFoundException 
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	@SuppressWarnings("resource")
	public static Upload putObjectWithTransferManager(String bucketName,String key,File file,boolean isPublic)  {
		createBucket(bucketName);
		Upload up = null;
		if(s3.doesObjectExist(bucketName, key)) {
			logger.info("bucket【"+bucketName+"】+object【"+key+"】已经存在");
		}else {
//			PutObjectRequest request = new PutObjectRequest(bucketName, key, file);
//			if(isPublic)
//				request.setCannedAcl(CannedAccessControlList.PublicRead);
//			up = tx.upload(request);
			
			try {
				FileInputStream input = new FileInputStream(file);
				byte[] bytes;
				try {
					bytes = IOUtils.toByteArray(input);
					logger.info("bytes.length------chunk="+bytes.length+";  "+input.available());
					ObjectMetadata metaData = new ObjectMetadata();
					metaData.setContentLength(bytes.length);
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
					PutObjectRequest request = new PutObjectRequest(bucketName, key, byteArrayInputStream, metaData);
					if(isPublic)
						request.setCannedAcl(CannedAccessControlList.PublicRead);
					up = tx.upload(request);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			logger.debug("bucketName【"+bucketName+"】-objectKey【"+key+"】开始上传对象存储！");
			try {
				up.waitForCompletion();
				logger.debug("bucketName【"+bucketName+"】-objectKey【"+key+"】上传对象存储成功！");
			} catch (AmazonServiceException e1) {
				e1.printStackTrace();
				logger.error("bucketName【"+bucketName+"】-objectKey【"+key+"】上传对象存储失败，原因【"+e1.getMessage()+"】！");
			} catch (AmazonClientException e1) {
				e1.printStackTrace();
				logger.error("bucketName【"+bucketName+"】-objectKey【"+key+"】上传对象存储失败，原因【"+e1.getMessage()+"】！");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				logger.error("bucketName【"+bucketName+"】-objectKey【"+key+"】上传对象存储失败，原因【"+e1.getMessage()+"】！");
			}
		}
		
		return up;
	}
	
	/**
	 * getObjects:(获取objects)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		下午2:40:26
	 * @param bucketName   
	 * @return void    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	@SuppressWarnings("deprecation")
	public static List<S3ObjectSummary> getObjects(String bucketName) {
		List<S3ObjectSummary> objects = new ArrayList<S3ObjectSummary>();
		if(s3.doesBucketExist(bucketName)) {
			ListObjectsV2Result lov2s = s3.listObjectsV2(bucketName);
			objects =  lov2s.getObjectSummaries();
			for (S3ObjectSummary os: objects) {  
	            //调用其 getKey 方法以检索对象名称  
	            logger.info("*对象名称： " + os.getKey());
	        }  
		}
		return objects;
	}
	
	/**
	 * getObject:(根据s3获取object)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		下午5:23:39
	 * @param bucketName
	 * @param objectKey
	 * @return   
	 * @return S3Object    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	@SuppressWarnings("deprecation")
	public static S3Object getObject(String bucketName,String objectKey) {
		S3Object s3o = null;
		if(s3.doesBucketExist(bucketName)) {
			s3o = s3.getObject(bucketName, objectKey);
		}
		return s3o;
	}
	
	/**
	 * deleteObject:(删除单个object)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		下午5:05:35
	 * @param bucketName
	 * @param objectKey   
	 * @return void    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	@SuppressWarnings("deprecation")
	public static boolean deleteObject(String bucketName,String objectKey) {
		boolean flag = true;
		try {
			if(s3.doesBucketExist(bucketName)) {
				if(s3.doesObjectExist(bucketName, objectKey)) {
					s3.deleteObject(bucketName,objectKey);
				}else {
					logger.info("objectKey【"+objectKey+"】不存在！");
				}
			}else {
				logger.info("bucketName【"+bucketName+"】不存在！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
	/**
	 * deleteBucket:(删除bucket)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		下午5:16:31
	 * @param bucketName   
	 * @return void    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	@SuppressWarnings("deprecation")
	public static void deleteBucket(String bucketName) {
		if(s3.doesBucketExist(bucketName)) {
			s3.deleteBucket(bucketName);
		}
	}
	
	/**
	 * getPublicURL:(获取	公有链接)
	 *
	 * @author   meishen
	 * @Date	 2018	2018年6月17日		下午3:30:01
	 * @param bucketName 
	 * @param key 该文件在bucket中的文件名 
	 * @param expirationDate 设置过期时间 无过期时间传null 
	 * @return   
	 * @return String    
	 * @throws 
	 * @since  Videoworks　Ver 1.1
	 */
	public static String getPublicURL(String bucketName,String key, Date expirationDate){  
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, key);    
        if (null != expirationDate) 
        	urlRequest.setExpiration(expirationDate);  
        return s3.generatePresignedUrl(urlRequest).toString();  
    }  
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		ParameterMap.getParameterMap("D:\\gearman.properties"); 
//		MinioAWSS3Client.getMinioAWSS3Client().putObject("videoworks-g", "key10", new File("D:\\video_96.ts"), true);
//		MinioAWSS3Client.getMinioAWSS3Client().getObjects("0000000test10");
//		MinioAWSS3Client.getMinioAWSS3Client().putObjectWithTransferManager("videoworks-13","keywhl",new File("D:\\1.jpg"),true);
		MinioAWSS3Client.getMinioAWSS3Client().putObject("videoworks-13","keywhl7",new File("D:\\1.jpg"),true);
//		MinioAWSS3Client.getMinioAWSS3Client().getObjects("videoworks-j");
	}
}
