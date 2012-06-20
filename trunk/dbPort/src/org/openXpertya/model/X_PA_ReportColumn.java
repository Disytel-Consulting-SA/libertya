/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_ReportColumn
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:37.758 */
public class X_PA_ReportColumn extends PO
{
/** Constructor estándar */
public X_PA_ReportColumn (Properties ctx, int PA_ReportColumn_ID, String trxName)
{
super (ctx, PA_ReportColumn_ID, trxName);
/** if (PA_ReportColumn_ID == 0)
{
setColumnType (null);	// R
setIsPrinted (true);	// Y
setName (null);
setNegativeAsZero (false);
setPA_ReportColumn_ID (0);
setPA_ReportColumnSet_ID (0);
setPostingType (null);	// A
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM PA_ReportColumn WHERE PA_ReportColumnSet_ID=@PA_ReportColumnSet_ID@
}
 */
}
/** Load Constructor */
public X_PA_ReportColumn (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=446 */
public static final int Table_ID=446;

/** TableName=PA_ReportColumn */
public static final String Table_Name="PA_ReportColumn";

protected static KeyNamePair Model = new KeyNamePair(446,"PA_ReportColumn");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_ReportColumn[").append(getID()).append("]");
return sb.toString();
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
AccountType = AccountType.substring(0,1);
}
set_Value ("AccountType", AccountType);
}
/** Get Account Type.
Indicates the type of account */
public String getAccountType() 
{
return (String)get_Value("AccountType");
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
public static final int AMOUNTTYPE_AD_Reference_ID=235;
/** Total Debit Only = DT */
public static final String AMOUNTTYPE_TotalDebitOnly = "DT";
/** Total Credit Only = CT */
public static final String AMOUNTTYPE_TotalCreditOnly = "CT";
/** Total Balance = BT */
public static final String AMOUNTTYPE_TotalBalance = "BT";
/** Total Quantity = QT */
public static final String AMOUNTTYPE_TotalQuantity = "QT";
/** Year Balance = BY */
public static final String AMOUNTTYPE_YearBalance = "BY";
/** Year Credit Only = CY */
public static final String AMOUNTTYPE_YearCreditOnly = "CY";
/** Year Debit Only = DY */
public static final String AMOUNTTYPE_YearDebitOnly = "DY";
/** Year Quantity = QY */
public static final String AMOUNTTYPE_YearQuantity = "QY";
/** Period Quantity = QP */
public static final String AMOUNTTYPE_PeriodQuantity = "QP";
/** Period Balance = BP */
public static final String AMOUNTTYPE_PeriodBalance = "BP";
/** Period Credit Only = CP */
public static final String AMOUNTTYPE_PeriodCreditOnly = "CP";
/** Period Debit Only = DP */
public static final String AMOUNTTYPE_PeriodDebitOnly = "DP";
/** Set Amount Type.
Type of amount to report */
public void setAmountType (String AmountType)
{
if (AmountType == null || AmountType.equals("DT") || AmountType.equals("CT") || AmountType.equals("BT") || AmountType.equals("QT") || AmountType.equals("BY") || AmountType.equals("CY") || AmountType.equals("DY") || AmountType.equals("QY") || AmountType.equals("QP") || AmountType.equals("BP") || AmountType.equals("CP") || AmountType.equals("DP"));
 else throw new IllegalArgumentException ("AmountType Invalid value - Reference_ID=235 - DT - CT - BT - QT - BY - CY - DY - QY - QP - BP - CP - DP");
if (AmountType != null && AmountType.length() > 2)
{
log.warning("Length > 2 - truncated");
AmountType = AmountType.substring(0,2);
}
set_Value ("AmountType", AmountType);
}
/** Get Amount Type.
Type of amount to report */
public String getAmountType() 
{
return (String)get_Value("AmountType");
}
/** Set Activity.
Business Activity */
public void setC_Activity_ID (int C_Activity_ID)
{
if (C_Activity_ID <= 0) set_Value ("C_Activity_ID", null);
 else 
set_Value ("C_Activity_ID", new Integer(C_Activity_ID));
}
/** Get Activity.
Business Activity */
public int getC_Activity_ID() 
{
Integer ii = (Integer)get_Value("C_Activity_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CALCULATIONTYPE_AD_Reference_ID=236;
/** Add (Op1+Op2) = A */
public static final String CALCULATIONTYPE_AddOp1PlusOp2 = "A";
/** Subtract (Op1-Op2) = S */
public static final String CALCULATIONTYPE_SubtractOp1_Op2 = "S";
/** Percentage (Op1 of Op2) = P */
public static final String CALCULATIONTYPE_PercentageOp1OfOp2 = "P";
/** Add Range (Op1 to Op2) = R */
public static final String CALCULATIONTYPE_AddRangeOp1ToOp2 = "R";
/** Set Calculation */
public void setCalculationType (String CalculationType)
{
if (CalculationType == null || CalculationType.equals("A") || CalculationType.equals("S") || CalculationType.equals("P") || CalculationType.equals("R"));
 else throw new IllegalArgumentException ("CalculationType Invalid value - Reference_ID=236 - A - S - P - R");
if (CalculationType != null && CalculationType.length() > 1)
{
log.warning("Length > 1 - truncated");
CalculationType = CalculationType.substring(0,1);
}
set_Value ("CalculationType", CalculationType);
}
/** Get Calculation */
public String getCalculationType() 
{
return (String)get_Value("CalculationType");
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
/** Set Campaign.
Marketing Campaign */
public void setC_Campaign_ID (int C_Campaign_ID)
{
if (C_Campaign_ID <= 0) set_Value ("C_Campaign_ID", null);
 else 
set_Value ("C_Campaign_ID", new Integer(C_Campaign_ID));
}
/** Get Campaign.
Marketing Campaign */
public int getC_Campaign_ID() 
{
Integer ii = (Integer)get_Value("C_Campaign_ID");
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
public static final int COLUMNTYPE_AD_Reference_ID=237;
/** Calculation = C */
public static final String COLUMNTYPE_Calculation = "C";
/** Segment Value = S */
public static final String COLUMNTYPE_SegmentValue = "S";
/** Relative Period = R */
public static final String COLUMNTYPE_RelativePeriod = "R";
/** Set Column Type */
public void setColumnType (String ColumnType)
{
if (ColumnType.equals("C") || ColumnType.equals("S") || ColumnType.equals("R"));
 else throw new IllegalArgumentException ("ColumnType Invalid value - Reference_ID=237 - C - S - R");
if (ColumnType == null) throw new IllegalArgumentException ("ColumnType is mandatory");
if (ColumnType.length() > 1)
{
log.warning("Length > 1 - truncated");
ColumnType = ColumnType.substring(0,1);
}
set_Value ("ColumnType", ColumnType);
}
/** Get Column Type */
public String getColumnType() 
{
return (String)get_Value("ColumnType");
}
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
if (C_Project_ID <= 0) set_Value ("C_Project_ID", null);
 else 
set_Value ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sales Region.
Sales coverage region */
public void setC_SalesRegion_ID (int C_SalesRegion_ID)
{
if (C_SalesRegion_ID <= 0) set_Value ("C_SalesRegion_ID", null);
 else 
set_Value ("C_SalesRegion_ID", new Integer(C_SalesRegion_ID));
}
/** Get Sales Region.
Sales coverage region */
public int getC_SalesRegion_ID() 
{
Integer ii = (Integer)get_Value("C_SalesRegion_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CURRENCYTYPE_AD_Reference_ID=238;
/** Source Currency = S */
public static final String CURRENCYTYPE_SourceCurrency = "S";
/** Accounting Currency = A */
public static final String CURRENCYTYPE_AccountingCurrency = "A";
/** Set Currency Type */
public void setCurrencyType (String CurrencyType)
{
if (CurrencyType == null || CurrencyType.equals("S") || CurrencyType.equals("A"));
 else throw new IllegalArgumentException ("CurrencyType Invalid value - Reference_ID=238 - S - A");
if (CurrencyType != null && CurrencyType.length() > 1)
{
log.warning("Length > 1 - truncated");
CurrencyType = CurrencyType.substring(0,1);
}
set_Value ("CurrencyType", CurrencyType);
}
/** Get Currency Type */
public String getCurrencyType() 
{
return (String)get_Value("CurrencyType");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
public static final int ELEMENTTYPE_AD_Reference_ID=181;
/** Activity = AY */
public static final String ELEMENTTYPE_Activity = "AY";
/** Org = OO */
public static final String ELEMENTTYPE_Org = "OO";
/** Account = AC */
public static final String ELEMENTTYPE_Account = "AC";
/** Product = PR */
public static final String ELEMENTTYPE_Product = "PR";
/** BPartner = BP */
public static final String ELEMENTTYPE_BPartner = "BP";
/** Org Trx = OT */
public static final String ELEMENTTYPE_OrgTrx = "OT";
/** Location From = LF */
public static final String ELEMENTTYPE_LocationFrom = "LF";
/** Location To = LT */
public static final String ELEMENTTYPE_LocationTo = "LT";
/** Sales Region = SR */
public static final String ELEMENTTYPE_SalesRegion = "SR";
/** Project = PJ */
public static final String ELEMENTTYPE_Project = "PJ";
/** Campaign = MC */
public static final String ELEMENTTYPE_Campaign = "MC";
/** User 1 = U1 */
public static final String ELEMENTTYPE_User1 = "U1";
/** User 2 = U2 */
public static final String ELEMENTTYPE_User2 = "U2";
/** Set Type.
Element Type (account or user defined) */
public void setElementType (String ElementType)
{
if (ElementType == null || ElementType.equals("AY") || ElementType.equals("OO") || ElementType.equals("AC") || ElementType.equals("PR") || ElementType.equals("BP") || ElementType.equals("OT") || ElementType.equals("LF") || ElementType.equals("LT") || ElementType.equals("SR") || ElementType.equals("PJ") || ElementType.equals("MC") || ElementType.equals("U1") || ElementType.equals("U2"));
 else throw new IllegalArgumentException ("ElementType Invalid value - Reference_ID=181 - AY - OO - AC - PR - BP - OT - LF - LT - SR - PJ - MC - U1 - U2");
if (ElementType != null && ElementType.length() > 2)
{
log.warning("Length > 2 - truncated");
ElementType = ElementType.substring(0,2);
}
set_Value ("ElementType", ElementType);
}
/** Get Type.
Element Type (account or user defined) */
public String getElementType() 
{
return (String)get_Value("ElementType");
}
/** Set Budget.
General Ledger Budget */
public void setGL_Budget_ID (int GL_Budget_ID)
{
if (GL_Budget_ID <= 0) set_Value ("GL_Budget_ID", null);
 else 
set_Value ("GL_Budget_ID", new Integer(GL_Budget_ID));
}
/** Get Budget.
General Ledger Budget */
public int getGL_Budget_ID() 
{
Integer ii = (Integer)get_Value("GL_Budget_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Adhoc Conversion.
Perform conversion for all amounts to currency */
public void setIsAdhocConversion (boolean IsAdhocConversion)
{
set_Value ("IsAdhocConversion", new Boolean(IsAdhocConversion));
}
/** Get Adhoc Conversion.
Perform conversion for all amounts to currency */
public boolean isAdhocConversion() 
{
Object oo = get_Value("IsAdhocConversion");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Printed.
Indicates if this document / line is printed */
public void setIsPrinted (boolean IsPrinted)
{
set_Value ("IsPrinted", new Boolean(IsPrinted));
}
/** Get Printed.
Indicates if this document / line is printed */
public boolean isPrinted() 
{
Object oo = get_Value("IsPrinted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
Name = Name.substring(0,60);
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
/** Set Negative as zero */
public void setNegativeAsZero (boolean NegativeAsZero)
{
set_Value ("NegativeAsZero", new Boolean(NegativeAsZero));
}
/** Get Negative as zero */
public boolean isNegativeAsZero() 
{
Object oo = get_Value("NegativeAsZero");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int OPER_1_ID_AD_Reference_ID=239;
/** Set Operand 1.
First operand for calculation */
public void setOper_1_ID (int Oper_1_ID)
{
if (Oper_1_ID <= 0) set_Value ("Oper_1_ID", null);
 else 
set_Value ("Oper_1_ID", new Integer(Oper_1_ID));
}
/** Get Operand 1.
First operand for calculation */
public int getOper_1_ID() 
{
Integer ii = (Integer)get_Value("Oper_1_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int OPER_2_ID_AD_Reference_ID=239;
/** Set Operand 2.
Second operand for calculation */
public void setOper_2_ID (int Oper_2_ID)
{
if (Oper_2_ID <= 0) set_Value ("Oper_2_ID", null);
 else 
set_Value ("Oper_2_ID", new Integer(Oper_2_ID));
}
/** Get Operand 2.
Second operand for calculation */
public int getOper_2_ID() 
{
Integer ii = (Integer)get_Value("Oper_2_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ORG_ID_AD_Reference_ID=130;
/** Set Organization.
Organizational entity within client */
public void setOrg_ID (int Org_ID)
{
if (Org_ID <= 0) set_Value ("Org_ID", null);
 else 
set_Value ("Org_ID", new Integer(Org_ID));
}
/** Get Organization.
Organizational entity within client */
public int getOrg_ID() 
{
Integer ii = (Integer)get_Value("Org_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Report Column.
Column in Report */
public void setPA_ReportColumn_ID (int PA_ReportColumn_ID)
{
set_ValueNoCheck ("PA_ReportColumn_ID", new Integer(PA_ReportColumn_ID));
}
/** Get Report Column.
Column in Report */
public int getPA_ReportColumn_ID() 
{
Integer ii = (Integer)get_Value("PA_ReportColumn_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Report Column Set.
Collection of Columns for Report */
public void setPA_ReportColumnSet_ID (int PA_ReportColumnSet_ID)
{
set_ValueNoCheck ("PA_ReportColumnSet_ID", new Integer(PA_ReportColumnSet_ID));
}
/** Get Report Column Set.
Collection of Columns for Report */
public int getPA_ReportColumnSet_ID() 
{
Integer ii = (Integer)get_Value("PA_ReportColumnSet_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int POSTINGTYPE_AD_Reference_ID=125;
/** Presupuestaria = B */
public static final String POSTINGTYPE_Presupuestaria = "B";
/** Pendientes = E */
public static final String POSTINGTYPE_Pendientes = "E";
/** Estadisticos = S */
public static final String POSTINGTYPE_Estadisticos = "S";
/** Actual = A */
public static final String POSTINGTYPE_Actual = "A";
/** Set PostingType.
The type of amount that this journal updated */
public void setPostingType (String PostingType)
{
if (PostingType.equals("B") || PostingType.equals("E") || PostingType.equals("S") || PostingType.equals("A"));
 else throw new IllegalArgumentException ("PostingType Invalid value - Reference_ID=125 - B - E - S - A");
if (PostingType == null) throw new IllegalArgumentException ("PostingType is mandatory");
if (PostingType.length() > 1)
{
log.warning("Length > 1 - truncated");
PostingType = PostingType.substring(0,1);
}
set_Value ("PostingType", PostingType);
}
/** Get PostingType.
The type of amount that this journal updated */
public String getPostingType() 
{
return (String)get_Value("PostingType");
}
/** Set Relative Period.
Period offset (0 is current) */
public void setRelativePeriod (int RelativePeriod)
{
set_Value ("RelativePeriod", new Integer(RelativePeriod));
}
/** Get Relative Period.
Period offset (0 is current) */
public int getRelativePeriod() 
{
Integer ii = (Integer)get_Value("RelativePeriod");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
}
