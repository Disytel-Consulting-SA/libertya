package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

/**
 * Línea de Transferencia de Mercadería
 * 
 * @author Franco Bonafine - Disytel
 */
public class MTransferLine extends X_M_TransferLine {

	/** Transferencia */
	private MTransfer transfer = null;
	
	/**
	 * Constructor de la clase.
	 * @param ctx
	 * @param transferLine_ID
	 * @param trxName
	 */
	public MTransferLine(Properties ctx, int transferLine_ID,
			String trxName) {
		super(ctx, transferLine_ID, trxName);
		
		if (transferLine_ID == 0) {
			setConfirmedQty(BigDecimal.ZERO);
			setQty(BigDecimal.ZERO);
		}
	}

	/**
	 * Constructor de la clase.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MTransferLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/**
	 * Constructor de la clase.
	 * @param transfer Encabezado al que pertenecerá la línea.
	 */
	public MTransferLine(MTransfer transfer) {
		this(transfer.getCtx(), 0, transfer.get_TrxName());
        if (transfer.getM_Transfer_ID() == 0) {
            throw new IllegalArgumentException("Header not saved");
        }
		setClientOrg(transfer);
		setM_Transfer_ID(transfer.getM_Transfer_ID());
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// La cantidad del artículo debe ser mayor que cero
		if (getQty().compareTo(BigDecimal.ZERO) <= 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "ValueMustBeGreatherThanZero",
					new Object[] { Msg.translate(getCtx(), "Quantity"), "0" }));
			return false;
		}

		/* COMENTADO: Flexibilizar la gestión de líneas de transferencia (por ejemplo: números de serie requieren una unidad por línea) */ 
//		// No es posible ingresar dos o mas líneas con el mismo artículo y ubicación origen.
//		if (!validateDuplicateLine()) {
//			log.saveError("SaveError", Msg.translate(getCtx(), "DuplicateMaterialTransferLine"));
//			return false;
//		}
		
		// Setear la ubicación por defecto del depósito de la transferencia entrante
		// cuando no posee valor asignado
		if (getTransfer().isWarehouseTransfer() && getM_Locator_To_ID() == 0) {
			MLocator locatorDefault = null;
			try{
				locatorDefault = MLocator.getDefault(getCtx(), getTransfer().getM_WarehouseTo_ID(), false,
						get_TrxName());
			} catch(Exception e){
				e.printStackTrace();
			}
			if(locatorDefault != null){
				setM_Locator_To_ID(locatorDefault.getM_Locator_ID());
			}
		}
		
		// Obtiene el número de línea si no ha sido asignado aún
		if (getLine() == 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COALESCE(MAX(Line), 0)+10 ");
			sql.append("FROM M_TransferLine ");
			sql.append("WHERE M_Transfer_ID = ?");

			int lineNo = DB.getSQLValue(get_TrxName(), sql.toString(), getM_Transfer_ID());

			setLine(lineNo);
		}
		return true;
	}
	
	/**
	 * Valida si existe una línea de la transferencia que ya contenga el artículo
	 * y ubicación indicados en esta línea.
	 * @return true si no existen líneas duplicadas.
	 */
	private boolean validateDuplicateLine() {
		String sql = 
			"SELECT COUNT(*) " +
			"FROM M_TransferLine " +
			"WHERE M_Product_ID = ? AND M_Locator_ID = ?" +
			"  AND M_Transfer_ID = ? " +
			"  AND M_TransferLine_ID <> ?";
		Long count = (Long)DB.getSQLObject(get_TrxName(), sql, 
				new Object[] {getM_Product_ID(), 
							  getM_Locator_ID(), 
							  getM_Transfer_ID(),
							  getM_TransferLine_ID()});
		return count == 0;
	}

	/**
	 * @return Devuelve el artículo de la línea.
	 */
	public MProduct getProduct() {
		return MProduct.get(getCtx(), getM_Product_ID());
	}

	public void setClientOrg(int AD_Client_ID, int AD_Org_ID){
		super.setClientOrg(AD_Client_ID, AD_Org_ID);
	} // setClientOrg
	
	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param oLine
	 * @param M_Locator_ID
	 * @param Qty
	 */

	public void setOrderLine(MOrderLine oLine, int M_Locator_Origin_ID, int M_Locator_Destination_ID, BigDecimal Qty) {
		setLine(oLine.getLine());

		MProduct product = oLine.getProduct();
		if (product == null) {
			set_ValueNoCheck("M_Product_ID", null);
			set_ValueNoCheck("M_AttributeSetInstance_ID", null);
			set_ValueNoCheck("M_Locator_ID", null);
		} else {
			setM_Product_ID(oLine.getM_Product_ID());
			setM_Locator_ID(M_Locator_Origin_ID);
			setM_Locator_To_ID(M_Locator_Destination_ID);
		}
	} // setOrderLine

	protected MTransfer getTransfer() {
		if(transfer == null){
			transfer = new MTransfer(getCtx(), getM_Transfer_ID(), get_TrxName());
		}
		return transfer;
	}

	protected void setTransfer(MTransfer transfer) {
		this.transfer = transfer;
	}

}
