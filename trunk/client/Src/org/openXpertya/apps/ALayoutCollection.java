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

import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

class ALayoutCollection extends HashMap {

    /**
     * Constructor de la clase ...
     *
     */

    public ALayoutCollection() {
        super();
    }    // ALayoutCollection

    /**
     * Descripción de Método
     *
     *
     * @param constraint
     * @param component
     *
     * @return
     */

    public Object put( Object constraint,Object component ) {
        if( !( component instanceof Component )) {
            throw new IllegalArgumentException( "ALayoutCollection can only add Component values" );
        }

        if( (constraint != null) &&!containsKey( constraint ) && (constraint instanceof ALayoutConstraint) ) {

            // Log.trace(this,Log.l6_Database, "ALayoutCollection.put", constraint.toString());

            return super.put( constraint,component );
        }

        // We need to create constraint

        if( super.size() == 0 ) {

            // Log.trace(this,Log.l6_Database, "ALayoutCollection.put - first");

            return super.put( new ALayoutConstraint( 0,0 ),component );
        }

        // Add to end of list

        int row = getMaxRow();

        if( row == -1 ) {
            row = 0;
        }

        int               col  = getMaxCol( row ) + 1;
        ALayoutConstraint next = new ALayoutConstraint( row,col );

        // Log.trace(this,Log.l6_Database, "ALayoutCollection.put - addEnd", next.toString());

        return super.put( next,component );
    }    // put

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getMaxRow() {
        int maxRow = -1;

        //

        Iterator i = keySet().iterator();

        while( i.hasNext()) {
            ALayoutConstraint c = ( ALayoutConstraint )i.next();

            maxRow = Math.max( maxRow,c.getRow());
        }

        return maxRow;
    }    // getMaxRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getMaxCol() {
        int maxCol = -1;

        //

        Iterator i = keySet().iterator();

        while( i.hasNext()) {
            ALayoutConstraint c = ( ALayoutConstraint )i.next();

            maxCol = Math.max( maxCol,c.getCol());
        }

        return maxCol;
    }    // getMaxCol

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public int getMaxCol( int row ) {
        int maxCol = -1;

        //

        Iterator i = keySet().iterator();

        while( i.hasNext()) {
            ALayoutConstraint c = ( ALayoutConstraint )i.next();

            if( c.getRow() == row ) {
                maxCol = Math.max( maxCol,c.getCol());
            }
        }

        return maxCol;
    }    // getMaxCol
}    // ALayoutCollection



/*
 *  @(#)ALayoutCollection.java   02.07.07
 * 
 *  Fin del fichero ALayoutCollection.java
 *  
 *  Versión 2.2
 *
 */
