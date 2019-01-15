package cn.videoworks.worker.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.TransferManager;

import cn.videoworks.worker.client.MinioAWSS3Client;

public class TestCWs3Main {
private static AmazonS3 s3 = null;
	
	private static TransferManager tx = null;
	
	private static MinioAWSS3Client aWSS3Client = null;
	
	private static Logger logger = LoggerFactory.getLogger(MinioAWSS3Client.class);
	
	@SuppressWarnings({ "static-access", "deprecation" })
	public static void main(String[] args) {
		
		String endpoint = args[0];
		String acess_key= args[1];
		String secret_key = args[2];
		String filePath = args[3];
		String key =args[4];
		logger.info("要上传的文件路径="+filePath);
		AWSCredentials credentials = new BasicAWSCredentials(acess_key,secret_key);
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTP);
		s3 = new AmazonS3Client(credentials,clientConfig);
		s3.setEndpoint(endpoint);
		File file = new File(filePath);
		putObject("videoworks-0", key, file, true);
	}
	
	
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
//		 else {
//			PutObjectRequest request = new PutObjectRequest(bucketName, key, file);
//			if(isPublic)
//				request.setCannedAcl(CannedAccessControlList.PublicRead);
//			PutObjectResult putObject = s3.putObject(bucketName, key, file);
//			System.out.println(putObject);
//			
//		}
		
		
		else {
			//--使用流
			try {
				FileInputStream input = new FileInputStream(file);
				ByteArrayInputStream byteArrayInputStream = null;
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				byte[] bytes = new byte[0];
				int n=0;
				
				try {
//					bytes = IOUtils.toByteArray(input);
					while(-1!=(n = input.read(buffer))) {
						output.write(buffer, 0, n);
					}
					bytes =output.toByteArray();
					
					logger.info("bytes.length="+bytes.length+";  "+input.available());
					ObjectMetadata metaData = new ObjectMetadata();
					metaData.setContentLength(bytes.length);
					 byteArrayInputStream = new ByteArrayInputStream(bytes);
					PutObjectRequest request = new PutObjectRequest(bucketName, key, byteArrayInputStream, metaData);
					if(isPublic)
						request.setCannedAcl(CannedAccessControlList.PublicRead);
					PutObjectResult putObject = s3.putObject(bucketName, key, file);
					logger.info("￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥"+putObject.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					if(byteArrayInputStream!=null) {
						byteArrayInputStream.close();
					}
					if(input!=null) {
						input.close();
					}
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
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

}
