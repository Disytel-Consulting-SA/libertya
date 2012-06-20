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



package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MClick extends X_W_Click {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param W_Click_ID
     * @param trxName
     */

    public MClick( Properties ctx,int W_Click_ID,String trxName ) {
        super( ctx,W_Click_ID,trxName );

        if( W_Click_ID == 0 ) {
            setProcessed( false );
        }
    }    // MClick

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param TargetURL
     * @param trxName
     */

    public MClick( Properties ctx,String TargetURL,String trxName ) {
        this( ctx,0,trxName );
        setTargetURL( TargetURL );
        setW_ClickCount_ID( 0 );
    }    // MClick

    /**
     * Descripción de Método
     *
     *
     * @param TargetURL
     */

    public void setTargetURL( String TargetURL ) {
        super.setTargetURL( TargetURL );
    }    // setTargetURL

    /**
     * Descripción de Método
     *
     *
     * @param W_ClickCount_ID
     */

    public void setW_ClickCount_ID( int W_ClickCount_ID ) {

        // specific id

        if( W_ClickCount_ID != 0 ) {
            super.setW_ClickCount_ID( W_ClickCount_ID );

            return;
        }

        // clean up url

        String url = getTargetURL();

        if( (url == null) || (url.length() == 0) ) {
            return;
        }

        String exactURL = url;

        // remove everything before first / .

        if( url.startsWith( "http://" )) {
            url = url.substring( 7 );
        }

        int dot   = url.indexOf( '.' );
        int slash = url.indexOf( '/' );

        while( (dot > slash) && (slash != -1) ) {
            url   = url.substring( slash + 1 );
            dot   = url.indexOf( '.' );
            slash = url.indexOf( '/' );
        }

        // remove everything after /

        if( slash != -1 ) {
            url = url.substring( 0,slash );
        }

        log.fine( "For " + exactURL + " - " + url );

        //

        String sql = "SELECT W_ClickCount_ID, TargetURL FROM W_ClickCount WHERE TargetURL LIKE ?";
        int               exactW_ClickCount_ID = 0;
        PreparedStatement pstmt                = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setString( 1,"%" + url + "%" );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                W_ClickCount_ID = rs.getInt( 1 );

                if( exactURL.equals( rs.getString( 2 ))) {
                    exactW_ClickCount_ID = W_ClickCount_ID;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        // Set Click Count

        if( exactW_ClickCount_ID != 0 ) {
            W_ClickCount_ID = exactW_ClickCount_ID;
        }

        if( W_ClickCount_ID == 0 ) {
            log.warning( "Not found for " + exactURL + " - " + url );

            return;
        }

        setProcessed( true );
        super.setW_ClickCount_ID( W_ClickCount_ID );
    }    // setW_ClickCount_ID
}    // MClick



/*
 *  @(#)MClick.java   02.07.07
 * 
 *  Fin del fichero MClick.java
 *  
 *  Versión 2.2
 *
 */
