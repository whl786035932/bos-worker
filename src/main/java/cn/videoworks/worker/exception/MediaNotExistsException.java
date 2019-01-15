package cn.videoworks.worker.exception;

public class MediaNotExistsException extends Exception{

	private String message;
	private String url;
	public MediaNotExistsException(String url) {
		this.url=url;
	}
	public String getMessage() {
		return "视频文件-"+this.url+",不存在";
	}
}
