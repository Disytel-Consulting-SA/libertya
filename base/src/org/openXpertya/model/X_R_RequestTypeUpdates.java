/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_RequestTypeUpdates
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.281 */
public class X_R_RequestTypeUpdates extends PO
{
/** Constructor estándar */
public X_R_RequestTypeUpdates (Properties ctx, int R_RequestTypeUpdates_ID, String trxName)
{
super (ctx, R_RequestTypeUpdates_ID, trxName);
/** if (R_RequestTypeUpdates_ID == 0)
{
setAD_User_ID (0);
setIsSelfService (false);
setR_RequestType_ID (0);
}
 */
}
/** Load Constructor */
public X_R_RequestTypeUpdates (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=784 */
public static final int Table_ID=784;

/** TableName=R_RequestTypeUpdates */
public static final String Table_Name="R_RequestTypeUpdates";

protected static KeyNamePair Model = new KeyNamePair(784,"R_RequestTypeUpdates");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_RequestTypeUpdates[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
set_ValueNoCheck ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
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
