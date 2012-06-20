/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Order_Node_Asset
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.218 */
public class X_MPC_Order_Node_Asset extends PO
{
/** Constructor estándar */
public X_MPC_Order_Node_Asset (Properties ctx, int MPC_Order_Node_Asset_ID, String trxName)
{
super (ctx, MPC_Order_Node_Asset_ID, trxName);
/** if (MPC_Order_Node_Asset_ID == 0)
{
setA_Asset_ID (0);
setMPC_Order_ID (0);
setMPC_Order_Node_Asset_ID (0);
setMPC_Order_Node_ID (0);
setMPC_Order_Workflow_ID (0);
}
 */
}
/** Load Constructor */
public X_MPC_Order_Node_Asset (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000011 */
public static final int Table_ID=1000011;

/** TableName=MPC_Order_Node_Asset */
public static final String Table_Name="MPC_Order_Node_Asset";

protected static KeyNamePair Model = new KeyNamePair(1000011,"MPC_Order_Node_Asset");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Order_Node_Asset[").append(getID()).append("]");
return sb.toString();
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
/** Set Order Node Asset ID */
public void setMPC_Order_Node_Asset_ID (int MPC_Order_Node_Asset_ID)
{
set_ValueNoCheck ("MPC_Order_Node_Asset_ID", new Integer(MPC_Order_Node_Asset_ID));
}
/** Get Order Node Asset ID */
public int getMPC_Order_Node_Asset_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_Node_Asset_ID");
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
/** Set Order Workflow */
public void setMPC_Order_Workflow_ID (int MPC_Order_Workflow_ID)
{
set_ValueNoCheck ("MPC_Order_Workflow_ID", new Integer(MPC_Order_Workflow_ID));
}
/** Get Order Workflow */
public int getMPC_Order_Workflow_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_Workflow_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
