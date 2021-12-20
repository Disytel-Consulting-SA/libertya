/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CAI
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2021-12-20 13:54:05.445 */
public class X_C_CAI extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CAI (Properties ctx, int C_CAI_ID, String trxName)
{
super (ctx, C_CAI_ID, trxName);
/** if (C_CAI_ID == 0)
{
setCAI (null);
setC_CAI_ID (0);
setDateCAI (new Timestamp(System.currentTimeMillis()));
setValidFrom (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_C_CAI (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CAI");

/** TableName=C_CAI */
public static final String Table_Name="C_CAI";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CAI");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CAI[").append(getID()).append("]");
return sb.toString();
}
/** Set CAI */
public void setCAI (String CAI)
{
if (CAI == null) throw new IllegalArgumentException ("CAI is mandatory");
if (CAI.length() > 14)
{
log.warning("Length > 14 - truncated");
CAI = CAI.substring(0,14);
}
set_Value ("CAI", CAI);
}
/** Get CAI */
public String getCAI() 
{
return (String)get_Value("CAI");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getCAI());
}
/** Set C_CAI_ID */
public void setC_CAI_ID (int C_CAI_ID)
{
set_ValueNoCheck ("C_CAI_ID", new Integer(C_CAI_ID));
}
/** Get C_CAI_ID */
public int getC_CAI_ID() 
{
Integer ii = (Integer)get_Value("C_CAI_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CAI Date */
public void setDateCAI (Timestamp DateCAI)
{
if (DateCAI == null) throw new IllegalArgumentException ("DateCAI is mandatory");
set_Value ("DateCAI", DateCAI);
}
/** Get CAI Date */
public Timestamp getDateCAI() 
{
return (Timestamp)get_Value("DateCAI");
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
if (ValidFrom == null) throw new IllegalArgumentException ("ValidFrom is mandatory");
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
}
