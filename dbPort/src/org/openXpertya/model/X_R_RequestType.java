/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_RequestType
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.25 */
public class X_R_RequestType extends PO
{
/** Constructor estándar */
public X_R_RequestType (Properties ctx, int R_RequestType_ID, String trxName)
{
super (ctx, R_RequestType_ID, trxName);
/** if (R_RequestType_ID == 0)
{
setConfidentialType (null);	// C
setDueDateTolerance (0);	// 7
setIsAutoChangeRequest (false);
setIsConfidentialInfo (false);	// N
setIsDefault (false);	// N
setIsEMailWhenDue (false);
setIsEMailWhenOverdue (false);
setIsSelfService (true);	// Y
setName (null);
setR_RequestType_ID (0);
}
 */
}
/** Load Constructor */
public X_R_RequestType (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=529 */
public static final int Table_ID=529;

/** TableName=R_RequestType */
public static final String Table_Name="R_RequestType";

protected static KeyNamePair Model = new KeyNamePair(529,"R_RequestType");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_RequestType[").append(getID()).append("]");
return sb.toString();
}
/** Set Auto Due Date Days.
Automatic Due Date Days */
public void setAutoDueDateDays (int AutoDueDateDays)
{
set_Value ("AutoDueDateDays", new Integer(AutoDueDateDays));
}
/** Get Auto Due Date Days.
Automatic Due Date Days */
public int getAutoDueDateDays() 
{
Integer ii = (Integer)get_Value("AutoDueDateDays");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CONFIDENTIALTYPE_AD_Reference_ID=340;
/** Public Information = A */
public static final String CONFIDENTIALTYPE_PublicInformation = "A";
/** Private Information = P */
public static final String CONFIDENTIALTYPE_PrivateInformation = "P";
/** Customer Confidential = C */
public static final String CONFIDENTIALTYPE_CustomerConfidential = "C";
/** Internal = I */
public static final String CONFIDENTIALTYPE_Internal = "I";
/** Set Confidentiality.
Type of Confidentiality */
public void setConfidentialType (String ConfidentialType)
{
if (ConfidentialType.equals("A") || ConfidentialType.equals("P") || ConfidentialType.equals("C") || ConfidentialType.equals("I"));
 else throw new IllegalArgumentException ("ConfidentialType Invalid value - Reference_ID=340 - A - P - C - I");
if (ConfidentialType == null) throw new IllegalArgumentException ("ConfidentialType is mandatory");
if (ConfidentialType.length() > 1)
{
log.warning("Length > 1 - truncated");
ConfidentialType = ConfidentialType.substring(0,0);
}
set_Value ("ConfidentialType", ConfidentialType);
}
/** Get Confidentiality.
Type of Confidentiality */
public String getConfidentialType() 
{
return (String)get_Value("ConfidentialType");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Due Date Tolerance.
Tolerance in days between the Date Next Action and the date the request is regarded as overdue */
public void setDueDateTolerance (int DueDateTolerance)
{
set_Value ("DueDateTolerance", new Integer(DueDateTolerance));
}
/** Get Due Date Tolerance.
Tolerance in days between the Date Next Action and the date the request is regarded as overdue */
public int getDueDateTolerance() 
{
Integer ii = (Integer)get_Value("DueDateTolerance");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Create Change Request.
Automatically create BOM (Engineering) Change Request */
public void setIsAutoChangeRequest (boolean IsAutoChangeRequest)
{
set_Value ("IsAutoChangeRequest", new Boolean(IsAutoChangeRequest));
}
/** Get Create Change Request.
Automatically create BOM (Engineering) Change Request */
public boolean isAutoChangeRequest() 
{
Object oo = get_Value("IsAutoChangeRequest");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Confidential Info.
Can enter confidential information */
public void setIsConfidentialInfo (boolean IsConfidentialInfo)
{
set_Value ("IsConfidentialInfo", new Boolean(IsConfidentialInfo));
}
/** Get Confidential Info.
Can enter confidential information */
public boolean isConfidentialInfo() 
{
Object oo = get_Value("IsConfidentialInfo");
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
/** Set EMail when Due.
Send EMail when Request becomes due */
public void setIsEMailWhenDue (boolean IsEMailWhenDue)
{
set_Value ("IsEMailWhenDue", new Boolean(IsEMailWhenDue));
}
/** Get EMail when Due.
Send EMail when Request becomes due */
public boolean isEMailWhenDue() 
{
Object oo = get_Value("IsEMailWhenDue");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set EMail when Overdue.
Send EMail when Request becomes overdue */
public void setIsEMailWhenOverdue (boolean IsEMailWhenOverdue)
{
set_Value ("IsEMailWhenOverdue", new Boolean(IsEMailWhenOverdue));
}
/** Get EMail when Overdue.
Send EMail when Request becomes overdue */
public boolean isEMailWhenOverdue() 
{
Object oo = get_Value("IsEMailWhenOverdue");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Invoiced.
Is this invoiced? */
public void setIsInvoiced (boolean IsInvoiced)
{
set_Value ("IsInvoiced", new Boolean(IsInvoiced));
}
/** Get Invoiced.
Is this invoiced? */
public boolean isInvoiced() 
{
Object oo = get_Value("IsInvoiced");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public void setIsSelfService (boolean IsSelfService)
{
set_Value ("IsSelfService", new Boolean(IsSelfService));
}
/** Get Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public boolean isSelfService() 
{
Object oo = get_Value("IsSelfService");
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
Name = Name.substring(0,59);
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
/** Set Request Type.
Type of request (e.g. Inquiry, Complaint, ..) */
public void setR_RequestType_ID (int R_RequestType_ID)
{
set_ValueNoCheck ("R_RequestType_ID", new Integer(R_RequestType_ID));
}
/** Get Request Type.
Type of request (e.g. Inquiry, Complaint, ..) */
public int getR_RequestType_ID() 
{
Integer ii = (Integer)get_Value("R_RequestType_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
