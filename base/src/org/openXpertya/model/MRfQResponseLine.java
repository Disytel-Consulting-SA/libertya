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

import org.openXpertya.util.DB;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRfQResponseLine extends X_C_RfQResponseLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MRfQResponseLine( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }
    }    // MRfQResponseLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRfQResponseLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRfQResponseLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param response
     * @param line
     */

    public MRfQResponseLine( MRfQResponse response,MRfQLine line ) {
        super( response.getCtx(),0,response.get_TrxName());
        setClientOrg( response );
        setC_RfQResponse_ID( response.getC_RfQResponse_ID());

        //

        setC_RfQLine_ID( line.getC_RfQLine_ID());

        //

        setIsSelectedWinner( false );
        setIsSelfService( false );

        //

        MRfQLineQty[] qtys = line.getQtys();

        for( int i = 0;i < qtys.length;i++ ) {
            if( qtys[ i ].isActive() && qtys[ i ].isRfQQty()) {
                if( getID() == 0 ) {    // save this line
                    save();
                }

                MRfQResponseLineQty qty = new MRfQResponseLineQty( this,qtys[ i ] );

                qty.save();
            }
        }
    }    // MRfQResponseLine

    /** Descripción de Campos */

    private MRfQLine m_rfqLine = null;

    /** Descripción de Campos */

    private MRfQResponseLineQty[] m_qtys = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRfQResponseLineQty[] getQtys() {
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

    public MRfQResponseLineQty[] getQtys( boolean requery ) {
        if( (m_qtys != null) &&!requery ) {
            return m_qtys;
        }

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_RfQResponseLineQty " + "WHERE C_RfQResponseLine_ID=? AND IsActive='Y'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_RfQResponseLine_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRfQResponseLineQty( getCtx(),rs,get_TrxName()));
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

        m_qtys = new MRfQResponseLineQty[ list.size()];
        list.toArray( m_qtys );

        return m_qtys;
    }    // getQtys

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRfQLine getRfQLine() {
        if( m_rfqLine == null ) {
            m_rfqLine = MRfQLine.get( getCtx(),getC_RfQLine_ID(),get_TrxName());
        }

        return m_rfqLine;
    }    // getRfQLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRfQResponseLine[" );

        sb.append( getID()).append( ",Winner=" ).append( isSelectedWinner()).append( "]" );

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

        if( !isActive()) {
            setIsSelectedWinner( false );
        }

        return true;
    }    // beforeSave

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
        if( !isActive()) {
            getQtys( false );

            for( int i = 0;i < m_qtys.length;i++ ) {
                MRfQResponseLineQty qty = m_qtys[ i ];

                if( qty.isActive()) {
                    qty.setIsActive( false );
                    qty.save();
                }
            }
        }

        return success;
    }    // success
}    // MRfQResponseLine



/*
 *  @(#)MRfQResponseLine.java   02.07.07
 * 
 *  Fin del fichero MRfQResponseLine.java
 *  
 *  Versión 2.2
 *
 */
