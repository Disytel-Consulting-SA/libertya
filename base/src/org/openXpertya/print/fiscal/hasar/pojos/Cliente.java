package org.openXpertya.print.fiscal.hasar.pojos;

public class Cliente { 
	public int Orden;
	public String NroDocumento;
	public int NroSecuencia;
	public String TipoDocumento;
	public String TipoCliente;
	public String Nombre;
	public String Direccion;
	
	public int getOrden() {
		return Orden;
	}
	public void setOrden(int orden) {
		Orden = orden;
	}
	public String getNroDocumento() {
		return NroDocumento;
	}
	public void setNroDocumento(String nroDocumento) {
		NroDocumento = nroDocumento;
	}
	public int getNroSecuencia() {
		return NroSecuencia;
	}
	public void setNroSecuencia(int nroSecuencia) {
		NroSecuencia = nroSecuencia;
	}
	public String getTipoDocumento() {
		return TipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		TipoDocumento = tipoDocumento;
	}
	public String getTipoCliente() {
		return TipoCliente;
	}
	public void setTipoCliente(String tipoCliente) {
		TipoCliente = tipoCliente;
	}
	public String getNombre() {
		return Nombre;
	}
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	public String getDireccion() {
		return Direccion;
	}
	public void setDireccion(String direccion) {
		Direccion = direccion;
	}
}
