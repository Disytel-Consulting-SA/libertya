package org.openXpertya.process.customImport.centralPos.pojos.cabal.movimientos;

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
	@SerializedName("hash_modelo")
	@Expose
	private String hash_modelo;
	@SerializedName("revisado")
	@Expose
	private String revisado;
	@SerializedName("numero_comercio")
	@Expose
	private String numero_comercio;
	@SerializedName("moneda_pago")
	@Expose
	private String moneda_pago;
	@SerializedName("fecha_pago")
	@Expose
	private String fecha_pago;
	@SerializedName("numero_liquidacion")
	@Expose
	private String numero_liquidacion;
	@SerializedName("codigo_operacion")
	@Expose
	private String codigo_operacion;
	@SerializedName("numero_tarjeta")
	@Expose
	private String numero_tarjeta;
	@SerializedName("fecha_compra")
	@Expose
	private String fecha_compra;
	@SerializedName("fecha_presentacion")
	@Expose
	private String fecha_presentacion;
	@SerializedName("numero_autorizacion")
	@Expose
	private String numero_autorizacion;
	@SerializedName("numero_lote")
	@Expose
	private String numero_lote;
	@SerializedName("numero_liquidacion_6pos")
	@Expose
	private String numero_liquidacion_6pos;
	@SerializedName("tasa_costo_financiero")
	@Expose
	private String tasa_costo_financiero;
	@SerializedName("codigo_ent_emis_tarj")
	@Expose
	private String codigo_ent_emis_tarj;
	@SerializedName("imp_venta")
	@Expose
	private String imp_venta;
	@SerializedName("subcodigo_operacion_val")
	@Expose
	private String subcodigo_operacion_val;
	@SerializedName("numero_cupon")
	@Expose
	private String numero_cupon;
	@SerializedName("codigo_cupon")
	@Expose
	private String codigo_cupon;
	@SerializedName("numero_terminal")
	@Expose
	private String numero_terminal;
	@SerializedName("motivo_contracargo")
	@Expose
	private String motivo_contracargo;
	@SerializedName("cantidad_cuotas")
	@Expose
	private String cantidad_cuotas;
	@SerializedName("importe_arn_comp")
	@Expose
	private String importe_arn_comp;
	@SerializedName("costo_fin_cup")
	@Expose
	private String costo_fin_cup;
	@SerializedName("cuit_comercio")
	@Expose
	private String cuit_comercio;
	@SerializedName("venta_tarj_prec_cod2")
	@Expose
	private String venta_tarj_prec_cod2;
	@SerializedName("bines")
	@Expose
	private String bines;
	@SerializedName("nombre_banco")
	@Expose
	private String nombre_banco;
	@SerializedName("producto")
	@Expose
	private String producto;
	
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
	public String getHash_modelo() {
		return hash_modelo;
	}
	public void setHash_modelo(String hash_modelo) {
		this.hash_modelo = hash_modelo;
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
	public String getMoneda_pago() {
		return moneda_pago;
	}
	public void setMoneda_pago(String moneda_pago) {
		this.moneda_pago = moneda_pago;
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
	public String getCodigo_operacion() {
		return codigo_operacion;
	}
	public void setCodigo_operacion(String codigo_operacion) {
		this.codigo_operacion = codigo_operacion;
	}
	public String getNumero_tarjeta() {
		return numero_tarjeta;
	}
	public void setNumero_tarjeta(String numero_tarjeta) {
		this.numero_tarjeta = numero_tarjeta;
	}
	public String getFecha_compra() {
		return fecha_compra;
	}
	public void setFecha_compra(String fecha_compra) {
		this.fecha_compra = fecha_compra;
	}
	public String getFecha_presentacion() {
		return fecha_presentacion;
	}
	public void setFecha_presentacion(String fecha_presentacion) {
		this.fecha_presentacion = fecha_presentacion;
	}
	public String getNumero_autorizacion() {
		return numero_autorizacion;
	}
	public void setNumero_autorizacion(String numero_autorizacion) {
		this.numero_autorizacion = numero_autorizacion;
	}
	public String getNumero_lote() {
		return numero_lote;
	}
	public void setNumero_lote(String numero_lote) {
		this.numero_lote = numero_lote;
	}
	public String getNumero_liquidacion_6pos() {
		return numero_liquidacion_6pos;
	}
	public void setNumero_liquidacion_6pos(String numero_liquidacion_6pos) {
		this.numero_liquidacion_6pos = numero_liquidacion_6pos;
	}
	public String getTasa_costo_financiero() {
		return tasa_costo_financiero;
	}
	public void setTasa_costo_financiero(String tasa_costo_financiero) {
		this.tasa_costo_financiero = tasa_costo_financiero;
	}
	public String getCodigo_ent_emis_tarj() {
		return codigo_ent_emis_tarj;
	}
	public void setCodigo_ent_emis_tarj(String codigo_ent_emis_tarj) {
		this.codigo_ent_emis_tarj = codigo_ent_emis_tarj;
	}
	public String getImp_venta() {
		return imp_venta;
	}
	public void setImp_venta(String imp_venta) {
		this.imp_venta = imp_venta;
	}
	public String getSubcodigo_operacion_val() {
		return subcodigo_operacion_val;
	}
	public void setSubcodigo_operacion_val(String subcodigo_operacion_val) {
		this.subcodigo_operacion_val = subcodigo_operacion_val;
	}
	public String getNumero_cupon() {
		return numero_cupon;
	}
	public void setNumero_cupon(String numero_cupon) {
		this.numero_cupon = numero_cupon;
	}
	public String getCodigo_cupon() {
		return codigo_cupon;
	}
	public void setCodigo_cupon(String codigo_cupon) {
		this.codigo_cupon = codigo_cupon;
	}
	public String getNumero_terminal() {
		return numero_terminal;
	}
	public void setNumero_terminal(String numero_terminal) {
		this.numero_terminal = numero_terminal;
	}
	public String getMotivo_contracargo() {
		return motivo_contracargo;
	}
	public void setMotivo_contracargo(String motivo_contracargo) {
		this.motivo_contracargo = motivo_contracargo;
	}
	public String getCantidad_cuotas() {
		return cantidad_cuotas;
	}
	public void setCantidad_cuotas(String cantidad_cuotas) {
		this.cantidad_cuotas = cantidad_cuotas;
	}
	public String getImporte_arn_comp() {
		return importe_arn_comp;
	}
	public void setImporte_arn_comp(String importe_arn_comp) {
		this.importe_arn_comp = importe_arn_comp;
	}
	public String getCosto_fin_cup() {
		return costo_fin_cup;
	}
	public void setCosto_fin_cup(String costo_fin_cup) {
		this.costo_fin_cup = costo_fin_cup;
	}
	public String getCuit_comercio() {
		return cuit_comercio;
	}
	public void setCuit_comercio(String cuit_comercio) {
		this.cuit_comercio = cuit_comercio;
	}
	public String getVenta_tarj_prec_cod2() {
		return venta_tarj_prec_cod2;
	}
	public void setVenta_tarj_prec_cod2(String venta_tarj_prec_cod2) {
		this.venta_tarj_prec_cod2 = venta_tarj_prec_cod2;
	}
	public String getBines() {
		return bines;
	}
	public void setBines(String bines) {
		this.bines = bines;
	}
	public String getNombre_banco() {
		return nombre_banco;
	}
	public void setNombre_banco(String nombre_banco) {
		this.nombre_banco = nombre_banco;
	}
	public String getProducto() {
		return producto;
	}
	public void setProducto(String producto) {
		this.producto = producto;
	}
}
