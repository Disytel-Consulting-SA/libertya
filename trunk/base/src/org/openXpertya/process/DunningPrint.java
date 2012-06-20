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

import java.io.File;
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MDunningLevel;
import org.openXpertya.model.MDunningRun;
import org.openXpertya.model.MDunningRunEntry;
import org.openXpertya.model.MMailText;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MUser;
import org.openXpertya.model.MUserMail;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.model.X_C_DunningRunEntry;
import org.openXpertya.print.MPrintFormat;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.util.EMail;
import org.openXpertya.util.ErrorUsuarioOXP;
import org.openXpertya.util.Language;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DunningPrint extends SvrProcess {

    /** Descripción de Campos */

    private boolean p_EmailPDF = false;

    /** Descripción de Campos */

    private int p_R_MailText_ID = 0;

    /** Descripción de Campos */

    private int p_C_DunningRun_ID = 0;

    /** Descripción de Campos */

    private boolean p_IsOnlyIfBPBalance = true;

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
            } else if( name.equals( "EmailPDF" )) {
                p_EmailPDF = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "R_MailText_ID" )) {
                p_R_MailText_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_DunningRun_ID" )) {
                p_C_DunningRun_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "IsOnlyIfBPBalance" )) {
                p_IsOnlyIfBPBalance = "Y".equals( para[ i ].getParameter());
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
        log.info( "C_DunningRun_ID=" + p_C_DunningRun_ID + ",R_MailText_ID=" + p_R_MailText_ID + ", EmailPDF=" + p_EmailPDF + ",IsOnlyIfBPBalance=" + p_IsOnlyIfBPBalance );

        // Need to have Template

        if( p_EmailPDF && (p_R_MailText_ID == 0) ) {
            throw new ErrorUsuarioOXP( "@NotFound@: @R_MailText_ID@" );
        }

        String    subject = "";
        String    message = "";
        MMailText mText   = null;

        if( p_EmailPDF ) {
            mText = new MMailText( getCtx(),p_R_MailText_ID,get_TrxName());

            if( p_EmailPDF && (mText.getID() == 0) ) {
                throw new ErrorUsuarioOXP( "@NotFound@: @R_MailText_ID@ - " + p_R_MailText_ID );
            }

            subject = mText.getMailHeader();
            message = mText.getMailText( true );
        }

        //

        MDunningRun run = new MDunningRun( getCtx(),p_C_DunningRun_ID,get_TrxName());

        if( run.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@: @C_DunningRun_ID@ - " + p_C_DunningRun_ID );
        }

        // Print Format on Dunning Level

        MDunningLevel level = new MDunningLevel( getCtx(),run.getC_DunningLevel_ID(),get_TrxName());
        MPrintFormat format = MPrintFormat.get( getCtx(),level.getDunning_PrintFormat_ID(),false );
        MClient            client  = MClient.get( getCtx());
        int                count   = 0;
        int                errors  = 0;
        MDunningRunEntry[] entries = run.getEntries( false );

        for( int i = 0;i < entries.length;i++ ) {
            MDunningRunEntry entry = entries[ i ];

            if( p_IsOnlyIfBPBalance && (entry.getAmt().signum() <= 0) ) {
                continue;
            }

            // To BPartner

            MBPartner bp = new MBPartner( getCtx(),entry.getC_BPartner_ID(),get_TrxName());

            if( bp.getID() == 0 ) {
                addLog( entry.getID(),null,null,"@NotFound@: @C_BPartner_ID@ " + entry.getC_BPartner_ID());
                errors++;

                continue;
            }

            // To User

            MUser to = new MUser( getCtx(),entry.getAD_User_ID(),get_TrxName());

            if( p_EmailPDF ) {
                if( to.getID() == 0 ) {
                    addLog( entry.getID(),null,null,"@NotFound@: @AD_User_ID@ - " + bp.getName());
                    errors++;

                    continue;
                } else if( (to.getEMail() == null) || (to.getEMail().length() == 0) ) {
                    addLog( entry.getID(),null,null,"@NotFound@: @EMail@ - " + to.getName());
                    errors++;

                    continue;
                }
            }

            // BP Language

            Language language  = Language.getLoginLanguage();    // Base Language
            String   tableName = "C_Dunning_Header_v";

            if( client.isMultiLingualDocument()) {
                tableName += "t";

                String AD_Language = bp.getAD_Language();

                if( AD_Language != null ) {
                    language = Language.getLanguage( AD_Language );
                }
            }

            format.setLanguage( language );
            format.setTranslationLanguage( language );

            // query

            MQuery query = new MQuery( tableName );

            query.addRestriction( "C_DunningRunEntry_ID",MQuery.EQUAL,new Integer( entry.getC_DunningRunEntry_ID()));

            // Engine

            PrintInfo info = new PrintInfo( bp.getName(),X_C_DunningRunEntry.Table_ID,entry.getC_DunningRunEntry_ID(),entry.getC_BPartner_ID());

            info.setDescription( bp.getName() + ", Amt=" + entry.getAmt());

            ReportEngine re      = new ReportEngine( getCtx(),format,query,info );
            boolean      printed = false;

            if( p_EmailPDF ) {
                EMail email = new EMail( client,null,to,null,null );

                if( !email.isValid()) {
                    addLog( entry.getID(),null,null,"@RequestActionEMailError@ Invalid EMail: " + to );
                    errors++;

                    continue;
                }

                email.setMessageHTML( subject,message );

                //

                File attachment = re.getPDF( File.createTempFile( "Dunning",".pdf" ));

                log.fine( to + " - " + attachment );
                email.addAttachment( attachment );

                //

                String msg = email.send();

                new MUserMail( mText,entry.getAD_User_ID(),email ).save();

                if( msg.equals( EMail.SENT_OK )) {
                    addLog( entry.getID(),null,null,bp.getName() + " @RequestActionEMailOK@" );
                    count++;
                    printed = true;
                } else {
                    addLog( entry.getID(),null,null,bp.getName() + " @RequestActionEMailError@ " + msg );
                    errors++;
                }
            } else {
                re.print();
                count++;
                printed = true;
            }
        }    // for all dunning letters

        if( p_EmailPDF ) {
            return "@Sent@=" + count + " - @Errors@=" + errors;
        }

        return "@Printed@=" + count;
    }    // doIt
}    // DunningPrint



/*
 *  @(#)DunningPrint.java   02.07.07
 * 
 *  Fin del fichero DunningPrint.java
 *  
 *  Versión 2.2
 *
 */
