package org.openXpertya.process;

import java.util.logging.Level;

import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_C_OrderLine;
import org.openXpertya.util.DB;

public class ComponentActivation extends SvrProcess {

	private String component = "";
	
	public static final String COMP_AtributosHeterogeneos = "AtributosHeterogeneos";
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for( int i = 0;i < para.length;i++ ) {
			log.fine( "prepare - " + para[ i ] );
			
			String name = para[ i ].getParameterName();
			
			if( para[ i ].getParameter() == null ) {
				;
			} else if( name.equalsIgnoreCase("component")) {
				component = ( String )para[ i ].getParameter();
			}  else {
				log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
			}
		}
	}
	
	@Override
	protected String doIt() throws Exception {
		String ret = null;
		
		MPreference.SetCustomPreferenceValue(component, "Y");
		
		if (component.equalsIgnoreCase(COMP_AtributosHeterogeneos)) {
			activateAtributosHeterogeneos();
		}
		
		return "@Completed@";
	}
	
	private void activateAtributosHeterogeneos() throws Exception {
		String sql;
		
		sql =   " UPDATE ad_field " +
				" set isdisplayed = 'Y' " +
				" , seqno = ( SELECT MAX(f2.seqno) + 10 FROM ad_field f2 WHERE f2.ad_tab_id = ad_field.ad_tab_id ) " +
				" , sortno = ( SELECT MAX(f2.sortno) + 10 FROM ad_field f2 WHERE f2.ad_tab_id = ad_field.ad_tab_id ) " +
				" WHERE ad_tab_id in ( 293 ) AND ad_field_id IN ( SELECT ad_field_id FROM ad_field INNER JOIN ad_column ON (ad_field.ad_column_id=ad_column.ad_column_id) AND columnname = 'OpenMatrix' ) ";
		
		if(DB.executeUpdate(sql.toString()) == -1)
			throw new Exception("@Error@");
		
		sql = new String("UPDATE ad_field " + 
				"SET isdisplayed = 'N' " + 
				"WHERE (ad_tab_id = 180) AND ad_field_id IN (SELECT ad_field_id	FROM ad_field as af	INNER JOIN ad_column as ac ON (af.ad_column_id = ac.ad_column_id) WHERE ac.columnname = 'M_AttributeSetInstance_ID') ");
		
		if(DB.executeUpdate(sql.toString()) == -1){
			throw new Exception("@Error@");
		}
	}
}

