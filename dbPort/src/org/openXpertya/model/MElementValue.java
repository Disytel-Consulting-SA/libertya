/*
 * @(#)MElementValue.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.ResultSet;

import java.util.Properties;

/**
 *      Natural Account
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MElementValue.java,v 1.9 2005/04/18 04:59:54 jjanke Exp $
 */
public class MElementValue extends X_C_ElementValue {

    /**
     *      Import Constructor
     *      @param imp import
     */
    public MElementValue(X_I_ElementValue imp) {

        this(imp.getCtx(), 0, imp.get_TrxName());
        setClientOrg(imp);
        set(imp);

    }		// MElementValue

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param C_ElementValue_ID ID or 0 for new
     * @param trxName
     */
    public MElementValue(Properties ctx, int C_ElementValue_ID, String trxName) {

        super(ctx, C_ElementValue_ID, trxName);

        if (C_ElementValue_ID == 0) {

            // setC_Element_ID (0);    //      Parent
            // setName (null);
            // setValue (null);
            setIsSummary(false);
            setAccountSign(ACCOUNTSIGN_Natural);
            setAccountType(ACCOUNTTYPE_Expense);
            setIsDocControlled(false);
            setIsForeignCurrency(false);
            setIsBankAccount(false);

            //
            setPostActual(true);
            setPostBudget(true);
            setPostEncumbrance(true);
            setPostStatistical(true);
        }

    }		// MElementValue

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MElementValue(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MElementValue

    /**
     *      Full Constructor
     *      @param ctx context
     *      @param Value value
     *      @param Name name
     *      @param Description description
     *      @param AccountType account type
     *      @param AccountSign account sign
     *      @param IsDocControlled doc controlled
     *      @param IsSummary summary
     * @param trxName
     */
    public MElementValue(Properties ctx, String Value, String Name, String Description, String AccountType, String AccountSign, boolean IsDocControlled, boolean IsSummary, String trxName) {

        this(ctx, 0, trxName);
        setValue(Value);
        setName(Name);
        setDescription(Description);
        setAccountType(AccountType);
        setAccountSign(AccountSign);
        setIsDocControlled(IsDocControlled);
        setIsSummary(IsSummary);

    }		// MElementValue

    /**
     *      After Delete
     *      @param success
     *      @return deleted
     */
    protected boolean afterDelete(boolean success) {

        if (success) {
            delete_Tree(MTree_Base.TREETYPE_ElementValue);
        }

        return true;

    }		// afterDelete

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        if (newRecord || !isInTree()) {
            insert_Tree(MTree_Base.TREETYPE_ElementValue, getC_Element_ID());
        }

        // Value/Name change
        if (!newRecord && (is_ValueChanged("Value") || is_ValueChanged("Name"))) {

            MAccount.updateValueDescription(getCtx(), "Account_ID=" + getC_ElementValue_ID(), get_TrxName());

            if ("Y".equals(Env.getContext(getCtx(), "$Element_U1"))) {
                MAccount.updateValueDescription(getCtx(), "User1_ID=" + getC_ElementValue_ID(), get_TrxName());
            }

            if ("Y".equals(Env.getContext(getCtx(), "$Element_U2"))) {
                MAccount.updateValueDescription(getCtx(), "User2_ID=" + getC_ElementValue_ID(), get_TrxName());
            }
        }

        return success;

    }		// afterSave

    /**
     *      Before Save
     *      @param newRecord
     *      @return true if ir can be saved
     */
    protected boolean beforeSave(boolean newRecord) {

        if (!newRecord && isSummary() && is_ValueChanged("IsSummary")) {

            String	sql	= "SELECT COUNT(*) FROM Fact_Acct WHERE Account_ID=?";
            int		no	= DB.getSQLValue(get_TrxName(), sql, getC_ElementValue_ID());

            if (no != 0) {

                log.saveError("Error", "Already posted to");

                return false;
            }
        }

        return true;

    }		// beforeSave

    /**
     *      User String Representation
     *      @return info value - name
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer();

        sb.append(getValue()).append(" - ").append(getName());

        return sb.toString();

    }		// toString

    /**
     *      Extended String Representation
     *      @return info
     */
    public String toStringX() {

        StringBuffer	sb	= new StringBuffer("MElementValue[");

        sb.append(getID()).append(",").append(getValue()).append(" - ").append(getName()).append("]");

        return sb.toString();

    }		// toStringX

    //~--- get methods --------------------------------------------------------

    /**
     * Is this an Activa Account
     * @return boolean
     */
    public boolean isActiva() {
        return ACCOUNTTYPE_Asset.equals(getAccountType());
    }		// isActive

    /**
     * Is this a Balance Sheet Account
     * @return boolean
     */
    public boolean isBalanceSheet() {

        String	accountType	= getAccountType();

        return (ACCOUNTTYPE_Asset.equals(accountType) || ACCOUNTTYPE_Liability.equals(accountType) || ACCOUNTTYPE_OwnerSEquity.equals(accountType));

    }		// isBalanceSheet

    /**
     * Is this a Passiva Account
     * @return boolean
     */
    public boolean isPassiva() {

        String	accountType	= getAccountType();

        return (ACCOUNTTYPE_Liability.equals(accountType) || ACCOUNTTYPE_OwnerSEquity.equals(accountType));

    }		// isPassiva

    //~--- set methods --------------------------------------------------------

    /**
     *      Set/Update Settings from import
     *      @param imp import
     */
    public void set(X_I_ElementValue imp) {

        setValue(imp.getValue());
        setName(imp.getName());
        setDescription(imp.getDescription());
        setAccountType(imp.getAccountType());
        setAccountSign(imp.getAccountSign());
        setIsSummary(imp.isSummary());
        setIsDocControlled(imp.isDocControlled());
        setC_Element_ID(imp.getC_Element_ID());

        //
        setPostActual(imp.isPostActual());
        setPostBudget(imp.isPostBudget());
        setPostEncumbrance(imp.isPostEncumbrance());
        setPostStatistical(imp.isPostStatistical());

        //
        // setC_BankAccount_ID(imp.getC_BankAccount_ID());
        // setIsForeignCurrency(imp.isForeignCurrency());
        // setC_Currency_ID(imp.getC_Currency_ID());
        // setIsBankAccount(imp.isIsBankAccount());
        // setValidFrom(null);
        // setValidTo(null);

    }		// set
    
    private boolean isInTree() {
    	String sql = 
        	"SELECT COUNT(tn.*) " +
        	"FROM AD_TreeNode tn " +
        	"INNER JOIN C_Element e ON (tn.AD_Tree_ID = e.AD_Tree_ID) " +
        	"WHERE e.C_Element_ID = ? " +
        	  "AND tn.Node_ID = ?";

		long count = (Long) DB.getSQLObject(get_TrxName(), sql, new Object[] {
				getC_Element_ID(), getC_ElementValue_ID() });
    	return count > 0;
    }
}	// MElementValue



/*
 * @(#)MElementValue.java   02.jul 2007
 * 
 *  Fin del fichero MElementValue.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
