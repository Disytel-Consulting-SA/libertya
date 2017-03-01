
package org.openXpertya.process.customImport.centralPos.pojos.firstdata.trailer;

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
	@SerializedName("tipo_registro")
	@Expose
	private String tipoRegistro;
	@SerializedName("nombre_archivo")
	@Expose
	private String nombreArchivo;
	@SerializedName("comercio_centralizador")
	@Expose
	private String comercioCentralizador;
	@SerializedName("moneda")
	@Expose
	private String moneda;
	@SerializedName("grupo_presentacion")
	@Expose
	private String grupoPresentacion;
	@SerializedName("plazo_pago")
	@Expose
	private String plazoPago;
	@SerializedName("tipo_plazo_pago")
	@Expose
	private String tipoPlazoPago;
	@SerializedName("fecha_presentacion")
	@Expose
	private String fechaPresentacion;
	@SerializedName("fecha_vencimiento_clearing")
	@Expose
	private String fechaVencimientoClearing;
	@SerializedName("producto")
	@Expose
	private String producto;
	@SerializedName("comercio_participante")
	@Expose
	private String comercioParticipante;
	@SerializedName("entidad_pagadora")
	@Expose
	private String entidadPagadora;
	@SerializedName("sucursal_pagadora")
	@Expose
	private String sucursalPagadora;
	@SerializedName("numero_liquidacion")
	@Expose
	private String numeroLiquidacion;
	@SerializedName("total_importe_total")
	@Expose
	private String totalImporteTotal;
	@SerializedName("total_importe_total_signo")
	@Expose
	private String totalImporteTotalSigno;
	@SerializedName("total_importe_sin_dto")
	@Expose
	private String totalImporteSinDto;
	@SerializedName("total_importe_sin_dto_signo")
	@Expose
	private String totalImporteSinDtoSigno;
	@SerializedName("total_importe_final")
	@Expose
	private String totalImporteFinal;
	@SerializedName("total_importe_final_signo")
	@Expose
	private String totalImporteFinalSigno;
	@SerializedName("aranceles_cto_fin")
	@Expose
	private String arancelesCtoFin;
	@SerializedName("aranceles_cto_fin_signo")
	@Expose
	private String arancelesCtoFinSigno;
	@SerializedName("retenciones_fiscales")
	@Expose
	private String retencionesFiscales;
	@SerializedName("retenciones_fiscales_signo")
	@Expose
	private String retencionesFiscalesSigno;
	@SerializedName("otros_debitos")
	@Expose
	private String otrosDebitos;
	@SerializedName("otros_debitos_signo")
	@Expose
	private String otrosDebitosSigno;
	@SerializedName("otros_creditos")
	@Expose
	private String otrosCreditos;
	@SerializedName("otros_creditos_signo")
	@Expose
	private String otrosCreditosSigno;
	@SerializedName("neto_comercios")
	@Expose
	private String netoComercios;
	@SerializedName("neto_comercios_signo")
	@Expose
	private String netoComerciosSigno;
	@SerializedName("total_registros_detalle")
	@Expose
	private String totalRegistrosDetalle;
	@SerializedName("monto_pend_cuotas")
	@Expose
	private String montoPendCuotas;
	@SerializedName("monto_pend_cuotas_signo")
	@Expose
	private String montoPendCuotasSigno;
	@SerializedName("subtipo_registro")
	@Expose
	private String subtipoRegistro;
	@SerializedName("iva_aranceles_ri")
	@Expose
	private String ivaArancelesRi;
	@SerializedName("iva_aranceles_ri_signo")
	@Expose
	private String ivaArancelesRiSigno;
	@SerializedName("impuesto_deb_cred")
	@Expose
	private String impuestoDebCred;
	@SerializedName("impuesto_deb_cred_signo")
	@Expose
	private String impuestoDebCredSigno;
	@SerializedName("iva_dto_pago_anticipado")
	@Expose
	private String ivaDtoPagoAnticipado;
	@SerializedName("iva_dto_pago_anticipado_signo")
	@Expose
	private String ivaDtoPagoAnticipadoSigno;
	@SerializedName("ret_iva_ventas")
	@Expose
	private String retIvaVentas;
	@SerializedName("ret_iva_ventas_signo")
	@Expose
	private String retIvaVentasSigno;
	@SerializedName("percepc_iva_r3337")
	@Expose
	private String percepcIvaR3337;
	@SerializedName("percepc_iva_r3337_signo")
	@Expose
	private String percepcIvaR3337Signo;
	@SerializedName("ret_imp_ganancias")
	@Expose
	private String retImpGanancias;
	@SerializedName("ret_imp_ganancias_signo")
	@Expose
	private String retImpGananciasSigno;
	@SerializedName("ret_imp_ingresos_brutos")
	@Expose
	private String retImpIngresosBrutos;
	@SerializedName("ret_imp_ingresos_brutos_signo")
	@Expose
	private String retImpIngresosBrutosSigno;
	@SerializedName("percep_ingr_brutos")
	@Expose
	private String percepIngrBrutos;
	@SerializedName("percep_ingr_brutos_signo")
	@Expose
	private String percepIngrBrutosSigno;
	@SerializedName("iva_servicios")
	@Expose
	private String ivaServicios;
	@SerializedName("iva_servicios_signo")
	@Expose
	private String ivaServiciosSigno;
	@SerializedName("categoria_iva")
	@Expose
	private String categoriaIva;
	@SerializedName("imp_sintereses_ley_25063")
	@Expose
	private String impSinteresesLey25063;
	@SerializedName("imp_sintereses_ley_25063_signo")
	@Expose
	private String impSinteresesLey25063Signo;
	@SerializedName("arancel")
	@Expose
	private String arancel;
	@SerializedName("arancel_signo")
	@Expose
	private String arancelSigno;
	@SerializedName("costo_financiero")
	@Expose
	private String costoFinanciero;
	@SerializedName("costo_financiero_signo")
	@Expose
	private String costoFinancieroSigno;
	@SerializedName("revisado")
	@Expose
	private String revisado;
	@SerializedName("hash_modelo")
	@Expose
	private String hashModelo;
	@SerializedName("provincia_ing_brutos")
	@Expose
	private String provinciaIngBrutos;

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

	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getComercioCentralizador() {
		return comercioCentralizador;
	}

	public void setComercioCentralizador(String comercioCentralizador) {
		this.comercioCentralizador = comercioCentralizador;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getGrupoPresentacion() {
		return grupoPresentacion;
	}

	public void setGrupoPresentacion(String grupoPresentacion) {
		this.grupoPresentacion = grupoPresentacion;
	}

	public String getPlazoPago() {
		return plazoPago;
	}

	public void setPlazoPago(String plazoPago) {
		this.plazoPago = plazoPago;
	}

	public String getTipoPlazoPago() {
		return tipoPlazoPago;
	}

	public void setTipoPlazoPago(String tipoPlazoPago) {
		this.tipoPlazoPago = tipoPlazoPago;
	}

	public String getFechaPresentacion() {
		return fechaPresentacion;
	}

	public void setFechaPresentacion(String fechaPresentacion) {
		this.fechaPresentacion = fechaPresentacion;
	}

	public String getFechaVencimientoClearing() {
		return fechaVencimientoClearing;
	}

	public void setFechaVencimientoClearing(String fechaVencimientoClearing) {
		this.fechaVencimientoClearing = fechaVencimientoClearing;
	}

	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	public String getComercioParticipante() {
		return comercioParticipante;
	}

	public void setComercioParticipante(String comercioParticipante) {
		this.comercioParticipante = comercioParticipante;
	}

	public String getEntidadPagadora() {
		return entidadPagadora;
	}

	public void setEntidadPagadora(String entidadPagadora) {
		this.entidadPagadora = entidadPagadora;
	}

	public String getSucursalPagadora() {
		return sucursalPagadora;
	}

	public void setSucursalPagadora(String sucursalPagadora) {
		this.sucursalPagadora = sucursalPagadora;
	}

	public String getNumeroLiquidacion() {
		return numeroLiquidacion;
	}

	public void setNumeroLiquidacion(String numeroLiquidacion) {
		this.numeroLiquidacion = numeroLiquidacion;
	}

	public String getTotalImporteTotal() {
		return totalImporteTotal;
	}

	public void setTotalImporteTotal(String totalImporteTotal) {
		this.totalImporteTotal = totalImporteTotal;
	}

	public String getTotalImporteTotalSigno() {
		return totalImporteTotalSigno;
	}

	public void setTotalImporteTotalSigno(String totalImporteTotalSigno) {
		this.totalImporteTotalSigno = totalImporteTotalSigno;
	}

	public String getTotalImporteSinDto() {
		return totalImporteSinDto;
	}

	public void setTotalImporteSinDto(String totalImporteSinDto) {
		this.totalImporteSinDto = totalImporteSinDto;
	}

	public String getTotalImporteSinDtoSigno() {
		return totalImporteSinDtoSigno;
	}

	public void setTotalImporteSinDtoSigno(String totalImporteSinDtoSigno) {
		this.totalImporteSinDtoSigno = totalImporteSinDtoSigno;
	}

	public String getTotalImporteFinal() {
		return totalImporteFinal;
	}

	public void setTotalImporteFinal(String totalImporteFinal) {
		this.totalImporteFinal = totalImporteFinal;
	}

	public String getTotalImporteFinalSigno() {
		return totalImporteFinalSigno;
	}

	public void setTotalImporteFinalSigno(String totalImporteFinalSigno) {
		this.totalImporteFinalSigno = totalImporteFinalSigno;
	}

	public String getArancelesCtoFin() {
		return arancelesCtoFin;
	}

	public void setArancelesCtoFin(String arancelesCtoFin) {
		this.arancelesCtoFin = arancelesCtoFin;
	}

	public String getArancelesCtoFinSigno() {
		return arancelesCtoFinSigno;
	}

	public void setArancelesCtoFinSigno(String arancelesCtoFinSigno) {
		this.arancelesCtoFinSigno = arancelesCtoFinSigno;
	}

	public String getRetencionesFiscales() {
		return retencionesFiscales;
	}

	public void setRetencionesFiscales(String retencionesFiscales) {
		this.retencionesFiscales = retencionesFiscales;
	}

	public String getRetencionesFiscalesSigno() {
		return retencionesFiscalesSigno;
	}

	public void setRetencionesFiscalesSigno(String retencionesFiscalesSigno) {
		this.retencionesFiscalesSigno = retencionesFiscalesSigno;
	}

	public String getOtrosDebitos() {
		return otrosDebitos;
	}

	public void setOtrosDebitos(String otrosDebitos) {
		this.otrosDebitos = otrosDebitos;
	}

	public String getOtrosDebitosSigno() {
		return otrosDebitosSigno;
	}

	public void setOtrosDebitosSigno(String otrosDebitosSigno) {
		this.otrosDebitosSigno = otrosDebitosSigno;
	}

	public String getOtrosCreditos() {
		return otrosCreditos;
	}

	public void setOtrosCreditos(String otrosCreditos) {
		this.otrosCreditos = otrosCreditos;
	}

	public String getOtrosCreditosSigno() {
		return otrosCreditosSigno;
	}

	public void setOtrosCreditosSigno(String otrosCreditosSigno) {
		this.otrosCreditosSigno = otrosCreditosSigno;
	}

	public String getNetoComercios() {
		return netoComercios;
	}

	public void setNetoComercios(String netoComercios) {
		this.netoComercios = netoComercios;
	}

	public String getNetoComerciosSigno() {
		return netoComerciosSigno;
	}

	public void setNetoComerciosSigno(String netoComerciosSigno) {
		this.netoComerciosSigno = netoComerciosSigno;
	}

	public String getTotalRegistrosDetalle() {
		return totalRegistrosDetalle;
	}

	public void setTotalRegistrosDetalle(String totalRegistrosDetalle) {
		this.totalRegistrosDetalle = totalRegistrosDetalle;
	}

	public String getMontoPendCuotas() {
		return montoPendCuotas;
	}

	public void setMontoPendCuotas(String montoPendCuotas) {
		this.montoPendCuotas = montoPendCuotas;
	}

	public String getMontoPendCuotasSigno() {
		return montoPendCuotasSigno;
	}

	public void setMontoPendCuotasSigno(String montoPendCuotasSigno) {
		this.montoPendCuotasSigno = montoPendCuotasSigno;
	}

	public String getSubtipoRegistro() {
		return subtipoRegistro;
	}

	public void setSubtipoRegistro(String subtipoRegistro) {
		this.subtipoRegistro = subtipoRegistro;
	}

	public String getIvaArancelesRi() {
		return ivaArancelesRi;
	}

	public void setIvaArancelesRi(String ivaArancelesRi) {
		this.ivaArancelesRi = ivaArancelesRi;
	}

	public String getIvaArancelesRiSigno() {
		return ivaArancelesRiSigno;
	}

	public void setIvaArancelesRiSigno(String ivaArancelesRiSigno) {
		this.ivaArancelesRiSigno = ivaArancelesRiSigno;
	}

	public String getImpuestoDebCred() {
		return impuestoDebCred;
	}

	public void setImpuestoDebCred(String impuestoDebCred) {
		this.impuestoDebCred = impuestoDebCred;
	}

	public String getImpuestoDebCredSigno() {
		return impuestoDebCredSigno;
	}

	public void setImpuestoDebCredSigno(String impuestoDebCredSigno) {
		this.impuestoDebCredSigno = impuestoDebCredSigno;
	}

	public String getIvaDtoPagoAnticipado() {
		return ivaDtoPagoAnticipado;
	}

	public void setIvaDtoPagoAnticipado(String ivaDtoPagoAnticipado) {
		this.ivaDtoPagoAnticipado = ivaDtoPagoAnticipado;
	}

	public String getIvaDtoPagoAnticipadoSigno() {
		return ivaDtoPagoAnticipadoSigno;
	}

	public void setIvaDtoPagoAnticipadoSigno(String ivaDtoPagoAnticipadoSigno) {
		this.ivaDtoPagoAnticipadoSigno = ivaDtoPagoAnticipadoSigno;
	}

	public String getRetIvaVentas() {
		return retIvaVentas;
	}

	public void setRetIvaVentas(String retIvaVentas) {
		this.retIvaVentas = retIvaVentas;
	}

	public String getRetIvaVentasSigno() {
		return retIvaVentasSigno;
	}

	public void setRetIvaVentasSigno(String retIvaVentasSigno) {
		this.retIvaVentasSigno = retIvaVentasSigno;
	}

	public String getPercepcIvaR3337() {
		return percepcIvaR3337;
	}

	public void setPercepcIvaR3337(String percepcIvaR3337) {
		this.percepcIvaR3337 = percepcIvaR3337;
	}

	public String getPercepcIvaR3337Signo() {
		return percepcIvaR3337Signo;
	}

	public void setPercepcIvaR3337Signo(String percepcIvaR3337Signo) {
		this.percepcIvaR3337Signo = percepcIvaR3337Signo;
	}

	public String getRetImpGanancias() {
		return retImpGanancias;
	}

	public void setRetImpGanancias(String retImpGanancias) {
		this.retImpGanancias = retImpGanancias;
	}

	public String getRetImpGananciasSigno() {
		return retImpGananciasSigno;
	}

	public void setRetImpGananciasSigno(String retImpGananciasSigno) {
		this.retImpGananciasSigno = retImpGananciasSigno;
	}

	public String getRetImpIngresosBrutos() {
		return retImpIngresosBrutos;
	}

	public void setRetImpIngresosBrutos(String retImpIngresosBrutos) {
		this.retImpIngresosBrutos = retImpIngresosBrutos;
	}

	public String getRetImpIngresosBrutosSigno() {
		return retImpIngresosBrutosSigno;
	}

	public void setRetImpIngresosBrutosSigno(String retImpIngresosBrutosSigno) {
		this.retImpIngresosBrutosSigno = retImpIngresosBrutosSigno;
	}

	public String getPercepIngrBrutos() {
		return percepIngrBrutos;
	}

	public void setPercepIngrBrutos(String percepIngrBrutos) {
		this.percepIngrBrutos = percepIngrBrutos;
	}

	public String getPercepIngrBrutosSigno() {
		return percepIngrBrutosSigno;
	}

	public void setPercepIngrBrutosSigno(String percepIngrBrutosSigno) {
		this.percepIngrBrutosSigno = percepIngrBrutosSigno;
	}

	public String getIvaServicios() {
		return ivaServicios;
	}

	public void setIvaServicios(String ivaServicios) {
		this.ivaServicios = ivaServicios;
	}

	public String getIvaServiciosSigno() {
		return ivaServiciosSigno;
	}

	public void setIvaServiciosSigno(String ivaServiciosSigno) {
		this.ivaServiciosSigno = ivaServiciosSigno;
	}

	public String getCategoriaIva() {
		return categoriaIva;
	}

	public void setCategoriaIva(String categoriaIva) {
		this.categoriaIva = categoriaIva;
	}

	public String getImpSinteresesLey25063() {
		return impSinteresesLey25063;
	}

	public void setImpSinteresesLey25063(String impSinteresesLey25063) {
		this.impSinteresesLey25063 = impSinteresesLey25063;
	}

	public String getImpSinteresesLey25063Signo() {
		return impSinteresesLey25063Signo;
	}

	public void setImpSinteresesLey25063Signo(String impSinteresesLey25063Signo) {
		this.impSinteresesLey25063Signo = impSinteresesLey25063Signo;
	}

	public String getArancel() {
		return arancel;
	}

	public void setArancel(String arancel) {
		this.arancel = arancel;
	}

	public String getArancelSigno() {
		return arancelSigno;
	}

	public void setArancelSigno(String arancelSigno) {
		this.arancelSigno = arancelSigno;
	}

	public String getCostoFinanciero() {
		return costoFinanciero;
	}

	public void setCostoFinanciero(String costoFinanciero) {
		this.costoFinanciero = costoFinanciero;
	}

	public String getCostoFinancieroSigno() {
		return costoFinancieroSigno;
	}

	public void setCostoFinancieroSigno(String costoFinancieroSigno) {
		this.costoFinancieroSigno = costoFinancieroSigno;
	}

	public String getRevisado() {
		return revisado;
	}

	public void setRevisado(String revisado) {
		this.revisado = revisado;
	}

	public String getHashModelo() {
		return hashModelo;
	}

	public void setHashModelo(String hashModelo) {
		this.hashModelo = hashModelo;
	}

	public String getProvinciaIngBrutos() {
		return provinciaIngBrutos;
	}

	public void setProvinciaIngBrutos(String provinciaIngBrutos) {
		this.provinciaIngBrutos = provinciaIngBrutos;
	}

}
