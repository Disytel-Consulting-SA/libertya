/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Remesa
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.359 */
public class X_C_Remesa extends PO
{
/** Constructor estándar */
public X_C_Remesa (Properties ctx, int C_Remesa_ID, String trxName)
{
super (ctx, C_Remesa_ID, trxName);
/** if (C_Remesa_ID == 0)
{
setC_Norma_ID (0);
setC_Remesa_ID (0);
setExecuteDate (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_C_Remesa (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000094 */
public static final int Table_ID=1000094;

/** TableName=C_Remesa */
public static final String Table_Name="C_Remesa";

protected static KeyNamePair Model = new KeyNamePair(1000094,"C_Remesa");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Remesa[").append(getID()).append("]");
return sb.toString();
}
public static final int C_NORMA_ID_AD_Reference_ID=1000043;
/** Set C_Norma_ID */
public void setC_Norma_ID (int C_Norma_ID)
{
set_Value ("C_Norma_ID", new Integer(C_Norma_ID));
}
/** Get C_Norma_ID */
public int getC_Norma_ID() 
{
Integer ii = (Integer)get_Value("C_Norma_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Remesa_ID */
public void setC_Remesa_ID (int C_Remesa_ID)
{
set_ValueNoCheck ("C_Remesa_ID", new Integer(C_Remesa_ID));
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
/** Set RxecuteDate */
public void setExecuteDate (Timestamp ExecuteDate)
{
if (ExecuteDate == null) throw new IllegalArgumentException ("ExecuteDate is mandatory");
set_Value ("ExecuteDate", ExecuteDate);
}
/** Get RxecuteDate */
public Timestamp getExecuteDate() 
{
return (Timestamp)get_Value("ExecuteDate");
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
/** Set Total Amount.
Total Amount */
public void setTotalAmt (BigDecimal TotalAmt)
{
set_Value ("TotalAmt", TotalAmt);
}
/** Get Total Amount.
Total Amount */
public BigDecimal getTotalAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set generate */
public void setgenerate (String generate)
{
if (generate != null && generate.length() > 1)
{
log.warning("Length > 1 - truncated");
generate = generate.substring(0,0);
}
set_Value ("generate", generate);
}
/** Get generate */
public String getgenerate() 
{
return (String)get_Value("generate");
}
}
