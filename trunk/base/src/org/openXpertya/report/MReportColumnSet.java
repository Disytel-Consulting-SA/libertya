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



package org.openXpertya.report;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.X_PA_ReportColumnSet;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MReportColumnSet extends X_PA_ReportColumnSet {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param PA_ReportColumnSet_ID
     * @param trxName
     */

    public MReportColumnSet( Properties ctx,int PA_ReportColumnSet_ID,String trxName ) {
        super( ctx,PA_ReportColumnSet_ID,trxName );

        if( PA_ReportColumnSet_ID == 0 ) {}
        else {
            loadColumns();
        }
    }    // MReportColumnSet

    /** Descripción de Campos */

    private MReportColumn[] m_columns = null;

    /**
     * Descripción de Método
     *
     */

    private void loadColumns() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM PA_ReportColumn WHERE PA_ReportColumnSet_ID=? AND IsActive='Y' ORDER BY SeqNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getPA_ReportColumnSet_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MReportColumn( getCtx(),rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"MReportColumnSet.loadColumns",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        //

        m_columns = new MReportColumn[ list.size()];
        list.toArray( m_columns );
        log.finest( "ID=" + getPA_ReportColumnSet_ID() + " - Size=" + list.size());
    }    // loadColumns

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MReportColumn[] getColumns() {
        return m_columns;
    }    // getColumns

    /**
     * Descripción de Método
     *
     */

    public void list() {
        System.out.println( toString());

        if( m_columns == null ) {
            return;
        }

        for( int i = 0;i < m_columns.length;i++ ) {
            System.out.println( "- " + m_columns[ i ].toString());
        }
    }    // list

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MReportColumnSet[" ).append( getID()).append( " - " ).append( getName()).append( "]" );

        return sb.toString();
    }
}    // MReportColumnSet



/*
 *  @(#)MReportColumnSet.java   02.07.07
 * 
 *  Fin del fichero MReportColumnSet.java
 *  
 *  Versión 2.2
 *
 */
