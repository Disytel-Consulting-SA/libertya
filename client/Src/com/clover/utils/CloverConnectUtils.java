package com.clover.utils;

import com.clover.remote.client.CloverDeviceConfiguration;
import com.clover.remote.client.WebSocketCloverDeviceConfiguration;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.VTrxPaymentTerminalForm;
import org.openXpertya.db.CConnection;
import org.openXpertya.model.MExternalService;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;

public class CloverConnectUtils {
	private static final SecureRandom random = new SecureRandom();
	private static final char[] vals = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'}; // Crockford's base 32 chars

	/*
  private static final String APP_ID = "com.cloverconnector.java.simple.sample:1.4.1";
  private static final String POS_NAME = "Clover Simple Sample Java";
  private static final String DEVICE_NAME = "Clover Device";
	 */
	
	// dREHER
	private static final String APP_ID = "org.libertya.core.micro.r3000.dev.trxclover:1.0";
	private static final String POS_NAME = "Clover Connect Libertya TPV";
	private static final String DEVICE_NAME = "Clover Device";
	private static final String AUTH_TOKEN = "AuthorizationTokenClover";

	private boolean isConnect = false;
	private String saveAuthToken = null;
	
	private MExternalService externalService = null;
	private String serviceName = "Clover";


	public CloverConnectUtils() {
		
	}

	public String getNextId() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 13; i++) {
			int idx = random.nextInt(vals.length);
			sb.append(vals[idx]);
		}
		return sb.toString();
	}

	public CloverDeviceConfiguration getNetworkConfiguration(String ip) {
		return getNetworkConfiguration(ip, null, null);
	}

	public CloverDeviceConfiguration getNetworkConfiguration(String ip, Integer port, final VTrxPaymentTerminalForm info) {
		Integer dvcPort = port != null ? port : Integer.valueOf(12345);
		try {
			
			URI endpoint = new URI("wss://" + ip + ":" + dvcPort + "/remote_pay");
			
			KeyStore trustStore  = createTrustStore();

			setConnect(true);
			
			// For WebSocket configuration, we must handle the device pairing via callback
			return new WebSocketCloverDeviceConfiguration(endpoint, APP_ID, trustStore, POS_NAME, DEVICE_NAME, getSaveAuthToken(info)) {
	
				private static final long serialVersionUID = 1L;

				@Override
				public void onPairingCode(final String pairingCode) {
					System.out.println("Enter Pairing Code on Device: " + pairingCode);

					if(info!=null)
						info.setPairingCode(pairingCode); 
					else
						ADialog.info(0, null, "Codigo de Emparejamiento: " + pairingCode);
					
					// TODO: aca mostraria el dialogo del codigo de pairing
					
				}

				@Override
				public void onPairingSuccess(String authToken) {
					System.out.println("Pairing successful " + authToken);
					
					setSaveAuthToken(authToken, info); // se deberia guardar en cache...
					if(info!=null)
						info.setPairingCode(""); 
					
					// TODO: aca cerraria la ventana de dialogo que muestra el codigo de pairing
				}

			};
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		System.err.println("Error creating CloverDeviceConfiguration");

		return null;
	}

	private KeyStore createTrustStore() {
		try {
			String storeType = KeyStore.getDefaultType();
			KeyStore trustStore = KeyStore.getInstance(storeType);
			char[] TRUST_STORE_PASSWORD = "clover".toCharArray();
			trustStore.load(null, TRUST_STORE_PASSWORD);
			
			// dREHER
			// Certificado obtenido desde la base de datos
			Certificate cert = loadCertificateFromDB(true);
			
			// Load the old "dev" cert.  This should be valid for all target environments (dev, stg, sandbox, prod).
			// Certificate cert = loadCertificateFromResource("/certs/device_ca_certificate.crt");
			trustStore.setCertificateEntry("dev", cert);

			// Now load the environment specific cert (e.g. prod).  Always retrieving this cert from prod as it is really
			// only valid in prod at this point, and we also don't have a mechanism within the SDK of specifying the target
			// environment.
			// cert = loadCertificateFromResource("/certs/env_device_ca_certificate.crt");
			
			cert = loadCertificateFromDB(false);
			trustStore.setCertificateEntry("prod", cert);

			return trustStore;
		} catch(Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	private Certificate loadCertificateFromDB(boolean isTest) {
		System.out.println("Loading cert:  " + isTest);

		InputStream is = null;
		try {
			
			// Crear un InputStream a partir del arreglo de bytes
            is = new ByteArrayInputStream(getCRT(isTest));
			System.out.println("InputStream=" + is);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return cf.generateCertificate(is);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {
					// NO-OP
				}
			}
		}
	}
	
	private Certificate loadCertificateFromResource(String name) {
		System.out.println("Loading cert:  " + name);

		InputStream is = null;
		try {
			
		    // dREHER
			// Lo obtengo como recurso dentro del mismo proyecto
			is = CloverConnectUtils.class.getResourceAsStream(name);
			System.out.println("InputStream=" + is);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return cf.generateCertificate(is);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {
					// NO-OP
				}
			}
		}
	}
	
	// dREHER devolver el certificado de testing
	private byte[] getCRT(boolean isTest) {
		byte[] crt = null;
		
		getExternalService();
		
		if(externalService != null) {
			if(isTest)
				crt = externalService.getTestCRT();
			else
				crt = externalService.getProductionCRT();
		}
		
		return crt;
	}
	
	public void getExternalService() {
		
		if(externalService!=null)
			return;
		
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_ExternalService_ID ");
		sql.append("FROM ");
		sql.append("	" + MExternalService.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	name = ? ");

		int C_ExternalService_ID = DB.getSQLValue(null, sql.toString(), serviceName);
		if(C_ExternalService_ID > 0)
			externalService = new MExternalService(Env.getCtx(), C_ExternalService_ID, null);
	}

	public boolean isConnect() {
		return isConnect;
	}

	public void setConnect(boolean isconnect) {
		isConnect = isconnect;
	}

	public String getSaveAuthToken(VTrxPaymentTerminalForm form) {
		if(saveAuthToken == null || saveAuthToken.isEmpty())
			saveAuthToken = Env.getContext(Env.getCtx(), "authTokenClover");
		if(saveAuthToken == null || saveAuthToken.isEmpty() && form != null)
			saveAuthToken = form.getProperty( AUTH_TOKEN );
		
		return saveAuthToken;
	}

	public void setSaveAuthToken(String saveAuthToken, VTrxPaymentTerminalForm form) {
		this.saveAuthToken = saveAuthToken;
		Env.setContext(Env.getCtx(), "authTokenClover", saveAuthToken);
		if(form!=null)
			form.setProperty( AUTH_TOKEN, saveAuthToken);
	}
}
