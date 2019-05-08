package org.openXpertya.process;

import java.math.BigDecimal;

import org.openXpertya.model.MJournal;
import org.openXpertya.model.MJournalBatch;
import org.openXpertya.model.X_I_GLJournal;
import org.openXpertya.util.DB;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.HTMLMsg.HTMLList;
import org.openXpertya.util.Msg;

public class ImportGLJournal extends ImportGLJournalBatch {

	@Override
	protected MJournalBatch importBatch(X_I_GLJournal imp, MJournalBatch currentBatch, String old_batchDocumentNo, String currentBatchDocumento) throws Exception{
		// No se crean Lotes sino sólo Diarios
		return null;
	}
	
	@Override
	protected void processJournals(){
		// Completar los diarios generados
		for (MJournal journal : journals) {
			if (!DocumentEngine.processAndSave(journal, MJournal.DOCACTION_Complete, false)) {
				DB.executeUpdate("UPDATE I_GLJournal SET I_ErrorMsg = '" + Msg.parseTranslation(getCtx(), journal.getProcessMsg())
						+ "' WHERE Gl_Journal_ID = " + journal.getID(), get_TrxName());
			} 
		}
	}

	protected String getMsg(Integer errors){
		addLog(0, null, new BigDecimal(errors), "@Errors@");
		addLog(0, null, new BigDecimal(journals.size()), "@GL_Journal_ID@: @Inserted@");
		addLog(0, null, new BigDecimal(lines.size()), "@GL_JournalLine_ID@: @Inserted@");
		
		HTMLMsg msg = new HTMLMsg();
		HTMLList list = msg.createList("final", "ul", "@ProcessOK@");
		msg.createAndAddListElement("errors", "@Errors@: "+errors, list);
		msg.createAndAddListElement("journals", "@GL_Journal_ID@ @Inserted@:"+journals.size(), list);
		msg.createAndAddListElement("lines", "@GL_JournalLine_ID@ @Inserted@: "+lines.size(), list);
		msg.addList(list);
		return getMsg(msg.toString());
	}
}
