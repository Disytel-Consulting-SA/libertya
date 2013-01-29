/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_BOM
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:35.087 */
public class X_M_BOM extends PO
{
/** Constructor estándar */
public X_M_BOM (Properties ctx, int M_BOM_ID, String trxName)
{
super (ctx, M_BOM_ID, trxName);
/** if (M_BOM_ID == 0)
{
setBOMType (null);	// A
setBOMUse (null);	// A
setM_BOM_ID (0);
setM_Product_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_M_BOM (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=798 */
public static final int Table_ID=798;

/** TableName=M_BOM */
public static final String Table_Name="M_BOM";

protected static KeyNamePair Model = new KeyNamePair(798,"M_BOM");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_BOM[").append(getID()).append("]");
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
public static final int BOMTYPE_AD_Reference_ID=347;
/** Maintenance = M */
public static final String BOMTYPE_Maintenance = "M";
/** Repair = R */
public static final String BOMTYPE_Repair = "R";
/** Current Active = A */
public static final String BOMTYPE_CurrentActive = "A";
/** Make-To-Order = O */
public static final String BOMTYPE_Make_To_Order = "O";
/** Previous = P */
public static final String BOMTYPE_Previous = "P";
/** Previous, Spare = S */
public static final String BOMTYPE_PreviousSpare = "S";
/** Future = F */
public static final String BOMTYPE_Future = "F";
/** Set BOM Type.
Type of BOM */
public void setBOMType (String BOMType)
{
if (BOMType.equals("M") || BOMType.equals("R") || BOMType.equals("A") || BOMType.equals("O") || BOMType.equals("P") || BOMType.equals("S") || BOMType.equals("F"));
 else throw new IllegalArgumentException ("BOMType Invalid value - Reference_ID=347 - M - R - A - O - P - S - F");
if (BOMType == null) throw new IllegalArgumentException ("BOMType is mandatory");
if (BOMType.length() > 1)
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
public static final int BOMUSE_AD_Reference_ID=348;
/** Master = A */
public static final String BOMUSE_Master = "A";
/** Engineering = E */
public static final String BOMUSE_Engineering = "E";
/** Manufacturing = M */
public static final String BOMUSE_Manufacturing = "M";
/** Planning = P */
public static final String BOMUSE_Planning = "P";
/** Set BOM Use.
The use of the Bill of Material */
public void setBOMUse (String BOMUse)
{
if (BOMUse.equals("A") || BOMUse.equals("E") || BOMUse.equals("M") || BOMUse.equals("P"));
 else throw new IllegalArgumentException ("BOMUse Invalid value - Reference_ID=348 - A - E - M - P");
if (BOMUse == null) throw new IllegalArgumentException ("BOMUse is mandatory");
if (BOMUse.length() > 1)
{
log.warning("Length > 1 - truncated");
BOMUse = BOMUse.substring(0,1);
}
set_Value ("BOMUse", BOMUse);
}
/** Get BOM Use.
The use of the Bill of Material */
public String getBOMUse() 
{
return (String)get_Value("BOMUse");
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
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
