package org.fidelius.pojos;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CuponPago {

	private String factura = "";
	private String nroCupon = "";
	private String nroLote = "";
	private Timestamp fechaPago = null;
	private BigDecimal importe = null;
	private String codigoComercio = "";
	private String tarjeta = "";
	private String nroTarjeta = "";
	private int cuotas = 0;
	private int id = 0;
		
	public CuponPago() {}

	public String getFactura() {
		return factura;
	}

	public void setFactura(String factura) {
		this.factura = factura;
	}

	public String getNroCupon() {
		return nroCupon;
	}

	public void setNroCupon(String nroCupon) {
		this.nroCupon = nroCupon;
	}

	public String getNroLote() {
		return nroLote;
	}

	public void setNroLote(String nroLote) {
		this.nroLote = nroLote;
	}

	public Timestamp getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(Timestamp fechaPago) {
		this.fechaPago = fechaPago;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public String getCodigoComercio() {
		return codigoComercio;
	}

	public void setCodigoComercio(String codigoComercio) {
		this.codigoComercio = codigoComercio;
	}

	public String getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}

	public String getNroTarjeta() {
		return nroTarjeta;
	}

	public void setNroTarjeta(String nroTarjeta) {
		this.nroTarjeta = nroTarjeta;
	}

	public int getCuotas() {
		return cuotas;
	}

	public void setCuotas(int cuotas) {
		this.cuotas = cuotas;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
