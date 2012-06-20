/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_WF_ProcessData
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.281 */
public class X_AD_WF_ProcessData extends PO
{
/** Constructor estándar */
public X_AD_WF_ProcessData (Properties ctx, int AD_WF_ProcessData_ID, String trxName)
{
super (ctx, AD_WF_ProcessData_ID, trxName);
/** if (AD_WF_ProcessData_ID == 0)
{
setAD_WF_ProcessData_ID (0);
setAD_WF_Process_ID (0);
setAttributeName (null);
}
 */
}
/** Load Constructor */
public X_AD_WF_ProcessData (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=648 */
public static final int Table_ID=648;

/** TableName=AD_WF_ProcessData */
public static final String Table_Name="AD_WF_ProcessData";

protected static KeyNamePair Model = new KeyNamePair(648,"AD_WF_ProcessData");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_WF_ProcessData[").append(getID()).append("]");
return sb.toString();
}
/** Set Workflow Process Data.
Workflow Process Context */
public void setAD_WF_ProcessData_ID (int AD_WF_ProcessData_ID)
{
set_ValueNoCheck ("AD_WF_ProcessData_ID", new Integer(AD_WF_ProcessData_ID));
}
/** Get Workflow Process Data.
Workflow Process Context */
public int getAD_WF_ProcessData_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_ProcessData_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Workflow Process.
Actual Workflow Process Instance */
public void setAD_WF_Process_ID (int AD_WF_Process_ID)
{
set_ValueNoCheck ("AD_WF_Process_ID", new Integer(AD_WF_Process_ID));
}
/** Get Workflow Process.
Actual Workflow Process Instance */
public int getAD_WF_Process_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_Process_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_WF_Process_ID()));
}
/** Set Attribute Name.
Name of the Attribute */
public void setAttributeName (String AttributeName)
{
if (AttributeName == null) throw new IllegalArgumentException ("AttributeName is mandatory");
if (AttributeName.length() > 60)
{
log.warning("Length > 60 - truncated");
AttributeName = AttributeName.substring(0,59);
}
set_Value ("AttributeName", AttributeName);
}
/** Get Attribute Name.
Name of the Attribute */
public String getAttributeName() 
{
return (String)get_Value("AttributeName");
}
/** Set Attribute Value.
Value of the Attribute */
public void setAttributeValue (String AttributeValue)
{
if (AttributeValue != null && AttributeValue.length() > 60)
{
log.warning("Length > 60 - truncated");
AttributeValue = AttributeValue.substring(0,59);
}
set_Value ("AttributeValue", AttributeValue);
}
/** Get Attribute Value.
Value of the Attribute */
public String getAttributeValue() 
{
return (String)get_Value("AttributeValue");
}
}
