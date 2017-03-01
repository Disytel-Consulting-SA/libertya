
package org.openXpertya.process.customImport.centralPos.pojos.firstdata.trailer;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trailer extends Pojo {

	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("trailer_participantes")
	@Expose
	private TrailerParticipantes trailerParticipantes;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public TrailerParticipantes getTrailerParticipantes() {
		return trailerParticipantes;
	}

	public void setTrailerParticipantes(TrailerParticipantes trailerParticipantes) {
		this.trailerParticipantes = trailerParticipantes;
	}

}
