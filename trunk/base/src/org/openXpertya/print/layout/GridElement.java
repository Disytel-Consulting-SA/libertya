/*
 *    El contenido de este fichero est� sujeto a la  Licencia P�blica openXpertya versi�n 1.1 (LPO)
 * en tanto en cuanto forme parte �ntegra del total del producto denominado:  openXpertya, soluci�n 
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *    Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *    Partes del c�digo son CopyRight (c) 2002-2007 de Ingenier�a Inform�tica Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultor�a y  Soporte en  Redes y  Tecnolog�as  de  la
 * Informaci�n S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de c�digo original de  terceros, recogidos en el  ADDENDUM  A, secci�n 3 (A.3) de dicha
 * licencia  LPO,  y si dicho c�digo es extraido como parte del total del producto, estar� sujeto a
 * su respectiva licencia original.  
 *     M�s informaci�n en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.print.layout;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Properties;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class GridElement extends PrintElement {

    /**
     * Constructor de la clase ...
     *
     *
     * @param rows
     * @param cols
     */

    public GridElement( int rows,int cols ) {
        m_rows       = rows;
        m_cols       = cols;
        m_textLayout = new TextLayout[ rows ][ cols ];
        m_iterator   = new AttributedCharacterIterator[ rows ][ cols ];
        m_rowHeight  = new int[ rows ];
        m_colWidth   = new int[ cols ];

        // explicit init

        for( int r = 0;r < m_rows;r++ ) {
            m_rowHeight[ r ] = 0;

            for( int c = 0;c < m_cols;c++ ) {
                m_textLayout[ r ][ c ] = null;
                m_iterator[ r ][ c ]   = null;
            }
        }

        for( int c = 0;c < m_cols;c++ ) {
            m_colWidth[ c ] = 0;
        }
    }    // GridElement

    /** Descripci�n de Campos */

    private int m_rowGap = 3;

    /** Descripci�n de Campos */

    private int m_colGap = 5;

    /** Descripci�n de Campos */

    private int m_rows;

    /** Descripci�n de Campos */

    private int m_cols;

    /** Descripci�n de Campos */

    private TextLayout[][] m_textLayout = null;

    /** Descripci�n de Campos */

    private AttributedCharacterIterator[][] m_iterator = null;

    /** Descripci�n de Campos */

    private int[] m_rowHeight = null;

    /** Descripci�n de Campos */

    private int[] m_colWidth = null;

    /** Descripci�n de Campos */

    private FontRenderContext m_frc = new FontRenderContext( null,true,true );

    /**
     * Descripci�n de M�todo
     *
     *
     * @param row
     * @param col
     * @param stringData
     * @param font
     * @param foreground
     */

    public void setData( int row,int col,String stringData,Font font,Paint foreground ) {
        if( (stringData == null) || (stringData.length() == 0) ) {
            return;
        }

        //
        // log.fine("setData - " + row + "/" + col + " - " + stringData);

        AttributedString aString = new AttributedString( stringData );

        aString.addAttribute( TextAttribute.FONT,font );
        aString.addAttribute( TextAttribute.FOREGROUND,foreground );

        AttributedCharacterIterator iter   = aString.getIterator();
        TextLayout                  layout = new TextLayout( iter,m_frc );

        setData( row,col,layout,iter );
    }    // setData

    /**
     * Descripci�n de M�todo
     *
     *
     * @param row
     * @param col
     * @param layout
     * @param iter
     */

    private void setData( int row,int col,TextLayout layout,AttributedCharacterIterator iter ) {
        if( layout == null ) {
            return;
        }

        if( p_sizeCalculated ) {
            throw new IllegalStateException( "Size already calculated" );
        }

        if( (row < 0) || (row >= m_rows) ) {
            throw new ArrayIndexOutOfBoundsException( "Row Index=" + row + " Rows=" + m_rows );
        }

        if( (col < 0) || (col >= m_cols) ) {
            throw new ArrayIndexOutOfBoundsException( "Column Index=" + col + " Cols=" + m_cols );
        }

        //

        m_textLayout[ row ][ col ] = layout;
        m_iterator[ row ][ col ]   = iter;

        // Set Size

        int height = ( int )( layout.getAscent() + layout.getDescent() + layout.getLeading()) + 1;
        int width = ( int )layout.getAdvance() + 1;

        if( m_rowHeight[ row ] < height ) {
            m_rowHeight[ row ] = height;
        }

        if( m_colWidth[ col ] < width ) {
            m_colWidth[ col ] = width;
        }
    }    // setData

    /**
     * Descripci�n de M�todo
     *
     *
     * @param rowGap
     * @param colGap
     */

    public void setGap( int rowGap,int colGap ) {
        m_rowGap = rowGap;
        m_colGap = colGap;
    }    // setGap

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    protected boolean calculateSize() {
        p_height = 0;

        for( int r = 0;r < m_rows;r++ ) {
            p_height += m_rowHeight[ r ];

            if( m_rowHeight[ r ] > 0 ) {
                p_height += m_rowGap;
            }
        }

        p_height -= m_rowGap;    // remove last
        p_width  = 0;

        for( int c = 0;c < m_cols;c++ ) {
            p_width += m_colWidth[ c ];

            if( m_colWidth[ c ] > 0 ) {
                p_width += m_colGap;
            }
        }

        p_width -= m_colGap;    // remove last

        return true;
    }    // calculateSize

    /**
     * Descripci�n de M�todo
     *
     *
     * @param g2D
     * @param pageNo
     * @param pageStart
     * @param ctx
     * @param isView
     */

    public void paint( Graphics2D g2D,int pageNo,Point2D pageStart,Properties ctx,boolean isView ) {
        Point2D.Double location = getAbsoluteLocation( pageStart );
        float          y        = ( float )location.y;

        //

        for( int row = 0;row < m_rows;row++ ) {
            float x = ( float )location.x;

            for( int col = 0;col < m_cols;col++ ) {
                if( m_textLayout[ row ][ col ] != null ) {
                    float yy = y + m_textLayout[ row ][ col ].getAscent();

                    // if (m_iterator[row][col] != null)
                    // g2D.drawString(m_iterator[row][col], x, yy);
                    // else

                    m_textLayout[ row ][ col ].draw( g2D,x,yy );
                }

                x += m_colWidth[ col ];

                if( m_colWidth[ col ] > 0 ) {
                    x += m_colGap;
                }
            }

            y += m_rowHeight[ row ];

            if( m_rowHeight[ row ] > 0 ) {
                y += m_rowGap;
            }
        }
    }    // paint
}    // GridElement



/*
 *  @(#)GridElement.java   12.10.07
 * 
 *  Fin del fichero GridElement.java
 *  
 *  Versión 2.2
 *
 */
