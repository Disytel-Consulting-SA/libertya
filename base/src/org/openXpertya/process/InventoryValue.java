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
import java.sql.Timestamp;

import org.openXpertya.model.X_T_InventoryValue;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InventoryValue extends SvrProcess {

    /** Descripción de Campos */

    private int m_M_PriceList_Version_ID;

    /** Descripción de Campos */

    private Timestamp m_DateValue;

    /** Descripción de Campos */

    private int m_M_Warehouse_ID;

    /** Descripción de Campos */

    private int m_C_Currency_ID;

    private boolean showProdsWithoutPrice = true;
    
    private BigDecimal priceVariationPercent;
    
   //private float priceVariationPercent;
    
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
            } else if( name.equals( "M_PriceList_Version_ID" )) {
                m_M_PriceList_Version_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DateValue" )) {
                m_DateValue = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "M_Warehouse_ID" )) {
                m_M_Warehouse_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_Currency_ID" )) {
                m_C_Currency_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "ShowNoPrice" )) {
            	showProdsWithoutPrice = "Y".equals((String)para[i].getParameter());
            } else if( name.equals( "PriceVariationPercent" )) {
	        	priceVariationPercent = (( BigDecimal )para[ i ].getParameter());
	        }
            
        }

        if( m_DateValue == null ) {
            m_DateValue = new Timestamp( System.currentTimeMillis());
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

        // Delete (just to be sure)

		deleteOldRecords(X_T_InventoryValue.Table_Name, "DateValue",
				getAD_PInstance_ID(), get_TrxName());
		

		// Insert Products-
		StringBuffer sql = new StringBuffer( "INSERT INTO T_InventoryValue " + "(AD_PInstance_ID,AD_Client_ID,AD_Org_ID,M_Warehouse_ID,M_Product_ID,M_AttributeSetInstance_ID, M_Product_Category_ID, M_Product_Family_ID, ShowNoPrice, PriceVariationPercent)" );
        sql.append( "SELECT "+getAD_PInstance_ID()+", p.AD_Client_ID,p.AD_Org_ID," ).append( m_M_Warehouse_ID ).append( ", p.M_Product_ID, pai.M_AttributeSetInstance_ID, p.M_Product_Category_ID, p.M_Product_Family_ID, " ).append(showProdsWithoutPrice?"'Y'":"'N'").append(", '"+priceVariationPercent).append( "' FROM M_Product p LEFT OUTER JOIN M_AttributeSet pa ON (p.M_AttributeSet_ID=pa.M_AttributeSet_ID) LEFT OUTER JOIN M_AttributeSetInstance pai ON (pa.M_AttributeSet_ID=pai.M_AttributeSet_ID) WHERE p.IsStocked='Y'" );

        int noPrd = DB.executeUpdate( sql.toString(),get_TrxName());

        log.fine( "Inserted=" + noPrd );

        if( noPrd == 0 ) {
            return "No Products";
        }

      // Update Constants

        sql = new StringBuffer( "UPDATE T_InventoryValue SET " );

        // YYYY-MM-DD HH24:MI:SS.mmmm  JDBC Timestamp format

        String myDate = m_DateValue.toString();

        sql.append( "DateValue=TO_DATE('" ).append( myDate.substring( 0,10 )).append( " 23:59:59','YYYY-MM-DD HH24:MI:SS')," ).append( "M_PriceList_Version_ID=" ).append( m_M_PriceList_Version_ID ).append( "," ).append( "C_Currency_ID=" ).append( m_C_Currency_ID );
        int no = DB.executeUpdate( sql.toString(),get_TrxName());
                
        no=DB.executeUpdate( "UPDATE T_InventoryValue " + "SET M_AttributeSetInstance_ID = 0 WHERE M_AttributeSetInstance_ID IS NULL AND "+ " AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());

        
        // Get current QtyOnHand

        no = DB.executeUpdate( "UPDATE T_InventoryValue iv " + "SET QtyOnHand = (SELECT SUM(QtyOnHand) FROM M_Storage s, M_Locator l" + " WHERE iv.M_Product_ID=s.M_Product_ID "+ " AND iv.M_AttributeSetInstance_ID=s.M_AttributeSetInstance_ID "+ " AND l.M_Locator_ID=s.M_Locator_ID" + " AND l.M_Warehouse_ID=iv.M_Warehouse_ID GROUP BY iv.m_product_id, iv.m_attributesetinstance_id)" + " WHERE AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());
                
        // Adjust for Valuation Date

        no = DB.executeUpdate( "UPDATE T_InventoryValue iv SET QtyOnHand = " + "(SELECT iv.QtyOnHand - NVL(SUM(t.MovementQty), 0) FROM M_Transaction t, M_Locator l " + " WHERE t.M_Product_ID=iv.M_Product_ID AND iv.M_AttributeSetInstance_ID=t.M_AttributeSetInstance_ID AND t.MovementDate > iv.DateValue" + " AND t.M_Locator_ID=l.M_Locator_ID AND l.M_Warehouse_ID=iv.M_Warehouse_ID)" + " WHERE AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());

        // Delete Recotds w/o OnHand Qty

        int noQty = DB.executeUpdate( "DELETE FROM T_InventoryValue WHERE QtyOnHand=0 OR QtyOnHand IS NULL AND AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());

        log.fine( "NoQty=" + noQty );

        if( noPrd == noQty ) {
            return "No OnHand";
        }
        
        // Update Prices
        // Modified by Lucas Hernandez - Kunan	
        no = DB.executeUpdate( "UPDATE T_InventoryValue iv " + "SET PricePO = " + "(SELECT currencyConvert (po.PriceList,po.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, po.AD_Client_ID,po.AD_Org_ID)" + " FROM M_Product_PO po WHERE po.M_Product_ID=iv.M_Product_ID" + " AND po.IsCurrentVendor='Y' LIMIT 1), " + "PriceList = " + "(SELECT currencyConvert(pp.PriceList,pl.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pl.AD_Client_ID,pl.AD_Org_ID)" + " FROM M_PriceList pl, M_PriceList_Version plv, M_ProductPrice pp" + " WHERE pp.M_Product_ID=iv.M_Product_ID AND pp.M_PriceList_Version_ID=iv.M_PriceList_Version_ID" + " AND pp.M_PriceList_Version_ID=plv.M_PriceList_Version_ID" + " AND plv.M_PriceList_ID=pl.M_PriceList_ID), " + "PriceStd = " + "(SELECT currencyConvert(pp.PriceStd,pl.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pl.AD_Client_ID,pl.AD_Org_ID)" + " FROM M_PriceList pl, M_PriceList_Version plv, M_ProductPrice pp" + " WHERE pp.M_Product_ID=iv.M_Product_ID AND pp.M_PriceList_Version_ID=iv.M_PriceList_Version_ID" + " AND pp.M_PriceList_Version_ID=plv.M_PriceList_Version_ID" + " AND plv.M_PriceList_ID=pl.M_PriceList_ID), " + "PriceLimit = " + "(SELECT currencyConvert(pp.PriceLimit,pl.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pl.AD_Client_ID,pl.AD_Org_ID)" + " FROM M_PriceList pl, M_PriceList_Version plv, M_ProductPrice pp" + " WHERE pp.M_Product_ID=iv.M_Product_ID AND pp.M_PriceList_Version_ID=iv.M_PriceList_Version_ID" + " AND pp.M_PriceList_Version_ID=plv.M_PriceList_Version_ID" + " AND plv.M_PriceList_ID=pl.M_PriceList_ID), " + "CostStandard = " + "(SELECT currencyConvert(pc.CurrentCostPrice,acs.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pc.AD_Client_ID,pc.AD_Org_ID)" + " FROM AD_ClientInfo ci, C_AcctSchema acs, M_Product_Costing pc" + " WHERE iv.AD_Client_ID=ci.AD_Client_ID AND ci.C_AcctSchema1_ID=acs.C_AcctSchema_ID" + " AND acs.C_AcctSchema_ID=pc.C_AcctSchema_ID" + " AND iv.M_Product_ID=pc.M_Product_ID)" + " WHERE iv.m_attributesetinstance_id=0 AND AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());
        no = DB.executeUpdate( "UPDATE T_InventoryValue iv " + "SET PricePO = " + "(SELECT currencyConvert (po.PriceList,po.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, po.AD_Client_ID,po.AD_Org_ID)" + " FROM M_Product_PO po WHERE po.M_Product_ID=iv.M_Product_ID" + " AND po.IsCurrentVendor='Y' LIMIT 1), " + "PriceList = " + "(SELECT currencyConvert(pp.PriceList,pl.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pl.AD_Client_ID,pl.AD_Org_ID)" + " FROM M_PriceList pl, M_PriceList_Version plv, M_ProductPriceInstance pp" + " WHERE pp.M_Product_ID=iv.M_Product_ID AND pp.M_AttributeSetInstance_ID=iv.M_AttributeSetInstance_ID AND pp.M_PriceList_Version_ID=iv.M_PriceList_Version_ID" + " AND pp.M_PriceList_Version_ID=plv.M_PriceList_Version_ID" + " AND plv.M_PriceList_ID=pl.M_PriceList_ID), " + "PriceStd = " + "(SELECT currencyConvert(pp.PriceStd,pl.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pl.AD_Client_ID,pl.AD_Org_ID)" + " FROM M_PriceList pl, M_PriceList_Version plv, M_ProductPriceInstance pp" + " WHERE pp.M_Product_ID=iv.M_Product_ID AND pp.M_AttributeSetInstance_ID=iv.M_AttributeSetInstance_ID AND pp.M_PriceList_Version_ID=iv.M_PriceList_Version_ID" + " AND pp.M_PriceList_Version_ID=plv.M_PriceList_Version_ID" + " AND plv.M_PriceList_ID=pl.M_PriceList_ID), " + "PriceLimit = " + "(SELECT currencyConvert(pp.PriceLimit,pl.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pl.AD_Client_ID,pl.AD_Org_ID)" + " FROM M_PriceList pl, M_PriceList_Version plv, M_ProductPriceInstance pp" + " WHERE pp.M_Product_ID=iv.M_Product_ID AND pp.M_AttributeSetInstance_ID=iv.M_AttributeSetInstance_ID AND pp.M_PriceList_Version_ID=iv.M_PriceList_Version_ID" + " AND pp.M_PriceList_Version_ID=plv.M_PriceList_Version_ID" + " AND plv.M_PriceList_ID=pl.M_PriceList_ID), " + "CostStandard = " + "(SELECT currencyConvert(pc.CurrentCostPrice,acs.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pc.AD_Client_ID,pc.AD_Org_ID)" + " FROM AD_ClientInfo ci, C_AcctSchema acs, M_Product_Costing pc" + " WHERE iv.AD_Client_ID=ci.AD_Client_ID AND ci.C_AcctSchema1_ID=acs.C_AcctSchema_ID" + " AND acs.C_AcctSchema_ID=pc.C_AcctSchema_ID" + " AND iv.M_Product_ID=pc.M_Product_ID)" + " WHERE iv.m_attributesetinstance_id<>0 AND AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());
        no = DB.executeUpdate( "UPDATE T_InventoryValue iv " + "SET PricePO = " + "(SELECT currencyConvert (po.PriceList,po.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, po.AD_Client_ID,po.AD_Org_ID)" + " FROM M_Product_PO po WHERE po.M_Product_ID=iv.M_Product_ID" + " AND po.IsCurrentVendor='Y' LIMIT 1), " + "PriceList = " + "(SELECT currencyConvert(pp.PriceList,pl.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pl.AD_Client_ID,pl.AD_Org_ID)" + " FROM M_PriceList pl, M_PriceList_Version plv, M_ProductPrice pp" + " WHERE pp.M_Product_ID=iv.M_Product_ID AND pp.M_PriceList_Version_ID=iv.M_PriceList_Version_ID" + " AND pp.M_PriceList_Version_ID=plv.M_PriceList_Version_ID" + " AND plv.M_PriceList_ID=pl.M_PriceList_ID), " + "PriceStd = " + "(SELECT currencyConvert(pp.PriceStd,pl.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pl.AD_Client_ID,pl.AD_Org_ID)" + " FROM M_PriceList pl, M_PriceList_Version plv, M_ProductPrice pp" + " WHERE pp.M_Product_ID=iv.M_Product_ID AND pp.M_PriceList_Version_ID=iv.M_PriceList_Version_ID" + " AND pp.M_PriceList_Version_ID=plv.M_PriceList_Version_ID" + " AND plv.M_PriceList_ID=pl.M_PriceList_ID), " + "PriceLimit = " + "(SELECT currencyConvert(pp.PriceLimit,pl.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pl.AD_Client_ID,pl.AD_Org_ID)" + " FROM M_PriceList pl, M_PriceList_Version plv, M_ProductPrice pp" + " WHERE pp.M_Product_ID=iv.M_Product_ID AND pp.M_PriceList_Version_ID=iv.M_PriceList_Version_ID" + " AND pp.M_PriceList_Version_ID=plv.M_PriceList_Version_ID" + " AND plv.M_PriceList_ID=pl.M_PriceList_ID), " + "CostStandard = " + "(SELECT currencyConvert(pc.CurrentCostPrice,acs.C_Currency_ID,iv.C_Currency_ID,iv.DateValue,null, pc.AD_Client_ID,pc.AD_Org_ID)" + " FROM AD_ClientInfo ci, C_AcctSchema acs, M_Product_Costing pc" + " WHERE iv.AD_Client_ID=ci.AD_Client_ID AND ci.C_AcctSchema1_ID=acs.C_AcctSchema_ID" + " AND acs.C_AcctSchema_ID=pc.C_AcctSchema_ID" + " AND iv.M_Product_ID=pc.M_Product_ID)" + " WHERE iv.PriceList IS NULL AND iv.PriceStd IS NULL AND iv.PriceLimit IS NULL AND iv.AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());
        
        String msg = "";

        if( no == 0 ) {
            msg = "No Prices";
        }
        
        // Update UPC
        
        no = DB.executeUpdate( "UPDATE T_InventoryValue iv " + "SET UPC = " + "(SELECT p.upc FROM M_Product p WHERE p.M_Product_ID=iv.M_Product_ID)" + " WHERE iv.m_attributesetinstance_id=0 AND AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());
        no = DB.executeUpdate( "UPDATE T_InventoryValue iv " + "SET UPC = " + "(SELECT pi.upc FROM M_Product_Upc_Instance pi WHERE pi.M_Product_ID=iv.M_Product_ID" + " AND pi.M_AttributeSetInstance_ID = iv.M_AttributeSetInstance_ID )" + " WHERE iv.m_attributesetinstance_id<>0 AND AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());

        if( no == 0 ) {
            msg = "No UPC";
        }

        // Update Values
        no = DB.executeUpdate( "UPDATE T_InventoryValue SET " + "PricePOAmt = QtyOnHand * (PricePO + (PricePO * '"+priceVariationPercent+"')/100), " + "PriceListAmt = QtyOnHand * (PriceList + (PriceList * '"+priceVariationPercent+"')/100), " + "PriceStdAmt = QtyOnHand * (PriceStd + (PriceStd * '"+priceVariationPercent+"')/100), " + "PriceLimitAmt = QtyOnHand * (PriceLimit + (PriceLimit * '"+priceVariationPercent+"')/100), " + "CostStandardAmt = QtyOnHand * (CostStandard + (CostStandard * '"+priceVariationPercent+"')/100)" + " WHERE AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());
        log.fine( "Valued=" + no );

        //
        if(!showProdsWithoutPrice){
        	noQty = DB.executeUpdate( "DELETE FROM T_InventoryValue WHERE pricelist IS NULL AND AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());
        }
      
        no=DB.executeUpdate( "UPDATE T_InventoryValue iv " + "SET M_AttributeSetInstance_ID = NULL WHERE M_AttributeSetInstance_ID = 0 AND "+ " AD_PInstance_ID = "+getAD_PInstance_ID(),get_TrxName());

        return msg;
    }    // doIt
      
}    // InventoryValue



/*
 *  @(#)InventoryValue.java   02.07.07
 * 
 *  Fin del fichero InventoryValue.java
 *  
 *  Versión 2.2
 *
 */
