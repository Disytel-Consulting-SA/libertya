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
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.attribute.RecommendedAtributeInstance;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
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

public class CalloutInOut extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String order( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_Order_ID = ( Integer )value;

        if( (C_Order_ID == null) || (C_Order_ID.intValue() == 0) ) {
            return "";
        }

        // Get Details

        MOrder order = new MOrder( ctx,C_Order_ID.intValue(),null );

        if( order.getID() != 0 ) {
            mTab.setValue( "DateOrdered",order.getDateOrdered());
            mTab.setValue( "POReference",order.getPOReference());
            mTab.setValue( "AD_Org_ID",new Integer( order.getAD_Org_ID()));
            mTab.setValue( "AD_OrgTrx_ID",new Integer( order.getAD_OrgTrx_ID()));
            //mTab.setValue( "C_Campaign_ID",new Integer( order.getC_Activity_ID()));
            mTab.setValue( "C_Campaign_ID",(order.getC_Campaign_ID() > 0 ? order.getC_Campaign_ID() : null));
            mTab.setValue( "C_Project_ID", (order.getC_Project_ID() > 0 ? order.getC_Project_ID() : null ));
            mTab.setValue( "User1_ID",new Integer( order.getUser1_ID()));
            mTab.setValue( "User2_ID",new Integer( order.getUser2_ID()));
            mTab.setValue( "M_Warehouse_ID",new Integer( order.getM_Warehouse_ID()));

            //

            mTab.setValue( "DeliveryRule",order.getDeliveryRule());
            mTab.setValue( "DeliveryViaRule",order.getDeliveryViaRule());
            mTab.setValue( "M_Shipper_ID",new Integer( order.getM_Shipper_ID()));
            mTab.setValue( "FreightCostRule",order.getFreightCostRule());
            mTab.setValue( "FreightAmt",order.getFreightAmt());
            mTab.setValue( "C_BPartner_ID",new Integer( order.getC_BPartner_ID()));
        }

        return "";
    }    // order

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String docType( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_DocType_ID = ( Integer )value;

        if( (C_DocType_ID == null) || (C_DocType_ID.intValue() == 0) ) {
            return "";
        }

        try {
            Env.setContext( ctx,WindowNo,"C_DocTypeTarget_ID",C_DocType_ID.intValue());

            String SQL = "SELECT d.doctypekey, d.DocBaseType, d.IsDocNoControlled, s.CurrentNext " + "FROM C_DocType d, AD_Sequence s " + "WHERE C_DocType_ID=?"    // 1
                         + " AND d.DocNoSequence_ID=s.AD_Sequence_ID(+)";
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_DocType_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // Set Movement Type

                String DocBaseType = rs.getString( "DocBaseType" );
                String docTypeKey = rs.getString( "docTypeKey" );

                if( DocBaseType.equals( "MMS" )) {           // Material Shipments
                	if(docTypeKey.equals(MDocType.DOCTYPE_CustomerReturn)){
                		mTab.setValue( "MovementType","C+" );    // Customer returns
                	}
                	else{
                		mTab.setValue( "MovementType","C-" );    // Customer Shipments
                	}
                } else if( DocBaseType.equals( "MMR" )) {    // Material Receipts
                	if(docTypeKey.equals(MDocType.DOCTYPE_VendorReturn)){
                		mTab.setValue( "MovementType","V-" );    // Vendor Returns
                	}
                	else{
                		mTab.setValue( "MovementType","V+" );    // Vendor Receipts
                	}
                }

                // DocumentNo

                if( rs.getString( "IsDocNoControlled" ).equals( "Y" )) {
                    mTab.setValue( "DocumentNo","<" + rs.getString( "CurrentNext" ) + ">" );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"docType",e );

            return e.getLocalizedMessage();
        }

        return "";
    }    // docType

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String bpartner( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_BPartner_ID = ( Integer )value;
        if( (C_BPartner_ID == null) || (C_BPartner_ID.intValue() == 0) ) {
        	
        	// Por defecto va la entidad comercial Consumidor Final para clientes
        	boolean IsSOTrx = "Y".equals( Env.getContext( ctx,WindowNo,"IsSOTrx" ));
        	if(IsSOTrx){
				C_BPartner_ID = DB
						.getSQLValue(
								null,
								"SELECT c_bpartner_id FROM c_bpartner WHERE isactive = 'Y' AND ad_componentobjectUID = ?",
								"CORE-C_BPartner-1012142");
				if(C_BPartner_ID <= 0){
					C_BPartner_ID = DB
					.getSQLValue(
							null,
							"SELECT c_bpartner_id FROM c_bpartner WHERE isactive = 'Y' AND upper(trim(value)) = upper(trim(?))",
							"CF");
				}
        	}
        	if((C_BPartner_ID == null) || (C_BPartner_ID.intValue() <= 0)){
        		return "";
        	}
        	else{
        		mTab.setValue("C_BPartner_ID", C_BPartner_ID);
        	}
        }
        
        String SQL = "SELECT p.AD_Language,p.C_PaymentTerm_ID," + "p.M_PriceList_ID,p.PaymentRule,p.POReference," + "p.SO_Description,p.IsDiscountPrinted," + "p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable," + "l.C_BPartner_Location_ID,c.AD_User_ID, p.deliveryrule " + "FROM C_BPartner p, C_BPartner_Location l, AD_User c " + "WHERE p.C_BPartner_ID=l.C_BPartner_ID(+)" + " AND p.C_BPartner_ID=c.C_BPartner_ID(+)" + " AND p.C_BPartner_ID=?";    // 1

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_BPartner_ID.intValue());

            ResultSet  rs = pstmt.executeQuery();

            if( rs.next()) {

                // Location

                Integer ii = new Integer( rs.getInt( "C_BPartner_Location_ID" ));

                if( rs.wasNull()) {
                    mTab.setValue( "C_BPartner_Location_ID",null );
                } else {
                    mTab.setValue( "C_BPartner_Location_ID",ii );
                }

                // Contact

                ii = new Integer( rs.getInt( "AD_User_ID" ));

                if( rs.wasNull()) {
                    mTab.setValue( "AD_User_ID",null );
                } else {
                    mTab.setValue( "AD_User_ID",ii );
                }

                // CreditAvailable

                double CreditAvailable = rs.getDouble( "CreditAvailable" );

                if( !rs.wasNull() && (CreditAvailable < 0) ) {
                    mTab.fireDataStatusEEvent( "CreditLimitOver",DisplayType.getNumberFormat( DisplayType.Amount ).format( CreditAvailable ));
                }
                
                // Seteo Comercial/Usuario dependiendo la EC seleccionada
                
                MBPartner ec = new MBPartner(ctx, C_BPartner_ID, null);
            	int contEC = ec.getSalesRep_ID();
            	
            	if (contEC == 0){
            		mTab.setValue( "SalesRep_ID",null );
            	} else {
            		mTab.setValue( "SalesRep_ID",contEC );
            	}

            	if(!Util.isEmpty(rs.getString("DeliveryRule"), true)){
            		mTab.setValue( "DeliveryRule",rs.getString("DeliveryRule") );
            	}
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"bpartner",e );

            return e.getLocalizedMessage();
        }

        return "";
    }    // bpartner

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String orderLine( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_OrderLine_ID = ( Integer )value;

        if( (C_OrderLine_ID == null) || (C_OrderLine_ID.intValue() == 0) ) {
            return "";
        }

        setCalloutActive( true );

        // Get Details

        MOrderLine ol = new MOrderLine( ctx,C_OrderLine_ID.intValue(),null );

        if( ol.getID() != 0 ) {
        	mTab.setValue( "M_Product_ID", (ol.getM_Product_ID() == 0 ? null  : ol.getM_Product_ID()));
            mTab.setValue( "M_AttributeSetInstance_ID",new Integer( ol.getM_AttributeSetInstance_ID()));

            //

            mTab.setValue( "C_UOM_ID",new Integer( ol.getC_UOM_ID()));

            MInOut inOut = new MInOut(ctx, (Integer)mTab.getValue("M_InOut_ID"), null);
            MDocType docType = new MDocType(ctx, inOut.getC_DocType_ID(), null);
            BigDecimal MovementQty = BigDecimal.ZERO;
            // Si es devolución de cliente, 
            // colocar dentro de la cantidad del movimiento la cantidad entregada
            boolean isReturn = docType.getDocTypeKey().equals(MDocType.DOCTYPE_CustomerReturn);
            if(isReturn){
            	MovementQty = ol.getQtyDelivered();
            }
            else{
            	MovementQty = ol.getQtyOrdered().subtract( ol.getQtyDelivered());
            }

            mTab.setValue( "MovementQty",MovementQty );

            BigDecimal QtyEntered = MovementQty;

            if( ol.getQtyEntered().compareTo( ol.getQtyOrdered()) != 0 
            		&& !isReturn) {
                QtyEntered = QtyEntered.multiply( ol.getQtyEntered()).divide( ol.getQtyOrdered(),BigDecimal.ROUND_HALF_UP );
            }

            mTab.setValue( "QtyEntered",QtyEntered );
        }

        setCalloutActive( false );

        return "";
    }    // orderLine

    private void updateMasi(Properties ctx,int WindowNo,MTab mTab,Integer M_Product_ID) {
    	
    	if (M_Product_ID == null)
    		return;
    	
    	Integer M_Warehouse_ID = Env.getContextAsInt( ctx,WindowNo,"M_Warehouse_ID" );

        if (MAttributeSet.GetCondicionesAtributosByWindowNo(WindowNo, mTab.getTabNo()).isAutoSuggestAttributeSetInstance()) {
	        RecommendedAtributeInstance[] rai = null;
	        
	        try {
	        	rai = CalloutOrder.updateAttributeSetInstance(mTab, WindowNo, M_Product_ID, M_Warehouse_ID);
	        } catch (SQLException e) {
	        	log.log(Level.SEVERE, "getAttributeSetInstanceSuggestion", e);
	        }
	        
	        if (rai != null && rai.length > 1) {
	        	
	        	if (!mTab.dataSave(false) || mTab.getValue( "M_InOutLine_ID" ) == null)
	        		return;
	        	
	        	mTab.dataRefreshAll();
	        	
	        	MInOut io = new MInOut(ctx, (Integer)mTab.getValue( "M_InOut_ID" ), null);
	        	MInOutLine cur = new MInOutLine(ctx, (Integer)mTab.getValue( "M_InOutLine_ID" ), null);
	        	
	    		for (int i = 1; i < rai.length; i++) {
	    			RecommendedAtributeInstance r = rai[i];
	    			MInOutLine l = new MInOutLine(io);
	    			
	    			PO.copyValues(cur, l);
	    			
	    			l.setQty(r.getQtyOnHand());
	    			l.setM_AttributeSetInstance_ID(r.getM_AtributeInstance_ID());
	    			
	    			l.save();
	    		}
	        }
        } else {
            if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
                mTab.setValue( "M_AttributeSetInstance_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
            } else {
                mTab.setValue( "M_AttributeSetInstance_ID",null );
            }
        }
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String product( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive()) {
            return "";
        }

        Integer M_Product_ID = ( Integer )value;
        boolean IsSOTrx = "Y".equals( Env.getContext( ctx,WindowNo,"IsSOTrx" ));


        if( (M_Product_ID == null) || (M_Product_ID.intValue() == 0) ) {
            return "";
        }

        setCalloutActive( true );

        // Set Attribute

        int M_Warehouse_ID = Env.getContextAsInt( ctx,WindowNo,"M_Warehouse_ID" );

        
        // 
        
        // FIXME: Solo de venta?
        
        if (IsSOTrx) {
	        RecommendedAtributeInstance[] rai = null;
	        
	        try {
	        	rai = CalloutOrder.updateAttributeSetInstance(mTab, WindowNo, M_Product_ID, M_Warehouse_ID);
	        } catch (SQLException e) {
	        	log.log(Level.SEVERE, "getAttributeSetInstanceSuggestion", e);
	        }
	        
	        if (rai != null && rai.length > 1) {
	        	MInOut io = new MInOut(ctx, (Integer)mTab.getValue( "M_InOut_ID" ), null);
	
	    		for (int i = 1; i < rai.length; i++) {
	    			RecommendedAtributeInstance r = rai[i];
	    			MInOutLine l = new MInOutLine(io);
	    			
	    			PO.copyValuesEvaluatee(mTab, l);
	    			
	    			l.setQty(r.getQtyOnHand());
	    			l.setM_AttributeSetInstance_ID(r.getM_AtributeInstance_ID());
	    			
	    			l.save();
	    		}
	        }
        } else {
            if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
                mTab.setValue( "M_AttributeSetInstance_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
            } else {
                mTab.setValue( "M_AttributeSetInstance_ID",null );
            }
        }
        
        // updateMasi(ctx, WindowNo, mTab, M_Product_ID);
        
        if( IsSOTrx ) {
            setCalloutActive( false );

            return "";
        }

        // Set UOM/Locator/Qty

        MProduct product = MProduct.get( ctx,M_Product_ID.intValue());

        mTab.setValue( "C_UOM_ID",new Integer( product.getC_UOM_ID()));

        BigDecimal QtyEntered = ( BigDecimal )mTab.getValue( "QtyEntered" );

        mTab.setValue( "MovementQty",QtyEntered );

        if( product.getM_Locator_ID() != 0 ) {
            MLocator loc = MLocator.get( ctx,product.getM_Locator_ID());

            if( M_Warehouse_ID == loc.getM_Warehouse_ID()) {
                mTab.setValue( "M_Locator_ID",new Integer( product.getM_Locator_ID()));
            } else {
                log.fine( "No Locator for M_Product_ID=" + M_Product_ID + " and M_Warehouse_ID=" + M_Warehouse_ID );
            }
        } else {
            log.fine( "No Locator for M_Product_ID=" + M_Product_ID );
        }

        setCalloutActive( false );

        return "";
    }    // product

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String qty( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        setCalloutActive( true );

        int M_Product_ID = Env.getContextAsInt( ctx,WindowNo,"M_Product_ID" );

        // log.log(Level.WARNING,"qty - init - M_Product_ID=" + M_Product_ID);

        BigDecimal MovementQty,QtyEntered;

        // No Product

        if( M_Product_ID == 0 ) {
            QtyEntered = ( BigDecimal )mTab.getValue( "QtyEntered" );
            mTab.setValue( "MovementQty",QtyEntered );
        }

        // UOM Changed - convert from Entered -> Product

        else if( mField.getColumnName().equals( "C_UOM_ID" )) {
            int C_UOM_To_ID = (( Integer )value ).intValue();

            QtyEntered  = ( BigDecimal )mTab.getValue( "QtyEntered" );
            MovementQty = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,QtyEntered );

            if( MovementQty == null ) {
                MovementQty = QtyEntered;
            }

            boolean conversion = QtyEntered.compareTo( MovementQty ) != 0;

            log.fine( "UOM=" + C_UOM_To_ID + ", QtyEntered=" + QtyEntered + " -> " + conversion + " MovementQty=" + MovementQty );
            Env.setContext( ctx,WindowNo,"UOMConversion",conversion
                    ?"Y"
                    :"N" );
            mTab.setValue( "MovementQty",MovementQty );
        }

        // No UOM defined

        else if( Env.getContextAsInt( ctx,WindowNo,"C_UOM_ID" ) == 0 ) {
            QtyEntered = ( BigDecimal )mTab.getValue( "QtyEntered" );
            mTab.setValue( "MovementQty",QtyEntered );
        }

        // QtyEntered changed - calculate MovementQty

        else if( mField.getColumnName().equals( "QtyEntered" )) {
        	int C_UOM_To_ID = Env.getContextAsInt( ctx,WindowNo,"C_UOM_ID" );

            QtyEntered  = ( BigDecimal )value;
            boolean isSOTrx = "Y".equals(Env.getContext(ctx, WindowNo, "IsSOTrx"));

            //Se quito la siguiente validación ya que ahora es necesario pode tener cantidades menores a 0, para poder
            //manejar los descuentos a proveedores.      

            /*
            // En remitos de compra no se permiten cantidades menores o iguales que cero
            if (!isSOTrx && QtyEntered.compareTo(Env.ZERO) < 0) {
            	mTab.setValue("QtyEntered", null);
            	setCalloutActive(false);
            	return Msg.getMsg(ctx,"FieldUnderZeroError", new Object[] {Msg.translate(ctx,"QtyEntered")});
            }
			*/
            MovementQty = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,QtyEntered );

            if( MovementQty == null ) {
                MovementQty = QtyEntered;
            }

            boolean conversion = QtyEntered.compareTo( MovementQty ) != 0;

            log.fine( "UOM=" + C_UOM_To_ID + ", QtyEntered=" + QtyEntered + " -> " + conversion + " MovementQty=" + MovementQty );
            Env.setContext( ctx,WindowNo,"UOMConversion",conversion
                    ?"Y"
                    :"N" );
            mTab.setValue( "MovementQty",MovementQty );
        }

        // MovementQty changed - calculate QtyEntered

        else if( mField.getColumnName().equals( "MovementQty" )) {
            int C_UOM_To_ID = Env.getContextAsInt( ctx,WindowNo,"C_UOM_ID" );

            MovementQty = ( BigDecimal )value;
            QtyEntered  = MUOMConversion.convertProductTo( ctx,M_Product_ID,C_UOM_To_ID,MovementQty );

            if( QtyEntered == null ) {
                QtyEntered = MovementQty;
            }

            boolean conversion = MovementQty.compareTo( QtyEntered ) != 0;

            log.fine( "UOM=" + C_UOM_To_ID + ", MovementQty=" + MovementQty + " -> " + conversion + " QtyEntered=" + QtyEntered );
            Env.setContext( ctx,WindowNo,"UOMConversion",conversion
                    ?"Y"
                    :"N" );
            mTab.setValue( "QtyEntered",QtyEntered );
        }

        //

        // updateMasi(ctx, WindowNo, mTab, (Integer)mTab.getValue( "M_Product_ID" ));
        
        //
        
        setCalloutActive( false );

        return "";
    }    // qty
    
    
	public String prePrinted_docNo( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		String strValue = (String)value;
		// Verificar si el nro ingresado ya existe, en ese caso elevar un
		// warning al guardar
		setCalloutActive( true );
		
		// Esto se comenta ya que en un principio se pedía la validación, pero
		// luego se decidió que no se implementa
		// ------------------------------------------------------------------------
//		// Verificar si existe un registro con la misma guía y obtener el nro de
//		// documento de esa transferencia
//		String docNo = MInOut.getDocNoInOutByStrColumnCondition(ctx,
//				"PrePrinted_DocNo", strValue,
//				(Integer) mTab.getValue("M_InOut_ID"),
//				(Integer) mTab.getValue("C_DocType_ID"),
//				null);
//		// Si existe una transferencia con ese nro entonces registrar el warning
//		if(docNo != null){
//			mTab.setCurrentRecordWarning(Msg.getMsg(ctx,
//					"PrePrintedInOutDocNoWarning", new Object[] { docNo }));
//		}
//		else{
//			mTab.clearCurrentRecordWarning();
//		}
		// ------------------------------------------------------------------------
		
		setCalloutActive( false );
		return "";
	}
    
}    // CalloutInOut



/*
 *  @(#)CalloutInOut.java   02.07.07
 * 
 *  Fin del fichero CalloutInOut.java
 *  
 *  Versión 2.2
 *
 */
