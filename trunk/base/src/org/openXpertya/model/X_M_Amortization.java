/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Amortization
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-01-09 10:57:09.034 */
public class X_M_Amortization extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Amortization (Properties ctx, int M_Amortization_ID, String trxName)
{
super (ctx, M_Amortization_ID, trxName);
/** if (M_Amortization_ID == 0)
{
setAmortizationDate (new Timestamp(System.currentTimeMillis()));	// @#Date@
setC_Currency_ID (0);
setC_DocType_ID (0);
setC_Period_ID (0);
setC_Year_ID (0);
setDocumentNo (null);
setM_Amortization_ID (0);
setPosted (false);	// N
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_M_Amortization (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Amortization");

/** TableName=M_Amortization */
public static final String Table_Name="M_Amortization";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Amortization");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Amortization[").append(getID()).append("]");
return sb.toString();
}
/** Set Amortization Date */
public void setAmortizationDate (Timestamp AmortizationDate)
{
if (AmortizationDate == null) throw new IllegalArgumentException ("AmortizationDate is mandatory");
set_Value ("AmortizationDate", AmortizationDate);
}
/** Get Amortization Date */
public Timestamp getAmortizationDate() 
{
return (Timestamp)get_Value("AmortizationDate");
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
set_Value ("C_DocType_ID", new Integer(C_DocType_ID));
}
/** Get Document Type.
Document type or rules */
public int getC_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Period.
Period of the Calendar */
public void setC_Period_ID (int C_Period_ID)
{
set_Value ("C_Period_ID", new Integer(C_Period_ID));
}
/** Get Period.
Period of the Calendar */
public int getC_Period_ID() 
{
Integer ii = (Integer)get_Value("C_Period_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Year.
Calendar Year */
public void setC_Year_ID (int C_Year_ID)
{
set_Value ("C_Year_ID", new Integer(C_Year_ID));
}
/** Get Year.
Calendar Year */
public int getC_Year_ID() 
{
Integer ii = (Integer)get_Value("C_Year_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Delete Amortization.
Delete Accounting, Amortization lines and Amortization */
public void setDeleteAmortization (String DeleteAmortization)
{
if (DeleteAmortization != null && DeleteAmortization.length() > 1)
{
log.warning("Length > 1 - truncated");
DeleteAmortization = DeleteAmortization.substring(0,1);
}
set_Value ("DeleteAmortization", DeleteAmortization);
}
/** Get Delete Amortization.
Delete Accounting, Amortization lines and Amortization */
public String getDeleteAmortization() 
{
return (String)get_Value("DeleteAmortization");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 60)
{
log.warning("Length > 60 - truncated");
Description = Description.substring(0,60);
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
/** Set Amortization */
public void setM_Amortization_ID (int M_Amortization_ID)
{
set_ValueNoCheck ("M_Amortization_ID", new Integer(M_Amortization_ID));
}
/** Get Amortization */
public int getM_Amortization_ID() 
{
Integer ii = (Integer)get_Value("M_Amortization_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Posted.
Posting status */
public void setPosted (boolean Posted)
{
set_Value ("Posted", new Boolean(Posted));
}
/** Get Posted.
Posting status */
public boolean isPosted() 
{
Object oo = get_Value("Posted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Run Amortization Process */
public void setRunAmortizationProcess (String RunAmortizationProcess)
{
if (RunAmortizationProcess != null && RunAmortizationProcess.length() > 1)
{
log.warning("Length > 1 - truncated");
RunAmortizationProcess = RunAmortizationProcess.substring(0,1);
}
set_Value ("RunAmortizationProcess", RunAmortizationProcess);
}
/** Get Run Amortization Process */
public String getRunAmortizationProcess() 
{
return (String)get_Value("RunAmortizationProcess");
}
}
