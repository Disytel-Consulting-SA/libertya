package org.openXpertya.process.customImport.centralPos.pojos.cabal.movimientos;

import java.util.List;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CabalMovimientos extends Pojo {

	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("movimientos")
	@Expose
	private List<Datum> movimientos;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public List<Datum> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<Datum> movimientos) {
		this.movimientos = movimientos;
	}

	
}
