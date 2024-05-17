/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import org.openXpertya.model.*;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Jacofer_RoadMapLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2020-01-09 16:44:51.276 */
public class LP_M_Jacofer_RoadMapLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public LP_M_Jacofer_RoadMapLine (Properties ctx, int M_Jacofer_RoadMapLine_ID, String trxName)
{
super (ctx, M_Jacofer_RoadMapLine_ID, trxName);
/** if (M_Jacofer_RoadMapLine_ID == 0)
{
setIncludeInOut (false);
setM_InOut_ID (0);
setM_Jacofer_RoadMap_ID (0);
setM_Jacofer_RoadMapLine_ID (0);
setProcessed (false);
}
 */
}
/** Load Constructor */
public LP_M_Jacofer_RoadMapLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Jacofer_RoadMapLine");

/** TableName=M_Jacofer_RoadMapLine */
public static final String Table_Name="M_Jacofer_RoadMapLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Jacofer_RoadMapLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("LP_M_Jacofer_RoadMapLine[").append(getID()).append("]");
return sb.toString();
}
public static final int C_BPARTNER_INOUT_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner (No Summary)");
/** Set Customer */
public void setC_BPartner_InOut_ID (int C_BPartner_InOut_ID)
{
throw new IllegalArgumentException ("C_BPartner_InOut_ID is virtual column");
}
/** Get Customer */
public int getC_BPartner_InOut_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_InOut_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Include In Out */
public void setIncludeInOut (boolean IncludeInOut)
{
set_Value ("IncludeInOut", new Boolean(IncludeInOut));
}
/** Get Include In Out */
public boolean isIncludeInOut() 
{
Object oo = get_Value("IncludeInOut");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Fecha del Remito */
public void setInOutDate (Timestamp InOutDate)
{
throw new IllegalArgumentException ("InOutDate is virtual column");
}
/** Get Fecha del Remito */
public Timestamp getInOutDate() 
{
return (Timestamp)get_Value("InOutDate");
}
/** Set Aforo */
public void setJacofer_Capacity (BigDecimal Jacofer_Capacity)
{
throw new IllegalArgumentException ("Jacofer_Capacity is virtual column");
}
/** Get Aforo */
public BigDecimal getJacofer_Capacity() 
{
BigDecimal bd = (BigDecimal)get_Value("Jacofer_Capacity");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cantidad de Bultos */
public void setJacofer_PackagesQty (BigDecimal Jacofer_PackagesQty)
{
throw new IllegalArgumentException ("Jacofer_PackagesQty is virtual column");
}
/** Get Cantidad de Bultos */
public BigDecimal getJacofer_PackagesQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Jacofer_PackagesQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Peso */
public void setJacofer_Weight (BigDecimal Jacofer_Weight)
{
throw new IllegalArgumentException ("Jacofer_Weight is virtual column");
}
/** Get Peso */
public BigDecimal getJacofer_Weight() 
{
BigDecimal bd = (BigDecimal)get_Value("Jacofer_Weight");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line No.
Unique line for this document */
public void setLine (int Line)
{
set_Value ("Line", new Integer(Line));
}
/** Get Line No.
Unique line for this document */
public int getLine() 
{
Integer ii = (Integer)get_Value("Line");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipment/Receipt.
Material Shipment Document */
public void setM_InOut_ID (int M_InOut_ID)
{
set_Value ("M_InOut_ID", new Integer(M_InOut_ID));
}
/** Get Shipment/Receipt.
Material Shipment Document */
public int getM_InOut_ID() 
{
Integer ii = (Integer)get_Value("M_InOut_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_Jacofer_RoadMap_ID */
public void setM_Jacofer_RoadMap_ID (int M_Jacofer_RoadMap_ID)
{
set_Value ("M_Jacofer_RoadMap_ID", new Integer(M_Jacofer_RoadMap_ID));
}
/** Get M_Jacofer_RoadMap_ID */
public int getM_Jacofer_RoadMap_ID() 
{
Integer ii = (Integer)get_Value("M_Jacofer_RoadMap_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_Jacofer_RoadMapLine_ID */
public void setM_Jacofer_RoadMapLine_ID (int M_Jacofer_RoadMapLine_ID)
{
set_ValueNoCheck ("M_Jacofer_RoadMapLine_ID", new Integer(M_Jacofer_RoadMapLine_ID));
}
/** Get M_Jacofer_RoadMapLine_ID */
public int getM_Jacofer_RoadMapLine_ID() 
{
Integer ii = (Integer)get_Value("M_Jacofer_RoadMapLine_ID");
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
}
