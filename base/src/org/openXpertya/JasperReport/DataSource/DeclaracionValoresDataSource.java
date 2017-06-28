package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.openXpertya.model.MPreference;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_M_Product;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;


public abstract class DeclaracionValoresDataSource extends QueryDataSource {

	/** Nombre de preference que posee los artículos a filtrar en todos los data source */
	private static final String productValuesFilterPreferenceName = "DeclaracionValores_FilterProductValues";
	
	/** Datos necesarios para los datos */
	private DeclaracionValoresDTO valoresDTO;
	
	/** Contexto */
	private Properties ctx;
	
	/** String con los campos que van en el SELECT de la query */
	private String select;
	
	/** String con los campos del GROUP BY de la query */
	private String groupBy;
	
	/** String con los campos del ORDER BY de la query */
	private String orderBy;
	
	/** Cláusula where adicional que deseen agregar las subclases */
	private String additionalWhereClause;
	
	/** Lista de artículos a filtrar */
	private int[] filterProductIDs = new int[0];
	
	public DeclaracionValoresDataSource(String trxName) {
		super(trxName);
	}
	
	public DeclaracionValoresDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String trxName) {
		super(trxName);
		setCtx(ctx);
		setValoresDTO(valoresDTO);
		setSelect(Util.isEmpty(select, true) ? "*, coalesce(ingreso - egreso,0) as total "
				: select);
		setGroupBy(groupBy);
		setOrderBy(orderBy);
		setAdditionalWhereClause(null);
		initFilteredProducts();
	}
	
	public DeclaracionValoresDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String additionalWhereClause, String trxName) {
		this(ctx, valoresDTO, select, groupBy, orderBy, trxName);
		setAdditionalWhereClause(additionalWhereClause);
	}
	
	protected void initFilteredProducts(){
		String productValues = MPreference.searchCustomPreferenceValue(
				productValuesFilterPreferenceName, Env.getAD_Client_ID(getCtx()),
				Env.getAD_Org_ID(getCtx()), null, true);
		if(!Util.isEmpty(productValues, true)){
			// Iterar por los artículos de filtro
			StringTokenizer tokens = new StringTokenizer(productValues, ";");
			String token;
			StringBuffer whereClause = new StringBuffer("(");
			while(tokens.hasMoreTokens()){
				token = tokens.nextToken();
				whereClause.append("'").append(token).append("',");
			}
			whereClause = new StringBuffer(whereClause.substring(0, whereClause.lastIndexOf(",")));
			whereClause.append(")");
			int[] productIDs = PO.getAllIDs(X_M_Product.Table_Name,
					"ad_client_id = " + Env.getAD_Client_ID(getCtx()) + " AND value IN " + whereClause.toString(),
					getTrxName());
			setFilterProductIDs(productIDs);
		}
	}
	
	protected String getStdSelect(boolean withWhereClause){
		String select = "SELECT " + getSelect()
				+ " FROM "+getDSDataTable()+" as pdv";
		if(withWhereClause){
			select += " WHERE ";
		}
		return select;
	}
	
	protected String getStdWhereClause(boolean withTenderType){
		return getStdWhereClause(withTenderType, null);
	}
	
	protected String getStdWhereClause(boolean withTenderType, String tableAlias){
		return getStdWhereClause(withTenderType, tableAlias, true, true);
	}

	protected String getStdWhereClause(boolean withTenderType, String tableAlias, boolean addDocStatus, boolean withAllocationActive){
		return getStdWhereClause(withTenderType, tableAlias, addDocStatus, withAllocationActive, true);
	}
	
	protected String getStdWhereClause(boolean withTenderType, String tableAlias, boolean addDocStatus, boolean withAllocationActive, boolean withProductFilter){
		StringBuffer stdWhere = new StringBuffer();
		if(!Util.isEmpty(tableAlias, true)){
			tableAlias += ".";
		}
		else{
			tableAlias = "";
		}
		// Modificaciones por nuevas funviews
		// No se agrega como condiciones las siguientes sentencias ya que se
		// agregan en la funviews, siempre y cuando el DS de esta instancia se
		// realice bajo las mismas
		if(!isFunView()){
			if(addDocStatus){
				stdWhere.append(tableAlias).append("docstatus IN ('CO','CL') AND ");
			}
			if(!getValoresDTO().getJournalIDs().isEmpty()){
				stdWhere.append(tableAlias).append("c_posjournal_id IN ").append(getValoresDTO().getJournalIDs().toString()
						.replaceAll("]", ")").replaceAll("\\[", "("));
			}
		}
		if (!Util.isEmpty(getValoresDTO().getUserID(), true)) {
			stdWhere.append(" AND ("+tableAlias+"ad_user_id = ?) ");
		}
		if(withTenderType){
			stdWhere.append(" AND ("+tableAlias+"tendertype IN (").append(getTenderType()).append(")) ");
		}
		if(withAllocationActive){
			stdWhere.append(" AND allocation_active = 'Y' ");
		}
		if(!Util.isEmpty(getAdditionalWhereClause(), true)){
			stdWhere.append(getAdditionalWhereClause());
		}
		// Filtrar documentos que posean los artículos definidos en la
		// preference de corrección de cobranza
		if (withProductFilter && getFilterProductIDs().length > 0
				&& !Util.isEmpty(getFilterProductSetOperator())) {
			stdWhere.append(getInvoiceProductFiltered("c_invoice_id", true));
		}
		return Util.removeInitialAND(stdWhere.toString());
	}
	
	protected Object[] getStdWhereClauseParams(){
		List<Object> params = new ArrayList<Object>();
		if (!Util.isEmpty(getValoresDTO().getUserID(), true)) {
			params.add(getValoresDTO().getUserID());
		}
		return params.toArray();
	}
	
	protected String getStdQuery(boolean withTenderType){
		StringBuffer sql = new StringBuffer(getStdSelect(true));
		sql.append(getStdWhereClause(withTenderType));
		if(!Util.isEmpty(getGroupBy())){
			sql.append(" GROUP BY "+getGroupBy());
		}
		if(!Util.isEmpty(getOrderBy(), true)){
			sql.append(" ORDER BY "+getOrderBy());
		}
		return sql.toString();
	}
	
	/**
	 * @return Suma de todos los registros 
	 */
	public BigDecimal getTotalAmt(){
		StringBuffer sql = new StringBuffer("select sum(total)::numeric(11,2) FROM (");
		sql.append(getQuery());
		sql.append(") as ce ");
		BigDecimal amt = (BigDecimal) DB.getSQLObject(getTrxName(),
				sql.toString(), getParameters());
		return amt == null?BigDecimal.ZERO:amt;
	}
	
	protected String getDSFunView(String funViewName){
		return funViewName+"("+getValoresDTO().getJournalIDsSQLArray()+")";
	}
	
	protected String getDSDataTable(){
		return getDSFunView("c_pos_declaracionvalores_v_filtered");
	}
	
	/**
	 * @return true si la subclase se realiza a partir de una funview o false si
	 *         no se utilizan fun views
	 */
	protected boolean isFunView(){
		return true;
	}
	
	/***
	 * @return el operador (IN o NOT IN) que permite filtrar los comprobantes de
	 *         recupero de payments en base a la preference donde se registran
	 *         los artículos a filtrar
	 */
	protected String getFilterProductSetOperator(){
		return " NOT IN ";
	}
	
	/**
	 * @return Condición con el conjunto de c_invoice_id que poseen los
	 *         artículos a filtrar
	 */
	protected String getInvoiceProductFiltered(String invoiceColumnName, boolean withInitialAnd){
		StringBuffer productFilterWhereClause = new StringBuffer(withInitialAnd?" AND ":"").append(invoiceColumnName);
		productFilterWhereClause.append(getFilterProductSetOperator());
		productFilterWhereClause.append(" (SELECT distinct c_invoice_id FROM c_invoiceline WHERE m_product_id IN ");
		productFilterWhereClause
				.append(Arrays.toString(getFilterProductIDs()).replaceAll("]", ")").replaceAll("\\[", "("));
		productFilterWhereClause.append(") ");
		return productFilterWhereClause.toString();
	}
	
	/**
	 * @return Tipo de pago/cobro de la subclase particular
	 */
	protected abstract String getTenderType();
	
	protected void setValoresDTO(DeclaracionValoresDTO valoresDTO) {
		this.valoresDTO = valoresDTO;
	}

	protected DeclaracionValoresDTO getValoresDTO() {
		return valoresDTO;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	public String getOrderBy(){
		return orderBy;
	}

	protected String getAdditionalWhereClause() {
		return additionalWhereClause;
	}

	protected void setAdditionalWhereClause(String additionalWhereClause) {
		this.additionalWhereClause = additionalWhereClause;
	}

	protected int[] getFilterProductIDs() {
		return filterProductIDs;
	}

	protected void setFilterProductIDs(int[] filterProductIDs) {
		this.filterProductIDs = filterProductIDs;
	}
}
