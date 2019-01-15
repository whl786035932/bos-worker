package cn.videoworks.worker.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ClassName:FTPClient Function: TODO ADD FUNCTION Reason: TODO ADD REASON
 * ftp客户端
 * 
 * @author meishen
 * @version
 * @since Ver 1.1
 * @Date 2018 2018年6月29日 下午4:15:50
 * 
 * @see
 */
public class FTPUtil {

	private static FTPClient ftp = null;
	private static FTPUtil instance = null;

	private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

	private FTPUtil() {
		ftp = new FTPClient();
		logger.info("创建ftp客户端成功!");
	}

	public static FTPUtil getInstance() {
		instance = new FTPUtil();
//		synchronized (FTPUtil.class) {
//			if (instance == null) {
//				instance = new FTPUtil();
//			}
//		}
		logger.info("创建FTPClient对象端成功!");
		return instance;
	}

	public FTPClient getFtpClient() {
		if (instance != null) {
			return instance.ftp;
		}
		return null;
	}

	/**
	 * connect:(连接ftp)
	 * 
	 * @author meishen
	 * @Date 2018 2018年6月29日 下午6:48:58
	 * @return
	 * @return boolean
	 * @throws Exception 
	 * @throws @since
	 *             Videoworks Ver 1.1
	 */
	public boolean connect(Map<String,String>params) throws Exception {
		boolean flag = false;
		if (null != ftp && ftp.isConnected()) {
			logger.info("ftp已连接");
			flag = true;
		} else {
			try {
				String host = "";
				int port = 0;
				String username = "";
				String password = "";
				try {
					host = params.get("ip");
					logger.info("ftp.ip=" + host);
					port = Integer.valueOf(params.get("port"));
					logger.info("ftp.port=" + port);
					username = params.get("user");
					logger.info("ftp.username=" + username);
					password = params.get("passWord");
					logger.info("ftp.password=" + password);
					System.out.println("ftp====================" + ftp);
					ftp.connect(host,port);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("FTP连接失败" + e.getMessage());
					throw new Exception("FTP连接失败" + e.getMessage());
				}
				ftp.login(username, password);
				logger.info("FTP登录成功!");
//				ftp.enterLocalActiveMode();
				ftp.enterLocalPassiveMode();
				if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
					ftp.setControlEncoding("UTF-8");
					ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
					logger.info("FTP连接成功。");
					flag = true;
				} else {
					logger.error("FTP连接失败，用户名或者密码错误!");
					close();
					flag = false;
				}
			} catch (SocketException e) {
				e.printStackTrace();
				logger.error("FTP连接失败，IP或者端口错误。");
				close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("FTP连接失败，IP或者端口错误。");
				close();
			}
		}
		return flag;
	}

	public void close() {
		try {
			if (ftp != null) {
				if (ftp.isConnected()) {
					ftp.logout();
					ftp.disconnect();
				}
				ftp = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("FTP关闭失败！");
		}
	}

	/**
	 * makeDirectory:(如果目录不存在，则创建)
	 * 
	 * @author meishen
	 * @Date 2018 2018年6月29日 下午6:54:39
	 * @param remote
	 * @return void
	 * @throws @since
	 *             Videoworks Ver 1.1
	 */
	public void makeDirectory(String remote) {
		try {
			if (!ftp.changeWorkingDirectory(remote)) {
				StringTokenizer s = new StringTokenizer(remote, "/"); // sign
				String pathName = "";
				while (s.hasMoreElements()) {
					pathName = pathName + "/" + (String) s.nextElement();
					ftp.mkd(pathName);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("创建目录失败:【" + remote + "】");
		}
	}

	/**
	 * 获取远程文件大小.
	 * @throws IOException 
	 */

	public Long getFileSize(String path) {
		FTPFile[] files = null;
		Long size = null;
		try {
			files = ftp.listFiles(path);
			if (files != null && files.length > 0) {
				size = files[0].getSize();
				logger.error("获取远程文件大小:" + size);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("获取远程文件大小失败");
		}
		return size;
	}
	/**
	 * upload:(上传文件)
	 * 
	 * @author meishen
	 * @Date 2018 2018年6月29日 下午6:55:07
	 * @param fileName
	 * @param filePath
	 * @param file
	 * @return
	 * @return boolean
	 * @throws @since
	 *             Videoworks Ver 1.1
	 */
	public boolean upload(String fileName, String filePath, InputStream input) {
		boolean flag = false;
		try {
			makeDirectory(filePath);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.changeWorkingDirectory(filePath);
			ftp.storeFile(fileName, input);
			logger.info("上传成功!");
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		} finally {
			try {
				input.close();
				//ftp.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	/**
	 * readFtpFile:(读取ftp文件流)
	 *
	 * @author meishen
	 * @Date 2018 2018年7月4日 下午2:27:54
	 * @param ftpFile
	 * @return
	 * @return InputStream
	 * @throws @since
	 *             Videoworks Ver 1.1
	 */
	public InputStream readFtpFile(String ftpFileName, String ftpFilePath) {
		InputStream is = null;
		try {
			ftp.enterLocalPassiveMode();
			ftp.changeWorkingDirectory(ftpFilePath);
			is = ftp.retrieveFileStream(ftpFileName);
		} catch (IOException e) {
			e.printStackTrace();
			is = null;
		}
		return is;
	}

	/**
	 * downloadFile:(ftp下载文件)
	 *
	 * @author meishen
	 * @Date 2018 2018年7月4日 下午5:26:01
	 * @param remotePath
	 * @param fileName
	 * @param localPath
	 * @return
	 * @throws Exception
	 * @return boolean
	 * @throws @since
	 *             Videoworks Ver 1.1
	 */
	public boolean downloadFile(String remotePath, String fileName, String localPath) throws Exception {
		FileOutputStream fos = null;
		try {
			File localFile = new File(localPath, fileName);
			fos = new FileOutputStream(localFile);

			ftp.enterLocalPassiveMode();
			ftp.changeWorkingDirectory(remotePath);
			boolean bok = ftp.retrieveFile(fileName, fos);

			fos.close();
			fos = null;

			return bok;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (Exception e2) {
				}
			}
		}

	}

	/**
	 * 往ftp 的filePath目录下写入文件
	 * 
	 * @param fileName
	 * @param filePath
	 * @param content
	 * @throws IOException
	 */
	public void writeFileToFtp(String fileName, String filePath, String content) throws IOException {
		InputStream is = null;
		try {
			
		boolean changeDir = ftp.changeWorkingDirectory(filePath);
		// 创建文件夹
		if (changeDir) {
			ftp.enterLocalPassiveMode();
			ftp.setControlEncoding("utf-8");
 			// 1.输入流
 			is = new ByteArrayInputStream(content.getBytes());
 			ftp.storeFile(new String(fileName.getBytes("utf-8"),
 					"iso-8859-1"), is);

// 			
//			// // 向指定文件写入内容，如果没有该文件，则先创建文件再写入。写入的方式是追加。
//			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ftpClient.appendFileStream(fileName),
//					"utf-8");
//			PrintWriter pw = new PrintWriter(outputStreamWriter, true); // 写入的文件名
//			pw.write(content);
//			pw.flush();
//			pw.close();
//			outputStreamWriter.close();
		} else {
			// 如果不能进入dir下，说明此目录不存在！
			StringTokenizer s = new StringTokenizer(filePath, "/"); // sign
			String pathName = "";
			while (s.hasMoreElements()) {
				pathName = pathName + "/" + (String) s.nextElement();
				ftp.mkd(pathName);
			}
			boolean changeWorkingDirectory = ftp.changeWorkingDirectory(filePath);
			is = new ByteArrayInputStream(content.getBytes());
 			ftp.storeFile(new String(fileName.getBytes("utf-8"),
 					"iso-8859-1"), is);
//			
//			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ftpClient.appendFileStream(fileName), "utf-8");
//			PrintWriter pw = new PrintWriter(outputStreamWriter, true); // 写入的文件名
//			pw.write(content);
//			pw.flush();
//			pw.close();
//			outputStreamWriter.close();
		}
		}finally {
			is.close();
			ftp.disconnect();
		}

		
	}
	/**
	 * 判断url是否为视频文件.
	 * 
	 * @param httpPath
	 * @return
	 */
	public static boolean existHttpPath(String httpPath) {
		URL httpurl = null;
		try {
			httpurl = new URL(httpPath);
			URLConnection rulConnection = httpurl.openConnection();
			rulConnection.getInputStream();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 通过url获取输入流.
	 * 
	 * @param url
	 *            文件 http url
	 * @return inputStream 输入流数据
	 */
	public static InputStream getFileStream(String url) {
		// 得到输入流
		InputStream inputStream = null;
		HttpURLConnection conn = null;
		try {
			// 测试数据
			// url =
			URL urls = new URL(url);
			conn = (HttpURLConnection) urls.openConnection();
			System.out.print("远程连接成功");//.debug("远程连接成功");
			// 设置超时间为3秒
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);// 使用 URL 连接进行输入
			conn.setUseCaches(false);// 忽略缓存
			// 防止屏蔽程序抓取而返回403错误
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			inputStream = conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return inputStream;
	}	
	public static void main(String[] args) throws Exception {
		FTPUtil client = FTPUtil.getInstance();
		Map<String, String> re = new HashMap<>();
		re.put("ip", "10.2.17.58");
		re.put("port", "21");
		re.put("user", "deploy");
		re.put("passWord", "deploy");
		boolean connect = client.connect(re);
		if (connect) {
			client.writeFileToFtp("517668.md5", "test/", "nihao");
		} else {
			System.out.println("ftp连接失败");
		}

	}

}
