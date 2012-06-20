/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PaymentFix
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-01-24 17:44:48.028 */
public class X_C_PaymentFix extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_PaymentFix (Properties ctx, int C_PaymentFix_ID, String trxName)
{
super (ctx, C_PaymentFix_ID, trxName);
/** if (C_PaymentFix_ID == 0)
{
setAllocatedAmt (Env.ZERO);
setBalance (Env.ZERO);
setC_AllocationHdr_ID (0);
setC_PaymentFix_ID (0);
setDocumentNo (null);
setProcessed (false);
setVoidedAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_PaymentFix (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_PaymentFix");

/** TableName=C_PaymentFix */
public static final String Table_Name="C_PaymentFix";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_PaymentFix");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PaymentFix[").append(getID()).append("]");
return sb.toString();
}
/** Set Allocated Amountt.
Amount allocated to this document */
public void setAllocatedAmt (BigDecimal AllocatedAmt)
{
if (AllocatedAmt == null) throw new IllegalArgumentException ("AllocatedAmt is mandatory");
set_Value ("AllocatedAmt", AllocatedAmt);
}
/** Get Allocated Amountt.
Amount allocated to this document */
public BigDecimal getAllocatedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("AllocatedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Balance */
public void setBalance (BigDecimal Balance)
{
if (Balance == null) throw new IllegalArgumentException ("Balance is mandatory");
set_Value ("Balance", Balance);
}
/** Get Balance */
public BigDecimal getBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("Balance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Allocation.
Payment allocation */
public void setC_AllocationHdr_ID (int C_AllocationHdr_ID)
{
set_Value ("C_AllocationHdr_ID", new Integer(C_AllocationHdr_ID));
}
/** Get Allocation.
Payment allocation */
public int getC_AllocationHdr_ID() 
{
Integer ii = (Integer)get_Value("C_AllocationHdr_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_Value ("C_Invoice_ID", null);
 else 
set_Value ("C_Invoice_ID", new Integer(C_Invoice_ID));
}
/** Get Invoice.
Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Fix.
Payment Fix */
public void setC_PaymentFix_ID (int C_PaymentFix_ID)
{
set_ValueNoCheck ("C_PaymentFix_ID", new Integer(C_PaymentFix_ID));
}
/** Get Payment Fix.
Payment Fix */
public int getC_PaymentFix_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentFix_ID");
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
Description = Description.substring(0,255);
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
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo == null) throw new IllegalArgumentException ("DocumentNo is mandatory");
if (DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,30);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDocumentNo());
}
/** Set Process */
public void setProcess (String Process)
{
if (Process != null && Process.length() > 1)
{
log.warning("Length > 1 - truncated");
Process = Process.substring(0,1);
}
set_Value ("Process", Process);
}
/** Get Process */
public String getProcess() 
{
return (String)get_Value("Process");
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
/** Set Voided Amount.
Voided Amount */
public void setVoidedAmt (BigDecimal VoidedAmt)
{
if (VoidedAmt == null) throw new IllegalArgumentException ("VoidedAmt is mandatory");
set_Value ("VoidedAmt", VoidedAmt);
}
/** Get Voided Amount.
Voided Amount */
public BigDecimal getVoidedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("VoidedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
}
