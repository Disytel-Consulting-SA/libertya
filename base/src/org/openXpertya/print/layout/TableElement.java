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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.openXpertya.model.MQuery;
import org.openXpertya.print.MPrintFormatItem;
import org.openXpertya.print.MPrintTableFormat;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.NamePair;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TableElement extends PrintElement {

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
     */

    public TableElement( ValueNamePair[] columnHeader,int[] columnMaxWidth,int[] columnMaxHeight,String[] columnJustification,boolean[] fixedWidth,ArrayList functionRows,boolean multiLineHeader,Object[][] data,KeyNamePair[] pk,String pkColumnName,int pageNoStart,Rectangle firstPage,Rectangle nextPages,int repeatedColumns,HashMap additionalLines,HashMap rowColFont,HashMap rowColColor,HashMap rowColBackground,MPrintTableFormat tFormat,ArrayList pageBreak ) {
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

        waitForLoad( LayoutEngine.IMAGE_TRUE );
        waitForLoad( LayoutEngine.IMAGE_FALSE );
    }    // TableElement

    /** Descripci�n de Campos */

    private ValueNamePair[] m_columnHeader;

    /** Descripci�n de Campos */

    private int[] m_columnMaxWidth;

    /** Descripci�n de Campos */

    private int[] m_columnMaxHeight;

    /** Descripci�n de Campos */

    private String[] m_columnJustification;

    /** Descripci�n de Campos */

    private boolean[] m_fixedWidth;

    /** Descripci�n de Campos */

    private boolean m_multiLineHeader;

    /** Descripci�n de Campos */

    private ArrayList m_functionRows;

    /** Descripci�n de Campos */

    private Object[][] m_data;

    /** Descripci�n de Campos */

    private KeyNamePair[] m_pk;

    /** Descripci�n de Campos */

    private String m_pkColumnName;

    /** Descripci�n de Campos */

    private int m_pageNoStart;

    /** Descripci�n de Campos */

    private Rectangle m_firstPage;

    /** Descripci�n de Campos */

    private Rectangle m_nextPages;

    /** Descripci�n de Campos */

    private int m_repeatedColumns;

    /** Descripci�n de Campos */

    private Font m_baseFont;

    /** Descripci�n de Campos */

    private HashMap m_rowColFont;

    /** Descripci�n de Campos */

    private Color m_baseColor;

    /** Descripci�n de Campos */

    private HashMap m_rowColColor;

    /** Descripci�n de Campos */

    private Color m_baseBackground;

    /** Descripci�n de Campos */

    private HashMap m_rowColBackground;

    /** Descripci�n de Campos */

    private MPrintTableFormat m_tFormat;

    /** Descripci�n de Campos */

    private ArrayList m_pageBreak;

    /** Descripci�n de Campos */

    private ArrayList m_columnWidths = new ArrayList();

    /** Descripci�n de Campos */

    private ArrayList m_rowHeights = new ArrayList();

    /** Descripci�n de Campos */

    private int m_headerHeight = 0;

    /** Descripci�n de Campos */

    private ArrayList m_firstRowOnPage = new ArrayList();

    /** Descripci�n de Campos */

    private ArrayList m_firstColumnOnPage = new ArrayList();

    /** Descripci�n de Campos */

    private ArrayList m_pageHeight = new ArrayList();

    /** Descripci�n de Campos */

    private HashMap m_rowColDrillDown = new HashMap();

    /** Descripci�n de Campos */

    private HashMap m_additionalLines = new HashMap();

    /** Descripci�n de Campos */

    private HashMap m_additionalLineData = new HashMap();

    /** Descripci�n de Campos */

    public static final int HEADER_ROW = -2;

    /** Descripci�n de Campos */

    public static final int ALL = -1;

    /** Descripci�n de Campos */

    private static final int H_GAP = 2;

    /** Descripci�n de Campos */

    private static final int V_GAP = 2;

    /** Descripci�n de Campos */

    private static final boolean DEBUG_PRINT = false;

    /**
     * Descripci�n de M�todo
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
                    dataSizes[ row ][ col ].addBelow( LayoutEngine.IMAGE_SIZE );

                    continue;
                } else if( dataItem instanceof ImageElement ) {
                    dataSizes[ row ][ col ].addBelow( new Dimension(( int )(( ImageElement )dataItem ).getWidth(),( int )(( ImageElement )dataItem ).getHeight()));

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
     * Descripci�n de M�todo
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

        // distribute proportionally (does not increase zero width columns)

        for( int x = 0;(remainingWidth > 0) && (x < 5);
                x++ )    // max 4 iterations
        {
            log.finest( "TotalWidth=" + totalWidth + ", Remaining=" + remainingWidth );

            for( int col = start;(col < toCol) && (remainingWidth != 0);col++ ) {
                int columnWidth = (( Float )m_columnWidths.get( col )).intValue();

                if( columnWidth != 0 ) {
                    int additionalPart = columnWidth * availableWidth / totalWidth;

                    if( remainingWidth < additionalPart ) {
                        m_columnWidths.set( col,new Float( columnWidth + remainingWidth ));
                        remainingWidth = 0;
                    } else {
                        m_columnWidths.set( col,new Float( columnWidth + additionalPart ));
                        remainingWidth -= additionalPart;
                    }

                    log.finest( "  col=" + col + " - From " + columnWidth + " to " + m_columnWidths.get( col ));
                }
            }
        }

        // add remainder to last non 0 width column

        for( int c = toCol - 1;(remainingWidth != 0) && (c >= 0);c-- ) {
            int columnWidth = (( Float )m_columnWidths.get( c )).intValue();

            if( columnWidth > 0 ) {
                m_columnWidths.set( c,new Float( columnWidth + remainingWidth ));
                log.finest( "Final col=" + c + " - From " + columnWidth + " to " + m_columnWidths.get( c ));
                remainingWidth = 0;
            }
        }
    }    // distribute Columns

    /**
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
     *
     */

    public void setHeightToLastPage() {
        int lastLayoutPage = getPageCount() + m_pageNoStart - 1;

        log.fine( "PageCount - Table=" + getPageCount() + "(Start=" + m_pageNoStart + ") Layout=" + lastLayoutPage + " - Old Height=" + p_height );
        p_height = getHeight( lastLayoutPage );
        log.fine( "New Height=" + p_height );
    }    // setHeightToLastPage

    /**
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public int getPageCount() {
        return m_firstRowOnPage.size() * m_firstColumnOnPage.size();
    }    // getPageCount

    /**
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
     *
     *
     * @param pageIndex
     *
     * @return
     */

    private int getPageNo( int pageIndex ) {
        return pageIndex + m_pageNoStart;
    }    // getPageNo

    /**
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    protected int getPageXCount() {
        return m_firstColumnOnPage.size();
    }    // getPageXCount

    /**
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    protected int getPageYCount() {
        return m_firstRowOnPage.size();
    }    // getPageYCount

    /**
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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

        for( int col = 0;(col < m_repeatedColumns) && (col < m_columnWidths.size());col++ ) {
            int colWidth = (( Float )m_columnWidths.get( col )).intValue();    // includes 2*Gaps+Line

            if( colWidth != 0 ) {
                printColumn( g2D,col,startX,startY,firstColumnPrint,firstRow,nextPageRow,isView );
                startX           += colWidth;
                firstColumnPrint = false;
            }

            if( regularColumnStart == col ) {
                regularColumnStart++;
            }
        }

        // paint columns

        for( int col = regularColumnStart;col < nextPageColumn;col++ ) {
            int colWidth = (( Float )m_columnWidths.get( col )).intValue();    // includes 2*Gaps+Line

            if( colWidth != 0 ) {
                printColumn( g2D,col,startX,startY,firstColumnPrint,firstRow,nextPageRow,isView );
                startX           += colWidth;
                firstColumnPrint = false;
            }
        }    // for all columns
    }        // paint

    /**
     * Descripci�n de M�todo
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
     */

    private void printColumn( Graphics2D g2D,int col,final int origX,final int origY,boolean leftVline,final int firstRow,final int nextPageRow,boolean isView ) {
        int curX = origX;
        int curY = origY;    // start from top

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

        if( leftVline )                                // draw left | line
        {
            g2D.setPaint( m_tFormat.getVLine_Color());
            g2D.setStroke( m_tFormat.getVLine_Stroke());

            if( m_tFormat.isPaintBoundaryLines()) {    // -> | (left)
                g2D.drawLine( origX,( int )( origY + m_tFormat.getLineStroke().floatValue()),origX,( int )( origY + rowHeight - ( 4 * m_tFormat.getLineStroke().floatValue())));
            }

            curX += m_tFormat.getVLineStroke().floatValue();
        }

        // X - start line

        if( m_tFormat.isPaintHeaderLines()) {
            g2D.setPaint( m_tFormat.getHeaderLine_Color());
            g2D.setStroke( m_tFormat.getHeader_Stroke());
            g2D.drawLine( origX,origY,    // -> - (top)
                ( int )( origX + colWidth - m_tFormat.getVLineStroke().floatValue()),origY );
        }

        curY += ( 2 * m_tFormat.getLineStroke().floatValue());    // thick

        // Background

        Color bg = getBackground( HEADER_ROW,col );

        if( !bg.equals( Color.white )) {
            g2D.setPaint( bg );
            g2D.fillRect( curX,( int )( curY - m_tFormat.getLineStroke().floatValue()),( int )( colWidth - m_tFormat.getVLineStroke().floatValue()),( int )( rowHeight - ( 4 * m_tFormat.getLineStroke().floatValue())));
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

            boolean fastDraw = LayoutEngine.s_FASTDRAW;

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

                    if( fastDraw ) {                     // Bug - set Font/Color explicitly
                        g2D.setFont( getFont( HEADER_ROW,col ));
                        g2D.setColor( getColor( HEADER_ROW,col ));
                        g2D.drawString( iter,penX,curY );
                    } else {
                        layout.draw( g2D,penX,curY );    // -> text
                    }

                    curY       += layout.getDescent() + layout.getLeading();
                    usedHeight += lineHeight;
                }

                if( !m_multiLineHeader ) {               // one line only
                    break;
                }
            }
        }                                                // length > 0

        curX += netWidth + H_GAP;
        curY += V_GAP;

        // Y end line

        g2D.setPaint( m_tFormat.getVLine_Color());
        g2D.setStroke( m_tFormat.getVLine_Stroke());

        if( m_tFormat.isPaintVLines()) {    // -> | (right)
            g2D.drawLine( curX,( int )( origY + m_tFormat.getLineStroke().floatValue()),curX,( int )( origY + rowHeight - ( 4 * m_tFormat.getLineStroke().floatValue())));
        }

        curX += m_tFormat.getVLineStroke().floatValue();

        // X end line

        if( m_tFormat.isPaintHeaderLines()) {
            g2D.setPaint( m_tFormat.getHeaderLine_Color());
            g2D.setStroke( m_tFormat.getHeader_Stroke());
            g2D.drawLine( origX,curY,    // -> - (button)
                ( int )( origX + colWidth - m_tFormat.getVLineStroke().floatValue()),curY );
        }

        curY += ( 2 * m_tFormat.getLineStroke().floatValue());    // thick

        // paint Data              ***************************************************

        for( int row = firstRow;row < nextPageRow;row++ ) {
            rowHeight = (( Float )m_rowHeights.get( row )).intValue();    // includes 2*Gaps+Line
            netHeight = rowHeight - ( 2 * V_GAP ) - m_tFormat.getLineStroke().floatValue();

            int rowYstart = curY;

            curX = origX;

            if( leftVline )                          // draw left | line
            {
                g2D.setPaint( m_tFormat.getVLine_Color());
                g2D.setStroke( m_tFormat.getVLine_Stroke());

                if( m_tFormat.isPaintBoundaryLines()) {
                    g2D.drawLine( curX,rowYstart,    // -> | (left)
                        curX,( int )( rowYstart + rowHeight - m_tFormat.getLineStroke().floatValue()));
                }

                curX += m_tFormat.getVLineStroke().floatValue();
            }

            // Background

            bg = getBackground( row,col );

            if( !bg.equals( Color.white )) {
                g2D.setPaint( bg );
                g2D.fillRect( curX,curY,( int )( colWidth - m_tFormat.getVLineStroke().floatValue()),( int )( rowHeight - m_tFormat.getLineStroke().floatValue()));
            }

            curX += H_GAP;    // upper left gap
            curY += V_GAP;

            // actual data

            Object[] printItems = getPrintItems( row,col );
            float    penY       = curY;

            for( int index = 0;index < printItems.length;index++ ) {
                if( printItems[ index ] == null ) {
                    ;
                } else if( printItems[ index ] instanceof ImageElement ) {
                    g2D.drawImage((( ImageElement )printItems[ index ] ).getImage(),curX,( int )penY,this );
                } else if( printItems[ index ] instanceof Boolean ) {
                    int penX = curX + ( int )(( netWidth - LayoutEngine.IMAGE_SIZE.width ) / 2 );    // center

                    if((( Boolean )printItems[ index ] ).booleanValue()) {
                        g2D.drawImage( LayoutEngine.IMAGE_TRUE,penX,( int )penY,this );
                    } else {
                        g2D.drawImage( LayoutEngine.IMAGE_FALSE,penX,( int )penY,this );
                    }

                    penY += LayoutEngine.IMAGE_SIZE.height;
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

                            boolean fastDraw = LayoutEngine.s_FASTDRAW;

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

                                        g2D.drawString( iter,penX,penY );
                                    } else {
                                        layout.draw( g2D,penX,penY );    // -> text
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

            if( m_tFormat.isPaintVLines()) {
                g2D.drawLine( curX,rowYstart,    // -> | (right)
                    curX,( int )( rowYstart + rowHeight - m_tFormat.getLineStroke().floatValue()));
            }

            curX += m_tFormat.getVLineStroke().floatValue();

            // X end line

            if( row == m_data.length - 1 )    // last Line
            {
                g2D.setPaint( m_tFormat.getHeaderLine_Color());
                g2D.setStroke( m_tFormat.getHeader_Stroke());
                g2D.drawLine( origX,curY,     // -> - (last line)
                    ( int )( origX + colWidth - m_tFormat.getVLineStroke().floatValue()),curY );
                curY += ( 2 * m_tFormat.getLineStroke().floatValue());    // thick
            } else {

                // next line is a funcion column -> underline this

                boolean nextIsFunction = m_functionRows.contains( new Integer( row + 1 ));

                if( nextIsFunction && m_functionRows.contains( new Integer( row ))) {
                    nextIsFunction = false;      // this is a function line too
                }

                if( nextIsFunction ) {
                    g2D.setPaint( m_tFormat.getFunctFG_Color());
                    g2D.setStroke( m_tFormat.getHLine_Stroke());
                    g2D.drawLine( origX,curY,    // -> - (bottom)
                        ( int )( origX + colWidth - m_tFormat.getVLineStroke().floatValue()),curY );
                } else if( m_tFormat.isPaintHLines()) {
                    g2D.setPaint( m_tFormat.getHLine_Color());
                    g2D.setStroke( m_tFormat.getHLine_Stroke());
                    g2D.drawLine( origX,curY,    // -> - (bottom)
                        ( int )( origX + colWidth - m_tFormat.getVLineStroke().floatValue()),curY );
                }

                curY += m_tFormat.getLineStroke().floatValue();
            }
        }    // for all rows
    }        // printColumn

    /**
     * Descripci�n de M�todo
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
     * Descripci�n de M�todo
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
}    // TableElement



/*
 *  @(#)TableElement.java   12.10.07
 * 
 *  Fin del fichero TableElement.java
 *  
 *  Versión 2.2
 *
 */
