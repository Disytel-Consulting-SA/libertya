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



package org.openXpertya.apps.wf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.wf.MWFNode;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WFNode extends JComponent {

    /**
     * Constructor de la clase ...
     *
     *
     * @param node
     */

    public WFNode( MWFNode node ) {
        super();
        setOpaque( true );
        m_node = node;
        setName( m_node.getName());
        m_icon = new WFIcon( node.getAction());
        m_name = m_node.getName( true );
        setBorder( s_border );

        // Tool Tip

        String description = node.getDescription( true );

        if( (description != null) && (description.length() > 0) ) {
            setToolTipText( description );
        } else {
            setToolTipText( node.getName( true ));
        }

        // Location

        setBounds( node.getXPosition(),node.getYPosition(),s_size.width,s_size.height );
        log.config( node.getAD_WF_Node_ID() + "," + node.getName() + " - " + getLocation());
    }    // WFNode

    /** Descripción de Campos */

    public static String PROPERTY_SELECTED = "selected";

    /** Descripción de Campos */

    private static Border s_border = BorderFactory.createBevelBorder( BevelBorder.RAISED );

    /** Descripción de Campos */

    private static Border s_borderSelected = BorderFactory.createBevelBorder( BevelBorder.LOWERED );

    /** Descripción de Campos */

    private static Dimension s_size = new Dimension( 100,50 );

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( WFNode.class );

    /** Descripción de Campos */

    private MWFNode m_node = null;

    /** Descripción de Campos */

    private WFIcon m_icon = null;

    /** Descripción de Campos */

    private String m_name = null;

    /** Descripción de Campos */

    private boolean m_selected = false;

    /** Descripción de Campos */

    private boolean m_moved = false;

    /**
     * Descripción de Método
     *
     *
     * @param selected
     */

    public void setSelected( boolean selected ) {
        firePropertyChange( PROPERTY_SELECTED,m_selected,selected );
        m_selected = selected;

        if( selected ) {
            setBorder( s_borderSelected );
            setForeground( Color.blue );
        } else {
            setBorder( s_border );
            setForeground( Color.black );
        }
    }    // setSelected

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSelected() {
        return m_selected;
    }    // isSelected

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Client_ID() {
        return m_node.getAD_Client_ID();
    }    // getAD_Client_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEditable() {
        return getAD_Client_ID() == Env.getAD_Client_ID( Env.getCtx());
    }    // isEditable

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_WF_Node_ID() {
        return m_node.getAD_WF_Node_ID();
    }    // getAD_WF_Node_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MWFNode getModel() {
        return m_node;
    }    // getModel

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     */

    public void setLocation( int x,int y ) {
        super.setLocation( x,y );
        m_node.setPosition( x,y );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "WFNode[" );

        sb.append( getAD_WF_Node_ID()).append( "-" ).append( m_name ).append( "," ).append( getBounds()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getPreferredSize() {
        return s_size;
    }    // getPreferredSize

    /**
     * Descripción de Método
     *
     *
     * @param g
     */

    protected void paintComponent( Graphics g ) {
        Graphics2D g2D    = ( Graphics2D )g;
        Rectangle  bounds = getBounds();

        m_icon.paintIcon( this,g2D,0,0 );

        //

        Color color = getForeground();

        g2D.setPaint( color );

        Font font = new Font( null );

        //

        AttributedString aString = new AttributedString( m_name );

        aString.addAttribute( TextAttribute.FONT,font );
        aString.addAttribute( TextAttribute.FOREGROUND,color );

        AttributedCharacterIterator iter = aString.getIterator();

        //

        LineBreakMeasurer measurer = new LineBreakMeasurer( iter,g2D.getFontRenderContext());
        float      width  = s_size.width - m_icon.getIconWidth() - 2;
        TextLayout layout = measurer.nextLayout( width );
        float      xPos   = m_icon.getIconWidth();
        float      yPos   = layout.getAscent() + 2;

        //

        layout.draw( g2D,xPos,yPos );
        width = s_size.width - 4;    // 2 pt

        while( measurer.getPosition() < iter.getEndIndex()) {
            layout = measurer.nextLayout( width );
            yPos   += layout.getAscent() + layout.getDescent() + layout.getLeading();
            layout.draw( g2D,2,yPos );
        }
    }    // paintComponent
}    // WFNode



/*
 *  @(#)WFNode.java   02.07.07
 * 
 *  Fin del fichero WFNode.java
 *  
 *  Versión 2.2
 *
 */
