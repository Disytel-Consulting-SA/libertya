package org.openXpertya.print.fiscal.hasar.pojos;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Jornada { 
	public int Numero;
	public Date Fecha;
	public List<Documento> Documentos = new ArrayList<Documento>();
	
	public int getNumero() {
		return Numero;
	}
	public void setNumero(int numero) {
		Numero = numero;
	}
	public Date getFecha() {
		return Fecha;
	}
	public void setFecha(Date fecha) {
		Fecha = fecha;
	}
	public void addDocumento(Documento doc) {
		Documentos.add(doc);
	}
	public List<Documento> getDocumentos(){
		return Documentos;
	}
}
