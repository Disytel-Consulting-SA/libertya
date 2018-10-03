package org.openXpertya.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.openXpertya.model.MPromotion;
import org.openXpertya.model.X_C_Promotion_Code;
import org.openXpertya.model.X_C_Promotion_Code_Batch;
import org.openXpertya.print.pdf.text.pdf.Barcode;
import org.openXpertya.print.pdf.text.pdf.Barcode128;

public class PromotionBarcodeGenerator extends BarcodeGenerator {

	/** Lote de generación actual */
	private X_C_Promotion_Code_Batch batchPromotionalCode = null;
	/** Promoción actual */
	private MPromotion promotion = null;

	/**
	 * Código promocional actual, si esta variable no es null, devuelve el
	 * código perteneciente a esta.
	 */
	private X_C_Promotion_Code promotionCode = null;
	
	/** Formato de fecha al reves */
	private SimpleDateFormat sdf;
	
	public PromotionBarcodeGenerator() {
		sdf = new SimpleDateFormat("SSSssmmHHddMMyy");
	}
	
	public PromotionBarcodeGenerator(X_C_Promotion_Code_Batch pcBatch, MPromotion promo) {
		this();
		setBatchPromotionalCode(pcBatch);
		setPromotion(promo);
	}

	@Override
	public String generateCode() {
		String code = getPromotionCode() != null?getPromotionCode().getCode():null;
		// Generar el código
		// El código se genera por: 
		
		// ID de lote generado + 
		// (milisegundo segundo minuto hora dia mes año) + 
		// ID de promoción
		if(Util.isEmpty(code, true)){
			Timestamp actualTime = Env.getTimestamp();
			code = getBatchPromotionalCode().getID()+sdf.format(actualTime)+getPromotion().getID();
			//((Barcode128)getBarcode()).
		}
		setCode(code);
		return code;
	}

	public X_C_Promotion_Code_Batch getBatchPromotionalCode() {
		return batchPromotionalCode;
	}

	public void setBatchPromotionalCode(X_C_Promotion_Code_Batch batchPromotionalCode) {
		this.batchPromotionalCode = batchPromotionalCode;
	}

	public MPromotion getPromotion() {
		return promotion;
	}

	public void setPromotion(MPromotion promotion) {
		this.promotion = promotion;
	}

	public X_C_Promotion_Code getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(X_C_Promotion_Code promotionCode) {
		this.promotionCode = promotionCode;
	}

	@Override
	public Barcode createBarcode() {
		return new Barcode128();
	}

}
