package org.openXpertya.util;

public interface AccumulableTask {

	public final String DB_PSTATEMENT 	= "PreparedStatement en BBDD";
	public final String DB_MODIFICATION = "Modificacion en BBDD";
	public final String DB_SELECTION  	= "Seleccion en BBDD";
	
	public final String NEW_ORDER_LINE		= "Instanciar lineas de pedido";
	public final String NEW_INVOICE_LINE	= "Instanciar lineas de factura";
	public final String NEW_INOUT_LINE		= "Instanciar lineas de remito";
	
	public final String SAVE_ORDER_LINE		= "Persistir lineas de pedido";
	public final String SAVE_INVOICE_LINE	= "Persistir lineas de factura";
	public final String SAVE_INOUT_LINE		= "Persistir lineas de remito";

}
