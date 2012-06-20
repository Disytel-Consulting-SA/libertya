/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_Lot
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.062 */
public class X_T_Lot extends PO
{
/** Constructor estándar */
public X_T_Lot (Properties ctx, int T_Lot_ID, String trxName)
{
super (ctx, T_Lot_ID, trxName);
/** if (T_Lot_ID == 0)
{
setMPC_Order_ID (0);
}
 */
}
/** Load Constructor */
public X_T_Lot (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000049 */
public static final int Table_ID=1000049;

/** TableName=T_Lot */
public static final String Table_Name="T_Lot";

protected static KeyNamePair Model = new KeyNamePair(1000049,"T_Lot");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_Lot[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
if (AD_PInstance_ID <= 0) set_Value ("AD_PInstance_ID", null);
 else 
set_Value ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set LotDoc */
public void setLotDoc (String LotDoc)
{
if (LotDoc != null && LotDoc.length() > 240)
{
log.warning("Length > 240 - truncated");
LotDoc = LotDoc.substring(0,239);
}
set_Value ("LotDoc", LotDoc);
}
/** Get LotDoc */
public String getLotDoc() 
{
return (String)get_Value("LotDoc");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getLotDoc());
}
/** Set Manufacturing Order.
Manufacturing Order */
public void setMPC_Order_ID (int MPC_Order_ID)
{
set_Value ("MPC_Order_ID", new Integer(MPC_Order_ID));
}
/** Get Manufacturing Order.
Manufacturing Order */
public int getMPC_Order_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_ID");
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
/** Set Qty Batch Size */
public void setQtyBatchSize (BigDecimal QtyBatchSize)
{
set_Value ("QtyBatchSize", QtyBatchSize);
}
/** Get Qty Batch Size */
public BigDecimal getQtyBatchSize() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyBatchSize");
if (bd == null) return Env.ZERO;
return bd;
}
}
