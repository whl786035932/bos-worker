package cn.videoworks.worker.dto;

public class DataDto {
	private String publishId;

	private String customerName;

	private String priority;

	private Integer dataTransferStatus;

	private Object extraData;

	/**
	 * @return the publishId
	 */
	public String getPublishId() {
		return publishId;
	}

	/**
	 * @param publishId the publishId to set
	 */
	public void setPublishId(String publishId) {
		this.publishId = publishId;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * @return the dataTransferStatus
	 */
	public Integer getDataTransferStatus() {
		return dataTransferStatus;
	}

	/**
	 * @param dataTransferStatus the dataTransferStatus to set
	 */
	public void setDataTransferStatus(Integer dataTransferStatus) {
		this.dataTransferStatus = dataTransferStatus;
	}

	/**
	 * @return the extraData
	 */
	public Object getExtraData() {
		return extraData;
	}

	/**
	 * @param extraData the extraData to set
	 */
	public void setExtraData(Object extraData) {
		this.extraData = extraData;
	}

}
