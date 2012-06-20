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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MMailText;
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

public class SendMailText extends SvrProcess {

    /** Descripción de Campos */
    private int m_R_MailText_ID = -1;

    /** Descripción de Campos */
    private MMailText m_MailText = null;

    /** Descripción de Campos */
    private int m_AD_User_ID = -1;

    /** Descripción de Campos */
    private MClient m_client = null;

    /** Descripción de Campos */
    private MUser m_from = null;

    /** Descripción de Campos */
    private ArrayList m_list = new ArrayList();

    /** Descripción de Campos */
    private int m_R_InterestArea_ID = -1;

    /** Descripción de Campos */
    private int m_C_BP_Group_ID = -1;

    // comes here

    /**
     * Descripción de Método
     *
     * Obtenemos las varibles:
     * 
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "R_InterestArea_ID" )) {
                m_R_InterestArea_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "R_MailText_ID" )) {
                m_R_MailText_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_BP_Group_ID" )) {
                m_C_BP_Group_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "AD_User_ID" )) {
                m_AD_User_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "R_MailText_ID=" + m_R_MailText_ID );

        // Mail Test

        m_MailText = new MMailText( getCtx(),m_R_MailText_ID,get_TrxName());

        if( m_MailText.getR_MailText_ID() == 0 ) {
            throw new Exception( "Not found @R_MailText_ID@=" + m_R_MailText_ID );
        }

        // Client Info

        m_client = MClient.get( getCtx());

        if( m_client.getAD_Client_ID() == 0 ) {
            throw new Exception( "Not found @AD_Client_ID@" );
        }

        if( (m_client.getSMTPHost() == null) || (m_client.getSMTPHost().length() == 0) ) {
            throw new Exception( "No SMTP Host found" );
        }

        //

        if( m_AD_User_ID > 0 ) {
            m_from = new MUser( getCtx(),m_AD_User_ID,get_TrxName());

            if( m_from.getAD_User_ID() == 0 ) {
                throw new Exception( "No found @AD_User_ID@=" + m_AD_User_ID );
            }
        }

        log.fine( "send from " + m_from );

        long start = System.currentTimeMillis();

        // Loop to Interest Area Subscribers       *******************************

        log.info( "Send to R_InterestArea_ID=" + m_R_InterestArea_ID );

        String sql = "SELECT u.Name, u.EMail, u.AD_User_ID " + "FROM R_ContactInterest ci" + " INNER JOIN AD_User u ON (ci.AD_User_ID=u.AD_User_ID) " + "WHERE ci.IsActive='Y' AND u.IsActive='Y'" + " AND ci.OptOutDate IS NULL" + " AND u.EMail IS NOT NULL" + " AND ci.R_InterestArea_ID=?";
        PreparedStatement pstmt   = null;
        int               counter = 0;
        int               errors  = 0;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,m_R_InterestArea_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                Boolean ok = sendIndividualMail( rs.getString( 1 ),rs.getInt( 3 ));

                if( ok == null ) {
                    ;
                } else if( ok.booleanValue()) {
                    counter++;
                } else {
                    errors++;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"InterestArea",ex );
        }

        // Loop to Interest Area Subscribers       *******************************

        log.info( "Send to C_BP_Group_ID=" + m_C_BP_Group_ID );
        sql = "SELECT u.Name, u.EMail, u.AD_User_ID " + "FROM AD_User u" + " INNER JOIN C_BPartner bp ON (u.C_BPartner_ID=bp.C_BPartner_ID) " + "WHERE u.IsActive='Y' AND bp.IsActive='Y'" + " AND u.EMail IS NOT NULL" + " AND bp.C_BP_Group_ID=?";

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,m_C_BP_Group_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                Boolean ok = sendIndividualMail( rs.getString( 1 ),rs.getInt( 3 ));

                if( ok == null ) {
                    ;
                } else if( ok.booleanValue()) {
                    counter++;
                } else {
                    errors++;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"BP_Group",ex );
        }

        // Clean Up

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        return "@Created@=" + counter + ", @Errors@=" + errors + " - " + ( System.currentTimeMillis() - start ) + "ms";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param Name
     * @param AD_User_ID
     *
     * @return
     */

    private Boolean sendIndividualMail( String Name,int AD_User_ID ) {

        // Prevent two email

        Integer ii = new Integer( AD_User_ID );

        if( m_list.contains( ii )) {
            return null;
        }

        m_list.add( ii );

        //

        MUser  to      = new MUser( getCtx(),AD_User_ID,get_TrxName());
        String subject = m_MailText.getMailHeader();
        String message = m_MailText.getMailText( true );

        //

        EMail mail = new EMail( m_client,m_from,to,subject,message );

        if( !mail.isValid() &&!mail.isValid( true )) {
            log.warning( "NOT VALID - " + mail );
            to.setIsActive( false );
            to.addDescription( "Invalid EMail" );
            to.save();

            return Boolean.FALSE;
        }

        boolean   OK = EMail.SENT_OK.equals( mail.send());
        MUserMail um = new MUserMail( m_MailText,AD_User_ID,mail );

        um.save();

        //

        if( OK ) {
            log.fine( to.getEMail());
        } else {
            log.warning( "FAILURE - " + to.getEMail());
        }

        addLog( 0,null,null,( OK
                              ?"@OK@"
                              :"@ERROR@" ) + " - " + to.getEMail());

        return new Boolean( OK );
    }    // sendIndividualMail
}    // SendMailText



/*
 *  @(#)SendMailText.java   02.07.07
 * 
 *  Fin del fichero SendMailText.java
 *  
 *  Versión 2.2
 *
 */
