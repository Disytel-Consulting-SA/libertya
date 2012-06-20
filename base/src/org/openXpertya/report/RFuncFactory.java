/**
 *   
 * Codigo original de Indeos Consultoria S.L. para el proyecto OpenXpertya 2007
 *  
 */

package org.openXpertya.report;
import org.openXpertya.report.rfunc.RFunc;
import org.openXpertya.util.CLogger;

public class RFuncFactory {

    private static CLogger s_log = CLogger.getCLogger( RFuncFactory.class );
    
	public static RFunc getRFunc(FinReport p_FR, String cmdLine)	{
//		 Obtenemos la funcion
		String cmd = cmdLine.substring(0,cmdLine.indexOf("("));
		// Y los argumentos
		String args = cmdLine.substring(cmdLine.indexOf("(")+1, cmdLine.indexOf(")"));

		RFunc func = getInstance(cmd);
		
		func.setArgsStr(args);
		func.setFinReport(p_FR);
		
		
		
		return func;
	}
	
	/**
	 * Obtiene una instancia de la funcion a llamar.
	 * @param func
	 * @return
	 */
	private static RFunc getInstance(String func)	{
		String name = "org.openXpertya.report.rfunc.RFunc_"+ func.toLowerCase();
		try { 
			Class cl= Class.forName(name);
			
			try {
				RFunc f = (RFunc) cl.newInstance();
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
