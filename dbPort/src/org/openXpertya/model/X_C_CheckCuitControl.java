/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CheckCuitControl
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-09-13 11:20:22.941 */
public class X_C_CheckCuitControl extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CheckCuitControl (Properties ctx, int C_CheckCuitControl_ID, String trxName)
{
super (ctx, C_CheckCuitControl_ID, trxName);
/** if (C_CheckCuitControl_ID == 0)
{
setC_CheckCuitControl_ID (0);
setCheckLimit (Env.ZERO);
setCUIT (null);
}
 */
}
/** Load Constructor */
public X_C_CheckCuitControl (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CheckCuitControl");

/** TableName=C_CheckCuitControl */
public static final String Table_Name="C_CheckCuitControl";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CheckCuitControl");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CheckCuitControl[").append(getID()).append("]");
return sb.toString();
}
/** Set C_CheckCuitControl_ID */
public void setC_CheckCuitControl_ID (int C_CheckCuitControl_ID)
{
set_ValueNoCheck ("C_CheckCuitControl_ID", new Integer(C_CheckCuitControl_ID));
}
/** Get C_CheckCuitControl_ID */
public int getC_CheckCuitControl_ID() 
{
Integer ii = (Integer)get_Value("C_CheckCuitControl_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CheckLimit */
public void setCheckLimit (BigDecimal CheckLimit)
{
if (CheckLimit == null) throw new IllegalArgumentException ("CheckLimit is mandatory");
set_Value ("CheckLimit", CheckLimit);
}
/** Get CheckLimit */
public BigDecimal getCheckLimit() 
{
BigDecimal bd = (BigDecimal)get_Value("CheckLimit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CUIT */
public void setCUIT (String CUIT)
{
if (CUIT == null) throw new IllegalArgumentException ("CUIT is mandatory");
if (CUIT.length() > 20)
{
log.warning("Length > 20 - truncated");
CUIT = CUIT.substring(0,20);
}
set_Value ("CUIT", CUIT);
}
/** Get CUIT */
public String getCUIT() 
{
return (String)get_Value("CUIT");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getCUIT());
}
/** Set Nombre */
public void setNombre (String Nombre)
{
if (Nombre != null && Nombre.length() > 60)
{
log.warning("Length > 60 - truncated");
Nombre = Nombre.substring(0,60);
}
set_Value ("Nombre", Nombre);
}
/** Get Nombre */
public String getNombre() 
{
return (String)get_Value("Nombre");
}
}
