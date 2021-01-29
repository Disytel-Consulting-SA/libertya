package org.openXpertya.util;

import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacturaElectronicaQRData {

	@SerializedName("ver")
	@Expose
	private Integer ver;
	@SerializedName("fecha")
	@Expose
	private String fecha;
	@SerializedName("cuit")
	@Expose
	private BigInteger cuit;
	@SerializedName("ptoVta")
	@Expose
	private Integer ptoVta;
	@SerializedName("tipoCmp")
	@Expose
	private Integer tipoCmp;
	@SerializedName("nroCmp")
	@Expose
	private Integer nroCmp;
	@SerializedName("importe")
	@Expose
	private Double importe;
	@SerializedName("moneda")
	@Expose
	private String moneda;
	@SerializedName("ctz")
	@Expose
	private Double ctz;
	@SerializedName("tipoDocRec")
	@Expose
	private Integer tipoDocRec;
	@SerializedName("nroDocRec")
	@Expose
	private BigInteger nroDocRec;
	@SerializedName("tipoCodAut")
	@Expose
	private String tipoCodAut;
	@SerializedName("codAut")
	@Expose
	private BigInteger codAut;

	public Integer getVer() {
		return ver;
	}

	public void setVer(Integer ver) {
		this.ver = ver;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	public Integer getPtoVta() {
		return ptoVta;
	}

	public void setPtoVta(Integer ptoVta) {
		this.ptoVta = ptoVta;
	}

	public Integer getTipoCmp() {
		return tipoCmp;
	}

	public void setTipoCmp(Integer tipoCmp) {
		this.tipoCmp = tipoCmp;
	}

	public Integer getNroCmp() {
		return nroCmp;
	}

	public void setNroCmp(Integer nroCmp) {
		this.nroCmp = nroCmp;
	}

	public Double getImporte() {
		return importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public Double getCtz() {
		return ctz;
	}

	public void setCtz(Double ctz) {
		this.ctz = ctz;
	}

	public Integer getTipoDocRec() {
		return tipoDocRec;
	}

	public void setTipoDocRec(Integer tipoDocRec) {
		this.tipoDocRec = tipoDocRec;
	}

	public String getTipoCodAut() {
		return tipoCodAut;
	}

	public void setTipoCodAut(String tipoCodAut) {
		this.tipoCodAut = tipoCodAut;
	}

	public BigInteger getCuit() {
		return cuit;
	}

	public void setCuit(BigInteger cuit) {
		this.cuit = cuit;
	}

	public BigInteger getNroDocRec() {
		return nroDocRec;
	}

	public void setNroDocRec(BigInteger nroDocRec) {
		this.nroDocRec = nroDocRec;
	}

	public BigInteger getCodAut() {
		return codAut;
	}

	public void setCodAut(BigInteger codAut) {
		this.codAut = codAut;
	}

}