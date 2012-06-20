package org.openXpertya.util;

import java.sql.Timestamp;
import java.util.Date;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MLocation;
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
 * DE REPLICACION Y NO ANTES
 * """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
 * 
 * @author usuario
 *
 */

public class ReplicationDocumentGenerator {


	public static final String DOC_INV		= "invoice";
	public static final String DOC_BP		= "bpartner";
	public static int heads_count = 1;
	public static int lines_count = 1;
	
    public static void main( String[] args ) {  
    	
    	// Parametro: validar
    	if (args.length == 0)
    	{
    		System.err.println("Se requiere al menos un parámetro indicando si deben generarse facturas (invoice) o entidades comerciales (bpartner).");
    		System.err.println("Alternativamente se puede indicar el numero de documentos a generar, y la cantidad de lineas en caso de ser facturas.  Ejemplo:");
    		System.err.println("  java -classpath lib/OXP.jar:lib/OXPLib.jar:lib/OXPXLib.jar org.openXpertya.util.ReplicationDocumentGenerator invoice 50 3");
    		System.exit(1);
    	}
    	// Cantidades
    	if (args.length > 1)
    		heads_count = Integer.parseInt(args[1]);
    	if (args.length > 2)
    		lines_count = Integer.parseInt(args[2]);
    	
    	// conexion a BBDD
        org.openXpertya.OpenXpertya.startupEnvironment( true );

        // ID de compañía y organización
        int thisOrgID  = DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y'" );
        int thisInstanceClient = DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y'" );
        Env.setContext(Env.getCtx(), "#AD_Client_ID", thisInstanceClient);
        Env.setContext(Env.getCtx(), "#AD_Org_ID", thisOrgID);

        if (DOC_INV.equalsIgnoreCase(args[0]))
        	generateInvoices(heads_count, lines_count);
        else
        	generateBPartners(heads_count);
       
    }
    
    /**
     * Creación de las facturas
     * @param invoiceCount: número de facturas a generar
     * @param linesPerInvoice: numero de líneas por factura
     */
    protected static void generateInvoices(int invoiceCount, int linesPerInvoice)
    {
    	
        // E.C. a utilizar en las facturas
        MBPartner bpartner = new MBPartner(Env.getCtx(), (PO.getAllIDs("C_BPartner", "value ilike 'CF' AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()), null))[0] , null);
        int invoiceVendorTypeID = (PO.getAllIDs("C_DocType", "doctypekey = 'VI' AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()), null))[0] ; 
        int taxIVA21 = (PO.getAllIDs("C_Tax", "name = 'IVA 21%' AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()), null))[0];
        int taxIVA105 = (PO.getAllIDs("C_Tax", "name = 'IVA 10,5%' AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()), null))[0];
        
        for (int i = 0; i < invoiceCount; i++)
        {
        	if (i % 10 == 0)
        		System.out.println (" Factura creada nro: " + i);
        	
        	// Encabezado
        	MInvoice invoice = new MInvoice(Env.getCtx(), 0, null);
            invoice.setClientOrg( Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx()));
            invoice.setC_DocTypeTarget_ID( invoiceVendorTypeID );    
            invoice.setBPartner(bpartner);
            invoice.setDescription(" Factura de Prueba Nro: " + i );
            invoice.setIsSOTrx(false);
            if (!invoice.save())
            	return;

            // Lineas
            for (int j = 0; j < linesPerInvoice; j++)
            {
                MInvoiceLine iLine = new MInvoiceLine( invoice );
                iLine.setQty( 1 );
                iLine.setPrice( Env.ONE);
               	iLine.setC_Tax_ID(j % 2 == 0 ? taxIVA21 : taxIVA105);	// variar un poco el impuesto
                if (!iLine.save())
                	return;
            }

            // Procesar
            if (linesPerInvoice > 0)
                invoice.processIt(DocAction.ACTION_Complete);
            
        	try {
        		Thread.sleep(1000);
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        
    }

    /**
     * Creación de las entidades comerciales
     * @param invoiceCount: número de facturas a generar
     * @param linesPerInvoice: numero de líneas por factura
     */
    protected static void generateBPartners(int count)
    {
    	
    	int bpGroupID = (PO.getAllIDs("C_BP_Group", "isDefault = 'Y' AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()), null))[0] ;
    	
    	for (int i = 0; i < count; i++)
        {
        	if (i % 10 == 0)
        		System.out.println (" BP creado nro: " + i);
        	
        	// Dirección
        	MLocation location = new MLocation(Env.getCtx(), 0, null);
        	location.setAddress1("Direccion");
        	location.save();
        	
        	// Entiad Comercial
        	MBPartner bpartner = new MBPartner(Env.getCtx(), 0, null);
        	String nameValue = "BPartner number: " + new Date().getTime();
        	bpartner.setName(nameValue);
        	bpartner.setValue(nameValue);
        	bpartner.setC_BP_Group_ID(bpGroupID);
        	bpartner.setAD_Language("es_AR");
        	bpartner.save();
        	
        	// Relacion entre direccion y EC
        	MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), 0, null);
        	bpLocation.setC_Location_ID(location.getC_Location_ID());
        	bpLocation.setC_BPartner_ID(bpartner.getC_BPartner_ID());
        	bpLocation.save();
        	
        	try {
        		Thread.sleep(1000);
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        }
    	
    }
}

