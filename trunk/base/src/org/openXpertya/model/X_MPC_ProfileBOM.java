/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_ProfileBOM
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.5 */
public class X_MPC_ProfileBOM extends PO
{
/** Constructor estándar */
public X_MPC_ProfileBOM (Properties ctx, int MPC_ProfileBOM_ID, String trxName)
{
super (ctx, MPC_ProfileBOM_ID, trxName);
/** if (MPC_ProfileBOM_ID == 0)
{
setMPC_ProfileBOM_ID (0);
setM_Product_Category_ID (0);
setName (null);
setSpecie (null);
}
 */
}
/** Load Constructor */
public X_MPC_ProfileBOM (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000000 */
public static final int Table_ID=1000000;

/** TableName=MPC_ProfileBOM */
public static final String Table_Name="MPC_ProfileBOM";

protected static KeyNamePair Model = new KeyNamePair(1000000,"MPC_ProfileBOM");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_ProfileBOM[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order.
Order */
public void setC_Order_ID (String C_Order_ID)
{
if (C_Order_ID != null && C_Order_ID.length() > 22)
{
log.warning("Length > 22 - truncated");
C_Order_ID = C_Order_ID.substring(0,21);
}
set_Value ("C_Order_ID", C_Order_ID);
}
/** Get Order.
Order */
public String getC_Order_ID() 
{
return (String)get_Value("C_Order_ID");
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
if (C_UOM_ID <= 0) set_Value ("C_UOM_ID", null);
 else 
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
/** Set ContenidoNeto */
public void setContenidoNeto (BigDecimal ContenidoNeto)
{
set_Value ("ContenidoNeto", ContenidoNeto);
}
/** Get ContenidoNeto */
public BigDecimal getContenidoNeto() 
{
BigDecimal bd = (BigDecimal)get_Value("ContenidoNeto");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Copy From.
Copy From Record */
public void setCopyFrom (String CopyFrom)
{
if (CopyFrom != null && CopyFrom.length() > 1)
{
log.warning("Length > 1 - truncated");
CopyFrom = CopyFrom.substring(0,0);
}
set_Value ("CopyFrom", CopyFrom);
}
/** Get Copy From.
Copy From Record */
public String getCopyFrom() 
{
return (String)get_Value("CopyFrom");
}
/** Set CopyToNutrients */
public void setCopyToNutrients (String CopyToNutrients)
{
if (CopyToNutrients != null && CopyToNutrients.length() > 1)
{
log.warning("Length > 1 - truncated");
CopyToNutrients = CopyToNutrients.substring(0,0);
}
set_Value ("CopyToNutrients", CopyToNutrients);
}
/** Get CopyToNutrients */
public String getCopyToNutrients() 
{
return (String)get_Value("CopyToNutrients");
}
/** Set Rate.
Currency Conversion Rate */
public void setCurrencyRate (BigDecimal CurrencyRate)
{
set_Value ("CurrencyRate", CurrencyRate);
}
/** Get Rate.
Currency Conversion Rate */
public BigDecimal getCurrencyRate() 
{
BigDecimal bd = (BigDecimal)get_Value("CurrencyRate");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Document Date.
Date of the Document */
public void setDateDoc (Timestamp DateDoc)
{
set_Value ("DateDoc", DateDoc);
}
/** Get Document Date.
Date of the Document */
public Timestamp getDateDoc() 
{
return (Timestamp)get_Value("DateDoc");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 250)
{
log.warning("Length > 250 - truncated");
Description = Description.substring(0,249);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Generate To.
Generate To */
public void setGenerateTo (String GenerateTo)
{
if (GenerateTo != null && GenerateTo.length() > 1)
{
log.warning("Length > 1 - truncated");
GenerateTo = GenerateTo.substring(0,0);
}
set_Value ("GenerateTo", GenerateTo);
}
/** Get Generate To.
Generate To */
public String getGenerateTo() 
{
return (String)get_Value("GenerateTo");
}
/** Set Published.
The Topic is published and can be viewed */
public void setIsPublished (boolean IsPublished)
{
set_Value ("IsPublished", new Boolean(IsPublished));
}
/** Get Published.
The Topic is published and can be viewed */
public boolean isPublished() 
{
Object oo = get_Value("IsPublished");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Line Amount.
Line Extended Amount (Quantity * Actual Price) without Freight and Charges */
public void setLineNetAmt (BigDecimal LineNetAmt)
{
set_Value ("LineNetAmt", LineNetAmt);
}
/** Get Line Amount.
Line Extended Amount (Quantity * Actual Price) without Freight and Charges */
public BigDecimal getLineNetAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineNetAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set BOM & Formula */
public void setMPC_Product_BOM_ID (int MPC_Product_BOM_ID)
{
if (MPC_Product_BOM_ID <= 0) set_Value ("MPC_Product_BOM_ID", null);
 else 
set_Value ("MPC_Product_BOM_ID", new Integer(MPC_Product_BOM_ID));
}
/** Get BOM & Formula */
public int getMPC_Product_BOM_ID() 
{
Integer ii = (Integer)get_Value("MPC_Product_BOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set MPC_ProfileBOM_ID */
public void setMPC_ProfileBOM_ID (int MPC_ProfileBOM_ID)
{
set_ValueNoCheck ("MPC_ProfileBOM_ID", new Integer(MPC_ProfileBOM_ID));
}
/** Get MPC_ProfileBOM_ID */
public int getMPC_ProfileBOM_ID() 
{
Integer ii = (Integer)get_Value("MPC_ProfileBOM_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
set_Value ("M_Product_Category_ID", new Integer(M_Product_Category_ID));
}
/** Get Product Category.
Category of a Product */
public int getM_Product_Category_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Category_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
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
/** Set Margin %.
Margin for a product as a percentage */
public void setMargin (BigDecimal Margin)
{
set_Value ("Margin", Margin);
}
/** Get Margin %.
Margin for a product as a percentage */
public BigDecimal getMargin() 
{
BigDecimal bd = (BigDecimal)get_Value("Margin");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
/** Set Old Value.
The old file data */
public void setOldValue (String OldValue)
{
if (OldValue != null && OldValue.length() > 40)
{
log.warning("Length > 40 - truncated");
OldValue = OldValue.substring(0,39);
}
set_Value ("OldValue", OldValue);
}
/** Get Old Value.
The old file data */
public String getOldValue() 
{
return (String)get_Value("OldValue");
}
/** Set List Price.
List Price */
public void setPriceList (BigDecimal PriceList)
{
set_Value ("PriceList", PriceList);
}
/** Get List Price.
List Price */
public BigDecimal getPriceList() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceList");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Process */
public void setProcess (String Process)
{
if (Process != null && Process.length() > 1)
{
log.warning("Length > 1 - truncated");
Process = Process.substring(0,0);
}
set_Value ("Process", Process);
}
/** Get Process */
public String getProcess() 
{
return (String)get_Value("Process");
}
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
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
public static final int SPECIE_AD_Reference_ID=1000003;
/** ACUICULTURA = Q */
public static final String SPECIE_ACUICULTURA = "Q";
/** AVES = A */
public static final String SPECIE_AVES = "A";
/** CERDOS = C */
public static final String SPECIE_CERDOS = "C";
/** RUMIANTES = R */
public static final String SPECIE_RUMIANTES = "R";
/** MASCOTAS = M */
public static final String SPECIE_MASCOTAS = "M";
/** OTROS = O */
public static final String SPECIE_OTROS = "O";
/** PLATINUM = P */
public static final String SPECIE_PLATINUM = "P";
/** GOLD = G */
public static final String SPECIE_GOLD = "G";
/** SILVER = S */
public static final String SPECIE_SILVER = "S";
/** BRONZE = B */
public static final String SPECIE_BRONZE = "B";
/** REPROCESO = X */
public static final String SPECIE_REPROCESO = "X";
/** Set Specie */
public void setSpecie (String Specie)
{
if (Specie.equals("Q") || Specie.equals("A") || Specie.equals("C") || Specie.equals("R") || Specie.equals("M") || Specie.equals("O") || Specie.equals("P") || Specie.equals("G") || Specie.equals("S") || Specie.equals("B") || Specie.equals("X"));
 else throw new IllegalArgumentException ("Specie Invalid value - Reference_ID=1000003 - Q - A - C - R - M - O - P - G - S - B - X");
if (Specie == null) throw new IllegalArgumentException ("Specie is mandatory");
if (Specie.length() > 40)
{
log.warning("Length > 40 - truncated");
Specie = Specie.substring(0,39);
}
set_Value ("Specie", Specie);
}
/** Get Specie */
public String getSpecie() 
{
return (String)get_Value("Specie");
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value != null && Value.length() > 22)
{
log.warning("Length > 22 - truncated");
Value = Value.substring(0,21);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getValue());
}
}
