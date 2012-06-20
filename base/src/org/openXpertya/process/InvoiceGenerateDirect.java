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
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InvoiceGenerateDirect extends SvrProcess {

    /** Descripción de Campos */

    private boolean p_Selection = false;

    /** Descripción de Campos */

    private Timestamp p_DateInvoiced = null;

    /** Descripción de Campos */

    private int p_AD_Org_ID = 0;

    /** Descripción de Campos */

    private int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int p_C_Order_ID = 0;

    /** Descripción de Campos */

    private boolean p_ConsolidateDocument = true;

    /** Descripción de Campos */

    private MInvoice m_invoice = null;

    /** Descripción de Campos */

    private MInOut m_ship = null;

    /** Descripción de Campos */

    private int m_created = 0;

    /** Descripción de Campos */

    private int m_line = 0;

    /** Descripción de Campos */

    private MBPartner m_bp = null;

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
            } else if( name.equals( "Selection" )) {
                p_Selection = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "DateInvoiced" )) {
                p_DateInvoiced = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "AD_Org_ID" )) {
                p_AD_Org_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_Order_ID" )) {
                p_C_Order_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "ConsolidateDocument" )) {
                p_ConsolidateDocument = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        // Login Date

        if( p_DateInvoiced == null ) {
            p_DateInvoiced = Env.getContextAsDate( getCtx(),"#Date" );
        }

        if( p_DateInvoiced == null ) {
            p_DateInvoiced = new Timestamp( System.currentTimeMillis());
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
        log.info( "doIt - Selection=" + p_Selection + ", DateInvoiced=" + p_DateInvoiced + ", AD_Org_ID=" + p_AD_Org_ID + ", C_BPartner_ID=" + p_C_BPartner_ID + ", C_Order_ID=" + p_C_Order_ID + ", Consolidate=" + p_ConsolidateDocument );

        //

        String sql = null;

        if( p_Selection )    // VInvoiceGen
        {
            sql = "SELECT * FROM C_Order " + "WHERE IsSelected='Y' AND DocStatus='CO' AND IsSOTrx='Y'AND IsDropShip='Y' AND IsPedidoGenerado='Y'" + "ORDER BY M_Warehouse_ID, PriorityRule, C_BPartner_ID, C_Order_ID";
        } else {
            sql = "SELECT * FROM C_Order o " + "WHERE DocStatus='CO' AND IsSOTrx='Y'AND IsDropShip='Y' AND IsPedidoGenerado='Y'";

            if( p_AD_Org_ID != 0 ) {
                sql += " AND AD_Org_ID=?";
            }

            if( p_C_BPartner_ID != 0 ) {
                sql += " AND C_BPartner_ID=?";
            }

            if( p_C_Order_ID != 0 ) {
                sql += " AND C_Order_ID=?";
            }

            //

            sql += " AND EXISTS (SELECT * FROM C_OrderLine ol " + "WHERE o.C_Order_ID=ol.C_Order_ID AND ol.QtyOrdered<>ol.QtyInvoiced) " + "ORDER BY M_Warehouse_ID, PriorityRule, C_BPartner_ID, C_Order_ID";
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            int index = 1;

            if( !p_Selection && (p_AD_Org_ID != 0) ) {
                pstmt.setInt( index++,p_AD_Org_ID );
            }

            if( !p_Selection && (p_C_BPartner_ID != 0) ) {
                pstmt.setInt( index++,p_C_BPartner_ID );
            }

            if( !p_Selection && (p_C_Order_ID != 0) ) {
                pstmt.setInt( index++,p_C_Order_ID );
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt - " + sql,e );
        }

        return generate( pstmt );
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param pstmt
     *
     * @return
     */

    private String generate( PreparedStatement pstmt ) {
        try {
            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MOrder order = new MOrder( getCtx(),rs,null );

                // New Invoice Location

                if( !p_ConsolidateDocument || ( (m_invoice != null) && (m_invoice.getC_BPartner_Location_ID() != order.getBill_Location_ID()))) {
                    completeInvoice();
                }

                boolean completeOrder = MOrder.INVOICERULE_AfterOrderDelivered.equals( order.getInvoiceRule());

                // Schedule After Delivery

                boolean      doInvoice = false;
                MOrderLine[] oLines    = order.getLines( false,null );

                for( int i = 0;i < oLines.length;i++ ) {
                    MOrderLine oLine     = oLines[ i ];
                    BigDecimal toInvoice = oLine.getQtyOrdered().subtract( oLine.getQtyInvoiced());
                    BigDecimal notInvoicedShipment = oLine.getQtyDelivered().subtract( oLine.getQtyInvoiced());

                    log.finer( "generate - Immediate - ToInvoice=" + toInvoice + " - " + oLine );
                    createLine( order,oLine,toInvoice );
                }    // for all order lines
            }        // for all orders

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"generate",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        completeInvoice();

        return "@Created@ = " + m_created;
    }    // generate

    /**
     * Descripción de Método
     *
     *
     * @param order
     * @param orderLine
     * @param qty
     */

    private void createLine( MOrder order,MOrderLine orderLine,BigDecimal qty ) {
        if( m_invoice == null ) {
            m_invoice = new MInvoice( order,0,p_DateInvoiced );

            if( !m_invoice.save()) {
                throw new IllegalStateException( "Could not create Invoice (o)" );
            }

            m_line = 10;
        }

        //

        MInvoiceLine line = new MInvoiceLine( m_invoice );

        line.setOrderLine( orderLine );
        line.setQty( qty );
        line.setLine( m_line );

        if( !line.save()) {
            throw new IllegalStateException( "Could not create Invoice Line (o)" );
        }

        log.finer( "createLine - " + line );
        m_line += 10;
    }    // createLine

    /**
     * Descripción de Método
     *
     */

    private void completeInvoice() {
        if( m_invoice != null ) {
            if( !m_invoice.processIt( DocAction.ACTION_Complete )) {
                log.warning( "completeInvoice - failed: " + m_invoice );
            }

            m_invoice.save();

            //

            addLog( m_invoice.getC_Invoice_ID(),m_invoice.getDateInvoiced(),null,m_invoice.getDocumentNo());
            m_created++;
        }

        m_invoice = null;
        m_ship    = null;
    }    // completeInvoice
}    // InvoiceGenerate



/*
 *  @(#)InvoiceGenerateDirect.java   02.07.07
 * 
 *  Fin del fichero InvoiceGenerateDirect.java
 *  
 *  Versión 2.2
 *
 */
