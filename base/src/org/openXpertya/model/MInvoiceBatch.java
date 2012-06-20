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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MInvoiceBatch extends X_C_InvoiceBatch {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_InvoiceBatch_ID
     * @param trxName
     */

    public MInvoiceBatch( Properties ctx,int C_InvoiceBatch_ID,String trxName ) {
        super( ctx,C_InvoiceBatch_ID,trxName );

        if( C_InvoiceBatch_ID == 0 ) {

            // setDocumentNo (null);
            // setC_Currency_ID (0);   // @$C_Currency_ID@

            setControlAmt( Env.ZERO );                                  // 0
            setDateDoc( new Timestamp( System.currentTimeMillis()));    // @#Date@
            setDocumentAmt( Env.ZERO );
            setIsSOTrx( false );                                        // N
            setProcessed( false );

            // setSalesRep_ID (0);

        }
    }                                                                   // MInvoiceBatch

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInvoiceBatch( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInvoiceBatch

    /** Descripción de Campos */

    private MInvoiceBatchLine[] m_lines = null;

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MInvoiceBatchLine[] getLines( boolean reload ) {
        if( (m_lines != null) &&!reload ) {
            return m_lines;
        }

        String sql = "SELECT * FROM C_InvoiceBatchLine WHERE C_InvoiceBatch_ID=? ORDER BY Line";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_InvoiceBatch_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInvoiceBatchLine( getCtx(),rs,get_TrxName()));
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

        //

        m_lines = new MInvoiceBatchLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param processed
     */

    public void setProcessed( boolean processed ) {
        super.setProcessed( processed );

        if( getID() == 0 ) {
            return;
        }

        String set = "SET Processed='" + ( processed
                                           ?"Y"
                                           :"N" ) + "' WHERE C_InvoiceBatch_ID=" + getC_InvoiceBatch_ID();
        int noLine = DB.executeUpdate( "UPDATE C_InvoiceBatchLine " + set,get_TrxName());

        m_lines = null;
        log.fine( processed + " - Lines=" + noLine );
    }    // setProcessed
}    // MInvoiceBatch



/*
 *  @(#)MInvoiceBatch.java   02.07.07
 * 
 *  Fin del fichero MInvoiceBatch.java
 *  
 *  Versión 2.2
 *
 */
