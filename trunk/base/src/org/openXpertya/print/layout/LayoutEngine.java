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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.attribute.DocAttributeSet;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MQuery;
import org.openXpertya.print.ArchiveEngine;
import org.openXpertya.print.CPaper;
import org.openXpertya.print.DataEngine;
import org.openXpertya.print.MPrintColor;
import org.openXpertya.print.MPrintFont;
import org.openXpertya.print.MPrintFormat;
import org.openXpertya.print.MPrintFormatItem;
import org.openXpertya.print.MPrintPaper;
import org.openXpertya.print.MPrintTableFormat;
import org.openXpertya.print.PrintData;
import org.openXpertya.print.PrintDataElement;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.NamePair;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class LayoutEngine implements Pageable,Printable,Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param format
     * @param data
     * @param query
     */

    public LayoutEngine( MPrintFormat format,PrintData data,MQuery query ) {
        log.info( format + " - " + data + " - " + query );

        // s_FASTDRAW = MClient.get(format.getCtx()).isUseBetaFunctions();
        //

        setPrintFormat( format,false );
        setPrintData( data,query,false );
        layout();
    }    // LayoutEngine

    /** Descripci�n de Campos */

    private static CLogger log = CLogger.getCLogger( LayoutEngine.class );

    /** Descripci�n de Campos */

    private boolean m_hasLayout = false;

    /** Descripci�n de Campos */

    private MPrintFormat m_format;

    /** Descripci�n de Campos */

    private Properties m_printCtx;

    /** Descripci�n de Campos */

    private PrintData m_data;

    /** Descripci�n de Campos */

    private MQuery m_query;

    /** Descripci�n de Campos */

    private MPrintColor m_printColor;

    /** Descripci�n de Campos */

    private MPrintFont m_printFont;

    /** Descripci�n de Campos */

    private int m_columnCount = -1;

    /** Descripci�n de Campos */

    private CPaper m_paper;

    /** Descripci�n de Campos */

    private int m_headerHeight = 18;    // 1/4" => 72/4

    /** Descripci�n de Campos */

    private int m_footerHeight = 18;

    /** Descripci�n de Campos */

    private int m_pageNo = 0;

    /** Descripci�n de Campos */

    private Page m_currPage;

    /** Descripci�n de Campos */

    private ArrayList m_pages = new ArrayList();

    /** Descripci�n de Campos */

    private HeaderFooter m_headerFooter;

    /** Descripci�n de Campos */

    private Rectangle m_header = new Rectangle();

    /** Descripci�n de Campos */

    private Rectangle m_content = new Rectangle();

    /** Descripci�n de Campos */

    private Rectangle m_footer = new Rectangle();

    /** Descripci�n de Campos */

    private int m_tempNLPositon = 0;

    /** Descripci�n de Campos */

    public static final int AREA_HEADER = 0;

    /** Descripci�n de Campos */

    public static final int AREA_CONTENT = 1;

    /** Descripci�n de Campos */

    public static final int AREA_FOOTER = 2;

    /** Descripci�n de Campos */

    private int m_area = AREA_CONTENT;

    /** Descripci�n de Campos */

    private Point2D.Double[] m_position = new Point2D.Double[]{ new Point2D.Double( 0,0 ),new Point2D.Double( 0,0 ),new Point2D.Double( 0,0 )};

    /** Descripci�n de Campos */

    private float m_maxHeightSinceNewLine[] = new float[]{ 0f,0f,0f };

    /** Descripci�n de Campos */

    private TableElement m_tableElement = null;

    /** Descripci�n de Campos */

    private float m_lastHeight[] = new float[]{ 0f,0f,0f };

    /** Descripci�n de Campos */

    private float m_lastWidth[] = new float[]{ 0f,0f,0f };

    /** Descripci�n de Campos */

    public static boolean s_FASTDRAW = true;

    /** Descripci�n de Campos */

    private boolean m_isCopy = false;

    /** Descripci�n de Campos */

    public static Image IMAGE_TRUE = null;

    /** Descripci�n de Campos */

    public static Image IMAGE_FALSE = null;

    /** Descripci�n de Campos */

    public static Dimension IMAGE_SIZE = new Dimension( 10,10 );

    static {
        Toolkit tk  = Toolkit.getDefaultToolkit();
        URL     url = LayoutEngine.class.getResource( "true10.gif" );

        if( url != null ) {
            IMAGE_TRUE = tk.getImage( url );
        }

        url = LayoutEngine.class.getResource( "false10.gif" );

        if( url != null ) {
            IMAGE_FALSE = tk.getImage( url );
        }
    }    // static init

    /**
     * Descripci�n de M�todo
     *
     *
     * @param format
     * @param doLayout
     */

    public void setPrintFormat( MPrintFormat format,boolean doLayout ) {
        m_format = format;

        // Initial & Default Settings

        m_printCtx = new Properties( format.getCtx());

        // Set Paper

        boolean tempHasLayout = m_hasLayout;

        m_hasLayout = false;    // do not start re-calculation

        MPrintPaper mPaper = MPrintPaper.get( format.getAD_PrintPaper_ID());

        if( m_format.isStandardHeaderFooter()) {
            setPaper( mPaper.getCPaper());
        } else {
            setPaper( mPaper.getCPaper(),m_format.getHeaderMargin(),m_format.getFooterMargin());
        }

        m_hasLayout = tempHasLayout;

        //

        m_printColor = MPrintColor.get( getCtx(),format.getAD_PrintColor_ID());
        m_printFont  = MPrintFont.get( format.getAD_PrintFont_ID());

        // Print Context

        Env.setContext( m_printCtx,Page.CONTEXT_REPORTNAME,m_format.getName());
        Env.setContext( m_printCtx,Page.CONTEXT_HEADER,Env.getHeader( m_printCtx,0 ));
        Env.setContext( m_printCtx,Env.LANGUAGE,m_format.getLanguage().getAD_Language());

        if( m_hasLayout && doLayout ) {
            layout();    // re-calculate
        }
    }                    // setPrintFormat

    /**
     * Descripci�n de M�todo
     *
     *
     * @param data
     * @param query
     * @param doLayout
     */

    public void setPrintData( PrintData data,MQuery query,boolean doLayout ) {
        m_data  = data;
        m_query = query;

        if( m_hasLayout && doLayout ) {
            layout();    // re-calculate
        }
    }                    // setPrintData

    /**
     * Descripci�n de M�todo
     *
     *
     * @param paper
     */

    public void setPaper( CPaper paper ) {
        setPaper( paper,m_headerHeight,m_footerHeight );
    }    // setPaper

    /**
     * Descripci�n de M�todo
     *
     *
     * @param paper
     * @param headerHeight
     * @param footerHeight
     */

    public void setPaper( CPaper paper,int headerHeight,int footerHeight ) {
        if( paper == null ) {
            return;
        }

        //

        boolean paperChange = (headerHeight != m_headerHeight) || (footerHeight != m_footerHeight);

        if( !paperChange ) {
            paperChange = !paper.equals( m_paper );
        }

        //

        log.fine( paper + " - Header=" + headerHeight + ", Footer=" + footerHeight );
        m_paper        = paper;
        m_headerHeight = headerHeight;
        m_footerHeight = footerHeight;
        calculatePageSize();

        //

        if( m_hasLayout && paperChange ) {
            layout();    // re-calculate
        }
    }                    // setPaper

    /**
     * Descripci�n de M�todo
     *
     *
     * @param job
     */

    public void pageSetupDialog( PrinterJob job ) {
        log.info( "" );

        if( m_paper.pageSetupDialog( job )) {
            setPaper( m_paper );
            layout();
        }
    }    // pageSetupDialog

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pf
     */

    protected void setPageFormat( PageFormat pf ) {
        if( pf != null ) {
            setPaper( new CPaper( pf ));
        } else {
            setPaper( null );
        }
    }    // setPageFormat

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public PageFormat getPageFormat() {
        return m_paper.getPageFormat();
    }    // getPageFormat

    /**
     * Descripci�n de M�todo
     *
     */

    private void calculatePageSize() {
        int x = ( int )m_paper.getImageableX( true );
        int w = ( int )m_paper.getImageableWidth( true );

        //

        int y      = ( int )m_paper.getImageableY( true );
        int h      = ( int )m_paper.getImageableHeight( true );
        int height = m_headerHeight;

        m_header.setBounds( x,y,w,height );

        //

        y      += height;
        height = h - m_headerHeight - m_footerHeight;
        m_content.setBounds( x,y,w,height );

        //

        y      += height;
        height = m_footerHeight;
        m_footer.setBounds( x,y,w,height );
        log.fine( "Paper=" + m_paper + ",HeaderHeight=" + m_headerHeight + ",FooterHeight=" + m_footerHeight + " => Header=" + m_header + ",Contents=" + m_content + ",Footer=" + m_footer );
    }    // calculatePageSize

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public CPaper getPaper() {
        return m_paper;
    }    // getPaper

    /**
     * Descripci�n de M�todo
     *
     */

    private void layout() {

        // Header/Footer

        m_headerFooter = new HeaderFooter( m_printCtx );

        if( !m_format.isForm() && m_format.isStandardHeaderFooter()) {
            createStandardHeaderFooter();
        }

        //

        m_pageNo = 0;
        m_pages.clear();
        m_tableElement = null;
        newPage( true,false );    // initialize

        //

        if( m_format.isForm()) {
            layoutForm();
        } else {

            // Parameter

            PrintElement element = layoutParameter();

            if( element != null ) {
                m_currPage.addElement( element );
                element.setLocation( m_position[ AREA_CONTENT ] );
                m_position[ AREA_CONTENT ].y += element.getHeight() + 5;    // GAP
            }

            // Table

            if( m_data != null ) {
                element = layoutTable( m_format,m_data,0 );
                element.setLocation( m_content.getLocation());

                for( int p = 1;p <= element.getPageCount();p++ ) {
                    if( p != 1 ) {
                        newPage( true,false );
                    }

                    m_currPage.addElement( element );
                }
            }
        }

        //

        String pageInfo = String.valueOf( m_pages.size()) + getPageInfo( m_pages.size());

        Env.setContext( m_printCtx,Page.CONTEXT_PAGECOUNT,pageInfo );

        Timestamp now = new Timestamp( System.currentTimeMillis());

        Env.setContext( m_printCtx,Page.CONTEXT_DATE,DisplayType.getDateFormat( DisplayType.Date,m_format.getLanguage()).format( now ));
        Env.setContext( m_printCtx,Page.CONTEXT_TIME,DisplayType.getDateFormat( DisplayType.DateTime,m_format.getLanguage()).format( now ));

        // Update Page Info

        int pages = m_pages.size();

        for( int i = 0;i < pages;i++ ) {
            Page page   = ( Page )m_pages.get( i );
            int  pageNo = page.getPageNo();

            pageInfo = String.valueOf( pageNo ) + getPageInfo( pageNo );
            page.setPageInfo( pageInfo );
            page.setPageCount( pages );
        }

        m_hasLayout = true;
    }    // layout

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public Properties getCtx() {
        return m_printCtx;
    }    // getCtx

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public int getColumnCount() {
        return m_columnCount;
    }    // getColumnCount

    /**
     * Descripci�n de M�todo
     *
     *
     * @param area
     */

    protected void setArea( int area ) {
        if( m_area == area ) {
            return;
        }

        if( (area < 0) || (area > 2) ) {
            throw new ArrayIndexOutOfBoundsException( area );
        }

        m_area = area;
    }    // setArea

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public int getArea() {
        return m_area;
    }    // getArea

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public Rectangle getAreaBounds() {
        Rectangle part = m_content;

        if( m_area == AREA_HEADER ) {
            part = m_header;
        } else if( m_area == AREA_FOOTER ) {
            part = m_footer;
        }

        //

        return part;
    }    // getAreaBounds

    /**
     * Descripci�n de M�todo
     *
     *
     * @param force
     * @param preserveXPos
     *
     * @return
     */

    protected int newPage( boolean force,boolean preserveXPos ) {

        // We are on a new page

        if( !force && (m_position[ AREA_CONTENT ].getX() == m_content.x) && (m_position[ AREA_CONTENT ].getY() == m_content.y) ) {
            log.fine( "skipped" );

            return m_pageNo;
        }

        m_pageNo++;
        m_currPage = new Page( m_printCtx,m_pageNo );
        m_pages.add( m_currPage );

        //

        m_position[ AREA_HEADER ].setLocation( m_header.x,m_header.y );

        if( preserveXPos ) {
            m_position[ AREA_CONTENT ].setLocation( m_position[ AREA_CONTENT ].x,m_content.y );
        } else {
            m_position[ AREA_CONTENT ].setLocation( m_content.x,m_content.y );
        }

        m_position[ AREA_FOOTER ].setLocation( m_footer.x,m_footer.y );
        m_maxHeightSinceNewLine = new float[]{ 0f,0f,0f };
        log.finer( "Page=" + m_pageNo );

        return m_pageNo;
    }    // newPage

    /**
     * Descripci�n de M�todo
     *
     */

    protected void newLine() {
        Rectangle part = m_content;

        if( m_area == AREA_HEADER ) {
            part = m_header;
        } else if( m_area == AREA_FOOTER ) {
            part = m_footer;
        }

        // Temporary NL Position

        int xPos = part.x;

        if( m_tempNLPositon != 0 ) {
            xPos = m_tempNLPositon;
        }

        if( isYspaceFor( m_maxHeightSinceNewLine[ m_area ] )) {
            m_position[ m_area ].setLocation( xPos,m_position[ m_area ].y + m_maxHeightSinceNewLine[ m_area ] );
            log.finest( "Page=" + m_pageNo + " [" + m_area + "] " + m_position[ m_area ].x + "/" + m_position[ m_area ].y );
        } else if( m_area == AREA_CONTENT ) {
            log.finest( "Not enough Y space " + m_lastHeight[ m_area ] + " - remaining " + getYspace() + " - Area=" + m_area );
            newPage( true,false );
            log.finest( "Page=" + m_pageNo + " [" + m_area + "] " + m_position[ m_area ].x + "/" + m_position[ m_area ].y );
        } else    // footer/header
        {
            m_position[ m_area ].setLocation( part.x,m_position[ m_area ].y + m_maxHeightSinceNewLine[ m_area ] );
            log.log( Level.SEVERE,"Outside of Area(" + m_area + "): " + m_position[ m_area ] );
        }

        m_maxHeightSinceNewLine[ m_area ] = 0f;
    }    // newLine

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
     * @param pageNo
     *
     * @return
     */

    public Page getPage( int pageNo ) {
        if( (pageNo <= 0) || (pageNo > m_pages.size())) {
            log.log( Level.SEVERE,"No page #" + pageNo );

            return null;
        }

        Page retValue = ( Page )m_pages.get( pageNo - 1 );

        return retValue;
    }    // getPage

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public ArrayList getPages() {
        return m_pages;
    }    // getPages

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public HeaderFooter getHeaderFooter() {
        return m_headerFooter;
    }    // getPages

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pageNo
     */

    protected void setPage( int pageNo ) {
        if( (pageNo <= 0) || (pageNo > m_pages.size())) {
            log.log( Level.SEVERE,"No page #" + pageNo );

            return;
        }

        Page retValue = ( Page )m_pages.get( pageNo - 1 );

        m_currPage = retValue;
    }    // setPage

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pageNo
     *
     * @return
     */

    public String getPageInfo( int pageNo ) {
        if( (m_tableElement == null) || (m_tableElement.getPageXCount() == 1) ) {
            return "";
        }

        int          pi = m_tableElement.getPageIndex( pageNo );
        StringBuffer sb = new StringBuffer( "(" );

        sb.append( m_tableElement.getPageYIndex( pi ) + 1 ).append( "," ).append( m_tableElement.getPageXIndex( pi ) + 1 ).append( ")" );

        return sb.toString();
    }    // getPageInfo

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public String getPageInfoMax() {
        if( (m_tableElement == null) || (m_tableElement.getPageXCount() == 1) ) {
            return "";
        }

        StringBuffer sb = new StringBuffer( "(" );

        sb.append( m_tableElement.getPageYCount()).append( "," ).append( m_tableElement.getPageXCount()).append( ")" );

        return sb.toString();
    }    // getPageInfoMax

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public MPrintFormat getFormat() {
        return m_format;
    }    // getFormat

    /**
     * Descripci�n de M�todo
     *
     *
     * @param isCopy
     *
     * @return
     */

    public LayoutEngine getPageable( boolean isCopy ) {
        setCopy( isCopy );

        if( (getNumberOfPages() == 0) ||!ArchiveEngine.isValid( this )) {
            log.warning( "Nothing to print - " + toString());

            return null;
        }

        return this;
    }    // getPageable

    /**
     * Descripci�n de M�todo
     *
     *
     * @param p
     */

    protected void setRelativePosition( Point2D p ) {
        if( p == null ) {
            return;
        }

        Rectangle part = m_content;

        if( m_area == AREA_HEADER ) {
            part = m_header;
        } else if( m_area == AREA_FOOTER ) {
            part = m_footer;
        }

        m_position[ m_area ].setLocation( part.x + p.getX(),part.y + p.getY());
        log.finest( "Page=" + m_pageNo + " [" + m_area + "] " + m_position[ m_area ].x + "/" + m_position[ m_area ].y );
    }    // setPosition

    /**
     * Descripci�n de M�todo
     *
     *
     * @param x
     * @param y
     */

    protected void setRelativePosition( float x,float y ) {
        setRelativePosition( new Point2D.Float( x,y ));
    }    // setPosition

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public Point2D getPosition() {
        return m_position[ m_area ];
    }    // getPosition

    /**
     * Descripci�n de M�todo
     *
     *
     * @param x
     */

    protected void setX( float x ) {
        m_position[ m_area ].x = x;
        log.finest( "Page=" + m_pageNo + " [" + m_area + "] " + m_position[ m_area ].x + "/" + m_position[ m_area ].y );
    }    // setX

    /**
     * Descripci�n de M�todo
     *
     *
     * @param xOffset
     */

    protected void addX( float xOffset ) {
        if( xOffset == 0f ) {
            return;
        }

        m_position[ m_area ].x += xOffset;
        log.finest( "Page=" + m_pageNo + " [" + m_area + "] " + m_position[ m_area ].x + "/" + m_position[ m_area ].y );
    }    // addX

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public float getX() {
        return( float )m_position[ m_area ].x;
    }    // getX

    /**
     * Descripci�n de M�todo
     *
     *
     * @param y
     */

    protected void setY( int y ) {
        m_position[ m_area ].y = y;
        log.finest( "Page=" + m_pageNo + " [" + m_area + "] " + m_position[ m_area ].x + "/" + m_position[ m_area ].y );
    }    // setY

    /**
     * Descripci�n de M�todo
     *
     *
     * @param yOffset
     */

    protected void addY( int yOffset ) {
        if( yOffset == 0f ) {
            return;
        }

        if( isYspaceFor( yOffset )) {
            m_position[ m_area ].y += yOffset;
            log.finest( "Page=" + m_pageNo + " [" + m_area + "] " + m_position[ m_area ].x + "/" + m_position[ m_area ].y );
        } else if( m_area == AREA_CONTENT ) {
            log.finest( "Not enough Y space " + m_lastHeight[ m_area ] + " - remaining " + getYspace() + " - Area=" + m_area );
            newPage( true,true );
            log.finest( "Page=" + m_pageNo + " [" + m_area + "] " + m_position[ m_area ].x + "/" + m_position[ m_area ].y );
        } else {
            m_position[ m_area ].y += yOffset;
            log.log( Level.SEVERE,"Outside of Area: " + m_position );
        }
    }    // addY

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public float getY() {
        return( float )m_position[ m_area ].y;
    }    // getY

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public float getXspace() {
        Rectangle part = m_content;

        if( m_area == AREA_HEADER ) {
            part = m_header;
        } else if( m_area == AREA_FOOTER ) {
            part = m_footer;
        }

        //

        return( float )( part.x + part.width - m_position[ m_area ].x );
    }    // getXspace

    /**
     * Descripci�n de M�todo
     *
     *
     * @param width
     *
     * @return
     */

    public boolean isXspaceFor( float width ) {
        return( getXspace() - width ) > 0f;
    }    // isXspaceFor

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public float getYspace() {
        Rectangle part = m_content;

        if( m_area == AREA_HEADER ) {
            part = m_header;
        } else if( m_area == AREA_FOOTER ) {
            part = m_footer;
        }

        //

        return( float )( part.y + part.height - m_position[ m_area ].y );
    }    // getYspace

    /**
     * Descripci�n de M�todo
     *
     *
     * @param height
     *
     * @return
     */

    public boolean isYspaceFor( float height ) {
        return( getYspace() - height ) > 0f;
    }    // isYspaceFor

    /**
     * Descripci�n de M�todo
     *
     */

    private void createStandardHeaderFooter_2_0() {
        PrintElement element = new ImageElement( org.openXpertya.OpenXpertya.getImageLogoSmall());    // 48x15

        // element = new ImageElement(org.openXpertya.OpenXpertya.getImageLogo()); //      100x30

        element.layout( m_header.width,0,false,MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft );
        element.setLocation( m_header.getLocation());
        m_headerFooter.addElement( element );

        //

        MPrintTableFormat tf    = m_format.getTableFormat();
        Font              font  = tf.getPageHeader_Font();
        Color             color = tf.getPageHeaderFG_Color();

        //

        element = new StringElement( "@*ReportName@",font,color,null,true );
        element.layout( m_header.width,0,true,MPrintFormatItem.FIELDALIGNMENTTYPE_Center );
        element.setLocation( m_header.getLocation());
        m_headerFooter.addElement( element );

        //
        //

        element = new StringElement( "@Page@ @*Page@ @of@ @*PageCount@",font,color,null,true );
        element.layout( m_header.width,0,true,MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight );
        element.setLocation( m_header.getLocation());
        m_headerFooter.addElement( element );

        // Footer

        font  = tf.getPageFooter_Font();
        color = tf.getPageFooterFG_Color();

        //

        element = new StringElement( OpenXpertya.OXP_R,font,color,null,true );

        // element = new StringElement(OpenXpertya.NAME, font, color, null, true);

        element.layout( m_footer.width,0,true,MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft );

        Point ft = m_footer.getLocation();

        ft.y += m_footer.height - element.getHeight() - 2;    // 2pt above min
        element.setLocation( ft );
        m_headerFooter.addElement( element );

        //

        element = new StringElement( "@*Header@",font,color,null,true );
        element.layout( m_footer.width,0,true,MPrintFormatItem.FIELDALIGNMENTTYPE_Center );
        element.setLocation( ft );
        m_headerFooter.addElement( element );

        //

        element = new StringElement( "@*CurrentDateTime@",font,color,null,true );
        element.layout( m_footer.width,0,true,MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight );
        element.setLocation( ft );
        m_headerFooter.addElement( element );
    }    // createStandardHeaderFooter

    private void createStandardHeaderFooter()
	{
		//
		MPrintTableFormat tf = m_format.getTableFormat();
		Font font = tf.getPageHeader_Font();
		Color color = tf.getPageHeaderFG_Color();
		
		//	element = new ImageElement(org.compiere.Compiere.getImageLogo());	//	100x30
		/** Removing/modifying the Compiere logo is a violation of the license	*/
		// No me jodas....
		//PrintElement element = new ImageElement(org.openXpertya.OpenXpertya.getImageLogoSmall());	//	48x15
		PrintElement element = new StringElement( MClient.get(Env.getCtx()).getName(), font, color, null, true);
		element.layout(m_header.width, 0, false, MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft);
		element.setLocation(m_header.getLocation());
		m_headerFooter.addElement(element);

		//
		element = new StringElement("@*ReportName@", font, color, null, true);
		element.layout (m_header.width, 0, true, MPrintFormatItem.FIELDALIGNMENTTYPE_Center);
		element.setLocation(m_header.getLocation());
		m_headerFooter.addElement(element);
		//
		//
		element = new StringElement("@Page@ @*Page@ @of@ @*PageCount@", font, color, null, true);
		element.layout (m_header.width, 0, true, MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight);
		element.setLocation(m_header.getLocation());
		m_headerFooter.addElement(element);

		//	Footer
		font = tf.getPageFooter_Font();
		color = tf.getPageFooterFG_Color();
		//

		/** Removing/modifying the Compiere logo is a violation of the license	*/
		
		//element = new StringElement(OpenXpertya.COPYRIGHT, font, color, null, true);
		//element.layout (m_footer.width, 0, true, MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft);
		Point ft = m_footer.getLocation();
		ft.y += m_footer.height - element.getHeight() - 2;	//	2pt above min
		//element.setLocation(ft);
		//m_headerFooter.addElement(element);
		
		//
		//element = new StringElement("@*Header@", font, color, null, true);
		//element.layout (m_footer.width, 0, true, MPrintFormatItem.FIELDALIGNMENTTYPE_Center);
		//element.setLocation(ft);
		//m_headerFooter.addElement(element);
		//
		element = new StringElement("@*CurrentDateTime@", font, color, null, true);
		element.layout (m_footer.width, 0, true, MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight);
		element.setLocation(ft);
		m_headerFooter.addElement(element);
	}	//	createStandardHeaderFooter

    
    /**
     * Descripci�n de M�todo
     *
     */

    private void layoutForm() {

        // log.info("layoutForm");

        m_columnCount = 0;

        if( m_data == null ) {
            return;
        }

        // for every row

        for( int row = 0;row < m_data.getRowCount();row++ ) {
            log.info( "Row=" + row );
            m_data.setRowIndex( row );

            boolean somethingPrinted = true;    // prevent NL of nothing printed and supress null

            // for every item

            for( int i = 0;i < m_format.getItemCount();i++ ) {
                MPrintFormatItem item = m_format.getItem( i );

                // log.fine("layoutForm - Row=" + row + " - #" + i + " - " + item);

                if( !item.isPrinted()) {
                    continue;
                }

                // log.fine("layoutForm - Row=" + row + " - #" + i + " - " + item);

                m_columnCount++;

                // Read Header/Footer just once

                if( (row > 0) && ( item.isHeader() || item.isFooter())) {
                    continue;
                }

                // Position

                if( item.isHeader()) {      // Area
                    setArea( AREA_HEADER );
                } else if( item.isFooter()) {
                    setArea( AREA_FOOTER );
                } else {
                    setArea( AREA_CONTENT );
                }

                //

                if( item.isSetNLPosition() && item.isRelativePosition()) {
                    m_tempNLPositon = 0;
                }

                // New Page/Line

                if( item.isNextPage()) {    // item.isPageBreak()                      //      new page
                    newPage( false,false );
                } else if( item.isNextLine() && somethingPrinted )    // new line
                {
                    newLine();
                    somethingPrinted = false;
                } else {
                    addX( m_lastWidth[ m_area ] );
                }

                // Relative Position space

                if( item.isRelativePosition()) {
                    addX( item.getXSpace());
                    addY( item.getYSpace());
                } else {                                              // Absolute relative position
                    setRelativePosition( item.getXPosition(),item.getYPosition());
                }

                // Temporary NL Position when absolute positioned

                if( item.isSetNLPosition() &&!item.isRelativePosition()) {
                    m_tempNLPositon = ( int )getPosition().getX();
                }

                // line alignment

                String  alignment   = item.getFieldAlignmentType();
                int     maxWidth    = item.getMaxWidth();
                boolean lineAligned = false;

                if( item.isRelativePosition()) {
                    if( item.isLineAlignLeading()) {
                        alignment = MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft;
                        maxWidth    = getAreaBounds().width;
                        lineAligned = true;
                    } else if( item.isLineAlignCenter()) {
                        alignment   = MPrintFormatItem.FIELDALIGNMENTTYPE_Center;
                        maxWidth    = getAreaBounds().width;
                        lineAligned = true;
                    } else if( item.isLineAlignTrailing()) {
                        alignment = MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight;
                        maxWidth    = getAreaBounds().width;
                        lineAligned = true;
                    }
                }

                // Type

                PrintElement element = null;

                if( item.isTypePrintFormat())     // ** included PrintFormat
                {
                    element = includeFormat( item,m_data );
                } else if( item.isTypeImage())    // **    Image
                {
                    if( item.isImageField()) {
                        element = createImageElement( item );
                    } else if( item.isImageIsAttached()) {
                        element = new ImageElement( item.getID());
                    } else {
                        element = new ImageElement( item.getImageURL());
                    }

                    element.layout( maxWidth,item.getMaxHeight(),false,alignment );
                } else if( item.isTypeField())    // **    Field
                {
                    if( (maxWidth == 0) && item.isFieldAlignBlock()) {
                        maxWidth = getAreaBounds().width;
                    }

                    element = createFieldElement( item,maxWidth,alignment,m_format.isForm());
                } else if( item.isTypeBox())    // **    Line/Box
                {
                    if( m_format.isForm()) {
                        element = createBoxElement( item );
                    }
                } else                          // (item.isTypeText())             //**    Text
                {
                    if( (maxWidth == 0) && item.isFieldAlignBlock()) {
                        maxWidth = getAreaBounds().width;
                    }

                    element = createStringElement( item.getPrintName( m_format.getLanguage()),item.getAD_PrintColor_ID(),item.getAD_PrintFont_ID(),maxWidth,item.getMaxHeight(),item.isHeightOneLine(),alignment,true );
                }

                // Printed - set last width/height

                if( element != null ) {
                    somethingPrinted = true;

                    if( !lineAligned ) {
                        m_lastWidth[ m_area ] = element.getWidth();
                    }

                    m_lastHeight[ m_area ] = element.getHeight();
                } else {
                    somethingPrinted       = false;
                    m_lastWidth[ m_area ]  = 0f;
                    m_lastHeight[ m_area ] = 0f;
                }

                // Does it fit?

                if( item.isRelativePosition() &&!lineAligned ) {
                    if( !isXspaceFor( m_lastWidth[ m_area ] )) {
                        log.finest( "Not enough X space for " + m_lastWidth[ m_area ] + " - remaining " + getXspace() + " - Area=" + m_area );
                        newLine();
                    }

                    if( (m_area == AREA_CONTENT) &&!isYspaceFor( m_lastHeight[ m_area ] )) {
                        log.finest( "Not enough Y space " + m_lastHeight[ m_area ] + " - remaining " + getYspace() + " - Area=" + m_area );
                        newPage( true,true );
                    }
                }

                // We know Position and Size
                // log.fine( "LayoutEngine.layoutForm",
                // "Page=" + m_pageNo + " [" + m_area + "] " + m_position[m_area].x + "/" + m_position[m_area].y
                // + " w=" + lastWidth[m_area] + ",h=" + lastHeight[m_area] + " " + item);

                if( element != null ) {
                    element.setLocation( m_position[ m_area ] );
                }

                // Add to Area

                if( m_area == AREA_CONTENT ) {
                    m_currPage.addElement( element );
                } else {
                    m_headerFooter.addElement( element );
                }

                //

                if( m_lastHeight[ m_area ] > m_maxHeightSinceNewLine[ m_area ] ) {
                    m_maxHeightSinceNewLine[ m_area ] = m_lastHeight[ m_area ];
                }
            }    // for every item
        }        // for every row
    }            // layoutForm

    /**
     * Descripci�n de M�todo
     *
     *
     * @param item
     * @param data
     *
     * @return
     */

    private PrintElement includeFormat( MPrintFormatItem item,PrintData data ) {
        newLine();

        PrintElement element = null;

        //

        MPrintFormat format = MPrintFormat.get( getCtx(),item.getAD_PrintFormatChild_ID(),false );

        format.setLanguage( m_format.getLanguage());

        if( m_format.isTranslationView()) {
            format.setTranslationLanguage( m_format.getLanguage());
        }

        int AD_Column_ID = item.getAD_Column_ID();

        log.info( format + " - Item=" + item.getName() + " (" + AD_Column_ID + ")" );

        //

        Object obj = data.getNode( new Integer( AD_Column_ID ));

        // Object obj = data.getNode(item.getColumnName());        //      slower

        if( obj == null ) {
            data.dumpHeader();
            data.dumpCurrentRow();
            log.log( Level.SEVERE,"No Node - AD_Column_ID=" + AD_Column_ID + " - " + item + " - " + data );

            return null;
        }

        PrintDataElement dataElement  = ( PrintDataElement )obj;
        String           recordString = dataElement.getValueKey();

        if( (recordString == null) || (recordString.length() == 0) ) {
            data.dumpHeader();
            data.dumpCurrentRow();
            log.log( Level.SEVERE,"No Record Key - " + dataElement + " - AD_Column_ID=" + AD_Column_ID + " - " + item );

            return null;
        }

        int Record_ID = 0;

        try {
            Record_ID = Integer.parseInt( recordString );
        } catch( Exception e ) {
            data.dumpCurrentRow();
            log.log( Level.SEVERE,"Invalid Record Key - " + recordString + " (" + e.getMessage() + ") - AD_Column_ID=" + AD_Column_ID + " - " + item );

            return null;
        }

        MQuery query = new MQuery( format.getAD_Table_ID());

        query.addRestriction( item.getColumnName(),MQuery.EQUAL,new Integer( Record_ID ));
        format.setTranslationViewQuery( query );
        log.fine( query.toString());

        //

        DataEngine de           = new DataEngine( format.getLanguage());
        PrintData  includedData = de.getPrintData( data.getCtx(),format,query );

        log.fine( includedData.toString());

        if( includedData == null ) {
            return null;
        }

        //

        element = layoutTable( format,includedData,item.getXSpace());

        // handle multi page tables

        if( element.getPageCount() > 1 ) {
            Point2D.Double loc = m_position[ m_area ];

            element.setLocation( loc );

            for( int p = 1;p < element.getPageCount();
                    p++ )    // don't add last one
            {
                m_currPage.addElement( element );
                newPage( true,false );
            }

            m_position[ m_area ] = loc;
            (( TableElement )element ).setHeightToLastPage();
        }

        m_lastWidth[ m_area ]  = element.getWidth();
        m_lastHeight[ m_area ] = element.getHeight();

        if( !isXspaceFor( m_lastWidth[ m_area ] )) {
            log.finest( "Not enough X space for " + m_lastWidth[ m_area ] + " - remaining " + getXspace() + " - Area=" + m_area );
            newLine();
        }

        if( (m_area == AREA_CONTENT) &&!isYspaceFor( m_lastHeight[ m_area ] )) {
            log.finest( "Not enough Y space " + m_lastHeight[ m_area ] + " - remaining " + getYspace() + " - Area=" + m_area );
            newPage( true,false );
        }

        //

        return element;
    }    // includeFormat

    /**
     * Descripci�n de M�todo
     *
     *
     * @param content
     * @param AD_PrintColor_ID
     * @param AD_PrintFont_ID
     * @param maxWidth
     * @param maxHeight
     * @param isHeightOneLine
     * @param FieldAlignmentType
     * @param isTranslated
     *
     * @return
     */

    private PrintElement createStringElement( String content,int AD_PrintColor_ID,int AD_PrintFont_ID,int maxWidth,int maxHeight,boolean isHeightOneLine,String FieldAlignmentType,boolean isTranslated ) {
        if( (content == null) || (content.length() == 0) ) {
            return null;
        }

        // Color / Font

        Color color = getColor();    // default

        if( (AD_PrintColor_ID != 0) && (m_printColor.getID() != AD_PrintColor_ID) ) {
            MPrintColor c = MPrintColor.get( getCtx(),AD_PrintColor_ID );

            if( c.getColor() != null ) {
                color = c.getColor();
            }
        }

        Font font = m_printFont.getFont();    // default

        if( (AD_PrintFont_ID != 0) && (m_printFont.getID() != AD_PrintFont_ID) ) {
            MPrintFont f = MPrintFont.get( AD_PrintFont_ID );

            if( f.getFont() != null ) {
                font = f.getFont();
            }
        }

        PrintElement e = new StringElement( content,font,color,null,isTranslated );

        e.layout( maxWidth,maxHeight,isHeightOneLine,FieldAlignmentType );

        return e;
    }    // createStringElement

    /**
     * Descripci�n de M�todo
     *
     *
     * @param item
     * @param maxWidth
     * @param FieldAlignmentType
     * @param isForm
     *
     * @return
     */

    private PrintElement createFieldElement( MPrintFormatItem item,int maxWidth,String FieldAlignmentType,boolean isForm ) {

        // Get Data

        Object obj = m_data.getNode( new Integer( item.getAD_Column_ID()));

        if( obj == null ) {
            return null;
        } else if( obj instanceof PrintDataElement ) {
            ;
        } else {
            log.log( Level.SEVERE,"Element not PrintDataElement " + obj.getClass());

            return null;
        }

        // Convert DataElement to String

        PrintDataElement data = ( PrintDataElement )obj;

        if( data.isNull() && item.isSuppressNull()) {
            return null;
        }

        String stringContent = data.getValueDisplay( m_format.getLanguage());

        if( ( (stringContent == null) || (stringContent.length() == 0) ) && item.isSuppressNull()) {
            return null;
        }

        // non-string

        Object content = stringContent;

        if( data.getValue() instanceof Boolean ) {
            content = data.getValue();
        }

        // Convert AmtInWords Content to alpha

        if( item.getColumnName().equals( "AmtInWords" )) {
            log.fine( "AmtInWords: " + stringContent );
            stringContent = Msg.getAmtInWords( m_format.getLanguage(),stringContent );
            content = stringContent;
        }

        // Label

        String label       = item.getPrintName( m_format.getLanguage());
        String labelSuffix = item.getPrintNameSuffix( m_format.getLanguage());

        // ID Type

        NamePair ID = null;

        if( data.isID()) {    // Record_ID/ColumnName
            Object value = data.getValue();

            if( value instanceof KeyNamePair ) {
                ID = new KeyNamePair((( KeyNamePair )value ).getKey(),item.getColumnName());
            } else if( value instanceof ValueNamePair ) {
                ID = new ValueNamePair((( ValueNamePair )value ).getValue(),item.getColumnName());
            }
        } else if( MPrintFormatItem.FIELDALIGNMENTTYPE_Default.equals( FieldAlignmentType )) {
            if( data.isNumeric()) {
                FieldAlignmentType = MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight;
            } else {
                FieldAlignmentType = MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft;
            }
        }

        // Get Color/ Font

        Color color = getColor();    // default

        if( (ID != null) &&!isForm ) {
            ;    // link color/underline handeled in PrintElement classes
        } else if( (item.getAD_PrintColor_ID() != 0) && (m_printColor.getID() != item.getAD_PrintColor_ID())) {
            MPrintColor c = MPrintColor.get( getCtx(),item.getAD_PrintColor_ID());

            if( c.getColor() != null ) {
                color = c.getColor();
            }
        }

        Font font = m_printFont.getFont();    // default

        if( (item.getAD_PrintFont_ID() != 0) && (m_printFont.getID() != item.getAD_PrintFont_ID())) {
            MPrintFont f = MPrintFont.get( item.getAD_PrintFont_ID());

            if( f.getFont() != null ) {
                font = f.getFont();
            }
        }

        // Create String, HTML or Location

        PrintElement e = null;

        if( data.getDisplayType() == DisplayType.Location ) {
            e = new LocationElement( m_printCtx,(( KeyNamePair )ID ).getKey(),font,color );
            e.layout( maxWidth,item.getMaxHeight(),item.isHeightOneLine(),FieldAlignmentType );
        } else {
            if( HTMLElement.isHTML( stringContent )) {
                e = new HTMLElement( stringContent );
            } else {
                e = new StringElement( content,font,color,isForm
                        ?null
                        :ID,label,labelSuffix );
            }

            e.layout( maxWidth,item.getMaxHeight(),item.isHeightOneLine(),FieldAlignmentType );
        }

        return e;
    }    // createFieldElement

    /**
     * Descripci�n de M�todo
     *
     *
     * @param item
     *
     * @return
     */

    private PrintElement createBoxElement( MPrintFormatItem item ) {
        Color color = getColor();    // default

        if( (item.getAD_PrintColor_ID() != 0) && (m_printColor.getID() != item.getAD_PrintColor_ID())) {
            MPrintColor c = MPrintColor.get( getCtx(),item.getAD_PrintColor_ID());

            if( c.getColor() != null ) {
                color = c.getColor();
            }
        }

        return new BoxElement( item,color );
    }    // createBoxElement

    /**
     * Descripci�n de M�todo
     *
     *
     * @param item
     *
     * @return
     */

    private PrintElement createImageElement( MPrintFormatItem item ) {
        Object obj = m_data.getNode( new Integer( item.getAD_Column_ID()));

        if( obj == null ) {
            return null;
        } else if( obj instanceof PrintDataElement ) {
            ;
        } else {
            log.log( Level.SEVERE,"Element not PrintDataElement " + obj.getClass());

            return null;
        }

        PrintDataElement data = ( PrintDataElement )obj;

        if( data.isNull() && item.isSuppressNull()) {
            return null;
        }

        String url = data.getValueDisplay( m_format.getLanguage());

        if( ( (url == null) || (url.length() == 0) ) ) {
            if( item.isSuppressNull()) {
                return null;
            } else {    // should create an empty area
                return null;
            }
        }

        ImageElement element = new ImageElement( url );

        return element;
    }    // createImageElement

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public Color getColor() {
        if( m_printColor == null ) {
            return Color.BLACK;
        }

        return m_printColor.getColor();
    }    // getColor

    /**
     * Descripci�n de M�todo
     *
     *
     * @param format
     * @param printData
     * @param xOffset
     *
     * @return
     */

    private PrintElement layoutTable( MPrintFormat format,PrintData printData,int xOffset ) {
        log.info( format.getName() + " - " + printData.getName());

        MPrintTableFormat tf = format.getTableFormat();

        // Initial Values

        HashMap    rowColFont = new HashMap();
        MPrintFont printFont  = MPrintFont.get( format.getAD_PrintFont_ID());

        rowColFont.put( new Point( TableElement.ALL,TableElement.ALL ),printFont.getFont());
        tf.setStandard_Font( printFont.getFont());
        rowColFont.put( new Point( TableElement.HEADER_ROW,TableElement.ALL ),tf.getHeader_Font());

        //

        HashMap     rowColColor = new HashMap();
        MPrintColor printColor  = MPrintColor.get( getCtx(),format.getAD_PrintColor_ID());

        rowColColor.put( new Point( TableElement.ALL,TableElement.ALL ),printColor.getColor());
        rowColColor.put( new Point( TableElement.HEADER_ROW,TableElement.ALL ),tf.getHeaderFG_Color());

        //

        HashMap rowColBackground = new HashMap();

        rowColBackground.put( new Point( TableElement.HEADER_ROW,TableElement.ALL ),tf.getHeaderBG_Color());

        // Sizes

        boolean   multiLineHeader = false;
        int       pageNoStart     = m_pageNo;
        int       repeatedColumns = 1;
        Rectangle firstPage       = new Rectangle( m_content );

        firstPage.x     += xOffset;
        firstPage.width -= xOffset;

        int yOffset = ( int )m_position[ AREA_CONTENT ].y - m_content.y;

        firstPage.y      += yOffset;
        firstPage.height -= yOffset;

        Rectangle nextPages = new Rectangle( m_content );

        nextPages.x     += xOffset;
        nextPages.width -= xOffset;

        // Column count

        int columnCount = 0;

        for( int c = 0;c < format.getItemCount();c++ ) {
            if( format.getItem( c ).isPrinted()) {
                columnCount++;
            }
        }

        // System.out.println("Cols=" + cols);

        // Header & Column Setup

        ValueNamePair[] columnHeader        = new ValueNamePair[ columnCount ];
        int[]           columnMaxWidth      = new int[ columnCount ];
        int[]           columnMaxHeight     = new int[ columnCount ];
        boolean[]       fixedWidth          = new boolean[ columnCount ];
        String[]        columnJustification = new String[ columnCount ];
        HashMap         additionalLines     = new HashMap();
        int             col                 = 0;

        for( int c = 0;c < format.getItemCount();c++ ) {
            MPrintFormatItem item = format.getItem( c );

            if( item.isPrinted()) {
                if( item.isNextLine() && (item.getBelowColumn() != 0) ) {
                    additionalLines.put( new Integer( col ),new Integer( item.getBelowColumn() - 1 ));

                    if( !item.isSuppressNull()) {
                        item.setIsSuppressNull( true );    // display size will be set to 0 in TableElement
                        item.save();
                    }
                }

                columnHeader[ col ] = new ValueNamePair( item.getColumnName(),item.getPrintName( format.getLanguage()));
                columnMaxWidth[ col ] = item.getMaxWidth();
                fixedWidth[ col ]     = ( (columnMaxWidth[ col ] != 0) && item.isFixedWidth());

                if( item.isSuppressNull()) {
                    if( columnMaxWidth[ col ] == 0 ) {
                        columnMaxWidth[ col ] = -1;    // indication suppress if Null
                    } else {
                        columnMaxWidth[ col ] *= -1;
                    }
                }

                columnMaxHeight[ col ] = item.getMaxHeight();

                if( item.isHeightOneLine()) {
                    columnMaxHeight[ col ] = -1;
                }

                columnJustification[ col ] = item.getFieldAlignmentType();

                if( (columnJustification[ col ] == null) || columnJustification[ col ].equals( MPrintFormatItem.FIELDALIGNMENTTYPE_Default )) {
                    columnJustification[ col ] = MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft;    // when generated sets correct alignment
                }

                // Column Fonts

                if( (item.getAD_PrintFont_ID() != 0) && (item.getAD_PrintFont_ID() != format.getAD_PrintFont_ID())) {
                    MPrintFont font = MPrintFont.get( item.getAD_PrintFont_ID());

                    rowColFont.put( new Point( TableElement.ALL,col ),font.getFont());
                }

                if( (item.getAD_PrintColor_ID() != 0) && (item.getAD_PrintColor_ID() != format.getAD_PrintColor_ID())) {
                    MPrintColor color = MPrintColor.get( getCtx(),item.getAD_PrintColor_ID());

                    rowColColor.put( new Point( TableElement.ALL,col ),color.getColor());
                }

                //

                col++;
            }
        }

        // The Data

        int rows = printData.getRowCount();

        // System.out.println("Rows=" + rows);

        Object[][]    data         = new Object[ rows ][ columnCount ];
        KeyNamePair[] pk           = new KeyNamePair[ rows ];
        String        pkColumnName = null;
        ArrayList     functionRows = new ArrayList();
        ArrayList     pageBreak    = new ArrayList();

        // for all rows

        for( int row = 0;row < rows;row++ ) {

            // System.out.println("row=" + row);

            printData.setRowIndex( row );

            if( printData.isFunctionRow()) {
                functionRows.add( new Integer( row ));
                rowColFont.put( new Point( row,TableElement.ALL ),tf.getFunct_Font());
                rowColColor.put( new Point( row,TableElement.ALL ),tf.getFunctFG_Color());
                rowColBackground.put( new Point( row,TableElement.ALL ),tf.getFunctBG_Color());

                if( printData.isPageBreak()) {
                    pageBreak.add( new Integer( row ));
                    log.finer( "PageBreak row=" + row );
                }
            }

            // Summary/Line Levels for Finanial Reports

            else {
            	int levelNo = printData.getLineLevelNo();
            	boolean isBold = printData.getLineIsBold();

            	if (levelNo != 0)
            	{
            		if (levelNo < 0)
            			levelNo = -levelNo;
            		Font base = printFont.getFont();
            		if (levelNo == 1)
            		{
            			int level1font=Font.ITALIC;
            			if(isBold==true)
            				level1font|=Font.BOLD;

            			rowColFont.put(new Point(row, TableElement.ALL), new Font (base.getName(),
            					level1font, base.getSize()-levelNo));
            		}
            		else if (levelNo == 2)
            		{
            			int level2font=Font.PLAIN;
            			if(isBold==true)
            				level2font=Font.BOLD;

            			rowColFont.put(new Point(row, TableElement.ALL), new Font (base.getName(),
            					level2font, base.getSize()-levelNo));
            		}
            	}
            	else if (isBold==true)
            	{
            		Font base = printFont.getFont();
            		rowColFont.put(new Point(row, TableElement.ALL), new Font (base.getName(),
            				Font.BOLD, base.getSize()));
            	}
            }

            // for all columns

            col = 0;

            for( int c = 0;c < format.getItemCount();c++ ) {
                MPrintFormatItem item        = format.getItem( c );
                Object           dataElement = null;

                if( item.isPrinted() && (item.getAD_Column_ID() > 0) )    // Text Columns
                {
                    if( item.isTypePrintFormat()) {}
                    else if( item.isTypeImage()) {
                        if( item.isImageField()) {
                            Object obj = printData.getNode( new Integer( item.getAD_Column_ID()));

                            if( obj == null ) {
                                ;
                            } else if( obj instanceof PrintDataElement ) {
                                PrintDataElement pde = ( PrintDataElement )obj;

                                data[ row ][ col ] = new ImageElement(( String )pde.getValue());
                            }
                        } else if( item.isImageIsAttached()) {
                            data[ row ][ col ] = new ImageElement( item.getID());
                        } else {
                            data[ row ][ col ] = new ImageElement( item.getImageURL());
                        }
                    } else {
                        Object obj = printData.getNode( new Integer( item.getAD_Column_ID()));

                        if( obj == null ) {
                            ;
                        } else if( obj instanceof PrintDataElement ) {
                            PrintDataElement pde = ( PrintDataElement )obj;

                            if( pde.isYesNo()) {
                                dataElement = pde.getValue();
                            }
                            else if (pde.isID()) {
                            	dataElement = pde.getValue();
                            	try {
                            		// Verificar si se ha redefinido el valor a imprimir
                            		if (item.getOverrideValue()!=null && item.getOverrideValue().length()>0)
                            			dataElement = new KeyNamePair(Integer.parseInt(pde.getValueKey()), item.getOverrideValue());
                            	}
                            	catch (Exception e) { e.printStackTrace(); }
                            }
                            else {
                                dataElement = pde.getValueDisplay( format.getLanguage());
                        		// Verificar si se ha redefinido el valor a imprimir
                        		if (item.getOverrideValue()!=null && item.getOverrideValue().length()>0)
                        			dataElement = item.getOverrideValue();
                            }
                        } else {
                            log.log( Level.SEVERE,"Element not PrintDataElement " + obj.getClass());
                        }

                        // System.out.println("  row=" + row + ",col=" + col + " - " + item.getAD_Column_ID() + " => " + dataElement);

                        data[ row ][ col ] = dataElement;
                    }

                    col++;
                }                // printed
            }                    // for all columns

            PrintDataElement pde = printData.getPKey();

            if( pde != null )    // for FunctionRows
            {
                pk[ row ] = ( KeyNamePair )pde.getValue();

                if( pkColumnName == null ) {
                    pkColumnName = pde.getColumnName();
                }
            }

            // else
            // System.out.println("No PK " + printData);

        }                        // for all rows

        //

        TableElement table = new TableElement( columnHeader,columnMaxWidth,columnMaxHeight,columnJustification,fixedWidth,functionRows,multiLineHeader,data,pk,pkColumnName,pageNoStart,firstPage,nextPages,repeatedColumns,additionalLines,rowColFont,rowColColor,rowColBackground,tf,pageBreak );

        table.layout( 0,0,false,MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft );

        if( m_tableElement == null ) {
            m_tableElement = table;
        }

        return table;
    }    // layoutTable

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    private PrintElement layoutParameter() {
        if( (m_query == null) ||!m_query.isActive()) {
            return null;
        }

        //

        ParameterElement pe = new ParameterElement( m_query,m_printCtx,m_format.getTableFormat());

        pe.layout( 0,0,false,null );

        return pe;
    }    // layoutParameter

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public int getNumberOfPages() {
        return m_pages.size();
    }    // getNumberOfPages

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pageIndex
     *
     * @return
     *
     * @throws IndexOutOfBoundsException
     */

    public PageFormat getPageFormat( int pageIndex ) throws IndexOutOfBoundsException {
        if( !havePage( pageIndex )) {
            throw new IndexOutOfBoundsException( "No page index=" + pageIndex );
        }

        return getPageFormat();
    }    // getPageFormat

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pageIndex
     *
     * @return
     *
     * @throws IndexOutOfBoundsException
     */

    public Printable getPrintable( int pageIndex ) throws IndexOutOfBoundsException {
        if( !havePage( pageIndex )) {
            throw new IndexOutOfBoundsException( "No page index=" + pageIndex );
        }

        return this;
    }    // getPrintable

    /**
     * Descripci�n de M�todo
     *
     *
     * @param graphics
     * @param pageFormat
     * @param pageIndex
     *
     * @return
     *
     * @throws PrinterException
     */

    public int print( Graphics graphics,PageFormat pageFormat,int pageIndex ) throws PrinterException {
        if( !havePage( pageIndex )) {
            return Printable.NO_SUCH_PAGE;
        }

        //

        Rectangle r = new Rectangle( 0,0,( int )getPaper().getWidth( true ),( int )getPaper().getHeight( true ));
        Page page = getPage( pageIndex + 1 );

        //
        // log.fine("#" + m_id, "PageIndex=" + pageIndex + ", Copy=" + m_isCopy);

        page.paint(( Graphics2D )graphics,r,false,m_isCopy );    // sets context
        getHeaderFooter().paint(( Graphics2D )graphics,r,false );

        //

        return Printable.PAGE_EXISTS;
    }    // print

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pageIndex
     *
     * @return
     */

    private boolean havePage( int pageIndex ) {
        if( (pageIndex < 0) || (pageIndex >= getNumberOfPages())) {
            return false;
        }

        return true;
    }    // havePage

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public boolean isCopy() {
        return m_isCopy;
    }    // isCopy

    /**
     * Descripci�n de M�todo
     *
     *
     * @param isCopy
     */

    public void setCopy( boolean isCopy ) {
        m_isCopy = isCopy;
    }    // setCopy

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public DocFlavor getDocFlavor() {
        return DocFlavor.SERVICE_FORMATTED.PAGEABLE;
    }    // getDocFlavor

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     *
     * @throws IOException
     */

    public Object getPrintData() throws IOException {
        return this;
    }    // getPrintData

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public DocAttributeSet getAttributes() {
        return null;
    }    // getAttributes

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     *
     * @throws IOException
     */

    public Reader getReaderForText() throws IOException {
        return null;
    }    // getReaderForText

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     *
     * @throws IOException
     */

    public InputStream getStreamForBytes() throws IOException {
        return null;
    }    // getStreamForBytes
    
}    // LayoutEngine



/*
 *  @(#)LayoutEngine.java   12.10.07
 * 
 *  Fin del fichero LayoutEngine.java
 *  
 *  Versión 2.2
 *
 */
