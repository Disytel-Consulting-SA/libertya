/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_WF_NodeNext
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:29.089 */
public class X_AD_WF_NodeNext extends PO
{
/** Constructor estándar */
public X_AD_WF_NodeNext (Properties ctx, int AD_WF_NodeNext_ID, String trxName)
{
super (ctx, AD_WF_NodeNext_ID, trxName);
/** if (AD_WF_NodeNext_ID == 0)
{
setAD_WF_Next_ID (0);
setAD_WF_Node_ID (0);
setAD_WF_NodeNext_ID (0);
setEntityType (null);	// U
setIsStdUserWorkflow (false);
setSeqNo (0);	// 10
setTRANSITIONTYPE (false);
}
 */
}
/** Load Constructor */
public X_AD_WF_NodeNext (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=131 */
public static final int Table_ID=131;

/** TableName=AD_WF_NodeNext */
public static final String Table_Name="AD_WF_NodeNext";

protected static KeyNamePair Model = new KeyNamePair(131,"AD_WF_NodeNext");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_WF_NodeNext[").append(getID()).append("]");
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
public static final int AD_WF_NEXT_ID_AD_Reference_ID=109;
/** Set Next Node.
Next Node in workflow */
public void setAD_WF_Next_ID (int AD_WF_Next_ID)
{
set_Value ("AD_WF_Next_ID", new Integer(AD_WF_Next_ID));
}
/** Get Next Node.
Next Node in workflow */
public int getAD_WF_Next_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_Next_ID");
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
/** Set Node Transition.
Workflow Node Transition */
public void setAD_WF_NodeNext_ID (int AD_WF_NodeNext_ID)
{
set_ValueNoCheck ("AD_WF_NodeNext_ID", new Integer(AD_WF_NodeNext_ID));
}
/** Get Node Transition.
Workflow Node Transition */
public int getAD_WF_NodeNext_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_NodeNext_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Std User Workflow.
Standard Manual User Approval Workflow */
public void setIsStdUserWorkflow (boolean IsStdUserWorkflow)
{
set_Value ("IsStdUserWorkflow", new Boolean(IsStdUserWorkflow));
}
/** Get Std User Workflow.
Standard Manual User Approval Workflow */
public boolean isStdUserWorkflow() 
{
Object oo = get_Value("IsStdUserWorkflow");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Transition Code.
Code resulting in TRUE of FALSE */
public void setTransitionCode (String TransitionCode)
{
if (TransitionCode != null && TransitionCode.length() > 2000)
{
log.warning("Length > 2000 - truncated");
TransitionCode = TransitionCode.substring(0,2000);
}
set_Value ("TransitionCode", TransitionCode);
}
/** Get Transition Code.
Code resulting in TRUE of FALSE */
public String getTransitionCode() 
{
return (String)get_Value("TransitionCode");
}
/** Set TRANSITIONTYPE */
public void setTRANSITIONTYPE (boolean TRANSITIONTYPE)
{
set_Value ("TRANSITIONTYPE", new Boolean(TRANSITIONTYPE));
}
/** Get TRANSITIONTYPE */
public boolean isTRANSITIONTYPE() 
{
Object oo = get_Value("TRANSITIONTYPE");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
