/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_AcctProcessorTable
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2018-05-07 13:40:47.127 */
public class X_C_AcctProcessorTable extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_AcctProcessorTable (Properties ctx, int C_AcctProcessorTable_ID, String trxName)
{
super (ctx, C_AcctProcessorTable_ID, trxName);
/** if (C_AcctProcessorTable_ID == 0)
{
setAD_Table_ID (0);
setC_AcctProcessor_ID (0);
setC_AcctProcessorTable_ID (0);
}
 */
}
/** Load Constructor */
public X_C_AcctProcessorTable (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_AcctProcessorTable");

/** TableName=C_AcctProcessorTable */
public static final String Table_Name="C_AcctProcessorTable";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_AcctProcessorTable");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_AcctProcessorTable[").append(getID()).append("]");
return sb.toString();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
set_Value ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Accounting Processor.
Accounting Processor/Server Parameters */
public void setC_AcctProcessor_ID (int C_AcctProcessor_ID)
{
set_Value ("C_AcctProcessor_ID", new Integer(C_AcctProcessor_ID));
}
/** Get Accounting Processor.
Accounting Processor/Server Parameters */
public int getC_AcctProcessor_ID() 
{
Integer ii = (Integer)get_Value("C_AcctProcessor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_AcctProcessorTable_ID */
public void setC_AcctProcessorTable_ID (int C_AcctProcessorTable_ID)
{
set_ValueNoCheck ("C_AcctProcessorTable_ID", new Integer(C_AcctProcessorTable_ID));
}
/** Get C_AcctProcessorTable_ID */
public int getC_AcctProcessorTable_ID() 
{
Integer ii = (Integer)get_Value("C_AcctProcessorTable_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sql WHERE.
Fully qualified SQL WHERE clause */
public void setWhereClause (String WhereClause)
{
if (WhereClause != null && WhereClause.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WhereClause = WhereClause.substring(0,2000);
}
set_Value ("WhereClause", WhereClause);
}
/** Get Sql WHERE.
Fully qualified SQL WHERE clause */
public String getWhereClause() 
{
return (String)get_Value("WhereClause");
}
}
