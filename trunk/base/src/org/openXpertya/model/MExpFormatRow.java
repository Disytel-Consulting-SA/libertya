package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

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
		// La longitud es obligatoria para formato de posición fija
		MExpFormat exportFormat = new MExpFormat(getCtx(),
				getAD_ExpFormat_ID(), get_TrxName());
		if (MExpFormat.FORMATTYPE_FixedPosition.equals(exportFormat
				.getFormatType()) && getLength() <= 0) {
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
