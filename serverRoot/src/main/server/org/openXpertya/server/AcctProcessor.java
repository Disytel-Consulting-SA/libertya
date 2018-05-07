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



package org.openXpertya.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.acct.Doc;
import org.openXpertya.model.MAcctProcessor;
import org.openXpertya.model.MAcctProcessorLog;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MClient;
import org.openXpertya.model.X_C_AcctProcessorTable;
import org.openXpertya.util.DB;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AcctProcessor extends ServidorOXP {

    /**
     * Constructor de la clase ...
     *
     *
     * @param model
     */

    public AcctProcessor( MAcctProcessor model ) {
        super( model,30 );    // 30 seconds delay
        m_model  = model;
        m_client = MClient.get( model.getCtx(),model.getAD_Client_ID());
    }    // AcctProcessor

    /** Descripción de Campos */

    private MAcctProcessor m_model = null;

    /** Descripción de Campos */

    private StringBuffer m_summary = new StringBuffer();

    /** Descripción de Campos */

    private MClient m_client = null;

    /** Descripción de Campos */

    private MAcctSchema[] m_ass = null;
    
    /**
     * Configuración por tabla y organización
     */
    private Map<Integer, String> tablesConfig;

    /**
     * Descripción de Método
     *
     */

    protected void doWork() {
        m_summary = new StringBuffer();

        // Get Schemata

        if( m_model.getC_AcctSchema_ID() == 0 ) {
            m_ass = MAcctSchema.getClientAcctSchema( getCtx(),m_model.getAD_Client_ID());
        } else {    // only specific accounting schema
            m_ass = new MAcctSchema[]{ new MAcctSchema( getCtx(),m_model.getC_AcctSchema_ID(),null )};
        }

        initTablesConfig();

        postSession();

        //

        int no = m_model.deleteLog();

        m_summary.append( "Logs deleted=" ).append( no );

        //

        MAcctProcessorLog pLog = new MAcctProcessorLog( m_model,m_summary.toString());

        pLog.setReference( "#" + String.valueOf( p_runCount ) + " - " + TimeUtil.formatElapsed( new Timestamp( p_startWork )));
        pLog.save();
    }    // doWork

    /**
     * Inicializa la configuración por tabla
     */
    protected void initTablesConfig(){
		String sql = "SELECT * FROM " + X_C_AcctProcessorTable.Table_Name
				+ " WHERE C_AcctProcessor_ID = ? AND isactive = 'Y'";
		Map<Integer, Map<Integer, String>> ct = new HashMap<Integer, Map<Integer,String>>();
		Map<Integer, String> whereClauses = new HashMap<Integer, String>();
		Map<Integer, String> orgAux;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	Integer tableID, orgID;
    	String where;
    	try {
			ps = DB.prepareStatement(sql);
			ps.setInt(1, m_model.getID());
			rs = ps.executeQuery();
			while(rs.next()){
				tableID = rs.getInt("ad_table_id");
				orgID = rs.getInt("ad_org_id");
				where = rs.getString("whereclause");
				// Por Tabla
				orgAux = ct.get(tableID);
				if(orgAux == null){
					orgAux = new HashMap<Integer, String>();
					ct.put(tableID, orgAux);
				}
				// Por Org
				ct.get(tableID).put(orgID, where);
			}
			
			rs.close();
			ps.close();
			
			// Inicializo las cláusulas where por tabla y org
			StringBuffer buildWhereClause = null;
			String whereForAllOrg;
			String whereClauseForTable;
			String whereFor0;
			for (Integer table : ct.keySet()) {
				whereForAllOrg = " 1=1 ";
				whereFor0 = null;
				buildWhereClause = new StringBuffer(" ( CASE ad_org_id ");
				for (Integer org : ct.get(table).keySet()) {
					if(org.intValue() == 0){
						whereFor0 = " ( "+ct.get(table).get(org)+" ) ";
					}
					else{
						buildWhereClause.append(" WHEN ").append(org).append(" THEN ( ").append(ct.get(table).get(org))
								.append(" ) ");
					}
				}
				buildWhereClause.append(" ELSE ").append(Util.isEmpty(whereFor0, true) ? whereForAllOrg : whereFor0)
						.append(" END ) ");
				
				// Si entra por esta condición, entonces el único where es por
				// org *
				whereClauseForTable = buildWhereClause.toString();
				if(!Util.isEmpty(whereFor0, true) && ct.get(table).size() == 1){
					whereClauseForTable = whereFor0;
				}
				
				whereClauses.put(table, whereClauseForTable.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	setTablesConfig(whereClauses);
    }
    
    /**
     * Descripción de Método
     *
     */

    private void postSession() {
        int[] documents = Doc.documentsTableID;

        for( int i = 0;i < documents.length;i++ ) {
            int    AD_Table_ID = documents[ i ];
            String TableName   = Doc.documentsTableName[ i ];

            // Post only special documents

            if( (m_model.getAD_Table_ID() != 0) && (m_model.getAD_Table_ID() != AD_Table_ID) ) {
                continue;
            }

            // The document to post

            Doc doc = Doc.get( m_ass,AD_Table_ID );

            if( doc == null ) {
                log.severe( getName() + ": No Doc for " + TableName );
                continue;
            }
            
			// Con respecto a cláusulas where, tenemos la posibilidad de agregar
			// en el mismo procesador cuando está configurado por tabla y 
            // en la tabla C_AcctProcessorTable. Se usan las
			// dos para dar soporte a configuraciones anteriores
            String whereTableConfig = getTablesConfig().get(AD_Table_ID);

            // Select id FROM table

            StringBuffer sql = new StringBuffer( "SELECT " );

            sql.append( doc.getTableName()).append( "_ID" );
            sql.append( " FROM " ).append( doc.getTableName());
            sql.append( " WHERE AD_Client_ID=?" );
            sql.append( " AND (0 = ? OR AD_Org_ID = ?) " );
            sql.append( " AND Processed='Y' AND Posted<>'Y' AND IsActive='Y'" );
            
            sql.append(Util.isEmpty(m_model.getWhereClause(), true)?"":" AND "+m_model.getWhereClause());
            sql.append(Util.isEmpty(whereTableConfig, true)?"":" AND "+whereTableConfig);
            
            sql.append( " ORDER BY Created" );

            //

            int               count      = 0;
            int               countError = 0;
            int               id         = 0;
            PreparedStatement pstmt      = null;

            try {
            	pstmt = DB.prepareStatement( sql.toString(), null, true);
                pstmt.setInt( 1,m_model.getAD_Client_ID());
                pstmt.setInt( 2,m_model.getAD_Org_ID());
                pstmt.setInt( 3,m_model.getAD_Org_ID());

                ResultSet rs = pstmt.executeQuery();

                while( !isInterrupted() && rs.next()) {
                    count++;
                    id = rs.getInt( 1 );

                    boolean ok = true;

                    try {
                        ok = doc.post( id,false );    // post no force
                    } catch( Exception e ) {
                        log.log( Level.SEVERE,getName() + ": " + doc.getTableName() + "_ID=" + id,e );
                        ok = false;
                    }

                    if( !ok ) {
                        countError++;
                    }
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,sql.toString(),e );
            }

            if( pstmt != null ) {
                try {
                    pstmt.close();
                } catch( Exception e ) {
                }
            }

            //

            if( count > 0 ) {
                m_summary.append( TableName ).append( "=" ).append( count );

                if( countError > 0 ) {
                    m_summary.append( "(Errors=" ).append( countError ).append( ")" );
                }

                m_summary.append( " - " );
                log.finer( getName() + ": " + m_summary.toString());
            } else {
                log.finer( getName() + ": " + TableName + " - no work" );
            }
        }
    }    // postSession

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerInfo() {
        return "#" + p_runCount + " - Last=" + m_summary.toString();
    }    // getServerInfo


	public Map<Integer, String> getTablesConfig() {
		return tablesConfig;
	}


	public void setTablesConfig(Map<Integer, String> tablesConfig) {
		this.tablesConfig = tablesConfig;
	}
}    // AcctProcessor



/*
 *  @(#)AcctProcessor.java   24.03.06
 * 
 *  Fin del fichero AcctProcessor.java
 *  
 *  Versión 2.2
 *
 */
