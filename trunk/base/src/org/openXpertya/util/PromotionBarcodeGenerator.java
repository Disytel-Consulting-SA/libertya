package org.openXpertya.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.openXpertya.model.MPromotion;
import org.openXpertya.model.X_AD_Org;
import org.openXpertya.model.X_C_Invoice;
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
		sdf = new SimpleDateFormat("ddMMyy");
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
			//code = getBatchPromotionalCode().getID()+sdf.format(actualTime)+getPromotion().getID();
			
			/* Modificado a fin de que genere codigos de 16 caracteres:
		     * 	Value de Sucursal (a la cual pertenece el ticket original de la compra)
		     * 	ID Cupón (más un 0 si la longitud del ID es menor a 7)
		     * 	Fecha Cupon (ddMMyy)
		     */
			String promCodeID = Integer.toString(promotionCode.getC_Promotion_Code_ID());
			if (promCodeID.length() < 8)
				promCodeID = "0" + promCodeID;
			StringBuffer sb = new StringBuffer();
			sb.append(getOrgValueFromOriginalTicket());
			sb.append(promCodeID);
			sb.append(sdf.format(actualTime));
			code = sb.toString();
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

	protected String getOrgValueFromOriginalTicket() {
		X_C_Invoice ticket = new X_C_Invoice(promotionCode.getCtx(), promotionCode.getC_Invoice_Orig_ID(), promotionCode.get_TrxName());
		X_AD_Org org = new X_AD_Org(ticket.getCtx(), ticket.getAD_Org_ID(), ticket.get_TrxName());
		return org.getValue();
	}
	
}
