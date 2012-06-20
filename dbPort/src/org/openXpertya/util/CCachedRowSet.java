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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Locale;

import javax.sql.RowSet;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;

import oracle.jdbc.rowset.OracleCachedRowSet;

import com.sun.rowset.CachedRowSetImpl;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CCachedRowSet extends CachedRowSetImpl implements CachedRowSet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public static CachedRowSet get() throws SQLException {
        CCachedRowSet crs = null;

        // only first time call

        if( s_loc == null ) {
            s_loc = Locale.getDefault();
            Locale.setDefault( Locale.US );
            crs = new CCachedRowSet();  
            Locale.setDefault( s_loc );
        } else {
            crs = new CCachedRowSet();
        }

        //

        return crs;
    }    // get

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

    public static RowSet getRowSet( String sql ) throws SQLException {
        CachedRowSet crs = get();
        crs.setConcurrency( ResultSet.CONCUR_READ_ONLY );
        crs.setType( ResultSet.TYPE_SCROLL_INSENSITIVE );
        crs.setCommand( sql );

        // Set Parameters
        // crs.execute(conn);

        return crs;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param conn
     *
     * @return
     *
     * @throws SQLException
     */

    public static RowSet getRowSet( String sql,Connection conn ) throws SQLException {
        if( DB.isOracle()) {
            Statement stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );
            ResultSet          rs  = stmt.executeQuery( sql );
            OracleCachedRowSet crs = new OracleCachedRowSet();

            crs.populate( rs );
            stmt.close();

            return crs;
        }

        CachedRowSet crs = get();

        crs.setConcurrency( ResultSet.CONCUR_READ_ONLY );
        crs.setType( ResultSet.TYPE_SCROLL_INSENSITIVE );
        crs.setCommand( sql );
        crs.execute( conn );

        return crs;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     *
     * @throws SQLException
     */

    public static RowSet getRowSet( ResultSet rs ) throws SQLException {
        if( DB.isOracle()) {
            OracleCachedRowSet crs = new OracleCachedRowSet();

            crs.populate( rs );

            return crs;
        }
        
        CachedRowSet crs = get(); //original
        //CachedRowSetImpl crs= new CachedRowSetImpl();
        crs.populate( rs );
         
        return crs;
    }    // get

    /** Descripción de Campos */

    private static Locale s_loc = null;

    /**
     * Constructor de la clase ...
     *
     *
     * @throws SQLException
     */

    private CCachedRowSet() throws SQLException {
        super();
        setSyncProvider( "com.sun.rowset.providers.RIOptimisticProvider" );
    }    // CCachedRowSet

    /**
     * Constructor de la clase ...
     *
     *
     * @param arg0
     *
     * @throws SQLException
     */

    private CCachedRowSet( Hashtable arg0 ) throws SQLException {
        super( arg0 );
        setSyncProvider( "com.sun.rowset.providers.RIOptimisticProvider" );
    }    // CCachedRowSet

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        try {
        	System.out.println( "en el main" );
            Locale.setDefault( Locale.CANADA );
            get();
            System.out.println( "OK 1" );
            get();
            System.out.println( "OK 1a" );
            new CachedRowSetImpl();
            System.out.println( "OK 2" );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }    // main
}    // CCachedRowSet



/*
 *  @(#)CCachedRowSet.java   25.03.06
 * 
 *  Fin del fichero CCachedRowSet.java
 *  
 *  Versión 2.2
 *
 */
