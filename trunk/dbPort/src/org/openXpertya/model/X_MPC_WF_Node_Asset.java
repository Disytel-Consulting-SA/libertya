/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_WF_Node_Asset
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.843 */
public class X_MPC_WF_Node_Asset extends PO
{
/** Constructor estándar */
public X_MPC_WF_Node_Asset (Properties ctx, int MPC_WF_Node_Asset_ID, String trxName)
{
super (ctx, MPC_WF_Node_Asset_ID, trxName);
/** if (MPC_WF_Node_Asset_ID == 0)
{
setAD_WF_Node_ID (0);
setAD_Workflow_ID (0);
setA_Asset_ID (0);
setMPC_WF_Node_Asset_ID (0);
}
 */
}
/** Load Constructor */
public X_MPC_WF_Node_Asset (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000024 */
public static final int Table_ID=1000024;

/** TableName=MPC_WF_Node_Asset */
public static final String Table_Name="MPC_WF_Node_Asset";

protected static KeyNamePair Model = new KeyNamePair(1000024,"MPC_WF_Node_Asset");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_WF_Node_Asset[").append(getID()).append("]");
return sb.toString();
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
/** Set Workflow.
Workflow or combination of tasks */
public void setAD_Workflow_ID (int AD_Workflow_ID)
{
set_ValueNoCheck ("AD_Workflow_ID", new Integer(AD_Workflow_ID));
}
/** Get Workflow.
Workflow or combination of tasks */
public int getAD_Workflow_ID() 
{
Integer ii = (Integer)get_Value("AD_Workflow_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Asset.
Asset used internally or by customers */
public void setA_Asset_ID (int A_Asset_ID)
{
set_Value ("A_Asset_ID", new Integer(A_Asset_ID));
}
/** Get Asset.
Asset used internally or by customers */
public int getA_Asset_ID() 
{
Integer ii = (Integer)get_Value("A_Asset_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Node Asset */
public void setMPC_WF_Node_Asset_ID (int MPC_WF_Node_Asset_ID)
{
set_ValueNoCheck ("MPC_WF_Node_Asset_ID", new Integer(MPC_WF_Node_Asset_ID));
}
/** Get Node Asset */
public int getMPC_WF_Node_Asset_ID() 
{
Integer ii = (Integer)get_Value("MPC_WF_Node_Asset_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
