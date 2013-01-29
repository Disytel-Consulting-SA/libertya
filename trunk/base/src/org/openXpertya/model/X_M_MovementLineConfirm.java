/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_MovementLineConfirm
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:38.64 */
public class X_M_MovementLineConfirm extends PO
{
/** Constructor estándar */
public X_M_MovementLineConfirm (Properties ctx, int M_MovementLineConfirm_ID, String trxName)
{
super (ctx, M_MovementLineConfirm_ID, trxName);
/** if (M_MovementLineConfirm_ID == 0)
{
setConfirmedQty (Env.ZERO);
setDifferenceQty (Env.ZERO);
setM_MovementConfirm_ID (0);
setM_MovementLineConfirm_ID (0);
setM_MovementLine_ID (0);
setM_Product_ID (0);
setProcessed (false);
setScrappedQty (Env.ZERO);
setTargetQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_MovementLineConfirm (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=737 */
public static final int Table_ID=737;

/** TableName=M_MovementLineConfirm */
public static final String Table_Name="M_MovementLineConfirm";

protected static KeyNamePair Model = new KeyNamePair(737,"M_MovementLineConfirm");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_MovementLineConfirm[").append(getID()).append("]");
return sb.toString();
}
/** Set Confirmed Quantity.
Confirmation of a received quantity */
public void setConfirmedQty (BigDecimal ConfirmedQty)
{
if (ConfirmedQty == null) throw new IllegalArgumentException ("ConfirmedQty is mandatory");
set_Value ("ConfirmedQty", ConfirmedQty);
}
/** Get Confirmed Quantity.
Confirmation of a received quantity */
public BigDecimal getConfirmedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ConfirmedQty");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Difference.
Difference Quantity */
public void setDifferenceQty (BigDecimal DifferenceQty)
{
if (DifferenceQty == null) throw new IllegalArgumentException ("DifferenceQty is mandatory");
set_Value ("DifferenceQty", DifferenceQty);
}
/** Get Difference.
Difference Quantity */
public BigDecimal getDifferenceQty() 
{
BigDecimal bd = (BigDecimal)get_Value("DifferenceQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Phys.Inventory Line.
Unique line in an Inventory document */
public void setM_InventoryLine_ID (int M_InventoryLine_ID)
{
if (M_InventoryLine_ID <= 0) set_Value ("M_InventoryLine_ID", null);
 else 
set_Value ("M_InventoryLine_ID", new Integer(M_InventoryLine_ID));
}
/** Get Phys.Inventory Line.
Unique line in an Inventory document */
public int getM_InventoryLine_ID() 
{
Integer ii = (Integer)get_Value("M_InventoryLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Move Confirm.
Inventory Move Confirmation */
public void setM_MovementConfirm_ID (int M_MovementConfirm_ID)
{
set_ValueNoCheck ("M_MovementConfirm_ID", new Integer(M_MovementConfirm_ID));
}
/** Get Move Confirm.
Inventory Move Confirmation */
public int getM_MovementConfirm_ID() 
{
Integer ii = (Integer)get_Value("M_MovementConfirm_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_MovementConfirm_ID()));
}
/** Set Move Line Confirm.
Inventory Move Line Confirmation */
public void setM_MovementLineConfirm_ID (int M_MovementLineConfirm_ID)
{
set_ValueNoCheck ("M_MovementLineConfirm_ID", new Integer(M_MovementLineConfirm_ID));
}
/** Get Move Line Confirm.
Inventory Move Line Confirmation */
public int getM_MovementLineConfirm_ID() 
{
Integer ii = (Integer)get_Value("M_MovementLineConfirm_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Move Line.
Inventory Move document Line */
public void setM_MovementLine_ID (int M_MovementLine_ID)
{
set_Value ("M_MovementLine_ID", new Integer(M_MovementLine_ID));
}
/** Get Move Line.
Inventory Move document Line */
public int getM_MovementLine_ID() 
{
Integer ii = (Integer)get_Value("M_MovementLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_Product_ID */
public void setM_Product_ID (int M_Product_ID)
{
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get M_Product_ID */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Scrapped Quantity.
The Quantity scrapped due to QA issues */
public void setScrappedQty (BigDecimal ScrappedQty)
{
if (ScrappedQty == null) throw new IllegalArgumentException ("ScrappedQty is mandatory");
set_Value ("ScrappedQty", ScrappedQty);
}
/** Get Scrapped Quantity.
The Quantity scrapped due to QA issues */
public BigDecimal getScrappedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ScrappedQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Target Quantity.
Target Movement Quantity */
public void setTargetQty (BigDecimal TargetQty)
{
if (TargetQty == null) throw new IllegalArgumentException ("TargetQty is mandatory");
set_Value ("TargetQty", TargetQty);
}
/** Get Target Quantity.
Target Movement Quantity */
public BigDecimal getTargetQty() 
{
BigDecimal bd = (BigDecimal)get_Value("TargetQty");
if (bd == null) return Env.ZERO;
return bd;
}
}
