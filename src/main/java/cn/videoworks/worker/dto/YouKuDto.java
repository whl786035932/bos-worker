package cn.videoworks.worker.dto;

import java.util.List;

public class YouKuDto {
	private String filePath;//
	private String title;//
	private String cutType;//
	private String column;//
	private String playyear;//
	private String playmonth;//
	private String playdate;//
	private String tags;//
	private String col_alias;
	private String classification;//
	private String userid;//
	private String isBlock;//
	//private String ip;
	private List<MediaDto> medias;
	private String broadCastTime;//
	private String assetId;
	private String mediaAssetId;
	private String keywords;
	private String properTitle;

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath
	 *            the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the cutType
	 */
	public String getCutType() {
		return cutType;
	}

	/**
	 * @param cutType
	 *            the cutType to set
	 */
	public void setCutType(String cutType) {
		this.cutType = cutType;
	}

	/**
	 * @return the column
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * @param column
	 *            the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * @return the playyear
	 */
	public String getPlayyear() {
		return playyear;
	}

	/**
	 * @param playyear
	 *            the playyear to set
	 */
	public void setPlayyear(String playyear) {
		this.playyear = playyear;
	}

	/**
	 * @return the playmonth
	 */
	public String getPlaymonth() {
		return playmonth;
	}

	/**
	 * @param playmonth
	 *            the playmonth to set
	 */
	public void setPlaymonth(String playmonth) {
		this.playmonth = playmonth;
	}

	/**
	 * @return the playdate
	 */
	public String getPlaydate() {
		return playdate;
	}

	/**
	 * @param playdate
	 *            the playdate to set
	 */
	public void setPlaydate(String playdate) {
		this.playdate = playdate;
	}


	/**
	 * @return the col_alias
	 */
	public String getCol_alias() {
		return col_alias;
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the properTitle
	 */
	public String getProperTitle() {
		return properTitle;
	}

	/**
	 * @param properTitle the properTitle to set
	 */
	public void setProperTitle(String properTitle) {
		this.properTitle = properTitle;
	}

	/**
	 * @param col_alias
	 *            the col_alias to set
	 */
	public void setCol_alias(String col_alias) {
		this.col_alias = col_alias;
	}

	/**
	 * @return the classification
	 */
	public String getClassification() {
		return classification;
	}

	/**
	 * @param classification the classification to set
	 */
	public void setClassification(String classification) {
		this.classification = classification;
	}

	/**
	 * @return the isBlock
	 */
	public String getIsBlock() {
		return isBlock;
	}

	/**
	 * @param isBlock the isBlock to set
	 */
	public void setIsBlock(String isBlock) {
		this.isBlock = isBlock;
	}

	/**
	 * @return the medias
	 */
	public List<MediaDto> getMedias() {
		return medias;
	}

	/**
	 * @param medias the medias to set
	 */
	public void setMedias(List<MediaDto> medias) {
		this.medias = medias;
	}

	/**
	 * @return the broadCastTime
	 */
	public String getBroadCastTime() {
		return broadCastTime;
	}

	/**
	 * @param broadCastTime the broadCastTime to set
	 */
	public void setBroadCastTime(String broadCastTime) {
		this.broadCastTime = broadCastTime;
	}

	/**
	 * @return the mediaAssetId
	 */
	public String getMediaAssetId() {
		return mediaAssetId;
	}

	/**
	 * @param mediaAssetId the mediaAssetId to set
	 */
	public void setMediaAssetId(String mediaAssetId) {
		this.mediaAssetId = mediaAssetId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * @return the assetId
	 */
	public String getAssetId() {
		return assetId;
	}

	/**
	 * @param assetId the assetId to set
	 */
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}
}
