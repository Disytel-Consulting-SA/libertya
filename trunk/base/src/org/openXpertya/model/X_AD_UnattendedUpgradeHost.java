/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_UnattendedUpgradeHost
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-12-04 12:46:14.841 */
public class X_AD_UnattendedUpgradeHost extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_UnattendedUpgradeHost (Properties ctx, int AD_UnattendedUpgradeHost_ID, String trxName)
{
super (ctx, AD_UnattendedUpgradeHost_ID, trxName);
/** if (AD_UnattendedUpgradeHost_ID == 0)
{
setAD_UnattendedUpgradeHost_ID (0);
setAD_UnattendedUpgrade_ID (0);
setStatus (null);	// P
}
 */
}
/** Load Constructor */
public X_AD_UnattendedUpgradeHost (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_UnattendedUpgradeHost");

/** TableName=AD_UnattendedUpgradeHost */
public static final String Table_Name="AD_UnattendedUpgradeHost";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_UnattendedUpgradeHost");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_UnattendedUpgradeHost[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_UnattendedUpgradeHost_ID */
public void setAD_UnattendedUpgradeHost_ID (int AD_UnattendedUpgradeHost_ID)
{
set_ValueNoCheck ("AD_UnattendedUpgradeHost_ID", new Integer(AD_UnattendedUpgradeHost_ID));
}
/** Get AD_UnattendedUpgradeHost_ID */
public int getAD_UnattendedUpgradeHost_ID() 
{
Integer ii = (Integer)get_Value("AD_UnattendedUpgradeHost_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_UnattendedUpgrade_ID */
public void setAD_UnattendedUpgrade_ID (int AD_UnattendedUpgrade_ID)
{
set_Value ("AD_UnattendedUpgrade_ID", new Integer(AD_UnattendedUpgrade_ID));
}
/** Get AD_UnattendedUpgrade_ID */
public int getAD_UnattendedUpgrade_ID() 
{
Integer ii = (Integer)get_Value("AD_UnattendedUpgrade_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Error Msg */
public void setErrorMsg (String ErrorMsg)
{
if (ErrorMsg != null && ErrorMsg.length() > 500)
{
log.warning("Length > 500 - truncated");
ErrorMsg = ErrorMsg.substring(0,500);
}
set_Value ("ErrorMsg", ErrorMsg);
}
/** Get Error Msg */
public String getErrorMsg() 
{
return (String)get_Value("ErrorMsg");
}
public static final int STATUS_AD_Reference_ID = MReference.getReferenceID("Unnatended Upgade Host Status");
/** Pending = P */
public static final String STATUS_Pending = "P";
/** OK = O */
public static final String STATUS_OK = "O";
/** Error = E */
public static final String STATUS_Error = "E";
/** Set Status */
public void setStatus (String Status)
{
if (Status.equals("P") || Status.equals("O") || Status.equals("E"));
 else throw new IllegalArgumentException ("Status Invalid value - Reference = STATUS_AD_Reference_ID - P - O - E");
if (Status == null) throw new IllegalArgumentException ("Status is mandatory");
if (Status.length() > 1)
{
log.warning("Length > 1 - truncated");
Status = Status.substring(0,1);
}
set_Value ("Status", Status);
}
/** Get Status */
public String getStatus() 
{
return (String)get_Value("Status");
}
/** Set Upgraded */
public void setUpgraded (Timestamp Upgraded)
{
set_Value ("Upgraded", Upgraded);
}
/** Get Upgraded */
public Timestamp getUpgraded() 
{
return (Timestamp)get_Value("Upgraded");
}
}
