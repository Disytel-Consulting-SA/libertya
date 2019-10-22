/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_Padron_MiPyme
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-10-21 13:15:31.927 */
public class X_I_Padron_MiPyme extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_Padron_MiPyme (Properties ctx, int I_Padron_MiPyme_ID, String trxName)
{
super (ctx, I_Padron_MiPyme_ID, trxName);
/** if (I_Padron_MiPyme_ID == 0)
{
setI_IsImported (false);
setI_Padron_MiPyme_ID (0);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_I_Padron_MiPyme (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_Padron_MiPyme");

/** TableName=I_Padron_MiPyme */
public static final String Table_Name="I_Padron_MiPyme";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_Padron_MiPyme");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_Padron_MiPyme[").append(getID()).append("]");
return sb.toString();
}
/** Set Actividad */
public void setActividad (int Actividad)
{
set_Value ("Actividad", new Integer(Actividad));
}
/** Get Actividad */
public int getActividad() 
{
Integer ii = (Integer)get_Value("Actividad");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CUIT */
public void setCUIT (String CUIT)
{
if (CUIT != null && CUIT.length() > 20)
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
/** Set Denominacion */
public void setDenominacion (String Denominacion)
{
if (Denominacion != null && Denominacion.length() > 60)
{
log.warning("Length > 60 - truncated");
Denominacion = Denominacion.substring(0,60);
}
set_Value ("Denominacion", Denominacion);
}
/** Get Denominacion */
public String getDenominacion() 
{
return (String)get_Value("Denominacion");
}
/** Set Fecha Inicio */
public void setFecha_Inicio (Timestamp Fecha_Inicio)
{
set_Value ("Fecha_Inicio", Fecha_Inicio);
}
/** Get Fecha Inicio */
public Timestamp getFecha_Inicio() 
{
return (Timestamp)get_Value("Fecha_Inicio");
}
/** Set Import Error Message.
Messages generated from import process */
public void setI_ErrorMsg (String I_ErrorMsg)
{
if (I_ErrorMsg != null && I_ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
I_ErrorMsg = I_ErrorMsg.substring(0,2000);
}
set_Value ("I_ErrorMsg", I_ErrorMsg);
}
/** Get Import Error Message.
Messages generated from import process */
public String getI_ErrorMsg() 
{
return (String)get_Value("I_ErrorMsg");
}
/** Set Imported.
Has this import been processed */
public void setI_IsImported (boolean I_IsImported)
{
set_Value ("I_IsImported", new Boolean(I_IsImported));
}
/** Get Imported.
Has this import been processed */
public boolean isI_IsImported() 
{
Object oo = get_Value("I_IsImported");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set I_Padron_MiPyme_ID */
public void setI_Padron_MiPyme_ID (int I_Padron_MiPyme_ID)
{
set_ValueNoCheck ("I_Padron_MiPyme_ID", new Integer(I_Padron_MiPyme_ID));
}
/** Get I_Padron_MiPyme_ID */
public int getI_Padron_MiPyme_ID() 
{
Integer ii = (Integer)get_Value("I_Padron_MiPyme_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
