/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Document_Action_Access
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2018-04-18 14:27:01.505 */
public class X_AD_Document_Action_Access extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Document_Action_Access (Properties ctx, int AD_Document_Action_Access_ID, String trxName)
{
super (ctx, AD_Document_Action_Access_ID, trxName);
/** if (AD_Document_Action_Access_ID == 0)
{
setAD_Document_Action_Access_ID (0);
setAD_Ref_List_ID (0);
setAD_Role_ID (0);
setC_DocType_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_Document_Action_Access (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Document_Action_Access");

/** TableName=AD_Document_Action_Access */
public static final String Table_Name="AD_Document_Action_Access";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Document_Action_Access");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Document_Action_Access[").append(getID()).append("]");
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
/** Set AD_Document_Action_Access_ID */
public void setAD_Document_Action_Access_ID (int AD_Document_Action_Access_ID)
{
set_ValueNoCheck ("AD_Document_Action_Access_ID", new Integer(AD_Document_Action_Access_ID));
}
/** Get AD_Document_Action_Access_ID */
public int getAD_Document_Action_Access_ID() 
{
Integer ii = (Integer)get_Value("AD_Document_Action_Access_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Reference List.
Reference List based on Table */
public void setAD_Ref_List_ID (int AD_Ref_List_ID)
{
set_Value ("AD_Ref_List_ID", new Integer(AD_Ref_List_ID));
}
/** Get Reference List.
Reference List based on Table */
public int getAD_Ref_List_ID() 
{
Integer ii = (Integer)get_Value("AD_Ref_List_ID");
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
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
set_Value ("C_DocType_ID", new Integer(C_DocType_ID));
}
/** Get Document Type.
Document type or rules */
public int getC_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
