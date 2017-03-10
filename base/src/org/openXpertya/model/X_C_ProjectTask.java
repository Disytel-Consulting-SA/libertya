/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_ProjectTask
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-03-10 10:07:15.266 */
public class X_C_ProjectTask extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_ProjectTask (Properties ctx, int C_ProjectTask_ID, String trxName)
{
super (ctx, C_ProjectTask_ID, trxName);
/** if (C_ProjectTask_ID == 0)
{
setC_ProjectPhase_ID (0);
setC_ProjectTask_ID (0);
setName (null);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM C_ProjectTask WHERE C_ProjectPhase_ID=@C_ProjectPhase_ID@
}
 */
}
/** Load Constructor */
public X_C_ProjectTask (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_ProjectTask");

/** TableName=C_ProjectTask */
public static final String Table_Name="C_ProjectTask";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_ProjectTask");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ProjectTask[").append(getID()).append("]");
return sb.toString();
}
/** Set Project Phase.
Phase of a Project */
public void setC_ProjectPhase_ID (int C_ProjectPhase_ID)
{
set_ValueNoCheck ("C_ProjectPhase_ID", new Integer(C_ProjectPhase_ID));
}
/** Get Project Phase.
Phase of a Project */
public int getC_ProjectPhase_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectPhase_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project Task.
Actual Project Task in a Phase */
public void setC_ProjectTask_ID (int C_ProjectTask_ID)
{
set_ValueNoCheck ("C_ProjectTask_ID", new Integer(C_ProjectTask_ID));
}
/** Get Project Task.
Actual Project Task in a Phase */
public int getC_ProjectTask_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectTask_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CREATEDOCUMENT_AD_Reference_ID = MReference.getReferenceID("C_DocType DocBaseType");
/** Material Production = MMP */
public static final String CREATEDOCUMENT_MaterialProduction = "MMP";
/** Match Invoice = MXI */
public static final String CREATEDOCUMENT_MatchInvoice = "MXI";
/** Match PO = MXP */
public static final String CREATEDOCUMENT_MatchPO = "MXP";
/** AR Pro Forma Invoice = ARF */
public static final String CREATEDOCUMENT_ARProFormaInvoice = "ARF";
/** Material Delivery = MMS */
public static final String CREATEDOCUMENT_MaterialDelivery = "MMS";
/** Material Receipt = MMR */
public static final String CREATEDOCUMENT_MaterialReceipt = "MMR";
/** Material Movement = MMM */
public static final String CREATEDOCUMENT_MaterialMovement = "MMM";
/** Purchase Order = POO */
public static final String CREATEDOCUMENT_PurchaseOrder = "POO";
/** Purchase Requisition = POR */
public static final String CREATEDOCUMENT_PurchaseRequisition = "POR";
/** Material Physical Inventory = MMI */
public static final String CREATEDOCUMENT_MaterialPhysicalInventory = "MMI";
/** Manufacturing Order Receipt = MOR */
public static final String CREATEDOCUMENT_ManufacturingOrderReceipt = "MOR";
/** Manufacturing Order Use Variation  = MOU */
public static final String CREATEDOCUMENT_ManufacturingOrderUseVariation = "MOU";
/** Manufacturing Order Method Variation  = MOM */
public static final String CREATEDOCUMENT_ManufacturingOrderMethodVariation = "MOM";
/** Manufacturing Order Rate Variation  = MOV */
public static final String CREATEDOCUMENT_ManufacturingOrderRateVariation = "MOV";
/** Manufacturing Order = MOP */
public static final String CREATEDOCUMENT_ManufacturingOrder = "MOP";
/** Maintenance Order = MOF */
public static final String CREATEDOCUMENT_MaintenanceOrder = "MOF";
/** GL Journal = GLJ */
public static final String CREATEDOCUMENT_GLJournal = "GLJ";
/** GL Document = GLD */
public static final String CREATEDOCUMENT_GLDocument = "GLD";
/** AP Invoice = API */
public static final String CREATEDOCUMENT_APInvoice = "API";
/** AP Payment = APP */
public static final String CREATEDOCUMENT_APPayment = "APP";
/** AR Invoice = ARI */
public static final String CREATEDOCUMENT_ARInvoice = "ARI";
/** AR Receipt = ARR */
public static final String CREATEDOCUMENT_ARReceipt = "ARR";
/** Sales Order = SOO */
public static final String CREATEDOCUMENT_SalesOrder = "SOO";
/** Manufacturing Order Issue = MOI */
public static final String CREATEDOCUMENT_ManufacturingOrderIssue = "MOI";
/** Bank Statement = CMB */
public static final String CREATEDOCUMENT_BankStatement = "CMB";
/** Cash Journal = CMC */
public static final String CREATEDOCUMENT_CashJournal = "CMC";
/** Payment Allocation = CMA */
public static final String CREATEDOCUMENT_PaymentAllocation = "CMA";
/** AP Credit Memo = APC */
public static final String CREATEDOCUMENT_APCreditMemo = "APC";
/** AR Credit Memo = ARC */
public static final String CREATEDOCUMENT_ARCreditMemo = "ARC";
/** Project Issue = PJI */
public static final String CREATEDOCUMENT_ProjectIssue = "PJI";
/** Amortization = AMO */
public static final String CREATEDOCUMENT_Amortization = "AMO";
/** Bank List = BLB */
public static final String CREATEDOCUMENT_BankList = "BLB";
/** Check Printing = CHP */
public static final String CREATEDOCUMENT_CheckPrinting = "CHP";
/** AP Payment Batch = APB */
public static final String CREATEDOCUMENT_APPaymentBatch = "APB";
/** Set CreateDocument */
public void setCreateDocument (String CreateDocument)
{
if (CreateDocument == null || CreateDocument.equals("MMP") || CreateDocument.equals("MXI") || CreateDocument.equals("MXP") || CreateDocument.equals("ARF") || CreateDocument.equals("MMS") || CreateDocument.equals("MMR") || CreateDocument.equals("MMM") || CreateDocument.equals("POO") || CreateDocument.equals("POR") || CreateDocument.equals("MMI") || CreateDocument.equals("MOR") || CreateDocument.equals("MOU") || CreateDocument.equals("MOM") || CreateDocument.equals("MOV") || CreateDocument.equals("MOP") || CreateDocument.equals("MOF") || CreateDocument.equals("GLJ") || CreateDocument.equals("GLD") || CreateDocument.equals("API") || CreateDocument.equals("APP") || CreateDocument.equals("ARI") || CreateDocument.equals("ARR") || CreateDocument.equals("SOO") || CreateDocument.equals("MOI") || CreateDocument.equals("CMB") || CreateDocument.equals("CMC") || CreateDocument.equals("CMA") || CreateDocument.equals("APC") || CreateDocument.equals("ARC") || CreateDocument.equals("PJI") || CreateDocument.equals("AMO") || CreateDocument.equals("BLB") || CreateDocument.equals("CHP") || CreateDocument.equals("APB") || ( refContainsValue("CORE-AD_Reference-183", CreateDocument) ) );
 else throw new IllegalArgumentException ("CreateDocument Invalid value: " + CreateDocument + ".  Valid: " +  refValidOptions("CORE-AD_Reference-183") );
if (CreateDocument != null && CreateDocument.length() > 1)
{
log.warning("Length > 1 - truncated");
CreateDocument = CreateDocument.substring(0,1);
}
set_Value ("CreateDocument", CreateDocument);
}
/** Get CreateDocument */
public String getCreateDocument() 
{
return (String)get_Value("CreateDocument");
}
/** Set Standard Task.
Standard Project Type Task */
public void setC_Task_ID (int C_Task_ID)
{
if (C_Task_ID <= 0) set_ValueNoCheck ("C_Task_ID", null);
 else 
set_ValueNoCheck ("C_Task_ID", new Integer(C_Task_ID));
}
/** Get Standard Task.
Standard Project Type Task */
public int getC_Task_ID() 
{
Integer ii = (Integer)get_Value("C_Task_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,2000);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
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
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
set_Value ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getSeqNo()));
}
}
