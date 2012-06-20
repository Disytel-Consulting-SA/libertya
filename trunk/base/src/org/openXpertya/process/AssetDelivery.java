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



package org.openXpertya.process;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MAsset;
import org.openXpertya.model.MAssetDelivery;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MMailText;
import org.openXpertya.model.MProductDownload;
import org.openXpertya.model.MUser;
import org.openXpertya.model.MUserMail;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AssetDelivery extends SvrProcess {

    /** Descripción de Campos */

    private MClient m_client = null;

    /** Descripción de Campos */

    private int m_A_Asset_Group_ID = 0;

    /** Descripción de Campos */

    private int m_M_Product_ID = 0;

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int m_A_Asset_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_GuaranteeDate = null;

    /** Descripción de Campos */

    private int m_NoGuarantee_MailText_ID = 0;

    /** Descripción de Campos */

    private boolean m_AttachAsset = false;

    //

    /** Descripción de Campos */

    private MMailText m_MailText = null;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "A_Asset_Group_ID" )) {
                m_A_Asset_Group_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "M_Product_ID" )) {
                m_M_Product_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BPartner_ID" )) {
                m_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "A_Asset_ID" )) {
                m_A_Asset_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "GuaranteeDate" )) {
                m_GuaranteeDate = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "NoGuarantee_MailText_ID" )) {
                m_NoGuarantee_MailText_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AttachAsset" )) {
                m_AttachAsset = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        if( m_GuaranteeDate == null ) {
            m_GuaranteeDate = new Timestamp( System.currentTimeMillis());
        }

        //

        m_client = MClient.get( getCtx());
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
        log.info( "" );

        long start = System.currentTimeMillis();

        // Test

        if( m_client.getSMTPHost() == null ) {
            throw new Exception( "No Client SMTP Info" );
        }

        if( m_client.getRequestEMail() == null ) {
            throw new Exception( "No Client Request User" );
        }

        // Asset selected

        if( m_A_Asset_ID != 0 ) {
            String msg = deliverIt( m_A_Asset_ID );

            addLog( m_A_Asset_ID,null,null,msg );

            return msg;
        }

        //

        StringBuffer sql = new StringBuffer( "SELECT A_Asset_ID, GuaranteeDate " + "FROM A_Asset a" + " INNER JOIN M_Product p ON (a.M_Product_ID=p.M_Product_ID) " + "WHERE " );

        if( m_A_Asset_Group_ID != 0 ) {
            sql.append( "a.A_Asset_Group_ID=" ).append( m_A_Asset_Group_ID ).append( " AND " );
        }

        if( m_M_Product_ID != 0 ) {
            sql.append( "p.M_Product_ID=" ).append( m_M_Product_ID ).append( " AND " );
        }

        if( m_C_BPartner_ID != 0 ) {
            sql.append( "a.C_BPartner_ID=" ).append( m_C_BPartner_ID ).append( " AND " );
        }

        String s = sql.toString();

        if( s.endsWith( " WHERE " )) {
            throw new Exception( "@RestrictSelection@" );
        }

        // No mail to expired

        if( m_NoGuarantee_MailText_ID == 0 ) {
            sql.append( "TRUNC(GuaranteeDate) >= " ).append( DB.TO_DATE( m_GuaranteeDate,true ));
            s = sql.toString();
        }

        // Clean up

        if( s.endsWith( " AND " )) {
            s = sql.substring( 0,sql.length() - 5 );
        }

        //

        Statement stmt      = null;
        int       count     = 0;
        int       errors    = 0;
        int       reminders = 0;

        try {
            stmt = DB.createStatement();

            ResultSet rs = stmt.executeQuery( s );

            while( rs.next()) {
                int       A_Asset_ID    = rs.getInt( 1 );
                Timestamp GuaranteeDate = rs.getTimestamp( 2 );

                // Guarantee Expired

                if( (GuaranteeDate != null) && GuaranteeDate.before( m_GuaranteeDate )) {
                    if( m_NoGuarantee_MailText_ID != 0 ) {
                        sendNoGuaranteeMail( A_Asset_ID,m_NoGuarantee_MailText_ID,get_TrxName());
                        reminders++;
                    }
                } else    // Guarantee valid
                {
                    String msg = deliverIt( A_Asset_ID );

                    addLog( A_Asset_ID,null,null,msg );

                    if( msg.startsWith( "** " )) {
                        errors++;
                    } else {
                        count++;
                    }
                }
            }

            rs.close();
            stmt.close();
            stmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,s,e );
        } finally {
            try {
                if( stmt != null ) {
                    stmt.close();
                }
            } catch( Exception e ) {
            }

            stmt = null;
        }

        log.info( "Count=" + count + ", Errors=" + errors + ", Reminder=" + reminders + " - " + ( System.currentTimeMillis() - start ) + "ms" );

        return "@Sent@=" + count + " - @Errors@=" + errors;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param A_Asset_ID
     * @param R_MailText_ID
     * @param trxName
     *
     * @return
     */

    private String sendNoGuaranteeMail( int A_Asset_ID,int R_MailText_ID,String trxName ) {
        MAsset asset = new MAsset( getCtx(),A_Asset_ID,trxName );

        if( asset.getAD_User_ID() == 0 ) {
            return "** No Asset User";
        }

        MUser user = new MUser( getCtx(),asset.getAD_User_ID(),get_TrxName());

        if( (user.getEMail() == null) || (user.getEMail().length() == 0) ) {
            return "** No Asset User Email";
        }

        if( (m_MailText == null) || (m_MailText.getR_MailText_ID() != R_MailText_ID) ) {
            m_MailText = new MMailText( getCtx(),R_MailText_ID,get_TrxName());
        }

        if( (m_MailText.getMailHeader() == null) || (m_MailText.getMailHeader().length() == 0) ) {
            return "** No Subject";
        }

        // Create Mail

        EMail email = new EMail( m_client,null,user,null,null );

        if( m_MailText.isHtml()) {
            email.setMessageHTML( m_MailText.getMailHeader(),m_MailText.getMailText( true ));
        } else {
            email.setSubject( m_MailText.getMailHeader());
            email.setMessageText( m_MailText.getMailText( true ));
        }

        String msg = email.send();

        new MUserMail( m_MailText,asset.getAD_User_ID(),email ).save();

        if( !EMail.SENT_OK.equals( msg )) {
            return "** Not delivered: " + user.getEMail() + " - " + msg;
        }

        //

        return user.getEMail();
    }    // sendNoGuaranteeMail

    /**
     * Descripción de Método
     *
     *
     * @param A_Asset_ID
     *
     * @return
     */

    private String deliverIt( int A_Asset_ID ) {
        log.fine( "A_Asset_ID=" + A_Asset_ID );

        long start = System.currentTimeMillis();

        //

        MAsset asset = new MAsset( getCtx(),A_Asset_ID,get_TrxName());

        if( asset.getAD_User_ID() == 0 ) {
            return "** No Asset User";
        }

        MUser user = new MUser( getCtx(),asset.getAD_User_ID(),get_TrxName());

        if( (user.getEMail() == null) || (user.getEMail().length() == 0) ) {
            return "** No Asset User Email";
        }

        if( asset.getProductR_MailText_ID() == 0 ) {
            return "** Product Mail Text";
        }

        if( (m_MailText == null) || (m_MailText.getR_MailText_ID() != asset.getProductR_MailText_ID())) {
            m_MailText = new MMailText( getCtx(),asset.getProductR_MailText_ID(),get_TrxName());
        }

        if( (m_MailText.getMailHeader() == null) || (m_MailText.getMailHeader().length() == 0) ) {
            return "** No Subject";
        }

        // Create Mail

        EMail email = new EMail( m_client,null,user,null,null );

        if( !email.isValid()) {
            asset.setHelp( asset.getHelp() + " - Invalid EMail" );
            asset.setIsActive( false );

            return "** Invalid EMail: " + user.getEMail();
        }

        if( m_client.isSmtpAuthorization()) {
            email.createAuthenticator( m_client.getRequestUser(),m_client.getRequestUserPW());
        }

        if( m_MailText.isHtml() || m_AttachAsset ) {
            email.setMessageHTML( m_MailText.getMailHeader(),m_MailText.getMailText( true ));
        } else {
            email.setSubject( m_MailText.getMailHeader());
            email.setMessageText( m_MailText.getMailText( true ));
        }

        if( m_AttachAsset ) {
            MProductDownload[] pdls = asset.getProductDownloads();

            if( pdls != null ) {
                for( int i = 0;i < pdls.length;i++ ) {
                    URL url = pdls[ i ].getDownloadURL( m_client.getDocumentDir());

                    if( url != null ) {
                        email.addAttachment( url );
                    }
                }
            } else {
                log.warning( "No DowloadURL for A_Asset_ID=" + A_Asset_ID );
            }
        }

        String msg = email.send();

        new MUserMail( m_MailText,asset.getAD_User_ID(),email ).save();

        if( !EMail.SENT_OK.equals( msg )) {
            return "** Not delivered: " + user.getEMail() + " - " + msg;
        }

        MAssetDelivery ad = asset.confirmDelivery( email,user.getAD_User_ID());

        ad.save();
        asset.save();

        //

        log.fine(( System.currentTimeMillis() - start ) + " ms" );

        // success

        return user.getEMail() + " - " + asset.getProductVersionNo();
    }    // deliverIt
}    // AssetDelivery



/*
 *  @(#)AssetDelivery.java   02.07.07
 * 
 *  Fin del fichero AssetDelivery.java
 *  
 *  Versión 2.2
 *
 */
