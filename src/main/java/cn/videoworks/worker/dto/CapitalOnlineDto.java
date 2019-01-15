package cn.videoworks.worker.dto;

import java.io.Serializable;

/**
 * 首都在线的funcation的data
 */
public class CapitalOnlineDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//文件的MD5值
	private String md5;
	public CapitalOnlineDto(String md5, String column_alias, String year, String month, String date, String hour,
			String minute, String second, String url, String assetId) {
		super();
		this.md5 = md5;
		this.column_alias = column_alias;
		this.year = year;
		this.month = month;
		this.date = date;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.url = url;
		this.assetId = assetId;
	}

	//栏目的别名
	private String column_alias;
	//年
	private String year;
	//月
	private String month;

	//日
	private String date;

	//时
	private String hour;

	public Integer getVideoType() {
		return videoType;
	}

	public void setVideoType(Integer videoType) {
		this.videoType = videoType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	//分
	private String minute;

	//秒
	private String second;

	//ts文件的路径，可能是本地路径也可能是对象存储的路径
	private String url;
	//媒资Id
	private String assetId;
	private Integer videoType;

	public CapitalOnlineDto() {
		
	}
	
	public String getAssetId() {
		return assetId;
	}
	

	public String getColumn_alias() {
		return column_alias;
	}

	public String getDate() {
		return date;
	}

	public String getHour() {
		return hour;
	}

	public String getMd5() {
		return md5;
	}

	public String getMinute() {
		return minute;
	}

	public String getMonth() {
		return month;
	}

	public String getSecond() {
		return second;
	}

	public String getUrl() {
		return url;
	}

	public String getYear() {
		return year;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public void setColumn_alias(String column_alias) {
		this.column_alias = column_alias;
	}

	public void setDate(String date) {
		this.date = date;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public void setMinute(String minute) {
		this.minute = minute;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	
	public void setSecond(String second) {
		this.second = second;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setYear(String year) {
		this.year = year;
	}
	
	
	

}
