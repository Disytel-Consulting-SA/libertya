package org.openXpertya.electronicInvoice;

/**
 * Interface para la gestion de Facturas Electronicas mediante WS de AFIP.
 * La interfaz se definió respetando dentro de las posibilidades la 
 * tradicional implementacion Wsfe que se apoyaba en pyafipws, a fin 
 * de evitar generar mayor impacto en la logica de la clase MInvoice
 */

import java.sql.Timestamp;

public interface ElectronicInvoiceInterface {
	
	/** Registra una factura electronica en el site de AFIP mediante WSFEV1 */
	public String generateCAE();
	
	/** Retorna el CAE obtenido */
	public String getCAE();

	/** Retorna el Vencimiento del CAE */
	public Timestamp getDateCae();

	/** Retorna el Numero de comprobante obtenido */
	public String getNroCbte();
	
	/** Retorna Error(es) al obtener el CAE */
	public String getErrorMsg();



}
