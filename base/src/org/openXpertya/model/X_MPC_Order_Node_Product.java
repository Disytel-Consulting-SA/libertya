/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Order_Node_Product
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.234 */
public class X_MPC_Order_Node_Product extends PO
{
/** Constructor estándar */
public X_MPC_Order_Node_Product (Properties ctx, int MPC_Order_Node_Product_ID, String trxName)
{
super (ctx, MPC_Order_Node_Product_ID, trxName);
/** if (MPC_Order_Node_Product_ID == 0)
{
setMPC_Order_ID (0);
setMPC_Order_Node_ID (0);
setMPC_Order_Node_Product_ID (0);
setMPC_Order_Workflow_ID (0);
setM_Product_ID (0);
}
 */
}
/** Load Constructor */
public X_MPC_Order_Node_Product (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000012 */
public static final int Table_ID=1000012;

/** TableName=MPC_Order_Node_Product */
public static final String Table_Name="MPC_Order_Node_Product";

protected static KeyNamePair Model = new KeyNamePair(1000012,"MPC_Order_Node_Product");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Order_Node_Product[").append(getID()).append("]");
return sb.toString();
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
/** Set Order Node Product */
public void setMPC_Order_Node_Product_ID (int MPC_Order_Node_Product_ID)
{
set_ValueNoCheck ("MPC_Order_Node_Product_ID", new Integer(MPC_Order_Node_Product_ID));
}
/** Get Order Node Product */
public int getMPC_Order_Node_Product_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_Node_Product_ID");
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
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Yield */
public void setYield (int Yield)
{
set_Value ("Yield", new Integer(Yield));
}
/** Get Yield */
public int getYield() 
{
Integer ii = (Integer)get_Value("Yield");
if (ii == null) return 0;
return ii.intValue();
}
}
