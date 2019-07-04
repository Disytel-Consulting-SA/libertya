/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por Fact_Acct_Balance_Config
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-07-04 17:47:14.681 */
public class X_Fact_Acct_Balance_Config extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_Fact_Acct_Balance_Config (Properties ctx, int Fact_Acct_Balance_Config_ID, String trxName)
{
super (ctx, Fact_Acct_Balance_Config_ID, trxName);
/** if (Fact_Acct_Balance_Config_ID == 0)
{
setAD_Column_ID (0);
setC_AcctSchema_ID (0);
setFact_Acct_Balance_Config_ID (0);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+1 AS DefaultValue FROM Fact_Acct_Balance_Config WHERE C_AcctSchema_ID=@C_AcctSchema_ID@
}
 */
}
/** Load Constructor */
public X_Fact_Acct_Balance_Config (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("Fact_Acct_Balance_Config");

/** TableName=Fact_Acct_Balance_Config */
public static final String Table_Name="Fact_Acct_Balance_Config";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"Fact_Acct_Balance_Config");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_Fact_Acct_Balance_Config[").append(getID()).append("]");
return sb.toString();
}
/** Set Column.
Column in the table */
public void setAD_Column_ID (int AD_Column_ID)
{
set_Value ("AD_Column_ID", new Integer(AD_Column_ID));
}
/** Get Column.
Column in the table */
public int getAD_Column_ID() 
{
Integer ii = (Integer)get_Value("AD_Column_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Component Version Identifier */
public void setAD_ComponentVersion_ID (int AD_ComponentVersion_ID)
{
if (AD_ComponentVersion_ID <= 0) set_Value ("AD_ComponentVersion_ID", null);
 else 
set_Value ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Accounting Schema.
Rules for accounting */
public void setC_AcctSchema_ID (int C_AcctSchema_ID)
{
set_Value ("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
}
/** Get Accounting Schema.
Rules for accounting */
public int getC_AcctSchema_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Fact Account Balance Config */
public void setFact_Acct_Balance_Config_ID (int Fact_Acct_Balance_Config_ID)
{
set_ValueNoCheck ("Fact_Acct_Balance_Config_ID", new Integer(Fact_Acct_Balance_Config_ID));
}
/** Get Fact Account Balance Config */
public int getFact_Acct_Balance_Config_ID() 
{
Integer ii = (Integer)get_Value("Fact_Acct_Balance_Config_ID");
if (ii == null) return 0;
return ii.intValue();
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
