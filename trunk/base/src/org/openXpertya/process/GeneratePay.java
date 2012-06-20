package org.openXpertya.process;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

//import org.openXpertya.apps.ADialog;
import org.openXpertya.model.X_C_BPartner_Pay;
import org.openXpertya.model.X_C_BPartner_Salary;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class GeneratePay extends SvrProcess {
	
	private int p_Record_ID = 0;
	private boolean Automatic=false;
	int Nperiods=0;
	
	private int NperiodNoAuto=0;
	
	private Properties m_ctx = Env.getCtx();
	private int[][] period= new int[12][2];
	/**
	 * Descripción de Clase
	 * prepara el evento: 
	 *  - recogemos si son 12 o 14 pagas.
	 *  - si no hay periodos error.
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */	
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		
		p_Record_ID = getRecord_ID();
		
		if (PayAlreadyGenerated()==false){
			Nperiods=0;
		}else
		{
			if (AutoPeriod()){
				
				Automatic=true;
				Nperiods = 12;
				
		        for( int i = 0;i < para.length;i++ ) {
		            int name = para[ i ].getParameterAsInt();
	
		            if( para[ i ].getParameter() == null ) {
		            	Nperiods = 12;
		            }else{
		            	Nperiods = name;
		            }
		        }
	
			}
			else{	//las pagas que sean
				Automatic=false;
				Nperiods=NperiodNoAuto;
				
			}
		}
	}
	/**
	 * Descripción de Clase
	 * Ejecuta el evento
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */	
	protected String doIt() throws java.lang.Exception {
		
		if(Nperiods==0){
			return "No se pueden crear las pagas: 'ya han sido generadas anteriormente'";
		}
	
		if (Automatic==true){
			
			if (Nperiods==12){
				GenerateAllPays(12);
				return "12 Pagas Generadas";
			}
			else if (Nperiods==14)
				GenerateAllPays(14);
				return "14 Pagas Generadas";
		}
		GenerateAllPays(NperiodNoAuto);	
		
		return "Pagas generadas";
		
	}
	/**
	 * Descripción de Clase
	 * verifica que los periodos se hayan generado automáticamente
	 * 
	 * @return true si son autos
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */	
	private boolean AutoPeriod(){

		String DBnames="";
		String GNnames="";
		String GNnamesEs="";
		int number=0;
		
		X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());
		String[] Period = {"Jan-", "Feb-", "Mar-", "Apr-", "May-", "Jun-", "Jul-", "Aug-", "Sep-", "Oct-", "Nov-", "Dec-"};
		String[] PeriodEs = {"Ene-", "Feb-", "Mar-", "Abr-", "May-", "Jun-", "Jul-", "Ago-", "Sep-", "Oct-", "Nov-", "Dic-"};
		
		String sql="select P.name,P.c_period_id from C_year Y , C_Period P, C_calendar  C where P.c_year_id=Y.c_year_id and Y.c_calendar_id=C.c_calendar_id and c.c_calendar_id=? and Y.c_year_id=?";
		PreparedStatement pstmt = DB.prepareStatement( sql );

		priodRedim();

		boolean APfinal=true;
		
		try{
		    pstmt.setInt(1,ActualSalary.getC_Calendar_ID());
		    pstmt.setInt(2,ActualSalary.getC_Year_ID());
		    ResultSet rs = pstmt.executeQuery();

		    while (rs.next()){
		    	
		    	int atleast1=0;
		    	DBnames=rs.getString(1);
		    	
				for (int i=0; i<Period.length; i++){
					
					GNnames=Period[i] + getYear().substring(2);
					GNnamesEs=PeriodEs[i] + getYear().substring(2);
					
					GNnamesEs=GNnamesEs.toLowerCase();
					GNnames=GNnames.toLowerCase();
					
					if (GNnames.equals(DBnames.toLowerCase()) || GNnamesEs.equals(DBnames.toLowerCase())){
						atleast1=atleast1 + 1;
					}
				}
						
				if (atleast1!=1)
					APfinal=false;

				//estoy recorriendo mi bucle de periodos,
				//Necesito saber cual es la id del periodo correspondiente a verano e invierno
				//si es verano almaceno 1, si es invierno almaceno 2 y si es cualquier otro almaceno 0
				
				//esto es para saber cual es la id de verano e invierno
				String VMes=PeriodEs[6] + getYear().substring(2);
				String VMonth=Period[6] + getYear().substring(2);
				
				String IMes=PeriodEs[11] + getYear().substring(2);
				String IMonth=Period[11] + getYear().substring(2);
				
				if ( (getMMdif(rs.getString(1), VMes)) || (getMMdif(rs.getString(1), VMonth)) )
					period[number][1]=1;
				else if ( (getMMdif(rs.getString(1), IMes)) || (getMMdif(rs.getString(1), IMonth)) )
					period[number][1]=2;
				else
					period[number][1]=0;
				
				period[number][0]=rs.getInt(2);
				
				number++;
		    }
		    NperiodNoAuto=number;
		    //si hay más de 12 registros es que no es igual
		    if (number==12)
		    	return APfinal;
		    return false;
		}
		catch(Exception e){
			return false;
		}
	}
	private String getYear(){
		X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());
		String anio="";
		
		String sql="Select year from c_year where c_year_id=?";
		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		try{
			pstmt.setInt(1,ActualSalary.getC_Year_ID());
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				anio=rs.getString(1);}
			rs.close();
			return anio;
		    }
		catch(Exception e){
			return "0000";
		}


	}

	private void GenerateAllPays(int total){

		X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());
		
		BigDecimal Netpay=ActualSalary.getNetSalary();
		BigDecimal Grosspay=ActualSalary.getSalary();
		

		Netpay=Netpay.divide(BigDecimal.valueOf(total),0, BigDecimal.ROUND_HALF_EVEN );
		Grosspay=Grosspay.divide(BigDecimal.valueOf(total),0, BigDecimal.ROUND_HALF_EVEN );

		for (int i=0; i<total; i++){
			X_C_BPartner_Pay NewPay= new X_C_BPartner_Pay(m_ctx,0,get_TrxName());

			NewPay.setC_BPartner_Salary_ID(ActualSalary.getC_BPartner_Salary_ID());
			NewPay.setPaid(false);
			

			if (i==12 && Automatic==true)
				NewPay.setConcept("03");//verano 02 //navidad 03
			else if (i==13 && Automatic==true)
				NewPay.setConcept("02");//verano 02 //navidad 03
			else
				NewPay.setConcept("01");//Ordinaria
				
			NewPay.setC_Year_ID(ActualSalary.getC_Year_ID());
			NewPay.setC_Calendar_ID(ActualSalary.getC_Calendar_ID());
			
			if (i==12  && Automatic==true)
				NewPay.setC_Period_ID(getDecID());//verano 
			else if (i==13  && Automatic==true)
				NewPay.setC_Period_ID(getAugID());//navidad 
			else
				NewPay.setC_Period_ID(period[i][0]);
			
			NewPay.setNetPay(Netpay);
			NewPay.setGrossPay(Grosspay);
			NewPay.save();
		}
		
		
	}
	
	private int getDecID(){
		
		for (int i=0; i<period.length; i++){
			if (period[i][1]==2)
				return period[i][0];
		}
		
		return 5;
		
	}
	private int getAugID(){
		
		for (int i=0; i<period.length; i++){
			if (period[i][1]==1)
				return period[i][0];
		}
		
		return 5;
		
	}
	
	private void priodRedim(){
		X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());
		String sql="select count(*) from C_year Y , C_Period P, C_calendar  C where P.c_year_id=Y.c_year_id and Y.c_calendar_id=C.c_calendar_id and c.c_calendar_id=? and Y.c_year_id=?";
		PreparedStatement pstmt = DB.prepareStatement( sql );

		try{
		    pstmt.setInt(1,ActualSalary.getC_Calendar_ID());
		    pstmt.setInt(2,ActualSalary.getC_Year_ID());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()){
				if (rs.getInt(1)>12){
					period= new int[rs.getInt(1)][2];
				}
			}
			rs.close();	
		}
		catch(Exception e){
		}
	}
	
	private boolean getMMdif(String A, String B){
		if (A.toLowerCase().equals(B.toLowerCase()))
			return true;
		return false;
	}
	/**
	 * Descripción de Clase
	 * verifica que no se hayan generado pagas previas 
	 * Para el mismo año del mismo calendario y la misma 
	 * persona
	 * 
	 * @return false si se han generado ya las pagas
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */	
	private boolean PayAlreadyGenerated(){
		X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());
		
		String sql="select count(*) from c_bpartner_pay where c_bpartner_salary_id=? and c_year_id=? and c_calendar_id=?";
		PreparedStatement pstmt = DB.prepareStatement( sql );

		try{
			    pstmt.setInt(3,ActualSalary.getC_Calendar_ID());
			    pstmt.setInt(2,ActualSalary.getC_Year_ID());
			    pstmt.setInt(1,ActualSalary.getC_BPartner_Salary_ID());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()){
					if (rs.getInt(1)==0){
						rs.close();
						return true;
					}
					
					rs.close();
					return false;
				}
				rs.close();
				return false;
			}
		catch(Exception e){
			return false;
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
