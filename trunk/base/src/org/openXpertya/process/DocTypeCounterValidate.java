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

import org.openXpertya.model.MDocTypeCounter;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DocTypeCounterValidate extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_DocTypeCounter_ID = 0;

    /** Descripción de Campos */

    private MDocTypeCounter m_counter = null;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        p_C_DocTypeCounter_ID = getRecord_ID();
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
        log.info( "C_DocTypeCounter_ID=" + p_C_DocTypeCounter_ID );
        m_counter = new MDocTypeCounter( getCtx(),p_C_DocTypeCounter_ID,get_TrxName());

        if( (m_counter == null) || (m_counter.getID() == 0) ) {
            throw new IllegalArgumentException( "Not found C_DocTypeCounter_ID=" + p_C_DocTypeCounter_ID );
        }

        //

        String error = m_counter.validate();

        m_counter.save();

        if( error != null ) {
            throw new Exception( error );
        }

        return "OK";
    }    // doIt
}    // DocTypeCounterValidate



/*
 *  @(#)DocTypeCounterValidate.java   02.07.07
 * 
 *  Fin del fichero DocTypeCounterValidate.java
 *  
 *  Versión 2.2
 *
 */
