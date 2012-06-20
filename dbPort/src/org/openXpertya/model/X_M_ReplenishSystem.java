/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_ReplenishSystem
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-06-18 14:08:51.859 */
public class X_M_ReplenishSystem extends PO
{
/** Constructor estándar */
public X_M_ReplenishSystem (Properties ctx, int M_ReplenishSystem_ID, String trxName)
{
super (ctx, M_ReplenishSystem_ID, trxName);
/** if (M_ReplenishSystem_ID == 0)
{
setM_ReplenishSystem_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_M_ReplenishSystem (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000157 */
public static final int Table_ID=1000157;

/** TableName=M_ReplenishSystem */
public static final String Table_Name="M_ReplenishSystem";

protected static KeyNamePair Model = new KeyNamePair(1000157,"M_ReplenishSystem");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_ReplenishSystem[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_Value ("AD_User_ID", null);
 else 
set_Value ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set minstockfunc */
public void setminstockfunc (String minstockfunc)
{
if (minstockfunc != null && minstockfunc.length() > 255)
{
log.warning("Length > 255 - truncated");
minstockfunc = minstockfunc.substring(0,255);
}
set_Value ("minstockfunc", minstockfunc);
}
/** Get minstockfunc */
public String getminstockfunc() 
{
return (String)get_Value("minstockfunc");
}
/** Set M_ReplenishSystem_ID */
public void setM_ReplenishSystem_ID (int M_ReplenishSystem_ID)
{
set_ValueNoCheck ("M_ReplenishSystem_ID", new Integer(M_ReplenishSystem_ID));
}
/** Get M_ReplenishSystem_ID */
public int getM_ReplenishSystem_ID() 
{
Integer ii = (Integer)get_Value("M_ReplenishSystem_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 255)
{
log.warning("Length > 255 - truncated");
Name = Name.substring(0,255);
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
/** Set qtyfunc */
public void setqtyfunc (String qtyfunc)
{
if (qtyfunc != null && qtyfunc.length() > 255)
{
log.warning("Length > 255 - truncated");
qtyfunc = qtyfunc.substring(0,255);
}
set_Value ("qtyfunc", qtyfunc);
}
/** Get qtyfunc */
public String getqtyfunc() 
{
return (String)get_Value("qtyfunc");
}
}
