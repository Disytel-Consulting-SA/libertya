package org.openXpertya.process.customImport.centralPos.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.mapping.NaranjaPayments;
import org.openXpertya.process.customImport.centralPos.mapping.extras.NaranjaCoupons;
import org.openXpertya.process.customImport.centralPos.mapping.extras.NaranjaHeaders;
import org.openXpertya.process.customImport.centralPos.mapping.extras.NaranjaInvoicedConcepts;
import org.openXpertya.process.customImport.centralPos.pojos.naranja.conceptos.Conceptos;
import org.openXpertya.process.customImport.centralPos.pojos.naranja.cupones.Detalle;
import org.openXpertya.process.customImport.centralPos.pojos.naranja.headers.Headers;

/**
 * Proceso de importación. Naranja.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportNaranja extends Import {

	public ImportNaranja(Properties ctx, String trxName) throws Exception {
		super(EXTERNAL_SERVICE_NARANJA, ctx, trxName);
	}

	@Override
	public String excecute() throws SaveFromAPIException {
		return importNaranjaCoupons();
	}

	private String importNaranjaCoupons() throws SaveFromAPIException {
		Detalle response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(); // Metodo get para obtener detalle de cupones con vencimiento en el mes de pago.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
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
			response = (Detalle) get.execute(Detalle.class); // Ejecuto la consulta.

			currentPage = response.getCupones().getCurrentPage();
			lastPage = response.getCupones().getLastPage();

			Set<Map<String, String>> matchingFields = new HashSet<Map<String, String>>();
			List<NaranjaCoupons> coupons = new ArrayList<NaranjaCoupons>();

			for (org.openXpertya.process.customImport.centralPos.pojos.naranja.cupones.Datum datum : response.getCupones().getData()) {
				NaranjaCoupons coupon = new NaranjaCoupons(datum);

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

	private List<NaranjaHeaders> importNaranjaHeaders(Set<Map<String, String>> matchingFields) throws SaveFromAPIException {
		Headers response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<NaranjaHeaders> headers = new ArrayList<NaranjaHeaders>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(externalService.getAttributeByName("URL Headers").getName()); // Metodo get para obtener headers.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
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
			response = (Headers) get.execute(Headers.class); // Ejecuto la consulta.

			currentPage = response.getHeaders().getCurrentPage();
			lastPage = response.getHeaders().getLastPage();

			for (org.openXpertya.process.customImport.centralPos.pojos.naranja.headers.Datum datum : response.getHeaders().getData()) {
				NaranjaHeaders header = new NaranjaHeaders(datum);
				headers.add(header);
			}
			currentPage++;
		}
		return headers;
	}

	private List<NaranjaInvoicedConcepts> importNaranjaInvoicedConcepts(Set<Map<String, String>> matchingFields) throws SaveFromAPIException {
		Conceptos response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<NaranjaInvoicedConcepts> invoicedConcepts = new ArrayList<NaranjaInvoicedConcepts>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			// Metodo get para obtener conceptos facturados a descontar en el mes de pago.
			get = makeGetter(externalService.getAttributeByName("URL Conceptos").getName());
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
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
			response = (Conceptos) get.execute(Conceptos.class); // Ejecuto la consulta.

			currentPage = response.getConceptosFacturadosMeses().getCurrentPage();
			lastPage = response.getConceptosFacturadosMeses().getLastPage();

			for (org.openXpertya.process.customImport.centralPos.pojos.naranja.conceptos.Datum datum : response.getConceptosFacturadosMeses().getData()) {
				NaranjaInvoicedConcepts invoicedConcept = new NaranjaInvoicedConcepts(datum);
				invoicedConcepts.add(invoicedConcept);
			}
			currentPage++;
		}
		return invoicedConcepts;
	}

}
