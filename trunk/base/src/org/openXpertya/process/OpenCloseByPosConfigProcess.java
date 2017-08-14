package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.model.MDocType;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class OpenCloseByPosConfigProcess extends SvrProcess {
	
	private int pos = 0;
	private boolean openCloseByPos = false;
	private int orgId = 0;

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
	            } else if( name.equalsIgnoreCase( "open_close_by_pos" )) {
	            	openCloseByPos = "Y".equals((String)para[ i ].getParameter());	            	
	            } else if( name.equalsIgnoreCase( "ad_org_id" )) {
	            	orgId = para[i].getParameterAsInt();           	
	            } else {
	                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
	            }
	        }
	}
	
	@Override
	protected String doIt() throws Exception {
		// 1 - Obtengo los tipos de documentos que coinciden con el POS ingresado
		List<MDocType> docTypes = getDocTypes(pos, orgId);
		
		/* 2 - Por cada uno de los tipos de documentos recuperados, seteo el valor
		   "Open Close By POS" según el parámetro ingresado    */
		for (MDocType docType : docTypes) {
			docType.setopen_close_by_pos(openCloseByPos);
			if (!docType.save()) {
				throw new Exception(Msg.getMsg(getCtx(), "DocTypeUpdateError"));
			}
		}
		
		return Msg.getMsg(getCtx(), "DocTypesUpdateSuccess");
	}
	
	private List<MDocType> getDocTypes(int pos, int orgId) {
		List<MDocType> docTypes = new ArrayList<MDocType>();
		
		//Construyo la query
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT  ");
		sql.append("dt.* ");
		sql.append("FROM c_doctype dt ");
		sql.append("WHERE docbasetype IN ('ARI', 'ARC') ");
		sql.append("AND ad_client_id = ? ");
		sql.append("  AND POSITION(TRIM(BOTH FROM to_char(?, '0000')) IN doctypekey) > 0; ");
		
		if (orgId != 0) {
			sql.append("AND ad_org_id = ? ");
		}
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			
			//Parámetros
			ps.setInt(1, getAD_Client_ID());
			ps.setInt(2, pos);
			if (orgId != 0) {
				ps.setInt(3, orgId);
			}
			
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

}
