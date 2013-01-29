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

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.OpenXpertya;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class Msg {

    /** Descripción de Campos */

    private static final int MAP_SIZE = 750;

    /** Descripción de Campos */

    //private static final String SEPARATOR = Env.NL + Env.NL;
    private static final String SEPARATOR = Env.NL;

    /** Descripción de Campos */

    private static Msg s_msg = null;

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( Msg.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private static Msg get() {
        if( s_msg == null ) {
            s_msg = new Msg();
        }

        return s_msg;
    }    // get

    /**
     * Constructor de la clase ...
     *
     */

    private Msg() {}    // Mag

    /** Descripción de Campos */

    private CCache m_languages = new CCache( "msg_lang",2,0 );

    /**
     * Descripción de Método
     *
     *
     * @param ad_language
     *
     * @return
     */

    private CCache getMsgMap( String ad_language ) {
        String AD_Language = ad_language;

        if( (AD_Language == null) || (AD_Language.length() == 0) ) {
            AD_Language = Language.getBaseAD_Language();
        }

        // Do we have the language ?

        CCache retValue = ( CCache )m_languages.get( AD_Language );

        if( (retValue != null) && (retValue.size() > 0) ) {
            return retValue;
        }

        // Load Language

        retValue = initMsg( AD_Language );

        if( retValue != null ) {
            m_languages.put( AD_Language,retValue );

            return retValue;
        }

        return retValue;
    }    // getMsgMap

    /**
     * Descripción de Método
     *
     *
     * @param AD_Language
     *
     * @return
     */

    private CCache initMsg( String AD_Language ) {

        // Trace.printStack();

        CCache msg = new CCache( "AD_Message",MAP_SIZE,0 );

        //

        if( !DB.isConnected()) {
            s_log.log( Level.SEVERE,"No DB Connection" );

            return null;
        }

        try {
            PreparedStatement pstmt = null;

            if( (AD_Language == null) || (AD_Language.length() == 0) || Env.isBaseLanguage( AD_Language,"AD_Language" )) {
                pstmt = DB.prepareStatement( "SELECT Value, MsgText, MsgTip FROM AD_Message" );
            } else {
                pstmt = DB.prepareStatement( "SELECT m.Value, t.MsgText, t.MsgTip " + "FROM AD_Message_Trl t, AD_Message m " + "WHERE m.AD_Message_ID=t.AD_Message_ID" + " AND t.AD_Language=?" );
                pstmt.setString( 1,AD_Language );
            }

            ResultSet rs = pstmt.executeQuery();

            // get values

            while( rs.next()) {
                String       AD_Message = rs.getString( 1 );
                StringBuffer MsgText    = new StringBuffer();

                MsgText.append( rs.getString( 2 ));

                String MsgTip = rs.getString( 3 );

                //

                if( MsgTip != null ) {    // messageTip on next line, if exists
                    MsgText.append( " " ).append( SEPARATOR ).append( MsgTip );
                }

                msg.put( AD_Message,MsgText.toString());
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"initMsg",e );

            return null;
        }

        //

        if( msg.size() < 100 ) {
            s_log.log( Level.SEVERE,"Too few (" + msg.size() + ") Records found for " + AD_Language );

            return null;
        }

        s_log.info( "Records=" + msg.size() + " - " + AD_Language );

        return msg;
    }    // initMsg

    /**
     * Descripción de Método
     *
     */

    public void reset() {
        if( m_languages == null ) {
            return;
        }

        // clear all languages

        Iterator iterator = m_languages.values().iterator();

        while( iterator.hasNext()) {
            HashMap hm = ( HashMap )iterator.next();

            hm.clear();
        }

        m_languages.clear();
    }    // reset

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String[] getLanguages() {
        if( m_languages == null ) {
            return null;
        }

        String[] retValue = new String[ m_languages.size()];

        m_languages.keySet().toArray( retValue );

        return retValue;
    }    // getLanguages

    /**
     * Descripción de Método
     *
     *
     * @param language
     *
     * @return
     */

    public boolean isLoaded( String language ) {
        if( m_languages == null ) {
            return false;
        }

        return m_languages.containsKey( language );
    }    // isLoaded

    /**
     * Descripción de Método
     *
     *
     * @param AD_Language
     * @param text
     *
     * @return
     */

    private String lookup( String AD_Language,String text ) {
        if( text == null ) {
            return null;
        }

        if( (AD_Language == null) || (AD_Language.length() == 0) ) {
            return text;
        }

        // hardcoded trl

        if( text.equals( "/" ) || text.equals( "\\" )) {
            return File.separator;
        }

        if( text.equals( ";" ) || text.equals( ":" )) {
            return File.pathSeparator;
        }

        if( text.equals( "OXP_HOME" )) {
            return OpenXpertya.getOXPHome();
        }

        if( text.equals( "bat" ) || text.equals( "sh" )) {
            if( System.getProperty( "os.name" ).startsWith( "Win" )) {
                return "bat";
            }

            return "sh";
        }

        if( text.equals( "CopyRight" )) {
            return OpenXpertya.COPYRIGHT;
        }

        //

        HashMap langMap = getMsgMap( AD_Language );

        if( langMap == null ) {
            return null;
        }

        return( String )langMap.get( text );
    }    // lookup

    /**
     * Descripción de Método
     *
     *
     * @param ad_language
     * @param AD_Message
     *
     * @return
     */

    public static String getMsg( String ad_language,String AD_Message ) {
    	s_log.fine(" En getMsg:---> Ad_languaje=" + ad_language +" Y AD_Message=  "+ AD_Message);
        if( (AD_Message == null) || (AD_Message.length() == 0) ) {
            return "";
        }

        //

        String AD_Language = ad_language;

        if( (AD_Language == null) || (AD_Language.length() == 0) ) {
            AD_Language = Language.getBaseAD_Language();
        }

        //

        String retStr = get().lookup( AD_Language,AD_Message );

        //

        if( (retStr == null) || (retStr.length() == 0) ) {
            s_log.warning( "NOT found: " + AD_Message );

            return AD_Message;
        }

        return retStr;
    }    // getMsg

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Message
     *
     * @return
     */

    public static String getMsg( Properties ctx,String AD_Message ) {
        return getMsg( Env.getAD_Language( ctx ),AD_Message );
    }    // getMeg

    /**
     * Descripción de Método
     *
     *
     * @param language
     * @param AD_Message
     *
     * @return
     */

    public static String getMsg( Language language,String AD_Message ) {
        return getMsg( language.getAD_Language(),AD_Message );
    }    // getMeg

    /**
     * Descripción de Método
     *
     *
     * @param ad_language
     * @param AD_Message
     * @param getText
     *
     * @return
     */

    public static String getMsg( String ad_language,String AD_Message,boolean getText ) {
        String retStr = getMsg( ad_language,AD_Message );
        int    pos    = retStr.indexOf( SEPARATOR );

        // No Tip

        if( pos == -1 ) {
            if( getText ) {
                return retStr;
            } else {
                return "";
            }
        } else    // with Tip
        {
            if( getText ) {
                retStr = retStr.substring( 0,pos );
            } else {
                int start = pos + SEPARATOR.length();
                int end   = retStr.length();

                retStr = retStr.substring( start );
            }
        }

        return retStr;
    }    // getMsg

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Message
     * @param getText
     *
     * @return
     */

    public static String getMsg( Properties ctx,String AD_Message,boolean getText ) {
        return getMsg( Env.getAD_Language( ctx ),AD_Message,getText );
    }    // getMsg

    /**
     * Descripción de Método
     *
     *
     * @param language
     * @param AD_Message
     * @param getText
     *
     * @return
     */

    public static String getMsg( Language language,String AD_Message,boolean getText ) {
        return getMsg( language.getAD_Language(),AD_Message,getText );
    }    // getMsg

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Message
     * @param args
     *
     * @return
     */

    public static String getMsg( Properties ctx,String AD_Message,Object[] args ) {
        return getMsg( Env.getAD_Language( ctx ),AD_Message,args );
    }    // getMsg

    /**
     * Descripción de Método
     *
     *
     * @param language
     * @param AD_Message
     * @param args
     *
     * @return
     */

    public static String getMsg( Language language,String AD_Message,Object[] args ) {
        return getMsg( language.getAD_Language(),AD_Message,args );
    }    // getMsg

    /**
     * Descripción de Método
     *
     *
     * @param ad_language
     * @param AD_Message
     * @param args
     *
     * @return
     */

    public static String getMsg( String ad_language,String AD_Message,Object[] args ) {
        String msg    = getMsg( ad_language,AD_Message );
        String retStr = msg;

        try {
            retStr = MessageFormat.format( msg,args );    // format string
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,msg,e );
        }

        return retStr;
    }    // getMsg

    /**
     * Descripción de Método
     *
     *
     * @param language
     * @param amount
     *
     * @return
     */

    public static String getAmtInWords( Language language,String amount ) {
        if( (amount == null) || (language == null) ) {
            return amount;
        }

        // Try to find Class

        String className = "org.openXpertya.util.AmtInWords_";

        try {
            className += language.getLanguageCode().toUpperCase();

            Class      clazz = Class.forName( className );
            AmtInWords aiw   = ( AmtInWords )clazz.newInstance();

            return aiw.getAmtInWords( amount );
        } catch( ClassNotFoundException e ) {
            s_log.log( Level.FINER,"Class not found: " + className );
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,className,e );
        }

        // Fallback

        StringBuffer sb   = new StringBuffer();
        int          pos  = amount.lastIndexOf( '.' );
        int          pos2 = amount.lastIndexOf( ',' );

        if( pos2 > pos ) {
            pos = pos2;
        }

        for( int i = 0;i < amount.length();i++ ) {
            if( pos == i )                          // we are done
            {
                String cents = amount.substring( i + 1 );

                sb.append( ' ' ).append( cents ).append( "/100" );

                break;
            } else {
                char c = amount.charAt( i );

                if( (c == ',') || (c == '.') ) {    // skip thousand separator
                    continue;
                }

                if( sb.length() > 0 ) {
                    sb.append( "*" );
                }

                sb.append( getMsg( language,String.valueOf( c )));
            }
        }

        return sb.toString();
    }    // getAmtInWords

    /**
     * Descripción de Método
     *
     *
     * @param ad_language
     * @param ColumnName
     * @param isSOTrx
     *
     * @return
     */

    public static String getElement( String ad_language,String ColumnName,boolean isSOTrx ) {
        if( (ColumnName == null) || ColumnName.equals( "" )) {
            return "";
        }

        String AD_Language = ad_language;

        if( (AD_Language == null) || (AD_Language.length() == 0) ) {
            AD_Language = Language.getBaseAD_Language();
        }

        // Check AD_Element

        String retStr = "";

        try {
            PreparedStatement pstmt = null;

            try {
                if( (AD_Language == null) || (AD_Language.length() == 0) || Env.isBaseLanguage( AD_Language,"AD_Element" )) {
                    pstmt = DB.prepareStatement( "SELECT Name, PO_Name FROM AD_Element WHERE UPPER(ColumnName)=?" );
                } else {
                    pstmt = DB.prepareStatement( "SELECT t.Name, t.PO_Name FROM AD_Element_Trl t, AD_Element e " + "WHERE t.AD_Element_ID=e.AD_Element_ID AND UPPER(e.ColumnName)=? " + "AND t.AD_Language=?" );
                    pstmt.setString( 2,AD_Language );
                }
            } catch( Exception e ) {
                return ColumnName;
            }

            pstmt.setString( 1,ColumnName.toUpperCase());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retStr = rs.getString( 1 );

                if( !isSOTrx ) {
                    String temp = rs.getString( 2 );

                    if( (temp != null) && (temp.length() > 0) ) {
                        retStr = temp;
                    }
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"getElement",e );

            return "";
        }

        if( retStr != null ) {
            return retStr.trim();
        }

        return retStr;
    }    // getElement

    private static Map getElementMap(String ad_language,String ColumnName,boolean isSOTrx ) {
        Map<String, String> element = new HashMap<String,String>();
        element.put("Name","");
        element.put("Description","");
        element.put("Help","");
        
    	if( (ColumnName == null) || ColumnName.equals( "" )) {
            return element;
        }
    	
    	element.put("Name",ColumnName);

        String AD_Language = ad_language;

        if( (AD_Language == null) || (AD_Language.length() == 0) ) {
            AD_Language = Language.getBaseAD_Language();
        }

        // Check AD_Element

        try {
            PreparedStatement pstmt = null;

            try {
                if( (AD_Language == null) || (AD_Language.length() == 0) || Env.isBaseLanguage( AD_Language,"AD_Element" )) {
                    pstmt = DB.prepareStatement( "SELECT Name, Description, Help, PO_Name, PO_Description, PO_Help FROM AD_Element WHERE UPPER(ColumnName)=?" );
                } else {
                    pstmt = DB.prepareStatement( "SELECT t.Name, t.Description, t.Help, t.PO_Name, t.PO_Description, t.PO_Help FROM AD_Element_Trl t, AD_Element e " + "WHERE t.AD_Element_ID=e.AD_Element_ID AND UPPER(e.ColumnName)=? " + "AND t.AD_Language=?" );
                    pstmt.setString( 2,AD_Language );
                }
            } catch( Exception e ) {
                return element;
            }

            pstmt.setString( 1,ColumnName.toUpperCase());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
            	String name = rs.getString("Name");
            	String description = rs.getString("Description");
                String help = rs.getString("Help");
                
            	if( !isSOTrx ) {
                	String poName = rs.getString("PO_Name");
                	String poDescription = rs.getString("PO_Description");
                    String poHelp = rs.getString("PO_Help");

                    if (poName != null && poName.length()>0)
                    	name = poName;
                    if (poDescription != null && poDescription.length()>0)
                    	description = poDescription;
                    if (poHelp != null && poHelp.length() > 0)
                    	help = poHelp;
                }
            	
            	element.put("Name", name != null ? name.trim() : "");
            	element.put("Description", description != null ? description.trim() : "");
            	element.put("Help", help != null ? help.trim() : "");
            }

            rs.close();
            pstmt.close();
        
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"getElement",e );

            return element;
        }

        return element;
    }    // getElement

    public static String getElementDescription( String ad_language,String ColumnName,boolean isSOTrx ) {
    	return (String)getElementMap(ad_language, ColumnName, isSOTrx).get("Description");
    }
    
    public static String getElementDescription( Properties ctx,String ColumnName ) {
    	return (String)getElementMap(Env.getAD_Language(ctx), ColumnName, true).get("Description");
    }
    
    public static String getElementDescription( Properties ctx,String ColumnName,boolean isSOTrx ) {
    	return (String)getElementMap(Env.getAD_Language(ctx), ColumnName, isSOTrx).get("Description");
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param ColumnName
     *
     * @return
     */

    public static String getElement( Properties ctx,String ColumnName ) {
        return getElement( Env.getAD_Language( ctx ),ColumnName,true );
    }    // getElement

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param ColumnName
     * @param isSOTrx
     *
     * @return
     */

    public static String getElement( Properties ctx,String ColumnName,boolean isSOTrx ) {
        return getElement( Env.getAD_Language( ctx ),ColumnName,isSOTrx );
    }    // getElement

    /**
     * Descripción de Método
     *
     *
     * @param ad_language
     * @param IsSOTrx
     * @param text
     *
     * @return
     */

    public static String translate( String ad_language,boolean IsSOTrx,String text ) {
        if( (text == null) || text.equals( "" )) {
            return "";
        }

        String AD_Language = ad_language;

        if( (AD_Language == null) || (AD_Language.length() == 0) ) {
            AD_Language = Language.getBaseAD_Language();
        }

        // Check AD_Message

        String retStr = get().lookup( AD_Language,text );

        if( retStr != null ) {
            return retStr;
        }

        // Check AD_Element

        retStr = getElement( AD_Language,text,IsSOTrx );

        if( !retStr.equals( "" )) {
            return retStr.trim();
        }

        // Nothing found

        if( !text.startsWith( "*" )) {
            s_log.fine( "NOT found: " + text );
        }

        return text;
    }    // translate

    /**
     * Descripción de Método
     *
     *
     * @param ad_language
     * @param text
     *
     * @return
     */

    public static String translate( String ad_language,String text ) {
        return translate( ad_language,true,text );
    }    // translate

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param text
     *
     * @return
     */

    public static String translate( Properties ctx,String text ) {
        if( (text == null) || (text.length() == 0) ) {
            return text;
        }

        String s = ( String )ctx.get( text );

        if( (s != null) && (s.length() > 0) ) {
            return s;
        }

        return translate( Env.getAD_Language( ctx ),Env.isSOTrx( ctx ),text );
    }    // translate

    /**
     * Descripción de Método
     *
     *
     * @param language
     * @param text
     *
     * @return
     */

    public static String translate( Language language,String text ) {
        return translate( language.getAD_Language(),false,text );
    }    // translate

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param text
     *
     * @return
     */

    public static String parseTranslation( Properties ctx,String text ) {
        if( (text == null) || (text.length() == 0) ) {
            return text;
        }

        String       inStr = text;
        String       token;
        StringBuffer outStr = new StringBuffer();
        int          i      = inStr.indexOf( "@" );

        while( i != -1 ) {
            outStr.append( inStr.substring( 0,i ));            // up to @
            inStr = inStr.substring( i + 1,inStr.length());    // from first @

            int j = inStr.indexOf( "@" );                      // next @

            if( j < 0 )                                        // no second tag
            {
                inStr = "@" + inStr;

                break;
            }

            token = inStr.substring( 0,j );
            outStr.append( translate( ctx,token ));            // replace context
            inStr = inStr.substring( j + 1,inStr.length());    // from second @
            i     = inStr.indexOf( "@" );
        }

        outStr.append( inStr );    // add remainder

        return outStr.toString();
    }    // parseTranslation
}    // Msg



/*
 *  @(#)Msg.java   25.03.06
 * 
 *  Fin del fichero Msg.java
 *  
 *  Versión 2.2
 *
 */
