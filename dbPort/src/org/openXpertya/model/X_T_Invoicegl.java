/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_Invoicegl
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.0 */
public class X_T_Invoicegl extends PO
{
/** Constructor estándar */
public X_T_Invoicegl (Properties ctx, int T_Invoicegl_ID, String trxName)
{
super (ctx, T_Invoicegl_ID, trxName);
/** if (T_Invoicegl_ID == 0)
{
setAD_PInstance_ID (0);
setAmtRevalCr (0);
setAmtRevalCrDiff (0);
setAmtRevalDr (0);
setAmtRevalDrDiff (0);
setC_ConversionTypeReval_ID (0);
setC_Invoice_ID (0);
setDateReval (new Timestamp(System.currentTimeMillis()));
setFact_Acct_ID (0);
}
 */
}
/** Load Constructor */
public X_T_Invoicegl (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=803 */
public static final int Table_ID=803;

/** TableName=T_Invoicegl */
public static final String Table_Name="T_Invoicegl";

protected static KeyNamePair Model = new KeyNamePair(803,"T_Invoicegl");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_Invoicegl[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
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
/** Set Revaluated Amount Cr.
Revaluated Cr Amount */
public void setAmtRevalCr (int AmtRevalCr)
{
set_Value ("AmtRevalCr", new Integer(AmtRevalCr));
}
/** Get Revaluated Amount Cr.
Revaluated Cr Amount */
public int getAmtRevalCr() 
{
Integer ii = (Integer)get_Value("AmtRevalCr");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Revaluated Difference Cr.
Revaluated Cr Amount Difference */
public void setAmtRevalCrDiff (int AmtRevalCrDiff)
{
set_Value ("AmtRevalCrDiff", new Integer(AmtRevalCrDiff));
}
/** Get Revaluated Difference Cr.
Revaluated Cr Amount Difference */
public int getAmtRevalCrDiff() 
{
Integer ii = (Integer)get_Value("AmtRevalCrDiff");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Revaluated Amount Dr.
Revaluated Dr Amount */
public void setAmtRevalDr (int AmtRevalDr)
{
set_Value ("AmtRevalDr", new Integer(AmtRevalDr));
}
/** Get Revaluated Amount Dr.
Revaluated Dr Amount */
public int getAmtRevalDr() 
{
Integer ii = (Integer)get_Value("AmtRevalDr");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Revaluated Difference Dr.
Revaluated Dr Amount Difference */
public void setAmtRevalDrDiff (int AmtRevalDrDiff)
{
set_Value ("AmtRevalDrDiff", new Integer(AmtRevalDrDiff));
}
/** Get Revaluated Difference Dr.
Revaluated Dr Amount Difference */
public int getAmtRevalDrDiff() 
{
Integer ii = (Integer)get_Value("AmtRevalDrDiff");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Revaluation Conversion Type.
Revaluation Currency Conversion Type */
public void setC_ConversionTypeReval_ID (int C_ConversionTypeReval_ID)
{
set_Value ("C_ConversionTypeReval_ID", new Integer(C_ConversionTypeReval_ID));
}
/** Get Revaluation Conversion Type.
Revaluation Currency Conversion Type */
public int getC_ConversionTypeReval_ID() 
{
Integer ii = (Integer)get_Value("C_ConversionTypeReval_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
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
/** Set Revaluation Date.
Date of Revaluation */
public void setDateReval (Timestamp DateReval)
{
if (DateReval == null) throw new IllegalArgumentException ("DateReval is mandatory");
set_Value ("DateReval", DateReval);
}
/** Get Revaluation Date.
Date of Revaluation */
public Timestamp getDateReval() 
{
return (Timestamp)get_Value("DateReval");
}
/** Set Accounting Fact */
public void setFact_Acct_ID (int Fact_Acct_ID)
{
set_Value ("Fact_Acct_ID", new Integer(Fact_Acct_ID));
}
/** Get Accounting Fact */
public int getFact_Acct_ID() 
{
Integer ii = (Integer)get_Value("Fact_Acct_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
