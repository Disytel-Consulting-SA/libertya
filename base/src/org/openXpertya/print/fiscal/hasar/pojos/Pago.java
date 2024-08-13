package org.openXpertya.print.fiscal.hasar.pojos;

public class Pago { 
	public int Orden;
	public String EsCambio;
	public double Monto;
	public String Descripcion;
	public String Tipo;
	public int Cuotas;
	public Desglose Desglose;
	public int getOrden() {
		return Orden;
	}
	public void setOrden(int orden) {
		Orden = orden;
	}
	public String getEsCambio() {
		return EsCambio;
	}
	public void setEsCambio(String esCambio) {
		EsCambio = esCambio;
	}
	public double getMonto() {
		return Monto;
	}
	public void setMonto(double monto) {
		Monto = monto;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public String getTipo() {
		return Tipo;
	}
	public void setTipo(String tipo) {
		Tipo = tipo;
	}
	public int getCuotas() {
		return Cuotas;
	}
	public void setCuotas(int cuotas) {
		Cuotas = cuotas;
	}
	public Desglose getDesglose() {
		return Desglose;
	}
	public void setDesglose(Desglose desglose) {
		Desglose = desglose;
	}
	
	
}
