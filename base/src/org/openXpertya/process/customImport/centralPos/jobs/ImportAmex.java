package org.openXpertya.process.customImport.centralPos.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.mapping.AmexPaymentsWithTaxes;
import org.openXpertya.process.customImport.centralPos.mapping.extras.AmexPayments;
import org.openXpertya.process.customImport.centralPos.mapping.extras.AmexTaxes;
import org.openXpertya.process.customImport.centralPos.pojos.amex.impuestos.AmexImpuestos;
import org.openXpertya.process.customImport.centralPos.pojos.amex.pagos.AmexPagos;

/**
 * Proceso de importación. Amex.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportAmex extends Import {

	public ImportAmex(Properties ctx, String trxName) throws Exception {
		super(EXTERNAL_SERVICE_AMEX, ctx, trxName);
	}

	@Override
	public String excecute() throws SaveFromAPIException {
		AmexPagos response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(); // Metodo get para obtener pagos.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
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
			response = (AmexPagos) get.execute(AmexPagos.class); // Ejecuto la consulta.

			currentPage = response.getPagos().getCurrentPage();
			lastPage = response.getPagos().getLastPage();

			List<String> secNumbers = new ArrayList<String>();
			List<AmexPayments> payments = new ArrayList<AmexPayments>();

			// Por cada resultado, inserto en la tabla de importación.
			List<org.openXpertya.process.customImport.centralPos.pojos.amex.pagos.Datum> data = response.getPagos().getData();

			for (org.openXpertya.process.customImport.centralPos.pojos.amex.pagos.Datum datum: data) {
				AmexPayments payment = new AmexPayments(datum);
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
		AmexImpuestos response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<AmexTaxes> taxes = new ArrayList<AmexTaxes>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(externalService.getAttributeByName("URL Impuestos").getName()); // Metodo get para obtener impuestos.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
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
			response = (AmexImpuestos) get.execute(AmexImpuestos.class); // Ejecuto la consulta.

			currentPage = response.getImpuestos().getCurrentPage();
			lastPage = response.getImpuestos().getLastPage();

			List<org.openXpertya.process.customImport.centralPos.pojos.amex.impuestos.Datum> data = response.getImpuestos().getData();

			for (org.openXpertya.process.customImport.centralPos.pojos.amex.impuestos.Datum datum: data) {
				AmexTaxes tax = new AmexTaxes(datum);
				taxes.add(tax);
			}
			currentPage++;
		}
		return taxes;
	}

}
