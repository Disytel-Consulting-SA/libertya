package org.openXpertya.replication.filter;

import java.io.ByteArrayInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.openXpertya.model.MAttachment;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.replication.ReplicationConstantsWS;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class ExternalAttachmentReplicationFilter extends ReplicationFilter {

	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {

		int attID = Integer.parseInt((String)getNewValueForElement(group, "AD_Attachment_ID"));
		MAttachment att = new MAttachment(Env.getCtx(), attID, trxName);

		// Replicar unicamente si el adjunto apunta a una tabla de replicacion con configuracion de salida
		int targetValid = DB.getSQLValue(null, 	" SELECT count(1) FROM AD_TableReplication " +
												" WHERE AD_Table_ID = " + att.getAD_Table_ID() + 
												" AND (		strpos(replicationarray, '"+ReplicationConstantsWS.REPLICATION_CONFIGURATION_SEND+"') > 0 " +
												" 		OR 	strpos(replicationarray, '"+ReplicationConstantsWS.REPLICATION_CONFIGURATION_SENDRECEIVE+"') > 0 " +
												" )");
		if (targetValid<=0) {
			repArraySetValueAllPositions(group, ReplicationConstantsWS.REPLICATION_CONFIGURATION_NO_ACTION);
			return;
		}
		
		// Replicar unicamente si el adjunto posee exclusivamente referencias a adjuntos externos, no almacenados en BBDD
		ByteArrayInputStream	in	= new ByteArrayInputStream(att.getBinaryData());
        ZipInputStream		zip	= new ZipInputStream(in);
        ZipEntry			entry	= zip.getNextEntry();
        boolean ok = true;
        while (entry != null) {
        	String extras = (entry.getExtra() != null ? new String(entry.getExtra()) : null);
        	if (extras == null || (extras != null && !extras.startsWith(MAttachment.EXTERNAL_ATTACHMENT_PREFIX))) {
        		ok = false;
        		break;
        	}
        	entry	= zip.getNextEntry();
        }
        if (!ok) {
        	repArraySetValueAllPositions(group, ReplicationConstantsWS.REPLICATION_CONFIGURATION_NO_ACTION);
			return;
        }
        
		
	}

}
