/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AuthorizationChainDocumentType
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-11-14 17:15:29.304 */
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
}
