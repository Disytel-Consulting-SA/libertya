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



package org.openXpertya.util;

import java.io.Serializable;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class NullParameter implements Serializable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param type
     */

    public NullParameter( int type ) {
        m_type = type;
    }    // NullParameter

    /** Descripción de Campos */

    private int m_type = -1;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getType() {
        return m_type;
    }    // getType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "NullParameter Type=" + m_type;
    }    // toString
}    // NullParameter



/*
 *  @(#)NullParameter.java   25.03.06
 * 
 *  Fin del fichero NullParameter.java
 *  
 *  Versión 2.2
 *
 */
