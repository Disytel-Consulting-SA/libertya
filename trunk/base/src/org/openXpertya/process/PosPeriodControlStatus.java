package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPeriodControl;
import org.openXpertya.model.X_C_PosPeriodControl;
import org.openXpertya.util.CacheMgt;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class PosPeriodControlStatus extends SvrProcess {
	
	private int pos = 0;
	private String action;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		 for( int i = 0;i < para.length;i++ ) {
	            log.fine( "prepare - " + para[ i ] );

	            String name = para[ i ].getParameterName();

	            if( para[ i ].getParameter() == null ) {
	                ;
	            } else if( name.equalsIgnoreCase( "pos" )) {
	            	pos = para[i].getParameterAsInt();
	            } else if( name.equalsIgnoreCase( "action" )) {
	            	action = (String)para[ i ].getParameter();	            	
	            } else {
	                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
	            }
	        }
		
	}

	@Override
	protected String doIt() throws Exception {
		//1-Valido parámetros
		validateParameters();
		
		//2-Recupero el registro
		MPeriodControl periodControl = new MPeriodControl(getCtx(), getRecord_ID(), get_TrxName());
		
		//3-Recupero todos los tipos de documento que corresponda por docbasetype y por punto de venta (si corresponde)
		List<MDocType> docTypes = getDocTypes(periodControl.getDocBaseType(), pos);
		
		//4-Por cada docType, genero o actualizo la entrada correspondiente
		for (MDocType docType : docTypes) {
			X_C_PosPeriodControl ppc = find(periodControl.getC_PeriodControl_ID(), docType.getC_DocType_ID());
			if (ppc == null) {
				ppc = new X_C_PosPeriodControl(getCtx(), 0, get_TrxName());
				ppc.setC_PeriodControl_ID(periodControl.getC_PeriodControl_ID());
				ppc.setC_DocType_ID(docType.getC_DocType_ID());
			}
			
	        // No Action

	        if( MPeriodControl.PERIODACTION_NoAction.equals(action)) {
	            return "@OK@";
	        }

	        // Open

	        if( MPeriodControl.PERIODACTION_OpenPeriod.equals(action)) {
	            ppc.setPeriodStatus( MPeriodControl.PERIODSTATUS_Open );
	        }

	        // Close

	        if( MPeriodControl.PERIODACTION_ClosePeriod.equals(action)) {
	            ppc.setPeriodStatus( MPeriodControl.PERIODSTATUS_Closed );
	        }

	        // Close Permanently

	        if( MPeriodControl.PERIODACTION_PermanentlyClosePeriod.equals(action)) {
	            ppc.setPeriodStatus( MPeriodControl.PERIODSTATUS_PermanentlyClosed );
	        }

	        ppc.setPeriodAction( MPeriodControl.PERIODACTION_NoAction );
	        
			if (!ppc.save()) {
				throw new Exception(Msg.getMsg(getCtx(), "ErrorInPosPeriodGeneration") + ": " + ppc.getProcessMsg());
			}
			
			// Reset Cache

	        CacheMgt.get().reset( "C_PeriodControl",ppc.getC_Posperiodcontrol_ID() );
	        CacheMgt.get().reset( "C_PosPeriodControl",ppc.getC_Posperiodcontrol_ID());
		}

        return "@OK@";
	}
	
	private void validateParameters() throws Exception {
		if (action == null) {
            log.log( Level.SEVERE,"Se produjo un error al obtener los Parámetros del reporte." );
            throw new Exception("@ParameterMissing@");
        }
	}
	
	private List<MDocType> getDocTypes(String docBaseType, int pos) {
		List<MDocType> docTypes = new ArrayList<MDocType>();
		
		//Construyo la query
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT  ");
		sql.append("dt.* ");
		sql.append("FROM c_doctype dt ");
		sql.append("WHERE docbasetype = ? ");
		sql.append("AND ad_client_id = ? ");
		sql.append("AND open_close_by_pos = 'Y' ");
		
		if (pos > 0) {
			sql.append("  AND POSITION(TRIM(BOTH FROM to_char(?, '0000')) IN doctypekey) > 0; ");
		}
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			
			//Parámetros
			ps.setString(1, docBaseType);
			ps.setInt(2, getAD_Client_ID());
			if (pos > 0)
				ps.setInt(3, pos);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				MDocType docType = new MDocType(getCtx(), rs, get_TrxName());
				docTypes.add(docType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return docTypes;
	}
	
	private X_C_PosPeriodControl find(int periodControlId, int docTypeId) {
		//Construyo la query
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT * ");
		sql.append("FROM c_posperiodcontrol ");
		sql.append("WHERE c_periodcontrol_id = ? ");
		sql.append("  AND c_doctype_id = ? ");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			
			//Parámetros
			ps.setInt(1, periodControlId);
			ps.setInt(2, docTypeId);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				return new X_C_PosPeriodControl(getCtx(), rs, get_TrxName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
