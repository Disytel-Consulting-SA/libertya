/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CardSettlementConcepts
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-01-30 16:10:29.186 */
public class X_C_CardSettlementConcepts extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CardSettlementConcepts (Properties ctx, int C_CardSettlementConcepts_ID, String trxName)
{
super (ctx, C_CardSettlementConcepts_ID, trxName);
/** if (C_CardSettlementConcepts_ID == 0)
{
setC_CardSettlementConcepts_ID (0);
setM_Product_ID (0);
setName (null);
setType (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_CardSettlementConcepts (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CardSettlementConcepts");

/** TableName=C_CardSettlementConcepts */
public static final String Table_Name="C_CardSettlementConcepts";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CardSettlementConcepts");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CardSettlementConcepts[").append(getID()).append("]");
return sb.toString();
}
/** Set Card Settlement Concepts ID */
public void setC_CardSettlementConcepts_ID (int C_CardSettlementConcepts_ID)
{
set_ValueNoCheck ("C_CardSettlementConcepts_ID", new Integer(C_CardSettlementConcepts_ID));
}
/** Get Card Settlement Concepts ID */
public int getC_CardSettlementConcepts_ID() 
{
Integer ii = (Integer)get_Value("C_CardSettlementConcepts_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("M_Product (all)");
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 255)
{
log.warning("Length > 255 - truncated");
Name = Name.substring(0,255);
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
public static final int TYPE_AD_Reference_ID = MReference.getReferenceID("Concept Type");
/** Commission = CO */
public static final String TYPE_Commission = "CO";
/** Others = OT */
public static final String TYPE_Others = "OT";
/** Set Type */
public void setType (String Type)
{
if (Type.equals("CO") || Type.equals("OT"));
 else throw new IllegalArgumentException ("Type Invalid value - Reference = TYPE_AD_Reference_ID - CO - OT");
if (Type == null) throw new IllegalArgumentException ("Type is mandatory");
if (Type.length() > 2)
{
log.warning("Length > 2 - truncated");
Type = Type.substring(0,2);
}
set_Value ("Type", Type);
}
/** Get Type */
public String getType() 
{
return (String)get_Value("Type");
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
