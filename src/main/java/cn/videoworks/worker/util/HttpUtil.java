package cn.videoworks.worker.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import cn.videoworks.commons.util.json.JsonConverter;

public class HttpUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(HttpUtil.class);

	public static ResponseEntity<String> sentHttpRequest(HttpMethod method,
			Object data, String url, HttpHeaders httpHeaders) {
		if (httpHeaders == null) {
			httpHeaders = getHttpHearders();
		}
		HttpEntity<String> httpEntity = new HttpEntity<String>(
				JsonConverter.format(data), httpHeaders);
		RestTemplate restTemplate = getUTF8StringRestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.exchange(url,
				method, httpEntity, String.class);
		return responseEntity;
	}

	/**
	 * 得到utf8编码的restTemplate
	 * 
	 * @return
	 */
	private static RestTemplate getUTF8StringRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> converters = new LinkedList<>();
		converters
				.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		converters.add(new MappingJackson2HttpMessageConverter());
		restTemplate.setMessageConverters(converters);
		return restTemplate;
	}

	/**
	 * 得到http请求头
	 * 
	 * @return
	 */
	public static HttpHeaders getHttpHearders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", "application/json");
		httpHeaders.set("Accept-Charset", "UTF-8");
		return httpHeaders;
	}

	public static String get(String url, Map<String, String> headersMap) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		if (headersMap != null && headersMap.size() > 0) {
			for (String key : headersMap.keySet()) {
				headers.add(key, headersMap.get(key));
			}
		}
		String result = restTemplate.getForObject(url, String.class);
		return result;
	}

	public static String post(String url, Map<String, Object> requestBody, Map<String, String> headersMap) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType
				.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		if (headersMap != null && headersMap.size() > 0) {
			for (String key : headersMap.keySet()) {
				headers.add(key, headersMap.get(key));
			}
		}
		HttpEntity<String> formEntity = new HttpEntity<String>(
				JsonConverter.format(requestBody), headers);
		String result = restTemplate.postForObject(url, formEntity,
				String.class);
		return result;
	}
	public static String post(String url, String requestBody, Map<String, String> headersMap) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		if (headersMap != null && headersMap.size() > 0) {
			for (String key : headersMap.keySet()) {
				headers.add(key, headersMap.get(key));
			}
		}
		HttpEntity<String> formEntity = new HttpEntity<String>(requestBody, headers);
		String result = restTemplate.postForObject(url, formEntity, String.class);
		return result;
	}
	public static String httpPost(String url, Map<String, Object> params) {
		URL u = null;
		HttpURLConnection con = null;
		// 构建请求参数
		StringBuffer sb = new StringBuffer();
		String send_data = null;
		if (params != null) {
			for (java.util.Map.Entry<String, Object> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
			send_data = sb.substring(0, sb.length() - 1);
		}
		System.out.println("send_url:" + url);
		System.out.println("send_data:" + send_data);
		// 尝试发送请求
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			// // POST 只能为大写，严格限制，post会不识别
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			//con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36)");
//			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			con.setRequestProperty("accept", "application/json");
			con.setRequestProperty("connection", "Keep-Alive");
			//con.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			con.setRequestProperty("user-agent","Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:37.0) Gecko/20100101 Firefox/37.0");
	           
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
			osw.write(send_data);
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}

		// 读取返回内容
		StringBuffer buffer = new StringBuffer();
		try {
			// 一定要有返回值，否则无法把请求发送给server端。
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}
	
	public static String httpPost1(String url, Map<String, Object> params) {
		URL u = null;
		HttpURLConnection con = null;
		// 构建请求参数
		StringBuffer sb = new StringBuffer();
		String send_data = null;
		byte[] data = null;
		if (params != null) {
			for (java.util.Map.Entry<String, Object> e : params.entrySet()) {
				if (!e.getKey().equals("data")) {
					sb.append(e.getKey());
					sb.append("=");
					sb.append(e.getValue());
					sb.append("&");
				} else {
					data = (byte[]) e.getValue();
				}
			}
			send_data = sb.substring(0, sb.length() - 1);
		}
		System.out.println("send_url:" + url);
		System.out.println("send_data:" + send_data);
		// 尝试发送请求
		try {
			u = new URL(url + "?" + send_data);
			con = (HttpURLConnection) u.openConnection();
			// // POST 只能为大写，严格限制，post会不识别
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			con.setRequestProperty("accept", "application/json");
			con.setRequestProperty("connection", "Keep-Alive");
			con.setRequestProperty("user-agent","Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:37.0) Gecko/20100101 Firefox/37.0");
			DataOutputStream osw = new DataOutputStream(con.getOutputStream());
			osw.writeChars(data.toString());
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}

		// 读取返回内容
		StringBuffer buffer = new StringBuffer();
		try {
			// 一定要有返回值，否则无法把请求发送给server端。
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}
	/**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, Map<String,String> params) {
        String result = "";
        BufferedReader in = null;
     // 构建请求参数
        StringBuffer sb = new StringBuffer();
        String param = null;
        try {
        	if (params != null) {
    			for (Map.Entry<String, String> e : params.entrySet()) {
    				sb.append(e.getKey());
    				sb.append("=");
    				sb.append(e.getValue());
    				sb.append("&");
    			}
    			param = sb.substring(0, sb.length() - 1);
    		}
        	System.out.println("send_url:" + url);
    		System.out.println("send_data:" + param.toString());
            String urlNameString = url + "?" + param.toString();
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
	/**
	 * put请求.
	 * 
	 * @param urlPath
	 * @param data
	 * @param charSet
	 * @param header
	 * @return
	 */
	public static String httpPut(String urlPath, byte[] data, String charSet, Map<String,String> header) {
		String result = null;
		URL url = null;
		HttpURLConnection httpurlconnection = null;
		try {
			url = new URL(urlPath);
			httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setDoInput(true);
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setConnectTimeout(2000000);// 设置连接主机超时（单位：毫秒）
			httpurlconnection.setReadTimeout(2000000);// 设置从主机读取数据超时（单位：毫秒）

			if (header != null) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					httpurlconnection.setRequestProperty(entry.getKey(), entry.getValue());
				}
				// httpurlconnection.setRequestProperty(content[0], content[1]);
				// for (int i = 0; i < header.length; i++) {
				// String[] content = header[i].split(":");
				// httpurlconnection.setRequestProperty(content[0], content[1]);
				// }
			}

			httpurlconnection.setRequestMethod("PUT");
			httpurlconnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			if (StringUtils.isNotBlank(data.toString())) {
				httpurlconnection.getOutputStream().write(data);
			}
			httpurlconnection.getOutputStream().flush();
			httpurlconnection.getOutputStream().close();
			int code = httpurlconnection.getResponseCode();

			if (code == 200) {
				DataInputStream in = new DataInputStream(
						httpurlconnection.getInputStream());
				int len = in.available();
				byte[] by = new byte[len];
				in.readFully(by);
				if (StringUtils.isNotBlank(charSet)) {
					result = new String(by, Charset.forName(charSet));
				} else {
					result = new String(by);
				}
				in.close();
			} else {
				logger.error("请求地址：" + urlPath + "返回状态异常，异常号为：" + code);
			}
		} catch (Exception e) {
			logger.error("访问url地址：" + urlPath + "发生异常", e);
		} finally {
			url = null;
			if (httpurlconnection != null) {
				httpurlconnection.disconnect();
			}
		}
		return result;
	}

}
