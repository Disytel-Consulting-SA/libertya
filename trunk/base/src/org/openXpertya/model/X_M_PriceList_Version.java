/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_PriceList_Version
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:38.875 */
public class X_M_PriceList_Version extends PO
{
/** Constructor estándar */
public X_M_PriceList_Version (Properties ctx, int M_PriceList_Version_ID, String trxName)
{
super (ctx, M_PriceList_Version_ID, trxName);
/** if (M_PriceList_Version_ID == 0)
{
setM_DiscountSchema_ID (0);
setM_PriceList_ID (0);
setM_PriceList_Version_ID (0);
setName (null);	// @#Date@
setValidFrom (new Timestamp(System.currentTimeMillis()));	// @#Date@
}
 */
}
/** Load Constructor */
public X_M_PriceList_Version (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=295 */
public static final int Table_ID=295;

/** TableName=M_PriceList_Version */
public static final String Table_Name="M_PriceList_Version";

protected static KeyNamePair Model = new KeyNamePair(295,"M_PriceList_Version");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_PriceList_Version[").append(getID()).append("]");
return sb.toString();
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
public static final int M_DISCOUNTSCHEMA_ID_AD_Reference_ID=249;
/** Set Discount Schema.
Schema to calculate the trade discount percentage */
public void setM_DiscountSchema_ID (int M_DiscountSchema_ID)
{
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
/** Set Price List.
Unique identifier of a Price List */
public void setM_PriceList_ID (int M_PriceList_ID)
{
set_ValueNoCheck ("M_PriceList_ID", new Integer(M_PriceList_ID));
}
/** Get Price List.
Unique identifier of a Price List */
public int getM_PriceList_ID() 
{
Integer ii = (Integer)get_Value("M_PriceList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Price List Version.
Identifies a unique instance of a Price List */
public void setM_PriceList_Version_ID (int M_PriceList_Version_ID)
{
set_ValueNoCheck ("M_PriceList_Version_ID", new Integer(M_PriceList_Version_ID));
}
/** Get Price List Version.
Identifies a unique instance of a Price List */
public int getM_PriceList_Version_ID() 
{
Integer ii = (Integer)get_Value("M_PriceList_Version_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRICELIST_VERSION_BASE_ID_AD_Reference_ID=188;
/** Set Base Price List.
Source for Price list calculations */
public void setM_Pricelist_Version_Base_ID (int M_Pricelist_Version_Base_ID)
{
if (M_Pricelist_Version_Base_ID <= 0) set_Value ("M_Pricelist_Version_Base_ID", null);
 else 
set_Value ("M_Pricelist_Version_Base_ID", new Integer(M_Pricelist_Version_Base_ID));
}
/** Get Base Price List.
Source for Price list calculations */
public int getM_Pricelist_Version_Base_ID() 
{
Integer ii = (Integer)get_Value("M_Pricelist_Version_Base_ID");
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
/** Set Create */
public void setProcCreate (String ProcCreate)
{
if (ProcCreate != null && ProcCreate.length() > 1)
{
log.warning("Length > 1 - truncated");
ProcCreate = ProcCreate.substring(0,0);
}
set_Value ("ProcCreate", ProcCreate);
}
/** Get Create */
public String getProcCreate() 
{
return (String)get_Value("ProcCreate");
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
if (ValidFrom == null) throw new IllegalArgumentException ("ValidFrom is mandatory");
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
}
