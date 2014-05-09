/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_POSPaymentMedium
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-05-09 17:18:30.132 */
public class X_C_POSPaymentMedium extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_POSPaymentMedium (Properties ctx, int C_POSPaymentMedium_ID, String trxName)
{
super (ctx, C_POSPaymentMedium_ID, trxName);
/** if (C_POSPaymentMedium_ID == 0)
{
setBeforeCheckDeadLineFrom (null);
setC_Currency_ID (0);	// @$C_Currency_ID@
setCheckDeadLine (null);
setContext (null);	// B
setC_POSPaymentMedium_ID (0);
setDateFrom (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDateTo (new Timestamp(System.currentTimeMillis()));
setIsMandatoryBank (false);
setIsNormalizedBank (false);
setM_EntidadFinanciera_ID (0);
setName (null);
setTenderType (null);	// 'CA'
setValidateBeforeCheckDeadLines (false);	// N
}
 */
}
/** Load Constructor */
public X_C_POSPaymentMedium (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_POSPaymentMedium");

/** TableName=C_POSPaymentMedium */
public static final String Table_Name="C_POSPaymentMedium";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_POSPaymentMedium");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_POSPaymentMedium[").append(getID()).append("]");
return sb.toString();
}
public static final int BANK_AD_Reference_ID = MReference.getReferenceID("Bank List");
/** Others Banks = O */
public static final String BANK_OthersBanks = "O";
/** Nacion = BN */
public static final String BANK_Nacion = "BN";
/** Frances = BF */
public static final String BANK_Frances = "BF";
/** Galicia = BG */
public static final String BANK_Galicia = "BG";
/** Hipotecario = BH */
public static final String BANK_Hipotecario = "BH";
/** Santander = BS */
public static final String BANK_Santander = "BS";
/** Provincia = BP */
public static final String BANK_Provincia = "BP";
/** Columbia = BC */
public static final String BANK_Columbia = "BC";
/** Comafi = CO */
public static final String BANK_Comafi = "CO";
/** Credicoop = CR */
public static final String BANK_Credicoop = "CR";
/** Itau = BI */
public static final String BANK_Itau = "BI";
/** Macro = BM */
public static final String BANK_Macro = "BM";
/** Citibank = CB */
public static final String BANK_Citibank = "CB";
/** Standard Bank = SB */
public static final String BANK_StandardBank = "SB";
/** American Express Bank = AE */
public static final String BANK_AmericanExpressBank = "AE";
/** BACS = CS */
public static final String BANK_BACS = "CS";
/** Bica = BB */
public static final String BANK_Bica = "BB";
/** Bradesco = BR */
public static final String BANK_Bradesco = "BR";
/** Cetelem = CE */
public static final String BANK_Cetelem = "CE";
/** CMF = CM */
public static final String BANK_CMF = "CM";
/** Ciudad = CI */
public static final String BANK_Ciudad = "CI";
/** Banco de Corrientes = AC */
public static final String BANK_BancoDeCorrientes = "AC";
/** Banco de Formosa = AF */
public static final String BANK_BancoDeFormosa = "AF";
/** Banco de La Pampa = AP */
public static final String BANK_BancoDeLaPampa = "AP";
/** Banco de Cordoba = AB */
public static final String BANK_BancoDeCordoba = "AB";
/** Banco del Uruguay = BU */
public static final String BANK_BancoDelUruguay = "BU";
/** Banco de San Juan = AJ */
public static final String BANK_BancoDeSanJuan = "AJ";
/** Banco de Santa Cruz = AS */
public static final String BANK_BancoDeSantaCruz = "AS";
/** Banco de Sgo. Del Estero = BE */
public static final String BANK_BancoDeSgoDelEstero = "BE";
/** Banco del Chubut = AH */
public static final String BANK_BancoDelChubut = "AH";
/** Banco del Sol = SO */
public static final String BANK_BancoDelSol = "SO";
/** Banco de Tucuman = AT */
public static final String BANK_BancoDeTucuman = "AT";
/** Banco do Brasil = BL */
public static final String BANK_BancoDoBrasil = "BL";
/** Finansur = FI */
public static final String BANK_Finansur = "FI";
/** Industrial = IN */
public static final String BANK_Industrial = "IN";
/** Mariva = MA */
public static final String BANK_Mariva = "MA";
/** Meridian = ME */
public static final String BANK_Meridian = "ME";
/** Banco de Rosario = RO */
public static final String BANK_BancoDeRosario = "RO";
/** Patagonia = PA */
public static final String BANK_Patagonia = "PA";
/** Piano = PI */
public static final String BANK_Piano = "PI";
/** Banco de Tierra del Fuego = TF */
public static final String BANK_BancoDeTierraDelFuego = "TF";
/** Banco de Neuquen = AN */
public static final String BANK_BancoDeNeuquen = "AN";
/** Roela = RB */
public static final String BANK_Roela = "RB";
/** Saenz = SA */
public static final String BANK_Saenz = "SA";
/** Supervielle = SU */
public static final String BANK_Supervielle = "SU";
/** Bank of America = AM */
public static final String BANK_BankOfAmerica = "AM";
/** Deutsche = DB */
public static final String BANK_Deutsche = "DB";
/** Banco de Entre Rios = ER */
public static final String BANK_BancoDeEntreRios = "ER";
/** Banco de La Rioja = RI */
public static final String BANK_BancoDeLaRioja = "RI";
/** Banco de Santa Fe = SF */
public static final String BANK_BancoDeSantaFe = "SF";
/** Banco del Chaco = CH */
public static final String BANK_BancoDelChaco = "CH";
/** RCI = RC */
public static final String BANK_RCI = "RC";
/** Royal = RY */
public static final String BANK_Royal = "RY";
/** HSBC = HS */
public static final String BANK_HSBC = "HS";
/** Paribas = PB */
public static final String BANK_Paribas = "PB";
/** Set Bank.
Bank */
public void setBank (String Bank)
{
if (Bank == null || Bank.equals("O") || Bank.equals("BN") || Bank.equals("BF") || Bank.equals("BG") || Bank.equals("BH") || Bank.equals("BS") || Bank.equals("BP") || Bank.equals("BC") || Bank.equals("CO") || Bank.equals("CR") || Bank.equals("BI") || Bank.equals("BM") || Bank.equals("CB") || Bank.equals("SB") || Bank.equals("AE") || Bank.equals("CS") || Bank.equals("BB") || Bank.equals("BR") || Bank.equals("CE") || Bank.equals("CM") || Bank.equals("CI") || Bank.equals("AC") || Bank.equals("AF") || Bank.equals("AP") || Bank.equals("AB") || Bank.equals("BU") || Bank.equals("AJ") || Bank.equals("AS") || Bank.equals("BE") || Bank.equals("AH") || Bank.equals("SO") || Bank.equals("AT") || Bank.equals("BL") || Bank.equals("FI") || Bank.equals("IN") || Bank.equals("MA") || Bank.equals("ME") || Bank.equals("RO") || Bank.equals("PA") || Bank.equals("PI") || Bank.equals("TF") || Bank.equals("AN") || Bank.equals("RB") || Bank.equals("SA") || Bank.equals("SU") || Bank.equals("AM") || Bank.equals("DB") || Bank.equals("ER") || Bank.equals("RI") || Bank.equals("SF") || Bank.equals("CH") || Bank.equals("RC") || Bank.equals("RY") || Bank.equals("HS") || Bank.equals("PB"));
 else throw new IllegalArgumentException ("Bank Invalid value - Reference = BANK_AD_Reference_ID - O - BN - BF - BG - BH - BS - BP - BC - CO - CR - BI - BM - CB - SB - AE - CS - BB - BR - CE - CM - CI - AC - AF - AP - AB - BU - AJ - AS - BE - AH - SO - AT - BL - FI - IN - MA - ME - RO - PA - PI - TF - AN - RB - SA - SU - AM - DB - ER - RI - SF - CH - RC - RY - HS - PB");
if (Bank != null && Bank.length() > 2)
{
log.warning("Length > 2 - truncated");
Bank = Bank.substring(0,2);
}
set_Value ("Bank", Bank);
}
/** Get Bank.
Bank */
public String getBank() 
{
return (String)get_Value("Bank");
}
public static final int BEFORECHECKDEADLINEFROM_AD_Reference_ID = MReference.getReferenceID("C_POSPaymentMedium Check Dead Line");
/** 090 = 90 */
public static final String BEFORECHECKDEADLINEFROM_090 = "90";
/** Today = 0 */
public static final String BEFORECHECKDEADLINEFROM_Today = "0";
/** 060 = 60 */
public static final String BEFORECHECKDEADLINEFROM_060 = "60";
/** 030 = 30 */
public static final String BEFORECHECKDEADLINEFROM_030 = "30";
/** No deadline = 99999 */
public static final String BEFORECHECKDEADLINEFROM_NoDeadline = "99999";
/** 120 = 120 */
public static final String BEFORECHECKDEADLINEFROM_120 = "120";
/** 150 = 150 */
public static final String BEFORECHECKDEADLINEFROM_150 = "150";
/** 180 = 180 */
public static final String BEFORECHECKDEADLINEFROM_180 = "180";
/** 210 = 210 */
public static final String BEFORECHECKDEADLINEFROM_210 = "210";
/** 240 = 240 */
public static final String BEFORECHECKDEADLINEFROM_240 = "240";
/** 270 = 270 */
public static final String BEFORECHECKDEADLINEFROM_270 = "270";
/** 300 = 300 */
public static final String BEFORECHECKDEADLINEFROM_300 = "300";
/** 330 = 330 */
public static final String BEFORECHECKDEADLINEFROM_330 = "330";
/** 360 = 360 */
public static final String BEFORECHECKDEADLINEFROM_360 = "360";
/** 390 = 390 */
public static final String BEFORECHECKDEADLINEFROM_390 = "390";
/** 420 = 420 */
public static final String BEFORECHECKDEADLINEFROM_420 = "420";
/** 450 = 450 */
public static final String BEFORECHECKDEADLINEFROM_450 = "450";
/** 480 = 480 */
public static final String BEFORECHECKDEADLINEFROM_480 = "480";
/** 510 = 510 */
public static final String BEFORECHECKDEADLINEFROM_510 = "510";
/** 540 = 540 */
public static final String BEFORECHECKDEADLINEFROM_540 = "540";
/** 570 = 570 */
public static final String BEFORECHECKDEADLINEFROM_570 = "570";
/** 600 = 600 */
public static final String BEFORECHECKDEADLINEFROM_600 = "600";
/** 630 = 630 */
public static final String BEFORECHECKDEADLINEFROM_630 = "630";
/** 660 = 660 */
public static final String BEFORECHECKDEADLINEFROM_660 = "660";
/** 690 = 690 */
public static final String BEFORECHECKDEADLINEFROM_690 = "690";
/** 720 = 720 */
public static final String BEFORECHECKDEADLINEFROM_720 = "720";
/** 015 = 15 */
public static final String BEFORECHECKDEADLINEFROM_015 = "15";
/** 045 = 45 */
public static final String BEFORECHECKDEADLINEFROM_045 = "45";
/** 075 = 75 */
public static final String BEFORECHECKDEADLINEFROM_075 = "75";
/** 105 = 105 */
public static final String BEFORECHECKDEADLINEFROM_105 = "105";
/** 135 = 135 */
public static final String BEFORECHECKDEADLINEFROM_135 = "135";
/** 165 = 165 */
public static final String BEFORECHECKDEADLINEFROM_165 = "165";
/** 195 = 195 */
public static final String BEFORECHECKDEADLINEFROM_195 = "195";
/** 225 = 225 */
public static final String BEFORECHECKDEADLINEFROM_225 = "225";
/** 255 = 255 */
public static final String BEFORECHECKDEADLINEFROM_255 = "255";
/** 285 = 285 */
public static final String BEFORECHECKDEADLINEFROM_285 = "285";
/** 315 = 315 */
public static final String BEFORECHECKDEADLINEFROM_315 = "315";
/** 345 = 345 */
public static final String BEFORECHECKDEADLINEFROM_345 = "345";
/** 375 = 375 */
public static final String BEFORECHECKDEADLINEFROM_375 = "375";
/** 405 = 405 */
public static final String BEFORECHECKDEADLINEFROM_405 = "405";
/** 435 = 435 */
public static final String BEFORECHECKDEADLINEFROM_435 = "435";
/** 465 = 465 */
public static final String BEFORECHECKDEADLINEFROM_465 = "465";
/** 495 = 495 */
public static final String BEFORECHECKDEADLINEFROM_495 = "495";
/** 525 = 525 */
public static final String BEFORECHECKDEADLINEFROM_525 = "525";
/** 555 = 555 */
public static final String BEFORECHECKDEADLINEFROM_555 = "555";
/** 585 = 585 */
public static final String BEFORECHECKDEADLINEFROM_585 = "585";
/** 615 = 615 */
public static final String BEFORECHECKDEADLINEFROM_615 = "615";
/** 645 = 645 */
public static final String BEFORECHECKDEADLINEFROM_645 = "645";
/** 675 = 675 */
public static final String BEFORECHECKDEADLINEFROM_675 = "675";
/** 705 = 705 */
public static final String BEFORECHECKDEADLINEFROM_705 = "705";
/** Set Before Check DeadLine From */
public void setBeforeCheckDeadLineFrom (String BeforeCheckDeadLineFrom)
{
if (BeforeCheckDeadLineFrom.equals("90") || BeforeCheckDeadLineFrom.equals("0") || BeforeCheckDeadLineFrom.equals("60") || BeforeCheckDeadLineFrom.equals("30") || BeforeCheckDeadLineFrom.equals("99999") || BeforeCheckDeadLineFrom.equals("120") || BeforeCheckDeadLineFrom.equals("150") || BeforeCheckDeadLineFrom.equals("180") || BeforeCheckDeadLineFrom.equals("210") || BeforeCheckDeadLineFrom.equals("240") || BeforeCheckDeadLineFrom.equals("270") || BeforeCheckDeadLineFrom.equals("300") || BeforeCheckDeadLineFrom.equals("330") || BeforeCheckDeadLineFrom.equals("360") || BeforeCheckDeadLineFrom.equals("390") || BeforeCheckDeadLineFrom.equals("420") || BeforeCheckDeadLineFrom.equals("450") || BeforeCheckDeadLineFrom.equals("480") || BeforeCheckDeadLineFrom.equals("510") || BeforeCheckDeadLineFrom.equals("540") || BeforeCheckDeadLineFrom.equals("570") || BeforeCheckDeadLineFrom.equals("600") || BeforeCheckDeadLineFrom.equals("630") || BeforeCheckDeadLineFrom.equals("660") || BeforeCheckDeadLineFrom.equals("690") || BeforeCheckDeadLineFrom.equals("720") || BeforeCheckDeadLineFrom.equals("15") || BeforeCheckDeadLineFrom.equals("45") || BeforeCheckDeadLineFrom.equals("75") || BeforeCheckDeadLineFrom.equals("105") || BeforeCheckDeadLineFrom.equals("135") || BeforeCheckDeadLineFrom.equals("165") || BeforeCheckDeadLineFrom.equals("195") || BeforeCheckDeadLineFrom.equals("225") || BeforeCheckDeadLineFrom.equals("255") || BeforeCheckDeadLineFrom.equals("285") || BeforeCheckDeadLineFrom.equals("315") || BeforeCheckDeadLineFrom.equals("345") || BeforeCheckDeadLineFrom.equals("375") || BeforeCheckDeadLineFrom.equals("405") || BeforeCheckDeadLineFrom.equals("435") || BeforeCheckDeadLineFrom.equals("465") || BeforeCheckDeadLineFrom.equals("495") || BeforeCheckDeadLineFrom.equals("525") || BeforeCheckDeadLineFrom.equals("555") || BeforeCheckDeadLineFrom.equals("585") || BeforeCheckDeadLineFrom.equals("615") || BeforeCheckDeadLineFrom.equals("645") || BeforeCheckDeadLineFrom.equals("675") || BeforeCheckDeadLineFrom.equals("705"));
 else throw new IllegalArgumentException ("BeforeCheckDeadLineFrom Invalid value - Reference = BEFORECHECKDEADLINEFROM_AD_Reference_ID - 90 - 0 - 60 - 30 - 99999 - 120 - 150 - 180 - 210 - 240 - 270 - 300 - 330 - 360 - 390 - 420 - 450 - 480 - 510 - 540 - 570 - 600 - 630 - 660 - 690 - 720 - 15 - 45 - 75 - 105 - 135 - 165 - 195 - 225 - 255 - 285 - 315 - 345 - 375 - 405 - 435 - 465 - 495 - 525 - 555 - 585 - 615 - 645 - 675 - 705");
if (BeforeCheckDeadLineFrom == null) throw new IllegalArgumentException ("BeforeCheckDeadLineFrom is mandatory");
if (BeforeCheckDeadLineFrom.length() > 10)
{
log.warning("Length > 10 - truncated");
BeforeCheckDeadLineFrom = BeforeCheckDeadLineFrom.substring(0,10);
}
set_Value ("BeforeCheckDeadLineFrom", BeforeCheckDeadLineFrom);
}
/** Get Before Check DeadLine From */
public String getBeforeCheckDeadLineFrom() 
{
return (String)get_Value("BeforeCheckDeadLineFrom");
}
public static final int BEFORECHECKDEADLINETO_AD_Reference_ID = MReference.getReferenceID("C_POSPaymentMedium Check Dead Line");
/** 090 = 90 */
public static final String BEFORECHECKDEADLINETO_090 = "90";
/** Today = 0 */
public static final String BEFORECHECKDEADLINETO_Today = "0";
/** 060 = 60 */
public static final String BEFORECHECKDEADLINETO_060 = "60";
/** 030 = 30 */
public static final String BEFORECHECKDEADLINETO_030 = "30";
/** No deadline = 99999 */
public static final String BEFORECHECKDEADLINETO_NoDeadline = "99999";
/** 120 = 120 */
public static final String BEFORECHECKDEADLINETO_120 = "120";
/** 150 = 150 */
public static final String BEFORECHECKDEADLINETO_150 = "150";
/** 180 = 180 */
public static final String BEFORECHECKDEADLINETO_180 = "180";
/** 210 = 210 */
public static final String BEFORECHECKDEADLINETO_210 = "210";
/** 240 = 240 */
public static final String BEFORECHECKDEADLINETO_240 = "240";
/** 270 = 270 */
public static final String BEFORECHECKDEADLINETO_270 = "270";
/** 300 = 300 */
public static final String BEFORECHECKDEADLINETO_300 = "300";
/** 330 = 330 */
public static final String BEFORECHECKDEADLINETO_330 = "330";
/** 360 = 360 */
public static final String BEFORECHECKDEADLINETO_360 = "360";
/** 390 = 390 */
public static final String BEFORECHECKDEADLINETO_390 = "390";
/** 420 = 420 */
public static final String BEFORECHECKDEADLINETO_420 = "420";
/** 450 = 450 */
public static final String BEFORECHECKDEADLINETO_450 = "450";
/** 480 = 480 */
public static final String BEFORECHECKDEADLINETO_480 = "480";
/** 510 = 510 */
public static final String BEFORECHECKDEADLINETO_510 = "510";
/** 540 = 540 */
public static final String BEFORECHECKDEADLINETO_540 = "540";
/** 570 = 570 */
public static final String BEFORECHECKDEADLINETO_570 = "570";
/** 600 = 600 */
public static final String BEFORECHECKDEADLINETO_600 = "600";
/** 630 = 630 */
public static final String BEFORECHECKDEADLINETO_630 = "630";
/** 660 = 660 */
public static final String BEFORECHECKDEADLINETO_660 = "660";
/** 690 = 690 */
public static final String BEFORECHECKDEADLINETO_690 = "690";
/** 720 = 720 */
public static final String BEFORECHECKDEADLINETO_720 = "720";
/** 015 = 15 */
public static final String BEFORECHECKDEADLINETO_015 = "15";
/** 045 = 45 */
public static final String BEFORECHECKDEADLINETO_045 = "45";
/** 075 = 75 */
public static final String BEFORECHECKDEADLINETO_075 = "75";
/** 105 = 105 */
public static final String BEFORECHECKDEADLINETO_105 = "105";
/** 135 = 135 */
public static final String BEFORECHECKDEADLINETO_135 = "135";
/** 165 = 165 */
public static final String BEFORECHECKDEADLINETO_165 = "165";
/** 195 = 195 */
public static final String BEFORECHECKDEADLINETO_195 = "195";
/** 225 = 225 */
public static final String BEFORECHECKDEADLINETO_225 = "225";
/** 255 = 255 */
public static final String BEFORECHECKDEADLINETO_255 = "255";
/** 285 = 285 */
public static final String BEFORECHECKDEADLINETO_285 = "285";
/** 315 = 315 */
public static final String BEFORECHECKDEADLINETO_315 = "315";
/** 345 = 345 */
public static final String BEFORECHECKDEADLINETO_345 = "345";
/** 375 = 375 */
public static final String BEFORECHECKDEADLINETO_375 = "375";
/** 405 = 405 */
public static final String BEFORECHECKDEADLINETO_405 = "405";
/** 435 = 435 */
public static final String BEFORECHECKDEADLINETO_435 = "435";
/** 465 = 465 */
public static final String BEFORECHECKDEADLINETO_465 = "465";
/** 495 = 495 */
public static final String BEFORECHECKDEADLINETO_495 = "495";
/** 525 = 525 */
public static final String BEFORECHECKDEADLINETO_525 = "525";
/** 555 = 555 */
public static final String BEFORECHECKDEADLINETO_555 = "555";
/** 585 = 585 */
public static final String BEFORECHECKDEADLINETO_585 = "585";
/** 615 = 615 */
public static final String BEFORECHECKDEADLINETO_615 = "615";
/** 645 = 645 */
public static final String BEFORECHECKDEADLINETO_645 = "645";
/** 675 = 675 */
public static final String BEFORECHECKDEADLINETO_675 = "675";
/** 705 = 705 */
public static final String BEFORECHECKDEADLINETO_705 = "705";
/** Set Before Check DeadLine To */
public void setBeforeCheckDeadLineTo (String BeforeCheckDeadLineTo)
{
if (BeforeCheckDeadLineTo == null || BeforeCheckDeadLineTo.equals("90") || BeforeCheckDeadLineTo.equals("0") || BeforeCheckDeadLineTo.equals("60") || BeforeCheckDeadLineTo.equals("30") || BeforeCheckDeadLineTo.equals("99999") || BeforeCheckDeadLineTo.equals("120") || BeforeCheckDeadLineTo.equals("150") || BeforeCheckDeadLineTo.equals("180") || BeforeCheckDeadLineTo.equals("210") || BeforeCheckDeadLineTo.equals("240") || BeforeCheckDeadLineTo.equals("270") || BeforeCheckDeadLineTo.equals("300") || BeforeCheckDeadLineTo.equals("330") || BeforeCheckDeadLineTo.equals("360") || BeforeCheckDeadLineTo.equals("390") || BeforeCheckDeadLineTo.equals("420") || BeforeCheckDeadLineTo.equals("450") || BeforeCheckDeadLineTo.equals("480") || BeforeCheckDeadLineTo.equals("510") || BeforeCheckDeadLineTo.equals("540") || BeforeCheckDeadLineTo.equals("570") || BeforeCheckDeadLineTo.equals("600") || BeforeCheckDeadLineTo.equals("630") || BeforeCheckDeadLineTo.equals("660") || BeforeCheckDeadLineTo.equals("690") || BeforeCheckDeadLineTo.equals("720") || BeforeCheckDeadLineTo.equals("15") || BeforeCheckDeadLineTo.equals("45") || BeforeCheckDeadLineTo.equals("75") || BeforeCheckDeadLineTo.equals("105") || BeforeCheckDeadLineTo.equals("135") || BeforeCheckDeadLineTo.equals("165") || BeforeCheckDeadLineTo.equals("195") || BeforeCheckDeadLineTo.equals("225") || BeforeCheckDeadLineTo.equals("255") || BeforeCheckDeadLineTo.equals("285") || BeforeCheckDeadLineTo.equals("315") || BeforeCheckDeadLineTo.equals("345") || BeforeCheckDeadLineTo.equals("375") || BeforeCheckDeadLineTo.equals("405") || BeforeCheckDeadLineTo.equals("435") || BeforeCheckDeadLineTo.equals("465") || BeforeCheckDeadLineTo.equals("495") || BeforeCheckDeadLineTo.equals("525") || BeforeCheckDeadLineTo.equals("555") || BeforeCheckDeadLineTo.equals("585") || BeforeCheckDeadLineTo.equals("615") || BeforeCheckDeadLineTo.equals("645") || BeforeCheckDeadLineTo.equals("675") || BeforeCheckDeadLineTo.equals("705"));
 else throw new IllegalArgumentException ("BeforeCheckDeadLineTo Invalid value - Reference = BEFORECHECKDEADLINETO_AD_Reference_ID - 90 - 0 - 60 - 30 - 99999 - 120 - 150 - 180 - 210 - 240 - 270 - 300 - 330 - 360 - 390 - 420 - 450 - 480 - 510 - 540 - 570 - 600 - 630 - 660 - 690 - 720 - 15 - 45 - 75 - 105 - 135 - 165 - 195 - 225 - 255 - 285 - 315 - 345 - 375 - 405 - 435 - 465 - 495 - 525 - 555 - 585 - 615 - 645 - 675 - 705");
if (BeforeCheckDeadLineTo != null && BeforeCheckDeadLineTo.length() > 10)
{
log.warning("Length > 10 - truncated");
BeforeCheckDeadLineTo = BeforeCheckDeadLineTo.substring(0,10);
}
set_Value ("BeforeCheckDeadLineTo", BeforeCheckDeadLineTo);
}
/** Get Before Check DeadLine To */
public String getBeforeCheckDeadLineTo() 
{
return (String)get_Value("BeforeCheckDeadLineTo");
}
/** Set Bank.
Bank */
public void setC_Bank_ID (int C_Bank_ID)
{
if (C_Bank_ID <= 0) set_Value ("C_Bank_ID", null);
 else 
set_Value ("C_Bank_ID", new Integer(C_Bank_ID));
}
/** Get Bank.
Bank */
public int getC_Bank_ID() 
{
Integer ii = (Integer)get_Value("C_Bank_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CHECKDEADLINE_AD_Reference_ID = MReference.getReferenceID("C_POSPaymentMedium Check Dead Line");
/** 090 = 90 */
public static final String CHECKDEADLINE_090 = "90";
/** Today = 0 */
public static final String CHECKDEADLINE_Today = "0";
/** 060 = 60 */
public static final String CHECKDEADLINE_060 = "60";
/** 030 = 30 */
public static final String CHECKDEADLINE_030 = "30";
/** No deadline = 99999 */
public static final String CHECKDEADLINE_NoDeadline = "99999";
/** 120 = 120 */
public static final String CHECKDEADLINE_120 = "120";
/** 150 = 150 */
public static final String CHECKDEADLINE_150 = "150";
/** 180 = 180 */
public static final String CHECKDEADLINE_180 = "180";
/** 210 = 210 */
public static final String CHECKDEADLINE_210 = "210";
/** 240 = 240 */
public static final String CHECKDEADLINE_240 = "240";
/** 270 = 270 */
public static final String CHECKDEADLINE_270 = "270";
/** 300 = 300 */
public static final String CHECKDEADLINE_300 = "300";
/** 330 = 330 */
public static final String CHECKDEADLINE_330 = "330";
/** 360 = 360 */
public static final String CHECKDEADLINE_360 = "360";
/** 390 = 390 */
public static final String CHECKDEADLINE_390 = "390";
/** 420 = 420 */
public static final String CHECKDEADLINE_420 = "420";
/** 450 = 450 */
public static final String CHECKDEADLINE_450 = "450";
/** 480 = 480 */
public static final String CHECKDEADLINE_480 = "480";
/** 510 = 510 */
public static final String CHECKDEADLINE_510 = "510";
/** 540 = 540 */
public static final String CHECKDEADLINE_540 = "540";
/** 570 = 570 */
public static final String CHECKDEADLINE_570 = "570";
/** 600 = 600 */
public static final String CHECKDEADLINE_600 = "600";
/** 630 = 630 */
public static final String CHECKDEADLINE_630 = "630";
/** 660 = 660 */
public static final String CHECKDEADLINE_660 = "660";
/** 690 = 690 */
public static final String CHECKDEADLINE_690 = "690";
/** 720 = 720 */
public static final String CHECKDEADLINE_720 = "720";
/** 015 = 15 */
public static final String CHECKDEADLINE_015 = "15";
/** 045 = 45 */
public static final String CHECKDEADLINE_045 = "45";
/** 075 = 75 */
public static final String CHECKDEADLINE_075 = "75";
/** 105 = 105 */
public static final String CHECKDEADLINE_105 = "105";
/** 135 = 135 */
public static final String CHECKDEADLINE_135 = "135";
/** 165 = 165 */
public static final String CHECKDEADLINE_165 = "165";
/** 195 = 195 */
public static final String CHECKDEADLINE_195 = "195";
/** 225 = 225 */
public static final String CHECKDEADLINE_225 = "225";
/** 255 = 255 */
public static final String CHECKDEADLINE_255 = "255";
/** 285 = 285 */
public static final String CHECKDEADLINE_285 = "285";
/** 315 = 315 */
public static final String CHECKDEADLINE_315 = "315";
/** 345 = 345 */
public static final String CHECKDEADLINE_345 = "345";
/** 375 = 375 */
public static final String CHECKDEADLINE_375 = "375";
/** 405 = 405 */
public static final String CHECKDEADLINE_405 = "405";
/** 435 = 435 */
public static final String CHECKDEADLINE_435 = "435";
/** 465 = 465 */
public static final String CHECKDEADLINE_465 = "465";
/** 495 = 495 */
public static final String CHECKDEADLINE_495 = "495";
/** 525 = 525 */
public static final String CHECKDEADLINE_525 = "525";
/** 555 = 555 */
public static final String CHECKDEADLINE_555 = "555";
/** 585 = 585 */
public static final String CHECKDEADLINE_585 = "585";
/** 615 = 615 */
public static final String CHECKDEADLINE_615 = "615";
/** 645 = 645 */
public static final String CHECKDEADLINE_645 = "645";
/** 675 = 675 */
public static final String CHECKDEADLINE_675 = "675";
/** 705 = 705 */
public static final String CHECKDEADLINE_705 = "705";
/** Set Check Dead Line.
Check Dead Line */
public void setCheckDeadLine (String CheckDeadLine)
{
if (CheckDeadLine.equals("90") || CheckDeadLine.equals("0") || CheckDeadLine.equals("60") || CheckDeadLine.equals("30") || CheckDeadLine.equals("99999") || CheckDeadLine.equals("120") || CheckDeadLine.equals("150") || CheckDeadLine.equals("180") || CheckDeadLine.equals("210") || CheckDeadLine.equals("240") || CheckDeadLine.equals("270") || CheckDeadLine.equals("300") || CheckDeadLine.equals("330") || CheckDeadLine.equals("360") || CheckDeadLine.equals("390") || CheckDeadLine.equals("420") || CheckDeadLine.equals("450") || CheckDeadLine.equals("480") || CheckDeadLine.equals("510") || CheckDeadLine.equals("540") || CheckDeadLine.equals("570") || CheckDeadLine.equals("600") || CheckDeadLine.equals("630") || CheckDeadLine.equals("660") || CheckDeadLine.equals("690") || CheckDeadLine.equals("720") || CheckDeadLine.equals("15") || CheckDeadLine.equals("45") || CheckDeadLine.equals("75") || CheckDeadLine.equals("105") || CheckDeadLine.equals("135") || CheckDeadLine.equals("165") || CheckDeadLine.equals("195") || CheckDeadLine.equals("225") || CheckDeadLine.equals("255") || CheckDeadLine.equals("285") || CheckDeadLine.equals("315") || CheckDeadLine.equals("345") || CheckDeadLine.equals("375") || CheckDeadLine.equals("405") || CheckDeadLine.equals("435") || CheckDeadLine.equals("465") || CheckDeadLine.equals("495") || CheckDeadLine.equals("525") || CheckDeadLine.equals("555") || CheckDeadLine.equals("585") || CheckDeadLine.equals("615") || CheckDeadLine.equals("645") || CheckDeadLine.equals("675") || CheckDeadLine.equals("705"));
 else throw new IllegalArgumentException ("CheckDeadLine Invalid value - Reference = CHECKDEADLINE_AD_Reference_ID - 90 - 0 - 60 - 30 - 99999 - 120 - 150 - 180 - 210 - 240 - 270 - 300 - 330 - 360 - 390 - 420 - 450 - 480 - 510 - 540 - 570 - 600 - 630 - 660 - 690 - 720 - 15 - 45 - 75 - 105 - 135 - 165 - 195 - 225 - 255 - 285 - 315 - 345 - 375 - 405 - 435 - 465 - 495 - 525 - 555 - 585 - 615 - 645 - 675 - 705");
if (CheckDeadLine == null) throw new IllegalArgumentException ("CheckDeadLine is mandatory");
if (CheckDeadLine.length() > 5)
{
log.warning("Length > 5 - truncated");
CheckDeadLine = CheckDeadLine.substring(0,5);
}
set_Value ("CheckDeadLine", CheckDeadLine);
}
/** Get Check Dead Line.
Check Dead Line */
public String getCheckDeadLine() 
{
return (String)get_Value("CheckDeadLine");
}
public static final int CONTEXT_AD_Reference_ID = MReference.getReferenceID("Payment medium context");
/** Both = B */
public static final String CONTEXT_Both = "B";
/** POS Only = P */
public static final String CONTEXT_POSOnly = "P";
/** Customer Receipts Only = R */
public static final String CONTEXT_CustomerReceiptsOnly = "R";
/** Set Payment Medium Context */
public void setContext (String Context)
{
if (Context.equals("B") || Context.equals("P") || Context.equals("R"));
 else throw new IllegalArgumentException ("Context Invalid value - Reference = CONTEXT_AD_Reference_ID - B - P - R");
if (Context == null) throw new IllegalArgumentException ("Context is mandatory");
if (Context.length() > 1)
{
log.warning("Length > 1 - truncated");
Context = Context.substring(0,1);
}
set_Value ("Context", Context);
}
/** Get Payment Medium Context */
public String getContext() 
{
return (String)get_Value("Context");
}
/** Set POS Payment Medium.
POS Terminal Payment Medium */
public void setC_POSPaymentMedium_ID (int C_POSPaymentMedium_ID)
{
set_ValueNoCheck ("C_POSPaymentMedium_ID", new Integer(C_POSPaymentMedium_ID));
}
/** Get POS Payment Medium.
POS Terminal Payment Medium */
public int getC_POSPaymentMedium_ID() 
{
Integer ii = (Integer)get_Value("C_POSPaymentMedium_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Date From.
Starting date for a range */
public void setDateFrom (Timestamp DateFrom)
{
if (DateFrom == null) throw new IllegalArgumentException ("DateFrom is mandatory");
set_Value ("DateFrom", DateFrom);
}
/** Get Date From.
Starting date for a range */
public Timestamp getDateFrom() 
{
return (Timestamp)get_Value("DateFrom");
}
/** Set Date To.
End date of a date range */
public void setDateTo (Timestamp DateTo)
{
if (DateTo == null) throw new IllegalArgumentException ("DateTo is mandatory");
set_Value ("DateTo", DateTo);
}
/** Get Date To.
End date of a date range */
public Timestamp getDateTo() 
{
return (Timestamp)get_Value("DateTo");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Is Mandatory Bank */
public void setIsMandatoryBank (boolean IsMandatoryBank)
{
set_Value ("IsMandatoryBank", new Boolean(IsMandatoryBank));
}
/** Get Is Mandatory Bank */
public boolean isMandatoryBank() 
{
Object oo = get_Value("IsMandatoryBank");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Normalized Bank */
public void setIsNormalizedBank (boolean IsNormalizedBank)
{
set_Value ("IsNormalizedBank", new Boolean(IsNormalizedBank));
}
/** Get Is Normalized Bank */
public boolean isNormalizedBank() 
{
Object oo = get_Value("IsNormalizedBank");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Discount Schema.
Schema to calculate the trade discount percentage */
public void setM_DiscountSchema_ID (int M_DiscountSchema_ID)
{
if (M_DiscountSchema_ID <= 0) set_Value ("M_DiscountSchema_ID", null);
 else 
set_Value ("M_DiscountSchema_ID", new Integer(M_DiscountSchema_ID));
}
/** Get Discount Schema.
Schema to calculate the trade discount percentage */
public int getM_DiscountSchema_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Entidad Financiera */
public void setM_EntidadFinanciera_ID (int M_EntidadFinanciera_ID)
{
set_Value ("M_EntidadFinanciera_ID", new Integer(M_EntidadFinanciera_ID));
}
/** Get Entidad Financiera */
public int getM_EntidadFinanciera_ID() 
{
Integer ii = (Integer)get_Value("M_EntidadFinanciera_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
public static final int TENDERTYPE_AD_Reference_ID = MReference.getReferenceID("C_POSPaymentMedium Tender Type");
/** Direct Deposit = A */
public static final String TENDERTYPE_DirectDeposit = "A";
/** Credit Card = C */
public static final String TENDERTYPE_CreditCard = "C";
/** Cash = CA */
public static final String TENDERTYPE_Cash = "CA";
/** Check = K */
public static final String TENDERTYPE_Check = "K";
/** Credit Note = N */
public static final String TENDERTYPE_CreditNote = "N";
/** Credit = CR */
public static final String TENDERTYPE_Credit = "CR";
/** Retencion = RE */
public static final String TENDERTYPE_Retencion = "RE";
/** Advance Receipt = AC */
public static final String TENDERTYPE_AdvanceReceipt = "AC";
/** Set Tender type.
Method of Payment */
public void setTenderType (String TenderType)
{
if (TenderType.equals("A") || TenderType.equals("C") || TenderType.equals("CA") || TenderType.equals("K") || TenderType.equals("N") || TenderType.equals("CR") || TenderType.equals("RE") || TenderType.equals("AC"));
 else throw new IllegalArgumentException ("TenderType Invalid value - Reference = TENDERTYPE_AD_Reference_ID - A - C - CA - K - N - CR - RE - AC");
if (TenderType == null) throw new IllegalArgumentException ("TenderType is mandatory");
if (TenderType.length() > 2)
{
log.warning("Length > 2 - truncated");
TenderType = TenderType.substring(0,2);
}
set_Value ("TenderType", TenderType);
}
/** Get Tender type.
Method of Payment */
public String getTenderType() 
{
return (String)get_Value("TenderType");
}
/** Set Validate Before CheckDead Lines */
public void setValidateBeforeCheckDeadLines (boolean ValidateBeforeCheckDeadLines)
{
set_Value ("ValidateBeforeCheckDeadLines", new Boolean(ValidateBeforeCheckDeadLines));
}
/** Get Validate Before CheckDead Lines */
public boolean isValidateBeforeCheckDeadLines() 
{
Object oo = get_Value("ValidateBeforeCheckDeadLines");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
