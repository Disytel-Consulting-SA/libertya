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



package org.openXpertya.impexp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public final class ImpFormat {

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     * @param AD_Table_ID
     * @param formatType
     */

    public ImpFormat( String name,int AD_Table_ID,String formatType,String delimiter ) {
        setName( name );
        setTable( AD_Table_ID );
        setFormatType( formatType );
        setFlexDelimiter(delimiter);
    }    // ImpFormat

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ImpFormat.class );

    /** Descripción de Campos */

    private String m_name;

    /** Descripción de Campos */

    private String m_formatType;

    /** Descripción de Campos */

    private int m_AD_Table_ID;

    /** Descripción de Campos */

    private String m_tableName;

    /** Descripción de Campos */

    private String m_tablePK;

    /** Descripción de Campos */

    private String m_tableUnique1;

    /** Descripción de Campos */

    private String m_tableUnique2;

    /** Descripción de Campos */

    private String m_tableUniqueParent;

    /** Descripción de Campos */

    private String m_tableUniqueChild;

    //

    /** Descripción de Campos */

    private String m_BPartner;

    /** Descripción de Campos */

    private ArrayList m_rows = new ArrayList();
    
    private char m_flexDelimiter = ',';

    /**
     * Descripción de Método
     *
     *
     * @param newName
     */

    public void setName( String newName ) {
        if( (newName == null) || (newName.length() == 0) ) {
            throw new IllegalArgumentException( "Name must be at least 1 char" );
        } else {
            m_name = newName;
        }
    }

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
     * @param AD_Table_ID
     */

    public void setTable( int AD_Table_ID ) {
        m_AD_Table_ID = AD_Table_ID;
        m_tableName   = null;
        m_tablePK     = null;

        String sql = "SELECT t.TableName,c.ColumnName " + "FROM AD_Table t INNER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID AND c.IsKey='Y') " + "WHERE t.AD_Table_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_tableName = rs.getString( 1 );
                m_tablePK   = rs.getString( 2 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"ImpFormat.setTable",e );
        }

        if( (m_tableName == null) || (m_tablePK == null) ) {
            log.log( Level.SEVERE,"Data not found for AD_Table_ID=" + AD_Table_ID );
        }

        // Set Additional Table Info

        m_tableUnique1      = "";
        m_tableUnique2      = "";
        m_tableUniqueParent = "";
        m_tableUniqueChild  = "";

        if( m_AD_Table_ID == 311 )                  // I_061_SyncItem
        {
            m_tableUnique1     = "H_UPC";           // UPC = unique
            m_tableUnique2     = "Value";
            m_tableUniqueChild = "H_Commodity1";    // Vendor No may not be unique !
            m_tableUniqueParent = "H_PartnrID";        // Makes it unique
        } else if( m_AD_Table_ID == 532 )              // I_Product
        {
            m_tableUnique1     = "UPC";                // UPC = unique
            m_tableUnique2     = "Value";
            m_tableUniqueChild = "VendorProductNo";    // Vendor No may not be unique !
            m_tableUniqueParent = "BPartner_Value";       // Makes it unique
        } else if( m_AD_Table_ID == 533 )                 // I_BPartner
        {
            m_tableUnique1 = "Value";                     // the key
        } else if( m_AD_Table_ID == 534 )                 // I_ElementValue
        {
            m_tableUniqueParent = "ElementName";          // the parent key
            m_tableUniqueChild  = "Value";                // the key
        } else if( m_AD_Table_ID == 535 )                 // I_ReportLine
        {
            m_tableUniqueParent = "ReportLineSetName";    // the parent key
            m_tableUniqueChild  = "Name";                 // the key
        }

    }                                                     // setTable

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Table_ID() {
        return m_AD_Table_ID;
    }    // getAD_Table_ID

    /** Descripción de Campos */

    public static final String FORMATTYPE_FIXED = "F";

    /** Descripción de Campos */

    public static final String FORMATTYPE_COMMA = "C";

    /** Descripción de Campos */

    public static final String FORMATTYPE_TAB = "T";

    /** Descripción de Campos */

    public static final String FORMATTYPE_XML = "X";

    /**
     * Descripción de Método
     *
     *
     * @param newFormatType
     */

    public void setFormatType( String newFormatType ) {
        if( newFormatType.equals( FORMATTYPE_FIXED ) || newFormatType.equals( FORMATTYPE_COMMA ) || newFormatType.equals( FORMATTYPE_TAB ) || newFormatType.equals( FORMATTYPE_XML )) {
            m_formatType = newFormatType;
        } else {
            throw new IllegalArgumentException( "FormatType must be F/C/T/X" );
        }
    }    // setFormatType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getFormatType() {
        return m_formatType;
    }    // getFormatType

    /**
     * Descripción de Método
     *
     *
     * @param newBPartner
     */

    public void setBPartner( String newBPartner ) {
        m_BPartner = newBPartner;
    }    // setBPartner

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getBPartner() {
        return m_BPartner;
    }    // getVPartner

    /**
     * Descripción de Método
     *
     *
     * @param row
     */

    public void addRow( ImpFormatRow row ) {
        m_rows.add( row );
    }    // addRow

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public ImpFormatRow getRow( int index ) {
        if( (index >= 0) && (index < m_rows.size())) {
            return( ImpFormatRow )m_rows.get( index );
        }

        return null;
    }    // getRow

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
     * @param name
     *
     * @return
     */

    public static ImpFormat load( String name ) {
        log.config( name );

        ImpFormat retValue = null;
        String    sql      = "SELECT * FROM AD_ImpFormat WHERE Name=?";
        int       ID       = 0;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setString( 1,name );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new ImpFormat( name,rs.getInt( "AD_Table_ID" ),rs.getString( "FormatType" ),rs.getString("Delimiter"));
                ID = rs.getInt( "AD_ImpFormat_ID" );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );

            return null;
        }

        loadRows( retValue,ID );

        return retValue;
    }    // getFormat

    /**
     * Descripción de Método
     *
     *
     * @param format
     * @param ID
     */

    private static void loadRows( ImpFormat format,int ID ) {
        String sql = "SELECT f.SeqNo,c.ColumnName,f.StartNo,f.EndNo,f.DataType,c.FieldLength,"    // 1..6
                     + "f.DataFormat,f.DecimalPoint,f.DivideBy100,f.ConstantValue,f.Callout "    // 7..11
                     + "FROM AD_ImpFormat_Row f,AD_Column c " + "WHERE AD_ImpFormat_ID=? AND f.AD_Column_ID=c.AD_Column_ID " + "ORDER BY SeqNo";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                ImpFormatRow row = new ImpFormatRow( rs.getInt( 1 ),rs.getString( 2 ),rs.getInt( 3 ),rs.getInt( 4 ),rs.getString( 5 ),rs.getInt( 6 ));

                //

                row.setFormatInfo( rs.getString( 7 ),rs.getString( 8 ),rs.getString( 9 ).equals( "Y" ),rs.getString( 10 ),rs.getString( 11 ));

                //

                format.addRow( row );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }
    }    // loadLines

    /**
     * Descripción de Método
     *
     *
     * @param line
     * @param withLabel
     * @param trace
     * @param ignoreEmpty
     *
     * @return
     */

    public String[] parseLine( String line,boolean withLabel,boolean trace,boolean ignoreEmpty ) {
        if( trace ) {
            log.config( "" + line );
        }

        ArrayList list = new ArrayList();

        // for all columns

        for( int i = 0;i < m_rows.size();i++ ) {
            ImpFormatRow row   = ( ImpFormatRow )m_rows.get( i );
            StringBuffer entry = new StringBuffer();

            // Label-Start

            if( withLabel ) {
                entry.append( row.getColumnName());
                entry.append( "=" );

                if( row.isString()) {
                    entry.append( "'" );
                } else if( row.isDate()) {
                    entry.append( "TO_DATE('" );
                }
            }

            // Get Data

            String info = null;

            if( row.isConstant()) {
                info = "Constant";
            } else if( m_formatType.equals( FORMATTYPE_FIXED )) {

                // check length

                if( (row.getStartNo() > 0) && (row.getEndNo() <= line.length())) {
                    info = line.substring( row.getStartNo() - 1,row.getEndNo());
                }
            } else {
                info = parseFlexFormat( line,m_formatType,row.getStartNo());
            }

            if( info == null ) {
                info = "";
            }

            // Interpret Data

            entry.append( row.parse( info ));

            // Label-End

            if( withLabel ) {
                if( row.isString()) {
                    entry.append( "'" );
                } else if( row.isDate()) {
                    entry.append( "','YYYY-MM-DD HH24:MI:SS')" );    // JDBC Timestamp format w/o miliseconds
                }
            }

            if( !ignoreEmpty || ( ignoreEmpty && (info.length() != 0) ) ) {
                list.add( entry.toString());
            }

            //

            if( trace ) {
                log.fine( info + "=>" + entry.toString() + " (Length=" + info.length() + ")" );
            }
        }    // for all columns

        String[] retValue = new String[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // parseLine

    /**
     * Descripción de Método
     *
     *
     * @param line
     * @param formatType
     * @param fieldNo
     *
     * @return
     */

    private String parseFlexFormat( String line,String formatType,int fieldNo ) {
        final char QUOTE = '"';

        // check input

        char delimiter = ' ';

        if( formatType.equals( FORMATTYPE_COMMA )) {
//            delimiter = ',';
            // FORMATTYPE_COMMA is used for csv files that can be delimited by comma, semicolon, etc.
            delimiter = getFlexDelimiter();            
        } else if( formatType.equals( FORMATTYPE_TAB )) {
            delimiter = '\t';
        } else {
            throw new IllegalArgumentException( "ImpFormat.parseFlexFormat - unknown format: " + formatType );
        }

        if( (line == null) || (line.length() == 0) || (fieldNo < 0) ) {
            return "";
        }

        // We need to read line sequentially as the fields may be delimited
        // with quotes (") when fields contain the delimiter
        // Example:    "Artikel,bez","Artikel,""nr""",DEM,EUR
        // needs to result in - Artikel,bez - Artikel,"nr" - DEM - EUR

        int pos    = 0;
        int length = line.length();

        for( int field = 1;(field <= fieldNo) && (pos < length);field++ ) {
            StringBuffer content = new StringBuffer();

            // two delimiter directly after each other

            if( line.charAt( pos ) == delimiter ) {
                pos++;

                continue;
            }

            // Handle quotes

            if( line.charAt( pos ) == QUOTE ) {
                pos++;    // move over beginning quote

                while( pos < length ) {

                    // double quote

                    if( (line.charAt( pos ) == QUOTE) && (pos + 1 < length) && (line.charAt( pos + 1 ) == QUOTE) ) {
                        content.append( line.charAt( pos++ ));
                        pos++;
                    }

                    // end quote

                    else if( line.charAt( pos ) == QUOTE ) {
                        pos++;

                        break;
                    }

                    // normal character

                    else {
                        content.append( line.charAt( pos++ ));
                    }
                }

                // we should be at end of line or a delimiter

                if( (pos < length) && (line.charAt( pos ) != delimiter) ) {
                    log.info( "Did not find delimiter at pos " + pos + " " + line );
                }

                pos++;    // move over delimiter
            } else        // plain copy
            {
                while( (pos < length) && (line.charAt( pos ) != delimiter) ) {
                    content.append( line.charAt( pos++ ));
                }

                pos++;    // move over delimiter
            }

            if( field == fieldNo ) {
                return content.toString();
            }
        }

        // nothing found

        return "";
    }    // parseFlexFormat

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param line
     *
     * @return
     */

    public boolean updateDB( Properties ctx,String line ) {
        if( (line == null) || (line.trim().length() == 0) ) {
            log.finest( "No Line" );

            return false;
        }

        String[] nodes = parseLine( line,true,false,true );    // with label, no trace, ignore empty

        if( nodes.length == 0 ) {
            log.finest( "Nothing parsed from: " + line );

            return false;
        }

        // log.config( "ImpFormat.updateDB - listSize=" + nodes.length);

        // Standard Fields

        int AD_Client_ID = Env.getAD_Client_ID( ctx );
        int AD_Org_ID    = Env.getAD_Org_ID( ctx );
        int UpdatedBy    = Env.getAD_User_ID( ctx );

        // Check if the record is already there ------------------------------

        StringBuffer sql = new StringBuffer( "SELECT COUNT(*), MAX(" ).append( m_tablePK ).append( ") FROM " ).append( m_tableName ).append( " WHERE AD_Client_ID=" ).append( AD_Client_ID ).append( " AND (" );

        //

        String where1           = null;
        String where2           = null;
        String whereParentChild = null;

        for( int i = 0;i < nodes.length;i++ ) {
            if( nodes[ i ].endsWith( "=''" ) || nodes[ i ].endsWith( "=0" )) {
                ;
            } else if( nodes[ i ].startsWith( m_tableUnique1 + "=" )) {
                where1 = nodes[ i ];
            } else if( nodes[ i ].startsWith( m_tableUnique2 + "=" )) {
                where2 = nodes[ i ];
            } else if( nodes[ i ].startsWith( m_tableUniqueParent + "=" ) || nodes[ i ].startsWith( m_tableUniqueChild + "=" )) {
                if( whereParentChild == null ) {
                    whereParentChild = nodes[ i ];
                } else {
                    whereParentChild += " AND " + nodes[ i ];
                }
            }
        }

        StringBuffer find = new StringBuffer();

        if( where1 != null ) {
            find.append( where1 );
        }

        if( where2 != null ) {
            if( find.length() > 0 ) {
                find.append( " OR " );
            }

            find.append( where2 );
        }

        if( (whereParentChild != null) && (whereParentChild.indexOf( " AND " ) != -1) )    // need to have both criteria
        {
            if( find.length() > 0 ) {
                find.append( " OR (" ).append( whereParentChild ).append( ")" );    // may have only one
            } else {
                find.append( whereParentChild );
            }
        }

        sql.append( find ).append( ")" );

        int count = 0;
        int ID    = 0;

        try {
            if( find.length() > 0 ) {
                PreparedStatement pstmt = DB.prepareStatement( sql.toString());
                ResultSet         rs    = pstmt.executeQuery();

                if( rs.next()) {
                    count = rs.getInt( 1 );

                    if( count == 1 ) {
                        ID = rs.getInt( 2 );
                    }
                }

                rs.close();
                pstmt.close();
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );

            return false;
        }

        // Insert Basic Record -----------------------------------------------

        if( ID == 0 ) {
            ID  = DB.getNextID( ctx,m_tableName,null );                                                                                                                                                       // get ID
            sql = new StringBuffer( "INSERT INTO " ).append( m_tableName ).append( "(" ).append( m_tablePK ).append( "," ).append( "AD_Client_ID,AD_Org_ID,Created,CreatedBy,Updated,UpdatedBy,IsActive" )    // StdFields
                .append( ") VALUES (" ).append( ID ).append( "," ).append( AD_Client_ID ).append( "," ).append( AD_Org_ID ).append( ",SysDate," ).append( UpdatedBy ).append( ",SysDate," ).append( UpdatedBy ).append( ",'Y'" ).append( ")" );

            //

            int no = DB.executeUpdate( sql.toString());

            if( no != 1 ) {
                log.log( Level.SEVERE,"Insert records=" + no + "; SQL=" + sql.toString());

                return false;
            }

            log.finer( "New ID=" + ID + " " + find );
        } else {
            log.finer( "Old ID=" + ID + " " + find );
        }

        // Update Info -------------------------------------------------------

        sql = new StringBuffer( "UPDATE " ).append( m_tableName ).append( " SET " );

        for( int i = 0;i < nodes.length;i++ ) {
            sql.append( nodes[ i ] ).append( "," );    // column=value
        }

        sql.append( "IsActive='Y',Processed='N',I_IsImported='N',Updated=SysDate,UpdatedBy=" ).append( UpdatedBy );
        sql.append( " WHERE " ).append( m_tablePK ).append( "=" ).append( ID );

        // Update Cmd

        int no = DB.executeUpdate( sql.toString());

        if( no != 1 ) {
            log.log( Level.SEVERE,m_tablePK + "=" + ID + " - rows updated=" + no );

            return false;
        }

        return true;
    }    // updateDB
    
    public void setFlexDelimiter(String delimiter) {
     	if (delimiter != null)
       	    m_flexDelimiter = delimiter.charAt(0);
    }
    	    
    public char getFlexDelimiter() {
       	return m_flexDelimiter;
    }                    

}    // ImpFormat



/*
 *  @(#)ImpFormat.java   02.07.07
 *
 *  Fin del fichero ImpFormat.java
 *
 *  Versión 2.2
 *
 */
