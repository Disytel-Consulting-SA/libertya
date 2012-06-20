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
import org.openXpertya.model.MClient;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MInvoiceSchedule;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InvoiceGeneratePos extends SvrProcess {

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

    private String p_docAction = DocAction.ACTION_Complete;

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
            } else if( name.equals( "DocAction" )) {
                p_docAction = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        // Login Date

        if( p_DateInvoiced == null ) {
            p_DateInvoiced = Env.getContextAsDate( getCtx(),"#Date" );
        }

        if( p_DateInvoiced == null ) {
            p_DateInvoiced = new Timestamp( System.currentTimeMillis());
        }

        // DocAction check

        if( !DocAction.ACTION_Complete.equals( p_docAction )) {
            p_docAction = DocAction.ACTION_Prepare;
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
        log.info( "Selection=" + p_Selection + ", DateInvoiced=" + p_DateInvoiced + ", AD_Org_ID=" + p_AD_Org_ID + ", C_BPartner_ID=" + p_C_BPartner_ID + ", C_Order_ID=" + p_C_Order_ID + ", DocAction=" + p_docAction + ", Consolidate=" + p_ConsolidateDocument );

        //

        String sql = null;

        if( p_Selection )    // VInvoiceGen
        {
            sql = "SELECT o.* FROM C_Order o " + "INNER JOIN C_DocType d ON (o.C_DocTypeTarget_ID=d.C_DocType_ID) " + "WHERE o.IsSelected='Y' AND o.DocStatus='CO' AND o.IsSOTrx='Y' AND d.DocSubTypeSo='WR' " + "ORDER BY o.M_Warehouse_ID, o.PriorityRule, o.C_BPartner_ID, o.C_Order_ID";
        } else {
            sql = "SELECT o.* FROM C_Order o " + "INNER JOIN C_DocType d ON (o.C_DocTypeTarget_ID=d.C_DocType_ID) " + "WHERE o.DocStatus='CO' AND o.IsSOTrx='Y' AND d.DocSubTypeSo='WR'";

            if( p_AD_Org_ID != 0 ) {
                sql += " AND o.AD_Org_ID=?";
            }

            if( p_C_BPartner_ID != 0 ) {
                sql += " AND o.C_BPartner_ID=?";
            }

            if( p_C_Order_ID != 0 ) {
                sql += " AND o.C_Order_ID=?";
            }

            //

            sql += " AND EXISTS (SELECT * FROM C_OrderLine ol " + "WHERE o.C_Order_ID=ol.C_Order_ID AND ol.QtyOrdered<>ol.QtyInvoiced) " + "ORDER BY o.M_Warehouse_ID, o.PriorityRule, o.C_BPartner_ID, o.C_Order_ID";
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());

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
            log.log( Level.SEVERE,sql,e );
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
                MOrder order = new MOrder( getCtx(),rs,get_TrxName());

                // New Invoice Location

                if( !p_ConsolidateDocument || ( (m_invoice != null) && (m_invoice.getC_BPartner_Location_ID() != order.getBill_Location_ID()))) {
                    completeInvoice();
                }

                boolean completeOrder = MOrder.INVOICERULE_AfterOrderDelivered.equals( order.getInvoiceRule());

                // Schedule After Delivery

                boolean doInvoice = false;

                if( MOrder.INVOICERULE_CustomerScheduleAfterDelivery.equals( order.getInvoiceRule())) {
                    m_bp = new MBPartner( getCtx(),order.getBill_BPartner_ID(),null );

                    if( m_bp.getC_InvoiceSchedule_ID() == 0 ) {
                        log.warning( "BPartner has no Schedule - set to After Delivery" );
                        order.setInvoiceRule( MOrder.INVOICERULE_AfterDelivery );
                        order.save();
                    } else {
                        MInvoiceSchedule is = MInvoiceSchedule.get( getCtx(),m_bp.getC_InvoiceSchedule_ID(),get_TrxName());

                        if( is.canInvoice( order.getDateOrdered(),order.getGrandTotal())) {
                            doInvoice = true;
                        } else {
                            continue;
                        }
                    }
                }    // Schedule

                // After Delivery

                if( doInvoice || MOrder.INVOICERULE_AfterDelivery.equals( order.getInvoiceRule())) {
                    MInOut[] shipments = order.getShipments();

                    for( int i = 0;i < shipments.length;i++ ) {
                        MInOut ship = shipments[ i ];

                        if( !ship.isComplete()) {
                            continue;
                        }

                        MInOutLine[] shipLines = ship.getLines( false );

                        for( int j = 0;j < shipLines.length;j++ ) {
                            MInOutLine shipLine = shipLines[ j ];

                            if( !order.isOrderLine( shipLine.getC_OrderLine_ID())) {
                                continue;
                            }

                            if( !shipLine.isInvoiced()) {
                                createLine( order,ship,shipLine );
                            }
                        }

                        m_line += 1000;
                    }
                }

                // After Order Delivered, Immediate

                else {
                    MOrderLine[] oLines = order.getLines( true,null );

                    for( int i = 0;i < oLines.length;i++ ) {
                        MOrderLine oLine     = oLines[ i ];
                        BigDecimal toInvoice = oLine.getQtyOrdered().subtract( oLine.getQtyInvoiced());

                        if( (toInvoice.compareTo( Env.ZERO ) == 0) && (oLine.getM_Product_ID() != 0) ) {
                            continue;
                        }

                        BigDecimal notInvoicedShipment = oLine.getQtyDelivered().subtract( oLine.getQtyInvoiced());

                        //

                        boolean fullyDelivered = oLine.getQtyOrdered().compareTo( oLine.getQtyDelivered()) == 0;

                        // Complete Order

                        if( completeOrder &&!fullyDelivered ) {
                            log.fine( "Failed CompleteOrder - " + oLine );
                            completeOrder = false;

                            break;
                        }

                        // Immediate

                        else if( MOrder.INVOICERULE_Immediate.equals( order.getInvoiceRule())) {
                            log.fine( "Immediate - ToInvoice=" + toInvoice + " - " + oLine );

                            BigDecimal qtyEntered = toInvoice;

                            // Correct UOM for QtyEntered

                            if( oLine.getQtyEntered().compareTo( oLine.getQtyOrdered()) != 0 ) {
                                qtyEntered = toInvoice.multiply( oLine.getQtyEntered()).divide( oLine.getQtyOrdered(),BigDecimal.ROUND_HALF_UP );
                            }

                            createLine( order,oLine,toInvoice,qtyEntered );
                        } else {
                            log.fine( "Failed: " + order.getInvoiceRule() + " - ToInvoice=" + toInvoice + " - " + oLine );
                        }
                    }    // for all order lines

                    if( MOrder.INVOICERULE_Immediate.equals( order.getInvoiceRule())) {
                        m_line += 1000;
                    }
                }

                // Complete Order successful

                if( completeOrder && MOrder.INVOICERULE_AfterOrderDelivered.equals( order.getInvoiceRule())) {
                    MInOut[] shipments = order.getShipments();

                    for( int i = 0;i < shipments.length;i++ ) {
                        MInOut ship = shipments[ i ];

                        if( !ship.isComplete()) {
                            continue;
                        }

                        MInOutLine[] shipLines = ship.getLines( false );

                        for( int j = 0;j < shipLines.length;j++ ) {
                            MInOutLine shipLine = shipLines[ j ];

                            if( !order.isOrderLine( shipLine.getC_OrderLine_ID())) {
                                continue;
                            }

                            if( !shipLine.isInvoiced()) {
                                createLine( order,ship,shipLine );
                            }
                        }

                        m_line += 1000;
                    }
                }    // complete Order
            }        // for all orders

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
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
     * @param qtyInvoiced
     * @param qtyEntered
     */

    private void createLine( MOrder order,MOrderLine orderLine,BigDecimal qtyInvoiced,BigDecimal qtyEntered ) {
        if( m_invoice == null ) {
            m_invoice = new MInvoice( order,0,p_DateInvoiced );

            if( !m_invoice.save()) {
                throw new IllegalStateException( "Could not create Invoice (o)" );
            }
        }

        //

        MInvoiceLine line = new MInvoiceLine( m_invoice );

        line.setOrderLine( orderLine );
        line.setQtyInvoiced( qtyInvoiced );
        line.setQtyEntered( qtyEntered );
        line.setLine( m_line + orderLine.getLine());

        if( !line.save()) {
            throw new IllegalStateException( "Could not create Invoice Line (o)" );
        }

        log.fine( line.toString());
    }    // createLine

    /**
     * Descripción de Método
     *
     *
     * @param order
     * @param ship
     * @param sLine
     */

    private void createLine( MOrder order,MInOut ship,MInOutLine sLine ) {
        if( m_invoice == null ) {
            m_invoice = new MInvoice( order,0,p_DateInvoiced );

            if( !m_invoice.save()) {
                throw new IllegalStateException( "Could not create Invoice (s)" );
            }
        }

        // Create Comment Line

        if( (m_ship == null) || (m_ship.getM_InOut_ID() != ship.getM_InOut_ID())) {
            MDocType dt = MDocType.get( getCtx(),ship.getC_DocType_ID());

            if( (m_bp == null) || (m_bp.getC_BPartner_ID() != ship.getC_BPartner_ID())) {
                m_bp = new MBPartner( getCtx(),ship.getC_BPartner_ID(),get_TrxName());
            }

            // Reference: Delivery: 12345 - 12.12.12

            MClient client      = MClient.get( getCtx());
            String  AD_Language = client.getAD_Language();

            if( client.isMultiLingualDocument() && (m_bp.getAD_Language() != null) ) {
                AD_Language = m_bp.getAD_Language();
            }

            if( AD_Language == null ) {
                AD_Language = Language.getBaseAD_Language();
            }

            java.text.SimpleDateFormat format = DisplayType.getDateFormat( DisplayType.Date,Language.getLanguage( AD_Language ));
            String reference = dt.getPrintName( m_bp.getAD_Language()) + ": " + ship.getDocumentNo() + " - " + format.format( ship.getMovementDate());

            m_ship = ship;

            //

            MInvoiceLine line = new MInvoiceLine( m_invoice );

            line.setIsDescription( true );
            line.setDescription( reference );
            line.setLine( m_line + sLine.getLine() - 1 );

            if( !line.save()) {
                throw new IllegalStateException( "Could not create Invoice Line (sh)" );
            }
        }

        //

        MInvoiceLine line = new MInvoiceLine( m_invoice );

        line.setShipLine( sLine );
        line.setQtyEntered( sLine.getQtyEntered());
        line.setQtyInvoiced( sLine.getMovementQty());
        line.setLine( m_line + sLine.getLine());

        if( !line.save()) {
            throw new IllegalStateException( "Could not create Invoice Line (s)" );
        }

        // Link

        sLine.setIsInvoiced( true );

        if( !sLine.save()) {
            throw new IllegalStateException( "Could not update Shipment Line" );
        }

        log.fine( line.toString());
    }    // createLine

    /**
     * Descripción de Método
     *
     */

    private void completeInvoice() {
        if( m_invoice != null ) {
            if( !m_invoice.processIt( p_docAction )) {
                log.warning( "completeInvoice - failed: " + m_invoice );
            }

            m_invoice.save();

            //

            addLog( m_invoice.getC_Invoice_ID(),m_invoice.getDateInvoiced(),null,m_invoice.getDocumentNo());
            m_created++;
        }

        m_invoice = null;
        m_ship    = null;
        m_line    = 0;
    }    // completeInvoice
}    // InvoiceGenerate



/*
 *  @(#)InvoiceGeneratePos.java   02.07.07
 * 
 *  Fin del fichero InvoiceGeneratePos.java
 *  
 *  Versión 2.2
 *
 */
