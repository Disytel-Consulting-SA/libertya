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



package org.openXpertya.util;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openXpertya.OpenXpertya;
import org.openXpertya.db.CConnection;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.MLookupCache;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MSession;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class Env {

    public static ArrayList getS_windows() {
		return s_windows;
	}

	/** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( Env.class );
    
    //Cache de imagenes para no leer siempre del disco
    private static CCache  s_cacheImg  = new CCache("imagenes",10,5);
    /**
     * Descripción de Método
     *
     *
     * @param status
     */

    public static void exitEnv( int status ) {

        // End Session

        MSession session = MSession.get( Env.getCtx(),false );    // finish

        if( session != null ) {
            session.logout();
        }

        //

        reset( true );    // final cache reset
        s_log.info( "" );

        //

        CLogMgt.shutdown();

        //

        if( Ini.isClient()) {
            System.exit( status );
        }
    }    // close

    /**
	 * dREHER, Logout from the system
	 */
	public static void logout()
	{
		//	End Session
		MSession session = MSession.get(Env.getCtx(), false);	//	finish
		if (session != null)
			session.logout();
		//
		reset(true);	// final cache reset
		//
		
		CConnection.get().setAppServerCredential();
	}
    
    /**
     * Descripción de Método
     *
     *
     * @param finalCall
     */

    public static void reset( boolean finalCall ) {
        s_log.info( "reset - finalCall=" + finalCall);

        // Dismantle windows

        s_windows.clear();

        // Clear all Context

        if( finalCall ) {
            s_ctx.clear();
        } else    // clear window context only
        {
            Object[] keys = s_ctx.keySet().toArray();

            for( int i = 0;i < keys.length;i++ ) {
                String tag = keys[ i ].toString();

                if( Character.isDigit( tag.charAt( 0 ))) {
                    s_ctx.remove( keys[ i ] );
                }
            }
        }

        // Cache

        CacheMgt.get().reset();
        DB.closeTarget();

        // Reset Role Access

        if( !finalCall ) {
            DB.setDBTarget( CConnection.get());

            MRole defaultRole = MRole.getDefault( s_ctx,false );

            if( defaultRole != null ) {
                defaultRole.loadAccess( true );    // Reload
            }
        }
    }                                              // resetAll

    /** Descripción de Campos */

    private static Properties s_ctx = new Properties();

    /** Descripción de Campos */

    public static final int WINDOW_FIND = 1110;

    /** Descripción de Campos */

    public static final int WINDOW_MLOOKUP = 1111;

    /** Descripción de Campos */

    public static final int WINDOW_CUSTOMIZE = 1112;

    /** Descripción de Campos */

    public static final int WINDOW_INFO = 1113;

    /** Descripción de Campos */

    public static final int TAB_INFO = 1113;
    
    public static final String CLOSE_APPS_PROP_NAME = "#CLOSE_APP";

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static final Properties getCtx() {
    	return contextProvider.getContext();
    }    // getCtx

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     */

    public static void setCtx( Properties ctx ) {
        if( ctx == null ) {
            throw new IllegalArgumentException( "Env.setCtx - require Context" );
        }

        s_ctx.clear();
        s_ctx = ctx;
    }    // setCtx

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param context
     * @param value
     */

    public static void setContext( Properties ctx,String context,String value ) {
    	s_log.finer("En setContext1 con ctx=" +ctx+"\n"+"Contex= "+context+ "\n"+"value= "+value );
        if( (ctx == null) || (context == null) ) {
            return;
        }

        s_log.finer( "Context " + context + "==" + value );

        //

        if( (value == null) || (value.length() == 0) ) {
            ctx.remove( context );
        } else {
            ctx.setProperty( context,value );
        }
    }    // setContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param context
     * @param value
     */

    public static void setContext( Properties ctx,String context,Timestamp value ) {
    	s_log.finer("En setContext2 con ctx=" +ctx+"\n"+"Contex= "+context+ "\n"+"value= "+value );
        if( (ctx == null) || (context == null) ) {
            return;
        }

        if( value == null ) {
            ctx.remove( context );
            s_log.finer( "Context " + context + "==" + value );
        } else {    // JDBC Format     2005-05-09 00:00:00.0
            String stringValue = value.toString();

            // Chop off .0

            stringValue = stringValue.substring( 0,stringValue.length() - 2 );
            ctx.setProperty( context,stringValue );
            s_log.finer( "Context " + context + "==" + stringValue );
        }
    }    // setContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param context
     * @param value
     */

    public static void setContext( Properties ctx,String context,int value ) {
    	s_log.finer("En setContext3 con ctx=" +ctx+"\n"+"Contex= "+context+ "\n"+"value= "+value );
        if( (ctx == null) || (context == null) ) {
            return;
        }

        s_log.finer( "Context " + context + "==" + value );

        //

        ctx.setProperty( context,String.valueOf( value ));
    }    // setContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param context
     * @param value
     */

    public static void setContext( Properties ctx,String context,boolean value ) {
    	s_log.finer("En setContext4 con ctx=" +ctx+"\n"+"Contex= "+context+ "\n"+"value= "+value );
        setContext( ctx,context,value
                                ?"Y"
                                :"N" );
    }    // setContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param context
     * @param value
     */

    public static void setContext( Properties ctx,int WindowNo,String context,String value ) {
    	s_log.finer("En setContext5 con ctx=" +ctx+"\n"+"Contex= "+context+ "\n"+"value= "+value );
        if( (ctx == null) || (context == null) ) {
            return;
        }

        if( (WindowNo != WINDOW_FIND) && (WindowNo != WINDOW_MLOOKUP) ) {
            s_log.finer( "Context(" + WindowNo + ") " + context + "==" + value );
        }

        //

        if( (value == null) || value.equals( "" )) {
            ctx.remove( WindowNo + "|" + context );
        } else {
            ctx.setProperty( WindowNo + "|" + context,value );
        }
    }    // setContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param context
     * @param value
     */

    public static void setContext( Properties ctx,int WindowNo,String context,Timestamp value ) {
    	s_log.finer("En setContext6 con ctx=" +ctx+"\n"+"Contex= "+context+ "\n"+"value= "+value );
        if( (ctx == null) || (context == null) ) {
            return;
        }

        boolean logit = (WindowNo != WINDOW_FIND) && (WindowNo != WINDOW_MLOOKUP);

        if( value == null ) {
            ctx.remove( WindowNo + "|" + context );
            s_log.finer( "Context(" + WindowNo + ") " + context + "==" + value );
        } else {    // JDBC Format     2005-05-09 00:00:00.0
            String stringValue = value.toString();

            // Chop off .0

            stringValue = stringValue.substring( 0,stringValue.length() - 2 );
            ctx.setProperty( WindowNo + "|" + context,stringValue );
            s_log.finer( "Context(" + WindowNo + ") " + context + "==" + stringValue );
        }
    }    // setContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param context
     * @param value
     */

    public static void setContext( Properties ctx,int WindowNo,String context,int value ) {
    	s_log.finer("En setContext7 con ctx=" +ctx+"\n"+"Contex= "+context+ "\n"+"value= "+value );
        if( (ctx == null) || (context == null) ) {
            return;
        }

        if( (WindowNo != WINDOW_FIND) && (WindowNo != WINDOW_MLOOKUP) ) {
            s_log.finer( "Context(" + WindowNo + ") " + context + "==" + value );
        }

        //

        ctx.setProperty( WindowNo + "|" + context,String.valueOf( value ));
    }    // setContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param context
     * @param value
     */

    public static void setContext( Properties ctx,int WindowNo,String context,boolean value ) {
    	s_log.finer("En setContext8 con ctx=" +ctx+"\n"+"Contex= "+context+ "\n"+"value= "+value );
        setContext( ctx,WindowNo,context,value
                                         ?"Y"
                                         :"N" );
    }    // setContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param TabNo
     * @param context
     * @param value
     */

    public static void setContext( Properties ctx,int WindowNo,int TabNo,String context,String value ) {
    	s_log.finer("En setContext9 con ctx=" +ctx+"Contex= "+context+ "\n"+"value= "+value );
        if( (ctx == null) || (context == null) ) {
            return;
        }

        if( (WindowNo != WINDOW_FIND) && (WindowNo != WINDOW_MLOOKUP) ) {
            s_log.finest( "Context(" + WindowNo + "," + TabNo + ") " + context + "==" + value );
        }

        //

        if( (value == null) || value.equals( "" )) {
            ctx.remove( WindowNo + "|" + TabNo + "|" + context );
        } else {
            ctx.setProperty( WindowNo + "|" + TabNo + "|" + context,value );
        }
    }    // setContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param autoCommit
     */

    public static void setAutoCommit( Properties ctx,boolean autoCommit ) {
        if( ctx == null ) {
            return;
        }

        ctx.setProperty( "AutoCommit",autoCommit
                                      ?"Y"
                                      :"N" );
    }    // setAutoCommit

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param autoCommit
     */

    public static void setAutoCommit( Properties ctx,int WindowNo,boolean autoCommit ) {
        if( ctx == null ) {
            return;
        }

        ctx.setProperty( WindowNo + "|AutoCommit",autoCommit
                ?"Y"
                :"N" );
    }    // setAutoCommit

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param isSOTrx
     */

    public static void setSOTrx( Properties ctx,boolean isSOTrx ) {
        if( ctx == null ) {
            return;
        }

        ctx.setProperty( "IsSOTrx",isSOTrx
                                   ?"Y"
                                   :"N" );
    }    // setSOTrx

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param context
     *
     * @return
     */

    public static String getContext( Properties ctx,String context ) {
        if( (ctx == null) || (context == null) ) {
            throw new IllegalArgumentException( "Env.getContext - require Context" );
        }
        
        // Disytel - FB - 2010-12-23
		// Antes de retornar el valor de la fecha actual se invoca el método que
		// actualiza este valor a partir de la fecha en el servidor de BD.
        if (context.equals("#Date")) {
        	updateDateContext(ctx);
        }
        
        return ctx.getProperty( context,"" );
    }    // getContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param context
     * @param onlyWindow
     *
     * @return
     */

    public static String getContext( Properties ctx,int WindowNo,String context,boolean onlyWindow ) {
        if( ctx == null ) {
            throw new IllegalArgumentException( "Env.getContext - No Ctx" );
        }

        if( context == null ) {
            throw new IllegalArgumentException( "Env.getContext - require Context" );
        }

        String s = ctx.getProperty( WindowNo + "|" + context );

        if( s == null ) {

            // Explicit Base Values

            if( context.startsWith( "#" ) || context.startsWith( "$" )) {
                return getContext( ctx,context );
            }

            if( onlyWindow ) {    // no Default values
                return "";
            }

            return getContext( ctx,"#" + context );
        }

        return s;
    }    // getContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param context
     *
     * @return
     */

    public static String getContext( Properties ctx,int WindowNo,String context ) {
        return getContext( ctx,WindowNo,context,false );
    }    // getContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param TabNo
     * @param context
     *
     * @return
     */

    public static String getContext( Properties ctx,int WindowNo,int TabNo,String context ) {
        if( (ctx == null) || (context == null) ) {
            throw new IllegalArgumentException( "Env.getContext - require Context" );
        }

        String s = ctx.getProperty( WindowNo + "|" + TabNo + "|" + context );
        
        if( s == null ) {
            return getContext( ctx,WindowNo,context,false );
        }

        return s;
    }    // getContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param context
     *
     * @return
     */

    public static int getContextAsInt( Properties ctx,String context ) {
        if( (ctx == null) || (context == null) ) {
            throw new IllegalArgumentException( "Env.getContext - require Context" );
        }

        String s = getContext( ctx,context );

        if( s.length() == 0 ) {
            s = getContext( ctx,0,context,false );    // search 0 and defaults
        }

        if( s.length() == 0 ) {
            return 0;
        }

        //

        try {
            return Integer.parseInt( s );
        } catch( NumberFormatException e ) {
            s_log.log( Level.SEVERE,"getContextAsInt (" + context + ") = " + s,e );
        }

        return 0;
    }    // getContextAsInt

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param context
     *
     * @return
     */

    public static int getContextAsInt( Properties ctx,int WindowNo,String context ) {
        String s = getContext( ctx,WindowNo,context,false );

        if( s.length() == 0 ) {
            return 0;
        }

        //

        try {
            return Integer.parseInt( s );
        } catch( NumberFormatException e ) {
            s_log.log( Level.SEVERE,"getContextAsInt (" + context + ") = " + s,e );
        }

        return 0;
    }    // getContextAsInt

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param TabNo
     * @param context
     *
     * @return
     */

    public static int getContextAsInt( Properties ctx,int WindowNo,int TabNo,String context ) {
        String s = getContext( ctx,WindowNo,TabNo,context );

        if( s.length() == 0 ) {
            return 0;
        }

        //

        try {
            return Integer.parseInt( s );
        } catch( NumberFormatException e ) {
            s_log.log( Level.SEVERE,"getContextAsInt (" + context + ") = " + s,e );
        }

        return 0;
    }    // getContextAsInt

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static boolean isAutoCommit( Properties ctx ) {
        if( ctx == null ) {
            throw new IllegalArgumentException( "Require Context" );
        }

        String s = getContext( ctx,"AutoCommit" );

        if( (s != null) && s.equals( "Y" )) {
            return true;
        }

        return false;
    }    // isAutoCommit

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     *
     * @return
     */

    public static boolean isAutoCommit( Properties ctx,int WindowNo ) {
        if( ctx == null ) {
            throw new IllegalArgumentException( "Env.getContext - require Context" );
        }

        String s = getContext( ctx,WindowNo,"AutoCommit",false );

        if( s != null ) {
            if( s.equals( "Y" )) {
                return true;
            } else {
                return false;
            }
        }

        return isAutoCommit( ctx );
    }    // isAutoCommit

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static boolean isSOTrx( Properties ctx ) {
        if( ctx == null ) {
            throw new IllegalArgumentException( "Require Context" );
        }

        String s = getContext( ctx,"IsSOTrx" );

        if( (s != null) && s.equals( "N" )) {
            return false;
        }

        return true;
    }    // isSOTrx

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param context
     *
     * @return
     */

    public static Timestamp getContextAsDate( Properties ctx,String context ) {
        return getContextAsDate( ctx,0,context );
    }    // getContextAsDate

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param context
     *
     * @return
     */

    public static Timestamp getContextAsDate( Properties ctx,int WindowNo,String context ) {
        if( (ctx == null) || (context == null) ) {
            throw new IllegalArgumentException( "Require Context" );
        }

        String s = getContext( ctx,WindowNo,context,false );

        // JDBC Format YYYY-MM-DD  example 2000-09-11 00:00:00.0

        if( (s == null) || s.equals( "" )) {
            s_log.log( Level.SEVERE,"No value for: " + context );

            return new Timestamp( System.currentTimeMillis());
        }

        // timestamp requires time

        if( s.trim().length() == 10 ) {
            s = s.trim() + " 00:00:00.0";
        } else if( s.indexOf( '.' ) == -1 ) {
            s = s.trim() + ".0";
        }

        return Timestamp.valueOf( s );
    }    // getContextAsDate

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static int getAD_Client_ID( Properties ctx ) {
        return Env.getContextAsInt( ctx,"#AD_Client_ID" );
    }    // getAD_Client_ID

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static int getAD_Org_ID( Properties ctx ) {
        return Env.getContextAsInt( ctx,"#AD_Org_ID" );
    }    // getAD_Client_ID

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static int getAD_User_ID( Properties ctx ) {
        return Env.getContextAsInt( ctx,"#AD_User_ID" );
    }    // getAD_User_ID

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static int getAD_Role_ID( Properties ctx ) {
        return Env.getContextAsInt( ctx,"#AD_Role_ID" );
    }    // getAD_Role_ID

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static int getM_Product_Category_ID( Properties ctx ) {
        return Env.getContextAsInt( ctx,"M_Product_Category_ID" );
    }    // getM_Product_Category_ID

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static int getM_Product_Family_ID( Properties ctx ) {
        return Env.getContextAsInt( ctx,"M_Product_Family_ID" );
    }    // getM_Product_Family_ID

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static int getC_PaymentTerm_ID( Properties ctx ) {
        return Env.getContextAsInt( ctx,"C_PaymentTerm_ID" );
    }    // getC_PaymentTerm_ID

   
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Window_ID
     * @param context
     * @param system
     *
     * @return
     */

    public static String getPreference( Properties ctx,int AD_Window_ID,String context,boolean system ) {
        if( (ctx == null) || (context == null) ) {
            throw new IllegalArgumentException( "Env.getPreference - require Context" );
        }

        String retValue = null;

        //

        if( !system )                                                            // User Preferences
        {
            retValue = ctx.getProperty( "P" + AD_Window_ID + "|" + context );    // Window Pref

            if( retValue == null ) {
                retValue = ctx.getProperty( "P|" + context );    // Global Pref
            }
        } else                                                   // System Preferences
        {
            retValue = ctx.getProperty( "#" + context );         // Login setting

            if( retValue == null ) {
                retValue = ctx.getProperty( "$" + context );     // Accounting setting
            }
        }

        //

        return( (retValue == null)
                ?""
                :retValue );
    }    // getPreference

    /** Descripción de Campos */

    static public final String LANGUAGE = "#AD_Language";

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param tableName
     *
     * @return
     */

    public static boolean isBaseLanguage( Properties ctx,String tableName ) {
        return Language.isBaseLanguage( getAD_Language( ctx ));
    }    // isBaseLanguage

    /**
     * Descripción de Método
     *
     *
     * @param AD_Language
     * @param tableName
     *
     * @return
     */

    public static boolean isBaseLanguage( String AD_Language,String tableName ) {
        return Language.isBaseLanguage( AD_Language );
    }    // isBaseLanguage

    /**
     * Descripción de Método
     *
     *
     * @param language
     * @param tableName
     *
     * @return
     */

    public static boolean isBaseLanguage( Language language,String tableName ) {
        return language.isBaseLanguage();
    }    // isBaseLanguage

    /**
     * Descripción de Método
     *
     *
     * @param tableName
     *
     * @return
     */

    public static boolean isBaseTranslation( String tableName ) {
        if( tableName.startsWith( "AD" ) || tableName.equals( "C_Country_Trl" )) {
            return true;
        }

        return false;
    }    // isBaseTranslation

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static boolean isMultiLingualDocument( Properties ctx ) {
        return MClient.get( ctx ).isMultiLingualDocument();
    }    // isMultiLingualDocument

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static String getAD_Language( Properties ctx ) {
        if( ctx != null ) {
            String lang = getContext( ctx,LANGUAGE );

            if( (lang != null) || (lang.length() > 0) ) {
                return lang;
            }
        }

        return Language.getBaseAD_Language();
    }    // getAD_Language

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static Language getLanguage( Properties ctx ) {
        if( ctx != null ) {
            String lang = getContext( ctx,LANGUAGE );

            if( (lang != null) || (lang.length() > 0) ) {
                return Language.getLanguage( lang );
            }
        }

        return Language.getBaseLanguage();
    }    // getLanguage

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static Language getLoginLanguage( Properties ctx ) {
        return Language.getLoginLanguage();
    }    // getLanguage

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param language
     */

    public static void verifyLanguage( Properties ctx,Language language ) {
        if( language.isBaseLanguage()) {
            return;
        }

        boolean   isSystemLanguage = false;
        ArrayList AD_Languages     = new ArrayList();
        String    sql              = "SELECT DISTINCT AD_Language FROM AD_Message_Trl";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                String AD_Language = rs.getString( 1 );

                if( AD_Language.equals( language.getAD_Language())) {
                    isSystemLanguage = true;

                    break;
                }

                AD_Languages.add( AD_Language );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"verifyLanguage",e );
        }

        // Found it

        if( isSystemLanguage ) {
            return;
        }

        // No Language - set to System

        if( AD_Languages.size() == 0 ) {
            s_log.warning( "NO System Language - Set to Base " + Language.getBaseAD_Language());
            language.setAD_Language( Language.getBaseAD_Language());

            return;
        }

        for( int i = 0;i < AD_Languages.size();i++ ) {
            String AD_Language = ( String )AD_Languages.get( i );    // en_US
            String lang        = AD_Language.substring( 0,2 );       // en

            //

            String langCompare = language.getAD_Language().substring( 0,2 );

            if( lang.equals( langCompare )) {
                s_log.fine( "Found similar Language " + AD_Language );
                language.setAD_Language( AD_Language );

                return;
            }
        }

        // We found same language
        // if (!"0".equals(Msg.getMsg(AD_Language, "0")))

        s_log.warning( "Not System Language=" + language + " - Set to Base Language " + Language.getBaseAD_Language());
        language.setAD_Language( Language.getBaseAD_Language());
    }    // verifyLanguage

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static String[] getEntireContext( Properties ctx ) {
        if( ctx == null ) {
            throw new IllegalArgumentException( "Env.getEntireContext - require Context" );
        }

        Iterator keyIterator = ctx.keySet().iterator();
        String[] sList       = new String[ ctx.size()];
        int      i           = 0;

        while( keyIterator.hasNext()) {
            Object key = keyIterator.next();

            sList[ i++ ] = key.toString() + " == " + ctx.get( key ).toString();
        }

        return sList;
    }    // getEntireContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     *
     * @return
     */

    public static String getHeader( Properties ctx,int WindowNo ) {
        StringBuffer sb = new StringBuffer();

        if( WindowNo > 0 ) {
            sb.append( getContext( ctx,WindowNo,"WindowName",false )).append( "  " );            
        } else
        {
        	sb.append (OpenXpertya.NAME2);

            // separator        	
            sb.append("    -    ");
            
            // user:perfil:organizacion:compañía
            sb.append(getContext( ctx,"#AD_User_Name" )).append(" : ").append(getContext(ctx, "#AD_Role_Name")).append( " : " ).append( getContext( ctx,"#AD_Org_Name" )).append( " : " ).append( getContext( ctx,"#AD_Client_Name" ));
            
            // database and logging
            String componentName = MComponentVersion.getCurrentComponentVersion(getCtx(), null)==null?"":MComponentVersion.getCurrentComponentVersion(getCtx(), null).getName();
            sb.append(" @ ").append(CConnection.get().getDbName()).append(componentName.length()>0?"    -    "+componentName:"");
            
        }

        //sb.append( getContext( ctx,"#AD_User_Name" )).append( "@" ).append( getContext( ctx,"#AD_Client_Name" )).append( "." ).append( getContext( ctx,"#AD_Org_Name" )).append( " [" ).append( CConnection.get().toString()).append( "]" );
        //sb.append( " Usuario:" + getContext( ctx,"#AD_User_Name" )).append( "@" ).append( getContext( ctx,"#AD_Org_Name" ) );

        return sb.toString();
    }    // getHeader

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     */

    public static void clearWinContext( Properties ctx,int WindowNo ) {
        if( ctx == null ) {
            throw new IllegalArgumentException( "Env.clearWinContext - require Context" );
        }

        //

        Object[] keys = ctx.keySet().toArray();

        for( int i = 0;i < keys.length;i++ ) {
            String tag = keys[ i ].toString();

            if( tag.startsWith( WindowNo + "|" )) {
                ctx.remove( keys[ i ] );
            }
        }

        // Clear Lookup Cache

        MLookupCache.cacheReset( WindowNo );

        // MLocator.cacheReset(WindowNo);
        //

        removeWindow( WindowNo );
    }    // clearWinContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     */

    public static void clearContext( Properties ctx ) {
        if( ctx == null ) {
            throw new IllegalArgumentException( "Require Context" );
        }

        ctx.clear();
    }    // clearContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param value
     * @param onlyWindow
     * @param ignoreUnparsable
     *
     * @return
     */

    public static String parseContext( Properties ctx,int WindowNo,String value,boolean onlyWindow,boolean ignoreUnparsable ) {
        if( (value == null) || (value.length() == 0) ) {
            return "";
        }

        String       token;
        String       inStr  = new String( value );
        StringBuffer outStr = new StringBuffer();
        int          i      = inStr.indexOf( "@" );

        while( i != -1 ) {
            outStr.append( inStr.substring( 0,i ));                          // up to @
            inStr = inStr.substring( i + 1,inStr.length());                  // from first @

            int j = inStr.indexOf( "@" );                                    // next @

            if( j < 0 ) {
                s_log.log( Level.SEVERE,"No second tag: " + inStr );

                return "";                                                   // no second tag
            }

            token = inStr.substring( 0,j );

            String ctxInfo = getContext( ctx,WindowNo,token,onlyWindow );    // get context

            if( (ctxInfo.length() == 0) && ( token.startsWith( "#" ) || token.startsWith( "$" ))) {
                ctxInfo = getContext( ctx,token );    // get global context
            }

            if( ctxInfo.length() == 0 ) {
                s_log.config( "No context Win=" + WindowNo + " for: " + token );

                if( !ignoreUnparsable ) {
                    return "";                                 // token not found
                }
            } else {
                outStr.append( ctxInfo );                      // replace context with Context
            }

            inStr = inStr.substring( j + 1,inStr.length());    // from second @
            i     = inStr.indexOf( "@" );
        }

        outStr.append( inStr );    // add the rest of the string

        return outStr.toString();
    }    // parseContext

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param value
     * @param onlyWindow
     *
     * @return
     */

    public static String parseContext( Properties ctx,int WindowNo,String value,boolean onlyWindow ) {
        return parseContext( ctx,WindowNo,value,onlyWindow,false );
    }    // parseContext

    /** Descripción de Campos */

    private static ArrayList s_windows = new ArrayList( 20 );

    /**
     * Descripción de Método
     *
     *
     * @param win
     *
     * @return
     */

    public static int createWindowNo( Container win ) {
        int retValue = s_windows.size();

        s_windows.add( win );

        return retValue;
    }    // createWindowNo

    /**
     * Descripción de Método
     *
     *
     * @param container
     *
     * @return
     */

    public static int getWindowNo( Container container ) {
        if( container == null ) {
            return 0;
        }

        JFrame winFrame = getFrame( container );

        if( winFrame == null ) {
            return 0;
        }

        // loop through windows

        for( int i = 0;i < s_windows.size();i++ ) {
            Container cmp = ( Container )s_windows.get( i );

            if( cmp != null ) {
                JFrame cmpFrame = getFrame( cmp );

                if( winFrame.equals( cmpFrame )) {
                    return i;
                }
            }
        }

        return 0;
    }    // getWindowNo

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     *
     * @return
     */

    public static JFrame getWindow( int WindowNo ) {
        JFrame retValue = null;

        try {
            retValue = getFrame(( Container )s_windows.get( WindowNo ));
        } catch( Exception e ) {
            System.err.println( "Env.getWindow - " + e );
        }

        return retValue;
    }    // getWindow

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     */

    private static void removeWindow( int WindowNo ) {
        if( WindowNo < s_windows.size()) {
            s_windows.set( WindowNo,null );
        }
    }    // removeWindow

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     */

    public static void clearWinContext( int WindowNo ) {
        clearWinContext( s_ctx,WindowNo );
    }    // clearWinContext

    /**
     * Descripción de Método
     *
     */

    public static void clearContext() {
        s_ctx.clear();
    }    // clearContext

    /**
     * Descripción de Método
     *
     *
     * @param container
     *
     * @return
     */

    public static JFrame getFrame( Container container ) {
        Container element = container;

        while( element != null ) {
            if( element instanceof JFrame ) {
                return( JFrame )element;
            }

            element = element.getParent();
        }

        return null;
    }    // getFrame

    /**
     * Descripción de Método
     *
     *
     * @param container
     *
     * @return
     */

    public static Graphics getGraphics( Container container ) {
        Container element = container;

        while( element != null ) {
            Graphics g = element.getGraphics();

            if( g != null ) {
                return g;
            }

            element = element.getParent();
        }

        return null;
    }    // getFrame

    /**
     * Descripción de Método
     *
     *
     * @param container
     *
     * @return
     */

    public static Window getParent( Container container ) {
        Container element = container;

        while( element != null ) {
            if( (element instanceof JDialog) || (element instanceof JFrame) ) {
                return( Window )element;
            }

            element = element.getParent();
        }

        return null;
    }    // getParent

    /**
     * Descripción de Método
     *
     *
     * @param fileNameInImageDir
     *
     * @return
     */

    public static Image getImage( String fileNameInImageDir ) {
        URL url = OpenXpertya.class.getResource( "images/" + fileNameInImageDir );

        if( url == null ) {
            s_log.log( Level.SEVERE,"Not found: " + fileNameInImageDir );

            return null;
        }

        Toolkit tk = Toolkit.getDefaultToolkit();

        return tk.getImage( url );
    }    // getImage

    /**
     * Descripción de Método
     *
     *
     * @param fileNameInImageDir
     *
     * @return
     */

    public static ImageIcon getImageIcon( String fileNameInImageDir ) {
        URL url = OpenXpertya.class.getResource( "images/" + fileNameInImageDir );
       
        if (s_cacheImg.containsKey(url))
        {
        	return (ImageIcon) s_cacheImg.get(url);
        }
       
        if( url == null ) {
            s_log.log( Level.SEVERE,"Not found: " + fileNameInImageDir );

            return null;
        }
        ImageIcon icon = new ImageIcon( url );
        s_cacheImg.put(url,icon);
        return icon ;
    }    // getImageIcon

    /**
     * Descripción de Método
     *
     *
     * @param url
     */

    public static void startBrowser( String url ) {
        s_log.info( url+"dfad" );

        // OS command

        String cmd = "explorer ";

        if( !System.getProperty( "os.name" ).startsWith( "Win" )) {
        	// Netscape vieja época, ahora Firefox
            //cmd = "netscape ";
        	cmd = "firefox ";
        }

        //

        String execute = cmd + url;

        try {
            Runtime.getRuntime().exec( execute );
        } catch( Exception e ) {
            System.err.println( "Env.startBrowser - " + execute + " - " + e );
        }
    }    // startBrowser

    /** Descripción de Campos */

    static final public BigDecimal ZERO = new BigDecimal( 0.0 );

    /** Descripción de Campos */

    static final public BigDecimal ONE = new BigDecimal( 1.0 );

    /** Descripción de Campos */

    static final public BigDecimal ONEHUNDRED = new BigDecimal( 100.0 );

    /** Descripción de Campos */

    public static final String NL = "\n"; // System.getProperty( "line.separator" );
    
    public static final String BLANK = " ";
    public static final String COMMA = ",";
    
    public static final String PARENTHESIS_OPEN = "(";
    public static final String PARENTHESIS_CLOSE = ")";

    static {

        // Set English as default Language

        s_ctx.put( LANGUAGE,Language.getBaseAD_Language());
    }    // static
    
    /**
     * @return Devuelve la fecha actual registrada en el contexto <code>Env.getCtx()</code>.
     * La precisión no incluye horas, minutos, segundos y milisegundos.
     * Ej: 2010-03-08 00:00:00.000
     */
    public static final Timestamp getDate() {
    	return getDate(null);
    }

	/**
	 * @param ctx
	 *            contexto fuente para obtener la fecha, si es null se toma el
	 *            contexto de <code>Env.getCtx()</code>
	 * @return Devuelve la fecha actual registrada en el contexto parámetro, si
	 *         este contexto es null entonces se toma el contexto de
	 *         <code>Env.getCtx()</code>. La precisión no incluye horas,
	 *         minutos, segundos y milisegundos. Ej: 2010-03-08 00:00:00.000
	 */
    public static final Timestamp getDate(Properties ctx) {
    	return Env.getContextAsDate(ctx != null?ctx:Env.getCtx(), "#Date");
    }
    
    /**
	 * Retorna el dia actual en formato anio mes dia
	 */
    public static String getDateTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /** Fecha de última actualización de la variable #Date */
    private static long dateContextUpdatedTime = 0;
    /** Indica cada cuantas horas se debe actualizar la fecha en el contexto */
    private static final int UPDATE_DATE_CONTEXT_EVERY_HOURS = 1;
	
    /**
	 * Actualiza la variable del entorno "#Date" tomando la fecha del servidor
	 * de BD.
	 */
    private static void updateDateContext(Properties ctx) {
    	long currentTime = System.currentTimeMillis();
    	// Calcula la cantidad de horas que pasaron desde la última actualización
    	long elapsedHours = (currentTime - dateContextUpdatedTime) / 3600000;
		// Solo obtiene la fecha desde la BD cada 1 hora. De esta forma se
		// minimizan los accesos a la BD y se maximiza el tiempo de
		// sincronización de la fecha.
    	if (elapsedHours < 0 || elapsedHours >= UPDATE_DATE_CONTEXT_EVERY_HOURS) {
    		Timestamp today = null;
    		if (DB.getDatabase() != null) {
    			today = DB.getSQLValueTimestamp(null, "select ('today'::text)::timestamp");
    		}
    		// Puede ser null si aún no hay conexión a la BD
    		if (today == null) {
    			today = new Timestamp(System.currentTimeMillis());
    		}
    		setContext(ctx, "#Date", today);
    		dateContextUpdatedTime = System.currentTimeMillis();
    	}
    }
    
    public static boolean closeApp(Properties ctx){
    	String close = Env.getContext(ctx, CLOSE_APPS_PROP_NAME);
    	return Util.isEmpty(close, true) || close.equals("Y"); 
    }
    
    public static boolean isPOSJournalSupervisor(Properties ctx){
    	String isPOSJournalSupervisor = Env.getContext(ctx, "#POSJournalSupervisor");
    	return isPOSJournalSupervisor != null && isPOSJournalSupervisor.equals("Y");
    }

    

	/**
	 * Get Value of Context for Window & Tab,
	 * if not found global context if available.
	 * If TabNo is TAB_INFO only tab's context will be checked.
	 * @param ctx context
	 * @param WindowNo window no
	 * @param TabNo tab no
	 * @param context context key
	 * @param onlyTab if true, no window value is searched
	 * @param onlyWindow if true, no global context will be searched
	 * @return value or ""
	 */
	public static String getContext (Properties ctx, int WindowNo, int TabNo, String context, boolean onlyTab, boolean onlyWindow)
	{
		if (ctx == null || context == null)
			throw new IllegalArgumentException ("Require Context");
		String s = ctx.getProperty(WindowNo+"|"+TabNo+"|"+context);
		// If TAB_INFO, don't check Window and Global context - teo_sarca BF [ 2017987 ]
		if (TAB_INFO == TabNo)
			return s != null ? s : "";
		//
		if (s == null && ! onlyTab)
			return getContext(ctx, WindowNo, context, onlyWindow);
		return s;
	}	//	getContext
	/**
	 * Get Value of Context for Window & Tab,
	 * if not found global context if available.
	 * If TabNo is TAB_INFO only tab's context will be checked.
	 * @param ctx context
	 * @param WindowNo window no
	 * @param TabNo tab no
	 * @param context context key
	 * @param onlyTab if true, no window value is searched
	 * @return value or ""
	 */
	public static String getContext (Properties ctx, int WindowNo, int TabNo, String context, boolean onlyTab)
	{
		final boolean onlyWindow = onlyTab ? true : false;
		return getContext(ctx, WindowNo, TabNo, context, onlyTab, onlyWindow);
	}
    

	/**
	 *	Is Auto New Record
	 *  @param ctx context
	 *  @return true if auto new
	 */
	public static boolean isAutoNew (Properties ctx)
	{
		if (ctx == null)
			throw new IllegalArgumentException ("Require Context");
		String s = getContext(ctx, "AutoNew");
		if (s != null && s.equals("Y"))
			return true;
		return false;
	}	//	isAutoNew
	
	/**
	 *	Is Window Auto New Record (if not set use default)
	 *  @param ctx context
	 *  @param WindowNo window no
	 *  @return true if auto new record
	 */
	public static boolean isAutoNew (Properties ctx, int WindowNo)
	{
		if (ctx == null)
			throw new IllegalArgumentException ("Require Context");
		String s = getContext(ctx, WindowNo, "AutoNew", false);
		if (s != null)
		{
			if (s.equals("Y"))
				return true;
			else
				return false;
		}
		return isAutoNew(ctx);
	}	//	isAutoNew
	
	public static void setContextProvider(ContextProvider provider)
	{
		contextProvider = provider;
		getCtx().put(LANGUAGE, Language.getBaseAD_Language());
	}
	private static ContextProvider contextProvider = new DefaultContextProvider();
	
	/**
	 * 	Do we run on Apple
	 *	@return true if Mac
	 */
	public static boolean isMac() 
   	{
   		String osName = System.getProperty ("os.name");
   		osName = osName.toLowerCase();
   		return osName.indexOf ("mac") != -1;
   	}	//	isMac
   	
   	/**
   	 * 	Do we run on Windows
   	 *	@return true if windows
   	 */
   	public static boolean isWindows()
   	{
   		String osName = System.getProperty ("os.name");
   		osName = osName.toLowerCase();
   		return osName.indexOf ("windows") != -1;
   	}	//	isWindows
   	
	/**
	 *	Get Context and convert it to an integer (0 if error)
	 *  @param ctx context
	 *  @param WindowNo window no
	 *  @param context context key
	 *  @param onlyWindow  if true, no defaults are used unless explicitly asked for
	 *  @return value or 0
	 */
	public static int getContextAsInt(Properties ctx, int WindowNo, String context, boolean onlyWindow)
	{
		String s = getContext(ctx, WindowNo, context, onlyWindow);
		if (s.length() == 0)
			return 0;
		//
		try
		{
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			s_log.log(Level.SEVERE, "(" + context + ") = " + s, e);
		}
		return 0;
	}	//	getContextAsInt

    //TODO Hernandez
    /**
	 *	Set Auto New Record
	 *  @param ctx context
	 *  @param autoNew auto new record
	 */
	public static void setAutoNew (Properties ctx, boolean autoNew)
	{
		if (ctx == null)
			return;
		ctx.setProperty("AutoNew", autoNew ? "Y" : "N");
	}	//	setAutoNew

	/**
	 *	Set Auto New Record for Window
	 *  @param ctx context
	 *  @param WindowNo window no
	 *  @param autoNew auto new record
	 */
	public static void setAutoNew (Properties ctx, int WindowNo, boolean autoNew)
	{
		if (ctx == null)
			return;
		ctx.setProperty(WindowNo+"|AutoNew", autoNew ? "Y" : "N");
	}	//	setAutoNew

	/**
	 *	Is Sales Order Trx
	 *  @param ctx context
	 *  @param WindowNo window no
	 *  @return true if SO (default)
	 */
	public static boolean isSOTrx (Properties ctx, int WindowNo)
	{
		String s = getContext(ctx, WindowNo, "IsSOTrx", true);
		if (s != null && s.equals("N"))
			return false;
		return true;
	}	//	isSOTrx

	
}    // Env



/*
 *  @(#)Env.java   25.03.06
 * 
 *  Fin del fichero Env.java
 *  
 *  Versión 2.2
 *
 */
