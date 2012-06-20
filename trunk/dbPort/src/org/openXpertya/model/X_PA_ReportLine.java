/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_ReportLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:37.838 */
public class X_PA_ReportLine extends PO
{
/** Constructor estándar */
public X_PA_ReportLine (Properties ctx, int PA_ReportLine_ID, String trxName)
{
super (ctx, PA_ReportLine_ID, trxName);
/** if (PA_ReportLine_ID == 0)
{
setIsEverPrinted (false);
setIsPageBreak (false);
setIsPrinted (true);	// Y
setIsSummary (false);
setLineType (null);
setName (null);
setPA_ReportLine_ID (0);
setPA_ReportLineSet_ID (0);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM PA_ReportLine WHERE PA_ReportLineSet_ID=@PA_ReportLineSet_ID@
}
 */
}
/** Load Constructor */
public X_PA_ReportLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=448 */
public static final int Table_ID=448;

/** TableName=PA_ReportLine */
public static final String Table_Name="PA_ReportLine";

protected static KeyNamePair Model = new KeyNamePair(448,"PA_ReportLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_ReportLine[").append(getID()).append("]");
return sb.toString();
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
/** Set Change sign */
public void setChangeSign (boolean ChangeSign)
{
set_Value ("ChangeSign", new Boolean(ChangeSign));
}
/** Get Change sign */
public boolean isChangeSign() 
{
Object oo = get_Value("ChangeSign");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Function */
public void setFunc (String Func)
{
if (Func != null && Func.length() > 512)
{
log.warning("Length > 512 - truncated");
Func = Func.substring(0,512);
}
set_Value ("Func", Func);
}
/** Get Function */
public String getFunc() 
{
return (String)get_Value("Func");
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
public static final int INDENTLEVEL_AD_Reference_ID=110;
/** Set Indentation Level */
public void setIndentLevel (int IndentLevel)
{
set_Value ("IndentLevel", new Integer(IndentLevel));
}
/** Get Indentation Level */
public int getIndentLevel() 
{
Integer ii = (Integer)get_Value("IndentLevel");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Is bold */
public void setIsBold (boolean IsBold)
{
set_Value ("IsBold", new Boolean(IsBold));
}
/** Get Is bold */
public boolean isBold() 
{
Object oo = get_Value("IsBold");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int ISEVERPRINTED_AD_Reference_ID=110;
/** Set Is ever printed */
public void setIsEverPrinted (boolean IsEverPrinted)
{
set_Value ("IsEverPrinted", new Boolean(IsEverPrinted));
}
/** Get Is ever printed */
public boolean isEverPrinted() 
{
Object oo = get_Value("IsEverPrinted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Page break.
Start with new page */
public void setIsPageBreak (boolean IsPageBreak)
{
set_Value ("IsPageBreak", new Boolean(IsPageBreak));
}
/** Get Page break.
Start with new page */
public boolean isPageBreak() 
{
Object oo = get_Value("IsPageBreak");
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
public static final int LINETYPE_AD_Reference_ID=241;
/** Description = D */
public static final String LINETYPE_Description = "D";
/** Segment Value = S */
public static final String LINETYPE_SegmentValue = "S";
/** Calculation = C */
public static final String LINETYPE_Calculation = "C";
/** Set Line Type */
public void setLineType (String LineType)
{
if (LineType.equals("D") || LineType.equals("S") || LineType.equals("C"));
 else throw new IllegalArgumentException ("LineType Invalid value - Reference_ID=241 - D - S - C");
if (LineType == null) throw new IllegalArgumentException ("LineType is mandatory");
if (LineType.length() > 1)
{
log.warning("Length > 1 - truncated");
LineType = LineType.substring(0,1);
}
set_Value ("LineType", LineType);
}
/** Get Line Type */
public String getLineType() 
{
return (String)get_Value("LineType");
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
public static final int OPER_1_ID_AD_Reference_ID=240;
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
public static final int OPER_2_ID_AD_Reference_ID=240;
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
public static final int OPER_3_ID_AD_Reference_ID=240;
/** Set Oper 3 */
public void setOper_3_ID (int Oper_3_ID)
{
if (Oper_3_ID <= 0) set_Value ("Oper_3_ID", null);
 else 
set_Value ("Oper_3_ID", new Integer(Oper_3_ID));
}
/** Get Oper 3 */
public int getOper_3_ID() 
{
Integer ii = (Integer)get_Value("Oper_3_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int OPER_4_ID_AD_Reference_ID=240;
/** Set Oper 4 */
public void setOper_4_ID (int Oper_4_ID)
{
if (Oper_4_ID <= 0) set_Value ("Oper_4_ID", null);
 else 
set_Value ("Oper_4_ID", new Integer(Oper_4_ID));
}
/** Get Oper 4 */
public int getOper_4_ID() 
{
Integer ii = (Integer)get_Value("Oper_4_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int OPER_5_ID_AD_Reference_ID=240;
/** Set Oper 5 */
public void setOper_5_ID (int Oper_5_ID)
{
if (Oper_5_ID <= 0) set_Value ("Oper_5_ID", null);
 else 
set_Value ("Oper_5_ID", new Integer(Oper_5_ID));
}
/** Get Oper 5 */
public int getOper_5_ID() 
{
Integer ii = (Integer)get_Value("Oper_5_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int OPER_6_ID_AD_Reference_ID=240;
/** Set Oper 6 */
public void setOper_6_ID (int Oper_6_ID)
{
if (Oper_6_ID <= 0) set_Value ("Oper_6_ID", null);
 else 
set_Value ("Oper_6_ID", new Integer(Oper_6_ID));
}
/** Get Oper 6 */
public int getOper_6_ID() 
{
Integer ii = (Integer)get_Value("Oper_6_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int OPER_7_ID_AD_Reference_ID=240;
/** Set Oper 7 */
public void setOper_7_ID (int Oper_7_ID)
{
if (Oper_7_ID <= 0) set_Value ("Oper_7_ID", null);
 else 
set_Value ("Oper_7_ID", new Integer(Oper_7_ID));
}
/** Get Oper 7 */
public int getOper_7_ID() 
{
Integer ii = (Integer)get_Value("Oper_7_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int OPER_8_ID_AD_Reference_ID=240;
/** Set Oper 8 */
public void setOper_8_ID (int Oper_8_ID)
{
if (Oper_8_ID <= 0) set_Value ("Oper_8_ID", null);
 else 
set_Value ("Oper_8_ID", new Integer(Oper_8_ID));
}
/** Get Oper 8 */
public int getOper_8_ID() 
{
Integer ii = (Integer)get_Value("Oper_8_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int OPER_9_ID_AD_Reference_ID=240;
/** Set Oper 9 */
public void setOper_9_ID (int Oper_9_ID)
{
if (Oper_9_ID <= 0) set_Value ("Oper_9_ID", null);
 else 
set_Value ("Oper_9_ID", new Integer(Oper_9_ID));
}
/** Get Oper 9 */
public int getOper_9_ID() 
{
Integer ii = (Integer)get_Value("Oper_9_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Oper. count */
public void setOper_Count (int Oper_Count)
{
set_Value ("Oper_Count", new Integer(Oper_Count));
}
/** Get Oper. count */
public int getOper_Count() 
{
Integer ii = (Integer)get_Value("Oper_Count");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PARENT_ID_AD_Reference_ID=242;
/** Set Parent.
Parent of Entity */
public void setParent_ID (int Parent_ID)
{
if (Parent_ID <= 0) set_Value ("Parent_ID", null);
 else 
set_Value ("Parent_ID", new Integer(Parent_ID));
}
/** Get Parent.
Parent of Entity */
public int getParent_ID() 
{
Integer ii = (Integer)get_Value("Parent_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Report Line */
public void setPA_ReportLine_ID (int PA_ReportLine_ID)
{
set_ValueNoCheck ("PA_ReportLine_ID", new Integer(PA_ReportLine_ID));
}
/** Get Report Line */
public int getPA_ReportLine_ID() 
{
Integer ii = (Integer)get_Value("PA_ReportLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Report Line Set */
public void setPA_ReportLineSet_ID (int PA_ReportLineSet_ID)
{
set_ValueNoCheck ("PA_ReportLineSet_ID", new Integer(PA_ReportLineSet_ID));
}
/** Get Report Line Set */
public int getPA_ReportLineSet_ID() 
{
Integer ii = (Integer)get_Value("PA_ReportLineSet_ID");
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
if (PostingType == null || PostingType.equals("B") || PostingType.equals("E") || PostingType.equals("S") || PostingType.equals("A"));
 else throw new IllegalArgumentException ("PostingType Invalid value - Reference_ID=125 - B - E - S - A");
if (PostingType != null && PostingType.length() > 1)
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
/** Set Print Line NUMERIC */
public void setPrintLineNo (int PrintLineNo)
{
set_Value ("PrintLineNo", new Integer(PrintLineNo));
}
/** Get Print Line NUMERIC */
public int getPrintLineNo() 
{
Integer ii = (Integer)get_Value("PrintLineNo");
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
