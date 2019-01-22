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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProductGamas extends X_M_Product_Gamas {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_Gamas_ID
     * @param trxName
     *
     * @return
     */

    public static MProductGamas get( Properties ctx,int M_Product_Gamas_ID,String trxName ) {
        Integer       ii = new Integer( M_Product_Gamas_ID );
        MProductGamas pc = ( MProductGamas )s_cache.get( ii );

        if( pc == null ) {
            pc = new MProductGamas( ctx,M_Product_Gamas_ID,trxName );
        }

        return pc;
    }    // get
    

    public static Integer getGamaIDFromCategory(int productCategoryID, String trxName){
        Integer gamaID = ( Integer )s_categoriesGamas.get( productCategoryID );
        
        // Si no esta cacheado lo busco
        if(gamaID == null){
			gamaID = DB.getSQLValue(trxName,
					"SELECT M_Product_Gamas_ID FROM M_Product_Category WHERE M_Product_Category_ID=?",
					productCategoryID);
			if(gamaID > 0){
				s_categoriesGamas.put(productCategoryID, gamaID);
			}
        }
        
        return gamaID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_Gamas_ID
     * @param M_Product_Category_ID
     *
     * @return
     */

    public static boolean isGama( int M_Product_Gamas_ID,int M_Product_Category_ID ) {
        if( (M_Product_Category_ID == 0) || (M_Product_Gamas_ID == 0) ) {
            return false;
        }
        Integer gamaID = getGamaIDFromCategory(M_Product_Category_ID, null);
        return gamaID != null && gamaID.intValue() == M_Product_Gamas_ID;
    }    // isCategory

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "M_Product_Gamas",20 );

    /** Descripción de Campos */

    private static CCache s_categoriesGamas = new CCache( "M_Product_Category",100 );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MProductGamas.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Product_Gamas_ID
     * @param trxName
     */

    public MProductGamas( Properties ctx,int M_Product_Gamas_ID,String trxName ) {
        super( ctx,M_Product_Gamas_ID,trxName );

        if( M_Product_Gamas_ID == 0 ) {

            // setName (null);
            // setValue (null);

            setIsDefault( false );
            setIsSelfService( true );    // Y
        }
    }                                    // MProductCategory

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProductGamas( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProductGamas
    
    /**
     * Devuelve la Familia a la que pertenece un artículo
     * @param ctx Contexto de la aplicación
     * @param productID ID del artículo
     * @param trxName Transacción para instanciación de objetos
     * @return la {@link MProductGamas} o <code>null</code> si el artículo
     * no pertence a ninguna familia. 
     */
    public static MProductGamas getOfProduct(Properties ctx, int productID, String trxName) {
    	MProductGamas gamas = null;
    	
    	String sql = 
    		"SELECT g.* " +
    		"FROM M_Product p " +
    		"INNER JOIN M_Product_Category c ON (p.M_Product_Category_ID = c.M_Product_Category_ID) " +
    		"INNER JOIN M_Product_Gamas g ON (c.M_Product_Gamas_ID = g.M_Product_Gamas_ID) " +
    		"WHERE p.M_Product_ID = ?";
    	
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	
    	
    	try {
    		pstmt = DB.prepareStatement(sql, trxName);
    		pstmt.setInt(1, productID);
    		rs = pstmt.executeQuery();
    		if (rs.next()) {
    			gamas = new MProductGamas(ctx, rs, trxName);
    		}
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "Error getting product gamas. M_Product_ID = " + productID, e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
    	
		return gamas;
    }
}    // MProductGamas



/*
 *  @(#)MProductGamas.java   02.07.07
 * 
 *  Fin del fichero MProductGamas.java
 *  
 *  Versión 2.2
 *
 */
