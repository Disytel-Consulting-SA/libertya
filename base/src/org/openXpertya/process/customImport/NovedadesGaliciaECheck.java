package org.openXpertya.process.customImport;

import java.io.BufferedReader;
import java.io.FileReader;

public class NovedadesGaliciaECheck extends FileImportProcess {

	public NovedadesGaliciaECheck() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doIt() throws Exception {
		NovedadesGaliciaECheckImportFile chif = getECheckImporter();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(p_file));
			String line = reader.readLine();
			if(line != null) {
				chif.readHead(line);
			}
			while ((line = reader.readLine()) != null) {
				chif.readDetail(line);
			}
			
		} catch(Exception e) {
			return errorMsg(e, false);
		} finally {
			reader.close();
		}
		return chif.getEndMsg();
	}

	/**
	 * @return el importador propio para este archivo, el cual puede ser de
	 *         recepción o de devolución
	 */
	protected NovedadesGaliciaECheckImportFile getECheckImporter() {
		return p_file.getName().contains("REC") ? 
				new NovedadesGaliciaECheckImportFileReception(getCtx(), get_TrxName())
				: new NovedadesGaliciaECheckImportFileFeedback(getCtx(), get_TrxName());
	}
}
