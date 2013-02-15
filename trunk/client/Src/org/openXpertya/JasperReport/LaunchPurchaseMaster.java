package org.openXpertya.JasperReport;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.PurchaseMasterDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MProductLines;
import org.openXpertya.model.MUser;
import org.openXpertya.util.Util;

public class LaunchPurchaseMaster extends JasperReportLaunch {

	/** Asociación de nombres de fechas con las fechas correspondientes */
	private Map<String, Timestamp> weekDates = new HashMap<String, Timestamp>();
	
	protected Integer getOrgID(){
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	protected Integer getBPartnerID(){
		return (Integer)getParameterValue("C_BPartner_ID");
	}
	
	protected Integer getProductLinesID(){
		return (Integer)getParameterValue("M_Product_Lines_ID");
	}
	
	
	protected Integer getDaysTo(Calendar dateWeekCalendar, int pastWeeks, boolean initialDate){
		Integer weekDay = dateWeekCalendar.get(Calendar.DAY_OF_WEEK);
		Integer daysTo = 0;
		switch (weekDay) {
		case Calendar.MONDAY:
			daysTo = (initialDate?7:1)+(7*(pastWeeks-1));
			break;
		case Calendar.TUESDAY:
			daysTo = (initialDate?8:2)+(7*(pastWeeks-1));
			break;
		case Calendar.WEDNESDAY:
			daysTo = (initialDate?9:3)+(7*(pastWeeks-1));
			break;
		case Calendar.THURSDAY:
			daysTo = (initialDate?10:4)+(7*(pastWeeks-1));
			break;
		case Calendar.FRIDAY:
			daysTo = (initialDate?11:5)+(7*(pastWeeks-1));
			break;
		case Calendar.SATURDAY:
			daysTo = (initialDate?12:6)+(7*(pastWeeks-1));
			break;
		case Calendar.SUNDAY:
			daysTo = (initialDate?13:7)+(7*(pastWeeks-1));
			break;
		default:
			break;
		}
		return daysTo*-1;
	}
	
	protected Timestamp getDateWeek(Calendar actualCalendar, int pastWeek, boolean initialDate){
		Calendar dateWeekCalendar = (Calendar)actualCalendar.clone();
		dateWeekCalendar.add(Calendar.DATE, getDaysTo(dateWeekCalendar, pastWeek, initialDate));
		return new Timestamp(dateWeekCalendar.getTimeInMillis());
	}
	
	
	protected Timestamp getDateFromWeek(Calendar actualCalendar, int pastWeek){
		return getDateWeek(actualCalendar, pastWeek, true);
	}
	
	protected Timestamp getDateToWeek(Calendar actualCalendar, int pastWeek){
		return getDateWeek(actualCalendar, pastWeek, false);
	}
	
	@Override
	protected void loadReportParameters() throws Exception {
		// Organización
		MOrg org = MOrg.get(getCtx(), getOrgID());
		addReportParameter("ORG_VALUE", org.getValue());
		addReportParameter("ORG_NAME", org.getName());
		
		// Proveedor
		MBPartner bpartner = new MBPartner(getCtx(), getBPartnerID(), get_TrxName());
		addReportParameter("BPARTNER_VALUE", bpartner.getValue());
		addReportParameter("BPARTNER_NAME", bpartner.getName());
		
		// Teléfonos del proveedor
		StringBuffer bpartnerPhones = new StringBuffer();
		MBPartnerLocation[] locations = bpartner.getLocations(false);
		for (MBPartnerLocation mbPartnerLocation : locations) {
			if(!Util.isEmpty(mbPartnerLocation.getPhone(), true)){
				bpartnerPhones.append(mbPartnerLocation.getPhone());
				bpartnerPhones.append(" - ");
			}
			if(!Util.isEmpty(mbPartnerLocation.getPhone2(), true)){
				bpartnerPhones.append(mbPartnerLocation.getPhone2());
				bpartnerPhones.append(" - ");
			}
			if(!Util.isEmpty(mbPartnerLocation.getFax(), true)){
				bpartnerPhones.append("FAX: ");
				bpartnerPhones.append(mbPartnerLocation.getFax());
				bpartnerPhones.append(" - ");
			}
		}
		addReportParameter("BPARTNER_PHONES", bpartnerPhones.toString());
		
		// Teléfonos de contactos
		StringBuffer contactPhones = new StringBuffer();
		MUser[] contacts = bpartner.getContacts(false);
		for (MUser mUser : contacts) {
			if(!Util.isEmpty(mUser.getPhone(), true)){
				contactPhones.append("TEL: ");
				contactPhones.append(mUser.getPhone());
				contactPhones.append(" - ");
			}
			if(!Util.isEmpty(mUser.getPhone2(), true)){
				contactPhones.append("TEL: ");
				contactPhones.append(mUser.getPhone2());
				contactPhones.append(" - ");
			}
			if(!Util.isEmpty(mUser.getphone3(), true)){
				contactPhones.append("TEL: ");
				contactPhones.append(mUser.getphone3());
				contactPhones.append(" - ");
			}
			if(!Util.isEmpty(mUser.getFax(), true)){
				contactPhones.append("FAX: ");
				contactPhones.append(mUser.getFax());
				contactPhones.append(" - ");
			}
		}
		addReportParameter("CONTACT_PHONE", contactPhones.toString());
		
		// Línea de artículo
		if(!Util.isEmpty(getProductLinesID(), true)){
			MProductLines lines = new MProductLines(getCtx(), getProductLinesID(), get_TrxName());
			addReportParameter("PRODUCT_LINES_VALUE", lines.getValue());
			addReportParameter("PRODUCT_LINES_NAME", lines.getName());			
		}

		// Días de inicio y fin de las semanas anteriores a la fecha actual
		Calendar actualCalendar = Calendar.getInstance();
		for (int i = 1; i < 5; i++) {
			getWeekDates().put("DATE_FROM_WEEK_"+i, getDateFromWeek(actualCalendar, i));
			getWeekDates().put("DATE_TO_WEEK_"+i, getDateToWeek(actualCalendar, i));
			addReportParameter("DATE_FROM_WEEK_"+i, getWeekDates().get("DATE_FROM_WEEK_"+i));
			addReportParameter("DATE_TO_WEEK_"+i, getWeekDates().get("DATE_TO_WEEK_"+i));
		}
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new PurchaseMasterDataSource(getOrgID(), getBPartnerID(),
				getProductLinesID(), getWeekDates(), get_TrxName());
	}

	protected Map<String, Timestamp> getWeekDates() {
		return weekDates;
	}

	protected void setWeekDates(Map<String, Timestamp> weekDates) {
		this.weekDates = weekDates;
	}

}
