package cn.videoworks.worker.dto;

import java.io.Serializable;
import java.util.List;

/***
 * cms的入库dto
 * 
 * @author whl
 *
 */
public class PublishStorageDto implements Serializable {

	private String bianji;
	private Integer number;
	private Integer type;
	private String mediaAssetId;
	private Integer assetId;
	private List<String> langeage;
	private String title;
	private List<String> tree;


	private String source;

	private String description;

	private List<PublishMovieDto> medias;

	private String column;

	private String datetime;

	private String channel;

	private List<PublishImageDto> posters;

	private String broadCastTime;

	//---可能会有得字段
	private String title_abbr;

	private List<String> tags;

	private List<String> classifications;

	private List<String> areas;

	private String cp;

	public PublishStorageDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<String> getAreas() {
		return areas;
	}

	public Integer getAssetId() {
		return assetId;
	}

	/**
	 * below field is own defined
	 */



	public String getBianji() {
		return bianji;
	}
	
	
	public String getBroadCastTime() {
		return broadCastTime;
	}
	public String getChannel() {
		return channel;
	}
	
	public List<String> getClassifications() {
		return classifications;
	}
	public String getColumn() {
		return column;
	}
	

	public String getCp() {
		return cp;
	}
	
	public String getDatetime() {
		return datetime;
	}
	

	public String getDescription() {
		return description;
	}

	public List<String> getLangeage() {
		return langeage;
	}

	public String getMediaAssetId() {
		return mediaAssetId;
	}

	public List<PublishMovieDto> getMedias() {
		return medias;
	}

	public Integer getNumber() {
		return number;
	}

	public List<PublishImageDto> getPosters() {
		return posters;
	}


	public String getSource() {
		return source;
	}

	public List<String> getTags() {
		return tags;
	}
	public String getTitle() {
		return title;
	}


	public String getTitle_abbr() {
		return title_abbr;
	}
	public List<String> getTree() {
		return tree;
	}
	public Integer getType() {
		return type;
	}

	public void setAreas(List<String> areas) {
		this.areas = areas;
	}
	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}

	public void setBianji(String bianji) {
		this.bianji = bianji;
	}


	public void setBroadCastTime(String broadCastTime) {
		this.broadCastTime = broadCastTime;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}



	public void setClassifications(List<String> classifications) {
		this.classifications = classifications;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}


	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	public void setLangeage(List<String> langeage) {
		this.langeage = langeage;
	}

	public void setMediaAssetId(String mediaAssetId) {
		this.mediaAssetId = mediaAssetId;
	}

	public void setMedias(List<PublishMovieDto> medias) {
		this.medias = medias;
	}



	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setPosters(List<PublishImageDto> posters) {
		this.posters = posters;
	}


	public void setSource(String source) {
		this.source = source;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}


	public void setTitle(String title) {
		this.title = title;
	}




	public void setTitle_abbr(String title_abbr) {
		this.title_abbr = title_abbr;
	}


	public void setTree(List<String> tree) {
		this.tree = tree;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
