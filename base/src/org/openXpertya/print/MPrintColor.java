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

import java.awt.Color;
import java.awt.SystemColor;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_PrintColor;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPrintColor extends X_AD_PrintColor {

    /**
     * Descripción de Método
     *
     *
     * @param color
     * @param name
     *
     * @return
     */

    static MPrintColor create( Color color,String name ) {
        MPrintColor pc = new MPrintColor( Env.getCtx(),0,null );

        pc.setName( name );
        pc.setColor( color );
        pc.save();

        return pc;
    }    // create

    /** Descripción de Campos */

    public static final Color darkGreen = new Color( 0,128,0 );

    /** Descripción de Campos */

    public static final Color blackGreen = new Color( 0,64,0 );

    /** Descripción de Campos */

    public static final Color darkBlue = new Color( 0,0,128 );

    /** Descripción de Campos */

    public static final Color blackBlue = new Color( 0,0,64 );

    /** Descripción de Campos */

    public static final Color whiteGray = new Color( 224,224,224 );

    /** Descripción de Campos */

    public static final Color brown = new Color( 153,102,51 );

    /** Descripción de Campos */

    public static final Color darkBrown = new Color( 102,51,0 );

    /** Descripción de Campos */

    static private CCache s_colors = new CCache( "AD_PrintColor",20 );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MPrintColor.class );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_PrintColor_ID
     *
     * @return
     */

    static public MPrintColor get( Properties ctx,int AD_PrintColor_ID ) {

        // if (AD_PrintColor_ID == 0)
        // return new MPrintColor (ctx, 0);

        Integer     key = new Integer( AD_PrintColor_ID );
        MPrintColor pc  = ( MPrintColor )s_colors.get( key );

        if( pc == null ) {
            pc = new MPrintColor( ctx,AD_PrintColor_ID,null );
            s_colors.put( key,pc );
        }

        return pc;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_PrintColor_ID
     *
     * @return
     */

    static public MPrintColor get( Properties ctx,String AD_PrintColor_ID ) {
        if( (AD_PrintColor_ID == null) || (AD_PrintColor_ID.length() == 0) ) {
            return null;
        }

        try {
            int id = Integer.parseInt( AD_PrintColor_ID );

            return get( ctx,id );
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"AD_PrintColor_ID=" + AD_PrintColor_ID + " - " + e.toString());
        }

        return null;
    }    // get

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_PrintColor_ID
     * @param trxName
     */

    public MPrintColor( Properties ctx,int AD_PrintColor_ID,String trxName ) {
        super( ctx,AD_PrintColor_ID,trxName );

        if( AD_PrintColor_ID == 0 ) {
            setIsDefault( false );
        }
    }    // MPrintColor

    
    public MPrintColor( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPrintColor
    
    /** Descripción de Campos */

    private Color m_cacheColor = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getColor() {
        if( m_cacheColor != null ) {
            return m_cacheColor;
        }

        String code = getCode();

        if( (code == null) || code.equals( "." )) {
            m_cacheColor = Color.black;
        }

        try {
            if( (code != null) &&!code.equals( "." )) {
                int rgba = Integer.parseInt( code );

                m_cacheColor = new Color( rgba,false );
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"MPrintColor.getColor",e );
        }

        if( code == null ) {
            m_cacheColor = Color.black;
        }

        // log.fine( "MPrintColor.getColor " + code, m_cacheColor);

        return m_cacheColor;
    }    // getColor

    /**
     * Descripción de Método
     *
     *
     * @param color
     */

    public void setColor( Color color ) {
        int rgba = color.getRGB();

        super.setCode( String.valueOf( rgba ));
    }    // setColor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getRRGGBB() {
        Color        color = getColor();
        StringBuffer sb    = new StringBuffer();

        sb.append( Util.toHex(( byte )color.getRed())).append( Util.toHex(( byte )color.getGreen())).append( Util.toHex(( byte )color.getBlue()));

        return sb.toString();
    }    // getRRGGBB

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPrintColor[" );

        sb.append( "ID=" ).append( getID()).append( ",Name=" ).append( getName()).append( ",RGB=" ).append( getCode()).append( "," ).append( getColor()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        org.openXpertya.OpenXpertya.startupEnvironment( true );

        Color[] colors = new Color[] {
            Color.black,Color.red,Color.green,Color.blue,Color.darkGray,Color.gray,Color.lightGray,Color.white,Color.cyan,Color.magenta,Color.orange,Color.pink,Color.yellow,SystemColor.textHighlight
        };
        String[] names = new String[] {
            "Black","Red","Green","Blue","Gray dark","Gray","Gray light","White","Cyan","Magenta","Orange","Pink","Yellow","Blue dark"
        };

        for( int i = 0;i < colors.length;i++ ) {
            System.out.println( names[ i ] + " = " + colors[ i ] + " RGB=" + colors[ i ].getRGB() + " -> " + new Color( colors[ i ].getRGB(),false ) + " -> " + new Color( colors[ i ].getRGB(),true ));
        }

        // Read All Colors

        int[] IDs = PO.getAllIDs( "AD_PrintColor",null,null );

        for( int i = 0;i < IDs.length;i++ ) {
            MPrintColor pc = new MPrintColor( Env.getCtx(),IDs[ i ],null );

            System.out.println( IDs[ i ] + ": " + pc + " = " + pc.getColor() + ", RGB=" + pc.getColor().getRGB());
        }
    }    // main
}    // MPrintColor



/*
 *  @(#)MPrintColor.java   23.03.06
 * 
 *  Fin del fichero MPrintColor.java
 *  
 *  Versión 2.2
 *
 */
