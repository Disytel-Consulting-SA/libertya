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



package org.openXpertya.model;

import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Scriptlet {

    /**
     * Descripción de Método
     *
     *
     * @param variable
     * @param script
     * @param ctx
     * @param WindowNo
     *
     * @return
     */

    static Object run( String variable,String script,Properties ctx,int WindowNo ) {
        Scriptlet scr = new Scriptlet( variable,script,ctx,WindowNo );

        scr.execute();

        return scr.getResult( false );
    }    // run

    /**
     * Constructor de la clase ...
     *
     */

    public Scriptlet() {
        this( VARIABLE,"",Env.getCtx(),0 );
    }    // Scriptlet

    /** Descripción de Campos */

    public static final String VARIABLE = "result";

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Scriptlet.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param variable
     * @param script
     * @param prop
     * @param WindowNo
     */

    public Scriptlet( String variable,String script,Properties prop,int WindowNo ) {
        setVariable( variable );
        setScript( script );
        setEnvironment( prop,WindowNo );
    }    // Scriptlet

    /**
     * Constructor de la clase ...
     *
     *
     * @param variable
     * @param script
     * @param ctx
     */

    public Scriptlet( String variable,String script,HashMap ctx ) {
        setVariable( variable );
        setScript( script );
        setEnvironment( ctx );
    }    // Scriptlet

    /** Descripción de Campos */

    private String m_variable;

    /** Descripción de Campos */

    private String m_script;

    /** Descripción de Campos */

    private HashMap m_ctx;

    /** Descripción de Campos */

    private Object m_result;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Exception execute() {
        m_result = null;

        if( (m_variable == null) || (m_variable.length() == 0) || (m_script == null) || (m_script.length() == 0) ) {
            IllegalArgumentException e = new IllegalArgumentException( "No variable/script" );

            log.config( e.toString());

            return e;
        }

        Interpreter i = new Interpreter();

        loadEnvironment( i );

        try {
            log.config( m_script );
            i.eval( m_script );
        } catch( Exception e ) {
            log.config( e.toString());

            return e;
        }

        try {
            m_result = i.get( m_variable );
            log.config( "Result (" + m_result.getClass().getName() + ") " + m_result );
        } catch( Exception e ) {
            log.config( "Result - " + e );

            if( e instanceof NullPointerException ) {
                e = new IllegalArgumentException( "Result Variable not found - " + m_variable );
            }

            return e;
        }

        return null;
    }    // execute

    /**
     * Descripción de Método
     *
     *
     * @param i
     */

    private void loadEnvironment( Interpreter i ) {
        if( m_ctx == null ) {
            return;
        }

        Iterator it = m_ctx.keySet().iterator();

        while( it.hasNext()) {
            String key   = ( String )it.next();
            Object value = m_ctx.get( key );

            try {
                if( value instanceof Boolean ) {
                    i.set( key,(( Boolean )value ).booleanValue());
                } else if( value instanceof Integer ) {
                    i.set( key,(( Integer )value ).intValue());
                } else if( value instanceof Double ) {
                    i.set( key,(( Double )value ).doubleValue());
                } else {
                    i.set( key,value );
                }
            } catch( EvalError ee ) {
                log.log( Level.SEVERE,"Scriptlet.setEnvironment",ee );
            }
        }
    }    // setEnvironment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getVariable() {
        return m_variable;
    }    // getVariable

    /**
     * Descripción de Método
     *
     *
     * @param variable
     */

    public void setVariable( String variable ) {
        if( (variable == null) || (variable.length() == 0) ) {
            m_variable = VARIABLE;
        } else {
            m_variable = variable;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param script
     */

    public void setScript( String script ) {
        if( script == null ) {
            m_script = "";
        } else {
            m_script = script;
        }
    }    // setScript

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getScript() {
        return m_script;
    }    // getScript

    /**
     * Descripción de Método
     *
     *
     * @param prop
     * @param WindowNo
     */

    public void setEnvironment( Properties prop,int WindowNo ) {
        if( prop == null ) {
            prop = Env.getCtx();
        }

        m_ctx = new HashMap();

        // Convert properties to HashMap

        Enumeration en = prop.keys();

        while( en.hasMoreElements()) {
            String key = en.nextElement().toString();

            // filter

            if( (key == null) || (key.length() == 0) || key.startsWith( "P" )    // Preferences
                    || ( (key.indexOf( "|" ) != -1) &&!key.startsWith( String.valueOf( WindowNo )))    // other Window Settings
                        ) {
                continue;
            }

            String value = prop.getProperty( key );

            setEnvironment( key,value );
        }
    }    // setEnvironment

    /**
     * Descripción de Método
     *
     *
     * @param key
     * @param stringValue
     */

    public void setEnvironment( String key,String stringValue ) {
        if( (key == null) || (key.length() == 0) ) {
            return;
        }

        // log.fine( "Scriptlet.setEnvironment " + key, stringValue);

        if( stringValue == null ) {
            m_ctx.remove( key );

            return;
        }

        // Boolean

        if( stringValue.equals( "Y" )) {
            m_ctx.put( convertKey( key ),new Boolean( true ));

            return;
        }

        if( stringValue.equals( "N" )) {
            m_ctx.put( convertKey( key ),new Boolean( false ));

            return;
        }

        // Timestamp

        Timestamp timeValue = null;

        try {
            timeValue = Timestamp.valueOf( stringValue );
            m_ctx.put( convertKey( key ),timeValue );

            return;
        } catch( Exception e ) {
        }

        // Numeric

        Integer intValue = null;

        try {
            intValue = Integer.valueOf( stringValue );
        } catch( Exception e ) {
        }

        Double doubleValue = null;

        try {
            doubleValue = Double.valueOf( stringValue );
        } catch( Exception e ) {
        }

        if( doubleValue != null ) {
            if( intValue != null ) {
                double di = Double.parseDouble( intValue.toString());

                // the numbers are the same -> integer

                if( Double.compare( di,doubleValue.doubleValue()) == 0 ) {
                    m_ctx.put( convertKey( key ),intValue );

                    return;
                }
            }

            m_ctx.put( convertKey( key ),doubleValue );

            return;
        }

        if( intValue != null ) {
            m_ctx.put( convertKey( key ),intValue );

            return;
        }

        m_ctx.put( convertKey( key ),stringValue );
    }    // SetEnvironment

    /**
     * Descripción de Método
     *
     *
     * @param key
     * @param value
     */

    public void setEnvironment( String key,Object value ) {
        if( (key != null) && (key.length() > 0) ) {

            // log.fine( "Scriptlet.setEnvironment " + key, value);

            if( value == null ) {
                m_ctx.remove( key );
            } else {
                m_ctx.put( convertKey( key ),value );
            }
        }
    }    // SetEnvironment

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    private String convertKey( String key ) {
        String retValue = Util.replace( key,"#","_" );

        return retValue;
    }    // convertKey

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     */

    public void setEnvironment( HashMap ctx ) {
        if( ctx == null ) {
            m_ctx = new HashMap();
        } else {
            m_ctx = ctx;
        }
    }    // setEnvironment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public HashMap getEnvironment() {
        return m_ctx;
    }    // getEnvironment

    /**
     * Descripción de Método
     *
     *
     * @param runIt
     *
     * @return
     */

    public Object getResult( boolean runIt ) {
        if( runIt ) {
            execute();
        }

        return m_result;
    }    // getResult

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( m_variable );

        sb.append( " { " ).append( m_script ).append( " } = " ).append( getResult( true ));

        return sb.toString();
    }    // toString
}    // Scriptlet



/*
 *  @(#)Scriptlet.java   02.07.07
 * 
 *  Fin del fichero Scriptlet.java
 *  
 *  Versión 2.2
 *
 */
