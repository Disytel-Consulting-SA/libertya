package org.openXpertya.print.fiscal.hasar.pojos;

public class Cierre { 
	public int Orden;
	public String TipoDocumento;
	public String SubTipoDocumento;
	public String CalificadorDocumento;
	public String Estacion;
	public String Registro;
	public double Version;
	public int NumeroDocumento;
	public String NumeroCompleto;
	public Totales Totales;
	public int NumeroCajero;
	
	public int getOrden() {
		return Orden;
	}
	public void setOrden(int orden) {
		Orden = orden;
	}
	public String getTipoDocumento() {
		return TipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		TipoDocumento = tipoDocumento;
	}
	public String getSubTipoDocumento() {
		return SubTipoDocumento;
	}
	public void setSubTipoDocumento(String subTipoDocumento) {
		SubTipoDocumento = subTipoDocumento;
	}
	public String getCalificadorDocumento() {
		return CalificadorDocumento;
	}
	public void setCalificadorDocumento(String calificadorDocumento) {
		CalificadorDocumento = calificadorDocumento;
	}
	public String getEstacion() {
		return Estacion;
	}
	public void setEstacion(String estacion) {
		Estacion = estacion;
	}
	public String getRegistro() {
		return Registro;
	}
	public void setRegistro(String registro) {
		Registro = registro;
	}
	public double getVersion() {
		return Version;
	}
	public void setVersion(double version) {
		Version = version;
	}
	public int getNumeroDocumento() {
		return NumeroDocumento;
	}
	public void setNumeroDocumento(int numeroDocumento) {
		NumeroDocumento = numeroDocumento;
	}
	public String getNumeroCompleto() {
		return NumeroCompleto;
	}
	public void setNumeroCompleto(String numeroCompleto) {
		NumeroCompleto = numeroCompleto;
	}
	public Totales getTotales() {
		return Totales;
	}
	public void setTotales(Totales totales) {
		Totales = totales;
	}
	public int getNumeroCajero() {
		return NumeroCajero;
	}
	public void setNumeroCajero(int numeroCajero) {
		NumeroCajero = numeroCajero;
	}
}
