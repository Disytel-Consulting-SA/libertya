package org.openXpertya.process.customImport.centralPos.pojos.cabal.retenciones;

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
	@SerializedName("comercio")
	@Expose
	private String comercio;
	@SerializedName("revisado")
	@Expose
	private String revisado;
	@SerializedName("numero_comercio")
	@Expose
	private String numero_comercio;
	@SerializedName("fecha_pago")
	@Expose
	private String fecha_pago;
	@SerializedName("numero_liquidacion")
	@Expose
	private String numero_liquidacion;
	@SerializedName("percepcion_ib")
	@Expose
	private String percepcion_ib;
	@SerializedName("signo_percepcion_ib")
	@Expose
	private String signo_percepcion_ib;	
	@SerializedName("sellado_provincial")
	@Expose
	private String sellado_provincial;
	@SerializedName("signo_sellado")
	@Expose
	private String signo_sellado;
	@SerializedName("costo_financiero")
	@Expose
	private String costo_financiero;
	@SerializedName("signo_costo_financiero")
	@Expose
	private String signo_costo_financiero;
	@SerializedName("iva_cf")
	@Expose
	private String iva_cf;
	@SerializedName("signo_iva_cfo")
	@Expose
	private String signo_iva_cfo;
	@SerializedName("iva_cf_alicuota_21")
	@Expose
	private String iva_cf_alicuota_21;
	@SerializedName("signo_iva_cf_alicuota_21")
	@Expose
	private String signo_iva_cf_alicuota_21;
	@SerializedName("percepcion_rg_2126")
	@Expose
	private String percepcion_rg_2126;
	@SerializedName("signo_percepcion_rg_2126")
	@Expose
	private String signo_percepcion_rg_2126;
	@SerializedName("iva_cf_alicuota_10_5")
	@Expose
	private String iva_cf_alicuota_10_5;
	@SerializedName("signo_iva_cf_alicuota_10_5")
	@Expose
	private String signo_iva_cf_alicuota_10_5;
	@SerializedName("impuesto_ley_25413")
	@Expose
	private String impuesto_ley_25413;
	@SerializedName("signo_impuesto_ley_25413")
	@Expose
	private String signo_impuesto_ley_25413;
	@SerializedName("provincia")
	@Expose
	private String provincia;
	@SerializedName("rg_140_98")
	@Expose
	private String rg_140_98;
	@SerializedName("signo_rg_140_98")
	@Expose
	private String signo_rg_140_98;
	@SerializedName("moneda_pago")
	@Expose
	private String moneda_pago;
	
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
	public String getComercio() {
		return comercio;
	}
	public void setComercio(String comercio) {
		this.comercio = comercio;
	}
	public String getRevisado() {
		return revisado;
	}
	public void setRevisado(String revisado) {
		this.revisado = revisado;
	}
	public String getNumero_comercio() {
		return numero_comercio;
	}
	public void setNumero_comercio(String numero_comercio) {
		this.numero_comercio = numero_comercio;
	}
	public String getFecha_pago() {
		return fecha_pago;
	}
	public void setFecha_pago(String fecha_pago) {
		this.fecha_pago = fecha_pago;
	}
	public String getNumero_liquidacion() {
		return numero_liquidacion;
	}
	public void setNumero_liquidacion(String numero_liquidacion) {
		this.numero_liquidacion = numero_liquidacion;
	}
	public String getPercepcion_ib() {
		return percepcion_ib;
	}
	public void setPercepcion_ib(String percepcion_ib) {
		this.percepcion_ib = percepcion_ib;
	}
	public String getSigno_percepcion_ib() {
		return signo_percepcion_ib;
	}
	public void setSigno_percepcion_ib(String signo_percepcion_ib) {
		this.signo_percepcion_ib = signo_percepcion_ib;
	}
	public String getSellado_provincial() {
		return sellado_provincial;
	}
	public void setSellado_provincial(String sellado_provincial) {
		this.sellado_provincial = sellado_provincial;
	}
	public String getSigno_sellado() {
		return signo_sellado;
	}
	public void setSigno_sellado(String signo_sellado) {
		this.signo_sellado = signo_sellado;
	}
	public String getCosto_financiero() {
		return costo_financiero;
	}
	public void setCosto_financiero(String costo_financiero) {
		this.costo_financiero = costo_financiero;
	}
	public String getSigno_costo_financiero() {
		return signo_costo_financiero;
	}
	public void setSigno_costo_financiero(String signo_costo_financiero) {
		this.signo_costo_financiero = signo_costo_financiero;
	}
	public String getIva_cf() {
		return iva_cf;
	}
	public void setIva_cf(String iva_cf) {
		this.iva_cf = iva_cf;
	}
	public String getSigno_iva_cfo() {
		return signo_iva_cfo;
	}
	public void setSigno_iva_cfo(String signo_iva_cfo) {
		this.signo_iva_cfo = signo_iva_cfo;
	}
	public String getIva_cf_alicuota_21() {
		return iva_cf_alicuota_21;
	}
	public void setIva_cf_alicuota_21(String iva_cf_alicuota_21) {
		this.iva_cf_alicuota_21 = iva_cf_alicuota_21;
	}
	public String getSigno_iva_cf_alicuota_21() {
		return signo_iva_cf_alicuota_21;
	}
	public void setSigno_iva_cf_alicuota_21(String signo_iva_cf_alicuota_21) {
		this.signo_iva_cf_alicuota_21 = signo_iva_cf_alicuota_21;
	}
	public String getPercepcion_rg_2126() {
		return percepcion_rg_2126;
	}
	public void setPercepcion_rg_2126(String percepcion_rg_2126) {
		this.percepcion_rg_2126 = percepcion_rg_2126;
	}
	public String getSigno_percepcion_rg_2126() {
		return signo_percepcion_rg_2126;
	}
	public void setSigno_percepcion_rg_2126(String signo_percepcion_rg_2126) {
		this.signo_percepcion_rg_2126 = signo_percepcion_rg_2126;
	}
	public String getIva_cf_alicuota_10_5() {
		return iva_cf_alicuota_10_5;
	}
	public void setIva_cf_alicuota_10_5(String iva_cf_alicuota_10_5) {
		this.iva_cf_alicuota_10_5 = iva_cf_alicuota_10_5;
	}
	public String getSigno_iva_cf_alicuota_10_5() {
		return signo_iva_cf_alicuota_10_5;
	}
	public void setSigno_iva_cf_alicuota_10_5(String signo_iva_cf_alicuota_10_5) {
		this.signo_iva_cf_alicuota_10_5 = signo_iva_cf_alicuota_10_5;
	}
	public String getImpuesto_ley_25413() {
		return impuesto_ley_25413;
	}
	public void setImpuesto_ley_25413(String impuesto_ley_25413) {
		this.impuesto_ley_25413 = impuesto_ley_25413;
	}
	public String getSigno_impuesto_ley_25413() {
		return signo_impuesto_ley_25413;
	}
	public void setSigno_impuesto_ley_25413(String signo_impuesto_ley_25413) {
		this.signo_impuesto_ley_25413 = signo_impuesto_ley_25413;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getRg_140_98() {
		return rg_140_98;
	}
	public void setRg_140_98(String rg_140_98) {
		this.rg_140_98 = rg_140_98;
	}
	public String getSigno_rg_140_98() {
		return signo_rg_140_98;
	}
	public void setSigno_rg_140_98(String signo_rg_140_98) {
		this.signo_rg_140_98 = signo_rg_140_98;
	}
	public String getMoneda_pago() {
		return moneda_pago;
	}
	public void setMoneda_pago(String moneda_pago) {
		this.moneda_pago = moneda_pago;
	}
	
	
}
