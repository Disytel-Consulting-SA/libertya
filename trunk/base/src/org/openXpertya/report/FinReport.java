/**
 *   
 * Codigo original de Indeos Consultoria S.L. para el proyecto OpenXpertya 2007
 * Algunas partes son Copyright  1999-2001 Jorg Janke, Copyright  ComPiere, Inc.
 *  
 */
package org.openXpertya.report;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openXpertya.model.MAcctSchemaElement;
import org.openXpertya.model.MElementValue;
import org.openXpertya.print.MPrintFormat;
import org.openXpertya.print.MPrintFormatItem;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.report.jcalc.Calculator;
import org.openXpertya.report.jcalc.CalculatorException;
import org.openXpertya.report.rfunc.RFunc;
import org.openXpertya.report.rfunc.RFuncException;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ErrorUsuarioOXP;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Trx;

/**
 * 
 * Clase para generar informes Financieros
 */
public class FinReport extends SvrProcess {
	

    /** Descripción de Campos */

    private int p_C_Period_ID = 0;

    /** Descripción de Campos */

    private int p_Org_ID = 0;

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

    private boolean p_DetailsSourceFirst = false;

    /** Descripción de Campos */

    private long m_start = System.currentTimeMillis();

    /** Descripción de Campos */

    private MReport m_report = null;

    /** Descripción de Campos */

    private FinReportPeriod[] m_periods = null;

    /** Descripción de Campos */

    private int m_reportPeriod = -1;

    /** Descripción de Campos */

    private StringBuffer m_parameterWhere = new StringBuffer();

    /** Descripción de Campos */

    private MReportColumn[] m_columns;

    /** Descripción de Campos */

    private CReportLine[] m_lines;
    
    /** Account Date from	*/
    private Timestamp p_acctDate_from;
    
    /** Account Date to	*/
    private Timestamp p_acctDate_to;
    
    
    /** Borrar lineas con todos los valores a 0 */
    private boolean p_deleteLinesWithZero = false;
    
    /** ID del proceso*/
    private int AD_PInstance_ID = 0;
    
    /** ArrayList con las lineas que deben ser re-procesadas	*/
    ArrayList retryCalc = new ArrayList();
    
   	
    protected void prepare() {
        StringBuffer sb = new StringBuffer( "Record_ID=" ).append( getRecord_ID());

        // Parameter

        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_Period_ID" )) {
                p_C_Period_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "Org_ID" )) {
                p_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
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
            } else if( name.equals( "DetailsSourceFirst" )) {
                p_DetailsSourceFirst = "Y".equals( para[ i ].getParameter());   
            } else if (name.equals("DateAcct"))	{ 
            	p_acctDate_from = (Timestamp)para[i].getParameter();
            	p_acctDate_to = (Timestamp)para[i].getParameter_To();
            }else if( name.equals( "DeleteLinesWithZero" )) {
                p_deleteLinesWithZero = "Y".equals( para[ i ].getParameter());   
            }else {
                log.info( "prepare - Unknown Parameter: " + name );
                
            }
        }

        // Optional Org

        if( p_Org_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Org,p_Org_ID ));
        }

        // Optional BPartner

        if( p_C_BPartner_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_BPartner,p_C_BPartner_ID ));
        }

        // Optional Product

        if( p_M_Product_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Product,p_M_Product_ID ));
        }

        // Optional Project

        if( p_C_Project_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Project,p_C_Project_ID ));
        }

        // Optional Activity

        if( p_C_Activity_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Activity,p_C_Activity_ID ));
        }

        // Optional Campaign

        if( p_C_Campaign_ID != 0 ) {
            m_parameterWhere.append( " AND C_Campaign_ID=" ).append( p_C_Campaign_ID );
        }

        // m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(getCtx(),
        // MAcctSchemaElement.ELEMENTTYPE_Campaign, p_C_Campaign_ID));
        // Optional Sales Region

        if( p_C_SalesRegion_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_SalesRegion,p_C_SalesRegion_ID ));
        }

        // Load Report Definition

        m_report = new MReport( getCtx(),getRecord_ID(),null );
        sb.append( " - " ).append( m_report );

        //

        //setPeriods();
        sb.append( " - C_Period_ID=" ).append( p_C_Period_ID ).append( " - " ).append( m_parameterWhere );

        //

        log.info( sb.toString());

        // m_report.list();

    }    // prepare

	
	
	protected String doIt() throws Exception {
		AD_PInstance_ID = getAD_PInstance_ID();
		log.info( " Corriendo: AD_PInstance_ID=" + AD_PInstance_ID);
		
        // Update AcctSchema Balances
        if( p_UpdateBalances ) {
            FinBalance.updateBalance( m_report.getC_AcctSchema_ID(),false );
        }
                
        // ** Get Data     ** Segment Values

        m_columns = m_report.getColumnSet().getColumns();

        if( m_columns.length == 0 ) {
            throw new ErrorUsuarioOXP( "@No@ @PA_ReportColumn_ID@" );
        }

        m_lines = m_report.getLineSet().getLiness();

        if( m_lines.length == 0 ) {
            throw new ErrorUsuarioOXP( "@No@ @PA_ReportLine_ID@" );
        }
        
        // Inicializamos las lineas, estableciendo las que  
        // son valor del segmento.
        try {
        	initLines();
		}
		catch (RuntimeException e)	{ 
        	String error = "Error inicializando las lineas. Abortando la creacion del balance";
        	log.severe(error);
        	
        	throw new ErrorUsuarioOXP(e.toString());
        }
        
        
        //Hacemos los calculos en las lineas
        if (doCalc() == false)	{
        	String error = "Error calculando las lineas. Abortando la creacion del balance.";
        	log.severe(error);
        	return error;
        }
        
        // Si hemos configurado que se oculten las lineas a 0
        if (p_deleteLinesWithZero)	{
        	hideLinesWithZero();
        }
        
        // Guardamos los datos en T_Report
        if (saveReportData() == false)	{
        	return "Error guardando los datos del informe.";
        }
        
        if( Ini.isClient()) {
            getProcessInfo().setTransientObject( getPrintFormat());
        } else {
            getProcessInfo().setSerializableObject( getPrintFormat());
        }

        log.info(( System.currentTimeMillis() - m_start ) + " ms" );

        return "Ok";
    }
	
	
	/**
	 * Inicializa los valores de las columnas para cada linea del informe
	 * y establece el valor para las lineas de tipo valor del segmento
	 * @return
	 */
	private boolean initLines()	{
		for( int line = 0;line < m_lines.length;line++ ) {
			// Inicializamos las columnas de valores
			m_lines [line].initCols(m_columns);
			
            // Line Segment Value (i.e. not calculation)
            if( m_lines[ line ].isLineTypeSegmentValue()) {
            	if (setLineTypeSegmentValue( line ) == false)	{
            		return false;
            	}
            }
        }    // for all lines
		return true;
	}
	
	
	
	/**
	 * Obtiene el saldo de la cuenta indicada en la linea del informe line
	 * @param line
	 * @return
	 */
	private boolean setLineTypeSegmentValue( int line) throws RuntimeException	{
		if( (m_lines[ line ] == null) || (m_lines[ line ].getSources().length == 0) ) {
            log.warning( "No Source lines: " + m_lines[ line ] );
            //return false;
        }
		
		BigDecimal value = Env.ONE.negate();

        // for all columns
        for( int col = 0;col < m_columns.length;col++ ) {

            // Ignore calculation columns

            if( m_columns[ col ].isColumnTypeCalculation()) {
            		
            	MReportColumn oper1 = new MReportColumn(Env.getCtx(),m_columns[ col ].getOper_1_ID(),get_TrxName());
            	MReportColumn oper2 = new MReportColumn(Env.getCtx(),m_columns[ col ].getOper_2_ID(),get_TrxName());
            	BigDecimal value1 = Env.ZERO;
            	BigDecimal value2 = Env.ZERO;
            	int j = 0;
            	boolean found = false;
            	while(j < m_columns.length && !found)
            	{
            		if(m_columns[j].equals(oper1))
            		{
            		   value1 = m_lines[line].getColValue(j);
            		   found = true;
            		}
            		j++;
            	}
                j = 0;
                found = false;
                while(j < m_columns.length && !found)
                {
            		if(m_columns[j].equals(oper2))
            		{
             		   value2 = m_lines[line].getColValue(j);
             		   found = true;
            		}
            		j++;
            	}
                
                String type = m_columns[ col ].getCalculationType();
                if(MReportColumn.CALCULATIONTYPE_AddOp1PlusOp2.equals(type))
            	  value = value1.add(value2);
                else if(MReportColumn.CALCULATIONTYPE_SubtractOp1_Op2.equals(type))
                	value = value1.subtract(value2);
                else if(MReportColumn.CALCULATIONTYPE_PercentageOp1OfOp2.equals(type) && !value2.equals(Env.ZERO))
                	value = value1.multiply(Env.ONEHUNDRED).divide(value2);
            	

                
                	
//            	m_lines [ line ].setColValue(col, value);
//                m_lines [ line ].setProcessed(true);            	
//                continue;
            }
            else
            {

	            StringBuffer info = new StringBuffer();
	
	            info.append( "Line=" ).append( line ).append( ",Col=" ).append( col );
	
	            // SELECT SUM()
	
	            StringBuffer select = new StringBuffer( "SELECT " );
	
	            // Tipo de importe
	            if( m_lines[ line ].getAmountType() != null )    // line amount type overwrites column
	            {
	            	String sql = m_lines[ line ].getSelectClause( true );
	
	                select.append( sql );
	                info.append( ": LineAmtType=" ).append( m_lines[ line ].getAmountType());
	            } else if( m_columns[ col ].getAmountType() != null ) {
	                String sql = m_columns[ col ].getSelectClause( true );
	
	                select.append( sql );
	                info.append( ": ColumnAmtType=" ).append( m_columns[ col ].getAmountType());
	            } else {
	                log.warning( "No Amount Type in line: " + m_lines[ line ] + " or column: " + m_columns[ col ] );
	
	                continue;
	            }
	
	            // Get Period/Date info
	
	            select.append( " FROM Fact_Acct_Balance WHERE 1=1 " );
	
	            // if (p_acctDate_from == null || p_acctDate_to == null)
	            // select.append( " 1=1 " );
	            
	            // Si son null las fechas no hago nada
	            if((this.p_acctDate_from != null) && (this.p_acctDate_to != null)){
	                int relativeOffset = 0;                // current
	
	                // Si tiene un período relativo
	                if( m_columns[ col ].isColumnTypeRelativePeriod()) {
	                    relativeOffset = m_columns[ col ].getRelativePeriod();
	                }
	
	                FinReportPeriod frp = null;
	                
	                if(m_lines[ line ].getAmountType() != null){
	                	// Obtengo el período relativo
	                     frp = this.getPeriod( relativeOffset,(MReportLine)m_lines[ line ]);
	                }
	                else{
	                	// Obtengo el período relativo
	                    frp = this.getPeriod( relativeOffset,(MReportColumn)m_columns[ col ]);
	                }
	                
	            	select.append(" AND DateAcct "+frp.getWhere());
	            }
	                        
	
	            // Line Where
	
	            // Añadimos excepciones para controlar que la carga de los campos el correcta.
	            String s = null;
	            try {
	            
	            	s = m_lines[ line ].getWhereClause();    // (sources, posting type)
	            }
	            catch (RuntimeException e)	{
	            	String error  = "No se ha podido obtener la clausula de la consulta en la linea " +m_lines[ line ].getName() + ":  " + e;
	            	throw new RuntimeException (error);
	            	
	            }
	            
	            if( (s != null) && (s.length() > 0) ) {
	                select.append( " AND " ).append( s );
	            }
	
	            // Report Where
	
	            s = m_report.getWhereClause();
	
	            if( (s != null) && (s.length() > 0) ) {
	                select.append( " AND " ).append( s );
	            }
	
	            // PostingType
	
	            if( !m_lines[ line ].isPostingType())    // only if not defined on line
	            {
	                String PostingType = m_columns[ col ].getPostingType();
	
	                if( (PostingType != null) && (PostingType.length() > 0) ) {
	                    select.append( " AND PostingType='" ).append( PostingType ).append( "'" );
	                }
	            }
	
	            if( m_columns[ col ].isColumnTypeSegmentValue()) {
	                String elementType = m_columns[ col ].getElementType();
	
	                if( MReportColumn.ELEMENTTYPE_Org.equals( elementType )) {
	                    select.append( " AND AD_Org_ID=" ).append( m_columns[ col ].getOrg_ID());
	                } else if( MReportColumn.ELEMENTTYPE_BPartner.equals( elementType )) {
	                    select.append( " AND C_BPartner_ID=" ).append( m_columns[ col ].getC_BPartner_ID());
	                } else if( MReportColumn.ELEMENTTYPE_Product.equals( elementType )) {
	                    select.append( " AND M_Product_ID=" ).append( m_columns[ col ].getM_Product_ID());
	                } else if( MReportColumn.ELEMENTTYPE_Project.equals( elementType )) {
	                    select.append( " AND C_Project_ID=" ).append( m_columns[ col ].getC_Project_ID());
	                } else if( MReportColumn.ELEMENTTYPE_Activity.equals( elementType )) {
	                    select.append( " AND C_Activity_ID=" ).append( m_columns[ col ].getC_Activity_ID());
	                } else if( MReportColumn.ELEMENTTYPE_Campaign.equals( elementType )) {
	                    select.append( " AND C_Campaign_ID=" ).append( m_columns[ col ].getC_Campaign_ID());
	                } else if( MReportColumn.ELEMENTTYPE_LocationFrom.equals( elementType )) {
	                    select.append( " AND C_LocFrom_ID=" ).append( m_columns[ col ].getC_Location_ID());
	                } else if( MReportColumn.ELEMENTTYPE_LocationTo.equals( elementType )) {
	                    select.append( " AND C_LocTo_ID=" ).append( m_columns[ col ].getC_Location_ID());
	                } else if( MReportColumn.ELEMENTTYPE_OrgTrx.equals( elementType )) {
	                    select.append( " AND AD_OrgTrx_ID=" ).append( m_columns[ col ].getOrg_ID());
	                } else if( MReportColumn.ELEMENTTYPE_SalesRegion.equals( elementType )) {
	                    select.append( " AND C_SalesRegion_ID=" ).append( m_columns[ col ].getC_SalesRegion_ID());
	                } else if( MReportColumn.ELEMENTTYPE_Account.equals( elementType )) {
	                	select.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Account,m_columns[ col ].getC_ElementValue_ID()));
	                	//select.append( " AND Account_ID=" ).append( m_columns[ col ].getC_ElementValue_ID());
	                } else if( MReportColumn.ELEMENTTYPE_User1.equals( elementType )) {
	                    select.append( " AND User2_ID=" ).append( m_columns[ col ].getC_ElementValue_ID());
	                } else if( MReportColumn.ELEMENTTYPE_User2.equals( elementType )) {
	                    select.append( " AND User2_ID=" ).append( m_columns[ col ].getC_ElementValue_ID());
	                }
	            }
	
	            // Parameter Where
	
	            select.append( m_parameterWhere );
	            log.info( "Line=" + line + ",Col=" + line + ": " + select );
	            log.info( info.toString());
	            
	            // Ejecutamos la consulta.
	//            BigDecimal value = Env.ONE.negate();
	            PreparedStatement pstmt = null;
	 
	            try	{
	            	pstmt = DB.prepareStatement(select.toString());
	            	ResultSet rs = pstmt.executeQuery();
	            	if (rs.next())	{
	            		value = rs.getBigDecimal(1);
	            	}	
	            }
	            catch (SQLException e)	{
	            	String error = "Error en la linea: " + m_lines[ line ].getName() + ". No se ha podido obtener el valor del segmento: " + e.toString();
	            	log.severe(error);
	            	return false;
	            }
	            finally
	    		{
	    			try
	    			{
	    				if (pstmt != null)
	    					pstmt.close ();
	    			}
	    			catch (Exception e)
	    			{}
	    			pstmt = null;
	    		}
            }
            
            if(m_columns[ col ].isNegativeAsZero() && value.signum() == -1)
            	value = Env.ZERO;
            
            //agregado 30/12/2008 - Horacio Alvarez
            String accountType = m_columns[col].getAccountType();
            if(accountType != null)
            {
            	MReportSource[] sources = m_lines[line].getSources();
            	if(sources != null && sources[0].getC_ElementValue_ID() != 0)
            	{
                	MElementValue element = new MElementValue(Env.getCtx(),sources[0].getC_ElementValue_ID(),get_TrxName());
                	if(!element.getAccountType().equals(accountType))
                		value = Env.ZERO;            		
            	}

            }            
            
            m_lines [ line ].setColValue(col, value);
            m_lines [ line ].setProcessed(true);
        }

        // Update Line Values
	
		return true;
	}
	
	/**
	 * Obtiene la clausula para delimitar los movimientos entre fechas
	 * @return
	 */
	public String getDateWhere()	{
		StringBuffer sql = new StringBuffer( " BETWEEN " );
		sql.append( DB.TO_DATE( p_acctDate_from )).append( " AND " ).append( DB.TO_DATE( p_acctDate_to ));
        return sql.toString();
    }
	
	/**
	 * Elimina los caracteres blancos de la funcion
	 * @param func funcion a modificar
	 * @return la funcion sin blancos
	 */
	public String removeWhites(String func){
		// Saco los blancos de adelante y atras
		String funcAux = func.trim();
		
		// Reemplazo todos los blancos por vacios
		funcAux = funcAux.replaceAll(" ", "");
		
		return funcAux;
	}
	
	/**
	 * Retorna el período relativo al período ingresado como parámetro
	 * @param relativeOffset offset relativo al período parámetro 
	 * @return el período relativo 
	 */
	public FinReportPeriod getPeriod(int relativeOffset,MReportLine line){
		FinReportPeriod periodReturn = null;
		
		if(line.isPeriod()){
			periodReturn = new FinReportPeriodPeriod(this.p_acctDate_from,this.p_acctDate_to,relativeOffset);
		} 
		else if(line.isYear()){
			periodReturn = new FinReportPeriodYear(this.p_acctDate_from,this.p_acctDate_to,relativeOffset);
		}
		else if(line.isTotal()){
			periodReturn = new FinReportPeriodTotal(this.p_acctDate_from,this.p_acctDate_to,relativeOffset);
		}
		
		return periodReturn;
	}
	
	/**
	 * Retorna el período relativo al período ingresado como parámetro
	 * @param relativeOffset offset relativo al período parámetro 
	 * @return el período relativo 
	 */
	public FinReportPeriod getPeriod(int relativeOffset,MReportColumn column){
		FinReportPeriod periodReturn = null;
		
		if(column.isPeriod()){
			periodReturn = new FinReportPeriodPeriod(this.p_acctDate_from,this.p_acctDate_to,relativeOffset);
		} 
		else if(column.isYear()){
			periodReturn = new FinReportPeriodYear(this.p_acctDate_from,this.p_acctDate_to,relativeOffset);
		}
		else if(column.isTotal()){
			periodReturn = new FinReportPeriodTotal(this.p_acctDate_from,this.p_acctDate_to,relativeOffset);
		}
		
		return periodReturn;
	}
	
	/**
	 * Obtiene el formato de impresion para la table T_Record
	 * @TODO: Generar el modelo a partir de los datos, en lugar
	 * y evitar el volcado a la tabla.
	 * @return
	 */ 
	private MPrintFormat getPrintFormat() {
	        int AD_PrintFormat_ID = m_report.getAD_PrintFormat_ID();

	        log.info( "AD_PrintFormat_ID=" + AD_PrintFormat_ID );

	        MPrintFormat pf        = null;
	        boolean      createNew = AD_PrintFormat_ID == 0;

	        // Create New

	        if( createNew ) {
	            int AD_Table_ID = 544;                                        // T_Report

	            pf                = MPrintFormat.createFromTable( Env.getCtx(),AD_Table_ID );
	            AD_PrintFormat_ID = pf.getAD_PrintFormat_ID();
	            m_report.setAD_PrintFormat_ID( AD_PrintFormat_ID );
	            m_report.save();
	        } else {
	            pf = MPrintFormat.get( getCtx(),AD_PrintFormat_ID, true );    // use Cache
	        }

	        // Print Format Sync

	        if( !m_report.getName().equals( pf.getName())) {
	            pf.setName( m_report.getName());
	        }

	        if( m_report.getDescription() == null ) {
	            if( pf.getDescription() != null ) {
	                pf.setDescription( null );
	            }
	        } else if( !m_report.getDescription().equals( pf.getDescription())) {
	            pf.setDescription( m_report.getDescription());
	        }

	        pf.save();
	        log.info( pf + " - #" + pf.getItemCount());

	        // Print Format Item Sync

	        int count = pf.getItemCount();

	        for( int i = 0;i < count;i++ ) {
	            MPrintFormatItem pfi        = pf.getItem( i );
	            String           ColumnName = pfi.getColumnName();

	            //

	            if( ColumnName == null ) {
	                log.severe("No ColumnName for #" + i + " - " + pfi );

	                if( pfi.isPrinted()) {
	                    pfi.setIsPrinted( false );
	                }

	                if( pfi.isOrderBy()) {
	                    pfi.setIsOrderBy( false );
	                }

	                if( pfi.getSortNo() != 0 ) {
	                    pfi.setSortNo( 0 );
	                }
	            } else if( ColumnName.startsWith( "Col" )) {
	                int index = Integer.parseInt( ColumnName.substring( 4 ));

	                if( index < m_columns.length ) {
	                    pfi.setIsPrinted( m_columns[ index ].isPrinted());

	                    String s = m_columns[ index ].getName();

	                    if( !pfi.getName().equals( s )) {
	                        pfi.setName( s );
	                        pfi.setPrintName( s );
	                    }

	                    int seq = 30 + index;

	                    if( pfi.getSeqNo() != seq ) {
	                        pfi.setSeqNo( seq );
	                    }
	                } else    // not printed
	                {
	                    if( pfi.isPrinted()) {
	                        pfi.setIsPrinted( false );
	                    }
	                }

	                // Not Sorted

	                if( pfi.isOrderBy()) {
	                    pfi.setIsOrderBy( false );
	                }

	                if( pfi.getSortNo() != 0 ) {
	                    pfi.setSortNo( 0 );
	                }
	            } else if( ColumnName.equals( "PrintLineNo" )) {
	                if( pfi.isPrinted()) {
	                    pfi.setIsPrinted( false );
	                }

	                if( !pfi.isOrderBy()) {
	                    pfi.setIsOrderBy( true );
	                }
	                
	            } else if( ColumnName.equals( "LevelNo" )) {
	                if( pfi.isPrinted()) {
	                    pfi.setIsPrinted( false );
	                }
	            } 

	            else if( ColumnName.equals( "Description" )) {
	                if( pfi.getSeqNo() != 20 ) {
	                    pfi.setSeqNo( 20 );
	                }

	                if( !pfi.isPrinted()) {
	                    pfi.setIsPrinted( true );
	                }

	                if( pfi.isOrderBy()) {
	                    pfi.setIsOrderBy( false );
	                }

	                if( pfi.getSortNo() != 0 ) {
	                    pfi.setSortNo( 0 );
	                }
	                if( pfi.getMaxWidth() != 400 ) {
	                    pfi.setMaxWidth( 400 );
	                }
	                pfi.setMaxWidth( 0 );
	            } else        // Not Printed, No Sort
	            {
	                if( pfi.isPrinted()) {
	                    pfi.setIsPrinted( false );
	                }

	                if( pfi.isOrderBy()) {
	                    pfi.setIsOrderBy( false );
	                }

	                if( pfi.getSortNo() != 0 ) {
	                    pfi.setSortNo( 0 );
	                }
	            }

	            pfi.save();
	            log.info( pfi.toString());
	        }

	        // set translated to original

	        pf.setTranslation();

	        // First one is unsorted - just re-load

	        if( createNew ) {
	            pf = MPrintFormat.get( getCtx(),AD_PrintFormat_ID,false );    // use Cache
	        }

	        return pf;
	}    // getPrintFormat
	    
	    
	
	/**
	 * Guarda los datos de las lineas en la table T_Report
	 * @TODO Se deberia crear un modelo de datos de impresion.
	 * @return
	 */
	private boolean saveReportData()	{
		String trxName = get_TrxName();
		for( int line = 0;line < m_lines.length;line++ ) {
			
			CReportLine dline = m_lines [ line ];
			
			if (!dline.isPrinted())	{
				continue;
			}
			
			
			MTReport rline = new MTReport(getCtx(), 0, trxName);
			
			rline.setAD_PInstance_ID(AD_PInstance_ID);
			
			rline.setPA_ReportLine_ID(dline.getPA_ReportLine_ID());
			rline.setRecord_ID(getRecord_ID());
			rline.setFact_Acct_ID(0);
			rline.setSeqNo(dline.getSeqNo());
			rline.setName(dline.getName());
			rline.setDescription(dline.getDescription());
			rline.setPrintLineNo(dline.getPrintLineNo());
			rline.setIsBold(dline.isBold());
			rline.setIndentLevel(dline.getIndentLevel());
			rline.setIsPageBreak(dline.isPageBreak());
			
			// Ponemos el valor de todas las columnas, excepto las de descripcion.
			if (!dline.getLineType().equals(dline.LINETYPE_Description))	{
			
				for( int col = 0;col < m_columns.length;col++ ) {
					rline.setColumnValue(col, dline.getColValue(col));
				}
			}
			
			// guardamos la linea del informe
			if (rline.save() == false)	{
				/*try {
					DB.rollback(true, trxName);
					
				}
				catch (SQLException e)	{
					log.severe("No se ha podido anular la transaccion: " + e);
				}*/
				
				
				return false;
			}
			
			/*try {
				DB.commit(true, trxName);
			}
			catch (SQLException e)	{
				log.severe("Error al enviar la transaccion: " + e);
				return false;
			}*/
			
		}
		
		return true;
	}
	
	/**
	 * Realiza los calculos de cada una de las lineas
	 * @return
	 */
	private boolean doCalc()	{
		boolean ok = false;
		for( int line = 0;line < m_lines.length;line++ ) {
			if (m_lines [ line ].getLineType().equals(CReportLine.LINETYPE_Calculation))	{
				if (calcLine(line) == false)	{
					return false;
				}
			}
		}
		
		// Es muy posible que algunas lineas deban procesarse en una segunda vuelta
		// debido a que contengan calculos que afecten a lineas que debido a su orden
		// se procesen despues. Lanzamos retryCal para encargarse de ellas
		
		if (retryCalc() == false)	{
			return false;
		}
		return true;
	}
	
	/**
	 * Se ejecuta despues de hacer los calculos, y efectua el bucle tantas veces como sea necesario
	 * hasta que todas las lineas esten procesadas, o el numero de no procesadas no varie entre pasadas.
	 * @return
	 */
	private boolean retryCalc()	{
		boolean ok = false;
		int retrySize = retryCalc.size();
		
		if (retrySize == 0)	{
			return true;
		}
		while (ok != true)	{
			// Convertimos retryCalc en un array para poder trabajar con el
			Integer[] retry = new Integer[retryCalc.size()]; 
			retryCalc.toArray(retry);
			
			// Reinicializamos retryCalc para guardar los nuevos registros no procesados
			retryCalc = new ArrayList();
			for (int i=0; i< retry.length; i++)	{
				if (calcLine(retry [ i].intValue()) == false)	{
					return false;
				}
			}
			 
			// Si el nuevo array de errores es igual a 0
			// O contiene los mismos elementos que antes, 
			// no hay razon para pasarlo de nuevo
			int newSize = retryCalc.size();
			if ( newSize == 0 ||newSize == retrySize )	{
				ok = true;
			}
			retrySize = newSize;
		}
		
		return true;
	}
	
	/**
	 * Calcula una linea basada en funcion.
	 * @param line
	 * @return
	 */
	private boolean calcLine(int line)	{
		String func = m_lines [ line ].getFunc();
		// Si es de tipo funcion
		if ( func != null && !func.equals(""))	{

			// Hacemos calculos independientes por cada columna
			try	{
				for( int col = 0;col < m_columns.length;col++ ) {

					// Pasamos la funcion por el decodificador para procesarlas
					String decoded = decodeFunc(col, func);
						
					BigDecimal value = calc(decoded);
					m_lines [ line ].setColValue(col, value);
					m_lines [ line ].setProcessed(true);
				}
			}catch (UnprocessedException e)	{
				log.info("Añadiendo linea para re-calcular: " + m_lines[ line ].getDescription());
				retryCalc.add(new Integer(line));
			}
		
		}
	
		return true;
	}
	
	
	/**
	 * Hace las sustituciones de las expresiones entre @ de la expresion func
	 * relativas a la columna col, ejecuta las funciones y devuelve la expresion con los resultados.
	 * @param col
	 * @param func
	 * @return Expresion con los valores sustituidos.
	 * @throws UnprocessedException
	 */
	private String decodeFunc( int col, String func) throws UnprocessedException	{
		log.info("Decoding " + func);
		
		// Sacamos los blancos que hay entre los operandos y el operador
		func = this.removeWhites(func);
		// Buscamos entradas con : para hacer suma de rangos
		String regExp = "@(\\w+:{1}\\w+)@";
		Pattern pattern = Pattern.compile(regExp,Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(func);

		while(matcher.find())	{
			
			for(int i=1;i<=matcher.groupCount();i++)	{
				String group = matcher.group(i);
				String arg1 = group.substring(0,group.indexOf(":"));
				String arg2 = group.substring(group.indexOf(":")+1);
				// Obtenemos el sumatorio de los rangos
				BigDecimal res = getSum(col, arg1, arg2);
				func = matcher.replaceFirst(res.toString());
				matcher=pattern.matcher(func);
			}

		}

		
		// Ahora las sustuciones por nombre 
		regExp = "@(\\w+)@";
		pattern= Pattern.compile(regExp,Pattern.MULTILINE);
		matcher=pattern.matcher(func);

		while(matcher.find())	{	
			for(int i=1;i<=matcher.groupCount();i++)	{
				String group = matcher.group(i);
				int index = getLineIndexByName(group);
				if (index < 0)	{
					log.severe ("Linea no encontrada: " + group);
					return "";
				}
				if (m_lines [ index ].isProcessed() == false)	{
					throw new UnprocessedException();
				}
				BigDecimal res = m_lines [ index ].getColValue(col);
				
				func = matcher.replaceFirst(res.toString());
				matcher=pattern.matcher(func);
			}

		}
		
		
		// Ejecutamos las funciones que hayan metido
		regExp = "[a-zA-Z_-]+ ?\\([a-zA-Z0-9\",' ]*\\)";
		pattern= Pattern.compile(regExp,Pattern.MULTILINE);
		matcher=pattern.matcher(func);

		while(matcher.find())	{
			
			for(int i=0;i<=matcher.groupCount();i++)	{
				String cmdLine = matcher.group(i);
				
		
				String res = execFunc(cmdLine);
				
				func = matcher.replaceFirst(res);
				matcher=pattern.matcher(func);
			}

		}		
		
		return func;
		
	}
	
	private String execFunc(String cmdLine)	{
		RFunc func = RFuncFactory.getRFunc(this, cmdLine);
		BigDecimal res = Env.ZERO;
		
		try {
			func.prepare();
		}
		catch (RFuncException e)	{
			log.severe("Error preparando la funcion: " + e);
			return null;
		}
		
		try	{
			res = func.doIt();
		}
		catch (RFuncException e)	{
			log.severe("Error ejecutando la funcion: " + e);
			return null;
		}		
		
		
		return res.toString();
	}
	
	
	
	/**
	 * Suma todos las lineas comprendidas entre la linea con name=begin
	 * y la linea con name=end de la columna dada.
	 * @param col
	 * @param begin
	 * @param end
	 * @return
	 * @throws UnprocessedException
	 */
	private BigDecimal getSum(int col, String begin, String end) throws UnprocessedException	{
		log.info("getSum: "+begin + " - " +end);
		
		int beg_index = 0;
		int end_index = 0;
		
		// Buscamos las posiciones de las lines de inicio y fin
		// Recordar que se ordena por seqno!!!
		for( int line = 0;line < m_lines.length;line++ ) {
			if (m_lines [ line ].getName().equals(begin))	{
				beg_index = line;
			}
			if (m_lines [ line ].getName().equals(end))	{
				end_index = line;
			}
		}
		
		// Si el inicio y final es 0
		if (end_index == 0 && beg_index == 0)	{
			log.severe("Lineas de inicio o final no encontrada.");
			return Env.ZERO;
		}
		
		// Si el final es menor que el inicio
		if (end_index < beg_index)	{
			log.severe("La posicion de inicio es menor que la de final. Seguramente se trate de un error");
			return Env.ZERO;
		}
		
		// Si el inicio y el final son iguales, establecemos el valor del segmento.
		if (end_index == beg_index)	{
			if (m_lines [ beg_index ].isProcessed() == false)	{
				throw new UnprocessedException();
			}
			log.info("El indice de inicio y final es identico, Devolviendo valor del campo.");
			 
			return m_lines [ beg_index ].getColValue(col);
			
		}
		
		
		// Recorremos estas lineas sumando el valor de cada columna
		BigDecimal res = Env.ZERO;
		for (int line = beg_index; line <= end_index; line ++)	{
			if (m_lines [ line ].isProcessed() == false)	{
				throw new UnprocessedException();
			}
			
			res = res.add(m_lines [ line ].getColValue(col));
		}
		
		return res;
	}
	
	/**
	 * Obtiene el indice de la linea en el array con el nombre dado.
	 * @param name
	 * @return
	 */
	private int getLineIndexByName(String name)	{
		for (int line =0; line < m_lines.length; line++)	{
			if (m_lines [ line ].getName().equals(name))	{
				return line;
			}
		}
		return -1;
	}
	
	/**
	 * Realiza la operacion matematica expresada por la cadena en cuestion.
	 * para ello usa la libreria jcalc.
	 * @param exp
	 * @return
	 */
	private BigDecimal calc(String exp)	{
		log.info("calc: " + exp);
		
		String res = "0";
		
		// Creamos la calculadora
		Calculator cal = new Calculator();
		try	{
			// Y calculamos la expresion.
			res = cal.evaluate_equation(exp);
		}
		catch (CalculatorException e)	{
			log.severe("Fallo al calcular la expresion: " + exp + " | " + e.toString());
			e.printStackTrace();
			return Env.ZERO;
		}
		return new BigDecimal(res);
	}
	
	
	/**
	 * Oculta las lineas que tienen todos los valores impresos a 0
	 *
	 */
	private void hideLinesWithZero()	{
		for(int line=0;line < m_lines.length; line++)	{
			// Queremos que algunas lineas se impriman siempre, esten a 0 o no..
			// y que las descripciones no se oculten
			if (m_lines [ line ].isEverPrinted() 
				|| m_lines [ line ].getLineType().equals(CReportLine.LINETYPE_Description))	{
				continue;
			}
			
			boolean hide = true;
			for (int col = 0; col < m_columns.length; col++)	{
				// Si la columna se debe imprimir
				if (m_columns [ col ].isPrinted())	{
					// Y su valor es distinto de 0
					if (!m_lines [ line ].getColValue(col).equals(Env.ZERO))	{
						hide = false;	// No la ocultes
					}
				}
			}
			// Si se debe ocultar
			if (hide)	{
				m_lines [ line ].setIsPrinted(false);	// Ocultala
			}
		}
	}
	
	
	
}


/**
 * Clase para manejar la excepcion de linea no procesada por dependencias 
 * @author Indeos Consultoria S.L.
 *
 */
class UnprocessedException extends RuntimeException	{
	public UnprocessedException()	{};
	
	public UnprocessedException( String str)	{
		super  (str);
	};
}




