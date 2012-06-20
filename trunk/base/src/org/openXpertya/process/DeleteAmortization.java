package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Level;

import org.openXpertya.model.MAmortization;
import org.openXpertya.model.MProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class DeleteAmortization extends SvrProcess {
	/** Table					*/
	private int AD_Table_ID;
	
	/** Record					*/
	private int AD_Record_ID;	
	
	@Override
	protected void prepare() {
		// ID de la tabla M_Amortization
		AD_Table_ID = getTable_ID();
		// ID del regsitro actual de la tabla M_Amortization
		AD_Record_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		// La consulta retorna la amortizacion con el ID AD_Record_ID
		String sql = "SELECT * FROM M_Amortization a WHERE (a.m_amortization_id ="  + AD_Record_ID + ");";
		PreparedStatement	pstmt	= null;
		MAmortization amortizacion = null;
		try {
            pstmt	= DB.prepareStatement(sql,get_TrxName());
            ResultSet	rs	= pstmt.executeQuery();
            
            if (rs.next()) {
            	amortizacion = new MAmortization(getCtx(), rs, get_TrxName());
            }
            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
        	log.log( Level.SEVERE,"doIt - " + sql,e );
        	throw new Exception( e );
        }
       
		// Si existe una amortizacion posterior procesada no se puede eliminar.
		if(MAmortization.isAmortizationProcessed(getCtx(), amortizacion, 1, get_TrxName())){
			throw new Exception(Msg.getMsg(Env.getCtx(),"CannotDeleteAmortizationExistNext"));
		}
	
		// Si el periodo de la amortizacion no esta abierto, no se puede eliminar.	
		if (!(MAmortization.getITime(getCtx(),
				MAmortization.getPeriodValueID(amortizacion), get_TrxName()))
				.isIncludedInPeriod(Env.getDate(getCtx()))) {
			throw new Exception(Msg.getMsg(Env.getCtx(),"CannotDeleteAmortization"));
		}
		
		// La consulta elimina toda la informacion contable de la tabla fact_acct que posee
		// el registro AD_Record_ID de la tabla M_Amortization
		sql = "DELETE FROM fact_acct f WHERE ((f.ad_table_id =" + AD_Table_ID + ") and (f.record_id ="+ AD_Record_ID + "))";
		DB.executeUpdate( sql,get_TrxName());
		
		// La consulta elimina todas las lineas de la amortizacion de la tabla M_AmortizationLine que pertenecen a la amortizacion con ID AD_Record_ID
        sql = "DELETE FROM M_AmortizationLine l WHERE (l.m_amortization_id ="  + AD_Record_ID + ");";
        DB.executeUpdate( sql,get_TrxName());
        
        // La consulta elimina la amortizacion con ID AD_Record_ID de la tabla M_Amortization
        sql = "DELETE FROM M_Amortization a WHERE (a.m_amortization_id ="  + AD_Record_ID + ");";
        DB.executeUpdate( sql,get_TrxName());

        // La consulta retorna el AD_Process_ID del proceso Balance_Update
		sql = "SELECT AD_Process_ID FROM AD_Process where (value = 'Balance_Update')";
		pstmt	= null;
		Integer	process_ID = null;
		try {
            pstmt	= DB.prepareStatement(sql,get_TrxName());
            ResultSet	rs	= pstmt.executeQuery();
            
            if (rs.next()) {
                process_ID	= new Integer(rs.getInt(1));
            }
            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
        	log.log( Level.SEVERE,"doIt - " + sql,e );
        	throw new Exception( e );
        }
        HashMap<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("C_AcctSchema_ID", Env.getContextAsInt(getCtx(), "$C_AcctSchema_ID"));
        parameters.put("IsRecreate", "Y");
		      
        // Ejecuto el proceso Actualizar Balance Contable para actualizar el balance luego de haber 
        // borrado informacion contable.
        ProcessInfo pi = MProcess.execute(getCtx(), process_ID, parameters, get_TrxName());
        
        if (pi.isError()){
        	log.severe( "Error - " + pi );
        	throw new Exception(pi.getSummary());
        }
			        
        return Msg.getMsg(Env.getCtx(),"ProcessOK");
	}

}
