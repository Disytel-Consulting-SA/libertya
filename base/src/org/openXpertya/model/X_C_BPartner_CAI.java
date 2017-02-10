/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_CAI
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-02-10 16:16:36.169 */
public class X_C_BPartner_CAI extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BPartner_CAI (Properties ctx, int C_BPartner_CAI_ID, String trxName)
{
super (ctx, C_BPartner_CAI_ID, trxName);
/** if (C_BPartner_CAI_ID == 0)
{
setCAI (null);
setC_BPartner_CAI_ID (0);
setC_BPartner_ID (0);
setDateCAI (new Timestamp(System.currentTimeMillis()));
setPOSNumber (0);
}
 */
}
/** Load Constructor */
public X_C_BPartner_CAI (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BPartner_CAI");

/** TableName=C_BPartner_CAI */
public static final String Table_Name="C_BPartner_CAI";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BPartner_CAI");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_CAI[").append(getID()).append("]");
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
/** Set C_BPartner_CAI_ID */
public void setC_BPartner_CAI_ID (int C_BPartner_CAI_ID)
{
set_ValueNoCheck ("C_BPartner_CAI_ID", new Integer(C_BPartner_CAI_ID));
}
/** Get C_BPartner_CAI_ID */
public int getC_BPartner_CAI_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_CAI_ID");
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
/** Set POS Number */
public void setPOSNumber (int POSNumber)
{
set_Value ("POSNumber", new Integer(POSNumber));
}
/** Get POS Number */
public int getPOSNumber() 
{
Integer ii = (Integer)get_Value("POSNumber");
if (ii == null) return 0;
return ii.intValue();
}
}
