package org.openXpertya.apps;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.awt.Container;

import org.openXpertya.model.*;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


/**
 * Class description:
 * Action performed When the button Generate Pay is push
 * We dont want this into the process because we need 
 * a more complex User Interface.
 *
 * @author Zarius - Dataware - 31/07/2006
 *
 * @param ctx context
 * @param WindowNo window id
 * @param mTab Tab id
 * @param mField Field id
 * @param value value of field
 *
 */
public class GeneratePays extends CalloutEngine {
	
	//** Properties **//
	Properties ctx=null;
	
	//** Log to display error mesage in console **//
	private static CLogger log = CLogger.getCLogger( AWindow.class );
	
	//** ID of the current Calendar working with **//
	private static int Calendar_ID = 0;
	
	//** Name of the current Year working with **//
	private static String Year = "";
	
	//** ID of the current Year working with **//
	private static int Year_ID = 0;
	
	//** ID of the salary row **//
	private static int Salary_ID = 0;
	
	/**
	 * Method description: * Check if the period are generated automaticly
	 * 
	 * @author Zarius - Dataware - 31/07/2006
	 *
	 * @return boolean
	 * 
	 **/
	public void constructor( Properties prop,int WindowNo,MTab mTab ){
		//** Setting the main values **//
		ctx=prop;
		Salary_ID= Env.getContextAsInt( ctx,WindowNo,"C_BPartner_Salary_ID");
		Year_ID=Env.getContextAsInt( ctx,WindowNo,"C_Year_ID");
		Year=GetYearName(Year_ID);
		Calendar_ID=Env.getContextAsInt(  ctx,WindowNo,"C_Calendar_ID");
		
		//** Saving info **//
		X_C_BPartner_Salary Salary = new X_C_BPartner_Salary(ctx, Salary_ID, null);
		Salary.save();
		
		//** Setting conditions **//
		boolean isAllreadyCreated = checkGeneratedPaids();
		boolean AutoPeriods = checkAutoPeriods();
		
		Container c = new Container();
		if (isAllreadyCreated==true){
			if(ADialog.ask(1,c," Los periodos ya existen. \n Se perderán los antiguos periodos no pagados.", null)){
				if (AutoPeriods){
					//Yes: 	Build FinalSQL (insert new pays) 	-1
					GenerateExist();
				}else{
					//No:	msg nothing done					-2
					ADialog.info(1,c,"Error: Los periodos no se han generado automáticamente", null);
				}
			}
		}else{
			if(!ADialog.ask(1,c,"Desea generar las pagas extra de Julio y Diciembre?", null)){
				if (AutoPeriods){
					//Yes: 	Build FinalSQL (12) 				-4
					GenerateNew(12);
				}else{
					//No:	msg nothing done					-5
					ADialog.info(1,c,"Error: Los periodos no se han generado automáticamente", null);
				}
			}else{
				if (AutoPeriods){
					//Yes: 	Build FinalSQL (14) 				-6
					GenerateNew(14);
				}else{
					//No:	msg nothing done					-7
					ADialog.info(1,c,"Error: Los periodos no se han generado automáticamente", null);
				}
			}
		}
	}
	
	/**
	 * Method description: * Check if the period are generated automaticly
	 * 
	 * @author Zarius - Dataware - 31/07/2006
	 *
	 * @param Year_ID 
	 *
	 * @return boolean
	 * 
	 **/
	private boolean checkAutoPeriods(){
		String[] Period = {"Jan-", "Feb-", "Mar-", "Apr-", "May-", "Jun-", "Jul-", "Aug-", "Sep-", "Oct-", "Nov-", "Dec-"};
		String[] PeriodEs = {"Ene-", "Feb-", "Mar-", "Abr-", "May-", "Jun-", "Jul-", "Ago-", "Sep-", "Oct-", "Nov-", "Dic-"};
		
		int counter=0;
		int i=0;
		while (i<12){
			Period[i]=(Period[i] + Year.substring(2,4)).toLowerCase();
			PeriodEs[i]=(PeriodEs[i] + Year.substring(2,4)).toLowerCase();
			i++;
		}

		String sql=
			"SELECT name " +
			"FROM c_period " +
			"WHERE c_year_id=? ";
		
		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		try {
			pstmt.setInt(1,Year_ID);
			
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()){
				for( i=0; i<12;i++){
					if (Period[i].equals(rs.getString(1).toLowerCase()) || PeriodEs[i].equals(rs.getString(1).toLowerCase())){
							counter++;
						}
					}
				}
			rs.close();
			if (counter==12){
				log.log(Level.SEVERE,"Auto-Periods = true");
				return true;
			}
			else{
				log.log(Level.SEVERE,"Auto-Periods = false");
				return false;
			}
		}
		catch(Exception e){
			log.log(Level.SEVERE,"Unreachable error: " + e.toString());
			return false;
		}
	}
	
	/**
	 * Method description: * Check if paid for 1 user, 1 calendar, 1 year exist
	 * 
	 * @author Zarius - Dataware - 31/07/2006
	 *
	 * @param Year_ID 
	 * @param Calendar_ID
	 * @param User_ID
	 *
	 * @return boolean
	 * 
	 **/
	private boolean checkGeneratedPaids(){
		
		String sql=
			"SELECT count(*) " +
			"FROM c_bpartner_pay " +
			"WHERE c_year_id=? " +
			"AND c_calendar_id=?" +
			"AND c_bpartner_salary_id=?";
		
		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		try {
			pstmt.setInt(1,Year_ID);
			pstmt.setInt(2,Calendar_ID);
			pstmt.setInt(3,Salary_ID);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()){
				if (rs.getInt(1)!=0){
					rs.close();
					log.log(Level.SEVERE,"Paid are already Generated = true");
					return true;
				}else{
					rs.close();
					log.log(Level.SEVERE,"Paid are already Generated = false");
					return false;
				}		
			}
			log.log(Level.SEVERE,"Paid are already Generated = false");
			return false;
		}
		catch(Exception e){
			log.log(Level.SEVERE,"Unreachable error: " + e.toString());
			return false;
		}
	}
	
	/**
	 * Method description: * Get Year name
	 * 
	 * @author Zarius - Dataware - 31/07/2006
	 *
	 * @param Year_ID 
	 *
	 * @return String
	 * 
	 **/
	private String GetYearName(int Year_ID){
		String name="Unreachable";
		String sql=
			"SELECT year " +
			"FROM c_year " +
			"WHERE c_year_id=?";
		
		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		try {
			pstmt.setInt(1,Year_ID);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()){
				name=rs.getString(1);
				rs.close();
			}
		}
		catch(Exception e){
			name="Error";
			log.log(Level.SEVERE,"Unreachable error: GeneratePays.GetYearName(" + Year_ID + ")" + e.toString());
		}
		return name;
	}	

	/**
	 * Method description: * Generate When Pay exist
	 * 
	 * @author Zarius - Dataware - 31/07/2006
	 *
	 * @param 12 or 14
	 * 
	 **/
	private void GenerateExist(){
		//Getting Salary
		X_C_BPartner_Salary Salary = new X_C_BPartner_Salary(ctx, Salary_ID, null);
		
		//Whe need to know the number of pay
		int PayNum=0;
		String sql="" +
		"SELECT count(*) " +
		"FROM c_bpartner_pay Pa " +
		"WHERE Pa.c_bpartner_salary_id=? " +
		"AND Pa.c_year_id=? " +
		"AND Pa.paid='N' " +
		"AND Pa.c_calendar_id=? ";
		
		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		try {
			pstmt.setInt(2,Year_ID);
			pstmt.setInt(3,Calendar_ID);
			pstmt.setInt(1,Salary_ID);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()){
				PayNum=rs.getInt(1);
				rs.close();
			}
		}
		catch(Exception e){
			log.log(Level.SEVERE,"Unknow error: " + e.toString());
		}

		
		//Antiguo Paga=aumento_Salario/numPagas
		BigDecimal Netpay=Percent(Salary.getNetSalary(),Salary.getRaise());
		Netpay=Netpay.divide(BigDecimal.valueOf(PayNum),10, BigDecimal.ROUND_HALF_EVEN );
		
		BigDecimal Grosspay=Percent(Salary.getSalary(),Salary.getRaise());
		Grosspay=Grosspay.divide(BigDecimal.valueOf(PayNum),10, BigDecimal.ROUND_HALF_EVEN );
		
		//For each row we update
		sql="" +
		"SELECT Pa.c_bpartner_pay_id, Pa.concept, Pe.periodno " +
		"FROM c_bpartner_pay Pa, c_period Pe " +
		"WHERE Pa.c_bpartner_salary_id=? " +
		"AND Pa.c_year_id=? " +
		"AND Pe.c_period_id=Pa.c_period_id " + 
		"AND Pa.paid='N' " +
		"AND Pa.c_calendar_id=? ";
		
		if (Salary.getRaiseAppDate()!=null){
			sql=sql + "AND  ( Pe.startdate>=? or enddate>=? )";
			sql=sql + "order by periodno";
		}
		
		pstmt = DB.prepareStatement( sql );
		
		try {
			pstmt.setInt(2,Year_ID);
			pstmt.setInt(3,Calendar_ID);
			pstmt.setInt(1,Salary_ID);
			
			int day=1;
			
			if (Salary.getRaiseAppDate()!=null){
				pstmt.setTimestamp(4,Salary.getRaiseAppDate());
				pstmt.setTimestamp(5,Salary.getRaiseAppDate());
				
				Calendar cld = Calendar.getInstance();
				cld.setTime(Salary.getRaiseAppDate());
				day=cld.get(Calendar.DAY_OF_MONTH);
			}
			
			
			
			ResultSet rs = pstmt.executeQuery();
			//for each c_bpartner_pay row
			int i = 0;
			while (rs.next()){
		
				X_C_BPartner_Pay Pay=new X_C_BPartner_Pay(ctx, rs.getInt(1), null); 

				if (i==0 && day!=1){
					Pay.setGrossPay(GenerateMiddlePay(Grosspay, Pay.getGrossPay(), day).setScale(2, Grosspay.ROUND_HALF_EVEN));
					Pay.setNetPay(GenerateMiddlePay(Netpay, Pay.getNetPay(), day).setScale(2, Grosspay.ROUND_HALF_EVEN));	
				}else{
					Grosspay=Grosspay.setScale(2, Grosspay.ROUND_HALF_EVEN);
					Pay.setGrossPay(Grosspay);
					
					Netpay=Netpay.setScale(2, Netpay.ROUND_HALF_EVEN);
					Pay.setNetPay(Netpay);
				}
				i++;
				Pay.save();
			}
			rs.close();
		}
		catch(Exception e){
			log.log(Level.SEVERE,"Unknow error: " + e.toString());
		}
	}

	/**
	 * Method description: * Generate New
	 * 
	 * @author Zarius - Dataware - 31/07/2006
	 *
	 * @param 12 or 14
	 * 
	 **/
	private void GenerateNew(int nbr){
		//Getting Salary
		X_C_BPartner_Salary Salary = new X_C_BPartner_Salary(ctx, Salary_ID, null);
		
		BigDecimal Netpay=Salary.getNetSalary();
		BigDecimal Grosspay=Salary.getSalary();

		Netpay=Netpay.divide(BigDecimal.valueOf(nbr),10, BigDecimal.ROUND_HALF_EVEN );
		Grosspay=Grosspay.divide(BigDecimal.valueOf(nbr),10, BigDecimal.ROUND_HALF_EVEN );

		String sql="" +
		"SELECT c_period_id, periodno " +
		"FROM c_period " + 
		"WHERE c_year_id=? ";
		
		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		int summerID=0;
		int christmasID=0;
		
		try {
			pstmt.setInt(1,Year_ID);
			ResultSet rs = pstmt.executeQuery();
			
			int i=0;
			while (rs.next()){
				
				if (rs.getInt(2)==7)
					summerID=rs.getInt(1);
				if (rs.getInt(2)==12)
					christmasID=rs.getInt(1);
				
				X_C_BPartner_Pay NewPay= new X_C_BPartner_Pay(ctx,0,null);

				NewPay.setC_BPartner_Salary_ID(Salary.getC_BPartner_Salary_ID());
				NewPay.setPaid(false);

				NewPay.setConcept("01");//Ordinaria
					
				NewPay.setC_Year_ID(Salary.getC_Year_ID());
				NewPay.setC_Calendar_ID(Salary.getC_Calendar_ID());
				NewPay.setC_Period_ID(rs.getInt(1));	
				
				NewPay.setNetPay(Netpay);
				NewPay.setGrossPay(Grosspay);
				NewPay.save();
				i++;
			}
		}
		catch(Exception e){
			log.log(Level.SEVERE,"Unknow error: " + e.toString());
		}
		
		if (nbr==14){
			X_C_BPartner_Pay NewPay= new X_C_BPartner_Pay(ctx,0,null);

			NewPay.setC_BPartner_Salary_ID(Salary.getC_BPartner_Salary_ID());
			NewPay.setPaid(false);

			NewPay.setConcept("02");//Verano
				
			NewPay.setC_Year_ID(Salary.getC_Year_ID());
			NewPay.setC_Calendar_ID(Salary.getC_Calendar_ID());
			NewPay.setC_Period_ID(summerID);	
			
			NewPay.setNetPay(Netpay);
			NewPay.setGrossPay(Grosspay);
			NewPay.save();
			
			NewPay= new X_C_BPartner_Pay(ctx,0,null);

			NewPay.setC_BPartner_Salary_ID(Salary.getC_BPartner_Salary_ID());
			NewPay.setPaid(false);

			NewPay.setConcept("03");//Verano
				
			NewPay.setC_Year_ID(Salary.getC_Year_ID());
			NewPay.setC_Calendar_ID(Salary.getC_Calendar_ID());
			NewPay.setC_Period_ID(christmasID);	
			
			NewPay.setNetPay(Netpay);
			NewPay.setGrossPay(Grosspay);
			NewPay.save();
		}
	}
	
	/**
	 * Method description: * Generate New pay in the middle of the month
	 * 
	 * @author Zarius - Dataware - 31/07/2006
	 *
	 * @param BigDecimal NewPay, BigDecimal OldPay, int day
	 * @return BigDecimal calculated pay
	 * 
	 **/
	private BigDecimal GenerateMiddlePay(BigDecimal NewPay,  BigDecimal OldPay,  int day){

		//-- Precio: (paga_mensual_actual/30*(30-day))+(paga_mensual_antigua/30*(day))
		BigDecimal OldPayperDay=OldPay.divide(BigDecimal.valueOf(30),10, BigDecimal.ROUND_HALF_EVEN );
		BigDecimal NewPayperDay=NewPay.divide(BigDecimal.valueOf(30),10, BigDecimal.ROUND_HALF_EVEN );
		
		BigDecimal Result=
			NewPayperDay.multiply(BigDecimal.valueOf(30-day))
			.add(
			OldPayperDay.multiply(BigDecimal.valueOf(day)));
	
		return Result;
	}
	
	/**
	 * Method description: * Generate BigDecimal + %
	 * 
	 * @author Zarius - Dataware - 31/07/2006
	 *
	 * @param BigDecimal Value, BigDecimal %
	 * @return BigDecimal new_value
	 * 
	 **/
	private BigDecimal Percent(BigDecimal value,int percent){
		
		BigDecimal Percent=new BigDecimal(percent);
		
		BigDecimal tempVal=Percent.divide(BigDecimal.valueOf(100), 10, BigDecimal.ROUND_HALF_EVEN);
		
		BigDecimal newR = value.multiply(tempVal).add(value);
		
		return newR;
	}
}

