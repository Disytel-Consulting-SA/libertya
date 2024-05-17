/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import org.openXpertya.model.*;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Jacofer_RoadMap
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2020-01-09 16:44:51.261 */
public class LP_M_Jacofer_RoadMap extends org.openXpertya.model.PO
{
/** Constructor estándar */
public LP_M_Jacofer_RoadMap (Properties ctx, int M_Jacofer_RoadMap_ID, String trxName)
{
super (ctx, M_Jacofer_RoadMap_ID, trxName);
/** if (M_Jacofer_RoadMap_ID == 0)
{
setDocAction (null);	// CO
setDocStatus (null);	// DR
setDocumentNo (null);
setM_Jacofer_RoadMap_ID (0);
setMovementDate (new Timestamp(System.currentTimeMillis()));	// @#Date@
setProcessed (false);
setTotal_Capacity (Env.ZERO);
setTotal_PackagesQty (Env.ZERO);
setTotal_Weight (Env.ZERO);
}
 */
}
/** Load Constructor */
public LP_M_Jacofer_RoadMap (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Jacofer_RoadMap");

/** TableName=M_Jacofer_RoadMap */
public static final String Table_Name="M_Jacofer_RoadMap";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Jacofer_RoadMap");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("LP_M_Jacofer_RoadMap[").append(getID()).append("]");
return sb.toString();
}
/** Set Add In Outs */
public void setAddInOuts (String AddInOuts)
{
if (AddInOuts != null && AddInOuts.length() > 1)
{
log.warning("Length > 1 - truncated");
AddInOuts = AddInOuts.substring(0,1);
}
set_Value ("AddInOuts", AddInOuts);
}
/** Get Add In Outs */
public String getAddInOuts() 
{
return (String)get_Value("AddInOuts");
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
if (DocAction.equals("AP") || DocAction.equals("CL") || DocAction.equals("PR") || DocAction.equals("IN") || DocAction.equals("CO") || DocAction.equals("--") || DocAction.equals("RC") || DocAction.equals("RJ") || DocAction.equals("RA") || DocAction.equals("WC") || DocAction.equals("XL") || DocAction.equals("RE") || DocAction.equals("PO") || DocAction.equals("VO") || ( refContainsValue("CORE-AD_Reference-135", DocAction) ) );
 else throw new IllegalArgumentException ("DocAction Invalid value: " + DocAction + ".  Valid: " +  refValidOptions("CORE-AD_Reference-135") );
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
if (DocStatus.equals("VO") || DocStatus.equals("NA") || DocStatus.equals("IP") || DocStatus.equals("CO") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("WC") || DocStatus.equals("WP") || DocStatus.equals("??") || DocStatus.equals("DR") || DocStatus.equals("IN") || DocStatus.equals("RE") || ( refContainsValue("CORE-AD_Reference-131", DocStatus) ) );
 else throw new IllegalArgumentException ("DocStatus Invalid value: " + DocStatus + ".  Valid: " +  refValidOptions("CORE-AD_Reference-131") );
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
/** Set M_Jacofer_RoadMap_ID */
public void setM_Jacofer_RoadMap_ID (int M_Jacofer_RoadMap_ID)
{
set_ValueNoCheck ("M_Jacofer_RoadMap_ID", new Integer(M_Jacofer_RoadMap_ID));
}
/** Get M_Jacofer_RoadMap_ID */
public int getM_Jacofer_RoadMap_ID() 
{
Integer ii = (Integer)get_Value("M_Jacofer_RoadMap_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Movement Date.
Date a product was moved in or out of inventory */
public void setMovementDate (Timestamp MovementDate)
{
if (MovementDate == null) throw new IllegalArgumentException ("MovementDate is mandatory");
set_Value ("MovementDate", MovementDate);
}
/** Get Movement Date.
Date a product was moved in or out of inventory */
public Timestamp getMovementDate() 
{
return (Timestamp)get_Value("MovementDate");
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
/** Set Aforo */
public void setTotal_Capacity (BigDecimal Total_Capacity)
{
if (Total_Capacity == null) throw new IllegalArgumentException ("Total_Capacity is mandatory");
set_Value ("Total_Capacity", Total_Capacity);
}
/** Get Aforo */
public BigDecimal getTotal_Capacity() 
{
BigDecimal bd = (BigDecimal)get_Value("Total_Capacity");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cantidad de Bultos */
public void setTotal_PackagesQty (BigDecimal Total_PackagesQty)
{
if (Total_PackagesQty == null) throw new IllegalArgumentException ("Total_PackagesQty is mandatory");
set_Value ("Total_PackagesQty", Total_PackagesQty);
}
/** Get Cantidad de Bultos */
public BigDecimal getTotal_PackagesQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Total_PackagesQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Peso */
public void setTotal_Weight (BigDecimal Total_Weight)
{
if (Total_Weight == null) throw new IllegalArgumentException ("Total_Weight is mandatory");
set_Value ("Total_Weight", Total_Weight);
}
/** Get Peso */
public BigDecimal getTotal_Weight() 
{
BigDecimal bd = (BigDecimal)get_Value("Total_Weight");
if (bd == null) return Env.ZERO;
return bd;
}
}
