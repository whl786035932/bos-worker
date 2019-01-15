package cn.videoworks.worker.dto;

import java.util.List;

public class JRTTDto {
	private List<MediaDto> medias;
	private List<PublishImageDto> posters;
	private String assetId;
	private String mediaAssetId;
	private String sourceFileExt;
	private String address;
	private String uploadFileName;
	private String sourceFile;
	private String infoContent;
	private String deployVideoName;
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
	 * @param mediaAssetId
	 *            the mediaAssetId to set
	 */
	public void setMediaAssetId(String mediaAssetId) {
		this.mediaAssetId = mediaAssetId;
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
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
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
	 * @return the sourceFile
	 */
	public String getSourceFile() {
		return sourceFile;
	}

	/**
	 * @param sourceFile
	 *            the sourceFile to set
	 */
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * @return the infoContent
	 */
	public String getInfoContent() {
		return infoContent;
	}

	/**
	 * @param infoContent
	 *            the infoContent to set
	 */
	public void setInfoContent(String infoContent) {
		this.infoContent = infoContent;
	}

	/**
	 * @return the deployVideoName
	 */
	public String getDeployVideoName() {
		return deployVideoName;
	}

	/**
	 * @param deployVideoName the deployVideoName to set
	 */
	public void setDeployVideoName(String deployVideoName) {
		this.deployVideoName = deployVideoName;
	}
}
