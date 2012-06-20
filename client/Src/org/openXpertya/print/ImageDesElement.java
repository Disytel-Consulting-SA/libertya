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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MAttachment;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.print.layout.PrintElement;
import org.openXpertya.util.CCache;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImageDesElement extends PrintElement implements InterfaceDesign,ActionListener {

    /** Descripción de Campos */

    private boolean isDragged;

    /** Descripción de Campos */

    private boolean isSelected;

    /** Descripción de Campos */

    private boolean isChangedSize;

    /** Descripción de Campos */

    private boolean isCalculateBounds;

    /** Descripción de Campos */

    private boolean isMoveOver;

    /** Descripción de Campos */

    private MPrintFormatItem pfItem;

    /** Descripción de Campos */

    private ViewDesign m_viewDesign;

    /** Descripción de Campos */

    private Rectangle bounds;

    /** Descripción de Campos */

    private Rectangle changes;

    /** Descripción de Campos */

    private CornerElement tpleft;

    /** Descripción de Campos */

    private CornerElement tpright;

    /** Descripción de Campos */

    private CornerElement dwleft;

    /** Descripción de Campos */

    private CornerElement dwright;

    /** Descripción de Campos */

    private CornerElement arrastrar;

    /** Descripción de Campos */

    private CornerElement linea;

    /** Descripción de Campos */

    private int m_cornerClicked;

    /** Descripción de Campos */

    private Image m_image = null;

    /** Descripción de Campos */

    public static Image IMAGE_TRUE = null;

    /** Descripción de Campos */

    public static Image IMAGE_FALSE = null;

    /** Descripción de Campos */

    public static Dimension IMAGE_SIZE = new Dimension( 10,10 );

    /** Descripción de Campos */

    public static ImageIcon ICON_TRUE = null;

    /** Descripción de Campos */

    public static ImageIcon ICON_FALSE = null;

    static {
        Toolkit tk  = Toolkit.getDefaultToolkit();
        URL     url = LayoutDesign.class.getResource( "true10.gif" );

        if( url != null ) {
            IMAGE_TRUE = tk.getImage( url );
        }

        url = LayoutDesign.class.getResource( "false10.gif" );

        if( url != null ) {
            IMAGE_FALSE = tk.getImage( url );
        }

        ICON_TRUE  = new ImageIcon( IMAGE_TRUE );
        ICON_FALSE = new ImageIcon( IMAGE_FALSE );
    }    // static init

    /**
     * Descripción de Método
     *
     *
     * @param imageURLString
     * @param AD_PrintFormatItem_ID
     *
     * @return
     */

    public static ImageDesElement get( String imageURLString,int AD_PrintFormatItem_ID ) {
        Object          key   = imageURLString;
        ImageDesElement image = ( ImageDesElement )s_cache.get( key );

        if( image == null ) {
            MPrintFormatItem item = new MPrintFormatItem( Env.getCtx(),AD_PrintFormatItem_ID,null );

            image = new ImageDesElement( imageURLString,item );
            s_cache.put( key,image );
        }

        return new ImageDesElement( image.getImage());
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param imageURL
     *
     * @return
     */

    public static ImageDesElement get( URL imageURL ) {
        Object          key   = imageURL;
        ImageDesElement image = ( ImageDesElement )s_cache.get( key );

        if( image == null ) {
            image = new ImageDesElement( imageURL );
            s_cache.put( key,image );
        }

        return new ImageDesElement( image.getImage());
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param AD_PrintFormatItem_ID
     *
     * @return
     */

    public static ImageDesElement get( int AD_PrintFormatItem_ID ) {
        Object          key   = new Integer( AD_PrintFormatItem_ID );
        ImageDesElement image = ( ImageDesElement )s_cache.get( key );

        if( image == null ) {
            MPrintFormatItem item = new MPrintFormatItem( Env.getCtx(),AD_PrintFormatItem_ID,null );

            image = new ImageDesElement( AD_PrintFormatItem_ID,item );
            s_cache.put( key,image );
        }

        return new ImageDesElement( image.getImage());
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "ImageElement",10,60 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param image
     */

    public ImageDesElement( Image image ) {
        m_image = image;

        if( m_image != null ) {
            log.fine( "Image=" + image );
        } else {
            log.log( Level.SEVERE,"Image is NULL" );
        }

        tpleft            = new CornerElement( CornerElement.TOPLEFT,this );
        tpright           = new CornerElement( CornerElement.TOPRIGHT,this );
        dwleft            = new CornerElement( CornerElement.DOWNLEFT,this );
        dwright           = new CornerElement( CornerElement.DOWNRIGHT,this );
        linea             = new CornerElement( 0,this );
        arrastrar         = new CornerElement( 0,this );
        isSelected        = false;
        isDragged         = false;
        isMoveOver        = false;
        isCalculateBounds = true;
        m_cornerClicked   = 0;
        m_viewDesign      = null;
        m_image           = image;
    }    // ImageDesElement

    /**
     * Constructor de la clase ...
     *
     *
     * @param imageURLstring
     * @param item
     */

    private ImageDesElement( String imageURLstring,MPrintFormatItem item ) {
        URL imageURL = getURL( imageURLstring );

        if( imageURL != null ) {
            m_image = Toolkit.getDefaultToolkit().getImage( imageURL );

            if( m_image != null ) {
                log.fine( "URL=" + imageURL );
            } else {
                log.log( Level.SEVERE,"Not loaded - URL=" + imageURL );
            }
        } else {
            log.log( Level.SEVERE,"Invalid URL=" + imageURLstring );
        }

        tpleft            = new CornerElement( CornerElement.TOPLEFT,this );
        tpright           = new CornerElement( CornerElement.TOPRIGHT,this );
        dwleft            = new CornerElement( CornerElement.DOWNLEFT,this );
        dwright           = new CornerElement( CornerElement.DOWNRIGHT,this );
        linea             = new CornerElement( 0,this );
        arrastrar         = new CornerElement( 0,this );
        isSelected        = false;
        isDragged         = false;
        isMoveOver        = false;
        isCalculateBounds = true;
        m_cornerClicked   = 0;
        m_viewDesign      = null;
        pfItem            = item;
    }    // ImageDesElement

    /**
     * Constructor de la clase ...
     *
     *
     * @param imageURL
     */

    private ImageDesElement( URL imageURL ) {
        if( imageURL != null ) {
            m_image = Toolkit.getDefaultToolkit().getImage( imageURL );

            if( m_image != null ) {
                log.fine( "URL=" + imageURL );
            } else {
                log.log( Level.SEVERE,"Not loaded - URL=" + imageURL );
            }
        } else {
            log.severe( "ImageURL is NULL" );
        }

        tpleft            = new CornerElement( CornerElement.TOPLEFT,this );
        tpright           = new CornerElement( CornerElement.TOPRIGHT,this );
        dwleft            = new CornerElement( CornerElement.DOWNLEFT,this );
        dwright           = new CornerElement( CornerElement.DOWNRIGHT,this );
        linea             = new CornerElement( 0,this );
        arrastrar         = new CornerElement( 0,this );
        isSelected        = false;
        isDragged         = false;
        isMoveOver        = false;
        isCalculateBounds = true;
        m_cornerClicked   = 0;
        m_viewDesign      = null;
    }    // ImageDesElement

    /**
     * Constructor de la clase ...
     *
     *
     * @param AD_PrintFormatItem_ID
     * @param item
     */

    public ImageDesElement( int AD_PrintFormatItem_ID,MPrintFormatItem item ) {
        loadAttachment( AD_PrintFormatItem_ID );
        tpleft            = new CornerElement( CornerElement.TOPLEFT,this );
        tpright           = new CornerElement( CornerElement.TOPRIGHT,this );
        dwleft            = new CornerElement( CornerElement.DOWNLEFT,this );
        dwright           = new CornerElement( CornerElement.DOWNRIGHT,this );
        linea             = new CornerElement( 0,this );
        arrastrar         = new CornerElement( 0,this );
        isSelected        = false;
        isDragged         = false;
        isMoveOver        = false;
        isCalculateBounds = true;
        m_cornerClicked   = 0;
        m_viewDesign      = null;
        pfItem            = item;
    }    // ImageDesElement

    /**
     * Descripción de Método
     *
     *
     * @param urlString
     *
     * @return
     */

    private URL getURL( String urlString ) {
        URL url = null;

        // not a URL - may be a resource

        if( urlString.indexOf( "://" ) == -1 ) {
            ClassLoader cl = getClass().getClassLoader();

            url = cl.getResource( urlString );

            if( url != null ) {
                return url;
            }

            log.log( Level.SEVERE,"Not found - " + urlString );

            return null;
        }

        // load URL

        try {
            url = new URL( urlString );
        } catch( MalformedURLException ex ) {
            log.log( Level.SEVERE,"getURL",ex );
        }

        return url;
    }    // getURL;

    /**
     * Descripción de Método
     *
     *
     * @param AD_PrintFormatItem_ID
     */

    private void loadAttachment( int AD_PrintFormatItem_ID ) {
        MAttachment attachment = MAttachment.get( Env.getCtx(),MPrintFormatItem.Table_ID,AD_PrintFormatItem_ID );

        if( attachment == null ) {
            log.log( Level.SEVERE,"No Attachment - AD_PrintFormatItem_ID=" + AD_PrintFormatItem_ID );

            return;
        }

        if( attachment.getEntryCount() != 1 ) {
            log.log( Level.SEVERE,"Need just 1 Attachment Entry = " + attachment.getEntryCount());

            return;
        }

        byte[] imageData = attachment.getEntryData( 0 );

        if( imageData != null ) {
            m_image = Toolkit.getDefaultToolkit().createImage( imageData );
        }

        if( m_image != null ) {
            log.fine( attachment.getEntryName( 0 ) + " - Size=" + imageData.length );
        } else {
            log.log( Level.SEVERE,attachment.getEntryName( 0 ) + " - not loaded (must be gif or jpg) - AD_PrintFormatItem_ID=" + AD_PrintFormatItem_ID );
        }
    }    // loadAttachment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean calculateSize() {
        p_width  = 0;
        p_height = 0;

        if( m_image == null ) {
            return true;
        }

        // we have an image

        waitForLoad( m_image );

        if( m_image != null ) {
            if(( pfItem == null ) || ( pfItem.getMaxWidth() == 0 )) {
                p_width = m_image.getWidth( this );
            } else {
                p_width = pfItem.getMaxWidth();
            }

            if(( pfItem == null ) || ( pfItem.getMaxHeight() == 0 )) {
                p_height = m_image.getHeight( this );
            } else {
                p_height = pfItem.getMaxHeight();
            }
        }

        return true;
    }    // calculateSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Image getImage() {
        return m_image;
    }    // getImage

    /**
     * Descripción de Método
     *
     *
     * @param g2D
     * @param pageNo
     * @param pageStart
     * @param ctx
     * @param isView
     */

    public void paint( Graphics2D g2D,int pageNo,Point2D pageStart,Properties ctx,boolean isView ) {
        if( m_image == null ) {
            return;
        }

        // Position

        Point2D.Double location = getAbsoluteLocation( pageStart );
        int            x        = ( int )location.x;

        if( MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight.equals( p_FieldAlignmentType )) {
            x += p_maxWidth - p_width;
        } else if( MPrintFormatItem.FIELDALIGNMENTTYPE_Center.equals( p_FieldAlignmentType )) {
            x += ( p_maxWidth - p_width ) / 2;
        }

        int y = ( int )location.y;

        //

        if( isCalculateBounds ) {
            bounds            = new Rectangle( x,y,( int )p_width,( int )p_height );
            changes           = new Rectangle( x,y,( int )p_width,( int )p_height );
            isCalculateBounds = false;
        }

        if( isSelected ) {
            tpleft.paint( g2D,( bounds.x - CornerElement.CORNERSIZE ) - 1,( bounds.y - CornerElement.CORNERSIZE ));
            tpright.paint( g2D,( int )(( bounds.x + bounds.width ) - 1 ),bounds.y - CornerElement.CORNERSIZE );
            dwleft.paint( g2D,( bounds.x - CornerElement.CORNERSIZE ) - 1,( int )( bounds.y + bounds.height ));
            dwright.paint( g2D,( int )(( bounds.x + bounds.width ) - 1 ),( int )( bounds.y + bounds.height ));
            linea.puntos( g2D,bounds.x - 1,bounds.y,bounds.width,bounds.height );

            if( isDragged ) {
                arrastrar.puntos( g2D,( changes.x - 1 ),changes.y,changes.width,changes.height );
            }
        } else {
            if( isMoveOver ) {
                linea.puntos( g2D,bounds.x - 1,bounds.y,bounds.width,bounds.height );
            }

            isMoveOver = false;
        }

        g2D.drawImage( m_image,x,y,bounds.width,bounds.height,this );
    }    // paint

    /**
     * Descripción de Método
     *
     *
     * @param MARGIN
     */

    public void setLocation( int MARGIN ) {
        Point p = new Point(( changes.x - bounds.x ),( changes.y - bounds.y ));

        if( pfItem.isRelativePosition()) {

            // if((pfItem.getXposition() + p.x) >= 0)

            if(( pfItem.getXSpace() + p.x ) >= 0 ) {

                // pfItem.setXspace(pfItem.getXposition() + p.x);

                pfItem.setXSpace( pfItem.getXSpace() + p.x );
            } else {
                pfItem.setXSpace( 0 );
            }

            // if ((pfItem.getXposition() + p.x) >= 0)

            if(( pfItem.getYSpace() + p.y ) >= 0 ) {

                // pfItem.setYspace(pfItem.getYposition() + p.y);

                pfItem.setYSpace( pfItem.getYSpace() + p.y );
            } else {
                pfItem.setYSpace( 0 );
            }
        } else {
            pfItem.setYPosition( pfItem.getYPosition() + p.y );
            pfItem.setXPosition( pfItem.getXPosition() + p.x );
        }

        pfItem.save();

        if( isDragged ) {
            Point2D newPos = new Point2D.Double(( changes.x ) - MARGIN,( changes.y ) - MARGIN );

            super.setLocation( newPos );
        }
    }

/* (non-Javadoc)
 * @see org.openXpertya.print.layout.InterfaceDesign#setDimension()
 */

    /**
     * Descripción de Método
     *
     */

    public void setDimension() {
        if(( pfItem.getMaxHeight() + ( changes.height - bounds.height )) >= 15 ) {

            // pfItem.setMaxHeight(pfItem.getMaxHeight() + (changes.height - bounds.height));

            pfItem.setMaxHeight( changes.height );
        } else {
            pfItem.setMaxHeight( 15 );
        }

        if(( pfItem.getMaxWidth() + ( changes.width - bounds.width )) >= 15 ) {

            // pfItem.setMaxWidth(pfItem.getMaxWidth() + (changes.width - bounds.width));

            pfItem.setMaxWidth( changes.width );
        } else {
            pfItem.setMaxWidth( 15 );
        }

        pfItem.save();
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#isSelected()
     */

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSelected() {
        return isSelected;
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#setSelected(boolean)
     */

    /**
     * Descripción de Método
     *
     *
     * @param isSelected
     */

    public void setSelected( boolean isSelected ) {
        this.isSelected = isSelected;
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#isDragged()
     */

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDragged() {
        return isDragged;
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#isClicked(int, int)
     */

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     *
     * @return
     */

    public boolean isClicked( int x,int y ) {
        if(( bounds != null ) && ( bounds.contains( x,y ))) {
            isMoveOver = true;

            return true;
        } else {
            return false;
        }
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#isCornerClicked(int, int)
     */

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     *
     * @return
     */

    public boolean isCornerClicked( int x,int y ) {
        isChangedSize = true;

        if( tpleft.isCornerClicked( x,y )) {
            m_cornerClicked = CornerElement.TOPLEFT;
        } else if( tpright.isCornerClicked( x,y )) {
            m_cornerClicked = CornerElement.TOPRIGHT;
        } else if( dwleft.isCornerClicked( x,y )) {
            m_cornerClicked = CornerElement.DOWNLEFT;
        } else if( dwright.isCornerClicked( x,y )) {
            m_cornerClicked = CornerElement.DOWNRIGHT;
        } else {
            m_cornerClicked = 0;
            isChangedSize   = false;
        }

        return( isChangedSize );
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#getCornerClicked()
     */

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getCornerClicked() {
        return m_cornerClicked;
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#isChangedSize()
     */

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isChangedSize() {
        return isChangedSize;
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#dragged(int)
     */

    /**
     * Descripción de Método
     *
     *
     * @param MARGIN
     */

    public void dragged( int MARGIN ) {
        setLocation( MARGIN );
        isDragged     = false;
        isChangedSize = false;
        isMoveOver    = false;
        bounds        = ( Rectangle )changes.clone();
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#changedSize(int)
     */

    /**
     * Descripción de Método
     *
     *
     * @param MARGIN
     */

    public void changedSize( int MARGIN ) {
        isChangedSize = false;
        setLocation( MARGIN );
        isDragged  = false;
        isMoveOver = false;
        setDimension();
        bounds = ( Rectangle )changes.clone();
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#Changes(int, int)
     */

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     */

    public void Changes( int x,int y ) {
        isDragged = true;

        if( !isChangedSize ) {
            changes = new Rectangle( bounds.x + x,bounds.y + y,bounds.width,bounds.height );
        } else {
            if( getCornerClicked() == CornerElement.TOPLEFT ) {
                changes = new Rectangle( bounds.x + x,bounds.y + y,bounds.width - x,bounds.height - y );
            } else if( getCornerClicked() == CornerElement.TOPRIGHT ) {
                changes = new Rectangle( bounds.x,bounds.y + y,bounds.width + x,bounds.height - y );
            } else if( getCornerClicked() == CornerElement.DOWNLEFT ) {
                changes = new Rectangle( bounds.x + x,bounds.y,bounds.width - x,bounds.height + y );
            } else {
                changes = new Rectangle( bounds.x,bounds.y,bounds.width + x,bounds.height + y );
            }

            Rectangle correct = ( Rectangle )changes.clone();

            if( changes.width < 15 ) {
                if( (getCornerClicked() == CornerElement.TOPLEFT) || (getCornerClicked() == CornerElement.DOWNLEFT) ) {
                    changes = new Rectangle(( bounds.x + bounds.width ) - 15,correct.y,15,correct.height );
                } else {
                    changes = new Rectangle( correct.x,correct.y,15,correct.height );
                }

                correct = ( Rectangle )changes.clone();
            }

            if( changes.height < 15 ) {
                if( (getCornerClicked() == CornerElement.TOPRIGHT) || (getCornerClicked() == CornerElement.TOPLEFT) ) {
                    changes = new Rectangle( correct.x,( bounds.y + bounds.height ) - 15,correct.width,15 );
                } else {
                    changes = new Rectangle( correct.x,correct.y,correct.width,15 );
                }
            }
        }
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#getPrintFormatItemID()
     */

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPrintFormatItemID() {
        return pfItem.getAD_PrintFormatItem_ID();
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#isRelativePosition()
     */

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isRelativePosition() {
        return pfItem.isRelativePosition();
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#getSeqNo()
     */

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSeqNo() {
        return pfItem.getSeqNo();
    }

    /**
     * Descripción de Método
     *
     *
     * @param seqNo
     */

    public void setSeqNo( int seqNo ) {
        pfItem.setSeqNo( seqNo );
        pfItem.save();
    }

    /**
     * Descripción de Método
     *
     *
     * @param vd
     *
     * @return
     */

    public ArrayList getMenuItems( ViewDesign vd ) {
        ArrayList items = new ArrayList();

        m_viewDesign = vd;

        if( pfItem.isActive()) {
            items.add( new JMenuItem( ACTIVO,ICON_TRUE ));
        } else {
            items.add( new JMenuItem( ACTIVO,ICON_FALSE ));
        }

        if( pfItem.isPrinted()) {
            items.add( new JMenuItem( IMPRESO,ICON_TRUE ));
        } else {
            items.add( new JMenuItem( IMPRESO,ICON_FALSE ));
        }

        if( pfItem.isPrinted()) {
            if( pfItem.isSuppressNull()) {
                items.add( new JMenuItem( SUPRIMIRNULOS,ICON_TRUE ));
            } else {
                items.add( new JMenuItem( SUPRIMIRNULOS,ICON_FALSE ));
            }
        }

        if( pfItem.isImageIsAttached()) {
            items.add( new JMenuItem( IMAGENADJUNTA,ICON_TRUE ));
        } else {
            items.add( new JMenuItem( IMAGENADJUNTA,ICON_FALSE ));
        }

        if( pfItem.isRelativePosition()) {
            items.add( new JMenuItem( POSICIONRELATIVA,ICON_TRUE ));
        } else {
            items.add( new JMenuItem( POSICIONRELATIVA,ICON_FALSE ));
        }

        if( pfItem.isSetNLPosition()) {
            items.add( new JMenuItem( FIJARPOSICIONNL,ICON_TRUE ));
        } else {
            items.add( new JMenuItem( FIJARPOSICIONNL,ICON_FALSE ));
        }

        if( pfItem.isRelativePosition()) {
            if( pfItem.isNextLine()) {
                items.add( new JMenuItem( PROXIMALINEA,ICON_TRUE ));
            } else {
                items.add( new JMenuItem( PROXIMALINEA,ICON_FALSE ));
            }
        }

        if( pfItem.isRelativePosition()) {
            if( pfItem.isNextPage()) {
                items.add( new JMenuItem( PROXIMAPAGINA,ICON_TRUE ));
            } else {
                items.add( new JMenuItem( PROXIMAPAGINA,ICON_FALSE ));
            }
        }

        if( pfItem.isFixedWidth()) {
            items.add( new JMenuItem( ANCHOFIJO,ICON_TRUE ));
        } else {
            items.add( new JMenuItem( ANCHOFIJO,ICON_FALSE ));
        }

        if( pfItem.isHeightOneLine()) {
            items.add( new JMenuItem( UNALINEA,ICON_TRUE ));
        } else {
            items.add( new JMenuItem( UNALINEA,ICON_FALSE ));
        }

        for( int i = 0;i < items.size();i++ ) {
            (( JMenuItem )items.get( i )).addActionListener( this );
        }

        return items;
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // TODO Auto-generated method stub

        if( e.getActionCommand() == ACTIVO ) {
            if( pfItem.isActive()) {
                pfItem.setIsActive( false );
            } else {
                pfItem.setIsActive( true );
            }
        }

        if( e.getActionCommand() == IMPRESO ) {
            if( pfItem.isPrinted()) {
                pfItem.setIsPrinted( false );
            } else {
                pfItem.setIsPrinted( true );
            }
        }

        if( e.getActionCommand() == SUPRIMIRNULOS ) {
            if( pfItem.isSuppressNull()) {
                pfItem.setIsSuppressNull( false );
            } else {
                pfItem.setIsSuppressNull( true );
            }
        }

        if( e.getActionCommand() == IMAGENADJUNTA ) {
            if( pfItem.isImageIsAttached()) {
                pfItem.setImageIsAttached( false );
            } else {
                pfItem.setImageIsAttached( true );
            }
        }

        if( e.getActionCommand() == POSICIONRELATIVA ) {
            if( pfItem.isRelativePosition()) {
                pfItem.setIsRelativePosition( false );
            } else {
                pfItem.setIsRelativePosition( true );
            }
        }

        if( e.getActionCommand() == FIJARPOSICIONNL ) {
            if( pfItem.isSetNLPosition()) {
                pfItem.setIsSetNLPosition( false );
            } else {
                pfItem.setIsSetNLPosition( true );
            }
        }

        if( e.getActionCommand() == PROXIMALINEA ) {
            if( pfItem.isNextLine()) {
                pfItem.setIsNextLine( false );
            } else {
                pfItem.setIsNextLine( true );
            }
        }

        if( e.getActionCommand() == ANCHOFIJO ) {
            if( pfItem.isFixedWidth()) {
                pfItem.setIsFixedWidth( false );
            } else {
                pfItem.setIsFixedWidth( true );
            }
        }

        if( e.getActionCommand() == UNALINEA ) {
            if( pfItem.isHeightOneLine()) {
                pfItem.setIsHeightOneLine( false );
            } else {
                pfItem.setIsHeightOneLine( true );
            }
        }

        if( e.getActionCommand() == PROXIMAPAGINA ) {
            if( pfItem.isNextPage()) {
                pfItem.setIsNextPage( false );
            } else {
                pfItem.setIsNextPage( true );
            }

            pfItem.save();
            m_viewDesign.changePage( this );
        } else {
            pfItem.save();
            m_viewDesign.updateLayout();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getFields() {
        ArrayList fields = new ArrayList();
        String    pos;

        // Primera barra de herramientas

        pos = new String( "Uno" );
        fields.add( pos );

        CLabel labelPrintFormatType = new CLabel( "Formato de impresi�n:" );

        fields.add( labelPrintFormatType );

        VLookup printFormatType;
        MLookup m_printFormatType = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),255 ),0 );

        printFormatType = new VLookup( "AD_Print_Format_Type",true,false,true,m_printFormatType );
        printFormatType.setValue( new String( pfItem.getPrintFormatType()));
        printFormatType.setSize( 150,24 );
        printFormatType.setMaximumSize( printFormatType.getSize());
        printFormatType.setPreferredSize( printFormatType.getSize());
        fields.add( printFormatType );

        CLabel labelFieldAlignement = new CLabel( "Alineacion del campo:" );

        fields.add( labelFieldAlignement );

        VLookup FieldAlignement;
        MLookup m_fieldAlignement = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),253 ),0 );

        FieldAlignement = new VLookup( "FieldAlignmetType",true,false,true,m_fieldAlignement );
        FieldAlignement.setValue( new String( pfItem.getFieldAlignmentType()));
        FieldAlignement.setSize( 150,24 );
        FieldAlignement.setMaximumSize( FieldAlignement.getSize());
        FieldAlignement.setPreferredSize( FieldAlignement.getSize());
        fields.add( FieldAlignement );

        CLabel labelLineAlignement = new CLabel( "Alineacion de la linea:" );

        fields.add( labelLineAlignement );

        VLookup lineAlignement;
        MLookup m_lineAlignement = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),254 ),0 );

        if( pfItem.isRelativePosition()) {
            lineAlignement = new VLookup( "LinedAlignmetType",true,false,true,m_lineAlignement );
            lineAlignement.setValue( new String( pfItem.getLineAlignmentType()));
        } else {
            lineAlignement = new VLookup( "LinedAlignmetType",false,true,false,m_lineAlignement );
        }

        lineAlignement.setSize( 150,24 );
        lineAlignement.setMaximumSize( lineAlignement.getSize());
        lineAlignement.setPreferredSize( lineAlignement.getSize());
        fields.add( lineAlignement );

        // Segunda barra de herramientas

        pos = new String( "Dos" );
        fields.add( pos );

        CLabel labelSeqNo = new CLabel( "Numero de Secuencia:" );

        fields.add( labelSeqNo );

        VNumber seqNo = new VNumber( "SeqNo",true,false,true,DisplayType.Integer,"Numero de Secuencia" );

        seqNo.setValue( new Integer( pfItem.getSeqNo()));
        seqNo.setSize( 60,20 );
        seqNo.setMaximumSize( seqNo.getSize());
        seqNo.setPreferredSize( seqNo.getSize());
        fields.add( seqNo );

        CLabel labelMaxWidth = new CLabel( "M�ximo Ancho:" );

        fields.add( labelMaxWidth );

        VNumber maxWidth = new VNumber( "MaxWidth",true,false,true,DisplayType.Integer,"M�ximo Ancho" );

        maxWidth.setValue( new Integer( pfItem.getMaxWidth()));
        maxWidth.setSize( 60,20 );
        maxWidth.setMaximumSize( maxWidth.getSize());
        maxWidth.setPreferredSize( maxWidth.getSize());
        fields.add( maxWidth );

        CLabel labelMaxHeight = new CLabel( "M�ximo Alto:" );

        fields.add( labelMaxHeight );

        VNumber maxHeight = new VNumber( "MaxHeight",true,false,true,DisplayType.Integer,"M�ximo Alto" );

        maxHeight.setValue( new Integer( pfItem.getMaxHeight()));
        maxHeight.setSize( 60,20 );
        maxHeight.setMaximumSize( maxHeight.getSize());
        maxHeight.setPreferredSize( maxHeight.getSize());
        fields.add( maxHeight );

        if( pfItem.isRelativePosition()) {
            CLabel labelXSpace = new CLabel( " Espacio X:" );

            fields.add( labelXSpace );

            VNumber XSpace = new VNumber( "XSpace",true,false,true,DisplayType.Integer,"Espacio X" );

            XSpace.setValue( new Integer( pfItem.getXSpace()));
            XSpace.setSize( 60,20 );
            XSpace.setMaximumSize( XSpace.getSize());
            XSpace.setPreferredSize( XSpace.getSize());
            fields.add( XSpace );

            CLabel labelYSpace = new CLabel( " Espacio Y:" );

            fields.add( labelYSpace );

            VNumber YSpace = new VNumber( "YSpace",true,false,true,DisplayType.Integer,"Espacio Y" );

            YSpace.setValue( new Integer( pfItem.getYSpace()));
            YSpace.setSize( 60,20 );
            YSpace.setMaximumSize( YSpace.getSize());
            YSpace.setPreferredSize( YSpace.getSize());
            fields.add( YSpace );
        } else {
            CLabel labelXPosition = new CLabel( "Posici�n X:" );

            fields.add( labelXPosition );

            VNumber XPosition = new VNumber( "XPosition",true,false,true,DisplayType.Integer,"Posici�n X" );

            XPosition.setValue( new Integer( pfItem.getXPosition()));
            XPosition.setSize( 60,20 );
            XPosition.setMaximumSize( XPosition.getSize());
            XPosition.setPreferredSize( XPosition.getSize());
            fields.add( XPosition );

            CLabel labelYPosition = new CLabel( "Posici�n Y:" );

            fields.add( labelYPosition );

            VNumber YPosition = new VNumber( "YPosition",true,false,true,DisplayType.Integer,"Posici�n Y" );

            YPosition.setValue( new Integer( pfItem.getYPosition()));
            YPosition.setSize( 60,20 );
            YPosition.setMaximumSize( YPosition.getSize());
            YPosition.setPreferredSize( YPosition.getSize());
            fields.add( YPosition );
        }

        // Tercera barra de herramientas

        pos = new String( "Tres" );
        fields.add( pos );

        CLabel labelName = new CLabel( "Nombre:" );

        fields.add( labelName );

        CTextField Name = new CTextField( pfItem.getName());

        Name.setSize( 140,23 );
        Name.setMaximumSize( Name.getSize());
        Name.setPreferredSize( Name.getSize());
        fields.add( Name );

        CLabel labelPrintName = new CLabel( "Nombre a imprimir:" );

        fields.add( labelPrintName );

        CTextField PrintName = new CTextField( pfItem.getPrintName());

        PrintName.setSize( 140,23 );
        PrintName.setMaximumSize( PrintName.getSize());
        PrintName.setPreferredSize( PrintName.getSize());
        fields.add( PrintName );

        if( !pfItem.isImageIsAttached()) {
            CLabel labelImageURL = new CLabel( "URL de la Imagen:" );

            fields.add( labelImageURL );

            CTextField ImageURL = new CTextField( pfItem.getImageURL());

            ImageURL.setSize( 240,23 );
            ImageURL.setMaximumSize( ImageURL.getSize());
            ImageURL.setPreferredSize( ImageURL.getSize());
            fields.add( ImageURL );
        }

        return fields;
    }

    /**
     * Descripción de Método
     *
     *
     * @param areatype
     *
     * @return
     */

    public boolean setHeaderFooter( String areatype ) {
        boolean change = false;

        if( !areatype.equals( pfItem.getPrintAreaType())) {
            if( pfItem.isRelativePosition()) {
                pfItem.setXSpace( 0 );
                pfItem.setYSpace( 0 );
            } else {
                pfItem.setXPosition( 0 );
                pfItem.setYPosition( 0 );
            }

            change = true;
        }

        pfItem.setPrintAreaType( areatype );
        pfItem.save();

        return change;
    }
}    // End ImageDesElement



/*
 *  @(#)ImageDesElement.java   02.07.07
 * 
 *  Fin del fichero ImageDesElement.java
 *  
 *  Versión 2.2
 *
 */
