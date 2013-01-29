/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Role_Invalid_Action
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:24.765 */
public class X_AD_Role_Invalid_Action extends PO
{
/** Constructor estándar */
public X_AD_Role_Invalid_Action (Properties ctx, int AD_Role_Invalid_Action_ID, String trxName)
{
super (ctx, AD_Role_Invalid_Action_ID, trxName);
/** if (AD_Role_Invalid_Action_ID == 0)
{
setAD_Role_invalid_action_ID (0);
setAD_Table_ID (0);
setalldocumenttypes (true);	// Y
setinv_docaction (null);
}
 */
}
/** Load Constructor */
public X_AD_Role_Invalid_Action (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000135 */
public static final int Table_ID=1000135;

/** TableName=AD_Role_Invalid_Action */
public static final String Table_Name="AD_Role_Invalid_Action";

protected static KeyNamePair Model = new KeyNamePair(1000135,"AD_Role_Invalid_Action");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Role_Invalid_Action[").append(getID()).append("]");
return sb.toString();
}
/** Set Role.
Responsibility Role */
public void setAD_Role_ID (int AD_Role_ID)
{
if (AD_Role_ID <= 0) set_Value ("AD_Role_ID", null);
 else 
set_Value ("AD_Role_ID", new Integer(AD_Role_ID));
}
/** Get Role.
Responsibility Role */
public int getAD_Role_ID() 
{
Integer ii = (Integer)get_Value("AD_Role_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_Role_invalid_action_ID */
public void setAD_Role_invalid_action_ID (int AD_Role_invalid_action_ID)
{
set_ValueNoCheck ("AD_Role_invalid_action_ID", new Integer(AD_Role_invalid_action_ID));
}
/** Get AD_Role_invalid_action_ID */
public int getAD_Role_invalid_action_ID() 
{
Integer ii = (Integer)get_Value("AD_Role_invalid_action_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
set_Value ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPE_ID_AD_Reference_ID=170;
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
if (C_DocType_ID <= 0) set_Value ("C_DocType_ID", null);
 else 
set_Value ("C_DocType_ID", new Integer(C_DocType_ID));
}
/** Get Document Type.
Document type or rules */
public int getC_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set alldocumenttypes */
public void setalldocumenttypes (boolean alldocumenttypes)
{
set_Value ("alldocumenttypes", new Boolean(alldocumenttypes));
}
/** Get alldocumenttypes */
public boolean isalldocumenttypes() 
{
Object oo = get_Value("alldocumenttypes");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set allroles */
public void setallroles (boolean allroles)
{
set_Value ("allroles", new Boolean(allroles));
}
/** Get allroles */
public boolean isallroles() 
{
Object oo = get_Value("allroles");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int INV_DOCACTION_AD_Reference_ID=135;
/** Approve = AP */
public static final String INV_DOCACTION_Approve = "AP";
/** Close = CL */
public static final String INV_DOCACTION_Close = "CL";
/** Prepare = PR */
public static final String INV_DOCACTION_Prepare = "PR";
/** Invalidate = IN */
public static final String INV_DOCACTION_Invalidate = "IN";
/** Complete = CO */
public static final String INV_DOCACTION_Complete = "CO";
/** <None> = -- */
public static final String INV_DOCACTION_None = "--";
/** Reverse - Correct = RC */
public static final String INV_DOCACTION_Reverse_Correct = "RC";
/** Reject = RJ */
public static final String INV_DOCACTION_Reject = "RJ";
/** Reverse - Accrual = RA */
public static final String INV_DOCACTION_Reverse_Accrual = "RA";
/** Wait Complete = WC */
public static final String INV_DOCACTION_WaitComplete = "WC";
/** Unlock = XL */
public static final String INV_DOCACTION_Unlock = "XL";
/** Re-activate = RE */
public static final String INV_DOCACTION_Re_Activate = "RE";
/** Post = PO */
public static final String INV_DOCACTION_Post = "PO";
/** Void = VO */
public static final String INV_DOCACTION_Void = "VO";
/** Set inv_docaction */
public void setinv_docaction (String inv_docaction)
{
if (inv_docaction.equals("AP") || inv_docaction.equals("CL") || inv_docaction.equals("PR") || inv_docaction.equals("IN") || inv_docaction.equals("CO") || inv_docaction.equals("--") || inv_docaction.equals("RC") || inv_docaction.equals("RJ") || inv_docaction.equals("RA") || inv_docaction.equals("WC") || inv_docaction.equals("XL") || inv_docaction.equals("RE") || inv_docaction.equals("PO") || inv_docaction.equals("VO"));
 else throw new IllegalArgumentException ("inv_docaction Invalid value - Reference_ID=135 - AP - CL - PR - IN - CO - -- - RC - RJ - RA - WC - XL - RE - PO - VO");
if (inv_docaction == null) throw new IllegalArgumentException ("inv_docaction is mandatory");
if (inv_docaction.length() > 2)
{
log.warning("Length > 2 - truncated");
inv_docaction = inv_docaction.substring(0,1);
}
set_Value ("inv_docaction", inv_docaction);
}
/** Get inv_docaction */
public String getinv_docaction() 
{
return (String)get_Value("inv_docaction");
}
}
