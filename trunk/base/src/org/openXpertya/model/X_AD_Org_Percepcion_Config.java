/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Org_Percepcion_Config
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-09-12 18:05:09.099 */
public class X_AD_Org_Percepcion_Config extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Org_Percepcion_Config (Properties ctx, int AD_Org_Percepcion_Config_ID, String trxName)
{
super (ctx, AD_Org_Percepcion_Config_ID, trxName);
/** if (AD_Org_Percepcion_Config_ID == 0)
{
setAD_Org_Percepcion_Config_ID (0);
setAD_Org_Percepcion_ID (0);
setMinimumNetAmount (Env.ZERO);
setPadronType (null);
}
 */
}
/** Load Constructor */
public X_AD_Org_Percepcion_Config (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Org_Percepcion_Config");

/** TableName=AD_Org_Percepcion_Config */
public static final String Table_Name="AD_Org_Percepcion_Config";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Org_Percepcion_Config");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Org_Percepcion_Config[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_Org_Percepcion_Config_ID */
public void setAD_Org_Percepcion_Config_ID (int AD_Org_Percepcion_Config_ID)
{
set_ValueNoCheck ("AD_Org_Percepcion_Config_ID", new Integer(AD_Org_Percepcion_Config_ID));
}
/** Get AD_Org_Percepcion_Config_ID */
public int getAD_Org_Percepcion_Config_ID() 
{
Integer ii = (Integer)get_Value("AD_Org_Percepcion_Config_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_Org_Percepcion_ID */
public void setAD_Org_Percepcion_ID (int AD_Org_Percepcion_ID)
{
set_ValueNoCheck ("AD_Org_Percepcion_ID", new Integer(AD_Org_Percepcion_ID));
}
/** Get AD_Org_Percepcion_ID */
public int getAD_Org_Percepcion_ID() 
{
Integer ii = (Integer)get_Value("AD_Org_Percepcion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Minimum Net Amount */
public void setMinimumNetAmount (BigDecimal MinimumNetAmount)
{
if (MinimumNetAmount == null) throw new IllegalArgumentException ("MinimumNetAmount is mandatory");
set_Value ("MinimumNetAmount", MinimumNetAmount);
}
/** Get Minimum Net Amount */
public BigDecimal getMinimumNetAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("MinimumNetAmount");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int PADRONTYPE_AD_Reference_ID = MReference.getReferenceID("Tipos de Padrón");
/** Padrón Bs As = B */
public static final String PADRONTYPE_PadrónBsAs = "B";
/** Padrón de Alto Riesgo CABA = A */
public static final String PADRONTYPE_PadrónDeAltoRiesgoCABA = "A";
/** Régimen Simplificado CABA = S */
public static final String PADRONTYPE_RégimenSimplificadoCABA = "S";
/** Set Padron Type */
public void setPadronType (String PadronType)
{
if (PadronType.equals("B") || PadronType.equals("A") || PadronType.equals("S"));
 else throw new IllegalArgumentException ("PadronType Invalid value - Reference = PADRONTYPE_AD_Reference_ID - B - A - S");
if (PadronType == null) throw new IllegalArgumentException ("PadronType is mandatory");
if (PadronType.length() > 1)
{
log.warning("Length > 1 - truncated");
PadronType = PadronType.substring(0,1);
}
set_Value ("PadronType", PadronType);
}
/** Get Padron Type */
public String getPadronType() 
{
return (String)get_Value("PadronType");
}
}
