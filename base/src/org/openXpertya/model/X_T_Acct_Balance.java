/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_Acct_Balance
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-03-12 16:09:56.329 */
public class X_T_Acct_Balance extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_Acct_Balance (Properties ctx, int T_Acct_Balance_ID, String trxName)
{
super (ctx, T_Acct_Balance_ID, trxName);
/** if (T_Acct_Balance_ID == 0)
{
setAD_PInstance_ID (0);
setSubindex (0);
setT_Acct_Balance_ID (0);
}
 */
}
/** Load Constructor */
public X_T_Acct_Balance (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_Acct_Balance");

/** TableName=T_Acct_Balance */
public static final String Table_Name="T_Acct_Balance";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_Acct_Balance");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_Acct_Balance[").append(getID()).append("]");
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
/** Set Account Code */
public void setAcct_Code (String Acct_Code)
{
if (Acct_Code != null && Acct_Code.length() > 512)
{
log.warning("Length > 512 - truncated");
Acct_Code = Acct_Code.substring(0,512);
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
if (Acct_Description != null && Acct_Description.length() > 1024)
{
log.warning("Length > 1024 - truncated");
Acct_Description = Acct_Description.substring(0,1024);
}
set_Value ("Acct_Description", Acct_Description);
}
/** Get Account Description */
public String getAcct_Description() 
{
return (String)get_Value("Acct_Description");
}
/** Set Balance */
public void setBalance (BigDecimal Balance)
{
set_Value ("Balance", Balance);
}
/** Get Balance */
public BigDecimal getBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("Balance");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set C_Elementvalue_To_ID */
public void setC_Elementvalue_To_ID (int C_Elementvalue_To_ID)
{
if (C_Elementvalue_To_ID <= 0) set_Value ("C_Elementvalue_To_ID", null);
 else 
set_Value ("C_Elementvalue_To_ID", new Integer(C_Elementvalue_To_ID));
}
/** Get C_Elementvalue_To_ID */
public int getC_Elementvalue_To_ID() 
{
Integer ii = (Integer)get_Value("C_Elementvalue_To_ID");
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
/** Set Hierarchical Code */
public void setHierarchicalCode (String HierarchicalCode)
{
if (HierarchicalCode != null && HierarchicalCode.length() > 1024)
{
log.warning("Length > 1024 - truncated");
HierarchicalCode = HierarchicalCode.substring(0,1024);
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
/** Set Account General Balance */
public void setT_Acct_Balance_ID (int T_Acct_Balance_ID)
{
set_ValueNoCheck ("T_Acct_Balance_ID", new Integer(T_Acct_Balance_ID));
}
/** Get Account General Balance */
public int getT_Acct_Balance_ID() 
{
Integer ii = (Integer)get_Value("T_Acct_Balance_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
