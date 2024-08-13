package org.openXpertya.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utilidades varias
 * 
 * @author dREHER
 *
 */
public class Utils {
	
		public static byte[] decodeASCII85(String ascii85String) {
			 // Remover los caracteres de inicio y fin del bloque ASCII85
	        ascii85String = ascii85String.replaceAll("<~", "").replaceAll("~>", "");
	        
	        // Decodificar la cadena ASCII85 a bytes
	        byte[] decodedBytes = decodeAscii85(ascii85String);
	        
	        return decodedBytes;
		}
		
		private static byte[] decodeAscii85(String ascii85String) {
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        char[] chars = ascii85String.toCharArray();
	        int value = 0;
	        int count = 0;
	        for (char c : chars) {
	            if (c == 'z') {
	                out.write(0);
	                count++;
	            } else if (c >= '!' && c <= 'u') {
	                value = value * 85 + (c - '!'); // Ascii85 to decimal conversion
	                count++;
	                if (count == 5) {
	                    out.write((byte) (value >> 24));
	                    out.write((byte) (value >> 16));
	                    out.write((byte) (value >> 8));
	                    out.write((byte) value);
	                    value = 0;
	                    count = 0;
	                }
	            }
	        }
	        int remaining = count - 1;
	        if (remaining >= 0) {
	            value += (int) Math.pow(85, remaining) * 33; // Ascii85 to decimal conversion
	            for (int i = 0; i < remaining; i++) {
	                out.write((byte) (value >> (24 - i * 8)));
	            }
	        }
	        return out.toByteArray();
	    }

		/**
		 * 
		 * Genera un archivo de texto tabulado con la informacion del fiscal
		 * 
		 * @param res TXT file
		 * dREHER
		 */
		public static String convertToFile(StringBuilder res, String nameFile) {
			
	        // Obtener la carpeta "home" del usuario
	        String userHome = System.getProperty("user.home");

	        // Construir la ruta completa del archivo zip
	        String zipFilePath = userHome + File.separator + nameFile;

	        BufferedWriter writer = null;
	        try {
	            writer = new BufferedWriter(new FileWriter(zipFilePath));
	            writer.write(res.toString());
	            writer.close();
	            System.out.println("Archivo generado exitosamente. " + zipFilePath);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally {

	        }
	        
	        return zipFilePath;
			
		}
		
		/**
		 * 
		 * Genera un archivo de texto tabulado con la informacion del fiscal
		 * 
		 * @param res XML
		 * dREHER
		 */
		public static String convertToFile(byte[] res, String nameFile) {
			
	        // Obtener la carpeta "home" del usuario
	        String userHome = System.getProperty("user.home");

	        // Construir la ruta completa del archivo zip
	        String zipFilePath = userHome + File.separator + nameFile;

            OutputStream outputStream = null;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(zipFilePath));
                outputStream.write(res);
            } catch (IOException e) {
                // Manejo de la excepción
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        // Manejo de la excepción al cerrar el flujo
                    }
                }
            }
	            
            System.out.println("Archivo ZIP generado exitosamente. " + zipFilePath);

            return zipFilePath;
			
		}
		
		/**
		 * 
		 * Genera un archivo de texto tabulado con la informacion del fiscal
		 * 
		 * @param res XML
		 * dREHER
		 */
		public static String convertToFile(StringBuilder res, String desde, String hasta, String impresora) {
			
	        // Obtener la carpeta "home" del usuario
	        String userHome = System.getProperty("user.home");

	        // Construir la ruta completa del archivo XML
	        String xmlFilePath = userHome + File.separator + "Auditoria_" +
	        								impresora.replace(" ", "_") + "_" +
	        								desde + "_" + hasta  + 
	        								".xml";

	        BufferedWriter writer = null;
	        try {
	            writer = new BufferedWriter(new FileWriter(xmlFilePath));
	            writer.write(res.toString());
	            writer.close();
	            System.out.println("Archivo XML generado exitosamente. " + xmlFilePath);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally {

	        }
	        
	        return xmlFilePath;
			
		}
	
}
