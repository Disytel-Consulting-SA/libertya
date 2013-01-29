/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Combo
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-07-14 14:03:43.068 */
public class X_C_Combo extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Combo (Properties ctx, int C_Combo_ID, String trxName)
{
super (ctx, C_Combo_ID, trxName);
/** if (C_Combo_ID == 0)
{
setC_Combo_ID (0);
setDiscountApplication (null);	// D
setName (null);
setProcessed (false);
setPublishStatus (null);	// D
setValidFrom (new Timestamp(System.currentTimeMillis()));	// @#Date@
}
 */
}
/** Load Constructor */
public X_C_Combo (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Combo");

/** TableName=C_Combo */
public static final String Table_Name="C_Combo";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Combo");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Combo[").append(getID()).append("]");
return sb.toString();
}
/** Set Products Combo.
Products Combo */
public void setC_Combo_ID (int C_Combo_ID)
{
set_ValueNoCheck ("C_Combo_ID", new Integer(C_Combo_ID));
}
/** Get Products Combo.
Products Combo */
public int getC_Combo_ID() 
{
Integer ii = (Integer)get_Value("C_Combo_ID");
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
public static final int DISCOUNTAPPLICATION_AD_Reference_ID = MReference.getReferenceID("Discount Application");
/** Discount To Price = D */
public static final String DISCOUNTAPPLICATION_DiscountToPrice = "D";
/** Bonus = B */
public static final String DISCOUNTAPPLICATION_Bonus = "B";
/** Set Discount Application.
Discount Application */
public void setDiscountApplication (String DiscountApplication)
{
if (DiscountApplication.equals("D") || DiscountApplication.equals("B"));
 else throw new IllegalArgumentException ("DiscountApplication Invalid value - Reference = DISCOUNTAPPLICATION_AD_Reference_ID - D - B");
if (DiscountApplication == null) throw new IllegalArgumentException ("DiscountApplication is mandatory");
if (DiscountApplication.length() > 1)
{
log.warning("Length > 1 - truncated");
DiscountApplication = DiscountApplication.substring(0,1);
}
set_Value ("DiscountApplication", DiscountApplication);
}
/** Get Discount Application.
Discount Application */
public String getDiscountApplication() 
{
return (String)get_Value("DiscountApplication");
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
public static final int PUBLISHSTATUS_AD_Reference_ID = MReference.getReferenceID("Discount Publish Status");
/** Published = P */
public static final String PUBLISHSTATUS_Published = "P";
/** Drafted = D */
public static final String PUBLISHSTATUS_Drafted = "D";
/** Set Publication Status.
Status of Publication */
public void setPublishStatus (String PublishStatus)
{
if (PublishStatus.equals("P") || PublishStatus.equals("D"));
 else throw new IllegalArgumentException ("PublishStatus Invalid value - Reference = PUBLISHSTATUS_AD_Reference_ID - P - D");
if (PublishStatus == null) throw new IllegalArgumentException ("PublishStatus is mandatory");
if (PublishStatus.length() > 1)
{
log.warning("Length > 1 - truncated");
PublishStatus = PublishStatus.substring(0,1);
}
set_Value ("PublishStatus", PublishStatus);
}
/** Get Publication Status.
Status of Publication */
public String getPublishStatus() 
{
return (String)get_Value("PublishStatus");
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
