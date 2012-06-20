/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BP_Vendor_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.875 */
public class X_C_BP_Vendor_Acct extends PO
{
/** Constructor estándar */
public X_C_BP_Vendor_Acct (Properties ctx, int C_BP_Vendor_Acct_ID, String trxName)
{
super (ctx, C_BP_Vendor_Acct_ID, trxName);
/** if (C_BP_Vendor_Acct_ID == 0)
{
setC_AcctSchema_ID (0);
setC_BPartner_ID (0);
setV_Liability_Acct (0);
setV_Prepayment_Acct (0);
}
 */
}
/** Load Constructor */
public X_C_BP_Vendor_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=185 */
public static final int Table_ID=185;

/** TableName=C_BP_Vendor_Acct */
public static final String Table_Name="C_BP_Vendor_Acct";

protected static KeyNamePair Model = new KeyNamePair(185,"C_BP_Vendor_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BP_Vendor_Acct[").append(getID()).append("]");
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
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_ValueNoCheck ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Vendor Liability.
Account for Vendor Liability */
public void setV_Liability_Acct (int V_Liability_Acct)
{
set_Value ("V_Liability_Acct", new Integer(V_Liability_Acct));
}
/** Get Vendor Liability.
Account for Vendor Liability */
public int getV_Liability_Acct() 
{
Integer ii = (Integer)get_Value("V_Liability_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Vendor Service Liability.
Account for Vender Service Liability */
public void setV_Liability_Services_Acct (int V_Liability_Services_Acct)
{
set_Value ("V_Liability_Services_Acct", new Integer(V_Liability_Services_Acct));
}
/** Get Vendor Service Liability.
Account for Vender Service Liability */
public int getV_Liability_Services_Acct() 
{
Integer ii = (Integer)get_Value("V_Liability_Services_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Vendor Prepayment.
Account for Vendor Prepayments */
public void setV_Prepayment_Acct (int V_Prepayment_Acct)
{
set_Value ("V_Prepayment_Acct", new Integer(V_Prepayment_Acct));
}
/** Get Vendor Prepayment.
Account for Vendor Prepayments */
public int getV_Prepayment_Acct() 
{
Integer ii = (Integer)get_Value("V_Prepayment_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
