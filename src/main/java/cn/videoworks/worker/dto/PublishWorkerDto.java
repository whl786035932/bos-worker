package cn.videoworks.worker.dto;

/**
 * bos发布的入库任务的dto
 * 
 * @author whl
 *
 */
public class PublishWorkerDto {
	// 发布模板
	private String publishId;

	private String customerName;



	// 任务的优先级
	private String priority;
	private Integer dataTransferStatus;

	private PublishStorageDto extraData;



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


	public PublishStorageDto getExtraData() {
		return extraData;
	}


	public void setExtraData(PublishStorageDto extraData) {
		this.extraData = extraData;
	}



	

	public String getPriority() {
		return priority;
	}





	public void setPriority(String priority) {
		this.priority = priority;
	}


}
