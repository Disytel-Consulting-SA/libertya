/*
 * @(#)MCurrency.java   12.oct 2007  Versión 2.2
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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import org.openXpertya.util.CCache;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 *      Currency Model.
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MCurrency.java,v 1.7 2005/03/11 20:28:32 jjanke Exp $
 */
public class MCurrency extends X_C_Currency {

    /** Store System Currencies */
    private static CCache	s_currencies	= new CCache("C_Currency", 50);

    public MCurrency(Properties ctx, ResultSet rs, String trxName) {
    	super(ctx, rs, trxName);
    }
    
    /**
     *      Currency Constructor
     *      @param ctx context
     *      @param C_Currency_ID id
     * @param trxName
     */
    public MCurrency(Properties ctx, int C_Currency_ID, String trxName) {

        super(ctx, C_Currency_ID, trxName);

        if (C_Currency_ID == 0) {

            setIsEMUMember(false);
            setIsEuro(false);
            setStdPrecision(2);
            setCostingPrecision(4);
        }

    }		// MCurrency

    /**
     *      Currency Constructor
     *      @param ctx context
     *      @param ISO_Code ISO
     *      @param Description Name
     *      @param CurSymbol symbol
     *      @param StdPrecision prec
     *      @param CostingPrecision prec
     * @param trxName
     */
    public MCurrency(Properties ctx, String ISO_Code, String Description, String CurSymbol, int StdPrecision, int CostingPrecision, String trxName) {

        super(ctx, 0, trxName);
        setISO_Code(ISO_Code);
        setDescription(Description);
        setCurSymbol(CurSymbol);
        setStdPrecision(StdPrecision);
        setCostingPrecision(CostingPrecision);
        setIsEMUMember(false);
        setIsEuro(false);

    }		// MCurrency

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {
        return "MCurrency[" + getC_Currency_ID() + "-" + getISO_Code() + "-" + getCurSymbol() + "," + getDescription() + ",Precision=" + getStdPrecision() + "/" + getCostingPrecision();
    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Currency
     *      @param ctx Context
     *      @param C_Currency_ID currency
     *      @return ISO Code
     */
    public static MCurrency get(Properties ctx, int C_Currency_ID) {
    	return get(ctx, C_Currency_ID, null);
    }		// get

    
    /**
     *      Get Currency
     *      @param ctx Context
     *      @param C_Currency_ID currency
     *      @param trxName 
     *      @return ISO Code
     */
    public static MCurrency get(Properties ctx, int C_Currency_ID, String trxName) {

        // Try Cache
        Integer		key		= new Integer(C_Currency_ID);
        MCurrency	retValue	= (MCurrency) s_currencies.get(key);

        if (retValue != null) {
            return retValue;
        }

        // Create it
        retValue	= new MCurrency(ctx, C_Currency_ID, trxName);

        // Save in System
        if (retValue.getAD_Client_ID() == 0) {
            s_currencies.put(key, retValue);
        }

        return retValue;
    }		// get
    
    /**
     * @param ctx
     * @param ISO_Code
     * @return 
     */
    public static MCurrency get(Properties ctx, String ISO_Code) {
		return MCurrency.get(
				ctx,
				DB.getSQLValue(null, "SELECT c_currency_id FROM " + Table_Name
						+ " WHERE upper(trim(ISO_Code)) = upper(trim('"
						+ ISO_Code + "'))"));
    }
    
    /**
     *      Get Currency Iso Code.
     *      @param ctx Context
     *      @param C_Currency_ID currency
     *      @return ISO Code
     */
    public static String getISO_Code(Properties ctx, int C_Currency_ID) {

        String	contextKey	= "C_Currency_" + C_Currency_ID;
        String	retValue	= ctx.getProperty(contextKey);

        if (retValue != null) {
            return retValue;
        }

        // Create it
        MCurrency	c	= get(ctx, C_Currency_ID);

        retValue	= c.getISO_Code();
        ctx.setProperty(contextKey, retValue);

        return retValue;

    }		// getISO

    
    
    /**
     *      Get Standard Precision.
     *      @param ctx Context
     *      @param C_Currency_ID currency
     *      @return Standard Precision
     */
    public static int getStdPrecision(Properties ctx, int C_Currency_ID) {
    	return getStdPrecision(ctx, C_Currency_ID, null);
    }		// getStdPrecision
    
    /**
     *      Get Standard Precision.
     *      @param ctx Context
     *      @param C_Currency_ID currency
     *      @return Standard Precision
     */
    public static int getStdPrecision(Properties ctx, int C_Currency_ID, String trxName) {
        MCurrency	c	= get(ctx, C_Currency_ID, trxName);
        return c.getStdPrecision();
    }		// getStdPrecision

    
    public static BigDecimal currencyConvert(BigDecimal amount, int currencyFrom, int currencyTo, Date date, int adOrg, Properties ctx )
    {
    	BigDecimal result = null;
    	try
    	{
    		StringBuffer sql = new StringBuffer("SELECT currencyconvert (?, ?, ?, ? ::timestamp, null, ?, ");
    		if (adOrg > 0)
    			sql.append( "? )");
    		else
    			sql.append( "null )");

    		PreparedStatement pstmt = DB.prepareStatement(sql.toString());
    		pstmt.setBigDecimal(1, amount);
    		pstmt.setInt(2, currencyFrom);
    		pstmt.setInt(3, currencyTo);
    		//pstmt.setDate(4, new  java.sql.Date(date.getTime()) );
    		// currencyconvert requiere un timestamp como parametro. En ciertos casos 
    		// estaba funcionando mal con el Date. 
    		pstmt.setTimestamp(4, new Timestamp(date.getTime()) ); 
    		pstmt.setInt(5, Env.getAD_Client_ID(ctx) );
    		
    		if (adOrg > 0)
    			pstmt.setInt(6, adOrg );
    		
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    			result = rs.getBigDecimal(1);
    	}
    	catch (Exception e ) {
    		e.printStackTrace();
    	}
		return result;
    }    
    
    /** ********************************************************************* */

    /**
     *      Load/link Currencies
     *      @param args args
     *
     * public static void main (String[] args)
     * {
     *       System.out.println("Currency");
     *       OpenXpertya.startupClient();
     *       //      Loop through
     *       for (int i = 0; i < s_table.length; i++)
     *       {
     *               
     *               System.out.println(s_table[i][I_Currency] + " - " + s_table[i][I_Name]);
     *               int prec = Integer.parseInt(s_table[i][I_Precision]);
     *               MCurrency cur = new MCurrency(Env.getCtx(), s_table[i][I_Currency],
     *                       s_table[i][I_Name], s_table[i][I_Symbol], prec, prec+2);
     *               cur.save();
     *               System.out.println(cur);
     *               
     *               String ISO = s_table[i][I_Currency];
     *               String Country = s_table[i][I_Country];
     *               String sql = "UPDATE C_Country SET C_Currency_ID="
     *                       + "(SELECT C_Currency_ID FROM C_Currency WHERE ISO_Code='" + ISO + "') "
     *                       + "WHERE CountryCode='" + Country + "'";
     *               int no = DB.executeUpdate(sql);
     *               System.out.println(ISO + " - " + Country + " - " + no);
     *               System.out.println("");
     *       }
     *
     * }       //      main
     *
     *
     * static int      I_Country = 0;
     * static int      I_Currency = 1;
     * static int      I_Precision = 2;
     * static int      I_Symbol = 3;
     * static int      I_DecimalPoint = 4;
     * static int      I_FormatIndex = 5;
     * static int      I_Name = 6;
     * static String[][] s_table = new String[][] {
     *       new String[]{"US","USD","2","$",".","0", "US Dollar"},
     *       new String[]{"AR","ARS","2","$",",","0", "Argentine Peso"},
     *       new String[]{"AS","USD","2","$",".","0","US Dollar"},
     *       new String[]{"CC","AUD","2","$",".","0","Australian Dollar"},
     *       new String[]{"CK","NZD","2","$",".","0","New Zealand Dollar"},
     *       new String[]{"CX","AUD","2","$",".","0","Australian Dollar"},
     *       new String[]{"EC","USD","2","$",".","0","US Dollar"},
     *       new String[]{"FM","USD","2","$",".","0","US Dollar"},
     *       new String[]{"GU","USD","2","$",".","0","US Dollar"},
     *       new String[]{"KI","AUD","2","$",".","0","Australian Dollar"},
     *       new String[]{"LR","LRD","2","$",".","0","Liberian Dollar"},
     *       new String[]{"MH","USD","2","$",".","0","US Dollar"},
     *       new String[]{"MP","USD","2","$",".","0","US Dollar"},
     *       new String[]{"MX","MXN","2","$",".","0","Mexican Peso"},
     *       new String[]{"NF","AUD","2","$",".","0","Australian Dollar"},
     *       new String[]{"NR","AUD","2","$",".","0","Australian Dollar"},
     *       new String[]{"NU","NZD","2","$",".","0","New Zealand Dollar"},
     *       new String[]{"NZ","NZD","2","$",".","0","New Zealand Dollar"},
     *       new String[]{"PR","USD","2","$",".","0","US Dollar"},
     *       new String[]{"PW","USD","2","$",".","0","US Dollar"},
     *       new String[]{"TC","USD","2","$",".","0","US Dollar"},
     *       new String[]{"TK","NZD","2","$",".","0","New Zealand Dollar"},
     *       new String[]{"TV","AUD","2","$",".","0","Australian Dollar"},
     *       new String[]{"VG","USD","2","$",".","0","US Dollar"},
     *       new String[]{"VI","USD","2","$",".","0","US Dollar"},
     *       new String[]{"UY","UYU","2","$U",",","2","Peso Uruguayo"},
     *       new String[]{"AM","AMD","2","",".","0","Armenian Dram"},
     *       new String[]{"AO","AOA","2","",".","0","Kwanza"},
     *       new String[]{"AZ","AZM","2","",".","0","Azerbaijanian Manat"},
     *       new String[]{"BO","BOB","2","",".","0","Boliviano"},
     *       new String[]{"CD","CDF","2","",".","0","Franc Congolais"},
     *       new String[]{"CZ","CZK","2","",",","3","Czech Koruna"},
     *       new String[]{"GE","GEL","2","",".","0","Lari"},
     *       new String[]{"IR","IRR","2","",".","2","Iranian Rial"},
     *       new String[]{"LT","LTL","2","",",","3","Lithuanian Litus"},
     *       new String[]{"MD","MDL","2","",".","0","Moldovan Leu"},
     *       new String[]{"PH","PHP","2","",".","0","Philippine Peso"},
     *       new String[]{"PL","PLN","2","",",","3","Zloty"},
     *       new String[]{"RU","RUR","2","",",","1","Russian Ruble"},
     *       new String[]{"SD","SDD","2","",".","0","Sudanese Dinar"},
     *       new String[]{"TJ","TJS","2","",".","0","Somoni"},
     *       new String[]{"TM","TMM","2","",".","0","Manat"},
     *       new String[]{"TP","TPE","0","",".","0","Timor Escudo"},
     *       new String[]{"UA","UAH","2","",",","3","Hryvnia"},
     *       new String[]{"UZ","UZS","2","",".","0","Uzbekistan Sum"},
     *       new String[]{"GB","GBP","2","ï¿½",".","0","Pound Sterling"},
     *       new String[]{"CY","CYP","2","ï¿½C",".","0","Cyprus Pound"},
     *       new String[]{"EG","EGP","2","ï¿½E",".","2","Egyptian Pound"},
     *       new String[]{"FK","FKP","2","ï¿½F",".","0","Falkland Islands Pound"},
     *       new String[]{"GI","GIP","2","ï¿½G",".","0","Gibraltar Pound"},
     *       new String[]{"SH","SHP","2","ï¿½S",".","0","Saint Helena Pound"},
     *       new String[]{"SY","SYP","2","ï¿½S",".","2","Syrian Pound"},
     *       new String[]{"JP","JPY","0","ï¿½",".","0","Yen"},
     *       new String[]{"GH","GHC","2","ï¿½",".","0","Cedi"},
     *       new String[]{"SV","SVC","2","ï¿½",".","0","El Salvador Colon"},
     *       new String[]{"AD","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"AT","EUR","2","ï¿½",",","2","euro"},
     *       new String[]{"BE","EUR","2","ï¿½",",","3","euro"},
     *       new String[]{"DE","EUR","2","ï¿½",",","3","euro"},
     *       new String[]{"ES","EUR","2","ï¿½",",","3","euro"},
     *       new String[]{"FI","EUR","2","ï¿½",",","3","euro"},
     *       new String[]{"FR","EUR","2","ï¿½",",","3","euro"},
     *       new String[]{"GF","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"GP","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"GR","EUR","2","ï¿½",",","3","euro"},
     *       new String[]{"IE","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"IT","EUR","2","ï¿½",",","2","euro"},
     *       new String[]{"LU","EUR","2","ï¿½",",","3","euro"},
     *       new String[]{"MC","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"MQ","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"NL","EUR","2","ï¿½",",","2","euro"},
     *       new String[]{"PM","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"PT","EUR","2","ï¿½","$","3","euro"},
     *       new String[]{"RE","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"SM","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"VA","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"YT","EUR","2","ï¿½",".","0","euro"},
     *       new String[]{"AU","AUD","2","A$",".","0","Australian Dollar"},
     *       new String[]{"AF","AFA","2","Af",".","0","Afghani"},
     *       new String[]{"AW","AWG","2","Af.",".","0","Aruban Guilder"},
     *       new String[]{"PA","PAB","2","B",".","0","Balboa"},
     *       new String[]{"BN","BND","2","B$",".","0","Brunei Dollar"},
     *       new String[]{"BS","BSD","2","B$",".","0","Bahamian Dollar"},
     *       new String[]{"BH","BHD","3","BD",".","2","Bahraini Dinar"},
     *       new String[]{"BM","BMD","2","Bd$",".","0","Bermudian Dollar"},
     *       new String[]{"BB","BBD","2","Bds$",".","0","Barbados Dollar"},
     *       new String[]{"BY","BYR","0","BR",",","3","Belarussian Ruble"},
     *       new String[]{"ET","ETB","2","Br",".","0","Ethiopian Birr"},
     *       new String[]{"VE","VEB","2","Bs",",","0","Bolivar"},
     *       new String[]{"TH","THB","2","Bt",".","0","Baht"},
     *       new String[]{"BZ","BZD","2","BZ$",".","0","Belize Dollar"},
     *       new String[]{"CA","CAD","2","C$",".","0","Canadian Dollar"},
     *       new String[]{"NI","NIO","2","C$",".","0","Cordoba Oro"},
     *       new String[]{"CV","CVE","2","C.V.Esc.",".","0","Cape Verde Escudo"},
     *       new String[]{"KM","KMF","0","CF",".","0","Comoro Franc"},
     *       new String[]{"BF","XOF","0","CFAF",".","0","CFA Franc BCEAO"},
     *       new String[]{"BJ","XOF","0","CFAF",".","0","CFA Franc BCEAO"},
     *       new String[]{"CF","XAF","0","CFAF",".","0","CFA Franc BEAC"},
     *       new String[]{"CG","XAF","0","CFAF",".","0","CFA Franc BEAC"},
     *       new String[]{"CI","XOF","0","CFAF",".","0","CFA Franc BCEA"},
     *       new String[]{"CM","XAF","0","CFAF",".","0","CFA Franc BEAC"},
     *       new String[]{"GA","XAF","0","CFAF",".","0","CFA Franc BEAC"},
     *       new String[]{"GQ","XAF","0","CFAF",".","0","CFA Franc BEAC"},
     *       new String[]{"ML","XOF","0","CFAF",".","0","CFA Franc BCEAO"},
     *       new String[]{"NE","XOF","0","CFAF",".","0","CFA Franc BCEAO"},
     *       new String[]{"SN","XOF","0","CFAF",".","0","CFA Franc BCEAO"},
     *       new String[]{"TD","XAF","0","CFAF",".","0","CFA Franc BEAC"},
     *       new String[]{"TG","XOF","0","CFAF",".","0","CFA Franc BCEAO"},
     *       new String[]{"NC","XPF","0","CFPF",".","0","CFP Franc"},
     *       new String[]{"PF","XPF","0","CFPF",".","0","CFP Franc"},
     *       new String[]{"WF","XPF","0","CFPF",".","0","CFP Franc"},
     *       new String[]{"CL","CLP","0","Ch$",",","0","Chilean Peso"},
     *       new String[]{"KY","KYD","2","CI$",".","0","Cayman Islands Dollar"},
     *       new String[]{"CO","COP","2","Col$",".","0","Colombian Peso"},
     *       new String[]{"KH","KHR","2","CR",".","0","Riel"},
     *       new String[]{"CU","CUP","2","Cu$",".","0","Cuban Peso"},
     *       new String[]{"GM","GMD","2","D",".","0","Dalasi"},
     *       new String[]{"VN","VND","2","D",",","3","Dong"},
     *       new String[]{"DZ","DZD","2","DA",".","2","Algerian Dinar"},
     *       new String[]{"ST","STD","2","Db",".","0","Dobra"},
     *       new String[]{"DJ","DJF","0","DF",".","0","Djibouti Franc"},
     *       new String[]{"AE","AED","2","Dh",".","2","UAE Dirham"},
     *       new String[]{"MA","MAD","2","DH",".","2","Moroccan Dirham"},
     *       new String[]{"YU","YUM","2","Din",".","0","Yugoslavian Dinar"},
     *       new String[]{"DK","DKK","2","Dkr",",","2","Danish Krone"},
     *       new String[]{"FO","DKK","2","Dkr",",","2","Danish Krone"},
     *       new String[]{"GL","DKK","2","Dkr",".","0","Danish Krone"},
     *       new String[]{"AG","XCD","2","EC$",".","0","East Caribbean Dollar"},
     *       new String[]{"AI","XCD","2","EC$",".","0","East Caribbean Dollar"},
     *       new String[]{"DM","XCD","2","EC$",".","0","East Caribbean Dollar"},
     *       new String[]{"GD","XCD","2","EC$",".","0","East Caribbean Dollar"},
     *       new String[]{"KN","XCD","2","EC$",".","0","East Caribbean Dollar"},
     *       new String[]{"LC","XCD","2","EC$",".","0","East Caribbean Dollar"},
     *       new String[]{"MS","XCD","2","EC$",".","0","East Caribbean Dollar"},
     *       new String[]{"VC","XCD","2","EC$",".","0","East Caribbean Dollar"},
     *       new String[]{"FJ","FJD","2","F$",".","0","Fiji Dollar"},
     *       new String[]{"BI","BIF","0","FBu",".","0","Burundi Franc"},
     *       new String[]{"MG","MGF","0","FMG",".","0","Malagasy Franc"},
     *       new String[]{"HU","HUF","2","Ft",",","3","Forint"},
     *       new String[]{"HT","HTG","2","G",".","0","Gourde"},
     *       new String[]{"GY","GYD","2","G$",".","0","Guyana Dollar"},
     *       new String[]{"HK","HKD","2","HK$",".","0","Hong Kong Dollar"},
     *       new String[]{"HR","HRK","2","HRK",",","2","Croatian Kuna"},
     *       new String[]{"IQ","IQD","3","ID",".","2","Iraqi Dinar"},
     *       new String[]{"IS","ISK","2","IKr",",","3","Iceland Krona"},
     *       new String[]{"JM","JMD","2","J$",".","0","Jamaican Dollar"},
     *       new String[]{"JO","JOD","3","JD",".","2","Jordanian Dinar"},
     *       new String[]{"MM","MMK","2","K",".","0","Kyat"},
     *       new String[]{"PG","PGK","2","K",".","0","Kina"},
     *       new String[]{"KE","KES","2","K Sh",".","0","Kenyan Shilling"},
     *       new String[]{"KW","KWD","3","KD",".","2","Kuwaiti Dinar"},
     *       new String[]{"BA","BAM","2","KM",".","0","Convertible Marks"},
     *       new String[]{"LA","LAK","2","KN",".","0","Kip"},
     *       new String[]{"ER","ERN","2","KR",".","0","Nakfa"},
     *       new String[]{"AL","ALL","2","L",",","1","Lek"},
     *       new String[]{"HN","HNL","2","L",".","0","Lempira"},
     *       new String[]{"RO","ROL","2","L",",","3","Leu"},
     *       new String[]{"SZ","SZL","2","L",".","0","Lilangeni"},
     *       new String[]{"LY","LYD","3","LD",".","2","Libyan Dinar"},
     *       new String[]{"SL","SLL","2","Le",".","0","Leone"},
     *       new String[]{"MT","MTL","2","Lm",".","0","Maltese Lira"},
     *       new String[]{"LV","LVL","2","Ls",",","3","Latvian Lats"},
     *       new String[]{"BG","BGL","2","Lv",",","3","Lev"},
     *       new String[]{"MU","MUR","2","Mau Rs",".","0","Mauritius Rupee"},
     *       new String[]{"MW","MWK","2","MK",".","0","Kwacha"},
     *       new String[]{"MK","MKD","2","MKD",".","0","Denar"},
     *       new String[]{"MZ","MZM","2","Mt",".","0","Metical"},
     *       new String[]{"AN","ANG","2","NAf.",".","0","Netherlands Antillian Guilder"},
     *       new String[]{"EE","EEK","2","Nfa",",","3","Kroon"},
     *       new String[]{"IL","ILS","2","NIS",".","2","New Israeli Sheqel"},
     *       new String[]{"NO","NOK","2","NKr",",","2","Norwegian Krone"},
     *       new String[]{"NP","NPR","2","NRs",".","0","Nepalese Rupee"},
     *       new String[]{"TW","TWD","2","NT$",".","0","New Taiwan Dollar"},
     *       new String[]{"BW","BWP","2","P",".","0","Pula"},
     *       new String[]{"MO","MOP","2","P",".","0","Pataca"},
     *       new String[]{"GT","GTQ","2","Q",".","0","Quetzal"},
     *       new String[]{"QA","QAR","2","QR",".","2","Qatari Rial"},
     *       new String[]{"LS","ZAR","2","R",".","0","Rand"},
     *       new String[]{"NA","ZAR","2","R",".","0","Rand"},
     *       new String[]{"ZA","ZAR","2","R",".","2","Rand"},
     *       new String[]{"BR","BRL","2","R$",",","0","Brazilian Real"},
     *       new String[]{"DO","DOP","2","RD$",".","0","Dominican Peso"},
     *       new String[]{"MV","MVR","2","Rf",".","0","Rufiyaa"},
     *       new String[]{"RW","RWF","0","RF",".","0","Rwanda Franc"},
     *       new String[]{"MY","MYR","2","RM",".","0","Malaysian Ringgit"},
     *       new String[]{"OM","OMR","3","RO",".","2","Rial Omani"},
     *       new String[]{"ID","IDR","2","Rp",",","0","Rupiah"},
     *       new String[]{"BT","INR","2","Rs",".","0","Indian Rupee"},
     *       new String[]{"IN","INR","2","Rs",".","0","Indian Rupee"},
     *       new String[]{"PK","PKR","2","Rs",".","0","Pakistan Rupee"},
     *       new String[]{"SG","SGD","2","S$",".","0","Singapore Dollar"},
     *       new String[]{"PE","PEN","2","S/.",",","0","Nuevo Sol"},
     *       new String[]{"SR","SRG","2","Sf.",".","0","Suriname Guilder"},
     *       new String[]{"SB","SBD","2","SI$",".","0","Solomon Islands Dollar"},
     *       new String[]{"SE","SEK","2","Sk",",","3","Swedish Krona"},
     *       new String[]{"SK","SKK","2","Sk",",","3","Slovak Koruna"},
     *       new String[]{"LK","LKR","2","SLRs",".","0","Sri Lanka Rupee"},
     *       new String[]{"SI","SIT","2","SlT",",","3","Tolar"},
     *       new String[]{"SO","SOS","2","So. Sh.",".","0","Somali Shilling"},
     *       new String[]{"SC","SCR","2","SR",".","0","Seychelles Rupee"},
     *       new String[]{"SA","SAR","2","SRls",".","2","Saudi Riyal"},
     *       new String[]{"CH","CHF","2","SwF",".","2","Swiss Franc"},
     *       new String[]{"LI","CHF","2","SwF",".","2","Swiss Franc"},
     *       new String[]{"TO","TOP","2","T$",".","0","Paï¿½anga"},
     *       new String[]{"TN","TND","3","TD",".","2","Tunisian Dinar"},
     *       new String[]{"BD","BDT","2","Tk",".","0","Taka"},
     *       new String[]{"TR","TRL","0","TL",",","3","Turkish Lira"},
     *       new String[]{"TZ","TZS","2","TSh",".","0","Tanzanian Shilling"},
     *       new String[]{"TT","TTD","2","TT$",".","0","Trinidad and Tobago Dollar"},
     *       new String[]{"MN","MNT","2","Tug",".","0","Tugrik"},
     *       new String[]{"MR","MRO","2","UM",".","0","Ouguiya"},
     *       new String[]{"UG","UGX","2","USh",".","0","Uganda Shilling"},
     *       new String[]{"VU","VUV","0","VT",".","0","Vatu"},
     *       new String[]{"KR","KRW","0","W",".","0","Won"},
     *       new String[]{"WS","WST","2","WS$",".","0","Tala"},
     *       new String[]{"CN","CNY","2","Y",".","0","Yuan Renminbi"},
     *       new String[]{"YE","YER","2","YRls",".","2","Yemeni Rial"},
     *       new String[]{"ZW","ZWD","2","Z$",".","0","Zimbabwe Dollar"},
     *       new String[]{"ZM","ZMK","2","ZK",".","0","Kwacha"},
     *       new String[]{"CR","CRC","2","",".","0","Costa Rican Colon"},
     *       new String[]{"GN","GNF","0","",".","0","Guinea Franc"},
     *       new String[]{"GW","GWP","2","",".","0","Guinea-Bissau Peso"},
     *       new String[]{"KG","KGS","2","",".","0","Som"},
     *       new String[]{"KP","KPW","2","",".","0","North Korean Won"},
     *       new String[]{"KZ","KZT","2","",".","0","Tenge"},
     *       new String[]{"LB","LBP","2","",".","2","Lebanese Pound"},
     *       new String[]{"NG","NGN","2","",".","0","Naira"},
     *       new String[]{"PY","PYG","0","",",","0","Guarani"}
     * };
     */
}	// MCurrency



/*
 * @(#)MCurrency.java   02.jul 2007
 * 
 *  Fin del fichero MCurrency.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
