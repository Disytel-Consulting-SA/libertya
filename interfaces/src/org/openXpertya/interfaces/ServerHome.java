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
 * Home interface for openXpertya/Server. Lookup using {1}
 * @version XDOCLET 1.1.2
 * @author XDOCLET 1.1.2 * @xdoclet-generated at 16-ago-2024 13:04:36
 */
public interface ServerHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/openXpertya/Server";
   public static final String JNDI_NAME="ejb/openXpertya/Server";

   public org.openXpertya.interfaces.Server create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}
