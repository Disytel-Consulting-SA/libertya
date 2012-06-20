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
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.model.MAttributeSet;
import org.openXpertya.model.MAttributeSetInstance;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MProductCategory;
import org.openXpertya.model.MStorage;
import org.openXpertya.model.MUOM;
import org.openXpertya.model.PO;
import org.openXpertya.model.attribute.RecommendedAtributeInstance;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InOutGenerate extends SvrProcess {

    private boolean p_Selection = false;
    private int p_M_Warehouse_ID = 0;
    private int p_C_BPartner_ID = 0;
    private Timestamp p_DatePromised = null;
    private boolean p_IsUnconfirmedInOut = false;
    private String p_docAction = DocAction.ACTION_Complete;
    private boolean p_ConsolidateDocument = true;
    private boolean p_forceSingleAttributeInstance = false;
    private MInOut m_shipment = null;
    private int m_created = 0;
    private int m_line = 0;
    private Timestamp m_movementDate = null;
    private int m_lastC_BPartner_Location_ID = -1;
    private String m_sql = null;
    private boolean m_highPriority = false;
    private String retInfo = "";
    private int m_DocTypeId = 0;
    
    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "M_Warehouse_ID" )) {
                p_M_Warehouse_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "DatePromised" )) {
                p_DatePromised = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "Selection" )) {
                p_Selection = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "IsUnconfirmedInOut" )) {
                p_IsUnconfirmedInOut = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "ConsolidateDocument" )) {
                p_ConsolidateDocument = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "DocAction" )) {
                p_docAction = ( String )para[ i ].getParameter();
            } else if( name.equals( "forceSingleAttributeInstance" )) {
            	p_forceSingleAttributeInstance = "Y".equals( para[ i ].getParameter()); 
            } else if( name.equals( "C_DocType_ID" )) {
            	m_DocTypeId = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }

            // Login Date

            m_movementDate = Env.getContextAsDate( getCtx(),"#Date" );

            if( m_movementDate == null ) {
                m_movementDate = new Timestamp( System.currentTimeMillis());
            }

            // DocAction check

            if( !DocAction.ACTION_Complete.equals( p_docAction )) {
                p_docAction = DocAction.ACTION_Prepare;
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
        log.info( "Selection=" + p_Selection + ", M_Warehouse_ID=" + p_M_Warehouse_ID + ", C_BPartner_ID=" + p_C_BPartner_ID + ", Consolidate=" + p_ConsolidateDocument + ", IsUnconfirmed=" + p_IsUnconfirmedInOut + ", Movement=" + m_movementDate );

        if( p_M_Warehouse_ID == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@ @M_Warehouse_ID@" );
        }

        if( p_Selection )    // VInOutGen
        {
            m_sql = "SELECT * FROM C_Order " + "WHERE IsSelected='Y' AND DocStatus='CO' AND IsSOTrx='Y' AND AD_Client_ID=? " + "ORDER BY M_Warehouse_ID, PriorityRule, C_BPartner_ID, C_BPartner_Location_ID, C_Order_ID";
        } else {
            m_sql = "SELECT * FROM C_Order o " + "WHERE DocStatus='CO' AND IsSOTrx='Y'"

            // No Offer,POS

            + " AND o.C_DocType_ID IN (SELECT C_DocType_ID FROM C_DocType " + "WHERE DocBaseType='SOO' AND DocSubTypeSO NOT IN ('ON','OB','WR'))" + "     AND o.IsDropShip='N'"

            // Open Order Lines with Warehouse

            + " AND EXISTS (SELECT * FROM C_OrderLine ol " + "WHERE ol.M_Warehouse_ID=?" + " AND o.C_Order_ID=ol.C_Order_ID AND ol.QtyOrdered<>ol.QtyDelivered)";

            if( p_C_BPartner_ID != 0 ) {
                m_sql += " AND C_BPartner_ID=?";
            }

            if( p_DatePromised != null ) {
                m_sql += " AND TRUNC(DatePromised)<=?";
            }

            m_sql += " ORDER BY M_Warehouse_ID, PriorityRule, C_BPartner_ID, C_BPartner_Location_ID, C_Order_ID";
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( m_sql,get_TrxName());

            int index = 1;

            if( p_Selection ) {
                pstmt.setInt( index++,Env.getAD_Client_ID( getCtx()));
            } else {
                pstmt.setInt( index++,p_M_Warehouse_ID );

                if( p_C_BPartner_ID != 0 ) {
                    pstmt.setInt( index++,p_C_BPartner_ID );
                }

                if( p_DatePromised != null ) {
                    pstmt.setTimestamp( index++,p_DatePromised );
                }
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,m_sql,e );
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
        MClient client = MClient.get( getCtx());
        log.fine("Entro en el generate de inoutgenerate");
        try {
            ResultSet rs = pstmt.executeQuery();

            while( rs.next())    // Order
            {
                MOrder order = new MOrder( getCtx(),rs,get_TrxName());
                
                log.fine( "check: " + order +",docTypeee:"+order.getC_DocType_ID() );

                // New Header Shipment Location

                if( !p_ConsolidateDocument || ( (m_shipment != null) && (m_shipment.getC_BPartner_Location_ID() != order.getC_BPartner_Location_ID()))) {
                   log.fine("En el primer if de generate(), inoutgenerate");
                	completeShipment();
                }

                //

                Timestamp minGuaranteeDate = m_movementDate;
                boolean   completeOrder    = MOrder.DELIVERYRULE_CompleteOrder.equals( order.getDeliveryRule());

                //

                String where = " AND M_Warehouse_ID=" + p_M_Warehouse_ID;
                //Modificado por ConSerTi
                if( !p_IsUnconfirmedInOut ) {
                	// Order DocType = "Pedido" -> 1000338 -> Only On Wide!!
                	MDocType odt = MDocType.get(getCtx(), order.getC_DocType_ID()); 
                	if(odt.getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder) && odt.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_StandardOrder)){
                		 where = " AND m_product_id NOT IN(SELECT m_product_id from M_Product WHERE m_product_category_id IN(SELECT m_product_category_id FROM M_Product_Category"+
                		 		" WHERE m_product_gamas_id=(SELECT m_product_gamas_id FROM M_Product_Gamas WHERE name LIKE 'Servicios'))) AND NOT EXISTS (SELECT * FROM M_InOutLine iol" + 
                		 		" INNER JOIN M_InOut io ON (iol.M_InOut_ID=io.M_InOut_ID) " + "WHERE iol.C_OrderLine_ID=C_OrderLine.C_OrderLine_ID AND io.DocStatus IN ('IP','WC'))";
                	}else{
                		where = " AND NOT EXISTS (SELECT * FROM M_InOutLine iol" + " INNER JOIN M_InOut io ON (iol.M_InOut_ID=io.M_InOut_ID) " + "WHERE iol.C_OrderLine_ID=C_OrderLine.C_OrderLine_ID AND io.DocStatus IN ('IP','WC'))";
                	}
                }
                //Fin modificado, inicialmente dentro del if( !p_IsUnconfirmedInOut ) el where del else
                // Deadlock Prevention - Order by M_Product_ID

                MOrderLine[] lines = order.getLines( where,"ORDER BY C_BPartner_Location_ID, M_Product_ID" );

                for( int i = 0;i < lines.length;i++ ) {
                    MOrderLine line = lines[ i ];

                    if( line.getM_Warehouse_ID() != p_M_Warehouse_ID ) {
                        continue;
                    }

                    log.fine( "check: " + line );

                    BigDecimal onHand    = Env.ZERO;
                    BigDecimal toDeliver = line.getQtyOrdered().subtract( line.getQtyDelivered());
                    MProduct product = line.getProduct();

                    // Nothing to Deliver

                    if( (product != null) && (toDeliver.compareTo( Env.ZERO ) == 0) ) {
                    	log.fine("Entro en product!=null y toDeliver.compareTo inoutgenerate");
                        continue;
                    }

                    // Check / adjust for confirmations

                    BigDecimal unconfirmedShippedQty = Env.ZERO;

                    if( p_IsUnconfirmedInOut && (product != null) && (toDeliver.compareTo( Env.ZERO ) != 0) ) {
                    	log.fine("Entro en p_IsUnconfirmedInOut y product!=null y toDeliver.compareTo de inoutgenerate");
                    	String where2 = "EXISTS (SELECT * FROM M_InOut io WHERE io.M_InOut_ID=M_InOutLine.M_InOut_ID AND io.DocStatus IN ('IP','WC'))";
                        MInOutLine[] iols = MInOutLine.getOfOrderLine( getCtx(),line.getC_OrderLine_ID(),where2,null );

                        for( int j = 0;j < iols.length;j++ ) {
                            unconfirmedShippedQty = unconfirmedShippedQty.add( iols[ j ].getMovementQty());
                        }

                        String logInfo = "Unconfirmed Qty=" + unconfirmedShippedQty + " - ToDeliver=" + toDeliver + "->";

                        toDeliver = toDeliver.subtract( unconfirmedShippedQty );
                        logInfo += toDeliver;

                        if( toDeliver.compareTo( Env.ZERO ) < 0 ) {
                            toDeliver = Env.ZERO;
                            logInfo   += " (set to 0)";
                        }

                        // Adjust On Hand

                        onHand = onHand.subtract( unconfirmedShippedQty );
                        log.fine( logInfo );
                    }

                    // Comments & lines w/o product & services
                    System.out.println(toDeliver);
                    if( ( (product == null) ||!product.isStocked()) && ( (line.getQtyOrdered().compareTo( Env.ZERO ) == 0    // comments
                            ) || (toDeliver.compareTo( Env.ZERO ) != 0) ) )    // lines w/o product
                            {
                    	
                        if( !MOrder.DELIVERYRULE_CompleteOrder.equals( order.getDeliveryRule())) {    // printed later
                            createLine( order,line,toDeliver,null,false );
                        }

                        continue;
                    }

                    // Stored Product

                    MProductCategory pc = MProductCategory.get( order.getCtx(),product.getM_Product_Category_ID(), get_TrxName());
                    String MMPolicy = pc.getMMPolicy();

                    if( (MMPolicy == null) || (MMPolicy.length() == 0) ) {
                        MMPolicy = client.getMMPolicy();
                    }

                    //

                    MStorage[] storages = MStorage.getWarehouse( getCtx(),line.getM_Warehouse_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstance_ID(),product.getM_AttributeSet_ID(),line.getM_AttributeSetInstance_ID() == 0,minGuaranteeDate,MClient.MMPOLICY_FiFo.equals( MMPolicy ),get_TrxName());

                    for( int j = 0;j < storages.length;j++ ) {
                        MStorage storage = storages[ j ];

                        onHand = onHand.add( storage.getQtyOnHand());
                    }

                    boolean fullLine = onHand.compareTo( toDeliver ) >= 0;

                    // Complete Order

                    if( completeOrder &&!fullLine ) {
                        log.fine( "Failed CompleteOrder - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + "), ToDeliver=" + toDeliver + " - " + line );
                        completeOrder = false;

                        break;
                    }

                    // Complete Line

                    else if( fullLine && MOrder.DELIVERYRULE_CompleteLine.equals( order.getDeliveryRule())) {
                        log.fine( "CompleteLine - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + ", ToDeliver=" + toDeliver + " - " + line );

                        //

                        createLine( order,line,toDeliver,storages,false );
                    }

                    // Availability

                    else if( MOrder.DELIVERYRULE_Availability.equals( order.getDeliveryRule()) && (onHand.compareTo( Env.ZERO ) > 0) ) {
                        BigDecimal deliver = toDeliver;

                        if( deliver.compareTo( onHand ) > 0 ) {
                            deliver = onHand;
                        }

                        log.fine( "Available - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + "), ToDeliver=" + toDeliver + ", Delivering=" + deliver + " - " + line );

                        //

                        createLine( order,line,deliver,storages,false );
                    }

                    // Force

                    else if( MOrder.DELIVERYRULE_Force.equals( order.getDeliveryRule())) {
                        BigDecimal deliver = toDeliver;

                        log.fine( "Force - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + "), ToDeliver=" + toDeliver + ", Delivering=" + deliver + " - " + line );

                        //

                        createLine( order,line,deliver,storages,true );
                    } else {
                        log.fine( "Failed: " + order.getDeliveryRule() + " - OnHand=" + onHand + " (Unconfirmed=" + unconfirmedShippedQty + "), ToDeliver=" + toDeliver + " - " + line );
                    }
                }    // for all order lines

                // Complete Order successful

                if( completeOrder && MOrder.DELIVERYRULE_CompleteOrder.equals( order.getDeliveryRule())) {
                	log.fine("Entro en completeOrder y MOrder.DELIVERYRULE de inoutgenerate");
                	for( int i = 0;i < lines.length;i++ ) {
                        MOrderLine line = lines[ i ];

                        if( line.getM_Warehouse_ID() != p_M_Warehouse_ID ) {
                            continue;
                        }

                        MProduct   product   = line.getProduct();
                        BigDecimal toDeliver = line.getQtyOrdered().subtract( line.getQtyDelivered());

                        //

                        MStorage[] storages = null;

                        if( (product != null) && product.isStocked()) {
                            MProductCategory pc = MProductCategory.get( order.getCtx(),product.getM_Product_Category_ID(), get_TrxName());
                            String MMPolicy = pc.getMMPolicy();

                            if( (MMPolicy == null) || (MMPolicy.length() == 0) ) {
                                MMPolicy = client.getMMPolicy();
                            }

                            //

                            storages = MStorage.getWarehouse( getCtx(),line.getM_Warehouse_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstance_ID(),product.getM_AttributeSet_ID(),line.getM_AttributeSetInstance_ID() == 0,minGuaranteeDate,MClient.MMPOLICY_FiFo.equals( MMPolicy ),null );
                        }

                        //

                        createLine( order,line,toDeliver,storages,false );
                    }
                }

                m_line += 1000;
            }    // while order

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,m_sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }
        log.fine("justo antes del complete en el generate() de inoutgenerate");
        completeShipment();
        
        //return "@Created@ = " + m_created + retInfo;
        return m_created + " remitos creados. " + retInfo;
    }    // generate

    private void checkLinePriority(MInOutLine line, MOrderLine orderLine) {
    	
    	// FIXME: Define tambien una constante en el codigo y al hace la comprobacion, 
    	//        si no tiene nada configurado en la ficha del articulo, que tome esa 
    	//        constante como valor.
    	
    	int diasDeDiferencia = 7;
    	
    	if (!m_highPriority && line.getM_AttributeSetInstance_ID() != 0) {
            MAttributeSetInstance masi = MAttributeSetInstance.get(getCtx(), line.getM_AttributeSetInstance_ID(), 0);
            Timestamp mdd = masi.getDueDate();
            
            if (mdd != null) {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(mdd);
                cal1.add(Calendar.DAY_OF_MONTH, diasDeDiferencia);

                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(orderLine.getDatePromised());
                
            	// if (mdd.compareTo(orderLine.getDatePromised()) <= 0)
                if (cal1.compareTo(cal2) >= 0)
                	m_highPriority = true;
            }
        }
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param order
     * @param orderLine
     * @param qty
     * @param storages
     * @param force
     */

    private void createLine( MOrder order,MOrderLine orderLine,BigDecimal qty,MStorage[] storages,boolean force ) {

        // Complete last Shipment - can have multiple shipments
    	log.fine("Createline de InoutGenerate");
    	
        if( m_lastC_BPartner_Location_ID != orderLine.getC_BPartner_Location_ID()) {
            completeShipment();
        }

        m_lastC_BPartner_Location_ID = orderLine.getC_BPartner_Location_ID();

        // Create New Shipment

        if( m_shipment == null ) {

        	if (m_created > 0) {
        		retInfo += "</ul>";
        	}

        	
        	m_shipment = new MInOut( order,0,m_movementDate );
            m_shipment.setC_DocType_ID(m_DocTypeId);
        	m_shipment.setM_Warehouse_ID( orderLine.getM_Warehouse_ID());    // sets Org too

        	
        	/* Disytel: esto no va
            if( order.getC_BPartner_ID() != orderLine.getC_BPartner_ID()) {
                m_shipment.setC_BPartner_ID( orderLine.getC_BPartner_ID());
            }

            if( order.getC_BPartner_Location_ID() != orderLine.getC_BPartner_Location_ID()) {
                m_shipment.setC_BPartner_Location_ID( orderLine.getC_BPartner_Location_ID());
            }
        	*/
        	
            if( !m_shipment.save()) {
                throw new IllegalStateException( "Could not create Shipment" );
            }

            MBPartner partner = new MBPartner(getCtx(), m_shipment.getC_BPartner_ID(), null);
            MBPartnerLocation bpLoc = new MBPartnerLocation(getCtx(), m_shipment.getC_BPartner_Location_ID(), null);
            MLocation loc = bpLoc.getLocation(true);
            retInfo += "<h3>Para: " + partner.getName();
            retInfo += ", " + loc + ". Remito " + m_shipment.getDocumentNo();
            retInfo += "</h3><ul>";

            
        }

        // Non Inventory Lines

        if( storages == null ) {
            MInOutLine line = new MInOutLine( m_shipment );

            line.setOrderLine( orderLine,0,Env.ZERO );
            line.setQty( qty );    // Correct UOM

            //
            
            checkLinePriority(line, orderLine);
            
            //
            
            if( orderLine.getQtyEntered().compareTo( orderLine.getQtyOrdered()) != 0 ) {
                line.setQtyEntered( qty.multiply( orderLine.getQtyEntered()).divide( orderLine.getQtyOrdered(),BigDecimal.ROUND_HALF_UP ));
            }

            line.setLine( m_line + orderLine.getLine());

            if( !line.save()) {
                throw new IllegalStateException( "Could not create Shipment Line" );
            }

            log.fine( line.toString());

            retInfo += "<li>";
            
            MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), null);
            MUOM uom = new MUOM(getCtx(), line.getC_UOM_ID(), null);
            DecimalFormat format = new DecimalFormat("#.##");
            retInfo += product.getName() + ": ";
            retInfo += format.format(line.getQtyEntered()) + " " + uom.getName();
            
            
            retInfo +=	"</li>";
            
            return;
        }

        // Inventory Lines

        BigDecimal toDeliver = qty;

        for( int i = 0;i < storages.length;i++ ) {
            MStorage   storage = storages[ i ];
            BigDecimal deliver = toDeliver;

            // Not enough On Hand

            if( deliver.compareTo( storage.getQtyOnHand()) > 0 ) {
                if( !force                                                // Adjust
                        || ( force && (i + 1 != storages.length) ) ) {    // if force not on last location
                    deliver = storage.getQtyOnHand();
                }
            }

            if( deliver.signum() == 0 ) {    // zero deliver
                continue;
            }

            int M_Locator_ID = storage.getM_Locator_ID();

            //

            MInOutLine line = new MInOutLine( m_shipment );

            line.setOrderLine( orderLine,M_Locator_ID,order.isSOTrx()
                    ?deliver
                    :Env.ZERO );
            line.setQty( deliver );

            if( orderLine.getQtyEntered().compareTo( orderLine.getQtyOrdered()) != 0 ) {
                line.setQtyEntered( line.getMovementQty().multiply( orderLine.getQtyEntered()).divide( orderLine.getQtyOrdered(),BigDecimal.ROUND_HALF_UP ));
            }

            line.setLine( m_line + orderLine.getLine());

            if( !line.save()) {
                throw new IllegalStateException( "Could not create Shipment Line" );
            }

            checkLinePriority(line, orderLine);

            log.fine( line.toString());

            retInfo += "<li>";
            
            MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), null);
            MUOM uom = new MUOM(getCtx(), line.getC_UOM_ID(), null);
            DecimalFormat format = new DecimalFormat("#.##");
            retInfo += product.getName() + ": ";
            retInfo += format.format(line.getQtyEntered()) + " " + uom.getName();
            
            
            retInfo +=	"</li>";
            
            
            log.fine( "ToDeliver=" + toDeliver + " - " + line );
            toDeliver = toDeliver.subtract( deliver );

            if( toDeliver.compareTo( Env.ZERO ) == 0 ) {
                break;
            }
        }

        if( toDeliver.compareTo( Env.ZERO ) != 0 ) {
            throw new IllegalStateException( "Not All Delivered - Remainder=" + toDeliver );
        }
    }    // createLine

    private void BreakLineAndApplyRai(MInOut io, MInOutLine lin, RecommendedAtributeInstance[] rai) {
    	
    	// lin.setM_AttributeSetInstance_ID(rai[0].getM_AtributeInstance_ID());
    	// lin.setQty(rai[0].getQtyOnHand());
    	
		for (int i = 0; i<rai.length; i++) {
			RecommendedAtributeInstance r = rai[i];
			
			MInOutLine newLine;
			
			if (i == 0) {
				newLine = lin;
			} else {
				newLine = new MInOutLine(io);
				PO.copyValues(lin, newLine);
			}
			
			newLine.setM_AttributeSetInstance_ID(r.getM_AtributeInstance_ID());
			newLine.setQty(r.getQtyOnHand());
			newLine.setLine(lin.getLine() + i);
			
			if (r.getM_Locator_ID() != null)
				newLine.setM_Locator_ID(r.getM_Locator_ID());
			
			newLine.save();
		}
    }
    
    private int BreakLinesByBestAttr() {
    	int M_InOut_ID = m_shipment.getM_InOut_ID();
    	int cantidadLineasDivididas = 0;
    	
    	try {
    		MInOutLine[] lines = m_shipment.getLines(true);
    		
    		for (MInOutLine lin : lines) {
    			if (MAttributeSet.ProductNeedsInstanceAttribute(lin.getM_Product_ID(), null)) {
	    			RecommendedAtributeInstance[] rai = MProduct.getRecommendedAtributeInstance(lin.getM_Product_ID(), lin.getQtyEntered(), p_forceSingleAttributeInstance, lin.getM_Warehouse_ID(), lin.getM_AttributeSetInstance_ID(), false);
	    			
	    			if (p_forceSingleAttributeInstance && rai.length != 1) {
	    				log.log(Level.SEVERE, "BreakLinesByBestAttr: getRecommendedAtributeInstance(force: true) returned != 1 - M_InOutLine_ID = " + lin.getM_InOutLine_ID() + " M_Product_ID = " + lin.getM_Product_ID());
	    				rai = MProduct.getRecommendedAtributeInstance(lin.getM_Product_ID(), lin.getQtyEntered(), false, lin.getM_Warehouse_ID(), lin.getM_AttributeSetInstance_ID(), false);
	    			}
	    			
	    			if (rai.length > 0) {
	    				if (rai.length > 1) 
	    					cantidadLineasDivididas++;
	
	    				//  
	    				
	    				BreakLineAndApplyRai(m_shipment, lin, rai);
	    				
	    				//
	    				
	    			} else {
	    				log.log(Level.SEVERE, "BreakLinesByBestAttr: getRecommendedAtributeInstance returned 0 - M_InOutLine_ID = " + lin.getM_InOutLine_ID());
	    			}
    			}
    		}
    		
    		if (cantidadLineasDivididas > 0) {
    			// Actualizar los n�meros de l�nea
    			
    			String sql = "UPDATE M_InOutLine ml1 SET line = ((SELECT COUNT(*) FROM M_InOutLine ml2 WHERE ml2.m_inout_id=ml1.m_inout_id AND ml2.M_InOutLine_ID < ml1.M_InOutLine_ID ) + 1) * 10 WHERE ml1.m_inout_id = " + M_InOut_ID;
    			DB.executeUpdate(sql);
    		}
    		
    	} catch (Exception e) {
    		cantidadLineasDivididas = -1;
    		log.log(Level.SEVERE, "BreakLinesByBestAttr", e);
    	}
    	
    	return cantidadLineasDivididas;
    }
    
    /**
     * Descripción de Método
     *
     */

    private void completeShipment() {
        if( m_shipment != null ) {

        	// BreakLinesByBestAttr();
        	
        	if (m_highPriority)
        		m_shipment.setPriorityRule(MInOut.PRIORITYRULE_High);
        	
            // Fails if there is a confirmation
        	log.fine("Lo que llega aqui, pdocAction="+p_docAction);
            if( !m_shipment.processIt( p_docAction )) {
                log.warning( "Failed: " + m_shipment );
            }
            
            m_shipment.save();

            //

            addLog( m_shipment.getM_InOut_ID(),m_shipment.getMovementDate(),null,m_shipment.getDocumentNo());
            m_created++;
        }

        m_shipment = null;
        m_line     = 0;
    }    // completeOrder
}    // InOutGenerate



/*
 *  @(#)InOutGenerate.java   02.07.07
 * 
 *  Fin del fichero InOutGenerate.java
 *  
 *  Versión 2.2
 *
 */
