/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Product_BOM
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:36.899 */
public class X_M_Product_BOM extends PO
{
/** Constructor estándar */
public X_M_Product_BOM (Properties ctx, int M_Product_BOM_ID, String trxName)
{
super (ctx, M_Product_BOM_ID, trxName);
/** if (M_Product_BOM_ID == 0)
{
setBOMQty (Env.ZERO);	// 1
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_Product_BOM WHERE M_Product_ID=@M_Product_ID@
setM_Product_BOM_ID (0);
setM_ProductBOM_ID (0);
setM_Product_ID (0);
}
 */
}
/** Load Constructor */
public X_M_Product_BOM (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=383 */
public static final int Table_ID=383;

/** TableName=M_Product_BOM */
public static final String Table_Name="M_Product_BOM";

protected static KeyNamePair Model = new KeyNamePair(383,"M_Product_BOM");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Product_BOM[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
/** Set BOM Quantity.
Bill of Materials Quantity */
public void setBOMQty (BigDecimal BOMQty)
{
if (BOMQty == null) throw new IllegalArgumentException ("BOMQty is mandatory");
set_Value ("BOMQty", BOMQty);
}
/** Get BOM Quantity.
Bill of Materials Quantity */
public BigDecimal getBOMQty() 
{
BigDecimal bd = (BigDecimal)get_Value("BOMQty");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int BOMTYPE_AD_Reference_ID=279;
/** Standard Part = P */
public static final String BOMTYPE_StandardPart = "P";
/** Optional Part = O */
public static final String BOMTYPE_OptionalPart = "O";
/** In alternative Group 1 = 1 */
public static final String BOMTYPE_InAlternativeGroup1 = "1";
/** In alternative Group 2 = 2 */
public static final String BOMTYPE_InAlternativeGroup2 = "2";
/** In alternaltve Group 3 = 3 */
public static final String BOMTYPE_InAlternaltveGroup3 = "3";
/** In alternative Group 6 = 6 */
public static final String BOMTYPE_InAlternativeGroup6 = "6";
/** In alternative Group 5 = 5 */
public static final String BOMTYPE_InAlternativeGroup5 = "5";
/** In alternative Group 7 = 7 */
public static final String BOMTYPE_InAlternativeGroup7 = "7";
/** In alternative Group 8 = 8 */
public static final String BOMTYPE_InAlternativeGroup8 = "8";
/** In alternative Group 9 = 9 */
public static final String BOMTYPE_InAlternativeGroup9 = "9";
/** In alternative Group 4 = 4 */
public static final String BOMTYPE_InAlternativeGroup4 = "4";
/** Set BOM Type.
Type of BOM */
public void setBOMType (String BOMType)
{
if (BOMType == null || BOMType.equals("P") || BOMType.equals("O") || BOMType.equals("1") || BOMType.equals("2") || BOMType.equals("3") || BOMType.equals("6") || BOMType.equals("5") || BOMType.equals("7") || BOMType.equals("8") || BOMType.equals("9") || BOMType.equals("4"));
 else throw new IllegalArgumentException ("BOMType Invalid value - Reference_ID=279 - P - O - 1 - 2 - 3 - 6 - 5 - 7 - 8 - 9 - 4");
if (BOMType != null && BOMType.length() > 1)
{
log.warning("Length > 1 - truncated");
BOMType = BOMType.substring(0,1);
}
set_Value ("BOMType", BOMType);
}
/** Get BOM Type.
Type of BOM */
public String getBOMType() 
{
return (String)get_Value("BOMType");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
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
/** Set BOM Line */
public void setM_Product_BOM_ID (int M_Product_BOM_ID)
{
set_ValueNoCheck ("M_Product_BOM_ID", new Integer(M_Product_BOM_ID));
}
/** Get BOM Line */
public int getM_Product_BOM_ID() 
{
Integer ii = (Integer)get_Value("M_Product_BOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCTBOM_ID_AD_Reference_ID=162;
/** Set BOM Product.
Bill of Material Component Product */
public void setM_ProductBOM_ID (int M_ProductBOM_ID)
{
set_Value ("M_ProductBOM_ID", new Integer(M_ProductBOM_ID));
}
/** Get BOM Product.
Bill of Material Component Product */
public int getM_ProductBOM_ID() 
{
Integer ii = (Integer)get_Value("M_ProductBOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_ProductBOM_ID()));
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
}
