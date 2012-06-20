/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_DesktopWorkbench
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:22.75 */
public class X_AD_DesktopWorkbench extends PO
{
/** Constructor estándar */
public X_AD_DesktopWorkbench (Properties ctx, int AD_DesktopWorkbench_ID, String trxName)
{
super (ctx, AD_DesktopWorkbench_ID, trxName);
/** if (AD_DesktopWorkbench_ID == 0)
{
setAD_DesktopWorkbench_ID (0);
setAD_Desktop_ID (0);
setAD_Workbench_ID (0);
setSeqNo (0);
}
 */
}
/** Load Constructor */
public X_AD_DesktopWorkbench (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=459 */
public static final int Table_ID=459;

/** TableName=AD_DesktopWorkbench */
public static final String Table_Name="AD_DesktopWorkbench";

protected static KeyNamePair Model = new KeyNamePair(459,"AD_DesktopWorkbench");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_DesktopWorkbench[").append(getID()).append("]");
return sb.toString();
}
/** Set Desktop Workbench */
public void setAD_DesktopWorkbench_ID (int AD_DesktopWorkbench_ID)
{
set_ValueNoCheck ("AD_DesktopWorkbench_ID", new Integer(AD_DesktopWorkbench_ID));
}
/** Get Desktop Workbench */
public int getAD_DesktopWorkbench_ID() 
{
Integer ii = (Integer)get_Value("AD_DesktopWorkbench_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Desktop.
Collection of Workbenches */
public void setAD_Desktop_ID (int AD_Desktop_ID)
{
set_ValueNoCheck ("AD_Desktop_ID", new Integer(AD_Desktop_ID));
}
/** Get Desktop.
Collection of Workbenches */
public int getAD_Desktop_ID() 
{
Integer ii = (Integer)get_Value("AD_Desktop_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Workbench.
Collection of windows, reports */
public void setAD_Workbench_ID (int AD_Workbench_ID)
{
set_Value ("AD_Workbench_ID", new Integer(AD_Workbench_ID));
}
/** Get Workbench.
Collection of windows, reports */
public int getAD_Workbench_ID() 
{
Integer ii = (Integer)get_Value("AD_Workbench_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_Workbench_ID()));
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
}
