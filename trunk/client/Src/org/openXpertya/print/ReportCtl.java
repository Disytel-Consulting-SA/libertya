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

import java.util.logging.Level;

import org.openXpertya.apps.ADialog;
import org.openXpertya.model.MPaySelectionCheck;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ReportCtl {
	
	/** dREHER, codigo compatibilidad Jasper Adempiere
     * Constants used to pass process parameters to Jasper Process
     */
    public static final String PARAM_PRINTER_NAME = "PRINTER_NAME";
    public static final String PARAM_PRINT_FORMAT = "PRINT_FORMAT";
    public static final String PARAM_PRINT_INFO = "PRINT_INFO";

    /**
     * Constructor de la clase ...
     *
     */

    private ReportCtl() {}    // ReportCtrl

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( ReportCtl.class );

    /**
     * Descripción de Método
     *
     *
     * @param pi
     * @param IsDirectPrint
     *
     * @return
     */

    static public boolean start( ProcessInfo pi,boolean IsDirectPrint ) {
        s_log.info( "start - " + pi );

        if( pi.getAD_Process_ID() == 110 ) {    // C_Order
            return startDocumentPrint( ReportEngine.ORDER,pi.getRecord_ID(),IsDirectPrint );
        } else if( pi.getAD_Process_ID() == 116 ) {    // C_Invoice
            return startDocumentPrint( ReportEngine.INVOICE,pi.getRecord_ID(),IsDirectPrint );
        } else if( pi.getAD_Process_ID() == 117 ) {    // M_InOut
            return startDocumentPrint( ReportEngine.SHIPMENT,pi.getRecord_ID(),IsDirectPrint );
        } else if( pi.getAD_Process_ID() == 217 ) {    // C_Project
            return startDocumentPrint( ReportEngine.PROJECT,pi.getRecord_ID(),IsDirectPrint );
        } else if( pi.getAD_Process_ID() == 276 ) {    // C_RfQResponse
            return startDocumentPrint( ReportEngine.RFQ,pi.getRecord_ID(),IsDirectPrint );
        } else if( pi.getAD_Process_ID() == 313 ) {    // C_Payment
            return startCheckPrint( pi.getRecord_ID(),IsDirectPrint );
        } else if( pi.getAD_Process_ID() == 159 ) {    // Dunning
            return startDocumentPrint( ReportEngine.DUNNING,pi.getRecord_ID(),IsDirectPrint );
        } else if( (pi.getAD_Process_ID() == 202           // Financial Report
                ) || (pi.getAD_Process_ID() == 204) ) {    // Financial Statement
            return startFinReport( pi );
        }

        return startStandardReport( pi,IsDirectPrint );
    }    // create

    /**
     * Descripción de Método
     *
     *
     * @param pi
     * @param IsDirectPrint
     *
     * @return
     */

    static public boolean startStandardReport( ProcessInfo pi,boolean IsDirectPrint ) {
        ReportEngine re = ReportEngine.get( Env.getCtx(),pi );

        if( re == null ) {
            pi.setSummary( "No ReportEngine" );

            return false;
        }

        if( IsDirectPrint ) {
            re.print();
        } else {
            new Viewer( re );
        }

        return true;
    }    // startStandardReport

    /**
     * Descripción de Método
     *
     *
     * @param pi
     *
     * @return
     */

    static public boolean startFinReport( ProcessInfo pi ) {
        int AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());

        // Create Query from Parameters

        String TableName = (pi.getAD_Process_ID() == 202)
                           ?"T_Report"
                           :"T_ReportStatement";
        MQuery query     = MQuery.get( Env.getCtx(),pi.getAD_PInstance_ID(),TableName );

        // Get PrintFormat

        MPrintFormat format = ( MPrintFormat )pi.getTransientObject();

        if( format == null ) {
            format = ( MPrintFormat )pi.getSerializableObject();
        }

        if( format == null ) {
            s_log.log( Level.SEVERE,"startFinReport - No PrintFormat" );

            return false;
        }

        PrintInfo    info = new PrintInfo( pi );
        ReportEngine re   = new ReportEngine( Env.getCtx(),format,query,info );

        new Viewer( re );

        return true;
    }    // startFinReport

    /**
     * Descripción de Método
     *
     *
     * @param type
     * @param Record_ID
     * @param IsDirectPrint
     *
     * @return
     */

    public static boolean startDocumentPrint( int type,int Record_ID,boolean IsDirectPrint ) {
        ReportEngine re = ReportEngine.get( Env.getCtx(),type,Record_ID );

        if( re == null ) {
            ADialog.error( 0,null,"NoDocPrintFormat" );

            return false;
        }

        if( IsDirectPrint ) {
            re.print();
            ReportEngine.printConfirm( type,Record_ID );
        } else {
            new Viewer( re );
        }

        return true;
    }    // StartDocumentPrint

    /**
     * Descripción de Método
     *
     *
     * @param C_Payment_ID
     * @param IsDirectPrint
     *
     * @return
     */

    public static boolean startCheckPrint( int C_Payment_ID,boolean IsDirectPrint ) {
        int                C_PaySelectionCheck_ID = 0;
        MPaySelectionCheck psc                    = MPaySelectionCheck.getOfPayment( Env.getCtx(),C_Payment_ID,null );

        if( psc != null ) {
            C_PaySelectionCheck_ID = psc.getC_PaySelectionCheck_ID();
        } else {
            psc = MPaySelectionCheck.createForPayment( Env.getCtx(),C_Payment_ID,null );

            if( psc != null ) {
                C_PaySelectionCheck_ID = psc.getC_PaySelectionCheck_ID();
            }
        }

        return startDocumentPrint( ReportEngine.CHECK,C_PaySelectionCheck_ID,IsDirectPrint );
    }    // startCheckPrint
}    // ReportCtl



/*
 *  @(#)ReportCtl.java   02.07.07
 * 
 *  Fin del fichero ReportCtl.java
 *  
 *  Versión 2.2
 *
 */
