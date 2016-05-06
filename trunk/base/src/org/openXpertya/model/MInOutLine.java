/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */

package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayUtil;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 * 
 * 
 * @version 2.2, 12.10.07
 * @author Equipo de Desarrollo de openXpertya
 */

public class MInOutLine extends X_M_InOutLine {

	// flag especial para evitar validaciones costosas computacionalmente,
	// las cuales no son necesario realizar en caso de ser parte de una venta
	// TPV
	protected boolean isTPVInstance = false;
	
	protected MInvoiceLine invoiceLine = null;
	protected MOrderLine orderLine = null;

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param ctx
	 * @param C_OrderLine_ID
	 * @param where
	 * @param trxName
	 * 
	 * @return
	 */

	public static MInOutLine[] getOfOrderLine(Properties ctx,
			int C_OrderLine_ID, String where, String trxName) {
		ArrayList list = new ArrayList();
		s_log.fine("En getOfOrderLine" + "where= " + where);
		String sql = "SELECT * FROM M_InOutLine WHERE C_OrderLine_ID=? ";

		if ((where != null) && (where.length() > 0)) {
			sql += " AND " + where;
		}

		PreparedStatement pstmt = null;

		try {
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, C_OrderLine_ID);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(new MInOutLine(ctx, rs, trxName));
			}

			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			s_log.log(Level.SEVERE, "getOfOrderLine", e);
		}

		try {
			if (pstmt != null) {
				pstmt.close();
			}

			pstmt = null;
		} catch (Exception e) {
			pstmt = null;
		}

		MInOutLine[] retValue = new MInOutLine[list.size()];

		list.toArray(retValue);

		return retValue;
	} // getOfOrderLine

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param ctx
	 * @param C_OrderLine_ID
	 * @param trxName
	 * 
	 * @return
	 */

	public static MInOutLine[] get(Properties ctx, int C_OrderLine_ID,
			String trxName) {
		ArrayList list = new ArrayList();
		s_log.fine("En MInOutLine.get");
		String sql = "SELECT * FROM M_InOutLine WHERE C_OrderLine_ID=?";
		PreparedStatement pstmt = null;

		try {
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, C_OrderLine_ID);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(new MInOutLine(ctx, rs, trxName));
			}

			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			s_log.log(Level.SEVERE, "get", e);
		}

		try {
			if (pstmt != null) {
				pstmt.close();
			}

			pstmt = null;
		} catch (Exception e) {
			pstmt = null;
		}

		MInOutLine[] retValue = new MInOutLine[list.size()];

		list.toArray(retValue);

		return retValue;
	} // getOfOrderLine

	/** Descripción de Campos */

	private static CLogger s_log = CLogger.getCLogger(MInOutLine.class);

	/**
	 * Constructor de la clase ...
	 * 
	 * 
	 * @param ctx
	 * @param M_InOutLine_ID
	 * @param trxName
	 */

	public MInOutLine(Properties ctx, int M_InOutLine_ID, String trxName) {
		super(ctx, M_InOutLine_ID, trxName);

		if (M_InOutLine_ID == 0) {

			// setLine (0);
			// setM_Locator_ID (0);
			// setC_UOM_ID (0);
			// setM_Product_ID (0);

			setM_AttributeSetInstance_ID(0);

			// setMovementQty (Env.ZERO);

			setConfirmedQty(Env.ZERO);
			setPickedQty(Env.ZERO);
			setScrappedQty(Env.ZERO);
			setTargetQty(Env.ZERO);
			setIsInvoiced(false);
			setIsDescription(false);
		}
	} // MInOutLine

	/**
	 * Constructor de la clase ...
	 * 
	 * 
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */

	public MInOutLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	} // MInOutLine

	/**
	 * Constructor de la clase ...
	 * 
	 * 
	 * @param inout
	 */

	public MInOutLine(MInOut inout) {
		this(inout.getCtx(), 0, inout.get_TrxName());
		setClientOrg(inout);
		setM_InOut_ID(inout.getM_InOut_ID());
		setM_Warehouse_ID(inout.getM_Warehouse_ID());
	} // MInOutLine

	/** Descripción de Campos */

	private MProduct m_product = null;

	/** Descripción de Campos */

	private int m_M_Warehouse_ID = 0;

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param oLine
	 * @param M_Locator_ID
	 * @param Qty
	 */

	public void setOrderLine(MOrderLine oLine, int M_Locator_ID, BigDecimal Qty) {
		setC_OrderLine_ID(oLine.getC_OrderLine_ID());
		setLine(oLine.getLine());
		setC_UOM_ID(oLine.getC_UOM_ID());

		MProduct product = oLine.getProduct();

		if (product == null) {
			set_ValueNoCheck("M_Product_ID", null);
			set_ValueNoCheck("M_AttributeSetInstance_ID", null);
			set_ValueNoCheck("M_Locator_ID", null);
		} else {
			setM_Product_ID(oLine.getM_Product_ID());
			setM_AttributeSetInstance_ID(oLine.getM_AttributeSetInstance_ID());

			//

			if (product.isItem()) {
				if (M_Locator_ID == 0) {
					setM_Locator_ID(Qty); // requires warehouse, product, asi
				} else {
					setM_Locator_ID(M_Locator_ID);
				}
			} else {
				set_ValueNoCheck("M_Locator_ID", null);
			}
		}

		setC_Charge_ID(oLine.getC_Charge_ID());
		setDescription(oLine.getDescription());
		setIsDescription(oLine.isDescription());
		setC_Project_ID(oLine.getC_Project_ID());
	} // setOrderLine

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param iLine
	 * @param M_Locator_ID
	 * @param Qty
	 */

	public void setInvoiceLine(MInvoiceLine iLine, int M_Locator_ID,
			BigDecimal Qty) {
		setC_OrderLine_ID(iLine.getC_OrderLine_ID());
		setLine(iLine.getLine());
		setC_UOM_ID(iLine.getC_UOM_ID());

		int M_Product_ID = iLine.getM_Product_ID();

		if (M_Product_ID == 0) {
			set_ValueNoCheck("M_Product_ID", null);
			set_ValueNoCheck("M_Locator_ID", null);
			set_ValueNoCheck("M_AttributeSetInstance_ID", null);
		} else {
			setM_Product_ID(M_Product_ID);
			setM_AttributeSetInstance_ID(iLine.getM_AttributeSetInstance_ID());

			if (M_Locator_ID == 0) {
				setM_Locator_ID(Qty); // requires warehouse, product, asi
			} else {
				setM_Locator_ID(M_Locator_ID);
			}
		}

		setC_Charge_ID(iLine.getC_Charge_ID());
		setDescription(iLine.getDescription());
		setIsDescription(iLine.isDescription());
		setC_Project_ID(iLine.getC_Project_ID());
	} // setOrderLine

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public int getM_Warehouse_ID() {
		if (m_M_Warehouse_ID == 0) {
			MInOut io = new MInOut(getCtx(), getM_InOut_ID(), get_TrxName());

			m_M_Warehouse_ID = io.getM_Warehouse_ID();
			log.fine("Had to query header");
		}

		return m_M_Warehouse_ID;
	} // getM_Warehouse_ID

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param warehouse_ID
	 */

	public void setM_Warehouse_ID(int warehouse_ID) {
		m_M_Warehouse_ID = warehouse_ID;
	} // setM_Warehouse_ID

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param Qty
	 */

	public void setM_Locator_ID(BigDecimal Qty) {

		// Locator esatblished

		if (getM_Locator_ID() != 0) {
			return;
		}

		// No Product

		if (getM_Product_ID() == 0) {
			set_ValueNoCheck("M_Locator_ID", null);

			return;
		}

		// Get existing Location

		int M_Locator_ID = MStorage.getM_Locator_ID(getM_Warehouse_ID(),
				getM_Product_ID(), getM_AttributeSetInstance_ID(), Qty,
				get_TrxName());

		// Get default Location

		if (M_Locator_ID == 0) {
			MWarehouse wh = MWarehouse.get(getCtx(), getM_Warehouse_ID());

			M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
		}

		setM_Locator_ID(M_Locator_ID);
	} // setM_Locator_ID

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param Qty
	 */

	public void setQty(BigDecimal Qty) {
		setQtyEntered(Qty);
		setMovementQty(Qty);
	} // setQtyInvoiced

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public MProduct getProduct() {
		if ((m_product == null) && (getM_Product_ID() != 0)) {
			m_product = MProduct.get(getCtx(), getM_Product_ID());
		}

		return m_product;
	} // getProduct

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param product
	 */

	public void setProduct(MProduct product) {
		m_product = product;

		if (m_product != null) {
			setM_Product_ID(m_product.getM_Product_ID());
			setC_UOM_ID(m_product.getC_UOM_ID());
		} else {
			setM_Product_ID(0);
			setC_UOM_ID(0);
		}

		setM_AttributeSetInstance_ID(0);
	} // setProduct

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param M_Product_ID
	 * @param setUOM
	 */

	public void setM_Product_ID(int M_Product_ID, boolean setUOM) {
		if (setUOM) {
			setProduct(MProduct.get(getCtx(), M_Product_ID));
		} else {
			super.setM_Product_ID(M_Product_ID);
		}

		setM_AttributeSetInstance_ID(0);
	} // setM_Product_ID

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param M_Product_ID
	 * @param C_UOM_ID
	 */

	public void setM_Product_ID(int M_Product_ID, int C_UOM_ID) {
		if (M_Product_ID != 0) {
			super.setM_Product_ID(M_Product_ID);
		}

		super.setC_UOM_ID(C_UOM_ID);
		setM_AttributeSetInstance_ID(0);
		m_product = null;
	} // setM_Product_ID

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param description
	 */

	public void addDescription(String description) {
		String desc = getDescription();

		if (desc == null) {
			setDescription(description);
		} else {
			setDescription(desc + " | " + description);
		}
	} // addDescription

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param M_AttributeSetInstance_ID
	 */

	public void setM_AttributeSetInstance_ID(int M_AttributeSetInstance_ID) {
		if (M_AttributeSetInstance_ID == 0) { // 0 is valid ID
			set_Value("M_AttributeSetInstance_ID", new Integer(0));
		} else {
			super.setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
		}
	} // setM_AttributeSetInstance_ID

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param newRecord
	 * 
	 * @return
	 */

	protected boolean beforeSave(boolean newRecord) {
		log.fine("beforeSave");

		// Get Line No

		if (getLine() == 0) {
			String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM M_InOutLine WHERE M_InOut_ID=?";
			int ii = DB.getSQLValue(get_TrxName(), sql, getM_InOut_ID());

			setLine(ii);
		}

		// UOM

		if (getC_UOM_ID() == 0) {
			setC_UOM_ID(Env.getContextAsInt(getCtx(), "#C_UOM_ID"));
		}

		if (getC_UOM_ID() == 0) {
			int C_UOM_ID = MUOM.getDefault_UOM_ID(getCtx());

			if (C_UOM_ID > 0) {
				setC_UOM_ID(C_UOM_ID);
			}
		}

		// Order Line

		MInOut inout = new MInOut(getCtx(), getM_InOut_ID(), get_TrxName());

		// Tomar el warehouse de la cabecera a fin de evitar releerlo al completar
		// (lo que obliga a instanciar la cabecera todas las veces)
		setM_Warehouse_ID(inout.getM_Warehouse_ID());
		
		// Shipment Header - Order mandatory

		if (inout.isSOTrx() && inout.getC_Order_ID() == 0
				&& !isProductionMovement(inout.getMovementType())) {
			log.saveError("DocumentNoOrder", "");
			return false;
		}

		if (getC_OrderLine_ID() == 0 && inout.getC_Order_ID() != 0
				&& !isProductionMovement(inout.getMovementType())) {
			if (inout.isSOTrx()) {
				// log.saveError( "FillMandatory",Msg.translate(
				// getCtx(),"C_Order_ID" ));
				log.saveError("DocumentLineNoOrderLine", "");
				return false;
			}
		}

		// Si la linea no tiene artículo seteado pero tiene referenciado una linea de pedido, tomar el articulo de la linea de pedido
		if (getM_Product_ID() == 0 && getC_OrderLine_ID() > 0) {
			X_C_OrderLine anOrderLine = new X_C_OrderLine(getCtx(), getC_OrderLine_ID(), get_TrxName());
			if (anOrderLine.getM_Product_ID() > 0)
				setM_Product_ID(anOrderLine.getM_Product_ID());
		}
		
		if (inout.isSOTrx() && !isProductionMovement(inout.getMovementType())) {
			MOrderLine orderLine = new MOrderLine(getCtx(),
					getC_OrderLine_ID(), get_TrxName());
			StringBuffer fields = new StringBuffer();
			if (getC_UOM_ID() != orderLine.getC_UOM_ID()) {
				fields.append(Msg.getMsg(getCtx(), "C_UOM_ID"));
				fields.append(",");
			}
			if (getM_Product_ID() != orderLine.getM_Product_ID()) {
				fields.append(Msg.getMsg(getCtx(), "M_Product_ID"));
				fields.append(",");
			}
			// if(getM_AttributeSetInstance_ID() !=
			// orderLine.getM_AttributeSetInstance_ID()){
			// fields.append("@M_AttributeSetInstance_ID@");
			// }
			if (fields.length() > 0) {
				String fieldsTranslate = fields.substring(0,
						fields.lastIndexOf(","));
				log.saveError("DocumentLineNotEqualsOrderLine",
						Msg.translate(getCtx(), "AD_Field_ID") + " : "
								+ fieldsTranslate);
				return false;
			}
		}
		/*
		 * if (getM_AttributeSetInstance_ID() == 0 &&
		 * shouldSetAttrSetInstance(inout)) { log.saveError("FillMandatory",
		 * Msg.translate( getCtx(),"M_AttributeSetInstance_ID" )); return false;
		 * }
		 */

		// Si el artículo es bien de uso entonces debe estar relacionado a una
		// línea de pedido/factura
		MProduct product = Util.isEmpty(getM_Product_ID(), true) ? null
				: MProduct.get(getCtx(), getM_Product_ID());
		if (!isTPVInstance && !isProductionMovement(inout.getMovementType())
				&& product != null 
				&& product.getProductType() != null
				&& product.getProductType().equals(MProduct.PRODUCTTYPE_Assets)
				&& Util.isEmpty(getC_OrderLine_ID(), true) 
				&& Util.isEmpty(getC_InvoiceLine_ID(), true)) {
			log.saveError("DocLineWithAssetProductWithoutDocLine", "");
			return false;
		}
		
		
		/*
		 * Añade una comprobacion en el metodo beforSave de las lineas de pedido
		 * y albaran, para que en las transacciones de venta, no se pueda
		 * guardar seleccionar un conjunto de atributos cuyo stockage sea menor
		 * que el indicado en la linea.
		 */
		if (inout.isSOTrx() && getM_AttributeSetInstance_ID() != 0
				&& inout.getMovementType().endsWith("-")) {
			MStorage storage = MStorage.get(Env.getCtx(), getM_Locator_ID(),
					getM_Product_ID(), getM_AttributeSetInstance_ID(),
					get_TrxName());
			// Added by Matías Cap - Disytel
			// Obtengo la cantidad reservada del pedido ya que se puede dar el
			// caso de que estamos creando un pedido, por ejemplo en TPV, lo que
			// realiza el pedido es reservar stock y cuando estamos en este
			// punto, se decrementa la cantidad reservada del storage, los
			// cuales incluyen los del pedido actual y a la hora de remitir se
			// debería sumar la cantidad reservada del pedido sino puede ser que
			// no nos quede stock al restar la cantidad reservada del stock en
			// el storage (que incluye el pedido actual)

			// TODO Queda ver porqué motivo se realiza esta validación aca, al
			// completar se hace?
			BigDecimal orderLineQtyReserved = BigDecimal.ZERO;
			if (!Util.isEmpty(getC_OrderLine_ID(), true)) {
				orderLineQtyReserved = DB
						.getSQLValueBD(
								get_TrxName(),
								"SELECT qtyordered-qtydelivered-qtytransferred FROM c_orderline WHERE c_orderline_id = ?",
								getC_OrderLine_ID());
			}
			if (storage.getQtyOnHand().subtract(storage.getQtyReserved())
					.add(orderLineQtyReserved).compareTo(getQtyEntered()) < 0) {
				log.saveError("NotEnoughStocked", "");
				return false;
			}
		}

		/* Si el project no está seteado, tomar el de la cabecera */
		if (getC_Project_ID() == 0)
			setC_Project_ID(inout.getC_Project_ID());

		// Validación para no cargar dos líneas de remito
		// asignadas a la misma línea de pedido (Se valida por cantidad del artículo, y no por número de líneas)
		if (!isTPVInstance && newRecord && inout.isSOTrx()
				&& !isProductionMovement(inout.getMovementType())) {
			String sql = "SELECT coalesce(sum(movementqty), 0.00) "
					+ "FROM m_inout as io "
					+ "INNER JOIN m_inoutline as iol ON (io.m_inout_id = iol.m_inout_id) "
					+ "WHERE io.m_inout_id = "+getM_InOut_ID()+" AND iol.c_orderline_id = ?";
			BigDecimal cantsInOut = DB.getSQLValueBD(get_TrxName(), sql, getC_OrderLine_ID() );
			BigDecimal cantsOrder = DB
					.getSQLValueBD(
							get_TrxName(),
							"SELECT coalesce(qtyEntered,0.00) FROM C_OrderLine WHERE C_OrderLine_ID = ? ",
							getC_OrderLine_ID());
			if (getMovementQty().add(cantsInOut).compareTo(cantsOrder) > 0) {
				log.saveError("ExistsDocumentLineWithOrderLine", "");
				return false;
			}
		}
		
		// Controlar cantidades por unidad de medida
        if(!MUOM.isAllowedQty(getCtx(), getC_UOM_ID(), getQtyEntered(), get_TrxName())){
			log.saveError(Msg.getMsg(getCtx(), "UOMNotAllowedQty",
					new Object[] { MUOM.get(getCtx(), getC_UOM_ID()).getName(),
							getQtyEntered() }), "");
			return false;
        }

		return true;
	} // beforeSave

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	protected boolean beforeDelete() {
		/*
		 * 
		 * Efectivamente, ese codigo no tiene sentido ahi, borralo
		 * tranquilamente y envialo tambien a la estandar cuando mezcles lo s
		 * cambios del numero de serie..
		 * 
		 * 
		 * log.saveError( "Error",Msg.getMsg( getCtx(),"CannotDelete" ));
		 * 
		 * return false;
		 */
		String docStatus = (String) DB.getSQLObject(get_TrxName(),
				"SELECT DocStatus FROM " + X_M_InOut.Table_Name
						+ " WHERE M_InOut_ID = ? ",
				new Object[] { getM_InOut_ID() });
		if (!MInOut.DOCSTATUS_Drafted.equals(docStatus)
				&& !MInOut.DOCSTATUS_InProgress.equals(docStatus)) {
			log.saveError("Error", Msg.getMsg(getCtx(), "CannotDelete"));
			return false;
		}

		return true;
	} // beforeDelete

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public String toString() {
		StringBuffer sb = new StringBuffer("MInOutLine[").append(getID())
				.append(",M_Product_ID=").append(getM_Product_ID())
				.append(",QtyEntered=").append(getQtyEntered())
				.append(",MovementQty=").append(getMovementQty())
				.append(",M_AttributeSetInstance_ID=")
				.append(getM_AttributeSetInstance_ID()).append("]");

		return sb.toString();
	} // toString

	public boolean shouldSetAttrSetInstance() {
		return shouldSetAttrSetInstance(null);
	}

	private boolean shouldSetAttrSetInstance(MInOut inout) {
		int ProductID = getM_Product_ID();

		if (ProductID == 0)
			return false;

		/*
		 * Modificar C_OrderLine y M_InOutLine para que compruebe que se ha
		 * introducido el conjunto de atributos, antes de permitir grabar si
		 * producto tiene configurado conjunto de atributos y se deben
		 * establacer.
		 */
		boolean pnia = MAttributeSet.ProductNeedsInstanceAttribute(ProductID, get_TrxName());

		if (!pnia)
			return false;

		if (inout == null)
			inout = new MInOut(getCtx(), getM_InOut_ID(), get_TrxName());

		MAttributeSet.CondicionesCasos cc = MAttributeSet
				.GetCondicionesAtributos(MAttributeSet.GetCasoByTableName(null,
						this, inout.getC_DocType_ID(), inout.isSOTrx()));

		return cc.isAtributeSetInstenceMandatory();

	}

	/** Devuelve la descripcion del producto asociado a la línea */
	public String getProductName() {
		if (getM_Product_ID() > 0) {
			MProduct prod = new MProduct(p_ctx, getM_Product_ID(), null);
			return getDescription() == null ? prod.getName() : (prod.getName()
					+ " - " + getDescription());
		}
		return getDescription();
	}

	/** Devuelve la descripcion del producto asociado a la línea */
	public String getUOMName() {
		if (getC_UOM_ID() > 0)
			return (new MUOM(p_ctx, getC_UOM_ID(), null)).getName();
		return "";
	}

	public String getLineStr() {
		return "" + getLine();
	}

	public String getProductValue() {
		if (getM_Product_ID() > 0)
			return (new MProduct(p_ctx, getM_Product_ID(), null)).getValue();
		return "";
	}

	/**
	 * @param movementType
	 *            tipo de movimiento
	 * @return true si el tipo de movimiento parámetro es de tipo producción
	 */
	protected boolean isProductionMovement(String movementType) {
		return movementType.startsWith("P");
	}

	public boolean isTPVInstance() {
		return isTPVInstance;
	}

	public void setTPVInstance(boolean isTPVInstance) {
		this.isTPVInstance = isTPVInstance;
	}

	/**
	 * @return nombre del cargo relacionado con esta línea NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
	public String getChargeName() {
		String changeName = "";
		if (!Util.isEmpty(getC_Charge_ID(), true)) {
			MCharge charge = new MCharge(getCtx(), getC_Charge_ID(),
					get_TrxName());
			changeName = charge.getName();
		}
		return changeName;
	}

	/**
	 * @return nombre del país relacionado con esta línea NO MODIFICAR FIRMA, SE
	 *         USA EN LA IMPRESIÓN DE LA FACTURA
	 */
	public String getCountryName() {
		String countryName = "";
		if (!Util.isEmpty(getC_Country_ID(), true)) {
			MCountry country = new MCountry(getCtx(), getC_Country_ID(),
					get_TrxName());
			countryName = country.getName();
		}
		return countryName;
	}

	/**
	 * @return nombre del proyecto relacionado con esta línea NO MODIFICAR
	 *         FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
	public String getProjectName() {
		String projectName = "";
		if (!Util.isEmpty(getC_Project_ID(), true)) {
			MProject project = new MProject(getCtx(), getC_Project_ID(),
					get_TrxName());
			projectName = project.getName();
		}
		return projectName;
	}

	/**
	 * @return descripción (por valor de campos identificadores) de la ubicación
	 *         relacionada con esta línea NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
	public String getLocatorDescription() {
		String locatorName = "";
		if (!Util.isEmpty(getM_Locator_ID(), true)) {
			MLocator locator = new MLocator(getCtx(), getM_Locator_ID(),
					get_TrxName());
			locatorName = DisplayUtil.getDisplayByIdentifiers(getCtx(),
					locator, X_M_Locator.Table_ID, get_TrxName());
		}
		return locatorName;
	}

	/**
	 * @return nombre del depósito. NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN
	 *         DE LA FACTURA
	 */
	public String getWarehouseName() {
		String warehouseName = null;
		if (!Util.isEmpty(getM_Warehouse_ID(), true)) {
			warehouseName = new MWarehouse(getCtx(), getM_Warehouse_ID(),
					get_TrxName()).getName();
		}
		return warehouseName;
	}

	/**
	 * @return la descripción por identificadores de la ref inout line. NO
	 *         MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
	public String getRefInOutLineDescription() {
		String refInOutLineDescription = null;
		if (!Util.isEmpty(getRef_InOutLine_ID(), true)) {
			MInOutLine refInOutLine = new MInOutLine(getCtx(),
					getRef_InOutLine_ID(), get_TrxName());
			refInOutLineDescription = DisplayUtil.getDisplayByIdentifiers(
					getCtx(), refInOutLine, X_M_InOutLine.Table_ID,
					get_TrxName());
		}
		return refInOutLineDescription;
	}

	/**
	 * Obtener la línea de la factura referenciada por esta línea de remito
	 * 
	 * @param requery
	 *            consultar nuevamente a la base de datos
	 * @return la línea de la factura con columna m_inoutline_id = getID().
	 */
	public MInvoiceLine getInvoiceLineReferenced(boolean requery) {
		if (invoiceLine == null || requery) {
			invoiceLine = MInvoiceLine.getOfInOutLine(this);
		}
		return invoiceLine;
	}

	/**
	 * Obtener la línea del pedido referenciada por esta línea de remito
	 * 
	 * @param requery
	 *            consultar nuevamente a la base de datos
	 * @return línea del pedido que se obtiene desde la columna
	 *         {@link MInOutLine#getC_OrderLine_ID()}
	 */
	public MOrderLine getOrderLineReferenced(boolean requery) {
		if (orderLine == null || requery) {
			if(!Util.isEmpty(getC_OrderLine_ID(), true)){
				orderLine = new MOrderLine(getCtx(), getC_OrderLine_ID(),
						get_TrxName());
			}
		}
		return orderLine;
	}

	/**
	 * @return precio ingresado con impuestos. La prioridad para verificación
	 *         es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
	public BigDecimal getPriceEnteredWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getPriceEnteredWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getPriceEnteredWithTax();
		}
		return amt;
	}

	/**
	 * @return precio ingresado sin impuestos. La prioridad para verificación
	 *         es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getPriceEnteredNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getPriceEnteredNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getPriceEnteredNet();
		}
		return amt;
	}

	/**
	 * @return precio ingresado con impuestos * cantidad ingresada. La prioridad
	 *         para verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalPriceEnteredWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalPriceEnteredWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalPriceEnteredWithTax();
		}
		return amt;
	}

	/**
	 * @return precio ingresado sin impuestos * cantidad ingresada. La prioridad
	 *         para verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalPriceEnteredNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalPriceEnteredNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalPriceEnteredNet();
		}
		return amt;
	}

	/**
	 * @return precio de lista con impuestos. La prioridad para verificación es:
	 *         1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getPriceListWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getPriceListWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getPriceListWithTax();
		}
		return amt;
	}

	/**
	 * @return precio de lista sin impuestos. La prioridad para verificación es:
	 *         1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getPriceListNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getPriceListNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getPriceListNet();
		}
		return amt;
	}

	/**
	 * @return precio de lista con impuestos * cantidad ingresada. La prioridad
	 *         para verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalPriceListWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalPriceListWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalPriceListWithTax();
		}
		return amt;
	}

	/**
	 * @return precio de lista sin impuestos * cantidad ingresada. La prioridad
	 *         para verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalPriceListNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalPriceListNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalPriceListNet();
		}
		return amt;
	}

	/**
	 * @return precio actual con impuestos. La prioridad para verificación es:
	 *         1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getPriceActualWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getPriceActualWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getPriceActualWithTax();
		}
		return amt;
	}

	/**
	 * @return precio actual sin impuestos. La prioridad para verificación es:
	 *         1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getPriceActualNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getPriceActualNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getPriceActualNet();
		}
		return amt;
	}

	/**
	 * @return precio actual con impuestos * cantiada ingresada. La prioridad
	 *         para verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalPriceActualWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalPriceActualWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalPriceActualWithTax();
		}
		return amt;
	}

	/**
	 * @return precio actual sin impuestos * cantiada ingresada. La prioridad para verificación es:
	 *         1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR
	 *         FIRMA, SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalPriceActualNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalPriceActualNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalPriceActualNet();
		}
		return amt;
	}

	/**
	 * @return bonificación con impuestos por unidad, o sea, bonificación con
	 *         impuesto / cantidad ingresada. La prioridad para verificación es:
	 *         1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getBonusUnityAmtWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getBonusUnityAmtWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getBonusUnityAmtWithTax();
		}
		return amt;
	}

	/**
	 * @return bonificación sin impuestos por unidad, o sea, bonificación sin
	 *         impuesto / cantidad ingresada. La prioridad para verificación es:
	 *         1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getBonusUnityAmtNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getBonusUnityAmtNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getBonusUnityAmtNet();
		}
		return amt;
	}

	/**
	 * @return bonificación con impuestos. La prioridad para verificación es: 1)
	 *         Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalBonusUnityAmtWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalBonusUnityAmtWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalBonusUnityAmtWithTax();
		}
		return amt;
	}

	/**
	 * @return bonificación sin impuestos. La prioridad para verificación es: 1)
	 *         Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalBonusUnityAmtNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalBonusUnityAmtNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalBonusUnityAmtNet();
		}
		return amt;
	}

	/**
	 * @return descuento de línea con impuestos por unidad, o sea, descuento de
	 *         línea con impuestos / cantidad ingresada. La prioridad para
	 *         verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getLineDiscountUnityAmtWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getLineDiscountUnityAmtWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getLineDiscountUnityAmtWithTax();
		}
		return amt;
	}

	/**
	 * @return descuento de línea sin impuestos por unidad, o sea, descuento de
	 *         línea sin impuestos / cantidad ingresada. La prioridad para
	 *         verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getLineDiscountUnityAmtNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getLineDiscountUnityAmtNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getLineDiscountUnityAmtNet();
		}
		return amt;
	}

	/**
	 * @return descuento de línea con impuestos. La prioridad para verificación
	 *         es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalLineDiscountUnityAmtWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalLineDiscountUnityAmtWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalLineDiscountUnityAmtWithTax();
		}
		return amt;
	}

	/**
	 * @return descuento de línea sin impuestos. La prioridad para verificación
	 *         es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalLineDiscountUnityAmtNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalLineDiscountUnityAmtNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalLineDiscountUnityAmtNet();
		}
		return amt;
	}

	/**
	 * @return descuento de documento con impuestos por unidad, o sea, descuento
	 *         de documento con impuestos / cantidad ingresada. La prioridad
	 *         para verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getDocumentDiscountUnityAmtWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getDocumentDiscountUnityAmtWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getDocumentDiscountUnityAmtWithTax();
		}
		return amt;
	}

	/**
	 * @return descuento de documento sin impuestos por unidad, o sea, descuento
	 *         de documento sin impuestos / cantidad ingresada. La prioridad
	 *         para verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getDocumentDiscountUnityAmtNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getDocumentDiscountUnityAmtNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getDocumentDiscountUnityAmtNet();
		}
		return amt;
	}

	/**
	 * @return descuento de documento con impuestos. La prioridad para
	 *         verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalDocumentDiscountUnityAmtWithTax() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalDocumentDiscountUnityAmtWithTax();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalDocumentDiscountUnityAmtWithTax();
		}
		return amt;
	}

	/**
	 * @return descuento de documento sin impuestos. La prioridad para
	 *         verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO
	 */
	public BigDecimal getTotalDocumentDiscountUnityAmtNet() {
		BigDecimal amt = BigDecimal.ZERO;
		if (getInvoiceLineReferenced(false) != null) {
			amt = getInvoiceLineReferenced(false).getTotalDocumentDiscountUnityAmtNet();
		} else if (getOrderLineReferenced(false) != null) {
			amt = getOrderLineReferenced(false).getTotalDocumentDiscountUnityAmtNet();
		}
		return amt;
	}

	/**
	 * @return true si la tasa está incluída en el precio, false caso contrario. La prioridad para
	 *         verificación es: 1) Línea de factura referenciada en
	 *         {@link MInvoiceLine#getM_InOutLine_ID()}; 2) Línea de pedido
	 *         referenciada en {@link #getC_OrderLine_ID()}. NO MODIFICAR FIRMA,
	 *         SE USA EN LA IMPRESIÓN DEL REMITO 
	 */
	public boolean isTaxIncluded(){
		boolean included = false;
		if (getInvoiceLineReferenced(false) != null) {
			included = getInvoiceLineReferenced(false).isTaxIncluded();
		} else if (getOrderLineReferenced(false) != null) {
			included = getOrderLineReferenced(false).isTaxIncluded();
		}
		return included;
	}
	
	/**
	 * 	Get Ship lines Of RMA Line
	 *	@param ctx context
	 *	@param M_RMALine_ID line
	 *	@param where optional addition where clause
	 *  @param trxName transaction
	 *	@return array of receipt lines
	 */
	public static MInOutLine[] getOfRMALine (Properties ctx,
		int M_RMALine_ID, String where, String trxName)
	{
		String whereClause = "M_RMALine_ID=? " + (!Util.isEmpty(where, true) ? " AND "+where : "");
		List<MRMALine> list = new Query(ctx, Table_Name, whereClause, trxName)
									.setParameters(M_RMALine_ID)
									.list();
		return list.toArray (new MInOutLine[list.size()]);
	}	//	getOfRMALine
	
	public boolean sameOrderLineUOM()
	{
		if (getC_OrderLine_ID() <= 0)
			return false;

		MOrderLine oLine = new MOrderLine(getCtx(), getC_OrderLine_ID(), get_TrxName());

		if (oLine.getC_UOM_ID() != getC_UOM_ID())
			return false;

		// inout has orderline and both has the same UOM
		return true;
	}
		
	/**
	 * @return precio std de tarifa costo.
	 */
	public BigDecimal getPriceStdCost(){
		return DB.getSQLValueBD(get_TrxName(), "SELECT * FROM determineproductpricestd(?,"+getAD_Org_ID()+",'N')", getM_Product_ID());
	}
	
	/**
	 * @return precio std de tarifa venta.
	 */
	public BigDecimal getPriceStdSales(){
		return DB.getSQLValueBD(get_TrxName(), "SELECT * FROM determineproductpricestd(?,"+getAD_Org_ID()+",'Y')", getM_Product_ID());
	}

} // MInOutLine

/*
 * @(#)MInOutLine.java 02.07.07
 * 
 * Fin del fichero MInOutLine.java
 * 
 * Versión 2.2
 */
