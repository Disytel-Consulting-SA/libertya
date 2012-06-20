/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Warehouse_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.078 */
public class X_M_Warehouse_Acct extends PO
{
/** Constructor estándar */
public X_M_Warehouse_Acct (Properties ctx, int M_Warehouse_Acct_ID, String trxName)
{
super (ctx, M_Warehouse_Acct_ID, trxName);
/** if (M_Warehouse_Acct_ID == 0)
{
setC_AcctSchema_ID (0);
setM_Warehouse_ID (0);
setW_Differences_Acct (0);
setW_InvActualAdjust_Acct (0);
setW_Inventory_Acct (0);
setW_Revaluation_Acct (0);
}
 */
}
/** Load Constructor */
public X_M_Warehouse_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=191 */
public static final int Table_ID=191;

/** TableName=M_Warehouse_Acct */
public static final String Table_Name="M_Warehouse_Acct";

protected static KeyNamePair Model = new KeyNamePair(191,"M_Warehouse_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Warehouse_Acct[").append(getID()).append("]");
return sb.toString();
}
/** Set Accounting Schema.
Rules for accounting */
public void setC_AcctSchema_ID (int C_AcctSchema_ID)
{
set_ValueNoCheck ("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
}
/** Get Accounting Schema.
Rules for accounting */
public int getC_AcctSchema_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_ValueNoCheck ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Warehouse Differences.
Warehouse Differences Account */
public void setW_Differences_Acct (int W_Differences_Acct)
{
set_Value ("W_Differences_Acct", new Integer(W_Differences_Acct));
}
/** Get Warehouse Differences.
Warehouse Differences Account */
public int getW_Differences_Acct() 
{
Integer ii = (Integer)get_Value("W_Differences_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Inventory Adjustment.
Account for Inventory value adjustments for Actual Costing */
public void setW_InvActualAdjust_Acct (int W_InvActualAdjust_Acct)
{
set_Value ("W_InvActualAdjust_Acct", new Integer(W_InvActualAdjust_Acct));
}
/** Get Inventory Adjustment.
Account for Inventory value adjustments for Actual Costing */
public int getW_InvActualAdjust_Acct() 
{
Integer ii = (Integer)get_Value("W_InvActualAdjust_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set (Not Used).
Warehouse Inventory Asset Account - Currently not used */
public void setW_Inventory_Acct (int W_Inventory_Acct)
{
set_Value ("W_Inventory_Acct", new Integer(W_Inventory_Acct));
}
/** Get (Not Used).
Warehouse Inventory Asset Account - Currently not used */
public int getW_Inventory_Acct() 
{
Integer ii = (Integer)get_Value("W_Inventory_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Inventory Revaluation.
Account for Inventory Revaluation */
public void setW_Revaluation_Acct (int W_Revaluation_Acct)
{
set_Value ("W_Revaluation_Acct", new Integer(W_Revaluation_Acct));
}
/** Get Inventory Revaluation.
Account for Inventory Revaluation */
public int getW_Revaluation_Acct() 
{
Integer ii = (Integer)get_Value("W_Revaluation_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
