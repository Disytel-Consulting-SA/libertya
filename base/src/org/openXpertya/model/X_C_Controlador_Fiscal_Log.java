/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Controlador_Fiscal_Log
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-11-29 20:35:19.541 */
public class X_C_Controlador_Fiscal_Log extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Controlador_Fiscal_Log (Properties ctx, int C_Controlador_Fiscal_Log_ID, String trxName)
{
super (ctx, C_Controlador_Fiscal_Log_ID, trxName);
/** if (C_Controlador_Fiscal_Log_ID == 0)
{
setC_Controlador_Fiscal_ID (0);
setC_Controlador_Fiscal_Log_ID (0);
setLogType (null);
}
 */
}
/** Load Constructor */
public X_C_Controlador_Fiscal_Log (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Controlador_Fiscal_Log");

/** TableName=C_Controlador_Fiscal_Log */
public static final String Table_Name="C_Controlador_Fiscal_Log";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Controlador_Fiscal_Log");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Controlador_Fiscal_Log[").append(getID()).append("]");
return sb.toString();
}
/** Set C_Controlador_Fiscal_ID */
public void setC_Controlador_Fiscal_ID (int C_Controlador_Fiscal_ID)
{
set_Value ("C_Controlador_Fiscal_ID", new Integer(C_Controlador_Fiscal_ID));
}
/** Get C_Controlador_Fiscal_ID */
public int getC_Controlador_Fiscal_ID() 
{
Integer ii = (Integer)get_Value("C_Controlador_Fiscal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Controlador_Fiscal_Log_ID */
public void setC_Controlador_Fiscal_Log_ID (int C_Controlador_Fiscal_Log_ID)
{
set_ValueNoCheck ("C_Controlador_Fiscal_Log_ID", new Integer(C_Controlador_Fiscal_Log_ID));
}
/** Get C_Controlador_Fiscal_Log_ID */
public int getC_Controlador_Fiscal_Log_ID() 
{
Integer ii = (Integer)get_Value("C_Controlador_Fiscal_Log_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_Value ("C_Invoice_ID", null);
 else 
set_Value ("C_Invoice_ID", new Integer(C_Invoice_ID));
}
/** Get Invoice.
Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Command.
Fiscal Command */
public void setCommand (String Command)
{
if (Command != null && Command.length() > 100)
{
log.warning("Length > 100 - truncated");
Command = Command.substring(0,100);
}
set_Value ("Command", Command);
}
/** Get Command.
Fiscal Command */
public String getCommand() 
{
return (String)get_Value("Command");
}
public static final int LOGTYPE_AD_Reference_ID = MReference.getReferenceID("Tipo de Log");
/** Error = E */
public static final String LOGTYPE_Error = "E";
/** Info = I */
public static final String LOGTYPE_Info = "I";
/** Set Log Type */
public void setLogType (String LogType)
{
if (LogType.equals("E") || LogType.equals("I") || ( refContainsValue("CORE-AD_Reference-1010241", LogType) ) );
 else throw new IllegalArgumentException ("LogType Invalid value: " + LogType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010241") );
if (LogType == null) throw new IllegalArgumentException ("LogType is mandatory");
if (LogType.length() > 1)
{
log.warning("Length > 1 - truncated");
LogType = LogType.substring(0,1);
}
set_Value ("LogType", LogType);
}
/** Get Log Type */
public String getLogType() 
{
return (String)get_Value("LogType");
}
/** Set Response.
Command response */
public void setResponse (String Response)
{
if (Response != null && Response.length() > 255)
{
log.warning("Length > 255 - truncated");
Response = Response.substring(0,255);
}
set_Value ("Response", Response);
}
/** Get Response.
Command response */
public String getResponse() 
{
return (String)get_Value("Response");
}

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO C_Controlador_Fiscal_Log(AD_Org_ID,C_Controlador_Fiscal_Log_ID,AD_Client_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy,C_Controlador_Fiscal_ID,C_Invoice_ID,Command,LogType,Response," + getAdditionalParamNames() + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?," + getAdditionalParamMarks() + ") ";

		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getC_Invoice_ID() == 0) sql = sql.replaceFirst("C_Invoice_ID,","").replaceFirst("\\?,", "");
 		 if (getCommand() == null) sql = sql.replaceFirst("Command,","").replaceFirst("\\?,", "");
 		 if (getLogType() == null) sql = sql.replaceFirst("LogType,","").replaceFirst("\\?,", "");
 		 if (getResponse() == null) sql = sql.replaceFirst("Response,","").replaceFirst("\\?,", "");
 		 skipAdditionalNullValues(sql);
 

 		 sql = sql.replace(",)", ")");
 
		 sql = sql.replace(",,)", ",");
 
		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 pstmt.setInt(col++, getAD_Org_ID());
		 pstmt.setInt(col++, getC_Controlador_Fiscal_Log_ID());
		 pstmt.setInt(col++, getAD_Client_ID());
		 pstmt.setString(col++, isActive()?"Y":"N");
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 pstmt.setInt(col++, getCreatedBy());
		 if (getUpdated() != null) pstmt.setTimestamp(col++, getUpdated());
		 pstmt.setInt(col++, getUpdatedBy());
		 pstmt.setInt(col++, getC_Controlador_Fiscal_ID());
		 if (getC_Invoice_ID() != 0) pstmt.setInt(col++, getC_Invoice_ID());
		 if (getCommand() != null) pstmt.setString(col++, getCommand());
		 if (getLogType() != null) pstmt.setString(col++, getLogType());
		 if (getResponse() != null) pstmt.setString(col++, getResponse());
		 col = setAdditionalInsertValues(col, pstmt);
 

		pstmt.executeUpdate();

		return true;

	}
catch (SQLException e) 
{
	log.log(Level.SEVERE, "insertDirect", e);
	log.saveError("Error", DB.getErrorMsg(e) + " - " + e);
	return false;
	}
catch (Exception e2) 
{
	log.log(Level.SEVERE, "insertDirect", e2);
	return false;
}

}

protected String getAdditionalParamNames() 
{
 return "";
 }
 
protected String getAdditionalParamMarks() 
{
 return "";
 }
 
protected void skipAdditionalNullValues(String sql) 
{
  }
 
protected int setAdditionalInsertValues(int col, PreparedStatement pstmt) throws Exception 
{
 return col;
 }
 
}
