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



package org.openXpertya.print;

import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.model.X_C_DunningRunEntry;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.model.X_C_PaySelectionCheck;
import org.openXpertya.model.X_C_Project;
import org.openXpertya.model.X_C_RfQResponse;
import org.openXpertya.model.X_M_InOut;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Language;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ReportDesign implements PrintServiceAttributeListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param pf
     * @param query
     * @param info
     */

    public ReportDesign( Properties ctx,MPrintFormat pf,MQuery query,PrintInfo info ) {
        if( pf == null ) {
            throw new IllegalArgumentException( "ReportDesign - no PrintFormat" );
        }

        log.info( pf + " -- " + query );
        m_ctx = ctx;

        //

        m_printFormat = pf;
        m_info        = info;
        setQuery( query );    // loads Data
    }                         // ReportDesign

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ReportEngine.class );

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private MPrintFormat m_printFormat;

    /** Descripción de Campos */

    private PrintInfo m_info;

    /** Descripción de Campos */

    private MQuery m_query;

    /** Descripción de Campos */

    private PrintData m_printData;

    /** Descripción de Campos */

    private LayoutDesign m_layout = null;

    /** Descripción de Campos */

    private String m_printerName = Ini.getProperty( Ini.P_PRINTER );

    /** Descripción de Campos */

    private ViewDesign m_viewDesign = null;

    /** Descripción de Campos */

    private int m_copies = 0;

    /**
     * Descripción de Método
     *
     *
     * @param pf
     */

    public void setPrintFormat( MPrintFormat pf )    // convertido a publico por ConSerTi para ser usado en el POS
    {
        m_printFormat = pf;

        if( m_layout != null ) {
            setPrintData();
            m_layout.setPrintFormat( pf,false );
            m_layout.setPrintData( m_printData,m_query,true );    // format changes data
        }

        if( m_viewDesign != null ) {
            m_viewDesign.revalidate();
        }
    }    // setPrintFormat

    /**
     * Descripción de Método
     *
     *
     * @param query
     */

    protected void setQuery( MQuery query ) {
        m_query = query;

        if( query == null ) {
            return;
        }

        //

        setPrintData();

        if( m_layout != null ) {
            m_layout.setPrintData( m_printData,m_query,true );
        }

        if( m_viewDesign != null ) {
            m_viewDesign.revalidate();
        }
    }    // setQuery

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MQuery getQuery() {
        return m_query;
    }    // getQuery

    /**
     * Descripción de Método
     *
     */

    private void setPrintData() {
        if( m_query == null ) {
            return;
        }

        DataEngine de = new DataEngine( m_printFormat.getLanguage());

        setPrintData( de.getPrintData( m_ctx,m_printFormat,m_query ));

        // m_printData.dump();

    }    // setPrintData

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PrintData getPrintData() {
        return m_printData;
    }    // getPrintData

    /**
     * Descripción de Método
     *
     *
     * @param printData
     */

    public void setPrintData( PrintData printData ) {
        if( printData == null ) {
            return;
        }

        m_printData = printData;
    }    // setPrintData

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getCopies() {
        return m_copies;
    }    // getCopies

    /**
     * Descripción de Método
     *
     *
     * @param copies
     */

    public void setCopies( int copies ) {
        m_copies = copies;
    }    // setCopies

    /**
     * Descripción de Método
     *
     */

    private void layout() {
        if( m_printFormat == null ) {
            throw new IllegalStateException( "ReportDesign.layout - no print format" );
        }

        if( m_printData == null ) {
            throw new IllegalStateException( "ReportDesign.layout - no print data (Delete Print Format and restart)" );
        }

        m_layout = new LayoutDesign( m_printFormat,m_printData,m_query );
    }    // layout

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected LayoutDesign getLayout() {
        if( m_layout == null ) {
            layout();
        }

        return m_layout;
    }    // getLayout

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_printFormat.getName();
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MPrintFormat getPrintFormat() {
        return m_printFormat;
    }    // getPrintFormat

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PrintInfo getPrintInfo() {
        return m_info;
    }    // getPrintInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Properties getCtx() {
        return m_layout.getCtx();
    }    // getCtx

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRowCount() {
        return m_printData.getRowCount();
    }    // getRowCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getColumnCount() {
        if( m_layout != null ) {
            return m_layout.getColumnCount();
        }

        return 0;
    }    // getColumnCount

    /**
     * Descripción de Método
     *
     *
     * @param vd
     *
     * @return
     */

    public ViewDesign getViewDesign( ViewerDesign vd ) {
        if( m_layout == null ) {
            layout();
        }

        if( m_viewDesign == null ) {
            m_viewDesign = new ViewDesign( m_layout,this,vd );
        }

        return m_viewDesign;
    }    // getViewDesign

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param pi
     *
     * @return
     */

    static public ReportDesign get( Properties ctx,ProcessInfo pi ) {
        int AD_Client_ID = Env.getAD_Client_ID( ctx );

        //

        int     AD_Table_ID       = 0;
        int     AD_ReportView_ID  = 0;
        String  TableName         = null;
        String  whereClause       = "";
        int     AD_PrintFormat_ID = 0;
        boolean IsForm            = false;
        int     Client_ID         = -1;

        // Get AD_Table_ID and TableName

        String sql = "SELECT rv.AD_ReportView_ID,rv.WhereClause," + " t.AD_Table_ID,t.TableName, pf.AD_PrintFormat_ID, pf.IsForm, pf.AD_Client_ID " + "FROM AD_PInstance pi" + " INNER JOIN AD_Process p ON (pi.AD_Process_ID=p.AD_Process_ID)" + " INNER JOIN AD_ReportView rv ON (p.AD_ReportView_ID=rv.AD_ReportView_ID)" + " INNER JOIN AD_Table t ON (rv.AD_Table_ID=t.AD_Table_ID)" + " LEFT OUTER JOIN AD_PrintFormat pf ON (p.AD_ReportView_ID=pf.AD_ReportView_ID AND pf.AD_Client_ID IN (0,?)) " + "WHERE pi.AD_PInstance_ID=? "    // #2
                     + "ORDER BY pf.AD_Client_ID DESC, pf.IsDefault DESC";    // own first

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Client_ID );
            pstmt.setInt( 2,pi.getAD_PInstance_ID());

            ResultSet rs = pstmt.executeQuery();

            // Just get first

            if( rs.next()) {
                AD_ReportView_ID = rs.getInt( 1 );    // required
                whereClause      = rs.getString( 2 );

                if( rs.wasNull()) {
                    whereClause = "";
                }

                //

                AD_Table_ID       = rs.getInt( 3 );
                TableName         = rs.getString( 4 );                 // required for query
                AD_PrintFormat_ID = rs.getInt( 5 );                    // required
                IsForm            = "Y".equals( rs.getString( 6 ));    // required
                Client_ID         = rs.getInt( 7 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e1 ) {
            log.log( Level.SEVERE,"get(1)",e1 );
        }

        // Nothing found

        if( AD_ReportView_ID == 0 ) {

            // Check Print format in Report Directly

            sql = "SELECT t.AD_Table_ID,t.TableName, pf.AD_PrintFormat_ID, pf.IsForm " + "FROM AD_PInstance pi" + " INNER JOIN AD_Process p ON (pi.AD_Process_ID=p.AD_Process_ID)" + " INNER JOIN AD_PrintFormat pf ON (p.AD_PrintFormat_ID=pf.AD_PrintFormat_ID)" + " INNER JOIN AD_Table t ON (pf.AD_Table_ID=t.AD_Table_ID) " + "WHERE pi.AD_PInstance_ID=?";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,pi.getAD_PInstance_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    whereClause       = "";
                    AD_Table_ID       = rs.getInt( 1 );
                    TableName         = rs.getString( 2 );                 // required for query
                    AD_PrintFormat_ID = rs.getInt( 3 );                    // required
                    IsForm            = "Y".equals( rs.getString( 4 ));    // required
                    Client_ID         = AD_Client_ID;
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e1 ) {
                log.log( Level.SEVERE,"get(2)",e1 );
            }

            if( AD_PrintFormat_ID == 0 ) {
                log.log( Level.SEVERE,"Report Info NOT found AD_PInstance_ID=" + pi.getAD_PInstance_ID() + ",AD_Client_ID=" + AD_Client_ID );

                return null;
            }
        }

        // Create Query from Parameters

        MQuery query = null;

        if( IsForm && (pi.getRecord_ID() != 0) ) {    // Form = one record
            query = MQuery.getEqualQuery( TableName + "_ID",pi.getRecord_ID());
        } else {
            query = MQuery.get( ctx,pi.getAD_PInstance_ID(),TableName );
        }

        // Add to static where clause from ReportView

        if( whereClause.length() != 0 ) {
            query.addRestriction( whereClause );
        }

        // Get PrintFormat

        MPrintFormat format = null;

        if( AD_PrintFormat_ID != 0 ) {

            // We have a PrintFormat with the correct Client

            if( Client_ID == AD_Client_ID ) {
                format = MPrintFormat.get( ctx,AD_PrintFormat_ID,false );
            } else {
                format = MPrintFormat.copyToClient( ctx,AD_PrintFormat_ID,AD_Client_ID );
            }
        }

        if( (format != null) && (format.getItemCount() == 0) ) {
            log.info( "No Items - recreating:  " + format );
            format.delete( true );
            format = null;
        }

        // Create it

        if( (format == null) && (AD_ReportView_ID != 0) ) {
            format = MPrintFormat.createFromReportView( ctx,AD_ReportView_ID,pi.getTitle());
        }

        if( format == null ) {
            return null;
        }

        //

        PrintInfo info = new PrintInfo( pi );

        info.setAD_Table_ID( AD_Table_ID );

        return new ReportDesign( ctx,format,query,info );
    }    // get

    /** Descripción de Campos */

    public static final int ORDER = 0;

    /** Descripción de Campos */

    public static final int SHIPMENT = 1;

    /** Descripción de Campos */

    public static final int INVOICE = 2;

    /** Descripción de Campos */

    public static final int PROJECT = 3;

    /** Descripción de Campos */

    public static final int RFQ = 4;

    //

    /** Descripción de Campos */

    public static final int REMITTANCE = 5;

    /** Descripción de Campos */

    public static final int CHECK = 6;

    /** Descripción de Campos */

    public static final int DUNNING = 7;

    /** Descripción de Campos */

    private static final String[] DOC_TABLES = new String[] {
        "C_Order_Header_v","M_InOut_Header_v","C_Invoice_Header_v","C_Project_Header_v","C_RfQResponse_v","C_PaySelection_Check_v","C_PaySelection_Check_v","C_DunningRunEntry_v"
    };

    /** Descripción de Campos */

    private static final String[] DOC_BASETABLES = new String[] {
        "C_Order","M_InOut","C_Invoice","C_Project","C_RfQResponse","C_PaySelectionCheck","C_PaySelectionCheck","C_DunningRunEntry"
    };

    /** Descripción de Campos */

    private static final String[] DOC_IDS = new String[] {
        "C_Order_ID","M_InOut_ID","C_Invoice_ID","C_Project_ID","C_RfQResponse_ID","C_PaySelectionCheck_ID","C_PaySelectionCheck_ID","C_DunningRunEntry_ID"
    };

    /** Descripción de Campos */

    private static final int[] DOC_TABLE_ID = new int[] {
        X_C_Order.Table_ID,X_M_InOut.Table_ID,X_C_Invoice.Table_ID,X_C_Project.Table_ID,X_C_RfQResponse.Table_ID,X_C_PaySelectionCheck.Table_ID,X_C_PaySelectionCheck.Table_ID,X_C_DunningRunEntry.Table_ID
    };

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param type
     * @param Record_ID
     *
     * @return
     */

    public static ReportDesign get( Properties ctx,int type,int Record_ID ) {

        // Order - Print Shipment or Invoice

        if( type == ORDER ) {
            int[] what = getDocumentWhat( Record_ID );

            if( what != null ) {
                type      = what[ 0 ];
                Record_ID = what[ 1 ];
            }
        }    // Order

        //

        String JobName           = DOC_BASETABLES[ type ] + "_Print";
        int    AD_PrintFormat_ID = 0;
        int    C_BPartner_ID     = 0;
        String DocumentNo        = null;
        int    copies            = 1;

        // Language

        MClient  client   = MClient.get( ctx );
        Language language = client.getLanguage();

        // Get Document Info

        String sql = null;

        if( type == CHECK ) {
            sql = "SELECT bad.Check_PrintFormat_ID,"                                                 // 1
                  + "     c.IsMultiLingualDocument,bp.AD_Language,bp.C_BPartner_ID,d.DocumentNo "    // 2..5
                  + "FROM C_PaySelectionCheck d" + " INNER JOIN C_PaySelection ps ON (d.C_PaySelection_ID=ps.C_PaySelection_ID)" + " INNER JOIN C_BankAccountDoc bad ON (ps.C_BankAccount_ID=bad.C_BankAccount_ID AND d.PaymentRule=bad.PaymentRule)" + " INNER JOIN AD_Client c ON (d.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN C_BPartner bp ON (d.C_BPartner_ID=bp.C_BPartner_ID) " + "WHERE d.C_PaySelectionCheck_ID=?";    // info from BankAccount
        } else if( type == DUNNING ) {
            sql = "SELECT dl.Dunning_PrintFormat_ID," + " c.IsMultiLingualDocument,bp.AD_Language,bp.C_BPartner_ID,dr.DunningDate " + "FROM C_DunningRunEntry d" + " INNER JOIN AD_Client c ON (d.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN C_BPartner bp ON (d.C_BPartner_ID=bp.C_BPartner_ID)" + " INNER JOIN C_DunningRun dr ON (d.C_DunningRun_ID=dr.C_DunningRun_ID)" + " INNER JOIN C_DunningLevel dl ON (dr.C_DunningLevel_ID=dr.C_DunningLevel_ID) " + "WHERE d.C_DunningRunEntry_ID=?";    // info from Dunning
        } else if( type == REMITTANCE ) {
            sql = "SELECT pf.Remittance_PrintFormat_ID," + " c.IsMultiLingualDocument,bp.AD_Language,bp.C_BPartner_ID,d.DocumentNo " + "FROM C_PaySelectionCheck d" + " INNER JOIN AD_Client c ON (d.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN AD_PrintForm pf ON (c.AD_Client_ID=pf.AD_Client_ID)" + " INNER JOIN C_BPartner bp ON (d.C_BPartner_ID=bp.C_BPartner_ID) " + "WHERE d.C_PaySelectionCheck_ID=?"    // info from PrintForm
                  + " AND pf.AD_Org_ID IN (0,d.AD_Org_ID) ORDER BY pf.AD_Org_ID DESC";
        } else if( type == PROJECT ) {
            sql = "SELECT pf.Project_PrintFormat_ID," + " c.IsMultiLingualDocument,bp.AD_Language,bp.C_BPartner_ID,d.Value " + "FROM C_Project d" + " INNER JOIN AD_Client c ON (d.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN AD_PrintForm pf ON (c.AD_Client_ID=pf.AD_Client_ID)" + " LEFT OUTER JOIN C_BPartner bp ON (d.C_BPartner_ID=bp.C_BPartner_ID) " + "WHERE d.C_Project_ID=?"    // info from PrintForm
                  + " AND pf.AD_Org_ID IN (0,d.AD_Org_ID) ORDER BY pf.AD_Org_ID DESC";
        } else if( type == RFQ ) {
            sql = "SELECT COALESCE(t.AD_PrintFormat_ID, pf.AD_PrintFormat_ID)," + " c.IsMultiLingualDocument,bp.AD_Language,bp.C_BPartner_ID,rr.Name " + "FROM C_RfQResponse rr" + " INNER JOIN C_RfQ r ON (rr.C_RfQ_ID=r.C_RfQ_ID)" + " INNER JOIN C_RfQ_Topic t ON (r.C_RfQ_Topic_ID=t.C_RfQ_Topic_ID)" + " INNER JOIN AD_Client c ON (rr.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN C_BPartner bp ON (rr.C_BPartner_ID=bp.C_BPartner_ID)," + " AD_PrintFormat pf " + "WHERE pf.AD_Client_ID IN (0,rr.AD_Client_ID)" + " AND pf.AD_Table_ID=725 AND pf.IsTableBased='N'"    // from RfQ PrintFormat
                  + " AND rr.C_RfQResponse_ID=? "    // Info from RfQTopic
                  + "ORDER BY t.AD_PrintFormat_ID, pf.AD_Client_ID DESC, pf.AD_Org_ID DESC";
        } else {                                                                  // Get PrintFormat from Org or 0 of document client
            sql = "SELECT pf.Order_PrintFormat_ID,pf.Shipment_PrintFormat_ID,"    // 1..2

            // Prio: 1. BPartner 2. DocType, 3. PrintFormat (Org)      //      see InvoicePrint

            + " COALESCE (bp.Invoice_PrintFormat_ID,dt.AD_PrintFormat_ID,pf.Invoice_PrintFormat_ID),"    // 3
            + " pf.Project_PrintFormat_ID, pf.Remittance_PrintFormat_ID,"    // 4..5
            + " c.IsMultiLingualDocument, bp.AD_Language,"                        // 6..7
            + " COALESCE(dt.DocumentCopies,0)+COALESCE(bp.DocumentCopies,1), "    // 8
            + " dt.AD_PrintFormat_ID,bp.C_BPartner_ID,d.DocumentNo "                                                                                                                                                                                                                                                                                                         // 9..11
            + "FROM " + DOC_BASETABLES[ type ] + " d" + " INNER JOIN AD_Client c ON (d.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN AD_PrintForm pf ON (c.AD_Client_ID=pf.AD_Client_ID)" + " INNER JOIN C_BPartner bp ON (d.C_BPartner_ID=bp.C_BPartner_ID)" + " LEFT OUTER JOIN C_DocType dt ON (d.C_DocType_ID=dt.C_DocType_ID) " + "WHERE d." + DOC_IDS[ type ] + "=?"    // info from PrintForm
            + " AND pf.AD_Org_ID IN (0,d.AD_Org_ID) " + "ORDER BY pf.AD_Org_ID DESC";
        }

        //

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,Record_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next())    // first record only
            {
                if( (type == CHECK) || (type == DUNNING) || (type == REMITTANCE) || (type == PROJECT) || (type == RFQ) ) {
                    AD_PrintFormat_ID = rs.getInt( 1 );
                    copies            = 1;

                    // Set Language when enabled

                    String AD_Language = rs.getString( 3 );

                    if( (AD_Language != null) && "Y".equals( rs.getString( 2 ))) {    // IsMultiLingualDocument
                        language = Language.getLanguage( AD_Language );
                    }

                    C_BPartner_ID = rs.getInt( 4 );

                    if( type == DUNNING ) {
                        Timestamp ts = rs.getTimestamp( 5 );

                        DocumentNo = ts.toString();
                    } else {
                        DocumentNo = rs.getString( 5 );
                    }
                } else {

                    // Set PrintFormat

                    AD_PrintFormat_ID = rs.getInt( type + 1 );

                    if( rs.getInt( 9 ) != 0 ) {    // C_DocType.AD_PrintFormat_ID
                        AD_PrintFormat_ID = rs.getInt( 9 );
                    }

                    copies = rs.getInt( 8 );

                    // Set Language when enabled

                    String AD_Language = rs.getString( 7 );

                    if( (AD_Language != null) && "Y".equals( rs.getString( 6 ))) {    // IsMultiLingualDocument
                        language = Language.getLanguage( AD_Language );
                    }

                    C_BPartner_ID = rs.getInt( 10 );
                    DocumentNo    = rs.getString( 11 );
                }
            }

            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Record_ID=" + Record_ID + ", SQL=" + sql,e );
        }

        if( AD_PrintFormat_ID == 0 ) {
            log.log( Level.SEVERE,"No PrintFormat found for Type=" + type + ", Record_ID=" + Record_ID );

            return null;
        }

        // Get Format & Data

        MPrintFormat format = MPrintFormat.get( ctx,AD_PrintFormat_ID,false );

        format.setLanguage( language );    // BP Language if Multi-Lingual

        // if (!Env.isBaseLanguage(language, DOC_TABLES[type]))

        format.setTranslationLanguage( language );

        // query

        MQuery query = new MQuery( DOC_TABLES[ type ] );

        query.addRestriction( DOC_IDS[ type ],MQuery.EQUAL,new Integer( Record_ID ));

        // log.config( "ReportCtrl.startDocumentPrint - " + format, query + " - " + language.getAD_Language());
        //

        if( (DocumentNo == null) || (DocumentNo.length() == 0) ) {
            DocumentNo = "DocPrint";
        }

        PrintInfo info = new PrintInfo( DocumentNo,DOC_TABLE_ID[ type ],Record_ID,C_BPartner_ID );

        info.setCopies( copies );
        info.setDocumentCopy( false );    // true prints "Copy" on second
        info.setPrinterName( format.getPrinterName());

        // Engine

        ReportDesign rd = new ReportDesign( ctx,format,query,info );

        rd.setCopies( copies );

        return rd;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param C_Order_ID
     *
     * @return
     */

    private static int[] getDocumentWhat( int C_Order_ID ) {
        int[] what = new int[ 2 ];

        what[ 0 ] = ORDER;
        what[ 1 ] = C_Order_ID;

        //

        String SQL = "SELECT dt.DocSubTypeSO " + "FROM C_DocType dt, C_Order o " + "WHERE o.C_DocType_ID=dt.C_DocType_ID" + " AND o.C_Order_ID=?";
        String DocSubTypeSO = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_Order_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                DocSubTypeSO = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e1 ) {
            log.log( Level.SEVERE,"DocSubType",e1 );

            return null;    // error
        }

        if( DocSubTypeSO == null ) {
            DocSubTypeSO = "";
        }

        // WalkIn Receipt, WalkIn Invoice,

        if( DocSubTypeSO.equals( "WR" ) || DocSubTypeSO.equals( "WI" )) {
            what[ 0 ] = INVOICE;

            // WalkIn Pickup,

        } else if( DocSubTypeSO.equals( "WP" )) {
            what[ 0 ] = SHIPMENT;

            // Offer Binding, Offer Nonbinding, Standard Order

        } else {
            return what;
        }

        // Get Record_ID of Invoice/Receipt

        if( what[ 0 ] == INVOICE ) {
            SQL = "SELECT C_Invoice_ID REC FROM C_Invoice WHERE C_Order_ID=?"    // 1
                  + " ORDER BY C_Invoice_ID DESC";
        } else {
            SQL = "SELECT M_InOut_ID REC FROM M_InOut WHERE C_Order_ID=?"    // 1
                  + " ORDER BY M_InOut_ID DESC";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_Order_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // if (i == 1 && ADialog.ask(0, null, what[0] == INVOICE ? "PrintOnlyRecentInvoice?" : "PrintOnlyRecentShipment?")) break;

                what[ 1 ] = rs.getInt( 1 );
            } else {    // No Document Found
                what[ 0 ] = ORDER;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e2 ) {
            log.log( Level.SEVERE,"Record_ID",e2 );

            return null;
        }

        log.fine( "Order => " + what[ 0 ] + " ID=" + what[ 1 ] );

        return what;
    }    // getDocumentWhat

    /**
     * Descripción de Método
     *
     *
     * @param type
     * @param Record_ID
     */

    public static void printConfirm( int type,int Record_ID ) {
        StringBuffer sql = new StringBuffer();

        if( (type == ORDER) || (type == SHIPMENT) || (type == INVOICE) ) {
            sql.append( "UPDATE " ).append( DOC_BASETABLES[ type ] ).append( " SET DatePrinted=SysDate, IsPrinted='Y' WHERE " ).append( DOC_IDS[ type ] ).append( "=" ).append( Record_ID );
        }

        //

        if( sql.length() > 0 ) {
            int no = DB.executeUpdate( sql.toString());

            if( no != 1 ) {
                log.log( Level.SEVERE,"Updated records=" + no + " - should be just one" );
            }
        }
    }    // printConfirm

    /**
     * Descripción de Método
     *
     *
     * @param writer
     * @param language
     * @param prefijo
     * @param valor
     * @param sufijo
     */

    public void createLabelFile( Writer writer,Language language,String prefijo,String valor,String sufijo ) {
        for( int row = 0;row < m_printData.getRowCount();row++ ) {
            m_printData.setRowIndex( row );

            for( int col = 0;col < m_printFormat.getItemCount();col++ ) {
                StringBuffer     sb   = new StringBuffer();
                MPrintFormatItem item = m_printFormat.getItem( col );

                if( item.isPrinted()) {
                    sb.append( prefijo );
                    sb.append( valor );
                    sb.append( item.getPrintNameSuffix());

                    Object obj = m_printData.getNode( new Integer( item.getAD_Column_ID()));
                    String data = "";

                    if( obj != null ) {
                        if( obj instanceof PrintDataElement ) {
                            data = (( PrintDataElement )obj ).getValueDisplay( language );    // formatted
                            sb.append( data );
                            sb.append( sufijo );
                            guardarLinea( writer,sb );
                        } else if( !( obj instanceof PrintData )) {
                            log.log( Level.SEVERE,"createLabelFile - Element not PrintData(Element) " + obj.getClass());
                        }
                    }
                }    // printed
            }        // for all columns
        }
    }                // createLabelFile

    /**
     * Descripción de Método
     *
     *
     * @param writer
     * @param linea
     */

    private void guardarLinea( Writer writer,StringBuffer linea ) {
        try {
            if( linea != null ) {
                if( linea.length() > 0 ) {
                    writer.write( linea.toString());
                    writer.write( Env.NL );
                }
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createLabelFile - writer",e );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param text
     * @param language
     * @param prefijo
     * @param valor
     * @param sufijo
     */

    public void printLabelDirect( StringBuffer text,Language language,String prefijo,String valor,String sufijo ) {
        for( int row = 0;row < m_printData.getRowCount();row++ ) {
            m_printData.setRowIndex( row );

            for( int col = 0;col < m_printFormat.getItemCount();col++ ) {
                StringBuffer     sb   = new StringBuffer();
                MPrintFormatItem item = m_printFormat.getItem( col );

                if( item.isPrinted()) {
                    sb.append( prefijo );
                    sb.append( valor );
                    sb.append( item.getPrintNameSuffix());

                    Object obj = m_printData.getNode( new Integer( item.getAD_Column_ID()));
                    String data = "";

                    if( obj != null ) {
                        if( obj instanceof PrintDataElement ) {
                            data = (( PrintDataElement )obj ).getValueDisplay( language );    // formatted
                            sb.append( data );
                            sb.append( sufijo );
                            sb.append( "\n" );
                            text.append( sb.toString());
                        } else if( !( obj instanceof PrintData )) {
                            log.log( Level.SEVERE,"printLabelDirect - Element not PrintData(Element) " + obj.getClass());
                        }
                    }
                }    // printed
            }        // for all columns
        }
    }                // printLabelDirect

    /**
     * Descripción de Método
     *
     *
     * @param writer
     * @param language
     * @param delimiter
     * @param header
     * @param printAll
     */

    public void createFileText( Writer writer,Language language,String delimiter,boolean header,boolean printAll ) {
        int begin = 0;

        if( header ) {
            begin = -1;
        }

        for( int row = begin;row < m_printData.getRowCount();row++ ) {
            if( row != -1 ) {
                m_printData.setRowIndex( row );
            }

            boolean first = true;

            for( int col = 0;col < m_printFormat.getItemCount();col++ ) {
                StringBuffer     sb   = new StringBuffer();
                MPrintFormatItem item = m_printFormat.getItem( col );

                if( item.isPrinted()) {

                    // column delimiter (comma or tab)

                    if( first ) {
                        first = false;
                    } else {
                        sb.append( delimiter );
                    }

                    // header row

                    if( row == -1 ) {
                        if( header ) {
                            guardarLineaTexto( writer,sb.append( m_printFormat.getItem( col ).getPrintName( language )).toString());
                        }
                    } else {
                        if( item.isTypeField() || printAll ) {
                            Object obj = m_printData.getNode( new Integer( item.getAD_Column_ID()));
                            String data = "";

                            if( obj != null ) {
                                if( obj instanceof PrintDataElement ) {
                                    data = (( PrintDataElement )obj ).getValueDisplay( language );    // formatted
                                    sb.append( data );
                                } else if( !( obj instanceof PrintData )) {
                                    log.log( Level.SEVERE,"createLabelFile - Element not PrintData(Element) " + obj.getClass());
                                }
                            }
                        }

                        guardarLineaTexto( writer,sb.toString());
                    }
                }    // printed
            }

            guardarLineaTexto( writer,Env.NL );
        }
    }                // createLabelFile

    /**
     * Descripción de Método
     *
     *
     * @param writer
     * @param linea
     */

    private void guardarLineaTexto( Writer writer,String linea ) {
        try {
            if( linea != null ) {
                if( linea.length() > 0 ) {
                    writer.write( linea );
                }
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createIntrastatFile - writer",e );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        org.openXpertya.OpenXpertya.startupEnvironment( true );

        //

        int    AD_Table_ID = 100;
        MQuery q           = new MQuery( "AD_Table" );

        q.addRestriction( "AD_Table_ID","<",108 );

        //

        MPrintFormat f = MPrintFormat.createFromTable( Env.getCtx(),AD_Table_ID );
        PrintInfo i = new PrintInfo( "test",AD_Table_ID,108,0 );

        i.setAD_Table_ID( AD_Table_ID );

        ReportDesign rd = new ReportDesign( Env.getCtx(),f,q,i );

        rd.layout();

        // rd.print();
        // rd.print(true, 1, false, "Epson Stylus COLOR 900 ESC/P 2");             //      Dialog
        // rd.print(true, 1, false, "HP LaserJet 3300 Series PCL 6");              //      Dialog
        // rd.print(false, 1, false, "Epson Stylus COLOR 900 ESC/P 2");    //      Dialog

        System.exit( 0 );
    }

    /**
     * Descripción de Método
     *
     *
     * @param psae
     */

    public void attributeUpdate( PrintServiceAttributeEvent psae ) {
        log.fine( "attributeUpdate - " + psae );

        // PrintUtil.dump (psae.getAttributes());

    }
}    // ReportDesign



/*
 *  @(#)ReportDesign.java   02.07.07
 * 
 *  Fin del fichero ReportDesign.java
 *  
 *  Versión 2.2
 *
 */
