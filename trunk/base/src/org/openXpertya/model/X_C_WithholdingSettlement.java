/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_WithholdingSettlement
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-11-30 14:29:20.57 */
public class X_C_WithholdingSettlement extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_WithholdingSettlement (Properties ctx, int C_WithholdingSettlement_ID, String trxName)
{
super (ctx, C_WithholdingSettlement_ID, trxName);
/** if (C_WithholdingSettlement_ID == 0)
{
setC_CreditCardSettlement_ID (0);
setC_RetencionType_ID (0);
setC_WithholdingSettlement_ID (0);
}
 */
}
/** Load Constructor */
public X_C_WithholdingSettlement (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_WithholdingSettlement");

/** TableName=C_WithholdingSettlement */
public static final String Table_Name="C_WithholdingSettlement";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_WithholdingSettlement");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_WithholdingSettlement[").append(getID()).append("]");
return sb.toString();
}
/** Set Amount.
Amount in a defined currency */
public void setAmount (BigDecimal Amount)
{
set_Value ("Amount", Amount);
}
/** Get Amount.
Amount in a defined currency */
public BigDecimal getAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("Amount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Card Settlement */
public void setC_CreditCardSettlement_ID (int C_CreditCardSettlement_ID)
{
set_Value ("C_CreditCardSettlement_ID", new Integer(C_CreditCardSettlement_ID));
}
/** Get Credit Card Settlement */
public int getC_CreditCardSettlement_ID() 
{
Integer ii = (Integer)get_Value("C_CreditCardSettlement_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Region.
Identifies a geographical Region */
public void setC_Region_ID (int C_Region_ID)
{
if (C_Region_ID <= 0) set_Value ("C_Region_ID", null);
 else 
set_Value ("C_Region_ID", new Integer(C_Region_ID));
}
/** Get Region.
Identifies a geographical Region */
public int getC_Region_ID() 
{
Integer ii = (Integer)get_Value("C_Region_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Type */
public void setC_RetencionType_ID (int C_RetencionType_ID)
{
set_Value ("C_RetencionType_ID", new Integer(C_RetencionType_ID));
}
/** Get Retencion Type */
public int getC_RetencionType_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Withholding Settlement ID */
public void setC_WithholdingSettlement_ID (int C_WithholdingSettlement_ID)
{
set_ValueNoCheck ("C_WithholdingSettlement_ID", new Integer(C_WithholdingSettlement_ID));
}
/** Get Withholding Settlement ID */
public int getC_WithholdingSettlement_ID() 
{
Integer ii = (Integer)get_Value("C_WithholdingSettlement_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
