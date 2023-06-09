/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */

package org.openXpertya.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class StringUtil {

    /**
     * Descripción de Método
     *
     *
     * @param s
     * @param length
     *
     * @return
     */

    public static String trim( String s,int length ) {
    	if(s == null){
    		return null;
    	}
        if( s.trim().length() >= length ) {
            return s.trim().substring( 0,length - 1 );
        } else {
            return s.trim();
        }
    }
    
    public static String trimStrict( String s,int length ) {
    	if(s == null){
    		return null;
    	}
        if( s.trim().length() > length ) {
            return s.trim().substring( 0,length );
        } else {
            return s.trim();
        }
    }
    
    /**
     * @param str
     * @return string parámetro con la primer letra en mayúscula
     */
    public static String fuc(String str){
    	String result = str;
    	if(!Util.isEmpty(str, true)){
        	char firstChar = str.charAt(0);
        	if(Character.isLetter(firstChar)){
        		String firstLetterStr = String.valueOf(firstChar);
				result = result.replaceFirst(firstLetterStr, firstLetterStr.toUpperCase());
        	}
    	}
    	return result;
    }
    
    /**
	 * Equivalente a implode("(",",",")"); de utilidad tiíica para generar clausulas
	 * sql UNION a partir de una lista de strings. Se agrega comilla simple a cada elemento.
	 * 
	 * @param list no puede ser null, no puede tener componentes null
	 * @return las String vacia si list es vacia
	 */
	public static String implodeForUnion(Collection<String> list) {
		return implode(list,"(",",",")");
	}
    
	/**
	 * Equivalente a implode("(",",",")"); de utilidad tiíica para generar clausulas
	 * sql UNION a partir de una lista de enteros.
	 * 
	 * @param list no puede ser null, no puede tener componentes null
	 * @return las String vacia si list es vacia, si no, una string de la forma "(int1,int2...,intN)"
	 */
	public static String implodeForUnion(List<Integer> list)
	{
		return implode(list,"(",",",")");
	}
	
    /**
     * Javier Adder:
     * Dada un a lista de Integer (se asume ninguno null), retorna la "implosión"
     * de la forma begin + int1 + int2 + ... intn + end; por ej, con (1,2,3), begin= "(",
     * end = ")", separador "-", retorna "(1-2-3)". Si la lista no tiene elemento se 
     * retorna la string vacia.
     * @param list lista de enteros, no puede ser null, ni tener elemenos null
     * @param begin string incial, no puede ser null
     * @param separator separador entre enteros, no puede ser null
     * @param end string final, no puede ser null
     * @return la implosión de la lista
     */
    public static String implode(List<Integer> list,String begin, String separator, String end)
    {
    	if (list.size()<= 0)
    		return "";
    	StringBuffer bs = new StringBuffer();
    	bs.append(begin);
    	for (int i = 0; i < list.size(); i++)
    	{
    		if ( i == 0)
    			bs.append(list.get(i).toString());
    		else
    			bs.append(separator).append(list.get(i).toString());
    	}
    	bs.append(end);
    	return bs.toString();
    }
    
    public static String implode(Collection<String> list,String begin, String separator, String end) {
    	if (list.size()<= 0)
    		return "";
    	StringBuffer bs = new StringBuffer();
    	bs.append(begin);
    	Iterator<String> it = list.iterator();
    	String element;
    	for (int i = 0; it.hasNext(); i++){
    		element = "'"+it.next()+"'";
    		if ( i > 0){
    			bs.append(separator);
    		}
    		bs.append(element);
    	}
    	bs.append(end);
    	return bs.toString();
    }
    
    public static String pad(String str, String filler, Integer length, boolean left){
    	if(str.length() >= length){
    		return str;
    	}
    	StringBuffer auxFiller = new StringBuffer();
    	for (int i = length - str.length(); i > 0 ; i--) {
			auxFiller.append(filler);
		}
    	return left?auxFiller.toString()+str:str+auxFiller.toString();
    }
    
    public static String valueOrDefault(Object value, String defaultValue){
    	return (value != null)?value.toString():defaultValue;
    }
    
    /**
	 * Splittea una línea basado en el caracter de split y devuelve tantas líneas
	 * como maxLengthPerLine lo indique. Es decir, ajusta texto a un máximo de
	 * tamaño.
	 * 
	 * @param baseString       línea base
	 * @param splitter         caracter de spliteado
	 * @param maxLengthPerLine tamaño máximo de línea
	 * @return lista de líneas con el ajuste de texto correspondiente
	 */
    public static List<String> splitLines(String baseString, String splitter, int maxLengthPerLine) {
    	String auxLine = "";
    	String[] baseSplitted = baseString.split(splitter);
    	List<String> splittedLines = new ArrayList<String>();
    	for (String bs : baseSplitted) {
			if(maxLengthPerLine < (auxLine.length() + bs.length() + 1)) {
				splittedLines.add(auxLine);
				auxLine = "";
			}
			auxLine += bs+" ";
		}
    	if(auxLine.length() > 0) {
    		splittedLines.add(auxLine);
    	}
    	return splittedLines;
    }
}



/*
 *  @(#)StringUtil.java   25.03.06
 * 
 *  Fin del fichero StringUtil.java
 *  
 *  Versión 2.2
 *
 */
