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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.logging.Level;

import org.openXpertya.db.BaseDatosOXP;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Element;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.ErrorOXPSystem;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TableCreateColumns extends SvrProcess {

    /** Descripción de Campos */

    private String p_EntityType = M_Element.ENTITYTYPE_Customization;

    /** Descripción de Campos */

    private int p_AD_Table_ID = 0;

    /** Descripción de Campos */

    private boolean p_AllTables = false;

    /** Descripción de Campos */

    private int m_count = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) 
            {
                ;
            } 
            else if( name.equals( "EntityType" )) 
            {
                p_EntityType = ( String )para[ i ].getParameter();
            } 
            else if( name.equals( "AllTables" )) 
            {
                p_AllTables = "Y".equals( para[ i ].getParameter());
            } 
            else 
            {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_AD_Table_ID = getRecord_ID();
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
        if( p_AD_Table_ID == 0 ) {
            throw new ErrorOXPSystem( "@NotFound@ @AD_Table_ID@ " + p_AD_Table_ID );
        }

        log.info( "EntityType=" + p_EntityType + ", AllTables=" + p_AllTables + ", AD_Table_ID" + p_AD_Table_ID );

        //

        Connection       conn    = DB.getConnectionRO();
        BaseDatosOXP     db      = DB.getDatabase();
        DatabaseMetaData md      = conn.getMetaData();
        String           catalog = db.getCatalog();
        String           schema  = db.getSchema();

        if( p_AllTables ) {
            addTable( md,catalog,schema );
        } else {
            M_Table table = new M_Table( getCtx(),p_AD_Table_ID,get_TrxName());

            if( (table == null) || (table.getID() == 0) ) {
                throw new ErrorOXPSystem( "@NotFound@ @AD_Table_ID@ " + p_AD_Table_ID );
            }

            log.info( table.getTableName() + ", EntityType=" + p_EntityType );

            String tableName = table.getTableName();

            if( DB.isOracle()) {
                tableName = tableName.toUpperCase();
            }
            //Modificado por Dataware S.L.
            else if( DB.isPostgreSQL() ) {
            	tableName = tableName.toLowerCase();
            }

            ResultSet rs = md.getColumns( catalog,schema,tableName,null );

            addTableColumn( rs,table );
        }

        return "#" + m_count;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param md
     * @param catalog
     * @param schema
     *
     * @throws Exception
     */

    private void addTable( DatabaseMetaData md,String catalog,String schema ) throws Exception {
        ResultSet rs = md.getTables( catalog,schema,null,null );

        while( rs.next()) {
            String tableName = rs.getString( "TABLE_NAME" );
            String tableType = rs.getString( "TABLE_TYPE" );

            // Try to find

            M_Table table = M_Table.get( getCtx(),tableName );

            // Create new ?

            if( table == null ) {
                String tn = tableName.toUpperCase();

                if( tn.startsWith( "T_SELECTION" )                 // temp table
                        || tn.endsWith( "_VT" )                    // print trl views
                        || tn.endsWith( "_V" )                     // views
                        || tn.endsWith( "_V1" )                    // views
                        || tn.startsWith( "A_A" )                  // asset tables not yet
                        || tn.startsWith( "A_D" )                  // asset tables not yet
                        || (tn.indexOf( "$" ) != -1                // oracle system tables
                            ) || (tn.indexOf( "EXPLAIN" ) != -1    // explain plan
                                ) ) {
                    log.fine( "Ignored: " + tableName + " - " + tableType );

                    continue;
                }

                //

                log.info( tableName + " - " + tableType );

                // Create New

                table = new M_Table( getCtx(),0,get_TrxName());
                table.setEntityType( p_EntityType );
                table.setName( tableName );
                table.setTableName( tableName );
                table.setIsView( "VIEW".equals( tableType ));

                if( !table.save()) {
                    continue;
                }
            }

            // Check Columns

            if( DB.isOracle()) {
                tableName = tableName.toUpperCase();
            }

            ResultSet rsC = md.getColumns( catalog,schema,tableName,null );

            addTableColumn( rsC,table );
        }
    }    // addTable

    /**
     * Descripción de Método
     *
     *
     * @param rs
     * @param table
     *
     * @throws Exception
     */

    private void addTableColumn( ResultSet rs,M_Table table ) throws Exception {
        String tableName = table.getTableName();

        if( DB.isOracle()) {
            tableName = tableName.toUpperCase();
        }
        //Modificado por Dataware S.L.
        else if( DB.isPostgreSQL() ) {
        	tableName = tableName.toLowerCase();
        }

        //

        while( rs.next()) {
            String tn = rs.getString( "TABLE_NAME" );

            if( !tableName.equalsIgnoreCase( tn )) {
                continue;
            }

            String   columnName = rs.getString( "COLUMN_NAME" );
            M_Column column     = table.getColumn( columnName );

            if( column != null ) {
                continue;
            }

            int    dataType = rs.getInt( "DATA_TYPE" );
            String typeName = rs.getString( "TYPE_NAME" );
            String nullable = rs.getString( "IS_NULLABLE" );
            int    size     = rs.getInt( "COLUMN_SIZE" );
            int    digits   = rs.getInt( "DECIMAL_DIGITS" );

            //

            log.config( columnName + " - DataType=" + dataType + " " + typeName + ", Nullable=" + nullable + ", Size=" + size + ", Digits=" + digits );

            //

            column = new M_Column( table );
            column.setEntityType( p_EntityType );

            //

            M_Element element = M_Element.get( getCtx(),columnName );

            if( element == null ) {
                element = new M_Element( getCtx(),columnName,p_EntityType,get_TrxName());
                element.save();
            }

            column.setColumnName( element.getColumnName());
            column.setName( element.getName());
            column.setDescription( element.getDescription());
            column.setHelp( element.getHelp());
            column.setAD_Element_ID( element.getAD_Element_ID());

            //

            column.setIsMandatory( "NO".equals( nullable ));

            // Key

            if( columnName.equalsIgnoreCase( tableName + "_ID" )) {
                column.setIsKey( true );
                column.setAD_Reference_ID( DisplayType.ID );
                column.setIsUpdateable( false );
            }

            // Account

            else if( (columnName.toUpperCase().indexOf( "ACCT" ) != -1) && (size == 10) ) {
                column.setAD_Reference_ID( DisplayType.Account );

                // Account

            } else if( columnName.equalsIgnoreCase( "C_Location_ID" )) {
                column.setAD_Reference_ID( DisplayType.Location );

                // Product Attribute

            } else if( columnName.equalsIgnoreCase( "M_AttributeSetInstance_ID" )) {
                column.setAD_Reference_ID( DisplayType.PAttribute );

                // SalesRep_ID (=User)

            } else if( columnName.equalsIgnoreCase( "SalesRep_ID" )) {
                column.setAD_Reference_ID( DisplayType.Table );
                column.setAD_Reference_Value_ID( 190 );
            
            // Si es AD_Client_ID o AD_Org_ID los hago TaleDir
            } else if(columnName.equalsIgnoreCase("AD_Client_ID") || columnName.equalsIgnoreCase("AD_Org_ID")) {
                column.setAD_Reference_ID( DisplayType.TableDir );
                    
            // Si termina en _id lo paso a entero
            } else if( columnName.toLowerCase().endsWith( "_id" )) {
                column.setAD_Reference_ID( DisplayType.Integer );

            // Date
            } else if( (dataType == Types.DATE) || (dataType == Types.TIME) || (dataType == Types.TIMESTAMP)

            // || columnName.toUpperCase().indexOf("DATE") != -1

            || columnName.equalsIgnoreCase( "Created" ) || columnName.equalsIgnoreCase( "Updated" )) {
                column.setAD_Reference_ID( DisplayType.DateTime );

                // CreatedBy/UpdatedBy (=User)

            } else if( columnName.equalsIgnoreCase( "CreatedBy" ) || columnName.equalsIgnoreCase( "UpdatedBy" )) {
                column.setAD_Reference_ID( DisplayType.Table );
                column.setAD_Reference_Value_ID( 110 );
                column.setIsUpdateable( false );
            }

            // CLOB

            else if( dataType == Types.CLOB ) {
                column.setAD_Reference_ID( DisplayType.TextLong );

                // BLOB

            } else if( dataType == Types.BLOB ) {
                column.setAD_Reference_ID( DisplayType.Binary );

                // Amount

            } else if( columnName.toUpperCase().indexOf( "AMT" ) != -1 ) {
                column.setAD_Reference_ID( DisplayType.Amount );

                // Qty

            } else if( columnName.toUpperCase().indexOf( "QTY" ) != -1 ) {
                column.setAD_Reference_ID( DisplayType.Quantity );

                // Boolean

            } else if( (size == 1) && ( columnName.toUpperCase().startsWith( "IS" ) || (dataType == Types.CHAR) ) ) {
                column.setAD_Reference_ID( DisplayType.YesNo );

                // List

            } else if( (size < 4) && (dataType == Types.CHAR) ) {
                column.setAD_Reference_ID( DisplayType.List );

                // Name, DocumentNo

            } else if( columnName.equalsIgnoreCase( "Name" ) || columnName.equals( "DocumentNo" )) {
                column.setAD_Reference_ID( DisplayType.String );
                column.setIsIdentifier( true );
                column.setSeqNo( 1 );
            }

            // String, Text

            else if( (dataType == Types.CHAR) || (dataType == Types.VARCHAR) || typeName.startsWith( "NVAR" ) || typeName.startsWith( "NCHAR" )) {
                if( typeName.startsWith( "N" )) {    // MultiByte
                    size /= 2;
                }

                if( size > 255 ) {
                    column.setAD_Reference_ID( DisplayType.Text );
                } else {
                    column.setAD_Reference_ID( DisplayType.String );
                }
            }

            // Number

            else if( (dataType == Types.INTEGER) || (dataType == Types.SMALLINT) || (dataType == Types.DECIMAL) || (dataType == Types.NUMERIC) ) {
                if( size == 10 ) {
                    column.setAD_Reference_ID( DisplayType.Integer );
                } else {
                    column.setAD_Reference_ID( DisplayType.Number );
                }
            }

            // ??

            else {
                column.setAD_Reference_ID( DisplayType.String );
            }

            column.setFieldLength( size );

            if( column.isUpdateable() && ( columnName.equalsIgnoreCase( "AD_Client_ID" ) || columnName.equalsIgnoreCase( "AD_Org_ID" ) || columnName.toUpperCase().startsWith( "CREATED" ) || columnName.toUpperCase().equals( "UPDATED" ))) {
                column.setIsUpdateable( false );
            }

            // Done

            if( column.save()) {
                addLog( 0,null,null,table.getTableName() + "." + column.getColumnName());
                m_count++;
            }
        }    // while columns
    }        // addTableColumn
}    // TableCreateColumns



/*
 *  @(#)TableCreateColumns.java   02.07.07
 * 
 *  Fin del fichero TableCreateColumns.java
 *  
 *  Versión 2.2
 *
 */
