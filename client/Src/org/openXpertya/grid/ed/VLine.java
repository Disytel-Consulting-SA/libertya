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



package org.openXpertya.grid.ed;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.border.AbstractBorder;

import org.compiere.plaf.CompierePLAF;
import org.openXpertya.util.Language;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VLine extends AbstractBorder {

    /**
     * Constructor de la clase ...
     *
     */

    public VLine() {
        this( "" );
    }    // VLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param header
     */

    public VLine( String header ) {
        super();
        setHeader( header );
    }    // VLine

    /** Descripción de Campos */

    private String m_header = "";

    /** Descripción de Campos */

    private Font m_font = CompierePLAF.getFont_Label();

    /** Descripción de Campos */

    private Color m_color = CompierePLAF.getTextColor_Label();

    /** Descripción de Campos */

    public final static int GAP = 5;

    /** Descripción de Campos */

    public final static int SPACE = 4;    // used in VPanel

    /**
     * Descripción de Método
     *
     *
     * @param c
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     */

    public void paintBorder( Component c,Graphics g,int x,int y,int w,int h ) {
        Graphics copy = g.create();

        if( copy != null ) {
            try {
                copy.translate( x,y );
                paintLine( c,copy,w,h );
            } finally {
                copy.dispose();
            }
        }
    }    // paintBorder

    /**
     * Descripción de Método
     *
     *
     * @param c
     * @param g
     * @param w
     * @param h
     */

    private void paintLine( Component c,Graphics g,int w,int h ) {
        int y = h - SPACE;

        // Line

        g.setColor( Color.darkGray );
        g.drawLine( GAP,y,w - GAP,y );
        g.setColor( Color.white );
        g.drawLine( GAP,y + 1,w - GAP,y + 1 );    // last part of line

        if( (m_header == null) || (m_header.length() == 0) ) {
            return;
        }

        // Header Text

        g.setColor( m_color );
        g.setFont( m_font );

        int x = GAP;

        if( !Language.getLoginLanguage().isLeftToRight()) {}

        g.drawString( m_header,GAP,h - SPACE - 1 );
    }    // paintLine

    /**
     * Descripción de Método
     *
     *
     * @param newHeader
     */

    public void setHeader( String newHeader ) {
        m_header = newHeader.replace( '_',' ' );
    }    // setHeader

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getHeader() {
        return m_header;
    }    // getHeader
}    // VLine



/*
 *  @(#)VLine.java   02.07.07
 * 
 *  Fin del fichero VLine.java
 *  
 *  Versión 2.2
 *
 */
