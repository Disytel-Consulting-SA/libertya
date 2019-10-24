package org.openXpertya.process.customImport.centralPos.jobs;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MExpenseConcepts;
import org.openXpertya.model.MExternalService;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.MTax;
import org.openXpertya.model.X_C_CardSettlementConcepts;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.model.X_C_Region;
import org.openXpertya.model.X_C_RetencionSchema;
import org.openXpertya.model.X_C_RetencionType;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.http.Post;
import org.openXpertya.process.customImport.centralPos.pojos.login.Login;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Proceso de importación abstracto.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public abstract class Import {
	
	public static final String EXTERNAL_SERVICE_AMEX =      "Central Pos - Amex";
	public static final String EXTERNAL_SERVICE_FIRSTDATA = "Central Pos - FirstData";
	public static final String EXTERNAL_SERVICE_NARANJA =   "Central Pos - Naranja";
	public static final String EXTERNAL_SERVICE_VISA =      "Central Pos - Visa";
	public static final String EXTERNAL_SERVICE_CABAL =      "Central Pos - Cabal";

	/** Resultados por página por defecto (en caso de no encontrarlo en las configuraciones). */
	private static final int DEFAULT_RESULTS_PER_PAGE = 100;

	/**
	 * Obtener la clase de importación perteneciente al tipo de tarjeta parámetro
	 * 
	 * @param ctx            contexto actual
	 * @param creditCardType tipo de tarjeta
	 * @param trxName        trx actual
	 * @return clase de importación correspondiente a la tarjeta parámetro, null
	 *         caso contrario
	 * @throws Exception
	 */
	public static Import get(Properties ctx, String creditCardType, String trxName) throws Exception {
		Import importJob = null;
		if(!Util.isEmpty(creditCardType, true)) {
			if (creditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_FIRSTDATA)) {
				importJob = new ImportFirstData(ctx, trxName);
			} else if (creditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_NARANJA)) {
				importJob = new ImportNaranja(ctx, trxName);
			} else if (creditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_AMEX)) {
				importJob = new ImportAmex(ctx, trxName);
			} else if (creditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_VISA)) {
				importJob = new ImportVisa(ctx, trxName);
			} else if (creditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_CABAL)) {
				importJob = new ImportCabal(ctx, trxName);
			}
		}
		return importJob;			
	}
	
	/** Logger. */
	protected CLogger log;
	/** Token de autenticación. */
	protected String token;
	/** Contexto. */
	protected Properties ctx;
	/** Nombre de la transacción. */
	protected String trxName;
	/** Parámetros adicionales de consulta. Opcionales. */
	protected Map<String, String> extraParams;
	/** Configuración de Servicios Externos. */
	protected MExternalService externalService;
	/** Cantidad de elementos por pagina. */
	protected int resultsPerPage;

	/**
	 * Constructor.
	 * @param conf Nombre de la configuraciín de Servicios Externos.
	 * @param ctx Contexto.
	 * @param trxName Nombre de la Transacción.
	 * @throws Exception Si la autenticación falla.
	 */
	public Import(String conf, Properties ctx, String trxName) throws Exception {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_ExternalService_ID ");
		sql.append("FROM ");
		sql.append("	" + MExternalService.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	name = ? ");

		int C_ExternalService_ID = DB.getSQLValue(trxName, sql.toString(), conf);

		externalService = new MExternalService(ctx, C_ExternalService_ID, trxName);

		resultsPerPage = DEFAULT_RESULTS_PER_PAGE;
		X_C_ExternalServiceAttributes perPage = externalService.getAttributeByName("Elementos por Página");
		if (perPage != null) {
			resultsPerPage = Integer.valueOf(perPage.getName());
		}

		token = login();

		if (token == null) {
			throw new Exception(CLogger.retrieveErrorString(Msg.getMsg(Env.getAD_Language(ctx), "CentralPosBadLogin")));
		}

		this.ctx = ctx;
		this.trxName = trxName;
		this.extraParams = new HashMap<String, String>();
	}

	/**
	 * Realiza la autenticación y obtiene el token de seguridad para
	 * realizar las consultas posteriores.<br>
	 * IMPORTANTE: Deben estar apropiadamente configurados los campos "Email y Contraseña".
	 */
	public String login() throws Exception{
		X_C_ExternalServiceAttributes attr = externalService.getAttributeByName("URL Login");
		Post loginPost = new Post(attr.getName());

		loginPost.addParam("email", externalService.getUserName());
		loginPost.addParam("password", externalService.getPassword());

		Login loginResponse = (Login) loginPost.execute(Login.class);
		
		if (loginResponse != null) {
			return loginResponse.getToken();
		} else {
			return null;
		}
	}

	/**
	 * Construye un llamado GET, agregando además,
	 * la autenticación en el Header del llamado.
	 * @param url Url.
	 * @return Llamado get listo.
	 */
	public Get makeGetter(String url) {
		Get get = new Get(url);
		get.addHeader("Authorization", "Bearer " + token);
		return get;
	}

	/**
	 * Construye un llamado GET, agregando además,
	 * la autenticación en el Header del llamado.
	 * @return Llamado get listo.
	 */
	public Get makeGetter() {
		Get get = new Get(externalService.getURL());
		get.addHeader("Authorization", "Bearer " + token);
		return get;
	}

	/**
	 * @return Nombre del servicio asociado a este proceso de importación 
	 */
	public String getServiceName() {
		return externalService == null?"":externalService.getName();
	}
	
	/**
	 * @return nombre de tabla asociada a esta importación
	 */
	public abstract String getTableName();
	
	/**
	 * @return los campos a filtrar de la tabla de importación
	 */
	public abstract String[] getFilteredFields();
	
	/**
	 * Inicia la importación.
	 * @return Total de elementos importados.
	 * @throws SaveFromAPIException
	 */
	public abstract String excecute() throws SaveFromAPIException, Exception;

	/**
	 * Setea la fecha de inicio de la consulta de importación
	 * 
	 * @param date fecha de inicio
	 */
	public abstract void setDateFromParam(Timestamp date);
	
	/**
	 * Setea la fecha de fin de la consulta de importación
	 * 
	 * @param date fecha de fin
	 */
	public abstract void setDateToParam(Timestamp date);
	
	/**
	 * Realiza las validaciones previo a guardar los datos en la tabla
	 * 
	 * @param ctx        contexto actual
	 * @param rs         result set con los datos actuales de la iteración
	 * @param attributes atributos del servicio externo
	 * @param trxName    nombre de trx actual
	 * @throws Exception en caso que alguno de los datos no permita seguir con la
	 *                   operación
	 */
	public abstract void validate(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes, String trxName) throws Exception;
	
	/**
	 * Realiza la creación/modificación de la liquidación
	 * 
	 * @param ctx        contexto actual
	 * @param rs         result set con los datos actuales de la iteración
	 * @param attributes atributos del servicio externo
	 * @param trxName    nombre de trx actual
	 * @return true si se creó la liquidación, false si se ignoró y no se creó nada
	 * @throws Exception en caso que alguno de los datos no permita seguir con la
	 *                   operación o se da error en el proceso de creación
	 */
	public abstract boolean create(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes, String trxName) throws Exception;
	
	/**
	 * Agrega parámetros adicionales a las consultas.
	 * @param name Nombre del parámetro.
	 * @param value Valor del parámetro.
	 */
	public void addParam(String name, Object value) {
		extraParams.put(name, String.valueOf(value));
	}

	/**
	 * Setea la clase encargada de emitir logs.
	 * @param log
	 */
	public void setCLogger(CLogger log) {
		this.log = log;
	}

	/**
	 * Retorna un mensaje de información. Utilizado para informar
	 * el estado de importación hacia las tablas temporales.
	 * @param params parámetros del mensaje.
	 * @return String con el mensaje de información correspondiente.
	 */
	protected String msg(Object[] params) {
		return Msg.getMsg(Env.getAD_Language(ctx), "CentralPosResultMsg", params);
	}

	/**
	 * Obtiene el ID de una Entidad Comercial vinculada a Entidades Financieras a
	 * travéz del Número de Comercio.
	 * 
	 * @param establishmentNumber Número de Comercio.
	 * @return ID de la Entidad Comercial, si se encontró, caso contrario -1.
	 */
	protected int getC_BPartner_ID(Properties ctx, String establishmentNumber, String trxName) {
		if (establishmentNumber == null || establishmentNumber.trim().isEmpty()) {
			return -1;
		}
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_BPartner_ID ");
		sql.append("FROM ");
		sql.append("	" + MEntidadFinanciera.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	EstablishmentNumber = '" + establishmentNumber + "' ");

		return DB.getSQLValue(trxName, sql.toString());
	}
	
	/**
	 * Obtiene el ID del concepto a partir de la clave
	 * 
	 * @param value clave del concepto
	 * @return ID del Concepto, si se encontró, caso contrario -1.
	 */
	protected int getCardSettlementConceptIDByValue(Properties ctx, String value, String trxName) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_CardSettlementConcepts_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_CardSettlementConcepts.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	value = ? ");
		sql.append("	AND AD_Client_ID = ? ");

		return DB.getSQLValueEx(trxName, sql.toString(), value, Env.getAD_Client_ID(ctx));
	}
	
	/**
	 * Obtiene el ID del impuesto a partir del nombre
	 * 
	 * @param ctx     contexto
	 * @param name    nombre del impuesto
	 * @param trxName nombre de la trx
	 * @return ID del impuesto por el nombre parámetro, -1 caso que no se encuentre
	 */
	protected int getTaxIDByName(Properties ctx, String name, String trxName) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_Tax_ID ");
		sql.append("FROM ");
		sql.append("	" + MTax.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	name = ? ");
		sql.append("	AND AD_Client_ID = ? ");

		return DB.getSQLValueEx(trxName, sql.toString(), name, Env.getAD_Client_ID(ctx));
	}
	
	/**
	 * Obtiene el ID del esquema de retención a partir de la clave
	 * 
	 * @param ctx     contexto
	 * @param value   clave del esquema de retención
	 * @param trxName nombre de la trx
	 * @return ID del esquema de retención por la clave parámetro, -1 caso que no se
	 *         encuentre
	 */
	protected int getRetencionSchemaIDByValue(Properties ctx, String value, String trxName) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_RetencionSchema_ID ");
		sql.append("FROM ");
		sql.append("	" + MRetencionSchema.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	value = ? ");
		sql.append("	AND AD_Client_ID = ? ");

		return DB.getSQLValueEx(trxName, sql.toString(), value, Env.getAD_Client_ID(ctx));
	}
	
	/**
	 * Obtiene el ID del Concepto de Liquidación basado en los datos parámetros
	 * 
	 * @param ctx                         contexto
	 * @param C_CardSettlementConcepts_ID id del concepto de liquidación
	 * @param C_CreditCardSettlement_ID   id de la liquidación
	 * @param trxName                     nombre de la trx
	 * @return ID del Concepto de Liquidación, si no se encuentra -1
	 */
	protected int getExpenseConceptIDByValueAndSettlementID(Properties ctx, int C_CardSettlementConcepts_ID, int C_CreditCardSettlement_ID, String trxName) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_ExpenseConcepts_ID ");
		sql.append("FROM ");
		sql.append("	" + MExpenseConcepts.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CardSettlementConcepts_ID = ? ");
		sql.append("	AND AD_Client_ID = ? ");
		sql.append("	AND C_CreditCardSettlement_ID = ? ");

		return DB.getSQLValueEx(trxName, sql.toString(), C_CardSettlementConcepts_ID, Env.getAD_Client_ID(ctx),
				C_CreditCardSettlement_ID);
	}
	
	/**
	 * Obtiene el ID esquema de retención a partir del número del establecimiento
	 * 
	 * @param ctx     contexto actual
	 * @param nroEst  número de establecimiento
	 * @param trxName trx actual
	 * @return ID esquema de retención, -1 caso que no se encuentre
	 */
	protected int getRetencionSchemaByNroEst(Properties ctx, String nroEst, String trxName) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	rs.C_RetencionSchema_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_RetencionSchema.Table_Name + " rs ");
		sql.append("	INNER JOIN " + MEntidadFinanciera.Table_Name + " ef ");
		sql.append("		ON ef.C_Region_ID = rs.C_Region_ID ");
		sql.append("	INNER JOIN " + X_C_RetencionType.Table_Name + " rt ");
		sql.append("		ON rs.C_RetencionType_ID = rs.C_RetencionType_ID ");
		sql.append("WHERE ");
		sql.append("	rs.RetencionApplication = 'S' ");
		sql.append("	AND rt.RetentionType = 'B' ");
		sql.append("	AND rt.IsActive = 'Y' ");
		sql.append("	AND rs.IsActive = 'Y' ");
		sql.append("	AND ef.EstablishmentNumber = ? ");

		PreparedStatement ps = null;
		ResultSet rst = null;

		int C_RetencionSchema_ID = -1;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setString(1, nroEst);
			rst = ps.executeQuery();

			if (rst.next()) {
				C_RetencionSchema_ID = rst.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return C_RetencionSchema_ID;
	}
	
	/**
	 * Obtiene el ID de una Entidad Financiera a través del Número de Comercio.
	 * 
	 * @param establishmentNumber Número de Comercio.
	 * @return ID de la Entidad Financiera, si se encontró, caso contrario -1.
	 */
	protected int getM_EntidadFinanciera_ID(Properties ctx, String establishmentNumber, String trxName) {
		if (establishmentNumber == null || establishmentNumber.trim().isEmpty()) {
			return -1;
		}
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	M_EntidadFinanciera_ID ");
		sql.append("FROM ");
		sql.append("	" + MEntidadFinanciera.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	EstablishmentNumber = '" + establishmentNumber + "' ");

		return DB.getSQLValue(trxName, sql.toString());
	}

	/**
	 * Obtiene una liquidación a partir del número de liquidación y la 
	 * E.Comercial asociada.
	 * @param nro_liq Nombre por el cual buscar el registro.
	 * @param C_BPartner_ID ID Entidad Comercial.
	 * @param paymentDate fecha de liquidación, si es null no compara por la misma
	 * @return ID Liquidación o -1 si no existe.
	 */
	protected int getSettlementIdFromNroAndBPartner(Properties ctx, String nro_liq, int C_BPartner_ID, Timestamp paymentDate, String trxName) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	c_creditcardsettlement_id ");
		sql.append("FROM ");
		sql.append("	" + MCreditCardSettlement.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	c_bpartner_id = ? ");
		sql.append("	AND settlementno = '").append(nro_liq).append("'");
		if(paymentDate != null) {
			sql.append("	AND paymentdate::date = ?::date ");
		}
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		int ccsID = -1;
		try {
			ps = DB.prepareStatement(sql.toString(), trxName, true);
			ps.setInt(1, C_BPartner_ID);
			if(paymentDate != null) {
				ps.setTimestamp(2, paymentDate);
			}
			rs = ps.executeQuery();
			if(rs.next()) {
				ccsID = rs.getInt("c_creditcardsettlement_id");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return ccsID;
	}
	
	/**
	 * Obtiene el esquema de retención para Bs As
	 * 
	 * @return ID del esquema de retención para Bs As
	 */
	protected int getRetencionSchemaForBsAs() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	rs.C_RetencionSchema_ID, ");
		sql.append("	rs.C_Region_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_RetencionSchema.Table_Name + " rs ");
		sql.append("	INNER JOIN " + X_C_RetencionType.Table_Name + " rt ");
		sql.append("		ON rs.C_RetencionType_ID = rs.C_RetencionType_ID ");
		sql.append("	INNER JOIN " + X_C_Region.Table_Name + " rg ");
		sql.append("		ON rs.C_Region_ID = rg.C_Region_ID ");
		sql.append("WHERE ");
		sql.append("	rs.RetencionApplication = 'S' ");
		sql.append("	AND rt.RetentionType = 'B' ");
		sql.append("	AND rt.IsActive = 'Y' ");
		sql.append("	AND rs.IsActive = 'Y' ");
		sql.append("	AND rs.IsActive = 'Y' ");
		sql.append("	AND rg.jurisdictioncode = 902 ");

		PreparedStatement ps = null;
		ResultSet rst = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if (rst.next()) {
				return rst.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	/**
	 * Realiza una multiplicación entre 2 números, o un número y un signo.
	 * 
	 * @param op1        Necesariamente debe ser un número, si se desea multiplicar
	 *                   con un símbolo, el mismo debe ir en el op2.
	 * @param op2        Número, símbolo (+, -), o palabra (Negativo, Positivo)
	 * @param defaultOP2 si el parámetro op2 es null o vacío, se toma este dato
	 * @return BigDecimal correspondiente. Si alguno de los operadores es nulo, o
	 *         vacío, o el op1 no es un BigDecimal válido, retorna 0 (cero).
	 */
	protected BigDecimal safeMultiply(String op1, String op2, String defaultOP2) {
		if (Util.isEmpty(op1, true)) {
			return BigDecimal.ZERO;
		}
		if(Util.isEmpty(op2, true)) {
			op2 = defaultOP2;
		}
		
		BigDecimal num1 = null;
		try {
			num1 = new BigDecimal(op1);
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
		
		String[] negatives = new String[] { "-", "Negativo" };
		for (String s : negatives) {
			if (op2.equalsIgnoreCase(s)) {
				return negativeValue(num1);
			}
		}
		
		String[] positives = new String[] { "+", "Positivo" };
		for (String s : positives) {
			if (op2.equalsIgnoreCase(s)) {
				return num1;
			}
		}
		
		BigDecimal num2 = null;
		try {
			num2 = new BigDecimal(op2);
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
		return num1.multiply(num2);
	}

	/**
	 * Realiza una multiplicación entre 2 números, o un número y un signo.
	 * 
	 * @param op1 Necesariamente debe ser un número, si se desea multiplicar con un
	 *            símbolo, el mismo debe ir en el op2.
	 * @param op2 Número, símbolo (+, -), o palabra (Negativo, Positivo)
	 * @return BigDecimal correspondiente. Si alguno de los operadores es nulo, o
	 *         vacío, o el op1 no es un BigDecimal válido, retorna 0 (cero).
	 */
	protected BigDecimal safeMultiply(String op1, String op2) {
		return safeMultiply(op1, op2, "0.00");
	}
	
	/**
	 * A partir de un String, intenta generar un BigDecimal.
	 * @param number String a ser convertido.
	 * @return BigDecimal, en caso de que el String sea nulo,
	 * vacío, o inválido, devolverá 0.
	 */
	protected BigDecimal safeNumber(String number) {
		if (number == null || number.trim().isEmpty()) {
			return BigDecimal.ZERO;
		}
		try {
			return new BigDecimal(number);
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * Retorna el valor negado de un BigDecimal.
	 * @param input número de entrada a negar.
	 * @return Valor negado.
	 */
	protected BigDecimal negativeValue(BigDecimal input) {
		if (input == null || input.signum() == 0) {
			return BigDecimal.ZERO;
		}
		return input.negate();
		
	}
}
