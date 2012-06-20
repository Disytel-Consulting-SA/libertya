package org.openXpertya.apps;

import java.util.concurrent.Semaphore;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MUser;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class UserValidation {

	/** Id de compañía */
	private MClient client;
	
	/** Resultado del cambio de datos de usuario */
	private CallResult userDataChangeResult;
	
	/** Semáforo para espera de cambio de datos del usuario */
	private Semaphore mutex;

	public void initialize(MClient client) {
		setClient(client);
		setMutex(new Semaphore(0, true));
	}

	public CallResult login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		setUserDataChangeResult(null);
		// Si la clave expiró, entonces abro la ventana de cambio de clave y
		// espero por una respuesta 
		if(MUser.isPasswordExpired(Env.getCtx(), AD_User_ID, null)){
			SwingWorker userWorker = new UserSwingWorker();
			userWorker.start();
			try{
				getMutex().acquire();
			} catch(InterruptedException ie){
				ie.printStackTrace();
			}
		}
		return getUserDataChangeResult();
	}

	public int getAD_Client_ID() {
		return getClient().getID();
	}

	private void setClient(MClient client) {
		this.client = client;
	}

	private MClient getClient() {
		return client;
	}

	
	private void setUserDataChangeResult(CallResult userDataChangeResult) {
		this.userDataChangeResult = userDataChangeResult;
	}

	private CallResult getUserDataChangeResult() {
		return userDataChangeResult;
	}
	
	
	private void setMutex(Semaphore mutex) {
		this.mutex = mutex;
	}

	private Semaphore getMutex() {
		return mutex;
	}


	private class UserSwingWorker extends SwingWorker implements UserDataChangeListener{

		/** Formulario para cambio de datos de usuario */
		private UserDataChange userDataChange;
		
		@Override
		public Object construct() {
			setUserDataChange(new UserDataChange(Msg.getMsg(Env.getCtx(), "PasswordExpired")));
			getUserDataChange().addUserDataListener(this);
			getUserDataChange().setModal(true);
			getUserDataChange().pack();
	    	AEnv.showCenterScreen(getUserDataChange());
			return true;
		}
		
		@Override
		public void userDataChanged(UserDataChangeEvent event) {
			CallResult result = new CallResult();
			result.setError(!event.isUserDataChanged());
			result.setShowError(false);
			setUserDataChangeResult(result);
			getMutex().release();
		}
		
		private void setUserDataChange(UserDataChange userDataChange) {
			this.userDataChange = userDataChange;
		}

		private UserDataChange getUserDataChange() {
			return userDataChange;
		}
		
	}
	
}
