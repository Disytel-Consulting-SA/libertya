package org.openXpertya.print.fiscal.hasar.pojos;

import java.util.List;


public class Documentos { 
	public List<Documento> Documento;
	public String type;
	public String text;
	
	public List<Documento> getDocumento() {
		return Documento;
	}
	public void setDocumento(List<Documento> documento) {
		Documento = documento;
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
