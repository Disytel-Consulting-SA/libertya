package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descuento aplicado a un Documento (Pedido, Factura)
 */
public class MDocumentDiscount extends X_C_DocumentDiscount {

	/** Logger para métodos static */
	private static CLogger s_log = CLogger.getCLogger(MDocumentDiscount.class);
	/** Discriminación de descuentos por tasa de impuesto */
	private List<MDocumentDiscount> discountsByTax = null;
	
	/**
	 * Busca en la BD y devuelve una lista descuentos asociados a una factura
	 * determinada. Solo devuelve los descuentos totalizados, no aquellos que sean
	 * discriminación por tasa de impuesto.
	 * 
	 * @param invoiceID
	 *            ID de factura
	 * @param ctx
	 *            Contexto para instanciación de objetos del modelo
	 * @param trxName
	 *            Transacción de BD
	 * @return Lista con los descuentos asociados, o lista vacía si la factura
	 *         no tiene descuentos
	 */
	public static List<MDocumentDiscount> getOfInvoice(int invoiceID, Properties ctx, String trxName) {
		return getOfInvoice(true, invoiceID, ctx, trxName);
	}

	/**
	 * Busca en la BD y devuelve una lista descuentos asociados a una factura
	 * determinada. Solo devuelve los descuentos totalizados, no aquellos que
	 * sean discriminación por tasa de impuesto, si es que el parámetro boolean
	 * así lo requiera.
	 * 
	 * @param onlyTotalized
	 *            true si se deben devolver sólo los totalizados
	 * @param invoiceID
	 *            ID de factura
	 * @param ctx
	 *            Contexto para instanciación de objetos del modelo
	 * @param trxName
	 *            Transacción de BD
	 * @return Lista con los descuentos asociados, o lista vacía si la factura
	 *         no tiene descuentos
	 */
	public static List<MDocumentDiscount> getOfInvoice(boolean onlyTotalized, int invoiceID, Properties ctx, String trxName) {
		return get("C_Invoice_ID = ?"
				+ (onlyTotalized ? " AND C_DocumentDiscount_Parent_ID IS NULL "
						: ""), new Object[] { invoiceID }, ctx, trxName);
	}

	/**
	 * Busca en la BD y devuelve una lista descuentos asociados a una factura
	 * determinada. Solo devuelve los descuentos totalizados, no aquellos que
	 * sean discriminación por tasa de impuesto, si es que el parámetro boolean
	 * así lo requiera.
	 * 
	 * @param onlyTotalized
	 *            true si se deben devolver sólo los totalizados
	 * @param invoiceID
	 *            ID de factura
	 * @param orderBy
	 *            cláusula de ordenación
	 * @param ctx
	 *            Contexto para instanciación de objetos del modelo
	 * @param trxName
	 *            Transacción de BD
	 * @return Lista con los descuentos asociados, o lista vacía si la factura
	 *         no tiene descuentos
	 */
	public static List<MDocumentDiscount> getOfInvoice(boolean onlyTotalized, int invoiceID, String orderBy, Properties ctx, String trxName) {
		return get("C_Invoice_ID = ?"
				+ (onlyTotalized ? " AND C_DocumentDiscount_Parent_ID IS NULL "
						: ""), new Object[] { invoiceID }, orderBy, ctx, trxName);
	}
	
	/**
	 * Busca en la BD y devuelve una lista descuentos asociados a un pedido
	 * determinad. Solo devuelve los descuentos totalizados, no aquellos que
	 * sean discriminación por tasa de impuesto.
	 * 
	 * @param orderID
	 *            ID del pedido
	 * @param ctx
	 *            Contexto para instanciación de objetos del modelo
	 * @param trxName
	 *            Transacción de BD
	 * @return Lista con los descuentos asociados, o lista vacía si la factura
	 *         no tiene descuentos
	 */
	public static List<MDocumentDiscount> getOfOrder(int orderID, Properties ctx, String trxName) {
		return get("C_Order_ID = ? AND C_DocumentDiscount_Parent_ID IS NULL",
				new Object[] { orderID }, ctx, trxName);
	}

	/**
	 * Busca en la BD y devuelve true si existe al menos un descuento aplicado
	 * actualmente al documento y tipo de descuento parámetros.
	 * 
	 * @param documentColumnName
	 *            nombre de la columna dentro de la tabla que hace referencia al
	 *            documento en cuestión 
	 * @param documentID
	 *            ID del documento en el nombre de la columna parámetro
	 * @param discountKind
	 *            tipo de descuento aplicado. Si se ingresa null entonces no
	 *            filtra por descuento
	 * @param ctx
	 *            Contexto para instanciación de objetos del modelo
	 * @param trxName
	 *            Transacción de BD
	 * @return true si existe al menos un descuento a ese documento con el tipo de
	 *         descuento correspondiente, falso caso contrario
	 */
	public static boolean existDocumentDiscountAlreadyApplied(String documentColumnName, int documentID, String discountKind, Properties ctx, String trxName) {
		StringBuffer sql = new StringBuffer("SELECT count(1) as cant FROM "
				+ Table_Name + " WHERE "+documentColumnName+" = " + documentID
				+ " AND C_DocumentDiscount_Parent_ID IS NULL");
		if(!Util.isEmpty(discountKind)){
			sql.append(" AND discountkind = '"+discountKind+"' ");
		}
		return DB.getSQLValue(trxName, sql.toString()) > 0;
	}

	/**
	 * Devuelve una lista de descuentos a partir de un filtro
	 * 
	 * @param filter
	 *            Filtro de la consulta WHERE
	 * @param parameters
	 *            Valores de los parámetros que contiene el filtro.
	 * @param ctx
	 *            Contexto para instanciación de objetos del modelo
	 * @return Lista con los descuentos encontrados, o lista vacía si no se
	 *         encontraron descuentos con el filtro indicado
	 */
	public static List<MDocumentDiscount> get(String filter, Object[] parameters, Properties ctx, String trxName) {
		return get(filter, parameters, "DiscountKind, TaxRate ASC", ctx, trxName);
	}

	/**
	 * Devuelve una lista de descuentos a partir de un filtro
	 * 
	 * @param filter
	 *            Filtro de la consulta WHERE
	 * @param parameters
	 *            Valores de los parámetros que contiene el filtro.
	 * @param orderBy
	 *            cláusula de ordenación
	 * @param ctx
	 *            Contexto para instanciación de objetos del modelo
	 * @return Lista con los descuentos encontrados, o lista vacía si no se
	 *         encontraron descuentos con el filtro indicado
	 */
	public static List<MDocumentDiscount> get(String filter, Object[] parameters, String orderBy, Properties ctx, String trxName) {
		List<MDocumentDiscount> list = new ArrayList<MDocumentDiscount>();

		String sql =
			 "SELECT * FROM C_DocumentDiscount " +
			 "WHERE "+ filter +
			" ORDER BY "+ orderBy;
		
		PreparedStatement pstmt = null;
		ResultSet rs            = null;
		
		try {
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			if (parameters.length > 0) {
				int i = 1;
				for (Object paramValue : parameters) {
					pstmt.setObject(i++, paramValue);
				}
			}
			
			rs = pstmt.executeQuery();
			MDocumentDiscount documentDiscount = null;
			while (rs.next()) {
				documentDiscount = new MDocumentDiscount(ctx, rs, trxName);
				list.add(documentDiscount);
			}
		} catch (SQLException e) {
			s_log.log(Level.SEVERE,
					"Error getting document discounts from DB. Filter= " + filter, e); 
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
		return list;
	}
	
	/**
	 * Constructor de la clase.
	 * @param ctx Contexto de la aplicación
	 * @param C_DocumentDiscount_ID ID del registro
	 * @param trxName Transacción de la BD
	 */
	public MDocumentDiscount(Properties ctx, int C_DocumentDiscount_ID,
			String trxName) {
		super(ctx, C_DocumentDiscount_ID, trxName);
		if (C_DocumentDiscount_ID == 0) {
			setDiscountAmt(BigDecimal.ZERO);
			setDiscountBaseAmt(BigDecimal.ZERO);
		}
	}

	/**
	 * Constructor de la clase.
	 * @param ctx Contexto de la aplicación
	 * @param rs ResultSet con el contenido del registro
	 * @param trxName Transacción de la BD
	 */
	public MDocumentDiscount(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Crea un descuento de documento que es una especificación por tasa de
	 * impuesto de un descuento padre.
	 * 
	 * @param parentDiscount
	 *            Descuento padre que contiene al descuento que se va a crear.
	 * @param taxRate
	 *            Tasa de impuesto
	 */
	public MDocumentDiscount(MDocumentDiscount parentDiscount, BigDecimal taxRate) {
		this(parentDiscount.getCtx(), 0, parentDiscount.get_TrxName());
		// Tasa
		setTaxRate(taxRate);
		// Referencia al padre
		setC_DocumentDiscount_Parent_ID(parentDiscount.getC_DocumentDiscount_ID());
		// Resto de propiedades iguales a las del padre
		setDiscountKind(parentDiscount.getDiscountKind());
		setDescription(parentDiscount.getDescription());
		setM_DiscountSchema_ID(parentDiscount.getM_DiscountSchema_ID());
		setCumulativeLevel(parentDiscount.getCumulativeLevel());
		setDiscountApplication(parentDiscount.getDiscountApplication());
		setC_Order_ID(parentDiscount.getC_Order_ID());
		setC_Invoice_ID(parentDiscount.getC_Invoice_ID());
		setClientOrg(parentDiscount);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// El importe base del descuento debe ser mayor que cero
		if (getDiscountBaseAmt().compareTo(BigDecimal.ZERO) <= 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(),
					"ValueMustBeGreatherThanZero", new Object[] { Msg
							.translate(getCtx(), "DiscountBaseAmt") }));
			return false;
		}
		
		// El importe del descuento debe ser distinto que cero
		if (getDiscountAmt().compareTo(BigDecimal.ZERO) == 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(),
					"ValueMustBeDifferentThanZero", new Object[] { Msg
							.translate(getCtx(), "DiscountAmt") }));
			return false;
		}

		// Obtiene el nivel desde el esquema
		if (getCumulativeLevel() == null) {
			String cumulativeLevel = DB.getSQLValueString(get_TrxName(), 
				"SELECT CumulativeLevel FROM M_DiscountSchema WHERE M_DiscountSchema_ID = ?", 
				getM_DiscountSchema_ID()
			);
			setCumulativeLevel(cumulativeLevel);
		}

		return true;
	}

	/**
	 * @return Indica si este descuento de documento es un descuento para una
	 *         tasa de impuesto determinada. De ser así, mediante el método
	 *         {@link #getTaxRate()} se puede obtener la tasa de impuesto
	 *         afectada por este descuento.
	 */
	public boolean isByTax() {
		return getC_DocumentDiscount_Parent_ID() > 0 && getTaxRate() != null
				&& getTaxRate().compareTo(BigDecimal.ZERO) > 0;
	}

	/**
	 * @return Indica si la aplicación de este descuento es como bonus.
	 */
	public boolean isBonusApplication() {
		return DISCOUNTAPPLICATION_Bonus.equals(getDiscountApplication());
	}
	
	/**
	 * @return Indica si es un descuento general manual.
	 */
	public boolean isManualGeneralDiscountKind() {
		return MDocumentDiscount.DISCOUNTKIND_ManualGeneralDiscount
				.equals(getDiscountKind());
	}

	/**
	 * @return Una lista con el detalle de descuentos discriminados por tasa de
	 *         impuesto. La suma de los importes de los descuentos devueltos por
	 *         este método es igual a importe de descuento de este descuento
	 */
	public List<MDocumentDiscount> getDiscountsByTax() {
		if (discountsByTax == null) {
			discountsByTax = get("C_DocumentDiscount_Parent_ID = ? AND TaxRate IS NOT NULL",
					new Object[] { getC_DocumentDiscount_ID() }, getCtx(), get_TrxName());
		}
		return discountsByTax;
	}
}
