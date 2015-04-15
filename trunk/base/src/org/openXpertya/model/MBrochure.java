package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MBrochure extends X_M_Brochure implements DocAction {

	public MBrochure(Properties ctx, int M_Brochure_ID, String trxName) {
		super(ctx, M_Brochure_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MBrochure(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave( boolean newRecord ) {
		// La fecha de inicio no puede estar después que la fecha de fin
		if(getDateFrom().after(getDateTo())){
			log.saveError("InvalidDateRange", "");
			return false;
		}
		return true;
	}
	
	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;

        DocumentEngine engine = new DocumentEngine( this,getDocStatus());

        boolean status = engine.processIt( action,getDocAction(),log );
        
        return status;
	}

	@Override
	public boolean unlockIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean invalidateIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String prepareIt() {
		// No existen líneas
		String whereClauseLines = "m_brochure_id = ?";
		if (!existRecordFor(getCtx(), X_M_BrochureLine.Table_Name,
				whereClauseLines, new Object[] { getID() }, get_TrxName())) { 
            m_processMsg = "@NoLines@";
            return DocAction.STATUS_Invalid;
        }
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean approveIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rejectIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String completeIt() {
		setProcessed( true );
        setDocAction( DOCACTION_Close );
        return DocAction.STATUS_Completed;
	}

	@Override
	public boolean postIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean voidIt() {
		setProcessed( true );
        setDocAction( DOCACTION_None );

        return true;
	}

	@Override
	public boolean closeIt() {
		setProcessed( true );
        setDocAction( DOCACTION_None );

        return true;
	}

	@Override
	public boolean reverseCorrectIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reActivateIt() {
		setDocAction( DOCACTION_Complete );
        setProcessed( false );

        return true;
	}

	@Override
	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDoc_User_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getC_Currency_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProcessed( boolean processed ) {
        super.setProcessed( processed );

        if( getID() == 0 ) {
            return;
        }

        String set = "SET Processed='" + ( processed
                                           ?"Y"
                                           :"N" ) + "' WHERE M_Brochure_ID=" + getID();
        DB.executeUpdate( "UPDATE M_BrochureLine " + set,get_TrxName());
    }    // setProcessed
}
