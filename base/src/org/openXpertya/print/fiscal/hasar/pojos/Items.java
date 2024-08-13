package org.openXpertya.print.fiscal.hasar.pojos;

import java.util.List;

public class Items { 
	public Cliente Cliente;
	public AtributosImpresion AtributosImpresion;
	public Emisor Emisor;
	public Apertura Apertura;
	public Venta Venta;
	public DiscriminacionIVA DiscriminacionIVA;
	public Totales Totales;
	public List<Pago> Pago;
	public List<LineaUsuario> LineaUsuario;
	public Cierre Cierre;
	public String type;
	public String text;
	public CierreDiarioEncabezado CierreDiarioEncabezado;
	public CierreDiarioContextoTipoComprobante CierreDiarioContextoTipoComprobante;
	public List<CierreDiarioInformacionDocumento> CierreDiarioInformacionDocumento;
	public List<CierreDiarioIVA> CierreDiarioIVA;
	public List<CierreDiarioTotales> CierreDiarioTotales;
	public List<CierreDiarioContextoTotales> CierreDiarioContextoTotales;
	public Cliente getCliente() {
		return Cliente;
	}
	public void setCliente(Cliente cliente) {
		Cliente = cliente;
	}
	public AtributosImpresion getAtributosImpresion() {
		return AtributosImpresion;
	}
	public void setAtributosImpresion(AtributosImpresion atributosImpresion) {
		AtributosImpresion = atributosImpresion;
	}
	public Emisor getEmisor() {
		return Emisor;
	}
	public void setEmisor(Emisor emisor) {
		Emisor = emisor;
	}
	public Apertura getApertura() {
		return Apertura;
	}
	public void setApertura(Apertura apertura) {
		Apertura = apertura;
	}
	public Venta getVenta() {
		return Venta;
	}
	public void setVenta(Venta venta) {
		Venta = venta;
	}
	public DiscriminacionIVA getDiscriminacionIVA() {
		return DiscriminacionIVA;
	}
	public void setDiscriminacionIVA(DiscriminacionIVA discriminacionIVA) {
		DiscriminacionIVA = discriminacionIVA;
	}
	public Totales getTotales() {
		return Totales;
	}
	public void setTotales(Totales totales) {
		Totales = totales;
	}
	public List<Pago> getPago() {
		return Pago;
	}
	public void setPago(List<Pago> pago) {
		Pago = pago;
	}
	public List<LineaUsuario> getLineaUsuario() {
		return LineaUsuario;
	}
	public void setLineaUsuario(List<LineaUsuario> lineaUsuario) {
		LineaUsuario = lineaUsuario;
	}
	public Cierre getCierre() {
		return Cierre;
	}
	public void setCierre(Cierre cierre) {
		Cierre = cierre;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public CierreDiarioEncabezado getCierreDiarioEncabezado() {
		return CierreDiarioEncabezado;
	}
	public void setCierreDiarioEncabezado(CierreDiarioEncabezado cierreDiarioEncabezado) {
		CierreDiarioEncabezado = cierreDiarioEncabezado;
	}
	public CierreDiarioContextoTipoComprobante getCierreDiarioContextoTipoComprobante() {
		return CierreDiarioContextoTipoComprobante;
	}
	public void setCierreDiarioContextoTipoComprobante(
			CierreDiarioContextoTipoComprobante cierreDiarioContextoTipoComprobante) {
		CierreDiarioContextoTipoComprobante = cierreDiarioContextoTipoComprobante;
	}
	public List<CierreDiarioInformacionDocumento> getCierreDiarioInformacionDocumento() {
		return CierreDiarioInformacionDocumento;
	}
	public void setCierreDiarioInformacionDocumento(
			List<CierreDiarioInformacionDocumento> cierreDiarioInformacionDocumento) {
		CierreDiarioInformacionDocumento = cierreDiarioInformacionDocumento;
	}
	public List<CierreDiarioIVA> getCierreDiarioIVA() {
		return CierreDiarioIVA;
	}
	public void setCierreDiarioIVA(List<CierreDiarioIVA> cierreDiarioIVA) {
		CierreDiarioIVA = cierreDiarioIVA;
	}
	public List<CierreDiarioTotales> getCierreDiarioTotales() {
		return CierreDiarioTotales;
	}
	public void setCierreDiarioTotales(List<CierreDiarioTotales> cierreDiarioTotales) {
		CierreDiarioTotales = cierreDiarioTotales;
	}
	public List<CierreDiarioContextoTotales> getCierreDiarioContextoTotales() {
		return CierreDiarioContextoTotales;
	}
	public void setCierreDiarioContextoTotales(List<CierreDiarioContextoTotales> cierreDiarioContextoTotales) {
		CierreDiarioContextoTotales = cierreDiarioContextoTotales;
	}
	
	
}
