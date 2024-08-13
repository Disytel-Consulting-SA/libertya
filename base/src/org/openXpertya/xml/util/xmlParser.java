package org.openXpertya.xml.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.openXpertya.print.fiscal.hasar.pojos.Cierre;
import org.openXpertya.print.fiscal.hasar.pojos.Cliente;
import org.openXpertya.print.fiscal.hasar.pojos.Documento;
import org.openXpertya.print.fiscal.hasar.pojos.Jornada;
import org.openXpertya.print.fiscal.hasar.pojos.Totales;
import org.openXpertya.util.CLogger;

/*
 * Utilidades para parsear XML
 * 
 * https://json2csharp.com/code-converters/xml-to-java
 * Sitio para convertir XML to pojos java
 * 
 * dREHER
 */


public class xmlParser {
    
	String filePath = "";
	String desde = "";
	String hasta = "";
	
	StringBuilder sbOut = new StringBuilder();
	ArrayList<String> notShow = new ArrayList<String>();

	private static CLogger log = CLogger.getCLogger(xmlParser.class);
	
	public String getDesde() {
		return desde;
	}

	public void setDesde(String desde) {
		this.desde = desde;
	}

	public String getHasta() {
		return hasta;
	}

	public void setHasta(String hasta) {
		this.hasta = hasta;
	}

	public xmlParser(String filePath) {
		this.filePath = filePath;
	}
	
	public ArrayList<String> getNotShow() {
		return notShow;
	}

	public void setNotShow(ArrayList<String> notShow) {
		this.notShow = notShow;
	}
	
	public String parsear() {
		String outFileName = "";
		
		if(filePath.isEmpty()) {
			log.warning("No se indico archivo a parsear!");
			return "";
		}
		
        try {
        	
        	File file = new File(filePath);
        	if(!file.exists())
        		return "No se encuentra el archivo: " + filePath;

        	Double[] totalesj = new Double[]{0.00,0.00,0.00,0.00,0.00,0.00,0.00};
        	Double[] totales = new Double[]{0.00,0.00,0.00,0.00,0.00,0.00,0.00};
        	
        	// Crear el constructor SAXBuilder
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(file);

            // Obtener el elemento raíz
            Element rootElement = document.getRootElement();

            // Obtener los elementos <Jornada>
            List<Element> jornadaElements = rootElement.getChildren("Jornada");
            System.out.println("Cantidad de jornadas: " + jornadaElements.size());
            
            // Recorrer los elementos <Jornada>
            for (Element jornadaElement : jornadaElements) {

                // Creo el objeto jornada y leo la fecha de la misma
                Jornada jornada = new Jornada();
                String jfecha = jornadaElement.getChildText("Fecha");
                String numero = jornadaElement.getChildText("Numero");
                
                jornada.setFecha(getFecha(jfecha));
                jornada.setNumero(Integer.valueOf(numero));
            	
                System.out.println("Jornada: " + jfecha + " # " + numero);

                // Obtener el elemento <Documentos> dentro de la Jornada
                Element documentosElement = jornadaElement.getChild("Documentos");
                
                List<Element> documentoElements = null;
                
                // Verificar si existen elementos <Documento> dentro de <Documentos>
                if (documentosElement != null) 
                    documentoElements = documentosElement.getChildren("Documento");
                
                System.out.println("Cantidad de documentos: " + documentoElements.size());    
                    
                // Recorrer los elementos <Documento>
                for (Element documento : documentoElements) {
                	
                    if(documento!=null) {
                
                    	String orden = documento.getChildText("Orden");
                    	if(orden==null || orden.isEmpty())
        					orden = "0";
                    	
                    	System.out.println("Documento: " + orden);
                    	Documento doc = new Documento();
                    	doc.setOrden(Integer.valueOf(orden));
                    	
                    	// Obtiene el elemento <Items> dentro de cada <Documento>
                    	Element itemElement = documento.getChild("Items");

                    	// Verifica si el elemento <Items> existe
                    	if (itemElement != null) {

                    		// Accede a los elementos dentro de <Items> y realiza las operaciones necesarias

                    			Cliente cliente = null;

                    			// Obtener el elemento <Cliente>
                    			Element clienteElement = itemElement.getChild("Cliente");
                    			if(clienteElement!=null) {
                    				// Crear un objeto Cliente
                    				cliente = new Cliente();

                    				// Obtener los valores de los elementos en <Cliente>
                    				String nroDocumento = clienteElement.getChildText("NroDocumento");
                    				String nombre = clienteElement.getChildText("Nombre");
                    				String direccion = clienteElement.getChildText("Direccion");
                    				String tipo = clienteElement.getChildText("TipoCliente");

                    				// Establecer los valores en el objeto Cliente
                    				cliente.setNroDocumento(nroDocumento);
                    				cliente.setNombre(nombre);
                    				cliente.setDireccion(direccion);
                    				cliente.setTipoCliente(tipo);

                    				// Realizar operaciones adicionales con el objeto Cliente
                    				// ...
                    			}

                    			// Obtener el elemento <Cierre>

                    			// Crear un objeto Cierre
                				Cierre cierre = new Cierre();

                    			Element cierreElement = itemElement.getChild("Cierre");
                    			if(cierreElement!=null) {
                    				
                    				// Obtener el valor del elemento <Orden> en <Cierre>
                    				String ordenc = cierreElement.getChildText("Orden");
                    				String subTipoDocumento = cierreElement.getChildText("SubTipoDocumento"); 
                    				String calificadorDocumento = cierreElement.getChildText("CalificadorDocumento");
                    				String numeroCompleto = cierreElement.getChildText("NumeroCompleto");

                    				// Establecer el valor en el objeto Cierre
                    				if(ordenc==null || ordenc.isEmpty())
                    					ordenc = "0";
                    				cierre.setOrden(Integer.valueOf(ordenc));
                    				cierre.setCalificadorDocumento(calificadorDocumento);
                    				cierre.setSubTipoDocumento(subTipoDocumento);
                    				cierre.setNumeroCompleto(numeroCompleto);

                    			}
                    			
                    				// Obtener el elemento <Cierre>
                    				Element totalesElement = itemElement.getChild("Totales");
                    				// Crear un objeto Cliente
                					Totales total = new Totales();
                					
                    				if(totalesElement!=null) {

                    					String base = totalesElement.getChildText("Base");
                    					String montoNoGravado = totalesElement.getChildText("MontoNoGravado");
                    					String montoGravado = totalesElement.getChildText("MontoGravado");
                    					String montoExento = totalesElement.getChildText("MontoExento");
                    					String montoIva = totalesElement.getChildText("MontoIVA");
                    					String montoOtrosTrib = totalesElement.getChildText("MontoOtrosTributos");
                    					String totalTk = totalesElement.getChildText("Final");

                    					total.setBase(String2D(base));
                    					total.setMontoNoGravado(String2D(montoNoGravado));
                    					total.setMontoGravado(String2D(montoGravado));
                    					total.setMontoExento(String2D(montoExento));
                    					total.setMontoIVA(String2D(montoIva));
                    					total.setMontoOtrosTributos(String2D(montoOtrosTrib));
                    					total.setTotal(String2D(totalTk));

                    				}

                    				// Realizar operaciones adicionales con el objeto Cierre
                    				// ...
                    				// Agregar el objeto Cliente y Cierre al objeto Documento o realizar otras acciones necesarias
                    				// ...

                    				if(cierre!= null && cierre.SubTipoDocumento!= null && !cierre.SubTipoDocumento.equals("ZFiscal")) {

                    					sbOut.append("\r\n" + "Comprobante. Orden: " + orden);
                    					sbOut.append("\r\n" + "Fecha: " + jornada.Fecha + " - " + cierre.SubTipoDocumento 
                    							+ " " + cierre.CalificadorDocumento
                    							+ " " + cierre.NumeroCompleto
                    							);
                    					sbOut.append("\r\n" + "Cliente:");

                    					if(cliente!=null)
                    						sbOut.append("\r\n" + cliente.Nombre 
                    								+ " Identificador: " + cliente.NroDocumento
                    								+ " Tipo: " + cliente.TipoCliente 
                    								);
                    					sbOut.append("\r\n" + getSeparator(true));
                    					sbOut.append("\r\n" + "Totales:");
                    					sbOut.append("\r\n" + "Neto-------- NoGravado--- Gravado----- Exento------ IVA--------- Tributos---- Total-------");
                    					sbOut.append("\r\n" + N2RG(total.Base,12) + " " +
                    							N2RG(total.MontoNoGravado,12) + " " +
                    							N2RG(total.MontoGravado,12) + " " + 
                    							N2RG(total.MontoExento,12) + " " +
                    							N2RG(total.MontoIVA,12) + " " +
                    							N2RG(total.MontoOtrosTributos,12) + " " +
                    							N2RG(total.Total,12) );
                    					sbOut.append("\r\n" + getSeparator(false) + "\r\n");

                    					totales[0] = totales[0] + total.Base;
                    					totales[1] = totales[1] + total.MontoNoGravado;
                    					totales[2] = totales[2] + total.MontoGravado;
                    					totales[3] = totales[3] + total.MontoExento;
                    					totales[4] = totales[4] + total.MontoIVA;
                    					totales[5] = totales[5] + total.MontoOtrosTributos;
                    					totales[6] = totales[6] + total.Total;
                    					
                    					totalesj[0] = totalesj[0] + total.Base;
                    					totalesj[1] = totalesj[1] + total.MontoNoGravado;
                    					totalesj[2] = totalesj[2] + total.MontoGravado;
                    					totalesj[3] = totalesj[3] + total.MontoExento;
                    					totalesj[4] = totalesj[4] + total.MontoIVA;
                    					totalesj[5] = totalesj[5] + total.MontoOtrosTributos;
                    					totalesj[6] = totalesj[6] + total.Total;

                    				}

                    			// Realizar operaciones adicionales con el objeto Documento
                    			// ...


                    	} // lopp over Items list

                    } // if sobre cada documento NO null

                } // loop over Documento list
                             
                if(jornadaElements.size() > 1) {
                	sbOut.append("\r\n" + getSeparator(true));
                	sbOut.append("\r\n" + "TOTAL JORNADA: " + jornada.getFecha() + " # " + jornada.getNumero());
                	sbOut.append("\r\n" + getSeparator(true));
                	sbOut.append("\r\n" + "Neto      :" + N2RG(totalesj[0], 15));
                	sbOut.append("\r\n" + "No Gravado:" + N2RG(totalesj[1], 15));
                	sbOut.append("\r\n" + "Gravado   :" + N2RG(totalesj[2], 15));
                	sbOut.append("\r\n" + "Exento    :" + N2RG(totalesj[3], 15));
                	sbOut.append("\r\n" + "IVA       :" + N2RG(totalesj[4], 15));
                	sbOut.append("\r\n" + "Otros Trib:" + N2RG(totalesj[5], 15));
                	sbOut.append("\r\n" + "TOTAL     :" + N2RG(totalesj[6], 15));
                	sbOut.append("\r\n\r\n");
                	totalesj = new Double[]{0.00,0.00,0.00,0.00,0.00,0.00,0.00};
                }
                
            } // loop over Jornada list
            
            sbOut.append("\r\n\r\n");
        	sbOut.append("\r\n" + getSeparator(true));
        	sbOut.append("\r\n" + "TOTAL GENERAL: (" + getDesde() + " - " + getHasta() + ")");
        	sbOut.append("\r\n" + getSeparator(true));
        	sbOut.append("\r\n" + "Neto      :" + N2RG(totales[0], 15));
        	sbOut.append("\r\n" + "No Gravado:" + N2RG(totales[1], 15));
        	sbOut.append("\r\n" + "Gravado   :" + N2RG(totales[2], 15));
        	sbOut.append("\r\n" + "Exento    :" + N2RG(totales[3], 15));
        	sbOut.append("\r\n" + "IVA       :" + N2RG(totales[4], 15));
        	sbOut.append("\r\n" + "Otros Trib:" + N2RG(totales[5], 15));
        	sbOut.append("\r\n" + "TOTAL     :" + N2RG(totales[6], 15));
        	sbOut.append("\r\n" + getSeparator(true) + "\r\n");
        	
            outFileName = writeToFile();
            
        } catch (Exception e) {
            System.out.println("Ocurrio un error al leer contenido fiscal. Error:" + e.toString());
        }
        
        return outFileName;
    }

	private Double String2D(String numberString) {
		double number = 0.00;
		if(numberString==null || numberString.isEmpty())
			return number;
		
		try {
            number = Double.parseDouble(numberString);
           // System.out.println("El número convertido es: " + number);
        } catch (NumberFormatException e) {
            System.out.println("Error al convertir el número.");
        }
		return number;
	}

	private Date getFecha(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		java.sql.Date fechaConvertida=null;

		if(date==null || date.isEmpty())
			return null;
		
		try {
			Date parsed =  dateFormat.parse(date);
			fechaConvertida = new java.sql.Date(parsed.getTime());
		} catch(Exception e) {
			System.out.println("Error occurred"+ e.getMessage());
		}
		return fechaConvertida;
	}

	private String N2RG(double base, int len) {
		DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
		decimalFormat.setPositivePrefix(" ");
		decimalFormat.format(base);

		return String.format("%" + len + "s", decimalFormat.format(base));
	}

	private Object getSeparator(boolean simple) {
		if(simple)
			return "------------------------------------------------------------------------------------------";
		
		return "==========================================================================================";
	}

	private String writeToFile() {
		// Construir la ruta completa del archivo txt
		String outFilePath = filePath.replace(".xml", ".txt");

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(outFilePath));
			writer.write(sbOut.toString());
			writer.close();
			System.out.println("Archivo TXT generado exitosamente. " + outFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {

		}

		return outFilePath;
	}

}
