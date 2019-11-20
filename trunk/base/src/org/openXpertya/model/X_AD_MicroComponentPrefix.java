/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_MicroComponentPrefix
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-11-20 13:21:02.033 */
public class X_AD_MicroComponentPrefix extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_MicroComponentPrefix (Properties ctx, int AD_MicroComponentPrefix_ID, String trxName)
{
super (ctx, AD_MicroComponentPrefix_ID, trxName);
/** if (AD_MicroComponentPrefix_ID == 0)
{
setAD_MicroComponentprefix_ID (0);
setDescription (null);
setPrefix (null);
}
 */
}
/** Load Constructor */
public X_AD_MicroComponentPrefix (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_MicroComponentPrefix");

/** TableName=AD_MicroComponentPrefix */
public static final String Table_Name="AD_MicroComponentPrefix";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_MicroComponentPrefix");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_MicroComponentPrefix[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_MicroComponentPrefix_ID */
public void setAD_MicroComponentprefix_ID (int AD_MicroComponentprefix_ID)
{
set_ValueNoCheck ("AD_MicroComponentprefix_ID", new Integer(AD_MicroComponentprefix_ID));
}
/** Get AD_MicroComponentPrefix_ID */
public int getAD_MicroComponentprefix_ID() 
{
Integer ii = (Integer)get_Value("AD_MicroComponentprefix_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description == null) throw new IllegalArgumentException ("Description is mandatory");
if (Description.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Description = Description.substring(0,2000);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Prefix.
Prefix before the sequence number */
public void setPrefix (String Prefix)
{
if (Prefix == null) throw new IllegalArgumentException ("Prefix is mandatory");
if (Prefix.length() > 10)
{
log.warning("Length > 10 - truncated");
Prefix = Prefix.substring(0,10);
}
set_Value ("Prefix", Prefix);
}
/** Get Prefix.
Prefix before the sequence number */
public String getPrefix() 
{
return (String)get_Value("Prefix");
}
}
