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
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
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

public class MProductCategory extends X_M_Product_Category {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_Category_ID
     *
     * @return
     */

    public static MProductCategory get( Properties ctx,int M_Product_Category_ID, String trxName ) {
        Integer          ii = new Integer( M_Product_Category_ID );
        MProductCategory pc = ( MProductCategory )s_cache.get( ii );

        if( pc == null ) {
            pc = new MProductCategory( ctx,M_Product_Category_ID,trxName );
            s_cache.put(ii, pc);
        }

        return pc;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_Category_ID
     * @param M_Product_ID
     *
     * @return
     */

    public static boolean isCategory( int M_Product_Category_ID,int M_Product_ID ) {
        if( (M_Product_ID == 0) || (M_Product_Category_ID == 0) ) {
            return false;
        }

        // Look up

        Integer product  = new Integer( M_Product_ID );
        Integer category = ( Integer )s_products.get( product );

        if( category != null ) {
            return category.intValue() == M_Product_Category_ID;
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( "SELECT M_Product_Category_ID FROM M_Product WHERE M_Product_ID=?",null );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                category = new Integer( rs.getInt( 1 ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"isCategory",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( category != null ) {

            // TODO: LRU logic

            s_products.put( product,category );

            //

            s_log.fine( "M_Product_ID=" + M_Product_ID + "(" + category + ") in M_Product_Category_ID=" + M_Product_Category_ID + " - " + ( category.intValue() == M_Product_Category_ID ));

            return category.intValue() == M_Product_Category_ID;
        }

        s_log.log( Level.SEVERE,"Not found M_Product_ID=" + M_Product_ID );

        return false;
    }    // isCategory

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "M_Product_Category",20 );

    /** Descripción de Campos */

    private static CCache s_products = new CCache( "M_Product",100 );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MProductCategory.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Product_Category_ID
     * @param trxName
     */

    public MProductCategory( Properties ctx,int M_Product_Category_ID,String trxName ) {
        super( ctx,M_Product_Category_ID,trxName );

        if( M_Product_Category_ID == 0 ) {

            // setName (null);
            // setValue (null);

            setMMPolicy( MMPOLICY_FiFo );    // F
            setPlannedMargin( Env.ZERO );
            setIsDefault( false );
            setIsSelfService( true );        // Y
        }
    }                                        // MProductCategory

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProductCategory( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProductCategory

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
        if( newRecord ) {
            insert_Accounting( "M_Product_Category_Acct","C_AcctSchema_Default",null );
        }

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        return delete_Accounting( "M_Product_Category_Acct" );
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFiFo() {
        return MMPOLICY_FiFo.equals( getMMPolicy());
    }    // isFiFo
}    // MProductCategory



/*
 *  @(#)MProductCategory.java   02.07.07
 * 
 *  Fin del fichero MProductCategory.java
 *  
 *  Versión 2.2
 *
 */
