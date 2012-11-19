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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MProduct;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_I_Product;
import org.openXpertya.model.X_M_Product;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportProduct extends SvrProcess {

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private boolean m_deleteOldImported = false;

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_DateValue = null;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( name.equals( "AD_Client_ID" )) {
                m_AD_Client_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DeleteOldImported" )) {
                m_deleteOldImported = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
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
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
        StringBuffer sql         = null;
        int          no          = 0;
        String       clientCheck = " AND AD_Client_ID=" + m_AD_Client_ID;

        // ****    Prepare ****

        // Delete Old Imported

        if( m_deleteOldImported ) {
            sql = new StringBuffer( "DELETE I_Product " + "WHERE I_IsImported='Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            log.info( "Delete Old Impored =" + no );
        }
        
        // Mandatory ID Organización del Articulo
        sql = new StringBuffer( "UPDATE I_Product i " + "Set AD_Org_ID= COALESCE ((SELECT AD_Org_ID FROM AD_Org r" + " WHERE r.Value=i.ContactProduct AND r.AD_Client_ID IN (0, i.AD_Client_ID)),AD_Org_ID) " + "WHERE I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        // Set Client, IaActive, Created/Updated, ProductType
        sql = new StringBuffer( "UPDATE I_Product " + "SET AD_Client_ID = COALESCE (AD_Client_ID, " ).append( m_AD_Client_ID ).append( ")," + " IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, current_timestamp)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, current_timestamp)," + " UpdatedBy = COALESCE (UpdatedBy, 0)," + " ProductType = COALESCE (ProductType, 'I')," + " I_ErrorMsg = ''," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
        no = DB.executeUpdate( sql.toString());
        log.info( "Reset=" + no );
        
        // Set Org
        sql = new StringBuffer( "UPDATE I_Product " + "SET AD_Org_ID = COALESCE (AD_Org_ID, 0) " + "WHERE (AD_Org_ID IS NULL AND (I_IsImported<>'Y' OR I_IsImported IS NULL))" );
        no = DB.executeUpdate( sql.toString());
        log.info( "Reset=" + no );
        
        // Set Optional BPartner

        sql = new StringBuffer( "UPDATE I_Product i " + "SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p" + " WHERE i.BPartner_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE C_BPartner_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "BPartner=" + no );
        
        //

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductInvalidBP")+". ' " + "WHERE C_BPartner_ID IS NULL AND BPartner_Value IS NOT NULL " + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        

        if( no != 0 ) {
            log.warning( "Invalid BPartner=" + no );
        }

        // ****    Find Product
        // EAN/UPC

        sql = new StringBuffer( "UPDATE I_Product i " + "SET M_Product_ID=(SELECT M_Product_ID FROM M_Product p" + " WHERE i.UPC=p.UPC AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "Product Existing UPC=" + no );

        // Value

        sql = new StringBuffer( "UPDATE I_Product i " + "SET M_Product_ID=(SELECT M_Product_ID FROM M_Product p" + " WHERE i.Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "Product Existing Value=" + no );

        // BP ProdNo

        sql = new StringBuffer( "UPDATE I_Product i " + "SET M_Product_ID=(SELECT M_Product_ID FROM M_Product_po p" + " WHERE i.C_BPartner_ID=p.C_BPartner_ID" + " AND i.VendorProductNo=p.VendorProductNo AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "Product Existing Vendor ProductNo=" + no );

        // Set Product Category (own)

        sql = new StringBuffer( "UPDATE I_Product " + "SET ProductCategory_Value=(SELECT Value FROM M_Product_Category" + " WHERE IsDefault='Y' AND AD_Client_ID=" ).append( m_AD_Client_ID ).append( " AND ROWNUM=1) " + "WHERE ProductCategory_Value IS NULL AND M_Product_Category_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Category Default=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product i " + "SET M_Product_Category_ID=(SELECT M_Product_Category_ID FROM M_Product_Category c" + " WHERE i.ProductCategory_Value=c.Value AND i.AD_Client_ID=c.AD_Client_ID) " + "WHERE ProductCategory_Value IS NOT NULL AND M_Product_Category_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "Set Category=" + no );

        // Copy From Product if Import does not have value

        String[] strFields = new String[] {
            "Value","Name","Description","DocumentNote","Help","UPC","SKU","Classification","ProductType","Discontinued","DiscontinuedBy","ImageURL","DescriptionURL"
        };

        for( int i = 0;i < strFields.length;i++ ) {
            sql = new StringBuffer( "UPDATE I_PRODUCT i " + "SET " ).append( strFields[ i ] ).append( " = (SELECT " ).append( strFields[ i ] ).append( " FROM M_Product p" + " WHERE i.M_Product_ID=p.M_Product_ID AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NOT NULL" + " AND " ).append( strFields[ i ] ).append( " IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());

            if( no != 0 ) {
                log.fine( "" + strFields[ i ] + " - default from existing Product=" + no );
            }
        }

        String[] numFields = new String[] {
            "C_UOM_ID","M_Product_Category_ID","Volume","Weight","ShelfWidth","ShelfHeight","ShelfDepth","UnitsPerPallet"
        };

        for( int i = 0;i < numFields.length;i++ ) {
            sql = new StringBuffer( "UPDATE I_PRODUCT i " + "SET " ).append( numFields[ i ] ).append( " = (SELECT " ).append( numFields[ i ] ).append( " FROM M_Product p" + " WHERE i.M_Product_ID=p.M_Product_ID AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NOT NULL" + " AND (" ).append( numFields[ i ] ).append( " IS NULL OR " ).append( numFields[ i ] ).append( "=0)" + " AND I_IsImported='N'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());

            if( no != 0 ) {
                log.fine( "" + numFields[ i ] + " default from existing Product=" + no );
            }
        }

        // Copy From Product_PO if Import does not have value

        String[] strFieldsPO = new String[] {
            "UPC","PriceEffective","VendorProductNo","VendorCategory","Manufacturer","Discontinued","DiscontinuedBy"
        };

        for( int i = 0;i < strFieldsPO.length;i++ ) {
            sql = new StringBuffer( "UPDATE I_PRODUCT i " + "SET " ).append( strFieldsPO[ i ] ).append( " = (SELECT " ).append( strFieldsPO[ i ] ).append( " FROM M_Product_PO p" + " WHERE i.M_Product_ID=p.M_Product_ID AND i.C_BPartner_ID=p.C_BPartner_ID AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NOT NULL AND C_BPartner_ID IS NOT NULL" + " AND " ).append( strFieldsPO[ i ] ).append( " IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());

            if( no != 0 ) {
                log.fine( "" + strFieldsPO[ i ] + " default from existing Product PO=" + no );
            }
        }

        String[] numFieldsPO = new String[] {
            "C_UOM_ID","C_Currency_ID","PriceList","PricePO","RoyaltyAmt","Order_Min","Order_Pack","CostPerOrder","DeliveryTime_Promised"
        };

        for( int i = 0;i < numFieldsPO.length;i++ ) {
            sql = new StringBuffer( "UPDATE I_PRODUCT i " + "SET " ).append( numFieldsPO[ i ] ).append( " = (SELECT " ).append( numFieldsPO[ i ] ).append( " FROM M_Product_PO p" + " WHERE i.M_Product_ID=p.M_Product_ID AND i.C_BPartner_ID=p.C_BPartner_ID AND i.AD_Client_ID=p.AD_Client_ID) " + "WHERE M_Product_ID IS NOT NULL AND C_BPartner_ID IS NOT NULL" + " AND (" ).append( numFieldsPO[ i ] ).append( " IS NULL OR " ).append( numFieldsPO[ i ] ).append( "=0)" + " AND I_IsImported='N'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());

            if( no != 0 ) {
                log.fine( "" + numFieldsPO[ i ] + " default from existing Product PO=" + no );
            }
        }

        // Invalid Category

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductInvalidCategory")+". ' " + "WHERE M_Product_Category_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Invalid Category=" + no );
        }

        // Set UOM (System/own)

        sql = new StringBuffer( "UPDATE I_Product i " + "SET X12DE355 = " + "(SELECT X12DE355 FROM C_UOM u WHERE u.IsDefault='Y' AND u.AD_Client_ID IN (0,i.AD_Client_ID) AND ROWNUM=1) " + "WHERE X12DE355 IS NULL AND C_UOM_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set UOM Default=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product i " + "SET C_UOM_ID = (SELECT C_UOM_ID FROM C_UOM u WHERE u.X12DE355 ilike i.X12DE355 AND u.AD_Client_ID IN (0,i.AD_Client_ID)) " + "WHERE C_UOM_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "Set UOM=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductInvalidUOM")+". ' " + "WHERE C_UOM_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Invalid UOM=" + no );
        }

        // Marca
        sql = new StringBuffer( "UPDATE I_Product i " + "SET M_Product_Family_ID = (SELECT M_Product_Family_ID FROM M_Product_Family f WHERE upper(trim(f.value)) = upper(trim(i.productfamily_value)) AND f.AD_Client_ID IN (0,i.AD_Client_ID)) " + "WHERE M_Product_Family_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "Set M_Product_Family_ID =" + no );
        
		sql = new StringBuffer("UPDATE I_Product "
				+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"
				+ getMsg("ImportProductInvalidFamily") + ". ' "
				+ "WHERE m_product_family_id IS NULL"
				+ " AND productfamily_value is not null "
				+ " AND I_IsImported<>'Y'").append(clientCheck);
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Invalid Family=" + no );
        }
        
        // Set Currency

        sql = new StringBuffer( "UPDATE I_Product i " + "SET ISO_Code=(SELECT ISO_Code FROM C_Currency c" + " INNER JOIN C_AcctSchema a ON (a.C_Currency_ID=c.C_Currency_ID)" + " INNER JOIN AD_ClientInfo cj ON (a.C_AcctSchema_ID=cj.C_AcctSchema1_ID)" + " WHERE cj.AD_Client_ID=i.AD_Client_ID) " + "WHERE C_Currency_ID IS NULL AND ISO_Code IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Currency Default=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product i " + "SET C_Currency_ID=(SELECT C_Currency_ID FROM C_Currency c" + " WHERE i.ISO_Code=c.ISO_Code AND c.AD_Client_ID IN (0,i.AD_Client_ID)) " + "WHERE C_Currency_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt- Set Currency=" + no );

        //

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductInvalidCurrency")+". ' " + "WHERE C_Currency_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Invalid Currency=" + no );
        }

        // Verify ProductType

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductInvalidType")+". ' " + "WHERE ProductType NOT IN ('I','S','E')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Invalid ProductType=" + no );
        }

        // Unique UPC/Value
        /* Actualmente se permiten múltiples líneas con los mismos artículos 
        sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductNotUniqueValue")+". ' " + "WHERE I_IsImported<>'Y'" + " AND Value IN (SELECT Value FROM I_Product pr WHERE i.AD_Client_ID=pr.AD_Client_ID GROUP BY Value HAVING COUNT(*) > 1)" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Not Unique Value=" + no );
        }
        */

        //
        /* Actualmente se permiten múltiples líneas con los mismos artículos
        sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductNotUniqueUPC")+". ' "  + "WHERE I_IsImported<>'Y'" + " AND UPC IN (SELECT UPC FROM I_Product pr WHERE i.AD_Client_ID=pr.AD_Client_ID GROUP BY UPC HAVING COUNT(*) > 1)" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Not Unique UPC=" + no );
        }
        */

        // Mandatory Value

        sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductRequieredValue")+". ' " + "WHERE Value IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "No Mandatory Value=" + no );
        }

        // Vendor Product No
        // sql = new StringBuffer ("UPDATE I_Product i "
        // + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No Mandatory VendorProductNo,' "
        // + "WHERE I_IsImported<>'Y'"
        // + " AND VendorProductNo IS NULL AND (C_BPartner_ID IS NOT NULL OR BPartner_Value IS NOT NULL)").append(clientCheck);
        // no = DB.executeUpdate(sql.toString());
        // log.info(log.l3_Util, "No Mandatory VendorProductNo=" + no);

        sql = new StringBuffer( "UPDATE I_Product " + "SET VendorProductNo=Value " + "WHERE C_BPartner_ID IS NOT NULL AND VendorProductNo IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "VendorProductNo Set to Value=" + no );

        //
        /* Actualmente se permiten múltiples líneas con los mismos artículos
        sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductNotUniqueVendorProductNo")+". ' " + "WHERE I_IsImported<>'Y'" + " AND C_BPartner_ID IS NOT NULL" + " AND (C_BPartner_ID, VendorProductNo) IN " + " (SELECT C_BPartner_ID, VendorProductNo FROM I_Product pr WHERE i.AD_Client_ID=pr.AD_Client_ID GROUP BY C_BPartner_ID, VendorProductNo HAVING COUNT(*) > 1)" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Not Unique VendorProductNo=" + no );
        }
        */

        // Setea Tax Category

        // Default
        sql = new StringBuffer( 
        		"UPDATE I_Product " + 
        		"SET C_TaxCategory_Name=" +
        		"	(SELECT Name " +
        		"	 FROM C_TaxCategory" + 
        		"    WHERE IsDefault='Y' AND AD_Client_ID=" ).append( m_AD_Client_ID ).append( " AND ROWNUM=1) " + 
        		"WHERE C_TaxCategory_Name IS NULL AND C_TaxCategory_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Tax Category Default=" + no );

        // Set

        sql = new StringBuffer( 
        		"UPDATE I_Product i " + 
        		"SET C_TaxCategory_ID=" +
        		"	(SELECT C_TaxCategory_ID " +
        		"	 FROM C_TaxCategory c" + 
        		"    WHERE i.C_TaxCategory_Name=c.Name AND i.AD_Client_ID=c.AD_Client_ID) " + 
        		"WHERE C_TaxCategory_Name IS NOT NULL AND C_TaxCategory_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.info( "Set Tax Category=" + no );
        
        // Invalid Tax Cateogory
        sql = new StringBuffer( 
        		"UPDATE I_Product " + 
        		"SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'"+ getMsg("ImportProductIvalidTaxCategory")+". ' " + 
        		"WHERE C_TaxCategory_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "Invalid Tax Category=" + no );
        }
        
        // Setea el Checkoutplace por default
        // Default
        sql = new StringBuffer( 
        		"UPDATE I_Product " + 
        		"SET CheckoutPlace='"+ MProduct.CHECKOUTPLACE_WarehousePOS +"'" +
        		"WHERE (CheckoutPlace IS NULL OR char_length(trim(CheckoutPlace)) = 0) AND I_IsImported<>'Y'" ).append( clientCheck );
        
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Tax Category Default=" + no );

        // -------------------------------------------------------------------

        int noInsert   = 0;
        int noUpdate   = 0;
        int noInsertPO = 0;
        int noUpdatePO = 0;

        // Go through Records

        log.fine( "start inserting/updating ..." );
        sql = new StringBuffer( "SELECT I_Product_ID, M_Product_ID, C_BPartner_ID, value " + "FROM I_Product WHERE I_IsImported='N'" ).append( clientCheck );

        Connection conn = DB.createConnection( false,Connection.TRANSACTION_READ_COMMITTED );

        try {

            /*
             *       Insert Product from Import
             * PreparedStatement pstmt_insertProduct = conn.prepareStatement
             *       ("INSERT INTO M_Product (M_Product_ID,"
             *       + "AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy,"
             *       + "Value,Name,Description,DocumentNote,Help,"
             *       + "UPC,SKU,C_UOM_ID,IsSummary,M_Product_Category_ID,C_TaxCategory_ID,"
             *       + "ProductType,ImageURL,DescriptionURL) "
             *       + "SELECT ?,"
             *       + "AD_Client_ID,AD_Org_ID,'Y',SysDate,CreatedBy,SysDate,UpdatedBy,"
             *       + "Value,Name,Description,DocumentNote,Help,"
             *       + "UPC,SKU,C_UOM_ID,'N',M_Product_Category_ID," + C_TaxCategory_ID + ","
             *       + "ProductType,ImageURL,DescriptionURL "
             *       + "FROM I_Product "
             *       + "WHERE I_Product_ID=?");
             */

            // Update Product from Import
        	//Anulada por ConSerTi, sintaxsis de updte no valido para postgres.
            //PreparedStatement pstmt_updateProduct = conn.prepareStatement( "UPDATE M_PRODUCT " + "SET (Value,Name,Description,DocumentNote,Help," + "UPC,SKU,C_UOM_ID,M_Product_Category_ID,Classification,ProductType," + "Volume,Weight,ShelfWidth,ShelfHeight,ShelfDepth,UnitsPerPallet," + "Discontinued,DiscontinuedBy,Updated,UpdatedBy)= " + "(SELECT Value,Name,Description,DocumentNote,Help," + "UPC,SKU,C_UOM_ID,M_Product_Category_ID,Classification,ProductType," + "Volume,Weight,ShelfWidth,ShelfHeight,ShelfDepth,UnitsPerPallet," + "Discontinued,DiscontinuedBy,SysDate,UpdatedBy" + " FROM I_Product WHERE I_Product_ID=?) " + "WHERE M_Product_ID=?" );
        	PreparedStatement pstmt_updateProduct = conn.prepareStatement("UPDATE M_PRODUCT " + "SET Value=aux.value"+
		    ",Name=aux.Name"+
		    ",Description=aux.Description"+
		    ",DocumentNote=aux.DocumentNote"+
		    ",Help=aux.Help"+
		    ",UPC=aux.UPC"+
		    ",SKU=aux.SKU"+
		    ",C_UOM_ID=aux.C_UOM_ID"+
		    ",M_Product_Category_ID=aux.M_Product_Category_ID"+
		    ",Classification=aux.Classification"+
		    ",ProductType=aux.ProductType"+
		    ",Volume=aux.Volume"+
		    ",Weight=aux.Weight"+
		    ",ShelfWidth=aux.ShelfWidth"+
		    ",ShelfHeight=aux.ShelfHeight"+
		    ",ShelfDepth=aux.ShelfDepth"+
		    ",UnitsPerPallet=aux.UnitsPerPallet"+
		    ",Discontinued=aux.Discontinued"+
		    ",DiscontinuedBy=aux.DiscontinuedBy"+
		    ",Updated=current_timestamp"+
		    ",UpdatedBy=aux.UpdatedBy" +
		    ",AD_Org_ID=aux.AD_Org_ID" +
" from (SELECT Value,Name,Description,DocumentNote,Help,UPC,SKU,C_UOM_ID,M_Product_Category_ID,Classification,ProductType,Volume,Weight,ShelfWidth,ShelfHeight,ShelfDepth,UnitsPerPallet,Discontinued,DiscontinuedBy,UpdatedBy,AD_Org_ID FROM I_Product WHERE I_Product_ID=?) as aux"
+" WHERE M_Product_ID=?");

            // Update Product_PO from Import

            //PreparedStatement pstmt_updateProductPO = conn.prepareStatement( "UPDATE M_Product_PO " + "SET (IsCurrentVendor,C_UOM_ID,C_Currency_ID,UPC," + "PriceList,PricePO,RoyaltyAmt,PriceEffective," + "VendorProductNo,VendorCategory,Manufacturer," + "Discontinued,DiscontinuedBy,Order_Min,Order_Pack," + "CostPerOrder,DeliveryTime_Promised,Updated,UpdatedBy)= " + "(SELECT 'Y',C_UOM_ID,C_Currency_ID,UPC," + "PriceList,PricePO,RoyaltyAmt,PriceEffective," + "VendorProductNo,VendorCategory,Manufacturer," + "Discontinued,DiscontinuedBy,Order_Min,Order_Pack," + "CostPerOrder,DeliveryTime_Promised,SysDate,UpdatedBy" + " FROM I_Product" + " WHERE I_Product_ID=?) " + "WHERE M_Product_ID=? AND C_BPartner_ID=?" );
        	PreparedStatement pstmt_updateProductPO = conn.prepareStatement("UPDATE M_Product_PO " + "SET IsCurrentVendor='Y'"+
        		    ",C_UOM_ID=aux1.C_UOM_ID"+
        		    ",C_Currency_ID=aux1.C_Currency_ID"+
        		    ",UPC=aux1.UPC"+
        		    ",PriceList=aux1.PriceList"+
        		    ",PricePO=aux1.PricePO"+
        		    ",RoyaltyAmt=aux1.RoyaltyAmt"+
        		    ",PriceEffective=aux1.PriceEffective"+
        		    ",VendorProductNo=aux1.VendorProductNo"+
        		    ",VendorCategory=aux1.VendorCategory"+
        		    ",Manufacturer=aux1.Manufacturer"+
        		    ",Discontinued=aux1.Discontinued"+
        		    ",Order_Min=aux1.Order_Min"+
        		    ",Order_Pack=aux1.Order_Pack"+
        		    ",CostPerOrder=aux1.CostPerOrder"+
        		    ",DeliveryTime_Promised=aux1.DeliveryTime_Promised"+
        		    ",Updated=current_timestamp"+
        		    ",UpdatedBy=aux1.UpdatedBy"+
        " from (SELECT 'Y',C_UOM_ID,C_Currency_ID,UPC,PriceList,PricePO,RoyaltyAmt,PriceEffective,VendorProductNo,VendorCategory,Manufacturer,Discontinued,DiscontinuedBy,Order_Min,Order_Pack,CostPerOrder,DeliveryTime_Promised,UpdatedBy FROM I_Product) as aux1"
        +" WHERE M_Product_ID=? AND C_BPartner_ID=?");
            

            // Insert Product from Import

            PreparedStatement pstmt_insertProductPO = conn.prepareStatement( "INSERT INTO M_Product_PO (M_Product_ID,C_BPartner_ID, " + "AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy," + "IsCurrentVendor,C_UOM_ID,C_Currency_ID,UPC," + "PriceList,PricePO,RoyaltyAmt,PriceEffective," + "VendorProductNo,VendorCategory,Manufacturer," + "Discontinued,DiscontinuedBy,Order_Min,Order_Pack," + "CostPerOrder,DeliveryTime_Promised) " + "SELECT ?,?, " + "AD_Client_ID,AD_Org_ID,'Y',current_timestamp,CreatedBy,current_timestamp,UpdatedBy," + "'Y',C_UOM_ID,C_Currency_ID,UPC," + "PriceList,PricePO,RoyaltyAmt,PriceEffective," + "VendorProductNo,VendorCategory,Manufacturer," + "Discontinued,DiscontinuedBy,Order_Min,Order_Pack," + "CostPerOrder,DeliveryTime_Promised " + "FROM I_Product " + "WHERE I_Product_ID=?" );

            // Set Imported = Y

            PreparedStatement pstmt_setImported = conn.prepareStatement( "UPDATE I_Product SET I_IsImported='Y', M_Product_ID=?, " + "Updated=current_timestamp, Processed='Y' WHERE I_Product_ID=?" );

            //
            log.finer("sql.toString= "+sql.toString());
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
            	log.finer("Un articulo................................................");
                int     I_Product_ID  = rs.getInt( 1 );
                int     M_Product_ID  = rs.getInt( 2 );
                int     C_BPartner_ID = rs.getInt( 3 );
                boolean newProduct    = M_Product_ID == 0;

                log.fine( "I_Product_ID..=" + I_Product_ID + ", M_Product_ID..=" + M_Product_ID + ", C_BPartner_ID..=" + C_BPartner_ID );

				// Buscar el artículo si es que no se agregó actualmente. Esto
				// se puede dar en los casos en que vienen 2 o más líneas con
				// distintos proveedores y nros de artículo . Por esto se debe
				// realizar la consulta para determinar si actualmente está
				// insertado
                if(newProduct){ 
                	newProduct = !PO.existRecordFor(getCtx(),
							X_M_Product.Table_Name,
							"ad_client_id = ? AND upper(trim(value)) = upper(trim('"
									+ rs.getString("value") + "')) ",
							new Object[] { Env.getAD_Client_ID(getCtx()) },
							null);
                	// Actualizo el id del artículo si es que existe
					M_Product_ID = newProduct ? 0
							: DB.getSQLValue(
									null,
									"SELECT m_product_id FROM m_product WHERE "
											+ "ad_client_id = ? AND upper(trim(value)) = upper(trim('"
											+ rs.getString("value") + "')) ",
									Env.getAD_Client_ID(getCtx()));
                }
                
                // Product

                if( newProduct )    // Insert new Product
                {
                	log.finer("es nuevo producto");
                    X_I_Product iP = new X_I_Product( getCtx(),I_Product_ID,null );
                    MProduct p = new MProduct( iP );

                    if( p.save()) {
                        M_Product_ID = p.getM_Product_ID();
                        log.finer( "Insert Product" );
                        noInsert++;
                    } else {
                        sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='Y', I_ErrorMsg=I_ErrorMsg||" ).append( DB.TO_STRING(getMsg("ImportProductSaveError") + ": " + CLogger.retrieveErrorAsString() )).append( "WHERE I_Product_ID=" ).append( I_Product_ID );
                        DB.executeUpdate( sql.toString());

                        continue;
                    }
                } else    // Update Product
                {
                	log.finer("No es nuevo producto");
                    pstmt_updateProduct.setInt( 1,I_Product_ID );
                    pstmt_updateProduct.setInt( 2,M_Product_ID );
                    log.finer("Vamos a ejecutar update product");

                    try {
                    	log.finer("ejecutando :"+ pstmt_updateProduct);
                        no = pstmt_updateProduct.executeUpdate();
                        log.finer( "Update Product == " + no );
                        noUpdate++;
                        log.finer("noUpdate= "+ noUpdate);
                    } catch( SQLException ex ) {
                        log.warning( "Update Product -> " + ex.toString());
                        sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='Y', I_ErrorMsg=I_ErrorMsg||" ).append( DB.TO_STRING( "Update Product: " + ex.toString())).append( "WHERE I_Product_ID=" ).append( I_Product_ID );
                        DB.executeUpdate( sql.toString());

                        continue;
                    }
                }

                // Do we have PO Info

                if( C_BPartner_ID != 0 ) {
                    no = 0;
                    log.finer("C_bpartner_id es distinto de 0");

                    // If Product existed, Try to Update first

                    if( !newProduct ) {
                        //pstmt_updateProductPO.setInt( 1,I_Product_ID );
                        pstmt_updateProductPO.setInt( 1,M_Product_ID );
                        pstmt_updateProductPO.setInt( 2,C_BPartner_ID );

                        try {
                            no = pstmt_updateProductPO.executeUpdate();
                            log.finer( "Update Product_PO == " + no );
                            noUpdatePO+= no;
                            log.finer("noUpdatePo= "+ noUpdatePO);
                        } catch( SQLException ex ) {
                            log.warning( "Update Product_PO -- " + ex.toString());
                            noUpdate--;
                            conn.rollback();
                            sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='Y', I_ErrorMsg=I_ErrorMsg||" ).append( DB.TO_STRING( "Update Product_PO: " + ex.toString())).append( "WHERE I_Product_ID=" ).append( I_Product_ID );
                            DB.executeUpdate( sql.toString());

                            continue;
                        }
                    }

                    if( no == 0 )    // Insert PO
                    {
                    	log.finer("En si no == 0");
                        pstmt_insertProductPO.setInt( 1,M_Product_ID );
                        pstmt_insertProductPO.setInt( 2,C_BPartner_ID );
                        pstmt_insertProductPO.setInt( 3,I_Product_ID );

                        try {
                            no = pstmt_insertProductPO.executeUpdate();
                            log.finer( "Insert Product_PO = " + no );
                            noInsertPO++;
                        } catch( SQLException ex ) {
                            log.warning( "Insert Product_PO - " + ex.toString());
                            noInsert--;    // assume that product also did not exist
                            conn.rollback();
                            sql = new StringBuffer( "UPDATE I_Product i " + "SET I_IsImported='Y', I_ErrorMsg=I_ErrorMsg||" ).append( DB.TO_STRING( "Insert Product_PO: " + ex.toString())).append( "WHERE I_Product_ID=" ).append( I_Product_ID );
                            DB.executeUpdate( sql.toString());

                            continue;
                        }
                    }
                }    // C_BPartner_ID != 0

                // Update I_Product

                pstmt_setImported.setInt( 1,M_Product_ID );
                pstmt_setImported.setInt( 2,I_Product_ID );
                no = pstmt_setImported.executeUpdate();

                //

                conn.commit();
            }    // for all I_Product

            rs.close();
            pstmt.close();

            //
            // pstmt_insertProduct.close();

            pstmt_updateProduct.close();
            pstmt_insertProductPO.close();
            pstmt_updateProductPO.close();
            pstmt_setImported.close();

            //

            conn.close();
            conn = null;
        } catch( SQLException e ) {
            try {
                if( conn != null ) {
                    conn.close();
                }

                conn = null;
            } catch( SQLException ex ) {
            }

            log.log( Level.SEVERE,"doIt",e );

            throw new Exception( "doIt",e );
        } finally {
            if( conn != null ) {
                conn.close();
            }

            conn = null;
        }

        // Set Error to indicator to not imported

        sql = new StringBuffer( "UPDATE I_Product " + "SET I_IsImported='N', Updated=current_timestamp " + "WHERE I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        addLog( 0,null,new BigDecimal( no ),"@Errors@" );
        addLog( 0,null,new BigDecimal( noInsert ),"@M_Product_ID@: @Inserted@" );
        addLog( 0,null,new BigDecimal( noUpdate ),"@M_Product_ID@: @Updated@" );
        addLog( 0,null,new BigDecimal( noInsertPO ),"@M_Product_ID@ @Purchase@: @Inserted@" );
        addLog( 0,null,new BigDecimal( noUpdatePO ),"@M_Product_ID@ @Purchase@: @Updated@" );

        return "";
    }    // doIt
    
    protected String getMsg(String msg) {
    	return Msg.translate(getCtx(), msg);
    }
}    // ImportProduct



/*
 *  @(#)ImportProduct.java   02.07.07
 * 
 *  Fin del fichero ImportProduct.java
 *  
 *  Versión 2.2
 *
 */
