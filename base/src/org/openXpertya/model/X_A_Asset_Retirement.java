/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por A_Asset_Retirement
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.781 */
public class X_A_Asset_Retirement extends PO
{
/** Constructor estándar */
public X_A_Asset_Retirement (Properties ctx, int A_Asset_Retirement_ID, String trxName)
{
super (ctx, A_Asset_Retirement_ID, trxName);
/** if (A_Asset_Retirement_ID == 0)
{
setA_Asset_ID (0);
setA_Asset_Retirement_ID (0);
setAssetMarketValueAmt (Env.ZERO);
setAssetValueAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_A_Asset_Retirement (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=540 */
public static final int Table_ID=540;

/** TableName=A_Asset_Retirement */
public static final String Table_Name="A_Asset_Retirement";

protected static KeyNamePair Model = new KeyNamePair(540,"A_Asset_Retirement");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_A_Asset_Retirement[").append(getID()).append("]");
return sb.toString();
}
/** Set Asset.
Asset used internally or by customers */
public void setA_Asset_ID (int A_Asset_ID)
{
set_ValueNoCheck ("A_Asset_ID", new Integer(A_Asset_ID));
}
/** Get Asset.
Asset used internally or by customers */
public int getA_Asset_ID() 
{
Integer ii = (Integer)get_Value("A_Asset_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Asset Retirement.
Internally used asset is not longer used. */
public void setA_Asset_Retirement_ID (int A_Asset_Retirement_ID)
{
set_ValueNoCheck ("A_Asset_Retirement_ID", new Integer(A_Asset_Retirement_ID));
}
/** Get Asset Retirement.
Internally used asset is not longer used. */
public int getA_Asset_Retirement_ID() 
{
Integer ii = (Integer)get_Value("A_Asset_Retirement_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getA_Asset_Retirement_ID()));
}
/** Set Market value Amount.
Market value of the asset */
public void setAssetMarketValueAmt (BigDecimal AssetMarketValueAmt)
{
if (AssetMarketValueAmt == null) throw new IllegalArgumentException ("AssetMarketValueAmt is mandatory");
set_Value ("AssetMarketValueAmt", AssetMarketValueAmt);
}
/** Get Market value Amount.
Market value of the asset */
public BigDecimal getAssetMarketValueAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("AssetMarketValueAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Asset value.
Book Value of the asset */
public void setAssetValueAmt (BigDecimal AssetValueAmt)
{
if (AssetValueAmt == null) throw new IllegalArgumentException ("AssetValueAmt is mandatory");
set_Value ("AssetValueAmt", AssetValueAmt);
}
/** Get Asset value.
Book Value of the asset */
public BigDecimal getAssetValueAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("AssetValueAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Invoice Line.
Invoice Detail Line */
public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
{
if (C_InvoiceLine_ID <= 0) set_Value ("C_InvoiceLine_ID", null);
 else 
set_Value ("C_InvoiceLine_ID", new Integer(C_InvoiceLine_ID));
}
/** Get Invoice Line.
Invoice Detail Line */
public int getC_InvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
