/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RetencionAcct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.734 */
public class X_C_RetencionAcct extends PO
{
/** Constructor estándar */
public X_C_RetencionAcct (Properties ctx, int C_RetencionAcct_ID, String trxName)
{
super (ctx, C_RetencionAcct_ID, trxName);
/** if (C_RetencionAcct_ID == 0)
{
setC_AcctSchema_ID (0);
setC_RetencionAcct_ID (0);
}
 */
}
/** Load Constructor */
public X_C_RetencionAcct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000139 */
public static final int Table_ID=1000139;

/** TableName=C_RetencionAcct */
public static final String Table_Name="C_RetencionAcct";

protected static KeyNamePair Model = new KeyNamePair(1000139,"C_RetencionAcct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RetencionAcct[").append(getID()).append("]");
return sb.toString();
}
/** Set Accounting Schema.
Rules for accounting */
public void setC_AcctSchema_ID (int C_AcctSchema_ID)
{
set_Value ("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
}
/** Get Accounting Schema.
Rules for accounting */
public int getC_AcctSchema_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Account */
public void setC_RetencionAcct_ID (int C_RetencionAcct_ID)
{
set_ValueNoCheck ("C_RetencionAcct_ID", new Integer(C_RetencionAcct_ID));
}
/** Get Retencion Account */
public int getC_RetencionAcct_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionAcct_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Type */
public void setC_RetencionType_ID (int C_RetencionType_ID)
{
if (C_RetencionType_ID <= 0) set_Value ("C_RetencionType_ID", null);
 else 
set_Value ("C_RetencionType_ID", new Integer(C_RetencionType_ID));
}
/** Get Retencion Type */
public int getC_RetencionType_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Account */
public void setb_retencion_acct (int b_retencion_acct)
{
set_Value ("b_retencion_acct", new Integer(b_retencion_acct));
}
/** Get Retencion Account */
public int getb_retencion_acct() 
{
Integer ii = (Integer)get_Value("b_retencion_acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Due Account */
public void setb_retencion_due_acct (int b_retencion_due_acct)
{
set_Value ("b_retencion_due_acct", new Integer(b_retencion_due_acct));
}
/** Get Retencion Due Account */
public int getb_retencion_due_acct() 
{
Integer ii = (Integer)get_Value("b_retencion_due_acct");
if (ii == null) return 0;
return ii.intValue();
}
}
