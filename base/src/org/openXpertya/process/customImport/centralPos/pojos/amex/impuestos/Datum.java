
package org.openXpertya.process.customImport.centralPos.pojos.amex.impuestos;

import org.openXpertya.process.customImport.centralPos.pojos.GenericDatum;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum extends GenericDatum {

	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("archivo_id")
	@Expose
	private String archivoId;
	@SerializedName("num_est")
	@Expose
	private String numEst;
	@SerializedName("fecha_pago")
	@Expose
	private String fechaPago;
	@SerializedName("num_sec_pago")
	@Expose
	private String numSecPago;
	@SerializedName("tipo_registro")
	@Expose
	private String tipoRegistro;
	@SerializedName("cod_imp")
	@Expose
	private String codImp;
	@SerializedName("cod_imp_desc")
	@Expose
	private String codImpDesc;
	@SerializedName("base_imp")
	@Expose
	private String baseImp;
	@SerializedName("fecha_imp")
	@Expose
	private String fechaImp;
	@SerializedName("porc_imp")
	@Expose
	private String porcImp;
	@SerializedName("importe_imp")
	@Expose
	private String importeImp;
	@SerializedName("hash_modelo")
	@Expose
	private String hashModelo;
	@SerializedName("revisado")
	@Expose
	private String revisado;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArchivoId() {
		return archivoId;
	}

	public void setArchivoId(String archivoId) {
		this.archivoId = archivoId;
	}

	public String getNumEst() {
		return numEst;
	}

	public void setNumEst(String numEst) {
		this.numEst = numEst;
	}

	public String getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(String fechaPago) {
		this.fechaPago = fechaPago;
	}

	public String getNumSecPago() {
		return numSecPago;
	}

	public void setNumSecPago(String numSecPago) {
		this.numSecPago = numSecPago;
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public String getCodImp() {
		return codImp;
	}

	public void setCodImp(String codImp) {
		this.codImp = codImp;
	}

	public String getCodImpDesc() {
		return codImpDesc;
	}

	public void setCodImpDesc(String codImpDesc) {
		this.codImpDesc = codImpDesc;
	}

	public String getBaseImp() {
		return baseImp;
	}

	public void setBaseImp(String baseImp) {
		this.baseImp = baseImp;
	}

	public String getFechaImp() {
		return fechaImp;
	}

	public void setFechaImp(String fechaImp) {
		this.fechaImp = fechaImp;
	}

	public String getPorcImp() {
		return porcImp;
	}

	public void setPorcImp(String porcImp) {
		this.porcImp = porcImp;
	}

	public String getImporteImp() {
		return importeImp;
	}

	public void setImporteImp(String importeImp) {
		this.importeImp = importeImp;
	}

	public String getHashModelo() {
		return hashModelo;
	}

	public void setHashModelo(String hashModelo) {
		this.hashModelo = hashModelo;
	}

	public String getRevisado() {
		return revisado;
	}

	public void setRevisado(String revisado) {
		this.revisado = revisado;
	}

}
