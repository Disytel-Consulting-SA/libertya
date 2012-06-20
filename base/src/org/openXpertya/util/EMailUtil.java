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



package org.openXpertya.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MClient;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class EMailUtil {

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( EMailUtil.class );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param strict
     *
     * @return
     */

    public static String getEMail( Properties ctx,boolean strict ) {
        String from = Env.getContext( ctx,EMail.CTX_EMAIL );

        if( from.length() != 0 ) {
            return from;
        }

        // Current User

        int AD_User_ID = Env.getContextAsInt( ctx,"#AD_User_ID" );

        if( AD_User_ID != 0 ) {
            from = getEMail_User( AD_User_ID,strict,ctx );
        }

        // Request

        if( (from == null) || (from.length() == 0) ) {
            from = getEMail_Request( ctx );
        }

        // Bogus

        if( (from == null) || (from.length() == 0) ) {
            if( strict ) {
                return null;
            }

            from = getEMail_Bogus( ctx );
        }

        return from;
    }    // getEMail

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     * @param AD_User_ID
     *
     * @return
     */

    public static String getEMail_BPartner( int C_BPartner_ID,int AD_User_ID ) {
        String email = null;
        String sql   = "SELECT bpc.EMail " + "FROM C_BPartner bp" + " INNER JOIN AD_User bpc ON (bp.C_BPartner_ID=bpc.C_BPartner_ID) " + "WHERE bp.C_BPartner_ID=?";

        if( AD_User_ID != 0 ) {
            sql += " AND bpc.AD_User_ID=?";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_BPartner_ID );

            if( AD_User_ID != 0 ) {
                pstmt.setInt( 2,AD_User_ID );
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                email = rs.getString( 1 );
            } else {
                s_log.warning( "None for C_BPartner_ID=" + C_BPartner_ID + ", AD_User_ID=" + AD_User_ID );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        return email;
    }    // getEMail_BPartner

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     * @param strict
     * @param ctx
     *
     * @return
     */

    public static String getEMail_User( int AD_User_ID,boolean strict,Properties ctx ) {
        String email = null;

        // Get ID

        String sql = "SELECT EMail, EMailUser, EMailUserPw, Name " + "FROM AD_User " + "WHERE AD_User_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_User_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                email = rs.getString( 1 );

                if( email != null ) {
                    email = cleanUpEMail( email );

                    if( ctx != null ) {
                        Env.setContext( ctx,EMail.CTX_EMAIL,email );
                        Env.setContext( ctx,EMail.CTX_EMAIL_USER,rs.getString( 2 ));
                        Env.setContext( ctx,EMail.CTX_EMAIL_USERPW,rs.getString( 3 ));
                    }
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"getEMail_User - " + sql,e );
        }

        if( (email == null) || (email.length() == 0) ) {
            s_log.warning( "getEMail_User - EMail not found - AD_User_ID=" + AD_User_ID );

            if( strict ) {
                return null;
            }

            email = getEMail_Bogus( (ctx == null)
                                    ?Env.getCtx()
                                    :ctx );
        }

        return email;
    }    // getEMail_User

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     *
     * @return
     */

    public static String getEMail_User( int AD_User_ID ) {
        return getEMail_User( AD_User_ID,false,null );
    }    // getEMail_User

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param strict
     *
     * @return
     */

    public static String getEMail_User( Properties ctx,boolean strict ) {
        String from = Env.getContext( ctx,EMail.CTX_EMAIL );

        if( from.length() != 0 ) {
            return from;
        }

        int AD_User_ID = Env.getContextAsInt( ctx,"#AD_User_ID" );

        from = getEMail_User( AD_User_ID,strict,ctx );

        return from;
    }    // getEMail_User

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static String getEMail_Bogus( Properties ctx ) {
        String email = System.getProperty( "user.name" ) + "@" + Env.getContext( ctx,"#AD_Client_Name" ) + ".com";

        email = cleanUpEMail( email );

        return email;
    }    // getBogusEMail

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static String getEMail_Request( Properties ctx ) {
        String email = Env.getContext( ctx,EMail.CTX_REQUEST_EMAIL );

        if( email.length() != 0 ) {
            return email;
        }

        String sql = "SELECT RequestEMail, RequestUser, RequestUserPw " + "FROM AD_Client " + "WHERE AD_Client_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,Env.getAD_Client_ID( ctx ));

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                email = rs.getString( 1 );
                email = cleanUpEMail( email );
                Env.setContext( ctx,EMail.CTX_REQUEST_EMAIL,email );
                Env.setContext( ctx,EMail.CTX_REQUEST_EMAIL_USER,rs.getString( 2 ));
                Env.setContext( ctx,EMail.CTX_REQUEST_EMAIL_USERPW,rs.getString( 3 ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getRequestEMail",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return email;
    }    // getRequestEMail

    /**
     * Descripción de Método
     *
     *
     * @param email
     *
     * @return
     */

    private static String cleanUpEMail( String email ) {
        if( (email == null) || (email.length() == 0) ) {
            return "";
        }

        //

        email = email.trim().toLowerCase();

        // Delete all spaces

        int pos = email.indexOf( " " );

        while( pos != -1 ) {
            email = email.substring( 0,pos ) + email.substring( pos + 1 );
            pos   = email.indexOf( " " );
        }

        return email;
    }    // cleanUpEMail

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     *
     * @return
     */

    public static String getNameOfUser( int AD_User_ID ) {
        String name = "?";

        // Get ID

        String sql = "SELECT Name FROM AD_User WHERE AD_User_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_User_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                name = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        return name;
    }    // getNameOfUser

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static String getSmtpHost1( Properties ctx ) {
        MClient client = MClient.get( ctx );

        if( (client != null) && (client.getSMTPHost() != null) ) {
            return client.getSMTPHost();
        }

        return "localhost";
    }    // getCurrentSmtpHost
}    // EMailUtil



/*
 *  @(#)EMailUtil.java   02.07.07
 * 
 *  Fin del fichero EMailUtil.java
 *  
 *  Versión 2.2
 *
 */
