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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MEnvio;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MPackage;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.print.MPrintFormat;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrintLabel extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public PrintLabel() {
        super();
    }    // PrintLabel

    /*
     *  (sin Javadoc)
     * @see org.openXpertya.process.SvrProcess#prepare()
     */

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
            } else if( name.equals( "PrintLabel" )) {
                m_ad_print_label_id = para[ i ].getParameterAsInt();
            } else if( name.equals( "NoCopias" )) {
                nCopias = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        envio = new MEnvio( getCtx(),getRecord_ID(),null );
    }

    /** Descripción de Campos */

    private MEnvio envio;

    /** Descripción de Campos */

    private int nCopias = 1;

    /** Descripción de Campos */

    private int m_ad_print_label_id;

    /*
     *  (sin Javadoc)
     * @see org.openXpertya.process.SvrProcess#doIt()
     */

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        createLabelFile();
        printLabelDirect();

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean createLabelFile() {

        // Creamos el archivo

        File outFile = new File( "Etiqueta.txt" );

        try {
            outFile.createNewFile();
        } catch( IOException e ) {
            log.log( Level.SEVERE,"PrintLabel - PrintLabel create file",e );
        }

        Writer writer = null;

        try {
            FileWriter fw = new FileWriter( outFile,false );

            writer = new BufferedWriter( fw );
        } catch( FileNotFoundException fnfe ) {
            log.log( Level.SEVERE,"createLabelFile - " + fnfe.toString());
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createLabelFile - ",e );

            return false;
        }

        StringBuffer sql = new StringBuffer( "SELECT l.LabelFormatType, f.FunctionPrefix, f.FunctionSuffix," + " l.PrintName, l.AD_PrintFormat_ID" + " FROM AD_PrintLabelLine l " + " LEFT OUTER JOIN AD_LabelPrinterFunction f ON (f.AD_LabelPrinterFunction_ID=l.AD_LabelPrinterFunction_ID)" + " WHERE l.AD_PrintLabel_ID=?" + " ORDER BY l.SeqNo ASC" );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            pstmt.setInt( 1,m_ad_print_label_id );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                StringBuffer linea   = new StringBuffer();
                String       prefijo = rs.getString( 2 );

                if( prefijo == null ) {
                    prefijo = new String( "" );
                }

                String sufijo = rs.getString( 3 );

                if( sufijo == null ) {
                    sufijo = new String( "" );
                }

                String valor = rs.getString( 4 );

                if( valor == null ) {
                    valor = new String( "" );
                }

                String tipoDato = rs.getString( 1 );

                // comando de configuracion de la impresora

                if(( tipoDato.compareTo( "F" ) == 0 ) || ( tipoDato.compareTo( "T" ) == 0 )) {
                    linea.append( prefijo ).append( valor ).append( sufijo );

                    // Formato de Impresion

                } else if( tipoDato.compareTo( "I" ) == 0 ) {
                    MPrintFormat pf = new MPrintFormat( getCtx(),rs.getInt( 5 ),null );
                    MQuery    query = createQuery( pf );
                    PrintInfo info  = new PrintInfo( pf.getName(),pf.getAD_Table_ID(),0 );

                    info.setDescription( query.getInfo());

                    ReportEngine re = new ReportEngine( getCtx(),pf,query,null );

                    re.createLabelFile( writer,pf.getLanguage(),prefijo,valor,sufijo );
                }

                // Indicar Cantidad de paquetes

                else if(( tipoDato.compareTo( "Q" ) == 0 ) || ( tipoDato.compareTo( "L" ) == 0 )) {
                    linea.append( prefijo ).append( valor ).append( nCopias ).append( sufijo );

                    // tipo desconocido

                } else {
                    linea.append( prefijo ).append( valor ).append( sufijo );
                    log.log( Level.SEVERE,"createLabelFile - Tipo de Funcion desconocida" );
                }

                guardarLinea( writer,linea );
            }

            //

            writer.flush();
            writer.close();
            rs.close();
            pstmt.close();
        } catch( SQLException e2 ) {
            log.log( Level.SEVERE,"createLabelFile - Header",e2 );

            return false;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createLabelFile - writer",e );

            return false;
        }

        return true;
    }    // createCSV

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean printLabelDirect() {
        StringBuffer texto   = new StringBuffer( "" );
        String       puerto  = null;
        String       printer = null;
        StringBuffer sql     = new StringBuffer( "SELECT l.LabelFormatType, f.FunctionPrefix, f.FunctionSuffix," + " l.PrintName, l.AD_PrintFormat_ID, p.Port, p.Name" + " FROM AD_PrintLabelLine l " + " LEFT OUTER JOIN AD_LabelPrinterFunction f ON (f.AD_LabelPrinterFunction_ID=l.AD_LabelPrinterFunction_ID)" + " LEFT OUTER JOIN AD_LabelPrinter p ON (f.AD_LabelPrinter_ID=p.AD_LabelPrinter_ID)" + " WHERE l.AD_PrintLabel_ID=?" + " ORDER BY l.SeqNo ASC" );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            pstmt.setInt( 1,m_ad_print_label_id );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                if( puerto == null ) {
                    puerto = rs.getString( 6 );
                }

                if( printer == null ) {
                    printer = rs.getString( 7 );
                }

                String prefijo = rs.getString( 2 );

                if( prefijo == null ) {
                    prefijo = new String( "" );
                }

                String sufijo = rs.getString( 3 );

                if( sufijo == null ) {
                    sufijo = new String( "" );
                }

                String valor = rs.getString( 4 );

                if( valor == null ) {
                    valor = new String( "" );
                }

                String tipoDato = rs.getString( 1 );

                // comando de configuracion de la impresora o texto

                if(( tipoDato.compareTo( "F" ) == 0 ) || ( tipoDato.compareTo( "T" ) == 0 )) {
                    texto.append( prefijo ).append( valor ).append( sufijo ).append( "\n" );

                    // Formato de Impresion

                } else if( tipoDato.compareTo( "I" ) == 0 ) {
                    MPrintFormat pf = new MPrintFormat( getCtx(),rs.getInt( 5 ),null );
                    MQuery    query = createQuery( pf );
                    PrintInfo info  = new PrintInfo( pf.getName(),pf.getAD_Table_ID(),0 );

                    info.setDescription( query.getInfo());

                    ReportEngine re = new ReportEngine( getCtx(),pf,query,null );

                    re.printLabelDirect( texto,pf.getLanguage(),prefijo,valor,sufijo );
                }

                // Indicar Cantidad de paquetes

                else if(( tipoDato.compareTo( "Q" ) == 0 ) || ( tipoDato.compareTo( "L" ) == 0 )) {
                    texto.append( prefijo ).append( valor ).append( nCopias ).append( sufijo ).append( "\n" );

                    // tipo desconocido

                } else {
                    texto.append( prefijo ).append( valor ).append( sufijo ).append( "\n" );
                    log.log( Level.SEVERE,"printLableDirect - Tipo de Funcion desconocida" );
                }
            }

            // *************************************************************

            if( puerto != null ) {
                FileOutputStream fos = new FileOutputStream( puerto );
                PrintStream      prs = new PrintStream( fos );

                prs.print( texto.toString());
                prs.close();
                fos.close();
            }

            // *************************************************************

            else {
                PrintService   labelPrinter = null;
                PrintService[] services     = PrintServiceLookup.lookupPrintServices( null,null );

                for( int i = 0;i < services.length;i++ ) {
                    if( services[ i ].getName().equalsIgnoreCase( printer )) {
                        labelPrinter = services[ i ];
                    }
                }

                if( labelPrinter != null ) {
                    DocFlavor   flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                    DocPrintJob pj     = labelPrinter.createPrintJob();
                    Doc         doc    = new SimpleDoc( texto.toString().getBytes(),flavor,null );

                    try {
                        pj.print( doc,null );
                    } catch( PrintException e ) {
                        System.out.println( "Error al imprimir: 1 " + e.getMessage());
                    }

                    flavor = DocFlavor.CHAR_ARRAY.TEXT_PLAIN;
                    pj     = labelPrinter.createPrintJob();
                    doc    = new SimpleDoc( texto.toString().toCharArray(),flavor,null );

                    try {
                        pj.print( doc,null );
                    } catch( PrintException e ) {
                        System.out.println( "Error al imprimir: 2 " + e.getMessage());
                    }
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e2 ) {
            log.log( Level.SEVERE,"printLableDirect - Header",e2 );

            return false;
        } catch( Exception e ) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Descripción de Método
     *
     *
     * @param c_order_id
     * @param m_ticket_id
     *
     * @return
     */

    public static boolean printLabelTicket( int c_order_id,int m_ticket_id ) {
        StringBuffer texto   = new StringBuffer( "" );
        String       puerto  = null;
        String       printer = null;
        StringBuffer sql     = new StringBuffer( "SELECT l.LabelFormatType, f.FunctionPrefix, f.FunctionSuffix," + " l.PrintName, l.AD_PrintFormat_ID, p.Port, p.Name" + " FROM AD_PrintLabelLine l " + " LEFT OUTER JOIN AD_LabelPrinterFunction f ON (f.AD_LabelPrinterFunction_ID=l.AD_LabelPrinterFunction_ID)" + " LEFT OUTER JOIN AD_LabelPrinter p ON (f.AD_LabelPrinter_ID=p.AD_LabelPrinter_ID)" + " WHERE l.AD_PrintLabel_ID=?" + " ORDER BY l.SeqNo ASC" );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            pstmt.setInt( 1,m_ticket_id );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                if( puerto == null ) {
                    puerto = rs.getString( 6 );
                }

                if( printer == null ) {
                    printer = rs.getString( 7 );
                }

                String prefijo = rs.getString( 2 );

                if( prefijo == null ) {
                    prefijo = new String( "" );
                }

                String sufijo = rs.getString( 3 );

                if( sufijo == null ) {
                    sufijo = new String( "" );
                }

                String valor = rs.getString( 4 );

                if( valor == null ) {
                    valor = new String( "" );
                }

                String tipoDato = rs.getString( 1 );

                // comando de configuracion de la impresora o texto

                if(( tipoDato.compareTo( "F" ) == 0 ) || ( tipoDato.compareTo( "T" ) == 0 )) {
                    texto.append( prefijo ).append( valor ).append( sufijo ).append( "\n" );

                    // Formato de Impresion

                } else if( tipoDato.compareTo( "I" ) == 0 ) {
                    MPrintFormat pf = new MPrintFormat( Env.getCtx(),rs.getInt( 5 ),null );
                    MQuery query = new MQuery( MOrder.Table_Name );

                    query.addRestriction( "C_Order_ID",MQuery.EQUAL,c_order_id );

                    PrintInfo info = new PrintInfo( pf.getName(),pf.getAD_Table_ID(),0 );

                    info.setDescription( query.getInfo());

                    ReportEngine re = new ReportEngine( Env.getCtx(),pf,query,null );

                    re.createLabelString( texto,pf.getLanguage(),prefijo,valor,sufijo );
                }

                // tipo desconocido

                else {
                    texto.append( prefijo ).append( valor ).append( sufijo ).append( "\n" );

                    // log.log(Level.SEVERE, "printLabelTicket - Tipo de Funcion desconocida");

                }
            }

            // *************************************************************

            if( puerto != null ) {
                FileOutputStream fos = new FileOutputStream( puerto );
                PrintStream      prs = new PrintStream( fos );

                prs.print( texto.toString());
                prs.close();
                fos.close();
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e2 ) {

            // log.log(Level.SEVERE, "printLableDirect - Header", e2);

            return false;
        } catch( Exception e ) {
            e.printStackTrace();
        }

        return true;
    }

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
            log.log( Level.SEVERE,"PrintLabel - writer",e );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param pf
     *
     * @return
     */

    private MQuery createQuery( MPrintFormat pf ) {
        MQuery       query = null;
        StringBuffer sql   = new StringBuffer( "SELECT t.TableName " + "FROM AD_Table t " + "WHERE t.AD_Table_ID=" );

        sql.append( pf.getAD_Table_ID());

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());
            ResultSet         rs    = pstmt.executeQuery();

            //

            if( rs.next()) {
                query = new MQuery( rs.getString( 1 ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"PrintLabel.createQuery",e );
        }

        if( query != null ) {
            query.addRestriction( "AD_Client_ID",MQuery.EQUAL,envio.getAD_Client_ID());
            query.addRestriction( " AD_Org_ID IN (0, " + envio.getAD_Org_ID() + ") " );

            if( pf.getAD_Table_ID() == 1000013 ) {    // formato de etiqueta
                query.addRestriction( "M_Envio_ID",MQuery.EQUAL,envio.getM_Envio_ID());
            }

            if( (pf.getAD_Table_ID() == MEnvio.Table_ID) || (pf.getAD_Table_ID() == MPackage.Table_ID) ) {
                query.addRestriction( "M_Envio_ID",MQuery.EQUAL,envio.getM_Envio_ID());
            }

            if( (pf.getAD_Table_ID() == MInOut.Table_ID) || (pf.getAD_Table_ID() == MPackage.Table_ID) ) {
                sql = new StringBuffer( "SELECT p.M_InOut_ID " + "FROM M_Package p " + "INNER JOIN M_Envio e ON (e.M_Envio_ID=p.M_Envio_ID) " + "WHERE p.M_Envio_ID=" );
                sql.append( envio.getM_Envio_ID());

                try {
                    PreparedStatement pstmt = DB.prepareStatement( sql.toString());
                    ResultSet rs = pstmt.executeQuery();

                    //

                    StringBuffer sb = null;
                    int          i  = 0;

                    while( rs.next()) {
                        if( i == 0 ) {
                            sb = new StringBuffer( " M_InOut_ID" );
                            sb.append( " IN (" );
                        }

                        if( i > 0 ) {
                            sb.append( "," );
                        }

                        sb.append( rs.getInt( 1 ));
                        i++;
                    }

                    if( i > 0 ) {
                        sb.append( ") " );
                    }

                    query.addRestriction( sb.toString());
                    rs.close();
                    pstmt.close();
                } catch( SQLException e ) {
                    log.log( Level.SEVERE,"PrintLabel.createQuery",e );
                }
            }

            if( (pf.getAD_Table_ID() == MBPartner.Table_ID) || (pf.getAD_Table_ID() == MBPartnerLocation.Table_ID) ) {
                sql = new StringBuffer( "SELECT a.C_BPartner_ID " + "FROM M_InOut a " + "WHERE a.M_InOut_ID IN " + "(SELECT p.M_InOut_ID " + "FROM M_Package p " + "INNER JOIN M_Envio e ON (e.M_Envio_ID=p.M_Envio_ID) " + "WHERE p.M_Envio_ID=" );
                sql.append( envio.getM_Envio_ID()).append( ")" );

                try {
                    PreparedStatement pstmt = DB.prepareStatement( sql.toString());
                    ResultSet rs = pstmt.executeQuery();

                    //

                    if( rs.next()) {
                        query.addRestriction( "C_BPartner_ID",MQuery.EQUAL,rs.getInt( 1 ));
                    }

                    rs.close();
                    pstmt.close();
                } catch( SQLException e ) {
                    log.log( Level.SEVERE,"PrintLabel.createQuery",e );
                }
            }

            if( pf.getAD_Table_ID() == MBPartnerLocation.Table_ID ) {
                sql = new StringBuffer( "SELECT a.C_BPartnerLocation_ID " + "FROM M_InOut a " + "WHERE a.M_InOut_ID IN " + "(SELECT p.M_InOut_ID " + "FROM M_Package p " + "INNER JOIN M_Envio e ON (e.M_Envio_ID=p.M_Envio_ID) " + "WHERE p.M_Envio_ID=" );
                sql.append( envio.getM_Envio_ID()).append( ")" );

                try {
                    PreparedStatement pstmt = DB.prepareStatement( sql.toString());
                    ResultSet rs = pstmt.executeQuery();

                    //

                    if( rs.next()) {
                        query.addRestriction( "C_BPartnerLocation_ID",MQuery.EQUAL,rs.getInt( 1 ));
                    }

                    rs.close();
                    pstmt.close();
                } catch( SQLException e ) {
                    log.log( Level.SEVERE,"PrintLabel.createQuery",e );
                }
            }

            if( pf.getAD_Table_ID() == MLocation.Table_ID ) {
                sql = new StringBuffer( "SELECT l.C_Location_ID(SELECT a.C_BPartnerLocation_ID " + "FROM M_InOut a " + "WHERE a.M_InOut_ID IN " + "(SELECT p.M_InOut_ID " + "FROM M_Package p " + "INNER JOIN M_Envio e ON (e.M_Envio_ID=p.M_Envio_ID) " + "WHERE p.M_Envio_ID=" );
                sql.append( envio.getM_Envio_ID()).append( "))" );

                try {
                    PreparedStatement pstmt = DB.prepareStatement( sql.toString());
                    ResultSet rs = pstmt.executeQuery();

                    //

                    if( rs.next()) {
                        query.addRestriction( "C_BPartnerLocation_ID",MQuery.EQUAL,rs.getInt( 1 ));
                    }

                    rs.close();
                    pstmt.close();
                } catch( SQLException e ) {
                    log.log( Level.SEVERE,"PrintLabel.createQuery",e );
                }
            }
        }

        return query;
    }
}    // PrintLabel



/*
 *  @(#)PrintLabel.java   02.07.07
 * 
 *  Fin del fichero PrintLabel.java
 *  
 *  Versión 2.2
 *
 */
