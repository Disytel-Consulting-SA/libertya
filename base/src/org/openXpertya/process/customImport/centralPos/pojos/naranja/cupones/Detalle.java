
package org.openXpertya.process.customImport.centralPos.pojos.naranja.cupones;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Detalle extends Pojo {

	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("cupones")
	@Expose
	private Cupones cupones;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Cupones getCupones() {
		return cupones;
	}

	public void setCupones(Cupones cupones) {
		this.cupones = cupones;
	}

}
