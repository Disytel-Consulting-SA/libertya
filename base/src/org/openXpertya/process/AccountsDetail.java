package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import org.openXpertya.model.MAcctSchemaElement;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MElementValue;
import org.openXpertya.model.MPeriod;
import org.openXpertya.print.MPrintFormat;
import org.openXpertya.report.FinBalance;
import org.openXpertya.report.MReportTree;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;

public class AccountsDetail extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_AcctSchema_ID = 0;

    /** Descripción de Campos */

    private String p_PostingType = null;

    /** Descripción de Campos */

    private int p_C_Period_ID = 0;

    /** Descripción de Campos */

    private Timestamp p_DateAcct_From = null;

    /** Descripción de Campos */

    private Timestamp p_DateAcct_To = null;

    /** Descripción de Campos */

    private int p_AD_Org_ID = 0;

    /** Descripción de Campos */

    private int p_Account_ID = 0;

    /** Descripción de Campos */

    private int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int p_M_Product_ID = 0;

    /** Descripción de Campos */

    private int p_C_Project_ID = 0;

    /** Descripción de Campos */

    private int p_C_Activity_ID = 0;

    /** Descripción de Campos */

    private int p_C_SalesRegion_ID = 0;

    /** Descripción de Campos */

    private int p_C_Campaign_ID = 0;

    /** Descripción de Campos */

    private boolean p_UpdateBalances = true;

    /** Descripción de Campos */

    private StringBuffer m_parameterWhere = new StringBuffer();

    /** Descripción de Campos */

    private MElementValue m_acct = null;

    /** Descripción de Campos */

    private long m_start = System.currentTimeMillis();

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        StringBuffer sb = new StringBuffer( "Record_ID=" ).append( getRecord_ID());

        // Parameter

        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_AcctSchema_ID" )) {
                p_C_AcctSchema_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "PostingType" )) {
                p_PostingType = ( String )para[ i ].getParameter();
            } else if( name.equals( "C_Period_ID" )) {
                p_C_Period_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DateAcct" )) {
                p_DateAcct_From = ( Timestamp )para[ i ].getParameter();
                p_DateAcct_To   = ( Timestamp )para[ i ].getParameter_To();
            } else if( name.equals( "AD_Org_ID" )) {
                p_AD_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_ElementValue_ID" )) {
                p_Account_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "M_Product_ID" )) {
                p_M_Product_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_Project_ID" )) {
                p_C_Project_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_Activity_ID" )) {
                p_C_Activity_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_SalesRegion_ID" )) {
                p_C_SalesRegion_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_Campaign_ID" )) {
                p_C_Campaign_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "UpdateBalances" )) {
                p_UpdateBalances = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        // Mandatory C_AcctSchema_ID, PostingType

        if (p_C_AcctSchema_ID == 0) 
        	p_C_AcctSchema_ID = MClient.get(getCtx()).getAcctSchema().getC_AcctSchema_ID();
        
        m_parameterWhere.append( "C_AcctSchema_ID=" ).append( p_C_AcctSchema_ID ); 

        // Optional Posting Type

        if( p_PostingType != null )
            m_parameterWhere.append( " AND fa.PostingType = ").append(DB.TO_STRING(p_PostingType));
        
        // Optional Account_ID
        if( p_Account_ID != 0 ) {
            m_parameterWhere.append( " AND fa." ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Account,p_Account_ID ));
        }

        // Optional Org
        if( p_AD_Org_ID != 0 ) {
            m_parameterWhere.append( " AND fa." ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Org,p_AD_Org_ID ));
        }

        // Optional BPartner
        if( p_C_BPartner_ID != 0 ) {
            m_parameterWhere.append( " AND fa." ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_BPartner,p_C_BPartner_ID ));
        }

        // Optional Product
        if( p_M_Product_ID != 0 ) {
            m_parameterWhere.append( " AND fa." ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Product,p_M_Product_ID ));
        }

        // Optional Project
        if( p_C_Project_ID != 0 ) {
            m_parameterWhere.append( " AND fa." ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Project,p_C_Project_ID ));
        }

        // Optional Activity
        if( p_C_Activity_ID != 0 ) {
            m_parameterWhere.append( " AND fa." ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Activity,p_C_Activity_ID ));
        }

        // Optional Campaign
        if( p_C_Campaign_ID != 0 ) {
            m_parameterWhere.append( " AND fa.C_Campaign_ID=" ).append( p_C_Campaign_ID );
        }
        // m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(getCtx(),
        // MAcctSchemaElement.ELEMENTTYPE_Campaign, p_C_Campaign_ID));

        // Optional Sales Region
        if( p_C_SalesRegion_ID != 0 ) {
            m_parameterWhere.append( " AND fa." ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_SalesRegion,p_C_SalesRegion_ID ));
        }

        setDateAcct();
        sb.append( " - DateAcct " ).append( p_DateAcct_From ).append( "-" ).append( p_DateAcct_To );
        sb.append( " - Where=" ).append( m_parameterWhere );
        log.fine( sb.toString());
    }    // prepare


    private void setDateAcct() {

        // Date defined
        if( p_DateAcct_From != null ) {
            if( p_DateAcct_To == null ) {
                p_DateAcct_To = new Timestamp( System.currentTimeMillis());
            }
        
        // Default date is current month
        } else if( p_C_Period_ID == 0 ) {
            GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());

            cal.setTimeInMillis( System.currentTimeMillis());
            cal.set( Calendar.HOUR_OF_DAY,0 );
            cal.set( Calendar.MINUTE,0 );
            cal.set( Calendar.SECOND,0 );
            cal.set( Calendar.MILLISECOND,0 );
            cal.set( Calendar.DAY_OF_MONTH,1 );    // set to first of month
            p_DateAcct_From = new Timestamp( cal.getTimeInMillis());
            cal.add( Calendar.MONTH,1 );
            cal.add( Calendar.DAY_OF_YEAR,-1 );    // last of month
            p_DateAcct_To = new Timestamp( cal.getTimeInMillis());

            // Se actualiza el valor del parámetro DateAcct en la instancia del proceso.
            DB.executeUpdate(
            	" UPDATE AD_PInstance_Para " +
            	" SET p_date = " + DB.TO_DATE(p_DateAcct_From) + ", " +
            	"     p_date_to = " + DB.TO_DATE(p_DateAcct_To) +
            	" WHERE parametername = 'DateAcct' AND AD_PInstance_ID = " + getAD_PInstance_ID());

        // Get Date from Period
        } else {
        
	        String sql = "SELECT StartDate, EndDate FROM C_Period WHERE C_Period_ID=?";
	        PreparedStatement pstmt = null;
	
	        try {
	            pstmt = DB.prepareStatement( sql );
	            pstmt.setInt( 1,p_C_Period_ID );
	
	            ResultSet rs = pstmt.executeQuery();
	
	            if( rs.next()) {
	                p_DateAcct_From = rs.getTimestamp( 1 );
	                p_DateAcct_To   = rs.getTimestamp( 2 );
	            }
	
	            rs.close();
	            pstmt.close();
	            pstmt = null;
	        } catch( Exception e ) {
	            log.log( Level.SEVERE,sql,e );
	        } finally {
	            try { 
	            	if( pstmt != null ) pstmt.close();
	            } catch( Exception e ) {}
	            pstmt = null;
	        }
        }
    }    // setDateAcct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String doIt() {

		// delete all rows older than a week
		DB.executeUpdate("DELETE FROM T_Acct_Detail WHERE Created < ('now'::text)::timestamp(6) - interval '7 days'", get_TrxName());		
		// delete all rows in table with the given ad_pinstance_id
		DB.executeUpdate("DELETE FROM T_Acct_Detail WHERE AD_PInstance_ID = " + getAD_PInstance_ID(), get_TrxName());
    	
    	StringBuffer insertSQL = new StringBuffer( 
        		"INSERT INTO T_Acct_Detail " + 
        		" (UpdateBalances, AD_Client_ID, AD_Org_ID, CreatedBy, UpdatedBy, " +
        		"  AD_PInstance_ID, Fact_Acct_ID, " + 
        		"  DateAcct, C_ElementValue_ID, Acct_Code, Acct_Description, Description, " + 
        		"  AmtAcctDr, AmtAcctCr, Balance, C_BPartner_ID, M_Product_ID, origin_tableName, procedence_id )");

    	
        // Update AcctSchema Balances

        if( p_UpdateBalances ) {
            FinBalance.updateBalance( p_C_AcctSchema_ID,false );
        }

        createBalanceLine(insertSQL);
        createDetailLines(insertSQL);

        return "";
    }    // doIt

    /**
     * Crea la línea de saldo hasta la fecha inicial.
     */
    private void createBalanceLine(StringBuffer insertSQL) {
    	StringBuffer sb = new StringBuffer(insertSQL.toString());
        sb.append( " SELECT " );
        sb.append(        (p_UpdateBalances?"'Y'":"'N'") + ",");
        sb.append(        getAD_Client_ID() + ",");
        sb.append(        p_AD_Org_ID + ", ");
        sb.append(        getAD_User_ID() + "," + getAD_User_ID() + ",");      
        sb.append(        getAD_PInstance_ID() + ", 0, ");
        sb.append(        DB.TO_DATE( p_DateAcct_From,true )).append( ",0, '0'," );
        sb.append(        DB.TO_STRING( Msg.getMsg( Env.getCtx(),"BeginningBalance" )) + ", ");
        sb.append(        DB.TO_STRING( Msg.getMsg( Env.getCtx(),"BeginningBalance" )) + ", ");
        sb.append( "      COALESCE(SUM(AmtAcctDr),0), COALESCE(SUM(AmtAcctCr),0), COALESCE(SUM(AmtAcctDr-AmtAcctCr),0), "); 
        sb.append(        (p_C_BPartner_ID == 0 ? "NULL":p_C_BPartner_ID) + ",");
        sb.append(        (p_M_Product_ID == 0 ? "NULL," :p_M_Product_ID + ","));
        sb.append( "	   NULL,"); //Origen
        sb.append( "	   NULL"); //Procedencia
        sb.append( " FROM Fact_Acct_Balance fa ");
        sb.append( " WHERE " ).append( m_parameterWhere );
        sb.append( "   AND DateAcct < " ).append( DB.TO_DATE( p_DateAcct_From ));

        // Start Beginning of Year

        if( p_Account_ID > 0 ) {
            m_acct = new MElementValue( getCtx(),p_Account_ID,get_TrxName());

            if( !m_acct.isBalanceSheet()) {
                MPeriod first = MPeriod.getFirstInYear( getCtx(),p_DateAcct_From );

                if( first != null ) {
                    sb.append( " AND DateAcct >= " ).append( DB.TO_DATE( first.getStartDate()));
                } else {
                    log.log( Level.SEVERE,"First period not found" );
                }
            }
        }

        int no = DB.executeUpdate( sb.toString(), get_TrxName());
        log.fine( "#" + no + " (Account_ID=" + p_Account_ID + ")" );
        log.finest( sb.toString());
    }    // createBalanceLine

    /**
     * Crea las líneas de detalle del reporte a partir de la tabla fact_acct.
     * Utiliza el <code>p_parameterWhere</code> para filtrar los registros de
     * asientos a mostrar. 
     */
    private void createDetailLines(StringBuffer insertSQL) {
    	StringBuffer sb = new StringBuffer(insertSQL.toString());
        // Obtiene todos los asientos que cumplen con la condición generada a partir de 
        // los parámetros.
        sb.append( " SELECT " );
        sb.append(        (p_UpdateBalances?"'Y'":"'N'") + ",");
        sb.append(        getAD_Client_ID() + ",");
        sb.append(        p_AD_Org_ID + ", ");
        sb.append(        getAD_User_ID() + "," + getAD_User_ID() + ",");      
        sb.append(        getAD_PInstance_ID() + ", ");
        sb.append( "      fa.Fact_Acct_ID," );
        sb.append( "      fa.DateAcct, ev.C_ElementValue_ID, ev.Value, ev.Name, fa.Description,");
        sb.append( "      AmtAcctDr, AmtAcctCr, AmtAcctDr-AmtAcctCr, ");
        sb.append( "      fa.C_BPartner_ID, fa.M_Product_ID, ");
        sb.append( "	  t.name,");
        sb.append( "	  fa.record_id");
        sb.append( " FROM Fact_Acct fa");
        sb.append( " INNER JOIN C_ElementValue ev ON (fa.Account_ID = ev.C_ElementValue_ID) ");
        sb.append( " LEFT JOIN AD_Table_trl t ON (fa.ad_table_id = t.ad_table_id) ");
        sb.append( " WHERE " ).append( m_parameterWhere );
        sb.append( "   AND fa.DateAcct BETWEEN " ).append( DB.TO_DATE( p_DateAcct_From ));
        sb.append( "                       AND " ).append( DB.TO_DATE( p_DateAcct_To ));
        sb.append( "   AND t.ad_language = 'es_AR'");
        
        int no = DB.executeUpdate( sb.toString(), get_TrxName());
        log.fine( "#" + no );
        log.finest( sb.toString());
    }
}    



