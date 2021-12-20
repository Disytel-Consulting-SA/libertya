/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_Retencion
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2021-12-20 13:54:05.322 */
public class X_C_BPartner_Retencion extends org.openXpertya.model.PO
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
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BPartner_Retencion");

/** TableName=C_BPartner_Retencion */
public static final String Table_Name="C_BPartner_Retencion";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BPartner_Retencion");
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
/** Set Alicuota */
public void setAlicuota (BigDecimal Alicuota)
{
set_Value ("Alicuota", Alicuota);
}
/** Get Alicuota */
public BigDecimal getAlicuota() 
{
BigDecimal bd = (BigDecimal)get_Value("Alicuota");
if (bd == null) return Env.ZERO;
return bd;
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
Description = Description.substring(0,60);
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
TaxID = TaxID.substring(0,20);
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
