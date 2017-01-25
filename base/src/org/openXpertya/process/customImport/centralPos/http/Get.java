package org.openXpertya.process.customImport.centralPos.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Llamado GET.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class Get {
	private static Gson gsonUtil = new Gson();
	private LinkedHashMap<String, String> params;
	private RequestBuilder reqBuilder;
	private HttpUriRequest request;
	private HttpClient client;
	private String url;

	public Get(String url) {
		this.url = url;
		client = HttpClientBuilder.create().build();
		reqBuilder = RequestBuilder.get().setUri(url);
		addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		params = new LinkedHashMap<String, String>();
	}

	public void addHeader(String key, String value) {
		reqBuilder.setHeader(key, value);
	}

	public void addQueryParam(String key, Object value) {
		params.put(key, String.valueOf(value));
	}

	public void addQueryParams(Map<String, String> params) {
		this.params.putAll(params);
	}

	@Override
	public String toString() {
		RequestBuilder rb = RequestBuilder.get().setUri(url);
		for (String key : params.keySet()) {
			rb.addParameter(key, params.get(key));
		}
		HttpUriRequest req = rb.build();
		return req.getURI().toString();
	}

	public Map<String, Object> execute() {
		String responseStr = null;
		try {
			for (String key : params.keySet()) {
				reqBuilder.addParameter(key, params.get(key));
			}
			request = reqBuilder.build();
			HttpResponse response = client.execute(request);
			responseStr = EntityUtils.toString(response.getEntity());
			return gsonUtil.fromJson(responseStr, new TypeToken<Map<String, Object>>() {}.getType());
		} catch (JsonSyntaxException e) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("err_msg", responseStr);
			return result;
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
