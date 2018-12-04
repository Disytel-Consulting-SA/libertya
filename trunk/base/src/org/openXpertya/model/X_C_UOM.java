/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_UOM
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2018-12-04 13:00:50.055 */
public class X_C_UOM extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_UOM (Properties ctx, int C_UOM_ID, String trxName)
{
super (ctx, C_UOM_ID, trxName);
/** if (C_UOM_ID == 0)
{
setAllowDecimals (false);
setCostingPrecision (0);
setC_UOM_ID (0);
setIsDefault (false);
setName (null);
setProductSelectable (false);
setStdPrecision (0);
setX12DE355 (null);
}
 */
}
/** Load Constructor */
public X_C_UOM (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_UOM");

/** TableName=C_UOM */
public static final String Table_Name="C_UOM";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_UOM");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_UOM[").append(getID()).append("]");
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
/** Set Allow Decimals.
Allow decimals in quantitys of document lines */
public void setAllowDecimals (boolean AllowDecimals)
{
set_Value ("AllowDecimals", new Boolean(AllowDecimals));
}
/** Get Allow Decimals.
Allow decimals in quantitys of document lines */
public boolean isAllowDecimals() 
{
Object oo = get_Value("AllowDecimals");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Costing Precision.
Rounding used costing calculations */
public void setCostingPrecision (int CostingPrecision)
{
set_Value ("CostingPrecision", new Integer(CostingPrecision));
}
/** Get Costing Precision.
Rounding used costing calculations */
public int getCostingPrecision() 
{
Integer ii = (Integer)get_Value("CostingPrecision");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_UOM_GROUP_ID_AD_Reference_ID = MReference.getReferenceID("c_uom_group_id");
/** Set C_UOM_Group_ID */
public void setC_UOM_Group_ID (int C_UOM_Group_ID)
{
if (C_UOM_Group_ID <= 0) set_Value ("C_UOM_Group_ID", null);
 else 
set_Value ("C_UOM_Group_ID", new Integer(C_UOM_Group_ID));
}
/** Get C_UOM_Group_ID */
public int getC_UOM_Group_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
set_ValueNoCheck ("C_UOM_ID", new Integer(C_UOM_ID));
}
/** Get UOM.
Unit of Measure */
public int getC_UOM_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Product Selectable.
If it is active, can select the uom for products */
public void setProductSelectable (boolean ProductSelectable)
{
set_Value ("ProductSelectable", new Boolean(ProductSelectable));
}
/** Get Product Selectable.
If it is active, can select the uom for products */
public boolean isProductSelectable() 
{
Object oo = get_Value("ProductSelectable");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Standard Precision.
Rule for rounding  calculated amounts */
public void setStdPrecision (int StdPrecision)
{
set_Value ("StdPrecision", new Integer(StdPrecision));
}
/** Get Standard Precision.
Rule for rounding  calculated amounts */
public int getStdPrecision() 
{
Integer ii = (Integer)get_Value("StdPrecision");
if (ii == null) return 0;
return ii.intValue();
}
/** Set UOM Code FE */
public void setUOMCodeFE (String UOMCodeFE)
{
if (UOMCodeFE != null && UOMCodeFE.length() > 10)
{
log.warning("Length > 10 - truncated");
UOMCodeFE = UOMCodeFE.substring(0,10);
}
set_Value ("UOMCodeFE", UOMCodeFE);
}
/** Get UOM Code FE */
public String getUOMCodeFE() 
{
return (String)get_Value("UOMCodeFE");
}
/** Set Symbol.
Symbol for a Unit of Measure */
public void setUOMSymbol (String UOMSymbol)
{
if (UOMSymbol != null && UOMSymbol.length() > 10)
{
log.warning("Length > 10 - truncated");
UOMSymbol = UOMSymbol.substring(0,10);
}
set_Value ("UOMSymbol", UOMSymbol);
}
/** Get Symbol.
Symbol for a Unit of Measure */
public String getUOMSymbol() 
{
return (String)get_Value("UOMSymbol");
}
/** Set UOM Code.
UOM EDI X12 Code */
public void setX12DE355 (String X12DE355)
{
if (X12DE355 == null) throw new IllegalArgumentException ("X12DE355 is mandatory");
if (X12DE355.length() > 4)
{
log.warning("Length > 4 - truncated");
X12DE355 = X12DE355.substring(0,4);
}
set_Value ("X12DE355", X12DE355);
}
/** Get UOM Code.
UOM EDI X12 Code */
public String getX12DE355() 
{
return (String)get_Value("X12DE355");
}
}
