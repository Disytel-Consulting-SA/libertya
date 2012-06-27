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



package org.openXpertya.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Evaluatee;
import org.openXpertya.util.Evaluator;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class MField implements Serializable,Evaluatee {

    /**
     * Constructor de la clase ...
     *
     *
     * @param vo
     */

    public MField( MFieldVO vo ) {

        m_vo = vo;
        // Set Attributes

        loadLookup();
        setError( false );
    }    // MField

    /** Descripción de Campos */

    private MFieldVO m_vo;

    /**
     * Descripción de Método
     *
     */

    protected void dispose() {

        // log.fine( "MField.dispose = " + m_vo.ColumnName);

        m_propertyChangeListeners = null;

        if( m_lookup != null ) {
            m_lookup.dispose();
        }

        m_lookup        = null;
        m_vo.lookupInfo = null;
        m_vo            = null;
    }    // dispose

    /** Descripción de Campos */

    private Lookup m_lookup = null;

    /** Descripción de Campos */

    private boolean m_inserting = false;

    /** Descripción de Campos */

    public static final int MAXDISPLAY_LENGTH = 60;

    /** Descripción de Campos */

    private Object m_value = null;

    /** Descripción de Campos */

    private static Object s_oldValue = new Object();

    /** Descripción de Campos */

    private Object m_oldValue = s_oldValue;

    /** Descripción de Campos */

    private boolean m_valueNoFire = true;

    /** Descripción de Campos */

    private boolean m_error = false;

    /** Descripción de Campos */

    private boolean m_parentChecked = false;

    /** Descripción de Campos */

    private PropertyChangeSupport m_propertyChangeListeners = new PropertyChangeSupport( this );

    /** Descripción de Campos */

    public static final String PROPERTY = "FieldValue";

    /** Descripción de Campos */

    public static final String INSERTING = "FieldValueInserting";

    /** Descripción de Campos */

    private String m_errorValue = null;

    /** Descripción de Campos */

    private boolean m_errorValueFlag = false;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MField.class );
    
    private boolean changed = false;

    /**
     * Descripción de Método
     *
     */

    public void loadLookup() {
        if( !isLookup()) {
            return;
        }

        log.config( "(..>" + m_vo.ColumnName + "<..)" );

        if( DisplayType.isLookup( m_vo.displayType )) {
            if( m_vo.lookupInfo == null ) {
                log.log( Level.SEVERE,"(" + m_vo.ColumnName + ") - No LookupInfo" );

                return;
            }

            // Prevent loading of CreatedBy/UpdatedBy

            if( (m_vo.displayType == DisplayType.Table) && ( m_vo.ColumnName.equals( "CreatedBy" ) || m_vo.ColumnName.equals( "UpdatedBy" ))) {
                m_vo.lookupInfo.IsCreadedUpdatedBy = true;
            }

            //

            m_vo.lookupInfo.IsKey = isKey();

            MLookup ml = new MLookup( m_vo.lookupInfo,m_vo.TabNo );

            m_lookup = ml;
        } else if( m_vo.displayType == DisplayType.Location )      // not cached
        {
            MLocationLookup ml = new MLocationLookup( m_vo.ctx,m_vo.WindowNo );

            m_lookup = ml;
        } else if( m_vo.displayType == DisplayType.Locator ) {
            MLocatorLookup ml = new MLocatorLookup( m_vo.ctx,m_vo.WindowNo );

            m_lookup = ml;
        } else if( m_vo.displayType == DisplayType.Account )       // not cached
        {
            MAccountLookup ma = new MAccountLookup( m_vo.ctx,m_vo.WindowNo );

            m_lookup = ma;
        } else if( m_vo.displayType == DisplayType.PAttribute )    // not cached
        {
            MPAttributeLookup pa = new MPAttributeLookup( m_vo.ctx,m_vo.WindowNo );

            m_lookup = pa;
        }
    }    // m_lookup

    /**
     * Descripción de Método
     *
     */

    public void lookupLoadComplete() {
        if( m_lookup == null ) {
            return;
        }

        m_lookup.loadComplete();
    }    // loadCompete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Lookup getLookup() {
        return m_lookup;
    }    // getLookup

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLookup() {
        boolean retValue = false;

        if( m_vo.IsKey ) {
            retValue = false;

            // else if (m_vo.ColumnName.equals("CreatedBy") || m_vo.ColumnName.equals("UpdatedBy"))
            // retValue = false;

        } else if( DisplayType.isLookup( m_vo.displayType )) {
            retValue = true;
        } else if( (m_vo.displayType == DisplayType.Location) || (m_vo.displayType == DisplayType.Locator) || (m_vo.displayType == DisplayType.Account) || (m_vo.displayType == DisplayType.PAttribute) ) {
            retValue = true;
        }

        return retValue;
    }    // isLookup

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean refreshLookup() {

        // if there is a validation string, the lookup is unstable

        if( (m_lookup == null) || (m_lookup.getValidation().length() == 0) ) {
            return true;
        }

        //

        log.fine( "MField.refreshLookup (" + m_vo.ColumnName + ")" );
        m_lookup.refresh();

        return m_lookup.isValidated();
    }    // refreshLookup

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getDependentOn() {
        ArrayList list = new ArrayList();

        // Display

        parseDepends( list,m_vo.DisplayLogic );
        parseDepends( list,m_vo.ReadOnlyLogic );

        // Lookup

        if( m_lookup != null ) {
            parseDepends( list,m_lookup.getValidation());
        }

        //

        if( (list.size() > 0) && CLogMgt.isLevelFiner()) {
            StringBuffer sb = new StringBuffer();

            for( int i = 0;i < list.size();i++ ) {
                sb.append( list.get( i )).append( " " );
            }

            log.finer( "(" + m_vo.ColumnName + ") " + sb.toString());
        }

        return list;
    }    // getDependentOn

    /**
     * Descripción de Método
     *
     *
     * @param list
     * @param parseString
     */

    private static void parseDepends( ArrayList list,String parseString ) {
        if( (parseString == null) || (parseString.length() == 0) ) {
            return;
        }

        // log.fine( "MField.parseDepends", parseString);

        String s = parseString;

        // while we have variables

        while( s.indexOf( "@" ) != -1 ) {
            int pos = s.indexOf( "@" );

            s   = s.substring( pos + 1 );
            pos = s.indexOf( "@" );

            if( pos == -1 ) {
                continue;    // error number of @@ not correct
            }

            String variable = s.substring( 0,pos );

            s = s.substring( pos + 1 );

            // log.fine( variable);

            list.add( variable );
        }
    }    // parseDepends

    protected static List<String> getDependensFieldColumNames(String parseString) {
    	ArrayList<String> list = new ArrayList();
    	parseDepends(list, parseString);
    	return list;
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param error
     */

    public void setError( boolean error ) {
        m_error = error;
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isError() {
        return m_error;
    }    // isError

    /**
     * Descripción de Método
     *
     *
     * @param checkContext
     *
     * @return
     */

    public boolean isMandatory( boolean checkContext ) {

        // Not mandatory

        if( !m_vo.IsMandatory || isVirtualColumn()) {
            return false;
        }

        // Numeric Keys and Created/Updated as well as
        // DocumentNo/Value/ASI ars not mandatory (persistency layer manages them)

        if(( m_vo.IsKey && m_vo.ColumnName.endsWith( "_ID" )) || m_vo.ColumnName.startsWith( "Created" ) || m_vo.ColumnName.startsWith( "Updated" ) || m_vo.ColumnName.equals( "Value" ) || m_vo.ColumnName.equals( "DocumentNo" ) || m_vo.ColumnName.equals( "M_AttributeSetInstance_ID" )) {    // 0 is valid
            return false;
        }

        // Mandatory if displayed

        return isDisplayed( checkContext );
    }    // isMandatory

    /**
     * Descripción de Método
     *
     *
     * @param checkContext
     *
     * @return
     */

    public boolean isEditable( boolean checkContext ) {
        if( isVirtualColumn()) {
            return false;
        }

        // Fields always enabled (are usually not updateable)

        if( m_vo.ColumnName.equals( "Posted" ) || ( m_vo.ColumnName.equals( "Record_ID" ) && (m_vo.displayType == DisplayType.Button) ) ) {    // Zoom
            return true;
        }

        // Fields always updareable

        if( m_vo.IsAlwaysUpdateable ) {    // Zoom
            return true;
        }

        // Tab or field is R/O

        if( m_vo.tabReadOnly || m_vo.IsReadOnly ) {
            log.finest( m_vo.ColumnName + " NO - TabRO=" + m_vo.tabReadOnly + ", FieldRO=" + m_vo.IsReadOnly );

            return false;
        }

        // Not Updateable - only editable if new updateable row

        if( !m_vo.IsUpdateable &&!m_inserting ) {
            log.finest( m_vo.ColumnName + " NO - FieldUpdateable=" + m_vo.IsUpdateable );

            return false;
        }

        // Field is the Link Column of the tab

        if( m_vo.ColumnName.equals( Env.getContext( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,"LinkColumnName" ))) {
            log.finest( m_vo.ColumnName + " NO - LinkColumn" );

            return false;
        }

        // Role Access & Column Access

        if( checkContext ) {
            int AD_Client_ID = Env.getContextAsInt( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,"AD_Client_ID" );
            int AD_Org_ID = Env.getContextAsInt( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,"AD_Org_ID" );
            int AD_Table_ID = Env.getContextAsInt( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,"AD_Table_ID" );

            if( !MRole.getDefault( m_vo.ctx,false ).canUpdate( AD_Client_ID,AD_Org_ID,AD_Table_ID,false )) {
                return false;
            }

            if( !MRole.getDefault( m_vo.ctx,false ).isColumnAccess( AD_Table_ID,m_vo.AD_Column_ID,false )) {
                return false;
            }
        }

        // Do we have a readonly rule

        if( checkContext && (m_vo.ReadOnlyLogic.length() > 0) ) {
            boolean retValue = !Evaluator.evaluateLogic( this,m_vo.ReadOnlyLogic );

            log.finest( m_vo.ColumnName + " R/O(" + m_vo.ReadOnlyLogic + ") => R/W-" + retValue );

            if( !retValue ) {
                return false;
            }
        }

        // Always editable if Active

        if( m_vo.ColumnName.equals( "Processing" ) || m_vo.ColumnName.equals( "PaymentRule" ) || m_vo.ColumnName.equals( "DocAction" ) || m_vo.ColumnName.equals( "GenerateTo" )) {
            return true;
        }

        // Record is Processed ***

        if( checkContext && Env.getContext( m_vo.ctx,m_vo.WindowNo,"Processed" ).equals( "Y" )) {
            return false;
        }

        // IsActive field is editable, if record not processed

        if( m_vo.ColumnName.equals( "IsActive" )) {
            return true;
        }

        // Record is not Active

        if( checkContext &&!Env.getContext( m_vo.ctx,m_vo.WindowNo,"IsActive" ).equals( "Y" )) {
            return false;
        }

        // ultimately visibily decides

        return isDisplayed( checkContext );
    }    // isEditable

    /**
     * Descripción de Método
     *
     *
     * @param inserting
     */

    public void setInserting( boolean inserting ) {
        m_inserting = inserting;
    }    // setInserting

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getDefault() {

        // No defaults for these fields

        if( m_vo.IsKey || (m_vo.displayType == DisplayType.RowID) || (m_vo.displayType == DisplayType.Binary) ) {
            return null;
        }

        // Set Parent to context if not explitly set

        if( isParent() && ( (m_vo.DefaultValue == null) || (m_vo.DefaultValue.length() == 0) ) ) {
            String parent = Env.getContext( m_vo.ctx,m_vo.WindowNo,m_vo.ColumnName );

            log.fine( "[Parent] " + m_vo.ColumnName + "=" + parent );

            return createDefault( parent );
        }

        // Always Active

        if( m_vo.ColumnName.equals( "IsActive" )) {
            log.fine( "[IsActive] " + m_vo.ColumnName + "=Y" );

            return "Y";
        }

        // Set Client & Org to System, if System access

        if( X_AD_Table.ACCESSLEVEL_SystemOnly.equals( Env.getContext( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,"AccessLevel" )) && ( m_vo.ColumnName.equals( "AD_Client_ID" ) || m_vo.ColumnName.equals( "AD_Org_ID" ))) {
            log.fine( "[SystemAccess] " + m_vo.ColumnName + "=0" );

            return new Integer( 0 );
        }

        // Set Org to System, if Client access

        else if( X_AD_Table.ACCESSLEVEL_SystemPlusClient.equals( Env.getContext( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,"AccessLevel" )) && m_vo.ColumnName.equals( "AD_Org_ID" )) {
            log.fine( "[ClientAccess] " + m_vo.ColumnName + "=0" );

            return new Integer( 0 );
        }

        String defStr = "";

        // Window Preferences
        
        defStr = Env.getPreference( m_vo.ctx,m_vo.AD_Window_ID,m_vo.ColumnName,false );

        if( !defStr.equals( "" )) {
            log.fine( "[UserPreference] " + m_vo.ColumnName + "=" + defStr );

            return createDefault( defStr );
        }

        // Column default value
        
        if( m_vo.DefaultValue.startsWith( "@SQL=" )) {
            String sql = m_vo.DefaultValue.substring( 5 );                      // w/o tag

            sql = Env.parseContext( m_vo.ctx,m_vo.WindowNo,sql,false,true );    // replace variables

            if( sql.equals( "" )) {
                log.log( Level.SEVERE,"(" + m_vo.ColumnName + ") - Default SQL variable parse failed: " + m_vo.DefaultValue );
            } else {
                try {
                    PreparedStatement stmt = DB.prepareStatement( sql );
                    ResultSet         rs   = stmt.executeQuery();

                    if( rs.next()) {
                        defStr = rs.getString( 1 );
                    } else {
                        log.log( Level.SEVERE,"(" + m_vo.ColumnName + ") - no Result: " + sql );
                    }

                    rs.close();
                    stmt.close();
                } catch( SQLException e ) {
                    log.log( Level.SEVERE,"(" + m_vo.ColumnName + ") " + sql,e );
                }
            }

            if( !defStr.equals( "" )) {
                log.fine( "[SQL] " + m_vo.ColumnName + "=" + defStr );

                return createDefault( defStr );
            }
        }    // SQL Statement
        
        
        if( !m_vo.DefaultValue.equals( "" ) &&!m_vo.DefaultValue.startsWith( "@SQL=" )) {
            defStr = "";

            // It is one or more variables/constants

            StringTokenizer st = new StringTokenizer( m_vo.DefaultValue,",;",false );

            while( st.hasMoreTokens()) {
                defStr = st.nextToken().trim();

                if( defStr.equals( "@SysDate@" )) {           // System Time
                    return new Timestamp( System.currentTimeMillis());
                } else if( defStr.indexOf( '@' ) != -1 ) {    // it is a variable
                    defStr = Env.getContext( m_vo.ctx,m_vo.WindowNo,defStr.replace( '@',' ' ).trim());
                } else if( defStr.indexOf( "'" ) != -1 ) {    // it is a 'String'
                    defStr = defStr.replace( '\'',' ' ).trim();
                }

                if( !defStr.equals( "" )) {
                    log.fine( "[DefaultValue] " + m_vo.ColumnName + "=" + defStr );

                    return createDefault( defStr );
                }
            }    // while more Tokens
        }        // Default value

        /*
         * Modificación por Matías Cap - Disytel
         * 
         * Cambios para que tome primero los valores por defecto del campo y despues 
         * los valores del sistema
         */
        defStr = Env.getPreference( m_vo.ctx,m_vo.AD_Window_ID,m_vo.ColumnName,true );

        if( !defStr.equals( "" )) {
            log.fine( "[SystemPreference] " + m_vo.ColumnName + "=" + defStr );

            return createDefault( defStr );
        }
        
        /*
         * Fin Modificación Matías Cap - Disytel
         */
        
        // Button to N

        if( (m_vo.displayType == DisplayType.Button) &&!m_vo.ColumnName.endsWith( "_ID" )) {
            log.fine( "[Button=N] " + m_vo.ColumnName );

            return "N";
        }

        // CheckBoxes default to No

        if( m_vo.displayType == DisplayType.YesNo ) {
            log.fine( "[YesNo=N] " + m_vo.ColumnName );

            return "N";
        }

        // lookups with one value
        // if (DisplayType.isLookup(m_vo.displayType) && m_lookup.getSize() == 1)
        // {
        // /** @todo default if only one lookup value */
        // }
        // IDs remain null

        if( m_vo.ColumnName.endsWith( "_ID" )) {
            log.fine( "[ID=null] " + m_vo.ColumnName );

            return null;
        }

        // actual Numbers default to zero

        if( DisplayType.isNumeric( m_vo.displayType )) {
            log.fine( "[Number=0] " + m_vo.ColumnName );

            return createDefault( "0" );
        }

        log.fine( "[NONE] " + m_vo.ColumnName );

        return null;
    }    // getDefault

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */

    private Object createDefault( String value ) {

        // true NULL

        if( (value == null) || (value.toString().length() == 0) ) {
            return null;
        }

        // see also MTable.readData

        try {

            // IDs & Integer & CreatedBy/UpdatedBy

            if( m_vo.ColumnName.endsWith( "atedBy" ) || m_vo.ColumnName.endsWith( "_ID" ) || (m_vo.displayType == DisplayType.Integer) ) {
                return new Integer( value );
            }

            // Number

            if( DisplayType.isNumeric( m_vo.displayType )) {
                return new BigDecimal( value );
            }

            // Timestamps

            if( DisplayType.isDate( m_vo.displayType )) {
                java.util.Date date = DisplayType.getDateFormat_JDBC().parse( value );

                return new Timestamp( date.getTime());
            }

            // Boolean

            if( m_vo.displayType == DisplayType.YesNo ) {
                return new Boolean( "Y".equals( value ));
            }

            // Default

            return value;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"MField.createDefault (" + m_vo.ColumnName + ") - " + e.getMessage());
        }

        return null;
    }    // createDefault

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean validateValue() {

        // null

        if( (m_value == null) || (m_value.toString().length() == 0) ) {
            if( isMandatory( true ))
            {
                m_error = true;
                return false;
            }    
            
                return true;
            
        }

        // Search not cached

        if( (getDisplayType() == DisplayType.Search) && (m_lookup != null) ) {

            // need to re-set invalid values - OK BPartner in PO Line - not OK SalesRep in Invoice

            if( m_lookup.getDirect( m_value,false,true ) == null ) {
                log.finest( "MField.validateValue " + m_vo.ColumnName + " Serach not valid - set to null" );
                setValue( null,m_inserting );
                m_error = true;

                return false;
            }

            return true;
        }

        // cannot be validated

        if( !isLookup() || m_lookup.containsKey( m_value )) {
            return true;
        }

        // it's not null, a lookup and does not have the key

        if( isKey() || isParent()) {    // parents/ket are not validated
            return true;
        }

        log.finest( "MField.validateValue " + m_vo.ColumnName + " - set to null" );
        setValue( null,m_inserting );
        m_error = true;

        return false;
    }    // validateValue

    /**
     * Descripción de Método
     *
     *
     * @param checkContext
     *
     * @return
     */

    public boolean isDisplayed( boolean checkContext ) {

        // ** static content **
        // not displayed

        if( !m_vo.IsDisplayed ) {
            return false;
        }

        // no restrictions

        if( m_vo.DisplayLogic.equals( "" )) {
            return true;
        }

        // ** dynamic content **

        if( checkContext ) {
            boolean retValue = Evaluator.evaluateLogic( this,m_vo.DisplayLogic );

            log.finest( m_vo.ColumnName + " (" + m_vo.DisplayLogic + ") => " + retValue );

            return retValue;
        }

        return true;
    }    // isDisplayed

    /**
     * Descripción de Método
     *
     *
     * @param variableName
     *
     * @return
     */

    public String get_ValueAsString( String variableName ) {
        return Env.getContext( m_vo.ctx,m_vo.WindowNo,variableName,true );
    }    // get_ValueAsString

    /**
     * Descripción de Método
     *
     *
     * @param list
     */

    public void addDependencies( ArrayList list ) {

        // nothing to parse

        if( !m_vo.IsDisplayed || m_vo.DisplayLogic.equals( "" )) {
            return;
        }

        StringTokenizer logic = new StringTokenizer( m_vo.DisplayLogic.trim(),"&|",false );

        while( logic.hasMoreTokens()) {
            StringTokenizer st = new StringTokenizer( logic.nextToken().trim(),"!=^",false );

            while( st.hasMoreTokens()) {
                String tag = st.nextToken().trim();         // get '@tag@'

                // Do we have a @variable@ ?

                if( tag.indexOf( '@' ) != -1 ) {
                    tag = tag.replace( '@',' ' ).trim();    // strip 'tag'

                    // Add columns (they might not be a column, but then it is static)

                    if( !list.contains( tag )) {
                        list.add( tag );
                    }
                }
            }
        }
    }    // addDependencies

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColumnName() {
        return m_vo.ColumnName;
    }    // getColumnName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColumnSQL() {
        if( (m_vo.ColumnSQL != null) && (m_vo.ColumnSQL.length() > 0) ) {
            return m_vo.ColumnSQL + " AS " + m_vo.ColumnName;
        }

        return m_vo.ColumnName;
    }    // getColumnSQL

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isVirtualColumn() {
        if( (m_vo.ColumnSQL != null) && (m_vo.ColumnSQL.length() > 0) ) {
            return true;
        }

        return false;
    }    // isColumnVirtual

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getHeader() {
        return m_vo.Header;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDisplayType() {
        return m_vo.displayType;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Window_ID() {
        return m_vo.AD_Window_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getWindowNo() {
        return m_vo.WindowNo;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Column_ID() {
        return m_vo.AD_Column_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDisplayLength() {
        return m_vo.DisplayLength;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSameLine() {
        return m_vo.IsSameLine;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDisplayed() {
        return m_vo.IsDisplayed;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
	public boolean isDisplayedInGrid()
	{
		return m_vo.IsDisplayedInGrid;
	}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplayLogic() {
        return m_vo.DisplayLogic;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDefaultValue() {
        return m_vo.DefaultValue;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadOnly() {
        if( isVirtualColumn()) {
            return true;
        }

        return m_vo.IsReadOnly;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUpdateable() {
        if( isVirtualColumn()) {
            return false;
        }

        return m_vo.IsUpdateable;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isHeading() {
        return m_vo.IsHeading;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFieldOnly() {
        return m_vo.IsFieldOnly;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEncryptedField() {
        return m_vo.IsEncryptedField;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSelectionColumn() {
        return m_vo.IsSelectionColumn;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getObscureType() {
        return m_vo.ObscureType;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSortNo() {
        return m_vo.SortNo;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getFieldLength() {
        return m_vo.FieldLength;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getVFormat() {
        return m_vo.VFormat;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getValueMin() {
        return m_vo.ValueMin;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getValueMax() {
        return m_vo.ValueMax;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getFieldGroup() {
        return m_vo.FieldGroup;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isKey() {
        return m_vo.IsKey;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isParent() {
        if( m_parentChecked || m_vo.IsParent ) {
            return m_vo.IsParent;
        }

        if( !DisplayType.isID( m_vo.displayType )) {
            m_parentChecked = true;

            return false;
        }

        String LinkColumnName = Env.getContext( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,"LinkColumnName" );

        m_vo.IsParent = m_vo.ColumnName.equals( LinkColumnName );

        if( m_vo.IsParent ) {
            log.finest( "MField.isParent - " + m_vo.IsParent + " - Link(" + LinkColumnName + ", W=" + m_vo.WindowNo + ",T=" + m_vo.TabNo + ") = " + m_vo.ColumnName );
        }

        return m_vo.IsParent;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCallout() {
        return m_vo.Callout;
    }
    
    public boolean isCalloutAlsoOnLoad() {
        return m_vo.CalloutAlsoOnLoad;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Process_ID() {
        return m_vo.AD_Process_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_vo.Description;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getHelp() {
        return m_vo.Help;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MFieldVO getVO() {
        return m_vo;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLongField() {

        // if (m_vo.displayType == DisplayType.String
        // || m_vo.displayType == DisplayType.Text
        // || m_vo.displayType == DisplayType.Memo
        // || m_vo.displayType == DisplayType.TextLong
        // || m_vo.displayType == DisplayType.Image)

        return( m_vo.DisplayLength >= MAXDISPLAY_LENGTH / 2 );

        // return false;

    }    // isLongField

    /**
     * Descripción de Método
     *
     */

    public void setValue() {

         log.fine( "MField.setValue sin parametros");

        if( m_valueNoFire ) {    // set the old value
            m_oldValue = m_value;
        }

        m_value     = null;
        m_inserting = false;
        m_error     = false;    // reset error

        // Does not fire, if same value
        log.fine("--Sin parametros-- "+"PROPERTY= "+ PROPERTY+ "m_oldValue= "+m_oldValue+" m_value= "+ m_value);
        m_propertyChangeListeners.firePropertyChange( PROPERTY,m_oldValue,m_value );

        //m_propertyChangeListeners.firePropertyChange(PROPERTY, s_oldValue, null);

    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @param newValue
     * @param inserting
     */

    public void setValue( Object newValue,boolean inserting ) {

         log.fine( "MField.setValue - " + newValue);

        if( m_valueNoFire ) {    // set the old value
            m_oldValue = m_value;
        }

        m_value     = newValue;
        m_inserting = inserting;
        m_error     = false;    // reset error
        setChanged(false);
        
        // Set Context

        if( (m_vo.displayType == DisplayType.Text) || 
        	(m_vo.displayType == DisplayType.Memo) || 
        	(m_vo.displayType == DisplayType.TextLong) ||
        	(m_vo.displayType == DisplayType.Binary) ||
        	(m_vo.displayType == DisplayType.RowID) ) {
            ;    // ignore
        } else if( newValue instanceof Boolean ) {
            Env.setContext( m_vo.ctx,m_vo.WindowNo,m_vo.ColumnName,(( Boolean )newValue ).booleanValue());
        } else if( newValue instanceof Timestamp ) {
            Env.setContext( m_vo.ctx,m_vo.WindowNo,m_vo.ColumnName,( Timestamp )m_value );
        } else {
            Env.setContext( m_vo.ctx,m_vo.WindowNo,m_vo.ColumnName,(m_value == null)
                    ?null
                    :m_value.toString());
        }

        // Does not fire, if same value
        
        log.fine("--Con parametros-- "+"PROPERTY= "+ PROPERTY+ "inserting= "+inserting + "INSERTING= "+ INSERTING+ "m_oldValue= "+m_oldValue+" m_value= "+ m_value);
      
        m_propertyChangeListeners.firePropertyChange( PROPERTY,inserting ?INSERTING :m_oldValue,m_value ); 
  
        // Agregado por Franco Bonafine - Disytel (2010-11-23)
        // Guarda en el contexto el valor de Processed e IsActive indicando el nro de pestaña
        // para saber el estado de estos campos para el registro actual de cada pestaña.
        // Actualmente se estaba guardando solo el valor para el registro actual en la pestaña
        // en la que el usuario está parado y eso hacía que la validación que se hace en
        // MTab.dataNew(...) falle ya que no chequeaba el estado del registro padre sino
        // del registro actual y no permitía por ejemplo, crear un registro en una pestaña
        // si el registro sobre el cual estábamos parados estaba desactivado o procesado.
        if (m_vo.ColumnName.equals("Processed") || m_vo.ColumnName.equals("IsActive")) {
        	String colValue = null;
        	// A veces viene como Boolean (true,false) y otras como String ("Y","N").
        	// Me aseguro de obtener siempre un "Y" "N" para guardar en el contexto.
        	if (m_value instanceof Boolean && m_value != null) {
        		colValue = (Boolean)m_value? "Y" : "N";
        	} else if (m_value != null) {
        		colValue = m_value.toString();
        	}
        	Env.setContext(m_vo.ctx, m_vo.WindowNo, m_vo.TabNo, m_vo.ColumnName, colValue);        	
        } // Fin Mod Franco Bonafine.
        
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getValue() {
        return m_value;
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValueNoFire( boolean value ) {
        m_valueNoFire = value;
    }    // setOldValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getOldValue() {
        return m_oldValue;
    }    // getOldValue

    /**
     * Descripción de Método
     *
     *
     * @param errorValue
     */

    public void setErrorValue( String errorValue ) {
        m_errorValue     = errorValue;
        m_errorValueFlag = true;
    }    // setErrorValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getErrorValue() {
        String s = m_errorValue;

        m_errorValue     = null;
        m_errorValueFlag = false;

        return s;
    }    // getErrorValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isErrorValue() {
        boolean b = m_errorValueFlag;

        m_errorValueFlag = false;

        return b;
    }    // isErrorValue

    /**
     * Descripción de Método
     *
     *
     * @param length
     */

    public void setDisplayLength( int length ) {
        m_vo.DisplayLength = length;
    }    // setDisplayLength

    // Inicio desarrollo Dataware S.L.
    /**
     * Pone el campo como solo lectura
     *
     *
     * @param displayed
     */

    public void setReadOnly( boolean readonly ) {
        m_vo.IsReadOnly = readonly;
    }    // setReadOnly
    // Fin desarrollo Dataware S.L.
    
    /**
     * Descripción de Método
     *
     *
     * @param displayed
     */

    public void setDisplayed( boolean displayed ) {
        m_vo.IsDisplayed = displayed;
    }    // setDisplayed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MField[" );

        sb.append( m_vo.ColumnName ).append( "=" ).append( m_value ).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toStringX() {
        StringBuffer sb = new StringBuffer( "MField[" );

        sb.append( m_vo.ColumnName ).append( "=" ).append( m_value ).append( ",DisplayType=" ).append( getDisplayType()).append( "]" );

        return sb.toString();
    }    // toStringX

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void removePropertyChangeListener( PropertyChangeListener l ) {
        m_propertyChangeListeners.removePropertyChangeListener( l );
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void addPropertyChangeListener( PropertyChangeListener l ) {
        m_propertyChangeListeners.addPropertyChangeListener( l );
    }

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param TabNo
     * @param AD_Tab_ID
     *
     * @return
     */

    public static MField[] createFields( Properties ctx,int WindowNo,int TabNo,int AD_Tab_ID ) {
        ArrayList         listVO       = new ArrayList();
        int               AD_Window_ID = 0;
        boolean           readOnly     = false;
        String            sql          = MFieldVO.getSQL( ctx );
        PreparedStatement pstmt        = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Tab_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MFieldVO vo = MFieldVO.create( ctx,WindowNo,TabNo,AD_Window_ID,readOnly,rs );

                listVO.add( vo );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"MField.createFields",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        MField[] retValue = new MField[ listVO.size()];

        for( int i = 0;i < listVO.size();i++ ) {
            retValue[ i ] = new MField(( MFieldVO )listVO.get( i ));
        }

        return retValue;
    }    // createFields
    
    /**
     * @return el nombre de la columna de la referencia, 
     * si no el nombre de la columna de la tabla.  
     * Las condiciones de retorno del nombre de la referencia son:
     * <ul>
     * <li>Que tenga una referencia.</li>
     * <li>Que el campo sea de tipo lookup.</li>
     * </ul> 
     */
    public String getReferenceColumnName(){
    	String columnName = getColumnName();
    	if((m_vo.AD_Reference_Value_ID != 0) &&
    			(DisplayType.isLookup(m_vo.displayType)) && 
    			(m_lookup != null)){
    		columnName = m_lookup.getColumnName();
    		if(columnName.indexOf(".") != -1){
    			columnName = columnName.substring(columnName.indexOf(".")+1);
    		}
    	}
    	return columnName;
    }

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isChanged() {
		return changed;
	}
}    // MField



/*
 *  @(#)MField.java   02.07.07
 * 
 *  Fin del fichero MField.java
 *  
 *  Versión 2.2
 *
 */
