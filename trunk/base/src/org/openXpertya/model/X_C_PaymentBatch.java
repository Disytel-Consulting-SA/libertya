/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PaymentBatch
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:31.578 */
public class X_C_PaymentBatch extends PO
{
/** Constructor estándar */
public X_C_PaymentBatch (Properties ctx, int C_PaymentBatch_ID, String trxName)
{
super (ctx, C_PaymentBatch_ID, trxName);
/** if (C_PaymentBatch_ID == 0)
{
setC_PaymentBatch_ID (0);
setC_PaymentProcessor_ID (0);
setName (null);
setProcessed (false);
setProcessing (false);
}
 */
}
/** Load Constructor */
public X_C_PaymentBatch (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=411 */
public static final int Table_ID=411;

/** TableName=C_PaymentBatch */
public static final String Table_Name="C_PaymentBatch";

protected static KeyNamePair Model = new KeyNamePair(411,"C_PaymentBatch");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PaymentBatch[").append(getID()).append("]");
return sb.toString();
}
/** Set Payment Batch.
Payment batch for EFT */
public void setC_PaymentBatch_ID (int C_PaymentBatch_ID)
{
set_ValueNoCheck ("C_PaymentBatch_ID", new Integer(C_PaymentBatch_ID));
}
/** Get Payment Batch.
Payment batch for EFT */
public int getC_PaymentBatch_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentBatch_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Processor.
Payment processor for electronic payments */
public void setC_PaymentProcessor_ID (int C_PaymentProcessor_ID)
{
set_Value ("C_PaymentProcessor_ID", new Integer(C_PaymentProcessor_ID));
}
/** Get Payment Processor.
Payment processor for electronic payments */
public int getC_PaymentProcessor_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentProcessor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document No.
Document sequence number of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 30)
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
/** Set Processing date */
public void setProcessingDate (Timestamp ProcessingDate)
{
set_Value ("ProcessingDate", ProcessingDate);
}
/** Get Processing date */
public Timestamp getProcessingDate() 
{
return (Timestamp)get_Value("ProcessingDate");
}
}
