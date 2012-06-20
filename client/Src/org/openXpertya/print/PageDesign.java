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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Properties;

import org.openXpertya.model.MQuery;
import org.openXpertya.print.layout.PrintElement;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PageDesign {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param pageNo
     */

    public PageDesign( Properties ctx,int pageNo ) {
        m_ctx    = ctx;
        m_pageNo = pageNo;

        if( (m_pageInfo == null) || (m_pageInfo.length() == 0) ) {
            m_pageInfo = String.valueOf( m_pageNo );
        }
    }    // Page

    /** Descripción de Campos */

    public static final String CONTEXT_PAGE = "*Page";

    /** Descripción de Campos */

    public static final String CONTEXT_PAGECOUNT = "*PageCount";

    /** Descripción de Campos */

    public static final String CONTEXT_MULTIPAGE = "*MultiPageInfo";

    /** Descripción de Campos */

    public static final String CONTEXT_COPY = "*CopyInfo";

    /** Descripción de Campos */

    public static final String CONTEXT_REPORTNAME = "*ReportName";

    /** Descripción de Campos */

    public static final String CONTEXT_HEADER = "*Header";

    /** Descripción de Campos */

    public static final String CONTEXT_DATE = "*CurrentDate";

    /** Descripción de Campos */

    public static final String CONTEXT_TIME = "*CurrentDateTime";

    /** Descripción de Campos */

    private int m_pageNo;

    /** Descripción de Campos */

    private int m_pageCount = 1;

    /** Descripción de Campos */

    private String m_pageInfo;

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private ArrayList m_elements = new ArrayList();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPageNo() {
        return m_pageNo;
    }    // getPageNo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPageInfo() {
        return m_pageInfo;
    }    // getPageInfo

    /**
     * Descripción de Método
     *
     *
     * @param pageInfo
     */

    public void setPageInfo( String pageInfo ) {
        if( (m_pageInfo == null) || (m_pageInfo.length() == 0) ) {
            m_pageInfo = String.valueOf( m_pageNo );
        }

        m_pageInfo = pageInfo;
    }    // getPageInfo

    /**
     * Descripción de Método
     *
     *
     * @param pageCount
     */

    public void setPageCount( int pageCount ) {
        m_pageCount = pageCount;
    }    // setPageCount

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
    }    // addElement

    /**
     * Descripción de Método
     *
     *
     * @param g2D
     * @param bounds
     * @param isView
     * @param isCopy
     */

    public void paint( Graphics2D g2D,Rectangle bounds,boolean isView,boolean isCopy ) {
        m_ctx.put( CONTEXT_PAGE,m_pageInfo );

        // log.finest( "PrintContext", CONTEXT_PAGE + "=" + m_pageInfo);
        //

        StringBuffer sb = new StringBuffer();

        if( m_pageCount != 1 ) {    // set to "Page 1 of 2"
            sb.append( Msg.getMsg( m_ctx,"Page" )).append( " " ).append( m_pageNo ).append( " " ).append( Msg.getMsg( m_ctx,"of" )).append( " " ).append( m_pageCount );
        } else {
            sb.append( " " );
        }

        m_ctx.put( CONTEXT_MULTIPAGE,sb.toString());

        // log.finest( "PrintContext", CONTEXT_MULTIPAGE + "=" + sb.toString());
        //

        sb = new StringBuffer();

        if( isCopy ) {    // set to "(Copy)"
            sb.append( "(" ).append( Msg.getMsg( m_ctx,"DocumentCopy" )).append( ")" );
        } else {
            sb.append( " " );
        }

        m_ctx.put( CONTEXT_COPY,sb.toString());

        // log.finest( "PrintContext copy=" + isCopy, CONTEXT_COPY + "=" + sb.toString());

        // Paint Background

        g2D.setColor( Color.white );
        g2D.fillRect( bounds.x,bounds.y,bounds.width,bounds.height );

        //

        Point pageStart = new Point( bounds.getLocation());

        for( int i = 0;i < m_elements.size();i++ ) {
            PrintElement e = ( PrintElement )m_elements.get( i );

            e.paint( g2D,m_pageNo,pageStart,m_ctx,isView );
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

            retValue = element.getDrillDown( relativePoint,m_pageNo );
        }

        return retValue;
    }    // getDrillDown

    /**
     * Descripción de Método
     *
     *
     * @param relativePoint
     *
     * @return
     */

    public MQuery getDrillAcross( Point relativePoint ) {
        MQuery retValue = null;

        for( int i = 0;(i < m_elements.size()) && (retValue == null);i++ ) {
            PrintElement element = ( PrintElement )m_elements.get( i );

            retValue = element.getDrillAcross( relativePoint,m_pageNo );
        }

        return retValue;
    }    // getDrillAcross

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "Page[" );

        sb.append( m_pageNo ).append( ",Elements=" ).append( m_elements.size());
        sb.append( "]" );

        return sb.toString();
    }    // toString

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
            if( m_elements.get( i ) instanceof InterfaceDesign ) {
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
            if( m_elements.get( i ) instanceof InterfaceDesign )    // DesignElement) || (m_elements.get(i) instanceof ImageDesElement))
            {
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
     * @param n
     */

    public void pageDesignSeq( int n ) {
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

        int seqNo = n;

        for( int k = 0;k < m_elements.size();k++ ) {
            InterfaceDesign de = ( InterfaceDesign )m_elements.get( k );

            de.setSeqNo( seqNo );
            seqNo += 10;
        }
    }
}    // PageDesign



/*
 *  @(#)PageDesign.java   02.07.07
 * 
 *  Fin del fichero PageDesign.java
 *  
 *  Versión 2.2
 *
 */
