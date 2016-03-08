package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class MExpFormatRow extends X_AD_ExpFormat_Row {

	public MExpFormatRow(Properties ctx, int AD_ExpFormat_Row_ID, String trxName) {
		super(ctx, AD_ExpFormat_Row_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MExpFormatRow(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	
	@Override
    protected boolean beforeSave(boolean newRecord) {
		// Mismo nombre que otro campo del mismo formato
		String newRecordWhereClause = newRecord?"":" AND AD_ExpFormat_Row_ID <> "+getID();
		PO founded = findFirst(getCtx(), get_TableName(), "AD_ExpFormat_ID = ? AND Name = '" + getName() + "'"+newRecordWhereClause,
				new Object[] { getAD_ExpFormat_ID() }, null, get_TrxName());
		if(founded != null){
			MExpFormatRow actualFounded = (MExpFormatRow)founded;
			log.saveError("SaveError", Msg.getMsg(getCtx(), "SameExpFormatRowWithName",
					new Object[] { actualFounded.getName(), actualFounded.getSeqNo() }));
			return false;
		}
		// Si es secuencial, entonces no debe tener columna asignada
		if(isSeqNumber()){
			setAD_Column_ID(0);
			setIsOrderField(false);
		}
		// La longitud es obligatoria para formato de posición fija
		MExpFormat exportFormat = new MExpFormat(getCtx(),
				getAD_ExpFormat_ID(), get_TrxName());
		if (Util.isEmpty(exportFormat.getDelimiter()) 
				&& MExpFormat.FORMATTYPE_FixedPosition.equals(exportFormat.getFormatType()) 
				&& getLength() <= 0) {
			log.saveError("LengthIsMandatory", "");
			return false;
		}
		// Si es constante, la columna es null y no puede ser de ordenamiento
		if(DATATYPE_Constant.equals(getDataType())){
			if (MExpFormat.FORMATTYPE_FixedPosition.equals(exportFormat
					.getFormatType()) && getLength() < getConstantValue().length()) {
				log.saveError("LengthMinorOfConstantLength", "");
				return false;
			}
			setAD_Column_ID(0);
			setIsOrderField(false);
		}
		return true;
	}
}
