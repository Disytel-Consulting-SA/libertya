package org.openXpertya.process.customImport.fidelius.http;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.openXpertya.process.customImport.fidelius.pojos.Pojo;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Cupon;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.CuponPendiente;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Datum;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Liquidacion;
import org.openXpertya.process.customImport.utils.Utilidades;
import org.openXpertya.util.CLogger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
	private static CLogger log;
	private String orgName;

	public Get(String url) {
		this.url = url;
		
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
		
		reqBuilder = RequestBuilder.get().setUri(url);
		addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		params = new LinkedHashMap<String, String>();
		
		log = CLogger.getCLogger(Get.class);
		
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

	public void explain() {
		System.out.println("Método = GET");
		System.out.println("URL = " + url);
		System.out.println("Params =");
		for (String key : params.keySet()) {
			System.out.println("\t" + key + " = " + params.get(key));
		}
	}

	public Pojo execute(Class<? extends Pojo> cls) throws Exception{
		
		for (String key : params.keySet()) {
			reqBuilder.addParameter(key, params.get(key));
		}
		
		request = reqBuilder.build();
		HttpResponse response = client.execute(request);
		
		// dREHER agrego control de respuesta nula
		HttpEntity entity = response.getEntity();
		if(entity==null)
			throw new Exception("No se encontraron datos con los parametros seleccionados!");
		
		String responseStr = EntityUtils.toString(entity);
		
		log.finest("Get: " + responseStr.substring(0, 300));
		
		return gsonUtil.fromJson(responseStr, cls);
	}
	
	public Pojo[] executeList(Class<? extends Pojo> cls) throws Exception{
		
		for (String key : params.keySet()) {
			reqBuilder.addParameter(key, params.get(key));
		}
		
		request = reqBuilder.build();
		HttpResponse response = client.execute(request);
		
		// dREHER agrego control de respuesta nula
		HttpEntity entity = response.getEntity();
		if(entity==null)
			throw new Exception("No se encontraron datos con los parametros seleccionados!");
		
		String responseStr = EntityUtils.toString(entity);
		
		log.finest("Get executeList: " + responseStr.substring(0, 300));
		
		Pojo[] listSection = gsonUtil.fromJson(responseStr, Pojo[].class); 
		
		return listSection;
	}
	
	public ArrayList<Liquidacion> getDataLiquidacion() throws Exception{
		
		ArrayList<Liquidacion> respuesta = new ArrayList<Liquidacion>();
		
		for (String key : params.keySet()) {
			reqBuilder.addParameter(key, params.get(key));
		}
		
		request = reqBuilder.build();
		HttpResponse response = client.execute(request);
		
		// dREHER agrego control de respuesta nula
		HttpEntity entity = response.getEntity();
		if(entity==null)
			throw new Exception("No se encontraron datos con los parametros seleccionados!");

		String responseStr = EntityUtils.toString(entity);
		
		int len = responseStr.length();
		if(len > 300)
			len = 300;
		log.finest("Get getDataLiquidaciones: " + responseStr.substring(0, len));
		
		 JsonElement jelement = new JsonParser().parse(responseStr);
		 JsonObject  jobject = jelement.getAsJsonObject();
		 
		 JsonElement jstatus = jobject.get("status");
		 int status = jstatus.getAsInt();
		 if(status!=1) {
			 log.warning("No se leyo liquidaciones correctamente!!! status=" + status);
			 return respuesta;
		 }
		 
		 JsonArray jarray = jobject.getAsJsonArray("data");

		 int liquidaciones = 0;
		 
		 try {

			 for(JsonElement j: jarray) {

				 liquidaciones++;
				 
				 if(j==null)
					 continue;

				 Liquidacion data = new Liquidacion();

				 JsonArray linea = j.getAsJsonArray();

				 int z = 0;
				 for(JsonElement ele: linea) {

					 System.out.println("Liquidacion: " + liquidaciones + " Elemento: " + z);
					 
					 if(ele==null || ele == JsonNull.INSTANCE) {
						 log.warning("Elemento nulo, saltear!");
						 z++;
						 continue;
					 }
					 
					 String result = ele.getAsString();
					 switch(z) {
					 case 0: data.setFechaPago(Utilidades.StringToTimestamp(result)); 
					 break;
					 case 1: data.setFechaPresentacion(Utilidades.StringToTimestamp(result)); 
					 break;
					 case 2: data.setFechaAnticipo(Utilidades.StringToTimestamp(result)); 
					 break;
					 case 3: data.setNroLiquidacion(Utilidades.StringToLong(result)); 
					 break;
					 case 4: data.setAntic(result); 
					 break;
					 case 5: data.setTarjeta(result); 
					 break;
					 case 6: data.setBancoPagador(result); 
					 break;
					 case 7: data.setNroComercio(result); 
					 break;
					// dREHER la nueva version 2024-04-12 Fidelius
					// case 8: data.setProvinciaIIBB(result); 
					// break;
					 case 8: data.setBruto(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 9: data.setNeto(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 10: data.setTotalDesc(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 11: data.setPromo(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 12: data.setArancel(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 13: data.setIvaArancel(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 14: data.setCFOTotal(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 15: data.setCFO21(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 16: data.setCFO105(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 17: data.setCFOAdel(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 18: data.setIVACFO21(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 19: data.setIVACFO105(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 20: data.setIVAAdel21(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 21: data.setIVATotal(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 22: data.setRetIIBB(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 23: data.setRetIBSIRTAC(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 24: data.setRetIVA(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 25: data.setRetGcia(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 26: data.setPercIVA(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 27: data.setPercIIBB(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 28: data.setRetMunic(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 29: data.setLiqAntTN(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 30: data.setPerc1135TN(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 31: data.setDtoFinanc(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 32: data.setIVADtoFinanc(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 33: data.setDebCred(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 34: data.setSaldos(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 35: data.setOtrosCostos(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 36: data.setIVAOtros(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 37: data.setPlanA1218(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 38: data.setIVAPlanA1218(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 39: data.setPorIvaPlana1218(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 40: data.setCUIT(result); 
					 break;

					 default:
						 ;
					 }
					 z++;
				 }


				 respuesta.add(data);
			 }

		 }catch(Exception ex) {
			 log.warning("Se produjo un error al importar Liquidaciones. " + ex.toString() 
			 + "\n" +
			 "Liquidacion: " + liquidaciones);
		 }

		 return respuesta;
	}
	
	public ArrayList<Cupon> getDataCupones() throws Exception{
		
		ArrayList<Cupon> respuesta = new ArrayList<Cupon>();
		
		for (String key : params.keySet()) {
			reqBuilder.addParameter(key, params.get(key));
		}
		
		request = reqBuilder.build();
		HttpResponse response = client.execute(request);
		
		// dREHER agrego control de respuesta nula
		HttpEntity entity = response.getEntity();
		if(entity==null)
			throw new Exception("No se encontraron datos con los parametros seleccionados!");
		
		String responseStr = EntityUtils.toString(entity);
		
		//StringBuffer sb = new StringBuffer();
		//sb.append(responseStr);
		//Buffer2Disk(sb);
		
		int len = responseStr.length();
		if(len > 300)
			len = 300;
		log.info("Get getDataCupones: " + responseStr.substring(0, len));
		
		 JsonElement jelement = new JsonParser().parse(responseStr);
		 JsonObject  jobject = jelement.getAsJsonObject();
		 
		 JsonElement jstatus = jobject.get("status");
		 int status = jstatus.getAsInt();
		 if(status!=1) {
			 log.warning("No se leyeron cupones correctamente!!! status=" + status);
			 return respuesta;
		 }
		 
		 JsonArray jarray = jobject.getAsJsonArray("data");
		 
		 int registro=0;
		 try {
			
			 for(JsonElement j: jarray) {

				 Cupon data = new Cupon();
				 registro++;
				 
				 if(j==null || j == JsonNull.INSTANCE) {
					 log.warning("Elemento nulo, saltea...");
					 continue;
				 }
				 
				 JsonArray linea = j.getAsJsonArray();
				 log.info(registro + " " + linea);

				 int z = 0;
				 for(JsonElement ele: linea) {

					 if(ele==null) {
						 z++;
						 continue;
					 }
					 
					 String result = ele.getAsString();
					 switch(z) {
					 case 0: data.setFechaVenta(Utilidades.StringToTimestamp(result)); 
					 break;
					 case 1: data.setFechaPago(Utilidades.StringToTimestamp(result)); 
					 break;
					 case 2: data.setFechaAnticipo(Utilidades.StringToTimestamp(result)); 
					 break;
					 case 3: data.setNroLiquidacion(Utilidades.StringToLong(result)); 
					 break;
					 case 4: data.setNroEquipo(Utilidades.StringToInt(result)); 
					 break;
					 case 5: data.setNomEquipo(result); 
					 break;
					 case 6: data.setNroLote(Utilidades.StringToInt(result)); 
					 break;
					 case 7: data.setNroCupon(Utilidades.StringToInt(result)); 
					 break;
					 case 8: data.setTarjeta(result);
					 break;
					 case 9: 
						 if(result.length() > 4)
							 result = result.substring(result.length() - 4);
						 data.setUlt4tarjeta(Utilidades.StringToInt(result));
						 break;
					 case 10: data.setAutorizacion(result); 
					 break;
					 case 11: 
						 
						 // dREHER validar que solo vengan numeros Naranja Z envia "Z" en cuotas...
						 if(result.indexOf("0123456789") > -1)
							 data.setCuotas(Utilidades.StringToInt(result));
						 else
							 data.setCuotas(1);
						 
					 break;
					 case 12: data.setImporteVenta(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 13: data.setExtraCash(result); 
					 break;
					 case 14: data.setNroComercio(result); 
					 break;
					 case 15: data.setBancoPagador(result); 
					 break;
					 case 16: data.setRechazo(result); 
					 break;
					 case 17: data.setArancel(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 18: data.setIvaArancel(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 19: data.setCFO(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 20: data.setIvaCFO(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 21: data.setAlicIvaCFO(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 22: data.setTipoOperacion(result); 
					 break;
					 case 23: data.setIDUnico(result); 
					 break;

					 default:
						 ;
					 }
					 z++;
				 }

				 respuesta.add(data);
			 }
		 }catch(Exception ex) {
			 log.warning("Se produjo un error al importar cupones. " + ex.toString() 
			 + "\n" +
			"Cupon: " + registro);
		 }
		
		 return respuesta;
	}
	
	/**  
	 * Devuelve la informacion de cupones pendientes
	 * @return
	 * @throws Exception
	 * dREHER
	 */
	public ArrayList<CuponPendiente> getDataPendientes() throws Exception{
		
		ArrayList<CuponPendiente> respuesta = new ArrayList<CuponPendiente>();
		
		for (String key : params.keySet()) {
			reqBuilder.addParameter(key, params.get(key));
		}
		
		request = reqBuilder.build();
		HttpResponse response = client.execute(request);
		
		// dREHER agrego control de respuesta nula
		HttpEntity entity = response.getEntity();
		if(entity==null)
			throw new Exception("No se encontraron datos con los parametros seleccionados!");
		
		String responseStr = EntityUtils.toString(entity);
		
		//StringBuffer sb = new StringBuffer();
		//sb.append(responseStr);
		//Buffer2Disk(sb);
		
		int len = responseStr.length();
		if(len > 300)
			len = 300;
		log.info("Get getDataCuponesPendientes: " + responseStr.substring(0, len));
		
		 JsonElement jelement = new JsonParser().parse(responseStr);
		 JsonObject  jobject = jelement.getAsJsonObject();
		 
		 
		 JsonElement jstatus = jobject.get("status");
		 int status = jstatus.getAsInt();
		 if(status!=1) {
			 log.warning("No se leyo cupones pendientes correctamente!!! status=" + status);
			 return respuesta;
		 }
		 
		 JsonArray jarray = jobject.getAsJsonArray("data");
		 
		 int registro=0;
		 try {
			
			 for(JsonElement j: jarray) {

				 CuponPendiente data = new CuponPendiente();
				 registro++;
				 
				 if(j==null || j == JsonNull.INSTANCE) {
					 log.warning("Elemento nulo, saltea...");
					 continue;
				 }
				 
				 JsonArray linea = j.getAsJsonArray();
				 log.info(registro + " " + linea);

				 int z = 0;
				 for(JsonElement ele: linea) {

					 if(ele==null) {
						 z++;
						 continue;
					 }
					 
					 String result = ele.getAsString();
					 switch(z) {
					 case 0: data.setFechaOper(Utilidades.StringToTimestamp(result)); 
					 break;
					 case 1: data.setHoraOper(result); 
					 break;
					 case 2: data.setNroTerminal(Utilidades.StringToLong(result)); 
					 break;
					 case 3: data.setEquipo(result); 
					 break;
					 case 4: data.setNombre_Comerc(result); 
					 break;
					 case 5: data.setTipoTrx(result); 
					 break;
					 case 6: data.setId_Clover(result); 
					 break;
					 case 7: data.setCodCom(result); 
					 break;
					 case 8: data.setNroLote(Utilidades.StringToInt(result));
					 break;
					 case 9: data.setTicket(Utilidades.StringToInt(result));
					 break;
					 case 10: data.setCodAut(result); 
					 break;
					 case 11: data.setFactura(result);
					 break;
					 case 12: data.setTarjeta(result); 
					 break;
					 case 13: data.setNroTarjeta(result); 
					 break;
					 case 14: data.setCuotaTipeada(Utilidades.StringToInt(result)); 
					 break;
					 case 15: data.setImporte(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 16: data.setMontoSec(Utilidades.StringToBigDecimal(result)); 
					 break;
					 case 17: data.setFechaPagoEst(Utilidades.StringToTimestamp(result)); 
					 break;
					 case 18: data.setId(Utilidades.StringToInt(result)); 
					 break;

					 default:
						 ;
					 }
					 z++;
				 }
				 
				 
				 boolean isAppend = true;
				 // Si se setea el nombre del comercio, solo agregarlo si es el mismo
				 if(getOrgName()!=null && !getOrgName().equals(data.getNombre_Comerc())) {
					 isAppend = false;
					 log.info("No corresponde el comercio, no importar...");
				 }
				 
				 if(isAppend) respuesta.add(data);
			 }
		 }catch(Exception ex) {
			 log.warning("Se produjo un error al importar cupones pendientes. " + ex.toString() 
			 + "\n" +
			"Cupon Pendiente: " + registro);
		 }
		
		 return respuesta;
	}
	
	private void Buffer2Disk(StringBuffer sb) {
		BufferedWriter writer = null;
		String filePath = "/home/jorge/disytel/Cupones.txt";

        try {
            // Crear un BufferedWriter para escribir en el archivo
            writer = new BufferedWriter(new FileWriter(filePath));

            // Escribir el contenido del StringBuffer en el archivo
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Cerrar el BufferedWriter
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	
}
