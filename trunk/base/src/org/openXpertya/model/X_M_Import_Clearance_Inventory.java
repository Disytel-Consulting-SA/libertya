/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Import_Clearance_Inventory
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2021-12-20 13:54:09.669 */
public class X_M_Import_Clearance_Inventory extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Import_Clearance_Inventory (Properties ctx, int M_Import_Clearance_Inventory_ID, String trxName)
{
super (ctx, M_Import_Clearance_Inventory_ID, trxName);
/** if (M_Import_Clearance_Inventory_ID == 0)
{
setM_Import_Clearance_ID (0);
setM_Import_Clearance_Inventory_ID (0);
setM_InventoryLine_ID (0);
setQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_Import_Clearance_Inventory (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Import_Clearance_Inventory");

/** TableName=M_Import_Clearance_Inventory */
public static final String Table_Name="M_Import_Clearance_Inventory";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Import_Clearance_Inventory");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Import_Clearance_Inventory[").append(getID()).append("]");
return sb.toString();
}
/** Set Import Clearance */
public void setM_Import_Clearance_ID (int M_Import_Clearance_ID)
{
set_Value ("M_Import_Clearance_ID", new Integer(M_Import_Clearance_ID));
}
/** Get Import Clearance */
public int getM_Import_Clearance_ID() 
{
Integer ii = (Integer)get_Value("M_Import_Clearance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_Import_Clearance_Inventory_ID */
public void setM_Import_Clearance_Inventory_ID (int M_Import_Clearance_Inventory_ID)
{
set_ValueNoCheck ("M_Import_Clearance_Inventory_ID", new Integer(M_Import_Clearance_Inventory_ID));
}
/** Get M_Import_Clearance_Inventory_ID */
public int getM_Import_Clearance_Inventory_ID() 
{
Integer ii = (Integer)get_Value("M_Import_Clearance_Inventory_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Phys.Inventory Line.
Unique line in an Inventory document */
public void setM_InventoryLine_ID (int M_InventoryLine_ID)
{
set_Value ("M_InventoryLine_ID", new Integer(M_InventoryLine_ID));
}
/** Get Phys.Inventory Line.
Unique line in an Inventory document */
public int getM_InventoryLine_ID() 
{
Integer ii = (Integer)get_Value("M_InventoryLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
if (Qty == null) throw new IllegalArgumentException ("Qty is mandatory");
set_Value ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
}
}
