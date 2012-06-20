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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.print.layout.StringElement;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.NamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DesignElement extends StringElement implements InterfaceDesign,ActionListener {

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

    private Rectangle bounds;

    /** Descripción de Campos */

    private Rectangle changes;

    /** Descripción de Campos */

    private ViewDesign m_viewDesign;

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

    private String m_string = "";

    /** Descripción de Campos */

    private MPrintFormatItem pfItem;

    /** Descripción de Campos */

    public static Image IMAGE_TRUE = null;

    /** Descripción de Campos */

    public static Image IMAGE_FALSE = null;

    // public static Dimension               IMAGE_SIZE = new Dimension(10,10);

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
     * Constructor de la clase ...
     *
     *
     * @param inText
     * @param font
     * @param paint
     * @param ID
     * @param translateText
     * @param item
     */

    public DesignElement( String inText,Font font,Paint paint,NamePair ID,boolean translateText,MPrintFormatItem item ) {
        super( inText,font,paint,ID,translateText );
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
        m_string          = inText;
    }

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#paint(java.awt.Graphics2D, int, java.awt.geom.Point2D, java.util.Properties, boolean)
     */

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
        super.paint( g2D,pageNo,pageStart,ctx,isView );

        // Position

        Point2D.Double location = getAbsoluteLocation( pageStart );
        int            x        = ( int )location.x;

        if( MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight.equals( p_FieldAlignmentType )) {
            x += p_maxWidth - p_width;
        } else if( MPrintFormatItem.FIELDALIGNMENTTYPE_Center.equals( p_FieldAlignmentType )) {
            x += ( p_maxWidth - p_width ) / 2;
        }

        int y = ( int )location.y;

        if( isCalculateBounds ) {
            bounds            = new Rectangle( x,y,( int )p_width,( int )p_height );
            changes           = new Rectangle( x,y,( int )p_width,( int )p_height );
            isCalculateBounds = false;
        }

        if( isSelected ) {

            // Dibuja la linea que delimita los bordes del campo

            linea.puntos( g2D,bounds.x - 1,bounds.y,bounds.width,bounds.height );    // la coordenada X es x-1,para que la linea punteada no se sobreescriba a la primera letra
            tpleft.paint( g2D,( bounds.x - CornerElement.CORNERSIZE ) - 1,( bounds.y - CornerElement.CORNERSIZE ));
            tpright.paint( g2D,( int )(( bounds.x + bounds.width ) - 1 ),bounds.y - CornerElement.CORNERSIZE );
            dwleft.paint( g2D,( bounds.x - CornerElement.CORNERSIZE ) - 1,( int )( bounds.y + bounds.height ));
            dwright.paint( g2D,( int )(( bounds.x + bounds.width ) - 1 ),( int )( bounds.y + bounds.height ));

            if( isDragged ) {
                arrastrar.puntos( g2D,( changes.x - 1 ),changes.y,changes.width,changes.height );
            }
        } else {
            if( isMoveOver ) {
                linea.puntos( g2D,bounds.x - 1,bounds.y,bounds.width,bounds.height );    // la coordenada X es x-1,para que la linea punteada no se sobreescriba a la primera letra
            }

            isMoveOver = false;
        }
    }    // End paint

    /*
     *  (non-Javadoc)
     * @see org.openXpertya.print.layout.InterfaceDesign#setLocation(int)
     */

    /**
     * Descripción de Método
     *
     *
     * @param MARGIN
     */

    public void setLocation( int MARGIN ) {
        Point p = new Point(( changes.x - bounds.x ),( changes.y - bounds.y ));

        if( pfItem.isRelativePosition()) {
            if(( pfItem.getXSpace() + p.x ) >= 0 ) {
                pfItem.setXSpace( pfItem.getXSpace() + p.x );
            } else {
                pfItem.setXSpace( 0 );
            }

            if(( pfItem.getYSpace() + p.y ) >= 0 ) {
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
            pfItem.setMaxHeight( changes.height );
        } else {
            pfItem.setMaxHeight( 15 );
        }

        if(( pfItem.getMaxWidth() + ( changes.width - bounds.width )) >= 15 ) {
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

        if( pfItem.getPrintFormatType().charAt( 0 ) == 'T' ) {
            fields = getFieldsText();
        } else {
            fields = getFieldsField();
        }

        return fields;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getFieldsText() {
        ArrayList text = new ArrayList();
        String    pos;

        // Primera barra de herramientas

        pos = new String( "Uno" );
        text.add( pos );

        CLabel labelPrintFormatType = new CLabel( "Formato de impresi�n:" );

        text.add( labelPrintFormatType );

        VLookup printFormatType;
        MLookup m_printFormatType = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),255 ),0 );

        printFormatType = new VLookup( "AD_Print_Format_Type",true,false,true,m_printFormatType );
        printFormatType.setValue( new String( pfItem.getPrintFormatType()));
        printFormatType.setSize( 150,24 );
        printFormatType.setMaximumSize( printFormatType.getSize());
        printFormatType.setPreferredSize( printFormatType.getSize());
        text.add( printFormatType );

        CLabel labelFieldAlignement = new CLabel( "Alineacion del campo:" );

        text.add( labelFieldAlignement );

        VLookup FieldAlignement;
        MLookup m_fieldAlignement = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),253 ),0 );

        FieldAlignement = new VLookup( "FieldAlignmetType",true,false,true,m_fieldAlignement );
        FieldAlignement.setValue( new String( pfItem.getFieldAlignmentType()));
        FieldAlignement.setSize( 150,24 );
        FieldAlignement.setMaximumSize( FieldAlignement.getSize());
        FieldAlignement.setPreferredSize( FieldAlignement.getSize());
        text.add( FieldAlignement );

        CLabel labelLineAlignement = new CLabel( "Alineacion de la linea:" );

        text.add( labelLineAlignement );

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
        text.add( lineAlignement );

        // Segunda barra de herramientas

        pos = new String( "Dos" );
        text.add( pos );

        CLabel labelSeqNo = new CLabel( "Numero de Secuencia:" );

        text.add( labelSeqNo );

        VNumber seqNo = new VNumber( "SeqNo",true,false,true,DisplayType.Integer,"Numero de Secuencia" );

        seqNo.setValue( new Integer( pfItem.getSeqNo()));
        seqNo.setSize( 60,20 );
        seqNo.setMaximumSize( seqNo.getSize());
        seqNo.setPreferredSize( seqNo.getSize());
        text.add( seqNo );

        CLabel labelMaxWidth = new CLabel( "M�ximo Ancho:" );

        text.add( labelMaxWidth );

        VNumber maxWidth = new VNumber( "MaxWidth",true,false,true,DisplayType.Integer,"M�ximo Ancho" );

        maxWidth.setValue( new Integer( pfItem.getMaxWidth()));
        maxWidth.setSize( 60,20 );
        maxWidth.setMaximumSize( maxWidth.getSize());
        maxWidth.setPreferredSize( maxWidth.getSize());
        text.add( maxWidth );

        CLabel labelMaxHeight = new CLabel( "M�ximo Alto:" );

        text.add( labelMaxHeight );

        VNumber maxHeight = new VNumber( "MaxHeight",true,false,true,DisplayType.Integer,"M�ximo Alto" );

        maxHeight.setValue( new Integer( pfItem.getMaxHeight()));
        maxHeight.setSize( 60,20 );
        maxHeight.setMaximumSize( maxHeight.getSize());
        maxHeight.setPreferredSize( maxHeight.getSize());
        text.add( maxHeight );

        if( pfItem.isRelativePosition()) {
            CLabel labelXSpace = new CLabel( " Espacio X:" );

            text.add( labelXSpace );

            VNumber XSpace = new VNumber( "XSpace",true,false,true,DisplayType.Integer,"Espacio X" );

            XSpace.setValue( new Integer( pfItem.getXSpace()));
            XSpace.setSize( 60,20 );
            XSpace.setMaximumSize( XSpace.getSize());
            XSpace.setPreferredSize( XSpace.getSize());
            text.add( XSpace );

            CLabel labelYSpace = new CLabel( " Espacio Y:" );

            text.add( labelYSpace );

            VNumber YSpace = new VNumber( "YSpace",true,false,true,DisplayType.Integer,"Espacio Y" );

            YSpace.setValue( new Integer( pfItem.getYSpace()));
            YSpace.setSize( 60,20 );
            YSpace.setMaximumSize( YSpace.getSize());
            YSpace.setPreferredSize( YSpace.getSize());
            text.add( YSpace );
        } else {
            CLabel labelXPosition = new CLabel( "Posici�n X:" );

            text.add( labelXPosition );

            VNumber XPosition = new VNumber( "XPosition",true,false,true,DisplayType.Integer,"Posici�n X" );

            XPosition.setValue( new Integer( pfItem.getXPosition()));
            XPosition.setSize( 60,20 );
            XPosition.setMaximumSize( XPosition.getSize());
            XPosition.setPreferredSize( XPosition.getSize());
            text.add( XPosition );

            CLabel labelYPosition = new CLabel( "Posici�n Y:" );

            text.add( labelYPosition );

            VNumber YPosition = new VNumber( "YPosition",true,false,true,DisplayType.Integer,"Posici�n Y" );

            YPosition.setValue( new Integer( pfItem.getYPosition()));
            YPosition.setSize( 60,20 );
            YPosition.setMaximumSize( YPosition.getSize());
            YPosition.setPreferredSize( YPosition.getSize());
            text.add( YPosition );
        }

        // Tercera barra de herramientas

        pos = new String( "Tres" );
        text.add( pos );

        CLabel labelName = new CLabel( "Nombre:" );

        text.add( labelName );

        CTextField Name = new CTextField( pfItem.getName());

        Name.setSize( 140,23 );
        Name.setMaximumSize( Name.getSize());
        Name.setPreferredSize( Name.getSize());
        text.add( Name );

        CLabel labelPrintName = new CLabel( "Nombre a imprimir:" );

        text.add( labelPrintName );

        CTextField PrintName = new CTextField( pfItem.getPrintName());

        PrintName.setSize( 140,23 );
        PrintName.setMaximumSize( PrintName.getSize());
        PrintName.setPreferredSize( PrintName.getSize());
        text.add( PrintName );

        // Cuarta barra de herramientas

        pos = new String( "Cuatro" );
        text.add( pos );

        try {
            CLabel labelPrintFont = new CLabel( "Fuente de impresi�n:" );

            text.add( labelPrintFont );

            VLookup printFont;
            MLookup m_printFont = MLookupFactory.get( Env.getCtx(),0,6963,19,Env.getLanguage( Env.getCtx()),"AD_PrintFont_ID",267,false,"" );

            printFont = new VLookup( "AD_PrintFont_ID",false,false,true,m_printFont );
            printFont.setValue( new Integer( pfItem.getAD_PrintFont_ID()));
            printFont.setSize( 150,24 );
            printFont.setMaximumSize( printFont.getSize());
            printFont.setPreferredSize( printFont.getSize());
            text.add( printFont );
        } catch( Exception e ) {
        }

        try {
            CLabel labelPrintColor = new CLabel( "Color de impresi�n:" );

            text.add( labelPrintColor );

            // CComboBox printColor = new CComboBox();

            VLookup printColor;
            MLookup m_printColor = MLookupFactory.get( Env.getCtx(),0,6958,19,Env.getLanguage( Env.getCtx()),"AD_PrintColor_ID",266,false,"" );

            printColor = new VLookup( "AD_PrintColor_ID",false,false,true,m_printColor );
            printColor.setValue( new Integer( pfItem.getAD_PrintColor_ID()));
            printColor.setSize( 150,24 );
            printColor.setMaximumSize( printColor.getSize());
            printColor.setPreferredSize( printColor.getSize());
            text.add( printColor );
        } catch( Exception e ) {
        }

        return text;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getFieldsField() {
        ArrayList field = new ArrayList();
        String    pos;

        // Primera barra de herramientas

        pos = new String( "Uno" );
        field.add( pos );

        CLabel labelPrintFormatType = new CLabel( "Formato de impresi�n:" );

        field.add( labelPrintFormatType );

        VLookup printFormatType;
        MLookup m_printFormatType = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),255 ),0 );

        printFormatType = new VLookup( "AD_Print_Format_Type",true,false,true,m_printFormatType );
        printFormatType.setValue( new String( pfItem.getPrintFormatType()));
        printFormatType.setSize( 150,24 );
        printFormatType.setMaximumSize( printFormatType.getSize());
        printFormatType.setPreferredSize( printFormatType.getSize());
        field.add( printFormatType );

        CLabel labelFieldAlignement = new CLabel( "Alineacion del campo:" );

        field.add( labelFieldAlignement );

        VLookup FieldAlignement;
        MLookup m_fieldAlignement = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),253 ),0 );

        FieldAlignement = new VLookup( "FieldAlignmetType",true,false,true,m_fieldAlignement );
        FieldAlignement.setValue( new String( pfItem.getFieldAlignmentType()));
        FieldAlignement.setSize( 150,24 );
        FieldAlignement.setMaximumSize( FieldAlignement.getSize());
        FieldAlignement.setPreferredSize( FieldAlignement.getSize());
        field.add( FieldAlignement );

        CLabel labelLineAlignement = new CLabel( "Alineacion de la linea:" );

        field.add( labelLineAlignement );

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
        field.add( lineAlignement );

        // Segunda barra de herramientas

        pos = new String( "Dos" );
        field.add( pos );

        CLabel labelSeqNo = new CLabel( "Numero de Secuencia:" );

        field.add( labelSeqNo );

        VNumber seqNo = new VNumber( "SeqNo",true,false,true,DisplayType.Integer,"Numero de Secuencia" );

        seqNo.setValue( new Integer( pfItem.getSeqNo()));
        seqNo.setSize( 60,20 );
        seqNo.setMaximumSize( seqNo.getSize());
        seqNo.setPreferredSize( seqNo.getSize());
        field.add( seqNo );

        CLabel labelMaxWidth = new CLabel( "M�ximo Ancho:" );

        field.add( labelMaxWidth );

        VNumber maxWidth = new VNumber( "MaxWidth",true,false,true,DisplayType.Integer,"M�ximo Ancho" );

        maxWidth.setValue( new Integer( pfItem.getMaxWidth()));
        maxWidth.setSize( 60,20 );
        maxWidth.setMaximumSize( maxWidth.getSize());
        maxWidth.setPreferredSize( maxWidth.getSize());
        field.add( maxWidth );

        CLabel labelMaxHeight = new CLabel( "M�ximo Alto:" );

        field.add( labelMaxHeight );

        VNumber maxHeight = new VNumber( "MaxHeight",true,false,true,DisplayType.Integer,"M�ximo Alto" );

        maxHeight.setValue( new Integer( pfItem.getMaxHeight()));
        maxHeight.setSize( 60,20 );
        maxHeight.setMaximumSize( maxHeight.getSize());
        maxHeight.setPreferredSize( maxHeight.getSize());
        field.add( maxHeight );

        if( pfItem.isRelativePosition()) {
            CLabel labelXSpace = new CLabel( " Espacio X:" );

            field.add( labelXSpace );

            VNumber XSpace = new VNumber( "XSpace",true,false,true,DisplayType.Integer,"Espacio X" );

            XSpace.setValue( new Integer( pfItem.getXSpace()));
            XSpace.setSize( 60,20 );
            XSpace.setMaximumSize( XSpace.getSize());
            XSpace.setPreferredSize( XSpace.getSize());
            field.add( XSpace );

            CLabel labelYSpace = new CLabel( " Espacio Y:" );

            field.add( labelYSpace );

            VNumber YSpace = new VNumber( "YSpace",true,false,true,DisplayType.Integer,"Espacio Y" );

            YSpace.setValue( new Integer( pfItem.getYSpace()));
            YSpace.setSize( 60,20 );
            YSpace.setMaximumSize( YSpace.getSize());
            YSpace.setPreferredSize( YSpace.getSize());
            field.add( YSpace );
        } else {
            CLabel labelXPosition = new CLabel( "Posici�n X:" );

            field.add( labelXPosition );

            VNumber XPosition = new VNumber( "XPosition",true,false,true,DisplayType.Integer,"Posici�n X" );

            XPosition.setValue( new Integer( pfItem.getXPosition()));
            XPosition.setSize( 60,20 );
            XPosition.setMaximumSize( XPosition.getSize());
            XPosition.setPreferredSize( XPosition.getSize());
            field.add( XPosition );

            CLabel labelYPosition = new CLabel( "Posici�n Y:" );

            field.add( labelYPosition );

            VNumber YPosition = new VNumber( "YPosition",true,false,true,DisplayType.Integer,"Posici�n Y" );

            YPosition.setValue( new Integer( pfItem.getYPosition()));
            YPosition.setSize( 60,20 );
            YPosition.setMaximumSize( YPosition.getSize());
            YPosition.setPreferredSize( YPosition.getSize());
            field.add( YPosition );
        }

        // Tercera barra de herramientas

        pos = new String( "Tres" );
        field.add( pos );

        CLabel labelName = new CLabel( "Nombre:" );

        field.add( labelName );

        CTextField Name = new CTextField( pfItem.getName());

        Name.setSize( 140,23 );
        Name.setMaximumSize( Name.getSize());
        Name.setPreferredSize( Name.getSize());
        field.add( Name );

        CLabel labelPrintName = new CLabel( "Nombre a imprimir:" );

        field.add( labelPrintName );

        CTextField PrintName = new CTextField( pfItem.getPrintName());

        PrintName.setSize( 140,23 );
        PrintName.setMaximumSize( PrintName.getSize());
        PrintName.setPreferredSize( PrintName.getSize());
        field.add( PrintName );

        CLabel labelPrintNameSuffix = new CLabel( "Sufijo de Impresi�n de Etiquetas:" );

        field.add( labelPrintNameSuffix );

        CTextField PrintNameSuffix = new CTextField( pfItem.getPrintNameSuffix());

        PrintNameSuffix.setSize( 140,23 );
        PrintNameSuffix.setMaximumSize( PrintNameSuffix.getSize());
        PrintNameSuffix.setPreferredSize( PrintNameSuffix.getSize());
        field.add( PrintNameSuffix );

        // Cuarta barra de herramientas

        pos = new String( "Cuatro" );
        field.add( pos );

        try {
            CLabel labelPrintFont = new CLabel( "Fuente de impresi�n:" );

            field.add( labelPrintFont );

            VLookup printFont;
            MLookup m_printFont = MLookupFactory.get( Env.getCtx(),0,6963,19,Env.getLanguage( Env.getCtx()),"AD_PrintFont_ID",267,false,"" );

            printFont = new VLookup( "AD_PrintFont_ID",false,false,true,m_printFont );
            printFont.setValue( new Integer( pfItem.getAD_PrintFont_ID()));
            printFont.setSize( 150,24 );
            printFont.setMaximumSize( printFont.getSize());
            printFont.setPreferredSize( printFont.getSize());
            field.add( printFont );
        } catch( Exception e ) {
        }

        try {
            CLabel labelPrintColor = new CLabel( "Color de impresi�n:" );

            field.add( labelPrintColor );

            // CComboBox printColor = new CComboBox();

            VLookup printColor;
            MLookup m_printColor = MLookupFactory.get( Env.getCtx(),0,6958,19,Env.getLanguage( Env.getCtx()),"AD_PrintColor_ID",266,false,"" );

            printColor = new VLookup( "AD_PrintColor_ID",false,false,true,m_printColor );
            printColor.setValue( new Integer( pfItem.getAD_PrintColor_ID()));
            printColor.setSize( 150,24 );
            printColor.setMaximumSize( printColor.getSize());
            printColor.setPreferredSize( printColor.getSize());
            field.add( printColor );
        } catch( Exception e ) {
        }

        MPrintFormat pf = new MPrintFormat( Env.getCtx(),pfItem.getAD_PrintFormat_ID(),null );

        try {
            CLabel labelColumn = new CLabel( "Columna:" );

            field.add( labelColumn );

            MLookup m_column = MLookupFactory.get( Env.getCtx(),0,16,DisplayType.Table,Env.getLanguage( Env.getCtx()),"AD_Column",3,false,"AD_Table_ID =" + pf.getAD_Table_ID());
            VLookup column = new VLookup( "AD_Column_ID",true,false,true,m_column );

            column.setValue( new Integer( pfItem.getAD_Column_ID()));
            column.setSize( 150,24 );
            column.setMaximumSize( column.getSize());
            column.setPreferredSize( column.getSize());
            field.add( column );
        } catch( Exception e ) {
        }

        return field;
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
}    // End DesignElement



/*
 *  @(#)DesignElement.java   02.07.07
 * 
 *  Fin del fichero DesignElement.java
 *  
 *  Versión 2.2
 *
 */
