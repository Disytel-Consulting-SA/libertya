/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ElectronicInvoiceFormatLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-03-12 12:10:03.011 */
public class X_AD_ElectronicInvoiceFormatLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ElectronicInvoiceFormatLine (Properties ctx, int AD_ElectronicInvoiceFormatLine_ID, String trxName)
{
super (ctx, AD_ElectronicInvoiceFormatLine_ID, trxName);
/** if (AD_ElectronicInvoiceFormatLine_ID == 0)
{
setAD_ElectronicInvoiceFormatHdr_ID (0);
setAD_ElectronicInvoiceFormatLine_ID (0);
setNombre_Campo (null);
}
 */
}
/** Load Constructor */
public X_AD_ElectronicInvoiceFormatLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ElectronicInvoiceFormatLine");

/** TableName=AD_ElectronicInvoiceFormatLine */
public static final String Table_Name="AD_ElectronicInvoiceFormatLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ElectronicInvoiceFormatLine");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ElectronicInvoiceFormatLine[").append(getID()).append("]");
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
/** Set AD_ElectronicInvoiceFormatHdr_ID */
public void setAD_ElectronicInvoiceFormatHdr_ID (int AD_ElectronicInvoiceFormatHdr_ID)
{
set_Value ("AD_ElectronicInvoiceFormatHdr_ID", new Integer(AD_ElectronicInvoiceFormatHdr_ID));
}
/** Get AD_ElectronicInvoiceFormatHdr_ID */
public int getAD_ElectronicInvoiceFormatHdr_ID() 
{
Integer ii = (Integer)get_Value("AD_ElectronicInvoiceFormatHdr_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_ElectronicInvoiceFormatLine_ID */
public void setAD_ElectronicInvoiceFormatLine_ID (int AD_ElectronicInvoiceFormatLine_ID)
{
set_ValueNoCheck ("AD_ElectronicInvoiceFormatLine_ID", new Integer(AD_ElectronicInvoiceFormatLine_ID));
}
/** Get AD_ElectronicInvoiceFormatLine_ID */
public int getAD_ElectronicInvoiceFormatLine_ID() 
{
Integer ii = (Integer)get_Value("AD_ElectronicInvoiceFormatLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set IsLeftAlign */
public void setIsLeftAlign (boolean IsLeftAlign)
{
set_Value ("IsLeftAlign", new Boolean(IsLeftAlign));
}
/** Get IsLeftAlign */
public boolean isLeftAlign() 
{
Object oo = get_Value("IsLeftAlign");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Longitud */
public void setLongitud (int Longitud)
{
set_Value ("Longitud", new Integer(Longitud));
}
/** Get Longitud */
public int getLongitud() 
{
Integer ii = (Integer)get_Value("Longitud");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Nombre_Campo */
public void setNombre_Campo (String Nombre_Campo)
{
if (Nombre_Campo == null) throw new IllegalArgumentException ("Nombre_Campo is mandatory");
if (Nombre_Campo.length() > 30)
{
log.warning("Length > 30 - truncated");
Nombre_Campo = Nombre_Campo.substring(0,30);
}
set_Value ("Nombre_Campo", Nombre_Campo);
}
/** Get Nombre_Campo */
public String getNombre_Campo() 
{
return (String)get_Value("Nombre_Campo");
}
/** Set Punto_Decimal */
public void setPunto_Decimal (boolean Punto_Decimal)
{
set_Value ("Punto_Decimal", new Boolean(Punto_Decimal));
}
/** Get Punto_Decimal */
public boolean isPunto_Decimal() 
{
Object oo = get_Value("Punto_Decimal");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Relleno */
public void setRelleno (String Relleno)
{
if (Relleno != null && Relleno.length() > 1)
{
log.warning("Length > 1 - truncated");
Relleno = Relleno.substring(0,1);
}
set_Value ("Relleno", Relleno);
}
/** Get Relleno */
public String getRelleno() 
{
return (String)get_Value("Relleno");
}
/** Set Secuencia */
public void setSecuencia (int Secuencia)
{
set_Value ("Secuencia", new Integer(Secuencia));
}
/** Get Secuencia */
public int getSecuencia() 
{
Integer ii = (Integer)get_Value("Secuencia");
if (ii == null) return 0;
return ii.intValue();
}
}
