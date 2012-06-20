package org.openXpertya.process;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.ecs.xhtml.bdo;
import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MClient;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class LocaleActivation extends SvrProcess {

	protected static CLogger s_log = CLogger.getCLogger(LocaleActivation.class);
	private static final String LOCALE_AR = "A";
    
    private static Properties ctx = Env.getCtx();
    private String trxName = "";
    private String locale = null;
     
    
	@Override
	protected void prepare() {
		trxName = get_TrxName();
		ProcessInfoParameter[] para = getParameter();
		for( int i = 0;i < para.length;i++ ) {
			log.fine( "prepare - " + para[ i ] );
			
			String name = para[ i ].getParameterName();
			
			if( para[ i ].getParameter() == null ) {
				;
			} else if( name.equals( "locale" )) {
				locale = ( String )para[ i ].getParameter();
			}  else {
				log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		String ret = null;
		String sql;

		// Activación de la localización de Argentina.
		if(LOCALE_AR.equals(locale)) {
			// Si ya se encuentra activa la localización argetina, se 
			// levanta una excepción.
			if(CalloutInvoiceExt.ComprobantesFiscalesActivos())
				throw new Exception(Msg.parseTranslation(getCtx(), "@LocaleARAlreadyActivatedError@"));
			
			String localeName = "Argentina";
			sql = "{call activate_locale_ar(?)}";
			
			CallableStatement cstmt = null;

			try {
				//pstmt = DB.prepareStatement( sql,get_TrxName());
				cstmt = DB.prepareCall(sql, ResultSet.CONCUR_UPDATABLE, true, get_TrxName());
				cstmt.setBoolean(1,Boolean.TRUE);
				cstmt.executeUpdate();
				
	            
	            // Se crean por defecto el conjunto de tipos de documento
	            // para el punto de venta 1 para cada compañia;
	            createClientDocTypes(1);
	            
	            ret = "@LocaleActivationOk@: " + localeName;
	            
			} catch( Exception e ) {
	            log.log( Level.SEVERE,sql,e );
	            ret = "@LocaleActivationError@: " + localeName + ". " + e.getMessage();
	            throw new Exception(Msg.parseTranslation(getCtx(),ret));
	        } finally {
	        	if (cstmt != null) cstmt.close();
	        }
		
		// Localización desconocida
		
		} else {
			log.log( Level.SEVERE,"doIt - Unknown Locale: " + locale );
			throw new Exception(Msg.parseTranslation(getCtx(),"@LocaleActivationError@"));
		}
		
		return ret;
	}
	
	private void createClientDocTypes(int posNumber) throws Exception {
		int[] clientIDs = PO.getAllIDs("AD_Client", "AD_Client_ID > 0", null);
		for (int i = 0; i < clientIDs.length; i++) {
			POSDocTypeCreate.createPOSDocTypes(clientIDs[i], posNumber, get_TrxName());
		}
	}
	
	/**
	 * Crea o actualiza datos relacionados con la localización actualmente activa, para una
	 * compañía específica.
	 * @param aD_client_ID Compañía a la que se le aplica la creación de datos.
	 * @param trxName Nombre de la transacción a utiizar en el procesamiento.
	 * @return
	 */
	public static String createClientLocaleData(int aD_client_ID, String trxName) throws Exception {
		String sqlData = null;
		String localeName = "";
		String ret = "";
		// Se valida si está activa la Localización Argentina. Se asigna funcion SQL
		// correspondiente para crear los datos de la compañía.
		if (CalloutInvoiceExt.ComprobantesFiscalesActivos(true)) {
			sqlData = "{call create_locale_ar_data(?)}";
			localeName = "Argentina";
		}
		// Si existe alguna localización activa, se ejecuta la consulta.
		if (sqlData != null) {
			CallableStatement cstmt = null;

			try {
				cstmt = DB.prepareCall(sqlData, ResultSet.CONCUR_UPDATABLE, true, trxName);
				cstmt.setInt(1,aD_client_ID);
				cstmt.executeUpdate();
	            
	            // Se crean por defecto el conjunto de tipos de documento
	            // para el punto de venta 1 para la compañía;
				POSDocTypeCreate.createPOSDocTypes(aD_client_ID, 1, trxName);
	            
	            ret = "@LocaleActivationOk@: " + localeName;
			} catch( Exception e ) {
	            s_log.log( Level.SEVERE, sqlData, e );
	            throw new Exception(Msg.parseTranslation(Env.getCtx(),"@LocaleActivationError@: " + localeName + ". " + e.getMessage())); 
	        } finally {
				try {
					if (cstmt != null) cstmt.close();
				} catch (SQLException e) {}
	        }
		}
		
		return ret;
	}
}
