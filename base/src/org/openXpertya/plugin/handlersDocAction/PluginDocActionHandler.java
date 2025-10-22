package org.openXpertya.plugin.handlersDocAction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.openXpertya.model.X_C_AcctSchema;
import org.openXpertya.acct.Doc;
import org.openXpertya.interfaces.Server;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatus;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.plugin.handlersPO.PluginHandler;
import org.openXpertya.process.DocAction;
import org.openXpertya.util.DB;
import org.openXpertya.util.DBException;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public abstract class PluginDocActionHandler extends PluginHandler {
	
	/** Valores de retorno para casos en que actualAction no sea prepareIt o completeIt (los cuales devuelven un String) */
	public static final String TRUE = "Y";
	public static final String FALSE = "N";
	
	/** Nombre del handler */
	protected String handlerName = "";
	
	/** Accion previa de los plugins sobre clase M */
	protected abstract MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin);
	
	/** Accion real de clase M */
	protected abstract String processActualAction(DocAction document);
	
	/** Accion posterior de los plugins sobre clase M */
	protected abstract MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin);
	
	/**
	 * Entrada principal a la gestión de persistencia mediante clases M + Plugins
	 * @param po - PO a almacenar
	 * @param newRecord - Nuevo registro?
	 * @param pluginList - Lista de plugins activos en el sistema
	 * @param log - CLogger
	 * @param handler - Manejador de persistencia por plugin
	 * @return verdadero o falso según la correcta ejecución de las validaciones
	 */
	public String processAction(DocAction document, Vector<MPluginDocAction> pluginList)
	{
		/**
		 * ===================== preAction() =====================
	     */
		int nextStatus = MPluginStatus.STATE_TRUE_AND_CONTINUE;
		MPluginStatusDocAction pluginStatusDocAction = null;
		String summary = null;
		for (int i=0; i < pluginList.size(); i++)
		{
			// obtener el LP_ correspondiente según el plugin
			PO copy = getLPluginPO((PO)document, pluginList.get(i));
			
			// invocar preAction y ver estado
			pluginStatusDocAction = this.processPreAction((copy!=null?(DocAction)copy:document), pluginList.get(i));
			
			// reincorporar las modificaciones realizadas en el plugin
			if (copy!=null)
				PO.deepCopyValues(copy, (PO)document);
			
			if (pluginStatusDocAction != null)
			{
				// El primer plugin en ejecucion define el nextStatus
				if (i==0)
					nextStatus = pluginStatusDocAction.getContinueStatus();
				
				// Mensaje resumen del procesamiento
				if(!Util.isEmpty(pluginStatusDocAction.getSummary(), true)){
					summary = (summary != null?"\n":"")+pluginStatusDocAction.getSummary();
				} 
				
				// determinar proximo paso a seguir
				if (pluginStatusDocAction.getContinueStatus() == MPluginStatus.STATE_FALSE)
				{
					((PO)document).setSummary(summary); // dREHER: Agrego el summary al PO
					String processMsg = pluginStatusDocAction.getProcessMsg();
					((PO)document).setProcessMsg(processMsg != null ? processMsg : "");
					return pluginStatusDocAction.getDocStatus();
				}
			}
		}
		
		/**
		 * ===================== Action() =====================
	     */	
		//SUR SOFTWARE: Agrego control de null sobre pluginStatusDocAction, porque en las lineas de caja venía nulo y generaba un error.
		String actualActionStatus = (nextStatus == MPluginStatus.STATE_TRUE_AND_SKIP && !Util
				.isEmpty(pluginStatusDocAction != null ? pluginStatusDocAction.getDocStatus() : null, true)) ? (pluginStatusDocAction != null ? pluginStatusDocAction 
				.getDocStatus() : null) : DocAction.STATUS_Invalid;
		if (nextStatus == MPluginStatus.STATE_TRUE_AND_CONTINUE)
		{
			// guardar el estado a fin de retornar al final del metodo si todo anda ok
			actualActionStatus = processActualAction(document);
			
			// Mensaje resumen del procesamiento
			if (pluginStatusDocAction != null && !Util.isEmpty(pluginStatusDocAction.getSummary(), true)) {
				summary = (summary != null?"\n":"")+pluginStatusDocAction.getSummary();
			}
			
			// en caso de ser inválido, detener la ejecución
			if (actualActionStatus.equals(DocAction.STATUS_Invalid) || actualActionStatus.equals(FALSE))
				return DocAction.STATUS_Invalid;
		}
			

		/**
		 * ===================== postAction() =====================
	     */		
		for (int i=0; i < pluginList.size(); i++)
		{
			// obtener el LP_ correspondiente según el plugin
			PO copy = getLPluginPO((PO)document, pluginList.get(i));			
			
			// -- postAction()  
			pluginStatusDocAction = this.processPostAction((copy!=null?(DocAction)copy:document), pluginList.get(i));
			
			// reincorporar las modificaciones realizadas en el plugin
			if (copy!=null)
				PO.deepCopyValues(copy, (PO)document);
			
			if (pluginStatusDocAction != null)
			{
				// Mensaje resumen del procesamiento
				if(!Util.isEmpty(pluginStatusDocAction.getSummary(), true)){
					summary = (summary != null?"\n":"")+pluginStatusDocAction.getSummary();
				}
				
				// determinar proximo paso a seguir
				if (pluginStatusDocAction.getContinueStatus() == MPluginStatus.STATE_FALSE)
				{
					((PO)document).setProcessMsg(pluginStatusDocAction.getProcessMsg());
					return pluginStatusDocAction.getDocStatus();
				}
			}
		}
		
		/**
		 * Si requiere completar o controlar el documento, guardar el estado para que se vea
		 * reflejado en el flujo de trabajo
		 * dREHER
		 */
		if(pluginStatusDocAction!=null && pluginStatusDocAction.getContinueStatus() == MPluginStatus.STATE_TRUE_AND_CONTINUE 
				&&
			( actualActionStatus.equals(DocAction.ACTION_Complete) || 
					actualActionStatus.equals("CT"))	) { // dREHER guardar el mensaje en el objeto
			
			((PO)document).setProcessMsg(pluginStatusDocAction.getProcessMsg());
			((PO)document).setSummary(summary);
			System.out.println("Continua Ok, guarda el sumario y el mensaje del proceso..." + summary);
			
			if(actualActionStatus.equals("CT"))
				actualActionStatus = "Y";
			
		}
		
		/**
		 * ===================== contabilidad en linea =====================
	     */
		if(actualActionStatus!=null && actualActionStatus.equals(DocAction.ACTION_Complete)) {

			PO record = ((PO)document);	
		
			boolean isOnlineAcct = false;

			// Verifico si el esquema actual utiliza contabilidad en linea
			MAcctSchema[] as = MAcctSchema.getClientAcctSchema(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()));
			if(as.length > 0) {
				X_C_AcctSchema cas = new X_C_AcctSchema(Env.getCtx(), as[0].getC_AcctSchema_ID(), record.get_TrxName());
				isOnlineAcct = cas.isCintolo_OnlineAccounting();
			}

			// si trabaja modalidad contabilidad en linea, verificar que se pueda registrar asiento, caso contrario NO permite avanzar con el
			// documento actual
			System.out.println("Debe postear inmediatamente=" + isOnlineAcct + " Estado documento=" + actualActionStatus);

			if(isOnlineAcct && postTable(record.get_Table_ID())) {

				System.out.println("Intenta postear: record.get_Table_ID()=" + record.get_Table_ID() + " ,record.getID()="+
						record.getID() + " trxName=" + record.get_TrxName());
				
				//Verifico 
				Server server = null;
				boolean isError = false;
				try {

					System.out.println("Se intenta postear inmediatamente desde el documento...");
					String post = Doc.postImmediate(as, record.get_Table_ID(), record.getID(), true, record.get_TrxName(), "Posted='N' AND DocStatus<>'CL'");
					if(post!=null)
						isError = true;
					System.out.println("Resultado del posteo:" + post);
					
					
					if(post!=null && post.equals("NoDoc")) {
						summary = (summary != null?"\n":"")+"El Documento a contabilizar todavia no esta PROCESADO!";
						isError = true;
					}
					
					/*
					if(isError) {
						System.out.println("Se intenta postear inmediatamente desde el servidor...");
						isError = false;
						server = CConnection.get().getServer();
						if( server == null ) {
							summary = (summary != null?"\n":"")+"No se encontro servidor de aplicacion contable!";
							isError = true;
						}
					}

					if (post!=null && !isError && !server.postImmediate( Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()), record.get_Table_ID(), record.getID(), true)) {
						summary = (summary != null?"\n":"")+"No se pudo publicar el asiento contable, por favor revise la configuración en los maestros intervinientes";
						isError = true;
					}
					 */
					
				} /*catch (RemoteException e) {
					e.printStackTrace();
					summary = (summary != null?"\n":"")+"Error de servidor al publicar el asiento contable, por favor reportar";
					isError = true;
				} */catch (Exception ex) {
					ex.printStackTrace();
					summary = (summary != null?"\n":"")+"Error de servidor al publicar el asiento contable, por favor reportar";
					isError = true;
				}

				if(isError) {
					((PO)document).setSummary(summary);
					((PO)document).setProcessMsg(summary);
					return DocAction.STATUS_Invalid;
				}

			}

		}

		return actualActionStatus;
	}

	/**
	 * 
	 * @param get_Table_ID
	 * @return true->debe postear, false-> no debe postear
	 * dREHER
	 */
	private boolean postTable(int get_Table_ID) {
		boolean post = false;
		
		// HardCoded que esta hecho en la clase Doc por ahora lo dejamos asi
		// dREHER
		if(get_Table_ID == MOrder.Table_ID)
			return post;
		
		String sql = "SELECT t.AD_Table_ID, t.TableName " +
				"FROM AD_Table t, AD_Column c " +
				"WHERE t.AD_Table_ID=c.AD_Table_ID AND " +
				"c.ColumnName='Posted' AND " +
				"IsView='N' " +
				"AND t.AD_Table_ID=? " +
				"ORDER BY t.AD_Table_ID";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, get_Table_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				post = true;
			}
		}
		catch (SQLException e)
		{
			throw new DBException(e, sql);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		return post;
	}

	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	
}
