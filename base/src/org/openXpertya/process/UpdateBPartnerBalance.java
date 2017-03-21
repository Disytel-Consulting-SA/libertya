package org.openXpertya.process;

import java.util.Properties;

import org.openXpertya.cc.CurrentAccountDocument;
import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrg;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class UpdateBPartnerBalance extends SvrProcess {

	/** Entidad Comercial */
	private MBPartner bpartner;
	
	/** ID de Entidad Comercial */
	private Integer bpartnerID;
	
	/** Contexto local */
	private Properties localCtx;
	
	/** Transacción local */
	private String localTrxName;
	
	/** Actualizar Saldo */
	private boolean updateBalance = true;
	
	/** Actualizar Estado */
	private boolean updateStatus = true;
	
	public UpdateBPartnerBalance(){
		
	}
	
	public UpdateBPartnerBalance(Properties ctx, String trxName){
		setLocal_ctx(ctx);
		setLocalTrxName(trxName);
	}
	
	public UpdateBPartnerBalance(Properties ctx, Integer bpartnerID, String trxName){
		setLocal_ctx(ctx);
		setLocalTrxName(trxName);
		setBpartnerID(bpartnerID);
	}
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
        // deteminar la tabla base que será actualizada 
        for( int i = 0; i < para.length; i++ ) {
            String name = para[i].getParameterName();
            if( name.equals( "C_BPartner_ID" )) {
            	setBpartnerID(para[i].getParameterAsInt());
            }
        }
	}
	
	@Override
	protected String doIt() throws Exception {
		// Obtengo el managaer actual
		CurrentAccountManager manager = CurrentAccountManagerFactory
				.getManager(new CurrentAccountDocument() {
					
					@Override
					public boolean isSkipCurrentAccount() {
						return false;
					}
					
					@Override
					public boolean isSOTrx() {
						return getBpartner() != null && getBpartner().isCustomer();
					}
				});
		// Obtengo la organización
		MOrg org = new MOrg(getCtx(),Env.getAD_Org_ID(getCtx()), get_TrxName());
		if(isUpdateBalance() && isUpdateStatus()){
			// Actualizo el saldo y estado de la entidad comercial
			CallResult result = manager.updateBalanceAndStatus(getCtx(), org,
					getBpartner(), get_TrxName());
			if (result.isError()) {
				throw new Exception(result.getMsg());
			}
		}
		else{
			if(isUpdateBalance()){
				// Actualizo el saldo y estado de la entidad comercial
				CallResult result = manager.updateBalance(getCtx(), org,
						getBpartner(), get_TrxName());
				if (result.isError()) {
					throw new Exception(result.getMsg());
				}
			}
			if(isUpdateStatus()){
				// Actualizo el saldo y estado de la entidad comercial
				CallResult result = manager.setCurrentAccountStatus(getCtx(),
						getBpartner(), org, get_TrxName());
				if (result.isError()) {
					throw new Exception(result.getMsg());
				}
			}
		}
		return "@Terminated@";
	}

	@Override
	public Properties getCtx() {
		Properties ctx = super.getCtx();
		if(getLocalCtx() != null){
			ctx = getLocalCtx();
		}
		return ctx;
	}
	
	@Override
	protected String get_TrxName() {
		String trxName = super.get_TrxName();
		if(!Util.isEmpty(getLocalTrxName(), true)){
			trxName = getLocalTrxName();
		}
		return trxName;
	}
	
	public String doProcess() throws Exception{
		return doIt();
	}
	
	protected Integer getBpartnerID() {
		return bpartnerID;
	}

	protected void setBpartnerID(Integer bpartnerID) {
		this.bpartnerID = bpartnerID;
		setBpartner(new MBPartner(getCtx(), bpartnerID, get_TrxName()));
	}

	public Properties getLocalCtx() {
		return localCtx;
	}

	public void setLocal_ctx(Properties localCtx) {
		this.localCtx = localCtx;
	}

	protected String getLocalTrxName() {
		return localTrxName;
	}

	protected void setLocalTrxName(String localTrxName) {
		this.localTrxName = localTrxName;
	}

	public MBPartner getBpartner() {
		return bpartner;
	}

	public void setBpartner(MBPartner bpartner) {
		this.bpartner = bpartner;
	}

	public boolean isUpdateBalance() {
		return updateBalance;
	}

	public void setUpdateBalance(boolean updateBalance) {
		this.updateBalance = updateBalance;
	}

	public boolean isUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(boolean updateStatus) {
		this.updateStatus = updateStatus;
	}
}
