package cn.videoworks.worker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.dto.YouKuDto;

public class YouKuUtil {
	private static final Logger log = LoggerFactory.getLogger(YouKuUtil.class);
	private static String ip = null;// ip
	private static String client_id = "";// 客户端id
	private static String clientSecret = "";// 客户秘钥
	private static String refreshToken = "";// 刷新token值
//	private static String httpUrl = "http://175.25.49.208:82/home/vwfs01/YOUKU";// 地址
	private static String access_token = "";
	private static String upload_token = "";
//	private static String uploadServer = "";
//	private static String uploadVid = "";
	private static String slice_task_id ="";
	private static String slice_length ="";
	private static String transferred ="";
	private static boolean finished = false;
	private static String slice_offset = "";
	private static String uid = "";
	private static String filePath = "";
	private static String file_size = "";
	private static String file_name = "";
	private static String file_ext = "";
	private static String file_md5 = "";
	private static String upload_server_ip = "";
//	private static Map<String,Object> dataFile = null;
	/**
	 * 初始化参数.
	 * 
	 */
	/**
	 * 初始化参数.
	 * 
	 */
	public static void initParam() {
		try {
			//ip = PropertiesUtil.getPropertiesUtil().get("v2.ip");
			client_id = PropertiesUtil.getPropertiesUtil().get("v2.client_id");
			clientSecret = PropertiesUtil.getPropertiesUtil().get("v2.client_secret");
			refreshToken = PropertiesUtil.getPropertiesUtil().get("v2.refresh_token");
			log.info("初始化数据:ip = " + ip + "client_id = " + client_id + "clientSecret= " + clientSecret + "refreshToken=" + refreshToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 刷新asscessToken.
	 */
	public static String refreshToken() {
		try {
			log.info("normal start refreshAccessToken...");
			// 设置参数
			Map<String, Object> allParams = new HashMap<>();
			allParams.put("client_id",client_id);// 客户id
			allParams.put("client_secret",clientSecret);// 秘钥
			allParams.put("grant_type","refresh_token");// 操作类型
			allParams.put("refresh_token",refreshToken);//刷新token
			String yUrl = "https://openapi.youku.com/v2/oauth2/token";//
			String r = HttpUtil.httpPost(yUrl, allParams);
			log.info("刷新asscessToken 结果:"+r);// 返回结果实例:{"access_token":"e22a2b3dffc1cf412122edc5dd9affa9","expires_in":2592000,"refresh_token":"2580985833605de3aebd96ce143179ce","token_type":"bearer"}
			boolean pass = check_error(r,"200");
			if (pass == false) {
				log.info("刷新asscessToken 结果:"+r);
				return null;
			}
			Map<String, Object> result = JsonConverter.asMap(r, String.class, Object.class);
			if (result.containsKey("access_token")) {
				log.info("normal", "success...");
				return result.get("access_token").toString();
			}
		}
		 catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 验证结果.
	 * 
	 * @param result
	 * @param code
	 * @return
	 */
	public static boolean check_error(String result, String code) {
		boolean pass = true;
		if (result != null) {
			if (result.contains("error")) {
				pass = false;
				return pass;
			}
//			Map<String, Object> m = JsonConverter.asMap(result, String.class, Object.class);
//			if (m.containsKey("status_code")) {
//				if (m.get("status_code").toString().equals(code)) {
//					pass = true;
//				}
//			}
		}
		return pass;
	}
	/**
	 * 初始化数据.
	 * 
	 * @param token
	 * @param filePath
	 * @param fileSize
	 */
	public static void initial(String token, String path, String fileSize) {
		access_token = token;
		uid = "rgjrkthbgd@163.com";
		filePath = path;
		file_size = fileSize;
		File file = new File(path);
		file_name = file.getName();
		if (file_name != null) {
			file_ext = file_name.substring(file_name.lastIndexOf(".") + 1);
		}
		file_md5 = null;
		upload_token = null;
		upload_server_ip = null;
		slice_task_id = null;
		slice_offset = null;
		slice_length = null;
		transferred = null;
		finished = false;
	}
	private static void _read_upload_state_from_file(){
		String save_file = filePath +".upload";
	}

	/**
	 * 数据上传.
	 * 
	 * @param params
	 * @param dto
	 */
	public static String upload(Map<String, String> params, YouKuDto dto) {
		log.info("数据上传 start");
		if (upload_token == null || upload_token.equals("")) {
			try {
				create(prepare_video_params(params, dto),dto);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		String ret = upload_slice_new();
		if(ret!=null){
			try {
				return commit(ret);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return null;
	}

	/**
	 * 上传分片.
	 * 
	 * @param params
	 * @param dto
	 * @return
	 * @throws InterruptedException
	 */
	public static String upload_slice_new() {
		log.info("开始上传分片");
		String ip = upload_server_ip;
		String token = upload_token;
		Integer MAX_SLICE_SIZE = 10485760;
		String fileSize = file_size;
		Integer last_block = 1;
		if (fileSize != null && !fileSize.equals("")) {
			if (Integer.valueOf(fileSize) % MAX_SLICE_SIZE == 0) {
				last_block = 0;
			}
		}
		Integer block_number = Integer.valueOf(fileSize)/MAX_SLICE_SIZE + last_block;
		Integer block_index = 0;
		File file = new File(filePath);
		Integer read_size = 0;
		String result = null;
		try {
			RandomAccessFile in = new RandomAccessFile(file,"r");
			//ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
			while (block_index < block_number) {
				slice_offset = String.valueOf(block_index * MAX_SLICE_SIZE);
				byte[] buff = new byte[MAX_SLICE_SIZE];
				if (block_index == block_number - 1) {
					read_size = Integer.valueOf(file_size) - MAX_SLICE_SIZE * (block_number - 1);
				} else {
					read_size = Integer.valueOf(MAX_SLICE_SIZE);
				}
				in.seek(MAX_SLICE_SIZE);
				in.read(buff, 0, read_size.intValue());
				block_index++;
				Integer.parseInt(slice_offset);
				Integer offset = Integer.valueOf(slice_offset);
				String contentRange = "bytes " + (((offset) + 1) - (offset + read_size) / (Integer.valueOf(fileSize)));//(((int)(Integer.valueOf(slice_offset)+1))-(Integer.valueOf(slice_offset) + read_size)) / Integer.valueOf(fileSize)).toString();
				result = upload_file(contentRange,buff,ip,token);
				//buff =  swapStream.
			}
			in.close();
			log.info("上传分片结果:" + result);
			if (result == null || !result.contains("upload_server_name")) {
				return null;
			}
			Map<String, Object> re = JsonConverter.asMap(result, String.class, Object.class);
			if (re.containsKey("upload_server_name")) {
				return re.get("upload_server_name").toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 上传文件.
	 * 
	 * @param contentRange
	 * @param buff
	 * @param ip2
	 * @param token
	 * @return
	 */
	private static String upload_file(String contentRange, byte[] buff, String ip2, String token) {
		log.info("开始上传文件");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Range", contentRange);
		String url = "http://" + ip2 + "/api/upload/?upload_token=" + token;
		int times = 0;
		while (times < 100) {
			try {
				String result = HttpUtil.httpPut(url, buff,"UTF-8", headers);
				log.info("上传文件返回结果:" + result);
				return result;
			} catch (Exception e) {
				 times += 1;
				e.printStackTrace();
			}
		}

		return null;
	}
	/**
	 * 创建文件.
	 * @param params
	 * @param dto
	 * @return
	 * @throws InterruptedException
	 */
			
	private static String create(Map<String, String> params,YouKuDto dto) throws InterruptedException {
		log.info("创建文件 start");
		params.put("file_name", file_name);
		params.put("file_size", file_size);
		params.put("file_md5", checksum_md5file(filePath));
		log.info("normal file_md5");
		params.put("client_id", client_id);
		log.info("normal client_id:"+client_id);
		params.put("access_token", access_token);
		log.info("normal accessToken");
		if (dto.getUserid() != null) {
			params.put("transfer_uid", dto.getUserid());
		} else {
			params.put("transfer_uid", "856478058");
		}
		String isBlock = null;
		if (dto.getIsBlock() != null) {
			isBlock = dto.getIsBlock();
		}
		if (isBlock != null) {
			params.put("auto_block", isBlock);
		} else {
			params.put("auto_block", "1");
		}
		log.info("创建文件参数  normal", JsonConverter.format(params));
		String url = "https://openapi.youku.com/v2/uploads/create.json";
		Map<String,Object>result = null;
		int times = 0;
		while (true) {
			try {
				String r = HttpUtil.sendGet(url, params);
				log.info("创建文件 返回结果:" + r);// 结果实例:{"video_id":"XMzk5NDkxNDQwNA==","upload_token":"NTU0ODk5MzM3XzAxMDA2NDNBQTI1QzJEQkM3OTFBNDUyRjhGMTE5OUFDM0U2MERCLTU4MEItNUQ1OC1DMEJDLUIzOEQ0MkM3NjIzM18xXzhlZDM2MWNjMWQ5YmI3OTEyMTNhYzZhMTlhOTFhMTE1","upload_server_uri":"106.14.99.65","instant_upload_ok":"no"}
				boolean pass = check_error(r, "201");
				if(pass == false) {
					log.info("创建文件 返回结果:" + r);
					return null;
				}
				result = JsonConverter.asMap(r, String.class, Object.class);
				break;
			} catch (Exception e) {
				times += 1;
				if (times > 10) {
					break;
				}
				Thread.sleep(Long.valueOf(30));
				e.printStackTrace();
			}
		}
		if (result != null) {
			if (result.containsKey("upload_token")) {
				upload_token = result.get("upload_token").toString();
			}
			if (result.containsKey("upload_server_uri")) {
				upload_server_ip = result.get("upload_server_uri").toString();
			}
			if(result.containsKey("instant_upload_ok")){
				if(result.get("instant_upload_ok").equals("yes")){
					return commit(null);
				}
			}
//			if (ip != null) {
//				upload_server_ip = ip;
//			} else {
//				if (result.containsKey("upload_server_uri")) {
//					upload_server_ip = result.get("upload_server_uri").toString();
//				}
//			}
		}
		return null;	
	}

	/**
	 * 提交文件.
	 * 
	 * @return
	 * @throws InterruptedException 
	 */
	private static String commit(String upload_server_name) throws InterruptedException {
		log.info("提交文件开始");
		Map<String,Object>params = new HashMap<>();
		// 设置参数
		params.put("access_token", access_token);
		params.put("client_id", client_id);
		params.put("upload_token", upload_token);
		params.put("upload_server_ip", upload_server_name);
		String url = "https://openapi.youku.com/v2/uploads/commit.json";
		int times = 0;
		while (true) {
			try {
				String r = HttpUtil.httpPost(url, params);// 结果实例:{"video_id":"XMzk5NDk0MjM5Mg=="}
				log.info("提交文件 返回结果" + r);
				boolean pass = check_error(r, "200");
				if (pass == false) {
					return null;
				}
				Map<String,Object>result = JsonConverter.asMap(r, String.class,Object.class);
				if (result != null) {
					if (result.containsKey("video_id")) {
						return result.get("video_id").toString();
					}
					return null;
				} else {
					return null;
				}
			} catch (Exception e) {
				times += 1;
				Thread.sleep(Long.valueOf(30));
				if(times>10){
					return null;
				}
			}
			
		}
	}

	public static String checksum_md5file(String path) {
		String bi = null;
		try {
			byte[] buffer = new byte[8192];
			int len = 0;
			MessageDigest md = MessageDigest.getInstance("MD5");
			File f = new File(path);
			FileInputStream fis = new FileInputStream(f);
			while ((len = fis.read(buffer)) != -1) {
				md.update(buffer, 0, len);
			}
			fis.close();
			byte[] b = md.digest();
			bi = encodeHex(b);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bi;
	}
	private static String encodeHex(byte[] data) {
		if (data == null) {
			return null;
		}
		  StringBuffer sb = new StringBuffer();  
          int length = data.length;
          for (int i = 0; i < length; i++) {  
              String hex = Integer.toHexString(data[i]&0xFF);  
              if (hex.length() == 1) {  
                  hex = '0' + hex;  
              }  
              sb.append(hex); 
          }  
          return sb.toString();  
	}
	private static Map<String, String> prepare_video_params(Map<String, String> params,YouKuDto dto) {
		//params.put("title", null);
		params.put("tags", "Others");
		params.put("description", "");
		params.put("copyright_type", "original");
		params.put("public_type", "all");
		//params.put("category", null);
		//params.put("watch_password", null);
		//params.put("latitude", null);
		//params.put("longitude", null);
		//params.put("shoot_time", null);
		String title = "";
		String newTitle = "";
		if (params.get("title") == null) {
			title = filePath;
		} else {
			newTitle = params.get("title");
			if (newTitle.length() > 80) {
				newTitle = newTitle.substring(0, 80);
				title = newTitle;
			}
		}
		params.put("title", newTitle);
		String classification = null;
		if(dto.getClassification()!=null){
			classification = dto.getClassification();
		}
		if (classification != null) {
			params.put("category", classification);
		}
		return params;
	}
	
}
