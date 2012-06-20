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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.compiere.swing.CPanel;
import org.openXpertya.apps.AEnv;
import org.openXpertya.model.MQuery;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ViewDesign extends CPanel implements MouseListener,MouseMotionListener,ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param layout
     * @param rd
     * @param vd
     */

    public ViewDesign( LayoutDesign layout,ReportDesign rd,ViewerDesign vd ) {
        m_layout   = layout;
        deSelected = null;
        this.addMouseListener( this );
        this.addMouseMotionListener( this );
        m_reportDesign = rd;
        m_viewerDesign = vd;
        mousePage      = 1;
        deSelectedPage = 0;
        m_printFormat  = m_reportDesign.getPrintFormat();
    }    // ViewDesign

    /** Descripción de Campos */

    private LayoutDesign m_layout;

    /** Descripción de Campos */

    private ReportDesign m_reportDesign;

    /** Descripción de Campos */

    private ViewerDesign m_viewerDesign;

    /** Descripción de Campos */

    private int printFormatItemID;

    /** Descripción de Campos */

    private InterfaceDesign deSelected;

    /** Descripción de Campos */

    private InterfaceDesign copySelected;

    /** Descripción de Campos */

    private InterfaceDesign deMouseOver;

    /** Descripción de Campos */

    private JPopupMenu popMenuBar = new JPopupMenu();

    /** Descripción de Campos */

    private boolean leftDrag;

    /** Descripción de Campos */

    private boolean notFormisDragged = false;

    /** Descripción de Campos */

    private boolean notFormChangeOrder = false;

    /** Descripción de Campos */

    private boolean isUsed = false;

    /** Descripción de Campos */

    private int idNotForm = 0;

    /** Descripción de Campos */

    private int clickXForm;

    /** Descripción de Campos */

    private int difXForm;

    /** Descripción de Campos */

    private int newWidth = 0;

    /** Descripción de Campos */

    private int itemIDPressed = 0;

    /** Descripción de Campos */

    private int itemIDReleased = 0;

    /** Descripción de Campos */

    private boolean notFormItemMoved = false;

    /** Descripción de Campos */

    private int clickX;

    /** Descripción de Campos */

    private int clickY;

    /** Descripción de Campos */

    private int mousePage;

    /** Descripción de Campos */

    private int deSelectedPage;

    /** Descripción de Campos */

    private Rectangle bounds;

    /** Descripción de Campos */

    private int m_zoomLevel = 0;

    /** Descripción de Campos */

    public static final String[] ZOOM_OPTIONS = new String[]{ "100%","75%","50%" };

    /** Descripción de Campos */

    public static int MARGIN = 5;

    /** Descripción de Campos */

    private static Color COLOR_BACKGROUND = Color.lightGray;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ViewDesign.class );

    /** Descripción de Campos */

    final static BasicStroke Stroke = new BasicStroke( 2.0f );

    /** Descripción de Campos */

    final static float dash[] = { 5.0f };

    /** Descripción de Campos */

    final static BasicStroke dashed = new BasicStroke( 1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f,dash,0.0f );

    /** Descripción de Campos */

    private MPrintFormat m_printFormat;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getMinimumSize() {
        return getMaximumSize();
    }    // getMinimumSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getMaximumSize() {
        return new Dimension( getPaperWidth() + ( 2 * MARGIN ),( getPaperHeight() + MARGIN ) * getPageCount() + MARGIN );
    }    // getMaximumSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getPreferredSize() {
        return getMaximumSize();
    }    // getPreferredSize

    /**
     * Descripción de Método
     *
     *
     * @param g
     */

    public void paintComponent( Graphics g ) {

        // log.fine( "View.paintComponent", g.getClip());

        Graphics2D g2D = ( Graphics2D )g;

        bounds = g2D.getClipBounds();

        //

        g2D.setColor( COLOR_BACKGROUND );
        g2D.fillRect( bounds.x,bounds.y,bounds.width,bounds.height );

        // for all pages

        for( int page = 0;page < m_layout.getPages().size();page++ ) {
            Rectangle pageRectangle = getRectangleOfPage( page + 1 );

            if( bounds.intersects( pageRectangle )) {
                PageDesign p = ( PageDesign )m_layout.getPages().get( page );

                p.paint( g2D,pageRectangle,true,false );    // sets context
                g2D.setColor( Color.BLACK );
                g2D.setStroke( Stroke );

                if( m_printFormat.isForm()) {
                    Rectangle area = getDimensionHeaderFooter( p,"area" );

                    g2D.setColor( Color.BLUE );
                    g2D.drawLine( area.x,area.y,area.width,area.y );
                    g2D.drawLine( area.x,area.height,area.width,area.height );

                    Rectangle hf = getDimensionHeaderFooter( p,"headerfooter" );

                    g2D.setStroke( dashed );
                    g2D.drawRect( hf.x,hf.y,hf.width,hf.height );

                    HeaderFooterDesign hfd = ( HeaderFooterDesign )m_layout.getHeaderFooterDesign();

                    hfd.paint( g2D,pageRectangle,true );
                } else {
                    g2D.setColor( Color.BLUE );

                    Rectangle area = getDimensionHeaderFooter( p,"area" );
                    Rectangle hf   = getDimensionHeaderFooter( p,"headerfooter" );

                    g2D.setStroke( dashed );
                    g2D.drawRect( hf.x,area.y,hf.width,area.height - area.y );

                    TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

                    tde.setMargin( MARGIN );
                }
            }    // paint page
        }        // for all pages
    }            // paintComponent

    /**
     * Descripción de Método
     *
     *
     * @param level
     */

    public void setZoomLevel( int level ) {
        m_zoomLevel = level;
    }    // setZoomLevel

    /**
     * Descripción de Método
     *
     *
     * @param levelString
     */

    public void setZoomLevel( String levelString ) {
        for( int i = 0;i < ZOOM_OPTIONS.length;i++ ) {
            if( ZOOM_OPTIONS[ i ].equals( levelString )) {
                m_zoomLevel = i;

                break;
            }
        }
    }    // setZoomLevel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getZoomLevel() {
        return m_zoomLevel;
    }    // getZoomLevel

    /**
     * Descripción de Método
     *
     *
     * @param pageNo
     *
     * @return
     */

    public Rectangle getRectangleOfPage( int pageNo ) {
        int y = MARGIN + (( pageNo - 1 ) * ( getPaperHeight() + MARGIN ));

        return new Rectangle( MARGIN,y,getPaperWidth(),getPaperHeight());
    }    // getRectangleOfPage

    /**
     * Descripción de Método
     *
     *
     * @param p
     *
     * @return
     */

    public float getPageNoAt( Point p ) {
        float y          = p.y;
        float pageHeight = getPaperHeight() + MARGIN;

        return 1f + ( y / pageHeight );
    }    // getPageAt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPageCount() {
        return m_layout.getPages().size();
    }    // getPageCount

    /**
     * Descripción de Método
     *
     *
     * @param pageNo
     *
     * @return
     */

    public String getPageInfo( int pageNo ) {
        return m_layout.getPageInfo( pageNo );
    }    // getPageInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPageInfoMax() {
        return m_layout.getPageInfoMax();
    }    // getPageInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CPaper getPaper() {
        return m_layout.getPaper();
    }    // getPaper

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPaperHeight() {
        return( int )m_layout.getPaper().getHeight( true );
    }    // getPaperHeight

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPaperWidth() {
        return( int )m_layout.getPaper().getWidth( true );
    }    // getPaperHeight

    /**
     * Descripción de Método
     *
     *
     * @param absolutePoint
     *
     * @return
     */

    public MQuery getDrillDown( Point absolutePoint ) {
        int pageNo = ( int )getPageNoAt( absolutePoint );

        // Rectangle pageRectangle = getRectangleOfPage(pageNo);

        String sql = "SELECT margintop,marginbottom,marginleft,marginright FROM ad_printpaper where ad_printpaper_id =" + "(SELECT ad_printpaper_id from ad_printformat where ad_printformat_id = " + m_printFormat.getAD_PrintFormat_ID() + ")";
        PreparedStatement pstmt = DB.prepareStatement( sql );
        Rectangle         pageRectangle;

        try {
            ResultSet rs = pstmt.executeQuery();

            rs.next();
            pageRectangle = new Rectangle(( MARGIN - 1 ) + rs.getInt( 3 ),rs.getInt( 1 ) + MARGIN,getPaperWidth() - rs.getInt( 4 ) - rs.getInt( 3 ) - 1,getPaperHeight() - rs.getInt( 2 ) - rs.getInt( 1 ));
        } catch( Exception e ) {
            pageRectangle = getRectangleOfPage( pageNo );
        }

        Point relativePoint = new Point( absolutePoint.x - pageRectangle.x,absolutePoint.y - pageRectangle.y );
        PageDesign page = ( PageDesign )m_layout.getPages().get( pageNo - 1 );

        //

        log.config( "Relative=" + relativePoint + ", " + page );

        // log.config("AbsolutePoint=" + absolutePoint + ", PageNo=" + pageNo + ", pageRectangle=" + pageRectangle);

        MQuery retValue = page.getDrillDown( relativePoint );

        if( retValue == null ) {
            retValue = m_layout.getHeaderFooterDesign().getDrillDown( relativePoint );
        }

        return retValue;
    }    // getDrillDown

    /**
     * Descripción de Método
     *
     *
     * @param absolutePoint
     *
     * @return
     */

    public MQuery getDrillAcross( Point absolutePoint ) {
        int       pageNo        = ( int )getPageNoAt( absolutePoint );
        Rectangle pageRectangle = getRectangleOfPage( pageNo );
        Point     relativePoint = new Point( absolutePoint.x - pageRectangle.x,absolutePoint.y - pageRectangle.y );
        PageDesign page = ( PageDesign )m_layout.getPages().get( pageNo - 1 );

        //

        log.config( "Relative=" + relativePoint + ", " + page );

        // log.config("AbsolutePoint=" + absolutePoint + ", PageNo=" + pageNo + ", pageRectangle=" + pageRectangle);

        return page.getDrillAcross( relativePoint );
    }    // getDrillAcross

//      MouseListener

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mousePressed( MouseEvent e ) {
        if( m_printFormat.isForm()) {
            boolean header = true;

            if( e.getClickCount() == 1 ) {
                if( deSelected != null ) {
                    deSelected.setSelected( false );
                    deSelectedPage = 0;
                }

                Rectangle pageRectangle = getRectangleOfPage( mousePage );

                if( bounds.intersects( pageRectangle )) {
                    PageDesign p;

                    if( mousePage > getPageCount()) {
                        p = ( PageDesign )m_layout.getPages().get( 1 );
                        repaint();
                    } else {
                        p = ( PageDesign )m_layout.getPages().get( mousePage - 1 );
                    }

                    deSelected = ( InterfaceDesign )p.getInterfaceDesign( e.getX(),e.getY());

                    if( deSelected != null ) {
                        deSelectedPage = mousePage;

                        if( !deSelected.isSelected()) {
                            deSelected.setSelected( true );
                            copySelected = deSelected;
                            this.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ));
                            header = false;
                        }

                        clickX = e.getX();
                        clickY = e.getY();

                        if( deSelected.isCornerClicked( e.getX(),e.getY())) {
                            int cornerNumber = deSelected.getCornerClicked();

                            if( (cornerNumber == 2) || (cornerNumber == 3) ) {
                                this.setCursor( Cursor.getPredefinedCursor( Cursor.SW_RESIZE_CURSOR ));
                            } else {
                                this.setCursor( Cursor.getPredefinedCursor( Cursor.SE_RESIZE_CURSOR ));
                            }

                            header = false;
                        }
                    }
                }    // paint page

                if( header && (mousePage == 1) ) {
                    HeaderFooterDesign hfd = ( HeaderFooterDesign )m_layout.getHeaderFooterDesign();

                    deSelected = ( InterfaceDesign )hfd.getInterfaceDesign( e.getX(),e.getY());

                    if( deSelected != null ) {
                        deSelectedPage = mousePage;

                        if( !deSelected.isSelected()) {
                            copySelected = deSelected;
                            deSelected.setSelected( true );
                            this.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ));
                        }

                        clickX = e.getX();
                        clickY = e.getY();

                        if( deSelected.isCornerClicked( e.getX(),e.getY())) {
                            int cornerNumber = deSelected.getCornerClicked();

                            if( (cornerNumber == 2) || (cornerNumber == 3) ) {
                                this.setCursor( Cursor.getPredefinedCursor( Cursor.SW_RESIZE_CURSOR ));
                            } else {
                                this.setCursor( Cursor.getPredefinedCursor( Cursor.SE_RESIZE_CURSOR ));
                            }
                        }
                    }
                }

                m_viewerDesign.updateToolBar( deSelected );
                repaint();
            }
        } else {
            TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();
            PageDesign p = ( PageDesign )m_layout.getPages().get(( int )getPageNoAt( new Point( e.getX(),e.getY())) - 1 );
            Rectangle area   = getDimensionHeaderFooter( p,"area" );
            Rectangle hf     = getDimensionHeaderFooter( p,"headerfooter" );
            Rectangle bounds = new Rectangle( hf.x,area.y,3 + tde.getTableWidth(( int )getPageNoAt( new Point( e.getX(),e.getY()))),tde.getTableHeight(( int )getPageNoAt( new Point( e.getX(),e.getY()))));
            int yCalculated = e.getY();

            if( m_printFormat.isStandardHeaderFooter()) {
                yCalculated = yCalculated - 17;
            }

            if( bounds.contains( e.getX(),yCalculated )) {
                difXForm = 0;

                if( !tde.isOneSelected()) {
                    clickXForm = e.getX();
                    difXForm   = 0;
                } else {
                    if(( !tde.isLeftLine( e.getX())) && ( !tde.isRightLine( e.getX()))) {
                        clickXForm = e.getX();
                        difXForm   = 0;
                    } else {
                        if( tde.isLeftLine( e.getX())) {
                            clickXForm = e.getX();

                            for( int i = 1;i < 5;i++ ) {
                                if( tde.isLeftLine( e.getX() + i )) {
                                    clickXForm = e.getX() + i;
                                    difXForm   = i;
                                }
                            }
                        }

                        if( tde.isRightLine( e.getX())) {
                            clickXForm = e.getX();

                            for( int i = 1;i < 5;i++ ) {
                                if( tde.isRightLine( e.getX() - i )) {
                                    clickXForm = e.getX() - i;
                                    difXForm   = -i;
                                }
                            }
                        }
                    }
                }

                tde.nameElement( clickXForm,( int )getPageNoAt( new Point( e.getX(),e.getY())));
                repaint();
            } else {
                tde.notSelected();
                idNotForm = 0;
            }

            repaint();

            if( tde.isOneSelected()) {
                tde       = ( TableDesignElement )m_layout.getTableDesignElement();
                idNotForm = tde.getPrintFormatItemID();
                m_viewerDesign.cargarToolBarNotForm( tde.getFields());
                mousePage = tde.getSelectedPage();
                repaint();
            } else {
                if( SwingUtilities.isLeftMouseButton( e )) {
                    m_viewerDesign.vaciarToolBarNotForm();
                }
            }
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseReleased( MouseEvent e ) {
        if( m_printFormat.isForm()) {
            if( deSelected != null ) {
                PageDesign p = ( PageDesign )m_layout.getPages().get( deSelectedPage - 1 );
                Rectangle          area = getDimensionHeaderFooter( p,"area" );
                Rectangle          hf   = getDimensionHeaderFooter( p,"headerfooter" );
                HeaderFooterDesign hfd  = ( HeaderFooterDesign )m_layout.getHeaderFooterDesign();

                if( deSelected.isChangedSize()) {
                    deSelected.changedSize( MARGIN );
                } else {
                    boolean reseq = false;

                    if((( e.getX() - clickX ) != 0 || ( e.getY() - clickY ) != 0 )) {
                        deSelected.dragged( MARGIN );
                    }

                    if( (e.getY() <= area.y) && (e.getY() >= hf.y) ) {
                        reseq = deSelected.setHeaderFooter( "H" );
                    } else {
                        if( (e.getY() >= area.height) && (e.getY() <= hf.y + hf.height) ) {
                            reseq = deSelected.setHeaderFooter( "F" );
                        } else {
                            reseq = deSelected.setHeaderFooter( "C" );
                        }
                    }

                    if( reseq ) {
                        updateLayout();
                        p = ( PageDesign )m_layout.getPages().get( deSelectedPage - 1 );
                        hfd = ( HeaderFooterDesign )m_layout.getHeaderFooterDesign();

                        int seq = hfd.headerFooterSeq();

                        p.pageDesignSeq( seq );
                    }
                }

                updateLayout();
                clickX = 0;
                clickY = 0;
            }

            m_viewerDesign.updateToolBar( deSelected );
        } else {
            TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

            if( notFormisDragged ) {
                tde.setDimension( newWidth,leftDrag );
                notFormisDragged = false;
                updateLayout();
                newWidth   = 0;
                clickXForm = 0;
                difXForm   = 0;
                m_viewerDesign.cargarToolBarNotForm( tde.getFields());
                repaint();
            }

            if( notFormChangeOrder ) {
                notFormChangeOrder = false;
                notFormItemMoved   = false;
                itemIDReleased     = 0;
                updateLayout();

                // refreshToolBar();

            }
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseEntered( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseExited( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {
        if( m_printFormat.isForm()) {
            boolean header = true;
            int     changeSeqno;

            if( e.getClickCount() == 2 ) {
                if( deSelected != null ) {
                    for( int page = 0;page < m_layout.getPages().size();page++ ) {
                        PageDesign p = ( PageDesign )m_layout.getPages().get( page );
                        Rectangle area = getDimensionHeaderFooter( p,"area" );

                        // Rectangle hf = getDimensionHeaderFooter(p,"headerfooter");

                        if( (e.getY() >= area.y) && (e.getY() <= area.height) ) {
                            changeSeqno = p.modifySeqNo( deSelected.getPrintFormatItemID(),deSelected.getSeqNo());
                            m_viewerDesign.updateSeqNo( changeSeqno );
                            header = false;
                        }
                    }

                    if( header && (mousePage == 1) ) {
                        HeaderFooterDesign hfd = ( HeaderFooterDesign )m_layout.getHeaderFooterDesign();

                        changeSeqno = hfd.modifySeqNo( deSelected.getPrintFormatItemID(),deSelected.getSeqNo());

                        if( changeSeqno != 0 ) {
                            m_viewerDesign.updateSeqNo( changeSeqno );
                        }
                    }

                    m_viewerDesign.updateToolBar( deSelected );
                    updateLayout();
                }
            }

            if( SwingUtilities.isRightMouseButton( e )) {
                popMenuBar.removeAll();

                if( deSelected != null ) {
                    ArrayList items = deSelected.getMenuItems( this );

                    for( int nItem = 0;nItem < items.size();nItem++ ) {
                        popMenuBar.add(( JMenuItem )items.get( nItem ));
                    }

                    popMenuBar.show(( Component )e.getSource(),e.getX(),e.getY());
                }
            }
        } else {
            if( e.getClickCount() == 2 ) {
                if(( int )getPageNoAt( new Point( e.getX(),e.getY())) <= getPageCount()) {
                    TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

                    if( tde.isOneSelected() &&!tde.isLeftLine( e.getX()) &&!tde.isRightLine( e.getX())) {
                        tde.changeSeqNo();
                        updateLayout();
                        repaint();
                    }
                }
            }

            if( SwingUtilities.isRightMouseButton( e )) {
                popMenuBar.removeAll();

                TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

                if( tde.isOneSelected()) {
                    ArrayList items = tde.getMenuItems( this );

                    for( int nItem = 0;nItem < items.size();nItem++ ) {
                        popMenuBar.add(( JMenuItem )items.get( nItem ));
                    }

                    popMenuBar.show(( Component )e.getSource(),e.getX(),e.getY());
                } else {
                    JMenu  subMenu = new JMenu( "Elegir Formato Tabla" );
                    String sql     = "SELECT DISTINCT ad_printTableFormat_ID,name FROM ad_printTableFormat WHERE isActive='Y'";
                    PreparedStatement pstmt = DB.prepareStatement( sql );

                    try {
                        ResultSet rs = pstmt.executeQuery();
                        JMenuItem item;

                        while( rs.next()) {
                            item = new JMenuItem( rs.getString( 2 ));
                            item.addActionListener( this );
                            subMenu.add( item );
                        }
                    } catch( Exception ex ) {
                    }

                    subMenu.addActionListener( this );

                    JMenuItem tableFormat = new JMenuItem( "Cargar Herramientas Tabla" );

                    tableFormat.addActionListener( this );

                    // popMenuBar.add(subMenu);

                    popMenuBar.add( tableFormat );
                    popMenuBar.add( subMenu );
                    popMenuBar.addSeparator();

                    JMenuItem newTableFormat = new JMenuItem( "Crear Nuevo Formato Tabla" );

                    newTableFormat.addActionListener( this );
                    popMenuBar.add( newTableFormat );
                    popMenuBar.show(( Component )e.getSource(),e.getX(),e.getY());
                }
            }
        }
    }

//      MouseMotionListener

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseDragged( MouseEvent e ) {
        if( m_printFormat.isForm()) {
            if( deSelected != null ) {
                if( SwingUtilities.isLeftMouseButton( e )) {
                    if( !deSelected.isDragged()) {
                        clickX = e.getX();
                        clickY = e.getY();
                    }

                    deSelected.Changes( e.getX() - clickX,e.getY() - clickY );
                    repaint();
                }
            }

            Point pt = new Point( e.getX(),e.getY());

            if(( int )getPageNoAt( pt ) != mousePage ) {
                mousePage = ( int )getPageNoAt( pt );

                if( (mousePage == 1) || (( mousePage == deSelectedPage ) && ( deSelectedPage != 0 ))) {
                    updateLayout();
                }
            }
        } else {
            TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

            if( tde.isOneSelected()) {
                if(( tde.isLeftLine( clickXForm - difXForm )) || ( tde.isRightLine( clickXForm - difXForm ))) {
                    notFormisDragged = true;

                    if( tde.isLeftLine( clickXForm - difXForm )) {
                        leftDrag = true;
                    } else {
                        leftDrag = false;
                    }

                    if( notFormisDragged ) {
                        if( leftDrag ) {
                            if((( e.getX() - difXForm ) < ( tde.getLine( false,false ) - 20 )) && (( e.getX() - difXForm ) > ( tde.getLine( true,true ) + 20 ))) {
                                newWidth = e.getX() - ( clickXForm - difXForm );
                            } else {
                                if(( e.getX() - difXForm ) < ( tde.getLine( false,false ) - 20 )) {
                                    newWidth = ( tde.getLine( true,true ) + 20 ) - ( clickXForm - difXForm );
                                } else {
                                    newWidth = ( tde.getLine( false,false ) - 20 ) - ( clickXForm - difXForm );
                                }
                            }
                        } else {
                            if(( e.getX() - difXForm ) > ( tde.getLine( false,true ) + 20 )) {
                                newWidth = e.getX() - ( clickXForm - difXForm );
                            } else {
                                newWidth = ( tde.getLine( false,true ) + 20 ) - ( clickXForm - difXForm );
                            }
                        }

                        tde.setAddWidth( newWidth,leftDrag );
                    }

                    repaint();
                } else {

                    // boolean paint =false;

                    if( !notFormChangeOrder ) {
                        notFormChangeOrder = true;
                        itemIDPressed      = tde.getPrintFormatItemID();
                    }

                    PageDesign p = ( PageDesign )m_layout.getPages().get(( int )getPageNoAt( new Point( e.getX(),e.getY())) - 1 );
                    Rectangle area = getDimensionHeaderFooter( p,"area" );
                    Rectangle hf   = getDimensionHeaderFooter( p,"headerfooter" );
                    Rectangle bounds = new Rectangle( hf.x,area.y,3 + tde.getTableWidth(( int )getPageNoAt( new Point( e.getX(),e.getY()))),tde.getTableHeight(( int )getPageNoAt( new Point( e.getX(),e.getY()))));
                    int yCalculated = e.getY();

                    if( m_printFormat.isStandardHeaderFooter()) {
                        yCalculated = yCalculated - 17;
                    }

                    if( bounds.contains( e.getX(),yCalculated )) {
                        if( (e.getX() < ( tde.getLine( false,true ))) || (e.getX() > ( tde.getLine( false,false )))) {
                            tde.nameElement( e.getX(),tde.getSelectedPage());    // mousePage);

                            if(( itemIDReleased != tde.getPrintFormatItemID()) && ( tde.getPrintFormatItemID() != itemIDPressed )) {
                                itemIDReleased   = tde.getPrintFormatItemID();
                                notFormItemMoved = true;
                                tde.nameElement( clickXForm,tde.getSelectedPage());
                                tde.setColSelectedByID( itemIDPressed );
                            }
                        }

                        if( (itemIDReleased != 0) && notFormItemMoved ) {
                            MPrintFormatItem pfi1 = new MPrintFormatItem( Env.getCtx(),itemIDPressed,null );
                            MPrintFormatItem pfi2 = new MPrintFormatItem( Env.getCtx(),itemIDReleased,null );

                            if(( pfi1.getMaxWidth() * 2 ) < pfi2.getMaxWidth()) {
                                if(( e.getX() < ( tde.getLine( false,true ) - ( pfi2.getMaxWidth() - ( pfi1.getMaxWidth()) + 10 ))) || ( e.getX() > ( tde.getLine( false,false ) + ( pfi2.getMaxWidth() - ( pfi1.getMaxWidth() - 10 ))))) {
                                    tde.reSeqNo( itemIDPressed,itemIDReleased,this );
                                    itemIDReleased = 0;
                                }
                            } else {
                                if(( e.getX() < ( tde.getLine( false,true ) - ( pfi2.getMaxWidth() / 2 ))) || ( e.getX() > ( tde.getLine( false,false ) + ( pfi2.getMaxWidth() / 2 )))) {
                                    tde.reSeqNo( itemIDPressed,itemIDReleased,this );
                                    itemIDReleased = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseMoved( MouseEvent e ) {
        if( m_printFormat.isForm()) {
            this.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ));

            boolean header = true;
            Point   pt     = new Point( e.getX(),e.getY());

            if(( int )getPageNoAt( pt ) != mousePage ) {
                mousePage = ( int )getPageNoAt( pt );

                if( mousePage == 1 )    // || ((mousePage==deSelectedPage) && (deSelectedPage!=0)))
                {
                    updateLayout();
                }
            }

            if(( m_layout != null ) && ( m_layout.getPages().size() != 0 )) {
                Rectangle pageRectangle = getRectangleOfPage( mousePage );

                if(( bounds != null ) && ( pageRectangle != null ) && ( bounds.intersects( pageRectangle ))) {
                    PageDesign p;

                    if( mousePage > getPageCount()) {
                        if( deSelectedPage != 0 ) {
                            if( deSelectedPage > getPageCount()) {
                                p = ( PageDesign )m_layout.getPages().get(( mousePage - ( mousePage - getPageCount())) - 1 );
                            } else {
                                p = ( PageDesign )m_layout.getPages().get( deSelectedPage - 1 );
                            }
                        } else {
                            p = ( PageDesign )m_layout.getPages().get(( mousePage - ( mousePage - getPageCount())) - 1 );
                        }
                    } else {
                        p = ( PageDesign )m_layout.getPages().get( mousePage - 1 );
                    }

                    deMouseOver = p.getInterfaceDesign( e.getX(),e.getY());

                    if(( deMouseOver != null ) && ( !deMouseOver.isDragged())) {
                        if( deMouseOver.isClicked( e.getX(),e.getY())) {
                            this.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ));
                            header = false;
                        } else if( deMouseOver.isCornerClicked( e.getX(),e.getY()) && deSelected.isSelected()) {
                            header = false;

                            int cornerNumber = deSelected.getCornerClicked();

                            if( (cornerNumber == 2) || (cornerNumber == 3) ) {
                                this.setCursor( Cursor.getPredefinedCursor( Cursor.SW_RESIZE_CURSOR ));
                            } else {
                                this.setCursor( Cursor.getPredefinedCursor( Cursor.SE_RESIZE_CURSOR ));
                            }
                        }
                    }

                    repaint();
                }

                if( header && (mousePage == 1) ) {
                    HeaderFooterDesign hfd = ( HeaderFooterDesign )m_layout.getHeaderFooterDesign();

                    deMouseOver = hfd.getInterfaceDesign( e.getX(),e.getY());

                    if(( deMouseOver != null ) && ( !deMouseOver.isDragged())) {
                        if( deMouseOver.isClicked( e.getX(),e.getY())) {
                            this.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ));
                        } else if( deMouseOver.isCornerClicked( e.getX(),e.getY()) && deSelected.isSelected()) {
                            int cornerNumber = deSelected.getCornerClicked();

                            if( (cornerNumber == 2) || (cornerNumber == 3) ) {
                                this.setCursor( Cursor.getPredefinedCursor( Cursor.SW_RESIZE_CURSOR ));
                            } else {
                                this.setCursor( Cursor.getPredefinedCursor( Cursor.SE_RESIZE_CURSOR ));
                            }
                        }
                    }
                }

                repaint();
            }
        } else {
            TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

            if(( int )getPageNoAt( new Point( e.getX(),e.getY())) <= getPageCount()) {
                PageDesign p = ( PageDesign )m_layout.getPages().get(( int )getPageNoAt( new Point( e.getX(),e.getY())) - 1 );

                if( (tde.getTableWidth(( int )getPageNoAt( new Point( e.getX(),e.getY()))) != 0) && (tde.getTableHeight(( int )getPageNoAt( new Point( e.getX(),e.getY()))) != 0) ) {
                    if(( tde.isLeftLine( e.getX())) || ( tde.isRightLine( e.getX()))) {
                        int yCalculated = e.getY();

                        if( m_printFormat.isStandardHeaderFooter()) {
                            yCalculated = yCalculated - 17;
                        }

                        Rectangle area = getDimensionHeaderFooter( p,"area" );

                        if( (yCalculated < area.y + tde.getTableHeight(( int )getPageNoAt( new Point( e.getX(),e.getY())))) && (yCalculated >= area.y) ) {
                            if(( int )getPageNoAt( new Point( e.getX(),e.getY())) == tde.getSelectedPage() && bounds.contains( e.getX(),e.getY())) {
                                this.setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ));
                            }
                        } else {
                            this.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ));
                        }
                    } else {
                        this.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ));
                    }
                }
            }
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param toolBar
     */

    public void changeFormat( boolean toolBar ) {
        MPrintFormat pf   = ( MPrintFormat )m_layout.getPrintFormat();
        int          pfID = pf.getAD_PrintFormat_ID();

        pf = new MPrintFormat( Env.getCtx(),pfID,null );
        m_layout.setPrintFormat( pf,true );

        if( toolBar ) {
            TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

            m_viewerDesign.cargarToolBarTable( tde );
        }
    }

    /**
     * Descripción de Método
     *
     */

    public void updateLayout() {
        boolean header = true;
        int     pages  = getPageCount();
        int     col    = 0;
        int     page   = 0;

        if( !m_printFormat.isForm()) {
            TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

            col  = tde.getColSelected();
            page = tde.getSelectedPage();
        }

        m_layout.layout();

        if( pages != getPageCount()) {
            changePage( deSelected );
        } else {
            if( deSelected != null ) {
                printFormatItemID = copySelected.getPrintFormatItemID();

                PageDesign p;

                p = ( PageDesign )m_layout.getPages().get( deSelectedPage - 1 );
                deSelected = p.getInterfaceDesign( printFormatItemID );

                if( deSelected != null ) {
                    deSelected.setSelected( true );
                    copySelected = deSelected;
                    header       = false;
                }

                if( header && (mousePage == 1) ) {
                    HeaderFooterDesign hfd = ( HeaderFooterDesign )m_layout.getHeaderFooterDesign();

                    deSelected = hfd.getInterfaceDesign( printFormatItemID );

                    if( deSelected != null ) {
                        deSelected.setSelected( true );
                    }
                }

                if( deSelected == null ) {
                    changePage( copySelected );
                }
            }

            repaint();
        }

        if( page > 0 ) {
            TableDesignElement tdenew = ( TableDesignElement )m_layout.getTableDesignElement();

            tdenew.setColSelected( col,page );
        }

        repaint();

        if( m_printFormat.isForm()) {
            if( deSelected != null ) {
                refreshToolBar();
            }
        } else {
            TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

            if( tde.isOneSelected() &&!notFormChangeOrder ) {
                ;
            }

            refreshToolBar();

            if( notFormItemMoved ) {
                tde.setColSelectedByID( itemIDPressed );
                notFormItemMoved = false;
                updateLayout();
            }
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public InterfaceDesign getDeSelected() {
        return deSelected;
    }

    /**
     * Descripción de Método
     *
     *
     * @param id
     */

    public void changePage( InterfaceDesign id ) {
        deSelectedPage = 1;

        if( id != null ) {
            printFormatItemID = id.getPrintFormatItemID();

            PageDesign p;
            int        cont = 1;

            deSelected = null;

            while(( deSelected == null ) && ( cont <= m_layout.getPages().size())) {
                deSelectedPage = cont;
                p              = ( PageDesign )m_layout.getPages().get( deSelectedPage - 1 );
                deSelected = p.getInterfaceDesign( printFormatItemID );
                cont       = cont + 1;
            }

            copySelected = deSelected;

            if( deSelected != null ) {
                deSelected.setSelected( true );
            }
        }

        this.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ));
        updateLayout();
        m_viewerDesign.changePage( deSelectedPage );
        updateLayout();
    }

    /**
     * Descripción de Método
     *
     *
     * @param p
     * @param area
     *
     * @return
     */

    public Rectangle getDimensionHeaderFooter( PageDesign p,String area ) {
        Rectangle border = new Rectangle();
        String    sql    = "SELECT margintop,marginbottom,marginleft,marginright FROM ad_printpaper where ad_printpaper_id =" + "(SELECT ad_printpaper_id from ad_printformat where ad_printformat_id = " + m_printFormat.getAD_PrintFormat_ID() + ")";
        PreparedStatement pstmt = DB.prepareStatement( sql );

        try {
            ResultSet rs = pstmt.executeQuery();

            rs.next();

            if( area == "area" ) {
                if( m_printFormat.isStandardHeaderFooter()) {
                    if( p.getPageNo() > 1 ) {
                        border = new Rectangle( bounds.x,( rs.getInt( 1 ) + MARGIN ) + (( getPaperHeight() + MARGIN ) * ( p.getPageNo() - 1 )),bounds.width,( MARGIN + getPaperHeight() - rs.getInt( 2 )) + (( getPaperHeight() + MARGIN ) * ( p.getPageNo() - 1 )));
                    } else {
                        border = new Rectangle( bounds.x,rs.getInt( 1 ) + MARGIN,bounds.width,MARGIN + getPaperHeight() - rs.getInt( 2 ));
                    }
                } else {
                    if( p.getPageNo() > 1 ) {
                        border = new Rectangle( bounds.x,( MARGIN + m_printFormat.getHeaderMargin() + rs.getInt( 1 )) + (( getPaperHeight() + MARGIN ) * ( p.getPageNo() - 1 )),bounds.width,( MARGIN + getPaperHeight() - m_printFormat.getFooterMargin() - rs.getInt( 2 )) + (( getPaperHeight() + MARGIN ) * ( p.getPageNo() - 1 )));
                    } else {
                        border = new Rectangle( bounds.x,MARGIN + m_printFormat.getHeaderMargin() + rs.getInt( 1 ),bounds.width,MARGIN + getPaperHeight() - m_printFormat.getFooterMargin() - rs.getInt( 2 ));
                    }
                }
            }

            if( area == "headerfooter" ) {
                if( p.getPageNo() > 1 ) {
                    border = new Rectangle(( MARGIN - 1 ) + rs.getInt( 3 ),( rs.getInt( 1 ) + MARGIN ) + (( getPaperHeight() + MARGIN ) * ( p.getPageNo() - 1 )),getPaperWidth() - rs.getInt( 4 ) - rs.getInt( 3 ) - 1,getPaperHeight() - rs.getInt( 2 ) - rs.getInt( 1 ));
                } else {
                    border = new Rectangle(( MARGIN - 1 ) + rs.getInt( 3 ),rs.getInt( 1 ) + MARGIN,getPaperWidth() - rs.getInt( 4 ) - rs.getInt( 3 ) - 1,getPaperHeight() - rs.getInt( 2 ) - rs.getInt( 1 ));
                }
            }
        } catch( Exception e ) {
        }

        return border;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTableDesignElementItemID() {
        TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

        return tde.getPrintFormatItemID();
    }

    /**
     * Descripción de Método
     *
     */

    public void setColSelectedByItemID() {
        if( idNotForm != 0 ) {
            updateLayout();
            repaint();
            m_viewerDesign.actualizarNotForm( idNotForm );
            updateLayout();
            repaint();

            TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

            repaint();
            tde.setColSelectedByID( idNotForm );
            repaint();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param id
     */

    public void setColSelectedByItemID( int id ) {
        TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

        if( tde.isOneSelected()) {
            tde.setColSelectedByID( id );
            repaint();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // TODO Auto-generated method stub

        if( e.getActionCommand() == "Cargar Herramientas Tabla" ) {
            TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

            m_viewerDesign.cargarToolBarTable( tde );
        } else if( e.getActionCommand() == "Crear Nuevo Formato Tabla" ) {
            AEnv.zoom( 523,0 );
        } else {
            String sql = "SELECT DISTINCT ad_printTableFormat_ID,name FROM ad_printTableFormat WHERE isActive='Y'";
            PreparedStatement pstmt = DB.prepareStatement( sql );

            try {
                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    if( e.getActionCommand().equals( rs.getString( 2 ))) {
                        MPrintFormat pf = ( MPrintFormat )m_reportDesign.getPrintFormat();

                        pf.setAD_PrintTableFormat_ID( rs.getInt( 1 ));
                        pf.save();
                        changeFormat( true );
                    }
                }
            } catch( Exception ex ) {
            }
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTableFormatID() {
        TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

        return tde.getTableFormatID();
    }

    /**
     * Descripción de Método
     *
     */

    public void refreshToolBar() {
        if( !isUsed ) {
            if( m_printFormat.isForm()) {
                m_viewerDesign.cargarToolBar( deSelected );
            } else {
                TableDesignElement tde = ( TableDesignElement )m_layout.getTableDesignElement();

                m_viewerDesign.cargarToolBarNotForm( tde.getFields());
            }
        } else {
            isUsed = false;
        }
    }

    /**
     * Descripción de Método
     *
     */

    public void ChangeByToolBar() {
        isUsed = true;
    }
}    // ViewDesign



/*
 *  @(#)ViewDesign.java   02.07.07
 * 
 *  Fin del fichero ViewDesign.java
 *  
 *  Versión 2.2
 *
 */
