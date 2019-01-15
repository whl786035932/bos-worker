/**
 * RestResponse.java
 * cn.videoworks.publisher.util
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2015年5月12日 		meishen
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
*/

package cn.videoworks.worker.util;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:RestResponse
 *
 * @author   meishen
 * @version  
 * @since    Ver 1.0.0
 * @Date	 2015年5月12日		下午2:36:55
 *
 */
public class RestResponse {

	/**
	 * Response status code.
	 *
	 * @see cn.videoworks.commons.webdev.constant.ResponseStatusCode
	 */
	private int statusCode;

	/** Response status message. */
	private String message;

	/** Response body. */
	private Map<String, Object> data;
	
	

	public RestResponse() {
		this.data = new HashMap<>();
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	
}

