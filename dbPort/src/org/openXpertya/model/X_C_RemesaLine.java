/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RemesaLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.375 */
public class X_C_RemesaLine extends PO
{
/** Constructor estándar */
public X_C_RemesaLine (Properties ctx, int C_RemesaLine_ID, String trxName)
{
super (ctx, C_RemesaLine_ID, trxName);
/** if (C_RemesaLine_ID == 0)
{
setC_BPartner_ID (0);
setC_RemesaLine_ID (0);
setC_Remesa_ID (0);
}
 */
}
/** Load Constructor */
public X_C_RemesaLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000095 */
public static final int Table_ID=1000095;

/** TableName=C_RemesaLine */
public static final String Table_Name="C_RemesaLine";

protected static KeyNamePair Model = new KeyNamePair(1000095,"C_RemesaLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RemesaLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_RemesaLine_ID */
public void setC_RemesaLine_ID (int C_RemesaLine_ID)
{
set_ValueNoCheck ("C_RemesaLine_ID", new Integer(C_RemesaLine_ID));
}
/** Get C_RemesaLine_ID */
public int getC_RemesaLine_ID() 
{
Integer ii = (Integer)get_Value("C_RemesaLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Remesa_ID */
public void setC_Remesa_ID (int C_Remesa_ID)
{
set_Value ("C_Remesa_ID", new Integer(C_Remesa_ID));
}
/** Get C_Remesa_ID */
public int getC_Remesa_ID() 
{
Integer ii = (Integer)get_Value("C_Remesa_ID");
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
}
