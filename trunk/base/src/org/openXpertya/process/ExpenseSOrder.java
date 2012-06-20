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

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MTimeExpense;
import org.openXpertya.model.MTimeExpenseLine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ExpenseSOrder extends SvrProcess {

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_DateFrom = null;

    /** Descripción de Campos */

    private Timestamp m_DateTo = null;

    /** Descripción de Campos */

    private int m_noOrders = 0;

    /** Descripción de Campos */

    private MOrder m_order = null;

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
            } else if( name.equals( "DateExpense" )) {
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
        StringBuffer sql = new StringBuffer( "SELECT * FROM S_TimeExpenseLine el " + "WHERE el.AD_Client_ID=?"    // #1
            + " AND el.C_BPartner_ID>0 AND el.IsInvoiced='Y'"    // Business Partner && to be invoiced
            + " AND el.C_OrderLine_ID IS NULL"                 // not invoiced yet
            + " AND EXISTS (SELECT * FROM S_TimeExpense e "    // processed only
            + "WHERE el.S_TimeExpense_ID=e.S_TimeExpense_ID AND e.Processed='Y')" );

        if( m_C_BPartner_ID != 0 ) {
            sql.append( " AND el.C_BPartner_ID=?" );    // #2
        }

        if( (m_DateFrom != null) || (m_DateTo != null) ) {
            sql.append( " AND EXISTS (SELECT * FROM S_TimeExpense e " + "WHERE el.S_TimeExpense_ID=e.S_TimeExpense_ID" );

            if( m_DateFrom != null ) {
                sql.append( " AND e.DateReport >= ?" );    // #3
            }

            if( m_DateTo != null ) {
                sql.append( " AND e.DateReport <= ?" );    // #4
            }

            sql.append( ")" );
        }

        sql.append( " ORDER BY el.C_BPartner_ID, el.C_Project_ID, el.S_TimeExpense_ID, el.Line" );

        //

        MBPartner    oldBPartner    = null;
        int          old_Project_ID = -1;
        MTimeExpense te             = null;

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
                MTimeExpenseLine tel = new MTimeExpenseLine( getCtx(),rs,get_TrxName());

                if( !tel.isInvoiced()) {
                    continue;
                }

                // New BPartner - New Order

                if( (oldBPartner == null) || (oldBPartner.getC_BPartner_ID() != tel.getC_BPartner_ID())) {
                    completeOrder();
                    oldBPartner = new MBPartner( getCtx(),tel.getC_BPartner_ID(),get_TrxName());
                }

                // New Project - New Order

                if( old_Project_ID != tel.getC_Project_ID()) {
                    completeOrder();
                    old_Project_ID = tel.getC_Project_ID();
                }

                if( (te == null) || (te.getS_TimeExpense_ID() != tel.getS_TimeExpense_ID())) {
                    te = new MTimeExpense( getCtx(),tel.getS_TimeExpense_ID(),get_TrxName());
                }

                //

                processLine( te,tel,oldBPartner );
            }    // ********* Expense Line Loop

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

        completeOrder();

        return "@Created@=" + m_noOrders;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param te
     * @param tel
     * @param bp
     */

    private void processLine( MTimeExpense te,MTimeExpenseLine tel,MBPartner bp ) {
        if( m_order == null ) {
            log.info( "processLine - New Order for " + bp + ", Project=" + tel.getC_Project_ID());
            m_order = new MOrder( getCtx(),0,get_TrxName());
            m_order.setAD_Org_ID( tel.getAD_Org_ID());
            m_order.setC_DocTypeTarget_ID( MOrder.DocSubTypeSO_OnCredit );

            //

            m_order.setBPartner( bp );

            if( m_order.getC_BPartner_Location_ID() == 0 ) {
                log.log( Level.SEVERE,"processLine - No BP Location: " + bp );
                addLog( 0,te.getDateReport(),null,"No Location: " + te.getDocumentNo() + " " + bp.getName());
                m_order = null;

                return;
            }

            m_order.setM_Warehouse_ID( te.getM_Warehouse_ID());

            if( tel.getC_Activity_ID() != 0 ) {
                m_order.setC_Activity_ID( tel.getC_Activity_ID());
            }

            if( tel.getC_Campaign_ID() != 0 ) {
                m_order.setC_Campaign_ID( tel.getC_Campaign_ID());
            }

            if( tel.getC_Project_ID() != 0 ) {
                m_order.setC_Project_ID( tel.getC_Project_ID());

                // Overwrite Price list

                MProject project = new MProject( getCtx(),tel.getC_Project_ID(),get_TrxName());

                if( project.getM_PriceList_ID() != 0 ) {
                    m_order.setM_PriceList_ID( project.getM_PriceList_ID());
                }
            }

            m_order.setSalesRep_ID( te.getDoc_User_ID());

            //

            if( !m_order.save()) {
                throw new IllegalStateException( "Cannot save Order" );
            }
        } else {

            // Update Header info

            if( (tel.getC_Activity_ID() != 0) && (tel.getC_Activity_ID() != m_order.getC_Activity_ID())) {
                m_order.setC_Activity_ID( tel.getC_Activity_ID());
            }

            if( (tel.getC_Campaign_ID() != 0) && (tel.getC_Campaign_ID() != m_order.getC_Campaign_ID())) {
                m_order.setC_Campaign_ID( tel.getC_Campaign_ID());
            }

            if( !m_order.save()) {
                new IllegalStateException( "Cannot save Order" );
            }
        }

        // OrderLine

        MOrderLine ol = new MOrderLine( m_order );

        //

        if( tel.getM_Product_ID() != 0 ) {
            ol.setM_Product_ID( tel.getM_Product_ID(),tel.getC_UOM_ID());
        }

        if( tel.getS_ResourceAssignment_ID() != 0 ) {
            ol.setS_ResourceAssignment_ID( tel.getS_ResourceAssignment_ID());
        }

        ol.setQty( tel.getQtyInvoiced());    //
        ol.setDescription( tel.getDescription());

        //

        BigDecimal price = tel.getPriceInvoiced();    //

        if( (price != null) && (price.compareTo( Env.ZERO ) != 0) ) {
            if( tel.getC_Currency_ID() != m_order.getC_Currency_ID()) {
                price = MConversionRate.convert( getCtx(),price,tel.getC_Currency_ID(),m_order.getC_Currency_ID(),m_order.getAD_Client_ID(),m_order.getAD_Org_ID());
            }

            ol.setPrice( price );
        } else {
            ol.setPrice();
        }

        if( (tel.getC_UOM_ID() != 0) && (ol.getC_UOM_ID() == 0) ) {
            ol.setC_UOM_ID( tel.getC_UOM_ID());
        }

        ol.setTax();

        if( !ol.save()) {
            throw new IllegalStateException( "Cannot save Order Line" );
        }

        // Update TimeExpense Line

        tel.setC_OrderLine_ID( ol.getC_OrderLine_ID());

        if( tel.save()) {
            log.fine( "processLine Updated " + tel + " with C_OrderLine_ID" );
        } else {
            log.log( Level.SEVERE,"processLine Not Updated " + tel + " with C_OrderLine_ID" );
        }
    }    // processLine

    /**
     * Descripción de Método
     *
     */

    private void completeOrder() {
        if( m_order == null ) {
            return;
        }

        m_order.setDocAction( DocAction.ACTION_Prepare );
        m_order.processIt( DocAction.ACTION_Prepare );

        if( !m_order.save()) {
            throw new IllegalStateException( "Cannot save Order" );
        }

        m_noOrders++;
        addLog( m_order.getID(),m_order.getDateOrdered(),m_order.getGrandTotal(),m_order.getDocumentNo());
        m_order = null;
    }    // completeOrder
}    // ExpenseSOrder



/*
 *  @(#)ExpenseSOrder.java   02.07.07
 * 
 *  Fin del fichero ExpenseSOrder.java
 *  
 *  Versión 2.2
 *
 */
