/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_LandedCost
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:30.437 */
public class X_C_LandedCost extends PO
{
/** Constructor estándar */
public X_C_LandedCost (Properties ctx, int C_LandedCost_ID, String trxName)
{
super (ctx, C_LandedCost_ID, trxName);
/** if (C_LandedCost_ID == 0)
{
setC_InvoiceLine_ID (0);
setC_LandedCost_ID (0);
setM_CostElement_ID (0);
}
 */
}
/** Load Constructor */
public X_C_LandedCost (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=759 */
public static final int Table_ID=759;

/** TableName=C_LandedCost */
public static final String Table_Name="C_LandedCost";

protected static KeyNamePair Model = new KeyNamePair(759,"C_LandedCost");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_LandedCost[").append(getID()).append("]");
return sb.toString();
}
/** Set Invoice Line.
Invoice Detail Line */
public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
{
set_ValueNoCheck ("C_InvoiceLine_ID", new Integer(C_InvoiceLine_ID));
}
/** Get Invoice Line.
Invoice Detail Line */
public int getC_InvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_InvoiceLine_ID()));
}
/** Set Landed Cost.
Landed cost to be allocated to material receipts */
public void setC_LandedCost_ID (int C_LandedCost_ID)
{
set_ValueNoCheck ("C_LandedCost_ID", new Integer(C_LandedCost_ID));
}
/** Get Landed Cost.
Landed cost to be allocated to material receipts */
public int getC_LandedCost_ID() 
{
Integer ii = (Integer)get_Value("C_LandedCost_ID");
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
public static final int LANDEDCOSTDISTRIBUTION_AD_Reference_ID=339;
/** Quantity = Q */
public static final String LANDEDCOSTDISTRIBUTION_Quantity = "Q";
/** Line = L */
public static final String LANDEDCOSTDISTRIBUTION_Line = "L";
/** Volume = V */
public static final String LANDEDCOSTDISTRIBUTION_Volume = "V";
/** Weight = W */
public static final String LANDEDCOSTDISTRIBUTION_Weight = "W";
/** Costs = C */
public static final String LANDEDCOSTDISTRIBUTION_Costs = "C";
/** Set Cost Distribution.
Landed Cost Distribution */
public void setLandedCostDistribution (String LandedCostDistribution)
{
if (LandedCostDistribution == null || LandedCostDistribution.equals("Q") || LandedCostDistribution.equals("L") || LandedCostDistribution.equals("V") || LandedCostDistribution.equals("W") || LandedCostDistribution.equals("C"));
 else throw new IllegalArgumentException ("LandedCostDistribution Invalid value - Reference_ID=339 - Q - L - V - W - C");
if (LandedCostDistribution != null && LandedCostDistribution.length() > 1)
{
log.warning("Length > 1 - truncated");
LandedCostDistribution = LandedCostDistribution.substring(0,0);
}
set_Value ("LandedCostDistribution", LandedCostDistribution);
}
/** Get Cost Distribution.
Landed Cost Distribution */
public String getLandedCostDistribution() 
{
return (String)get_Value("LandedCostDistribution");
}
/** Set Cost Element.
Product Cost Element */
public void setM_CostElement_ID (int M_CostElement_ID)
{
set_Value ("M_CostElement_ID", new Integer(M_CostElement_ID));
}
/** Get Cost Element.
Product Cost Element */
public int getM_CostElement_ID() 
{
Integer ii = (Integer)get_Value("M_CostElement_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipment/Receipt Line.
Line on Shipment or Receipt document */
public void setM_InOutLine_ID (int M_InOutLine_ID)
{
if (M_InOutLine_ID <= 0) set_Value ("M_InOutLine_ID", null);
 else 
set_Value ("M_InOutLine_ID", new Integer(M_InOutLine_ID));
}
/** Get Shipment/Receipt Line.
Line on Shipment or Receipt document */
public int getM_InOutLine_ID() 
{
Integer ii = (Integer)get_Value("M_InOutLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipment/Receipt.
Material Shipment Document */
public void setM_InOut_ID (int M_InOut_ID)
{
if (M_InOut_ID <= 0) set_Value ("M_InOut_ID", null);
 else 
set_Value ("M_InOut_ID", new Integer(M_InOut_ID));
}
/** Get Shipment/Receipt.
Material Shipment Document */
public int getM_InOut_ID() 
{
Integer ii = (Integer)get_Value("M_InOut_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
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
}
