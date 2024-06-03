package org.openXpertya.process;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MExternalService;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MJacoferRoadMap;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.PresentacionCOT;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GenerateCOTProcess extends ExportProcess {
	private Properties localCtx;
	private String localTrxName;
	private String filePath;
	private int HojaDeRuta = 0;
	private boolean presentacion = false;
	private int lineasExportadas = 0;
	private StringBuffer errorLog = new StringBuffer();
	
	@Override
	protected void prepare() {
		super.prepare();
		ProcessInfoParameter[] para = getParameter();
		localCtx = getCtx();
		localTrxName = get_TrxName();
		
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null) ;
			else if (name.equals("HojaDeRuta"))
				HojaDeRuta = para[i].getParameterAsInt();
			else if (name.equals("Presentacion"))
				presentacion = para[i].getParameter().equals("Y");
		}
	}

	@Override
	protected String doIt() throws Exception {
		if (HojaDeRuta == 0)
			return "Error al seleccionar la Hoja de Ruta.";
		
		String exportDocumentMsg = super.doIt();
		String uploadMsg = "";
		
		//Presentar archivo COT en Arba?
		if (this.presentacion) {
			
			//obtener configuracion y archivo cot
			MExternalService cotArbaExternalService = getCotArbaExternalService();
			File cotFile = new File(this.filePath);
			PresentacionCOT presentacion = new PresentacionCOT(cotArbaExternalService, cotFile);
			//Presentar file
			uploadMsg = presentacion.presentar();
		}
		
		return exportDocumentMsg + uploadMsg;
	}
	
	protected MExternalService getCotArbaExternalService() throws Exception {
		
		MExternalService cotArbaExternalService = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;		
		try {
			ps = DB.prepareStatement("SELECT C_ExternalService_ID FROM C_ExternalService WHERE value = 'COT'");
			rs = ps.executeQuery();
			if(rs.next()) {				
				cotArbaExternalService = new MExternalService(localCtx,rs.getInt("C_ExternalService_ID"),localTrxName);
			}			
		} catch (Exception e) {
			log.log(Level.SEVERE, "No existe un Servicio Externo con clave COT", e);
			throw e;
		}
		return cotArbaExternalService;
	}
	
	
	
	
	@Override
	protected void loadExpFormatRows() {
		return;
	}
	
	@Override
	protected void createDocument() throws IOException {
		//obtener path
		this.filePath = getFilePath();
		
		//crear parent dirs
		File file = new File(this.filePath);
		file.getParentFile().mkdirs();
		
		setExportFile(file);
	    FileOutputStream fos = new FileOutputStream(this.filePath);
	    OutputStreamWriter osw = new OutputStreamWriter(fos, this.getEncodingType());
	    BufferedWriter bw = new BufferedWriter(osw);
	    
	    setFileWriter(new PrintWriter(bw));	
	}
	
	@Override
	protected void fillDocument() {
		try {
			write(getFileHeader());
			write(getFileBody());
			write(getFileFooter());
		} catch (Exception e) {
			System.out.println("Error al escribir el archivo.");
			e.printStackTrace();
		}

	}
	
	@Override
	protected void saveDocument() throws Exception {		
		// Cierro el archivo
		getFileWriter().close();
		
		if(!errorLog.toString().isEmpty()) {
			// Si tenemos un errorLog, borramos el archivo creado y lanzamos una exception
			File f = new File(filePath);
			f.delete();
			throw new Exception(errorLog.toString());
		}

		// Guardo la última fecha de exportación del formato
		if(getExportFormat() != null){
			getExportFormat().setDateExportNo(getDayExportNo());
			getExportFormat().setLastExportedDate(Env.getTimestamp());
			if(!getExportFormat().save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}else {
				
				// dREHER exporto correctamente, informar a AFIP
				enviarAFIP();
				
			}
		}
	}
	
	/**
	 * Metodo para enviar a AFIP
	 * 
	 *  La url de produccion es:
	 * 	https://cot.arba.gov.ar/TransporteBienes/SeguridadCliente/presentarRemitos.do
	 * 
	 * 	La url para cargar remitos en el ambiente de testing es:
		http://cot.test.arba.gov.ar/TransporteBienes/pages/remitos/PresentarRemitos.jsp
	 * 
		enviando un formulario multipart por método POST con los siguientes atributos:
		 user
		 password
		 file (archivo de texto con los remitos)
	 * 
Respuesta de la transacción:
-----------------------------------------------------------		
Respuesta de transacción exitosa
	<TBCOMPROBANTE>
		<cuitEmpresa>N11</cuitEmpresa>
		<numeroComprobante>N9</numeroComprobante>
		<nombreArchivo>A41</nombreArchivo>
		<codigoIntegridad>A50</codigoIntegridad>
		<validacionesRemitos class="list">
			<remito>
				<numeroUnico>A16</numeroUnico>
				<procesado>A2</procesado> (SI)
				<cot>N</cot>
			</remito>
			<remito>
				<numeroUnico>A16</numeroUnico>
				<procesado>A2</procesado> (NO)
				<errores class="list">
					<error>
						<codigo>N2</codigo>
						<descripcion>A150</descripcion>
					</error>
				</errores>
			</remito>
 		</validacionesRemitos>
	</TBCOMPROBANTE>


Respuesta de transacción fallida
----------------------------------------------------------------
	<TBError>
 		<tipoError>A20</tipoError> (DATO o ERROR INESPERADO)
 		<codigoError>N2</codigoError>
 		<mensajeError>A150</mensajeError>
	</TBError>
	
	 * 
	 * dREHER
	 */
	private void enviarAFIP() {
		
		// URL del servidor Produccion
        String url = "https://cot.arba.gov.ar/TransporteBienes/SeguridadCliente/presentarRemitos.do";
        
        // URL ambiente testing
        url = "http://cot.test.arba.gov.ar/TransporteBienes/pages/remitos/PresentarRemitos.jsp";

        // Datos del formulario
        String user = "tu_usuario";
        String password = "tu_contraseña";
        File file = new File(filePath);

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // Construir el formulario multipart
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("user", user);
        builder.addTextBody("password", password);
        builder.addBinaryBody("file", file, ContentType.TEXT_PLAIN, file.getName());

        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        try {
            // Enviar la solicitud
            HttpResponse response = httpClient.execute(httpPost);

            // Procesar la respuesta
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            System.out.println("Código de estado: " + statusCode);
            System.out.println("Respuesta del servidor: " + responseBody);
            
            // Termino ok el envio, parsear respuesta...
            if(statusCode == 200) {
            	// Recibio un error
            	if(responseBody.contains("<TBError>")) {
            		
            		parsearRespuestaKO(responseBody);
			
            	}else {
		
            		parsearRespuestaOK(responseBody);
            		
            	}
            }

            // Manejar la respuesta según sea necesario
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Parsear respuesta AFIP SIN ERROR
	 * @param responseBody
	 * dREHER
	 */
	private void parsearRespuestaOK(String responseBody) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder builder = factory.newDocumentBuilder();
        	ByteArrayInputStream input = new ByteArrayInputStream(responseBody.getBytes("UTF-8"));
        	Document document = builder.parse(input);

			// Obtener el elemento raíz
			Element root = document.getDocumentElement();

			// Obtener los elementos hijos de TBCOMPROBANTE
			NodeList comprobanteList = root.getElementsByTagName("TBCOMPROBANTE");
			for (int i = 0; i < comprobanteList.getLength(); i++) {
				Element comprobanteElement = (Element) comprobanteList.item(i);

				// Leer los valores de los elementos dentro de TBCOMPROBANTE
				String cuitEmpresa = comprobanteElement.getElementsByTagName("cuitEmpresa").item(0).getTextContent();
				String numeroComprobante = comprobanteElement.getElementsByTagName("numeroComprobante").item(0).getTextContent();
				String nombreArchivo = comprobanteElement.getElementsByTagName("nombreArchivo").item(0).getTextContent();
				String codigoIntegridad = comprobanteElement.getElementsByTagName("codigoIntegridad").item(0).getTextContent();

				// Mostrar los valores
				System.out.println("cuitEmpresa: " + cuitEmpresa);
				System.out.println("numeroComprobante: " + numeroComprobante);
				System.out.println("nombreArchivo: " + nombreArchivo);
				System.out.println("codigoIntegridad: " + codigoIntegridad);

				// Obtener los elementos de validacionesRemitos
				NodeList validacionesRemitosList = comprobanteElement.getElementsByTagName("validacionesRemitos");
				for (int j = 0; j < validacionesRemitosList.getLength(); j++) {
					Element validacionesRemitosElement = (Element) validacionesRemitosList.item(j);

					// Obtener los elementos remito dentro de validacionesRemitos
					NodeList remitoList = validacionesRemitosElement.getElementsByTagName("remito");
					for (int k = 0; k < remitoList.getLength(); k++) {
						Element remitoElement = (Element) remitoList.item(k);

						// Leer los valores de los elementos dentro de remito
						String numeroUnico = remitoElement.getElementsByTagName("numeroUnico").item(0).getTextContent();
						String procesado = remitoElement.getElementsByTagName("procesado").item(0).getTextContent();
						String cot = remitoElement.getElementsByTagName("cot").item(0).getTextContent();

						// Mostrar los valores
						System.out.println("numeroUnico: " + numeroUnico);
						System.out.println("procesado: " + procesado);
						System.out.println("cot: " + cot);

						// Verificar si hay errores dentro de remito
						NodeList erroresList = remitoElement.getElementsByTagName("errores");
						if (erroresList.getLength() > 0) {
							Element erroresElement = (Element) erroresList.item(0);

							// Obtener el elemento error dentro de errores
							Element errorElement = (Element) erroresElement.getElementsByTagName("error").item(0);

							// Leer los valores de los elementos dentro de error
							String codigoError = errorElement.getElementsByTagName("codigo").item(0).getTextContent();
							String descripcionError = errorElement.getElementsByTagName("descripcion").item(0).getTextContent();

							// Mostrar los valores de error
							System.out.println("codigoError: " + codigoError);
							System.out.println("descripcionError: " + descripcionError);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parsear respuesta AFIP con ERROR
	 * @param responseBody
	 * dREHER
	 */
	private void parsearRespuestaKO(String responseBody) {

        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder builder = factory.newDocumentBuilder();
        	ByteArrayInputStream input = new ByteArrayInputStream(responseBody.getBytes("UTF-8"));
        	Document document = builder.parse(input);

        	// Obtener el elemento raíz
        	Element rootElement = document.getDocumentElement();

            // Obtener los elementos hijo
            NodeList childNodes = rootElement.getChildNodes();

            String tipoError = "";
            String codigoError = "";
            String mensajeError = "";

            // Recorrer los elementos hijo
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String nodeName = element.getNodeName();
                    String nodeValue = element.getTextContent();

                    switch (nodeName) {
                        case "tipoError":
                            tipoError = nodeValue;
                            break;
                        case "codigoError":
                            codigoError = nodeValue;
                            break;
                        case "mensajeError":
                            mensajeError = nodeValue;
                            break;
                        default:
                            // Manejar otros elementos si es necesario
                            break;
                    }
                }
            }

            // Imprimir los datos extraídos
            System.out.println("Tipo de Error: " + tipoError);
            System.out.println("Código de Error: " + codigoError);
            System.out.println("Mensaje de Error: " + mensajeError);

        } catch (Exception e) {
            e.printStackTrace();
        }
	}


	@Override
	protected String getMsg() {
		MJacoferRoadMap rm = new MJacoferRoadMap(localCtx, HojaDeRuta, localTrxName);
		return "Hoja de Ruta: " + rm.getDocumentNo() + " - Remitos Exportados: " + lineasExportadas;
	}
	
	@Override
	protected String getFilePath() {
		MExternalService es = null;
		MClient client = new MClient(localCtx,Env.getAD_Client_ID(localCtx),localTrxName);
		SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyyMMdd");
		MClientInfo info = client.getInfo();
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		String date = ymdFormat.format(today);
		
		StringBuffer filepath = new StringBuffer(getExportFormat().getFileName());
		
		PreparedStatement ps = null;
		ResultSet rs = null;		
		try {
			ps = DB.prepareStatement("SELECT C_ExternalService_ID FROM C_ExternalService WHERE value = 'COT'");
			rs = ps.executeQuery();
			if(rs.next()) {				
				es = new MExternalService(localCtx,rs.getInt("C_ExternalService_ID"),localTrxName);
			}			
		} catch (Exception e) {
			log.log(Level.SEVERE, "No existe un Servicio Externo con clave COT", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		
		// Filepath para Planexware
		filepath.append("TB_");
		filepath.append(es.getAttributeByName("ORIGEN").getName().trim() + "_");
		filepath.append(es.getAttributeByName("DESTINO").getName().trim() + "_");
		filepath.append(es.getAttributeByName("TIPO_DOCUMENTO").getName().trim() + "_");
		filepath.append(info.getCUIT().replace("-","") + "_");
		filepath.append(es.getAttributeByName("NRO_PLANTA").getName().trim());
		filepath.append(es.getAttributeByName("NRO_PUERTA").getName().trim() + "_");	
		filepath.append(date + "_");
		String str_seq;
		// Lógica de secuenciación, la cual se resetea a 000001 cada nuevo día.
		if(date.equals(es.getAttributeByName("FECHA_SECUENCIA").getName())) {
			X_C_ExternalServiceAttributes nro_seq = es.getAttributeByName("NRO_SECUENCIA");
			int seq = Integer.parseInt(nro_seq.getName());
			seq++;
			nro_seq.setName(String.valueOf(seq));
			nro_seq.save();
			str_seq = fillWithZerosAtStart(String.valueOf(seq),6);
		} else {
			X_C_ExternalServiceAttributes fecha_seq = es.getAttributeByName("FECHA_SECUENCIA");
			X_C_ExternalServiceAttributes nro_seq = es.getAttributeByName("NRO_SECUENCIA");
			fecha_seq.setName(date);
			fecha_seq.save();
			nro_seq.setName("1");
			nro_seq.save();
			str_seq = "000001";
		}		
		filepath.append(str_seq + "_");
		filepath.append(es.getAttributeByName("VIAJE").getName().trim() + "_");
		filepath.append(str_seq + "_");
		filepath.append(es.getAttributeByName("DEPENDENCIA").getName().trim() + "_");
		filepath.append(es.getAttributeByName("REFERENCIA").getName().trim());
		filepath.append(".txt");
		
		return filepath.toString();
	}
	
	protected String getFileHeader() {
		MClient client = new MClient(localCtx,Env.getAD_Client_ID(localCtx),localTrxName);
		MClientInfo info = client.getInfo();
		
		StringBuffer header = new StringBuffer();
		header.append("01|");
		header.append(info.getCUIT().replace("-",""));
		header.append("\n");
		
		return header.toString();
	}

	protected String getFileBody() {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat hmFormat = new SimpleDateFormat("hhmm");
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DISTINCT ");
		sql.append("	TO_CHAR(io.movementdate,'yyyyMMdd') AS FECHA_EMISION, ");
		sql.append("	getExternalServiceAttribute('COT','SUJETO_GENERADOR') AS SUJETO_GENERADOR, ");
		sql.append("	CASE WHEN cat_iva.i_tipo_iva = 'CF' THEN 1 ELSE 0 END AS DESTINATARIO_CONSUMIDOR_FINAL, ");
		sql.append("	CASE WHEN (cat_iva.i_tipo_iva = 'CF' AND io.jacofer_capacity > 5000)  ");
		sql.append("		THEN getExternalServiceAttribute('COT','DESTINATARIO_TIPO_DOCUMENTO') ELSE '' END AS DESTINATARIO_TIPO_DOCUMENTO, ");
		sql.append("	CASE WHEN (cat_iva.i_tipo_iva = 'CF' AND io.jacofer_capacity > 5000) ");
		sql.append("		THEN (CASE WHEN getExternalServiceAttribute('COT','SUJETO_GENERADOR') = 'D'	THEN REPLACE(cli.cuit,'-','') ");
		sql.append("		ELSE bp.taxid	END) ELSE ''END AS DESTINATARIO_DOCUMENTO, ");
		sql.append("	CASE WHEN NOT(cat_iva.i_tipo_iva = 'CF') THEN bp.taxid ELSE '' END AS DESTINATARIO_CUIT, ");
		sql.append("	bp.name::CHARACTER(50) AS DESTINATARIO_RAZON_SOCIAL, ");	
		sql.append("	CASE WHEN cat_iva.i_tipo_iva = 'CF' THEN 0::NUMERIC ELSE getExternalServiceAttribute('COT','DESTINATARIO_TENEDOR')::NUMERIC END AS DESTINATARIO_TENEDOR, ");
		sql.append("	l.address1::CHARACTER(40) AS DESTINO_DOMICILIO_CALLE, ");
		sql.append("	l.postal::CHARACTER(8) AS DESTINO_DOMICILIO_CODIGOPOSTAL, ");
		sql.append("	l.city::CHARACTER(50) AS DESTINO_DOMICILIO_LOCALIDAD, ");
		sql.append("	CASE WHEN r.jacofer_codigoarba IS NULL THEN '0' ELSE r.jacofer_codigoarba END AS DESTINO_DOMICILIO_PROVINCIA, ");
		sql.append("	r.name AS destino_region, ");
		sql.append("	getExternalServiceAttribute('COT','ENTREGA_DOMICILIO_ORIGEN') AS ENTREGA_DOMICILIO_ORIGEN, ");
		sql.append("	REPLACE(cli.cuit,'-','') AS ORIGEN_CUIT, ");
		sql.append("	cl.name::CHARACTER(50) AS ORIGEN_RAZON_SOCIAL, ");
		sql.append("	getExternalServiceAttribute('COT','EMISOR_TENEDOR') AS EMISOR_TENEDOR, ");
		sql.append("	cll.address1::CHARACTER(40) AS ORIGEN_DOMICILIO_CALLE, ");
		sql.append("	cll.postal::CHARACTER(8) AS ORIGEN_DOMICILIO_CODIGOPOSTAL, ");
		sql.append("	cll.city::CHARACTER(50) AS ORIGEN_DOMICILIO_LOCALIDAD, ");
		sql.append("	CASE WHEN clr.jacofer_codigoarba IS NULL THEN '0' ELSE clr.jacofer_codigoarba END AS ORIGEN_DOMICILIO_PROVINCIA, ");
		sql.append("	clr.name AS origen_region, ");
		sql.append("	shbp.taxid AS TRANSPORTISTA_CUIT, ");
		sql.append("	getExternalServiceAttribute('COT','PRODUCTO_NO_TERM_DEV') AS PRODUCTO_NO_TERM_DEV, ");
		sql.append("	REPLACE(TO_CHAR(io.jacofer_capacity,'999999999999D00'),',','') AS IMPORTE, ");
		sql.append("	rml.m_inout_id, ");
		sql.append("	io.documentno ");
		sql.append("FROM m_jacofer_roadmapline rml ");
		sql.append("INNER JOIN ad_client cl ON rml.ad_client_id = cl.ad_client_id ");
		sql.append("INNER JOIN ad_clientinfo cli ON cl.ad_client_id = cli.ad_client_id ");
		sql.append("INNER JOIN c_location cll ON cli.c_location_id = cll.c_location_id ");
		sql.append("LEFT JOIN c_region clr ON cll.c_region_id = clr.c_region_id ");
		sql.append("INNER JOIN m_jacofer_roadmap rm ON rml.m_jacofer_roadmap_id = rm.m_jacofer_roadmap_id ");
		sql.append("INNER JOIN m_inout io ON rml.m_inout_id = io.m_inout_id ");
		sql.append("INNER JOIN c_bpartner bp ON io.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("LEFT JOIN c_bpartner_location bpl ON io.c_bpartner_location_id = bpl.c_bpartner_location_id ");
		sql.append("LEFT JOIN c_location l ON l.c_location_id = bpl.c_location_id ");
		sql.append("LEFT JOIN c_region r ON l.c_region_id = r.c_region_id ");
		sql.append("LEFT JOIN c_categoria_iva cat_iva ON bp.c_categoria_iva_id = cat_iva.c_categoria_iva_id ");
		sql.append("LEFT JOIN m_shipper sh ON bp.m_shipper_id = sh.m_shipper_id ");
		sql.append("LEFT JOIN c_bpartner shbp ON sh.c_bpartner_id = shbp.c_bpartner_id ");
		sql.append("WHERE rml.isactive = 'Y' ");
		sql.append("	AND io.docstatus IN ('CO', 'CL') ");
		sql.append("	AND bp.deliveryviarule IN ('D', 'S') ");
		sql.append("AND rm.m_jacofer_roadmap_id = " + HojaDeRuta);
		sql.append(";");

		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer body = new StringBuffer();
		try {
			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				lineasExportadas++;
				int M_InOut_ID = rs.getInt("M_InOut_ID");		
				body.append("02|"); // TIPO_REGISTRO
				body.append(rs.getString("FECHA_EMISION").trim() + "|");
				body.append(getCodigoUnico(M_InOut_ID).trim() + "|"); //CODIGO_UNICO
				body.append(ymdFormat.format(today).trim() + "|"); // FECHA_SALIDA_TRANSPORTE
				body.append(hmFormat.format(today).trim() + "|"); // HORA_SALIDA_TRANSPORTE
				body.append(rs.getString("SUJETO_GENERADOR").trim() + "|");
				body.append(rs.getString("DESTINATARIO_CONSUMIDOR_FINAL").trim() + "|");
				body.append(rs.getString("DESTINATARIO_TIPO_DOCUMENTO").trim() + "|");
				body.append(rs.getString("DESTINATARIO_DOCUMENTO").trim() + "|");
				body.append(rs.getString("DESTINATARIO_CUIT").trim() + "|");
				body.append(rs.getString("DESTINATARIO_RAZON_SOCIAL").trim() + "|");
				body.append(rs.getString("DESTINATARIO_TENEDOR").trim() + "|");
				String dom = rs.getString("DESTINO_DOMICILIO_CALLE").trim();
				String nroDom = getNroDomicilio(dom);
				body.append(!nroDom.equals("") ? dom.replaceAll(nroDom, "").trim() + "|" : dom + "|"); // ORIGEN_DOMICILIO_CALLE
				body.append(nroDom + "|"); // DESTINO_DOMICILIO_NUMERO
				body.append(nroDom.equals("") ? "S/N|" : "|"); // DESTINO_DOMICILIO_COMPLE
				body.append("|"); // DESTINO_DOMICILIO_PISO
				body.append("|"); // DESTINO_DOMICILIO_DTO
				body.append("|"); // DESTINO_DOMICILIO_BARRIO
				body.append(rs.getString("DESTINO_DOMICILIO_CODIGOPOSTAL").trim() + "|");
				body.append(rs.getString("DESTINO_DOMICILIO_LOCALIDAD").trim() + "|");
				// Check para el campo 'Código Arba' de la C_Region
				if(!rs.getString("DESTINO_DOMICILIO_PROVINCIA").equals("0")) 
					body.append(rs.getString("DESTINO_DOMICILIO_PROVINCIA").trim() + "|");
				else 
					errorLog.append("Región: " + rs.getString("destino_region").trim() + " - Código Arba vacío\n");
				body.append("|"); // PROPIO_DESTINO_DOMICILIO_CODIGO
				body.append(rs.getString("ENTREGA_DOMICILIO_ORIGEN").trim() + "|");
				body.append(rs.getString("ORIGEN_CUIT").trim() + "|");
				body.append(rs.getString("ORIGEN_RAZON_SOCIAL").trim() + "|");
				body.append(rs.getString("EMISOR_TENEDOR").trim() + "|");
				dom = rs.getString("ORIGEN_DOMICILIO_CALLE").trim();
				nroDom = getNroDomicilio(dom);
				body.append(!nroDom.equals("") ? dom.replaceAll(nroDom, "").trim() + "|" : dom + "|"); // ORIGEN_DOMICILIO_CALLE
				body.append(nroDom + "|"); // ORIGEN_DOMICILIO_NUMERO
				body.append(nroDom.equals("") ? "S/N|" : "|"); // ORIGEN_DOMICILIO_COMPLE
				body.append("|"); // ORIGEN_DOMICILIO_PISO
				body.append("|"); // ORIGEN_DOMICILIO_DTO
				body.append("|"); // ORIGEN_DOMICILIO_BARRIO
				body.append(rs.getString("ORIGEN_DOMICILIO_CODIGOPOSTAL").trim() + "|");
				body.append(rs.getString("ORIGEN_DOMICILIO_LOCALIDAD").trim() + "|");
				// Check para el campo 'Código Arba' de la C_Region
				if(!rs.getString("ORIGEN_DOMICILIO_PROVINCIA").equals("0")) 
					body.append(rs.getString("ORIGEN_DOMICILIO_PROVINCIA").trim() + "|");
				else 
					errorLog.append("Región: " + rs.getString("origen_region").trim() + " - Código Arba vacío\n");
				body.append(rs.getString("TRANSPORTISTA_CUIT").trim() + "|");
				body.append("|"); // TIPO_RECORRIDO
				body.append("|"); // RECORRIDO_LOCALIDAD
				body.append("|"); // RECORRIDO_CALLE
				body.append("|"); // RECORRIDO_RUTA
				body.append("|"); // PATENTE_VEHICULO
				body.append("|"); // PATENTE_ACOPLADO
				body.append(rs.getString("PRODUCTO_NO_TERM_DEV").trim() + "|");
				// Check para que el importe sea > 0 
				float importe = Float.parseFloat(rs.getString("IMPORTE").trim());
				if(importe > 0 || (importe == 0 && rs.getString("ORIGEN_CUIT").trim().equals(rs.getString("DESTINATARIO_CUIT").trim())) )
					body.append(rs.getString("IMPORTE").trim() + "\n");
				else
					errorLog.append("Remito: "+ rs.getString("documentno").trim() + " - El importe no puede ser 0");
				body.append(getProducts(M_InOut_ID));
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getFileFooter", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return body.toString();
	}
	
	// Metodo que obtiene el numero de domicilio de un string (Asumiendo que es efectivamente un nro)
	private String getNroDomicilio(String dom) {
		String nro = "";
		String[] split = dom.split(" ");
		char chStart = split[split.length - 1].charAt(0); // Primer char del ultimo sub str del domicilio
		char chEnd = split[split.length - 1].charAt(split[split.length - 1].length() - 1); // Ultimo char del ultimo sub str del domicilio
		
		if((chStart >= '0' && chStart <= '9') && (chEnd >= '0' && chEnd <= '9')) { // Si arranca y termina con nro => Asumo que es un nro
			nro = split[split.length - 1];
		}
		
		return nro;
	}

	protected String getProducts(int M_InOut_ID) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	CASE WHEN p.jacofer_codigoarba IS NULL THEN '0' ELSE p.jacofer_codigoarba END AS CODIGO_UNICO_PRODUCTO, ");
		sql.append("	CASE WHEN uom.jacofer_codigoarba IS NULL THEN '0' ELSE uom.jacofer_codigoarba END AS ARBA_CODIGO_UNIDAD_MEDIDA, ");
		sql.append("	REPLACE(TO_CHAR(iol.qtyentered,'9999999999999D00'),',','') AS CANTIDAD, ");
		sql.append("	p.value::CHARACTER(25) AS PROPIO_CODIGO_PRODUCTO, ");
		sql.append("	p.name::CHARACTER(40) AS PROPIO_DESCRIPCION_PRODUCTO, ");
		sql.append("	uom.name::CHARACTER(20)  AS PROPIO_DESCRIPCION_UNIDAD_MEDIDA, ");
		sql.append("	REPLACE(TO_CHAR(iol.qtyentered,'9999999999999D00'),',','') AS CANTIDAD_AJUSTADA, ");
		sql.append("	io.documentno ");
		sql.append("FROM m_inoutline iol ");
		sql.append("INNER JOIN m_inout io ON io.m_inout_id = iol.m_inout_id  ");
		sql.append("INNER JOIN m_product p ON iol.m_product_id = p.m_product_id  ");
		sql.append("INNER JOIN c_uom uom ON iol.c_uom_id = uom.c_uom_id  ");
		sql.append("WHERE iol.m_inout_id = " + M_InOut_ID);
		sql.append(";");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer products = new StringBuffer();
		try {
			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				products.append("03|"); // TIPO_REGISTRO
				// Check para el campo 'Código Arba' del M_Product
				if(!rs.getString("CODIGO_UNICO_PRODUCTO").equals("0")) 
					products.append(rs.getString("CODIGO_UNICO_PRODUCTO").trim() + "|");
				else 
					errorLog.append("Producto: " + rs.getString("PROPIO_CODIGO_PRODUCTO").trim() + " - Código Arba vacío\n");
				// Check para el campo 'Código Arba' de la C_UOM
				if(!rs.getString("ARBA_CODIGO_UNIDAD_MEDIDA").equals("0")) 
					products.append(rs.getString("ARBA_CODIGO_UNIDAD_MEDIDA").trim() + "|");
				else 
					errorLog.append("UOM: " + rs.getString("PROPIO_DESCRIPCION_UNIDAD_MEDIDA").trim() + " - Código Arba vacío\n");
				// Check para que la cantidad sea > 0
				if(Integer.parseInt(rs.getString("CANTIDAD").trim() ) > 0)
					products.append(rs.getString("CANTIDAD").trim() + "|");
				else
					errorLog.append("Remito: "  + rs.getString("documentno") + " Producto: " + rs.getString("PROPIO_CODIGO_PRODUCTO").trim() + " - La cantidad no puede ser 0");
				products.append(rs.getString("PROPIO_CODIGO_PRODUCTO").trim() + "|");
				products.append(rs.getString("PROPIO_DESCRIPCION_PRODUCTO").trim() + "|");
				products.append(rs.getString("PROPIO_DESCRIPCION_UNIDAD_MEDIDA").trim() + "|");
				products.append(rs.getString("CANTIDAD_AJUSTADA").trim() + "\n");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getFileFooter", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return products.toString();
	}
	
	protected String getFileFooter() {
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT ");
		sql.append("	COUNT(*) AS CANTIDAD_TOTAL_REMITOS ");
		sql.append("FROM m_jacofer_roadmapline rml ");
		sql.append("INNER JOIN m_jacofer_roadmap rm ON rml.m_jacofer_roadmap_id = rm.m_jacofer_roadmap_id ");
		sql.append("INNER JOIN m_inout io ON rml.m_inout_id = io.m_inout_id ");
		sql.append("INNER JOIN c_bpartner bp ON io.c_bpartner_id = bp.c_bpartner_id  ");
		sql.append("INNER JOIN m_shipper sh ON bp.m_shipper_id = sh.m_shipper_id ");
		sql.append("WHERE rml.isactive = 'Y' ");
		sql.append("	AND io.docstatus IN ('CO', 'CL') ");
		sql.append("	AND bp.deliveryviarule IN ('D', 'S') ");
		sql.append("	AND rm.m_jacofer_roadmap_id = " + HojaDeRuta);
		sql.append(";");

		
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer footer = new StringBuffer();
		try {
			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				footer.append("04|");
				// Check para que el documento tenga al menos 1 remito
				if(rs.getInt("CANTIDAD_TOTAL_REMITOS") > 0)
					footer.append(rs.getInt("CANTIDAD_TOTAL_REMITOS"));
				else
					this.errorLog.append("La hoja de ruta debe tener por lo menos 1 remito asociado.");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getFileFooter", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return footer.toString();
	}
	
	protected String getCodigoUnico(int M_InOut_ID) throws Exception  {
		MInOut io = new MInOut(localCtx,M_InOut_ID,localTrxName);
		MDocType dt = new MDocType(localCtx, io.getC_DocType_ID(), localTrxName);
			
		if (dt.getdocsubtypecae() == null || dt.getdocsubtypecae().isEmpty())
			throw new Exception("El tipo de documento no tiene configurado código AFIP/ARBA");
		
		StringBuffer code = new StringBuffer();
		code.append(fillWithZerosAtStart(dt.getdocsubtypecae(),3));
		code.append(fillWithZerosAtStart(io.getDocumentNo(),13));
		
		return code.toString();
	}
	
	// Función para agregar 0s deltante de un string hasta llegar a un largo especificado.
	protected String fillWithZerosAtStart(String str, int len) {
		while(str.length() < len) {
			str = "0" + str;
		}
		return str;
	}
}
