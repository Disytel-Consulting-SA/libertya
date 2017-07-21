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

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Esta clase no puede ser serializable ya que la primer superclase que no es
 * serializable en la lista de superclases, no posee un constructor por defecto
 * sin argumentos.
 * 
 * 
 * @version 2.2, 12.10.07
 * @author Equipo de Desarrollo de openXpertya
 */

public class CLogger extends Logger {

    /**
     * Descripción de Método
     *
     *
     * @param className
     *
     * @return
     */

    public static synchronized CLogger getCLogger( String className ) {

        // CLogMgt.initialize();

        LogManager manager = LogManager.getLogManager();

        if( className == null ) {
            className = "";
        }

        Logger result = manager.getLogger( className );

        if( (result != null) && (result instanceof CLogger) ) {
            return( CLogger )result;
        }

        //

        CLogger newLogger = new CLogger( className,null );

        manager.addLogger( newLogger );

        return newLogger;
    }    // getLogger

    /**
     * Descripción de Método
     *
     *
     * @param clazz
     *
     * @return
     */

    public static CLogger getCLogger( Class clazz ) {
        if( clazz == null ) {
            return get();
        }

        return getCLogger( clazz.getName());
    }    // getLogger

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static CLogger get() {
        if( s_logger == null ) {
            s_logger = getCLogger( "org.openXpertya.default" );
        }

        return s_logger;
    }    // get

    /** Descripción de Campos */

    private static CLogger s_logger = null;

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     * @param resourceBundleName
     */

    private CLogger( String name,String resourceBundleName ) {
        super( name,resourceBundleName );
        setLevel( Level.ALL );
    }    // CLogger

    /** Descripción de Campos */

    private static ValueNamePair s_lastError = null;

    /** Descripción de Campos */

    private static Exception s_lastException = null;

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     * @param message
     *
     * @return
     */

    public boolean saveError( String AD_Message,String message ) {
        return saveError( AD_Message,message,true );
    }    // setError

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     * @param ex
     *
     * @return
     */

    public boolean saveError( String AD_Message,Exception ex ) {
        s_lastException = ex;

        return saveError( AD_Message,ex.getLocalizedMessage(),true );
    }    // setError

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     * @param message
     * @param issueError
     *
     * @return
     */

    public boolean saveError( String AD_Message,String message,boolean issueError ) {
        s_lastError = new ValueNamePair( AD_Message,message );

        // print it

        if( issueError ) {
            severe( AD_Message + " - " + message );
        }

        return true;
    }    // setError

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static ValueNamePair retrieveError() {
        ValueNamePair vp = s_lastError;

        s_lastError = null;

        return vp;
    }    // retrieveError

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static Exception retrieveException() {
        Exception ex = s_lastException;

        s_lastException = null;

        return ex;
    }    // retrieveError
    
	public static String retrieveErrorAsString() {
	    String msg = "";
		ValueNamePair np = retrieveError();
		// Se intenta obtener el mensaje a partir del último error producido.
		if (np != null) {
			String name = (np.getName() != null) ? Msg.translate(Env.getCtx(), np.getName()) : "";
			String value = (np.getValue() != null) ? Msg.translate(Env.getCtx(), np.getValue()) : "";
			if (name.length() > 0 && value.length() > 0)
				msg = value + ": " + name;
			else if (name.length() > 0)
				msg = name;
			else if (value.length() > 0)
				msg = value;
			else
				msg = "";
		}
		return msg;
	}
	
	/**
	 * Get Error message from stack
	 * @param defaultMsg default message (used when there are no errors on stack)
	 * @return error message, or defaultMsg if there is not error message saved
	 * @see #retrieveError()
	 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
	 */
	public static String retrieveErrorString(String defaultMsg) {
		ValueNamePair vp = retrieveError();
		if (vp == null)
			return defaultMsg;
		return vp.getName();
	}
	
	/**
	 *  Get Warning from Stack
	 *  @return AD_Message as Value and Message as String
	 */
	public static ValueNamePair retrieveWarning()
	{
		ValueNamePair vp = (ValueNamePair) Env.getCtx().remove(LAST_WARNING);
		return vp;
	}   //  retrieveWarning
	private static final String LAST_WARNING = "org.openXpertya.util.CLogger.lastWarning";

	public static String retrieveWarningAsString() {
	    String msg = "";
		ValueNamePair np = retrieveWarning();
		// Se intenta obtener el mensaje a partir del último error producido.
		if (np != null) {
			String name = (np.getName() != null) ? Msg.translate(Env.getCtx(), np.getName()) : "";
			String value = (np.getValue() != null) ? Msg.translate(Env.getCtx(), np.getValue()) : "";
			if (name.length() > 0 && value.length() > 0)
				msg = value + ": " + name;
			else if (name.length() > 0)
				msg = name;
			else if (value.length() > 0)
				msg = value;
			else
				msg = "";
		}
		return msg;
	}
	
	
	public static ValueNamePair retrieveInfo()
	{
		ValueNamePair vp = (ValueNamePair) Env.getCtx().remove(LAST_INFO);
		return vp;
	}   //  retrieveInfo
	private static final String LAST_INFO = "org.openXpertya.util.CLogger.lastInfo";

	/**
	 *  Save Warning as ValueNamePair.
	 *  @param AD_Message message key
	 *  @param message clear text message
	 *  @return true
	 */
	public boolean saveWarning (String AD_Message, String message)
	{
		ValueNamePair lastWarning = new ValueNamePair(AD_Message, message);
		Env.getCtx().put(LAST_WARNING, lastWarning);
		//  print it
		if (true) //	issueError
			warning(AD_Message + " - " + message);
		return true;
	}   //  saveWarning

	public void printDebug(String title, String msg){
		System.out.println("==> " + title + " - " + msg + " - "
				+ new Date(System.currentTimeMillis()));
	}
}    // CLogger



/*
 *  @(#)CLogger.java   25.03.06
 * 
 *  Fin del fichero CLogger.java
 *  
 *  Versión 2.2
 *
 */
