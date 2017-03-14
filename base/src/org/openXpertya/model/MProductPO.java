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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProductPO extends X_M_Product_PO {

    /** Descripción de Campos */

    private final static String sqlGetProductPO = "SELECT * FROM M_Product_PO " + "WHERE M_Product_ID=? AND IsActive='Y' " + "ORDER BY IsCurrentVendor DESC, Updated DESC";

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param trxName
     *
     * @return
     */

    public static MProductPO[] getOfProduct( Properties ctx,int M_Product_ID,String trxName ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_Product_PO " + "WHERE M_Product_ID=? AND IsActive='Y' " + "ORDER BY IsCurrentVendor DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                list.add( new MProductPO( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,"getOfProduct",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MProductPO[] retValue = new MProductPO[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getOfProduct

    
    public static List<MProductPO> getOfBPartner(Properties ctx, Integer bPartnerID, String trxName) throws Exception{
    	List<MProductPO> pos = new ArrayList<MProductPO>();
    	String sql = "SELECT * FROM M_Product_PO " + "WHERE C_BPartner_ID=? AND IsActive='Y' ";
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, bPartnerID);
			rs = ps.executeQuery();
			while (rs.next()) {
				pos.add(new MProductPO(ctx, rs, trxName));
			}
		} catch (Exception e) {
			throw e;
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
    	return pos;
    }
    
    public static List<Integer> getProductIDsOfBPartner(Properties ctx, Integer bPartnerID, String trxName) {
    	List<Integer> pos = new ArrayList<Integer>();
    	String sql = "SELECT m_product_id FROM M_Product_PO " + "WHERE C_BPartner_ID=? AND IsActive='Y' ";
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, bPartnerID);
			rs = ps.executeQuery();
			while (rs.next()) {
				pos.add(rs.getInt("m_product_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
    	return pos;
    }
    
    /**
	 * @param productID
	 *            id del artículo
	 * @param trxName
	 *            nombre de transacción
	 * @return id del primer proveedor en órden de actual proveedor y última vez
	 *         actualizado descendientemente
	 */
    public static Integer getFirstVendorID(Integer productID, String trxName){
		return DB
				.getSQLValue(
						trxName,
						"SELECT c_bpartner_id FROM "
								+ Table_Name
								+ " WHERE m_product_id = ? AND IsActive='Y' ORDER BY IsCurrentVendor DESC, updated desc",
						productID);
    }
    
    
    public static BigDecimal dameOrderMin(Properties ctx, Integer M_Product_ID, Integer C_BPartner_ID, Integer C_UOM_ID, BigDecimal QtyEntered, String trxName) {    	
    	// Convertir a la unidad del ARTICULO
		if (Util.isEmpty(C_UOM_ID, true) || Util.isEmpty(C_BPartner_ID, true))
    		return null;
    	
		QtyEntered = MUOMConversion.convertProductFrom(ctx, M_Product_ID,
				C_UOM_ID, QtyEntered);
    	
    	if (QtyEntered == null)
    		return null;
    	
    	BigDecimal orderMin = null;
    	
        MProductPO po = MProductPO.get(ctx, M_Product_ID, C_BPartner_ID, trxName);
        if (po != null) {
        	orderMin = po.getOrder_Min();
	        if (orderMin != null) {
				orderMin = MUOMConversion.convertProductFrom(ctx, M_Product_ID,
						po.getC_UOM_ID(), orderMin);
	        }
        }
        
        return orderMin;    	
    }
    
    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MProductPO.class );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param trxName
     *
     * @return
     */

    public static MProductPO getOfOneProduct( Properties ctx,int M_Product_ID,String trxName ) {
        MProductPO        mppo  = null;
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sqlGetProductPO );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                mppo = new MProductPO( ctx,rs,trxName );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,"getOfOneProduct",ex );
        }

        pstmt = null;

        return mppo;
    }    // getOfOneProduct

	/**
	 * Obtener el registro de artículo de proveedor del artículo y proveedor
	 * parámetro.
	 * 
	 * @param ctx
	 *            contexto
	 * @param productID
	 *            id de producto
	 * @param bpartnerID
	 *            id de entidad comercial
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return registro de artículo de proveedor, null si no existe relación
	 *         entre ese artículo y proveedor
	 */
    public static MProductPO get(Properties ctx, int productID, int bpartnerID, String trxName){
    	String sql = "SELECT * FROM " + Table_Name
		+ " WHERE c_bpartner_id = ? and m_product_id = ?";
		MProductPO po = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, bpartnerID);
			ps.setInt(2, productID);
			rs = ps.executeQuery();
			if(rs.next()){
				po = new MProductPO(ctx, rs, trxName);
			}
		} catch (Exception e) {
			s_log.severe("Error finding product po, method get. "+e.getMessage());
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				s_log.severe("Error finding product po, method get. "+e2.getMessage());
			}
		}
		return po;
    }
    
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MProductPO( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        } else {

            // setM_Product_ID (0);    // @M_Product_ID@
            // setC_BPartner_ID (0);   // 0
            // setVendorProductNo (null);      // @Value@

            setIsCurrentVendor( true );    // Y
        }
    }                                      // MProduct_PO

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProductPO( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProductPO
    
    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Se valida que no se pueda crear mas de un registro en la tabla M_Product_PO con el mismo C_BPartner_ID para el mismo Artículo
        if(is_ValueChanged( "C_BPartner_ID" )){
        	int count = Integer.parseInt(DB.getSQLObject(get_TrxName(), "SELECT COUNT(*) FROM M_Product_PO po WHERE po.C_BPartner_ID = ? AND po.M_Product_ID = ?", new Object[]{getC_BPartner_ID(),getM_Product_ID()}).toString());
        	if (count > 0){
        		log.saveError("ExistRecordWithBP", "");
    			return false;
        	}
    	}
  
        return true;
    }    // beforeSave
    
}    // MProductPO



/*
 *  @(#)MProductPO.java   02.07.07
 * 
 *  Fin del fichero MProductPO.java
 *  
 *  Versión 2.2
 *
 */
