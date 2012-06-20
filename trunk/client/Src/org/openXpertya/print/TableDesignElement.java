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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.compiere.swing.CCheckBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MQuery;
import org.openXpertya.print.layout.Dimension2DImpl;
import org.openXpertya.print.layout.HTMLElement;
import org.openXpertya.print.layout.HTMLRenderer;
import org.openXpertya.print.layout.PrintElement;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.NamePair;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TableDesignElement extends PrintElement implements ActionListener {

    /** Descripción de Campos */

    public static final String PROXIMALINEA = "Proxima Linea";

    /** Descripción de Campos */

    public static final String PROXIMAPAGINA = "Proxima Pagina";

    /** Descripción de Campos */

    public static final String ANCHOFIJO = "Ancho Fijo";

    /** Descripción de Campos */

    public static final String UNALINEA = "Una Linea";

    /** Descripción de Campos */

    public static Image IMAGE_TRUE = Env.getImage( "true10.gif" );

    /** Descripción de Campos */

    public static Image IMAGE_FALSE = Env.getImage( "false10.gif" );

    /** Descripción de Campos */

    public static ImageIcon ICON_TRUE = Env.getImageIcon( "true10.gif" );

    /** Descripción de Campos */

    public static ImageIcon ICON_FALSE = Env.getImageIcon( "false10.gif" );

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
     * @param columnHeader
     * @param columnMaxWidth
     * @param columnMaxHeight
     * @param columnJustification
     * @param fixedWidth
     * @param functionRows
     * @param multiLineHeader
     * @param data
     * @param pk
     * @param pkColumnName
     * @param pageNoStart
     * @param firstPage
     * @param nextPages
     * @param repeatedColumns
     * @param additionalLines
     * @param rowColFont
     * @param rowColColor
     * @param rowColBackground
     * @param tFormat
     * @param pageBreak
     * @param dataItems
     */

    public TableDesignElement( ValueNamePair[] columnHeader,int[] columnMaxWidth,int[] columnMaxHeight,String[] columnJustification,boolean[] fixedWidth,ArrayList functionRows,boolean multiLineHeader,Object[][] data,KeyNamePair[] pk,String pkColumnName,int pageNoStart,Rectangle firstPage,Rectangle nextPages,int repeatedColumns,HashMap additionalLines,HashMap rowColFont,HashMap rowColColor,HashMap rowColBackground,MPrintTableFormat tFormat,ArrayList pageBreak,MPrintFormatItem[] dataItems ) {
        super();
        log.fine( "Cols=" + columnHeader.length + ", Rows=" + data.length );
        m_columnHeader        = columnHeader;
        m_columnMaxWidth      = columnMaxWidth;
        m_columnMaxHeight     = columnMaxHeight;
        m_columnJustification = columnJustification;
        m_functionRows        = functionRows;
        m_fixedWidth          = fixedWidth;

        //

        m_multiLineHeader = multiLineHeader;
        m_data            = data;
        m_dataItems       = dataItems;
        m_pk              = pk;
        m_pkColumnName    = pkColumnName;

        //

        m_pageNoStart     = pageNoStart;
        m_firstPage       = firstPage;
        m_nextPages       = nextPages;
        m_repeatedColumns = repeatedColumns;
        m_additionalLines = additionalLines;

        // Used Fonts,Colots

        Point pAll = new Point( ALL,ALL );

        m_rowColFont = rowColFont;
        m_baseFont   = ( Font )m_rowColFont.get( pAll );

        if( m_baseFont == null ) {
            m_baseFont = new Font( null );
        }

        m_rowColColor = rowColColor;
        m_baseColor   = ( Color )m_rowColColor.get( pAll );

        if( m_baseColor == null ) {
            m_baseColor = Color.black;
        }

        m_rowColBackground = rowColBackground;
        m_baseBackground   = ( Color )m_rowColBackground.get( pAll );

        if( m_baseBackground == null ) {
            m_baseBackground = Color.white;
        }

        m_tFormat = tFormat;

        // Page Break - not two after each other

        m_pageBreak = pageBreak;

        for( int i = 0;i < m_pageBreak.size();i++ ) {
            Integer row = ( Integer )m_pageBreak.get( i );

            while(( i + 1 ) < m_pageBreak.size()) {
                Integer nextRow = ( Integer )m_pageBreak.get( i + 1 );

                if(( row.intValue() + 1 ) == nextRow.intValue()) {
                    log.fine( "- removing PageBreak row=" + row );
                    m_pageBreak.remove( i );
                    row = nextRow;
                } else {
                    break;
                }
            }
        }    // for all page breaks

        // Load Image

        waitForLoad( LayoutDesign.IMAGE_TRUE );
        waitForLoad( LayoutDesign.IMAGE_FALSE );
    }    // TableElement

    /** Descripción de Campos */

    private boolean isOneSelected = false;

    /** Descripción de Campos */

    private boolean isPrevSelected = false;

    /** Descripción de Campos */

    private int addwidth = 0;

    /** Descripción de Campos */

    private int tableHeight;

    /** Descripción de Campos */

    private int maxTableHeight;

    /** Descripción de Campos */

    private int tableWidth;

    /** Descripción de Campos */

    private ArrayList m_tableWidth = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_tableHeight = new ArrayList();

    /** Descripción de Campos */

    private int leftSelected = 0;

    /** Descripción de Campos */

    private int leftPrevSelected = 0;

    /** Descripción de Campos */

    private int rightSelected = 0;

    /** Descripción de Campos */

    private int rightNextSelected = 0;

    /** Descripción de Campos */

    private int MARGIN = 0;

    /** Descripción de Campos */

    private boolean isLeft = false;

    /** Descripción de Campos */

    private int posXclick = 0;

    /** Descripción de Campos */

    private int posPage = 0;

    /** Descripción de Campos */

    private int leftLine = 0;;

    /** Descripción de Campos */

    private int rightLine = 0;

    /** Descripción de Campos */

    private int rowSelected = 0;

    /** Descripción de Campos */

    private int colSelected = 0;

    /** Descripción de Campos */

    private MPrintFormatItem[] m_dataItems;

    /** Descripción de Campos */

    static final String ACTIVO = "Activo";

    /** Descripción de Campos */

    static final String IMPRESO = "Impreso";

    /** Descripción de Campos */

    static final String SUPRIMIRNULOS = "Suprimir Nulos";

    /** Descripción de Campos */

    private ViewDesign m_viewDesign = null;

    /** Descripción de Campos */

    final static float dash[] = { 5.0f };

    /** Descripción de Campos */

    final static BasicStroke dashed = new BasicStroke( 5.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f,dash,0.0f );

    /** Descripción de Campos */

    private ValueNamePair[] m_columnHeader;

    /** Descripción de Campos */

    private int[] m_columnMaxWidth;

    /** Descripción de Campos */

    private int[] m_columnMaxHeight;

    /** Descripción de Campos */

    private String[] m_columnJustification;

    /** Descripción de Campos */

    private boolean[] m_fixedWidth;

    /** Descripción de Campos */

    private boolean m_multiLineHeader;

    /** Descripción de Campos */

    private ArrayList m_functionRows;

    /** Descripción de Campos */

    private Object[][] m_data;

    /** Descripción de Campos */

    private KeyNamePair[] m_pk;

    /** Descripción de Campos */

    private String m_pkColumnName;

    /** Descripción de Campos */

    private int m_pageNoStart;

    /** Descripción de Campos */

    private Rectangle m_firstPage;

    /** Descripción de Campos */

    private Rectangle m_nextPages;

    /** Descripción de Campos */

    private int m_repeatedColumns;

    /** Descripción de Campos */

    private Font m_baseFont;

    /** Descripción de Campos */

    private HashMap m_rowColFont;

    /** Descripción de Campos */

    private Color m_baseColor;

    /** Descripción de Campos */

    private HashMap m_rowColColor;

    /** Descripción de Campos */

    private Color m_baseBackground;

    /** Descripción de Campos */

    private HashMap m_rowColBackground;

    /** Descripción de Campos */

    private MPrintTableFormat m_tFormat;

    /** Descripción de Campos */

    private ArrayList m_pageBreak;

    /** Descripción de Campos */

    private ArrayList m_columnWidths = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_rowHeights = new ArrayList();

    /** Descripción de Campos */

    private int m_headerHeight = 0;

    /** Descripción de Campos */

    private ArrayList m_firstRowOnPage = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_firstColumnOnPage = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_pageHeight = new ArrayList();

    /** Descripción de Campos */

    private HashMap m_rowColDrillDown = new HashMap();

    /** Descripción de Campos */

    private HashMap m_additionalLines = new HashMap();

    /** Descripción de Campos */

    private HashMap m_additionalLineData = new HashMap();

    /** Descripción de Campos */

    public static final int HEADER_ROW = -2;

    /** Descripción de Campos */

    public static final int ALL = -1;

    /** Descripción de Campos */

    private static final int H_GAP = 2;

    /** Descripción de Campos */

    private static final int V_GAP = 2;

    /** Descripción de Campos */

    private static final boolean DEBUG_PRINT = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean calculateSize() {
        if( p_sizeCalculated ) {
            return true;
        }

        p_width              = 0;
        m_additionalLineData = new HashMap();    // reset

        // Max Column Width = 50% of available width (used if maxWidth not set)

        float dynMxColumnWidth = m_firstPage.width / 2;

        // Width caolculation

        int rows = m_data.length;
        int cols = m_columnHeader.length;

        // Data Sizes and Header Sizes

        Dimension2DImpl[][] dataSizes   = new Dimension2DImpl[ rows ][ cols ];
        Dimension2DImpl[]   headerSizes = new Dimension2DImpl[ cols ];
        FontRenderContext   frc         = new FontRenderContext( null,true,true );

        // data rows

        for( int dataCol = 0;dataCol < cols;dataCol++ ) {
            int col = dataCol;

            // Print below existing column

            if( m_additionalLines.containsKey( new Integer( dataCol ))) {
                col = (( Integer )m_additionalLines.get( new Integer( dataCol ))).intValue();
                log.finest( "DataColumn=" + dataCol + ", BelowColumn=" + col );
            }

            float colWidth = 0;

            for( int row = 0;row < rows;row++ ) {
                Object dataItem = m_data[ row ][ dataCol ];

                if( dataItem == null ) {
                    dataSizes[ row ][ dataCol ] = new Dimension2DImpl();

                    continue;
                }

                String string = dataItem.toString();

                if( string.length() == 0 ) {
                    dataSizes[ row ][ dataCol ] = new Dimension2DImpl();

                    continue;
                }

                Font font = getFont( row,dataCol );

                // Print below existing column

                if( col != dataCol ) {
                    addAdditionalLines( row,col,dataItem );
                    dataSizes[ row ][ dataCol ] = new Dimension2DImpl();    // don't print
                } else {
                    dataSizes[ row ][ dataCol ] = new Dimension2DImpl();
                }

                if( dataItem instanceof Boolean ) {
                    dataSizes[ row ][ col ].addBelow( LayoutDesign.IMAGE_SIZE );

                    continue;
                } else if( dataItem instanceof ImageDesElement ) {
                    dataSizes[ row ][ col ].addBelow( new Dimension(( int )(( ImageDesElement )dataItem ).getWidth(),( int )(( ImageDesElement )dataItem ).getHeight()));

                    continue;
                }

                // No Width Limitations

                if( (m_columnMaxWidth[ col ] == 0) || (m_columnMaxWidth[ col ] == -1) ) {

                    // if (HTMLElement.isHTML(string))
                    // log.finest( "HTML (no) r=" + row + ",c=" + dataCol);

                    TextLayout layout = new TextLayout( string,font,frc );
                    float      width  = layout.getAdvance() + 2;    // buffer
                    float      height = layout.getAscent() + layout.getDescent() + layout.getLeading();

                    if( width > dynMxColumnWidth ) {
                        m_columnMaxWidth[ col ] = ( int )Math.ceil( dynMxColumnWidth );
                    } else if( colWidth < width ) {
                        colWidth = width;
                    }

                    if( dataSizes[ row ][ col ] == null ) {
                        dataSizes[ row ][ col ] = new Dimension2DImpl();
                        log.log( Level.SEVERE,"calculateSize - No Size for r=" + row + ",c=" + col );
                    }

                    dataSizes[ row ][ col ].addBelow( width,height );
                }

                // Width limitations

                if( (m_columnMaxWidth[ col ] != 0) && (m_columnMaxWidth[ col ] != -1) ) {
                    float height = 0;

                    //

                    if( HTMLElement.isHTML( string )) {

                        // log.finest( "HTML (limit) r=" + row + ",c=" + dataCol);

                        HTMLRenderer renderer = HTMLRenderer.get( string );

                        colWidth = renderer.getWidth();

                        if( m_columnMaxHeight[ col ] == -1 ) {    // one line only
                            height = renderer.getHeightOneLine();
                        } else {
                            height = renderer.getHeight();
                        }

                        renderer.setAllocation(( int )colWidth,( int )height );

                        // log.finest( "calculateSize HTML - " + renderer.getAllocation());

                        m_data[ row ][ dataCol ] = renderer;    // replace for printing
                    } else {
                        String[] lines = Pattern.compile( "$",Pattern.MULTILINE ).split( string );

                        for( int lineNo = 0;lineNo < lines.length;lineNo++ ) {
                            AttributedString aString = new AttributedString( lines[ lineNo ] );

                            aString.addAttribute( TextAttribute.FONT,font );

                            AttributedCharacterIterator iter = aString.getIterator();
                            LineBreakMeasurer measurer = new LineBreakMeasurer( iter,frc );

                            while( measurer.getPosition() < iter.getEndIndex()) {
                                TextLayout layout = measurer.nextLayout( Math.abs( m_columnMaxWidth[ col ] ));
                                float width = layout.getAdvance();

                                if( colWidth < width ) {
                                    colWidth = width;
                                }

                                float lineHeight = layout.getAscent() + layout.getDescent() + layout.getLeading();

                                if( m_columnMaxHeight[ col ] == -1 )    // one line only
                                {
                                    height = lineHeight;

                                    break;
                                } else if( (m_columnMaxHeight[ col ] == 0) || ( height + lineHeight ) <= m_columnMaxHeight[ col ] ) {
                                    height += lineHeight;
                                }
                            }
                        }    // for all lines
                    }

                    if( m_fixedWidth[ col ] ) {
                        colWidth = Math.abs( m_columnMaxWidth[ col ] );
                    }

                    dataSizes[ row ][ col ].addBelow( colWidth,height );
                }

                dataSizes[ row ][ col ].roundUp();

                if( dataItem instanceof NamePair ) {
                    m_rowColDrillDown.put( new Point( row,col ),dataItem );
                }

                // System.out.println("Col=" + col + ", row=" + row + " => " + dataSizes[row][col] + " - ColWidth=" + colWidth);

            }    // for all data rows

            // Column Width  for Header

            String string = "";

            if( m_columnHeader[ dataCol ] != null ) {
                string = m_columnHeader[ dataCol ].toString();
            }

            // Print below existing column

            if( col != dataCol ) {
                headerSizes[ dataCol ] = new Dimension2DImpl();
            } else if( ((colWidth == 0) && (m_columnMaxWidth[ dataCol ] < 0    // suppress Null
                    )) || (string.length() == 0) ) {
                headerSizes[ dataCol ] = new Dimension2DImpl();
            } else {
                Font font = getFont( HEADER_ROW,dataCol );

                if( !font.isBold()) {
                    font = new Font( font.getName(),Font.BOLD,font.getSize());
                }

                // No Width Limitations

                if( (m_columnMaxWidth[ dataCol ] == 0) || (m_columnMaxWidth[ dataCol ] == -1) ||!m_multiLineHeader ) {
                    TextLayout layout = new TextLayout( string,font,frc );
                    float      width  = layout.getAdvance() + 3;    // buffer
                    float      height = layout.getAscent() + layout.getDescent() + layout.getLeading();

                    if( width > dynMxColumnWidth ) {
                        m_columnMaxWidth[ dataCol ] = ( int )Math.ceil( dynMxColumnWidth );
                    } else if( colWidth < width ) {
                        colWidth = width;
                    }

                    headerSizes[ dataCol ] = new Dimension2DImpl( width,height );
                }

                // Width limitations

                if( (m_columnMaxWidth[ dataCol ] != 0) && (m_columnMaxWidth[ dataCol ] != -1) ) {
                    float height = 0;

                    //

                    String[] lines = Pattern.compile( "$",Pattern.MULTILINE ).split( string );

                    for( int lineNo = 0;lineNo < lines.length;lineNo++ ) {
                        AttributedString aString = new AttributedString( lines[ lineNo ] );

                        aString.addAttribute( TextAttribute.FONT,font );

                        AttributedCharacterIterator iter = aString.getIterator();
                        LineBreakMeasurer measurer = new LineBreakMeasurer( iter,frc );

                        colWidth = Math.abs( m_columnMaxWidth[ dataCol ] );

                        while( measurer.getPosition() < iter.getEndIndex()) {
                            TextLayout layout = measurer.nextLayout( colWidth );
                            float lineHeight = layout.getAscent() + layout.getDescent() + layout.getLeading();

                            if( !m_multiLineHeader )    // one line only
                            {
                                height = lineHeight;

                                break;
                            } else if( (m_columnMaxHeight[ dataCol ] == 0) || ( height + lineHeight ) <= m_columnMaxHeight[ dataCol ] ) {
                                height += lineHeight;
                            }
                        }
                    }    // for all header lines

                    headerSizes[ dataCol ] = new Dimension2DImpl( colWidth,height );
                }
            }    // headerSize

            headerSizes[ dataCol ].roundUp();
            colWidth = ( float )Math.ceil( colWidth );

            // System.out.println("Col=" + dataCol + " => " + headerSizes[dataCol]);

            // Round Column Width

            if( dataCol == 0 ) {
                colWidth += m_tFormat.getVLineStroke().floatValue();
            }

            if( colWidth != 0 ) {
                colWidth += ( 2 * H_GAP ) + m_tFormat.getVLineStroke().floatValue();
            }

            // Print below existing column

            if( col != dataCol ) {
                m_columnWidths.add( new Float( 0.0 ));    // for the data column

                Float origWidth = ( Float )m_columnWidths.get( col );

                if( origWidth == null ) {
                    log.log( Level.SEVERE,"calculateSize - Column " + dataCol + " below " + col + " - no value for orig width" );
                } else {
                    if( origWidth.compareTo( new Float( colWidth )) >= 0 ) {
                        log.finest( "Same Width - Col=" + col + " - OrigWidth=" + origWidth + " - Width=" + colWidth + " - Total=" + p_width );
                    } else {
                        m_columnWidths.set( col,new Float( colWidth ));
                        p_width += ( colWidth - origWidth.floatValue());
                        log.finest( "New Width - Col=" + col + " - OrigWidth=" + origWidth + " - Width=" + colWidth + " - Total=" + p_width );
                    }
                }
            }

            // Add new Column

            else {
                m_columnWidths.add( new Float( colWidth ));
                p_width += colWidth;
                log.finest( "Width - Col=" + dataCol + " - Width=" + colWidth + " - Total=" + p_width );
            }
        }    // for all columns

        // Height  **********

        p_height = 0;

        for( int row = 0;row < rows;row++ ) {
            float rowHeight = 0f;

            for( int col = 0;col < cols;col++ ) {
                if( dataSizes[ row ][ col ].height > rowHeight ) {
                    rowHeight = ( float )dataSizes[ row ][ col ].height;
                }
            }    // for all columns

            rowHeight += m_tFormat.getLineStroke().floatValue() + ( 2 * V_GAP );
            m_rowHeights.add( new Float( rowHeight ));
            p_height += rowHeight;
        }    // for all rows

        // HeaderRow

        m_headerHeight = 0;

        for( int col = 0;col < cols;col++ ) {
            if( headerSizes[ col ].height > m_headerHeight ) {
                m_headerHeight = ( int )headerSizes[ col ].height;
            }
        }    // for all columns

        m_headerHeight += ( 4 * m_tFormat.getLineStroke().floatValue()) + ( 2 * V_GAP );    // Thick lines
        p_height += m_headerHeight;

        // Page Layout     *******************************************************

        log.fine( "FirstPage=" + m_firstPage + ", NextPages=" + m_nextPages );

        // One Page on Y | Axis

        if( (m_firstPage.height >= p_height) && (m_pageBreak.size() == 0) ) {
            log.finest( "Page Y=1 - PageHeight=" + m_firstPage.height + " - TableHeight=" + p_height );
            m_firstRowOnPage.add( new Integer( 0 ));     // Y
            m_pageHeight.add( new Float( p_height ));    // Y index only
        }

        // multiple pages on Y | Axis

        else {
            float   availableHeight = 0f;
            float   usedHeight      = 0f;
            boolean firstPage       = true;

            // for all rows

            for( int row = 0;row < m_rowHeights.size();row++ ) {
                float rowHeight = (( Float )m_rowHeights.get( row )).floatValue();

                // Y page break

                boolean forcePageBreak = isPageBreak( row );

                if( (availableHeight < rowHeight) || forcePageBreak ) {
                    availableHeight = firstPage
                                      ?m_firstPage.height
                                      :m_nextPages.height;
                    m_firstRowOnPage.add( new Integer( row ));    // Y
                    log.finest( "Page Y=" + m_firstRowOnPage.size() + " - Row=" + row + " - force=" + forcePageBreak );

                    if( !firstPage ) {
                        m_pageHeight.add( new Float( usedHeight ));    // Y index only
                        log.finest( "Page Y=" + m_pageHeight.size() + " - PageHeight=" + usedHeight );
                    }

                    firstPage = false;

                    //

                    availableHeight -= m_headerHeight;
                    usedHeight      += m_headerHeight;
                }

                availableHeight -= rowHeight;
                usedHeight      += rowHeight;
            }                                              // for all rows

            m_pageHeight.add( new Float( usedHeight ));    // Y index only
            log.finest( "Page Y=" + m_pageHeight.size() + " - PageHeight=" + usedHeight );
        }    // multiple Y | pages

        // One page on - X Axis

        if( m_firstPage.width >= p_width ) {
            log.finest( "Page X=1 - PageWidth=" + m_firstPage.width + " - TableWidth=" + p_width );
            m_firstColumnOnPage.add( new Integer( 0 ));    // X

            //

            distributeColumns( m_firstPage.width - ( int )p_width,0,m_columnWidths.size());
        }

        // multiple pages on - X Axis

        else {
            int availableWidth = 0;
            int lastStart      = 0;

            for( int col = 0;col < m_columnWidths.size();col++ ) {
                int columnWidth = (( Float )m_columnWidths.get( col )).intValue();

                // X page preak

                if( availableWidth < columnWidth ) {
                    if( col != 0 ) {
                        distributeColumns( availableWidth,lastStart,col );
                    }

                    //

                    m_firstColumnOnPage.add( new Integer( col ));    // X
                    log.finest( "Page X=" + m_firstColumnOnPage.size() + " - Col=" + col );
                    lastStart      = col;
                    availableWidth = m_firstPage.width;    // Width is the same on all pages

                    //

                    for( int repCol = 0;(repCol < m_repeatedColumns) && (col > repCol);repCol++ ) {
                        float repColumnWidth = (( Float )m_columnWidths.get( repCol )).floatValue();

                        // leave 50% of space available for non repeated columns

                        if( availableWidth < m_firstPage.width * 0.5 ) {
                            break;
                        }

                        availableWidth -= repColumnWidth;
                    }
                }    // pageBreak

                availableWidth -= columnWidth;
            }        // for acc columns
        }            // multiple - X pages

        // Last row Lines

        p_height += m_tFormat.getLineStroke().floatValue();    // last fat line
        log.fine( "Pages=" + getPageCount() + " X=" + m_firstColumnOnPage.size() + "/Y=" + m_firstRowOnPage.size() + " - Width=" + p_width + ", Height=" + p_height );

        return true;
    }    // calculateSize

    /**
     * Descripción de Método
     *
     *
     * @param availableWidth
     * @param fromCol
     * @param toCol
     */

    private void distributeColumns( int availableWidth,int fromCol,int toCol ) {
        log.finest( "Available=" + availableWidth + ", Columns " + fromCol + "->" + toCol );

        int start = fromCol;

        if( (fromCol == 0) && (m_repeatedColumns > 0) ) {
            start = m_repeatedColumns;
        }

        // calculate total Width

        int totalWidth = availableWidth;

        for( int col = start;col < toCol;col++ ) {
            totalWidth += (( Float )m_columnWidths.get( col )).floatValue();
        }

        int remainingWidth = availableWidth;
    }    // distribute Columns

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    private boolean isPageBreak( int row ) {
        for( int i = 0;i < m_pageBreak.size();i++ ) {
            Integer rr = ( Integer )m_pageBreak.get( i );

            if( rr.intValue() + 1 == row ) {
                return true;
            } else if( rr.intValue() > row ) {
                return false;
            }
        }

        return false;
    }    // isPageBreak

    /**
     * Descripción de Método
     *
     */

    public void setHeightToLastPage() {
        int lastLayoutPage = getPageCount() + m_pageNoStart - 1;

        log.fine( "PageCount - Table=" + getPageCount() + "(Start=" + m_pageNoStart + ") Layout=" + lastLayoutPage + " - Old Height=" + p_height );
        p_height = getHeight( lastLayoutPage );
        log.fine( "New Height=" + p_height );
    }    // setHeightToLastPage

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param col
     *
     * @return
     */

    private Font getFont( int row,int col ) {

        // First specific position

        Font font = ( Font )m_rowColFont.get( new Point( row,col ));

        if( font != null ) {
            return font;
        }

        // Row Next

        font = ( Font )m_rowColFont.get( new Point( row,ALL ));

        if( font != null ) {
            return font;
        }

        // Column then

        font = ( Font )m_rowColFont.get( new Point( ALL,col ));

        if( font != null ) {
            return font;
        }

        // default

        return m_baseFont;
    }    // getFont

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param col
     *
     * @return
     */

    private Color getColor( int row,int col ) {

        // First specific position

        Color color = ( Color )m_rowColColor.get( new Point( row,col ));

        if( color != null ) {
            return color;
        }

        // Row Next

        color = ( Color )m_rowColColor.get( new Point( row,ALL ));

        if( color != null ) {
            return color;
        }

        // Column then

        color = ( Color )m_rowColColor.get( new Point( ALL,col ));

        if( color != null ) {
            return color;
        }

        // default

        return m_baseColor;
    }    // getFont

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param col
     *
     * @return
     */

    private Color getBackground( int row,int col ) {

        // First specific position

        Color color = ( Color )m_rowColBackground.get( new Point( row,col ));

        if( color != null ) {
            return color;
        }

        // Row Next

        color = ( Color )m_rowColBackground.get( new Point( row,ALL ));

        if( color != null ) {
            return color;
        }

        // Column then

        color = ( Color )m_rowColBackground.get( new Point( ALL,col ));

        if( color != null ) {
            return color;
        }

        // default

        return m_baseBackground;
    }    // getFont

    /**
     * Descripción de Método
     *
     *
     * @param pageNo
     *
     * @return
     */

    public float getHeight( int pageNo ) {
        int pageIndex  = getPageIndex( pageNo );
        int pageYindex = getPageYIndex( pageIndex );

        log.fine( "Page=" + pageNo + " - PageIndex=" + pageIndex + ", PageYindex=" + pageYindex );

        float pageHeight = (( Float )m_pageHeight.get( pageYindex )).floatValue();
        float pageHeightPrevious = 0f;

        if( pageYindex > 0 ) {
            pageHeightPrevious = (( Float )m_pageHeight.get( pageYindex - 1 )).floatValue();
        }

        float retValue = pageHeight - pageHeightPrevious;

        log.fine( "Page=" + pageNo + " - PageIndex=" + pageIndex + ", PageYindex=" + pageYindex + ", Height=" + String.valueOf( retValue ));

        return retValue;
    }    // getHeight

    /**
     * Descripción de Método
     *
     *
     * @param pageNo
     *
     * @return
     */

    public float getWidth( int pageNo ) {
        int pageIndex = getPageIndex( pageNo );

        if( pageIndex == 0 ) {
            return m_firstPage.width;
        }

        return m_nextPages.width;
    }    // getHeight

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPageCount() {
        return m_firstRowOnPage.size() * m_firstColumnOnPage.size();
    }    // getPageCount

    /**
     * Descripción de Método
     *
     *
     * @param pageNo
     *
     * @return
     */

    protected int getPageIndex( int pageNo ) {
        int index = pageNo - m_pageNoStart;

        if( index < 0 ) {
            log.log( Level.SEVERE,"index=" + index,new Exception());
        }

        return index;
    }    // getPageIndex

    /**
     * Descripción de Método
     *
     *
     * @param pageIndex
     *
     * @return
     */

    private int getPageNo( int pageIndex ) {
        return m_pageNoStart + pageIndex;
    }    // getPageNo

    /**
     * Descripción de Método
     *
     *
     * @param pageIndex
     *
     * @return
     */

    protected int getPageXIndex( int pageIndex ) {
        int noXpages = m_firstColumnOnPage.size();

        // int noYpages = m_firstRowOnPage.size();

        int x = pageIndex % noXpages;

        return x;
    }    // getPageXIndex

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected int getPageXCount() {
        return m_firstColumnOnPage.size();
    }    // getPageXCount

    /**
     * Descripción de Método
     *
     *
     * @param pageIndex
     *
     * @return
     */

    protected int getPageYIndex( int pageIndex ) {
        int noXpages = m_firstColumnOnPage.size();

        // int noYpages = m_firstRowOnPage.size();

        int y = ( pageIndex - ( pageIndex % noXpages )) / noXpages;

        return y;
    }    // getPageYIndex

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected int getPageYCount() {
        return m_firstRowOnPage.size();
    }    // getPageYCount

    /**
     * Descripción de Método
     *
     *
     * @param relativePoint
     * @param pageNo
     *
     * @return
     */

    public MQuery getDrillDown( Point relativePoint,int pageNo ) {
        if( m_rowColDrillDown.size() == 0 ) {
            return null;
        }

        if( !getBounds( pageNo ).contains( relativePoint )) {
            return null;
        }

        int row = getRow( relativePoint.y,pageNo );

        if( row == -1 ) {
            return null;
        }

        int col = getCol( relativePoint.x,pageNo );

        if( col == -1 ) {
            return null;
        }

        log.fine( "Row=" + row + ", Col=" + col + ", PageNo=" + pageNo );

        //

        NamePair pp = ( NamePair )m_rowColDrillDown.get( new Point( row,col ));

        if( pp == null ) {
            return null;
        }

        String columnName = MQuery.getZoomColumnName( m_columnHeader[ col ].getID());
        String tableName = MQuery.getZoomTableName( columnName );
        Object code      = pp.getID();

        if( pp instanceof KeyNamePair ) {
            code = new Integer((( KeyNamePair )pp ).getKey());
        }

        //

        MQuery query = new MQuery( tableName );

        query.addRestriction( columnName,MQuery.EQUAL,code,null,pp.toString());

        return query;
    }    // getDrillDown

    /**
     * Descripción de Método
     *
     *
     * @param relativePoint
     * @param pageNo
     *
     * @return
     */

    public MQuery getDrillAcross( Point relativePoint,int pageNo ) {
        if( !getBounds( pageNo ).contains( relativePoint )) {
            return null;
        }

        int row = getRow( relativePoint.y,pageNo );

        if( row == -1 ) {
            return null;
        }

        log.fine( "Row=" + row + ", PageNo=" + pageNo );

        //

        if( m_pk[ row ] == null ) {    // FunctionRows
            return null;
        }

        return MQuery.getEqualQuery( m_pkColumnName,m_pk[ row ].getKey());
    }    // getDrillAcross

    /**
     * Descripción de Método
     *
     *
     * @param pageNo
     *
     * @return
     */

    public Rectangle getBounds( int pageNo ) {
        int pageIndex  = getPageIndex( pageNo );
        int pageYindex = getPageYIndex( pageIndex );

        if( pageYindex == 0 ) {
            return m_firstPage;
        } else {
            return m_nextPages;
        }
    }    // getBounds

    /**
     * Descripción de Método
     *
     *
     * @param yPos
     * @param pageNo
     *
     * @return
     */

    private int getRow( int yPos,int pageNo ) {
        int pageIndex  = getPageIndex( pageNo );
        int pageYindex = getPageYIndex( pageIndex );

        //

        int curY = ( (pageYindex == 0)
                     ?m_firstPage.y
                     :m_nextPages.y ) + m_headerHeight;

        if( yPos < curY ) {
            return -1;    // above
        }

        //

        int firstRow = (( Integer )m_firstRowOnPage.get( pageYindex )).intValue();
        int nextPageRow = m_data.length;    // no of rows

        if( pageYindex + 1 < m_firstRowOnPage.size()) {
            nextPageRow = (( Integer )m_firstRowOnPage.get( pageYindex + 1 )).intValue();
        }

        //

        for( int row = firstRow;row < nextPageRow;row++ ) {
            int rowHeight = (( Float )m_rowHeights.get( row )).intValue();    // includes 2*Gaps+Line

            if( (yPos >= curY) && (yPos < ( curY + rowHeight ))) {
                return row;
            }

            curY += rowHeight;
        }

        // below

        return -1;
    }    // getRow

    /**
     * Descripción de Método
     *
     *
     * @param xPos
     * @param pageNo
     *
     * @return
     */

    private int getCol( int xPos,int pageNo ) {
        int pageIndex  = getPageIndex( pageNo );
        int pageXindex = getPageXIndex( pageIndex );

        //

        int curX = (pageXindex == 0)
                   ?m_firstPage.x
                   :m_nextPages.x;

        if( xPos < curX ) {
            return -1;    // too left
        }

        int firstColumn = (( Integer )m_firstColumnOnPage.get( pageXindex )).intValue();
        int nextPageColumn = m_columnHeader.length;    // no of cols

        if( pageXindex + 1 < m_firstColumnOnPage.size()) {
            nextPageColumn = (( Integer )m_firstColumnOnPage.get( pageXindex + 1 )).intValue();
        }

        // fixed volumns

        int regularColumnStart = firstColumn;

        for( int col = 0;col < m_repeatedColumns;col++ ) {
            int colWidth = (( Float )m_columnWidths.get( col )).intValue();    // includes 2*Gaps+Line

            if( (xPos >= curX) && (xPos < ( curX + colWidth ))) {
                return col;
            }

            curX += colWidth;

            if( regularColumnStart == col ) {
                regularColumnStart++;
            }
        }

        // regular columns

        for( int col = regularColumnStart;col < nextPageColumn;col++ ) {
            int colWidth = (( Float )m_columnWidths.get( col )).intValue();    // includes 2*Gaps+Line

            if( (xPos >= curX) && (xPos < ( curX + colWidth ))) {
                return col;
            }

            curX += colWidth;
        }    // for all columns

        // too right

        return -1;
    }    // getCol

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
        int pageIndex  = getPageIndex( pageNo );
        int pageXindex = getPageXIndex( pageIndex );
        int pageYindex = getPageYIndex( pageIndex );

        if( DEBUG_PRINT ) {
            log.config( "Page=" + pageNo + " [x=" + pageXindex + ", y=" + pageYindex + "]" );
        }

        //

        int firstColumn = (( Integer )m_firstColumnOnPage.get( pageXindex )).intValue();
        int nextPageColumn = m_columnHeader.length;    // no of cols

        if( pageXindex + 1 < m_firstColumnOnPage.size()) {
            nextPageColumn = (( Integer )m_firstColumnOnPage.get( pageXindex + 1 )).intValue();
        }

        //

        int firstRow = (( Integer )m_firstRowOnPage.get( pageYindex )).intValue();
        int nextPageRow = m_data.length;    // no of rows

        if( pageYindex + 1 < m_firstRowOnPage.size()) {
            nextPageRow = (( Integer )m_firstRowOnPage.get( pageYindex + 1 )).intValue();
        }

        if( DEBUG_PRINT ) {
            log.finest( "Col=" + firstColumn + "-" + ( nextPageColumn - 1 ) + ", Row=" + firstRow + "-" + ( nextPageRow - 1 ));
        }

        MARGIN = ( int )pageStart.getX();

        // Top Left

        int startX = ( int )pageStart.getX();
        int startY = ( int )pageStart.getY();

        // Table Start

        startX += (pageXindex == 0)
                  ?m_firstPage.x
                  :m_nextPages.x;
        startY += (pageYindex == 0)
                  ?m_firstPage.y
                  :m_nextPages.y;

        if( DEBUG_PRINT ) {
            log.finest( "PageStart=" + pageStart + ", StartTable x=" + startX + ", y=" + startY );
        }

        // paint first fixed volumns

        boolean firstColumnPrint   = true;
        int     regularColumnStart = firstColumn;
        boolean isSelected         = false;

        tableWidth     = 0;
        maxTableHeight = 0;

        for( int col = 0;(col < m_repeatedColumns) && (col < m_columnWidths.size());col++ ) {
            isSelected = false;

            int colWidth = (( Float )m_columnWidths.get( col )).intValue();    // includes 2*Gaps+Line

            if( colWidth != 0 ) {
                if((( posXclick >= startX ) && (posXclick < ( startX + colWidth ))) || ( isPrevSelected && (colSelected == col) ) ) {
                    isSelected     = true;
                    isPrevSelected = false;
                    isOneSelected  = true;
                }

                tableWidth        += colWidth;
                leftPrevSelected  = 0;
                leftSelected      = 0;
                rightSelected     = 0;
                rightNextSelected = 0;

                if( isOneSelected )    // && pageNo==posPage)
                {
                    if( isLeft ) {
                        if(( colSelected - 1 ) == col ) {
                            leftPrevSelected = addwidth;
                        }

                        if( colSelected == col ) {
                            leftSelected = addwidth;
                        }
                    } else {
                        if( colSelected == col ) {
                            rightSelected = addwidth;
                        }

                        if( colSelected < col ) {
                            rightNextSelected = addwidth;
                        }
                    }
                }

                printColumn( g2D,col,startX,startY,firstColumnPrint,firstRow,nextPageRow,isView,isSelected );
                startX           += colWidth;
                firstColumnPrint = false;
            }

            if( regularColumnStart == col ) {
                regularColumnStart++;
            }

            if( tableHeight > maxTableHeight ) {
                maxTableHeight = tableHeight;
            }
        }

        // paint columns

        for( int col = regularColumnStart;col < nextPageColumn;col++ ) {
            isSelected = false;

            int colWidth = (( Float )m_columnWidths.get( col )).intValue();    // includes 2*Gaps+Line

            if( colWidth != 0 ) {
                if(((( posXclick >= ( startX )) && ( posXclick < ( startX + colWidth ))) && ( pageNo == posPage )) || ( isPrevSelected && (colSelected == col) ) ) {
                    isSelected     = true;
                    isPrevSelected = false;
                    isOneSelected  = true;
                }

                tableWidth        += colWidth;
                leftPrevSelected  = 0;
                leftSelected      = 0;
                rightSelected     = 0;
                rightNextSelected = 0;

                if( isOneSelected && (( pageNo == posPage ) || ( colSelected == 0 ))) {
                    if( isLeft ) {
                        if(( colSelected - 1 ) == col ) {
                            leftPrevSelected = addwidth;
                        }

                        if( colSelected == col ) {
                            leftSelected = addwidth;
                        }
                    } else {
                        if( colSelected == col ) {
                            rightSelected = addwidth;
                        }

                        if( colSelected < col ) {
                            rightNextSelected = addwidth;
                        }
                    }
                }

                printColumn( g2D,col,startX,startY,firstColumnPrint,firstRow,nextPageRow,isView,isSelected );
                startX           += colWidth;
                firstColumnPrint = false;
            }

            if( tableHeight > maxTableHeight ) {
                maxTableHeight = tableHeight;
            }
        }    // for all columns

        Integer th = new Integer( maxTableHeight );
        Integer tw = new Integer( tableWidth );

        m_tableWidth.add( tw );
        m_tableHeight.add( th );

        if( !isOneSelected ) {
            isPrevSelected = false;
            addwidth       = 0;
            posXclick      = 0;
            posPage        = 0;
            leftLine       = 0;;
            rightLine      = 0;
            rowSelected    = 0;
            colSelected    = 0;
        }
    }    // paint

    /**
     * Descripción de Método
     *
     *
     * @param g2D
     * @param col
     * @param origX
     * @param origY
     * @param leftVline
     * @param firstRow
     * @param nextPageRow
     * @param isView
     * @param isSelected
     */

    private void printColumn( Graphics2D g2D,int col,final int origX,final int origY,boolean leftVline,final int firstRow,final int nextPageRow,boolean isView,boolean isSelected ) {
        int curX = origX;
        int curY = origY;    // start from top

        tableHeight = 0 - curY;

        //

        float colWidth = (( Float )m_columnWidths.get( col )).floatValue();    // includes 2*Gaps+Line
        float netWidth = colWidth - ( 2 * H_GAP ) - m_tFormat.getVLineStroke().floatValue();

        if( leftVline ) {
            netWidth -= m_tFormat.getVLineStroke().floatValue();
        }

        int   rowHeight = m_headerHeight;
        float netHeight = rowHeight - ( 4 * m_tFormat.getLineStroke().floatValue()) + ( 2 * V_GAP );

        if( DEBUG_PRINT ) {
            log.finer( "#" + col + " - x=" + curX + ", y=" + curY + ", width=" + colWidth + "/" + netWidth + ", HeaderHeight=" + rowHeight + "/" + netHeight );
        }

        String alignment = m_columnJustification[ col ];

        // paint header    ***************************************************

        if( leftVline || isSelected )                                 // draw left | line
        {
            g2D.setPaint( m_tFormat.getVLine_Color());
            g2D.setStroke( m_tFormat.getVLine_Stroke());

            if( isSelected ) {
                g2D.setStroke( dashed );
            }

            if( m_tFormat.isPaintBoundaryLines() || isSelected ) {    // -> | (left)
                g2D.drawLine(( leftSelected + origX ) + rightNextSelected,( int )( origY + m_tFormat.getLineStroke().floatValue()),( leftSelected + origX ) + rightNextSelected,( int )( origY + rowHeight - ( 4 * m_tFormat.getLineStroke().floatValue())));
            }

            curX += m_tFormat.getVLineStroke().floatValue();
        }

        // X - start line

        if( m_tFormat.isPaintHeaderLines()) {
            g2D.setPaint( m_tFormat.getHeaderLine_Color());
            g2D.setStroke( m_tFormat.getHeader_Stroke());

            if( isSelected ) {
                g2D.setStroke( dashed );
            }

            g2D.drawLine(( origX + leftSelected ) + rightNextSelected,origY,    // -> - (top)
                rightNextSelected + (( leftPrevSelected + ( int )( origX + colWidth - m_tFormat.getVLineStroke().floatValue())) + rightSelected ),origY );
        }

        curY += ( 2 * m_tFormat.getLineStroke().floatValue());    // thick

        // Background

        Color bg = getBackground( HEADER_ROW,col );

        if( !bg.equals( Color.white )) {
            g2D.setPaint( bg );
            g2D.fillRect( rightNextSelected + ( curX + leftSelected ),( int )( curY - m_tFormat.getLineStroke().floatValue()),rightSelected + (( leftPrevSelected + ( int )( colWidth - m_tFormat.getVLineStroke().floatValue())) - leftSelected ),( int )( rowHeight - ( 4 * m_tFormat.getLineStroke().floatValue())));
        }

        curX += H_GAP;    // upper left gap
        curY += V_GAP;

        // Header

        AttributedString            aString    = null;
        AttributedCharacterIterator iter       = null;
        LineBreakMeasurer           measurer   = null;
        float                       usedHeight = 0;

        if( m_columnHeader[ col ].toString().length() > 0 ) {
            aString = new AttributedString( m_columnHeader[ col ].toString());
            aString.addAttribute( TextAttribute.FONT,getFont( HEADER_ROW,col ));
            aString.addAttribute( TextAttribute.FOREGROUND,getColor( HEADER_ROW,col ));

            //

            boolean fastDraw = LayoutDesign.s_FASTDRAW;

            if( fastDraw &&!isView &&!Util.is8Bit( m_columnHeader[ col ].toString())) {
                fastDraw = false;
            }

            iter     = aString.getIterator();
            measurer = new LineBreakMeasurer( iter,g2D.getFontRenderContext());

            while( measurer.getPosition() < iter.getEndIndex())    // print header
            {
                TextLayout layout = measurer.nextLayout( netWidth + 2 );

                if( iter.getEndIndex() != measurer.getPosition()) {
                    fastDraw = false;
                }

                float lineHeight = layout.getAscent() + layout.getDescent() + layout.getLeading();

                if( (m_columnMaxHeight[ col ] <= 0    // -1 = FirstLineOnly
                        ) || ( usedHeight + lineHeight ) <= m_columnMaxHeight[ col ] ) {
                    if( alignment.equals( MPrintFormatItem.FIELDALIGNMENTTYPE_Block )) {
                        layout   = layout.getJustifiedLayout( netWidth + 2 );
                        fastDraw = false;
                    }

                    curY += layout.getAscent();

                    float penX = curX;

                    if( alignment.equals( MPrintFormatItem.FIELDALIGNMENTTYPE_Center )) {
                        penX += ( netWidth - layout.getAdvance()) / 2;
                    } else if(( alignment.equals( MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight ) && layout.isLeftToRight()) || ( alignment.equals( MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft ) &&!layout.isLeftToRight())) {
                        penX += netWidth - layout.getAdvance();
                    }

                    //

                    if( fastDraw ) {    // Bug - set Font/Color explicitly
                        g2D.setFont( getFont( HEADER_ROW,col ));
                        g2D.setColor( getColor( HEADER_ROW,col ));
                        g2D.drawString( iter,( leftSelected + penX ) + rightNextSelected,curY );
                    } else {
                        layout.draw( g2D,( leftSelected + penX ) + rightNextSelected,curY );    // -> text
                    }

                    curY       += layout.getDescent() + layout.getLeading();
                    usedHeight += lineHeight;
                }

                if( !m_multiLineHeader ) {    // one line only
                    break;
                }
            }
        }                                     // length > 0

        curX += netWidth + H_GAP;
        curY += V_GAP;

        // Y end line

        g2D.setPaint( m_tFormat.getVLine_Color());
        g2D.setStroke( m_tFormat.getVLine_Stroke());

        if( isSelected ) {
            g2D.setPaint( m_tFormat.getHLine_Color());
            g2D.setStroke( dashed );
            leftLine  = ( int )( curX - colWidth );
            rightLine = curX;

            if( m_tFormat.isPaintVLines()) {
                g2D.drawLine( leftSelected + ( int )( curX - colWidth ),( int )( origY + m_tFormat.getLineStroke().floatValue()),leftSelected + ( int )( curX - colWidth ),( int )( origY + rowHeight - ( 4 * m_tFormat.getLineStroke().floatValue())));
            }
        }

        if( m_tFormat.isPaintVLines() || isSelected ) {    // -> | (right)
            g2D.drawLine((( rightSelected + curX ) + rightNextSelected ) + leftPrevSelected,( int )( origY + m_tFormat.getLineStroke().floatValue()),(( rightSelected + curX ) + rightNextSelected ) + leftPrevSelected,( int )( origY + rowHeight - ( 4 * m_tFormat.getLineStroke().floatValue())));
        }

        curX += m_tFormat.getVLineStroke().floatValue();

        // X end line

        if( m_tFormat.isPaintHeaderLines()) {
            g2D.setPaint( m_tFormat.getHeaderLine_Color());
            g2D.setStroke( m_tFormat.getHeader_Stroke());

            if( isSelected ) {
                g2D.setStroke( dashed );
            }

            g2D.drawLine(( origX + leftSelected ) + rightNextSelected,curY,    // -> - (button)
                (( leftPrevSelected + ( int )( origX + colWidth - m_tFormat.getVLineStroke().floatValue())) + rightSelected ) + rightNextSelected,curY );
        }

        curY += ( 2 * m_tFormat.getLineStroke().floatValue());    // thick

        // paint Data              ***************************************************

        for( int row = firstRow;row < nextPageRow;row++ ) {
            rowHeight = (( Float )m_rowHeights.get( row )).intValue();    // includes 2*Gaps+Line
            netHeight = rowHeight - ( 2 * V_GAP ) - m_tFormat.getLineStroke().floatValue();

            int rowYstart = curY;

            curX = origX;

            if( leftVline )                                                                // draw left | line
            {
                g2D.setPaint( m_tFormat.getVLine_Color());
                g2D.setStroke( m_tFormat.getVLine_Stroke());

                if( isSelected ) {
                    g2D.setStroke( dashed );
                }

                if( m_tFormat.isPaintBoundaryLines()) {
                    g2D.drawLine(( leftSelected + curX ) + rightNextSelected,rowYstart,    // -> | (left)
                        ( leftSelected + curX ) + rightNextSelected,( int )( rowYstart + rowHeight - m_tFormat.getLineStroke().floatValue()));
                }

                curX += m_tFormat.getVLineStroke().floatValue();
            }

            // Background

            bg = getBackground( row,col );

            if( !bg.equals( Color.white )) {
                g2D.setPaint( bg );
                g2D.fillRect(( curX + leftSelected ) + rightNextSelected,curY,(( leftPrevSelected + ( int )( colWidth - m_tFormat.getVLineStroke().floatValue())) - leftSelected ) + rightSelected,( int )( rowHeight - m_tFormat.getLineStroke().floatValue()));
            }

            curX += H_GAP;    // upper left gap
            curY += V_GAP;

            // actual data

            Object[]           printItems = getPrintItems( row,col );
            MPrintFormatItem[] arrayItem  = getMPrintItems( row,col );
            float              penY       = curY;

            for( int index = 0;index < printItems.length;index++ ) {
                if( printItems[ index ] == null ) {
                    ;
                } else if( printItems[ index ] instanceof ImageDesElement ) {
                    g2D.drawImage((( ImageDesElement )printItems[ index ] ).getImage(),curX,( int )penY,this );
                } else if( printItems[ index ] instanceof Boolean ) {
                    int penX = curX + ( int )(( netWidth - LayoutDesign.IMAGE_SIZE.width ) / 2 );    // center

                    if((( Boolean )printItems[ index ] ).booleanValue()) {
                        g2D.drawImage( LayoutDesign.IMAGE_TRUE,( leftSelected + penX ) + rightNextSelected,( int )penY,this );
                    } else {
                        g2D.drawImage( LayoutDesign.IMAGE_FALSE,( leftSelected + penX ) + rightNextSelected,( int )penY,this );
                    }

                    penY += LayoutDesign.IMAGE_SIZE.height;
                } else if( printItems[ index ] instanceof HTMLRenderer ) {
                    HTMLRenderer renderer = ( HTMLRenderer )printItems[ index ];
                    Rectangle allocation = new Rectangle(( int )colWidth,( int )netHeight );

                    // log.finest( "printColumn HTML - " + allocation);

                    g2D.translate( curX,penY );
                    renderer.paint( g2D,allocation );
                    g2D.translate( -curX,-penY );
                    penY += allocation.getHeight();
                } else {
                    String str = printItems[ index ].toString();

                    if( DEBUG_PRINT ) {
                        log.fine( "row=" + row + ",col=" + col + " - " + str + " 8Bit=" + Util.is8Bit( str ));
                    }

                    if( str.length() > 0 ) {
                        usedHeight = 0;

                        String[] lines = Pattern.compile( "$",Pattern.MULTILINE ).split( str );

                        for( int lineNo = 0;lineNo < lines.length;lineNo++ ) {
                            aString = new AttributedString( lines[ lineNo ] );
                            aString.addAttribute( TextAttribute.FONT,getFont( row,col ));

                            if( isView && (printItems[ index ] instanceof NamePair) )    // ID
                            {
                                aString.addAttribute( TextAttribute.FOREGROUND,LINK_COLOR );
                                aString.addAttribute( TextAttribute.UNDERLINE,TextAttribute.UNDERLINE_LOW_ONE_PIXEL,0,str.length());
                            } else {
                                aString.addAttribute( TextAttribute.FOREGROUND,getColor( row,col ));
                            }

                            //

                            iter = aString.getIterator();

                            boolean fastDraw = LayoutDesign.s_FASTDRAW;

                            if( fastDraw &&!isView &&!Util.is8Bit( lines[ lineNo ] )) {
                                fastDraw = false;
                            }

                            measurer = new LineBreakMeasurer( iter,g2D.getFontRenderContext());

                            while( measurer.getPosition() < iter.getEndIndex())    // print element
                            {
                                TextLayout layout = measurer.nextLayout( netWidth + 2 );

                                if( iter.getEndIndex() != measurer.getPosition()) {
                                    fastDraw = false;
                                }

                                float lineHeight = layout.getAscent() + layout.getDescent() + layout.getLeading();

                                if( ( (m_columnMaxHeight[ col ] <= 0) || ( usedHeight + lineHeight ) <= m_columnMaxHeight[ col ] ) && ( usedHeight + lineHeight ) <= netHeight ) {
                                    if( alignment.equals( MPrintFormatItem.FIELDALIGNMENTTYPE_Block ) && (measurer.getPosition() < iter.getEndIndex())) {
                                        layout = layout.getJustifiedLayout( netWidth + 2 );
                                        fastDraw = false;
                                    }

                                    penY += layout.getAscent();

                                    float penX = curX;

                                    if( alignment.equals( MPrintFormatItem.FIELDALIGNMENTTYPE_Center )) {
                                        penX += ( netWidth - layout.getAdvance()) / 2;
                                    } else if(( alignment.equals( MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight ) && layout.isLeftToRight()) || ( alignment.equals( MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft ) &&!layout.isLeftToRight())) {
                                        penX += netWidth - layout.getAdvance();
                                    }

                                    //

                                    if( fastDraw ) {    // Bug - set Font/Color explicitly
                                        g2D.setFont( getFont( row,col ));

                                        if( isView && (printItems[ index ] instanceof NamePair) )    // ID
                                        {
                                            g2D.setColor( LINK_COLOR );

                                            // TextAttribute.UNDERLINE

                                        } else {
                                            g2D.setColor( getColor( row,col ));
                                        }

                                        g2D.drawString( iter,( leftSelected + penX ) + rightNextSelected,penY );
                                    } else {
                                        layout.draw( g2D,( leftSelected + penX ) + rightNextSelected,penY );    // -> text
                                    }

                                    if( DEBUG_PRINT ) {
                                        log.fine( "row=" + row + ",col=" + col + " - " + str + " - x=" + penX + ",y=" + penY );
                                    }

                                    penY += layout.getDescent() + layout.getLeading();
                                    usedHeight += lineHeight;

                                    //

                                    if( m_columnMaxHeight[ col ] == -1 ) {    // FirstLineOny
                                        break;
                                    }
                                }
                            }    // print element
                        }        // for all lines
                    }            // length > 0
                }                // non boolean
            }                    // for all print items

            curY += netHeight + V_GAP;
            curX += netWidth + H_GAP;

            // Y end line

            g2D.setPaint( m_tFormat.getVLine_Color());
            g2D.setStroke( m_tFormat.getVLine_Stroke());

            if( isSelected ) {
                g2D.setPaint( m_tFormat.getHLine_Color());
                g2D.setStroke( dashed );
                g2D.drawLine(( leftSelected + ( int )( curX - colWidth )),rowYstart,    // -> | (right)
                    ( leftSelected + ( int )( curX - colWidth )),( int )( rowYstart + rowHeight - m_tFormat.getLineStroke().floatValue()));
            }

            if( m_tFormat.isPaintVLines() || isSelected ) {
                g2D.drawLine( rightSelected + (( curX + leftPrevSelected ) + rightNextSelected ),rowYstart,    // -> | (right)
                    rightSelected + (( curX + leftPrevSelected ) + rightNextSelected ),( int )( rowYstart + rowHeight - m_tFormat.getLineStroke().floatValue()));
            }

            curX += m_tFormat.getVLineStroke().floatValue();

            // X end line

            if( row == m_data.length - 1 )                                         // last Line
            {
                g2D.setPaint( m_tFormat.getHeaderLine_Color());
                g2D.setStroke( m_tFormat.getHeader_Stroke());

                if( isSelected ) {
                    g2D.setStroke( dashed );
                }

                g2D.drawLine(( leftSelected + origX ) + rightNextSelected,curY,    // -> - (last line)
                    (( leftPrevSelected + ( int )( origX + colWidth - m_tFormat.getVLineStroke().floatValue())) + rightSelected ) + rightNextSelected,curY );
                curY += ( 2 * m_tFormat.getLineStroke().floatValue());    // thick
            } else {

                // next line is a funcion column -> underline this

                boolean nextIsFunction = m_functionRows.contains( new Integer( row + 1 ));

                if( nextIsFunction && m_functionRows.contains( new Integer( row ))) {
                    nextIsFunction = false;                                            // this is a function line too
                }

                if( nextIsFunction ) {
                    g2D.setPaint( m_tFormat.getFunctFG_Color());
                    g2D.setStroke( m_tFormat.getHLine_Stroke());

                    if( isSelected ) {
                        g2D.setStroke( dashed );
                    }

                    g2D.drawLine(( leftSelected + origX ) + rightNextSelected,curY,    // -> - (bottom)
                        (( leftPrevSelected + ( int )( origX + colWidth - m_tFormat.getVLineStroke().floatValue())) + rightSelected ) + rightNextSelected,curY );
                } else if( m_tFormat.isPaintHLines()) {
                    g2D.setPaint( m_tFormat.getHLine_Color());
                    g2D.setStroke( m_tFormat.getHLine_Stroke());

                    if( isSelected ) {
                        g2D.setStroke( dashed );
                    }

                    g2D.drawLine(( leftSelected + origX ) + rightNextSelected,curY,    // -> - (bottom)
                        (( leftPrevSelected + ( int )( origX + colWidth - m_tFormat.getVLineStroke().floatValue())) + rightSelected ) + rightNextSelected,curY );
                }

                curY += m_tFormat.getLineStroke().floatValue();
            }
        }    // for all rows

        tableHeight += curY;
    }    // printColumn

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param col
     * @param data
     */

    private void addAdditionalLines( int row,int col,Object data ) {
        Point     key  = new Point( row,col );
        ArrayList list = ( ArrayList )m_additionalLineData.get( key );

        if( list == null ) {
            list = new ArrayList();
        }

        list.add( data );
        m_additionalLineData.put( key,list );
    }    // addAdditionalLines

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param col
     *
     * @return
     */

    private Object[] getPrintItems( int row,int col ) {
        Point     key  = new Point( row,col );
        ArrayList list = ( ArrayList )m_additionalLineData.get( key );

        if( list == null ) {
            if( m_data[ row ][ col ] != null ) {
                return new Object[]{ m_data[ row ][ col ] };
            } else {
                return new Object[]{};
            }
        }

        // multiple

        ArrayList retList = new ArrayList();

        retList.add( m_data[ row ][ col ] );
        retList.addAll( list );

        return retList.toArray();
    }    // getPrintItems

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param col
     *
     * @return
     */

    private MPrintFormatItem[] getMPrintItems( int row,int col ) {

        // Log.print("\n" + m_dataItems[row][col].getName() + "n");

        if( m_data[ row ][ col ] != null ) {
            return new MPrintFormatItem[]{ m_dataItems[ col ] };
        } else {
            return new MPrintFormatItem[]{};
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param clickX
     * @param pageNo
     */

    public void nameElement( int clickX,int pageNo ) {
        posXclick = clickX;
        posPage   = pageNo;

        int pageIndex  = getPageIndex( pageNo );
        int pageXindex = getPageXIndex( pageIndex );
        int startX     = MARGIN;

        startX += (pageXindex == 0)
                  ?m_firstPage.x
                  :m_nextPages.x;

        int nextPageColumn = m_columnHeader.length;    // no of cols

        if( pageXindex + 1 < m_firstColumnOnPage.size()) {
            nextPageColumn = (( Integer )m_firstColumnOnPage.get( pageXindex + 1 )).intValue();
        }

        int firstColumn = (( Integer )m_firstColumnOnPage.get( pageXindex )).intValue();
        int regularColumnStart = firstColumn;

        for( int col = 0;(col < m_repeatedColumns) && (col < m_columnWidths.size());col++ ) {
            int colWidth = (( Float )m_columnWidths.get( col )).intValue();    // includes 2*Gaps+Line

            if( colWidth != 0 ) {
                if((( posXclick >= startX ) && (posXclick < ( startX + colWidth ))) || ( isPrevSelected && (colSelected == col) ) ) {
                    colSelected    = col;
                    isPrevSelected = false;
                    isOneSelected  = true;
                }

                startX += colWidth;

                if( regularColumnStart == col ) {
                    regularColumnStart++;
                }
            }
        }

        for( int col = regularColumnStart;col < nextPageColumn;col++ ) {
            int colWidth = (( Float )m_columnWidths.get( col )).intValue();    // includes 2*Gaps+Line

            if( colWidth != 0 ) {
                if(((( posXclick >= ( startX )) && ( posXclick < ( startX + colWidth ))) && ( pageNo == posPage )) || ( isPrevSelected && (colSelected == col) ) ) {
                    colSelected    = col;
                    isPrevSelected = false;
                    isOneSelected  = true;
                }

                startX += colWidth;
            }
        }    // for all columns

        if( !isOneSelected ) {
            isPrevSelected = false;
            addwidth       = 0;
            posXclick      = 0;
            posPage        = 0;
            leftLine       = 0;
            rightLine      = 0;
            rowSelected    = 0;
            colSelected    = 0;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param x
     *
     * @return
     */

    public boolean isLeftLine( int x ) {
        if(( x > leftLine - 3 ) && ( x < leftLine + 3 )) {
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
     *
     * @return
     */

    public boolean isRightLine( int x ) {
        if(( x > rightLine - 3 ) && ( x < rightLine + 3 )) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSelectedPage() {
        return posPage;
    }

    /**
     * Descripción de Método
     *
     *
     * @param newWidth
     * @param one
     */

    public void setDimension( int newWidth,boolean one ) {
        addwidth = newWidth;

        if( one ) {
            if( colSelected - 1 >= 0 ) {
                m_dataItems[ colSelected - 1 ].setMaxWidth( m_dataItems[ colSelected - 1 ].getMaxWidth() + newWidth );
                m_dataItems[ colSelected - 1 ].save();
            }

            m_dataItems[ colSelected ].setMaxWidth( m_dataItems[ colSelected ].getMaxWidth() - newWidth );
            m_dataItems[ colSelected ].save();
        } else {
            m_dataItems[ colSelected ].setMaxWidth( m_dataItems[ colSelected ].getMaxWidth() + newWidth );
            m_dataItems[ colSelected ].save();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param newWidth
     * @param left
     */

    public void setAddWidth( int newWidth,boolean left ) {
        isLeft   = left;
        addwidth = newWidth;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getColSelected() {
        return colSelected;
    }

    /**
     * Descripción de Método
     *
     *
     * @param col
     * @param PageNo
     */

    public void setColSelected( int col,int PageNo ) {
        posPage        = PageNo;
        colSelected    = col;
        isPrevSelected = true;
        isOneSelected  = true;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOneSelected() {
        return isOneSelected;
    }

    /**
     * Descripción de Método
     *
     */

    public void notSelected() {
        isOneSelected  = false;
        isPrevSelected = false;
        addwidth       = 0;
        posXclick      = 0;
        posPage        = 0;
        leftLine       = 0;;
        rightLine      = 0;
        rowSelected    = 0;
        colSelected    = 0;
    }

    /**
     * Descripción de Método
     *
     *
     * @param page
     *
     * @return
     */

    public int getTableHeight( int page ) {
        if( m_tableHeight.size() != 0 ) {
            return(( Integer )m_tableHeight.get( page - 1 )).intValue();
        } else {
            return 0;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param page
     *
     * @return
     */

    public int getTableWidth( int page ) {
        if( m_tableWidth.size() != 0 ) {
            if(( page ) < m_tableWidth.size()) {
                return(( Integer )m_tableWidth.get( page - 1 )).intValue();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param id1
     * @param id2
     * @param vd
     */

    public void reSeqNo( int id1,int id2,ViewDesign vd ) {
        int              nextPageColumn = m_columnHeader.length;
        MPrintFormatItem pfi1           = new MPrintFormatItem( Env.getCtx(),id1,null );
        MPrintFormatItem pfi2           = new MPrintFormatItem( Env.getCtx(),id2,null );
        boolean          reOrderLeft    = false;
        boolean          reOrderRight   = false;
        int              aux            = 0;

        for( int col = 0;col < nextPageColumn;col++ ) {
            m_dataItems[ col ].setSeqNo( 10 * ( col + 1 ));
            m_dataItems[ col ].save();
        }

        // vd.updateLayout();

        for( int col = 0;col < nextPageColumn;col++ ) {

            // ordenar de izquiera a derecha hasta encontrar el item donde se presiono

            if( reOrderLeft ) {
                if( !reOrderRight ) {
                    m_dataItems[ col - 1 ].setSeqNo( m_dataItems[ col ].getSeqNo());
                } else {
                    reOrderLeft  = false;
                    reOrderRight = false;
                    m_dataItems[ col - 1 ].setSeqNo( m_dataItems[ col - 1 ].getSeqNo() + 10 );
                }

                m_dataItems[ col - 1 ].save();
            }

            // id1:elemento donde se presiono el raton

            if( m_dataItems[ col ].getAD_PrintFormatItem_ID() == id1 ) {
                if( !reOrderLeft ) {
                    aux = pfi1.getSeqNo();
                    pfi1.setSeqNo( pfi2.getSeqNo());
                    pfi1.save();
                }

                reOrderRight  = true;
                isOneSelected = true;
                colSelected   = col;
            }

            // id2: elemento donde se solto el raton

            if( m_dataItems[ col ].getAD_PrintFormatItem_ID() == id2 ) {
                if( !reOrderRight ) {
                    pfi1.setSeqNo( m_dataItems[ col ].getSeqNo());
                    pfi1.save();
                }

                reOrderLeft = true;
            }

            // ordenar de derecha a izquierda hasta encontrar el item donde se solto

            if( reOrderRight ) {
                if( !reOrderLeft ) {
                    m_dataItems[ col + 1 ].setSeqNo( aux );
                    aux = aux + 10;
                    m_dataItems[ col + 1 ].save();
                } else {
                    reOrderLeft  = false;
                    reOrderRight = false;
                }
            }
        }

        // vd.updateLayout();

        for( int col = 0;col < nextPageColumn;col++ ) {
            if( m_dataItems[ col ].getAD_PrintFormatItem_ID() == id1 ) {
                colSelected    = col;
                isOneSelected  = true;
                isPrevSelected = false;
            }
        }

        vd.updateLayout();
    }

    /**
     * Descripción de Método
     *
     */

    public void changeSeqNo() {
        int nextPageColumn = m_columnHeader.length;

        for( int col = 0;col < nextPageColumn;col++ ) {
            m_dataItems[ col ].setSeqNo( 10 * ( col + 1 ));
            m_dataItems[ col ].save();
        }

        int aux = 0;

        if( colSelected - 1 >= 0 ) {
            aux = m_dataItems[ colSelected - 1 ].getSeqNo();
            m_dataItems[ colSelected - 1 ].setSeqNo( m_dataItems[ colSelected ].getSeqNo());
            m_dataItems[ colSelected - 1 ].save();
            m_dataItems[ colSelected ].setSeqNo( aux );
            m_dataItems[ colSelected ].save();
            colSelected = colSelected - 1;
        }
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
        m_viewDesign = vd;

        ArrayList items = new ArrayList();

        if( m_dataItems[ colSelected ].isActive()) {
            items.add( new JMenuItem( ACTIVO,ICON_TRUE ));
        } else {
            items.add( new JMenuItem( ACTIVO,ICON_FALSE ));
        }

        if( m_dataItems[ colSelected ].isPrinted()) {
            items.add( new JMenuItem( IMPRESO,ICON_TRUE ));
        } else {
            items.add( new JMenuItem( IMPRESO,ICON_FALSE ));
        }

        if( m_dataItems[ colSelected ].isPrinted()) {
            if( m_dataItems[ colSelected ].isSuppressNull()) {
                items.add( new JMenuItem( SUPRIMIRNULOS,ICON_TRUE ));
            } else {
                items.add( new JMenuItem( SUPRIMIRNULOS,ICON_FALSE ));
            }
        }

        if( m_dataItems[ colSelected ].isRelativePosition()) {
            if( m_dataItems[ colSelected ].isNextLine()) {
                items.add( new JMenuItem( PROXIMALINEA,ICON_TRUE ));
            } else {
                items.add( new JMenuItem( PROXIMALINEA,ICON_FALSE ));
            }
        }

        if( m_dataItems[ colSelected ].isRelativePosition()) {
            if( m_dataItems[ colSelected ].isNextPage()) {
                items.add( new JMenuItem( PROXIMAPAGINA,ICON_TRUE ));
            } else {
                items.add( new JMenuItem( PROXIMAPAGINA,ICON_FALSE ));
            }
        }

        if( m_dataItems[ colSelected ].isFixedWidth()) {
            items.add( new JMenuItem( ANCHOFIJO,ICON_TRUE ));
        } else {
            items.add( new JMenuItem( ANCHOFIJO,ICON_FALSE ));
        }

        if( m_dataItems[ colSelected ].isHeightOneLine()) {
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
            if( m_dataItems[ colSelected ].isActive()) {
                m_dataItems[ colSelected ].setIsActive( false );
            } else {
                m_dataItems[ colSelected ].setIsActive( true );
            }
        }

        if( e.getActionCommand() == IMPRESO ) {
            if( m_dataItems[ colSelected ].isPrinted()) {
                m_dataItems[ colSelected ].setIsPrinted( false );
            } else {
                m_dataItems[ colSelected ].setIsPrinted( true );
            }
        }

        if( e.getActionCommand() == SUPRIMIRNULOS ) {
            if( m_dataItems[ colSelected ].isSuppressNull()) {
                m_dataItems[ colSelected ].setIsSuppressNull( false );
            } else {
                m_dataItems[ colSelected ].setIsSuppressNull( true );
            }
        }

        if( e.getActionCommand() == PROXIMALINEA ) {
            if( m_dataItems[ colSelected ].isNextLine()) {
                m_dataItems[ colSelected ].setIsNextLine( false );
            } else {
                m_dataItems[ colSelected ].setIsNextLine( true );
            }

            m_dataItems[ colSelected ].save();
            m_viewDesign.refreshToolBar();
        }

        if( e.getActionCommand() == ANCHOFIJO ) {
            if( m_dataItems[ colSelected ].isFixedWidth()) {
                m_dataItems[ colSelected ].setIsFixedWidth( false );
            } else {
                m_dataItems[ colSelected ].setIsFixedWidth( true );
            }
        }

        if( e.getActionCommand() == UNALINEA ) {
            if( m_dataItems[ colSelected ].isHeightOneLine()) {
                m_dataItems[ colSelected ].setIsHeightOneLine( false );
            } else {
                m_dataItems[ colSelected ].setIsHeightOneLine( true );
            }
        }

        if( e.getActionCommand() == PROXIMAPAGINA ) {
            if( m_dataItems[ colSelected ].isNextPage()) {
                m_dataItems[ colSelected ].setIsNextPage( false );
            } else {
                m_dataItems[ colSelected ].setIsNextPage( true );
            }
        }

        m_dataItems[ colSelected ].save();
        m_viewDesign.updateLayout();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPrintFormatItemID() {
        if( isOneSelected ) {
            return m_dataItems[ colSelected ].getAD_PrintFormatItem_ID();
        } else {
            return 0;
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

        if( isOneSelected ) {

            // Primera barra de herramientas

            pos = new String( "Uno" );
            fields.add( pos );

            CLabel labelPrintFormatType = new CLabel( "Formato de impresi�n:" );

            fields.add( labelPrintFormatType );

            VLookup printFormatType;
            MLookup m_printFormatType = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),255 ),0 );

            printFormatType = new VLookup( "AD_Print_Format_Type",true,false,true,m_printFormatType );

            if( m_dataItems[ colSelected ].getPrintFormatType().length() != 0 ) {
                printFormatType.setValue( m_dataItems[ colSelected ].getPrintFormatType());
            }

            printFormatType.setSize( 150,24 );
            printFormatType.setMaximumSize( printFormatType.getSize());
            printFormatType.setPreferredSize( printFormatType.getSize());
            fields.add( printFormatType );

            CLabel labelFieldAlignement = new CLabel( "Alineacion del campo:" );

            fields.add( labelFieldAlignement );

            VLookup FieldAlignement;
            MLookup m_fieldAlignement = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),253 ),0 );

            FieldAlignement = new VLookup( "FieldAlignmetType",true,false,true,m_fieldAlignement );

            if( m_dataItems[ colSelected ].getFieldAlignmentType().length() != 0 ) {
                FieldAlignement.setValue( m_dataItems[ colSelected ].getFieldAlignmentType());
            }

            FieldAlignement.setSize( 150,24 );
            FieldAlignement.setMaximumSize( FieldAlignement.getSize());
            FieldAlignement.setPreferredSize( FieldAlignement.getSize());
            fields.add( FieldAlignement );

            MPrintFormat pf = new MPrintFormat( Env.getCtx(),m_dataItems[ colSelected ].getAD_PrintFormat_ID(),null );

            try {
                CLabel labelColumn = new CLabel( "Columna:" );

                fields.add( labelColumn );

                MLookup m_column = MLookupFactory.get( Env.getCtx(),0,16,DisplayType.Table,Env.getLanguage( Env.getCtx()),"AD_Column",3,false,"AD_Table_ID =" + pf.getAD_Table_ID());
                VLookup column = new VLookup( "AD_Column_ID",true,false,true,m_column );

                column.setValue( new Integer( m_dataItems[ colSelected ].getAD_Column_ID()));
                column.setSize( 150,24 );
                column.setMaximumSize( column.getSize());
                column.setPreferredSize( column.getSize());
                fields.add( column );
            } catch( Exception e ) {
            }

            // Segunda barra de herramientas

            pos = new String( "Dos" );
            fields.add( pos );

            CLabel labelSeqNo = new CLabel( "Numero Secuencia:" );

            fields.add( labelSeqNo );

            VNumber seqNo = new VNumber( "SeqNo",true,false,true,DisplayType.Integer,"Numero de Secuencia" );

            seqNo.setValue( new Integer( m_dataItems[ colSelected ].getSeqNo()));
            seqNo.setSize( 60,20 );
            seqNo.setMaximumSize( seqNo.getSize());
            seqNo.setPreferredSize( seqNo.getSize());
            fields.add( seqNo );

            CLabel labelMaxWidth = new CLabel( "M�ximo Ancho:" );

            fields.add( labelMaxWidth );

            VNumber maxWidth = new VNumber( "MaxWidth",true,false,true,DisplayType.Integer,"M�ximo Ancho" );

            maxWidth.setValue( new Integer( m_dataItems[ colSelected ].getMaxWidth()));
            maxWidth.setSize( 60,20 );
            maxWidth.setMaximumSize( maxWidth.getSize());
            maxWidth.setPreferredSize( maxWidth.getSize());
            fields.add( maxWidth );

            CLabel labelMaxHeight = new CLabel( "M�ximo Alto:" );

            fields.add( labelMaxHeight );

            VNumber maxHeight = new VNumber( "MaxHeight",true,false,true,DisplayType.Integer,"M�ximo Alto" );

            maxHeight.setValue( new Integer( m_dataItems[ colSelected ].getMaxHeight()));
            maxHeight.setSize( 60,20 );
            maxHeight.setMaximumSize( maxHeight.getSize());
            maxHeight.setPreferredSize( maxHeight.getSize());
            fields.add( maxHeight );

            CLabel labelName = new CLabel( "Nombre:" );

            fields.add( labelName );

            CTextField Name = new CTextField( m_dataItems[ colSelected ].getName());

            Name.setSize( 140,23 );
            Name.setMaximumSize( Name.getSize());
            Name.setPreferredSize( Name.getSize());
            fields.add( Name );

            if( m_dataItems[ colSelected ].isNextLine()) {
                CLabel labelBelowColumn = new CLabel( "ColumnaAbajo:" );

                fields.add( labelBelowColumn );

                VNumber belowColumn = new VNumber( "BelowColumn",true,false,true,DisplayType.Integer,"Nombre" );

                belowColumn.setValue( new Integer( m_dataItems[ colSelected ].getBelowColumn()));
                belowColumn.setSize( 60,23 );
                belowColumn.setMaximumSize( belowColumn.getSize());
                belowColumn.setPreferredSize( belowColumn.getSize());
                fields.add( belowColumn );
            }

            // tercera barra de herramientas

            pos = new String( "Tres" );
            fields.add( pos );

            CLabel labelPrintName = new CLabel( "Nombre a imprimir:" );

            fields.add( labelPrintName );

            CTextField PrintName = new CTextField( m_dataItems[ colSelected ].getPrintName());

            PrintName.setSize( 140,23 );
            PrintName.setMaximumSize( PrintName.getSize());
            PrintName.setPreferredSize( PrintName.getSize());
            fields.add( PrintName );

            try {
                CLabel labelPrintFont = new CLabel( "Fuente de impresi�n:" );

                fields.add( labelPrintFont );

                VLookup printFont;
                MLookup m_printFont = MLookupFactory.get( Env.getCtx(),0,6963,19,Env.getLanguage( Env.getCtx()),"AD_PrintFont_ID",267,false,"" );

                printFont = new VLookup( "AD_PrintFont_ID",false,false,true,m_printFont );

                if( m_dataItems[ colSelected ].getAD_PrintFont_ID() != 0 ) {
                    printFont.setValue( new Integer( m_dataItems[ colSelected ].getAD_PrintFont_ID()));
                }

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

                if( m_dataItems[ colSelected ].getAD_PrintColor_ID() != 0 ) {
                    printColor.setValue( new Integer( m_dataItems[ colSelected ].getAD_PrintColor_ID()));
                }

                printColor.setSize( 150,24 );
                printColor.setMaximumSize( printColor.getSize());
                printColor.setPreferredSize( printColor.getSize());
                fields.add( printColor );
            } catch( Exception e ) {
            }
        } else {

            // Primera barra de herramientas

            pos = new String( "Uno" );
            fields.add( pos );

            CCheckBox predeterminado = new CCheckBox( "Predeterminado",m_tFormat.isDefault());

            fields.add( predeterminado );

            CLabel labelColorLetraEncabezado = new CLabel( "Color Letra Encabezado:" );

            fields.add( labelColorLetraEncabezado );

            try {
                VLookup colorLetraEncabezado;
                MLookup m_colorLetraEncabezado = MLookupFactory.get( Env.getCtx(),0,7621,18,Env.getLanguage( Env.getCtx()),"HdrTextFG_PrintColor_ID",266,false,"" );

                colorLetraEncabezado = new VLookup( "HdrTextFG_PrintColor_ID",false,false,true,m_colorLetraEncabezado );

                if( m_tFormat.getHdrTextFG_PrintColor_ID() != 0 ) {
                    colorLetraEncabezado.setValue( new Integer( m_tFormat.getHdrTextFG_PrintColor_ID()));
                }

                colorLetraEncabezado.setSize( 100,24 );
                colorLetraEncabezado.setMaximumSize( colorLetraEncabezado.getSize());
                colorLetraEncabezado.setPreferredSize( colorLetraEncabezado.getSize());
                fields.add( colorLetraEncabezado );
            } catch( Exception e ) {
            }

            CLabel labelColorFondoEncabezado = new CLabel( "Fondo Encabezado:" );

            fields.add( labelColorFondoEncabezado );

            try {
                VLookup colorFondoEncabezado;
                MLookup m_colorFondoEncabezado = MLookupFactory.get( Env.getCtx(),0,7632,18,Env.getLanguage( Env.getCtx()),"HdrTextBG_PrintColor_ID",266,false,"" );

                colorFondoEncabezado = new VLookup( "HdrTextBG_PrintColor_ID",false,false,true,m_colorFondoEncabezado );

                if( m_tFormat.getHdrTextBG_PrintColor_ID() != 0 ) {
                    colorFondoEncabezado.setValue( new Integer( m_tFormat.getHdrTextBG_PrintColor_ID()));
                }

                colorFondoEncabezado.setSize( 100,24 );
                colorFondoEncabezado.setMaximumSize( colorFondoEncabezado.getSize());
                colorFondoEncabezado.setPreferredSize( colorFondoEncabezado.getSize());
                fields.add( colorFondoEncabezado );
            } catch( Exception e ) {
            }

            CLabel labelFuenteEncabezado = new CLabel( "Fuente Encabezado:" );

            fields.add( labelFuenteEncabezado );

            try {
                VLookup fuenteEncabezado;
                MLookup m_fuenteEncabezado = MLookupFactory.get( Env.getCtx(),0,7622,18,Env.getLanguage( Env.getCtx()),"Hdr_PrintFont_ID",267,false,"" );

                fuenteEncabezado = new VLookup( "Hdr_PrintFont_ID",false,false,true,m_fuenteEncabezado );

                if( m_tFormat.getHdr_PrintFont_ID() != 0 ) {
                    fuenteEncabezado.setValue( new Integer( m_tFormat.getHdr_PrintFont_ID()));
                }

                fuenteEncabezado.setSize( 150,24 );
                fuenteEncabezado.setMaximumSize( fuenteEncabezado.getSize());
                fuenteEncabezado.setPreferredSize( fuenteEncabezado.getSize());
                fields.add( fuenteEncabezado );
            } catch( Exception e ) {
            }

            // Segunda barra de herramientas

            pos = new String( "Dos" );
            fields.add( pos );

            CCheckBox lCabecera = new CCheckBox( "Linea de Encabezado",m_tFormat.isPaintHeaderLines());

            fields.add( lCabecera );

            CLabel labelColorLineasCabecera = new CLabel( "Color de Linea:" );

            fields.add( labelColorLineasCabecera );

            try {
                VLookup colorLineasCabecera;
                MLookup m_colorLineasCabecera = MLookupFactory.get( Env.getCtx(),0,7617,18,Env.getLanguage( Env.getCtx()),"HdrLine_PrintColor_ID",266,false,"" );

                colorLineasCabecera = new VLookup( "HdrLine_PrintColor_ID",false,false,true,m_colorLineasCabecera );
                colorLineasCabecera.setValue( new Integer( m_tFormat.getHdrLine_PrintColor_ID()));
                colorLineasCabecera.setSize( 100,24 );
                colorLineasCabecera.setMaximumSize( colorLineasCabecera.getSize());
                colorLineasCabecera.setPreferredSize( colorLineasCabecera.getSize());
                fields.add( colorLineasCabecera );
            } catch( Exception e ) {
            }

            CLabel labelTipoLineasCabecera = new CLabel( "Tipo de Linea:" );

            fields.add( labelTipoLineasCabecera );

            VLookup tipoLineasCabecera;
            MLookup m_tipoLineasCabecera = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),312 ),0 );

            tipoLineasCabecera = new VLookup( "HeaderLineType",false,false,true,m_tipoLineasCabecera );

            if( m_tFormat.getHdrStrokeType() != null ) {
                tipoLineasCabecera.setValue( new String( m_tFormat.getHdrStrokeType()));
            }

            tipoLineasCabecera.setSize( 175,24 );
            tipoLineasCabecera.setMaximumSize( tipoLineasCabecera.getSize());
            tipoLineasCabecera.setPreferredSize( tipoLineasCabecera.getSize());
            fields.add( tipoLineasCabecera );

            CLabel labelLineaCabecera = new CLabel( "Linea:" );

            fields.add( labelLineaCabecera );

            VNumber lineaCabecera = new VNumber( "Linea Cabecera",false,false,true,DisplayType.Number,"Linea Cabecera" );

            lineaCabecera.setValue( m_tFormat.getHdrStroke());
            lineaCabecera.setSize( 60,20 );
            lineaCabecera.setMaximumSize( lineaCabecera.getSize());
            lineaCabecera.setPreferredSize( lineaCabecera.getSize());
            fields.add( lineaCabecera );

            // tercera barra de herramientas

            pos = new String( "Tres" );
            fields.add( pos );

            CCheckBox lineaV = new CCheckBox( "Linea V",m_tFormat.isPaintVLines());

            fields.add( lineaV );

            CCheckBox simbolosFuncion = new CCheckBox( "Simbolos de Funcion",m_tFormat.isPrintFunctionSymbols());

            fields.add( simbolosFuncion );

            CLabel labelColorFuncion = new CLabel( "Color Funcion:" );

            fields.add( labelColorFuncion );

            try {
                VLookup colorFuncion;
                MLookup m_colorFuncion = MLookupFactory.get( Env.getCtx(),0,7628,18,Env.getLanguage( Env.getCtx()),"FunctFG_PrintColor_ID",266,false,"" );

                colorFuncion = new VLookup( "FunctFG_PrintColor_ID",false,false,true,m_colorFuncion );

                if( m_tFormat.getFunctFG_PrintColor_ID() != 0 ) {
                    colorFuncion.setValue( new Integer( m_tFormat.getFunctFG_PrintColor_ID()));
                }

                colorFuncion.setSize( 100,24 );
                colorFuncion.setMaximumSize( colorFuncion.getSize());
                colorFuncion.setPreferredSize( colorFuncion.getSize());
                fields.add( colorFuncion );
            } catch( Exception e ) {
            }

            CLabel labelColorFuncionFondo = new CLabel( "Color Fondo:" );

            fields.add( labelColorFuncionFondo );

            try {
                VLookup colorFuncionFondo;
                MLookup m_colorFuncionFondo = MLookupFactory.get( Env.getCtx(),0,7626,18,Env.getLanguage( Env.getCtx()),"FunctBG_PrintColor_ID",266,false,"" );

                colorFuncionFondo = new VLookup( "FunctBG_PrintColor_ID",false,false,true,m_colorFuncionFondo );

                if( m_tFormat.getFunctBG_PrintColor_ID() != 0 ) {
                    colorFuncionFondo.setValue( new Integer( m_tFormat.getFunctBG_PrintColor_ID()));
                }

                colorFuncionFondo.setSize( 100,24 );
                colorFuncionFondo.setMaximumSize( colorFuncionFondo.getSize());
                colorFuncionFondo.setPreferredSize( colorFuncionFondo.getSize());
                fields.add( colorFuncionFondo );
            } catch( Exception e ) {
            }

            CLabel labelFuenteFuncion = new CLabel( "Fuente Funcion:" );

            fields.add( labelFuenteFuncion );

            try {
                VLookup fuenteFuncion;
                MLookup m_fuenteFuncion = MLookupFactory.get( Env.getCtx(),0,7624,18,Env.getLanguage( Env.getCtx()),"Funct_PrintFont_ID",267,false,"" );

                fuenteFuncion = new VLookup( "Funct_PrintFont_ID",false,false,true,m_fuenteFuncion );

                if( m_tFormat.getFunct_PrintFont_ID() != 0 ) {
                    fuenteFuncion.setValue( new Integer( m_tFormat.getFunct_PrintFont_ID()));
                }

                fuenteFuncion.setSize( 150,24 );
                fuenteFuncion.setMaximumSize( fuenteFuncion.getSize());
                fuenteFuncion.setPreferredSize( fuenteFuncion.getSize());
                fields.add( fuenteFuncion );
            } catch( Exception e ) {
            }

            // Cuarta barra de herramientas

            pos = new String( "Cuatro" );
            fields.add( pos );

            CCheckBox lineaH = new CCheckBox( "Linea H",m_tFormat.isPaintHLines());

            fields.add( lineaH );

            CCheckBox lineasLimite = new CCheckBox( "Lineas de Limite",m_tFormat.isPaintBoundaryLines());

            fields.add( lineasLimite );

            CLabel labelColorLineasLimite = new CLabel( "Color de Lineas:" );

            fields.add( labelColorLineasLimite );

            try {
                VLookup colorLineasLimite;
                MLookup m_colorLineasLimite = MLookupFactory.get( Env.getCtx(),0,7631,18,Env.getLanguage( Env.getCtx()),"Line_PrintColor_ID",266,false,"" );

                colorLineasLimite = new VLookup( "Line_PrintColor_ID",false,false,true,m_colorLineasLimite );

                if( m_tFormat.getLine_PrintColor_ID() != 0 ) {
                    colorLineasLimite.setValue( new Integer( m_tFormat.getLine_PrintColor_ID()));
                }

                colorLineasLimite.setSize( 100,24 );
                colorLineasLimite.setMaximumSize( colorLineasLimite.getSize());
                colorLineasLimite.setPreferredSize( colorLineasLimite.getSize());
                fields.add( colorLineasLimite );
            } catch( Exception e ) {
            }

            CLabel labelTipoLineasLimite = new CLabel( "Tipo de Lineas:" );

            fields.add( labelTipoLineasLimite );

            VLookup tipoLineasLimite;
            MLookup m_tipoLineasLimite = new MLookup( MLookupFactory.getLookup_List( Env.getLanguage( Env.getCtx()),312 ),0 );

            tipoLineasLimite = new VLookup( "LimitLineType",false,false,true,m_tipoLineasLimite );

            if( m_tFormat.getLineStrokeType().length() != 0 ) {
                tipoLineasLimite.setValue( new String( m_tFormat.getLineStrokeType()));
            }

            tipoLineasLimite.setSize( 175,24 );
            tipoLineasLimite.setMaximumSize( tipoLineasLimite.getSize());
            tipoLineasLimite.setPreferredSize( tipoLineasLimite.getSize());
            fields.add( tipoLineasLimite );

            CLabel labelLineaLimite = new CLabel( "Linea:" );

            fields.add( labelLineaLimite );

            VNumber lineaLimite = new VNumber( "Linea Limite",false,false,true,DisplayType.Number,"Linea Limite" );

            lineaLimite.setValue(( BigDecimal )m_tFormat.getLineStroke());
            lineaLimite.setSize( 60,20 );
            lineaLimite.setMaximumSize( lineaLimite.getSize());
            lineaLimite.setPreferredSize( lineaLimite.getSize());
            fields.add( lineaLimite );
        }

        return fields;
    }

    /**
     * Descripción de Método
     *
     *
     * @param id
     */

    public void setColSelectedByID( int id ) {
        for( int i = 0;i < m_dataItems.length;i++ ) {
            if( m_dataItems[ i ].getAD_PrintFormatItem_ID() == id ) {
                colSelected    = i;
                isPrevSelected = false;
                isOneSelected  = true;
            }
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param newMargin
     */

    public void setMargin( int newMargin ) {
        MARGIN = newMargin;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTableFormatID() {
        return m_tFormat.getAD_PrintTableFormat_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @param prevCol
     * @param isLeftLine
     *
     * @return
     */

    public int getLine( boolean prevCol,boolean isLeftLine ) {
        int col = colSelected;

        if( prevCol ) {
            col = col - 1;

            if( isLeftLine ) {
                return leftLine - (( Float )m_columnWidths.get( colSelected - 1 )).intValue();
            } else {
                return leftLine;
            }
        } else {
            if( isLeftLine ) {
                return leftLine;
            } else {
                return rightLine;
            }
        }
    }
}    // TableDesignElement



/*
 *  @(#)TableDesignElement.java   02.07.07
 * 
 *  Fin del fichero TableDesignElement.java
 *  
 *  Versión 2.2
 *
 */
