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

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.model.UpdateRecord_IDReports;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DataEngine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param language
     */

    public DataEngine( Language language ) {
        if( language != null ) {
            m_language = language;
        }
    }    // DataEngine

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( DataEngine.class );

    /** Descripción de Campos */

    private String m_synonym = "A";

    /** Descripción de Campos */

    private Language m_language = Language.getLoginLanguage();

    /** Descripción de Campos */

    private PrintDataGroup m_group = new PrintDataGroup();

    /** Descripción de Campos */

    private long m_startTime = System.currentTimeMillis();

    /** Descripción de Campos */

    private int m_runningTotalLines = -1;

    /** Descripción de Campos */

    private String m_runningTotalString = null;

    /** Descripción de Campos */

    public static final String KEY = "*";

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param format
     * @param query
     *
     * @return
     */

    public PrintData getPrintData( Properties ctx,MPrintFormat format,MQuery query ) {
        if( format == null ) {
            throw new IllegalStateException( "No print format" );
        }

        String tableName  = null;
        String reportName = format.getName();

        //

        if( format.getAD_ReportView_ID() != 0 ) {
            String sql = "SELECT t.AD_Table_ID, t.TableName, rv.Name " + "FROM AD_Table t" + " INNER JOIN AD_ReportView rv ON (t.AD_Table_ID=rv.AD_Table_ID) " + "WHERE rv.AD_ReportView_ID=?";    // 1

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,format.getAD_ReportView_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    tableName  = rs.getString( 2 );                               // TableName
                    reportName = rs.getString( 3 );
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"ReportView",e );

                return null;
            }
        } else {
            String sql = "SELECT TableName FROM AD_Table WHERE AD_Table_ID=?";    // #1

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql.toString());

                pstmt.setInt( 1,format.getAD_Table_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    tableName = rs.getString( 1 );    // TableName
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e1 ) {
                log.log( Level.SEVERE,"Table",e1 );

                return null;
            }
        }

        if( tableName == null ) {
            log.log( Level.SEVERE,"Not found Format=" + format );

            return null;
        }

        if( format.isTranslationView() && tableName.toLowerCase().endsWith( "_v" )) {    // _vt not just _v
            tableName += "t";
            
            query.addRestriction("ad_language", "=", Env.getAD_Language( Env.getCtx()) );
        }
        log.fine( "Query despues de anadir el ad_language" + query.toString() );
        
        format.setTranslationViewQuery( query );

        //

        PrintData pd = getPrintDataInfo( ctx,format,query,reportName,tableName );

        if( pd == null ) {
            return null;
        }

        loadPrintData( pd,format );

        return pd;
    }    // getPrintData

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param format
     * @param query
     * @param reportName
     * @param tableName
     *
     * @return
     */

    private PrintData getPrintDataInfo( Properties ctx,MPrintFormat format,MQuery query,String reportName,String tableName ) {
        m_startTime = System.currentTimeMillis();
        log.info( reportName + " - " + m_language.getAD_Language());
        log.fine( "TableName=" + tableName + ", Query=" + query );
        log.fine( "Format=" + format );
        
        ArrayList columns = new ArrayList();

        m_group = new PrintDataGroup();

        // Order Columns (identifed by non zero/null SortNo)

        int[]     orderAD_Column_IDs = format.getOrderAD_Column_IDs();
        ArrayList orderColumns       = new ArrayList( orderAD_Column_IDs.length );

        for( int i = 0;i < orderAD_Column_IDs.length;i++ ) {
            log.finest( "Order AD_Column_ID=" + orderAD_Column_IDs[ i ] );
            orderColumns.add( "" );    // initial value overwritten with fully qualified name
        }

        // Direct SQL w/o Reference Info

        StringBuffer sqlSELECT = new StringBuffer( "SELECT " );
        StringBuffer sqlFROM   = new StringBuffer( " FROM " );

        sqlFROM.append( tableName );

        StringBuffer sqlGROUP = new StringBuffer( " GROUP BY " );

        //

        boolean IsGroupedBy = false;

        //

        String sql = "SELECT c.AD_Column_ID,c.ColumnName,"                                // 1..2
                     + "c.AD_Reference_ID,c.AD_Reference_Value_ID,"                       // 3..4
                     + "c.FieldLength,c.IsMandatory,c.IsKey,c.IsParent,"                  // 5..8
                     + "COALESCE(rvc.IsGroupFunction,'N'),rvc.FunctionColumn,"            // 9..10
                     + "pfi.IsGroupBy,pfi.IsSummarized,pfi.IsAveraged,pfi.IsCounted, "    // 11..14
                     + "pfi.IsPrinted,pfi.SortNo,pfi.IsPageBreak, "                                                                                                                                                                                                                                                                                                       // 15..17
                     + "pfi.IsMinCalc,pfi.IsMaxCalc, "                                                                                                                                                                                                                                                                                                                    // 18..19
                     + "pfi.isRunningTotal,pfi.RunningTotalLines, "                                                                                                                                                                                                                                                                                                       // 20..21
                     + "pfi.IsVarianceCalc, pfi.IsDeviationCalc "                                                                                                                                                                                                                                                                                                         // 22..23
                     + "FROM AD_PrintFormat pf" 
                     + " INNER JOIN AD_PrintFormatItem pfi ON (pf.AD_PrintFormat_ID=pfi.AD_PrintFormat_ID)" 
                     + " INNER JOIN AD_Column c ON (pfi.AD_Column_ID=c.AD_Column_ID)" 
                     + " LEFT JOIN AD_ReportView_Col rvc ON (pf.AD_ReportView_ID=rvc.AD_ReportView_ID AND c.AD_Column_ID=rvc.AD_Column_ID) " + "WHERE pf.AD_PrintFormat_ID=?"    // #1
                     + " AND pfi.IsActive='Y' AND (pfi.IsPrinted='Y' OR c.IsKey='Y' OR pfi.SortNo > 0) " 
                     + "ORDER BY pfi.IsPrinted DESC, pfi.SeqNo";    // Functions are put in first column

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,format.getID());

            ResultSet rs = pstmt.executeQuery();

            m_synonym = "A";    // synonym

            while( rs.next()) {
            	//dentro de este while si rs.getString(2)==Record_ID entonces hacemos algo especial...

                // get Values from record

                int    AD_Column_ID          = rs.getInt( 1 );
                String ColumnName            = rs.getString( 2 );
                int    AD_Reference_ID       = rs.getInt( 3 );
                int    AD_Reference_Value_ID = rs.getInt( 4 );

                // ColumnInfo

                int     FieldLength = rs.getInt( 5 );
                boolean IsMandatory = "Y".equals( rs.getString( 6 ));
                boolean IsKey       = "Y".equals( rs.getString( 7 ));
                boolean IsParent    = "Y".equals( rs.getString( 8 ));

                // SQL GroupBy

                boolean IsGroupFunction = "Y".equals( rs.getString( 9 ));

                if( IsGroupFunction ) {
                    IsGroupedBy = true;
                }

                String FunctionColumn = rs.getString( 10 );

                if( FunctionColumn == null ) {
                    FunctionColumn = "";
                }

                // Breaks/Column Functions

                if( "Y".equals( rs.getString( 11 ))) {
                    m_group.addGroupColumn( ColumnName );
                }

                if( "Y".equals( rs.getString( 12 ))) {
                    m_group.addFunction( ColumnName,PrintDataFunction.F_SUM );
                }

                if( "Y".equals( rs.getString( 13 ))) {
                    m_group.addFunction( ColumnName,PrintDataFunction.F_MEAN );
                }

                if( "Y".equals( rs.getString( 14 ))) {
                    m_group.addFunction( ColumnName,PrintDataFunction.F_COUNT );
                }

                if( "Y".equals( rs.getString( 18 ))) {    // IsMinCalc
                    m_group.addFunction( ColumnName,PrintDataFunction.F_MIN );
                }

                if( "Y".equals( rs.getString( 19 ))) {    // IsMaxCalc
                    m_group.addFunction( ColumnName,PrintDataFunction.F_MAX );
                }

                if( "Y".equals( rs.getString( 22 ))) {    // IsVarianceCalc
                    m_group.addFunction( ColumnName,PrintDataFunction.F_VARIANCE );
                }

                if( "Y".equals( rs.getString( 23 ))) {    // IsDeviationCalc
                    m_group.addFunction( ColumnName,PrintDataFunction.F_DEVIATION );
                }

                if( "Y".equals( rs.getString( 20 ))) {    // isRunningTotal

                    // RunningTotalLines only once - use max

                    m_runningTotalLines = Math.max( m_runningTotalLines,rs.getInt( 21 ));
                }

                // General Info

                boolean IsPrinted   = "Y".equals( rs.getString( 15 ));
                int     SortNo      = rs.getInt( 16 );
                boolean isPageBreak = "Y".equals( rs.getString( 17 ));

                // Fully qualified Table.Column for ordering

                String          orderName = tableName + "." + ColumnName;
                PrintDataColumn pdc       = null;

                // -- Key --

                if( IsKey ) {

                    // =>      Table.Column,

                    sqlSELECT.append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    sqlGROUP.append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    pdc = new PrintDataColumn( AD_Column_ID,ColumnName,AD_Reference_ID,FieldLength,KEY,isPageBreak );    // KeyColumn
                } else if( !IsPrinted ) {    // not printed Sort Columns
                    ;

                    // -- Parent, TableDir (and unqualified Search) --

                } else if( IsParent || (AD_Reference_ID == DisplayType.TableDir) || ( (AD_Reference_ID == DisplayType.Search) && (AD_Reference_Value_ID == 0) ) ) {

                    // Creates Embedded SQL in the form
                    // SELECT ColumnTable.Name FROM ColumnTable WHERE TableName.ColumnName=ColumnTable.ColumnName

                    String eSql = MLookupFactory.getLookup_TableDirEmbed( m_language,ColumnName,tableName );

                    // TableName

                    String table = ColumnName;

                    if( table.endsWith( "_ID" )) {
                        table = table.substring( 0,table.length() - 3 );
                    }

                    // DisplayColumn

                    String display = ColumnName;

                    // => (..) AS AName, Table.ID,
                    // Aca esta el toco, eSql = ""

                    if(eSql.length() != 0){
                    	sqlSELECT.append( "(" ).append( eSql ).append( ") AS " ).append( m_synonym ).append( display ).append( "," ).append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    	sqlGROUP.append( m_synonym ).append( display ).append( "," ).append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    	orderName = m_synonym + display;
                    }
                    //sqlSELECT.append( "(" ).append( eSql ).append( ") AS " ).append( m_synonym ).append( display ).append( "," ).append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    //sqlGROUP.append( m_synonym ).append( display ).append( "," ).append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    //orderName = m_synonym + display;

                    //

                    pdc = new PrintDataColumn( AD_Column_ID,ColumnName,AD_Reference_ID,FieldLength,orderName,isPageBreak );
                    synonymNext();
                }

                // -- Table --

                else if( (AD_Reference_ID == DisplayType.Table) || ( (AD_Reference_ID == DisplayType.Search) && (AD_Reference_Value_ID != 0) ) ) {
                    TableReference tr = getTableReference( AD_Reference_Value_ID );
                    String display = tr.DisplayColumn;

                    // => A.Name AS AName, Table.ID,

                    if( tr.IsValueDisplayed ) {
                        sqlSELECT.append( m_synonym ).append( ".Value||'-'||" );
                    }

                    sqlSELECT.append( m_synonym ).append( "." ).append( display );
                    sqlSELECT.append( " AS " ).append( m_synonym ).append( display ).append( "," ).append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    sqlGROUP.append( m_synonym ).append( "." ).append( display ).append( "," ).append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    orderName = m_synonym + display;

                    // => x JOIN table A ON (x.KeyColumn=A.Key)

                    if( IsMandatory ) {
                        sqlFROM.append( " INNER JOIN " );
                    } else {
                        sqlFROM.append( " LEFT OUTER JOIN " );
                    }

                    sqlFROM.append( tr.TableName ).append( " " ).append( m_synonym ).append( " ON (" ).append( tableName ).append( "." ).append( ColumnName ).append( "=" ).append( m_synonym ).append( "." ).append( tr.KeyColumn ).append( ")" );

                    //

                    pdc = new PrintDataColumn( AD_Column_ID,ColumnName,AD_Reference_ID,FieldLength,orderName,isPageBreak );
                    synonymNext();
                }

                // -- List or Button with ReferenceValue --
                
                else if( (AD_Reference_ID == DisplayType.List) || ( (AD_Reference_ID == DisplayType.Button) && (AD_Reference_Value_ID != 0) ) ) {
                    if( Env.isBaseLanguage( m_language,"AD_Ref_List" )) {

                        // => A.Name AS AName,

                        sqlSELECT.append( m_synonym ).append( ".Name AS " ).append( m_synonym ).append( "Name," );
                        sqlGROUP.append( m_synonym ).append( ".Name," );
                        orderName = m_synonym + "Name";

                        // => x JOIN AD_Ref_List A ON (x.KeyColumn=A.Value AND A.AD_Reference_ID=123)

                        if( IsMandatory ) {
                            sqlFROM.append( " INNER JOIN " );
                        } else {
                            sqlFROM.append( " LEFT OUTER JOIN " );
                        }

                        sqlFROM.append( "AD_Ref_List " ).append( m_synonym ).append( " ON (" ).append( tableName ).append( "." ).append( ColumnName ).append( "=" ).append( m_synonym ).append( ".Value" ).append( " AND " ).append( m_synonym ).append( ".AD_Reference_ID=" ).append( AD_Reference_Value_ID ).append( ")" );
                    } else {

                        // => A.Name AS AName,

                        sqlSELECT.append( m_synonym ).append( ".Name AS " ).append( m_synonym ).append( "Name," );
                        sqlGROUP.append( m_synonym ).append( ".Name," );
                        orderName = m_synonym + "Name";

                        // LEFT OUTER JOIN AD_Ref_List XA ON (AD_Table.EntityType=XA.Value AND XA.AD_Reference_ID=245)
                        // LEFT OUTER JOIN AD_Ref_List_Trl A ON (XA.AD_Ref_List_ID=A.AD_Ref_List_ID AND A.AD_Language='de_DE')

                        if( IsMandatory ) {
                            sqlFROM.append( " INNER JOIN " );
                        } else {
                            sqlFROM.append( " LEFT OUTER JOIN " );
                        }

                        sqlFROM.append( " AD_Ref_List X" ).append( m_synonym ).append( " ON (" ).append( tableName ).append( "." ).append( ColumnName ).append( "=X" ).append( m_synonym ).append( ".Value AND X" ).append( m_synonym ).append( ".AD_Reference_ID=" ).append( AD_Reference_Value_ID ).append( ")" );

                        if( IsMandatory ) {
                            sqlFROM.append( " INNER JOIN " );
                        } else {
                            sqlFROM.append( " LEFT OUTER JOIN " );
                        }

                        sqlFROM.append( " AD_Ref_List_Trl " ).append( m_synonym ).append( " ON (X" ).append( m_synonym ).append( ".AD_Ref_List_ID=" ).append( m_synonym ).append( ".AD_Ref_List_ID" ).append( " AND " ).append( m_synonym ).append( ".AD_Language='" ).append( m_language.getAD_Language()).append( "')" );
                    }

                    // TableName.ColumnName,

                    sqlSELECT.append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    pdc = new PrintDataColumn( AD_Column_ID,ColumnName,AD_Reference_ID,FieldLength,orderName,isPageBreak );
                    synonymNext();
                }

                // -- Special Lookups --

                else if( (AD_Reference_ID == DisplayType.Location) || (AD_Reference_ID == DisplayType.Account) || (AD_Reference_ID == DisplayType.Locator) || (AD_Reference_ID == DisplayType.PAttribute) ) {

                    // TableName, DisplayColumn

                    String table   = "";
                    String key     = "";
                    String display = "";
                    String synonym = null;

                    //

                    if( AD_Reference_ID == DisplayType.Location ) {
                        table   = "C_Location";
                        key     = "C_Location_ID";
                        display = "City||'.'";    // in case City is empty
                        synonym = "Address";
                    } else if( AD_Reference_ID == DisplayType.Account ) {
                        table   = "C_ValidCombination";
                        key     = "C_ValidCombination_ID";
                        display = "Combination";
                    } else if( AD_Reference_ID == DisplayType.Locator ) {
                        table   = "M_Locator";
                        key     = "M_Locator_ID";
                        display = "Value";
                    } else if( AD_Reference_ID == DisplayType.PAttribute ) {
                        table   = "M_AttributeSetInstance";
                        key     = "M_AttributeSetInstance_ID";
                        display = "Description";

                        if( CLogMgt.isLevelFine()) {
                            display += "||'{'||" + m_synonym + ".M_AttributeSetInstance_ID||'}'";
                        }

                        synonym = "Description";
                    }

                    if( synonym == null ) {
                        synonym = display;
                    }

                    // => A.Name AS AName, table.ID,

                    sqlSELECT.append( m_synonym ).append( "." ).append( display ).append( " AS " ).append( m_synonym ).append( synonym ).append( "," ).append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    sqlGROUP.append( m_synonym ).append( "." ).append( synonym ).append( "," ).append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    orderName = m_synonym + synonym;

                    // => x JOIN table A ON (table.ID=A.Key)

                    if( IsMandatory ) {
                        sqlFROM.append( " INNER JOIN " );
                    } else {
                        sqlFROM.append( " LEFT OUTER JOIN " );
                    }

                    sqlFROM.append( table ).append( " " ).append( m_synonym ).append( " ON (" ).append( tableName ).append( "." ).append( ColumnName ).append( "=" ).append( m_synonym ).append( "." ).append( key ).append( ")" );

                    //

                    pdc = new PrintDataColumn( AD_Column_ID,ColumnName,AD_Reference_ID,FieldLength,orderName,isPageBreak );
                    synonymNext();
                }

                // -- Standard Column --

                else {
                	log.fine("Zarius ---------------------->" + rs.getString(2));

            		
                    int          index = FunctionColumn.indexOf( "@" );
                    StringBuffer sb    = new StringBuffer();

                    if( index == -1 ) {

                    	//Begin Updated by Zarius - Dataware - 30/08/2006
                    	if ( rs.getString(2).equals("Record_ID")){
                    	//Updating report view
                    	UpdateRecord_IDReports URR= new UpdateRecord_IDReports();
                    	URR.doIt();
                    	//Selecting names
                    	sb.append( "(SELECT ad_record_idreportnames_v.name " );
                    	sb.append( "FROM ad_record_idreportnames_v " );
                    	sb.append( "WHERE ad_record_idreportnames_v.a=").append(tableName).append(".ad_table_id " );
                    	sb.append( "and ad_record_idreportnames_v.r=C_Unavailability_v.Record_id) AS Record_ID, " );
                    		
                    	}else{
                    		sb.append( tableName ).append( "." ).append( ColumnName ).append( "," );
                    	}

                    	//End Updated by Zarius - Dataware - 30/08/2006
                        sqlSELECT.append( sb.toString());
                        
                        if( !IsGroupFunction ) {
                            sqlGROUP.append( sb.toString());
                        }
                    } else {

                        // => Function(Table.Column) AS Column   -- function has @ where column name goes

                        sb.append( FunctionColumn.substring( 0,index ))

                        // If I eg entered sum(amount)  as function column in the report view the query would look like:
                        // Tablename.amountsum(amount), after removing the line below I get the wanted result. The original query column (tablename.column) is replaced by the function column entered in the report view window.
                        // .append(tableName).append(".").append(ColumnName)       // xxxxxx

                        .append( FunctionColumn.substring( index + 1 ));
                        sqlSELECT.append( sb.toString()).append( " AS " ).append( ColumnName ).append( "," );

                        
                        if( !IsGroupFunction ) {
                            sqlGROUP.append( sb.toString()).append( "," );
                        }
                    }

                    pdc = new PrintDataColumn( AD_Column_ID,ColumnName,AD_Reference_ID,FieldLength,ColumnName,isPageBreak );
                }

                // Order Sequence - Overwrite order column name

                for( int i = 0;i < orderAD_Column_IDs.length;i++ ) {
                    if( AD_Column_ID == orderAD_Column_IDs[ i ] ) {
                        orderColumns.set( i,orderName );

                        break;
                    }
                }

                //

                if( (pdc == null) || ( !IsPrinted &&!IsKey )) {
                    continue;
                }

                columns.add( pdc );
            }    // for all Fields in Tab

            rs.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"SQL=" + sql + " - ID=" + format.getID(),e );
        }

        if( columns.size() == 0 ) {
            log.log( Level.SEVERE,"No Colums - Delete Report Format " + reportName + " and start again" );
            log.finest( "No Colums - SQL=" + sql + " - ID=" + format.getID());

            return null;
        }

        boolean hasLevelNo = false;
        boolean hasIsBold = false;
		boolean hasIndentLevel = false;
		boolean hasIsPageBreak = false;
		
        if( tableName.startsWith( "T_Report" )) {
            hasLevelNo = true;
            hasIsBold = true;
			hasIndentLevel = true;
			hasIsPageBreak = true;
			
            if( sqlSELECT.indexOf( "LevelNo" ) == -1 ) {
                sqlSELECT.append( "LevelNo," );
            }

			if (sqlSELECT.indexOf("IsBold") == -1)
				sqlSELECT.append("IsBold,");
			
			if (sqlSELECT.indexOf("IndentLevel") == -1)
				sqlSELECT.append("IndentLevel,");
			
			if (sqlSELECT.indexOf("IsPageBreak") == -1)
				sqlSELECT.append("IsPageBreak,");
        }

        StringBuffer finalSQL = new StringBuffer();

        finalSQL.append( sqlSELECT.substring( 0,sqlSELECT.length() - 1 )).append( sqlFROM );

        // WHERE clause

        if( tableName.startsWith( "T_Report" )) {
            finalSQL.append( " WHERE " );

            for( int i = 0;i < query.getRestrictionCount();i++ ) {
                String q = query.getWhereClause( i );

                if( q.indexOf( "AD_PInstance_ID" ) != -1 ) {    // ignore all other Parameters
                    finalSQL.append( q );
                }
            }    // for all restrictions
        } else {

            // User supplied Where Clause

            if( (query != null) && query.isActive()) {
                finalSQL.append( " WHERE " );

                if( !query.getTableName().equals( tableName )) {
                    query.setTableName( tableName );
                }

                finalSQL.append( query.getWhereClause( true ));
            }

            // Access Restriction

            MRole role = MRole.getDefault( ctx,false );

            if( (role.getAD_Role_ID() == 0) &&!Ini.isClient()) {
                ;    // System Access
            } else {
                finalSQL = new StringBuffer( role.addAccessSQL( finalSQL.toString(),tableName,MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO ));
            }
        }

        // Group By

        if( IsGroupedBy ) {
            finalSQL.append( sqlGROUP.substring( 0,sqlGROUP.length() - 1 ));    // last ,
        }

        // Add ORDER BY clause

        if( orderColumns != null ) {
            for( int i = 0;i < orderColumns.size();i++ ) {
                if( i == 0 ) {
                    finalSQL.append( " ORDER BY " );
                } else {
                    finalSQL.append( "," );
                }

                String by = ( String )orderColumns.get( i );

                if( (by == null) || (by.length() == 0) ) {
                    by = String.valueOf( i + 1 );
                }

                finalSQL.append( by );
            }
        }    // order by

        // Print Data

        PrintData         pd   = new PrintData( ctx,reportName );
        PrintDataColumn[] info = new PrintDataColumn[ columns.size()];

        columns.toArray( info );    // column order is is m_synonymc with SELECT column position
        pd.setColumnInfo( info );
        pd.setTableName( tableName );
        pd.setSQL( finalSQL.toString());
        pd.setHasLevelNo( hasLevelNo );
        pd.setHasIsBold(hasIsBold);
		pd.setHasIndentLevel(hasIndentLevel);
		pd.setHasIsPageBreak(hasIsPageBreak);
		
        log.finest( finalSQL.toString());
        log.finest( "Group=" + m_group );

        return pd;
    }    // getPrintDataInfo

    /**
     * Descripción de Método
     *
     */

    private void synonymNext() {
        int  length = m_synonym.length();
        char cc     = m_synonym.charAt( 0 );

        if( cc == 'Z' ) {
            cc = 'A';
            length++;
        } else {
            cc++;
        }

        //

        m_synonym = String.valueOf( cc );

        if( length == 1 ) {
            return;
        }

        m_synonym += String.valueOf( cc );

        if( length == 2 ) {
            return;
        }

        m_synonym += String.valueOf( cc );
    }    // synonymNext

    /**
     * Descripción de Método
     *
     *
     * @param AD_Reference_Value_ID
     *
     * @return
     */

    public static TableReference getTableReference( int AD_Reference_Value_ID ) {
        TableReference tr = new TableReference();

        //

        String SQL = "SELECT t.TableName, ck.ColumnName AS KeyColumn,"                                                                                                                                                                                                                                                                                     // 1..2
                     + " cd.ColumnName AS DisplayColumn, rt.IsValueDisplayed, cd.IsTranslated " + "FROM AD_Ref_Table rt" + " INNER JOIN AD_Table t ON (rt.AD_Table_ID = t.AD_Table_ID)" + " INNER JOIN AD_Column ck ON (rt.AD_Key = ck.AD_Column_ID)" + " INNER JOIN AD_Column cd ON (rt.AD_Display = cd.AD_Column_ID) " + "WHERE rt.AD_Reference_ID=?"    // 1
                     + " AND rt.IsActive = 'Y' AND t.IsActive = 'Y'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,AD_Reference_Value_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                tr.TableName        = rs.getString( 1 );
                tr.KeyColumn        = rs.getString( 2 );
                tr.DisplayColumn    = rs.getString( 3 );
                tr.IsValueDisplayed = "Y".equals( rs.getString( 4 ));
                tr.IsTranslated     = "Y".equals( rs.getString( 5 ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,SQL,ex );
        }

        return tr;
    }    // getTableReference


    PreparedStatement pstmt;
    /**
     * Descripción de Método
     *
     *
     * @param pd
     * @param format
     */
    private void loadPrintData( PrintData pd,MPrintFormat format ) {

        // Translate Spool Output

        boolean translateSpool = pd.getTableName().equals( "T_Spool" );

        m_runningTotalString = Msg.getMsg( format.getLanguage(),"RunningTotal" );

        int             rowNo      = 0;
        PrintDataColumn pdc        = null;
        boolean         hasLevelNo = pd.hasLevelNo();
        int             levelNo    = 0;
        boolean hasIsBold=pd.hasIsBold();
		
		boolean hasIndentLevel=pd.hasIndentLevel();
		boolean hasIsPageBreak = pd.hasIsPageBreak();
		
		int indentLevel=0;
		
        //

        try {
        	log.info("SQL: " + pd.getSQL());
        	
            pstmt = DB.prepareStatement(pd.getSQL());
            ResultSet         rs    = pstmt.executeQuery();

            // Row Loop

            while( rs.next()) {
				boolean isBold=false;
				boolean isPageBreak = false;
				
                if( hasLevelNo ) {
                    levelNo = rs.getInt( "LevelNo" );
                } else {
                    levelNo = 0;
                }

				if (hasIsBold)
					isBold = "Y".equals(rs.getString("IsBold"));
				
				if (hasIndentLevel)
					indentLevel = rs.getInt("IndentLevel");
				else
					indentLevel = 0;
				if (hasIsPageBreak)
					isPageBreak = "Y".equals(rs.getString("IsPageBreak"));
				
                // Check Group Change ----------------------------------------

                if( m_group.getGroupColumnCount() > 1 )    // one is GRANDTOTAL_
                {

                    // Check Columns for Function Columns

                    for( int i = pd.getColumnInfo().length - 1;i >= 0;
                            i-- )                          // backwards (leaset group first)
                    {
                        PrintDataColumn group_pdc = pd.getColumnInfo()[ i ];

                        if( !m_group.isGroupColumn( group_pdc.getColumnName())) {
                            continue;
                        }

                        // Group change

                        Object value = m_group.groupChange( group_pdc.getColumnName(),rs.getObject( group_pdc.getAlias()));

                        if( value != null )    // Group change
                        {
                            char[] functions = m_group.getFunctions( group_pdc.getColumnName());

                            for( int f = 0;f < functions.length;f++ ) {
                                printRunningTotal( pd,levelNo,rowNo++ );
                                //pd.addRow( true,levelNo );
                                pd.addRow(true, levelNo, isBold, indentLevel, isPageBreak);

                                // get columns

                                for( int c = 0;c < pd.getColumnInfo().length;c++ ) {
                                    pdc = pd.getColumnInfo()[ c ];

                                    // log.fine("loadPrintData - PageBreak = " + pdc.isPageBreak());

                                    if( group_pdc.getColumnName().equals( pdc.getColumnName())) {
                                        String valueString = value.toString();

                                        if( value instanceof Timestamp ) {
                                            valueString = DisplayType.getDateFormat( pdc.getDisplayType(),m_language ).format( value );
                                        }

                                        valueString += PrintDataFunction.getFunctionSymbol( functions[ f ] );
                                        pd.addNode( new PrintDataElement( pdc.getColumnName(),valueString,DisplayType.String,false,pdc.isPageBreak()));
                                    } else if( m_group.isFunctionColumn( pdc.getColumnName(),functions[ f ] )) {
                                        pd.addNode( new PrintDataElement( pdc.getColumnName(),m_group.getValue( group_pdc.getColumnName(),pdc.getColumnName(),functions[ f ] ),PrintDataFunction.getFunctionDisplayType( functions[ f ] ),false,pdc.isPageBreak()));
                                    }
                                }    // for all columns
                            }        // for all functions

                            // Reset Group Values

                            for( int c = 0;c < pd.getColumnInfo().length;c++ ) {
                                pdc = pd.getColumnInfo()[ c ];
                                m_group.reset( group_pdc.getColumnName(),pdc.getColumnName());
                            }
                        }    // Group change
                    }        // for all columns
                }            // group change

                // new row ---------------------------------------------------

                printRunningTotal( pd,levelNo,rowNo++ );
				// Se comenta la línea siguiente ya que es la responsable del interlineado
				// innecesario de los reportes
                //pd.addRow( false,levelNo );

				// Si es PageBreak, el primer argumanto, is FunctionColumn debe ser true
				pd.addRow(isPageBreak, levelNo, isBold, indentLevel, isPageBreak);

				int counter = 1;

                // get columns

                for( int i = 0;i < pd.getColumnInfo().length;i++ ) {
                    pdc = pd.getColumnInfo()[ i ];

                    PrintDataElement pde = null;

                    // Key Column - No DisplayColumn

                    if( pdc.getAlias().equals( KEY )) {
                        if( pdc.getColumnName().endsWith( "_ID" )) {

                            // int id = rs.getInt(pdc.getColumnIDName());

                            int id = rs.getInt( counter++ );

                            if( !rs.wasNull()) {
                                KeyNamePair pp = new KeyNamePair( id,KEY );    // Key

                                pde = new PrintDataElement( pdc.getColumnName(),pp,pdc.getDisplayType(),true,pdc.isPageBreak());
                            }
                        } else {

                            // String id = rs.getString(pdc.getColumnIDName());

                            String id = rs.getString( counter++ );

                            if( !rs.wasNull()) {
                                ValueNamePair pp = new ValueNamePair( id,KEY );    // Key

                                pde = new PrintDataElement( pdc.getColumnName(),pp,pdc.getDisplayType(),true,pdc.isPageBreak());
                            }
                        }
                    }

                    // Non-Key Column

                    else {

                        // Display and Value Column

                        if( pdc.hasAlias()) {

                            // DisplayColumn first

                            String display = rs.getString( counter++ );

                            if( pdc.getColumnName().endsWith( "_ID" )) {
                                int id = rs.getInt( counter++ );

                                if( (display != null) &&!rs.wasNull()) {
                                    KeyNamePair pp = new KeyNamePair( id,display );

                                    pde = new PrintDataElement( pdc.getColumnName(),pp,pdc.getDisplayType());
                                }
                            } else {
                                String id = rs.getString( counter++ );

                                if( (display != null) &&!rs.wasNull()) {
                                    ValueNamePair pp = new ValueNamePair( id,display );

                                    pde = new PrintDataElement( pdc.getColumnName(),pp,pdc.getDisplayType());
                                }
                            }
                        }

                        // Display Value only

                        else {

                            // Transformation for Booleans

                            if( pdc.getDisplayType() == DisplayType.YesNo ) {
                                String s = rs.getString( counter++ );

                                if( !rs.wasNull()) {
                                    boolean b = s.equals( "Y" );

                                    pde = new PrintDataElement( pdc.getColumnName(),new Boolean( b ),pdc.getDisplayType());
                                }
                            } else if( pdc.getDisplayType() == DisplayType.TextLong ) {
                                Clob   clob  = rs.getClob( counter++ );
                                String value = "";

                                if( clob != null ) {
                                    long length = clob.length();

                                    value = clob.getSubString( 1,( int )length );
                                }

                                pde = new PrintDataElement( pdc.getColumnName(),value,pdc.getDisplayType());
                            } else

                            // The general case

                            {
                                Object obj = rs.getObject( counter++ );

                                if( (obj != null) && (obj instanceof String) ) {
                                    obj = (( String )obj ).trim();

                                    if((( String )obj ).length() == 0 ) {
                                        obj = null;
                                    }
                                }

                                if( obj != null ) {

                                    // Translate Spool Output

                                    if( translateSpool && (obj instanceof String) ) {
                                        String s = ( String )obj;

                                        s = Msg.parseTranslation( pd.getCtx(),s );
                                        if("Description".equals(pdc.getColumnName()) && hasIndentLevel==true && indentLevel>0)
											s=lpad(s, " ", s.length()+(indentLevel*4));
										pde = new PrintDataElement( pdc.getColumnName(),s,pdc.getDisplayType());
                                    } else {
										if("Description".equals(pdc.getColumnName()) && hasIndentLevel==true && indentLevel>0)
											obj=lpad((String)obj, " ", ((String)obj).length()+(indentLevel*4));
										pde = new PrintDataElement( pdc.getColumnName(),obj,pdc.getDisplayType());
                                    }
                                }
                            }
                        }    // Value only
                    }        // Non-Key Column

                    if( pde != null ) {
                        pd.addNode( pde );
                        m_group.addValue( pde.getColumnName(),pde.getFunctionValue());
                    }
                }    // for all columns
            }        // for all rows

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,pdc + " - " + e.getMessage() + "\nSQL=" + pd.getSQL());
        }

        // --      we have all rows - finish
        // Check last Group Change

        if( m_group.getGroupColumnCount() > 1 )    // one is TOTAL
        {
            for( int i = pd.getColumnInfo().length - 1;i >= 0;
                    i-- )                          // backwards (leaset group first)
            {
                PrintDataColumn group_pdc = pd.getColumnInfo()[ i ];

                if( !m_group.isGroupColumn( group_pdc.getColumnName())) {
                    continue;
                }

                Object value = m_group.groupChange( group_pdc.getColumnName(),new Object());

                if( value != null )    // Group change
                {
                    char[] functions = m_group.getFunctions( group_pdc.getColumnName());

                    for( int f = 0;f < functions.length;f++ ) {
                        printRunningTotal( pd,levelNo,rowNo++ );
                        // TODO: COMPROBAR
                        pd.addRow( true,levelNo );
                        //pd.addRow(true, levelNo, isBold, indentLevel);
                        
                        // get columns

                        for( int c = 0;c < pd.getColumnInfo().length;c++ ) {
                            pdc = pd.getColumnInfo()[ c ];

                            if( group_pdc.getColumnName().equals( pdc.getColumnName())) {
                                String valueString = value.toString();

                                if( value instanceof Timestamp ) {
                                    valueString = DisplayType.getDateFormat( pdc.getDisplayType(),m_language ).format( value );
                                }

                                valueString += PrintDataFunction.getFunctionSymbol( functions[ f ] );
                                pd.addNode( new PrintDataElement( pdc.getColumnName(),valueString,DisplayType.String ));
                            } else if( m_group.isFunctionColumn( pdc.getColumnName(),functions[ f ] )) {
                                pd.addNode( new PrintDataElement( pdc.getColumnName(),m_group.getValue( group_pdc.getColumnName(),pdc.getColumnName(),functions[ f ] ),PrintDataFunction.getFunctionDisplayType( functions[ f ] )));
                            }
                        }
                    }    // for all functions

                    // No Need to Reset

                }        // Group change
            }
        }                // last group change

        // Add Total Lines

        if( m_group.isGroupColumn( PrintDataGroup.TOTAL )) {
            char[] functions = m_group.getFunctions( PrintDataGroup.TOTAL );

            for( int f = 0;f < functions.length;f++ ) {
                printRunningTotal( pd,levelNo,rowNo++ );
				// TODO: Comprobar
				pd.addRow(true, levelNo);
				//pd.addRow(true, levelNo, isBold, indentLevel);

                // get columns

                for( int c = 0;c < pd.getColumnInfo().length;c++ ) {
                    pdc = pd.getColumnInfo()[ c ];

                    if( c == 0 )                                                    // put Function in first Column
                    {
                        String name = "";
                        
                        // if( !format.getTableFormat().isPrintFunctionSymbols()) {    // Translate Sum, etc.
                        if( format.getTableFormat().isPrintFunctionSymbols()) {    // Translate Sum, etc.
                            name = Msg.getMsg( format.getLanguage(),PrintDataFunction.getFunctionName( functions[ f ] ));
                            name += PrintDataFunction.getFunctionSymbol( functions[ f ] );    // Symbol
                        }

                        // name += PrintDataFunction.getFunctionSymbol( functions[ f ] );    // Symbol
                        pd.addNode( new PrintDataElement( pdc.getColumnName(),name.trim(),DisplayType.String ));
                    } else if( m_group.isFunctionColumn( pdc.getColumnName(),functions[ f ] )) {
                        pd.addNode( new PrintDataElement( pdc.getColumnName(),m_group.getValue( PrintDataGroup.TOTAL,pdc.getColumnName(),functions[ f ] ),PrintDataFunction.getFunctionDisplayType( functions[ f ] )));
                    }
                }    // for all columns
            }        // for all functions

            // No Need to Reset

        }            // TotalLine

        if( pd.getRowCount() == 0 ) {
            if( CLogMgt.isLevelFiner()) {
                log.warning( "NO Rows - ms=" + ( System.currentTimeMillis() - m_startTime ) + "\nSQL=" + pd.getSQL());
            } else {
                log.warning( "NO Rows - ms=" + ( System.currentTimeMillis() - m_startTime ));
            }
        } else {
            log.info( "Rows=" + pd.getRowCount() + " - ms=" + ( System.currentTimeMillis() - m_startTime ));
        }
    }    // loadPrintData

    /**
     * Descripción de Método
     *
     *
     * @param pd
     * @param levelNo
     * @param rowNo
     */

    private void printRunningTotal( PrintData pd,int levelNo,int rowNo ) {
        if( m_runningTotalLines < 1 ) {    // -1 = none
            return;
        }

        log.fine( "(" + m_runningTotalLines + ") - Row=" + rowNo + ", mod=" + rowNo % m_runningTotalLines );

        if( rowNo % m_runningTotalLines != 0 ) {
            return;
        }

        log.fine( "Row=" + rowNo );

        PrintDataColumn pdc   = null;
        int             start = 0;

        if( rowNo == 0 ) {    // no page break on page 1
            start = 1;
        }

        for( int rt = start;rt < 2;rt++ ) {
            pd.addRow( true,levelNo );

            // get sum columns

            for( int c = 0;c < pd.getColumnInfo().length;c++ ) {
                pdc = pd.getColumnInfo()[ c ];

                if( c == 0 ) {
                    String title = "RunningTotal";

                    pd.addNode( new PrintDataElement( pdc.getColumnName(),title,DisplayType.String,false,rt == 0 ));    // page break
                } else if( m_group.isFunctionColumn( pdc.getColumnName(),PrintDataFunction.F_SUM )) {
                    pd.addNode( new PrintDataElement( pdc.getColumnName(),m_group.getValue( PrintDataGroup.TOTAL,pdc.getColumnName(),PrintDataFunction.F_SUM ),PrintDataFunction.getFunctionDisplayType( PrintDataFunction.F_SUM ),false,false ));
                }
            }    // for all sum columns
        }        // two lines
    }            // printRunningTotal

	public static String lpad(String valueToPad, String filler, int size)
	{  
		while (valueToPad.length() < size)  
			valueToPad = filler + valueToPad;
		
	    return valueToPad;  
	}
	
    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        org.openXpertya.OpenXpertya.startup( true );

        // DataEngine de = new DataEngine(null);

        DataEngine de    = new DataEngine( Language.getLanguage( "de_DE" ));
        MQuery     query = new MQuery();

        query.addRestriction( "AD_Table_ID",MQuery.LESS,105 );

        // PrintData pd = de.load_fromTable(100, query, null, null, false);
        // pd.dump();
        // pd.createXML(new javax.xml.transform.stream.StreamResult(System.out));

    }
}    // DataEngine


/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

class TableReference {

    /** Descripción de Campos */

    public String TableName;

    /** Descripción de Campos */

    public String KeyColumn;

    /** Descripción de Campos */

    public String DisplayColumn;

    /** Descripción de Campos */

    public boolean IsValueDisplayed = false;

    /** Descripción de Campos */

    public boolean IsTranslated = false;
}    // TableReference



/*
 *  @(#)DataEngine.java   23.03.06
 * 
 *  Fin del fichero DataEngine.java
 *  
 *  Versión 2.2
 *
 */
