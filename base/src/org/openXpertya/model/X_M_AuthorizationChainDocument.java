/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AuthorizationChainDocument
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-10-19 15:08:50.703 */
public class X_M_AuthorizationChainDocument extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_AuthorizationChainDocument (Properties ctx, int M_AuthorizationChainDocument_ID, String trxName)
{
super (ctx, M_AuthorizationChainDocument_ID, trxName);
/** if (M_AuthorizationChainDocument_ID == 0)
{
setM_AuthorizationChainDocument_ID (0);
setM_AuthorizationChainLink_ID (0);
}
 */
}
/** Load Constructor */
public X_M_AuthorizationChainDocument (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_AuthorizationChainDocument");

/** TableName=M_AuthorizationChainDocument */
public static final String Table_Name="M_AuthorizationChainDocument";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_AuthorizationChainDocument");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_AuthorizationChainDocument[").append(getID()).append("]");
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
/** Set Authorization Date */
public void setAuthorizationDate (Timestamp AuthorizationDate)
{
set_Value ("AuthorizationDate", AuthorizationDate);
}
/** Get Authorization Date */
public Timestamp getAuthorizationDate() 
{
return (Timestamp)get_Value("AuthorizationDate");
}
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_Value ("C_Invoice_ID", null);
 else 
set_Value ("C_Invoice_ID", new Integer(C_Invoice_ID));
}
/** Get Invoice.
Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
if (C_Order_ID <= 0) set_Value ("C_Order_ID", null);
 else 
set_Value ("C_Order_ID", new Integer(C_Order_ID));
}
/** Get Order.
Order */
public int getC_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_AuthorizationChainDocument_ID */
public void setM_AuthorizationChainDocument_ID (int M_AuthorizationChainDocument_ID)
{
set_ValueNoCheck ("M_AuthorizationChainDocument_ID", new Integer(M_AuthorizationChainDocument_ID));
}
/** Get M_AuthorizationChainDocument_ID */
public int getM_AuthorizationChainDocument_ID() 
{
Integer ii = (Integer)get_Value("M_AuthorizationChainDocument_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int STATUS_AD_Reference_ID = MReference.getReferenceID("Authorization Status");
/** Pending = P */
public static final String STATUS_Pending = "P";
/** Authorized = A */
public static final String STATUS_Authorized = "A";
/** Set Status */
public void setStatus (String Status)
{
if (Status == null || Status.equals("P") || Status.equals("A"));
 else throw new IllegalArgumentException ("Status Invalid value - Reference = STATUS_AD_Reference_ID - P - A");
if (Status != null && Status.length() > 1)
{
log.warning("Length > 1 - truncated");
Status = Status.substring(0,1);
}
set_Value ("Status", Status);
}
/** Get Status */
public String getStatus() 
{
return (String)get_Value("Status");
}
}
