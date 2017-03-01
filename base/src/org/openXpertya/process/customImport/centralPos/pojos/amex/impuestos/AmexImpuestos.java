package org.openXpertya.process.customImport.centralPos.pojos.amex.impuestos;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AmexImpuestos extends Pojo {

	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("impuestos")
	@Expose
	private Impuestos impuestos;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Impuestos getImpuestos() {
		return impuestos;
	}

	public void setImpuestos(Impuestos impuestos) {
		this.impuestos = impuestos;
	}

}
