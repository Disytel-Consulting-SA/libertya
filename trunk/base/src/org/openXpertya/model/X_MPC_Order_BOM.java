/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Order_BOM
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.921 */
public class X_MPC_Order_BOM extends PO
{
/** Constructor estándar */
public X_MPC_Order_BOM (Properties ctx, int MPC_Order_BOM_ID, String trxName)
{
super (ctx, MPC_Order_BOM_ID, trxName);
/** if (MPC_Order_BOM_ID == 0)
{
setC_UOM_ID (0);
setMPC_Order_BOM_ID (0);
setMPC_Order_ID (0);
setM_Product_ID (0);
setName (null);
setValidFrom (new Timestamp(System.currentTimeMillis()));
setValue (null);
}
 */
}
/** Load Constructor */
public X_MPC_Order_BOM (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000015 */
public static final int Table_ID=1000015;

/** TableName=MPC_Order_BOM */
public static final String Table_Name="MPC_Order_BOM";

protected static KeyNamePair Model = new KeyNamePair(1000015,"MPC_Order_BOM");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Order_BOM[").append(getID()).append("]");
return sb.toString();
}
public static final int BOMTYPE_AD_Reference_ID=279;
/** In alternative Group 4 = 4 */
public static final String BOMTYPE_InAlternativeGroup4 = "4";
/** In alternative Group 7 = 7 */
public static final String BOMTYPE_InAlternativeGroup7 = "7";
/** In alternative Group 8 = 8 */
public static final String BOMTYPE_InAlternativeGroup8 = "8";
/** In alternative Group 9 = 9 */
public static final String BOMTYPE_InAlternativeGroup9 = "9";
/** In alternative Group 5 = 5 */
public static final String BOMTYPE_InAlternativeGroup5 = "5";
/** In alternative Group 6 = 6 */
public static final String BOMTYPE_InAlternativeGroup6 = "6";
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
/** Set BOM Type.
Type of BOM */
public void setBOMType (String BOMType)
{
if (BOMType == null || BOMType.equals("4") || BOMType.equals("7") || BOMType.equals("8") || BOMType.equals("9") || BOMType.equals("5") || BOMType.equals("6") || BOMType.equals("P") || BOMType.equals("O") || BOMType.equals("1") || BOMType.equals("2") || BOMType.equals("3"));
 else throw new IllegalArgumentException ("BOMType Invalid value - Reference_ID=279 - 4 - 7 - 8 - 9 - 5 - 6 - P - O - 1 - 2 - 3");
if (BOMType != null && BOMType.length() > 1)
{
log.warning("Length > 1 - truncated");
BOMType = BOMType.substring(0,0);
}
set_Value ("BOMType", BOMType);
}
/** Get BOM Type.
Type of BOM */
public String getBOMType() 
{
return (String)get_Value("BOMType");
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
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
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 510)
{
log.warning("Length > 510 - truncated");
Description = Description.substring(0,509);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Document No.
Document sequence number of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 20)
{
log.warning("Length > 20 - truncated");
DocumentNo = DocumentNo.substring(0,19);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence number of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set Order BOM ID */
public void setMPC_Order_BOM_ID (int MPC_Order_BOM_ID)
{
set_ValueNoCheck ("MPC_Order_BOM_ID", new Integer(MPC_Order_BOM_ID));
}
/** Get Order BOM ID */
public int getMPC_Order_BOM_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_BOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Manufacturing Order.
Manufacturing Order */
public void setMPC_Order_ID (int MPC_Order_ID)
{
set_ValueNoCheck ("MPC_Order_ID", new Integer(MPC_Order_ID));
}
/** Get Manufacturing Order.
Manufacturing Order */
public int getMPC_Order_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_ID");
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
if (Name.length() > 120)
{
log.warning("Length > 120 - truncated");
Name = Name.substring(0,119);
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
/** Set Revision */
public void setRevision (String Revision)
{
if (Revision != null && Revision.length() > 10)
{
log.warning("Length > 10 - truncated");
Revision = Revision.substring(0,9);
}
set_Value ("Revision", Revision);
}
/** Get Revision */
public String getRevision() 
{
return (String)get_Value("Revision");
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
if (ValidFrom == null) throw new IllegalArgumentException ("ValidFrom is mandatory");
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
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 80)
{
log.warning("Length > 80 - truncated");
Value = Value.substring(0,79);
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
