/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_Document
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:28.234 */
public class X_C_BPartner_Document extends PO
{
/** Constructor estándar */
public X_C_BPartner_Document (Properties ctx, int C_BPartner_Document_ID, String trxName)
{
super (ctx, C_BPartner_Document_ID, trxName);
/** if (C_BPartner_Document_ID == 0)
{
setC_BPartner_Document_ID (0);
setC_BPartner_ID (0);
setC_DocType_ID (0);
}
 */
}
/** Load Constructor */
public X_C_BPartner_Document (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000110 */
public static final int Table_ID=1000110;

/** TableName=C_BPartner_Document */
public static final String Table_Name="C_BPartner_Document";

protected static KeyNamePair Model = new KeyNamePair(1000110,"C_BPartner_Document");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_Document[").append(getID()).append("]");
return sb.toString();
}
/** Set C_BPartner_Document_ID */
public void setC_BPartner_Document_ID (int C_BPartner_Document_ID)
{
set_ValueNoCheck ("C_BPartner_Document_ID", new Integer(C_BPartner_Document_ID));
}
/** Get C_BPartner_Document_ID */
public int getC_BPartner_Document_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Document_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPE_ID_AD_Reference_ID=170;
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
