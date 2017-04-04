/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AuthorizationChainDocumentType
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-04-04 13:20:47.538 */
public class X_M_AuthorizationChainDocumentType extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_AuthorizationChainDocumentType (Properties ctx, int M_AuthorizationChainDocumentType_ID, String trxName)
{
super (ctx, M_AuthorizationChainDocumentType_ID, trxName);
/** if (M_AuthorizationChainDocumentType_ID == 0)
{
setC_DocType_ID (0);
setM_AuthorizationChainDocumentType_ID (0);
setM_AuthorizationChain_ID (0);
setNotAuthorizationStatus (null);
}
 */
}
/** Load Constructor */
public X_M_AuthorizationChainDocumentType (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_AuthorizationChainDocumentType");

/** TableName=M_AuthorizationChainDocumentType */
public static final String Table_Name="M_AuthorizationChainDocumentType";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_AuthorizationChainDocumentType");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_AuthorizationChainDocumentType[").append(getID()).append("]");
return sb.toString();
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
/** Set M_AuthorizationChainDocumentType_ID */
public void setM_AuthorizationChainDocumentType_ID (int M_AuthorizationChainDocumentType_ID)
{
set_ValueNoCheck ("M_AuthorizationChainDocumentType_ID", new Integer(M_AuthorizationChainDocumentType_ID));
}
/** Get M_AuthorizationChainDocumentType_ID */
public int getM_AuthorizationChainDocumentType_ID() 
{
Integer ii = (Integer)get_Value("M_AuthorizationChainDocumentType_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_AuthorizationChainDocumentType_ID()));
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
public static final int NOTAUTHORIZATIONSTATUS_AD_Reference_ID = MReference.getReferenceID("_Document Status");
/** Voided = VO */
public static final String NOTAUTHORIZATIONSTATUS_Voided = "VO";
/** Not Approved = NA */
public static final String NOTAUTHORIZATIONSTATUS_NotApproved = "NA";
/** In Progress = IP */
public static final String NOTAUTHORIZATIONSTATUS_InProgress = "IP";
/** Completed = CO */
public static final String NOTAUTHORIZATIONSTATUS_Completed = "CO";
/** Approved = AP */
public static final String NOTAUTHORIZATIONSTATUS_Approved = "AP";
/** Closed = CL */
public static final String NOTAUTHORIZATIONSTATUS_Closed = "CL";
/** Waiting Confirmation = WC */
public static final String NOTAUTHORIZATIONSTATUS_WaitingConfirmation = "WC";
/** Waiting Payment = WP */
public static final String NOTAUTHORIZATIONSTATUS_WaitingPayment = "WP";
/** Unknown = ?? */
public static final String NOTAUTHORIZATIONSTATUS_Unknown = "??";
/** Drafted = DR */
public static final String NOTAUTHORIZATIONSTATUS_Drafted = "DR";
/** Invalid = IN */
public static final String NOTAUTHORIZATIONSTATUS_Invalid = "IN";
/** Reversed = RE */
public static final String NOTAUTHORIZATIONSTATUS_Reversed = "RE";
/** Set Not Authorize Status */
public void setNotAuthorizationStatus (String NotAuthorizationStatus)
{
if (NotAuthorizationStatus.equals("VO") || NotAuthorizationStatus.equals("NA") || NotAuthorizationStatus.equals("IP") || NotAuthorizationStatus.equals("CO") || NotAuthorizationStatus.equals("AP") || NotAuthorizationStatus.equals("CL") || NotAuthorizationStatus.equals("WC") || NotAuthorizationStatus.equals("WP") || NotAuthorizationStatus.equals("??") || NotAuthorizationStatus.equals("DR") || NotAuthorizationStatus.equals("IN") || NotAuthorizationStatus.equals("RE") || ( refContainsValue("CORE-AD_Reference-131", NotAuthorizationStatus) ) );
 else throw new IllegalArgumentException ("NotAuthorizationStatus Invalid value: " + NotAuthorizationStatus + ".  Valid: " +  refValidOptions("CORE-AD_Reference-131") );
if (NotAuthorizationStatus == null) throw new IllegalArgumentException ("NotAuthorizationStatus is mandatory");
if (NotAuthorizationStatus.length() > 2)
{
log.warning("Length > 2 - truncated");
NotAuthorizationStatus = NotAuthorizationStatus.substring(0,2);
}
set_Value ("NotAuthorizationStatus", NotAuthorizationStatus);
}
/** Get Not Authorize Status */
public String getNotAuthorizationStatus() 
{
return (String)get_Value("NotAuthorizationStatus");
}
}
