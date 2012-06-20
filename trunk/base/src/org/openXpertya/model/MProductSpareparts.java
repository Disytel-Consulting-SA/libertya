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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProductSpareparts extends X_M_Product_Spareparts {

    /**
     * Descripción de Método
     *
     *
     * @param product
     *
     * @return
     */

    public static MProductSpareparts[] getSparepartsLines( MProduct product ) {
        return getSparepartsLines( product.getCtx(),product.getM_Product_ID());
    }    // getSparepartsLines

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     *
     * @return
     */

    public static MProductSpareparts[] getSparepartsLines( Properties ctx,int M_Product_ID ) {
        String sql = "SELECT * FROM M_Product_Spareparts WHERE M_Product_ID=? ORDER BY M_Productspareparts_ID";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProductSpareparts( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getSparepartsLines",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //
        // s_log.debug("getSparepartsLines - #" + list.size() + " - M_Product_ID=" + M_Product_ID);

        MProductSpareparts[] retValue = new MProductSpareparts[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getSparepartsLines

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MProductSpareparts.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Product_Spareparts_ID
     * @param trxName
     */

    public MProductSpareparts( Properties ctx,int M_Product_Spareparts_ID,String trxName ) {
        super( ctx,M_Product_Spareparts_ID,trxName );

        if( M_Product_Spareparts_ID == 0 ) {

            // setM_Product_ID (0);    //      parent
            // setLine (0);    // @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_Product_Spareparts WHERE M_Product_ID=@M_Product_ID@
            // setM_ProductSpareparts_ID(0);

            setSparepartsqty( 0 );    // 1
        }
    }                                 // MProductSpareparts

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProductSpareparts( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProductSpareparts

    /** Descripción de Campos */

    private MProduct m_product = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProduct getProduct() {
        if( (m_product == null) && (getM_Productspareparts_ID() != 0) ) {
            m_product = MProduct.get( getCtx(),getM_Productspareparts_ID());
        }

        return m_product;
    }    // getProduct

    /**
     * Descripción de Método
     *
     *
     * @param M_ProductSpareparts_ID
     */

    public void setM_ProductSpareparts_ID( int M_ProductSpareparts_ID ) {
        super.setM_Productspareparts_ID( M_ProductSpareparts_ID );
        m_product = null;
    }    // setM_ProductSpareparts_ID
}



/*
 *  @(#)MProductSpareparts.java   02.07.07
 * 
 *  Fin del fichero MProductSpareparts.java
 *  
 *  Versión 2.2
 *
 */
