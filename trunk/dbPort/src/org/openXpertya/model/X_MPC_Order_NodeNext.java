/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Order_NodeNext
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.187 */
public class X_MPC_Order_NodeNext extends PO
{
/** Constructor estándar */
public X_MPC_Order_NodeNext (Properties ctx, int MPC_Order_NodeNext_ID, String trxName)
{
super (ctx, MPC_Order_NodeNext_ID, trxName);
/** if (MPC_Order_NodeNext_ID == 0)
{
setAD_WF_Node_ID (0);
setEntityType (null);
setMPC_Order_ID (0);
setMPC_Order_Next_ID (0);
setMPC_Order_Node_ID (0);
setSeqNo (0);	// 10
}
 */
}
/** Load Constructor */
public X_MPC_Order_NodeNext (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000010 */
public static final int Table_ID=1000010;

/** TableName=MPC_Order_NodeNext */
public static final String Table_Name="MPC_Order_NodeNext";

protected static KeyNamePair Model = new KeyNamePair(1000010,"MPC_Order_NodeNext");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Order_NodeNext[").append(getID()).append("]");
return sb.toString();
}
/** Set Next Node.
Next Node in workflow */
public void setAD_WF_Next_ID (int AD_WF_Next_ID)
{
if (AD_WF_Next_ID <= 0) set_Value ("AD_WF_Next_ID", null);
 else 
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
set_Value ("AD_WF_Node_ID", new Integer(AD_WF_Node_ID));
}
/** Get Node.
Workflow Node (activity), step or process */
public int getAD_WF_Node_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_Node_ID");
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
Description = Description.substring(0,254);
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
/** Customization = CUST */
public static final String ENTITYTYPE_Customization = "CUST";
/** Dictionary = D */
public static final String ENTITYTYPE_Dictionary = "D";
/** User maintained = U */
public static final String ENTITYTYPE_UserMaintained = "U";
/** Applications Integrated with openXpertya = A */
public static final String ENTITYTYPE_ApplicationsIntegratedWithOpenXpertya = "A";
/** Country Version = C */
public static final String ENTITYTYPE_CountryVersion = "C";
/** Set Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public void setEntityType (String EntityType)
{
if (EntityType.equals("CUST") || EntityType.equals("D") || EntityType.equals("U") || EntityType.equals("A") || EntityType.equals("C"));
 else throw new IllegalArgumentException ("EntityType Invalid value - Reference_ID=245 - CUST - D - U - A - C");
if (EntityType == null) throw new IllegalArgumentException ("EntityType is mandatory");
if (EntityType.length() > 1)
{
log.warning("Length > 1 - truncated");
EntityType = EntityType.substring(0,0);
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
/** Set Manufacturing Order.
Manufacturing Order */
public void setMPC_Order_ID (int MPC_Order_ID)
{
set_ValueNoCheck ("MPC_Order_ID", new Integer(MPC_Order_ID));
}
/** Get Manufacturing Order.
Manufacturing Order */
public int getMPC_Order_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int MPC_ORDER_NEXT_ID_AD_Reference_ID=1000039;
/** Set Order Next ID */
public void setMPC_Order_Next_ID (int MPC_Order_Next_ID)
{
set_Value ("MPC_Order_Next_ID", new Integer(MPC_Order_Next_ID));
}
/** Get Order Next ID */
public int getMPC_Order_Next_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_Next_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set MPC_Order_NodeNext_ID */
public void setMPC_Order_NodeNext_ID (int MPC_Order_NodeNext_ID)
{
if (MPC_Order_NodeNext_ID <= 0) set_ValueNoCheck ("MPC_Order_NodeNext_ID", null);
 else 
set_ValueNoCheck ("MPC_Order_NodeNext_ID", new Integer(MPC_Order_NodeNext_ID));
}
/** Get MPC_Order_NodeNext_ID */
public int getMPC_Order_NodeNext_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_NodeNext_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order Node ID */
public void setMPC_Order_Node_ID (int MPC_Order_Node_ID)
{
set_ValueNoCheck ("MPC_Order_Node_ID", new Integer(MPC_Order_Node_ID));
}
/** Get Order Node ID */
public int getMPC_Order_Node_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_Node_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SEQNO_AD_Reference_ID=110;
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
TransitionCode = TransitionCode.substring(0,1999);
}
set_Value ("TransitionCode", TransitionCode);
}
/** Get Transition Code.
Code resulting in TRUE of FALSE */
public String getTransitionCode() 
{
return (String)get_Value("TransitionCode");
}
}
