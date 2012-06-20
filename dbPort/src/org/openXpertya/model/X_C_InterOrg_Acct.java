/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_InterOrg_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.937 */
public class X_C_InterOrg_Acct extends PO
{
/** Constructor estándar */
public X_C_InterOrg_Acct (Properties ctx, int C_InterOrg_Acct_ID, String trxName)
{
super (ctx, C_InterOrg_Acct_ID, trxName);
/** if (C_InterOrg_Acct_ID == 0)
{
setAD_OrgTo_ID (0);
setC_AcctSchema_ID (0);
setIntercompanyDueFrom_Acct (0);
setIntercompanyDueTo_Acct (0);
}
 */
}
/** Load Constructor */
public X_C_InterOrg_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=397 */
public static final int Table_ID=397;

/** TableName=C_InterOrg_Acct */
public static final String Table_Name="C_InterOrg_Acct";

protected static KeyNamePair Model = new KeyNamePair(397,"C_InterOrg_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_InterOrg_Acct[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_ORGTO_ID_AD_Reference_ID=130;
/** Set Inter-Organization.
Organization valid for intercompany documents */
public void setAD_OrgTo_ID (int AD_OrgTo_ID)
{
set_ValueNoCheck ("AD_OrgTo_ID", new Integer(AD_OrgTo_ID));
}
/** Get Inter-Organization.
Organization valid for intercompany documents */
public int getAD_OrgTo_ID() 
{
Integer ii = (Integer)get_Value("AD_OrgTo_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Accounting Schema.
Rules for accounting */
public void setC_AcctSchema_ID (int C_AcctSchema_ID)
{
set_ValueNoCheck ("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
}
/** Get Accounting Schema.
Rules for accounting */
public int getC_AcctSchema_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Intercompany Due From Acct.
Intercompany Due From / Receivables Account */
public void setIntercompanyDueFrom_Acct (int IntercompanyDueFrom_Acct)
{
set_Value ("IntercompanyDueFrom_Acct", new Integer(IntercompanyDueFrom_Acct));
}
/** Get Intercompany Due From Acct.
Intercompany Due From / Receivables Account */
public int getIntercompanyDueFrom_Acct() 
{
Integer ii = (Integer)get_Value("IntercompanyDueFrom_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Intercompany Due To Acct.
Intercompany Due To / Payable Account */
public void setIntercompanyDueTo_Acct (int IntercompanyDueTo_Acct)
{
set_Value ("IntercompanyDueTo_Acct", new Integer(IntercompanyDueTo_Acct));
}
/** Get Intercompany Due To Acct.
Intercompany Due To / Payable Account */
public int getIntercompanyDueTo_Acct() 
{
Integer ii = (Integer)get_Value("IntercompanyDueTo_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
