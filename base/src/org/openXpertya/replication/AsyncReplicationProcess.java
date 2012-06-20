package org.openXpertya.replication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.MAsyncReplication;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_ReplicationError;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;

/**
 * Procesa la tabla de replicación asincronica a fin de 
 * impactar las entradas existentes en las tablas de producción
 */

public class AsyncReplicationProcess extends SvrProcess {

	/** ID de esta organización */
	int thisOrgID = -1; 

	
	@Override
	protected void prepare() {

		/* Setear el ID de esta organizacion (ignora la Org del login, utiliza la conf. de thisHost) */
		thisOrgID  = DB.getSQLValue(get_TrxName(), "SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' AND AD_Client_ID = " + getAD_Client_ID());

	}
	
	
	@Override
	protected String doIt() throws Exception {
	
		/* Configuración correcta en AD_ReplicationHost? */
		if (thisOrgID == -1)
			throw new Exception (" Sin marca de host.  Debe realizar la configuración correspondiente en la ventana Hosts de Replicación ");
			
		/* Recuperar todas las organizaciones a fin de replicar la informacion para cada una 
		   Contemplando la posibilidad de replicar en este momento unicamente a una sucursal destino */		
		int[] orgs = PO.getAllIDs("AD_Org", " isActive = 'Y' AND AD_Client_ID = " + getAD_Client_ID() + " AND AD_Org_ID != " + thisOrgID, get_TrxName());
 		
		/* Valores a usar en la iteracion principal */
		int initialChangelog_ID = -1, finalChangelog_ID = -1, cur_initialChangelog_ID = -1, new_initialChangelog_ID = -1, AD_AsyncReplication_ID = -1;
		
		/* Iterar por todas las sucursales */
		for (int i=0; i<orgs.length; i++)
		{
			try
			{
				/* Recuperar las entradas en la tabla de replicación asincronica para la sucursal i-esima */
				PreparedStatement pstmt = DB.prepareStatement(" SELECT async_content, initialchangelog_id, finalchangelog_id, AD_AsyncReplication_ID, async_action " +
															  " FROM AD_AsyncReplication " +
															  " WHERE Org_Source_ID = " + orgs[i] + 
															  " AND isActive = 'Y' " +
															  " AND (async_status IS NULL OR async_status = '" + MAsyncReplication.ASYNC_STATUS_ErrorInReplication + "')" +
															  " ORDER BY AD_AsyncReplication_ID ASC ", get_TrxName());
				ResultSet rs = pstmt.executeQuery();
	
				/** El envio del XML puede estar particionado en varios chunks XML (debido al gran volumen de información a procesar).
				 * Es por esto que que el contenido completo de una "unidad de replicación" puede estar almacenado en varias entradas de AD_AsyncReplication 
				 * La unidad está definida por la igualdad en el initialChangelog_ID (y tambien finalChangelog_ID)  */
	
				/* Iterar por todas las entradas existentes para la organizacion 
				 * en cuestion, commiteando la transacción cada vez que se 
				 * completa el procesamiento de una unidad de replicación */
				while (rs.next())
				{
					/* Leer valores adicional por posible excepcion en replicacion */
					initialChangelog_ID = rs.getInt("initialchangelog_id");
					finalChangelog_ID = rs.getInt("finalchangelog_id");
					AD_AsyncReplication_ID = rs.getInt("AD_AsyncReplication_ID");
					
					/* Actualizar referencia para determinar unidad de replicación */
					new_initialChangelog_ID = rs.getInt("initialchangelog_id");
					if (cur_initialChangelog_ID == -1)
						cur_initialChangelog_ID = new_initialChangelog_ID;
	
					/* Si cur y new difieren, entonces es otra unidad de 
					 * replicación: commitear lo procesado hasta el momento */
					if (cur_initialChangelog_ID != new_initialChangelog_ID)
					{
						DB.executeUpdate(" UPDATE AD_AsyncReplication SET Async_Status = '" + MAsyncReplication.ASYNC_STATUS_Replicated + "'" +
										 " WHERE AD_AsyncReplication_ID = " + AD_AsyncReplication_ID, get_TrxName());
						Trx.getTrx(get_TrxName()).commit();
						cur_initialChangelog_ID = new_initialChangelog_ID;
					}
					
					/* Verificar si es un pedido de replicación tardía (para no actualizar el ultimoa changelogid replicado */
					boolean delayedInsert = MAsyncReplication.ASYNC_ACTION_DelayedReplicate.equals(rs.getString("async_action"));
					
					/* Procesar un chunck de la unidad de replicación */					
					ReplicationXMLUpdater.processChangelog(rs.getString("async_content"), get_TrxName(), orgs[i], initialChangelog_ID, finalChangelog_ID);
				}
			}
			catch (Exception e)
			{
				/* En caso de un error, rollbackear hasta previo savePoint */
				Trx.getTrx(get_TrxName()).rollback();
				e.printStackTrace();
				
				/* Guardar en tabla de error de replicacion */
				X_AD_ReplicationError aLog = new X_AD_ReplicationError(getCtx(), 0, get_TrxName());
				aLog.setORG_Target_ID(orgs[i]);	// TODO: DEBERIA HABER UN CAMPO ORG_Source_ID A FIN DE ALMACENAR EL ORIGEN en lugar de usar el campo Target!!
				aLog.setInitialChangelog_ID(cur_initialChangelog_ID);
				aLog.setFinalChangelog_ID(finalChangelog_ID);
				aLog.setReplication_Type(X_AD_ReplicationError.REPLICATION_TYPE_Asynchronous);
				aLog.setReplication_Error(Env.getDateTime("yyyy/MM/dd-HH:mm:ss.SSS") + " - Error local en replicación asincrónica. AD_Async_Replication_ID:" + AD_AsyncReplication_ID + ". Error:" + e.getMessage());
				aLog.setClientOrg(getAD_Client_ID(), thisOrgID);
				aLog.save();
				
				/* Commitear el error y continuar con la siguiente sucursal */
				Trx.getTrx(get_TrxName()).commit();
			}
		}
		
		return "FINALIZADO";
	}


}
