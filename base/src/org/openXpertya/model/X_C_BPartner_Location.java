/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_Location
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-09-22 15:30:12.102 */
public class X_C_BPartner_Location extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BPartner_Location (Properties ctx, int C_BPartner_Location_ID, String trxName)
{
super (ctx, C_BPartner_Location_ID, trxName);
/** if (C_BPartner_Location_ID == 0)
{
setC_BPartner_ID (0);
setC_BPartner_Location_ID (0);
setC_Location_ID (0);
setIsBillTo (true);	// Y
setIsPayFrom (true);	// Y
setIsRemitTo (true);	// Y
setIsShipTo (true);	// Y
setName (null);	// .
}
 */
}
/** Load Constructor */
public X_C_BPartner_Location (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BPartner_Location");

/** TableName=C_BPartner_Location */
public static final String Table_Name="C_BPartner_Location";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BPartner_Location");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_Location[").append(getID()).append("]");
return sb.toString();
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
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_ValueNoCheck ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Partner Location.
Identifies the (ship to) address for this Business Partner */
public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
{
set_ValueNoCheck ("C_BPartner_Location_ID", new Integer(C_BPartner_Location_ID));
}
/** Get Partner Location.
Identifies the (ship to) address for this Business Partner */
public int getC_BPartner_Location_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Address.
Location or Address */
public void setC_Location_ID (int C_Location_ID)
{
set_Value ("C_Location_ID", new Integer(C_Location_ID));
}
/** Get Address.
Location or Address */
public int getC_Location_ID() 
{
Integer ii = (Integer)get_Value("C_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sales Region.
Sales coverage region */
public void setC_SalesRegion_ID (int C_SalesRegion_ID)
{
if (C_SalesRegion_ID <= 0) set_Value ("C_SalesRegion_ID", null);
 else 
set_Value ("C_SalesRegion_ID", new Integer(C_SalesRegion_ID));
}
/** Get Sales Region.
Sales coverage region */
public int getC_SalesRegion_ID() 
{
Integer ii = (Integer)get_Value("C_SalesRegion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set EMail.
Electronic Mail Address */
public void setEMail (String EMail)
{
if (EMail != null && EMail.length() > 255)
{
log.warning("Length > 255 - truncated");
EMail = EMail.substring(0,255);
}
set_Value ("EMail", EMail);
}
/** Get EMail.
Electronic Mail Address */
public String getEMail() 
{
return (String)get_Value("EMail");
}
/** Set Fax.
Facsimile number */
public void setFax (String Fax)
{
if (Fax != null && Fax.length() > 40)
{
log.warning("Length > 40 - truncated");
Fax = Fax.substring(0,40);
}
set_Value ("Fax", Fax);
}
/** Get Fax.
Facsimile number */
public String getFax() 
{
return (String)get_Value("Fax");
}
/** Set Invoice Address.
Business Partner Invoice/Bill Address */
public void setIsBillTo (boolean IsBillTo)
{
set_Value ("IsBillTo", new Boolean(IsBillTo));
}
/** Get Invoice Address.
Business Partner Invoice/Bill Address */
public boolean isBillTo() 
{
Object oo = get_Value("IsBillTo");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set ISDN.
ISDN or modem line */
public void setISDN (String ISDN)
{
if (ISDN != null && ISDN.length() > 40)
{
log.warning("Length > 40 - truncated");
ISDN = ISDN.substring(0,40);
}
set_Value ("ISDN", ISDN);
}
/** Get ISDN.
ISDN or modem line */
public String getISDN() 
{
return (String)get_Value("ISDN");
}
/** Set Pay-From Address.
Business Partner pays from that address and we'll send dunning letters there */
public void setIsPayFrom (boolean IsPayFrom)
{
set_Value ("IsPayFrom", new Boolean(IsPayFrom));
}
/** Get Pay-From Address.
Business Partner pays from that address and we'll send dunning letters there */
public boolean isPayFrom() 
{
Object oo = get_Value("IsPayFrom");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Remit-To Address.
Business Partner payment address */
public void setIsRemitTo (boolean IsRemitTo)
{
set_Value ("IsRemitTo", new Boolean(IsRemitTo));
}
/** Get Remit-To Address.
Business Partner payment address */
public boolean isRemitTo() 
{
Object oo = get_Value("IsRemitTo");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Ship Address.
Business Partner Shipment Address */
public void setIsShipTo (boolean IsShipTo)
{
set_Value ("IsShipTo", new Boolean(IsShipTo));
}
/** Get Ship Address.
Business Partner Shipment Address */
public boolean isShipTo() 
{
Object oo = get_Value("IsShipTo");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsTemporal */
public void setIsTemporal (boolean IsTemporal)
{
set_Value ("IsTemporal", new Boolean(IsTemporal));
}
/** Get IsTemporal */
public boolean isTemporal() 
{
Object oo = get_Value("IsTemporal");
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
/** Set Phone.
Identifies a telephone number */
public void setPhone (String Phone)
{
if (Phone != null && Phone.length() > 40)
{
log.warning("Length > 40 - truncated");
Phone = Phone.substring(0,40);
}
set_Value ("Phone", Phone);
}
/** Get Phone.
Identifies a telephone number */
public String getPhone() 
{
return (String)get_Value("Phone");
}
/** Set 2nd Phone.
Identifies an alternate telephone number. */
public void setPhone2 (String Phone2)
{
if (Phone2 != null && Phone2.length() > 40)
{
log.warning("Length > 40 - truncated");
Phone2 = Phone2.substring(0,40);
}
set_Value ("Phone2", Phone2);
}
/** Get 2nd Phone.
Identifies an alternate telephone number. */
public String getPhone2() 
{
return (String)get_Value("Phone2");
}
}
