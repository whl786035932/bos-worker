package cn.videoworks.worker.dto;

import java.util.List;

public class CommonExtraDataDto {
	
	private String broadCastTime;
	private List<CommonMediaDataDto> medias;
	private List<CommonPosterDto> posters;
	private String assetId;
	private  String channel;
	private String column;
	private String source;
	private String title;
	private Integer type;
	private String mediaAssetId;
	private String area;
	private List<String> tag;
	private  String programType1;
	
	public String getProgramType1() {
		return programType1;
	}
	public void setProgramType1(String programType1) {
		this.programType1 = programType1;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public List<String> getTag() {
		return tag;
	}
	public void setTag(List<String> tag) {
		this.tag = tag;
	}
	public String getAssetId() {
		return assetId;
	}
	public List<CommonMediaDataDto> getMedias() {
		return medias;
	}
	public void setMedias(List<CommonMediaDataDto> medias) {
		this.medias = medias;
	}
	public List<CommonPosterDto> getPosters() {
		return posters;
	}
	public void setPosters(List<CommonPosterDto> posters) {
		this.posters = posters;
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
