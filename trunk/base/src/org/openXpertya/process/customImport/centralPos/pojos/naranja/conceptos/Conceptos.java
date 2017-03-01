
package org.openXpertya.process.customImport.centralPos.pojos.naranja.conceptos;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Conceptos extends Pojo {

	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("conceptos_facturados_meses")
	@Expose
	private ConceptosFacturadosMeses conceptosFacturadosMeses;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public ConceptosFacturadosMeses getConceptosFacturadosMeses() {
		return conceptosFacturadosMeses;
	}

	public void setConceptosFacturadosMeses(ConceptosFacturadosMeses conceptosFacturadosMeses) {
		this.conceptosFacturadosMeses = conceptosFacturadosMeses;
	}

}
