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


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import javax.swing.JOptionPane;







import org.openXpertya.model.MDiscountSchemaLine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/*
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProductPriceTemp extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public ProductPriceTemp() {
        super();
    }    // ProductPriceTemp

    /** Descripción de Campos */

    private int m_PriceList_Version_ID;

    /** Descripción de Campos */

    private int m_Org_ID;

    /** Descripción de Campos */

    private int m_Client_ID;

    /** Descripción de Campos */

    private int m_DiscountSchema_ID;

    /** Descripción de Campos */

    private int m_PriceList_Version_Base_ID;

    /** Descripción de Campos */

    private int no;

    /** Descripción de Campos */

    private String ResultStr;

    /** Descripción de Campos */

    private String Message = "";

    /** Descripción de Campos */

    private int m_Currency_ID;

    /** Descripción de Campos */

    private int m_StdPrecision;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
    	log.info( " currupio Estoy ProductPriceTemp.Prepare" );
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            //JOptionPane.showMessageDialog( null,"En ProducpriceTemp, prepare() con los siguentes parametros para["+i+"]"+"\n"+para[i],"..Fin", JOptionPane.INFORMATION_MESSAGE );

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Client_ID" )) {
                m_Client_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "M_PriceList_Version_ID" )) {
                m_PriceList_Version_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AD_Org_ID" )) {
                m_Org_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "M_DiscountSchema_ID" )) {
                m_DiscountSchema_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "M_PriceList_Version_Base_ID" )) {
                m_PriceList_Version_Base_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        log.equals( m_PriceList_Version_ID + "-" + m_Org_ID + "-" + m_Client_ID + "-" + m_DiscountSchema_ID + "-" + m_PriceList_Version_Base_ID );
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String doIt() {
    	log.info( " currupio Estoy ProductPriceTemp.doIT" );

        // Checking Prerequisites

        if( !updatePricesMProductPO() ||!defaultCurrentVendor()) {
            return "";
        }

        /*
         * Make sure that we have only one active product
         */

        correctingDuplicates();

        /*
         * Create Selection of Product and discount schema line
         */

        priceListInfo();

        /*
         * For All Discount Lines in Sequence
         */

        discountLine();

        /*
         * Set Selected Price default = PriceList new
         */

//        setSelectedPriceDefault();

        updatePriceVariations(m_PriceList_Version_ID, null);
        
       
        return "";
    }    // doIt

    // PO Prices must exists

    
    /**
     * Actualizar las entradas en la tabla de importación de listas de precio,
     * pudiendo especificar tanto la versión de lista de precio como el registro 
     * de la tabla I_ProductPrice a fin de filtrar adecuadamente la actualización 
     */
    public static void updatePriceVariations(Integer priceListVersionID, Integer recordNo)
    {
    	// actualizar las variaciones
	    DB.executeUpdate(	" UPDATE I_ProductPrice " + 
				" SET variationpricelist = pricelist / previouspricelist, " +
				" 		variationpricelimit = pricelimit / previouspricelimit, " +
				" 		variationpricestd = pricestd / previouspricestd " +
				" WHERE " + getUserSQLCheck() + 
				" AND i_isimported = 'N' AND processed = 'N' " +
				(priceListVersionID == null ? "" : " AND M_PriceList_Version_ID = " + priceListVersionID) +
				(recordNo == null ? "" : " AND I_ProductPrice_ID = " + recordNo) ,
				 true, null, true);
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean updatePricesMProductPO() {
    	log.info( "currupio Estoy ProductPriceTemp.updatePricesMOroducPO" );
        ResultStr = "CorrectingProductPO";

        try {
            no = DB.executeUpdate( "UPDATE M_Product_PO" + " SET PriceList = 0 " + " WHERE PriceList IS NULL" );
            no = no + DB.executeUpdate( "UPDATE M_Product_PO" + " SET PriceLastPO = 0" + " WHERE PriceLastPo IS NULL" );
            no = no + DB.executeUpdate( "UPDATE M_Product_PO" + " SET PricePO = PriceLastPO" + " WHERE (PricePO IS NULL OR PricePO = 0) AND PriceLastPO <> 0" );
            no = no + DB.executeUpdate( "UPDATE M_Product_PO" + " SET PricePO = 0" + " WHERE PricePo IS NULL" );
            Message = "Updated " + no + " Prices ";

            return true;
        } catch( Exception e ) {
            ResultStr = "ERROR \t" + ResultStr + ":" + e.getMessage() + " " + Message;
            log.log( Level.SEVERE,ResultStr );

            return false;
        }
    }    // UpdateAD_PInstance

    // Set default current vendor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean defaultCurrentVendor() {
    	log.info( "currupio Estoy ProductPriceTemp.defaultCurrentVendor" );
        ResultStr = "defaultCurrentVendor";

        try {
            no = DB.executeUpdate( "UPDATE M_Product_PO p" + " SET IsCurrentVendor = 'Y'" + " WHERE IsCurrentVendor = 'N'" + " AND NOT EXISTS" + " (SELECT z.M_Product_ID FROM M_Product_PO z" + " WHERE z.M_Product_ID = p.M_Product_ID" + " GROUP BY z.M_Product_ID HAVING COUNT(*) > 1)" );
            Message = "Updated " + no + "Current Vendors";

            return true;
        } catch( Exception e ) {
            ResultStr = "ERROR \t" + ResultStr + ":" + e.getMessage() + " " + Message;
            log.log( Level.SEVERE,"\n " + ResultStr );

            return false;
        }
    }    // DefaultCurrentVendor

    /**
     * Descripción de Método
     *
     */

    private void correctingDuplicates() {
    	log.info( " currupio Estoy ProductPriceTemp.correctingDuplicates" );
        ResultStr = "correctingDuplicates";

        String sql   = null;
        String c_sql = null;

        // All duplicates products

        sql = "SELECT DISTINCT M_Product_ID" + " FROM M_Product_PO po" + " WHERE IsCurrentVendor = 'Y' AND IsActive = 'Y'" + " AND EXISTS (SELECT M_Product_ID FROM M_Product_PO x" + " WHERE x.M_Product_ID = po.M_Product_ID" + " GROUP BY M_Product_ID HAVING COUNT(*) > 1)" + " ORDER BY 1";

        PreparedStatement pstmt   = null;
        PreparedStatement c_pstmt = null;
        ResultSet         rs;
        ResultSet         c_rs;

        try {
            pstmt = DB.prepareStatement( sql );
            rs    = pstmt.executeQuery();

            while( rs.next()) {
                c_sql = "SELECT M_Product_ID, C_BPartner_ID" + " FROM M_Product_PO" + " WHERE IsCurrentVendor = 'Y' AND IsActive = 'Y'" + " AND M_Product_ID = " + rs.getInt( 1 ) + " ORDER BY PriceList DESC";
                c_pstmt = DB.prepareStatement( c_sql );
                c_rs    = c_pstmt.executeQuery();
                c_rs.next();    // Leave first

                while( c_rs.next()) {
                    no = DB.executeUpdate( "UPDATE M_Product_PO" + " SET IsCurrentVendor = 'N'" + " WHERE M_Product_ID = " + c_rs.getInt( 1 ) + " AND C_BPartner_ID = " + c_rs.getInt( 2 ));
                }

                c_rs.close();
                c_pstmt.close();
                c_pstmt = null;
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            ResultStr = ResultStr + ":" + e.getMessage() + " " + Message;
            log.log( Level.SEVERE,"\n " + ResultStr );
        }
    }    // CorrectingDuplicates

    // Get PriceList Info

    /**
     * Descripción de Método
     *
     */

    private void priceListInfo() {
    	log.info( "currupio Estoy ProductPriceTemp.priceListInfo" );
        ResultStr = "priceListInfo";

        String sql = new String( "SELECT p.C_Currency_ID, c.StdPrecision" + " FROM M_PriceList p, M_PriceList_Version v, C_Currency c" + " WHERE p.M_PriceList_ID = v.M_PriceList_ID" + " AND p.C_Currency_ID = c.C_Currency_ID" + " AND v.M_PriceList_Version_ID = " + m_PriceList_Version_ID );
        PreparedStatement pstmt = null;
        ResultSet         rs;

        try {
            pstmt = DB.prepareStatement( sql );
            rs    = pstmt.executeQuery();

            if( rs.next()) {
                m_Currency_ID  = rs.getInt( 1 );
                m_StdPrecision = rs.getInt( 2 );
            }

            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            ResultStr = ResultStr + ":" + e.getMessage() + " " + sql;
            log.log( Level.SEVERE,"\nERROR PriceListInfo " + ResultStr );
        }
    }    // PriceListInfo

    // Create de Selection for all discount schema line

    /**
     * Descripción de Método
     *
     */

    private void discountLine() {
    	log.info( "currupio Estoy ProductPriceTemp.disconuntLine" );
    	//JOptionPane.showMessageDialog( null,"Paramos para ver lo datos de la tabla temporal en descountLine con m_dicountShema_ID= "+ m_DiscountSchema_ID ,"..Fin", JOptionPane.INFORMATION_MESSAGE );
        String sql = new String( "SELECT *" + " FROM M_DiscountSchemaLine" + " WHERE M_DiscountSchema_ID = " + m_DiscountSchema_ID + " AND IsActive = 'Y'" + " ORDER BY SeqNo" );
        PreparedStatement pstmt = null;
        ResultSet         rs;

        try {
        	// Eliminar solo registros del usuario en cuestión (luego en la importación también se filtra por dicho criterio)
            no    = DB.executeUpdate( "DELETE FROM I_ProductPrice WHERE " + getUserSQLCheck() );
            pstmt = DB.prepareStatement( sql );
            rs    = pstmt.executeQuery();

            while( rs.next()) {
                ResultStr = " Parameter Seq = " + rs.getInt( "SeqNo" );

                // Log.print(ResultStr);
                // Clear Temporary Table
                //JOptionPane.showMessageDialog( null,"Paramos  para ver los datos de la tabla temporal antes de createSlection= "+ m_DiscountSchema_ID+ "and isstrong= " +rs.getString( "IsStrong" )+" M_discountSchemaline_ID= "+rs.getString("m_discountschemaline_id"),"..Fin", JOptionPane.INFORMATION_MESSAGE );
                createSelection( rs );
                //JOptionPane.showMessageDialog( null,"Paramos  para ver los datos de la tabla temporal despues de createSlection= "+ m_DiscountSchema_ID+ "and isstrong= " +rs.getString( "IsStrong" )+" M_discountSchemaline_ID= "+rs.getString("m_discountschemaline_id"),"..Fin", JOptionPane.INFORMATION_MESSAGE );

                if( "N".equals( rs.getString( "IsStrong" ))) {
                	//JOptionPane.showMessageDialog( null,"Paramos  para ver los datos de la tabla temporal NO ISSTRONG antes de deleteTemparySelcctiondespues de createSlection= "+ m_DiscountSchema_ID+ "and isstrong= " +rs.getString( "IsStrong" )+" M_discountSchemaline_ID= "+rs.getString("m_discountschemaline_id"),"..Fin", JOptionPane.INFORMATION_MESSAGE );
                    deleteTemporarySelection( rs );
                    //JOptionPane.showMessageDialog( null,"Paramos  para ver los datos de la tabla temporal NO ISSTRONG desde de deleteTemparySelcctiondespues de createSlection= "+ m_DiscountSchema_ID+ "and isstrong= " +rs.getString( "IsStrong" )+" M_discountSchemaline_ID= "+rs.getString("m_discountschemaline_id"),"..Fin", JOptionPane.INFORMATION_MESSAGE );
                }
                //JOptionPane.showMessageDialog( null,"Paramos  para ver los datos de la tabla temporal antes del copipryces en descountLine con m_dicountShema_ID= "+ m_DiscountSchema_ID+ "and isstrong= " +rs.getString( "IsStrong" )+" M_discountSchemaline_ID= "+rs.getString("m_discountschemaline_id"),"..Fin", JOptionPane.INFORMATION_MESSAGE );
                copyPrices( rs );
                //JOptionPane.showMessageDialog( null,"Paramos  para ver los datos de la tabla temporal despues del copipryces en descountLine con m_dicountShema_ID= "+ m_DiscountSchema_ID+ "and isstrong= " +rs.getString( "IsStrong" )+" M_discountSchemaline_ID= "+rs.getString("m_discountschemaline_id"),"..Fin", JOptionPane.INFORMATION_MESSAGE );

                if( "Y".equals( rs.getString( "IsStrong" ))) {
                	//JOptionPane.showMessageDialog( null,"Paramos  para ver los datos de la tabla temporal   es ISSTRONG antes del updatDiscount ,con m_dicountShema_ID= "+ m_DiscountSchema_ID+ "and isstrong= " +rs.getString( "IsStrong" )+" M_discountSchemaline_ID= "+rs.getString("m_discountschemaline_id"),"..Fin", JOptionPane.INFORMATION_MESSAGE );       	
                    updateDiscountStrong( rs );
                    //JOptionPane.showMessageDialog( null,"Paramos  para ver los datos de la tabla temporal   es ISSTRONG despues  del updatDiscount y antes del deleteTemporarySelection,con m_dicountShema_ID= "+ m_DiscountSchema_ID+ "and isstrong= " +rs.getString( "IsStrong" )+" M_discountSchemaline_ID= "+rs.getString("m_discountschemaline_id"),"..Fin", JOptionPane.INFORMATION_MESSAGE );
                    deleteTemporarySelection( rs );
                    //JOptionPane.showMessageDialog( null,"Paramos  para ver los datos de la tabla temporal   es ISSTRONG despues del deleteTemporarySelection,con m_dicountShema_ID= "+ m_DiscountSchema_ID+ "and isstrong= " +rs.getString( "IsStrong" )+" M_discountSchemaline_ID= "+rs.getString("m_discountschemaline_id"),"..Fin", JOptionPane.INFORMATION_MESSAGE );
                }

                calculation( rs );
                //JOptionPane.showMessageDialog( null,"Paramos  para ver los datos de la tabla temporal despues del calculation en descountLine con m_dicountShema_ID= "+ m_DiscountSchema_ID+ "and isstrong= " +rs.getString( "IsStrong" )+" M_discountSchemaline_ID= "+rs.getString("m_discountschemaline_id"),"..Fin", JOptionPane.INFORMATION_MESSAGE );

                // Log Info

                Message = "";
            }    // For all DiscountLines

            rs.close();
            pstmt.close();
            pstmt = null;

            // Delete Temporary Selection

        } catch( Exception e ) {
            ResultStr = ResultStr + ":" + e.getMessage() + " " + Message;
            log.log( Level.SEVERE,"\n " + ResultStr );
        }
    }            // DiscountLine

    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    private void createSelection( ResultSet rs ) {
    	log.info( "currupio Estoy ProductPriceTemp.createSelection" );
        ResultStr = "create selection";
        StringBuffer optionalRestrictions = setOptionalRestrictions(rs, m_PriceList_Version_Base_ID == 0); 
        
        try {
            if( m_PriceList_Version_Base_ID == 0 ) {

                // Create Selection from M_Product_PO

                StringBuffer sql = new StringBuffer( 	"INSERT INTO I_ProductPrice (AD_Client_ID, AD_Org_ID, CreatedBy, UpdatedBy, M_Product_ID, M_DiscountSchemaLine_ID, M_PriceList_Version_ID, M_AttributeSetInstance_ID)" +
                										" SELECT DISTINCT p.AD_Client_ID, p.AD_Org_ID, " + Env.getAD_User_ID(getCtx()) + ", " + Env.getAD_User_ID(getCtx()) + ", po.M_Product_ID, " + rs.getInt( "M_DiscountSchemaLine_ID" ) + ", " + m_PriceList_Version_ID + ", aseti.M_AttributeSetInstance_ID::int " + 
                										" FROM M_Product p " + 
                										" INNER JOIN M_Product_PO po ON p.M_Product_ID = po.M_Product_ID  " +
                										" INNER JOIN M_AttributeSet aset ON p.M_AttributeSet_ID = aset.M_AttributeSet_ID " +
                										" INNER JOIN M_AttributeSetInstance aseti ON aset.M_AttributeSet_ID = aseti.M_AttributeSet_ID " +
                										" WHERE p.M_Product_ID = po.M_Product_ID " + " AND (p.AD_Client_ID = " + m_Client_ID + " OR p.AD_Client_ID = 0 )" + " AND p.IsActive = 'Y' AND po.IsActive = 'Y' AND po.IsCurrentVendor = 'Y'"  +
                										optionalRestrictions.toString() +
                										" UNION " +
                										" SELECT DISTINCT p.AD_Client_ID, p.AD_Org_ID, " + Env.getAD_User_ID(getCtx()) + ", " + Env.getAD_User_ID(getCtx()) + ", po.M_Product_ID, " + rs.getInt( "M_DiscountSchemaLine_ID" )  + ", " + m_PriceList_Version_ID + ", null::int " + 
                										" FROM M_Product p, M_Product_PO po" + " WHERE p.M_Product_ID = po.M_Product_ID" + " AND (p.AD_Client_ID = " + m_Client_ID + " OR p.AD_Client_ID = 0 )" + " AND p.IsActive = 'Y' AND po.IsActive = 'Y' AND po.IsCurrentVendor = 'Y' AND p.M_AttributeSet_ID IS NULL " + 
                										optionalRestrictions.toString() ); 

                no = DB.executeUpdate( sql.toString());
            } else {

                // Create Selection from existing PriceList

                StringBuffer sql = new StringBuffer(  	" INSERT INTO I_ProductPrice (AD_Client_ID, AD_Org_ID, CreatedBy, UpdatedBy, M_Product_ID, M_DiscountSchemaLine_ID, M_AttributeSetInstance_ID) " + 
                										" SELECT DISTINCT p.AD_Client_ID, p.AD_Org_ID, " + Env.getAD_User_ID(getCtx()) + ", " + Env.getAD_User_ID(getCtx()) + ", p.M_Product_ID, " + rs.getInt( "M_DiscountSchemaLine_ID" ) + ", null::integer FROM M_Product p, M_ProductPrice z " + " WHERE p.M_Product_ID = z.M_Product_ID" + " AND z.M_PriceList_Version_ID = " + m_PriceList_Version_Base_ID + " AND p.IsActive = 'Y' AND z.IsActive = 'Y'" +
                										optionalRestrictions.toString() +
                										" UNION " +
                										" SELECT DISTINCT p.AD_Client_ID, p.AD_Org_ID, " + Env.getAD_User_ID(getCtx()) + ", " + Env.getAD_User_ID(getCtx()) + ", p.M_Product_ID, " + rs.getInt( "M_DiscountSchemaLine_ID" ) + ", z.M_AttributeSetInstance_ID FROM M_Product p, M_ProductPriceInstance z " + " WHERE p.M_Product_ID = z.M_Product_ID" + " AND z.M_PriceList_Version_ID = " + m_PriceList_Version_Base_ID + " AND p.IsActive = 'Y' AND z.IsActive = 'Y'" +  
                										optionalRestrictions.toString());

                no = DB.executeUpdate( sql.toString());
            }

            Message = Message + " @SELECTED@ = " + no;

            // Log.print(Message);

        } catch( Exception e ) {
            ResultStr = ResultStr + ":" + e.getMessage() + " " + Message;
            log.log( Level.SEVERE,"\n " + ResultStr );
        }
        //JOptionPane.showMessageDialog( null,null,"Paramos para ver datos de tabla temporal, I_ProductPrice", JOptionPane.INFORMATION_MESSAGE );
    }    // CreateSelection

    
    /**
     *	Amplia la clausula where inicial con los filtros adicionales
     * @param rs: el resultSet con las distintas reglas
     * @param poBased: Basado en M_Product_PO (en caso de true) o a partir de una lista existente (false)
     */
    protected StringBuffer setOptionalRestrictions(ResultSet rs, boolean poBased)
    {
    	StringBuffer sql = new StringBuffer();
    	try
    	{
            // subfamilia
            if( rs.getInt( "M_Product_Category_ID" ) != 0 )
                sql.append( " AND p.M_Product_Category_ID = " + rs.getInt( "M_Product_Category_ID" ));
            // artículo
            if( rs.getInt( "M_Product_ID" ) != 0 )
                sql.append( " AND p.M_Product_ID = " + rs.getInt( "M_Product_ID" ));
            // marcas
            if( rs.getInt( "M_Product_Family_ID" ) != 0 )
                sql.append( " AND p.M_Product_Family_ID = " + rs.getInt( "M_Product_Family_ID" ));
            // familia
            if( rs.getInt( "M_Product_Gamas_ID" ) != 0 )
                sql.append( " AND p.M_Product_Gamas_ID = " + rs.getInt( "M_Product_Gamas_ID" ));
            // entidad comercial
	    	if (poBased) {
	            if( rs.getInt( "C_BPartner_ID" ) != 0 )
	                sql.append( " AND po.C_BPartner_ID = " + rs.getInt( "C_BPartner_ID" ));
	    	}
	    	else {
	            if( rs.getInt( "C_BPartner_ID" ) != 0 )
	                sql.append( " AND EXISTS (SELECT * FROM M_Product_PO po WHERE po.M_Product_ID = p.M_Product_ID AND po.C_BPartner_ID = " + rs.getInt( "C_BPartner_ID" ) + ")" );
	    	}
	    	// Comprado y Vendido
	    	String optionPS = rs.getString( "SoldPurchasedOption" );
	    	if(!Util.isEmpty(optionPS, true)){
	    		if(!optionPS.equals(MDiscountSchemaLine.SOLDPURCHASEDOPTION_Sold)){
	    			sql.append( " AND (p.IsPurchased = 'Y') " );
	    		}
	    		
	    		if(!optionPS.equals(MDiscountSchemaLine.SOLDPURCHASEDOPTION_Purchased)){
	    			sql.append( " AND (p.IsSold = 'Y') " );
	    		}	    		
	    	}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
       
    	return sql;
    }
    
    
    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    private void deleteTemporarySelection( ResultSet rs ) {
    	log.info( "currupio Estoy ProductPriceTemp.deleteTemporarySelection " );
        ResultStr = "Delete temporary selection";

        try {
            //no = DB.executeUpdate( "DELETE I_ProductPrice " + " WHERE M_Product_ID IN " + " (SELECT M_Product_ID FROM I_ProductPrice WHERE M_DiscountSchemaLine_ID = " + rs.getInt( "M_DiscountSchemaLine_ID" ) + ")" + " AND M_DiscountSchemaLine_ID <> " + rs.getInt( "M_DiscountSchemaLine_ID" ));
        	no = DB.executeUpdate( "DELETE I_ProductPrice " + " WHERE " + getUserSQLCheck() + " AND M_Product_ID IN " + " (SELECT M_Product_ID FROM I_ProductPrice WHERE M_DiscountSchemaLine_ID = " + rs.getInt( "M_DiscountSchemaLine_ID" ) + ")" + " AND M_DiscountSchemaLine_ID <> " + rs.getInt( "M_DiscountSchemaLine_ID" ));
        } catch( Exception e ) {
            ResultStr = ResultStr + ":" + e.getMessage() + " " + Message;
            log.log( Level.SEVERE,"\n " + ResultStr );
        }

        Message = ", @Deleted@" + no;
        log.log(Level.FINE,Message);
    }    // deleteTemporarySelection

    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    private void copyPrices( ResultSet rs ) {
    	log.info( "currupio Estoy ProductPriceTemp.copyPrices, con la sql convertida " );
    	 PreparedStatement pstmt = null;
    	 ResultSet         rs1;
 
    	// si no hay definido una version base de tarifa, se utiliza la información de M_Product_PO
        if( m_PriceList_Version_Base_ID == 0 ) {
        	log.info( "Estoy ProductPriceTemp.copyPrices,Creando una version nueva." );

            // Copy and Convert from Product_PO

            ResultStr = "CopyPrices_PO";
            
            
            

            try {
            	//Nuevo .
                //Problema: La consulta de actualizacion, devuelve multiples valores.Hay que hacer la actualizacion para cada
                //producto. 
                //Solucion: Seleccionamos todos los productos de la tabla temporal, y los vamos actualizando.
            	
            	// Por el momento M_Product_PO no brinda soporte para ASET, por lo tanto no hay información de precio para artíuclos con instancia de atributos
                String sql1 = new String( "SELECT  m_product_id" + " FROM I_ProductPrice WHERE i_isimported <> 'Y' AND M_AttributeSetInstance_ID IS NULL AND " + getUserSQLCheck() );
                
                pstmt = DB.prepareStatement( sql1);
                rs1    = pstmt.executeQuery();
                while( rs1.next()) {
          
              	
                //Fin nuevo
            	//Nueva consulta sql, con la estructura del posgres
            	String sql= "Update I_ProductPrice SET"
            		+ " Pricelist=(select COALESCE(currencyconvert( po.PriceList, po.C_Currency_ID, " + m_Currency_ID + ", mdsl.ConversionDate" + ", " + rs.getInt( "C_ConversionType_ID" ) + ", " + m_Client_ID + ", " + m_Org_ID+"),0)"+" FROM M_Product_PO po" + ", M_DiscountSchemaLine mdsl" +",I_ProductPrice tpp" +", M_Product p WHERE p.M_Product_ID = po.M_Product_ID AND po.M_Product_ID = tpp.M_Product_ID"+" AND tpp.M_Product_ID="+rs1.getInt("m_product_id") + " AND po.IsCurrentVendor = 'Y' AND po.IsActive = 'Y'" + " AND mdsl.M_DiscountSchemaLine_ID=" + rs.getInt( "M_DiscountSchemaLine_ID" ) + " AND p.M_AttributeSet_ID is null)," 
            		+ " PriceStd=(select COALESCE(currencyconvert( po.PriceList, po.C_Currency_ID, " + m_Currency_ID + ", mdsl.ConversionDate" + ", " + rs.getInt( "C_ConversionType_ID" ) + ", " + m_Client_ID + ", " + m_Org_ID + ") , 0)"+" FROM M_Product_PO po" + ", M_DiscountSchemaLine mdsl" +",I_ProductPrice tpp" + ", M_Product p WHERE p.M_Product_ID = po.M_Product_ID AND po.M_Product_ID = tpp.M_Product_ID" +" AND tpp.M_Product_ID="+rs1.getInt("m_product_id")+ " AND po.IsCurrentVendor = 'Y' AND po.IsActive = 'Y'" + " AND mdsl.M_DiscountSchemaLine_ID=" + rs.getInt( "M_DiscountSchemaLine_ID" ) + " AND p.M_AttributeSet_ID is null)," 
            		+ " PriceLimit=(select COALESCE(currencyconvert( po.PricePO, po.C_Currency_ID, " + m_Currency_ID + ", mdsl.ConversionDate" + ", " + rs.getInt( "C_ConversionType_ID" ) + ", " + m_Client_ID + ", " + m_Org_ID + ") , 0)" + " FROM M_Product_PO po" + ", M_DiscountSchemaLine mdsl" +",I_ProductPrice tpp" + ", M_Product p WHERE p.M_Product_ID = po.M_Product_ID AND po.M_Product_ID = tpp.M_Product_ID" +" AND tpp.M_Product_ID="+rs1.getInt("m_product_id")+ " AND po.IsCurrentVendor = 'Y' AND po.IsActive = 'Y'" + " AND mdsl.M_DiscountSchemaLine_ID=" + rs.getInt( "M_DiscountSchemaLine_ID" ) + " AND p.M_AttributeSet_ID is null)"
            		+" WHERE " + getUserSQLCheck() + " AND M_DiscountSchemaLine_ID=" + rs.getInt( "M_DiscountSchemaLine_ID" )+" AND I_ProductPrice.m_product_id= " + rs1.getInt("m_product_id");
                //While
                
                no = DB.executeUpdate( sql );
                }//while
            } catch( Exception e ) {
                ResultStr = ResultStr + ":" + e.getMessage() + " " + Message;
                log.log( Level.SEVERE,"\n " + ResultStr );
            }
           
        // Si se definió una tarifa base... 
        } else {

            // Copy and Convert from other PriceList_Version
        	log.info( "Estoy ProductPriceTemp.copyPrices,Modificando una version existente." );

            ResultStr = "CopyPrices_PL";

            try {
            	//Modificado por ConSerti, mal sentencia update para Postgres.
            	String sql1 = new String( "SELECT  m_product_id, M_AttributeSetInstance_ID FROM I_ProductPrice WHERE i_isimported <> 'Y' AND " + getUserSQLCheck() );
                pstmt = DB.prepareStatement( sql1);
                rs1    = pstmt.executeQuery();
                while( rs1.next()) {
            	String sql= "Update I_ProductPrice SET"
            		+ " previouspricelist = (select pp.PriceList FROM M_ProductPrice pp WHERE pp.M_PriceList_Version_ID = " + m_PriceList_Version_Base_ID + " AND M_Product_ID = " + rs1.getInt("m_product_id")  + ")," 
            		+ " previouspricestd = (select pp.PriceStd FROM M_ProductPrice pp WHERE pp.M_PriceList_Version_ID = " + m_PriceList_Version_Base_ID + " AND M_Product_ID = " + rs1.getInt("m_product_id")  + "),"
            		+ " previouspricelimit = (select pp.Pricelimit FROM M_ProductPrice pp WHERE pp.M_PriceList_Version_ID = " + m_PriceList_Version_Base_ID + " AND M_Product_ID = " + rs1.getInt("m_product_id")  + "),"            		
            		+ " Pricelist=( select COALESCE((" +	getSubQuery(rs, rs1, "M_ProductPriceInstance pp", "pp.PriceList", rs1.getInt("M_AttributeSetInstance_ID"))  
            						  + "),("
            						  +	getSubQuery(rs, rs1, "M_ProductPrice pp", "pp.PriceList", null) + " ))), "
            		+ " PriceStd =( select COALESCE((" +	getSubQuery(rs, rs1, "M_ProductPriceInstance pp", "pp.PriceStd", rs1.getInt("M_AttributeSetInstance_ID"))  
              						  + "),("
              						  +	getSubQuery(rs, rs1, "M_ProductPrice pp", "pp.PriceStd", null) + " ))), "            						  
            		+ " PriceLimit=( select COALESCE(("+	getSubQuery(rs, rs1, "M_ProductPriceInstance pp", "pp.PriceLimit", rs1.getInt("M_AttributeSetInstance_ID")) 
            						  + "),("
            						  +	getSubQuery(rs, rs1, "M_ProductPrice pp", "pp.PriceLimit", null) + " ))), "            						  
					+ " M_PriceList_Version_ID = " + m_PriceList_Version_ID
            		+ " WHERE I_ProductPrice." + getUserSQLCheck() + " AND M_DiscountSchemaLine_ID=" + rs.getInt( "M_DiscountSchemaLine_ID" )+" AND I_ProductPrice.m_product_id= " + rs1.getInt("m_product_id") 
            		+ (rs1.getInt("M_AttributeSetInstance_ID") == 0 ? "AND M_AttributeSetInstance_id isnull" : " AND I_ProductPrice.M_AttributeSetInstance_ID = " + rs1.getInt("M_AttributeSetInstance_ID")); 
            	
            	    log.fine("Em execut update con sql= "+ sql);
            	    no = DB.executeUpdate( sql );
                
              }//while
        	    
            } catch( Exception e ) {
                ResultStr = ResultStr + ":" + e.getMessage() + " " + Message;
                log.log( Level.SEVERE,"\n " + ResultStr );
            }
        }
        //JOptionPane.showMessageDialog( null,null,"Paramos para ver datos despues, I_ProductPrice", JOptionPane.INFORMATION_MESSAGE );

        Message = Message + ", @Inserted@ = " + no;
    }    // CopyPrices

	// Subquery para facilitar la actualización de precios
    protected String getSubQuery(ResultSet rs, ResultSet rs1, String tableName, String priceType, Integer asetID) throws Exception
    {
	     return 
	      " select COALESCE(currencyconvert( " + priceType + ", pl.C_Currency_ID, " + m_Currency_ID + ", mdsl.ConversionDate" + ", " + rs.getInt( "C_ConversionType_ID" ) + ", " + m_Client_ID + ", " + m_Org_ID+"),0) "
	    + " FROM " + tableName  
		+ " INNER JOIN M_PriceList_Version plv ON (pp.M_PriceList_Version_ID = plv.M_PriceList_Version_ID)"
		+ " INNER JOIN M_PriceList pl ON ( plv.M_PriceList_ID = pl.M_PriceList_ID)"
		+ ", M_DiscountSchemaLine mdsl"
		+ " WHERE pp.M_PriceList_Version_ID = " + m_PriceList_Version_Base_ID
		+ " AND pp.M_Product_ID =" + rs1.getInt("m_product_id")
		+ " AND pp.IsActive = 'Y'"
		+ " AND mdsl.M_DiscountSchemaLine_ID=" + rs.getInt("M_DiscountSchemaLine_ID") 
		+ (asetID == null ? " " : " AND pp.M_AttributeSetInstance_ID = " + asetID); 
    }
				
    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    private void updateDiscountStrong( ResultSet rs ) {
    	log.info( "currupio Estoy ProductPriceTemp.updateDisconuntStrong, con la sql convertida " );
    	PreparedStatement pstmt = null;
   	 	ResultSet         rs1;

        // Update I_ProductPrice for DiscountSchemaLine strong

        ResultStr = "updateDiscountStrong";

        try {
        	//Modificado por ConSerTi. En Postgres, no es correcta la sintaxis del update.
           
        	String sql ="select * from I_ProductPrice t " 
        	+ " WHERE " + getUserSQLCheck() + " AND M_DiscountSchemaLine_ID = " 
        	+ rs.getInt( "M_DiscountSchemaLine_ID" ) 
        	+ " AND EXISTS (SELECT t.M_Product_ID " 
        	+ "FROM I_ProductPrice t " 
        	+ "WHERE t.M_Product_ID = M_Product_ID " 
        	+ "AND " + getUserSQLCheck() 
        	+ "AND t.M_DiscountSchemaLine_ID <> "
        	+ rs.getInt( "M_DiscountSchemaLine_ID" ) + ")";
        	pstmt = DB.prepareStatement(sql);
            rs1    = pstmt.executeQuery();
            while( rs1.next()) {
            	 String sql1 = "UPDATE I_ProductPrice set Pricelist= "
            		           +"(Select Pricelist from I_ProductPrice where " + getUserSQLCheck() + " AND M_Product_ID="+ rs1.getInt("M_Product_ID")
            		           +" and M_DiscountSchemaLine_ID<>" + rs.getInt( "M_DiscountSchemaLine_ID" )
            		           + ") , PriceStd = "
            		           +"(Select PriceStd from I_ProductPrice where " + getUserSQLCheck() + " AND M_Product_ID="+ rs1.getInt("M_Product_ID")
            		           +" and M_DiscountSchemaLine_ID<>" + rs.getInt( "M_DiscountSchemaLine_ID" )
            		           + "), PriceLimit = "
            		           +"(Select PriceLimit from I_ProductPrice where " + getUserSQLCheck() + "AND M_Product_ID="+ rs1.getInt("M_Product_ID")
            		           +" and M_DiscountSchemaLine_ID<>" + rs.getInt( "M_DiscountSchemaLine_ID" )
            		           +")"
            		           +" where " + getUserSQLCheck() + " AND M_DiscountSchemaLine_ID =" + rs.getInt( "M_DiscountSchemaLine_ID" )
            		           +" and M_Product_ID="+ rs1.getInt("M_Product_ID") ;
            	 log.fine ("Actualizando = "+ sql1);
            	 no = DB.executeUpdate( sql1);
            }
        	
           log.fine("Se actualizaron en el updateDiscountStrong ---- "+no+" registros."+ sql );
        } catch( Exception e ) {
            ResultStr = ResultStr + ":" + e.getMessage() + " " + Message;
            log.log( Level.SEVERE,"\n " + ResultStr );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    private void calculation( ResultSet rs ) {
    	log.info( "currupio Estoy ProductPriceTemp.calculation, con la sql convertida " );
        ResultStr = "Calculation";

        try {
            StringBuffer sql = new StringBuffer( "UPDATE I_ProductPrice SET" );

            sql.append( " PriceList = " );

            if( rs.getString( "List_Base" ).equalsIgnoreCase("S") ) {
                sql.append( "(PriceStd + " );
            } else if( rs.getString( "List_Base" ).equalsIgnoreCase("X") ) {
                sql.append( "(PriceLimit + " );
            } else {
                sql.append( "(PriceList + " );
            }

            sql.append( rs.getBigDecimal( "List_AddAmt" ) + ") * (1 - " + rs.getBigDecimal( "List_Discount" ) + "/100)," );
            sql.append( " PriceStd = " );

            if( rs.getString( "Std_Base" ).equalsIgnoreCase("L") ) {
                sql.append( "(PriceList + " );
            } else if( rs.getString( "Std_Base" ).equalsIgnoreCase("X") ) {
                sql.append( "(PriceLimit + " );
            } else {
                sql.append( "(PriceStd + " );
            }

            sql.append( rs.getBigDecimal( "Std_AddAmt" ) + ") * (1 - " + rs.getBigDecimal( "Std_Discount" ) + "/100)," );
            sql.append( " PriceLimit = " );

            if( rs.getString( "Limit_Base" ).equalsIgnoreCase("L") ) {
                sql.append( "(PriceList + " );
            } else if( rs.getString( "Limit_Base" ).equalsIgnoreCase("S") ) {
                sql.append( "(PriceStd + " );
            } else {
                sql.append( "(PriceLimit + " );
            }

            sql.append( rs.getBigDecimal( "Limit_AddAmt" ) + ") * (1 - " + rs.getBigDecimal( "Limit_Discount" ) + "/100)" );
            sql.append( " WHERE " + getUserSQLCheck() + " AND M_DiscountSchemaLine_ID = " + rs.getInt( "M_DiscountSchemaLine_ID" ));
            no = DB.executeUpdate( sql.toString());
            rounding( rs );

            // Fixed Price overwrite

            ResultStr = ResultStr + ", Fix";
            sql       = new StringBuffer( "UPDATE I_ProductPrice p SET PriceList = " );

            if( rs.getString( "List_Base" ).equalsIgnoreCase("F") ) {
                sql.append( rs.getBigDecimal( "List_Fixed" ) + " , PriceStd = " );
            } else {
                sql.append( "PriceList , PriceStd = " );
            }

            if( rs.getString( "Std_Base" ).equalsIgnoreCase("F") ) {
                sql.append( rs.getBigDecimal( "Std_Fixed" ) + " , PriceLimit = " );
            } else {
                sql.append( "PriceStd , PriceLimit = " );
            }

            if( rs.getString( "Limit_Base" ).equalsIgnoreCase("F") ) {
                sql.append( rs.getBigDecimal( "Limit_Fixed" ));
            } else {
                sql.append( "PriceLimit" );
            }

            sql.append( " WHERE " + getUserSQLCheck() + " AND p.M_DiscountSchemaLine_ID = " + rs.getInt( "M_DiscountSchemaLine_ID" ));
            no = DB.executeUpdate( sql.toString());
            
			boolean addTax = rs.getString("List_AddProductTax")
					.equalsIgnoreCase("Y")
					|| rs.getString("Limit_AddProductTax")
							.equalsIgnoreCase("Y")
					|| rs.getString("Std_AddProductTax").equalsIgnoreCase("Y");
            
			if(addTax){
	            // Incluir impuesto del artículo
	            sql       = new StringBuffer( "UPDATE I_ProductPrice p SET PriceList = " );

	            String addTaxSql = " (coalesce((select rate from m_product pro inner join c_taxcategory tc on tc.c_taxcategory_id = pro.c_taxcategory_id inner join c_tax t on t.c_taxcategory_id = tc.c_taxcategory_id where pro.m_product_id = p.m_product_id and t.isactive = 'Y' order by t.isdefault desc limit 1),0.00)/100) ";
	            
	            if( rs.getString( "List_AddProductTax" ).equalsIgnoreCase("Y") ) {
	            	
	                sql.append(  "PriceList + (PriceList * " + addTaxSql + ") , PriceStd = " );
	            } else {
	                sql.append( "PriceList , PriceStd = " );
	            }

	            if( rs.getString( "Std_AddProductTax" ).equalsIgnoreCase("Y") ) {
	                sql.append( "PriceStd + (PriceStd * " + addTaxSql + ") , PriceLimit = " );
	            } else {
	                sql.append( "PriceStd , PriceLimit = " );
	            }

	            if( rs.getString( "Limit_AddProductTax" ).equalsIgnoreCase("Y") ) {
	            	sql.append( "PriceLimit + (PriceLimit * " + addTaxSql + ")" );
	            } else {
	                sql.append( "PriceLimit" );
	            }
	            
	            sql.append( " WHERE " + getUserSQLCheck() + " AND p.M_DiscountSchemaLine_ID = " + rs.getInt( "M_DiscountSchemaLine_ID" ));
	            no = DB.executeUpdate( sql.toString());				
			}
			
        } catch( Exception e ) {
            ResultStr = ResultStr + ":" + e.getMessage() + " " + Message;
            log.log( Level.SEVERE,"\n " + ResultStr );
        }
    }    // Calculation

    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    private void rounding( ResultSet rs ) {
    	log.info( "currupio Estoy ProductPriceTemp.rounding, con la sql convertida " );
        ResultStr = ResultStr + ", Round";

        try {
            StringBuffer sql = new StringBuffer( "UPDATE I_ProductPrice p SET PriceList = " );

            if( rs.getString( "List_Rounding" ) == "N" ) {
                sql.append( "PriceList" );
            } else if( rs.getString( "List_Rounding" ) == "0" ) {
                sql.append( "ROUND(PriceList, 0)" );
            } else if( rs.getString( "List_Rounding" ) == "D" ) {
                sql.append( "ROUND(PriceList, 1)" );
            } else if( rs.getString( "List_Rounding" ) == "T" ) {
                sql.append( "ROUND(PriceList, -1)" );
            } else if( rs.getString( "List_Rounding" ) == "5" ) {
                sql.append( "ROUND(PriceList * 20, 0)" );
            } else if( rs.getString( "List_Rounding" ) == "Q" ) {
                sql.append( "ROUND(PriceList * 4, 0)" );
            } else {
                sql.append( "ROUND(PriceList," + m_StdPrecision + ")" );
            }

            sql.append( ", PriceStd = " );

            if( rs.getString( "Std_Rounding" ) == "N" ) {
                sql.append( "PriceStd" );
            } else if( rs.getString( "Std_Rounding" ) == "0" ) {
                sql.append( "ROUND(PriceStd, 0)" );
            } else if( rs.getString( "Std_Rounding" ) == "D" ) {
                sql.append( "ROUND(PriceStd, 1)" );
            } else if( rs.getString( "Std_Rounding" ) == "T" ) {
                sql.append( "ROUND(PriceStd, -1)" );
            } else if( rs.getString( "Std_Rounding" ) == "5" ) {
                sql.append( "ROUND(PriceStd * 20, 0)" );
            } else if( rs.getString( "Std_Rounding" ) == "Q" ) {
                sql.append( "ROUND(PriceStd * 4, 0)" );
            } else {
                sql.append( "ROUND(PriceStd, " + m_StdPrecision + ")" );
            }

            sql.append( ", PriceLimit = " );

            if( rs.getString( "Limit_Rounding" ) == "N" ) {
                sql.append( "PriceLimit" );
            } else if( rs.getString( "Limit_Rounding" ) == "0" ) {
                sql.append( "ROUND(PriceLimit, 0)" );
            } else if( rs.getString( "Limit_Rounding" ) == "D" ) {
                sql.append( "ROUND(PriceLimit, 1)" );
            } else if( rs.getString( "Limit_Rounding" ) == "T" ) {
                sql.append( "ROUND(PriceLimit, -1)" );
            } else if( rs.getString( "Limit_Rounding" ) == "5" ) {
                sql.append( "ROUND(PriceLimit * 20, 0)" );
            } else if( rs.getString( "Limit_Rounding" ) == "Q" ) {
                sql.append( "ROUND(PriceLimit * 4, 0)" );
            } else {
                sql.append( "ROUND(PriceLimit, " + m_StdPrecision + ")" );
            }

            sql.append( " WHERE " + getUserSQLCheck() + " AND M_DiscountSchemaLine_ID=" + rs.getInt( "M_DiscountSchemaLine_ID" ));
            no      = DB.executeUpdate( sql.toString());
            Message = Message + ", @Updated@ = " + no;
        } catch( Exception e ) {
            ResultStr = ResultStr + ":" + e.getMessage() + " " + Message;
            log.log( Level.SEVERE,"\n " + ResultStr );
        }
    }    // Rounding

    /**
     * Descripción de Método
     *
     */

//    private void setSelectedPriceDefault() {
//    	log.info( "currupio Estoy ProductPriceTemp.setSelectedPriceDefault, con la sql convertida " );
//        try {
//            StringBuffer sql = new StringBuffer( "UPDATE I_ProductPrice SET" );
//
//            sql.append( " SelectedPrice = PriceList" );
//            no = DB.executeUpdate( sql.toString());
//        } catch( Exception e ) {
//            ResultStr = ResultStr + ":" + e.getMessage() + " " + Message;
//            log.log( Level.SEVERE,"\n" + ResultStr );
//        }
//    }
    
    /**
     * Sub-Clausula Where para filtrar las tuplas según el usuario conectado 
     */
    protected static String getUserSQLCheck()
    { 
    	return " CreatedBy = " + Env.getAD_User_ID(Env.getCtx()) + " ";
    }
    
}    // ProductPriceTemp



/*
 *  @(#)ProductPriceTemp.java   02.07.07
 * 
 *  Fin del fichero ProductPriceTemp.java
 *  
 *  Versión 2.2
 *
 */
