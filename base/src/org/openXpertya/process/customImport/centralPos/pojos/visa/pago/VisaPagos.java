
package org.openXpertya.process.customImport.centralPos.pojos.visa.pago;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VisaPagos extends Pojo {

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
