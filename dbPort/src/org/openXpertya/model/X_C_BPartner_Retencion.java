/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_Retencion
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:28.328 */
public class X_C_BPartner_Retencion extends PO
{
/** Constructor estándar */
public X_C_BPartner_Retencion (Properties ctx, int C_BPartner_Retencion_ID, String trxName)
{
super (ctx, C_BPartner_Retencion_ID, trxName);
/** if (C_BPartner_Retencion_ID == 0)
{
setC_BPartner_ID (0);
setC_BPartner_Retencion_ID (0);
setC_RetencionSchema_ID (0);
}
 */
}
/** Load Constructor */
public X_C_BPartner_Retencion (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000144 */
public static final int Table_ID=1000144;

/** TableName=C_BPartner_Retencion */
public static final String Table_Name="C_BPartner_Retencion";

protected static KeyNamePair Model = new KeyNamePair(1000144,"C_BPartner_Retencion");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_Retencion[").append(getID()).append("]");
return sb.toString();
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
/** Set Vendor Retencion */
public void setC_BPartner_Retencion_ID (int C_BPartner_Retencion_ID)
{
set_ValueNoCheck ("C_BPartner_Retencion_ID", new Integer(C_BPartner_Retencion_ID));
}
/** Get Vendor Retencion */
public int getC_BPartner_Retencion_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Retencion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Schema */
public void setC_RetencionSchema_ID (int C_RetencionSchema_ID)
{
set_Value ("C_RetencionSchema_ID", new Integer(C_RetencionSchema_ID));
}
/** Get Retencion Schema */
public int getC_RetencionSchema_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 60)
{
log.warning("Length > 60 - truncated");
Description = Description.substring(0,59);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDescription());
}
/** Set Tax ID.
Tax Identification */
public void setTaxID (String TaxID)
{
if (TaxID != null && TaxID.length() > 20)
{
log.warning("Length > 20 - truncated");
TaxID = TaxID.substring(0,19);
}
set_Value ("TaxID", TaxID);
}
/** Get Tax ID.
Tax Identification */
public String getTaxID() 
{
return (String)get_Value("TaxID");
}
}
