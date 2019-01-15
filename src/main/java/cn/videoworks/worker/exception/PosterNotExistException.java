package cn.videoworks.worker.exception;

import org.bouncycastle.jce.exception.ExtException;

public class PosterNotExistException extends Exception{
	private String message;
	private String url;
	public String getMessage() {
		message="海报:"+url+"不存在";
		return message;
	}
	
	public PosterNotExistException(String url) {
		this.url=url;
	}
}
