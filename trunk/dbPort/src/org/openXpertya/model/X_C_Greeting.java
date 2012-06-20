/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Greeting
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:31.487 */
public class X_C_Greeting extends PO
{
/** Constructor estándar */
public X_C_Greeting (Properties ctx, int C_Greeting_ID, String trxName)
{
super (ctx, C_Greeting_ID, trxName);
/** if (C_Greeting_ID == 0)
{
setC_Greeting_ID (0);
setIsDefault (false);
setIsFirstNameOnly (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_Greeting (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=346 */
public static final int Table_ID=346;

/** TableName=C_Greeting */
public static final String Table_Name="C_Greeting";

protected static KeyNamePair Model = new KeyNamePair(346,"C_Greeting");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Greeting[").append(getID()).append("]");
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
/** Set Greeting.
Greeting to print on correspondence */
public void setC_Greeting_ID (int C_Greeting_ID)
{
set_ValueNoCheck ("C_Greeting_ID", new Integer(C_Greeting_ID));
}
/** Get Greeting.
Greeting to print on correspondence */
public int getC_Greeting_ID() 
{
Integer ii = (Integer)get_Value("C_Greeting_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Greeting.
For letters, e.g. "Dear 
{
0}
" or "Dear Mr. 
{
0}
" - At runtime, "
{
0}
" is replaced by the name */
public void setGreeting (String Greeting)
{
if (Greeting != null && Greeting.length() > 60)
{
log.warning("Length > 60 - truncated");
Greeting = Greeting.substring(0,60);
}
set_Value ("Greeting", Greeting);
}
/** Get Greeting.
For letters, e.g. "Dear 
{
0}
" or "Dear Mr. 
{
0}
" - At runtime, "
{
0}
" is replaced by the name */
public String getGreeting() 
{
return (String)get_Value("Greeting");
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
/** Set First name only.
Print only the first name in greetings */
public void setIsFirstNameOnly (boolean IsFirstNameOnly)
{
set_Value ("IsFirstNameOnly", new Boolean(IsFirstNameOnly));
}
/** Get First name only.
Print only the first name in greetings */
public boolean isFirstNameOnly() 
{
Object oo = get_Value("IsFirstNameOnly");
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
}
