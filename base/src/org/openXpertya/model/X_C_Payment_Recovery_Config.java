/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Payment_Recovery_Config
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-06-26 19:39:14.973 */
public class X_C_Payment_Recovery_Config extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Payment_Recovery_Config (Properties ctx, int C_Payment_Recovery_Config_ID, String trxName)
{
super (ctx, C_Payment_Recovery_Config_ID, trxName);
/** if (C_Payment_Recovery_Config_ID == 0)
{
setC_DocType_Credit_Recovery_ID (0);
setC_DocType_Credit_Rejected_ID (0);
setC_DocType_Recovery_ID (0);
setC_DocType_Rejected_ID (0);
setC_Payment_Recovery_Config_ID (0);
setM_Product_Recovery_ID (0);
setM_Product_Rejected_ID (0);
}
 */
}
/** Load Constructor */
public X_C_Payment_Recovery_Config (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Payment_Recovery_Config");

/** TableName=C_Payment_Recovery_Config */
public static final String Table_Name="C_Payment_Recovery_Config";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Payment_Recovery_Config");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Payment_Recovery_Config[").append(getID()).append("]");
return sb.toString();
}
public static final int C_DOCTYPE_CREDIT_RECOVERY_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Recovery Credit Document Type */
public void setC_DocType_Credit_Recovery_ID (int C_DocType_Credit_Recovery_ID)
{
set_Value ("C_DocType_Credit_Recovery_ID", new Integer(C_DocType_Credit_Recovery_ID));
}
/** Get Recovery Credit Document Type */
public int getC_DocType_Credit_Recovery_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_Credit_Recovery_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPE_CREDIT_REJECTED_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Rejected Credit Document Type */
public void setC_DocType_Credit_Rejected_ID (int C_DocType_Credit_Rejected_ID)
{
set_Value ("C_DocType_Credit_Rejected_ID", new Integer(C_DocType_Credit_Rejected_ID));
}
/** Get Rejected Credit Document Type */
public int getC_DocType_Credit_Rejected_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_Credit_Rejected_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPE_RECOVERY_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Recovery Document Type */
public void setC_DocType_Recovery_ID (int C_DocType_Recovery_ID)
{
set_Value ("C_DocType_Recovery_ID", new Integer(C_DocType_Recovery_ID));
}
/** Get Recovery Document Type */
public int getC_DocType_Recovery_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_Recovery_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPE_REJECTED_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Rejected Document Type */
public void setC_DocType_Rejected_ID (int C_DocType_Rejected_ID)
{
set_Value ("C_DocType_Rejected_ID", new Integer(C_DocType_Rejected_ID));
}
/** Get Rejected Document Type */
public int getC_DocType_Rejected_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_Rejected_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Recovery Config */
public void setC_Payment_Recovery_Config_ID (int C_Payment_Recovery_Config_ID)
{
set_ValueNoCheck ("C_Payment_Recovery_Config_ID", new Integer(C_Payment_Recovery_Config_ID));
}
/** Get Payment Recovery Config */
public int getC_Payment_Recovery_Config_ID() 
{
Integer ii = (Integer)get_Value("C_Payment_Recovery_Config_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCT_RECOVERY_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set Recovery Product */
public void setM_Product_Recovery_ID (int M_Product_Recovery_ID)
{
set_Value ("M_Product_Recovery_ID", new Integer(M_Product_Recovery_ID));
}
/** Get Recovery Product */
public int getM_Product_Recovery_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Recovery_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCT_REJECTED_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set Rejected Product */
public void setM_Product_Rejected_ID (int M_Product_Rejected_ID)
{
set_Value ("M_Product_Rejected_ID", new Integer(M_Product_Rejected_ID));
}
/** Get Rejected Product */
public int getM_Product_Rejected_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Rejected_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
