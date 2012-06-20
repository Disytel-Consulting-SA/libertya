/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c)  2003-2005 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.report;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.X_PA_Report;

/**
 * Descripción de Clase
 *
 *
 * @version    2.0, 22.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MReport extends X_PA_Report {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param PA_Report_ID
     * @param trxName
     */

    public MReport( Properties ctx,int PA_Report_ID,String trxName ) {
        super( ctx,PA_Report_ID, trxName );

        if( PA_Report_ID == 0 ) {

            // setName (null);
            // setPA_ReportLineSet_ID (0);
            // setPA_ReportColumnSet_ID (0);

            setListSources( false );
            setListTrx( false );
        } else {
            m_columnSet = new MReportColumnSet( ctx,getPA_ReportColumnSet_ID(),trxName );
            m_lineSet = new MReportLineSet( ctx,getPA_ReportLineSet_ID(),trxName );
        }
    }    // MReport

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MReport( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs, trxName );
        m_columnSet = new MReportColumnSet( ctx,getPA_ReportColumnSet_ID(),trxName );
        m_lineSet = new MReportLineSet( ctx,getPA_ReportLineSet_ID(),trxName );
    }    // MReport

    /** Descripción de Campos */

    private MReportColumnSet m_columnSet = null;

    /** Descripción de Campos */

    private MReportLineSet m_lineSet = null;

    /**
     * Descripción de Método
     *
     */

    public void list() {
        System.out.println( toString());

        if( m_columnSet != null ) {
            m_columnSet.list();
        }

        System.out.println();

        if( m_lineSet != null ) {
            m_lineSet.list();
        }
    }    // dump

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getWhereClause() {

        // AD_Client indirectly via AcctSchema

        StringBuffer sb = new StringBuffer();

        // Mandatory       AcctSchema

        sb.append( "C_AcctSchema_ID=" ).append( getC_AcctSchema_ID());

        //

        return sb.toString();
    }    // getWhereClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MReport[" ).append( getID()).append( " - " ).append( getName());

        if( getDescription() != null ) {
            sb.append( "(" ).append( getDescription()).append( ")" );
        }

        sb.append( " - C_AcctSchema_ID=" ).append( getC_AcctSchema_ID()).append( ", C_Calendar_ID=" ).append( getC_Calendar_ID());
        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MReportColumnSet getColumnSet() {
        return m_columnSet;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MReportLineSet getLineSet() {
        return m_lineSet;
    }
}    // MReport



/*
 *  @(#)MReport.java   22.03.06
 * 
 *  Fin del fichero MReport.java
 *  
 *  Versión 2.0
 *
 */
