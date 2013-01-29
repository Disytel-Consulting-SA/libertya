/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Product_Gamas
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-12-03 14:00:43.933 */
public class X_M_Product_Gamas extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Product_Gamas (Properties ctx, int M_Product_Gamas_ID, String trxName)
{
super (ctx, M_Product_Gamas_ID, trxName);
/** if (M_Product_Gamas_ID == 0)
{
setIsDefault (false);
setIsSelfService (false);
setM_Product_Gamas_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_M_Product_Gamas (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Product_Gamas");

/** TableName=M_Product_Gamas */
public static final String Table_Name="M_Product_Gamas";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Product_Gamas");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Product_Gamas[").append(getID()).append("]");
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
/** Set Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public void setIsSelfService (boolean IsSelfService)
{
set_Value ("IsSelfService", new Boolean(IsSelfService));
}
/** Get Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public boolean isSelfService() 
{
Object oo = get_Value("IsSelfService");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set m_product_gamas_id */
public void setM_Product_Gamas_ID (int M_Product_Gamas_ID)
{
set_ValueNoCheck ("M_Product_Gamas_ID", new Integer(M_Product_Gamas_ID));
}
/** Get m_product_gamas_id */
public int getM_Product_Gamas_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Gamas_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCT_LINES_ID_AD_Reference_ID = MReference.getReferenceID("M_Product_Lines");
/** Set M_Product_Lines_ID */
public void setM_Product_Lines_ID (int M_Product_Lines_ID)
{
if (M_Product_Lines_ID <= 0) set_Value ("M_Product_Lines_ID", null);
 else 
set_Value ("M_Product_Lines_ID", new Integer(M_Product_Lines_ID));
}
/** Get M_Product_Lines_ID */
public int getM_Product_Lines_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Lines_ID");
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
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
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
if (Value != null && Value.length() > 100)
{
log.warning("Length > 100 - truncated");
Value = Value.substring(0,100);
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
