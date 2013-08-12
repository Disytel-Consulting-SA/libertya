/*
 * @(#)MFieldVO.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Properties;
import java.util.logging.Level;

/**
 *  Field Model Value Object
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version  $Id: MFieldVO.java,v 1.13 2005/04/25 05:04:29 jjanke Exp $
 */
public class MFieldVO implements Serializable {

    /** Descripción de Campo */
    static final long	serialVersionUID	= 4385061125114436797L;

    /** Context */
    public Properties	ctx	= null;

    /** Is the Tab Read Only */
    public boolean	tabReadOnly	= false;

    /** Lookup Value Object */
    public MLookupInfo	lookupInfo	= null;

	//*  Feature Request FR [ 1757088 ]
	public int          Included_Tab_ID = 0;

	/** Collapse By Default * */
	public boolean IsCollapsedByDefault = false;
	/**  Autocompletion for textfields - Feature Request FR [ 1757088 ] */
	public boolean IsAutocomplete = false;

	public String InfoFactoryClass = null;
	
    // Process Parameter

    /** Descripción de Campo */
    public boolean	isRange	= false;

    /** Is Process Parameter */
    public boolean	isProcess	= false;

    /** Descripción de Campo */
    public int	displayType	= 0;
    
	/**	Table ID		*/
	public int          AD_Table_ID = 0;


    /** Descripción de Campo */
    public String	ValueMin	= "";

    /** Descripción de Campo */
    public String	ValueMax	= "";

    public boolean sameLine = false;
    
    // Lookup

    /** Descripción de Campo */
    public String	ValidationCode	= "";

    /** Descripción de Campo */
    public String	VFormat	= "";

    /** Descripción de Campo */
    public int	SortNo	= 0;

    /** Descripción de Campo */
    public String	ReadOnlyLogic	= "";

    /** Descripción de Campo */
    public String	ObscureType	= null;

    /** Descripción de Campo */
    public boolean	IsUpdateable	= false;

    /** Descripción de Campo */
    public boolean	IsSelectionColumn	= false;

    /** Descripción de Campo */
    public boolean	IsSameLine	= false;

    /** Descripción de Campo */
    public boolean	IsReadOnly	= false;

    /** Descripción de Campo */
    public boolean	IsParent	= false;

    /** Descripción de Campo */
    public boolean	IsMandatory	= false;

    /** Descripción de Campo */
    public boolean	IsKey	= false;

    /** Descripción de Campo */
    public boolean	IsHeading	= false;

    /** Descripción de Campo */
    public boolean	IsFieldOnly	= false;

    /** Descripción de Campo */
    public boolean	IsEncryptedField	= false;

    /**	Storage Encryption	*/
	public boolean      IsEncryptedColumn = false;

    /** Descripción de Campo */
    public boolean	IsDisplayed	= false;

    /** Descripción de Campo */
    public boolean IsDisplayedInGrid = false;
    
    /** Descripción de Campo */
    public boolean	IsAlwaysUpdateable	= false;

    /** Descripción de Campo */
    public String	Help	= "";
    
	/**	Mandatory Logic	*/
	public String 		MandatoryLogic = "";

    /** Descripción de Campo */
    public String	Header	= "";

    /** Descripción de Campo */
    public int	FieldLength	= 0;

    /** Descripción de Campo */
    public String	FieldGroup	= "";
    
	/**	Field Group	Type	*/
	public String       FieldGroupType = "";
	
    /** Descripción de Campo */
    public String	DisplayLogic	= "";

    /** Descripción de Campo */
    public int	DisplayLength	= 0;

    /** Descripción de Campo */
    public String	Description	= "";

    /** Descripción de Campo */
    public String	DefaultValue2	= "";

    /** Descripción de Campo */
    public String	DefaultValue	= "";

    // Database Fields

    /** Descripción de Campo */
    public String	ColumnName	= "";

    /** Descripción de Campo */
    public String	Callout	= "";

    /** Descripción de Campo */
    public boolean CalloutAlsoOnLoad = false;
    
    /** Descripción de Campo */
    public int	AD_Reference_Value_ID	= 0;

    /** Descripción de Campo */
    public int	AD_Process_ID	= 0;

    /** Descripción de Campo */
    public int	AD_Column_ID	= 0;

    /** AD_Winmdow_ID */
    public int	AD_Window_ID;

	public int			AD_Tab_ID;
	
    /** AD_Winmdow_ID */
    public int	AD_Field_ID;

    /** Descripción de Campo */
    public String	ColumnSQL;

    /** Tab No */
    public int	TabNo;

    /** Window No */
    public int	WindowNo;

    /**
     *  protected constructor
     *  @param ctx context
     *  @param WindowNo window
     *  @param TabNo tab
     *  @param AD_Window_ID window
     *  @param tabReadOnly read only
     */
    protected MFieldVO(Properties ctx, int WindowNo, int TabNo, int AD_Window_ID, boolean tabReadOnly) {

        this.ctx		= ctx;
        this.WindowNo		= WindowNo;
        this.TabNo		= TabNo;
        this.AD_Window_ID	= AD_Window_ID;
        this.tabReadOnly	= tabReadOnly;

    }		// MFieldVO

    /**
     *  Create Field Value Object
     *  @param ctx context
     *  @param WindowNo window
     *  @param TabNo tab
     *  @param AD_Window_ID window
     *  @param readOnly r/o
     *  @param rs resultset AD_Field_v
     *  @return MFieldVO
     */
    public static MFieldVO create(Properties ctx, int WindowNo, int TabNo, int AD_Window_ID, boolean readOnly, ResultSet rs) {

        MFieldVO vo	  = new MFieldVO(ctx, WindowNo, TabNo, AD_Window_ID, readOnly);
        // Perfil actual. Se utiliza para determinar el acceso al campo.
        MRole	 role = MRole.getDefault(vo.ctx, false);
        try {

            vo.ColumnName	= rs.getString("ColumnName");
            vo.AD_Field_ID = rs.getInt("AD_Field_ID");
            
            if (vo.ColumnName == null) {
                return null;
            }

            CLogger.get().fine(vo.ColumnName);

            ResultSetMetaData	rsmd	= rs.getMetaData();

            for (int i = 1; i <= rsmd.getColumnCount(); i++) {

                String	columnName	= rsmd.getColumnName(i);
                String roleFieldAccess = role.getFieldAccess(vo.AD_Field_ID);
                
                if (columnName.equalsIgnoreCase("Name")) {
                    vo.Header	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("AD_Reference_ID")) {
                    vo.displayType	= rs.getInt(i);
                } else if (columnName.equalsIgnoreCase("AD_Column_ID")) {
                    vo.AD_Column_ID	= rs.getInt(i);
                } else if (columnName.equalsIgnoreCase("DisplayLength")) {
                    vo.DisplayLength	= rs.getInt(i);
                } else if (columnName.equalsIgnoreCase("IsSameLine")) {
                    vo.IsSameLine	= "Y".equals(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("IsDisplayed")) {
                    vo.IsDisplayed	= 
                    	"Y".equals(rs.getString(i)) && 
                    	!MFieldAccess.ACCESSTYPE_NotDisplayed.equals(roleFieldAccess);
                }else if (columnName.equalsIgnoreCase("IsDisplayedInGrid")) {
					vo.IsDisplayedInGrid  = "Y".equals(rs.getString (i));
                } else if (columnName.equalsIgnoreCase("DisplayLogic")) {
                    vo.DisplayLogic	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("DefaultValue")) {
                    vo.DefaultValue	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("IsMandatory")) {
                    vo.IsMandatory	= "Y".equals(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("IsReadOnly")) {
                    vo.IsReadOnly	= 
                    	"Y".equals(rs.getString(i)) ||
                    	MFieldAccess.ACCESSTYPE_ReadOnly.equals(roleFieldAccess);
                } else if (columnName.equalsIgnoreCase("IsUpdateable")) {
                    vo.IsUpdateable	= "Y".equals(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("IsAlwaysUpdateable")) {
                    vo.IsAlwaysUpdateable	= "Y".equals(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("IsHeading")) {
                    vo.IsHeading	= "Y".equals(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("IsFieldOnly")) {
                    vo.IsFieldOnly	= "Y".equals(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("IsEncryptedField")) {
                    vo.IsEncryptedField	= "Y".equals(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("IsSelectionColumn")) {
                    vo.IsSelectionColumn	= "Y".equals(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("SortNo")) {
                    vo.SortNo	= rs.getInt(i);
                } else if (columnName.equalsIgnoreCase("FieldLength")) {
                    vo.FieldLength	= rs.getInt(i);
                } else if (columnName.equalsIgnoreCase("VFormat")) {
                    vo.VFormat	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("ValueMin")) {
                    vo.ValueMin	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("ValueMax")) {
                    vo.ValueMax	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("FieldGroup")) {
                    vo.FieldGroup	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("IsKey")) {
                    vo.IsKey	= "Y".equals(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("IsParent")) {
                    vo.IsParent	= "Y".equals(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("Description")) {
                    vo.Description	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("Help")) {
                    vo.Help	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("Callout")) {
                    vo.Callout	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("CalloutAlsoOnLoad")) {
                    vo.CalloutAlsoOnLoad	= "Y".equalsIgnoreCase(rs.getString(i));
                } else if (columnName.equalsIgnoreCase("AD_Process_ID")) {
                    vo.AD_Process_ID	= rs.getInt(i);
                } else if (columnName.equalsIgnoreCase("ReadOnlyLogic")) {
                    vo.ReadOnlyLogic	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("ObscureType")) {
                    vo.ObscureType	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("AD_Reference_Value_ID")) {
                    vo.AD_Reference_Value_ID	= rs.getInt(i);
                } else if (columnName.equalsIgnoreCase("ValidationCode")) {
                    vo.ValidationCode	= rs.getString(i);
                } else if (columnName.equalsIgnoreCase("ColumnSQL")) {
                    vo.ColumnSQL	= rs.getString(i);
                }
				else if (columnName.equalsIgnoreCase("Included_Tab_ID"))
					vo.Included_Tab_ID = rs.getInt(i);
            }
            // En el caso que la columna IsReadOnly tenga el valor true, se setea el valor de la columna IsAlwaysUpdateable en false
            // Si el campo IsReadOnly es true, no se puede actualizar el valor del campo, independientemente de si es Siempre Actualizable o no.
            if (vo.IsReadOnly) vo.IsAlwaysUpdateable = false;

            if (vo.Header == null) {
                vo.Header	= vo.ColumnName;
            }

        } catch (SQLException e) {

            CLogger.get().log(Level.SEVERE, "create", e);

            return null;
        }

        vo.initFinish();

        return vo;

    }		// create

    /**
     *  Create range "to" Parameter Field from "from" Parameter Field
     *  @param voF field value object
     *  @return to MFieldVO
     */
    public static MFieldVO createParameter(MFieldVO voF) {

        MFieldVO	voT	= new MFieldVO(voF.ctx, voF.WindowNo, voF.TabNo, voF.AD_Window_ID, voF.tabReadOnly);

        voT.isProcess		= true;
        voT.IsDisplayed		= true;
        voT.IsDisplayedInGrid = true;
        voT.IsReadOnly		= false;
        voT.IsUpdateable	= true;

        //
        voT.AD_Column_ID	= voF.AD_Column_ID;	// AD_Process_Para_ID
        voT.ColumnName		= voF.ColumnName;
        voT.Header		= voF.Header;
        voT.Description		= voF.Description;
        voT.Help		= voF.Help;
        voT.displayType		= voF.displayType;
        voT.IsMandatory		= voF.IsMandatory;
        voT.FieldLength		= voF.FieldLength;
        voT.DisplayLength	= voF.FieldLength;
        voT.DefaultValue	= voF.DefaultValue2;
        voT.DisplayLogic	= voF.DisplayLogic;
        voT.VFormat		= voF.VFormat;
        voT.ValueMin		= voF.ValueMin;
        voT.ValueMax		= voF.ValueMax;
        voT.isRange		= voF.isRange;

        // Eloy Gomez
        // Permitimos rangos de VLookup
        voT.AD_Reference_Value_ID 	= voF.AD_Reference_Value_ID;
        voT.ValidationCode 			= voF.ValidationCode;
        voT.initFinish();
        
        //
        return voT;

    }		// createParameter

    /**
     *  Init Field for Process Parameter
     *  @param ctx context
     *  @param WindowNo window
     *  @param rs result set AD_Process_Para
     *  @return MFieldVO
     */
    public static MFieldVO createParameter(Properties ctx, int WindowNo, ResultSet rs) {

        MFieldVO	vo	= new MFieldVO(ctx, WindowNo, 0, 0, false);

        vo.isProcess	= true;
        vo.IsDisplayed	= true;
        vo.IsDisplayedInGrid = true;
        vo.IsReadOnly	= false;
        vo.IsUpdateable	= true;

        try {

            vo.AD_Column_ID	= rs.getInt("AD_Process_Para_ID");	// **
            vo.ColumnName	= rs.getString("ColumnName");
            vo.Header		= rs.getString("Name");
            vo.Description	= rs.getString("Description");
            vo.Help		= rs.getString("Help");
            vo.displayType	= rs.getInt("AD_Reference_ID");
            vo.IsMandatory	= rs.getString("IsMandatory").equals("Y");
            vo.FieldLength	= rs.getInt("FieldLength");
            vo.DisplayLength	= vo.FieldLength;
            vo.DisplayLogic	= rs.getString("DisplayLogic");
            vo.IsEncryptedField	= "Y".equals(rs.getString("IsEncrypted"));
            
            // vo.DisplayLength = rs.getInt("DisplayLength");
            vo.DefaultValue	= rs.getString("DefaultValue");
            vo.DefaultValue2	= rs.getString("DefaultValue2");
            vo.VFormat		= rs.getString("VFormat");
            vo.ValueMin		= rs.getString("ValueMin");
            vo.ValueMax		= rs.getString("ValueMax");
            vo.isRange		= rs.getString("IsRange").equals("Y");
            vo.sameLine	= rs.getString("sameline").equals("Y");
            //
            vo.AD_Reference_Value_ID	= rs.getInt("AD_Reference_Value_ID");
            vo.ValidationCode		= rs.getString("ValidationCode");

        } catch (SQLException e) {
            CLogger.get().log(Level.SEVERE, "createParameter", e);
        }

        //
        vo.initFinish();

        if (vo.DefaultValue2 == null) {
            vo.DefaultValue2	= "";
        }

        return vo;

    }		// createParameter

    /**
     *  Make a standard field (Created/Updated/By)
     *  @param ctx context
     *  @param WindowNo window
     *  @param TabNo tab
     *  @param AD_Window_ID window
     *  @param tabReadOnly rab is r/o
     *  @param isCreated is Created field
     *  @param isTimestamp is the timestamp (not by)
     *  @return MFieldVO
     */
    public static MFieldVO createStdField(Properties ctx, int WindowNo, int TabNo, int AD_Window_ID, boolean tabReadOnly, boolean isCreated, boolean isTimestamp) {

        MFieldVO	vo	= new MFieldVO(ctx, WindowNo, TabNo, AD_Window_ID, tabReadOnly);

        vo.ColumnName	= isCreated
                          ? "Created"
                          : "Updated";

        if (!isTimestamp) {
            vo.ColumnName	+= "By";
        }

        vo.displayType	= isTimestamp
                          ? DisplayType.DateTime
                          : DisplayType.Table;

        if (!isTimestamp) {
            vo.AD_Reference_Value_ID	= 110;		// AD_User Table Reference
        }

        vo.IsDisplayed	= false;
        vo.IsDisplayedInGrid = true;
        vo.IsMandatory	= false;
        vo.IsReadOnly	= false;
        vo.IsUpdateable	= true;
        vo.initFinish();

        return vo;

    }		// initStdField

    /**
     *  Validate Fields and create LookupInfo if required
     */
    protected void initFinish() {

        // Not null fields
        if (DisplayLogic == null) {
            DisplayLogic	= "";
        }

        if (DefaultValue == null) {
            DefaultValue	= "";
        }

        if (FieldGroup == null) {
            FieldGroup	= "";
        }

        if (Description == null) {
            Description	= "";
        }

        if (Help == null) {
            Help	= "";
        }

        if (Callout == null) {
            Callout	= "";
        }

        if (ReadOnlyLogic == null) {
            ReadOnlyLogic	= "";
        }

        // Create Lookup, if not ID
        if (DisplayType.isLookup(displayType)) {

            try {
                lookupInfo	= MLookupFactory.getLookupInfo(ctx, WindowNo, AD_Column_ID, displayType, Env.getLanguage(ctx), ColumnName, AD_Reference_Value_ID, IsParent, ValidationCode);
            } catch (Exception e)	// Cannot create Lookup
            {

                CLogger.get().log(Level.SEVERE, "No LookupInfo for " + ColumnName, e);
                displayType	= DisplayType.ID;
            }
        }
    }		// initFinish

    //~--- get methods --------------------------------------------------------

    /**
     *  Return the SQL statement used for the MFieldVO.create
     *  @param ctx context
     *  @return SQL with or w/o translation and 1 parameter
     */
    public static String getSQL(Properties ctx) {

        // IsActive is part of View
        String	sql	= "SELECT * FROM AD_Field_v WHERE AD_Tab_ID=?" + " ORDER BY iskey desc, SeqNo asc";

        if (!Env.isBaseLanguage(ctx, "AD_Tab")) {
            sql	= "SELECT * FROM AD_Field_vt WHERE AD_Tab_ID=?" + " AND AD_Language='" + Env.getAD_Language(ctx) + "'" + " ORDER BY iskey desc, SeqNo asc";
        }

        return sql;
    }		// getSQL

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Context including contained elements
     *  @param newCtx new context
     */
    public void setCtx(Properties newCtx) {

        ctx	= newCtx;

        if (lookupInfo != null) {
            lookupInfo.ctx	= newCtx;
        }

    }		// setCtx
    
	/**
	 * 	Clone Field.
	 *	@param Ctx ctx
	 *	@param windowNo window no
	 *	@param tabNo tab no
	 *	@param ad_Window_ID window id
	 *	@param ad_Tab_ID tab id
	 *	@param TabReadOnly r/o
	 *	@return Field or null
	 */
	public MFieldVO clone(Properties Ctx, int windowNo, int tabNo, 
		int ad_Window_ID, int ad_Tab_ID, 
		boolean TabReadOnly)
	{
		MFieldVO clone = new MFieldVO(Ctx, windowNo, tabNo, 
			ad_Window_ID, ad_Tab_ID, TabReadOnly);
		//
		clone.isProcess = false;
		//  Database Fields
		clone.ColumnName = ColumnName;
		clone.ColumnSQL = ColumnSQL;
		clone.Header = Header;
		clone.displayType = displayType;
		clone.AD_Table_ID = AD_Table_ID;
		clone.AD_Column_ID = AD_Column_ID;
		clone.DisplayLength = DisplayLength;
		clone.IsSameLine = IsSameLine;
		clone.IsDisplayed = IsDisplayed;
		clone.DisplayLogic = DisplayLogic;
		clone.DefaultValue = DefaultValue;
		clone.IsMandatory = IsMandatory;
		clone.IsReadOnly = IsReadOnly;
		clone.IsUpdateable = IsUpdateable;
		clone.IsAlwaysUpdateable = IsAlwaysUpdateable;
		clone.IsHeading = IsHeading;
		clone.IsFieldOnly = IsFieldOnly;
		clone.IsEncryptedField = IsEncryptedField;
		clone.IsEncryptedColumn = IsEncryptedColumn;
		clone.IsSelectionColumn = IsSelectionColumn;
		clone.IsAutocomplete = IsAutocomplete;
		clone.SortNo = SortNo;
		clone.FieldLength = FieldLength;
		clone.VFormat = VFormat;
		clone.ValueMin = ValueMin;
		clone.ValueMax = ValueMax;
		clone.FieldGroup = FieldGroup;
		clone.FieldGroupType = FieldGroupType;
		clone.IsKey = IsKey;
		clone.IsParent = IsParent;
		clone.Callout = Callout;
		clone.AD_Process_ID = AD_Process_ID;
		clone.Description = Description;
		clone.Help = Help;
		clone.ReadOnlyLogic = ReadOnlyLogic;
		clone.MandatoryLogic = MandatoryLogic;
		clone.ObscureType = ObscureType;
		//	Lookup
		clone.ValidationCode = ValidationCode;
		clone.AD_Reference_Value_ID = AD_Reference_Value_ID;
		clone.lookupInfo = lookupInfo;

		//  Process Parameter
		clone.isRange = isRange;
		clone.DefaultValue2 = DefaultValue2;

		return clone;
	}	//	clone

	/**************************************************************************
	 *  protected constructor.
	 *  @param Ctx context
	 *  @param windowNo window
	 *  @param tabNo tab
	 *  @param ad_Window_ID window
	 *  @param ad_Tab_ID tab
	 *  @param TabReadOnly tab read only
	 */
	protected MFieldVO (Properties Ctx, int windowNo, int tabNo, 
		int ad_Window_ID, int ad_Tab_ID, boolean TabReadOnly)
	{
		ctx = Ctx;
		WindowNo = windowNo;
		TabNo = tabNo;
		AD_Window_ID = ad_Window_ID;
		AD_Tab_ID = ad_Tab_ID;
		tabReadOnly = TabReadOnly;
	}   //  MFieldVO

	
}	// MFieldVO



/*
 * @(#)MFieldVO.java   02.jul 2007
 * 
 *  Fin del fichero MFieldVO.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
