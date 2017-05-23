/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BankAccountDocUser
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-05-23 11:16:36.873 */
public class X_C_BankAccountDocUser extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BankAccountDocUser (Properties ctx, int C_BankAccountDocUser_ID, String trxName)
{
super (ctx, C_BankAccountDocUser_ID, trxName);
/** if (C_BankAccountDocUser_ID == 0)
{
setAD_User_ID (0);
setC_BankAccountDoc_ID (0);
setC_BankAccountDocUser_ID (0);
}
 */
}
/** Load Constructor */
public X_C_BankAccountDocUser (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BankAccountDocUser");

/** TableName=C_BankAccountDocUser */
public static final String Table_Name="C_BankAccountDocUser";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BankAccountDocUser");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BankAccountDocUser[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
set_Value ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Account Document.
Checks, Transfers, etc. */
public void setC_BankAccountDoc_ID (int C_BankAccountDoc_ID)
{
set_Value ("C_BankAccountDoc_ID", new Integer(C_BankAccountDoc_ID));
}
/** Get Bank Account Document.
Checks, Transfers, etc. */
public int getC_BankAccountDoc_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccountDoc_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Account Doc User */
public void setC_BankAccountDocUser_ID (int C_BankAccountDocUser_ID)
{
set_ValueNoCheck ("C_BankAccountDocUser_ID", new Integer(C_BankAccountDocUser_ID));
}
/** Get Bank Account Doc User */
public int getC_BankAccountDocUser_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccountDocUser_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
