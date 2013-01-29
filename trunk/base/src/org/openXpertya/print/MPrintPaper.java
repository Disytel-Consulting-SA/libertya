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

import java.util.Properties;

import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_PrintPaper;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPrintPaper extends X_AD_PrintPaper {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_PrintPaper_ID
     * @param trxName
     */

    private MPrintPaper( Properties ctx,int AD_PrintPaper_ID,String trxName ) {
        super( ctx,AD_PrintPaper_ID,trxName );

        if( AD_PrintPaper_ID == 0 ) {
            setIsDefault( false );
            setIsLandscape( true );
            setCode( "iso-a4" );
            setMarginTop( 36 );
            setMarginBottom( 36 );
            setMarginLeft( 36 );
            setMarginRight( 36 );
        }
    }    // MPrintPaper

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MPrintPaper.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MediaSize getMediaSize() {
        String nameCode = getCode();

        if( nameCode == null ) {
            return getMediaSizeDefault();
        }

        // Get Name

        MediaSizeName nameMedia = null;

        if( nameCode.equals( "iso-a4" )) {
            nameMedia = MediaSizeName.ISO_A4;
        } else if( nameCode.equals( "na-letter" )) {
            nameMedia = MediaSizeName.NA_LETTER;
        } else if( nameCode.equals( "na-legal" )) {
            nameMedia = MediaSizeName.NA_LEGAL;
        }

        // other media sizes come here

        if( nameMedia == null ) {
            return getMediaSizeDefault();
        }

        //

        MediaSize retValue = MediaSize.getMediaSizeForName( nameMedia );

        if( retValue == null ) {
            retValue = getMediaSizeDefault();
        }

        // log.fine( "MPrintPaper.getMediaSize", retValue);

        return retValue;
    }    // getMediaSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MediaSize getMediaSizeDefault() {
        MediaSize retValue = Language.getLoginLanguage().getMediaSize();

        if( retValue == null ) {
            retValue = MediaSize.ISO.A4;
        }

        log.fine( retValue.toString());

        return retValue;
    }    // getMediaSizeDefault

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CPaper getCPaper() {
        CPaper retValue = new CPaper( getMediaSize(),isLandscape(),getMarginLeft(),getMarginTop(),getMarginRight(),getMarginBottom());

        return retValue;
    }    // getCPaper

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param landscape
     *
     * @return
     */

    static MPrintPaper create( String name,boolean landscape ) {
        MPrintPaper pp = new MPrintPaper( Env.getCtx(),0,null );

        pp.setName( name );
        pp.setIsLandscape( landscape );
        pp.save();

        return pp;
    }    // create

    /** Descripción de Campos */

    static private CCache s_papers = new CCache( "AD_PrintPaper",5 );

    /**
     * Descripción de Método
     *
     *
     * @param AD_PrintPaper_ID
     *
     * @return
     */

    static public MPrintPaper get( int AD_PrintPaper_ID ) {
        Integer     key = new Integer( AD_PrintPaper_ID );
        MPrintPaper pp  = ( MPrintPaper )s_papers.get( key );

        if( pp == null ) {
            pp = new MPrintPaper( Env.getCtx(),AD_PrintPaper_ID,null );
            s_papers.put( key,pp );
        } else {
            s_log.config( "AD_PrintPaper_ID=" + AD_PrintPaper_ID );
        }

        return pp;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        org.openXpertya.OpenXpertya.startupEnvironment( true );

        // create ("Standard Landscape", true);
        // create ("Standard Portrait", false);

        // Read All Papers

        int[] IDs = PO.getAllIDs( "AD_PrintPaper",null,null );

        for( int i = 0;i < IDs.length;i++ ) {
            System.out.println( "--" );

            MPrintPaper pp = new MPrintPaper( Env.getCtx(),IDs[ i ],null );

            pp.dump();
        }
    }
}    // MPrintPaper



/*
 *  @(#)MPrintPaper.java   23.03.06
 * 
 *  Fin del fichero MPrintPaper.java
 *  
 *  Versión 2.2
 *
 */
