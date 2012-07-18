package org.openXpertya.pos.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuthManager {

	/**
	 * Map de asociación entre momentos de autorizaciones y clases
	 * (operaciones) a autorizar
	 */
	private HashMap<String, List<AuthOperation>> authorizeOperations;
	
	public AuthManager() {
		setAuthorizeOperations(new HashMap<String, List<AuthOperation>>());
	}
	
	public void addAuthOperation(AuthOperation authOperation){
		List<AuthOperation> operations = getAuthorizeOperations().get(authOperation.getAuthorizeMoment()); 
		if(operations == null){
			operations = new ArrayList<AuthOperation>();
		}
		operations.add(authOperation);
		getAuthorizeOperations().put(authOperation.getAuthorizeMoment(), operations);
	}
	
	public void removeAuthOperation(AuthOperation authOperation){
		List<AuthOperation> operations = getAuthorizeOperations().get(authOperation.getAuthorizeMoment());
		if(operations != null){
			operations.remove(authOperation);
		}
		getAuthorizeOperations().put(authOperation.getAuthorizeMoment(), operations);
	}
	
	public boolean existsAuthOperation(AuthOperation authOperation){
		List<AuthOperation> operations = getAuthorizeOperations().get(
				authOperation.getAuthorizeMoment());
		return operations != null && operations.contains(authOperation);
	}
	
	public List<AuthOperation> getOperations(String authorizationMoment){
		return getAuthorizeOperations().get(authorizationMoment) != null ? getAuthorizeOperations()
				.get(authorizationMoment) : new ArrayList<AuthOperation>();
	}

	public void setAuthorizeOperations(HashMap<String, List<AuthOperation>> authorizeOperations) {
		this.authorizeOperations = authorizeOperations;
	}

	public HashMap<String, List<AuthOperation>> getAuthorizeOperations() {
		return authorizeOperations;
	}

}
