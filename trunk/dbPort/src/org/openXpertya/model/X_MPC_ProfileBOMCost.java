/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_ProfileBOMCost
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.625 */
public class X_MPC_ProfileBOMCost extends PO
{
/** Constructor estándar */
public X_MPC_ProfileBOMCost (Properties ctx, int MPC_ProfileBOMCost_ID, String trxName)
{
super (ctx, MPC_ProfileBOMCost_ID, trxName);
/** if (MPC_ProfileBOMCost_ID == 0)
{
setMPC_ProfileBOMCost_ID (0);
setMPC_ProfileBOM_ID (0);
setM_ProductE_ID (0);
}
 */
}
/** Load Constructor */
public X_MPC_ProfileBOMCost (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000045 */
public static final int Table_ID=1000045;

/** TableName=MPC_ProfileBOMCost */
public static final String Table_Name="MPC_ProfileBOMCost";

protected static KeyNamePair Model = new KeyNamePair(1000045,"MPC_ProfileBOMCost");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_ProfileBOMCost[").append(getID()).append("]");
return sb.toString();
}
/** Set Date Ordered.
Date of Order */
public void setDateOrdered (Timestamp DateOrdered)
{
set_Value ("DateOrdered", DateOrdered);
}
/** Get Date Ordered.
Date of Order */
public Timestamp getDateOrdered() 
{
return (Timestamp)get_Value("DateOrdered");
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
/** Set Envase */
public void setEnvase (BigDecimal Envase)
{
set_Value ("Envase", Envase);
}
/** Get Envase */
public BigDecimal getEnvase() 
{
BigDecimal bd = (BigDecimal)get_Value("Envase");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Generate To.
Generate To */
public void setGenerateTo (String GenerateTo)
{
if (GenerateTo != null && GenerateTo.length() > 1)
{
log.warning("Length > 1 - truncated");
GenerateTo = GenerateTo.substring(0,0);
}
set_Value ("GenerateTo", GenerateTo);
}
/** Get Generate To.
Generate To */
public String getGenerateTo() 
{
return (String)get_Value("GenerateTo");
}
/** Set IsPacking */
public void setIsPacking (boolean IsPacking)
{
set_Value ("IsPacking", new Boolean(IsPacking));
}
/** Get IsPacking */
public boolean isPacking() 
{
Object oo = get_Value("IsPacking");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsUSD */
public void setIsUSD (boolean IsUSD)
{
set_Value ("IsUSD", new Boolean(IsUSD));
}
/** Get IsUSD */
public boolean isUSD() 
{
Object oo = get_Value("IsUSD");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Line Amount.
Line Extended Amount (Quantity * Actual Price) without Freight and Charges */
public void setLineNetAmt (BigDecimal LineNetAmt)
{
set_Value ("LineNetAmt", LineNetAmt);
}
/** Get Line Amount.
Line Extended Amount (Quantity * Actual Price) without Freight and Charges */
public BigDecimal getLineNetAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineNetAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line Total.
Total line amount incl. Tax */
public void setLineTotalAmt (BigDecimal LineTotalAmt)
{
set_Value ("LineTotalAmt", LineTotalAmt);
}
/** Get Line Total.
Total line amount incl. Tax */
public BigDecimal getLineTotalAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineTotalAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set MPC_ProfileBOMCost_ID */
public void setMPC_ProfileBOMCost_ID (int MPC_ProfileBOMCost_ID)
{
set_ValueNoCheck ("MPC_ProfileBOMCost_ID", new Integer(MPC_ProfileBOMCost_ID));
}
/** Get MPC_ProfileBOMCost_ID */
public int getMPC_ProfileBOMCost_ID() 
{
Integer ii = (Integer)get_Value("MPC_ProfileBOMCost_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set MPC_ProfileBOM_ID */
public void setMPC_ProfileBOM_ID (int MPC_ProfileBOM_ID)
{
set_ValueNoCheck ("MPC_ProfileBOM_ID", new Integer(MPC_ProfileBOM_ID));
}
/** Get MPC_ProfileBOM_ID */
public int getMPC_ProfileBOM_ID() 
{
Integer ii = (Integer)get_Value("MPC_ProfileBOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Price List.
Unique identifier of a Price List */
public void setM_PriceList_ID (int M_PriceList_ID)
{
if (M_PriceList_ID <= 0) set_Value ("M_PriceList_ID", null);
 else 
set_Value ("M_PriceList_ID", new Integer(M_PriceList_ID));
}
/** Get Price List.
Unique identifier of a Price List */
public int getM_PriceList_ID() 
{
Integer ii = (Integer)get_Value("M_PriceList_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCTE_ID_AD_Reference_ID=1000032;
/** Set M_ProductE_ID */
public void setM_ProductE_ID (int M_ProductE_ID)
{
set_Value ("M_ProductE_ID", new Integer(M_ProductE_ID));
}
/** Get M_ProductE_ID */
public int getM_ProductE_ID() 
{
Integer ii = (Integer)get_Value("M_ProductE_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCTET_ID_AD_Reference_ID=1000034;
/** Set M_ProductEt_ID */
public void setM_ProductEt_ID (int M_ProductEt_ID)
{
if (M_ProductEt_ID <= 0) set_Value ("M_ProductEt_ID", null);
 else 
set_Value ("M_ProductEt_ID", new Integer(M_ProductEt_ID));
}
/** Get M_ProductEt_ID */
public int getM_ProductEt_ID() 
{
Integer ii = (Integer)get_Value("M_ProductEt_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCTT_ID_AD_Reference_ID=1000033;
/** Set M_ProductT_ID */
public void setM_ProductT_ID (int M_ProductT_ID)
{
if (M_ProductT_ID <= 0) set_Value ("M_ProductT_ID", null);
 else 
set_Value ("M_ProductT_ID", new Integer(M_ProductT_ID));
}
/** Get M_ProductT_ID */
public int getM_ProductT_ID() 
{
Integer ii = (Integer)get_Value("M_ProductT_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Margin %.
Margin for a product as a percentage */
public void setMargin (BigDecimal Margin)
{
set_Value ("Margin", Margin);
}
/** Get Margin %.
Margin for a product as a percentage */
public BigDecimal getMargin() 
{
BigDecimal bd = (BigDecimal)get_Value("Margin");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Offer Amount.
Amount of the Offer */
public void setOfferAmt (BigDecimal OfferAmt)
{
set_Value ("OfferAmt", OfferAmt);
}
/** Get Offer Amount.
Amount of the Offer */
public BigDecimal getOfferAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("OfferAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PriceE */
public void setPriceE (BigDecimal PriceE)
{
set_Value ("PriceE", PriceE);
}
/** Get PriceE */
public BigDecimal getPriceE() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceE");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PriceEt */
public void setPriceEt (BigDecimal PriceEt)
{
set_Value ("PriceEt", PriceEt);
}
/** Get PriceEt */
public BigDecimal getPriceEt() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceEt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PriceF */
public void setPriceF (BigDecimal PriceF)
{
set_Value ("PriceF", PriceF);
}
/** Get PriceF */
public BigDecimal getPriceF() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceF");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PriceP */
public void setPriceP (BigDecimal PriceP)
{
set_Value ("PriceP", PriceP);
}
/** Get PriceP */
public BigDecimal getPriceP() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceP");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PriceT */
public void setPriceT (BigDecimal PriceT)
{
set_Value ("PriceT", PriceT);
}
/** Get PriceT */
public BigDecimal getPriceT() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceT");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set QtyE */
public void setQtyE (BigDecimal QtyE)
{
set_Value ("QtyE", QtyE);
}
/** Get QtyE */
public BigDecimal getQtyE() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyE");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set QtyEt */
public void setQtyEt (BigDecimal QtyEt)
{
set_Value ("QtyEt", QtyEt);
}
/** Get QtyEt */
public BigDecimal getQtyEt() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyEt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Ordered Quantity.
Ordered Quantity */
public void setQtyOrdered (BigDecimal QtyOrdered)
{
set_Value ("QtyOrdered", QtyOrdered);
}
/** Get Ordered Quantity.
Ordered Quantity */
public BigDecimal getQtyOrdered() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyOrdered");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set QtyT */
public void setQtyT (BigDecimal QtyT)
{
set_Value ("QtyT", QtyT);
}
/** Get QtyT */
public BigDecimal getQtyT() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyT");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Resource.
Resource */
public void setS_Resource_ID (int S_Resource_ID)
{
if (S_Resource_ID <= 0) set_Value ("S_Resource_ID", null);
 else 
set_Value ("S_Resource_ID", new Integer(S_Resource_ID));
}
/** Get Resource.
Resource */
public int getS_Resource_ID() 
{
Integer ii = (Integer)get_Value("S_Resource_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
