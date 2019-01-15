package cn.videoworks.worker.function;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.client.MD5GearmanClient;
import cn.videoworks.worker.client.MinioAWSS3Client;
import cn.videoworks.worker.client.MinioSDKClient;
import cn.videoworks.worker.constant.ResponseDictionary;
import cn.videoworks.worker.dto.AwsStorageDto;
import cn.videoworks.worker.util.FileUtil;
import cn.videoworks.worker.util.Md5Util;
import cn.videoworks.worker.util.PropertiesUtil;
import cn.videoworks.worker.util.ResponseStatusCode;

/**
 * 注入到AWSS3存储
 * 
 * @author whl
 *
 */
public class WriteMinioAWS3Storage implements GearmanFunction {

	public WriteMinioAWS3Storage() {

	}


	private static final Logger log = LoggerFactory.getLogger(WriteMinioAWS3Storage.class);

	@SuppressWarnings("static-access")
	@Override
	public byte[] work(String arg0, byte[] data, GearmanFunctionCallback callback) {
		ArrayList<AwsStorageDto> returnDtos = new ArrayList<AwsStorageDto>();
		String aws_endpoint;
		try {
			aws_endpoint = PropertiesUtil.getPropertiesUtil().get("aws_endpoint");
			String string = new String(data, "UTF-8");
			log.info("写入对象存储Work，收到参数：" + string);
			List<AwsStorageDto> asList = JsonConverter.asList(string, AwsStorageDto.class);

			for (AwsStorageDto awsStorageDto : asList) {
				// 获取校验码
			
				String id = awsStorageDto.getId();
				String url = awsStorageDto.getUrl();
				Integer type = awsStorageDto.getType();
				
				String midia_asset_id = awsStorageDto.getMediaAssetId();
				
				
				AwsStorageDto returnDto = new AwsStorageDto();
				returnDto.setId(id);
				returnDto.setType(type);
				returnDto.setMediaAssetId(midia_asset_id);
				boolean exists = FileUtil.exists(url);
				
				if(exists) {
					String bucketName = "";
					File file = new File(url);
					//从worker中获取checks-sum
					String check_sum ="";
					String host = PropertiesUtil.getPropertiesUtil().get("gearman.ip");
					String port = PropertiesUtil.getPropertiesUtil().get("gearman.port");
					MD5GearmanClient cdnGearmanClient = MD5GearmanClient.getCDNGearmanClient(host, Integer.valueOf(port), "md5sum");
					HashMap<String, String> hashMap = new HashMap<String,String>();
					hashMap.put("filepath", url);
					String format = JsonConverter.format(hashMap);
					Map<String, Object> submitJob = cdnGearmanClient.submitMd5Job(format,"md5sum");
					Integer statusCode = Integer.valueOf(String.valueOf(submitJob.get("statusCode")));
					if(statusCode!=ResponseStatusCode.OK) {
						byte[] source = FileUtil.getByte(file);
						check_sum = Md5Util.getMD5(source);
					}else {
						check_sum = String.valueOf(submitJob.get("check_sum"));
					}
					bucketName = "videoworks-" + check_sum.substring(check_sum.length() - 1);
				
					String suffix = url.substring(url.lastIndexOf("."));
					check_sum = check_sum + suffix;
//					AWSS3Client.getAmazonS3Client().putObject(bucketName, check_sum, file, true);  //使用传统的awss3
//					MinioSDKClient.getMinioSDKClient().putObject(bucketName, check_sum, file, true);//,使用minio的SDK ,速度慢
					MinioAWSS3Client.getMinioAWSS3Client().putObject(bucketName, check_sum, file, true); //分段上传,修改权限json
					if (aws_endpoint.endsWith("/"))
						returnDto.setUrl(aws_endpoint + bucketName + "/" + check_sum);
					else
						returnDto.setUrl(aws_endpoint + "/" + bucketName + "/" + check_sum);
					returnDtos.add(returnDto);
				}else {
					returnDto.setUrl(url);
					returnDtos.add(returnDto);
					return buildResponse(ResponseDictionary.EXTERNALINTERFACECALLSEXCEPTION, "注入失败:" +url+"不存在",
							returnDtos);
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			log.error("存储注入失败：" + e1.getMessage());
			return buildResponse(ResponseDictionary.EXTERNALINTERFACECALLSEXCEPTION, "注入失败:" + e1.getMessage(),
					returnDtos);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("存储注入失败：" + e.getMessage());
			return buildResponse(ResponseDictionary.EXTERNALINTERFACECALLSEXCEPTION, "注入失败:" + e.getMessage(),
					returnDtos);
		}
		log.info("存储注入成功：" );
		return buildResponse(ResponseDictionary.SUCCESS, "注入成功", returnDtos);
	}

	public byte[] buildResponse(Integer statusCode, String message, List<AwsStorageDto> returnDtos) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("statusCode", statusCode);
		response.put("message", message);
		response.put("data", returnDtos);
		String returnStr = JsonConverter.format(response);
		log.info("返回的结果是="+returnStr);
		byte[] bytes = null;
		try {
			bytes = returnStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		}
		return bytes;
	}
}
