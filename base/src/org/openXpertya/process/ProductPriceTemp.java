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
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MDiscountSchemaLine;
import org.openXpertya.model.MPriceListVersion;
import org.openXpertya.model.X_M_PriceList_Version;
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

    private String localTrx = null;
    private Properties localCtx = null;
    
    /**
     * Constructor de la clase ...
     *
     */

    public ProductPriceTemp() {
        super();
    }    // ProductPriceTemp
    
	public ProductPriceTemp(Properties ctx, Integer clientID, Integer orgID,
			Integer priceListVersionID, Integer priceListVersionBaseID,
			Integer discountSchemaID, String trxName) {
		localCtx = ctx;
		localTrx = trxName;
    	setM_Client_ID(clientID);
    	setM_Org_ID(orgID);
    	setM_PriceList_Version_ID(priceListVersionID);
    	setM_PriceList_Version_Base_ID(priceListVersionBaseID);
    	setM_DiscountSchema_ID(discountSchemaID);
    }
    
    protected void prepare() {
    	log.info( " currupio Estoy ProductPriceTemp.Prepare" );
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            //JOptionPane.showMessageDialog( null,"En ProducpriceTemp, prepare() con los siguentes parametros para["+i+"]"+"\n"+para[i],"..Fin", JOptionPane.INFORMATION_MESSAGE );

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Client_ID" )) {
                setM_Client_ID(para[ i ].getParameterAsInt());
            } else if( name.equals( "M_PriceList_Version_ID" )) {
                setM_PriceList_Version_ID(para[ i ].getParameterAsInt());
            } else if( name.equals( "AD_Org_ID" )) {
                setM_Org_ID(para[ i ].getParameterAsInt());
            } else if( name.equals( "M_DiscountSchema_ID" )) {
                setM_DiscountSchema_ID(para[ i ].getParameterAsInt());
            } else if( name.equals( "M_PriceList_Version_Base_ID" )) {
                setM_PriceList_Version_Base_ID(para[ i ].getParameterAsInt());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        /*
         * Según:
         * 		https://sourceforge.net/p/libertya/tickets/54/
         * 		http://www.libertya.org/forums/topic/generacion-de-listas-de-precios-via-web-no-funciona/
         * 
         * El boton "Crear" en la pestaña Versión de la ventana Tarifas era capturado en el 
         * actionButton() de la clase APanel.  Bajo el criterio col.equals("ProcCreate"), se
         * invocaba a VProdPricGen la cual se encargaba de instanciar ProductPriceTemp, e 
         * inyectar los argumentos correspondientes del caso.
         * 
         * Dado que no es correcto la captura del evento e invocación desde APanel, se opta por adecuar
         * el botón a finde invocar directamente a esta clase sin intereacción por parte de APanel.
         * El problema es que este proceso NO cuenta con los argumentos a nivel metadatos y por lo tanto
         * para poder determinar la compañía, organización, etc. se deben determinar a partir del
         * registro en donde el usuario está ubicado, y a partir de allí especificarlos.
         * 
         * Sin embargo, es posible que este proceso sea invocado desde otras ubicaciones y en esos
         * casos no deberían (re)definirse los valores de los argumentos si es que los mismos son 
         * inyectados de manera similar quelo hace VProdPricGen.
         * 
         * Por lo tanto: unicamente SI nos encontramos en la ventana de Versión de Lista de Precio 
         * y SI estamos ubicados en un registro en particular, deducir los valores de los parámetros 
         * a partir de la información contenida en el registro en cuestión.
         * 
         * De esta manera es posible omitir el código de APanel y también evitar implementar este
         * tipo de solución en la clase AbstractADWindowPanel de Libertya WEB.
         */
        if (X_M_PriceList_Version.Table_ID == getTable_ID() && getRecord_ID() > 0) {
        	X_M_PriceList_Version plv = new X_M_PriceList_Version (getCtx(), getRecord_ID(), null);
        	if (getM_Client_ID() <= 0)
        		setM_Client_ID(plv.getAD_Client_ID());
        	if (getM_PriceList_Version_ID() <= 0)
        		setM_PriceList_Version_ID(plv.getM_PriceList_Version_ID());
        	if (getM_Org_ID() <= 0)
        		setM_Org_ID(plv.getAD_Org_ID());
        	if (getM_DiscountSchema_ID() <= 0) 
        		setM_DiscountSchema_ID(plv.getM_DiscountSchema_ID());
        	if (getM_PriceList_Version_Base_ID() <= 0)
        		setM_PriceList_Version_Base_ID(plv.getM_Pricelist_Version_Base_ID());
        }
        
        log.equals( getM_PriceList_Version_ID() + "-" + getM_Org_ID() + "-" + getM_Client_ID() + "-" + getM_DiscountSchema_ID() + "-" + getM_PriceList_Version_Base_ID() );
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String doIt() {
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

        updatePriceVariations(getM_PriceList_Version_ID(), null, null);
        
		MPriceListVersion priceListVersion = new MPriceListVersion(getCtx(),
				getM_PriceList_Version_ID(), null);
       
        return priceListVersion.getName();
    }    // doIt

    // PO Prices must exists

    
    /**
     * Actualizar las entradas en la tabla de importación de listas de precio,
     * pudiendo especificar tanto la versión de lista de precio como el registro 
     * de la tabla I_ProductPrice a fin de filtrar adecuadamente la actualización 
     */
    public static void updatePriceVariations(Integer priceListVersionID, Integer recordNo, String trxName)
    {
    	// actualizar las variaciones
	    DB.executeUpdate(	" UPDATE I_ProductPrice " + 
				" SET variationpricelist = (CASE WHEN previouspricelist is not null AND previouspricelist <> 0 THEN pricelist / previouspricelist ELSE pricelist END), " +
				" 		variationpricelimit = (CASE WHEN previouspricelimit is not null AND previouspricelimit <> 0 THEN pricelimit / previouspricelimit ELSE pricelimit END), " +
				" 		variationpricestd = (CASE WHEN previouspricestd is not null AND previouspricestd <> 0 THEN pricestd / previouspricestd ELSE pricestd END) " +
				" WHERE " + getUserSQLCheck() + 
				" AND i_isimported = 'N' AND processed = 'N' " +
				(priceListVersionID == null ? "" : " AND M_PriceList_Version_ID = " + priceListVersionID) +
				(recordNo == null ? "" : " AND I_ProductPrice_ID = " + recordNo) ,
				 true, trxName, true);
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean updatePricesMProductPO() {
        setResultStr("CorrectingProductPO");

        try {
			setNo(DB.executeUpdate("UPDATE M_Product_PO" + " SET PriceList = 0 "
					+ " WHERE PriceList IS NULL",  null));
			setNo(getNo() + DB.executeUpdate("UPDATE M_Product_PO"
					+ " SET PriceLastPO = 0" + " WHERE PriceLastPo IS NULL",
					null));
			setNo(getNo() + DB.executeUpdate(
					"UPDATE M_Product_PO"
							+ " SET PricePO = PriceLastPO"
							+ " WHERE (PricePO IS NULL OR PricePO = 0) AND PriceLastPO <> 0",
							null));
			setNo(getNo() + DB.executeUpdate("UPDATE M_Product_PO" + " SET PricePO = 0"
					+ " WHERE PricePo IS NULL", null));
            setMessage("Updated " + getNo() + " Prices ");

            return true;
        } catch( Exception e ) {
            setResultStr("ERROR \t" + getResultStr() + ":" + e.getMessage() + " " + getMessage());
            log.severe( getResultStr() );

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
    	boolean ok = true;
        setResultStr("defaultCurrentVendor");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
			// Obtengo los artículos que están asociados a proveedores que no
			// están marcados como proveedor actual 
        	String query = "select distinct m_product_id " +
        				   "from m_product_po " +
        				   "where ad_client_id = ? " +
        				   "except " +
        				   "select distinct m_product_id " +
        				   "from m_product_po " +
        				   "where ad_client_id = ? and iscurrentvendor = 'Y' and isactive = 'Y'";
        	ps = DB.prepareStatement(query, null);
        	ps.setInt(1, Env.getAD_Client_ID(getCtx()));
        	ps.setInt(2, Env.getAD_Client_ID(getCtx()));
        	rs = ps.executeQuery();
        	Integer productID;
			// Actualizo el proveedor actual para aquellos artículos que no
			// tiene ninguno marcado
        	while(rs.next()){
        		productID = rs.getInt("m_product_id");
				setNo(getNo() + DB.executeUpdate("update m_product_po "
						+ "set iscurrentvendor = 'Y' "
						+ "where m_product_id = " + productID
						+ " and c_bpartner_id = (select c_bpartner_id "
						+ "from m_product_po where isactive = 'Y' and m_product_id = " + productID
						+ "order by created desc " 
						+ "limit 1)", null));
        	}
            setMessage("Updated " + getNo() + " Current Vendors");
        } catch( Exception e ) {
            setResultStr("ERROR \t" + getResultStr() + ":" + e.getMessage() + " " + getMessage());
            log.severe( getResultStr() );
            ok = false;
        } finally {
        	try {
        		if(rs != null) rs.close();
				if(ps != null) ps.close();
			} catch (Exception e2) {
				setResultStr("ERROR \t" + getResultStr() + ":" + e2.getMessage() + " " + getMessage());
	            log.severe( getResultStr() );
			}
        }
        return ok;
    }    // DefaultCurrentVendor

    /**
     * Descripción de Método
     *
     */

    private void correctingDuplicates() {
        setResultStr("correctingDuplicates");

        String sql   = null;
        String c_sql = null;

        // All duplicates products

        sql = "select m_product_id, count(*) " +
        		"from m_product_po " +
        		"where ad_client_id = ? and iscurrentvendor = 'Y' and isactive = 'Y' " +
        		"group by m_product_id " +
        		"having count(*) > 1";

        PreparedStatement pstmt   = null;
        PreparedStatement c_pstmt = null;
        ResultSet         rs = null;
        ResultSet         c_rs = null;

        try {
            pstmt = DB.prepareStatement( sql, null);
            pstmt.setInt(1, Env.getAD_Client_ID(getCtx()));
            rs    = pstmt.executeQuery();
			// La fecha de creación es la que se toma para determinar el
			// actual provedor 
            while( rs.next()) {
                c_sql = "select m_product_id, c_bpartner_id " +
                		"from m_product_po " +
                		"where m_product_id = ? and iscurrentvendor = 'Y' and isactive = 'Y' " +
                		"order by created desc " +
                		"limit 1";
                c_pstmt = DB.prepareStatement( c_sql, null );
                c_pstmt.setInt(1, rs.getInt("m_product_id"));
                c_rs    = c_pstmt.executeQuery();
                if(c_rs.next()){
					setNo(getNo() + DB.executeUpdate("update m_product_po "
							+ "set iscurrentvendor = 'N' "
							+ "where m_product_id = "
							+ c_rs.getInt("m_product_id")
							+ " and c_bpartner_id <> "
							+ c_rs.getInt("c_bpartner_id"), null));
                }
            }
        } catch( Exception e ) {
            setResultStr(getResultStr() + ":" + e.getMessage() + " " + getMessage());
            log.severe( getResultStr() );
        } finally{
        	try {
				if(c_rs != null)c_rs.close();
				if(c_pstmt != null)c_pstmt.close();
				if(rs != null)rs.close();
				if(pstmt != null)pstmt.close();
			} catch (Exception e2) {
				setResultStr(getResultStr() + ":" + e2.getMessage() + " " + getMessage());
	            log.severe( getResultStr() );
			}
        }
    }    // CorrectingDuplicates

    // Get PriceList Info

    /**
     * Descripción de Método
     *
     */

    private void priceListInfo() {
        setResultStr("priceListInfo");

		String sql = new String("SELECT p.C_Currency_ID, c.StdPrecision"
				+ " FROM M_PriceList p, M_PriceList_Version v, C_Currency c"
				+ " WHERE p.M_PriceList_ID = v.M_PriceList_ID"
				+ " AND p.C_Currency_ID = c.C_Currency_ID"
				+ " AND v.M_PriceList_Version_ID = " + getM_PriceList_Version_ID());
        PreparedStatement pstmt = null;
        ResultSet         rs = null;

        try {
            pstmt = DB.prepareStatement( sql, null );
            rs    = pstmt.executeQuery();

            if( rs.next()) {
                m_Currency_ID  = rs.getInt( 1 );
                m_StdPrecision = rs.getInt( 2 );
            }
        } catch( Exception e ) {
            setResultStr(getResultStr() + ":" + e.getMessage() + " " + sql);
            log.severe( "ERROR PriceListInfo " + getResultStr() );
        } finally {
        	try {
        		if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (Exception e2) {
				setResultStr("ERROR \t" + getResultStr() + ":" + e2.getMessage() + " " + getMessage());
	            log.severe( getResultStr() );
			}
        }
    }    // PriceListInfo

    // Create de Selection for all discount schema line

    protected String getDiscountSchemaLinesQuery(){
		StringBuffer sql = new StringBuffer("SELECT distinct dsl.* FROM M_DiscountSchemaLine dsl ");
		if(getM_Org_ID() > 0){
			sql.append("INNER JOIN m_discountschemaline_org_application_v dsloav on dsloav.m_discountschemaline_id = dsl.m_discountschemaline_id ");
		}
		sql.append("WHERE dsl.M_DiscountSchema_ID = ").append(getM_DiscountSchema_ID());
		if(getM_Org_ID() > 0){
			sql.append(" and dsloav.ad_org_id = ").append(getM_Org_ID());
		}
		sql.append(" ORDER BY dsl.SeqNo ");
    	return sql.toString();
    }
    
    /**
     * Descripción de Método
     *
     */

    private void discountLine() {
        String sql = getDiscountSchemaLinesQuery();
        PreparedStatement pstmt = null;
        ResultSet         rs = null;

        try {
        	// Eliminar solo registros del usuario en cuestión (luego en la importación también se filtra por dicho criterio)
			setNo(DB.executeUpdate("DELETE FROM I_ProductPrice WHERE "
					+ getUserSQLCheck() + " AND m_pricelist_version_id = "+getM_PriceList_Version_ID(), null));
            pstmt = DB.prepareStatement( sql, null );
            rs    = pstmt.executeQuery();

            while( rs.next()) {
                setResultStr(" Parameter Seq = " + rs.getInt( "SeqNo" ));
                // Inserción de precios
                createSelection( rs );
                if( "N".equals( rs.getString( "IsStrong" ))) {
                    deleteTemporarySelection( rs );
                }
                // Copia de precios
                copyPrices( rs );
                if( "Y".equals( rs.getString( "IsStrong" ))) {       	
                    updateDiscountStrong( rs );
                    deleteTemporarySelection( rs );
                }
                // Cálculos de regla de precios
                calculation( rs );
                setMessage("");
            }
        } catch( Exception e ) {
            setResultStr(getResultStr() + ":" + e.getMessage() + " " + getMessage());
            log.severe( getResultStr() );
        } finally {
        	try {
        		if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (Exception e2) {
				setResultStr("ERROR \t" + getResultStr() + ":" + e2.getMessage() + " " + getMessage());
	            log.severe( getResultStr() );
			}
        }
    }            // DiscountLine

    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    protected void createSelection( ResultSet rs ) {
        setResultStr("create selection");
		StringBuffer optionalRestrictions = setOptionalRestrictions(rs,
				getM_PriceList_Version_Base_ID() == 0); 
        
        try {
            if( getM_PriceList_Version_Base_ID() == 0 ) {

                // Create Selection from M_Product_PO

                StringBuffer sql = new StringBuffer( 	"INSERT INTO I_ProductPrice (AD_Client_ID, AD_Org_ID, CreatedBy, UpdatedBy, M_Product_ID, M_DiscountSchemaLine_ID, M_PriceList_Version_ID, M_AttributeSetInstance_ID)" +
                										" SELECT DISTINCT p.AD_Client_ID, p.AD_Org_ID, " + Env.getAD_User_ID(getCtx()) + ", " + Env.getAD_User_ID(getCtx()) + ", po.M_Product_ID, " + rs.getInt( "M_DiscountSchemaLine_ID" ) + ", " + getM_PriceList_Version_ID() + ", aseti.M_AttributeSetInstance_ID::int " + 
                										" FROM M_Product p " + 
                										" INNER JOIN M_Product_PO po ON p.M_Product_ID = po.M_Product_ID  " +
                										" INNER JOIN M_AttributeSet aset ON p.M_AttributeSet_ID = aset.M_AttributeSet_ID " +
                										" INNER JOIN M_AttributeSetInstance aseti ON aset.M_AttributeSet_ID = aseti.M_AttributeSet_ID " +
                										" INNER JOIN M_Product_Category pc on pc.M_Product_Category_ID = p.M_Product_Category_ID " + 
                										" LEFT JOIN M_Product_Gamas pg ON pg.M_Product_Gamas_ID = pc.M_Product_Gamas_ID " +
                										" LEFT JOIN M_Product_Lines pl ON pl.M_Product_Lines_ID = pg.M_Product_Lines_ID " +
                										" WHERE p.M_Product_ID = po.M_Product_ID " + " AND (p.AD_Client_ID = " + getM_Client_ID() + " OR p.AD_Client_ID = 0 )" + " AND p.IsActive = 'Y' AND po.IsActive = 'Y' AND po.IsCurrentVendor = 'Y'"  +
                										optionalRestrictions.toString() +
                										" UNION " +
                										" SELECT DISTINCT p.AD_Client_ID, p.AD_Org_ID, " + Env.getAD_User_ID(getCtx()) + ", " + Env.getAD_User_ID(getCtx()) + ", po.M_Product_ID, " + rs.getInt( "M_DiscountSchemaLine_ID" )  + ", " + getM_PriceList_Version_ID() + ", null::int " + 
                										" FROM M_Product p " +
                										" INNER JOIN M_Product_PO po ON p.M_Product_ID = po.M_Product_ID " +
                										" INNER JOIN M_Product_Category pc on pc.M_Product_Category_ID = p.M_Product_Category_ID " + 
                										" LEFT JOIN M_Product_Gamas pg ON pg.M_Product_Gamas_ID = pc.M_Product_Gamas_ID " +
                										" LEFT JOIN M_Product_Lines pl ON pl.M_Product_Lines_ID = pg.M_Product_Lines_ID " +
                										" WHERE (p.AD_Client_ID = " + getM_Client_ID() + " OR p.AD_Client_ID = 0 )" + " AND p.IsActive = 'Y' AND po.IsActive = 'Y' AND po.IsCurrentVendor = 'Y' AND p.M_AttributeSet_ID IS NULL " + 
                										optionalRestrictions.toString() ); 

                setNo(DB.executeUpdate( sql.toString(), null));
            } else {

                // Create Selection from existing PriceList

                StringBuffer sql = new StringBuffer(  	" INSERT INTO I_ProductPrice (AD_Client_ID, AD_Org_ID, CreatedBy, UpdatedBy, M_Product_ID, M_DiscountSchemaLine_ID, M_AttributeSetInstance_ID, m_pricelist_version_id) " + 
                										" SELECT DISTINCT p.AD_Client_ID, p.AD_Org_ID, " + Env.getAD_User_ID(getCtx()) + ", " + Env.getAD_User_ID(getCtx()) + ", p.M_Product_ID, " + rs.getInt( "M_DiscountSchemaLine_ID" ) + ", null::integer, " +getM_PriceList_Version_ID() +
                										" FROM M_Product p " +
                										" INNER JOIN M_ProductPrice z ON p.M_Product_ID = z.M_Product_ID " + 
                										" INNER JOIN M_Product_Category pc on pc.M_Product_Category_ID = p.M_Product_Category_ID " + 
                										" LEFT JOIN M_Product_Gamas pg ON pg.M_Product_Gamas_ID = pc.M_Product_Gamas_ID " +
                										" LEFT JOIN M_Product_Lines pl ON pl.M_Product_Lines_ID = pg.M_Product_Lines_ID " +
                										" WHERE z.M_PriceList_Version_ID = " + getM_PriceList_Version_Base_ID() + " AND p.IsActive = 'Y' AND z.IsActive = 'Y'" +
                										optionalRestrictions.toString() +
                										" UNION " +
                										" SELECT DISTINCT p.AD_Client_ID, p.AD_Org_ID, " + Env.getAD_User_ID(getCtx()) + ", " + Env.getAD_User_ID(getCtx()) + ", p.M_Product_ID, " + rs.getInt( "M_DiscountSchemaLine_ID" ) + ", z.M_AttributeSetInstance_ID, " +getM_PriceList_Version_ID() +
                										" FROM M_Product p " +
                										" INNER JOIN M_ProductPriceInstance z ON p.M_Product_ID = z.M_Product_ID " + 
                										" INNER JOIN M_Product_Category pc on pc.M_Product_Category_ID = p.M_Product_Category_ID " + 
                										" LEFT JOIN M_Product_Gamas pg ON pg.M_Product_Gamas_ID = pc.M_Product_Gamas_ID " +
                										" LEFT JOIN M_Product_Lines pl ON pl.M_Product_Lines_ID = pg.M_Product_Lines_ID " +
                										" WHERE z.M_PriceList_Version_ID = " + getM_PriceList_Version_Base_ID() + " AND p.IsActive = 'Y' AND z.IsActive = 'Y'" +  
                										optionalRestrictions.toString());

                setNo(DB.executeUpdate( sql.toString(), null));
            }
            setMessage(getMessage() + " @SELECTED@ = " + getNo());
        } catch( Exception e ) {
            setResultStr(getResultStr() + ":" + e.getMessage() + " " + getMessage());
            log.severe( getResultStr() );
        }
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
                sql.append( " AND pg.M_Product_Gamas_ID = " + rs.getInt( "M_Product_Gamas_ID" ));
            // Línea de artículo
            if(rs.getInt( "M_Product_Lines_ID" ) != 0)
            	sql.append( " AND pl.M_Product_Lines_ID = " + rs.getInt( "M_Product_Lines_ID" ));
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
        setResultStr("Delete temporary selection");

        try {
			setNo(DB.executeUpdate("DELETE I_ProductPrice pp "
					+ " WHERE "
					+ getUserSQLCheck()
					+ " AND m_pricelist_version_id =  "+getM_PriceList_Version_ID()
					+ " AND EXISTS "
					+ " (SELECT M_Product_ID FROM I_ProductPrice pp2 WHERE M_DiscountSchemaLine_ID = "
					+ rs.getInt("M_DiscountSchemaLine_ID") + " AND pp2.M_Product_ID = pp.M_Product_ID "
					+ " AND m_pricelist_version_id =  "+getM_PriceList_Version_ID()
					+")"
					+ " AND M_DiscountSchemaLine_ID <> "
					+ rs.getInt("M_DiscountSchemaLine_ID"), null));
        } catch( Exception e ) {
            setResultStr(getResultStr() + ":" + e.getMessage() + " " + getMessage());
            log.severe( getResultStr() );
        }

        setMessage(", @Deleted@" + getNo());
    }    // deleteTemporarySelection

    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    private void copyPrices( ResultSet rs ) {
    	 PreparedStatement pstmt = null;
    	 ResultSet         rs1 = null;
 
    	 try{
    	 
	    	 // si no hay definido una version base de tarifa, se utiliza la información de M_Product_PO
	    	 if( getM_PriceList_Version_Base_ID() == 0 ) {
	    		 // Copy and Convert from Product_PO
	    		 setResultStr("CopyPrices_PO");
	            	//Nuevo .
	                //Problema: La consulta de actualizacion, devuelve multiples valores.Hay que hacer la actualizacion para cada
	                //producto. 
	                //Solucion: Seleccionamos todos los productos de la tabla temporal, y los vamos actualizando.
	            	
	            	// Por el momento M_Product_PO no brinda soporte para ASET, por lo tanto no hay información de precio para artíuclos con instancia de atributos
					String sql1 = new String(
							"SELECT  m_product_id"
									+ " FROM I_ProductPrice WHERE i_isimported <> 'Y' AND M_AttributeSetInstance_ID IS NULL AND "
									+ getUserSQLCheck());
	                
	                pstmt = DB.prepareStatement( sql1, null);
	                rs1    = pstmt.executeQuery();
	                while( rs1.next()) {
		            	String sql= "Update I_ProductPrice SET"
		            		+ " Pricelist=(select COALESCE(currencyconvert( po.PriceList, po.C_Currency_ID, " + m_Currency_ID + ", mdsl.ConversionDate" + ", " + rs.getInt( "C_ConversionType_ID" ) + ", " + getM_Client_ID() + ", " + getM_Org_ID()+"),0)"+" FROM M_Product_PO po" + ", M_DiscountSchemaLine mdsl" +",I_ProductPrice tpp" +", M_Product p WHERE p.M_Product_ID = po.M_Product_ID AND po.M_Product_ID = tpp.M_Product_ID"+" AND tpp.M_Product_ID="+rs1.getInt("m_product_id") + " AND po.IsCurrentVendor = 'Y' AND po.IsActive = 'Y'" + " AND mdsl.M_DiscountSchemaLine_ID=" + rs.getInt( "M_DiscountSchemaLine_ID" ) + " AND p.M_AttributeSet_ID is null)," 
		            		+ " PriceStd=(select COALESCE(currencyconvert( po.PriceList, po.C_Currency_ID, " + m_Currency_ID + ", mdsl.ConversionDate" + ", " + rs.getInt( "C_ConversionType_ID" ) + ", " + getM_Client_ID() + ", " + getM_Org_ID() + ") , 0)"+" FROM M_Product_PO po" + ", M_DiscountSchemaLine mdsl" +",I_ProductPrice tpp" + ", M_Product p WHERE p.M_Product_ID = po.M_Product_ID AND po.M_Product_ID = tpp.M_Product_ID" +" AND tpp.M_Product_ID="+rs1.getInt("m_product_id")+ " AND po.IsCurrentVendor = 'Y' AND po.IsActive = 'Y'" + " AND mdsl.M_DiscountSchemaLine_ID=" + rs.getInt( "M_DiscountSchemaLine_ID" ) + " AND p.M_AttributeSet_ID is null)," 
		            		+ " PriceLimit=(select COALESCE(currencyconvert( po.PricePO, po.C_Currency_ID, " + m_Currency_ID + ", mdsl.ConversionDate" + ", " + rs.getInt( "C_ConversionType_ID" ) + ", " + getM_Client_ID() + ", " + getM_Org_ID() + ") , 0)" + " FROM M_Product_PO po" + ", M_DiscountSchemaLine mdsl" +",I_ProductPrice tpp" + ", M_Product p WHERE p.M_Product_ID = po.M_Product_ID AND po.M_Product_ID = tpp.M_Product_ID" +" AND tpp.M_Product_ID="+rs1.getInt("m_product_id")+ " AND po.IsCurrentVendor = 'Y' AND po.IsActive = 'Y'" + " AND mdsl.M_DiscountSchemaLine_ID=" + rs.getInt( "M_DiscountSchemaLine_ID" ) + " AND p.M_AttributeSet_ID is null)"
		            		+" WHERE " + getUserSQLCheck() + " AND M_DiscountSchemaLine_ID=" + rs.getInt( "M_DiscountSchemaLine_ID" )+" AND I_ProductPrice.m_product_id= " + rs1.getInt("m_product_id");
		                
		                setNo(DB.executeUpdate( sql, null));
	                }//while
	        // Si se definió una tarifa base... 
	        } else {
	            // Copy and Convert from other PriceList_Version
	            setResultStr("CopyPrices_PL");
		    	//Modificado por ConSerti, mal sentencia update para Postgres.
				String sql = new String("SELECT  ipp.m_product_id, " +
												"ipp.M_AttributeSetInstance_ID, " +
												"COALESCE(pp.pricelist,0.00) as previouspricelist, " +
												"COALESCE(pp.pricestd,0.00) as previouspricestd, " +
												"COALESCE(pp.pricelimit,0.00) as previouspricelimit, " +
												"COALESCE(currencyconvert( COALESCE(ppi.PriceList, pp.PriceList), COALESCE(pli.C_Currency_ID, pl.C_Currency_ID), "
																				+ m_Currency_ID
																				+ ", mdsl.ConversionDate, "
																				+ rs.getInt("C_ConversionType_ID")
																				+ ", "
																				+ getM_Client_ID()
																				+ ", "
																				+ getM_Org_ID()
																				+ "),0) as pricelist, "
																				+
												"COALESCE(currencyconvert( COALESCE(ppi.PriceStd, pp.PriceStd), COALESCE(pli.C_Currency_ID, pl.C_Currency_ID), "
																				+ m_Currency_ID
																				+ ", mdsl.ConversionDate, "
																				+ rs.getInt("C_ConversionType_ID")
																				+ ", "
																				+ getM_Client_ID()
																				+ ", "
																				+ getM_Org_ID()
																				+ "),0) as pricestd, "
																				+
												"COALESCE(currencyconvert( COALESCE(ppi.PriceLimit, pp.PriceLimit), COALESCE(pli.C_Currency_ID, pl.C_Currency_ID), "
																				+ m_Currency_ID
																				+ ", mdsl.ConversionDate, "
																				+ rs.getInt("C_ConversionType_ID")
																				+ ", "
																				+ getM_Client_ID()
																				+ ", "
																				+ getM_Org_ID()
																				+ "),0) as pricelimit "
																				+
											"FROM I_ProductPrice as ipp " +
											"LEFT JOIN M_ProductPrice as pp ON pp.m_product_id = ipp.m_product_id " +
											"LEFT JOIN M_ProductPriceInstance as ppi ON ppi.m_product_id = ipp.m_product_id AND ppi.M_AttributeSetInstance_ID = ipp.M_AttributeSetInstance_ID " +
											"LEFT JOIN M_PriceList_Version as plv ON pp.M_PriceList_Version_ID = plv.M_PriceList_Version_ID " +
											"LEFT JOIN M_PriceList as pl ON plv.M_PriceList_ID = pl.M_PriceList_ID " +
											"LEFT JOIN M_PriceList_Version as plvi ON ppi.M_PriceList_Version_ID = plvi.M_PriceList_Version_ID " +
											"LEFT JOIN M_PriceList as pli ON plvi.M_PriceList_ID = pli.M_PriceList_ID, M_DiscountSchemaLine mdsl " +
											"WHERE i_isimported <> 'Y' " +
													"AND (CASE WHEN ipp.m_attributesetinstance_id is null " +
																"THEN pp.M_PriceList_Version_ID = " + getM_PriceList_Version_Base_ID() +
																"ELSE ppi.M_PriceList_Version_ID = " + getM_PriceList_Version_Base_ID() + " END)" +
													"AND (CASE WHEN ipp.m_attributesetinstance_id is null " +
																"THEN pp.IsActive = 'Y' " +
																"ELSE ppi.IsActive = 'Y' END) " +
													"AND mdsl.M_DiscountSchemaLine_ID = "+rs.getInt("M_DiscountSchemaLine_ID") + 
													" AND ipp."+getUserSQLCheck());
		        pstmt = DB.prepareStatement( sql, null);
		        rs1    = pstmt.executeQuery();
		        StringBuffer sqlUpdate;
		        while( rs1.next()) {
		        	sqlUpdate = new StringBuffer("Update I_ProductPrice SET ");
		        	sqlUpdate.append(" previouspricelist = ").append(rs1.getBigDecimal("previouspricelist")).append(" , ");
		        	sqlUpdate.append(" previouspricestd = ").append(rs1.getBigDecimal("previouspricestd")).append(" , ");
		        	sqlUpdate.append(" previouspricelimit = ").append(rs1.getBigDecimal("previouspricelimit")).append(" , ");
		        	sqlUpdate.append(" pricelist = ").append(rs1.getBigDecimal("pricelist")).append(" , ");
		        	sqlUpdate.append(" pricestd = ").append(rs1.getBigDecimal("pricestd")).append(" , ");
		        	sqlUpdate.append(" pricelimit = ").append(rs1.getBigDecimal("pricelimit"));
		        	sqlUpdate.append(" WHERE ");
		        	sqlUpdate.append(" m_product_id = ").append(rs1.getInt("m_product_id"));
					sqlUpdate
							.append(" AND ")
							.append(rs1.getInt("M_AttributeSetInstance_ID") == 0 ? " M_AttributeSetInstance_id is null "
									: " M_AttributeSetInstance_ID = "
											+ rs1.getInt("M_AttributeSetInstance_ID"));
		        	sqlUpdate.append(" AND ").append(getUserSQLCheck());
					sqlUpdate.append(" AND M_DiscountSchemaLine_ID="
							+ rs.getInt("M_DiscountSchemaLine_ID"));
					sqlUpdate.append(" AND M_PriceList_Version_ID = ").append(getM_PriceList_Version_ID());
					
		    	    setNo(DB.executeUpdate( sqlUpdate.toString(),null ));
		        }//while
	        }
        } catch( Exception e ) {
            setResultStr(getResultStr() + ":" + e.getMessage() + " " + getMessage());
            log.severe( getResultStr() );
        } finally {
        	try {
        		if(rs1 != null) rs1.close();
				if(pstmt != null) pstmt.close();
			} catch (Exception e2) {
				setResultStr("ERROR \t" + getResultStr() + ":" + e2.getMessage() + " " + getMessage());
	            log.severe( getResultStr() );
			}
        }
        setMessage(getMessage() + ", @Inserted@ = " + getNo());
    }    // CopyPrices

	// Subquery para facilitar la actualización de precios
    protected String getSubQuery(ResultSet rs, ResultSet rs1, String tableName, String priceType, Integer asetID) throws Exception
    {
	     return 
	      " select COALESCE(currencyconvert( " + priceType + ", pl.C_Currency_ID, " + m_Currency_ID + ", mdsl.ConversionDate" + ", " + rs.getInt( "C_ConversionType_ID" ) + ", " + getM_Client_ID() + ", " + getM_Org_ID()+"),0) "
	    + " FROM " + tableName  
		+ " INNER JOIN M_PriceList_Version plv ON (pp.M_PriceList_Version_ID = plv.M_PriceList_Version_ID)"
		+ " INNER JOIN M_PriceList pl ON ( plv.M_PriceList_ID = pl.M_PriceList_ID)"
		+ ", M_DiscountSchemaLine mdsl"
		+ " WHERE pp.M_PriceList_Version_ID = " + getM_PriceList_Version_Base_ID()
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
    	PreparedStatement pstmt = null;
   	 	ResultSet         rs1 = null;

        // Update I_ProductPrice for DiscountSchemaLine strong

        setResultStr("updateDiscountStrong");

        try {
        	//Modificado por ConSerTi. En Postgres, no es correcta la sintaxis del update.
           
        	String sql ="select * from I_ProductPrice t " 
        	+ " WHERE " + getUserSQLCheck() + " AND M_DiscountSchemaLine_ID = " 
        	+ rs.getInt( "M_DiscountSchemaLine_ID" ) 
        	+ " AND EXISTS (SELECT t2.M_Product_ID " 
        	+ "FROM I_ProductPrice t2 " 
        	+ "WHERE t.M_Product_ID = t2.M_Product_ID " 
        	+ "AND " + getUserSQLCheck() 
        	+ "AND t2.M_DiscountSchemaLine_ID <> "
        	+ rs.getInt( "M_DiscountSchemaLine_ID" )
        	+" AND t2.m_pricelist_version_id = "+getM_PriceList_Version_ID() 
        	+ ")" 
        	+" AND m_pricelist_version_id = "+getM_PriceList_Version_ID() ;
        	pstmt = DB.prepareStatement(sql, null);
            rs1    = pstmt.executeQuery();
            while( rs1.next()) {
            	 String sql1 = "UPDATE I_ProductPrice set Pricelist= "
            		           +"coalesce((Select Pricelist from I_ProductPrice where " + getUserSQLCheck() + " AND M_Product_ID="+ rs1.getInt("M_Product_ID")
            		           +" and M_DiscountSchemaLine_ID<>" + rs.getInt( "M_DiscountSchemaLine_ID" )
            		           +" AND m_pricelist_version_id = "+getM_PriceList_Version_ID()
            		           + "),0.00) , PriceStd = "
            		           +"coalesce((Select PriceStd from I_ProductPrice where " + getUserSQLCheck() + " AND M_Product_ID="+ rs1.getInt("M_Product_ID")
            		           +" and M_DiscountSchemaLine_ID<>" + rs.getInt( "M_DiscountSchemaLine_ID" )
            		           +" AND m_pricelist_version_id = "+getM_PriceList_Version_ID() 
            		           + "),0.00) , PriceLimit = "
            		           +"coalesce((Select PriceLimit from I_ProductPrice where " + getUserSQLCheck() + "AND M_Product_ID="+ rs1.getInt("M_Product_ID")
            		           +" and M_DiscountSchemaLine_ID<>" + rs.getInt( "M_DiscountSchemaLine_ID" )
            		           +" AND m_pricelist_version_id = "+getM_PriceList_Version_ID() 
            		           +"), 0.00)"
            		           +" where " + getUserSQLCheck() + " AND M_DiscountSchemaLine_ID =" + rs.getInt( "M_DiscountSchemaLine_ID" )
            		           +" and M_Product_ID="+ rs1.getInt("M_Product_ID")
            	 			   +" AND m_pricelist_version_id = "+getM_PriceList_Version_ID();
            	 setNo(DB.executeUpdate( sql1, null));
            }
        } catch( Exception e ) {
            setResultStr(getResultStr() + ":" + e.getMessage() + " " + getMessage());
            log.severe( getResultStr() );
        } finally {
        	try {
        		if(rs1 != null) rs1.close();
				if(pstmt != null) pstmt.close();
			} catch (Exception e2) {
				setResultStr("ERROR \t" + getResultStr() + ":" + e2.getMessage() + " " + getMessage());
	            log.severe( getResultStr() );
			}
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    private void calculation( ResultSet rs ) {
        setResultStr("Calculation");

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

			sql.append(rs.getBigDecimal("Limit_AddAmt") + ") * (1 - "
					+ rs.getBigDecimal("Limit_Discount") + "/100)");
			sql.append(" WHERE " + getUserSQLCheck()
					+ " AND M_DiscountSchemaLine_ID = "
					+ rs.getInt("M_DiscountSchemaLine_ID"));
			sql.append(" AND m_pricelist_version_id = ").append(getM_PriceList_Version_ID());
            setNo(DB.executeUpdate( sql.toString(), null));

            // Fixed Price overwrite

            setResultStr(getResultStr() + ", Fix");
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

			sql.append(" WHERE " + getUserSQLCheck()
					+ " AND p.M_DiscountSchemaLine_ID = "
					+ rs.getInt("M_DiscountSchemaLine_ID"));
			sql.append(" AND m_pricelist_version_id = ").append(getM_PriceList_Version_ID());
            setNo(DB.executeUpdate( sql.toString(), null));
            
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
	            sql.append(" AND m_pricelist_version_id = ").append(getM_PriceList_Version_ID());
	            setNo(DB.executeUpdate( sql.toString(), null));				
			}
			
			// Redondeo al final de todo
            rounding( rs );
			
        } catch( Exception e ) {
            setResultStr(getResultStr() + ":" + e.getMessage() + " " + getMessage());
            log.severe( getResultStr() );
        }
    }    // Calculation

    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    private void rounding( ResultSet rs ) {
        setResultStr(getResultStr() + ", Round");

        try {
            StringBuffer sql = new StringBuffer( "UPDATE I_ProductPrice p SET " );
            sql.append(" PriceList = ");
            sql.append(getRoundingSQL(rs.getString("List_Rounding"), "PriceList"));
            sql.append(" , ");
            sql.append(" PriceStd = ");
            sql.append(getRoundingSQL(rs.getString("Std_Rounding"), "PriceStd"));
            sql.append( " , " );
            sql.append(" PriceLimit = ");
            sql.append(getRoundingSQL(rs.getString("Limit_Rounding"), "PriceLimit"));

			sql.append(" WHERE " + getUserSQLCheck()
					+ " AND M_DiscountSchemaLine_ID="
					+ rs.getInt("M_DiscountSchemaLine_ID"));
			sql.append(" AND m_pricelist_version_id = ").append(getM_PriceList_Version_ID());
            setNo(DB.executeUpdate( sql.toString(), null));
            setMessage(getMessage() + ", @Updated@ = " + getNo());
        } catch( Exception e ) {
            setResultStr(getResultStr() + ":" + e.getMessage() + " " + getMessage());
            log.severe( getResultStr() );
        }
    }    // Rounding
    
    protected String getRoundingSQL(String roundingOption, String numericColumnName){
    	StringBuffer sql = new StringBuffer();
    	if( roundingOption.equals(MDiscountSchemaLine.LIST_ROUNDING_NoRounding) ) {
            sql.append( "?" );
        } else if( roundingOption.equals(MDiscountSchemaLine.LIST_ROUNDING_WholeNumber00) ) {
            sql.append( "ROUND(?, 0)" );
        } else if( roundingOption.equals(MDiscountSchemaLine.LIST_ROUNDING_Dime102030)) {
            sql.append( "ROUND(?, 1)" );
        } else if( roundingOption.equals(MDiscountSchemaLine.LIST_ROUNDING_Ten10002000) ) {
            sql.append( "ROUND(?, -1)" );
        } else if( roundingOption.equals(MDiscountSchemaLine.LIST_ROUNDING_Nickel051015) ) {
            sql.append( " (CASE WHEN trunc(?, 2) - trunc(?, 1) < 0.03 THEN trunc(?, 1) " +
            					"ELSE trunc(?, 1) + 0.05 END) " );
        } else if( roundingOption.equals(MDiscountSchemaLine.LIST_ROUNDING_Quarter255075) ) {
			sql.append(" (CASE WHEN trunc(?, 2) - trunc(?) BETWEEN 0.13 AND 0.37 THEN trunc(?) + 0.25 " +
							"WHEN trunc(?, 2) - trunc(?) BETWEEN 0.38 AND 0.62 THEN trunc(?) + 0.5 " +
							"WHEN trunc(?, 2) - trunc(?) BETWEEN 0.63 AND 0.87 THEN trunc(?) + 0.75	" +
							"WHEN trunc(?, 2) - trunc(?) >= 0.88 THEN trunc(?) + 1 " +
							"ELSE trunc(?) END) ");
        } else if( roundingOption.equals(MDiscountSchemaLine.LIST_ROUNDING_090RoundUp) ) {
        	sql.append(" CASE WHEN trunc(?, 2) - trunc(?) >= 0.90 THEN trunc(?) + 1 " +
        					"ELSE ? END ");
        } else if(roundingOption.equals(MDiscountSchemaLine.LIST_ROUNDING_Always090)){
        	sql.append( "ROUND(?, 0) + 0.90" );
        } else {
            sql.append( "ROUND(?," + m_StdPrecision + ")" );
        }
    	String sqlReal = sql.toString().replace("?", numericColumnName); 
    	return sqlReal;
    }

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
    
    @Override
    public Properties getCtx(){
    	if(localCtx != null){
    		return localCtx;
    	}
    	return super.getCtx();
    }
    
    protected String get_TrxName(){
    	if(localTrx != null){
    		return localTrx;
    	}
    	return super.get_TrxName();
    }

	protected int getM_PriceList_Version_Base_ID() {
		return m_PriceList_Version_Base_ID;
	}

	protected void setM_PriceList_Version_Base_ID(int m_PriceList_Version_Base_ID) {
		this.m_PriceList_Version_Base_ID = m_PriceList_Version_Base_ID;
	}

	protected int getM_PriceList_Version_ID() {
		return m_PriceList_Version_ID;
	}

	protected void setM_PriceList_Version_ID(int m_PriceList_Version_ID) {
		this.m_PriceList_Version_ID = m_PriceList_Version_ID;
	}

	protected int getM_DiscountSchema_ID() {
		return m_DiscountSchema_ID;
	}

	protected void setM_DiscountSchema_ID(int m_DiscountSchema_ID) {
		this.m_DiscountSchema_ID = m_DiscountSchema_ID;
	}

	protected int getM_Org_ID() {
		return m_Org_ID;
	}

	protected void setM_Org_ID(int m_Org_ID) {
		this.m_Org_ID = m_Org_ID;
	}

	protected int getM_Client_ID() {
		return m_Client_ID;
	}

	protected void setM_Client_ID(int m_Client_ID) {
		this.m_Client_ID = m_Client_ID;
	}

	protected int getNo() {
		return no;
	}

	protected void setNo(int no) {
		this.no = no;
	}

	protected String getResultStr() {
		return ResultStr;
	}

	protected void setResultStr(String resultStr) {
		ResultStr = resultStr;
	}

	protected String getMessage() {
		return Message;
	}

	protected void setMessage(String message) {
		Message = message;
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
