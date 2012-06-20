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



package org.openXpertya.sqlj;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class OpenXpertya implements Serializable {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static String getVersion() {
        return "OpenXpertya SQLJ 2.0";
    }    // version

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static String getProperties() {
        StringBuffer sb = new StringBuffer();
        Enumeration  en = System.getProperties().keys();

        while( en.hasMoreElements()) {
            if( sb.length() != 0 ) {
                sb.append( " - " );
            }

            String key   = ( String )en.nextElement();
            String value = System.getProperty( key );

            sb.append( key ).append( "=" ).append( value );
        }

        return sb.toString();
    }    // environment

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     *
     * @throws SQLException
     */

    public static String getProperty( String key ) throws SQLException {
        if( (key == null) || (key.length() == 0) ) {
            return "null";
        }

        return System.getProperty( key,"NotFound" );
    }    // environment

    /** Descripción de Campos */

    public static final String TYPE_ORACLE = "oracle";

    /** Descripción de Campos */

    public static final String TYPE_SYBASE = "sybase";

    /** Descripción de Campos */

    public static final String TYPE_DB2 = "db2";

    // begin e-evolution vpj-cd 02/02/2005 PostgreSQL

    /** Descripción de Campos */

    public static final String TYPE_POSTGRESQL = "PostgreSQL";

    // end e-evolution vpj-cd 02/02/2005 PostgreSQL

    /** Descripción de Campos */

    public static String s_type = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static String getServerType() {
        if( s_type == null ) {
            String vendor = System.getProperty( "java.vendor" );

            if( vendor.startsWith( "Oracle" )) {
                s_type = TYPE_ORACLE;
            } else if( vendor.startsWith( "Sybase" )) {
                s_type = TYPE_SYBASE;
            } else {
                s_type = "??";
            }
        }

        return s_type;
    }    // getServerType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    static boolean isOracle() {
        if( s_type == null ) {
            getServerType();
        }

        if( s_type != null ) {
            return TYPE_ORACLE.equals( s_type );
        }

        return false;
    }    // isOracle

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    static boolean isSybase() {
        if( s_type == null ) {
            getServerType();
        }

        if( s_type != null ) {
            return TYPE_SYBASE.equals( s_type );
        }

        return false;
    }    // isSybase

    // begin vpj-cd e-evolution 02/22/2005 PostgreSQL

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    static boolean isPostgreSQL() {
        if( s_type == null ) {
            getServerType();
        }

        if( s_type != null ) {
            return TYPE_POSTGRESQL.equals( s_type );
        }

        return false;
    }    // isPostgreSQL

    // end vpj-cd e-evolution 02/22/2005 PostgreSQL

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    static String getConnectionURL() {
        if( s_url != null ) {
            return s_url;
        }

        if( isOracle()) {
            s_url = "jdbc:default:connection:";
        } else if( isSybase()) {
            s_url = "jdbc:default:connection";

            // begin vpj-cd e-evolution 02/22/2005 PostgreSQL

        } else if( isPostgreSQL()) {
            return "jdbc:default:connection";
        }

        return "jdbc:default:connection";

        //
        // return s_url;
        // end vpj-cd e-evolution 02/22/2005 PostgreSQL
        //

    }    // getConnectionURL

    /** Descripción de Campos */

    protected static String s_url = null;

    /** Descripción de Campos */

    protected static String s_uid = null;

    /** Descripción de Campos */

    protected static String s_pwd = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    private static Connection getConnection() throws SQLException {
        if( (s_uid != null) && (s_pwd != null) ) {
            return DriverManager.getConnection( getConnectionURL(),s_uid,s_pwd );
        }

        return DriverManager.getConnection( getConnectionURL());
    }    // getConnection

    /**
     * Descripción de Método
     *
     *
     * @param sql
     *
     * @return
     *
     * @throws SQLException
     */

    static PreparedStatement prepareStatement( String sql ) throws SQLException {
        return prepareStatement( sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );
    }    // prepareStatement

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param resultSetType
     * @param resultSetCurrency
     *
     * @return
     *
     * @throws SQLException
     */

    static PreparedStatement prepareStatement( String sql,int resultSetType,int resultSetCurrency ) throws SQLException {
        if( s_conn == null ) {
            s_conn = getConnection();
        }

        try {
            return s_conn.prepareStatement( sql,resultSetType,resultSetCurrency );
        } catch( Exception e )    // connection not good anymore
        {
        }

        // get new Connection

        s_conn = getConnection();

        return s_conn.prepareStatement( sql );
    }    //

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param param1
     *
     * @return
     *
     * @throws SQLException
     */

    static int getSQLValue( String sql,int param1 ) throws SQLException {
        int               retValue = -1;
        PreparedStatement pstmt    = prepareStatement( sql );

        pstmt.setInt( 1,param1 );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            retValue = rs.getInt( 1 );
        }

        rs.close();
        pstmt.close();

        return retValue;
    }    // getSQLValue

    /** Descripción de Campos */

    private static Connection s_conn = null;

    /** Descripción de Campos */

    public static final BigDecimal ZERO = new BigDecimal( 0.0 );

    /**
     * Descripción de Método
     *
     *
     * @param p_dateTime
     *
     * @return
     */

    public static Timestamp trunc( Timestamp p_dateTime ) {
        Timestamp time = p_dateTime;

        if( time == null ) {
            time = new Timestamp( System.currentTimeMillis());
        }

        //

        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( time );
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        //

        java.util.Date temp = cal.getTime();

        return new Timestamp( temp.getTime());
    }    // trunc

    /**
     * Descripción de Método
     *
     *
     * @param p_dateTime
     * @param XX
     *
     * @return
     */

    public static Timestamp firstOf( Timestamp p_dateTime,String XX ) {
        Timestamp time = p_dateTime;

        if( time == null ) {
            time = new Timestamp( System.currentTimeMillis());
        }

        //

        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( time );
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        //

        if( "MM".equals( XX )) {                   // Month
            cal.set( Calendar.DAY_OF_MONTH,1 );
        } else if( "DY".equals( XX )) {            // Week
            cal.set( Calendar.DAY_OF_WEEK,Calendar.SUNDAY );
        } else if( "Q".equals( XX ))               // Quarter
        {
            cal.set( Calendar.DAY_OF_MONTH,1 );

            int mm = cal.get( Calendar.MONTH );    // January = 0

            if( mm < Calendar.APRIL ) {
                cal.set( Calendar.MONTH,Calendar.JANUARY );
            } else if( mm < Calendar.JULY ) {
                cal.set( Calendar.MONTH,Calendar.APRIL );
            } else if( mm < Calendar.OCTOBER ) {
                cal.set( Calendar.MONTH,Calendar.JULY );
            } else {
                cal.set( Calendar.MONTH,Calendar.OCTOBER );
            }
        }

        //

        java.util.Date temp = cal.getTime();

        return new Timestamp( temp.getTime());
    }    // trunc

    /**
     * Descripción de Método
     *
     *
     * @param start
     * @param end
     *
     * @return
     */

    static public int getDaysBetween( Timestamp start,Timestamp end ) {
        boolean negative = false;

        if( end.before( start )) {
            negative = true;

            Timestamp temp = start;

            start = end;
            end   = temp;
        }

        //

        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( start );
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        GregorianCalendar calEnd = new GregorianCalendar();

        calEnd.setTime( end );
        calEnd.set( Calendar.HOUR_OF_DAY,0 );
        calEnd.set( Calendar.MINUTE,0 );
        calEnd.set( Calendar.SECOND,0 );
        calEnd.set( Calendar.MILLISECOND,0 );

        // System.out.println("Start=" + start + ", End=" + end + ", dayStart=" + cal.get(Calendar.DAY_OF_YEAR) + ", dayEnd=" + calEnd.get(Calendar.DAY_OF_YEAR));

        // in same year

        if( cal.get( Calendar.YEAR ) == calEnd.get( Calendar.YEAR )) {
            if( negative ) {
                return( calEnd.get( Calendar.DAY_OF_YEAR ) - cal.get( Calendar.DAY_OF_YEAR )) * -1;
            }

            return calEnd.get( Calendar.DAY_OF_YEAR ) - cal.get( Calendar.DAY_OF_YEAR );
        }

        // not very efficient, but correct

        int counter = 0;

        while( calEnd.after( cal )) {
            cal.add( Calendar.DAY_OF_YEAR,1 );
            counter++;
        }

        if( negative ) {
            return counter * -1;
        }

        return counter;
    }    // getDaysBetween

    /**
     * Descripción de Método
     *
     *
     * @param day
     * @param offset
     *
     * @return
     */

    static public Timestamp addDays( Timestamp day,int offset ) {
        if( day == null ) {
            day = new Timestamp( System.currentTimeMillis());
        }

        //

        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( day );
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        if( offset != 0 ) {
            cal.add( Calendar.DAY_OF_YEAR,offset );    // may have a problem with negative (before 1/1)
        }

        //

        java.util.Date temp = cal.getTime();

        return new Timestamp( temp.getTime());
    }    // addDays

    /**
     * Descripción de Método
     *
     *
     * @param day
     *
     * @return
     */

    static public Timestamp nextBusinessDay( Timestamp day ) {
        if( day == null ) {
            day = new Timestamp( System.currentTimeMillis());
        }

        //

        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( day );
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        //

        int dow = cal.get( Calendar.DAY_OF_WEEK );

        if( dow == Calendar.SATURDAY ) {
            cal.add( Calendar.DAY_OF_YEAR,2 );
        } else if( dow == Calendar.SUNDAY ) {
            cal.add( Calendar.DAY_OF_YEAR,1 );
        }

        //

        java.util.Date temp = cal.getTime();

        return new Timestamp( temp.getTime());
    }    // nextBusinessDay

    /**
     * Descripción de Método
     *
     *
     * @param source
     * @param posIndex
     *
     * @return
     */

    public static String charAt( String source,int posIndex ) {
        if( source == null ) {
            return null;
        }

        try {
           return( source.substring( posIndex)); 
		    //return( String.valueOf(source.charAt(posIndex-1)) );
        } catch( Exception e ) {
        }

        return null;
    }    // charAt

    /**
     * Descripción de Método
     *
     *
     * @param AD_Sequence_ID
     * @param System
     *
     * @return
     *
     * @throws SQLException
     */

    public static int nextID( int AD_Sequence_ID,String System ) throws SQLException {
        boolean      isSystem = (System != null) && "Y".equals( System );
        int          retValue = -1;
        StringBuffer sql      = new StringBuffer( "SELECT CurrentNext" );

        if( isSystem ) {
            sql.append( "Sys" );
        }

        sql.append( ",IncrementNo FROM AD_Sequence WHERE AD_Sequence_ID=?" );

        PreparedStatement pstmt = prepareStatement( sql.toString(),ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE );
        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            retValue = rs.getInt( 1 );

            int incrementNo = rs.getInt( 2 );

            rs.updateInt( 2,retValue + incrementNo );
            pstmt.getConnection().commit();
        }

        rs.close();
        pstmt.close();

        //

        return retValue;
    }    // nextID
}    // OpenXpertya



/*
 *  @(#)OpenXpertya.java   23.03.06
 * 
 *  Fin del fichero OpenXpertya.java
 *  
 *  Versión 2.2
 *
 */
