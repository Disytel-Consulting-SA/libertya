/*
 * @(#)Language.java   12.oct 2007  Versión 2.2
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

import java.awt.ComponentOrientation;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;

import javax.print.attribute.standard.MediaSize;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class Language implements Serializable {

    /** Descripción de Campos */
    public static final String	AD_Language_en_US	= "en_US";

    /** Descripción de Campos */
    private static final String	AD_Language_en_GB	= "en_GB";

    /** Descripción de Campos */
    private static final String	AD_Language_en_AU	= "en_AU";

    /** Descripción de Campos */
    private static final String	AD_Language_ca_ES	= "ca_ES";

    /** Descripción de Campos */
    private static final String	AD_Language_hr_HR	= "hr_HR";

    /** Descripción de Campos */
    private static final String	AD_Language_de_DE	= "de_DE";

    /** Descripción de Campos */
    private static final String	AD_Language_it_IT	= "it_IT";

    /** Descripción de Campos */
    private static final String	AD_Language_fr_FR	= "fr_FR";

    /** Descripción de Campos */
    private static final String	AD_Language_es_ES	= "es_ES";

    /** Descripción de Campos */
    private static final String	AD_Language_bg_BG	= "bg_BG";

    /** Descripción de Campos */
    private static final String	AD_Language_th_TH	= "th_TH";

    /** Descripción de Campos */
    private static final String	AD_Language_pl_PL	= "pl_PL";

    /** Descripción de Campos */
    private static final String	AD_Language_zh_TW	= "zh_TW";

    /** Descripción de Campos */
    private static final String	AD_Language_zh_CN	= "zh_CN";

    /** Descripción de Campos */
    private static final String	AD_Language_vi_VN	= "vi_VN";

    /** Descripción de Campos */
    private static final String	AD_Language_sv_SE	= "sv_SE";

    /** Descripción de Campos */
    private static final String	AD_Language_sl_SI	= "sl_SI";

    /** Descripción de Campos */
    private static final String	AD_Language_ru_RU	= "ru_RU";

    /** Descripción de Campos */
    private static final String	AD_Language_ro_RO	= "ro_RO";

    /** Descripción de Campos */
    private static final String	AD_Language_pt_BR	= "pt_BR";

    /** Descripción de Campos */
    private static final String	AD_Language_no_NO	= "no_NO";

    /** Descripción de Campos */
    private static final String	AD_Language_nl_NL	= "nl_NL";

    /** Descripción de Campos */
    private static final String	AD_Language_ml_ML	= "ml_ML";

    /** Descripción de Campos */
    private static final String	AD_Language_ja_JP	= "ja_JP";

    /** Descripción de Campos */
    private static final String	AD_Language_fi_FI	= "fi_FI";

    /** Descripción de Campos */
    private static final String	AD_Language_fa_IR	= "fa_IR";

    /** Descripción de Campos */
    private static final String	AD_Language_dk_DK	= "dk_DK";

    /** Descripción de Campos */
    private static final String	AD_Language_es_AR	= "es_AR";

    /** Descripción de Campos */
    private static final String	AD_Language_es_MX	= "es_MX"; 
    
    /** Descripción de Campos */
    private static final String AD_Language_es_PY	= "es_PY";
    
    /** Descripción de Campos */
    static private Language[]	s_languages	= {

        new Language("English", AD_Language_en_US, Locale.US, null, null, MediaSize.NA.LETTER),		// Base Language

        // ordered by locale
        // Not predefined Locales - need to define decimal Point and date pattern (not sure about time)
        new Language("\u0411\u044A\u043B\u0433\u0430\u0440\u0441\u043A\u0438 (BG)", AD_Language_bg_BG, new Locale("bg", "BG"), new Boolean(false), "dd/MM/yyyy", MediaSize.ISO.A4), new Language("Catal\u00e0", AD_Language_ca_ES, new Locale("ca", "ES"), null, "dd/MM/yyyy", MediaSize.ISO.A4), new Language("Deutsch", AD_Language_de_DE, Locale.GERMANY, null, null, MediaSize.ISO.A4), new Language("Dansk", AD_Language_dk_DK, new Locale("dk", "DK"), new Boolean(false), "dd-MM-yyyy", MediaSize.ISO.A4), new Language("English (AU)", AD_Language_en_AU, new Locale("en", "AU"), null, "dd/MM/yyyy", MediaSize.ISO.A4), new Language("English (UK)", AD_Language_en_GB, Locale.UK, null, null, MediaSize.ISO.A4), new Language("Espa\u00f1ol", AD_Language_es_ES, new Locale("es", "ES"), new Boolean(false), "dd/MM/yyyy", MediaSize.ISO.A4), new Language("Farsi", AD_Language_fa_IR, new Locale("fa", "IR"), new Boolean(false), "dd-MM-yyyy", MediaSize.ISO.A4), new Language("Finnish", AD_Language_fi_FI, new Locale("fi", "FI"), new Boolean(true), "dd.MM.yyyy", MediaSize.ISO.A4),
        new Language("Fran\u00e7ais", AD_Language_fr_FR, Locale.FRANCE, null, null,	// dd.MM.yy
                     MediaSize.ISO.A4), new Language("Hrvatski", AD_Language_hr_HR, new Locale("hr", "HR"), null, "dd.MM.yyyy", MediaSize.ISO.A4),
        new Language("Italiano", AD_Language_it_IT, Locale.ITALY, null, null,		// dd.MM.yy
                         MediaSize.ISO.A4), new Language("\u65e5\u672c\u8a9e (JP)", AD_Language_ja_JP, Locale.JAPAN, null, null, MediaSize.ISO.A4), new Language("Malay", AD_Language_ml_ML, new Locale("ml", "ML"), new Boolean(false), "dd-MM-yyyy", MediaSize.ISO.A4), new Language("Nederlands", AD_Language_nl_NL, new Locale("nl", "NL"), new Boolean(false), "dd-MM-yyyy", MediaSize.ISO.A4), new Language("Norsk", AD_Language_no_NO, new Locale("no", "NO"), new Boolean(false), "dd/MM/yyyy", MediaSize.ISO.A4), new Language("Polski", AD_Language_pl_PL, new Locale("pl", "PL"), new Boolean(false), "dd-MM-yyyy", MediaSize.ISO.A4), new Language("Portugu"+"\u0411"+"s (BR)", AD_Language_pt_BR, new Locale("pt", "BR"), new Boolean(false), "dd/MM/yyyy", MediaSize.ISO.A4), new Language("Rom\u00e2n\u0103", AD_Language_ro_RO, new Locale("ro", "RO"), new Boolean(false), "dd.MM.yyyy", MediaSize.ISO.A4), new Language("\u0420\u0443\u0441\u0441\u043a\u0438\u0439 (Russian)", AD_Language_ru_RU, new Locale("ru", "RU"), new Boolean(false), "dd-MM-yyyy", MediaSize.ISO.A4), new Language("Slovenski", AD_Language_sl_SI, new Locale("sl", "SI"), null, "dd.MM.yyyy", MediaSize.ISO.A4), new Language("Svenska", AD_Language_sv_SE, new Locale("sv", "SE"), new Boolean(false), "dd.MM.yyyy", MediaSize.ISO.A4), new Language("\u0e44\u0e17\u0e22 (TH)", AD_Language_th_TH, new Locale("th", "TH"), new Boolean(false), "dd/MM/yyyy", MediaSize.ISO.A4), new Language("Vi\u1EC7t Nam", AD_Language_vi_VN, new Locale("vi", "VN"), new Boolean(false), "dd-MM-yyyy", MediaSize.ISO.A4), new Language("\u7b80\u4f53\u4e2d\u6587 (CN)", AD_Language_zh_CN, Locale.CHINA, null, "yyyy-MM-dd", MediaSize.ISO.A4),
        new Language("\u7e41\u9ad4\u4e2d\u6587 (TW)", AD_Language_zh_TW, Locale.TAIWAN, null, null,	// dd.MM.yy
                             MediaSize.ISO.A4),
        new Language("Espa\u00F1ol (Argentina)", 	AD_Language_es_AR, new Locale("es", "AR"), null, "dd.MM.yyyy", MediaSize.ISO.A4),
        //new Language("Espa\u00F1ol (Mexico)", 		AD_Language_es_MX, new Locale("es", "MX"), null, "dd.MM.yyyy", MediaSize.ISO.A4),
        new Language("Espa\u00F1ol (Paraguay)",     AD_Language_es_PY, new Locale("es", "PY"), null, "dd.MM.yyyy", MediaSize.ISO.A4)
    };

    /** Descripción de Campos */
    private static Language	s_loginLanguage	= s_languages[0];

    /** Descripción de Campos */
    private static Logger	log	= Logger.getLogger(Language.class.getName());

    /** Descripción de Campos */
    private MediaSize	m_mediaSize	= MediaSize.ISO.A4;

    /** Descripción de Campos */
    private String	m_AD_Language;

    /** Descripción de Campos */
    private SimpleDateFormat	m_dateFormat;

    //

    /** Descripción de Campos */
    private Boolean	m_decimalPoint;

    /** Descripción de Campos */
    private Boolean	m_leftToRight;

    /** Descripción de Campos */
    private Locale	m_locale;

    /** Descripción de Campos */
    private String	m_name;

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     * @param AD_Language
     * @param locale
     */
    public Language(String name, String AD_Language, Locale locale) {
        this(name, AD_Language, locale, null, null, null);
    }		// Language

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     * @param AD_Language
     * @param locale
     * @param decimalPoint
     * @param javaDatePattern
     * @param mediaSize
     */
    public Language(String name, String AD_Language, Locale locale, Boolean decimalPoint, String javaDatePattern, MediaSize mediaSize) {

        if ((name == null) || (AD_Language == null) || (locale == null)) {
            throw new IllegalArgumentException("Idioma -> El lenguaje es nulo o no existe");
        }

        m_name		= name;
        m_AD_Language	= AD_Language;
        m_locale	= locale;

        //
        m_decimalPoint	= decimalPoint;
        setDateFormat(javaDatePattern);
        setMediaSize(mediaSize);

    }		// Language

    /**
     * Descripción de Método
     *
     *
     * @param language
     */
    public static void addLanguage(Language language) {

        if (language == null) {
            return;
        }

        ArrayList	list	= new ArrayList(Arrays.asList(s_languages));

        list.add(language);
        s_languages	= new Language[list.size()];
        list.toArray(s_languages);

    }		// addLanguage

    /**
     * Descripción de Método
     *
     *
     * @param obj
     *
     * @return
     */
    public boolean equals(Object obj) {

        if (obj instanceof Language) {

            Language	cmp	= (Language) obj;

            if (cmp.getAD_Language().equals(m_AD_Language)) {
                return true;
            }
        }

        return false;

    }		// equals

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int hashCode() {
        return m_AD_Language.hashCode();
    }		// hashcode

    /**
     * Descripción de Método
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        System.out.println(Locale.TRADITIONAL_CHINESE);
        System.out.println(Locale.TAIWAN);
        System.out.println(Locale.SIMPLIFIED_CHINESE);
        System.out.println(Locale.CHINESE);
        System.out.println(Locale.PRC);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("Language=[");

        sb.append(m_name).append(",Locale=").append(m_locale.toString()).append(",AD_Language=").append(m_AD_Language).append(",DatePattern=").append(getDBdatePattern()).append(",DecimalPoint=").append(isDecimalPoint()).append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getAD_Language() {
        return m_AD_Language;
    }		// getAD_Language

    /**
     * Descripción de Método
     *
     *
     * @param locale
     *
     * @return
     */
    public static String getAD_Language(Locale locale) {

        if (locale != null) {

            for (int i = 0; i < s_languages.length; i++) {

                if (locale.equals(s_languages[i].getLocale())) {
                    return s_languages[i].getAD_Language();
                }
            }
        }

        return s_loginLanguage.getAD_Language();

    }		// getLocale

    /**
     * Descripción de Método
     *
     *
     * @param langInfo
     *
     * @return
     */
    public static String getAD_Language(String langInfo) {
        return getLanguage(langInfo).getAD_Language();
    }		// getAD_Language

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getBaseAD_Language() {
        return s_languages[0].getAD_Language();
    }		// getBase

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Language getBaseLanguage() {
        return s_languages[0];
    }		// getBase

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getDBdatePattern() {
        return getDateFormat().toPattern().toUpperCase(m_locale);
    }		// getDBdatePattern

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public SimpleDateFormat getDateFormat() {

        if (m_dateFormat == null) {

            m_dateFormat	= (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, m_locale);

            String	sFormat	= m_dateFormat.toPattern();

            // some short formats have only one M and d (e.g. ths US)
            if ((sFormat.indexOf("MM") == -1) && (sFormat.indexOf("dd") == -1)) {

                String	nFormat	= "";

                for (int i = 0; i < sFormat.length(); i++) {

                    if (sFormat.charAt(i) == 'M') {
                        nFormat	+= "MM";
                    } else if (sFormat.charAt(i) == 'd') {
                        nFormat	+= "dd";
                    } else {
                        nFormat	+= sFormat.charAt(i);
                    }
                }

                // System.out.println(sFormat + " => " + nFormat);
                m_dateFormat.applyPattern(nFormat);
            }

            // Unknown short format => use JDBC
            if (m_dateFormat.toPattern().length() != 8) {
                m_dateFormat.applyPattern("yyyy-MM-dd");
            }

            // 4 digit year
            if (m_dateFormat.toPattern().indexOf("yyyy") == -1) {

                sFormat	= m_dateFormat.toPattern();

                String	nFormat	= "";

                for (int i = 0; i < sFormat.length(); i++) {

                    if (sFormat.charAt(i) == 'y') {
                        nFormat	+= "yy";
                    } else {
                        nFormat	+= sFormat.charAt(i);
                    }
                }

                m_dateFormat.applyPattern(nFormat);
            }

            m_dateFormat.setLenient(true);
        }

        return m_dateFormat;

    }		// getDateFormat

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public SimpleDateFormat getDateTimeFormat() {

        SimpleDateFormat	retValue	= (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, m_locale);

        // System.out.println("Pattern=" + retValue.toLocalizedPattern() + ", Loc=" + retValue.toLocalizedPattern());
        return retValue;

    }		// getDateTimeFormat

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */
    public static Language getLanguage(int index) {

        if ((index < 0) || (index >= s_languages.length)) {
            return s_loginLanguage;
        }

        return s_languages[index];

    }		// getLanguage

    /**
     * Descripción de Método
     *
     *
     * @param langInfo
     *
     * @return
     */
    public static Language getLanguage(String langInfo) {

        String	lang	= langInfo;

        if ((lang == null) || (lang.length() == 0)) {
            lang	= System.getProperty("user.language", "");
        }

        // Search existing Languages
        for (int i = 0; i < s_languages.length; i++) {

            if (lang.equals(s_languages[i].getName()) || lang.equals(s_languages[i].getLanguageCode()) || lang.equals(s_languages[i].getAD_Language())) {
                return s_languages[i];
            }
        }

        // Create Language on the fly
        if (lang.length() == 5)		// standard format <language>_<Country>
        {

            String	language	= lang.substring(0, 2);
            String	country		= lang.substring(3);
            Locale	locale		= new Locale(language, country);

            log.info("Adding Language=" + language + ", Country=" + country + ", Locale=" + locale);

            Language	ll	= new Language(lang, lang, locale);

            // Add to Languages
            java.util.List	list	= new ArrayList(Arrays.asList(s_languages));

            list.add(ll);
            s_languages	= new Language[list.size()];
            list.toArray(s_languages);

            // Return Language
            return ll;
        }

        // Get the default one
        return s_loginLanguage;

    }		// getLanguage

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getLanguageCode() {
        return m_locale.getLanguage();
    }		// getLanguageCode

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static int getLanguageCount() {
        return s_languages.length;
    }		// getLanguageCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public Locale getLocale() {
        return m_locale;
    }		// getLocale

    /**
     * Descripción de Método
     *
     *
     * @param langInfo
     *
     * @return
     */
    public static Locale getLocale(String langInfo) {
        return getLanguage(langInfo).getLocale();
    }		// getLocale

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Language getLoginLanguage() {
        return s_loginLanguage;
    }		// getLanguage

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public MediaSize getMediaSize() {
        return m_mediaSize;
    }		// getMediaSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getName() {
        return m_name;
    }		// getName

    /**
     * Descripción de Método
     *
     *
     * @param langInfo
     *
     * @return
     */
    public static String getName(String langInfo) {
        return getLanguage(langInfo).getName();
    }		// getAD_Language

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String[] getNames() {

        String[]	retValue	= new String[s_languages.length];

        for (int i = 0; i < s_languages.length; i++) {
            retValue[i]	= s_languages[i].getName();
        }

        return retValue;

    }		// getNames

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public SimpleDateFormat getTimeFormat() {
        return (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.LONG, m_locale);
    }		// getTimeFormat

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isBaseLanguage() {
        return this.equals(getBaseLanguage());
    }		// isBaseLanguage

    /**
     * Descripción de Método
     *
     *
     * @param langInfo
     *
     * @return
     */
    public static boolean isBaseLanguage(String langInfo) {

        if ((langInfo == null) || (langInfo.length() == 0) || langInfo.equals(s_languages[0].getName()) || langInfo.equals(s_languages[0].getLanguageCode()) || langInfo.equals(s_languages[0].getAD_Language())) {
            return true;
        }

        return false;

    }		// isBaseLanguage

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isDecimalPoint() {

        if (m_decimalPoint == null) {

            DecimalFormatSymbols	dfs	= new DecimalFormatSymbols(m_locale);

            m_decimalPoint	= new Boolean(dfs.getDecimalSeparator() == '.');
        }

        return m_decimalPoint.booleanValue();

    }		// isDecimalPoint

    /**
     * Descripción de Método
     *
     *
     * @param langInfo
     *
     * @return
     */
    public static boolean isDecimalPoint(String langInfo) {
        return getLanguage(langInfo).isDecimalPoint();
    }		// getAD_Language

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isLeftToRight() {

        if (m_leftToRight == null) {

            // returns true if language not iw, ar, fa, ur
            m_leftToRight	= new Boolean(ComponentOrientation.getOrientation(m_locale).isLeftToRight());
        }

        return m_leftToRight.booleanValue();

    }		// isLeftToRight

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param AD_Language
     */
    public void setAD_Language(String AD_Language) {

        if (AD_Language != null) {

            m_AD_Language	= AD_Language;
            log.config(toString());
        }

    }		// getAD_Language

    /**
     * Descripción de Método
     *
     *
     * @param javaDatePattern
     */
    public void setDateFormat(String javaDatePattern) {

        if (javaDatePattern == null) {
            return;
        }

        m_dateFormat	= (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, m_locale);

        try {
            m_dateFormat.applyPattern(javaDatePattern);
        } catch (Exception e) {

            log.severe(javaDatePattern + " - " + e);
            m_dateFormat	= null;
        }

    }		// setDateFormat

    /**
     * Descripción de Método
     *
     *
     * @param locale
     */
    public void setLocale(Locale locale) {

        if (locale == null) {
            return;
        }

        m_locale	= locale;
        m_decimalPoint	= null;		// reset

    }					// getLocale

    /**
     * Descripción de Método
     *
     *
     * @param language
     */
    public static void setLoginLanguage(Language language) {

        if (language != null) {

            s_loginLanguage	= language;
            log.config(s_loginLanguage.toString());
        }

    }		// setLanguage

    /**
     * Descripción de Método
     *
     *
     * @param size
     */
    public void setMediaSize(MediaSize size) {

        if (size != null) {
            m_mediaSize	= size;
        }

    }		// setMediaSize
}	// Language



/*
 * @(#)Language.java   02.jul 2007
 * 
 *  Fin del fichero Language.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
