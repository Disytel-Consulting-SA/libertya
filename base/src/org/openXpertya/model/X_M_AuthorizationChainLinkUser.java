/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AuthorizationChainLinkUser
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-10-19 15:08:50.743 */
public class X_M_AuthorizationChainLinkUser extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_AuthorizationChainLinkUser (Properties ctx, int M_AuthorizationChainLinkUser_ID, String trxName)
{
super (ctx, M_AuthorizationChainLinkUser_ID, trxName);
/** if (M_AuthorizationChainLinkUser_ID == 0)
{
setAD_User_ID (0);
setM_AuthorizationChainLink_ID (0);
setM_AuthorizationChainLinkUser_ID (0);
}
 */
}
/** Load Constructor */
public X_M_AuthorizationChainLinkUser (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_AuthorizationChainLinkUser");

/** TableName=M_AuthorizationChainLinkUser */
public static final String Table_Name="M_AuthorizationChainLinkUser";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_AuthorizationChainLinkUser");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_AuthorizationChainLinkUser[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
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
/** Set End Date.
Last effective date (inclusive) */
public void setEndDate (Timestamp EndDate)
{
set_Value ("EndDate", EndDate);
}
/** Get End Date.
Last effective date (inclusive) */
public Timestamp getEndDate() 
{
return (Timestamp)get_Value("EndDate");
}
/** Set M_AuthorizationChainLink_ID */
public void setM_AuthorizationChainLink_ID (int M_AuthorizationChainLink_ID)
{
set_Value ("M_AuthorizationChainLink_ID", new Integer(M_AuthorizationChainLink_ID));
}
/** Get M_AuthorizationChainLink_ID */
public int getM_AuthorizationChainLink_ID() 
{
Integer ii = (Integer)get_Value("M_AuthorizationChainLink_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_AuthorizationChainLinkUser_ID */
public void setM_AuthorizationChainLinkUser_ID (int M_AuthorizationChainLinkUser_ID)
{
set_ValueNoCheck ("M_AuthorizationChainLinkUser_ID", new Integer(M_AuthorizationChainLinkUser_ID));
}
/** Get M_AuthorizationChainLinkUser_ID */
public int getM_AuthorizationChainLinkUser_ID() 
{
Integer ii = (Integer)get_Value("M_AuthorizationChainLinkUser_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Start Date.
First effective day (inclusive) */
public void setStartDate (Timestamp StartDate)
{
set_Value ("StartDate", StartDate);
}
/** Get Start Date.
First effective day (inclusive) */
public Timestamp getStartDate() 
{
return (Timestamp)get_Value("StartDate");
}
}
