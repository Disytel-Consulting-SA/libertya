/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_StateTermsCode
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:39.828 */
public class X_M_StateTermsCode extends PO
{
/** Constructor estándar */
public X_M_StateTermsCode (Properties ctx, int M_StateTermsCode_ID, String trxName)
{
super (ctx, M_StateTermsCode_ID, trxName);
/** if (M_StateTermsCode_ID == 0)
{
setm_statetermscode_id (0);
}
 */
}
/** Load Constructor */
public X_M_StateTermsCode (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000098 */
public static final int Table_ID=1000098;

/** TableName=M_StateTermsCode */
public static final String Table_Name="M_StateTermsCode";

protected static KeyNamePair Model = new KeyNamePair(1000098,"M_StateTermsCode");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_StateTermsCode[").append(getID()).append("]");
return sb.toString();
}
/** Set Validation code.
Validation Code */
public void setCode (BigDecimal Code)
{
set_Value ("Code", Code);
}
/** Get Validation code.
Validation Code */
public BigDecimal getCode() 
{
BigDecimal bd = (BigDecimal)get_Value("Code");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set m_statetermscode_id */
public void setm_statetermscode_id (int m_statetermscode_id)
{
set_ValueNoCheck ("m_statetermscode_id", new Integer(m_statetermscode_id));
}
/** Get m_statetermscode_id */
public int getm_statetermscode_id() 
{
Integer ii = (Integer)get_Value("m_statetermscode_id");
if (ii == null) return 0;
return ii.intValue();
}
}
