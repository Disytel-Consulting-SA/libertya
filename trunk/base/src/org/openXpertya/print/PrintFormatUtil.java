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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrintFormatUtil {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     */

    public PrintFormatUtil( Properties ctx ) {
        super();
        m_ctx = ctx;
    }    // PrintFormatUtil

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private Properties m_ctx;

    /**
     * Descripción de Método
     *
     */

    public void addMissingColumns() {
        int               total = 0;
        String            sql   = "SELECT * FROM AD_PrintFormat pf " + "ORDER BY Name";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                total += addMissingColumns( new MPrintFormat( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"addMissingColumns",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        log.info( "Total = " + total );
    }    // addMissingColumns

    /**
     * Descripción de Método
     *
     *
     * @param pf
     *
     * @return
     */

    public int addMissingColumns( MPrintFormat pf ) {
        log.info( pf.toString());

        String sql = "SELECT c.AD_Column_ID, c.ColumnName " + "FROM AD_Column c " + "WHERE NOT EXISTS " + "(SELECT * " + "FROM AD_PrintFormatItem pfi" + " INNER JOIN AD_PrintFormat pf ON (pfi.AD_PrintFormat_ID=pf.AD_PrintFormat_ID) " + "WHERE pf.AD_Table_ID=c.AD_Table_ID" + " AND pfi.AD_Column_ID=c.AD_Column_ID" + " AND pfi.AD_PrintFormat_ID=?)"    // 1
                     + " AND c.AD_Table_ID=? "    // 2
                     + "ORDER BY 1";
        PreparedStatement pstmt   = null;
        int               counter = 0;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,pf.getAD_PrintFormat_ID());
            pstmt.setInt( 2,pf.getAD_Table_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int              AD_Column_ID = rs.getInt( 1 );
                String           ColumnName   = rs.getString( 2 );
                MPrintFormatItem pfi          = MPrintFormatItem.createFromColumn( pf,AD_Column_ID,0 );

                if( pfi.getID() != 0 ) {
                    log.info( "#" + ++counter + " - added " + ColumnName );
                } else {
                    log.warning( "Not added: " + ColumnName );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"addMissingColumns",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( counter == 0 ) {
            log.info( "None" );
        } else {
            log.info( "Added=" + counter );
        }

        return counter;
    }    // addMissingColumns

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        org.openXpertya.OpenXpertya.startupEnvironment( true );

        //

        PrintFormatUtil pfu = new PrintFormatUtil( Env.getCtx());

        pfu.addMissingColumns();
    }    // main
}    // PrintFormatUtils



/*
 *  @(#)PrintFormatUtil.java   23.03.06
 * 
 *  Fin del fichero PrintFormatUtil.java
 *  
 *  Versión 2.2
 *
 */
