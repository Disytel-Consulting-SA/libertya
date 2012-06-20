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



package org.openXpertya.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MAlert;
import org.openXpertya.model.MAlertProcessor;
import org.openXpertya.model.MAlertProcessorLog;
import org.openXpertya.model.MAlertRecipient;
import org.openXpertya.model.MAlertRule;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MUser;
import org.openXpertya.model.MUserRoles;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Env;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AlertProcessor extends ServidorOXP {

    /**
     * Constructor de la clase ...
     *
     *
     * @param model
     */

    public AlertProcessor( MAlertProcessor model ) {
        super( model,180 );    // 3 monute delay
        m_model  = model;
        m_client = MClient.get( model.getCtx(),model.getAD_Client_ID());
    }    // AlertProcessor

    /** Descripción de Campos */

    private MAlertProcessor m_model = null;

    /** Descripción de Campos */

    private StringBuffer m_summary = new StringBuffer();

    /** Descripción de Campos */

    private StringBuffer m_errors = new StringBuffer();

    /** Descripción de Campos */

    private MClient m_client = null;

    /**
     * Descripción de Método
     *
     */

    protected void doWork() {
        m_summary = new StringBuffer();
        m_errors  = new StringBuffer();

        //

        int      count      = 0;
        int      countError = 0;
        MAlert[] alerts     = m_model.getAlerts( false );

        for( int i = 0;i < alerts.length;i++ ) {
            if( !processAlert( alerts[ i ] )) {
                countError++;
            }

            count++;
        }

        //

        String summary = "Total=" + count;

        if( countError > 0 ) {
            summary += ", Not processed=" + countError;
        }

        summary += " - ";
        m_summary.insert( 0,summary );

        //

        int no = m_model.deleteLog();

        m_summary.append( "Logs deleted=" ).append( no );

        //

        MAlertProcessorLog pLog = new MAlertProcessorLog( m_model,m_summary.toString());

        pLog.setReference( "#" + String.valueOf( p_runCount ) + " - " + TimeUtil.formatElapsed( new Timestamp( p_startWork )));
        pLog.setTextMsg( m_errors.toString());
        pLog.save();
    }    // doWork

    /**
     * Descripción de Método
     *
     *
     * @param alert
     *
     * @return
     */

    private boolean processAlert( MAlert alert ) {
        log.info( "processAlert - " + alert );

        if( !alert.isValid()) {
            return false;
        }

        StringBuffer message = new StringBuffer( alert.getAlertMessage()).append( Env.NL );

        //

        boolean      valid     = true;
        boolean      processed = false;
        MAlertRule[] rules     = alert.getRules( false );

        for( int i = 0;i < rules.length;i++ ) {
            if( i > 0 ) {
                message.append( Env.NL ).append( "================================" ).append( Env.NL );
            }

            MAlertRule rule = rules[ i ];

            log.fine( "processAlert - " + rule );

            if( !rule.isValid()) {
                continue;
            }

            // Pre

            String sql = rule.getPreProcessing();

            if( (sql != null) && (sql.length() > 0) ) {
                int no = DB.executeUpdate( sql,false );

                if( no == -1 ) {
                    ValueNamePair error = CLogger.retrieveError();

                    rule.setErrorMsg( "Pre=" + error.getName());
                    m_errors.append( "Pre=" + error.getName());
                    rule.setIsValid( false );
                    rule.save();
                    valid = false;

                    break;
                }
            }    // Pre

            // The processing

            sql = rule.getSql();

            if( alert.isEnforceRoleSecurity() || alert.isEnforceClientSecurity()) {
                int AD_Role_ID = alert.getFirstAD_Role_ID();

                if( AD_Role_ID == -1 ) {
                    AD_Role_ID = alert.getFirstUserAD_Role_ID();
                }

                if( AD_Role_ID != -1 ) {
                    MRole role = MRole.get( getCtx(),AD_Role_ID,null );

                    sql = role.addAccessSQL( sql,null,true,false );
                }
            }

            try {
                String text = listSqlSelect( sql );

                if( (text != null) && (text.length() > 0) ) {
                    message.append( text );
                    processed = true;
                }
            } catch( Exception e ) {
                rule.setErrorMsg( "Select=" + e.getLocalizedMessage());
                m_errors.append( "Select=" + e.getLocalizedMessage());
                rule.setIsValid( false );
                rule.save();
                valid = false;

                break;
            }

            // Pre

            sql = rule.getPostProcessing();

            if( (sql != null) && (sql.length() > 0) ) {
                int no = DB.executeUpdate( sql,false );

                if( no == -1 ) {
                    ValueNamePair error = CLogger.retrieveError();

                    rule.setErrorMsg( "Post=" + error.getName());
                    m_errors.append( "Post=" + error.getName());
                    rule.setIsValid( false );
                    rule.save();
                    valid = false;

                    break;
                }
            }    // Post

            processed = true;
        }        // for all rules

        // Update header if error

        if( !valid || ( valid &&!processed )) {
            alert.setIsValid( false );
            alert.save();

            return false;
        }

        // Nothing to report

        if( (message.length() == 0) && processed ) {
            m_summary.append( alert.getName()).append( "=No Result - " );

            return true;
        }

        // Send Message

        int               countMail  = 0;
        MAlertRecipient[] recipients = alert.getRecipients( false );

        for( int i = 0;i < recipients.length;i++ ) {
            MAlertRecipient recipient = recipients[ i ];

            if( recipient.getAD_User_ID() >= 0 ) {    // System == 0
                if( sendEmail( alert,message.toString(),recipient.getAD_User_ID())) {
                    countMail++;
                }
            }

            if( recipient.getAD_Role_ID() >= 0 )    // SystemAdministrator == 0
            {
                MUserRoles[] urs = MUserRoles.getOfRole( getCtx(),recipient.getAD_Role_ID());

                for( int j = 0;j < urs.length;j++ ) {
                    MUserRoles ur = urs[ j ];

                    if( sendEmail( alert,message.toString(),ur.getAD_User_ID())) {
                        countMail++;
                    }
                }
            }
        }

        m_summary.append( alert.getName()).append( " (EMails=" ).append( countMail ).append( ") - " );

        return valid;
    }    // processAlert

    /**
     * Descripción de Método
     *
     *
     * @param alert
     * @param message
     * @param AD_User_ID
     *
     * @return
     */

    private boolean sendEmail( MAlert alert,String message,int AD_User_ID ) {
        MUser user  = MUser.get( getCtx(),AD_User_ID );
        EMail email = new EMail( m_client,null,user,alert.getAlertSubject(),message );

        return EMail.SENT_OK.equals( email.send());
    }    // sendEmail

    /**
     * Descripción de Método
     *
     *
     * @param sql
     *
     * @return
     *
     * @throws Exception
     */

    private String listSqlSelect( String sql ) throws Exception {
        StringBuffer      result = new StringBuffer();
        PreparedStatement pstmt  = null;
        Exception         error  = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet         rs   = pstmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            while( rs.next()) {
                result.append( "------------------" ).append( Env.NL );

                for( int col = 1;col <= meta.getColumnCount();col++ ) {
                    result.append( meta.getColumnLabel( col )).append( " = " );
                    result.append( rs.getString( col ));
                    result.append( Env.NL );
                }    // for all columns
            }

            if( result.length() == 0 ) {
                log.fine( "listSqlSelect - no rows selected" );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"listSqlSelect - " + sql,e );
            error = e;
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Error occured

        if( error != null ) {
            throw new Exception( "(" + sql + ") " + Env.NL + error.getLocalizedMessage());
        }

        return result.toString();
    }    // listSqlSelect

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerInfo() {
        return "#" + p_runCount + " - Last=" + m_summary.toString();
    }    // getServerInfo

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        OpenXpertya.startup( true );

        MAlertProcessor model = new MAlertProcessor( Env.getCtx(),100,null );
        AlertProcessor  ap    = new AlertProcessor( model );

        ap.start();
    }    // main
}    // AlertProcessor



/*
 *  @(#)AlertProcessor.java   24.03.06
 * 
 *  Fin del fichero AlertProcessor.java
 *  
 *  Versión 2.2
 *
 */
