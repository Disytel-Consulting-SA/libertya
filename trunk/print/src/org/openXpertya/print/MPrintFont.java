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
import java.awt.GraphicsEnvironment;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_PrintFont;
import org.openXpertya.util.CCache;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPrintFont extends X_AD_PrintFont {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_PrintFont_ID
     * @param trxName
     */

    private MPrintFont( Properties ctx,int AD_PrintFont_ID,String trxName ) {
        super( ctx,AD_PrintFont_ID,trxName );

        if( AD_PrintFont_ID == 0 ) {
            setIsDefault( false );
        }
    }    // MPrintFont

    /** Descripción de Campos */

    private Font m_cacheFont = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Font getFont() {
        if( m_cacheFont != null ) {
            return m_cacheFont;
        }

        String code = ( String )get_Value( "Code" );

        if( (code == null) || code.equals( "." )) {
            m_cacheFont = new Font( null );
        }

        try {
            if( (code != null) &&!code.equals( "." )) {

                // fontfamilyname-style-pointsize

                m_cacheFont = Font.decode( code );
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"MPrintFont.getFont",e );
        }

        if( code == null ) {
            m_cacheFont = new Font( null );    // family=dialog,name=Dialog,style=plain,size=12
        }

        // log.fine( "MPrintFont.getFont " + code, m_cacheFont);

        return m_cacheFont;
    }    // getFont

    /**
     * Descripción de Método
     *
     *
     * @param font
     */

    public void setFont( Font font ) {

        // fontfamilyname-style-pointsize

        StringBuffer sb = new StringBuffer();

        sb.append( font.getFamily()).append( "-" );

        int style = font.getStyle();

        if( style == Font.PLAIN ) {
            sb.append( "PLAIN" );
        } else if( style == Font.BOLD ) {
            sb.append( "BOLD" );
        } else if( style == Font.ITALIC ) {
            sb.append( "ITALIC" );
        } else if( style == ( Font.BOLD + Font.ITALIC )) {
            sb.append( "BOLDITALIC" );
        }

        sb.append( "-" ).append( font.getSize());
        setCode( sb.toString());
    }    // setFont

    /**
     * Descripción de Método
     *
     *
     * @param font
     *
     * @return
     */

    static MPrintFont create( Font font ) {
        MPrintFont   pf   = new MPrintFont( Env.getCtx(),0,null );
        StringBuffer name = new StringBuffer( font.getName());

        if( font.isBold()) {
            name.append( " bold" );
        }

        if( font.isItalic()) {
            name.append( " italic" );
        }

        name.append( " " ).append( font.getSize());
        pf.setName( name.toString());
        pf.setFont( font );
        pf.save();

        return pf;
    }    // create

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPrintFont[" );

        sb.append( "ID=" ).append( getID()).append( ",Name=" ).append( getName()).append( "PSName=" ).append( getFont().getPSName()).append( getFont()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toPS() {
        StringBuffer sb = new StringBuffer( "/" );

        sb.append( getFont().getPSName());

        if( getFont().isBold()) {
            sb.append( " Bold" );
        }

        if( getFont().isItalic()) {
            sb.append( " Italic" );
        }

        sb.append( " " ).append( getFont().getSize()).append( " selectfont" );

        return sb.toString();
    }    // toPS

    /**
     * Descripción de Método
     *
     *
     * @param font
     */

    static void dump( Font font ) {
        System.out.println( "Family=" + font.getFamily());
        System.out.println( "FontName=" + font.getFontName());
        System.out.println( "Name=" + font.getName());
        System.out.println( "PSName=" + font.getPSName());
        System.out.println( "Style=" + font.getStyle());
        System.out.println( "Size=" + font.getSize());
        System.out.println( "Attributes:" );

        Map      map  = font.getAttributes();
        Iterator keys = map.keySet().iterator();

        while( keys.hasNext()) {
            Object key   = keys.next();
            Object value = map.get( key );

            System.out.println( " - " + key + "=" + value );
        }

        System.out.println( font );
    }    // dump

    /** Descripción de Campos */

    static private CCache s_fonts = new CCache( "AD_PrintFont",20 );

    /**
     * Descripción de Método
     *
     *
     * @param AD_PrintFont_ID
     *
     * @return
     */

    static public MPrintFont get( int AD_PrintFont_ID ) {
        Integer    key = new Integer( AD_PrintFont_ID );
        MPrintFont pf  = ( MPrintFont )s_fonts.get( key );

        if( pf == null ) {
            pf = new MPrintFont( Env.getCtx(),AD_PrintFont_ID,null );
            s_fonts.put( key,pf );
        }

        return pf;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        System.out.println( "Available Fonts:" );

        String[] family = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for( int i = 0;i < family.length;i++ ) {
            System.out.println( " - " + family[ i ] );
        }

        org.openXpertya.OpenXpertya.startup( true );

        MPrintFont pf = new MPrintFont( Env.getCtx(),100,null );

        dump( pf.getFont());

        String[] systemLocical = new String[]{ "Dialog","DialogInput","Monospaced","Serif","SansSerif" };

        for( int i = 0;i < systemLocical.length;i++ ) {

            // create(new Font(systemLocical[i], Font.BOLD, 13));
            // create(new Font(systemLocical[i], Font.PLAIN, 11));
            // create(new Font(systemLocical[i], Font.BOLD, 11));
            // create(new Font(systemLocical[i], Font.ITALIC, 11));
            // create(new Font(systemLocical[i], Font.PLAIN, 10));
            // create(new Font(systemLocical[i], Font.BOLD, 10));
            // create(new Font(systemLocical[i], Font.ITALIC, 10));
            // create(new Font(systemLocical[i], Font.PLAIN, 9));
            // create(new Font(systemLocical[i], Font.BOLD, 9));
            // create(new Font(systemLocical[i], Font.ITALIC, 9));
            // create(new Font(systemLocical[i], Font.PLAIN, 8));
            // create(new Font(systemLocical[i], Font.BOLD, 8));
            // create(new Font(systemLocical[i], Font.ITALIC, 8));

        }

        // Read All Fonts

        int[] IDs = PO.getAllIDs( "AD_PrintFont",null,null );

        for( int i = 0;i < IDs.length;i++ ) {
            pf = new MPrintFont( Env.getCtx(),IDs[ i ],null );
            System.out.println( IDs[ i ] + " = " + pf.getFont());
        }
    }    // main
}    // MPrintFont



/*
 *  @(#)MPrintFont.java   23.03.06
 * 
 *  Fin del fichero MPrintFont.java
 *  
 *  Versión 2.2
 *
 */
