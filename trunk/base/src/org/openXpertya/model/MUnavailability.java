package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class MUnavailability extends X_C_Unavailability {
	
    public MUnavailability( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    } // MUnavailability
    
    public MUnavailability( Properties ctx,int C_Unavailability_ID,String trxName ) {
    	super( ctx,C_Unavailability_ID,trxName );
    } // MUnavailability
    
    protected boolean beforeSave( boolean newRecord ) {

    	if (getC_Period_ID()==0){
    		//Aki verifico el año:
    		if (getC_Year_ID()==0){
    			return true;
    		}else{
	    		if (YearCompare()==false){
	    			log.saveError( "Error","El año seleccionado no se corresponde con los periodos");
	    			return false;
	    		}else{
	    			return true;
	    		}
    		}
    	}
    	
    	int finalResult=0;
    	
    	if(DateCompare()==false)
    		finalResult=1;
    	if(PeriodCompare()==false)
    		finalResult=2;
    	
    	if (finalResult==0){
    		return true;
    	}else{
    		if (finalResult==1){
    			log.saveError( "Error","La fecha inicial no puede ser superior a la fecha final");
    		}else if(finalResult==2){
    			log.saveError( "Error","El periodo no se corresponde con las fechas seleccionadas");
    		}
    		return false;
    	}	
    } // beforeSave
    
    private boolean DateCompare(){
  		
  		// comprobamos las fechas
    	int diff = getDateTo().compareTo(getDateFrom());
  		if (diff < 0){
  			log.saveError("Error: ", "La fecha 'Desde' debe ser igual o posterior que la fecha 'Hasta'");
  			return false;	
  		}
  		
  		// Buscamos si el existe un limite de indisponibilidad exactamente igual al que vamos a grabar
  		// Teniendo en cuenta la tabla el registro, el calendario, el año y el periodo
  		String sql = "SELECT Quantity FROM C_Unavailability_Limit WHERE AD_Table_ID=? AND Record_ID=? AND C_Calendar_ID = ? AND C_Year_ID = ? AND C_Period_ID = ?";

		int limite = 0;
			
        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,getAD_Table_ID() );
            pstmt.setInt( 2,getRecord_ID() );
            pstmt.setInt( 3,getC_Calendar_ID() );
            pstmt.setInt( 4,getC_Year_ID() );
            pstmt.setInt( 5,getC_Period_ID() );
            
            ResultSet rs = pstmt.executeQuery();

            while ( rs.next()) {
            	limite = limite + rs.getInt( 1 );
            }
            
            long difms = getDateTo().getTime() - getDateFrom().getTime();
            long diferencia = difms / (1000 * 60 * 60 * 24);
            
            if (diferencia > limite && limite!=0){
            	log.saveError("Error: ", "El maximo del limite configurado es superior que el de este tipo de registro.");
      			return false;
            }
            
            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }
  		
    	return true;
    }

    private boolean PeriodCompare(){
    	
    	String sql=
    		"SELECT count(*) " +
    		"FROM C_Period " +
    		"WHERE (? BETWEEN startdate AND enddate) AND " +
    			"(? BETWEEN startdate AND enddate) " +
    			"AND C_Period_ID=? ";
    	
        log.log(Level.SEVERE, 	"SQL by Zarius: SELECT count(*) " +
        		"FROM C_Period " +
        		"WHERE (" + getDateFrom() + " BETWEEN startdate AND enddate) AND " +
        			"(" + getDateTo() + " BETWEEN startdate AND enddate) " +
        			"AND C_Period_ID=" + getC_Period_ID());
    	
        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setTimestamp( 1, getDateFrom());
            pstmt.setTimestamp( 2,getDateTo());
            pstmt.setInt( 3,getC_Period_ID());
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next() && rs.getInt(1)==1){
            	rs.close();
            	return true;
            }else{
            	rs.close();
            	return false;
            }
        }
        catch(Exception e){
        	return false;
        }
    }
    private boolean YearCompare() {
    
    	String Year="";
    	
    	String sql=
    		"SELECT Year " + 
    		"FROM C_Year " +
    		"WHERE C_Year_ID=?";
    	
    	try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            
            pstmt.setInt( 1,getC_Year_ID());
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()){
            	Year=rs.getString(1);
            	rs.close();
            }else{
            	return false;
            }

    	}
    	catch(Exception e){
        	return false;
    	}
    	
    	sql=
    		"SELECT Y.Year " +
    		"FROM c_period P, C_Year Y " +
    		"WHERE (  ? BETWEEN P.startdate AND P.enddate) " +
    			"AND ( ?  BETWEEN P.startdate AND P.enddate ) " +
    			"AND Y.C_Calendar_ID=? " +
    			"AND Y.C_Year_ID=P.C_Year_ID ";
	
    	log.log(Level.SEVERE, "SQL by Zarius: " +
        		"SELECT Y.Year " +
        		"FROM c_period P, C_Year Y " +
        		"WHERE (  '" + getDateFrom() + "' BETWEEN P.startdate AND P.enddate) " +
        			"AND ( '" + getDateTo() + "'  BETWEEN P.startdate AND P.enddate ) " +
        			"AND Y.C_Calendar_ID=" + getC_Calendar_ID() +
        			"AND Y.C_Year_ID=P.C_Year_ID ");
    			
        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            
            pstmt.setTimestamp( 1, getDateFrom());
            pstmt.setTimestamp( 2,getDateTo());
            pstmt.setInt( 3, getC_Calendar_ID());

            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()){
            	if (rs.getString(1).equals(Year)){
	            	rs.close();
	            	return true;
            	}else{
            		return false;
            	}
            }else{
            	rs.close();
            	return false;
            }
        }
        catch(Exception e){
        	return false;
        }
    }
}
