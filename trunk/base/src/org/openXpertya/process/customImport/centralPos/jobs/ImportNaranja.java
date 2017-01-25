package org.openXpertya.process.customImport.centralPos.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.process.customImport.centralPos.commons.CentralPosImport;
import org.openXpertya.process.customImport.centralPos.commons.NaranjaImport;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.http.utils.DefaultResponse;
import org.openXpertya.process.customImport.centralPos.pojos.NaranjaPayments;
import org.openXpertya.process.customImport.centralPos.pojos.extras.NaranjaCoupons;
import org.openXpertya.process.customImport.centralPos.pojos.extras.NaranjaHeaders;
import org.openXpertya.process.customImport.centralPos.pojos.extras.NaranjaInvoicedConcepts;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Proceso de importación. Naranja.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportNaranja extends Import {

	public ImportNaranja(Properties ctx, String trxName) {
		super(new NaranjaImport(), ctx, trxName);
	}

	@Override
	public String excecute() throws SaveFromAPIException {
		return importNaranjaCoupons();
	}

	private String importNaranjaCoupons() throws SaveFromAPIException {
		DefaultResponse response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = centralPosImport.makeGetter("/cupones", token); // Metodo get para obtener detalle de cupones con vencimiento en el mes de pago.
			get.addQueryParam("paginate", CentralPosImport.RESULTS_PER_PAGE); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : NaranjaCoupons.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}

			response = new DefaultResponse(get.execute()); // Ejecuto la consulta.

			if (response.get("err_msg") != null) {
				throw new SaveFromAPIException(Msg.getMsg(Env.getAD_Language(ctx), "CentralPosUnexpectedError"));
			}

			@SuppressWarnings("unchecked")
			LinkedTreeMap<String, Object> couponData = (LinkedTreeMap<String, Object>) response.get("cupones");

			currentPage = ((Double) couponData.get("current_page")).intValue();
			lastPage = ((Double) couponData.get("last_page")).intValue();

			@SuppressWarnings("unchecked")
			List<LinkedTreeMap<String, Object>> pageData = (List<LinkedTreeMap<String, Object>>) couponData.get("data");

			List<Map<String, String>> matchingFields = new ArrayList<Map<String, String>>();
			List<NaranjaCoupons> coupons = new ArrayList<NaranjaCoupons>();

			for (LinkedTreeMap<String, Object> itemResultMap : pageData) {
				NaranjaCoupons coupon = new NaranjaCoupons(itemResultMap);

				Map<String, String> leFields = new HashMap<String, String>();
				leFields.put("comercio", (String) coupon.getValue("comercio"));
				leFields.put("fecha_pago", (String) coupon.getValue("fecha_pago"));

				matchingFields.add(leFields);

				coupons.add(coupon);
			}

			List<NaranjaHeaders> headers = importNaranjaHeaders(matchingFields);
			List<NaranjaInvoicedConcepts> invConcepts = importNaranjaInvoicedConcepts(matchingFields);
			for (NaranjaCoupons coupon : coupons) {
				if (coupon != null) {
					NaranjaPayments payment = new NaranjaPayments(coupon);

					for (NaranjaHeaders header : headers) {
						if (header != null) {
							if (NaranjaPayments.match(coupon, header)) {
								payment.setHeader(header);
								break;
							}
						}
					}
					for (NaranjaInvoicedConcepts invConcept : invConcepts) {
						if (invConcept != null) {
							if (NaranjaPayments.match(coupon, invConcept)) {
								payment.setInvoicedConcept(invConcept);
								break;
							}
						}
					}
					int no = payment.save(ctx, trxName);
					if (no > 0) {
						processed += no;
					} else if (no < 0) {
						areadyExists += (no * -1);
					}
				}
			}
			log.info("Procesados = " + processed + ", Preexistentes = " + areadyExists + ", Pagina = " + currentPage + "/" + lastPage);
			currentPage++;
		}
		return msg(new Object[] { processed, areadyExists });
	}

	private List<NaranjaHeaders> importNaranjaHeaders(List<Map<String, String>> matchingFields) throws SaveFromAPIException {
		DefaultResponse response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<NaranjaHeaders> headers = new ArrayList<NaranjaHeaders>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = centralPosImport.makeGetter("/headers", token); // Metodo get para obtener headers.
			get.addQueryParam("paginate", CentralPosImport.RESULTS_PER_PAGE); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			// Filtro por campos de matching.
			NaranjaPayments p = new NaranjaPayments();
			String[] fieldNames = p.matchingFields;

			for (String field : fieldNames) {
				StringBuffer tmpStr = new StringBuffer();
				for (Map<String, String> map : matchingFields) {
					String str = map.get(field);
					if (str != null) {
						tmpStr.append(str + ",");
					}
				}
				if (tmpStr.length() > 0) {
					tmpStr.deleteCharAt(tmpStr.length() - 1);
					get.addQueryParam(field + "-in", tmpStr);
				}
			}

			StringBuffer fields = new StringBuffer();
			for (String field : NaranjaHeaders.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}

			response = new DefaultResponse(get.execute()); // Ejecuto la consulta.

			if (response.get("err_msg") != null) {
				throw new SaveFromAPIException(Msg.getMsg(Env.getAD_Language(ctx), "CentralPosUnexpectedError"));
			}

			@SuppressWarnings("unchecked")
			LinkedTreeMap<String, Object> headerData = (LinkedTreeMap<String, Object>) response.get("headers");

			currentPage = ((Double) headerData.get("current_page")).intValue();
			lastPage = ((Double) headerData.get("last_page")).intValue();

			@SuppressWarnings("unchecked")
			List<LinkedTreeMap<String, Object>> pageData = (List<LinkedTreeMap<String, Object>>) headerData.get("data");

			for (LinkedTreeMap<String, Object> itemResultMap : pageData) {
				NaranjaHeaders header = new NaranjaHeaders(itemResultMap);
				headers.add(header);
			}
			currentPage++;
		}
		return headers;
	}

	private List<NaranjaInvoicedConcepts> importNaranjaInvoicedConcepts(List<Map<String, String>> matchingFields) throws SaveFromAPIException {
		DefaultResponse response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<NaranjaInvoicedConcepts> invoicedConcepts = new ArrayList<NaranjaInvoicedConcepts>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = centralPosImport.makeGetter("/conceptos-facturados-meses", token); // Metodo get para obtener conceptos facturados a descontar en el mes de pago.
			get.addQueryParam("paginate", CentralPosImport.RESULTS_PER_PAGE); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			// Filtro por campos de matching.
			NaranjaPayments p = new NaranjaPayments();
			String[] fieldNames = p.matchingFields;

			for (String field : fieldNames) {
				StringBuffer tmpStr = new StringBuffer();
				for (Map<String, String> map : matchingFields) {
					String str = map.get(field);
					if (str != null) {
						tmpStr.append(str + ",");
					}
				}
				if (tmpStr.length() > 0) {
					tmpStr.deleteCharAt(tmpStr.length() - 1);
					get.addQueryParam(field + "-in", tmpStr);
				}
			}

			StringBuffer fields = new StringBuffer();
			for (String field : NaranjaInvoicedConcepts.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}

			response = new DefaultResponse(get.execute()); // Ejecuto la consulta.

			if (response.get("err_msg") != null) {
				throw new SaveFromAPIException(Msg.getMsg(Env.getAD_Language(ctx), "CentralPosUnexpectedError"));
			}

			@SuppressWarnings("unchecked")
			LinkedTreeMap<String, Object> conceptsData = (LinkedTreeMap<String, Object>) response.get("conceptos_facturados_meses");

			currentPage = ((Double) conceptsData.get("current_page")).intValue();
			lastPage = ((Double) conceptsData.get("last_page")).intValue();

			@SuppressWarnings("unchecked")
			List<LinkedTreeMap<String, Object>> pageData = (List<LinkedTreeMap<String, Object>>) conceptsData.get("data");

			for (LinkedTreeMap<String, Object> itemResultMap : pageData) {
				NaranjaInvoicedConcepts invoicedConcept = new NaranjaInvoicedConcepts(itemResultMap);
				invoicedConcepts.add(invoicedConcept);
			}
			currentPage++;
		}
		return invoicedConcepts;
	}

}
