package org.openXpertya.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarHelper {

	
	/**
	 * Lee el contenido de un archivo de texto ubicado dentro de un jar
	 * @param jarURL ubicación en el file system
	 * @param resource recurso a leer dentro del jar
	 * @param crFormat concatena un salto de linea, un <br>, o lo que sea necesario
	 * @param commentFormat formato del comentario (si una linea inicia con dicho formato, no es incorporada). 
	 * @return un String con el contenido del texto leido
	 * @throws Exception
	 */
	public static String readFromJar(String jarURL, String resource, String crFormat, String commentFormat) throws Exception 
	{
	       JarFile jarFile = new JarFile(jarURL);
	       JarEntry entry = jarFile.getJarEntry(resource);
	       
	       /* Si no existe el archivo, retornar null */
	       if (entry == null)
	    	   return null;
	       
	       InputStream input = jarFile.getInputStream(entry);
	       StringBuilder content = new StringBuilder();
	       InputStreamReader isr = new InputStreamReader(input);
	       BufferedReader reader = new BufferedReader(isr);

	       String line;
	       while ((line = reader.readLine()) != null)
	    	   // Si la linea contiene informacion, y no es un comentario, incorporarla
	    	   if ( line.length() > 0 )
	    	   {
	    		   line = removeComment(line, commentFormat);
	    		   if (line.length() > 0 && !"".equals(line))
	    			   content.append(line + crFormat);
	    	   }
	       jarFile.close();
	       
	       return content.toString();
	}
	
	
	/**
	 * Lee el contenido de un properties ubicado dentro del jar
	 * @param jarURL ubicación en el file system
	 * @param resource recurso ubicacion del properties
	 * @return un String con el contenido del texto leido
	 * @throws Exception
	 */
	public static Properties readPropertiesFromJar(String jarURL, String resource) throws Exception 
	{
	       JarFile jarFile = new JarFile(jarURL);
	       JarEntry entry = jarFile.getJarEntry(resource);
	       
	       /* Si no existe el archivo, retornar null */
	       if (entry == null)
	    	   return null;
	       
	       InputStream input = jarFile.getInputStream(entry);
	       Properties props = new java.util.Properties();
	       props.load(input);
	       
	       return props;
	}
	
	
	/**
	 * Lee el contenido de un archivo binario ubicado dentro de un jar
	 * @param jarURL jarURL ubicación en el file system
	 * @param resource resource recurso a leer dentro del jar
	 * @return el array de bytes correspondiente
	 * @throws Exception en caso de error
	 */
	public static byte[] readBinaryFromJar(String jarURL, String resource) throws Exception
	{
	       JarFile jarFile = new JarFile(jarURL);
	       JarEntry entry = jarFile.getJarEntry(resource);
	       
	       /* Si no existe el archivo, elevar la excepción */
	       if (entry == null)
	    	   throw new Exception ("Recurso " + resource + " no encontrado en archivo " + jarURL);
	       
	       /* Obtener el contenido del archivo */
	       InputStream input = jarFile.getInputStream(entry);
	       ByteArrayOutputStream output	= new ByteArrayOutputStream();
	       byte[]			buffer	= new byte[1024 * 8];		// 8kB
           int				length	= -1;
           
           /* Escribir a la salida para luego convertir a array de bytes */
           while ((length = input.read(buffer)) != -1) {
               output.write(buffer, 0, length);
           }
	       
	       /* Retornar el bytearray */
	       return output.toByteArray();
	}
	
	
	
	/**
	 * Remueve los comentarios de una linea 
	 * @param line linea a la cual deben removerse los comentarios
	 * @param commentFormat caracteres especiales que indican comentario
	 * @return
	 */
	private static String removeComment(String line, String commentFormat)
	{
		// Si no hay formato de comentario especificado, retornar directamente la linea original
		if (commentFormat == null)
			return line;

		// Existe un comentario en alguna posicion?
		int commentPos = line.indexOf(commentFormat);
		
		// De existir, retornar solo hasta esa posicion
		if (commentPos > -1)
			return line.substring(0, commentPos);
		
		// Caso general sin comentario, retornar la linea original
		return line;
	}
	
	
	/**
	 * Lee el archivo preinstall.sql y separa las sentencias basado en los comentarios de tipo: --YYYYMMDD-HHMM
	 * De esta manera es más sencillo realizar el seguimiento de los queries que generan problema
	 */
	public static ArrayList<String> readPreinstallSQLSentencesFromJar(String jarURL, String resource) throws Exception
	{
		final String sqlComment = "--";
		ArrayList<String> retValue = new ArrayList<String>();
		
	    JarFile jarFile = new JarFile(jarURL);
	    JarEntry entry = jarFile.getJarEntry(resource);
	       
	    /* Si no existe el archivo, retornar la lista vacía */
	    if (entry == null)
	    	return retValue;
	       
	    InputStream input = jarFile.getInputStream(entry);
	    StringBuilder content = new StringBuilder();
	    InputStreamReader isr = new InputStreamReader(input);
	    BufferedReader reader = new BufferedReader(isr);

	    String line;
	    // iterar por todas las lineas
	    while ((line = reader.readLine()) != null) {
	    	// Si la linea contiene informacion, y no es un comentario, incorporarla
	    	if ( line.length() > 0 ) {
	    		// Es una linea de separación de sentencias?
	    		if (isPreinstallSplitComment(line)) {
	    			// guardar la sentencia previa a la actual
	    			if (content.toString().length() > 0)
	    				retValue.add(content.toString());
	    			// reiniciar para la nueva sentencia
	    			content = new StringBuilder();
	    			continue;
	    		}
	    		// Si es una linea de comentario tradicional, simplemente omitirla
	    		line = removeComment(line, sqlComment);
	    		if (line.length() > 0 && !"".equals(line))
	    			content.append(line + " ");
	    	}
	    }
		// guardar la sentencia final
		if (content.toString().length() > 0)
			retValue.add(content.toString());

		// cierre del archivo
		jarFile.close();
	       
		return retValue;
	}
	
	/**
	 * Retorna true si el comentario respeta el formato:    --YYYYMMDD-HHMM (o similar teniendo 
	 * en cuenta eventuales espacios adicionales) de separación de sentencias; o false caso contrario
	 */
	protected static boolean isPreinstallSplitComment(String line) {
		
		// formato y tamaño a buscar
		final String dateTimeFormat = "yyyyMMdd-HHmm"; 
		final int dateTimeLength = dateTimeFormat.length();
	
		try {
			
			// si directamente no inicia como un comentario, omitir
			if (!line.trim().startsWith("--"))
				return false;
			
			// remover -- y todos los espacios, dejando la fecha/hora toda junta.
			line = line.substring(2).replaceAll(" ", "");
			
			// remover texto posterior a la fecha-hora
			line = line.substring(0, dateTimeLength);
			
			// Intentar parsear el comentario
			new SimpleDateFormat(dateTimeFormat).parse(line);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
