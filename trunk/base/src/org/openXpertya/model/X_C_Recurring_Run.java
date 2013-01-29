/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Recurring_Run
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.328 */
public class X_C_Recurring_Run extends PO
{
/** Constructor estándar */
public X_C_Recurring_Run (Properties ctx, int C_Recurring_Run_ID, String trxName)
{
super (ctx, C_Recurring_Run_ID, trxName);
/** if (C_Recurring_Run_ID == 0)
{
setC_Recurring_ID (0);
setC_Recurring_Run_ID (0);
}
 */
}
/** Load Constructor */
public X_C_Recurring_Run (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=573 */
public static final int Table_ID=573;

/** TableName=C_Recurring_Run */
public static final String Table_Name="C_Recurring_Run";

protected static KeyNamePair Model = new KeyNamePair(573,"C_Recurring_Run");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Recurring_Run[").append(getID()).append("]");
return sb.toString();
}
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_ValueNoCheck ("C_Invoice_ID", null);
 else 
set_ValueNoCheck ("C_Invoice_ID", new Integer(C_Invoice_ID));
}
/** Get Invoice.
Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
if (C_Order_ID <= 0) set_ValueNoCheck ("C_Order_ID", null);
 else 
set_ValueNoCheck ("C_Order_ID", new Integer(C_Order_ID));
}
/** Get Order.
Order */
public int getC_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
if (C_Payment_ID <= 0) set_ValueNoCheck ("C_Payment_ID", null);
 else 
set_ValueNoCheck ("C_Payment_ID", new Integer(C_Payment_ID));
}
/** Get Payment.
Payment identifier */
public int getC_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Payment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
if (C_Project_ID <= 0) set_ValueNoCheck ("C_Project_ID", null);
 else 
set_ValueNoCheck ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Recurring.
Recurring Document */
public void setC_Recurring_ID (int C_Recurring_ID)
{
set_ValueNoCheck ("C_Recurring_ID", new Integer(C_Recurring_ID));
}
/** Get Recurring.
Recurring Document */
public int getC_Recurring_ID() 
{
Integer ii = (Integer)get_Value("C_Recurring_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Recurring Run.
Recurring Document Run */
public void setC_Recurring_Run_ID (int C_Recurring_Run_ID)
{
set_ValueNoCheck ("C_Recurring_Run_ID", new Integer(C_Recurring_Run_ID));
}
/** Get Recurring Run.
Recurring Document Run */
public int getC_Recurring_Run_ID() 
{
Integer ii = (Integer)get_Value("C_Recurring_Run_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Date.
Date of the Document */
public void setDateDoc (Timestamp DateDoc)
{
set_Value ("DateDoc", DateDoc);
}
/** Get Document Date.
Date of the Document */
public Timestamp getDateDoc() 
{
return (Timestamp)get_Value("DateDoc");
}
/** Set Journal Batch.
General Ledger Journal Batch */
public void setGL_JournalBatch_ID (int GL_JournalBatch_ID)
{
if (GL_JournalBatch_ID <= 0) set_ValueNoCheck ("GL_JournalBatch_ID", null);
 else 
set_ValueNoCheck ("GL_JournalBatch_ID", new Integer(GL_JournalBatch_ID));
}
/** Get Journal Batch.
General Ledger Journal Batch */
public int getGL_JournalBatch_ID() 
{
Integer ii = (Integer)get_Value("GL_JournalBatch_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
