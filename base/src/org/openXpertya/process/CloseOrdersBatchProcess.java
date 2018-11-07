package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MOrder;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

/**
 * IMPORTANTE
 * 
 * SE SUGIERE CREAR EL SIGUIENTE INDICE PARA MEJORAR LA PERFORMANCE DEL PROCESO:
 * 
 * 		create index orderdateordered on c_order (dateordered) 
 *		create index orderlineqtyreservednotzero on c_orderline (dateordered) where qtyreserved > 0
 *
 */

public class CloseOrdersBatchProcess  {

	// Param Fecha Desde 
	static final String PARAM_DATE_FROM	 					=	"-f";
	// Param Fecha Hasta	
	static final String PARAM_DATE_TO	 					=	"-t";
	// Ayuda
	static final String PARAM_HELP			 				=	"-h";
	// Filtro por Compañía
	static final String PARAM_CLIENT_FILTER	 				=	"-c";
	// Filtro por Organizacion
	static final String PARAM_ORG_FILTER	 				=	"-o";
	// Filtro por Organizacion basado en thishost de ad_replicationhost
	static final String PARAM_ORG_FILTER_HOST	 			=	"-oh";	
	// Acción a realizar
	static final String PARAM_ISSOTRX_FILTER	 			=	"-i";
	// Pedidos por trx
	static final String PARAM_TRX_BATCH_COUNT	 			=	"-n";
	/* Filtro por Compañía */
	protected static int clientFilter = 0;
	/* Filtro por organizacion.  (0 todas, >0 una en particular, -1 segun replicacion),  */
	protected static int orgFilter = 0;
	/* Filtro por issotrx */
	protected static String isSOTrxFilter = null;
	// Fecha desde
	static String dateFrom = "";
	// Fecha hasta	
	static String dateTo = "";
	// Fecha hasta	
	static int trxBatchCount = 100;	
	
	/**
	 * Entrada principal desde terminal
	 */
	public static void main(String[] args) 
	{
		for (String arg : args) {
			if (arg.toLowerCase().startsWith(PARAM_HELP))
				showHelp(" Ayuda: \n " 
							+ PARAM_CLIENT_FILTER 			+ "  Filtra por compañía (Si no se especifica toma la compañía de la organización concreta. Obligatorio si no se ingresa una organización concreta.) \n "
							+ PARAM_ORG_FILTER 				+ "  Filtra por organizacion (Indicar su orgID. Si no se especifica se toma 0 (*)) \n "
							+ PARAM_ORG_FILTER_HOST			+ " Filtra por organizacion (Se determina el orgID segun thishost de AD_ReplicationHost) \n "
							+ PARAM_ISSOTRX_FILTER 			+ "  Transacción de ventas o compras. Valores Y o N respectivamente. Obligatorio. \n "
							+ PARAM_DATE_FROM 				+ "  Fecha Desde. Obligatorio. Formato: YYYY-MM-DD \n "
							+ PARAM_DATE_TO 				+ "  Fecha Hasta. Obligatorio. Formato: YYYY-MM-DD \n "
							+ PARAM_TRX_BATCH_COUNT			+ "  Numero de pedidos por TRX, por defecto " + trxBatchCount);
			// Filtrado por compañía en particular?
			else if (arg.toLowerCase().startsWith(PARAM_CLIENT_FILTER))
				clientFilter = Integer.parseInt(arg.substring(PARAM_CLIENT_FILTER.length()));
			// Filtrado por organizacion en particular?
			else if (arg.toLowerCase().startsWith(PARAM_ORG_FILTER)) {
				// Definicion de organizacion segun el host?
				if (arg.toLowerCase().startsWith(PARAM_ORG_FILTER_HOST)) {
					orgFilter = -1;
				} else {
					// Definicion de organizacion explícita?
					orgFilter = Integer.parseInt(arg.substring(PARAM_ORG_FILTER.length()));
				}
			}
			// Filtrado por tipo de transacción
			else if (arg.toLowerCase().startsWith(PARAM_ISSOTRX_FILTER))
				isSOTrxFilter = arg.substring(PARAM_ISSOTRX_FILTER.length());
			// Fecha desde
			else if (arg.toLowerCase().startsWith(PARAM_DATE_FROM))
				dateFrom = arg.substring(PARAM_DATE_FROM.length());
			// Fecha hasta
			else if (arg.toLowerCase().startsWith(PARAM_DATE_TO))
				dateTo = arg.substring(PARAM_DATE_TO.length());
			// Numero de pedidos por Trx
			else if (arg.toLowerCase().startsWith(PARAM_TRX_BATCH_COUNT))
				trxBatchCount = Integer.parseInt(arg.substring(PARAM_TRX_BATCH_COUNT.length()));
			else 
				log("WARNING: Argumento " + arg + " ignorado");
		}
		
	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null) { 
	  		System.err.println("ERROR: La variable de entorno OXP_HOME no está seteada ");
	  		System.exit(1);
	  	}
	  	
	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false )) {
	  		System.err.println("ERROR: Error al iniciar la configuracion... Postgres esta levantado?");
	  		System.exit(1);
	  	}
	  	
	  	// Filtrar por organizacion, basándonos en la configuracion del host
	  	if (orgFilter == -1) {
	  		orgFilter = DB.getSQLValue(null, "SELECT ad_org_id from ad_replicationhost where thishost = 'Y'");
	  	}
		
	  	// Tipo de Transacción
	  	if(isSOTrxFilter == null){
	  		showHelp("No se ha ingresado el tipo de transacción, ventas o compras (parámetro -i), de los pedidos a cerrar.");
	  	}
	  	
	  	// Compañía
	  	if(Util.isEmpty(clientFilter, true) && !Util.isEmpty(orgFilter, true)){
	  		clientFilter = DB.getSQLValue(null, "SELECT ad_client_id FROM ad_org WHERE ad_org_id = ?", orgFilter);
	  	}
	  	
	  	// Fecha desde y hasta obligatorios
	  	if(Util.isEmpty(dateFrom, true) || Util.isEmpty(dateTo, true)){
	  		System.err.println("DateFrom y DateTo obligatorios");
	  		System.exit(1);
	  	}
	  	
	  	// Configuracion
	  	Env.setContext(Env.getCtx(), "#AD_Language", "es_AR");
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", clientFilter);
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", orgFilter);
	  	if (Env.getContext(Env.getCtx(), "#AD_Client_ID") == null 
	  			|| Env.getContextAsInt(Env.getCtx(), "#AD_Client_ID") <= 0) {
	  		System.err.println("Configuracion de Compañía faltante.");
	  		System.exit(1);
	  	}

	  	// Info de acceso
	  	log("Conectado a: " + OpenXpertya.getDatabaseInfo());
	  	
	  	int ok=0;
	  	int ko=0;
	  	int i=0;
		String trxName = null;
		try {
			log("Obteniendo los pedidos para el intervalo indicado (esto puede demorar unos minutos)...");
			
			// Obtener pedidos a cerrar. Basado en el dateordered de los orderlines (tienen mismo valor que los order)
			PreparedStatement pstmt = DB.prepareStatement(
					" select distinct c_order_id " +
					" from c_orderline  " +
					" where c_order_id in ( " +
					" 			select c_order_id " + 
					" 			from c_order  " +
					" 			where dateordered between '" + dateFrom + " 00:00:00.0000' and '" + dateTo + " 23:59:59.9999' " + 
					" 			and docstatus = 'CO'  " +
					" 			and issotrx = '"  + isSOTrxFilter + "'" +
					" 			and ad_client_id = " + clientFilter +
								((orgFilter > 0) ? " and ad_org_id = " + orgFilter : "") +
					" ) " +
					" and dateordered between '" + dateFrom + " 00:00:00.0000' and '" + dateTo + " 23:59:59.9999' " +
					" and qtyreserved > 0  " +
					" order by c_order_id asc ",	
				null, true);
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				i++;
				if ((i-1) % trxBatchCount==0) {
					log("Nueva TRX");
					trxName = Trx.createTrxName();
					Trx.getTrx(trxName).start();
				}

				// Cerrar el pedido
				log(" (" + i + ") Cerrando c_order_id " + rs.getInt("c_order_id") + " : ", false, true);
				MOrder anOrder = new MOrder(Env.getCtx(), rs.getInt("c_order_id"), trxName);
				if (!DocumentEngine.processAndSave(anOrder, DocAction.ACTION_Close, false)) {
					log("[KO] " + anOrder.getProcessMsg(), true, false);
					ko++;
				} else {
					log("[OK] ", true, false);
					ok++;
				}
				// Commit de la trx
				if (i % trxBatchCount==0) {
					log("Commit TRX");
					Trx.getTrx(trxName).commit();
					Trx.getTrx(trxName).close();
					trxName = null;
				}
			}
			// Para las ultimas orders
			if (trxName!=null) {
				log("Commit TRX");
				Trx.getTrx(trxName).commit();
				Trx.getTrx(trxName).close();				
			}
			
			log("Finalizado. OK:" + ok + " KO:" + ko);
		}
		catch (Exception e) {
			log("ERROR GENERAL EN EJECUCION: " + e.toString());
			System.exit(1);
		}

	}
	
	protected static void showHelp(String message) {
		System.out.println(message);
		System.exit(1);
	}
	
	protected static String getDateTime() {
		return Env.getDateTime("yyyy-MM-dd HH:mm:ss.SS") + " ";
	}
	
	protected static void log(String message) {
		log(message, true, true);
	}
	
	protected static void log(String message, boolean newLineAtEnd, boolean withTimestamp) {
		if (withTimestamp)
			System.out.print(getDateTime());
		System.out.print(message);
		if (newLineAtEnd)
			System.out.println();
	}
}
