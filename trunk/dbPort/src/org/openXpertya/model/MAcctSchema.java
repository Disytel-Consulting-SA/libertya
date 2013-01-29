/*
 * @(#)MAcctSchema.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import org.openXpertya.report.MReportTree;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.KeyNamePair;


//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *  Accounting Schema Model (base)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *  @version    $Id: MAcctSchema.java,v 1.15 2005/04/25 05:04:21 jjanke Exp $
 */
public class MAcctSchema extends X_C_AcctSchema {

    /** Cache of Client AcctSchema Arrays */
    private static CCache	s_schema	= new CCache("AD_ClientInfo", 3);	// 3 clients

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MAcctSchema.class);

    /** Cache of AcctSchemas */
    private static CCache	s_cache	= new CCache("C_AcctSchema", 3);	// 3 accounting schemas

    /** Element List */
    private MAcctSchemaElement[]	m_elements	= null;

    /** GL Info */
    private MAcctSchemaGL	m_gl	= null;

    /** Default Info */
    private MAcctSchemaDefault	m_default	= null;

    /** Descripción de Campo */
    private MAccount	m_SuspenseError_Acct	= null;

    /** Descripción de Campo */
    private MAccount	m_DueTo_Acct	= null;

    /** Descripción de Campo */
    private MAccount	m_DueFrom_Acct	= null;

    /** Descripción de Campo */
    private MAccount	m_CurrencyBalancing_Acct	= null;

    /** Accounting Currency Precision */
    private int	m_stdPrecision	= -1;

    /** Costing Currency Precision */
    private int	m_costPrecision	= -1;

    /**
     *      Parent Constructor
     *
     * @param client
     * @param currency
     */
    public MAcctSchema(MClient client, KeyNamePair currency) {

        this(client.getCtx(), 0, client.get_TrxName());
        setClientOrg(client);
        setC_Currency_ID(currency.getKey());
        setName(client.getName() + " " + getGAAP() + "/" + get_ColumnCount() + " " + currency.getName());

    }		// MAcctSchema

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param C_AcctSchema_ID id
     * @param trxName
     */
    public MAcctSchema(Properties ctx, int C_AcctSchema_ID, String trxName) {

        super(ctx, C_AcctSchema_ID, trxName);

        if (C_AcctSchema_ID == 0) {

            // setC_Currency_ID (0);
            // setName (null);
            setAutoPeriodControl(true);
            setPeriod_OpenFuture(2);
            setPeriod_OpenHistory(2);
            setCostingMethod(COSTINGMETHOD_StandardCosting);
            setGAAP(GAAP_InternationalGAAP);
            setHasAlias(true);
            setHasCombination(false);
            setIsAccrual(true);		// Y
            setIsDiscountCorrectsTax(false);
            setIsTradeDiscountPosted(false);
            setSeparator("-");		// -
        }

    }					// MAcctSchema

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MAcctSchema(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MAcctSchema

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true
     */
    protected boolean beforeSave(boolean newRecord) {

    	// No se permite cambiar de método de amortización si se realizaron amortizaciones
    	if(is_ValueChanged("M_Amortization_Method_ID")){
			int cant = DB
					.getSQLValue(
							get_TrxName(),
							"SELECT count(*) FROM m_amortization WHERE ad_client_id = ?",
							getAD_Client_ID());
        	if(cant > 0){
        		log.saveError("AcctSchemaAmortizationsAlreadyCreated", "");
        		return false;
        	}
    	}
    	
        checkCosting();

        return true;

    }		// beforeSave

    /**
     *      Check Costing Setup
     */
    public void checkCosting() {
    	
        // Create Cost Type
        if (getM_CostType_ID() == 0) {

            MCostType	ct	= new MCostType(getCtx(), 0, get_TrxName());

            ct.setClientOrg(getAD_Client_ID(), 0);
            ct.setName(getName());
            ct.save();
            setM_CostType_ID(ct.getM_CostType_ID());
        }

        // Create Cost Elements
        MCostElement.getMaterialCostElement(this, getCostingMethod());
    }		// checkCosting

    /**
     *      String representation
     *  @return String rep
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("AcctSchema[");

        sb.append(getID()).append("-").append(getName()).append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *  Get AccountSchema of Client
     *      @param ctx context
     *  @param C_AcctSchema_ID schema id
     *  @return Accounting schema
     */
    public static MAcctSchema get(Properties ctx, int C_AcctSchema_ID) {

        // Check Cache
        Integer		key		= new Integer(C_AcctSchema_ID);
        MAcctSchema	retValue	= (MAcctSchema) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new MAcctSchema(ctx, C_AcctSchema_ID, null);
        s_cache.put(key, retValue);

        return retValue;
    }		// get

    /**
     *      Get AcctSchema Defaults
     *      @return defaults
     */
    public MAcctSchemaDefault getAcctSchemaDefault() {

        if (m_default == null) {
            m_default	= MAcctSchemaDefault.get(getCtx(), getC_AcctSchema_ID());
        }

        if (m_default == null) {
            throw new IllegalStateException("No Default Definition for C_AcctSchema_ID=" + getC_AcctSchema_ID());
        }

        return m_default;

    }		// getAcctSchemaDefault

    /**
     *  Get AcctSchema Element
     *  @param elementType segment type - AcctSchemaElement.ELEMENTTYPE_
     *  @return AcctSchemaElement
     */
    public MAcctSchemaElement getAcctSchemaElement(String elementType) {

        if (m_elements == null) {
            getAcctSchemaElements();
        }

        for (int i = 0; i < m_elements.length; i++) {

            MAcctSchemaElement	ase	= m_elements[i];

            if (ase.getElementType().equals(elementType)) {
                return ase;
            }
        }

        return null;

    }		// getAcctSchemaElement

    /**
     *  AcctSchema Elements
     *  @return ArrayList of AcctSchemaElement
     */
    public MAcctSchemaElement[] getAcctSchemaElements() {

        if (m_elements == null) {
            m_elements	= MAcctSchemaElement.getAcctSchemaElements(this);
        }

        return m_elements;

    }		// getAcctSchemaElements

    /**
     *      Get AcctSchema GL info
     *      @return GL info
     */
    public MAcctSchemaGL getAcctSchemaGL() {

        if (m_gl == null) {
            m_gl	= MAcctSchemaGL.get(getCtx(), getC_AcctSchema_ID());
        }

        if (m_gl == null) {
            throw new IllegalStateException("No GL Definition for C_AcctSchema_ID=" + getC_AcctSchema_ID());
        }

        return m_gl;

    }		// getAcctSchemaGL

    /**
     *  Get AccountSchema of Client
     *      @param ctx context
     *  @param AD_Client_ID client or 0 for all
     *  @return Array of AcctSchema of Client
     */
    public static MAcctSchema[] getClientAcctSchema(Properties ctx, int AD_Client_ID) {

        // Check Cache
        Integer	key	= new Integer(AD_Client_ID);

        if (s_schema.containsKey(key)) {
            return (MAcctSchema[]) s_schema.get(key);
        }

        // Create New
        ArrayList	list	= new ArrayList();
        String		sql	= "SELECT C_AcctSchema1_ID," + "Acct2_Active,C_AcctSchema2_ID," + "Acct3_Active,C_AcctSchema3_ID " + "FROM AD_ClientInfo ";

        if (AD_Client_ID != 0) {
            sql	+= "WHERE AD_Client_ID=?";
        }

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql);

            if (AD_Client_ID != 0) {
                pstmt.setInt(1, AD_Client_ID);
            }

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                int	id	= rs.getInt(1);

                if (id != 0) {
                    list.add(MAcctSchema.get(ctx, id));
                }

                //
                if (rs.getString(2).equals("Y")) {

                    id	= rs.getInt(3);

                    if (id != 0) {
                        list.add(MAcctSchema.get(ctx, id));
                    }
                }

                if (rs.getString(4).equals("Y")) {

                    id	= rs.getInt(5);

                    if (id != 0) {
                        list.add(new MAcctSchema(ctx, id, null));
                    }
                }
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        // Save
        MAcctSchema[]	retValue	= new MAcctSchema[list.size()];

        list.toArray(retValue);
        s_schema.put(key, retValue);

        return retValue;
    }		// getClientAcctSchema

    /**
     *      Get Costing Precision of accounting Currency
     *      @return precision
     */
    public int getCostingPrecision() {

        if (m_costPrecision < 0) {
            getStdPrecision();
        }

        return m_costPrecision;

    }		// getCostingPrecision

    /**
     *      Get Currency Balancing Account
     *  @return currency balancing account
     */
    public MAccount getCurrencyBalancing_Acct() {

        if (m_CurrencyBalancing_Acct != null) {
            return m_CurrencyBalancing_Acct;
        }

        if (m_gl == null) {
            getAcctSchemaGL();
        }

        int	C_ValidCombination_ID	= m_gl.getCurrencyBalancing_Acct();

        m_CurrencyBalancing_Acct	= MAccount.get(getCtx(), C_ValidCombination_ID);

        return m_CurrencyBalancing_Acct;

    }		// getCurrencyBalancing_Acct

    /**
     *      Get Due From Account for Segment
     *  @param segment ignored
     *  @return Account
     */
    public MAccount getDueFrom_Acct(String segment) {

        if (m_DueFrom_Acct != null) {
            return m_DueFrom_Acct;
        }

        if (m_gl == null) {
            getAcctSchemaGL();
        }

        int	C_ValidCombination_ID	= m_gl.getIntercompanyDueFrom_Acct();

        m_DueFrom_Acct	= MAccount.get(getCtx(), C_ValidCombination_ID);

        return m_DueFrom_Acct;

    }		// getDueFrom_Acct

    /**
     *      Get Due To Account for Segment
     *  @param segment ignored
     *  @return Account
     */
    public MAccount getDueTo_Acct(String segment) {

        if (m_DueTo_Acct != null) {
            return m_DueTo_Acct;
        }

        if (m_gl == null) {
            getAcctSchemaGL();
        }

        int	C_ValidCombination_ID	= m_gl.getIntercompanyDueTo_Acct();

        m_DueTo_Acct	= MAccount.get(getCtx(), C_ValidCombination_ID);

        return m_DueTo_Acct;

    }		// getDueTo_Acct

    /**
     *      Get Std Precision of accounting Currency
     *      @return precision
     */
    public int getStdPrecision() {

        if (m_stdPrecision < 0) {

            MCurrency	cur	= MCurrency.get(getCtx(), getC_Currency_ID());

            m_stdPrecision	= cur.getStdPrecision();
            m_costPrecision	= cur.getCostingPrecision();
        }

        return m_stdPrecision;

    }		// getStdPrecision

    /**
     *      Get Suspense Error Account
     *  @return suspense error account
     */
    public MAccount getSuspenseBalancing_Acct() {

        if (m_SuspenseError_Acct != null) {
            return m_SuspenseError_Acct;
        }

        if (m_gl == null) {
            getAcctSchemaGL();
        }

        int	C_ValidCombination_ID	= m_gl.getSuspenseBalancing_Acct();

        m_SuspenseError_Acct	= MAccount.get(getCtx(), C_ValidCombination_ID);

        return m_SuspenseError_Acct;

    }		// getSuspenseBalancing_Acct

    /**
     *  Has AcctSchema Element
     *  @param segmentType segment type - AcctSchemaElement.SEGMENT_
     *  @return true if schema has segment type
     */
    public boolean isAcctSchemaElement(String segmentType) {
        return getAcctSchemaElement(segmentType) != null;
    }		// isAcctSchemaElement

    /**
     *      Is Currency Balancing active
     *      @return suspense balancing
     */
    public boolean isCurrencyBalancing() {

        if (m_gl == null) {
            getAcctSchemaGL();
        }

        return m_gl.isUseCurrencyBalancing();

    }		// isSuspenseBalancing

    /**
     *      Is Suspense Balancing active
     *      @return suspense balancing
     */
    public boolean isSuspenseBalancing() {

        if (m_gl == null) {
            getAcctSchemaGL();
        }

        return m_gl.isUseSuspenseBalancing() && (m_gl.getSuspenseBalancing_Acct() != 0);

    }		// isSuspenseBalancing
    

    /**
	 * 	Skip creating postings for this Org.
	 *	@param AD_Org_ID
	 *	@return true if to skip
	 */
	public boolean isSkipOrg (int AD_Org_ID)
	{
		if (getAD_OrgOnly_ID() == 0)
			return false;
		//	Only Organization
		if (getAD_OrgOnly_ID() == AD_Org_ID)
			return false;
		if (m_onlyOrg == null)
			m_onlyOrg = MOrg.get(getCtx(), getAD_OrgOnly_ID());
		//	Not Summary Only - i.e. skip it
		if (!m_onlyOrg.isSummary())
			return true;
		final Integer[] onlyOrgs = getOnlyOrgs();
		if (onlyOrgs == null)
		{
			return false;
		}
		for (int i = 0; i < onlyOrgs.length; i++)
		{
			if (AD_Org_ID == onlyOrgs[i].intValue())
				return false;
		}
		return true;
	}	//	isSkipOrg
	/** Only Post Org					*/
	private MOrg					m_onlyOrg = null;
	/**
	 * Get Only Org Children
	 * @return array of AD_Org_ID
	 */
	public Integer[] getOnlyOrgs()
	{
		if (m_onlyOrgs == null)
		{
			m_onlyOrgs = MReportTree.getChildIDs(getCtx(), 
					0, "OO" /*MAcctSchemaElement.ELEMENTTYPE_Organization*/, 
					getAD_OrgOnly_ID());
		}
		return m_onlyOrgs;
	}	//	getOnlyOrgs
	/** Only Post Org Childs			*/
	private Integer[] 				m_onlyOrgs = null; 

}	// MAcctSchema



/*
 * @(#)MAcctSchema.java   02.jul 2007
 * 
 *  Fin del fichero MAcctSchema.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
