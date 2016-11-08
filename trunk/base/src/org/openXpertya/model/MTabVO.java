/*
 * @(#)MTabVO.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.model.MFieldVO;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MTabVO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Evaluatee;
import org.openXpertya.util.Evaluator;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *  Model Tab Value Object
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version  $Id: MTabVO.java,v 1.25 2005/05/21 04:47:54 jjanke Exp $
 */
public class MTabVO implements Evaluatee, Serializable {

    /** Descripción de Campo */
    static final long	serialVersionUID	= 9160212869277319305L;

    /** Descripción de Campo */
    public String	Name	= "";

    /** Descripción de Campo */
    public boolean	IsView	= false;

    /** Descripción de Campo */
    public boolean	IsSingleRow	= false;

    /** Descripción de Campo */
    public boolean	IsSecurityEnabled	= false;

    /** Descripción de Campo */
    public boolean	IsReadOnly	= false;

    /** Descripción de Campo */
    public boolean	IsInsertRecord	= true;

    /** Descripción de Campo */
    public boolean	IsHighVolume	= false;

    /** Descripción de Campo */
    public boolean	IsDeleteable	= false;

    /** Descripción de Campo */
    public String	Help	= "";

    /** Descripción de Campo */
    public boolean	HasTree	= false;

    /** Descripción de Campo */
    public String	Description	= "";

    /** Descripción de Campo */
    public int	AD_Process_ID	= 0;

    /** Primary Parent Column */
    public int	AD_Column_ID	= 0;
    
	/** Parent Tab Link Column */
	public	int			Parent_Column_ID = 0;

    /** Descripción de Campo */
    public int	TabLevel	= 0;

    /** Descripción de Campo */
    public String	ReplicationType	= "L";

    //

    /** Descripción de Campo */
    public boolean	IsSortTab	= false;

    /** Descripción de Campo */
    public int	Included_Tab_ID	= 0;

    /** Descripción de Campo */
    public int	AD_Image_ID	= 0;

    /** Descripción de Campo */
    public int	AD_ColumnSortYesNo_ID	= 0;

    /** Descripción de Campo */
    public int	AD_ColumnSortOrder_ID	= 0;

    // Derived

    /** Descripción de Campo */
    public boolean	onlyCurrentRows	= true;

    // Only

    /** Descripción de Campo */
    public int	onlyCurrentDays	= 0;

    /** Fields contain MFieldVO entities */
    public ArrayList<MFieldVO>	Fields = null;

    // Database Fields

    /** Descripción de Campo */
    public int	AD_Tab_ID;

    /** Descripción de Campo */
    public int	AD_Table_ID;

    /** AD Window - replicated */
    public int	AD_Window_ID;

    /** Descripción de Campo */
    public String	AccessLevel;

    /** Descripción de Campo */
    public String	CommitWarning;

    /** Descripción de Campo */
    public String	DisplayLogic;

    /** Descripción de Campo */
    public String	OrderByClause;

    /** Descripción de Campo */
    public String	ReadOnlyLogic;

    /** Tab No (not AD_Tab_ID) 0.. */
    public int	TabNo;

    /** Descripción de Campo */
    public String	TableName;

    /** Descripción de Campo */
    public String	WhereClause;

    /** Window No - replicated */
    public int	WindowNo;

    /** Context - replicated */
    public Properties	ctx;
    
    /** Mostrar mensajes processmsg en dialog */
    public boolean showDialogProcessMsg;

    /** Pestaña Siempre actualizable */
    public boolean	IsAlwaysUpdateable	= false;
    
    /** Permite copia de registros */
    private boolean isAllowCopyRecord = true;
    
    /**
     *  protected constructor - must use Factory
     */
    protected MTabVO() {}		// MTabVO

    /**
     *      Create MTab VO
     *
     *  @param wVO value object
     *  @param TabNo tab no
     *      @param rs ResultSet from AD_Tab_v
     *      @param isRO true if window is r/o
     *  @param onlyCurrentRows if true query is limited to not processed records
     *  @return TabVO
     */
    public static MTabVO create(MWindowVO wVO, int TabNo, ResultSet rs, boolean isRO, boolean onlyCurrentRows) {

        CLogger.get().config("#" + TabNo);

        MTabVO	vo	= new MTabVO();

        vo.ctx		= wVO.ctx;
        vo.WindowNo	= wVO.WindowNo;
        vo.AD_Window_ID	= wVO.AD_Window_ID;
        vo.TabNo	= TabNo;

        //
        if (!loadTabDetails(vo, rs)) {
            return null;
        }

        if (isRO) {

            CLogger.get().fine("Tab is ReadOnly");
            vo.IsReadOnly	= true;
        }

        vo.onlyCurrentRows	= onlyCurrentRows;

        // Create Fields
        if (vo.IsSortTab) {
            vo.Fields	= new ArrayList();	// dummy
        } else {

            createFields(vo);

            if ((vo.Fields == null) || (vo.Fields.size() == 0)) {

                CLogger.get().log(Level.SEVERE, "No Fields");

                return null;
            }
        }

        return vo;

    }		// create

    /**
     *  Create Tab Fields
     *  @param mTabVO tab value object
     *  @return true if fields were created
     */
    protected static boolean createFields(MTabVO mTabVO) {

        mTabVO.Fields	= new ArrayList();

        String	sql	= MFieldVO.getSQL(mTabVO.ctx);

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql);

            pstmt.setInt(1, mTabVO.AD_Tab_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MFieldVO	voF	= MFieldVO.create(mTabVO.ctx, mTabVO.WindowNo, mTabVO.TabNo, mTabVO.AD_Window_ID, mTabVO.IsReadOnly, rs);

                if (voF != null) {
                    mTabVO.Fields.add(voF);
                }
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {

            CLogger.get().log(Level.SEVERE, "createFields", e);

            return false;
        }

        return mTabVO.Fields.size() != 0;

    }		// createFields

    /**
     *      Get Variable Value (Evaluatee)
     *      @param variableName name
     *      @return value
     */
    public String get_ValueAsString(String variableName) {
        return Env.getContext(ctx, WindowNo, variableName, false);	// not just window
    }		// get_ValueAsString

    /**
     *      Load Tab Details from rs into vo
     *      @param vo Tab value object
     *      @param rs ResultSet from AD_Tab_v/t
     *      @return true if read ok
     */
    protected static boolean loadTabDetails(MTabVO vo, ResultSet rs) {

        MRole	role		= MRole.getDefault(vo.ctx, false);
        boolean	showTrl		= "Y".equals(Env.getContext(vo.ctx, "#ShowTrl"));
        boolean	showAcct	= "Y".equals(Env.getContext(vo.ctx, "#ShowAcct"));
        boolean	showAdvanced	= "Y".equals(Env.getContext(vo.ctx, "#ShowAdvanced"));

        // CLogger.get().warning("ShowTrl=" + showTrl + ", showAcct=" + showAcct);
        try {

            vo.AD_Tab_ID	= rs.getInt("AD_Tab_ID");
            Env.setContext(vo.ctx, vo.WindowNo, vo.TabNo, "AD_Tab_ID", String.valueOf(vo.AD_Tab_ID));
            vo.Name	= rs.getString("Name");
            Env.setContext(vo.ctx, vo.WindowNo, vo.TabNo, "Name", vo.Name);
            Boolean roleTabAccess = role.getTabAccess(vo.AD_Window_ID, vo.AD_Tab_ID);
            String roleTabWhere = role.getTabWhere(vo.AD_Window_ID, vo.AD_Tab_ID);
            
            // Translation Tab **
            if (rs.getString("IsTranslationTab").equals("Y")) {

                // Document Translation
                vo.TableName	= rs.getString("TableName");

                if (!Env.isBaseTranslation(vo.TableName)	// C_UOM, ...
                        &&!Env.isMultiLingualDocument(vo.ctx)) {
                    showTrl	= false;
                }

                if (!showTrl) {

                    CLogger.get().fine("TrlTab Not displayed - AD_Tab_ID=" + vo.AD_Tab_ID + "=" + vo.Name + ", Table=" + vo.TableName + ", BaseTrl=" + Env.isBaseTranslation(vo.TableName) + ", MultiLingual=" + Env.isMultiLingualDocument(vo.ctx));

                    return false;
                }
            }

            // Advanced Tab    **
            if (!showAdvanced && rs.getString("IsAdvancedTab").equals("Y")) {

                CLogger.get().fine("AdvancedTab Not displayed - AD_Tab_ID=" + vo.AD_Tab_ID + " " + vo.Name);

                return false;
            }

            // Accounting Info Tab     **
            if (!showAcct && rs.getString("IsInfoTab").equals("Y")) {

                CLogger.get().fine("AcctTab Not displayed - AD_Tab_ID=" + vo.AD_Tab_ID + " " + vo.Name);

                return false;
            }
            
            // Tab Access
            
            if (roleTabAccess == null) {
            	
            	CLogger.get().fine("No Role Access - AD_Tab_ID=" + vo.AD_Tab_ID + " " + vo.Name);
            	
            	return false;
            }

            // DisplayLogic
            vo.DisplayLogic	= rs.getString("DisplayLogic");

            if ((vo.DisplayLogic != null) && (vo.DisplayLogic.length() > 0)) {

                if ((Env.parseContext(vo.ctx, 0, vo.DisplayLogic, false, false).length() > 0) && Evaluator.evaluateLogic(vo, vo.DisplayLogic)) {

                    CLogger.get().fine("Tab Not displayed (" + vo.DisplayLogic + ") AD_Tab_ID=" + vo.AD_Tab_ID + " " + vo.Name);

                    return false;
                }
            }

            // Access Level
            vo.AccessLevel	= rs.getString("AccessLevel");

            if (!role.canView(vo.ctx, vo.AccessLevel))		// No Access
            {

                CLogger.get().fine("No Role Access - AD_Tab_ID=" + vo.AD_Tab_ID + " " + vo.Name);

                return false;

            }		// Used by MField.getDefault

            Env.setContext(vo.ctx, vo.WindowNo, vo.TabNo, "AccessLevel", vo.AccessLevel);

            // Table Access
            vo.AD_Table_ID	= rs.getInt("AD_Table_ID");
            Env.setContext(vo.ctx, vo.WindowNo, vo.TabNo, "AD_Table_ID", String.valueOf(vo.AD_Table_ID));

            if (!role.isTableAccess(vo.AD_Table_ID, true)) {

                CLogger.get().fine("No Table Access - AD_Tab_ID=" + vo.AD_Tab_ID + " " + vo.Name);

                return false;
            }

            // El famoso miedo al Booleano!
            // if (rs.getString("IsReadOnly").equals("Y")) {
            //     vo.IsReadOnly	= true;
            // }
            // Se rescribe la condición sin miedo agregando el acceso a pestaña.
            vo.IsReadOnly = rs.getString("IsReadOnly").equals("Y") || !roleTabAccess;

            vo.ReadOnlyLogic	= rs.getString("ReadOnlyLogic");

            if (rs.getString("IsInsertRecord").equals("N")) {
                vo.IsInsertRecord	= false;
            }

            if (rs.getString("IsAlwaysUpdateable").equals("Y")) {
                vo.IsAlwaysUpdateable = true;
            }
            
            vo.setAllowCopyRecord(rs.getString("AllowCopyRecord").equals("Y"));
            
            //
            vo.Description	= rs.getString("Description");

            if (vo.Description == null) {
                vo.Description	= "";
            }

            vo.Help	= rs.getString("Help");

            if (vo.Help == null) {
                vo.Help	= "";
            }

            if (rs.getString("IsSingleRow").equals("Y")) {
                vo.IsSingleRow	= true;
            }

            if (rs.getString("HasTree").equals("Y")) {
                vo.HasTree	= true;
            }

            vo.AD_Table_ID	= rs.getInt("AD_Table_ID");
            vo.TableName	= rs.getString("TableName");

            if (rs.getString("IsView").equals("Y")) {
                vo.IsView	= true;
            }

            vo.AD_Column_ID	= rs.getInt("AD_Column_ID");	// Primary Parent Column

            if (rs.getString("IsSecurityEnabled").equals("Y")) {
                vo.IsSecurityEnabled	= true;
            }

            if (rs.getString("IsDeleteable").equals("Y")) {
                vo.IsDeleteable	= true;
            }

            if (rs.getString("IsHighVolume").equals("Y")) {
                vo.IsHighVolume	= true;
            }

            vo.CommitWarning	= rs.getString("CommitWarning");

            if (vo.CommitWarning == null) {
                vo.CommitWarning	= "";
            }

            vo.WhereClause	= rs.getString("WhereClause");

            if (vo.WhereClause == null) {
                vo.WhereClause	= "";
            }
            
            if (roleTabWhere != null && roleTabWhere.length() > 0) {
            	vo.WhereClause += " AND " + roleTabWhere; 
            }

            vo.OrderByClause	= rs.getString("OrderByClause");

            if (vo.OrderByClause == null) {
                vo.OrderByClause	= "";
            }

            vo.AD_Process_ID	= rs.getInt("AD_Process_ID");

            if (rs.wasNull()) {
                vo.AD_Process_ID	= 0;
            }

            vo.AD_Image_ID	= rs.getInt("AD_Image_ID");

            if (rs.wasNull()) {
                vo.AD_Image_ID	= 0;
            }

            vo.Included_Tab_ID	= rs.getInt("Included_Tab_ID");

            if (rs.wasNull()) {
                vo.Included_Tab_ID	= 0;
            }

            //
            vo.TabLevel	= rs.getInt("TabLevel");

            if (rs.wasNull()) {
                vo.TabLevel	= 0;
            }

            //
            vo.IsSortTab	= rs.getString("IsSortTab").equals("Y");

            if (vo.IsSortTab) {

                vo.AD_ColumnSortOrder_ID	= rs.getInt("AD_ColumnSortOrder_ID");
                vo.AD_ColumnSortYesNo_ID	= rs.getInt("AD_ColumnSortYesNo_ID");
            }
            
            vo.showDialogProcessMsg = rs.getString("isprocessmsgshowdialog") != null?rs.getString("isprocessmsgshowdialog").equals("Y"):false;
            //
            // Replication Type - set R/O if Reference
            try {

                int	index	= rs.findColumn("ReplicationType");

                vo.ReplicationType	= rs.getString(index);

                if ("R".equals(vo.ReplicationType)) {
                    vo.IsReadOnly	= true;
                }

            } catch (Exception e) {}

        } catch (SQLException ex) {

            CLogger.get().log(Level.SEVERE, "loadTabDetails", ex);

            return false;
        }

        return true;

    }		// loadTabDetails

    //~--- get methods --------------------------------------------------------


    // Es necesario al menos por el momento, no recuperar las pestañas incluidas en la solución web,
    // pero mantener el query original para la solución swing, con lo cual se adoptó esta solución
    protected static boolean omitIncludedTabs = false;
    public static void omitIncludedTabs() {
		omitIncludedTabs = true;
    }
    
    /**
     *  Return the SQL statement used for the MTabVO.create
     *  @param ctx context
     *  @return SQL SELECT String
     */
    protected static String getSQL(Properties ctx) {

        // View only returns IsActive='Y'
        String	sql	= " SELECT * FROM AD_Tab_v WHERE AD_Window_ID=? ";

        if (!Env.isBaseLanguage(ctx, "AD_Window")) {
            sql	= " SELECT * FROM AD_Tab_vt WHERE AD_Window_ID=?" + " AND AD_Language='" + Env.getAD_Language(ctx) + "' ";
        }
        
        // Clausula adicional para excluir pestañas incluidas
        if (omitIncludedTabs) {
        	sql = sql + " AND AD_Tab_ID NOT IN (SELECT included_tab_id FROM AD_Tab_vt WHERE included_tab_id IS NOT NULL) ";
        }
        
        sql = sql + " ORDER BY SeqNo";
        return sql;
    }		// getSQL

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Context including contained elements
     *  @param newCtx new context
     */
    public void setCtx(Properties newCtx) {

        ctx	= newCtx;

        for (int i = 0; i < Fields.size(); i++) {

            MFieldVO	field	= (MFieldVO) Fields.get(i);

            field.setCtx(newCtx);
        }

    }		// setCtx
    
	/**
	 * 	Clone
	 * 	@param Ctx context
	 * 	@param windowNo no
	 *	@return MTabVO or null
	 */
	protected MTabVO clone(Properties Ctx, int windowNo)
	{
		MTabVO clone = new MTabVO(Ctx, windowNo);
		clone.AD_Window_ID = AD_Window_ID;
		clone.TabNo = TabNo;
		Env.setContext(Ctx, windowNo, clone.TabNo, MTab.CTX_AD_Tab_ID, String.valueOf(clone.AD_Tab_ID));
		//
		clone.AD_Tab_ID = AD_Tab_ID;
		clone.Name = Name;
		Env.setContext(Ctx, windowNo, clone.TabNo, MTab.CTX_Name, clone.Name);
		clone.Description = Description;
		clone.Help = Help;
		clone.IsSingleRow = IsSingleRow;
		clone.IsReadOnly = IsReadOnly;
		clone.IsInsertRecord = IsInsertRecord;
		clone.IsAlwaysUpdateable = IsAlwaysUpdateable;
		clone.isAllowCopyRecord = isAllowCopyRecord; 
		clone.HasTree = HasTree;
		clone.AD_Table_ID = AD_Table_ID;
		clone.AD_Column_ID = AD_Column_ID;
		clone.Parent_Column_ID = Parent_Column_ID;
		clone.TableName = TableName;
		clone.IsView = IsView;
		clone.AccessLevel = AccessLevel;
		clone.IsSecurityEnabled = IsSecurityEnabled;
		clone.IsDeleteable = IsDeleteable;
		clone.IsHighVolume = IsHighVolume;
		clone.AD_Process_ID = AD_Process_ID;
		clone.CommitWarning = CommitWarning;
		clone.WhereClause = WhereClause;
		clone.OrderByClause = OrderByClause;
		clone.ReadOnlyLogic = ReadOnlyLogic;
		clone.DisplayLogic = DisplayLogic;
		clone.TabLevel = TabLevel;
		clone.AD_Image_ID = AD_Image_ID;
		clone.Included_Tab_ID = Included_Tab_ID;
		clone.ReplicationType = ReplicationType;
		Env.setContext(Ctx, windowNo, clone.TabNo, MTab.CTX_AccessLevel, clone.AccessLevel);
		Env.setContext(Ctx, windowNo, clone.TabNo, MTab.CTX_AD_Table_ID, String.valueOf(clone.AD_Table_ID));

		//
		clone.IsSortTab = IsSortTab;
		clone.AD_ColumnSortOrder_ID = AD_ColumnSortOrder_ID;
		clone.AD_ColumnSortYesNo_ID = AD_ColumnSortYesNo_ID;
		//  Derived
		clone.onlyCurrentRows = true;
		clone.onlyCurrentDays = 0;

		clone.Fields = new ArrayList<MFieldVO>();
		for (int i = 0; i < Fields.size(); i++)
		{
			MFieldVO field = Fields.get(i);
			MFieldVO cloneField = field.clone(Ctx, windowNo, TabNo, 
				AD_Window_ID, AD_Tab_ID, IsReadOnly);
			if (cloneField == null)
				return null;
			clone.Fields.add(cloneField);
		}
		
		return clone;
	}	//	clone

	/**************************************************************************
	 *  protected constructor - must use Factory
	 *  @param Ctx context
	 *  @param windowNo window
	 */
	protected MTabVO (Properties Ctx, int windowNo)
	{
		ctx = Ctx;
		WindowNo = windowNo;
	}   //  MTabVO

	public boolean isAllowCopyRecord() {
		return isAllowCopyRecord;
	}

	public void setAllowCopyRecord(boolean isAllowCopyRecord) {
		this.isAllowCopyRecord = isAllowCopyRecord;
	}

	
}	// MTabVO



/*
 * @(#)MTabVO.java   02.jul 2007
 * 
 *  Fin del fichero MTabVO.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
