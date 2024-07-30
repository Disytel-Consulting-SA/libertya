package org.openXpertya.process.customImport.fidelius.pojos.login;

import org.openXpertya.process.customImport.fidelius.pojos.GenericDatum;
import org.openXpertya.process.customImport.fidelius.pojos.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login extends Pojo {

	@SerializedName("status")
	@Expose
	private String status;

	public String getToken() {
		return status;
	}

	public void setToken(String token) {
		this.status = token;
	}
	
	public String getStatus() {
		
		return GenericDatum.get("status", getToken());
		
	}

}