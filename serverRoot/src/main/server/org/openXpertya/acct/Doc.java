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



package org.openXpertya.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MAmortization;
import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MCash;
import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInventory;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MJournal;
import org.openXpertya.model.MMatchInv;
import org.openXpertya.model.MMatchPO;
import org.openXpertya.model.MMovement;
import org.openXpertya.model.MNote;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPeriod;
import org.openXpertya.model.MProjectIssue;
import org.openXpertya.model.ModelValidationEngine;
import org.openXpertya.model.ModelValidator;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_M_Production;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DBException;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class Doc {

    /** Descripción de Campos */

    public static int[] documentsTableID = null;

    /** Descripción de Campos */

    public static String[] documentsTableName = null;

    static{
    	documentsTableID = new int[] {
    	        MInvoice.Table_ID,          // C_Invoice
    	        MAllocationHdr.Table_ID,    // C_Allocation
    	        MCash.Table_ID,             // C_Cash
    	        MBankStatement.Table_ID,    // C_BankStatement
    	        // Por ahora se comenta la contabilidad de los pedidos
    	        // MOrder.Table_ID,            // C_Order
    	        MPayment.Table_ID,          // C_Payment
    	        MInOut.Table_ID,            // M_InOut
    	        MInventory.Table_ID,        // M_Inventory
    	        MMovement.Table_ID,         // M_Movement
    	        X_M_Production.Table_ID,    // M_Production
    	        MJournal.Table_ID,          // GL_Journal
    	        MMatchInv.Table_ID,         // M_MatchInv
    	        MMatchPO.Table_ID,          // M_MatchPO
    	        MProjectIssue.Table_ID,     // C_ProjectIssue
    	        MAmortization.Table_ID,     // MAmortization
    	        MCreditCardSettlement.Table_ID // Credit Card Settlement
    	    };
    	
    	documentsTableName = new String[] {
    	        MInvoice.Table_Name,          // C_Invoice
    	        MAllocationHdr.Table_Name,    // C_Allocation
    	        MCash.Table_Name,             // C_Cash
    	        MBankStatement.Table_Name,    // C_BankStatement
    	        // Por ahora se comenta la contabilidad de los pedidos
    	        // MOrder.Table_Name,            // C_Order
    	        MPayment.Table_Name,          // C_Payment
    	        MInOut.Table_Name,            // M_InOut
    	        MInventory.Table_Name,        // M_Inventory
    	        MMovement.Table_Name,         // M_Movement
    	        X_M_Production.Table_Name,    // M_Production
    	        MJournal.Table_Name,          // GL_Journal
    	        MMatchInv.Table_Name,         // M_MatchInv
    	        MMatchPO.Table_Name,          // M_MatchPO
    	        MProjectIssue.Table_Name,     // C_ProjectIssue
    	        MAmortization.Table_Name, 		// MAmortization
    	        MCreditCardSettlement.Table_Name // Credit Card Settlement
    	    };

    }
    
    /** Descripción de Campos */

    public static final String DOCTYPE_ARInvoice = "ARI";

    /** Descripción de Campos */

    public static final String DOCTYPE_ARCredit = "ARC";

    /** Descripción de Campos */

    public static final String DOCTYPE_ARReceipt = "ARR";

    /** Descripción de Campos */

    public static final String DOCTYPE_ARProForma = "ARF";

    /** Descripción de Campos */

    public static final String DOCTYPE_APInvoice = "API";

    /** Descripción de Campos */

    public static final String DOCTYPE_APCredit = "APC";

    /** Descripción de Campos */

    public static final String DOCTYPE_APPayment = "APP";

    /** Descripción de Campos */

    public static final String DOCTYPE_BankStatement = "CMB";

    /** Descripción de Campos */

    public static final String DOCTYPE_CashJournal = "CMC";

    /** Descripción de Campos */

    public static final String DOCTYPE_Allocation = "CMA";

    /** Descripción de Campos */

    public static final String DOCTYPE_MatShipment = "MMS";

    /** Descripción de Campos */

    public static final String DOCTYPE_MatReceipt = "MMR";

    /** Descripción de Campos */

    public static final String DOCTYPE_MatInventory = "MMI";

    /** Descripción de Campos */

    public static final String DOCTYPE_MatMovement = "MMM";

    /** Descripción de Campos */

    public static final String DOCTYPE_MatProduction = "MMP";

    /** Descripción de Campos */

    public static final String DOCTYPE_MatMatchInv = "MXI";

    /** Descripción de Campos */

    public static final String DOCTYPE_MatMatchPO = "MXP";

    /** Descripción de Campos */

    public static final String DOCTYPE_GLJournal = "GLJ";

    /** Descripción de Campos */

    public static final String DOCTYPE_POrder = "POO";

    /** Descripción de Campos */

    public static final String DOCTYPE_SOrder = "SOO";

    /** Descripción de Campos */

    public static final String DOCTYPE_ProjectIssue = "PJI";

    public static final String DOCTYPE_CreditCardSettlement = "CCS";
    
    // Posting Status - AD_Reference_ID=234     //

    /** Descripción de Campos */

    public static final String STATUS_NotPosted = "N";

    /** Descripción de Campos */

    public static final String STATUS_NotBalanced = "b";

    /** Descripción de Campos */

    public static final String STATUS_NotConvertible = "c";

    /** Descripción de Campos */

    public static final String STATUS_PeriodClosed = "p";

    /** Descripción de Campos */

    public static final String STATUS_InvalidAccount = "i";

    /** Descripción de Campos */

    public static final String STATUS_PostPrepared = "y";

    /** Descripción de Campos */

    public static final String STATUS_Posted = "Y";

    /** Descripción de Campos */

    public static final String STATUS_Error = "E";

    /**
     * Descripción de Método
     *
     *
     * @param ass
     * @param AD_Table_ID
     *
     * @return
     */

    public static Doc get( MAcctSchema[] ass,int AD_Table_ID ) {
        Doc doc = null;

        if (AD_Table_ID == MInvoice.Table_ID)
            doc = new Doc_Invoice( ass,AD_Table_ID,MInvoice.Table_Name );
       	else if (AD_Table_ID == MAllocationHdr.Table_ID)
            doc = new Doc_Allocation( ass,AD_Table_ID,MAllocationHdr.Table_Name );
       	else if (AD_Table_ID == MCash.Table_ID)
            doc = new Doc_Cash( ass,AD_Table_ID,MCash.Table_Name );
       	else if (AD_Table_ID == MBankStatement.Table_ID)
            doc = new Doc_Bank( ass,AD_Table_ID,MBankStatement.Table_Name );
        // Por ahora se comenta la contabilidad de los pedidos
        // else if (AD_Table_ID == MOrder.Table_ID)
        //   doc = new Doc_Order( ass,AD_Table_ID,MOrder.Table_Name );
       	else if (AD_Table_ID == MPayment.Table_ID)
            doc = new Doc_Payment( ass,AD_Table_ID,MPayment.Table_Name );
       	else if (AD_Table_ID == MInOut.Table_ID)
            doc = new Doc_InOut( ass,AD_Table_ID,MInOut.Table_Name );
       	else if (AD_Table_ID == MInventory.Table_ID)
            doc = new Doc_Inventory( ass,AD_Table_ID,MInventory.Table_Name );
       	else if (AD_Table_ID == MMovement.Table_ID)
            doc = new Doc_Movement( ass,AD_Table_ID,MMovement.Table_Name );
       	else if (AD_Table_ID == X_M_Production.Table_ID)
            doc = new Doc_Production( ass,AD_Table_ID,X_M_Production.Table_Name );
       	else if (AD_Table_ID == MJournal.Table_ID)
            doc = new Doc_GLJournal( ass,AD_Table_ID,MJournal.Table_Name );
       	else if (AD_Table_ID == MMatchInv.Table_ID)
            doc = new Doc_MatchInv( ass,AD_Table_ID,MMatchInv.Table_Name );
       	else if (AD_Table_ID == MMatchPO.Table_ID)
            doc = new Doc_MatchPO( ass,AD_Table_ID,MMatchPO.Table_Name );
       	else if (AD_Table_ID == MProjectIssue.Table_ID)
            doc = new Doc_ProjectIssue( ass,AD_Table_ID,MProjectIssue.Table_Name );
       	else if (AD_Table_ID == MAmortization.Table_ID)
            doc = new Doc_Amortization( ass,AD_Table_ID,MAmortization.Table_Name );
       	else if (AD_Table_ID == MCreditCardSettlement.Table_ID)
            doc = new Doc_CreditCardSettlement( ass,AD_Table_ID,MCreditCardSettlement.Table_Name );
        
        if( doc == null ) {
            s_log.log( Level.SEVERE,"get - Unknown AD_Table_ID=" + AD_Table_ID );
        }

        return doc;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param Record_ID
     * @param force
     *
     * @return
     */

    public static boolean postImmediate( MAcctSchema[] ass,int AD_Table_ID,int Record_ID,boolean force ) {
        Doc doc = get( ass,AD_Table_ID );

        if( doc != null ) {
            return doc.post( Record_ID,force );
        }

        return false;
    }    // post

    /**
     * Invocado desde ZK.  TODO: Unificar logica de aplicacion contable (ZK / Swing)
     */
	public static String postImmediate (MAcctSchema[] ass, 
			int AD_Table_ID, int Record_ID, boolean force, String trxName)
		{
			Doc doc = get (ass, AD_Table_ID, Record_ID, trxName);
			if (doc != null)
				return doc.post( Record_ID,force)?null:"PostServerError";	//	repost
			return "NoDoc";
		}   //  post
    
	
	/**
	 *  Create Posting document
	 *	@param ass accounting schema
	 *  @param AD_Table_ID Table ID of Documents
	 *  @param Record_ID record ID to load
	 *  @param trxName transaction name
	 *  @return Document or null
	 */
	public static Doc get (MAcctSchema[] ass, int AD_Table_ID, int Record_ID, String trxName)
	{
		String TableName = null;
		for (int i = 0; i < getDocumentsTableID().length; i++)
		{
			if (getDocumentsTableID()[i] == AD_Table_ID)
			{
				TableName = getDocumentsTableName()[i];
				break;
			}
		}
		if (TableName == null)
		{
			s_log.severe("Not found AD_Table_ID=" + AD_Table_ID);
			return null;
		}
		//
		Doc doc = null;
		StringBuffer sql = new StringBuffer("SELECT * FROM ")
			.append(TableName)
			.append(" WHERE ").append(TableName).append("_ID=? AND Processed='Y'");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt (1, Record_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				doc = get (ass, AD_Table_ID);
			}
			else
				s_log.severe("Not Found: " + TableName + "_ID=" + Record_ID);
		}
		catch (Exception e)
		{
			s_log.log (Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; 
			pstmt = null;
		}
		return doc;
	}	//	get
	
	
	
    /** Descripción de Campos */

    protected static CLogger s_log = CLogger.getCLogger( Doc.class );

    
	public static int[] getDocumentsTableID() {
		fillDocumentsTableArrays();
		return documentsTableID;
	}
    
	public static String[] getDocumentsTableName() {
		fillDocumentsTableArrays();
		return documentsTableName;
	}
	
	private static void fillDocumentsTableArrays() {
		if (documentsTableID == null) {
			String sql = "SELECT t.AD_Table_ID, t.TableName " +
					"FROM AD_Table t, AD_Column c " +
					"WHERE t.AD_Table_ID=c.AD_Table_ID AND " +
					"c.ColumnName='Posted' AND " +
					"IsView='N' " +
					"ORDER BY t.AD_Table_ID";
			ArrayList<Integer> tableIDs = new ArrayList<Integer>();
			ArrayList<String> tableNames = new ArrayList<String>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					tableIDs.add(rs.getInt(1));
					tableNames.add(rs.getString(2));
				}
			}
			catch (SQLException e)
			{
				throw new DBException(e, sql);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
			//	Convert to array
			documentsTableID = new int[tableIDs.size()];
			documentsTableName = new String[tableIDs.size()];
			for (int i = 0; i < documentsTableID.length; i++)
			{
				documentsTableID[i] = tableIDs.get(i);
				documentsTableName[i] = tableNames.get(i);
			}
		}
	}
	
	
	/** Document Status      			*/
	private String				m_DocStatus = null;
	/** The Document				*/
	protected PO				p_po = null;
	/**	Actual Document Status  */
	protected String			p_Status = null;
	public String getPostStatus() {
		return p_Status;
	}
	/* If the transaction must be managed locally (false if it's managed externally by the caller) */ 
	private boolean m_manageLocalTrx;

	/** Error Message			*/
	protected String			p_Error = null;
	
	/** Document No      			*/
	private String				m_DocumentNo = null;
	
	public String get_TableName()
	{
		return p_po.get_TableName();
	}	//	get_TableName

	/**
	 * 	Get Record_ID
	 *	@return record id
	 */
	public int get_ID()
	{
		return p_po.getID();
	}	//	get_ID

	
	/**
	 * 	Get Document No
	 *	@return document No
	 */
	public String getDocumentNo()
	{
		if (m_DocumentNo != null)
			return m_DocumentNo;
		int index = p_po.get_ColumnIndex("DocumentNo");
		if (index == -1)
			index = p_po.get_ColumnIndex("Name");
		if (index == -1)
			throw new UnsupportedOperationException("No DocumentNo");
		m_DocumentNo = (String)p_po.get_Value(index);
		return m_DocumentNo;
	}	//	getDocumentNo

	
	/**
	 * 	Get AD_Org_ID
	 *	@return org
	 */
	public int getAD_Org_ID()
	{
		return p_po.getAD_Org_ID();
	}	//	getAD_Org_ID
	/**
	 * 	Get AD_Client_ID
	 *	@return client
	 */
	public int getAD_Client_ID()
	{
		return p_po.getAD_Client_ID();
	}	//	getAD_Client_ID

	/**
	 * 	Is Document Posted
	 *	@return true if posted
	 */
	public boolean isPosted()
	{
		int index = p_po.get_ColumnIndex("Posted");
		if (index != -1)
		{
			Object posted = p_po.get_Value(index);
			if (posted instanceof Boolean)
				return ((Boolean)posted).booleanValue();
			if (posted instanceof String)
				return "Y".equals(posted);
		}
		throw new IllegalStateException("No Posted");
	}	//	isPosted

	
	/**
	 * 	Delete Accounting
	 *	@return number of records
	 */
	private int deleteAcct()
	{
		StringBuffer sql = new StringBuffer ("DELETE Fact_Acct WHERE AD_Table_ID=")
			.append(get_Table_ID())
			.append(" AND Record_ID=").append(p_po.getID());
		int no = DB.executeUpdate(sql.toString(), getTrxName());
		if (no != 0)
			log.info("deleted=" + no);
		return no;
	}	//	deleteAcct
	
	
	public int get_Table_ID()
	{
		return p_po.get_Table_ID();
	}	//	get_Table_ID

	/**************************************************************************
	 *  Load Document Type and GL Info.
	 * 	Set p_DocumentType and p_GL_Category_ID
	 * 	@return document type (i.e. C_DocType.DocBaseType)
	 */
	public String getDocumentType()
	{
		if (m_DocumentType == null)
			setDocumentType(null);
		return m_DocumentType;
	}   //  getDocumentType
	private String				m_DocumentType = null;
	
	/**
	 *  Load Document Type and GL Info.
	 * 	Set p_DocumentType and p_GL_Category_ID
	 *	@param DocumentType
	 */
	public void setDocumentType (String DocumentType)
	{
		if (DocumentType != null)
			m_DocumentType = DocumentType;
		//  No Document Type defined
		if (m_DocumentType == null && getC_DocType_ID() != 0)
		{
			String sql = "SELECT DocBaseType, GL_Category_ID FROM C_DocType WHERE C_DocType_ID=?";
			PreparedStatement pstmt = null;
			ResultSet rsDT = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, getC_DocType_ID());
				rsDT = pstmt.executeQuery();
				if (rsDT.next())
				{
					m_DocumentType = rsDT.getString(1);
					m_GL_Category_ID = rsDT.getInt(2);
				}
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rsDT, pstmt);
				rsDT = null; 
				pstmt = null;
			}
		}
		if (m_DocumentType == null)
		{
			log.log(Level.SEVERE, "No DocBaseType for C_DocType_ID=" 
				+ getC_DocType_ID() + ", DocumentNo=" + getDocumentNo());
		}

		//  We have a document Type, but no GL info - search for DocType
		if (m_GL_Category_ID == 0)
		{
			String sql = "SELECT GL_Category_ID FROM C_DocType "
				+ "WHERE AD_Client_ID=? AND DocBaseType=?";
			try
			{
				PreparedStatement pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, getAD_Client_ID());
				pstmt.setString(2, m_DocumentType);
				ResultSet rsDT = pstmt.executeQuery();
				if (rsDT.next())
					m_GL_Category_ID = rsDT.getInt(1);
				rsDT.close();
				pstmt.close();
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, sql, e);
			}
		}

		//  Still no GL_Category - get Default GL Category
		if (m_GL_Category_ID == 0)
		{
			String sql = "SELECT GL_Category_ID FROM GL_Category "
				+ "WHERE AD_Client_ID=? "
				+ "ORDER BY IsDefault DESC";
			try
			{
				PreparedStatement pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, getAD_Client_ID());
				ResultSet rsDT = pstmt.executeQuery();
				if (rsDT.next())
					m_GL_Category_ID = rsDT.getInt(1);
				rsDT.close();
				pstmt.close();
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, sql, e);
			}
		}
		//
		if (m_GL_Category_ID == 0)
			log.log(Level.SEVERE, "No default GL_Category - " + toString());

		if (m_DocumentType == null)
			throw new IllegalStateException("Document Type not found");
	}	//	setDocumentType

	private int					m_GL_Category_ID = 0;

	/**
	 * 	Get C_DocType_ID
	 *	@return DocType
	 */
	public int getC_DocType_ID()
	{
		int index = p_po.get_ColumnIndex("C_DocType_ID");
		if (index != -1)
		{
			Integer ii = (Integer)p_po.get_Value(index);
			//	DocType does not exist - get DocTypeTarget
			if (ii != null && ii.intValue() == 0)
			{
				index = p_po.get_ColumnIndex("C_DocTypeTarget_ID");
				if (index != -1)
					ii = (Integer)p_po.get_Value(index);
			}
			if (ii != null)
				return ii.intValue();
		}
		return 0;
	}	//	getC_DocType_ID

	
	/**
	 * 	Get Accounting Date
	 *	@return currency
	 */
	public Timestamp getDateAcct()
	{
		if (m_DateAcct != null)
			return m_DateAcct;
		int index = p_po.get_ColumnIndex("DateAcct");
		if (index != -1)
		{
			m_DateAcct = (Timestamp)p_po.get_Value(index);
			if (m_DateAcct != null)
				return m_DateAcct;
		}
		throw new IllegalStateException("No DateAcct");
	}	//	getDateAcct
	/** Accounting Date				*/
	private Timestamp			m_DateAcct = null;

	
	
	/**
	 *  Post Document.
	 *  <pre>
	 *  - try to lock document (Processed='Y' (AND Processing='N' AND Posted='N'))
	 * 		- if not ok - return false
	 *          - postlogic (for all Accounting Schema)
	 *              - create Fact lines
	 *          - postCommit
	 *              - commits Fact lines and Document & sets Processing = 'N'
	 *              - if error - create Note
	 *  </pre>
	 *  @param force if true ignore that locked
	 *  @param repost if true ignore that already posted
	 *  @return null if posted error otherwise
	 */
	public final String post (boolean force, boolean repost)
	{
		if (m_DocStatus == null)
			;	//	return "No DocStatus for DocumentNo=" + getDocumentNo();
		else if (m_DocStatus.equals(DocumentEngine.STATUS_Completed)
			|| m_DocStatus.equals(DocumentEngine.STATUS_Closed)
			|| m_DocStatus.equals(DocumentEngine.STATUS_Voided)
			|| m_DocStatus.equals(DocumentEngine.STATUS_Reversed))
			;
		else
			return "Invalid DocStatus='" + m_DocStatus + "' for DocumentNo=" + getDocumentNo();
		//
		if (p_po.getAD_Client_ID() != m_ass[0].getAD_Client_ID())
		{
			String error = "AD_Client_ID Conflict - Document=" + p_po.getAD_Client_ID()
				+ ", AcctSchema=" + m_ass[0].getAD_Client_ID();
			log.severe(error);
			return error;
		}
		
		//  Lock Record ----
		String trxName = null;	//	outside trx if on server
		if (! m_manageLocalTrx)
			trxName = getTrxName(); // on trx if it's in client
		StringBuffer sql = new StringBuffer ("UPDATE ");
		sql.append(get_TableName()).append( " SET Processing='Y' WHERE ")
			.append(get_TableName()).append("_ID=").append(get_ID())
			.append(" AND Processed='Y' AND IsActive='Y'");
		if (!force)
			sql.append(" AND (Processing='N' OR Processing IS NULL)");
		if (!repost)
			sql.append(" AND Posted='N'");
		if (DB.executeUpdate(sql.toString(), trxName) == 1)
			log.info("Locked: " + get_TableName() + "_ID=" + get_ID());
		else
		{
			log.log(Level.SEVERE, "Resubmit - Cannot lock " + get_TableName() + "_ID=" 
				+ get_ID() + ", Force=" + force + ",RePost=" + repost);
			if (force)
				return "Cannot Lock - ReSubmit";
			return "Cannot Lock - ReSubmit or RePost with Force";
		}
		
		p_Error = loadDocumentDetails();
		if (p_Error != null)
			return p_Error;

		Trx trx = Trx.get(getTrxName(), true);
		//  Delete existing Accounting
		if (repost)
		{
			if (isPosted() && !isPeriodOpen())	//	already posted - don't delete if period closed
			{
				log.log(Level.SEVERE, toString() + " - Period Closed for already posed document");
				unlock();
				trx.commit(); trx.close();
				return "PeriodClosed";
			}
			//	delete it
			deleteAcct();
		}
		else if (isPosted())
		{
			log.log(Level.SEVERE, toString() + " - Document already posted");
			unlock();
			trx.commit(); trx.close();
			return "AlreadyPosted";
		}
		
		p_Status = STATUS_NotPosted;

		//  Create Fact per AcctSchema
		m_fact = new ArrayList<Fact>(); // m_fact = new Fact[]; 

		//  for all Accounting Schema
		boolean OK = true;
		//getPO().setDoc(this) TODO Hernandez;
		try
		{
			for (int i = 0; OK && i < m_ass.length; i++)
			{
				//	if acct schema has "only" org, skip
				boolean skip = false;
				if (m_ass[i].getAD_OrgOnly_ID() != 0)
				{
					//	Header Level Org
					skip = m_ass[i].isSkipOrg(getAD_Org_ID());
					//	Line Level Org
					if (p_lines != null)
					{
						for (int line = 0; skip && line < p_lines.length; line++)
						{
							skip = m_ass[i].isSkipOrg(p_lines[line].getAD_Org_ID());
							if (!skip)
								break;
						}
					}
				}
				if (skip)
					continue;
				//	post
				log.info("(" + i + ") " + p_po);
				p_Status = postLogic (i);
				if (!p_Status.equals(STATUS_Posted))
					OK = false;
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "", e);
			p_Status = STATUS_Error;
			p_Error = e.toString();
			OK = false;
		}

		String validatorMsg = null;
		// Call validator on before post
		if (p_Status.equals(STATUS_Posted)) {			
			validatorMsg = ModelValidationEngine.get().fireDocValidate(getPO(), ModelValidator.TIMING_BEFORE_POST);
			if (validatorMsg != null) {
				p_Status = STATUS_Error;
				p_Error = validatorMsg;
				OK = false;
			}
		}

		//  commitFact
		p_Status = postCommit (p_Status);

		if (p_Status.equals(STATUS_Posted)) {
			validatorMsg = ModelValidationEngine.get().fireDocValidate(getPO(), ModelValidator.TIMING_AFTER_POST);
			if (validatorMsg != null) {
				p_Status = STATUS_Error;
				p_Error = validatorMsg;
				OK = false;
			}
		}
		  
		//  Create Note
		if (!p_Status.equals(STATUS_Posted))
		{
			//  Insert Note
			String AD_MessageValue = "PostingError-" + p_Status;
			int AD_User_ID = p_po.getUpdatedBy();
			MNote note = new MNote (getCtx(), AD_MessageValue, AD_User_ID, 
				getAD_Client_ID(), getAD_Org_ID(), null);
			note.setRecord(p_po.get_Table_ID(), p_po.getID());
			//  Reference
			note.setReference(toString());	//	Document
			//	Text
			StringBuffer Text = new StringBuffer (Msg.getMsg(Env.getCtx(), AD_MessageValue));
			if (p_Error != null)
				Text.append(" (").append(p_Error).append(")");
			String cn = getClass().getName();
			Text.append(" - ").append(cn.substring(cn.lastIndexOf('.')))
				.append(" (").append(getDocumentType())
				.append(" - DocumentNo=").append(getDocumentNo())
				.append(", DateAcct=").append(getDateAcct().toString().substring(0,10))
				.append(", Amount=").append(getAmount())
				.append(", Sta=").append(p_Status)
				.append(" - PeriodOpen=").append(isPeriodOpen())
				.append(", Balanced=").append(isBalanced());
			note.setTextMsg(Text.toString());
			note.save();
			p_Error = Text.toString();
		}

		//  dispose facts
		for (int i = 0; i < m_fact.size(); i++) //TODO Hernandez m_fact.length;
		{
			Fact fact = m_fact.get(i);	// m_fact.[i];
			if (fact != null)
				fact.dispose();
		}
		p_lines = null;

		if (p_Status.equals(STATUS_Posted))
			return null;
		return p_Error;
	}   //  post
	
	
	protected PO getPO()
	{
		return p_po;
	}	//	getPO

	
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     */

    Doc( MAcctSchema[] ass ) {

        // log.info(p_TableName);

        p_vo = null;

//              m_ass = MAcctSchema.getClientAcctSchema (ctx, AD_Client_ID);

        m_ass = ass;
    }    // Doc

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private MAcctSchema[] m_ass = null;

    /** Descripción de Campos */

    private ArrayList<Fact> m_fact = null;

    /** Descripción de Campos */

    protected String m_trxName = null;

    /** Descripción de Campos */

    protected static final int NO_CURRENCY = -1;

    /** Descripción de Campos */

    private Properties m_ctx = null;

    /** Descripción de Campos */

    protected DocVO p_vo = null;

    /** Descripción de Campos */

    protected DocLine[] p_lines = new DocLine[ 0 ];

    /** Descripción de Campos */

    protected int p_AD_Table_ID = 0;

    /** Descripción de Campos */

    protected String p_TableName = null;

    /** Descripción de Campos */

    private int p_Record_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected Properties getCtx() {
        return m_ctx;
    }    // getCtx

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTableName() {
        return p_TableName;
    }    // getTableName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Table_ID() {
        return p_AD_Table_ID;
    }    // getAD_Table_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRecord_ID() {
        return p_Record_ID;
    }    // getRecord_ID

    /**
     * Descripción de Método
     *
     *
     * @param Record_ID
     * @param force
     *
     * @return
     */

    public final boolean post( int Record_ID,boolean force ) {
        boolean retValue = false;
        String  trxName  = null;    // no p_vo

        // Lock Record ----

        StringBuffer sql = new StringBuffer( "UPDATE " );

        sql.append( p_TableName ).append( " SET Processing='Y' WHERE " ).append( p_TableName ).append( "_ID=" ).append( Record_ID ).append( " AND Processed='Y' AND IsActive='Y'" );

        if( !force ) {
            sql.append( " AND (Processing='N' OR Processing IS NULL) AND Posted='N'" );
        }

        if( DB.executeUpdate( sql.toString(),trxName ) != 1 ) {
            log.log( Level.SEVERE,"Cannot lock Document - ignored: " + p_TableName + "_ID=" + Record_ID );

            return false;
        }

        // Get Record Info ----

        sql = new StringBuffer( "SELECT * FROM " );
        sql.append( p_TableName ).append( " WHERE AD_Client_ID=? AND " )    // additional security
            .append( p_TableName ).append( "_ID=?" );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString(),trxName );

            pstmt.setInt( 1,m_ass[ 0 ].getAD_Client_ID());
            pstmt.setInt( 2,Record_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = post( rs,force );    // ----
            }

            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Record_ID=" + Record_ID,e );
            retValue = false;
        }

        return retValue;
    }    // post

    /**
     * Descripción de Método
     *
     *
     * @param rs
     * @param force
     *
     * @return
     */

    private final boolean post( ResultSet rs,boolean force ) {

        // log.info("post");

        m_trxName = null;
        m_ctx     = new Properties( m_ass[ 0 ].getCtx());
        m_ctx.setProperty( "#AD_Client_ID",String.valueOf( m_ass[ 0 ].getAD_Client_ID()));

        // p_Record_ID

        p_vo = new DocVO();

        if( !loadDocument( rs,force )) {
            return false;
        }

        // Create Fact per AcctSchema

        m_fact = new ArrayList<Fact>( m_ass.length ); // m_fact = new Fact[ m_ass.length ];

        // for all Accounting Schema

        boolean OK = true;

        try {
            for( int i = 0;OK && (i < m_ass.length);i++ ) {
                log.info( "(" + i + "): " + p_vo );
                p_vo.Status = postLogic( i );

                if( !p_vo.Status.equals( STATUS_Posted )) {
                    OK = false;
                }
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"post",e );
            p_vo.Status = STATUS_Error;
            p_vo.Error  = e.toString();
            OK          = false;
        }

        // commitFact

        p_vo.Status = postCommit( p_vo.Status );

        // Create Note

        if( !p_vo.Status.equals( STATUS_Posted )) {

            // Insert Note

            String AD_MessageValue = "PostingError-" + p_vo.Status;
            int    AD_User_ID      = p_vo.UpdatedBy;
            MNote  note            = new MNote( getCtx(),AD_MessageValue,AD_User_ID,p_vo.AD_Client_ID,p_vo.AD_Org_ID,null );

            note.setRecord( p_AD_Table_ID,p_Record_ID );

            // Reference

            note.setReference( toString());    // Document

            // Text

            StringBuffer Text = new StringBuffer( Msg.getMsg( Env.getCtx(),AD_MessageValue ));

            if( p_vo.Error != null ) {
                Text.append( " (" ).append( p_vo.Error ).append( ")" );
            }

            String cn = getClass().getName();

            Text.append( " - " ).append( cn.substring( cn.lastIndexOf( '.' ))).append( " (" ).append( p_vo.DocumentType ).append( " - DocumentNo=" ).append( p_vo.DocumentNo ).append( ", DateAcct=" ).append( p_vo.DateAcct.toString().substring( 0,10 )).append( ", Amount=" ).append( getAmount()).append( ", Sta=" ).append( p_vo.Status ).append( " - PeriodOpen=" ).append( isPeriodOpen()).append( ", Balanced=" ).append( isBalanced());
            note.setTextMsg( Text.toString());
            note.save();
        }

        // dispose facts

        for( int i = 0;i < m_fact.size();i++ ) { //m_fact.length //TODO Hernandez
            if( m_fact.get(i) != null ) { //m_fact [i]
                m_fact.get(i).dispose();
            }
        }

        p_lines   = null;
        m_trxName = null;

        return p_vo.Status.equals( STATUS_Posted );
    }    // post

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    private final String postLogic( int index ) {

        // rejectUnbalanced

        if( !m_ass[ index ].isSuspenseBalancing() &&!isBalanced()) {
            return STATUS_NotBalanced;
        }

        // rejectUnconvertible

        if( !isConvertible( m_ass[ index ] )) {
            return STATUS_NotConvertible;
        }

        // rejectPeriodClosed

        if( !isPeriodOpen()) {
            return STATUS_PeriodClosed;
        }

        // createFacts

        m_fact.add(index, createFact( m_ass[ index ] )); //m_fact[ index ] = createFact( m_ass[ index ] );

        if( m_fact.get(index) == null ) {  ////m_fact[ index ]
            return STATUS_Error;
        }

        p_vo.Status = STATUS_PostPrepared;

        // Disyte - FB
        // Invierte importes negativos
        m_fact.get(index).correctNegativeAmounts(); ////m_fact[ index ]
        
        // check accounts

        if( !m_fact.get(index).checkAccounts()) {
            return STATUS_InvalidAccount;
        }

        // distribute

        if( !m_fact.get(index).distribute()) {
            return STATUS_Error;
        }

        // Balanceo del asiento
        doBalancing(index);

        // Modificaciones custom para cada tipo de documento

        String status = applyCustomSettings(m_fact.get(index), index);
        if (status != null)
        	return status;
        
        return STATUS_Posted;
    }    // postLogic

    /**
     * Realiza acciones de balanceo sobre el asiento contable
     * @param index índice del esquema contable
     */
    protected void doBalancing(int index){
    	doBalancing(index, 0);
    }
    
    protected void doBalancing(int index, int projectID){
    	// balanceSource
    	if( !m_fact.get(index).isSourceBalanced()) {
    		m_fact.get(index).balanceSource(projectID);
    	}

    	// balanceSegments

    	if( !m_fact.get(index).isSegmentBalanced()) {
    		m_fact.get(index).balanceSegments(projectID);
    	}

    	// balanceAccounting

    	if( !m_fact.get(index).isAcctBalanced()) {
    		m_fact.get(index).balanceAccounting(projectID);
    	}
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param status
     *
     * @return
     */

    private final String postCommit( String status ) {
        log.info( "Sta=" + status + " DT=" + p_vo.DocumentType + " ID=" + p_Record_ID );
        p_vo.Status = status;

        Trx trx = Trx.get( getTrxName(),true );

        try {

            // *** Transaction Start       ***
            // Commit Facts

            if( status.equals( STATUS_Posted )) {
                for( int i = 0;i < m_fact.size();i++ ) { //m_fact.length
                    if( (m_fact.get(i) != null) && m_fact.get(i).save( getTrxName())) {
                        ;
                    } else {
                        log.log( Level.SEVERE,"(fact not saved) ... rolling back" );
                        trx.rollback();
                        trx.close();
                        unlock();

                        return STATUS_Error;
                    }
                }
            }

            // Commit Doc

            if( !save( getTrxName()))    // contains unlock & document status update
            {
                log.log( Level.SEVERE,"(doc not saved) ... rolling back" );
                trx.rollback();
                trx.close();
                unlock();

                return STATUS_Error;
            }

            // Success

            trx.commit();
            trx.close();
            trx = null;

            // *** Transaction End         ***

        } catch( Exception e ) {
            log.log( Level.SEVERE,"... rolling back",e );
            status = STATUS_Error;

            try {
                if( trx != null ) {
                    trx.rollback();
                }
            } catch( Exception e2 ) {
            }

            try {
                if( trx != null ) {
                    trx.close();
                }

                trx = null;
            } catch( Exception e3 ) {
            }

            unlock();
        }

        p_vo.Status = status;

        return status;
    }    // postCommit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String getTrxName() {
        if( m_trxName == null ) {
            if( p_vo != null ) {
                m_trxName = "Post" + p_vo.DocumentType + p_Record_ID;
            } else {
                m_trxName = Trx.createTrxName( "Post" );
            }
        }

        return m_trxName;
    }    // getTrxName

    /**
     * Descripción de Método
     *
     */

    private void unlock() {
        StringBuffer sql = new StringBuffer( "UPDATE " );

        sql.append( p_TableName ).append( " SET Processing='N' WHERE " ).append( p_TableName ).append( "_ID=" ).append( p_Record_ID );
        DB.executeUpdate( sql.toString(),null );    // no trx
    }                                               // unlock

    // General Document Methods

    /**
     * Descripción de Método
     *
     *
     * @param rs
     * @param force
     *
     * @return
     */

    private boolean loadDocument( ResultSet rs,boolean force ) {
        log.fine( p_TableName );

        //

        p_vo.Status = STATUS_Error;

        String Name = null;

        try {
            String            key  = p_TableName + "_ID";
            ResultSetMetaData rsmd = rs.getMetaData();

            for( int i = 1;i <= rsmd.getColumnCount();i++ ) {
                String col = rsmd.getColumnName( i );

                if( col.equalsIgnoreCase( key )) {
                    p_Record_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "AD_Client_ID" )) {
                    p_vo.AD_Client_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "AD_Org_ID" )) {
                    p_vo.AD_Org_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_BPartner_ID" )) {
                    p_vo.C_BPartner_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_BPartner_Location_ID" )) {
                    p_vo.C_BPartner_Location_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "M_Product_ID" )) {
                    p_vo.M_Product_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "AD_OrgTrx_ID" )) {
                    p_vo.AD_OrgTrx_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_SalesRegion_ID" )) {
                    p_vo.C_SalesRegion_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_Project_ID" )) {
                    p_vo.C_Project_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_Campaign_ID" )) {
                    p_vo.C_Campaign_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_Activity_ID" )) {
                    p_vo.C_Activity_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_LocFrom_ID" )) {
                    p_vo.C_LocFrom_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_LocTo_ID" )) {
                    p_vo.C_LocTo_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "User1_ID" )) {
                    p_vo.User1_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "User2_ID" )) {
                    p_vo.User2_ID = rs.getInt( i );

                    //

                } else if( col.equalsIgnoreCase( "DocumentNo" )) {
                    p_vo.DocumentNo = rs.getString( i );
                } else if( col.equalsIgnoreCase( "Description" )) {
                    p_vo.Description = rs.getString( i );
                } else if( col.equalsIgnoreCase( "Name" )) {
                    Name = rs.getString( i );
                } else if( col.equalsIgnoreCase( "DateAcct" )) {
                    p_vo.DateAcct = rs.getTimestamp( i );
                } else if( col.equalsIgnoreCase( "DateDoc" )) {
                    p_vo.DateDoc = rs.getTimestamp( i );
                } else if( col.equalsIgnoreCase( "C_Period_ID" )) {
                    p_vo.C_Period_ID = rs.getInt( i );

                    //

                } else if( col.equalsIgnoreCase( "C_Currency_ID" )) {
                    p_vo.C_Currency_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_ConversionType_ID" )) {
                    p_vo.C_ConversionType_ID = rs.getInt( i );

                    //

                } else if( col.equalsIgnoreCase( "C_DocType_ID" )) {
                    p_vo.C_DocType_ID = rs.getInt( i );

                    // Special Document Fields

                } else if( col.equalsIgnoreCase( "C_Charge_ID" )) {
                    p_vo.C_Charge_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "ChargeAmt" )) {
                    p_vo.ChargeAmt = rs.getBigDecimal( i );
                } else if( col.equalsIgnoreCase( "C_BankAccount_ID" )) {
                    p_vo.C_BankAccount_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "M_Warehouse_ID" )) {
                    p_vo.M_Warehouse_ID = rs.getInt( i );

                    //

                } else if( col.equalsIgnoreCase( "Posted" )) {
                    p_vo.Posted = "Y".equals( rs.getString( i ));
                } else if( col.equalsIgnoreCase( "UpdatedBy" )) {
                    p_vo.UpdatedBy = rs.getInt( i );
                }
            }    // for all columns

            p_vo.Status = STATUS_NotPosted;
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"load " + p_TableName,e );
        }

        // Call Document Specific Info

        if( !loadDocumentDetails( rs )) {
            loadDocumentType();
        }

        // Fill Acct/Trx Date

        if( p_vo.DateAcct == null & p_vo.DateDoc != null ) {
            p_vo.DateAcct = p_vo.DateDoc;
        }

        if( p_vo.DateDoc == null & p_vo.DateAcct != null ) {
            p_vo.DateDoc = p_vo.DateAcct;
        }

        // DocumentNo (or Name)

        if( (p_vo.DocumentNo == null) || (p_vo.DocumentNo.length() == 0) ) {
            p_vo.DocumentNo = Name;
        }

        if( (p_vo.DocumentNo == null) || (p_vo.DocumentNo.length() == 0) ) {
            p_vo.DocumentNo = "";
        }

        // Check Mandatory Info

        String error = "";

        if( p_Record_ID == 0 ) {
            error += " Record_ID";
        }

        if( p_vo.AD_Client_ID == 0 ) {
            error += " AD_Client_ID";
        }

        if( p_vo.AD_Org_ID == 0 ) {
            error += " AD_Org_ID";
        }

        if( p_vo.C_Currency_ID == 0 ) {
            error += " C_Currency_ID";
        }

        if( p_vo.DateAcct == null ) {
            error += " DateAcct";
        }

        if( p_vo.DateDoc == null ) {
            error += " DateDoc";
        }

        if( error.length() > 0 ) {
            log.log( Level.SEVERE,toString() + " - Mandatory info missing: " + error );

            return false;
        }

        // Delete existing Accounting

        if( force ) {
            if( p_vo.Posted &&!isPeriodOpen())    // already posted - don't delete if period closed
            {
                log.log( Level.SEVERE,toString() + " - Period Closed for already posed document" );

                return false;
            }

            // delete it

            StringBuffer sql = new StringBuffer( "DELETE Fact_Acct " + "WHERE AD_Table_ID=" );

            sql.append( p_AD_Table_ID ).append( " AND Record_ID=" ).append( p_Record_ID );

            int no = DB.executeUpdate( sql.toString(),null);

            log.info( "deleted=" + no );
        } else if( p_vo.Posted ) {
            log.log( Level.SEVERE,toString() + " - Document already posted" );

            return false;
        }

        return true;
    }    // loadDocument

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean loadDocumentType() {
        boolean retValue = true;

        // No Document Type defined

        if( (p_vo.DocumentType == null) && (p_vo.C_DocType_ID != 0) ) {
            String sql = "SELECT DocBaseType, GL_Category_ID FROM C_DocType WHERE C_DocType_ID=?";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,p_vo.C_DocType_ID );

                ResultSet rsDT = pstmt.executeQuery();

                if( rsDT.next()) {
                    p_vo.DocumentType   = rsDT.getString( 1 );
                    p_vo.GL_Category_ID = rsDT.getInt( 2 );
                }

                rsDT.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,sql,e );
            }
        }

        if( p_vo.DocumentType == null ) {
            log.log( Level.SEVERE,"No DocBaseType setup for C_DocType_ID=" + p_vo.C_DocType_ID + ", DocumentNo=" + p_vo.DocumentNo );
            retValue = false;
        }

        // We have a document Type, but no GL info - search for DocType

        if( p_vo.GL_Category_ID == 0 ) {
            String sql = "SELECT GL_Category_ID FROM C_DocType " + "WHERE AD_Client_ID=? AND DocBaseType=?";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,p_vo.AD_Client_ID );
                pstmt.setString( 2,p_vo.DocumentType );

                ResultSet rsDT = pstmt.executeQuery();

                if( rsDT.next()) {
                    p_vo.GL_Category_ID = rsDT.getInt( 1 );
                }

                rsDT.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,sql,e );
            }
        }

        // Still no GL_Category - get Default GL Category

        if( p_vo.GL_Category_ID == 0 ) {
            String sql = "SELECT GL_Category_ID FROM GL_Category " + "WHERE AD_Client_ID=? " + "ORDER BY IsDefault DESC";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,p_vo.AD_Client_ID );

                ResultSet rsDT = pstmt.executeQuery();

                if( rsDT.next()) {
                    p_vo.GL_Category_ID = rsDT.getInt( 1 );
                }

                rsDT.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,sql,e );
            }
        }

        if( p_vo.GL_Category_ID == 0 ) {
            log.log( Level.SEVERE,"No default GL_Category - " + toString());
            retValue = false;
        }

        // Budget

        p_vo.GL_Budget_ID = 0;

        return retValue;
    }    // loadDocumentType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isBalanced() {

        // Multi-Currency documents are source balanced by definition

        if( p_vo.MultiCurrency ) {
            return true;
        }

        //

        boolean retValue = getBalance().compareTo( Env.ZERO ) == 0;

        if( retValue ) {
            log.fine( "Yes " + p_vo.toString());
        } else {
            log.warning( "NO - " + p_vo.toString());
        }

        return retValue;
    }    // isBalanced

    /**
     * Descripción de Método
     *
     *
     * @param acctSchema
     *
     * @return
     */

    public boolean isConvertible( MAcctSchema acctSchema ) {

        // No Currency in document

        if( p_vo.C_Currency_ID == NO_CURRENCY ) {
            log.fine( "(none) - " + p_vo.toString());

            return true;
        }

        // Get All Currencies

        HashSet set = new HashSet();

        set.add( new Integer( p_vo.C_Currency_ID ));

        for( int i = 0;(p_lines != null) && (i < p_lines.length);i++ ) {
            int C_Currency_ID = p_lines[ i ].getC_Currency_ID();

            if( C_Currency_ID != NO_CURRENCY ) {
                set.add( new Integer( C_Currency_ID ));
            }
        }

        // just one and the same

        if( (set.size() == 1) && (acctSchema.getC_Currency_ID() == p_vo.C_Currency_ID) ) {
            log.fine( "(same) Cur=" + p_vo.C_Currency_ID + " - " + p_vo.toString());

            return true;
        }

        boolean  convertible = true;
        Iterator it          = set.iterator();

        while( it.hasNext() && convertible ) {
            int C_Currency_ID = (( Integer )it.next()).intValue();

            if( C_Currency_ID != acctSchema.getC_Currency_ID()) {
                BigDecimal amt = MConversionRate.getRate( C_Currency_ID,acctSchema.getC_Currency_ID(),p_vo.DateAcct,p_vo.C_ConversionType_ID,p_vo.AD_Client_ID,p_vo.AD_Org_ID );

                if( amt == null ) {
                    convertible = false;
                    log.warning( "NOT from C_Currency_ID=" + C_Currency_ID + " to " + acctSchema.getC_Currency_ID() + " - " + p_vo.toString());
                } else {
                    log.fine( "From C_Currency_ID=" + C_Currency_ID );
                }
            }
        }

        log.fine( "Convertible=" + convertible + ", AcctSchema C_Currency_ID=" + acctSchema.getC_Currency_ID() + " - " + p_vo.toString());

        return convertible;
    }    // isConvertible

    /**
     * Descripción de Método
     *
     */

    public void setC_Period_ID() {
        MPeriod period = null;

        // Period defined in GL Journal (e.g. adjustment period)

        if( p_vo.C_Period_ID > 0 ) {
            period = MPeriod.get( getCtx(),p_vo.C_Period_ID,m_trxName );
        } else {
            period = MPeriod.get( getCtx(),p_vo.DateAcct );
        }

        // Is Period Open?

        if( (period != null) && (period.isOpen( p_vo.DocumentType ) || period.isPermanentlyOpen( p_vo.DocumentType ))) {
            p_vo.C_Period_ID = period.getC_Period_ID();
        } else {
            p_vo.C_Period_ID = -1;
        }

        //

        log.fine(    // + p_vo.AD_Client_ID + " - "
            p_vo.DateAcct + " - " + p_vo.DocumentType + " => " + p_vo.C_Period_ID );
    }    // setC_Period_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPeriodOpen() {
        setC_Period_ID();

        boolean open = p_vo.C_Period_ID > 0;

        if( open ) {
            log.fine( "Yes - " + p_vo.toString());
        } else {
            log.warning( "NO - " + p_vo.toString());
        }

        return open;
    }    // isPeriodOpen

    /** Descripción de Campos */

    public static final int AMTTYPE_Gross = 0;

    /** Descripción de Campos */

    public static final int AMTTYPE_Net = 1;

    /** Descripción de Campos */

    public static final int AMTTYPE_Charge = 2;

    /**
     * Descripción de Método
     *
     *
     * @param AmtType
     *
     * @return
     */

    public BigDecimal getAmount( int AmtType ) {
        if( (AmtType < 0) || (AmtType >= p_vo.Amounts.length) ) {
            return null;
        }

        return p_vo.Amounts[ AmtType ];
    }    // getAmount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAmount() {
        return p_vo.Amounts[ 0 ];
    }    // getAmount

    /** Descripción de Campos */

    public static final int ACCTTYPE_Charge = 0;

    /** Descripción de Campos */

    public static final int ACCTTYPE_C_Receivable = 1;

    /** Descripción de Campos */

    public static final int ACCTTYPE_V_Liability = 2;

    /** Descripción de Campos */

    public static final int ACCTTYPE_V_Liability_Services = 3;

    /** Descripción de Campos */

    public static final int ACCTTYPE_UnallocatedCash = 10;

    /** Descripción de Campos */

    public static final int ACCTTYPE_BankInTransit = 11;

    /** Descripción de Campos */

    public static final int ACCTTYPE_PaymentSelect = 12;

    /** Descripción de Campos */

    public static final int ACCTTYPE_C_Prepayment = 13;

    /** Descripción de Campos */

    public static final int ACCTTYPE_V_Prepayment = 14;

    /** Descripción de Campos */

    public static final int ACCTTYPE_CashAsset = 20;

    /** Descripción de Campos */

    public static final int ACCTTYPE_CashTransfer = 21;

    /** Descripción de Campos */

    public static final int ACCTTYPE_CashExpense = 22;

    /** Descripción de Campos */

    public static final int ACCTTYPE_CashReceipt = 23;

    /** Descripción de Campos */

    public static final int ACCTTYPE_CashDifference = 24;

    /** Descripción de Campos */

    public static final int ACCTTYPE_DiscountExp = 30;

    /** Descripción de Campos */

    public static final int ACCTTYPE_DiscountRev = 31;

    /** Descripción de Campos */

    public static final int ACCTTYPE_WriteOff = 32;

    /** Descripción de Campos */

    public static final int ACCTTYPE_BankAsset = 40;

    /** Descripción de Campos */

    public static final int ACCTTYPE_InterestRev = 41;

    /** Descripción de Campos */

    public static final int ACCTTYPE_InterestExp = 42;

    /** Descripción de Campos */

    public static final int ACCTTYPE_InvDifferences = 50;

    /** Descripción de Campos */

    public static final int ACCTTYPE_NotInvoicedReceipts = 51;

    /** Descripción de Campos */

    public static final int ACCTTYPE_ProjectAsset = 61;

    /** Descripción de Campos */

    public static final int ACCTTYPE_ProjectWIP = 62;

    /** Descripción de Campos */

    public static final int ACCTTYPE_PPVOffset = 101;

    /**
     * Descripción de Método
     *
     *
     * @param AcctType
     * @param as
     *
     * @return
     */

    public final MAccount getAccount( int AcctType,MAcctSchema as ) {
        int    para_1 = 0;    // first parameter (second is always AcctSchema)
        String sql    = null;

        if( AcctType == ACCTTYPE_Charge )    // see getChargeAccount in DocLine
        {
            int cmp = getAmount( AMTTYPE_Charge ).compareTo( Env.ZERO );

            if( cmp == 0 ) {
                return null;
            } else if( cmp < 0 ) {
                sql = "SELECT CH_Expense_Acct FROM C_Charge_Acct WHERE C_Charge_ID=? AND C_AcctSchema_ID=?";
            } else {
                sql = "SELECT CH_Revenue_Acct FROM C_Charge_Acct WHERE C_Charge_ID=? AND C_AcctSchema_ID=?";
            }

            para_1 = p_vo.C_Charge_ID;
        } else if( AcctType == ACCTTYPE_V_Liability ) {
            sql = "SELECT V_Liability_Acct FROM C_BP_Vendor_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BPartner_ID;
        } else if( AcctType == ACCTTYPE_V_Liability_Services ) {
            sql = "SELECT V_Liability_Services_Acct FROM C_BP_Vendor_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BPartner_ID;
        } else if( AcctType == ACCTTYPE_C_Receivable ) {
            sql = "SELECT C_Receivable_Acct FROM C_BP_Customer_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BPartner_ID;
        } else if( AcctType == ACCTTYPE_V_Prepayment ) {
            sql = "SELECT V_Prepayment_Acct FROM C_BP_Vendor_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BPartner_ID;
        } else if( AcctType == ACCTTYPE_C_Prepayment ) {
            sql = "SELECT C_Prepayment_Acct FROM C_BP_Customer_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BPartner_ID;
        } else if( AcctType == ACCTTYPE_UnallocatedCash ) {
            sql = "SELECT B_UnallocatedCash_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BankAccount_ID;
        } else if( AcctType == ACCTTYPE_BankInTransit ) {
            sql = "SELECT B_InTransit_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BankAccount_ID;
        } else if( AcctType == ACCTTYPE_PaymentSelect ) {
            sql = "SELECT B_PaymentSelect_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BankAccount_ID;
        } else if( AcctType == ACCTTYPE_DiscountExp ) {
            sql = "SELECT a.PayDiscount_Exp_Acct FROM C_BP_Group_Acct a, C_BPartner bp " + "WHERE a.C_BP_Group_ID=bp.C_BP_Group_ID AND bp.C_BPartner_ID=? AND a.C_AcctSchema_ID=?";
            para_1 = p_vo.C_BPartner_ID;
        } else if( AcctType == ACCTTYPE_DiscountRev ) {
            sql = "SELECT PayDiscount_Rev_Acct FROM C_BP_Group_Acct a, C_BPartner bp " + "WHERE a.C_BP_Group_ID=bp.C_BP_Group_ID AND bp.C_BPartner_ID=? AND a.C_AcctSchema_ID=?";
            para_1 = p_vo.C_BPartner_ID;
        } else if( AcctType == ACCTTYPE_WriteOff ) {
            sql = "SELECT WriteOff_Acct FROM C_BP_Group_Acct a, C_BPartner bp " + "WHERE a.C_BP_Group_ID=bp.C_BP_Group_ID AND bp.C_BPartner_ID=? AND a.C_AcctSchema_ID=?";
            para_1 = p_vo.C_BPartner_ID;
        } else if( AcctType == ACCTTYPE_BankAsset ) {
            sql = "SELECT B_Asset_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BankAccount_ID;
        } else if( AcctType == ACCTTYPE_InterestRev ) {
            sql = "SELECT B_InterestRev_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BankAccount_ID;
        } else if( AcctType == ACCTTYPE_InterestExp ) {
            sql = "SELECT B_InterestExp_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_BankAccount_ID;
        } else if( AcctType == ACCTTYPE_CashAsset ) {
            sql = "SELECT CB_Asset_Acct FROM C_CashBook_Acct WHERE C_CashBook_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_CashBook_ID;
        } else if( AcctType == ACCTTYPE_CashTransfer ) {
            sql = "SELECT CB_CashTransfer_Acct FROM C_CashBook_Acct WHERE C_CashBook_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_CashBook_ID;
        } else if( AcctType == ACCTTYPE_CashExpense ) {
            sql = "SELECT CB_Expense_Acct FROM C_CashBook_Acct WHERE C_CashBook_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_CashBook_ID;
        } else if( AcctType == ACCTTYPE_CashReceipt ) {
            sql = "SELECT CB_Receipt_Acct FROM C_CashBook_Acct WHERE C_CashBook_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_CashBook_ID;
        } else if( AcctType == ACCTTYPE_CashDifference ) {
            sql = "SELECT CB_Differences_Acct FROM C_CashBook_Acct WHERE C_CashBook_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_CashBook_ID;
        } else if( AcctType == ACCTTYPE_InvDifferences ) {
            sql = "SELECT W_Differences_Acct FROM M_Warehouse_Acct WHERE M_Warehouse_ID=? AND C_AcctSchema_ID=?";

            // "SELECT W_Inventory_Acct, W_Revaluation_Acct, W_InvActualAdjust_Acct FROM M_Warehouse_Acct WHERE M_Warehouse_ID=? AND C_AcctSchema_ID=?";

            para_1 = p_vo.M_Warehouse_ID;
        } else if( AcctType == ACCTTYPE_NotInvoicedReceipts ) {
            sql = "SELECT NotInvoicedReceipts_Acct FROM C_BP_Group_Acct a, C_BPartner bp " + "WHERE a.C_BP_Group_ID=bp.C_BP_Group_ID AND bp.C_BPartner_ID=? AND a.C_AcctSchema_ID=?";
            para_1 = p_vo.C_BPartner_ID;
        } else if( AcctType == ACCTTYPE_ProjectAsset ) {
            sql = "SELECT PJ_Asset_Acct FROM C_Project_Acct WHERE C_Project_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_Project_ID;
        } else if( AcctType == ACCTTYPE_ProjectWIP ) {
            sql = "SELECT PJ_WIP_Acct FROM C_Project_Acct WHERE C_Project_ID=? AND C_AcctSchema_ID=?";
            para_1 = p_vo.C_Project_ID;
        } else if( AcctType == ACCTTYPE_PPVOffset ) {
            sql = "SELECT PPVOffset_Acct FROM C_AcctSchema_GL WHERE C_AcctSchema_ID=?";
            para_1 = -1;
        } else {
            log.severe( "getAccount - Not found AcctType=" + AcctType );

            return null;
        }

        // Do we have sql & Parameter

        if( (sql == null) || (para_1 == 0) ) {
            log.severe( "No Parameter for AcctType=" + AcctType + " - SQL=" + sql );

            return null;
        }

        // Get Acct

        int Account_ID = 0;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            if( para_1 == -1 ) {    // GL Accounts
                pstmt.setInt( 1,as.getC_AcctSchema_ID());
            } else {
                pstmt.setInt( 1,para_1 );
                pstmt.setInt( 2,as.getC_AcctSchema_ID());
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                Account_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"AcctType=" + AcctType + " - SQL=" + sql,e );

            return null;
        }

        // No account

        if( Account_ID == 0 ) {
            log.severe( "NO account Type=" + AcctType + ", Record=" + p_Record_ID );

            return null;
        }

        // Return Account

        MAccount acct = MAccount.get( as.getCtx(),Account_ID );

        return acct;
    }    // getAccount

    /**
     * Descripción de Método
     *
     *
     * @param trxName
     *
     * @return
     */

    private final boolean save( String trxName ) {
        log.fine( toString() + "->" + p_vo.Status );

        StringBuffer sql = new StringBuffer( "UPDATE " );

        sql.append( p_TableName ).append( " SET Posted='" ).append( p_vo.Status ).append( "',Processing='N' " ).append( "WHERE " ).append( p_TableName ).append( "_ID=" ).append( p_Record_ID );

        int no = DB.executeUpdate( sql.toString(),trxName );

        return no == 1;
    }    // save

    /**
     * Descripción de Método
     *
     *
     * @param Record_ID
     *
     * @return
     */

    public DocLine getDocLine( int Record_ID ) {
        if( (p_lines == null) || (p_lines.length == 0) || (Record_ID == 0) ) {
            return null;
        }

        for( int i = 0;i < p_lines.length;i++ ) {
            if( p_lines[ i ].getTrxLine_ID() == Record_ID ) {
                return p_lines[ i ];
            }
        }

        return null;
    }    // getDocLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        if( p_vo == null ) {
            return "Doc";
        }

        return p_vo.toString();
    }    // toString

    
    protected int getSchemaCurrency(FactLine factLine)
    {
    	MAcctSchema as = MAcctSchema.get( factLine.getCtx(), factLine.getC_AcctSchema_ID());
		return as.getC_Currency_ID();
    }
    
    // To be overwritten by Subclasses

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected abstract boolean loadDocumentDetails( ResultSet rs );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract BigDecimal getBalance();

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    public abstract Fact createFact( MAcctSchema as );
    
    /**
     * Permite realizar modificaciones custom en el paso inmediatamente previo a la persistencia de los asientos
     * @param fact el asiento con sus lineas a pa
     * @param index índice del esquema contable
     * @return Debe devolver un STATUS (STATUS_Posted, STATUS_Error, etc).
     */
    public abstract String applyCustomSettings( Fact fact, int index );
    
	/**
	 *  Load Document Details
	 *  @return error message or null
	 *  TODO: Subclasses MUST implement this method!
	 */
	protected abstract String loadDocumentDetails ();

	
    
}    // Doc



/*
 *  @(#)Doc.java   24.03.06
 * 
 *  Fin del fichero Doc.java
 *  
 *  Versión 2.2
 *
 */
