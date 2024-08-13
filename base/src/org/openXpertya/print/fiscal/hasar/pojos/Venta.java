package org.openXpertya.print.fiscal.hasar.pojos;

public class Venta { 
	public int Orden;
	public String Descripcion;
	public Object CodigoInterno;
	public Object CodigoMatrix;
	public int Cantidad;
	public int UnidadesMatrix;
	public String UnidadMedida;
	public double Precio;
	public String CondicionIVA;
	public int TasaIVA;
	public String ImpuestoComoCoeficiente;
	public int ImpInt;
	public String ImpIntFijo;
	public String PorBaseImponible;
	public int Grupo;
	public Desglose Desglose;
	public int getOrden() {
		return Orden;
	}
	public void setOrden(int orden) {
		Orden = orden;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public Object getCodigoInterno() {
		return CodigoInterno;
	}
	public void setCodigoInterno(Object codigoInterno) {
		CodigoInterno = codigoInterno;
	}
	public Object getCodigoMatrix() {
		return CodigoMatrix;
	}
	public void setCodigoMatrix(Object codigoMatrix) {
		CodigoMatrix = codigoMatrix;
	}
	public int getCantidad() {
		return Cantidad;
	}
	public void setCantidad(int cantidad) {
		Cantidad = cantidad;
	}
	public int getUnidadesMatrix() {
		return UnidadesMatrix;
	}
	public void setUnidadesMatrix(int unidadesMatrix) {
		UnidadesMatrix = unidadesMatrix;
	}
	public String getUnidadMedida() {
		return UnidadMedida;
	}
	public void setUnidadMedida(String unidadMedida) {
		UnidadMedida = unidadMedida;
	}
	public double getPrecio() {
		return Precio;
	}
	public void setPrecio(double precio) {
		Precio = precio;
	}
	public String getCondicionIVA() {
		return CondicionIVA;
	}
	public void setCondicionIVA(String condicionIVA) {
		CondicionIVA = condicionIVA;
	}
	public int getTasaIVA() {
		return TasaIVA;
	}
	public void setTasaIVA(int tasaIVA) {
		TasaIVA = tasaIVA;
	}
	public String getImpuestoComoCoeficiente() {
		return ImpuestoComoCoeficiente;
	}
	public void setImpuestoComoCoeficiente(String impuestoComoCoeficiente) {
		ImpuestoComoCoeficiente = impuestoComoCoeficiente;
	}
	public int getImpInt() {
		return ImpInt;
	}
	public void setImpInt(int impInt) {
		ImpInt = impInt;
	}
	public String getImpIntFijo() {
		return ImpIntFijo;
	}
	public void setImpIntFijo(String impIntFijo) {
		ImpIntFijo = impIntFijo;
	}
	public String getPorBaseImponible() {
		return PorBaseImponible;
	}
	public void setPorBaseImponible(String porBaseImponible) {
		PorBaseImponible = porBaseImponible;
	}
	public int getGrupo() {
		return Grupo;
	}
	public void setGrupo(int grupo) {
		Grupo = grupo;
	}
	public Desglose getDesglose() {
		return Desglose;
	}
	public void setDesglose(Desglose desglose) {
		Desglose = desglose;
	}
	
	
}
