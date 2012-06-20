/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_DistributionRunLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:37.703 */
public class X_M_DistributionRunLine extends PO
{
/** Constructor estándar */
public X_M_DistributionRunLine (Properties ctx, int M_DistributionRunLine_ID, String trxName)
{
super (ctx, M_DistributionRunLine_ID, trxName);
/** if (M_DistributionRunLine_ID == 0)
{
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_DistributionRunLine WHERE M_DistributionRun_ID=@M_DistributionRun_ID@
setM_DistributionList_ID (0);
setM_DistributionRunLine_ID (0);
setM_DistributionRun_ID (0);
setM_Product_ID (0);
setMinQty (Env.ZERO);	// 0
setTotalQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_DistributionRunLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=713 */
public static final int Table_ID=713;

/** TableName=M_DistributionRunLine */
public static final String Table_Name="M_DistributionRunLine";

protected static KeyNamePair Model = new KeyNamePair(713,"M_DistributionRunLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_DistributionRunLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
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
/** Set Distribution List.
Distribution Lists allow to distribute products to a selected list of partners */
public void setM_DistributionList_ID (int M_DistributionList_ID)
{
set_Value ("M_DistributionList_ID", new Integer(M_DistributionList_ID));
}
/** Get Distribution List.
Distribution Lists allow to distribute products to a selected list of partners */
public int getM_DistributionList_ID() 
{
Integer ii = (Integer)get_Value("M_DistributionList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Distribution Run Line.
Distribution Run Lines define Distribution List, the Product and Quantiries */
public void setM_DistributionRunLine_ID (int M_DistributionRunLine_ID)
{
set_ValueNoCheck ("M_DistributionRunLine_ID", new Integer(M_DistributionRunLine_ID));
}
/** Get Distribution Run Line.
Distribution Run Lines define Distribution List, the Product and Quantiries */
public int getM_DistributionRunLine_ID() 
{
Integer ii = (Integer)get_Value("M_DistributionRunLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Distribution Run.
Distribution Run create Orders to distribute products to a selected list of partners */
public void setM_DistributionRun_ID (int M_DistributionRun_ID)
{
set_ValueNoCheck ("M_DistributionRun_ID", new Integer(M_DistributionRun_ID));
}
/** Get Distribution Run.
Distribution Run create Orders to distribute products to a selected list of partners */
public int getM_DistributionRun_ID() 
{
Integer ii = (Integer)get_Value("M_DistributionRun_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_DistributionRun_ID()));
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
/** Set Minimum Quantity.
Minimum quantity for the business partner */
public void setMinQty (BigDecimal MinQty)
{
if (MinQty == null) throw new IllegalArgumentException ("MinQty is mandatory");
set_Value ("MinQty", MinQty);
}
/** Get Minimum Quantity.
Minimum quantity for the business partner */
public BigDecimal getMinQty() 
{
BigDecimal bd = (BigDecimal)get_Value("MinQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Total Quantity.
Total Quantity */
public void setTotalQty (BigDecimal TotalQty)
{
if (TotalQty == null) throw new IllegalArgumentException ("TotalQty is mandatory");
set_Value ("TotalQty", TotalQty);
}
/** Get Total Quantity.
Total Quantity */
public BigDecimal getTotalQty() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalQty");
if (bd == null) return Env.ZERO;
return bd;
}
}
