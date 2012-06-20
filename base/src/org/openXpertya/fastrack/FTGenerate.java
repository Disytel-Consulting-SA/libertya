package org.openXpertya.fastrack;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.MPreference;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class FTGenerate extends FTModule {

	//Variables de instancia
	
	/** Información de ejecución del módulo */
	
	private String info = "";
	
	//Constructores
	
	public FTGenerate() {

	}

	public FTGenerate(String trxName) {
		super(trxName);

	}

	
	//Getters y Setters
	
	public void setInfo(String info) {
		this.info = info;
	}

	public String getInfo() {
		return info;
	}
	
	
	
	//Métodos varios
	
	/**
	 * Obtiene el preference que identifica versión no fast-track
	 * @return el preference
	 */
	private MPreference getFirst() throws Exception{
		//Obtengo el preference que define versión no fast-track
		String sql = "SELECT * FROM ad_preference WHERE (attribute = 'AD_FTDoc')";
		
		PreparedStatement psmt = null;
		ResultSet rs = null;
		MPreference pri = null;
		
		try{
			psmt = DB.prepareStatement(sql, this.getTrxName());
			rs = psmt.executeQuery();
			int resultados = 0;
			
			while(rs.next()){
				//Obtengo el preference a partir del result set
				pri = new MPreference(Env.getCtx(),rs,this.getTrxName());
				resultados++;
			}
			
			//Si hay más de una tupla tiro la excepción
			if(resultados > 1){
				throw new Exception("Existe mas de una tupla de ft");
			}
			
		} catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally{
			try{
				rs.close();
				psmt.close();
				psmt = null;
			}catch(Exception e){
				psmt = null;
			}
		}
		
		return pri;
	}
	
	/**
	 * Obtiene la preference con las macs
	 * @return el preference
	 */
	private MPreference getSecond() throws Exception{
		//Obtengo el preference de MAC
		String sql = "SELECT * FROM ad_preference WHERE (attribute = 'AD_MCDoc')";
		
		PreparedStatement psmt = null;
		ResultSet rs = null;
		MPreference sec = null;
		
		try{
			psmt = DB.prepareStatement(sql, this.getTrxName());
			rs = psmt.executeQuery();
			int resultados = 0;
			
			while(rs.next()){
				//Obtengo el preference a partir del result set
				sec = new MPreference(Env.getCtx(),rs,this.getTrxName());
				resultados++;
			}
			
			//Si hay más de una tupla tiro la excepción
			if(resultados > 1){
				throw new Exception("Existe mas de una tupla de mac");
			}
			
		} catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally{
			try{
				rs.close();
				psmt.close();
				psmt = null;
			}catch(Exception e){
				psmt = null;
			}
		}
		
		return sec;

	}
	
	/**
	 * 
	 * @return
	 */
	private MPreference getKey() throws Exception{
		//Obtengo el preference del password
		String sql = "SELECT * FROM ad_preference WHERE (attribute = 'C_Dockey')";
		
		PreparedStatement psmt = null;
		ResultSet rs = null;
		MPreference sec = null;
		
		try{
			psmt = DB.prepareStatement(sql, this.getTrxName());
			rs = psmt.executeQuery();
			//Determinar la cantidad de resultados
			int resultados = 0;
			
			while(rs.next()){
				//Obtengo el preference a partir del result set
				sec = new MPreference(Env.getCtx(),rs,this.getTrxName());
				resultados++;
			}
			
			//Si hay más de una tupla tiro la excepción
			if(resultados > 1){
				throw new Exception("Se ha detectado un fallo de seguridad en el sistema");
			}
		} catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally{
			try{
				rs.close();
				psmt.close();
				psmt = null;
			}catch(Exception e){
				psmt = null;
			}
		}
		
		return sec;
	}
	
	
	private boolean checkFirstAndEliminate() throws Exception{
		boolean retorno = true;
		//Obtengo el preference que define versión no fast-track
		MPreference pri = this.getFirst();
		
		MPreference sec = this.getSecond();
		
		if(pri != null){
			if(sec != null){
				retorno = false;
				this.delFirst();
			}			
		}
		else{
			retorno = false;
		}
		
		return retorno;
	}
	
	/**
	 * Chequea si existe la primera y no existe la segunda. Determina si estamos en una versión estándard
	 * @return true si pasa lo anterior, false cc
	 */
	private boolean checkPreConditions() throws Exception{
		//Obtengo el preference que define versión no fast-track
		
		MPreference pri = this.getFirst();
		//Obtengo el preference de MAC
		MPreference sec = this.getSecond();
		
		return ((pri != null) && (sec == null)); 
	}
	
	
	/**
	 * Chequea si no existe la primer tupla y existe la segunda. Determina el cumplimiento 
	 * de las condiciones que se tienen que dar en la versión fast-track (Macs), a excepción del md5.
	 * Esta validación debe estar acompañada con la de macs.  
	 * @return true si pasa lo anterior, false cc
	 */
	private boolean checkPostConditions() throws Exception{
		//Obtengo el preference que define versión no fast-track
		MPreference pri = this.getFirst();
		
		//Obtengo el preference de MAC
		MPreference sec = this.getSecond();
		
		return ((pri == null) && (sec != null));
	}
	
	/**
	 * Determina si las macs generadas con el value del preference de la base son iguales
	 * @param second preference a verificar 
	 * @return true si son iguales, false caso contrario
	 */
	private boolean checkMacs() throws Exception{
		MPreference second = this.getSecond();
		
		//Si el valor del campo value es igual a las MAC 
		return (second.getValue().equals(this.generate(this.getMacs())));
		
	}
	
	/**
	 * Determina si los passwords son iguales
	 * @param pass password ingresado
	 * @return true en caso de coincidir, false cc
	 * @throws Exception 
	 */
	private boolean checkPass(String pass) throws Exception{
		//Genero el md5 del pass
		String passHash = this.generate(pass);
		
		//Obtengo el password de la base
		MPreference key = this.getKey();
		
		//Devuelvo si las claves son iguales
		return passHash.equals(key.getValue());
	}
	/**
	 * Convierte un array de bytes en string
	 * @param data array de bytes
	 * @return string asociado
	 */
	private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
        	int halfbyte = (data[i] >>> 4) & 0x0F;
        	int two_halfs = 0;
        	do {
	        	if ((0 <= halfbyte) && (halfbyte <= 9))
	                buf.append((char) ('0' + halfbyte));
	            else
	            	buf.append((char) ('a' + (halfbyte - 10)));
	        	halfbyte = data[i] & 0x0F;
        	} while(two_halfs++ < 1);
        }
        return buf.toString();
    }
	
	
	private String getMA() throws Exception{
		//Creo el sql
		String sql = "SELECT 'deprecated' as macs";
		String retorno = new String();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			ps = DB.prepareStatement(sql, this.getTrxName());
			rs = ps.executeQuery();
			
			if(rs.next()){
				retorno = rs.getString("macs");
			}
			else{
				throw new Exception();
			}
								
		}catch(Exception e){
			throw new Exception("Imposible procesar. Informacion del sistema incompleta");
		}finally{
			try{
			ps.close();
			if (rs!=null)
				rs.close();
			}catch(Exception e){
				e.printStackTrace();
				throw new Exception(e);
			}
		}
		
		return  retorno;
		
	}
	
	
	/**
	 * Obtiene la/s MAC Address
	 * @return macs concatenadas  
	 */
	private String getMacs() throws Exception{
		StringBuffer gen = new StringBuffer();
		
		try{
			//Obtengo las MAC Address 
			String macs = this.getMA();
			
			String[] macList = macs.split(";");
			
			int total = macList.length;
			
			for(int i = 0; i < total ; i++){
				gen.append(macList[i]);
			}
			
		}catch(UnsupportedOperationException ue){
			this.setInfo(ue.getMessage());
			ue.printStackTrace();
			throw new Exception(ue.getMessage());			
		}catch(IOException ioe){
			this.setInfo(ioe.getMessage());
			ioe.printStackTrace();
			throw new Exception(ioe.getMessage());
		}catch(Exception e){
			this.setInfo(e.getMessage());
			e.printStackTrace();
			throw new Exception("No se pueden resolver las variables de hardware");
		}
		
		return gen.toString();
	}
	
	/**
	 * Genera el md5 del parámetro y lo devuelve
	 * @return el hash en string
	 */
	private String generate(String value) throws Exception{
		
		String resStr = null;
		
		try{
			//Operaciones md5
			//Creo el md5
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			md.update(value.getBytes("utf-8"), 0, value.length());
			
			//Obtengo el hash
			byte[] result = md.digest();
			
			
			//Obtengo el string a partir de ese hash
			resStr = FTGenerate.convertToHex(result);
			
			md.reset();
			
		} catch(NoSuchAlgorithmException nae){
			this.getLog().severe("Hash no funcionando");
			nae.printStackTrace();
			throw new Exception("Hash no funcionando");
		} catch(UnsupportedEncodingException ee){
			this.getLog().severe("Encoding no funcionando");
			ee.printStackTrace();
			throw new Exception("Encoding no funcionando");
			
		}
		
		return resStr;
	}
	
	/**
	 * Crea el preference para las MACs
	 */
	private void createSecond() throws Exception{
		//Creo la preference con la MAC
		MPreference preferenceMAC = new MPreference(Env.getCtx(),0,this.getTrxName());
		
		//Seteo los valores
		preferenceMAC.setAttribute("AD_MCDoc");
		preferenceMAC.setValue(this.generate(this.getMacs()));
		
		//Guardo
		if(!preferenceMAC.save()){
			throw new Exception("No se guardo el hash");
		}

	}
	

	/**
	 * Elimino la primer preference
	 */
	private void delSecond() throws Exception{
		//Elimino la primer tupla de ad_preference
		String sql = "DELETE FROM ad_preference WHERE (attribute = 'AD_MCDoc')";
		
		//Ejecuto el sql
		ExecuterSql.executeUpdate(sql, this.getTrxName());
	}
	
	/**
	 * Elimino la primer preference
	 */
	private void delFirst() throws Exception{
		//Elimino la primer tupla de ad_preference
		String sql = "DELETE FROM ad_preference WHERE (attribute = 'AD_FTDoc')";
		
		//Ejecuto el sql
		ExecuterSql.executeUpdate(sql, this.getTrxName());
	}
	
	/**
	 * Operaciones a realizar en caso que no estén creadas:
	 * <ul>
	 * <li>Elimina el preference que identifica que no estamos en la versión fast-track</li>
	 * <li>Crea el preference para la versión con respecto a la MAC</li> 
	 * </ul>
	 * @throws Exception 
	 */
	public void ejecutar() throws Exception{
		//Determino las primeras condiciones
		if(this.checkPreConditions()){
			//Si entró por aca significa que está todo bien para crear la tupla preference con las macs
			
			//Elimino la primera
			this.delFirst();
			
			//Creo la segunda
			this.createSecond();
			
			this.setInfo("Se completo correctamente");
		}
		else{
			//Determino las segundas condiciones
			if(!(this.checkPostConditions() && this.checkMacs())){
				this.setInfo("El sistema detecto cambios en hardware y base de datos...No se puede seguir con la operacion");
				throw new Exception("El sistema detecto cambios en hardware y base de datos...No se puede seguir con la operacion");
			}
			
			this.setInfo("Se completo correctamente");
		}
	}
	
	
	/*
	 * --------------------------------------------------------------------------------
	 * 							Métodos externos
	 * --------------------------------------------------------------------------------	
	 */
	
	/**
	 * Valida si la ejecución del procedimiento puede llevarse a cabo dependiendo de las condiciones 
	 * @return true si se puede ejecutar, false cc
	 */
	public boolean validate() throws Exception{
		boolean retorno = this.checkPreConditions() || (this.checkPostConditions() && this.checkMacs());
		
		if(!retorno){
			this.setInfo("El sistema detecto modificaciones que impiden realizar la accion indicada.  Contactese con el proveedor.");
		}
		//Retorna si se puede realizar la operación dependiendo de las condiciones
		return retorno;
	}
	
	
	/**
	 * Desbloquea el sistema verificando el password pasado como parámetro
	 * @param pass password
	 * @return Mensaje de éxito 
	 * @throws Exception
	 */
	public String unlock(String pass) throws Exception{
		String retorno = "El sistema no necesita desbloqueo";
		
		if(!this.checkPreConditions()){
			if(this.checkPass(pass)){
				//Elimino la primer tupla
				this.delFirst();
				
				//Elimino la segunda
				this.delSecond();
				
				//Creo la segunda
				this.createSecond();
						
				retorno = "Sistema desbloqueado existosamente";
			} else{
				throw new Exception("Las clave ingresada es distinta a la clave registrada");
			}
		}
		
		
		return retorno;
		
	}
	
	/*
	 * --------------------------------------------------------------------------------
	 * 						Fin Métodos externos
	 * --------------------------------------------------------------------------------
	 */
	
	
	
	
	public void deshacer() {

	}

}
