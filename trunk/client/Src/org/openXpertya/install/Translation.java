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



package org.openXpertya.install;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MLanguage;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.Login;
import org.openXpertya.util.Msg;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 07.11.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Translation {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     */

    public Translation( Properties ctx ) {
        m_ctx = ctx;
    }    // Translation

    /** Descripción de Campos */

    public static final String DTD = "<!DOCTYPE openxpertyaTrl PUBLIC \"-//openxpertya, Inc.//DTD OpenXpertya Translation 1.0//EN\" \"http://www.openxpertya.org/dtd/openxpertyaTrl.dtd\">";

    /** Descripción de Campos */

    public static final String XML_TAG = "openxpertyaTrl";

    /** Descripción de Campos */

    public static final String XML_ATTRIBUTE_TABLE = "table";

    /** Descripción de Campos */

    public static final String XML_ATTRIBUTE_LANGUAGE = "language";

    /** Descripción de Campos */

    public static final String XML_ROW_TAG = "row";

    /** Descripción de Campos */

    public static final String XML_ROW_ATTRIBUTE_ID = "id";

    /** Descripción de Campos */

    public static final String XML_ROW_ATTRIBUTE_TRANSLATED = "trl";

    /** Descripción de Campos */

    public static final String XML_VALUE_TAG = "value";

    /** Descripción de Campos */

    public static final String XML_VALUE_ATTRIBUTE_COLUMN = "column";

    /** Descripción de Campos */

    public static final String XML_VALUE_ATTRIBUTE_ORIGINAL = "original";

    /** Descripción de Campos */

    private boolean m_IsCentrallyMaintained = false;

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private Properties m_ctx = null;

    /**
     * Descripción de Método
     *
     *
     * @param directory
     * @param AD_Client_ID
     * @param AD_Language
     * @param Trl_Table
     *
     * @return
     */

    public String importTrl( String directory,int AD_Client_ID,String AD_Language,String Trl_Table ) {
        String fileName = directory + File.separator + Trl_Table + "_" + AD_Language + ".xml";

        System.out.println("");
        System.out.print( " Importing XML for table: " + Trl_Table);
        
        log.info( fileName );

        File in = new File( fileName );

        if( !in.exists()) {
            String msg = "El fichero no existe: " + fileName;

            log.log( Level.SEVERE,msg );

            return msg;
        }

        try {
            TranslationHandler handler = new TranslationHandler( AD_Client_ID );
            SAXParserFactory factory = SAXParserFactory.newInstance();

            //factory.setValidating(true);

            SAXParser parser = factory.newSAXParser();

            parser.parse( in,handler );
            System.out.println("");
            log.info( "Actualizado=" + handler.getUpdateCount());

            return Msg.getMsg( m_ctx,"Actualizado" ) + "=" + handler.getUpdateCount();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"importTrl",e );

            return e.toString();
        }
    }    // importTrl

    /**
     * Descripción de Método
     *
     *
     * @param directory
     * @param AD_Client_ID
     * @param AD_Language
     * @param Trl_Table
     *
     * @return
     */

    public String exportTrl( String directory,int AD_Client_ID,String AD_Language,String Trl_Table ) {
    	String fileName = directory + File.separator + Trl_Table + "_" + AD_Language + ".xml";


        log.info( "exportTrl - " + fileName + " "+ AD_Language);

        File    out            = new File( fileName );
        boolean isBaseLanguage = Language.isBaseLanguage( AD_Language );
        String  tableName      = Trl_Table;
        int     pos            = tableName.indexOf( "_Trl" );
        String  Base_Table     = Trl_Table.substring( 0,pos );

        if( isBaseLanguage ) {
            tableName = Base_Table;
        }

        String   keyColumn  = Base_Table + "_ID";
        String[] trlColumns = getTrlColumns( Base_Table );

        //

        StringBuffer sql = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // System.out.println(factory.getClass().getName());

            DocumentBuilder builder  = factory.newDocumentBuilder();
            Document        document = builder.newDocument();

            document.appendChild( document.createComment( OpenXpertya.getSummaryAscii()));
            document.appendChild( document.createComment( DTD ));

            // Root

            Element root = document.createElement( XML_TAG );

            root.setAttribute( XML_ATTRIBUTE_LANGUAGE,AD_Language );
            root.setAttribute( XML_ATTRIBUTE_TABLE,Base_Table );
            document.appendChild( root );

            //

            sql = new StringBuffer( "SELECT " );

            if( isBaseLanguage ) {
                sql.append( "'Y'," );                  // 1
            } else {
                sql.append( "t.IsTranslated," );
            }

            sql.append( "t." ).append( keyColumn );    // 2

            //

            for( int i = 0;i < trlColumns.length;i++ ) {
                sql.append( ", t." ).append( trlColumns[ i ] ).append( ",o." ).append( trlColumns[ i ] ).append( " AS " ).append( trlColumns[ i ] ).append( "O" );
            }

            //

            sql.append( " FROM " ).append( tableName ).append( " t" ).append( " INNER JOIN " ).append( Base_Table ).append( " o ON (t." ).append( keyColumn ).append( "=o." ).append( keyColumn ).append( ")" );

            boolean haveWhere = false;

            if( !isBaseLanguage ) {
                sql.append( " WHERE t.AD_Language=?" );
                haveWhere = true;
            }
            //Anulado por ConSerTi.
            //if( m_IsCentrallyMaintained ) {
            //    sql.append( haveWhere
            //                ?" AND "
            //                :" WHERE " ).append( "o.IsCentrallyMaintained='N'" );
            //    haveWhere = true;
            //}
            //Fin anulacion

            if( AD_Client_ID >= 0 ) {
                sql.append( haveWhere
                            ?" AND "
                            :" WHERE " ).append( "o.AD_Client_ID=" ).append( AD_Client_ID );
            }

            sql.append( " ORDER BY t." ).append( keyColumn );

            //

            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            if( !isBaseLanguage ) {
                pstmt.setString( 1,AD_Language );
            }
            //Modificado por ConSerTi. Y si es,tambien.
            pstmt.setString( 1,AD_Language );

            ResultSet rs   = pstmt.executeQuery();
            int       rows = 0;

            while( rs.next()) {
                Element row = document.createElement( XML_ROW_TAG );

                row.setAttribute( XML_ROW_ATTRIBUTE_ID,String.valueOf( rs.getInt( 2 )));    // KeyColumn
                row.setAttribute( XML_ROW_ATTRIBUTE_TRANSLATED,rs.getString( 1 ));    // IsTranslated

                for( int i = 0;i < trlColumns.length;i++ ) {
                    Element value = document.createElement( XML_VALUE_TAG );

                    value.setAttribute( XML_VALUE_ATTRIBUTE_COLUMN,trlColumns[ i ] );

                    String origString = rs.getString( trlColumns[ i ] + "O" );    // Original Value

                    if( origString == null ) {
                        origString = "";
                    }

                    String valueString = rs.getString( trlColumns[ i ] );    // Value

                    if( valueString == null ) {
                        valueString = "";
                    }

                    value.setAttribute( XML_VALUE_ATTRIBUTE_ORIGINAL,origString );
                    value.appendChild( document.createTextNode( valueString ));
                    row.appendChild( value );
                }

                root.appendChild( row );
                rows++;
            }

            rs.close();
            pstmt.close();
            log.info( "Records=" + rows + ", DTD=" + document.getDoctype() + " - " + Trl_Table );

            //

            DOMSource          source      = new DOMSource( document );
            TransformerFactory tFactory    = TransformerFactory.newInstance();
            Transformer        transformer = tFactory.newTransformer();

            // Output

            out.createNewFile();

            StreamResult result = new StreamResult( out.getAbsolutePath() );

            // Transform

            transformer.transform( source,result );
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"exportTrl",e );
        }

        return "";
    }    // exportTrl

    /**
     * Descripción de Método
     *
     *
     * @param Base_Table
     *
     * @return
     */

    private String[] getTrlColumns( String Base_Table ) {
        m_IsCentrallyMaintained = false;

        String sql = "SELECT TableName FROM AD_Table t" + " INNER JOIN AD_Column c ON (c.AD_Table_ID=t.AD_Table_ID AND c.ColumnName='IsCentrallyMaintained') " + "WHERE t.TableName=? AND c.IsActive='Y'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setString( 1,Base_Table );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_IsCentrallyMaintained = true;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"(IsCentrallyMaintained)",e );
        }

        sql = "SELECT ColumnName " + "FROM AD_Column c" + " INNER JOIN AD_Table t ON (c.AD_Table_ID=t.AD_Table_ID) " + "WHERE t.TableName=?" + " AND c.AD_Reference_ID IN (10,14) " + "ORDER BY IsMandatory DESC, ColumnName";

        ArrayList list = new ArrayList();

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setString( 1,Base_Table + "_Trl" );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                String s = rs.getString( 1 );

                // System.out.println(s);

                list.add( s );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        // Lo convierte a un Array

        String[] retValue = new String[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getTrlColumns

    /**
     * Descripción de Método
     *
     *
     * @param AD_Language
     *
     * @return
     */

    public String validateLanguage( String AD_Language ) {
        String    sql      = "SELECT * " + "FROM AD_Language " + "WHERE AD_Language=?";
        MLanguage language = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setString( 1,AD_Language );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                language = new MLanguage( m_ctx,rs,null );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );

            return e.toString();
        }

        // No AD_Language Record

        if( language == null ) {
            log.log( Level.SEVERE,"El lenguaje no existe: " + AD_Language );

            return "El lenguaje no existe: " + AD_Language;
        }

        // El lenguaje existe

        if( language.isActive()) {
            if( language.isBaseLanguage()) {
                return "";
            }
        } else {
            log.log( Level.SEVERE,"Lenguaje no activo o no lenguaje del sistema: " + AD_Language );

            return "Lenguaje no activo o no lenguaje del sistema: " + AD_Language;
        }

        // Validate Translation

        log.info( "Comenzando a validar ... " + language );
        language.maintain( true );

        return "";
    }    // validateLanguage

    /**
     * Descripción de Método
     *
     *
     * @param directory
     * @param AD_Language
     * @param mode
     */

    private void process( String directory,String AD_Language,String mode ) {
        File dir = new File( directory );

        if( !dir.exists()) {
            dir.mkdir();
        }

        dir = new File( directory );

        if( !dir.exists()) {
            System.out.println( "No puedo crear el directorio " + directory );
            System.exit( 1 );
        }

        String sql = "SELECT Name, TableName " + "FROM AD_Table " + "WHERE TableName LIKE '%_Trl' " + "ORDER BY 1";
        ArrayList trlTables = new ArrayList();

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                trlTables.add( rs.getString( 2 ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        for( int i = 0;i < trlTables.size();i++ ) {
            String table = ( String )trlTables.get( i );

            if( mode.startsWith( "i" )) {
                importTrl( directory,-1,AD_Language,table );
            } else {
                exportTrl( directory,-1,AD_Language,table );
            }
        }
    }    // process

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        if( args.length != 3 ) {
            System.out.println( "formato : java Translation directorio AD_Language import|export" );
            System.out.println( "ejemplo: java Translation /ServidorOXP/data/ar_ES ar_ES import" );
            System.out.println( "example: java Translation /ServidorOXP/data/es_US es_US export" );
            System.exit( 1 );
        }

        //

        Login.initTest( false );

        String      directory   = args[ 0 ];
        String      AD_Language = args[ 1 ];
        String      mode        = args[ 2 ];
        Translation trl         = new Translation( Env.getCtx());
        String      msg         = trl.validateLanguage( AD_Language );

        if( msg.length() > 0 ) {
            System.err.println( msg );
        } else {
            trl.process( directory,AD_Language,mode );
        }

        System.exit( 0 );
    }    // main
}    // Translation



/*
 *  @(#)Translation.java   07.11.06
 * 
 *  Fin del fichero Translation.java
 *  
 *  Versión 2.2
 *
 */
