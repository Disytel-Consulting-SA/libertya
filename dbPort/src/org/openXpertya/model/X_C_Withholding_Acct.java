/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Withholding_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.734 */
public class X_C_Withholding_Acct extends PO
{
/** Constructor estándar */
public X_C_Withholding_Acct (Properties ctx, int C_Withholding_Acct_ID, String trxName)
{
super (ctx, C_Withholding_Acct_ID, trxName);
/** if (C_Withholding_Acct_ID == 0)
{
setC_AcctSchema_ID (0);
setC_Withholding_ID (0);
setWithholding_Acct (0);
}
 */
}
/** Load Constructor */
public X_C_Withholding_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=400 */
public static final int Table_ID=400;

/** TableName=C_Withholding_Acct */
public static final String Table_Name="C_Withholding_Acct";

protected static KeyNamePair Model = new KeyNamePair(400,"C_Withholding_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Withholding_Acct[").append(getID()).append("]");
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
/** Set Withholding.
Withholding type defined */
public void setC_Withholding_ID (int C_Withholding_ID)
{
set_ValueNoCheck ("C_Withholding_ID", new Integer(C_Withholding_ID));
}
/** Get Withholding.
Withholding type defined */
public int getC_Withholding_ID() 
{
Integer ii = (Integer)get_Value("C_Withholding_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Withholding.
Account for Withholdings */
public void setWithholding_Acct (int Withholding_Acct)
{
set_Value ("Withholding_Acct", new Integer(Withholding_Acct));
}
/** Get Withholding.
Account for Withholdings */
public int getWithholding_Acct() 
{
Integer ii = (Integer)get_Value("Withholding_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
