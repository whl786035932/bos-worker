package cn.videoworks.worker.dto;

public class AwsStorageDto {

	private String id;
	private String url;
	private String check_sum;
	private Integer type;
	private String mediaAssetId;
	
	public String getMediaAssetId() {
		return mediaAssetId;
	}
	public void setMediaAssetId(String mediaAssetId) {
		this.mediaAssetId = mediaAssetId;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCheck_sum() {
		return check_sum;
	}
	public void setCheck_sum(String check_sum) {
		this.check_sum = check_sum;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	
}
