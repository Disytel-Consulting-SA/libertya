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
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.model.attribute.RecommendedAtributeInstance;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.AssetDTO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MInOut extends X_M_InOut implements DocAction {

	// flag especial para evitar validaciones costosas computacionalmente,
	// las cuales no son necesario realizar en caso de ser parte de una venta
	// TPV
	protected boolean isTPVInstance = false;
	
    /**
     * Descripción de Método
     *
     *
     * @param order
     * @param movementDate
     * @param forceDelivery
     * @param allAttributeInstances
     * @param minGuaranteeDate
     * @param complete
     * @param trxName
     *
     * @return
     */

    public static MInOut createFrom( MOrder order,Timestamp movementDate,boolean forceDelivery,boolean allAttributeInstances,Timestamp minGuaranteeDate,boolean complete,String trxName ) {
        if( order == null ) {
            throw new IllegalArgumentException( "No Order" );
        }

        //

        if( !forceDelivery && DELIVERYRULE_CompleteLine.equals( order.getDeliveryRule())) {
            return null;
        }

        // Create Meader

        MInOut retValue = new MInOut( order,0,movementDate );
        
        retValue.setDocAction( complete
                               ?DOCACTION_Complete
                               :DOCACTION_Prepare );

        // Check if we can create the lines

        MOrderLine[] oLines = order.getLines( true,"M_Product_ID" );
        JOptionPane.showInputDialog("Estoy en MInout antes del for"+oLines.length);
        for( int i = 0;i < oLines.length;i++ ) {
			BigDecimal qty = oLines[i].getQtyOrdered()
					.subtract(oLines[i].getQtyDelivered())
					.subtract(oLines[i].getQtyTransferred());
            JOptionPane.showInputDialog("Estoy en MInout y qty="+qty);
            // Nothing to deliver

            if( qty.compareTo( Env.ZERO ) == 0 ) {
                continue;
            }

            // Stock Info

            MStorage[] storages = null;
            MProduct   product  = oLines[ i ].getProduct();

            if( (product != null) && (product.getID() != 0) && product.isStocked()) {
                MProductCategory pc = MProductCategory.get( order.getCtx(),product.getM_Product_Category_ID(), trxName);
                String MMPolicy = pc.getMMPolicy();

                if( (MMPolicy == null) || (MMPolicy.length() == 0) ) {
                    MClient client = MClient.get( order.getCtx());

                    MMPolicy = client.getMMPolicy();
                }

                storages = MStorage.getWarehouse( order.getCtx(),order.getM_Warehouse_ID(),oLines[ i ].getM_Product_ID(),oLines[ i ].getM_AttributeSetInstance_ID(),product.getM_AttributeSet_ID(),allAttributeInstances,minGuaranteeDate,MClient.MMPOLICY_FiFo.equals( MMPolicy ),trxName );
            }

            if( !forceDelivery ) {
                BigDecimal maxQty = Env.ZERO;

                for( int ll = 0;ll < storages.length;ll++ ) {
                    maxQty = maxQty.add( storages[ ll ].getQtyOnHand());
                }

                if( DELIVERYRULE_Availability.equals( order.getDeliveryRule())) {
                    if( maxQty.compareTo( qty ) < 0 ) {
                        qty = maxQty;
                    }
                } else if( DELIVERYRULE_CompleteLine.equals( order.getDeliveryRule())) {
                    if( maxQty.compareTo( qty ) < 0 ) {
                        continue;
                    }
                }
            }

            // Create Line

            if( retValue.getID() == 0 ) {    // not saved yet
                retValue.save( trxName );
            }

            // Create a line until qty is reached

            for( int ll = 0;ll < storages.length;ll++ ) {
                BigDecimal lineQty = storages[ ll ].getQtyOnHand();

                if( lineQty.compareTo( qty ) > 0 ) {
                    lineQty = qty;
                }

                MInOutLine line = new MInOutLine( retValue );

                line.setOrderLine( oLines[ i ],storages[ ll ].getM_Locator_ID(),order.isSOTrx()
                        ?lineQty
                        :Env.ZERO );
                line.setQty( lineQty );    // Correct UOM for QtyEntered

                if( oLines[ i ].getQtyEntered().compareTo( oLines[ i ].getQtyOrdered()) != 0 ) {
                    line.setQtyEntered( lineQty.multiply( oLines[ i ].getQtyEntered()).divide( oLines[ i ].getQtyOrdered(),BigDecimal.ROUND_HALF_UP ));
                }

                line.save( trxName );

                // Delivered everything ?

                qty = qty.subtract( lineQty );

                // storage[ll].changeQtyOnHand(lineQty, !order.isSOTrx()); // Credit Memo not considered
                // storage[ll].save(get_TrxName());

                if( qty.compareTo( Env.ZERO ) == 0 ) {
                    break;
                }
            }
        }    // for all order lines

        // No Lines saved

        if( retValue.getID() == 0 ) {
            return null;
        }

        return retValue;
    }    // createFrom

    /**
     * Descripción de Método
     *
     *
     * @param from
     * @param dateDoc
     * @param C_DocType_ID
     * @param isSOTrx
     * @param counter
     * @param trxName
     * @param setOrder
     *
     * @return
     */

    public static MInOut copyFrom( MInOut from,Timestamp dateDoc,int C_DocType_ID,boolean isSOTrx,boolean counter,String trxName,boolean setOrder, boolean isReverseCopy ) {
        MInOut to = new MInOut( from.getCtx(),0,from.get_TrxName() );

        to.set_TrxName( trxName );
        copyValues( from,to,from.getAD_Client_ID(),from.getAD_Org_ID());
        to.setM_InOut_ID( 0 );
        to.set_ValueNoCheck( "DocumentNo",null );

        //
        
        to.setDocStatus(DOCSTATUS_InProgress);	//IP
        //to.setDocStatus( DOCSTATUS_Drafted );    // Draft
        to.setDocAction( DOCACTION_Complete );

        //

        to.setC_DocType_ID( C_DocType_ID );
        to.setIsSOTrx( isSOTrx );

        if( counter ) {
            to.setMovementType( isSOTrx
                                ?MOVEMENTTYPE_CustomerShipment
                                :MOVEMENTTYPE_VendorReceipts );
        }

        //

        to.setDateOrdered( dateDoc );
        to.setDateAcct( dateDoc );
        to.setMovementDate( dateDoc );
        to.setDatePrinted( null );
        to.setIsPrinted( false );
        to.setDateReceived( null );
        to.setNoPackages( 0 );
        to.setShipDate( null );
        to.setPickDate( null );
        to.setIsInTransit( false );

        //

        to.setIsApproved( false );
        to.setC_Invoice_ID( 0 );
        to.setTrackingNo( null );
        to.setIsInDispute( false );

        //

        to.setPosted( false );
        to.setProcessed( false );
        to.setC_Order_ID( 0 );    // Overwritten by setOrder

        if( counter ) {
            to.setC_Order_ID( 0 );
            to.setRef_InOut_ID( from.getM_InOut_ID());

            // Try to find Order/Invoice link

            if( from.getC_Order_ID() != 0 ) {
                MOrder peer = new MOrder( from.getCtx(),from.getC_Order_ID(),from.get_TrxName());

                if( peer.getRef_Order_ID() != 0 ) {
                    to.setC_Order_ID( peer.getRef_Order_ID());
                }
            }

            if( from.getC_Invoice_ID() != 0 ) {
                MInvoice peer = new MInvoice( from.getCtx(),from.getC_Invoice_ID(),from.get_TrxName());

                if( peer.getRef_Invoice_ID() != 0 ) {
                    to.setC_Invoice_ID( peer.getRef_Invoice_ID());
                }
            }
        } else {
            to.setRef_InOut_ID( 0 );

            if( setOrder ) {
                to.setC_Order_ID( from.getC_Order_ID());
            }
        }

        //

        if( !to.save( trxName )) {
            throw new IllegalStateException( "Could not create Shipment" );
        }

        if( counter ) {
            from.setRef_InOut_ID( to.getM_InOut_ID());
        }

        if( to.copyLinesFrom( from,counter,setOrder, isReverseCopy ) == 0 ) {
            throw new IllegalStateException( "Could not create Shipment Lines" );
        }

        return to;
    }    // copyFrom

    
    /**
	 * Obtener el nro de documento del remito en el caso que exista el
	 * valor pasado como parámetro a la columna parámetro. Además, es posible
	 * filtrar por el registro actual en caso que se requiera
	 * 
	 * @param ctx
	 *            contexto
	 * @param columnName
	 *            nombre de la columna a verificar
	 * @param value
	 *            valor de la columna
	 * @param trxName
	 *            nombre de la transacción actual
	 * @return el nro de documento del remito si es que existe para ese
	 *         valor de columna, null caso contrario
	 */
	public static String getDocNoInOutByStrColumnCondition(Properties ctx, String columnName, String value, Integer inOutSelfID, Integer docTypeID, String trxName){
		if(Util.isEmpty(value, true)) return null;
		Object[] transferParams1 = new Object[] { Env.getAD_Org_ID(ctx), value, docTypeID };
		Object[] transferParams2 = new Object[] { Env.getAD_Org_ID(ctx), value, docTypeID, inOutSelfID };
		boolean selfEmpty = Util.isEmpty(inOutSelfID, true);
		return (String) DB
				.getSQLObject(
						null,
						"SELECT documentno as cant FROM "
								+ Table_Name
								+ " WHERE docstatus IN ('CO','CL') AND ad_org_id = ? AND upper(trim("
								+ columnName + ")) = upper(trim(?)) "
								+ " AND c_doctype_id = ? "
								+ (selfEmpty ? "" : " AND m_inout_id <> ?"),
						selfEmpty ? transferParams1 : transferParams2);
	}
    
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_InOut_ID
     * @param trxName
     */

    public MInOut( Properties ctx,int M_InOut_ID,String trxName ) {
        super( ctx,M_InOut_ID,trxName );
        log.fine("Entro en el minout, constructor1");
        if( M_InOut_ID == 0 ) {

            // setDocumentNo (null);
            // setC_BPartner_ID (0);
            // setC_BPartner_Location_ID (0);
            // setM_Warehouse_ID (0);
            // setC_DocType_ID (0);

            setIsSOTrx( false );
            setMovementDate( new Timestamp( System.currentTimeMillis()));
            setDateAcct( getMovementDate());

            // setMovementType (MOVEMENTTYPE_CustomerShipment);
            setDeliveryRule( DELIVERYRULE_Availability );
            setDeliveryViaRule( DELIVERYVIARULE_Pickup );
            setFreightCostRule( FREIGHTCOSTRULE_FreightIncluded );
            //Modificado por Conserti para modificar el estado del documento
            MDocType dt   = MDocType.get( getCtx(),getC_DocType_ID());
            boolean  pick = dt.isPickQAConfirm();
            boolean  ship = dt.isShipConfirm();
            if(pick && ship){
            	setDocStatus( DOCSTATUS_InProgress);
            }
            else{
            	setDocStatus(DOCSTATUS_Completed);
            }
            //Fin Modificado
            //setDocStatus( DOCSTATUS_Drafted );
            setDocStatus( DOCSTATUS_InProgress );
            setDocAction( DOCACTION_Complete );
            setPriorityRule( PRIORITYRULE_Medium );
            setNoPackages( 0 );
            setIsInTransit( false );
            setIsPrinted( false );
            setSendEMail( false );
            setIsInDispute( false );

            //

            setIsApproved( false );
            super.setProcessed( false );
            setProcessing( false );
            setPosted( false );
        }
    }    // MInOut

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInOut( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInOut

    /**
     * Constructor de la clase ...
     *
     *
     * @param order
     * @param C_DocTypeShipment_ID
     * @param movementDate
     */

    public MInOut( MOrder order,int C_DocTypeShipment_ID,Timestamp movementDate ) {
        this( order.getCtx(),0,order.get_TrxName());
        setClientOrg( order );
        setC_BPartner_ID( order.getC_BPartner_ID());
        setC_BPartner_Location_ID( order.getC_BPartner_Location_ID());    // shipment address
        setAD_User_ID( order.getAD_User_ID());
       //Añadido Conserti
        setDocStatus( DOCSTATUS_InProgress );
        //

        setPriorityRule(order.getPriorityRule());
        
        setM_Warehouse_ID( order.getM_Warehouse_ID());
        setIsSOTrx( order.isSOTrx());
        setMovementType( order.isSOTrx()
                         ?MOVEMENTTYPE_CustomerShipment
                         :MOVEMENTTYPE_VendorReceipts );

        if( C_DocTypeShipment_ID == 0 ) {
            C_DocTypeShipment_ID = DB.getSQLValue( null,"SELECT C_DocTypeShipment_ID FROM C_DocType WHERE C_DocType_ID=?",order.getC_DocType_ID());
        }

        setC_DocType_ID( C_DocTypeShipment_ID );

        // Default - Today

        if( movementDate != null ) {
            setMovementDate( movementDate );
        }

        setDateAcct( getMovementDate());

        // Copy from Order

        setC_Order_ID( order.getC_Order_ID());
        setDeliveryRule( order.getDeliveryRule());
        setDeliveryViaRule( order.getDeliveryViaRule());
        setM_Shipper_ID( order.getM_Shipper_ID());
        setFreightCostRule( order.getFreightCostRule());
        setFreightAmt( order.getFreightAmt());
        setSalesRep_ID( order.getSalesRep_ID());

        //

        setC_Activity_ID( order.getC_Activity_ID());
        setC_Campaign_ID( order.getC_Campaign_ID());
        setC_Charge_ID( order.getC_Charge_ID());
        setChargeAmt( order.getChargeAmt());

        //

        setC_Project_ID( order.getC_Project_ID());
        setDateOrdered( order.getDateOrdered());
        setDescription( order.getDescription());
        setPOReference( order.getPOReference());
        setSalesRep_ID( order.getSalesRep_ID());
        setAD_OrgTrx_ID( order.getAD_OrgTrx_ID());
        setUser1_ID( order.getUser1_ID());
        setUser2_ID( order.getUser2_ID());
    }    // MInOut

    /**
     * Constructor de la clase ...
     *
     *
     * @param invoice
     * @param C_DocTypeShipment_ID
     * @param movementDate
     * @param M_Warehouse_ID
     */

    public MInOut( MInvoice invoice,int C_DocTypeShipment_ID,Timestamp movementDate,int M_Warehouse_ID ) {
        this( invoice.getCtx(),0,invoice.get_TrxName());
        setClientOrg( invoice );
        setC_BPartner_ID( invoice.getC_BPartner_ID());
        setC_BPartner_Location_ID( invoice.getC_BPartner_Location_ID());    // shipment address
        setAD_User_ID( invoice.getAD_User_ID());
        //Añadido Conserti
        setDocStatus( DOCSTATUS_InProgress );
        //

        setM_Warehouse_ID( M_Warehouse_ID );
        setIsSOTrx( invoice.isSOTrx());
        setMovementType( invoice.isSOTrx()
                         ?MOVEMENTTYPE_CustomerShipment
                         :MOVEMENTTYPE_VendorReceipts );

        MOrder order = null;

        if( invoice.getC_Order_ID() != 0 ) {
            order = new MOrder( invoice.getCtx(),invoice.getC_Order_ID(),invoice.get_TrxName());
        }

        if( (C_DocTypeShipment_ID == 0) && (order != null) ) {
            C_DocTypeShipment_ID = DB.getSQLValue( null,"SELECT C_DocTypeShipment_ID FROM C_DocType WHERE C_DocType_ID=?",order.getC_DocType_ID());
        }

        if( C_DocTypeShipment_ID != 0 ) {
            setC_DocType_ID( C_DocTypeShipment_ID );
        } else {
            setC_DocType_ID();
        }

        // Default - Today

        if( movementDate != null ) {
            setMovementDate( movementDate );
        }

        setDateAcct( getMovementDate());

        // Copy from Invoice

        setC_Order_ID( invoice.getC_Order_ID());
        setSalesRep_ID( invoice.getSalesRep_ID());

        //

        setC_Activity_ID( invoice.getC_Activity_ID());
        setC_Campaign_ID( invoice.getC_Campaign_ID());
        setC_Charge_ID( invoice.getC_Charge_ID());
        setChargeAmt( invoice.getChargeAmt());

        //

        setC_Project_ID( invoice.getC_Project_ID());
        setDateOrdered( invoice.getDateOrdered());
        setDescription( invoice.getDescription());
        setPOReference( invoice.getPOReference());
        setAD_OrgTrx_ID( invoice.getAD_OrgTrx_ID());
        setUser1_ID( invoice.getUser1_ID());
        setUser2_ID( invoice.getUser2_ID());

        if( order != null ) {
            setDeliveryRule( order.getDeliveryRule());
            setDeliveryViaRule( order.getDeliveryViaRule());
            setM_Shipper_ID( order.getM_Shipper_ID());
            setFreightCostRule( order.getFreightCostRule());
            setFreightAmt( order.getFreightAmt());
        }
    }    // MInOut

    /**
     * Constructor de la clase ...
     *
     *
     * @param original
     * @param C_DocTypeShipment_ID
     * @param movementDate
     */

    public MInOut( MInOut original,int C_DocTypeShipment_ID,Timestamp movementDate ) {
        this( original.getCtx(),0,original.get_TrxName());
        setClientOrg( original );
        setC_BPartner_ID( original.getC_BPartner_ID());
        setC_BPartner_Location_ID( original.getC_BPartner_Location_ID());    // shipment address
        setAD_User_ID( original.getAD_User_ID());
        //Añadido Conserti
        setDocStatus( DOCSTATUS_InProgress );
        //

        setM_Warehouse_ID( original.getM_Warehouse_ID());
        setIsSOTrx( original.isSOTrx());
        setMovementType( original.getMovementType());

        if( C_DocTypeShipment_ID == 0 ) {
            setC_DocType_ID( original.getC_DocType_ID());
        } else {
            setC_DocType_ID( C_DocTypeShipment_ID );
        }

        // Default - Today

        if( movementDate != null ) {
            setMovementDate( movementDate );
        }

        setDateAcct( getMovementDate());

        // Copy from Order

        setC_Order_ID( original.getC_Order_ID());
        setDeliveryRule( original.getDeliveryRule());
        setDeliveryViaRule( original.getDeliveryViaRule());
        setM_Shipper_ID( original.getM_Shipper_ID());
        setFreightCostRule( original.getFreightCostRule());
        setFreightAmt( original.getFreightAmt());
        setSalesRep_ID( original.getSalesRep_ID());

        //

        setC_Activity_ID( original.getC_Activity_ID());
        setC_Campaign_ID( original.getC_Campaign_ID());
        setC_Charge_ID( original.getC_Charge_ID());
        setChargeAmt( original.getChargeAmt());

        //

        setC_Project_ID( original.getC_Project_ID());
        setDateOrdered( original.getDateOrdered());
        setDescription( original.getDescription());
        setPOReference( original.getPOReference());
        setSalesRep_ID( original.getSalesRep_ID());
        setAD_OrgTrx_ID( original.getAD_OrgTrx_ID());
        setUser1_ID( original.getUser1_ID());
        setUser2_ID( original.getUser2_ID());
    }    // MInOut

    /** Descripción de Campos */

    private MInOutLine[] m_lines = null;

    /** Descripción de Campos */

    private MInOutConfirm[] m_confirms = null;

    /** Descripción de Campos */

    private MBPartner m_partner = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDocStatusName() {
        return MRefList.getListName( getCtx(),DOCSTATUS_AD_Reference_ID,getDocStatus());
    }    // getDocStatusName

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
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MInOut[" ).append( getID()).append( "-" ).append( getDocumentNo()).append( ",DocStatus=" ).append( getDocStatus()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MInOutLine[] getLines( boolean requery ) {
    	requery = true;
    	
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        ArrayList list = new ArrayList();
        //Modificada por ConSerti, para que no saque los cargos en los albaranes.
        //String    sql  = "SELECT * FROM M_InOutLine WHERE M_InOut_ID=?  ORDER BY Line"; //Original.
        log.fine("En MInOut.getLines");
        String    sql  = "SELECT * FROM M_InOutLine WHERE M_InOut_ID=? and c_charge_id is null ORDER BY Line";
        //Fin Modificación.
        PreparedStatement pstmt = null;
        ResultSet         rs    = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_InOut_ID());
            rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInOutLine( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            rs = null;
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,get_TrxName(),ex );
            list = null;

            // throw new DBException(ex);

        } finally {
            try {
                if( rs != null ) {
                    rs.close();
                }

                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( SQLException e ) {
            }
        }

        pstmt = null;
        rs    = null;

        //

        if( list == null ) {
            return null;
        }

        //

        m_lines = new MInOutLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getMInOutLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInOutLine[] getLines() {
        return getLines( true );
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MInOutConfirm[] getConfirmations( boolean requery ) {
        if( (m_confirms != null) &&!requery ) {
            return m_confirms;
        }

        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM M_InOutConfirm WHERE M_InOut_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_InOut_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInOutConfirm( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getConfirmations",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_confirms = new MInOutConfirm[ list.size()];
        list.toArray( m_confirms );

        return m_confirms;
    }    // getConfirmations

    /**
     * Descripción de Método
     *
     *
     * @param otherShipment
     * @param counter
     * @param setOrder
     *
     * @return
     */

    public int copyLinesFrom( MInOut otherShipment,boolean counter,boolean setOrder, boolean isReverseCopy ) {
        if( isProcessed() || isPosted() || (otherShipment == null) ) {
            return 0;
        }

        MInOutLine[] fromLines = otherShipment.getLines( true );
        int          count     = 0;

        for( int i = 0;i < fromLines.length;i++ ) {
            MInOutLine line = new MInOutLine( this );

            line.set_TrxName( get_TrxName());
            PO.copyValues( fromLines[ i ],line,getAD_Client_ID(),getAD_Org_ID());
            line.setM_InOut_ID( getM_InOut_ID());
            line.setM_InOutLine_ID( 0 );    // new

            // Reset

            if( !setOrder ) {
                line.setC_OrderLine_ID( 0 );
            }

            // FIXME: De ahora en adelante, no se le borra la instancia de atributos a la linea
            //        ya que cuando ésta se guarda, se valida si hay que asignarle un valor o no.
            //        Si se le elimina dicho valor a la linea, puede que falle al guardarla.
            
            // if( !counter ) {
            //     line.setM_AttributeSetInstance_ID( 0 );
            // }

            // line.setS_ResourceAssignment_ID(0);

            line.setRef_InOutLine_ID( 0 );
            line.setIsInvoiced( false );

            //

            line.setConfirmedQty( Env.ZERO );
            line.setPickedQty( Env.ZERO );
            line.setScrappedQty( Env.ZERO );
            line.setTargetQty( Env.ZERO );

            // Set Locator based on header Warehouse

            if( getM_Warehouse_ID() != otherShipment.getM_Warehouse_ID()) {
                line.setM_Locator_ID( 0 );
                line.setM_Locator_ID( Env.ZERO );
            }

            //

            if( counter ) {
                line.setRef_InOutLine_ID( fromLines[ i ].getM_InOutLine_ID());

                if( fromLines[ i ].getC_OrderLine_ID() != 0 ) {
                    MOrderLine peer = new MOrderLine( getCtx(),fromLines[ i ].getC_OrderLine_ID(),get_TrxName());

                    if( peer.getRef_OrderLine_ID() != 0 ) {
                        line.setC_OrderLine_ID( peer.getRef_OrderLine_ID());
                    }
                }
            }

            //
            if(isReverseCopy){
            	line.setQty(line.getQtyEntered().negate());
            }
            
            line.setProcessed( false );

            if( line.save( get_TrxName())) {
                count++;
            }

            // Cross Link

            if( counter ) {
                fromLines[ i ].setRef_InOutLine_ID( line.getM_InOutLine_ID());
                fromLines[ i ].save( get_TrxName());
            }
        }

        if( fromLines.length != count ) {
            log.log( Level.SEVERE,"copyLinesFrom - Line difference - From=" + fromLines.length + " <> Saved=" + count );
        }

        return count;
    }    // copyLinesFrom

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

        String sql = "UPDATE M_InOutLine SET Processed='" + ( processed
                ?"Y"
                :"N" ) + "' WHERE M_InOut_ID=" + getM_InOut_ID();
        int noLine = DB.executeUpdate( sql,get_TrxName());

        m_lines = null;
        log.fine( processed + " - Lines=" + noLine );
    }    // setProcessed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MBPartner getBPartner() {
        if( m_partner == null ) {
            m_partner = new MBPartner( getCtx(),getC_BPartner_ID(),get_TrxName());
        }

        return m_partner;
    }    // getPartner

    /**
     * Descripción de Método
     *
     *
     * @param DocBaseType
     */

    public void setC_DocType_ID( String DocBaseType ) {
        String sql = "SELECT C_DocType_ID FROM C_DocType " + "WHERE AD_Client_ID=? AND DocBaseType=?" + " AND IsSOTrx='" + ( isSOTrx()
                ?"Y"
                :"N" ) + "' " + "ORDER BY IsDefault DESC";
        int C_DocType_ID = DB.getSQLValue( null,sql,getAD_Client_ID(),DocBaseType );

        if( C_DocType_ID <= 0 ) {
            log.log( Level.SEVERE,"Not found for AC_Client_ID=" + getAD_Client_ID() + " - " + DocBaseType );
        } else {
            log.fine( "DocBaseType=" + DocBaseType + " - C_DocType_ID=" + C_DocType_ID );
            setC_DocType_ID( C_DocType_ID );

            boolean isSOTrx = MDocType.DOCBASETYPE_MaterialDelivery.equals( DocBaseType );

            setIsSOTrx( isSOTrx );
        }
    }    // setC_DocType_ID

    /**
     * Descripción de Método
     *
     */

    public void setC_DocType_ID() {
        if( isSOTrx()) {
            setC_DocType_ID( MDocType.DOCBASETYPE_MaterialDelivery );
        } else {
            setC_DocType_ID( MDocType.DOCBASETYPE_MaterialReceipt );
        }
    }    // setC_DocType_ID

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

        // Set Locations

        MBPartnerLocation[] locs = bp.getLocations( false );

        if( locs != null ) {
            for( int i = 0;i < locs.length;i++ ) {
                if( locs[ i ].isShipTo()) {
                    setC_BPartner_Location_ID( locs[ i ].getC_BPartner_Location_ID());
                }
            }

            // set to first if not set

            if( (getC_BPartner_Location_ID() == 0) && (locs.length > 0) ) {
                setC_BPartner_Location_ID( locs[ 0 ].getC_BPartner_Location_ID());
            }
        }

        if( getC_BPartner_Location_ID() == 0 ) {
            log.log( Level.SEVERE,"Has no To Address: " + bp );
        }

        // Set Contact

        MUser[] contacts = bp.getContacts( false );

        if( (contacts != null) && (contacts.length > 0) ) {    // get first User
            setAD_User_ID( contacts[ 0 ].getAD_User_ID());
        }
    }                                                          // setBPartner

    /**
     * Descripción de Método
     *
     */

    public void createConfirmation() {
        MDocType dt   = MDocType.get( getCtx(),getC_DocType_ID());
        boolean  pick = dt.isPickQAConfirm();
        boolean  ship = dt.isShipConfirm();

        // Nothing to do

        if( !pick &&!ship ) {
            log.fine( "No need" );

            return;
        }

        // Create Both .. after each other

        if( pick && ship ) {
            boolean         havePick      = false;
            boolean         haveShip      = false;
            MInOutConfirm[] confirmations = getConfirmations( false );

            for( int i = 0;i < confirmations.length;i++ ) {
                MInOutConfirm confirm = confirmations[ i ];

                if( MInOutConfirm.CONFIRMTYPE_PickQAConfirm.equals( confirm.getConfirmType())) {
                    if( !confirm.isProcessed())    // wait intil done
                    {
                        log.fine( "Unprocessed: " + confirm );

                        return;
                    }

                    havePick = true;
                } else if( MInOutConfirm.CONFIRMTYPE_ShipReceiptConfirm.equals( confirm.getConfirmType())) {
                    haveShip = true;
                }
            }

            // Create Pick

            if( !havePick ) {
                MInOutConfirm.create( this,MInOutConfirm.CONFIRMTYPE_PickQAConfirm,false );

                return;
            }

            // Create Ship

            if( !haveShip ) {
                MInOutConfirm.create( this,MInOutConfirm.CONFIRMTYPE_ShipReceiptConfirm,false );

                return;
            }

            return;
        }

        // Create just one

        if( pick ) {
            MInOutConfirm.create( this,MInOutConfirm.CONFIRMTYPE_PickQAConfirm,true );
        } else if( ship ) {
            MInOutConfirm.create( this,MInOutConfirm.CONFIRMTYPE_ShipReceiptConfirm,true );
        }
    }    // createConfirmation

    /**
     * Descripción de Método
     *
     *
     * @param M_Warehouse_ID
     */

    public void setM_Warehouse_ID( int M_Warehouse_ID ) {
        if( M_Warehouse_ID == 0 ) {
            log.severe( "Ignored - Cannot set AD_Warehouse_ID to 0" );

            return;
        }

        super.setM_Warehouse_ID( M_Warehouse_ID );

        //

        MWarehouse wh = MWarehouse.get( getCtx(),getM_Warehouse_ID());

        if( wh.getAD_Org_ID() != getAD_Org_ID()) {
            log.warning( "M_Warehouse_ID=" + M_Warehouse_ID + ", Overwritten AD_Org_ID=" + getAD_Org_ID() + "->" + wh.getAD_Org_ID());
            setAD_Org_ID( wh.getAD_Org_ID());
        }
    }    // setM_Warehouse_ID

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Warehouse Org

        if( newRecord ) {
            MWarehouse wh = MWarehouse.get( getCtx(),getM_Warehouse_ID());

        	//Validar número de doccumento para remitos de salida
        	if (!isSOTrx() && existsDocNumber(false)) {
        		log.saveError("ShipmentNumberAlredyExists", "");
        		return false;
        	}
            
            
            if( wh.getAD_Org_ID() != getAD_Org_ID()) {
                log.saveError( "WarehouseOrgConflict","" );

                return false;
            }
        }

        // Shipment - Needs Order

//        if( isSOTrx() && (getC_Order_ID() == 0) && !isProcessed() && !isIgnoreShipmentOrder()) {
//            log.saveError( "FillMandatory",Msg.translate( getCtx(),"C_Order_ID" ));
//
//            return false;
//        }
        
        // Esto se comenta ya que en un principio se pedía la validación, pero
		// luego se decidió que no se implementa
		// ------------------------------------------------------------------------
//        // Para Ventas
//        if(isSOTrx()){
//			// Verificar que no exista la misma cadena de nro de documento
//			// preimpreso, lo cual no debe permitir guardar
//			String docNo = getDocNoInOutByStrColumnCondition(getCtx(),
//					"PrePrinted_DocNo", getPrePrinted_DocNo(),
//					getID(), getC_DocType_ID(), get_TrxName());
//			if(docNo != null){
//				log.saveError("", Msg.getMsg(getCtx(), "PrePrintedInOutDocNoWarning",
//						new Object[] { docNo }));
//				return false;
//			}
//        }
		// ------------------------------------------------------------------------

        // No permitir guardar un remito que posee un pedido no facturado cuando
		// la regla de envío de mercadería es Después de Facturación
        MBPartner bpartner = new MBPartner(getCtx(), getC_BPartner_ID(), get_TrxName());
		if (isSOTrx() 
				&& getMovementType().endsWith("-")
				&& !Util.isEmpty(getC_Order_ID(), true)
				&& bpartner.getDeliveryRule() != null
				&& bpartner.getDeliveryRule().equals(
						MBPartner.DELIVERYRULE_AfterInvoicing)
				&& (getDocStatus().equals(DOCSTATUS_Drafted) || getDocStatus()
						.equals(DOCSTATUS_InProgress))) {
			// Verificar si el pedido cargado se encuentra dentro de la consulta
			// de pedidos de facturas
			String sql = "SELECT coalesce(count(*),0) as exist FROM (SELECT c_order_id FROM C_Invoice WHERE c_bpartner_id = ? AND "
					+ getInvoiceOrderFilter(this)
					+ ") as orders WHERE orders.c_order_id = ?";
			Integer exist = DB.getSQLValue(get_TrxName(), sql,
					bpartner.getID(), getC_Order_ID());
			if(exist <= 0){
				log.saveError( "InOutDeliveryRuleOrder","" );
                return false;
			}
        }
        
        return true;
    }    // beforeSave

    private boolean ignoreShipmentOrder = false;
    
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

        if( is_ValueChanged( "AD_Org_ID" )) {
            String sql = "UPDATE M_InOutLine ol" + " SET AD_Org_ID =" + "(SELECT AD_Org_ID" + " FROM M_InOut o WHERE ol.M_InOut_ID=o.M_InOut_ID) " + "WHERE M_InOut_ID=" + getC_Order_ID();
            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( "Lines -> #" + no );
        }

        return true;
    }    // afterSave

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

        return engine.processIt( processAction,getDocAction(),log );
    }    // process



    /** Descripción de Campos */

    private boolean m_justPrepared = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean unlockIt() {
        log.info( toString());
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
    	log.fine("En prepareIt de MInout");
        log.info( toString());
        m_processMsg = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_BEFORE_PREPARE );

        if( m_processMsg != null ) {
            return DocAction.STATUS_Invalid;
        }
        
        // Cuando está activado el control de cierres de almacenes y es un remito
        // de salida se actualiza la fecha en caso de que la misma sea menor a la
        // fecha actual. Esto es necesario para que se pueda completar el documento
        // pasando la validación de cierre. (además es lógico que la fecha real del
        // remito sea igual a la fecha en que se completó el mismo, y no a la fecha
        // en que se creó).
		// Si existe un cierre de almacén en estado En Proceso, significa que
		// tenemos un cierre de almacén reactivado, en ese caso no debemos
		// modificar la fecha del comprobante
        if (MWarehouseClose.isWarehouseCloseControlActivated() 
        		&& isSOTrx() 
        		&& getMovementDate().compareTo(Env.getDate()) < 0
				&& !MWarehouseClose.existsWarehouseCloseInProgress(getCtx(),
						getM_Warehouse_ID(), get_TrxName())) {
        	
        	setMovementDate(Env.getDate());
        	setDateAcct(Env.getDate());
        }
        
        MDocType dt = MDocType.get( getCtx(),getC_DocType_ID());

        // Std Period open?

		if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType(),
				getM_Warehouse_ID(), 
				isTPVInstance())) {
            if (MWarehouseClose.isWarehouseCloseControlActivated()) {
            	m_processMsg = "@PeriodClosedOrWarehouseClosed@";
            } else {
            	m_processMsg = "@PeriodClosed@";
            }

            return DocAction.STATUS_Invalid;
        }

        // Credit Check

        if( !isSOTrx()) {
            if (existsDocNumber(true)) {
        		m_processMsg = "@ShippmentNumberAlredyExists@";
        		return DocAction.STATUS_Invalid;

        	}
        }

        // -----------------------------------------------------------------------
		// IMPORTANTE: Estas porciones de código se deben dejar antes de las
		// validaciones de stock y cantidades en las líneas del pedido
        // -----------------------------------------------------------------------
		// Para artículos bienes de uso que posean conjunto de atributos
		// asignado y la línea no tenga una instancia del conjunto de atributos,
		// se debe eliminar esa línea y crear tantas líneas como cantidad se
		// haya ingresado, asignarle una instancia de conjunto de atributos y
		// la referencia a la línea de pedido/factura
        // -----------------------------------------------------------------------
        if(!isTPVInstance && !isSOTrx()){
        	manageAssetLines();
        }
        // -----------------------------------------------------------------------
        
        // Verificar que la cantidad de las líneas de la devolución del cliente 
        // no sobrepasen la cantidad entregada (delivered) de cada línea de pedido
		// No se puede reservar mayor cantidad a la pedida, esto se da
		// para remitos de salida con líneas negativas 
        // No se puede entregar más mercadería de la pedida
        if(isSOTrx()){
        	MInOutLine[] lines = getLines(true);
        	MInOutLine line;
        	MOrderLine orderLine;
        	for (int i = 0; i < lines.length; i++) {
        		line = lines[i];
				if(line.getC_OrderLine_ID() != 0){
					orderLine = new MOrderLine(getCtx(), line.getC_OrderLine_ID(), get_TrxName());
					if ((getMovementType().endsWith("+") && line
							.getMovementQty().signum() >= 0)
							|| (!getMovementType().endsWith("+") && line
									.getMovementQty().signum() < 0)) {
						// La cantidad de la devolución no debe superar la cantidad
						// entregada del pedido 
						if(line.getMovementQty().abs().compareTo(orderLine.getQtyDelivered()) > 0){
							m_processMsg = "@CustomerReturnExceedsQtyDelivered@";
				    		return DocAction.STATUS_Invalid;
						}
					}
				}
			}
        }
        
        // Borra las líneas cuya cantidad sea 0 (cero)
        deleteEmptyLines();
                
        //
        
    	if ( isSOTrx() && !isTPVInstance && ! suggestMasiAndBreakLines() ) {
    		m_processMsg = "@NotEnoughStocked@";
    		return DocAction.STATUS_InProgress;
    	}
    	
    	//
    	
    	log.fine("Va a entrar en la función de instancia de copiar las lineas (BreakLinesToUnit) y c_order_id="+getC_Order_ID());

    	if (BreakLinesToUnit() != 0) {
        	// JOptionPane.showMessageDialog( null, "Por favor, introduzca los n�meros de serie y complete el albar�n ",null, JOptionPane.INFORMATION_MESSAGE );
        	m_processMsg = "Por favor, introduzca los números de serie y complete el albarán ";
        	return DocAction.STATUS_InProgress;
    	}
    	
        // Lines

        MInOutLine[] lines = getLines( true );

        if( (lines == null) || (lines.length == 0) ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }
        
		// Verificar que las lineas tengan asociado un producto.
        if(isSOTrx()){
        	MInOutLine[] lineas = getLines(true);
        	MInOutLine linea;
        	for (int i = 0; i < lineas.length; i++) {
        		linea = lineas[i];
				if(linea.getM_Product_ID() == 0){
					m_processMsg = "@LineNoProduct@";
		    		return DocAction.STATUS_Invalid;
				}
			}
        }

        MOrder order = new MOrder(getCtx(), getC_Order_ID(), get_TrxName());
        
        // Verificar que el pedido no esté cerrado
        
        if(MOrder.STATUS_Closed.equals(order.getDocStatus())){
        	
        	m_processMsg = "@DeliveryWithOrderClose@";
        	
        	return DocAction.STATUS_Invalid;
        }
        
        // Mandatory Attributes

        for( int i = 0;i < lines.length;i++ ) {
        	
        	checkLinePriority(lines[i], order);
        	
            if( lines[ i ].getM_AttributeSetInstance_ID() != 0 ) {
                continue;
            }

            MProduct product = lines[ i ].getProduct();

            if( product != null ) {
                int M_AttributeSet_ID = product.getM_AttributeSet_ID();

                if( M_AttributeSet_ID != 0 ) {
                    MAttributeSet mas = MAttributeSet.get( getCtx(),M_AttributeSet_ID);

                    if( (mas != null) && (( isSOTrx() && mas.isMandatory()) || ( !isSOTrx() && mas.isMandatoryAlways()))) {
                        m_processMsg = "@M_AttributeSet_ID@ @IsMandatory@";

                        return DocAction.STATUS_Invalid;
                    }
                }
            }
        }
        
        checkMaterialPolicy();    // set MASI
        createConfirmation();
      
        m_justPrepared = true;

        if( !DOCACTION_Complete.equals( getDocAction())) {
            setDocAction( DOCACTION_Complete );
        }

        return DocAction.STATUS_InProgress;
    }    // prepareIt

	/**
	 * Gestiona las líneas de bienes de uso, eliminando las líneas que poseen
	 * artículos bienes de uso que no tengan instancia de conjunto de atributos
	 * seteada y además que el artículo propio contenga un conjunto de atributos
	 * seteado. De esa manera, se crea la cantidad de líneas automáticamente con
	 * cada instancia del artículo por la cantidad agregada en cada línea,
	 * eliminando la línea original sin instancia de conjunto de atributos.
	 * 
	 * @return resultado de la operación
	 */
    private CallResult manageAssetLines(){
    	CallResult result = new CallResult();
		List<PO> assetLines = PO
				.find(getCtx(),
						X_M_InOutLine.Table_Name,
						"(m_attributesetinstance_id is null OR m_attributesetinstance_id = 0) AND m_product_id IN (select m_product_id from m_product where producttype = 'A' and m_attributeset_id is not null and m_attributeset_id <> 0) AND m_inout_id = ?",
						new Object[]{getID()}, null, get_TrxName());
		MInOutLine inOutLine = null, newInOutLine = null;
		MAttributeSetInstance instance = null;
		MOrderLine orderLine = null;
		MInvoiceLine invoiceLine = null;
		AssetDTO assetDTO = null;
		Integer productID;
		BigDecimal cost;
		for (PO line : assetLines) {
			List<MInOutLine> newLines = new ArrayList<MInOutLine>();
			inOutLine = (MInOutLine)line;
			int cant = inOutLine.getMovementQty().abs().intValue();
			for (int i = 0; i < cant; i++) {
				newInOutLine = new MInOutLine(this);
				PO.copyValues(inOutLine, newInOutLine);
				newInOutLine.setQty(new BigDecimal(newInOutLine
						.getMovementQty().signum()));
				// Crear la clase con los datos necesarios para crear la instancia
				assetDTO = new AssetDTO();
				// Obtener la línea de pedido relacionada
				if(!Util.isEmpty(inOutLine.getC_OrderLine_ID(), true)){
					orderLine = new MOrderLine(getCtx(), inOutLine.getC_OrderLine_ID(), get_TrxName());
					productID = orderLine.getM_Product_ID();
					cost = orderLine.getPriceEntered();
				}
				else {
					invoiceLine = new MInvoiceLine(getCtx(), inOutLine.getC_InvoiceLine_ID(), get_TrxName());
					productID = invoiceLine.getM_Product_ID();
					cost = invoiceLine.getPriceEntered();
				}

				assetDTO.setCost(cost);
				assetDTO.setDateFrom(getMovementDate());
				assetDTO.setProductID(productID);
				assetDTO.setCtx(getCtx());
				assetDTO.setTrxName(get_TrxName());
				try{
					// Crear la instancia de atributos
					instance = MAttributeSetInstance.createAssetAttributeInstance(assetDTO);
				} catch(Exception e){
					result.setMsg(e.getMessage(), true);
					return result;
				}
				// Asociarla a la línea de remito
				newInOutLine.setM_AttributeSetInstance_ID(instance.getID());
				newLines.add(newInOutLine);
			}
			// Eliminar la línea anterior
			if(!inOutLine.delete(true, get_TrxName())){
				result.setMsg(CLogger.retrieveErrorAsString(), true);
				return result;
			}
			// Guardar todas las líneas nuevas
			for (MInOutLine mInOutLine : newLines) {
				if(!mInOutLine.save()){
					result.setMsg(CLogger.retrieveErrorAsString(), true);
					return result;
				}
			}
		}        
		return result;
    }
    
    
    private boolean existsDocNumber(boolean completed) {
		String sql = "SELECT * FROM " + Table_Name + " WHERE AD_Client_ID = ? ";
		if (completed) {
				sql += " AND DocStatus IN ('CO', 'CL') ";
		}
		sql +=	" AND IsActive = 'Y' AND DocumentNo = ?" +
		" AND isSOTrx = 'N' ";
		CPreparedStatement pstm = DB.prepareStatement(sql, get_TrxName());
		
		try {
			pstm.setInt(1, getAD_Client_ID());
			pstm.setString(2, getDocumentNo());
			ResultSet rs = pstm.executeQuery();
			return rs.next();
			
		} catch (SQLException e) {
			log.saveError("Error validating document number", e);
			return false;
		}
	}

	/**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean approveIt() {
        log.info( toString());
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
        log.info( toString());
        setIsApproved( false );

        return true;
    }    // rejectIt

    /**
	 * La instancia de conjunto de atributos es por art�culo. Si la linea tiene m�s de un art�culo, hay que 
	 * partir las lineas en tantas como art�culos haya para poder asignarle a cada uno de ellos individualmente
	 * los diferentes atributos.
	 * 
	 * Si tengo una linea con cinco modulos de RAM, hay que partir la linea en otras cuatro m�s, para poder
	 * indicar, por ejemplo, los numeros de serie de cada modulo individualmente.
	 * 
	 * Solo se invoca si el articulo de la linea tiene atributos de instancia. 
     *
     * @return La cantidad de lineas originalmes divididas.
     */
    private int BreakLinesToUnit() {
    	int M_InOut_ID = this.getM_InOut_ID();
    	int cantidadLineasDivididas = 0;
    	int cantidadLineasSinConjunto = 0;
    	
    	log.fine("BreakUpProductionLines - BreakLines");
    	try {
    		StringBuffer sql;

    		if (M_InOut_ID == 0) //Si no hay orden de produccion seleccionada..
    			return 0;

    		BigDecimal x = (BigDecimal)DB.getSQLObject(get_TrxName(), "select max(line) from m_inoutline where M_InOut_ID = ?", new Object[]{M_InOut_ID});
    		Integer max_line = (x != null) ? x.intValue() : 10;

    		sql = new StringBuffer( "SELECT m_inoutline_id,c_orderline_id,description,m_inout_id,m_locator_id" +
    				",m_product_id,c_uom_id,movementqty,isinvoiced,m_attributesetinstance_id" +
    				",isdescription,confirmedqty,pickedqty,scrappedqty,targetqty,ref_inoutline_id" +
    				",processed,qtyentered,c_charge_id,isselected from m_inoutline" +
    				" where m_inout_id="+M_InOut_ID );	
    		PreparedStatement pstmt = null;
    		ResultSet rs;
    		pstmt = DB.prepareStatement( sql.toString(), get_TrxName());
    		rs    = pstmt.executeQuery();
    		BigDecimal quantity=null;

    		while(rs.next()){
    			quantity = rs.getBigDecimal("qtyentered");
    			int Line = max_line + 10;

    			boolean isInstanceAttr = MAttributeSet.ProductNeedsInstanceAttribute(rs.getInt("m_product_id"), get_TrxName());
    			
    			int attributeSetInstanceID = rs.getInt("m_attributesetinstance_id");
    			boolean setInstanceReusable ;
    			boolean instanceUniquePerUnit = false;
    			
    			if (attributeSetInstanceID == 0 || !isInstanceAttr) {
    				setInstanceReusable = true;
    			} else {
   					MAttributeSet mas = MAttributeSet.get(getCtx(), DB.getSQLValue(get_TrxName(), "SELECT M_AttributeSet_ID FROM M_AttributeSetInstance WHERE M_AttributeSetInstance_ID = " + attributeSetInstanceID));
   					mas.set_TrxName(get_TrxName());
   					instanceUniquePerUnit = mas.isInstanceUniquePerUnit();
   					setInstanceReusable = !instanceUniquePerUnit;
    			}
    			
    			if (isInstanceAttr && instanceUniquePerUnit && quantity.compareTo(BigDecimal.ONE) > 0) {
    				
    				cantidadLineasDivididas++;
    				
    				log.fine("La línea de pedido tiene mas de un articulo, quantity=" + quantity);
    				
    				for (int i=1;i<quantity.intValue();i++) {
    					MInOutLine aux = new MInOutLine(Env.getCtx(),0,get_TrxName());
    					// MOrderLine aux = new MOrderLine(Env.getCtx(),rs.getInt(1),null);
    					// aux_new= MOrder.co

    					aux.setM_InOut_ID(M_InOut_ID);
    					aux.setC_OrderLine_ID(rs.getInt("c_orderline_id"));
    					aux.setM_Locator_ID(rs.getInt("m_locator_id"));
    					aux.setM_Product_ID(rs.getInt("m_product_id"));
    					aux.setC_UOM_ID(rs.getInt("c_uom_id"));
    					aux.setDescription(rs.getString("description"));
    					aux.setMovementQty(BigDecimal.ONE); // rs.getBigDecimal("movementqty"));
    					aux.setIsInvoiced(rs.getBoolean("isinvoiced"));
    					
    					// FIXME: No se le puede asignar las instancia de atributos!!
    					// aux.setM_AttributeSetInstance_ID(rs.getInt("m_attributesetinstance_id"));
    					
    					if (setInstanceReusable) {
    						aux.setM_AttributeSetInstance_ID(attributeSetInstanceID);
    					} else {
    						cantidadLineasSinConjunto++;
    					}
    					
    					aux.setIsDescription(rs.getBoolean("isdescription"));
    					aux.setConfirmedQty(rs.getBigDecimal("confirmedqty"));
    					aux.setPickedQty(rs.getBigDecimal("pickedqty"));
    					aux.setScrappedQty(rs.getBigDecimal("scrappedqty"));
    					aux.setTargetQty(rs.getBigDecimal("targetqty"));
    					aux.setRef_InOutLine_ID(rs.getInt("ref_inoutline_id"));
    					aux.setProcessed(rs.getBoolean("processed"));
    					aux.setQtyEntered(BigDecimal.ONE);
    					aux.setC_Charge_ID(rs.getInt("c_charge_id"));


    					// FIXME: Por que no se setea el Warehouse?
    					// aux.setM_Warehouse_ID(rs.getInt("m_warehouse_id"));


    					// aux.setPriceEntered(rs.getBigDecimal("priceentered"));
    					aux.setLine(Line);
    					Line+=10;
    					aux.save();
    				}
    				
    				MInOutLine aux = new MInOutLine(Env.getCtx(), rs.getInt(1), get_TrxName());
    				
    				aux.setQty(BigDecimal.ONE);
    				
    				aux.save();
    				
    				// int noLine = DB.executeUpdate( "UPDATE m_inoutline set qtyentered=1.0, MovementQty=1.0 where m_inoutline_id="+rs.getInt(1) ,get_TrxName());
    				// log.fine("Numero de lineas actualizadas:"+noLine);
    			}
    		}

    		rs.close();
    		pstmt.close();
    		pstmt = null;
    	} catch( Exception e ) {
    		log.log( Level.SEVERE,"BreakUpProductionLines - BreakLines " + e );
    		cantidadLineasDivididas = -1;
    		cantidadLineasSinConjunto = -1;
    	}

    	return cantidadLineasSinConjunto;
    }

    private RecommendedAtributeInstance[] getRecommendedAttributeSetInstance(int M_Product_ID, BigDecimal Qty, int M_Warehouse_ID) {
    	RecommendedAtributeInstance[] rai = null;

    	try {
	    	RecommendedAtributeInstance[] raiMulti = MProduct.getRecommendedAtributeInstance(M_Product_ID, Qty, false, M_Warehouse_ID, 0, false);
	    	RecommendedAtributeInstance[] raiUni = MProduct.getRecommendedAtributeInstance(M_Product_ID, Qty, true, M_Warehouse_ID, 0, false);
	    	
	    	if (raiUni.length == 1)
	    		rai = raiUni;
	    	else
	    		rai = raiMulti;
	    	
    	} catch (SQLException e) {
    		log.log(Level.SEVERE, "MInOut.getRecommendedAttributeSetInstance", e);
    	}
    	
    	return rai;
    }
    
    /** Actualiza y, de ser necesario, parte las lineas del albarán de acuerdo al parametro raiRaw. 
     * 
     * Se espera que las instancias de atributos recomendadas provistas en raiRaw posean M_Locator_ID; para ello
     * se deberá invocar al metodo MProduct.getRecommendedAtributeInstance con el parametro "agrupar" en false. 
     * 
     * @param M_Product_ID
     * @param M_Warehouse_ID
     * @param raiRaw
     * @return
     */
    private boolean updateMasiAndBreakLinesWith(int M_Product_ID, int M_Warehouse_ID, RecommendedAtributeInstance[] raiRaw) {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	Vector<MInOutLine> lines = new Vector<MInOutLine>();
    	Vector<RecommendedAtributeInstance> rai = new Vector<RecommendedAtributeInstance>(Arrays.asList(raiRaw));
    	
    	try {
    		/*
    		 * Busco todas las lineas que respetan la condicion. Esto está hecho así ya 
    		 * que puede haber varias lineas parecidas. 
    		 *  
    		 */
    		String sql = " SELECT m_inoutline_id FROM m_inoutline " +
    			" INNER JOIN M_Locator ON (M_Locator.M_Locator_ID=M_InOutLine.M_Locator_ID) " +
    			" WHERE M_InOut_ID = ? AND M_Product_ID = ? AND M_Locator.M_Warehouse_ID = ? " + 
    			" ORDER BY c_orderline_id ";
    		ps = DB.prepareStatement(sql, get_TrxName());
    		
    		ps.setInt(1, getM_InOut_ID());
    		ps.setInt(2, M_Product_ID);
    		ps.setInt(3, M_Warehouse_ID);
    		
    		rs = ps.executeQuery();
    		
    		while (rs.next()) {
    			lines.add(new MInOutLine(getCtx(), rs.getInt(1), get_TrxName()));
    		}
    		
    		if (lines.size() == 0)
    			return false;
    		
    		/*
    		 * Necesito que la cantidad de lineas de albaran que cumplen la condicion
    		 * sea igual a la cantidad de sugerencias. Entonces instancio la cantidad de 
    		 * registros que se necesitan para igualar las cantidades.
    		 * 
    		 * En el segundo caso (lineas > rai) se podrian eliminar las lineas sobrantes
    		 * en vez de crear nuevos rai de Qty=1, pero por el momento no prefiero hacerlo.
    		 * 
    		 */
    		if (lines.size() < rai.size()) {
    			/*
    			 * Faltan nuevas lineas de I/O: Divido las lineas de Albaran para igualar a la cantidad de lineas de RAI
    			 */
    			
            	int sourceIoLine = 0;
            	BigDecimal sourceIoLineCount = BigDecimal.ZERO;
            	
        		sourceIoLineCount = lines.get(sourceIoLine).getQtyEntered();
        		
        		int faltan = rai.size() - lines.size();
    			
    			for (int i = 0; i < faltan; i++) {
    				MInOutLine line = new MInOutLine(this);
    				
    				while (sourceIoLineCount.compareTo(BigDecimal.ONE) <= 0) {
    					sourceIoLine++;
    					sourceIoLineCount = lines.get(sourceIoLine).getQtyEntered();
    				}
    				
    				sourceIoLineCount = sourceIoLineCount.subtract(BigDecimal.ONE);
    				
    				PO.copyValues(lines.get(sourceIoLine), line);
    				
    				lines.add(line);
    			}
    		} else if (lines.size() > rai.size()) {
    			/*
    			 * Faltan nuevos RAI: Divido los RAI que hay para igualar a la cantidad de lineas de Albaran
    			 */
    			int faltan = lines.size() - rai.size();
    			int x = 0;
    			
    			for (int i = 0; i < faltan; i++) {
    				while (rai.get(x).getQtyOnHand().compareTo(BigDecimal.ONE) <= 0)
    					x++;
    				
    				rai.get(x).setQtyOnHand(rai.get(x).getQtyOnHand().subtract(BigDecimal.ONE));
    				rai.add(new RecommendedAtributeInstance(rai.get(x).getM_AtributeInstance_ID(), BigDecimal.ONE, rai.get(x).getM_Locator_ID()));
    			}
    		}
    		
    		/*
    		 * Establezco las nuevas propiedades de las lineas de los albaranes.
    		 */
			for (int i = rai.size() - 1; i >= 0 ; --i) {
				MInOutLine line = lines.get(i);
				
				line.setM_Locator_ID(rai.get(i).getM_Locator_ID());
				line.setM_Product_ID(M_Product_ID);
				line.setQty(rai.get(i).getQtyOnHand());
				line.setM_AttributeSetInstance_ID(rai.get(i).getM_AtributeInstance_ID());
				
				if (!line.save())
					return false;
			}
    		
    	} catch (SQLException e) {
    		log.log(Level.SEVERE, "", e);
    		return false;
    	} finally {
    		try {
    			if (ps != null) ps.close();
    		} catch (SQLException e) { }
    		try {
    			if (rs != null) rs.close();
    		} catch (SQLException e) { }
    	}
    	
    	return true;
    }
    
    /**
     * Busca lineas del albarán que tengan productos que haya que asignarles instancia de conjunto de 
     * atributos. Si no tienen asignada una instancia, busca una sugerencia de acuerdo a la disponibilidad
     * de stock y de la fecha de vencimiento, y en caso de que no se pueda satisfacer el pedido en una 
     * unica linea, se la parte en las que sea necesario. 
     * 
     * @return
     */
    private boolean suggestMasiAndBreakLines() {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		String sql = " select M_Product_ID, M_Warehouse_ID, SUM(QtyEntered), COUNT(*) " +
				" from m_inoutline " +
				" INNER JOIN M_Locator ON (M_Locator.M_Locator_ID=M_InOutLine.M_Locator_ID) " +  
				" WHERE M_InOut_ID = ? AND M_AttributeSetInstance_ID = 0 " +
				" GROUP BY M_Product_ID, M_Warehouse_ID " +
				" ORDER BY SUM(QtyEntered) DESC ";
    		ps = DB.prepareStatement(sql, get_TrxName());
    		
    		ps.setInt(1, getM_InOut_ID());
    		
    		rs = ps.executeQuery();
    		
    		while (rs.next()) {
    			int M_Product_ID = rs.getInt(1);
    			int M_Warehouse_ID = rs.getInt(2);
    			
    			if (MAttributeSet.ProductNeedsInstanceAttribute(M_Product_ID, get_TrxName())) {
    				RecommendedAtributeInstance[] rai = getRecommendedAttributeSetInstance(M_Product_ID, rs.getBigDecimal(3), M_Warehouse_ID);
    				
    				if (rai.length == 0)
    					return false;
    				
    				if ( ! updateMasiAndBreakLinesWith(M_Product_ID, M_Warehouse_ID, rai) )
    					return false;
    			}
    		}
    		
    	} catch (SQLException e) {
    		log.log(Level.SEVERE, "", e);
    		return false;
    	} finally {
    		try {
    			if (ps != null) ps.close();
    		} catch (SQLException e) { }
    		try {
    			if (rs != null) rs.close();
    		} catch (SQLException e) { }
    	}
    	
    	return true;
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */
    
    public String completeIt() {
    	log.fine("En completeIt");
        // Re-Check
    	
    	// mostrar aviso
    	if (!MOrder.showRegionWarning(this.getC_BPartner_Location_ID()))
    		return "";
    	
    	//
    	
    	MInOutLine[] lines2 = getLines( true );
    	BigDecimal convertedQty = null;
    	
    	//
    	MDocType docType = MDocType.get(getCtx(), getC_DocType_ID(), get_TrxName());
    	
    	for( int lineIndex2 = 0;lineIndex2 < lines2.length ;lineIndex2++ ) {
            MInOutLine sLine2   = lines2[ lineIndex2 ];

            /*
             * Modificar los m�todos Completeit de C_Order y M_InOut para que compruebe de nuevo que la mercanc�a 
             * tiene conjunto de atributos, si es necesario que los lleve, antes de completar.
             */
            if (sLine2.getM_AttributeSetInstance_ID() == 0 && sLine2.shouldSetAttrSetInstance()) {
            	m_processMsg = "La linea " + sLine2.getLine() + " no posee una instancia de conjunto de atributos; debera especificarse el valor manualmente.";
            	return DocAction.STATUS_InProgress;
            }
            
    	}
    	
        if( !m_justPrepared ) {
            String status = prepareIt();

            if( !DocAction.STATUS_InProgress.equals( status )) {
                return status;
            }
        }

        // Outstanding (not processed) Incoming Confirmations ?

        MInOutConfirm[] confirmations = getConfirmations( true );

        for( int i = 0;i < confirmations.length;i++ ) {
            MInOutConfirm confirm = confirmations[ i ];

            if( !confirm.isProcessed()) {
                if( MInOutConfirm.CONFIRMTYPE_CustomerConfirmation.equals( confirm.getConfirmType())) {
                    continue;
                }

                //

                m_processMsg = "Open @M_InOutConfirm_ID@: " + confirm.getConfirmTypeName() + " - " + confirm.getDocumentNo();
                setDocStatus(STATUS_InProgress);
                return DocAction.STATUS_InProgress;
            }
        }

        // Implicit Approval

        if( !isApproved()) {
            approveIt();
        }
        
        String     MovementType = getMovementType();

        log.info( toString());

        StringBuffer info = new StringBuffer();

        // For all lines
        BigDecimal onHand    = Env.ZERO;
        BigDecimal Reserved    = Env.ZERO;
        MInOutLine[] lines = getLines( true );
        Timestamp minGuaranteeDate = Env.getContextAsDate( getCtx(),"#Date" );
        for( int lineIndex = 0;lineIndex < lines.length;lineIndex++ ) {
            MInOutLine sLine   = lines[ lineIndex ];
            sLine.setM_Warehouse_ID(getM_Warehouse_ID());
            MProduct   product = sLine.getProduct();
            
            //Añadido para comprobar que existe stock suficiente para el albaran
            log.fine("La comprobacion de que la regla de albaranado:"+this.get_Value("DeliveryRule"));
            
            // if (!this.get_Value("DeliveryRule").toString().equalsIgnoreCase("F") && isSOTrx()) {
			if (!getDeliveryRule().equalsIgnoreCase(DELIVERYRULE_Force)
					&& !getDeliveryRule().equalsIgnoreCase(
							DELIVERYRULE_Force_AfterInvoicing) && isSOTrx()
					&& MovementType.endsWith("-")
					&& sLine.getQtyEntered().signum() > 0) {
            	//Si la regla de albaranado es distinta de F y ademas es un albar�n de salida
            	
	            MProductCategory pc = MProductCategory.get( Env.getCtx(),product.getM_Product_Category_ID(), get_TrxName());
	            String MMPolicy = pc.getMMPolicy();
	            // Qty & Type
	            MStorage[] storages = MStorage.getWarehouse( getCtx(),sLine.getM_Warehouse_ID(),sLine.getM_Product_ID(),sLine.getM_AttributeSetInstance_ID(),product.getM_AttributeSet_ID(),sLine.getM_AttributeSetInstance_ID() == 0,minGuaranteeDate,MClient.MMPOLICY_FiFo.equals( MMPolicy ),get_TrxName());
	
	            for ( int j = 0;j < storages.length;j++ ) {
	                MStorage storage = storages[ j ];
	
	                onHand = onHand.add( storage.getQtyOnHand());
	                Reserved = Reserved.add(storage.getQtyReserved());
	            }
	            
	            if (onHand.compareTo(Reserved) < 0) {
	            	// JOptionPane.showMessageDialog(null,"No hay stock suficiente para completar el albaran","No existe stock",JOptionPane.ERROR_MESSAGE);
	            	m_processMsg = "No hay stock suficiente para completar el albaran";
	            	return DocAction.STATUS_Invalid;
	            }
            }
            
//            String     MovementType = getMovementType();
            BigDecimal Qty          = sLine.getMovementQty();

            if( MovementType.charAt( 1 ) == '-' ) {    // C- Customer Shipment - V- Vendor Return
                Qty = Qty.negate();
            }

            BigDecimal QtySO = Env.ZERO;
            BigDecimal QtyPO = Env.ZERO;

            // Update Order Line

            /*
             * TPV Performance: no actualizar las cantidades via modelo, usar UPDATEs directos 
             */
//            MOrderLine ol = null;
        	BigDecimal ol_qtyOrdered = null, ol_qtyReserved = null, ol_qtyDelivered = null, ol_qtyTransferred = null, ol_qtyInvoiced = null, ol_qtyReturned = null;
        	int ol_attsetinstanceID = -1;
        	boolean ol_ok = false;
        	Timestamp ol_dateDelivered = null;
        	
            if( sLine.getC_OrderLine_ID() != 0 ) {
                
            	try    	{
	                String sql = " SELECT qtyOrdered, qtyReserved, qtyDelivered, M_AttributeSetInstance_ID, qtyTransferred, qtyInvoiced, coalesce((select sum(iol.movementqty) as qty from c_orderline as ol2 inner join m_inoutline as iol on iol.c_orderline_id = ol2.c_orderline_id inner join m_inout as io on io.m_inout_id = iol.m_inout_id inner join c_doctype as dt on dt.c_doctype_id = io.c_doctype_id where ol.c_orderline_id = ol2.c_orderline_id AND dt.doctypekey = 'DC' and io.docstatus IN ('CL','CO')),0) as ol_qtyReturned " +
	                				" FROM C_OrderLine ol WHERE ol.C_OrderLine_ID = " + sLine.getC_OrderLine_ID();
	                PreparedStatement stmt =  DB.prepareStatement(sql , get_TrxName());
	                ResultSet rs = stmt.executeQuery();
	                if (rs.next())
	                {
	                	ol_ok = true;
	                	ol_qtyOrdered = rs.getBigDecimal(1);
	                	ol_qtyReserved = rs.getBigDecimal(2);
	                	ol_qtyDelivered = rs.getBigDecimal(3);
	                	ol_attsetinstanceID = rs.getInt(4);
	                	ol_qtyTransferred = rs.getBigDecimal(5);
	                	ol_qtyInvoiced = rs.getBigDecimal(6);
						ol_qtyReturned = docType.isAllowDeliveryReturned() ? BigDecimal.ZERO
								: rs.getBigDecimal(7);
	                }
            	}
            	catch (Exception e)	{
            		e.printStackTrace();
                    m_processMsg = "Could not retrieve Order Line";
                    return DocAction.STATUS_Invalid;
            	}

                
                if( isSOTrx()) {
                    QtySO = sLine.getMovementQty();
					if ((MovementType.endsWith("-") && QtySO.signum() >= 0)
							|| (MovementType.endsWith("+") && QtySO.signum() < 0)){
	                    if (ol_qtyOrdered.subtract(ol_qtyDelivered).subtract(ol_qtyTransferred).subtract(ol_qtyReturned).subtract(sLine.getMovementQty().abs()).compareTo(Env.ZERO) < 0) {
	                    	m_processMsg = Msg.translate(getCtx(), "MovementGreaterThanOrder");
	                    	if(ol_qtyReturned.compareTo(BigDecimal.ZERO) > 0){
	                    		m_processMsg += ". "+Msg.getMsg(getCtx(), "AdditionQtyReturned");
	                    	}
	                    	return DocAction.STATUS_Invalid;
	                    }
	                    
	                    // Para las reglas de envío Después de Facturación y Forzar
						// - Después de Facturación, no se debe entregar más de lo
						// facturado
						if (getDeliveryRule().equals(
								MInOut.DELIVERYRULE_AfterInvoicing)
								|| getDeliveryRule().equals(
										MInOut.DELIVERYRULE_Force_AfterInvoicing)) {
							if(QtySO.add(ol_qtyDelivered).add(ol_qtyTransferred).add(ol_qtyReturned).compareTo(ol_qtyInvoiced) > 0){
								m_processMsg = Msg.translate(getCtx(), "MovementGreaterThanInvoiced");
								if(ol_qtyReturned.compareTo(BigDecimal.ZERO) > 0){
		                    		m_processMsg += ". "+Msg.getMsg(getCtx(), "AdditionQtyReturned");
		                    	}
		                    	return DocAction.STATUS_Invalid;
							}
						}
                    }
                    else{
                    	QtySO = QtySO.negate();
                    }
                } else {
                    QtyPO = sLine.getMovementQty();
                    if (MovementType.endsWith("-")) {
                    	QtyPO = QtyPO.negate();
                    }
                }
            }

            log.info( "Line=" + sLine.getLine() + " - Qty=" + sLine.getMovementQty());

            // Stock Movement - Counterpart MOrder.reserveStock

            if( (product != null) && product.isStocked()) {
                log.fine( "Material Transaction" );

                MTransaction mtrx                               = null;
                int          reservationAttributeSetInstance_ID = sLine.getM_AttributeSetInstance_ID();

                if( ol_ok ) {
                    reservationAttributeSetInstance_ID = ol_attsetinstanceID;
                }

                //

//                if( sLine.getM_AttributeSetInstance_ID() == 0 ) {
                    MInOutLineMA mas[] = MInOutLineMA.get( getCtx(),sLine.getM_InOutLine_ID(),get_TrxName());

                    for( int j = 0;j < mas.length;j++ ) {
                        MInOutLineMA ma    = mas[ j ];
                        BigDecimal   QtyMA = ma.getMovementQty();

                        if( MovementType.charAt( 1 ) == '-' ) {    // C- Customer Shipment - V- Vendor Return
                            QtyMA = QtyMA.negate();
                        }

                        BigDecimal QtySOMA = Env.ZERO;
                        BigDecimal QtyPOMA = Env.ZERO;

                        if( sLine.getC_OrderLine_ID() != 0 ) {
                            if( isSOTrx()) {
                            	if(MovementType.endsWith("-")){
                            		QtySOMA = ma.getMovementQty();
                            	}
                            	else{
                            		QtySOMA = ma.getMovementQty().negate();
                            	}
								// Si es un remito creado desde el TPV entonces
								// no se tienen en cuenta las cantidades
								// reservadas para actualizar ya que las mismas
								// no fueron seteadas en el pedido (performance
								// TPV)
                            	if (isTPVInstance()) {
                            		QtySOMA = BigDecimal.ZERO;
                            	}
                            } else {
                                QtyPOMA = ma.getMovementQty();
                                if (MovementType.endsWith("-")) {
                                	QtyPOMA = QtyPOMA.negate();
                                }

                            }
                        }

                        // Update Storage - see also VMatch.createMatchRecord

                        if( !MStorage.add( getCtx(),getM_Warehouse_ID(),sLine.getM_Locator_ID(),sLine.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),reservationAttributeSetInstance_ID,QtyMA,QtySOMA.negate(),QtyPOMA.negate(),get_TrxName())) {
                            m_processMsg = "Cannot correct Inventory (MA)";

                            return DocAction.STATUS_Invalid;
                        }

                        // Create Transaction

                        mtrx = new MTransaction( getCtx(),MovementType,sLine.getM_Locator_ID(),sLine.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),QtyMA,getMovementDate(),get_TrxName());
                        /*
                         * Added by Matías Cap - Disytel
                         * 
                         * Setear la organización de la transacción con la organización que tiene la línea, 
                         * esto se realiza porque se seteaba tal organización con el contexto y si en el 
                         * contexto tenemos AD_Org_ID = 0, no funciona ya que el remito tiene que pertenecer 
                         * a una organización. 
                         */
                        mtrx.setAD_Org_ID(sLine.getAD_Org_ID());
                        /*
                         * Fin adición Matías Cap - Disytel
                         */
                        mtrx.setM_InOutLine_ID( sLine.getM_InOutLine_ID());

                        if( !mtrx.save()) {
                            m_processMsg = Msg.translate(getCtx(), "CouldNoCreateMaterialTransaction");

                            return DocAction.STATUS_Invalid;
                        }
                    }
//                }

                // sLine.getM_AttributeSetInstance_ID() != 0

                if( mtrx == null ) {

                    // Fallback: Update Storage - see also VMatch.createMatchRecord

					// La cantidad reservada debe tener el mismo signo que la
					// cantidad a modificar del stock
					// Se supone que si se agrega stock, se agrega stock
					// reservado y si se saca stock, se saca de reservado. Esto,
					// en el caso que esté asociado a un pedido
					QtySO = Qty.compareTo(BigDecimal.ZERO) >= 0 ? QtySO.abs()
							: QtySO.abs().negate();
                    if( !MStorage.add( getCtx(),getM_Warehouse_ID(),sLine.getM_Locator_ID(),sLine.getM_Product_ID(),sLine.getM_AttributeSetInstance_ID(),reservationAttributeSetInstance_ID,Qty,QtySO,QtyPO.negate(),get_TrxName())) {
                        m_processMsg = "Cannot correct Inventory";

                        return DocAction.STATUS_Invalid;
                    }

                    // FallBack: Create Transaction

                    mtrx = new MTransaction( getCtx(),MovementType,sLine.getM_Locator_ID(),sLine.getM_Product_ID(),sLine.getM_AttributeSetInstance_ID(),Qty,getMovementDate(),get_TrxName());
                    /*
                     * Added by Matías Cap - Disytel
                     * 
                     * Setear la organización de la transacción con la organización que tiene la línea, 
                     * esto se realiza porque se seteaba tal organización con el contexto y si en el 
                     * contexto tenemos AD_Org_ID = 0, no funciona ya que el remito tiene que pertenecer 
                     * a una organización. 
                     */
                    mtrx.setAD_Org_ID(sLine.getAD_Org_ID());
                    /*
                     * Fin adición Matías Cap - Disytel
                     */
                    mtrx.setM_InOutLine_ID( sLine.getM_InOutLine_ID());

                    if( !mtrx.save()) {
                        m_processMsg = "Could not create Material Transaction";

                        return DocAction.STATUS_Invalid;
                    }
                }

                // Correct Order Line

                if( ol_ok ) {    // other in VMatch.createMatchRecord
                	if(isSOTrx()){
                		if(MovementType.endsWith("+")){
                			ol_qtyReserved = ol_qtyReserved.add(sLine.getMovementQty()).add((ol_qtyTransferred));
                		}
                		else{
                			ol_qtyReserved = ol_qtyReserved.subtract( sLine.getMovementQty()).subtract(ol_qtyTransferred);
                		}
						// Si es un remito creado desde el TPV entonces se
						// anulan las cantidades reservadas a setear en el
						// pedido ya que al completar el MOrder esta
						// funcionalidad de reserva es ignorada para mejorar la
						// performance del TPV (es decir que el campo de
						// QtyReserved de la línea de pedido siempre va a estar
						// en 0)
                    	if (isTPVInstance()) {
                    		ol_qtyReserved = BigDecimal.ZERO;
                    	}
                	}
                	else{
                		if(MovementType.endsWith("+")){
                			ol_qtyReserved = ol_qtyReserved.subtract( sLine.getMovementQty()).subtract((ol_qtyTransferred));
                		}
                		else{
                			ol_qtyReserved = ol_qtyReserved.add( sLine.getMovementQty()).add((ol_qtyTransferred));
                		}
                	}
                } 
            }                                                   // stock movement

            // Update Order Line

            if( ol_ok && isSOTrx()) {
//                if( isSOTrx())                                  // PO is done by Matching
//                {
                    ol_qtyDelivered = ol_qtyDelivered.subtract( Qty );
                    if(MovementType.endsWith("-")){
                    	ol_dateDelivered = getMovementDate();    // overwrite=last
                    }
//                }

                try {
	                String updateSQL = " UPDATE C_OrderLine " +
	                					" SET qtyOrdered = ?, qtyReserved = ?, qtyDelivered = ?, M_AttributeSetInstance_ID = ?, dateDelivered = ? " +
	                					" WHERE C_OrderLine_ID = " + sLine.getC_OrderLine_ID();
	                PreparedStatement updateStmt = DB.prepareStatement( updateSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, get_TrxName());
	                
	                updateStmt.setBigDecimal(1, ol_qtyOrdered);
	                updateStmt.setBigDecimal(2, ol_qtyReserved);
	                updateStmt.setBigDecimal(3, ol_qtyDelivered);
	                updateStmt.setInt(4, ol_attsetinstanceID);
	                updateStmt.setTimestamp(5, ol_dateDelivered);
	
	                if( updateStmt.executeUpdate() != 1) {
	                    m_processMsg = "Could not update Order Line";
	
	                    return DocAction.STATUS_Invalid;
	                } else {
	                    log.fine( "OrderLine -> Reserved=" + ol_qtyReserved + ", Delivered=" + ol_qtyDelivered);
	                }
                }
                catch (Exception e) {
                	e.printStackTrace();
                    m_processMsg = "Could not update Order Line";
                    return DocAction.STATUS_Invalid;
                }
            }

            // Create Asset for SO

            if( (product != null) && isSOTrx() && product.isCreateAsset()) {
                log.fine( "Asset" );
                info.append( "@A_Asset_ID@: " );

                int noAssets = sLine.getMovementQty().intValue();

                if( !product.isOneAssetPerUOM()) {
                    noAssets = 1;
                }

                for( int i = 0;i < noAssets;i++ ) {
                    if( i > 0 ) {
                        info.append( " - " );
                    }

                    int deliveryCount = i + 1;

                    if( noAssets == 1 ) {
                        deliveryCount = 0;
                    }

                    MAsset asset = new MAsset( this,sLine,deliveryCount );

                    if( !asset.save( get_TrxName())) {
                        m_processMsg = "Could not create Asset";

                        return DocAction.STATUS_Invalid;
                    }

                    info.append( asset.getValue());
                }
            }    // Asset

            // Matching

            if( !isSOTrx() && (sLine.getM_Product_ID() != 0) ) {
                BigDecimal matchQty = sLine.getMovementQty();

                // Invoice - Receipt Match (requires Product)

                MInvoiceLine iLine = MInvoiceLine.getOfInOutLine( sLine );

                if( (iLine != null) && (iLine.getM_Product_ID() != 0) ) {
                    MMatchInv[] matches = MMatchInv.get( getCtx(),sLine.getM_InOutLine_ID(),iLine.getC_InvoiceLine_ID(),get_TrxName());

                    if( (matches == null) || (matches.length == 0) ) {
                        MMatchInv inv = new MMatchInv( iLine,getMovementDate(),matchQty );

                        if( !inv.save( get_TrxName())) {
                            m_processMsg = "Could not create Inv Matching";

                            return DocAction.STATUS_Invalid;
                        }
                    }
                }

                // Link to Order

                if( sLine.getC_OrderLine_ID() != 0 ) {
                    log.fine( "PO Matching" );

                    // Ship - PO

                    MMatchPO po = new MMatchPO( sLine,getMovementDate(),matchQty );

                    if( !po.save( get_TrxName())) {
                        m_processMsg = "Could not create PO Matching";

                        return DocAction.STATUS_Invalid;
                    }
                } else    // No Order - Try finding links via Invoice
                {

                    // Invoice has an Order Link

                    if( (iLine != null) && (iLine.getC_OrderLine_ID() != 0) ) {

                        // Invoice is created before  Shipment

                        log.fine( "PO(Inv) Matching" );

                        // Ship - Invoice

                        MMatchPO po = new MMatchPO( iLine,getMovementDate(),matchQty );

                        po.setM_InOutLine_ID( sLine.getM_InOutLine_ID());

                        if( !po.save( get_TrxName())) {
                            m_processMsg = "Could not create PO(Inv) Matching";

                            return DocAction.STATUS_Invalid;
                        }
                    }
                }    // No Order
            }        // PO Matching
        }            // for all lines

        // Counter Documents

        MInOut counter = createCounterDoc();

        if( counter != null ) {
            info.append( " - @CounterDoc@: @M_InOut_ID@=" ).append( counter.getDocumentNo());
        }

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            m_processMsg = valid;

            return DocAction.STATUS_Invalid;
        }

        m_processMsg = info.toString();
        setProcessed( true );
        setDocAction( DOCACTION_Close );

        return DocAction.STATUS_Completed;
    }    // completeIt

    /**
     * Descripción de Método
     *
     */

    private void checkMaterialPolicy() {
    	//Cambiado por JorgeV - Disytel para mejorar la performance 
    	int no = 0; //MInOutLineMA.deleteInOutMA( getM_InOut_ID(),get_TrxName());

        // Incoming Trx

        String  MovementType = getMovementType();
        boolean inTrx        = MovementType.endsWith("+");    // V+ Vendor Receipt, C+ Devolución de cliente
        boolean inTrxAux = inTrx;
        MClient client       = MClient.get( getCtx());

        // Check Lines
    	MInOutLine[] lines = getLines( false );
        for( int i = 0;i < lines.length;i++ ) {
            MInOutLine line     = lines[ i ];

            //Cambiado por JorgeV - Disytel para mejorar la performance
            no=MInOutLineMA.deleteInOutMALine(line,get_TrxName());
            if( no > 0 ) {
            	log.config( "Delete old InoutLineMA #" + no );
            }
         	// Fin modificacion         
            
            boolean    needSave = false;
            MProduct   product  = line.getProduct();
			// Es una transacción de entrada cuando el tipo de movimiento es de
			// entrada y la cantidad de la línea es positiva
            inTrxAux = inTrx && line.getMovementQty().signum() >= 0;
            
            // Need to have Location

            if( (product != null) && (line.getM_Locator_ID() == 0) ) {
                line.setM_Warehouse_ID( getM_Warehouse_ID());
                line.setM_Locator_ID( inTrxAux
                                      ?Env.ZERO
                                      :line.getMovementQty());    // default Locator
                needSave = true;
            }

            // Attribute Set Instance

            if( (product != null) && (line.getM_AttributeSetInstance_ID() == 0) ) {
                if( inTrxAux ) {
                	// FIXME Se asignaba un attributesetInstance nuevo cada vez que se completaba un remito de Compras.
                	// Modificar cuando se verifique si esto es correcto.
                	// Esto se realiza para la política de entrega de materiales
					// Cuando se deba chequear y verificar que la política
					// funcione correctamente, se debe buscar otro mecanismo
					// para registrar el histórico de registro de mercadería y
					// la posterior por FIFO o LIFO 
//                    MAttributeSetInstance asi = new MAttributeSetInstance( getCtx(),0,get_TrxName());
//
//                    asi.setClientOrg( getAD_Client_ID(),0 );
//                    asi.setM_AttributeSet_ID( product.getM_AttributeSet_ID());
//
//                    if( asi.save()) {
//                        line.setM_AttributeSetInstance_ID( asi.getM_AttributeSetInstance_ID());
//                        log.config( "New ASI=" + line );
//                        needSave = true;
//                    }
                } else    // Outgoing Trx
                {
                    MProductCategory pc = MProductCategory.get( getCtx(),product.getM_Product_Category_ID(), get_TrxName());
                    String MMPolicy = pc.getMMPolicy();

                    if( (MMPolicy == null) || (MMPolicy.length() == 0) ) {
                        MMPolicy = client.getMMPolicy();
                    }

                    //

                    MStorage[] storages = MStorage.getAll( getCtx(),line.getM_Product_ID(),line.getM_Locator_ID(),MClient.MMPOLICY_FiFo.equals( MMPolicy ),get_TrxName());
                    BigDecimal qtyToDeliver = line.getMovementQty();
                    BigDecimal qtyAux = BigDecimal.ZERO;
                    for( int ii = 0;ii < storages.length;ii++ ) {
                        MStorage storage = storages[ ii ];

                        if( storage.getQtyOnHand().compareTo( qtyToDeliver ) >= 0 ) {
                        	qtyAux = qtyToDeliver;
                        }
                        else{
                        	qtyAux = storage.getQtyOnHand();
                        }
                        
                        MInOutLineMA ma = new MInOutLineMA( line,storage.getM_AttributeSetInstance_ID(),qtyAux);

                        if( !ma.save()) {
                            ;
                        }
                        
                        qtyToDeliver = qtyToDeliver.subtract(qtyAux);

                        if( qtyToDeliver.signum() == 0 ) {
                            break;
                        }
                    }    // for all storages

                    // No AttributeSetInstance found for remainder

                    if( qtyToDeliver.signum() != 0 ) {
                        MInOutLineMA ma = new MInOutLineMA( line,0,qtyToDeliver );

                        if( !ma.save()) {
                            ;
                        }

                        log.fine( "##: " + ma );
                    }
                }    // outgoing Trx
            }        // attributeSetInstance

            if( needSave &&!line.save()) {
                log.severe( "NOT saved " + line );
            }
        }            // for all lines
    }                // checkMaterialPolicy

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private MInOut createCounterDoc() {

        // Is this a counter doc ?

        if( getRef_InOut_ID() != 0 ) {
            return null;
        }

        // Org Must be linked to BPartner

        MOrg org                  = MOrg.get( getCtx(),getAD_Org_ID());
        int  counterC_BPartner_ID = org.getLinkedC_BPartner_ID();

        if( counterC_BPartner_ID == 0 ) {
            return null;
        }

        // Business Partner needs to be linked to Org

        MBPartner bp               = new MBPartner( getCtx(),getC_BPartner_ID(),null );
        int       counterAD_Org_ID = bp.getAD_OrgBP_ID_Int();

        if( counterAD_Org_ID == 0 ) {
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

        MInOut counter = copyFrom( this,getMovementDate(),C_DocTypeTarget_ID,!isSOTrx(),true,get_TrxName(),true, false );

        //

        counter.setAD_Org_ID( counterAD_Org_ID );
        counter.setM_Warehouse_ID( counterOrgInfo.getM_Warehouse_ID());

        //

        counter.setBPartner( counterBP );

        // Refernces (Should not be required

        counter.setSalesRep_ID( getSalesRep_ID());
        counter.save( get_TrxName());

        String  MovementType = counter.getMovementType();
        boolean inTrx        = MovementType.charAt( 1 ) == '+';    // V+ Vendor Receipt

        // Update copied lines

        MInOutLine[] counterLines = counter.getLines( true );

        for( int i = 0;i < counterLines.length;i++ ) {
            MInOutLine counterLine = counterLines[ i ];

            counterLine.setClientOrg( counter );
            counterLine.setM_Warehouse_ID( counter.getM_Warehouse_ID());
            counterLine.setM_Locator_ID( 0 );
            counterLine.setM_Locator_ID( inTrx
                                         ?Env.ZERO
                                         :counterLine.getMovementQty());

            //

            counterLine.save( get_TrxName());
        }

        log.fine( counter.toString());

        // Document Action

        if( counterDT != null ) {
            if( counterDT.getDocAction() != null ) {
                counter.setDocAction( counterDT.getDocAction());
                counter.processIt( counterDT.getDocAction());
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
        log.info( toString());

        if( DOCSTATUS_Closed.equals( getDocStatus()) || DOCSTATUS_Reversed.equals( getDocStatus()) || DOCSTATUS_Voided.equals( getDocStatus())) {
            m_processMsg = "Document Closed: " + getDocStatus();

            return false;
        }

        // Not Processed

        if( DOCSTATUS_Drafted.equals( getDocStatus()) || DOCSTATUS_Invalid.equals( getDocStatus()) || DOCSTATUS_InProgress.equals( getDocStatus()) || DOCSTATUS_Approved.equals( getDocStatus()) || DOCSTATUS_NotApproved.equals( getDocStatus())) {

            // Set lines to 0

            MInOutLine[] lines = getLines( false );

            for( int i = 0;i < lines.length;i++ ) {
                MInOutLine line = lines[ i ];
                BigDecimal old  = line.getMovementQty();

                if( old.compareTo( Env.ZERO ) != 0 ) {
                    line.setQty( Env.ZERO );
                    line.addDescription( "Void (" + old + ")" );
                    line.save( get_TrxName());
                }
            }
        } else {
            return reverseCorrectIt();
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

    public boolean closeIt() {
        log.info( toString());
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

        // Deep Copy

        MInOut reversal = copyFrom( this,getMovementDate(),getC_DocType_ID(),isSOTrx(),false,get_TrxName(),true, true );

        if( reversal == null ) {
            m_processMsg = "Could not create Ship Reversal";

            return false;
        }

        // Reverse Line Qty

        // Matías Cap - Disytel
        // Estas líneas fueron comentadas ya que en la creación del documento
        // inverso de remitos de cliente, en el before me saltaba el error de que no 
        // tenía stock...pero el documento inverso al remito de salida 
        // realiza entrada de mercadería por lo que no tiene que realizar la verificación
        // de stock. 
        // La solución a esto fue pasarle un parámetro adicional al copyFrom
        // identificando que la transacción es la inversa, lo que produce es que 
        // al crear las líneas las crea con las cantidades negadas que 
        // es lo mismo que hace en las líneas comentadas debajo.
        
//        MInOutLine[] lines = reversal.getLines( false );
//
//        for( int i = 0;i < lines.length;i++ ) {
//            MInOutLine line = lines[ i ];
//
//            line.setQtyEntered( line.getQtyEntered().negate());
//            line.setMovementQty( line.getMovementQty().negate());
//
//            if( !line.save( get_TrxName())) {
//                m_processMsg = "Could not correct Ship Reversal Line";
//
//                return false;
//            }
//        }

        reversal.setC_Order_ID( getC_Order_ID());
        reversal.addDescription( "{->" + getDocumentNo() + ")" );

        //

        if( !reversal.processIt( DocAction.ACTION_Complete )) {
            m_processMsg = "@ReversalError@: " + reversal.getProcessMsg();

            return false;
        }
        
		// 01/04/13 FB. El remito inverso debe quedar en el mismo estado que el
		// original, es decir, anulado, dado que al dejarlo en estado cerrado
		// aparece en listados y procesos que tienen en cuenta este estado
		// Cerrado, y en realidad este remito reverso se debe ignorar en todos
		// los procesos y listados.
        // -> reversal.closeIt();
        // -> reversal.setDocStatus( DOCSTATUS_Closed );
        // -> reversal.setDocAction( DOCACTION_None );
        reversal.setDocStatus(DOCSTATUS_Voided);
        reversal.setDocAction(DOCACTION_None);
        if (!reversal.save( get_TrxName())) {
        	m_processMsg = "@ReversalError@: " + CLogger.retrieveErrorAsString();
        	return false;
        }

        //

        addDescription( "(" + reversal.getDocumentNo() + "<-)" );
        m_processMsg = reversal.getDocumentNo();
        setProcessed( true );
        setDocAction( DOCACTION_None );

        return true;
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

        return false;
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

        // : Total Lines = 123.00 (#1)

        sb.append( ":" )

        // .append(Msg.translate(getCtx(),"TotalLines")).append("=").append(getTotalLines())

        .append( " (#" ).append( getLines( false ).length ).append( ")" );

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
        return Env.ZERO;
    }    // getApprovalAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        return Env.getContextAsInt( getCtx(),"$C_Currency_ID " );
    }    // getC_Currency_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isComplete() {
        String ds = getDocStatus();

        return "CO".equals( ds ) || "CL".equals( ds );
    }    // isComplete
    
    private void checkLinePriority(MInOutLine line, MOrder orderHdr) {
    	
    	// FIXME: Define tambien una constante en el codigo y al hace la comprobacion, 
    	//        si no tiene nada configurado en la ficha del articulo, que tome esa 
    	//        constante como valor.
    	
    	int diasDeDiferencia = 7;
    	
    	if ( !getPriorityRule().equals(MInOut.PRIORITYRULE_High) && line.getM_AttributeSetInstance_ID() != 0 ) {
			MAttributeSetInstance masi = MAttributeSetInstance.get(getCtx(),
					line.getM_AttributeSetInstance_ID(), 0, get_TrxName());
            Timestamp mdd = masi.getDueDate();
            
            if (mdd != null) {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(mdd);
                cal1.add(Calendar.DAY_OF_MONTH, diasDeDiferencia);

                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(orderHdr.getDatePromised());
                
            	// if (mdd.compareTo(orderLine.getDatePromised()) <= 0)
                if (cal1.compareTo(cal2) >= 0)
                	setPriorityRule(MInOut.PRIORITYRULE_High);
            }
        }
    }


	/**
	 * @return the ignoreShipmentOrder
	 */
	public boolean isIgnoreShipmentOrder() {
		return ignoreShipmentOrder;
	}

	/**
	 * @param ignoreShipmentOrder the ignoreShipmentOrder to set
	 */
	public void setIgnoreShipmentOrder(boolean ignoreShipmentOrder) {
		this.ignoreShipmentOrder = ignoreShipmentOrder;
	}
	
	/**
	 * Borra las líneas vacías del remito. Una línea vacía es aquella
	 * cuya cantidad MovementQty es cero.
	 */
	private void deleteEmptyLines() {
		String sql = 
			"DELETE FROM M_InOutLine " +
			"WHERE M_InOut_ID = " + getM_InOut_ID() + " AND MovementQty = 0";
		DB.executeUpdate(sql, get_TrxName());
	}

	public boolean isTPVInstance() {
		return isTPVInstance;
	}

	public void setTPVInstance(boolean isTPVInstance) {
		this.isTPVInstance = isTPVInstance;
	}
	
	/**
	 * 
	 * @return el filtro de pedidos que se aplica al crear un remito a partir
	 *         de un pedido
	 */
	public static String getOrderFilter(MInOut inout) {
		boolean afterInvoicing = (inout.getDeliveryRule().equals(
				MInOut.DELIVERYRULE_AfterInvoicing) || inout.getDeliveryRule()
				.equals(MInOut.DELIVERYRULE_Force_AfterInvoicing))
				&& inout.getMovementType().endsWith("-");
		MDocType docType = MDocType.get(inout.getCtx(),
				inout.getC_DocType_ID(), inout.get_TrxName());
    	StringBuffer filter = new StringBuffer();
		// Si es un remito de ventas, solo se pueden elegir pedidos
		// que tengan al menos una línea cuya cantidad ordenada sea mayor
		// a la cantidad entregada. Es decir, pedidos que tienen algún
		// pendiente de entrega de mercadería o cualquier pedido en caso
		// de que el remito se trate de una devolución de cliente (en este caso
		// entra mercadería, con lo cual no se deben validar pendientes de
		// entrega).
		filter.append("C_Order.IsSOTrx='").append(inout.isSOTrx()?"Y":"N")
				.append("' AND ")
				.append("C_Order.DocStatus IN ('CL','CO') AND ")
				.append("(C_Order.C_Order_ID IN ")
				.append("(SELECT ol.C_Order_ID ")
				.append("FROM C_OrderLine ol ")
				.append("WHERE ")
				.append(afterInvoicing ? " ol.QtyInvoiced > "
						: " ol.QtyOrdered > ")
				.append(" ol.QtyDelivered+ol.QtyTransferred")
				.append(docType.isAllowDeliveryReturned()?"":"+coalesce((select sum(iol.movementqty) as qty from c_orderline as ol2 inner join m_inoutline as iol on iol.c_orderline_id = ol2.c_orderline_id inner join m_inout as io on io.m_inout_id = iol.m_inout_id inner join c_doctype as dt on dt.c_doctype_id = io.c_doctype_id where ol.c_orderline_id = ol2.c_orderline_id AND dt.doctypekey = 'DC' and io.docstatus IN ('CL','CO')),0)")
				.append(") ")
				.append("OR ")
				.append("(C_Order.IsSOTrx='Y' AND POSITION('+' IN '")
				.append(inout.getMovementType()).append("') > 0)")
				.append("OR ")
				.append("(C_Order.IsSOTrx='N' AND POSITION('-' IN '")
				.append(inout.getMovementType()).append("') > 0)")
				.append(")")
				.append(" AND ")
				.append("(C_Order.C_DocType_ID IN ")
				.append("(SELECT C_DocType_ID ")
				.append("FROM C_DocType ")
				.append("WHERE C_DocType.EnableInCreateFromShipment = 'Y'))");
		return filter.toString();
	}

	/**
	 * @return el filtro de facturas que se aplica al crear un remito a partir
	 *         de una factura
	 */
	public static String getInvoiceFilter(MInOut inout) {
		StringBuffer filter = new StringBuffer();

		filter.append("C_Invoice.IsSOTrx='")
				.append(inout.isSOTrx()?"Y":"N")
				.append("' AND ")
				.append("C_Invoice.DocStatus IN ('CL','CO') AND ")
				.append("C_Invoice.C_Invoice_ID IN (")
				.append("SELECT il.C_Invoice_ID ")
				.append("FROM C_InvoiceLine il ")
				.append("LEFT OUTER JOIN M_MatchInv mi ON (il.C_InvoiceLine_ID=mi.C_InvoiceLine_ID) ")
				.append("GROUP BY il.C_Invoice_ID,mi.C_InvoiceLine_ID,il.QtyInvoiced ")
				.append("HAVING (il.QtyInvoiced<>SUM(mi.Qty) AND mi.C_InvoiceLine_ID IS NOT NULL) OR mi.C_InvoiceLine_ID IS NULL) ");

		return filter.toString();
	}
	
	
	/**
	 * @return el filtro de facturas que se aplica al crear un remito a partir
	 *         de pedidos asociados a facturas
	 */
	public static String getInvoiceOrderFilter(MInOut inout) {
		StringBuffer filter = new StringBuffer();

		filter.append("C_Invoice.IsSOTrx='").append(inout.isSOTrx()?"Y":"N")
				.append("' AND ")
				.append("C_Invoice.DocStatus IN ('CL','CO') AND ")
				.append("C_Invoice.C_Order_ID IS NOT NULL AND ")
				.append("C_Invoice.C_Order_ID IN (")
				.append("SELECT C_Order.C_Order_ID ").append("FROM C_Order ")
				.append("WHERE (").append(getOrderFilter(inout)).append(")")
				.append(")");

		return filter.toString();
	}
	
}    // MInOut



/*
 *  @(#)MInOut.java   02.07.07
 * 
 *  Fin del fichero MInOut.java
 *  
 *  Versión 2.2
 *
 */
