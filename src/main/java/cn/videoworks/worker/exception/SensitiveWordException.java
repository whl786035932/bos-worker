package cn.videoworks.worker.exception;

public class SensitiveWordException  extends Exception{
	
	private String sensitiveWord;
	@Override
	public String getMessage() {
		return "敏感词异常："+sensitiveWord;
	}
	
	public SensitiveWordException(String sensitiveWord) {
		this.sensitiveWord = sensitiveWord;
	}
}

