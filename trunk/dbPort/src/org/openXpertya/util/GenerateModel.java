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



package org.openXpertya.util;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.util.Vector;
import java.util.logging.*;
import java.util.logging.*;

import org.openXpertya.*;
import org.openXpertya.model.*;
import org.openXpertya.plugin.common.PluginConstants;


/**
 * Descripción de Clase
 * 
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de Libertya
 *
 *
 */

public class GenerateModel {

    /**
     * Constructor de la clase ...
     *
     *
     * @param AD_Table_ID
     * @param directory
     * @param packageName
     */
	
	private final static String PO_CLASS_NAME = "org.openXpertya.model.PO";
	
    public GenerateModel( int AD_Table_ID,String directory,String packageName ) {

    	// Determinar el corelevel de la tabla (es un plugin? es del core?)
    	// Las tablas generales (transaccionales, como c_invoice, c_order, etc.) no tienen definido un coreLevel    	
    	Integer coreLevelTable = coreLevelTable(AD_Table_ID);
    	
    	/** ES UNA TABLA DE SYSTEM CORE O TABLA GENERAL? */
    	if (coreLevelTable == null || coreLevelTable == 0)
    	{
	        String fileName = createX(AD_Table_ID, directory, packageName, true, null, false, "org.openXpertya.model.PO" );
	        
	        /** CREAR LA CLASE LP_ CON LOS METODOS EXTRA DE CADA PLUGIN (CASO: TABLA DE CORE, CON CAMPOS EXTRA DE PLUGIN) */
	        Vector<Integer> lPlugins = getLPlugins(AD_Table_ID);
	        for (Integer componentID : lPlugins)
	        {
	        	String pluginPackageName = DB.getSQLValueString(null, " SELECT packagename FROM AD_Component WHERE AD_Component_ID = ? ", componentID) + "." + PluginConstants.PACKAGE_NAME_MODEL;
		        createX(AD_Table_ID, directory, pluginPackageName, false, componentID, true, getCoreSuperClass(AD_Table_ID) ); // packageName + "." + fileName
	        }
	        
    	}
    	else	/** ES UNA TABLA DE PLUGIN */
    	{
    		String pluginTablePackageName = getPluginPackageName(AD_Table_ID) + "." + PluginConstants.PACKAGE_NAME_MODEL;
    		String fileName = createX(AD_Table_ID, directory, pluginTablePackageName, false, null, true, "org.openXpertya.model.PO" );
	        
	        /** CREAR LA CLASE LP_ CON LOS METODOS EXTRA DE CADA PLUGIN (CASO: TABLA DE PLUGIN, CON CAMPOS EXTRA DE OTRO PLUGIN) */
	        Vector<Integer> lPlugins = getLPlugins(AD_Table_ID);
	        for (Integer componentID : lPlugins)
	        {
	        	String pluginPackageName = DB.getSQLValueString(null, " SELECT packagename FROM AD_Component WHERE AD_Component_ID = ? ", componentID) + "." + PluginConstants.PACKAGE_NAME_MODEL;
	        	if (pluginPackageName.equals(pluginTablePackageName))
	        		continue;
		        createX(AD_Table_ID, directory, pluginPackageName, false, componentID, true, fileName );
	        }
	        
    	}
    	

    }    // GenerateModel

    /** Descripción de Campos */

    private Timestamp s_run = new Timestamp( System.currentTimeMillis());

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( GenerateModel.class );

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param sb
     * @param mandatory
     * @param packageName
     *
     * @return
     */

    private String createHeader( int AD_Table_ID,StringBuffer sb,StringBuffer mandatory,String packageName, boolean isPluginHeader, String superClass ) {
        String tableName   = "";
        int    accessLevel = 0;
        String sql         = "SELECT TableName, AccessLevel FROM AD_Table WHERE AD_Table_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                tableName   = rs.getString( 1 );
                accessLevel = rs.getInt( 2 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        if( tableName == null ) {
            throw new RuntimeException( "TableName not found for ID=" + AD_Table_ID );
        }

        //

        String keyColumn = tableName + "_ID";
        String className = ( isPluginHeader ? PluginConstants.LIBERTYA_PLUGIN_PREFIX : "X_") + tableName;

        //

        StringBuffer start = new StringBuffer().append( "/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */\n" + "package " + packageName + ";" );

        if( !packageName.equals( "org.openXpertya.model" )) {
            start.append( "import org.openXpertya.model.*;" );
        }

        start.append( "import java.util.logging.Level; import java.util.*;" + "import java.sql.*;" + "import java.math.*;" + "import org.openXpertya.util.*;"

        // Class

        + "/** Modelo Generado por " ).append( tableName ).append( "\n" + " *  @author Comunidad de Desarrollo Libertya" + "*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:" + "*         * Jorg Janke \n" + " *  @version " ).append( " - " ).append( s_run ).append( " */\n" + "public class " ).append( className ).append( " extends " + superClass + "{"

        // Standard Constructor

        + "/** Constructor estándar */\n" + "public " ).append( className ).append( " (Properties ctx, int " ).append( keyColumn ).append( ", String trxName)" + "{" + "super (ctx, " ).append( keyColumn ).append( ", trxName);" + "/** if (" ).append( keyColumn ).append( " == 0)" + "{" ).append( mandatory ).append( "} */\n" + "}"    // Constructor End

        // Short Constructor
//                      + "/** Short Constructor */\n"
//                      + "public ").append(className).append(" (Properties ctx, int ").append(keyColumn).append(")"
//                      + "{"
//                      + "this (ctx, ").append(keyColumn).append(", null);"
//                      + "}"   //      Constructor End

        // Load Constructor

        + "/** Load Constructor */\n" + "public " ).append( className ).append( " (Properties ctx, ResultSet rs, String trxName)" + "{" + "super (ctx, rs, trxName);" + "}"    // Load Constructor End

        // Identificacion general de clase segun identificador
       		
        + (extendsAnotherX(superClass) ? "" : "/** AD_Table_ID" + " */\n" + "public static final int Table_ID = M_Table.getTableID(\"" + tableName + "\");\n" + "/** TableName=" + tableName + " */\n" + "public static final String Table_Name=\"" + tableName + "\";\n" + "protected static KeyNamePair Model = new KeyNamePair(Table_ID,\"" + tableName + "\");" + "protected static BigDecimal AccessLevel = new BigDecimal(" + accessLevel + ");\n" + "/** Load Meta Data */\n" + "protected POInfo initPO (Properties ctx)" + "{" + "POInfo poi = POInfo.getPOInfo (ctx, Table_ID);" + "return poi;" + "}" )   // initPO

        // toString

        + "public String toString()" + "{" + "StringBuffer sb = new StringBuffer (\"" ).append( className ).append( "[\")" + ".append(getID()).append(\"]\");" + "return sb.toString();" + "}" );

        StringBuffer end = new StringBuffer( "}" );

        sb.insert( 0,start );
        sb.append( end );

        return ( isPluginHeader ? (packageName + ".") : "" ) + className;
    }    // createHeader

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param mandatory
     *
     * @return
     */

    private StringBuffer createColumns( int AD_Table_ID,StringBuffer mandatory, boolean coreLevelZero, Integer ad_component_id ) {
        StringBuffer sb  = new StringBuffer();
        String       sql = "SELECT c.ColumnName, c.IsUpdateable, c.IsMandatory,"    // 1..3
                           + " c.AD_Reference_ID, c.AD_Reference_Value_ID, DefaultValue, SeqNo, "    // 4..7
                           + " c.FieldLength, c.ValueMin, c.ValueMax, c.VFormat, c.Callout, "    // 8..12
                           + " c.Name, c.Description, c.ColumnSQL, c.IsEncrypted " + "FROM AD_Column c " 
                           + " INNER JOIN ad_componentversion cv on cv.ad_componentversion_id = c.ad_componentversion_id "
                           + " INNER JOIN ad_component co on co.ad_component_id = cv.ad_component_id "
                           + " WHERE c.AD_Table_ID=?" + " AND c.ColumnName <> 'AD_Client_ID'" + " AND c.ColumnName <> 'AD_Org_ID'" + " AND c.ColumnName <> 'IsActive'" + " AND c.ColumnName NOT LIKE 'Created%'" + " AND c.ColumnName NOT LIKE 'Updated%' " 
                           + (coreLevelZero ? " AND co.corelevel = 0 " : " AND co.corelevel > 0 ")
                           + (ad_component_id == null ? "" : " AND co.ad_component_id = " + ad_component_id)
                           + " ORDER BY c.ColumnName";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                String  columnName            = rs.getString( 1 );
                boolean isUpdateable          = "Y".equals( rs.getString( 2 ));
                boolean isMandatory           = "Y".equals( rs.getString( 3 ));
                int     displayType           = rs.getInt( 4 );
                int     AD_Reference_Value_ID = rs.getInt( 5 );
                String  defaultValue          = rs.getString( 6 );
                int     seqNo                 = rs.getInt( 7 );
                int     fieldLength           = rs.getInt( 8 );
                String  ValueMin              = rs.getString( 9 );
                String  ValueMax              = rs.getString( 10 );
                String  VFormat               = rs.getString( 11 );
                String  Callout               = rs.getString( 12 );
                String  Name                  = rs.getString( 13 );
                String  Description           = rs.getString( 14 );
                String  ColumnSQL             = rs.getString( 15 );
                boolean virtualColumn         = (ColumnSQL != null) && (ColumnSQL.length() > 0);
                boolean IsEncrypted = "Y".equals( rs.getString( 16 ));

                //

                sb.append( createColumnMethods( mandatory,columnName,isUpdateable,isMandatory,displayType,AD_Reference_Value_ID,fieldLength,defaultValue,ValueMin,ValueMax,VFormat,Callout,Name,Description,virtualColumn,IsEncrypted ));

                //

                if( seqNo == 1 ) {
                    sb.append( createKeyNamePair( columnName,displayType ));
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return sb;
    }    // createColumns

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     * @param columnName
     * @param isUpdateable
     * @param isMandatory
     * @param displayType
     * @param AD_Reference_ID
     * @param fieldLength
     * @param defaultValue
     * @param ValueMin
     * @param ValueMax
     * @param VFormat
     * @param Callout
     * @param Name
     * @param Description
     * @param virtualColumn
     * @param IsEncrypted
     *
     * @return
     */

    private String createColumnMethods( StringBuffer mandatory,String columnName,boolean isUpdateable,boolean isMandatory,int displayType,int AD_Reference_ID,int fieldLength,String defaultValue,String ValueMin,String ValueMax,String VFormat,String Callout,String Name,String Description,boolean virtualColumn,boolean IsEncrypted ) {
        Class clazz = DisplayType.getClass( displayType,true );

        if( defaultValue == null ) {
            defaultValue = "";
        }

        // Handle Posted

        if( columnName.equalsIgnoreCase( "Posted" ) || columnName.equalsIgnoreCase( "Processed" ) || columnName.equalsIgnoreCase( "Processing" )) {
            clazz           = Boolean.class;
            AD_Reference_ID = 0;
        }

        // Record_ID

        else if( columnName.equalsIgnoreCase( "Record_ID" )) {
            clazz           = Integer.class;
            AD_Reference_ID = 0;
        }

        // String Key

        else if( columnName.equalsIgnoreCase( "AD_Language" )) {
            clazz = String.class;
        }

        // Data Type

        String dataType = clazz.getName();

        dataType = dataType.substring( dataType.lastIndexOf( '.' ) + 1 );

        if( dataType.equals( "Boolean" )) {
            dataType = "boolean";
        } else if( dataType.equals( "Integer" )) {
            dataType = "int";
        } else if( (displayType == DisplayType.Binary) || (displayType == DisplayType.Image) ) {
            dataType = "byte[]";
        }

        StringBuffer sb = new StringBuffer();

        // ****** Set Comment ******

        sb.append( "/** Set " ).append( Name );

        if( (Description != null) && (Description.length() > 0) ) {
            sb.append( ".\n" ).append( Description );
        }

        sb.append( " */\n" );

        // Set     ********

        String setValue = "set_Value";

        if( IsEncrypted ) {
            setValue = "set_ValueE";
        }

        // public void setColumn (xxx variable)

        sb.append( "public " );

        if( !isUpdateable ) {
            setValue = "set_ValueNoCheck";

            if( IsEncrypted ) {
                setValue = "set_ValueNoCheckE";
            }
        }

        sb.append( "void set" ).append( columnName ).append( " (" ).append( dataType ).append( " " ).append( columnName ).append( ")" + "{" );

        // List Validation

        if( AD_Reference_ID != 0 ) {
            String staticVar = addListValidation( sb, AD_Reference_ID, MReference.getReferenceName(AD_Reference_ID), columnName,!isMandatory );

            sb.insert( 0,staticVar );
        }

        // setValue ("ColumnName", xx);

        if( virtualColumn ) {
            sb.append( "throw new IllegalArgumentException (\"" ).append( columnName ).append( " is virtual column\");" );
        } else if( clazz.equals( Integer.class )) {
            if( columnName.endsWith( "_ID" )) {
                if( !isMandatory ) {    // set optional _ID to null if 0
                    sb.append( "if (" ).append( columnName ).append( " <= 0) " ).append( setValue ).append( " (\"" ).append( columnName ).append( "\", null); else \n" );
                }
            }

            sb.append( setValue ).append( " (\"" ).append( columnName ).append( "\", new Integer(" ).append( columnName ).append( "));" );
        } else if( clazz.equals( Boolean.class )) {
            sb.append( setValue ).append( " (\"" ).append( columnName ).append( "\", new Boolean(" ).append( columnName ).append( "));" );
        } else {
            if( isMandatory ) {    // does not apply to int/boolean
                sb.append( "if (" ).append( columnName ).append( " == null)" + " throw new IllegalArgumentException (\"" ).append( columnName ).append( " is mandatory\");" );
            }

            // String length check

            if( clazz.equals( String.class ) && (fieldLength > 0) ) {
                sb.append( "if (" );

                if( !isMandatory ) {
                    sb.append( columnName ).append( " != null && " );
                }

                sb.append( columnName ).append( ".length() > " ).append( fieldLength ).append( "){log.warning(\"Length > " ).append( fieldLength ).append( " - truncated\");" ).append( columnName ).append( " = " ).append( columnName ).append( ".substring(0," ).append( fieldLength ).append( ");}" );
            }

            //

            sb.append( setValue ).append( " (\"" ).append( columnName ).append( "\", " ).append( columnName ).append( ");" );
        }

        sb.append( "}" );

        // Mandatory call in constructor

        if( isMandatory ) {
            mandatory.append( "set" ).append( columnName ).append( " (" );

            if( clazz.equals( Integer.class )) {
                mandatory.append( "0" );
            } else if( clazz.equals( Boolean.class )) {
                if( defaultValue.indexOf( 'Y' ) != -1 ) {
                    mandatory.append( true );
                } else {
                    mandatory.append( "false" );
                }
            } else if( clazz.equals( BigDecimal.class )) {
                mandatory.append( "Env.ZERO" );
            } else if( clazz.equals( Timestamp.class )) {
                mandatory.append( "new Timestamp(System.currentTimeMillis())" );
            } else {
                mandatory.append( "null" );
            }

            mandatory.append( ");" );

            if( defaultValue.length() > 0 ) {
                mandatory.append( "// " ).append( defaultValue ).append( Env.NL );
            }
        }

        // ****** Get Comment ******

        sb.append( "/** Get " ).append( Name );

        if( (Description != null) && (Description.length() > 0) ) {
            sb.append( ".\n" ).append( Description );
        }

        sb.append( " */\n" );

        // Get     ********

        String getValue = "get_Value";

        if( IsEncrypted ) {
            getValue = "get_ValueE";
        }

        sb.append( "public " ).append( dataType );

        if( clazz.equals( Boolean.class )) {
            sb.append( " is" );

            if( columnName.toLowerCase().startsWith( "is" )) {
                sb.append( columnName.substring( 2 ));
            } else {
                sb.append( columnName );
            }
        } else {
            sb.append( " get" ).append( columnName );
        }

        sb.append( "() {" );

        if( clazz.equals( Integer.class )) {
            sb.append( "Integer ii = (Integer)" ).append( getValue ).append( "(\"" ).append( columnName ).append( "\");" + "if (ii == null)" + " return 0;" + "return ii.intValue();" );
        } else if( clazz.equals( BigDecimal.class )) {
            sb.append( "BigDecimal bd = (BigDecimal)" ).append( getValue ).append( "(\"" ).append( columnName ).append( "\");" + "if (bd == null)" + " return Env.ZERO;" + "return bd;" );
        } else if( clazz.equals( Boolean.class )) {
            sb.append( "Object oo = " ).append( getValue ).append( "(\"" ).append( columnName ).append( "\");" + "if (oo != null) { if (oo instanceof Boolean) return ((Boolean)oo).booleanValue(); return \"Y\".equals(oo);}" + "return false;" );
        } else if( dataType.equals( "Object" )) {
            sb.append( "return " ).append( getValue ).append( "(\"" ).append( columnName ).append( "\");" );
        } else {
            sb.append( "return (" ).append( dataType ).append( ")" ).append( getValue ).append( "(\"" ).append( columnName ).append( "\");" );
        }

        sb.append( "}" );

        //

        return sb.toString();
    }    // createColumnMethods

    /**
     * Descripción de Método
     *
     *
     * @param sb
     * @param AD_Reference_ID
     * @param columnName
     * @param nullable
     *
     * @return
     */

    private String addListValidation( StringBuffer sb, int AD_Reference_ID, String  referenceName, String columnName,boolean nullable ) {
        StringBuffer retValue = new StringBuffer();
        
        String aReferenceToken = columnName.toUpperCase() + "_AD_Reference_ID";

        retValue.append( "public static final int " + aReferenceToken + " = MReference.getReferenceID(\"" + referenceName + "\");" );

        //

        boolean      found  = false;
        StringBuffer values = new StringBuffer( "Reference = " ).append( aReferenceToken );
        StringBuffer statement = new StringBuffer();

        if( nullable ) {
            statement.append( "if (" ).append( columnName ).append( " == null" );
        }

        String sql = "SELECT Value, Name FROM AD_Ref_List WHERE AD_Reference_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Reference_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                String value = rs.getString( 1 );

                values.append( " - " ).append( value );

                if( statement.length() == 0 ) {
                    statement.append( "if (" ).append( columnName ).append( ".equals(\"" ).append( value ).append( "\")" );
                } else {
                    statement.append( " || " ).append( columnName ).append( ".equals(\"" ).append( value ).append( "\")" );
                }

                found = true;

                // Name (SmallTalkNotation)

                String       name      = rs.getString( 2 );
                char[]       nameArray = name.toCharArray();
                StringBuffer nameClean = new StringBuffer();
                boolean      initCap   = true;

                for( int i = 0;i < nameArray.length;i++ ) {
                    char c = nameArray[ i ];

                    if( Character.isJavaIdentifierPart( c )) {
                        if( initCap ) {
                            nameClean.append( Character.toUpperCase( c ));
                        } else {
                            nameClean.append( c );
                        }

                        initCap = false;
                    } else {
                        if( c == '+' ) {
                            nameClean.append( "Plus" );
                        } else if( c == '-' ) {
                            nameClean.append( "_" );
                        } else if( c == '>' ) {
                            if( name.indexOf( '<' ) == -1 ) {    // ignore <xx>
                                nameClean.append( "Gt" );
                            }
                        } else if( c == '<' ) {
                            if( name.indexOf( '>' ) == -1 ) {    // ignore <xx>
                                nameClean.append( "Le" );
                            }
                        } else if( c == '!' ) {
                            nameClean.append( "Not" );
                        } else if( c == '=' ) {
                            nameClean.append( "Eq" );
                        } else if( c == '~' ) {
                            nameClean.append( "Like" );
                        }

                        initCap = true;
                    }
                }

                retValue.append( "/** " ).append( name ).append( " = " ).append( value ).append( " */\n" );
                retValue.append( "public static final String " ).append( columnName.toUpperCase()).append( "_" ).append( nameClean ).append( " = \"" ).append( value ).append( "\";" );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
            found = false;
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        statement.append( ")" + "; " + "else " + "throw new IllegalArgumentException (\"" ).append( columnName ).append( " Invalid value - " ).append( values ).append( "\");" );

        //

        if( found ) {
            sb.append( statement );
        }

        return retValue.toString();
    }    // addListValidation

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     * @param displayType
     *
     * @return
     */

    private StringBuffer createKeyNamePair( String columnName,int displayType ) {
        String method = "get" + columnName + "()";

        if( displayType != DisplayType.String ) {
            method = "String.valueOf(" + method + ")";
        }

        StringBuffer sb = new StringBuffer( "public KeyNamePair getKeyNamePair() " + "{return new KeyNamePair(getID(), " ).append( method ).append( ");}" );

        return sb;
    }    // createKeyNamePair

    /**
     * Descripción de Método
     *
     *
     * @param sb
     * @param fileName
     */

    private void writeToFile( StringBuffer sb,String fileName ) { 
        try {
            File       out = new File( fileName );
            FileWriter fw  = new FileWriter( out );

            for( int i = 0;i < sb.length();i++ ) {
                char c = sb.charAt( i );

                // after
                                
                if( (c == ';') || (c == '}') ) {
                    fw.write( c );

                    if( sb.substring( i + 1 ).startsWith( "//" )) {
                        fw.write( '\t' );
                    } else {
                        fw.write( Env.NL );
                    }
                }

                // before & after

                else if( c == '{' ) {
                    fw.write( Env.NL );
                    fw.write( c );
                    fw.write( Env.NL );
                } else {
                    fw.write( c );
                }
            }

            fw.flush();
            fw.close();

            float size = out.length();

            size /= 1024;
            //log.info( out.getAbsolutePath() + " - " + size + " kB" );
            log.info("\n Building " + out.getAbsolutePath() + " " + size + " kB");
            log.info("\n ---------------------------------------------------------------------- done");

        } catch( Exception ex ) {

            log.info("Error building " + fileName);
            log.info("---------------------------------------------------------------------- error");

        }
    }    // writeToFile

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "GenerateModel[" ).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {    	
        org.openXpertya.OpenXpertya.startupEnvironment( true );
        //CLogMgt.setLevel( Level.FINE );
       
        CLogMgt.setLevel(Level.ALL);

        log.info( "Generate Model   $Revision: 1.42 $" );
        log.info( "----------------------------------" );
        
        // first parameter

        // begin vpj-cd e-evolution 01/18/2005

        String directory = "C:\\ramaconserti\\dbPort\\src\\org\\openXpertya\\model\\";


        // end vpj-cd e-evolution 01/18/2005
        
        if( args.length > 0 ) {
            directory = args[ 0 ];
        }

        if( (directory == null) || (directory.length() == 0) ) {
            System.err.println( "No Directory" );
            System.exit( 1 );
        }

        log.info( "Directory: " + directory );

        // second parameter
        // begin vpj-cd e-evolution 01/18/2005

        String packageName = "org.openXpertya.model";
        
        // String packageName = "openXpertya.model";
        // end vpj-cd e-evolution 01/18/2005

        if( args.length > 1 ) {
            packageName = args[ 1 ];
        }

        if( (packageName == null) || (packageName.length() == 0) ) {
            System.err.println( "No package" );
            System.exit( 1 );
        }

        log.info( "Package:   " + packageName );

        // third parameter
        // begin vpj-cd e-evolution 01/18/2005

        String entityType = "'U','A','D'";    // User, Application
        
        // tring entityType = "'U','A'"; //      User, Application
        // end vpj-cd e-evolution 01/18/2005

        if( args.length > 2 ) {
            entityType = args[ 2 ];
        }

        if( (entityType == null) || (entityType.length() == 0) ) {
            System.err.println( "No EntityType" );
            System.exit( 1 );
        }

        StringBuffer sql = new StringBuffer( "EntityType IN (" ).append( entityType ).append( ")" );

        //log.info( sql.toString());
        log.info( "----------------------------------" );

        // complete sql

        // Lineas modificadas para que genere las tablas Trl
        //sql.insert( 0,"SELECT AD_Table_ID " + "FROM AD_Table " + "WHERE (TableName IN ('RV_WarehousePrice','RV_BPartner')"    // special views
        //            + " OR IsView='N')" + " AND TableName NOT LIKE '%_Trl' AND " );
        
        sql.insert( 0,"SELECT AD_Table_ID " + "FROM AD_Table " + "WHERE (TableName IN ('RV_WarehousePrice','RV_BPartner')"    // special views
                + " OR IsView='N') AND "); 	        
         
        sql.append( " ORDER BY TableName" );

        //
        
        int               count = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                new GenerateModel( rs.getInt( 1 ),directory,packageName );
                count++;
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.severe( "main - " + e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.info( "Generated = " + count );
    }    // main
    
    
    /**
     * Retorna el tipo de componente segun el coreLevel indicado en el componente correspondiente 
     * @param AD_Table_ID
     * @return
     */
    protected Integer coreLevelTable(int AD_Table_ID)
    {
    	Integer coreLevel = DB.getSQLValue(null, " select corelevel from ad_table t inner join ad_componentversion cv on t.ad_componentversion_id = cv.ad_componentversion_id inner join ad_component c on cv.ad_component_id = c.ad_component_id where ad_table_id = ?", AD_Table_ID );
    	if (coreLevel >= 0)
    		return coreLevel;
    	return null;
    }
    
    /**
     * Retorna el packageName a partir del identificador de la tabla
     * @param AD_Table_ID
     * @return
     */
    protected String getPluginPackageName(int AD_Table_ID)
    {
    	return DB.getSQLValueString(null, " select packageName from ad_table t inner join ad_componentversion cv on t.ad_componentversion_id = cv.ad_componentversion_id inner join ad_component c on cv.ad_component_id = c.ad_component_id where ad_table_id = ?", AD_Table_ID );
    }
    
    
    /**
     * Para una tabla dada, devuelve todos los plugins (AD_Component_ID) existentes en las columnas que comprenden la tabla
     * @param AD_Table_ID
     * @return
     */
    Vector<Integer> getLPlugins(int AD_Table_ID)
    {
    	Vector<Integer> result = new Vector<Integer>();
    	try
    	{
    		PreparedStatement stmt = DB.prepareStatement(" SELECT distinct C.AD_Component_ID FROM AD_Column col INNER JOIN AD_ComponentVersion cv ON col.AD_ComponentVersion_ID = cv.AD_ComponentVersion_ID INNER JOIN AD_Component c ON cv.AD_Component_ID = c.AD_Component_ID WHERE c.corelevel <> 0 AND col.ad_table_id = " + AD_Table_ID );
    		ResultSet rs = stmt.executeQuery();
    		
    		while (rs.next()) 
    			result.add(rs.getInt(1));
    		
    		return result;
    	} 
    	catch (Exception e)    	{
    		e.printStackTrace();
    		return result;
    	}
    }
    
    private String createX(int AD_Table_ID,String directory,String packageName, boolean coreLevelZero, Integer componentID, boolean isPluginHeader, String superClass  )
    {
    	// create column access methods
        StringBuffer mandatory = new StringBuffer();
        StringBuffer sb        = createColumns( AD_Table_ID,mandatory, coreLevelZero, componentID ); 

        // add header stuff
        String fileName = createHeader( AD_Table_ID,sb,mandatory,packageName, isPluginHeader, superClass ); 

        // append SaveDirect method (if corresponds)
       	createInsertDirectMethod(AD_Table_ID, sb);
        
        // Save it
        writeToFile( sb,directory + fileName + ".java" );
        
        return fileName;
    }
    
    private boolean extendsAnotherX(String superClass)
    {
    	return !PO_CLASS_NAME.equals(superClass);
    }
    
    /**
     * Determinar si la superclase para los componentes de core debe ser una clase M o una clase X_
     * @param AD_Table_ID
     * @return
     */
    private String getCoreSuperClass(int AD_Table_ID)
    {
    	String tableName = DB.getSQLValueString(null, " SELECT tablename FROM AD_Table WHERE AD_Table_ID = ? ", AD_Table_ID);
    	if (tableName!=null)
    	{
    		Class<?> clazz = M_Table.getClass(tableName);
    		return clazz.getName();
    	}
    	return null;
    	
    }
    
    /**
     * Generación de método directo de persistencia 
     * (para evitar la determinación en tiempo de ejecución de query a ejecutar)
     * @param AD_Table_ID
     * @param sb
     */
    protected void createInsertDirectMethod(int AD_Table_ID, StringBuffer sb)
    {
    	StringBuffer columnNames = new StringBuffer();
    	StringBuffer columnSets = new StringBuffer();
    	StringBuffer columnMarks = new StringBuffer();
    	StringBuffer columnNotNeeded = new StringBuffer();
    	
    	// determinar el nombre de la tabla
    	String tableName = DB.getSQLValueString(null, " SELECT tablename FROM AD_Table WHERE generateDirectMethods = 'Y' AND AD_Table_ID = ?", AD_Table_ID);

    	// si no obtengo resultado es porque no tiene seteado el generateDirectMethods
    	if (tableName == null)
    		return;
    	
    	try 
    	{
	    	PreparedStatement pstmt = DB.prepareStatement(" SELECT columnname, AD_Reference_ID FROM AD_Column WHERE AD_Table_ID = " + AD_Table_ID + " AND ColumnSQL IS NULL ");
	    	ResultSet rs = pstmt.executeQuery();
	    	while (rs.next())
	    	{
	    		boolean isValidColumn = false;
	    		Class clazz = DisplayType.getClass( rs.getInt("AD_Reference_ID"), true );
	            String dataType = clazz.getName();
	            dataType = dataType.substring( dataType.lastIndexOf( '.' ) + 1 );
	            
	            // Segun el tipo de dato indicar el set correspondiente
	            String columnName = rs.getString("columnname");
	            if( dataType.equals( "Boolean" )) {

	            	if (columnName.startsWith("Is"))
	            		columnName = columnName.replaceFirst("Is", "is");
	            	else
	            		columnName = "is" + columnName;
	            		
	            	columnSets.append("\t\t pstmt.setString(col++, " + columnName + "()?\"Y\":\"N\");" );
	            	isValidColumn = true;
	            } else if( dataType.equals( "Integer" )) {
	            	columnSets.append("\t\t if (get" + columnName + "() != 0) pstmt.setInt(col++, get" + columnName + "());" );
	            	columnNotNeeded.append("\t\t if (get" + columnName + "() == 0) sql = sql.replaceFirst(\"" + columnName + ",\",\"\").replaceFirst(\"\\\\?,\", \"\"); ");
	            	isValidColumn = true;
            	} else if( dataType.equals( "String" )) {
            		columnSets.append("\t\t if (get" + columnName + "() != null) pstmt.setString(col++, get" + columnName + "());" );
            		columnNotNeeded.append("\t\t if (get" + columnName + "() == null) sql = sql.replaceFirst(\"" + columnName + ",\",\"\").replaceFirst(\"\\\\?,\", \"\"); ");
            		isValidColumn = true;
            	} else if( dataType.equals( "BigDecimal" )) {
            		columnSets.append("\t\t if (get" + columnName + "() != null) pstmt.setBigDecimal(col++, get" + columnName + "());" );
            		columnNotNeeded.append("\t\t if (get" + columnName + "() == null) sql = sql.replaceFirst(\"" + columnName + ",\",\"\").replaceFirst(\"\\\\?,\", \"\"); ");
            		isValidColumn = true;
            	} else if( dataType.equals( "Timestamp" )) {
            		columnSets.append("\t\t if (get" + columnName + "() != null) pstmt.setTimestamp(col++, get" + columnName + "());" );
            		columnNotNeeded.append("\t\t if (get" + columnName + "() == null) sql = sql.replaceFirst(\"" + columnName + ",\",\"\").replaceFirst(\"\\\\?,\", \"\"); ");
            		isValidColumn = true;
            	}
	    		
	    		// INSERT INTO (xxx, xxx, xxx) VALUES (?, ?, ?)
	            if (isValidColumn)
	            {
	            	columnNames.append(rs.getString("columnname")).append(",");
	            	columnMarks.append("?,");
	            }
	    		
	    	}

	    	// quitar ultima coma
	    	columnNames.deleteCharAt(columnNames.length()-1);
	    	columnMarks.deleteCharAt(columnMarks.length()-1);
	    	    	
	    	// crear el metodo
        	StringBuffer methodBody = new StringBuffer();
        	methodBody.append("\n" );
        	methodBody.append("public boolean insertDirect() { \n" );
        	methodBody.append("try { \n " );
        	// siguiente linea comentada: no es necesario obtener el valor de ID, dado que ya lo tiene seteado
        	// methodBody.append("\t\t set" + tableName + "_ID(DB.getSQLValue(get_TrxName(), \"SELECT nextval('seq_" + tableName + "')\")); \n ");
        	methodBody.append("\t\t String sql = \" INSERT INTO " + tableName + "(" + columnNames + ") VALUES (" + columnMarks + ") \";\n" );
        	methodBody.append(columnNotNeeded + "\n ");
        	methodBody.append("\t\t int col = 1; \n");
        	methodBody.append("\t\t CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true); \n");
        	methodBody.append(columnSets + "\n");
        	methodBody.append("\t\tpstmt.executeUpdate();\n");
        	methodBody.append("\t\treturn true;\n");
        	methodBody.append("\t}catch (SQLException e) {");
        	methodBody.append("\tlog.log(Level.SEVERE, \"insertDirect\", e);" );
        	methodBody.append("\tlog.saveError(\"Error\", DB.getErrorMsg(e) + \" - \" + e);" );
        	methodBody.append("\treturn false;");
        	methodBody.append("\t}catch (Exception e2) {");
        	methodBody.append("\tlog.log(Level.SEVERE, \"insertDirect\", e2);" );
        	methodBody.append("\treturn false;");
        	methodBody.append("}\n");
        	methodBody.append("}\n");

        	sb.insert(sb.lastIndexOf("}"), methodBody);
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	
    }
}    // GenerateModel



/*
 *  @(#)GenerateModel.java   25.03.06
 *
 *  Fin del fichero GenerateModel.java
 *
 *  Versión 2.2
 *
 */
