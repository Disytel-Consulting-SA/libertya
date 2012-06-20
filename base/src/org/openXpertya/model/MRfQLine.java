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
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRfQLine extends X_C_RfQLine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_RfQLine_ID
     * @param trxName
     *
     * @return
     */

    public static MRfQLine get( Properties ctx,int C_RfQLine_ID,String trxName ) {
        Integer  key      = new Integer( C_RfQLine_ID );
        MRfQLine retValue = ( MRfQLine )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MRfQLine( ctx,C_RfQLine_ID,trxName );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_RfQLine",20 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_RfQLine_ID
     * @param trxName
     */

    public MRfQLine( Properties ctx,int C_RfQLine_ID,String trxName ) {
        super( ctx,C_RfQLine_ID,trxName );

        if( C_RfQLine_ID == 0 ) {
            setLine( 0 );
        }
    }    // MRfQLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRfQLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );

        if( getID() > 0 ) {
            s_cache.put( new Integer( getID()),this );
        }
    }    // MRfQLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param rfq
     */

    public MRfQLine( MRfQ rfq ) {
        this( rfq.getCtx(),0,rfq.get_TrxName());
        setClientOrg( rfq );
        setC_RfQ_ID( rfq.getC_RfQ_ID());
    }    // MRfQLine

    /** Descripción de Campos */

    private MRfQLineQty[] m_qtys = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRfQLineQty[] getQtys() {
        return getQtys( false );
    }    // getQtys

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MRfQLineQty[] getQtys( boolean requery ) {
        if( (m_qtys != null) &&!requery ) {
            return m_qtys;
        }

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_RfQLineQty " + "WHERE C_RfQLine_ID=? AND IsActive='Y' " + "ORDER BY Qty";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_RfQLine_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRfQLineQty( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getQtys",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Create Default (1)

        if( list.size() == 0 ) {
            MRfQLineQty qty = new MRfQLineQty( this );

            qty.save();
            list.add( qty );
        }

        m_qtys = new MRfQLineQty[ list.size()];
        list.toArray( m_qtys );

        return m_qtys;
    }    // getQtys

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getProductDetailHTML() {
        if( getM_Product_ID() == 0 ) {
            return "";
        }

        StringBuffer sb      = new StringBuffer();
        MProduct     product = MProduct.get( getCtx(),getM_Product_ID());

        sb.append( product.getName());

        if( (product.getDescription() != null) && (product.getDescription().length() > 0) ) {
            sb.append( "<br><i>" ).append( product.getDescription()).append( "</i>" );
        }

        return sb.toString();
    }    // getProductDetails

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRfQLine[" );

        sb.append( getID()).append( "," ).append( getLine()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Calculate Complete Date (also used to verify)

        if( (getDateWorkStart() != null) && (getDeliveryDays() != 0) ) {
            setDateWorkComplete( TimeUtil.addDays( getDateWorkStart(),getDeliveryDays()));

            // Calculate Delivery Days

        } else if( (getDateWorkStart() != null) && (getDeliveryDays() == 0) && (getDateWorkComplete() != null) ) {
            setDeliveryDays( TimeUtil.getDaysBetween( getDateWorkStart(),getDateWorkComplete()));

            // Calculate Start Date

        } else if( (getDateWorkStart() == null) && (getDeliveryDays() != 0) && (getDateWorkComplete() != null) ) {
            setDateWorkStart( TimeUtil.addDays( getDateWorkComplete(),getDeliveryDays() * -1 ));
        }

        return true;
    }    // beforeSave
}    // MRfQLine



/*
 *  @(#)MRfQLine.java   02.07.07
 * 
 *  Fin del fichero MRfQLine.java
 *  
 *  Versión 2.2
 *
 */
