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



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.DiscountCalculator.IDocumentLine;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.HTMLMsg.HTMLList;
import org.openXpertya.util.MProductCache;
import org.openXpertya.util.Msg;
import org.openXpertya.util.StringUtil;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MOrder extends X_C_Order implements DocAction, Authorization  {

    /**
     * Descripción de Método
     *
     *
     * @param from
     * @param dateDoc
     * @param C_DocTypeTarget_ID
     * @param isSOTrx
     * @param counter
     * @param copyASI
     * @param trxName
     *
     * @return
     */

    public static MOrder copyFrom( MOrder from,Timestamp dateDoc,int C_DocTypeTarget_ID,boolean isSOTrx,boolean counter,boolean copyASI,String trxName ) {
    	//JOptionPane.showMessageDialog( null,"Recien llegado al copyfrom ",null, JOptionPane.INFORMATION_MESSAGE );

    	MOrder to = new MOrder( from.getCtx(),0,trxName );
        to.set_TrxName( trxName );
        PO.copyValues( from,to,from.getAD_Client_ID(),from.getAD_Org_ID());
        to.setC_Order_ID( 0 );
        to.set_ValueNoCheck( "DocumentNo",null );
        
       if(counter){
    	   MBPartner a = new MBPartner(Env.getCtx(),from.getC_BPartner_ID(),null);
    	   to.setAD_Org_ID(Integer.valueOf(a.getAD_OrgBP_ID()).intValue());
    	   MOrg b = new MOrg(Env.getCtx(),Integer.valueOf(a.getAD_OrgBP_ID()).intValue(),null);
    	   //MOrgInfo c = new MOrgInfo(getCtx(),b);
    	   MOrgInfo c = MOrgInfo.get( Env.getCtx(),Integer.valueOf(a.getAD_OrgBP_ID()).intValue());
    	   to.setM_Warehouse_ID(c.getM_Warehouse_ID());
    	   //JOptionPane.showMessageDialog( null,"El ad_org_id en el if es= "+a.getAD_OrgBP_ID(),null, JOptionPane.INFORMATION_MESSAGE );
    	   //JOptionPane.showMessageDialog( null,"El warehouse en el if es= "+c.getM_Warehouse_ID(),null, JOptionPane.INFORMATION_MESSAGE );

       }
        
        to.setDocStatus( DOCSTATUS_Drafted );    // Draft
        to.setDocAction( DOCACTION_Complete );
        
        //

        to.setC_DocType_ID( 0 );
        to.setC_DocTypeTarget_ID( C_DocTypeTarget_ID );
        to.setIsSOTrx( isSOTrx );

        //

        to.setIsSelected( false );
        to.setDateOrdered( dateDoc );
        to.setDateAcct( dateDoc );
        to.setDatePromised( dateDoc );    // assumption
        to.setDatePrinted( null );
        to.setIsPrinted( false );

        //

        to.setIsApproved( false );
        to.setIsCreditApproved( false );
        to.setC_Payment_ID( 0 );
        to.setC_CashLine_ID( 0 );

        // Amounts are updated  when adding lines

        to.setGrandTotal( Env.ZERO );
        to.setTotalLines( Env.ZERO );

        //

        to.setIsDelivered( false );
        to.setIsInvoiced( false );
        to.setIsSelfService( false );
        to.setIsTransferred( false );
        to.setPosted( false );
        to.setProcessed( false );

        if( counter ) {
            to.setRef_Order_ID( from.getC_Order_ID());
        } else {
            to.setRef_Order_ID( 0 );
        }

        //
       
        if( !to.save( trxName )) {
            throw new IllegalStateException( "Could not create Order" );
        }
        
        if( counter ) {
            from.setRef_Order_ID( to.getC_Order_ID());
           // to.save();
        }
        
        //JOptionPane.showMessageDialog( null,"Antes de crear laslineas la id = "+from.getC_Order_ID(),null, JOptionPane.INFORMATION_MESSAGE );
        if( to.copyLinesFrom( from,counter,copyASI ) == 0 ) {
            throw new IllegalStateException( "Could not create Order Lines" );
        }

        return to;
    }    // copyFrom
    
    /**
     * @param order
     * @return true si se aplicaron descuentos en este pedido parámetro
     */
    public static boolean isDiscountsApplied(MOrder order){
    	BigDecimal totalDiscountsAmt = order.getTotalDiscountAmtFromLines(null);
		List<MDocumentDiscount> discounts = MDocumentDiscount.getOfOrder(
				order.getID(), order.getCtx(), order.get_TrxName());
		return totalDiscountsAmt.compareTo(BigDecimal.ZERO) != 0
				|| (discounts != null && discounts.size() > 0); 
    }
    
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Order_ID
     * @param trxName
     */

    public MOrder( Properties ctx,int C_Order_ID,String trxName ) {
        super( ctx,C_Order_ID,trxName );

        // New

        if( C_Order_ID == 0 ) {
            setDocStatus( DOCSTATUS_Drafted );
            setDocAction( DOCACTION_Prepare );

            //

            setDeliveryRule( DELIVERYRULE_Availability );
            setFreightCostRule( FREIGHTCOSTRULE_FreightIncluded );
            setInvoiceRule( INVOICERULE_Immediate );
            setPaymentRule( PAYMENTRULE_OnCredit );
            setPriorityRule( PRIORITYRULE_Medium );
            setDeliveryViaRule( DELIVERYVIARULE_Pickup );

            //

            setIsDiscountPrinted( false );
            setIsSelected( false );
            setIsTaxIncluded( false );
            setIsSOTrx( true );
            setIsDropShip( false );
            setSendEMail( false );

            //

            setIsApproved( false );
            setIsPrinted( false );
            setIsCreditApproved( false );
            setIsDelivered( false );
            setIsInvoiced( false );
            setIsTransferred( false );
            setIsSelfService( false );

            //

            super.setProcessed( false );
            setProcessing( false );
            setPosted( false );
            setDateOrdered(Env.getTimestamp());
            setDateAcct(getDateOrdered());
            setDatePromised(getDateOrdered());
            setFreightAmt( Env.ZERO );
            setChargeAmt( Env.ZERO );
            setTotalLines( Env.ZERO );
            setGrandTotal( Env.ZERO );
        }
    }    // MOrder

    /**
     * Constructor de la clase ...
     *
     *
     * @param project
     * @param IsSOTrx
     * @param DocSubTypeSO
     */

    public MOrder( MProject project,boolean IsSOTrx,String DocSubTypeSO ) {
        this( project.getCtx(),0,project.get_TrxName());
        setAD_Client_ID( project.getAD_Client_ID());
        setAD_Org_ID( project.getAD_Org_ID());
        setC_Campaign_ID( project.getC_Campaign_ID());
        setSalesRep_ID( project.getSalesRep_ID());

        //

        setC_Project_ID( project.getC_Project_ID());
        setDescription( project.getName());

        Timestamp ts = project.getDateContract();

        if( ts != null ) {
            setDateOrdered( ts );
        }

        ts = project.getDateFinish();

        if( ts != null ) {
            setDatePromised( ts );
        }

        //

        setC_BPartner_ID( project.getC_BPartner_ID());
        setC_BPartner_Location_ID( project.getC_BPartner_Location_ID());
        setAD_User_ID( project.getAD_User_ID());

        //

        setM_Warehouse_ID( project.getM_Warehouse_ID());
        setM_PriceList_ID( project.getM_PriceList_ID());
        setC_PaymentTerm_ID( project.getC_PaymentTerm_ID());

        //

        setIsSOTrx( IsSOTrx );

        if( IsSOTrx ) {
            if( (DocSubTypeSO == null) || (DocSubTypeSO.length() == 0) ) {
                setC_DocTypeTarget_ID( DocSubTypeSO_OnCredit );
            } else {
                setC_DocTypeTarget_ID( DocSubTypeSO );
            }
        } else {
            setC_DocTypeTarget_ID();
        }
    }    // MOrder

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MOrder( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MOrder

    /** Descripción de Campos */

    private MOrderLine[] m_lines = null;

    /** Descripción de Campos */

    private MOrderTax[] m_taxes = null;

    /** Descripción de Campos */

    private boolean m_forceCreation = false;

	/**
	 * Booleanos que determinan si al anular el pedido se deben anular los
	 * remitos y facturas que referencian a este
	 */
    private boolean voidInOuts = true;
    private boolean voidInvoices = true;
    
    private Integer tpvGeneratedInvoiceID = 0;
    
    /**
	 * Flag especial para evitar validaciones costosas computacionalmente, las
	 * cuales no son necesario realizar en caso de ser parte de una venta TPV
	 */
 	protected boolean isTPVInstance = false;
 	
 	/** Flag que forza la reserva de stock */
 	private boolean forceReserveStock = false; 
    
 	/** Actualización del monto de descuento a nivel del documento */
 	private boolean updateChargeAmt = false;
 	
    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     * @param AD_Org_ID
     */

    public void setClientOrg( int AD_Client_ID,int AD_Org_ID ) {
        super.setClientOrg( AD_Client_ID,AD_Org_ID );
    }    // setClientOrg

    /**
     * Descripción de Método
     *
     *
     * @param AD_Org_ID
     */

    public void setAD_Org_ID( int AD_Org_ID ) {
        super.setAD_Org_ID( AD_Org_ID );
    }    // setAD_Org_ID

    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void addDescription( String description ) {
        String desc = getDescription();

        if( desc == null ) {
            setDescription( description );
        } else {
            setDescription( desc + " | " + description );
        }
    }    // addDescription

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    public void setC_BPartner_ID( int C_BPartner_ID ) {
        super.setC_BPartner_ID( C_BPartner_ID );
        super.setBill_BPartner_ID( C_BPartner_ID );
    }    // setC_BPartner_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_Location_ID
     */

    public void setC_BPartner_Location_ID( int C_BPartner_Location_ID ) {
        super.setC_BPartner_Location_ID( C_BPartner_Location_ID );
        super.setBill_Location_ID( C_BPartner_Location_ID );
    }    // setC_BPartner_Location_ID

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     */

    public void setAD_User_ID( int AD_User_ID ) {
        super.setAD_User_ID( AD_User_ID );
        super.setBill_User_ID( AD_User_ID );
    }    // setAD_User_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    public void setShip_BPartner_ID( int C_BPartner_ID ) {
        super.setC_BPartner_ID( C_BPartner_ID );
    }    // setShip_BPartner_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_Location_ID
     */

    public void setShip_Location_ID( int C_BPartner_Location_ID ) {
        super.setC_BPartner_Location_ID( C_BPartner_Location_ID );
    }    // setShip_Location_ID

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     */

    public void setShip_User_ID( int AD_User_ID ) {
        super.setAD_User_ID( AD_User_ID );
    }    // setShip_User_ID

    /**
     * Descripción de Método
     *
     *
     * @param M_Warehouse_ID
     */

    public void setM_Warehouse_ID( int M_Warehouse_ID ) {
        super.setM_Warehouse_ID( M_Warehouse_ID );
    }    // setM_Warehouse_ID

    /**
     * Descripción de Método
     *
     *
     * @param SalesRep_ID
     */
    public void setSalesRep_ID( int SalesRep_ID ) {
        super.setSalesRep_ID( SalesRep_ID );
    }    // setSalesRep_ID

    /**
     * Descripción de Método
     *
     *
     * @param IsDropShip
     */
    public void setIsDropShip( boolean IsDropShip ) {
        super.setIsDropShip( IsDropShip );
    }    // setIsDropShip

    /** Descripción de Campos */

    public static final String DocSubTypeSO_Standard = "SO";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_Quotation = "OB";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_Proposal = "ON";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_Prepay = "PR";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_POS = "WR";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_Warehouse = "WP";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_OnCredit = "WI";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_RMA = "RM";

    /**
     * Descripción de Método
     *
     *
     * @param DocSubTypeSO_x
     */

    public void setC_DocTypeTarget_ID( String DocSubTypeSO_x ) {
        String sql = "SELECT C_DocType_ID FROM C_DocType " + "WHERE AD_Client_ID=? AND AD_Org_ID IN (0," + getAD_Org_ID() + ") AND DocSubTypeSO=? " + "ORDER BY AD_Org_ID DESC, IsDefault DESC";
        int C_DocType_ID = DB.getSQLValue( null,sql,getAD_Client_ID(),DocSubTypeSO_x );

        if( C_DocType_ID <= 0 ) {
            log.severe( "Not found for AD_Client_ID=" + getAD_Client_ID() + ", SubType=" + DocSubTypeSO_x );
        } else {
            log.fine( "(SO) - " + DocSubTypeSO_x );
            setC_DocTypeTarget_ID( C_DocType_ID );
            setIsSOTrx( true );
        }
    }    // setC_DocTypeTarget_ID

    /**
     * Descripción de Método
     *
     */

    public void setC_DocTypeTarget_ID() {
        if( isSOTrx())    // SO = Std Order
        {
            setC_DocTypeTarget_ID( DocSubTypeSO_Standard );

            return;
        }

        // PO

        String sql = "SELECT C_DocType_ID FROM C_DocType " + "WHERE AD_Client_ID=? AND AD_Org_ID IN (0," + getAD_Org_ID() + ") AND DocBaseType='POO' " + "ORDER BY AD_Org_ID DESC, IsDefault DESC";
        int C_DocType_ID = DB.getSQLValue( null,sql,getAD_Client_ID());

        if( C_DocType_ID <= 0 ) {
            log.severe( "No POO found for AD_Client_ID=" + getAD_Client_ID());
        } else {
            log.fine( "(PO) - " + C_DocType_ID );
            setC_DocTypeTarget_ID( C_DocType_ID );
        }
    }    // setC_DocTypeTarget_ID

    /**
     * Descripción de Método
     *
     *
     * @param bp
     */

    public void setBPartner( MBPartner bp ) {
        if( bp == null ) {
            return;
        }

        setC_BPartner_ID( bp.getC_BPartner_ID());

        // Defaults Payment Term

        int ii = 0;

        if( isSOTrx()) {
            ii = bp.getC_PaymentTerm_ID();
        } else {
            ii = bp.getPO_PaymentTerm_ID();
        }

        if( ii != 0 ) {
            setC_PaymentTerm_ID( ii );
        }

        // Default Price List

        if( isSOTrx()) {
            ii = bp.getM_PriceList_ID();
        } else {
            ii = bp.getPO_PriceList_ID();
        }

        if( ii != 0 ) {
            setM_PriceList_ID( ii );
        }

        // Default Delivery/Via Rule

        String ss = bp.getDeliveryRule();

        if( ss != null && ss.trim().length() > 0) {
            setDeliveryRule( ss );
        }

        ss = bp.getDeliveryViaRule();

        if( ss != null && ss.trim().length() > 0) {
            setDeliveryViaRule( ss );
        }

        // Default Invoice/Payment Rule

        ss = bp.getInvoiceRule();

        if( ss != null && ss.trim().length() > 0) {
            setInvoiceRule( ss );
        }

        ss = bp.getPaymentRule();

        if( ss != null && ss.trim().length() > 0) {
            setPaymentRule( ss );
        }

        // Sales Rep

        ii = bp.getSalesRep_ID();

        if( ii != 0 ) {
            setSalesRep_ID( ii );
        }

        // Set Locations

        MBPartnerLocation[] locs = bp.getLocations( false );

        if( locs != null ) {
            for( int i = 0;i < locs.length;i++ ) {
                if( locs[ i ].isShipTo()) {
                    super.setC_BPartner_Location_ID( locs[ i ].getC_BPartner_Location_ID());
                }

                if( locs[ i ].isBillTo()) {
                    setBill_Location_ID( locs[ i ].getC_BPartner_Location_ID());
                }
            }

            // set to first

            if( (getC_BPartner_Location_ID() == 0) && (locs.length > 0) ) {
                super.setC_BPartner_Location_ID( locs[ 0 ].getC_BPartner_Location_ID());
            }

            if( (getBill_Location_ID() == 0) && (locs.length > 0) ) {
                setBill_Location_ID( locs[ 0 ].getC_BPartner_Location_ID());
            }
        }

        if( getC_BPartner_Location_ID() == 0 ) {
            log.log( Level.SEVERE,"MOrder.setBPartner - Has no Ship To Address: " + bp );
        }

        if( getBill_Location_ID() == 0 ) {
            log.log( Level.SEVERE,"MOrder.setBPartner - Has no Bill To Address: " + bp );
        }

        // Set Contact

        MUser[] contacts = bp.getContacts( false );

        if( (contacts != null) && (contacts.length == 1) ) {
            setAD_User_ID( contacts[ 0 ].getAD_User_ID());
        }
    }    // setBPartner

    /**
     * Descripción de Método
     *
     *
     * @param otherOrder
     * @param counter
     * @param copyASI
     *
     * @return
     */

    public int copyLinesFrom( MOrder otherOrder,boolean counter,boolean copyASI ) {
        if( isProcessed() || isPosted() || (otherOrder == null) ) {
            return 0;
        }

        MOrderLine[] fromLines = otherOrder.getLines( false,null );
        int          count     = 0;

        // Si el otro pedido es un presupuesto y está vencido, entonces se actualizan
        // los precios de las líneas con los precios actuales.
        Timestamp today = Env.getDate();
        boolean updatePrices = otherOrder.isExpiredProposal(today);
        
        MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID(), get_TrxName());
		boolean isOrderTransferred = docType.getDocTypeKey().equalsIgnoreCase(
				MDocType.DOCTYPE_Pedido_Transferible);
        
        for( int i = 0;i < fromLines.length;i++ ) {
            MOrderLine line = new MOrderLine( this );

            PO.copyValues( fromLines[ i ],line,getAD_Client_ID(),getAD_Org_ID());
            //JOptionPane.showMessageDialog( null,"En copylinesfrom,el c_order_id="+getC_Order_ID(),null, JOptionPane.INFORMATION_MESSAGE );
            line.setC_Order_ID( getC_Order_ID());
            line.setOrder( this );
            
            line.setC_OrderLine_ID( 0 );    // new

            // References

            if( !copyASI ) {
                line.setM_AttributeSetInstance_ID( 0 );
                line.setS_ResourceAssignment_ID( 0 );
            }

            if( counter ) {
                line.setRef_OrderLine_ID( fromLines[ i ].getC_OrderLine_ID());
            } else {
                line.setRef_OrderLine_ID( 0 );
            }
            if(counter){
            	MBPartner a = new MBPartner(Env.getCtx(),otherOrder.getC_BPartner_ID(),null);
            	line.setAD_Org_ID(Integer.valueOf(a.getAD_OrgBP_ID()).intValue());
            }
            //

            line.setQtyDelivered( Env.ZERO );
            line.setQtyInvoiced( Env.ZERO );
            line.setQtyReserved( Env.ZERO );
            line.setDateDelivered( null );
            line.setDateInvoiced( null );

			// Si el tipo de doc del pedido es transferible, entonces la
			// cantidad de la línea es lo pendiente a entregar y la cantidad
			// facturada es el total ya que no se puede facturar.
            // Se fuerza el seteo de la línea de pedido origen
            if(isOrderTransferred){
            	line.setQty(fromLines[i].getPendingDeliveredQty());
            	line.setQtyInvoiced(line.getQtyOrdered());
            	line.setRef_OrderLine_ID(fromLines[i].getC_OrderLine_ID());
            }
            
            // Tax

            if( getC_BPartner_ID() != otherOrder.getC_BPartner_ID()) {
                line.setTax();    // recalculate
            }

            //
            //

            line.setProcessed( false );

            // Actualización de precios
            if (updatePrices) {
            	line.setDateOrdered(today);
            	line.setPrice();
            }
            
            if( line.save( get_TrxName())) {
                count++;
            }

            // Cross Link

            if( counter ) {
                fromLines[ i ].setRef_OrderLine_ID( line.getC_OrderLine_ID());
                fromLines[ i ].save( get_TrxName());
            }
        }

        if( fromLines.length != count ) {
            log.log( Level.SEVERE,"Line difference - From=" + fromLines.length + " <> Saved=" + count );
        }

        return count;
    }    // copyLinesFrom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MOrder[" ).append( getID()).append( "-" ).append( getDocumentNo()).append( ",IsSOTrx=" ).append( isSOTrx()).append( ",C_DocType_ID=" ).append( getC_DocType_ID()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param M_PriceList_ID
     */

    public void setM_PriceList_ID( int M_PriceList_ID ) {
        MPriceList pl = MPriceList.get( getCtx(),M_PriceList_ID,null );

        if( pl.getID() == M_PriceList_ID ) {
            super.setM_PriceList_ID( M_PriceList_ID );
            setC_Currency_ID( pl.getC_Currency_ID());
            setIsTaxIncluded( pl.isTaxIncluded());
        }
    }    // setM_PriceList_ID

    /**
     * Descripción de Método
     *
     *
     * @param whereClause
     * @param orderClause
     *
     * @return
     */

    public MOrderLine[] getLines( String whereClause,String orderClause ) {
    	log.fine("getLines 1");
        ArrayList    list = new ArrayList();
        StringBuffer sql  = new StringBuffer( "SELECT * FROM C_OrderLine WHERE C_Order_ID=? " );

        if( whereClause != null ) {
            sql.append( whereClause );
        }

        if( orderClause != null ) {
            sql.append( " " ).append( orderClause );
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
            pstmt.setInt( 1,getC_Order_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MOrderLine ol = new MOrderLine( getCtx(),rs,get_TrxName());
                //Ader soporte para cache multi-documentos
                ol.setProductCache(this.getProductCache());

                ol.setHeaderInfo( this );
                list.add( ol );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines - " + sql,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        //

        MOrderLine[] lines = new MOrderLine[ list.size()];

        list.toArray( lines );

        return lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param requery
     * @param orderBy
     *
     * @return
     */

    public MOrderLine[] getLines( boolean requery,String orderBy ) {
    	log.fine("getLines 2");
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        //

        String orderClause = "ORDER BY ";

        if( (orderBy != null) && (orderBy.length() > 0) ) {
            orderClause += orderBy;
        } else {
            orderClause += "Line";
        }

        m_lines = getLines( null,orderClause );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MOrderLine[] getLines() {
    	log.fine("getLines 3");
        return getLines( false,null );
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param step
     */

    public void renumberLines( int step ) {
        int          number = step;
        MOrderLine[] lines  = getLines( true,null );    // Line is default

        for( int i = 0;i < lines.length;i++ ) {
            MOrderLine line = lines[ i ];

            line.setLine( number );
            line.save( get_TrxName());
            number += step;
        }

        m_lines = null;
    }    // renumberLines

    /**
     * Descripción de Método
     *
     *
     * @param C_OrderLine_ID
     *
     * @return
     */

    public boolean isOrderLine( int C_OrderLine_ID ) {
        if( m_lines == null ) {
            getLines();
        }

        for( int i = 0;i < m_lines.length;i++ ) {
            if( m_lines[ i ].getC_OrderLine_ID() == C_OrderLine_ID ) {
                return true;
            }
        }

        return false;
    }    // isOrderLine

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MOrderTax[] getTaxes( boolean requery ) {
        if( (m_taxes != null) &&!requery ) {
            return m_taxes;
        }

        //

        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM C_OrderTax WHERE C_Order_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Order_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MOrderTax( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getTaxes",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        //

        m_taxes = new MOrderTax[ list.size()];
        list.toArray( m_taxes );

        return m_taxes;
    }    // getTaxes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInvoice[] getInvoices() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_Invoice WHERE C_Order_ID=? ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Order_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInvoice( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getInvoices",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        //

        MInvoice[] retValue = new MInvoice[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getInvoices

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Invoice_ID() {
    	return getC_Invoice_ID(false);
    }
    
    public int getC_Invoice_ID(boolean onlyDebits) {
        int       C_Invoice_ID = 0;
        ArrayList list         = new ArrayList();
        StringBuffer    sql = new StringBuffer("SELECT i.C_Invoice_ID FROM C_Invoice as i ");
        if(onlyDebits){
            sql.append(" INNER JOIN C_DocType as dt ON dt.c_doctype_id = i.c_doctypetarget_id ");
        }
        sql.append(" WHERE i.C_Order_ID=? AND i.DocStatus IN ('CO','CL') ");
        if(onlyDebits){
			sql.append(" AND dt.docbasetype IN ('"
					+ MDocType.DOCBASETYPE_APInvoice + "','"
					+ MDocType.DOCBASETYPE_ARInvoice + "') ");
        }
        sql.append(" ORDER BY i.Created DESC ");
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
            pstmt.setInt( 1,getC_Order_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                C_Invoice_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getC_Invoice_ID",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return C_Invoice_ID;
    }    // getC_Invoice_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInOut[] getShipments() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_InOut WHERE C_Order_ID=? ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Order_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInOut( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getShipments",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        //

        MInOut[] retValue = new MInOut[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getShipments

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCurrencyISO() {
        return MCurrency.getISO_Code( getCtx(),getC_Currency_ID());
    }    // getCurrencyISO

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPrecision() {
        return MCurrency.getStdPrecision( getCtx(),getC_Currency_ID());
    }    // getPrecision

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDocStatusName() {
        return MRefList.getListName( getCtx(),131,getDocStatus());
    }    // getDocStatusName

    /**
     * Descripción de Método
     *
     *
     * @param DocAction
     */

    public void setDocAction( String DocAction ) {
        setDocAction( DocAction,false );
    }    // setDocAction

    /**
     * Descripción de Método
     *
     *
     * @param DocAction
     * @param forceCreation
     */

    public void setDocAction( String DocAction,boolean forceCreation ) {
        super.setDocAction( DocAction );
        m_forceCreation = forceCreation;
    }    // setDocAction

    /**
     * Descripción de Método
     *
     *
     * @param processed
     */

    public void setProcessed( boolean processed ) {
        super.setProcessed( processed );

        if( getID() == 0 ) {
            return;
        }

        String set = "SET Processed='" + ( processed
                                           ?"Y"
                                           :"N" ) + "' WHERE C_Order_ID=" + getC_Order_ID();
        int noLine = DB.executeUpdate( "UPDATE C_OrderLine " + set,get_TrxName());
        int noTax = DB.executeUpdate( "UPDATE C_OrderTax " + set,get_TrxName());

        m_lines = null;
        m_taxes = null;
        log.fine( "setProcessed - " + processed + " - Lines=" + noLine + ", Tax=" + noTax );
    }    // setProcessed

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */
    //Ader: nuevo beforeSave : evitar leer TODAS las lineas desde la DB y si solo se requiere
    //saber la cantidad de lineas, bueno, solo leer esto con un solo select
    protected boolean beforeSave( boolean newRecord ) 
    {
    	// Client/Org Check
        if( getAD_Org_ID() == 0 ) {
            int context_AD_Org_ID = Env.getAD_Org_ID( getCtx());
            if( context_AD_Org_ID != 0 ) {
                setAD_Org_ID( context_AD_Org_ID );
                log.warning( "Changed Org to Context=" + context_AD_Org_ID );
            }
        }

        if( getAD_Client_ID() == 0 ) {
            m_processMsg = "AD_Client_ID = 0";
            return false;
        }

        // New Record Doc Type - make sure DocType set to 0
        if( newRecord && (getC_DocType_ID() == 0) ) {
            setC_DocType_ID( 0 );
        }

        // Default Warehouse
        if( getM_Warehouse_ID() == 0 ) {
            int ii = Env.getContextAsInt( getCtx(),"#M_Warehouse_ID" );
            if( ii != 0 ) {
                setM_Warehouse_ID( ii );
            } else {
                log.severe( "No Warehouse" );
                return false;
            }
        }

        // Warehouse Org
        if( newRecord || is_ValueChanged( "AD_Org_ID" ) || is_ValueChanged( "M_Warehouse_ID" )) {
            MWarehouse wh = MWarehouse.get( getCtx(),getM_Warehouse_ID());
            if( wh.getAD_Org_ID() != getAD_Org_ID()) {
                log.saveError( "WarehouseOrgConflict","" );
                return false;
            }
        }
        
        // Reservations in Warehouse
        if( !newRecord && is_ValueChanged( "M_Warehouse_ID" )) {
        	//TODO: pasar esto a una solo acceso; actualmente no molesta desde el tpv
            MOrderLine[] lines = getLines( false,null );
            for( int i = 0;i < lines.length;i++ ) {
                if( !lines[ i ].canChangeWarehouse()) {
                    return false;
                }
            }
        }

        // No Partner Info - set Template
        if( getC_BPartner_ID() == 0 ) {
            setBPartner( MBPartner.getTemplate( getCtx(),getAD_Client_ID()));
        }
        if( getC_BPartner_Location_ID() == 0 ) {
            setBPartner( new MBPartner( getCtx(),getC_BPartner_ID(),null ));
        }

        // No Bill - get from Ship
        if( getBill_BPartner_ID() == 0 ) {
            setBill_BPartner_ID( getC_BPartner_ID());
            setBill_Location_ID( getC_BPartner_Location_ID());
        }
        if( getBill_Location_ID() == 0 ) {
            setBill_Location_ID( getC_BPartner_Location_ID());
        }

        // Default Price List
        if( getM_PriceList_ID() == 0 ) {
            int ii = DB.getSQLValue( null,"SELECT M_PriceList_ID FROM M_PriceList " 
            		+ "WHERE AD_Client_ID=? AND IsSOPriceList=? " 
            		+ "ORDER BY IsDefault DESC",
            		getAD_Client_ID(),
            		isSOTrx()?"Y":"N" );

            if( ii != 0 ) {
                setM_PriceList_ID( ii );
            }
        }
        // Default Currency
        if( getC_Currency_ID() == 0 ) {
            String sql = "SELECT C_Currency_ID FROM M_PriceList WHERE M_PriceList_ID=?";
            int ii = DB.getSQLValue( null,sql,getM_PriceList_ID());

            if( ii != 0 ) {
                setC_Currency_ID( ii );
            } else {
                setC_Currency_ID( Env.getContextAsInt( getCtx(),"#C_Currency_ID" ));
            }
        }

        // Default Sales Rep
        if( getSalesRep_ID() == 0 ) {
            int ii = Env.getContextAsInt( getCtx(),"#SalesRep_ID" );
            if( ii != 0 ) {
                setSalesRep_ID( ii );
            }
        }

        // Default Document Type
        if( getC_DocTypeTarget_ID() == 0 ) {
            setC_DocTypeTarget_ID( DocSubTypeSO_Standard );
        }

        // Default Payment Term
        if( getC_PaymentTerm_ID() == 0 ) {
            int ii = Env.getContextAsInt( getCtx(),"#C_PaymentTerm_ID" );
            if( ii != 0 ) {
                setC_PaymentTerm_ID( ii );
            } else {
                String sql = "SELECT C_PaymentTerm_ID FROM C_PaymentTerm WHERE AD_Client_ID=? AND IsDefault='Y'";
                ii = DB.getSQLValue( null,sql,getAD_Client_ID());
                if( ii != 0 ) {
                    setC_PaymentTerm_ID( ii );
                }
            }
        }
        
        MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID(), get_TrxName());
        
        // Se valida el nro de documento único cuando este documento está en Borrador solamente
        if(MOrder.DOCSTATUS_Drafted.equals(getDocStatus())){
    		// Nro de documento existente, entonces colocar corchetes al principio y
    		// final para que genere uno nuevo
            String whereClause = "ad_client_id = ? and ad_org_id = ? and c_doctypetarget_id = ? and docstatus in ('CO','CL')";
            whereClause += newRecord?"":" and documentno = '"+getDocumentNo()+"'";
    		if (docType.isUniqueDocumentno()
    				&& existRecordFor(getCtx(), get_TableName(), whereClause,
    						new Object[] { getAD_Client_ID(), getAD_Org_ID(),
    								getC_DocTypeTarget_ID() }, get_TrxName())) {
            	setDocumentNo("<"+getDocumentNo()+">");
            }
        }
        
        //si el registro es nuevo, no pueden haber lineas, no tiene sentido el chequeo
    	//si ni la lista de precios ni la moneda cambio, tampoco hay que hacer el chequoe
    	boolean needCheckQtyLines =
    		!newRecord && 
    		(is_ValueChanged( "M_PriceList_ID" ) ||
    		is_ValueChanged( "C_Currency_ID" ));
    	if (needCheckQtyLines) //si newRecord necesariamete se tiene 0 lineas
    	{
      		int qtyLines = getQtyLinesFromDB();
			// Disytel: Si ya se incorporaron lineas, no permitir el cambio de
			// la tarifa. Siempre y cuando el flag del tipo de documento que
			// permite cambiar la tarifa se encuentre activo 
			if (is_ValueChanged("M_PriceList_ID") && qtyLines > 0
					&& !docType.isAllowChangePriceList())
      		{
      			log.saveError( "Error",Msg.getMsg( getCtx(),"PriceListChangedLinesAlreadyLoadedConfig" ));
      			return false;
    		}
      		// Disytel: Si ya se incorporaron lineas, no permitir el cambio de la moneda destino
      		if (is_ValueChanged( "C_Currency_ID" ) && qtyLines > 0 )
      		{
      			log.saveError( "Error", Msg.getMsg( getCtx(),"CurrencyChangedLinesAlreadyLoaded" ));
      			return false;
      		}        
    	}
        
        //TODO: cambiar esto para usa cache....
        // Disytel: Si no hay conversion, no permitir seleccionar moneda destino
        int priceListCurrency = new MPriceList(getCtx(), getM_PriceList_ID(), null).getC_Currency_ID();
        if (priceListCurrency != getC_Currency_ID() && MCurrency.currencyConvert(new BigDecimal(1), priceListCurrency, getC_Currency_ID(), getDateOrdered(), getAD_Org_ID(), getCtx()) == null)
        {
        	log.saveError( "Error", Msg.getMsg( getCtx(),"NoCurrencyConversion" ) );
        	return false;
        } 
        
        // Si la Tarifa es mayor a 0 setear el Impuesto Incluido a partir de la tarifa
 		if (getM_PriceList_ID() > 0 ){
 			setIsTaxIncluded(new MPriceList(getCtx(), getM_PriceList_ID(),null).isTaxIncluded()); 
 		}
        
        // Verifica la Fecha de validez (solo para Presupuestos)
        if (MDocType.DOCSUBTYPESO_Proposal.equals(getDocSubTypeSO())) {
        	if (getValidTo() != null && getValidTo().compareTo(getDateOrdered()) < 0) {
        		log.saveError("SaveError", Msg.getMsg(getCtx(), "OrderValidToLessThanDateError"));
        		return false;
        	}
        } else {
        	setValidTo(null);
        }
        
		// Para pedidos transferibles la organización no debe ser igual a la
		// organización destino y el almacén tampoco debe ser el mismo
		boolean isOrderTransferred = docType.getDocTypeKey()
				.equalsIgnoreCase(MDocType.DOCTYPE_Pedido_Transferible);
		if(isOrderTransferred){
			// Organización
			if(getAD_Org_ID() == getAD_Org_Transfer_ID()){
				log.saveError("OrderTransferredEqualOrg", "");
				return false;
			}
			// Almacén
			if(getM_Warehouse_ID() == getM_Warehouse_Transfer_ID()){
				log.saveError("OrderTransferredEqualWarehouse", "");
				return false;
			}
		}
		
		// Si cambió la tarifa, entonces actualizar todos los precios de las
		// líneas
		// Se debe validar a su vez que todos los artículos existan dentro de la
		// tarifa, sino error
		if (!newRecord 
				&& docType.isAllowChangePriceList()
				&& (is_ValueChanged("M_PriceList_ID") || is_ValueChanged("DateOrdered"))
				&& (MOrder.DOCSTATUS_Drafted.equals(getDocStatus()) 
						|| MOrder.DOCSTATUS_InProgress.equals(getDocStatus()))) {
			// Si no cambió la tarifa, pero sí la fecha, sólo modifico los
			// valores si la nueva fecha trae como consecuencia otra versión de
			// tarifa que la fecha anterior
			boolean changePrices = true;
			if (!is_ValueChanged("M_PriceList_ID")
					&& is_ValueChanged("DateOrdered")) {
				MPriceList priceList = MPriceList.get(getCtx(),
						getM_PriceList_ID(), get_TrxName());
				MPriceListVersion pricelistVersionOrder = priceList
						.getPriceListVersion(
								(Timestamp) get_ValueOld("DateOrdered"), true);
				MPriceListVersion pricelistVersionActual = priceList
						.getPriceListVersion(getDateOrdered(), true);
				changePrices = (pricelistVersionOrder == null && pricelistVersionActual != null)
						|| (pricelistVersionOrder != null && pricelistVersionActual == null)
						|| (pricelistVersionActual.getID() != pricelistVersionOrder
								.getID());
			}
			if(changePrices){
				// Verificar los artículos que no se encuentran dentro de la tarifa
				// seleccionada y abortar si es que existe alguno
				CallResult result = isAllProductsInPriceList(); 
				if(result.isError()){
					log.saveError("SaveError", result.getMsg());
					return false;
				}
				// Iterar por las líneas y modifico sus precios
				MProductPricing pp;
				for (MOrderLine orderLine : getLines()) {
					pp = new MProductPricing(orderLine.getM_Product_ID(),
							getC_BPartner_ID(), orderLine.getQtyEntered(),
							isSOTrx());
					pp.setPriceDate(getDateOrdered());
					pp.setM_PriceList_ID(getM_PriceList_ID());
					orderLine.setPriceList(pp.getPriceList());
					orderLine.setPriceLimit(pp.getPriceLimit());
					// Seteo el precio actual y el entered en base al precio de
					// lista aplicando el descuento existente
					orderLine.setPrice(pp.getPriceList().subtract(
							pp.getPriceList().multiply(
									orderLine.getDiscount().divide(
											Env.ONEHUNDRED, 4,
											BigDecimal.ROUND_HALF_UP))));
					if(!orderLine.save()){
						log.saveError("SaveError", CLogger.retrieveErrorAsString());
						return false;
					}
				}
				// Actualizar el neto y el total del pedido
				calculateTaxTotal();
			}
		}

        return true;
    }    // beforeSave
    
    /**
     * Verifica si todos los artículos de las líneas se encuentran en la tarifa configurada
     * @param order
     * @return
     */
    private CallResult isAllProductsInPriceList(){
    	CallResult result = new CallResult();
		// Query para determinar los artículos que no están en la lista de
		// precios
		String sql = "SELECT p.value, p.name " +
					 "FROM (SELECT DISTINCT m_product_id " +
					 "		FROM c_orderline " +
					 "		WHERE c_order_id = ? " +
					 "		EXCEPT " +
					 "		SELECT DISTINCT ol.m_product_id " +
					 "		FROM c_orderline as ol " +
					 "		INNER JOIN m_productprice as pp on pp.m_product_id = ol.m_product_id " +
					 "		INNER JOIN m_pricelist_version as plv on plv.m_pricelist_version_id = pp.m_pricelist_version_id " +
					 "		WHERE ol.c_order_id = ? AND plv.m_pricelist_id = ? AND pp.isactive = 'Y' AND date_trunc('day',validfrom) <= date_trunc('day',?::timestamp)) as ne " +
					 "INNER JOIN m_product as p on p.m_product_id = ne.m_product_id " +
					 "ORDER BY p.value";
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean allInPriceList = true;
		HTMLMsg msg = new HTMLMsg();
		HTMLMsg.HTMLList productList = msg.createList("products", "ul",
				Msg.getMsg(getCtx(), "ProductsNotInPriceList"));  
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, getID());
			ps.setInt(2, getID());
			ps.setInt(3, getM_PriceList_ID());
			ps.setTimestamp(4, getDateOrdered());
			rs = ps.executeQuery();
			while (rs.next()) {
				allInPriceList = false;
				// Agregar artículo a la lista
				msg.createAndAddListElement(rs.getString("value"),
						rs.getString("value") + " - " + rs.getString("name"),
						productList);
			}
			msg.addList(productList);
		} catch (Exception e) {
			getLog().severe(e.getMessage());
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				getLog().severe(e2.getMessage());
			}
		}
		
		if(!allInPriceList){
			result.setMsg(msg.toString(), true);
		}
		
		return result;
	}
    
    /**
     * Descripción de Método
     * 
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        if( !success || newRecord ) {
            return success;
        }

        // Propagate Description changes
        if( is_ValueChanged( "Description" ) || is_ValueChanged( "POReference" )) {
        	// begin DMA - Dataware - BugNo: 242
        	String sql;
        	
        	if(DB.isPostgreSQL()) {
	            sql = "UPDATE C_Invoice " +
	            			 "SET Description=o.Description, POReference=o.POReference " + 
	            			 "FROM C_Order o " +
	            			 "WHERE C_Invoice.C_Order_ID=o.C_Order_ID " +
	            			 "AND C_Invoice.DocStatus NOT IN ('RE','CL') " +
	            			 "AND C_Invoice.C_Order_ID=" + getC_Order_ID();
        	} else {
        		sql = "UPDATE C_Invoice i" + 
        			  " SET (Description,POReference)=" + 
        			  "(SELECT Description,POReference " + 
        			  "FROM C_Order o WHERE i.C_Order_ID=o.C_Order_ID) " + 
        			  "WHERE DocStatus NOT IN ('RE','CL') AND C_Order_ID=" + getC_Order_ID();
        	}
            // end DMA - Dataware - BugNo: 242
            
            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( "Description -> #" + no );
        }
        
        // Propagate Changes of Payment Info to existing (not reversed/closed) invoices

        if( is_ValueChanged( "PaymentRule" ) || is_ValueChanged( "C_PaymentTerm_ID" ) || is_ValueChanged( "DateAcct" ) || is_ValueChanged( "C_Payment_ID" ) || is_ValueChanged( "C_CashLine_ID" )) {
            // begin DMA - Dataware - BugNo: 242
        	String sql;
        	
        	if(DB.isPostgreSQL()) {
	            sql = "UPDATE C_Invoice " +
	            			 "SET PaymentRule=o.PaymentRule, C_PaymentTerm_ID=o.C_PaymentTerm_ID, DateAcct=o.DateAcct, " +
	            			 "    C_Payment_ID=o.C_Payment_ID, C_CashLine_ID=o.C_CashLine_ID " +
	            			 "FROM C_Order o " + 
	            			 "WHERE C_Invoice.C_Order_ID=o.C_Order_ID " +
	            			 "AND C_Invoice.DocStatus NOT IN ('RE','CL') " +
	            			 "AND C_Invoice.C_Order_ID=" + getC_Order_ID();
        	} else {
        		sql = "UPDATE C_Invoice i " + 
        			  "SET (PaymentRule,C_PaymentTerm_ID,DateAcct,C_Payment_ID,C_CashLine_ID)=" + 
        			  "(SELECT PaymentRule,C_PaymentTerm_ID,DateAcct,C_Payment_ID,C_CashLine_ID " + 
        			  "FROM C_Order o WHERE i.C_Order_ID=o.C_Order_ID)" + 
        			  "WHERE DocStatus NOT IN ('RE','CL') AND C_Order_ID=" + getC_Order_ID();	        	
        	}
            // end DMA - Dataware - BugNo: 242

            // Don't touch Closed/Reversed entries

            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( "Payment -> #" + no );
        }

        // Sync Lines

        afterSaveSync( "AD_Org_ID" );
        afterSaveSync( "C_BPartner_ID" );
        afterSaveSync( "C_BPartner_Location_ID" );
        afterSaveSync( "DateOrdered" );
        afterSaveSync( "DatePromised" );
        afterSaveSync( "M_Warehouse_ID" );
        afterSaveSync( "M_Shipper_ID" );
        afterSaveSync( "C_Currency_ID" );

        // begin vpj-cd e-evolution 01/25/2005 CMPCS

//        afterSaveSync( "DocStatus" );

        // end vpj-cd e-evolution 01/25/2005 CMPCS

        return true;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     */

    private void afterSaveSync( String columnName ) {
    	log.fine(" En afterSaveSync con columName = " + columnName);

        // begin vpj-cd e-evolution 01/25/2005 CMPCS

//        if( is_ValueChanged( "DocStatus" ) || is_ValueChanged( "DatePromised" ) || is_ValueChanged( "M_Warehouse_ID" )) {
//            if( (columnName.compareTo( "DocStatus" ) == 0) || (columnName.compareTo( "DatePromised" ) == 0) || (columnName.compareTo( "M_Warehouse_ID" ) == 0) ) {
//                // begin DMA - Dataware - BugNo: 242
//            	String sql;
//            	
//            	if(DB.isPostgreSQL()) {
//            		sql = "UPDATE MPC_MRP " + 
//                			 "SET " + columnName + "=o." + columnName + " " +
//            				 "FROM C_Order o " +
//            				 "WHERE MPC_MRP.C_Order_ID=o.C_Order_ID " +
//            				 "AND MPC_MRP.C_Order_ID=" + getC_Order_ID();
//            	} else {
//            		sql = " UPDATE MPC_MRP m " + 
//            			  "SET " + columnName + " =" + 
//            			  " (SELECT " + columnName + " FROM C_Order o WHERE m.C_Order_ID=o.C_Order_ID) " + 
//            			  " WHERE m.C_Order_ID = " + getC_Order_ID();            		
//            	}
//                // end DMA - Dataware - BugNo: 242
//                int no = DB.executeUpdate( sql,get_TrxName());
//
//                log.fine( columnName + " MPC_MRP set DocStatus --------di---" + no +"   "+ sql);
//            }
//
//            if( columnName.compareTo( "DocStatus" ) == 0 ) {
//            	log.fine("Retornado en aftersavesync");
//                return;
//            }
//        }

        // end vpj-cd e-evolution 01/25/2005 CMPCS

        if( is_ValueChanged( columnName )) {
        	// begin DMA - Dataware - BugNo: 242
        	String sql;
        	
        	if(DB.isPostgreSQL()) {
	        	sql = "UPDATE C_OrderLine " +
	        				 "SET " + columnName + "=o." + columnName + " " +
	        				 "FROM C_Order o " +
	        				 "WHERE C_OrderLine.C_Order_ID=o.C_Order_ID " +
	        				 "AND C_OrderLine.C_Order_ID=" + getC_Order_ID();
        	} else {
        		sql = "UPDATE C_OrderLine ol" + 
        			" SET " + columnName + " =" + 
        			"(SELECT " + columnName + " FROM C_Order o WHERE ol.C_Order_ID=o.C_Order_ID) " + 
        			"WHERE C_Order_ID=" + getC_Order_ID();	
        	}
        	
        	// end DMA - Dataware - BugNo: 242
            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( columnName + " Lines -> #" + no );
        }
    }    // afterSaveSync

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        if( isProcessed()) {
            return false;
        }

        getLines();

        for( int i = 0;i < m_lines.length;i++ ) {
            if( !m_lines[ i ].beforeDelete()) {
                return false;
            }
        }

        return true;
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param processAction
     *
     * @return
     */

    public boolean processIt( String processAction ) {
        m_processMsg = null;

        DocumentEngine engine = new DocumentEngine( this,getDocStatus());

        boolean status = engine.processIt( processAction,getDocAction(),log );
        
        return status;
    }    // processIt


    /** Descripción de Campos */

    private boolean m_justPrepared = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean unlockIt() {
        log.info( "unlockIt - " + toString());
        setProcessing( false );

        return true;
    }    // unlockIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean invalidateIt() {
        log.info( toString());
        setDocAction( DOCACTION_Prepare );

        return true;
    }    // invalidateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String prepareIt() {
        log.info( toString());
        m_processMsg = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_BEFORE_PREPARE );

        if( m_processMsg != null ) {
            return DocAction.STATUS_Invalid;
        }

        MDocType dt = MDocType.get( getCtx(),getC_DocTypeTarget_ID());
        MBPartner bpartner = new MBPartner(getCtx(), getC_BPartner_ID(), get_TrxName());
        
        // Std Period open?

        if( !MPeriod.isOpen( getCtx(),getDateAcct(),dt.getDocBaseType())) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        // Se borran las líneas del pedido que tienen cantidad igual a cero.
        
        /* TPV performance:
         * 	Evitar invocacion (y doble) a getLines, manteniendo variable totalLines 
         *  El seteo del numero de linea no tiene implicancia alguna, realizarlo via SQL
         *  Instanciar solo las lineas que sea necesario (el resto usar los IDs)
         */
/*
        int orderLineIDs[] = PO.getAllIDs("C_OrderLine", " C_Order_ID = " + getC_Order_ID(), get_TrxName());
        int totalLines = orderLineIDs.length;
        int newLineNo = 10;
                
        for (int lineID : orderLineIDs) {
        	BigDecimal qtyOrdered = DB.getSQLValueBD(get_TrxName(), " SELECT COALESCE(qtyordered,0) FROM C_OrderLine WHERE C_OrderLine_ID = ?", lineID);
        	if (Env.ZERO.compareTo(qtyOrdered) == 0) {
        		MOrderLine line = new MOrderLine(getCtx(), lineID, get_TrxName());
				if(!line.delete(true))
					log.severe("Could not delete 0-quantity order line. LineID=" + lineID);
				else
					totalLines--;
        	} else {
        		int cant = DB.executeUpdate(" UPDATE C_OrderLine SET line = " + newLineNo + 
        									" WHERE C_OrderLine_ID = " + lineID , get_TrxName());
        		if (cant==0)
        			log.warning("Could not set the new line number. LineID="+lineID +", New Line=" + newLineNo);
        		else
        			newLineNo += 10;        		
        	}
		}

        // Lines
              
        if( totalLines == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }
*/

        // Se borran las líneas del pedido que tienen cantidad igual a cero. 
        //(borrar codigo comentado previo y esta misma linea una vez testeada la mejora).
        //ADER: Performance para TPV Y Logica clásica. Las cosas se actualizan SOLO si es encesario
        //Tambien, ante la falla de eliminación SE CANCELA; principalmente porque PostGreSQL
        //tiene la cualidad de rollbackear automaticamente ante errores. Idem para la reenumeración.
        //La eliminación de lineas con cantidad ordenada cero y la reenumeración se hace por separado
        //Y SOLO si es necesario. Si la reenumeración es llevada a cabo, es posible que
        //se tenga que reeler las lineas, si es que la lógica posterior lo requiere; ver
        //m_needReloadForRenumber (si es true, las unico que puede ser distinto en DB es el campo Line).
        
        boolean ok;
        //eliminación de lineas con cantidad cero (si es necesario).
        //Accesos a DB: 0 o 1 (0 en el caso más frecuente)
        ok = deleteQtyOrderedCeroIfNeeded();
        if (!ok)
        {
        	m_processMsg = "Error eliminando lineas con cantidad ordenada cero"; //TODO: mejorar mensaje
        	return DocAction.STATUS_Invalid;
        }
        
        //reenumración 10,20,30.... SOLO si es necesario. 
        //Accesos a DB: 0 o 1 (0 en el caso más frecuente)
        ok = renumberLinesIfNeeded();
        if (!ok)
        {
        	m_processMsg = "Error reenumerando lineas"; //TODO: mejorar mensaje
         	return DocAction.STATUS_Invalid;
            
        }
        //en este punto m_lines tiene las lineas que realmente estan en la base de datos
        //salvoq que se haya cometido un error previo. Lo unico que puede
        //diferir son los campos Line


        if( m_lines.length <= 0 ) { //m_lines es potencialmente actulizado or deleteQtyOrdered
            m_processMsg = "@NoLines@";
            return DocAction.STATUS_Invalid;
        }
        
        //NOTA: luego de estas dos metodos, no es necesario forzar a reeleer las lineas
        //los pasos que siguen, por ej explodeBOM, lo hacen explicitamente en caso
        //de ser necesario.
        //FIN de Mejora I
        
		// Actualización del precio de lista al priceactual en el caso que el
		// precio de lista sea 0
		DB.executeUpdate(
				"UPDATE c_orderline SET pricelist = priceentered WHERE c_order_id = "
						+ getID() + " AND pricelist = 0", get_TrxName());
        

        // Convert DocType to Target

        if( getC_DocType_ID() != getC_DocTypeTarget_ID()) {

            // Cannot change Std to anything else if different warehouses

            if( getC_DocType_ID() != 0 ) {
                MDocType dtOld = MDocType.get( getCtx(),getC_DocType_ID());

                if( MDocType.DOCSUBTYPESO_StandardOrder.equals( dtOld.getDocSubTypeSO())    // From SO
                        &&!MDocType.DOCSUBTYPESO_StandardOrder.equals( dt.getDocSubTypeSO()))    // To !SO
                        {
		                	// verificar que las lineas tengan el mismo warehouse que la cabecera
		                	String sql = " select count(1) from ( " +
		                					"		select distinct m_warehouse_id " +
		                					" 		from c_orderline " +
		                					"		where c_order_id = " + getC_Order_ID() +  // warehouse de las lineas
		                					"		union select " + getM_Warehouse_ID() +    // warehouse de la cabecera
		                					"	) as foo ";
		                	int count = DB.getSQLValue(get_TrxName(), sql);
		           
                			if (count != 1) {
	                            log.warning( "different Warehouse");
	                            m_processMsg = "@CannotChangeDocType@";
	
	                            return DocAction.STATUS_Invalid;
		                    }
                        }
            }

            // New or in Progress/Invalid

            if( DOCSTATUS_Drafted.equals( getDocStatus()) || DOCSTATUS_InProgress.equals( getDocStatus()) || DOCSTATUS_Invalid.equals( getDocStatus()) || (getC_DocType_ID() == 0) ) {
                setC_DocType_ID( getC_DocTypeTarget_ID());
            } else    // convert only if offer
            {
                if( dt.isOffer()) {
                    setC_DocType_ID( getC_DocTypeTarget_ID());
                } else {
                    m_processMsg = "@CannotChangeDocType@";

                    return DocAction.STATUS_Invalid;
                }
            }
        }             // convert DocType
        
        // Si la Tarifa es mayor a 0 setear el Impuesto Incluido a partir de la tarifa
  		if (getM_PriceList_ID() > 0 ){
  			setIsTaxIncluded(new MPriceList(getCtx(), getM_PriceList_ID(),null).isTaxIncluded()); 
  		}

        // Mandatory Product Attribute Set Instance

        String mandatoryType = "='Y'";    // IN ('Y','S')
        String sql	= " SELECT COUNT(*) " + "FROM C_OrderLine ol" 
    				+ " INNER JOIN M_Product p ON (ol.M_Product_ID=p.M_Product_ID)" 
    				+ " INNER JOIN M_AttributeSet pas ON (p.M_AttributeSet_ID=pas.M_AttributeSet_ID) " 
    				+ " WHERE pas.MandatoryType" + mandatoryType 
    				+ " AND ol.M_AttributeSetInstance_ID IS NULL" 
    				+ " AND ol.C_Order_ID=?";
        int no = DB.getSQLValue( get_TrxName(),sql,getC_Order_ID());

        if( no != 0 ) {
            m_processMsg = "@LinesWithoutProductAttribute@ (" + no + ")";

            return DocAction.STATUS_Invalid;
        }

        // Lines

        explodeBOM();

        
        // Controlar las cantidades minimas pedidas
        if (!isSOTrx()
				&& dt.getDocTypeKey()
						.equals(MDocType.DOCTYPE_PurchaseOrder)) {
        	CallResult result = controlOrderMin();
        	if(result.isError()){
        		m_processMsg = result.getMsg();
                return DocAction.STATUS_Invalid;
        	}
        }
        
        // Si el tipo de documento está marcado que sólo permita artículos del proveedor
        if(dt.isOnlyVendorProducts()){
        	CallResult result = controlVendorProducts();
        	if(result.isError()){
        		setProcessMsg(result.getMsg());
        		return DocAction.STATUS_Invalid; 
        	}
        }
        
        // instanciar recien ahora las lineas
        /*
        int[] lines = PO.getAllIDs("C_OrderLine", "C_Order_ID = " + getC_Order_ID() , get_TrxName() );
        if( !reserveStock( dt, lines )) {
            m_processMsg = "@CannotReserveStock@";

            return DocAction.STATUS_Invalid;
        }
        */
        
        // Validación de productos "Comprados" en las líneas del pedido si el documento
        // es un pedido a proveedor.
        if (dt.isDocType(MDocType.DOCTYPE_PurchaseOrder)) {
        	List<String> notPurchasedProducts = getNotPurchasedProducts();
        	if (!notPurchasedProducts.isEmpty()) {
        		m_processMsg = "@NotPurchasedProductsError@ " + notPurchasedProducts.toString();
        		return DocAction.STATUS_Invalid;
        	}
        	
        	// Monto mínimo de compra
        	if(getGrandTotal().compareTo(bpartner.getMinimumPurchasedAmt()) < 0){
				m_processMsg = Msg.getMsg(getCtx(),
						"GrandTotalNotSurpassMinimumPurchaseAmt",
						new Object[] { bpartner.getMinimumPurchasedAmt() });
        		return DocAction.STATUS_Invalid;
        	}
        }
        
        //Ader, Mejora II: reservación de stock mejorada
		boolean isOrderTransferable = dt.getDocTypeKey().equalsIgnoreCase(
				MDocType.DOCTYPE_Pedido_Transferible);
		// Para pedidos transferibles no se debe reservar stock
        if(!isOrderTransferable && !reserveStockII( dt )) {
            m_processMsg = "@CannotReserveStock@";
            return DocAction.STATUS_Invalid;
        }
        //NOTA: luego de este metodo, no es necesario releer las lineas ya que calculeTaxTotal
        //lo va a hacer explicitamente (aunque posiblemente NO lo requiera).
        //Fin de Mejora II
        
        if( !calculateTaxTotal()) {
            m_processMsg = "@TaxCalculatingError@";

            return DocAction.STATUS_Invalid;
        }
        
        m_justPrepared = true;

        // if (!DOCACTION_Complete.equals(getDocAction()))         don't set for just prepare
        // setDocAction(DOCACTION_Complete);
        
        return DocAction.STATUS_InProgress;
    }    // prepareIt

    /**
     * Descripción de Método
     *
     */

    private void explodeBOM() {
        String where = "AND IsActive='Y' AND EXISTS " + "(SELECT * FROM M_Product p WHERE C_OrderLine.M_Product_ID=p.M_Product_ID" + " AND p.IsBOM='Y' AND p.IsVerified='Y' AND p.IsStocked='N')";

        //

        String sql = "SELECT COUNT(*) FROM C_OrderLine " + "WHERE C_Order_ID=? " + where;
        int count = DB.getSQLValue( get_TrxName(),sql,getC_Order_ID());

        while( count != 0 ) {
            renumberLines( 1000 );    // max 999 bom items

            // Order Lines with non-stocked BOMs

            MOrderLine[] lines = getLines( where,"ORDER BY Line" );

            for( int i = 0;i < lines.length;i++ ) {
                MOrderLine line    = lines[ i ];
                MProduct   product = MProduct.get( getCtx(),line.getM_Product_ID());

                log.fine( product.getName());

                // New Lines

                int           lineNo = line.getLine();
                MProductBOM[] boms   = MProductBOM.getBOMLines( product );

                for( int j = 0;j < boms.length;j++ ) {
                    MProductBOM bom     = boms[ j ];
                    MOrderLine  newLine = new MOrderLine( this );

                    newLine.setLine( ++lineNo );
                    newLine.setM_Product_ID( bom.getProduct().getM_Product_ID());
                    newLine.setC_UOM_ID( bom.getProduct().getC_UOM_ID());
                    newLine.setQty( line.getQtyOrdered().multiply( bom.getBOMQty()));

                    if( bom.getDescription() != null ) {
                        newLine.setDescription( bom.getDescription());
                    }

                    //

                    newLine.setPrice();
                    newLine.save( get_TrxName());
                }

                // Convert into Comment Line

                line.setM_Product_ID( 0 );
                line.setM_AttributeSetInstance_ID( 0 );
                line.setPrice( Env.ZERO );
                line.setPriceLimit( Env.ZERO );
                line.setPriceList( Env.ZERO );
                line.setLineNetAmt( Env.ZERO );
                line.setFreightAmt( Env.ZERO );

                //

                String description = product.getName();

                if( product.getDescription() != null ) {
                    description += " " + product.getDescription();
                }

                if( line.getDescription() != null ) {
                    description += " " + line.getDescription();
                }

                line.setDescription( description );
                line.save( get_TrxName());
            }                  // for all lines with BOM

            m_lines = null;    // force requery
            count   = DB.getSQLValue( get_TrxName(),sql,getC_Invoice_ID());
            renumberLines( 10 );
        }                      // while count != 0
    }                          // explodeBOM

    /**
     * Descripción de Método
     *
     *
     * @param dt
     * @param lines
     *
     * @return
     */
    	// TPV Performance: utilizar identificadores de lineas en lugar de los objetos
    public boolean reserveStock( MDocType dt,int[] orderLineIDs ) {
        if( dt == null ) {
            dt = MDocType.get( getCtx(),getC_DocType_ID());
        }

        // Binding

        boolean binding = !dt.isProposal();

        // Not binding - i.e. Target=0

        if( DOCACTION_Void.equals( getDocAction())

        // Closing Binding Quotation

        || ( MDocType.DOCSUBTYPESO_Quotation.equals( dt.getDocSubTypeSO()) && DOCACTION_Close.equals( getDocAction())) || isDropShip()) {
            binding = false;
        }

        boolean isSOTrx = isSOTrx();

        log.fine( "Binding=" + binding + " - IsSOTrx=" + isSOTrx );

        // Force same WH for all but SO/PO

        int header_M_Warehouse_ID = getM_Warehouse_ID();

        if( MDocType.DOCSUBTYPESO_StandardOrder.equals( dt.getDocSubTypeSO()) || MDocType.DOCBASETYPE_PurchaseOrder.equals( dt.getDocBaseType())) {
            header_M_Warehouse_ID = 0;    // don't enforce
        }

        // Always check and (un) Reserve Inventory

        /*
         * TPV Performance
         */
        boolean ol_ok = false;
        int ol_M_Warehouse_ID = -1, ol_AD_Org_ID = -1, ol_Line = -1, ol_M_Product_ID = -1, ol_M_AttributeSetInstance_ID = -1;
        BigDecimal ol_QtyOrdered = null, ol_QtyReserved = null, ol_QtyDelivered = null, ol_QtyTransferred = null;
        
        for( int i = 0;i < orderLineIDs.length;i++ ) {
            int anOrderLineID = orderLineIDs[ i ];

        		try    	{
        			String sql = " SELECT M_Warehouse_ID, AD_Org_ID, Line, M_Product_ID, M_AttributeSetInstance_ID, " +
        							" QtyOrdered, QtyReserved, QtyDelivered, QtyTransferred FROM C_OrderLine WHERE C_OrderLine_ID = " + anOrderLineID;
        			PreparedStatement stmt =  DB.prepareStatement(sql , get_TrxName());
        			ResultSet rs = stmt.executeQuery();
        			if (rs.next())
        			{
	                	ol_ok = true;
	                	ol_M_Warehouse_ID = rs.getInt(1);
	                	ol_AD_Org_ID = rs.getInt(2);
	                	ol_Line = rs.getInt(3);
	                	ol_M_Product_ID = rs.getInt(4);
	                	ol_M_AttributeSetInstance_ID = rs.getInt(5);
	                	ol_QtyOrdered = rs.getBigDecimal(6);
	                	ol_QtyReserved = rs.getBigDecimal(7);
	                	ol_QtyDelivered = rs.getBigDecimal(8);
	                	ol_QtyTransferred = rs.getBigDecimal(9);
        			}
        		}
	        	catch (Exception e)	{
	        		e.printStackTrace();
	                log.saveError(" Error recuperando datos de linea de pedido: " + anOrderLineID, e.getMessage());
	                return false;
	        	}

            
            
            // Check/set WH/Org

            if( header_M_Warehouse_ID != 0 )    // enforce WH
            {
             
            	DB.executeUpdate(" UPDATE C_OrderLine " +
            						" SET M_Warehouse_ID = " + header_M_Warehouse_ID + ", AD_Org_ID = " + getAD_Org_ID() +  
            						" WHERE C_OrderLine_ID = " + anOrderLineID, get_TrxName());
            }

            // Binding

            BigDecimal target     = binding
                                    ?ol_QtyOrdered
                                    :Env.ZERO;
			BigDecimal difference = target.subtract(
					ol_QtyReserved.subtract(ol_QtyTransferred)).subtract(
					ol_QtyDelivered.add(ol_QtyTransferred));
            
            

			if (difference.compareTo(Env.ZERO) == 0
					&& ol_QtyTransferred.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            log.fine( "Line=" + ol_Line + " - Target=" + target + ",Difference=" + difference + " - Ordered=" + ol_QtyOrdered + ",Reserved=" + ol_QtyReserved + ",Delivered=" + ol_QtyDelivered);

            // Check Product - Stocked and Item

            MProduct product = new MProduct(getCtx(), ol_M_Product_ID, get_TrxName());

            if( (product != null) && product.isStocked()) {
                BigDecimal ordered      = isSOTrx
                                          ?Env.ZERO
                                          :difference;
                BigDecimal reserved     = isSOTrx
                                          ?difference
                                          :Env.ZERO;
                int        M_Locator_ID = 0;

                // Get Locator to reserve

                if( ol_M_AttributeSetInstance_ID != 0 ) {    // Get existing Location
                    M_Locator_ID = MStorage.getM_Locator_ID( ol_M_Warehouse_ID,ol_M_Product_ID,ol_M_AttributeSetInstance_ID,ordered,get_TrxName());
                }

                // Get default Location

                if( M_Locator_ID == 0 ) {
                    MWarehouse wh = MWarehouse.get( getCtx(),ol_M_Warehouse_ID);

                    M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
                }

                // Update Storage

				if ((reserved.compareTo(Env.ZERO) != 0 || ordered
						.compareTo(Env.ZERO) != 0)
						&& !MStorage.add(getCtx(), ol_M_Warehouse_ID,
								M_Locator_ID,
						ol_M_Product_ID, ol_M_AttributeSetInstance_ID,
						ol_M_AttributeSetInstance_ID, Env.ZERO, reserved,
						ordered, get_TrxName())) {
                    return false;
                }

                // update line

                DB.executeUpdate( " UPDATE C_OrderLine SET QtyReserved = " + ol_QtyReserved.subtract(ol_QtyTransferred).add(difference) + " WHERE C_OrderLine_ID = " + anOrderLineID, get_TrxName());
            }
        }    // reverse inventory

        return true;
    }    // reserveStock

    
    /***********************************************************************************
     * Mejoras de performance y refactoring de lógica de documentos.
     * Autor: Ader Javier - Cognitiva Consultores (Equipo de Desarrollo de Libertya)
     * Principales metodos/secciones:
     * -deleteQtyOrderedCeroIfNeeded(): elimina con a lo sumo un acceso a DB todas
     * las lineas con cantidad oredenadas cero
     * -renumberLinesIfNeeded(): reenumera las lineas (10,20,30...) con a lo sumo un acceso
     * a DB
     * -reserveStockII( MDocType dt ) : reserva stock y actualiza las lineas con a lo
     * sumo 3 accesos a DB (sin contar con los accesos utilizados por MStorage en si mismo)
     * 
     * Todas estos metodos utilizan como punto de partida las MOrderLine's (pontencialmente
     * cacheadas) via getLines(), y requieren condiciones minimas sobre las mismas. Ver comentarios.
     * Tambien, como acceden directamente a la DB sin pasar por MOrderLine, las lineas
     * cacheadas (m_lines) van a diferir con respecto a la DB (aunque en el caso de la reenumeración
     * o eliminación de lineas, o seteo de OrgID y WhID, casi nunca se genera la discrepacia en
     * escenarios normales) pero solo en pocos campos (QtyReserved, AD_Org_ID, M_Warehouse_ID, Line). 
     * Estos campos en general no son de mayor importancia en pasos posteriores de la logica de documentos
     * asi que no deberia ser necesario, en muchos casos, releer las lineas desde DB. Ver métodos
     * en particualar para detalles.
     * -sección de cache de productos : getProductFromCache, initCacheProductFromLines, etc 
     */
    //TODO: siguientes flags no estan siendo seteados a false en caso de una reelectura
    //de m_lines. Por lo pronto, esto no es necesario (en realidad, no son 
    //acutualmente consultados), pero tal vez sí a futuro.
    
    /** flag seteado a false o true por el método renumberLinesIfNeeded */
    private boolean m_needReloadForRenumber = false;
    /** flag seteado a false o true por el método enforceWHandOrgIfNeeded(...) */
    private boolean m_needReloadForEnforceWHandOrg = false;
    /** flag setead a false o true por el método updateQtyReservedIfNeeded */
    private boolean m_needReloadForQtyReservedUpdated = false;
    /** si m_needReloadForQtyReservedUpdated = true, contiene la data usada la ultima ve
     * que se invococo al metodo  updateQtyReservedIfNeeded; con estos datos se puede
     * inferir las discrepancia, si la logica lo requiere, entre las cacheadas actualmente
     * y la DB.
     */
    private DataReserveStock[] m_dataUsedInQtyReservedUpdated = null;

    /** cache de productos; puede ser seteada externamente en caso de escenarios multi-documentos */
    private MProductCache m_prodCache;
    
    /**
     * Dadas las lines obtenidas via getLines() , elimina, si es necesario
     * aquellas que tienen qtyReserved 0 o menor. Si no hay ninguna NO se accede
     * a DB.<br> 
     * Si hay , se eliminan (con solo acceso a DB, NO usando PO.delete()); en 
     * este ultimo caso, se actuliza la variable interna m_lines con la lista de aquellas
     * que permanecen. Esto es despues de llamar a este metodo, no deberia ser necesario
     * recargarlas. En caso de encontrar algun error se retorna false<br>
     * NOTA: despues de esta eliminación es posible que la lineas requieran reenumeración
     * @return false si ocurre algun error (esto pasa si el DELETE no elimina correctamente a las lineas)
     */
    private boolean deleteQtyOrderedCeroIfNeeded()
    {
    	// No se eliminan las líneas cuando el pedido ya fue procesado
    	if(!DOCSTATUS_Drafted.equals(getDocStatus())){
    		return true;
    	}
    	MOrderLine[] lines = getLines();
    	List<Integer> idsToRemove = new ArrayList<Integer>();
    	List<MOrderLine> linesNotCero = new ArrayList<MOrderLine>();
    	for (int i =0; i< lines.length ;i++)
    	{
    		MOrderLine ol = lines[i];
    		if (ol.getQtyOrdered().compareTo(BigDecimal.ZERO)<= 0 && !ol.getProduct().getProductType().equals("E"))
    			idsToRemove.add(ol.getC_OrderLine_ID());
    		else
    			linesNotCero.add(ol);
    	}
    	if (idsToRemove.size() <= 0)
    	{	linesNotCero.clear();
    		return true; //OK, ninguna para eliminar, no se hace nada
    	}
    	String queryDelete = "DELETE FROM C_OrderLine WHERE C_OrderLine_ID";
    	if (idsToRemove.size() == 1)
    		queryDelete = queryDelete + " = " + idsToRemove.get(0).toString();
    	else
    		queryDelete = queryDelete  + " IN " + StringUtil.implodeForUnion(idsToRemove);

    	int qtyDeleted = DB.executeUpdate(queryDelete, get_TrxName());
    	
    	if (qtyDeleted != idsToRemove.size())
    		return false; //NO deberia pasar
    	
    	MOrderLine[] newLines = new MOrderLine[linesNotCero.size()];
    	linesNotCero.toArray(newLines);
    	this.m_lines = newLines;
    	return true;
       
    }
    /**
     * Dadas las lineas obtenidas via getLines() las reenumera (campo Line) si es necesario.
     * Es necesario si el conjunto de lineas no se pueden ordernar en una
     * secuencia no creciente de la forma (10,20,30....), sin repetición. En caso
     * de ser necesario lo reenumera generando UN solo acceso a DB; depues de esto
     * los compos Line en la lineas (m_lines) van a diferir de lo dado en la DB, PERO
     * SOLO este campo. En general, si la logica posterior no require que estos
     * campos esten actualizados, no se deberia ser necesario la relectura de las lineas (y aún
     * en este caso es posible inferir el order en base de datos sin accederla).<br>
     * Como efecto colateral setea a false o true el campo m_needReloadForRenumber, si es
     * que esta modificación se llevo a cabo.
     * @return false en caso de error al ejecutra (si es necesario) el UPDATE sobre la base
     * de datos.
     */
    private boolean renumberLinesIfNeeded()
    {
    	this.m_needReloadForRenumber = false;
    	MOrderLine[] lines = getLines();
    	if (lines.length <= 0)
    		return true;
    	//0-> 10, 1-> 20... Si este arreglo termina con todos true, es porque NO 
    	//es necesario la reenumeración. Java inicializa a false.
    	boolean[] numLines = new boolean[lines.length];
  
    	for (int i = 0; i < lines.length; i++)
    	{
    		int numLine = lines[i].getLine();
    		if (numLine <= 0) //tiene que ser mayor que cero
    			continue;
    		if ((numLine % 10) != 0) //tiene que ser multiplo de 10
    			continue;
    		int indexInNumLines = (numLine/10) -1; //10 -> 0, 20 -> 1, 130 -> 12
    		//tiene que estar dentro de los limites 0.. numLines.length-1; el <0 no 
    		//es realemente necesario, pero por las dudas
    		if (indexInNumLines <0 || indexInNumLines >=numLines.length )
    			continue;
     		numLines[indexInNumLines] = true;
    	}

    	boolean isNeeded = false;
    	for (int i=0; i < numLines.length; i++)
    	{
    		if (numLines[i]) //OK
    			continue;
    		//else
    		isNeeded = true; //al menos un numLines == false
    		break;
    	}
    	if (!isNeeded)
    		return true; //OK, nada que reeenumerar
    	
    	//elimina los huecos y/o Line iguales y/o Lines no convencionales (17 por ej); 
    	//Los numeros de de linea resultante son SIEMPRE distintos
    	//y dentro de la secuencia (10,20,30... ). Respeta el orden actual por Line, 
    	//pero si hay dos C_OrderLine con  igual Line queda con valor menor la que tiene
    	//menor C_OrderLine_ID (esto es, la que fue creada antes, toma valor menor).
    	//esta setencia acutaliza todas las lineas, aún cuando tomen el mismo valor
    	//de Line que tenian antes (posiblemente se pueda hacer de otra manera, pero
    	//en general otras formas dan mejoras solo en ciertos casos).
    	//TODO: esta sentencia posiblemente sea relativamente costosa en tiempo
    	//de procesamientos; tal vez se pueda mejorar usando la forma UPDATE "UPDATE... FROM..."
    	//NOTA: el select interno cuenta la cantidad de lineas que tiene numero de linea menor
    	//(ANTES del update; si este no fuera el caso, PostGreSQL tieen un bug "conceptual")
    	//o tiene el mismo número de Line pero menor C_OrderLine_ID. A esta cantidad le suma 1
    	//y lo multiplica por 10; de esta manera se genera SIEMPRE valores postivos, distintos, múltiplos
    	//de 10, comenzando en 10, y saltando de 10 en 10.
    	String queryUpdate = 
    		"UPDATE C_OrderLine OL1 SET  " +
    		" Line = " +
    		"(SELECT 10 * (COUNT(1) + 1) FROM C_OrderLine OL2 " +
    		"  WHERE " + 
    		"    OL2.C_Order_ID = OL1.C_Order_ID AND " +
    		"    (OL2.Line < OL1.Line " +
    		"	 OR " +
    		"	 (OL2.Line = OL1.Line AND OL2.C_OrderLine_ID < OL1.C_OrderLine_ID) " +
    		"	 ) " +
    		") " +
    		"WHERE OL1.C_Order_ID = " + getC_Order_ID();
    	
    	int qtyUpdated = DB.executeUpdate(queryUpdate, get_TrxName());
    	this.m_needReloadForRenumber = true;
    	
    	if (qtyUpdated != lines.length)
    		return false; //por alguna razon no se actualizaron las Lines (o no se actualizaron todas)
    	
    	return true;
    }
    
    /**
     * Algoritmo de reservación de stock que es equivalente al original (salvo por bugs en 
     * este último), a partir de las lineas obtenidas via el metodo getLines(). Por si mismo
     * genera solo 2 accesos a DB (potencialmente 3 si se requiere que las lineas y la cabecera
     * tengan igual id Org e Id Wh que la cabecera y esto no se cumple); 
     * a esto hay que sumarles los N  necesarios para actualizar M_Storage via MStorage.add(....), 
     * y otros N potenciales via MStorage.getM_Locator_ID si las lienas tienen seteado conjuntos
     * de atributos).<br>
     * El algoritmo anterior generaba N*3 y potencialmente N*4 accesos a DB por si mismo (a
     * estos hay que agregarles los necesarios para actualizar el stock en si mismo). Esto
     * es, sin contar los accesos de modificación de stock, se paso de un algortimo
     * con cantidad de accesos lineal con respecto a la cantidad de lineas N, a uno
     * con cantiad de accesos constante.<br>
     * NOTA: este algoritmo requiere que las MOrderLines obtenidas via getLines() (y por lo
     * tanto esten pontencialmente cacheadas) correspondan en número con las lineas en DB y
     * en los campos usados por la clase DataReservedStock. Esto significa en particular
     * que no se require relleer las lineas despues de haber invocado a metodos como
     * deleteQtyOrderedCeroIfNeeded() (ya que este metodo deja en m_lines la lineas
     * no eliminadas) o renumberLinesIfNeeded() (ya que este metodo solo modica los
     * campos Lines de C_OrderLine, dato que no tiene importancia para la reservación
     * de stock). <br>
     * Finalmente, luego de invocar este método, las lineas obtenidas via getLines()
     * pueden diferir con respecto a los datos en DB SOLO en los campos QtyOrdered,
     * AD_Org_ID y M_Warehouese_ID (esto dos últimos menos problable). Estos son
     * los únicos campos de las lineas que son modificados por este metodo accediendo
     * directamente a la BD.
     * De cualquier manera, las discrepacia, si es que existe, puede ser inferida
     * posteriormente ( ver enforceWHandOrgIfNeeded() y updateQtyReservedIfNeeded(....))
     *  
     * @param dt
     * @return false si ocurre algún error al intentar reservar stock o actualizar las lineas
     * para que reflejen tal reservación.
     */
    public boolean reserveStockII( MDocType dt ) 
    {
    	boolean ok;
        if( dt == null ) {
            dt = MDocType.get( getCtx(),getC_DocType_ID());
        }

        // Binding
        boolean binding = !dt.isProposal();
        // Not binding - i.e. Target=0
        if( //Voiding
        	DOCACTION_Void.equals( getDocAction())
        	//OR Closing Binding Quotation
        	|| 
        	( MDocType.DOCSUBTYPESO_Quotation.equals( dt.getDocSubTypeSO()) 
        			&& DOCACTION_Close.equals( getDocAction())
        	)
        	//OR isDropShip
        	|| isDropShip()
        	) 
        {
            binding = false;
        }

        boolean isSOTrx = isSOTrx();

        log.fine( "Binding=" + binding + " - IsSOTrx=" + isSOTrx );
        // Force same WH for all but SO/PO
        int header_M_Warehouse_ID = getM_Warehouse_ID();

        if( MDocType.DOCSUBTYPESO_StandardOrder.equals( dt.getDocSubTypeSO()) 
          || MDocType.DOCBASETYPE_PurchaseOrder.equals( dt.getDocBaseType())) 
        {
            header_M_Warehouse_ID = 0;    // don't enforce
        }

        // Always check and (un) Reserve Inventory

        boolean needEnforceWHandOrg = header_M_Warehouse_ID != 0;
        MOrderLine[] lines = getLines();
        DataReserveStock[] data = 
        	getDataForReserveStock(
        		lines, 
        		needEnforceWHandOrg, 
        		getAD_Org_ID(), 
        		getM_Warehouse_ID());
        
        if (data == null)
        {
        	return false; //TODO: saveError en log?
        }
        
        for (int i = 0; i < data.length; i++)
        {
        	DataReserveStock item = data[i];
        	if (item.M_Product_ID <=0)
        		continue;
        	
            BigDecimal target     = binding
            						?item.QtyOrdered
            						:Env.ZERO;
			BigDecimal difference = target.subtract(item.QtyReserved).subtract(
					item.QtyDelivered.add(item.QtyTransferred));

            if( difference.compareTo( Env.ZERO ) == 0 ) 
            	continue;
            
            //saleo actulizacion de almacen si el producto asociao a la linea lleva stock
            if (!item.IsStocked)
            	continue;
            //EN ESTE PUNTO NO SE FUERZA Org y WH en Lineas, se hace mas tarde Y SOLO si es requerido
            
            //Pedidos de Cliente (IsSoTrx) solo afecta M_Storage.qtyReserved (se "reservan" para el pedido)
            //Pedidos a Proveedor (!IsSoTrx) solo afecta M_Storage.qtyOrdered
            BigDecimal ordered      = isSOTrx ?Env.ZERO         :difference;
            BigDecimal reserved     = isSOTrx ?difference    :Env.ZERO;
            int        M_Locator_ID = 0;

            // Get Locator to reserve
            if( item.M_AttributeSetInstance_ID != 0 ) 
            {    // Get existing Location
                M_Locator_ID = MStorage.getM_Locator_ID( 
                		item.M_Warehouse_ID, //esto ademas corrige el bug potencial en codigo viejo
                		item.M_Product_ID,
                		item.M_AttributeSetInstance_ID,
                		ordered, //FIXME? porque le pasa ordered?
                		get_TrxName());
            }

            // Get default Location TODO: nunca se mira el locator del producto????
            if( M_Locator_ID == 0 ) {
                MWarehouse wh = MWarehouse.get( getCtx(),item.M_Warehouse_ID);
                M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
            }
            
            // Update Storage
             ok = MStorage.add( getCtx(),
            		item.M_Warehouse_ID,M_Locator_ID,
            		item.M_Product_ID,
            		item.M_AttributeSetInstance_ID,
            		item.M_AttributeSetInstance_ID,
            		Env.ZERO, //diffQtyOnHand - cantidad onHand queda igual
            		reserved, //diffQtyReserved - cantidad reserveda: solo si es IsSoTrx
            		ordered,  //diffQtyOrdered - cantidad ordenada: sol si no es IsSoTrx 
            		get_TrxName());
            if (!ok)
            {
            	//TODO save error en Log?
                return false;
            }
            
            // NO update line ahora; solo se registra la diferencia que se tiene que aplicar
            item.differenceAppliedInStock = difference;

        }
        //Recien aca  se  fuerza Org. y WH en lineas  SOLO SI ES NECESARIO
        if (needEnforceWHandOrg)
        {
        	ok = enforceWHandOrgIfNeeded();
        	if (!ok)
        	{
        		//TODO save error en log?
        		return false;  
        	}
        }
        
        //recien aca  ser actualiza las cantidades reservdads Y SOLO
        //si es necesario
        ok = updateQtyReservedIfNeeded(data);
        if (!ok)
        {
        	//TODO save error en log?
        	return false;
        }
        //OK, en este punto esta todo ok, las lineas actualmente cacheadas
        //puede diferir solo en OrgID , WhId,  QtyReserved (y estos campos
        //se pueden inferir de flags y datos interno). Si la logica
        //posterior requiere que estos campos esten actualizas con respecto
        //a la DB, pueden reelear las lineas , si asi lo desea.
        return true;
    }    // reserveStock

    /**
     * Obtiene información (con a lo sumo UN SOLO acceso a DB) requerida por el algoritmo
     * de reservación de stock , para un conjunto dado de lineas.
     * 
     * @param lines MOrderLines a partir de la que se infiere la información
     * @param needEnforceWHandOrg si es false, los DataReserveStock's resultantes van a tener los
     * ids AD_Org_ID y M_Warehouse_ID tomados desde las lineas; si true, tomados de los sig. parámetros
     * @param AD_Org_ID id de Org. a usar si needEnforceWHandOrg es true
     * @param M_Warehouse_ID id de WH. a usar si needEnforceWHandOrg es true
     * @return retorna el arreglo (con un DataReservedStock por cada linea) o null si hay un error
     */
    private DataReserveStock[] getDataForReserveStock(MOrderLine[] lines,
    		boolean needEnforceWHandOrg ,int AD_Org_ID, int M_Warehouse_ID)
    {
    	
    	//DataReserveStock[] data = new DataReserveStock[lines.length];
    	ArrayList<DataReserveStock> data = new ArrayList<MOrder.DataReserveStock>();
    	List<Integer> listIdProducts = new ArrayList<Integer>();
    	for (int i = 0; i < lines.length; i++)
    	{
			// FB - No reservar stock de artículos retirados por TPV que emite
			// el remito inmediatamente después. El TPV solo setea el
			// CheckoutPlace a PointOfSale si emite remito y además el artículo
			// se retira por TPV. Si el TPV no se encarga de emitir el remito
			// entonces setea que se retira por Almacén (lo cual es lógico)
    		MOrderLine ol = lines[i];
			if (!isForceReserveStock() 
					&& ol.getCheckoutPlace() != null
					&& MProduct.CHECKOUTPLACE_PointOfSale.equals(ol
							.getCheckoutPlace())) { 
    			continue;
    		}
    		
    		DataReserveStock item = new DataReserveStock();
    		//todos los datos posibles se obtienen directamente desde la linea
    		item.C_OrderLine_ID = ol.getC_OrderLine_ID();
    		item.M_Warehouse_ID = ol.getM_Warehouse_ID();
    		item.AD_Org_ID = ol.getAD_Org_ID();
    		item.M_Product_ID = ol.getM_Product_ID();
    		item.M_AttributeSetInstance_ID = ol.getM_AttributeSetInstance_ID();
    		item.QtyOrdered = ol.getQtyOrdered();
    		item.QtyReserved = ol.getQtyReserved();
    		item.QtyDelivered = ol.getQtyDelivered();
    		item.QtyTransferred = ol.getQtyTransferred();
    		item.IsStocked = false; //por defecto
    		item.M_Product_IsStocked = false;
    		item.M_Product_ProductType = "";
    		//warehouse y org forzado, no se tiene en cuenta los ids de estos campos
    		//en las lineas si no que se toman de los parametros (de paso, solucion
    		// a bug en codigo anterior)
    		if (needEnforceWHandOrg)
    		{
    			item.AD_Org_ID = AD_Org_ID;
    			item.M_Warehouse_ID = M_Warehouse_ID;
    		}
    		//en la lista de id de productos solo hay que saltear ids 0 y repetidos
    		if (item.M_Product_ID > 0 && !listIdProducts.contains(item.M_Product_ID))
    		{
    			listIdProducts.add(item.M_Product_ID);
    			
    		}
    		//finalmente se agrega al arreglo
    		//data[i] = item;
    		data.add(item);
    	}
    	
    	//OK, en este punto tenemos seteados todos los campos SALVO datos relativos al producto
    	//y su relación con el stock

    	//caso simple (NO deberia pasar en general, salvo un pedido que tenga lineas 
    	//que NO son productos)
		// FB - Ahora esta condición se puede dar también si todos los artículos
		// son retirados por TPV (no hay que reservar la mercadería de ninguno).
    	if (listIdProducts.size()<= 0)
    	{
    		return data.toArray(new DataReserveStock[0]);
    	}
    	
    	//generacion de query con datos necesarios de los productos ; en este
    	//caso SOLO se requiere isStocked y productType
    	String querySelectInfoProduct = 
    		"SELECT M_Product_ID,IsStocked,ProductType FROM M_Product WHERE M_Product_ID ";
    	if (listIdProducts.size() == 1)
    		querySelectInfoProduct = querySelectInfoProduct + " = " + listIdProducts.get(0);
    	else
    		querySelectInfoProduct = querySelectInfoProduct + " IN " 
    			+ StringUtil.implodeForUnion(listIdProducts);
    	
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try
		{
			int M_Product_ID;
			boolean IsStocked = false;
			String ProductType = "";
			pstmt = DB.prepareStatement(querySelectInfoProduct,get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				M_Product_ID = rs.getInt(1);
				if (M_Product_ID <=0)
					continue; //no deberia pasar....
				IsStocked = "Y".equals(rs.getString(2));
				ProductType = rs.getString(3);
				//actualiza la info de producto en los DataReserveStock correspondintes
				//no es lo mas eficiente, pero bueno
				for (DataReserveStock item : data)
				{
					//DataReserveStock item = data[i];
					if (item.M_Product_ID != M_Product_ID)
						continue;
					item.M_Product_IsStocked = IsStocked;
					item.M_Product_ProductType = ProductType;
					//calculado tal como lo hace MProduct.isStocked()
					//se mantiene stock si el producto es un item (vs por ej, un servicio)
					//y ademas esta marcado explicitamente como isStocked
					if (item.M_Product_IsStocked &&
						(MProduct.PRODUCTTYPE_Item.equals(item.M_Product_ProductType) ||
								MProduct.PRODUCTTYPE_Assets.equals(item.M_Product_ProductType))
					)
					{
						item.IsStocked = true;
					}
				}
			}
			rs.close();
			pstmt.close();
		}catch(Exception ex) 
		{
		  log.log(Level.SEVERE, "MOrder.getDataForReserveStock ", ex);
		  return null;
		}finally
		{
			try{
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				rs = null; pstmt = null;
			}catch(Exception ex2){}
		}
		
		
    	return data.toArray(new DataReserveStock[0]);
    }
    
    /**
     * Dadas las lineas obtenidas via getLines(), verifica si hay alguna con ID Org
     * o ID WH distinta a la actual MOrder. Si hay al menos una, actualiza
     * TODAS (por simplicidad) las lineas en DB seteandole el WhID y el OrgID de la MOrder
     * actual (y en este caso setea el flag m_needReloadForEnforceWHandOrg a true), si no, no hace 
     * nada (y setea el flag m_needReloadForEnforceWHandOrg a false). Si
     * la actualización se realiza, las lineas actualemente cacheadas (m_lines)
     * SOLO puede diferir en el OrgID y el WhID con respecto a la DB, y en tal caso
     * el valor en la DB puede inferirse simplemente mirando el OrgID y el WhID
     * de la actual MOrder.<br> 
     * Este metodo genera, a lo sumo, un solo acceso a DB.
     * 
     * @return false si ocurre algún error de acceso a DB
     */
    private boolean enforceWHandOrgIfNeeded()
    {
    	this.m_needReloadForEnforceWHandOrg = false;
    	MOrderLine[] lines = getLines();
    	if (lines.length <=0)
    		return true;
    	int OrgID = getAD_Org_ID();
    	int WhID = getM_Warehouse_ID();
    	
    	//simplicando el algoritmo; si hay al menos una discrepacia se actulizan
    	//todas las lineas. El caso normal (el más habitual) es que no se requiera
    	//ningun update, ya que las lineas casi siempre son creadas con el Org y
    	//la WH de la cabecera; asi que no merece mayor esfuerzo.
    	boolean needUpdate = false;
    	for (int i=0; i<lines.length; i++)
    	{
    		MOrderLine ol = lines[i];
    		if (ol.getAD_Org_ID()!= OrgID || ol.getM_Warehouse_ID()!= WhID)
    		{
    			needUpdate = true;
    		    break;
    		}
    	}
    	if (! needUpdate)
    		return true; //OK, update salteado
  
    	//ok, se tiene que actualizar (se setea el flag)
    	this.m_needReloadForEnforceWHandOrg = true;
        //se actualizan TODAS, esto retorna todas la lineas actulizadas (aun cuando no cambien el valor)
       	int qtyUpdated = 
       		DB.executeUpdate(
       			" UPDATE C_OrderLine " +
				" SET M_Warehouse_ID = " + WhID + ", AD_Org_ID = " + OrgID +  
				" WHERE C_Order_ID = " + getC_Order_ID(), get_TrxName());
    	
       	if (qtyUpdated != lines.length)
       		return false; //algo raro paso
    	//OK
    	return true;
    }
    /**
     * A partir de los datos dados en un arreglo DataReserveStock (generados
     * por el algoritmo de reservación de estock, en particular el 
     * campo DataReserveStock.differenceAppliedInStock y DataReserveStock.QtyReserved) actualiza
     * las QtyReserved en base de datos (el valor seteado va a ser la suma de los
     * dos campos anteiores). Esta acutalización solo se hace en caso de ser necesario
     * (un DataReserveStock con diferencia distitta de cero), y en este caso se genera
     * un solo acceso a DB para actulizar todas las lineas que lo requieran; tambien
     * bajo esta ultima situción se setea el flag m_needReloadForQtyReservedUpdated
     * a true y el parametro data se registra en m_dataUsedInQtyReservedUpdated
     * por si logica posterior requiere saber que modicaciones se realizaron.
     * @param data
     * @return false si se genera algún erorr al acutalizar las lineas en DB.
     */
    private boolean updateQtyReservedIfNeeded(DataReserveStock[] data)
    {
    	this.m_needReloadForQtyReservedUpdated = false;
    	
    	StringBuffer sb = new StringBuffer();
    	List<Integer> listIds = new ArrayList<Integer>();
    	
    	sb.append("UPDATE C_OrderLine SET QtyReserved  = ( ");
    	sb.append(" CASE C_OrderLine_ID ");
    	for (int i = 0; i < data.length ; i ++)
    	{
    		DataReserveStock item = data[i];
			if (item.differenceAppliedInStock.compareTo(BigDecimal.ZERO) == 0
					&& item.QtyTransferred.compareTo(BigDecimal.ZERO) == 0)
    			continue;
			BigDecimal newQtyReserved = item.QtyReserved.add(
					item.differenceAppliedInStock).add(item.QtyTransferred);
    		int C_OrderLine_ID = item.C_OrderLine_ID;
    		
    		listIds.add(C_OrderLine_ID);
    		
    		sb.append(" WHEN ").append(C_OrderLine_ID).append(" THEN ").append(newQtyReserved);
    	}
    	
    	sb.append(" END ) WHERE C_OrderLine_ID IN ");
    	if (listIds.size() <= 0) //no hay ninguna c_orderLine que actualizar
    		return true;
    	
    	sb.append(StringUtil.implodeForUnion(listIds)); //(12,2,4,89)
    	String queryUpdate = sb.toString();
    	this.m_needReloadForQtyReservedUpdated = true;
    	this.m_dataUsedInQtyReservedUpdated = data;
    	
      	int qtyUpdated = 
       		DB.executeUpdate(queryUpdate,get_TrxName());
      	
      	if (qtyUpdated != listIds.size())
      		return false; //algo raro paso...
      	
    	return true;
    }
    /**
     * Clase auxiliar para el algoritmo de reservación de stock
     * @author Ader Javier
     *
     */
    private static class DataReserveStock
    {
    	int C_OrderLine_ID;
    	int M_Warehouse_ID;
    	int AD_Org_ID;
    	//int Line; no es realmente requerida, y si m_needReloadForRenumber va mostra una info incorrecta
    	int M_Product_ID;
    	int M_AttributeSetInstance_ID;
    	BigDecimal QtyOrdered;
    	BigDecimal QtyReserved;
    	BigDecimal QtyDelivered;
    	BigDecimal QtyTransferred;
    	/** determina si lleva stock: calculado a partir de M_Product_IsStocket y M_Product_ProductType */
    	boolean IsStocked; //campo calculado a partir de los siguientes
    	boolean M_Product_IsStocked;
    	String M_Product_ProductType = "";
    	/** este campo es calculado por el algoritmo de reservación de stock y representa
    	 * la "diferencia" aplicada. Si es cero, esta item no genero modificación de stock.
    	 * Valor por defecto cero.
    	 */
    	BigDecimal differenceAppliedInStock = BigDecimal.ZERO;
    }
    
    /**************** Manejo de cache de productos  *****************************************/
    /**
     * Metodo usado por logica de procesamientos multi-documento, para setear externamente
     * la cache de productos (asi evitar que la logica de procesamiento la vuelva a generar).
     * 
     * @param cache
     */
    public void setProductCache(MProductCache cache)
    {
    	m_prodCache = cache;
    }
    
    public MProductCache getProductCache()
    {
    	return m_prodCache;
    }
    /**
     * Obtiene de la cache de productos el asociado a M_Product_ID. Puede generar
     * un accesos a DB indirectamente, si el producto no esta actulamente cacheado.
     * <b> Retorna NULL si tal producto no se puede obtener; en general NO debe pasar
     * esto, ya que signifca que tal producto no existe en la DB </b>
     * @param M_Product_ID
     * @return producto o null
     */
    private MProduct getProductFromCache(int M_Product_ID)
    {
    	if (m_prodCache == null)
     		m_prodCache = new MProductCache(getCtx(),get_TrxName());
    	if (M_Product_ID <=0)
    		return null;
    	MProduct p = m_prodCache.get(M_Product_ID);
    	return p;
    }
    
    /**
     * Carga en la cache de productos aquellos en la lineas (lines) que no se
     * encuentran en la misma. Un solo acceso a DB como máximo y solo si hay al menos un producto
     * no actualmete cacheado.
     * 
     * @param lines MOrderLine's a partir de la cual obtener los ids de los productos a cargar
     * @return false si al menos un producto no se pudo cargar en cache (en genral no deberia pasar)
     */
    private boolean initCacheProdFromLines(MOrderLine[] lines)
    {
    	if (m_prodCache == null)
    		m_prodCache = new MProductCache(getCtx(),get_TrxName());
    	
    	List<Integer> newIds = new ArrayList<Integer>();
    	
    	for (int i = 0; i < lines.length; i++)
    	{
    		MOrderLine ol = lines[i];
    		int M_Product_ID = ol.getM_Product_ID();
    		if (M_Product_ID <= 0)
    			continue;
    		if (m_prodCache.contains(M_Product_ID))
    			continue;
    		if (newIds.contains(M_Product_ID))
    		    continue;
    		newIds.add(M_Product_ID);
    	}
    	
    	if (newIds.size() <= 0)
    		return true; //nada para cargar, todos ya cacheados
    	
    	//carga masiva en cache; un solo acceso a DB
    	int qtyCached = m_prodCache.loadMasive(newIds);
    	
    	if (qtyCached != newIds.size())
    		return false; //algunos no se cargaron....
    	
    	return true;
    }
    
    /****************** Otras mejoras Mejoras ***************************************/
    
    /**
     * Retorna la cantidad de lineas accediendola desde base de datos.
     * Un solo acceso a DB
     */
    private int getQtyLinesFromDB()
    {
    	int id = getC_Order_ID();
    	if (id <= 0)
    		return 0;
    	int qtyLines = DB.getSQLValue(get_TrxName(),
    	 "SELECT COUNT(1) FROM C_OrderLine WHERE C_Order_ID = " + id);
    	return qtyLines;
    }
    
    /************** FIN de Mejoras de performance y refactoring de lógica de documentos          */
    
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean calculateTaxTotal() {
        log.fine( "" );

        // Delete Taxes

		// FIXME Se agregó una condición para que si la tasa es de percepción que
		// no la elimine ya que se agrega manualmente en el TPV, sacar esa
		// condición cuando se pase la lógica de percepciones también a pedidos 
		DB.executeUpdate(
				"DELETE FROM C_OrderTax WHERE C_Order_ID="
						+ getC_Order_ID()
						+ " AND c_tax_id NOT IN (SELECT c_tax_id FROM c_tax WHERE ispercepcion = 'Y')",
				get_TrxName());
        m_taxes = null;

        // Lines

        BigDecimal   totalLines = Env.ZERO;
        ArrayList    taxList    = new ArrayList();
        MOrderLine[] lines      = getLines(true);

        for( int i = 0;i < lines.length;i++ ) {
            MOrderLine line  = lines[ i ];
            Integer    taxID = new Integer( line.getC_Tax_ID());

            if( !taxList.contains( taxID )) {
                MOrderTax oTax = MOrderTax.get( line,getPrecision(),false,get_TrxName());    // current Tax

                oTax.setIsTaxIncluded( isTaxIncluded());

                if( !oTax.calculateTaxFromLines()) {
                    return false;
                }

                if( !oTax.save( get_TrxName())) {
                    return false;
                }

                taxList.add( taxID );
            }

            totalLines = totalLines.add( line.getLineNetAmt());
        }

        // Taxes

        BigDecimal  grandTotal = totalLines;
        MOrderTax[] taxes      = getTaxes( true );

        for( int i = 0;i < taxes.length;i++ ) {
            MOrderTax oTax = taxes[ i ];
            MTax      tax  = oTax.getTax();

            if( tax.isSummary()) {
                MTax[] cTaxes = tax.getChildTaxes( false );

                for( int j = 0;j < cTaxes.length;j++ ) {
                    MTax       cTax   = cTaxes[ j ];
                    BigDecimal taxAmt = cTax.calculateTax( oTax.getTaxBaseAmt(),isTaxIncluded(),getPrecision());

                    MOrderTax newOTax = null;
                    
                    // Si el tax hijo ya está insertado en el pedido, se suman las cantidades
                    if (taxList.contains(cTax.getC_Tax_ID())) {
                    	for (int k = 0; k < taxes.length && newOTax == null; k++) {
                    		if(taxes[k].getC_Tax_ID() == cTax.getC_Tax_ID())
                    			newOTax = taxes[k]; 
						}
                    	
                    	newOTax.setTaxBaseAmt(newOTax.getTaxBaseAmt().add(oTax.getTaxBaseAmt()));
                    	newOTax.setTaxAmt(newOTax.getTaxAmt().add(taxAmt));
                    	
                    // Sino, se crea un nuevo tax para el pedido.
                    } else {

	                    newOTax = new MOrderTax( getCtx(),0,get_TrxName());
	
	                    newOTax.setClientOrg( this );
	                    newOTax.setC_Order_ID( getC_Order_ID());
	                    newOTax.setC_Tax_ID( cTax.getC_Tax_ID());
	                    newOTax.setPrecision( getPrecision());
	                    newOTax.setIsTaxIncluded( isTaxIncluded());
	                    newOTax.setTaxBaseAmt( oTax.getTaxBaseAmt());
	                    newOTax.setTaxAmt( taxAmt );	                    
                    }
                    
                    if( !newOTax.save( get_TrxName())) {
                        return false;
                    }
                }

                if( !oTax.delete( true,get_TrxName())) {
                    return false;
                }
            }
        }
        
        // Recalculo el total a partir de los impuestos del pedido.
        taxes = getTaxes(true);
        for (MOrderTax orderTax : taxes) {
			if(!isTaxIncluded())
				grandTotal = grandTotal.add( orderTax.getTaxAmt() ); 
		}

        // Recalculo el total a partir del importe del cargo
        grandTotal = grandTotal.add(getChargeAmt());
        
        setTotalLines( totalLines );
        setGrandTotal( grandTotal );

        return true;
    }    // calculateTaxTotal

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean approveIt() {
        log.info( "approveIt - " + toString());
        setIsApproved( true );

        return true;
    }    // approveIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean rejectIt() {
        log.info( "rejectIt - " + toString());
        setIsApproved( false );

        return true;
    }    // rejectIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public void BreakLines(int M_Order_ID){
  	   log.fine("BreakUpProductionLines - BreakLines");
  	   try {
         	StringBuffer sql,sql_line;
         	if(M_Order_ID==0)//Si no hay orden de produccion seleccionada..
         		return;
         	int max_line=10;
         	sql_line = new StringBuffer("select max(line) from c_orderline where c_order_id="+M_Order_ID);
         	PreparedStatement pstmtline=null;
         	ResultSet rsline;
         	pstmtline = DB.prepareStatement( sql_line.toString());
          rsline    = pstmtline.executeQuery();
          if(rsline.next()){
          	max_line=rsline.getInt(1);
          }
          rsline.close();
  		pstmtline.close();
  		pstmtline = null;
  		
         	sql = new StringBuffer( "SELECT c_orderline_id,qtyordered,c_bpartner_id,c_bpartner_location_id" +
         			",dateordered,datepromised,datedelivered,dateinvoiced,description,m_product_id " +
         			",m_warehouse_id,c_uom_id,m_shipper_id,c_currency_id,pricelist,priceactual,pricelimit,linenetamt" +
         			",discount,freightamt,c_charge_id,c_tax_id,s_resourceassignment_id,ref_orderline_id,m_attributesetinstance_id" +
         			",isdescription,processed,priceentered from c_orderline" +
         			" where c_order_id="+M_Order_ID );	
          PreparedStatement pstmt = null;
          ResultSet rs;
          pstmt = DB.prepareStatement( sql.toString());
          rs    = pstmt.executeQuery();
          BigDecimal quantity=null;
         
          while(rs.next()){
             	quantity=rs.getBigDecimal(2);
             	int Line =max_line+10;
             	if(quantity.compareTo(BigDecimal.ONE)==1){
             		log.fine("La línea de pedido tiene mas de un articulo,quantity="+quantity);
             		for(int i=1;i<quantity.intValue();i++){
             		MOrderLine aux = new MOrderLine(Env.getCtx(),0,null);
             		//MOrderLine aux = new MOrderLine(Env.getCtx(),rs.getInt(1),null);
             		//aux_new= MOrder.co
             	
             		aux.setC_Order_ID(M_Order_ID);
             		
             		aux.setQtyOrdered(BigDecimal.ONE);
             		aux.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
             		aux.setC_BPartner_Location_ID(rs.getInt("c_bpartner_location_id"));
             		aux.setDateOrdered(rs.getTimestamp("dateordered"));
             		aux.setDatePromised(rs.getTimestamp("datepromised"));
             		aux.setDateDelivered(rs.getTimestamp("datedelivered"));
             		aux.setDateInvoiced(rs.getTimestamp("dateinvoiced"));
             		aux.setDescription(rs.getString("description"));
             		aux.setM_Product_ID(rs.getInt("m_product_id"));
             		aux.setM_Warehouse_ID(rs.getInt("m_warehouse_id"));
             		aux.setC_UOM_ID(rs.getInt("c_uom_id"));
             		aux.setQtyReserved(BigDecimal.ONE);
             		aux.setQtyEntered(BigDecimal.ONE);
             		aux.setQtyDelivered(BigDecimal.ONE);
             		aux.setQtyInvoiced(BigDecimal.ONE);
             		aux.setM_Shipper_ID(rs.getInt("m_shipper_id"));
             		aux.setC_Currency_ID(rs.getInt("c_currency_id"));
             		aux.setPriceList(rs.getBigDecimal("pricelist"));
             		aux.setPriceActual(rs.getBigDecimal("priceactual"));
             		aux.setPriceLimit(rs.getBigDecimal("pricelimit"));
             		aux.setLineNetAmt(rs.getBigDecimal("linenetamt"));
             		aux.setDiscount(rs.getBigDecimal("discount"));
             		aux.setFreightAmt(rs.getBigDecimal("freightamt"));
             		aux.setC_Charge_ID(rs.getInt("c_charge_id"));
             		aux.setC_Tax_ID(rs.getInt("c_tax_id"));
             		aux.setS_ResourceAssignment_ID(rs.getInt("s_resourceassignment_id"));
             		aux.setRef_OrderLine_ID(rs.getInt("ref_orderline_id"));
             		aux.setM_AttributeSetInstance_ID(rs.getInt("m_attributesetinstance_id"));
             		aux.setIsDescription(rs.getBoolean("isdescription"));
             		aux.setProcessed(rs.getBoolean("processed"));
             		aux.setPriceEntered(rs.getBigDecimal("priceentered"));
             		aux.setLine(Line);
             		Line+=10;
             		aux.save();
             		}   		
             		int noLine = DB.executeUpdate( "UPDATE C_OrderLine set qtyordered="+BigDecimal.ONE+",qtyentered="+BigDecimal.ONE+",linenetamt="+rs.getBigDecimal("priceactual")+" where c_orderline_id="+rs.getInt(1) ,get_TrxName());
                    log.fine("Numero de lineas actualizadas:"+noLine);
             	}
          }
          
          rs.close();
  		pstmt.close();
  		pstmt = null;
         }catch( Exception e ) {
             log.log( Level.SEVERE,"BreakUpProductionLines - BreakLines " + e );
         }
         
  	   return;
  	   }
    
    public static boolean showRegionWarning(int bPartnerLocationID) {
    	
    	MBPartnerLocation bloc = new MBPartnerLocation(Env.getCtx(), bPartnerLocationID, null);
    	MRegion reg = MRegion.get(Env.getCtx(), bloc.getLocation(true).getC_Region_ID());
    	String msg = reg.getWarningMsg();
    	
        if (msg != null && msg.length() > 0) {
        	msg = Msg.getMsg(Env.getCtx(), msg);
        	String title = Msg.getMsg(Env.getCtx(), "Warning");
        	
        	int x = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        	
        	if (x != JOptionPane.YES_OPTION)
        		return false;
        }
        
        return true;
    }
    
    
   
    public String completeIt() {
    	
		if (!Util.isEmpty(this.getM_AuthorizationChain_ID(), true)) {
			AuthorizationChainManager authorizationChainManager = new AuthorizationChainManager(
					this, getCtx(), get_TrxName());
			try {
				if (authorizationChainManager
						.loadAuthorizationChain(reactiveOrder())) {
					m_processMsg = Msg.getMsg(getCtx(), "ExistsAuthorizationChainLink");
					this.setProcessed(true);
					return DOCSTATUS_WaitingConfirmation;
				}
			} catch (Exception e) {
				m_processMsg = e.getMessage();
				return DocAction.STATUS_Invalid;
			}
		}
    	
        MDocType dt           = MDocType.get( getCtx(),getC_DocType_ID());
        String   DocSubTypeSO = dt.getDocSubTypeSO();
        boolean isOrderTransferable = dt.getDocTypeKey().equalsIgnoreCase(
				MDocType.DOCTYPE_Pedido_Transferible);
        // Just prepare
        
        MOrderLine[] lines = getLines( false );
        
        initCacheProdFromLines(lines); //Ader: mejora de caches: TODO, falta cache para MAttributeSet
        
        for( int lineIndex = 0;lineIndex < lines.length;lineIndex++ ) {
            MOrderLine sLine   = lines[ lineIndex ];
			
            // La cantidad pedida no puede ser menor a la cantidad entregada + la
    		// cantidad transferida
			BigDecimal qtyDeliveredTransferred = sLine.getQtyDelivered().add(
					sLine.getQtyTransferred());
			if (!Util.isEmpty(qtyDeliveredTransferred, true)
					&& sLine.getQtyOrdered().compareTo(qtyDeliveredTransferred) < 0) {
				m_processMsg = "@LinesWithQtyOrderedMinorToQtyDelivered@";
            	return DocAction.STATUS_Invalid;
        	}
			
			// La cantidad pedida no puede ser menor a la cantidad facturada
			if (!Util.isEmpty(sLine.getQtyInvoiced(), true)
					&& sLine.getQtyOrdered().compareTo(sLine.getQtyInvoiced()) < 0) {
				m_processMsg = "@LinesWithQtyOrderedMinorToQtyInvoiced@";
            	return DocAction.STATUS_Invalid;
        	}
            
            //MProduct   product = sLine.getProduct(); -> depende de la cache en MProduct, que expira cada 5 min..
            MProduct   product = getProductFromCache(sLine.getM_Product_ID());
            
            //puede ser NULL
            if (product == null)
            	continue;
        	//getM_AttributeSet_ID() con valor 0 represnta NULL,... NO tiene sentido hacer un 
            //new MAttributeSet con id 0, tal como esta el codigo acutalmente eso es equivalente
            //a "crear" un nuevo MAttributeSet NO a obtener el que tiene id 0
        	if (product.getM_AttributeSet_ID() <= 0)
        		continue;
            if (product != null) { 
            	//TODO: hace cache MAttirbuteSet, inicalizarlar al principio y usar getAttributeSetFromCache
	            MAttributeSet atri = new MAttributeSet(Env.getCtx(),product.getM_AttributeSet_ID(),null);
	            
	            /*
	             * Modificar los m�todos Completeit de C_Order y M_InOut para que compruebe de nuevo que la mercanc�a 
	             * tiene conjunto de atributos, si es necesario que los lleve, antes de completar.
	             */
	            /*
	            if (sLine.getM_AttributeSetInstance_ID() == 0 && sLine.shouldSetAttrSetInstance()) {
	            	m_processMsg = "La l�nea " + sLine.getLine() + " no posee una instancia de conjunto de atributos; deber� especificarse el valor manualmente.";
	            	return DocAction.STATUS_InProgress;
	            }
	            */
	            if(atri.isInstanceAttribute()&& (sLine.getQtyEntered().compareTo(BigDecimal.ONE)==1) && atri.isOrderInstanceAttribute())
		        {
	            	log.fine("Va a entrar en la función de instancia de copiar las lineas y c_order_id=");
	            	BreakLines(this.getC_Order_ID());
	            	m_lines = null; //por las dudas, aunque a continuación se va a retornar
	            	// JOptionPane.showMessageDialog( null,"Por favor, introduzca los números de serie y complete el pedido ",null, JOptionPane.INFORMATION_MESSAGE );
	            	m_processMsg = "Por favor, introduzca los números de serie y complete el pedido ";
	            	return DocAction.STATUS_InProgress;
		        }
            }
        }
        if( DOCACTION_Prepare.equals( getDocAction())) {
            setProcessed( false );

            return DocAction.STATUS_InProgress;
        }

        // Offers
        if( MDocType.DOCSUBTYPESO_Proposal.equals( DocSubTypeSO ) || 
        		MDocType.DOCSUBTYPESO_Quotation.equals( DocSubTypeSO )) 
        {
            // Binding
            if( MDocType.DOCSUBTYPESO_Quotation.equals( DocSubTypeSO )) {
            	//int[] orderLines = PO.getAllIDs("C_OrderLine", "C_Order_ID = " + getC_Order_ID(), get_TrxName() );
                //reserveStock( dt,orderLines);
            	//Ader: Mejora II, de paso, se chequea por error
            	//tal como lo hace prepareIt (PostGreSQL rollbackea automaticamente...)
            	if (! reserveStockII(dt) )
            	{
                    m_processMsg = "@CannotReserveStock@";
                    return DocAction.STATUS_Invalid;

            	}
            }
            setProcessed( true );
            return DocAction.STATUS_Completed;
        }

        // Waiting Payment - until we have a payment

        if( !m_forceCreation && MDocType.DOCSUBTYPESO_PrepayOrder.equals( DocSubTypeSO ) && (getC_Payment_ID() == 0) && (getC_CashLine_ID() == 0) ) {
            setProcessed( true );

            return DocAction.STATUS_WaitingPayment;
        }

        // Re-Check

        if (!m_justPrepared	&& !existsJustPreparedDoc()) {
            String status = prepareIt();

            if( !DocAction.STATUS_InProgress.equals( status )) {
                return status;
            }
        }

        // Implicit Approval

        if( !isApproved()) {
            approveIt();
        }

        getLines( true,null );
        log.info( toString());

        StringBuffer info        = new StringBuffer();
        boolean      realTimePOS = false;

        // Create SO Shipment - Force Shipment

        MInOut shipment = null;

        if( MDocType.DOCSUBTYPESO_OnCreditOrder.equals( DocSubTypeSO )    // (W)illCall(I)nvoice
                || MDocType.DOCSUBTYPESO_WarehouseOrder.equals( DocSubTypeSO )    // (W)illCall(P)ickup
                || MDocType.DOCSUBTYPESO_POSOrder.equals( DocSubTypeSO )    // (W)alkIn(R)eceipt
                || MDocType.DOCSUBTYPESO_PrepayOrder.equals( DocSubTypeSO )) {
            if( !DELIVERYRULE_Force.equals( getDeliveryRule())) {
                setDeliveryRule( DELIVERYRULE_Force );
            }

            //

            shipment = createShipment( dt,realTimePOS
                                          ?null
                                          :getDateOrdered());

            if( shipment == null ) {
                return DocAction.STATUS_Invalid;
            }

            info.append( "@M_InOut_ID@: " ).append( shipment.getDocumentNo());

            String msg = shipment.getProcessMsg();

            if( (msg != null) && (msg.length() > 0) ) {
                info.append( " (" ).append( msg ).append( ")" );
            }
        }    // Shipment

        // Create SO Invoice - Always invoice complete Order

        if( MDocType.DOCSUBTYPESO_POSOrder.equals( DocSubTypeSO ) || MDocType.DOCSUBTYPESO_OnCreditOrder.equals( DocSubTypeSO ) || MDocType.DOCSUBTYPESO_PrepayOrder.equals( DocSubTypeSO )) {
            MInvoice invoice = createInvoice( dt,shipment,realTimePOS
                    ?null
                    :getDateOrdered());

            if( invoice == null ) {
                return DocAction.STATUS_Invalid;
            }

            info.append( " - @C_Invoice_ID@: " ).append( invoice.getDocumentNo());

            String msg = invoice.getProcessMsg();

            if( (msg != null) && (msg.length() > 0) ) {
                info.append( " (" ).append( msg ).append( ")" );
            }
        }    // Invoice

        // Counter Documents
        log.fine("Antes de entrar en counterDoc");
        MOrder counter = createCounterDoc();
        
        if( counter != null ) {
            info.append( " - @CounterDoc@: @Order@=" ).append( counter.getDocumentNo());
        }

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            if( info.length() > 0 ) {
                info.append( " - " );
            }

            info.append( valid );
            m_processMsg = info.toString();

            return DocAction.STATUS_Invalid;
        }
        
        
        /*
		 * Verificar, para los pedidos de clientes, que los productos tengan tildado el 
		 * campo "Vendido" (checkbox en la ventana de artículos). La semántica de ese campo 
		 * dice que es un artículo que se vende en la organización
		 */
        MDocType docTarget = MDocType.get(this.p_ctx, this.getC_DocTypeTarget_ID());
        
        // Si es un pedido de cliente
        if(docTarget.getDocTypeKey().equals(MDocType.DOCTYPE_StandarOrder)){
        	// Itero por las líneas del pedido verificando los artículos de venta
        	for (MOrderLine orderLine : lines) {
				//MProduct lineProduct = MProduct.get(this.getCtx(),orderLine.getM_Product_ID());
        		//Ader: cache Multi-docuemntos
				MProduct lineProduct = getProductFromCache(orderLine.getM_Product_ID());
				if (lineProduct == null)
					continue;
				
				// Si el producto no es de venta
				if(!lineProduct.isSold()){
					m_processMsg = Msg.getMsg(this.getCtx(), "NotSalesProduct", new Object[]{lineProduct.getName()});
					
					return DocAction.STATUS_Invalid;
				}
			}
        }

        // Para pedidos transferibles, se debe controlar que la cantidad de
		// la línea no supere el pendiente a entregar del pedido original
		// relacionado y setear la cantidad transferida a la pedida
        if (isOrderTransferable){
			int locatorID = MWarehouse.getDefaultLocatorID(getM_Warehouse_ID(),
					get_TrxName());
	    	for (MOrderLine orderLine : lines) {
				if (!Util.isEmpty(orderLine.getRef_OrderLine_ID(), true)) {
					MOrderLine refOrderLine = new MOrderLine(getCtx(),
							orderLine.getRef_OrderLine_ID(), get_TrxName());
					if (orderLine.getQtyOrdered().compareTo(
							refOrderLine.getPendingDeliveredQty()) > 0) {
						m_processMsg = "@QtyOrderedSurpassOrigLineQtyReserved@";
						return DocAction.STATUS_Invalid;
					}
					// Setear la cantidad transferida para que no se pueda remitir
					orderLine.setQtyTransferred(orderLine.getQtyOrdered());
					if(!orderLine.save()){
						m_processMsg = CLogger.retrieveErrorAsString();
						return DocAction.STATUS_Invalid;
					}
					// Setear la cantidad transferida a la línea original
					refOrderLine.setQtyTransferred(orderLine.getQtyOrdered());
					if(!refOrderLine.save()){
						m_processMsg = CLogger.retrieveErrorAsString();
						return DocAction.STATUS_Invalid;
					}
					// Desreservar el stock del pedido original si el artículo se stockea
					if (MProduct.isProductStocked(getCtx(),
							orderLine.getM_Product_ID())
							&& !MStorage.add(getCtx(), getM_Warehouse_ID(), locatorID,
									orderLine.getM_Product_ID(), 0, 0, BigDecimal.ZERO,
									orderLine.getQtyOrdered().negate(),
									BigDecimal.ZERO,
									get_TrxName())) {
						m_processMsg = "@CannotReserveStock@";
		                 return DocAction.STATUS_Invalid;
					}
		        }
	    	}
			// Crear el documento transferido a partir del transferible
			// automáticamente
	    	try{
				createTransferOrder(MDocType.getDocType(getCtx(),
						MDocType.DOCTYPE_Pedido_Transferido, get_TrxName())
						.getID());
	    	} catch(Exception e){
	    		m_processMsg = e.getMessage();
	    		return DOCSTATUS_Invalid;
	    	}
    	}

        setProcessed( true );
        m_processMsg = info.toString();

        //

        setDocAction( DOCACTION_Close );

        // Si el Tipo de Documento Base es Pedido de Cliente y el Tipo de Documento tiene seteado el 
        // check box llamado “Imprimir al Completar”, entonces se ejecuta el proceso "Nota de Pedido (Impresión)"
        if( (MDocType.DOCSUBTYPESO_StandardOrder.equals(DocSubTypeSO)) && (dt.isPrintAtCompleting()) ){ 
        	HashMap<String,Object> parameters = new HashMap<String,Object>();
            parameters.put("C_Order_ID", getID());
        	int processID = DB.getSQLValue( null, "SELECT AD_Process_ID FROM AD_Process WHERE value='" + getNotaPedidoValue()+ "' " );
            ProcessInfo pi = MProcess.execute(getCtx(), processID, parameters, get_TrxName()); 
        }
        
        return DocAction.STATUS_Completed;
    }    // completeIt
    
    private Boolean reactiveOrder() {
		return !Util.isEmpty(getOldGrandTotal(), true) && (!this.getGrandTotal().equals(this.getOldGrandTotal()));
	}

	protected String getNotaPedidoValue(){
		return "Nota de Pedido (Impresión)";
	}
    
    /**
     * Descripción de Método
     *
     *
     * @param dt
     * @param movementDate
     *
     * @return
     */

    private MInOut createShipment( MDocType dt,Timestamp movementDate ) {
        log.info( "For " + dt );

        MInOut shipment = new MInOut( this,dt.getC_DocTypeShipment_ID(),movementDate );

        // shipment.setDateAcct(getDateAcct());

        if( !shipment.save( get_TrxName())) {
            m_processMsg = "@NotCreateShipment@";

            return null;
        }

        //

        MOrderLine[] oLines = getLines( true,null );

        for( int i = 0;i < oLines.length;i++ ) {
            MOrderLine oLine = oLines[ i ];

            //

            MInOutLine ioLine = new MInOutLine( shipment );

            // Qty = Ordered - Delivered

            BigDecimal MovementQty = oLine.getQtyOrdered().subtract( oLine.getQtyDelivered());

            // Location

            int M_Locator_ID = MStorage.getM_Locator_ID( oLine.getM_Warehouse_ID(),oLine.getM_Product_ID(),oLine.getM_AttributeSetInstance_ID(),MovementQty,get_TrxName());

            if( M_Locator_ID == 0 )    // Get default Location
            {
                MWarehouse wh = MWarehouse.get( getCtx(),oLine.getM_Warehouse_ID());

                M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
            }

            //

            ioLine.setOrderLine( oLine,M_Locator_ID,MovementQty );
            ioLine.setQty( MovementQty );

            if( oLine.getQtyEntered().compareTo( oLine.getQtyOrdered()) != 0 ) {
                ioLine.setQtyEntered( MovementQty.multiply( oLine.getQtyEntered()).divide( oLine.getQtyOrdered(),BigDecimal.ROUND_HALF_UP ));
            }

            if( !ioLine.save( get_TrxName())) {
                m_processMsg = "@NotCreateShipmentLine@";

                return null;
            }
        }

        // Manually Process Shipment

        /*
        String status = shipment.completeIt();
        shipment.setDocStatus( status );
        shipment.save( get_TrxName());
         
        
        if( !DOCSTATUS_Completed.equals( status )) {
            m_processMsg = "@M_InOut_ID@: " + shipment.getProcessMsg();

            return null;
        }
		*/
        
        shipment.setDocAction(DOCACTION_Complete);
        shipment.setDocStatus(DOCSTATUS_Drafted);
        
        boolean ret = shipment.processIt(DOCACTION_Complete);
        ret = ret && shipment.save();
        
        if( !DOCSTATUS_Completed.equals( shipment.getDocStatus() )) {
            m_processMsg = "@M_InOut_ID@: " + shipment.getProcessMsg();

            return null;
        }
        
        return shipment;
    }    // createShipment

    /**
     * Descripción de Método
     *
     *
     * @param dt
     * @param shipment
     * @param invoiceDate
     *
     * @return
     */

    private MInvoice createInvoice( MDocType dt,MInOut shipment,Timestamp invoiceDate ) {
        log.info( dt.toString());

        MInvoice invoice = new MInvoice( this,dt.getC_DocTypeInvoice_ID(),invoiceDate );

        if( !invoice.save( get_TrxName())) {
            m_processMsg = "@NotCreateInvoice@";

            return null;
        }

        // If we have a Shipment - use that as a base

        if( shipment != null ) {
            if( !INVOICERULE_AfterDelivery.equals( getInvoiceRule())) {
                setInvoiceRule( INVOICERULE_AfterDelivery );
            }

            //

            MInOutLine[] sLines = shipment.getLines( false );

            for( int i = 0;i < sLines.length;i++ ) {
                MInOutLine sLine = sLines[ i ];

                //

                MInvoiceLine iLine = new MInvoiceLine( invoice );

                iLine.setShipLine( sLine );

                // Qty = Delivered

                iLine.setQtyEntered( sLine.getQtyEntered());
                iLine.setQtyInvoiced( sLine.getMovementQty());

                if( !iLine.save( get_TrxName())) {
                    m_processMsg = "@NotCreateInvoiceLineFromShipmentLine@";

                    return null;
                }

                //

                sLine.setIsInvoiced( true );

                if( !sLine.save( get_TrxName())) {
                    log.warning( "Could not update Shipment line: " + sLine );
                }
            }
        } else    // Create Invoice from Order
        {
            if( !INVOICERULE_Immediate.equals( getInvoiceRule())) {
                setInvoiceRule( INVOICERULE_Immediate );
            }

            //

            MOrderLine[] oLines = getLines();

            for( int i = 0;i < oLines.length;i++ ) {
                MOrderLine oLine = oLines[ i ];

                //

                MInvoiceLine iLine = new MInvoiceLine( invoice );

                iLine.setOrderLine( oLine );

                // Qty = Ordered - Invoiced

                iLine.setQtyInvoiced( oLine.getQtyOrdered().subtract( oLine.getQtyInvoiced()));

                if( oLine.getQtyOrdered().compareTo( oLine.getQtyEntered()) == 0 ) {
                    iLine.setQtyEntered( iLine.getQtyInvoiced());
                } else {
                    iLine.setQtyEntered( iLine.getQtyInvoiced().multiply( oLine.getQtyEntered()).divide( oLine.getQtyOrdered(),BigDecimal.ROUND_HALF_UP ));
                }

                if( !iLine.save( get_TrxName())) {
                    m_processMsg = "@NotCreateInvoiceLineFromOrderLine@";

                    return null;
                }
            }
        }

        // Manually Process Invoice

        String status = invoice.completeIt();

        invoice.setDocStatus( status );
        invoice.save( get_TrxName());
        setC_CashLine_ID( invoice.getC_CashLine_ID());

        if( !DOCSTATUS_Completed.equals( status )) {
            m_processMsg = "@C_Invoice_ID@: " + invoice.getProcessMsg();

            return null;
        }

        return invoice;
    }    // createInvoice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private MOrder createCounterDoc() {

        // Is this itself a counter doc ?
    	log.fine("Llegue a createCounterDoc");
        if( getRef_Order_ID() != 0 ) {
        	log.fine("Sale por el primer if");
            return null;
        }

        // Org Must be linked to BPartner

        MOrg org                  = MOrg.get( getCtx(),getAD_Org_ID());
        int  counterC_BPartner_ID = org.getLinkedC_BPartner_ID();

        if( counterC_BPartner_ID == 0 ) {
        	log.fine("Sale por el segundo if");
            return null;
        }

        // Business Partner needs to be linked to Org

        MBPartner bp               = new MBPartner( getCtx(),getC_BPartner_ID(),null );
        int       counterAD_Org_ID = bp.getAD_OrgBP_ID_Int();

        if( counterAD_Org_ID == 0 ) {
        	log.fine("Sale por el tercer if");
            return null;
        }

        MBPartner counterBP = new MBPartner( getCtx(),counterC_BPartner_ID,null );
        MOrgInfo counterOrgInfo = MOrgInfo.get( getCtx(),counterAD_Org_ID );

        log.info( "Counter BP=" + counterBP.getName());

        // Document Type

        int             C_DocTypeTarget_ID = 0;
        MDocTypeCounter counterDT          = MDocTypeCounter.getCounterDocType( getCtx(),getC_DocType_ID());

        if( counterDT != null ) {
            log.fine( counterDT.toString());

            if( !counterDT.isCreateCounter() ||!counterDT.isValid()) {
                return null;
            }

            C_DocTypeTarget_ID = counterDT.getCounter_C_DocType_ID();
        } else    // indirect
        {
            C_DocTypeTarget_ID = MDocTypeCounter.getCounterDocType_ID( getCtx(),getC_DocType_ID());
            log.fine( "Indirect C_DocTypeTarget_ID=" + C_DocTypeTarget_ID );

            if( C_DocTypeTarget_ID <= 0 ) {
                return null;
            }
        }

        // Deep Copy
        log.fine("Justo antes de llamar al counter, la id es="+this.getC_Order_ID());
        MOrder counter = copyFrom( this,getDateOrdered(),C_DocTypeTarget_ID,!isSOTrx(),true,false,get_TrxName());

        //
       /* if(counter){
        	MBPartner a = new MBPartner(Env.getCtx(),from.getC_BPartner_ID(),null);
        	to.setAD_Org_ID(Integer.valueOf(a.getAD_OrgBP_ID()).intValue());
        	MOrg b = new MOrg(Env.getCtx(),Integer.valueOf(a.getAD_OrgBP_ID()).intValue(),null);
        	MOrgInfo c = new MOrgInfo(b);
        	to.setM_Warehouse_ID(c.getM_Warehouse_ID());
        }*/
        log.fine("La org que deja para grabar es:"+counterAD_Org_ID);
        log.fine("El almacén que intenta grabar es:"+counterOrgInfo.getM_Warehouse_ID());
        counter.setAD_Org_ID( counterAD_Org_ID );
        counter.setM_Warehouse_ID( counterOrgInfo.getM_Warehouse_ID());
        
        //

        counter.setBPartner( counterBP );
        counter.setDatePromised( getDatePromised());    // default is date ordered

        // Refernces (Should not be required

        counter.setSalesRep_ID( getSalesRep_ID());
        counter.save( get_TrxName());

        // Update copied lines

        MOrderLine[] counterLines = counter.getLines( true,null );

        for( int i = 0;i < counterLines.length;i++ ) {
        	log.fine("Estoy en el for de morderlines");
            MOrderLine counterLine = counterLines[ i ];

            counterLine.setOrder( counter );    // copies header values (BP, etc.)
            counterLine.setPrice();
            counterLine.setTax();
            counterLine.save( get_TrxName());
        }

        log.fine( counter.toString());

        // Document Action

        if( counterDT != null ) {
            if( counterDT.getDocAction() != null ) {
               // counter.setDocAction( counterDT.getDocAction());
               // counter.processIt( counterDT.getDocAction());
            	counter.setDocStatus(DocAction.STATUS_InProgress);
            	counter.setDocAction(DocAction.ACTION_Complete);
              //  counter.processIt( DocAction.STATUS_InProgress);
                counter.save( get_TrxName());
            }
        }

        return counter;
    }    // createCounterDoc

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean postIt() {
        log.info( toString());

        return false;
    }    // postIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean voidIt() {
    	MDocType dt = MDocType.get( getCtx(),getC_DocType_ID());
    	boolean isOrderTransferred = dt.getDocTypeKey().equalsIgnoreCase(
				MDocType.DOCTYPE_Pedido_Transferible);
    	int locatorID = 0;
    	// El pedido transferible ya fue transferido, no se puede anular
    	if(isOrderTransferred){
    		locatorID = MWarehouse.getDefaultLocatorID(getM_Warehouse_ID(), get_TrxName());
    		if(isTransferred()){
	    		m_processMsg = "@TransferableDocVoidNotAllowed@";
	    		return false;
    		}
    	}
    	
        MOrderLine[] lines = getLines( true,"M_Product_ID" );

        log.info( toString());

        for( int i = 0;i < lines.length;i++ ) {
            MOrderLine line = lines[ i ];
            BigDecimal old  = line.getQtyOrdered();

            // Si el pedido es transferible, se deben decrementar las cantidades
    		// transferidas del pedido original e incrementar la cantidad pedida y
    		// reservada del stock
            if(isOrderTransferred){
				DB.executeUpdate(
						"UPDATE c_orderline SET qtytransferred = qtytransferred-"+String.valueOf(old)+" WHERE c_orderline_id = "
								+ line.getRef_OrderLine_ID(), get_TrxName());
				
				// Reservar el stock del pedido original si el artículo se stockea
				if (MProduct.isProductStocked(getCtx(), line.getM_Product_ID())
						&& !MStorage.add(getCtx(), getM_Warehouse_ID(), locatorID,
								line.getM_Product_ID(), 0, 0, BigDecimal.ZERO,
								old, BigDecimal.ZERO, get_TrxName())) {
					m_processMsg = "@CannotReserveStock@";
                    return false;
				}
            }
            
            if( old.compareTo( Env.ZERO ) != 0 ) {
                line.addDescription( Msg.getMsg( getCtx(),"Voided" ) + " (" + old + ")" );
                line.setQty( Env.ZERO );
                line.setLineNetAmt( Env.ZERO );
                line.save( get_TrxName());
            }
        }

        addDescription( Msg.getMsg( getCtx(),"Voided" ));

        // Clear Reservations
        int[] orderLines = PO.getAllIDs("C_OrderLine", "C_Order_ID = " + getC_Order_ID() , get_TrxName() );
        if( !isOrderTransferred && !reserveStock( null,orderLines )) {
            m_processMsg = "@NotUnreserveStockVoid@";

            return false;
        }

        if( !createReversals()) {
            return false;
        } 

        setProcessed( true );
        setDocAction( DOCACTION_None );

        return true;
    }    // voidIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean createReversals() {

        // Cancel only Sales

        if( !isSOTrx()) {
            return true;
        }

        log.info( "createReversals" );

        StringBuffer info = new StringBuffer();

        // Si se deben anular los remitos se anulan
        if(isVoidInOuts()){
        
	        // Reverse All *Shipments*
	
	        info.append( "@M_InOut_ID@:" );
	
	        MInOut[] shipments = getShipments();
	
	        for( int i = 0;i < shipments.length;i++ ) {
	            MInOut ship = shipments[ i ];
	
	            // if closed - ignore
	
	            if( MInOut.DOCSTATUS_Closed.equals( ship.getDocStatus()) || MInOut.DOCSTATUS_Reversed.equals( ship.getDocStatus()) || MInOut.DOCSTATUS_Voided.equals( ship.getDocStatus())) {
	                continue;
	            }
	
	            ship.set_TrxName( get_TrxName());
	
	            // If not completed - void - otherwise reverse it
	
	            if( !MInOut.DOCSTATUS_Completed.equals( ship.getDocStatus())) {
	                if( ship.voidIt()) {
	                    ship.setDocStatus( MInOut.DOCSTATUS_Voided );
	                }
	            } else if( ship.reverseCorrectIt())    // completed shipment
	            {
	                ship.setDocStatus( MInOut.DOCSTATUS_Reversed );
	                info.append( " " ).append( ship.getDocumentNo());
	            } else {
	                m_processMsg = "@CouldNotReverseShipment@ " + ship;
	
	                return false;
	            }
	
	            ship.setDocAction( MInOut.DOCACTION_None );
	            ship.save( get_TrxName());
	        }                                          // for all shipments
        
        }

        // Si se debe anular las facturas se anulan
        if(isVoidInvoices()){
        
	        // Reverse All *Invoices*
	
	        info.append( " - @C_Invoice_ID@:" );
	
	        MInvoice[] invoices = getInvoices();
	
	        for( int i = 0;i < invoices.length;i++ ) {
	            MInvoice invoice = invoices[ i ];
	
	            // if closed - ignore
	
	            if( MInvoice.DOCSTATUS_Closed.equals( invoice.getDocStatus()) || MInvoice.DOCSTATUS_Reversed.equals( invoice.getDocStatus()) || MInvoice.DOCSTATUS_Voided.equals( invoice.getDocStatus())) {
	                continue;
	            }
	
	            invoice.set_TrxName( get_TrxName());
	
	            // If not compleded - void - otherwise reverse it
	
	            if( !MInvoice.DOCSTATUS_Completed.equals( invoice.getDocStatus())) {
	                if( invoice.voidIt()) {
	                    invoice.setDocStatus( MInvoice.DOCSTATUS_Voided );
	                }
	            } else if( invoice.reverseCorrectIt())    // completed invoice
	            {
	                invoice.setDocStatus( MInvoice.DOCSTATUS_Reversed );
	                info.append( " " ).append( invoice.getDocumentNo());
	            } else {
	                m_processMsg = "@CouldNotReverseInvoice@ " + invoice;
	
	                return false;
	            }
	
	            invoice.setDocAction( MInvoice.DOCACTION_None );
	            invoice.save( get_TrxName());
	        }                                             // for all shipments
        }
        
        m_processMsg = info.toString();

        return true;
    }    // createReversals

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
        log.info( toString());
        MDocType dt = MDocType.get( getCtx(),getC_DocType_ID());
        boolean isOrderTransferred = dt.getDocTypeKey().equalsIgnoreCase(
				MDocType.DOCTYPE_Pedido_Transferible);

        if(!isOrderTransferred){
	        // Close Not delivered Qty - SO/PO
	
	        MOrderLine[] lines = getLines( true,"M_Product_ID" );
	
	        for( int i = 0;i < lines.length;i++ ) {
	            MOrderLine line = lines[ i ];
	            BigDecimal old  = line.getQtyOrdered();
				BigDecimal qtyDelivered = line.getQtyDelivered().add(
						line.getQtyTransferred());
				
	            if( old.compareTo(qtyDelivered) != 0 ) {
	
	            	//Modificado por ConSerTi por el fallo de linea a 0 al cerrar un pedido
	               log.fine("Estoy en MOrder y entro en el if, para poner el valor..:"+line.getQtyDelivered()+", old="+old);
	            	line.setQtyOrdered(qtyDelivered);
	//            	line.setQtyOrdered(old);
	
	//            	Fin modificacion
	            	// QtyEntered unchanged	
	                line.addDescription( "Close (" + old + ")" );
	                line.save( get_TrxName());
	            }
	        }
	
	        // Clear Reservations
	        int[] orderLines = PO.getAllIDs("C_OrderLine", "C_Order_ID = " + getC_Order_ID() , get_TrxName() );
	        if( !reserveStock( null,orderLines )) {
	            m_processMsg = "@NotUnreserveStockClose@";
	
	            return false;
	        }
        }

        setProcessed( true );
        setDocAction( DOCACTION_None );

        return true;
    }    // closeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseCorrectIt() {
        log.info( toString());

        return voidIt();
    }    // reverseCorrectionIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt() {
        log.info( toString());

        return false;
    }    // reverseAccrualIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt() {
        log.info( toString());

        MDocType dt           = MDocType.get( getCtx(),getC_DocType_ID());
        String   DocSubTypeSO = dt.getDocSubTypeSO();

        // PO - just re-open

        if( !isSOTrx()) {
            log.fine( "Existing documents not modified - " + dt );
        }

        // Reverse Direct Documents

        if( MDocType.DOCSUBTYPESO_OnCreditOrder.equals( DocSubTypeSO )    // (W)illCall(I)nvoice
                || MDocType.DOCSUBTYPESO_WarehouseOrder.equals( DocSubTypeSO )    // (W)illCall(P)ickup
                || MDocType.DOCSUBTYPESO_POSOrder.equals( DocSubTypeSO ))    // (W)alkIn(R)eceipt
                {
            if( !createReversals()) {
                return false;
            }
        } else {
            log.fine( "reActivateIt - Existing documents not modified - SubType=" + DocSubTypeSO );
        }
        
        // Actualiza la fecha del pedido a la fecha actual
		// Se comentan estas líneas ya que no se debe modificar la fecha del
		// pedido al reactivar, ya que dispara otras funcionalidades indeseadas,
		// de última que se modifique a mano. 
//        Timestamp today = Env.getDate();
//        setDateOrdered(today);
//        setDateAcct(today);
        
        // Comentado por para que deje el precio que estaba
        // ---------------------------------------------------------
//        // Actualización de los precios de las líneas según el precio actual de tarifa.
//        if (!updateLinesPrices()) {
//        	return false;
//        }
        // ---------------------------------------------------------

		// Retornar el pendiente o pedido, dependiendo del signo del pedido, al
		// stock y luego al completar vuelve todo al stock. Esto se hace así ya
		// que puede quedar un pedido reactivado indefinidamente y reservando
		// stock innecesariamente
        // Si se reactiva un pedido transferible, en realidad debe sumar el reservado
        boolean isOrderTransferred = dt.getDocTypeKey().equalsIgnoreCase(
				MDocType.DOCTYPE_Pedido_Transferible);
        Integer locatorID = MWarehouse.getDefaultLocatorID(getM_Warehouse_ID(), get_TrxName());
        MOrderLine[] lines = getLines(true,null);
        
        BigDecimal qtyReserved, qtyOrdered;
        int updated = 0;
        for (MOrderLine line : lines) {
        	qtyReserved = (isSOTrx()?line.getPendingDeliveredQty():BigDecimal.ZERO).negate();
        	qtyOrdered = (isSOTrx()?BigDecimal.ZERO:line.getPendingDeliveredQty()).negate();
        	// Si el pedido es transferible, se deben decrementar las cantidades
    		// transferidas del pedido original 
            if(isOrderTransferred){
				updated = DB.executeUpdate(
						"UPDATE c_orderline SET qtytransferred = qtytransferred-"
								+ String.valueOf(line.getQtyOrdered())
								+ " WHERE c_orderline_id = "
								+ line.getRef_OrderLine_ID(), get_TrxName());
				if(updated != 1){
					m_processMsg = "Can not update ref order line";
					return false;
				}
				qtyReserved = (isSOTrx()?line.getQtyOrdered():BigDecimal.ZERO);
				qtyOrdered = (isSOTrx()?BigDecimal.ZERO:line.getQtyOrdered());
            }
            
			// Actualizar la cantidad pendiente del pedido a 0
			updated = DB.executeUpdate(
					"UPDATE c_orderline SET qtyReserved = 0 WHERE c_orderline_id = "
							+ line.getID(), get_TrxName());
            
			if(updated != 1){
				m_processMsg = "Can not update order line reserved qty";
				return false;
			}
			
            // Actualizar la cantidad reservada o pedida en el stock
			if (MProduct.isProductStocked(getCtx(), line.getM_Product_ID())
					&& !MStorage.add(getCtx(), getM_Warehouse_ID(), locatorID,
							line.getM_Product_ID(), 0, 0, BigDecimal.ZERO,
							qtyReserved, qtyOrdered, get_TrxName())) {
				m_processMsg = "@CannotReserveStock@";
                return false;
			}
		}
        
        setDocAction( DOCACTION_Complete );
        setProcessed( false );

        return true;
    }    // reActivateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSummary() {
        StringBuffer sb = new StringBuffer();

        sb.append( getDocumentNo());

        // : Grand Total = 123.00 (#1)

        sb.append( ": " ).append( Msg.translate( getCtx(),"GrandTotal" )).append( "=" ).append( getGrandTotal());

        if( m_lines != null ) {
            sb.append( " (#" ).append( m_lines.length ).append( ")" );
        }

        // - Description

        if( (getDescription() != null) && (getDescription().length() > 0) ) {
            sb.append( " - " ).append( getDescription());
        }

        return sb.toString();
    }    // getSummary

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getResumen() {
        StringBuffer sb = new StringBuffer();

        sb.append( "Numero de documento: " + getDocumentNo());
        sb.append( ": " ).append( Msg.translate( getCtx(),"GrandTotal" ));
        sb.append( "=" ).append( getGrandTotal());
        sb.append( " - N� de l�neas de pedido: " ).append( getLines( true,null ).length ).append( "." );

        // - Description

        if( (getDescription() != null) && (getDescription().length() > 0) ) {
            sb.append( " - Descripci�n: " ).append( getDescription());
        }

        return sb.toString();
    }    // getResumen creado por ConSerTi

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDoc_User_ID() {
        return getSalesRep_ID();
    }    // getDoc_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt() {
        return getGrandTotal();
    }    // getApprovalAmt

    public int getCuentaLineas (){
	MOrderLine[] lines = getLines(false);
	return lines.length;
    }//getCuentaLineas

    public MOrderLine[] getLines (boolean requery){
	if (m_lines != null && !requery)
		return m_lines;
	//
	m_lines = getLines(null, "ORDER BY Line");
		return m_lines;
    }	//	getLines

	/**
	 * @return Retorna una lista con todos los productos que estan asociados a
	 * alguna línea de este pedido y que no son están marcados como "Comprados" 
	 * (IsPurchased = 'N').
	 */
	private List<String> getNotPurchasedProducts() {
		List<String> products = new ArrayList<String>();
		String sql =
			" SELECT DISTINCT p.Name " + 
			" FROM C_OrderLine ol " +
			" INNER JOIN M_Product p ON (ol.M_Product_ID = p.M_Product_ID) " +
			" WHERE  ol.C_Order_ID = ? AND p.IsPurchased = 'N' "; 

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_Order_ID());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				products.add(rs.getString("Name"));
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error while getting order products", e);
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				if (rs != null) rs.close();
			} catch (Exception e) {}
		}
		
		return products;
	}

	/**
	 * @return El wrapper de este pedido para ser utilizado en un calculador de
	 *         descuentos {@link DiscountCalculator}.
	 */
	public IDocument getDiscountableWrapper() {
		return new DiscountableMOrderWrapper();
	}

	/**
	 * Actualiza los importes de este pedido a partir de los importes de sus
	 * líneas y sus impuestos asociados. NO invoca el método
	 * {@link MOrder#save()}, siendo responsabilidad del cliente realizar el
	 * guardado de los cambios.
	 * 
	 * @return <code>true</code> si la actualización se puede realizar
	 *         correctamente, <code>false</code> si se produce algún error.
	 */
	protected boolean updateAmounts() {
		boolean updateOk = true;
		PreparedStatement pstmt = null;
		ResultSet         rs    = null;
		
		StringBuffer dataSql = new StringBuffer();
		dataSql
			.append("SELECT ")
			// Suma de los importes netos de las líneas
			.append(  "(SELECT COALESCE(SUM(LineNetAmt),0) ")
			.append(   "FROM C_OrderLine ol ")
			.append(   "WHERE ol.C_Order_ID = o.C_Order_ID) AS TotalLines, ")
			// Suma de los impuestos de este pedido
			.append(  "(SELECT COALESCE(SUM(TaxAmt),0) ")
			.append(   "FROM C_OrderTax ot ")
			.append(   "WHERE ot.C_Order_ID = o.C_Order_ID) AS TotalTaxAmt ")
		    .append("FROM C_Order o ")
		    .append("WHERE o.C_Order_ID = ?");
		
		BigDecimal totalLines  = null; // Total neto de líneas
		BigDecimal totalTaxAmt = null; // Total de impuestos
		
		try {
			pstmt = DB.prepareStatement(dataSql.toString(), get_TrxName());
			pstmt.setInt(1, getC_Order_ID());
			rs = pstmt.executeQuery();
			// Obtiene los importes necesarios para actualizar el total del pedido.
			if (rs.next()) {
				totalLines  = rs.getBigDecimal("TotalLines");
				totalTaxAmt = rs.getBigDecimal("TotalTaxAmt");
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error updating Order Header", e);
			updateOk = false;
		} finally {
			try {
				if (rs != null) rs.close(); rs = null;
				if (pstmt != null) pstmt.close(); pstmt = null;
			} catch (Exception e) {}
		}
		
		if (totalLines == null || totalTaxAmt == null) {
			updateOk = false;
		// Se obtuvieron ambos importes
		} else {
			// Actualización del monto de descuento de la cabecera del pedido
			if(isUpdateChargeAmt()){
				BigDecimal chargeAmt = getTotalDocumentDiscountAmtFromLines();
				chargeAmt = Util.isEmpty(chargeAmt, true)?BigDecimal.ZERO:chargeAmt.negate(); 
				setChargeAmt(chargeAmt);
			}
			BigDecimal grandTotal = null;    // Total del pedido calculado
			BigDecimal chargeAmt =           // Importe del cargo (descuentos) 
				getChargeAmt() != null ? getChargeAmt() : BigDecimal.ZERO;
			
			if (isTaxIncluded()) {
				grandTotal = totalLines.add(chargeAmt);
			} else {
				grandTotal = totalLines.add(totalTaxAmt).add(chargeAmt);
			}
			
			setTotalLines(totalLines);
			setGrandTotal(grandTotal);
			
			log.info("Order updated - TotalLines = " + totalLines + " - GrandTotal = " + grandTotal); 
		}
		
		return updateOk;
	}
	
	public BigDecimal getChargeAmt() {
		return super.getChargeAmt() == null ? BigDecimal.ZERO : super.getChargeAmt();
	}
	
//	private boolean updateLinesPrices() {
//		MOrderLine[] lines = getLines();
//		for (MOrderLine line : lines) {
//			// Setea la fecha actual a la línea para que el precio se re-calcule
//			// basado en la fecha de hoy
//			line.setDateOrdered(Env.getDate());
//			line.setPrice();
//			if (!line.save()) {
//				m_processMsg = "@OrderLineSaveError@ #" + line.getLine() + ": "
//						+ line.getProcessMsg();
//				return false;
//			}
//		}
//		
//		updateAmounts();
//		
//		return true;
//	}
	
	public String getDocSubTypeSO() {
		int docTypeID = getC_DocType_ID() == 0 ? getC_DocTypeTarget_ID() : getC_DocType_ID();
		return MDocType.get(getCtx(), docTypeID).getDocSubTypeSO();
	}
	
	public boolean isExpiredProposal(Date date) {
		return 
			MDocType.DOCSUBTYPESO_Proposal.equals(getDocSubTypeSO()) 
				&& getValidTo() != null
				&& getValidTo().compareTo(date) < 0
				&& !TimeUtil.isSameDay(getValidTo(), new Timestamp(date.getTime()));
	}

    /**
	 * @param voidInOuts the voidInOuts to set
	 */
	public void setVoidInOuts(boolean voidInOuts) {
		this.voidInOuts = voidInOuts;
	}

	/**
	 * @return the voidInOuts
	 */
	public boolean isVoidInOuts() {
		return voidInOuts;
	}

	/**
	 * @param voidInvoices the voidInvoices to set
	 */
	public void setVoidInvoices(boolean voidInvoices) {
		this.voidInvoices = voidInvoices;
	}

	/**
	 * @return the voidInvoices
	 */
	public boolean isVoidInvoices() {
		return voidInvoices;
	}

	public BigDecimal getTotalLinesNet() {
		BigDecimal total = Env.ZERO;
		for (MOrderLine orderLine : getLines()) {
			// Total de líneas sin impuestos
			total = total.add(orderLine.getTotalPriceEnteredNet());
		}
		return total;
	}
	
	public BigDecimal getTotalLinesNetWithoutDocumentDiscount() {
		BigDecimal total = Env.ZERO;
		for (MOrderLine orderLine : getLines()) {
			// Total de líneas sin impuestos
			total = total.add(orderLine.getTotalPriceEnteredNet()).subtract(
					orderLine.getTotalDocumentDiscountUnityAmtNet());
		}
		return total;
	}
	
	public void setTpvGeneratedInvoiceID(Integer tpvGeneratedInvoiceID) {
		this.tpvGeneratedInvoiceID = tpvGeneratedInvoiceID;
	}

	public Integer getTpvGeneratedInvoiceID() {
		return tpvGeneratedInvoiceID;
	}
	
	public BigDecimal getPercepcionesTotalAmt(){
		String sql = "select sum(taxamt) " +
					 "from c_ordertax as ot " +
					 "inner join c_tax as t on t.c_tax_id = ot.c_tax_id " +
					 "where ot.c_order_id = ? AND t.ispercepcion = 'Y'";
		BigDecimal percepcionAmt = DB.getSQLValueBD(get_TrxName(), sql, getID());
		return percepcionAmt == null ? BigDecimal.ZERO : percepcionAmt;
	}
	
	/**
	 * @return lista de percepciones aplicadas a esta factura
	 */
	public List<MOrderTax> getAppliedPercepciones(){
		String sql = "select ot.* " +
					 "from c_ordertax as ot " +
					 "inner join c_tax as t on t.c_tax_id = ot.c_tax_id " +
					 "where ot.c_order_id = ? AND t.ispercepcion = 'Y'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<MOrderTax> percepciones = new ArrayList<MOrderTax>();
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, getID());
			rs = ps.executeQuery();
			while(rs.next()){
				percepciones.add(new MOrderTax(getCtx(), rs, get_TrxName()));
			}
		} catch (Exception e) {
			log.severe("ERROR getting percepciones");
			e.printStackTrace();
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				log.severe("ERROR getting percepciones");
				e2.printStackTrace();
			}
		}
		return percepciones;
	}
	
	/**
     * Wrapper de {@link MOrder} para cálculo de descuentos.
     */
	private class DiscountableMOrderWrapper extends DiscountableDocument {

		@Override
		protected List<? extends Object> getOriginalLines() {
			return Arrays.asList(getLines(false));
		}

		@Override
		protected IDocumentLine createDocumentLine(Object originalLine) {
			return ((MOrderLine)originalLine).createDiscountableWrapper(this);
		}

		@Override
		public Date getDate() {
			return getDateOrdered();
		}

		@Override
		public void setTotalDocumentDiscount(BigDecimal discountAmount) {
			// En el pedido se invierte el signo del descuento ya que un valor
			// positivo representa un cargo al encabezado del pedido.
			setChargeAmt(discountAmount.negate());
			if (discountAmount.compareTo(BigDecimal.ZERO) == 0) {
				setC_Charge_ID(0);
			}
			updateAmounts();
		}

		@Override
		public boolean isCalculateNetDiscount() {
			return true;
		}

		@Override
		public void setDocumentReferences(MDocumentDiscount documentDiscount) {
			documentDiscount.setC_Order_ID(getC_Order_ID());
			documentDiscount.setC_Invoice_ID(getTpvGeneratedInvoiceID());
		}

		@Override
		public void setDocumentDiscountChargeID(int chargeID) {
			setC_Charge_ID(chargeID);
		}

		@Override
		public void setTotalBPartnerDiscount(BigDecimal discountAmount) {
			// No es necesario discriminar el importe de descuento de EC aquí.
		}

		@Override
		public void setTotalManualGeneralDiscount(BigDecimal discountAmount) {
			// No es necesario discriminar el importe de descuento de EC aquí.			
		}

		@Override
		public Integer getOrgID() {
			return MOrder.this.getAD_Org_ID();
		}

		@Override
		public Integer getBPartnerID() {
			return MOrder.this.getC_BPartner_ID();
		}

		@Override
		public Integer getDocTypeID() {
			return MOrder.this.getC_DocTypeTarget_ID();
		}

		@Override
		public boolean isSOTrx() {
			return MOrder.this.isSOTrx();
		}

		@Override
		public boolean isApplyPercepcion() {
			return true;
		}
		
		@Override
		public List<DocumentTax> getAppliedPercepciones(){
			List<MOrderTax> orderTaxes = MOrder.this.getAppliedPercepciones();
			List<DocumentTax> documentTaxes = new ArrayList<DocumentTax>();
			DocumentTax doctax;
			for (MOrderTax orderTax : orderTaxes) {
				// Se debe determinar el porcentaje del impuesto que se aplicó
				// en el documento, esto se determina con el importe base y el
				// monto del impuesto
				doctax = new DocumentTax();
				doctax.setTaxID(orderTax.getC_Tax_ID());
				doctax.setTaxAmt(orderTax.getTaxAmt());
				doctax.setTaxBaseAmt(orderTax.getTaxBaseAmt());
				doctax.setTaxRate();
				documentTaxes.add(doctax);
			}
			return documentTaxes;
		}
	}
	
	public Integer getWarehouseDeliveryProductsCount(){
		Integer count = 0;
		for (MOrderLine orderLine : getLines()) {
			count += orderLine.isDeliverDocumentPrintable()?1:0;
		}
		return count;
	}
	
	public boolean isTPVInstance() {
		return isTPVInstance;
	}

	public void setTPVInstance(boolean isTPVInstance) {
		this.isTPVInstance = isTPVInstance;
	}
	
	// Proceso de Reasignación de almacén del Pedido
    public void changeOrderWarehouse(Integer M_Warehouse_ID) throws Exception {
        if (getDocStatus().compareTo(DOCSTATUS_Drafted) == 0){
            setM_Warehouse_ID(M_Warehouse_ID);
        }
        else{
            if (getDocStatus().compareTo(DOCSTATUS_Completed) == 0){
                // Reactivar el documento para poder modificar el almacen
                if (!DocumentEngine.processAndSave(this, DOCACTION_Re_Activate, false))
                    throw new Exception ("Error al reactivar el pedido: " + Msg.parseTranslation(getCtx(), getProcessMsg()));
                setAD_Org_ID( MWarehouse.get( getCtx(),M_Warehouse_ID).getAD_Org_ID());
                setM_Warehouse_ID(M_Warehouse_ID);
                // Guardar la cabecera de pedido antes de modificar las líneas, dado que las líneas recuperan la cabecera
                // a fin de determinar si es necesario modificar el warehouse a partir de la información de la cabecera
                if (!save()) {
                	throw new Exception ("Error al actualizar la cabecera de pedido: " + Msg.parseTranslation(getCtx(), CLogger.retrieveErrorAsString()));
                }
                // Setear el warehouse de las líneas
                for (MOrderLine anOrderLine : getLines()) {
                    anOrderLine.setM_Warehouse_ID(M_Warehouse_ID);
                    if (!anOrderLine.save())
                        throw new Exception ("Error al actualizar las linea de pedido: " + Msg.parseTranslation(getCtx(), CLogger.retrieveErrorAsString()));   
                }
                // Completar nuevamente
                if (!DocumentEngine.processAndSave(this, DOCACTION_Complete, true))
                    throw new Exception ("Error al completar el pedido: " + Msg.parseTranslation(getCtx(), getProcessMsg()));
            }   
        }
    }

	public boolean isForceReserveStock() {
		return forceReserveStock;
	}

	public void setForceReserveStock(boolean forceReserveStock) {
		this.forceReserveStock = forceReserveStock;
	}
	
	public BigDecimal getTotalDocumentDiscountAmtFromLines() {
		String sql = "select sum(documentdiscountamt) as documentdiscountamt from c_orderline where c_order_id = ?";
		BigDecimal amt = DB.getSQLValueBD(get_TrxName(), sql, getID());
		return Util.isEmpty(amt, false)?BigDecimal.ZERO:amt;
	}
	
	public BigDecimal getTotalLineDiscountAmtFromLines() {
		String sql = "select sum(linediscountamt) as linediscountamt from c_orderline where c_order_id = ?";
		BigDecimal amt = DB.getSQLValueBD(get_TrxName(), sql, getID());
		return Util.isEmpty(amt, false)?BigDecimal.ZERO:amt;
	}
	
	public BigDecimal getTotalBonusDiscountAmtFromLines() {
		String sql = "select sum(linebonusamt) as linebonusamt from c_orderline where c_order_id = ?";
		BigDecimal amt = DB.getSQLValueBD(get_TrxName(), sql, getID());
		return Util.isEmpty(amt, false)?BigDecimal.ZERO:amt;
	}
	
	public BigDecimal getTotalDiscountAmtFromLines(String discountColumnName) {
		discountColumnName = !Util.isEmpty(discountColumnName, true) ? discountColumnName
				: "documentdiscountamt+linediscountamt+linebonusamt";
		String sql = "select sum(" + discountColumnName
				+ ") from c_orderline where c_order_id = ?";
		BigDecimal amt = DB.getSQLValueBD(get_TrxName(), sql, getID());
		return Util.isEmpty(amt, false)?BigDecimal.ZERO:amt;
	}
	
	/**
	 * Se crea el pedido transferido o transferible a partir del pedido.
	 */
	public MOrder createTransferOrder(Integer newDocTypeID) throws Exception{
		// Crear el pedido idéntico
		MOrder newOrder = new MOrder(getCtx(), 0, get_TrxName());
		MOrder.copyValues(this, newOrder);
		newOrder.setC_DocTypeTarget_ID(newDocTypeID);
		newOrder.setC_DocType_ID(newDocTypeID);
		// Intercambiar el depósito y organización origen por destino 
		newOrder.setAD_Org_ID(getAD_Org_Transfer_ID());
		newOrder.setAD_Org_Transfer_ID(getAD_Org_ID());
		newOrder.setM_Warehouse_ID(getM_Warehouse_Transfer_ID());
		newOrder.setM_Warehouse_Transfer_ID(getM_Warehouse_ID());
		newOrder.setRef_Order_ID(getID());
		newOrder.setDocStatus(DOCSTATUS_Drafted);
		newOrder.setDocAction(DOCACTION_Complete);
		newOrder.setProcessed(false);
		// Guardar
		if(!newOrder.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		// Copiar las líneas
		MOrderLine newOrderLine;
		BigDecimal pendingQty;
		for (MOrderLine orderLine : getLines(true)) {
			newOrderLine = new MOrderLine(getCtx(), 0, get_TrxName());
			MOrderLine.copyValues(orderLine, newOrderLine);
			pendingQty = orderLine.getQtyOrdered().subtract(orderLine.getQtyDelivered());
			
			newOrderLine.setC_Order_ID(newOrder.getID());
			newOrderLine.setQty(pendingQty);
			newOrderLine.setQtyReserved(BigDecimal.ZERO);
			newOrderLine.setQtyInvoiced(pendingQty);
			newOrderLine.setQtyDelivered(BigDecimal.ZERO);
			newOrderLine.setQtyTransferred(BigDecimal.ZERO);
			newOrderLine.setPrice(orderLine.getPriceEntered());
			newOrderLine.setRef_OrderLine_ID(orderLine.getID());
			newOrderLine.setProcessed(false);
			newOrderLine.setM_Warehouse_ID(newOrder.getM_Warehouse_ID());
			newOrderLine.setAD_Org_ID(newOrder.getAD_Org_ID());
			if(!newOrderLine.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
		return newOrder;
	}

	public boolean isUpdateChargeAmt() {
		return updateChargeAmt;
	}

	public void setUpdateChargeAmt(boolean updateChargeAmt) {
		this.updateChargeAmt = updateChargeAmt;
	}

	@Override
	public int getAuthorizationID() {
		return this.getM_AuthorizationChain_ID();
	}

	@Override
	public void setDocumentID(X_M_AuthorizationChainDocument authDocument) {
		authDocument.setC_Order_ID(this.getC_Order_ID());
	}

	@Override
	public Integer getDocTypeID() {
		return MOrder.this.getC_DocTypeTarget_ID();
	}

	/**
	 * Control realizado en las líneas contra las cantidades mínimas pedidas
	 * configuradas en el proveedor
	 * 
	 * @return
	 */
	private CallResult controlOrderMin(){
		CallResult result = new CallResult();
		String sql = "select ol.line, p.value, p.name, ol.qtyentered, po.order_min "
					+ "from c_orderline as ol "
					+ "inner join m_product_po as po on (po.m_product_id = ol.m_product_id and ol.c_bpartner_id = po.c_bpartner_id and po.isactive = 'Y') "
					+ "inner join m_product as p on p.m_product_id = ol.m_product_id "
					+ "where ol.c_order_id = ? and ol.qtyentered < po.order_min "
					+ "order by ol.line";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, getID());
			rs = ps.executeQuery();
			if(rs.next()){
				result.setMsg(Msg.getMsg(
						getCtx(),
						"QtyEnteredLessThanOrderMinQty",
						new Object[] { rs.getString("value"), rs.getString("name"),
								rs.getBigDecimal("order_min"), rs.getBigDecimal("qtyentered") }), true);
			}
		} catch (Exception e) {
			result.setMsg(e.getMessage(), true);
		} finally{
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
			} catch (Exception e2) {
				result.setMsg(e2.getMessage(), true);
			}
		}
		return result;
	}
	
	/**
	 * Realiza el control de los artículos del proveedor
	 * @return resultado
	 */
	private CallResult controlVendorProducts(){
		String sqlVendor = "select ol.m_product_id, p.value, p.name "
				+ "from c_orderline ol "
				+ "inner join m_product p on p.m_product_id = ol.m_product_id "
				+ "left join (select m_product_id "
				+ "				from m_product_po "
				+ "				where c_bpartner_id = ? and isactive = 'Y') po on ol.m_product_id = po.m_product_id "
				+ "where ol.c_order_id = ? and po.m_product_id is null";
    	PreparedStatement ps = null; 
    	ResultSet rs = null;
    	CallResult result = new CallResult();
    	try {
    		ps = DB.prepareStatement(sqlVendor, get_TrxName());
    		ps.setInt(1, getC_BPartner_ID());
        	ps.setInt(2, getID());
        	rs = ps.executeQuery();
        	HTMLMsg msg = new HTMLMsg();
			HTMLList list = msg.createList("onlyvendorproducts", "ul", Msg.getMsg(getCtx(), "OnlyVendorProducts"));
        	while(rs.next()){
				msg.createAndAddListElement(rs.getString("value"),
						rs.getString("value") + " - " + rs.getString("name"), list);
				result.setError(true);
        	}
        	msg.addList(list);
        	result.setMsg(msg.toString());
		} catch (Exception e) {
			result.setMsg(e.getMessage(), true);
		} finally {
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				result.setMsg(e2.getMessage(), true);
			}
		}
    	return result;
	}
	
}    // MOrder



/*
 *  @(#)MOrder.java   02.07.07
 * 
 *  Fin del fichero MOrder.java
 *  
 *  Versión 2.2
 *
 */
