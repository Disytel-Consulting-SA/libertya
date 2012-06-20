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
import java.awt.Font;
import java.awt.Stroke;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.X_AD_PrintTableFormat;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPrintTableFormat extends X_AD_PrintTableFormat {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_PrintTableFormat_ID
     * @param trxName
     */

    public MPrintTableFormat( Properties ctx,int AD_PrintTableFormat_ID,String trxName ) {
        super( ctx,AD_PrintTableFormat_ID,trxName );

        if( AD_PrintTableFormat_ID == 0 ) {

            // setName (null);

            setIsDefault( false );
            setIsPaintHeaderLines( true );    // Y
            setIsPaintBoundaryLines( false );
            setIsPaintHLines( false );
            setIsPaintVLines( false );
            setIsPrintFunctionSymbols( true );
        }
    }                                         // MPrintTableFormat

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPrintTableFormat( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPrintTableFormat

    /** Descripción de Campos */

    private Font standard_Font = new Font( null );

    /** Descripción de Campos */

    private Font pageHeader_Font;

    /** Descripción de Campos */

    private Font pageFooter_Font;

    /** Descripción de Campos */

    private Color pageHeaderFG_Color;

    /** Descripción de Campos */

    private Color pageHeaderBG_Color;

    /** Descripción de Campos */

    private Color pageFooterFG_Color;

    /** Descripción de Campos */

    private Color pageFooterBG_Color;

    /** Descripción de Campos */

    private Font parameter_Font;

    /** Descripción de Campos */

    private Color parameter_Color;

    /** Descripción de Campos */

    private Font header_Font;

    /** Descripción de Campos */

    private Color headerFG_Color;

    /** Descripción de Campos */

    private Color headerBG_Color;

    /** Descripción de Campos */

    private Color hdrLine_Color;

    /** Descripción de Campos */

    private Stroke header_Stroke;    // -

    /** Descripción de Campos */

    private Font funct_Font;

    /** Descripción de Campos */

    private Color functFG_Color;

    /** Descripción de Campos */

    private Color functBG_Color;

    /** Descripción de Campos */

    private Color lineH_Color;

    /** Descripción de Campos */

    private Color lineV_Color;

    /** Descripción de Campos */

    private Stroke lineH_Stroke;    // -

    /** Descripción de Campos */

    private Stroke lineV_Stroke;    // |

    //

    /**
     * Descripción de Método
     *
     *
     * @param standard_Font
     */

    public void setStandard_Font( Font standard_Font ) {
        if( standard_Font != null ) {
            this.standard_Font = standard_Font;
        }
    }    // setStandard_Font

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Font getStandard_Font() {
        return standard_Font;
    }    // getStandard_Font

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Font getHeader_Font() {
        if( header_Font != null ) {
            return header_Font;
        }

        int i = getHdr_PrintFont_ID();

        if( i != 0 ) {
            header_Font = MPrintFont.get( i ).getFont();
        }

        if( header_Font == null ) {
            header_Font = new Font( standard_Font.getName(),Font.BOLD,standard_Font.getSize());
        }

        return header_Font;
    }    // getHeader_Font

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getHeaderFG_Color() {
        if( headerFG_Color != null ) {
            return headerFG_Color;
        }

        int i = getHdrTextFG_PrintColor_ID();

        if( i != 0 ) {
            headerFG_Color = MPrintColor.get( getCtx(),i ).getColor();
        }

        if( headerFG_Color == null ) {
            headerFG_Color = MPrintColor.blackBlue;
        }

        return headerFG_Color;
    }    // getHeaderFG_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getHeaderBG_Color() {
        if( headerBG_Color != null ) {
            return headerBG_Color;
        }

        int i = getHdrTextBG_PrintColor_ID();

        if( i != 0 ) {
            headerBG_Color = MPrintColor.get( getCtx(),i ).getColor();
        }

        if( headerBG_Color == null ) {
            headerBG_Color = Color.cyan;
        }

        return headerBG_Color;
    }    // getHeaderBG_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getHeaderLine_Color() {
        if( hdrLine_Color != null ) {
            return hdrLine_Color;
        }

        int i = getHdrLine_PrintColor_ID();

        if( i != 0 ) {
            hdrLine_Color = MPrintColor.get( getCtx(),i ).getColor();
        }

        if( hdrLine_Color == null ) {
            hdrLine_Color = MPrintColor.blackBlue;
        }

        return hdrLine_Color;
    }    // getHeaderLine_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Stroke getHeader_Stroke() {
        if( header_Stroke == null ) {
            float width = getHdrStroke().floatValue();

            if( (getHdrStrokeType() == null) || HDRSTROKETYPE_SolidLine.equals( getHdrStrokeType())) {
                header_Stroke = new BasicStroke( width );                                                                                    // -

                //

            } else if( HDRSTROKETYPE_DashedLine.equals( getHdrStrokeType())) {
                header_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDashed( width ),0.0f );    // - -
            } else if( HDRSTROKETYPE_DottedLine.equals( getHdrStrokeType())) {
                header_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDotted( width ),0.0f );    // . . .
            } else if( HDRSTROKETYPE_Dash_DottedLine.equals( getHdrStrokeType())) {
                header_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDash_Dotted( width ),0.0f );    // - . -
            }

            // default / fallback

            if( header_Stroke == null ) {
                header_Stroke = new BasicStroke( width );    // -
            }
        }

        return header_Stroke;
    }    // getHeader_Stroke

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getHdrStroke() {
        BigDecimal retValue = super.getHdrStroke();

        if( (retValue == null) || (Env.ZERO.compareTo( retValue ) <= 0) ) {
            retValue = new BigDecimal( 2.0 );
        }

        return retValue;
    }    // getHdrStroke

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Font getFunct_Font() {
        if( funct_Font != null ) {
            return funct_Font;
        }

        int i = getFunct_PrintFont_ID();

        if( i != 0 ) {
            funct_Font = MPrintFont.get( i ).getFont();
        }

        if( funct_Font == null ) {
            funct_Font = new Font( standard_Font.getName(),Font.BOLD | Font.ITALIC,standard_Font.getSize());
        }

        return funct_Font;
    }    // getFunct_Font

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getFunctBG_Color() {
        if( functBG_Color != null ) {
            return functBG_Color;
        }

        int i = getFunctBG_PrintColor_ID();

        if( i != 0 ) {
            functBG_Color = MPrintColor.get( getCtx(),i ).getColor();
        }

        if( functBG_Color == null ) {
            functBG_Color = Color.white;
        }

        return functBG_Color;
    }    // getFunctBG_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getFunctFG_Color() {
        if( functFG_Color != null ) {
            return functFG_Color;
        }

        int i = getFunctFG_PrintColor_ID();

        if( i != 0 ) {
            functFG_Color = MPrintColor.get( getCtx(),i ).getColor();
        }

        if( functFG_Color == null ) {
            functFG_Color = MPrintColor.darkGreen;
        }

        return functFG_Color;
    }    // getFunctFG_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Font getParameter_Font() {
        if( parameter_Font == null ) {
            parameter_Font = new Font( standard_Font.getName(),Font.ITALIC,standard_Font.getSize());
        }

        return parameter_Font;
    }    // getParameter_Font

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getParameter_Color() {
        if( parameter_Color == null ) {
            parameter_Color = Color.darkGray;
        }

        return parameter_Color;
    }    // getParameter_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Font getPageHeader_Font() {
        if( pageHeader_Font == null ) {
            pageHeader_Font = new Font( standard_Font.getName(),Font.BOLD,standard_Font.getSize());
        }

        return pageHeader_Font;
    }    // getPageHeader_Font

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getPageHeaderFG_Color() {
        if( pageHeaderFG_Color == null ) {
            pageHeaderFG_Color = MPrintColor.blackBlue;
        }

        return pageHeaderFG_Color;
    }    // getPageHeaderFG_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getPageHeaderBG_Color() {
        if( pageHeaderBG_Color == null ) {
            pageHeaderBG_Color = Color.white;
        }

        return pageHeaderBG_Color;
    }    // getPageHeaderBG_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Font getPageFooter_Font() {
        if( pageFooter_Font == null ) {
            pageFooter_Font = new Font( standard_Font.getName(),Font.PLAIN,standard_Font.getSize() - 2 );
        }

        return pageFooter_Font;
    }    // getPageFooter_Font

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getPageFooterFG_Color() {
        if( pageFooterFG_Color == null ) {
            pageFooterFG_Color = MPrintColor.blackBlue;
        }

        return pageFooterFG_Color;
    }    // getPageFooterFG_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getPageFooterBG_Color() {
        if( pageFooterBG_Color == null ) {
            pageFooterBG_Color = Color.white;
        }

        return pageFooterBG_Color;
    }    // getPageFooterBG_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getHLine_Color() {
        if( lineH_Color != null ) {
            return lineH_Color;
        }

        int i = getLine_PrintColor_ID();

        if( i != 0 ) {
            lineH_Color = MPrintColor.get( getCtx(),i ).getColor();
        }

        if( lineH_Color == null ) {
            lineH_Color = Color.lightGray;
        }

        return lineH_Color;
    }    // getHLine_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getVLine_Color() {
        if( lineV_Color != null ) {
            return lineV_Color;
        }

        int i = getLine_PrintColor_ID();

        if( i != 0 ) {
            lineV_Color = MPrintColor.get( getCtx(),i ).getColor();
        }

        if( lineV_Color == null ) {
            lineV_Color = Color.lightGray;
        }

        return lineV_Color;
    }    // getVLine_Color

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Stroke getHLine_Stroke() {
        if( lineH_Stroke == null ) {
            float width = getLineStroke().floatValue() / 2;

            if( (getHdrStrokeType() == null) || LINESTROKETYPE_DottedLine.equals( getLineStrokeType())) {
                lineH_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDotted( width ),0.0f );    // . . .

                //

            } else if( LINESTROKETYPE_SolidLine.equals( getLineStrokeType())) {
                lineH_Stroke = new BasicStroke( width );    // -
            } else if( LINESTROKETYPE_DashedLine.equals( getLineStrokeType())) {
                lineH_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDashed( width ),0.0f );    // - -
            } else if( LINESTROKETYPE_Dash_DottedLine.equals( getLineStrokeType())) {
                lineH_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDash_Dotted( width ),0.0f );    // - . -
            }

            // default / fallback

            if( lineH_Stroke == null ) {
                lineH_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDotted( width ),0.0f );    // . . .
            }
        }

        return lineH_Stroke;
    }    // getHLine_Stroke

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Stroke getVLine_Stroke() {
        if( lineV_Stroke == null ) {
            float width = getLineStroke().floatValue() / 2;

            if( (getHdrStrokeType() == null) || LINESTROKETYPE_DottedLine.equals( getLineStrokeType())) {
                lineV_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDotted( width ),0.0f );    // . . .

                //

            } else if( LINESTROKETYPE_SolidLine.equals( getLineStrokeType())) {
                lineV_Stroke = new BasicStroke( width );    // -
            } else if( LINESTROKETYPE_DashedLine.equals( getLineStrokeType())) {
                lineV_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDashed( width ),0.0f );    // - -
            } else if( LINESTROKETYPE_Dash_DottedLine.equals( getLineStrokeType())) {
                lineV_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDash_Dotted( width ),0.0f );    // - . -
            }

            // default / fallback

            if( lineV_Stroke == null ) {
                lineV_Stroke = new BasicStroke( width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f,getPatternDotted( width ),0.0f );    // . . .
            }
        }

        return lineV_Stroke;
    }    // getVLine_Stroke

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getLineStroke() {
        BigDecimal retValue = super.getLineStroke();

        if( (retValue == null) || (Env.ZERO.compareTo( retValue ) <= 0) ) {
            retValue = new BigDecimal( 1.0 );
        }

        return retValue;
    }    // getLineStroke

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getVLineStroke() {
        BigDecimal retValue = super.getLineStroke();

        if( (retValue == null) || (Env.ZERO.compareTo( retValue ) <= 0) ) {
            retValue = new BigDecimal( 1.0 );
        }

        return retValue;
    }    // getVLineStroke

    /**
     * Descripción de Método
     *
     *
     * @param width
     *
     * @return
     */

    private float[] getPatternDotted( float width ) {
        return new float[]{ 2 * width,2 * width };
    }    // getPatternDotted

    /**
     * Descripción de Método
     *
     *
     * @param width
     *
     * @return
     */

    private float[] getPatternDashed( float width ) {
        return new float[]{ 10 * width,4 * width };
    }    // getPatternDashed

    /**
     * Descripción de Método
     *
     *
     * @param width
     *
     * @return
     */

    private float[] getPatternDash_Dotted( float width ) {
        return new float[]{ 10 * width,2 * width,2 * width,2 * width };
    }    // getPatternDash_Dotted

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "AD_PrintTableFormat",3 );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MPrintTableFormat.class );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_PrintTableFormat_ID
     * @param standard_font
     *
     * @return
     */

    static public MPrintTableFormat get( Properties ctx,int AD_PrintTableFormat_ID,Font standard_font ) {
        Integer           ii = new Integer( AD_PrintTableFormat_ID );
        MPrintTableFormat tf = ( MPrintTableFormat )s_cache.get( ii );

        if( tf == null ) {
            if( AD_PrintTableFormat_ID == 0 ) {
                tf = getDefault( ctx );
            } else {
                tf = new MPrintTableFormat( ctx,AD_PrintTableFormat_ID,null );
            }

            s_cache.put( ii,tf );
        }

        tf.setStandard_Font( standard_font );

        return tf;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_PrintTableFormat_ID
     * @param AD_PrintFont_ID
     *
     * @return
     */

    static public MPrintTableFormat get( Properties ctx,int AD_PrintTableFormat_ID,int AD_PrintFont_ID ) {
        return get( ctx,AD_PrintTableFormat_ID,MPrintFont.get( AD_PrintFont_ID ).getFont());
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    static public MPrintTableFormat getDefault( Properties ctx ) {
        MPrintTableFormat tf  = null;
        String            sql = "SELECT * FROM AD_PrintTableFormat " + "WHERE AD_Client_ID IN (0,?) AND IsActive='Y' " + "ORDER BY IsDefault DESC, AD_Client_ID DESC";
        int               AD_Client_ID = Env.getAD_Client_ID( ctx );
        PreparedStatement pstmt        = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                tf = new MPrintTableFormat( ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getDefault",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return tf;
    }    // get
}    // MPrintTableFormat



/*
 *  @(#)MPrintTableFormat.java   23.03.06
 * 
 *  Fin del fichero MPrintTableFormat.java
 *  
 *  Versión 2.2
 *
 */
