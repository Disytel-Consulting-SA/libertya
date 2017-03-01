package org.openXpertya.process.customImport.centralPos.pojos.amex.pagos;

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
	@SerializedName("tipo_reg")
	@Expose
	private String tipoReg;
	@SerializedName("importe_pago")
	@Expose
	private String importePago;
	@SerializedName("cod_banco")
	@Expose
	private String codBanco;
	@SerializedName("cod_suc_banc")
	@Expose
	private String codSucBanc;
	@SerializedName("num_cuenta_banc")
	@Expose
	private String numCuentaBanc;
	@SerializedName("nom_est")
	@Expose
	private String nomEst;
	@SerializedName("cod_moneda")
	@Expose
	private String codMoneda;
	@SerializedName("importe_deuda_ant")
	@Expose
	private String importeDeudaAnt;
	@SerializedName("imp_bruto_est")
	@Expose
	private String impBrutoEst;
	@SerializedName("imp_desc_pago")
	@Expose
	private String impDescPago;
	@SerializedName("imp_tot_impuestos")
	@Expose
	private String impTotImpuestos;
	@SerializedName("imp_tot_desc_acel")
	@Expose
	private String impTotDescAcel;
	@SerializedName("imp_neto_ajuste")
	@Expose
	private String impNetoAjuste;
	@SerializedName("estado_pago")
	@Expose
	private String estadoPago;
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

	public String getTipoReg() {
		return tipoReg;
	}

	public void setTipoReg(String tipoReg) {
		this.tipoReg = tipoReg;
	}

	public String getImportePago() {
		return importePago;
	}

	public void setImportePago(String importePago) {
		this.importePago = importePago;
	}

	public String getCodBanco() {
		return codBanco;
	}

	public void setCodBanco(String codBanco) {
		this.codBanco = codBanco;
	}

	public String getCodSucBanc() {
		return codSucBanc;
	}

	public void setCodSucBanc(String codSucBanc) {
		this.codSucBanc = codSucBanc;
	}

	public String getNumCuentaBanc() {
		return numCuentaBanc;
	}

	public void setNumCuentaBanc(String numCuentaBanc) {
		this.numCuentaBanc = numCuentaBanc;
	}

	public String getNomEst() {
		return nomEst;
	}

	public void setNomEst(String nomEst) {
		this.nomEst = nomEst;
	}

	public String getCodMoneda() {
		return codMoneda;
	}

	public void setCodMoneda(String codMoneda) {
		this.codMoneda = codMoneda;
	}

	public String getImporteDeudaAnt() {
		return importeDeudaAnt;
	}

	public void setImporteDeudaAnt(String importeDeudaAnt) {
		this.importeDeudaAnt = importeDeudaAnt;
	}

	public String getImpBrutoEst() {
		return impBrutoEst;
	}

	public void setImpBrutoEst(String impBrutoEst) {
		this.impBrutoEst = impBrutoEst;
	}

	public String getImpDescPago() {
		return impDescPago;
	}

	public void setImpDescPago(String impDescPago) {
		this.impDescPago = impDescPago;
	}

	public String getImpTotImpuestos() {
		return impTotImpuestos;
	}

	public void setImpTotImpuestos(String impTotImpuestos) {
		this.impTotImpuestos = impTotImpuestos;
	}

	public String getImpTotDescAcel() {
		return impTotDescAcel;
	}

	public void setImpTotDescAcel(String impTotDescAcel) {
		this.impTotDescAcel = impTotDescAcel;
	}

	public String getImpNetoAjuste() {
		return impNetoAjuste;
	}

	public void setImpNetoAjuste(String impNetoAjuste) {
		this.impNetoAjuste = impNetoAjuste;
	}

	public String getEstadoPago() {
		return estadoPago;
	}

	public void setEstadoPago(String estadoPago) {
		this.estadoPago = estadoPago;
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
