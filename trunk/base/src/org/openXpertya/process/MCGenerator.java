package org.openXpertya.process;

import java.util.logging.Level;

import org.openXpertya.fastrack.FTGenerate;
import org.openXpertya.util.Trx;

public class MCGenerator extends SvrProcess {

	//Variables de instancia
	
	/** Password ingresado en el proceso */
	
	private String pass;
	
	/** Nombre de la transacción */
	
	private String trxName = new String("MCGenerator"+System.currentTimeMillis()+Thread.currentThread().getId());
	
	//Constructores
	
	
	public MCGenerator() {

	}


	//Getters y Setters
	

	public void setPass(String pass) {
		this.pass = pass;
	}


	public String getPass() {
		return pass;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}


	public String getTrxName() {
		return trxName;
	}
	
	
	//Métodos varios

	//Métodos de transacción
	
	/**
	 * Crea y retorna una transacción
	 * @return una nueva transacción 
	 */
	
	public Trx createTrx(){
		//Creo la transacción
		return Trx.get(trxName, true);
	}
	
	/**
	 * Retorna una transacción 
	 * @return la transacción con el nombre contenido en la variable de instancia o una nueva
	 */
	public Trx getTrx(){
		//Me fijo primero si esta la transacción con ese nombre
		Trx trx = Trx.get(trxName, false);
		
		//Si no existe, la creo
		if( trx == null){
			trx = createTrx();
		}
		
		return trx;
	}

	
	protected void prepare() {
		
		ProcessInfoParameter[] para = getParameter();
		
		int todos = para.length;
		
		for(int i = 0; i < todos ; i++){
			String name = para[i].getParameterName();
			
			if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "Password" )) {
                 pass = (String)para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
		}

	}

	
	protected String doIt() throws Exception {
		String retorno = null;
		FTGenerate generator = new FTGenerate(this.getTrxName());
		
		//Arranco la transacción
		this.getTrx().start();
		
		try{
			//Desbloquear
			retorno = generator.unlock(this.getPass());
			
			//Commit and close
			this.getTrx().commit();
			this.getTrx().close();
		}catch(Exception e){
			this.getTrx().rollback();
			this.getTrx().close();
			retorno = "Proceso incompleto: "+e.getMessage();
		}
		
		return retorno;
	}


	
}
