package cn.videoworks.worker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.worker.client.FTPUtil;
import cn.videoworks.worker.constant.ResponseDictionary;
import cn.videoworks.worker.dto.MediaDto;
import cn.videoworks.worker.dto.PublishImageDto;
import cn.videoworks.worker.dto.TencentDto;

public class TencentUtil {
	private static Logger log = LoggerFactory.getLogger(TencentUtil.class);
	private static String targetPath = null;
	private static String jsonMessage = null;

	/**
	 * 命名json文档生成json信息(第一步).
	 */
	public static Map<String, Object> InfoFileGenerator(TencentDto dto) {
		// 将内容写进去
		targetPath =  dto.getTargetPath();
		log.info("targetPath="+targetPath);
		jsonMessage = dto.getJsonMessage();
		return response(ResponseDictionary.SUCCESS, "InfoFileGenerator method success");
	}

	/**
	 * 发布BatchFtpUpload4Tencent第二步(判断json是否存在,不存在创建并上传图片)
	 * @throws Exception 
	 */
	public static Map<String, Object> BatchFtpUpload4Tencent(TencentDto dto) throws Exception {
		try {
			//String targetPath = dto.getTargetPath();
			// 获取对应的ftp相关信息
			Map<String, String> ftpMessage = getFtpMessageBatchFtpUpload4Tencent();
			// 拼接json链接
			String checkFileName = targetPath + "cover.json";
			// 获取ftp信息
			FTPUtil ftp = FTPUtil.getInstance();
			boolean isConnect = ftp.connect(ftpMessage);
			if (!isConnect) {
				return response(ResponseDictionary.ERROR, "BatchFtpUpload4Tencent ftp 链接失败");
			}
			Long size = null;
			try {
				size = ftp.getFileSize(checkFileName);
				log.info("第二步 json size:" + size);
				ftp.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 判断ftp是否已经上传图片
			if (size != null && size > 0) {
				// 关闭ftp
				log.info("ftp 已经上传过图片");
				return response(ResponseDictionary.SUCCESS, "BatchFtpUpload4Tencent method uploadPicture 已经上传");
			}
			log.info("ftp 开始上传图片");
			if (dto.getPosters() == null || dto.getPosters().size() == 0) {
				return response(ResponseDictionary.ERROR, "BatchFtpUpload4Tencent 参数海报为空无法上传");
			}
			// 上传图片
			ftpUploadByWput(ftpMessage,targetPath,dto.getPosters());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response(ResponseDictionary.SUCCESS, "BatchFtpUpload4Tencent method success");
	}
	
	/**
	 * 第三步(上传ftp)上传json文件并写入内容.
	 * @throws Exception 
	 */
	public static Map<String, Object> FtpDeployer(TencentDto dto) throws Exception {
		// 获取ftp信息
		Map<String, String> ftpMessage = getFtpMessageBatchFtpUpload4Tencent();
		log.info("第三步开始运行");
		FTPUtil ftp = FTPUtil.getInstance();
		boolean isConnect;
		try {
			isConnect = ftp.connect(ftpMessage);
			if (!isConnect) {
				return response(ResponseDictionary.ERROR, "BatchFtpUpload4Tencent ftp 链接失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ftp.writeFileToFtp("cover.json",targetPath,jsonMessage);
		ftp.close();
		log.info("第三步运行结束json 内容写入成功");
		return response(ResponseDictionary.SUCCESS, "FtpDeployer success");
	}
	
	/**
	 * 四步省略.
	 * 第五步上传视频.
	 */
	public static Map<String, Object> FtpDeployerFive(TencentDto dto) {
		// 获取文件后缀
		log.info("第四步开始上传视频");
		if (dto.getMedias() == null || dto.getMedias().size() == 0) {
			log.info("第五步上传视频 视频为空,直接返回!");
			return response(ResponseDictionary.SUCCESS, "FtpDeployerFive 视频为空直接返回 success");	
		}
		// 获取ftp信息
		Map<String, String> ftpMessage = null;
		FTPUtil ftp = FTPUtil.getInstance();
		try {
			ftpMessage = getFtpMessageBatchFtpUpload4Tencent();
			boolean isConnect = ftp.connect(ftpMessage);
			if(!isConnect){
				return response(ResponseDictionary.ERROR, "FtpDeployerFive 第五步 ftp链接失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (MediaDto me : dto.getMedias()) {
			File file = new File(me.getSourceUrl());
			// 文件名称
			String file_name = file.getName();
			//String ext = file_name.substring(file_name.lastIndexOf(".") + 1);
			// 替换文件名后缀
			//String fileName = me.getSourceUrl().replace(ext,dto.getInfoFileNameExt());
			// 判断文件是否存在
			if (!file.exists()) {
				log.info("第五步 上传ftp失败!原因:文件不存在");
				return response(ResponseDictionary.SUCCESS, "FtpDeployer 原视频文件不存在");
			}
			// 设置目标文件
			String targetFileName = file_name.substring(0, file_name.lastIndexOf(".")) + ".p5.mp4";
			log.info("上传视频文件名:"+targetFileName);
			InputStream in = null;
			try {
				in = new FileInputStream(me.getSourceUrl());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			ftp.upload(targetFileName, targetPath, in);
		}
		// 关闭ftp
		ftp.close();
		log.info("第四步结束:视频上传成功");
		return response(ResponseDictionary.SUCCESS, "FtpDeployerFive success");	
	}

	/**
	 * 第六步 上传视频json文件.
	 */
	public static Map<String, Object> uploadVideoJosn(TencentDto dto) {
		log.info("第五步开始:上传视频json文件");
		FTPUtil ftp = FTPUtil.getInstance();
		Map<String, String> ftpMessage = null;
		try {
			ftpMessage = getFtpMessageBatchFtpUpload4Tencent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean isConnect;
		try {
			isConnect = ftp.connect(ftpMessage);
			if (!isConnect) {
				return response(ResponseDictionary.ERROR, "BatchFtpUpload4Tencent ftp 链接失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i=0;i<dto.getMedias().size();i++) {
			File file = new File(dto.getMedias().get(i).getSourceUrl());
			//String targetFileNameJson = targetPath + file.getName() + ".p5.mp4"+".json";
			String targetFileNameJson = file.getName().substring(0, file.getName().lastIndexOf(".")) + ".p5.mp4"+".json";
			try {
				ftp.writeFileToFtp(targetFileNameJson, targetPath, dto.getVoideoMessages().get(i));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.info("第五步结束!");
		return response(ResponseDictionary.SUCCESS, "第六步uploadVideoJosn 上传成功 success");
	}
	/**
	 * 上传视频json信息.
	 */
	public static Map<String, Object> uploadVideo(TencentDto dto){
		Map<String, String> ftpMessage = null;
		try {
			ftpMessage = getFtpMessageBatchFtpUpload4Tencent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		FTPUtil ftp = FTPUtil.getInstance();
		boolean isConnect;
		try {
			isConnect = ftp.connect(ftpMessage);
			if (!isConnect) {
				return response(ResponseDictionary.ERROR, "BatchFtpUpload4Tencent ftp 链接失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response(ResponseDictionary.SUCCESS, "uploadVideo 最后一步 success");
	}
	
	private static Map<String,Object> ftpUploadByWput(Map<String, String> ftpMessage, String targetPath, List<PublishImageDto> posters) {
		// 获取ftp信息
		try {
			FTPUtil ftp = FTPUtil.getInstance();
			boolean isConnect = ftp.connect(ftpMessage);
			if(!isConnect){
				return response(ResponseDictionary.ERROR, "BatchFtpUpload4Tencent ftpUploadByWput ftp链接失败");
			}
			for (PublishImageDto dto : posters) {
				uploadFile(ftp, dto.getSourceUrl(), targetPath);
			}
			// 关闭ftp
			ftp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response(ResponseDictionary.SUCCESS, "BatchFtpUpload4Tencent ftpUploadByWput success");
	}

	/**
	 * 上传文件.
	 */
	@SuppressWarnings("static-access")
	public static Map<String, Object> uploadFile(FTPUtil ftp, String sourceUrl, String targetPath) {
		// 远程链接
		File sourceFile = new File(sourceUrl);
		//String targetFile = targetPath +File.separator + sourceFile.getName();
		try {
			if (sourceUrl.startsWith("http")) {
				if(ftp.existHttpPath(sourceUrl)){
					InputStream in = ftp.getFileStream(sourceUrl);
					// 上传图片
					ftp.upload(sourceFile.getName(), targetPath, in);
					return response(ResponseDictionary.SUCCESS, "BatchFtpUpload4Tencent uploadPoster success");
				}
			}
		} catch (Exception e1) {
			return response(ResponseDictionary.ERROR, "BatchFtpUpload4Tencent uploadPoster success");
		}
		// 判读file 是否存在
		if(!sourceFile.exists()){
			log.info("文件不存在");
			return response(ResponseDictionary.ERROR, "BatchFtpUpload4Tencent uploadPoster" + sourceFile.getName() + "上传失败!");
		}
		try {
			InputStream in = new FileInputStream(sourceUrl);
			ftp.upload(sourceFile.getName(), targetPath, in);
		} catch (FileNotFoundException e) {
			log.info("BatchFtpUpload4Tencent uploadPoster" + sourceFile.getName() + "上传失败!");
		}
		return response(ResponseDictionary.SUCCESS, "BatchFtpUpload4Tencent uploadPoster success");
	}
	
	/**
	 * 返回结果.
	 * 
	 * @param code
	 * @param message
	 */
	public static Map<String, Object> response(Integer code, String message) {
		Map<String, Object> result = new HashMap<>();
		result.put("statusCode", code);
		result.put("message", message);
		return result;
	}
	@SuppressWarnings("static-access")
	private static Map<String, String> getFtpMessageBatchFtpUpload4Tencent() throws Exception{
		Map<String, String> re = new HashMap<>();
		String ftpIp = PropertiesUtil.getPropertiesUtil().get("BatchFtpUpload.ftpIp");
		String ftpPort = PropertiesUtil.getPropertiesUtil().get("BatchFtpUpload.ftpPort");
		String ftpUser = PropertiesUtil.getPropertiesUtil().get("BatchFtpUpload.ftpUser");
		String ftpPassWord = PropertiesUtil.getPropertiesUtil().get("BatchFtpUpload.ftpPassWord");
		re.put("ip", ftpIp);
		re.put("port", ftpPort);
		re.put("user", ftpUser);
		re.put("passWord", ftpPassWord);
		return re;
	}
}
