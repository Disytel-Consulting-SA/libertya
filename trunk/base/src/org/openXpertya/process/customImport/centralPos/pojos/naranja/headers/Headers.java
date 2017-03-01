
package org.openXpertya.process.customImport.centralPos.pojos.naranja.headers;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Headers extends Pojo {

	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("headers")
	@Expose
	private Headers_ headers;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Headers_ getHeaders() {
		return headers;
	}

	public void setHeaders(Headers_ headers) {
		this.headers = headers;
	}

}
