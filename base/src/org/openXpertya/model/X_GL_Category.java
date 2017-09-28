/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por GL_Category
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-09-26 10:30:26.989 */
public class X_GL_Category extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_GL_Category (Properties ctx, int GL_Category_ID, String trxName)
{
super (ctx, GL_Category_ID, trxName);
/** if (GL_Category_ID == 0)
{
setCategoryType (null);	// M
setGL_Category_ID (0);
setIsDefault (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_GL_Category (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("GL_Category");

/** TableName=GL_Category */
public static final String Table_Name="GL_Category";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"GL_Category");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_GL_Category[").append(getID()).append("]");
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
public static final int CATEGORYTYPE_AD_Reference_ID = MReference.getReferenceID("GL Category Type");
/** Manual = M */
public static final String CATEGORYTYPE_Manual = "M";
/** Import = I */
public static final String CATEGORYTYPE_Import = "I";
/** Document = D */
public static final String CATEGORYTYPE_Document = "D";
/** Set Category Type.
Source of the Journal with this category */
public void setCategoryType (String CategoryType)
{
if (CategoryType.equals("M") || CategoryType.equals("I") || CategoryType.equals("D") || ( refContainsValue("CORE-AD_Reference-207", CategoryType) ) );
 else throw new IllegalArgumentException ("CategoryType Invalid value: " + CategoryType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-207") );
if (CategoryType == null) throw new IllegalArgumentException ("CategoryType is mandatory");
if (CategoryType.length() > 1)
{
log.warning("Length > 1 - truncated");
CategoryType = CategoryType.substring(0,1);
}
set_Value ("CategoryType", CategoryType);
}
/** Get Category Type.
Source of the Journal with this category */
public String getCategoryType() 
{
return (String)get_Value("CategoryType");
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
/** Bank List = BLB */
public static final String DOCBASETYPE_BankList = "BLB";
/** Check Printing = CHP */
public static final String DOCBASETYPE_CheckPrinting = "CHP";
/** AP Payment Batch = APB */
public static final String DOCBASETYPE_APPaymentBatch = "APB";
/** Credit Card Settlement = CCS */
public static final String DOCBASETYPE_CreditCardSettlement = "CCS";
/** Set Document BaseType.
Logical type of document */
public void setDocBaseType (String DocBaseType)
{
if (DocBaseType == null || DocBaseType.equals("MMP") || DocBaseType.equals("MXI") || DocBaseType.equals("MXP") || DocBaseType.equals("ARF") || DocBaseType.equals("MMS") || DocBaseType.equals("MMR") || DocBaseType.equals("MMM") || DocBaseType.equals("POO") || DocBaseType.equals("POR") || DocBaseType.equals("MMI") || DocBaseType.equals("MOR") || DocBaseType.equals("MOU") || DocBaseType.equals("MOM") || DocBaseType.equals("MOV") || DocBaseType.equals("MOP") || DocBaseType.equals("MOF") || DocBaseType.equals("GLJ") || DocBaseType.equals("GLD") || DocBaseType.equals("API") || DocBaseType.equals("APP") || DocBaseType.equals("ARI") || DocBaseType.equals("ARR") || DocBaseType.equals("SOO") || DocBaseType.equals("MOI") || DocBaseType.equals("CMB") || DocBaseType.equals("CMC") || DocBaseType.equals("CMA") || DocBaseType.equals("APC") || DocBaseType.equals("ARC") || DocBaseType.equals("PJI") || DocBaseType.equals("AMO") || DocBaseType.equals("BLB") || DocBaseType.equals("CHP") || DocBaseType.equals("APB") || DocBaseType.equals("CCS") || ( refContainsValue("CORE-AD_Reference-183", DocBaseType) ) );
 else throw new IllegalArgumentException ("DocBaseType Invalid value: " + DocBaseType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-183") );
if (DocBaseType != null && DocBaseType.length() > 3)
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
/** Set GL Category.
General Ledger Category */
public void setGL_Category_ID (int GL_Category_ID)
{
set_ValueNoCheck ("GL_Category_ID", new Integer(GL_Category_ID));
}
/** Get GL Category.
General Ledger Category */
public int getGL_Category_ID() 
{
Integer ii = (Integer)get_Value("GL_Category_ID");
if (ii == null) return 0;
return ii.intValue();
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
}
