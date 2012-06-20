/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_StandardResponse
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.375 */
public class X_R_StandardResponse extends PO
{
/** Constructor estándar */
public X_R_StandardResponse (Properties ctx, int R_StandardResponse_ID, String trxName)
{
super (ctx, R_StandardResponse_ID, trxName);
/** if (R_StandardResponse_ID == 0)
{
setName (null);
setR_StandardResponse_ID (0);
setResponseText (null);
}
 */
}
/** Load Constructor */
public X_R_StandardResponse (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=775 */
public static final int Table_ID=775;

/** TableName=R_StandardResponse */
public static final String Table_Name="R_StandardResponse";

protected static KeyNamePair Model = new KeyNamePair(775,"R_StandardResponse");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_StandardResponse[").append(getID()).append("]");
return sb.toString();
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
/** Set Standard Response.
Request Standard Response  */
public void setR_StandardResponse_ID (int R_StandardResponse_ID)
{
set_ValueNoCheck ("R_StandardResponse_ID", new Integer(R_StandardResponse_ID));
}
/** Get Standard Response.
Request Standard Response  */
public int getR_StandardResponse_ID() 
{
Integer ii = (Integer)get_Value("R_StandardResponse_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Response Text.
Request Response Text */
public void setResponseText (String ResponseText)
{
if (ResponseText == null) throw new IllegalArgumentException ("ResponseText is mandatory");
if (ResponseText.length() > 2000)
{
log.warning("Length > 2000 - truncated");
ResponseText = ResponseText.substring(0,1999);
}
set_Value ("ResponseText", ResponseText);
}
/** Get Response Text.
Request Response Text */
public String getResponseText() 
{
return (String)get_Value("ResponseText");
}
}
