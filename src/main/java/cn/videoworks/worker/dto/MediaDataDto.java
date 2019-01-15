package cn.videoworks.worker.dto;

import java.io.Serializable;

public class MediaDataDto implements Serializable{
	private String filename;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public MediaDataDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MediaDataDto(String filename, String format, Long size, Long duration, MediaVideoDto video) {
		super();
		this.filename = filename;
		this.format = format;
		this.size = size;
		this.duration = duration;
		this.video = video;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public MediaVideoDto getVideo() {
		return video;
	}
	public void setVideo(MediaVideoDto video) {
		this.video = video;
	}
	private String format;
	private Long size;
	private Long duration;
	private MediaVideoDto video;
	

}
