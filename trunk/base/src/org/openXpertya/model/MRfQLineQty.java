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
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRfQLineQty extends X_C_RfQLineQty {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_RfQLineQty_ID
     * @param trxName
     *
     * @return
     */

    public static MRfQLineQty get( Properties ctx,int C_RfQLineQty_ID,String trxName ) {
        Integer     key      = new Integer( C_RfQLineQty_ID );
        MRfQLineQty retValue = ( MRfQLineQty )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MRfQLineQty( ctx,C_RfQLineQty_ID,trxName );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_RfQLineQty",20 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_RfQLineQty_ID
     * @param trxName
     */

    public MRfQLineQty( Properties ctx,int C_RfQLineQty_ID,String trxName ) {
        super( ctx,C_RfQLineQty_ID,trxName );

        if( C_RfQLineQty_ID == 0 ) {

            // setC_RfQLine_ID (0);
            // setC_UOM_ID (0);

            setIsOfferQty( false );
            setIsPurchaseQty( false );
            setQty( Env.ONE );    // 1
        }
    }                             // MRfQLineQty

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRfQLineQty( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );

        if( getID() > 0 ) {
            s_cache.put( new Integer( getID()),this );
        }
    }    // MRfQLineQty

    /**
     * Constructor de la clase ...
     *
     *
     * @param line
     */

    public MRfQLineQty( MRfQLine line ) {
        this( line.getCtx(),0,line.get_TrxName());
        setClientOrg( line );
        setC_RfQLine_ID( line.getC_RfQLine_ID());
    }    // MRfQLineQty

    /** Descripción de Campos */

    private MUOM m_uom = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getUomName() {
        if( m_uom == null ) {
            m_uom = MUOM.get( getCtx(),getC_UOM_ID());
        }

        return m_uom.getName();
    }    // getUomText

    /**
     * Descripción de Método
     *
     *
     * @param onlyValidAmounts
     *
     * @return
     */

    public MRfQResponseLineQty[] getResponseQtys( boolean onlyValidAmounts ) {
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;
        String            sql   = "SELECT * FROM C_RfQResponseLineQty WHERE C_RfQLineQty_ID=? AND IsActive='Y'";

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_RfQLineQty_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRfQResponseLineQty qty = new MRfQResponseLineQty( getCtx(),rs,get_TrxName());

                if( onlyValidAmounts &&!qty.isValidAmt()) {
                    ;
                } else {
                    list.add( qty );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getResponseQtys",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MRfQResponseLineQty[] retValue = new MRfQResponseLineQty[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getResponseQtys

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRfQLineQty[" );

        sb.append( getID()).append( ",Qty=" ).append( getQty()).append( ",Offer=" ).append( isOfferQty()).append( ",Purchase=" ).append( isPurchaseQty()).append( "]" );

        return sb.toString();
    }    // toString
}    // MRfQLineQty



/*
 *  @(#)MRfQLineQty.java   02.07.07
 * 
 *  Fin del fichero MRfQLineQty.java
 *  
 *  Versión 2.2
 *
 */
