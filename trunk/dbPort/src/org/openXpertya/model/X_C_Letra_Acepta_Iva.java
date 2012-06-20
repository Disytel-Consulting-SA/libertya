/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Letra_Acepta_Iva
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:31.825 */
public class X_C_Letra_Acepta_Iva extends PO
{
/** Constructor estándar */
public X_C_Letra_Acepta_Iva (Properties ctx, int C_Letra_Acepta_Iva_ID, String trxName)
{
super (ctx, C_Letra_Acepta_Iva_ID, trxName);
/** if (C_Letra_Acepta_Iva_ID == 0)
{
setCategoria_Customer (0);
setCategoria_Vendor (0);
setC_Letra_Acepta_IVA_ID (0);
setC_Letra_Comprobante_ID (0);
}
 */
}
/** Load Constructor */
public X_C_Letra_Acepta_Iva (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000152 */
public static final int Table_ID=1000152;

/** TableName=C_Letra_Acepta_Iva */
public static final String Table_Name="C_Letra_Acepta_Iva";

protected static KeyNamePair Model = new KeyNamePair(1000152,"C_Letra_Acepta_Iva");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Letra_Acepta_Iva[").append(getID()).append("]");
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
public static final int CATEGORIA_CUSTOMER_AD_Reference_ID=1000076;
/** Set categoria_customer */
public void setCategoria_Customer (int Categoria_Customer)
{
set_Value ("Categoria_Customer", new Integer(Categoria_Customer));
}
/** Get categoria_customer */
public int getCategoria_Customer() 
{
Integer ii = (Integer)get_Value("Categoria_Customer");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CATEGORIA_VENDOR_AD_Reference_ID=1000076;
/** Set categoria_vendor */
public void setCategoria_Vendor (int Categoria_Vendor)
{
set_Value ("Categoria_Vendor", new Integer(Categoria_Vendor));
}
/** Get categoria_vendor */
public int getCategoria_Vendor() 
{
Integer ii = (Integer)get_Value("Categoria_Vendor");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Letra acepta IVA */
public void setC_Letra_Acepta_IVA_ID (int C_Letra_Acepta_IVA_ID)
{
set_ValueNoCheck ("C_Letra_Acepta_IVA_ID", new Integer(C_Letra_Acepta_IVA_ID));
}
/** Get Letra acepta IVA */
public int getC_Letra_Acepta_IVA_ID() 
{
Integer ii = (Integer)get_Value("C_Letra_Acepta_IVA_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Letra del comprobante */
public void setC_Letra_Comprobante_ID (int C_Letra_Comprobante_ID)
{
set_Value ("C_Letra_Comprobante_ID", new Integer(C_Letra_Comprobante_ID));
}
/** Get Letra del comprobante */
public int getC_Letra_Comprobante_ID() 
{
Integer ii = (Integer)get_Value("C_Letra_Comprobante_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Monto máximo */
public void setMontoMax (BigDecimal MontoMax)
{
set_Value ("MontoMax", MontoMax);
}
/** Get Monto máximo */
public BigDecimal getMontoMax() 
{
BigDecimal bd = (BigDecimal)get_Value("MontoMax");
if (bd == null) return Env.ZERO;
return bd;
}
}
