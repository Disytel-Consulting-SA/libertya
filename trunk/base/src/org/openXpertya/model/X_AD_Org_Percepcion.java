/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
/** Modelo Generado por AD_Org_Percepcion
 *  @author Comunidad de Desarrollo Libertya Basado en Codigo Original Modificado, Revisado y Optimizado de: Jorg Janke 
 *  @version  - 2016-11-24 16:20:43.46 */
public class X_AD_Org_Percepcion extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Org_Percepcion (Properties ctx, int AD_Org_Percepcion_ID, String trxName)
{
super (ctx, AD_Org_Percepcion_ID, trxName);
/** if (AD_Org_Percepcion_ID == 0)
{
setAD_Org_Percepcion_ID (0);
setAlicuota (Env.ZERO);
setC_RetencionProcessor_ID (0);
setC_Tax_ID (0);
setIsConvenioMultilateral (false);
setMinimumNetAmount (Env.ZERO);
setName (null);
setUseCABAJurisdiction (false);
}
*/
}
/** Load Constructor */
public X_AD_Org_Percepcion (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Org_Percepcion");

/** TableName=AD_Org_Percepcion */
public static final String Table_Name="AD_Org_Percepcion";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Org_Percepcion");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Org_Percepcion[").append(getID()).append("]");
return sb.toString();
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
/** Set Alicuota */
public void setAlicuota (BigDecimal Alicuota)
{
if (Alicuota == null) throw new IllegalArgumentException ("Alicuota is mandatory");
set_Value ("Alicuota", Alicuota);
}
/** Get Alicuota */
public BigDecimal getAlicuota() 
{
BigDecimal bd = (BigDecimal)get_Value("Alicuota");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Retencion Processor */
public void setC_RetencionProcessor_ID (int C_RetencionProcessor_ID)
{
set_Value ("C_RetencionProcessor_ID", new Integer(C_RetencionProcessor_ID));
}
/** Get Retencion Processor */
public int getC_RetencionProcessor_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionProcessor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax.
Tax identifier */
public void setC_Tax_ID (int C_Tax_ID)
{
set_Value ("C_Tax_ID", new Integer(C_Tax_ID));
}
/** Get Tax.
Tax identifier */
public int getC_Tax_ID() 
{
Integer ii = (Integer)get_Value("C_Tax_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Convenio Multilateral.
Entidades comerciales en Convenio Multilateral */
public void setIsConvenioMultilateral (boolean IsConvenioMultilateral)
{
set_Value ("IsConvenioMultilateral", new Boolean(IsConvenioMultilateral));
}
/** Get Convenio Multilateral.
Entidades comerciales en Convenio Multilateral */
public boolean isConvenioMultilateral() 
{
Object oo = get_Value("IsConvenioMultilateral");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Use CABA Jurisdiction */
public void setUseCABAJurisdiction (boolean UseCABAJurisdiction)
{
set_Value ("UseCABAJurisdiction", new Boolean(UseCABAJurisdiction));
}
/** Get Use CABA Jurisdiction */
public boolean isUseCABAJurisdiction() 
{
Object oo = get_Value("UseCABAJurisdiction");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
