/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_ComboLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-07-14 14:03:43.118 */
public class X_C_ComboLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_ComboLine (Properties ctx, int C_ComboLine_ID, String trxName)
{
super (ctx, C_ComboLine_ID, trxName);
/** if (C_ComboLine_ID == 0)
{
setC_Combo_ID (0);	// @C_Combo_ID@
setC_ComboLine_ID (0);
setDiscount (Env.ZERO);
setM_Product_ID (0);
setQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_ComboLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_ComboLine");

/** TableName=C_ComboLine */
public static final String Table_Name="C_ComboLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_ComboLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ComboLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Products Combo.
Products Combo */
public void setC_Combo_ID (int C_Combo_ID)
{
set_Value ("C_Combo_ID", new Integer(C_Combo_ID));
}
/** Get Products Combo.
Products Combo */
public int getC_Combo_ID() 
{
Integer ii = (Integer)get_Value("C_Combo_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Combo_ID()));
}
/** Set Products Combo Line.
Products Combo Line */
public void setC_ComboLine_ID (int C_ComboLine_ID)
{
set_ValueNoCheck ("C_ComboLine_ID", new Integer(C_ComboLine_ID));
}
/** Get Products Combo Line.
Products Combo Line */
public int getC_ComboLine_ID() 
{
Integer ii = (Integer)get_Value("C_ComboLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Discount %.
Discount in percent */
public void setDiscount (BigDecimal Discount)
{
if (Discount == null) throw new IllegalArgumentException ("Discount is mandatory");
set_Value ("Discount", Discount);
}
/** Get Discount %.
Discount in percent */
public BigDecimal getDiscount() 
{
BigDecimal bd = (BigDecimal)get_Value("Discount");
if (bd == null) return Env.ZERO;
return bd;
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
