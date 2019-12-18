package org.openXpertya.process.customImport.centralPos.pojos.cabal.retenciones;

import java.util.List;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CabalRetenciones extends Pojo {

	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("retenciones")
	@Expose
	private List<Datum> retenciones;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public List<Datum> getRetenciones() {
		return retenciones;
	}

	public void setRetenciones(List<Datum> retenciones) {
		this.retenciones = retenciones;
	}

}
