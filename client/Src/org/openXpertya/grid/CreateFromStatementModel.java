package org.openXpertya.grid;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankStatementLine;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MRefList;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class CreateFromStatementModel extends CreateFromModel {

	// =============================================================================================
	// Logica en comun para la carga de cuentas
	// =============================================================================================

	private CreateFromStatementData createFromData;
	
	
	public void loadPayment(Payment payment, ResultSet rs) throws SQLException {
        payment.selected = false;
        payment.paymentID = rs.getInt("C_Payment_ID");
        payment.dateTrx = rs.getTimestamp("DateTrx");
        payment.documentNo = rs.getString("DocumentNo");
        payment.currencyID = rs.getInt("C_Currency_ID");
        payment.currencyISO = rs.getString("ISO_Code");
        payment.payAmt = rs.getBigDecimal("PayAmt");
        payment.convertedAmt = rs.getBigDecimal("ConvertedAmt");
        payment.bPartnerName = rs.getString("BPartnerName");
        payment.tenderType = rs.getString("tendertype");
		payment.tenderTypeDescription = MRefList.getListName(ctx, MPayment.TENDERTYPE_AD_Reference_ID,
				payment.tenderType);
		if (!Util.isEmpty(payment.tenderType)
				&& MPayment.TENDERTYPE_Check.equals(payment.tenderType)) {
	        payment.dueDate=rs.getTimestamp("duedate");
		}
		payment.boletaDepositoID = rs.getInt("m_boletadeposito_id");
		payment.boletaDepositoDocumentNo = rs.getString("boletadeposito_documentno");
		payment.creditCardSettlementID = rs.getInt("c_creditcardsettlement_id");
		payment.creditCardSettlementDocumentNo = rs.getString("creditcardsettlement_documentno");
	}
	
	
    public void save(int C_BankStatement_ID, String trxName, List<? extends SourceEntity> selectedSourceEntities, CreateFromPluginInterface handler) throws CreateFromSaveException {
    	// fixed values
        MBankStatement bs = new MBankStatement( Env.getCtx(),C_BankStatement_ID, trxName);
        
        selectedSourceEntities= ungroup(selectedSourceEntities);

        // Lines
        for (SourceEntity sourceEntity : selectedSourceEntities) {
        	Payment payment = (Payment)sourceEntity;	
            Timestamp trxDate = payment.dueDate != null?payment.dueDate:payment.dateTrx;
            int C_Payment_ID = payment.paymentID;
            int C_Currency_ID = payment.currencyID;
            BigDecimal TrxAmt  = payment.payAmt;
            BigDecimal StmtAmt = payment.convertedAmt;

            //

            log.fine( "Line Date=" + trxDate + ", Payment=" + C_Payment_ID + ", Currency=" + C_Currency_ID + ", Amt=" + TrxAmt );

            //
            MPayment pay = new MPayment( Env.getCtx(),C_Payment_ID, trxName);
            MBankStatementLine bsl = new MBankStatementLine( bs );

            bsl.setStatementLineDate( trxDate );
            bsl.setPayment(pay);
            
            handler.customMethod(pay, null);
            
            if( !bsl.save()) {
                throw new CreateFromSaveException(
             		   "@StatementLineSaveError@ (@C_Paymenty_ID@ # " + payment.documentNo + "):<br>" + 
             		   CLogger.retrieveErrorAsString()
             	);

            }
        }        // for all rows
    }    // save

    /**
     * Datos en base al origen y los parámetros 
     * @return lista de payments filtrados
     */
	public List<Payment> getData(){
		List<Payment> data = new ArrayList<CreateFromModel.Payment>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = getCreateFromData().getQuery();

		try {
			pstmt = DB.prepareStatement(sql, null, true);
			int p = 1;
			for (Object param : getCreateFromData().getSQLParams()) {
				pstmt.setObject(p++, param);
			}
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Payment payment = new Payment();
				loadPayment(payment, rs);
				data.add(payment);
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, sql.toString(), e);
		} finally {
			try {
				if (rs != null)	rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e2) {
				log.log(Level.SEVERE, sql.toString(), e2);
			}
		}
		return data;
	}
	
	public void setBankAccount(Integer bankAccountID){
		getCreateFromData().setBankAccountID(bankAccountID);
	}
	
	public void setCouponBatchNo(String couponBatchNo){
		getCreateFromData().setCouponBatchNo(couponBatchNo);
	}
	
	public void setGrouped(boolean grouped){
		getCreateFromData().setGrouped(grouped);
	}
	
	public void setStatementDate(Timestamp statementDate){
		getCreateFromData().setStatementDate(statementDate);
	}
	
	public boolean isAllowGrouped(){
		return getCreateFromData().isAllowGrouped();
	}
	
	public boolean isAllowCouponBatchNoFilter(){
		return getCreateFromData().isAllowCouponBatchNoFilter();
	}
    
	private List<? extends SourceEntity> ungroup(List<? extends SourceEntity> selectedSourceEntities){
		List<SourceEntity> selectedSourceEntitiesReturn = new ArrayList<SourceEntity>();

		// Se debe desagrupar cuando está agrupado
		if(getCreateFromData().isAllowGrouped() && getCreateFromData().isGrouped()){
			Timestamp minDateTrx = null, maxDateTrx = null;
			
			for (SourceEntity sourceEntity : selectedSourceEntities) {
				Payment payment = (Payment) sourceEntity;
				// Tomar los payments de las fechas seleccionadas
				if(minDateTrx == null || minDateTrx.after(payment.dateTrx)){
					minDateTrx = payment.dateTrx;
				}
				if(maxDateTrx == null || maxDateTrx.before(payment.dateTrx)){
					maxDateTrx = payment.dateTrx;
				}
			}
			// Si hay fechas entonces hay datos seleccionados
			if(minDateTrx != null || maxDateTrx != null){
				// Se setea a false para que no agrupe
				getCreateFromData().setGrouped(false);
				getCreateFromData().setDateTrxFrom(minDateTrx);
				getCreateFromData().setDateTrxTo(maxDateTrx);
				selectedSourceEntitiesReturn.addAll(getData());
			}
			getCreateFromData().setGrouped(true);
			getCreateFromData().setDateTrxFrom(null);
			getCreateFromData().setDateTrxTo(null);
		}
		// Ya está desagrupado
		else{
			selectedSourceEntitiesReturn = (List<CreateFromModel.SourceEntity>)selectedSourceEntities;
		}
		return selectedSourceEntitiesReturn;
	}


	public CreateFromStatementData getCreateFromData() {
		return createFromData;
	}


	public void setCreateFromData(CreateFromStatementData createFromData) {
		this.createFromData = createFromData;
	}

}
