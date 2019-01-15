package cn.videoworks.worker.dto;

import java.util.List;

public class CapitalOnlineExtraDataDto {
	
	private String broadCastTime;
	private List<CapitalMediaDataDto> medias;
	private String assetId;
	private  String channel;
	private String column;
//	private String column_alias;
	private String source;
//	public String getColumn_alias() {
//		return column_alias;
//	}
//	public void setColumn_alias(String column_alias) {
//		this.column_alias = column_alias;
//	}
	private String title;
	private Integer type;
	private String mediaAssetId;
	public String getAssetId() {
		return assetId;
	}
	public String getBroadCastTime() {
		return broadCastTime;
	}
	public String getChannel() {
		return channel;
	}
	public String getColumn() {
		return column;
	}
	public String getMediaAssetId() {
		return mediaAssetId;
	}
	public List<CapitalMediaDataDto> getMedias() {
		return medias;
	}
	public String getSource() {
		return source;
	}
	public String getTitle() {
		return title;
	}
	public Integer getType() {
		return type;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public void setBroadCastTime(String broadCastTime) {
		this.broadCastTime = broadCastTime;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public void setMediaAssetId(String mediaAssetId) {
		this.mediaAssetId = mediaAssetId;
	}
	public void setMedias(List<CapitalMediaDataDto> medias) {
		this.medias = medias;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	
	

}
