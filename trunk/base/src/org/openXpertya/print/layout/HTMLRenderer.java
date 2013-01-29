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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.openXpertya.util.CLogger;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class HTMLRenderer extends View {

    /**
     * Descripci�n de M�todo
     *
     *
     * @param html
     *
     * @return
     */

    public static HTMLRenderer get( String html ) {
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument  doc = ( HTMLDocument )kit.createDefaultDocument();

        try {
            doc.remove( 0,doc.getLength());

            Reader r = new StringReader( html );

            kit.read( r,doc,0 );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"HTMLRenderer",e );
        }

        // Create Renderer

        Element      element  = doc.getDefaultRootElement();
        ViewFactory  factory  = kit.getViewFactory();
        View         view     = factory.create( element );    // Y_AXIS is main
        HTMLRenderer renderer = new HTMLRenderer( factory,view );

        renderer.preferenceChanged( null,true,true );

        return renderer;
    }    // get

    /** Descripci�n de Campos */

    private static CLogger log = CLogger.getCLogger( HTMLRenderer.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param f
     * @param v
     */

    public HTMLRenderer( ViewFactory f,View v ) {
        super( null );
        m_factory = f;
        m_view    = v;
        m_view.setParent( this );

        // initially layout to the preferred size

        setSize( m_view.getPreferredSpan( X_AXIS ),m_view.getPreferredSpan( Y_AXIS ));
    }    // HTMLRenderer

    /** Descripci�n de Campos */

    private int m_width;

    /** Descripci�n de Campos */

    private View m_view;

    /** Descripci�n de Campos */

    private ViewFactory m_factory;

    /** Descripci�n de Campos */

    private Rectangle m_allocation;

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public float getWidth() {
        return getPreferredSpan( javax.swing.text.View.X_AXIS );
    }    // getWidth

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public float getHeight() {
        return getPreferredSpan( javax.swing.text.View.Y_AXIS );
    }    // getHeight

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public float getHeightOneLine() {
        return 30f;    // HARDCODED
    }                  // getHeightOneLine

    /**
     * Descripci�n de M�todo
     *
     *
     * @param width
     * @param height
     */

    public void setAllocation( int width,int height ) {
        setAllocation( new Rectangle( width,height ));
    }    // setAllocation

    /**
     * Descripci�n de M�todo
     *
     *
     * @param allocation
     */

    public void setAllocation( Rectangle allocation ) {
        m_allocation = allocation;
    }    // setAllocation

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public Rectangle getAllocation() {
        if( m_allocation == null ) {
            return new Rectangle(( int )getWidth(),( int )getHeight());
        }

        return m_allocation;
    }    // getAllocation

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public AttributeSet getAttributes() {
        return null;
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @param axis
     *
     * @return
     */

    public float getPreferredSpan( int axis ) {
        if( axis == X_AXIS ) {

            // width currently laid out to

            return m_width;
        }

        return m_view.getPreferredSpan( axis );
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @param axis
     *
     * @return
     */

    public float getMinimumSpan( int axis ) {
        return m_view.getMinimumSpan( axis );
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @param axis
     *
     * @return
     */

    public float getMaximumSpan( int axis ) {
        return Integer.MAX_VALUE;
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @param axis
     *
     * @return
     */

    public float getAlignment( int axis ) {
        return m_view.getAlignment( axis );
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @param g
     * @param allocation
     */

    public void paint( Graphics g,Shape allocation ) {
        Rectangle alloc = allocation.getBounds();

        m_view.setSize( alloc.width,alloc.height );    // layout

        Shape oldClip = g.getClip();

        g.setClip( alloc );    // limit print
        m_view.paint( g,allocation );
        g.setClip( oldClip );
    }                          // paint

    /**
     * Descripci�n de M�todo
     *
     *
     * @param parent
     */

    public void setParent( View parent ) {
        throw new Error( "Can't set parent on root view" );
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public int getViewCount() {
        return 1;
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @param n
     *
     * @return
     */

    public View getView( int n ) {
        return m_view;
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pos
     * @param a
     * @param b
     *
     * @return
     *
     * @throws BadLocationException
     */

    public Shape modelToView( int pos,Shape a,Position.Bias b ) throws BadLocationException {
        return m_view.modelToView( pos,a,b );
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @param p0
     * @param b0
     * @param p1
     * @param b1
     * @param a
     *
     * @return
     *
     * @throws BadLocationException
     */

    public Shape modelToView( int p0,Position.Bias b0,int p1,Position.Bias b1,Shape a ) throws BadLocationException {
        return m_view.modelToView( p0,b0,p1,b1,a );
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @param x
     * @param y
     * @param a
     * @param bias
     *
     * @return
     */

    public int viewToModel( float x,float y,Shape a,Position.Bias[] bias ) {
        return m_view.viewToModel( x,y,a,bias );
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public Document getDocument() {
        return m_view.getDocument();
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public int getStartOffset() {
        return m_view.getStartOffset();
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public int getEndOffset() {
        return m_view.getEndOffset();
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public Element getElement() {
        return m_view.getElement();
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @param width
     * @param height
     */

    public void setSize( float width,float height ) {
        this.m_width = ( int )width;
        m_view.setSize( width,height );
    }

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public ViewFactory getViewFactory() {
        return m_factory;
    }
}    // HTMLRenderer



/*
 *  @(#)HTMLRenderer.java   12.10.07
 * 
 *  Fin del fichero HTMLRenderer.java
 *  
 *  Versión 2.2
 *
 */
