package org.openXpertya.process;

import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MExternalService;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_ExternalService;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Promotion_Code;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

import com.sugarcrm.www.sugarcrm.Entry_value;
import com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.SugarsoapBindingStub;
import com.sugarcrm.www.sugarcrm.SugarsoapLocator;
import com.sugarcrm.www.sugarcrm.SugarsoapPortType;
import com.sugarcrm.www.sugarcrm.User_auth;

public class PromotionCodesSuiteCRMSyncProcess extends SvrProcess {

	/** Nombre del servicio externo */
	public static final String EXT_SRV_SUITE_CRM_NAME = "SuiteCRM";

	/** Referncia a la configuracion al servicio externo */
	public MExternalService externalService = null; 
	
	/** Nombre del atributo de Servicio externo: Modulo de Cupon */
	public static String EXT_SRV_COUPONS_MODULE_ATTRIBUTE = "COUPONS_MODULE"; 

	/** Nombre de la aplicacion */
	public static String APPLICATION_NAME = "Libertya";
	
	/** Tamaño maximo del array requerido por el servicio */
	public static int NAME_VALUE_MAX_SIZE = 10;

	/** Modulo de contactos en SuiteCRM */
	public static String CONTACTS_MODULE_NAME = "Contacts";
	
	/** Modulo de codigos promocionales en SuiteCRM */
	public static String PROMOTION_CODES_MODULE_NAME = null;

	/** Estados de las promociones */
	public static enum PROMOTION_CODE_STATUS { Generado, Utilizado, Vencido }
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected String doIt() throws Exception {

		// Marcar los registros que han expirado para su sincronizacion
		markExpired();

		// Cargar la configuracion segun servicio externo
		loadConfiguration();

		// Acceso a los servicios de Suite
		SugarsoapLocator locator = new SugarsoapLocator();
		locator.setsugarsoapPortEndpointAddress(externalService.getURL());
		SugarsoapPortType port = locator.getsugarsoapPort();
		((SugarsoapBindingStub)port).setTimeout(externalService.getTimeout());
		
		// Login (duracion de la sesion: 1440 segundos)
		User_auth user = new User_auth(externalService.getUserName(), md5(externalService.getPassword()));
		Name_value[] list = new Name_value[NAME_VALUE_MAX_SIZE];
		Entry_value login = port.login(user, APPLICATION_NAME, list);
		
		// Recuperar la nomina de cupones pendientes a sincronizar		
		PreparedStatement pstmt = DB.prepareStatement(	" SELECT * " +
														" FROM C_Promotion_Code " +
														" WHERE suitesyncstatus IN ('" + X_C_Promotion_Code.SUITESYNCSTATUS_Pending + "', '" + X_C_Promotion_Code.SUITESYNCSTATUS_Error + "') " +
														" AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()) +
														" ORDER BY C_Promotion_Code_ID ASC ", null);
		ResultSet rs = pstmt.executeQuery();
		int ok=0;
		int ko=0;
		X_C_Promotion_Code aPromotionCode = null;
		while (rs.next()) {
				try {
					// Recuperar el codigo de promocion para actualizar
					aPromotionCode = new X_C_Promotion_Code(getCtx(), rs, null);
					// Carga de datos en la lista
					ArrayList<Name_value> params = createParams(aPromotionCode, login, port); 
					// Codigo del cupon
					params.add(new Name_value("name", aPromotionCode.getCode()));
					// Contacto del cupon (valor en SuiteCRM)
					params.add(new Name_value("contact_id_c", getContactID(aPromotionCode, login, port)));
					// Fecha de generacion del cupon
					params.add(new Name_value("fecha_generacion", timestampToString(aPromotionCode.getValidFrom())));
					// Estado del cupon
					params.add(new Name_value("estado", getEstado(aPromotionCode)));
					// Fecha de vencimiento (si existe)
					String val = timestampToString(aPromotionCode.getValidTo());
					params.add(new Name_value("fecha_vencimiento", val!=null?val:""));
					// Fecha de utilizacion (si existe)
					val = getFechaUtilizacion(aPromotionCode);
					params.add(new Name_value("fecha_utilizacion", val!=null?val:""));

					// Registracion cupon en Suite
					params.toArray(list);
					port.set_entry(login.getId(), PROMOTION_CODES_MODULE_NAME, list);

					// Actualizacion status cupon
					aPromotionCode.setSuiteSyncStatus(X_C_Promotion_Code.SUITESYNCSTATUS_Synchronized);
					if (!aPromotionCode.save()) {
						throw new Exception("Error al actualizar el estado del cupon. " + CLogger.retrieveErrorAsString());
					}
					ok++;
				} catch (Exception e) {
					ko++;
					log("Error al sincronizar cupon " + aPromotionCode.getCode() + ": " + e.toString());
				}
		}
		log("FINALIZADO. OK: " + ok + ". KO: " + ko);
		return "";
	}

	/**
	 * Marca para la sincronizacion a los cupones que han expirado desde la ultima ejecucion del proceso
	 */
	protected void markExpired() throws Exception {
		// Los vencidos desde la ultima ejecucion deben marcarse para su sincronizacion
		ArrayList<Timestamp> interval = getTimeInterval();
		int expired = DB.executeUpdate(	" UPDATE C_Promotion_Code " +
										" SET suitesyncstatus = '" + X_C_Promotion_Code.SUITESYNCSTATUS_Pending + "' " +
										" WHERE 1=1 " +
										(interval.get(0) != null ? " AND validto > '" + interval.get(0) + "'" :"") +
										(interval.get(1) != null ? " AND validto < '" + interval.get(1) + "'" :""));
		log("Expirados: " + expired + ". " + (interval.get(0)!=null?"Desde " + interval.get(0).toString():"") + ". Hasta " + interval.get(1).toString());
	}
	
	/** Retorna 2 timestamps con el intervalo "desde" y "hasta" desde la ultima ejecucion del proceso y la actual */
	protected ArrayList<Timestamp> getTimeInterval() throws Exception {
		ArrayList<Timestamp> result = new ArrayList<Timestamp>();
		Timestamp from = null;
		Timestamp to = null;
		String sql = 	" SELECT created " +
						" FROM AD_PInstance " +
						" WHERE AD_Process_ID = " + getProcessInfo().getAD_Process_ID() +
						" ORDER BY AD_PInstance_ID " +
						" DESC LIMIT 2";
		PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		// La ejecucion actual (debería existir siempre)
		if (rs.next()) {
			to = rs.getTimestamp("created");
		}
		// La ejecucion anterior
		if (rs.next()) {
			from = rs.getTimestamp("created");
		}
		result.add(from);
		result.add(to);
		return result;
	}
	
	/** Crea la map inicial para la carga de valores y determina si debe asignar un ID de Suite o no (si ya existe en Suite o no)  */
	protected ArrayList<Name_value> createParams(X_C_Promotion_Code aPromotionCode, Entry_value login, SugarsoapPortType port) throws Exception {
		// Buscar el ID en Suite del cupon
		ArrayList<Name_value> params = new ArrayList<Name_value>();
		Get_entry_list_result_version2 cupon = port.get_entry_list(login.getId(), PROMOTION_CODES_MODULE_NAME, PROMOTION_CODES_MODULE_NAME.toLowerCase()+".name='"+aPromotionCode.getCode()+"'", null, 0, null, null, 1, 0, false);
		// Si el cupon ya existe en sugar entonces asignarle el ID (se interpreta como una modificacion).  En caso contrario no asignarle el ID (se interpreta como una insercion)
		if (cupon!=null && cupon.getEntry_list()!=null && cupon.getEntry_list().length>0)
			params.add(new Name_value("id", cupon.getEntry_list()[0].getId()));
		return params;
	}
	
	/** Obtiene el ID del contacto (de SuiteCRM) para el cupon dado */
	protected String getContactID(X_C_Promotion_Code promotionCode, Entry_value login, SugarsoapPortType port) throws Exception {
		// El cupon tiene la referencia a la factura original con la cual se origino el cupon?
		if (promotionCode.getC_Invoice_Orig_ID()==0)
			throw new Exception("El cupon " + promotionCode.getCode() + " no posee la referencia al ticket originante");
		// Si la EC es CF, el DNI debo sacarlo de la factura directamente, campo nroidentificcliente 
		// Si la EC no es CF, debo debo sacarlo de la factura directamente, campo cuit
		String documentNo = DB.getSQLValueString(null, "SELECT coalesce(nullif(cuit,''), nullif(nroidentificcliente,'')) FROM C_Invoice WHERE C_Invoice_ID = ?", promotionCode.getC_Invoice_Orig_ID());
		if (documentNo == null || documentNo.length()==0)
			throw new Exception("La factura con ID " + promotionCode.getC_Invoice_Orig_ID() + " no tiene informacion registrada sobre la entidad comercial");
		Get_entry_list_result_version2 contact = port.get_entry_list(login.getId(), CONTACTS_MODULE_NAME, "contacts_cstm.num_doc_c='"+documentNo+"'", null, 0, null, null, 1, 0, false);
		if (contact==null || contact.getEntry_list()==null || contact.getEntry_list().length==0)
			throw new Exception("Imposible recuperar el contacto cuyo documento es " + documentNo + " para el cupon " + promotionCode.getCode());
		return contact.getEntry_list()[0].getId();
	}
	
	/** Devuelve el estado actual del cupon */
	protected String getEstado(X_C_Promotion_Code promotionCode) {
		// Si tiene una referencia de una factura, fue utilizado
		if (promotionCode.getC_Invoice_ID() > 0)
			return PROMOTION_CODE_STATUS.Utilizado.toString();
		else {
			// Ya esta vencido?
			if (promotionCode.getValidTo() !=null && Env.getTimestamp().compareTo(promotionCode.getValidTo())>0)
				return PROMOTION_CODE_STATUS.Vencido.toString();	
			else 
				// Todavia esta vigente?
				return PROMOTION_CODE_STATUS.Generado.toString();
		}
	}
	
	/** Devuelve la fecha de utilizacion del cupon, basado en la fecha de la factura donde fue utilizado */
	protected String getFechaUtilizacion(X_C_Promotion_Code promotionCode) {
		if (promotionCode.getC_Invoice_ID()>0) {
			X_C_Invoice anInvoice = new X_C_Invoice(getCtx(), promotionCode.getC_Invoice_ID(), null);
			return timestampToString(anInvoice.getDateInvoiced());
		}
		return null;
	}
	
	/** Convierte un timeStamp a string yyyy-MM-dd */
	protected String timestampToString(Timestamp timeStamp) {
		if (timeStamp==null)
			return null;
		return timeStamp.toString().substring(0, 10);
	}
	
	/** Retora el md5 de value */
	protected static String md5(String value) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		StringBuffer sb = new StringBuffer();
		md.update(value.getBytes());
		byte[] digest = md.digest();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}
	
	/** Carga la configuracion de servicio externo */
	protected void loadConfiguration() throws Exception {
		List<PO> extsrv = MExternalService.find(getCtx(), X_C_ExternalService.Table_Name, "name=?", new Object[]{EXT_SRV_SUITE_CRM_NAME}, null, null);
		if (extsrv.size() < 1) 
			throw new Exception("No existe configuracion de Servicio Externo con nombre " + EXT_SRV_SUITE_CRM_NAME);

		externalService = (MExternalService)(extsrv.get(0)); 
		
		// Nombre del modulo de cupones
		X_C_ExternalServiceAttributes anAtt = externalService.getAttributeByName(EXT_SRV_COUPONS_MODULE_ATTRIBUTE);
		PROMOTION_CODES_MODULE_NAME = anAtt.getName();
		if (PROMOTION_CODES_MODULE_NAME == null)
			throw new Exception("Sin atributo " + EXT_SRV_COUPONS_MODULE_ATTRIBUTE);
	}
	
	/** Envia a log, sea cual fuere la implementacion */
	protected static void log(Object arg) {
		System.out.println(Env.getTimestamp() + ". " + arg);
	}
	
	
	public static void main(String args[]) {
	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null) {
	  		System.err.println("ERROR: La variable de entorno OXP_HOME no está seteada ");
	  		System.exit(1);
	  	}

	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false )) {
	  		System.err.println("ERROR: Error al iniciar el ambiente cliente.  Revise la configuración");
	  		System.exit(1);	
	  	}
	  	
	  	// Configuracion 
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	if (Env.getContext(Env.getCtx(), "#AD_Client_ID") == null || Env.getContext(Env.getCtx(), "#AD_Client_ID") == null) {
	  		System.err.println("ERROR: Sin marca de host.  Debe realizar la configuración correspondiente en la ventana Hosts de Replicación. ");
	  		System.exit(1);
	  	}
	  	
	  	// Informar a usuario e Iniciar la transacción
	  	log("[Client] Host: " + DB.getDatabaseInfo());
	  	try {
	  		int processID = DB.getSQLValue(null, "SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectuid = 'CORE-AD_Process-1010634'");
	  		MProcess process = new MProcess(Env.getCtx(), processID, null);
	  		ProcessInfo pi = new ProcessInfo("SuiteSync", processID);
	  		MProcess.execute(Env.getCtx(), process, pi);
	  		if (pi.isError())
	  			System.err.println(pi.getSummary());
	  	}
	  	catch (Exception e) {
	  		log(e.toString());
	  	}
	}

	
}
