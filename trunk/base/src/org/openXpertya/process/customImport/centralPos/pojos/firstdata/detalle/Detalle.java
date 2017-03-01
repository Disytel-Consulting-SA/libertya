
package org.openXpertya.process.customImport.centralPos.pojos.firstdata.detalle;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Detalle extends Pojo {

	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("liquidacion_participantes")
	@Expose
	private LiquidacionParticipantes liquidacionParticipantes;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public LiquidacionParticipantes getLiquidacionParticipantes() {
		return liquidacionParticipantes;
	}

	public void setLiquidacionParticipantes(LiquidacionParticipantes liquidacionParticipantes) {
		this.liquidacionParticipantes = liquidacionParticipantes;
	}

}
