/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_ReportLineSet
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-12-07 13:27:21.234 */
public class X_I_ReportLineSet extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_ReportLineSet (Properties ctx, int I_ReportLineSet_ID, String trxName)
{
super (ctx, I_ReportLineSet_ID, trxName);
/** if (I_ReportLineSet_ID == 0)
{
setI_ReportLineSet_ID (0);
}
 */
}
/** Load Constructor */
public X_I_ReportLineSet (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_ReportLineSet");

/** TableName=I_ReportLineSet */
public static final String Table_Name="I_ReportLineSet";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_ReportLineSet");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_ReportLineSet[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_ORGTRX_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (Trx)");
/** Set Trx Organization.
Performing or initiating organization */
public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
{
if (AD_OrgTrx_ID <= 0) set_Value ("AD_OrgTrx_ID", null);
 else 
set_Value ("AD_OrgTrx_ID", new Integer(AD_OrgTrx_ID));
}
/** Get Trx Organization.
Performing or initiating organization */
public int getAD_OrgTrx_ID() 
{
Integer ii = (Integer)get_Value("AD_OrgTrx_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AMOUNTTYPE_AD_Reference_ID = MReference.getReferenceID("PA_Report AmountType");
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
 else throw new IllegalArgumentException ("AmountType Invalid value - Reference = AMOUNTTYPE_AD_Reference_ID - DT - CT - BT - QT - BY - CY - DY - QY - QP - BP - CP - DP");
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
public static final int CALCULATIONTYPE_AD_Reference_ID = MReference.getReferenceID("C_Categoria_Iva");
/** Set Calculation */
public void setCalculationType (String CalculationType)
{
if (CalculationType != null && CalculationType.length() > 2)
{
log.warning("Length > 2 - truncated");
CalculationType = CalculationType.substring(0,2);
}
set_Value ("CalculationType", CalculationType);
}
/** Get Calculation */
public String getCalculationType() 
{
return (String)get_Value("CalculationType");
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
public static final int CHANGESIGN_AD_Reference_ID = MReference.getReferenceID("C_Categoria_Iva");
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
if (Description != null && Description.length() > 250)
{
log.warning("Length > 250 - truncated");
Description = Description.substring(0,250);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
public static final int ELEMENTTYPE_AD_Reference_ID = MReference.getReferenceID("C_AcctSchema ElementType");
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
 else throw new IllegalArgumentException ("ElementType Invalid value - Reference = ELEMENTTYPE_AD_Reference_ID - AY - OO - AC - PR - BP - OT - LF - LT - SR - PJ - MC - U1 - U2");
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
/** Set ev_value */
public void setEv_Value (String Ev_Value)
{
if (Ev_Value != null && Ev_Value.length() > 250)
{
log.warning("Length > 250 - truncated");
Ev_Value = Ev_Value.substring(0,250);
}
set_Value ("Ev_Value", Ev_Value);
}
/** Get ev_value */
public String getEv_Value() 
{
return (String)get_Value("Ev_Value");
}
/** Set Function */
public void setFunc (String Func)
{
if (Func != null && Func.length() > 1024)
{
log.warning("Length > 1024 - truncated");
Func = Func.substring(0,1024);
}
set_Value ("Func", Func);
}
/** Get Function */
public String getFunc() 
{
return (String)get_Value("Func");
}
public static final int I_ERRORMSG_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set Import Error Message.
Messages generated from import process */
public void setI_ErrorMsg (String I_ErrorMsg)
{
if (I_ErrorMsg != null && I_ErrorMsg.length() > 4000)
{
log.warning("Length > 4000 - truncated");
I_ErrorMsg = I_ErrorMsg.substring(0,4000);
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
public static final int I_REPORTLINESET_ID_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set Report Line Set */
public void setI_ReportLineSet_ID (int I_ReportLineSet_ID)
{
set_ValueNoCheck ("I_ReportLineSet_ID", new Integer(I_ReportLineSet_ID));
}
/** Get Report Line Set */
public int getI_ReportLineSet_ID() 
{
Integer ii = (Integer)get_Value("I_ReportLineSet_ID");
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
/** Set Is report source */
public void setIsReportSource (boolean IsReportSource)
{
set_Value ("IsReportSource", new Boolean(IsReportSource));
}
/** Get Is report source */
public boolean isReportSource() 
{
Object oo = get_Value("IsReportSource");
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
public static final int LINETYPE_AD_Reference_ID = MReference.getReferenceID("PA_Report LineType");
/** Description = D */
public static final String LINETYPE_Description = "D";
/** Segment Value = S */
public static final String LINETYPE_SegmentValue = "S";
/** Calculation = C */
public static final String LINETYPE_Calculation = "C";
/** Set Line Type */
public void setLineType (String LineType)
{
if (LineType == null || LineType.equals("D") || LineType.equals("S") || LineType.equals("C"));
 else throw new IllegalArgumentException ("LineType Invalid value - Reference = LINETYPE_AD_Reference_ID - D - S - C");
if (LineType != null && LineType.length() > 1)
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
if (Name != null && Name.length() > 250)
{
log.warning("Length > 250 - truncated");
Name = Name.substring(0,250);
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
/** Set Report Line */
public void setPA_ReportLine_ID (int PA_ReportLine_ID)
{
if (PA_ReportLine_ID <= 0) set_Value ("PA_ReportLine_ID", null);
 else 
set_Value ("PA_ReportLine_ID", new Integer(PA_ReportLine_ID));
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
if (PA_ReportLineSet_ID <= 0) set_Value ("PA_ReportLineSet_ID", null);
 else 
set_Value ("PA_ReportLineSet_ID", new Integer(PA_ReportLineSet_ID));
}
/** Get Report Line Set */
public int getPA_ReportLineSet_ID() 
{
Integer ii = (Integer)get_Value("PA_ReportLineSet_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Report Source.
Restriction of what will be shown in Report Line */
public void setPA_ReportSource_ID (int PA_ReportSource_ID)
{
if (PA_ReportSource_ID <= 0) set_Value ("PA_ReportSource_ID", null);
 else 
set_Value ("PA_ReportSource_ID", new Integer(PA_ReportSource_ID));
}
/** Get Report Source.
Restriction of what will be shown in Report Line */
public int getPA_ReportSource_ID() 
{
Integer ii = (Integer)get_Value("PA_ReportSource_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int POSTINGTYPE_AD_Reference_ID = MReference.getReferenceID("_Posting Type");
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
 else throw new IllegalArgumentException ("PostingType Invalid value - Reference = POSTINGTYPE_AD_Reference_ID - B - E - S - A");
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
/** Set Print Line Number */
public void setPrintLineNo (int PrintLineNo)
{
set_Value ("PrintLineNo", new Integer(PrintLineNo));
}
/** Get Print Line Number */
public int getPrintLineNo() 
{
Integer ii = (Integer)get_Value("PrintLineNo");
if (ii == null) return 0;
return ii.intValue();
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
/** Set rls_description */
public void setRLS_Description (String RLS_Description)
{
if (RLS_Description != null && RLS_Description.length() > 250)
{
log.warning("Length > 250 - truncated");
RLS_Description = RLS_Description.substring(0,250);
}
set_Value ("RLS_Description", RLS_Description);
}
/** Get rls_description */
public String getRLS_Description() 
{
return (String)get_Value("RLS_Description");
}
/** Set rls_name */
public void setRLS_Name (String RLS_Name)
{
if (RLS_Name != null && RLS_Name.length() > 250)
{
log.warning("Length > 250 - truncated");
RLS_Name = RLS_Name.substring(0,250);
}
set_Value ("RLS_Name", RLS_Name);
}
/** Get rls_name */
public String getRLS_Name() 
{
return (String)get_Value("RLS_Name");
}
public static final int RS_ELEMENTTYPE_AD_Reference_ID = MReference.getReferenceID("C_AcctSchema ElementType");
/** Activity = AY */
public static final String RS_ELEMENTTYPE_Activity = "AY";
/** Org = OO */
public static final String RS_ELEMENTTYPE_Org = "OO";
/** Account = AC */
public static final String RS_ELEMENTTYPE_Account = "AC";
/** Product = PR */
public static final String RS_ELEMENTTYPE_Product = "PR";
/** BPartner = BP */
public static final String RS_ELEMENTTYPE_BPartner = "BP";
/** Org Trx = OT */
public static final String RS_ELEMENTTYPE_OrgTrx = "OT";
/** Location From = LF */
public static final String RS_ELEMENTTYPE_LocationFrom = "LF";
/** Location To = LT */
public static final String RS_ELEMENTTYPE_LocationTo = "LT";
/** Sales Region = SR */
public static final String RS_ELEMENTTYPE_SalesRegion = "SR";
/** Project = PJ */
public static final String RS_ELEMENTTYPE_Project = "PJ";
/** Campaign = MC */
public static final String RS_ELEMENTTYPE_Campaign = "MC";
/** User 1 = U1 */
public static final String RS_ELEMENTTYPE_User1 = "U1";
/** User 2 = U2 */
public static final String RS_ELEMENTTYPE_User2 = "U2";
/** Set RS_ElementType */
public void setRS_ElementType (String RS_ElementType)
{
if (RS_ElementType == null || RS_ElementType.equals("AY") || RS_ElementType.equals("OO") || RS_ElementType.equals("AC") || RS_ElementType.equals("PR") || RS_ElementType.equals("BP") || RS_ElementType.equals("OT") || RS_ElementType.equals("LF") || RS_ElementType.equals("LT") || RS_ElementType.equals("SR") || RS_ElementType.equals("PJ") || RS_ElementType.equals("MC") || RS_ElementType.equals("U1") || RS_ElementType.equals("U2"));
 else throw new IllegalArgumentException ("RS_ElementType Invalid value - Reference = RS_ELEMENTTYPE_AD_Reference_ID - AY - OO - AC - PR - BP - OT - LF - LT - SR - PJ - MC - U1 - U2");
if (RS_ElementType != null && RS_ElementType.length() > 2)
{
log.warning("Length > 2 - truncated");
RS_ElementType = RS_ElementType.substring(0,2);
}
set_Value ("RS_ElementType", RS_ElementType);
}
/** Get RS_ElementType */
public String getRS_ElementType() 
{
return (String)get_Value("RS_ElementType");
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
