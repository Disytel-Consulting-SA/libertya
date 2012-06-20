/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_SplittingLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-11-27 13:31:17.159 */
public class X_M_SplittingLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_SplittingLine (Properties ctx, int M_SplittingLine_ID, String trxName)
{
super (ctx, M_SplittingLine_ID, trxName);
/** if (M_SplittingLine_ID == 0)
{
setC_UOM_ID (0);
setM_Locator_ID (0);
setM_Product_To_ID (0);
setM_Splitting_ID (0);
setM_SplittingLine_ID (0);
setProductQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_SplittingLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_SplittingLine");

/** TableName=M_SplittingLine */
public static final String Table_Name="M_SplittingLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_SplittingLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_SplittingLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Converted Quantity.
Converted Quantity */
public void setConvertedQty (BigDecimal ConvertedQty)
{
set_Value ("ConvertedQty", ConvertedQty);
}
/** Get Converted Quantity.
Converted Quantity */
public BigDecimal getConvertedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ConvertedQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
set_Value ("C_UOM_ID", new Integer(C_UOM_ID));
}
/** Get UOM.
Unit of Measure */
public int getC_UOM_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_ID");
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
public static final int M_PRODUCT_TO_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set Product.
Product, Service, Item */
public void setM_Product_To_ID (int M_Product_To_ID)
{
set_Value ("M_Product_To_ID", new Integer(M_Product_To_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_To_ID() 
{
Integer ii = (Integer)get_Value("M_Product_To_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Splitting.
Product Splitting */
public void setM_Splitting_ID (int M_Splitting_ID)
{
set_Value ("M_Splitting_ID", new Integer(M_Splitting_ID));
}
/** Get Splitting.
Product Splitting */
public int getM_Splitting_ID() 
{
Integer ii = (Integer)get_Value("M_Splitting_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Splitting Line.
Splitting Line */
public void setM_SplittingLine_ID (int M_SplittingLine_ID)
{
set_ValueNoCheck ("M_SplittingLine_ID", new Integer(M_SplittingLine_ID));
}
/** Get Splitting Line.
Splitting Line */
public int getM_SplittingLine_ID() 
{
Integer ii = (Integer)get_Value("M_SplittingLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product Quantity.
Product Quantity */
public void setProductQty (BigDecimal ProductQty)
{
if (ProductQty == null) throw new IllegalArgumentException ("ProductQty is mandatory");
set_Value ("ProductQty", ProductQty);
}
/** Get Product Quantity.
Product Quantity */
public BigDecimal getProductQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ProductQty");
if (bd == null) return Env.ZERO;
return bd;
}
}
