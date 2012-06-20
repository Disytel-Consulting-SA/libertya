package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.plugin.install.PluginXMLUpdater;
import org.openXpertya.replication.ReplicationXMLUpdater;
import org.openXpertya.util.DB;

public class MAsyncReplication extends X_AD_AsyncReplication {

	/** TODO: Subirla a la X */
	public static final String ASYNC_ACTION_DelayedReplicate = "X";
	
	public MAsyncReplication(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MAsyncReplication(Properties ctx, int AD_AsyncReplication_ID,
			String trxName) {
		super(ctx, AD_AsyncReplication_ID, trxName);
		// TODO Auto-generated constructor stub
	}


}
