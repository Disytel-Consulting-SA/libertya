package org.openXpertya.process.customImport.fidelius.http;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.openXpertya.process.customImport.fidelius.pojos.Pojo;
import org.openXpertya.process.customImport.utils.Utilidades;
import org.openXpertya.util.CLogger;

import com.google.gson.Gson;

/**
 * Metodo POST.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class Post {
	private static Gson gsonUtil = new Gson();

	private List<NameValuePair> params;
	private HttpPost postMethod;
	private HttpClient client;
	
	/** Logger. */
	private CLogger log;
	
	// --------------------------------------------------------------------------------------------
	
	
	// --------------------------------------------------------------------------------------------

	public Post(String url) {
		
		// dREHER 2024-04-12
		// Original client = HttpClientBuilder.create().build();
		
		if(Utilidades.getMetodoConnect().equals("1")) {
			client = HttpClientBuilder.create().build();
		}else {

			// ---------------------- nuevo codigo		
			HttpClientBuilder cb = HttpClientBuilder.create();
			SSLContextBuilder sslcb = new SSLContextBuilder();
			try {
				sslcb.loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()),
						new TrustSelfSignedStrategy());
				cb.setSslcontext(sslcb.build());
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			client = cb.build();
			// ----------------------------------------------- fin nuevo codigo		
		}		
		
		postMethod = new HttpPost(url);
		params = new ArrayList<NameValuePair>();
		
		log = CLogger.getCLogger(Post.class);
		
		log.warning("Post. Direccion a conectarse : " + url);
	}

	public void addParam(String key, String value) {
		params.add(new BasicNameValuePair(key, value));
	}

	public Pojo execute(Class<? extends Pojo> cls) throws Exception{
		postMethod.setEntity(new UrlEncodedFormEntity(params));
		HttpResponse response = null;
		String msg = null;
		
		log.warning("Post. Intentara conectarse a: " + postMethod + " Parametros=" + params);
		
		try {
			response = client.execute(postMethod);
		}catch(Exception ex) {
			msg = "Error al conectar servicio web. Error:" + ex.toString();
			log.warning(msg);
		}

		if(response==null)
			throw new Exception(msg);
		
		// dREHER agrego control de respuesta nula
		HttpEntity entity = response.getEntity();
		if(entity==null)
			throw new Exception("No se encontraron datos con los parametros seleccionados!");
		
		String responseStr = EntityUtils.toString(entity);
		
		Pojo resp = null;
		
/**		
 		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost("http://www.rfp.ca/login/");

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("username", "myusername"));
        nvps.add(new BasicNameValuePair("password", "mypassword"));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        response = httpclient.execute(httpost);
*/	
		
		resp = gsonUtil.fromJson(responseStr, cls);
		
		log.finest("Response: " + responseStr +  " Respuesta: " + resp);
		
		return resp;
	}

}
