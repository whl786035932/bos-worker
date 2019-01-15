package cn.videoworks.worker.exception;

import org.bouncycastle.jce.exception.ExtException;

public class MediaInfoException extends Exception{
	private String message;
	public String getMessage() {
		return message;
	}
	
	public MediaInfoException(String message) {
		this.message=message;
	}
}
