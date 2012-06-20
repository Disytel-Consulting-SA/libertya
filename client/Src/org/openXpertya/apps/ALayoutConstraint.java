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



package org.openXpertya.apps;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ALayoutConstraint implements Comparable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param row
     * @param col
     */

    public ALayoutConstraint( int row,int col ) {
        m_row = row;
        m_col = col;
    }    // ALayoutConstraint

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ALayoutConstraint createNext() {
        return new ALayoutConstraint( m_row,m_col + 1 );
    }    // createNext

    /** Descripción de Campos */

    private int m_row;

    /** Descripción de Campos */

    private int m_col;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRow() {
        return m_row;
    }    // getRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getCol() {
        return m_col;
    }    // getCol

    /**
     * Descripción de Método
     *
     *
     * @param o
     *
     * @return
     */

    public int compareTo( Object o ) {
        ALayoutConstraint comp = null;

        if( o instanceof ALayoutConstraint ) {
            comp = ( ALayoutConstraint )o;
        }

        if( comp == null ) {
            return +111;
        }

        // Row compare

        int rowComp = m_row - comp.getRow();

        if( rowComp != 0 ) {
            return rowComp;
        }

        // Column compare

        return m_col - comp.getCol();
    }    // compareTo

    /**
     * Descripción de Método
     *
     *
     * @param o
     *
     * @return
     */

    public boolean equals( Object o ) {
        if( o instanceof ALayoutConstraint ) {
            return compareTo( o ) == 0;
        }

        return false;
    }    // equal

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "ALayoutConstraint [Row=" + m_row + ", Col=" + m_col + "]";
    }    // toString
}    // ALayoutConstraint



/*
 *  @(#)ALayoutConstraint.java   02.07.07
 * 
 *  Fin del fichero ALayoutConstraint.java
 *  
 *  Versión 2.2
 *
 */
