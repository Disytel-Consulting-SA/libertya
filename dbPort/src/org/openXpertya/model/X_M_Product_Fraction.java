/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Product_Fraction
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-11-24 14:06:01.221 */
public class X_M_Product_Fraction extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Product_Fraction (Properties ctx, int M_Product_Fraction_ID, String trxName)
{
super (ctx, M_Product_Fraction_ID, trxName);
/** if (M_Product_Fraction_ID == 0)
{
setM_Product_Fraction_ID (0);
setM_Product_ID (0);
setM_Product_To_ID (0);
}
 */
}
/** Load Constructor */
public X_M_Product_Fraction (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Product_Fraction");

/** TableName=M_Product_Fraction */
public static final String Table_Name="M_Product_Fraction";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Product_Fraction");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Product_Fraction[").append(getID()).append("]");
return sb.toString();
}
/** Set Product Fraction */
public void setM_Product_Fraction_ID (int M_Product_Fraction_ID)
{
set_ValueNoCheck ("M_Product_Fraction_ID", new Integer(M_Product_Fraction_ID));
}
/** Get Product Fraction */
public int getM_Product_Fraction_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Fraction_ID");
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
public static final int M_PRODUCT_TO_ID_AD_Reference_ID = MReference.getReferenceID("M_Product (no summary)");
/** Set Splitting Target Product.
Splitting Target Product */
public void setM_Product_To_ID (int M_Product_To_ID)
{
set_Value ("M_Product_To_ID", new Integer(M_Product_To_ID));
}
/** Get Splitting Target Product.
Splitting Target Product */
public int getM_Product_To_ID() 
{
Integer ii = (Integer)get_Value("M_Product_To_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
