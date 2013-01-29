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

import java.io.File;
import java.io.Serializable;
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
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Trace;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrintData implements Serializable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param name
     */

    public PrintData( Properties ctx,String name ) {
        if( name == null ) {
            throw new IllegalArgumentException( "PrintData - Name cannot be null" );
        }

        m_ctx  = ctx;
        m_name = name;
    }    // PrintData

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param name
     * @param nodes
     */

    public PrintData( Properties ctx,String name,ArrayList nodes ) {
        if( name == null ) {
            throw new IllegalArgumentException( "PrintData - Name cannot be null" );
        }

        m_ctx  = ctx;
        m_name = name;

        if( nodes != null ) {
            m_nodes = nodes;
        }
    }    // PrintData

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private String m_name;

    /** Descripción de Campos */

    private ArrayList m_rows = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_nodes = null;

    /** Descripción de Campos */

    private int m_row = -1;

    /** Descripción de Campos */

    private ArrayList m_functionRows = new ArrayList();

    /** Descripción de Campos */

    private boolean m_hasLevelNo = false;

    /** Descripción de Campos */

    private static final String LEVEL_NO = "LEVELNO";

	/** La table tiene IsBold       */ 
	private boolean		m_hasIsBold = false;
	/**	Level Number Indicator		*/
	private static final String	ISBOLD = "ISBOLD";
	/** La table tiene IndentLevel       */ 
	private boolean		m_hasIndentLevel = false;
	/**	Level Number Indicator		*/
	private static final String	INDENTLEVEL = "INDENTLEVEL";

	/** La table tiene pageBreak		*/
	private boolean m_hasIsPageBreak = false;
	private static final String ISPAGEBREAK = "ISPAGEBREAK";
	
    /** Descripción de Campos */

    private PrintDataColumn[] m_columnInfo = null;

    /** Descripción de Campos */

    private String m_sql = null;

    /** Descripción de Campos */

    private String m_TableName = null;

    /** Descripción de Campos */

    public static final String XML_TAG = "compiereData";

    /** Descripción de Campos */

    public static final String XML_ROW_TAG = "row";

    /** Descripción de Campos */

    public static final String XML_ATTRIBUTE_NAME = "name";

    /** Descripción de Campos */

    public static final String XML_ATTRIBUTE_COUNT = "count";

    /** Descripción de Campos */

    public static final String XML_ATTRIBUTE_NO = "no";

    /** Descripción de Campos */

    public static final String XML_ATTRIBUTE_FUNCTION_ROW = "function_row";

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( PrintData.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Properties getCtx() {
        return m_ctx;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_name;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @param newInfo
     */

    public void setColumnInfo( PrintDataColumn[] newInfo ) {
        m_columnInfo = newInfo;
    }    // setColumnInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PrintDataColumn[] getColumnInfo() {
        return m_columnInfo;
    }    // getColumnInfo

    /**
     * Descripción de Método
     *
     *
     * @param sql
     */

    public void setSQL( String sql ) {
        m_sql = sql;
    }    // setSQL

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSQL() {
        return m_sql;
    }    // getSQL

    /**
     * Descripción de Método
     *
     *
     * @param TableName
     */

    public void setTableName( String TableName ) {
        m_TableName = TableName;
    }    // setTableName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTableName() {
        return m_TableName;
    }    // getTableName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "PrintData[" );

        sb.append( m_name ).append( ",Rows=" ).append( m_rows.size());

        if( m_TableName != null ) {
            sb.append( ",TableName=" ).append( m_TableName );
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEmpty() {
        if( m_nodes == null ) {
            return true;
        }

        return m_nodes.size() == 0;
    }    // isEmpty

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getNodeCount() {
        if( m_nodes == null ) {
            return 0;
        }

        return m_nodes.size();
    }    // getNodeCount

    /**
     * Descripción de Método
     *
     *
     * @param functionRow
     * @param levelNo
     */

    /*
    public void addRow( boolean functionRow,int levelNo ) {
        m_nodes = new ArrayList();
        m_row   = m_rows.size();
        m_rows.add( m_nodes );

        if( functionRow ) {
            m_functionRows.add( new Integer( m_row ));
        }

        if( m_hasLevelNo && (levelNo != 0) ) {
            addNode( new PrintDataElement( LEVEL_NO,new Integer( levelNo ),DisplayType.Integer ));
        }
    }    // addRow
*/
    public void addRow (boolean functionRow, int levelNo)
	{
		addRow(functionRow, levelNo, false, 0, false);
	}
	
	public void addRow (boolean functionRow, int levelNo, boolean isBold, int indentLevel, boolean isPageBreak)
	{
		m_nodes = new ArrayList();
		m_row = m_rows.size();
		m_rows.add (m_nodes);
		if (functionRow)
			m_functionRows.add(new Integer(m_row));
		if (m_hasLevelNo && levelNo != 0)
			addNode(new PrintDataElement(LEVEL_NO, new Integer(levelNo), DisplayType.Integer));
		if (m_hasIsBold && isBold == true)
			addNode(new PrintDataElement(ISBOLD, new Boolean(true), DisplayType.YesNo));
		if (m_hasIndentLevel && indentLevel != 0)
			addNode(new PrintDataElement(INDENTLEVEL, new Integer(indentLevel), DisplayType.Integer));
		if (isPageBreak)
			addNode(new PrintDataElement(ISPAGEBREAK, new Boolean(isPageBreak), DisplayType.YesNo, false,isPageBreak));	
	}	//	addRow
	
    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public boolean setRowIndex( int row ) {
        if( (row < 0) || (row >= m_rows.size())) {
            return false;
        }

        m_row   = row;
        m_nodes = ( ArrayList )m_rows.get( m_row );

        return true;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean setRowNext() {
        return setRowIndex( m_row + 1 );
    }    // setRowNext

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRowCount() {
        return m_rows.size();
    }    // getRowCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRowIndex() {
        return m_row;
    }    // getRowIndex

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public boolean isFunctionRow( int row ) {
        return m_functionRows.contains( new Integer( row ));
    }    // isFunctionRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFunctionRow() {
        return m_functionRows.contains( new Integer( m_row ));
    }    // isFunctionRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPageBreak() {

        // page break requires function and meta data

        if( isFunctionRow() && (m_nodes != null) ) {
            for( int i = 0;i < m_nodes.size();i++ ) {
                Object o = m_nodes.get( i );

                if( o instanceof PrintDataElement ) {
                    PrintDataElement pde = ( PrintDataElement )o;

                    if( pde.isPageBreak()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }    // isPageBreak

    /**
     * Descripción de Método
     *
     *
     * @param hasLevelNo
     */

    public void setHasLevelNo( boolean hasLevelNo ) {
        m_hasLevelNo = hasLevelNo;
    }    // hasLevelNo


	/**
	 * 	PrintData has IsBold
	 * 	@param hasIsBold true if sql contains IsBold
	 */
	public void setHasIsBold (boolean hasIsBold)
	{
		m_hasIsBold = hasIsBold;
	}	//	hasIsBold
	
	
	
	/**
	 * 	PrintData has IsPageBreak
	 * 	@param hasIsBold true if sql contains IsBold
	 */
	public void setHasIsPageBreak (boolean hasIsPageBreak)
	{
		m_hasIsPageBreak = hasIsPageBreak;
	}
	
	/**
	 * 	PrintData has Indent Level
	 * 	@param hasIndentLevel true if sql contains IndentLevel
	 */
	public void setHasIndentLevel (boolean hasIndentLevel)
	{
		m_hasIndentLevel = hasIndentLevel;
	}	//	hasIndentLevel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean hasLevelNo() {
        return m_hasLevelNo;
    }    // hasLevelNo


	/**
	 * 	PrintData has Is Bold
	 * 	@return true if sql contains IsBold
	 */
	public boolean hasIsBold()
	{
		return m_hasIsBold;
	}	//	hasIsBold
	
	/**
	 * 	PrintData has Is Bold
	 * 	@return true if sql contains IsBold
	 */
	public boolean hasIsPageBreak()
	{
		return m_hasIsPageBreak;
	}	//	hasIsPageBreak
	

	
	/**
	 * 	PrintData has IndentLevel
	 * 	@return true if sql contains IndentLevel
	 */
	public boolean hasIndentLevel()
	{
		return m_hasIndentLevel;
	}	//	hasIndentLevel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getLineLevelNo() {
        if( (m_nodes == null) ||!m_hasLevelNo ) {
            return 0;
        }

        for( int i = 0;i < m_nodes.size();i++ ) {
            Object o = m_nodes.get( i );

            if( o instanceof PrintDataElement ) {
                PrintDataElement pde = ( PrintDataElement )o;

                if( LEVEL_NO.equals( pde.getColumnName())) {
                    Integer ii = ( Integer )pde.getValue();

                    return ii.intValue();
                }
            }
        }

        return 0;
    }    // getLineLevel


	/**
	 * 	Get Line Bold value
	 * 	@return bold attribute set false = default
	 */
	public boolean getLineIsBold ()
	{
		if (m_nodes == null || !m_hasIsBold)
			return false;

		for (int i = 0; i < m_nodes.size(); i++)
		{
			Object o = m_nodes.get (i);
			if (o instanceof PrintDataElement)
			{
				PrintDataElement pde = (PrintDataElement)o;
				if (ISBOLD.equals (pde.getColumnName()))
				{
					Boolean ii = (Boolean)pde.getValue();
					return ii.booleanValue();
				}
			}
		}
		
		return false;
	}	//	getLineIsBold
	
	/**
	 * 	Get Line Indent Level for current row
	 * 	@return line indent level 0 = default
	 */
	public int getLineIndentLevel ()
	{
		if (m_nodes == null || !m_hasIndentLevel)
			return 0;

		for (int i = 0; i < m_nodes.size(); i++)
		{
			Object o = m_nodes.get (i);
			if (o instanceof PrintDataElement)
			{
				PrintDataElement pde = (PrintDataElement)o;
				if (INDENTLEVEL.equals (pde.getColumnName()))
				{
					Integer ii = (Integer)pde.getValue();
					return ii.intValue();
				}
			}
		}
		
		return 0;
	}	//	getLineIndentLevel

	
    /**
     * Descripción de Método
     *
     *
     * @param parent
     */

    public void addNode( PrintData parent ) {
        if( parent == null ) {
            throw new IllegalArgumentException( "Parent cannot be null" );
        }

        if( m_nodes == null ) {
            addRow( false,0 );
        }

        m_nodes.add( parent );
    }    // addNode

    /**
     * Descripción de Método
     *
     *
     * @param node
     */

    public void addNode( PrintDataElement node ) {
        if( node == null ) {
            throw new IllegalArgumentException( "Node cannot be null" );
        }

        if( m_nodes == null ) {
            addRow( false,0 );
        }

        m_nodes.add( node );
    }    // addNode

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public Object getNode( int index ) {
        if( (m_nodes == null) || (index < 0) || (index >= m_nodes.size())) {
            return null;
        }

        return m_nodes.get( index );
    }    // getNode

    /**
     * Descripción de Método
     *
     *
     * @param name
     *
     * @return
     */

    public Object getNode( String name ) {
        int index = getIndex( name );

        if( index < 0 ) {
            return null;
        }

        return m_nodes.get( index );
    }    // getNode

    /**
     * Descripción de Método
     *
     *
     * @param AD_Column_ID
     *
     * @return
     */

    public Object getNode( Integer AD_Column_ID ) {
        int index = getIndex( AD_Column_ID.intValue());

        if( index < 0 ) {
            return null;
        }

        return m_nodes.get( index );
    }    // getNode

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PrintDataElement getPKey() {
        if( m_nodes == null ) {
            return null;
        }

        for( int i = 0;i < m_nodes.size();i++ ) {
            Object o = m_nodes.get( i );

            if( o instanceof PrintDataElement ) {
                PrintDataElement pde = ( PrintDataElement )o;

                if( pde.isPKey()) {
                    return pde;
                }
            }
        }

        return null;
    }    // getPKey

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public int getIndex( String columnName ) {
        if( m_nodes == null ) {
            return -1;
        }

        for( int i = 0;i < m_nodes.size();i++ ) {
            Object o = m_nodes.get( i );

            if( o instanceof PrintDataElement ) {
                if( columnName.equals((( PrintDataElement )o ).getColumnName())) {
                    return i;
                }
            } else if( o instanceof PrintData ) {
                if( columnName.equals((( PrintData )o ).getName())) {
                    return i;
                }
            } else {
                log.log( Level.SEVERE,"Element not PrintData(Element) " + o.getClass().getName());
            }
        }

        // As Data is stored sparse, there might be lots of NULL values
        // log.log(Level.SEVERE, "PrintData.getIndex - Element not found - " + name);

        return -1;
    }    // getIndex

    /**
     * Descripción de Método
     *
     *
     * @param AD_Column_ID
     *
     * @return
     */

    public int getIndex( int AD_Column_ID ) {
        if( m_columnInfo == null ) {
            return -1;
        }

        for( int i = 0;i < m_columnInfo.length;i++ ) {
            if( m_columnInfo[ i ].getAD_Column_ID() == AD_Column_ID ) {
                return getIndex( m_columnInfo[ i ].getColumnName());
            }
        }

        log.log( Level.SEVERE,"Column not found - AD_Column_ID=" + AD_Column_ID );

        if( AD_Column_ID == 0 ) {
            Trace.printStack();
        }

        return -1;
    }    // getIndex

    /**
     * Descripción de Método
     *
     */

    public void dump() {
        dump( this );
    }    // dump

    /**
     * Descripción de Método
     *
     */

    public void dumpHeader() {
        dumpHeader( this );
    }    // dumpHeader

    /**
     * Descripción de Método
     *
     */

    public void dumpCurrentRow() {
        dumpRow( this,m_row );
    }    // dump

    /**
     * Descripción de Método
     *
     *
     * @param pd
     */

    private static void dump( PrintData pd ) {
        dumpHeader( pd );

        for( int i = 0;i < pd.getRowCount();i++ ) {
            dumpRow( pd,i );
        }
    }    // dump

    /**
     * Descripción de Método
     *
     *
     * @param pd
     */

    private static void dumpHeader( PrintData pd ) {
        log.info( pd.toString());

        if( pd.getColumnInfo() != null ) {
            for( int i = 0;i < pd.getColumnInfo().length;i++ ) {
                log.config( i + ": " + pd.getColumnInfo()[ i ] );
            }
        }
    }    // dump

    /**
     * Descripción de Método
     *
     *
     * @param pd
     * @param row
     */

    private static void dumpRow( PrintData pd,int row ) {
        log.info( "Row #" + row );

        if( (row < 0) || (row >= pd.getRowCount())) {
            log.warning( "- invalid -" );

            return;
        }

        pd.setRowIndex( row );

        if( pd.getNodeCount() == 0 ) {
            log.config( "- n/a -" );

            return;
        }

        for( int i = 0;i < pd.getNodeCount();i++ ) {
            Object obj = pd.getNode( i );

            if( obj == null ) {
                log.config( "- NULL -" );
            } else if( obj instanceof PrintData ) {
                log.config( "- included -" );
                dump(( PrintData )obj );
            } else if( obj instanceof PrintDataElement ) {
                log.config((( PrintDataElement )obj ).toStringX());
            } else {
                log.config( "- INVALID: " + obj );
            }
        }
    }    // dumpRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Document getDocument() {
        Document document = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // System.out.println(factory.getClass().getName());

            DocumentBuilder builder = factory.newDocumentBuilder();

            document = builder.newDocument();
            document.appendChild( document.createComment( OpenXpertya.getSummaryAscii()));
        } catch( Exception e ) {
            System.err.println( e );
            e.printStackTrace();
        }

        // Root

        Element root = document.createElement( PrintData.XML_TAG );

        root.setAttribute( XML_ATTRIBUTE_NAME,getName());
        root.setAttribute( XML_ATTRIBUTE_COUNT,String.valueOf( getRowCount()));
        document.appendChild( root );
        processXML( this,document,root );

        return document;
    }    // getDocument

    /**
     * Descripción de Método
     *
     *
     * @param pd
     * @param document
     * @param root
     */

    private static void processXML( PrintData pd,Document document,Element root ) {
        for( int r = 0;r < pd.getRowCount();r++ ) {
            pd.setRowIndex( r );

            Element row = document.createElement( PrintData.XML_ROW_TAG );

            row.setAttribute( XML_ATTRIBUTE_NO,String.valueOf( r ));

            if( pd.isFunctionRow()) {
                row.setAttribute( XML_ATTRIBUTE_FUNCTION_ROW,"yes" );
            }

            root.appendChild( row );

            //

            for( int i = 0;i < pd.getNodeCount();i++ ) {
                Object o = pd.getNode( i );

                if( o instanceof PrintData ) {
                    PrintData pd_x    = ( PrintData )o;
                    Element   element = document.createElement( PrintData.XML_TAG );

                    element.setAttribute( XML_ATTRIBUTE_NAME,pd_x.getName());
                    element.setAttribute( XML_ATTRIBUTE_COUNT,String.valueOf( pd_x.getRowCount()));
                    row.appendChild( element );
                    processXML( pd_x,document,element );    // recursive call
                } else if( o instanceof PrintDataElement ) {
                    PrintDataElement pde = ( PrintDataElement )o;

                    if( !pde.isNull()) {
                        Element element = document.createElement( PrintDataElement.XML_TAG );

                        element.setAttribute( PrintDataElement.XML_ATTRIBUTE_NAME,pde.getColumnName());

                        if( pde.hasKey()) {
                            element.setAttribute( PrintDataElement.XML_ATTRIBUTE_KEY,pde.getValueKey());
                        }

                        element.appendChild( document.createTextNode( pde.getValueDisplay( null )));    // not formatted
                        row.appendChild( element );
                    }
                } else {
                    log.log( Level.SEVERE,"Element not PrintData(Element) " + o.getClass().getName());
                }
            }    // columns
        }        // rows
    }            // processTree

    /**
     * Descripción de Método
     *
     *
     * @param result
     *
     * @return
     */

    public boolean createXML( StreamResult result ) {
        try {
            DOMSource          source      = new DOMSource( getDocument());
            TransformerFactory tFactory    = TransformerFactory.newInstance();
            Transformer        transformer = tFactory.newTransformer();

            transformer.transform( source,result );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"(StreamResult)",e );

            return false;
        }

        return true;
    }    // createXML

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     *
     * @return
     */

    public boolean createXML( String fileName ) {
        try {
            File file = new File( fileName );

            file.createNewFile();

            StreamResult result = new StreamResult( file );

            createXML( result );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"(file)",e );

            return false;
        }

        return true;
    }    // createXMLFile

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param input
     *
     * @return
     */

    public static PrintData parseXML( Properties ctx,File input ) {
        log.config( input.toString());

        PrintData pd = null;

        try {
            PrintDataHandler handler = new PrintDataHandler( ctx );
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser        parser  = factory.newSAXParser();

            parser.parse( input,handler );
            pd = handler.getPrintData();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }

        return pd;
    }    // parseXML

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        System.setProperty( "javax.xml.parsers.DocumentBuilderFactory","org.apache.crimson.jaxp.DocumentBuilderFactoryImpl" );    // System Default

        // "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

        System.setProperty( "javax.xml.parsers.SAXParserFactory","org.apache.crimson.jaxp.SAXParserFactoryImpl" );    // System Default

        // "org.apache.xerces.jaxp.SAXParserFactoryImpl");

        PrintData pd = new PrintData( new Properties(),"test1" );

        pd.addNode( new PrintDataElement( "test1element1","testvalue<1>",0 ));
        pd.addNode( new PrintDataElement( "test1element2","testvalue&2&",0 ));

        PrintData pdx = new PrintData( new Properties(),"test2" );

        pdx.addNode( new PrintDataElement( "test2element1-1","testvalue11",0 ));
        pdx.addNode( new PrintDataElement( "test2element1-2","testvalue12",0 ));
        pdx.addRow( false,0 );
        pdx.addNode( new PrintDataElement( "test2element2-1","testvalue21",0 ));
        pdx.addNode( new PrintDataElement( "test2element2-2","testvalue22",0 ));
        pd.addNode( pdx );
        pd.addNode( new PrintDataElement( "test1element3","testvalue/3/",0 ));
        pd.createXML( "C:\\Temp\\printData.xml" );
        pd.createXML( new StreamResult( System.out ));
        System.out.println( "" );
        pd.dump();

        // parse

        System.out.println( "" );

        PrintData pd1 = parseXML( new Properties(),new File( "C:\\Temp\\printData.xml" ));

        pd1.createXML( new StreamResult( System.out ));
        System.out.println( "" );
        pd1.dump();
    }    // main
}    // PrintData



/*
 *  @(#)PrintData.java   23.03.06
 * 
 *  Fin del fichero PrintData.java
 *  
 *  Versión 2.2
 *
 */
