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
import java.sql.Timestamp;
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

public class MRfQ extends X_C_RfQ {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_RfQ_ID
     * @param trxName
     *
     * @return
     */

    public static MRfQ get( Properties ctx,int C_RfQ_ID,String trxName ) {
        Integer key      = new Integer( C_RfQ_ID );
        MRfQ    retValue = ( MRfQ )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MRfQ( ctx,C_RfQ_ID,trxName );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_RfQ",10 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_RfQ_ID
     * @param trxName
     */

    public MRfQ( Properties ctx,int C_RfQ_ID,String trxName ) {
        super( ctx,C_RfQ_ID,trxName );

        if( C_RfQ_ID == 0 ) {

            // setC_RfQ_Topic_ID (0);
            // setName (null);
            // setC_Currency_ID (0);   // @$C_Currency_ID @
            // setSalesRep_ID (0);
            //

            setDateResponse( new Timestamp( System.currentTimeMillis()));
            setDateWorkStart( new Timestamp( System.currentTimeMillis()));
            setIsInvitedVendorsOnly( false );
            setQuoteType( QUOTETYPE_QuoteSelectedLines );
            setIsQuoteAllQty( false );
            setIsQuoteTotalAmt( false );
            setIsRfQResponseAccepted( true );
            setIsSelfService( true );
            setProcessed( false );
        }
    }    // MRfQ

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRfQ( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRfQ

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRfQLine[] getLines() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_RfQLine " + "WHERE C_RfQ_ID=? AND IsActive='Y' " + "ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_RfQ_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRfQLine( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MRfQLine[] retValue = new MRfQLine[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param activeOnly
     * @param completedOnly
     *
     * @return
     */

    public MRfQResponse[] getResponses( boolean activeOnly,boolean completedOnly ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_RfQResponse " + "WHERE C_RfQ_ID=?";

        if( activeOnly ) {
            sql += " AND IsActive='Y'";
        }

        if( completedOnly ) {
            sql += " AND IsComplete='Y'";
        }

        sql += " ORDER BY Price";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_RfQ_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRfQResponse( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MRfQResponse[] retValue = new MRfQResponse[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getResponses

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRfQ[" );

        sb.append( getID()).append( ",Name=" ).append( getName()).append( ",QuoteType=" ).append( getQuoteType()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isQuoteTotalAmtOnly() {
        return QUOTETYPE_QuoteTotalOnly.equals( getQuoteType());
    }    // isQuoteTotalAmtOnly

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isQuoteSelectedLines() {
        return QUOTETYPE_QuoteSelectedLines.equals( getQuoteType());
    }    // isQuoteSelectedLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isQuoteAllLines() {
        return QUOTETYPE_QuoteAllLines.equals( getQuoteType());
    }    // isQuoteAllLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String checkQuoteTotalAmtOnly() {
        if( !isQuoteTotalAmtOnly()) {
            return null;
        }

        // Need to check Line Qty

        MRfQLine[] lines = getLines();

        for( int i = 0;i < lines.length;i++ ) {
            MRfQLine      line = lines[ i ];
            MRfQLineQty[] qtys = line.getQtys();

            if( qtys.length > 1 ) {
                log.warning( "isQuoteTotalAmtOnlyValid - #" + qtys.length + " - " + line );

                String msg = "@Line@ " + line.getLine() + ": #@C_RfQLineQty@=" + qtys.length + " - @IsQuoteTotalAmt@";

                return msg;
            }
        }

        return null;
    }    // checkQuoteTotalAmtOnly

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
}    // MRfQ



/*
 *  @(#)MRfQ.java   02.07.07
 * 
 *  Fin del fichero MRfQ.java
 *  
 *  Versión 2.2
 *
 */
