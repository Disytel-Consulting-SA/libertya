package org.openXpertya.model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class CalloutTransfer extends CalloutEngine {

	private static final String TRANSFER_DUE_DATE_DAYS_PREFERENCE = "TransferDueDateDays"; 
	
	public String transactionDate( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		if(value == null) return "";
		
		setCalloutActive( true );
		
		int clientID = Env.getAD_Client_ID(ctx);
		int orgID = Env.getAD_Org_ID(ctx);
		// Asignar la fecha de vencimiento
		
		// Existe una preferencia colocada como constante dentro de esta clase
		// que determina la cantidad de días que se deben sumar a la fecha de la
		// transacción. El resultado de esa suma se setea en el campo de fecha de vencimiento.
		
		// El valor de preferencia puede estar a nivel organización, si para la
		// org actual no existe una configurada, por defecto se tomará la de
		// organización *. En el caso que tampoco exista para la org *, se toma
		// por defecto 1 día después a la fecha de transacción
		
		Timestamp trxDate = (Timestamp)value;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(trxDate.getTime());
		// 1) Obtengo la preference por org
		String days = MPreference.GetCustomPreferenceValue(
				TRANSFER_DUE_DATE_DAYS_PREFERENCE, clientID, orgID, null, true);
		// 2) Si no encontré para org actual, obtengo la preference para org *
		if(Util.isEmpty(days, true)){
			days = MPreference.GetCustomPreferenceValue(
					TRANSFER_DUE_DATE_DAYS_PREFERENCE, clientID, 0, null, true);
		}
		// 3) Si no encontré para org *, por defecto es 1 día
		if(Util.isEmpty(days, true)){
			days = "1";
		}
		// Casteo el resultado de la preferencia a Integer, si hubo error, por
		// defecto es 1 día + a la fecha de transacción
		Integer intdays;
		try{
			intdays = Integer.parseInt(days);
		} catch (NumberFormatException nfe) {
			intdays = 1;
		}
		// Sumo la cantidad de días a la fecha de transacción y la seteo a el
		// campo fecha de vencimiento
		calendar.add(Calendar.DATE, intdays);
		mTab.setValue("DueDate", new Timestamp(calendar.getTimeInMillis()));		
		
		setCalloutActive( false );
		
		return "";
	}
	
	
	public String transportGuide( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		String strValue = (String)value;
		// Verificar si el nro ingresado ya existe, en ese caso elevar un
		// warning al guardar
		setCalloutActive( true );
		
		// Esto se comenta ya que en un principio se pedía la validación, pero
		// luego se decidió que no se implementa
		// ------------------------------------------------------------------------
//		// Verificar si existe un registro con la misma guía y obtener el nro de
//		// documento de esa transferencia
//		String docNo = MTransfer.getDocNoTransferByStrColumnCondition(ctx,
//				"Transport_Guide", strValue,
//				(Integer) mTab.getValue("M_Transfer_ID"), null);
//		// Si existe una transferencia con ese nro entonces registrar el warning
//		if(docNo != null){
//			mTab.setCurrentRecordWarning(Msg.getMsg(ctx,
//					"TransportGuideWarning", new Object[] { docNo }));
//		}
//		else{
//			mTab.clearCurrentRecordWarning();
//		}
		// ------------------------------------------------------------------------
		
		setCalloutActive( false );
		return "";
	}
}
