/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Replenish
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-06-18 14:08:51.843 */
public class X_M_Replenish extends PO
{
/** Constructor estándar */
public X_M_Replenish (Properties ctx, int M_Replenish_ID, String trxName)
{
super (ctx, M_Replenish_ID, trxName);
/** if (M_Replenish_ID == 0)
{
setLevel_Max (Env.ZERO);
setLevel_Min (Env.ZERO);
setM_Product_ID (0);
setM_Warehouse_ID (0);
setReplenishType (null);
}
 */
}
/** Load Constructor */
public X_M_Replenish (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=249 */
public static final int Table_ID=249;

/** TableName=M_Replenish */
public static final String Table_Name="M_Replenish";

protected static KeyNamePair Model = new KeyNamePair(249,"M_Replenish");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Replenish[").append(getID()).append("]");
return sb.toString();
}
/** Set CP */
public void setCP (BigDecimal CP)
{
set_Value ("CP", CP);
}
/** Get CP */
public BigDecimal getCP() 
{
BigDecimal bd = (BigDecimal)get_Value("CP");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CS */
public void setCS (BigDecimal CS)
{
set_Value ("CS", CS);
}
/** Get CS */
public BigDecimal getCS() 
{
BigDecimal bd = (BigDecimal)get_Value("CS");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Maximum Level.
Maximum Inventory level for this product */
public void setLevel_Max (BigDecimal Level_Max)
{
if (Level_Max == null) throw new IllegalArgumentException ("Level_Max is mandatory");
set_Value ("Level_Max", Level_Max);
}
/** Get Maximum Level.
Maximum Inventory level for this product */
public BigDecimal getLevel_Max() 
{
BigDecimal bd = (BigDecimal)get_Value("Level_Max");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Minimum Level.
Minimum Inventory level for this product */
public void setLevel_Min (BigDecimal Level_Min)
{
if (Level_Min == null) throw new IllegalArgumentException ("Level_Min is mandatory");
set_Value ("Level_Min", Level_Min);
}
/** Get Minimum Level.
Minimum Inventory level for this product */
public BigDecimal getLevel_Min() 
{
BigDecimal bd = (BigDecimal)get_Value("Level_Min");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
set_ValueNoCheck ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_ReplenishSystem_ID */
public void setM_ReplenishSystem_ID (int M_ReplenishSystem_ID)
{
if (M_ReplenishSystem_ID <= 0) set_Value ("M_ReplenishSystem_ID", null);
 else 
set_Value ("M_ReplenishSystem_ID", new Integer(M_ReplenishSystem_ID));
}
/** Get M_ReplenishSystem_ID */
public int getM_ReplenishSystem_ID() 
{
Integer ii = (Integer)get_Value("M_ReplenishSystem_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_ValueNoCheck ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Parametro1 */
public void setParametro1 (String Parametro1)
{
if (Parametro1 != null && Parametro1.length() > 60)
{
log.warning("Length > 60 - truncated");
Parametro1 = Parametro1.substring(0,60);
}
set_Value ("Parametro1", Parametro1);
}
/** Get Parametro1 */
public String getParametro1() 
{
return (String)get_Value("Parametro1");
}
/** Set Parametro2 */
public void setParametro2 (String Parametro2)
{
if (Parametro2 != null && Parametro2.length() > 60)
{
log.warning("Length > 60 - truncated");
Parametro2 = Parametro2.substring(0,60);
}
set_Value ("Parametro2", Parametro2);
}
/** Get Parametro2 */
public String getParametro2() 
{
return (String)get_Value("Parametro2");
}
/** Set Parametro3 */
public void setParametro3 (String Parametro3)
{
if (Parametro3 != null && Parametro3.length() > 60)
{
log.warning("Length > 60 - truncated");
Parametro3 = Parametro3.substring(0,60);
}
set_Value ("Parametro3", Parametro3);
}
/** Get Parametro3 */
public String getParametro3() 
{
return (String)get_Value("Parametro3");
}
/** Set Processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed */
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
/** Set R */
public void setR (BigDecimal R)
{
set_Value ("R", R);
}
/** Get R */
public BigDecimal getR() 
{
BigDecimal bd = (BigDecimal)get_Value("R");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int REPLENISHTYPE_AD_Reference_ID=164;
/** Maintain Maximum Level = 2 */
public static final String REPLENISHTYPE_MaintainMaximumLevel = "2";
/** Manual = 0 */
public static final String REPLENISHTYPE_Manual = "0";
/** Reorder below Minimum Level = 1 */
public static final String REPLENISHTYPE_ReorderBelowMinimumLevel = "1";
/** Automatic Replenish = 3 */
public static final String REPLENISHTYPE_AutomaticReplenish = "3";
/** Automatic Replenish(all) = 4 */
public static final String REPLENISHTYPE_AutomaticReplenishAll = "4";
/** Advanced Replenish = 5 */
public static final String REPLENISHTYPE_AdvancedReplenish = "5";
/** Set Replenish Type.
Method for re-ordering a product */
public void setReplenishType (String ReplenishType)
{
if (ReplenishType.equals("2") || ReplenishType.equals("0") || ReplenishType.equals("1") || ReplenishType.equals("3") || ReplenishType.equals("4") || ReplenishType.equals("5"));
 else throw new IllegalArgumentException ("ReplenishType Invalid value - Reference_ID=164 - 2 - 0 - 1 - 3 - 4 - 5");
if (ReplenishType == null) throw new IllegalArgumentException ("ReplenishType is mandatory");
if (ReplenishType.length() > 1)
{
log.warning("Length > 1 - truncated");
ReplenishType = ReplenishType.substring(0,1);
}
set_Value ("ReplenishType", ReplenishType);
}
/** Get Replenish Type.
Method for re-ordering a product */
public String getReplenishType() 
{
return (String)get_Value("ReplenishType");
}
/** Set SS */
public void setSS (BigDecimal SS)
{
set_Value ("SS", SS);
}
/** Get SS */
public BigDecimal getSS() 
{
BigDecimal bd = (BigDecimal)get_Value("SS");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set T */
public void setT (BigDecimal T)
{
set_Value ("T", T);
}
/** Get T */
public BigDecimal getT() 
{
BigDecimal bd = (BigDecimal)get_Value("T");
if (bd == null) return Env.ZERO;
return bd;
}
}
