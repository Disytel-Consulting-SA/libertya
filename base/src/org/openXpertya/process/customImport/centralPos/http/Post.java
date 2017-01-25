package org.openXpertya.process.customImport.centralPos.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

	public Post(String url) {
		client = HttpClientBuilder.create().build();
		postMethod = new HttpPost(url);
		params = new ArrayList<NameValuePair>();
	}

	public void addParam(String key, String value) {
		params.add(new BasicNameValuePair(key, value));
	}

	public Map<String, Object> execute() {
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = client.execute(postMethod);

			return gsonUtil.fromJson(EntityUtils.toString(response.getEntity()), new TypeToken<Map<String, Object>>(){}.getType());

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
