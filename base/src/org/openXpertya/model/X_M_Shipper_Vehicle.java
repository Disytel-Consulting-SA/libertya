/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Shipper_Vehicle
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2023-10-01 14:12:59.639 */
public class X_M_Shipper_Vehicle extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Shipper_Vehicle (Properties ctx, int M_Shipper_Vehicle_ID, String trxName)
{
super (ctx, M_Shipper_Vehicle_ID, trxName);
/** if (M_Shipper_Vehicle_ID == 0)
{
setLicense (null);
setM_Shipper_ID (0);
setM_Shipper_Vehicle_ID (0);
setTrailer (false);
setVehicle_Name (null);
}
 */
}
/** Load Constructor */
public X_M_Shipper_Vehicle (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Shipper_Vehicle");

/** TableName=M_Shipper_Vehicle */
public static final String Table_Name="M_Shipper_Vehicle";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Shipper_Vehicle");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Shipper_Vehicle[").append(getID()).append("]");
return sb.toString();
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
/** Set License */
public void setLicense (String License)
{
if (License == null) throw new IllegalArgumentException ("License is mandatory");
if (License.length() > 7)
{
log.warning("Length > 7 - truncated");
License = License.substring(0,7);
}
set_Value ("License", License);
}
/** Get License */
public String getLicense() 
{
return (String)get_Value("License");
}
/** Set Shipper.
Method or manner of product delivery */
public void setM_Shipper_ID (int M_Shipper_ID)
{
set_Value ("M_Shipper_ID", new Integer(M_Shipper_ID));
}
/** Get Shipper.
Method or manner of product delivery */
public int getM_Shipper_ID() 
{
Integer ii = (Integer)get_Value("M_Shipper_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_Shipper_Vehicle_ID */
public void setM_Shipper_Vehicle_ID (int M_Shipper_Vehicle_ID)
{
set_ValueNoCheck ("M_Shipper_Vehicle_ID", new Integer(M_Shipper_Vehicle_ID));
}
/** Get M_Shipper_Vehicle_ID */
public int getM_Shipper_Vehicle_ID() 
{
Integer ii = (Integer)get_Value("M_Shipper_Vehicle_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Trailer */
public void setTrailer (boolean Trailer)
{
set_Value ("Trailer", new Boolean(Trailer));
}
/** Get Trailer */
public boolean isTrailer() 
{
Object oo = get_Value("Trailer");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Vechicle Name */
public void setVehicle_Name (String Vehicle_Name)
{
if (Vehicle_Name == null) throw new IllegalArgumentException ("Vehicle_Name is mandatory");
if (Vehicle_Name.length() > 40)
{
log.warning("Length > 40 - truncated");
Vehicle_Name = Vehicle_Name.substring(0,40);
}
set_Value ("Vehicle_Name", Vehicle_Name);
}
/** Get Vechicle Name */
public String getVehicle_Name() 
{
return (String)get_Value("Vehicle_Name");
}
}
