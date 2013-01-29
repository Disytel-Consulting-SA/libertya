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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Properties;

import org.openXpertya.model.MQuery;
import org.openXpertya.util.Msg;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Page {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param pageNo
     */

    public Page( Properties ctx,int pageNo ) {
        m_ctx    = ctx;
        m_pageNo = pageNo;

        if( (m_pageInfo == null) || (m_pageInfo.length() == 0) ) {
            m_pageInfo = String.valueOf( m_pageNo );
        }
    }    // Page

    /** Descripci�n de Campos */

    public static final String CONTEXT_PAGE = "*Page";

    /** Descripci�n de Campos */

    public static final String CONTEXT_PAGECOUNT = "*PageCount";

    /** Descripci�n de Campos */

    public static final String CONTEXT_MULTIPAGE = "*MultiPageInfo";

    /** Descripci�n de Campos */

    public static final String CONTEXT_COPY = "*CopyInfo";

    /** Descripci�n de Campos */

    public static final String CONTEXT_REPORTNAME = "*ReportName";

    /** Descripci�n de Campos */

    public static final String CONTEXT_HEADER = "*Header";

    /** Descripci�n de Campos */

    public static final String CONTEXT_DATE = "*CurrentDate";

    /** Descripci�n de Campos */

    public static final String CONTEXT_TIME = "*CurrentDateTime";

    /** Descripci�n de Campos */

    private int m_pageNo;

    /** Descripci�n de Campos */

    private int m_pageCount = 1;

    /** Descripci�n de Campos */

    private String m_pageInfo;

    /** Descripci�n de Campos */

    private Properties m_ctx;

    /** Descripci�n de Campos */

    private ArrayList m_elements = new ArrayList();

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public int getPageNo() {
        return m_pageNo;
    }    // getPageNo

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public String getPageInfo() {
        return m_pageInfo;
    }    // getPageInfo

    /**
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
     *
     *
     * @param pageCount
     */

    public void setPageCount( int pageCount ) {
        m_pageCount = pageCount;
    }    // setPageCount

    /**
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
}    // Page



/*
 *  @(#)Page.java   12.10.07
 * 
 *  Fin del fichero Page.java
 *  
 *  Versión 2.2
 *
 */
