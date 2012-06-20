package org.openXpertya.replenish;

import org.openXpertya.replenish.replenishFunc.ReplenishFunc;
import org.openXpertya.util.CLogger;

public class ReplenishFuncFactory {

private static CLogger s_log = CLogger.getCLogger( ReplenishFuncFactory.class );
    
	public static ReplenishFunc getReplenishFunc(Replenish p_R, String cmdLine)	{
		// Obtenemos la funcion
		String cmd = cmdLine.substring(0,cmdLine.indexOf("("));
		// Y los argumentos
		String args = cmdLine.substring(cmdLine.indexOf("(")+1, cmdLine.indexOf(")"));

		ReplenishFunc func = getInstance(cmd);
		
		func.setArgsStr(args);
		func.setReplenish(p_R);
		
		return func;
	}

	
	private static ReplenishFunc getInstance(String func)	{
		String name = "org.openXpertya.replenish.replenishFunc.ReplenishFunc_"+ func.toLowerCase();
		try { 
			Class cl= Class.forName(name);
			
			try {
				ReplenishFunc f = (ReplenishFunc) cl.newInstance();
				return f;
			}
			catch (InstantiationException e)	{
				s_log.severe("Error al instanciar el objeto" + e);
				return null;
			}
			catch (IllegalAccessException e)	{
				s_log.severe("Error al instanciar el objeto" + e);
				return null;
			}
		}
		catch (ClassNotFoundException e)	{
			s_log.severe("Clase no encontrada - : " + name + ": " + e);
			return null;
		}
	}
}
