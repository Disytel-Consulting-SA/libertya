/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_ElementValue
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:34.609 */
public class X_I_ElementValue extends PO
{
/** Constructor estándar */
public X_I_ElementValue (Properties ctx, int I_ElementValue_ID, String trxName)
{
super (ctx, I_ElementValue_ID, trxName);
/** if (I_ElementValue_ID == 0)
{
setI_ElementValue_ID (0);
setI_IsImported (false);
}
 */
}
/** Load Constructor */
public X_I_ElementValue (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=534 */
public static final int Table_ID=534;

/** TableName=I_ElementValue */
public static final String Table_Name="I_ElementValue";

protected static KeyNamePair Model = new KeyNamePair(534,"I_ElementValue");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_ElementValue[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_COLUMN_ID_AD_Reference_ID=272;
/** Set Column.
Column in the table */
public void setAD_Column_ID (int AD_Column_ID)
{
if (AD_Column_ID <= 0) set_Value ("AD_Column_ID", null);
 else 
set_Value ("AD_Column_ID", new Integer(AD_Column_ID));
}
/** Get Column.
Column in the table */
public int getAD_Column_ID() 
{
Integer ii = (Integer)get_Value("AD_Column_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ACCOUNTSIGN_AD_Reference_ID=118;
/** Natural = N */
public static final String ACCOUNTSIGN_Natural = "N";
/** Debit = D */
public static final String ACCOUNTSIGN_Debit = "D";
/** Credit = C */
public static final String ACCOUNTSIGN_Credit = "C";
/** Set Account Sign.
Indicates the Natural Sign of the Account as a Debit or Credit */
public void setAccountSign (String AccountSign)
{
if (AccountSign == null || AccountSign.equals("N") || AccountSign.equals("D") || AccountSign.equals("C"));
 else throw new IllegalArgumentException ("AccountSign Invalid value - Reference_ID=118 - N - D - C");
if (AccountSign != null && AccountSign.length() > 1)
{
log.warning("Length > 1 - truncated");
AccountSign = AccountSign.substring(0,0);
}
set_Value ("AccountSign", AccountSign);
}
/** Get Account Sign.
Indicates the Natural Sign of the Account as a Debit or Credit */
public String getAccountSign() 
{
return (String)get_Value("AccountSign");
}
public static final int ACCOUNTTYPE_AD_Reference_ID=117;
/** Asset = A */
public static final String ACCOUNTTYPE_Asset = "A";
/** Liability = L */
public static final String ACCOUNTTYPE_Liability = "L";
/** Revenue = R */
public static final String ACCOUNTTYPE_Revenue = "R";
/** Expense = E */
public static final String ACCOUNTTYPE_Expense = "E";
/** Owner's Equity = O */
public static final String ACCOUNTTYPE_OwnerSEquity = "O";
/** Memo = M */
public static final String ACCOUNTTYPE_Memo = "M";
/** Set Account Type.
Indicates the type of account */
public void setAccountType (String AccountType)
{
if (AccountType == null || AccountType.equals("A") || AccountType.equals("L") || AccountType.equals("R") || AccountType.equals("E") || AccountType.equals("O") || AccountType.equals("M"));
 else throw new IllegalArgumentException ("AccountType Invalid value - Reference_ID=117 - A - L - R - E - O - M");
if (AccountType != null && AccountType.length() > 1)
{
log.warning("Length > 1 - truncated");
AccountType = AccountType.substring(0,0);
}
set_Value ("AccountType", AccountType);
}
/** Get Account Type.
Indicates the type of account */
public String getAccountType() 
{
return (String)get_Value("AccountType");
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
/** Set Element.
Accounting Element */
public void setC_Element_ID (int C_Element_ID)
{
if (C_Element_ID <= 0) set_Value ("C_Element_ID", null);
 else 
set_Value ("C_Element_ID", new Integer(C_Element_ID));
}
/** Get Element.
Accounting Element */
public int getC_Element_ID() 
{
Integer ii = (Integer)get_Value("C_Element_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Default Account.
Name of the Default Account Column */
public void setDefault_Account (String Default_Account)
{
if (Default_Account != null && Default_Account.length() > 30)
{
log.warning("Length > 30 - truncated");
Default_Account = Default_Account.substring(0,29);
}
set_Value ("Default_Account", Default_Account);
}
/** Get Default Account.
Name of the Default Account Column */
public String getDefault_Account() 
{
return (String)get_Value("Default_Account");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Element Name.
Name of the Element */
public void setElementName (String ElementName)
{
if (ElementName != null && ElementName.length() > 60)
{
log.warning("Length > 60 - truncated");
ElementName = ElementName.substring(0,59);
}
set_Value ("ElementName", ElementName);
}
/** Get Element Name.
Name of the Element */
public String getElementName() 
{
return (String)get_Value("ElementName");
}
/** Set Import Account.
Import Account Value */
public void setI_ElementValue_ID (int I_ElementValue_ID)
{
set_ValueNoCheck ("I_ElementValue_ID", new Integer(I_ElementValue_ID));
}
/** Get Import Account.
Import Account Value */
public int getI_ElementValue_ID() 
{
Integer ii = (Integer)get_Value("I_ElementValue_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Import Error Message.
Messages generated from import process */
public void setI_ErrorMsg (String I_ErrorMsg)
{
if (I_ErrorMsg != null && I_ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
I_ErrorMsg = I_ErrorMsg.substring(0,1999);
}
set_Value ("I_ErrorMsg", I_ErrorMsg);
}
/** Get Import Error Message.
Messages generated from import process */
public String getI_ErrorMsg() 
{
return (String)get_Value("I_ErrorMsg");
}
/** Set Imported.
Has this import been processed */
public void setI_IsImported (boolean I_IsImported)
{
set_Value ("I_IsImported", new Boolean(I_IsImported));
}
/** Get Imported.
Has this import been processed */
public boolean isI_IsImported() 
{
Object oo = get_Value("I_IsImported");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Document Controlled.
Control account - If an account is controlled by a document, you cannot post manually to it */
public void setIsDocControlled (boolean IsDocControlled)
{
set_Value ("IsDocControlled", new Boolean(IsDocControlled));
}
/** Get Document Controlled.
Control account - If an account is controlled by a document, you cannot post manually to it */
public boolean isDocControlled() 
{
Object oo = get_Value("IsDocControlled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Summary Level.
This is a summary entity */
public void setIsSummary (boolean IsSummary)
{
set_Value ("IsSummary", new Boolean(IsSummary));
}
/** Get Summary Level.
This is a summary entity */
public boolean isSummary() 
{
Object oo = get_Value("IsSummary");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public static final int PARENTELEMENTVALUE_ID_AD_Reference_ID=273;
/** Set Parent Account.
The parent (summary) account */
public void setParentElementValue_ID (int ParentElementValue_ID)
{
if (ParentElementValue_ID <= 0) set_Value ("ParentElementValue_ID", null);
 else 
set_Value ("ParentElementValue_ID", new Integer(ParentElementValue_ID));
}
/** Get Parent Account.
The parent (summary) account */
public int getParentElementValue_ID() 
{
Integer ii = (Integer)get_Value("ParentElementValue_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Parent Key.
Key if the Parent */
public void setParentValue (String ParentValue)
{
if (ParentValue != null && ParentValue.length() > 40)
{
log.warning("Length > 40 - truncated");
ParentValue = ParentValue.substring(0,39);
}
set_Value ("ParentValue", ParentValue);
}
/** Get Parent Key.
Key if the Parent */
public String getParentValue() 
{
return (String)get_Value("ParentValue");
}
/** Set Post Actual.
Actual Values can be posted */
public void setPostActual (boolean PostActual)
{
set_Value ("PostActual", new Boolean(PostActual));
}
/** Get Post Actual.
Actual Values can be posted */
public boolean isPostActual() 
{
Object oo = get_Value("PostActual");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Post Budget.
Budget values can be posted */
public void setPostBudget (boolean PostBudget)
{
set_Value ("PostBudget", new Boolean(PostBudget));
}
/** Get Post Budget.
Budget values can be posted */
public boolean isPostBudget() 
{
Object oo = get_Value("PostBudget");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Post Encumbrance.
Post commitments to this account */
public void setPostEncumbrance (boolean PostEncumbrance)
{
set_Value ("PostEncumbrance", new Boolean(PostEncumbrance));
}
/** Get Post Encumbrance.
Post commitments to this account */
public boolean isPostEncumbrance() 
{
Object oo = get_Value("PostEncumbrance");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Post Statistical.
Post statistical quantities to this account? */
public void setPostStatistical (boolean PostStatistical)
{
set_Value ("PostStatistical", new Boolean(PostStatistical));
}
/** Get Post Statistical.
Post statistical quantities to this account? */
public boolean isPostStatistical() 
{
Object oo = get_Value("PostStatistical");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value != null && Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,39);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getValue());
}
}
