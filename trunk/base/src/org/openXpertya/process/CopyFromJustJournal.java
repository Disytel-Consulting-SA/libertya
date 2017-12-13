package org.openXpertya.process;

import org.openXpertya.model.MJournal;

/**
 * Clase proceso que permite la copia de journals (GL_Journal)
 * @author Matias Cap
 *
 */
public class CopyFromJustJournal extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		MJournal from = new MJournal(getCtx(), getParamValueAsInt("GL_JOURNAL_ID"), get_TrxName());
		MJournal to = new MJournal(getCtx(), getRecord_ID(), get_TrxName());
		// Copio los valores entre diarios
		int no = to.copyLinesFrom( from , to.getDateAcct(), "X");
		return "@Copied@=" + no;
	}
	
}