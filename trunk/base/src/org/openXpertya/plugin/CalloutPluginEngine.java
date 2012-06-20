package org.openXpertya.plugin;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.CalloutEngine;
import org.openXpertya.model.MField;
import org.openXpertya.model.MTab;

public abstract class CalloutPluginEngine extends CalloutEngine {

	/** Estado luego de ejecutar el plugin*/
	protected MPluginStatusCallout state; 

	public CalloutPluginEngine()
	{
		isPluginInstance = true;
		state = new MPluginStatusCallout();
	}

	
    public MPluginStatusCallout start( Properties ctx,String methodName,int WindowNo,MTab mTab,MField mField,Object value,Object oldValue, boolean dummyParam ) {
        if( (methodName == null) || (methodName.length() == 0) ) {
            throw new IllegalArgumentException( "No Method Name" );
        }

        StringBuffer msg = new StringBuffer( methodName ).append( " - " ).append( mField.getColumnName()).append( "=" ).append( value ).append( " (old=" ).append( oldValue ).append( ") {active=" ).append( isCalloutActive()).append( "}" );

        if( !isCalloutActive()) {
            log.info( "No esta el callout Activo"+msg.toString());
        }

        // Find Method
        Method method = getMethod( methodName );
        
        if( method == null ) {
            throw new IllegalArgumentException( "Method not found: " + methodName );
        }

        int argLength = method.getParameterTypes().length;

        if( !( (argLength == 5) || (argLength == 6) ) ) {
            throw new IllegalArgumentException( "Method " + methodName + " has invalid no of arguments: " + argLength );
        }

        // Call Method

        try {	
       	
       		 Object[] args=null;
        		 
            if( argLength == 6 ) {
                args = new Object[] {
                    ctx,new Integer( WindowNo ),mTab,mField,value,oldValue
                };
            } else {
                	args = new Object[]{ ctx,new Integer( WindowNo ),mTab,mField,value };
            }

            state = ( MPluginStatusCallout )method.invoke( this,args );
        } catch( Exception e ) {
            setCalloutActive( false );

            Throwable ex = e.getCause();    // InvocationTargetException

            if( ex ==             	
null ) {
                ex = e;
            }

            log.log( Level.SEVERE,"start:....... " + methodName,ex );
            ex.printStackTrace( System.err );
            state.setContinueStatus(MPluginStatus.STATE_FALSE);
            state.setErrorMessage(ex.getLocalizedMessage());
        }

        return state;
    }  
    	
    
	
}
