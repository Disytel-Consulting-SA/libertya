/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_SumsAndBalance
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-06-18 14:08:53.515 */
public class X_T_SumsAndBalance extends PO
{
/** Constructor estándar */
public X_T_SumsAndBalance (Properties ctx, int T_SumsAndBalance_ID, String trxName)
{
super (ctx, T_SumsAndBalance_ID, trxName);
/** if (T_SumsAndBalance_ID == 0)
{
setAD_PInstance_ID (0);
setSubindex (0);
setT_SumsAndBalance_ID (0);
}
 */
}
/** Load Constructor */
public X_T_SumsAndBalance (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000176 */
public static final int Table_ID=1000176;

/** TableName=T_SumsAndBalance */
public static final String Table_Name="T_SumsAndBalance";

protected static KeyNamePair Model = new KeyNamePair(1000176,"T_SumsAndBalance");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_SumsAndBalance[").append(getID()).append("]");
return sb.toString();
}
/** Set Account Code */
public void setAcct_Code (String Acct_Code)
{
if (Acct_Code != null && Acct_Code.length() > 255)
{
log.warning("Length > 255 - truncated");
Acct_Code = Acct_Code.substring(0,255);
}
set_Value ("Acct_Code", Acct_Code);
}
/** Get Account Code */
public String getAcct_Code() 
{
return (String)get_Value("Acct_Code");
}
/** Set Account Description */
public void setAcct_Description (String Acct_Description)
{
if (Acct_Description != null && Acct_Description.length() > 512)
{
log.warning("Length > 512 - truncated");
Acct_Description = Acct_Description.substring(0,512);
}
set_Value ("Acct_Description", Acct_Description);
}
/** Get Account Description */
public String getAcct_Description() 
{
return (String)get_Value("Acct_Description");
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
/** Set Account Element.
Account Element */
public void setC_ElementValue_ID (int C_ElementValue_ID)
{
if (C_ElementValue_ID <= 0) set_Value ("C_ElementValue_ID", null);
 else 
set_Value ("C_ElementValue_ID", new Integer(C_ElementValue_ID));
}
/** Get Account Element.
Account Element */
public int getC_ElementValue_ID() 
{
Integer ii = (Integer)get_Value("C_ElementValue_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Credit */
public void setCredit (BigDecimal Credit)
{
set_Value ("Credit", Credit);
}
/** Get Credit */
public BigDecimal getCredit() 
{
BigDecimal bd = (BigDecimal)get_Value("Credit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Balance */
public void setCreditBalance (BigDecimal CreditBalance)
{
set_Value ("CreditBalance", CreditBalance);
}
/** Get Credit Balance */
public BigDecimal getCreditBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditBalance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Account Date.
Accounting Date */
public void setDateAcct (Timestamp DateAcct)
{
set_Value ("DateAcct", DateAcct);
}
/** Get Account Date.
Accounting Date */
public Timestamp getDateAcct() 
{
return (Timestamp)get_Value("DateAcct");
}
/** Set Debit */
public void setDebit (BigDecimal Debit)
{
set_Value ("Debit", Debit);
}
/** Get Debit */
public BigDecimal getDebit() 
{
BigDecimal bd = (BigDecimal)get_Value("Debit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Debit Balance */
public void setDebitBalance (BigDecimal DebitBalance)
{
set_Value ("DebitBalance", DebitBalance);
}
/** Get Debit Balance */
public BigDecimal getDebitBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("DebitBalance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Hierarchical Code */
public void setHierarchicalCode (String HierarchicalCode)
{
if (HierarchicalCode != null && HierarchicalCode.length() > 512)
{
log.warning("Length > 512 - truncated");
HierarchicalCode = HierarchicalCode.substring(0,512);
}
set_Value ("HierarchicalCode", HierarchicalCode);
}
/** Get Hierarchical Code */
public String getHierarchicalCode() 
{
return (String)get_Value("HierarchicalCode");
}
/** Set Subindex */
public void setSubindex (int Subindex)
{
set_Value ("Subindex", new Integer(Subindex));
}
/** Get Subindex */
public int getSubindex() 
{
Integer ii = (Integer)get_Value("Subindex");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sums And Balances */
public void setT_SumsAndBalance_ID (int T_SumsAndBalance_ID)
{
set_ValueNoCheck ("T_SumsAndBalance_ID", new Integer(T_SumsAndBalance_ID));
}
/** Get Sums And Balances */
public int getT_SumsAndBalance_ID() 
{
Integer ii = (Integer)get_Value("T_SumsAndBalance_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
