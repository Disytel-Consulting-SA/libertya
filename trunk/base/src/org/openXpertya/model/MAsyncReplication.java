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

	

	/**
	 * Invocacion a replicación de entradas pendientes
	 * en tabla de replicación asincrónica.
	 * 
	 * En caso que en la tabla asyncReplication quedan entradas
	 * con estado KO, no será posible replicar directamente, debido
	 * a que esto implica la existencia de un posible origen de 
	 * error que se propagará si todavía no fue corregido.  
	 */
	public static boolean processPendingContentInAsyncReplication(Properties m_ctx, int sourceOrgID, String m_trxName) throws Exception
	{
		/* Quedan replicaciones en estado error (KO) por corregir? 
		 * En este caso, no se deberán replicar entradas posteriores. */
		int count = DB.getSQLValue(m_trxName, " SELECT count(1) FROM AD_AsyncReplication WHERE isActive = 'Y' AND Org_Source_ID = ? AND async_status = ? ", sourceOrgID, ASYNC_STATUS_ErrorInReplication);  
		if (count > 0)
			return false;
		
		return processPendingEntries(m_ctx, sourceOrgID, m_trxName);

	}
	
	/**
	 * Busca el contenido pendiente de replicacion en la tabla 
	 * AD_AsyncReplication para la sucursal recibida como parametro,
	 * luego realiza el procesamiento correspondiente a fin de 
	 * finalizar con las replicaciones todavía no procesadas para
	 * garantizar la correcta replicación de nuevos pedidos
	 * 
	 * @param sourceOrgID sucursal origen
	 * @throws Exception en caso de error en el procesamiento
	 * @return 	true si se procesaron correctamente todas las entradas, 
	 * 			false si quedan entradas en estado KO 
	 */
	public static boolean processPendingEntries(Properties m_ctx, int sourceOrgID, String m_trxName) throws Exception
	{
		/* Recuperar las entradas del asyncReplication */
		int i = 1;
		String sql = " SELECT * FROM AD_AsyncReplication WHERE isActive = 'Y' AND Org_Source_ID = ? AND (async_status IS NULL OR async_status = ?) ORDER BY AD_AsyncReplication_ID ASC ";
		PreparedStatement pstmt = DB.prepareStatement(sql, m_trxName);
		pstmt.setInt(i++, sourceOrgID);
		pstmt.setString(i++, ASYNC_STATUS_ErrorInReplication);
		
		/* Iterar por cada replicación pendiente */
		ResultSet rs = pstmt.executeQuery();
		X_AD_AsyncReplication asyncReplication = null;
		while (rs.next())
		{
			try
			{
				/* Procesar la entrada de replicacion asincronica */
				asyncReplication = new X_AD_AsyncReplication(m_ctx, rs, m_trxName);
				int initialChangelogID = asyncReplication.getInitialChangelog_ID();
				int lastChangelogID = asyncReplication.getFinalChangelog_ID();
				PluginXMLUpdater uploaderMetaData = new ReplicationXMLUpdater(asyncReplication.getasync_content(), m_trxName, 
																				sourceOrgID, initialChangelogID, lastChangelogID);
				uploaderMetaData.processChangeLog();
				asyncReplication.setasync_status(X_AD_AsyncReplication.ASYNC_STATUS_Replicated);
				asyncReplication.save();
			}
			catch (Exception e)
			{
				/* En caso de error, almacenar el mismo y devolver falso */
				asyncReplication.setasync_status(X_AD_AsyncReplication.ASYNC_STATUS_ErrorInReplication);
				asyncReplication.save();
				return false;
			}
		}
		
		/* Una vez que todos los changeLogs fueron procesados, retornar OK */
		return true;
	}
}
