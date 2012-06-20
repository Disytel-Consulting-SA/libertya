/*
 * @(#)ValueNamePair.java   12.oct 2007  Versión 2.2
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

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public final class ValueNamePair extends NamePair {

    /** Descripción de Campos */
    private String	m_value	= null;

    /**
     * Constructor de la clase ...
     *
     *
     * @param value
     * @param name
     */
    public ValueNamePair(String value, String name) {

        super(name);
        m_value	= value;

        if (m_value == null) {
            m_value	= "";
        }

    }		// ValueNamePair

    /**
     * Descripción de Método
     *
     *
     * @param obj
     *
     * @return
     */
    public boolean equals(Object obj) {

        if (obj instanceof ValueNamePair) {

            ValueNamePair	pp	= (ValueNamePair) obj;

            if ((pp.getName() != null) && (pp.getValue() != null) && pp.getName().equals(getName()) && pp.getValue().equals(m_value)) {
                return true;
            }

            return false;
        }

        return false;

    }		// equals

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int hashCode() {
        return m_value.hashCode();
    }		// hashCode

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getID() {
        return m_value;
    }		// getID

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getValue() {
        return m_value;
    }		// getValue
}	// KeyValuePair



/*
 * @(#)ValueNamePair.java   02.jul 2007
 * 
 *  Fin del fichero ValueNamePair.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
