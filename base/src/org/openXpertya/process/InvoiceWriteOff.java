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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InvoiceWriteOff extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int p_C_Invoice_ID = 0;

    /** Descripción de Campos */

    private BigDecimal p_MaxInvWriteOffAmt = Env.ZERO;

    /** Descripción de Campos */

    private Timestamp p_DateInvoiced_From = null;

    /** Descripción de Campos */

    private Timestamp p_DateInvoiced_To = null;

    /** Descripción de Campos */

    private Timestamp p_DateAcct = null;

    /** Descripción de Campos */

    private boolean p_IsSimulation = true;

    /** Descripción de Campos */

    private MAllocationHdr m_alloc = null;

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
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_Invoice_ID" )) {
                p_C_Invoice_ID = para[ i ].getParameterAsInt();

                //

            } else if( name.equals( "MaxInvWriteOffAmt" )) {
                p_MaxInvWriteOffAmt = ( BigDecimal )para[ i ].getParameter();
            } else if( name.equals( "DateInvoiced" )) {
                p_DateInvoiced_From = ( Timestamp )para[ i ].getParameter();
                p_DateInvoiced_To   = ( Timestamp )para[ i ].getParameter_To();
            } else if( name.equals( "DateAcct" )) {
                p_DateAcct = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "IsSimulation" )) {
                p_IsSimulation = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
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
        log.info( "doIt - C_BPartner_ID=" + p_C_BPartner_ID + ", C_Invoice_ID=" + p_C_Invoice_ID );

        if( (p_C_BPartner_ID == 0) && (p_C_Invoice_ID == 0) ) {
            throw new IllegalArgumentException( "Select Business Partner or Invoice" );
        }

        StringBuffer sql = new StringBuffer( "SELECT C_Invoice_ID,DocumentNo,DateInvoiced," + " C_Currency_ID,GrandTotal, invoiceOpen(C_Invoice_ID, 0) AS OpenAmt " + "FROM C_Invoice WHERE " );

        if( p_C_Invoice_ID != 0 ) {
            sql.append( "C_Invoice_ID=" ).append( p_C_Invoice_ID );
        } else {
            sql.append( "IsSOTrx='Y' AND C_BPartner_ID=" ).append( p_C_BPartner_ID );

            if( (p_DateInvoiced_From != null) && (p_DateInvoiced_To != null) ) {
                sql.append( " AND TRIM(DateInvoiced) BETWEEN " ).append( DB.TO_DATE( p_DateInvoiced_From,true )).append( " AND " ).append( DB.TO_DATE( p_DateInvoiced_To,true ));
            } else if( p_DateInvoiced_From != null ) {
                sql.append( " AND TRIM(DateInvoiced) >= " ).append( DB.TO_DATE( p_DateInvoiced_From,true ));
            } else if( p_DateInvoiced_To != null ) {
                sql.append( " AND TRIM(DateInvoiced) <= " ).append( DB.TO_DATE( p_DateInvoiced_To,true ));
            }
        }

        sql.append( " AND IsPaid='N'" );
        log.fine( "doIt - " + sql );

        //

        int               counter = 0;
        PreparedStatement pstmt   = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                if( writeOff( rs.getInt( 1 ),rs.getString( 2 ),rs.getTimestamp( 3 ),rs.getInt( 4 ),rs.getBigDecimal( 6 ))) {
                    ;
                }

                counter++;
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return "#" + counter;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param C_Invoice_ID
     * @param DocumentNo
     * @param DateInvoiced
     * @param C_Currency_ID
     * @param OpenAmt
     *
     * @return
     */

    private boolean writeOff( int C_Invoice_ID,String DocumentNo,Timestamp DateInvoiced,int C_Currency_ID,BigDecimal OpenAmt ) {

        // Nothing to do

        if( (OpenAmt == null) || (OpenAmt.compareTo( Env.ZERO ) == 0) ) {
            return false;
        }

        //

        if( p_IsSimulation ) {
            addLog( C_Invoice_ID,DateInvoiced,OpenAmt,DocumentNo );

            return true;
        }

        // Allocation

        MAllocationHdr alloc = new MAllocationHdr( getCtx(),true,p_DateAcct,C_Currency_ID,getProcessInfo().getTitle() + " #" + getAD_PInstance_ID(),get_TrxName());

        alloc.save();

        // Line

        MAllocationLine aLine = new MAllocationLine( alloc,Env.ZERO,Env.ZERO,OpenAmt,Env.ZERO );

        aLine.setC_Invoice_ID( C_Invoice_ID );
        aLine.save();

        // Process It

        if( alloc.processIt( DocAction.ACTION_Complete )) {
            alloc.save();
            addLog( C_Invoice_ID,DateInvoiced,OpenAmt,DocumentNo );

            return true;
        }

        // Error

        log.log( Level.SEVERE,"writeOff - C_Invoice_ID=" + C_Invoice_ID );

        return false;
    }    // writeOff
}    // InvoiceWriteOff



/*
 *  @(#)InvoiceWriteOff.java   02.07.07
 * 
 *  Fin del fichero InvoiceWriteOff.java
 *  
 *  Versión 2.2
 *
 */
