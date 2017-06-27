package org.openXpertya.recovery;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.process.AbstractSvrProcess;
import org.openXpertya.util.ClassUtil;
import org.openXpertya.util.Msg;


/**
 * EL proceso de recupero/devolución/rechazo de cobros permite dejar
 * consistentes las transacciones cuando están involucrados correcciones de
 * cobro entre Libertya y el testigo físico.
 * Está basado en Tipo y Subtipos. 
 * <ul>Los tipos son (basado en Referencia Payment Recovery Types):
 * <li>Devolución</li>
 * <li>Recupero</li>
 * <li>Rechazo</li>
 * <li>Cobro Mal Registrado</li>
 * </ul>
 * Los subtipos son (basado en Referencia Payment Recovery SubTypes):
 * <li>Por Devolución de Mercadería</li>
 * <li>Por Sobrante de Cobro</li>
 * <li>Por Faltante de Cobro</li>
 * <li>Por Rechazo</li>
 * </ul>
 *  
 * @author Matías Cap - Disytel
 *
 */
public class PaymentRecoveryProcess extends AbstractSvrProcess {

	/** Conector entre tipo y subtipo */
	protected static final String TYPE_SUBTYPE_CONNECTOR = "_";
	
	/** Valores de la referencia para tipos (Referencia Payment Recovery Types) */
	protected static final String RETURN_RECOVERY_TYPE = "T";
	protected static final String RECOVERY_RECOVERY_TYPE = "C";
	protected static final String REJECT_RECOVERY_TYPE = "J";
	protected static final String FIXPAYMENTAMOUNT_RECOVERY_TYPE = "F";
	
	/** Valores de la referencia para subtipos (Referencia Payment Recovery SubTypes) */
	protected static final String FOR_RETURN_RECOVERY_SUBTYPE = "FT";
	protected static final String FOR_REJECT_RECOVERY_SUBTYPE = "FJ";
	protected static final String FOR_MISSING_RECOVERY_SUBTYPE = "FM";
	protected static final String FOR_SURPLUS_RECOVERY_SUBTYPE = "FS";
	
	/** Asociación entre tipos/subtipos y estrategia de solución */
	private Map<String, Class<?>> recoveryStrategies; 
	
	public PaymentRecoveryProcess() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Conecta el tipo y subtipo junto al conector
	 * 
	 * @param type
	 *            tipo
	 * @param subtype
	 *            subtipo
	 * @return tipo + conector + subtipo
	 */
	protected String connect(String type, String subtype){
		return type + TYPE_SUBTYPE_CONNECTOR + subtype;
	}
	
	/**
	 * En base al tipo y subtipo de operación a realizar, se instancia la 
	 * estrategia de solución correspondiente. <br>
	 * Tipos y subtipos<br>
	 * -- Devolución<br>
	 * ---- Por Devolución de Mercadería<br>
	 * ---- Por Sobrante de Cobro<br>
	 * -- Recupero<br>
	 * ---- Por Faltante de Cobro<br>
	 * ---- Por Rechazo<br>
	 * -- Cobro mal Registrado<br>
	 * ---- Por Faltante de Cobro<br>
	 * ---- Por Sobrante de Cobro<br>
	 * -- Rechazo<br>
	 * @return asociación entre tipos/subtipos y estrategias de resolución
	 */
	protected Map<String, Class<?>> createRecoveryStrategies(){
		Map<String, Class<?>> strategies = new HashMap<String, Class<?>>();
		// Devolución
		strategies.put(connect(RETURN_RECOVERY_TYPE, FOR_RETURN_RECOVERY_SUBTYPE),
				ReturnedPaymentRecoveryProcess.class);
		strategies.put(connect(RETURN_RECOVERY_TYPE, FOR_SURPLUS_RECOVERY_SUBTYPE),
				RecoveryFromReturnedPaymentRecoveryProcess.class);
		// Recupero
		strategies.put(connect(RECOVERY_RECOVERY_TYPE, FOR_MISSING_RECOVERY_SUBTYPE),
				RecoveryPaymentRecoveryProcess.class);
		strategies.put(connect(RECOVERY_RECOVERY_TYPE, FOR_REJECT_RECOVERY_SUBTYPE),
				RecoveryFromRejectedPaymentRecoveryProcess.class);
		// Cobro mal Registrado
		strategies.put(connect(FIXPAYMENTAMOUNT_RECOVERY_TYPE, FOR_MISSING_RECOVERY_SUBTYPE),
				PaymentAmountRecoveryProcess.class);
		strategies.put(connect(FIXPAYMENTAMOUNT_RECOVERY_TYPE, FOR_SURPLUS_RECOVERY_SUBTYPE),
				PaymentAmountRecoveryProcess.class);
		// Rechazo
		strategies.put(connect(REJECT_RECOVERY_TYPE, ""), RejectedPaymentRecoveryProcess.class);
		return strategies;
	}
	
	@Override
	protected String doIt() throws Exception {
		// Inicializar las combinaciones posibles
		setRecoveryStrategies(createRecoveryStrategies());
		// Tipo
		String recoveryType = (String) getParametersValues().get("TYPE");
		recoveryType = recoveryType != null?recoveryType:"";
		// Subtipo
		String recoverySubType = (String) getParametersValues().get("SUBTYPE");
		recoverySubType = recoverySubType != null?recoverySubType:"";
		
		// Ejecutar la estrategia adecuada para resolución del caso
		Class<?> strategyClass = getRecoveryStrategies().get(connect(recoveryType, recoverySubType));
		if(strategyClass == null){
			// No existe estrategia encargada de esta resolución
			log.severe("No strategy class found for type "+recoveryType+" and subtype "+recoverySubType);
			throw new Exception(Msg.getMsg(getCtx(), "PaymentRecoveryStrategyNotFound"));
		}
		// Obtener la estrategia
		PaymentRecoveryStrategyProcess strategy = (PaymentRecoveryStrategyProcess)ClassUtil.getInstance(strategyClass.getName(),
				new Class[] { Properties.class, Map.class, String.class },
				new Object[] { getCtx(), getParametersValues(), get_TrxName() });
		
		return strategy.doIt();
	}

	protected Map<String, Class<?>> getRecoveryStrategies() {
		return recoveryStrategies;
	}

	protected void setRecoveryStrategies(Map<String, Class<?>> recoveryStrategies) {
		this.recoveryStrategies = recoveryStrategies;
	}

}
