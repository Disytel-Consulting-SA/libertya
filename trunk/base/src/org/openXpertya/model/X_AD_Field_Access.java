/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Field_Access
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-04-20 16:01:16.405 */
public class X_AD_Field_Access extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Field_Access (Properties ctx, int AD_Field_Access_ID, String trxName)
{
super (ctx, AD_Field_Access_ID, trxName);
/** if (AD_Field_Access_ID == 0)
{
setAccessType (null);
setAD_Field_Access_ID (0);
setAD_Field_ID (0);
setAD_Role_ID (0);
setAD_Tab_ID (0);	// @AD_Tab_ID@
}
 */
}
/** Load Constructor */
public X_AD_Field_Access (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Field_Access");

/** TableName=AD_Field_Access */
public static final String Table_Name="AD_Field_Access";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Field_Access");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Field_Access[").append(getID()).append("]");
return sb.toString();
}
public static final int ACCESSTYPE_AD_Reference_ID = MReference.getReferenceID("AD_Field Access Type");
/** Read Only = R */
public static final String ACCESSTYPE_ReadOnly = "R";
/** Not Displayed = D */
public static final String ACCESSTYPE_NotDisplayed = "D";
/** Always Updateable = A */
public static final String ACCESSTYPE_AlwaysUpdateable = "A";
/** Set Access Type */
public void setAccessType (String AccessType)
{
if (AccessType.equals("R") || AccessType.equals("D") || AccessType.equals("A") || ( refContainsValue("CORE-AD_Reference-1000083", AccessType) ) );
 else throw new IllegalArgumentException ("AccessType Invalid value: " + AccessType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1000083") );
if (AccessType == null) throw new IllegalArgumentException ("AccessType is mandatory");
if (AccessType.length() > 1)
{
log.warning("Length > 1 - truncated");
AccessType = AccessType.substring(0,1);
}
set_Value ("AccessType", AccessType);
}
/** Get Access Type */
public String getAccessType() 
{
return (String)get_Value("AccessType");
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
/** Set Component Version Identifier */
public void setAD_ComponentVersion_ID (int AD_ComponentVersion_ID)
{
if (AD_ComponentVersion_ID <= 0) set_Value ("AD_ComponentVersion_ID", null);
 else 
set_Value ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Field Access */
public void setAD_Field_Access_ID (int AD_Field_Access_ID)
{
set_ValueNoCheck ("AD_Field_Access_ID", new Integer(AD_Field_Access_ID));
}
/** Get Field Access */
public int getAD_Field_Access_ID() 
{
Integer ii = (Integer)get_Value("AD_Field_Access_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Field.
Field on a database table */
public void setAD_Field_ID (int AD_Field_ID)
{
set_ValueNoCheck ("AD_Field_ID", new Integer(AD_Field_ID));
}
/** Get Field.
Field on a database table */
public int getAD_Field_ID() 
{
Integer ii = (Integer)get_Value("AD_Field_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Role.
Responsibility Role */
public void setAD_Role_ID (int AD_Role_ID)
{
set_Value ("AD_Role_ID", new Integer(AD_Role_ID));
}
/** Get Role.
Responsibility Role */
public int getAD_Role_ID() 
{
Integer ii = (Integer)get_Value("AD_Role_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tab.
Tab within a Window */
public void setAD_Tab_ID (int AD_Tab_ID)
{
set_Value ("AD_Tab_ID", new Integer(AD_Tab_ID));
}
/** Get Tab.
Tab within a Window */
public int getAD_Tab_ID() 
{
Integer ii = (Integer)get_Value("AD_Tab_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Dynamic Validation.
Dynamic Validation Rule */
public void setAD_Val_Rule_ID (int AD_Val_Rule_ID)
{
if (AD_Val_Rule_ID <= 0) set_Value ("AD_Val_Rule_ID", null);
 else 
set_Value ("AD_Val_Rule_ID", new Integer(AD_Val_Rule_ID));
}
/** Get Dynamic Validation.
Dynamic Validation Rule */
public int getAD_Val_Rule_ID() 
{
Integer ii = (Integer)get_Value("AD_Val_Rule_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
