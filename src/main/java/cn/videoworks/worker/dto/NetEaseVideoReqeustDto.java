package cn.videoworks.worker.dto;
/**
 * 视频上报网易的请求体
 * @author whl
 *
 */
public class NetEaseVideoReqeustDto {

	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Integer getFull_view() {
		return full_view;
	}
	public void setFull_view(Integer full_view) {
		this.full_view = full_view;
	}
	public String getPic_url() {
		return pic_url;
	}
	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}
	public Integer getCategory_id() {
		return category_id;
	}
	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getIsoriginal() {
		return isoriginal;
	}
	public void setIsoriginal(Integer isoriginal) {
		this.isoriginal = isoriginal;
	}
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getDrama_name() {
		return drama_name;
	}
	public void setDrama_name(String drama_name) {
		this.drama_name = drama_name;
	}
	private String description;
	private String tag;
	private Integer full_view=0;
	private String pic_url;
	private Integer category_id;
	private String url;
	private Integer isoriginal=0;
	private String area_code;
	private String access_token;
	private String drama_name;
	
	
	
}
