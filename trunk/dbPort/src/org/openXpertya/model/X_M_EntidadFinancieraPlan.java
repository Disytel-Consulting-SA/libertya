/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_EntidadFinancieraPlan
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-04-21 12:28:58.352 */
public class X_M_EntidadFinancieraPlan extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_EntidadFinancieraPlan (Properties ctx, int M_EntidadFinancieraPlan_ID, String trxName)
{
super (ctx, M_EntidadFinancieraPlan_ID, trxName);
/** if (M_EntidadFinancieraPlan_ID == 0)
{
setAccreditationDays (0);
setCuotasCobro (0);
setCuotasPago (0);
setDateFrom (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDateTo (new Timestamp(System.currentTimeMillis()));
setM_EntidadFinanciera_ID (0);	// @M_EntidadFinanciera_ID@
setM_EntidadFinancieraPlan_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_M_EntidadFinancieraPlan (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_EntidadFinancieraPlan");

/** TableName=M_EntidadFinancieraPlan */
public static final String Table_Name="M_EntidadFinancieraPlan";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_EntidadFinancieraPlan");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_EntidadFinancieraPlan[").append(getID()).append("]");
return sb.toString();
}
/** Set Accreditation Days.
Accreditation Days */
public void setAccreditationDays (int AccreditationDays)
{
set_Value ("AccreditationDays", new Integer(AccreditationDays));
}
/** Get Accreditation Days.
Accreditation Days */
public int getAccreditationDays() 
{
Integer ii = (Integer)get_Value("AccreditationDays");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cuotas Cobro.
Cuotas Cobro */
public void setCuotasCobro (int CuotasCobro)
{
set_Value ("CuotasCobro", new Integer(CuotasCobro));
}
/** Get Cuotas Cobro.
Cuotas Cobro */
public int getCuotasCobro() 
{
Integer ii = (Integer)get_Value("CuotasCobro");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cuotas Pago.
Cuotas Pago */
public void setCuotasPago (int CuotasPago)
{
set_Value ("CuotasPago", new Integer(CuotasPago));
}
/** Get Cuotas Pago.
Cuotas Pago */
public int getCuotasPago() 
{
Integer ii = (Integer)get_Value("CuotasPago");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Date From.
Starting date for a range */
public void setDateFrom (Timestamp DateFrom)
{
if (DateFrom == null) throw new IllegalArgumentException ("DateFrom is mandatory");
set_Value ("DateFrom", DateFrom);
}
/** Get Date From.
Starting date for a range */
public Timestamp getDateFrom() 
{
return (Timestamp)get_Value("DateFrom");
}
/** Set Date To.
End date of a date range */
public void setDateTo (Timestamp DateTo)
{
if (DateTo == null) throw new IllegalArgumentException ("DateTo is mandatory");
set_Value ("DateTo", DateTo);
}
/** Get Date To.
End date of a date range */
public Timestamp getDateTo() 
{
return (Timestamp)get_Value("DateTo");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Discount Schema.
Schema to calculate the trade discount percentage */
public void setM_DiscountSchema_ID (int M_DiscountSchema_ID)
{
if (M_DiscountSchema_ID <= 0) set_Value ("M_DiscountSchema_ID", null);
 else 
set_Value ("M_DiscountSchema_ID", new Integer(M_DiscountSchema_ID));
}
/** Get Discount Schema.
Schema to calculate the trade discount percentage */
public int getM_DiscountSchema_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Entidad Financiera */
public void setM_EntidadFinanciera_ID (int M_EntidadFinanciera_ID)
{
set_Value ("M_EntidadFinanciera_ID", new Integer(M_EntidadFinanciera_ID));
}
/** Get Entidad Financiera */
public int getM_EntidadFinanciera_ID() 
{
Integer ii = (Integer)get_Value("M_EntidadFinanciera_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Plan de Entidad Financiera.
Plan de Entidad Financiera */
public void setM_EntidadFinancieraPlan_ID (int M_EntidadFinancieraPlan_ID)
{
set_ValueNoCheck ("M_EntidadFinancieraPlan_ID", new Integer(M_EntidadFinancieraPlan_ID));
}
/** Get Plan de Entidad Financiera.
Plan de Entidad Financiera */
public int getM_EntidadFinancieraPlan_ID() 
{
Integer ii = (Integer)get_Value("M_EntidadFinancieraPlan_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
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
}
