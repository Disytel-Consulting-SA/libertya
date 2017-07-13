package org.openXpertya.process.customImport.centralPos.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.mapping.FirstDataTrailerAndDetail;
import org.openXpertya.process.customImport.centralPos.mapping.extras.DetailParticipant;
import org.openXpertya.process.customImport.centralPos.mapping.extras.TrailerParticipants;
import org.openXpertya.process.customImport.centralPos.pojos.firstdata.detalle.Detalle;
import org.openXpertya.process.customImport.centralPos.pojos.firstdata.trailer.Trailer;

/**
 * Proceso de importación. FirstData.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportFirstData extends Import {

	public ImportFirstData(Properties ctx, String trxName) throws Exception {
		super(EXTERNAL_SERVICE_FIRSTDATA, ctx, trxName);
	}

	/**
	 * Inicia la importación.
	 * @return Total de elementos importados.
	 * @throws SaveFromAPIException
	 */
	public String excecute() throws SaveFromAPIException {
		Trailer response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(); // Metodo get para obtener trailer de participantes.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : TrailerParticipants.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}
			response = (Trailer) get.execute(Trailer.class); // Ejecuto la consulta.

			currentPage = response.getTrailerParticipantes().getCurrentPage();
			lastPage = response.getTrailerParticipantes().getLastPage();

			List<String> settlementNumbers = new ArrayList<String>();
			List<TrailerParticipants> trailers = new ArrayList<TrailerParticipants>();

			// Por cada resultado, inserto en la tabla de importación.
			for (org.openXpertya.process.customImport.centralPos.pojos.firstdata.trailer.Datum itemResultMap : response.getTrailerParticipantes().getData()) {
				TrailerParticipants tp = new TrailerParticipants(itemResultMap);
				settlementNumbers.add(tp.getSettlementNo());
				trailers.add(tp);
			}

			List<DetailParticipant> details = importFirstDataDetails(settlementNumbers);
			for (TrailerParticipants trailer : trailers) {
				if (trailer != null) {
					FirstDataTrailerAndDetail fdtad = null;
					for (DetailParticipant detail : details) {
						if (detail != null) {
							if (FirstDataTrailerAndDetail.match(trailer, detail)) {
								fdtad = new FirstDataTrailerAndDetail(trailer, detail);
								int no = fdtad.save(ctx, trxName);
								if (no > 0) {
									processed += no;
								} else if (no < 0) {
									areadyExists += (no * -1);
								}
							}
						}
					}
				}
			}
			log.info("Procesados = " + processed + ", Preexistentes = " + areadyExists + ", Pagina = " + currentPage + "/" + lastPage);
			currentPage++;
		}
		return msg(new Object[] { processed, areadyExists });
	}

	private List<DetailParticipant> importFirstDataDetails(List<String> settlementNumbers) throws SaveFromAPIException {
		Detalle response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<DetailParticipant> taxes = new ArrayList<DetailParticipant>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			// Metodo get para obtener detalles de liquidación.
			get = makeGetter(externalService.getAttributeByName("URL detalle liq").getName());
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			// Filtro por "número de liquidación".
			StringBuffer settlementNoStr = new StringBuffer();
			for (String s : settlementNumbers) {
				settlementNoStr.append(s + ",");
			}
			if (settlementNoStr.length() > 0) {
				settlementNoStr.deleteCharAt(settlementNoStr.length() - 1);
				get.addQueryParam("numero_liquidacion-in", settlementNoStr);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : DetailParticipant.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}

			response = (Detalle) get.execute(Detalle.class); // Ejecuto la consulta.

			lastPage = response.getLiquidacionParticipantes().getLastPage();
			currentPage = response.getLiquidacionParticipantes().getCurrentPage();

			for (org.openXpertya.process.customImport.centralPos.pojos.firstdata.detalle.Datum itemResultMap : response.getLiquidacionParticipantes().getData()) {
				DetailParticipant tax = new DetailParticipant(itemResultMap);
				taxes.add(tax);
			}
			currentPage++;
		}
		return taxes;
	}

}
