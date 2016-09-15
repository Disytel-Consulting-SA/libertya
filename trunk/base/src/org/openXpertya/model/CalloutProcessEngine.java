package org.openXpertya.model;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;

public class CalloutProcessEngine implements CalloutProcess {

	protected CLogger log = CLogger.getCLogger( getClass());
	protected Map<String, MField> fields;
	
	public CalloutProcessEngine(){
		
	}
	
	protected Method getMethod( String methodName ) {
        Method[] allMethods = getClass().getMethods();

        for( int i = 0;i < allMethods.length;i++ ) {
            if( methodName.equals( allMethods[ i ].getName())) {
                return allMethods[ i ];
            }
        }

        return null;
    } 
	
	@Override
	public String start(Properties ctx, Integer windowNo, String methodName, MField field, Object value,
			Object oldValue, Map<String, MField> fields) {
		if (methodName == null || methodName.length() == 0)
			throw new IllegalArgumentException ("No Method Name");
		
		this.fields = fields; 
		//
		String retValue = "";
		
		//	Find Method
		Method method = getMethod(methodName);
		if (method == null)
			throw new IllegalArgumentException ("Method not found: " + methodName);

		//	Call Method
		try {
			Object[] args = new Object[] {ctx, windowNo, field, value, oldValue};
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
		}
		finally
		{
			this.fields = null;
		}
		return retValue;
	}
	
}
