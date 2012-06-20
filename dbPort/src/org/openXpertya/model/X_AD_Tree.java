/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Tree
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:28.606 */
public class X_AD_Tree extends PO
{
/** Constructor estándar */
public X_AD_Tree (Properties ctx, int AD_Tree_ID, String trxName)
{
super (ctx, AD_Tree_ID, trxName);
/** if (AD_Tree_ID == 0)
{
setAD_Tree_ID (0);
setIsAllNodes (false);
setName (null);
setTreeType (null);
}
 */
}
/** Load Constructor */
public X_AD_Tree (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=288 */
public static final int Table_ID=288;

/** TableName=AD_Tree */
public static final String Table_Name="AD_Tree";

protected static KeyNamePair Model = new KeyNamePair(288,"AD_Tree");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Tree[").append(getID()).append("]");
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
/** Set All Nodes.
All Nodes are included (Complete Tree) */
public void setIsAllNodes (boolean IsAllNodes)
{
set_Value ("IsAllNodes", new Boolean(IsAllNodes));
}
/** Get All Nodes.
All Nodes are included (Complete Tree) */
public boolean isAllNodes() 
{
Object oo = get_Value("IsAllNodes");
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
public static final int TREETYPE_AD_Reference_ID=120;
/** Activity = AY */
public static final String TREETYPE_Activity = "AY";
/** Organization = OO */
public static final String TREETYPE_Organization = "OO";
/** BoM = BB */
public static final String TREETYPE_BoM = "BB";
/** Project = PJ */
public static final String TREETYPE_Project = "PJ";
/** Sales Region = SR */
public static final String TREETYPE_SalesRegion = "SR";
/** Product Category = PC */
public static final String TREETYPE_ProductCategory = "PC";
/** Campaign = MC */
public static final String TREETYPE_Campaign = "MC";
/** Menu = MM */
public static final String TREETYPE_Menu = "MM";
/** Element Value = EV */
public static final String TREETYPE_ElementValue = "EV";
/** Product = PR */
public static final String TREETYPE_Product = "PR";
/** BPartner = BP */
public static final String TREETYPE_BPartner = "BP";
/** Set Type | Area.
Element this tree is built on (i.e Product, Business Partner) */
public void setTreeType (String TreeType)
{
if (TreeType.equals("AY") || TreeType.equals("OO") || TreeType.equals("BB") || TreeType.equals("PJ") || TreeType.equals("SR") || TreeType.equals("PC") || TreeType.equals("MC") || TreeType.equals("MM") || TreeType.equals("EV") || TreeType.equals("PR") || TreeType.equals("BP"));
 else throw new IllegalArgumentException ("TreeType Invalid value - Reference_ID=120 - AY - OO - BB - PJ - SR - PC - MC - MM - EV - PR - BP");
if (TreeType == null) throw new IllegalArgumentException ("TreeType is mandatory");
if (TreeType.length() > 2)
{
log.warning("Length > 2 - truncated");
TreeType = TreeType.substring(0,2);
}
set_ValueNoCheck ("TreeType", TreeType);
}
/** Get Type | Area.
Element this tree is built on (i.e Product, Business Partner) */
public String getTreeType() 
{
return (String)get_Value("TreeType");
}
}
