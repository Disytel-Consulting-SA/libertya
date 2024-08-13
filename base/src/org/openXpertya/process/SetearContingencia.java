package org.openXpertya.process;

import java.util.Properties;

import org.openXpertya.model.MPOS;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;


/**
 * 
 * Permite setear la contingencia de un TPV desde la caja diaria
 * 
 * @author dREHER
 * 
 * */

public class SetearContingencia extends SvrProcess {

	/** ID de la caja diaria */
	private Integer posJournalID = 0;
	
	/** Caja Diaria */
	private MPOSJournal posJournal = null;
	
	/** Y/N setear contingencia */
	private boolean isContingencia = false;
	
	/** Mensaje final de proceso */
	private HTMLMsg msg = new HTMLMsg();
	
	/** Transaccción para uso externo a SvrProcess (instanciación por constructor) */
	private String localTrxName = null;
	
	/** Contexto para uso externo a SvrProcess (instanciación por constructor) */
	private Properties localCtx = null;
	
	public SetearContingencia() {
		super();
	}

	/**
	 * Constructor del proceso para setear contingencia
	 * 
	 * @param porJournalID
	 *            ID de la caja diaria a setear contingencia
	 * @param ctx
	 *            Contexto de la aplicación
	 * @param trxName
	 *            Nombre de transacción a utilizar.
	 */
	public SetearContingencia(int posJournalID, Properties ctx, String trxName) {
		super();
		this.posJournalID = posJournalID;
		localTrxName = trxName;
		if (ctx == null) {
			ctx = Env.getCtx();
		}
		localCtx = ctx;
	}

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		String name = null;
        for( int i = 0;i < para.length;i++ ) {
            name = para[ i ].getParameterName();
            if( name.equalsIgnoreCase( "C_PosJournal_ID" )) {
            	posJournalID = para[ i ].getParameterAsInt();
            }else if(name.equalsIgnoreCase( "IsContingencia" )) {
            	isContingencia = para[ i ].getParameter().equals("Y");
            }
        }
        
        if(posJournalID <= 0)
        	posJournalID = getRecord_ID();
        
	}
	
	@Override
	protected String doIt() throws Exception {
		
		if(posJournal==null)
			initialize();
		
		String resultado = setContingencia(posJournal, isContingencia);
		
		return resultado;
	}

	/**
	 * Inicializa la caja diaria.
	 * 
	 * @throws Exception
	 *             en caso de error al inicializar
	 */
	protected void initialize() throws Exception{
		initPosJournal();
	}
	


	/**
	 * Setear contingencia
	 * 
	 * @param posJournal
	 * @param makeMsg
	 *            true si se debe setea contingencia, false si se quita contingencia
	 * @throws Exception
	 *             en caso de error
	 */
	protected String setContingencia(MPOSJournal posjournal, boolean iscontingencia) throws Exception{
		String ret = "";
		 		
		try {
			
			// Recupero la configuracion de TPV a partir de la caja diaria
			if (posjournal.getC_POS_ID() > 0) {
				MPOS pos = new MPOS(Env.getCtx(), posjournal.getC_POS_ID(), get_TrxName());
				if(pos != null) {
					Object tmp = pos.get_Value("IsContingencia");
					boolean isActualContingencia = false;
					if(tmp!=null)
						if(tmp.equals("Y") || tmp.equals(true))
							isActualContingencia = true;
					
					// Se quiere setear el estado actual, informar...
					if(iscontingencia==isActualContingencia)
						ret = "Contingencia Actual=" + (iscontingencia?"SI":"NO") + "- El punto de venta ya se encuentra en ese estado";
					else {
						int n = DB.executeUpdate("UPDATE C_POS SET IsContingencia='" + (iscontingencia?"Y":"N") + "' WHERE C_POS_ID=" +
									pos.getC_POS_ID(), get_TrxName());
						if(n > 0) {
							ret = "Nueva Contingencia=" + (iscontingencia?"SI":"NO") + "- El punto de venta alterno su estado";
							// DB.commit(true, get_TrxName());
						}
						else
							ret = "No se pudo guardar el cambio de estado de contingencia";
					}
						
				}
			}
			
		}catch(Exception ex) {
			log.warning("Error al imprimir en controlador fiscal: " + ex);
		}

		return ret;
	}	
	
	
	/**
	 * Inicializa la caja diaria con la de la BD
	 */
	protected void initPosJournal(){
		setPosJournal(new MPOSJournal(getCtx(), posJournalID, get_TrxName()));
	}

	
	protected void setPosJournal(MPOSJournal posJournal) {
		this.posJournal = posJournal;
	}

	protected MPOSJournal getPosJournal() {
		return posJournal;
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
