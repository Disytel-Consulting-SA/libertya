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



package org.openXpertya.report.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.report.core.RModel;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RColumn {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param columnName
     * @param displayType
     */

    public RColumn( Properties ctx,String columnName,int displayType ) {
        this( ctx,columnName,displayType,null );
    }    // RColumn

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param columnName
     * @param displayType
     * @param sql
     */

    public RColumn( Properties ctx,String columnName,int displayType,String sql ) {
        m_colHeader   = Msg.translate( ctx,columnName );
        m_displayType = displayType;
        m_colSQL      = sql;

        if( (m_colSQL == null) || (m_colSQL.length() == 0) ) {
            m_colSQL = columnName;
        }

        // Strings

        if( (displayType == DisplayType.String) || (displayType == DisplayType.Text) || (displayType == DisplayType.Memo) ) {
            m_colClass = String.class;    // default size=30

            // Amounts

        } else if( displayType == DisplayType.Amount ) {
            m_colClass = BigDecimal.class;
            m_colSize  = 70;
        }

        // Boolean

        else if( displayType == DisplayType.YesNo ) {
            m_colClass = Boolean.class;

            // Date

        } else if( DisplayType.isDate( displayType )) {
            m_colClass = Timestamp.class;

            // Number

        } else if( (displayType == DisplayType.Quantity) || (displayType == DisplayType.Number) || (displayType == DisplayType.CostPrice) ) {
            m_colClass = Double.class;
            m_colSize  = 70;
        }

        // Integer

        else if( displayType == DisplayType.Integer ) {
            m_colClass = Integer.class;

            // TableDir, Search,...

        } else {
            m_colClass = String.class;

            Language language = Language.getLanguage( Env.getAD_Language( ctx ));

            if( columnName.equals( "Account_ID" ) || columnName.equals( "User1_ID" ) || columnName.equals( "User2_ID" )) {
                m_colSQL += ",(" + MLookupFactory.getLookup_TableDirEmbed( language,"C_ElementValue_ID",RModel.TABLE_ALIAS,columnName ) + ")";
                m_isIDcol = true;
            } else if( columnName.equals( "C_LocFrom_ID" ) || columnName.equals( "C_LocTo_ID" )) {
                m_colSQL += ",(" + MLookupFactory.getLookup_TableDirEmbed( language,"C_Location_ID",RModel.TABLE_ALIAS,columnName ) + ")";
                m_isIDcol = true;
            } else if( columnName.equals( "AD_OrgTrx_ID" )) {
                m_colSQL += ",(" + MLookupFactory.getLookup_TableDirEmbed( language,"AD_Org_ID",RModel.TABLE_ALIAS,columnName ) + ")";
                m_isIDcol = true;
            } else if( displayType == DisplayType.TableDir ) {
                m_colSQL += ",(" + MLookupFactory.getLookup_TableDirEmbed( language,columnName,RModel.TABLE_ALIAS ) + ")";
                m_isIDcol = true;
            }
        }
    }    // RColumn

    /**
     * Constructor de la clase ...
     *
     *
     * @param colHeader
     * @param colSQL
     * @param colClass
     */

    public RColumn( String colHeader,String colSQL,Class colClass ) {
        m_colHeader = colHeader;
        m_colSQL    = colSQL;
        m_colClass  = colClass;
    }    // RColumn

    
	/**
	 *  Create Report Column
	 *	@param ctx context 
	 *	@param columnName column name
	 *	@param displayType display type
	 *	@param sql sql (if null then columnName is used). 
	 *	@param AD_Reference_Value_ID List/Table Reference
	 *	@param refColumnName UserReference column name
	 *	Will be overwritten if TableDir or Search 
	 */
	public RColumn (Properties ctx, String columnName, int displayType, 
		String sql,	int AD_Reference_Value_ID, String refColumnName)
	{
		m_columnName = columnName;
		m_colHeader = Msg.translate(ctx, columnName);
		if (refColumnName != null)
			m_colHeader = Msg.translate(ctx, refColumnName);
		m_displayType = displayType;
		m_colSQL = sql;
		if (m_colSQL == null || m_colSQL.length() == 0)
			m_colSQL = columnName;

		//  Strings
		if (DisplayType.isText(displayType))
			m_colClass = String.class;                  //  default size=30
		//  Amounts
		else if (displayType == DisplayType.Amount)
		{
			m_colClass = BigDecimal.class;
			m_colSize = 70;
		}
		//  Boolean
		else if (displayType == DisplayType.YesNo)
			m_colClass = Boolean.class;
		//  Date
		else if (DisplayType.isDate(displayType))
			m_colClass = Timestamp.class;
		//  Number
		else if (displayType == DisplayType.Quantity 
			|| displayType == DisplayType.Number  
			|| displayType == DisplayType.CostPrice)
		{
			m_colClass = Double.class;
			m_colSize = 70;
		}
		//  Integer
		else if (displayType == DisplayType.Integer)
			m_colClass = Integer.class;
		//  List
		else if (displayType == DisplayType.List)
		{
			Language language = Language.getLanguage(Env.getAD_Language(ctx));
			m_colSQL = "(" + MLookupFactory.getLookup_ListEmbed(
				language, AD_Reference_Value_ID, columnName) + ")";
			m_displaySQL = m_colSQL;
			m_colClass = String.class;
			m_isIDcol = false;
		}
		else if (displayType == DisplayType.ID) {
			m_colClass = Integer.class;
		}
		/**  Table
		else if (displayType == DisplayType.Table)
		{
			Language language = Language.getLanguage(Env.getAD_Language(ctx));
			m_colSQL += ",(" + MLookupFactory.getLookup_TableEmbed(
				language, columnName, RModel.TABLE_ALIAS, AD_Reference_Value_ID) + ")";
			m_colClass = String.class;
			m_isIDcol = false;
		}	**/
		//  TableDir, Search,...
		else
		{
			m_colClass = String.class;
			Language language = Language.getLanguage(Env.getAD_Language(ctx));
			if (columnName.equals("Account_ID") 
				|| columnName.equals("User1_ID") || columnName.equals("User2_ID"))
			{
				m_displaySQL = "(" + MLookupFactory.getLookup_TableDirEmbed(
					language, "C_ElementValue_ID", RModel.TABLE_ALIAS, columnName) + ")";
				m_colSQL += "," + m_displaySQL;
				m_isIDcol = true;
			}
			else if (columnName.startsWith("UserElement") && refColumnName != null)
			{
				m_displaySQL = "(" + MLookupFactory.getLookup_TableDirEmbed(
					language, refColumnName, RModel.TABLE_ALIAS, columnName) + ")";
				m_colSQL += "," + m_displaySQL;
				m_isIDcol = true;
			}
			else if (columnName.equals("C_LocFrom_ID") || columnName.equals("C_LocTo_ID"))
			{
				m_displaySQL = "(" + MLookupFactory.getLookup_TableDirEmbed(
					language, "C_Location_ID", RModel.TABLE_ALIAS, columnName) + ")";
				m_colSQL += "," + m_displaySQL;
				m_isIDcol = true;
			}
			else if (columnName.equals("AD_OrgTrx_ID"))
			{
				m_displaySQL = "(" + MLookupFactory.getLookup_TableDirEmbed(
					language, "AD_Org_ID", RModel.TABLE_ALIAS, columnName) + ")";
				m_colSQL += "," + m_displaySQL;
				m_isIDcol = true;
			}
			else if (displayType == DisplayType.TableDir)
			{
				m_displaySQL = "(" + MLookupFactory.getLookup_TableDirEmbed(
					language, columnName, RModel.TABLE_ALIAS) + ")";
				m_colSQL += "," + m_displaySQL;
				m_isIDcol = true;
			}
		}
	}   //  RColumn
    
	
	/** Column Name                 */
	private String		m_columnName;
	
    /** Descripción de Campos */

    private String m_colHeader;

    /** Descripción de Campos */

    private String m_colSQL;

	/** Column Display SQL          */
	private String      m_displaySQL;
    
    /** Descripción de Campos */
    
    private Class m_colClass;

    /** Descripción de Campos */

    private int m_displayType = 0;

    /** Descripción de Campos */

    private int m_colSize = 30;

    /** Descripción de Campos */

    private boolean m_readOnly = true;

    /** Descripción de Campos */

    private boolean m_colorColumn = false;

    /** Descripción de Campos */

    private boolean m_isIDcol = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColHeader() {
        return m_colHeader;
    }

    /**
     * Descripción de Método
     *
     *
     * @param colHeader
     */

    public void setColHeader( String colHeader ) {
        m_colHeader = colHeader;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColSQL() {
        return m_colSQL;
    }

    /**
     * Descripción de Método
     *
     *
     * @param colSQL
     */

    public void setColSQL( String colSQL ) {
        m_colSQL = colSQL;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isIDcol() {
        return m_isIDcol;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Class getColClass() {
        return m_colClass;
    }

    /**
     * Descripción de Método
     *
     *
     * @param colClass
     */

    public void setColClass( Class colClass ) {
        m_colClass = colClass;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getColSize() {
        return m_colSize;
    }    // getColumnSize

    /**
     * Descripción de Método
     *
     *
     * @param colSize
     */

    public void setColSize( int colSize ) {
        m_colSize = colSize;
    }    // getColumnSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDisplayType() {
        return m_displayType;
    }    // getDisplayType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadOnly() {
        return m_readOnly;
    }

    /**
     * Descripción de Método
     *
     *
     * @param readOnly
     */

    public void setReadOnly( boolean readOnly ) {
        m_readOnly = readOnly;
    }

    /**
     * Descripción de Método
     *
     *
     * @param colorColumn
     */

    public void setColorColumn( boolean colorColumn ) {
        m_colorColumn = colorColumn;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isColorColumn() {
        return m_colorColumn;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "RColumn[" );

        sb.append( m_colSQL ).append( "=" ).append( m_colHeader ).append( "]" );

        return sb.toString();
    }    // toString
}    // RColumn



/*
 *  @(#)RColumn.java   02.07.07
 * 
 *  Fin del fichero RColumn.java
 *  
 *  Versión 2.2
 *
 */
