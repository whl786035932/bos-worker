package cn.videoworks.worker.dto;

import java.io.Serializable;

public class PublishImageDto implements Serializable {
	
	private String id;
	private String sourceUrl;
	private String targetUrl;
	private Integer width;
	private Integer height;
	private Integer sequence;
	private Integer type;
	
	//-------------


	public Integer getHeight() {
		return height;
	}

	public String getId() {
		return id;
	}

	public PublishImageDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getSequence() {
		return sequence;
	}


	public String getSourceUrl() {
		return sourceUrl;
	}

	public String getTargetUrl() {
		return targetUrl;
	}
	public Integer getType() {
		return type;
	}


	public Integer getWidth() {
		return width;
	}


	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}


	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public void setType(Integer type) {
		this.type = type;
	}


	public void setWidth(Integer width) {
		this.width = width;
	}

}
