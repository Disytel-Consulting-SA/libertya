/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por Test
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.296 */
public class X_Test extends PO
{
/** Constructor estándar */
public X_Test (Properties ctx, int Test_ID, String trxName)
{
super (ctx, Test_ID, trxName);
/** if (Test_ID == 0)
{
setName (null);
setTest_ID (0);
}
 */
}
/** Load Constructor */
public X_Test (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=135 */
public static final int Table_ID=135;

/** TableName=Test */
public static final String Table_Name="Test";

protected static KeyNamePair Model = new KeyNamePair(135,"Test");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_Test[").append(getID()).append("]");
return sb.toString();
}
/** Set Account_Acct */
public void setAccount_Acct (int Account_Acct)
{
set_Value ("Account_Acct", new Integer(Account_Acct));
}
/** Get Account_Acct */
public int getAccount_Acct() 
{
Integer ii = (Integer)get_Value("Account_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set BinaryData.
Binary Data */
public void setBinaryData (byte[] BinaryData)
{
set_Value ("BinaryData", BinaryData);
}
/** Get BinaryData.
Binary Data */
public byte[] getBinaryData() 
{
return (byte[])get_Value("BinaryData");
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
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
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
if (C_Currency_ID <= 0) set_Value ("C_Currency_ID", null);
 else 
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Address.
Location or Address */
public void setC_Location_ID (int C_Location_ID)
{
if (C_Location_ID <= 0) set_Value ("C_Location_ID", null);
 else 
set_Value ("C_Location_ID", new Integer(C_Location_ID));
}
/** Get Address.
Location or Address */
public int getC_Location_ID() 
{
Integer ii = (Integer)get_Value("C_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
if (C_Payment_ID <= 0) set_Value ("C_Payment_ID", null);
 else 
set_Value ("C_Payment_ID", new Integer(C_Payment_ID));
}
/** Get Payment.
Payment identifier */
public int getC_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Payment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
if (C_UOM_ID <= 0) set_Value ("C_UOM_ID", null);
 else 
set_Value ("C_UOM_ID", new Integer(C_UOM_ID));
}
/** Get UOM.
Unit of Measure */
public int getC_UOM_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Character Data.
Long Character Field */
public void setCharacterData (String CharacterData)
{
if (CharacterData != null && CharacterData.length() > 4000)
{
log.warning("Length > 4000 - truncated");
CharacterData = CharacterData.substring(0,3999);
}
set_Value ("CharacterData", CharacterData);
}
/** Get Character Data.
Long Character Field */
public String getCharacterData() 
{
return (String)get_Value("CharacterData");
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Locator.
Warehouse Locator */
public void setM_Locator_ID (int M_Locator_ID)
{
if (M_Locator_ID <= 0) set_Value ("M_Locator_ID", null);
 else 
set_Value ("M_Locator_ID", new Integer(M_Locator_ID));
}
/** Get Locator.
Warehouse Locator */
public int getM_Locator_ID() 
{
Integer ii = (Integer)get_Value("M_Locator_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
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
/** Set Amount */
public void setT_Amount (BigDecimal T_Amount)
{
set_Value ("T_Amount", T_Amount);
}
/** Get Amount */
public BigDecimal getT_Amount() 
{
BigDecimal bd = (BigDecimal)get_Value("T_Amount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Date */
public void setT_Date (Timestamp T_Date)
{
set_Value ("T_Date", T_Date);
}
/** Get Date */
public Timestamp getT_Date() 
{
return (Timestamp)get_Value("T_Date");
}
/** Set DateTime */
public void setT_DateTime (Timestamp T_DateTime)
{
set_Value ("T_DateTime", T_DateTime);
}
/** Get DateTime */
public Timestamp getT_DateTime() 
{
return (Timestamp)get_Value("T_DateTime");
}
/** Set Integer */
public void setT_Integer (int T_Integer)
{
set_Value ("T_Integer", new Integer(T_Integer));
}
/** Get Integer */
public int getT_Integer() 
{
Integer ii = (Integer)get_Value("T_Integer");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Number */
public void setT_Number (BigDecimal T_Number)
{
set_Value ("T_Number", T_Number);
}
/** Get Number */
public BigDecimal getT_Number() 
{
BigDecimal bd = (BigDecimal)get_Value("T_Number");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Qty */
public void setT_Qty (BigDecimal T_Qty)
{
set_Value ("T_Qty", T_Qty);
}
/** Get Qty */
public BigDecimal getT_Qty() 
{
BigDecimal bd = (BigDecimal)get_Value("T_Qty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Test ID */
public void setTest_ID (int Test_ID)
{
set_ValueNoCheck ("Test_ID", new Integer(Test_ID));
}
/** Get Test ID */
public int getTest_ID() 
{
Integer ii = (Integer)get_Value("Test_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
