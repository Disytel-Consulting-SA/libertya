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



package org.openXpertya.print;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Properties;

import org.openXpertya.model.MQuery;
import org.openXpertya.print.layout.PrintElement;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class HeaderFooterDesign {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     */

    public HeaderFooterDesign( Properties ctx ) {
        m_ctx = ctx;
    }    // HeaderFooter

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private ArrayList m_elements = new ArrayList();

    /** Descripción de Campos */

    private PrintElement[] m_pe = null;

    /**
     * Descripción de Método
     *
     *
     * @param element
     */

    public void addElement( PrintElement element ) {
        if( element != null ) {
            m_elements.add( element );
        }

        m_pe = null;
    }    // addElement

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PrintElement[] getElements() {
        if( m_pe == null ) {
            m_pe = new PrintElement[ m_elements.size()];
            m_elements.toArray( m_pe );
        }

        return m_pe;
    }    // getElements

    /**
     * Descripción de Método
     *
     *
     * @param g2D
     * @param bounds
     * @param isView
     */

    public void paint( Graphics2D g2D,Rectangle bounds,boolean isView ) {
        Point pageStart = new Point( bounds.getLocation());

        getElements();

        for( int i = 0;i < m_pe.length;i++ ) {
            m_pe[ i ].paint( g2D,0,pageStart,m_ctx,isView );
        }
    }    // paint

    /**
     * Descripción de Método
     *
     *
     * @param relativePoint
     *
     * @return
     */

    public MQuery getDrillDown( Point relativePoint ) {
        MQuery retValue = null;

        for( int i = 0;(i < m_elements.size()) && (retValue == null);i++ ) {
            PrintElement element = ( PrintElement )m_elements.get( i );

            retValue = element.getDrillDown( relativePoint,1 );
        }

        return retValue;
    }    // getDrillDown

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     *
     * @return
     */

    public InterfaceDesign getInterfaceDesign( int x,int y ) {
        for( int i = 0;i < m_elements.size();i++ ) {
            if(( m_elements.get( i ) instanceof DesignElement ) || ( m_elements.get( i ) instanceof ImageDesElement )) {
                InterfaceDesign de = ( InterfaceDesign )m_elements.get( i );

                if( de.isClicked( x,y ) || de.isCornerClicked( x,y )) {
                    return( de );
                }
            }
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param ID
     *
     * @return
     */

    public InterfaceDesign getInterfaceDesign( int ID ) {
        for( int i = 0;i < m_elements.size();i++ ) {
            if(( m_elements.get( i ) instanceof DesignElement ) || ( m_elements.get( i ) instanceof ImageDesElement )) {
                InterfaceDesign de = ( InterfaceDesign )m_elements.get( i );

                if( de.getPrintFormatItemID() == ID ) {
                    return( de );
                }
            }
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param ID
     * @param SeqNo
     *
     * @return
     */

    public int modifySeqNo( int ID,int SeqNo ) {
        int oldSeqno = 0;
        int posOld   = 0;
        int newSeqno = 0;
        int posNew   = 0;

        for( int i = 0;i < m_elements.size();i++ ) {
            if( m_elements.get( i ) instanceof InterfaceDesign ) {
                InterfaceDesign de = ( InterfaceDesign )m_elements.get( i );

                if(( de.getPrintFormatItemID() == ID )) {
                    oldSeqno = de.getSeqNo();
                    posOld   = i;
                }
            }
        }

        posNew   = posOld;
        newSeqno = oldSeqno;

        for( int i = 0;i < m_elements.size();i++ ) {
            if( m_elements.get( i ) instanceof InterfaceDesign ) {
                InterfaceDesign de = ( InterfaceDesign )m_elements.get( i );

                if( (de.getSeqNo() < newSeqno) && (i != posOld) ) {
                    newSeqno = de.getSeqNo();
                    posNew   = i;
                }
            }
        }

        if( newSeqno != oldSeqno ) {
            for( int i = 0;i < m_elements.size();i++ ) {
                if( m_elements.get( i ) instanceof InterfaceDesign ) {
                    InterfaceDesign de = ( InterfaceDesign )m_elements.get( i );

                    if( (de.getSeqNo() > newSeqno) && (de.getSeqNo() < oldSeqno) ) {
                        newSeqno = de.getSeqNo();
                        posNew   = i;
                    }
                }
            }
        }

        InterfaceDesign de = ( InterfaceDesign )m_elements.get( posOld );

        de.setSeqNo( newSeqno );
        de = ( InterfaceDesign )m_elements.get( posNew );
        de.setSeqNo( oldSeqno );

        return newSeqno;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int headerFooterSeq() {
        int aux = 0;

        for( int i = 0;i < m_elements.size() - 1;i++ ) {
            boolean repeat = false;

            for( int j = 0;j < ( m_elements.size() - i ) - 1;j++ ) {
                InterfaceDesign de  = ( InterfaceDesign )m_elements.get( j );
                InterfaceDesign de2 = ( InterfaceDesign )m_elements.get( j + 1 );

                if( de2.getSeqNo() < de.getSeqNo()) {
                    aux = de.getSeqNo();
                    de.setSeqNo( de2.getSeqNo());
                    de2.setSeqNo( aux );
                }
            }
        }

        int seqNo = 10;

        for( int k = 0;k < m_elements.size();k++ ) {
            InterfaceDesign de = ( InterfaceDesign )m_elements.get( k );

            de.setSeqNo( seqNo );
            seqNo += 10;
        }

        return seqNo;
    }
}    // HeaderFooterDesign



/*
 *  @(#)HeaderFooterDesign.java   02.07.07
 * 
 *  Fin del fichero HeaderFooterDesign.java
 *  
 *  Versión 2.2
 *
 */
