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



package org.openXpertya.wstore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WebOrder {

    /**
     * Constructor de la clase ...
     *
     *
     * @param wu
     * @param wb
     * @param ctx
     */

    public WebOrder( WebUser wu,WebBasket wb,Properties ctx ) {
        m_ctx = ctx;
        createOrder( wu,wb );
    }    // WebOrder

    /**
     * Constructor de la clase ...
     *
     *
     * @param order
     */

    public WebOrder( MOrder order ) {
        m_ctx   = order.getCtx();
        m_order = order;
    }    // WebOrder

    /** Descripción de Campos */

    public static final String NAME = "webOrder";

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private MOrder m_order;

    /** Descripción de Campos */

    private Properties m_ctx;

    /**
     * Descripción de Método
     *
     *
     * @param wu
     * @param wb
     *
     * @return
     */

    private boolean createOrder( WebUser wu,WebBasket wb ) {
        log.fine( "createOrder" );

        //

        m_order = new MOrder( m_ctx,0,null );
        log.fine( "createOrder - AD_Client_ID=" + m_order.getAD_Client_ID() + ",AD_Org_ID=" + m_order.getAD_Org_ID() + " - " + m_ctx + " - " + m_order );

        //
        
        //m_order.setC_DocType_ID( 1000335 );
        //m_order.setC_DocTypeTarget_ID( 1000335 );     
        m_order.setC_DocTypeTarget_ID( MOrder.DocSubTypeSO_Proposal );
        m_order.setPaymentRule( MOrder.PAYMENTRULE_CreditCard );
        m_order.setDeliveryRule( MOrder.DELIVERYRULE_AfterReceipt );
        m_order.setInvoiceRule( MOrder.INVOICERULE_Immediate );
        m_order.setIsSelfService( true );

        if( wb.getM_PriceList_ID() > 0 ) {
            m_order.setM_PriceList_ID( wb.getM_PriceList_ID());
        }

        if( wb.getSalesRep_ID() != 0 ) {
            m_order.setSalesRep_ID( wb.getSalesRep_ID());
        }

        // BPartner

        m_order.setC_BPartner_ID( wu.getC_BPartner_ID());
        m_order.setC_BPartner_Location_ID( wu.getC_BPartner_Location_ID());
        m_order.setAD_User_ID( wu.getAD_User_ID());

        //

        m_order.setSendEMail( true );
        m_order.setDocAction( MOrder.DOCACTION_Prepare );
        m_order.save();
        log.fine( "createOrder - ID=" + m_order.getC_Order_ID() + ", DocNo=" + m_order.getDocumentNo());

        ArrayList lines = wb.getLines();

        for( int i = 0;i < lines.size();i++ ) {
            WebBasketLine wbl = ( WebBasketLine )lines.get( i );
            MOrderLine    ol  = new MOrderLine( m_order );

            ol.setM_Product_ID( wbl.getM_Product_ID(),true );
            ol.setQty( wbl.getQuantity());
            ol.setPrice();
            ol.setPrice( wbl.getPrice());
            ol.setTax();
            ol.save();
        }    // for all lines

        boolean ok = m_order.processIt( MOrder.DOCACTION_Prepare );

        m_order.save();

        // Web User = Customer

        if( !wu.isCustomer()) {

            // log.info("-------------------------------------- " + wu.isCustomer());

            wu.setIsCustomer( true );
            wu.save();

            // log.info("-------------------------------------- " + wu.isCustomer());

        }

        BigDecimal amt = m_order.getGrandTotal();

        log.info( "createOrder " + amt );

        return ok;
    }    // createOrder

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "WebOrder[" );

        sb.append( m_order ).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param payment
     *
     * @return
     */

    public boolean process( MPayment payment ) {
        if( m_order == null ) {
            return false;
        }

        if( payment.getID() == 0 ) {
            payment.save();
        }

        m_order.setC_Payment_ID( payment.getC_Payment_ID());
        m_order.setDocAction( MOrder.DOCACTION_WaitComplete );

        boolean ok = m_order.processIt( MOrder.DOCACTION_WaitComplete );

        m_order.save();

        //

        payment.setC_Order_ID( m_order.getC_Order_ID());
        payment.setC_Invoice_ID( getInvoice_ID());

        //

        return ok;
    }    // process

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Order_ID() {
        if( m_order != null ) {
            return m_order.getC_Order_ID();
        }

        return 0;
    }    // getC_Order_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCompleted() {
        if( m_order == null ) {
            return false;
        }

        return MOrder.STATUS_Completed.equals( m_order.getDocStatus()) || MOrder.STATUS_Closed.equals( m_order.getDocStatus());
    }    // isCompleted

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInProgress() {
        if( m_order == null ) {
            return false;
        }

        return MOrder.DOCSTATUS_InProgress.equals( m_order.getDocStatus());
    }    // isInProgress

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDocumentNo() {
        return m_order.getDocumentNo();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getTotalLines() {
        return m_order.getTotalLines();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getFreightAmt() {
        return m_order.getFreightAmt();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getTaxAmt() {
        return m_order.getGrandTotal().subtract( m_order.getTotalLines()).subtract( m_order.getFreightAmt());
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getGrandTotal() {
        return m_order.getGrandTotal();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSalesRep_ID() {
        return m_order.getSalesRep_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected MOrder getOrder() {
        return m_order;
    }

    /** Descripción de Campos */

    private String m_invoiceInfo = null;

    /** Descripción de Campos */

    private int m_C_Invoice_ID = -1;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInvoiceInfo() {
        if( m_invoiceInfo == null ) {
            MInvoice[] invoices = m_order.getInvoices();
            int        length   = invoices.length;

            if( length > 0 )    // get last invoice
            {
                m_C_Invoice_ID = invoices[ length - 1 ].getC_Invoice_ID();
                m_invoiceInfo  = invoices[ length - 1 ].getDocumentNo();
            }
        }

        return m_invoiceInfo;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getInvoice_ID() {
        if( m_C_Invoice_ID == -1 ) {
            getInvoiceInfo();
        }

        return m_C_Invoice_ID;
    }    // getInvoice_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        if( m_order == null ) {
            return 0;
        }

        return m_order.getC_Currency_ID();
    }    // getC_Currency_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCurrencyISO() {
        if( m_order == null ) {
            return "";
        }

        return MCurrency.getISO_Code( m_ctx,m_order.getC_Currency_ID());
    }    // getCurrencyISO

    public int getCuentaLineas(){
	return m_order.getCuentaLineas();
    }	//	getCuentaLineas

    public String getDeliveryViaRule() 	{
	return m_order.getDeliveryViaRule();
    }
	
    public String getDeliveryRule() {
	return m_order.getDeliveryRule();
    }
	
    public String getPaymentRule() {
	return m_order.getPaymentRule();
    }

    public String getDocStatus() {
	return m_order.getDocStatus();
    }
	
    public String getDescription() {
	return m_order.getDescription();
    }
	
}    // WebOrder



/*
 *  @(#)WebOrder.java   12.10.07
 * 
 *  Fin del fichero WebOrder.java
 *  
 *  Versión 2.2
 *
 */
