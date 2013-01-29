/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.print;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.compiere.swing.CComboBox;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CPrinter extends CComboBox implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public CPrinter() {
        super( getPrinterNames());

        // Set Default

        setValue( Ini.getProperty( Ini.P_PRINTER ));
        this.addActionListener( this );
    }    // CPrinter

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {}    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PrintService getPrintService() {
        String currentService = ( String )getSelectedItem();

        for( int i = 0;i < s_services.length;i++ ) {
            if( s_services[ i ].getName().equals( currentService )) {
                return s_services[ i ];
            }
        }

        return PrintServiceLookup.lookupDefaultPrintService();
    }    // getPrintService

//      private static PrintService[]   s_services = PrinterJob.lookupPrintServices();

    /** Descripción de Campos */

    private static PrintService[] s_services = PrintServiceLookup.lookupPrintServices( null,null );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static String[] getPrinterNames() {
        String[] retValue = new String[ s_services.length ];

        for( int i = 0;i < s_services.length;i++ ) {
            retValue[ i ] = s_services[ i ] == null ? "" : s_services[ i ].getName();
        }

        return retValue;
    }    // getPrintServiceNames

	/**
	 * @param printerName
	 *            nombre de impresora a buscar
	 * @return true si existe esa impresora para seleccionar. false si el
	 *         parámetro es null o vacío, si existen servicios de impresoras o
	 *         no exista esa impresora parámetro
	 */
    public static boolean existsPrinterName(String printerName){
    	String[] printerNames = getPrinterNames();
    	boolean found = false;
		if (!Util.isEmpty(printerName, true)){
	    	for (int i = 0; i < printerNames.length && !found; i++) {
				found = !Util.isEmpty(printerNames[i], true)
						&& printerNames[i].equalsIgnoreCase(printerName); 
			}
		}
		return found;    	
    }
    
    
    public static String getDefaultPrinterName(){
    	return Ini.getProperty( Ini.P_PRINTER );
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static PrinterJob getPrinterJob() {
        return getPrinterJob( Ini.getProperty( Ini.P_PRINTER ));
    }    // getPrinterJob

    /**
     * Descripción de Método
     *
     *
     * @param printerName
     *
     * @return
     */

    public static PrinterJob getPrinterJob( String printerName ) {
        PrinterJob   pj = PrinterJob.getPrinterJob();
        PrintService ps = null;

        // find printer service

        if( Util.isEmpty(printerName, true) ) {
            printerName = Ini.getProperty( Ini.P_PRINTER );
        }

        if( !Util.isEmpty(printerName, true) ) {

            // System.out.println("CPrinter.getPrinterJob - searching " + printerName);

            for( int i = 0;i < s_services.length;i++ ) {
                String serviceName = s_services[ i ].getName();

                if( printerName.equals( serviceName )) {
                    ps = s_services[ i ];

                    // System.out.println("CPrinter.getPrinterJob - found " + printerName);

                    break;
                }

                // System.out.println("CPrinter.getPrinterJob - not: " + serviceName);

            }
        }    // find printer service

        try {
            if( ps != null ) {
                pj.setPrintService( ps );
            }
        } catch( Exception e ) {
            System.err.println( "CPrinter.getPrinterJob - " + e.toString());
        }

        //

        String serviceName = pj.getPrintService().getName();

        if( (printerName != null) &&!printerName.equals( serviceName )) {
            System.err.println( "CPrinter.getPrinterJob - Not found: " + printerName + " - Used: " + serviceName );
        }

        return pj;
    }    // getPrinterJob
    
}    // CPrinter



/*
 *  @(#)CPrinter.java   23.03.06
 * 
 *  Fin del fichero CPrinter.java
 *  
 *  Versión 2.2
 *
 */
