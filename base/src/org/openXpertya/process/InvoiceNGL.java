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



package org.openXpertya.process;

import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InvoiceNGL extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_AcctSchema_ID = 0;

    /** Descripción de Campos */

    private int p_C_ConversionTypeReval_ID = 0;

    /** Descripción de Campos */

    private Timestamp p_DateReval = null;

    /** Descripción de Campos */

    private String p_APAR = "A";

    /** Descripción de Campos */

    private static String ONLY_AP = "P";

    /** Descripción de Campos */

    private static String ONLY_AR = "R";

    /** Descripción de Campos */

    private int p_C_Currency_ID = 0;

    /** Descripción de Campos */

    private int p_C_DocType_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_AcctSchema_ID" )) {
                p_C_AcctSchema_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_ConversionTypeReval_ID" )) {
                p_C_ConversionTypeReval_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "DateReval" )) {
                p_DateReval = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "APAR" )) {
                p_APAR = ( String )para[ i ].getParameter();
            } else if( name.equals( "C_Currency_ID" )) {
                p_C_Currency_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_DocType_ID" )) {
                p_C_DocType_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "C_AcctSchema_ID=" + p_C_AcctSchema_ID + ",C_ConversionTypeReval_ID=" + p_C_ConversionTypeReval_ID + ",DateReval=" + p_DateReval + ", APAR=" + p_APAR + ",C_Currency_ID=" + p_C_Currency_ID + ", C_DocType_ID=" + p_C_DocType_ID );

        // Parameter

        if( p_DateReval == null ) {
            p_DateReval = new Timestamp( System.currentTimeMillis());
        }

        // Delete - just to be sure

        String sql = "DELETE T_InvoiceGL WHERE AD_PInstance_ID=" + getAD_PInstance_ID();
        int no = DB.executeUpdate( sql,get_TrxName());

        if( no > 0 ) {
            log.info( "Deleted #" + no );
        }

        // Insert Trx

        String dateStr = DB.TO_DATE( p_DateReval,true );

        sql = "INSERT INTO T_InvoiceGL (AD_Client_ID, AD_Org_ID, IsActive, Created,CreatedBy, Updated,UpdatedBy," + " AD_PInstance_ID, C_Invoice_ID, Fact_Acct_ID," + " AmtRevalDr, AmtRevalCr," + " DateReval, C_ConversionTypeReval_ID, AmtRevalDrDiff, AmtRevalCrDiff) " + "SELECT i.AD_Client_ID, i.AD_Org_ID, i.IsActive, i.Created,i.CreatedBy, i.Updated,i.UpdatedBy," + getAD_PInstance_ID() + ", i.C_Invoice_ID, fa.Fact_Acct_ID," + " currencyConvert(fa.AmtSourceDr, i.C_Currency_ID, a.C_Currency_ID, " + dateStr + ", " + p_C_ConversionTypeReval_ID + ", i.AD_Client_ID, i.AD_Org_ID)," + " currencyConvert(fa.AmtSourceCr, i.C_Currency_ID, a.C_Currency_ID, " + dateStr + ", " + p_C_ConversionTypeReval_ID + ", i.AD_Client_ID, i.AD_Org_ID)," + dateStr + ", " + p_C_ConversionTypeReval_ID + ", 0, 0 " + "FROM C_Invoice i" + " INNER JOIN Fact_Acct fa ON (fa.AD_Table_ID=318 AND fa.Record_ID=i.C_Invoice_ID" + " AND (i.GrandTotal=fa.AmtSourceDr OR i.GrandTotal=fa.AmtSourceCr))" + " INNER JOIN C_AcctSchema a ON (fa.C_AcctSchema_ID=a.C_AcctSchema_ID) " + "WHERE i.C_Currency_ID<>a.C_Currency_ID" + " AND i.IsPaid='N'" + " AND fa.C_AcctSchema_ID=" + p_C_AcctSchema_ID;

        if( ONLY_AR.equals( p_APAR )) {
            sql += " AND i.IsSOTrx='Y'";
        } else if( ONLY_AP.equals( p_APAR )) {
            sql += " AND i.IsSOTrx='N'";
        }

        if( p_C_Currency_ID != 0 ) {
            sql += "AND i.C_Currency_ID=" + p_C_Currency_ID;
        }

        no = DB.executeUpdate( sql,get_TrxName());

        if( no > 0 ) {
            log.info( "Inserted #" + no );
        }

        // Calculate Difference

        sql = "UPDATE T_InvoiceGL gl " + "SET (AmtRevalDrDiff,AmtRevalCrDiff)=" + "(SELECT gl.AmtRevalDr-fa.AmtAcctDr, gl.AmtRevalCr-fa.AmtAcctCr " + "FROM Fact_Acct fa " + "WHERE gl.Fact_Acct_ID=fa.Fact_Acct_ID) " + "WHERE AD_PInstance_ID=" + getAD_PInstance_ID();

        if( no > 0 ) {
            log.info( "Updated #" + no );
        }

        //

        if( p_C_DocType_ID != 0 ) {
            createGLJournal();
        }

        return "#" + no;
    }    // doIt

    /**
     * Descripción de Método
     *
     */

    private void createGLJournal() {}    // createGLJournal
}    // InvoiceNGL



/*
 *  @(#)InvoiceNGL.java   02.07.07
 * 
 *  Fin del fichero InvoiceNGL.java
 *  
 *  Versión 2.2
 *
 */
