package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class MCreditCardClose extends X_C_CreditCard_Close implements DocAction {
	
	public MCreditCardClose(Properties ctx, int C_CreditCard_Close_ID, String trxName) {
		super(ctx, C_CreditCard_Close_ID, trxName);
	}

	public MCreditCardClose(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	// Métodos estáticos
	
		// Creo el where y el arreglo de parámetros para buscar el PO
	public static MCreditCardClose get(Properties ctx, int ad_org_id, Date dateTrx, String trxName){
		StringBuffer whereClause = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		if(ad_org_id != 0){
			whereClause.append("(ad_org_id = ?)");
			params.add(ad_org_id);
		}
		if(dateTrx != null){
			if(whereClause.length() > 0){
				whereClause.append(" AND ");
			}
			whereClause.append("(datetrx = ?)");
			params.add(new java.sql.Date(dateTrx.getTime()));
		}
		return (MCreditCardClose) PO.findFirst(ctx, "c_creditcard_close", whereClause.toString(), params.toArray(), null, trxName);		
	}

	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
        DocumentEngine engine = new DocumentEngine( this,getDocStatus());
        return engine.processIt(action,getDocAction(),log);
	}

	@Override
	public boolean unlockIt() {
		return false;
	}

	@Override
	public boolean invalidateIt() {
		return false;
	}

	@Override
	public String prepareIt() {
		// Verificar que haya cierre del día anterior completo para este almacén
			if(!existsPreviousDayCloseCompleted(getDateTrx(), getAD_Org_ID(), get_TrxName())) {
				// Aquí sabemos que no existe un cierre completado para el día anterior.
				// Además, hay que tener en cuenta que si es el primer cierre que está completando
				// el resultado no es error debido a que el primer cierre siempre
				// hay que dejar completarlo.
				if (getCreditCardCloseCount(getID(), getDateTrx(), getAD_Org_ID(), get_TrxName()) >= 1) {
					m_processMsg = "@NotExistBeforeCreditCardClose@";
					return DocAction.STATUS_Invalid;
				}
			}
			
			// Verificar que no haya cupones de tarjeta para la fecha de cierre y organización que no tengan asignada la Referencia al Cierre de Tarjetas
			if(DB.getSQLValue(get_TrxName(), "SELECT COUNT(C_Payment_ID) FROM C_Payment p WHERE AD_Org_ID = ? AND p.DocStatus in ('CO','CL') AND datetrx::date = '" + getDateTrx() + "'::date AND tendertype='C' AND not exists (select cccl.c_payment_id from c_creditcard_close ccc inner join c_creditcard_closeline cccl on cccl.c_creditcard_close_id = ccc.c_creditcard_close_id where cccl.c_payment_id = p.c_payment_id and ccc.datetrx::date = p.datetrx::date)", getAD_Org_ID(),true) > 0){
				m_processMsg = "@CouponsWithoutReference@";
				return DocAction.STATUS_Invalid;
			}
			
			//Verifico que no haya números de cupones erróneos
			if (validarCampo("couponnumber") > 0) {
				m_processMsg = "@CouponsInvalid@";
				return DocAction.STATUS_Invalid;
			}
			
			//Verifico que no haya números de tarjetas erróneos
			if (validarCampo("creditcardnumber") > 0) {
				m_processMsg = "@CreditCardsWithInvalidNumber@";
				return DocAction.STATUS_Invalid;
			}
			
			//Verifico que no haya números de lotes erróneos 
			if (validarCampo("couponbatchnumber") > 0) {
				m_processMsg = "@BatchNumberInvalid@";
				return DocAction.STATUS_Invalid;
			}
			
			//Verifico que no haya cupones repetidos (Misma Organización, Nro Cupon, Nro de Lote, Fecha de Cupón, EC asociada a la EF del cupón)
			String duplicados = "SELECT COUNT(*) FROM ("
								+ 	"select couponnumber, couponbatchnumber, ef.c_bpartner_id, COUNT(*) "
								+ 	"FROM C_CreditCard_CloseLine cl "
								+	"inner join m_entidadfinancieraplan efp on efp.m_entidadfinancieraplan_id = cl.m_entidadfinancieraplan_id "
								+	"inner join m_entidadfinanciera ef on ef.m_entidadfinanciera_id = efp.m_entidadfinanciera_id "
								+ 	"where c_creditcard_close_id = " +getC_CreditCard_Close_ID() + " "
								+	"GROUP BY couponnumber, couponbatchnumber, ef.c_bpartner_id HAVING COUNT(*) > 1) as duplicados";
			if (DB.getSQLValue(get_TrxName(), duplicados, true)>0){
				m_processMsg = "@RepeatedCoupons@";
				return DocAction.STATUS_Invalid;
			}
			
			return DocAction.STATUS_InProgress;
	}
	
	//Verificar campo inválido o vacío
	public int validarCampo(String campo){
		return DB.getSQLValue(get_TrxName(), "SELECT COALESCE(COUNT(C_Payment_ID),0) FROM C_CreditCard_CloseLine WHERE (isnumeric("+campo+") = 'f' OR "+campo+" is null) AND C_CreditCard_Close_ID = " + getC_CreditCard_Close_ID() + " AND AD_Org_ID = ? ", getAD_Org_ID(),true);
	}
	
	/**
	 * Verifica si existe un Cierre de Tarjeta completo para un día anterior a la
	 * fecha indicada.
	 * @param date Fecha origen de la verificación. Se buscará un cierre para 
	 * <code>date - 1</code>.
	 * @param creditcar_close_ID cierre que se está completando 
	 * @param trxName Transacción utilizada para instanciación de POs.
	 * @return <code>true</code> si existe un cierre en estado completado, <code>false</code>
	 * en caso contrario (no existe la tupla de cierre o existe pero en estado Borrador)
	 */
	public static boolean existsPreviousDayCloseCompleted(Date date, int creditcar_close_ID, String trxName) {
		// Obtiene el día anterior a la fecha parámetro
		Calendar previousDayCalendar = Calendar.getInstance();
		previousDayCalendar.setTimeInMillis(date.getTime());
		previousDayCalendar.add(Calendar.DATE, -1);
		Date previousDay = previousDayCalendar.getTime();
		
		// Busca un cierre para el día anterior
		MCreditCardClose previousDayCC = MCreditCardClose.get(Env.getCtx(), creditcar_close_ID, previousDay, trxName); 
		// Se genera la condición de retorno. El cierre debe existir y estar en 
		// estado Completado.
		return previousDayCC != null && previousDayCC.isCompleted();
	}
	
	/**
	 * @param actualcreditcardID
	 *            id del cierre de tarjeta
	 * @param AD_Org_ID
	 *            id del cierre de organización del cierre actual
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return cantidad de registros para el cierre actual
	 */
	private static int getCreditCardCloseCount(int actualcreditcardID, Date date, int AD_Org_ID, String trxName){
		return DB
				.getSQLValue(
						trxName,
						"SELECT coalesce(count(C_CreditCard_Close_id),0) "
								+ "FROM C_CreditCard_Close "
								+ " WHERE Datetrx::date <= '" + date + "'::date "
								+ (actualcreditcardID != 0 ? "AND C_CreditCard_Close_ID <> "
										+ actualcreditcardID
										: "")
								//+ " AND DocStatus not in ('CL','CO')"
								+ " AND AD_Org_ID = " + AD_Org_ID);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Verificar si hay un cierre para la fecha elegída
		if (DB.getSQLValue(
				get_TrxName(),
				"SELECT COUNT(C_CreditCard_Close_ID) FROM C_CreditCard_Close WHERE DateTrx::date = '" +
				getDateTrx() + "'::date AND C_CreditCard_Close_ID <> " + getC_CreditCard_Close_ID() + " AND Ad_Org_ID = ?", getAD_Org_ID()) > 0) {
			// Hay una tupla para la misma fecha y almacén, por lo tanto no
			// seguir
			log.saveError("CreditCardCloseRepeated", "");
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean beforeDelete() {
		// Sólo se puede borrar registros en estado "Borrador"
		if (!getDocStatus().equals(DOCSTATUS_Drafted)){
			log.saveError("CannotDeleteRecordInProcessState", "");
			return false;
		}
		return true;
	}

	@Override
	public boolean approveIt() {
		return false;
	}

	@Override
	public boolean rejectIt() {
		return false;
	}

	@Override
	public String completeIt() {
		setAllowReopening(false);
		setProcessed(true);
		setDocAction(DOCACTION_None);

		return DocAction.STATUS_Completed;
	}

	@Override
	public boolean postIt() {
		return false;
	}

	@Override
	public boolean voidIt() {
		m_processMsg = "@NotAllowedVoidCreditCardClose@";
		return false;
	}

	@Override
	public boolean closeIt() {
		m_processMsg = "@NotAllowedCloseCreditCardClose@";
		return false;
	}

	@Override
	public boolean reverseCorrectIt() {
		m_processMsg = "@NotAllowedReverseCreditCardClose@";
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		return false;
	}

	@Override
	public boolean reActivateIt() {
		if (isAllowReopening()){
			setDocAction(DOCACTION_Complete);
	        setProcessed(false);        
			return true;
		}
		m_processMsg = "@LockCreditCardClose@";
		return false;
	}

	@Override
	public String getSummary() {
		return null;
	}

	@Override
	public int getDoc_User_ID() {
		return 0;
	}

	@Override
	public int getC_Currency_ID() {
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return null;
	}
	
	/**
	 * @return Indica si este cierre está en estado Completado.
	 */
	public boolean isCompleted() {
		return DOCSTATUS_Completed.equals(getDocStatus());
	}
}
