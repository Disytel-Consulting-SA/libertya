/*
 * @(#)NamePair.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.util;

import java.io.Serializable;

import java.util.Comparator;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public abstract class NamePair implements Comparator, Serializable, Comparable {

    /** Descripción de Campos */
    private String	m_name;

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     */
    protected NamePair(String name) {

        m_name	= name;

        if (m_name == null) {
            m_name	= "";
        }

    }		// NamePair

    /**
     * Descripción de Método
     *
     *
     * @param o1
     * @param o2
     *
     * @return
     */
    public int compare(Object o1, Object o2) {

        String	s1	= (o1 == null)
                          ?""
                          :o1.toString();
        String	s2	= (o2 == null)
                          ?""
                          :o2.toString();

        return s1.compareTo(s2);	// sort order ??

    }					// compare

    /**
     * Descripción de Método
     *
     *
     * @param o
     *
     * @return
     */
    public int compareTo(Object o) {
        return compare(this, o);
    }		// compareTo

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String toString() {
        return m_name;
    }		// toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String toStringX() {

        StringBuffer	sb	= new StringBuffer(getID());

        sb.append("=").append(m_name);

        return sb.toString();

    }		// toStringX

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public abstract String getID();

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getName() {
        return m_name;
    }		// getName
}	// NamePair



/*
 * @(#)NamePair.java   02.jul 2007
 * 
 *  Fin del fichero NamePair.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
