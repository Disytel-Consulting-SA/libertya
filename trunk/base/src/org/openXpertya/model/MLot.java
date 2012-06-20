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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.KeyNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MLot extends X_M_Lot {

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MLot.class );

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

    public static MLot[] getProductLots( Properties ctx,int M_Product_ID,String trxName ) {
        String            sql   = "SELECT * FROM M_Lot WHERE M_Product_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MLot( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,"(MLot)",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MLot[] retValue = new MLot[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getProductLots

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     * @param trxName
     *
     * @return
     */

    public static KeyNamePair[] getProductLots( int M_Product_ID,String trxName ) {
        String            sql   = "SELECT M_Lot_ID, Name FROM M_Lot WHERE M_Product_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 )));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,"getProductLots (KeyNamePair)",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        KeyNamePair[] retValue = new KeyNamePair[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getProductLots

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Lot_ID
     * @param trxName
     */

    public MLot( Properties ctx,int M_Lot_ID,String trxName ) {
        super( ctx,M_Lot_ID,trxName );
    }    // MLot

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MLot( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MLot

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctl
     * @param M_Product_ID
     * @param Name
     */

    public MLot( MLotCtl ctl,int M_Product_ID,String Name ) {
        this( ctl.getCtx(),0,ctl.get_TrxName());
        setClientOrg( ctl );
        setM_LotCtl_ID( ctl.getM_LotCtl_ID());
        setM_Product_ID( M_Product_ID );
        setName( Name );
    }    // MLot

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return getName();
    }    // toString
}    // MLot



/*
 *  @(#)MLot.java   02.07.07
 * 
 *  Fin del fichero MLot.java
 *  
 *  Versión 2.2
 *
 */
