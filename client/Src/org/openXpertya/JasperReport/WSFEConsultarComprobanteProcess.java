package org.openXpertya.JasperReport;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openXpertya.JasperReport.DataSource.WSFEConsultarComprobanteDataSource;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.X_C_DocType;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import fexv1.dif.afip.gov.ar.ClsFEXAuthRequest;
import fexv1.dif.afip.gov.ar.ClsFEXGetCMP;
import fexv1.dif.afip.gov.ar.FEXGetCMPResponse;

import FEV1.dif.afip.gov.ar.Err;
import FEV1.dif.afip.gov.ar.FEAuthRequest;
import FEV1.dif.afip.gov.ar.FECompConsultaReq;
import FEV1.dif.afip.gov.ar.FECompConsultaResponse;
import FEV1.dif.afip.gov.ar.ServiceLocator;
import FEV1.dif.afip.gov.ar.ServiceSoap;

public class WSFEConsultarComprobanteProcess extends SvrProcess {

	// TIPOS DE COMPROBANTES SEGUN AFIP 
	public static final int TIPO_COMPROBANTE_FACTURA_A 							= 1;
	public static final int TIPO_COMPROBANTE_NOTA_DEBITO_A						= 2;
	public static final int TIPO_COMPROBANTE_NOTA_CREDITO_A						= 3;
	public static final int TIPO_COMPROBANTE_FACTURA_B 							= 6;
	public static final int TIPO_COMPROBANTE_NOTA_DEBITO_B						= 7;
	public static final int TIPO_COMPROBANTE_NOTA_CREDITO_B						= 8;
	public static final int TIPO_COMPROBANTE_FACTURA_C 							= 11;
	public static final int TIPO_COMPROBANTE_NOTA_DEBITO_C						= 12;
	public static final int TIPO_COMPROBANTE_NOTA_CREDITO_C						= 13;
	public static final int TIPO_COMPROBANTE_FACTURA_EXPORTACION 				= 19;
	public static final int TIPO_COMPROBANTE_NOTA_DEBITO_EXPORTACION			= 20;
	public static final int TIPO_COMPROBANTE_NOTA_CREDITO_EXPORTACION			= 21;
	public static final int TIPO_COMPROBANTE_FACTURA_EXPORTACION_SIMPLIFICADO 	= 22;
	public static final int TIPO_COMPROBANTE_FACTURA_M 							= 51;
	public static final int TIPO_COMPROBANTE_NOTA_DEBITO_M						= 52;
	public static final int TIPO_COMPROBANTE_NOTA_CREDITO_M						= 53;
	public static final int TIPO_COMPROBANTE_TIQUE_FACTURA_A 					= 81;
	public static final int TIPO_COMPROBANTE_TIQUE_FACTURA_B 					= 82;
	public static final int TIPO_COMPROBANTE_TIQUE 								= 83;

	/** Etiquta token dentro del archivo */
	public static final String TA_TAG_TOKEN = "token";
	/** Etiquta sign dentro del archivo */
	public static final String TA_TAG_SIGN  = "sign";
	/** Nombre del archivo TA para servicio WSFE */
	public static final String TA_WSFE_FILENAME 		= "TA-wsfe.xml";
	/** Nombre del archivo TA para servicio WSFEX */
	public static final String TA_WSFEX_FILENAME 		= "TA-wsfex.xml";

	/** Preferencia URL AFIP sobre la cual realizar la consulta (WSFE) */
	public static final String PREFERENCE_WSFECC_URL 	= "WSFECC_URL";
	/** Preferencia URL AFIP sobre la cual realizar la consulta (WSFEX) */
	public static final String PREFERENCE_WSFEXCC_URL 	= "WSFEXCC_URL";


	
	
	/** Nomina de comprobantes recuperados.  En cada posicion de la lista hay una map con todos los datos, o bien ERROR_KEY si no se pudo recuperar */
	protected ArrayList<HashMap<String, String>> retrievedDocuments = new ArrayList<HashMap<String, String>>();

	/** Numero de reintentos por documento */
	public static final int RETRY_MAX = 3;
	/** Numero de documentos a consultar por ejecucion */
	public static final int MAX_DOCS = 100;
	
	/** Mapeo entre tipos de documentos con los tipos definidos por AFIP */
	public static HashMap<String, Integer> docTypeMap = new HashMap<String, Integer>();

	/** ID de la compañía */
	Integer clientID = null;
	/** CUIT de la compañía */
	Long cuit = null;	
	/** Sign de acceso a WS de AFIP */
	String sign = null;
	/** Token de acceso a WS de AFIP */
	String token = null;

	/** Numero de comprobante (desde) */
	protected long cbteNroFrom = -1;
	/** Numero de comprobante (hasta) */
	protected long cbteNroTo = -1;
	/** Recuperacion un CAE en particular: punto de venta */
	protected int ptoVta = -1;
	/** Recuperacion un CAE en particular: tipo de comprobante */
	protected int cbteTipo = -1;
	/** Tipo de documento (nombre) */
	protected String cbteTipoNombre = "";
	
	/** DataSource para interaccion con Jasper */ 
	protected WSFEConsultarComprobanteDataSource ds;


	
	static {
		// === TIPOS DE DOCUMENTOS A CONTEMPLAR EN LA BUSQUEDA === 
		
		// Comprobantes tipo A 
		docTypeMap.put("CIA", TIPO_COMPROBANTE_FACTURA_A);
		docTypeMap.put("CDNA", TIPO_COMPROBANTE_NOTA_DEBITO_A);
		docTypeMap.put("CCNA", TIPO_COMPROBANTE_NOTA_CREDITO_A);
		// Comprobantes tipo B		
		docTypeMap.put("CIB", TIPO_COMPROBANTE_FACTURA_B);
		docTypeMap.put("CDNB", TIPO_COMPROBANTE_NOTA_DEBITO_B);
		docTypeMap.put("CCNB", TIPO_COMPROBANTE_NOTA_CREDITO_B);
		// Comprobantes tipo C		
		docTypeMap.put("CIC", TIPO_COMPROBANTE_FACTURA_C);
		docTypeMap.put("CDNC", TIPO_COMPROBANTE_NOTA_DEBITO_C);
		docTypeMap.put("CCNC", TIPO_COMPROBANTE_NOTA_CREDITO_C);
		// Comprobantes tipo M		
		docTypeMap.put("CIM", TIPO_COMPROBANTE_FACTURA_M);
		docTypeMap.put("CDNM", TIPO_COMPROBANTE_NOTA_DEBITO_M);
		docTypeMap.put("CCNM", TIPO_COMPROBANTE_NOTA_CREDITO_M);
		// Comprobantes tipo E		
		docTypeMap.put("CIE", TIPO_COMPROBANTE_FACTURA_EXPORTACION);
		docTypeMap.put("CDNE", TIPO_COMPROBANTE_NOTA_DEBITO_EXPORTACION);
		docTypeMap.put("CCNE", TIPO_COMPROBANTE_NOTA_CREDITO_EXPORTACION);
	}
	
		
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null)
				;
// Comentado: el punto de venta se determina a partir del tipo de documento			
//			else if (name.equalsIgnoreCase("PtoVta")) {
//				ptoVta = para[i].getParameterAsInt();
//			}
			else if (name.equalsIgnoreCase("C_DocType_ID")) {
				MDocType aDocType = new MDocType(getCtx(), para[i].getParameterAsInt(), get_TrxName());
				cbteTipo = getCbteTipo(aDocType);
				cbteTipoNombre = aDocType.getName();
				ptoVta = aDocType.getPosNumber(); 
			}
			else if (name.equalsIgnoreCase("NroCbte")) {
				cbteNroFrom = ((BigDecimal)para[i].getParameter()).longValue();
				cbteNroTo = ((BigDecimal)para[i].getParameter_To()).longValue();
			}
		}
	}
	
	
	@Override
	protected String doIt() throws Exception {

		// Validaciones preliminares
		checkPreconditions();
		// Carga de valores iniciales
		loadInitialValues();
		// Consulta de comprobantes
		queryInvoices();
		// Crear el informe
		createReport();
		
		return "";
	}

	
	/** Recupera el tipo de documento según AFIP a partir del tipo de la factura */
	protected static int getCbteTipo(String docTypeKey) throws Exception {
		int i=0;
		while (!Character.isDigit(docTypeKey.charAt(i))) i++;
		return docTypeMap.get(docTypeKey.substring(0, i));
	}
	
	
	/** Genera la consulta al WS de AFIP por el comprobante dado */
	protected HashMap<String, String> consultarCAE(long cbteNro, int cbteTipo, String cbeTipoDesc, int ptoVta) {
		
		// Valores minimos a retornar, bien sea error o no (numero de documento, tipo de comprobante y punto de venta)
		HashMap<String, String> retValues = new HashMap<String, String>(); 
		retValues.put("DocNro", 	"" + cbteNro);
		retValues.put("CbteTipo", 	"" + cbteTipo);
		retValues.put("PtoVta", 	"" + ptoVta);
		
		// Recuperar el servicio
		ServiceLocator locator = new ServiceLocator();
 		// Se intenta recuperar preferencia indicando URL de consulta. En caso de no existir se toma valor por defecto (URL actual de producción)
		String afipURL = MPreference.GetCustomPreferenceValue(PREFERENCE_WSFECC_URL, clientID);
		if (afipURL == null || afipURL.length() == 0)
			afipURL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
		locator.setServiceSoapEndpointAddress(afipURL);
		ServiceSoap service = null;
		try {
			service = locator.getServiceSoap();
		} catch (Exception e) {
			retValues.put("Resultado", "Error acceso WSFE: " + e.toString());
			log.saveError("[WSFECC] Error acceso WSFE: ", e.toString());
			return retValues;
		}
		
		FEAuthRequest auth = new FEAuthRequest();
		FECompConsultaReq consulta = new FECompConsultaReq();
		
		auth.setCuit(cuit);
		auth.setSign(sign);
		auth.setToken(token);
		
		consulta.setCbteNro(cbteNro);
		consulta.setCbteTipo(cbteTipo);
		consulta.setPtoVta(ptoVta);

		// Invocacion a la operacion
		FECompConsultaResponse response = null;
		int tryNo = 0;
		boolean endLoop = false;
		while (!endLoop) {
			try {
				tryNo++;
				response = service.FECompConsultar(auth, consulta);
				response.toString();	// <-- NPE check
				endLoop = true;
			} catch (Exception e) {
				// Al capturar una excepción al RETRY_MAX intento, no continuar intentando
				if (tryNo == RETRY_MAX) {
					// Error al interactuar con WSFE de AFIP
					retValues.put("Resultado", "Error de conexión: " + e.toString());
					log.saveError("[WSFECC] Error de conexión: ", e.toString());
					return retValues;
				}
			}
		}
		
		// Error recibido desde WSFE de AFIP
		if (response.getErrors() != null && response.getErrors().length > 0) {
			StringBuffer completeErrorStr = new StringBuffer();
			for (Err error : response.getErrors()) {
				StringBuffer errorStr = new StringBuffer();
				errorStr.append(error.getCode()).append(". ").append(error.getMsg());
				completeErrorStr.append(errorStr);
			}
			retValues.put("Resultado", "Error: " + completeErrorStr.toString());
			log.saveError("[WSFECC] Error para cbteNro " + cbteNro + ", cbteTipo " + cbteTipo + ", ptoVta " + ptoVta + ": ", completeErrorStr.toString());
			return retValues;
		}
		
		// En este punto se supone valores recibidos conformes a un comprobante encontrado
		retValues.put("CbteDesde", 			"" + response.getResultGet().getCbteDesde());
		retValues.put("CbteFch", 			"" + response.getResultGet().getCbteFch());
		retValues.put("CbteHasta", 			"" + response.getResultGet().getCbteHasta());
		retValues.put("CbteTipo", 			"" + response.getResultGet().getCbteTipo());
		retValues.put("CodAutorizacion", 	"" + response.getResultGet().getCodAutorizacion());
		retValues.put("Concepto", 			"" + response.getResultGet().getConcepto());
		retValues.put("DocNro", 			"" + response.getResultGet().getDocNro());
		retValues.put("DocTipo", 			"" + response.getResultGet().getDocTipo());
		retValues.put("EmisionTipo", 		"" + response.getResultGet().getEmisionTipo());
		retValues.put("FchProceso", 		"" + response.getResultGet().getFchProceso());
		retValues.put("FchServDesde", 		"" + response.getResultGet().getFchServDesde());
		retValues.put("FchServHasta", 		"" + response.getResultGet().getFchServHasta());
		retValues.put("FchVto", 			"" + response.getResultGet().getFchVto());
		retValues.put("FchVtoPago", 		"" + response.getResultGet().getFchVtoPago());
		retValues.put("ImpIVA", 			"" + response.getResultGet().getImpIVA());
		retValues.put("ImpNeto", 			"" + response.getResultGet().getImpNeto());
		retValues.put("ImpOpEx", 			"" + response.getResultGet().getImpOpEx());
		retValues.put("ImpTotal", 			"" + response.getResultGet().getImpTotal());
		retValues.put("ImpTotConc", 		"" + response.getResultGet().getImpTotConc());
		retValues.put("ImpTrib", 			"" + response.getResultGet().getImpTrib());
		retValues.put("MonCotiz", 			"" + response.getResultGet().getMonCotiz());
		retValues.put("MonId", 				"" + response.getResultGet().getMonId());
		retValues.put("PtoVta", 			"" + response.getResultGet().getPtoVta());
		retValues.put("Resultado", 			"" + response.getResultGet().getResultado());
		
		return retValues; 
	}
	
	
	/** Genera la consulta al WS de AFIP por el comprobante dado */
	public HashMap<String, String> consultarCAEX(long cbteNro, int cbteTipo, String cbeTipoDesc, int ptoVta) {
		
		// Valores minimos a retornar, bien sea error o no (numero de documento, tipo de comprobante y punto de venta)
		HashMap<String, String> retValues = new HashMap<String, String>(); 
		retValues.put("DocNro", 	"" + cbteNro);
		retValues.put("CbteTipo", 	"" + cbteTipo);
		retValues.put("PtoVta", 	"" + ptoVta);
		
		// Recuperar el servicio
		fexv1.dif.afip.gov.ar.ServiceLocator locator = new fexv1.dif.afip.gov.ar.ServiceLocator();
 		// Se intenta recuperar preferencia indicando URL de consulta. En caso de no existir se toma valor por defecto (URL actual de producción)
		String afipURL = MPreference.GetCustomPreferenceValue(PREFERENCE_WSFEXCC_URL, clientID);
		if (afipURL == null || afipURL.length() == 0)
			afipURL = "https://servicios1.afip.gov.ar/wsfexv1/service.asmx";
		locator.setServiceSoapEndpointAddress(afipURL);
		fexv1.dif.afip.gov.ar.ServiceSoap service = null;
		try {
			service = locator.getServiceSoap();
		} catch (Exception e) {
			retValues.put("Resultado", "Error acceso WSFEX: " + e.toString());
			log.saveError("[WSFEXCC] Error acceso WSFEX: ", e.toString());
			return retValues;
		}
		
		ClsFEXAuthRequest auth = new ClsFEXAuthRequest();
		ClsFEXGetCMP consulta = new ClsFEXGetCMP();
		
		auth.setCuit(cuit);
		auth.setSign(sign);
		auth.setToken(token);
		
		consulta.setCbte_nro(cbteNro);
		consulta.setCbte_tipo((short)cbteTipo);
		consulta.setPunto_vta((short)ptoVta);

		// Invocacion a la operacion
		FEXGetCMPResponse response = null;
		int tryNo = 0;
		boolean endLoop = false;
		while (!endLoop) {
			try {
				tryNo++;
				response = service.FEXGetCMP(auth, consulta);
				response.toString();	// <-- NPE check
				endLoop = true;
			} catch (Exception e) {
				// Al capturar una excepción al RETRY_MAX intento, no continuar intentando
				if (tryNo == RETRY_MAX) {
					// Error al interactuar con WSFE de AFIP
					retValues.put("Resultado", "Error de conexión: " + e.toString());
					log.saveError("[WSFECC] Error de conexión: ", e.toString());
					return retValues;
				}
			}
		}
		
		// Error recibido desde WSFE de AFIP
		if (response.getFEXErr() != null && !"OK".equalsIgnoreCase(response.getFEXErr().getErrMsg())) {
			StringBuffer completeErrorStr = new StringBuffer();
			completeErrorStr.append(response.getFEXErr().getErrCode()).append(". ").append(response.getFEXErr().getErrMsg());
			retValues.put("Resultado", "Error: " + completeErrorStr.toString());
			log.saveError("[WSFECC] Error para cbteNro " + cbteNro + ", cbteTipo " + cbteTipo + ", ptoVta " + ptoVta + ": ", completeErrorStr.toString());
			return retValues;
		}
		
		// En este punto se supone valores recibidos conformes a un comprobante encontrado
		retValues.put("CbteDesde", 			"" + response.getFEXResultGet().getCbte_nro());
		retValues.put("CbteFch", 			"" + response.getFEXResultGet().getFecha_cbte());
		retValues.put("CbteHasta", 			"" + response.getFEXResultGet().getCbte_nro());
		retValues.put("CbteTipo", 			"" + response.getFEXResultGet().getCbte_tipo());
		retValues.put("CodAutorizacion", 	"" + response.getFEXResultGet().getCae());
//		retValues.put("Concepto", 			"" + response.getFEXResultGet().getDst_cmp());
		retValues.put("DocNro", 			"" + response.getFEXResultGet().getCbte_nro());
		retValues.put("DocTipo", 			"" + response.getFEXResultGet().getTipo_expo());
//		retValues.put("EmisionTipo", 		"" + response.getFEXResultGet().getEmisionTipo());
//		retValues.put("FchProceso", 		"" + response.getFEXResultGet().getFecha_cbte_cae());
//		retValues.put("FchServDesde", 		"" + response.getFEXResultGet().getFchServDesde());
//		retValues.put("FchServHasta", 		"" + response.getFEXResultGet().getFchServHasta());
		retValues.put("FchVto", 			"" + response.getFEXResultGet().getFch_venc_Cae());
//		retValues.put("FchVtoPago", 		"" + response.getFEXResultGet().getFchVtoPago());
//		retValues.put("ImpIVA", 			"" + response.getFEXResultGet().getImpIVA());
//		retValues.put("ImpNeto", 			"" + response.getFEXResultGet().getImpNeto());
//		retValues.put("ImpOpEx", 			"" + response.getFEXResultGet().getImpOpEx());
		retValues.put("ImpTotal", 			"" + response.getFEXResultGet().getImp_total());
//		retValues.put("ImpTotConc", 		"" + response.getFEXResultGet().getImpTotConc());
//		retValues.put("ImpTrib", 			"" + response.getFEXResultGet().getImpTrib());
		retValues.put("MonCotiz", 			"" + response.getFEXResultGet().getMoneda_ctz());
		retValues.put("MonId", 				"" + response.getFEXResultGet().getMoneda_Id());
		retValues.put("PtoVta", 			"" + response.getFEXResultGet().getPunto_vta());
		retValues.put("Resultado", 			"" + response.getFEXResultGet().getResultado());
		
		return retValues; 
	}
	
	/**
	 * Retorna el tipo de comprobante segun la denominacion de AFIP a partir del doctype 
	 */
	protected int getCbteTipo(X_C_DocType aDocType) {
		try {
			StringBuffer type = new StringBuffer();
			for (int i = 0; i < aDocType.getDocTypeKey().length(); i++) {
				if (Character.isDigit(aDocType.getDocTypeKey().charAt(i)))
					break;
				type.append(aDocType.getDocTypeKey().charAt(i));
			}
			return docTypeMap.get(type.toString());
		} catch (Exception e) {
			return -1;
		}
	}

	/** Validaciones preliminares */
	protected void checkPreconditions() throws Exception {
		if (cbteNroFrom == -1 || cbteNroTo == -1 || cbteNroFrom > cbteNroTo)
			throw new Exception("Rango de comprobantes indicado invalido");
		if (cbteNroTo - cbteNroFrom > MAX_DOCS )
			throw new Exception("Rango de comprobantes a consultar muy extenso. Máximo por ejecución: " + MAX_DOCS);
		if (ptoVta == -1)
			throw new Exception("Punto de venta indicado invalido");
		if (cbteTipo == -1)
			throw new Exception("Tipo de documento indicado invalido");
	}
	
	
	/** Carga inicial */
	protected void loadInitialValues() throws Exception {
		// Parse del TA.xml
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		File file = new File(getTAFileName());
		Document doc = builder.parse(file);
		// Token
		NodeList tokenNodes = doc.getElementsByTagName(TA_TAG_TOKEN);
		token = (tokenNodes.item(0).getFirstChild().getNodeValue());
		// Sign
		NodeList signNodes = doc.getElementsByTagName(TA_TAG_SIGN);
		sign = (signNodes.item(0).getFirstChild().getNodeValue());
		// ClientID
		clientID = getAD_Client_ID();
		// Cuit
		try {
			cuit = Long.parseLong(DB.getSQLValueString(null, "SELECT replace(cuit, '-', '') FROM AD_ClientInfo WHERE AD_Client_ID = ?", clientID));
		} catch (Exception ex) {
			throw new Exception ("Error al recuperar CUIT de la BBDD de compañía " + clientID + ". " + ex.getMessage());
		}
			

	}

	/** Consultar cada una de los comprobantes */
	protected void queryInvoices() throws Exception {
		// Consultar cada comprobante e incorporar a la nómina de resultados
		for (long i = cbteNroFrom; i <= cbteNroTo; i++) {
			if (!isExportacion(cbteTipo))
				retrievedDocuments.add(consultarCAE(i, cbteTipo, "", ptoVta));
			else
				retrievedDocuments.add(consultarCAEX(i, cbteTipo, "", ptoVta));
		}
	}

	
	/** Genera y visuaiza el informe Jasper */
	protected void createReport() throws Exception {
		ds = new WSFEConsultarComprobanteDataSource(retrievedDocuments);
		
		try {
			ds.loadData();
		} catch (RuntimeException e) {
			throw new RuntimeException("No se pueden cargar los datos del informe: " + e.getMessage(), e);
		}
		
		// Determinar JasperReport para wrapper, tabla y registro actual
		ProcessInfo base_pi = getProcessInfo();
		int AD_Process_ID = base_pi.getAD_Process_ID();
		MProcess proceso = MProcess.get(Env.getCtx(), AD_Process_ID);
		if (proceso.isJasperReport() != true)
			throw new Exception ("No es informe Jasper");
		MJasperReport jasperwrapper = new MJasperReport(getCtx(), proceso.getAD_JasperReport_ID(), get_TrxName());
				
		jasperwrapper.addParameter("P_CBTE_FROM", cbteNroFrom);
		jasperwrapper.addParameter("P_CBTE_TO", cbteNroTo);
		jasperwrapper.addParameter("P_PTOVTA", ptoVta);
		jasperwrapper.addParameter("P_TIPO_CBTE", cbteTipoNombre);
		
		try {
			jasperwrapper.fillReport(ds, this);
			jasperwrapper.showReport(getProcessInfo());
		} catch (RuntimeException e) {
			throw new RuntimeException("No se ha podido rellenar el informe: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna la ubicacion completa del archivo TA (path y nomrbe de archivo), basado en las preferencias y el tipo de documento
	 */
	protected String getTAFileName() throws Exception {

		/* === RECUPERACION DE PREFERENCIA POR USUARIO (acorde a Wsfe.java)  === */
		// Probar configuracion especifica para un pto vta dado
		MPreference preference = MPreference.getUserPreference(Env.getCtx(), "WSFE_PV" + ptoVta, null);
		if(preference == null){
			// Probar configuracion PV general
			preference = MPreference.getUserPreference(Env.getCtx(), "WSFE", null);
		}
		String pyafipwsLocation = null;
		if (preference != null) {
			pyafipwsLocation = preference.getValue();
		} else {
			/* === RECUPERACION DE PREFERENCIA POR COMPAÑÍA (sin especificación de usuario) === */
			// Probar configuracion especifica para un pto vta dado
			pyafipwsLocation = MPreference.GetCustomPreferenceValue("WSFE_PV" + ptoVta, clientID);
			// Probar configuracion general
			if (pyafipwsLocation == null || pyafipwsLocation.length() == 0)
				pyafipwsLocation = MPreference.GetCustomPreferenceValue("WSFE", clientID);
		}
		if (pyafipwsLocation == null || pyafipwsLocation.length() == 0)
			throw new Exception ("Preferencia WSFE_PV" + ptoVta + " o WSFE no encontradas en los valores predeterminados.");
		return pyafipwsLocation + File.separator + getSimpleTAFileName(cbteTipo);
	}
	
	
	/**
	 * Retorna el nombre del archivo TA dependiendo si es una factura de exportacion o no
	 */
	protected String getSimpleTAFileName(int tipoCbte) {
		return isExportacion(tipoCbte) ? TA_WSFEX_FILENAME : TA_WSFE_FILENAME;
	}
	
	
	/**
	 * Retorna true si es un comprobante de exportacion a partir de un tipoCbte según denominacion de AFIP 
	 */
	protected boolean isExportacion(int tipoCbte) {
		return (tipoCbte == TIPO_COMPROBANTE_FACTURA_EXPORTACION ||
				tipoCbte == TIPO_COMPROBANTE_NOTA_DEBITO_EXPORTACION ||
				tipoCbte == TIPO_COMPROBANTE_NOTA_CREDITO_EXPORTACION || 
				tipoCbte == TIPO_COMPROBANTE_FACTURA_EXPORTACION_SIMPLIFICADO);
	}	
	 

	
}
