/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Plugin_Detail
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-12-07 10:06:57.45 */
public class X_AD_Plugin_Detail extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Plugin_Detail (Properties ctx, int AD_Plugin_Detail_ID, String trxName)
{
super (ctx, AD_Plugin_Detail_ID, trxName);
/** if (AD_Plugin_Detail_ID == 0)
{
setAD_Plugin_Detail_ID (0);
setAD_Plugin_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_Plugin_Detail (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Plugin_Detail");

/** TableName=AD_Plugin_Detail */
public static final String Table_Name="AD_Plugin_Detail";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Plugin_Detail");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Plugin_Detail[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_Plugin_Detail_ID */
public void setAD_Plugin_Detail_ID (int AD_Plugin_Detail_ID)
{
set_ValueNoCheck ("AD_Plugin_Detail_ID", new Integer(AD_Plugin_Detail_ID));
}
/** Get AD_Plugin_Detail_ID */
public int getAD_Plugin_Detail_ID() 
{
Integer ii = (Integer)get_Value("AD_Plugin_Detail_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_Plugin_ID */
public void setAD_Plugin_ID (int AD_Plugin_ID)
{
set_Value ("AD_Plugin_ID", new Integer(AD_Plugin_ID));
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
/** Set Component_First_Changelog */
public void setComponent_First_Changelog (String Component_First_Changelog)
{
if (Component_First_Changelog != null && Component_First_Changelog.length() > 50)
{
log.warning("Length > 50 - truncated");
Component_First_Changelog = Component_First_Changelog.substring(0,50);
}
set_Value ("Component_First_Changelog", Component_First_Changelog);
}
/** Get Component_First_Changelog */
public String getComponent_First_Changelog() 
{
return (String)get_Value("Component_First_Changelog");
}
/** Set Component_Last_Changelog */
public void setComponent_Last_Changelog (String Component_Last_Changelog)
{
if (Component_Last_Changelog != null && Component_Last_Changelog.length() > 50)
{
log.warning("Length > 50 - truncated");
Component_Last_Changelog = Component_Last_Changelog.substring(0,50);
}
set_Value ("Component_Last_Changelog", Component_Last_Changelog);
}
/** Get Component_Last_Changelog */
public String getComponent_Last_Changelog() 
{
return (String)get_Value("Component_Last_Changelog");
}
/** Set Install_Details */
public void setInstall_Details (String Install_Details)
{
if (Install_Details != null && Install_Details.length() > 2147483647)
{
log.warning("Length > 2147483647 - truncated");
Install_Details = Install_Details.substring(0,2147483647);
}
set_Value ("Install_Details", Install_Details);
}
/** Get Install_Details */
public String getInstall_Details() 
{
return (String)get_Value("Install_Details");
}
/** Set Version.
Version of the table definition */
public void setVersion (String Version)
{
if (Version != null && Version.length() > 10)
{
log.warning("Length > 10 - truncated");
Version = Version.substring(0,10);
}
set_Value ("Version", Version);
}
/** Get Version.
Version of the table definition */
public String getVersion() 
{
return (String)get_Value("Version");
}
}
