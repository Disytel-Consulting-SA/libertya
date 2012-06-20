/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_GroupUpdates
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.906 */
public class X_R_GroupUpdates extends PO
{
/** Constructor estándar */
public X_R_GroupUpdates (Properties ctx, int R_GroupUpdates_ID, String trxName)
{
super (ctx, R_GroupUpdates_ID, trxName);
/** if (R_GroupUpdates_ID == 0)
{
setAD_User_ID (0);
setIsSelfService (false);
setR_Group_ID (0);
}
 */
}
/** Load Constructor */
public X_R_GroupUpdates (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=786 */
public static final int Table_ID=786;

/** TableName=R_GroupUpdates */
public static final String Table_Name="R_GroupUpdates";

protected static KeyNamePair Model = new KeyNamePair(786,"R_GroupUpdates");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_GroupUpdates[").append(getID()).append("]");
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
/** Set Group.
Request Group */
public void setR_Group_ID (int R_Group_ID)
{
set_ValueNoCheck ("R_Group_ID", new Integer(R_Group_ID));
}
/** Get Group.
Request Group */
public int getR_Group_ID() 
{
Integer ii = (Integer)get_Value("R_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
