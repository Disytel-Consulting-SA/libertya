/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Order_BOMLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.953 */
public class X_MPC_Order_BOMLine extends PO
{
/** Constructor estándar */
public X_MPC_Order_BOMLine (Properties ctx, int MPC_Order_BOMLine_ID, String trxName)
{
super (ctx, MPC_Order_BOMLine_ID, trxName);
/** if (MPC_Order_BOMLine_ID == 0)
{
setC_UOM_ID (0);
setIsCritical (false);
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM MPC_Order_BOMLine WHERE MPC_Order_BOM_ID=@MPC_Order_BOM_ID@
setMPC_Order_BOMLine_ID (0);
setMPC_Order_BOM_ID (0);
setMPC_Order_ID (0);
setM_Product_ID (0);
setM_Warehouse_ID (0);
setQtyBOM (Env.ZERO);
setQtyBatch (Env.ZERO);
setQtyDelivered (Env.ZERO);
setQtyPost (Env.ZERO);
setQtyReject (Env.ZERO);
setQtyRequiered (Env.ZERO);
setQtyReserved (Env.ZERO);
setQtyScrap (Env.ZERO);
setValidFrom (new Timestamp(System.currentTimeMillis()));	// @#Date@
}
 */
}
/** Load Constructor */
public X_MPC_Order_BOMLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000019 */
public static final int Table_ID=1000019;

/** TableName=MPC_Order_BOMLine */
public static final String Table_Name="MPC_Order_BOMLine";

protected static KeyNamePair Model = new KeyNamePair(1000019,"MPC_Order_BOMLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Order_BOMLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Assay */
public void setAssay (BigDecimal Assay)
{
set_ValueNoCheck ("Assay", Assay);
}
/** Get Assay */
public BigDecimal getAssay() 
{
BigDecimal bd = (BigDecimal)get_Value("Assay");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set BackflushGroup */
public void setBackflushGroup (String BackflushGroup)
{
if (BackflushGroup != null && BackflushGroup.length() > 30)
{
log.warning("Length > 30 - truncated");
BackflushGroup = BackflushGroup.substring(0,29);
}
set_ValueNoCheck ("BackflushGroup", BackflushGroup);
}
/** Get BackflushGroup */
public String getBackflushGroup() 
{
return (String)get_Value("BackflushGroup");
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
set_ValueNoCheck ("C_UOM_ID", new Integer(C_UOM_ID));
}
/** Get UOM.
Unit of Measure */
public int getC_UOM_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int COMPONENTTYPE_AD_Reference_ID=1000037;
/** Component = CO */
public static final String COMPONENTTYPE_Component = "CO";
/** Packing = PK */
public static final String COMPONENTTYPE_Packing = "PK";
/** Tools = TL */
public static final String COMPONENTTYPE_Tools = "TL";
/** Planning = PL */
public static final String COMPONENTTYPE_Planning = "PL";
/** Phantom = PH */
public static final String COMPONENTTYPE_Phantom = "PH";
/** By Product = BY */
public static final String COMPONENTTYPE_ByProduct = "BY";
/** Set Component Type */
public void setComponentType (String ComponentType)
{
if (ComponentType == null || ComponentType.equals("CO") || ComponentType.equals("PK") || ComponentType.equals("TL") || ComponentType.equals("PL") || ComponentType.equals("PH") || ComponentType.equals("BY"));
 else throw new IllegalArgumentException ("ComponentType Invalid value - Reference_ID=1000037 - CO - PK - TL - PL - PH - BY");
if (ComponentType != null && ComponentType.length() > 2)
{
log.warning("Length > 2 - truncated");
ComponentType = ComponentType.substring(0,1);
}
set_Value ("ComponentType", ComponentType);
}
/** Get Component Type */
public String getComponentType() 
{
return (String)get_Value("ComponentType");
}
/** Set Date Delivered.
Date when the product was delivered */
public void setDateDelivered (Timestamp DateDelivered)
{
set_Value ("DateDelivered", DateDelivered);
}
/** Get Date Delivered.
Date when the product was delivered */
public Timestamp getDateDelivered() 
{
return (Timestamp)get_Value("DateDelivered");
}
public static final int DELIVERTO_AD_Reference_ID=286;
/** Set Deliver To.
Person or department  to whom  the products nust be given. */
public void setDeliverTo (int DeliverTo)
{
set_Value ("DeliverTo", new Integer(DeliverTo));
}
/** Get Deliver To.
Person or department  to whom  the products nust be given. */
public int getDeliverTo() 
{
Integer ii = (Integer)get_Value("DeliverTo");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 510)
{
log.warning("Length > 510 - truncated");
Description = Description.substring(0,509);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Forecast */
public void setForecast (BigDecimal Forecast)
{
set_ValueNoCheck ("Forecast", Forecast);
}
/** Get Forecast */
public BigDecimal getForecast() 
{
BigDecimal bd = (BigDecimal)get_Value("Forecast");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set IsCritical */
public void setIsCritical (boolean IsCritical)
{
set_Value ("IsCritical", new Boolean(IsCritical));
}
/** Get IsCritical */
public boolean isCritical() 
{
Object oo = get_Value("IsCritical");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsQtyPercentage */
public void setIsQtyPercentage (boolean IsQtyPercentage)
{
set_ValueNoCheck ("IsQtyPercentage", new Boolean(IsQtyPercentage));
}
/** Get IsQtyPercentage */
public boolean isQtyPercentage() 
{
Object oo = get_Value("IsQtyPercentage");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int ISSUEMETHOD_AD_Reference_ID=1000002;
/** Issue = 0 */
public static final String ISSUEMETHOD_Issue = "0";
/** BackFlush = 1 */
public static final String ISSUEMETHOD_BackFlush = "1";
/** Set IssueMethod */
public void setIssueMethod (String IssueMethod)
{
if (IssueMethod == null || IssueMethod.equals("0") || IssueMethod.equals("1"));
 else throw new IllegalArgumentException ("IssueMethod Invalid value - Reference_ID=1000002 - 0 - 1");
if (IssueMethod != null && IssueMethod.length() > 1)
{
log.warning("Length > 1 - truncated");
IssueMethod = IssueMethod.substring(0,0);
}
set_Value ("IssueMethod", IssueMethod);
}
/** Get IssueMethod */
public String getIssueMethod() 
{
return (String)get_Value("IssueMethod");
}
/** Set LTOffSet */
public void setLTOffSet (BigDecimal LTOffSet)
{
set_Value ("LTOffSet", LTOffSet);
}
/** Get LTOffSet */
public BigDecimal getLTOffSet() 
{
BigDecimal bd = (BigDecimal)get_Value("LTOffSet");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line No.
Unique line for this document */
public void setLine (int Line)
{
set_Value ("Line", new Integer(Line));
}
/** Get Line No.
Unique line for this document */
public int getLine() 
{
Integer ii = (Integer)get_Value("Line");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order BOM Line ID */
public void setMPC_Order_BOMLine_ID (int MPC_Order_BOMLine_ID)
{
set_ValueNoCheck ("MPC_Order_BOMLine_ID", new Integer(MPC_Order_BOMLine_ID));
}
/** Get Order BOM Line ID */
public int getMPC_Order_BOMLine_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_BOMLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order BOM ID */
public void setMPC_Order_BOM_ID (int MPC_Order_BOM_ID)
{
set_ValueNoCheck ("MPC_Order_BOM_ID", new Integer(MPC_Order_BOM_ID));
}
/** Get Order BOM ID */
public int getMPC_Order_BOM_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_BOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Manufacturing Order.
Manufacturing Order */
public void setMPC_Order_ID (int MPC_Order_ID)
{
set_ValueNoCheck ("MPC_Order_ID", new Integer(MPC_Order_ID));
}
/** Get Manufacturing Order.
Manufacturing Order */
public int getMPC_Order_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute Set Instance.
Product Attribute Set Instance */
public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
{
if (M_AttributeSetInstance_ID <= 0) set_ValueNoCheck ("M_AttributeSetInstance_ID", null);
 else 
set_ValueNoCheck ("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
}
/** Get Attribute Set Instance.
Product Attribute Set Instance */
public int getM_AttributeSetInstance_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSetInstance_ID");
if (ii == null) return 0;
return ii.intValue();
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
set_ValueNoCheck ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
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
/** Set Qty.
Bill of Materials Quantity */
public void setQtyBOM (BigDecimal QtyBOM)
{
if (QtyBOM == null) throw new IllegalArgumentException ("QtyBOM is mandatory");
set_ValueNoCheck ("QtyBOM", QtyBOM);
}
/** Get Qty.
Bill of Materials Quantity */
public BigDecimal getQtyBOM() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyBOM");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Qty % */
public void setQtyBatch (BigDecimal QtyBatch)
{
if (QtyBatch == null) throw new IllegalArgumentException ("QtyBatch is mandatory");
set_ValueNoCheck ("QtyBatch", QtyBatch);
}
/** Get Qty % */
public BigDecimal getQtyBatch() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyBatch");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Delivered Quantity.
Delivered Quantity */
public void setQtyDelivered (BigDecimal QtyDelivered)
{
if (QtyDelivered == null) throw new IllegalArgumentException ("QtyDelivered is mandatory");
set_ValueNoCheck ("QtyDelivered", QtyDelivered);
}
/** Get Delivered Quantity.
Delivered Quantity */
public BigDecimal getQtyDelivered() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyDelivered");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Quantity.
The Quantity Entered is based on the selected UoM */
public void setQtyEntered (BigDecimal QtyEntered)
{
set_ValueNoCheck ("QtyEntered", QtyEntered);
}
/** Get Quantity.
The Quantity Entered is based on the selected UoM */
public BigDecimal getQtyEntered() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyEntered");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Qty Post */
public void setQtyPost (BigDecimal QtyPost)
{
if (QtyPost == null) throw new IllegalArgumentException ("QtyPost is mandatory");
set_ValueNoCheck ("QtyPost", QtyPost);
}
/** Get Qty Post */
public BigDecimal getQtyPost() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyPost");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Qty Reject */
public void setQtyReject (BigDecimal QtyReject)
{
if (QtyReject == null) throw new IllegalArgumentException ("QtyReject is mandatory");
set_ValueNoCheck ("QtyReject", QtyReject);
}
/** Get Qty Reject */
public BigDecimal getQtyReject() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyReject");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Qty Requiered */
public void setQtyRequiered (BigDecimal QtyRequiered)
{
if (QtyRequiered == null) throw new IllegalArgumentException ("QtyRequiered is mandatory");
set_Value ("QtyRequiered", QtyRequiered);
}
/** Get Qty Requiered */
public BigDecimal getQtyRequiered() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyRequiered");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Reserved Quantity.
Reserved Quantity */
public void setQtyReserved (BigDecimal QtyReserved)
{
if (QtyReserved == null) throw new IllegalArgumentException ("QtyReserved is mandatory");
set_ValueNoCheck ("QtyReserved", QtyReserved);
}
/** Get Reserved Quantity.
Reserved Quantity */
public BigDecimal getQtyReserved() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyReserved");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Qty Scrap */
public void setQtyScrap (BigDecimal QtyScrap)
{
if (QtyScrap == null) throw new IllegalArgumentException ("QtyScrap is mandatory");
set_ValueNoCheck ("QtyScrap", QtyScrap);
}
/** Get Qty Scrap */
public BigDecimal getQtyScrap() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyScrap");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Scrap */
public void setScrap (int Scrap)
{
set_ValueNoCheck ("Scrap", new Integer(Scrap));
}
/** Get Scrap */
public int getScrap() 
{
Integer ii = (Integer)get_Value("Scrap");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
if (ValidFrom == null) throw new IllegalArgumentException ("ValidFrom is mandatory");
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
}
