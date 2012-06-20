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

public class MContactInterest extends X_R_ContactInterest {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param R_InterestArea_ID
     * @param AD_User_ID
     *
     * @return
     */

    public static MContactInterest get( Properties ctx,int R_InterestArea_ID,int AD_User_ID ) {
        MContactInterest retValue = null;
        String           sql      = "SELECT * FROM R_ContactInterest " + "WHERE R_InterestArea_ID=? AND AD_User_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,null );
            pstmt.setInt( 1,R_InterestArea_ID );
            pstmt.setInt( 2,AD_User_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MContactInterest( ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"get",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( retValue == null ) {
            retValue = new MContactInterest( ctx,R_InterestArea_ID,AD_User_ID,null );
            s_log.fine( "get - NOT found - " + retValue );
        } else {
            s_log.fine( "get - found - " + retValue );
        }

        return retValue;
    }    // get

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MContactInterest( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }
    }    // MContactInterest

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param R_InterestArea_ID
     * @param AD_User_ID
     * @param trxName
     */

    public MContactInterest( Properties ctx,int R_InterestArea_ID,int AD_User_ID,String trxName ) {
        super( ctx,0,trxName );
        setR_InterestArea_ID( R_InterestArea_ID );
        setAD_User_ID( AD_User_ID );
        setIsActive( false );
    }    // MContactInterest

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MContactInterest( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MContactInterest

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MContactInterest.class );

    /**
     * Descripción de Método
     *
     *
     * @param OptOutDate
     */

    public void setOptOutDate( Timestamp OptOutDate ) {
        if( OptOutDate == null ) {
            OptOutDate = new Timestamp( System.currentTimeMillis());
        }

        log.fine( "setOptOutDate - " + OptOutDate );
        super.setOptOutDate( OptOutDate );
        setIsActive( false );
    }    // setOptOutDate

    /**
     * Descripción de Método
     *
     */

    public void unsubscribe() {
        setOptOutDate( null );
    }    // unsubscribe

    /**
     * Descripción de Método
     *
     *
     * @param SubscribeDate
     */

    public void setSubscribeDate( Timestamp SubscribeDate ) {
        if( SubscribeDate == null ) {
            SubscribeDate = new Timestamp( System.currentTimeMillis());
        }

        log.fine( "setSubscribeDate - " + SubscribeDate );
        super.setSubscribeDate( SubscribeDate );
        super.setOptOutDate( null );
        setIsActive( true );
    }    // setSubscribeDate

    /**
     * Descripción de Método
     *
     */

    public void subscribe() {
        setSubscribeDate( null );
    }    // subscribe

    /**
     * Descripción de Método
     *
     *
     * @param subscribe
     */

    public void subscribe( boolean subscribe ) {
        if( subscribe ) {
            setSubscribeDate( null );
        } else {
            setOptOutDate( null );
        }
    }    // subscribe

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSubscribed() {
        if( !isActive() || (getOptOutDate() != null) ) {
            return false;
        }

        return true;
    }    // isSubscribed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MContactInterest[" ).append( "R_InterestArea_ID=" ).append( getR_InterestArea_ID()).append( ",AD_User_ID=" ).append( getAD_User_ID()).append( ",Subscribed=" ).append( isSubscribed()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        org.openXpertya.OpenXpertya.startup( true );

        int              R_InterestArea_ID = 1000002;
        int              AD_User_ID        = 1000002;
        MContactInterest ci                = MContactInterest.get( Env.getCtx(),R_InterestArea_ID,AD_User_ID );

        ci.subscribe();
        ci.save();

        //

        ci = MContactInterest.get( Env.getCtx(),R_InterestArea_ID,AD_User_ID );
    }    // main
}    // MContactInterest



/*
 *  @(#)MContactInterest.java   02.07.07
 * 
 *  Fin del fichero MContactInterest.java
 *  
 *  Versión 2.2
 *
 */
