/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_Lista_Patagonia
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-05-23 11:01:08.039 */
public class X_I_Lista_Patagonia extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_Lista_Patagonia (Properties ctx, int I_Lista_Patagonia_ID, String trxName)
{
super (ctx, I_Lista_Patagonia_ID, trxName);
/** if (I_Lista_Patagonia_ID == 0)
{
setI_IsImported (false);
setI_Lista_Patagonia_ID (0);
}
 */
}
/** Load Constructor */
public X_I_Lista_Patagonia (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_Lista_Patagonia");

/** TableName=I_Lista_Patagonia */
public static final String Table_Name="I_Lista_Patagonia";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_Lista_Patagonia");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_Lista_Patagonia[").append(getID()).append("]");
return sb.toString();
}
/** Set Beneficiario */
public void setBeneficiario (String Beneficiario)
{
if (Beneficiario != null && Beneficiario.length() > 30)
{
log.warning("Length > 30 - truncated");
Beneficiario = Beneficiario.substring(0,30);
}
set_Value ("Beneficiario", Beneficiario);
}
/** Get Beneficiario */
public String getBeneficiario() 
{
return (String)get_Value("Beneficiario");
}
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
if (C_Payment_ID <= 0) set_Value ("C_Payment_ID", null);
 else 
set_Value ("C_Payment_ID", new Integer(C_Payment_ID));
}
/** Get Payment.
Payment identifier */
public int getC_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Payment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Fecha Alta */
public void setF_Alta (Timestamp F_Alta)
{
set_Value ("F_Alta", F_Alta);
}
/** Get Fecha Alta */
public Timestamp getF_Alta() 
{
return (Timestamp)get_Value("F_Alta");
}
/** Set Fecha Emision */
public void setF_Emision (Timestamp F_Emision)
{
set_Value ("F_Emision", F_Emision);
}
/** Get Fecha Emision */
public Timestamp getF_Emision() 
{
return (Timestamp)get_Value("F_Emision");
}
/** Set Fecha Vto Cpd */
public void setF_Vto_Cpd (Timestamp F_Vto_Cpd)
{
set_Value ("F_Vto_Cpd", F_Vto_Cpd);
}
/** Get Fecha Vto Cpd */
public Timestamp getF_Vto_Cpd() 
{
return (Timestamp)get_Value("F_Vto_Cpd");
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
/** Set I_Lista_Patagonia_ID */
public void setI_Lista_Patagonia_ID (int I_Lista_Patagonia_ID)
{
set_ValueNoCheck ("I_Lista_Patagonia_ID", new Integer(I_Lista_Patagonia_ID));
}
/** Get I_Lista_Patagonia_ID */
public int getI_Lista_Patagonia_ID() 
{
Integer ii = (Integer)get_Value("I_Lista_Patagonia_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Importe */
public void setImporte (BigDecimal Importe)
{
set_Value ("Importe", Importe);
}
/** Get Importe */
public BigDecimal getImporte() 
{
BigDecimal bd = (BigDecimal)get_Value("Importe");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Moneda */
public void setMoneda (String Moneda)
{
if (Moneda != null && Moneda.length() > 3)
{
log.warning("Length > 3 - truncated");
Moneda = Moneda.substring(0,3);
}
set_Value ("Moneda", Moneda);
}
/** Get Moneda */
public String getMoneda() 
{
return (String)get_Value("Moneda");
}
/** Set Motivo_Reemp */
public void setMotivo_Reemp (String Motivo_Reemp)
{
if (Motivo_Reemp != null && Motivo_Reemp.length() > 1)
{
log.warning("Length > 1 - truncated");
Motivo_Reemp = Motivo_Reemp.substring(0,1);
}
set_Value ("Motivo_Reemp", Motivo_Reemp);
}
/** Get Motivo_Reemp */
public String getMotivo_Reemp() 
{
return (String)get_Value("Motivo_Reemp");
}
/** Set Nro Chq Reemp */
public void setNro_Chq_Reemp (String Nro_Chq_Reemp)
{
if (Nro_Chq_Reemp != null && Nro_Chq_Reemp.length() > 15)
{
log.warning("Length > 15 - truncated");
Nro_Chq_Reemp = Nro_Chq_Reemp.substring(0,15);
}
set_Value ("Nro_Chq_Reemp", Nro_Chq_Reemp);
}
/** Get Nro Chq Reemp */
public String getNro_Chq_Reemp() 
{
return (String)get_Value("Nro_Chq_Reemp");
}
/** Set Nro Chq Usado */
public void setNro_Chq_Usado (String Nro_Chq_Usado)
{
if (Nro_Chq_Usado != null && Nro_Chq_Usado.length() > 15)
{
log.warning("Length > 15 - truncated");
Nro_Chq_Usado = Nro_Chq_Usado.substring(0,15);
}
set_Value ("Nro_Chq_Usado", Nro_Chq_Usado);
}
/** Get Nro Chq Usado */
public String getNro_Chq_Usado() 
{
return (String)get_Value("Nro_Chq_Usado");
}
/** Set OP Ref */
public void setOP_Ref (String OP_Ref)
{
if (OP_Ref != null && OP_Ref.length() > 25)
{
log.warning("Length > 25 - truncated");
OP_Ref = OP_Ref.substring(0,25);
}
set_Value ("OP_Ref", OP_Ref);
}
/** Get OP Ref */
public String getOP_Ref() 
{
return (String)get_Value("OP_Ref");
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
