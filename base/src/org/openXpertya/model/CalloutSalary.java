package org.openXpertya.model;

import java.util.Properties;
import java.util.Date;
import java.util.logging.Level;

import java.sql.*;
import java.text.SimpleDateFormat;
import org.openXpertya.util.DB;

/**
 * Class description:
 * Callout asigned to RaiseAppDate field from
 * C_BPartner_Salary table
 *
 * @author Zarius - Dataware - 31/07/2006
 *
 * @param ctx
 * @param WindowNo
 * @param mTab
 * @param mField
 * @param value
 *
 */
public class CalloutSalary extends CalloutEngine {
	/**
	 * Method Description
	 * Confirm that the selectted year match with the current year
	 * if not, set default as 01/01/<current year>
	 * 
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 *
	 * @return error if it append
	 */
	public String confirmYear( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ){
		
		//Get current working data and save into NewSalary to work with.
		Integer Salary_ID = ( Integer ) mTab.getValue("C_BPartner_Salary_ID");
		X_C_BPartner_Salary NewSalary = new X_C_BPartner_Salary(ctx, Salary_ID.intValue(), null);
		
		//Get year of the RaiseAppDate Column
		String RaiseAppDate_Year = mTab.getValue("RaiseAppDate").toString().substring(0,4);


		//Get year of the Year Column
		String Selected_Year="";
		String sql=
		
		"SELECT year " +
		"FROM C_Year " +
		"WHERE c_year_id=?";
		
		try
		{
			PreparedStatement pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,NewSalary.getC_Year_ID());
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next())
            	Selected_Year=rs.getString(1);
            else
            	return "El año seleccionado no existe";
            rs.close();
		}
		catch(Exception e){
				log.log( Level.SEVERE,"Error on org.openXpertya.model.CalloutSalary.confirmYear line 70",e );
				return "Error at org.openXpertya.model.CalloutSalary.confirmYear lines(12-42)";
		}
		
		//Comparing the two years:
		if (Selected_Year.equals(RaiseAppDate_Year))
			return "";
		else{
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
				Date date = sdf.parse(Selected_Year + "-01-01 00:00:00.000");
				Timestamp timestamp = new java.sql.Timestamp(date.getTime());
				mTab.setValue("RaiseAppDate", timestamp);
			}
			catch(Exception E){
				log.log( Level.SEVERE,"Error on org.openXpertya.model.CalloutSalary.confirmYear line 85",E );
				return "Error ejecutando actualización de Timestamp";
			}
			
			return "La fecha de aplicación de subida no se corresponde con el año seleccionado." +
			" Se ha seleccionado por defecto 01/01/" + Selected_Year;
		}
	}
}
