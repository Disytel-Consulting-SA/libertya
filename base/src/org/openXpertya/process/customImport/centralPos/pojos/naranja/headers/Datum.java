package org.openXpertya.process.customImport.centralPos.pojos.naranja.headers;

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
	@SerializedName("fecha_proceso")
	@Expose
	private String fechaProceso;
	@SerializedName("fecha_pago")
	@Expose
	private String fechaPago;
	@SerializedName("nro_liquidacion")
	@Expose
	private String nroLiquidacion;
	@SerializedName("cuit_ag_retencion")
	@Expose
	private String cuitAgRetencion;
	@SerializedName("signo_total_descuentos")
	@Expose
	private String signoTotalDescuentos;
	@SerializedName("total_descuentos")
	@Expose
	private String totalDescuentos;
	@SerializedName("signo_ret_iva_140")
	@Expose
	private String signoRetIva140;
	@SerializedName("retencion_iva_140")
	@Expose
	private String retencionIva140;
	@SerializedName("signo_ret_ganancias")
	@Expose
	private String signoRetGanancias;
	@SerializedName("retencion_ganancias")
	@Expose
	private String retencionGanancias;
	@SerializedName("signo_ret_iva_3130")
	@Expose
	private String signoRetIva3130;
	@SerializedName("retencion_3130")
	@Expose
	private String retencion3130;
	@SerializedName("signo_base_imponible")
	@Expose
	private String signoBaseImponible;
	@SerializedName("base_imponible_ing_bru")
	@Expose
	private String baseImponibleIngBru;
	@SerializedName("alicuota_ing_brutos")
	@Expose
	private String alicuotaIngBrutos;
	@SerializedName("signo_ret_ing_brutos")
	@Expose
	private String signoRetIngBrutos;
	@SerializedName("ret_ingresos_brutos")
	@Expose
	private String retIngresosBrutos;
	@SerializedName("signo_retencion_municipal")
	@Expose
	private String signoRetencionMunicipal;
	@SerializedName("retencion_municipal")
	@Expose
	private String retencionMunicipal;
	@SerializedName("signo_dbtos_cdtos")
	@Expose
	private String signoDbtosCdtos;
	@SerializedName("debitos_creditos")
	@Expose
	private String debitosCreditos;
	@SerializedName("signo_percepcion_1135")
	@Expose
	private String signoPercepcion1135;
	@SerializedName("percepcion_1135")
	@Expose
	private String percepcion1135;
	@SerializedName("signo_liq_negativa_ant")
	@Expose
	private String signoLiqNegativaAnt;
	@SerializedName("liq_negativa_dia_ant")
	@Expose
	private String liqNegativaDiaAnt;
	@SerializedName("signo_embargo_y_cesiones")
	@Expose
	private String signoEmbargoYCesiones;
	@SerializedName("embargo_y_cesiones")
	@Expose
	private String embargoYCesiones;
	@SerializedName("signo_neto")
	@Expose
	private String signoNeto;
	@SerializedName("neto")
	@Expose
	private String neto;
	@SerializedName("rubro")
	@Expose
	private String rubro;
	@SerializedName("signo_total_otros_debitos")
	@Expose
	private String signoTotalOtrosDebitos;
	@SerializedName("importe_total_otros_debitos")
	@Expose
	private String importeTotalOtrosDebitos;
	@SerializedName("signo_total_creditos")
	@Expose
	private String signoTotalCreditos;
	@SerializedName("importe_total_creditos")
	@Expose
	private String importeTotalCreditos;
	@SerializedName("signo_total_anticipos")
	@Expose
	private String signoTotalAnticipos;
	@SerializedName("importe_total_anticipos")
	@Expose
	private String importeTotalAnticipos;
	@SerializedName("signo_total_cheque_diferido")
	@Expose
	private String signoTotalChequeDiferido;
	@SerializedName("importe_total_cheque_diferido")
	@Expose
	private String importeTotalChequeDiferido;
	@SerializedName("marca")
	@Expose
	private String marca;
	@SerializedName("num_cliente")
	@Expose
	private String numCliente;
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

	public String getComercio() {
		return comercio;
	}

	public void setComercio(String comercio) {
		this.comercio = comercio;
	}

	public String getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(String fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public String getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(String fechaPago) {
		this.fechaPago = fechaPago;
	}

	public String getNroLiquidacion() {
		return nroLiquidacion;
	}

	public void setNroLiquidacion(String nroLiquidacion) {
		this.nroLiquidacion = nroLiquidacion;
	}

	public String getCuitAgRetencion() {
		return cuitAgRetencion;
	}

	public void setCuitAgRetencion(String cuitAgRetencion) {
		this.cuitAgRetencion = cuitAgRetencion;
	}

	public String getSignoTotalDescuentos() {
		return signoTotalDescuentos;
	}

	public void setSignoTotalDescuentos(String signoTotalDescuentos) {
		this.signoTotalDescuentos = signoTotalDescuentos;
	}

	public String getTotalDescuentos() {
		return totalDescuentos;
	}

	public void setTotalDescuentos(String totalDescuentos) {
		this.totalDescuentos = totalDescuentos;
	}

	public String getSignoRetIva140() {
		return signoRetIva140;
	}

	public void setSignoRetIva140(String signoRetIva140) {
		this.signoRetIva140 = signoRetIva140;
	}

	public String getRetencionIva140() {
		return retencionIva140;
	}

	public void setRetencionIva140(String retencionIva140) {
		this.retencionIva140 = retencionIva140;
	}

	public String getSignoRetGanancias() {
		return signoRetGanancias;
	}

	public void setSignoRetGanancias(String signoRetGanancias) {
		this.signoRetGanancias = signoRetGanancias;
	}

	public String getRetencionGanancias() {
		return retencionGanancias;
	}

	public void setRetencionGanancias(String retencionGanancias) {
		this.retencionGanancias = retencionGanancias;
	}

	public String getSignoRetIva3130() {
		return signoRetIva3130;
	}

	public void setSignoRetIva3130(String signoRetIva3130) {
		this.signoRetIva3130 = signoRetIva3130;
	}

	public String getRetencion3130() {
		return retencion3130;
	}

	public void setRetencion3130(String retencion3130) {
		this.retencion3130 = retencion3130;
	}

	public String getSignoBaseImponible() {
		return signoBaseImponible;
	}

	public void setSignoBaseImponible(String signoBaseImponible) {
		this.signoBaseImponible = signoBaseImponible;
	}

	public String getBaseImponibleIngBru() {
		return baseImponibleIngBru;
	}

	public void setBaseImponibleIngBru(String baseImponibleIngBru) {
		this.baseImponibleIngBru = baseImponibleIngBru;
	}

	public String getAlicuotaIngBrutos() {
		return alicuotaIngBrutos;
	}

	public void setAlicuotaIngBrutos(String alicuotaIngBrutos) {
		this.alicuotaIngBrutos = alicuotaIngBrutos;
	}

	public String getSignoRetIngBrutos() {
		return signoRetIngBrutos;
	}

	public void setSignoRetIngBrutos(String signoRetIngBrutos) {
		this.signoRetIngBrutos = signoRetIngBrutos;
	}

	public String getRetIngresosBrutos() {
		return retIngresosBrutos;
	}

	public void setRetIngresosBrutos(String retIngresosBrutos) {
		this.retIngresosBrutos = retIngresosBrutos;
	}

	public String getSignoRetencionMunicipal() {
		return signoRetencionMunicipal;
	}

	public void setSignoRetencionMunicipal(String signoRetencionMunicipal) {
		this.signoRetencionMunicipal = signoRetencionMunicipal;
	}

	public String getRetencionMunicipal() {
		return retencionMunicipal;
	}

	public void setRetencionMunicipal(String retencionMunicipal) {
		this.retencionMunicipal = retencionMunicipal;
	}

	public String getSignoDbtosCdtos() {
		return signoDbtosCdtos;
	}

	public void setSignoDbtosCdtos(String signoDbtosCdtos) {
		this.signoDbtosCdtos = signoDbtosCdtos;
	}

	public String getDebitosCreditos() {
		return debitosCreditos;
	}

	public void setDebitosCreditos(String debitosCreditos) {
		this.debitosCreditos = debitosCreditos;
	}

	public String getSignoPercepcion1135() {
		return signoPercepcion1135;
	}

	public void setSignoPercepcion1135(String signoPercepcion1135) {
		this.signoPercepcion1135 = signoPercepcion1135;
	}

	public String getPercepcion1135() {
		return percepcion1135;
	}

	public void setPercepcion1135(String percepcion1135) {
		this.percepcion1135 = percepcion1135;
	}

	public String getSignoLiqNegativaAnt() {
		return signoLiqNegativaAnt;
	}

	public void setSignoLiqNegativaAnt(String signoLiqNegativaAnt) {
		this.signoLiqNegativaAnt = signoLiqNegativaAnt;
	}

	public String getLiqNegativaDiaAnt() {
		return liqNegativaDiaAnt;
	}

	public void setLiqNegativaDiaAnt(String liqNegativaDiaAnt) {
		this.liqNegativaDiaAnt = liqNegativaDiaAnt;
	}

	public String getSignoEmbargoYCesiones() {
		return signoEmbargoYCesiones;
	}

	public void setSignoEmbargoYCesiones(String signoEmbargoYCesiones) {
		this.signoEmbargoYCesiones = signoEmbargoYCesiones;
	}

	public String getEmbargoYCesiones() {
		return embargoYCesiones;
	}

	public void setEmbargoYCesiones(String embargoYCesiones) {
		this.embargoYCesiones = embargoYCesiones;
	}

	public String getSignoNeto() {
		return signoNeto;
	}

	public void setSignoNeto(String signoNeto) {
		this.signoNeto = signoNeto;
	}

	public String getNeto() {
		return neto;
	}

	public void setNeto(String neto) {
		this.neto = neto;
	}

	public String getRubro() {
		return rubro;
	}

	public void setRubro(String rubro) {
		this.rubro = rubro;
	}

	public String getSignoTotalOtrosDebitos() {
		return signoTotalOtrosDebitos;
	}

	public void setSignoTotalOtrosDebitos(String signoTotalOtrosDebitos) {
		this.signoTotalOtrosDebitos = signoTotalOtrosDebitos;
	}

	public String getImporteTotalOtrosDebitos() {
		return importeTotalOtrosDebitos;
	}

	public void setImporteTotalOtrosDebitos(String importeTotalOtrosDebitos) {
		this.importeTotalOtrosDebitos = importeTotalOtrosDebitos;
	}

	public String getSignoTotalCreditos() {
		return signoTotalCreditos;
	}

	public void setSignoTotalCreditos(String signoTotalCreditos) {
		this.signoTotalCreditos = signoTotalCreditos;
	}

	public String getImporteTotalCreditos() {
		return importeTotalCreditos;
	}

	public void setImporteTotalCreditos(String importeTotalCreditos) {
		this.importeTotalCreditos = importeTotalCreditos;
	}

	public String getSignoTotalAnticipos() {
		return signoTotalAnticipos;
	}

	public void setSignoTotalAnticipos(String signoTotalAnticipos) {
		this.signoTotalAnticipos = signoTotalAnticipos;
	}

	public String getImporteTotalAnticipos() {
		return importeTotalAnticipos;
	}

	public void setImporteTotalAnticipos(String importeTotalAnticipos) {
		this.importeTotalAnticipos = importeTotalAnticipos;
	}

	public String getSignoTotalChequeDiferido() {
		return signoTotalChequeDiferido;
	}

	public void setSignoTotalChequeDiferido(String signoTotalChequeDiferido) {
		this.signoTotalChequeDiferido = signoTotalChequeDiferido;
	}

	public String getImporteTotalChequeDiferido() {
		return importeTotalChequeDiferido;
	}

	public void setImporteTotalChequeDiferido(String importeTotalChequeDiferido) {
		this.importeTotalChequeDiferido = importeTotalChequeDiferido;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getNumCliente() {
		return numCliente;
	}

	public void setNumCliente(String numCliente) {
		this.numCliente = numCliente;
	}

	public String getRevisado() {
		return revisado;
	}

	public void setRevisado(String revisado) {
		this.revisado = revisado;
	}

}
