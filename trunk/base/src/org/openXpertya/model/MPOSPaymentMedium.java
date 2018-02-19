package org.openXpertya.model;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

public class MPOSPaymentMedium extends X_C_POSPaymentMedium {

	/** Log estático */
	private static CLogger s_log = CLogger.getCLogger(MPOSPaymentMedium.class);

	/**
	 * Obtiene los medios de pago disponibles para el tipo de pago parámetro y
	 * contexto de uso. Si el tipo de pago parámetro es null y el contexto de
	 * uso también es null entonces retorna todos los medios de pago disponibles
	 * que existen para la compañía actual y que se encuentren activos. Un medio
	 * de pago es disponible cuando es válido para la fecha actual. El contexto
	 * de uso determina los medios de pago disponibles para ese contexto, el
	 * parámetro exclude verifica si hayq ue excluir el contexto que viene como
	 * parámetro o se debe incluir junto con el tipo de contexto Ambos.
	 * 
	 * @param ctx
	 *            contexto
	 * @param tenderType
	 *            tipo de pago
	 * @param contextOfUse
	 *            contexto de uso del medio de pago
	 * @param exclude
	 *            true si se debe excluir el contexto de uso en la búsqueda o se
	 *            debe inclui junto con Ambos.
	 * @param trxName
	 *            transacción actual
	 * @return lista de medios de pago disponibles
	 */
	public static List<MPOSPaymentMedium> getAvailablePaymentMediums(Properties ctx, String tenderType, String contextOfUse, boolean exclude, String trxName){
		// Se buscan los medios de pago que sean válidos para la fecha actual.
		StringBuffer sql = new StringBuffer("SELECT * "
				+ "FROM C_POSPaymentMedium " + "WHERE AD_Client_ID = ? AND (AD_Org_ID = ? OR AD_Org_ID = 0) "
				+ "AND ? BETWEEN DateFrom AND DateTo " + "AND IsActive = 'Y' ");
		if(tenderType != null){
			sql.append(" AND (tendertype = '"+tenderType+"') ");
		}
		if(contextOfUse != null){
			if(exclude){
				sql.append(" AND (context <> '"+contextOfUse+"') ");
			}
			else{
				sql.append(" AND (context = '" + contextOfUse
						+ "' OR context = '" + MPOSPaymentMedium.CONTEXT_Both
						+ "') ");
			}
		}
		sql.append("ORDER BY Name ASC");
		List<MPOSPaymentMedium> paymentMediums = new ArrayList<MPOSPaymentMedium>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = DB.prepareStatement(sql.toString(),trxName);
			int i = 1;
			pstmt.setInt(i++, Env.getAD_Client_ID(ctx));
			pstmt.setInt(i++, Env.getAD_Org_ID(ctx));
			pstmt.setDate(i++, new Date(System.currentTimeMillis()));
			rs = pstmt.executeQuery();
			while(rs.next()){
				paymentMediums.add(new MPOSPaymentMedium(ctx, rs, trxName));
			}
		} catch (Exception e) {
			s_log.log(Level.SEVERE, "Error getting POS payment mediums.", e);
		} finally{
			try {
				if(pstmt != null)pstmt.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				s_log.log(Level.SEVERE, "Error getting POS payment mediums.", e2);
			}
		}
		return paymentMediums;
	}
	
	/**
	 * Obtiene los medios de pago disponibles para el tipo de pago parámetro y
	 * contexto de uso. Si el tipo de pago parámetro es null y el contexto de
	 * uso también es null entonces retorna todos los medios de pago disponibles
	 * que existen para la compañía actual y que se encuentren activos. Un medio
	 * de pago es disponible cuando es válido para la fecha actual. El contexto
	 * de uso determina los medios de pago disponibles para ese contexto, el
	 * parámetro exclude verifica si hayq ue excluir el contexto que viene como
	 * parámetro o se debe incluir junto con el tipo de contexto Ambos.
	 * 
	 * @param ctx
	 *            contexto
	 * @param tenderType
	 *            tipo de pago
	 * @param contextOfUse
	 *            contexto de uso del medio de pago
	 * @param exclude
	 *            true si se debe excluir el contexto de uso en la búsqueda o se
	 *            debe inclui junto con Ambos.
	 * @param trxName
	 *            transacción actual
	 * @param currencyId
	 *            moneda
	 * @return lista de medios de pago disponibles
	 */
	public static List<MPOSPaymentMedium> getAvailablePaymentMediums(Properties ctx, String tenderType, String contextOfUse, boolean exclude, String trxName, int currencyId){
		// Se buscan los medios de pago que sean válidos para la fecha actual.
		StringBuffer sql = new StringBuffer("SELECT * "
				+ "FROM C_POSPaymentMedium " + "WHERE AD_Client_ID = ? AND (AD_Org_ID = ? OR AD_Org_ID = 0) "
				+ "AND ?::Date BETWEEN DateFrom::Date AND DateTo::date " + "AND IsActive = 'Y' ");
		if(tenderType != null){
			sql.append(" AND (tendertype = '"+tenderType+"') ");
		}
		if(contextOfUse != null){
			if(exclude){
				sql.append(" AND (context <> '"+contextOfUse+"') ");
			}
			else{
				sql.append(" AND (context = '" + contextOfUse
						+ "' OR context = '" + MPOSPaymentMedium.CONTEXT_Both
						+ "') ");
			}
		}
		sql.append(" AND (C_Currency_ID = ?) ");
		sql.append("ORDER BY Name ASC");
		List<MPOSPaymentMedium> paymentMediums = new ArrayList<MPOSPaymentMedium>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = DB.prepareStatement(sql.toString(),trxName, true);
			int i = 1;
			pstmt.setInt(i++, Env.getAD_Client_ID(ctx));
			pstmt.setInt(i++, Env.getAD_Org_ID(ctx));
			pstmt.setTimestamp(i++, Env.getDate());
			pstmt.setInt(i++, currencyId);
			rs = pstmt.executeQuery();
			while(rs.next()){
				paymentMediums.add(new MPOSPaymentMedium(ctx, rs, trxName));
			}
		} catch (Exception e) {
			s_log.log(Level.SEVERE, "Error getting POS payment mediums.", e);
		} finally{
			try {
				if(pstmt != null)pstmt.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				s_log.log(Level.SEVERE, "Error getting POS payment mediums.", e2);
			}
		}
		return paymentMediums;
	}

	/**
	 * Obtiene los tender types disponibles para la fecha actual a partir de un
	 * contexto de uso. El parámetro exlude indica si hay que excluir el
	 * contexto parámetro o se debe incluir en la búsqueda, junto con la opción
	 * ambos. Si se excluye retorna los tender types distintos al contexto
	 * parámetro, caso contrario los que contienen ese contexto junto con el
	 * "Ambos".
	 * 
	 * @param ctx
	 *            contexto
	 * @param contextOfUse
	 *            contexto de uso
	 * @param exclude
	 *            true si se debe excluir el contexto, false cc
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return lista distinta de tendertypes disponibles a la feha actual y que
	 *         cumple el contexto de uso
	 */
	public static List<String> getAvailablesTenderTypesByContextOfUse(Properties ctx, String contextOfUse, boolean exclude, String trxName){
		// Obtengo los tipos de pago diferentes que contienen como contexto de uso TPV
		StringBuffer sql = new StringBuffer("SELECT distinct tendertype FROM C_POSPaymentMedium "
				+ "WHERE AD_Client_ID = ? AND (AD_Org_ID = ? OR AD_Org_ID = 0) AND ? BETWEEN DateFrom AND DateTo "
				+ "AND IsActive = 'Y' ");
		if(exclude){
			sql.append(" AND (context <> ?) ");
		}
		else{
			sql.append(" AND (context = ? OR context = '"+MPOSPaymentMedium.CONTEXT_Both+"') ");
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		Date today = new Date(System.currentTimeMillis());
		List<String> tenderTypes = new ArrayList<String>();
		try{
			 ps = DB.prepareStatement(sql.toString(), trxName);
			 int i = 1;
			 ps.setInt(i++, Env.getAD_Client_ID(ctx));
			 ps.setInt(i++, Env.getAD_Org_ID(ctx));
			 ps.setDate(i++, today);
			 ps.setString(i++, contextOfUse);
			 rs = ps.executeQuery();
			 while(rs.next()){
				 tenderTypes.add(rs.getString("tendertype"));
			 }
		} catch(Exception e){
			s_log.log(Level.SEVERE, "Error getting POS payment mediums.", e);
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				s_log.log(Level.SEVERE, "Error getting POS payment mediums.", e2);
			}
		}
		return tenderTypes;
	}
	
	/**
	 * Obtiene los tender types disponibles para la fecha actual a partir de un
	 * contexto de uso. El parámetro exlude indica si hay que excluir el
	 * contexto parámetro o se debe incluir en la búsqueda, junto con la opción
	 * ambos. Si se excluye retorna los tender types distintos al contexto
	 * parámetro, caso contrario los que contienen ese contexto junto con el
	 * "Ambos".
	 * 
	 * @param ctx
	 *            contexto
	 * @param contextOfUse
	 *            contexto de uso
	 * @param exclude
	 *            true si se debe excluir el contexto, false cc
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @param currencyId
	 *            moneda
	 * @return lista distinta de tendertypes disponibles a la feha actual y que
	 *         cumple el contexto de uso
	 */
	public static List<String> getAvailablesTenderTypesByContextOfUse(Properties ctx, String contextOfUse, boolean exclude, String trxName, int currencyId){
		// Obtengo los tipos de pago diferentes que contienen como contexto de uso TPV
		StringBuffer sql = new StringBuffer("SELECT distinct tendertype FROM C_POSPaymentMedium "
				+ "WHERE AD_Client_ID = ? AND (AD_Org_ID = ? OR AD_Org_ID = 0) AND ?::date BETWEEN DateFrom::Date AND DateTo::Date "
				+ "AND IsActive = 'Y' ");
		if(exclude){
			sql.append(" AND (context <> ?) ");
		}
		else{
			sql.append(" AND (context = ? OR context = '"+MPOSPaymentMedium.CONTEXT_Both+"') ");
		}
		sql.append(" AND (C_Currency_ID = ?) ");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> tenderTypes = new ArrayList<String>();
		try{
			 ps = DB.prepareStatement(sql.toString(), trxName, true);
			 int i = 1;
			 ps.setInt(i++, Env.getAD_Client_ID(ctx));
			 ps.setInt(i++, Env.getAD_Org_ID(ctx));
			 ps.setTimestamp(i++, Env.getDate());
			 ps.setString(i++, contextOfUse);
			 ps.setInt(i++, currencyId);
			 rs = ps.executeQuery();
			 while(rs.next()){
				 tenderTypes.add(rs.getString("tendertype"));
			 }
		} catch(Exception e){
			s_log.log(Level.SEVERE, "Error getting POS payment mediums.", e);
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				s_log.log(Level.SEVERE, "Error getting POS payment mediums.", e2);
			}
		}
		return tenderTypes;
	}

	/**
	 * Obtener los tender types como pares para poder utilizarlo donde deseen.
	 * El filtro de contexto de uso y el parámetro exclude determina que se debe
	 * excluir este dato o incluir junto con Ambos. Según el parámetro
	 * orderByName es posible ordenar la lista retornada por nombre en vez de
	 * por value como se realiza por defecto.
	 * 
	 * @param ctx
	 *            contexto
	 * @param contextOfUse
	 *            contexto de uso del medio de pago
	 * @param exclude
	 *            true si se debe excluir el contexto de uso en la query sql o
	 *            incluirla junto con el valor Ambos.
	 * @param orderByName
	 *            true si se debe ordenar por nombre de tender type o false si
	 *            es por value por defecto
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return lista de tender types a utilizar
	 */
	public static List<ValueNamePair> getTenderTypesByContextOfUse(Properties ctx, String contextOfUse, boolean exclude, boolean orderByName, String trxName){
		// Obtiene los tipos de pago de la referencia
		ValueNamePair[] allTenderTypes = MRefList.getList(
				MPOSPaymentMedium.TENDERTYPE_AD_Reference_ID, false, Env
						.getCtx());
		List<String> tenderTypes = getAvailablesTenderTypesByContextOfUse(ctx,
				contextOfUse, exclude, trxName);
		List<ValueNamePair> realTenderTypes = new ArrayList<ValueNamePair>();
		// Iterar por todos los tender types y filtrar 
		for (int i = 0; i < allTenderTypes.length; i++) {
			// Si existe el tendertype dentro de los tender types configurados
			// entonces lo agrego a la lista a retornar
			if(tenderTypes.contains(allTenderTypes[i].getValue())){
				realTenderTypes.add(allTenderTypes[i]);
			}
		}
		// ordeno por nombre si el parámetro me lo permite
		if(orderByName){
			// Se ordenan por nombre (por defecto vienen ordenados por value)
			Collections.sort(realTenderTypes, new Comparator<ValueNamePair>() {
				@Override
				public int compare(ValueNamePair o1, ValueNamePair o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
		}
		return realTenderTypes;
	}
	
	/**
	 * Obtener los tender types como pares para poder utilizarlo donde deseen.
	 * El filtro de contexto de uso y el parámetro exclude determina que se debe
	 * excluir este dato o incluir junto con Ambos. Según el parámetro
	 * orderByName es posible ordenar la lista retornada por nombre en vez de
	 * por value como se realiza por defecto.
	 * 
	 * @param ctx
	 *            contexto
	 * @param contextOfUse
	 *            contexto de uso del medio de pago
	 * @param exclude
	 *            true si se debe excluir el contexto de uso en la query sql o
	 *            incluirla junto con el valor Ambos.
	 * @param orderByName
	 *            true si se debe ordenar por nombre de tender type o false si
	 *            es por value por defecto
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @param currencyID
	 *            moneda del medio de pago
	 * @return lista de tender types a utilizar
	 */
	public static List<ValueNamePair> getTenderTypesByContextOfUse(Properties ctx, String contextOfUse, boolean exclude, boolean orderByName, String trxName, int currencyID){
		// Obtiene los tipos de pago de la referencia
		ValueNamePair[] allTenderTypes = MRefList.getList(
				MPOSPaymentMedium.TENDERTYPE_AD_Reference_ID, false, Env
						.getCtx());
		List<String> tenderTypes = getAvailablesTenderTypesByContextOfUse(ctx,
				contextOfUse, exclude, trxName, currencyID);
		List<ValueNamePair> realTenderTypes = new ArrayList<ValueNamePair>();
		// Iterar por todos los tender types y filtrar 
		for (int i = 0; i < allTenderTypes.length; i++) {
			// Si existe el tendertype dentro de los tender types configurados
			// entonces lo agrego a la lista a retornar
			if(tenderTypes.contains(allTenderTypes[i].getValue())){
				realTenderTypes.add(allTenderTypes[i]);
			}
		}
		// ordeno por nombre si el parámetro me lo permite
		if(orderByName){
			// Se ordenan por nombre (por defecto vienen ordenados por value)
			Collections.sort(realTenderTypes, new Comparator<ValueNamePair>() {
				@Override
				public int compare(ValueNamePair o1, ValueNamePair o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
		}
		return realTenderTypes;
	}
	
	
	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param C_POSPaymentMedium_ID
	 * @param trxName
	 */
	public MPOSPaymentMedium(Properties ctx, int C_POSPaymentMedium_ID,
			String trxName) {
		super(ctx, C_POSPaymentMedium_ID, trxName);
		// Valores por defecto
		if (C_POSPaymentMedium_ID == 0) {
			setTenderType(TENDERTYPE_Cash);
			setM_EntidadFinanciera_ID(0);
			setC_Currency_ID(Env.getContextAsInt(ctx, "$C_Currency_ID"));
			setCheckDeadLine(null);
			setDateFrom(Env.getDate());
			setBank(null);
		}
	}

	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MPOSPaymentMedium(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	public void setM_EntidadFinanciera_ID(int M_EntidadFinanciera_ID) {
		// Bypass: La columna M_EntidadFinanciera_ID está marcada como entrada obligatoria
		// pero solo para restringir la interfaz gráfica (en la BD puede ser NULL), con
		// lo cual si el ID a asignar es 0 en realidad se guarda como NULL.
		// Si no es 0, se utiliza el setter de la superclase que se encarga de realizar
		// las validaciones según los metadatos de la columna.
		if (M_EntidadFinanciera_ID == 0) {
			set_ValueNoCheck("M_EntidadFinanciera_ID", null);
		} else {
			super.setM_EntidadFinanciera_ID(M_EntidadFinanciera_ID);
		}
	}

	@Override
	public void setCheckDeadLine(String CheckDeadLine) {
		// Bypass: La columna CheckDeadLine está marcada como entrada obligatoria
		// pero solo para restringir la interfaz gráfica (en la BD puede ser NULL)
		set_ValueNoCheck("CheckDeadLine", null);
	}
	
	@Override
	public void setBank(String bank) {
		set_ValueNoCheck("Bank", bank);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// La fecha final de validez no puede ser anterior a la fecha
		// inicial.
		if (getDateTo().compareTo(getDateFrom()) < 0) {
			log.saveError("SaveError", Msg.translate(getCtx(), "InvalidDateRange"));
			return false;
		}
		
		// No es posible configurar el tipo de medio de pago Retención o Cobro
		// Adelantado para el TPV porque por ahora no se encuentra implementado.
		// Cuando se implemente alguno de ellos en el tpv entonces modificar
		// esta validación 
		if ((TENDERTYPE_AdvanceReceipt.equals(getTenderType()) || TENDERTYPE_Retencion
				.equals(getTenderType()))
				&& !CONTEXT_CustomerReceiptsOnly.equals(getContext())) {
			log.saveError("SaveError", Msg.translate(getCtx(), "PaymentMediumPOSContextNotAllowed"));
			return false;
		}
		
		// No es posible configurar el tipo de medio de pago Crédito para
		// Recibos de Cliente
		if (TENDERTYPE_Credit.equals(getTenderType())
				&& !CONTEXT_POSOnly.equals(getContext())) {
			log.saveError("SaveError", Msg.translate(getCtx(), "PaymentMediumCRContextNotAllowed"));
			return false;
		}
		
		// El MP Tarjeta de Crédito requiere la Entidad Financiera.
		if (TENDERTYPE_CreditCard.equals(getTenderType())) {
			if(getM_EntidadFinanciera_ID() == 0){
				log.saveError("SaveError", Msg.translate(getCtx(), "CreditCardNeedEntidadFinanciera"));
				return false;
			}
			// La entidad financiera tiene que tener la misma organización que
			// el medio de cobro o *
			MEntidadFinanciera ef = new MEntidadFinanciera(getCtx(),
					getM_EntidadFinanciera_ID(), get_TrxName());
			if (ef.getAD_Org_ID() != getAD_Org_ID() && ef.getAD_Org_ID() != 0) {
				log.saveError("SaveError",
						Msg.getMsg(getCtx(), "EntidadFinancieraOrgNotCompatibleWithPM"));
				return false;
			}
		}
		
		// Validaciones a nivel de cheque
		if (TENDERTYPE_Check.equals(getTenderType())) {
			// El MP Cheque requiere el plazo de cobro
			if(getCheckDeadLine() == null){
				log.saveError("FillMandatory", Msg.translate(getCtx(), "CheckDeadLine"));
				return false;
			}
			// Si se deben validar los plazos anteriores entonces verifico que
			// esté bien formado el rango
			if(isValidateBeforeCheckDeadLines()){
				// El plazo inicial del rango no puede ser igual o mayor al plazo del cheque
				if (Integer.parseInt(getBeforeCheckDeadLineFrom()) >= Integer
						.parseInt(getCheckDeadLine())) {
					log.saveError("InitialValueGreaterThanCheckDeadLine", "");
					return false;
				}
				// El valor final no debe ser mayor al valor inicial
				if (!Util.isEmpty(getBeforeCheckDeadLineTo(), true)
						&& Integer.parseInt(getBeforeCheckDeadLineFrom()) > Integer
								.parseInt(getBeforeCheckDeadLineTo())) {
					log.saveError("InitialValueGreaterThanEndValue", "");
					return false;
				}
			}
			// Seteo a null el valor final
			if (!isValidateBeforeCheckDeadLines()
					|| (isValidateBeforeCheckDeadLines() && Util.isEmpty(
							getBeforeCheckDeadLineTo(), true))) {
				setBeforeCheckDeadLineTo(null);
			}
		}
		
		// Borrado de valores que no se corresponden con el tipo de medio de pago
		
		// Entidad Financiera (MP Tarjeta de Crédito)
		if (!TENDERTYPE_CreditCard.equals(getTenderType())) {
			setM_EntidadFinanciera_ID(0);
		}
		
		// Plazo de Cobro del Cheque (MP Cheque)
		if (!TENDERTYPE_Check.equals(getTenderType())) {
			setCheckDeadLine(null);
			setValidateBeforeCheckDeadLines(false);
		}

		// Banco (MP Cheque & Tarjeta)
		if (!TENDERTYPE_Check.equals(getTenderType()) 
				&& !TENDERTYPE_CreditCard.equals(getTenderType())) {
			setBank(null);
		}
		
		// Retenciones
		if (TENDERTYPE_Retencion.equals(getTenderType())) {
			setM_DiscountSchema_ID(0);
		}
				
		return true;
	}
	
	@Override
	public String toString(){
		return getName();
	}
}
