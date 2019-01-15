package cn.videoworks.worker.dto;

import java.util.List;

public class TencentDto {
	private List<MediaDto> medias;
	private List<PublishImageDto> posters;
	private String broadCastTime;
	private String filePath;
	private String assetId;
	private String mediaAssetId;
	private String infoFileNameExt;
	private String deployVideoName;
	private String targetPath;
	private String sourceFileExt;
	private String uploadFileName;// 第三步参数
	private List<String> voideoMessages;
	private String jsonMessage;

	private String staff;
	private String organization;
	private String ctype;
	private String coverid;
	private String coverinfos;
	private String clip;
	private String desc;
	private String playtime;
	private String title;
	private List<String> tags;
	private String columnname;
	private String full;
	private String startTime;
	private String publish_date;

	private String rid;

	private String subject;
	private String lsbc;
	
	private String channel;
	
	private String collection;
	private String second_title;

	/**
	 * @return the medias
	 */
	public List<MediaDto> getMedias() {
		return medias;
	}

	/**
	 * @param medias
	 *            the medias to set
	 */
	public void setMedias(List<MediaDto> medias) {
		this.medias = medias;
	}

	/**
	 * @return the posters
	 */
	public List<PublishImageDto> getPosters() {
		return posters;
	}

	/**
	 * @param posters
	 *            the posters to set
	 */
	public void setPosters(List<PublishImageDto> posters) {
		this.posters = posters;
	}

	/**
	 * @return the assetId
	 */
	public String getAssetId() {
		return assetId;
	}

	/**
	 * @param assetId
	 *            the assetId to set
	 */
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	/**
	 * @return the mediaAssetId
	 */
	public String getMediaAssetId() {
		return mediaAssetId;
	}

	/**
	 * @return the broadCastTime
	 */
	public String getBroadCastTime() {
		return broadCastTime;
	}

	/**
	 * @param broadCastTime
	 *            the broadCastTime to set
	 */
	public void setBroadCastTime(String broadCastTime) {
		this.broadCastTime = broadCastTime;
	}

	/**
	 * @param mediaAssetId
	 *            the mediaAssetId to set
	 */
	public void setMediaAssetId(String mediaAssetId) {
		this.mediaAssetId = mediaAssetId;
	}

	/**
	 * @return the infoFileNameExt
	 */
	public String getInfoFileNameExt() {
		return infoFileNameExt;
	}

	/**
	 * @param infoFileNameExt
	 *            the infoFileNameExt to set
	 */
	public void setInfoFileNameExt(String infoFileNameExt) {
		this.infoFileNameExt = infoFileNameExt;
	}

	/**
	 * @return the deployVideoName
	 */
	public String getDeployVideoName() {
		return deployVideoName;
	}

	/**
	 * @param deployVideoName
	 *            the deployVideoName to set
	 */
	public void setDeployVideoName(String deployVideoName) {
		this.deployVideoName = deployVideoName;
	}

	/**
	 * @return the targetPath
	 */
	public String getTargetPath() {
		return targetPath;
	}

	/**
	 * @param targetPath
	 *            the targetPath to set
	 */
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	/**
	 * @return the sourceFileExt
	 */
	public String getSourceFileExt() {
		return sourceFileExt;
	}

	/**
	 * @param sourceFileExt
	 *            the sourceFileExt to set
	 */
	public void setSourceFileExt(String sourceFileExt) {
		this.sourceFileExt = sourceFileExt;
	}

	/**
	 * @return the uploadFileName
	 */
	public String getUploadFileName() {
		return uploadFileName;
	}

	/**
	 * @param uploadFileName
	 *            the uploadFileName to set
	 */
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

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
	 * @return the jsonMessage
	 */
	public String getJsonMessage() {
		return jsonMessage;
	}

	/**
	 * @param jsonMessage
	 *            the jsonMessage to set
	 */
	public void setJsonMessage(String jsonMessage) {
		this.jsonMessage = jsonMessage;
	}

	/**
	 * @return the voideoMessages
	 */
	public List<String> getVoideoMessages() {
		return voideoMessages;
	}

	/**
	 * @param voideoMessages
	 *            the voideoMessages to set
	 */
	public void setVoideoMessages(List<String> voideoMessages) {
		this.voideoMessages = voideoMessages;
	}

	/**
	 * @return the staff
	 */
	public String getStaff() {
		return staff;
	}

	/**
	 * @param staff
	 *            the staff to set
	 */
	public void setStaff(String staff) {
		this.staff = staff;
	}

	/**
	 * @return the organization
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * @return the second_title
	 */
	public String getSecond_title() {
		return second_title;
	}

	/**
	 * @param second_title the second_title to set
	 */
	public void setSecond_title(String second_title) {
		this.second_title = second_title;
	}

	/**
	 * @return the collection
	 */
	public String getCollection() {
		return collection;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(String collection) {
		this.collection = collection;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the lsbc
	 */
	public String getLsbc() {
		return lsbc;
	}

	/**
	 * @param lsbc the lsbc to set
	 */
	public void setLsbc(String lsbc) {
		this.lsbc = lsbc;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the rid
	 */
	public String getRid() {
		return rid;
	}

	/**
	 * @param rid
	 *            the rid to set
	 */
	public void setRid(String rid) {
		this.rid = rid;
	}

	/**
	 * @return the publish_date
	 */
	public String getPublish_date() {
		return publish_date;
	}

	/**
	 * @param publish_date
	 *            the publish_date to set
	 */
	public void setPublish_date(String publish_date) {
		this.publish_date = publish_date;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @param organization
	 *            the organization to set
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * @return the ctype
	 */
	public String getCtype() {
		return ctype;
	}

	/**
	 * @param ctype
	 *            the ctype to set
	 */
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	/**
	 * @return the coverid
	 */
	public String getCoverid() {
		return coverid;
	}

	/**
	 * @param coverid
	 *            the coverid to set
	 */
	public void setCoverid(String coverid) {
		this.coverid = coverid;
	}

	/**
	 * @return the coverinfos
	 */
	public String getCoverinfos() {
		return coverinfos;
	}

	/**
	 * @param coverinfos
	 *            the coverinfos to set
	 */
	public void setCoverinfos(String coverinfos) {
		this.coverinfos = coverinfos;
	}

	/**
	 * @return the clip
	 */
	public String getClip() {
		return clip;
	}

	/**
	 * @param clip
	 *            the clip to set
	 */
	public void setClip(String clip) {
		this.clip = clip;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the playtime
	 */
	public String getPlaytime() {
		return playtime;
	}

	/**
	 * @param playtime
	 *            the playtime to set
	 */
	public void setPlaytime(String playtime) {
		this.playtime = playtime;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the columnname
	 */
	public String getColumnname() {
		return columnname;
	}

	/**
	 * @param columnname
	 *            the columnname to set
	 */
	public void setColumnname(String columnname) {
		this.columnname = columnname;
	}

	/**
	 * @return the full
	 */
	public String getFull() {
		return full;
	}

	/**
	 * @param full
	 *            the full to set
	 */
	public void setFull(String full) {
		this.full = full;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}
