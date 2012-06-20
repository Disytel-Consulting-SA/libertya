/*
 * @(#)Util.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.util;

import java.awt.Color;
import java.awt.font.TextAttribute;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class Util {
	
/** codigo por compatibilidad Adempiere Jasper Reports */
   
  
    
    // dREHER, por si llega un integer
    public static boolean isEmpty (Integer numb)
    {
    	return numb == null;
    }   //  isEmpty
    
   
/** codigo por compatibilidad Adempiere Jasper Reports **/

    /**
     * Descripción de Método
     *
     *
     * @param map
     */
    static public void dump(Map map) {

        System.out.println("Dump Map - size=" + map.size());

        Iterator	it	= map.keySet().iterator();

        while (it.hasNext()) {

            Object	key	= it.next();
            Object	value	= map.get(key);

            System.out.println(key + "=" + value);
        }

    }		// dump (Map)

    /**
     * Descripción de Método
     *
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static int findIndexOf(String str, char search) {
        return findIndexOf(str, search, search);
    }		// findIndexOf

    /**
     * Descripción de Método
     *
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static int findIndexOf(String str, String search) {

        if ((str == null) || (search == null) || (search.length() == 0)) {
            return -1;
        }

        //
        int	endIndex	= -1;
        int	parCount	= 0;
        boolean	ignoringText	= false;
        int	size		= str.length();

        while (++endIndex < size) {

            char	c	= str.charAt(endIndex);

            if (c == '\'') {
                ignoringText	= !ignoringText;
            } else if (!ignoringText) {

                if ((parCount == 0) && (c == search.charAt(0))) {

                    if (str.substring(endIndex).startsWith(search)) {
                        return endIndex;
                    }

                } else if (c == ')') {
                    parCount--;
                } else if (c == '(') {
                    parCount++;
                }
            }
        }

        return -1;

    }		// findIndexOf

    /**
     * Descripción de Método
     *
     *
     * @param str
     * @param search1
     * @param search2
     *
     * @return
     */
    public static int findIndexOf(String str, char search1, char search2) {

        if (str == null) {
            return -1;
        }

        //
        int	endIndex	= -1;
        int	parCount	= 0;
        boolean	ignoringText	= false;
        int	size		= str.length();

        while (++endIndex < size) {

            char	c	= str.charAt(endIndex);

            if (c == '\'') {
                ignoringText	= !ignoringText;
            } else if (!ignoringText) {

                if ((parCount == 0) && ((c == search1) || (c == search2))) {
                    return endIndex;
                } else if (c == ')') {
                    parCount--;
                } else if (c == '(') {
                    parCount++;
                }
            }
        }

        return -1;

    }		// findIndexOf

    /**
     * Descripción de Método
     *
     *
     * @param in
     *
     * @return
     */
    public static String initCap(String in) {

        if ((in == null) || (in.length() == 0)) {
            return in;
        }

        //
        boolean	capitalize	= true;
        char[]	data		= in.toCharArray();

        for (int i = 0; i < data.length; i++) {

            if ((data[i] == ' ') || Character.isWhitespace(data[i])) {
                capitalize	= true;
            } else if (capitalize) {

                data[i]		= Character.toUpperCase(data[i]);
                capitalize	= false;

            } else {
                data[i]	= Character.toLowerCase(data[i]);
            }
        }

        return new String(data);

    }		// initCap

    /**
     * Descripción de Método
     *
     *
     * @param str
     *
     * @return
     */
    public static boolean is8Bit(String str) {

        if ((str == null) || (str.length() == 0)) {
            return true;
        }

        char[]	cc	= str.toCharArray();

        for (int i = 0; i < cc.length; i++) {

            if (cc[i] > 255) {

                // System.out.println("Not 8 Bit - " + str);
                return false;
            }
        }

        return true;

    }		// is8Bit

    /**
     * Descripción de Método
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        AttributedString	aString	= new AttributedString("test test");

        aString.addAttribute(TextAttribute.FOREGROUND, Color.blue);
        aString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 2, 4);
        getIterator(aString, new AttributedCharacterIterator.Attribute[] { TextAttribute.UNDERLINE });

    }		// main

    /**
     * Descripción de Método
     *
     *
     * @param content
     *
     * @return
     */
    public static String maskHTML(String content) {

        if ((content == null) || (content.length() == 0) || content.equals(" ")) {
            return "&nbsp";
        }

        //
        String	temp	= replace(content, "<", "&lt;");

        temp	= replace(temp, ">", "&gt;");
        temp	= replace(temp, "\"", "&quot;");
        temp	= replace(temp, "&", "&amp;");

        return temp;

    }		// maskHTML

    /**
     * Descripción de Método
     *
     *
     * @param comp
     */
    public static void printActionInputMap(JComponent comp) {

        System.out.println("-----------------------");
        System.out.println("ActionMap for Component " + comp.toString());

        ActionMap	am	= comp.getActionMap();
        Object[]	amKeys	= am.allKeys();		// including Parents

        for (int i = 0; i < amKeys.length; i++) {

            Action		a	= am.get(amKeys[i]);
            StringBuffer	sb	= new StringBuffer("- ");

            sb.append(a.getValue(Action.NAME));

            if (a.getValue(Action.ACTION_COMMAND_KEY) != null) {
                sb.append(", Cmd=").append(a.getValue(Action.ACTION_COMMAND_KEY));
            }

            if (a.getValue(Action.ACCELERATOR_KEY) != null) {
                sb.append(", Acc=").append(a.getValue(Action.ACCELERATOR_KEY));
            }

            if (a.getValue(Action.MNEMONIC_KEY) != null) {
                sb.append(", Mem=").append(a.getValue(Action.MNEMONIC_KEY));
            }

            if (a.getValue(Action.SHORT_DESCRIPTION) != null) {
                sb.append(" - ").append(a.getValue(Action.SHORT_DESCRIPTION));
            }

            System.out.println(sb.toString() + " - " + a);
        }

        //
        System.out.println("----------------------");
        System.out.println("InputMap for Component " + comp.toString());

        InputMap	im		= comp.getInputMap();
        KeyStroke[]	kStrokes	= im.allKeys();

        if (kStrokes != null) {

            for (int i = 0; i < kStrokes.length; i++) {
                System.out.println("- " + kStrokes[i].toString() + " - " + im.get(kStrokes[i]).toString());
            }
        }

        //
        System.out.println("----------------------");
        System.out.println("InputMap for Component When Focused " + comp.toString());
        im		= comp.getInputMap(JComponent.WHEN_FOCUSED);
        kStrokes	= im.allKeys();

        if (kStrokes != null) {

            for (int i = 0; i < kStrokes.length; i++) {
                System.out.println("- " + kStrokes[i].toString() + " - " + im.get(kStrokes[i]).toString());
            }
        }

        System.out.println("----------------------");
        System.out.println("InputMap for Component When Focused in Window " + comp.toString());
        im		= comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        kStrokes	= im.allKeys();

        if (kStrokes != null) {

            for (int i = 0; i < kStrokes.length; i++) {
                System.out.println("- " + kStrokes[i].toString() + " - " + im.get(kStrokes[i]).toString());
            }
        }

        System.out.println("----------------------");
        System.out.println("InputMap for Component When Ancestor " + comp.toString());
        im		= comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        kStrokes	= im.allKeys();

        if (kStrokes != null) {

            for (int i = 0; i < kStrokes.length; i++) {
                System.out.println("- " + kStrokes[i].toString() + " - " + im.get(kStrokes[i]).toString());
            }
        }

        System.out.println("----------------------");

    }		// printActionInputMap

    /**
     * Descripción de Método
     *
     *
     * @param in
     *
     * @return
     */
    public static String removeCRLF(String in) {

        char[]		inArray	= in.toCharArray();
        StringBuffer	out	= new StringBuffer(inArray.length);

        for (int i = 0; i < inArray.length; i++) {

            char	c	= inArray[i];

            if ((c == '\n') || (c == '\r')) {
                ;
            } else {
                out.append(c);
            }
        }

        return out.toString();

    }		// removeCRLF

    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param oldPart
     * @param newPart
     *
     * @return
     */
    public static String replace(String value, String oldPart, String newPart) {

        if ((value == null) || (value.length() == 0) || (oldPart == null) || (oldPart.length() == 0)) {
            return value;
        }

        //
        int		oldPartLength	= oldPart.length();
        String		oldValue	= value;
        StringBuffer	retValue	= new StringBuffer();
        int		pos		= oldValue.indexOf(oldPart);

        while (pos != -1) {

            retValue.append(oldValue.substring(0, pos));

            if ((newPart != null) && (newPart.length() > 0)) {
                retValue.append(newPart);
            }

            oldValue	= oldValue.substring(pos + oldPartLength);
            pos		= oldValue.indexOf(oldPart);
        }

        retValue.append(oldValue);

        // log.fine( "Env.replace - " + value + " - Old=" + oldPart + ", New=" + newPart + ", Result=" + retValue.toString());
        return retValue.toString();

    }		// replace

    /**
     * Descripción de Método
     *
     *
     * @param b
     *
     * @return
     */
    static public String toHex(byte b) {

        char	hexDigit[]	= {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        char[]	array	= { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };

        return new String(array);
    }

    /**
     * Descripción de Método
     *
     *
     * @param c
     *
     * @return
     */
    static public String toHex(char c) {

        byte	hi	= (byte) (c >>> 8);
        byte	lo	= (byte) (c & 0xff);

        return toHex(hi) + toHex(lo);

    }		// toHex

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param string
     * @param countChar
     *
     * @return
     */
    public static int getCount(String string, char countChar) {

        if ((string == null) || (string.length() == 0)) {
            return 0;
        }

        int	counter	= 0;
        char[]	array	= string.toCharArray();

        for (int i = 0; i < array.length; i++) {

            if (array[i] == countChar) {
                counter++;
            }
        }

        return counter;

    }		// getCount

    /**
     * Descripción de Método
     *
     *
     * @param aString
     * @param relevantAttributes
     *
     * @return
     */
    static public AttributedCharacterIterator getIterator(AttributedString aString, AttributedCharacterIterator.Attribute[] relevantAttributes) {

        AttributedCharacterIterator	iter	= aString.getIterator();
        Set				set	= iter.getAllAttributeKeys();

        // System.out.println("AllAttributeKeys=" + set);
        if (set.size() == 0) {
            return iter;
        }

        // Check, if there are unwanted attributes
        Set	unwanted	= new HashSet(iter.getAllAttributeKeys());

        for (int i = 0; i < relevantAttributes.length; i++) {
            unwanted.remove(relevantAttributes[i]);
        }

        if (unwanted.size() == 0) {
            return iter;
        }

        // Create new String
        StringBuffer	sb	= new StringBuffer();

        for (char c = iter.first(); c != AttributedCharacterIterator.DONE; c = iter.next()) {
            sb.append(c);
        }

        aString	= new AttributedString(sb.toString());

        // copy relevant attributes
        Iterator	it	= iter.getAllAttributeKeys().iterator();

        while (it.hasNext()) {

            AttributedCharacterIterator.Attribute	att	= (AttributedCharacterIterator.Attribute) it.next();

            if (!unwanted.contains(att)) {

                for (char c = iter.first(); c != AttributedCharacterIterator.DONE; c = iter.next()) {

                    Object	value	= iter.getAttribute(att);

                    if (value != null) {

                        int	start	= iter.getRunStart(att);
                        int	limit	= iter.getRunLimit(att);

                        // System.out.println("Attribute=" + att + " Value=" + value + " Start=" + start + " Limit=" + limit);
                        aString.addAttribute(att, value, start, limit);
                        iter.setIndex(limit);
                    }
                }
            }

            // else
            // System.out.println("Unwanted: " + att);
        }

        return aString.getIterator();

    }		// getIterator

    /**
     * Descripción de Método
     *
     *
     * @param str
     *
     * @return
     */
    public static boolean isEmpty(String str) {
        return isEmpty(str, false);
    }		// isEmpty
    
    /** * Is String Empty 
     * @param str string 
     * @param trimWhitespaces trim whitespaces 
     * @return true if >= 1 char */ 
    public static boolean isEmpty (String str, boolean trimWhitespaces) { 
    	if (str == null) return true; 
    	if (trimWhitespaces) 
    		return str.trim().length() == 0; 
    	else 
    		return str.length() == 0; 
    } // isEmpty


	/**
	 * Verifica si el parámetro entero es null y si el flag withZeroCheck es
	 * verdadero, entonces también verifico si es igual a 0 en caso que no sea
	 * null el parámetro.
	 * 
	 * @param intValue
	 *            valor entero a verificar
	 * @param withZeroCheck
	 *            si es true verifico también que sea 0, false no verifico
	 * @return true si intValue es igual a null y si el flag withZeroCheck está
	 *         seteado en true también verifico que sea 0
	 */
    public static boolean isEmpty(Integer intValue, boolean withZeroCheck) {
    	return (intValue == null) || (withZeroCheck?intValue.intValue() == 0:false) ;
    }		// isEmpty
    
    
    public static String getHTMLListElement(String pdata){
    	return "<li>"+pdata+"</li>";
    }
    
    public static String removeInitialAND(String str){
    	// Si el índice del AND es al principio, entonces lo saco
    	int andIndex = str.indexOf("AND");
		if(andIndex > 0 && andIndex < 2){
			str = str.substring(andIndex + 2);
		}
		return str;
    }
    
    
	public static String replaceCharAt(String s, int pos, char c) 
	{
		StringBuffer buf = new StringBuffer( s );
		buf.setCharAt( pos, c );
		return buf.toString( );
	}
	
}	// Util



/*
 * @(#)Util.java   02.jul 2007
 * 
 *  Fin del fichero Util.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
