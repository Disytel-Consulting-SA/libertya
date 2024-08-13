package org.openXpertya.print.fiscal.hasar.pojos;

public class Documento { 
	public int Orden;
	public Items Items;
	
    public Documento(int orden) {
        this.Orden = orden;
    }
    
    public Documento(String orden) {
        this.Orden = Integer.parseInt(orden);
    }

    public Documento() {
    	
    }
	
	public int getOrden() {
		return Orden;
	}
	public void setOrden(int orden) {
		Orden = orden;
	}
	public Items getItems() {
		return Items;
	}
	public void setItems(Items items) {
		Items = items;
	}
	
}
