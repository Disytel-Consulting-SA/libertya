package org.adempiere.ws;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
/*
 * Parsea un string o StringBuilder a JsonObject
 * 
 * dREHER
 * jorge.dreher@gmail.com
 * 
 */
public class StringToJson {

	public StringToJson(){
		
	}
	
	public static JsonObject parser(String jsonStr){
		Gson gson = new Gson();
		JsonElement element = gson.fromJson (jsonStr, JsonElement.class);
		JsonObject jsonObj = element.getAsJsonObject();
		
		return jsonObj;
	}
	
	public static JsonObject parser(StringBuilder jsonStr){
		Gson gson = new Gson();
		JsonElement element = gson.fromJson (jsonStr.toString(), JsonElement.class);
		JsonObject jsonObj = element.getAsJsonObject();
		
		return jsonObj;
	}
	
}
