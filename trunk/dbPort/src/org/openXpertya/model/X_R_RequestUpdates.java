/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_RequestUpdates
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.328 */
public class X_R_RequestUpdates extends PO
{
/** Constructor estándar */
public X_R_RequestUpdates (Properties ctx, int R_RequestUpdates_ID, String trxName)
{
super (ctx, R_RequestUpdates_ID, trxName);
/** if (R_RequestUpdates_ID == 0)
{
setAD_User_ID (0);
setIsSelfService (false);
setR_Request_ID (0);
}
 */
}
/** Load Constructor */
public X_R_RequestUpdates (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=783 */
public static final int Table_ID=783;

/** TableName=R_RequestUpdates */
public static final String Table_Name="R_RequestUpdates";

protected static KeyNamePair Model = new KeyNamePair(783,"R_RequestUpdates");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_RequestUpdates[").append(getID()).append("]");
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
/** Set Request.
Request from a Business Partner or Prospect */
public void setR_Request_ID (int R_Request_ID)
{
set_ValueNoCheck ("R_Request_ID", new Integer(R_Request_ID));
}
/** Get Request.
Request from a Business Partner or Prospect */
public int getR_Request_ID() 
{
Integer ii = (Integer)get_Value("R_Request_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
