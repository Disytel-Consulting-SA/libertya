/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ElectronicInvoiceFormatHdr
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-05-28 14:49:21.156 */
public class X_AD_ElectronicInvoiceFormatHdr extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ElectronicInvoiceFormatHdr (Properties ctx, int AD_ElectronicInvoiceFormatHdr_ID, String trxName)
{
super (ctx, AD_ElectronicInvoiceFormatHdr_ID, trxName);
/** if (AD_ElectronicInvoiceFormatHdr_ID == 0)
{
setAD_ElectronicInvoiceFormatHdr_ID (0);
setAD_ElectronicInvoiceFormat_ID (0);
setExpFileName (null);
settipo (null);
}
 */
}
/** Load Constructor */
public X_AD_ElectronicInvoiceFormatHdr (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ElectronicInvoiceFormatHdr");

/** TableName=AD_ElectronicInvoiceFormatHdr */
public static final String Table_Name="AD_ElectronicInvoiceFormatHdr";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ElectronicInvoiceFormatHdr");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ElectronicInvoiceFormatHdr[").append(getID()).append("]");
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
set_ValueNoCheck ("AD_ElectronicInvoiceFormatHdr_ID", new Integer(AD_ElectronicInvoiceFormatHdr_ID));
}
/** Get AD_ElectronicInvoiceFormatHdr_ID */
public int getAD_ElectronicInvoiceFormatHdr_ID() 
{
Integer ii = (Integer)get_Value("AD_ElectronicInvoiceFormatHdr_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_ElectronicInvoiceFormat_ID */
public void setAD_ElectronicInvoiceFormat_ID (int AD_ElectronicInvoiceFormat_ID)
{
set_Value ("AD_ElectronicInvoiceFormat_ID", new Integer(AD_ElectronicInvoiceFormat_ID));
}
/** Get AD_ElectronicInvoiceFormat_ID */
public int getAD_ElectronicInvoiceFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_ElectronicInvoiceFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Campo1 */
public void setCampo1 (String Campo1)
{
if (Campo1 != null && Campo1.length() > 20)
{
log.warning("Length > 20 - truncated");
Campo1 = Campo1.substring(0,20);
}
set_Value ("Campo1", Campo1);
}
/** Get Campo1 */
public String getCampo1() 
{
return (String)get_Value("Campo1");
}
/** Set Campo2 */
public void setCampo2 (String Campo2)
{
if (Campo2 != null && Campo2.length() > 20)
{
log.warning("Length > 20 - truncated");
Campo2 = Campo2.substring(0,20);
}
set_Value ("Campo2", Campo2);
}
/** Get Campo2 */
public String getCampo2() 
{
return (String)get_Value("Campo2");
}
/** Set Distinto */
public void setDistinto (String Distinto)
{
if (Distinto != null && Distinto.length() > 50)
{
log.warning("Length > 50 - truncated");
Distinto = Distinto.substring(0,50);
}
set_Value ("Distinto", Distinto);
}
/** Get Distinto */
public String getDistinto() 
{
return (String)get_Value("Distinto");
}
/** Set ExpFileName */
public void setExpFileName (String ExpFileName)
{
if (ExpFileName == null) throw new IllegalArgumentException ("ExpFileName is mandatory");
if (ExpFileName.length() > 200)
{
log.warning("Length > 200 - truncated");
ExpFileName = ExpFileName.substring(0,200);
}
set_Value ("ExpFileName", ExpFileName);
}
/** Get ExpFileName */
public String getExpFileName() 
{
return (String)get_Value("ExpFileName");
}
/** Set IsCondicional */
public void setIsCondicional (boolean IsCondicional)
{
set_Value ("IsCondicional", new Boolean(IsCondicional));
}
/** Get IsCondicional */
public boolean isCondicional() 
{
Object oo = get_Value("IsCondicional");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int TIPO_AD_Reference_ID = MReference.getReferenceID("InvoiceType");
/** Todo = A */
public static final String TIPO_Todo = "A";
/** Venta = V */
public static final String TIPO_Venta = "V";
/** Compra = C */
public static final String TIPO_Compra = "C";
/** Set Tipo */
public void settipo (String tipo)
{
if (tipo.equals("A") || tipo.equals("V") || tipo.equals("C"));
 else throw new IllegalArgumentException ("tipo Invalid value - Reference = TIPO_AD_Reference_ID - A - V - C");
if (tipo == null) throw new IllegalArgumentException ("tipo is mandatory");
if (tipo.length() > 1)
{
log.warning("Length > 1 - truncated");
tipo = tipo.substring(0,1);
}
set_Value ("tipo", tipo);
}
/** Get Tipo */
public String gettipo() 
{
return (String)get_Value("tipo");
}
}
