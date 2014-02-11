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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.openXpertya.util.Trx;
import org.openXpertya.model.MField;
import org.openXpertya.model.MTable.Loader;
import org.openXpertya.model.DataStatusEvent;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DBException;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.MSort;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MTable extends AbstractTableModel implements Serializable {

	public static final String DATA_REFRESH_MESSAGE = "Refreshed";
	protected final static Integer NEW_ROW_ID = Integer.valueOf(-1);
	protected static final int DEFAULT_FETCH_SIZE = 200;
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Table_ID
     * @param TableName
     * @param WindowNo
     * @param TabNo
     * @param withAccessControl
     */

    public MTable( Properties ctx,int AD_Table_ID,String TableName,int WindowNo,int TabNo,boolean withAccessControl ) {
        super();
        log.info( TableName );
        m_ctx         = ctx;
        m_AD_Table_ID = AD_Table_ID;
        setTableName( TableName );
        m_WindowNo          = WindowNo;
        m_TabNo             = TabNo;
        m_withAccessControl = withAccessControl;
    }    // MTable

    /** Descripción de Campos */

    protected static CLogger log = CLogger.getCLogger( MTable.class.getName());

    /** Descripción de Campos */

    protected Properties m_ctx;

    /** Descripción de Campos */

    protected int m_AD_Table_ID;

    /** Descripción de Campos */

    protected String m_tableName = "";

    /** Descripción de Campos */

    protected int m_WindowNo;

    /** Descripción de Campos */

    protected int m_TabNo;

    /** Descripción de Campos */

    protected boolean m_withAccessControl;

    /** Descripción de Campos */

    protected boolean m_readOnly = true;

    /** Descripción de Campos */

    protected boolean m_deleteable = true;

    //

	//virtual table state variables
	protected boolean				m_virtual;
	public static final String CTX_KeyColumnName = "KeyColumnName";

    
    /** Descripción de Campos */

    protected int m_rowCount = 0;

    /** Descripción de Campos */

    protected boolean m_changed = false;

    /** Descripción de Campos */

    protected int m_rowChanged = -1;

    /** Descripción de Campos */

    protected boolean m_inserting = false;

    /** Descripción de Campos */

    protected int m_newRow = -1;

    /** Descripción de Campos */

    protected boolean m_open = false;

    /** Descripción de Campos */

    protected boolean m_compareDB = true;    // set to true after every save

    // The buffer for all data

    /** Descripción de Campos */

    protected volatile ArrayList m_buffer = new ArrayList( 100 );

    /** Descripción de Campos */

    protected volatile ArrayList<MSort>		m_sort = new ArrayList<MSort>(100);
    
	protected volatile Map<Integer, Object[]> m_virtualBuffer = new HashMap<Integer, Object[]>(100);

    /** Descripción de Campos */

    protected Object[] m_rowData = null;

    /** Descripción de Campos */

    protected Object[] m_oldValue = null;

    //

    /** Descripción de Campos */

    protected Loader m_loader = null;

    /** Descripción de Campos */

    protected ArrayList m_fields = new ArrayList( 30 );
    
    protected Map<String, MField> mapFields = new HashMap<String, MField>();

    /** Descripción de Campos */

    protected ArrayList m_parameterSELECT = new ArrayList( 5 );

    /** Descripción de Campos */

    protected ArrayList m_parameterWHERE = new ArrayList( 5 );

    /** Descripción de Campos */

    protected String m_SQL;

    /** Descripción de Campos */

    protected String m_SQL_Count;

    /** Descripción de Campos */

    protected String m_SQL_Select;

    /** Descripción de Campos */

    protected String m_whereClause = "";

    /** Descripción de Campos */

    protected boolean m_onlyCurrentRows = false;

    /** Descripción de Campos */

    protected int m_onlyCurrentDays = 1;

    /** Descripción de Campos */

    protected String m_orderClause = "";
    
	/** Max Rows to query or 0 for all	*/
	protected int					m_maxRows = 0;

    /** Descripción de Campos */

    protected int m_indexKeyColumn = -1;

    /** Descripción de Campos */

    protected int m_indexColorColumn = -1;

    /** Descripción de Campos */

    protected int m_indexProcessedColumn = -1;

    /** Descripción de Campos */

    protected int m_indexActiveColumn = -1;

    /** Descripción de Campos */

    protected int m_indexClientColumn = -1;

    /** Descripción de Campos */

    protected int m_indexOrgColumn = -1;

    /** Descripción de Campos */

    protected Vector m_dataStatusListeners;
   
    //Añadido por ConSerTi
    protected boolean exitProccesed=false;

    /** Descripción de Campos */

    protected VetoableChangeSupport m_vetoableChangeSupport = new VetoableChangeSupport( this );
	protected Thread m_loaderThread;

    /** Descripción de Campos */

    public static final String PROPERTY = "MTable-RowSave";

    /**
     * Descripción de Método
     *
     *
     * @param newTableName
     */

    public void setTableName( String newTableName ) {
    	log.info("En setTableName" );
        if( m_open ) {
            log.log( Level.SEVERE,"Table already open - ignored" );

            return;
        }

        if( (newTableName == null) || (newTableName.length() == 0) ) {
            return;
        }

        m_tableName = newTableName;
    }    // setTableName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTableName() {
    	log.info("En getTableName()" );
        return m_tableName;
    }    // getTableName

    /**
     * Descripción de Método
     *
     *
     * @param newWhereClause
     * @param onlyCurrentRows
     * @param onlyCurrentDays
     *
     * @return
     */

    public boolean setSelectWhereClause( String newWhereClause,boolean onlyCurrentRows,int onlyCurrentDays ) {
    	log.info("setSelectWhereClause con = "+newWhereClause );
    	
        if( m_open ) {
            log.log( Level.SEVERE,"Table already open - ignored" );

            return false;
        }

        //

        m_whereClause     = newWhereClause;
        m_onlyCurrentRows = onlyCurrentRows;
        m_onlyCurrentDays = onlyCurrentDays;

        if( m_whereClause == null ) {
            m_whereClause = "";
        }

        return true;
    }    // setWhereClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSelectWhereClause() {
        return m_whereClause;
    }    // getWhereClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOnlyCurrentRowsDisplayed() {
        return !m_onlyCurrentRows;
    }    // isHistoryDisplayed

    /**
     * Descripción de Método
     *
     *
     * @param newOrderClause
     */

    public void setOrderClause( String newOrderClause ) {
        m_orderClause = newOrderClause;

        if( m_orderClause == null ) {
            m_orderClause = "";
        }
    }    // setOrderClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getOrderClause() {
        return m_orderClause;
    }    // getOrderClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String createSelectSql() {
    	log.info(" en createSelectSql()" );
        if( (m_fields.size() == 0) || (m_tableName == null) || m_tableName.equals( "" )) {
            return "";
        }

        // Create SELECT Part

        StringBuffer select = new StringBuffer( "SELECT " );

        for( int i = 0;i < m_fields.size();i++ ) {
            if( i > 0 ) {
                select.append( "," );
            }

            MField field = ( MField )m_fields.get( i );
            if (field.getColumnSQL().equals("Processed"))
            {
            	exitProccesed=true;
            }
            log.finer("el campo es= "+field);

            select.append( field.getColumnSQL());    // ColumnName or Virtual Column
        }

        //

        select.append( " FROM " ).append( m_tableName );
        m_SQL_Select = select.toString();
        m_SQL_Count  = "SELECT COUNT(*) FROM " + m_tableName;

        //

        StringBuffer where = new StringBuffer( "" );

        // WHERE

        if( m_whereClause.length() > 0 ) {
            where.append( " WHERE " );

            if( m_whereClause.indexOf( "@" ) == -1 ) {
                where.append( m_whereClause );
            } else {    // replace variables
                where.append( Env.parseContext( m_ctx,m_WindowNo,m_whereClause,false ));
            }
        }

        if( m_onlyCurrentRows && (m_TabNo == 0) ) {
            if( where.toString().indexOf( " WHERE " ) == -1 ) {
                where.append( " WHERE " );
            } else {
                where.append( " AND " );
            }

            // Show only unprocessed or the one updated within x days
            //Modificado 
            log.finer(m_tableName);
            
            /*if (m_tableName.equals("M_Product_Gamas"))
            {
            	log.finer("soy igual a m_product_gamas");
            	where.append( "(Updated<" );
            }	
            else
            {*/
            //Si en esa table hay columna Processed
            if (exitProccesed)
            	where.append( "(Processed='N' OR Updated>" );//Antes updated<
            else
            	where.append( "(Updated>" );
            	
            if( DB.isSybase()) {
                where.append( "dateadd(dd,-1,getdate())" );
            } else {
            	//Modificado por ConSerTi, dado que estamos en postgres la funcion de conversion cambia el "SysDate" a "CURRENT_TIMESTAMP",
            	//pero la sentencia "CURRENT_TIMESTAMP-1" es erronea "operator does not exist: timestamp with time zone - integer",
            	//con lo que hay que hacer un cast.
            	//Sentencia Original "where.append("SysDate-1")"
            	//Salen ahora los registros de una semana respecto a la fecha actual
                where.append( "(current_timestamp-cast(cast(7 as text)|| 'days' as interval))" );
                
                //Fin de la modificacion
            }

            where.append( ")" );
        }

        // RO/RW Access

        m_SQL       = m_SQL_Select + where.toString();
        m_SQL_Count += where.toString();

        if( m_withAccessControl ) {
            boolean ro = MRole.SQL_RO;

            // if (!m_readOnly)
            // ro = MRole.SQL_RW;

            m_SQL = MRole.getDefault( m_ctx,false ).addAccessSQL( m_SQL,m_tableName,MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO );
            m_SQL_Count = MRole.getDefault( m_ctx,false ).addAccessSQL( m_SQL_Count,m_tableName,MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO );
        }

        // ORDER BY

        if( !m_orderClause.equals( "" )) {
            m_SQL += " ORDER BY " + m_orderClause;
        }

        //

        log.fine( m_SQL_Count );
        Env.setContext( m_ctx,m_WindowNo,m_TabNo,"SQL",m_SQL );

        return m_SQL;
    }    // createSelectSql

    /**
     * Descripción de Método
     *
     *
     * @param field
     */

    public void addField( MField field ) {
        log.fine( "(" + m_tableName + ") - " + field.getColumnName());

        if( m_open ) {
            log.log( Level.SEVERE,"Table already open - ignored: " + field.getColumnName());

            return;
        }

        if( !MRole.getDefault( m_ctx,false ).isColumnAccess( m_AD_Table_ID,field.getAD_Column_ID(),true )) {
            log.fine( "No Column Access " + field.getColumnName());

            return;
        }

        // Set Index for Key column

        if( field.isKey()) {
            m_indexKeyColumn = m_fields.size();
        } else if( field.getColumnName().equals( "IsActive" )) {
            m_indexActiveColumn = m_fields.size();
        } else if( field.getColumnName().equals( "Processed" )) {
            m_indexProcessedColumn = m_fields.size();
        } else if( field.getColumnName().equals( "AD_Client_ID" )) {
            m_indexClientColumn = m_fields.size();
        } else if( field.getColumnName().equals( "AD_Org_ID" )) {
            m_indexOrgColumn = m_fields.size();
        }

        //

        m_fields.add( field );
        getMapFields().put(field.getColumnName(), field);
    }    // addColumn

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public String getColumnName( int index ) {
        if( (index < 0) || (index > m_fields.size())) {
            log.log( Level.SEVERE,"Invalid index=" + index );

            return "";
        }

        //

        MField field = ( MField )m_fields.get( index );

        return field.getColumnName();
    }    // getColumnName

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public int findColumn( String columnName ) {
        for( int i = 0;i < m_fields.size();i++ ) {
            MField field = ( MField )m_fields.get( i );

            if( columnName.equals( field.getColumnName())) {
                return i;
            }
        }

        return -1;
    }    // findColumn

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public Class getColumnClass( int index ) {
        if( (index < 0) || (index >= m_fields.size())) {
            log.log( Level.SEVERE,"Invalid index=" + index );

            return null;
        }

        MField field = ( MField )m_fields.get( index );

        return DisplayType.getClass( field.getDisplayType(),false );
    }    // getColumnClass

    /**
     * Descripción de Método
     *
     *
     * @param index
     * @param parameter
     */

    public void setParameterSELECT( int index,Object parameter ) {
        if( index >= m_parameterSELECT.size()) {
            m_parameterSELECT.add( parameter );
        } else {
            m_parameterSELECT.set( index,parameter );
        }
    }    // setParameterSELECT

    /**
     * Descripción de Método
     *
     *
     * @param index
     * @param parameter
     */

    public void setParameterWHERE( int index,Object parameter ) {
        if( index >= m_parameterWHERE.size()) {
            m_parameterWHERE.add( parameter );
        } else {
            m_parameterWHERE.set( index,parameter );
        }
    }    // setParameterWHERE

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    protected MField getField( int index ) {
        if( (index < 0) || (index >= m_fields.size())) {
            return null;
        }

        return( MField )m_fields.get( index );
    }    // getColumn

    /**
     * Descripción de Método
     *
     *
     * @param identifier
     *
     * @return
     */

    protected MField getField( String identifier ) {
        if( (identifier == null) || (identifier.length() == 0) ) {
            return null;
        }

        int cols = m_fields.size();

        for( int i = 0;i < cols;i++ ) {
            MField field = ( MField )m_fields.get( i );

            if( identifier.equalsIgnoreCase( field.getColumnName())) {
                return field;
            }
        }

        // log.log(Level.SEVERE, "getField - not found: '" + identifier + "'");

        return null;
    }    // getField

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MField[] getFields() {
        MField[] retValue = new MField[ m_fields.size()];

        m_fields.toArray( retValue );

        return retValue;
    }    // getField

	/**************************************************************************
	 *	Open Database.
	 *  if already opened, data is refreshed
	 *	@param maxRows maximum number of rows or 0 for all
	 *	@return true if success
	 */
	public boolean open (int maxRows)
	{
		log.info("MaxRows=" + maxRows);
		m_maxRows = maxRows;
		if (m_open)
		{
			log.fine("already open");
			dataRefreshAll();
			return true;
		}

		if (m_virtual)
		{
			verifyVirtual();
		}

		//	create m_SQL and m_countSQL
		createSelectSql();
		if (m_SQL == null || m_SQL.equals(""))
		{
			log.log(Level.SEVERE, "No SQL");
			return false;
		}

		//	Start Loading
		m_loader = new Loader();
		m_rowCount = m_loader.open(maxRows);
		if (m_virtual)
		{
			m_buffer = null;
			m_virtualBuffer = new HashMap<Integer, Object[]>(210);
		}
		else
		{
			m_buffer = new ArrayList<Object[]>(m_rowCount+10);
		}
		m_sort = new ArrayList<MSort>(m_rowCount+10);
		if (m_rowCount > 0)
		{
			if (m_rowCount < 1000)
				m_loader.run();
			else
			{
				m_loaderThread = new Thread(m_loader, "TLoader");
				m_loaderThread.start();
			}
		}
		else
			m_loader.close();
		m_open = true;
		//
		m_changed = false;
		m_rowChanged = -1;
		m_inserting = false;
		return true;
	}	//	open
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean open() {
        log.info( "En MTable.Open()" );

        if( m_open ) {
            log.fine( "already open" );
            dataRefreshAll();

            return true;
        }

        // create m_SQL and m_countSQL

        createSelectSql();

        if( (m_SQL == null) || m_SQL.equals( "" )) {
            log.log( Level.SEVERE,"No SQL" );

            return false;
        }

        // Start Loading

        m_loader   = new Loader();
        m_rowCount = m_loader.open();
        m_buffer   = new ArrayList( m_rowCount + 10 );
        m_sort     = new ArrayList( m_rowCount + 10 );

        if( m_rowCount > 0 ) {
            m_loader.start();
        } else {
            m_loader.close();
        }

        m_open = true;

        //

        m_changed    = false;
        m_rowChanged = -1;

        return true;
    }    // open

	protected void verifyVirtual()
	{
		if (m_indexKeyColumn == -1)
		{
			m_virtual = false;
			return;
		}
		MField[] fields = getFields();
		for(int i = 0; i < fields.length; i++)
		{
			if (fields[i].isKey() && i != m_indexKeyColumn)
			{
				m_virtual = false;
				return;
			}
		}
	}

    
    /**
     * Descripción de Método
     *
     */

    public void loadComplete() {

        // Wait for loader

        if( m_loader != null ) {
            if( m_loader.isAlive()) {
                try {
                    m_loader.join();
                } catch( InterruptedException ie ) {
                    log.log( Level.SEVERE,"Join interrupted",ie );
                }
            }
        }

        // wait for field lookup loaders

        for( int i = 0;i < m_fields.size();i++ ) {
            MField field = ( MField )m_fields.get( i );

            field.lookupLoadComplete();
        }
    }    // loadComplete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLoading() {
        if( (m_loader != null) && m_loader.isAlive()) {
            return true;
        }

        return false;
    }    // isLoading

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOpen() {
        return m_open;
    }    // isOpen

    /**
     * Descripción de Método
     *
     *
     * @param finalCall
     */

    public void close( boolean finalCall ) {
        if( !m_open ) {
            return;
        }

        log.fine( "final=" + finalCall );

        // remove listeners

        if( finalCall ) {
            m_dataStatusListeners.clear();

            TableModelListener evl[] = ( TableModelListener[] )listenerList.getListeners( TableModelListener.class );

            for( int i = 0;i < evl.length;i++ ) {
                listenerList.remove( TableModelListener.class,evl[ i ] );
            }

            VetoableChangeListener vcl[] = m_vetoableChangeSupport.getVetoableChangeListeners();

            for( int i = 0;i < vcl.length;i++ ) {
                m_vetoableChangeSupport.removeVetoableChangeListener( vcl[ i ] );
            }
        }

        // Stop loader

        while( (m_loader != null) && m_loader.isAlive()) {
            log.log(Level.SEVERE, "Interrupting Loader ..." );
            m_loader.interrupt();

            try {
                Thread.sleep( 200 );    // .2 second
            } catch( InterruptedException ie ) {
            }
        }

        if( !m_inserting ) {
            dataSave( false );    // not manual
        }

        if( m_buffer != null ) {
            m_buffer.clear();
        }

        m_buffer = null;

        if( m_sort != null ) {
            m_sort.clear();
        }

        m_sort = null;

        if( finalCall ) {
            dispose();
        }

        // Fields are disposed from MTab

        log.fine( "" );
        m_open = false;
    }    // close

    /**
     * Descripción de Método
     *
     */

    protected void dispose() {

        // MFields

        for( int i = 0;i < m_fields.size();i++ ) {
            (( MField )m_fields.get( i )).dispose();
        }

        m_fields.clear();
        m_fields = null;

        //

        m_dataStatusListeners   = null;
        m_vetoableChangeSupport = null;

        //

        m_parameterSELECT.clear();
        m_parameterSELECT = null;
        m_parameterWHERE.clear();
        m_parameterWHERE = null;

        // clear data arrays

        m_buffer   = null;
        m_sort     = null;
        m_rowData  = null;
        m_oldValue = null;
        m_loader   = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getColumnCount() {
        return m_fields.size();
    }    // getColumnCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getFieldCount() {
        return m_fields.size();
    }    // getFieldCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRowCount() {
        return m_rowCount;
    }    // getRowCount

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     */

    public void setColorColumn( String columnName ) {
        m_indexColorColumn = findColumn( columnName );
    }    // setColorColumn

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public int getColorCode( int row ) {
        if( m_indexColorColumn == -1 ) {
            return 0;
        }

        Object data = getValueAt( row,m_indexColorColumn );

        // We need to have a Number

        if( (data == null) ||!( data instanceof BigDecimal )) {
            return 0;
        }

        BigDecimal bd = ( BigDecimal )data;

        return bd.signum();
    }    // getColorCode

    /**
     * Descripción de Método
     *
     *
     * @param col
     * @param ascending
     */

    public void sort( int col,boolean ascending ) {
        log.info( "#" + col + " " + ascending );

        if( getRowCount() == 0 ) {
            return;
        }

        MField field = getField( col );

        // RowIDs are not sorted

        if( field.getDisplayType() == DisplayType.RowID ) {
            return;
        }

        boolean isLookup = DisplayType.isLookup( field.getDisplayType());

        // fill MSort entities with data entity

        for( int i = 0;i < m_sort.size();i++ ) {
            MSort    sort    = ( MSort )m_sort.get( i );
            Object[] rowData = ( Object[] )m_buffer.get( sort.index );

            if( isLookup ) {
                sort.data = field.getLookup().getDisplay( rowData[ col ] );    // lookup
            } else {
                sort.data = rowData[ col ];    // data
            }
        }

        // sort it

        MSort sort = new MSort( 0,null );

        sort.setSortAsc( ascending );
        Collections.sort( m_sort,sort );

        // update UI

        fireTableDataChanged();

        // Info detected by MTab.dataStatusChanged and current row set to 0

        fireDataStatusIEvent( "Sorted" );
    }    // sort

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public int getKeyID( int row ) {

        // Log.info("MTable.getKeyID - row=" + row + ", keyColIdx=" + m_indexKeyColumn);

        if( m_indexKeyColumn != -1 ) {
            try {
                Integer ii = ( Integer )getValueAt( row,m_indexKeyColumn );

                if( ii == null ) {
                    return -1;
                }

                return ii.intValue();
            } catch( Exception e )    // Alpha Key
            {
                return -1;
            }
        }

        return -1;
    }    // getKeyID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getKeyColumnName() {
        if( m_indexKeyColumn != -1 ) {
            return getColumnName( m_indexKeyColumn );
        }

        return "";
    }    // getKeyColumnName

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param col
     *
     * @return
     */

    public Object getValueAt( int row,int col ) {

        // log.config( "MTable.getValueAt r=" + row + " c=" + col);

        if( !m_open || (row < 0) || (col < 0) || (row >= m_rowCount) ) {

            // log.fine( "Out of bounds - Open=" + m_open + ", RowCount=" + m_rowCount);

            return null;
        }

        // need to wait for data read into buffer

        int loops = 0;

        while( (row >= m_buffer.size()) && m_loader.isAlive() && (loops < 15) ) {
            log.fine( "Waiting for loader row=" + row + ", size=" + m_buffer.size());

            try {
                Thread.sleep( 500 );    // 1/2 second
            } catch( InterruptedException ie ) {
            }

            loops++;
        }

        // empty buffer

        if( row >= m_buffer.size()) {

            // log.fine( "Empty buffer");

            return null;
        }

        // return Data item

        MSort    sort    = ( MSort )m_sort.get( row );
        Object[] rowData = ( Object[] )m_buffer.get( sort.index );

        // out of bounds

        if( (rowData == null) || (col > rowData.length) ) {

            // log.fine( "No data or Column out of bounds");

            return null;
        }

        return rowData[ col ];
    }    // getValueAt

    /**
     * Descripción de Método
     *
     *
     * @param changed
     */

    public void setChanged( boolean changed ) {

        // Can we edit?

        if( !m_open || m_readOnly ) {
            return;
        }

        // Indicate Change

        m_changed = changed;

        if( !changed ) {
            m_rowChanged = -1;
        }

        fireDataStatusIEvent( "" );
    }    // setChanged

    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param row
     * @param col
     */

    public final void setValueAt( Object value,int row,int col ) {
        setValueAt( value,row,col,false );
    }    // setValueAt

    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param row
     * @param col
     * @param force
     */

    public final void setValueAt( Object value,int row,int col,boolean force ) {

        // Disytel: si no hay registros, insertar uno nuevo automaticamente
    	// (este caso sirve para cuando no se presiona el boton nuevo,
    	// pero se ingresan datos en alguno de los campos
    	// Revertido: problemas con pestañas incluidas
    	//
        //if (row < 0 && !isInserting() && !m_readOnly && m_rowCount == 0)
        //{
        //	this.dataNew(-1, false);
        //	row = 0;
        //}      	
    	
        // Can we edit?

        if( !m_open || m_readOnly                   // not accessible
                || (row < 0) || (col < 0            // invalid index
                    ) || (col == 0                  // cannot change ID
                        ) || (m_rowCount == 0) )    // no rows 
                        {
            //log.finest( "r=" + row + " c=" + col + " - R/O=" + m_readOnly + ", Rows=" + m_rowCount + " - Ignored"+value.toString() );

            return;
        }
        
        dataSave( row,false );
        
        // Has anything changed?

        Object oldValue = getValueAt( row,col );

        if( !force && ( ( (oldValue == null) && (value == null) ) || ( (oldValue != null) && oldValue.equals( value )) || ( (oldValue != null) && (value != null) && oldValue.toString().equals( value.toString())))) {
            log.finest( "r=" + row + " c=" + col + " - New=" + value + "==Old=" + oldValue + " - Ignored" );

            return;
        }

        log.fine( "r=" + row + " c=" + col + " = " + value + " (" + oldValue + ")" );

        // Save old value

        m_oldValue      = new Object[ 3 ];
        m_oldValue[ 0 ] = new Integer( row );
        m_oldValue[ 1 ] = new Integer( col );
        m_oldValue[ 2 ] = oldValue;

        // Set Data item

        MSort    sort    = ( MSort )m_sort.get( row );
        Object[] rowData = ( Object[] )m_buffer.get( sort.index );

        m_rowChanged = row;

        // Selection

        if( col == 0 ) {
            rowData[ col ] = value;
            m_buffer.set( sort.index,rowData );

            return;
        }

        // save original value - shallow copy

        if( m_rowData == null ) {
            int size = m_fields.size();

            m_rowData = new Object[ size ];

            for( int i = 0;i < size;i++ ) {
                m_rowData[ i ] = rowData[ i ];
            }
        }

        // save & update

        rowData[ col ] = value;
        m_buffer.set( sort.index,rowData );

        // update Table

        fireTableCellUpdated( row,col );

        // update MField

        MField field = getField( col );

        field.setValue( value,m_inserting );

        // inform

        DataStatusEvent evt = createDSE();

        field.setChanged(evt.isChanged());
        
        evt.setChangedColumn( col );
        fireDataStatusChanged( evt );
        
    }    // setValueAt

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param col
     *
     * @return
     */

    public Object getOldValue( int row,int col ) {
        if( m_oldValue == null ) {
            return null;
        }

        if((( Integer )m_oldValue[ 0 ] ).intValue() == row && (( Integer )m_oldValue[ 1 ] ).intValue() == col ) {
            return m_oldValue[ 2 ];
        }

        return null;
    }    // getOldValue

    /**
     * Descripción de Método
     *
     *
     * @param onlyRealChange
     *
     * @return
     */

    public boolean needSave( boolean onlyRealChange ) {
        return needSave( m_rowChanged,onlyRealChange );
    }    // needSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean needSave() {
        return needSave( m_rowChanged,false );
    }    // needSave

    /**
     * Descripción de Método
     *
     *
     * @param newRow
     *
     * @return
     */

    public boolean needSave( int newRow ) {
        return needSave( newRow,false );
    }    // needSave

    /**
     * Descripción de Método
     *
     *
     * @param newRow
     * @param onlyRealChange
     *
     * @return
     */

    public boolean needSave( int newRow,boolean onlyRealChange ) {
        log.fine( "Row=" + newRow + ", Changed=" + m_rowChanged + "/" + m_changed );    // m_rowChanged set in setValueAt

        // nothing done

        if( !m_changed && (m_rowChanged == -1) ) {
            return false;
        }

        // E.g. New unchanged records

        if( m_changed && (m_rowChanged == -1) && onlyRealChange ) {
            return false;
        }

        // same row

        if( newRow == m_rowChanged ) {
            return false;
        }

        return true;
    }    // needSave

    /** Descripción de Campos */

    public static final char SAVE_OK = 'O';    // the only OK condition

    /** Descripción de Campos */

    public static final char SAVE_ERROR = 'E';

    /** Descripción de Campos */

    public static final char SAVE_ACCESS = 'A';

    /** Descripción de Campos */

    public static final char SAVE_MANDATORY = 'M';

    /** Descripción de Campos */

    public static final char SAVE_ABORT = 'U';

    /**
     * Descripción de Método
     *
     *
     * @param newRow
     * @param manualCmd
     *
     * @return
     */

    public boolean dataSave( int newRow,boolean manualCmd ) {
        log.fine( "Row=" + newRow + ", Changed=" + m_rowChanged + "/" + m_changed );    // m_rowChanged set in setValueAt

        // nothing done

        if( !m_changed && (m_rowChanged == -1) ) {
            return true;
        }

        // same row, don't save yet

        if( newRow == m_rowChanged ) {
            return true;
        }

        return( dataSave( manualCmd ) == SAVE_OK );
    }    // dataSave

    /**
     * Descripción de Método
     *
     *
     * @param manualCmd
     *
     * @return
     */

    public char dataSave( boolean manualCmd ) {

        // cannot save

        if( !m_open ) {
            log.warning( "Error - Open=" + m_open );

            return SAVE_ERROR;
        }

        // no need - not changed - row not positioned - no Value changed

        if( m_rowChanged == -1 ) {
            log.config( "NoNeed - Changed=" + m_changed + ", Row=" + m_rowChanged );

            // return SAVE_ERROR;

            if( !manualCmd ) {
                return SAVE_OK;
            }
        }

        // Value not changed

        if( m_rowData == null ) {
            log.fine( "No Changes" );

            return SAVE_ERROR;
        }

        if( m_readOnly )

        // If Processed - not editable (Find always editable)  -> ok for changing payment terms, etc.

        {
            log.warning( "IsReadOnly - ignored" );
            dataIgnore();

            return SAVE_ACCESS;
        }

        // row not positioned - no Value changed

        if( m_rowChanged == -1 ) {
            if( m_newRow != -1 ) {    // new row and nothing changed - might be OK
                m_rowChanged = m_newRow;
            } else {
                fireDataStatusEEvent( "SaveErrorNoChange","" );

                return SAVE_ERROR;
            }
        }

        // Can we change?

        int[] co           = getClientOrg( m_rowChanged );
        int   AD_Client_ID = co[ 0 ];
        int   AD_Org_ID    = co[ 1 ];

        if( !MRole.getDefault( m_ctx,false ).canUpdate( AD_Client_ID,AD_Org_ID,m_AD_Table_ID,true )) {
            fireDataStatusEEvent( CLogger.retrieveError());
            dataIgnore();

            return SAVE_ACCESS;
        }

        log.info( "Row=" + m_rowChanged );

        // inform about data save action, if not manually initiated

        try {
            if( !manualCmd ) {
                m_vetoableChangeSupport.fireVetoableChange( PROPERTY,0,m_rowChanged );
            }
        } catch( PropertyVetoException pve ) {
            log.warning( pve.getMessage());
            dataIgnore();

            return SAVE_ABORT;
        }

        // get updated row data

        MSort    sort    = ( MSort )m_sort.get( m_rowChanged );
        Object[] rowData = ( Object[] )m_buffer.get( sort.index );

        // Check Mandatory

        String missingColumns = getMandatory( rowData );

        if( missingColumns.length() != 0 ) {

            // Trace.printStack(false, false);
        	
            fireDataStatusEEvent( "FillMandatory",missingColumns + "\n" );

            return SAVE_MANDATORY;
        }

        int Record_ID = 0;

        if( !m_inserting ) {
            Record_ID = getKeyID( m_rowChanged );
        }

        try {
//            if( !m_tableName.endsWith( "_Trl" )) {    // translation tables have no model
                return dataSavePO( Record_ID );
//            }
        } catch( Exception e ) {
            if( e instanceof ClassNotFoundException ) {
                log.warning( m_tableName + " - " + e.getLocalizedMessage());
            } else {
                log.log( Level.SEVERE,"Persistency Issue - " + m_tableName,e );

                return SAVE_ERROR;
            }
        }

        /*
        log.info( "NonPO" );

        boolean error = false;

        lobReset();

        //

        String       is    = null;
        final String ERROR = "ERROR: ";
        final String INFO  = "Info: ";

        // SQL with specific where clause

        String       SQL        = m_SQL_Select;
        StringBuffer refreshSQL = new StringBuffer( SQL ).append( " WHERE " );    // to be completed when key known
        StringBuffer singleRowWHERE = new StringBuffer();
        StringBuffer multiRowWHERE  = new StringBuffer();

        // Create SQL      & RowID
        
        if( m_inserting ) {
            SQL += " WHERE 1=2";//Condicion anterior= 1=2
        } else {    // FOR UPDATE causes  -  ORA-01002 fetch out of sequence
            SQL += " WHERE " + getWhereClause( rowData );
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( SQL,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );

            ResultSet rs = pstmt.executeQuery();

            // only one row

            if( !( m_inserting || rs.next())) {
                rs.close();
                pstmt.close();
                fireDataStatusEEvent( "SaveErrorRowNotFound","" );
                dataRefresh( m_rowChanged );

                return SAVE_ERROR;
            }

            Object[] rowDataDB = null;

            // Prepare

            boolean manualUpdate = ResultSet.CONCUR_READ_ONLY == rs.getConcurrency();

            if( DB.isRemoteObjects()) {
                manualUpdate = true;
            }

            if( manualUpdate ) {
                createUpdateSqlReset();
            }

            if( m_inserting ) {
                if( manualUpdate ) {
                    log.fine( "Prepare inserting ... manual" );
                } else {
                    log.fine( "Prepare inserting ... RowSet" );
                    rs.moveToInsertRow();
                }
            } else {
                log.fine( "Prepare updating ... manual=" + manualUpdate );

                // get current Data in DB

                rowDataDB = readData( rs );
            }

            // Constants for Created/Updated(By)

            Timestamp now  = new Timestamp( System.currentTimeMillis());
            int       user = Env.getContextAsInt( m_ctx,"#AD_User_ID" );
            int       size = m_fields.size();

            for( int col = 0;col < size;col++ ) {
                MField field = ( MField )m_fields.get( col );

                if( field.isVirtualColumn()) {
                    continue;
                }

                String columnName = field.getColumnName();

                // log.fine(columnName + "= " + m_rowData[col] + " <> DB: " + rowDataDB[col] + " -> " + rowData[col]);

                // RowID

                if( field.getDisplayType() == DisplayType.RowID ) {
                    ;    // ignore

                    // New Key

                } else if( field.isKey() && m_inserting ) {
                    if( columnName.endsWith( "_ID" ) || columnName.toUpperCase().endsWith( "_ID" )) {
                        int insertID = DB.getNextID( m_ctx,m_tableName,null );    // no trx

                        if( manualUpdate ) {
                            createUpdateSql( columnName,String.valueOf( insertID ));
                        } else {
                            rs.updateInt( col + 1,insertID );    // ***
                        }

                        singleRowWHERE.append( columnName ).append( "=" ).append( insertID );

                        //

                        is = INFO + columnName + " -> " + insertID + " (Key)";
                    } else                                      // Key with String value
                    {
                        String str = rowData[ col ].toString();

                        if( manualUpdate ) {
                            createUpdateSql( columnName,DB.TO_STRING( str ));
                        } else {
                            rs.updateString( col + 1,str );     // ***
                        }

                        singleRowWHERE = new StringBuffer();    // overwrite
                        singleRowWHERE.append( columnName ).append( "=" ).append( DB.TO_STRING( str ));

                        //

                        is = INFO + columnName + " -> " + str + " (StringKey)";
                    }

                    log.fine( is );
                }    // New Key

                // New DocumentNo

                else if( columnName.equals( "DocumentNo" )) {
                    boolean newDocNo = false;
                    String  docNo    = ( String )rowData[ col ];

                    // we need to have a doc number

                    if( (docNo == null) || (docNo.length() == 0) ) {
                        newDocNo = true;

                        // Preliminary ID from CalloutSystem

                    } else if( docNo.startsWith( "<" ) && docNo.endsWith( ">" )) {
                        newDocNo = true;
                    }

                    if( newDocNo || m_inserting ) {
                        String insertDoc = null;

                        // always overwrite if insering with mandatory DocType DocNo

                        if( m_inserting ) {
                            insertDoc = DB.getDocumentNo( m_ctx,m_WindowNo,m_tableName,true,null );    // only doc type - no trx
                        }

                        log.fine( "DocumentNo entered=" + docNo + ", DocTypeInsert=" + insertDoc + ", newDocNo=" + newDocNo );

                        // can we use entered DocNo?

                        if( (insertDoc == null) || (insertDoc.length() == 0) ) {
                            if( !newDocNo && (docNo != null) && (docNo.length() > 0) ) {
                                insertDoc = docNo;
                            } else {                                                                        // get a number from DocType or Table
                                insertDoc = DB.getDocumentNo( m_ctx,m_WindowNo,m_tableName,false,null );    // no trx
                            }
                        }

                        // There might not be an automatic document no for this document

                        if( (insertDoc == null) || (insertDoc.length() == 0) ) {

                            // in case DB function did not return a value

                            if( (docNo != null) && (docNo.length() != 0) ) {
                                insertDoc = ( String )rowData[ col ];
                            } else {
                                error = true;
                                is    = ERROR + field.getColumnName() + "= " + rowData[ col ] + " NO DocumentNo";
                                log.fine( is );

                                break;
                            }
                        }

                        //

                        if( manualUpdate ) {
                            createUpdateSql( columnName,DB.TO_STRING( insertDoc ));
                        } else {
                            rs.updateString( col + 1,insertDoc );    // ***
                        }

                        //

                        is = INFO + columnName + " -> " + insertDoc + " (DocNo)";
                        log.fine( is );
                    }
                }    // New DocumentNo

                // New Value(key)

                else if( columnName.equals( "Value" ) && m_inserting ) {
                    String value = ( String )rowData[ col ];

                    // Get from Sequence, if not entered

                    if( (value == null) || (value.length() == 0) ) {
                        value = DB.getDocumentNo( m_ctx,m_WindowNo,m_tableName,false,null );

                        // No Value

                        if( (value == null) || (value.length() == 0) ) {
                            error = true;
                            is    = ERROR + field.getColumnName() + "= " + rowData[ col ] + " No Value";
                            log.fine( is );

                            break;
                        }
                    }

                    if( manualUpdate ) {
                        createUpdateSql( columnName,DB.TO_STRING( value ));
                    } else {
                        rs.updateString( col + 1,value );    // ***
                    }

                    //

                    is = INFO + columnName + " -> " + value + " (Value)";
                    log.fine( is );
                }                                                                                     // New Value(key)

                // Updated         - check database

                else if( columnName.equals( "Updated" )) {
                    if( m_compareDB &&!m_inserting &&!m_rowData[ col ].equals( rowDataDB[ col ] ))    // changed
                    {
                        error = true;
                        is    = ERROR + field.getColumnName() + "= " + m_rowData[ col ] + " != DB: " + rowDataDB[ col ];
                        log.fine( is );

                        break;
                    }

                    if( manualUpdate ) {
                        createUpdateSql( columnName,DB.TO_DATE( now,false ));
                    } else {
                        rs.updateTimestamp( col + 1,now );    // ***
                    }

                    //

                    is = INFO + "Updated/By -> " + now + " - " + user;
                    log.fine( is );
                }                                             // Updated

                // UpdatedBy       - update

                else if( columnName.equals( "UpdatedBy" )) {
                    if( manualUpdate ) {
                        createUpdateSql( columnName,String.valueOf( user ));
                    } else {
                        rs.updateInt( col + 1,user );         // ***
                    }
                }                                             // UpdatedBy

                // Created

                else if( m_inserting && columnName.equals( "Created" )) {
                    if( manualUpdate ) {
                        createUpdateSql( columnName,DB.TO_DATE( now,false ));
                    } else {
                        rs.updateTimestamp( col + 1,now );    // ***
                    }
                }                                             // Created

                // CreatedBy

                else if( m_inserting && columnName.equals( "CreatedBy" )) {
                    if( manualUpdate ) {
                        createUpdateSql( columnName,String.valueOf( user ));
                    } else {
                        rs.updateInt( col + 1,user );         // ***
                    }
                }                                             // CreatedBy

                // Nothing changed & null

                else if( (m_rowData[ col ] == null) && (rowData[ col ] == null) ) {
                    if( m_inserting ) {
                        if( manualUpdate ) {
                            createUpdateSql( columnName,"NULL" );
                        } else {
                            rs.updateNull( col + 1 );                                                                                                                                                             // ***
                        }

                        is = INFO + columnName + "= NULL";
                        log.fine( is );
                    }
                }

                // ***     Data changed ***

                else if( m_inserting || ( (m_rowData[ col ] == null) && (rowData[ col ] != null) ) || ( (m_rowData[ col ] != null) && (rowData[ col ] == null) ) ||!m_rowData[ col ].equals( rowData[ col ] ))    // changed
                {

                    // Original == DB

                    if( m_inserting ||!m_compareDB || ( (m_rowData[ col ] == null) && (rowDataDB[ col ] == null) ) || ( (m_rowData[ col ] != null) && m_rowData[ col ].equals( rowDataDB[ col ] ))) {
                        if( CLogMgt.isLevelFinest()) {
                            log.fine( columnName + "=" + rowData[ col ] + " " + ( (rowData[ col ] == null)
                                    ?""
                                    :rowData[ col ].getClass().getName()));
                        }

                        //

                        String type = "String";

                        if( rowData[ col ] == null ) {
                            if( manualUpdate ) {
                                createUpdateSql( columnName,"NULL" );
                            } else {
                                rs.updateNull( col + 1 );    // ***
                            }
                        }

                        // ID - int

                        else if( DisplayType.isID( field.getDisplayType()) || (field.getDisplayType() == DisplayType.Integer) ) {
                            int number = 0;

                            try {
                                number = Integer.parseInt( rowData[ col ].toString());

                                if( manualUpdate ) {
                                    createUpdateSql( columnName,String.valueOf( number ));
                                } else {
                                    rs.updateInt( col + 1,number );    // ***
                                }
                            } catch( Exception e )                     // could also be a String (AD_Language, AD_Message)
                            {
                                if( manualUpdate ) {
                                    createUpdateSql( columnName,DB.TO_STRING( rowData[ col ].toString()));
                                } else {
                                    rs.updateString( col + 1,rowData[ col ].toString());    // ***
                                }
                            }

                            type = "Int";
                        }

                        // Numeric - BigDecimal

                        else if( DisplayType.isNumeric( field.getDisplayType())) {
                            if( manualUpdate ) {
                                createUpdateSql( columnName,rowData[ col ].toString());
                            } else {
                                rs.updateBigDecimal( col + 1,( BigDecimal )rowData[ col ] );    // ***
                            }

                            type = "Number";
                        }

                        // Date - Timestamp

                        else if( DisplayType.isDate( field.getDisplayType())) {
                            if( manualUpdate ) {
                                createUpdateSql( columnName,DB.TO_DATE(( Timestamp )rowData[ col ],false ));
                            } else {
                                rs.updateTimestamp( col + 1,( Timestamp )rowData[ col ] );    // ***
                            }

                            type = "Date";
                        }

                        // LOB

                        else if( field.getDisplayType() == DisplayType.TextLong ) {
                            PO_LOB lob = new PO_LOB( getTableName(),columnName,null,field.getDisplayType(),rowData[ col ] );

                            lobAdd( lob );
                            type = "CLOB";
                        }

                        // Boolean

                        else if( field.getDisplayType() == DisplayType.YesNo ) {
                            String yn = null;

                            if( rowData[ col ] instanceof Boolean ) {
                                Boolean bb = ( Boolean )rowData[ col ];

                                yn = bb.booleanValue()
                                     ?"Y"
                                     :"N";
                            } else {
                                yn = "Y".equals( rowData[ col ] )
                                     ?"Y"
                                     :"N";
                            }

                            if( manualUpdate ) {
                                createUpdateSql( columnName,DB.TO_STRING( yn ));
                            } else {
                                rs.updateString( col + 1,yn );    // ***
                            }
                        }

                        // String and others

                        else {
                            if( manualUpdate ) {
                                createUpdateSql( columnName,DB.TO_STRING( rowData[ col ].toString()));
                            } else {
                                rs.updateString( col + 1,rowData[ col ].toString());    // ***
                            }
                        }

                        //

                        is = INFO + columnName + "= " + m_rowData[ col ] + " -> " + rowData[ col ] + " (" + type + ")";
                        log.fine( is );
                    }

                    // Original != DB

                    else {
                        error = true;
                        is    = ERROR + field.getColumnName() + "= " + m_rowData[ col ] + " != DB: " + rowDataDB[ col ] + " -> " + rowData[ col ];
                        log.fine( is );
                    }
                }    // Data changed

                // Single Key - retrieval sql

                if( field.isKey() &&!m_inserting ) {
                    if( rowData[ col ] == null ) {
                        throw new RuntimeException( "Key is NULL - " + columnName );
                    }

                    if( columnName.endsWith( "_ID" )) {
                        singleRowWHERE.append( columnName ).append( "=" ).append( rowData[ col ] );
                    } else {
                        singleRowWHERE = new StringBuffer();    // overwrite
                        singleRowWHERE.append( columnName ).append( "=" ).append( DB.TO_STRING( rowData[ col ].toString()));
                    }
                }

                // MultiKey Inserting - retrieval sql

                if( field.isParent()) {
                    if( rowData[ col ] == null ) {
                        throw new RuntimeException( "MultiKey Parent is NULL - " + columnName );
                    }

                    if( multiRowWHERE.length() != 0 ) {
                        multiRowWHERE.append( " AND " );
                    }

                    if( columnName.endsWith( "_ID" )) {
                        multiRowWHERE.append( columnName ).append( "=" ).append( rowData[ col ] );
                    } else {
                        multiRowWHERE.append( columnName ).append( "=" ).append( DB.TO_STRING( rowData[ col ].toString()));
                    }
                }
            }    // for every column

            if( error ) {
                if( manualUpdate ) {
                    createUpdateSqlReset();
                } else {
                    rs.cancelRowUpdates();
                }

                rs.close();
                pstmt.close();
                fireDataStatusEEvent( "SaveErrorDataChanged","" );
                dataRefresh( m_rowChanged );

                return SAVE_ERROR;
            }

            //

            String whereClause = singleRowWHERE.toString();

            if( whereClause.length() == 0 ) {
                whereClause = multiRowWHERE.toString();
            }

            if( m_inserting ) {
                log.fine( "Inserting ..." );

                if( manualUpdate ) {
                	log.fine("en manualUpdated ");
                    String sql = createUpdateSql( true,null );
                    log.fine("la sql del m_inserting "+ sql);
                    int    no  = DB.executeUpdateEx( sql,null );    // no Trx

                    if( no != 1 ) {
                        log.log( Level.SEVERE,"Insert #=" + no + " - " + sql );
                    }
                } else {
                	log.fine("en el esle de if m_inserting");
                    rs.insertRow();
                }
            } else {
                log.fine( "Updating ... " + whereClause );

                if( manualUpdate ) {
                    String sql = createUpdateSql( false,whereClause );
                    int    no  = DB.executeUpdateEx( sql,null );    // no Trx

                    if( no != 1 ) {
                        log.log( Level.SEVERE,"Update #=" + no + " - " + sql );
                    }
                } else {
                    rs.updateRow();
                }
            }

            log.fine( "Committing ..." );
            DB.commit( true,null );                                 // no Trx

            //

            lobSave( whereClause );

            // data may be updated by trigger after update

            if( m_inserting || manualUpdate ) {
                rs.close();
                pstmt.close();

                // need to re-read row to get ROWID, Key, DocumentNo

                log.fine( "Reading ... " + whereClause );
                refreshSQL.append( whereClause );
                pstmt = DB.prepareStatement( refreshSQL.toString());
                rs    = pstmt.executeQuery();

                if( rs.next()) {
                    rowDataDB = readData( rs );

                    // update buffer

                    m_buffer.set( sort.index,rowDataDB );
                    fireTableRowsUpdated( m_rowChanged,m_rowChanged );
                } else {
                    log.log( Level.SEVERE,"Inserted row not found" );
                }
            } else {
                log.fine( "Refreshing ..." );
                rs.refreshRow();    // only use
                rowDataDB = readData( rs );

                // update buffer

                m_buffer.set( sort.index,rowDataDB );
                fireTableRowsUpdated( m_rowChanged,m_rowChanged );
            }

            //

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException e ) {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }

                pstmt = null;
            } catch( Exception ex ) {
            }

            String msg = "SaveError";

            if( e.getErrorCode() == 1 )    // Unique Constraint
            {
                log.log( Level.SEVERE,"Key Not Unique",e );
                msg = "SaveErrorNotUnique";
            } else {
                log.log( Level.SEVERE,SQL,e );
            }

            fireDataStatusEEvent( msg,e.getLocalizedMessage());

            return SAVE_ERROR;
        }

        // everything ok

        m_rowData    = null;
        m_changed    = false;
        m_compareDB  = true;
        m_rowChanged = -1;
        m_newRow     = -1;
        m_inserting  = false;
        fireDataStatusIEvent( "Saved" );

        //

        log.info( "fini" );
        */
        return SAVE_OK;
    }    // dataSave

    /**
     * Descripción de Método
     *
     *
     * @param Record_ID
     *
     * @return
     *
     * @throws Exception
     */

    protected char dataSavePO( int Record_ID ) throws Exception {
        log.fine( "ID=" + Record_ID );

        //

        MSort    sort    = ( MSort )m_sort.get( m_rowChanged );
        Object[] rowData = ( Object[] )m_buffer.get( sort.index );

        //

        M_Table table = M_Table.get( m_ctx,m_AD_Table_ID );
        PO      po    = null;

        if( Record_ID != -1 ) {
            po = table.getPO( Record_ID,null );
            log.fine("po=" + po);
        } else {    // Multi - Key
            po = table.getPO( getWhereClause( rowData ),null );
        }

        // No Persistent Object

        if( po == null ) {
        	// General PO
        	if( Record_ID != -1 ) {
        		po = table.getGeneralPO(Record_ID, null);
        	}
        	else{
        		po = table.getGeneralPO(getWhereClause( rowData ), null);
        	}
        	// En caso que no exista
        	if(po == null){
        		throw new ClassNotFoundException( "No Persistent Object" );
        	}
        }

        int size = m_fields.size();

        for( int col = 0;col < size;col++ ) {
            MField field = ( MField )m_fields.get( col );

            if( field.isVirtualColumn()) {
                continue;
            }

            String columnName = field.getColumnName();
            Object value      = rowData[ col ];
            Object oldValue   = m_rowData[ col ];

            // RowID

            if( field.getDisplayType() == DisplayType.RowID ) {
                ;                                                                                                                                            // ignore

                // Nothing changed & null

            } else if( (oldValue == null) && (value == null) ) {
                ;                                                                                                                                            // ignore

                // ***     Data changed ***

            } else if( m_inserting || ( (oldValue == null) && (value != null) ) || ( (oldValue != null) && (value == null) ) ||!oldValue.equals( value ))    // changed
            {

                // Check existence

                int poIndex = po.get_ColumnIndex( columnName );

                if( poIndex < 0 ) {

                    // Custom Fields not in PO

                    po.set_CustomColumn( columnName,value );

                    // log.log(Level.SEVERE, "Column not found: " + columnName);

                    continue;
                }

                Object dbValue = po.get_Value( poIndex );
                
                if( m_inserting ||!m_compareDB

                || ( oldValue != null && field.getDisplayType() == DisplayType.Binary) //&& Arrays.equals((byte[])oldValue,(byte[])dbValue))
                // Original == DB
				//Modificado por ConSerTi, ( (oldValue != null) && oldValue.equals( dbValue ))--->oldValue.equals( dbValue )
                || ( (oldValue == null) && (dbValue == null) ) || ( (oldValue != null) && String.valueOf(oldValue).equalsIgnoreCase(String.valueOf(dbValue)))
                
                // Target == DB (changed by trigger to new value already)

                || ( (value == null) && (dbValue == null) ) || ( (value != null) && String.valueOf(value).equalsIgnoreCase( String.valueOf(dbValue) ))) {
                    po.set_ValueNoCheck( columnName,value );
                }

                // Original != DB

                else {
                    String msg = columnName + "= " + oldValue + ( (oldValue == null)
                            ?""
                            :"(" + oldValue.getClass().getName() + ")" ) + " != DB: " + dbValue + ( (dbValue == null)
                            ?""
                            :"(" + dbValue.getClass().getName() + ")" ) + " -> New: " + value + ( (value == null)
                            ?""
                            :"(" + value.getClass().getName() + ")" );

                    // CLogMgt.setLevel(Level.FINEST);
                    // po.dump();
                   
                    fireDataStatusEEvent( "SaveErrorDataChanged",msg );
                    dataRefresh( m_rowChanged );

                    return SAVE_ERROR;
                }
            }    // Data changed
        }        // for every column

		// Este nuevo flag se setea a true para denotar que estamos modificando
		// un registro desde la interfaz gráfica y no instanciado en otra parte
		// del código
        po.setFromTab(true);
        
        if( !po.save()) {
            String        msg  = "SaveError";
            String        info = "";
            ValueNamePair pp   = CLogger.retrieveError();

            if( pp != null ) {
                msg  = pp.getValue();
                info = pp.getName();
            }

            Exception ex = CLogger.retrieveException();

            if( (ex != null) && (ex instanceof SQLException) && (( SQLException )ex ).getErrorCode() == 1 ) {
                msg = "SaveErrorNotUnique";
            }
            fireDataStatusEEvent( msg,info );

            return SAVE_ERROR;
        }

        // Refresh - update buffer

        String whereClause = po.get_WhereClause( true );

        log.fine( "Reading ... " + whereClause );

        StringBuffer refreshSQL = new StringBuffer( m_SQL_Select ).append( " WHERE " ).append( whereClause );
        PreparedStatement pstmt = DB.prepareStatement( refreshSQL.toString());

        try {
            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                Object[] rowDataDB = readData( rs );

                // update buffer

                m_buffer.set( sort.index,rowDataDB );
                fireTableRowsUpdated( m_rowChanged,m_rowChanged );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException e ) {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }

                pstmt = null;
            } catch( Exception ex ) {
            }

            String msg = "SaveError";
            log.log( Level.SEVERE,refreshSQL.toString(),e );
            fireDataStatusEEvent( msg,e.getLocalizedMessage());

            return SAVE_ERROR;
        }

        // everything ok

        m_rowData    = null;
        m_changed    = false;
        m_compareDB  = true;
        m_rowChanged = -1;
        m_newRow     = -1;
        m_inserting  = false;
        fireDataStatusIEvent( "Saved" );

        //

        log.info( "fini" );

        return SAVE_OK;
    }    // dataSavePO

    /**
     * Descripción de Método
     *
     *
     * @param rowData
     *
     * @return
     */

    protected String getWhereClause( Object[] rowData ) {
    	if(rowData == null){
    		return null;
    	}
        int          size           = m_fields.size();
        StringBuffer singleRowWHERE = null;
        StringBuffer multiRowWHERE  = null;

        for( int col = 0;col < size;col++ ) {
            MField field = ( MField )m_fields.get( col );

            if( field.isKey()) {
                String columnName = field.getColumnName();
                Object value      = rowData[ col ];

                if( value == null ) {
                    log.log( Level.WARNING,"PK data is null - " + columnName );

                    return null;
                }

                if( columnName.endsWith( "_ID" )) {
                    singleRowWHERE = new StringBuffer( columnName ).append( "=" ).append( value );
                } else {
                    singleRowWHERE = new StringBuffer( columnName ).append( "=" ).append( DB.TO_STRING( value.toString()));
                }
            } else if( field.isParent()) {
                String columnName = field.getColumnName();
                Object value      = rowData[ col ];

                if( value == null ) {
                    log.log( Level.INFO,"FK data is null - " + columnName );

                    continue;
                }

                if( multiRowWHERE == null ) {
                    multiRowWHERE = new StringBuffer();
                } else {
                    multiRowWHERE.append( " AND " );
                }

                if( columnName.endsWith( "_ID" )) {
                    multiRowWHERE.append( columnName ).append( "=" ).append( value );
                } else {
                    multiRowWHERE.append( columnName ).append( "=" ).append( DB.TO_STRING( value.toString()));
                }
            }
        }    // for all columns

        if( singleRowWHERE != null ) {
            return singleRowWHERE.toString();
        }

        if( multiRowWHERE != null ) {
            return multiRowWHERE.toString();
        }

        log.log( Level.WARNING,"No key Found" );

        return null;
    }    // getWhereClause

    /** Descripción de Campos */

    protected ArrayList m_createSqlColumn = new ArrayList();

    /** Descripción de Campos */

    protected ArrayList m_createSqlValue = new ArrayList();

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     * @param value
     */

    protected void createUpdateSql( String columnName,String value ) {
        m_createSqlColumn.add( columnName );
        m_createSqlValue.add( value );
        log.finest( "#" + m_createSqlColumn.size() + " - " + columnName + "=" + value );
    }    // createUpdateSQL

    /**
     * Descripción de Método
     *
     *
     * @param insert
     * @param whereClause
     *
     * @return
     */

    protected String createUpdateSql( boolean insert,String whereClause ) {
        StringBuffer sb = new StringBuffer();
        log.info("en createUpdateSql con insert= "+insert+" y whereClause= "+ whereClause);

        if( insert ) {
            sb.append( "INSERT INTO " ).append( m_tableName ).append( " (" );

            for( int i = 0;i < m_createSqlColumn.size();i++ ) {
                if( i != 0 ) {
                    sb.append( "," );
                }

                sb.append( m_createSqlColumn.get( i ));
            }

            sb.append( ") VALUES ( " );

            for( int i = 0;i < m_createSqlValue.size();i++ ) {
                if( i != 0 ) {
                    sb.append( "," );
                }

                sb.append( m_createSqlValue.get( i ));
            }

            sb.append( ")" );
        } else {
            sb.append( "UPDATE " ).append( m_tableName ).append( " SET " );

            for( int i = 0;i < m_createSqlColumn.size();i++ ) {
                if( i != 0 ) {
                    sb.append( "," );
                }

                sb.append( m_createSqlColumn.get( i )).append( "=" ).append( m_createSqlValue.get( i ));
            }

            sb.append( " WHERE " ).append( whereClause );
        }

        log.fine("sb.toString "+ sb.toString());

        // reset
        log.info("En createUpdateSql con =" + sb.toString());
        createUpdateSqlReset();
        

        return sb.toString();
    }    // createUpdateSql

    /**
     * Descripción de Método
     *
     */

    protected void createUpdateSqlReset() {
        m_createSqlColumn = new ArrayList();
        m_createSqlValue  = new ArrayList();
    }    // createUpdateSqlReset

    /**
     * Descripción de Método
     *
     *
     * @param rowData
     *
     * @return
     */

    protected String getMandatory( Object[] rowData ) {

        // see also => ProcessParameter.saveParameter

        StringBuffer sb = new StringBuffer();

        // Check all columns

        int size = m_fields.size();

        for( int i = 0;i < size;i++ ) {
            MField field = ( MField )m_fields.get( i );

			// Es obligatorio, pero si el registro actual está procesado y no es
			// siempre actualizable, entonces no se verifica obligatoriedad
			if (field.isMandatory(true)) // check context
            {
                if( (rowData[ i ] == null) || (rowData[ i ].toString().length() == 0) ) {
					if (m_indexProcessedColumn < 0
							|| (m_indexProcessedColumn >= 0 && !("Y".equals(rowData[m_indexProcessedColumn])) )
							|| (m_indexProcessedColumn >= 0
									&& ("Y".equals(rowData[m_indexProcessedColumn]))
									&& field.isAlwaysUpdateable())) {
	                    field.setInserting( true );    // set editable otherwise deadlock
	                    field.setError( true );
	
	                    if( sb.length() > 0 ) {
	                        sb.append( ", " );
	                    }
	
	                    sb.append( field.getHeader());
                	}
                } else {
                    field.setError( false );
                }
            }
        }

        if( sb.length() == 0 ) {
            return "";
        }

        return sb.toString();
    }    // getMandatory

    /** Descripción de Campos */

    protected ArrayList m_lobInfo = null;

    /**
     * Descripción de Método
     *
     */

    protected void lobReset() {
        m_lobInfo = null;
    }    // resetLOB

    /**
     * Descripción de Método
     *
     *
     * @param lob
     */

    protected void lobAdd( PO_LOB lob ) {
        log.fine( "LOB=" + lob );

        if( m_lobInfo == null ) {
            m_lobInfo = new ArrayList();
        }

        m_lobInfo.add( lob );
    }    // lobAdd

    /**
     * Descripción de Método
     *
     *
     * @param whereClause
     */

    protected void lobSave( String whereClause ) {
        if( m_lobInfo == null ) {
            return;
        }

        for( int i = 0;i < m_lobInfo.size();i++ ) {
            PO_LOB lob = ( PO_LOB )m_lobInfo.get( i );

            lob.save( whereClause );
        }    // for all LOBs

        lobReset();
    }    // lobSave

    /**
     * Descripción de Método
     *
     *
     * @param currentRow
     * @param copyCurrent
     *
     * @return
     */

    public boolean dataNew( int currentRow,boolean copyCurrent ) {
        log.info( "Current=" + currentRow + ", Copy=" + copyCurrent );

        // Read only

        if( m_readOnly ) {
            fireDataStatusEEvent( "AccessCannotInsert","" );

            return false;
        }

        // || !Access.canViewInsert(m_ctx, m_WindowNo, tableLevel, true, true))
        // fireDataStatusEvent(Log.retrieveError());

        // see if we need to save

        dataSave( -2,false );
        m_inserting = true;

        // Create default data

        int size = m_fields.size();

        m_rowData = new Object[ size ];    // "original" data

        Object[] rowData = new Object[ size ];

        // fill data

        if( copyCurrent ) {
            MSort    sort     = ( MSort )m_sort.get( currentRow );
            Object[] origData = ( Object[] )m_buffer.get( sort.index );

            for( int i = 0;i < size;i++ ) {
                MField field      = ( MField )m_fields.get( i );
                String columnName = field.getColumnName();

                if( field.isVirtualColumn()) {
                    ;
                } else if( field.isKey() || columnName.equals( "AD_Client_ID" )

                //

                || columnName.startsWith( "Created" ) || columnName.startsWith( "Updated" ) || columnName.equals( "EntityType" ) || columnName.equals( "DocumentNo" ) || columnName.equals( "Processed" ) || columnName.equals( "IsSelfService" ) || columnName.equals( "DocAction" ) || columnName.equals( "DocStatus" ) || columnName.startsWith( "Ref_" )

                // Order/Invoice

                || columnName.equals( "GrandTotal" ) || columnName.equals( "TotalLines" ) || columnName.equals( "C_CashLine_ID" ) || columnName.equals( "C_Payment_ID" ) || columnName.equals( "IsPaid" ) || columnName.equals( "IsAllocated" )) {
                    rowData[ i ] = field.getDefault();
                    field.setValue( rowData[ i ],m_inserting );
                } else {
                    rowData[ i ] = origData[ i ];
                }
            }
        } else    // new
        {
            for( int i = 0;i < size;i++ ) {
                MField field = ( MField )m_fields.get( i );

                rowData[ i ] = field.getDefault();
                field.setValue( rowData[ i ],m_inserting );
            }
        }

        m_changed    = true;
        m_compareDB  = true;
        m_rowChanged = -1;    // only changed in setValueAt
        m_newRow     = currentRow + 1;

        // if there is no record, the current row could be 0 (and not -1)

        if( m_buffer.size() < m_newRow ) {
            m_newRow = m_buffer.size();
        }

        // add Data at end of buffer

        MSort sort = new MSort( m_buffer.size(),null );    // index

        m_buffer.add( rowData );

        // add Sort pointer

        m_sort.add( m_newRow,sort );
        m_rowCount++;

        // inform

        log.fine( "Current=" + currentRow + ", New=" + m_newRow );
        fireTableRowsInserted( m_newRow,m_newRow );
        fireDataStatusIEvent( copyCurrent
                              ?"UpdateCopied"
                              :"Inserted" );
        log.fine( "Current=" + currentRow + ", New=" + m_newRow + " - complete" );

        return true;
    }    // dataNew

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public boolean dataDelete( int row ) {
        log.info( "Row=" + row );

        if( row < 0 ) {
            return false;
        }

        // Tab R/O

        if( m_readOnly ) {
            fireDataStatusEEvent( "AccessCannotDelete","" );    // previleges

            return false;
        }

        // Is this record deletable?

        if( !m_deleteable ) {
            fireDataStatusEEvent( "AccessNotDeleteable","" );    // audit

            return false;
        }

        // Processed Column and not an Import Table

        if( (m_indexProcessedColumn > 0) &&!m_tableName.startsWith( "I_" )) {
            Boolean processed = ( Boolean )getValueAt( row,m_indexProcessedColumn );

            if( (processed != null) && processed.booleanValue()) {
                fireDataStatusEEvent( "CannotDeleteTrx","" );

                return false;
            }
        }

        // fireDataStatusEvent(Log.retrieveError());

        MSort    sort    = ( MSort )m_sort.get( row );
        Object[] rowData = ( Object[] )m_buffer.get( sort.index );

        //

        M_Table table     = M_Table.get( m_ctx,m_AD_Table_ID );
        PO      po        = null;
        int     Record_ID = getKeyID( m_rowChanged );

        if( Record_ID != -1 ) {
            po = table.getPO( Record_ID,null );
        } else {    // Multi - Key
            po = table.getPO( getWhereClause( rowData ),null );
        }
        
        // General PO
        
        if(po == null){
        	if( Record_ID != -1 ) {
                po = table.getGeneralPO(Record_ID, null);
            } else {    // Multi - Key
                po = table.getGeneralPO(getWhereClause( rowData ), null);
            }
        }

        // Delete via PO

        if( po != null ) {
            boolean ok = false;

            try {
                ok = po.delete( false );
            } catch( Throwable t ) {
                log.log( Level.SEVERE,"Delete",t );
            }

            if( !ok ) {
                ValueNamePair vp = CLogger.retrieveError();

                if( vp != null ) {
                    fireDataStatusEEvent( vp );
                } else {
                    fireDataStatusEEvent( "DeleteError","" );
                }

                return false;
            }
        } else    // Delete via SQL
        {
            StringBuffer sql = new StringBuffer( "DELETE " );

            sql.append( m_tableName ).append( " WHERE " ).append( getWhereClause( rowData ));

            int no = 0;

            try {
                //PreparedStatement pstmt = DB.prepareStatement( sql.toString());
                PreparedStatement pstmt = DB.prepareStatement( sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, null);
                no = pstmt.executeUpdate();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,sql.toString(),e );

                String msg = "DeleteError";

                if( e.getErrorCode() == 2292 ) {    // Child Record Found
                    msg = "DeleteErrorDependent";
                }

                fireDataStatusEEvent( msg,e.getLocalizedMessage());

                return false;
            }

            // Check Result

            if( no != 1 ) {
                log.log( Level.SEVERE,"Number of deleted rows = " + no );

                return false;
            }
        }

        // Get Sort

        int bufferRow = sort.index;

        // Delete row in Buffer and shifts all below up

        m_buffer.remove( bufferRow );
        m_rowCount--;

        // Delete row in Sort

        m_sort.remove( row );

        // Correct pointer in Sort

        for( int i = 0;i < m_sort.size();i++ ) {
            MSort ptr = ( MSort )m_sort.get( i );

            if( ptr.index > bufferRow ) {
                ptr.index--;    // move up
            }
        }

        // inform

        m_changed    = false;
        m_rowChanged = -1;
        fireTableRowsDeleted( row,row );
        fireDataStatusIEvent( "Deleted" );
        log.fine( "Row=" + row + " complete" );

        return true;
    }    // dataDelete

    /**
     * Descripción de Método
     *
     */

    public void dataIgnore() {
        if( !m_inserting &&!m_changed && (m_rowChanged < 0) ) {
            log.fine( "Nothing to ignore" );

            return;
        }

        log.info( "Inserting=" + m_inserting );

        // Inserting - delete new row

        if( m_inserting ) {

            // Get Sort

            MSort sort      = ( MSort )m_sort.get( m_newRow );
            int   bufferRow = sort.index;

            // Delete row in Buffer and shifts all below up

            m_buffer.remove( bufferRow );
            m_rowCount--;

            // Delete row in Sort

            m_sort.remove( m_newRow );    // pintint to the last column, so no adjustment

            //

            m_changed    = false;
            m_rowData    = null;
            m_rowChanged = -1;
            m_inserting  = false;

            // inform

            fireTableRowsDeleted( m_newRow,m_newRow );
        } else {

            // update buffer

            if( m_rowData != null ) {
                MSort sort = ( MSort )m_sort.get( m_rowChanged );

                m_buffer.set( sort.index,m_rowData );
            }

            m_changed    = false;
            m_rowData    = null;
            m_rowChanged = -1;
            m_inserting  = false;

            // inform
            // fireTableRowsUpdated(m_rowChanged, m_rowChanged); >> messes up display?? (clearSelection)

        }

        m_newRow = -1;
        fireDataStatusIEvent( "Ignored" );
    }    // dataIgnore

	/**
	 *	Refresh Row - ignore changes
	 *  @param row row
	 *  @param fireStatusEvent
	 */
	public void dataRefresh (int row, boolean fireStatusEvent)
	{
		log.info("Row=" + row);

		if (row < 0 || m_sort.size() == 0 || m_inserting)
			return;

		MSort sort = (MSort)m_sort.get(row);
		Object[] rowData = getDataAtRow(row);

		//  ignore
		dataIgnore();

		//	Create SQL
		String where = getWhereClause(rowData);
		if (where == null || where.length() == 0)
			where = "1=2";
		String sql = m_SQL_Select + " WHERE " + where;
		sort = (MSort)m_sort.get(row);
		Object[] rowDataDB = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();
			//	only one row
			if (rs.next())
				rowDataDB = readData(rs);
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
			fireTableRowsUpdated(row, row);
			fireDataStatusEEvent("RefreshError", sql, true);
			return;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		//	update buffer
		setDataAtRow(row, rowDataDB);
		//	info
		m_rowData = null;
		m_changed = false;
		m_rowChanged = -1;
		m_inserting = false;
		fireTableRowsUpdated(row, row);
		if (fireStatusEvent)
			fireDataStatusIEvent(DATA_REFRESH_MESSAGE, "");
	}	//	dataRefresh

    
    /**
     * Descripción de Método
     *
     *
     * @param row
     */

    public void dataRefresh( int row ) {
        log.info( "Row=" + row );

        if( (row < 0) || (m_sort.size() == 0) || m_inserting ) {
            return;
        }

        MSort    sort    = ( MSort )m_sort.get( row );
        Object[] rowData = ( Object[] )m_buffer.get( sort.index );

        // ignore

        dataIgnore();

        // Create SQL

        String where = getWhereClause( rowData );

        if( (where == null) || (where.length() == 0) ) {
            where = "2=2"; //Modificado por ConSerTi. Condicion anterior 1=2
        }

        String sql = m_SQL_Select + " WHERE " + where;

        sort = ( MSort )m_sort.get( row );

        Object[] rowDataDB = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            // only one row

            if( rs.next()) {
                rowDataDB = readData( rs );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
            fireTableRowsUpdated( row,row );
            fireDataStatusEEvent( "RefreshError","" );

            return;
        }

        // update buffer

        m_buffer.set( sort.index,rowDataDB );

        // info

        m_rowData    = null;
        m_changed    = false;
        m_rowChanged = -1;
        m_inserting  = false;
        fireTableRowsUpdated( row,row );
        fireDataStatusIEvent( "Refreshed" );
    }    // dataRefresh

	/**
	 *	Refresh all Rows - ignore changes
	 *  @param fireStatusEvent
	 */
	public void dataRefreshAll(boolean fireStatusEvent)
	{
		log.info("");
		m_inserting = false;	//	should not happen
		dataIgnore();
		close(false);
		open(m_maxRows);
		//	Info
		m_rowData = null;
		m_changed = false;
		m_rowChanged = -1;
		m_inserting = false;
		fireTableDataChanged();
		if (fireStatusEvent)
			fireDataStatusIEvent(DATA_REFRESH_MESSAGE, "");
	}	//	dataRefreshAll

    
    /**
     * Descripción de Método
     *
     */

    public void dataRefreshAll() {
        log.info( "" );
        m_inserting = false;    // should not happen
        dataIgnore();
        close( false );
        open();

        // Info

        m_rowData    = null;
        m_changed    = false;
        m_rowChanged = -1;
        m_inserting  = false;
        fireTableDataChanged();
        fireDataStatusIEvent( "Refreshed" );
    }    // dataRefreshAll

    /**
     * Descripción de Método
     *
     *
     * @param whereClause
     * @param onlyCurrentRows
     * @param onlyCurrentDays
     *
     * @return
     */

    public boolean dataRequery( String whereClause,boolean onlyCurrentRows,int onlyCurrentDays ) {
        log.info( whereClause + "; OnlyCurrent=" + onlyCurrentRows );
        close( false );
        m_onlyCurrentDays = onlyCurrentDays;
        setSelectWhereClause( whereClause,onlyCurrentRows,m_onlyCurrentDays );
        open();

        // Info

        m_rowData    = null;
        m_changed    = false;
        m_rowChanged = -1;
        m_inserting  = false;
        fireTableDataChanged();
        fireDataStatusIEvent( "Refreshed" );

        return true;
    }    // dataRequery

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param col
     *
     * @return
     */

    public boolean isCellEditable( int row,int col ) {

        // log.fine( "MTable.isCellEditable - Row=" + row + ", Col=" + col);
        // Make Rows selectable

        if( col == 0 ) {
            return true;
        }

        // Entire Table not editable

        if( m_readOnly ) {
            return false;
        }

        // Key not editable

        if( col == m_indexKeyColumn ) {
            return false;
        }

        // Check column range

        if( (col < 0) && (col >= m_fields.size())) {
            return false;
        }

        // IsActive Column always editable if no processed exists

        if( (col == m_indexActiveColumn) && (m_indexProcessedColumn == -1) ) {
            return true;
        }

        // Row

        if( !isRowEditable( row )) {
            return false;
        }

        // Column

        return(( MField )m_fields.get( col )).isEditable( false );
    }    // IsCellEditable

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public boolean isRowEditable( int row ) {

        // log.fine( "MTable.isRowEditable - Row=" + row);
        // Entire Table not editable or no row

        if( m_readOnly || (row < 0) ) {
            return false;
        }

        // If not Active - not editable

        if( m_indexActiveColumn > 0 )    // && m_TabNo != Find.s_TabNo)
        {
            Object value = getValueAt( row,m_indexActiveColumn );

            if( value instanceof Boolean ) {
                if( !(( Boolean )value ).booleanValue()) {
                    return false;
                }
            } else if( "N".equals( value )) {
                return false;
            }
        }

        // If Processed - not editable (Find always editable)

        if( m_indexProcessedColumn > 0 )    // && m_TabNo != Find.s_TabNo)
        {
            Object processed = getValueAt( row,m_indexProcessedColumn );

            if( processed instanceof Boolean ) {
                if((( Boolean )processed ).booleanValue()) {
                    return false;
                }
            } else if( "Y".equals( processed )) {
                return false;
            }
        }

        //

        int[] co           = getClientOrg( row );
        int   AD_Client_ID = co[ 0 ];
        int   AD_Org_ID    = co[ 1 ];

        return MRole.getDefault( m_ctx,false ).canUpdate( AD_Client_ID,AD_Org_ID,m_AD_Table_ID,false );
    }    // isRowEditable

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    protected int[] getClientOrg( int row ) {
        int AD_Client_ID = -1;

        if( m_indexClientColumn != -1 ) {
            Integer ii = ( Integer )getValueAt( row,m_indexClientColumn );

            if( ii != null ) {
                AD_Client_ID = ii.intValue();
            }
        }

        int AD_Org_ID = 0;

        if( m_indexOrgColumn != -1 ) {
            Integer ii = ( Integer )getValueAt( row,m_indexOrgColumn );

            if( ii != null ) {
                AD_Org_ID = ii.intValue();
            }
        }

        return new int[]{ AD_Client_ID,AD_Org_ID };
    }    // getClientOrg

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setReadOnly( boolean value ) {
        log.fine( "ReadOnly=" + value );
        m_readOnly = value;
    }    // setReadOnly

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadOnly() {
        return m_readOnly;
    }    // isReadOnly

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInserting() {
        return m_inserting;
    }    // isInserting

    /**
     * Descripción de Método
     *
     *
     * @param compareDB
     */

    public void setCompareDB( boolean compareDB ) {
        m_compareDB = compareDB;
    }    // setCompareDB

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean getCompareDB() {
        return m_compareDB;
    }    // getCompareDB

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setDeleteable( boolean value ) {
        log.fine( "Deleteable=" + value );
        m_deleteable = value;
    }    // setDeleteable

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected Object[] readData( ResultSet rs ) {
        int      size        = m_fields.size();
        Object[] rowData     = new Object[ size ];
        String   columnName  = null;
        int      displayType = 0;

        // Types see also MField.createDefault

        try {

            // get row data

            for( int j = 0;j < size;j++ ) {

                // Column Info

                MField field = ( MField )m_fields.get( j );

                columnName  = field.getColumnName();
                displayType = field.getDisplayType();

                // Integer, ID, Lookup (UpdatedBy is a numeric column)

                if( (displayType == DisplayType.Integer) || ( DisplayType.isID( displayType ) && ( columnName.endsWith( "_ID" ) || columnName.endsWith( "_Acct" ))) || columnName.endsWith( "atedBy" )) {
                    rowData[ j ] = new Integer( rs.getInt( j + 1 ));                    // Integer

                    if( rs.wasNull()) {
                        rowData[ j ] = null;
                    }
                }

                // Number

                else if( DisplayType.isNumeric( displayType )) {
                    rowData[ j ] = rs.getBigDecimal( j + 1 );                           // BigDecimal

                    // Date

                } else if( DisplayType.isDate( displayType )) {
                    rowData[ j ] = rs.getTimestamp( j + 1 );                            // Timestamp

                    // RowID or Key (and Selection)

                } else if( displayType == DisplayType.RowID ) {
                    rowData[ j ] = null;

                    // YesNo

                } else if( displayType == DisplayType.YesNo ) {
                    rowData[ j ] = new Boolean( "Y".equals( rs.getString( j + 1 )));    // Boolean

                    // LOB

                } else if( displayType == DisplayType.TextLong ) {
                    Object value = rs.getObject( j + 1 );

                    if( rs.wasNull()) {
                        rowData[ j ] = null;
                    } else if( value instanceof Clob ) {
                        Clob lob    = ( Clob )value;
                        long length = lob.length();

                        rowData[ j ] = lob.getSubString( 1,( int )length );
                   }
                   
                    // Binary
                    
                } else if( displayType == DisplayType.Binary ) {
            		rowData[j] = rs.getBytes( j + 1);
            	
            		
                // String

                } else {
                    rowData[ j ] = rs.getString( j + 1 );    // String
                }
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,columnName + ", DT=" + displayType,e );
        }

        return rowData;
    }    // readData

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void removeDataStatusListener( DataStatusListener l ) {
        if( (m_dataStatusListeners != null) && m_dataStatusListeners.contains( l )) {
            Vector v = ( Vector )m_dataStatusListeners.clone();

            v.removeElement( l );
            m_dataStatusListeners = v;
        }
    }    // removeDataStatusListener

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void addDataStatusListener( DataStatusListener l ) {
        Vector v = (m_dataStatusListeners == null)
                   ?new Vector( 2 )
                   :( Vector )m_dataStatusListeners.clone();

        if( !v.contains( l )) {
            v.addElement( l );
            m_dataStatusListeners = v;
        }
    }    // addDataStatusListener

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    protected void fireDataStatusChanged( DataStatusEvent e ) {
        if( m_dataStatusListeners != null ) {
            Vector listeners = m_dataStatusListeners;
            int    count     = listeners.size();

            for( int i = 0;i < count;i++ ) {
                (( DataStatusListener )listeners.elementAt( i )).dataStatusChanged( e );
            }
        }
    }    // fireDataStatusChanged

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected DataStatusEvent createDSE() {
        boolean changed = m_changed;

        if( m_rowChanged != -1 ) {
            changed = true;
        }
        
        DataStatusEvent dse = new DataStatusEvent( this,m_rowCount,changed,Env.isAutoCommit( m_ctx,m_WindowNo ),m_inserting );

        dse.AD_Table_ID = m_AD_Table_ID;
        dse.Record_ID   = null;

        return dse;
    }    // createDSE

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     */

    protected void fireDataStatusIEvent( String AD_Message ) {
        DataStatusEvent e = createDSE();

        e.setInfo( AD_Message,"",false );
        fireDataStatusChanged( e );
    }    // fireDataStatusEvent

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     * @param info
     */

    protected void fireDataStatusEEvent( String AD_Message,String info ) {

        // org.openXpertya.util.Trace.printStack();
        //

        DataStatusEvent e = createDSE();

        e.setInfo( AD_Message,info,true );
        log.saveError( AD_Message,info );
        fireDataStatusChanged( e );
    }    // fireDataStatusEvent

    /**
     * Descripción de Método
     *
     *
     * @param errorLog
     */

    protected void fireDataStatusEEvent( ValueNamePair errorLog ) {
        if( errorLog != null ) {
            fireDataStatusEEvent( errorLog.getValue(),errorLog.getName());
        }
    }    // fireDataStatusEvent

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void removeVetoableChangeListener( VetoableChangeListener l ) {
        m_vetoableChangeSupport.removeVetoableChangeListener( l );
    }    // removeVetoableChangeListener

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void addVetoableChangeListener( VetoableChangeListener l ) {
        m_vetoableChangeSupport.addVetoableChangeListener( l );
    }    // addVetoableChangeListener

    /**
     * Descripción de Método
     *
     *
     * @param e
     *
     * @throws java.beans.PropertyVetoException
     */

    protected void fireVetoableChange( PropertyChangeEvent e ) throws java.beans.PropertyVetoException {
        m_vetoableChangeSupport.fireVetoableChange( e );
    }    // fireVetoableChange

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return new StringBuffer( "MTable[" ).append( m_tableName ).append( ",WindowNo=" ).append( m_WindowNo ).append( ",Tab=" ).append( m_TabNo ).append( "]" ).toString();
    }    // toString

    public void setMapFields(Map<String, MField> mapFields) {
		this.mapFields = mapFields;
	}

	public Map<String, MField> getMapFields() {
		return mapFields;
	}

	/**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    class Loader extends Thread implements Serializable {

		/**
		 *	Open ResultSet
		 *	@param maxRows maximum number of rows or 0 for all
		 *	@return number of records
		 */
		protected int open (int maxRows)
		{
		//	log.config( "MTable Loader.open");
			//	Get Number of Rows
			int rows = 0;
			PreparedStatement pstmt = null;
			ResultSet rs = null;			
			try
			{
				pstmt = DB.prepareStatement(m_SQL_Count, null);
				setParameter (pstmt, true);
				rs = pstmt.executeQuery();
				if (rs.next())
					rows = rs.getInt(1);
			}
			catch (SQLException e0)
			{
				//	Zoom Query may have invalid where clause
				if (DBException.isInvalidIdentifierError(e0))
					log.warning("Count - " + e0.getLocalizedMessage() + "\nSQL=" + m_SQL_Count);
				else
					log.log(Level.SEVERE, "Count SQL=" + m_SQL_Count, e0);
				return 0;
			}
			finally
			{
				DB.close(rs, pstmt);				
			}
			StringBuffer info = new StringBuffer("Rows=");
			info.append(rows);
			if (rows == 0)
				info.append(" - ").append(m_SQL_Count);
						
			//postgresql need trx to use cursor based resultset
			String trxName = m_virtual ? Trx.createTrxName("Loader") : null;
			trx  = trxName != null ? Trx.get(trxName, true) : null;
			//	open Statement (closed by Loader.close)
			try
			{
				m_pstmt = DB.prepareStatement(m_SQL, trxName);
				if (maxRows > 0 && rows > maxRows)
				{
					m_pstmt.setMaxRows(maxRows);
					info.append(" - MaxRows=").append(maxRows);
					rows = maxRows;
				}
				//ensure not all row is fectch into memory for virtual table
				if (m_virtual)
					m_pstmt.setFetchSize(100);
				setParameter (m_pstmt, false);
				m_rs = m_pstmt.executeQuery();
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, m_SQL, e);
				return 0;
			}
			log.fine(info.toString());
			return rows;
		}	//	open

    	
        /**
         * Constructor de la clase ...
         *
         */

        public Loader() {
            super( "TLoader" );
        }    // Loader

        /** Descripción de Campos */

        protected PreparedStatement m_pstmt = null;
        protected ResultSet m_rs = null;
        protected Trx trx = null;

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        protected int open() {

            // log.config( "MTable Loader.open");
            // Get Number of Rows

            int rows = 0;

            try {
            	//JOptionPane.showMessageDialog( null,"Ejecutando la consulta: "+m_SQL_Count,"MTable.open()", JOptionPane.INFORMATION_MESSAGE );
                PreparedStatement pstmt = DB.prepareStatement( m_SQL_Count );

                setParameter( pstmt,true );

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    rows = rs.getInt( 1 );
                }
                //JOptionPane.showMessageDialog( null,"Con el resultado de:  "+rows,"MTable.open()", JOptionPane.INFORMATION_MESSAGE ); 
                rs.close();
                pstmt.close();
            } catch( SQLException e0 ) {

                // Zoom Query may have invalid where clause
            	// JOptionPane.showMessageDialog( null,"Error Numero: "+e0.getErrorCode(),"MTable.open()", JOptionPane.INFORMATION_MESSAGE );
                if( e0.getErrorCode() == 904 ) {    // ORA-00904: "C_x_ID": invalid identifier
                    log.warning( "Count - " + e0.getLocalizedMessage() + "\nSQL=" + m_SQL_Count );
                } else {
                    log.log( Level.SEVERE,"Count SQL=" + m_SQL_Count,e0 );
                }

                return 0;
            }

            // open Statement (closed by Loader.close)

            try {
                m_pstmt = DB.prepareStatement( m_SQL );

                // m_pstmt.setFetchSize(20);

                setParameter( m_pstmt,false );
                m_rs = m_pstmt.executeQuery();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,m_SQL,e );

                return 0;
            }

            StringBuffer info = new StringBuffer( "Rows=" );

            info.append( rows );

            if( rows == 0 ) {
                info.append( " - " ).append( m_SQL_Count );
            }

            log.fine( info.toString());

            return rows;
        }    // open

        /**
         * Descripción de Método
         *
         */

        protected void close() {

            // log.config( "MTable Loader.close");

            try {
                if( m_rs != null ) {
                    m_rs.close();
                }

                if( m_pstmt != null ) {
                    m_pstmt.close();
                }
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"closeRS",e );
            }

            m_rs    = null;
            m_pstmt = null;
        }    // close

        /**
         * Descripción de Método
         *
         */

        public void run() {
            log.info( "" );

            if( m_rs == null ) {
                return;
            }

            try {
                while( m_rs.next()) {
                    if( this.isInterrupted()) {
                        log.fine( "Interrupted" );
                        close();

                        return;
                    }

                    // Get Data

                    Object[] rowData = readData( m_rs );

                    // add Data

                    MSort sort = new MSort( m_buffer.size(),null );    // index

                    m_buffer.add( rowData );
                    m_sort.add( sort );

                    // Statement all 250 rows & sleep

                    if( m_buffer.size() % 250 == 0 ) {

                        // give the other processes a chance

                        try {
                            yield();
                            sleep( 10 );                               // .01 second
                        } catch( InterruptedException ie ) {
                            log.fine( "Interrupted while sleeping" );
                            close();

                            return;
                        }

                        DataStatusEvent evt = createDSE();

                        evt.setLoading( m_buffer.size());
                        fireDataStatusChanged( evt );
                    }
                }                                                      // while(rs.next())
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"run",e );
            }

            close();
            fireDataStatusIEvent( "" );
        }    // run

        /**
         * Descripción de Método
         *
         *
         * @param pstmt
         * @param countSQL
         */

        protected void setParameter( PreparedStatement pstmt,boolean countSQL ) {
            if( (m_parameterSELECT.size() == 0) && (m_parameterWHERE.size() == 0) ) {
                return;
            }

            try {
                int pos = 1;    // position in Statement

                // Select Clause Parameters

                for( int i = 0;!countSQL && (i < m_parameterSELECT.size());i++ ) {
                    Object para = m_parameterSELECT.get( i );

                    if( para != null ) {
                        log.fine( "Select " + i + "=" + para );
                    }

                    //

                    if( para == null ) {
                        ;
                    } else if( para instanceof Integer ) {
                        Integer ii = ( Integer )para;

                        pstmt.setInt( pos++,ii.intValue());
                    } else if( para instanceof BigDecimal ) {
                        pstmt.setBigDecimal( pos++,( BigDecimal )para );
                    } else {
                        pstmt.setString( pos++,para.toString());
                    }
                }

                // Where Clause Parameters

                for( int i = 0;i < m_parameterWHERE.size();i++ ) {
                    Object para = m_parameterWHERE.get( i );

                    if( para != null ) {
                        log.fine( "Where " + i + "=" + para );
                    }

                    //

                    if( para == null ) {
                        ;
                    } else if( para instanceof Integer ) {
                        Integer ii = ( Integer )para;

                        pstmt.setInt( pos++,ii.intValue());
                    } else if( para instanceof BigDecimal ) {
                        pstmt.setBigDecimal( pos++,( BigDecimal )para );
                    } else {
                        pstmt.setString( pos++,para.toString());
                    }
                }
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"parameter",e );
            }
        }    // setParameter
    }    // Loader
    
	protected void setDataAtRow(int row, Object[] rowData) {
		MSort sort = m_sort.get(row);
		if (m_virtual)
		{
			if (sort.index != NEW_ROW_ID && !(m_virtualBuffer.containsKey(sort.index)))
			{
				fillBuffer(row, DEFAULT_FETCH_SIZE);
			}
			m_virtualBuffer.put(sort.index, rowData);
		}
		else
		{
			m_buffer.set(sort.index, rowData);
		}

	}

	protected Object[] getDataAtRow(int row)
	{
		return getDataAtRow(row, true);
	}

	protected Object[] getDataAtRow(int row, boolean fetchIfNotFound)
	{
		MSort sort = (MSort)m_sort.get(row);
		Object[] rowData = null;
		if (m_virtual)
		{
			if (sort.index != NEW_ROW_ID && !(m_virtualBuffer.containsKey(sort.index)) && fetchIfNotFound)
			{
				fillBuffer(row, DEFAULT_FETCH_SIZE);
			}
			rowData = (Object[])m_virtualBuffer.get(sort.index);
		}
		else
		{
			rowData = (Object[])m_buffer.get(sort.index);
		}
		return rowData;
	}

	
	/**
	 *  Create and fire Data Status Error Event
	 *  @param AD_Message message
	 *  @param info info
	 *  @param isError error
	 */
	protected void fireDataStatusEEvent (String AD_Message, String info, boolean isError)
	{
	//	org.compiere.util.Trace.printStack();
		//
		DataStatusEvent e = createDSE();
		e.setInfo(AD_Message, info, isError, !isError);
		if (isError)
			log.saveWarning(AD_Message, info);
		fireDataStatusChanged (e);
	}   //  fireDataStatusEvent

	protected void fillBuffer(int start, int fetchSize)
	{
		//adjust start if needed
		if (start > 0)
		{
			if (start + fetchSize >= m_sort.size())
			{
				start = start - (fetchSize - ( m_sort.size() - start ));
				if (start < 0)
					start = 0;
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(m_SQL_Select)
			.append(" WHERE ")
			.append(getKeyColumnName())
			.append(" IN (");
		Map<Integer, Integer>rowmap = new LinkedHashMap<Integer, Integer>(DEFAULT_FETCH_SIZE);
		for(int i = start; i < start+fetchSize && i < m_sort.size(); i++)
		{
			if(i > start)
				sql.append(",");
			sql.append(m_sort.get(i).index);
			rowmap.put(m_sort.get(i).index, i);
		}
		sql.append(")");

		Object[] newRow = m_virtualBuffer.get(NEW_ROW_ID);
		//cache changed row
		Object[] changedRow = m_rowChanged >= 0 ? getDataAtRow(m_rowChanged, false) : null;
		m_virtualBuffer = new HashMap<Integer, Object[]>(210);
		if (newRow != null && newRow.length > 0)
			m_virtualBuffer.put(NEW_ROW_ID, newRow);
		if (changedRow != null && changedRow.length > 0)
		{
			if (changedRow[m_indexKeyColumn] != null && (Integer)changedRow[m_indexKeyColumn] > 0)
			{
				m_virtualBuffer.put((Integer)changedRow[m_indexKeyColumn], changedRow);
			}
		}

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = DB.prepareStatement(sql.toString(), null);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				Object[] data = readData(rs);
				rowmap.remove(data[m_indexKeyColumn]);
				m_virtualBuffer.put((Integer)data[m_indexKeyColumn], data);
			}
			if (!rowmap.isEmpty())
			{
				List<Integer> toremove = new ArrayList<Integer>();
				for(Map.Entry<Integer, Integer> entry : rowmap.entrySet())
				{
					toremove.add(entry.getValue());
				}
				Collections.reverse(toremove);
				for(Integer row : toremove)
				{
					m_sort.remove(row);
				}
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		finally
		{
			DB.close(rs, stmt);
		}
	}

	/**
	 *  Create and fire Data Status Info Event
	 *  @param AD_Message message
	 *  @param info additional info
	 */
	protected void fireDataStatusIEvent (String AD_Message, String info)
	{
		DataStatusEvent e = createDSE();
		e.setInfo(AD_Message, info, false,false);
		fireDataStatusChanged (e);
	}   //  fireDataStatusEvent

	
	// verify if the current record has changed
	public boolean hasChanged(int row) {
		// not so aggressive (it can has still concurrency problems)
		// compare Updated, IsProcessed
		if (getKeyID(row) > 0) {
			int colUpdated = findColumn("Updated");
			int colProcessed = findColumn("Processed");
			
			boolean hasUpdated = (colUpdated > 0);
			boolean hasProcessed = (colProcessed > 0);
			
			String columns = null;
			if (hasUpdated && hasProcessed) {
				columns = new String("Updated, Processed");
			} else if (hasUpdated) {
				columns = new String("Updated");
			} else if (hasProcessed) {
				columns = new String("Processed");
			} else {
				// no columns updated or processed to commpare
				return false;
			}

	    	Timestamp dbUpdated = null;
	    	String dbProcessedS = null;
	    	PreparedStatement pstmt = null;
	    	ResultSet rs = null;
	    	String sql = "SELECT " + columns + " FROM " + m_tableName + " WHERE " + m_tableName + "_ID=?";
	    	try
	    	{
	    		pstmt = DB.prepareStatement(sql, null);
	    		pstmt.setInt(1, getKeyID(row));
	    		rs = pstmt.executeQuery();
	    		if (rs.next()) {
	    			int idx = 1;
	    			if (hasUpdated)
	    				dbUpdated = rs.getTimestamp(idx++);
	    			if (hasProcessed)
	    				dbProcessedS = rs.getString(idx++);
	    		}
	    		else
	    			log.info("No Value " + sql);
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
	    	
	    	if (hasUpdated) {
				Timestamp memUpdated = null;
				memUpdated = (Timestamp) getOldValue(row, colUpdated);
				if (memUpdated == null)
					memUpdated = (Timestamp) getValueAt(row, colUpdated);

				if (memUpdated != null && ! memUpdated.equals(dbUpdated))
					return true;
	    	}
	    	
	    	if (hasProcessed) {
				Boolean memProcessed = null;
				memProcessed = (Boolean) getOldValue(row, colProcessed);
				if (memProcessed == null)
					memProcessed = (Boolean) getValueAt(row, colProcessed);
		    	
				Boolean dbProcessed = Boolean.TRUE;
				if (! dbProcessedS.equals("Y"))
					dbProcessed = Boolean.FALSE;
				if (memProcessed != null && ! memProcessed.equals(dbProcessed))
					return true;
	    	}
		}

		// @TODO: configurable aggressive - compare each column with the DB
		return false;
	}

	public int getNewRow()
	{
		return m_newRow;
	}
	
}        // MTable



/*
 *  @(#)MTable.java   02.07.07
 * 
 *  Fin del fichero MTable.java
 *  
 *  Versión 2.2
 *
 */
