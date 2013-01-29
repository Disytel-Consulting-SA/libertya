/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ElectronicInvoiceFormat
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-03-12 12:10:02.898 */
public class X_AD_ElectronicInvoiceFormat extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ElectronicInvoiceFormat (Properties ctx, int AD_ElectronicInvoiceFormat_ID, String trxName)
{
super (ctx, AD_ElectronicInvoiceFormat_ID, trxName);
/** if (AD_ElectronicInvoiceFormat_ID == 0)
{
setAD_ElectronicInvoiceFormat_ID (0);
setNombreFormato (null);
}
 */
}
/** Load Constructor */
public X_AD_ElectronicInvoiceFormat (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ElectronicInvoiceFormat");

/** TableName=AD_ElectronicInvoiceFormat */
public static final String Table_Name="AD_ElectronicInvoiceFormat";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ElectronicInvoiceFormat");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ElectronicInvoiceFormat[").append(getID()).append("]");
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
/** Set AD_ElectronicInvoiceFormat_ID */
public void setAD_ElectronicInvoiceFormat_ID (int AD_ElectronicInvoiceFormat_ID)
{
set_ValueNoCheck ("AD_ElectronicInvoiceFormat_ID", new Integer(AD_ElectronicInvoiceFormat_ID));
}
/** Get AD_ElectronicInvoiceFormat_ID */
public int getAD_ElectronicInvoiceFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_ElectronicInvoiceFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set NombreFormato */
public void setNombreFormato (String NombreFormato)
{
if (NombreFormato == null) throw new IllegalArgumentException ("NombreFormato is mandatory");
if (NombreFormato.length() > 32)
{
log.warning("Length > 32 - truncated");
NombreFormato = NombreFormato.substring(0,32);
}
set_Value ("NombreFormato", NombreFormato);
}
/** Get NombreFormato */
public String getNombreFormato() 
{
return (String)get_Value("NombreFormato");
}
}
