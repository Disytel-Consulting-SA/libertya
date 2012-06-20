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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.openXpertya.report.MReportLine;
import org.openXpertya.report.MReportLineSet;
import org.openXpertya.report.MReportSource;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ReportLineSet_Copy extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public ReportLineSet_Copy() {
        super();
    }    // ReportLineSet_Copy

    /** Descripción de Campos */

    private int m_PA_ReportLineSet_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "PA_ReportLineSet_ID" )) {
                m_PA_ReportLineSet_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        int to_ID = super.getRecord_ID();

        log.info( "From PA_ReportLineSet_ID=" + m_PA_ReportLineSet_ID + ", To=" + to_ID );

        if( to_ID < 1 ) {
            throw new Exception( MSG_SaveErrorRowNotFound );
        }

        //

        MReportLineSet to    = new MReportLineSet( getCtx(),to_ID,get_TrxName());
        MReportLineSet rlSet = new MReportLineSet( getCtx(),m_PA_ReportLineSet_ID,get_TrxName());
        MReportLine[] rls = rlSet.getLiness();

        for( int i = 0;i < rls.length;i++ ) {
            MReportLine rl = MReportLine.copy( getCtx(),to.getAD_Client_ID(),to.getAD_Org_ID(),to_ID,rls[ i ],get_TrxName());

            rl.save();

            MReportSource[] rss = rls[ i ].getSources();

            if( rss != null ) {
                for( int ii = 0;ii < rss.length;ii++ ) {
                    MReportSource rs = MReportSource.copy( getCtx(),to.getAD_Client_ID(),to.getAD_Org_ID(),rl.getID(),rss[ ii ],get_TrxName());

                    rs.save();
                }
            }

            // Oper 1/2 were set to Null !

        }

        return "@Copied@=" + rls.length;
    }    // doIt
}    // ReportLineSet_Copy



/*
 *  @(#)ReportLineSet_Copy.java   02.07.07
 * 
 *  Fin del fichero ReportLineSet_Copy.java
 *  
 *  Versión 2.2
 *
 */
