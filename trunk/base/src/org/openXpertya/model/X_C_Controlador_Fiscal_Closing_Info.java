/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Controlador_Fiscal_Closing_Info
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-06-07 13:47:47.171 */
public class X_C_Controlador_Fiscal_Closing_Info extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Controlador_Fiscal_Closing_Info (Properties ctx, int C_Controlador_Fiscal_Closing_Info_ID, String trxName)
{
super (ctx, C_Controlador_Fiscal_Closing_Info_ID, trxName);
/** if (C_Controlador_Fiscal_Closing_Info_ID == 0)
{
setC_Controlador_Fiscal_Closing_Info_ID (0);
setC_Controlador_Fiscal_ID (0);
setCreditNote_A_LastEmitted (0);
setCreditNoteAmt (Env.ZERO);
setCreditNote_BC_LastEmitted (0);
setCreditNoteExemptAmt (Env.ZERO);
setCreditNoteGravadoAmt (Env.ZERO);
setCreditNoteInternalTaxAmt (Env.ZERO);
setCreditNoteNoGravadoAmt (Env.ZERO);
setCreditNoteNotRegisteredTaxAmt (Env.ZERO);
setCreditNotePerceptionAmt (Env.ZERO);
setCreditNoteTaxAmt (Env.ZERO);
setFiscalClosingDate (new Timestamp(System.currentTimeMillis()));
setFiscalClosingNo (0);
setFiscalClosingType (null);
setFiscalDocument_A_LastEmitted (0);
setFiscalDocumentAmt (Env.ZERO);
setFiscalDocument_BC_LastEmitted (0);
setFiscalDocumentExemptAmt (Env.ZERO);
setFiscalDocumentGravadoAmt (Env.ZERO);
setFiscalDocumentInternalTaxAmt (Env.ZERO);
setFiscalDocumentNoGravadoAmt (Env.ZERO);
setFiscalDocumentNotRegisteredTaxAmt (Env.ZERO);
setFiscalDocumentPerceptionAmt (Env.ZERO);
setFiscalDocumentTaxAmt (Env.ZERO);
setNoFiscalHomologatedAmt (Env.ZERO);
setQtyCanceledCreditNote (0);
setQtyCanceledFiscalDocument (0);
setQtyCreditNote (0);
setQtyCreditNoteA (0);
setQtyCreditNoteBC (0);
setQtyFiscalDocument (0);
setQtyFiscalDocumentA (0);
setQtyFiscalDocumentBC (0);
setQtyNoFiscalDocument (0);
setQtyNoFiscalHomologated (0);
}
 */
}
/** Load Constructor */
public X_C_Controlador_Fiscal_Closing_Info (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Controlador_Fiscal_Closing_Info");

/** TableName=C_Controlador_Fiscal_Closing_Info */
public static final String Table_Name="C_Controlador_Fiscal_Closing_Info";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Controlador_Fiscal_Closing_Info");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Controlador_Fiscal_Closing_Info[").append(getID()).append("]");
return sb.toString();
}
/** Set C_Controlador_Fiscal_Closing_Info_ID */
public void setC_Controlador_Fiscal_Closing_Info_ID (int C_Controlador_Fiscal_Closing_Info_ID)
{
set_ValueNoCheck ("C_Controlador_Fiscal_Closing_Info_ID", new Integer(C_Controlador_Fiscal_Closing_Info_ID));
}
/** Get C_Controlador_Fiscal_Closing_Info_ID */
public int getC_Controlador_Fiscal_Closing_Info_ID() 
{
Integer ii = (Integer)get_Value("C_Controlador_Fiscal_Closing_Info_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Controlador_Fiscal_ID */
public void setC_Controlador_Fiscal_ID (int C_Controlador_Fiscal_ID)
{
set_Value ("C_Controlador_Fiscal_ID", new Integer(C_Controlador_Fiscal_ID));
}
/** Get C_Controlador_Fiscal_ID */
public int getC_Controlador_Fiscal_ID() 
{
Integer ii = (Integer)get_Value("C_Controlador_Fiscal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Credit Note A Last Emitted */
public void setCreditNote_A_LastEmitted (int CreditNote_A_LastEmitted)
{
set_Value ("CreditNote_A_LastEmitted", new Integer(CreditNote_A_LastEmitted));
}
/** Get Credit Note A Last Emitted */
public int getCreditNote_A_LastEmitted() 
{
Integer ii = (Integer)get_Value("CreditNote_A_LastEmitted");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Credit Note Amt */
public void setCreditNoteAmt (BigDecimal CreditNoteAmt)
{
if (CreditNoteAmt == null) throw new IllegalArgumentException ("CreditNoteAmt is mandatory");
set_Value ("CreditNoteAmt", CreditNoteAmt);
}
/** Get Credit Note Amt */
public BigDecimal getCreditNoteAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditNoteAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Note B/C Last Emitted */
public void setCreditNote_BC_LastEmitted (int CreditNote_BC_LastEmitted)
{
set_Value ("CreditNote_BC_LastEmitted", new Integer(CreditNote_BC_LastEmitted));
}
/** Get Credit Note B/C Last Emitted */
public int getCreditNote_BC_LastEmitted() 
{
Integer ii = (Integer)get_Value("CreditNote_BC_LastEmitted");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Credit Note Exempt Amt */
public void setCreditNoteExemptAmt (BigDecimal CreditNoteExemptAmt)
{
if (CreditNoteExemptAmt == null) throw new IllegalArgumentException ("CreditNoteExemptAmt is mandatory");
set_Value ("CreditNoteExemptAmt", CreditNoteExemptAmt);
}
/** Get Credit Note Exempt Amt */
public BigDecimal getCreditNoteExemptAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditNoteExemptAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Note Gravado Amt */
public void setCreditNoteGravadoAmt (BigDecimal CreditNoteGravadoAmt)
{
if (CreditNoteGravadoAmt == null) throw new IllegalArgumentException ("CreditNoteGravadoAmt is mandatory");
set_Value ("CreditNoteGravadoAmt", CreditNoteGravadoAmt);
}
/** Get Credit Note Gravado Amt */
public BigDecimal getCreditNoteGravadoAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditNoteGravadoAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Note Internal Tax Amt */
public void setCreditNoteInternalTaxAmt (BigDecimal CreditNoteInternalTaxAmt)
{
if (CreditNoteInternalTaxAmt == null) throw new IllegalArgumentException ("CreditNoteInternalTaxAmt is mandatory");
set_Value ("CreditNoteInternalTaxAmt", CreditNoteInternalTaxAmt);
}
/** Get Credit Note Internal Tax Amt */
public BigDecimal getCreditNoteInternalTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditNoteInternalTaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Note No Gravado Amt */
public void setCreditNoteNoGravadoAmt (BigDecimal CreditNoteNoGravadoAmt)
{
if (CreditNoteNoGravadoAmt == null) throw new IllegalArgumentException ("CreditNoteNoGravadoAmt is mandatory");
set_Value ("CreditNoteNoGravadoAmt", CreditNoteNoGravadoAmt);
}
/** Get Credit Note No Gravado Amt */
public BigDecimal getCreditNoteNoGravadoAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditNoteNoGravadoAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Note Not Registered Tax Amt */
public void setCreditNoteNotRegisteredTaxAmt (BigDecimal CreditNoteNotRegisteredTaxAmt)
{
if (CreditNoteNotRegisteredTaxAmt == null) throw new IllegalArgumentException ("CreditNoteNotRegisteredTaxAmt is mandatory");
set_Value ("CreditNoteNotRegisteredTaxAmt", CreditNoteNotRegisteredTaxAmt);
}
/** Get Credit Note Not Registered Tax Amt */
public BigDecimal getCreditNoteNotRegisteredTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditNoteNotRegisteredTaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Note Perception Amt */
public void setCreditNotePerceptionAmt (BigDecimal CreditNotePerceptionAmt)
{
if (CreditNotePerceptionAmt == null) throw new IllegalArgumentException ("CreditNotePerceptionAmt is mandatory");
set_Value ("CreditNotePerceptionAmt", CreditNotePerceptionAmt);
}
/** Get Credit Note Perception Amt */
public BigDecimal getCreditNotePerceptionAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditNotePerceptionAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Note Tax Amt */
public void setCreditNoteTaxAmt (BigDecimal CreditNoteTaxAmt)
{
if (CreditNoteTaxAmt == null) throw new IllegalArgumentException ("CreditNoteTaxAmt is mandatory");
set_Value ("CreditNoteTaxAmt", CreditNoteTaxAmt);
}
/** Get Credit Note Tax Amt */
public BigDecimal getCreditNoteTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditNoteTaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fiscal Closing Date */
public void setFiscalClosingDate (Timestamp FiscalClosingDate)
{
if (FiscalClosingDate == null) throw new IllegalArgumentException ("FiscalClosingDate is mandatory");
set_Value ("FiscalClosingDate", FiscalClosingDate);
}
/** Get Fiscal Closing Date */
public Timestamp getFiscalClosingDate() 
{
return (Timestamp)get_Value("FiscalClosingDate");
}
/** Set Fiscal Closing No */
public void setFiscalClosingNo (int FiscalClosingNo)
{
set_Value ("FiscalClosingNo", new Integer(FiscalClosingNo));
}
/** Get Fiscal Closing No */
public int getFiscalClosingNo() 
{
Integer ii = (Integer)get_Value("FiscalClosingNo");
if (ii == null) return 0;
return ii.intValue();
}
public static final int FISCALCLOSINGTYPE_AD_Reference_ID = MReference.getReferenceID("Fiscal_Close_Types");
/** Cierre X = X */
public static final String FISCALCLOSINGTYPE_CierreX = "X";
/** Cierre Z = Z */
public static final String FISCALCLOSINGTYPE_CierreZ = "Z";
/** Set Fiscal Closing Type */
public void setFiscalClosingType (String FiscalClosingType)
{
if (FiscalClosingType.equals("X") || FiscalClosingType.equals("Z") || ( refContainsValue("CORE-AD_Reference-1010119", FiscalClosingType) ) );
 else throw new IllegalArgumentException ("FiscalClosingType Invalid value: " + FiscalClosingType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010119") );
if (FiscalClosingType == null) throw new IllegalArgumentException ("FiscalClosingType is mandatory");
if (FiscalClosingType.length() > 1)
{
log.warning("Length > 1 - truncated");
FiscalClosingType = FiscalClosingType.substring(0,1);
}
set_Value ("FiscalClosingType", FiscalClosingType);
}
/** Get Fiscal Closing Type */
public String getFiscalClosingType() 
{
return (String)get_Value("FiscalClosingType");
}
/** Set Fiscal Document A Last Emitted */
public void setFiscalDocument_A_LastEmitted (int FiscalDocument_A_LastEmitted)
{
set_Value ("FiscalDocument_A_LastEmitted", new Integer(FiscalDocument_A_LastEmitted));
}
/** Get Fiscal Document A Last Emitted */
public int getFiscalDocument_A_LastEmitted() 
{
Integer ii = (Integer)get_Value("FiscalDocument_A_LastEmitted");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Fiscal Document Amt */
public void setFiscalDocumentAmt (BigDecimal FiscalDocumentAmt)
{
if (FiscalDocumentAmt == null) throw new IllegalArgumentException ("FiscalDocumentAmt is mandatory");
set_Value ("FiscalDocumentAmt", FiscalDocumentAmt);
}
/** Get Fiscal Document Amt */
public BigDecimal getFiscalDocumentAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FiscalDocumentAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fiscal Document B/C Last Emitted */
public void setFiscalDocument_BC_LastEmitted (int FiscalDocument_BC_LastEmitted)
{
set_Value ("FiscalDocument_BC_LastEmitted", new Integer(FiscalDocument_BC_LastEmitted));
}
/** Get Fiscal Document B/C Last Emitted */
public int getFiscalDocument_BC_LastEmitted() 
{
Integer ii = (Integer)get_Value("FiscalDocument_BC_LastEmitted");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Fiscal Document Exempt Amt */
public void setFiscalDocumentExemptAmt (BigDecimal FiscalDocumentExemptAmt)
{
if (FiscalDocumentExemptAmt == null) throw new IllegalArgumentException ("FiscalDocumentExemptAmt is mandatory");
set_Value ("FiscalDocumentExemptAmt", FiscalDocumentExemptAmt);
}
/** Get Fiscal Document Exempt Amt */
public BigDecimal getFiscalDocumentExemptAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FiscalDocumentExemptAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fiscal Document Gravado Amt */
public void setFiscalDocumentGravadoAmt (BigDecimal FiscalDocumentGravadoAmt)
{
if (FiscalDocumentGravadoAmt == null) throw new IllegalArgumentException ("FiscalDocumentGravadoAmt is mandatory");
set_Value ("FiscalDocumentGravadoAmt", FiscalDocumentGravadoAmt);
}
/** Get Fiscal Document Gravado Amt */
public BigDecimal getFiscalDocumentGravadoAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FiscalDocumentGravadoAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fiscal Document Internal Tax Amt */
public void setFiscalDocumentInternalTaxAmt (BigDecimal FiscalDocumentInternalTaxAmt)
{
if (FiscalDocumentInternalTaxAmt == null) throw new IllegalArgumentException ("FiscalDocumentInternalTaxAmt is mandatory");
set_Value ("FiscalDocumentInternalTaxAmt", FiscalDocumentInternalTaxAmt);
}
/** Get Fiscal Document Internal Tax Amt */
public BigDecimal getFiscalDocumentInternalTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FiscalDocumentInternalTaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fiscal Document No Gravado Amt */
public void setFiscalDocumentNoGravadoAmt (BigDecimal FiscalDocumentNoGravadoAmt)
{
if (FiscalDocumentNoGravadoAmt == null) throw new IllegalArgumentException ("FiscalDocumentNoGravadoAmt is mandatory");
set_Value ("FiscalDocumentNoGravadoAmt", FiscalDocumentNoGravadoAmt);
}
/** Get Fiscal Document No Gravado Amt */
public BigDecimal getFiscalDocumentNoGravadoAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FiscalDocumentNoGravadoAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fiscal Document Not Registered Tax Amt */
public void setFiscalDocumentNotRegisteredTaxAmt (BigDecimal FiscalDocumentNotRegisteredTaxAmt)
{
if (FiscalDocumentNotRegisteredTaxAmt == null) throw new IllegalArgumentException ("FiscalDocumentNotRegisteredTaxAmt is mandatory");
set_Value ("FiscalDocumentNotRegisteredTaxAmt", FiscalDocumentNotRegisteredTaxAmt);
}
/** Get Fiscal Document Not Registered Tax Amt */
public BigDecimal getFiscalDocumentNotRegisteredTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FiscalDocumentNotRegisteredTaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fiscal Document Perception Amt */
public void setFiscalDocumentPerceptionAmt (BigDecimal FiscalDocumentPerceptionAmt)
{
if (FiscalDocumentPerceptionAmt == null) throw new IllegalArgumentException ("FiscalDocumentPerceptionAmt is mandatory");
set_Value ("FiscalDocumentPerceptionAmt", FiscalDocumentPerceptionAmt);
}
/** Get Fiscal Document Perception Amt */
public BigDecimal getFiscalDocumentPerceptionAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FiscalDocumentPerceptionAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fiscal Document Tax Amt */
public void setFiscalDocumentTaxAmt (BigDecimal FiscalDocumentTaxAmt)
{
if (FiscalDocumentTaxAmt == null) throw new IllegalArgumentException ("FiscalDocumentTaxAmt is mandatory");
set_Value ("FiscalDocumentTaxAmt", FiscalDocumentTaxAmt);
}
/** Get Fiscal Document Tax Amt */
public BigDecimal getFiscalDocumentTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FiscalDocumentTaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set No Fiscal Homologated Amt */
public void setNoFiscalHomologatedAmt (BigDecimal NoFiscalHomologatedAmt)
{
if (NoFiscalHomologatedAmt == null) throw new IllegalArgumentException ("NoFiscalHomologatedAmt is mandatory");
set_Value ("NoFiscalHomologatedAmt", NoFiscalHomologatedAmt);
}
/** Get No Fiscal Homologated Amt */
public BigDecimal getNoFiscalHomologatedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("NoFiscalHomologatedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set POS Name */
public void setPOSName (String POSName)
{
if (POSName != null && POSName.length() > 100)
{
log.warning("Length > 100 - truncated");
POSName = POSName.substring(0,100);
}
set_Value ("POSName", POSName);
}
/** Get POS Name */
public String getPOSName() 
{
return (String)get_Value("POSName");
}
/** Set Punto De Venta */
public void setPuntoDeVenta (int PuntoDeVenta)
{
set_Value ("PuntoDeVenta", new Integer(PuntoDeVenta));
}
/** Get Punto De Venta */
public int getPuntoDeVenta() 
{
Integer ii = (Integer)get_Value("PuntoDeVenta");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Qty Canceled Credit Note */
public void setQtyCanceledCreditNote (int QtyCanceledCreditNote)
{
set_Value ("QtyCanceledCreditNote", new Integer(QtyCanceledCreditNote));
}
/** Get Qty Canceled Credit Note */
public int getQtyCanceledCreditNote() 
{
Integer ii = (Integer)get_Value("QtyCanceledCreditNote");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Qty Canceled Fiscal Document */
public void setQtyCanceledFiscalDocument (int QtyCanceledFiscalDocument)
{
set_Value ("QtyCanceledFiscalDocument", new Integer(QtyCanceledFiscalDocument));
}
/** Get Qty Canceled Fiscal Document */
public int getQtyCanceledFiscalDocument() 
{
Integer ii = (Integer)get_Value("QtyCanceledFiscalDocument");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Qty Credit Note */
public void setQtyCreditNote (int QtyCreditNote)
{
set_Value ("QtyCreditNote", new Integer(QtyCreditNote));
}
/** Get Qty Credit Note */
public int getQtyCreditNote() 
{
Integer ii = (Integer)get_Value("QtyCreditNote");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Qty Credit Note A */
public void setQtyCreditNoteA (int QtyCreditNoteA)
{
set_Value ("QtyCreditNoteA", new Integer(QtyCreditNoteA));
}
/** Get Qty Credit Note A */
public int getQtyCreditNoteA() 
{
Integer ii = (Integer)get_Value("QtyCreditNoteA");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Qty Credit Note B/C */
public void setQtyCreditNoteBC (int QtyCreditNoteBC)
{
set_Value ("QtyCreditNoteBC", new Integer(QtyCreditNoteBC));
}
/** Get Qty Credit Note B/C */
public int getQtyCreditNoteBC() 
{
Integer ii = (Integer)get_Value("QtyCreditNoteBC");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Qty Fiscal Document */
public void setQtyFiscalDocument (int QtyFiscalDocument)
{
set_Value ("QtyFiscalDocument", new Integer(QtyFiscalDocument));
}
/** Get Qty Fiscal Document */
public int getQtyFiscalDocument() 
{
Integer ii = (Integer)get_Value("QtyFiscalDocument");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Qty Fiscal Document A */
public void setQtyFiscalDocumentA (int QtyFiscalDocumentA)
{
set_Value ("QtyFiscalDocumentA", new Integer(QtyFiscalDocumentA));
}
/** Get Qty Fiscal Document A */
public int getQtyFiscalDocumentA() 
{
Integer ii = (Integer)get_Value("QtyFiscalDocumentA");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Qty Fiscal Document B/C */
public void setQtyFiscalDocumentBC (int QtyFiscalDocumentBC)
{
set_Value ("QtyFiscalDocumentBC", new Integer(QtyFiscalDocumentBC));
}
/** Get Qty Fiscal Document B/C */
public int getQtyFiscalDocumentBC() 
{
Integer ii = (Integer)get_Value("QtyFiscalDocumentBC");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Qty No Fiscal Document */
public void setQtyNoFiscalDocument (int QtyNoFiscalDocument)
{
set_Value ("QtyNoFiscalDocument", new Integer(QtyNoFiscalDocument));
}
/** Get Qty No Fiscal Document */
public int getQtyNoFiscalDocument() 
{
Integer ii = (Integer)get_Value("QtyNoFiscalDocument");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Qty No Fiscal Homologated */
public void setQtyNoFiscalHomologated (int QtyNoFiscalHomologated)
{
set_Value ("QtyNoFiscalHomologated", new Integer(QtyNoFiscalHomologated));
}
/** Get Qty No Fiscal Homologated */
public int getQtyNoFiscalHomologated() 
{
Integer ii = (Integer)get_Value("QtyNoFiscalHomologated");
if (ii == null) return 0;
return ii.intValue();
}
}
