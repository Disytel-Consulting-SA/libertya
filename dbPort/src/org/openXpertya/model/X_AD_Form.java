/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Form
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:26.643 */
public class X_AD_Form extends PO
{
/** Constructor estándar */
public X_AD_Form (Properties ctx, int AD_Form_ID, String trxName)
{
super (ctx, AD_Form_ID, trxName);
/** if (AD_Form_ID == 0)
{
setAccessLevel (null);
setAD_Form_ID (0);
setEntityType (null);	// U
setIsBetaFunctionality (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_Form (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=376 */
public static final int Table_ID=376;

/** TableName=AD_Form */
public static final String Table_Name="AD_Form";

protected static KeyNamePair Model = new KeyNamePair(376,"AD_Form");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Form[").append(getID()).append("]");
return sb.toString();
}
public static final int ACCESSLEVEL_AD_Reference_ID=5;
/** Organization = 1 */
public static final String ACCESSLEVEL_Organization = "1";
/** Client+Organization = 3 */
public static final String ACCESSLEVEL_ClientPlusOrganization = "3";
/** System only = 4 */
public static final String ACCESSLEVEL_SystemOnly = "4";
/** All = 7 */
public static final String ACCESSLEVEL_All = "7";
/** Client only = 2 */
public static final String ACCESSLEVEL_ClientOnly = "2";
/** System+Client = 6 */
public static final String ACCESSLEVEL_SystemPlusClient = "6";
/** Set Data Access Level.
Access Level required */
public void setAccessLevel (String AccessLevel)
{
if (AccessLevel.equals("1") || AccessLevel.equals("3") || AccessLevel.equals("4") || AccessLevel.equals("7") || AccessLevel.equals("2") || AccessLevel.equals("6"));
 else throw new IllegalArgumentException ("AccessLevel Invalid value - Reference_ID=5 - 1 - 3 - 4 - 7 - 2 - 6");
if (AccessLevel == null) throw new IllegalArgumentException ("AccessLevel is mandatory");
if (AccessLevel.length() > 1)
{
log.warning("Length > 1 - truncated");
AccessLevel = AccessLevel.substring(0,1);
}
set_Value ("AccessLevel", AccessLevel);
}
/** Get Data Access Level.
Access Level required */
public String getAccessLevel() 
{
return (String)get_Value("AccessLevel");
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
/** Set Component Version Identifier */
public void setAD_ComponentVersion_ID (int AD_ComponentVersion_ID)
{
if (AD_ComponentVersion_ID <= 0) set_Value ("AD_ComponentVersion_ID", null);
 else 
set_Value ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Special Form.
Special Form */
public void setAD_Form_ID (int AD_Form_ID)
{
set_ValueNoCheck ("AD_Form_ID", new Integer(AD_Form_ID));
}
/** Get Special Form.
Special Form */
public int getAD_Form_ID() 
{
Integer ii = (Integer)get_Value("AD_Form_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Classname.
Java Classname */
public void setClassname (String Classname)
{
if (Classname != null && Classname.length() > 60)
{
log.warning("Length > 60 - truncated");
Classname = Classname.substring(0,60);
}
set_Value ("Classname", Classname);
}
/** Get Classname.
Java Classname */
public String getClassname() 
{
return (String)get_Value("Classname");
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
public static final int ENTITYTYPE_AD_Reference_ID=245;
/** Applications Integrated with openXpertya = A */
public static final String ENTITYTYPE_ApplicationsIntegratedWithOpenXpertya = "A";
/** Country Version = C */
public static final String ENTITYTYPE_CountryVersion = "C";
/** Dictionary = D */
public static final String ENTITYTYPE_Dictionary = "D";
/** User maintained = U */
public static final String ENTITYTYPE_UserMaintained = "U";
/** Customization = CUST */
public static final String ENTITYTYPE_Customization = "CUST";
/** Set Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public void setEntityType (String EntityType)
{
if (EntityType.equals("A") || EntityType.equals("C") || EntityType.equals("D") || EntityType.equals("U") || EntityType.equals("CUST"));
 else throw new IllegalArgumentException ("EntityType Invalid value - Reference_ID=245 - A - C - D - U - CUST");
if (EntityType == null) throw new IllegalArgumentException ("EntityType is mandatory");
if (EntityType.length() > 4)
{
log.warning("Length > 4 - truncated");
EntityType = EntityType.substring(0,4);
}
set_Value ("EntityType", EntityType);
}
/** Get Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public String getEntityType() 
{
return (String)get_Value("EntityType");
}
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,2000);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Beta Functionality.
This functionality is considered Beta */
public void setIsBetaFunctionality (boolean IsBetaFunctionality)
{
set_Value ("IsBetaFunctionality", new Boolean(IsBetaFunctionality));
}
/** Get Beta Functionality.
This functionality is considered Beta */
public boolean isBetaFunctionality() 
{
Object oo = get_Value("IsBetaFunctionality");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set jsp URL.
Web URL of the jsp function */
public void setJSPURL (String JSPURL)
{
if (JSPURL != null && JSPURL.length() > 120)
{
log.warning("Length > 120 - truncated");
JSPURL = JSPURL.substring(0,120);
}
set_Value ("JSPURL", JSPURL);
}
/** Get jsp URL.
Web URL of the jsp function */
public String getJSPURL() 
{
return (String)get_Value("JSPURL");
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
}
