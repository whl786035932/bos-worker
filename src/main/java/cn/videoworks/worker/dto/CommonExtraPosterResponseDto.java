package cn.videoworks.worker.dto;

public class CommonExtraPosterResponseDto {
	private String id;
	private String url;

	private String file_name;

	private Long duration;
	private Double bitrate;
	private String width;
	private String height;
	private Integer type;
	private Integer size;
	private String check_sum;
	
	public Double getBitrate() {
		return bitrate;
	}
	
	public String getCheck_sum() {
		return check_sum;
	}

	public Long getDuration() {
		return duration;
	}

	public String getFile_name() {
		return file_name;
	}

	public String getHeight() {
		return height;
	}

	public String getId() {
		return id;
	}

	public Integer getSize() {
		return size;
	}

	public Integer getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public String getWidth() {
		return width;
	}

	public void setBitrate(Double bitrate) {
		this.bitrate = bitrate;
	}

	public void setCheck_sum(String check_sum) {
		this.check_sum = check_sum;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setWidth(String width) {
		this.width = width;
	}
	
	
}
