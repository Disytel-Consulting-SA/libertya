package org.openXpertya.process.customImport.centralPos.pojos.amex.pagos;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AmexPagos extends Pojo {

	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("pagos")
	@Expose
	private Pagos pagos;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Pagos getPagos() {
		return pagos;
	}

	public void setPagos(Pagos pagos) {
		this.pagos = pagos;
	}

}
