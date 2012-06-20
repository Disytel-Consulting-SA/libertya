package org.openXpertya.process;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.db.BaseDatosOXP;
import org.openXpertya.model.MTable;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Element;
import org.openXpertya.model.M_Field;
import org.openXpertya.model.M_Tab;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.ErrorOXPSystem;



public class AgregarCamposComponent extends SvrProcess {

	private int p_AD_Table_ID = 0;
	private int p_AD_Tab_ID = 0;
	private int m_count = 0;
	
	private String m_schema;
	
	@Override
	protected String doIt() throws Exception {

		
		Connection       conn    = DB.getConnectionRO();
        BaseDatosOXP     db      = DB.getDatabase();
        DatabaseMetaData md      = conn.getMetaData();
        String           catalog = db.getCatalog();
                         m_schema  = db.getSchema();
		
                         
        ResultSet rsTablas = getTargetTables();
        
        while (rsTablas.next())
        {
	        p_AD_Table_ID = DB.getSQLValue(get_TrxName(), " SELECT AD_Table_ID FROM AD_Table WHERE lower(tablename) = lower(?) ", rsTablas.getString("tablename") );
        
	                         
			M_Table table = new M_Table( getCtx(),p_AD_Table_ID,get_TrxName());
	
	        if( (table == null) || (table.getID() == 0) ) {
	            throw new ErrorOXPSystem( "@NotFound@ @AD_Table_ID@ " + p_AD_Table_ID );
	        }
	
	        String tableName = table.getTableName();
	        
	        if( DB.isOracle()) {
	            tableName = tableName.toUpperCase();
	        }
	        //Modificado por Dataware S.L.
	        else if( DB.isPostgreSQL() ) {
	        	tableName = tableName.toLowerCase();
	        }
	
	        ResultSet rs = md.getColumns( catalog,m_schema,tableName,null );
	       
	        // Importo los campos AD_ComponentVersion_ID y AD_ComponentObjectUID
	        if (addTableColumn( rs,table )) {
	
	        	// Agrego los Campos a la pestañan
		        String sql = "select ad_tab_id from ad_tab where ad_table_id = ?";
		        PreparedStatement pstmt = null;
		    	

			        pstmt = DB.prepareStatement( sql, get_TrxName() );
			        pstmt.setInt( 1,p_AD_Table_ID);
			        	
			        ResultSet rsF = pstmt.executeQuery();
			
			        while( rsF.next()) {
			        	addTabFields(rsF.getInt("ad_tab_id"));
			        }	
			        
			        if( pstmt != null )
			            pstmt.close();
			   
	        }
        }

        return "Se importaron " + m_count + " campos" ;
	}

	@Override
	protected void prepare() {
		
	}

	private void addTabFields(int p_AD_Tab_ID) throws Exception {
	    M_Tab tab = new M_Tab( getCtx(),p_AD_Tab_ID,get_TrxName());
	
	    if( (p_AD_Tab_ID == 0) || (tab == null) || (tab.getID() == 0) ) {
	        throw new ErrorOXPSystem( "@NotFound@: @AD_Tab_ID@ " + p_AD_Tab_ID );
	    }
	
	    log.info( tab.toString());
	
	    //
	
	    int    count = 0;
	    //
	    // ACTUALMENTE ESTA IMPORTANDO TODOS LOS CAMPOS Y SOLO DEBERÍA IMPORTAR AD_COMPONENTOBJECTUID Y AD_COMPONENTVERSION_ID SI EXISTEN 
	    //
	    String sql   = "SELECT * FROM AD_Column c " + "WHERE NOT EXISTS (SELECT * FROM AD_Field f " + "WHERE c.AD_Column_ID=f.AD_Column_ID" + " AND c.AD_Table_ID=?"    // #1
	                   + " AND f.AD_Tab_ID=?)"    // #2
	                   + " AND AD_Table_ID=?"     // #3
	                   + " AND NOT (Name LIKE 'Created%' OR Name LIKE 'Updated%')" + " AND IsActive='Y' " + "ORDER BY Name";
	    PreparedStatement pstmt = null;
	
	        pstmt = DB.prepareStatement( sql, get_TrxName() );
	        pstmt.setInt( 1,tab.getAD_Table_ID());
	        pstmt.setInt( 2,tab.getAD_Tab_ID());
	        pstmt.setInt( 3,tab.getAD_Table_ID());
	
	        ResultSet rs = pstmt.executeQuery();
	
	        while( rs.next()) {
	            M_Column column = new M_Column( getCtx(),rs,get_TrxName());
	
	            //
	
	            M_Field field = new M_Field( tab );
	
	            field.setColumn( column );
	            if (field.getName().equalsIgnoreCase("ad_componentobjectuid")) {
	            	field.setIsDisplayed(false);
	            }
	            
	            if( field.save()) {
					// Se agrega metodo para el traspaso de las traducciones si existen en ad_element_trl
	            	field.exportElement2FieldTranslations(field.getAD_Field_ID(), column.getAD_Column_ID());                	
	                //addLog( 0,null,null,column.getName());
	                count++;
	            }
	        }
	
	        rs.close();
	        if( pstmt != null ) 
	            pstmt.close();
	        pstmt = null;

	}   
	
	private boolean addTableColumn( ResultSet rs,M_Table table ) throws Exception {
        
		boolean newColumn = false;
		
		String v_EntityType = "U";
		
		String tableName = table.getTableName();

        if( DB.isOracle()) {
            tableName = tableName.toUpperCase();
        }
        //Modificado por Dataware S.L.
        else if( DB.isPostgreSQL() ) {
        	tableName = tableName.toLowerCase();
        }
        
        while( rs.next()) {
            String tn = rs.getString( "TABLE_NAME" );

            if( !tableName.equalsIgnoreCase( tn )) {
                continue;
            }

            String   columnName = rs.getString( "COLUMN_NAME" );
            M_Column column     = table.getColumn( columnName );

            if( column != null ) {
                continue;
            }

            String nullable = rs.getString( "IS_NULLABLE" );
            int    size     = rs.getInt( "COLUMN_SIZE" );
                        
            column = new M_Column( table );
            column.setEntityType( v_EntityType );

            //

            M_Element element = M_Element.get( getCtx(),columnName.toUpperCase() );

            if( element == null ) {
                element = new M_Element( getCtx(),columnName,v_EntityType,get_TrxName());
                element.save();
            }

            column.setColumnName( element.getColumnName());
            column.setName( element.getName());
            column.setDescription( element.getDescription());
            column.setHelp( element.getHelp());
            column.setAD_Element_ID( element.getAD_Element_ID());

            //

            column.setIsMandatory( "NO".equals( nullable ));

         // Si termina en _id lo paso a entero
            if( columnName.equalsIgnoreCase("ad_componentversion_id")) {
            	column.setAD_Reference_ID( DisplayType.TableDir );
            }
            else if ( columnName.equalsIgnoreCase("ad_componentobjectuid")) {
                column.setAD_Reference_ID( DisplayType.String );
            }
            else {
            	continue;
            }
            	

            column.setFieldLength( size );
            
            // Done

            if( column.save()) {
            	newColumn = true;
                m_count++;
            }
        }// while
        
        System.out.println("Tabla: " + tableName + " - Total campos importados hasta el momento: " + m_count );
        return newColumn;
	}
	
	/**
	 * Retorna las tablas a las que se les deberán incorporar los nuevos campos 
	 */
	private ResultSet getTargetTables() throws Exception
	{
		// tablas a nivel sql que contienen los campos AD_ComponentVersion_ID y AD_ComponentObjectUID
		String queryTables = 	" SELECT t.tablename " +
								" FROM pg_tables t  " +
								" INNER JOIN pg_class c on c.relname = t.tablename " +
								" WHERE schemaname = '" + m_schema + "' " +
								" AND c.oid IN " +
								" ( " +
								"	SELECT DISTINCT attrelid " + 
								"	FROM pg_attribute " +
								"	WHERE attname ilike 'AD_ComponentVersion_ID' OR attname ilike 'AD_ComponentObjectUID' " +
								" ) ";
		
		PreparedStatement stmt = DB.prepareStatement(queryTables);
		ResultSet rs = stmt.executeQuery();
		
		return rs;
		
	}
	
}
