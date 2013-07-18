package org.openXpertya.JasperReport;


public class LaunchDeclaracionValoresXOrg extends LaunchDeclaracionValores {

	protected static String SELECT = " c_pos_id, posname, sum(coalesce(ingreso - egreso,0)) as total ";
	protected static String GROUP_BY = " c_pos_id, posname ";
	protected static String ORDER_BY = " posname ";
	
	public LaunchDeclaracionValoresXOrg() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getTitle(){
		return "Declaracion de Valores por Sucursal";
	}
	
	protected MJasperReport getDeclaracionValoresSubreport() throws Exception{
		return getJasperReport(getCtx(), "DeclaracionValoresXOrg-Subreporte", get_TrxName());
	}
	
	@Override
	protected boolean isLoadCuentaCorrienteDataSource(){
		return false;
	}
	
	@Override
	protected boolean isLoadVoidDocumentsDataSource(){
		return false;
	}
	
	@Override
	protected String getSelect(){
		return SELECT;
	}
	
	@Override
	protected String getGroupBy(){
		return GROUP_BY;
	}
	
	@Override
	protected String getOrderBy(){
		return ORDER_BY;
	}
}
