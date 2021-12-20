/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Import_Clearance
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2021-12-20 13:54:09.665 */
public class X_M_Import_Clearance extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Import_Clearance (Properties ctx, int M_Import_Clearance_ID, String trxName)
{
super (ctx, M_Import_Clearance_ID, trxName);
/** if (M_Import_Clearance_ID == 0)
{
setClearanceNumber (null);
setM_Import_Clearance_ID (0);
setMovementDate (new Timestamp(System.currentTimeMillis()));
setM_Product_ID (0);
setQty (Env.ZERO);
setQtyUsed (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_Import_Clearance (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Import_Clearance");

/** TableName=M_Import_Clearance */
public static final String Table_Name="M_Import_Clearance";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Import_Clearance");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Import_Clearance[").append(getID()).append("]");
return sb.toString();
}
/** Set Clearance Number.
Import Clearance Number */
public void setClearanceNumber (String ClearanceNumber)
{
if (ClearanceNumber == null) throw new IllegalArgumentException ("ClearanceNumber is mandatory");
if (ClearanceNumber.length() > 255)
{
log.warning("Length > 255 - truncated");
ClearanceNumber = ClearanceNumber.substring(0,255);
}
set_Value ("ClearanceNumber", ClearanceNumber);
}
/** Get Clearance Number.
Import Clearance Number */
public String getClearanceNumber() 
{
return (String)get_Value("ClearanceNumber");
}
/** Set Import Clearance */
public void setM_Import_Clearance_ID (int M_Import_Clearance_ID)
{
set_ValueNoCheck ("M_Import_Clearance_ID", new Integer(M_Import_Clearance_ID));
}
/** Get Import Clearance */
public int getM_Import_Clearance_ID() 
{
Integer ii = (Integer)get_Value("M_Import_Clearance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Movement Date.
Date a product was moved in or out of inventory */
public void setMovementDate (Timestamp MovementDate)
{
if (MovementDate == null) throw new IllegalArgumentException ("MovementDate is mandatory");
set_Value ("MovementDate", MovementDate);
}
/** Get Movement Date.
Date a product was moved in or out of inventory */
public Timestamp getMovementDate() 
{
return (Timestamp)get_Value("MovementDate");
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
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
if (Qty == null) throw new IllegalArgumentException ("Qty is mandatory");
set_Value ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Qty Used */
public void setQtyUsed (BigDecimal QtyUsed)
{
if (QtyUsed == null) throw new IllegalArgumentException ("QtyUsed is mandatory");
set_Value ("QtyUsed", QtyUsed);
}
/** Get Qty Used */
public BigDecimal getQtyUsed() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyUsed");
if (bd == null) return Env.ZERO;
return bd;
}
}
