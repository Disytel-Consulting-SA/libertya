/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BP_Relation
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.843 */
public class X_C_BP_Relation extends PO
{
/** Constructor estándar */
public X_C_BP_Relation (Properties ctx, int C_BP_Relation_ID, String trxName)
{
super (ctx, C_BP_Relation_ID, trxName);
/** if (C_BP_Relation_ID == 0)
{
setC_BP_Relation_ID (0);
setC_BPartnerRelation_ID (0);
setC_BPartnerRelation_Location_ID (0);
setC_BPartner_ID (0);
setIsBillTo (false);
setIsPayFrom (false);
setIsRemitTo (false);
setIsShipTo (false);	// N
setName (null);
}
 */
}
/** Load Constructor */
public X_C_BP_Relation (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=678 */
public static final int Table_ID=678;

/** TableName=C_BP_Relation */
public static final String Table_Name="C_BP_Relation";

protected static KeyNamePair Model = new KeyNamePair(678,"C_BP_Relation");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BP_Relation[").append(getID()).append("]");
return sb.toString();
}
/** Set Partner Relation.
Business Partner Relation */
public void setC_BP_Relation_ID (int C_BP_Relation_ID)
{
set_ValueNoCheck ("C_BP_Relation_ID", new Integer(C_BP_Relation_ID));
}
/** Get Partner Relation.
Business Partner Relation */
public int getC_BP_Relation_ID() 
{
Integer ii = (Integer)get_Value("C_BP_Relation_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_BPARTNERRELATION_ID_AD_Reference_ID=138;
/** Set Related Partner.
Related Business Partner */
public void setC_BPartnerRelation_ID (int C_BPartnerRelation_ID)
{
set_Value ("C_BPartnerRelation_ID", new Integer(C_BPartnerRelation_ID));
}
/** Get Related Partner.
Related Business Partner */
public int getC_BPartnerRelation_ID() 
{
Integer ii = (Integer)get_Value("C_BPartnerRelation_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_BPARTNERRELATION_LOCATION_ID_AD_Reference_ID=159;
/** Set Related Partner Location.
Location of the related Business Partner */
public void setC_BPartnerRelation_Location_ID (int C_BPartnerRelation_Location_ID)
{
set_Value ("C_BPartnerRelation_Location_ID", new Integer(C_BPartnerRelation_Location_ID));
}
/** Get Related Partner Location.
Location of the related Business Partner */
public int getC_BPartnerRelation_Location_ID() 
{
Integer ii = (Integer)get_Value("C_BPartnerRelation_Location_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Partner Location.
Identifies the (ship to) address for this Business Partner */
public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
{
if (C_BPartner_Location_ID <= 0) set_Value ("C_BPartner_Location_ID", null);
 else 
set_Value ("C_BPartner_Location_ID", new Integer(C_BPartner_Location_ID));
}
/** Get Partner Location.
Identifies the (ship to) address for this Business Partner */
public int getC_BPartner_Location_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Location_ID");
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
set_ValueNoCheck ("IsShipTo", new Boolean(IsShipTo));
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
}
