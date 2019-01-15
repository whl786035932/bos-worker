package cn.videoworks.worker.dto;

/**
 * 网易发布的入库任务的dto
 * 
 * @author whl
 *
 */
public class CommonWorkerDto {
	// 发布模板
	private String publishId;

	private String customerName;



	// 任务的优先级
	private String priority;
	private Integer dataTransferStatus;

	private CommonExtraDataDto extraData;



	public String getPublishId() {
		return publishId;
	}


	public void setPublishId(String publishId) {
		this.publishId = publishId;
	}


	public String getCustomerName() {
		return customerName;
	}


	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}


	public Integer getDataTransferStatus() {
		return dataTransferStatus;
	}


	public void setDataTransferStatus(Integer dataTransferStatus) {
		this.dataTransferStatus = dataTransferStatus;
	}

	



	public String getPriority() {
		return priority;
	}





	public CommonExtraDataDto getExtraData() {
		return extraData;
	}


	public void setExtraData(CommonExtraDataDto extraData) {
		this.extraData = extraData;
	}


	public void setPriority(String priority) {
		this.priority = priority;
	}


}
