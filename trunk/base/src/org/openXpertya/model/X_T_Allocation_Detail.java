/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_Allocation_Detail
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.812 */
public class X_T_Allocation_Detail extends PO
{
/** Constructor estándar */
public X_T_Allocation_Detail (Properties ctx, int T_Allocation_Detail_ID, String trxName)
{
super (ctx, T_Allocation_Detail_ID, trxName);
/** if (T_Allocation_Detail_ID == 0)
{
setAD_PInstance_ID (0);
setC_AllocationHdr_ID (0);
setT_Allocation_Detail_ID (0);
}
 */
}
/** Load Constructor */
public X_T_Allocation_Detail (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000138 */
public static final int Table_ID=1000138;

/** TableName=T_Allocation_Detail */
public static final String Table_Name="T_Allocation_Detail";

protected static KeyNamePair Model = new KeyNamePair(1000138,"T_Allocation_Detail");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_Allocation_Detail[").append(getID()).append("]");
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
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
if (C_Currency_ID <= 0) set_Value ("C_Currency_ID", null);
 else 
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
/** Set Transaction Date.
Transaction Date */
public void setDateTrx (Timestamp DateTrx)
{
set_Value ("DateTrx", DateTrx);
}
/** Get Transaction Date.
Transaction Date */
public Timestamp getDateTrx() 
{
return (Timestamp)get_Value("DateTrx");
}
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 22)
{
log.warning("Length > 22 - truncated");
DocumentNo = DocumentNo.substring(0,21);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set Grand Total.
Total amount of document */
public void setGrandTotal (BigDecimal GrandTotal)
{
set_Value ("GrandTotal", GrandTotal);
}
/** Get Grand Total.
Total amount of document */
public BigDecimal getGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("GrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Allocation Detail */
public void setT_Allocation_Detail_ID (int T_Allocation_Detail_ID)
{
set_ValueNoCheck ("T_Allocation_Detail_ID", new Integer(T_Allocation_Detail_ID));
}
/** Get Allocation Detail */
public int getT_Allocation_Detail_ID() 
{
Integer ii = (Integer)get_Value("T_Allocation_Detail_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cash */
public void setcash (boolean cash)
{
set_Value ("cash", new Boolean(cash));
}
/** Get Cash */
public boolean iscash() 
{
Object oo = get_Value("cash");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set montosaldado */
public void setmontosaldado (BigDecimal montosaldado)
{
set_Value ("montosaldado", montosaldado);
}
/** Get montosaldado */
public BigDecimal getmontosaldado() 
{
BigDecimal bd = (BigDecimal)get_Value("montosaldado");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set pagonro */
public void setpagonro (String pagonro)
{
if (pagonro != null && pagonro.length() > 22)
{
log.warning("Length > 22 - truncated");
pagonro = pagonro.substring(0,21);
}
set_Value ("pagonro", pagonro);
}
/** Get pagonro */
public String getpagonro() 
{
return (String)get_Value("pagonro");
}
public static final int TIPO_AD_Reference_ID=214;
/** Check = K */
public static final String TIPO_Check = "K";
/** Direct Debit = D */
public static final String TIPO_DirectDebit = "D";
/** Direct Deposit = A */
public static final String TIPO_DirectDeposit = "A";
/** Credit Card = C */
public static final String TIPO_CreditCard = "C";
/** Cash = CA */
public static final String TIPO_Cash = "CA";
/** Set Tipo */
public void settipo (String tipo)
{
if (tipo == null || tipo.equals("K") || tipo.equals("D") || tipo.equals("A") || tipo.equals("C") || tipo.equals("CA"));
 else throw new IllegalArgumentException ("tipo Invalid value - Reference_ID=214 - K - D - A - C - CA");
if (tipo != null && tipo.length() > 22)
{
log.warning("Length > 22 - truncated");
tipo = tipo.substring(0,21);
}
set_Value ("tipo", tipo);
}
/** Get Tipo */
public String gettipo() 
{
return (String)get_Value("tipo");
}
}
