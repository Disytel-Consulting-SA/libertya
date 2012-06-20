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

import java.math.BigDecimal;

import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPrintFormatProcess extends SvrProcess {

    /** Descripción de Campos */

    private BigDecimal m_AD_PrintFormat_ID;

    /** Descripción de Campos */

    private BigDecimal m_AD_Table_ID;

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
            } else if( name.equals( "AD_PrintFormat_ID" )) {
                m_AD_PrintFormat_ID = (( BigDecimal )para[ i ].getParameter());
            } else if( name.equals( "AD_Table_ID" )) {
                m_AD_Table_ID = (( BigDecimal )para[ i ].getParameter());
            } else {
                log.equals( "prepare - Unknown Parameter=" + para[ i ].getParameterName());
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
        if( (m_AD_Table_ID != null) && (m_AD_Table_ID.intValue() > 0) ) {
            log.info( "Create from AD_Table_ID=" + m_AD_Table_ID );

            MPrintFormat pf = MPrintFormat.createFromTable( getCtx(),m_AD_Table_ID.intValue(),getRecord_ID());

            addLog( m_AD_Table_ID.intValue(),null,new BigDecimal( pf.getItemCount()),pf.getName());

            return pf.getName() + " #" + pf.getItemCount();
        } else if( (m_AD_PrintFormat_ID != null) && (m_AD_PrintFormat_ID.intValue() > 0) ) {
            log.info( "MPrintFormatProcess - Copy from AD_PrintFormat_ID=" + m_AD_PrintFormat_ID );

            MPrintFormat pf = MPrintFormat.copy( getCtx(),m_AD_PrintFormat_ID.intValue(),getRecord_ID());

            addLog( m_AD_PrintFormat_ID.intValue(),null,new BigDecimal( pf.getItemCount()),pf.getName());

            return pf.getName() + " #" + pf.getItemCount();
        } else {
            throw new Exception( MSG_InvalidArguments );
        }
    }    // doIt
}    // MPrintFormatProcess



/*
 *  @(#)MPrintFormatProcess.java   23.03.06
 * 
 *  Fin del fichero MPrintFormatProcess.java
 *  
 *  Versión 2.2
 *
 */
