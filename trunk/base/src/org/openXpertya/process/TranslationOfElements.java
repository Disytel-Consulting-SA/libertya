package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import org.openXpertya.model.X_AD_Field_Trl;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class TranslationOfElements extends SvrProcess {
	
	// Parametros del proceso
	private int ad_Element_ID = 0;
	private String names = null;
	private String description = null;
	private String help = null;
	private String ad_Language = null;
	private String deleteName = null;
	private String deleteDescription = null;
	private String deleteHelp = null;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Element_ID" )) {
            	ad_Element_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "Name" )) {
            	names = (String) para[ i ].getParameter();
            } else if( name.equals( "Description" )) {
            	description = (String) para[ i ].getParameter();
            } else if( name.equals( "Help" )) {
            	help = (String) para[ i ].getParameter();
            }else if( name.equals( "AD_Language" )) {
            	ad_Language = (String) para[ i ].getParameter();
            } else if( name.equals( "DeleteName" )) {
            	deleteName = (String) para[ i ].getParameter();
            } else if( name.equals( "DeleteDescription" )) {
            	deleteDescription = (String) para[ i ].getParameter();
            }else if( name.equals( "DeleteHelp" )) {
            	deleteHelp = (String) para[ i ].getParameter();
            }else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
	}

	@Override
	protected String doIt() throws Exception {	
		// La sonsulta sql retorna los elementos con el ID ingresado como parametro en el proceso. 
		// En el caso que el parametro ad_language sea ingresado se agrega como filtro a la consulta.
		String sql = "select * from ad_field_trl t inner join ad_field f on (t.ad_field_id = f.ad_field_id) inner join ad_column c on (f.ad_column_id = c.ad_column_id) where (ad_element_id = "
				+ ad_Element_ID + ")";
		if( !Util.isEmpty(ad_Language, true) ) {
			sql = sql + "and (ad_language = '" + ad_Language + "')";
		}
		sql = sql + ";";		
		try {
            PreparedStatement stmt = DB.prepareStatement(sql, get_TrxName());
            ResultSet rs   = stmt.executeQuery();
            
            while( rs.next()) {
                X_AD_Field_Trl traduccion = new X_AD_Field_Trl(getCtx(),rs,get_TrxName());
                // Si el check de borrado fue seteado se borra el campo correspondiente.
                // En caso contrario, solo si se ingreso un texto se modifica el campo.
                if(deleteName.equalsIgnoreCase("Y")){
                	traduccion.setName("");   
                }
                else{
                	if( !Util.isEmpty(names, true) ) {
                    	traduccion.setName(names);    
                    } 
                }
                // Si el check de borrado fue seteado se borra el campo correspondiente.
                // En caso contrario, solo si se ingreso un texto se modifica el campo.
                if(deleteDescription.equalsIgnoreCase("Y")){
                	traduccion.setDescription("");  
                }
                else{
                	 if( !Util.isEmpty(description, true) ) {
                     	traduccion.setDescription(description);
                	 } 
                }
                // Si el check de borrado fue seteado se borra el campo correspondiente.
                // En caso contrario, solo si se ingreso un texto se modifica el campo.
                if(deleteHelp.equalsIgnoreCase("Y")){
                	traduccion.setHelp("");    
                }
                else{
                	 if( !Util.isEmpty(help, true) ) {
                     	traduccion.setHelp(help);    
                     } 
                }
    
                if(!traduccion.save()){
                	throw new Exception(CLogger.retrieveErrorAsString());
                }
            }

            rs.close();
            stmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt - " + sql,e );
            throw new Exception( e );
        }
		return null;
	}

}
