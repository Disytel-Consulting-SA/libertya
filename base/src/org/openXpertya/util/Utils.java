package org.openXpertya.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import org.openXpertya.model.MPreference;

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
	
		/**
		 * Devuelve los impuestos de una factura, agrupados por area de afectacion
		 * 
		 * @param C_Invoice_ID
		 * @return ArrayList de impuestos agrupados por area de afectacion
		 * @author dREHER
		 */
		public static ArrayList<String> getGroupInvoiceTaxes(int C_Invoice_ID, boolean isOnlyIVA){
			ArrayList<String> taxes = new ArrayList<String>();
			String sql = "select case \n"
					+ "		when t.TaxAreaType='P' then 'Provincial'\n"
					+ "		when t.TaxAreaType='N' then 'Nacional'\n"
					+ "		when t.TaxAreaType='I' then 'Interno'\n"
					+ "		when t.TaxAreaType='M' then 'Municipal'\n"
					+ "		else 'Sin Tipo Area'\n"
					+ "		end as TaxAreaType, coalesce(sum(ci.taxamt),0) as amount\n"
					+ "from c_invoicetax ci \n"
					+ "inner join c_tax t on t.c_tax_id=ci.c_tax_id\n"
					+ "inner join c_taxcategory ca on ca.c_taxcategory_id=t.c_taxcategory_id\n"
					+ "where ci.c_invoice_id=?\n";

			// dREHER 5.0 TODO: mejorar esta validacion
			if(isOnlyIVA) {
				sql += " and UPPER(ca.name) ILIKE '%IVA%'";
			}else {
				sql += " and UPPER(ca.name) NOT ILIKE '%IVA%'";
			}
					
			sql += " group by t.taxareatype \n" +
				   " order by t.TaxAreaType";
	        
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, C_Invoice_ID);
				rs = pstmt.executeQuery();
				while(rs.next()) {
					
					BigDecimal amount = rs.getBigDecimal("amount");
					if(amount.compareTo(Env.ZERO) > 0) {
						// Formatear el BigDecimal
						String numeroFormateado = BigDecimalToStringFMT(amount);
						taxes.add(rs.getString("TaxAreaType") + " $" + numeroFormateado);
					}
				}
			}catch (SQLException e) {
					System.out.println("Error al leer impuestos agrupados de facturas... " + e.toString());
			}finally {
				DB.close(rs, pstmt);
				rs=null; pstmt=null;
			}
			
			return taxes;
		}
		
		/**
		 * Devuelve total de impuestos de una factura
		 * 
		 * @param C_Invoice_ID
		 * @return Total afectado
		 * @author dREHER
		 */
		public static BigDecimal getTotalAmtInvoiceTaxes(int C_Invoice_ID, boolean isOnlyIVA){
			BigDecimal total = Env.ZERO;
			String sql = "select coalesce(sum(ci.taxamt),0) as amount\n"
					+ "from c_invoicetax ci \n"
					+ "inner join c_tax t on t.c_tax_id=ci.c_tax_id\n"
					+ "inner join c_taxcategory ca on ca.c_taxcategory_id=t.c_taxcategory_id\n"
					+ "where ci.c_invoice_id=?\n";

			// dREHER 5.0 TODO: mejorar esta validacion
			if(isOnlyIVA) {
				sql += " and UPPER(ca.name) ILIKE '%IVA%'";
			}else {
				sql += " and UPPER(ca.name) NOT ILIKE '%IVA%'";
			}
				
	        
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, C_Invoice_ID);
				rs = pstmt.executeQuery();
				while(rs.next()) {
					
					BigDecimal amount = rs.getBigDecimal("amount");
					if(amount.compareTo(Env.ZERO) > 0) {
						total = total.add(amount);
					}
				}
			}catch (SQLException e) {
					System.out.println("Error al leer impuestos agrupados de facturas... " + e.toString());
			}finally {
				DB.close(rs, pstmt);
				rs=null; pstmt=null;
			}
			
			return total;
		}
		
		/**
		 * Devuelve la cadena final de impuestos de una factura
		 * @param C_Invoice_ID
		 * @return String con la cadena final de impuestos
		 * @author dREHER
		 */
		public static String getStringInvoiceTaxes(int C_Invoice_ID, boolean isOnlyIVA) {
			String desc = "";
			
			if(isOnlyIVA)
				desc = "IVA Contenido $ ";
			else
				desc = "Otros Impuestos Nacionales Indirectos $ ";
			
			ArrayList<String> taxes = getGroupInvoiceTaxes(C_Invoice_ID, isOnlyIVA);
			int i = 0;
			for(String s : taxes) {
				desc += (i > 0?", ":"") + s;
				i++;
			}
			
			return desc;
		}
		
		/**
		 * Devuelve la cadena final de impuestos de una factura
		 * @param C_Invoice_ID
		 * @return String con la cadena final de impuestos
		 * @author dREHER
		 */
		public static String getStringTotalInvoiceTaxes(int C_Invoice_ID, boolean isOnlyIVA) {
			String desc = "";
			
			if(isOnlyIVA)
				desc = "IVA Contenido $ ";
			else
				desc = "Otros Impuestos Nacionales Indirectos $ ";
			
			BigDecimal total = getTotalAmtInvoiceTaxes(C_Invoice_ID, isOnlyIVA);
			
			// Formatear el BigDecimal
			String numeroFormateado = BigDecimalToStringFMT(total);
			
			return desc + numeroFormateado;
		}
		
		/**
		 * Devuelve si se debe mostrar o no la leyenda de impuestos en facturas B
		 * @return true/false
		 * @author dREHER
		 */
		public static boolean isMostrarImpuestosFCB() {
			boolean isMostrarTaxes = false;
			String tmp = MPreference.GetCustomPreferenceValue("MostrarImpuestosFC_B", Env.getAD_Client_ID(Env.getCtx()));
			if(tmp!=null && tmp.equals("Y"))
				isMostrarTaxes = true;
			
			return isMostrarTaxes;
		}
		
		/**
		 * Devuelve si se debe mostrar o no la leyenda de impuestos en facturas B
		 * @return cadena de prefijo
		 * @author dREHER
		 */
		public static String getPrefijoMostrarImpuestosFCB() {
			String prefijo = MPreference.GetCustomPreferenceValue("PrefijoMostrarImpuestosFC_B", Env.getAD_Client_ID(Env.getCtx()));
			if(prefijo==null) prefijo = "";
			
			return prefijo;
		}
		
		/**
		 * Devuelve si se debe mostrar o no la leyenda de impuestos en facturas B
		 * @return cadena de prefijo
		 * @author dREHER
		 */
		public static String getPrefijoMostrarImpuestosFCBEtiqueta() {
			String prefijo = MPreference.GetCustomPreferenceValue("PrefijoMostrarImpuestosFC_BLabel", Env.getAD_Client_ID(Env.getCtx()));
			if(prefijo==null) prefijo = "";
			
			return prefijo;
		}
		
		public static String BigDecimalToStringFMT(BigDecimal numero) {
			 // Definir el formato deseado con separador de miles y dos decimales
	        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
	        symbols.setDecimalSeparator(',');
	        symbols.setGroupingSeparator('.');

	        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
	        
	        return decimalFormat.format(numero);
		}
}
