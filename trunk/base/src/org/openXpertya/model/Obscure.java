/*
 * @(#)Obscure.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.model;

/**
 *      Obscure Strings (e.g. Credit Card Numbers).
 *      Obscure Type defined in AD_Field
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: Obscure.java,v 1.4 2005/03/11 20:28:38 jjanke Exp $
 */
public class Obscure extends Object {

    /** Obscure Digits but last 4 = 904 (default) */
    public static final String	OBSCURETYPE_ObscureDigitsButLast4	= "904";

    /** Obscure Digits but first/last 4 = 944 */
    public static final String	OBSCURETYPE_ObscureDigitsButFirstLast4	= "944";

    /** Obscure AlphaNumeric but last 4 = A04 */
    public static final String	OBSCURETYPE_ObscureAlphaNumericButLast4	= "A04";

    /** Obscure AlphaNumeric but first/last 4 = A44 */
    public static final String	OBSCURETYPE_ObscureAlphaNumericButFirstLast4	= "A44";

    /** Obscure Type */
    private String	m_type	= OBSCURETYPE_ObscureDigitsButLast4;

    /** Clear Value */
    private String	m_clearValue;

    /** Obscrure Value */
    private String	m_obscuredValue;

    /**
     *      Obscure
     */
    public Obscure() {}		// Obscure

    /**
     *      Obscure.
     *      Obscure Digits but last 4
     *      @param clearValue clear value
     */
    public Obscure(String clearValue) {
        setClearValue(clearValue);
    }		// Obscure

    /**
     *      Obscure
     *      @param clearValue clear value
     *      @param obscureType Obscure Type
     */
    public Obscure(String clearValue, String obscureType) {

        setClearValue(clearValue);
        setType(obscureType);

    }		// Obscure

    /**
     *      test
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(Obscure.obscure("1a2b3c4d5e6f7g8h9"));
    }		// main

    /**
     *      Obscure clear value.
     *      Obscure Digits but last 4
     *      @param clearValue clear value
     *      @return obscured value
     */
    public static String obscure(String clearValue) {

        Obscure	ob	= new Obscure(clearValue);

        return ob.getObscuredValue();

    }		// obscure

    /**
     *      Obscure clear value
     *      @param clearValue clear value
     *      @param obscureType Obscure Type
     *      @return obscured value
     */
    public static String obscure(String clearValue, String obscureType) {

        Obscure	ob	= new Obscure(clearValue, obscureType);

        return ob.getObscuredValue();

    }		// obscure

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Clear Value
     *      @return Returns the clear Value.
     */
    public String getClearValue() {
        return m_clearValue;
    }		// getClearValue

    /**
     *      Get Obscured Value
     *      @return Returns the obscuredValue.
     */
    public String getObscuredValue() {

        if (m_obscuredValue != null) {
            return m_obscuredValue;
        }

        if ((m_clearValue == null) || (m_clearValue.length() == 0)) {
            return m_clearValue;
        }

        //
        boolean	alpha		= m_type.charAt(0) == 'A';
        int	clearStart	= Integer.parseInt(m_type.substring(1, 2));
        int	clearEnd	= Integer.parseInt(m_type.substring(2));

        //
        char[]		chars	= m_clearValue.toCharArray();
        int		length	= chars.length;
        StringBuffer	sb	= new StringBuffer(length);

        for (int i = 0; i < length; i++) {

            char	c	= chars[i];

            if (i < clearStart) {
                sb.append(c);
            } else if (i >= length - clearEnd) {
                sb.append(c);
            } else {

                if (!alpha &&!Character.isDigit(c)) {
                    sb.append(c);
                } else {
                    sb.append('*');
                }
            }
        }

        m_obscuredValue	= sb.toString();

        return m_obscuredValue;

    }		// getObscuredValue

    /**
     *      Get Obscured Value
     *      @param clearValue The clearValue to set.
     *      @return Returns the obscuredValue.
     */
    public String getObscuredValue(String clearValue) {

        setClearValue(clearValue);

        return getObscuredValue();

    }		// getObscuredValue

    /**
     *      Get Obscure Type
     *      @return type
     */
    public String getType() {
        return m_type;
    }		// getType

    //~--- set methods --------------------------------------------------------

    /**
     *      Set Clear Value
     *      @param clearValue The clearValue to set.
     */
    public void setClearValue(String clearValue) {

        m_clearValue	= clearValue;
        m_obscuredValue	= null;

    }		// setClearValue

    /**
     *      Set Type
     *      @param obscureType Obscure Type
     */
    public void setType(String obscureType) {

        if ((obscureType == null) || obscureType.equals("904") || obscureType.equals("944") || obscureType.equals("A44") || obscureType.equals("A04")) {

            m_type		= obscureType;
            m_obscuredValue	= null;

            return;
        }

        throw new IllegalArgumentException("ObscureType Invalid value - Reference_ID=291 - 904 - 944 - A44 - A04");

    }		// setType
}	// Obscrure



/*
 * @(#)Obscure.java   02.jul 2007
 * 
 *  Fin del fichero Obscure.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
