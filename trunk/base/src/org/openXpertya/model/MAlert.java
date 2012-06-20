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

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAlert extends X_AD_Alert {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Alert_ID
     * @param trxName
     */

    public MAlert( Properties ctx,int AD_Alert_ID,String trxName ) {
        super( ctx,AD_Alert_ID,trxName );

        if( AD_Alert_ID == 0 ) {

            // setAD_AlertProcessor_ID (0);
            // setName (null);
            // setAlertMessage (null);
            // setAlertSubject (null);

            setEnforceClientSecurity( true );    // Y
            setEnforceRoleSecurity( true );      // Y
            setIsValid( true );                  // Y
        }
    }                                            // MAlert

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAlert( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAlert

    /** Descripción de Campos */

    private MAlertRule[] m_rules = null;

    /** Descripción de Campos */

    private MAlertRecipient[] m_recipients = null;

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MAlertRule[] getRules( boolean reload ) {
        if( (m_rules != null) &&!reload ) {
            return m_rules;
        }

        String            sql   = "SELECT * FROM AD_AlertRule " + "WHERE AD_Alert_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_Alert_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAlertRule( getCtx(),rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getRules",e );
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

        m_rules = new MAlertRule[ list.size()];
        list.toArray( m_rules );

        return m_rules;
    }    // getRules

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MAlertRecipient[] getRecipients( boolean reload ) {
        if( (m_recipients != null) &&!reload ) {
            return m_recipients;
        }

        String sql = "SELECT * FROM AD_AlertRecipient " + "WHERE AD_Alert_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_Alert_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAlertRecipient( getCtx(),rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getRecipients",e );
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

        m_recipients = new MAlertRecipient[ list.size()];
        list.toArray( m_recipients );

        return m_recipients;
    }    // getRecipients

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getFirstAD_Role_ID() {
        getRecipients( false );

        for( int i = 0;i < m_recipients.length;i++ ) {
            if( m_recipients[ i ].getAD_Role_ID() != -1 ) {
                return m_recipients[ i ].getAD_Role_ID();
            }
        }

        return -1;
    }    // getForstAD_Role_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getFirstUserAD_Role_ID() {
        getRecipients( false );

        int AD_User_ID = getFirstAD_User_ID();

        if( AD_User_ID != -1 ) {
            MUserRoles[] urs = MUserRoles.getOfUser( getCtx(),AD_User_ID );

            for( int i = 0;i < urs.length;i++ ) {
                if( urs[ i ].isActive()) {
                    return urs[ i ].getAD_Role_ID();
                }
            }
        }

        return -1;
    }    // getFirstUserAD_Role_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getFirstAD_User_ID() {
        getRecipients( false );

        for( int i = 0;i < m_recipients.length;i++ ) {
            if( m_recipients[ i ].getAD_User_ID() != -1 ) {
                return m_recipients[ i ].getAD_User_ID();
            }
        }

        return -1;
    }    // getFirstAD_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MAlert[" );

        sb.append( getID()).append( "-" ).append( getName()).append( ",Valid=" ).append( isValid());

        if( m_rules != null ) {
            sb.append( ",Rules=" ).append( m_rules.length );
        }

        if( m_recipients != null ) {
            sb.append( ",Recipients=" ).append( m_recipients.length );
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString
}    // MAlert



/*
 *  @(#)MAlert.java   02.07.07
 * 
 *  Fin del fichero MAlert.java
 *  
 *  Versión 2.2
 *
 */
