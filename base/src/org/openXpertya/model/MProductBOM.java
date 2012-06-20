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
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProductBOM extends X_M_Product_BOM {

    /**
     * Descripción de Método
     *
     *
     * @param product
     *
     * @return
     */

    public static MProductBOM[] getBOMLines( MProduct product ) {
        return getBOMLines( product.getCtx(),product.getM_Product_ID(),product.get_TrxName());
    }    // getBOMLines

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

    public static MProductBOM[] getBOMLines( Properties ctx,int M_Product_ID,String trxName ) {
        String sql = "SELECT * FROM M_Product_BOM WHERE M_Product_ID=? ORDER BY Line";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProductBOM( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getBOMLines",e );
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
        // s_log.fine("getBOMLines - #" + list.size() + " - M_Product_ID=" + M_Product_ID);

        MProductBOM[] retValue = new MProductBOM[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getBOMLines

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MProductBOM.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Product_BOM_ID
     * @param trxName
     */

    public MProductBOM( Properties ctx,int M_Product_BOM_ID,String trxName ) {
        super( ctx,M_Product_BOM_ID,trxName );

        if( M_Product_BOM_ID == 0 ) {

            // setM_Product_ID (0);    //      parent
            // setLine (0);    // @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_Product_BOM WHERE M_Product_ID=@M_Product_ID@
            // setM_ProductBOM_ID(0);

            setBOMQty( Env.ZERO );    // 1
        }
    }                                 // MProductBOM

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProductBOM( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProductBOM

    /** Descripción de Campos */

    private MProduct m_product = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProduct getProduct() {
        if( (m_product == null) && (getM_ProductBOM_ID() != 0) ) {
            m_product = MProduct.get( getCtx(),getM_ProductBOM_ID());
        }

        return m_product;
    }    // getProduct

    /**
     * Descripción de Método
     *
     *
     * @param M_ProductBOM_ID
     */

    public void setM_ProductBOM_ID( int M_ProductBOM_ID ) {
        super.setM_ProductBOM_ID( M_ProductBOM_ID );
        m_product = null;
    }    // setM_ProductBOM_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MProductBOM[" );

        sb.append( getID()).append( ",Line=" ).append( getLine()).append( ",Type=" ).append( getBOMType()).append( ",Qty=" ).append( getBOMQty());

        if( m_product == null ) {
            sb.append( ",M_Product_ID=" ).append( getM_ProductBOM_ID());
        } else {
            sb.append( "," ).append( m_product );
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString

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
        if( newRecord || is_ValueChanged( "M_ProductBOM_ID" )) {
            MProduct product = new MProduct( getCtx(),getM_Product_ID(),get_TrxName());

            if( get_TrxName() != null ) {
                product.load( get_TrxName());
            }

            if( product.isVerified()) {
                product.setIsVerified( false );
                product.save( get_TrxName());
            }
        }

        return success;
    }    // afterSave
}    // MProductBOM



/*
 *  @(#)MProductBOM.java   02.07.07
 * 
 *  Fin del fichero MProductBOM.java
 *  
 *  Versión 2.2
 *
 */
