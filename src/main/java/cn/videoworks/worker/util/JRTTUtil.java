package cn.videoworks.worker.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.videoworks.worker.constant.ResponseDictionary;
import cn.videoworks.worker.dto.JRTTDto;
import cn.videoworks.worker.dto.MediaDto;
import cn.videoworks.worker.dto.PublishImageDto;

public class JRTTUtil {
	private static final Logger log = LoggerFactory.getLogger(JRTTUtil.class);
	
	/**
	 * 整合视频数据.
	 */
	
	public static Map<String,Object> uniteMedia(MediaDto d,JRTTDto Jrrt){
		// 获取文件后缀
		File file = new File(Jrrt.getDeployVideoName());
		// 文件名称
		String file_name = file.getName();
		String ext = file_name.substring(file_name.lastIndexOf(".") + 1);
		// 替换文件名后缀
		String fileName = Jrrt.getDeployVideoName().replace(ext, Jrrt.getSourceFileExt());
		if (fileName == null) {
			return response(ResponseDictionary.ERROR,"fileName为空");
		}
		if(fileName.startsWith("vwfs://")){
			fileName = vwfs_path(fileName);
		}
		if (!FileUtil.exists(fileName)) {
			log.info("fileName is error");
			return response(ResponseDictionary.ERROR,"fileName不存在");
		}
		//部署地址
		String deployAddress = Jrrt.getAddress();
		File dir = new File(deployAddress);
		// 文件夹不存在创建
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		// 目标文件名称
		String targetFileName = Jrrt.getUploadFileName();
		if (targetFileName == null) {
			log.info("getUploadFileName error");
			return response(ResponseDictionary.ERROR,"getUploadFileName error");
		}
		// 创建部署目录
		String targetPath = new File(deployAddress + File.separator + targetFileName).getParent();
		String tempDir = deployAddress;
		String[] dirList = targetPath.replace(deployAddress, "").split(File.separator);
		for (String dis : dirList) {
			tempDir = tempDir + File.separator + dis;
			if (!FileUtil.exists(tempDir)) {
				new File(tempDir).mkdir();
			}
		}
		try {
			// 复制文件
			copyFile(new File(fileName),new File(deployAddress + File.separator + targetFileName));
			return response(ResponseDictionary.SUCCESS,"视频复制成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response(ResponseDictionary.SUCCESS,"视频复制成功");
	}

	/**
	 * 整合海报数据.
	 * 
	 */
	public static Map<String, Object> uniteImage(PublishImageDto im, JRTTDto Jrrt) {
		// 替换文件名后缀
		String fileName = Jrrt.getSourceFile();
		if (fileName == null) {
			return response(ResponseDictionary.ERROR, "fileName为空");
		}
		if (fileName.startsWith("vwfs://")) {
			fileName = vwfs_path(fileName);
		}
		if (!FileUtil.exists(fileName)) {
			log.info("fileName is error");
			return response(ResponseDictionary.ERROR, "fileName不存在");
		}
		// 部署地址
		String deployAddress = Jrrt.getAddress();
		File dir = new File(deployAddress);
		// 文件夹不存在创建
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		// 目标文件名称
		String targetFileName = Jrrt.getUploadFileName();
		if (targetFileName == null) {
			log.info("getUploadFileName error");
			return response(ResponseDictionary.ERROR, "getUploadFileName error");
		}
		// 创建部署目录
		String targetPath = new File(deployAddress + File.separator + targetFileName).getParent();
		String tempDir = deployAddress;
		String[] dirList = targetPath.replace(deployAddress, "").split(
				File.separator);
		for (String dis : dirList) {
			tempDir = tempDir + File.separator + dis;
			if (!FileUtil.exists(tempDir)) {
				new File(tempDir).mkdir();
			}
		}
		try {
			// 复制文件
			copyFile(new File(fileName), new File(deployAddress + File.separator + targetFileName));
			return response(ResponseDictionary.SUCCESS, "视频复制成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response(ResponseDictionary.SUCCESS, "视频复制成功");

	}
	/**
	 * 生成
	 */
	private static String vwfs_path(String url){
		if (!url.startsWith("vwfs://")) {
			return url;
		}
		return url;
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile
	 *            源文件
	 * @param targetFile
	 *            目标文件
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile)throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	/**
	 * 返回结果.
	 */
	private static Map<String, Object> response(Integer status, String message) {
		Map<String, Object> result = new HashMap<>();
		result.put("statusCode", status);
		result.put("message", message);
		return result;
	}
	
  
  
}
