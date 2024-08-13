package org.openXpertya.print.fiscal.hasar.pojos;

public class Descarga { 

	public Jornada Jornada;
	public String type;
	public String text;
	
	public Jornada getJornada() {
		return Jornada;
	}
	public void setJornada(Jornada jornada) {
		Jornada = jornada;
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
