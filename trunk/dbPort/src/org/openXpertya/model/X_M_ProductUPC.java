/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_ProductUPC
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-03-22 15:08:29.718 */
public class X_M_ProductUPC extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_ProductUPC (Properties ctx, int M_ProductUPC_ID, String trxName)
{
super (ctx, M_ProductUPC_ID, trxName);
/** if (M_ProductUPC_ID == 0)
{
setIsDefault (false);	// N
setM_Product_ID (0);	// @M_Product_ID@
setM_ProductUPC_ID (0);
setUPC (null);
}
 */
}
/** Load Constructor */
public X_M_ProductUPC (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_ProductUPC");

/** TableName=M_ProductUPC */
public static final String Table_Name="M_ProductUPC";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_ProductUPC");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_ProductUPC[").append(getID()).append("]");
return sb.toString();
}
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
set_ValueNoCheck ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product UPC.
Product UPC */
public void setM_ProductUPC_ID (int M_ProductUPC_ID)
{
set_Value ("M_ProductUPC_ID", new Integer(M_ProductUPC_ID));
}
/** Get Product UPC.
Product UPC */
public int getM_ProductUPC_ID() 
{
Integer ii = (Integer)get_Value("M_ProductUPC_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public void setUPC (String UPC)
{
if (UPC == null) throw new IllegalArgumentException ("UPC is mandatory");
if (UPC.length() > 30)
{
log.warning("Length > 30 - truncated");
UPC = UPC.substring(0,30);
}
set_Value ("UPC", UPC);
}
/** Get UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public String getUPC() 
{
return (String)get_Value("UPC");
}
}
