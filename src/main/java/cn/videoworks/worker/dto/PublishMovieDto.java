package cn.videoworks.worker.dto;

import java.io.Serializable;

public class PublishMovieDto implements Serializable {
	
	private String id;
	private String sourceUrl;
	private String targetUrl;
	private Long duration;
	
	//below field is own defined for cms stroage


	public Long getDuration() {
		return duration;
	}

	public PublishMovieDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	
	
}
