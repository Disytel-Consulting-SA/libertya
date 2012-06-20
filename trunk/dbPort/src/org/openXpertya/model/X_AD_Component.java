/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Component
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:26.343 */
public class X_AD_Component extends PO
{
/** Constructor estándar */
public X_AD_Component (Properties ctx, int AD_Component_ID, String trxName)
{
super (ctx, AD_Component_ID, trxName);
/** if (AD_Component_ID == 0)
{
setAD_Component_ID (0);
setCoreLevel (0);
setPackageName (null);
setPrefix (null);
setPublicName (null);
}
 */
}
/** Load Constructor */
public X_AD_Component (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1010190 */
public static final int Table_ID=1010190;

/** TableName=AD_Component */
public static final String Table_Name="AD_Component";

protected static KeyNamePair Model = new KeyNamePair(1010190,"AD_Component");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Component[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_Component_ID */
public void setAD_Component_ID (int AD_Component_ID)
{
set_ValueNoCheck ("AD_Component_ID", new Integer(AD_Component_ID));
}
/** Get AD_Component_ID */
public int getAD_Component_ID() 
{
Integer ii = (Integer)get_Value("AD_Component_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Author.
Author/Creator of the Entity */
public void setAuthor (String Author)
{
if (Author != null && Author.length() > 100)
{
log.warning("Length > 100 - truncated");
Author = Author.substring(0,100);
}
set_Value ("Author", Author);
}
/** Get Author.
Author/Creator of the Entity */
public String getAuthor() 
{
return (String)get_Value("Author");
}
/** Set CoreLevel */
public void setCoreLevel (int CoreLevel)
{
set_Value ("CoreLevel", new Integer(CoreLevel));
}
/** Get CoreLevel */
public int getCoreLevel() 
{
Integer ii = (Integer)get_Value("CoreLevel");
if (ii == null) return 0;
return ii.intValue();
}
/** Set PackageName */
public void setPackageName (String PackageName)
{
if (PackageName == null) throw new IllegalArgumentException ("PackageName is mandatory");
if (PackageName.length() > 100)
{
log.warning("Length > 100 - truncated");
PackageName = PackageName.substring(0,100);
}
set_Value ("PackageName", PackageName);
}
/** Get PackageName */
public String getPackageName() 
{
return (String)get_Value("PackageName");
}
/** Set Prefix.
Prefix before the sequence number */
public void setPrefix (String Prefix)
{
if (Prefix == null) throw new IllegalArgumentException ("Prefix is mandatory");
if (Prefix.length() > 4)
{
log.warning("Length > 4 - truncated");
Prefix = Prefix.substring(0,4);
}
set_Value ("Prefix", Prefix);
}
/** Get Prefix.
Prefix before the sequence number */
public String getPrefix() 
{
return (String)get_Value("Prefix");
}
/** Set PublicName */
public void setPublicName (String PublicName)
{
if (PublicName == null) throw new IllegalArgumentException ("PublicName is mandatory");
if (PublicName.length() > 100)
{
log.warning("Length > 100 - truncated");
PublicName = PublicName.substring(0,100);
}
set_Value ("PublicName", PublicName);
}
/** Get PublicName */
public String getPublicName() 
{
return (String)get_Value("PublicName");
}
}
