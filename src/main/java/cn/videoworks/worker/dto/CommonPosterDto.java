package cn.videoworks.worker.dto;

public class CommonPosterDto {

	/** 主键 uuid */
	private String id;
	
	/** 源地址 */
	private String sourceUrl;
	
	/** 目标存储地址 */
	private String targetUrl;
	
	/** 宽 */
	private Integer width;
	
	/**  高 */
	private Integer height;
	
	/** 顺序 */
	private Integer sequence;
	
	/** 1:海报,2:封面,3:缩略图 */
	private Integer type;
	
	/** 大小 kb */
	private Integer size;
	
	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
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

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
}
