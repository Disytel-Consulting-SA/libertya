/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */
 
 package org.openXpertya.session;

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
 * 	OpenXpertya Server Bean.
 *
 *  @ejb:bean name="openXpertya/Server"
 *           display-name="OpenXpertya Server Session Bean"
 *           type="Stateless"
 *           transaction-type="Bean"
 *           jndi-name="ejb/openXpertya/Server"
 *
 *  @ejb:ejb-ref ejb-name="openXpertya/Server"
 *              ref-name="openXpertya/Server"
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya  
 * 
 */
public class ServerBean implements SessionBean
{
	/**	Context				*/
	private SessionContext 	m_Context;
	/**	Logger				*/
	private static CLogger log = CLogger.getCLogger(ServerBean.class);
	//
	private static int		s_no = 0;
	private int				m_no = 0;
	//
	private int				m_windowCount = 0;
	private int				m_postCount = 0;
	private int				m_processCount = 0;
	private int				m_workflowCount = 0;
	private int				m_paymentCount = 0;
	private int				m_nextSeqCount = 0;
	private int				m_stmt_rowSetCount = 0;
	private int				m_stmt_updateCount = 0;
	private int				m_cacheResetCount = 0;
	private int				m_updateLOBCount = 0;

	/**
	 *  Obtiene y crea valor de objeto de modelo de ventana
	 *  @ejb:interface-method view-type="remote"
	 *
	 *  @param ctx   Environment Properties
	 *  @param WindowNo  number of this window
	 *  @param AD_Window_ID  the internal number of the window, if not 0, AD_Menu_ID is ignored
	 *  @param AD_Menu_ID ine internal menu number, used when AD_Window_ID is 0
	 *  @return initialized Window Model
	 *  @throws RemoteException
	 */
	public MWindowVO getWindowVO (Properties ctx, int WindowNo, int AD_Window_ID, int AD_Menu_ID)
		throws RemoteException
	{
		log.info ("getWindowVO[" + m_no + "] Window=" + AD_Window_ID);
	//	log.fine(ctx);
		MWindowVO vo = MWindowVO.create(ctx, WindowNo, AD_Window_ID, AD_Menu_ID);
		m_windowCount++;
		return vo;
	}	//	getWindowVO


	/**
	 *  Post Immediate
	 *  @ejb:interface-method view-type="remote"
	 *
	 *	@param	ctx Client Context
	 *  @param  AD_Client_ID    Client ID of Document
	 *  @param  AD_Table_ID     Table ID of Document
	 *  @param  Record_ID       Record ID of this document
	 *  @param  force           force posting
	 *  @return true, if success
	 *  @throws RemoteException
	 */
	public boolean postImmediate (Properties ctx, 
		int AD_Client_ID, int AD_Table_ID, int Record_ID, boolean force)
		throws RemoteException
	{
		log.info ("[" + m_no + "] Table=" + AD_Table_ID + ", Record=" + Record_ID);
		m_postCount++;
		MAcctSchema[] ass = MAcctSchema.getClientAcctSchema(ctx, AD_Client_ID);
		return Doc.postImmediate(ass, AD_Table_ID, Record_ID, force);
	}	//	postImmediate

	/*************************************************************************
	 *  Get Prepared Statement ResultSet
	 *  @ejb:interface-method view-type="remote"
	 *
	 *  @param info Result info
	 *  @return RowSet
	 *  @throws RemoteException
	 * @throws NotSerializableException
	 */
	public RowSet pstmt_getRowSet (CStatementVO info) throws RemoteException, NotSerializableException
	{
		log.finer("[" + m_no + "]");
		m_stmt_rowSetCount++;
		CPreparedStatement pstmt = new CPreparedStatement(info);
		return pstmt.remote_getRowSet();
	}	//	pstmt_getRowSet

	/**
	 *  Get Statement ResultSet
	 *  @ejb:interface-method view-type="remote"
	 *
	 *  @param info Result info
	 *  @return RowSet
	 *  @throws RemoteException
	 */
	public RowSet stmt_getRowSet (CStatementVO info) throws RemoteException
	{
		log.finer("[" + m_no + "]");
		m_stmt_rowSetCount++;
		CStatement stmt = new CStatement(info);
		return stmt.remote_getRowSet();
	}	//	stmt_getRowSet

	/**
	 *  Execute Update
	 *  @ejb:interface-method view-type="remote"
	 *
	 *  @param info Result info
	 *  @return row count
	 *  @throws RemoteException
	 */
	public int stmt_executeUpdate (CStatementVO info) throws RemoteException
	{
		log.finer("[" + m_no + "]");
		m_stmt_updateCount++;
		if (info.getParameterCount() == 0)
		{
			CStatement stmt = new CStatement(info);
			return stmt.remote_executeUpdate();
		}
		CPreparedStatement pstmt = new CPreparedStatement(info);
		return pstmt.remote_executeUpdate();
	}	//	stmt_executeUpdate

	/*************************************************************************
	 *	Get next number for Key column = 0 is Error.
	 *  @ejb:interface-method view-type="remote"
	 *
	 *  @param AD_Client_ID client
	 *  @param TableName table name
	 * 	@param trxName optional Transaction Name
	 *  @return next no
	 *  @throws RemoteException
	 */
	public int getNextID (int AD_Client_ID, String TableName, String trxName) throws RemoteException
	{
		int retValue = MSequence.getNextID (AD_Client_ID, TableName, trxName);
		log.finer("[" + m_no + "] " + TableName + " = " + retValue);
		m_nextSeqCount++;
		return retValue;
	}	//	getNextID

	/**
	 * 	Get Document No from table
	 *  @ejb:interface-method view-type="remote"
	 * 
	 *	@param AD_Client_ID client
	 *	@param TableName table name
	 * 	@param trxName optional Transaction Name
	 *	@return document no or null
	 *  @throws RemoteException
	 */
	public String getDocumentNo (int AD_Client_ID, String TableName, String trxName)  throws RemoteException
	{
		m_nextSeqCount++;
		String dn = MSequence.getDocumentNo (AD_Client_ID, TableName, trxName);
		if (dn == null)		//	try again
			dn = MSequence.getDocumentNo (AD_Client_ID, TableName, trxName);
		return dn;
	}	//	GetDocumentNo
	
	/**
	 * 	Get Document No based on Document Type
	 *  @ejb:interface-method view-type="remote"
	 * 
	 *	@param C_DocType_ID document type
	 * 	@param trxName optional Transaction Name
	 *	@return document no or null
	 *  @throws RemoteException
	 */
	public String getDocumentNo (int C_DocType_ID, String trxName)  throws RemoteException
	{
		m_nextSeqCount++;
		String dn = MSequence.getDocumentNo (C_DocType_ID, trxName);
		if (dn == null)		//	try again
			dn = MSequence.getDocumentNo (C_DocType_ID, trxName);
		return dn;
	}	//	getDocumentNo


	/*************************************************************************
	 *  Process Remote
	 *  @ejb:interface-method view-type="remote"
	 *
	 *  @param ctx Context
	 *  @param pi Process Info
	 *  @return resulting Process Info
	 *  @throws RemoteException
	 */
	public ProcessInfo process (Properties ctx, ProcessInfo pi) throws RemoteException
	{
		String className = pi.getClassName();
		log.info(className + " - " + pi);
		m_processCount++;
		//	Get Class
		Class clazz = null;
		try
		{
			clazz = Class.forName (className);
		}
		catch (ClassNotFoundException ex)
		{
			log.log(Level.WARNING, className, ex);
			pi.setSummary ("ClassNotFound", true);
			return pi;
		}
		//	Get Process
		SvrProcess process = null;
		try
		{
			process = (SvrProcess)clazz.newInstance ();
		}
		catch (Exception ex)
		{
			log.log(Level.WARNING, "Instance for " + className, ex);
			pi.setSummary ("InstanceError", true);
			return pi;
		}
		//	Start Process
		Trx trx = Trx.get(Trx.createTrxName("ServerPrc"), true);
		try
		{
			boolean ok = process.startProcess (ctx, pi, trx);
			pi = process.getProcessInfo();
			trx.commit();
			trx.close();
		}
		catch (Exception ex1)
		{
			trx.rollback();
			trx.close();
			pi.setSummary ("ProcessError", true);
			return pi;
		}
		return pi;
	}	//	process


	/*************************************************************************
	 *  Run Workflow (and wait) on Server
	 *  @ejb:interface-method view-type="remote"
	 *
	 *  @param ctx Context
	 *  @param pi Process Info
	 *  @return process info
	 *  @throws RemoteException
	 */
	public ProcessInfo workflow (Properties ctx, ProcessInfo pi, int AD_Workflow_ID)
		throws RemoteException
	{
		log.info ("[" + m_no + "] " + AD_Workflow_ID);
		m_workflowCount++;
		MWorkflow wf = MWorkflow.get (ctx, AD_Workflow_ID);
		MWFProcess wfProcess = wf.startWait(pi);	//	may return null
		log.fine(pi.toString());
		return pi;
	}	//	workflow

	/**
	 *  Run Workflow (and wait) on Server
	 *  @ejb:interface-method view-type="remote"
	 *
	 *  @param ctx Context
	 *  @param payment payment
	 *  @return true if approved
	 *  @throws RemoteException
	 */
	public boolean paymentOnline (Properties ctx, int C_Payment_ID, int C_PaymentProcessor_ID, String trxName)
		throws RemoteException
	{
		MPayment payment = new MPayment (ctx, C_Payment_ID, trxName);
		MPaymentProcessor mpp = new MPaymentProcessor (ctx, C_PaymentProcessor_ID, null);
		log.info ("[" + m_no + "] " + payment + " - " + mpp);
		m_paymentCount++;
		boolean approved = false;
		try
		{
			PaymentProcessor pp = PaymentProcessor.create(mpp, payment);
			if (pp == null)
				payment.setErrorMessage("No Payment Processor");
			else
			{
				approved = pp.processCC ();
				if (approved)
					payment.setErrorMessage(null);
				else
					payment.setErrorMessage("From " +  payment.getCreditCardName() 
						+ ": " + payment.getR_RespMsg());
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "processOnline", e);
			payment.setErrorMessage("Payment Processor Error");
		}
		payment.save();	
		return approved;
	}	//	paymentOnline
	
	/**
	 *  Cash Reset
	 *  @ejb:interface-method view-type="remote"
	 *
	 *  @param tableName table name
	 *  @param Record_ID record or 0 for all
	 * 	@param returns number of records reset
	 *  @throws RemoteException
	 */
	public int cacheReset (String tableName, int Record_ID) throws RemoteException
	{
		log.config(tableName + " - " + Record_ID);
		m_cacheResetCount++;
		return CacheMgt.get().reset(tableName, Record_ID);
	}	//	cacheReset
	
	/**
	 *  LOB update
	 *  @ejb:interface-method view-type="remote"
	 *
	 *  @param sql table name
	 *  @param displayType display type (i.e. BLOB/CLOB)
	 * 	@param value the data
	 * 	@param returns true if updated
	 *  @throws RemoteException
	 */
	public boolean updateLOB (String sql, int displayType, Object value) throws RemoteException
	{
		if (sql == null || value == null)
		{
			log.fine("No sql or data");
			return false;
		}
		log.fine(sql);
		m_updateLOBCount++;
		boolean success = true;
		Connection con = DB.createConnection(false, Connection.TRANSACTION_READ_COMMITTED);
		PreparedStatement pstmt = null;
		try
		{
			pstmt = con.prepareStatement(sql);
			if (displayType == DisplayType.TextLong)
				pstmt.setString(1, (String)value);
			else
				pstmt.setBytes(1, (byte[])value);
			int no = pstmt.executeUpdate();
			//
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.FINE, sql, e);
			success = false;
		}
		//	Close Statement
		try
		{
			if (pstmt != null)
				pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			pstmt = null;
		}
		
		//	Success - commit local trx
		if (success)
		{
			try
			{
				con.commit();
				con.close();
				con = null;
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "commit" , e);
				success = false;
			}
		}
		//	Error - roll back
		if (!success)
		{
			log.severe ("rollback");
			try
			{
				con.rollback();
				con.close();
				con = null;
			}
			catch (Exception ee)
			{
				log.log(Level.SEVERE, "rollback" , ee);
			}
		}
		
		//	Clean Connection
		try
		{
			if (con != null)
				con.close();
			con = null;
		}
		catch (Exception e)
		{
			con = null;
		}
		return success;
	}	//	updateLOB

	
	/**************************************************************************
	 * 	Describes the instance and its content for debugging purpose
	 *  @ejb:interface-method view-type="remote"
	 * 	@return Debugging information about the instance and its content
	 */
	public String getStatus()
	{
		StringBuffer sb = new StringBuffer("ServerBean[");
		sb.append(m_no)
			.append("-Window=").append(m_windowCount)
			.append(",Post=").append(m_postCount)
			.append(",Process=").append(m_processCount)
			.append(",NextSeq=").append(m_nextSeqCount)
			.append(",Workflow=").append(m_workflowCount)
			.append(",Payment=").append(m_paymentCount)
			.append(",RowSet=").append(m_stmt_rowSetCount)
			.append(",Update=").append(m_stmt_updateCount)
			.append(",CacheReset=").append(m_cacheResetCount)
			.append(",UpdateLob=").append(m_updateLOBCount)
			.append("]");
		return sb.toString();
	}	//	getStatus


	/**
	 * 	String Representation
	 * 	@return info
	 */
	public String toString()
	{
		return getStatus();
	}	//	toString

	
	/**************************************************************************
	 * 	Create the Session Bean
	 * 	@throws CreateException
	 *  @ejb:create-method view-type="remote"
	 */
	public void ejbCreate() throws CreateException
	{
		m_no = ++s_no;
		log.info ("#" + getStatus());
		try
		{
			OpenXpertya.startup(false);
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, "ejbCreate", ex);
		//	throw new CreateException ();
		}
	}	//	ejbCreate


	// -------------------------------------------------------------------------
	// Framework Callbacks
	// -------------------------------------------------------------------------

	/**
	 * Method setSessionContext
	 * @param aContext SessionContext
	 * @throws EJBException
	 * @see javax.ejb.SessionBean#setSessionContext(SessionContext)
	 */
	public void setSessionContext (SessionContext aContext) throws EJBException
	{
		m_Context = aContext;
	}	//	setSessionContext

	/**
	 * Method ejbActivate
	 * @throws EJBException
	 * @see javax.ejb.SessionBean#ejbActivate()
	 */
	public void ejbActivate() throws EJBException
	{
		if (log == null)
			log = CLogger.getCLogger(getClass());
		log.info ("ejbActivate " + getStatus());
	}	//	ejbActivate

	/**
	 * Method ejbPassivate
	 * @throws EJBException
	 * @see javax.ejb.SessionBean#ejbPassivate()
	 */
	public void ejbPassivate() throws EJBException
	{
		log.info ("ejbPassivate " + getStatus());
	}	//	ejbPassivate

	/**
	 * Method ejbRemove
	 * @throws EJBException
	 * @see javax.ejb.SessionBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException
	{
		log.info ("ejbRemove " + getStatus());
	}	//	ejbRemove


	/**************************************************************************
	 * 	Dump SerialVersionUID of class 
	 *	@param clazz class
	 */
	protected static void dumpSVUID (Class clazz)
	{
		String s = clazz.getName() 
			+ " ==\nstatic final long serialVersionUID = "
			+ java.io.ObjectStreamClass.lookup(clazz).getSerialVersionUID()
			+ "L;\n";
		System.out.println (s);
	}	//	dumpSVUID

	/**
	 * 	imprime el UID de las clases usadas
	 * 	

org.openXpertya.process.ProcessInfo ==
static final long serialVersionUID = -1993220053515488725L;

org.openXpertya.util.CStatementVO ==
static final long serialVersionUID = -3393389471515956399L;

org.openXpertya.model.MQuery ==
static final long serialVersionUID = 1511402030597166113L;

org.openXpertya.model.POInfo ==
static final long serialVersionUID = -5976719579744948419L;

org.openXpertya.model.POInfoColumn ==
static final long serialVersionUID = -3983585608504631958L;

org.openXpertya.model.MWindowVO ==
static final long serialVersionUID = 3802628212531678981L;

org.openXpertya.model.MTabVO ==
static final long serialVersionUID = 9160212869277319305L;

org.openXpertya.model.MFieldVO ==
static final long serialVersionUID = 4385061125114436797L;

org.openXpertya.model.MLookupInfo ==
static final long serialVersionUID = -7958664359250070233L;

	 *
	 *	 *	@param args ignored
	 */
	public static void main (String[] args)
	{
		dumpSVUID(ProcessInfo.class);
		dumpSVUID(CStatementVO.class);
		dumpSVUID(MQuery.class);
		dumpSVUID(POInfo.class);
		dumpSVUID(POInfoColumn.class);
		dumpSVUID(MWindowVO.class);
		dumpSVUID(MTabVO.class);
		dumpSVUID(MFieldVO.class);
		dumpSVUID(MLookupInfo.class);
	}	//	main

}	

/*
 *  @(#)ServerBean.java   30.03.06
 * 
 *  Fin del fichero ServerBean.java
 *  
 *  Versión 2.2
 *
 */