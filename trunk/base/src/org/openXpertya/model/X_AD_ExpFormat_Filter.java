/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ExpFormat_Filter
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-02-10 20:31:06.28 */
public class X_AD_ExpFormat_Filter extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ExpFormat_Filter (Properties ctx, int AD_ExpFormat_Filter_ID, String trxName)
{
super (ctx, AD_ExpFormat_Filter_ID, trxName);
/** if (AD_ExpFormat_Filter_ID == 0)
{
setAD_ExpFormat_Filter_ID (0);
setAD_ExpFormat_ID (0);
setFilter (null);
}
 */
}
/** Load Constructor */
public X_AD_ExpFormat_Filter (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ExpFormat_Filter");

/** TableName=AD_ExpFormat_Filter */
public static final String Table_Name="AD_ExpFormat_Filter";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ExpFormat_Filter");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ExpFormat_Filter[").append(getID()).append("]");
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
/** Set Component Version Identifier */
public void setAD_ComponentVersion_ID (int AD_ComponentVersion_ID)
{
if (AD_ComponentVersion_ID <= 0) set_Value ("AD_ComponentVersion_ID", null);
 else 
set_Value ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_ExpFormat_Filter_ID */
public void setAD_ExpFormat_Filter_ID (int AD_ExpFormat_Filter_ID)
{
set_ValueNoCheck ("AD_ExpFormat_Filter_ID", new Integer(AD_ExpFormat_Filter_ID));
}
/** Get AD_ExpFormat_Filter_ID */
public int getAD_ExpFormat_Filter_ID() 
{
Integer ii = (Integer)get_Value("AD_ExpFormat_Filter_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Export Format */
public void setAD_ExpFormat_ID (int AD_ExpFormat_ID)
{
set_Value ("AD_ExpFormat_ID", new Integer(AD_ExpFormat_ID));
}
/** Get Export Format */
public int getAD_ExpFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_ExpFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Filter.
SQL condition used for filter data from export format data source */
public void setFilter (String Filter)
{
if (Filter == null) throw new IllegalArgumentException ("Filter is mandatory");
if (Filter.length() > 2500)
{
log.warning("Length > 2500 - truncated");
Filter = Filter.substring(0,2500);
}
set_Value ("Filter", Filter);
}
/** Get Filter.
SQL condition used for filter data from export format data source */
public String getFilter() 
{
return (String)get_Value("Filter");
}
}
