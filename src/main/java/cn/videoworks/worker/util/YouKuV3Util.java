package cn.videoworks.worker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qcloud.Utilities.MD5;

import cn.videoworks.commons.util.json.JsonConverter;
import cn.videoworks.worker.dto.YouKuV3Dto;

public class YouKuV3Util {
	private static final Logger log = LoggerFactory.getLogger(YouKuV3Util.class);
	//private static final String ip = "175.25.49.208";// ip
	private static String ip = "";// 测试ip
	private static String clientId = "";// 客户端id
	private static String clientSecret = "";// 客户秘钥
	private static String refreshToken = "";// 刷新token值
	//private static final String httpUrl = "http://175.25.49.208:82/home/vwfs01/YOUKU";// 地址
	private static String accessToken = "";
	private static String uploadToken = "";
	private static String uploadServer = "";
	private static String uploadVid = "";
	private static String slice_task_id ="";
	private static String slice_length ="";
	private static String transferred ="";
	private static String finished ="";
	private static String slice_offset = "";
	private static Map<String,Object> dataFile = null;

	/**
	 * 初始化参数.
	 * 
	 */
	public static void initParam() {
		try {
			ip = PropertiesUtil.getPropertiesUtil().get("client_ip");
			clientId = PropertiesUtil.getPropertiesUtil().get("clientId");
			clientSecret = PropertiesUtil.getPropertiesUtil().get("clientSecret");
			refreshToken = PropertiesUtil.getPropertiesUtil().get("refreshToken");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 刷新asscessToken.
	 */
	public static String refreshAccessToken() {
		try {
			log.info("normal start refreshAccessToken...");
			// 设置参数
			Map<String, Object> allParams = new HashMap<>();
			allParams.put("version", "3.0");// 版本
			allParams.put("action", "youku.user.authorize.token.refresh");// 版本
			allParams.put("client_id",clientId);// 客户id
			allParams.put("format","json");//数据格式
			allParams.put("refreshToken",refreshToken);//刷新token
			String tailUrl = getSignAndUrlParam(allParams);
			String yUrl = "https://openapi.youku.com/router/rest.json";
			Map<String,Object> pa = null;
			if(tailUrl!=null){
				pa = JsonConverter.asMap(tailUrl, String.class, Object.class);
			}
			String r = HttpUtil.httpPost(yUrl, pa);
			log.info("刷新asscessToken返回结果:" + r);
			Map<String, Object> result = JsonConverter.asMap(r, String.class, Object.class);
			if (result.containsKey("errno")) {
				if (Integer.valueOf(result.get("errno").toString()) == 0) {
					log.info("normal", "success...");
					if (result.containsKey("token")) {
						Object token = result.get("token");
						if (token != null) {
							String t = JsonConverter.format(token);
							Map<String, Object> tokenParm = JsonConverter.asMap(t, String.class, Object.class);
							if (tokenParm != null && tokenParm.containsKey("accessToken")) {
								accessToken = tokenParm.get("accessToken").toString();
								log.info("最新AccessToken:"+tokenParm.get("accessToken").toString());
								return tokenParm.get("accessToken").toString();
							}else{
								return null;
							}
						}else{
							return null;
						}
					}else{
						return null;
					}
				}
			}
		}
		 catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 排序参数,生成签名.
	 * 
	 * @param allParams
	 * @return
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	private static String getSignAndUrlParam(Map<String, Object> allParams) {
		try {
			allParams.put("timestamp", String.valueOf(new Date().getTime() / 1000));
			List<String> allParamsKeyList = new ArrayList<>();
			Map<String, Object> opensysparamsDict = new HashMap<>();
			for (String allParamKey : allParams.keySet()) {
				allParamsKeyList.add(allParamKey);
			}
			TreeMap<String, Object> sortMap = new TreeMap<String, Object>(new MapKeyComparator());
			sortMap.putAll(allParams);
			//log.info("排序后的参数:" + JsonConverter.format(sortMap));
			TreeMap signMap = get_sign(sortMap, clientId, clientSecret);
			//log.info("返回sign的值:" + signMap.toString());
			String params = null;
			if (signMap != null) {
				 params = JsonConverter.format(signMap);// 原有
			}
			return params;
		} catch (Exception e) {
			log.info("error", "youku uploadV3 getSignAndUrlParam except, %s"
					+ e.getMessage());
			return null;
		}
	}
	
	 /**
     * @brief 签名
     *
     * @param params 需加密的参数，TreeMap保证参数按升序排序，
     *               非Java语言需要先按参数名进行排序，系统参数与业务参数相同的情况下，系统参数在前
     * @param appKey 代理层获取密钥
     * @param secret 加密密钥
     *
     * @return 返回请求openapi所需参数，
     *          1、GET请求，直接遍历Map，拼接k-v即可；
     *          2、POST请求，迭代Map，封装为NameValuePair即可
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static TreeMap get_sign(TreeMap<String,Object> params, String appKey, String secret) throws Exception {
        /**
         * 用于存放与业务参数名相同的系统参数
         */
        Map serviceDuplicatePairs = new TreeMap<>();
        if(params.get("client_id") != null) {
            serviceDuplicatePairs.put("client_id", appKey);
        } else {
            params.put("client_id", appKey);
        }
        if(params.get("timestamp") != null) {
            serviceDuplicatePairs.put("timestamp", System.currentTimeMillis() / 1000);
        } else {
            params.put("timestamp", System.currentTimeMillis() / 1000);
        }
        if(params.get("version") != null) {
            serviceDuplicatePairs.put("version",  "3.0");
        } else {
            params.put("version",  "3.0");
        }

        String signMethod = params.get("sign_method") == null ? null : params.get("sign_method").toString();
        if(signMethod == null || "".equals(signMethod)) {
            //signMethod = SignMethodEnum.MD5.getValue();
            params.put("sign_method", "md5");
        } else {
            serviceDuplicatePairs.put("sign_method", signMethod);
        }

        StringBuffer signString = new StringBuffer();
        try {
            /**
             * 生成签名字符串
             */
            for(Map.Entry entry : params.entrySet()) {
                /**
                 * 同名参数，系统参数置于业务参数前
                 */
                if(serviceDuplicatePairs.get(entry.getKey()) != null) {
                    signString.append(entry.getKey());
                    /**
                     * 对参数值进行URLEncode, 不同开发语言对特殊字符encode结果可能不同，
                     * 以Java URLEncoder结果为准
                     * Encode值可以参考http://tool.chinaz.com/tools/urlencode.aspx
                     */
                    signString.append(URLEncoder.encode(serviceDuplicatePairs.get(entry.getKey()).toString(), "UTF-8"));
                }
                signString.append(entry.getKey());
                signString.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new Exception(e.getMessage());
        }
//        System.out.println(signString.toString());
        String sign = "";
//        if(SignMethodEnum.isHmac(signMethod)){
//            try {
//                sign = hmacSign(secret, signMethod, signString.toString());
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (InvalidKeyException e) {
//                e.printStackTrace();
//            }
//        } else {
            signString.append(secret);
            try {
                System.out.println(signString.toString());
                sign = md5Sign(signString.toString());
            } catch(Exception e) {
                params.put("error", "加密失败");
                return params;
            }
       // }
        return packageRequestParams(params, serviceDuplicatePairs, appKey, sign);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static TreeMap packageRequestParams(TreeMap params, Map dupiicateParams, String appKey, String sign) {
        StringBuffer buffer = new StringBuffer();
        /**
         * 拼接系统参数
         */
        buffer.append("{");
        buffer.append("\"client_id\":");
        buffer.append("\"");
        buffer.append(appKey);
        buffer.append("\",");
        buffer.append("\"timestamp\":");
        buffer.append("\"");
        buffer.append(params.get("timestamp"));
        buffer.append("\",");
        buffer.append("\"version\":");
        buffer.append("\"3.0\",");
        buffer.append("\"sign_method\":");
        buffer.append("\"");
        buffer.append(params.get("sign_method"));
        buffer.append("\",");
        buffer.append("\"sign\":");
        buffer.append("\"");
        buffer.append(sign);
        buffer.append("\",");
        buffer.append("\"action\":");
        buffer.append("\"");
        buffer.append(params.get("action"));
        buffer.append("\"");

        String access_token = (String) params.get("access_token");
        if(access_token != null && !"".equals(access_token)) {
            buffer.append(",\"access_token\":");
            buffer.append("\"");
            buffer.append(access_token);
            buffer.append("\"");
            params.remove("access_token");
        }
        buffer.append("}");
        params.put("opensysparams", buffer.toString());

        /**
         * 将Map中的系统参数移出
         */
        if(dupiicateParams.get("client_id") == null) {
            params.remove("client_id");
        }
        if(dupiicateParams.get("timestamp") == null) {
            params.remove("timestamp");
        }
        if(dupiicateParams.get("version") == null) {
            params.remove("version");
        }
        if(dupiicateParams.get("sign_method") == null) {
            params.remove("sign_method");
        }
        if(dupiicateParams.get("action") == null) {
            params.remove("action");
        }

        return params;
    }

    private static String md5Sign(String signString)  {
        String sign = MD5.stringToMD5(signString);
        return sign;
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toLowerCase());
        }
        return sign.toString();
    }

	/**
	 * 创建任务.
	 * 
	 */
	public static String createTask(YouKuV3Dto dto, String fileSize) {
		try {
			log.info("normal start createTask...");
			Map<String, Object> allParams = new HashMap<String, Object>();
			allParams.put("version", "3.0");
			allParams.put("action", "youku.video.upload.create");
			allParams.put("client_id", clientId);
			allParams.put("access_token", accessToken);
			allParams.put("client_ip", ip);
			allParams.put("server_type", "gupload");
			File file = new File(dto.getFilePath());
			String fileName = file.getName();
			allParams.put("file_name", fileName);

			allParams.put("file_size", fileSize);
			allParams.put("file_md5",getMD5Three(dto.getFilePath()));
			//allParams.put("transfer_uid", "797905423");856478058
			allParams.put("transfer_uid", "856478058");
			if (dto.getUserid() != null) {
				allParams.put("transfer_uid", Integer.valueOf(dto.getUserid()));
			}
			String isBlock = null;
			if (dto.getIsBlock() != null) {
				isBlock = dto.getIsBlock();
			}
			if (isBlock != null) {
				allParams.put("auto_block", Integer.valueOf(isBlock));
			} else {
				allParams.put("auto_block", 1);
			}
			String tailUrl = getSignAndUrlParam(allParams);
			Map<String,Object> pa = null;
			if(tailUrl!=null){
				pa = JsonConverter.asMap(tailUrl, String.class, Object.class);
			}
			String yUrl = "https://openapi.youku.com/router/rest.json";
			String r = HttpUtil.httpPost(yUrl, pa);// 返回结果实例:{"cost":0.068,"data":[{"vid":"XMzk5NTAyODE5Mg==","upload_token":"NTU0OTI3NjgxXzAxMDA2NDNBQTI1QzJEQ0E3QzIyNDAyRjhGMTE5OUU5MkYzNzdDLUYxQzAtMEIzRS0zRURBLTJEQzM5Q0MzRjkzOF8xXzMwZmZkM2FiOWNjY2VjZDlkNGI4YTJjNjg0NmJjMTI0","upload_server":"106.14.99.65"}],"e":{"error_msg":"OK","error_code":1},"openapi":{"traceId":"0bade83615465048286348737e570a"}}
			log.info("创建任务  normal  result:" + r);
			Map<String, Object> result = JsonConverter.asMap(r, String.class,Object.class);
			if (result == null) {
				return null;
			}
			if (result.containsKey("e")) {
				String e = JsonConverter.format(result.get("e"));
				Map<String, Object> eRusult = JsonConverter.asMap(e, String.class, Object.class);
				if (eRusult.containsKey("error_code")) {
					if (eRusult.get("error_code").toString().equals("1")) {
						String data = JsonConverter.format(result.get("data"));
						List<Object> list = JsonConverter.asList(data, Object.class);
						if (list != null && list.size() > 0) {
							String dat = JsonConverter.format(list.get(0));
							Map<String, Object> datas = JsonConverter.asMap(dat, String.class, Object.class);
							if (datas.containsKey("upload_token")) {
								uploadToken = datas.get("upload_token").toString();
							}
							if (datas.containsKey("upload_server")) {
								uploadServer = datas.get("upload_server").toString();
							}
							if (datas.containsKey("vid")) {
								uploadVid = datas.get("vid").toString();
							}
							return list.get(0).toString();
						} else {
							return null;
						}
					} else {
						return null;
					}
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			log.info("error youku uploadV3 创建任务createTask except" + e.getMessage());
			return null;
		}
		return null;
	}

	public static String getMD5Three(String path) {
		BigInteger bi = null;
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
			bi = new BigInteger(1, b);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bi.toString(16);
	}
	
	/**
	 * 保存任务.
	 */
	public static void saveTask(YouKuV3Dto dto) {
		log.info("normal start 保存任务saveTask...");
		String title = "";
		if (dto.getClassificationA_int() != null && dto.getClassificationA_int().equals("2")) {
			title = dto.getColumn() + dto.getPlayyear() + dto.getPlaymonth() + dto.getPlaydate() + dto.getProperTitle() + " 高清";
			log.info("normal classification type is 2, title %s" + title);
		} else {
			title = dto.getProperTitle() + " 高清" + dto.getColumn() + dto.getPlayyear() + dto.getPlaymonth() + dto.getPlaydate();
			log.info("normal classification type isnot 2, title %s" + title);
		}

		String newTitle = title;
		if (newTitle.length() > 80) {
			newTitle = new String(newTitle.getBytes(), 0, 20);
		}
		title = newTitle;
		log.info("normal", title);
		String classification = null;
		if (dto.getClassification() != null) {
			classification = dto.getClassification();
		}
		Map<String, Object> allParams = new HashMap<String, Object>();
		Map<String, Object> movie_param = new HashMap<String, Object>();
		allParams.put("version", "3.0");
		allParams.put("action", "youku.video.upload.save");
		allParams.put("client_id", clientId);
		allParams.put("access_token", accessToken);
		allParams.put("transfer_uid", "856478058");
		if (dto.getUserid() != null) {
			allParams.put("transfer_uid", Integer.valueOf(dto.getUserid()));
		}
		allParams.put("upload_token", uploadToken);
		allParams.put("title", title);
		allParams.put("description", dto.getProperTitle());
		allParams.put("original", "original");
		allParams.put("privacy", "anybody");
		// 开始推送时间
		movie_param.put("push_starttime", DateUtil.format.format(new Date()));
		if (classification != null) {
			allParams.put("category_name", classification);
		}
		// 节目分类
		if (dto.getClassification() != null) {
			movie_param.put("showcategory", dto.getClassification());
		}
		allParams.put("tags", dto.getTags());
		// 播出开始时间
		if (dto.getVideostarttime() != null) {
			movie_param.put("tv_starttime", dto.getVideostarttime());
		}
		// 播出结束时间
		if (dto.getVideoendtime() != null) {
			movie_param.put("tv_endtime", dto.getVideoendtime());
		}
		// 节目id
		if (dto.getShow_id() != null) {
			movie_param.put("show_id", dto.getShow_id());
		}
		// 视频剪辑方式
		if (dto.getCut_mode() != null) {
			movie_param.put("cut_mode", dto.getCut_mode());
		}
		// 任务ID
		if (dto.getTaskid() != null) {
			movie_param.put("taskid", dto.getTaskid());
		}
		// 内容主线
		if (dto.getContent_main_thread_int() != null) {
			movie_param.put("content_main_thread",
					dto.getContent_main_thread_int());
		}

		// 周边剪辑方式
		if (dto.getVideo_cut_mode_int() != null) {
			movie_param.put("video_cut_mode", dto.getVideo_cut_mode_int());
		}
		// 节目关系
		if (dto.getShow_relation_int() != null) {
			movie_param.put("show_relation", dto.getShow_relation_int());
		}

		// 视频类型（二级标签)
		if (dto.getRemain_video_type_int() != null) {
			movie_param
					.put("remain_video_type", dto.getRemain_video_type_int());
		}

		if (dto.getVtype_mark_int() != null) {
			movie_param.put("vtype_mark", dto.getVtype_mark_int());
		}

		// 期数
		if (dto.getStage() != null) {
			movie_param.put("stage", dto.getStage());
		}

		// 播出频道
		if (dto.getChannel() != null) {
			movie_param.put("tv_channel", dto.getChannel());
		}
		// 栏目名称
		if (dto.getColumn() != null) {
			movie_param.put("tv_programa", dto.getColumn());
		}
		// 是否有logo
		movie_param.put("has_logo", "0");
		// 是否有开关板
		movie_param.put("has_switch", "0");
		// 是否遮标
		movie_param.put("has_delogo", "0");
		// 生产工具
		movie_param.put("product_tool", "18876");
		// 物料来源
		movie_param.put("material_source", "18881");
		// 规格
		movie_param.put("video_specification", "18890");
		// 所属正片视频剪切开始时间点
		if (dto.getDeployDate() != null) {
			movie_param.put("deployDate", dto.getDeployDate());
			movie_param.put("src_end", dto.getDeployDate());
		}
		// 所属正片视频剪切结束时间点
		// 结束推送时间
		movie_param.put("push_endtime", DateUtil.format.format(new Date()));
		allParams.put("movie_param", JsonConverter.format(movie_param));
		//log.info("normal allParams:%s" + (allParams));
		//log.info("normal movie_param:%s" + (movie_param));
		String tailUrl = getSignAndUrlParam(allParams);
		String yUrl = "https://openapi.youku.com/router/rest.json";
		Map<String,Object> pa = null;
		if(tailUrl!=null){
			pa = JsonConverter.asMap(tailUrl, String.class, Object.class);
		}
		String r = HttpUtil.httpPost(yUrl, pa);// 返回结果实例:{"cost":0.30200002,"data":[{"vid":"XMzk5NTA2MDgyMA==","upload_token":"NTU0OTM1Nzc5XzAxMDA2NDNBQTI1QzJEQ0U4MjZENkYyRjhGMTE5OTkwNkRDQzQwLTREM0ItN0E5NC05NDFFLUM4N0FBN0RDM0UzQV8xX2FhYzI0N2JiYmEyM2Y3MzlmZDVlNjdjZTM5ZjI5ZTZj"}],"e":{"error_msg":"OK","error_code":1},"openapi":{"traceId":"0b8ba16815465058583872377e1ef9"}}
		log.info("保存任务 result" + r);
	}
	
	/**
	 * 创建文件.
	 */
	public static String create_file(YouKuV3Dto dto, String fileSize) {
		log.info("normal 创建文件 start create_file...");
		Map<String, Object> params = new HashMap<>();
		params.put("upload_token", uploadToken);
		params.put("file_size", fileSize);
		String filePath = dto.getFilePath();
		File file = new File(filePath);
		String fileName = file.getName();
		String ext = "";
		if (fileName != null) {
			ext = fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		params.put("ext", ext);
		params.put("slice_length", 2048); // # KB
		uploadServer = "106.15.56.184";//106.15.56.184
		String url = "http://" + uploadServer + "/gupload/create_file";
		int times = 0;
		while (true) {
			try {
				String r = HttpUtil.httpPost(url, params);
				log.info("创建文件 result:" + r);
				if (r.indexOf("<Response [201]>") != -1) {
					SaveUploadStateToFile(dto);
				}
				return r;
			} catch (Exception e) {
				times += 1;
				if (times > 10) {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					return null;
				}
			}
		}
		//return null;
	}
	
	private static void SaveUploadStateToFile(YouKuV3Dto dto) {

		String save_file = dto.getFilePath() + ".upload";
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("upload_token", uploadToken);
		data.put("upload_server_ip", uploadServer);
		File file = new File(save_file);
		data.put("file", file);
		dataFile = data;
		// with open(save_file, 'w') as f:
		// json.dump(data, f)
	}

	/**
	 * 新建分片.
	 * 
	 */
	public static String new_slice() {
		log.info("normal start new_slice...");
		Map<String, String> params = new HashMap<String, String>();
		params.put("upload_token", uploadToken);
		String url = "http://" + uploadServer + "/gupload/new_slice";
		log.info("normal begin new_slice, url is " + url + " params is " + JsonConverter.format(params));
		int times = 0;
		while (true) {
			try {
				String r = HttpUtil.sendGet(url, params);
				log.info("新建分片返回结果:" + r);//结果实例:{"slice_task_id":1,"offset":0,"length":2097152,"transferred":0,"finished":false}
				if (r != null) {
					SaveSliceState(r);
				}
//				if (r.indexOf("<Response [201]>") != -1) {
//				}
				return r;
			} catch (Exception e) {
				times += 1;
				if (times > 10) {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					return null;
				}
			}

		}
		//return null;
	}

	private static void SaveSliceState(String result) {
		Map<String, Object> map = new HashMap<>();
		if (result != null && !result.equals("")) {
			map = JsonConverter.asMap(result, String.class, Object.class);
			if (map != null) {
				if (map.containsKey("slice_task_id")) {
					slice_task_id = map.get("slice_task_id").toString();
				}
				if (map.containsKey("offset")) {
					slice_offset = map.get("offset").toString();
				}
				if (map.containsKey("length")) {
					slice_length = map.get("length").toString();
				}
				if (map.containsKey("transferred")) {
					transferred = map.get("transferred").toString();
				}
				if (map.containsKey("finished")) {
					finished = map.get("finished").toString();
				}
			}
		}

	}
	
	/**
	 * 上传分片.
	 * @throws Exception 
	 */
	public static String uploadSlice(YouKuV3Dto dto) throws Exception {
		log.info("normal start upload_slice...");
		RandomAccessFile file = new RandomAccessFile(dto.getFilePath(), "rw");
		file.seek(Long.valueOf(slice_offset));
		byte[] data = new byte[Integer.valueOf(slice_length)];
		file.read(data);
		log.info("上传分片data长度:" + data.length);
		Map<String, Object> params = new HashMap<>();
		params.put("upload_token", uploadToken);
		log.info("上传分片upload_token:" + uploadToken);
		params.put("slice_task_id", slice_task_id);
		log.info("上传分片slice_task_id:" + slice_task_id);
		params.put("offset", slice_offset);
		log.info("上传分片slice_offset:" + slice_offset);
		params.put("length", slice_length);
		log.info("上传分片length:"+slice_length);
		params.put("hash", getMd5FromData(data));
		//log.info("上传分片hash:" + Md5Util.getMD5(data));
		params.put("data", data);
		//log.info("上传分片所有参数:"+JsonConverter.format(params));
		String url = "http://" + uploadServer + "/gupload/upload_slice";
		int times = 0;
		while (true) {
			try {
				String r = HttpUtil.httpPost1(url, params);
				log.info("上传分片 返回结果:" + r);// 实例:{"error":{"code":120010225,"type":"UploadsException","description":"Hash check error"}}
				if(r.contains("error")){
					return null;
				}
				SaveSliceState(r);
				return slice_task_id;
			} catch (Exception e) {
				times += 1;
				if (times > 10) {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			break;
		}
		return null;
	}
	
	private static String getMd5FromData(byte[] data) {
		String bi = null;
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			md.update(data);
			byte[] b = md.digest();
			log.debug("加密后字符串" + b.toString());
			bi = encodeHex(b);
		} catch (NoSuchAlgorithmException e) {
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
             //sb.append(hex.toUpperCase()); 
              sb.append(hex); 
          }  
          return sb.toString();  
//		StringBuilder sbuilder = new StringBuilder(data.length * 2);
//		for (int i = 0; i < data.length; i++) {
//			if (((int) data[i] & 0xff) < 0x10) {
//				sbuilder.append("0");
//			}
//			sbuilder.append(Long.toString((int) data[i] & 0xff, 16));
//		}
//		return sbuilder.toString();
	}
	
	public static Map<String, Object> check() {
		log.info("normal start check_status...");
		Map<String, String> params = new HashMap<>();
		params.put("upload_token", uploadToken);
		String url = "http://" + uploadServer + "/gupload/check";
		int times = 0;
		while (true) {
			try {
				times++;
				if (times > 10) {
					return null;
				}
				String r = HttpUtil.sendGet(url, params);
				log.info("检查check 返回结果:" + r);//{"status":4,"upload_server_ip":"10.30.88.156","transferred_percent":0,"confirmed_percent":0,"empty_tasks":2,"finished":false}
				Map<String, Object> result = JsonConverter.asMap(r, String.class, Object.class);
				if (result.containsKey("status")) {
					log.info("normal", "success...");
					return result;
				} else {
					continue;
				}
			} catch (Exception e) {
				if (times > 10) {
					log.info("error", "check error");
					return null;
				} else {
					continue;
				}
			}

		}
	}

	/**
	 * 提交.
	 */
	public static Boolean commit(YouKuV3Dto dto) {
		log.info("normal start commit...");
		Map<String, Object> allParams = new HashMap<String, Object>();
		allParams.put("version", "3.0");
		allParams.put("action", "youku.video.upload.complete");
		allParams.put("client_id", clientId);
		allParams.put("access_token", accessToken);
		allParams.put("client_ip", ip);
		allParams.put("upload_token", uploadToken);
		allParams.put("upload_server_ip", uploadServer);
		String tailUrl = getSignAndUrlParam(allParams);
		String yUrl = "https://openapi.youku.com/router/rest.json";
		Map<String,Object> pa = null;
		if(tailUrl!=null){
			pa = JsonConverter.asMap(tailUrl, String.class, Object.class);
		}
		String r = HttpUtil.httpPost(yUrl, pa);
		log.info("提交返回结果:" + r);
		Map<String, Object> result = JsonConverter.asMap(r, String.class,Object.class);
		if (result.containsKey("e")) {
			return false;
		} else {
			DeleteUploadStateFile(dto);
			return true;
		}
	}
	
	private static void DeleteUploadStateFile(YouKuV3Dto dto) {
		File file = new File(dto.getFilePath() + ".upload");
		file.delete();
		dataFile = null;
	}
	
	private static void ReadUploadStateFromFile(YouKuV3Dto dto) {
		String save_file = dto.getFilePath() + ".upload";
		uploadToken = dataFile.get("upload_token").toString();
		uploadServer = dataFile.get("upload_server_ip").toString();
		Map<String, Object> result = check();
		if (result.containsKey("e")) {
			Object e = result.get("e");
			Map<String, Object> ecode = JsonConverter.asMap(e.toString(), String.class, Object.class);
			if (ecode.containsKey("code") && ecode.get("code").toString().equals("120010223")) {
				uploadToken = null;
				uploadServer = null;
				DeleteUploadStateFile(dto);
			}
		}
	}
}
