/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RfQResponseLineQty
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.078 */
public class X_C_RfQResponseLineQty extends PO
{
/** Constructor estándar */
public X_C_RfQResponseLineQty (Properties ctx, int C_RfQResponseLineQty_ID, String trxName)
{
super (ctx, C_RfQResponseLineQty_ID, trxName);
/** if (C_RfQResponseLineQty_ID == 0)
{
setC_RfQLineQty_ID (0);
setC_RfQResponseLineQty_ID (0);
setC_RfQResponseLine_ID (0);
setPrice (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_RfQResponseLineQty (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=672 */
public static final int Table_ID=672;

/** TableName=C_RfQResponseLineQty */
public static final String Table_Name="C_RfQResponseLineQty";

protected static KeyNamePair Model = new KeyNamePair(672,"C_RfQResponseLineQty");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RfQResponseLineQty[").append(getID()).append("]");
return sb.toString();
}
/** Set RfQ Line Quantity.
Request for Quotation Line Quantity */
public void setC_RfQLineQty_ID (int C_RfQLineQty_ID)
{
set_ValueNoCheck ("C_RfQLineQty_ID", new Integer(C_RfQLineQty_ID));
}
/** Get RfQ Line Quantity.
Request for Quotation Line Quantity */
public int getC_RfQLineQty_ID() 
{
Integer ii = (Integer)get_Value("C_RfQLineQty_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set RfQ Response Line Qty.
Request for Quotation Response Line Quantity */
public void setC_RfQResponseLineQty_ID (int C_RfQResponseLineQty_ID)
{
set_ValueNoCheck ("C_RfQResponseLineQty_ID", new Integer(C_RfQResponseLineQty_ID));
}
/** Get RfQ Response Line Qty.
Request for Quotation Response Line Quantity */
public int getC_RfQResponseLineQty_ID() 
{
Integer ii = (Integer)get_Value("C_RfQResponseLineQty_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set RfQ Response Line.
Request for Quotation Response Line */
public void setC_RfQResponseLine_ID (int C_RfQResponseLine_ID)
{
set_ValueNoCheck ("C_RfQResponseLine_ID", new Integer(C_RfQResponseLine_ID));
}
/** Get RfQ Response Line.
Request for Quotation Response Line */
public int getC_RfQResponseLine_ID() 
{
Integer ii = (Integer)get_Value("C_RfQResponseLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_RfQResponseLine_ID()));
}
/** Set Discount %.
Discount in percent */
public void setDiscount (BigDecimal Discount)
{
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
/** Set Price.
Price */
public void setPrice (BigDecimal Price)
{
if (Price == null) throw new IllegalArgumentException ("Price is mandatory");
set_Value ("Price", Price);
}
/** Get Price.
Price */
public BigDecimal getPrice() 
{
BigDecimal bd = (BigDecimal)get_Value("Price");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Ranking.
Relative Rank Number */
public void setRanking (int Ranking)
{
set_Value ("Ranking", new Integer(Ranking));
}
/** Get Ranking.
Relative Rank Number */
public int getRanking() 
{
Integer ii = (Integer)get_Value("Ranking");
if (ii == null) return 0;
return ii.intValue();
}
}
