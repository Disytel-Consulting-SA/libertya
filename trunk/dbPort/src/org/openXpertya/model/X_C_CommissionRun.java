/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CommissionRun
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.296 */
public class X_C_CommissionRun extends PO
{
/** Constructor estándar */
public X_C_CommissionRun (Properties ctx, int C_CommissionRun_ID, String trxName)
{
super (ctx, C_CommissionRun_ID, trxName);
/** if (C_CommissionRun_ID == 0)
{
setC_CommissionRun_ID (0);
setC_Commission_ID (0);
setDocumentNo (null);
setGrandTotal (Env.ZERO);
setProcessed (false);
setStartDate (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_C_CommissionRun (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=436 */
public static final int Table_ID=436;

/** TableName=C_CommissionRun */
public static final String Table_Name="C_CommissionRun";

protected static KeyNamePair Model = new KeyNamePair(436,"C_CommissionRun");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CommissionRun[").append(getID()).append("]");
return sb.toString();
}
/** Set Commission Run.
Commission Run or Process */
public void setC_CommissionRun_ID (int C_CommissionRun_ID)
{
set_ValueNoCheck ("C_CommissionRun_ID", new Integer(C_CommissionRun_ID));
}
/** Get Commission Run.
Commission Run or Process */
public int getC_CommissionRun_ID() 
{
Integer ii = (Integer)get_Value("C_CommissionRun_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Commission.
Commission */
public void setC_Commission_ID (int C_Commission_ID)
{
set_ValueNoCheck ("C_Commission_ID", new Integer(C_Commission_ID));
}
/** Get Commission.
Commission */
public int getC_Commission_ID() 
{
Integer ii = (Integer)get_Value("C_Commission_ID");
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
/** Set Document No.
Document sequence number of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo == null) throw new IllegalArgumentException ("DocumentNo is mandatory");
if (DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,29);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence number of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDocumentNo());
}
/** Set Grand Total.
Total amount of document */
public void setGrandTotal (BigDecimal GrandTotal)
{
if (GrandTotal == null) throw new IllegalArgumentException ("GrandTotal is mandatory");
set_ValueNoCheck ("GrandTotal", GrandTotal);
}
/** Get Grand Total.
Total amount of document */
public BigDecimal getGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("GrandTotal");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Start Date.
First effective day (inclusive) */
public void setStartDate (Timestamp StartDate)
{
if (StartDate == null) throw new IllegalArgumentException ("StartDate is mandatory");
set_Value ("StartDate", StartDate);
}
/** Get Start Date.
First effective day (inclusive) */
public Timestamp getStartDate() 
{
return (Timestamp)get_Value("StartDate");
}
}
