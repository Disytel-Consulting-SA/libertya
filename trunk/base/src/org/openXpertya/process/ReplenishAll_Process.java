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
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.MQuarter;
import org.openXpertya.model.MQuarterCategory;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ReplenishAll_Process extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public ReplenishAll_Process() {
        super();
    }    // Quarter_Process

    /** Descripción de Campos */

    private boolean m_OutGoings = true;

    /** Descripción de Campos */

    private boolean m_Tam12 = true;

    /** Descripción de Campos */

    private boolean m_Tam3 = true;

    /** Descripción de Campos */

    private boolean m_Stock = true;

    /** Descripción de Campos */

    private boolean m_Incomings = true;

    /** Descripción de Campos */

    private int p_M_Warehouse_ID = 0;

    /** Descripción de Campos */

    StringBuffer info = new StringBuffer( "Replenish_Process - Calculate parameters: " );

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
            } else if( name.equals( "M_Warehouse_ID" )) {
                p_M_Warehouse_ID = para[ i ].getParameterAsInt();
                log.fine("M_WArehouse_prepare_QuarterProcess="+p_M_Warehouse_ID);
            } else if( name.equals( "OutGoings" )) {
                m_OutGoings = "Y".equals( para[ i ].getParameter());
                log.fine("M_outgoings_prepare_QuarterProcess="+m_OutGoings+para[ i ].getParameter());
            } else if( name.equals( "Tam12" )) {
                m_Tam12 = "Y".equals( para[ i ].getParameter());
                log.fine("M_Tam12_prepare_QuarterProcess="+m_Tam12+para[ i ].getParameter());
            } else if( name.equals( "Tam3" )) {
                m_Tam3 = "Y".equals( para[ i ].getParameter());
                log.fine("M_Tam3_prepare_QuarterProcess="+m_Tam3+para[ i ].getParameter());
            } else if( name.equals( "Stock" )) {
                m_Stock = "Y".equals( para[ i ].getParameter());
                log.fine("M_stock_prepare_QuarterProcess="+m_Stock+para[ i ].getParameter());
            } else if( name.equals( "Incomings" )) {
                m_Incomings = "Y".equals( para[ i ].getParameter());
                log.fine("M_Incomings_prepare_QuarterProcess="+m_Incomings+para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }

            if( "Y".equals( para[ i ].getParameter())) {
                info.append( para[ i ].getParameterName()).append( " - " );
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
        return "@Calculated@=" + info.toString();
    }    // doIt
}    // ReportLineSet_Copy



/*
 *  @(#)Quarter_Process.java   02.07.07
 * 
 *  Fin del fichero Quarter_Process.java
 *  
 *  Versión 2.2
 *
 */
