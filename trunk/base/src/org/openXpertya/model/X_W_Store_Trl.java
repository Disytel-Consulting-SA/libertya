/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por W_Store_Trl
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:39.193 */
public class X_W_Store_Trl extends PO
{
/** Constructor estándar */
public X_W_Store_Trl (Properties ctx, int W_Store_Trl_ID, String trxName)
{
super (ctx, W_Store_Trl_ID, trxName);
/** if (W_Store_Trl_ID == 0)
{
setAD_Language (null);
setIsTranslated (false);
setWebInfo (null);
setWebParam1 (null);
setWebParam2 (null);
setWebParam3 (null);
setWebParam4 (null);
setWebParam5 (null);
setWebParam6 (null);
setW_Store_ID (0);
}
 */
}
/** Load Constructor */
public X_W_Store_Trl (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=779 */
public static final int Table_ID=779;

/** TableName=W_Store_Trl */
public static final String Table_Name="W_Store_Trl";

protected static KeyNamePair Model = new KeyNamePair(779,"W_Store_Trl");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_W_Store_Trl[").append(getID()).append("]");
return sb.toString();
}
/** Set Language.
Language for this entity */
public void setAD_Language (String AD_Language)
{
if (AD_Language == null) throw new IllegalArgumentException ("AD_Language is mandatory");
if (AD_Language.length() > 6)
{
log.warning("Length > 6 - truncated");
AD_Language = AD_Language.substring(0,6);
}
set_ValueNoCheck ("AD_Language", AD_Language);
}
/** Get Language.
Language for this entity */
public String getAD_Language() 
{
return (String)get_Value("AD_Language");
}
/** Set EMail Footer.
Footer added to EMails */
public void setEMailFooter (String EMailFooter)
{
if (EMailFooter != null && EMailFooter.length() > 2000)
{
log.warning("Length > 2000 - truncated");
EMailFooter = EMailFooter.substring(0,2000);
}
set_Value ("EMailFooter", EMailFooter);
}
/** Get EMail Footer.
Footer added to EMails */
public String getEMailFooter() 
{
return (String)get_Value("EMailFooter");
}
/** Set EMail Header.
Header added to EMails */
public void setEMailHeader (String EMailHeader)
{
if (EMailHeader != null && EMailHeader.length() > 2000)
{
log.warning("Length > 2000 - truncated");
EMailHeader = EMailHeader.substring(0,2000);
}
set_Value ("EMailHeader", EMailHeader);
}
/** Get EMail Header.
Header added to EMails */
public String getEMailHeader() 
{
return (String)get_Value("EMailHeader");
}
/** Set Translated.
This column is translated */
public void setIsTranslated (boolean IsTranslated)
{
set_Value ("IsTranslated", new Boolean(IsTranslated));
}
/** Get Translated.
This column is translated */
public boolean isTranslated() 
{
Object oo = get_Value("IsTranslated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Web Store Info.
Web Store Header Information */
public void setWebInfo (String WebInfo)
{
if (WebInfo == null) throw new IllegalArgumentException ("WebInfo is mandatory");
if (WebInfo.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebInfo = WebInfo.substring(0,2000);
}
set_Value ("WebInfo", WebInfo);
}
/** Get Web Store Info.
Web Store Header Information */
public String getWebInfo() 
{
return (String)get_Value("WebInfo");
}
/** Set Web Parameter 1.
Web Site Parameter 1 (default: header image) */
public void setWebParam1 (String WebParam1)
{
if (WebParam1 == null) throw new IllegalArgumentException ("WebParam1 is mandatory");
if (WebParam1.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam1 = WebParam1.substring(0,2000);
}
set_Value ("WebParam1", WebParam1);
}
/** Get Web Parameter 1.
Web Site Parameter 1 (default: header image) */
public String getWebParam1() 
{
return (String)get_Value("WebParam1");
}
/** Set Web Parameter 2.
Web Site Parameter 2 (default index page) */
public void setWebParam2 (String WebParam2)
{
if (WebParam2 == null) throw new IllegalArgumentException ("WebParam2 is mandatory");
if (WebParam2.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam2 = WebParam2.substring(0,2000);
}
set_Value ("WebParam2", WebParam2);
}
/** Get Web Parameter 2.
Web Site Parameter 2 (default index page) */
public String getWebParam2() 
{
return (String)get_Value("WebParam2");
}
/** Set Web Parameter 3.
Web Site Parameter 3 (default left - menu) */
public void setWebParam3 (String WebParam3)
{
if (WebParam3 == null) throw new IllegalArgumentException ("WebParam3 is mandatory");
if (WebParam3.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam3 = WebParam3.substring(0,2000);
}
set_Value ("WebParam3", WebParam3);
}
/** Get Web Parameter 3.
Web Site Parameter 3 (default left - menu) */
public String getWebParam3() 
{
return (String)get_Value("WebParam3");
}
/** Set Web Parameter 4.
Web Site Parameter 4 (default footer left) */
public void setWebParam4 (String WebParam4)
{
if (WebParam4 == null) throw new IllegalArgumentException ("WebParam4 is mandatory");
if (WebParam4.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam4 = WebParam4.substring(0,2000);
}
set_Value ("WebParam4", WebParam4);
}
/** Get Web Parameter 4.
Web Site Parameter 4 (default footer left) */
public String getWebParam4() 
{
return (String)get_Value("WebParam4");
}
/** Set Web Parameter 5.
Web Site Parameter 5 (default footer center) */
public void setWebParam5 (String WebParam5)
{
if (WebParam5 == null) throw new IllegalArgumentException ("WebParam5 is mandatory");
if (WebParam5.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam5 = WebParam5.substring(0,2000);
}
set_Value ("WebParam5", WebParam5);
}
/** Get Web Parameter 5.
Web Site Parameter 5 (default footer center) */
public String getWebParam5() 
{
return (String)get_Value("WebParam5");
}
/** Set Web Parameter 6.
Web Site Parameter 6 (default footer right) */
public void setWebParam6 (String WebParam6)
{
if (WebParam6 == null) throw new IllegalArgumentException ("WebParam6 is mandatory");
if (WebParam6.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam6 = WebParam6.substring(0,2000);
}
set_Value ("WebParam6", WebParam6);
}
/** Get Web Parameter 6.
Web Site Parameter 6 (default footer right) */
public String getWebParam6() 
{
return (String)get_Value("WebParam6");
}
/** Set Web Store.
A Web Store of the Client */
public void setW_Store_ID (int W_Store_ID)
{
set_ValueNoCheck ("W_Store_ID", new Integer(W_Store_ID));
}
/** Get Web Store.
A Web Store of the Client */
public int getW_Store_ID() 
{
Integer ii = (Integer)get_Value("W_Store_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
