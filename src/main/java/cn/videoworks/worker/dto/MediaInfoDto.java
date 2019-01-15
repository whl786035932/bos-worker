package cn.videoworks.worker.dto;

import java.io.Serializable;
import java.util.List;

public class MediaInfoDto  implements Serializable {

	public MediaInfoDto() {
		super();
	}
	
	private Boolean done;
	private MediaDataDto data;
	


	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public MediaDataDto getData() {
		return data;
	}

	public void setData(MediaDataDto data) {
		this.data = data;
	}

	
}
