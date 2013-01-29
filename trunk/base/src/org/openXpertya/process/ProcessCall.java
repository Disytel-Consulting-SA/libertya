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

import java.util.Properties;

import org.openXpertya.util.Trx;

/**
 * Descripción de Interface
 *
 *
 * @version 2.2, 25.03.06
 * @author         Equipo de Desarrollo de openXpertya    
 */

public interface ProcessCall {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param pi
     * @param trx
     *
     * @return
     */

    public boolean startProcess( Properties ctx,ProcessInfo pi,Trx trx );
}    // ProcessCall



/*
 *  @(#)ProcessCall.java   25.03.06
 * 
 *  Fin del fichero ProcessCall.java
 *  
 *  Versión 2.2
 *
 */
