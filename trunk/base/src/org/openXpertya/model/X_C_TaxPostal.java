/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_TaxPostal
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.453 */
public class X_C_TaxPostal extends PO
{
/** Constructor estándar */
public X_C_TaxPostal (Properties ctx, int C_TaxPostal_ID, String trxName)
{
super (ctx, C_TaxPostal_ID, trxName);
/** if (C_TaxPostal_ID == 0)
{
setC_TaxPostal_ID (0);
setC_Tax_ID (0);
setPostal (null);
}
 */
}
/** Load Constructor */
public X_C_TaxPostal (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=701 */
public static final int Table_ID=701;

/** TableName=C_TaxPostal */
public static final String Table_Name="C_TaxPostal";

protected static KeyNamePair Model = new KeyNamePair(701,"C_TaxPostal");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_TaxPostal[").append(getID()).append("]");
return sb.toString();
}
/** Set Tax ZIP.
Tax Postal/ZIP */
public void setC_TaxPostal_ID (int C_TaxPostal_ID)
{
set_ValueNoCheck ("C_TaxPostal_ID", new Integer(C_TaxPostal_ID));
}
/** Get Tax ZIP.
Tax Postal/ZIP */
public int getC_TaxPostal_ID() 
{
Integer ii = (Integer)get_Value("C_TaxPostal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax.
Tax identifier */
public void setC_Tax_ID (int C_Tax_ID)
{
set_ValueNoCheck ("C_Tax_ID", new Integer(C_Tax_ID));
}
/** Get Tax.
Tax identifier */
public int getC_Tax_ID() 
{
Integer ii = (Integer)get_Value("C_Tax_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set ZIP.
Postal code */
public void setPostal (String Postal)
{
if (Postal == null) throw new IllegalArgumentException ("Postal is mandatory");
if (Postal.length() > 10)
{
log.warning("Length > 10 - truncated");
Postal = Postal.substring(0,9);
}
set_Value ("Postal", Postal);
}
/** Get ZIP.
Postal code */
public String getPostal() 
{
return (String)get_Value("Postal");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getPostal());
}
/** Set ZIP To.
Postal code to */
public void setPostal_To (String Postal_To)
{
if (Postal_To != null && Postal_To.length() > 10)
{
log.warning("Length > 10 - truncated");
Postal_To = Postal_To.substring(0,9);
}
set_Value ("Postal_To", Postal_To);
}
/** Get ZIP To.
Postal code to */
public String getPostal_To() 
{
return (String)get_Value("Postal_To");
}
}
