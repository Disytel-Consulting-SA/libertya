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

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AD_Column_Sync extends SvrProcess {

    /** Descripción de Campos */



    /** Descripción de Campos */

    String v_ResultStr = null;

    /** Descripción de Campos */

    String v_Message = null;

    /** Descripción de Campos */

    int v_Result = 1;    // 0=failure

    /** Descripción de Campos */

    int v_Record_ID = 0;

    /** Descripción de Campos */

    int v_AD_User_ID = 0;

    /** Descripción de Campos */

    String v_TableName = null;

    /** Descripción de Campos */

    String v_ColumnName = null;

    /** Descripción de Campos */

    int v_AD_Reference_ID = 0;

    /** Descripción de Campos */

    int v_FieldLength = 0;

    /** Descripción de Campos */

    String v_DefaultValue = null;

    /** Descripción de Campos */

    boolean v_IsMandatory = false;

    /** Descripción de Campos */

    String v_DB_DataType = null;

    /** Descripción de Campos */

    String v_CMD = null;

    /** Descripción de Campos */

    String v_DB_TableName = null;

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
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        v_Record_ID = getRecord_ID();
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
        StringBuffer sql = new StringBuffer( "SELECT     t.TableName, c.ColumnName, c.AD_Reference_ID, c.FieldLength, c.DefaultValue, c.IsMandatory FROM AD_Table t, AD_Column c WHERE   t.AD_Table_ID = c.AD_Table_ID AND c.AD_Column_ID = ?" );

        log.finest( sql.toString());

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
            pstmt.setInt( 1,v_Record_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                v_TableName       = rs.getString( 1 );
                v_ColumnName      = rs.getString( 2 );
                v_AD_Reference_ID = rs.getInt( 3 );
                v_FieldLength     = rs.getInt( 4 );
                v_DefaultValue    = rs.getString( 5 );
                v_IsMandatory     = (rs.getString( 6 ).compareTo( "Y" ) == 0)
                                    ?true
                                    :false;
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        DatabaseMetaData md = DB.getConnectionRO().getMetaData();

        // find v_DB_TableName <> null

        ResultSet rsTables = md.getTables( md.getConnection().getCatalog(),"openXpertya","%",new String[]{ "TABLE" } );

        if( rsTables != null ) {
            while( rsTables.next()) {
                if( rsTables.getString( 3 ).toUpperCase().compareTo( v_TableName.toUpperCase()) == 0 ) {
                    v_DB_TableName = rsTables.getString( 3 ).toUpperCase();
                }
            }
        }

        rsTables.close();

        if( v_DB_TableName == null ) {
            log.info( "Create Table Command" );
            v_CMD = "CREATE TABLE " + v_TableName.toUpperCase() + "(XXXX CHAR(1))";

            try {
                DB.executeUpdate( v_CMD );
            } catch( Exception e ) {
                log.log( Level.SEVERE,"Error: " + v_CMD,e );
            }
        }

        try {
            System.out.println( "Table" + v_TableName.toUpperCase());
            md = DB.getConnectionRO().getMetaData();

            // System.out.println("getCatalog():" + md.getConnection().getCatalog());
            // ResultSet sourceColumns = md.getColumns(md.getConnection().getCatalog(), null , v_TableName.toUpperCase(), null);

            ResultSet sourceColumns = md.getColumns( md.getConnection().getCatalog(),"openXpertya",v_TableName.toLowerCase(),"%" );

            while( sourceColumns.next()) {

                // System.out.println("ResultSet sourceColumns");

                String columnName = sourceColumns.getString( "COLUMN_NAME" );

                // System.out.println("Metadata" + columnName + "Internal" + v_ColumnName);
                // Data Type & Precision

                if( columnName.toUpperCase().compareTo( v_ColumnName.toUpperCase()) == 0 ) {

                    // v_DB_DataType = sourceColumns.getInt ("DATA_TYPE");           //      sql.Types

                    v_DB_DataType = sourceColumns.getString( "TYPE_NAME" );

                    break;

                    // System.out.println("e-evolution --------------" + v_DB_DataType);
                    // String typeName = sourceColumns.getString ("TYPE_NAME");      //      DB Dependent
                    // int size = sourceColumns.getInt ("COLUMN_SIZE");
                    // int decDigits = sourceColumns.getInt("DECIMAL_DIGITS");
                    // if (sourceColumns.wasNull())
                    // decDigits = -1;

                }
            }    // for all columns

            sourceColumns.close();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"createTable",ex );

            return "";
        }

        // Create Statement

        if( v_DB_DataType == null ) {
            log.info( "Create ALTER Command" );

            // Get TableName

            v_CMD = "ALTER TABLE " + v_TableName.toUpperCase() + " ADD " + v_ColumnName + " ";

            // Map Data Type

            if( (v_AD_Reference_ID == 10) || (v_AD_Reference_ID == 11) || (v_AD_Reference_ID == 12) || (v_AD_Reference_ID == 13) || (v_AD_Reference_ID == 14) ) {

                // String, Text

                v_CMD = v_CMD + "NVARCHAR2(" + v_FieldLength + ')';
            } else if( (v_AD_Reference_ID == 17) || (v_AD_Reference_ID == 20) || (v_AD_Reference_ID == 28) ) {

                // List,YesNo,Button

                v_CMD = v_CMD + "CHAR(" + v_FieldLength + ")";
            } else if( (v_AD_Reference_ID == 13) || (v_AD_Reference_ID == 18) || (v_AD_Reference_ID == 19) || (v_AD_Reference_ID == 21) || (v_AD_Reference_ID == 25) || (v_AD_Reference_ID == 27) || (v_AD_Reference_ID == 30) || (v_AD_Reference_ID == 31) ) {

                // ID,Table,TableDir,Location,Account,Color,Search,Locator

                v_CMD = v_CMD + "NUMBER(10)";
            } else if( (v_AD_Reference_ID == 11) || (v_AD_Reference_ID == 12) || (v_AD_Reference_ID == 22) || (v_AD_Reference_ID == 29) ) {    // Integer,Amount,Number,Quantity
                v_CMD = v_CMD + "NUMBER";
            } else if( (v_AD_Reference_ID == 15) || (v_AD_Reference_ID == 16) ) {    // Date,DateTime
                v_CMD = v_CMD + "DATE";
            } else {

                // 23-Binary, 24-Radio, 26-RowID, 32-Image

                return "DisplayType Not Supported";
            }

            // Default (literal)

            if( (v_DefaultValue != null) && (v_DefaultValue.length() != 0) ) {
                if( (v_AD_Reference_ID == 10) || (v_AD_Reference_ID == 14) || (v_AD_Reference_ID == 17) || (v_AD_Reference_ID == 20) || (v_AD_Reference_ID == 28) ) {
                    v_CMD = v_CMD + " DEFAULT ('" + v_DefaultValue + "')";
                } else {
                    v_CMD = v_CMD + " DEFAULT " + v_DefaultValue;
                }
            }

            // Mandatory

            if( v_IsMandatory ) {
                if( (v_DefaultValue == null) || (v_DefaultValue.length() == 0) ) {
                    return "Mandatory requites literal default value";
                } else {
                    v_CMD = v_CMD + " NOT NULL";
                }
            }

            try {
                DB.executeUpdate( v_CMD,false,get_TrxName());

                return "@Created@ - " + v_CMD;
            } catch( Exception ex ) {
                log.log( Level.SEVERE,"Error Command: " + v_CMD,ex );

                return "Error Command: " + v_CMD;
            }
        } else {
            log.info( "CreateALTERCommand" );

            // Get TableName

            if( DB.isOracle()) {
                v_CMD = "ALTER TABLE " + v_TableName + " MODIFY " + v_ColumnName + " ";
            }

            if( DB.isPostgreSQL()) {
                v_CMD = "ALTER TABLE " + v_TableName + " ALTER COLUMN " + v_ColumnName + " TYPE ";
            }

            // Map Data Type

            if( (v_AD_Reference_ID == 10) || (v_AD_Reference_ID == 14) ) {    // String, Text
                v_CMD = v_CMD + "NVARCHAR2(" + v_FieldLength + ")";
            } else if( (v_AD_Reference_ID == 17) || (v_AD_Reference_ID == 20) || (v_AD_Reference_ID == 28) ) {

                // List,YesNo,Button

                v_CMD = v_CMD + "CHAR(" + v_FieldLength + ")";
            } else if( (v_AD_Reference_ID == 13) || (v_AD_Reference_ID == 18) || (v_AD_Reference_ID == 19) || (v_AD_Reference_ID == 21) || (v_AD_Reference_ID == 25) || (v_AD_Reference_ID == 27) || (v_AD_Reference_ID == 30) || (v_AD_Reference_ID == 31) ) {    // ID,Table,TableDir,Location,Account,Color,Search,Locator
                v_CMD = v_CMD + "NUMBER(10)";
            } else if( (v_AD_Reference_ID == 11) || (v_AD_Reference_ID == 12) || (v_AD_Reference_ID == 22) || (v_AD_Reference_ID == 29) ) {    // Integer,Amount,Number,Quantity
                v_CMD = v_CMD + "NUMBER";
            } else if( (v_AD_Reference_ID == 15) || (v_AD_Reference_ID == 16) ) {    // Date,DateTime
                v_CMD = v_CMD + "DATE";
            } else {

                // 23-Binary, 24-Radio, 26-RowID, 32-Image

                return "DisplayType Not Supported";
            }

            // Default (literal)

            if( (v_DefaultValue != null) && (v_DefaultValue.length() != 0) ) {
                if( (v_AD_Reference_ID == 10) || (v_AD_Reference_ID == 14) || (v_AD_Reference_ID == 17) || (v_AD_Reference_ID == 20) || (v_AD_Reference_ID == 28) ) {
                    v_CMD = v_CMD + " DEFAULT ('" + v_DefaultValue + "')";
                } else {
                    v_CMD = v_CMD + " DEFAULT " + v_DefaultValue;
                }
            }

            // Mandatory

            if( v_IsMandatory ) {
                if( (v_DefaultValue == null) || (v_DefaultValue.length() == 0) ) {
                    return "Mandatory requites literal default value";
                } else {
                    v_CMD = v_CMD + " NOT NULL";
                }
            }

            try {
                DB.executeUpdate( v_CMD,false,get_TrxName());

                return "@Updated@ - " + v_CMD;
            } catch( Exception ex ) {
                log.log( Level.SEVERE,"Error Command: " + v_CMD,ex );

                return "Error Command: " + v_CMD;
            }

            /*
             * --      Table did not exist - drop initial column
             * IF (v_DB_TableName IS NULL) THEN
             *       v_ResultStr := 'CreateDropXXColumnCommand';
             *       BEGIN
             *               v_CMD := 'ALTER TABLE ' || v_TableName || ' DROP COLUMN XXXX';
             *               EXECUTE IMMEDIATE v_Cmd;
             *       EXCEPTION
             *               WHEN OTHERS THEN
             *                       v_Result := 0;  -- failure
             *                       v_Message := 'Error: ' || SQLERRM || ' - Command: ' || v_Cmd;
             *       END;
             * END IF;
             */

        }
    }    // doIt
}    // WindowCopy



/*
 *  @(#)AD_Column_Sync.java   02.07.07
 * 
 *  Fin del fichero AD_Column_Sync.java
 *  
 *  Versión 2.2
 *
 */
