package org.openXpertya.process.customImport.centralPos.jobs;

import java.util.List;
import java.util.Properties;

import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.mapping.VisaPayments;
import org.openXpertya.process.customImport.centralPos.pojos.visa.pago.Datum;
import org.openXpertya.process.customImport.centralPos.pojos.visa.pago.VisaPagos;

/**
 * Proceso de importación. Visa.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportVisa extends Import {

	public ImportVisa(Properties ctx, String trxName) throws Exception {
		super(EXTERNAL_SERVICE_VISA, ctx, trxName);
	}

	@Override
	public String excecute() throws SaveFromAPIException {
		VisaPagos response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(); // Metodo get para obtener pagos de visa.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : VisaPayments.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}
			response = (VisaPagos) get.execute(VisaPagos.class); // Ejecuto la consulta.

			currentPage = response.getPagos().getCurrentPage();
			lastPage = response.getPagos().getLastPage();

			// Por cada resultado, inserto en la tabla de importación.
			List<Datum> data = response.getPagos().getData();

			for (Datum datum: data) {
				VisaPayments payment = new VisaPayments(datum);
				int no = payment.save(ctx, trxName);
				if (no > 0) {
					processed += no;
				} else if (no < 0) {
					areadyExists += (no * -1);
				}
			}
			log.info("Procesados = " + processed + ", Preexistentes = " + areadyExists + ", Pagina = " + currentPage + "/" + lastPage);
			currentPage++;
		}
		return msg(new Object[] { processed, areadyExists });
	}

}
