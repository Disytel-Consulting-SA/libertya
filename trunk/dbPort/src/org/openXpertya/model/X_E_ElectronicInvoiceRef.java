/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por E_ElectronicInvoiceRef
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-03-12 12:10:16.217 */
public class X_E_ElectronicInvoiceRef extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_E_ElectronicInvoiceRef (Properties ctx, int E_ElectronicInvoiceRef_ID, String trxName)
{
super (ctx, E_ElectronicInvoiceRef_ID, trxName);
/** if (E_ElectronicInvoiceRef_ID == 0)
{
setClave_Busqueda (null);
setCodigo (null);
setE_ElectronicInvoiceRef_ID (0);
setTabla_Ref (null);
}
 */
}
/** Load Constructor */
public X_E_ElectronicInvoiceRef (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("E_ElectronicInvoiceRef");

/** TableName=E_ElectronicInvoiceRef */
public static final String Table_Name="E_ElectronicInvoiceRef";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"E_ElectronicInvoiceRef");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_E_ElectronicInvoiceRef[").append(getID()).append("]");
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
/** Set Clave_Busqueda */
public void setClave_Busqueda (String Clave_Busqueda)
{
if (Clave_Busqueda == null) throw new IllegalArgumentException ("Clave_Busqueda is mandatory");
if (Clave_Busqueda.length() > 50)
{
log.warning("Length > 50 - truncated");
Clave_Busqueda = Clave_Busqueda.substring(0,50);
}
set_Value ("Clave_Busqueda", Clave_Busqueda);
}
/** Get Clave_Busqueda */
public String getClave_Busqueda() 
{
return (String)get_Value("Clave_Busqueda");
}
/** Set Codigo */
public void setCodigo (String Codigo)
{
if (Codigo == null) throw new IllegalArgumentException ("Codigo is mandatory");
if (Codigo.length() > 15)
{
log.warning("Length > 15 - truncated");
Codigo = Codigo.substring(0,15);
}
set_Value ("Codigo", Codigo);
}
/** Get Codigo */
public String getCodigo() 
{
return (String)get_Value("Codigo");
}
/** Set Descripcion */
public void setDescripcion (String Descripcion)
{
if (Descripcion != null && Descripcion.length() > 30)
{
log.warning("Length > 30 - truncated");
Descripcion = Descripcion.substring(0,30);
}
set_Value ("Descripcion", Descripcion);
}
/** Get Descripcion */
public String getDescripcion() 
{
return (String)get_Value("Descripcion");
}
/** Set Desde */
public void setDesde (Timestamp Desde)
{
set_Value ("Desde", Desde);
}
/** Get Desde */
public Timestamp getDesde() 
{
return (Timestamp)get_Value("Desde");
}
/** Set E_ElectronicInvoiceRef_ID */
public void setE_ElectronicInvoiceRef_ID (int E_ElectronicInvoiceRef_ID)
{
set_ValueNoCheck ("E_ElectronicInvoiceRef_ID", new Integer(E_ElectronicInvoiceRef_ID));
}
/** Get E_ElectronicInvoiceRef_ID */
public int getE_ElectronicInvoiceRef_ID() 
{
Integer ii = (Integer)get_Value("E_ElectronicInvoiceRef_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Hasta */
public void setHasta (Timestamp Hasta)
{
set_Value ("Hasta", Hasta);
}
/** Get Hasta */
public Timestamp getHasta() 
{
return (Timestamp)get_Value("Hasta");
}
/** Set Persona */
public void setPersona (String Persona)
{
if (Persona != null && Persona.length() > 10)
{
log.warning("Length > 10 - truncated");
Persona = Persona.substring(0,10);
}
set_Value ("Persona", Persona);
}
/** Get Persona */
public String getPersona() 
{
return (String)get_Value("Persona");
}
/** Set Tabla_Ref */
public void setTabla_Ref (String Tabla_Ref)
{
if (Tabla_Ref == null) throw new IllegalArgumentException ("Tabla_Ref is mandatory");
if (Tabla_Ref.length() > 4)
{
log.warning("Length > 4 - truncated");
Tabla_Ref = Tabla_Ref.substring(0,4);
}
set_Value ("Tabla_Ref", Tabla_Ref);
}
/** Get Tabla_Ref */
public String getTabla_Ref() 
{
return (String)get_Value("Tabla_Ref");
}
}
