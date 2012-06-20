/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Element
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.843 */
public class X_C_Element extends PO
{
/** Constructor estándar */
public X_C_Element (Properties ctx, int C_Element_ID, String trxName)
{
super (ctx, C_Element_ID, trxName);
/** if (C_Element_ID == 0)
{
setAD_Tree_ID (0);
setC_Element_ID (0);
setElementType (null);	// A
setIsBalancing (false);
setIsNaturalAccount (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_Element (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=142 */
public static final int Table_ID=142;

/** TableName=C_Element */
public static final String Table_Name="C_Element";

protected static KeyNamePair Model = new KeyNamePair(142,"C_Element");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Element[").append(getID()).append("]");
return sb.toString();
}
/** Set Tree.
Identifies a Tree */
public void setAD_Tree_ID (int AD_Tree_ID)
{
set_ValueNoCheck ("AD_Tree_ID", new Integer(AD_Tree_ID));
}
/** Get Tree.
Identifies a Tree */
public int getAD_Tree_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ADD1TREE_ID_AD_Reference_ID=184;
/** Set Additional Tree (1).
For parallel Reporting */
public void setAdd1Tree_ID (int Add1Tree_ID)
{
if (Add1Tree_ID <= 0) set_Value ("Add1Tree_ID", null);
 else 
set_Value ("Add1Tree_ID", new Integer(Add1Tree_ID));
}
/** Get Additional Tree (1).
For parallel Reporting */
public int getAdd1Tree_ID() 
{
Integer ii = (Integer)get_Value("Add1Tree_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ADD2TREE_ID_AD_Reference_ID=184;
/** Set Additional Tree (2).
For parallel Reporting */
public void setAdd2Tree_ID (int Add2Tree_ID)
{
if (Add2Tree_ID <= 0) set_Value ("Add2Tree_ID", null);
 else 
set_Value ("Add2Tree_ID", new Integer(Add2Tree_ID));
}
/** Get Additional Tree (2).
For parallel Reporting */
public int getAdd2Tree_ID() 
{
Integer ii = (Integer)get_Value("Add2Tree_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Element.
Accounting Element */
public void setC_Element_ID (int C_Element_ID)
{
set_ValueNoCheck ("C_Element_ID", new Integer(C_Element_ID));
}
/** Get Element.
Accounting Element */
public int getC_Element_ID() 
{
Integer ii = (Integer)get_Value("C_Element_ID");
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
public static final int ELEMENTTYPE_AD_Reference_ID=116;
/** Account = A */
public static final String ELEMENTTYPE_Account = "A";
/** User defined = U */
public static final String ELEMENTTYPE_UserDefined = "U";
/** Set Type.
Element Type (account or user defined) */
public void setElementType (String ElementType)
{
if (ElementType.equals("A") || ElementType.equals("U"));
 else throw new IllegalArgumentException ("ElementType Invalid value - Reference_ID=116 - A - U");
if (ElementType == null) throw new IllegalArgumentException ("ElementType is mandatory");
if (ElementType.length() > 1)
{
log.warning("Length > 1 - truncated");
ElementType = ElementType.substring(0,0);
}
set_ValueNoCheck ("ElementType", ElementType);
}
/** Get Type.
Element Type (account or user defined) */
public String getElementType() 
{
return (String)get_Value("ElementType");
}
/** Set Balancing.
All transactions within an element value must balance (e.g. cost centers) */
public void setIsBalancing (boolean IsBalancing)
{
set_Value ("IsBalancing", new Boolean(IsBalancing));
}
/** Get Balancing.
All transactions within an element value must balance (e.g. cost centers) */
public boolean isBalancing() 
{
Object oo = get_Value("IsBalancing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Natural Account.
The primary natural account */
public void setIsNaturalAccount (boolean IsNaturalAccount)
{
set_Value ("IsNaturalAccount", new Boolean(IsNaturalAccount));
}
/** Get Natural Account.
The primary natural account */
public boolean isNaturalAccount() 
{
Object oo = get_Value("IsNaturalAccount");
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Value Format.
Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public void setVFormat (String VFormat)
{
if (VFormat != null && VFormat.length() > 40)
{
log.warning("Length > 40 - truncated");
VFormat = VFormat.substring(0,39);
}
set_Value ("VFormat", VFormat);
}
/** Get Value Format.
Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public String getVFormat() 
{
return (String)get_Value("VFormat");
}
}
