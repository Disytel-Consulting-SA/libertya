/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CAI_DocType
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2021-12-20 13:54:05.456 */
public class X_C_CAI_DocType extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CAI_DocType (Properties ctx, int C_CAI_DocType_ID, String trxName)
{
super (ctx, C_CAI_DocType_ID, trxName);
/** if (C_CAI_DocType_ID == 0)
{
setC_CAI_DocType_ID (0);
setC_CAI_ID (0);
setC_DocType_ID (0);
}
 */
}
/** Load Constructor */
public X_C_CAI_DocType (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CAI_DocType");

/** TableName=C_CAI_DocType */
public static final String Table_Name="C_CAI_DocType";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CAI_DocType");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CAI_DocType[").append(getID()).append("]");
return sb.toString();
}
/** Set C_CAI_DocType_ID */
public void setC_CAI_DocType_ID (int C_CAI_DocType_ID)
{
set_ValueNoCheck ("C_CAI_DocType_ID", new Integer(C_CAI_DocType_ID));
}
/** Get C_CAI_DocType_ID */
public int getC_CAI_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_CAI_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_CAI_ID */
public void setC_CAI_ID (int C_CAI_ID)
{
set_Value ("C_CAI_ID", new Integer(C_CAI_ID));
}
/** Get C_CAI_ID */
public int getC_CAI_ID() 
{
Integer ii = (Integer)get_Value("C_CAI_ID");
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
