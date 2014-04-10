/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_TransferLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-04-10 17:28:14.12 */
public class X_M_TransferLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_TransferLine (Properties ctx, int M_TransferLine_ID, String trxName)
{
super (ctx, M_TransferLine_ID, trxName);
/** if (M_TransferLine_ID == 0)
{
setConfirmedQty (Env.ZERO);	// 0
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_TransferLine WHERE M_Transfer_ID=@M_Transfer_ID@
setM_Locator_ID (0);	// @SQL=SELECT m_locator_id FROM m_locator where m_warehouse_id = @M_Warehouse_ID@ order by isdefault desc limit 1
setM_Product_ID (0);
setM_Transfer_ID (0);	// @M_Transfer_ID@
setM_TransferLine_ID (0);
setProcessed (false);
setQty (Env.ZERO);	// 0
}
 */
}
/** Load Constructor */
public X_M_TransferLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_TransferLine");

/** TableName=M_TransferLine */
public static final String Table_Name="M_TransferLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_TransferLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_TransferLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Confirmed Quantity.
Confirmation of a received quantity */
public void setConfirmedQty (BigDecimal ConfirmedQty)
{
if (ConfirmedQty == null) throw new IllegalArgumentException ("ConfirmedQty is mandatory");
set_Value ("ConfirmedQty", ConfirmedQty);
}
/** Get Confirmed Quantity.
Confirmation of a received quantity */
public BigDecimal getConfirmedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ConfirmedQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line No.
Unique line for this document */
public void setLine (int Line)
{
set_Value ("Line", new Integer(Line));
}
/** Get Line No.
Unique line for this document */
public int getLine() 
{
Integer ii = (Integer)get_Value("Line");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Locator.
Warehouse Locator */
public void setM_Locator_ID (int M_Locator_ID)
{
set_Value ("M_Locator_ID", new Integer(M_Locator_ID));
}
/** Get Locator.
Warehouse Locator */
public int getM_Locator_ID() 
{
Integer ii = (Integer)get_Value("M_Locator_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Target Locator.
Target Locator */
public void setM_Locator_To_ID (int M_Locator_To_ID)
{
if (M_Locator_To_ID <= 0) set_Value ("M_Locator_To_ID", null);
 else 
set_Value ("M_Locator_To_ID", new Integer(M_Locator_To_ID));
}
/** Get Target Locator.
Target Locator */
public int getM_Locator_To_ID() 
{
Integer ii = (Integer)get_Value("M_Locator_To_ID");
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
/** Set Material Transfer.
Material Transfer */
public void setM_Transfer_ID (int M_Transfer_ID)
{
set_Value ("M_Transfer_ID", new Integer(M_Transfer_ID));
}
/** Get Material Transfer.
Material Transfer */
public int getM_Transfer_ID() 
{
Integer ii = (Integer)get_Value("M_Transfer_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Material Transfer Line.
Material Transfer Line */
public void setM_TransferLine_ID (int M_TransferLine_ID)
{
set_ValueNoCheck ("M_TransferLine_ID", new Integer(M_TransferLine_ID));
}
/** Get Material Transfer Line.
Material Transfer Line */
public int getM_TransferLine_ID() 
{
Integer ii = (Integer)get_Value("M_TransferLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
