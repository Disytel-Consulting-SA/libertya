/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Categoria_Iva
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-05-17 17:47:27.761 */
public class X_C_Categoria_Iva extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Categoria_Iva (Properties ctx, int C_Categoria_Iva_ID, String trxName)
{
super (ctx, C_Categoria_Iva_ID, trxName);
/** if (C_Categoria_Iva_ID == 0)
{
setC_Categoria_Iva_ID (0);
setIsPercepcionLiable (false);
setName (null);
setRequiereCUIT (false);
}
 */
}
/** Load Constructor */
public X_C_Categoria_Iva (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Categoria_Iva");

/** TableName=C_Categoria_Iva */
public static final String Table_Name="C_Categoria_Iva";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Categoria_Iva");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Categoria_Iva[").append(getID()).append("]");
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
/** Set Categoria de IVA */
public void setC_Categoria_Iva_ID (int C_Categoria_Iva_ID)
{
set_ValueNoCheck ("C_Categoria_Iva_ID", new Integer(C_Categoria_Iva_ID));
}
/** Get Categoria de IVA */
public int getC_Categoria_Iva_ID() 
{
Integer ii = (Integer)get_Value("C_Categoria_Iva_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Codigo */
public void setCodigo (int Codigo)
{
set_ValueNoCheck ("Codigo", new Integer(Codigo));
}
/** Get Codigo */
public int getCodigo() 
{
Integer ii = (Integer)get_Value("Codigo");
if (ii == null) return 0;
return ii.intValue();
}
/** Set c_sicore */
public void setc_sicore (String c_sicore)
{
if (c_sicore != null && c_sicore.length() > 2)
{
log.warning("Length > 2 - truncated");
c_sicore = c_sicore.substring(0,2);
}
set_Value ("c_sicore", c_sicore);
}
/** Get c_sicore */
public String getc_sicore() 
{
return (String)get_Value("c_sicore");
}
/** Set Is Percepcion Liable */
public void setIsPercepcionLiable (boolean IsPercepcionLiable)
{
set_Value ("IsPercepcionLiable", new Boolean(IsPercepcionLiable));
}
/** Get Is Percepcion Liable */
public boolean isPercepcionLiable() 
{
Object oo = get_Value("IsPercepcionLiable");
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
if (Name.length() > 40)
{
log.warning("Length > 40 - truncated");
Name = Name.substring(0,40);
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
/** Set Requiere CUIT.
Indica que la Entidad Comercial con esta categoría de IVA debe tener un número de CUIT asignado. */
public void setRequiereCUIT (boolean RequiereCUIT)
{
set_Value ("RequiereCUIT", new Boolean(RequiereCUIT));
}
/** Get Requiere CUIT.
Indica que la Entidad Comercial con esta categoría de IVA debe tener un número de CUIT asignado. */
public boolean isRequiereCUIT() 
{
Object oo = get_Value("RequiereCUIT");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
