package org.openXpertya.print.fiscal.hasar.pojos;

public class DiscriminacionIVA { 
	public int Orden;
	public String CondicionIVA;
	public int TasaIVA;
	public double Base;
	public double MontoIVA;
	public IVA IVA;
	public String type;
	public String text;
	public int getOrden() {
		return Orden;
	}
	public void setOrden(int orden) {
		Orden = orden;
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
	public double getBase() {
		return Base;
	}
	public void setBase(double base) {
		Base = base;
	}
	public double getMontoIVA() {
		return MontoIVA;
	}
	public void setMontoIVA(double montoIVA) {
		MontoIVA = montoIVA;
	}
	public IVA getIVA() {
		return IVA;
	}
	public void setIVA(IVA iVA) {
		IVA = iVA;
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
	
	
}
