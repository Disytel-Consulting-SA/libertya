package org.openXpertya.process.customImport.centralPos.pojos.firstdata.detalle;

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
	@SerializedName("fecha_operacion")
	@Expose
	private String fechaOperacion;
	@SerializedName("codigo_movimiento")
	@Expose
	private String codigoMovimiento;
	@SerializedName("codigo_origen")
	@Expose
	private String codigoOrigen;
	@SerializedName("caja_nro_cinta_posnet")
	@Expose
	private String cajaNroCintaPosnet;
	@SerializedName("caratula_terminal_posnet")
	@Expose
	private String caratulaTerminalPosnet;
	@SerializedName("resumen_lote_posnet")
	@Expose
	private String resumenLotePosnet;
	@SerializedName("cupon_cupon_posnet")
	@Expose
	private String cuponCuponPosnet;
	@SerializedName("cuotas_plan")
	@Expose
	private String cuotasPlan;
	@SerializedName("cuota_vigente")
	@Expose
	private String cuotaVigente;
	@SerializedName("importe_total")
	@Expose
	private String importeTotal;
	@SerializedName("importe_total_signo")
	@Expose
	private String importeTotalSigno;
	@SerializedName("importe_sin_dto")
	@Expose
	private String importeSinDto;
	@SerializedName("importe_sin_dto_signo")
	@Expose
	private String importeSinDtoSigno;
	@SerializedName("importe_final")
	@Expose
	private String importeFinal;
	@SerializedName("importe_final_signo")
	@Expose
	private String importeFinalSigno;
	@SerializedName("porc_desc")
	@Expose
	private String porcDesc;
	@SerializedName("marca_error")
	@Expose
	private String marcaError;
	@SerializedName("tipo_plan_cuotas")
	@Expose
	private String tipoPlanCuotas;
	@SerializedName("nro_tarjeta")
	@Expose
	private String nroTarjeta;
	@SerializedName("motivo_rechazo_1")
	@Expose
	private String motivoRechazo1;
	@SerializedName("motivo_rechazo_2")
	@Expose
	private String motivoRechazo2;
	@SerializedName("motivo_rechazo_3")
	@Expose
	private String motivoRechazo3;
	@SerializedName("motivo_rechazo_4")
	@Expose
	private String motivoRechazo4;
	@SerializedName("fecha_present_original")
	@Expose
	private String fechaPresentOriginal;
	@SerializedName("motivo_reversion")
	@Expose
	private String motivoReversion;
	@SerializedName("tipo_operacion")
	@Expose
	private String tipoOperacion;
	@SerializedName("marca_campana")
	@Expose
	private String marcaCampana;
	@SerializedName("codigo_cargo_pago")
	@Expose
	private String codigoCargoPago;
	@SerializedName("entidad_emisora")
	@Expose
	private String entidadEmisora;
	@SerializedName("importe_arancel")
	@Expose
	private String importeArancel;
	@SerializedName("importe_arancel_signo")
	@Expose
	private String importeArancelSigno;
	@SerializedName("iva_arancel")
	@Expose
	private String ivaArancel;
	@SerializedName("iva_arancel_signo")
	@Expose
	private String ivaArancelSigno;
	@SerializedName("promocion_cuotas_alfa")
	@Expose
	private String promocionCuotasAlfa;
	@SerializedName("tna")
	@Expose
	private String tna;
	@SerializedName("importe_costo_financiero")
	@Expose
	private String importeCostoFinanciero;
	@SerializedName("importe_costo_financiero_signo")
	@Expose
	private String importeCostoFinancieroSigno;
	@SerializedName("iva_costo_financiero")
	@Expose
	private String ivaCostoFinanciero;
	@SerializedName("iva_costo_financiero_signo")
	@Expose
	private String ivaCostoFinancieroSigno;
	@SerializedName("porcentaje_tasa_directa")
	@Expose
	private String porcentajeTasaDirecta;
	@SerializedName("importe_costo_tasa_dta")
	@Expose
	private String importeCostoTasaDta;
	@SerializedName("importe_costo_tasa_dta_signo")
	@Expose
	private String importeCostoTasaDtaSigno;
	@SerializedName("iva_costo_tasa_dta")
	@Expose
	private String ivaCostoTasaDta;
	@SerializedName("iva_costo_tasa_dta_signo")
	@Expose
	private String ivaCostoTasaDtaSigno;
	@SerializedName("nro_autoriz")
	@Expose
	private String nroAutoriz;
	@SerializedName("alicuota_iva_fo")
	@Expose
	private Object alicuotaIvaFo;
	@SerializedName("marca_cashback")
	@Expose
	private String marcaCashback;
	@SerializedName("revisado")
	@Expose
	private String revisado;
	@SerializedName("hash_modelo")
	@Expose
	private String hashModelo;
	@SerializedName("id_deb_aut")
	@Expose
	private String idDebAut;

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

	public String getFechaOperacion() {
		return fechaOperacion;
	}

	public void setFechaOperacion(String fechaOperacion) {
		this.fechaOperacion = fechaOperacion;
	}

	public String getCodigoMovimiento() {
		return codigoMovimiento;
	}

	public void setCodigoMovimiento(String codigoMovimiento) {
		this.codigoMovimiento = codigoMovimiento;
	}

	public String getCodigoOrigen() {
		return codigoOrigen;
	}

	public void setCodigoOrigen(String codigoOrigen) {
		this.codigoOrigen = codigoOrigen;
	}

	public String getCajaNroCintaPosnet() {
		return cajaNroCintaPosnet;
	}

	public void setCajaNroCintaPosnet(String cajaNroCintaPosnet) {
		this.cajaNroCintaPosnet = cajaNroCintaPosnet;
	}

	public String getCaratulaTerminalPosnet() {
		return caratulaTerminalPosnet;
	}

	public void setCaratulaTerminalPosnet(String caratulaTerminalPosnet) {
		this.caratulaTerminalPosnet = caratulaTerminalPosnet;
	}

	public String getResumenLotePosnet() {
		return resumenLotePosnet;
	}

	public void setResumenLotePosnet(String resumenLotePosnet) {
		this.resumenLotePosnet = resumenLotePosnet;
	}

	public String getCuponCuponPosnet() {
		return cuponCuponPosnet;
	}

	public void setCuponCuponPosnet(String cuponCuponPosnet) {
		this.cuponCuponPosnet = cuponCuponPosnet;
	}

	public String getCuotasPlan() {
		return cuotasPlan;
	}

	public void setCuotasPlan(String cuotasPlan) {
		this.cuotasPlan = cuotasPlan;
	}

	public String getCuotaVigente() {
		return cuotaVigente;
	}

	public void setCuotaVigente(String cuotaVigente) {
		this.cuotaVigente = cuotaVigente;
	}

	public String getImporteTotal() {
		return importeTotal;
	}

	public void setImporteTotal(String importeTotal) {
		this.importeTotal = importeTotal;
	}

	public String getImporteTotalSigno() {
		return importeTotalSigno;
	}

	public void setImporteTotalSigno(String importeTotalSigno) {
		this.importeTotalSigno = importeTotalSigno;
	}

	public String getImporteSinDto() {
		return importeSinDto;
	}

	public void setImporteSinDto(String importeSinDto) {
		this.importeSinDto = importeSinDto;
	}

	public String getImporteSinDtoSigno() {
		return importeSinDtoSigno;
	}

	public void setImporteSinDtoSigno(String importeSinDtoSigno) {
		this.importeSinDtoSigno = importeSinDtoSigno;
	}

	public String getImporteFinal() {
		return importeFinal;
	}

	public void setImporteFinal(String importeFinal) {
		this.importeFinal = importeFinal;
	}

	public String getImporteFinalSigno() {
		return importeFinalSigno;
	}

	public void setImporteFinalSigno(String importeFinalSigno) {
		this.importeFinalSigno = importeFinalSigno;
	}

	public String getPorcDesc() {
		return porcDesc;
	}

	public void setPorcDesc(String porcDesc) {
		this.porcDesc = porcDesc;
	}

	public String getMarcaError() {
		return marcaError;
	}

	public void setMarcaError(String marcaError) {
		this.marcaError = marcaError;
	}

	public String getTipoPlanCuotas() {
		return tipoPlanCuotas;
	}

	public void setTipoPlanCuotas(String tipoPlanCuotas) {
		this.tipoPlanCuotas = tipoPlanCuotas;
	}

	public String getNroTarjeta() {
		return nroTarjeta;
	}

	public void setNroTarjeta(String nroTarjeta) {
		this.nroTarjeta = nroTarjeta;
	}

	public String getMotivoRechazo1() {
		return motivoRechazo1;
	}

	public void setMotivoRechazo1(String motivoRechazo1) {
		this.motivoRechazo1 = motivoRechazo1;
	}

	public String getMotivoRechazo2() {
		return motivoRechazo2;
	}

	public void setMotivoRechazo2(String motivoRechazo2) {
		this.motivoRechazo2 = motivoRechazo2;
	}

	public String getMotivoRechazo3() {
		return motivoRechazo3;
	}

	public void setMotivoRechazo3(String motivoRechazo3) {
		this.motivoRechazo3 = motivoRechazo3;
	}

	public String getMotivoRechazo4() {
		return motivoRechazo4;
	}

	public void setMotivoRechazo4(String motivoRechazo4) {
		this.motivoRechazo4 = motivoRechazo4;
	}

	public String getFechaPresentOriginal() {
		return fechaPresentOriginal;
	}

	public void setFechaPresentOriginal(String fechaPresentOriginal) {
		this.fechaPresentOriginal = fechaPresentOriginal;
	}

	public String getMotivoReversion() {
		return motivoReversion;
	}

	public void setMotivoReversion(String motivoReversion) {
		this.motivoReversion = motivoReversion;
	}

	public String getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public String getMarcaCampana() {
		return marcaCampana;
	}

	public void setMarcaCampana(String marcaCampana) {
		this.marcaCampana = marcaCampana;
	}

	public String getCodigoCargoPago() {
		return codigoCargoPago;
	}

	public void setCodigoCargoPago(String codigoCargoPago) {
		this.codigoCargoPago = codigoCargoPago;
	}

	public String getEntidadEmisora() {
		return entidadEmisora;
	}

	public void setEntidadEmisora(String entidadEmisora) {
		this.entidadEmisora = entidadEmisora;
	}

	public String getImporteArancel() {
		return importeArancel;
	}

	public void setImporteArancel(String importeArancel) {
		this.importeArancel = importeArancel;
	}

	public String getImporteArancelSigno() {
		return importeArancelSigno;
	}

	public void setImporteArancelSigno(String importeArancelSigno) {
		this.importeArancelSigno = importeArancelSigno;
	}

	public String getIvaArancel() {
		return ivaArancel;
	}

	public void setIvaArancel(String ivaArancel) {
		this.ivaArancel = ivaArancel;
	}

	public String getIvaArancelSigno() {
		return ivaArancelSigno;
	}

	public void setIvaArancelSigno(String ivaArancelSigno) {
		this.ivaArancelSigno = ivaArancelSigno;
	}

	public String getPromocionCuotasAlfa() {
		return promocionCuotasAlfa;
	}

	public void setPromocionCuotasAlfa(String promocionCuotasAlfa) {
		this.promocionCuotasAlfa = promocionCuotasAlfa;
	}

	public String getTna() {
		return tna;
	}

	public void setTna(String tna) {
		this.tna = tna;
	}

	public String getImporteCostoFinanciero() {
		return importeCostoFinanciero;
	}

	public void setImporteCostoFinanciero(String importeCostoFinanciero) {
		this.importeCostoFinanciero = importeCostoFinanciero;
	}

	public String getImporteCostoFinancieroSigno() {
		return importeCostoFinancieroSigno;
	}

	public void setImporteCostoFinancieroSigno(String importeCostoFinancieroSigno) {
		this.importeCostoFinancieroSigno = importeCostoFinancieroSigno;
	}

	public String getIvaCostoFinanciero() {
		return ivaCostoFinanciero;
	}

	public void setIvaCostoFinanciero(String ivaCostoFinanciero) {
		this.ivaCostoFinanciero = ivaCostoFinanciero;
	}

	public String getIvaCostoFinancieroSigno() {
		return ivaCostoFinancieroSigno;
	}

	public void setIvaCostoFinancieroSigno(String ivaCostoFinancieroSigno) {
		this.ivaCostoFinancieroSigno = ivaCostoFinancieroSigno;
	}

	public String getPorcentajeTasaDirecta() {
		return porcentajeTasaDirecta;
	}

	public void setPorcentajeTasaDirecta(String porcentajeTasaDirecta) {
		this.porcentajeTasaDirecta = porcentajeTasaDirecta;
	}

	public String getImporteCostoTasaDta() {
		return importeCostoTasaDta;
	}

	public void setImporteCostoTasaDta(String importeCostoTasaDta) {
		this.importeCostoTasaDta = importeCostoTasaDta;
	}

	public String getImporteCostoTasaDtaSigno() {
		return importeCostoTasaDtaSigno;
	}

	public void setImporteCostoTasaDtaSigno(String importeCostoTasaDtaSigno) {
		this.importeCostoTasaDtaSigno = importeCostoTasaDtaSigno;
	}

	public String getIvaCostoTasaDta() {
		return ivaCostoTasaDta;
	}

	public void setIvaCostoTasaDta(String ivaCostoTasaDta) {
		this.ivaCostoTasaDta = ivaCostoTasaDta;
	}

	public String getIvaCostoTasaDtaSigno() {
		return ivaCostoTasaDtaSigno;
	}

	public void setIvaCostoTasaDtaSigno(String ivaCostoTasaDtaSigno) {
		this.ivaCostoTasaDtaSigno = ivaCostoTasaDtaSigno;
	}

	public String getNroAutoriz() {
		return nroAutoriz;
	}

	public void setNroAutoriz(String nroAutoriz) {
		this.nroAutoriz = nroAutoriz;
	}

	public Object getAlicuotaIvaFo() {
		return alicuotaIvaFo;
	}

	public void setAlicuotaIvaFo(Object alicuotaIvaFo) {
		this.alicuotaIvaFo = alicuotaIvaFo;
	}

	public String getMarcaCashback() {
		return marcaCashback;
	}

	public void setMarcaCashback(String marcaCashback) {
		this.marcaCashback = marcaCashback;
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

	public String getIdDebAut() {
		return idDebAut;
	}

	public void setIdDebAut(String idDebAut) {
		this.idDebAut = idDebAut;
	}

}
