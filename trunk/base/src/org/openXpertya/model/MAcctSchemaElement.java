/*
 * @(#)MAcctSchemaElement.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.model.M_Column;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *  Account Schema Element Object
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *  @version    $Id: MAcctSchemaElement.java,v 1.9 2005/03/11 20:28:37 jjanke Exp $
 */
public final class MAcctSchemaElement extends X_C_AcctSchema_Element {

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MAcctSchemaElement.class);

    /** Cache */
    private static CCache	s_cache	= new CCache("C_AcctSchema_Element", 10);

    /**
     *      Parent Constructor
     *      @param as accounting schema
     */
    public MAcctSchemaElement(MAcctSchema as) {

        this(as.getCtx(), 0, as.get_TrxName());
        setClientOrg(as);
        setC_AcctSchema_ID(as.getC_AcctSchema_ID());

        // setC_Element_ID (0);
        // setElementType (null);
        // setName (null);
        // setSeqNo (0);

    }		// MAcctSchemaElement

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param C_AcctSchema_Element_ID id
     * @param trxName
     */
    public MAcctSchemaElement(Properties ctx, int C_AcctSchema_Element_ID, String trxName) {

        super(ctx, C_AcctSchema_Element_ID, trxName);

        if (C_AcctSchema_Element_ID == 0) {

            // setC_AcctSchema_Element_ID (0);
            // setC_AcctSchema_ID (0);
            // setC_Element_ID (0);
            // setElementType (null);
            setIsBalanced(false);
            setIsMandatory(false);

            // setName (null);
            // setOrg_ID (0);
            // setSeqNo (0);
        }

    }		// MAcctSchemaElement

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MAcctSchemaElement(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MAcctSchemaElement

    /**
     *      After Delete
     *      @param success success
     *      @return success
     */
    protected boolean afterDelete(boolean success) {

        // Update Account Info
        MAccount.updateValueDescription(getCtx(), "AD_Client_ID=" + getAD_Client_ID(), get_TrxName());

        //
        s_cache.clear();

        return success;
    }		// afterDelete

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        // Default Value
        if (isMandatory() && is_ValueChanged("IsMandatory")) {

            if (ELEMENTTYPE_Activity.equals(getElementType())) {
                updateData("C_Activity_ID", getC_Activity_ID());
            } else if (ELEMENTTYPE_BPartner.equals(getElementType())) {
                updateData("C_BPartner_ID", getC_BPartner_ID());
            } else if (ELEMENTTYPE_Product.equals(getElementType())) {
                updateData("M_Product_ID", getM_Product_ID());
            } else if (ELEMENTTYPE_Project.equals(getElementType())) {
                updateData("C_Project_ID", getC_Project_ID());
            }
        }

        // Resequence
        if (newRecord || is_ValueChanged("SeqNo")) {
            MAccount.updateValueDescription(getCtx(), "AD_Client_ID=" + getAD_Client_ID(), get_TrxName());
        }

        // Clear Cache
        s_cache.clear();

        return success;
    }		// afterSave

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true if it can be saved
     */
    protected boolean beforeSave(boolean newRecord) {

        if (isMandatory()) {

            String	errorField	= null;

            if (ELEMENTTYPE_Account.equals(getElementType()) && (getC_ElementValue_ID() == 0)) {
                errorField	= "C_ElementValue_ID";
            } else if (ELEMENTTYPE_Activity.equals(getElementType()) && (getC_Activity_ID() == 0)) {
                errorField	= "C_Activity_ID";
            } else if (ELEMENTTYPE_BPartner.equals(getElementType()) && (getC_BPartner_ID() == 0)) {
                errorField	= "C_BPartner_ID";
            } else if (ELEMENTTYPE_Campaign.equals(getElementType()) && (getC_Campaign_ID() == 0)) {
                errorField	= "C_Campaign_ID";
            } else if (ELEMENTTYPE_LocationFrom.equals(getElementType()) && (getC_Location_ID() == 0)) {
                errorField	= "C_Location_ID";
            } else if (ELEMENTTYPE_LocationTo.equals(getElementType()) && (getC_Location_ID() == 0)) {
                errorField	= "C_Location_ID";
            } else if (ELEMENTTYPE_Org.equals(getElementType()) && (getOrg_ID() == 0)) {
                errorField	= "Org_ID";
            } else if (ELEMENTTYPE_OrgTrx.equals(getElementType()) && (getOrg_ID() == 0)) {
                errorField	= "Org_ID";
            } else if (ELEMENTTYPE_Product.equals(getElementType()) && (getM_Product_ID() == 0)) {
                errorField	= "M_Product_ID";
            } else if (ELEMENTTYPE_Project.equals(getElementType()) && (getC_Project_ID() == 0)) {
                errorField	= "C_Project_ID";
            } else if (ELEMENTTYPE_SalesRegion.equals(getElementType()) && (getC_SalesRegion_ID() == 0)) {
                errorField	= "C_SalesRegion_ID";
            } else if (ELEMENTTYPE_User1.equals(getElementType()) && (getC_ElementValue_ID() == 0)) {
                errorField	= "C_ElementValue_ID";
            } else if (ELEMENTTYPE_User2.equals(getElementType()) && (getC_ElementValue_ID() == 0)) {
                errorField	= "C_ElementValue_ID";
            }

            if (errorField != null) {

                log.saveError("Error", Msg.parseTranslation(getCtx(), "@IsMandatory@: @" + errorField + "@"));

                return false;
            }
        }

        return true;

    }		// beforeSave

	/**
	 * Get Display ColumnName
	 * @return column name
	 */
	public String getDisplayColumnName()
	{
		String et = getElementType();
		if (ELEMENTTYPE_User1.equals(et) || ELEMENTTYPE_User2.equals(et))
		{
			if (m_ColumnName == null)
				m_ColumnName = M_Column.getColumnName(getCtx(), getAD_Column_ID());
			return m_ColumnName;
		}
		return getColumnName(et);
	}	//	getDisplayColumnName
	
    
    /**
     *  String representation
     *  @return info
     */
    public String toString() {
        return "AcctSchemaElement[" + getID() + "-" + getName() + "(" + getElementType() + ")=" + getDefaultValue() + ",Pos=" + getSeqNo() + "]";
    }		// toString

    /**
     *      Update ValidCombination and Fact with mandatory value
     *      @param element element
     *      @param id new default
     */
    private void updateData(String element, int id) {

        MAccount.updateValueDescription(getCtx(), element + "=" + id, get_TrxName());

        //
        String	sql	= "UPDATE C_ValidCombination SET " + element + "=" + id + " WHERE " + element + " IS NULL AND AD_Client_ID=" + getAD_Client_ID();
        int	noC	= DB.executeUpdate(sql, get_TrxName());

        //
        sql	= "UPDATE Fact_Acct SET " + element + "=" + id + " WHERE " + element + " IS NULL AND C_AcctSchema_ID=" + getC_AcctSchema_ID();

        int	noF	= DB.executeUpdate(sql, get_TrxName());

        //
        log.fine("ValidCombination=" + noC + ", Fact=" + noF);

    }		// updateData

    //~--- get methods --------------------------------------------------------

    /**
     *  Factory: Return ArrayList of Account Schema Elements
     *      @param as Accounting Schema
     *  @return ArrayList with Elements
     */
    public static MAcctSchemaElement[] getAcctSchemaElements(MAcctSchema as) {

        Integer			key		= new Integer(as.getC_AcctSchema_ID());
        MAcctSchemaElement[]	retValue	= (MAcctSchemaElement[]) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        s_log.fine("C_AcctSchema_ID=" + as.getC_AcctSchema_ID());

        ArrayList	list	= new ArrayList();

        //
        String	sql	= "SELECT * FROM C_AcctSchema_Element " + "WHERE C_AcctSchema_ID=? AND IsActive='Y' ORDER BY SeqNo";

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql, as.get_TrxName());

            pstmt.setInt(1, as.getC_AcctSchema_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MAcctSchemaElement	ase	= new MAcctSchemaElement(as.getCtx(), rs, as.get_TrxName());

                s_log.fine(" - " + ase);

                if (ase.isMandatory() && (ase.getDefaultValue() == 0)) {
                    s_log.log(Level.SEVERE, "No default value for " + ase.getName());
                }

                list.add(ase);

                //
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        retValue	= new MAcctSchemaElement[list.size()];
        list.toArray(retValue);
        s_cache.put(key, retValue);

        return retValue;

    }		// getAcctSchemaElements

    /**
     *  Get ColumnName
     *  @return column name
     */
    public String getColumnName() {
        return getColumnName(getElementType());
    }		// getColumnName

    /**
     *  Get Column Name of ELEMENTTYPE
     *      @param elementType ELEMENTTYPE
     *  @return column name
     */
    public static String getColumnName(String elementType) {

        if (elementType.equals(ELEMENTTYPE_Org)) {
            return "AD_Org_ID";
        } else if (elementType.equals(ELEMENTTYPE_Account)) {
            return "Account_ID";
        } else if (elementType.equals(ELEMENTTYPE_BPartner)) {
            return "C_BPartner_ID";
        } else if (elementType.equals(ELEMENTTYPE_Product)) {
            return "M_Product_ID";
        } else if (elementType.equals(ELEMENTTYPE_Activity)) {
            return "C_Activity_ID";
        } else if (elementType.equals(ELEMENTTYPE_LocationFrom)) {
            return "C_LocFrom_ID";
        } else if (elementType.equals(ELEMENTTYPE_LocationTo)) {
            return "C_LocTo_ID";
        } else if (elementType.equals(ELEMENTTYPE_Campaign)) {
            return "C_Campaign_ID";
        } else if (elementType.equals(ELEMENTTYPE_OrgTrx)) {
            return "AD_OrgTrx_ID";
        } else if (elementType.equals(ELEMENTTYPE_Project)) {
            return "C_Project_ID";
        } else if (elementType.equals(ELEMENTTYPE_SalesRegion)) {
            return "C_SalesRegion_ID";
        } else if (elementType.equals(ELEMENTTYPE_User1)) {
            return "User1_ID";
        } else if (elementType.equals(ELEMENTTYPE_User2)) {
            return "User2_ID";
        }

        //
        return "";

    }		// getColumnName

    /**
     *      Get Default element value
     *      @return default
     */
    public int getDefaultValue() {

        String	elementType	= getElementType();
        int	defaultValue	= 0;

        if (elementType.equals(ELEMENTTYPE_Org)) {
            defaultValue	= getOrg_ID();
        } else if (elementType.equals(ELEMENTTYPE_Account)) {
            defaultValue	= getC_ElementValue_ID();
        } else if (elementType.equals(ELEMENTTYPE_BPartner)) {
            defaultValue	= getC_BPartner_ID();
        } else if (elementType.equals(ELEMENTTYPE_Product)) {
            defaultValue	= getM_Product_ID();
        } else if (elementType.equals(ELEMENTTYPE_Activity)) {
            defaultValue	= getC_Activity_ID();
        } else if (elementType.equals(ELEMENTTYPE_LocationFrom)) {
            defaultValue	= getC_Location_ID();
        } else if (elementType.equals(ELEMENTTYPE_LocationTo)) {
            defaultValue	= getC_Location_ID();
        } else if (elementType.equals(ELEMENTTYPE_Campaign)) {
            defaultValue	= getC_Campaign_ID();
        } else if (elementType.equals(ELEMENTTYPE_OrgTrx)) {
            defaultValue	= getOrg_ID();
        } else if (elementType.equals(ELEMENTTYPE_Project)) {
            defaultValue	= getC_Project_ID();
        } else if (elementType.equals(ELEMENTTYPE_SalesRegion)) {
            defaultValue	= getC_SalesRegion_ID();
        } else if (elementType.equals(ELEMENTTYPE_User1)) {
            defaultValue	= getC_ElementValue_ID();
        } else if (elementType.equals(ELEMENTTYPE_User2)) {
            defaultValue	= getC_ElementValue_ID();
        }

        return defaultValue;

    }		// getDefault

    /**
     *  Get Value Query for ELEMENTTYPE Type
     *      @param elementType ELEMENTTYPE type
     *  @return query "SELECT Value,Name FROM Table WHERE ID="
     */
    public static String getValueQuery(String elementType) {

        if (elementType.equals(ELEMENTTYPE_Org)) {
            return "SELECT Value,Name FROM AD_Org WHERE AD_Org_ID=";
        } else if (elementType.equals(ELEMENTTYPE_Account)) {
            return "SELECT Value,Name FROM C_ElementValue WHERE C_ElementValue_ID=";
        } else if (elementType.equals(ELEMENTTYPE_BPartner)) {
            return "SELECT Value,Name FROM C_BPartner WHERE C_BPartner_ID=";
        } else if (elementType.equals(ELEMENTTYPE_Product)) {
            return "SELECT Value,Name FROM M_Product WHERE M_Product_ID=";
        } else if (elementType.equals(ELEMENTTYPE_Activity)) {
            return "SELECT Value,Name FROM C_Activity WHERE C_Activity_ID=";
        } else if (elementType.equals(ELEMENTTYPE_LocationFrom)) {
            return "SELECT City,Address1 FROM C_Location WHERE C_Location_ID=";
        } else if (elementType.equals(ELEMENTTYPE_LocationTo)) {
            return "SELECT City,Address1 FROM C_Location WHERE C_Location_ID=";
        } else if (elementType.equals(ELEMENTTYPE_Campaign)) {
            return "SELECT Value,Name FROM C_Campaign WHERE C_Campaign_ID=";
        } else if (elementType.equals(ELEMENTTYPE_OrgTrx)) {
            return "SELECT Value,Name FROM AD_Org WHERE AD_Org_ID=";
        } else if (elementType.equals(ELEMENTTYPE_Project)) {
            return "SELECT Value,Name FROM C_Project WHERE C_Project_ID=";
        } else if (elementType.equals(ELEMENTTYPE_SalesRegion)) {
            return "SELECT Value,Name FROM C_SalesRegion WHERE C_SalesRegion_ID";
        } else if (elementType.equals(ELEMENTTYPE_User1)) {
            return "SELECT Value,Name FROM C_ElementValue WHERE C_ElementValue_ID=";
        } else if (elementType.equals(ELEMENTTYPE_User2)) {
            return "SELECT Value,Name FROM C_ElementValue WHERE C_ElementValue_ID=";
        }

        //
        return "";

    }		// getColumnName

    /**
     *  Is Element Type
     *
     * @param elementType
     *      @return ELEMENTTYPE type
     */
    public boolean isElementType(String elementType) {

        if (elementType == null) {
            return false;
        }

        return elementType.equals(getElementType());

    }		// isElementType

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param SeqNo
     * @param Name
     * @param C_Element_ID
     * @param C_ElementValue_ID
     */
    public void setTypeAccount(int SeqNo, String Name, int C_Element_ID, int C_ElementValue_ID) {

        setElementType(ELEMENTTYPE_Account);
        setSeqNo(SeqNo);
        setName(Name);
        setC_Element_ID(C_Element_ID);
        setC_ElementValue_ID(C_ElementValue_ID);
    }

    /**
     * Descripción de Método
     *
     *
     * @param SeqNo
     * @param Name
     * @param C_BPartner_ID
     */
    public void setTypeBPartner(int SeqNo, String Name, int C_BPartner_ID) {

        setElementType(ELEMENTTYPE_BPartner);
        setSeqNo(SeqNo);
        setName(Name);
        setC_BPartner_ID(C_BPartner_ID);
    }

	/** User Element Column Name		*/
	private String		m_ColumnName = null;
    
    /**
     *      Set Organization Type
     *      @param SeqNo sequence
     *      @param Name name
     *      @param Org_ID id
     */
    public void setTypeOrg(int SeqNo, String Name, int Org_ID) {

        setElementType(ELEMENTTYPE_Org);
        setSeqNo(SeqNo);
        setName(Name);
        setOrg_ID(Org_ID);

    }		// setTypeOrg

    /**
     * Descripción de Método
     *
     *
     * @param SeqNo
     * @param Name
     * @param M_Product_ID
     */
    public void setTypeProduct(int SeqNo, String Name, int M_Product_ID) {

        setElementType(ELEMENTTYPE_Product);
        setSeqNo(SeqNo);
        setName(Name);
        setM_Product_ID(M_Product_ID);
    }

    /**
     * Descripción de Método
     *
     *
     * @param SeqNo
     * @param Name
     * @param C_Project_ID
     */
    public void setTypeProject(int SeqNo, String Name, int C_Project_ID) {

        setElementType(ELEMENTTYPE_Project);
        setSeqNo(SeqNo);
        setName(Name);
        setC_Project_ID(C_Project_ID);
    }
}	// AcctSchemaElement



/*
 * @(#)MAcctSchemaElement.java   02.jul 2007
 * 
 *  Fin del fichero MAcctSchemaElement.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
