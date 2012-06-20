/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_InOutLineConfirm
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:38.14 */
public class X_M_InOutLineConfirm extends PO
{
/** Constructor estándar */
public X_M_InOutLineConfirm (Properties ctx, int M_InOutLineConfirm_ID, String trxName)
{
super (ctx, M_InOutLineConfirm_ID, trxName);
/** if (M_InOutLineConfirm_ID == 0)
{
setConfirmedQty (Env.ZERO);
setM_InOutConfirm_ID (0);
setM_InOutLineConfirm_ID (0);
setM_InOutLine_ID (0);
setProcessed (false);
setTargetQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_InOutLineConfirm (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=728 */
public static final int Table_ID=728;

/** TableName=M_InOutLineConfirm */
public static final String Table_Name="M_InOutLineConfirm";

protected static KeyNamePair Model = new KeyNamePair(728,"M_InOutLineConfirm");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_InOutLineConfirm[").append(getID()).append("]");
return sb.toString();
}
/** Set Invoice Line.
Invoice Detail Line */
public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
{
if (C_InvoiceLine_ID <= 0) set_Value ("C_InvoiceLine_ID", null);
 else 
set_Value ("C_InvoiceLine_ID", new Integer(C_InvoiceLine_ID));
}
/** Get Invoice Line.
Invoice Detail Line */
public int getC_InvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Confirmation No.
Confirmation Number */
public void setConfirmationNo (String ConfirmationNo)
{
if (ConfirmationNo != null && ConfirmationNo.length() > 20)
{
log.warning("Length > 20 - truncated");
ConfirmationNo = ConfirmationNo.substring(0,19);
}
set_Value ("ConfirmationNo", ConfirmationNo);
}
/** Get Confirmation No.
Confirmation Number */
public String getConfirmationNo() 
{
return (String)get_Value("ConfirmationNo");
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
/** Set Ship/Receipt Confirmation.
Material Shipment or Receipt Confirmation */
public void setM_InOutConfirm_ID (int M_InOutConfirm_ID)
{
set_ValueNoCheck ("M_InOutConfirm_ID", new Integer(M_InOutConfirm_ID));
}
/** Get Ship/Receipt Confirmation.
Material Shipment or Receipt Confirmation */
public int getM_InOutConfirm_ID() 
{
Integer ii = (Integer)get_Value("M_InOutConfirm_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Ship/Receipt Confirmation Line.
Material Shipment or Receipt Confirmation Line */
public void setM_InOutLineConfirm_ID (int M_InOutLineConfirm_ID)
{
set_ValueNoCheck ("M_InOutLineConfirm_ID", new Integer(M_InOutLineConfirm_ID));
}
/** Get Ship/Receipt Confirmation Line.
Material Shipment or Receipt Confirmation Line */
public int getM_InOutLineConfirm_ID() 
{
Integer ii = (Integer)get_Value("M_InOutLineConfirm_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipment/Receipt Line.
Line on Shipment or Receipt document */
public void setM_InOutLine_ID (int M_InOutLine_ID)
{
set_ValueNoCheck ("M_InOutLine_ID", new Integer(M_InOutLine_ID));
}
/** Get Shipment/Receipt Line.
Line on Shipment or Receipt document */
public int getM_InOutLine_ID() 
{
Integer ii = (Integer)get_Value("M_InOutLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_InOutLine_ID()));
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
set_ValueNoCheck ("TargetQty", TargetQty);
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
