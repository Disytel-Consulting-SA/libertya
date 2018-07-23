package org.openXpertya.process;

import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MOrder;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

public class NotInvoicingOrdersCloseProcess extends SvrProcess {
	
	// Ayuda
	static final String PARAM_HELP			 				=	"-h";
	// Filtro por Organizacion
	static final String PARAM_CLIENT_FILTER	 				=	"-c";
	// Filtro por Organizacion
	static final String PARAM_ORG_FILTER	 				=	"-o";
	// Acción a realizar
	static final String PARAM_ISSOTRX_FILTER	 			=	"-i";
	/* Filtro por Compañía */
	protected static int clientFilter = 0;
	/* Filtro por organizacion */
	protected static int orgFilter = 0;
	/* Filtro por issotrx */
	protected static String isSOTrxFilter = null;
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	/** Transacción de Ventas */
	private boolean isSOTrx = true;
	
	/** Contexto local */
	protected Properties localCtx;
	
	/** Nombre de transacción local */
	protected String localTrxName;
	
	/** Organización */
	protected Integer orgID;
	
	public NotInvoicingOrdersCloseProcess(){}
	
	public NotInvoicingOrdersCloseProcess(Properties ctx, String isSOTrx, String trxName){
		this.localCtx = ctx;
		this.localTrxName = trxName;
		setSOTrx(isSOTrx.equals("Y"));
	}
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("DateTo")){
				setDateTo((Timestamp)para[i].getParameter());
			}
			else if(name.equals("IsSOTrx")){
				setSOTrx(((String)para[i].getParameter()).equals("Y"));
			}
		}
	}

	public String getAditionalWhereClause(){
		return " AND NOT EXISTS (SELECT C_Invoice.C_Invoice_ID FROM C_Invoice WHERE C_Invoice.C_Order_ID = C_Order.C_Order_ID AND C_Invoice.DocStatus IN ('CO','CL')) ";
	}
	
	@Override
	protected String doIt() throws Exception {
		if(getDateTo() == null){
			setDateTo(Env.getDate(getCtx()));
		}
		
		DocumentCompleteProcess dcp = new DocumentCompleteProcess(getCtx(),
				MDocType.getDocType(getCtx(),
						isSOTrx() ? MDocType.DOCTYPE_StandarOrder
								: MDocType.DOCTYPE_PurchaseOrder, get_TrxName()),
				MOrder.DOCACTION_Close, null,
				TimeUtil.addDays(getDateTo(), -1), getAditionalWhereClause(),
				get_TrxName());
		return dcp.start();
	}

	protected Timestamp getDateTo() {
		return dateTo;
	}

	protected void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	protected boolean isSOTrx() {
		return isSOTrx;
	}

	protected void setSOTrx(boolean isSOTrx) {
		this.isSOTrx = isSOTrx;
	}
	
	/**
	 * Entrada principal desde terminal
	 */
	public static void main(String[] args) 
	{
		for (String arg : args) {
			if (arg.toLowerCase().startsWith(PARAM_HELP))
				showHelp(" Ayuda: \n  " 
							+ PARAM_CLIENT_FILTER 			+ " Filtra por compañía (Si no se especifica toma la compañía de la organización concreta. Obligatorio si no se ingresa una organización concreta.) \n "
							+ PARAM_ORG_FILTER 				+ " Filtra por organizacion (Si no se especifica se toma 0 (*)) \n "
							+ PARAM_ISSOTRX_FILTER 			+ " Transacción de ventas o compras. Valores Y o N respectivamente. Obligatorio. \n ");
			// Filtrado por compañía en particular?
			else if (arg.toLowerCase().startsWith(PARAM_CLIENT_FILTER))
				clientFilter = Integer.parseInt(arg.substring(PARAM_CLIENT_FILTER.length()));
			// Filtrado por organizacion en particular?
			else if (arg.toLowerCase().startsWith(PARAM_ORG_FILTER))
				orgFilter = Integer.parseInt(arg.substring(PARAM_ORG_FILTER.length()));
			// Filtrado por tipo de transacción
			else if (arg.toLowerCase().startsWith(PARAM_ISSOTRX_FILTER))
				isSOTrxFilter = arg.substring(PARAM_ISSOTRX_FILTER.length());
			else 
				System.out.println("WARNING: Argumento " + arg + " ignorado");
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
		
	  	// Tipo de Transacción
	  	if(isSOTrxFilter == null){
	  		showHelp("No se ha ingresado el tipo de transacción, ventas o compras (parámetro -i), de los pedidos a cerrar.");
	  	}
	  	
	  	// Compañía
	  	if(Util.isEmpty(clientFilter, true) && !Util.isEmpty(orgFilter, true)){
	  		clientFilter = DB.getSQLValue(null, "SELECT ad_client_id FROM ad_org WHERE ad_org_id = ?", orgFilter);
	  	}
	  	
	  	// Configuracion
	  	Env.setContext(Env.getCtx(), "#AD_Language", "es_AR");
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", clientFilter);
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", orgFilter);
	  	if (Env.getContext(Env.getCtx(), "#AD_Client_ID") == null 
	  			|| Env.getContextAsInt(Env.getCtx(), "#AD_Client_ID") <= 0) {
	  		System.err.println("Configuracion de Compañía faltante.");
	  		return;
	  	}

	  	String result = "";
		String trxName = Trx.createTrxName();
		try {
			Trx.getTrx(trxName).start();
			// Disparar el proceso
			NotInvoicingOrdersCloseProcess niocp = new NotInvoicingOrdersCloseProcess(Env.getCtx(), isSOTrxFilter,
					trxName);
			result = niocp.start();
			Trx.getTrx(trxName).commit();
			System.out.println("NotInvoicingOrdersCloseProcess OK. "+result.toString());
		}
		catch (Exception e) {
			Trx.getTrx(trxName).rollback();
			System.out.println("NotInvoicingOrdersCloseProcess ERROR: " + e.toString());
			System.exit(1);
		}
		finally {
			Trx.getTrx(trxName).close();			
		}

	}
	
	/**
	 * Comienza la ejecución del proceso.
	 * 
	 * @return Mesaje HTML con los documentos procesados
	 * @throws Exception
	 *             cuando se produce un error en el proceso 
	 */
	public String start() throws Exception {
		return doIt();
	}
	
	protected static void showHelp(String message) {
		System.out.println(message);
		System.exit(1);
	}
	
	@Override
	public Properties getCtx() {
		if (localCtx != null) {
			return localCtx;
		} else {
			return super.getCtx();
		}
	}
	
	@Override
	public String get_TrxName() {
		if (!Util.isEmpty(localTrxName, true)) {
			return localTrxName;
		} else {
			return super.get_TrxName();
		}
	}
}
