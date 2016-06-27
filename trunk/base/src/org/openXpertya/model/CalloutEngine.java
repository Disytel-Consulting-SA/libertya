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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutEngine implements Callout {

	protected boolean isPluginInstance;
	
    /**
     * Constructor de la clase ...
     *
     */

    public CalloutEngine() {
        super();
        isPluginInstance = false;
    }

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());
	private MTab m_mTab;
	private MField m_mField;


    /**
     * Descripción de Método
     *
     *
     * @param methodName
     * @param value
     *
     * @return
     */

    public String convert( String methodName,String value ) {
        if( (methodName == null) || (methodName.length() == 0) ) {
            throw new IllegalArgumentException( "No Method Name" );
        }

        //

        String       retValue = null;
        StringBuffer msg      = new StringBuffer( methodName ).append( " - " ).append( value );

        log.info( msg.toString());

        //
        // Find Method

        Method method = getMethod( methodName );

        if( method == null ) {
            throw new IllegalArgumentException( "Method not found: " + methodName );
        }

        int argLength = method.getParameterTypes().length;

        if( argLength != 1 ) {
            throw new IllegalArgumentException( "Method " + methodName + " has invalid no of arguments: " + argLength );
        }

        // Call Method

        try {
            Object[] args = new Object[]{ value };
            retValue = ( String )method.invoke( this,args );
        } catch( Exception e ) {
            setCalloutActive( false );
            log.log( Level.SEVERE,"convert: " + methodName,e );
            e.printStackTrace( System.err );
        }

        return retValue;
    }    // convert

    /**
     * Descripción de Método
     *
     *
     * @param methodName
     *
     * @return
     */

    protected Method getMethod( String methodName ) {
        Method[] allMethods = getClass().getMethods();

        for( int i = 0;i < allMethods.length;i++ ) {
            if( methodName.equals( allMethods[ i ].getName())) {
                return allMethods[ i ];
            }
        }

        return null;
    }    // getMethod

    /** Descripción de Campos */

    private static boolean s_calloutActive = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected static boolean isCalloutActive() {
        return s_calloutActive;
    }    // isCalloutActive

    /**
     * Descripción de Método
     *
     *
     * @param active
     */

    protected static void setCalloutActive( boolean active ) {
        s_calloutActive = active;
    }    // setCalloutActive

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String dateAcct( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive()) {    // assuming it is resetting value
            return "";
        }

        // setCalloutActive(true);

        if( (value == null) ||!( value instanceof Timestamp )) {
            return "";
        }

        mTab.setValue( "DateAcct",value );

        // setCalloutActive(false);

        return "";
    }    // dateAcct

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String rate( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive() || (value == null) ) {    // assuming it is Conversion_Rate
            return "";
        }

        setCalloutActive( true );

        BigDecimal rate1 = ( BigDecimal )value;
        BigDecimal rate2 = Env.ZERO;
        BigDecimal one   = new BigDecimal( 1.0 );

        if( rate1.doubleValue() != 0.0 ) {    // no divide by zero
            rate2 = one.divide( rate1,12,BigDecimal.ROUND_HALF_UP );
        }

        //

        if( mField.getColumnName().equals( "MultiplyRate" )) {
            mTab.setValue( "DivideRate",rate2 );
        } else {
            mTab.setValue( "MultiplyRate",rate2 );
        }

        log.info( mField.getColumnName() + "=" + rate1 + " => " + rate2 );
        setCalloutActive( false );

        return "";
    }    // rate

	public boolean isPluginInstance() {
		return isPluginInstance;
	}

	public void setPluginInstance(boolean isPluginInstance) {
		this.isPluginInstance = isPluginInstance;
	}

	@Override
	public String start(Properties ctx, String methodName, int WindowNo,
			MTab mTab, MField mField, Object value, Object oldValue) {
		// TODO Auto-generated method stub
		if (methodName == null || methodName.length() == 0)
			throw new IllegalArgumentException ("No Method Name");
		
		m_mTab = mTab;
		m_mField = mField;
		
		//
		String retValue = "";
		StringBuffer msg = new StringBuffer(methodName).append(" - ")
			.append(mField.getColumnName())
			.append("=").append(value)
			.append(" (old=").append(oldValue)
			.append(") {active=").append(isCalloutActive()).append("}");
		if (!isCalloutActive())
			log.info (msg.toString());
		
		//	Find Method
		Method method = getMethod(methodName);
		if (method == null)
			throw new IllegalArgumentException ("Method not found: " + methodName);
		int argLength = method.getParameterTypes().length;
		if (!(argLength == 5 || argLength == 6))
			throw new IllegalArgumentException ("Method " + methodName 
				+ " has invalid no of arguments: " + argLength);

		//	Call Method
		try
		{
			Object[] args = null;
			if (argLength == 6)
				args = new Object[] {ctx, new Integer(WindowNo), mTab, mField, value, oldValue};
			else
				args = new Object[] {ctx, new Integer(WindowNo), mTab, mField, value}; 
			retValue = (String)method.invoke(this, args);
		}
		catch (Exception e)
		{
			Throwable ex = e.getCause();	//	InvocationTargetException
			if (ex == null)
				ex = e;
			log.log(Level.SEVERE, "start: " + methodName, ex);
			retValue = ex.getLocalizedMessage();
			if (retValue == null)
			{
				retValue = ex.toString();
			}
			// En caso de excepción deberíamos garantizar el fin del callout
			setCalloutActive(false);
		}
		finally
		{
			m_mTab = null;
			m_mField = null;
		}
		return retValue;
	}
}    // CalloutEngine



/*
 *  @(#)CalloutEngine.java   02.07.07
 * 
 *  Fin del fichero CalloutEngine.java
 *  
 *  Versión 2.2
 *
 */
