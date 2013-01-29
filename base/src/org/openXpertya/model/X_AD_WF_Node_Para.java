/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_WF_Node_Para
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:29.106 */
public class X_AD_WF_Node_Para extends PO
{
/** Constructor estándar */
public X_AD_WF_Node_Para (Properties ctx, int AD_WF_Node_Para_ID, String trxName)
{
super (ctx, AD_WF_Node_Para_ID, trxName);
/** if (AD_WF_Node_Para_ID == 0)
{
setAD_WF_Node_ID (0);
setAD_WF_Node_Para_ID (0);
setEntityType (null);	// U
}
 */
}
/** Load Constructor */
public X_AD_WF_Node_Para (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=643 */
public static final int Table_ID=643;

/** TableName=AD_WF_Node_Para */
public static final String Table_Name="AD_WF_Node_Para";

protected static KeyNamePair Model = new KeyNamePair(643,"AD_WF_Node_Para");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_WF_Node_Para[").append(getID()).append("]");
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
/** Set Process Parameter */
public void setAD_Process_Para_ID (int AD_Process_Para_ID)
{
if (AD_Process_Para_ID <= 0) set_Value ("AD_Process_Para_ID", null);
 else 
set_Value ("AD_Process_Para_ID", new Integer(AD_Process_Para_ID));
}
/** Get Process Parameter */
public int getAD_Process_Para_ID() 
{
Integer ii = (Integer)get_Value("AD_Process_Para_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Node.
Workflow Node (activity), step or process */
public void setAD_WF_Node_ID (int AD_WF_Node_ID)
{
set_ValueNoCheck ("AD_WF_Node_ID", new Integer(AD_WF_Node_ID));
}
/** Get Node.
Workflow Node (activity), step or process */
public int getAD_WF_Node_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_Node_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_WF_Node_ID()));
}
/** Set Workflow Node Parameter.
Workflow Node Execution Parameter */
public void setAD_WF_Node_Para_ID (int AD_WF_Node_Para_ID)
{
set_ValueNoCheck ("AD_WF_Node_Para_ID", new Integer(AD_WF_Node_Para_ID));
}
/** Get Workflow Node Parameter.
Workflow Node Execution Parameter */
public int getAD_WF_Node_Para_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_Node_Para_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute Name.
Name of the Attribute */
public void setAttributeName (String AttributeName)
{
if (AttributeName != null && AttributeName.length() > 60)
{
log.warning("Length > 60 - truncated");
AttributeName = AttributeName.substring(0,60);
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
AttributeValue = AttributeValue.substring(0,60);
}
set_Value ("AttributeValue", AttributeValue);
}
/** Get Attribute Value.
Value of the Attribute */
public String getAttributeValue() 
{
return (String)get_Value("AttributeValue");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
public static final int ENTITYTYPE_AD_Reference_ID=245;
/** Applications Integrated with openXpertya = A */
public static final String ENTITYTYPE_ApplicationsIntegratedWithOpenXpertya = "A";
/** Country Version = C */
public static final String ENTITYTYPE_CountryVersion = "C";
/** Dictionary = D */
public static final String ENTITYTYPE_Dictionary = "D";
/** User maintained = U */
public static final String ENTITYTYPE_UserMaintained = "U";
/** Customization = CUST */
public static final String ENTITYTYPE_Customization = "CUST";
/** Set Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public void setEntityType (String EntityType)
{
if (EntityType.equals("A") || EntityType.equals("C") || EntityType.equals("D") || EntityType.equals("U") || EntityType.equals("CUST"));
 else throw new IllegalArgumentException ("EntityType Invalid value - Reference_ID=245 - A - C - D - U - CUST");
if (EntityType == null) throw new IllegalArgumentException ("EntityType is mandatory");
if (EntityType.length() > 4)
{
log.warning("Length > 4 - truncated");
EntityType = EntityType.substring(0,4);
}
set_Value ("EntityType", EntityType);
}
/** Get Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public String getEntityType() 
{
return (String)get_Value("EntityType");
}
}
