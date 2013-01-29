/**
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



package org.openXpertya.dbPort;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openXpertya.db.Database;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

/**
 * Descripción de la Clase Convert
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Convert {

    /**
     * Constructor de la clase ...
     *
     *
     * @param type Database.DB_
     */

    public Convert( String type ) {
        if( Database.DB_ORACLE.equals( type )) {
            m_isOracle = true;
        } else if( Database.DB_SYBASE.equals( type )) {
            m_map = ConvertMap.getSybaseMap();
        } else if( Database.DB_POSTGRESQL.equals( type )) {
            m_map = ConvertMap.getPostgeSQLMap();
        } else {
            throw new UnsupportedOperationException( "Unsupported database: " + type );
        }
    }    // Convert

    /** Descripción de Campos */

    public static final int REGEX_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;

    /** Descripción de Campos */

    private boolean m_isOracle = false;

    /** Descripción de Campos */

    private TreeMap m_map;

    /** Descripción de Campos */

    private Statement m_stmt = null;

    /** Descripción de Campos */

    private String m_conversionError = null;

    /** Descripción de Campos */

    private Exception m_exception = null;

    /** Descripción de Campos */

    private boolean m_verbose = true;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Convert.class );

    /**
     * Descripción de Método
     *
     *
     * @param verbose
     */

    public void setVerbose( boolean verbose ) {
        m_verbose = verbose;
    }    // setVerbose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOracle() {
        return m_isOracle;
    }    // isOracle

    /**
     * Descripción de Método
     *
     *  Ejecuta la sentencia SQL (para al primer error).
	 *  Si ocurre un error hadError() devuelve true.
	 *  Puedes obtener detalles via getConversionError() o getException() 
     *
     * @param sqlStatements
     * @param conn
     *
     * @return true if success
	 *  @throws IllegalStateException if no connection
     */

    public boolean execute( String sqlStatements,Connection conn ) {
        if( conn == null ) {
            throw new IllegalStateException( "Require connection" );
        }

        //

        String[] sql = convert( sqlStatements );

        m_exception = null;

        if( (m_conversionError != null) || (sql == null) ) {
            return false;
        }

        boolean ok        = true;
        int     i         = 0;
        String  statement = null;

        try {
            if( m_stmt == null ) {
                m_stmt = conn.createStatement();
            }

            //

            for( i = 0;ok && (i < sql.length);i++ ) {
                statement = sql[ i ];

                if( statement.length() == 0 ) {
                    if( m_verbose ) {
                        log.finer( "Skipping empty (" + i + ")" );
                    }
                } else {
                    if( m_verbose ) {
                        log.finest( "Executing (" + i + ") <<" + statement + ">>" );
                    } else {
                        log.finest( "Executing " + i );
                    }

                    try {
                        m_stmt.clearWarnings();

                        int        no   = m_stmt.executeUpdate( statement );
                        SQLWarning warn = m_stmt.getWarnings();

                        if( warn != null ) {
                            if( m_verbose ) {
                                log.finest( "- " + warn );
                            } else {
                                log.finest( "Executing (" + i + ") <<" + statement + ">>" );
                                log.finest( "- " + warn );
                            }
                        }

                        if( m_verbose ) {
                            log.fine( "- ok " + no );
                        }
                    } catch( SQLException ex ) {

                        // Ignore Drop Errors

                        if( !statement.startsWith( "DROP " )) {
                            ok          = false;
                            m_exception = ex;
                        }

                        if( !m_verbose ) {
                            log.finest( "Executing (" + i + ") <<" + statement + ">>" );
                        }

                        log.finest( "Error executing " + i + "/" + sql.length + " = " + ex );
                    }
                }
            }    // for all statements
        } catch( SQLException e ) {
            m_exception = e;

            if( !m_verbose ) {
                log.finest( "Executing (" + i + ") <<" + statement + ">>" );
            }

            log.finest( "Error executing " + i + "/" + sql.length + " = " + e );

            return false;
        }

        return ok;
    }    // execute

    /**
     * Descripción de Método
     *
     *
     * @return execution exception
     */

    public Exception getException() {
        return m_exception;
    }    // getException

    /**
     * Descripción de Método
     * Devuelve true si ocurre un error al ejecutar o convertir
     * puedes obtener mayores detalles via getConversionError() o getException()
     * @return true si ocurre un error
     */

    public boolean hasError() {
        return( m_exception != null ) | ( m_conversionError != null );
    }    // hasError

    /**
     * Descripción de Método
     *
     *  Convierte la sentencia SQL (para al primer error).
	 *  las sentencias se delimitan con /
	 *  si ocurre un error hadError() devuelve true.
	 *  puedes obtener mayores detalles via getConversionError()
     *
     * @param sqlStatements
     *
     * @return la sentencia convertida como una cadena
     */

    public String convertAll( String sqlStatements ) {
        String[]     sql = convert( sqlStatements );
        StringBuffer sb  = new StringBuffer( sqlStatements.length() + 10 );

        for( int i = 0;i < sql.length;i++ ) {

            // line.separator

            sb.append( sql[ i ] ).append( "\n/\n" );

            if( m_verbose ) {
                log.finest( "Statement " + i + ": " + sql[ i ] );
            }
        }

        return sb.toString();
    }    // convertAll

    /**
     * Descripción de Método
     *
	 *  Convierte sentencia SQL (para al primer error).
	 *  Si ocurre un error hadError() devuelve true.
	 *  Puedes obtener mayores detaller via getConversionError()
     *
     * @param sqlStatements
     *
     * @return Array de sentencias convertidas
     */

    public String[] convert( String sqlStatements ) {
        m_conversionError = null;

        if( (sqlStatements == null) || (sqlStatements.length() == 0) ) {
            m_conversionError = "SQL_Statement is null or has zero length";
            log.finest( m_conversionError );

            return null;
        }

        //

        return convertIt( sqlStatements );
    }    // convert

    /**
     * Descripción de Método
     *
     *
     * @return null o el ultimo error de conversion
     */

    public String getConversionError() {
        return m_conversionError;
    }    // getConversionError

    /**
     *  Descripción de Método
	 *  rutina de Conversion (para al primer error).
	 *  <pre>
	 *  - mask / in Strings
	 *  - break into single statement
	 *  - unmask statements
	 *  - for each statement: convertStatement
	 *      - remove comments
	 *          - process FUNCTION/TRIGGER/PROCEDURE
	 *          - process Statement: convertSimpleStatement
	 *              - based on ConvertMap
	 *              - convertComplexStatement
	 *                  - decode, sequence, exception
	 *  </pre>
     * @param sqlStatements
     *
     * @return array de sentencias convertidas
     */

    private String[] convertIt( String sqlStatements ) {

        // Need to mask / in SQL Strings !
    	//log.finest( "En Conver.java/converIT, con la sql SIN convertida:=  " + sqlStatements);

        final char   MASK   = '\u001F';    // Unit Separator
        StringBuffer masked = new StringBuffer( sqlStatements.length());
        Matcher      m      = Pattern.compile( "'[^']+'",Pattern.DOTALL ).matcher( sqlStatements );

        while( m.find()) {
            String group = m.group();             // SQL string
            if( group.indexOf( "/" ) != -1 ) {    // / in string
                group = group.replace( '/',MASK );
            }
            if( group.indexOf( '$' ) != -1 ) {    // Group character needs to be escaped
                group = Util.replace( group,"$","\\$" );
            }

            m.appendReplacement( masked,group );
        }

        m.appendTail( masked );

        String tempResult = masked.toString();
        //log.finest( "En Conver.java/converIT, tempResult:=  " + tempResult);
        
        // Statements ending with /

        String[]  sql    = tempResult.split( "\\s/\\s" );    // ("(;\\s)|(\\s/\\s)");
        ArrayList result = new ArrayList( sql.length );

        // process statements
        
        for( int i = 0;i < sql.length;i++ ) {
            String statement = sql[ i ];
            if( statement.indexOf( MASK ) != -1 ) {
                statement = statement.replace( MASK,'/' );
            }

            result.addAll( convertStatement( statement));    // may return more than one target statement
            
        }
        
        //Fin modificación

        // convert to array
        log.finest( "En Conver.java/converIT, con la sql convertida:=  " + result);
        sql = new String[ result.size()];
        result.toArray( sql );

        return sql;
    }    // convertIt

    /**
     *  Descripción de Método
	 *  Convert single Statements.
	 *  - remove comments
	 *      - process FUNCTION/TRIGGER/PROCEDURE
	 *      - process Statement
     * @param sqlStatement
     *
     * @return sentencia convertida
     */

    private ArrayList convertStatement( String sqlStatement ) {
        ArrayList result = new ArrayList();

        if( m_isOracle ) {
            result.add( sqlStatement );

            return result;
        }

        // remove comments

        String statement = removeComments( sqlStatement );

        // log.finest("------------------------------------------------------------");
        // log.finest(statement);
        // log.finest("------------------->");

        String  cmpString = statement.toUpperCase();
        boolean isCreate  = cmpString.startsWith( "CREATE " );

        // Process

        if( isCreate && (cmpString.indexOf( " FUNCTION " ) != -1) ) {
            result.addAll( convertFunction( statement ));
        } else if( isCreate && (cmpString.indexOf( " TRIGGER " ) != -1) ) {
            result.addAll( convertTrigger( statement ));
        } else if( isCreate && (cmpString.indexOf( " PROCEDURE " ) != -1) ) {
            result.addAll( convertProcedure( statement ));
        } else if( isCreate && (cmpString.indexOf( " VIEW " ) != -1) ) {
            result.addAll( convertView( statement ));

            // comienzo cambios para PostgreSQL

        } else if( cmpString.indexOf( "ROWNUM" ) != -1 ) {
            result.add( converSimpleStatement( convertAlias( convertRowNum( statement ))));
        }

        /*
         * else if (cmpString.indexOf("UPDATE ") != -1)
         * {
         *       result.add(converSimpleStatement(convertUpdate(statement)));
         *   return result;
         * }
         */

        else if( (cmpString.indexOf( "DELETE " ) != -1) && (cmpString.indexOf( "DELETE FROM" ) == -1) ) {
            result.add( converSimpleStatement( convertAlias( convertDelete( statement ))));

// fin modificaciones para PostgreSQL

            // Simple Statement

        } else {
            result.add( converSimpleStatement( convertAlias( statement )));
        }

        //
        // log.finest("<-------------------");
        // for (int i = 0; i < result.size(); i++)
        // log.finest(result.get(i));
        // log.finest("------------------------------------------------------------");
// begin comienzo cambios para PostgreSQL
        // result.add(convertAlias(statement));
// fin cambios para PostgreSQL

        return result;
    }    // convertStatement

    /**
     * Descripción de Método
     *
	 *  Convierte una sentencia simple SQL .
	 *  Basado en ConvertMap
     *
     * @param sqlStatement
     *
     * @return sentencia simple convertida
     */

    private String converSimpleStatement( String sqlStatement ) {

        // Error Checks

        if( sqlStatement.toUpperCase().indexOf( "EXCEPTION WHEN" ) != -1 ) {
            String error = "Exception clause needs to be converted: " + sqlStatement;

            log.finest( error );
            m_conversionError = error;

            return sqlStatement;
        }

        // Standard Statement

        String   retValue = sqlStatement;
        Iterator iter     = m_map.keySet().iterator();

        while( iter.hasNext()) {
            String regex       = ( String )iter.next();
            String replacement = ( String )m_map.get( regex );

            try {
                Pattern p = Pattern.compile( regex,REGEX_FLAGS );
                Matcher m = p.matcher( retValue );

                retValue = m.replaceAll( replacement );
            } catch( Exception e ) {
                String error = "Error expression: " + regex + " - " + e;

                log.finest( error );
                m_conversionError = error;
            }
        }

        // Convert Decode, Sequence, Join, ..

        return convertComplexStatement( retValue );
    }    // convertSimpleStatement

     /*
     *  Descripción de Método
	 *  Clean up Statement.
	 *  Remove all comments and while spaces
	 *  Database specific functionality can me tagged as follows:
	 *  <pre>
	 *	&#047;*ORACLE&gt;*&#047;
	 *      Oracle Specific Statement
	 *	&#047;*&lt;ORACLE*&#047;
	 *	&#047;*POSTGRESQL&gt;
	 *      PostgreSQL Specicic Statements
	 *	&lt;POSTGRESQL*&#047;
	 *  </pre>
     * @param statement
     *
     * @return sentencia SQL
     */

    protected String removeComments( String statement ) {
        String clean = statement.trim();

        // Remove /*ORACLE>*/ /*<ORACLE*/

        Matcher m = Pattern.compile( "\\/\\*ORACLE>.*<ORACLE\\*\\/",Pattern.DOTALL ).matcher( clean );

        clean = m.replaceAll( "" );

        // Remove /.POSTGRESQL>

        m     = Pattern.compile( "\\/\\*POSTGRESQL>" ).matcher( clean );
        clean = m.replaceAll( "" );

        // Remove <POSTGRESQL./

        m     = Pattern.compile( "<POSTGRESQL\\*\\/" ).matcher( clean );
        clean = m.replaceAll( "" );

        // Remove /* */

        m = Pattern.compile( "\\/\\*.*\\*\\/",Pattern.DOTALL ).matcher( clean );
        clean = m.replaceAll( "" );

        /**
		//  Borrado --
		m = Pattern.compile("--.*$").matcher(clean);        //  up to EOL
		clean = m.replaceAll("");
		m = Pattern.compile("--.*[\\n\\r]").matcher(clean); //  -- at BOL
		clean = m.replaceAll("");
		**/
        
        // Convert cr/lf/tab to single space

        m     = Pattern.compile( "\\s+" ).matcher( clean );
        clean = m.replaceAll( " " );
        clean = clean.trim();

        return clean;
    }    // removeComments

    /*
     *  Descripción de Método
	 *  Convierte.
	 *  <pre>
	 *      CREATE OR REPLACE FUNCTION AD_Message_Get
	 *      (p_AD_Message IN VARCHAR, p_AD_Language IN VARCHAR)
	 *      RETURN VARCHAR AS
	 *      ...
	 *      END AD_Message_Get;
	 *  =>
	 *      CREATE FUNCTION AD_Message_Get
	 *      (VARCHAR, VARCHAR)
	 *      RETURNS VARCHAR AS '
	 *      DECLARE
	 *      p_AD_Message ALIAS FOR $1;
	 *      p_AD_Language ALIAS FOR $2;
	 *      ....
	 *      END;
	 *      ' LANGUAGE 'plpgsql';
	 *  </pre>
     * @param sqlStatement
     *
	 *  @return CREATE and DROP Function statement
     */

    private ArrayList convertFunction( String sqlStatement ) {
        ArrayList result = new ArrayList();
        log.finest("Estoy en ConverFuncion con sqlStatment=" + sqlStatement);
        // Convert statement - to avoid handling contents of comments

        String stmt = converSimpleStatement( sqlStatement );

        // Double quotes '

        stmt = Pattern.compile( "'" ).matcher( stmt ).replaceAll( "''" );

        // remove OR REPLACE

        int orReplacePos = stmt.toUpperCase().indexOf( " OR REPLACE " );

        if( orReplacePos != -1 ) {
            stmt = "CREATE" + stmt.substring( orReplacePos + 11 );
        }

        // Line separators

        String match = "(\\([^\\)]*\\))"                  // (.) Parameter
                       + "|(\\bRETURN \\w+ (AS)|(IS))"    // RETURN CLAUSE
                       + "|(;)"                           // Statement End

        // Nice to have - for readability

        + "|(\\bBEGIN\\b)"    // BEGIN
        + "|(\\bTHEN\\b)" + "|(\\bELSE\\b)" + "|(\\bELSIF\\b)";
        Matcher m = Pattern.compile( match,Pattern.CASE_INSENSITIVE ).matcher( stmt );
        StringBuffer sb = new StringBuffer();

        // First group -> ( )
        // CREATE OR REPLACE FUNCTION AD_Message_Get ( p_AD_Message IN VARCHAR, p_AD_Language IN VARCHAR)
        // CREATE FUNCTION AD_Message_Get (VARCHAR, VARCHAR)

        m.find();
        m.appendReplacement( sb,"" );

        String       name      = sb.substring( 6 ).trim();
        StringBuffer signature = new StringBuffer();

        //

        String group = m.group().trim();

        // log.finest("Group: " + group);

        StringBuffer alias = new StringBuffer();

        // Parameters

        if( group.startsWith( "(" ) && group.endsWith( ")" )) {

            // Default not supported

            if( group.toUpperCase().indexOf( " DEFAULT " ) != -1 ) {
                String error = "DEFAULT in Parameter not supported";

                log.finest( error );
                m_conversionError = error;

                return result;
            }

            signature.append( "(" );

            if( group.length() > 2 ) {
                group = group.substring( 1,group.length() - 1 );

                // Paraneters are delimited by ,

                String[] parameters = group.split( "," );

                for( int i = 0;i < parameters.length;i++ ) {
                    if( i != 0 ) {
                        signature.append( ", " );
                    }

                    // name ALIAS FOR $1

                    String p = parameters[ i ].trim();

                    alias.append( p.substring( 0,p.indexOf( " " ))).append( " ALIAS FOR $" ).append( i + 1 ).append( ";\n" );

                    // Datatape

                    signature.append( p.substring( p.lastIndexOf( " " ) + 1 ));
                }
            }

            signature.append( ")" );
            sb.append( signature );

            // log.finest("Alias: " + alias.toString());
            // log.finest("Signature: " + signature.toString());

        }

        // No Parameters

        else {
            String error = "Missing Parameter ()";

            log.finest( error );
            m_conversionError = error;

            return result;
        }

        sb.append( "\n" );

        // Need to create drop statement

        if( orReplacePos != -1 ) {
            String drop = "DROP " + name + signature.toString();

            // log.finest(drop);

            result.add( drop );
        }

        // log.finest("1>" + sb.toString() + "<1");

        // Second Group -> RETURN VARCHAR AS
        // RETURNS VARCHAR AS

        m.find();
        group = m.group();
        m.appendReplacement( sb,"" );

        if( group.startsWith( "RETURN" )) {
            sb.append( "RETURNS" ).append( group.substring( group.indexOf( " " )));
        }

        sb.append( " '\nDECLARE\n" ).append( alias );    // add aliases here

        // log.finest("2>" + sb.toString() + "<2");

        // remainder statements

        while( m.find()) {
            String group2 = m.group();

            if( group2.indexOf( '$' ) != -1 ) {    // Group character needs to be escaped
                group2 = Util.replace( group2,"$","\\$" );
            }

            m.appendReplacement( sb,group2 );
            sb.append( "\n" );
        }

        m.appendTail( sb );

        // finish

        sb.append( "' LANGUAGE 'plpgsql';" );

        // log.finest(">" + sb.toString() + "<");

        result.add( sb.toString());

        //

        return result;
    }    // convertFunction

    /**
     * Descripción de Método
	 *  Convert Procedure.
	 *  <pre>
	 *      CREATE OR REPLACE PROCEDURE AD_Message_X
	 *      (p_AD_Message IN VARCHAR, p_AD_Language IN VARCHAR)
	 *      ...
	 *      END AD_Message_X;
	 *  =>
	 *      CREATE FUNCTION AD_Message_X
	 *      (VARCHAR, VARCHAR)
	 *      RETURNS VARCHAR AS '
	 *      DECLARE
	 *      p_AD_Message ALIAS FOR $1;
	 *      p_AD_Language ALIAS FOR $2;
	 *      ....
	 *      END;
	 *      ' LANGUAGE 'plpgsql';
	 *  </pre>
     * @param sqlStatement
     *
	 *  @return CREATE and DROP Function statement
     */

    private ArrayList convertProcedure( String sqlStatement ) {
        ArrayList result = new ArrayList();

        // Convert statement - to avoid handling contents of comments

        String stmt = converSimpleStatement( sqlStatement );

        // Double quotes '

        stmt = Pattern.compile( "'" ).matcher( stmt ).replaceAll( "''" );

        // remove OR REPLACE

        int orReplacePos = stmt.toUpperCase().indexOf( " OR REPLACE " );

        if( orReplacePos != -1 ) {
            stmt = "CREATE" + stmt.substring( orReplacePos + 11 );
        }

        // Line separators

        String match = "(\\([^\\)]*\\))"                  // (.) Parameter
                       + "|(\\bRETURN \\w+ (AS)|(IS))"    // RETURN CLAUSE
                       + "|(;)"                           // Statement End

        // Nice to have - for readability

        + "|(\\bBEGIN\\b)"    // BEGIN
        + "|(\\bTHEN\\b)" + "|(\\bELSE\\b)" + "|(\\bELSIF\\b)";
        Matcher m = Pattern.compile( match,Pattern.CASE_INSENSITIVE ).matcher( stmt );
        StringBuffer sb = new StringBuffer();

        // First group -> ( )
        // CREATE OR REPLACE FUNCTION AD_Message_Get ( p_AD_Message IN VARCHAR, p_AD_Language IN VARCHAR)
        // CREATE FUNCTION AD_Message_Get (VARCHAR, VARCHAR)

        m.find();
        m.appendReplacement( sb,"" );

        String       name      = sb.substring( 6 ).trim();
        StringBuffer signature = new StringBuffer();

        //

        String group = m.group().trim();

        // log.finest("Group: " + group);

        StringBuffer alias = new StringBuffer();

        // Parameters

        if( group.startsWith( "(" ) && group.endsWith( ")" )) {

            // Default not supported

            if( group.toUpperCase().indexOf( " DEFAULT " ) != -1 ) {
                String error = "DEFAULT in Parameter not supported";

                log.finest( error );
                m_conversionError = error;

                return result;
            }

            signature.append( "(" );

            if( group.length() > 2 ) {
                group = group.substring( 1,group.length() - 1 );

                // Paraneters are delimited by ,

                String[] parameters = group.split( "," );

                for( int i = 0;i < parameters.length;i++ ) {
                    if( i != 0 ) {
                        signature.append( ", " );
                    }

                    // name ALIAS FOR $1

                    String p = parameters[ i ].trim();

                    alias.append( p.substring( 0,p.indexOf( " " ))).append( " ALIAS FOR $" ).append( i + 1 ).append( ";\n" );

                    // Datatape

                    signature.append( p.substring( p.lastIndexOf( " " ) + 1 ));
                }
            }

            signature.append( ")" );
            sb.append( signature );

            // log.finest("Alias: " + alias.toString());
            // log.finest("Signature: " + signature.toString());

        }

        // No Parameters

        else {
            String error = "Missing Parameter ()";

            log.finest( error );
            m_conversionError = error;

            return result;
        }

        sb.append( "\n" );

        // Need to create drop statement

        if( orReplacePos != -1 ) {
            String drop = "DROP " + name + signature.toString();

            // log.finest(drop);

            result.add( drop );
        }

        // log.finest("1>" + sb.toString() + "<1");

        // Second Group -> RETURN VARCHAR AS
        // RETURNS VARCHAR AS

        m.find();
        group = m.group();
        m.appendReplacement( sb,"" );

        if( group.startsWith( "RETURN" )) {
            sb.append( "RETURNS" ).append( group.substring( group.indexOf( " " )));
        }

        sb.append( " '\nDECLARE\n" ).append( alias );    // add aliases here

        // log.finest("2>" + sb.toString() + "<2");

        // remainder statements

        while( m.find()) {
            String group2 = m.group();

            if( group2.indexOf( '$' ) != -1 ) {    // Group character needs to be escaped
                group2 = Util.replace( group2,"$","\\$" );
            }

            m.appendReplacement( sb,group2 );
            sb.append( "\n" );
        }

        m.appendTail( sb );

        // finish

        sb.append( "' LANGUAGE 'plpgsql';" );

        // log.finest(">" + sb.toString() + "<");

        result.add( sb.toString());

        //

        return result;
    }    // convertProcedure

    /**
     *  Descripción de Método
	 *  Convert Trigger.
	 *  <pre>
	 *      DROP FUNCTION emp_trgF();
	 *      CREATE FUNCTION emp_trg () RETURNS OPAQUE AS '....
	 *          RETURN NEW; ...
	 *          ' LANGUAGE 'plpgsql';
	 *      DROP TRIGGER emp_trg ON emp;
	 *      CREATE TRIGGER emp_trg BEFORE INSERT OR UPDATE ON emp
	 *      FOR EACH ROW EXECUTE PROCEDURE emp_trgF();
	 *  </pre>
     * @param sqlStatement
     *
	 *  @return CREATE and DROP TRIGGER and associated Function statement
     */

    private ArrayList convertTrigger( String sqlStatement ) {
        ArrayList result = new ArrayList();

        // Convert statement - to avoid handling contents of comments

        String stmt = converSimpleStatement( sqlStatement );

        // Trigger specific replacements

        stmt = Pattern.compile( "\\bINSERTING\\b" ).matcher( stmt ).replaceAll( "TG_OP='INSERT'" );
        stmt = Pattern.compile( "\\bUPDATING\\b" ).matcher( stmt ).replaceAll( "TG_OP='UPDATE'" );
        stmt = Pattern.compile( "\\bDELETING\\b" ).matcher( stmt ).replaceAll( "TG_OP='DELETE'" );
        stmt = Pattern.compile( ":new." ).matcher( stmt ).replaceAll( "NEW." );
        stmt = Pattern.compile( ":old." ).matcher( stmt ).replaceAll( "OLD." );

        // Double quotes '

        stmt = Pattern.compile( "'" ).matcher( stmt ).replaceAll( "''" );

        // remove OR REPLACE

        int orReplacePos = stmt.toUpperCase().indexOf( " OR REPLACE " );

        // trigger Name

        int    triggerPos  = stmt.toUpperCase().indexOf( " TRIGGER " ) + 9;
        String triggerName = stmt.substring( triggerPos );

        triggerName = triggerName.substring( 0,triggerName.indexOf( " " ));

        // table name

        String tableName = stmt.substring( stmt.toUpperCase().indexOf( " ON " ) + 4 );

        tableName = tableName.substring( 0,tableName.indexOf( " " ));

        // Function Drop

        if( orReplacePos != -1 ) {
            String drop = "DROP FUNCTION " + triggerName + "F()";

            // log.finest(drop);

            result.add( drop );
        }

        // Function & Trigger

        int pos = stmt.indexOf( "DECLARE " );

        if( pos == -1 ) {
            pos = stmt.indexOf( "BEGIN " );
        }

        String       functionCode = stmt.substring( pos );
        StringBuffer triggerCode  = new StringBuffer( "CREATE TRIGGER " );

        triggerCode.append( triggerName ).append( "\n" ).append( stmt.substring( triggerPos + triggerName.length(),pos )).append( "\nEXECUTE PROCEDURE " ).append( triggerName ).append( "F();" );

        // Add NEW to existing Return   --> DELETE Trigger ?

        functionCode = Pattern.compile( "\\bRETURN;",Pattern.CASE_INSENSITIVE ).matcher( functionCode ).replaceAll( "RETURN NEW;" );

        // Add final return and change name

        functionCode = Pattern.compile( "\\bEND " + triggerName + ";",Pattern.CASE_INSENSITIVE ).matcher( functionCode ).replaceAll( "\nRETURN NEW;\nEND " + triggerName + "F;" );

        // Line separators

        String match = "(\\(.*\\))"    // (.) Parameter
                       + "|(;)"        // Statement End

        // Nice to have - for readability

        + "|(\\bBEGIN\\b)"    // BEGIN
        + "|(\\bTHEN\\b)" + "|(\\bELSE\\b)" + "|(\\bELSIF\\b)";
        Matcher m = Pattern.compile( match,Pattern.CASE_INSENSITIVE ).matcher( functionCode );

        // Function Header

        StringBuffer sb = new StringBuffer( "CREATE FUNCTION " );

        sb.append( triggerName ).append( "F() RETURNS OPAQUE AS '\n" );

        // remainder statements

        while( m.find()) {
            String group = m.group();

            if( group.indexOf( '$' ) != -1 ) {    // Group character needs to be escaped
                group = Util.replace( group,"$","\\$" );
            }

            m.appendReplacement( sb,group );
            sb.append( "\n" );
        }

        m.appendTail( sb );

        // finish Function

        sb.append( "' LANGUAGE 'plpgsql';" );

        // log.finest(">" + sb.toString() + "<");

        result.add( sb.toString());

        // Trigger Drop

        if( orReplacePos != -1 ) {
            String drop = "DROP TRIGGER " + triggerName.toLowerCase() + " ON " + tableName;

            // log.finest(drop);

            result.add( drop );
        }

        // Trigger
        // Remove Column references OF ... ON

        String trigger = Pattern.compile( "\\sOF.*ON\\s" ).matcher( triggerCode ).replaceAll( " ON " );

        // log.finest(trigger);

        result.add( trigger );

        //

        return result;
    }    // convertTrigger

    /**
     *  Descripción de Método
	 *  Convert View.
	 *  Handle CREATE OR REPLACE
     * @param sqlStatement
     *
	 *  @return converted statement(s)
     */

    private ArrayList convertView( String sqlStatement ) {
        ArrayList result = new ArrayList();
        String    stmt   = converSimpleStatement( sqlStatement );

        // remove OR REPLACE

        int orReplacePos = stmt.toUpperCase().indexOf( " OR REPLACE " );

        if( orReplacePos != -1 ) {
            int    index = stmt.indexOf( " VIEW " );
            int    space = stmt.indexOf( ' ',index + 6 );
            String drop  = "DROP VIEW " + stmt.substring( index + 6,space );

            result.add( drop );

            //

            String create = "CREATE" + stmt.substring( index );

            result.add( create );
        } else {    // simple statement
            result.add( stmt );
        }

        return result;
    }    // convertView

    /**
     * Descripción de Método
	 *  Converts Decode, Outer Join and Sequence.
	 *  <pre>
	 *      DECODE (a, 1, 'one', 2, 'two', 'none')
	 *       => CASE WHEN a = 1 THEN 'one' WHEN a = 2 THEN 'two' ELSE 'none' END
     *
	 *      AD_Error_Seq.nextval
	 *       => nextval('AD_Error_Seq')
     *
	 *      RAISE_APPLICATION_ERROR (-20100, 'Table Sequence not found')
	 *       => RAISE EXCEPTION 'Table Sequence not found'
	 *
	 *  </pre>
     *  @param sqlStatement
	 *  @return converted statement
     */

    private String convertComplexStatement( String sqlStatement ) {
        String       retValue = sqlStatement;
        StringBuffer sb       = null;

        // Convert all decode parts

        while( retValue.indexOf( "DECODE" ) != -1 ) {
            retValue = convertDecode( retValue );
        }
		/**
		 * Sequence Handling --------------------------------------------------
		 *  AD_Error_Seq.nextval
		 *  => nextval('AD_Error_Seq')
		 */

        Matcher m = Pattern.compile( "\\w+\\.(nextval)|(curval)",Pattern.CASE_INSENSITIVE ).matcher( retValue );

        sb = new StringBuffer();

        while( m.find()) {
            String group = m.group();

            // System.out.print("-> " + group);

            int    pos      = group.indexOf( "." );
            String seqName  = group.substring( 0,pos );
            String funcName = group.substring( pos + 1 );

            group = funcName + "('" + seqName + "')";

            // log.finest(" => " + group);

            if( group.indexOf( '$' ) != -1 ) {    // Group character needs to be escaped
                group = Util.replace( group,"$","\\$" );
            }

            m.appendReplacement( sb,group );
        }

        m.appendTail( sb );
        retValue = sb.toString();

		/**
		 * RAISE --------------------------------------------------------------
		 *  RAISE_APPLICATION_ERROR (-20100, 'Table Sequence not found')
		 *  => RAISE EXCEPTION 'Table Sequence not found'
		 */
        m        = Pattern.compile( "RAISE_APPLICATION_ERROR\\s*\\(.+'\\)",Pattern.CASE_INSENSITIVE ).matcher( retValue );
        sb = new StringBuffer();

        while( m.find()) {
            String group = m.group();

            System.out.print( "-> " + group );

            String result = "RAISE EXCEPTION " + group.substring( group.indexOf( '\'' ),group.lastIndexOf( '\'' ) + 1 );

            log.finest( " => " + result );

            if( result.indexOf( '$' ) != -1 ) {    // Group character needs to be escaped
                result = Util.replace( result,"$","\\$" );
            }

            m.appendReplacement( sb,result );
        }

        m.appendTail( sb );
        retValue = sb.toString();

        // Truncate Handling -------------------------------------------------

        while( retValue.indexOf( "TRUNC" ) != -1 ) {
            retValue = convertTrunc( retValue );
        }

        // Outer Join Handling -----------------------------------------------

        int index = retValue.indexOf( "SELECT " );

        if( (index != -1) && (retValue.indexOf( "(+)",index ) != -1) ) {
            retValue = convertOuterJoin( retValue );
        }

        return retValue;
    }    // convertComplexStatement

    /**
     * Descripción de Método Converts Decode.
	 *  <pre>
	 *      DECODE (a, 1, 'one', 2, 'two', 'none')
	 *       => CASE WHEN a = 1 THEN 'one' WHEN a = 2 THEN 'two' ELSE 'none' END
	 *  </pre>
     * @param sqlStatement
     *
	 *  @return converted statement
     */

    private String convertDecode( String sqlStatement ) {

        // log.finest("DECODE<== " + sqlStatement);

        String       statement = sqlStatement;
        StringBuffer sb        = new StringBuffer( "CASE" );
        int          index     = statement.indexOf( "DECODE" );
        String       firstPart = statement.substring( 0,index );

        // find the opening (

        index     = statement.indexOf( '(',index );
        statement = statement.substring( index + 1 );

        // find the expression "a" - find first , ignoring ()

        index = Util.findIndexOf( statement,',' );

        String expression = statement.substring( 0,index ).trim();

        // log.finest("Expression=" + expression);

        // Pairs "1, 'one',"

        statement = statement.substring( index + 1 );
        index     = Util.findIndexOf( statement,',' );

        while( index != -1 ) {
            String first = statement.substring( 0,index );
            char   cc    = statement.charAt( index );

            statement = statement.substring( index + 1 );

            // log.finest("First=" + first + ", Char=" + cc);
            //

            boolean error = false;

            if( cc == ',' ) {
                index = Util.findIndexOf( statement,',',')' );

                if( index == -1 ) {
                    error = true;
                } else {
                    String second = statement.substring( 0,index );

                    sb.append( " WHEN " ).append( expression ).append( "=" ).append( first.trim()).append( " THEN " ).append( second.trim());

                    // log.finest(">>" + sb.toString());

                    statement = statement.substring( index + 1 );
                    index     = Util.findIndexOf( statement,',',')' );
                }
            } else if( cc == ')' ) {
                sb.append( " ELSE " ).append( first.trim()).append( " END" );

                // log.finest(">>" + sb.toString());

                index = -1;
            } else {
                error = true;
            }

            if( error ) {
                log.log( Level.SEVERE,"SQL=(" + sqlStatement + ")\n====Result=(" + sb.toString() + ")\n====Statement=(" + statement + ")\n====First=(" + first + ")\n====Index=" + index );
                m_conversionError = "Decode conversion error";
            }
        }

        sb.append( statement );
        sb.insert( 0,firstPart );

        // log.finest("DECODE==> " + sb.toString());

        return sb.toString();
    }    // convertDecode

    /**
     * Descripción de Método
	 *  Convert Outer Join.
	 *  Converting joins can ve very complex when multiple tables/keys are involved.
	 *  The main scenarios supported are two tables with multiple key columns
	 *  and multiple tables with single key columns.
	 *  <pre>
	 *      SELECT a.Col1, b.Col2 FROM tableA a, tableB b WHERE a.ID=b.ID(+)
	 *      => SELECT a.Col1, b.Col2 FROM tableA a LEFT OUTER JOIN tableB b ON (a.ID=b.ID)
     *
	 *      SELECT a.Col1, b.Col2 FROM tableA a, tableB b WHERE a.ID(+)=b.ID
	 *      => SELECT a.Col1, b.Col2 FROM tableA a RIGHT OUTER JOIN tableB b ON (a.ID=b.ID)
	 *  Assumptions:
	 *  - No outer joins in sub queries (ignores sub-queries)
	 *  - OR condition ignored (not sure what to do, should not happen)
	 *  Limitations:
	 *  - Parameters for outer joins must be first - as sequence of parameters changes
	 *  </pre>
     * @param sqlStatement
     *
	 *  @return converted statement
     */

    private String convertOuterJoin( String sqlStatement ) {
        boolean trace = false;

        //

        int fromIndex = Util.findIndexOf( sqlStatement.toUpperCase()," FROM " );
        int whereIndex = Util.findIndexOf( sqlStatement.toUpperCase()," WHERE " );

// comienzo cambios para PostgreSQL
        // int endWhereIndex = Util.findIndexOf(sqlStatement.toUpperCase(), " GRPUP BY ");

        int endWhereIndex = Util.findIndexOf( sqlStatement.toUpperCase()," GROUP BY " );

// fin cambios para PostgreSQL

        if( endWhereIndex == -1 ) {
            endWhereIndex = Util.findIndexOf( sqlStatement.toUpperCase()," ORDER BY " );
        }

        if( endWhereIndex == -1 ) {
            endWhereIndex = sqlStatement.length();
        }

        //

        if( trace ) {
            log.finest( "OuterJoin<== " + sqlStatement );

            // log.finest("From=" + fromIndex + ", Where=" + whereIndex + ", End=" + endWhereIndex + ", Length=" + sqlStatement.length());

        }

        //

        String selectPart = sqlStatement.substring( 0,fromIndex );
        String fromPart   = sqlStatement.substring( fromIndex,whereIndex );
        String wherePart  = sqlStatement.substring( whereIndex,endWhereIndex );
        String rest       = sqlStatement.substring( endWhereIndex );

        // find/remove all (+) from WHERE clase ------------------------------

        String    newWherePart = wherePart;
        ArrayList joins        = new ArrayList();
        int       pos          = newWherePart.indexOf( "(+)" );

        while( pos != -1 ) {

            // find starting point

            int start       = newWherePart.lastIndexOf( " AND ",pos );
            int startOffset = 5;

            if( start == -1 ) {
                start       = newWherePart.lastIndexOf( " OR ",pos );
                startOffset = 4;
            }

            if( start == -1 ) {
                start       = newWherePart.lastIndexOf( "WHERE ",pos );
                startOffset = 6;
            }

            if( start == -1 ) {
                String error = "Start point not found in clause " + wherePart;

                log.severe( error );
                m_conversionError = error;

                return sqlStatement;
            }

            // find end point

            int end = newWherePart.indexOf( " AND ",pos );

            if( end == -1 ) {
                end = newWherePart.indexOf( " OR ",pos );
            }

            if( end == -1 ) {
                end = newWherePart.length();
            }

            // log.finest("<= " + newWherePart + " - Start=" + start + "+" + startOffset + ", End=" + end);

            // extract condition

            String condition = newWherePart.substring( start + startOffset,end );

            joins.add( condition );

            if( trace ) {
                log.finest( "->" + condition );
            }

            // new WHERE clause

            newWherePart = newWherePart.substring( 0,start ) + newWherePart.substring( end );

            // log.finest("=> " + newWherePart);
            //

            pos = newWherePart.indexOf( "(+)" );
        }

        // correct beginning

        newWherePart = newWherePart.trim();

        if( newWherePart.startsWith( "AND " )) {
            newWherePart = "WHERE" + newWherePart.substring( 3 );
        } else if( newWherePart.startsWith( "OR " )) {
            newWherePart = "WHERE" + newWherePart.substring( 2 );
        }

        if( trace ) {
            log.finest( "=> " + newWherePart );
        }

        // Correct FROM clause -----------------------------------------------
        // Disassemble FROM

        String[] fromParts  = fromPart.trim().substring( 4 ).split( "," );
        HashMap  fromAlias  = new HashMap();    // tables to be processed
        HashMap  fromLookup = new HashMap();    // used tabled

        for( int i = 0;i < fromParts.length;i++ ) {
            String entry = fromParts[ i ].trim();
            String alias = entry;                                    // no alias
            String table = entry;
            int    aPos  = entry.lastIndexOf( ' ' );

            if( aPos != -1 ) {
                alias = entry.substring( aPos + 1 );
                table = entry.substring( 0,entry.indexOf( ' ' ));    // may have AS
            }

            fromAlias.put( alias,table );
            fromLookup.put( alias,table );

            if( trace ) {
                log.finest( "Alias=" + alias + ", Table=" + table );
            }
        }

        StringBuffer newFrom = new StringBuffer();

        for( int i = 0;i < joins.size();i++ ) {
            Join first = new Join(( String )joins.get( i ));

            first.setMainTable(( String )fromLookup.get( first.getMainAlias()));
            fromAlias.remove( first.getMainAlias());    // remove from list
            first.setJoinTable(( String )fromLookup.get( first.getJoinAlias()));
            fromAlias.remove( first.getJoinAlias());    // remove from list

            if( trace ) {
                log.finest( "-First: " + first );
            }

            //

            if( newFrom.length() == 0 ) {
                newFrom.append( " FROM " );
            } else {
                newFrom.append( ", " );
            }

            newFrom.append( first.getMainTable()).append( " " ).append( first.getMainAlias()).append( first.isLeft()
                    ?" LEFT"
                    :" RIGHT" ).append( " OUTER JOIN " ).append( first.getJoinTable()).append( " " ).append( first.getJoinAlias()).append( " ON (" ).append( first.getCondition());

            // keep it open - check for other key comparisons

            for( int j = i + 1;j < joins.size();j++ ) {
                Join second = new Join(( String )joins.get( j ));

                second.setMainTable(( String )fromLookup.get( second.getMainAlias()));
                second.setJoinTable(( String )fromLookup.get( second.getJoinAlias()));

                if(( first.getMainTable().equals( second.getMainTable()) && first.getJoinTable().equals( second.getJoinTable())) || second.isConditionOf( first )) {
                    if( trace ) {
                        log.finest( "-Second/key: " + second );
                    }

                    newFrom.append( " AND " ).append( second.getCondition());
                    joins.remove( j );                          // remove from join list
                    fromAlias.remove( first.getJoinAlias());    // remove from table list

                    // ----

                    for( int k = i + 1;k < joins.size();k++ ) {
                        Join third = new Join(( String )joins.get( k ));

                        third.setMainTable(( String )fromLookup.get( third.getMainAlias()));
                        third.setJoinTable(( String )fromLookup.get( third.getJoinAlias()));

                        if( third.isConditionOf( second )) {
                            if( trace ) {
                                log.finest( "-Third/key: " + third );
                            }

                            newFrom.append( " AND " ).append( third.getCondition());
                            joins.remove( k );                          // remove from join list
                            fromAlias.remove( third.getJoinAlias());    // remove from table list
                        } else if( trace ) {
                            log.finest( "-Third/key-skip: " + third );
                        }
                    }
                } else if( trace ) {
                    log.finest( "-Second/key-skip: " + second );
                }
            }

            newFrom.append( ")" );    // close ON

            // check dependency on first table

            for( int j = i + 1;j < joins.size();j++ ) {
                Join second = new Join(( String )joins.get( j ));

                second.setMainTable(( String )fromLookup.get( second.getMainAlias()));
                second.setJoinTable(( String )fromLookup.get( second.getJoinAlias()));

                if( first.getMainTable().equals( second.getMainTable())) {
                    if( trace ) {
                        log.finest( "-Second/dep: " + second );
                    }

                    // FROM (AD_Field f LEFT OUTER JOIN AD_Column c ON (f.AD_Column_ID = c.AD_Column_ID))
                    // LEFT OUTER JOIN AD_FieldGroup fg ON (f.AD_FieldGroup_ID = fg.AD_FieldGroup_ID),

                    newFrom.insert( 6,'(' );    // _FROM ...
                    newFrom.append( ')' );      // add parantesis on previous relation

                    //

                    newFrom.append( second.isLeft()
                                    ?" LEFT"
                                    :" RIGHT" ).append( " OUTER JOIN " ).append( second.getJoinTable()).append( " " ).append( second.getJoinAlias()).append( " ON (" ).append( second.getCondition());
                    joins.remove( j );                           // remove from join list
                    fromAlias.remove( second.getJoinAlias());    // remove from table list

                    // additional join colums would come here

                    newFrom.append( ")" );    // close ON

                    // ----

                    for( int k = i + 1;k < joins.size();k++ ) {
                        Join third = new Join(( String )joins.get( k ));

                        third.setMainTable(( String )fromLookup.get( third.getMainAlias()));
                        third.setJoinTable(( String )fromLookup.get( third.getJoinAlias()));

                        if( second.getJoinTable().equals( third.getMainTable())) {
                            if( trace ) {
                                log.finest( "-Third-dep: " + third );
                            }

                            // FROM ((C_BPartner p LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID))
                            // LEFT OUTER JOIN C_BPartner_Location l ON (p.C_BPartner_ID=l.C_BPartner_ID))
                            // LEFT OUTER JOIN C_Location a ON (l.C_Location_ID=a.C_Location_ID)

                            newFrom.insert( 6,'(' );    // _FROM ...
                            newFrom.append( ')' );      // add parantesis on previous relation

                            //

                            newFrom.append( third.isLeft()
                                            ?" LEFT"
                                            :" RIGHT" ).append( " OUTER JOIN " ).append( third.getJoinTable()).append( " " ).append( third.getJoinAlias()).append( " ON (" ).append( third.getCondition());
                            joins.remove( k );                          // remove from join list
                            fromAlias.remove( third.getJoinAlias());    // remove from table list

                            // additional join colums would come here

                            newFrom.append( ")" );    // close ON
                        } else if( trace ) {
                            log.finest( "-Third-skip: " + third );
                        }
                    }
                } else if( trace ) {
                    log.finest( "-Second/dep-skip: " + second );
                }
            }                                         // dependency on first table
        }

        // remaining Tables

        Iterator it = fromAlias.keySet().iterator();

        while( it.hasNext()) {
            Object alias = it.next();
            Object table = fromAlias.get( alias );

            newFrom.append( ", " ).append( table );

            if( !table.equals( alias )) {
                newFrom.append( " " ).append( alias );
            }
        }

        if( trace ) {
            log.finest( newFrom.toString());
        }

        //

        StringBuffer retValue = new StringBuffer( sqlStatement.length() + 20 );

        retValue.append( selectPart ).append( newFrom ).append( " " ).append( newWherePart ).append( rest );

        //

        if( trace ) {
            log.finest( "OuterJoin==> " + retValue.toString());
        }

        return retValue.toString();
    }    // convertOuterJoin

    /**
     *  Descripción de Método Convert RowNum.
	 *  <pre>
	 *      SELECT Col1 FROM tableA WHERE ROWNUM=1
	 *      => SELECT Col1 FROM tableA LIMIT 1
	 *  Assumptions/Limitations:
	 *  - RowNum not used in SELECT part
	 *  </pre>
     *  @param sqlStatement
     *
	 *  @return converted statement
     */

    private String convertRowNum( String sqlStatement ) {
        log.finest( "RowNum<== " + sqlStatement );
       
        String retValue = null;
        int    rownum   = sqlStatement.indexOf( "AND ROWNUM=1" );

        if( sqlStatement.indexOf( "AND ROWNUM=1" ) > 1 ) {
            //retValue = Util.replace( sqlStatement,"AND ROWNUM=1","" );
            retValue = Util.replace( sqlStatement,"AND ROWNUM=1","LIMIT 1" );

            //return retValue + " LIMIT 1"; (original)
        }    
        /*Anuludo por momentaneamente por ConSerTi.    
            return retValue + " LIMIT 1"; 
        } else if( sqlStatement.indexOf( "AND ROWNUM= 1" ) > 1 ) {
            retValue = Util.replace( sqlStatement,"AND ROWNUM= 1","" );

            return retValue + " LIMIT 1";
        } else if( sqlStatement.indexOf( "AND ROWNUM = 1" ) > 1 ) {
            retValue = Util.replace( sqlStatement,"AND ROWNUM = 1","" );

            return retValue + " LIMIT 1";
        } else if( sqlStatement.indexOf( "AND ROWNUM =1" ) > 1 ) {
            retValue = Util.replace( sqlStatement,"AND ROWNUM =1","" );

            return retValue + " LIMIT 1";
        }

        // log.finest("RowNum==> " + retValue); (fin anulado)*/ 

        return retValue;
    }    // convertRowNum

    /**
     * Descripción de Método
	 *  Convert TRUNC.
	 *  Assumed that it is used for date only!
     * @param sqlStatement
	 *  @return converted statement
     */

    private String convertTrunc( String sqlStatement ) {

        // comienzo cambios para PostgreSQL

        if( DB.isSybase()) {
            return Util.replace( sqlStatement,"TRUNC(","convert(date," );
        }

     /**
	 *  <pre>
	 *      TRUNC(myDate)
	 *      => DATE_Trunc('day',myDate)
	 *
	 *      TRUNC(myDate,'oracleFormat')
	 *      => DATE_Trunc('pgFormat',myDate)
	 *
	 *      Oracle          =>  PostgreSQL  (list not complete!)
	 *          Q               quarter
	 *          MM              month
	 *          DD              day
	 *      Spacial handling of DAY,DY  (Starting dat of the week)
	 *      => DATE_Trunc('day',($1-DATE_PART('dow',$1)));
	 *  </pre>
     *  
     */


        int    index           = sqlStatement.indexOf( "TRUNC" );
        String beforeStatement = sqlStatement.substring( 0,index );
        String afterStatement  = sqlStatement.substring( index );

        afterStatement = afterStatement.substring( afterStatement.indexOf( '(' ) + 1 );
        index = Util.findIndexOf( afterStatement,')' );

        String temp = afterStatement.substring( 0,index ).trim();

        afterStatement = afterStatement.substring( index + 1 );

        // log.finest("Trunc<== " + temp);

        StringBuffer retValue = new StringBuffer( "DATE_Trunc(" );    // lower case otherwise endless-loop

        if( temp.indexOf( ',' ) == -1 ) {
            retValue.append( "'day'," ).append( temp );
        } else    // with format
        {
            int    pos      = temp.indexOf( ',' );
            String variable = temp.substring( 0,pos ).trim();
            String format   = temp.substring( pos + 1 ).trim();

            if( format.equals( "'Q'" )) {
                retValue.append( "'quarter'," ).append( variable );
            } else if( format.equals( "'MM'" )) {
                retValue.append( "'month'," ).append( variable );
            } else if( format.equals( "'DD'" )) {
                retValue.append( "'day'," ).append( variable );
            } else if( format.equals( "'DY'" ) || format.equals( "'DAY'" )) {
                retValue.append( "'day',(" ).append( variable ).append( "-DATE_PART('dow'," ).append( variable ).append( "))" );
            } else {
                log.severe( "TRUNC format not supported: " + format );
                retValue.append( "'day'," ).append( variable );
            }
        }

        retValue.append( ')' );

        // log.finest("Trunc==> " + retValue.toString());
        //

        retValue.insert( 0,beforeStatement );
        retValue.append( afterStatement );

        return retValue.toString();

 // fin cambios para PostgreSQL

    }    // convertTrunc

 // comienzo cambios para PostgreSQL

    /**
     * Descripción de Método
	 *  Converts Decode.
	 *  <pre>
	 *      UPDATE C_Order i SET 
	 *       => UPDATE C_Order SET
	 *  </pre>
     * @param sqlStatement
     *
	 *  @return converted statement
     */

    private String convertUpdate( String sqlStatement ) {

        // log.finest("DECODE<== " + sqlStatement);

        String       statement = sqlStatement;
        StringBuffer sb        = new StringBuffer( "UPDATE" );
        int          index     = statement.toUpperCase().indexOf( "UPDATE " );

        // String firstPart = statement.substring(0,index);

        int begintable = statement.indexOf( ' ',6 );

        // begin the opening ' ' begin Alias

        int begin = statement.indexOf( ' ',7 );

        // statement = statement.substring(begin);

        // end Alias

        int end = statement.toUpperCase().indexOf( "SET",0 );    // statement.indexOf("SET", 0 );

        // System.out.println("Convertion UPDATE e-evolution:" + statement.substring(0, begin ) + " " + statement.substring(end));
        // System.out.println("Statement Convert:" + statement);
        // System.out.println("begin Alias:" + begin + " end Alias:" + end );
        // System.out.println("Alias:" + statement.substring(begin, end).trim());

        String alias     = null;
        String sqlUpdate = null;

        if( end > begin ) {
            alias = statement.substring( begin,end ).trim() + ".";

            String table = statement.substring( begintable,begin ).trim();

            statement = statement.substring( 0,begin ) + " " + statement.substring( end );

            if( !alias.equals( "." )) {
                sqlUpdate = Util.replace( statement,alias,table + "." );    // Pattern.compile("\\b"+alias+"\\b", Pattern.CASE_INSENSITIVE)

                return sqlUpdate;
            }
        }

        sqlUpdate = statement;

        // System.out.println("Convert Update:"+sqlUpdate);

        return sqlUpdate;
    }    // convertDecode



    /**
     * Descripción de Método
	 *  Converts Decode.
	 *  <pre>
	 *      DELETE C_Order i WHERE  
	 *       => DELETE FROM C_Order WHERE  
	 *  </pre>
     * @param sqlStatement
     *
	 *  @return converted statement
     */

    private String convertDelete( String sqlStatement ) {
        String statement = sqlStatement;

        /**
         * int index = statement.toUpperCase().indexOf("DELETE ");
         * int begintable = statement.indexOf(' ', 6 );
         *
         * //  begin the opening ' ' begin Alias
         * int begin = statement.indexOf(' ', 7 );
         * //  end Alias
         * int end = statement.toUpperCase().indexOf("WHERE", 0 );
         * String alias =  null;
         * String sqlDelete = statement ;
         * if (end > begin)
         * {
         * alias = statement.substring(begin,end).trim()+".";
         * String table = statement.substring(begintable,begin).trim();
         * System.out.println("Table" + table);
         * statement = statement.substring(0,begin) + " " + statement.substring(end);
         * if (!alias.equals("."))
         * {
         * sqlDelete =  Util.replace(statement, alias , table + ".");
         * }
         * }
         *
         * //sqlDelete = Util.replace(sqlDelete, "DELETE " , "DELETE FROM ");
         * System.out.println("Convertion DELETE e-evolution:" + statement.substring(0, begin ) + " " + statement.substring(end));
         * System.out.println("Statement Convert:" + statement);
         * System.out.println("begin Alias:" + begin + " end Alias:" + end );
         * System.out.println("Alias:" + statement.substring(begin, end).trim());
         * System.out.println("Convert Delete:"+sqlDelete);
         */

        return Util.replace( statement,"DELETE ","DELETE FROM " );
    }    // convertDelete


    /**
     * Descripción de Método
	 *  Converts Decode.
	 *  <pre>
	 *      DELETE C_Order i WHERE  
	 *       => DELETE FROM C_Order WHERE  
	 *  </pre>
     *  @param sqlStatement
	 *  @return converted statement
     */

    private String convertAlias( String sqlStatement ) {
        String statement  = sqlStatement;
        int    index      = 0;
        int    begintable = 0;
        int    begin      = 0;
        int    end        = 0;
        String alias      = null;

        if( statement.toUpperCase().indexOf( "DELETE FROM " ) == 0 ) {
            index      = statement.toUpperCase().indexOf( "DELETE FROM " );
            begintable = statement.indexOf( ' ',11 );

            // begin the opening ' ' begin Alias

            begin = statement.indexOf( ' ',12 );

            // end Alias

            end = statement.toUpperCase().indexOf( "WHERE",0 );
        } else if( statement.toUpperCase().indexOf( "UPDATE " ) == 0 ) {
            index      = statement.toUpperCase().indexOf( "UPDATE " );

            // String firstPart = statement.substring(0,index);

            begintable = statement.indexOf( ' ',6 );

            // begin the opening ' ' begin Alias

            begin = statement.indexOf( ' ',7 );

            // statement = statement.substring(begin);

            // end Alias

            end = statement.toUpperCase().indexOf( "SET",0 );    // statement.indexOf("SET", 0 );
        } else {
            return statement;
        }

        String sqlAlias = statement;

        if( end > begin ) {
            alias = statement.substring( begin,end ).trim() + ".";

            String table = statement.substring( begintable,begin ).trim();

            // System.out.println( "Table" + table );
            statement = statement.substring( 0,begin ) + " " + statement.substring( end );

            if( !alias.equals( "." )) {
                sqlAlias = Util.replace( statement,alias,table + "." );
            }
        }

        // sqlDelete = Util.replace(sqlDelete, "DELETE " , "DELETE FROM ");
        // System.out.println("Convertion Alias:" + statement.substring(0, begin ) + " " + statement.substring(end));
        // System.out.println("Statement Convert:" + statement);
        // System.out.println("begin Alias:" + begin + " end Alias:" + end );
        // System.out.println("Alias:" + statement.substring(begin, end).trim());
        // System.out.println("SQL Alias:"+sqlAlias);

        return sqlAlias;
    }    // convertDelete

// fin modificaciones para PostgreSQL

}    // Convert



/*
 *  @(#)Convert.java   25.03.06
 * 
 *  Fin del fichero Convert.java
 *  
 *  Versión 2.2
 *
 */
