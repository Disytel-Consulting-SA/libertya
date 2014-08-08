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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.event.EventListenerList;

import org.openXpertya.model.DataStatusEvent;
import org.openXpertya.util.DisplayType;
import org.openXpertya.model.MField;
import org.openXpertya.model.StateChangeListener;
import org.openXpertya.model.StateChangeEvent;
import org.openXpertya.model.MWindow;
import org.openXpertya.model.MTab;
import org.openXpertya.plugin.CalloutPluginEngine;
import org.openXpertya.plugin.MPluginStatus;
import org.openXpertya.plugin.MPluginStatusCallout;
import org.openXpertya.plugin.common.PluginCalloutUtils;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Evaluatee;
import org.openXpertya.util.Evaluator;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MTab implements DataStatusListener,Evaluatee,Serializable {

	// Context property names:
	public static final String CTX_KeyColumnName = "_TabInfo_KeyColumnName";
	public static final String CTX_LinkColumnName = "_TabInfo_LinkColumnName";
	public static final String CTX_TabLevel = "_TabInfo_TabLevel";
	public static final String CTX_AccessLevel = "_TabInfo_AccessLevel";
	public static final String CTX_AD_Tab_ID = "_TabInfo_AD_Tab_ID";
	public static final String CTX_Name = "_TabInfo_Name";
	public static final String CTX_AD_Table_ID = "_TabInfo_AD_Table_ID";
	public static final String CTX_FindSQL = "_TabInfo_FindSQL";
	public static final String CTX_SQL = "_TabInfo_SQL";

	public static final String DEFAULT_STATUS_MESSAGE = "NavigateOrUpdate";
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param vo
     */
	
    public MTab( MTabVO vo, MWindow w ) {
        m_vo = vo;
        m_window = w;
        // Create MTable

        m_mTable = new MTable( m_vo.ctx,m_vo.AD_Table_ID,m_vo.TableName,m_vo.WindowNo,m_vo.TabNo,true );
        m_mTable.setReadOnly( m_vo.IsReadOnly || m_vo.IsView );
        m_mTable.setDeleteable( m_vo.IsDeleteable );

        // Load Tab
        // if (vo.TabNo == 0)

        initTab( false );

        // else
        // {
        // m_loader = new Loader();
        // m_loader.setPriority(Thread.MIN_PRIORITY);
        // m_loader.start();
        // }
        // waitLoadCompete();

    }    // M_Tab

    /** Descripción de Campos */

    protected MTabVO m_vo;

	// The window of this tab
	protected MWindow			m_window;

    
    /** Descripción de Campos */

    protected MTable m_mTable = null;

    /** Descripción de Campos */

    protected String m_keyColumnName = "";

    /** Descripción de Campos */

    protected String m_linkColumnName = "";

	protected String m_parentColumnName = "";

	
    /** Descripción de Campos */

    protected String m_extendedWhere;

    /** Descripción de Campos */

    protected HashMap m_Attachment = null;

    /** Descripción de Campos */

    protected ArrayList m_Lock = null;

    /** Descripción de Campos */

    protected int m_currentRow = -1;

    /** Descripción de Campos */

    protected PropertyChangeSupport m_propertyChangeSupport = new PropertyChangeSupport( this );

    /** Descripción de Campos */

    public static final String PROPERTY = "CurrentRow";

    /** A list of event listeners for this component.	*/
    protected EventListenerList m_listenerList = new EventListenerList();

    /** Descripción de Campos */

    protected Vector m_dataStatusListeners = null;

    /** Descripción de Campos */

    protected DataStatusEvent m_DataStatusEvent = null;

    //

    /** Descripción de Campos */

    protected MQuery m_query = new MQuery();

    /** Descripción de Campos */

    protected String m_oldQuery = "0=9";

    /** Descripción de Campos */

    protected String m_linkValue = "999999";

    /** Descripción de Campos */

    protected String[] m_OrderBys = new String[ 3 ];

    /** Descripción de Campos */

    protected ArrayList m_parents = new ArrayList( 2 );

    /** Descripción de Campos */

    protected MultiMap m_depOnField = new MultiMap();

    /** Descripción de Campos */

    protected Loader m_loader = null;

    /** Descripción de Campos */

    protected volatile boolean m_loadComplete = false;

    /** Descripción de Campos */

    protected boolean m_included = false;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());
    
    protected boolean m_parentNeedSave = false;

	protected long m_lastDataStatusEventTime;

	protected DataStatusEvent m_lastDataStatusEvent;

    protected String currentRecordWarning = null;

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    class Loader extends Thread {

        /**
         * Descripción de Método
         *
         */

        public void run() {
            initTab( true );
        }    // run
    }    // Loader


    /**
     * Descripción de Método
     *
     */

    protected void waitLoadCompete() {
        if( m_loadComplete ) {
            return;
        }

        //

        m_loader.setPriority( Thread.NORM_PRIORITY );
        log.config( "" );

        while( m_loader.isAlive()) {
            try {
                Thread.sleep( 100 );    // 1/10 sec
            } catch( Exception e ) {
                log.log( Level.SEVERE,"",e );
            }
        }

        log.config( "fini" );
    }    // waitLoadComplete

    /**
     * Descripción de Método
     *
     *
     * @param async
     *
     * @return
     */

    protected boolean initTab( boolean async ) {
        log.fine( "#" + m_vo.TabNo + " - Async=" + async + " - Where=" + m_vo.WhereClause );
        m_extendedWhere = m_vo.WhereClause;

        // Get Field Data

        if( !loadFields()) {
            m_loadComplete = true;

            return false;
        }

        // Order By

        m_mTable.setOrderClause( getOrderByClause( m_vo.onlyCurrentRows ));

        if( async ) {
            log.fine( "#" + m_vo.TabNo + " - Async=" + async + " - fini" );
        }

        m_loadComplete = true;

        return true;
    }    // initTab

    /**
     * Descripción de Método
     *
     */

    protected void dispose() {
        log.fine( "#" + m_vo.TabNo );
        m_OrderBys = null;

        //

        m_parents.clear();
        m_parents = null;

        //

        m_mTable.close( true );    // also disposes Fields
        m_mTable = null;

        //

        m_depOnField.clear();
        m_depOnField = null;

        if( m_Attachment != null ) {
            m_Attachment.clear();
        }

        m_Attachment = null;

        //

        m_vo.Fields.clear();
        m_vo.Fields = null;
        m_vo        = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean loadFields() {
        log.fine( "LoadFields#" + m_vo.TabNo );

        if( m_vo.Fields == null ) {
            return false;
        }

        // Add Fields

        for( int f = 0;f < m_vo.Fields.size();f++ ) {
            MFieldVO voF = ( MFieldVO )m_vo.Fields.get( f );

            // Add Fields to Table

            if( voF != null ) {
                MField field      = new MField( voF );
                String columnName = field.getColumnName();

                // Record Info

                if( field.isKey()) {
                    m_keyColumnName = columnName;
                }

                // Parent Column(s)

                if( field.isParent()) {
                    m_parents.add( columnName );
                }

                // Order By

                int sortNo = field.getSortNo();

                if( sortNo == 0 ) {
                    ;
                } else if( Math.abs( sortNo ) == 1 ) {
                    m_OrderBys[ 0 ] = columnName;

                    if( sortNo < 0 ) {
                        m_OrderBys[ 0 ] += " DESC";
                    }
                } else if( Math.abs( sortNo ) == 2 ) {
                    m_OrderBys[ 1 ] = columnName;

                    if( sortNo < 0 ) {
                        m_OrderBys[ 1 ] += " DESC";
                    }
                } else if( Math.abs( sortNo ) == 3 ) {
                    m_OrderBys[ 2 ] = columnName;

                    if( sortNo < 0 ) {
                        m_OrderBys[ 2 ] += " DESC";
                    }
                }

                // Add field

                m_mTable.addField( field );

                // List of ColumnNames, this field is dependent on

                ArrayList list = field.getDependentOn();

                for( int i = 0;i < list.size();i++ ) {
                    m_depOnField.put( list.get( i ),field );    // ColumnName, Field
                }

                // Add fields all fields are dependent on

                if( columnName.equals( "IsActive" ) || columnName.equals( "Processed" )) {
                    m_depOnField.put( columnName,null );
                }
            }
        }    // for all fields

        // Add Standard Fields
        // Bug fix, para las vistas no es obligatorio esas columnas
        if( !m_vo.IsView && m_mTable.getField( "Created" ) == null ) {
            MField created = new MField( MFieldVO.createStdField( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,m_vo.AD_Window_ID,false,true,true ));

            m_mTable.addField( created );
        }

        if( !m_vo.IsView && m_mTable.getField( "CreatedBy" ) == null ) {
            MField createdBy = new MField( MFieldVO.createStdField( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,m_vo.AD_Window_ID,false,true,false ));

            m_mTable.addField( createdBy );
        }

        if( !m_vo.IsView && m_mTable.getField( "Updated" ) == null ) {
            MField updated = new MField( MFieldVO.createStdField( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,m_vo.AD_Window_ID,false,false,true ));

            m_mTable.addField( updated );
        }

        if( !m_vo.IsView && m_mTable.getField( "UpdatedBy" ) == null ) {
            MField updatedBy = new MField( MFieldVO.createStdField( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,m_vo.AD_Window_ID,false,false,false ));

            m_mTable.addField( updatedBy );
        }

        return true;
    }    // loadFields

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MTable getTableModel() {
        return m_mTable;
    }    // getTableModel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public javax.swing.Icon getIcon() {
        if( m_vo.AD_Image_ID == 0 ) {
            return null;
        }

        //

        return null;
    }    // getIcon

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public boolean hasDependants( String columnName ) {

        // m_depOnField.printToLog();

        return m_depOnField.containsKey( columnName );
    }    // isDependentOn

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public ArrayList getDependantList( String columnName ) {
        return m_depOnField.getValues( columnName );
    }    // getDependentFieldList

    /**
     * Descripción de Método
     *
     *
     * @param query
     */

    public void setQuery( MQuery query ) {
        if( query == null ) {
            m_query = new MQuery();
        } else {
            m_query = query;
        }
    }    // setQuery

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MQuery getQuery() {
        return m_query;
    }    // getQuery

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isQueryActive() {
        return m_query.isActive();
    }    // isQueryActive

    /**
     * Descripción de Método
     *
     */

    public void enableEvents() {

        // Setup Events

        m_mTable.addDataStatusListener( this );

        // m_mTable.addTableModelListener(this);

    }    // enableEvents

    /**
     * Descripción de Método
     *
     *
     * @param onlyCurrentRows
     */

    public void query( boolean onlyCurrentRows ) {
        query( onlyCurrentRows,0 );
    }    // query

    /**
     * Descripción de Método
     *
     *
     * @param onlyCurrentRows
     * @param onlyCurrentDays
     */

    public void query( boolean onlyCurrentRows,int onlyCurrentDays ) {
        log.fine( "#" + m_vo.TabNo + " - Only Current Rows=" + onlyCurrentRows + ", Days=" + onlyCurrentDays + ", Detail=" + isDetail());

        // is it same query?

        boolean refresh = m_oldQuery.equals( m_query.getWhereClause()) && (m_vo.onlyCurrentRows == onlyCurrentRows) && (m_vo.onlyCurrentDays == onlyCurrentDays);

        m_oldQuery           = m_query.getWhereClause();
        m_vo.onlyCurrentRows = onlyCurrentRows;
        m_vo.onlyCurrentDays = onlyCurrentDays;

        // Tab Where Clause

        StringBuffer where = new StringBuffer( m_vo.WhereClause );

        if( m_vo.onlyCurrentDays > 0 ) {
            if( where.length() > 0 ) {
                where.append( " AND " );
            }

            where.append( "Created >= " );

            if( DB.isSybase()) {
                where.append( "dateadd(dd,-" ).append( m_vo.onlyCurrentDays ).append( ",getdate())" );
            } else if (DB.isPostgreSQL()) {
                where.append( "current_date - interval '" ).append( m_vo.onlyCurrentDays ).append(" days '");
            }else
            {
            	where.append( "SysDate-" ).append( m_vo.onlyCurrentDays );
            }
            
        }

        // Detail Query

        if( isDetail()) {
            String lc = getLinkColumnName();

            if( lc.equals( "" )) {
                log.severe( "No link column" );
            } else {
                String value = Env.getContext( m_vo.ctx,m_vo.WindowNo,lc );

                // Same link value?

                if( refresh ) {
                    refresh = m_linkValue.equals( value );
                }

                m_linkValue = value;

                /*
                 * ------------------------------------------------------------------------------
                 * Modified by Matias Cap - Disytel
                 * ------------------------------------------------------------------------------
                 * 
                 * Al navegar entre pestañas dependientes de pestañas padre, se encontró un bug cuando
                 * el valor de la pestaña padre es null, o sea, el valor de LinkColumnName del contexto.
                 * Esto traía un problema ya que no se agregaba la cláusula where filtrando por el 
                 * valor del padre porque era null.
                 * La modificación es en base a ese valor del LinkColumnName, cuando es null o la longitud 
                 * es 0, lo seteo a -1 si el link es un id o lo dejo vacío si no es id.
                 * 
                 *  
                 *  -----------------------------------------------------------------------------
                 *  Código anterior
                 *  -----------------------------------------------------------------------------
                 *  // Check validity
				 *
                 *  if( value.length() == 0 ) {
                 *  	log.severe( "No value for link column " + lc );   	                    
                 *	}else{
                 *   
                 *		// we have column and value
				 * 
                 *		if( where.length() != 0 ) {
                 *			where.append( " AND " );
                 * 		}
				 *
                 *		where.append( lc ).append( "=" );
				 *
                 *		if( lc.endsWith( "_ID" )) {
                 *			where.append( value );
                 *		} else {
                 *  		where.append( "'" ).append( value ).append( "'" );
                 *		}
                 *	}
				 *
				 * ------------------------------------------------------------------------
				 * Fin del código anterior
				 * ------------------------------------------------------------------------
                 */
                
                
                
                // Check validity

                if( value.length() == 0 ) {
                    log.severe( "No value for link column " + lc );
                    
                    if(lc.endsWith( "_ID" )){
                    	value = "-1";
                    }                    
                }
                    
                // we have column and value

                if( where.length() != 0 ) {
                	where.append( " AND " );
                }

                where.append( lc ).append( "=" );

                if( lc.endsWith( "_ID" )) {
                	where.append( value );
                } else {
                    where.append( "'" ).append( value ).append( "'" );
                }
                
            }
        }    // isDetail

        m_extendedWhere = where.toString();

        // Final Query

        if( m_query.isActive()) {
            String q = validateQuery( m_query );

            if( q != null ) {
                if( where.length() > 0 ) {
                    where.append( " AND " );
                }

                where.append( q );
            }
        }

        log.fine( "#" + m_vo.TabNo + " - " + where );

        if( m_mTable.isOpen()) {
            if( refresh ) {
                m_mTable.dataRefreshAll();
            } else {
                m_mTable.dataRequery( where.toString(),m_vo.onlyCurrentRows &&!isDetail(),onlyCurrentDays );
            }
        } else {
            m_mTable.setSelectWhereClause( where.toString(),m_vo.onlyCurrentRows &&!isDetail(),onlyCurrentDays );
            m_mTable.open();
        }

        // Go to Record 0

        setCurrentRow( 0,true );
    }    // query

	/**
	 *	Assemble whereClause and query MTable and position to row 0.
	 *  <pre>
	 *		Scenarios:
	 *		- Never opened 					(full query)
	 *		- query changed 				(full query)
	 *		- Detail link value changed		(full query)
	 *		- otherwise 					(refreshAll)
	 *  </pre>
	 *  @param onlyCurrentRows only current rows
	 *  @param onlyCurrentDays if only current row, how many days back
	 *  @param maxRows maximum rows or 0 for all
	 */
	public void query (boolean onlyCurrentRows, int onlyCurrentDays, int maxRows)
	{
		if (!m_loadComplete) initTab(false);
		
		log.fine("#" + m_vo.TabNo
			+ " - Only Current Rows=" + onlyCurrentRows
			+ ", Days=" + onlyCurrentDays + ", Detail=" + isDetail());
		//	is it same query?
		boolean refresh = m_oldQuery.equals(m_query.getWhereClause())
			&& m_vo.onlyCurrentRows == onlyCurrentRows && m_vo.onlyCurrentDays == onlyCurrentDays;
		m_oldQuery = m_query.getWhereClause();
		m_vo.onlyCurrentRows = onlyCurrentRows;
		m_vo.onlyCurrentDays = onlyCurrentDays;

		/**
		 *	Set Where Clause
		 */
		//	Tab Where Clause
		StringBuffer where = new StringBuffer(m_vo.WhereClause);
		if (m_vo.onlyCurrentDays > 0)
		{
			if (where.length() > 0)
				where.append(" AND ");
			where.append("Created >= ");
			where.append("SysDate-").append(m_vo.onlyCurrentDays);
		}
		//	Detail Query
		if (isDetail())
		{
			m_parentNeedSave = false;
			String lc = getLinkColumnName();
			if (lc.equals("")) {
				log.warning ("No link column");
				where.append (" 2=3");
			}
			else
			{
				String value = null;
				if ( m_parentColumnName.length() > 0 )
				{	
					// explicit parent link defined
					value = Env.getContext(m_vo.ctx, m_vo.WindowNo, getParentTabNo(), m_parentColumnName, true);
					if (value == null || value.length() == 0)
						value = Env.getContext(m_vo.ctx, m_vo.WindowNo, m_parentColumnName, true); // back compatibility
				} else {
					value = Env.getContext(m_vo.ctx, m_vo.WindowNo, getParentTabNo(), lc, true);
					if (value == null || value.length() == 0)
						value = Env.getContext(m_vo.ctx, m_vo.WindowNo, lc, true); // back compatibility
				}	
				
				//	Same link value?
				if (refresh)
					refresh = m_linkValue.equals(value);
				m_linkValue = value;
				//	Check validity
				if (value.length() == 0)
				{
					//log.severe ("No value for link column " + lc);
					//parent is new, can't retrieve detail
					m_parentNeedSave = true;
					if (where.length() != 0)
						where.append(" AND ");
					// where.append(lc).append(" is null ");
					// as opened by this fix [ 1881480 ] Navigation problem between tabs
					// it's safer to avoid retrieving details at all if there is no parent value
					where.append (" 2=3");
				}
				else
				{
					//	we have column and value
					if (where.length() != 0)
						where.append(" AND ");
					where.append(getTableName()).append(".").append(lc).append("=");
					if (lc.endsWith("_ID"))
						where.append(DB.TO_NUMBER(new BigDecimal(value), DisplayType.ID));
					else
						where.append(DB.TO_STRING(value));
				}
			}
		}	//	isDetail

		m_extendedWhere = where.toString();

		//	Final Query
		if (m_query.isActive())
		{
			String q = validateQuery(m_query);
			if (q != null)
			{
				if (where.length() > 0 )
					where.append(" AND ");
				where.append(" (").append(q).append(")");
			}
		}

		/**
		 *	Query
		 */
		log.fine("#" + m_vo.TabNo + " - " + where);
		if (m_mTable.isOpen())
		{
			if (refresh)
				m_mTable.dataRefreshAll();
			else
				m_mTable.dataRequery(where.toString(), m_vo.onlyCurrentRows && !isDetail(), onlyCurrentDays);
		}
		else
		{
			m_mTable.setSelectWhereClause(where.toString(), m_vo.onlyCurrentRows && !isDetail(), onlyCurrentDays);
			m_mTable.open(maxRows);
		}
		//  Go to Record 0
		setCurrentRow(0, true);
	}	//	query

    
    /**
     * Descripción de Método
     *
     *
     * @param query
     *
     * @return
     */

    protected String validateQuery( MQuery query ) {
    	log.fine("-----------------> En validateQuery");
        if( (query == null) || (query.getRestrictionCount() == 0) ) {
            return null;
        }

        // Check: only one restriction

        if( query.getRestrictionCount() != 1 ) {
            log.fine( "Ignored(More than 1 Restriction): " + query );

            return query.getWhereClause();
        }

        String colName = query.getColumnName( 0 );

        if( colName == null ) {
            log.fine( "Ignored(No Column): " + query );

            return query.getWhereClause();
        }

        // a '(' in the name = function - don't try to resolve

        if( colName.indexOf( '(' ) != -1 ) {
            log.fine( "Ignored(Function): " + colName );

            return query.getWhereClause();
        }

        // OK - Query is valid - Simple Query.

        if( getField( colName ) != null ) {
            log.fine( "Field Found: " + colName );

            return query.getWhereClause();
        }

        // Find Refernce Column e.g. BillTo_ID -> C_BPartner_Location_ID

        String sql = "SELECT cc.ColumnName " + "FROM AD_Column c" + " INNER JOIN AD_Ref_Table r ON (c.AD_Reference_Value_ID=r.AD_Reference_ID)" + " INNER JOIN AD_Column cc ON (r.AD_Key=cc.AD_Column_ID) " + "WHERE c.AD_Reference_ID IN (18,30)"    // Table/Search
                     + " AND c.ColumnName=?";
        String refColName = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setString( 1,colName );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                refColName = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"(ref) - Column=" + colName,e );

            return query.getWhereClause();
        }

        // Reference Column found

        if( refColName != null ) {
            query.setColumnName( 0,refColName );

            if( getField( refColName ) != null ) {
                log.fine( "Column " + colName + " replaced with " + refColName );

                return query.getWhereClause();
            }

            colName = refColName;
        }

        // Column NOT in Tab - create EXISTS subquery

        String tableName    = null;
        String tabKeyColumn = getKeyColumnName();

        // Column=SalesRep_ID, Key=AD_User_ID, Query=SalesRep_ID=101

        sql = "SELECT t.TableName " + "FROM AD_Column c" + " INNER JOIN AD_Table t ON (c.AD_Table_ID=t.AD_Table_ID) " + "WHERE c.ColumnName=? AND IsKey='Y'"    // #1 Link Column
              + " AND EXISTS (SELECT * FROM AD_Column cc" + " WHERE cc.AD_Table_ID=t.AD_Table_ID AND cc.ColumnName=?)";    // #2 Tab Key Column

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setString( 1,colName );
            pstmt.setString( 2,tabKeyColumn );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                tableName = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"Column=" + colName + ", Key=" + tabKeyColumn,e );

            return null;
        }

        // Causes could be functions in query
        // e.g. Column=UPPER(Name), Key=AD_Element_ID, Query=UPPER(AD_Element.Name) LIKE '%CUSTOMER%'

        if( tableName == null ) {
            log.info( "Not successfull - Column=" + colName + ", Key=" + tabKeyColumn + ", Query=" + query );

            return query.getWhereClause();
        }
        log.fine("donde las xx");

        query.setTableName( "xx" );

        StringBuffer result = new StringBuffer( "EXISTS (SELECT * FROM " ).append( tableName ).append( " xx WHERE " ).append( query.getWhereClause( true )).append( " AND xx." ).append( tabKeyColumn ).append( "=" ).append( getTableName()).append( "." ).append( tabKeyColumn ).append( ")" );

        log.fine( result.toString());

        return result.toString();
    }    // validateQuery

	/**
	 *  Refresh row data
	 *  @param row index
	 *  @param fireEvent
	 */
	public void dataRefresh (int row, boolean fireEvent)
	{
		log.fine("#" + m_vo.TabNo + " - row=" + row);
		m_mTable.dataRefresh(row, fireEvent);
		setCurrentRow(row, fireEvent);
		if (fireEvent)
			fireStateChangeEvent(new StateChangeEvent(this, StateChangeEvent.DATA_REFRESH));
	}   //  dataRefresh

    
	/**
	 *  Refresh current row data
	 *  @param fireEvent
	 */
	public void dataRefresh (boolean fireEvent)
	{
		dataRefresh (m_currentRow, fireEvent);
	}   //  dataRefresh

    
	/**************************************************************************
	 *  Refresh all data
	 *  @param fireEvent
	 */
	public void dataRefreshAll (boolean fireEvent)
	{
		log.fine("#" + m_vo.TabNo);
		/** @todo does not work with alpha key */
		int keyNo = m_mTable.getKeyID(m_currentRow);
		m_mTable.dataRefreshAll(fireEvent);
		//  Should use RowID - not working for tables with multiple keys
		if (keyNo != -1)
		{
			if (keyNo != m_mTable.getKeyID(m_currentRow))   //  something changed
			{
				int size = getRowCount();
				for (int i = 0; i < size; i++)
				{
					if (keyNo == m_mTable.getKeyID(i))
					{
						m_currentRow = i;
						break;
					}
				}
			}
		}
		setCurrentRow(m_currentRow, fireEvent);
		if (fireEvent)
			fireStateChangeEvent(new StateChangeEvent(this, StateChangeEvent.DATA_REFRESH_ALL));
	}   //  dataRefreshAll

	
    /**
     * Descripción de Método
     *
     */

    public void dataRefreshAll() {
        log.fine( "#" + m_vo.TabNo );

        int keyNo = m_mTable.getKeyID( m_currentRow );

        m_mTable.dataRefreshAll();

        // Should use RowID - not working for tables with multiple keys

        if( keyNo != -1 ) {
            if( keyNo != m_mTable.getKeyID( m_currentRow ))    // something changed
            {
                int size = getRowCount();

                for( int i = 0;i < size;i++ ) {
                    if( keyNo == m_mTable.getKeyID( i )) {
                        m_currentRow = i;

                        break;
                    }
                }
            }
        }

        setCurrentRow( m_currentRow,true );
    }    // dataRefreshAll

    /**
     * Descripción de Método
     *
     */

    public void dataRefresh() {
        dataRefresh( m_currentRow );
    }    // dataRefresh

    /**
     * Descripción de Método
     *
     *
     * @param row
     */

    public void dataRefresh( int row ) {
        log.fine( "#" + m_vo.TabNo + " - row=" + row );
        m_mTable.dataRefresh( row );
        setCurrentRow( row,true );
    }    // dataRefresh

    /**
     * Descripción de Método
     *
     *
     * @param manualCmd
     *
     * @return
     */

    public boolean dataSave( boolean manualCmd ) {
        log.fine( "#" + m_vo.TabNo + " - row=" + m_currentRow );

        try {
            boolean retValue = ( m_mTable.dataSave( manualCmd ) == MTable.SAVE_OK );

            if( manualCmd ) {
                setCurrentRow( m_currentRow,false );
            }

            clearCurrentRecordWarning(retValue);
            
            return retValue;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"#" + m_vo.TabNo + " - row=" + m_currentRow,e );
        }

        return false;
    }    // dataSave

    /**
     * Descripción de Método
     *
     *
     * @param rowChange
     * @param onlyRealChange
     *
     * @return
     */

    public boolean needSave( boolean rowChange,boolean onlyRealChange ) {
        if( rowChange ) {
            return m_mTable.needSave( -2,onlyRealChange );
        } else {
            if( onlyRealChange ) {
                return m_mTable.needSave();
            } else {
                return m_mTable.needSave( onlyRealChange );
            }
        }
    }    // isDataChanged

    /**
     * Descripción de Método
     *
     */

    public void dataIgnore() {
        log.fine( "#" + m_vo.TabNo );
        m_mTable.dataIgnore();
        setCurrentRow( m_currentRow,false );    // re-load data
        log.fine( "#" + m_vo.TabNo + "- fini" );
    }                                           // dataIgnore

    /**
     * Descripción de Método
     *
     *
     * @param copy
     *
     * @return
     */

    public boolean dataNew( boolean copy ) {
        log.fine( "#" + m_vo.TabNo );

        // Prevent New Where Main Record is processed

        if( m_vo.TabNo > 0 ) {
        	// Modificado por Franco Bonafine - Disytel (2010-11-23)
        	// Estas dos sentencias obtenían el valor de Processed e IsActive del registro actual
        	// y NO del registro principal o maestro ya que en el contexto se pisaba constantemente
        	// el valor de Processed e IsActive con los valores del registro que se estaba visualizando
        	// (no se guardaba el valor para cada registro posicionado en cada pestaña, y en
        	// particular para el registro principal o padre de este registro).
        	// Entonces se corrigió este hecho en MField, ahora el MField guarda por cada pestaña
        	// el valor para Processed e IsActive y aquí se obtienen esos valores del contexto
        	// indicando la pestaña padre.
        	//
        	// --> Código antiguo
        	// boolean processed = "Y".equals( Env.getContext( m_vo.ctx,m_vo.WindowNo,"Processed" ));
            // boolean active = "Y".equals( Env.getContext( m_vo.ctx,m_vo.WindowNo, "IsActive" ));
        	// <-- 
        	//
        	// TODO: Obtener el nro de pestaña padre para casos en que hay al menos 3 pestañas anidadas.
        	// Aquí se supone como pestaña padre la pestaña número 0 para corregir los casos mas
        	// comunes de todos, pero queda pendiente el cálculo correcto de la pestaña padre
        	// a partir de el nivel de pestaña o de la configuración de columna primario o 
        	// enlace a tabla principal. 
        	// Dado que anteriormente la validación no hacía lo que pretendía, asumiendo la pestaña
        	// padre como la 0 se corrige el problema de no poder crear un registro en una pestaña
        	// cuando el registro actual está desactivado o procesado, y además se valida correctamente
        	// el hecho de que si el registro padre está procesado o desactivado no se puedan crear
        	// registros en la pestaña hijo, siempre y cuando la relación sea de 2 pestañas, entre la
        	// principal y cualquier pestaña hijo.
        	int parentTabNo = 0;
        	// Obtiene los dos valores del contexto indicando el nro de pestaña del registro principal.
			// Si la pestaña está marcada como siempre actualizable, entonces no
			// se verifica el campo processed
        	
			boolean processed = !isAlwaysUpdateable()
					&& "Y".equals(Env.getContext(m_vo.ctx, m_vo.WindowNo,
							parentTabNo, "Processed"));
            boolean active = "Y".equals( Env.getContext( m_vo.ctx,m_vo.WindowNo, parentTabNo, "IsActive" ));

            if( processed ||!active ) {
                log.severe( "Not allowed in TabNo=" + m_vo.TabNo + " -> Processed=" + processed + ",Active=" + active );

                return false;
            }

            log.fine( "Processed=" + processed + ", Active=" + active );
        }

        boolean retValue = m_mTable.dataNew( m_currentRow,copy );

        if( !retValue ) {
            return retValue;
        }

        setCurrentRow( m_currentRow + 1,true );

        // process all Callouts (no dependency check - assumed that settings are valid)

        for( int i = 0;i < getFieldCount();i++ ) {
            processCallout( getField( i ));
        }

        // check validity of defaults

        for( int i = 0;i < getFieldCount();i++ ) {
            getField( i ).validateValue();
        }

        m_mTable.setChanged( false );

        return retValue;
    }    // dataNew

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean dataDelete() {
        log.fine( "#" + m_vo.TabNo + " - row=" + m_currentRow );

        boolean retValue = m_mTable.dataDelete( m_currentRow );

        setCurrentRow( m_currentRow,true );

        clearCurrentRecordWarning(retValue);
        
        return retValue;
    }    // dataDelete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_vo.Name;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_vo.Description;
    }    // getDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getHelp() {
        return m_vo.Help;
    }    // getHelp

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTabLevel() {
        return m_vo.TabLevel;
    }    // getTabLevel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCommitWarning() {
        if (hasCurrentRecordWarning())
        	return getCurrentRecordWarning();
        else
        	return m_vo.CommitWarning;
    }    // getCommitWarning

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected MTable getMTable() {
        return m_mTable;
    }    // getMTable

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getKeyColumnName() {
        return m_keyColumnName;
    }    // getKeyColumnName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getLinkColumnName() {
        return m_linkColumnName;
    }    // getLinkColumnName

    /**
     * Descripción de Método
     *
     *
     * @param linkColumnName
     */

    public void setLinkColumnName( String linkColumnName ) {
        if( linkColumnName != null ) {
            m_linkColumnName = linkColumnName;
        } else {
            if( m_vo.AD_Column_ID == 0 ) {
                return;

                // we have a link column identified (primary parent column)

            } else {
                String SQL = "SELECT ColumnName FROM AD_Column WHERE AD_Column_ID=?";

                try {
                    PreparedStatement pstmt = DB.prepareStatement( SQL );

                    pstmt.setInt( 1,m_vo.AD_Column_ID );    // Parent Link Column

                    ResultSet rs = pstmt.executeQuery();

                    if( rs.next()) {
                        m_linkColumnName = rs.getString( 1 );
                    }

                    rs.close();
                    pstmt.close();
                } catch( SQLException e ) {
                    log.log( Level.SEVERE,"",e );
                }

                log.fine( "AD_Column_ID=" + m_vo.AD_Column_ID + " - " + m_linkColumnName );
            }
        }

        Env.setContext( m_vo.ctx,m_vo.WindowNo,m_vo.TabNo,"LinkColumnName",m_linkColumnName );
    }    // setLinkColumnName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCurrent() {

        // Open?

        if( !m_mTable.isOpen()) {
            return false;
        }

        // Same Query

        if( !m_oldQuery.equals( m_query.getWhereClause())) {
            return false;
        }

        // Detail?

        if( !isDetail()) {
            return true;
        }

        // Same link column value

        String value = Env.getContext( m_vo.ctx,m_vo.WindowNo,getLinkColumnName());

        return m_linkValue.equals( value );
    }    // isCurrent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOpen() {

        // Open?

        if( m_mTable != null ) {
            return m_mTable.isOpen();
        }

        return false;
    }    // isCurrent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isIncluded() {
        return m_included;
    }    // isIncluded

    /**
     * Descripción de Método
     *
     *
     * @param isIncluded
     */

    public void setIncluded( boolean isIncluded ) {
        m_included = isIncluded;
    }    // setIncluded

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOnlyCurrentRows() {
        return m_vo.onlyCurrentRows;
    }    // isOnlyCurrentRows

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getParentColumnNames() {
        return m_parents;
    }    // getParentColumnNames

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected int getTreeID() {
        log.fine( m_vo.TableName );

        String SQL = "SELECT * FROM AD_ClientInfo WHERE AD_Client=" + Env.getContext( m_vo.ctx,m_vo.WindowNo,"AD_Client_ID" ) + " ORDER BY AD_Org DESC";

        //

        if( m_vo.TableName.equals( "AD_Menu" )) {
            return 10;    // MM
        } else if( m_vo.TableName.equals( "C_ElementValue" )) {
            return 20;    // EV
        } else if( m_vo.TableName.equals( "M_Product" )) {
            return 30;    // PR
        } else if( m_vo.TableName.equals( "C_BPartner" )) {
            return 40;    // BP
        } else if( m_vo.TableName.equals( "AD_Org" )) {
            return 50;    // OO
        } else if( m_vo.TableName.equals( "C_Project" )) {
            return 60;    // PJ
        }

        return 0;
    }    // getTreeID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDetail() {

        // We have IsParent columns and/or a link column

        if( (m_parents.size() > 0) || (m_vo.AD_Column_ID != 0) ) {
            return true;
        }

        return false;
    }    // isDetail

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPrinted() {
        return m_vo.AD_Process_ID != 0;
    }    // isPrinted

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getWindowNo() {
        return m_vo.WindowNo;
    }    // getWindowNo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTabNo() {
        return m_vo.TabNo;
    }    // getTabNo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Process_ID() {
        return m_vo.AD_Process_ID;
    }    // getAD_Process_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isHighVolume() {
        return m_vo.IsHighVolume;
    }    // isHighVolume

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadOnly() {
        if( m_vo.IsReadOnly ) {
            return true;
        }

        // no restrictions

        if( (m_vo.ReadOnlyLogic == null) || m_vo.ReadOnlyLogic.equals( "" )) {
            return m_vo.IsReadOnly;
        }

        // ** dynamic content **

        boolean retValue = Evaluator.evaluateLogic( this,m_vo.ReadOnlyLogic );

        log.finest( m_vo.Name + " (" + m_vo.ReadOnlyLogic + ") => " + retValue );

        return retValue;
    }    // isReadOnly

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInsertRecord() {
        if( isReadOnly()) {
            return false;
        }

        return m_vo.IsInsertRecord;
    }    // isInsertRecord
    
    public boolean isAlwaysUpdateable() {
        if( isReadOnly()) {
            return false;
        }
        
        if(!isInsertRecord()){
        	return false;
        }

        return m_vo.IsAlwaysUpdateable;
    }
    
    public boolean isInserting(){
    	return m_mTable.isInserting();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDisplayed() {

        // no restrictions

        if( (m_vo.DisplayLogic == null) || m_vo.DisplayLogic.equals( "" )) {
            return true;
        }

        // ** dynamic content **

        boolean retValue = Evaluator.evaluateLogic( this,m_vo.DisplayLogic );

        log.finest( m_vo.Name + " (" + m_vo.DisplayLogic + ") => " + retValue );

        return retValue;
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
     * @return
     */

    public boolean isSingleRow() {
        return m_vo.IsSingleRow;
    }    // isSingleRow;

    /**
     * Descripción de Método
     *
     *
     * @param isSingleRow
     */

    public void setSingleRow( boolean isSingleRow ) {
        m_vo.IsSingleRow = isSingleRow;
    }    // setSingleRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTreeTab() {
        return m_vo.HasTree;
    }    // isTreeTab

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Tab_ID() {
        return m_vo.AD_Tab_ID;
    }    // getAD_Tab_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Table_ID() {
        return m_vo.AD_Table_ID;
    }    // getAD_Table_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Window_ID() {
        return m_vo.AD_Window_ID;
    }    // getAD_Window_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getIncluded_Tab_ID() {
        return m_vo.Included_Tab_ID;
    }    // getIncluded_Tab_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTableName() {
        return m_vo.TableName;
    }    // getTableName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getWhereClause() {
        return m_vo.WhereClause;
    }    // getWhereClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSortTab() {
        return m_vo.IsSortTab;
    }    // isSortTab

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_ColumnSortOrder_ID() {
        return m_vo.AD_ColumnSortOrder_ID;
    }    // getAD_ColumnSortOrder_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_ColumnSortYesNo_ID() {
        return m_vo.AD_ColumnSortYesNo_ID;
    }    // getAD_ColumnSortYesNo_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getWhereExtended() {
        return m_extendedWhere;
    }    // getWhereExtended

    /**
     * Descripción de Método
     *
     *
     * @param onlyCurrentRows
     *
     * @return
     */

    protected String getOrderByClause( boolean onlyCurrentRows ) {

        // First Prio: Tab Order By

        if( m_vo.OrderByClause.length() > 0 ) {
            return m_vo.OrderByClause;
        }

        // Second Prio: Fields (save it)

        m_vo.OrderByClause = "";

        for( int i = 0;i < 3;i++ ) {
            String order = m_OrderBys[ i ];

            if( (order != null) && (order.length() > 0) ) {
                if( m_vo.OrderByClause.length() > 0 ) {
                    m_vo.OrderByClause += ",";
                }

                m_vo.OrderByClause += order;
            }
        }

        if( m_vo.OrderByClause.length() > 0 ) {
            return m_vo.OrderByClause;
        }

        // Third Prio: onlyCurrentRows

        m_vo.OrderByClause = "Created";

        if( onlyCurrentRows &&!isDetail()) {    // first tab only
            m_vo.OrderByClause += " DESC";
        }

        return m_vo.OrderByClause;
    }    // getOrderByClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTrxInfo() {

        // InvoiceBatch

        if( m_vo.TableName.startsWith( "C_InvoiceBatch" )) {
            int Record_ID = Env.getContextAsInt( m_vo.ctx,m_vo.WindowNo,"C_InvoiceBatch_ID" );

            log.fine( m_vo.TableName + " - " + Record_ID );

            MessageFormat mf = null;

            try {
                mf = new MessageFormat( Msg.getMsg( Env.getAD_Language( m_vo.ctx ),"InvoiceBatchSummary" ));
            } catch( Exception e ) {
                log.log( Level.SEVERE,"InvoiceBatchSummary=" + Msg.getMsg( Env.getAD_Language( m_vo.ctx ),"InvoiceBatchSummary" ),e );
            }

            if( mf == null ) {
                return " ";
            }

            Object[] arguments = new Object[ 3 ];
            boolean  filled    = false;

            //

            String sql = "SELECT COUNT(*), NVL(SUM(LineNetAmt),0), NVL(SUM(LineTotalAmt),0) " + "FROM C_InvoiceBatchLine " + "WHERE C_InvoiceBatch_ID=? AND IsActive='Y'";

            //

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,Record_ID );

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {

                    // {0} - Number of lines

                    Integer lines = new Integer( rs.getInt( 1 ));

                    arguments[ 0 ] = lines;

                    // {1} - Line net

                    Double net = new Double( rs.getDouble( 2 ));

                    arguments[ 1 ] = net;

                    // {2} - Line net

                    Double total = new Double( rs.getDouble( 3 ));

                    arguments[ 2 ] = total;
                    filled         = true;
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,m_vo.TableName + "\nSQL=" + sql,e );
            }

            if( filled ) {
                return mf.format( arguments );
            }

            return " ";
        }    // InvoiceBatch

        // Order || Invoice

        else if( m_vo.TableName.startsWith( "C_Order" ) || m_vo.TableName.startsWith( "C_Invoice" )) {
            int     Record_ID;
            boolean isOrder = m_vo.TableName.startsWith( "C_Order" );

            //

            StringBuffer sql = new StringBuffer( "SELECT COUNT(*) AS Lines,c.ISO_Code,o.TotalLines,o.GrandTotal," + "currencyBase(o.GrandTotal,o.C_Currency_ID,o.DateAcct, o.AD_Client_ID,o.AD_Org_ID) AS ConvAmt " );

            if( isOrder ) {
            	log.fine("En MTAB.java con m_vo.ctx= "+ m_vo.ctx + " y el m_vo.WindowNo= "+m_vo.WindowNo);
                Record_ID = Env.getContextAsInt( m_vo.ctx,m_vo.WindowNo,"C_Order_ID" );
                sql.append( "FROM C_Order o" + " INNER JOIN C_Currency c ON (o.C_Currency_ID=c.C_Currency_ID)" + " INNER JOIN C_OrderLine l ON (o.C_Order_ID=l.C_Order_ID) " + "WHERE o.C_Order_ID=? " );
            } else {
                Record_ID = Env.getContextAsInt( m_vo.ctx,m_vo.WindowNo,"C_Invoice_ID" );
                sql.append( "FROM C_Invoice o" + " INNER JOIN C_Currency c ON (o.C_Currency_ID=c.C_Currency_ID)" + " INNER JOIN C_InvoiceLine l ON (o.C_Invoice_ID=l.C_Invoice_ID) " + "WHERE o.C_Invoice_ID=? " );
            }

            sql.append( "GROUP BY o.C_Currency_ID, c.ISO_Code, o.TotalLines, o.GrandTotal, o.DateAcct, o.AD_Client_ID, o.AD_Org_ID" );
            log.fine( m_vo.TableName + " - " + Record_ID );

            MessageFormat mf = null;
            log.fine("En MTAB.java con OrderSummary ="+ Msg.getMsg( Env.getAD_Language( m_vo.ctx ),"OrderSummary" ));
            try {
                mf = new MessageFormat( Msg.getMsg( Env.getAD_Language( m_vo.ctx ),"OrderSummary" ));
            } catch( Exception e ) {
                log.log( Level.SEVERE,"OrderSummary=" + Msg.getMsg( Env.getAD_Language( m_vo.ctx ),"OrderSummary" ),e );
            }

            if( mf == null ) {
                return " ";
            }

            Object[] arguments = new Object[ 6 ];
            boolean  filled    = false;

            //

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql.toString());

                pstmt.setInt( 1,Record_ID );

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {

                    // {0} - Number of lines

                    Integer lines = new Integer( rs.getInt( 1 ));

                    arguments[ 0 ] = lines;

                    // {1} - Line toral

                    Double lineTotal = new Double( rs.getDouble( 3 ));

                    arguments[ 1 ] = lineTotal;

                    // {2} - Grand total (including tax, etc.)

                    Double grandTotal = new Double( rs.getDouble( 4 ));

                    arguments[ 2 ] = grandTotal;

                    // {3} - Currency

                    String currency = rs.getString( 2 );

                    arguments[ 3 ] = currency;

                    // (4) - Grand total converted to Euro

                    Double grandEuro = new Double( rs.getDouble( 5 ));

                    arguments[ 4 ] = grandEuro;

                    arguments[ 5 ] = MCurrency.getISO_Code(Env.getCtx(), Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" ));
                    filled         = true;
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,m_vo.TableName + "\nSQL=" + sql,e );
            }

            if( filled ) {
                return mf.format( arguments );
            }

            return " ";
        }    // Order || Invoice

        // Expense Report

        else if( m_vo.TableName.startsWith( "S_TimeExpense" ) && (m_vo.TabNo == 0) ) {
            int Record_ID = Env.getContextAsInt( m_vo.ctx,m_vo.WindowNo,"S_TimeExpense_ID" );

            log.fine( m_vo.TableName + " - " + Record_ID );

            MessageFormat mf = null;

            try {
                mf = new MessageFormat( Msg.getMsg( Env.getAD_Language( m_vo.ctx ),"ExpenseSummary" ));
            } catch( Exception e ) {
                log.log( Level.SEVERE,"ExpenseSummary=" + Msg.getMsg( Env.getAD_Language( m_vo.ctx ),"ExpenseSummary" ),e );
            }

            if( mf == null ) {
                return " ";
            }

            Object[] arguments = new Object[ 3 ];
            boolean  filled    = false;

            //

            String SQL = "SELECT COUNT(*) AS Lines, SUM(ConvertedAmt*Qty) " + "FROM S_TimeExpenseLine " + "WHERE S_TimeExpense_ID=?";

            //

            try {
                PreparedStatement pstmt = DB.prepareStatement( SQL );

                pstmt.setInt( 1,Record_ID );

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {

                    // {0} - Number of lines

                    Integer lines = new Integer( rs.getInt( 1 ));

                    arguments[ 0 ] = lines;

                    // {1} - Line toral

                    Double total = new Double( rs.getDouble( 2 ));

                    arguments[ 1 ] = total;

                    // {3} - Currency

                    arguments[ 2 ] = " ";
                    filled         = true;
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,m_vo.TableName + "\nSQL=" + SQL,e );
            }

            if( filled ) {
                return mf.format( arguments );
            }

            return " ";
        }    // S_TimeExpense

        // Default - No Trx Info

        return null;
    }    // getTrxInfo

    /**
     * Descripción de Método
     *
     */

    protected void loadDependentInfo() {
        if( m_vo.TableName.equals( "C_Order" )) {
            int     C_DocTyp_ID = 0;
            Integer target      = ( Integer )getValue( "C_DocTypeTarget_ID" );

            if( target != null ) {
                C_DocTyp_ID = target.intValue();
            }

            if( C_DocTyp_ID == 0 ) {
                return;
            }

            String sql = "SELECT DocSubTypeSO FROM C_DocType WHERE C_DocType_ID=?";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,C_DocTyp_ID );

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    Env.setContext( m_vo.ctx,m_vo.WindowNo,"OrderType",rs.getString( 1 ));
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"loadOrderType",e );
            }
        }    // loadOrderInfo
        
        // Carga el DocTypeKey del documento en caso de exisitir
        Integer targetDocTypeID = (Integer)getValue("C_DocTypeTarget_ID");
        if (targetDocTypeID != null) {
        	String docTypeKey = DB.getSQLValueString(null, 
        			"SELECT DocTypeKey FROM C_DocType WHERE C_DocType_ID = ?", targetDocTypeID);
        	if (docTypeKey != null) {
        		 Env.setContext( m_vo.ctx,m_vo.WindowNo,"DocTypeKey",docTypeKey);
        	}
        }
        	
    }        // loadDependentInfo

    /**
     * Descripción de Método
     *
     */

    public void loadAttachments() {
        log.fine( "#" + m_vo.TabNo );

        if( !canHaveAttachment()) {
            return;
        }

        String SQL = "SELECT AD_Attachment_ID, Record_ID FROM AD_Attachment " + "WHERE AD_Table_ID=?";

        try {
            if( m_Attachment == null ) {
                m_Attachment = new HashMap();
            } else {
                m_Attachment.clear();
            }

            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,m_vo.AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                Integer key   = new Integer( rs.getInt( 2 ));
                Integer value = new Integer( rs.getInt( 1 ));

                m_Attachment.put( key,value );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadAttachments",e );
        }

        log.config( "#" + m_Attachment.size());
    }    // loadAttachment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean canHaveAttachment() {
        if( getKeyColumnName().endsWith( "_ID" )) {
            return true;
        }

        return false;
    }    // canHaveAttachment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean hasAttachment() {
        if( m_Attachment == null ) {
            loadAttachments();
        }

        if( (m_Attachment == null) || m_Attachment.isEmpty()) {
            return false;
        }

        //

        Integer key = new Integer( m_mTable.getKeyID( m_currentRow ));

        return m_Attachment.containsKey( key );
    }    // hasAttachment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_AttachmentID() {
        if( m_Attachment == null ) {
            loadAttachments();
        }

        if( m_Attachment.isEmpty()) {
            return 0;
        }

        //

        Integer key   = new Integer( m_mTable.getKeyID( m_currentRow ));
        Integer value = ( Integer )m_Attachment.get( key );

        if( value == null ) {
            return 0;
        } else {
            return value.intValue();
        }
    }    // getAttachmentID

    /**
     * Descripción de Método
     *
     */

    public void loadLocks() {
        int AD_User_ID = Env.getContextAsInt( Env.getCtx(),"#AD_User_ID" );

        log.fine( "#" + m_vo.TabNo + " - AD_User_ID=" + AD_User_ID );

        if( !canHaveAttachment()) {
            return;
        }

        String SQL = "SELECT Record_ID " + "FROM AD_protected_Access " + "WHERE AD_User_ID=? AND AD_Table_ID=? AND IsActive='Y' " + "ORDER BY Record_ID";

        try {
            if( m_Lock == null ) {
                m_Lock = new ArrayList();
            } else {
                m_Lock.clear();
            }

            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,AD_User_ID );
            pstmt.setInt( 2,m_vo.AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                Integer key = new Integer( rs.getInt( 1 ));

                m_Lock.add( key );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadLocks",e );
        }

        log.fine( "loadLocks #" + m_Lock.size());
    }    // loadLooks

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLocked() {
        if( !MRole.getDefault( m_vo.ctx,false ).isPersonalLock()) {
            return false;
        }

        if( m_Lock == null ) {
            loadLocks();
        }

        if( (m_Lock == null) || m_Lock.isEmpty()) {
            return false;
        }

        //

        Integer key = new Integer( m_mTable.getKeyID( m_currentRow ));

        return m_Lock.contains( key );
    }    // isLocked

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param Record_ID
     * @param lock
     */

    public void lock( Properties ctx,int Record_ID,boolean lock ) {
        int AD_User_ID = Env.getContextAsInt( ctx,"#AD_User_ID" );

        log.fine( "Lock=" + lock + ", AD_User_ID=" + AD_User_ID + ", AD_Table_ID=" + m_vo.AD_Table_ID + ", Record_ID=" + Record_ID );

        MPrivateAccess access = MPrivateAccess.get( ctx,AD_User_ID,m_vo.AD_Table_ID,Record_ID );

        if( access == null ) {
            access = new MPrivateAccess( ctx,AD_User_ID,m_vo.AD_Table_ID,Record_ID );
        }

        access.setIsActive( lock );
        access.save();

        //

        loadLocks();
    }    // lock

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dataStatusChanged( DataStatusEvent e ) {
        log.fine( "#" + m_vo.TabNo + " - " + e.toString());

        int oldCurrentRow = e.getCurrentRow();

        m_DataStatusEvent = e;    // save it

        // when sorted set current row to 0

        String msg = m_DataStatusEvent.getAD_Message();

        if( (msg != null) && msg.equals( "Sorted" )) {
            setCurrentRow( 0,true );
        }

        // set current row

        m_DataStatusEvent.setCurrentRow( m_currentRow );

        // Same row - update value

        if( oldCurrentRow == m_currentRow ) {
            MField field = m_mTable.getField( e.getChangedColumn());

            if( field != null ) {
                Object value = m_mTable.getValueAt( m_currentRow,e.getChangedColumn());

                field.setValue( value,m_mTable.isInserting());
            }
        } else {    // Redistribute Info with current row info
            fireDataStatusChanged( m_DataStatusEvent );
        }

        // log.fine("dataStatusChanged #" + m_vo.TabNo + "- fini", e.toString());

    }    // dataStatusChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    protected void fireDataStatusChanged( DataStatusEvent e ) {
        log.fine( e.toString());

        if( m_dataStatusListeners != null ) {

            // WHO Info

            if( e.getCurrentRow() >= 0 ) {
                e.Created   = ( Timestamp )getValue( "Created" );
                e.CreatedBy = ( Integer )getValue( "CreatedBy" );
                e.Updated   = ( Timestamp )getValue( "Updated" );
                e.UpdatedBy = ( Integer )getValue( "UpdatedBy" );
                e.Record_ID = getValue( m_keyColumnName );

                // Info

                StringBuffer info = new StringBuffer( getTableName());

                // We have a key column

                if( (m_keyColumnName != null) && (m_keyColumnName.length() > 0) ) {
                    info.append( " - " ).append( m_keyColumnName ).append( "=" ).append( e.Record_ID );
                } else    // we have multiple parents
                {
                    for( int i = 0;i < m_parents.size();i++ ) {
                        String keyCol = ( String )m_parents.get( i );

                        info.append( " - " ).append( keyCol ).append( "=" ).append( getValue( keyCol ));
                    }
                }

                e.Info = info.toString();
            }

            e.setInserting( m_mTable.isInserting());

            // Distribute/fire it

            Vector listeners = m_dataStatusListeners;
            int    count     = listeners.size();

            for( int i = 0;i < count;i++ ) {
                (( DataStatusListener )listeners.elementAt( i )).dataStatusChanged( e );
            }
        }

        // log.trace(log.l4_Data, "MTab.fireDataStatusChanged - fini", e.toString());

    }    // fireDataStatusChanged

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     * @param info
     */

    protected void fireDataStatusEEvent( String AD_Message,String info ) {
        m_mTable.fireDataStatusEEvent( AD_Message,info );
    }    // fireDataStatusEvent

    /**
     * Descripción de Método
     *
     *
     * @param errorLog
     */

    protected void fireDataStatusEEvent( ValueNamePair errorLog ) {
        if( errorLog != null ) {
            m_mTable.fireDataStatusEEvent( errorLog );
        }
    }    // fireDataStatusEvent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getCurrentRow() {
        if( m_currentRow != verifyRow( m_currentRow )) {
            setCurrentRow( m_mTable.getRowCount() - 1,true );
        }

        return m_currentRow;
    }    // getCurrentRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRecord_ID() {
        return m_mTable.getKeyID( m_currentRow );
    }    // getRecord_ID

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public int getKeyID( int row ) {
        return m_mTable.getKeyID( row );
    }    // getCurrentKeyID

    /**
     * Descripción de Método
     *
     *
     * @param targetRow
     *
     * @return
     */

    public int navigate( int targetRow ) {

        // nothing to do

        if( targetRow == m_currentRow ) {
            return m_currentRow;
        }

        log.info( "Row=" + targetRow );

        // Row range check

        int newRow = verifyRow( targetRow );

        // Check, if we have old uncommitted data

        if (m_mTable.dataSave( newRow,false )) 
        
        	// Se mueve a la nueva posición solo si no hay errores al guardar.
        	return setCurrentRow( newRow,true );

        else
        	// Si hay errores no se mueve y retorna la fila actual.
        	return m_currentRow;
    }    // navigate

    /**
     * Descripción de Método
     *
     *
     * @param rowChange
     *
     * @return
     */

    public int navigateRelative( int rowChange ) {
        return navigate( m_currentRow + rowChange );
    }    // navigateRelative

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int navigateCurrent() {
        log.info( "Row=" + m_currentRow );

        return setCurrentRow( m_currentRow,true );
    }    // navigateCurrent

    /**
     * Descripción de Método
     *
     *
     * @param targetRow
     *
     * @return
     */

    protected int verifyRow( int targetRow ) {
        int newRow = targetRow;

        // Table Open?

        if( !m_mTable.isOpen()) {
            log.severe( "Table not open" );

            return -1;
        }

        // Row Count

        int rows = getRowCount();

        if( rows == 0 ) {
            log.fine( "No Rows" );
           return -1; 
           
        }

        if( newRow >= rows ) {
            newRow = rows - 1;
            log.fine( "Set to max Row: " + newRow );
        } else if( newRow < 0 ) {
            newRow = 0;
            log.fine( "Set to first Row" );
        }

        return newRow;
    }    // verifyRow

	/**
	 *  Set current row - used for deleteSelection
	 *  @return current row
	 */
	public void setCurrentRow(int row){
			setCurrentRow(row, false);
	}

    
    /**
     * Descripción de Método
     *
     *
     * @param newCurrentRow
     * @param fireEvents
     *
     * @return
     */

    public int setCurrentRow( int newCurrentRow,boolean fireEvents ) {
        int oldCurrentRow = m_currentRow;

        m_currentRow = verifyRow( newCurrentRow );
        log.fine( "Row=" + m_currentRow + " - fire=" + fireEvents );

        // Update Field Values

        int size = m_mTable.getColumnCount();
        int i = 0;
        for( i = 0;i < size;i++ ) {
            MField mField = m_mTable.getField( i );

            // get Value from Table

            if( m_currentRow >= 0 ) {
                Object value = m_mTable.getValueAt( m_currentRow,i );

                mField.setValue( value,m_mTable.isInserting());

                if( m_mTable.isInserting()) {    // set invalid values to null
                    mField.validateValue();
                }
            } else {                             // no rows - set to a reasonable value - not updateable

//                              Object value = null;
//                              if (mField.isKey() || mField.isParent() || mField.getColumnName().equals(m_linkColumnName))
//                                      value = mField.getDefault();

                mField.setValue();
            }
            
            // disparar el callout en la carga del registro si tiene el check correspondiente en la def. de la columna
            if (mField.isCalloutAlsoOnLoad())
            	processCallout( mField);
        }


        loadDependentInfo();

        if( !fireEvents ) {    // prevents informing twice
            return m_currentRow;
        }

        // inform VTable/..    -> rowChanged

        m_propertyChangeSupport.firePropertyChange( PROPERTY,oldCurrentRow,m_currentRow );

        // inform APanel/..    -> dataStatus with row updated

        if( m_DataStatusEvent == null ) {
            log.fine( "No existing data status event" );
        } else {
            m_DataStatusEvent.setCurrentRow( m_currentRow );

            String status = m_DataStatusEvent.getAD_Message();

            if( (status == null) || (status.length() == 0) ) {
            	if(m_DataStatusEvent.toString().indexOf("*") > -1){
            		m_DataStatusEvent.setInfo( "CreateNew",null,false );
            	}
            	else{
            		m_DataStatusEvent.setInfo( "NavigateOrUpdate",null,false );
            	}
            }

            fireDataStatusChanged( m_DataStatusEvent );
        }

        return m_currentRow;
    }    // setCurrentRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRowCount() {
        int count = m_mTable.getRowCount();

        // Wait a bit if currently loading

        if( (count == 0) && m_mTable.isLoading()) {
            try {
                Thread.sleep( 100 );    // .1 sec
            } catch( Exception e ) {
            }

            count = m_mTable.getRowCount();
        }

        return count;
    }    // getRowCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getFieldCount() {
        return m_mTable.getColumnCount();
    }    // getFieldCount

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public MField getField( int index ) {
        return m_mTable.getField( index );
    }    // getField

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public MField getField( String columnName ) {
        return m_mTable.getField( columnName );
    }    // getField

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MField[] getFields() {
        return m_mTable.getFields();
    }    // getField
    
    
    public Map<String, MField> getMapFields(){
    	return m_mTable.getMapFields();
    }

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     * @param value
     *
     * @return
     */

    public String setValue( String columnName,Object value ) {
        if( columnName == null ) {
            return "NoColumn";
        }

        return setValue( m_mTable.getField( columnName ),value );
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @param field
     * @param value
     *
     * @return
     */

    public String setValue( MField field,Object value ) {
        if( field == null ) {
            return "NoField";
        }

        log.fine( field.getColumnName() + "=" + value + " - Row=" + m_currentRow );

        int col = m_mTable.findColumn( field.getColumnName());

        m_mTable.setValueAt( value,m_currentRow,col,false );

        //

        return processFieldChange( field );
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @param changedField
     *
     * @return
     */

    public String processFieldChange( MField changedField ) {
        processDependencies( changedField );

        return processCallout( changedField );
    }    // processFieldChange

    /**
     * Descripción de Método
     *
     *
     * @param changedField
     */

    public void processDependencies( MField changedField ) {
        String columnName = changedField.getColumnName();

        // log.trace(log.l4_Data, "Changed Column", columnName);

        // when column name is not in list of DependentOn fields - fini

        if( !hasDependants( columnName )) {
            return;
        }

        // Get dependent MFields (may be because of display or dynamic lookup)

        ArrayList list = getDependantList( columnName );

        for( int i = 0;i < list.size();i++ ) {
            MField dependentField = ( MField )list.get( i );

            // log.trace(log.l5_DData, "Dependent Field", dependentField==null ? "null" : dependentField.getColumnName());
            // if the field has a lookup

            if( (dependentField != null) && (dependentField.getLookup() instanceof MLookup) ) {
                MLookup mLookup = ( MLookup )dependentField.getLookup();

                // log.trace(log.l6_Database, "Lookup Validation", mLookup.getValidation());
                // if the lookup is dynamic (i.e. contains this columnName as variable)

                if( mLookup.getValidation().indexOf( "@" + columnName + "@" ) != -1 ) {
                    log.fine( columnName + " changed - " + dependentField.getColumnName() + " set to null" );

                    // invalidate current selection

                    setValue( dependentField,null );
                }
            }
        }    // for all dependent fields
    }        // processDependencies

    /**
     * Descripción de Método
     *
     *
     * @param field
     *
     * @return
     */

    public String processCallout( MField field ) {
        String callout = field.getCallout();
        //JOptionPane.showMessageDialog( null,"En MTab, processCallout \n con callout= "+field.getCallout(),"..Fin", JOptionPane.INFORMATION_MESSAGE );

/* Comentado con el fin de permitir callouts dinamicos en plugins incluso si el campo no contenia nada especificado en la columna
        if( callout.length() == 0 ) {
            return "";
        }
*/
        
        /** Inserta los componentes necesarios para logica adicional de plugins */
        callout = PluginCalloutUtils.insertPluginClasses(callout, getTableName(), field.getColumnName());

        Object value    = field.getValue();
        Object oldValue = field.getOldValue();

        log.fine( field.getColumnName() + "=" + value + " (" + callout + ") - old=" + oldValue );

        StringTokenizer st = new StringTokenizer( callout,";",false );

        /** Variables para uso de plugins */
        int nextStatus = MPluginStatus.STATE_TRUE_AND_CONTINUE;
        MPluginStatusCallout status = null;
        int invokeNumber = 0;
        
        while( st.hasMoreTokens() && nextStatus != MPluginStatus.STATE_FALSE)         // for each callout
        {
            String  cmd         = st.nextToken().trim();
            Callout call        = null;
            String  method      = null;
            int     methodStart = cmd.lastIndexOf( "." );

            try {
                if( methodStart != -1 )    // no class
                {
                    Class cClass = Class.forName( cmd.substring( 0,methodStart ));

                    call   = ( Callout )cClass.newInstance();
                    method = cmd.substring( methodStart + 1 );
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"class",e );

                return "Callout Invalid: " + cmd + " (" + e.toString() + ")";
            }

            if( (call == null) || (method == null) || (method.length() == 0) ) {
                return "Callout Invalid: " + method;
            }

            String retValue = "";

            try {
            	/** 
            	 * Ampliacion para plugins - invocar segun tipo de callout (core o plugin) 
            	 */
            	if (call.isPluginInstance())
            	{
            		/* Invocacion a metodo de plugin */
                	invokeNumber++;
            		CalloutPluginEngine pluginCall = (CalloutPluginEngine)call;
            		status = pluginCall.start( m_vo.ctx,method,m_vo.WindowNo,this,field,value,oldValue,false );
            		
            		/* El primer plugin en ejecucion define el nextStatus */
                	if (invokeNumber==1)
            			nextStatus = status.getContinueStatus();

                	/* si devuelve error, setear el retValue para seguir con la ejecucion standard de plugins*/            			
            		if (status.getContinueStatus() == MPluginStatus.STATE_FALSE)
            			retValue = " " + status.getErrorMessage();
            	}
            	else
            	{
            		/* Invocacion a metodo normal, indicado desde metadatos
            		 * Se invocara unicamente para STATE_TRUE_AND_CONTINUE 
            		 * (si hay error o STATE_TRUE_AND_SKIP, se obvia el metodo) 
            		 */
            		if (nextStatus == MPluginStatus.STATE_TRUE_AND_CONTINUE)
            			retValue = call.start( m_vo.ctx,method,m_vo.WindowNo,this,field,value,oldValue );
            	}
            } catch( Exception e ) {
                log.log( Level.SEVERE,"start",e );
                retValue = "Callout Invalid: " + e.toString();

                return retValue;
            }

            if( !retValue.equals( "" ))    // interrupt on first error
            {
                log.severe( retValue );

                return retValue;
            }
        }                                  // for each callout

        return "";
    }    // processCallout

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public Object getValue( String columnName ) {
        if( columnName == null ) {
            return null;
        }

        MField field = m_mTable.getField( columnName );

        return getValue( field );
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @param field
     *
     * @return
     */

    public Object getValue( MField field ) {
        if( field == null ) {
            return null;
        }

        return field.getValue();
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param columnName
     *
     * @return
     */

    public Object getValue( int row,String columnName ) {
        int col = m_mTable.findColumn( columnName );

        if( col == -1 ) {
            return null;
        }

        return m_mTable.getValueAt( row,col );
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        String retValue = "MTab #" + m_vo.TabNo;

        if( m_vo != null ) {
            retValue += " " + m_vo.Name + " (" + m_vo.AD_Tab_ID + ")";
        }

        return retValue;
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void removePropertyChangeListener( PropertyChangeListener l ) {
        m_propertyChangeSupport.removePropertyChangeListener( l );
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void addPropertyChangeListener( PropertyChangeListener l ) {
        m_propertyChangeSupport.addPropertyChangeListener( l );
    }

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
    }

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
    }

	/**
	 * @return Returns the currentRecordWarning.
	 */
	public String getCurrentRecordWarning() {
		return currentRecordWarning;
	}

	/**
	 * @param currentRecordWarning The currentRecordWarning to set.
	 */
	public void setCurrentRecordWarning(String currentRecordWarning) {
		// No se permiten mensajes con longitud 0.
		if (currentRecordWarning != null && currentRecordWarning.length() == 0)
			currentRecordWarning = null;
		this.currentRecordWarning = currentRecordWarning;
	}
	
	public void clearCurrentRecordWarning() {
		clearCurrentRecordWarning(true);
	}
	
	public boolean hasCurrentRecordWarning() {
		return getCurrentRecordWarning() != null;
	}
	
	protected void clearCurrentRecordWarning(boolean ok) {
		if (ok)
			setCurrentRecordWarning(null);
	}

	public boolean isShowDialogProcessMsg(){
		return m_vo.showDialogProcessMsg;
	}	
	
	/**
	 *  Get dependents fields of columnName
	 *  @param columnName column name
	 *  @return ArrayList with GridFields dependent on columnName
	 */
	public ArrayList<MField> getDependantFields (String columnName)
	{
		return m_depOnField.getValues(columnName);
	}   //  getDependentFields

	
	/**
	 * 	Get Display Logic
	 *	@return display logic
	 */
	public String getDisplayLogic()
	{
		return m_vo.DisplayLogic;
	}	//	getDisplayLogic

	/**
	 *  Get a list of variables, this tab is dependent on.
	 *  - for display purposes
	 *  @return ArrayList
	 */
	public ArrayList<String> getDependentOn()
	{
		ArrayList<String> list = new ArrayList<String>();
		//  Display
		Evaluator.parseDepends(list, m_vo.DisplayLogic);
		//
		if (list.size() > 0 && CLogMgt.isLevelFiner())
		{
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < list.size(); i++)
				sb.append(list.get(i)).append(" ");
			log.finer("(" + m_vo.Name + ") " + sb.toString());
		}
		return list;
	}   //  getDependentOn

	
	/**
	 * Get Boolean Value of Field with columnName.
	 * If there is no column with the given name, the context for current window will be checked.
	 * @param columnName column name
	 * @return boolean value or false if the field was not found
	 * @author Teo Sarca
	 */
	public boolean getValueAsBoolean(String columnName)
	{
		int index = m_mTable.findColumn(columnName);
		if (index != -1)
		{
			Object oo = m_mTable.getValueAt(m_currentRow, index);
			if (oo instanceof String)
				return "Y".equals(oo);
			if (oo instanceof Boolean)
				return ((Boolean)oo).booleanValue();
		}
		return "Y".equals(Env.getContext(m_vo.ctx, m_vo.WindowNo, columnName));
	}	//	isProcessed

	// Validate if the current tab record has changed in database or any parent record
	// Return if there are changes
	public boolean hasChangedCurrentTabAndParents() {
		String msg = null;
		// Carlos Ruiz / globalqss - [ adempiere-Bugs-1985481 ] Processed documents can be edited
		// Validate that current record has not changed and validate that every parent above has not changed
		if (m_mTable.hasChanged(m_currentRow)) {
			// return error stating that current record has changed and it cannot be saved
			msg = Msg.getMsg(Env.getCtx(), "CurrentRecordModified");
			log.saveError("CurrentRecordModified", msg, false);
			return true;
		}
		if (isDetail()) {
			// get parent tab
			// the parent tab is the first tab above with level = this_tab_level-1
			int level = m_vo.TabLevel;
			for (int i = m_window.getTabIndex(this) - 1; i >= 0; i--) {
				MTab parentTab = m_window.getTab(i);
				if (parentTab.m_vo.TabLevel == level-1) {
					// this is parent tab
					if (parentTab.m_mTable.hasChanged(parentTab.m_currentRow)) {
						// return error stating that current record has changed and it cannot be saved
						msg = Msg.getMsg(Env.getCtx(), "ParentRecordModified") + ": " + parentTab.getName();
						log.saveError("ParentRecordModified", msg, false);
						return true;
					} else {
						// search for the next parent
						if (parentTab.isDetail()) {
							level = parentTab.m_vo.TabLevel;
						} else {
							break;
						}
					}
				}
			}
		}
		return false;
	}


	/**
	 * 	Is Processed
	 *	@return true if current record is processed
	 */
	public boolean isProcessed()
	{
		return getValueAsBoolean("Processed");
	}	//	isProcessed

	public boolean isLoadComplete()
	{
		return m_loadComplete;
	}

	protected void fireStateChangeEvent(StateChangeEvent e)
	{
		StateChangeListener[] listeners = m_listenerList.getListeners(StateChangeListener.class);
		if (listeners.length == 0)
			return;
		for(int i = 0; i < listeners.length; i++) {
			listeners[i].stateChange(e);
		}
		
	}

	
	public MTab getParentTab()
	{
		int parentTabNo = getParentTabNo();
		if (parentTabNo < 0 || parentTabNo == m_vo.TabNo)
			return null;
		return m_window.getTab(parentTabNo);
	}

	
	/**
	 * get Parent Tab No
	 * @return Tab No
	 */
	protected int getParentTabNo()
	{
		int tabNo = m_vo.TabNo;
		int currentLevel = m_vo.TabLevel;
		int parentLevel = currentLevel-1;
		if (parentLevel < 0)
			return tabNo;
			while (parentLevel != currentLevel)
			{
				tabNo--;				
				currentLevel = Env.getContextAsInt(m_vo.ctx, m_vo.WindowNo, tabNo, MTab.CTX_TabLevel);
			}
		return tabNo;
	}

	
	/**
	 * 	Tab contains Always Update Field
	 *	@return true if field with always updateable
	 */
	public boolean isAlwaysUpdateField()
	{
		for (int i = 0; i < m_mTable.getColumnCount(); i++)
		{
			MField field = m_mTable.getField(i);
			if (field.isAlwaysUpdateable())
				return true;
		}
		return false;
	}	//	isAlwaysUpdateField

	/**
	 *	Is Query New Record
	 *  @return true if query active
	 */
	public boolean isQueryNewRecord()
	{
		if (m_query != null)
			return m_query.isNewRecordQuery();
		return false;
	}	//	isQueryNewRecord

	
}    // MTab



/*
 *  @(#)MTab.java   02.07.07
 * 
 *  Fin del fichero MTab.java
 *  
 *  Versión 2.2
 *
 */
