/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_TranNatureCodeB
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:39.921 */
public class X_M_TranNatureCodeB extends PO
{
/** Constructor estándar */
public X_M_TranNatureCodeB (Properties ctx, int M_TranNatureCodeB_ID, String trxName)
{
super (ctx, M_TranNatureCodeB_ID, trxName);
/** if (M_TranNatureCodeB_ID == 0)
{
setm_trannaturecodea_id (0);
setm_trannaturecodeb_id (0);
}
 */
}
/** Load Constructor */
public X_M_TranNatureCodeB (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000096 */
public static final int Table_ID=1000096;

/** TableName=M_TranNatureCodeB */
public static final String Table_Name="M_TranNatureCodeB";

protected static KeyNamePair Model = new KeyNamePair(1000096,"M_TranNatureCodeB");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_TranNatureCodeB[").append(getID()).append("]");
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
public static final int M_TRANNATURECODEA_ID_AD_Reference_ID=1000044;
/** Set m_trannaturecodea_id */
public void setm_trannaturecodea_id (int m_trannaturecodea_id)
{
set_Value ("m_trannaturecodea_id", new Integer(m_trannaturecodea_id));
}
/** Get m_trannaturecodea_id */
public int getm_trannaturecodea_id() 
{
Integer ii = (Integer)get_Value("m_trannaturecodea_id");
if (ii == null) return 0;
return ii.intValue();
}
/** Set m_trannaturecodeb_id */
public void setm_trannaturecodeb_id (int m_trannaturecodeb_id)
{
set_ValueNoCheck ("m_trannaturecodeb_id", new Integer(m_trannaturecodeb_id));
}
/** Get m_trannaturecodeb_id */
public int getm_trannaturecodeb_id() 
{
Integer ii = (Integer)get_Value("m_trannaturecodeb_id");
if (ii == null) return 0;
return ii.intValue();
}
}
