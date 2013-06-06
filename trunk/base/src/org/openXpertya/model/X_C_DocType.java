/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_DocType
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-06-04 18:48:21.265 */
public class X_C_DocType extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_DocType (Properties ctx, int C_DocType_ID, String trxName)
{
super (ctx, C_DocType_ID, trxName);
/** if (C_DocType_ID == 0)
{
setAllowChangePriceList (false);
setAllowDeliveryReturned (false);
setC_DocType_ID (0);
setDocBaseType (null);
setDocTypeKey (null);
setDocumentCopies (0);	// 1
setDragOrderDocumentDiscounts (false);
setDragOrderLineDiscounts (false);
setDragOrderPrice (false);
setEnabledInPOS (false);
setEnableInCreateFromShipment (false);
setFiscalDocument (null);
setGL_Category_ID (0);
setHasCharges (false);
setIsCheckoutPlaceRestricted (false);
setIsCreateCounter (true);	// Y
setIsDefault (false);
setIsDefaultCounterDoc (false);
setIsDocNoControlled (true);	// Y
setIsFiscal (false);
setIsFiscalDocument (false);	// N
setIsInTransit (false);
setIsPickQAConfirm (false);
setIsPrintAtCompleting (false);
setIsPrintPreview (true);	// Y
setIsShipConfirm (false);
setIsSOTrx (false);
setIsSplitWhenDifference (false);	// N
setLinesCountMax (0);
setName (null);
setPOSEnableDue (false);
setPOSEnableDueDays (0);
setPrintName (null);
setsigno_issotrx (null);
}
 */
}
/** Load Constructor */
public X_C_DocType (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_DocType");

/** TableName=C_DocType */
public static final String Table_Name="C_DocType";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_DocType");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_DocType[").append(getID()).append("]");
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
/** Set Print Format.
Data Print Format */
public void setAD_PrintFormat_ID (int AD_PrintFormat_ID)
{
if (AD_PrintFormat_ID <= 0) set_Value ("AD_PrintFormat_ID", null);
 else 
set_Value ("AD_PrintFormat_ID", new Integer(AD_PrintFormat_ID));
}
/** Get Print Format.
Data Print Format */
public int getAD_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Process.
Process or Report */
public void setAD_Process_ID (int AD_Process_ID)
{
if (AD_Process_ID <= 0) set_Value ("AD_Process_ID", null);
 else 
set_Value ("AD_Process_ID", new Integer(AD_Process_ID));
}
/** Get Process.
Process or Report */
public int getAD_Process_ID() 
{
Integer ii = (Integer)get_Value("AD_Process_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Allow Change Price List.
Change price list is allowed in document. All prices in lines will be overwritten */
public void setAllowChangePriceList (boolean AllowChangePriceList)
{
set_Value ("AllowChangePriceList", new Boolean(AllowChangePriceList));
}
/** Get Allow Change Price List.
Change price list is allowed in document. All prices in lines will be overwritten */
public boolean isAllowChangePriceList() 
{
Object oo = get_Value("AllowChangePriceList");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Allow delivery returneds */
public void setAllowDeliveryReturned (boolean AllowDeliveryReturned)
{
set_Value ("AllowDeliveryReturned", new Boolean(AllowDeliveryReturned));
}
/** Get Allow delivery returneds */
public boolean isAllowDeliveryReturned() 
{
Object oo = get_Value("AllowDeliveryReturned");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set CAI */
public void setCAI (String CAI)
{
if (CAI != null && CAI.length() > 14)
{
log.warning("Length > 14 - truncated");
CAI = CAI.substring(0,14);
}
set_Value ("CAI", CAI);
}
/** Get CAI */
public String getCAI() 
{
return (String)get_Value("CAI");
}
/** Set C_Controlador_Fiscal_ID */
public void setC_Controlador_Fiscal_ID (int C_Controlador_Fiscal_ID)
{
if (C_Controlador_Fiscal_ID <= 0) set_Value ("C_Controlador_Fiscal_ID", null);
 else 
set_Value ("C_Controlador_Fiscal_ID", new Integer(C_Controlador_Fiscal_ID));
}
/** Get C_Controlador_Fiscal_ID */
public int getC_Controlador_Fiscal_ID() 
{
Integer ii = (Integer)get_Value("C_Controlador_Fiscal_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPEDIFFERENCE_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Difference Document.
Document type for generating in dispute Shipments */
public void setC_DocTypeDifference_ID (int C_DocTypeDifference_ID)
{
if (C_DocTypeDifference_ID <= 0) set_Value ("C_DocTypeDifference_ID", null);
 else 
set_Value ("C_DocTypeDifference_ID", new Integer(C_DocTypeDifference_ID));
}
/** Get Difference Document.
Document type for generating in dispute Shipments */
public int getC_DocTypeDifference_ID() 
{
Integer ii = (Integer)get_Value("C_DocTypeDifference_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
set_ValueNoCheck ("C_DocType_ID", new Integer(C_DocType_ID));
}
/** Get Document Type.
Document type or rules */
public int getC_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPEINVOICE_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Document Type for Invoice.
Document type used for invoices generated from this sales document */
public void setC_DocTypeInvoice_ID (int C_DocTypeInvoice_ID)
{
if (C_DocTypeInvoice_ID <= 0) set_Value ("C_DocTypeInvoice_ID", null);
 else 
set_Value ("C_DocTypeInvoice_ID", new Integer(C_DocTypeInvoice_ID));
}
/** Get Document Type for Invoice.
Document type used for invoices generated from this sales document */
public int getC_DocTypeInvoice_ID() 
{
Integer ii = (Integer)get_Value("C_DocTypeInvoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPEPROFORMA_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Document Type for ProForma.
Document type used for pro forma invoices generated from this sales document */
public void setC_DocTypeProforma_ID (int C_DocTypeProforma_ID)
{
if (C_DocTypeProforma_ID <= 0) set_Value ("C_DocTypeProforma_ID", null);
 else 
set_Value ("C_DocTypeProforma_ID", new Integer(C_DocTypeProforma_ID));
}
/** Get Document Type for ProForma.
Document type used for pro forma invoices generated from this sales document */
public int getC_DocTypeProforma_ID() 
{
Integer ii = (Integer)get_Value("C_DocTypeProforma_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPESHIPMENT_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Document Type for Shipment.
Document type used for shipments generated from this sales document */
public void setC_DocTypeShipment_ID (int C_DocTypeShipment_ID)
{
if (C_DocTypeShipment_ID <= 0) set_Value ("C_DocTypeShipment_ID", null);
 else 
set_Value ("C_DocTypeShipment_ID", new Integer(C_DocTypeShipment_ID));
}
/** Get Document Type for Shipment.
Document type used for shipments generated from this sales document */
public int getC_DocTypeShipment_ID() 
{
Integer ii = (Integer)get_Value("C_DocTypeShipment_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_REVERSEDOCTYPE_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set C_ReverseDocType_ID */
public void setC_ReverseDocType_ID (int C_ReverseDocType_ID)
{
if (C_ReverseDocType_ID <= 0) set_Value ("C_ReverseDocType_ID", null);
 else 
set_Value ("C_ReverseDocType_ID", new Integer(C_ReverseDocType_ID));
}
/** Get C_ReverseDocType_ID */
public int getC_ReverseDocType_ID() 
{
Integer ii = (Integer)get_Value("C_ReverseDocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CAI Date */
public void setDateCAI (Timestamp DateCAI)
{
set_Value ("DateCAI", DateCAI);
}
/** Get CAI Date */
public Timestamp getDateCAI() 
{
return (Timestamp)get_Value("DateCAI");
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
public static final int DOCBASETYPE_AD_Reference_ID = MReference.getReferenceID("C_DocType DocBaseType");
/** Material Production = MMP */
public static final String DOCBASETYPE_MaterialProduction = "MMP";
/** Match Invoice = MXI */
public static final String DOCBASETYPE_MatchInvoice = "MXI";
/** Match PO = MXP */
public static final String DOCBASETYPE_MatchPO = "MXP";
/** AR Pro Forma Invoice = ARF */
public static final String DOCBASETYPE_ARProFormaInvoice = "ARF";
/** Material Delivery = MMS */
public static final String DOCBASETYPE_MaterialDelivery = "MMS";
/** Material Receipt = MMR */
public static final String DOCBASETYPE_MaterialReceipt = "MMR";
/** Material Movement = MMM */
public static final String DOCBASETYPE_MaterialMovement = "MMM";
/** Purchase Order = POO */
public static final String DOCBASETYPE_PurchaseOrder = "POO";
/** Purchase Requisition = POR */
public static final String DOCBASETYPE_PurchaseRequisition = "POR";
/** Material Physical Inventory = MMI */
public static final String DOCBASETYPE_MaterialPhysicalInventory = "MMI";
/** Manufacturing Order Receipt = MOR */
public static final String DOCBASETYPE_ManufacturingOrderReceipt = "MOR";
/** Manufacturing Order Use Variation  = MOU */
public static final String DOCBASETYPE_ManufacturingOrderUseVariation = "MOU";
/** Manufacturing Order Method Variation  = MOM */
public static final String DOCBASETYPE_ManufacturingOrderMethodVariation = "MOM";
/** Manufacturing Order Rate Variation  = MOV */
public static final String DOCBASETYPE_ManufacturingOrderRateVariation = "MOV";
/** Manufacturing Order = MOP */
public static final String DOCBASETYPE_ManufacturingOrder = "MOP";
/** Maintenance Order = MOF */
public static final String DOCBASETYPE_MaintenanceOrder = "MOF";
/** GL Journal = GLJ */
public static final String DOCBASETYPE_GLJournal = "GLJ";
/** GL Document = GLD */
public static final String DOCBASETYPE_GLDocument = "GLD";
/** AP Invoice = API */
public static final String DOCBASETYPE_APInvoice = "API";
/** AP Payment = APP */
public static final String DOCBASETYPE_APPayment = "APP";
/** AR Invoice = ARI */
public static final String DOCBASETYPE_ARInvoice = "ARI";
/** AR Receipt = ARR */
public static final String DOCBASETYPE_ARReceipt = "ARR";
/** Sales Order = SOO */
public static final String DOCBASETYPE_SalesOrder = "SOO";
/** Manufacturing Order Issue = MOI */
public static final String DOCBASETYPE_ManufacturingOrderIssue = "MOI";
/** Bank Statement = CMB */
public static final String DOCBASETYPE_BankStatement = "CMB";
/** Cash Journal = CMC */
public static final String DOCBASETYPE_CashJournal = "CMC";
/** Payment Allocation = CMA */
public static final String DOCBASETYPE_PaymentAllocation = "CMA";
/** AP Credit Memo = APC */
public static final String DOCBASETYPE_APCreditMemo = "APC";
/** AR Credit Memo = ARC */
public static final String DOCBASETYPE_ARCreditMemo = "ARC";
/** Project Issue = PJI */
public static final String DOCBASETYPE_ProjectIssue = "PJI";
/** Amortization = AMO */
public static final String DOCBASETYPE_Amortization = "AMO";
/** Set Document BaseType.
Logical type of document */
public void setDocBaseType (String DocBaseType)
{
if (DocBaseType.equals("MMP") || DocBaseType.equals("MXI") || DocBaseType.equals("MXP") || DocBaseType.equals("ARF") || DocBaseType.equals("MMS") || DocBaseType.equals("MMR") || DocBaseType.equals("MMM") || DocBaseType.equals("POO") || DocBaseType.equals("POR") || DocBaseType.equals("MMI") || DocBaseType.equals("MOR") || DocBaseType.equals("MOU") || DocBaseType.equals("MOM") || DocBaseType.equals("MOV") || DocBaseType.equals("MOP") || DocBaseType.equals("MOF") || DocBaseType.equals("GLJ") || DocBaseType.equals("GLD") || DocBaseType.equals("API") || DocBaseType.equals("APP") || DocBaseType.equals("ARI") || DocBaseType.equals("ARR") || DocBaseType.equals("SOO") || DocBaseType.equals("MOI") || DocBaseType.equals("CMB") || DocBaseType.equals("CMC") || DocBaseType.equals("CMA") || DocBaseType.equals("APC") || DocBaseType.equals("ARC") || DocBaseType.equals("PJI") || DocBaseType.equals("AMO"));
 else throw new IllegalArgumentException ("DocBaseType Invalid value - Reference = DOCBASETYPE_AD_Reference_ID - MMP - MXI - MXP - ARF - MMS - MMR - MMM - POO - POR - MMI - MOR - MOU - MOM - MOV - MOP - MOF - GLJ - GLD - API - APP - ARI - ARR - SOO - MOI - CMB - CMC - CMA - APC - ARC - PJI - AMO");
if (DocBaseType == null) throw new IllegalArgumentException ("DocBaseType is mandatory");
if (DocBaseType.length() > 3)
{
log.warning("Length > 3 - truncated");
DocBaseType = DocBaseType.substring(0,3);
}
set_Value ("DocBaseType", DocBaseType);
}
/** Get Document BaseType.
Logical type of document */
public String getDocBaseType() 
{
return (String)get_Value("DocBaseType");
}
public static final int DOCNOSEQUENCE_ID_AD_Reference_ID = MReference.getReferenceID("AD_Sequence for Documents");
/** Set Document Sequence.
Document sequence determines the numbering of documents */
public void setDocNoSequence_ID (int DocNoSequence_ID)
{
if (DocNoSequence_ID <= 0) set_Value ("DocNoSequence_ID", null);
 else 
set_Value ("DocNoSequence_ID", new Integer(DocNoSequence_ID));
}
/** Get Document Sequence.
Document sequence determines the numbering of documents */
public int getDocNoSequence_ID() 
{
Integer ii = (Integer)get_Value("DocNoSequence_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int DOCSUBTYPECAE_AD_Reference_ID = MReference.getReferenceID("DocSubTypeCae");
/** Facturas A = 01 */
public static final String DOCSUBTYPECAE_FacturasA = "01";
/** Notas de Debito A = 02 */
public static final String DOCSUBTYPECAE_NotasDeDebitoA = "02";
/** Notas de Credito A = 03 */
public static final String DOCSUBTYPECAE_NotasDeCreditoA = "03";
/** Recibos A = 04 */
public static final String DOCSUBTYPECAE_RecibosA = "04";
/** Notas de Venta al Contado A = 05 */
public static final String DOCSUBTYPECAE_NotasDeVentaAlContadoA = "05";
/** Facturas B = 06 */
public static final String DOCSUBTYPECAE_FacturasB = "06";
/** Notas de Debito B = 07 */
public static final String DOCSUBTYPECAE_NotasDeDebitoB = "07";
/** Notas de Credito B = 08 */
public static final String DOCSUBTYPECAE_NotasDeCreditoB = "08";
/** Recibos B = 09 */
public static final String DOCSUBTYPECAE_RecibosB = "09";
/** Notas de Venta al contado B = 10 */
public static final String DOCSUBTYPECAE_NotasDeVentaAlContadoB = "10";
/** Set docsubtypecae */
public void setdocsubtypecae (String docsubtypecae)
{
if (docsubtypecae == null || docsubtypecae.equals("01") || docsubtypecae.equals("02") || docsubtypecae.equals("03") || docsubtypecae.equals("04") || docsubtypecae.equals("05") || docsubtypecae.equals("06") || docsubtypecae.equals("07") || docsubtypecae.equals("08") || docsubtypecae.equals("09") || docsubtypecae.equals("10"));
 else throw new IllegalArgumentException ("docsubtypecae Invalid value - Reference = DOCSUBTYPECAE_AD_Reference_ID - 01 - 02 - 03 - 04 - 05 - 06 - 07 - 08 - 09 - 10");
if (docsubtypecae != null && docsubtypecae.length() > 2)
{
log.warning("Length > 2 - truncated");
docsubtypecae = docsubtypecae.substring(0,2);
}
set_Value ("docsubtypecae", docsubtypecae);
}
/** Get docsubtypecae */
public String getdocsubtypecae() 
{
return (String)get_Value("docsubtypecae");
}
public static final int DOCSUBTYPEINV_AD_Reference_ID = MReference.getReferenceID("DocSubTypeInv");
/** Electronico = EL */
public static final String DOCSUBTYPEINV_Electronico = "EL";
/** Impreso Fiscal = IF */
public static final String DOCSUBTYPEINV_ImpresoFiscal = "IF";
/** No Fiscal = NF */
public static final String DOCSUBTYPEINV_NoFiscal = "NF";
/** Fiscal = SF */
public static final String DOCSUBTYPEINV_Fiscal = "SF";
/** Set docsubtypeinv */
public void setdocsubtypeinv (String docsubtypeinv)
{
if (docsubtypeinv == null || docsubtypeinv.equals("EL") || docsubtypeinv.equals("IF") || docsubtypeinv.equals("NF") || docsubtypeinv.equals("SF"));
 else throw new IllegalArgumentException ("docsubtypeinv Invalid value - Reference = DOCSUBTYPEINV_AD_Reference_ID - EL - IF - NF - SF");
if (docsubtypeinv != null && docsubtypeinv.length() > 2)
{
log.warning("Length > 2 - truncated");
docsubtypeinv = docsubtypeinv.substring(0,2);
}
set_Value ("docsubtypeinv", docsubtypeinv);
}
/** Get docsubtypeinv */
public String getdocsubtypeinv() 
{
return (String)get_Value("docsubtypeinv");
}
public static final int DOCSUBTYPESO_AD_Reference_ID = MReference.getReferenceID("C_DocType SubTypeSO");
/** Warehouse Order = WP */
public static final String DOCSUBTYPESO_WarehouseOrder = "WP";
/** Return Material = RM */
public static final String DOCSUBTYPESO_ReturnMaterial = "RM";
/** On Credit Order = WI */
public static final String DOCSUBTYPESO_OnCreditOrder = "WI";
/** Prepay Order = PR */
public static final String DOCSUBTYPESO_PrepayOrder = "PR";
/** Quotation = OB */
public static final String DOCSUBTYPESO_Quotation = "OB";
/** POS Order = WR */
public static final String DOCSUBTYPESO_POSOrder = "WR";
/** Standard Order = SO */
public static final String DOCSUBTYPESO_StandardOrder = "SO";
/** Proposal = ON */
public static final String DOCSUBTYPESO_Proposal = "ON";
/** Tender = TR */
public static final String DOCSUBTYPESO_Tender = "TR";
/** Set SO Sub Type.
Sales Order Sub Type */
public void setDocSubTypeSO (String DocSubTypeSO)
{
if (DocSubTypeSO == null || DocSubTypeSO.equals("WP") || DocSubTypeSO.equals("RM") || DocSubTypeSO.equals("WI") || DocSubTypeSO.equals("PR") || DocSubTypeSO.equals("OB") || DocSubTypeSO.equals("WR") || DocSubTypeSO.equals("SO") || DocSubTypeSO.equals("ON") || DocSubTypeSO.equals("TR"));
 else throw new IllegalArgumentException ("DocSubTypeSO Invalid value - Reference = DOCSUBTYPESO_AD_Reference_ID - WP - RM - WI - PR - OB - WR - SO - ON - TR");
if (DocSubTypeSO != null && DocSubTypeSO.length() > 2)
{
log.warning("Length > 2 - truncated");
DocSubTypeSO = DocSubTypeSO.substring(0,2);
}
set_Value ("DocSubTypeSO", DocSubTypeSO);
}
/** Get SO Sub Type.
Sales Order Sub Type */
public String getDocSubTypeSO() 
{
return (String)get_Value("DocSubTypeSO");
}
/** Set Document Type Key.
Clave única de identificación del tipo de documento */
public void setDocTypeKey (String DocTypeKey)
{
if (DocTypeKey == null) throw new IllegalArgumentException ("DocTypeKey is mandatory");
if (DocTypeKey.length() > 40)
{
log.warning("Length > 40 - truncated");
DocTypeKey = DocTypeKey.substring(0,40);
}
set_ValueNoCheck ("DocTypeKey", DocTypeKey);
}
/** Get Document Type Key.
Clave única de identificación del tipo de documento */
public String getDocTypeKey() 
{
return (String)get_Value("DocTypeKey");
}
/** Set Document Copies.
Number of copies to be printed */
public void setDocumentCopies (int DocumentCopies)
{
set_Value ("DocumentCopies", new Integer(DocumentCopies));
}
/** Get Document Copies.
Number of copies to be printed */
public int getDocumentCopies() 
{
Integer ii = (Integer)get_Value("DocumentCopies");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Note.
Additional information for a Document */
public void setDocumentNote (String DocumentNote)
{
if (DocumentNote != null && DocumentNote.length() > 2000)
{
log.warning("Length > 2000 - truncated");
DocumentNote = DocumentNote.substring(0,2000);
}
set_Value ("DocumentNote", DocumentNote);
}
/** Get Document Note.
Additional information for a Document */
public String getDocumentNote() 
{
return (String)get_Value("DocumentNote");
}
/** Set Drag Order Document Discounts */
public void setDragOrderDocumentDiscounts (boolean DragOrderDocumentDiscounts)
{
set_Value ("DragOrderDocumentDiscounts", new Boolean(DragOrderDocumentDiscounts));
}
/** Get Drag Order Document Discounts */
public boolean isDragOrderDocumentDiscounts() 
{
Object oo = get_Value("DragOrderDocumentDiscounts");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Drag Order Line Discounts */
public void setDragOrderLineDiscounts (boolean DragOrderLineDiscounts)
{
set_Value ("DragOrderLineDiscounts", new Boolean(DragOrderLineDiscounts));
}
/** Get Drag Order Line Discounts */
public boolean isDragOrderLineDiscounts() 
{
Object oo = get_Value("DragOrderLineDiscounts");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Drag Order Price */
public void setDragOrderPrice (boolean DragOrderPrice)
{
set_Value ("DragOrderPrice", new Boolean(DragOrderPrice));
}
/** Get Drag Order Price */
public boolean isDragOrderPrice() 
{
Object oo = get_Value("DragOrderPrice");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Enabled In POS.
Document enable to use in POS */
public void setEnabledInPOS (boolean EnabledInPOS)
{
set_Value ("EnabledInPOS", new Boolean(EnabledInPOS));
}
/** Get Enabled In POS.
Document enable to use in POS */
public boolean isEnabledInPOS() 
{
Object oo = get_Value("EnabledInPOS");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Enable In Create From Shipment */
public void setEnableInCreateFromShipment (boolean EnableInCreateFromShipment)
{
set_Value ("EnableInCreateFromShipment", new Boolean(EnableInCreateFromShipment));
}
/** Get Enable In Create From Shipment */
public boolean isEnableInCreateFromShipment() 
{
Object oo = get_Value("EnableInCreateFromShipment");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int FISCALDOCUMENT_AD_Reference_ID = MReference.getReferenceID("Fiscal Document Type");
/** Invoice = I */
public static final String FISCALDOCUMENT_Invoice = "I";
/** Credit Note = C */
public static final String FISCALDOCUMENT_CreditNote = "C";
/** Debit Note = D */
public static final String FISCALDOCUMENT_DebitNote = "D";
/** Set Fiscal Document */
public void setFiscalDocument (String FiscalDocument)
{
if (FiscalDocument.equals("I") || FiscalDocument.equals("C") || FiscalDocument.equals("D"));
 else throw new IllegalArgumentException ("FiscalDocument Invalid value - Reference = FISCALDOCUMENT_AD_Reference_ID - I - C - D");
if (FiscalDocument == null) throw new IllegalArgumentException ("FiscalDocument is mandatory");
if (FiscalDocument.length() > 1)
{
log.warning("Length > 1 - truncated");
FiscalDocument = FiscalDocument.substring(0,1);
}
set_Value ("FiscalDocument", FiscalDocument);
}
/** Get Fiscal Document */
public String getFiscalDocument() 
{
return (String)get_Value("FiscalDocument");
}
/** Set GL Category.
General Ledger Category */
public void setGL_Category_ID (int GL_Category_ID)
{
set_Value ("GL_Category_ID", new Integer(GL_Category_ID));
}
/** Get GL Category.
General Ledger Category */
public int getGL_Category_ID() 
{
Integer ii = (Integer)get_Value("GL_Category_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Charges.
Charges can be added to the document */
public void setHasCharges (boolean HasCharges)
{
set_Value ("HasCharges", new Boolean(HasCharges));
}
/** Get Charges.
Charges can be added to the document */
public boolean isHasCharges() 
{
Object oo = get_Value("HasCharges");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Pro forma Invoice.
Indicates if Pro Forma Invoices can be generated from this document */
public void setHasProforma (boolean HasProforma)
{
set_Value ("HasProforma", new Boolean(HasProforma));
}
/** Get Pro forma Invoice.
Indicates if Pro Forma Invoices can be generated from this document */
public boolean isHasProforma() 
{
Object oo = get_Value("HasProforma");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Checkout Place Restricted.
Checkout Place Restricted */
public void setIsCheckoutPlaceRestricted (boolean IsCheckoutPlaceRestricted)
{
set_Value ("IsCheckoutPlaceRestricted", new Boolean(IsCheckoutPlaceRestricted));
}
/** Get Checkout Place Restricted.
Checkout Place Restricted */
public boolean isCheckoutPlaceRestricted() 
{
Object oo = get_Value("IsCheckoutPlaceRestricted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Create Counter Document.
Create Counter Document */
public void setIsCreateCounter (boolean IsCreateCounter)
{
set_Value ("IsCreateCounter", new Boolean(IsCreateCounter));
}
/** Get Create Counter Document.
Create Counter Document */
public boolean isCreateCounter() 
{
Object oo = get_Value("IsCreateCounter");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Default Counter Document.
The document type is the default counter document type */
public void setIsDefaultCounterDoc (boolean IsDefaultCounterDoc)
{
set_Value ("IsDefaultCounterDoc", new Boolean(IsDefaultCounterDoc));
}
/** Get Default Counter Document.
The document type is the default counter document type */
public boolean isDefaultCounterDoc() 
{
Object oo = get_Value("IsDefaultCounterDoc");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Document is Number Controlled.
The document has a document sequence */
public void setIsDocNoControlled (boolean IsDocNoControlled)
{
set_Value ("IsDocNoControlled", new Boolean(IsDocNoControlled));
}
/** Get Document is Number Controlled.
The document has a document sequence */
public boolean isDocNoControlled() 
{
Object oo = get_Value("IsDocNoControlled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set iselectronic */
public void setiselectronic (boolean iselectronic)
{
set_Value ("iselectronic", new Boolean(iselectronic));
}
/** Get iselectronic */
public boolean iselectronic() 
{
Object oo = get_Value("iselectronic");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Fiscal */
public void setIsFiscal (boolean IsFiscal)
{
set_Value ("IsFiscal", new Boolean(IsFiscal));
}
/** Get Is Fiscal */
public boolean isFiscal() 
{
Object oo = get_Value("IsFiscal");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Fiscal Document */
public void setIsFiscalDocument (boolean IsFiscalDocument)
{
set_Value ("IsFiscalDocument", new Boolean(IsFiscalDocument));
}
/** Get Fiscal Document */
public boolean isFiscalDocument() 
{
Object oo = get_Value("IsFiscalDocument");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set In Transit.
Movement is in transit */
public void setIsInTransit (boolean IsInTransit)
{
set_Value ("IsInTransit", new Boolean(IsInTransit));
}
/** Get In Transit.
Movement is in transit */
public boolean isInTransit() 
{
Object oo = get_Value("IsInTransit");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Payment Order Seq */
public void setIsPaymentOrderSeq (boolean IsPaymentOrderSeq)
{
set_Value ("IsPaymentOrderSeq", new Boolean(IsPaymentOrderSeq));
}
/** Get Is Payment Order Seq */
public boolean isPaymentOrderSeq() 
{
Object oo = get_Value("IsPaymentOrderSeq");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Pick/QA Confirmation.
Require Pick or QA Confirmation before processing */
public void setIsPickQAConfirm (boolean IsPickQAConfirm)
{
set_Value ("IsPickQAConfirm", new Boolean(IsPickQAConfirm));
}
/** Get Pick/QA Confirmation.
Require Pick or QA Confirmation before processing */
public boolean isPickQAConfirm() 
{
Object oo = get_Value("IsPickQAConfirm");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Print At Completing.
The document will be printed when is completed. */
public void setIsPrintAtCompleting (boolean IsPrintAtCompleting)
{
set_Value ("IsPrintAtCompleting", new Boolean(IsPrintAtCompleting));
}
/** Get Is Print At Completing.
The document will be printed when is completed. */
public boolean isPrintAtCompleting() 
{
Object oo = get_Value("IsPrintAtCompleting");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Print Preview */
public void setIsPrintPreview (boolean IsPrintPreview)
{
set_Value ("IsPrintPreview", new Boolean(IsPrintPreview));
}
/** Get Is Print Preview */
public boolean isPrintPreview() 
{
Object oo = get_Value("IsPrintPreview");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Receipt Seq */
public void setIsReceiptSeq (boolean IsReceiptSeq)
{
set_Value ("IsReceiptSeq", new Boolean(IsReceiptSeq));
}
/** Get Is Receipt Seq */
public boolean isReceiptSeq() 
{
Object oo = get_Value("IsReceiptSeq");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Ship/Receipt Confirmation.
Require Ship or Receipt Confirmation before processing */
public void setIsShipConfirm (boolean IsShipConfirm)
{
set_Value ("IsShipConfirm", new Boolean(IsShipConfirm));
}
/** Get Ship/Receipt Confirmation.
Require Ship or Receipt Confirmation before processing */
public boolean isShipConfirm() 
{
Object oo = get_Value("IsShipConfirm");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sales Transaction.
This is a Sales Transaction */
public void setIsSOTrx (boolean IsSOTrx)
{
set_Value ("IsSOTrx", new Boolean(IsSOTrx));
}
/** Get Sales Transaction.
This is a Sales Transaction */
public boolean isSOTrx() 
{
Object oo = get_Value("IsSOTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Split when Difference.
Split document when there is a difference */
public void setIsSplitWhenDifference (boolean IsSplitWhenDifference)
{
set_Value ("IsSplitWhenDifference", new Boolean(IsSplitWhenDifference));
}
/** Get Split when Difference.
Split document when there is a difference */
public boolean isSplitWhenDifference() 
{
Object oo = get_Value("IsSplitWhenDifference");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Lines Count Max */
public void setLinesCountMax (int LinesCountMax)
{
set_Value ("LinesCountMax", new Integer(LinesCountMax));
}
/** Get Lines Count Max */
public int getLinesCountMax() 
{
Integer ii = (Integer)get_Value("LinesCountMax");
if (ii == null) return 0;
return ii.intValue();
}
/** Set m_trannaturecodea_id */
public void setm_trannaturecodea_id (BigDecimal m_trannaturecodea_id)
{
set_Value ("m_trannaturecodea_id", m_trannaturecodea_id);
}
/** Get m_trannaturecodea_id */
public BigDecimal getm_trannaturecodea_id() 
{
BigDecimal bd = (BigDecimal)get_Value("m_trannaturecodea_id");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set m_trannaturecodeb_id */
public void setm_trannaturecodeb_id (BigDecimal m_trannaturecodeb_id)
{
set_Value ("m_trannaturecodeb_id", m_trannaturecodeb_id);
}
/** Get m_trannaturecodeb_id */
public BigDecimal getm_trannaturecodeb_id() 
{
BigDecimal bd = (BigDecimal)get_Value("m_trannaturecodeb_id");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set POS Enable Due.
Document enable in POS with due days */
public void setPOSEnableDue (boolean POSEnableDue)
{
set_Value ("POSEnableDue", new Boolean(POSEnableDue));
}
/** Get POS Enable Due.
Document enable in POS with due days */
public boolean isPOSEnableDue() 
{
Object oo = get_Value("POSEnableDue");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set POS Enable Due Days.
Enable Days after transaction date */
public void setPOSEnableDueDays (int POSEnableDueDays)
{
set_Value ("POSEnableDueDays", new Integer(POSEnableDueDays));
}
/** Get POS Enable Due Days.
Enable Days after transaction date */
public int getPOSEnableDueDays() 
{
Integer ii = (Integer)get_Value("POSEnableDueDays");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Printer Name.
Name of the Printer */
public void setPrinterName (String PrinterName)
{
if (PrinterName != null && PrinterName.length() > 60)
{
log.warning("Length > 60 - truncated");
PrinterName = PrinterName.substring(0,60);
}
set_Value ("PrinterName", PrinterName);
}
/** Get Printer Name.
Name of the Printer */
public String getPrinterName() 
{
return (String)get_Value("PrinterName");
}
/** Set Print Text.
The label text to be printed on a document or correspondence. */
public void setPrintName (String PrintName)
{
if (PrintName == null) throw new IllegalArgumentException ("PrintName is mandatory");
if (PrintName.length() > 60)
{
log.warning("Length > 60 - truncated");
PrintName = PrintName.substring(0,60);
}
set_Value ("PrintName", PrintName);
}
/** Get Print Text.
The label text to be printed on a document or correspondence. */
public String getPrintName() 
{
return (String)get_Value("PrintName");
}
public static final int SIGNO_ISSOTRX_AD_Reference_ID = MReference.getReferenceID("C_DocType_Signo_Issotrx");
/** -1 = -1 */
public static final String SIGNO_ISSOTRX__1 = "-1";
/** 1 = 1 */
public static final String SIGNO_ISSOTRX_1 = "1";
/** Set Signo IsSOTrx */
public void setsigno_issotrx (String signo_issotrx)
{
if (signo_issotrx.equals("-1") || signo_issotrx.equals("1"));
 else throw new IllegalArgumentException ("signo_issotrx Invalid value - Reference = SIGNO_ISSOTRX_AD_Reference_ID - -1 - 1");
if (signo_issotrx == null) throw new IllegalArgumentException ("signo_issotrx is mandatory");
if (signo_issotrx.length() > 4)
{
log.warning("Length > 4 - truncated");
signo_issotrx = signo_issotrx.substring(0,4);
}
set_Value ("signo_issotrx", signo_issotrx);
}
/** Get Signo IsSOTrx */
public String getsigno_issotrx() 
{
return (String)get_Value("signo_issotrx");
}
}
