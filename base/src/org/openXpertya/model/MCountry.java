/*
 * @(#)MCountry.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Language;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Location Country Model (Value Object)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *  @version    $Id: MCountry.java,v 1.16 2005/04/19 04:43:46 jjanke Exp $
 */
public final class MCountry extends X_C_Country implements Comparator, Serializable {

    /** Display Language */
    private static String	s_AD_Language	= null;

    /** Country Cache */
    private static CCache	s_countries	= null;

    /** Default Country */
    private static MCountry	s_default	= null;

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MCountry.class);

    // Default DisplaySequence */

    /** Descripción de Campo */
    private static String	DISPLAYSEQUENCE	= "@C@, @P@";

    /** Translated Name */
    private String	m_trlName	= null;

    /**
     *      Create empty Country
     *      @param ctx context
     *      @param C_Country_ID ID
     * @param trxName
     */
    public MCountry(Properties ctx, int C_Country_ID, String trxName) {

        super(ctx, C_Country_ID, trxName);

        if (C_Country_ID == 0) {

            // setName (null);
            // setCountryCode (null);
            setDisplaySequence(DISPLAYSEQUENCE);
            setHasRegion(false);
            setHasPostal_Add(false);
            setIsAddressLinesLocalReverse(false);
            setIsAddressLinesReverse(false);
        }

    }		// MCountry

    /**
     *      Create Country from current row in ResultSet
     *      @param ctx context
     *  @param rs ResultSet
     * @param trxName
     */
    public MCountry(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MCountry

    /**
     *  Compare based on Name
     *  @param o1 object 1
     *  @param o2 object 2
     *  @return -1,0, 1
     */
    public int compare(Object o1, Object o2) {

        String	s1	= o1.toString();

        if (s1 == null) {
            s1	= "";
        }

        String	s2	= o2.toString();

        if (s2 == null) {
            s2	= "";
        }

        return s1.compareTo(s2);

    }		// compare

    /**
     *      Load Countries.
     *      Set Default Language to Client Language
     *      @param ctx context
     */
    private static void loadAllCountries(Properties ctx) {

        MClient		client	= MClient.get(ctx);
        MLanguage	lang	= MLanguage.get(ctx, client.getAD_Language());
        MCountry	usa	= null;

        //
        s_countries	= new CCache("C_Country", 250);

        String	sql	= "SELECT * FROM C_Country WHERE IsActive='Y'";

        try {

            Statement	stmt	= DB.createStatement();
            ResultSet	rs	= stmt.executeQuery(sql);

            while (rs.next()) {

                MCountry	c	= new MCountry(ctx, rs, null);

                s_countries.put(String.valueOf(c.getC_Country_ID()), c);

                // Country code of Client Language
                if ((lang != null) && lang.getCountryCode().equals(c.getCountryCode())) {
                    s_default	= c;
                }

                if (c.getC_Country_ID() == 100) {	// USA
                    usa	= c;
                }
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        if (s_default == null) {
            s_default	= usa;
        }

        s_log.fine("#" + s_countries.size() + " - Default=" + s_default);

    }		// loadAllCountries

    /**
     *      Insert Countries
     *      @param args none
     */
    public static void main(String[] args) {

        /**
         *     Migration before
         * UPDATE C_Country SET AD_Client_ID=0, AD_Org_ID=0 WHERE AD_Client_ID<>0 OR AD_Org_ID<>0;
         * UPDATE C_Region SET AD_Client_ID=0, AD_Org_ID=0 WHERE AD_Client_ID<>0 OR AD_Org_ID<>0;
         * IDs migration for C_Location, C_City, C_Tax (C_Country, C_Region)
         *
         * //      from http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1-semic.txt
         * String countries = "AFGHANISTAN;AF, ALBANIA;AL, ALGERIA;DZ, AMERICAN SAMOA;AS, ANDORRA;AD, ANGOLA;AO, ANGUILLA;AI, ANTARCTICA;AQ, ANTIGUA AND BARBUDA;AG, ARGENTINA;AR,"
         *       + "ARMENIA;AM, ARUBA;AW, AUSTRALIA;AU, AUSTRIA;AT, AZERBAIJAN;AZ, BAHAMAS;BS, BAHRAIN;BH, BANGLADESH;BD, BARBADOS;BB, BELARUS;BY, BELGIUM;BE, BELIZE;BZ,"
         *       + "BENIN;BJ, BERMUDA;BM, BHUTAN;BT, BOLIVIA;BO, BOSNIA AND HERZEGOVINA;BA, BOTSWANA;BW, BOUVET ISLAND;BV, BRAZIL;BR, BRITISH INDIAN OCEAN TERRITORY;IO, BRUNEI DARUSSALAM;BN,"
         *       + "BULGARIA;BG, BURKINA FASO;BF, BURUNDI;BI, CAMBODIA;KH, CAMEROON;CM, CANADA;CA, CAPE VERDE;CV, CAYMAN ISLANDS;KY, CENTRAL AFRICAN REPUBLIC;CF, CHAD;TD, CHILE;CL,"
         *       + "CHINA;CN, CHRISTMAS ISLAND;CX, COCOS (KEELING) ISLANDS;CC, COLOMBIA;CO, COMOROS;KM, CONGO;CG, CONGO THE DEMOCRATIC REPUBLIC OF THE;CD, COOK ISLANDS;CK,"
         *       + "COSTA RICA;CR, COTE D'IVOIRE;CI, CROATIA;HR, CUBA;CU, CYPRUS;CY, CZECH REPUBLIC;CZ, DENMARK;DK, DJIBOUTI;DJ, DOMINICA;DM, DOMINICAN REPUBLIC;DO, ECUADOR;EC,"
         *       + "EGYPT;EG, EL SALVADOR;SV, EQUATORIAL GUINEA;GQ, ERITREA;ER, ESTONIA;EE, ETHIOPIA;ET, FALKLAND ISLANDS (MALVINAS);FK, FAROE ISLANDS;FO, FIJI;FJ,"
         *       + "FINLAND;FI, FRANCE;FR, FRENCH GUIANA;GF, FRENCH POLYNESIA;PF, FRENCH SOUTHERN TERRITORIES;TF, GABON;GA, GAMBIA;GM, GEORGIA;GE, GERMANY;DE, GHANA;GH,"
         *       + "GIBRALTAR;GI, GREECE;GR, GREENLAND;GL, GRENADA;GD, GUADELOUPE;GP, GUAM;GU, GUATEMALA;GT, GUINEA;GN, GUINEA-BISSAU;GW, GUYANA;GY, HAITI;HT,"
         *       + "HEARD ISLAND AND MCDONALD ISLANDS;HM, HOLY SEE (VATICAN CITY STATE);VA, HONDURAS;HN, HONG KONG;HK, HUNGARY;HU, ICELAND;IS, INDIA;IN, INDONESIA;ID,"
         *       + "IRAN ISLAMIC REPUBLIC OF;IR, IRAQ;IQ, IRELAND;IE, ISRAEL;IL, ITALY;IT, JAMAICA;JM, JAPAN;JP, JORDAN;JO, KAZAKHSTAN;KZ, KENYA;KE, KIRIBATI;KI, KOREA DEMOCRATIC PEOPLE'S REPUBLIC OF;KP,"
         *       + "KOREA REPUBLIC OF;KR, KUWAIT;KW, KYRGYZSTAN;KG, LAO PEOPLE'S DEMOCRATIC REPUBLIC;LA, LATVIA;LV, LEBANON;LB, LESOTHO;LS, LIBERIA;LR, LIBYAN ARAB JAMAHIRIYA;LY,"
         *       + "LIECHTENSTEIN;LI, LITHUANIA;LT, LUXEMBOURG;LU, MACAO;MO, MACEDONIA FORMER YUGOSLAV REPUBLIC OF;MK, MADAGASCAR;MG, MALAWI;MW, MALAYSIA;MY, MALDIVES;MV, "
         *       + "MALI;ML, MALTA;MT, MARSHALL ISLANDS;MH, MARTINIQUE;MQ, MAURITANIA;MR, MAURITIUS;MU, MAYOTTE;YT, MEXICO;MX, MICRONESIA FEDERATED STATES OF;FM,"
         *       + "MOLDOVA REPUBLIC OF;MD, MONACO;MC, MONGOLIA;MN, MONTSERRAT;MS, MOROCCO;MA, MOZAMBIQUE;MZ, MYANMAR;MM, NAMIBIA;NA, NAURU;NR, NEPAL;NP,"
         *       + "NETHERLANDS;NL, NETHERLANDS ANTILLES;AN, NEW CALEDONIA;NC, NEW ZEALAND;NZ, NICARAGUA;NI, NIGER;NE, NIGERIA;NG, NIUE;NU, NORFOLK ISLAND;NF,"
         *       + "NORTHERN MARIANA ISLANDS;MP, NORWAY;NO, OMAN;OM, PAKISTAN;PK, PALAU;PW, PALESTINIAN TERRITORY OCCUPIED;PS, PANAMA;PA, PAPUA NEW GUINEA;PG,"
         *       + "PARAGUAY;PY, PERU;PE, PHILIPPINES;PH, PITCAIRN;PN, POLAND;PL, PORTUGAL;PT, PUERTO RICO;PR, QATAR;QA, REUNION;RE, ROMANIA;RO, RUSSIAN FEDERATION;RU,"
         *       + "RWANDA;RW, SAINT HELENA;SH, SAINT KITTS AND NEVIS;KN, SAINT LUCIA;LC, SAINT PIERRE AND MIQUELON;PM, SAINT VINCENT AND THE GRENADINES;VC,"
         *       + "SAMOA;WS, SAN MARINO;SM, SAO TOME AND PRINCIPE;ST, SAUDI ARABIA;SA, SENEGAL;SN, SEYCHELLES;SC, SIERRA LEONE;SL, SINGAPORE;SG, SLOVAKIA;SK,"
         *       + "SLOVENIA;SI, SOLOMON ISLANDS;SB, SOMALIA;SO, SOUTH AFRICA;ZA, SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS;GS, SPAIN;ES, SRI LANKA;LK,"
         *       + "SUDAN;SD, SURINAME;SR, SVALBARD AND JAN MAYEN;SJ, SWAZILAND;SZ, SWEDEN;SE, SWITZERLAND;CH, SYRIAN ARAB REPUBLIC;SY, TAIWAN;TW,"
         *       + "TAJIKISTAN;TJ, TANZANIA UNITED REPUBLIC OF;TZ, THAILAND;TH, TIMOR-LESTE;TL, TOGO;TG, TOKELAU;TK, TONGA;TO, TRINIDAD AND TOBAGO;TT,"
         *       + "TUNISIA;TN, TURKEY;TR, TURKMENISTAN;TM, TURKS AND CAICOS ISLANDS;TC, TUVALU;TV, UGANDA;UG, UKRAINE;UA, UNITED ARAB EMIRATES;AE, UNITED KINGDOM;GB,"
         *       + "UNITED STATES;US, UNITED STATES MINOR OUTLYING ISLANDS;UM, URUGUAY;UY, UZBEKISTAN;UZ, VANUATU;VU, VENEZUELA;VE, VIET NAM;VN, VIRGIN ISLANDS BRITISH;VG,"
         *       + "VIRGIN ISLANDS U.S.;VI, WALLIS AND FUTUNA;WF, WESTERN SAHARA;EH, YEMEN;YE, YUGOSLAVIA;YU, ZAMBIA;ZM, ZIMBABWE;ZW";
         * /
         * org.openXpertya.OpenXpertya.startupClient();
         * StringTokenizer st = new StringTokenizer(countries, ",", false);
         * while (st.hasMoreTokens())
         * {
         *       String s = st.nextToken().trim();
         *       int pos = s.indexOf(";");
         *       String name = Util.initCap(s.substring(0,pos));
         *       String cc = s.substring(pos+1);
         *       System.out.println(cc + " - " + name);
         *       /
         *       MCountry mc = new MCountry(Env.getCtx(), 0);
         *       mc.setCountryCode(cc);
         *       mc.setName(name);
         *       mc.setDescription(name);
         *       mc.save();
         * }
         */
    }		// main

    /**
     *      Return Name - translated if DisplayLanguage is set.
     *  @return Name
     */
    public String toString() {

        if (s_AD_Language != null) {

            String	nn	= getTrlName();

            if (nn != null) {
                return nn;
            }
        }

        return getName();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Country (cached)
     *      @param ctx context
     *      @param C_Country_ID ID
     *      @return Country
     */
    public static MCountry get(Properties ctx, int C_Country_ID) {

        if ((s_countries == null) || (s_countries.size() == 0)) {
            loadAllCountries(ctx);
        }

        String		key	= String.valueOf(C_Country_ID);
        MCountry	c	= (MCountry) s_countries.get(key);

        if (c != null) {
            return c;
        }

        c	= new MCountry(ctx, C_Country_ID, null);

        if (c.getC_Country_ID() == C_Country_ID) {

            s_countries.put(key, c);

            return c;
        }

        return null;

    }		// get

    /**
     *      Return Countries as Array
     *      @param ctx context
     *  @return MCountry Array
     */
    public static MCountry[] getCountries(Properties ctx) {

        if ((s_countries == null) || (s_countries.size() == 0)) {
            loadAllCountries(ctx);
        }

        MCountry[]	retValue	= new MCountry[s_countries.size()];

        s_countries.values().toArray(retValue);
        Arrays.sort(retValue, new MCountry(ctx, 0, null));

        return retValue;

    }		// getCountries

    /**
     *      Get Default Country
     *      @param ctx context
     *      @return Country
     */
    public static MCountry getDefault(Properties ctx) {

        if ((s_countries == null) || (s_countries.size() == 0)) {
            loadAllCountries(ctx);
        }

        return s_default;

    }		// get

    /**
     *      Get Display Sequence
     *      @return display sequence
     */
    public String getDisplaySequence() {

        String	ds	= super.getDisplaySequence();

        if ((ds == null) || (ds.length() == 0)) {
            ds	= DISPLAYSEQUENCE;
        }

        return ds;

    }		// getDisplaySequence

    /**
     *      Get Local Display Sequence.
     *      If not defined get Display Sequence
     *      @return local display sequence
     */
    public String getDisplaySequenceLocal() {

        String	ds	= super.getDisplaySequenceLocal();

        if ((ds == null) || (ds.length() == 0)) {
            ds	= getDisplaySequence();
        }

        return ds;

    }		// getDisplaySequenceLocal

    /**
     *      Get Translated Name
     *      @return name
     */
    public String getTrlName() {

        if ((m_trlName != null) && (s_AD_Language != null)) {

            m_trlName	= get_Translation("Name", s_AD_Language);

            if (m_trlName == null) {
                s_AD_Language	= null;		// assume that there is no translation
            }
        }

        return m_trlName;

    }		// getTrlName

    //~--- set methods --------------------------------------------------------

    /**
     *      Set the Language for Display (toString)
     *      @param AD_Language language or null
     */
    public static void setDisplayLanguage(String AD_Language) {

        s_AD_Language	= AD_Language;

        if (Language.isBaseLanguage(AD_Language)) {
            s_AD_Language	= null;
        }

    }		// setDisplayLanguage
}	// MCountry



/*
 * @(#)MCountry.java   02.jul 2007
 * 
 *  Fin del fichero MCountry.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
