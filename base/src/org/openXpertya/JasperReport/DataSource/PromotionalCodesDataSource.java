package org.openXpertya.JasperReport.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MPromotion;
import org.openXpertya.model.MPromotionCode;
import org.openXpertya.model.X_C_Promotion_Code;
import org.openXpertya.util.DB;
import org.openXpertya.util.PromotionBarcodeGenerator;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class PromotionalCodesDataSource implements OXPJasperDataSource {

	private Properties ctx;
	private String trxName;
	private int promotionalCodeBatchID;
	private int m_currentRecord = -1;
	private int total_lines = -1;
	private PromotionBarcodeGenerator currentBarcodeGenerator;
	private List<MPromotionCode> promotionCodes;
	
	public PromotionalCodesDataSource(Properties ctx, int promotionalCodeBatchID, String trxName) {
		setCtx(ctx);
		setTrxName(trxName);
		setPromotionalCodeBatchID(promotionalCodeBatchID);
		setPromotionCodes(new ArrayList<MPromotionCode>());
	}

	public void loadData() throws RuntimeException {
		// Datos de cupones
		String sql = "select * from "+X_C_Promotion_Code.Table_Name+
				" where c_promotion_code_batch_id = ? and isactive = 'Y'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = DB.prepareStatement(sql, getTrxName());
			ps.setInt(1, getPromotionalCodeBatchID());
			rs = ps.executeQuery();
			while (rs.next()) {
				getPromotionCodes().add(new MPromotionCode(getCtx(), rs, getTrxName()));
			}
			total_lines = getPromotionCodes().size();
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean next() throws JRException {
		m_currentRecord++;
		if(m_currentRecord < total_lines){
			setCurrentBarcodeGenerator(new PromotionBarcodeGenerator());
			getCurrentBarcodeGenerator().setPromotionCode(getPromotionCodes().get(m_currentRecord));
			getCurrentBarcodeGenerator().generateCode();
			return true;
		}
		
		return false;
	}
	
	@Override
	public Object getFieldValue(JRField arg0) throws JRException {
		String name = arg0.getName().toUpperCase();
		Object value = null;
		if(name.equals("CODE")){
			value = getCurrentBarcodeGenerator().getCode();
		}
		else if(name.equals("BARCODE")){
			value = getCurrentBarcodeGenerator().getBarcodeImage();
		}
		else if(name.equals("PROMOTION")){
			MPromotion promo = new MPromotion(getCtx(), getPromotionCodes().get(m_currentRecord).getC_Promotion_ID(),
					getTrxName());
			value = promo.getName();
		}
		else if(name.equals("VALID_FROM")){
			value = getPromotionCodes().get(m_currentRecord).getValidFrom();
		}
		else if(name.equals("VALID_TO")){
			value = getPromotionCodes().get(m_currentRecord).getValidTo();
		}
		return value;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	protected String getTrxName() {
		return trxName;
	}

	protected void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	protected int getPromotionalCodeBatchID() {
		return promotionalCodeBatchID;
	}

	protected void setPromotionalCodeBatchID(int promotionalCodeBatchID) {
		this.promotionalCodeBatchID = promotionalCodeBatchID;
	}

	protected PromotionBarcodeGenerator getCurrentBarcodeGenerator() {
		return currentBarcodeGenerator;
	}

	protected void setCurrentBarcodeGenerator(PromotionBarcodeGenerator currentBarcodeGenerator) {
		this.currentBarcodeGenerator = currentBarcodeGenerator;
	}

	protected List<MPromotionCode> getPromotionCodes() {
		return promotionCodes;
	}

	protected void setPromotionCodes(List<MPromotionCode> promotionCodes) {
		this.promotionCodes = promotionCodes;
	}

}
