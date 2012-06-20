package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

//import org.openXpertya.apps.ADialog;
import org.openXpertya.model.X_C_BPartner_Salary;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 29.06/2006
 * @author     Dataware    
 */
public class GenerateRaise extends SvrProcess {
	
	private String error="";
	private int p_Record_ID = 0;
	private Properties m_ctx = Env.getCtx();

	int exist=2;
	int anio=0;
	boolean overWrite=false;
	
	/**
	 * Descripción de Clase
	 * Debemos mirar si existe una paga para el año siguiente,
	 * ||-> si existe preguntamos si machacamos
	 * 		||-> Si dice que si sobreescribimos		exist=1 && otra var==true
	 * 		||-> Si no cancelamos					exist>1 || otra var==false
	 * ||-> si no existe directamente creamos		exist=0
	 *
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */
	protected void prepare() {
		p_Record_ID = getRecord_ID();
		

		X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());

		if (!ActualSalary.isBlocked()){
			//Verificamos que Exista una paga para el año siguiente
			String sql="Select year from c_year where c_year_id=?";
			PreparedStatement pstmt = DB.prepareStatement( sql );
			
			try{
			    pstmt.setInt(1,ActualSalary.getC_Year_ID());
			    
			    ResultSet rs = pstmt.executeQuery();
			    
			    //anio contiene el año +1
			    if (rs.next()){
			    	anio=Integer.valueOf(rs.getString(1)).intValue();
			    	anio=anio-(-1);
			    	rs.close();
			    	
			    	//depende del usuario
			    	sql="Select count(*) ";
			    	sql=sql + "from c_year Y, c_bpartner P, c_bpartner_salary S ";
			    	sql=sql + "where Y.year=? and P.c_bpartner_id=? and S.c_bpartner_id=P.c_bpartner_id and S.c_year_id=Y.c_year_id";
			    	
			    	pstmt = DB.prepareStatement( sql );
			    	pstmt.setString(1,Integer.toString(anio));
			    	pstmt.setInt(2,ActualSalary.getC_BPartner_ID());
			    	//alamacenamos en exist cual de los 3 casos vamos a manejar
				    try{
				    	rs = pstmt.executeQuery();
				    	if (rs.next()){
				    		exist=rs.getInt(1);
				    	}
				    	rs.close();
				    }catch (Exception e){
				    	error="Error entre lineas 70-77";
				    }
				    
			    	if (YearExist(anio))
			    	{
			    		exist=10;
			    	}else
			    	{
					    if (exist==1){
					    	overWrite=true;
					    }
			    	}
			    }
			    
			    
			}catch (Exception e){
				error=error + ". Error entre lineas 50-92 " + e.toString();
			}
		}
		
	}
	protected String doIt() throws java.lang.Exception {
		String Rfinal="";
		X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());
		if (!ActualSalary.isBlocked()){

			//manejamos la superioridad de Raise
			if (ActualSalary.getRaise()>25){
		    		exist=11;
			}
			
			
			if (exist==0){									//Creamos
				if(createNew())
				{
					Rfinal= "  Creado correctamente. " + error;
					ActualSalary.setisBlocked(true);
				}
				else
					Rfinal= "Error al crear nueva fila";
				}else if(exist==1 && overWrite==true){			//Sobreescribimos
					if(OverWrite()){
						Rfinal= "  Sobreescrito correctamente" + error;
						ActualSalary.setisBlocked(true);
					}
				else
					Rfinal="  Error al sobreescribir";
				}else if(exist==1 && overWrite==false){			//Cancelado
						Rfinal= "  Operación cancelada por el usuario. " + error;
				}else if(exist==10){
						Rfinal= "  Operación cancelada por el sistema: No existe el año " + anio + " en el calendario seleccionado.";
				}else if (exist==11){
						Rfinal= "Cancelado por el usuario aumento erroneo";
				}
				else{
					if (error.equals("")){
						Rfinal= "  Operación cancelada por el sistema: Ya existe un salario para " +  getname() + " en " + anio + ".";
				}
			}
			
			ActualSalary.save();

		}else
		{
			Rfinal="Ya se generó el aumento para este empleado en las fechas seleccionadas";
		}
		return Rfinal;
	}
	/**
	 * Descripción de Clase
	 * Obtiene el nombre mediante el id de cbpartner.
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */
	private String getname(){
		X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());
		String sql="Select name from c_bpartner where c_bpartner_id=?";
		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		try{
		    pstmt.setInt(1,ActualSalary.getC_BPartner_ID());
		    ResultSet rs = pstmt.executeQuery();
		    if (rs.next())
		    	return rs.getString(1);
		    rs.close();
		}
		catch(Exception e){
			return "unnamed";
		}
		return "unnamed";
	}
	/**
	 * Descripción de Clase
	 * crea una nueva fila aumentando el salario y para el año siguiente (si es que exsite el año siguiente)
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */	
	private boolean createNew(){
		try{
			X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());
			X_C_BPartner_Salary NewSalary = new X_C_BPartner_Salary(m_ctx,0,get_TrxName());
			

			NewSalary.setIsActive(true);
			NewSalary.setC_BPartner_ID(ActualSalary.getC_BPartner_ID());
			
			//--Calculamos el nuevo sueldo:
			BigDecimal Raise= new BigDecimal (ActualSalary.getRaise());
	
			NewSalary.setSalary(FinalSalary(ActualSalary.getSalary(), Raise));
			NewSalary.setNetSalary(FinalSalary(ActualSalary.getNetSalary(), Raise));
			//--FIN del nuevo sueldo
			
			NewSalary.setC_Currency_ID(ActualSalary.getC_Currency_ID());
			NewSalary.setC_Calendar_ID(ActualSalary.getC_Calendar_ID());
			
			//Necesitamos la ID del nuevo año
			int AnioId=NewYearID();
			Calendar cld = Calendar.getInstance();
			cld.set( getYear(AnioId), 0, 1, 24, 0 ,0);			
			Timestamp ts = new Timestamp(cld.getTimeInMillis());   	
			NewSalary.setTIMESTAMP(ts);
			
			NewSalary.setC_Year_ID(AnioId);
			NewSalary.setRaise(0);
			NewSalary.save();
			return true;
		}
		catch(Exception e){
			return false;
		}
		
	}
	/**
	 * Descripción de Clase
	 * Sobreescribe
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */
	private boolean OverWrite(){
		try{
			X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());

			//--Calculamos el nuevo sueldo:
			BigDecimal Raise= new BigDecimal (ActualSalary.getRaise());
	
			ActualSalary.setSalary(FinalSalary(ActualSalary.getSalary(), Raise));
			ActualSalary.setNetSalary(FinalSalary(ActualSalary.getNetSalary(), Raise));
			//--FIN del nuevo sueldo
			
			//Necesitamos la ID del nuevo año
			ActualSalary.setC_Year_ID(NewYearID());
			ActualSalary.save();
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	/**
	 * Descripción de Clase
	 * nos indica si el año existe para el calendaio específico.
	 * 
	 * @return boolean true=año no existe
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */	
	private boolean YearExist(int year){
		X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());
		String sql="Select count(*) from c_year where year=? and c_calendar_id=?";
		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		try{
		    pstmt.setString(1,Integer.toString(year));
		    pstmt.setInt(2,ActualSalary.getC_Calendar_ID());
		    ResultSet rs = pstmt.executeQuery();
		    if (rs.next()){
		    	if (rs.getInt(1)!=0){
		    		return false;
		    	}
	    		return true;
		    }
    		rs.close();
		}
		catch(Exception e){
			return false;
		}
		return false;
	}
	/**
	 * Descripción de Clase
	 * Calcula el nuevo salario
	 * 
	 * @return new salary
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */	
	private BigDecimal FinalSalary(BigDecimal salary, BigDecimal raise){

		//raise=raise.divide(new BigDecimal("100"));
		
		//raise.add(new BigDecimal("1"));
		
		salary=salary.add((salary.multiply(raise)).divide(new BigDecimal("100")));
		
		salary.setScale(0, BigDecimal.ROUND_UP);
		int Fsalary= salary.intValue();
		return new BigDecimal(Fsalary);
	}
	/**
	 * Descripción de Clase
	 * devuelve la clave del nuevo año 
	 * 
	 * @return int new year id
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */	
	private int NewYearID(){
		X_C_BPartner_Salary ActualSalary = new X_C_BPartner_Salary(m_ctx,p_Record_ID,get_TrxName());
		String sql="Select c_year_id from c_year where year=? and c_calendar_id=?";

		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		try{
			pstmt.setInt(1,anio);
			pstmt.setInt(2,ActualSalary.getC_Calendar_ID());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()){
				int i= rs.getInt(1);
				rs.close();
				return i;
			}
			rs.close();
		}
		catch(Exception e)
		{return 0;}
		return 0;
	}
	
	/**
	 * Descripción de Clase
	 * devuelve el año dado su id
	 * 
	 * @return int new year id
	 * @version 2.2, 29.06/2006
	 * @author     Dataware    
	 */	
	private int getYear(int id){

		int anio=0;
		
		String sql="Select year from c_year where c_year_id=?";
		PreparedStatement pstmt = DB.prepareStatement( sql );
		
		try{
			pstmt.setInt(1,id);
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
/*
 *  @(#)GenerateRaise.java   29.06.2006
 * 
 *  Fin del fichero GenerateRaise.java
 *  Generato by Zarius 
 *  Versión 2.2
 *
 */