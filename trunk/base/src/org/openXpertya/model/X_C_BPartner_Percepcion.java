/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_Percepcion
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-05-17 17:47:27.234 */
public class X_C_BPartner_Percepcion extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BPartner_Percepcion (Properties ctx, int C_BPartner_Percepcion_ID, String trxName)
{
super (ctx, C_BPartner_Percepcion_ID, trxName);
/** if (C_BPartner_Percepcion_ID == 0)
{
setC_BPartner_ID (0);
setC_BPartner_Percepcion_ID (0);
setC_Region_ID (0);
}
 */
}
/** Load Constructor */
public X_C_BPartner_Percepcion (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BPartner_Percepcion");

/** TableName=C_BPartner_Percepcion */
public static final String Table_Name="C_BPartner_Percepcion";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BPartner_Percepcion");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_Percepcion[").append(getID()).append("]");
return sb.toString();
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
/** Set C_BPartner_Percepcion_ID */
public void setC_BPartner_Percepcion_ID (int C_BPartner_Percepcion_ID)
{
set_ValueNoCheck ("C_BPartner_Percepcion_ID", new Integer(C_BPartner_Percepcion_ID));
}
/** Get C_BPartner_Percepcion_ID */
public int getC_BPartner_Percepcion_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Percepcion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Region.
Identifies a geographical Region */
public void setC_Region_ID (int C_Region_ID)
{
set_Value ("C_Region_ID", new Integer(C_Region_ID));
}
/** Get Region.
Identifies a geographical Region */
public int getC_Region_ID() 
{
Integer ii = (Integer)get_Value("C_Region_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Registration Number */
public void setRegistrationNo (String RegistrationNo)
{
if (RegistrationNo != null && RegistrationNo.length() > 60)
{
log.warning("Length > 60 - truncated");
RegistrationNo = RegistrationNo.substring(0,60);
}
set_Value ("RegistrationNo", RegistrationNo);
}
/** Get Registration Number */
public String getRegistrationNo() 
{
return (String)get_Value("RegistrationNo");
}
}
