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
import java.util.ArrayList;
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

public class MDistributionRun extends X_M_DistributionRun {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_DistributionRun_ID
     * @param trxName
     */

    public MDistributionRun( Properties ctx,int M_DistributionRun_ID,String trxName ) {
        super( ctx,M_DistributionRun_ID,trxName );
    }    // MDistributionRun

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDistributionRun( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDistributionRun

    /** Descripción de Campos */

    private MDistributionRunLine[] m_lines = null;

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MDistributionRunLine[] getLines( boolean reload ) {
        if( !reload && (m_lines != null) ) {
            return m_lines;
        }

        //

        String sql = "SELECT * FROM M_DistributionRunLine " + "WHERE M_DistributionRun_ID=? AND IsActive='Y' AND TotalQty IS NOT NULL AND TotalQty<> 0 ORDER BY Line";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_DistributionRun_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MDistributionRunLine( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_lines = new MDistributionRunLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines
}    // MDistributionRun



/*
 *  @(#)MDistributionRun.java   02.07.07
 * 
 *  Fin del fichero MDistributionRun.java
 *  
 *  Versión 2.2
 *
 */
