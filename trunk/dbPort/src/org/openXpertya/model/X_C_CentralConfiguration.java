/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CentralConfiguration
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-11-26 10:41:33.734 */
public class X_C_CentralConfiguration extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CentralConfiguration (Properties ctx, int C_CentralConfiguration_ID, String trxName)
{
super (ctx, C_CentralConfiguration_ID, trxName);
/** if (C_CentralConfiguration_ID == 0)
{
setAD_ReplicationHost_ID (0);
setC_CentralConfiguration_ID (0);
setIsControlActivated (false);
setIsRemoteCreditControl (false);
setIsRemoteObtainControl (false);
}
 */
}
/** Load Constructor */
public X_C_CentralConfiguration (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CentralConfiguration");

/** TableName=C_CentralConfiguration */
public static final String Table_Name="C_CentralConfiguration";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CentralConfiguration");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CentralConfiguration[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ReplicationHost_ID */
public void setAD_ReplicationHost_ID (int AD_ReplicationHost_ID)
{
set_Value ("AD_ReplicationHost_ID", new Integer(AD_ReplicationHost_ID));
}
/** Get AD_ReplicationHost_ID */
public int getAD_ReplicationHost_ID() 
{
Integer ii = (Integer)get_Value("AD_ReplicationHost_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Alternative Central Address.
Alternative Central Address */
public void setAlternativeCentralAddress (String AlternativeCentralAddress)
{
if (AlternativeCentralAddress != null && AlternativeCentralAddress.length() > 255)
{
log.warning("Length > 255 - truncated");
AlternativeCentralAddress = AlternativeCentralAddress.substring(0,255);
}
set_Value ("AlternativeCentralAddress", AlternativeCentralAddress);
}
/** Get Alternative Central Address.
Alternative Central Address */
public String getAlternativeCentralAddress() 
{
return (String)get_Value("AlternativeCentralAddress");
}
/** Set C_CentralConfiguration_ID */
public void setC_CentralConfiguration_ID (int C_CentralConfiguration_ID)
{
set_ValueNoCheck ("C_CentralConfiguration_ID", new Integer(C_CentralConfiguration_ID));
}
/** Get C_CentralConfiguration_ID */
public int getC_CentralConfiguration_ID() 
{
Integer ii = (Integer)get_Value("C_CentralConfiguration_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Is Control Activated */
public void setIsControlActivated (boolean IsControlActivated)
{
set_Value ("IsControlActivated", new Boolean(IsControlActivated));
}
/** Get Is Control Activated */
public boolean isControlActivated() 
{
Object oo = get_Value("IsControlActivated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Remote Credit Control */
public void setIsRemoteCreditControl (boolean IsRemoteCreditControl)
{
set_Value ("IsRemoteCreditControl", new Boolean(IsRemoteCreditControl));
}
/** Get Is Remote Credit Control */
public boolean isRemoteCreditControl() 
{
Object oo = get_Value("IsRemoteCreditControl");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Remote Obtain Control */
public void setIsRemoteObtainControl (boolean IsRemoteObtainControl)
{
set_Value ("IsRemoteObtainControl", new Boolean(IsRemoteObtainControl));
}
/** Get Is Remote Obtain Control */
public boolean isRemoteObtainControl() 
{
Object oo = get_Value("IsRemoteObtainControl");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int MANAGEACTIVATION_AD_Reference_ID = MReference.getReferenceID("Activation/Deactivation CCAC");
/** Centralized Control Activation = A */
public static final String MANAGEACTIVATION_CentralizedControlActivation = "A";
/** Centralized Control Deactivation = D */
public static final String MANAGEACTIVATION_CentralizedControlDeactivation = "D";
/** Set Manage Activation */
public void setManageActivation (String ManageActivation)
{
if (ManageActivation == null || ManageActivation.equals("A") || ManageActivation.equals("D"));
 else throw new IllegalArgumentException ("ManageActivation Invalid value - Reference = MANAGEACTIVATION_AD_Reference_ID - A - D");
if (ManageActivation != null && ManageActivation.length() > 1)
{
log.warning("Length > 1 - truncated");
ManageActivation = ManageActivation.substring(0,1);
}
set_Value ("ManageActivation", ManageActivation);
}
/** Get Manage Activation */
public String getManageActivation() 
{
return (String)get_Value("ManageActivation");
}
}
