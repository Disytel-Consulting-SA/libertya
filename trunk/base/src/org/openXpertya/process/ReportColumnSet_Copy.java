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

import org.openXpertya.report.MReportColumn;
import org.openXpertya.report.MReportColumnSet;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ReportColumnSet_Copy extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public ReportColumnSet_Copy() {
        super();
    }    // ReportColumnSet_Copy

    /** Descripción de Campos */

    private int m_PA_ReportColumnSet_ID = 0;

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
            } else if( name.equals( "PA_ReportColumnSet_ID" )) {
                m_PA_ReportColumnSet_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
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

        log.info( "From PA_ReportColumnSet_ID=" + m_PA_ReportColumnSet_ID + ", To=" + to_ID );

        if( to_ID < 1 ) {
            throw new Exception( MSG_SaveErrorRowNotFound );
        }

        //

        MReportColumnSet to = new MReportColumnSet( getCtx(),to_ID,get_TrxName());
        MReportColumnSet rcSet = new MReportColumnSet( getCtx(),m_PA_ReportColumnSet_ID,get_TrxName());
        MReportColumn[] rcs = rcSet.getColumns();

        for( int i = 0;i < rcs.length;i++ ) {
            MReportColumn rc = MReportColumn.copy( getCtx(),to.getAD_Client_ID(),to.getAD_Org_ID(),to_ID,rcs[ i ],get_TrxName());

            rc.save();
        }

        // Oper 1/2 were set to Null !

        return "@Copied@=" + rcs.length;
    }    // doIt
}    // ReportColumnSet_Copy



/*
 *  @(#)ReportColumnSet_Copy.java   02.07.07
 * 
 *  Fin del fichero ReportColumnSet_Copy.java
 *  
 *  Versión 2.2
 *
 */
