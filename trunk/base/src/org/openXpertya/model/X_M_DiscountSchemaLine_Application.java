/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_DiscountSchemaLine_Application
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-08-21 23:53:34.952 */
public class X_M_DiscountSchemaLine_Application extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_DiscountSchemaLine_Application (Properties ctx, int M_DiscountSchemaLine_Application_ID, String trxName)
{
super (ctx, M_DiscountSchemaLine_Application_ID, trxName);
/** if (M_DiscountSchemaLine_Application_ID == 0)
{
setM_DiscountSchemaLine_Application_ID (0);
setM_DiscountSchemaLine_ID (0);	// @M_DiscountSchemaLine_ID@
}
 */
}
/** Load Constructor */
public X_M_DiscountSchemaLine_Application (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_DiscountSchemaLine_Application");

/** TableName=M_DiscountSchemaLine_Application */
public static final String Table_Name="M_DiscountSchemaLine_Application";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_DiscountSchemaLine_Application");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_DiscountSchemaLine_Application[").append(getID()).append("]");
return sb.toString();
}
/** Set M_DiscountSchemaLine_Application_ID */
public void setM_DiscountSchemaLine_Application_ID (int M_DiscountSchemaLine_Application_ID)
{
set_ValueNoCheck ("M_DiscountSchemaLine_Application_ID", new Integer(M_DiscountSchemaLine_Application_ID));
}
/** Get M_DiscountSchemaLine_Application_ID */
public int getM_DiscountSchemaLine_Application_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountSchemaLine_Application_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Discount Pricelist.
Line of the pricelist trade discount schema */
public void setM_DiscountSchemaLine_ID (int M_DiscountSchemaLine_ID)
{
set_Value ("M_DiscountSchemaLine_ID", new Integer(M_DiscountSchemaLine_ID));
}
/** Get Discount Pricelist.
Line of the pricelist trade discount schema */
public int getM_DiscountSchemaLine_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountSchemaLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
