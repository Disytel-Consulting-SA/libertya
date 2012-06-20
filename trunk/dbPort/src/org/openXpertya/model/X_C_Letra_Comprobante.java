/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Letra_Comprobante
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:31.839 */
public class X_C_Letra_Comprobante extends PO
{
/** Constructor estándar */
public X_C_Letra_Comprobante (Properties ctx, int C_Letra_Comprobante_ID, String trxName)
{
super (ctx, C_Letra_Comprobante_ID, trxName);
/** if (C_Letra_Comprobante_ID == 0)
{
setC_Letra_Comprobante_ID (0);
setIsDefault (false);
setLetra (null);
}
 */
}
/** Load Constructor */
public X_C_Letra_Comprobante (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000153 */
public static final int Table_ID=1000153;

/** TableName=C_Letra_Comprobante */
public static final String Table_Name="C_Letra_Comprobante";

protected static KeyNamePair Model = new KeyNamePair(1000153,"C_Letra_Comprobante");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Letra_Comprobante[").append(getID()).append("]");
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
/** Set Letra del comprobante */
public void setC_Letra_Comprobante_ID (int C_Letra_Comprobante_ID)
{
set_ValueNoCheck ("C_Letra_Comprobante_ID", new Integer(C_Letra_Comprobante_ID));
}
/** Get Letra del comprobante */
public int getC_Letra_Comprobante_ID() 
{
Integer ii = (Integer)get_Value("C_Letra_Comprobante_ID");
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
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Letra */
public void setLetra (String Letra)
{
if (Letra == null) throw new IllegalArgumentException ("Letra is mandatory");
if (Letra.length() > 1)
{
log.warning("Length > 1 - truncated");
Letra = Letra.substring(0,1);
}
set_Value ("Letra", Letra);
}
/** Get Letra */
public String getLetra() 
{
return (String)get_Value("Letra");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getLetra());
}
}
