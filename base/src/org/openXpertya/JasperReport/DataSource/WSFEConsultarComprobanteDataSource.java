package org.openXpertya.JasperReport.DataSource;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class WSFEConsultarComprobanteDataSource implements OXPJasperDataSource {

	/** Documentos recuperados */
	protected ArrayList<HashMap<String, String>> documents;
	/** Posicion actual */
	protected int currentPos = -1;
	
	public WSFEConsultarComprobanteDataSource(ArrayList<HashMap<String, String>> documents) {
		this.documents = documents;
	}
	
	@Override
	public Object getFieldValue(JRField arg0) throws JRException {
		return documents.get(currentPos).get(arg0);
	}

	@Override
	public boolean next() throws JRException {
		currentPos++;
		return (currentPos < documents.size());
	}

	@Override
	public void loadData() throws Exception {
		// TODO Auto-generated method stub	
	}

}
