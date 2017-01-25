package org.openXpertya.process.customImport.centralPos.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.process.customImport.centralPos.commons.AmexImport;
import org.openXpertya.process.customImport.centralPos.commons.CentralPosImport;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.http.utils.DefaultResponse;
import org.openXpertya.process.customImport.centralPos.pojos.AmexPaymentsWithTaxes;
import org.openXpertya.process.customImport.centralPos.pojos.extras.AmexPayments;
import org.openXpertya.process.customImport.centralPos.pojos.extras.AmexTaxes;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Proceso de importación. Amex.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportAmex extends Import {

	public ImportAmex(Properties ctx, String trxName) {
		super(new AmexImport(), ctx, trxName);
	}

	@Override
	public String excecute() throws SaveFromAPIException {
		return importAmexPayments();
	}

	private String importAmexPayments() throws SaveFromAPIException {
		DefaultResponse response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = centralPosImport.makeGetter("/pagos", token); // Metodo get para obtener pagos.
			get.addQueryParam("paginate", CentralPosImport.RESULTS_PER_PAGE); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : AmexPayments.filteredFields) {
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
			LinkedTreeMap<String, Object> paymentData = (LinkedTreeMap<String, Object>) response.get("pagos");

			currentPage = ((Double) paymentData.get("current_page")).intValue();
			lastPage = ((Double) paymentData.get("last_page")).intValue();

			@SuppressWarnings("unchecked")
			List<LinkedTreeMap<String, Object>> pageData = (List<LinkedTreeMap<String, Object>>) paymentData.get("data");

			List<String> secNumbers = new ArrayList<String>();
			List<AmexPayments> payments = new ArrayList<AmexPayments>();

			// Por cada resultado, inserto en la tabla de importación.
			for (LinkedTreeMap<String, Object> itemResultMap : pageData) {
				AmexPayments payment = new AmexPayments(itemResultMap);
				secNumbers.add(payment.getNumSecPago());
				payments.add(payment);
			}

			List<AmexTaxes> taxes = importAmexTaxes(secNumbers);
			for (AmexPayments payment : payments) {
				if (payment != null) {
					AmexPaymentsWithTaxes apwt = null;
					for (AmexTaxes tax : taxes) {
						if (tax != null) {
							if (AmexPaymentsWithTaxes.match(payment, tax)) {
								apwt = new AmexPaymentsWithTaxes(payment, tax);
								break;
							}
						}
					}
					if (apwt == null) {
						apwt = new AmexPaymentsWithTaxes(payment);
					}
					int no = apwt.save(ctx, trxName);
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

	private List<AmexTaxes> importAmexTaxes(List<String> secNumbers) throws SaveFromAPIException {
		DefaultResponse response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<AmexTaxes> taxes = new ArrayList<AmexTaxes>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = centralPosImport.makeGetter("/impuestos", token); // Metodo get para obtener impuestos.
			get.addQueryParam("paginate", CentralPosImport.RESULTS_PER_PAGE); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			// Filtro por "número secuencial de pago".
			StringBuffer secNumbersStr = new StringBuffer();
			for (String s : secNumbers) {
				secNumbersStr.append(s + ",");
			}
			if (secNumbersStr.length() > 0) {
				secNumbersStr.deleteCharAt(secNumbersStr.length() - 1);
				get.addQueryParam("num_sec_pago-in", secNumbersStr);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : AmexTaxes.filteredFields) {
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
			LinkedTreeMap<String, Object> taxData = (LinkedTreeMap<String, Object>) response.get("impuestos");

			currentPage = ((Double) taxData.get("current_page")).intValue();
			lastPage = ((Double) taxData.get("last_page")).intValue();

			@SuppressWarnings("unchecked")
			List<LinkedTreeMap<String, Object>> pageData = (List<LinkedTreeMap<String, Object>>) taxData.get("data");

			for (LinkedTreeMap<String, Object> itemResultMap : pageData) {
				AmexTaxes tax = new AmexTaxes(itemResultMap);
				taxes.add(tax);
			}
			currentPage++;
		}
		return taxes;
	}

}
