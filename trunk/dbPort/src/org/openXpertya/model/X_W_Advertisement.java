/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por W_Advertisement
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.328 */
public class X_W_Advertisement extends PO
{
/** Constructor estándar */
public X_W_Advertisement (Properties ctx, int W_Advertisement_ID, String trxName)
{
super (ctx, W_Advertisement_ID, trxName);
/** if (W_Advertisement_ID == 0)
{
setC_BPartner_ID (0);
setIsSelfService (true);	// Y
setName (null);
setPublishStatus (null);	// U
setW_Advertisement_ID (0);
}
 */
}
/** Load Constructor */
public X_W_Advertisement (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=579 */
public static final int Table_ID=579;

/** TableName=W_Advertisement */
public static final String Table_Name="W_Advertisement";

protected static KeyNamePair Model = new KeyNamePair(579,"W_Advertisement");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_W_Advertisement[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_Value ("AD_User_ID", null);
 else 
set_Value ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Advertisement Text.
Text of the Advertisement */
public void setAdText (String AdText)
{
if (AdText != null && AdText.length() > 2000)
{
log.warning("Length > 2000 - truncated");
AdText = AdText.substring(0,1999);
}
set_Value ("AdText", AdText);
}
/** Get Advertisement Text.
Text of the Advertisement */
public String getAdText() 
{
return (String)get_Value("AdText");
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Image URL.
URL of  image */
public void setImageURL (String ImageURL)
{
if (ImageURL != null && ImageURL.length() > 120)
{
log.warning("Length > 120 - truncated");
ImageURL = ImageURL.substring(0,119);
}
set_Value ("ImageURL", ImageURL);
}
/** Get Image URL.
URL of  image */
public String getImageURL() 
{
return (String)get_Value("ImageURL");
}
/** Set Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public void setIsSelfService (boolean IsSelfService)
{
set_Value ("IsSelfService", new Boolean(IsSelfService));
}
/** Get Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public boolean isSelfService() 
{
Object oo = get_Value("IsSelfService");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
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
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int PUBLISHSTATUS_AD_Reference_ID=310;
/** Released = R */
public static final String PUBLISHSTATUS_Released = "R";
/** Test = T */
public static final String PUBLISHSTATUS_Test = "T";
/** Under Revision = U */
public static final String PUBLISHSTATUS_UnderRevision = "U";
/** Void = V */
public static final String PUBLISHSTATUS_Void = "V";
/** Set Publication Status.
Status of Publication */
public void setPublishStatus (String PublishStatus)
{
if (PublishStatus.equals("R") || PublishStatus.equals("T") || PublishStatus.equals("U") || PublishStatus.equals("V"));
 else throw new IllegalArgumentException ("PublishStatus Invalid value - Reference_ID=310 - R - T - U - V");
if (PublishStatus == null) throw new IllegalArgumentException ("PublishStatus is mandatory");
if (PublishStatus.length() > 1)
{
log.warning("Length > 1 - truncated");
PublishStatus = PublishStatus.substring(0,0);
}
set_Value ("PublishStatus", PublishStatus);
}
/** Get Publication Status.
Status of Publication */
public String getPublishStatus() 
{
return (String)get_Value("PublishStatus");
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
/** Set Version.
Version of the table definition */
public void setVersion (int Version)
{
set_Value ("Version", new Integer(Version));
}
/** Get Version.
Version of the table definition */
public int getVersion() 
{
Integer ii = (Integer)get_Value("Version");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Advertisement.
Web Advertisement */
public void setW_Advertisement_ID (int W_Advertisement_ID)
{
set_ValueNoCheck ("W_Advertisement_ID", new Integer(W_Advertisement_ID));
}
/** Get Advertisement.
Web Advertisement */
public int getW_Advertisement_ID() 
{
Integer ii = (Integer)get_Value("W_Advertisement_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Click Count.
Web Click Management */
public void setW_ClickCount_ID (int W_ClickCount_ID)
{
if (W_ClickCount_ID <= 0) set_Value ("W_ClickCount_ID", null);
 else 
set_Value ("W_ClickCount_ID", new Integer(W_ClickCount_ID));
}
/** Get Click Count.
Web Click Management */
public int getW_ClickCount_ID() 
{
Integer ii = (Integer)get_Value("W_ClickCount_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Counter Count.
Web Counter Count Management */
public void setW_CounterCount_ID (int W_CounterCount_ID)
{
if (W_CounterCount_ID <= 0) set_Value ("W_CounterCount_ID", null);
 else 
set_Value ("W_CounterCount_ID", new Integer(W_CounterCount_ID));
}
/** Get Counter Count.
Web Counter Count Management */
public int getW_CounterCount_ID() 
{
Integer ii = (Integer)get_Value("W_CounterCount_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Web Parameter 1.
Web Site Parameter 1 (default: header image) */
public void setWebParam1 (String WebParam1)
{
if (WebParam1 != null && WebParam1.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam1 = WebParam1.substring(0,1999);
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
if (WebParam2 != null && WebParam2.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam2 = WebParam2.substring(0,1999);
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
if (WebParam3 != null && WebParam3.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam3 = WebParam3.substring(0,1999);
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
if (WebParam4 != null && WebParam4.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam4 = WebParam4.substring(0,1999);
}
set_Value ("WebParam4", WebParam4);
}
/** Get Web Parameter 4.
Web Site Parameter 4 (default footer left) */
public String getWebParam4() 
{
return (String)get_Value("WebParam4");
}
}
