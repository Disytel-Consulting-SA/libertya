package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.X_C_BPartner_Padron_BsAs;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.Env;


public class DeletePadronBsAs extends SvrProcess {

		
	private int p_AD_Org_ID = 0;
	private Timestamp p_publication_date;
	private String p_PadronType = null;
	private int ad_Client_ID = 0;
	
	/**	Client to be imported to		*/
	private String clientCheck = null;
	private int regDeleted;
	 
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Org_ID" )) {
                p_AD_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "PublicationDate" )) {
            	p_publication_date = (Timestamp)para[i].getParameter();
            } else if( name.equals( "PadronType" )) {
                p_PadronType = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
		}
		ad_Client_ID = Env.getAD_Client_ID(Env.getCtx());
		setClientCheck(" AND AD_Client_ID=" + ad_Client_ID);
	}
	
	@Override
	protected String doIt() throws Exception {
		// Eliminación de registros en padrón
		StringBuffer sql = new StringBuffer(); 
		sql.append("DELETE FROM " + X_C_BPartner_Padron_BsAs.Table_Name);
		sql.append(" WHERE date_trunc('day',?::date) = date_trunc('day',fecha_publicacion) ");
		if (p_AD_Org_ID != 0){
			sql.append(" AND (AD_Org_ID = "+ p_AD_Org_ID +") ");
		}
		sql.append("AND padrontype = '" + p_PadronType + "' "+getClientCheck());
				
		PreparedStatement ps = new CPreparedStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql.toString(),	get_TrxName(), true);
		ps.setTimestamp(1, p_publication_date);
		regDeleted = ps.executeUpdate();
		
		log.log (Level.SEVERE,"doIt - Cantidad de registros eliminados =" + regDeleted);
		
		addLog (0, null, new BigDecimal (regDeleted), "Registros eliminados del Padron");

		return "Proceso finalizado satisfactoriamente";

	}
	
	protected String getClientCheck() {
		return clientCheck;
	}

	protected void setClientCheck(String clientCheck) {
		this.clientCheck = clientCheck;
	}

}
