/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_Padron_BsAs
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-10-27 01:43:37.834 */
public class X_C_BPartner_Padron_BsAs extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BPartner_Padron_BsAs (Properties ctx, int C_BPartner_Padron_BsAs_ID, String trxName)
{
super (ctx, C_BPartner_Padron_BsAs_ID, trxName);
/** if (C_BPartner_Padron_BsAs_ID == 0)
{
setc_bpartner_padron_bsas_id (0);
}
 */
}
/** Load Constructor */
public X_C_BPartner_Padron_BsAs (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BPartner_Padron_BsAs");

/** TableName=C_BPartner_Padron_BsAs */
public static final String Table_Name="C_BPartner_Padron_BsAs";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BPartner_Padron_BsAs");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_Padron_BsAs[").append(getID()).append("]");
return sb.toString();
}
/** Set alta_baja */
public void setalta_baja (String alta_baja)
{
if (alta_baja != null && alta_baja.length() > 1)
{
log.warning("Length > 1 - truncated");
alta_baja = alta_baja.substring(0,1);
}
set_Value ("alta_baja", alta_baja);
}
/** Get alta_baja */
public String getalta_baja() 
{
return (String)get_Value("alta_baja");
}
/** Set cbio_alicuota */
public void setcbio_alicuota (String cbio_alicuota)
{
if (cbio_alicuota != null && cbio_alicuota.length() > 1)
{
log.warning("Length > 1 - truncated");
cbio_alicuota = cbio_alicuota.substring(0,1);
}
set_Value ("cbio_alicuota", cbio_alicuota);
}
/** Get cbio_alicuota */
public String getcbio_alicuota() 
{
return (String)get_Value("cbio_alicuota");
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
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
/** Set c_bpartner_padron_bsas_id */
public void setc_bpartner_padron_bsas_id (int c_bpartner_padron_bsas_id)
{
set_ValueNoCheck ("c_bpartner_padron_bsas_id", new Integer(c_bpartner_padron_bsas_id));
}
/** Get c_bpartner_padron_bsas_id */
public int getc_bpartner_padron_bsas_id() 
{
Integer ii = (Integer)get_Value("c_bpartner_padron_bsas_id");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CUIT */
public void setCUIT (String CUIT)
{
if (CUIT != null && CUIT.length() > 11)
{
log.warning("Length > 11 - truncated");
CUIT = CUIT.substring(0,11);
}
set_Value ("CUIT", CUIT);
}
/** Get CUIT */
public String getCUIT() 
{
return (String)get_Value("CUIT");
}
/** Set fecha_desde */
public void setfecha_desde (Timestamp fecha_desde)
{
set_Value ("fecha_desde", fecha_desde);
}
/** Get fecha_desde */
public Timestamp getfecha_desde() 
{
return (Timestamp)get_Value("fecha_desde");
}
/** Set fecha_hasta */
public void setfecha_hasta (Timestamp fecha_hasta)
{
set_Value ("fecha_hasta", fecha_hasta);
}
/** Get fecha_hasta */
public Timestamp getfecha_hasta() 
{
return (Timestamp)get_Value("fecha_hasta");
}
/** Set fecha_publicacion */
public void setfecha_publicacion (Timestamp fecha_publicacion)
{
set_Value ("fecha_publicacion", fecha_publicacion);
}
/** Get fecha_publicacion */
public Timestamp getfecha_publicacion() 
{
return (Timestamp)get_Value("fecha_publicacion");
}
/** Set nro_grupo_per */
public void setnro_grupo_per (BigDecimal nro_grupo_per)
{
set_Value ("nro_grupo_per", nro_grupo_per);
}
/** Get nro_grupo_per */
public BigDecimal getnro_grupo_per() 
{
BigDecimal bd = (BigDecimal)get_Value("nro_grupo_per");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set nro_grupo_ret */
public void setnro_grupo_ret (BigDecimal nro_grupo_ret)
{
set_Value ("nro_grupo_ret", nro_grupo_ret);
}
/** Get nro_grupo_ret */
public BigDecimal getnro_grupo_ret() 
{
BigDecimal bd = (BigDecimal)get_Value("nro_grupo_ret");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int PADRONTYPE_AD_Reference_ID = MReference.getReferenceID("Tipos de Padrón");
/** Padrón Bs As = B */
public static final String PADRONTYPE_PadrónBsAs = "B";
/** Set Padron Type */
public void setPadronType (String PadronType)
{
if (PadronType == null || PadronType.equals("B"));
 else throw new IllegalArgumentException ("PadronType Invalid value - Reference = PADRONTYPE_AD_Reference_ID - B");
if (PadronType != null && PadronType.length() > 1)
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
/** Set percepcion */
public void setpercepcion (BigDecimal percepcion)
{
set_Value ("percepcion", percepcion);
}
/** Get percepcion */
public BigDecimal getpercepcion() 
{
BigDecimal bd = (BigDecimal)get_Value("percepcion");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set retencion */
public void setretencion (BigDecimal retencion)
{
set_Value ("retencion", retencion);
}
/** Get retencion */
public BigDecimal getretencion() 
{
BigDecimal bd = (BigDecimal)get_Value("retencion");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set tipo_contr_insc */
public void settipo_contr_insc (String tipo_contr_insc)
{
if (tipo_contr_insc != null && tipo_contr_insc.length() > 1)
{
log.warning("Length > 1 - truncated");
tipo_contr_insc = tipo_contr_insc.substring(0,1);
}
set_Value ("tipo_contr_insc", tipo_contr_insc);
}
/** Get tipo_contr_insc */
public String gettipo_contr_insc() 
{
return (String)get_Value("tipo_contr_insc");
}
}
