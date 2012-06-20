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



package org.openXpertya;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MProductDownload;
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

public class MigrateData {

    /**
     * Constructor de la clase ...
     *
     */

    public MigrateData() {
        release252c();
    }    // MigrateData

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MigrateData.class );

    /**
     * Descripción de Método
     *
     */

    private void release252c() {
        String sql = "SELECT COUNT(*) FROM M_ProductDownload";
        int    no  = DB.getSQLValue( null,sql );

        if( no > 0 ) {
            log.finer( "No Need - Downloads #" + no );

            return;
        }

        //

        int count = 0;

        sql = "SELECT AD_Client_ID, AD_Org_ID, M_Product_ID, Name, DownloadURL " + "FROM M_Product " + "WHERE DownloadURL IS NOT NULL";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int    AD_Client_ID = rs.getInt( 1 );
                int    AD_Org_ID    = rs.getInt( 2 );
                int    M_Product_ID = rs.getInt( 3 );
                String Name         = rs.getString( 4 );
                String DownloadURL  = rs.getString( 5 );

                //

                Properties ctx = new Properties( Env.getCtx());

                Env.setContext( ctx,"#AD_Client_ID",AD_Client_ID );
                Env.setContext( ctx,"AD_Client_ID",AD_Client_ID );
                Env.setContext( ctx,"#AD_Org_ID",AD_Org_ID );
                Env.setContext( ctx,"AD_Org_ID",AD_Org_ID );

                MProductDownload pdl = new MProductDownload( ctx,0,null );

                pdl.setM_Product_ID( M_Product_ID );
                pdl.setName( Name );
                pdl.setDownloadURL( DownloadURL );

                if( pdl.save()) {
                    count++;

                    String sqlUpdate = "UPDATE M_Product SET DownloadURL = NULL WHERE M_Product_ID=" + M_Product_ID;
                    int updated = DB.executeUpdate( sqlUpdate );

                    if( updated != 1 ) {
                        log.warning( "Product not updated" );
                    }
                } else {
                    log.warning( "Product Download not created M_Product_ID=" + M_Product_ID );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        log.info( "#" + count );
    }    // release252c

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        OpenXpertya.startup( true );
        new MigrateData();
    }    // main
}    // MigrateData



/*
 *  @(#)MigrateData.java   02.07.07
 * 
 *  Fin del fichero MigrateData.java
 *  
 *  Versión 2.2
 *
 */
