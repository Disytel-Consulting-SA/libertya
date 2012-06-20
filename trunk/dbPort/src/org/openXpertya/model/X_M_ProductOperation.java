/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_ProductOperation
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:39.0 */
public class X_M_ProductOperation extends PO
{
/** Constructor estándar */
public X_M_ProductOperation (Properties ctx, int M_ProductOperation_ID, String trxName)
{
super (ctx, M_ProductOperation_ID, trxName);
/** if (M_ProductOperation_ID == 0)
{
setM_ProductOperation_ID (0);
setM_Product_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_M_ProductOperation (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=796 */
public static final int Table_ID=796;

/** TableName=M_ProductOperation */
public static final String Table_Name="M_ProductOperation";

protected static KeyNamePair Model = new KeyNamePair(796,"M_ProductOperation");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_ProductOperation[").append(getID()).append("]");
return sb.toString();
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Product Operation.
Product Manufacturing Operation */
public void setM_ProductOperation_ID (int M_ProductOperation_ID)
{
set_ValueNoCheck ("M_ProductOperation_ID", new Integer(M_ProductOperation_ID));
}
/** Get Product Operation.
Product Manufacturing Operation */
public int getM_ProductOperation_ID() 
{
Integer ii = (Integer)get_Value("M_ProductOperation_ID");
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
/** Set Setup Time CMPCS */
public void setSetupTime (BigDecimal SetupTime)
{
set_Value ("SetupTime", SetupTime);
}
/** Get Setup Time CMPCS */
public BigDecimal getSetupTime() 
{
BigDecimal bd = (BigDecimal)get_Value("SetupTime");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Teardown Time.
Time at the end of the operation */
public void setTeardownTime (BigDecimal TeardownTime)
{
set_Value ("TeardownTime", TeardownTime);
}
/** Get Teardown Time.
Time at the end of the operation */
public BigDecimal getTeardownTime() 
{
BigDecimal bd = (BigDecimal)get_Value("TeardownTime");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Runtime per Unit.
Time to produce one unit */
public void setUnitRuntime (BigDecimal UnitRuntime)
{
set_Value ("UnitRuntime", UnitRuntime);
}
/** Get Runtime per Unit.
Time to produce one unit */
public BigDecimal getUnitRuntime() 
{
BigDecimal bd = (BigDecimal)get_Value("UnitRuntime");
if (bd == null) return Env.ZERO;
return bd;
}
}
