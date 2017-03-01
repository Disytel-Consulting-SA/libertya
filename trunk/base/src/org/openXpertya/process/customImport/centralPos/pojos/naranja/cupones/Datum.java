package org.openXpertya.process.customImport.centralPos.pojos.naranja.cupones;

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
	@SerializedName("fecha_presentacion")
	@Expose
	private String fechaPresentacion;
	@SerializedName("numero_recap")
	@Expose
	private String numeroRecap;
	@SerializedName("cupon")
	@Expose
	private String cupon;
	@SerializedName("tarjeta")
	@Expose
	private String tarjeta;
	@SerializedName("fecha_compra")
	@Expose
	private String fechaCompra;
	@SerializedName("moneda")
	@Expose
	private String moneda;
	@SerializedName("plan")
	@Expose
	private String plan;
	@SerializedName("compra")
	@Expose
	private String compra;
	@SerializedName("entrega")
	@Expose
	private String entrega;
	@SerializedName("fecha_cuota")
	@Expose
	private String fechaCuota;
	@SerializedName("importe_cuota")
	@Expose
	private String importeCuota;
	@SerializedName("numero_cuota")
	@Expose
	private String numeroCuota;
	@SerializedName("tipo_mov")
	@Expose
	private String tipoMov;
	@SerializedName("estado")
	@Expose
	private String estado;
	@SerializedName("descripcion")
	@Expose
	private String descripcion;
	@SerializedName("codigo_aut")
	@Expose
	private String codigoAut;
	@SerializedName("tipo_op")
	@Expose
	private String tipoOp;
	@SerializedName("numero_devolucion")
	@Expose
	private String numeroDevolucion;
	@SerializedName("tipo_cd")
	@Expose
	private String tipoCd;
	@SerializedName("nro_terminal")
	@Expose
	private String nroTerminal;
	@SerializedName("nro_lote")
	@Expose
	private String nroLote;
	@SerializedName("codigo_especial")
	@Expose
	private String codigoEspecial;
	@SerializedName("nro_debito")
	@Expose
	private String nroDebito;
	@SerializedName("marca")
	@Expose
	private String marca;
	@SerializedName("num_cliente")
	@Expose
	private String numCliente;
	@SerializedName("fecha_pago")
	@Expose
	private String fechaPago;
	@SerializedName("bines")
	@Expose
	private String bines;
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

	public String getFechaPresentacion() {
		return fechaPresentacion;
	}

	public void setFechaPresentacion(String fechaPresentacion) {
		this.fechaPresentacion = fechaPresentacion;
	}

	public String getNumeroRecap() {
		return numeroRecap;
	}

	public void setNumeroRecap(String numeroRecap) {
		this.numeroRecap = numeroRecap;
	}

	public String getCupon() {
		return cupon;
	}

	public void setCupon(String cupon) {
		this.cupon = cupon;
	}

	public String getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}

	public String getFechaCompra() {
		return fechaCompra;
	}

	public void setFechaCompra(String fechaCompra) {
		this.fechaCompra = fechaCompra;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getCompra() {
		return compra;
	}

	public void setCompra(String compra) {
		this.compra = compra;
	}

	public String getEntrega() {
		return entrega;
	}

	public void setEntrega(String entrega) {
		this.entrega = entrega;
	}

	public String getFechaCuota() {
		return fechaCuota;
	}

	public void setFechaCuota(String fechaCuota) {
		this.fechaCuota = fechaCuota;
	}

	public String getImporteCuota() {
		return importeCuota;
	}

	public void setImporteCuota(String importeCuota) {
		this.importeCuota = importeCuota;
	}

	public String getNumeroCuota() {
		return numeroCuota;
	}

	public void setNumeroCuota(String numeroCuota) {
		this.numeroCuota = numeroCuota;
	}

	public String getTipoMov() {
		return tipoMov;
	}

	public void setTipoMov(String tipoMov) {
		this.tipoMov = tipoMov;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCodigoAut() {
		return codigoAut;
	}

	public void setCodigoAut(String codigoAut) {
		this.codigoAut = codigoAut;
	}

	public String getTipoOp() {
		return tipoOp;
	}

	public void setTipoOp(String tipoOp) {
		this.tipoOp = tipoOp;
	}

	public String getNumeroDevolucion() {
		return numeroDevolucion;
	}

	public void setNumeroDevolucion(String numeroDevolucion) {
		this.numeroDevolucion = numeroDevolucion;
	}

	public String getTipoCd() {
		return tipoCd;
	}

	public void setTipoCd(String tipoCd) {
		this.tipoCd = tipoCd;
	}

	public String getNroTerminal() {
		return nroTerminal;
	}

	public void setNroTerminal(String nroTerminal) {
		this.nroTerminal = nroTerminal;
	}

	public String getNroLote() {
		return nroLote;
	}

	public void setNroLote(String nroLote) {
		this.nroLote = nroLote;
	}

	public String getCodigoEspecial() {
		return codigoEspecial;
	}

	public void setCodigoEspecial(String codigoEspecial) {
		this.codigoEspecial = codigoEspecial;
	}

	public String getNroDebito() {
		return nroDebito;
	}

	public void setNroDebito(String nroDebito) {
		this.nroDebito = nroDebito;
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

	public String getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(String fechaPago) {
		this.fechaPago = fechaPago;
	}

	public String getBines() {
		return bines;
	}

	public void setBines(String bines) {
		this.bines = bines;
	}

	public String getRevisado() {
		return revisado;
	}

	public void setRevisado(String revisado) {
		this.revisado = revisado;
	}

}
