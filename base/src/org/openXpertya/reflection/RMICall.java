package org.openXpertya.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RMICall extends Call {

	@Override
	public void run(){
		// Creo un objeto para registrar el resultado de la llamada al método
		CallResult callResult = new CallResult();
		Object result = null;
		RMICallConfig rmiConfig = (RMICallConfig)getClient().getConfig();
		try{
			// Obtengo el método de la clase caller que su firma está definida
			// por el nombre y parámetros pasados
			Method method = rmiConfig
					.getCaller()
					.getClass()
					.getMethod(rmiConfig.getMethod(),
							rmiConfig.getParametersTypes());
			
			// Invoce el método obtenido
			result = method.invoke(rmiConfig.getCaller(),
					rmiConfig.getParametersValues());
			
			// Me guardo el resultado
			callResult.setResult(result);
//			if(callResult.isError()){
//				callResult.setMsg(getClient().getConfig().getCtx(),"ReflectionResultError",true);
//			}
		} catch(NoSuchMethodException nsme){
			callResult.setMsg(getClient().getConfig().getCtx(),"NotExistMethod",true);
		} catch(InvocationTargetException ite){
			callResult.setMsg(getClient().getConfig().getCtx(),"MethodCallReturnsException",true);
			String msg = "";
			if(ite.getMessage() != null){
				msg = ite.getMessage();
			}
			else if(ite.getCause() != null){
				msg = ite.getCause().getMessage();
			}
			callResult.setMsg(callResult.getMsg()+" . "+msg);
		} catch(IllegalAccessException iae){
			callResult.setMsg(getClient().getConfig().getCtx(),"IllegalAccessMethod",true);
		} catch(Exception e){
			callResult.setMsg(getClient().getConfig().getCtx(),"ReflectionMethodError",true);
			String msg = "";
			if(e.getMessage() != null){
				msg = e.getMessage();
			}
			else if(e.getCause() != null){
				msg = e.getCause().getMessage();
			}
			callResult.setMsg(callResult.getMsg()+" . "+msg);
		}
		// Aviso de finalización de método y desbloqueo de llamador
		getClient().callReturn(callResult);
	}
	
}
