package cn.videoworks.worker.dto;

import java.util.List;
/***
 * cms的入库dto,入库到CMS
 * @author whl
 *
 */
public class CmsStorageDto {
	
	private Integer asset_id;
	private String title;
	
	private String title_abbr;
	
	private Integer type;
	private String description;
	
	private Long publish_time;
	private List<String> tags;
	
	private String cp;
	private String source;
	
	private String source_channel;
	
	private String  source_column;
	
	private List<CmsMovieDto> movies;
	
	private List<CmsImageDto> images;
	
	
	private List<String> classifications;
	private List<String> areas;
	public List<String> getClassifications() {
		return classifications;
	}

	public void setClassifications(List<String> classifications) {
		this.classifications = classifications;
	}

	public List<String> getAreas() {
		return areas;
	}

	public void setAreas(List<String> areas) {
		this.areas = areas;
	}

	public Integer getAsset_id() {
		return asset_id;
	}

	public void setAsset_id(Integer asset_id) {
		this.asset_id = asset_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle_abbr() {
		return title_abbr;
	}

	public void setTitle_abbr(String title_abbr) {
		this.title_abbr = title_abbr;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPublish_time() {
		return publish_time;
	}

	public void setPublish_time(Long publish_time) {
		this.publish_time = publish_time;
	}

	public List<CmsMovieDto> getMovies() {
		return movies;
	}

	public void setMovies(List<CmsMovieDto> movies) {
		this.movies = movies;
	}

	public List<CmsImageDto> getImages() {
		return images;
	}

	public void setImages(List<CmsImageDto> images) {
		this.images = images;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource_channel() {
		return source_channel;
	}

	public void setSource_channel(String source_channel) {
		this.source_channel = source_channel;
	}

	public String getSource_column() {
		return source_column;
	}

	public void setSource_column(String source_column) {
		this.source_column = source_column;
	}


	
}
