/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Norma
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:30.656 */
public class X_C_Norma extends PO
{
/** Constructor estándar */
public X_C_Norma (Properties ctx, int C_Norma_ID, String trxName)
{
super (ctx, C_Norma_ID, trxName);
/** if (C_Norma_ID == 0)
{
setC_Norma_ID (0);
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_Norma (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000093 */
public static final int Table_ID=1000093;

/** TableName=C_Norma */
public static final String Table_Name="C_Norma";

protected static KeyNamePair Model = new KeyNamePair(1000093,"C_Norma");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Norma[").append(getID()).append("]");
return sb.toString();
}
/** Set C_Norma_ID */
public void setC_Norma_ID (int C_Norma_ID)
{
set_ValueNoCheck ("C_Norma_ID", new Integer(C_Norma_ID));
}
/** Get C_Norma_ID */
public int getC_Norma_ID() 
{
Integer ii = (Integer)get_Value("C_Norma_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CONCEPTO_ORDEN_AD_Reference_ID=1000060;
/** Nomina = 1 */
public static final String CONCEPTO_ORDEN_Nomina = "1";
/** Pension = 8 */
public static final String CONCEPTO_ORDEN_Pension = "8";
/** Otros Conceptos = 9 */
public static final String CONCEPTO_ORDEN_OtrosConceptos = "9";
/** Set Concepto_Orden */
public void setConcepto_Orden (String Concepto_Orden)
{
if (Concepto_Orden == null || Concepto_Orden.equals("1") || Concepto_Orden.equals("8") || Concepto_Orden.equals("9"));
 else throw new IllegalArgumentException ("Concepto_Orden Invalid value - Reference_ID=1000060 - 1 - 8 - 9");
if (Concepto_Orden != null && Concepto_Orden.length() > 1)
{
log.warning("Length > 1 - truncated");
Concepto_Orden = Concepto_Orden.substring(0,0);
}
set_Value ("Concepto_Orden", Concepto_Orden);
}
/** Get Concepto_Orden */
public String getConcepto_Orden() 
{
return (String)get_Value("Concepto_Orden");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Domiciliado */
public void setDomiciliado (boolean Domiciliado)
{
set_Value ("Domiciliado", new Boolean(Domiciliado));
}
/** Get Domiciliado */
public boolean isDomiciliado() 
{
Object oo = get_Value("Domiciliado");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Domicilio_Deudor */
public void setDomicilio_Deudor (String Domicilio_Deudor)
{
if (Domicilio_Deudor != null && Domicilio_Deudor.length() > 60)
{
log.warning("Length > 60 - truncated");
Domicilio_Deudor = Domicilio_Deudor.substring(0,59);
}
set_Value ("Domicilio_Deudor", Domicilio_Deudor);
}
/** Get Domicilio_Deudor */
public String getDomicilio_Deudor() 
{
return (String)get_Value("Domicilio_Deudor");
}
public static final int GASTOS_AD_Reference_ID=1000061;
/** Ordenante = 1 */
public static final String GASTOS_Ordenante = "1";
/** Beneficiario = 2 */
public static final String GASTOS_Beneficiario = "2";
/** Set Gastos */
public void setGastos (String Gastos)
{
if (Gastos == null || Gastos.equals("1") || Gastos.equals("2"));
 else throw new IllegalArgumentException ("Gastos Invalid value - Reference_ID=1000061 - 1 - 2");
if (Gastos != null && Gastos.length() > 1)
{
log.warning("Length > 1 - truncated");
Gastos = Gastos.substring(0,0);
}
set_Value ("Gastos", Gastos);
}
/** Get Gastos */
public String getGastos() 
{
return (String)get_Value("Gastos");
}
/** Set IsSOTrx */
public void setIsSOTrx (boolean IsSOTrx)
{
set_Value ("IsSOTrx", new Boolean(IsSOTrx));
}
/** Get IsSOTrx */
public boolean isSOTrx() 
{
Object oo = get_Value("IsSOTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
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
return new KeyNamePair(getID(), getName());
}
/** Set Plaza_Deudor */
public void setPlaza_Deudor (String Plaza_Deudor)
{
if (Plaza_Deudor != null && Plaza_Deudor.length() > 60)
{
log.warning("Length > 60 - truncated");
Plaza_Deudor = Plaza_Deudor.substring(0,59);
}
set_Value ("Plaza_Deudor", Plaza_Deudor);
}
/** Get Plaza_Deudor */
public String getPlaza_Deudor() 
{
return (String)get_Value("Plaza_Deudor");
}
/** Set Postal_Code */
public void setPostal_Code (String Postal_Code)
{
if (Postal_Code != null && Postal_Code.length() > 20)
{
log.warning("Length > 20 - truncated");
Postal_Code = Postal_Code.substring(0,19);
}
set_Value ("Postal_Code", Postal_Code);
}
/** Get Postal_Code */
public String getPostal_Code() 
{
return (String)get_Value("Postal_Code");
}
public static final int SUBNORMA_AD_Reference_ID=1000059;
/** 01 = 01 */
public static final String SUBNORMA_01 = "01";
/** 02 = 02 */
public static final String SUBNORMA_02 = "02";
/** Set SubNorma */
public void setSubNorma (String SubNorma)
{
if (SubNorma == null || SubNorma.equals("01") || SubNorma.equals("02"));
 else throw new IllegalArgumentException ("SubNorma Invalid value - Reference_ID=1000059 - 01 - 02");
if (SubNorma != null && SubNorma.length() > 2)
{
log.warning("Length > 2 - truncated");
SubNorma = SubNorma.substring(0,1);
}
set_Value ("SubNorma", SubNorma);
}
/** Get SubNorma */
public String getSubNorma() 
{
return (String)get_Value("SubNorma");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 60)
{
log.warning("Length > 60 - truncated");
Value = Value.substring(0,59);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
/** Set adeudo */
public void setadeudo (BigDecimal adeudo)
{
set_Value ("adeudo", adeudo);
}
/** Get adeudo */
public BigDecimal getadeudo() 
{
BigDecimal bd = (BigDecimal)get_Value("adeudo");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set cod_ordenante */
public void setcod_ordenante (String cod_ordenante)
{
if (cod_ordenante != null && cod_ordenante.length() > 60)
{
log.warning("Length > 60 - truncated");
cod_ordenante = cod_ordenante.substring(0,59);
}
set_Value ("cod_ordenante", cod_ordenante);
}
/** Get cod_ordenante */
public String getcod_ordenante() 
{
return (String)get_Value("cod_ordenante");
}
/** Set cod_presentador */
public void setcod_presentador (String cod_presentador)
{
if (cod_presentador != null && cod_presentador.length() > 60)
{
log.warning("Length > 60 - truncated");
cod_presentador = cod_presentador.substring(0,59);
}
set_Value ("cod_presentador", cod_presentador);
}
/** Get cod_presentador */
public String getcod_presentador() 
{
return (String)get_Value("cod_presentador");
}
/** Set n_cabecera_ordenante */
public void setn_cabecera_ordenante (String n_cabecera_ordenante)
{
if (n_cabecera_ordenante != null && n_cabecera_ordenante.length() > 22)
{
log.warning("Length > 22 - truncated");
n_cabecera_ordenante = n_cabecera_ordenante.substring(0,21);
}
set_Value ("n_cabecera_ordenante", n_cabecera_ordenante);
}
/** Get n_cabecera_ordenante */
public String getn_cabecera_ordenante() 
{
return (String)get_Value("n_cabecera_ordenante");
}
/** Set n_cabecera_presentador */
public void setn_cabecera_presentador (String n_cabecera_presentador)
{
if (n_cabecera_presentador != null && n_cabecera_presentador.length() > 22)
{
log.warning("Length > 22 - truncated");
n_cabecera_presentador = n_cabecera_presentador.substring(0,21);
}
set_Value ("n_cabecera_presentador", n_cabecera_presentador);
}
/** Get n_cabecera_presentador */
public String getn_cabecera_presentador() 
{
return (String)get_Value("n_cabecera_presentador");
}
/** Set n_euros */
public void setn_euros (String n_euros)
{
if (n_euros != null && n_euros.length() > 22)
{
log.warning("Length > 22 - truncated");
n_euros = n_euros.substring(0,21);
}
set_Value ("n_euros", n_euros);
}
/** Get n_euros */
public String getn_euros() 
{
return (String)get_Value("n_euros");
}
/** Set n_reg_individual */
public void setn_reg_individual (String n_reg_individual)
{
if (n_reg_individual != null && n_reg_individual.length() > 22)
{
log.warning("Length > 22 - truncated");
n_reg_individual = n_reg_individual.substring(0,21);
}
set_Value ("n_reg_individual", n_reg_individual);
}
/** Get n_reg_individual */
public String getn_reg_individual() 
{
return (String)get_Value("n_reg_individual");
}
/** Set n_total_general */
public void setn_total_general (String n_total_general)
{
if (n_total_general != null && n_total_general.length() > 22)
{
log.warning("Length > 22 - truncated");
n_total_general = n_total_general.substring(0,21);
}
set_Value ("n_total_general", n_total_general);
}
/** Get n_total_general */
public String getn_total_general() 
{
return (String)get_Value("n_total_general");
}
/** Set n_total_ordenante */
public void setn_total_ordenante (String n_total_ordenante)
{
if (n_total_ordenante != null && n_total_ordenante.length() > 22)
{
log.warning("Length > 22 - truncated");
n_total_ordenante = n_total_ordenante.substring(0,21);
}
set_Value ("n_total_ordenante", n_total_ordenante);
}
/** Get n_total_ordenante */
public String getn_total_ordenante() 
{
return (String)get_Value("n_total_ordenante");
}
}
