package cn.videoworks.worker.dto;

import java.io.Serializable;

public class MediaVideoDto implements Serializable{
	private String format;
	private String profile;
	private String scan_type;
	private Integer height;
	private Integer width;
	private String aspect_ratio;
	private String chroma_subsampling;
	private Integer framerate;
	public MediaVideoDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MediaVideoDto(String format, String profile, String scan_type, Integer height, Integer width,
			String aspect_ratio, String chroma_subsampling, Integer framerate, String colorspace, Double bitrate) {
		super();
		this.format = format;
		this.profile = profile;
		this.scan_type = scan_type;
		this.height = height;
		this.width = width;
		this.aspect_ratio = aspect_ratio;
		this.chroma_subsampling = chroma_subsampling;
		this.framerate = framerate;
		this.colorspace = colorspace;
		this.bitrate = bitrate;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getScan_type() {
		return scan_type;
	}
	public void setScan_type(String scan_type) {
		this.scan_type = scan_type;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public String getAspect_ratio() {
		return aspect_ratio;
	}
	public void setAspect_ratio(String aspect_ratio) {
		this.aspect_ratio = aspect_ratio;
	}
	public String getChroma_subsampling() {
		return chroma_subsampling;
	}
	public void setChroma_subsampling(String chroma_subsampling) {
		this.chroma_subsampling = chroma_subsampling;
	}
	public Integer getFramerate() {
		return framerate;
	}
	public void setFramerate(Integer framerate) {
		this.framerate = framerate;
	}
	public String getColorspace() {
		return colorspace;
	}
	public void setColorspace(String colorspace) {
		this.colorspace = colorspace;
	}
	public Double getBitrate() {
		return bitrate;
	}
	public void setBitrate(Double bitrate) {
		this.bitrate = bitrate;
	}
	private String colorspace;
	private Double bitrate;

}
