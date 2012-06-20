/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Plugin
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-11-01 12:40:55.903 */
public class X_AD_Plugin extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Plugin (Properties ctx, int AD_Plugin_ID, String trxName)
{
super (ctx, AD_Plugin_ID, trxName);
/** if (AD_Plugin_ID == 0)
{
setAD_ComponentVersion_ID (0);
setAD_Plugin_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_Plugin (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Plugin");

/** TableName=AD_Plugin */
public static final String Table_Name="AD_Plugin";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Plugin");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Plugin[").append(getID()).append("]");
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
set_Value ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_Plugin_ID */
public void setAD_Plugin_ID (int AD_Plugin_ID)
{
set_ValueNoCheck ("AD_Plugin_ID", new Integer(AD_Plugin_ID));
}
/** Get AD_Plugin_ID */
public int getAD_Plugin_ID() 
{
Integer ii = (Integer)get_Value("AD_Plugin_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Component_Export_Date */
public void setComponent_Export_Date (String Component_Export_Date)
{
if (Component_Export_Date != null && Component_Export_Date.length() > 30)
{
log.warning("Length > 30 - truncated");
Component_Export_Date = Component_Export_Date.substring(0,30);
}
set_Value ("Component_Export_Date", Component_Export_Date);
}
/** Get Component_Export_Date */
public String getComponent_Export_Date() 
{
return (String)get_Value("Component_Export_Date");
}
/** Set Component_Last_Changelog */
public void setComponent_Last_Changelog (String Component_Last_Changelog)
{
if (Component_Last_Changelog != null && Component_Last_Changelog.length() > 30)
{
log.warning("Length > 30 - truncated");
Component_Last_Changelog = Component_Last_Changelog.substring(0,30);
}
set_Value ("Component_Last_Changelog", Component_Last_Changelog);
}
/** Get Component_Last_Changelog */
public String getComponent_Last_Changelog() 
{
return (String)get_Value("Component_Last_Changelog");
}
}
