package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;


import org.openXpertya.util.DB;


public class MBPartnerSalary extends X_C_BPartner_Salary {
	
    public MBPartnerSalary( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAlert
    
    public MBPartnerSalary( Properties ctx,int AD_Action_ID,String trxName ) {
        super( ctx,AD_Action_ID,trxName );
    }
	

	protected boolean beforeSave( boolean newRecord ) {
		/* Aqui antes de salvar tengo que establecer el 
		 * campo Date de la BDD 
		 * y le tengo que asignar el año seleccionado 
		 * 
		 * Asi en la vista puedo agrupar por dicha fecha y el calendario
		 */  	

    	//Insertamos el anio conrrespondiente:
		Calendar cld = Calendar.getInstance();
		cld.set( getYear(), 0, 0, 24, 0 ,0);			
    	Timestamp ts = new Timestamp(cld.getTimeInMillis());   	
    	setTIMESTAMP(ts);

    return true;	
    }
	private int getYear(){

		int anio=0;
		
		String sql="Select year from c_year where c_year_id=?";
		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		try{
			pstmt.setInt(1,getC_Year_ID());
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				anio=rs.getInt(1);}
			rs.close();
			return anio;
		    }
		catch(Exception e){
			return 0;
		}
	}
}
