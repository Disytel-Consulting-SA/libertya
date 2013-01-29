/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_PerpetualInv
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:38.765 */
public class X_M_PerpetualInv extends PO
{
/** Constructor estándar */
public X_M_PerpetualInv (Properties ctx, int M_PerpetualInv_ID, String trxName)
{
super (ctx, M_PerpetualInv_ID, trxName);
/** if (M_PerpetualInv_ID == 0)
{
setCountHighMovement (false);
setDateNextRun (new Timestamp(System.currentTimeMillis()));
setM_PerpetualInv_ID (0);
setName (null);
setNoInventoryCount (0);	// 1
setNoProductCount (0);	// 1
setNumberOfRuns (0);	// 1
}
 */
}
/** Load Constructor */
public X_M_PerpetualInv (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=342 */
public static final int Table_ID=342;

/** TableName=M_PerpetualInv */
public static final String Table_Name="M_PerpetualInv";

protected static KeyNamePair Model = new KeyNamePair(342,"M_PerpetualInv");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_PerpetualInv[").append(getID()).append("]");
return sb.toString();
}
/** Set Count high turnover items.
Count High Movement products */
public void setCountHighMovement (boolean CountHighMovement)
{
set_Value ("CountHighMovement", new Boolean(CountHighMovement));
}
/** Get Count high turnover items.
Count High Movement products */
public boolean isCountHighMovement() 
{
Object oo = get_Value("CountHighMovement");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Date last run.
Date the process was last run. */
public void setDateLastRun (Timestamp DateLastRun)
{
set_ValueNoCheck ("DateLastRun", DateLastRun);
}
/** Get Date last run.
Date the process was last run. */
public Timestamp getDateLastRun() 
{
return (Timestamp)get_Value("DateLastRun");
}
/** Set Date next run.
Date the process will run next */
public void setDateNextRun (Timestamp DateNextRun)
{
if (DateNextRun == null) throw new IllegalArgumentException ("DateNextRun is mandatory");
set_ValueNoCheck ("DateNextRun", DateNextRun);
}
/** Get Date next run.
Date the process will run next */
public Timestamp getDateNextRun() 
{
return (Timestamp)get_Value("DateNextRun");
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
/** Set Perpetual Inventory.
Rules for generating physical inventory */
public void setM_PerpetualInv_ID (int M_PerpetualInv_ID)
{
set_ValueNoCheck ("M_PerpetualInv_ID", new Integer(M_PerpetualInv_ID));
}
/** Get Perpetual Inventory.
Rules for generating physical inventory */
public int getM_PerpetualInv_ID() 
{
Integer ii = (Integer)get_Value("M_PerpetualInv_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
if (M_Product_Category_ID <= 0) set_Value ("M_Product_Category_ID", null);
 else 
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
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
if (M_Warehouse_ID <= 0) set_Value ("M_Warehouse_ID", null);
 else 
set_Value ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
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
/** Set Number of Inventory counts.
Frequency of inventory counts per year */
public void setNoInventoryCount (int NoInventoryCount)
{
set_Value ("NoInventoryCount", new Integer(NoInventoryCount));
}
/** Get Number of Inventory counts.
Frequency of inventory counts per year */
public int getNoInventoryCount() 
{
Integer ii = (Integer)get_Value("NoInventoryCount");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Number of Product counts.
Frequency of product counts per year */
public void setNoProductCount (int NoProductCount)
{
set_Value ("NoProductCount", new Integer(NoProductCount));
}
/** Get Number of Product counts.
Frequency of product counts per year */
public int getNoProductCount() 
{
Integer ii = (Integer)get_Value("NoProductCount");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Number of runs.
Frequency of processing Perpetual Inventory */
public void setNumberOfRuns (int NumberOfRuns)
{
set_Value ("NumberOfRuns", new Integer(NumberOfRuns));
}
/** Get Number of runs.
Frequency of processing Perpetual Inventory */
public int getNumberOfRuns() 
{
Integer ii = (Integer)get_Value("NumberOfRuns");
if (ii == null) return 0;
return ii.intValue();
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
