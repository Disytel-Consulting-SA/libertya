/*
 * @(#)MSort.java   12.oct 2007  Versión 2.2
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

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.util.Comparator;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public final class MSort implements Comparator, Serializable {

    /** Descripción de Campos */
    private int	m_multiplier	= 1;	// Asc by default

    /** Descripción de Campos */
    public Object	data;

    /** Descripción de Campos */
    public int	index;

    /**
     * Constructor de la clase ...
     *
     *
     * @param new_index
     * @param new_data
     */
    public MSort(int new_index, Object new_data) {

        index	= new_index;
        data	= new_data;

    }		// MSort

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

        // Get Objects to compare
        Object	cmp1	= null;

        if (o1 instanceof MSort) {
            cmp1	= ((MSort) o1).data;
        }

        if (cmp1 instanceof NamePair) {
            cmp1	= ((NamePair) cmp1).getName();
        }

        Object	cmp2	= o2;

        if (o2 instanceof MSort) {
            cmp2	= ((MSort) o2).data;
        }

        if (cmp2 instanceof NamePair) {
            cmp2	= ((NamePair) cmp2).getName();
        }

        // Comparing Null values
        if (cmp1 == null) {
            cmp1	= new String("");
        }

        if (cmp2 == null) {
            cmp2	= new String("");
        }

        // String
        /*
         * Disytel - Matias Cap
         * Se realizó modificación a las condiciones del if, se encontraban de la siguiente manera
         * if ((cmp1 instanceof String) && (cmp1 instanceof String)) siempre comparaba por cmp1
         */
        if ((cmp1 instanceof String) && (cmp2 instanceof String)) {

            String	s	= (String) cmp1;

            return s.compareTo((String) cmp2) * m_multiplier;
        }

        // Date
        else if ((cmp1 instanceof Timestamp) && (cmp2 instanceof Timestamp)) {

            Timestamp	t	= (Timestamp) cmp1;

            return t.compareTo((Timestamp) cmp2) * m_multiplier;
        }

        // BigDecimal
        else if ((cmp1 instanceof BigDecimal) && (cmp2 instanceof BigDecimal)) {

            BigDecimal	d	= (BigDecimal) cmp1;

            return d.compareTo((BigDecimal) cmp2) * m_multiplier;
        }

        // Integer
        else if ((cmp1 instanceof Integer) && (cmp2 instanceof Integer)) {

            Integer	d	= (Integer) cmp1;

            return d.compareTo((Integer) cmp2) * m_multiplier;
        }

        // String value
        String	s	= cmp1.toString();

        return s.compareTo(cmp2.toString()) * m_multiplier;
    }		// compare

    /**
     * Descripción de Método
     *
     *
     * @param obj
     *
     * @return
     */
    public boolean equals(Object obj) {

        if (obj instanceof MSort) {

            MSort	ms	= (MSort) obj;

            if (data == ms.data) {
                return true;
            }
        }

        return false;

    }		// equals

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MSort[");

        sb.append("Index=").append(index).append(",Data=").append(data);
        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param ascending
     */
    public void setSortAsc(boolean ascending) {

        if (ascending) {
            m_multiplier	= 1;
        } else {
            m_multiplier	= -1;
        }

    }		// setSortAsc
}	// MSort



/*
 * @(#)MSort.java   02.jul 2007
 * 
 *  Fin del fichero MSort.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
