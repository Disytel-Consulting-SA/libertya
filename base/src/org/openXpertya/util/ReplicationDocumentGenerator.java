package org.openXpertya.util;

import java.sql.Timestamp;
import java.util.Date;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.PO;
import org.openXpertya.process.DocAction;


/**
 * GENERA GRAN CANTIDAD DE DOCUMENTOS
 * A FIN DE VERIFICAR LA PERFORMANCE
 * DE LA FUNCIONALIDAD DE REPLICACION
 * 
 * IMPORTANTE!!! GENERA LAS ENTRADAS EN LA BASE DE DATOS SEGUN EL ULTIMO LOGIN!!
 * """""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
 * 
 * IMPORTANTE!!! UTILIZAR ESTA FUNCIONALIDAD ""LUEGO"" DE REALIZAR LA CONFIGURACION 
 * DE REPLICACION Y NO ANTES, YA QUE EN CASO CONTRARIO NO SE BITACOREARAN LAS ENTRADAS!
 * """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
 * 
 * @author usuario
 *
 */

public class ReplicationDocumentGenerator {

	public static final int HEADS_COUNT = 1;
	public static final int LINES_COUNT = 1;
	
    public static void main( String[] args ) {  
    	
        org.openXpertya.OpenXpertya.startupEnvironment( true );

        // System.out.println(" Inicio: " + new Timestamp(new Date().getTime()));
        
        Env.setContext(Env.getCtx(), "#AD_Client_ID", 1010016);
        Env.setContext(Env.getCtx(), "#AD_Org_ID", 1010053);

        MBPartner bpartner = new MBPartner(Env.getCtx(), (PO.getAllIDs("C_BPartner", "value ilike 'CF' AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()), null))[0] , null);
        int invoiceVendorTypeID = (PO.getAllIDs("C_DocType", "doctypekey = 'VI' AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()), null))[0] ; 
        int taxIVA21 = (PO.getAllIDs("C_Tax", "name = 'IVA 21%' AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()), null))[0];
        int taxIVA105 = (PO.getAllIDs("C_Tax", "name = 'IVA 10,5%' AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()), null))[0];
        
        for (int i = 0; i < HEADS_COUNT; i++)
        {
        	if (i % 10 == 0)
        		System.out.println (" Factura creada nro: " + i);
        	
        	
        	
        	// Encabezado
        	TimeStatsLogger.beginTask("Creacion de Factura");
        	MInvoice invoice = new MInvoice(Env.getCtx(), 0, null);
        	TimeStatsLogger.endTask("Creacion de Factura");
        	
            invoice.setClientOrg( Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx()));
            invoice.setC_DocTypeTarget_ID( invoiceVendorTypeID );    
            invoice.setBPartner(bpartner);
            invoice.setDescription(" Factura de Prueba Nro: " + i );
            invoice.setIsSOTrx(false);
            TimeStatsLogger.beginTask("Persistencia de Factura");
            if (!invoice.save())
            	return;
            TimeStatsLogger.endTask("Persistencia de Factura");

            
            
            TimeStatsLogger.beginTask("Creacion de " + LINES_COUNT + " lineas de factura");
            
            // Lineas
            for (int j = 0; j < LINES_COUNT; j++)
            {
            	TimeStatsLogger.beginTask("Creacion de Linea de Factura");
                MInvoiceLine iLine = new MInvoiceLine( invoice );
                TimeStatsLogger.endTask("Creacion de Linea de Factura");
                iLine.setQty( 1 );
                iLine.setPrice( Env.ONE);
               	iLine.setC_Tax_ID(j % 2 == 0 ? taxIVA21 : taxIVA105);	// variar un poco el impuesto
               	TimeStatsLogger.beginTask("Persistencia de Linea de Factura");
                if (!iLine.save())
                	return;
                TimeStatsLogger.endTask("Persistencia de Linea de Factura");
            }
            
            TimeStatsLogger.endTask("Creacion de " + LINES_COUNT + " lineas de factura");
            
            if (LINES_COUNT > 0)
            {
                TimeStatsLogger.beginTask("Completar Factura");
                
                // Procesar
                invoice.processIt(DocAction.ACTION_Complete);
                
                TimeStatsLogger.endTask("Completar Factura");
            }
        }
        
        // System.out.println(" Fin: " + new Timestamp(new Date().getTime()));
        
    }
    
}
