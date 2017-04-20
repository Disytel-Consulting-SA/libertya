/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BankListLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-04-20 14:01:30.309 */
public class X_C_BankListLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BankListLine (Properties ctx, int C_BankListLine_ID, String trxName)
{
super (ctx, C_BankListLine_ID, trxName);
/** if (C_BankListLine_ID == 0)
{
setC_AllocationHdr_ID (0);
setC_BankList_ID (0);
setC_BankListLine_ID (0);
setElectronicPaymentTotal (Env.ZERO);
setLine (Env.ZERO);	// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM C_BankListLine WHERE C_BankList_ID=@C_BankList_ID@
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_C_BankListLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BankListLine");

/** TableName=C_BankListLine */
public static final String Table_Name="C_BankListLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BankListLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BankListLine[").append(getID()).append("]");
return sb.toString();
}
public static final int C_ALLOCATIONHDR_ID_AD_Reference_ID = MReference.getReferenceID("C_Allocation");
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
/** Set Bank List */
public void setC_BankList_ID (int C_BankList_ID)
{
set_Value ("C_BankList_ID", new Integer(C_BankList_ID));
}
/** Get Bank List */
public int getC_BankList_ID() 
{
Integer ii = (Integer)get_Value("C_BankList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_BankListLine_ID */
public void setC_BankListLine_ID (int C_BankListLine_ID)
{
set_ValueNoCheck ("C_BankListLine_ID", new Integer(C_BankListLine_ID));
}
/** Get C_BankListLine_ID */
public int getC_BankListLine_ID() 
{
Integer ii = (Integer)get_Value("C_BankListLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
throw new IllegalArgumentException ("C_BPartner_ID is virtual column");
}
/** Get Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
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
/** Set Electronic Payment Total */
public void setElectronicPaymentTotal (BigDecimal ElectronicPaymentTotal)
{
if (ElectronicPaymentTotal == null) throw new IllegalArgumentException ("ElectronicPaymentTotal is mandatory");
set_Value ("ElectronicPaymentTotal", ElectronicPaymentTotal);
}
/** Get Electronic Payment Total */
public BigDecimal getElectronicPaymentTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("ElectronicPaymentTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Total */
public void setGrandTotal (BigDecimal GrandTotal)
{
throw new IllegalArgumentException ("GrandTotal is virtual column");
}
/** Get Total */
public BigDecimal getGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("GrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line No.
Unique line for this document */
public void setLine (BigDecimal Line)
{
if (Line == null) throw new IllegalArgumentException ("Line is mandatory");
set_Value ("Line", Line);
}
/** Get Line No.
Unique line for this document */
public BigDecimal getLine() 
{
BigDecimal bd = (BigDecimal)get_Value("Line");
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
/** Set Payment Order Date */
public void setTrxDate (Timestamp TrxDate)
{
throw new IllegalArgumentException ("TrxDate is virtual column");
}
/** Get Payment Order Date */
public Timestamp getTrxDate() 
{
return (Timestamp)get_Value("TrxDate");
}
}
