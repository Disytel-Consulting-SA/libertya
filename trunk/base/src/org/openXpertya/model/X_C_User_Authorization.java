/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_User_Authorization
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-09-06 20:19:03.913 */
public class X_C_User_Authorization extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_User_Authorization (Properties ctx, int C_User_Authorization_ID, String trxName)
{
super (ctx, C_User_Authorization_ID, trxName);
/** if (C_User_Authorization_ID == 0)
{
setAD_User_Auth_ID (0);
setAD_User_Login_ID (0);
setC_User_Authorization_ID (0);
}
 */
}
/** Load Constructor */
public X_C_User_Authorization (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_User_Authorization");

/** TableName=C_User_Authorization */
public static final String Table_Name="C_User_Authorization";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_User_Authorization");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_User_Authorization[").append(getID()).append("]");
return sb.toString();
}
/** Set Process.
Process or Report */
public void setAD_Process_ID (int AD_Process_ID)
{
if (AD_Process_ID <= 0) set_Value ("AD_Process_ID", null);
 else 
set_Value ("AD_Process_ID", new Integer(AD_Process_ID));
}
/** Get Process.
Process or Report */
public int getAD_Process_ID() 
{
Integer ii = (Integer)get_Value("AD_Process_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_USER_AUTH_ID_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set Authorize by */
public void setAD_User_Auth_ID (int AD_User_Auth_ID)
{
set_Value ("AD_User_Auth_ID", new Integer(AD_User_Auth_ID));
}
/** Get Authorize by */
public int getAD_User_Auth_ID() 
{
Integer ii = (Integer)get_Value("AD_User_Auth_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_USER_LOGIN_ID_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set Login User */
public void setAD_User_Login_ID (int AD_User_Login_ID)
{
set_Value ("AD_User_Login_ID", new Integer(AD_User_Login_ID));
}
/** Get Login User */
public int getAD_User_Login_ID() 
{
Integer ii = (Integer)get_Value("AD_User_Login_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Authorization Date */
public void setAuthTime (Timestamp AuthTime)
{
set_Value ("AuthTime", AuthTime);
}
/** Get Authorization Date */
public Timestamp getAuthTime() 
{
return (Timestamp)get_Value("AuthTime");
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
/** Set C_User_Authorization_ID */
public void setC_User_Authorization_ID (int C_User_Authorization_ID)
{
set_ValueNoCheck ("C_User_Authorization_ID", new Integer(C_User_Authorization_ID));
}
/** Get C_User_Authorization_ID */
public int getC_User_Authorization_ID() 
{
Integer ii = (Integer)get_Value("C_User_Authorization_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Operation Log */
public void setOperationLog (String OperationLog)
{
if (OperationLog != null && OperationLog.length() > 1000)
{
log.warning("Length > 1000 - truncated");
OperationLog = OperationLog.substring(0,1000);
}
set_Value ("OperationLog", OperationLog);
}
/** Get Operation Log */
public String getOperationLog() 
{
return (String)get_Value("OperationLog");
}
/** Set Percentage.
Percent of the entire amount */
public void setPercentage (BigDecimal Percentage)
{
set_Value ("Percentage", Percentage);
}
/** Get Percentage.
Percent of the entire amount */
public BigDecimal getPercentage() 
{
BigDecimal bd = (BigDecimal)get_Value("Percentage");
if (bd == null) return Env.ZERO;
return bd;
}

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO C_User_Authorization(C_User_Authorization_ID,AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy,Amount,Percentage,AD_Process_ID,AD_User_Auth_ID,AD_User_Login_ID,AuthTime,C_Invoice_ID,OperationLog" + getAdditionalParamNames() + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" + getAdditionalParamMarks() + ") ";

		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getAmount() == null) sql = sql.replaceFirst("Amount,","").replaceFirst("\\?,", "");
 		 if (getPercentage() == null) sql = sql.replaceFirst("Percentage,","").replaceFirst("\\?,", "");
 		 if (getAD_Process_ID() == 0) sql = sql.replaceFirst("AD_Process_ID,","").replaceFirst("\\?,", "");
 		 if (getAuthTime() == null) sql = sql.replaceFirst("AuthTime,","").replaceFirst("\\?,", "");
 		 if (getC_Invoice_ID() == 0) sql = sql.replaceFirst("C_Invoice_ID,","").replaceFirst("\\?,", "");
 		 if (getOperationLog() == null) sql = sql.replaceFirst("OperationLog,","").replaceFirst("\\?,", "");
 		 skipAdditionalNullValues(sql);
 

 		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 pstmt.setInt(col++, getC_User_Authorization_ID());
		 pstmt.setInt(col++, getAD_Client_ID());
		 pstmt.setInt(col++, getAD_Org_ID());
		 pstmt.setString(col++, isActive()?"Y":"N");
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 pstmt.setInt(col++, getCreatedBy());
		 if (getUpdated() != null) pstmt.setTimestamp(col++, getUpdated());
		 pstmt.setInt(col++, getUpdatedBy());
		 if (getAmount() != null) pstmt.setBigDecimal(col++, getAmount());
		 if (getPercentage() != null) pstmt.setBigDecimal(col++, getPercentage());
		 if (getAD_Process_ID() != 0) pstmt.setInt(col++, getAD_Process_ID());
		 pstmt.setInt(col++, getAD_User_Auth_ID());
		 pstmt.setInt(col++, getAD_User_Login_ID());
		 if (getAuthTime() != null) pstmt.setTimestamp(col++, getAuthTime());
		 if (getC_Invoice_ID() != 0) pstmt.setInt(col++, getC_Invoice_ID());
		 if (getOperationLog() != null) pstmt.setString(col++, getOperationLog());
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
