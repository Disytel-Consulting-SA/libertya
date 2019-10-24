
package org.openXpertya.process.customImport.centralPos.pojos.cabal.pagos;

import org.openXpertya.process.customImport.centralPos.pojos.GenericDatum;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Datos devueltos por Cabal 
 * @author Matias Cap - Disytel
 *
 */

public class Datum extends GenericDatum {

	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("archivo_id")
	@Expose
	private String archivoId;
	@SerializedName("hash_modelo")
	@Expose
	private String hash_modelo;
	@SerializedName("comercio")
	@Expose
	private String comercio;
	@SerializedName("numero_comercio")
	@Expose
	private String numero_comercio;
	@SerializedName("moneda_pago")
	@Expose
	private String moneda_pago;
	@SerializedName("importe_venta")
	@Expose
	private String importe_venta;
	@SerializedName("signo_importe_bruto")
	@Expose
	private String signo_importe_bruto;
	@SerializedName("importe_arancel")
	@Expose
	private String importe_arancel;
	@SerializedName("signo_importe_arancel")
	@Expose
	private String signo_importe_arancel;
	@SerializedName("importe_iva_arancel")
	@Expose
	private String importe_iva_arancel;
	@SerializedName("signo_iva_sobre_arancel")
	@Expose
	private String signo_iva_sobre_arancel;
	@SerializedName("retencion_iva")
	@Expose
	private String retencion_iva;
	@SerializedName("signo_retencion_iva")
	@Expose
	private String signo_retencion_iva;
	@SerializedName("retencion_ganancias")
	@Expose
	private String retencion_ganancias;
	@SerializedName("signo_retencion_ganancias")
	@Expose
	private String signo_retencion_ganancias;
	@SerializedName("retencion_ingresos_brutos")
	@Expose
	private String retencion_ingresos_brutos;
	@SerializedName("signo_retencion_ingresos_brutos")
	@Expose
	private String signo_retencion_ingresos_brutos;
	@SerializedName("percepcion_rg_3337")
	@Expose
	private String percepcion_rg_3337;
	@SerializedName("signo_percepcion_3337")
	@Expose
	private String signo_percepcion_3337;
	@SerializedName("importe_neto_final")
	@Expose
	private String importe_neto_final;
	@SerializedName("signo_importe_neto_final")
	@Expose
	private String signo_importe_neto_final;
	@SerializedName("fecha_pago")
	@Expose
	private String fecha_pago;
	@SerializedName("numero_liquidacion")
	@Expose
	private String numero_liquidacion;
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

	public String getHash_modelo() {
		return hash_modelo;
	}

	public void setHash_modelo(String hash_modelo) {
		this.hash_modelo = hash_modelo;
	}

	public String getComercio() {
		return comercio;
	}

	public void setComercio(String comercio) {
		this.comercio = comercio;
	}

	public String getNumero_comercio() {
		return numero_comercio;
	}

	public void setNumero_comercio(String numero_comercio) {
		this.numero_comercio = numero_comercio;
	}

	public String getMoneda_pago() {
		return moneda_pago;
	}

	public void setMoneda_pago(String moneda_pago) {
		this.moneda_pago = moneda_pago;
	}

	public String getImporte_venta() {
		return importe_venta;
	}

	public void setImporte_venta(String importe_venta) {
		this.importe_venta = importe_venta;
	}

	public String getSigno_importe_bruto() {
		return signo_importe_bruto;
	}

	public void setSigno_importe_bruto(String signo_importe_bruto) {
		this.signo_importe_bruto = signo_importe_bruto;
	}

	public String getImporte_arancel() {
		return importe_arancel;
	}

	public void setImporte_arancel(String importe_arancel) {
		this.importe_arancel = importe_arancel;
	}

	public String getSigno_importe_arancel() {
		return signo_importe_arancel;
	}

	public void setSigno_importe_arancel(String signo_importe_arancel) {
		this.signo_importe_arancel = signo_importe_arancel;
	}

	public String getImporte_iva_arancel() {
		return importe_iva_arancel;
	}

	public void setImporte_iva_arancel(String importe_iva_arancel) {
		this.importe_iva_arancel = importe_iva_arancel;
	}

	public String getSigno_iva_sobre_arancel() {
		return signo_iva_sobre_arancel;
	}

	public void setSigno_iva_sobre_arancel(String signo_iva_sobre_arancel) {
		this.signo_iva_sobre_arancel = signo_iva_sobre_arancel;
	}

	public String getRetencion_iva() {
		return retencion_iva;
	}

	public String getSigno_importe_neto_final() {
		return signo_importe_neto_final;
	}

	public void setSigno_importe_neto_final(String signo_importe_neto_final) {
		this.signo_importe_neto_final = signo_importe_neto_final;
	}

	public void setRetencion_iva(String retencion_iva) {
		this.retencion_iva = retencion_iva;
	}

	public String getSigno_retencion_iva() {
		return signo_retencion_iva;
	}

	public void setSigno_retencion_iva(String signo_retencion_iva) {
		this.signo_retencion_iva = signo_retencion_iva;
	}

	public String getRetencion_ganancias() {
		return retencion_ganancias;
	}

	public void setRetencion_ganancias(String retencion_ganancias) {
		this.retencion_ganancias = retencion_ganancias;
	}

	public String getSigno_retencion_ganancias() {
		return signo_retencion_ganancias;
	}

	public void setSigno_retencion_ganancias(String signo_retencion_ganancias) {
		this.signo_retencion_ganancias = signo_retencion_ganancias;
	}

	public String getRetencion_ingresos_brutos() {
		return retencion_ingresos_brutos;
	}

	public void setRetencion_ingresos_brutos(String retencion_ingresos_brutos) {
		this.retencion_ingresos_brutos = retencion_ingresos_brutos;
	}

	public String getSigno_retencion_ingresos_brutos() {
		return signo_retencion_ingresos_brutos;
	}

	public void setSigno_retencion_ingresos_brutos(String signo_retencion_ingresos_brutos) {
		this.signo_retencion_ingresos_brutos = signo_retencion_ingresos_brutos;
	}

	public String getPercepcion_rg_3337() {
		return percepcion_rg_3337;
	}

	public void setPercepcion_rg_3337(String percepcion_rg_3337) {
		this.percepcion_rg_3337 = percepcion_rg_3337;
	}

	public String getSigno_percepcion_3337() {
		return signo_percepcion_3337;
	}

	public void setSigno_percepcion_3337(String signo_percepcion_3337) {
		this.signo_percepcion_3337 = signo_percepcion_3337;
	}

	public String getImporte_neto_final() {
		return importe_neto_final;
	}

	public void setImporte_neto_final(String importe_neto_final) {
		this.importe_neto_final = importe_neto_final;
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

	public String getRevisado() {
		return revisado;
	}

	public void setRevisado(String revisado) {
		this.revisado = revisado;
	}	
}
