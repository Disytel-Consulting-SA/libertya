package org.openXpertya.grid;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openXpertya.model.MProcessPara;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;


public class CreateFromProcessParameterModel extends CreateFromModel {

	public CreateFromProcessParameterModel() {
		// TODO Auto-generated constructor stub
	}

	public StringBuffer getProcessParameterQuery(){
		StringBuffer sql = new StringBuffer("select * from ad_process_para where ad_process_id = ? order by seqno");
		return sql;
	}
	
	public void loadProcessParameter(ProcessParameter processPara, ResultSet rs) throws SQLException {
		processPara.processParaID = rs.getInt("AD_Process_Para_ID");
		processPara.seqNo = rs.getInt("SeqNo");
		processPara.name = rs.getString("Name");
		processPara.columnName = rs.getString("ColumnName");
	}
	
	public void save(int processID, String trxName, List<? extends SourceEntity> selectedSourceEntities, CreateFromPluginInterface handler) throws CreateFromSaveException {
		// Obtengo los parámetros a copiar del proceso parámetro
		MProcessPara newParam;
		MProcessPara oldParam;
		for (int i = 0; i < selectedSourceEntities.size(); i++) {
			oldParam = new MProcessPara(
					ctx,
					((ProcessParameter) selectedSourceEntities.get(i)).processParaID,
					trxName);
			newParam = new MProcessPara(ctx, 0, trxName);
			MProcessPara.copyValues(oldParam, newParam);
			newParam.setAD_Process_ID(processID);
			if(!newParam.save()){
				throw new CreateFromSaveException(CLogger.retrieveErrorAsString());
			}
			// Copiar traducciones
			newParam.copyTranslation(oldParam);
		}
	}
}
