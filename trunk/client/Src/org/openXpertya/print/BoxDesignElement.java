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
import java.awt.Graphics2D;
import java.awt.Image;
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
import org.openXpertya.print.layout.PrintElement;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class BoxDesignElement extends PrintElement implements InterfaceDesign,ActionListener {

    /** Descripción de Campos */

    private boolean isDragged;

    /** Descripción de Campos */

    private boolean isSelected;

    /** Descripción de Campos */

    private boolean isCalculateBounds;

    /** Descripción de Campos */

    private boolean isMoveOver;

    /** Descripción de Campos */

    private boolean isChangedSize;

    /** Descripción de Campos */

    private ViewDesign m_viewDesign;

    /** Descripción de Campos */

    private CornerElement tpleft;

    /** Descripción de Campos */

    private CornerElement dwright;

    /** Descripción de Campos */

    private boolean lineDownRight;

    /** Descripción de Campos */

    private boolean lineDownLeft;

    /** Descripción de Campos */

    private boolean lineUpRight;

    /** Descripción de Campos */

    private boolean lineUpLeft;

    /** Descripción de Campos */

    private CornerElement arrastrar;

    /** Descripción de Campos */

    private CornerElement linea;

    /** Descripción de Campos */

    private int m_cornerClicked;

    /** Descripción de Campos */

    private Rectangle bounds;

    /** Descripción de Campos */

    private Rectangle changes;

    /** Descripción de Campos */

    private MPrintFormatItem pfItem;

    /** Descripción de Campos */

    public static Image IMAGE_TRUE = null;

    /** Descripción de Campos */

    public static Image IMAGE_FALSE = null;

    // public static Dimension           IMAGE_SIZE = new Dimension(10,10);

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
     * @param item
     * @param color
     */

    public BoxDesignElement( MPrintFormatItem item,Color color ) {
        super();

        if( (item != null) && item.isTypeBox()) {
            tpleft            = new CornerElement( CornerElement.TOPLEFT,this );
            dwright           = new CornerElement( CornerElement.DOWNRIGHT,this );
            lineDownRight     = false;
            lineDownLeft      = false;
            lineUpRight       = false;
            lineUpLeft        = false;
            linea             = new CornerElement( 0,this );
            arrastrar         = new CornerElement( 0,this );
            isSelected        = false;
            isDragged         = false;
            isMoveOver        = false;
            isCalculateBounds = true;
            m_cornerClicked   = 0;
            m_viewDesign      = null;
            pfItem            = item;
            m_item            = item;
            m_color           = color;
        }
    }    // BoxElement

    /** Descripción de Campos */

    private MPrintFormatItem m_item = null;

    /** Descripción de Campos */

    private Color m_color = Color.BLACK;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean calculateSize() {
        p_width  = 0;
        p_height = 0;

        if( m_item == null ) {
            return true;
        }

        return true;
    }    // calculateSize

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
        if( m_item == null ) {
            return;
        }

        //

        g2D.setColor( m_color );

        BasicStroke s = new BasicStroke( m_item.getLineWidth());

        g2D.setStroke( s );

        //

        Point2D.Double location    = getAbsoluteLocation( pageStart );
        int            x           = ( int )location.x;
        int            y           = ( int )location.y;
        int            width       = m_item.getMaxWidth();
        int            height      = m_item.getMaxHeight();
        int            selectX     = x;
        int            selectY     = y;
        int            selectWidth = width;

        lineDownRight = true;
        lineDownLeft  = false;
        lineUpRight   = false;
        lineUpLeft    = false;

        if( selectWidth < 0 ) {
            selectWidth   = -selectWidth;
            selectX       = x - selectWidth;
            lineDownRight = false;
            lineDownLeft  = true;
        }

        int selectHeight = height;

        if( selectHeight < 0 ) {
            selectHeight  = -selectHeight;
            selectY       = y - selectHeight;
            lineDownRight = false;
            lineUpRight   = true;
        }

        if( lineUpRight && lineDownLeft ) {
            lineUpRight  = false;
            lineDownLeft = false;
            lineUpLeft   = true;
        }

        if( isCalculateBounds ) {
            bounds  = new Rectangle( selectX,selectY,selectWidth,selectHeight );
            changes = new Rectangle( selectX,selectY,selectWidth,selectHeight );
            isCalculateBounds = false;
        }

        if( m_item.getPrintFormatType().equals( MPrintFormatItem.PRINTFORMATTYPE_Line )) {
            g2D.drawLine( x,y,x + width,y + height );

            if( isSelected ) {
                linea.puntos( g2D,selectX,selectY,selectWidth,selectHeight );

                if( lineDownLeft || lineUpLeft ) {
                    selectX = x - 1;
                } else {
                    selectX = ( x - CornerElement.CORNERSIZE ) - 1;
                }

                if( lineUpLeft || lineUpRight ) {
                    selectY = y;
                } else {
                    selectY = ( y - CornerElement.CORNERSIZE );
                }

                tpleft.paint( g2D,selectX,selectY );

                if( lineDownLeft || lineUpLeft ) {
                    selectX = ( int )(( x - selectWidth ) - 1 - CornerElement.CORNERSIZE );
                } else {
                    selectX = ( int )(( x + selectWidth ) - 1 );
                }

                if( lineUpLeft || lineUpRight ) {
                    selectY = ( int )( y - selectHeight ) - CornerElement.CORNERSIZE;
                } else {
                    selectY = ( int )( y + selectHeight );
                }

                dwright.paint( g2D,selectX,selectY );

                // el calculo del arrastrar deberia ser como la linea punteada pero aplicando los cambios

                if( isDragged ) {
                    arrastrar.puntos( g2D,( changes.x - 1 ),changes.y,changes.width,changes.height );
                }
            } else {
                if( isMoveOver ) {
                    linea.puntos( g2D,x - 1,y,width,height );    // la coordenada X es x-1,para que la linea punteada no se sobreescriba a la primera letra
                }

                isMoveOver = false;
            }
        } else {
            String type = m_item.getShapeType();

            if( type == null ) {
                type = "";
            }

            if( m_item.isFilledRectangle()) {
                if( type.equals( MPrintFormatItem.SHAPETYPE_3DRectangle )) {
                    g2D.fill3DRect( x,y,width,height,true );
                } else if( type.equals( MPrintFormatItem.SHAPETYPE_Oval )) {
                    g2D.fillOval( x,y,width,height );
                } else if( type.equals( MPrintFormatItem.SHAPETYPE_RoundRectangle )) {
                    g2D.fillRoundRect( x,y,width,height,m_item.getArcDiameter(),m_item.getArcDiameter());
                } else {
                    g2D.fillRect( x,y,width,height );
                }
            } else {
                if( type.equals( MPrintFormatItem.SHAPETYPE_3DRectangle )) {
                    g2D.draw3DRect( x,y,width,height,true );
                } else if( type.equals( MPrintFormatItem.SHAPETYPE_Oval )) {
                    g2D.drawOval( x,y,width,height );
                } else if( type.equals( MPrintFormatItem.SHAPETYPE_RoundRectangle )) {
                    g2D.drawRoundRect( x,y,width,height,m_item.getArcDiameter(),m_item.getArcDiameter());
                } else {
                    g2D.drawRect( x,y,width,height );
                }
            }
        }
    }    // paint

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Descripción de Método
     *
     *
     * @param isSelected
     */

    public void setSelected( boolean isSelected ) {
        this.isSelected = isSelected;
    }

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

    /**
     * Descripción de Método
     *
     */

    public void setDimension() {

        // if ( (pfItem.getMaxHeight() + (changes.height - bounds.height))>=15)

        pfItem.setMaxHeight( changes.height );
        pfItem.setMaxWidth( changes.width );
        pfItem.save();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDragged() {
        return isDragged;
    }

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
        } else if( dwright.isCornerClicked( x,y )) {
            m_cornerClicked = CornerElement.DOWNRIGHT;
        } else {
            m_cornerClicked = 0;
            isChangedSize   = false;
        }

        return( isChangedSize );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getCornerClicked() {
        return m_cornerClicked;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isChangedSize() {
        return isChangedSize;
    }

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
            if( (pfItem.getMaxHeight() > 0) && (pfItem.getMaxWidth() > 0) ) {
                lineDownRight = true;
                lineDownLeft  = false;
                lineUpRight   = false;
                lineUpLeft    = false;
            }

            if( (pfItem.getMaxHeight() > 0) && (pfItem.getMaxWidth() < 0) ) {
                lineDownRight = false;
                lineDownLeft  = true;

                // lineUpRight = false;
                // lineUpLeft = false;

            }

            if( (pfItem.getMaxHeight() < 0) && (pfItem.getMaxWidth() > 0) ) {
                lineDownRight = false;

                // lineDownLeft = false;

                lineUpRight = true;

                // lineUpLeft = false;

            }

            if( lineUpRight && lineDownLeft ) {
                lineDownRight = false;
                lineDownLeft  = false;
                lineUpRight   = false;
                lineUpLeft    = true;
            }

            if( lineDownRight ) {
                if( getCornerClicked() == CornerElement.TOPLEFT ) {
                    changes = new Rectangle( bounds.x + x,bounds.y + y,bounds.width - x,bounds.height - y );
                } else {
                    changes = new Rectangle( bounds.x,bounds.y,bounds.width + x,bounds.height + y );
                }
            }

            if( lineDownLeft ) {
                if( getCornerClicked() == CornerElement.TOPLEFT ) {
                    changes = new Rectangle( bounds.x,bounds.y + y,bounds.width + x,bounds.height - y );
                } else {
                    changes = new Rectangle( bounds.x + x,bounds.y,bounds.width - x,bounds.height + y );
                }
            }

            if( lineUpRight ) {
                if( getCornerClicked() == CornerElement.TOPLEFT ) {
                    changes = new Rectangle( bounds.x + x,bounds.y,bounds.width - x,bounds.height + y );
                } else {
                    changes = new Rectangle( bounds.x,bounds.y + y,bounds.width + x,bounds.height - y );
                }
            }

            if( lineUpLeft ) {
                if( getCornerClicked() == CornerElement.TOPLEFT ) {
                    changes = new Rectangle( bounds.x,bounds.y,bounds.width + x,bounds.height + y );
                } else {
                    changes = new Rectangle( bounds.x + x,bounds.y + y,bounds.width - x,bounds.height - y );
                }
            }
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPrintFormatItemID() {
        return pfItem.getAD_PrintFormatItem_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isRelativePosition() {
        return pfItem.isRelativePosition();
    }

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

        if( m_item.getPrintFormatType().equals( MPrintFormatItem.PRINTFORMATTYPE_Line )) {
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
        } else {
            CLabel labelShapeType = new CLabel( "Tipo de Forma:" );

            fields.add( labelShapeType );

            VLookup shapeType;
            MLookup m_shapeType = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),333 ),0 );

            shapeType = new VLookup( "ShapeType",true,false,true,m_shapeType );
            shapeType.setValue( new String( pfItem.getShapeType()));
            shapeType.setSize( 160,24 );
            shapeType.setMaximumSize( shapeType.getSize());
            shapeType.setPreferredSize( shapeType.getSize());
            fields.add( shapeType );
        }

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

        // Cuarta barra de herramientas

        pos = new String( "Cuatro" );
        fields.add( pos );

        try {
            CLabel labelPrintFont = new CLabel( "Fuente de impresi�n:" );

            fields.add( labelPrintFont );

            VLookup printFont;
            MLookup m_printFont = MLookupFactory.get( Env.getCtx(),0,6963,19,Env.getLanguage( Env.getCtx()),"AD_PrintFont_ID",267,false,"" );

            printFont = new VLookup( "AD_PrintFont_ID",false,false,true,m_printFont );
            printFont.setValue( new Integer( pfItem.getAD_PrintFont_ID()));
            printFont.setSize( 150,24 );
            printFont.setMaximumSize( printFont.getSize());
            printFont.setPreferredSize( printFont.getSize());
            fields.add( printFont );
        } catch( Exception e ) {
        }

        try {
            CLabel labelPrintColor = new CLabel( "Color de impresi�n:" );

            fields.add( labelPrintColor );

            // CComboBox printColor = new CComboBox();

            VLookup printColor;
            MLookup m_printColor = MLookupFactory.get( Env.getCtx(),0,6958,19,Env.getLanguage( Env.getCtx()),"AD_PrintColor_ID",266,false,"" );

            printColor = new VLookup( "AD_PrintColor_ID",false,false,true,m_printColor );
            printColor.setValue( new Integer( pfItem.getAD_PrintColor_ID()));
            printColor.setSize( 150,24 );
            printColor.setMaximumSize( printColor.getSize());
            printColor.setPreferredSize( printColor.getSize());
            fields.add( printColor );
        } catch( Exception e ) {
        }

        CLabel labelLineWidth = new CLabel( "Grosor de l�nea:" );

        fields.add( labelLineWidth );

        VNumber lineWidth = new VNumber( "LineWidth",true,false,true,DisplayType.Integer,"Grosor de l�nea" );

        lineWidth.setValue( new Integer( pfItem.getLineWidth()));
        lineWidth.setSize( 60,20 );
        lineWidth.setMaximumSize( lineWidth.getSize());
        lineWidth.setPreferredSize( lineWidth.getSize());
        fields.add( lineWidth );

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
}    // BoxElement



/*
 *  @(#)BoxDesignElement.java   02.07.07
 * 
 *  Fin del fichero BoxDesignElement.java
 *  
 *  Versión 2.2
 *
 */
