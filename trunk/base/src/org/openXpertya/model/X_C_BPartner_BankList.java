/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_BankList
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-24 20:12:03.849 */
public class X_C_BPartner_BankList extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BPartner_BankList (Properties ctx, int C_BPartner_BankList_ID, String trxName)
{
super (ctx, C_BPartner_BankList_ID, trxName);
/** if (C_BPartner_BankList_ID == 0)
{
setC_BPartner_BankList_ID (0);
setC_BPartner_ID (0);
setC_DocType_ID (0);
setC_ElectronicPaymentBranch_ID (0);
}
 */
}
/** Load Constructor */
public X_C_BPartner_BankList (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BPartner_BankList");

/** TableName=C_BPartner_BankList */
public static final String Table_Name="C_BPartner_BankList";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BPartner_BankList");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_BankList[").append(getID()).append("]");
return sb.toString();
}
/** Set Bank Account.
Account at the Bank */
public void setC_BankAccount_ID (int C_BankAccount_ID)
{
if (C_BankAccount_ID <= 0) set_Value ("C_BankAccount_ID", null);
 else 
set_Value ("C_BankAccount_ID", new Integer(C_BankAccount_ID));
}
/** Get Bank Account.
Account at the Bank */
public int getC_BankAccount_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccount_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_BPartner_BankList_ID */
public void setC_BPartner_BankList_ID (int C_BPartner_BankList_ID)
{
set_ValueNoCheck ("C_BPartner_BankList_ID", new Integer(C_BPartner_BankList_ID));
}
/** Get C_BPartner_BankList_ID */
public int getC_BPartner_BankList_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_BankList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
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
public static final int C_ELECTRONICPAYMENTBRANCH_ID_AD_Reference_ID = MReference.getReferenceID("C_ElectronicPaymentBranch");
/** Set C_ElectronicPaymentBranch_ID */
public void setC_ElectronicPaymentBranch_ID (int C_ElectronicPaymentBranch_ID)
{
set_Value ("C_ElectronicPaymentBranch_ID", new Integer(C_ElectronicPaymentBranch_ID));
}
/** Get C_ElectronicPaymentBranch_ID */
public int getC_ElectronicPaymentBranch_ID() 
{
Integer ii = (Integer)get_Value("C_ElectronicPaymentBranch_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Nombre Retirante */
public void setNombre_Retirante (String Nombre_Retirante)
{
if (Nombre_Retirante != null && Nombre_Retirante.length() > 30)
{
log.warning("Length > 30 - truncated");
Nombre_Retirante = Nombre_Retirante.substring(0,30);
}
set_Value ("Nombre_Retirante", Nombre_Retirante);
}
/** Get Nombre Retirante */
public String getNombre_Retirante() 
{
return (String)get_Value("Nombre_Retirante");
}
}
