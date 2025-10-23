package org.adempiere.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.adempiere.utils.Miscfunc;
import org.apache.commons.codec.binary.Base64;
import org.openXpertya.util.CLogger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/*
 * Clase para comunicar con un servicio web y devolver el resultado del mismo
 * 
 * dREHER
 * 06/04/2017
 * jorge.dreher@gmail.com
 * 
 */


public class ClientWS {

	private String url = "";
	private String json = "";
	private String user = "";
	private String pass = "";
	private JsonObject result = null;
	private String errorCode = "";
	private String method = "POST";
	private String parameters = "";
	private StringBuilder resultado = null;
	

	public String getParameters() {
		return parameters;
	}


	public void setParameters(String parameters) {
		this.parameters = parameters;
	}


	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getJson() {
		return json;
	}


	public void setJson(String json) {
		this.json = json;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getPass() {
		return pass;
	}


	public void setPass(String pass) {
		this.pass = pass;
	}


	public JsonObject getResult() {
		return result;
	}


	public void setResult(JsonObject result) {
		this.result = result;
	}


	public String getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	


	public StringBuilder getResultado() {
		return resultado;
	}


	public void setResultado(StringBuilder resultado) {
		this.resultado = resultado;
	}
	
	
	
/* Inicializa la clase */	
	public ClientWS(){
		
	}
	
	public ClientWS(String url){
		this.setUrl(url);
	}

	public Boolean ConnectAndExecute(){
		
		Boolean isOk = false;
		
		try {
			
			String authString = getUser() + ":" + getPass();
			CLogger.getCLogger("ClientWS").info("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			CLogger.getCLogger("ClientWS").info("Base64 encoded auth string: " + authStringEnc);
			
			String urlx = getUrl();
			if(getMethod().equals("GET") && getParameters()!= null && getParameters()!="")
				urlx += configParameters(getParameters());
			
			
			URL url = new URL(urlx);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(getMethod());
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
			
			// con metodo POST
			if(getParameters()!= null && getParameters()!=""){
				
				if(getMethod().equals("POST")){

					String input = getParameters();

					CLogger.getCLogger("ClientWS").info("Consume WS en metodo POST= " + urlx + " parameters= " + input);

					OutputStream os = conn.getOutputStream();
					os.write(input.getBytes());
					os.flush();
					
				}

			}
			
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED && conn.getResponseCode() != 200) {
				setErrorCode("Failed : HTTP error code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			StringBuilder sb = new StringBuilder();
			
			CLogger.getCLogger("ClientWS").info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				CLogger.getCLogger("ClientWS").info("Respuesta= " + output);
				sb.append(output);
			}

			br.close();
			conn.disconnect();
			
			parserJsonResult(sb);
			
			isOk = true;
			
		}catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
			CLogger.getCLogger("ClientWS").warning("Error al intentar conectar a url= " + getUrl());

		}
		
		return isOk;
	}
	
	// Parser result Json
	public void parserJsonResult(StringBuilder jsonStr){

		JsonObject o = null;
		try{
			o = StringToJson.parser(jsonStr);
		}catch(Exception ex){
			CLogger.getCLogger("ClientWS").warning("Error al leer resultado Json. Error= " + ex.toString());
			o = null;
		}
		
		setResult(o);
		setResultado(jsonStr);
		
		CLogger.getCLogger("ClientWS").info("Traduce el resultado en un objeto Json= " + getResult());
	}
	
	// Config para metodo GET
	private String configParameters(String parameters) {
		
		setParameters(parameters);

		return parameters;
	}


	
}
