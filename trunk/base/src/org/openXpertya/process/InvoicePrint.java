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
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MUser;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.print.MPrintFormat;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.util.DB;
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

public class InvoicePrint extends SvrProcess {

    /** Descripción de Campos */

    private boolean p_emailPDF = false;

    /** Descripción de Campos */

    private int p_R_MailText_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_dateInvoiced_From = null;

    /** Descripción de Campos */

    private Timestamp m_dateInvoiced_To = null;

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int m_C_Invoice_ID = 0;

    /** Descripción de Campos */

    private String m_DocumentNo_From = null;

    /** Descripción de Campos */

    private String m_DocumentNo_To = null;

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
            } else if( name.equals( "DateInvoiced" )) {
                m_dateInvoiced_From = (( Timestamp )para[ i ].getParameter());
                m_dateInvoiced_To   = (( Timestamp )para[ i ].getParameter_To());
            } else if( name.equals( "EmailPDF" )) {
                p_emailPDF = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "R_MailText_ID" )) {
                p_R_MailText_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BPartner_ID" )) {
                m_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_Invoice_ID" )) {
                m_C_Invoice_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "DocumentNo" )) {
                m_DocumentNo_From = ( String )para[ i ].getParameter();
                m_DocumentNo_To   = ( String )para[ i ].getParameter_To();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        if( (m_DocumentNo_From != null) && (m_DocumentNo_From.length() == 0) ) {
            m_DocumentNo_From = null;
        }

        if( (m_DocumentNo_To != null) && (m_DocumentNo_To.length() == 0) ) {
            m_DocumentNo_To = null;
        }
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

        // Need to have Template

        if( p_emailPDF && (p_R_MailText_ID == 0) ) {
            throw new ErrorUsuarioOXP( "@NotFound@: @R_MailText_ID@" );
        }

        // Too broad selection

        if( (m_C_BPartner_ID == 0) && (m_C_Invoice_ID == 0) && (m_dateInvoiced_From == null) && (m_dateInvoiced_To == null) && (m_DocumentNo_From == null) && (m_DocumentNo_To == null) ) {
            throw new ErrorUsuarioOXP( "@RestrictSelection@" );
        }

        MClient client = MClient.get( getCtx());

        // Get Info

        StringBuffer sql = new StringBuffer( "SELECT i.C_Invoice_ID,bp.AD_Language,c.IsMultiLingualDocument,"    // 1..3

        // Prio: 1. BPartner 2. DocType, 3. PrintFormat (Org)      //      see ReportCtl+MInvoice

        + " COALESCE(bp.Invoice_PrintFormat_ID, dt.AD_PrintFormat_ID, pf.Invoice_PrintFormat_ID),"    // 4
        + " dt.DocumentCopies+bp.DocumentCopies,"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           // 5
        + " bpc.AD_User_ID, i.DocumentNo,"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  // 6..7
        + " mt.MailHeader, mt.MailText,bp.C_BPartner_ID "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   // 8..10
        + "FROM C_Invoice i" + " INNER JOIN C_BPartner bp ON (i.C_BPartner_ID=bp.C_BPartner_ID)" + " LEFT OUTER JOIN AD_User bpc ON (i.AD_User_ID=bpc.AD_User_ID)" + " INNER JOIN AD_Client c ON (i.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN AD_PrintForm pf ON (i.AD_Client_ID=pf.AD_Client_ID)" + " INNER JOIN C_DocType dt ON (i.C_DocType_ID=dt.C_DocType_ID)" + " LEFT OUTER JOIN R_MailText mt ON (i.AD_Client_ID=mt.AD_Client_ID AND mt.R_MailText_ID=" ).append( p_R_MailText_ID ).append( ") WHERE pf.AD_Org_ID IN (0,i.AD_Org_ID) AND " );    // more them 1 PF
        boolean needAnd = false;

        if( m_C_Invoice_ID != 0 ) {
            sql.append( "i.C_Invoice_ID=" ).append( m_C_Invoice_ID );
        } else {
            if( m_C_BPartner_ID != 0 ) {
                sql.append( "i.C_BPartner_ID=" ).append( m_C_BPartner_ID );
                needAnd = true;
            }

            if( (m_dateInvoiced_From != null) && (m_dateInvoiced_To != null) ) {
                if( needAnd ) {
                    sql.append( " AND " );
                }

                sql.append( "TRUNC(i.DateInvoiced) BETWEEN " ).append( DB.TO_DATE( m_dateInvoiced_From,true )).append( " AND " ).append( DB.TO_DATE( m_dateInvoiced_To,true ));
                needAnd = true;
            } else if( m_dateInvoiced_From != null ) {
                if( needAnd ) {
                    sql.append( " AND " );
                }

                sql.append( "TRUNC(i.DateInvoiced) >= " ).append( DB.TO_DATE( m_dateInvoiced_From,true ));
                needAnd = true;
            } else if( m_dateInvoiced_To != null ) {
                if( needAnd ) {
                    sql.append( " AND " );
                }

                sql.append( "TRUNC(i.DateInvoiced) <= " ).append( DB.TO_DATE( m_dateInvoiced_To,true ));
                needAnd = true;
            } else if( (m_DocumentNo_From != null) && (m_DocumentNo_To != null) ) {
                if( needAnd ) {
                    sql.append( " AND " );
                }

                sql.append( "i.DocumentNo BETWEEN " ).append( DB.TO_STRING( m_DocumentNo_From )).append( " AND " ).append( DB.TO_STRING( m_DocumentNo_To ));
            } else if( m_DocumentNo_From != null ) {
                if( needAnd ) {
                    sql.append( " AND " );
                }

                if( m_DocumentNo_From.indexOf( '%' ) == -1 ) {
                    sql.append( "i.DocumentNo >= " ).append( DB.TO_STRING( m_DocumentNo_From ));
                } else {
                    sql.append( "i.DocumentNo LIKE " ).append( DB.TO_STRING( m_DocumentNo_From ));
                }
            }
        }

        sql.append( " ORDER BY i.C_Invoice_ID, pf.AD_Org_ID DESC" );    // more than 1 PF record
        log.fine( sql.toString());

        MPrintFormat format                = null;
        int          old_AD_PrintFormat_ID = -1;
        int          old_C_Invoice_ID      = -1;
        int          C_BPartner_ID         = 0;
        int          count                 = 0;
        int          errors                = 0;

        try {
            Statement stmt = DB.createStatement();
            ResultSet rs   = stmt.executeQuery( sql.toString());

            while( rs.next()) {
                int C_Invoice_ID = rs.getInt( 1 );

                if( C_Invoice_ID == old_C_Invoice_ID ) {    // multiple pf records
                    continue;
                }

                old_C_Invoice_ID = C_Invoice_ID;

                // Set Language when enabled

                Language language = Language.getLoginLanguage();    // Base Language
                String AD_Language = rs.getString( 2 );

                if( (AD_Language != null) && "Y".equals( rs.getString( 3 ))) {
                    language = Language.getLanguage( AD_Language );
                }

                //

                int AD_PrintFormat_ID = rs.getInt( 4 );
                int copies            = rs.getInt( 5 );

                if( copies == 0 ) {
                    copies = 1;
                }

                int    AD_User_ID = rs.getInt( 6 );
                MUser  to         = new MUser( getCtx(),AD_User_ID,get_TrxName());
                String DocumentNo = rs.getString( 7 );

                //

                String subject = rs.getString( 8 );
                String message = rs.getString( 9 );

                C_BPartner_ID = rs.getInt( 10 );

                //

                String documentDir = client.getDocumentDir();

                if( (documentDir == null) || (documentDir.length() == 0) ) {
                    documentDir = ".";
                }

                //

                if( p_emailPDF && ( (to.getID() == 0) || (to.getEMail() == null) || (to.getEMail().length() == 0) ) ) {
                    addLog( C_Invoice_ID,null,null,DocumentNo + " @RequestActionEMailNoTo@" );
                    errors++;

                    continue;
                }

                if( AD_PrintFormat_ID == 0 ) {
                    addLog( C_Invoice_ID,null,null,DocumentNo + " No Print Format" );
                    errors++;

                    continue;
                }

                // Get Format & Data

                if( AD_PrintFormat_ID != old_AD_PrintFormat_ID ) {
                    format = MPrintFormat.get( getCtx(),AD_PrintFormat_ID,false );
                    old_AD_PrintFormat_ID = AD_PrintFormat_ID;
                }

                format.setLanguage( language );
                format.setTranslationLanguage( language );

                // query

                MQuery query = new MQuery( "C_Invoice_Header_v" );

                query.addRestriction( "C_Invoice_ID",MQuery.EQUAL,new Integer( C_Invoice_ID ));

                // Engine

                PrintInfo info = new PrintInfo( DocumentNo,X_C_Invoice.Table_ID,C_Invoice_ID,C_BPartner_ID );

                info.setCopies( copies );

                ReportEngine re = new ReportEngine( getCtx(),format,query,info );
                boolean printed = false;

                if( p_emailPDF ) {
                    EMail email = new EMail( client,null,to,null,null );

                    if( !email.isValid()) {
                        addLog( C_Invoice_ID,null,null,DocumentNo + " @RequestActionEMailError@ Invalid EMail: " + to );
                        errors++;

                        continue;
                    }

                    email.setMessageHTML( subject,message );

                    //

                    File attachment = re.getPDF( new File( MInvoice.getPDFFileName( documentDir,C_Invoice_ID )));

                    log.fine( to + " - " + attachment );
                    email.addAttachment( attachment );

                    //

                    String msg = email.send();

                    if( msg.equals( EMail.SENT_OK )) {
                        addLog( C_Invoice_ID,null,null,DocumentNo + " @RequestActionEMailOK@" );
                        count++;
                        printed = true;
                    } else {
                        addLog( C_Invoice_ID,null,null,DocumentNo + " @RequestActionEMailError@ " + msg );
                        errors++;
                    }
                } else {
                    re.print();
                    count++;
                    printed = true;
                }

                // Print Confirm

                if( printed ) {
                    StringBuffer sb = new StringBuffer( "UPDATE C_Invoice " + "SET DatePrinted=SysDate, IsPrinted='Y' WHERE C_Invoice_ID=" ).append( C_Invoice_ID );
                    int no = DB.executeUpdate( sb.toString(),get_TrxName());
                }
            }

            rs.close();
            stmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt - " + sql,e );

            throw new Exception( e );
        }

        //

        if( p_emailPDF ) {
            return "@Sent@=" + count + " - @Errors@=" + errors;
        }

        return "@Printed@=" + count;
    }    // doIt
}    // InvoicePrint



/*
 *  @(#)InvoicePrint.java   02.07.07
 * 
 *  Fin del fichero InvoicePrint.java
 *  
 *  Versión 2.2
 *
 */
