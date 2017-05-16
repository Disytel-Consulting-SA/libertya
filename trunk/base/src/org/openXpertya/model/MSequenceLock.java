package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class MSequenceLock extends X_AD_Sequence_Lock {

	public MSequenceLock(Properties ctx, int AD_Sequence_Lock_ID, String trxName) {
		super(ctx, AD_Sequence_Lock_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MSequenceLock(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	protected boolean beforeSave(boolean newRecord) {
		// Controlo que no exista un registro activo para el mismo tipo de
		// documento o dupla table-registro
		String seqCondition = !Util.isEmpty(getAD_Sequence_ID())?" AND ad_sequence_id = " + getAD_Sequence_ID():"";
		String docTypeCondition = !Util.isEmpty(getC_DocType_ID()) ? " AND c_doctype_id = " + getC_DocType_ID(): "";
		//String recordCondition = (!Util.isEmpty(getAD_Table_ID()) && !Util.isEmpty(getRecord_ID())
		//				? " and ad_table_id = " + getAD_Table_ID() + " and record_id = " + getRecord_ID() : "");
		String newRecordCondition = newRecord?"":" AND AD_Sequence_Lock_ID <> "+getID();
		if(existRecordFor(getCtx(), get_TableName(),
				"ad_client_id = ? and isactive = 'Y' "
						+ (Util.isEmpty(docTypeCondition, true) ? seqCondition : docTypeCondition) + newRecordCondition,
				new Object[] { getAD_Client_ID() }, get_TrxName())){
			log.saveError("SaveError", "ExistsLockForSequence");
			return false;
		}
		
		// Actualizar la descripción si es null
		if(Util.isEmpty(getDescription(), true)){
			String description = "";
			if(!Util.isEmpty(getAD_Table_ID())){
				M_Table table = M_Table.get(getCtx(), getAD_Table_ID());
				description += Msg.getElement(getCtx(), table.getTableName()+"_ID");
			}
			if(Util.isEmpty(description, true) && !Util.isEmpty(getC_DocType_ID())){
				MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
				description += dt.getName();
			}
			setDescription(description);
		}
		
		return true;
	}
}
