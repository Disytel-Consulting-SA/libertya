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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MTimeExpense;
import org.openXpertya.model.MTimeExpenseLine;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ExpenseAPInvoice extends SvrProcess {

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_DateFrom = null;

    /** Descripción de Campos */

    private Timestamp m_DateTo = null;

    /** Descripción de Campos */

    private int m_noInvoices = 0;

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
                m_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "DateReport" )) {
                m_DateFrom = ( Timestamp )para[ i ].getParameter();
                m_DateTo   = ( Timestamp )para[ i ].getParameter_To();
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
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
        StringBuffer sql = new StringBuffer( "SELECT * " + "FROM S_TimeExpense e " + "WHERE e.Processed='Y'" + " AND e.AD_Client_ID=?" );    // #1

        if( m_C_BPartner_ID != 0 ) {
            sql.append( " AND e.C_BPartner_ID=?" );    // #2
        }

        if( m_DateFrom != null ) {
            sql.append( " AND e.DateReport >= ?" );    // #3
        }

        if( m_DateTo != null ) {
            sql.append( " AND e.DateReport <= ?" );    // #4
        }

        sql.append( " AND EXISTS (SELECT * FROM S_TimeExpenseLine el " + "WHERE e.S_TimeExpense_ID=el.S_TimeExpense_ID" + " AND el.C_InvoiceLine_ID IS NULL" + " AND el.ConvertedAmt<>0) " + "ORDER BY e.C_BPartner_ID, e.S_TimeExpense_ID" );

        //

        int      old_BPartner_ID = -1;
        MInvoice invoice         = null;

        //

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());

            int par = 1;

            pstmt.setInt( par++,getAD_Client_ID());

            if( m_C_BPartner_ID != 0 ) {
                pstmt.setInt( par++,m_C_BPartner_ID );
            }

            if( m_DateFrom != null ) {
                pstmt.setTimestamp( par++,m_DateFrom );
            }

            if( m_DateTo != null ) {
                pstmt.setTimestamp( par++,m_DateTo );
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next())    // ********* Expense Line Loop
            {
                MTimeExpense te = new MTimeExpense( getCtx(),rs,get_TrxName());

                // New BPartner - New Order

                if( te.getC_BPartner_ID() != old_BPartner_ID ) {
                    completeInvoice( invoice );

                    MBPartner bp = new MBPartner( getCtx(),te.getC_BPartner_ID(),get_TrxName());

                    //

                    log.info( "doIt - New Invoice for " + bp );
                    invoice = new MInvoice( getCtx(),0,null );
                    invoice.setClientOrg( te.getAD_Client_ID(),te.getAD_Org_ID());
                    invoice.setC_DocTypeTarget_ID( MDocType.DOCBASETYPE_APInvoice );    // API
                    invoice.setDocumentNo( te.getDocumentNo());

                    //

                    invoice.setBPartner( bp );

                    if( invoice.getC_BPartner_Location_ID() == 0 ) {
                        log.log( Level.SEVERE,"doIt - No BP Location: " + bp );
                        addLog( 0,te.getDateReport(),null,"No Location: " + te.getDocumentNo() + " " + bp.getName());
                        invoice = null;

                        break;
                    }

                    invoice.setM_PriceList_ID( te.getM_PriceList_ID());
                    invoice.setSalesRep_ID( te.getDoc_User_ID());

                    String descr = Msg.translate( getCtx(),"S_TimeExpense_ID" ) + ": " + te.getDocumentNo() + " " + DisplayType.getDateFormat( DisplayType.Date ).format( te.getDateReport());

                    invoice.setDescription( descr );

                    if( !invoice.save()) {
                        new IllegalStateException( "Cannot save Invoice" );
                    }

                    old_BPartner_ID = bp.getC_BPartner_ID();
                }

                MTimeExpenseLine[] tel = te.getLines( false );

                for( int i = 0;i < tel.length;i++ ) {
                    MTimeExpenseLine line = tel[ i ];

                    // Already Invoiced or nothing to be reimbursed

                    if( (line.getC_InvoiceLine_ID() != 0) || (Env.ZERO.compareTo( line.getQtyReimbursed()) == 0) || (Env.ZERO.compareTo( line.getPriceReimbursed()) == 0) ) {
                        continue;
                    }

                    // Update Header info

                    if( (line.getC_Activity_ID() != 0) && (line.getC_Activity_ID() != invoice.getC_Activity_ID())) {
                        invoice.setC_Activity_ID( line.getC_Activity_ID());
                    }

                    if( (line.getC_Campaign_ID() != 0) && (line.getC_Campaign_ID() != invoice.getC_Campaign_ID())) {
                        invoice.setC_Campaign_ID( line.getC_Campaign_ID());
                    }

                    if( (line.getC_Project_ID() != 0) && (line.getC_Project_ID() != invoice.getC_Project_ID())) {
                        invoice.setC_Project_ID( line.getC_Project_ID());
                    }

                    if( !invoice.save()) {
                        new IllegalStateException( "Cannot save Invoice" );
                    }

                    // Create OrderLine

                    MInvoiceLine il = new MInvoiceLine( invoice );

                    //

                    if( line.getM_Product_ID() != 0 ) {
                        il.setM_Product_ID( line.getM_Product_ID(),true );
                    }

                    il.setQty( line.getQtyReimbursed());    // Entered/Invoiced
                    il.setDescription( line.getDescription());

                    //
                    // il.setPrice();  //      not really a list/limit price for reimbursements

                    il.setPrice( line.getPriceReimbursed());    //
                    il.setTax();

                    if( !il.save()) {
                        new IllegalStateException( "Cannot save Invoice Line" );
                    }

                    // Update TEL

                    line.setC_InvoiceLine_ID( il.getC_InvoiceLine_ID());
                    line.save();
                }    // for all expense lines
            }        // ********* Expense Line Loop

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        completeInvoice( invoice );

        return "@Created@=" + m_noInvoices;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param invoice
     */

    private void completeInvoice( MInvoice invoice ) {
        if( invoice == null ) {
            return;
        }

        invoice.setDocAction( DocAction.ACTION_Prepare );
        invoice.processIt( DocAction.ACTION_Prepare );

        if( !invoice.save()) {
            new IllegalStateException( "Cannot save Invoice" );
        }

        //

        m_noInvoices++;
        addLog( invoice.getID(),invoice.getDateInvoiced(),invoice.getGrandTotal(),invoice.getDocumentNo());
    }    // completeInvoice
}    // ExpenseAPInvoice



/*
 *  @(#)ExpenseAPInvoice.java   02.07.07
 * 
 *  Fin del fichero ExpenseAPInvoice.java
 *  
 *  Versión 2.2
 *
 */
