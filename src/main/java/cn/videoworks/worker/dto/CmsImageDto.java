package cn.videoworks.worker.dto;

public class CmsImageDto {
	
	private String id;
	private String url;
	
	private String file_name;
	
	private Integer type;

	private Integer width;

	private Integer height;
	
	private Integer size;
	private String check_sum;
	public String getCheck_sum() {
		return check_sum;
	}
	
	public String getFile_name() {
		return file_name;
	}

	public Integer getHeight() {
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

	public Integer getWidth() {
		return width;
	}

	public void setCheck_sum(String check_sum) {
		this.check_sum = check_sum;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public void setHeight(Integer height) {
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

	public void setWidth(Integer width) {
		this.width = width;
	}

}
