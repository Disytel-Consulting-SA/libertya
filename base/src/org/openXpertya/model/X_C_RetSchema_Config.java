/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RetSchema_Config
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2021-12-20 13:54:07.998 */
public class X_C_RetSchema_Config extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_RetSchema_Config (Properties ctx, int C_RetSchema_Config_ID, String trxName)
{
super (ctx, C_RetSchema_Config_ID, trxName);
/** if (C_RetSchema_Config_ID == 0)
{
setC_RetencionSchema_ID (0);
setC_RetSchema_Config_ID (0);
setIs_Range (false);
setName (null);
setOrden (0);	// 1
setPadronType (null);
setParamType (null);
setValor (null);
}
 */
}
/** Load Constructor */
public X_C_RetSchema_Config (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_RetSchema_Config");

/** TableName=C_RetSchema_Config */
public static final String Table_Name="C_RetSchema_Config";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_RetSchema_Config");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RetSchema_Config[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
/** Set Base Amount */
public void setBaseAmt (BigDecimal BaseAmt)
{
set_Value ("BaseAmt", BaseAmt);
}
/** Get Base Amount */
public BigDecimal getBaseAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("BaseAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Retencion Schema */
public void setC_RetencionSchema_ID (int C_RetencionSchema_ID)
{
set_ValueNoCheck ("C_RetencionSchema_ID", new Integer(C_RetencionSchema_ID));
}
/** Get Retencion Schema */
public int getC_RetencionSchema_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Schema Config */
public void setC_RetSchema_Config_ID (int C_RetSchema_Config_ID)
{
set_ValueNoCheck ("C_RetSchema_Config_ID", new Integer(C_RetSchema_Config_ID));
}
/** Get Retencion Schema Config */
public int getC_RetSchema_Config_ID() 
{
Integer ii = (Integer)get_Value("C_RetSchema_Config_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Is Range */
public void setIs_Range (boolean Is_Range)
{
set_Value ("Is_Range", new Boolean(Is_Range));
}
/** Get Is Range */
public boolean is_Range() 
{
Object oo = get_Value("Is_Range");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int NAME_AD_Reference_ID = MReference.getReferenceID("RetConfig_ParamName");
/** Descuento Neto = N */
public static final String NAME_DescuentoNeto = "N";
/** Desde Padron = P */
public static final String NAME_DesdePadron = "P";
/** Importe No Imponible = INI */
public static final String NAME_ImporteNoImponible = "INI";
/** Porcentaje a Retener = T */
public static final String NAME_PorcentajeARetener = "T";
/** Minimo a Retener = MR */
public static final String NAME_MinimoARetener = "MR";
/** Importe Fijo = IF */
public static final String NAME_ImporteFijo = "IF";
/** Excedente De = E */
public static final String NAME_ExcedenteDe = "E";
/** Porcentaje del Excedente = PE */
public static final String NAME_PorcentajeDelExcedente = "PE";
/** Por Lugar de Entrega = LE */
public static final String NAME_PorLugarDeEntrega = "LE";
/** Por Region Origen y Destino = OD */
public static final String NAME_PorRegionOrigenYDestino = "OD";
/** Minimum Net Amount = MNA */
public static final String NAME_MinimumNetAmount = "MNA";
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name.equals("N") || Name.equals("P") || Name.equals("INI") || Name.equals("T") || Name.equals("MR") || Name.equals("IF") || Name.equals("E") || Name.equals("PE") || Name.equals("LE") || Name.equals("OD") || Name.equals("MNA") || ( refContainsValue("CORE-AD_Reference-1000082", Name) ) );
 else throw new IllegalArgumentException ("Name Invalid value: " + Name + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1000082") );
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 30)
{
log.warning("Length > 30 - truncated");
Name = Name.substring(0,30);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getName()));
}
/** Set Orden */
public void setOrden (int Orden)
{
set_Value ("Orden", new Integer(Orden));
}
/** Get Orden */
public int getOrden() 
{
Integer ii = (Integer)get_Value("Orden");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PADRONTYPE_AD_Reference_ID = MReference.getReferenceID("Tipos de Padrón");
/** Padrón Bs As = B */
public static final String PADRONTYPE_PadrónBsAs = "B";
/** Padrón de Alto Riesgo CABA = A */
public static final String PADRONTYPE_PadrónDeAltoRiesgoCABA = "A";
/** Régimen Simplificado CABA = S */
public static final String PADRONTYPE_RégimenSimplificadoCABA = "S";
/** Padrón de Regímenes Generales = G */
public static final String PADRONTYPE_PadrónDeRegímenesGenerales = "G";
/** Coeficientes Tucuman = C */
public static final String PADRONTYPE_CoeficientesTucuman = "C";
/** Padron Contribuyentes Tucuman = T */
public static final String PADRONTYPE_PadronContribuyentesTucuman = "T";
/** Set Padron Type */
public void setPadronType (String PadronType)
{
if (PadronType.equals("B") || PadronType.equals("A") || PadronType.equals("S") || PadronType.equals("G") || PadronType.equals("C") || PadronType.equals("T") || ( refContainsValue("CORE-AD_Reference-1010198", PadronType) ) );
 else throw new IllegalArgumentException ("PadronType Invalid value: " + PadronType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010198") );
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
public static final int PARAMTYPE_AD_Reference_ID = MReference.getReferenceID("Rango Retención");
/** Rango = RA */
public static final String PARAMTYPE_Rango = "RA";
/** Valor = VA */
public static final String PARAMTYPE_Valor = "VA";
/** Set Param Type */
public void setParamType (String ParamType)
{
if (ParamType.equals("RA") || ParamType.equals("VA") || ( refContainsValue("CORE-AD_Reference-1000071", ParamType) ) );
 else throw new IllegalArgumentException ("ParamType Invalid value: " + ParamType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1000071") );
if (ParamType == null) throw new IllegalArgumentException ("ParamType is mandatory");
if (ParamType.length() > 3)
{
log.warning("Length > 3 - truncated");
ParamType = ParamType.substring(0,3);
}
set_Value ("ParamType", ParamType);
}
/** Get Param Type */
public String getParamType() 
{
return (String)get_Value("ParamType");
}
/** Set Value */
public void setValor (String Valor)
{
if (Valor == null) throw new IllegalArgumentException ("Valor is mandatory");
if (Valor.length() > 20)
{
log.warning("Length > 20 - truncated");
Valor = Valor.substring(0,20);
}
set_Value ("Valor", Valor);
}
/** Get Value */
public String getValor() 
{
return (String)get_Value("Valor");
}
}
