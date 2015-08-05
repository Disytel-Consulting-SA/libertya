package org.openXpertya.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DisplayType;

public class ExportProcessGeneral extends SvrProcess {

	private Integer AD_Org_ID;
	private int AD_ExpFormat_ID;
	private Date Date;
	private Date Date_To;
	/** Contexto local */
	private Properties localCtx;

	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("AD_Org_ID")) {
				setAD_Org_ID(para[i].getParameterAsInt());
			} else if (name.equals("AD_ExpFormat_ID")) {
				setAD_ExpFormat_ID(para[i].getParameterAsInt());
			} else if (name.equals("Date")) {
				setDate((Date) para[i].getParameter());
				setDate_To((Date) para[i].getParameter_To());
			}
		}
	}

	protected String doIt() throws Exception {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		List<String> parametersNames = new ArrayList<String>();
		HashMap<String, Integer> parametersClass = new HashMap<String, Integer>();
		
		if ( (getAD_Org_ID() != null) && (getAD_Org_ID() > 0) ){
			parameters.put("AD_Org_ID", getAD_Org_ID());
			parametersNames.add("AD_Org_ID");
			parametersClass.put("AD_Org_ID", DisplayType.Integer);	
		}
		
		if (getDate() != null) {
			parameters.put("Date", getDate());
			parametersNames.add("Date");
			parametersClass.put("Date", DisplayType.Date);
		}
		if (getDate_To() != null) {
			parameters.put("Date_To", getDate_To());
			parametersNames.add("Date_To");
			parametersClass.put("Date_To", DisplayType.Date);
		}

		ExportProcessGeneralFormat exportProcess = new ExportProcessGeneralFormat(
				getCtx(), getAD_ExpFormat_ID(), parameters, parametersNames,
				parametersClass, get_TrxName());

		return exportProcess.doIt();
	}

	public Integer getAD_Org_ID() {
		return AD_Org_ID;
	}

	public void setAD_Org_ID(Integer aD_Org_ID) {
		AD_Org_ID = aD_Org_ID;
	}

	public int getAD_ExpFormat_ID() {
		return AD_ExpFormat_ID;
	}

	public void setAD_ExpFormat_ID(Integer AD_ExpFormat_ID) {
		this.AD_ExpFormat_ID = AD_ExpFormat_ID;
	}

	public Date getDate() {
		return Date;
	}

	public void setDate(Date date_From) {
		Date = date_From;
	}

	public Date getDate_To() {
		return Date_To;
	}

	public void setDate_To(Date date_To) {
		Date_To = date_To;
	}

	@Override
	public Properties getCtx() {
		if (localCtx != null) {
			return localCtx;
		} else {
			return super.getCtx();
		}
	}

	public void setLocalCtx(Properties ctx) {
		localCtx = ctx;
	}

}
