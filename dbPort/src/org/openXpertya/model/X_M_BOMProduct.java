/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_BOMProduct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:35.117 */
public class X_M_BOMProduct extends PO
{
/** Constructor estándar */
public X_M_BOMProduct (Properties ctx, int M_BOMProduct_ID, String trxName)
{
super (ctx, M_BOMProduct_ID, trxName);
/** if (M_BOMProduct_ID == 0)
{
setBOMProductType (null);	// S
setBOMQty (Env.ZERO);	// 1
setLeadTimeOffset (0);
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_BOMProduct WHERE M_BOM_ID=@M_BOM_ID@
setM_BOM_ID (0);
setM_BOMProduct_ID (0);
}
 */
}
/** Load Constructor */
public X_M_BOMProduct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=801 */
public static final int Table_ID=801;

/** TableName=M_BOMProduct */
public static final String Table_Name="M_BOMProduct";

protected static KeyNamePair Model = new KeyNamePair(801,"M_BOMProduct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_BOMProduct[").append(getID()).append("]");
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
public static final int BOMPRODUCTTYPE_AD_Reference_ID=349;
/** Standard Product = S */
public static final String BOMPRODUCTTYPE_StandardProduct = "S";
/** Optional Product = O */
public static final String BOMPRODUCTTYPE_OptionalProduct = "O";
/** Alternative = A */
public static final String BOMPRODUCTTYPE_Alternative = "A";
/** Alternative (Default) = D */
public static final String BOMPRODUCTTYPE_AlternativeDefault = "D";
/** Outside Processing = X */
public static final String BOMPRODUCTTYPE_OutsideProcessing = "X";
/** Set Component Type.
BOM Product Type */
public void setBOMProductType (String BOMProductType)
{
if (BOMProductType.equals("S") || BOMProductType.equals("O") || BOMProductType.equals("A") || BOMProductType.equals("D") || BOMProductType.equals("X"));
 else throw new IllegalArgumentException ("BOMProductType Invalid value - Reference_ID=349 - S - O - A - D - X");
if (BOMProductType == null) throw new IllegalArgumentException ("BOMProductType is mandatory");
if (BOMProductType.length() > 1)
{
log.warning("Length > 1 - truncated");
BOMProductType = BOMProductType.substring(0,1);
}
set_Value ("BOMProductType", BOMProductType);
}
/** Get Component Type.
BOM Product Type */
public String getBOMProductType() 
{
return (String)get_Value("BOMProductType");
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,2000);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Lead Time Offset.
Optional Lead Time offest before starting production */
public void setLeadTimeOffset (int LeadTimeOffset)
{
set_Value ("LeadTimeOffset", new Integer(LeadTimeOffset));
}
/** Get Lead Time Offset.
Optional Lead Time offest before starting production */
public int getLeadTimeOffset() 
{
Integer ii = (Integer)get_Value("LeadTimeOffset");
if (ii == null) return 0;
return ii.intValue();
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getLine()));
}
/** Set Attribute Set Instance.
Product Attribute Set Instance */
public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
{
if (M_AttributeSetInstance_ID <= 0) set_Value ("M_AttributeSetInstance_ID", null);
 else 
set_Value ("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
}
/** Get Attribute Set Instance.
Product Attribute Set Instance */
public int getM_AttributeSetInstance_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSetInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Alternative Group.
Product BOM Alternative Group */
public void setM_BOMAlternative_ID (int M_BOMAlternative_ID)
{
if (M_BOMAlternative_ID <= 0) set_Value ("M_BOMAlternative_ID", null);
 else 
set_Value ("M_BOMAlternative_ID", new Integer(M_BOMAlternative_ID));
}
/** Get Alternative Group.
Product BOM Alternative Group */
public int getM_BOMAlternative_ID() 
{
Integer ii = (Integer)get_Value("M_BOMAlternative_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set BOM.
Bill of Material */
public void setM_BOM_ID (int M_BOM_ID)
{
set_ValueNoCheck ("M_BOM_ID", new Integer(M_BOM_ID));
}
/** Get BOM.
Bill of Material */
public int getM_BOM_ID() 
{
Integer ii = (Integer)get_Value("M_BOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set BOM Component.
Bill of Material Component (Product) */
public void setM_BOMProduct_ID (int M_BOMProduct_ID)
{
set_ValueNoCheck ("M_BOMProduct_ID", new Integer(M_BOMProduct_ID));
}
/** Get BOM Component.
Bill of Material Component (Product) */
public int getM_BOMProduct_ID() 
{
Integer ii = (Integer)get_Value("M_BOMProduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Change Notice.
Bill of Materials (Engineering) Change Notice (Version) */
public void setM_ChangeNotice_ID (int M_ChangeNotice_ID)
{
if (M_ChangeNotice_ID <= 0) set_Value ("M_ChangeNotice_ID", null);
 else 
set_Value ("M_ChangeNotice_ID", new Integer(M_ChangeNotice_ID));
}
/** Get Change Notice.
Bill of Materials (Engineering) Change Notice (Version) */
public int getM_ChangeNotice_ID() 
{
Integer ii = (Integer)get_Value("M_ChangeNotice_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCTBOM_ID_AD_Reference_ID=162;
/** Set BOM Product.
Bill of Material Component Product */
public void setM_ProductBOM_ID (int M_ProductBOM_ID)
{
if (M_ProductBOM_ID <= 0) set_Value ("M_ProductBOM_ID", null);
 else 
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
/** Set Product Operation.
Product Manufacturing Operation */
public void setM_ProductOperation_ID (int M_ProductOperation_ID)
{
if (M_ProductOperation_ID <= 0) set_Value ("M_ProductOperation_ID", null);
 else 
set_Value ("M_ProductOperation_ID", new Integer(M_ProductOperation_ID));
}
/** Get Product Operation.
Product Manufacturing Operation */
public int getM_ProductOperation_ID() 
{
Integer ii = (Integer)get_Value("M_ProductOperation_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
}
