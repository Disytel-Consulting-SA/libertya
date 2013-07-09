package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Línea de Fraccionamiento de Artículo. Artículo
 * Resultante de fraccionar un artículo original.
 * 
 * @author Franco Bonafine - Disytel
 *
 */
public class MSplittingLine extends X_M_SplittingLine {

	/** Cache del fraccionamiento. Se resetea antes de cada save en la línea. */ 
	private MSplitting splitting = null;
	
	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param M_SplittingLine_ID
	 * @param trxName
	 */
	public MSplittingLine(Properties ctx, int M_SplittingLine_ID, String trxName) {
		super(ctx, M_SplittingLine_ID, trxName);
		// Valores por defecto para nuevos registros.
		if (M_SplittingLine_ID == 0) {
			setProductQty(BigDecimal.ZERO);
			setConvertedQty(BigDecimal.ZERO);
		}
	}

	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MSplittingLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		splitting = null; // Resetea el encabezado para que se vuelva a cargar
		
		// Validaciones del artículo destino (los mensajes en el log
		// se asignan dentro del método)
		if (!validateTargetProduct()) {
			return false;
		}
		
		// No es posible ingresar dos o mas líneas con el mismo artículo.
		if (!validateDuplicateLine()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "DuplicateSplittingLine"));
			return false;
		}
		
		// La cantidad del artículo debe ser mayor que cero
		if (getProductQty().compareTo(BigDecimal.ZERO) <= 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "ValueMustBeGreatherThanZero",
					new Object[] { Msg.translate(getCtx(), "Quantity"), "0" }));
			return false;
		}
		
		// Se calcula la cantidad convertida del artículo
		if (is_ValueChanged("M_Product_To_ID") || is_ValueChanged("ProductQty")) {
			calculateQuantity();
		}
		
		// Se valida la línea según las condiciones del encabezado.
		if (!getSplitting().beforeSaveLine(this)) {
			return false;
		}
		
		// Setear precio de costo
		setCost(MProductPricing.getCostPrice(getCtx(), getAD_Org_ID(),
				getM_Product_To_ID(),
				MProductPO.getFirstVendorID(getM_Product_To_ID(), get_TrxName()),
				Env.getContextAsInt(getCtx(), "$C_Currency_ID"), Env.getDate(),
				false, false, null, false, get_TrxName()));
		
		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		return updateSplitting(success);
	}
	
	@Override
	protected boolean afterDelete(boolean success) {
		return updateSplitting(success);
	}
	
	/**
	 * Actualiza el encabezado del fraccionamiento.
	 */
	private boolean updateSplitting(boolean success) {
		// Solo se actualiza el encabezado del fraccionamiento si el guardado
		// o borrado de esta línea se realizó correctamente.
		if (success) {
			getSplitting().update();
			success = getSplitting().save();
		}
		return success;
	}

	/**
	 * @return Devuelve el encabezado del fraccionamiento al cual
	 * pertenece está línea.
	 */
	public MSplitting getSplitting() {
		if (splitting == null) {
			splitting = new MSplitting(getCtx(), getM_Splitting_ID(), get_TrxName());
		}
		return splitting;
		
	}
	
	/**
	 * @return Devuelve el artículo destino de la línea.
	 */
	public MProduct getProductTo() {
		return MProduct.get(getCtx(), getM_Product_To_ID());
	}
	
	/**
	 * Validación del artículo destino.
	 * @return true si el artículo es apto para el fraccionamiento, false en caso contrario
	 */
	protected boolean validateTargetProduct() {

		// El artículo destino debe estar en la lista de artículos destino de 
		// fraccionamiento del artículo del encabezado.
		if (!getSplitting().getProduct().isProductFraction(getM_Product_To_ID())) {
			log.saveError("SaveError", Msg.translate(getCtx(), "ProductMustBeFraction"));
			return false;
		}

		// El artículo debe ser convertible a la UM común de conversión del fraccionamiento.
		if (!getProductTo().hasConversionToUOM(getSplitting().getC_Conversion_UOM_ID())) {
			log.saveError("SaveError", Msg.translate(getCtx(), "SplitTargetProductConversionRequired"));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Valida si existe una línea en este fraccionamiento que ya contenga el artículo
	 * indicado en esta línea.
	 * @return true si no existen líneas duplicadas.
	 */
	private boolean validateDuplicateLine() {
		String sql = 
			"SELECT COUNT(*) " +
			"FROM M_SplittingLine " +
			"WHERE M_Product_To_ID = ? AND M_Splitting_ID = ? " +
			"  AND M_SplittingLine_ID <> ?";
		Long count = (Long)DB.getSQLObject(get_TrxName(), sql, 
				new Object[] {getM_Product_To_ID(), 
							  getM_Splitting_ID(), 
							  getM_SplittingLine_ID()});
		return count == 0;
	}
	
	/**
	 * Calcula y asigna la cantidad convertida a la UM común del fraccionamiento.
	 */
	private void calculateQuantity() {
		setConvertedQty(getSplitting().convertToConversionUOM(getProductQty(), getM_Product_To_ID()));
	}
}
