package cn.videoworks.worker.dto;

import java.util.List;

public class YouKuV3Dto {
	private String filePath;//
	private String userid;// 用户id
	private String isBlock;//
	private String classificationA_int;//
	private String column;//
	private String playyear;//
	private String playmonth;//
	private String playdate;//
	private String classification;// 分类
	private String properTitle;// 标题
	private String keywords;//
	private String tags;//
	private List<String> tag;
	private String videostarttime;
	private String videoendtime;
	private String show_id;// 节目id
	private String cut_mode;// 剪辑方式
	private String taskid;
	private String content_main_thread;// 内容主线
	private String content_main_thread_int;// 内容主线
	private String video_cut_mode;// 周边剪辑方式
	private String video_cut_mode_int;// 周边剪辑方式
	private String show_relation;// 节目关系
	private String show_relation_int;//
	private String remain_video_type;// 视频二级标签
	private String remain_video_type_int;//
	private String vtype_mark;// 二级标签
	private String vtype_mark_int;
	private String stage;// 集数
	private String channel;//
	private String deployDate;//
	private List<MediaDto> medias;
	private String broadCastTime;//
	private String assetId;
	private String mediaAssetId;
	private String cutType;
	private String source;
	private String title;
	private String type;
	private List<PublishImageDto>posters;
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
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 *            the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @return the isBlock
	 */
	public String getIsBlock() {
		return isBlock;
	}

	/**
	 * @param isBlock
	 *            the isBlock to set
	 */
	public void setIsBlock(String isBlock) {
		this.isBlock = isBlock;
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords
	 *            the keywords to set
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the classificationA_int
	 */
	public String getClassificationA_int() {
		return classificationA_int;
	}

	/**
	 * @param classificationA_int
	 *            the classificationA_int to set
	 */
	public void setClassificationA_int(String classificationA_int) {
		this.classificationA_int = classificationA_int;
	}

	// /**
	// * @return the col_alias
	// */
	// public String getCol_alias() {
	// return col_alias;
	// }
	//
	// /**
	// * @param col_alias
	// * the col_alias to set
	// */
	// public void setCol_alias(String col_alias) {
	// this.col_alias = col_alias;
	// }

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

	// /**
	// * @return the properTitleA
	// */
	// public String getProperTitleA() {
	// return properTitleA;
	// }
	//
	// /**
	// * @param properTitleA
	// * the properTitleA to set
	// */
	// public void setProperTitleA(String properTitleA) {
	// this.properTitleA = properTitleA;
	// }

	/**
	 * @return the classification
	 */
	public String getClassification() {
		return classification;
	}

	/**
	 * @param classification
	 *            the classification to set
	 */
	public void setClassification(String classification) {
		this.classification = classification;
	}

	/**
	 * @return the properTitle
	 */
	/*
	 * public String getProperTitle() { return properTitle; }
	 *//**
	 * @param properTitle
	 *            the properTitle to set
	 */
	/*
	 * public void setProperTitle(String properTitle) { this.properTitle =
	 * properTitle; }
	 */

	// /**
	// * @return the classificationA
	// */
	// public String getClassificationA() {
	// return classificationA;
	// }
	//
	// /**
	// * @param classificationA
	// * the classificationA to set
	// */
	// public void setClassificationA(String classificationA) {
	// this.classificationA = classificationA;
	// }

	/**
	 * @return the videostarttime
	 */
	public String getVideostarttime() {
		return videostarttime;
	}

	/**
	 * @param videostarttime
	 *            the videostarttime to set
	 */
	public void setVideostarttime(String videostarttime) {
		this.videostarttime = videostarttime;
	}

	/**
	 * @return the videoendtime
	 */
	public String getVideoendtime() {
		return videoendtime;
	}

	/**
	 * @return the content_main_thread
	 */
	public String getContent_main_thread() {
		return content_main_thread;
	}

	/**
	 * @return the video_cut_mode
	 */
	public String getVideo_cut_mode() {
		return video_cut_mode;
	}

	/**
	 * @param video_cut_mode
	 *            the video_cut_mode to set
	 */
	public void setVideo_cut_mode(String video_cut_mode) {
		this.video_cut_mode = video_cut_mode;
	}

	/**
	 * @param content_main_thread
	 *            the content_main_thread to set
	 */
	public void setContent_main_thread(String content_main_thread) {
		this.content_main_thread = content_main_thread;
	}

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
	 * @param videoendtime
	 *            the videoendtime to set
	 */
	public void setVideoendtime(String videoendtime) {
		this.videoendtime = videoendtime;
	}

	/**
	 * @return the show_id
	 */
	public String getShow_id() {
		return show_id;
	}

	/**
	 * @param show_id
	 *            the show_id to set
	 */
	public void setShow_id(String show_id) {
		this.show_id = show_id;
	}

	/**
	 * @return the cut_mode
	 */
	public String getCut_mode() {
		return cut_mode;
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
	 * @param mediaAssetId
	 *            the mediaAssetId to set
	 */
	public void setMediaAssetId(String mediaAssetId) {
		this.mediaAssetId = mediaAssetId;
	}

	/**
	 * @return the broadCastTime
	 */
	public String getBroadCastTime() {
		return broadCastTime;
	}

	/**
	 * @return the tag
	 */
	public List<String> getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(List<String> tag) {
		this.tag = tag;
	}

	/**
	 * @param broadCastTime
	 *            the broadCastTime to set
	 */
	public void setBroadCastTime(String broadCastTime) {
		this.broadCastTime = broadCastTime;
	}

	/**
	 * @param cut_mode
	 *            the cut_mode to set
	 */
	public void setCut_mode(String cut_mode) {
		this.cut_mode = cut_mode;
	}

	/**
	 * @return the taskid
	 */
	public String getTaskid() {
		return taskid;
	}

	/**
	 * @param taskid
	 *            the taskid to set
	 */
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	/**
	 * @return the content_main_thread_int
	 */
	public String getContent_main_thread_int() {
		return content_main_thread_int;
	}

	/**
	 * @param content_main_thread_int
	 *            the content_main_thread_int to set
	 */
	public void setContent_main_thread_int(String content_main_thread_int) {
		this.content_main_thread_int = content_main_thread_int;
	}

	/**
	 * @return the video_cut_mode_int
	 */
	public String getVideo_cut_mode_int() {
		return video_cut_mode_int;
	}

	/**
	 * @param video_cut_mode_int
	 *            the video_cut_mode_int to set
	 */
	public void setVideo_cut_mode_int(String video_cut_mode_int) {
		this.video_cut_mode_int = video_cut_mode_int;
	}

	/**
	 * @return the show_relation_int
	 */
	public String getShow_relation_int() {
		return show_relation_int;
	}

	/**
	 * @param show_relation_int
	 *            the show_relation_int to set
	 */
	public void setShow_relation_int(String show_relation_int) {
		this.show_relation_int = show_relation_int;
	}

	/**
	 * @return the remain_video_type_int
	 */
	public String getRemain_video_type_int() {
		return remain_video_type_int;
	}

	/**
	 * @param remain_video_type_int
	 *            the remain_video_type_int to set
	 */
	public void setRemain_video_type_int(String remain_video_type_int) {
		this.remain_video_type_int = remain_video_type_int;
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
	 * @return the vtype_mark_int
	 */
	public String getVtype_mark_int() {
		return vtype_mark_int;
	}

	/**
	 * @param vtype_mark_int
	 *            the vtype_mark_int to set
	 */
	public void setVtype_mark_int(String vtype_mark_int) {
		this.vtype_mark_int = vtype_mark_int;
	}

	/**
	 * @return the stage
	 */
	public String getStage() {
		return stage;
	}

	/**
	 * @param stage
	 *            the stage to set
	 */
	public void setStage(String stage) {
		this.stage = stage;
	}

	/**
	 * @return the channelName
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channelName
	 *            the channelName to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the deployDate
	 */
	public String getDeployDate() {
		return deployDate;
	}

	/**
	 * @param deployDate
	 *            the deployDate to set
	 */
	public void setDeployDate(String deployDate) {
		this.deployDate = deployDate;
	}

	// /**
	// * @return the keywords
	// */
	// public String getKeywords() {
	// return keywords;
	// }
	//
	// /**
	// * @param keywords the keywords to set
	// */
	// public void setKeywords(String keywords) {
	// this.keywords = keywords;
	// }

	/**
	 * @return the show_relation
	 */
	public String getShow_relation() {
		return show_relation;
	}

	/**
	 * @param show_relation
	 *            the show_relation to set
	 */
	public void setShow_relation(String show_relation) {
		this.show_relation = show_relation;
	}

	/**
	 * @return the remain_video_type
	 */
	public String getRemain_video_type() {
		return remain_video_type;
	}

	/**
	 * @param remain_video_type
	 *            the remain_video_type to set
	 */
	public void setRemain_video_type(String remain_video_type) {
		this.remain_video_type = remain_video_type;
	}

	/**
	 * @return the vtype_mark
	 */
	public String getVtype_mark() {
		return vtype_mark;
	}

	/**
	 * @param vtype_mark
	 *            the vtype_mark to set
	 */
	public void setVtype_mark(String vtype_mark) {
		this.vtype_mark = vtype_mark;
	}

	/**
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * @return the properTitle
	 */
	public String getProperTitle() {
		return properTitle;
	}

	/**
	 * @param properTitle
	 *            the properTitle to set
	 */
	public void setProperTitle(String properTitle) {
		this.properTitle = properTitle;
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
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the posters
	 */
	public List<PublishImageDto> getPosters() {
		return posters;
	}

	/**
	 * @param posters the posters to set
	 */
	public void setPosters(List<PublishImageDto> posters) {
		this.posters = posters;
	}
}
