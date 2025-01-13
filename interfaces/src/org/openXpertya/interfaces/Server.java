/*
 * Generated file - Do not edit!
 */
package org.openXpertya.interfaces;

import java.lang.*;
import java.io.NotSerializableException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.util.logging.Level;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.RowSet;
import org.openXpertya.OpenXpertya;
import org.openXpertya.acct.Doc;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MFieldVO;
import org.openXpertya.model.MLookupInfo;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPaymentProcessor;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MSequence;
import org.openXpertya.model.MTabVO;
import org.openXpertya.model.MWindowVO;
import org.openXpertya.model.POInfo;
import org.openXpertya.model.POInfoColumn;
import org.openXpertya.model.PaymentProcessor;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.CStatement;
import org.openXpertya.util.CStatementVO;
import org.openXpertya.util.CacheMgt;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Trx;
import org.openXpertya.wf.MWFProcess;
import org.openXpertya.wf.MWorkflow;

/**
 * Remote interface for openXpertya/Server.
 * @version XDOCLET 1.1.2
 * @author XDOCLET 1.1.2 * @xdoclet-generated at 16-ago-2024 13:04:36
 */
public interface Server
   extends javax.ejb.EJBObject
{
   /**
    * Cash Reset
    * @param tableName table name
    * @param Record_ID record or 0 for all
    * @param returns number of records reset
    * @throws RemoteException    */
   public int cacheReset( java.lang.String tableName,int Record_ID ) throws java.rmi.RemoteException;

   /**
    * Get Document No from table
    * @param AD_Client_ID client
    * @param TableName table name
    * @param trxName optional Transaction Name
    * @return document no or null
    * @throws RemoteException    */
   public java.lang.String getDocumentNo( int AD_Client_ID,java.lang.String TableName,java.lang.String trxName ) throws java.rmi.RemoteException;

   /**
    * Get Document No based on Document Type
    * @param C_DocType_ID document type
    * @param trxName optional Transaction Name
    * @return document no or null
    * @throws RemoteException    */
   public java.lang.String getDocumentNo( int C_DocType_ID,java.lang.String trxName ) throws java.rmi.RemoteException;

   /**
    * Get next number for Key column = 0 is Error.
    * @param AD_Client_ID client
    * @param TableName table name
    * @param trxName optional Transaction Name
    * @return next no
    * @throws RemoteException    */
   public int getNextID( int AD_Client_ID,java.lang.String TableName,java.lang.String trxName ) throws java.rmi.RemoteException;

   /**
    * Describes the instance and its content for debugging purpose
    * @return Debugging information about the instance and its content    */
   public java.lang.String getStatus(  ) throws java.rmi.RemoteException;

   /**
    * Get Unique Document No based on Document Type
    * @param C_DocType_ID document type
    * @param trxName optional Transaction Name
    * @return unique document no or null
    * @throws RemoteException    */
   public java.lang.String getUniqueDocumentNo( int C_DocType_ID,java.lang.String trxName ) throws java.rmi.RemoteException;

   /**
    * Obtiene y crea valor de objeto de modelo de ventana
    * @param ctx   Environment Properties
    * @param WindowNo  number of this window
    * @param AD_Window_ID  the internal number of the window, if not 0, AD_Menu_ID is ignored
    * @param AD_Menu_ID ine internal menu number, used when AD_Window_ID is 0
    * @return initialized Window Model
    * @throws RemoteException    */
   public org.openXpertya.model.MWindowVO getWindowVO( java.util.Properties ctx,int WindowNo,int AD_Window_ID,int AD_Menu_ID ) throws java.rmi.RemoteException;

   /**
    * Run Workflow (and wait) on Server
    * @param ctx Context
    * @param payment payment
    * @return true if approved
    * @throws RemoteException    */
   public boolean paymentOnline( java.util.Properties ctx,int C_Payment_ID,int C_PaymentProcessor_ID,java.lang.String trxName ) throws java.rmi.RemoteException;

   /**
    * Post Immediate
    * @param ctx Client Context
    * @param AD_Client_ID    Client ID of Document
    * @param AD_Table_ID     Table ID of Document
    * @param Record_ID       Record ID of this document
    * @param force           force posting
    * @return true, if success
    * @throws RemoteException    */
   public boolean postImmediate( java.util.Properties ctx,int AD_Client_ID,int AD_Table_ID,int Record_ID,boolean force ) throws java.rmi.RemoteException;

   /**
    * Process Remote
    * @param ctx Context
    * @param pi Process Info
    * @return resulting Process Info
    * @throws RemoteException    */
   public org.openXpertya.process.ProcessInfo process( java.util.Properties ctx,org.openXpertya.process.ProcessInfo pi ) throws java.rmi.RemoteException;

   /**
    * Get Prepared Statement ResultSet
    * @param info Result info
    * @return RowSet
    * @throws RemoteException
    * @throws NotSerializableException    */
   public javax.sql.RowSet pstmt_getRowSet( org.openXpertya.util.CStatementVO info ) throws java.io.NotSerializableException, java.rmi.RemoteException;

   /**
    * Execute Update
    * @param info Result info
    * @return row count
    * @throws RemoteException    */
   public int stmt_executeUpdate( org.openXpertya.util.CStatementVO info ) throws java.rmi.RemoteException;

   /**
    * Get Statement ResultSet
    * @param info Result info
    * @return RowSet
    * @throws RemoteException    */
   public javax.sql.RowSet stmt_getRowSet( org.openXpertya.util.CStatementVO info ) throws java.rmi.RemoteException;

   /**
    * LOB update
    * @param sql table name
    * @param displayType display type (i.e. BLOB/CLOB)
    * @param value the data
    * @param returns true if updated
    * @throws RemoteException    */
   public boolean updateLOB( java.lang.String sql,int displayType,java.lang.Object value ) throws java.rmi.RemoteException;

   /**
    * Run Workflow (and wait) on Server
    * @param ctx Context
    * @param pi Process Info
    * @return process info
    * @throws RemoteException    */
   public org.openXpertya.process.ProcessInfo workflow( java.util.Properties ctx,org.openXpertya.process.ProcessInfo pi,int AD_Workflow_ID ) throws java.rmi.RemoteException;

}
