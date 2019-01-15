package cn.videoworks.worker.dto;

import java.io.Serializable;

public class MediaAudioDto implements Serializable{

	public MediaAudioDto() {
		super();
	}
	
	private String format;
	private String profile;
	private Long bitrate;
	private Integer channels;
	private Integer samplingrate;
	private String lanuage;
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public MediaAudioDto(String format, String profile, Long bitrate, Integer channels, Integer samplingrate,
			String lanuage) {
		super();
		this.format = format;
		this.profile = profile;
		this.bitrate = bitrate;
		this.channels = channels;
		this.samplingrate = samplingrate;
		this.lanuage = lanuage;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public Long getBitrate() {
		return bitrate;
	}
	public void setBitrate(Long bitrate) {
		this.bitrate = bitrate;
	}
	public Integer getChannels() {
		return channels;
	}
	public void setChannels(Integer channels) {
		this.channels = channels;
	}
	public Integer getSamplingrate() {
		return samplingrate;
	}
	public void setSamplingrate(Integer samplingrate) {
		this.samplingrate = samplingrate;
	}
	public String getLanuage() {
		return lanuage;
	}
	public void setLanuage(String lanuage) {
		this.lanuage = lanuage;
	}

}
