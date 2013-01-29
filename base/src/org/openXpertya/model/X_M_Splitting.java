/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Splitting
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-11-30 17:35:54.317 */
public class X_M_Splitting extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Splitting (Properties ctx, int M_Splitting_ID, String trxName)
{
super (ctx, M_Splitting_ID, trxName);
/** if (M_Splitting_ID == 0)
{
setC_Conversion_UOM_ID (0);
setC_UOM_ID (0);
setDateTrx (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDocAction (null);	// CO
setDocStatus (null);	// DR
setDocumentNo (null);
setM_Locator_ID (0);
setM_Product_ID (0);
setM_Splitting_ID (0);
setM_Warehouse_ID (0);
setProcessed (false);
setProductQty (Env.ZERO);
setSplitQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_Splitting (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Splitting");

/** TableName=M_Splitting */
public static final String Table_Name="M_Splitting";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Splitting");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Splitting[").append(getID()).append("]");
return sb.toString();
}
public static final int C_CONVERSION_UOM_ID_AD_Reference_ID = MReference.getReferenceID("C_UOM");
/** Set Conversion UOM.
Conversion UOM */
public void setC_Conversion_UOM_ID (int C_Conversion_UOM_ID)
{
set_Value ("C_Conversion_UOM_ID", new Integer(C_Conversion_UOM_ID));
}
/** Get Conversion UOM.
Conversion UOM */
public int getC_Conversion_UOM_ID() 
{
Integer ii = (Integer)get_Value("C_Conversion_UOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Comments.
Comments or additional information */
public void setComments (String Comments)
{
if (Comments != null && Comments.length() > 255)
{
log.warning("Length > 255 - truncated");
Comments = Comments.substring(0,255);
}
set_Value ("Comments", Comments);
}
/** Get Comments.
Comments or additional information */
public String getComments() 
{
return (String)get_Value("Comments");
}
/** Set Converted Product Quantity.
Converted Product Quantity */
public void setConvertedProductQty (BigDecimal ConvertedProductQty)
{
set_Value ("ConvertedProductQty", ConvertedProductQty);
}
/** Get Converted Product Quantity.
Converted Product Quantity */
public BigDecimal getConvertedProductQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ConvertedProductQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Converted Shrink Quantity.
Converted Shrink Quantity */
public void setConvertedShrinkQty (BigDecimal ConvertedShrinkQty)
{
set_Value ("ConvertedShrinkQty", ConvertedShrinkQty);
}
/** Get Converted Shrink Quantity.
Converted Shrink Quantity */
public BigDecimal getConvertedShrinkQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ConvertedShrinkQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Converted Split Quantity.
Converted Split Quantity */
public void setConvertedSplitQty (BigDecimal ConvertedSplitQty)
{
set_Value ("ConvertedSplitQty", ConvertedSplitQty);
}
/** Get Converted Split Quantity.
Converted Split Quantity */
public BigDecimal getConvertedSplitQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ConvertedSplitQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
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
/** Set Transaction Date.
Transaction Date */
public void setDateTrx (Timestamp DateTrx)
{
if (DateTrx == null) throw new IllegalArgumentException ("DateTrx is mandatory");
set_Value ("DateTrx", DateTrx);
}
/** Get Transaction Date.
Transaction Date */
public Timestamp getDateTrx() 
{
return (Timestamp)get_Value("DateTrx");
}
public static final int DOCACTION_AD_Reference_ID = MReference.getReferenceID("_Document Action");
/** Approve = AP */
public static final String DOCACTION_Approve = "AP";
/** Close = CL */
public static final String DOCACTION_Close = "CL";
/** Prepare = PR */
public static final String DOCACTION_Prepare = "PR";
/** Invalidate = IN */
public static final String DOCACTION_Invalidate = "IN";
/** Complete = CO */
public static final String DOCACTION_Complete = "CO";
/** <None> = -- */
public static final String DOCACTION_None = "--";
/** Reverse - Correct = RC */
public static final String DOCACTION_Reverse_Correct = "RC";
/** Reject = RJ */
public static final String DOCACTION_Reject = "RJ";
/** Reverse - Accrual = RA */
public static final String DOCACTION_Reverse_Accrual = "RA";
/** Wait Complete = WC */
public static final String DOCACTION_WaitComplete = "WC";
/** Unlock = XL */
public static final String DOCACTION_Unlock = "XL";
/** Re-activate = RE */
public static final String DOCACTION_Re_Activate = "RE";
/** Post = PO */
public static final String DOCACTION_Post = "PO";
/** Void = VO */
public static final String DOCACTION_Void = "VO";
/** Set Document Action.
The targeted status of the document */
public void setDocAction (String DocAction)
{
if (DocAction.equals("AP") || DocAction.equals("CL") || DocAction.equals("PR") || DocAction.equals("IN") || DocAction.equals("CO") || DocAction.equals("--") || DocAction.equals("RC") || DocAction.equals("RJ") || DocAction.equals("RA") || DocAction.equals("WC") || DocAction.equals("XL") || DocAction.equals("RE") || DocAction.equals("PO") || DocAction.equals("VO"));
 else throw new IllegalArgumentException ("DocAction Invalid value - Reference = DOCACTION_AD_Reference_ID - AP - CL - PR - IN - CO - -- - RC - RJ - RA - WC - XL - RE - PO - VO");
if (DocAction == null) throw new IllegalArgumentException ("DocAction is mandatory");
if (DocAction.length() > 2)
{
log.warning("Length > 2 - truncated");
DocAction = DocAction.substring(0,2);
}
set_Value ("DocAction", DocAction);
}
/** Get Document Action.
The targeted status of the document */
public String getDocAction() 
{
return (String)get_Value("DocAction");
}
public static final int DOCSTATUS_AD_Reference_ID = MReference.getReferenceID("_Document Status");
/** Voided = VO */
public static final String DOCSTATUS_Voided = "VO";
/** Not Approved = NA */
public static final String DOCSTATUS_NotApproved = "NA";
/** In Progress = IP */
public static final String DOCSTATUS_InProgress = "IP";
/** Completed = CO */
public static final String DOCSTATUS_Completed = "CO";
/** Approved = AP */
public static final String DOCSTATUS_Approved = "AP";
/** Closed = CL */
public static final String DOCSTATUS_Closed = "CL";
/** Waiting Confirmation = WC */
public static final String DOCSTATUS_WaitingConfirmation = "WC";
/** Waiting Payment = WP */
public static final String DOCSTATUS_WaitingPayment = "WP";
/** Unknown = ?? */
public static final String DOCSTATUS_Unknown = "??";
/** Drafted = DR */
public static final String DOCSTATUS_Drafted = "DR";
/** Invalid = IN */
public static final String DOCSTATUS_Invalid = "IN";
/** Reversed = RE */
public static final String DOCSTATUS_Reversed = "RE";
/** Set Document Status.
The current status of the document */
public void setDocStatus (String DocStatus)
{
if (DocStatus.equals("VO") || DocStatus.equals("NA") || DocStatus.equals("IP") || DocStatus.equals("CO") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("WC") || DocStatus.equals("WP") || DocStatus.equals("??") || DocStatus.equals("DR") || DocStatus.equals("IN") || DocStatus.equals("RE"));
 else throw new IllegalArgumentException ("DocStatus Invalid value - Reference = DOCSTATUS_AD_Reference_ID - VO - NA - IP - CO - AP - CL - WC - WP - ?? - DR - IN - RE");
if (DocStatus == null) throw new IllegalArgumentException ("DocStatus is mandatory");
if (DocStatus.length() > 2)
{
log.warning("Length > 2 - truncated");
DocStatus = DocStatus.substring(0,2);
}
set_Value ("DocStatus", DocStatus);
}
/** Get Document Status.
The current status of the document */
public String getDocStatus() 
{
return (String)get_Value("DocStatus");
}
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo == null) throw new IllegalArgumentException ("DocumentNo is mandatory");
if (DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,30);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDocumentNo());
}
/** Set Phys.Inventory.
Parameters for a Physical Inventory */
public void setM_Inventory_ID (int M_Inventory_ID)
{
if (M_Inventory_ID <= 0) set_Value ("M_Inventory_ID", null);
 else 
set_Value ("M_Inventory_ID", new Integer(M_Inventory_ID));
}
/** Get Phys.Inventory.
Parameters for a Physical Inventory */
public int getM_Inventory_ID() 
{
Integer ii = (Integer)get_Value("M_Inventory_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Locator.
Warehouse Locator */
public void setM_Locator_ID (int M_Locator_ID)
{
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
/** Set Splitting.
Product Splitting */
public void setM_Splitting_ID (int M_Splitting_ID)
{
set_ValueNoCheck ("M_Splitting_ID", new Integer(M_Splitting_ID));
}
/** Get Splitting.
Product Splitting */
public int getM_Splitting_ID() 
{
Integer ii = (Integer)get_Value("M_Splitting_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_Value ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
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
/** Set Product Quantity.
Product Quantity */
public void setProductQty (BigDecimal ProductQty)
{
if (ProductQty == null) throw new IllegalArgumentException ("ProductQty is mandatory");
set_Value ("ProductQty", ProductQty);
}
/** Get Product Quantity.
Product Quantity */
public BigDecimal getProductQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ProductQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Shrink Quantity.
Shrink Quantity */
public void setShrinkQty (BigDecimal ShrinkQty)
{
set_Value ("ShrinkQty", ShrinkQty);
}
/** Get Shrink Quantity.
Shrink Quantity */
public BigDecimal getShrinkQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ShrinkQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Quantity Splitted.
Quantity Splitted */
public void setSplitQty (BigDecimal SplitQty)
{
if (SplitQty == null) throw new IllegalArgumentException ("SplitQty is mandatory");
set_Value ("SplitQty", SplitQty);
}
/** Get Quantity Splitted.
Quantity Splitted */
public BigDecimal getSplitQty() 
{
BigDecimal bd = (BigDecimal)get_Value("SplitQty");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int VOID_INVENTORY_ID_AD_Reference_ID = MReference.getReferenceID("M_Inventory");
/** Set Splitting Void Inventory.
Splitting Void Inventory */
public void setVoid_Inventory_ID (int Void_Inventory_ID)
{
if (Void_Inventory_ID <= 0) set_Value ("Void_Inventory_ID", null);
 else 
set_Value ("Void_Inventory_ID", new Integer(Void_Inventory_ID));
}
/** Get Splitting Void Inventory.
Splitting Void Inventory */
public int getVoid_Inventory_ID() 
{
Integer ii = (Integer)get_Value("Void_Inventory_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
