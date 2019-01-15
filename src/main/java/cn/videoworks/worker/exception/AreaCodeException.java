package cn.videoworks.worker.exception;

public class AreaCodeException extends Exception {
	private String message;
	public AreaCodeException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return  this.message;
	}
}
