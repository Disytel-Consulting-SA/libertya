/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Warehouse
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-11-11 16:17:08.916 */
public class X_M_Warehouse extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Warehouse (Properties ctx, int M_Warehouse_ID, String trxName)
{
super (ctx, M_Warehouse_ID, trxName);
/** if (M_Warehouse_ID == 0)
{
setC_Location_ID (0);
setM_Warehouse_ID (0);
setName (null);
setSeparator (null);	// *
setStockAvailableForSale (true);	// Y
setValue (null);
}
 */
}
/** Load Constructor */
public X_M_Warehouse (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Warehouse");

/** TableName=M_Warehouse */
public static final String Table_Name="M_Warehouse";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Warehouse");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Warehouse[").append(getID()).append("]");
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
/** Set Address.
Location or Address */
public void setC_Location_ID (int C_Location_ID)
{
set_Value ("C_Location_ID", new Integer(C_Location_ID));
}
/** Get Address.
Location or Address */
public int getC_Location_ID() 
{
Integer ii = (Integer)get_Value("C_Location_ID");
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
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_ValueNoCheck ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int PRODUCTCHANGECHARGE_ID_AD_Reference_ID = MReference.getReferenceID("C_Charge");
/** Set Product Change Charge.
Product Change Charge */
public void setProductChangeCharge_ID (int ProductChangeCharge_ID)
{
if (ProductChangeCharge_ID <= 0) set_Value ("ProductChangeCharge_ID", null);
 else 
set_Value ("ProductChangeCharge_ID", new Integer(ProductChangeCharge_ID));
}
/** Get Product Change Charge.
Product Change Charge */
public int getProductChangeCharge_ID() 
{
Integer ii = (Integer)get_Value("ProductChangeCharge_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Element Separator.
Element Separator */
public void setSeparator (String Separator)
{
if (Separator == null) throw new IllegalArgumentException ("Separator is mandatory");
if (Separator.length() > 1)
{
log.warning("Length > 1 - truncated");
Separator = Separator.substring(0,1);
}
set_Value ("Separator", Separator);
}
/** Get Element Separator.
Element Separator */
public String getSeparator() 
{
return (String)get_Value("Separator");
}
public static final int SHRINK_CHARGE_ID_AD_Reference_ID = MReference.getReferenceID("C_Charge");
/** Set Shrink Charge.
Shrink Charge */
public void setShrink_Charge_ID (int Shrink_Charge_ID)
{
if (Shrink_Charge_ID <= 0) set_Value ("Shrink_Charge_ID", null);
 else 
set_Value ("Shrink_Charge_ID", new Integer(Shrink_Charge_ID));
}
/** Get Shrink Charge.
Shrink Charge */
public int getShrink_Charge_ID() 
{
Integer ii = (Integer)get_Value("Shrink_Charge_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SPLITTING_CHARGE_ID_AD_Reference_ID = MReference.getReferenceID("C_Charge");
/** Set Splitting Charge.
Splitting Charge */
public void setSplitting_Charge_ID (int Splitting_Charge_ID)
{
if (Splitting_Charge_ID <= 0) set_Value ("Splitting_Charge_ID", null);
 else 
set_Value ("Splitting_Charge_ID", new Integer(Splitting_Charge_ID));
}
/** Get Splitting Charge.
Splitting Charge */
public int getSplitting_Charge_ID() 
{
Integer ii = (Integer)get_Value("Splitting_Charge_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Stock Available For Sale */
public void setStockAvailableForSale (boolean StockAvailableForSale)
{
set_Value ("StockAvailableForSale", new Boolean(StockAvailableForSale));
}
/** Get Stock Available For Sale */
public boolean isStockAvailableForSale() 
{
Object oo = get_Value("StockAvailableForSale");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
