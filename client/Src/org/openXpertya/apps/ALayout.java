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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ALayout implements LayoutManager2 {

    /**
     * Constructor de la clase ...
     *
     */

    public ALayout() {
        this( 2,4,true );
    }    // ALayout

    /**
     * Constructor de la clase ...
     *
     *
     * @param spaceH
     * @param spaceV
     * @param colFill
     */

    public ALayout( int spaceH,int spaceV,boolean colFill ) {
        setSpaceH( spaceH );
        setSpaceV( spaceV );
        m_colFill = colFill;
    }    // ALayout

    /** Descripción de Campos */

    private ALayoutCollection m_data = new ALayoutCollection();

    /** Descripción de Campos */

    private int m_spaceH;

    /** Descripción de Campos */

    private int m_spaceV;

    /** Descripción de Campos */

    private boolean m_colFill;

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param comp
     */

    public void addLayoutComponent( String name,Component comp ) {
        addLayoutComponent( comp,null );
    }    // addLayoutComponent

    /**
     * Descripción de Método
     *
     *
     * @param component
     * @param constraint
     */

    public void addLayoutComponent( Component component,Object constraint ) {
        ALayoutConstraint con = null;

        if( constraint instanceof ALayoutConstraint ) {
            con = ( ALayoutConstraint )constraint;
        }

        //

        m_data.put( con,component );
    }    // addLayoutComponent

    /**
     * Descripción de Método
     *
     *
     * @param comp
     */

    public void removeLayoutComponent( Component comp ) {
        if( !m_data.containsValue( comp )) {
            return;
        }

        Iterator it = m_data.keySet().iterator();

        while( it.hasNext()) {
            Object key = it.next();

            if( m_data.get( key ).equals( comp )) {
                m_data.remove( key );

                return;
            }
        }
    }    // removeLayoutComponent

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */

    public Dimension preferredLayoutSize( Container parent ) {
        return calculateLayoutSize( parent,'P' );
    }    // preferredLayoutSize

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */

    public Dimension minimumLayoutSize( Container parent ) {
        return calculateLayoutSize( parent,'m' );
    }    // minimumLayoutSize

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */

    public Dimension maximumLayoutSize( Container parent ) {
        return calculateLayoutSize( parent,'M' );
    }    // maximumLayoutSize

    /**
     * Descripción de Método
     *
     *
     * @param parent
     * @param how
     *
     * @return
     */

    private Dimension calculateLayoutSize( Container parent,char how ) {
        checkComponents( parent );

        // --  Create 2D Dimension Array

        int           rows = getRowCount();
        int           cols = getColCount();
        Dimension[][] dim  = new Dimension[ rows ][ cols ];

        //

        Object[] keys = m_data.keySet().toArray();

        Arrays.sort( keys );

        for( int i = 0;i < keys.length;i++ ) {
            ALayoutConstraint constraint = ( ALayoutConstraint )keys[ i ];
            Component         component  = ( Component )m_data.get( keys[ i ] );
            Dimension         d          = null;

            if( how == 'P' ) {
                d = component.getPreferredSize();
            } else if( how == 'M' ) {
                d = component.getMaximumSize();
            } else {
                d = component.getMinimumSize();
            }

            if( component.isVisible()) {
                dim[ constraint.getRow()][ constraint.getCol()] = d;
            } else {
                dim[ constraint.getRow()][ constraint.getCol()] = null;
            }
        }

        // --  Calculate 2D Dimension Size

        Insets    insets   = parent.getInsets();
        Dimension retValue = new Dimension( insets.left + insets.right,insets.top + insets.bottom );

        retValue.height += m_spaceH;
        retValue.width  += m_spaceV;

        int maxWidth = 0;

        for( int r = 0;r < rows;r++ ) {
            int height = 0;
            int width  = 0;

            for( int c = 0;c < cols;c++ ) {
                Dimension d = dim[ r ][ c ];

                if( d != null ) {
                    width  += d.width;
                    height = Math.max( height,d.height );
                }

                width += m_spaceV;
            }    // for all columns

            retValue.height += height + m_spaceH;
            maxWidth        += Math.max( maxWidth,width );
        }        // for all rows

        retValue.width += maxWidth;

        // Log.trace(this,Log.l6_Database, "ALayout.calculateLayoutSize", retValue.toString());

        return retValue;
    }    // calculateLayoutSize

    /**
     * Descripción de Método
     *
     *
     * @param parent
     */

    public void layoutContainer( Container parent ) {
        checkComponents( parent );

        // --  Create 2D Component Array

        int           rows = getRowCount();
        int           cols = getColCount();
        Component[][] com  = new Component[ rows ][ cols ];

        //

        Object[] keys = m_data.keySet().toArray();

        Arrays.sort( keys );

        for( int i = 0;i < keys.length;i++ ) {
            ALayoutConstraint constraint = ( ALayoutConstraint )keys[ i ];
            Component         component  = ( Component )m_data.get( keys[ i ] );

            if( component.isVisible()) {
                com[ constraint.getRow()][ constraint.getCol()] = component;
            } else {
                com[ constraint.getRow()][ constraint.getCol()] = null;
            }
        }

        // --  Calculate Column Size

        int[] colWidth    = new int[ cols ];
        int[] rowHeight   = new int[ rows ];
        int   columnWidth = m_spaceV;

        for( int c = 0;c < cols;c++ ) {
            int width = 0;

            for( int r = 0;r < rows;r++ ) {
                Component component = com[ r ][ c ];

                if( component != null ) {
                    width = Math.max( width,component.getPreferredSize().width );
                    rowHeight[ r ] = Math.max( rowHeight[ r ],component.getPreferredSize().height );
                }
            }

            colWidth[ c ] = width;
            columnWidth   += width + m_spaceV;
        }

        // --  Stretch/Squeeze Columns to fit target width

        int    parentWidth = parent.getSize().width;
        double multiplier  = ( double )parentWidth / ( double )columnWidth;

        if( multiplier < .5 ) {    // limit sqeezing
            multiplier = .5;
        }

        for( int c = 0;c < cols;c++ ) {
            colWidth[ c ] = ( int )( colWidth[ c ] * multiplier );
        }

        int spaceV = ( int )( m_spaceV * multiplier );

        //
//              log.fine( "ALayout.layoutContainer",
//                      "ParentWidth=" + parentWidth + ", ColumnWidth=" + columnWidth + ", SpaceV=" + spaceV + ",  Multiplier=" + multiplier);

        // --  Lay out components

        Insets insets = parent.getInsets();
        int    posH   = insets.top + m_spaceH;

        for( int r = 0;r < rows;r++ ) {
            int posV   = insets.left + spaceV;
            int height = 0;

            for( int c = 0;c < cols;c++ ) {
                Component component = com[ r ][ c ];

                if( component != null ) {
                    Dimension ps = component.getPreferredSize();
                    int       w  = ps.width;

                    if( m_colFill || (w > colWidth[ c ]) ) {    // limit or stretch
                        w = colWidth[ c ];
                    }

                    int h        = ps.height;
                    int topSpace = 0;

                    if( h < rowHeight[ r ] ) {                  // push a little bit lower
                        topSpace = ( rowHeight[ r ] - h ) / 3;
                    }

                    height = Math.max( height,h );
                    component.setBounds( posV,posH + topSpace,w,h );

//                                      log.fine( "ALayout.layoutContainer",
//                                              "Row=" + r + ", Col=" + c + ",  PosV=" + posV + ", PosH=" + posH + "/" + topSpace + ",  width=" + w + ", height=" + h);

                }

                posV += colWidth[ c ] + spaceV;
            }    // for all columns

            posH += height + m_spaceH;
        }        // for all rows
    }            // layoutContainer

    /**
     * Descripción de Método
     *
     *
     * @param target
     *
     * @return
     */

    public float getLayoutAlignmentX( Container target ) {
        return 0f;
    }    // getLayoutAlignmentX

    /**
     * Descripción de Método
     *
     *
     * @param target
     *
     * @return
     */

    public float getLayoutAlignmentY( Container target ) {
        return 0f;
    }    // getLayoutAlignmentY

    /**
     * Descripción de Método
     *
     *
     * @param target
     */

    public void invalidateLayout( Container target ) {}    // invalidateLayout

    /**
     * Descripción de Método
     *
     *
     * @param target
     */

    private void checkComponents( Container target ) {
        int size = target.getComponentCount();

        for( int i = 0;i < size;i++ ) {
            Component comp = target.getComponent( i );

            if( !m_data.containsValue( comp )) {
                m_data.put( null,comp );
            }
        }
    }    // checkComponents

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRowCount() {
        return m_data.getMaxRow() + 1;
    }    // getRowCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getColCount() {
        return m_data.getMaxCol() + 1;
    }    // getColCount

    /**
     * Descripción de Método
     *
     *
     * @param spaceH
     */

    public void setSpaceH( int spaceH ) {
        m_spaceH = spaceH;
    }    // setSpaceH

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSpaceH() {
        return m_spaceH;
    }    // getSpaceH

    /**
     * Descripción de Método
     *
     *
     * @param spaceV
     */

    public void setSpaceV( int spaceV ) {
        m_spaceV = spaceV;
    }    // setSpaceV

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSpaceV() {
        return m_spaceV;
    }    // getSpaceV
}    // ALayout



/*
 *  @(#)ALayout.java   02.07.07
 * 
 *  Fin del fichero ALayout.java
 *  
 *  Versión 2.2
 *
 */
