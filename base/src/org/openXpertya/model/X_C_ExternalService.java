/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.KeyNamePair;
/** Modelo Generado por C_ExternalService
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-09-26 11:58:22.814 */
public class X_C_ExternalService extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_ExternalService (Properties ctx, int C_ExternalService_ID, String trxName)
{
super (ctx, C_ExternalService_ID, trxName);
/** if (C_ExternalService_ID == 0)
{
setC_ExternalService_ID (0);
setName (null);
setURL (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_ExternalService (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_ExternalService");

/** TableName=C_ExternalService */
public static final String Table_Name="C_ExternalService";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_ExternalService");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ExternalService[").append(getID()).append("]");
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
/** Set External Service */
public void setC_ExternalService_ID (int C_ExternalService_ID)
{
set_ValueNoCheck ("C_ExternalService_ID", new Integer(C_ExternalService_ID));
}
/** Get External Service */
public int getC_ExternalService_ID() 
{
Integer ii = (Integer)get_Value("C_ExternalService_ID");
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
/** Set Password.
Password of any length (case sensitive) */
public void setPassword (String Password)
{
if (Password != null && Password.length() > 60)
{
log.warning("Length > 60 - truncated");
Password = Password.substring(0,60);
}
set_Value ("Password", Password);
}
/** Get Password.
Password of any length (case sensitive) */
public String getPassword() 
{
return (String)get_Value("Password");
}
/** Set Port */
public void setPort (int Port)
{
set_Value ("Port", new Integer(Port));
}
/** Get Port */
public int getPort() 
{
Integer ii = (Integer)get_Value("Port");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Timeout */
public void setTimeout (int Timeout)
{
set_Value ("Timeout", new Integer(Timeout));
}
/** Get Timeout */
public int getTimeout() 
{
Integer ii = (Integer)get_Value("Timeout");
if (ii == null) return 0;
return ii.intValue();
}
/** Set URL.
URL */
public void setURL (String URL)
{
if (URL == null) throw new IllegalArgumentException ("URL is mandatory");
if (URL.length() > 255)
{
log.warning("Length > 255 - truncated");
URL = URL.substring(0,255);
}
set_Value ("URL", URL);
}
/** Get URL.
URL */
public String getURL() 
{
return (String)get_Value("URL");
}
/** Set User Name */
public void setUserName (String UserName)
{
if (UserName != null && UserName.length() > 60)
{
log.warning("Length > 60 - truncated");
UserName = UserName.substring(0,60);
}
set_Value ("UserName", UserName);
}
/** Get User Name */
public String getUserName() 
{
return (String)get_Value("UserName");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
