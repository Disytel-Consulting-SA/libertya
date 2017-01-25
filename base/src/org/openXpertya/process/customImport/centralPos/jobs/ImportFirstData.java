package org.openXpertya.process.customImport.centralPos.jobs;

import java.util.List;
import java.util.Properties;

import org.openXpertya.process.customImport.centralPos.commons.CentralPosImport;
import org.openXpertya.process.customImport.centralPos.commons.FirstDataImport;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.http.utils.DefaultResponse;
import org.openXpertya.process.customImport.centralPos.pojos.TrailerParticipant;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Proceso de importación. FirstData.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportFirstData extends Import {

	public ImportFirstData(Properties ctx, String trxName) {
		super(new FirstDataImport(), ctx, trxName);
	}

	/**
	 * Inicia la importación.
	 * @return Total de elementos importados.
	 * @throws SaveFromAPIException
	 */
	public String excecute() throws SaveFromAPIException {
		DefaultResponse response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = centralPosImport.makeGetter("/trailer-participantes", token); // Metodo get para obtener trailer de participantes.
			get.addQueryParam("paginate", CentralPosImport.RESULTS_PER_PAGE); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : TrailerParticipant.filteredFields) {
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
			LinkedTreeMap<String, Object> trailerData = (LinkedTreeMap<String, Object>) response.get("trailer_participantes");

			currentPage = ((Double) trailerData.get("current_page")).intValue();
			lastPage = ((Double) trailerData.get("last_page")).intValue();

			@SuppressWarnings("unchecked")
			List<LinkedTreeMap<String, Object>> pageData = (List<LinkedTreeMap<String, Object>>) trailerData.get("data");

			// Por cada resultado, inserto en la tabla de importación.
			for (LinkedTreeMap<String, Object> itemResultMap : pageData) {
				TrailerParticipant trailerParticipant = new TrailerParticipant(itemResultMap);
				int no = trailerParticipant.save(ctx, trxName);
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
