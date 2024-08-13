package org.openXpertya.process;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.openXpertya.model.MPOS;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;


/**
 * 
 * Permite obtener bloques de auditoria desde una impresora Hasar 2G
 * 
 * @author dREHER
 * 
 * */

public class ConsultarMemoriaFiscalHasar2G extends SvrProcess {

	/** Fecha Desde */
	private Timestamp fechaDesde = null;
	
	/** Fecha Hasta */
	private Timestamp fechaHasta = null;
	
	/** Impresora fiscal */
	private Integer controladorFiscalID;
	
	/** Mensaje final de proceso */
	private HTMLMsg msg = new HTMLMsg();
	
	/** Transaccción para uso externo a SvrProcess (instanciación por constructor) */
	private String localTrxName = null;
	
	/** Contexto para uso externo a SvrProcess (instanciación por constructor) */
	private Properties localCtx = null;
	
	public ConsultarMemoriaFiscalHasar2G() {
		super();
	}

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		String name = null;
        for( int i = 0;i < para.length;i++ ) {
            name = para[ i ].getParameterName();
            if( name.equalsIgnoreCase( "fechaDesde" )) 
            	fechaDesde = (Timestamp)para[ i ].getParameter();
            else if(name.equalsIgnoreCase( "fechaHasta" )) 
            	fechaHasta = (Timestamp)para[ i ].getParameter();
            else if(name.equalsIgnoreCase( "controladorFiscalID"))
            	controladorFiscalID = para[ i ].getParameter_ToAsInt();
            
        }
	}
	
	@Override
	protected String doIt() throws Exception {
		String res = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		
		if(fechaDesde==null)
			fechaDesde = Env.getDate(getCtx());
		if(fechaHasta==null)
			fechaHasta = Env.getDate(getCtx());
			
		
		
		
			
		
		return res;
	}

	/**
	 * Inicializa la caja diaria.
	 * 
	 * @throws Exception
	 *             en caso de error al inicializar
	 */
	protected void initialize() throws Exception{
		if(controladorFiscalID <= 0)
			throw new Exception("Debe especificar un controlador fiscal para poder continuar");
	}

	@Override
	protected String get_TrxName() {
		if (localTrxName != null) {
			return localTrxName;
		} else {
			return super.get_TrxName();
		}
	}
	
	@Override
	public Properties getCtx() {
		if (localCtx != null) {
			return localCtx;
		} else {
			return super.getCtx();
		}
	}

	/**
	 * Comienza la ejecución del proceso.
	 * 
	 * @return Mensaje HTML con la alternancia de contingencia
	 * @throws Exception
	 *             cuando se produce un error el seteo de contingencia
	 */
	public String start() throws Exception {
		return doIt();
	}

}
