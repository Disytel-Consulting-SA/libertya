package org.openXpertya.amortization;

import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.model.MAmortization;
import org.openXpertya.model.MAmortizationMethod;
import org.openXpertya.model.MAmortizationProcessor;
import org.openXpertya.util.ClassUtil;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public abstract class AbstractAmortizationProcessorFactory {
	
	/**
	 * Obtener el procesador actualmente
	 * @param ctx
	 * @param constructorParamTypes
	 * @param constructorParamArgs
	 * @param trxName
	 * @return
	 */
	private static AbstractAmortizationProcessor getProcessor(Properties ctx, Class<?>[] constructorParamTypes, Object[] constructorParamArgs, String trxName) throws Exception{
		AbstractAmortizationProcessor processor = null;
		Integer amortizationMethodID = Env.getContextAsInt(ctx, "$M_Amortization_Method_ID");
		// Si no tiene método configurado, entonces error
		if(Util.isEmpty(amortizationMethodID, true)){
			throw new Exception(Msg.getMsg(ctx, "NotExistAmortizationMethodConfigured"));
		}
		MAmortizationMethod method = new MAmortizationMethod(ctx, amortizationMethodID, trxName);
		MAmortizationProcessor amortizationProcessor = new MAmortizationProcessor(ctx,
				method.getM_Amortization_Processor_ID(), trxName);
		// Si no tiene nombre de clase configurada, entonces error
		if(Util.isEmpty(amortizationProcessor.getClassname(), true)){
			throw new Exception(Msg.getMsg(ctx, "AmortizationProcessorClassNameNotExist"));
		}
		processor = (AbstractAmortizationProcessor) ClassUtil.getInstance(
				amortizationProcessor.getClassname(),
				constructorParamTypes, constructorParamArgs);
		return processor;
	}
	
	public static AbstractAmortizationProcessor getProcessor(Properties ctx, MAmortization amortization, String trxName) throws Exception{
		return getProcessor(ctx, new Class[] { Properties.class,
				MAmortization.class, String.class }, new Object[] { ctx,
				amortization, trxName }, trxName);
	}
	
	public static AbstractAmortizationProcessor getProcessor(Properties ctx, Integer amortizationID, String trxName) throws Exception{
		return getProcessor(ctx, new Class[] { Properties.class,
				Integer.class, String.class }, new Object[] { ctx,
				amortizationID, trxName }, trxName);
	}
	
	public static AbstractAmortizationProcessor getProcessor(Properties ctx, Timestamp date, String trxName) throws Exception{
		return getProcessor(ctx, new Class[] { Properties.class,
				Timestamp.class, String.class }, new Object[] { ctx,
				date, trxName }, trxName);
	}
}
