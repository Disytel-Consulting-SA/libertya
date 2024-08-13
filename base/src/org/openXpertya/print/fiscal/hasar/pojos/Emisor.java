package org.openXpertya.print.fiscal.hasar.pojos;

public class Emisor { 
	public int Orden;
	public double CUIT;
	public String RazonSocial;
	public String NroRegistro;
	public int FechaInicioActividades;
	public String IngBrutos;
	public String TipoEmisor;
	public String TipoHabilitacion;
	public String LeyendaComprobantesAConLeyenda;
	public String LeyendaComprobantesM;
	
	public int getOrden() {
		return Orden;
	}
	public void setOrden(int orden) {
		Orden = orden;
	}
	public double getCUIT() {
		return CUIT;
	}
	public void setCUIT(double cUIT) {
		CUIT = cUIT;
	}
	public String getRazonSocial() {
		return RazonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		RazonSocial = razonSocial;
	}
	public String getNroRegistro() {
		return NroRegistro;
	}
	public void setNroRegistro(String nroRegistro) {
		NroRegistro = nroRegistro;
	}
	public int getFechaInicioActividades() {
		return FechaInicioActividades;
	}
	public void setFechaInicioActividades(int fechaInicioActividades) {
		FechaInicioActividades = fechaInicioActividades;
	}
	public String getIngBrutos() {
		return IngBrutos;
	}
	public void setIngBrutos(String ingBrutos) {
		IngBrutos = ingBrutos;
	}
	public String getTipoEmisor() {
		return TipoEmisor;
	}
	public void setTipoEmisor(String tipoEmisor) {
		TipoEmisor = tipoEmisor;
	}
	public String getTipoHabilitacion() {
		return TipoHabilitacion;
	}
	public void setTipoHabilitacion(String tipoHabilitacion) {
		TipoHabilitacion = tipoHabilitacion;
	}
	public String getLeyendaComprobantesAConLeyenda() {
		return LeyendaComprobantesAConLeyenda;
	}
	public void setLeyendaComprobantesAConLeyenda(String leyendaComprobantesAConLeyenda) {
		LeyendaComprobantesAConLeyenda = leyendaComprobantesAConLeyenda;
	}
	public String getLeyendaComprobantesM() {
		return LeyendaComprobantesM;
	}
	public void setLeyendaComprobantesM(String leyendaComprobantesM) {
		LeyendaComprobantesM = leyendaComprobantesM;
	}
}
