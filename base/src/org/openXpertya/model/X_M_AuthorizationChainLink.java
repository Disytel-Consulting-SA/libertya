/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AuthorizationChainLink
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-11-14 17:15:29.492 */
public class X_M_AuthorizationChainLink extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_AuthorizationChainLink (Properties ctx, int M_AuthorizationChainLink_ID, String trxName)
{
super (ctx, M_AuthorizationChainLink_ID, trxName);
/** if (M_AuthorizationChainLink_ID == 0)
{
setLinkNumber (0);	// @SQL=SELECT NVL(MAX(LinkNumber),0)+1 AS DefaultValue FROM M_AuthorizationChainLink WHERE M_AuthorizationChain_ID=@M_AuthorizationChain_ID@
setMandatory (false);
setM_AuthorizationChain_ID (0);
setM_AuthorizationChainLink_ID (0);
setValidateDocumentAmount (false);
}
 */
}
/** Load Constructor */
public X_M_AuthorizationChainLink (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_AuthorizationChainLink");

/** TableName=M_AuthorizationChainLink */
public static final String Table_Name="M_AuthorizationChainLink";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_AuthorizationChainLink");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_AuthorizationChainLink[").append(getID()).append("]");
return sb.toString();
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDescription());
}
/** Set LinkNumber */
public void setLinkNumber (int LinkNumber)
{
set_Value ("LinkNumber", new Integer(LinkNumber));
}
/** Get LinkNumber */
public int getLinkNumber() 
{
Integer ii = (Integer)get_Value("LinkNumber");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Mandatory */
public void setMandatory (boolean Mandatory)
{
set_Value ("Mandatory", new Boolean(Mandatory));
}
/** Get Mandatory */
public boolean isMandatory() 
{
Object oo = get_Value("Mandatory");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set M_AuthorizationChain_ID */
public void setM_AuthorizationChain_ID (int M_AuthorizationChain_ID)
{
set_Value ("M_AuthorizationChain_ID", new Integer(M_AuthorizationChain_ID));
}
/** Get M_AuthorizationChain_ID */
public int getM_AuthorizationChain_ID() 
{
Integer ii = (Integer)get_Value("M_AuthorizationChain_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_AuthorizationChainLink_ID */
public void setM_AuthorizationChainLink_ID (int M_AuthorizationChainLink_ID)
{
set_ValueNoCheck ("M_AuthorizationChainLink_ID", new Integer(M_AuthorizationChainLink_ID));
}
/** Get M_AuthorizationChainLink_ID */
public int getM_AuthorizationChainLink_ID() 
{
Integer ii = (Integer)get_Value("M_AuthorizationChainLink_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set MaximumAmount */
public void setMaximumAmount (BigDecimal MaximumAmount)
{
set_Value ("MaximumAmount", MaximumAmount);
}
/** Get MaximumAmount */
public BigDecimal getMaximumAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("MaximumAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set MinimumAmount */
public void setMinimumAmount (BigDecimal MinimumAmount)
{
set_Value ("MinimumAmount", MinimumAmount);
}
/** Get MinimumAmount */
public BigDecimal getMinimumAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("MinimumAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Validate Document Amount */
public void setValidateDocumentAmount (boolean ValidateDocumentAmount)
{
set_Value ("ValidateDocumentAmount", new Boolean(ValidateDocumentAmount));
}
/** Get Validate Document Amount */
public boolean isValidateDocumentAmount() 
{
Object oo = get_Value("ValidateDocumentAmount");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
