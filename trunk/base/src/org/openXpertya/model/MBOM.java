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

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBOM extends X_M_BOM {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_BOM_ID
     *
     * @return
     */

    public static MBOM get( Properties ctx,int M_BOM_ID ) {
        Integer key      = new Integer( M_BOM_ID );
        MBOM    retValue = ( MBOM )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MBOM( ctx,M_BOM_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param trxName
     * @param whereClause
     *
     * @return
     */

    public static MBOM[] getOfProduct( Properties ctx,int M_Product_ID,String trxName,String whereClause ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_BOM WHERE M_Product_ID=?";

        if( (whereClause != null) && (whereClause.length() > 0) ) {
            sql += " AND " + whereClause;
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MBOM( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MBOM[] retValue = new MBOM[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getOfProduct

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "M_BOM",20 );

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MBOM.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_BOM_ID
     * @param trxName
     */

    public MBOM( Properties ctx,int M_BOM_ID,String trxName ) {
        super( ctx,M_BOM_ID,trxName );

        if( M_BOM_ID == 0 ) {

            // setM_Product_ID (0);
            // setName (null);

            setBOMType( BOMTYPE_CurrentActive );    // A
            setBOMUse( BOMUSE_Master );             // A
        }
    }                                               // MBOM

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBOM( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBOM

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // BOM Type

        if( newRecord || is_ValueChanged( "BOMType" )) {

            // Only one Current Active

            if( getBOMType().equals( BOMTYPE_CurrentActive )) {
                MBOM[] boms = getOfProduct( getCtx(),getM_Product_ID(),get_TrxName(),"BOMType='A' AND BOMUse='" + getBOMUse() + "' AND IsActive='Y'" );

                if( (boms.length == 0    // only one = this
                        ) || ( (boms.length == 1) && (boms[ 0 ].getM_BOM_ID() == getM_BOM_ID()))) {
                    ;
                } else {
                    log.saveError( "Error",Msg.parseTranslation( getCtx(),"Can only have one Current Active BOM for Product BOM Use (" + getBOMType() + ")" ));

                    return false;
                }
            }

            // Only one MTO

            else if( getBOMType().equals( BOMTYPE_Make_To_Order )) {
                MBOM[] boms = getOfProduct( getCtx(),getM_Product_ID(),get_TrxName(),"IsActive='Y'" );

                if( (boms.length == 0    // only one = this
                        ) || ( (boms.length == 1) && (boms[ 0 ].getM_BOM_ID() == getM_BOM_ID()))) {
                    ;
                } else {
                    log.saveError( "Error",Msg.parseTranslation( getCtx(),"Can only have single Make-to-Order BOM for Product" ));

                    return false;
                }
            }
        }    // BOM Type

        return true;
    }    // beforeSave
}    // MBOM



/*
 *  @(#)MBOM.java   02.07.07
 * 
 *  Fin del fichero MBOM.java
 *  
 *  Versión 2.2
 *
 */
