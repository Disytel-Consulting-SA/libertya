/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Project_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.203 */
public class X_C_Project_Acct extends PO
{
/** Constructor estándar */
public X_C_Project_Acct (Properties ctx, int C_Project_Acct_ID, String trxName)
{
super (ctx, C_Project_Acct_ID, trxName);
/** if (C_Project_Acct_ID == 0)
{
setC_AcctSchema_ID (0);
setC_Project_ID (0);
setPJ_Asset_Acct (0);
setPJ_WIP_Acct (0);
}
 */
}
/** Load Constructor */
public X_C_Project_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=204 */
public static final int Table_ID=204;

/** TableName=C_Project_Acct */
public static final String Table_Name="C_Project_Acct";

protected static KeyNamePair Model = new KeyNamePair(204,"C_Project_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Project_Acct[").append(getID()).append("]");
return sb.toString();
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
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
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
/** Set Project Asset.
Project Asset Account */
public void setPJ_Asset_Acct (int PJ_Asset_Acct)
{
set_Value ("PJ_Asset_Acct", new Integer(PJ_Asset_Acct));
}
/** Get Project Asset.
Project Asset Account */
public int getPJ_Asset_Acct() 
{
Integer ii = (Integer)get_Value("PJ_Asset_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Work In Progress.
Account for Work in Progress */
public void setPJ_WIP_Acct (int PJ_WIP_Acct)
{
set_Value ("PJ_WIP_Acct", new Integer(PJ_WIP_Acct));
}
/** Get Work In Progress.
Account for Work in Progress */
public int getPJ_WIP_Acct() 
{
Integer ii = (Integer)get_Value("PJ_WIP_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
