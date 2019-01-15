package cn.videoworks.worker.function;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.client.BaiDuBosClient;
import cn.videoworks.worker.constant.ResponseDictionary;
import cn.videoworks.worker.dto.AwsStorageDto;
import cn.videoworks.worker.util.FileUtil;
import cn.videoworks.worker.util.PropertiesUtil;

/**
 * 注入到百度云的对象存储
 * 
 * @author whl
 *
 */
public class WriteBosStorage implements GearmanFunction {
	private static final Logger log = LoggerFactory.getLogger(WriteBosStorage.class);

	@Override
	public byte[] work(String arg0, byte[] data, GearmanFunctionCallback arg2) throws Exception {
		ArrayList<AwsStorageDto> returnDtos = new ArrayList<AwsStorageDto>();
		String aws_endpoint;
		try {
			aws_endpoint = PropertiesUtil.getPropertiesUtil().get("aws_endpoint");
			String string = new String(data, "UTF-8");
			log.info("写入对象存储Work，收到参数：" + string);
			List<AwsStorageDto> asList = JsonConverter.asList(string, AwsStorageDto.class);

			for (AwsStorageDto awsStorageDto : asList) {
				// 获取校验码
				String check_sum = awsStorageDto.getCheck_sum();
				String id = awsStorageDto.getId();
				String url = awsStorageDto.getUrl();
				Integer type = awsStorageDto.getType();

				String bucketName = "";

				if (check_sum != null) {
					bucketName = "videoworks-" + check_sum.substring(check_sum.length() - 1);
				}
				File file = new File(url);

				String suffix = url.substring(url.lastIndexOf("."));// 腾讯云必需知道文件后缀,此处得到.mp4或者.ts等，包含"."
				check_sum = check_sum + suffix;

				BaiDuBosClient.getBaiDuBosClient().putObject(bucketName, check_sum, file);
				aws_endpoint = PropertiesUtil.getPropertiesUtil().get("BD_ENDPOINT");
				// 删除ftp文件
				 FileUtil.deleteFile(url);
				AwsStorageDto returnDto = new AwsStorageDto();
				returnDto.setId(id);
				returnDto.setType(type);
				if (aws_endpoint.endsWith("/"))
					returnDto.setUrl(aws_endpoint + bucketName + "/" + check_sum);
				else
					returnDto.setUrl(aws_endpoint + "/" + bucketName + "/" + check_sum);
				returnDtos.add(returnDto);
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
		return buildResponse(ResponseDictionary.SUCCESS, "注入成功", returnDtos);
	}

	public byte[] buildResponse(Integer statusCode, String message, List<AwsStorageDto> returnDtos) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("statusCode", statusCode);
		response.put("message", message);
		response.put("data", returnDtos);
		String returnStr = JsonConverter.format(response);
		byte[] bytes = null;
		try {
			bytes = returnStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		}
		return bytes;
	}

}
