/*
 * @(#)MClient.java   07.jul 2007  Versión 2.1
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

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

/**
 *  Client Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MClient.java,v 1.22 2005/05/09 21:35:02 jjanke Exp $
 */
public class MClient extends X_AD_Client {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MClient.class);

    /** Cache */
    private static CCache	s_cache	= new CCache("AD_Client", 3);

    /** Client Info */
    private MClientInfo	m_info	= null;

    /** Language */
    private Language	m_language	= null;

    /** New Record */
    private boolean	m_createNew	= false;

    /** Client Info Setup Tree for Account */
    private int	m_AD_Tree_Account_ID;

    /**
     *      Simplified Constructor
     *      @param ctx context
     * @param trxName
     */
    public MClient(Properties ctx, String trxName) {
        this(ctx, Env.getAD_Client_ID(ctx), trxName);
    }		// MClient

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Client_ID id
     * @param trxName
     */
    public MClient(Properties ctx, int AD_Client_ID, String trxName) {
        this(ctx, AD_Client_ID, false, trxName);
    }		// MClient

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MClient(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MClient

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Client_ID id
     *      @param createNew create new
     * @param trxName
     */
    public MClient(Properties ctx, int AD_Client_ID, boolean createNew, String trxName) {

        super(ctx, AD_Client_ID, trxName);
        m_createNew	= createNew;

        if (AD_Client_ID == 0) {

            if (m_createNew) {

                // setValue (null);
                // setName (null);
                setAD_Org_ID(0);
                setIsMultiLingualDocument(false);
                setIsSmtpAuthorization(false);
                setIsUseBetaFunctions(true);
                setAD_Language(Language.getBaseAD_Language());
                setAutoArchive(AUTOARCHIVE_None);
                setMMPolicy(MMPOLICY_FiFo);	// F
            } else {
                load(get_TrxName());
            }
        }

    }						// MClient

    /**
     *      Save
     *      @return true if saved
     */
    public boolean save() {

        if ((getID() == 0) &&!m_createNew) {
            return saveUpdate();
        }

        return super.save();

    }		// save

    /**
     *      Create Trees and Setup Client Info
     *
     * @param language
     *
     * @return
     */
    public boolean setupClientInfo(String language) {

        // Create Trees
        String	sql	= null;

/*        if (Env.isBaseLanguage(language, "AD_Ref_List")) {	// Get TreeTypes & Name
            sql	= "SELECT Value, Name FROM AD_Ref_List WHERE AD_Reference_ID=120 AND IsActive='Y'";
        } else {
            sql	= "SELECT l.Value, t.Name FROM AD_Ref_List l, AD_Ref_List_Trl t " 
            	+ "WHERE l.AD_Reference_ID=120 AND l.AD_Ref_List_ID=t.AD_Ref_List_ID "
            	+ "AND l.IsActive='Y' AND t.AD_Language = '"+language+"'";
        }*/
        
        //Horacio Alvarez 2008-12-23 
        //Se cambia el IF por una query donde busca el valor traducido, si existe, sino el valor default.
        sql	= "SELECT l.Value, COALESCE (t.Name,l.name) as name " 
        	+ "FROM AD_Ref_List l "
        	+ "LEFT JOIN AD_Ref_List_Trl t "
        	+ "ON ( l.AD_Ref_List_ID = t.AD_Ref_List_ID AND t.AD_Language = '"+language+"' AND t.IsActive = 'Y') "
        	+ "WHERE l.AD_Reference_ID = 120 AND l.IsActive='Y' ";        

        // Tree IDs
        int	AD_Tree_Org_ID		= 0,
		AD_Tree_BPartner_ID	= 0,
		AD_Tree_Project_ID	= 0,
		AD_Tree_SalesRegion_ID	= 0,
		AD_Tree_Product_ID	= 0,
		AD_Tree_Campaign_ID	= 0,
		AD_Tree_Activity_ID	= 0;
        boolean	success			= false;

        try {

            PreparedStatement	stmt	= DB.prepareStatement(sql, get_TrxName());
            ResultSet		rs	= stmt.executeQuery();
            MTree_Base		tree	= null;

            while (rs.next()) {

                String	value	= rs.getString(1);
                String	name	= getName() + " " + rs.getString(2);

                //
                if (value.equals(MTree_Base.TREETYPE_Organization)) {

                    tree		= new MTree_Base(this, name, value);
                    success		= tree.save();
                    AD_Tree_Org_ID	= tree.getAD_Tree_ID();

                } else if (value.equals(MTree_Base.TREETYPE_BPartner)) {

                    tree		= new MTree_Base(this, name, value);
                    success		= tree.save();
                    AD_Tree_BPartner_ID	= tree.getAD_Tree_ID();

                } else if (value.equals(MTree_Base.TREETYPE_Project)) {

                    tree		= new MTree_Base(this, name, value);
                    success		= tree.save();
                    AD_Tree_Project_ID	= tree.getAD_Tree_ID();

                } else if (value.equals(MTree_Base.TREETYPE_SalesRegion)) {

                    tree			= new MTree_Base(this, name, value);
                    success			= tree.save();
                    AD_Tree_SalesRegion_ID	= tree.getAD_Tree_ID();

                } else if (value.equals(MTree_Base.TREETYPE_Product)) {

                    tree		= new MTree_Base(this, name, value);
                    success		= tree.save();
                    AD_Tree_Product_ID	= tree.getAD_Tree_ID();

                } else if (value.equals(MTree_Base.TREETYPE_ElementValue)) {

                    tree			= new MTree_Base(this, name, value);
                    success			= tree.save();
                    m_AD_Tree_Account_ID	= tree.getAD_Tree_ID();

                } else if (value.equals(MTree_Base.TREETYPE_Campaign)) {

                    tree		= new MTree_Base(this, name, value);
                    success		= tree.save();
                    AD_Tree_Campaign_ID	= tree.getAD_Tree_ID();

                } else if (value.equals(MTree_Base.TREETYPE_Activity)) {

                    tree		= new MTree_Base(this, name, value);
                    success		= tree.save();
                    AD_Tree_Activity_ID	= tree.getAD_Tree_ID();

                } else if (value.equals(MTree_Base.TREETYPE_Menu)) {	// No Menu
                    success	= true;
                } else							// PC (Product Category), BB (BOM)
                {

                    tree	= new MTree_Base(this, name, value);
                    success	= tree.save();
                }

                if (!success) {

                    log.log(Level.SEVERE, "setupClientInfo - Tree NOT created: " + name);

                    break;
                }
            }

            rs.close();
            stmt.close();

        } catch (SQLException e1) {

            log.log(Level.SEVERE, "setupClientInfo - Trees", e1);
            success	= false;
        }

        if (!success) {
            return false;
        }

        // Create ClientInfo
        MClientInfo	clientInfo	= new MClientInfo(this, AD_Tree_Org_ID, AD_Tree_BPartner_ID, AD_Tree_Project_ID, AD_Tree_SalesRegion_ID, AD_Tree_Product_ID, AD_Tree_Campaign_ID, AD_Tree_Activity_ID, get_TrxName());

        success	= clientInfo.save();

        return success;
    }		// createTrees

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MClient[").append(getID()).append("-").append(getValue()).append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get optionally cached client
     *      @param ctx context
     *      @return client
     */
    public static MClient get(Properties ctx) {
        return get(ctx, Env.getAD_Client_ID(ctx));
    }		// get

    /**
     *      Get client
     *      @param ctx context
     *      @param AD_Client_ID id
     *      @return client
     */
    public static MClient get(Properties ctx, int AD_Client_ID) {

        Integer	key	= new Integer(AD_Client_ID);
        MClient	client	= (MClient) s_cache.get(key);

        if (client != null) {
            return client;
        }

        client	= new MClient(ctx, AD_Client_ID, null);
        s_cache.put(key, client);

        return client;

    }		// get

    /**
     *      Get AD_Language
     *      @return Language
     */
    public String getAD_Language() {

        String	s	= super.getAD_Language();

        if (s == null) {
            return Language.getBaseAD_Language();
        }

        return s;

    }		// getAD_Language

    /**
     *      Get Primary Accounting Schema
     *      @return Acct Schema or null
     */
    public MAcctSchema getAcctSchema() {

        if (m_info == null) {
            m_info	= MClientInfo.get(getCtx(), getAD_Client_ID());
        }

        if (m_info != null) {

            int	C_AcctSchema_ID	= m_info.getC_AcctSchema1_ID();

            if (C_AcctSchema_ID != 0) {
                return MAcctSchema.get(getCtx(), C_AcctSchema_ID);
            }
        }

        return null;

    }		// getMClientInfo

    /**
     *      Get all clients
     *      @param ctx context
     *      @return clients
     */
    public static MClient[] getAll(Properties ctx) {

        ArrayList		list	= new ArrayList();
        String			sql	= "SELECT * FROM AD_Client";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MClient	client	= new MClient(ctx, rs, null);

                s_cache.put(new Integer(client.getAD_Client_ID()), client);
                list.add(client);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getAll", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        MClient[]	retValue	= new MClient[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// get

    /**
     *      Get Default Accounting Currency
     *      @return currency or 0
     */
    public int getC_Currency_ID() {

        if (m_info == null) {
            getInfo();
        }

        if (m_info != null) {
            return m_info.getC_Currency_ID();
        }

        return 0;

    }		// getC_Currency_ID

    /**
     *      Get Client Info
     *      @return Client Info
     */
    public MClientInfo getInfo() {

        if (m_info == null) {
            m_info	= MClientInfo.get(getCtx(), getAD_Client_ID());
        }

        return m_info;

    }		// getMClientInfo

    /**
     *      Get Language
     *      @return client language
     */
    public Language getLanguage() {

        if (m_language == null) {

            m_language	= Language.getLanguage(getAD_Language());
            Env.verifyLanguage(getCtx(), m_language);
        }

        return m_language;

    }		// getLanguage

    /**
     *      Get Locale
     *      @return locale
     */
    public Locale getLocale() {

        Language	lang	= getLanguage();

        if (lang != null) {
            return lang.getLocale();
        }

        return Locale.getDefault();

    }		// getLocale

    /**
     *      Get SMTP Host
     *      @return SMTP or loaclhost
     */
    public String getSMTPHost() {

        String	s	= super.getSMTPHost();

        if (s == null) {
            s	= "localhost";
        }

        return s;

    }		// getSMTPHost

    /**
     *      Get AD_Tree_Account_ID created in setup client info
     *      @return Account Tree ID
     */
    public int getSetup_AD_Tree_Account_ID() {
        return m_AD_Tree_Account_ID;
    }		// getSetup_AD_Tree_Account_ID

    /**
     *      Is Auto Archive on
     *      @return true if auto archive
     */
    public boolean isAutoArchive() {

        String	aa	= getAutoArchive();

        return (aa != null) &&!aa.equals(AUTOARCHIVE_None);

    }		// isAutoArchive

    /**
     *      Update Trl Tables automatically?
     *      @param TableName table name
     *      @return true if automatically translated
     */
    public boolean isAutoUpdateTrl(String TableName) {

        if (super.isMultiLingualDocument()) {
            return false;
        }

        if (TableName == null) {
            return false;
        }

        // Not Multi-Lingual Documents - only Doc Related
        if (TableName.startsWith("AD")) {
            return false;
        }

        return true;

    }		// isMultiLingualDocument

    //~--- set methods --------------------------------------------------------

    /**
     *      Set AD_Language
     *      @param AD_Language new language
     */
    public void setAD_Language(String AD_Language) {

        m_language	= null;
        super.setAD_Language(AD_Language);

    }		// setAD_Language
    
    /**
     *      Set AD_Client
     *      @param 
     */    
	public Integer getCategoriaIva() {
		// devuelve la categoria de iva de la compañia
	Integer vInfo_ID = 0;	
	StringBuffer sql = new StringBuffer( "SELECT C_Categoria_IVA_ID FROM AD_ClientInfo " +
				"         WHERE  AD_Client_Id = ? " );
   /* 
    * realizo una busqueda sql, por que la instanciación de AD_ClientInfo es muy oscura, no es normal
    * como las otras subclases de PO
    */
	
   try {
        PreparedStatement pstmt = DB.prepareStatement( sql.toString());
        pstmt.setInt( 1,Env.getAD_Client_ID(getCtx()));
        ResultSet rs = pstmt.executeQuery();
        
        if(rs.next()) {
           vInfo_ID = rs.getInt("C_Categoria_IVA_ID");
        }
        rs.close();
        pstmt.close();
    } catch( SQLException e ) {
        log.log( Level.SEVERE,sql.toString(),e );
    }
    	return vInfo_ID;    
	}   // getCategoriaIva
	

	/**
	 * 	Define is a field is displayed based on ASP rules
	 * 	@param ad_field_id
	 *	@return boolean indicating if it's displayed or not
	 */
	public boolean isDisplayField(int aDFieldID) {
//		if (! isUseASP())
			return true;
//
//		if (m_fieldAccess == null)
//		{
//			m_fieldAccess = new ArrayList<Integer>(11000);
//			String sqlvalidate =
//				"SELECT AD_Field_ID "
//				 + "  FROM AD_Field "
//				 + " WHERE (   AD_Field_ID IN ( "
//				 // ASP subscribed fields for client
//				 + "              SELECT f.AD_Field_ID "
//				 + "                FROM ASP_Field f, ASP_Tab t, ASP_Window w, ASP_Level l, ASP_ClientLevel cl "
//				 + "               WHERE w.ASP_Level_ID = l.ASP_Level_ID "
//				 + "                 AND cl.AD_Client_ID = " + getAD_Client_ID()
//				 + "                 AND cl.ASP_Level_ID = l.ASP_Level_ID "
//				 + "                 AND f.ASP_Tab_ID = t.ASP_Tab_ID "
//				 + "                 AND t.ASP_Window_ID = w.ASP_Window_ID "
//				 + "                 AND f.IsActive = 'Y' "
//				 + "                 AND t.IsActive = 'Y' "
//				 + "                 AND w.IsActive = 'Y' "
//				 + "                 AND l.IsActive = 'Y' "
//				 + "                 AND cl.IsActive = 'Y' "
//				 + "                 AND f.ASP_Status = 'S') "
//				 + "        OR AD_Tab_ID IN ( "
//				 // ASP subscribed fields for client
//				 + "              SELECT t.AD_Tab_ID "
//				 + "                FROM ASP_Tab t, ASP_Window w, ASP_Level l, ASP_ClientLevel cl "
//				 + "               WHERE w.ASP_Level_ID = l.ASP_Level_ID "
//				 + "                 AND cl.AD_Client_ID = " + getAD_Client_ID()
//				 + "                 AND cl.ASP_Level_ID = l.ASP_Level_ID "
//				 + "                 AND t.ASP_Window_ID = w.ASP_Window_ID "
//				 + "                 AND t.IsActive = 'Y' "
//				 + "                 AND w.IsActive = 'Y' "
//				 + "                 AND l.IsActive = 'Y' "
//				 + "                 AND cl.IsActive = 'Y' "
//				 + "                 AND t.AllFields = 'Y' "
//				 + "                 AND t.ASP_Status = 'S') "
//				 + "        OR AD_Field_ID IN ( "
//				 // ASP show exceptions for client
//				 + "              SELECT AD_Field_ID "
//				 + "                FROM ASP_ClientException ce "
//				 + "               WHERE ce.AD_Client_ID = " + getAD_Client_ID()
//				 + "                 AND ce.IsActive = 'Y' "
//				 + "                 AND ce.AD_Field_ID IS NOT NULL "
//				 + "                 AND ce.ASP_Status = 'S') "
//				 + "       ) "
//				 + "   AND AD_Field_ID NOT IN ( "
//				 // minus ASP hide exceptions for client
//				 + "          SELECT AD_Field_ID "
//				 + "            FROM ASP_ClientException ce "
//				 + "           WHERE ce.AD_Client_ID = " + getAD_Client_ID()
//				 + "             AND ce.IsActive = 'Y' "
//				 + "             AND ce.AD_Field_ID IS NOT NULL "
//				 + "             AND ce.ASP_Status = 'H')" 
//				 + " ORDER BY AD_Field_ID";
//			PreparedStatement pstmt = null;
//			ResultSet rs = null;
//			try
//			{
//				pstmt = DB.prepareStatement(sqlvalidate, get_TrxName());
//				rs = pstmt.executeQuery();
//				while (rs.next())
//					m_fieldAccess.add(rs.getInt(1));
//			}
//			catch (Exception e)
//			{
//				log.log(Level.SEVERE, sqlvalidate, e);
//			}
//			finally
//			{
//				DB.close(rs, pstmt);
//			}
//		}
//		return (Collections.binarySearch(m_fieldAccess, aDFieldID) > 0);
	}
	/**	Field Access			*/
	private ArrayList<Integer>	m_fieldAccess = null;

}	// MClient



/*
 * @(#)MClient.java   02.jul 2007
 * 
 *  Fin del fichero MClient.java
 *  
 *  Versión 2.1  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
