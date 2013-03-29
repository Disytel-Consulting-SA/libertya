package org.openXpertya.JasperReport.DataSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class RegisteredDocumentsDataSource implements OXPJasperDataSource {

	/** Datos necesarios para los datos */
	private DeclaracionValoresDTO valoresDTO;
	
	/** Contexto */
	private Properties ctx;
	
	/** Transacción */
	private String trxName;
	
	/** Opción de Filtrado */
	private String filterOption;
	
	/** Documentos registrados */
	private List<RegisteredDocumentDTO> documents;
	
	/** Registro Actual */
	private RegisteredDocumentDTO currentDocument;
	
	/** Total de documentos */
	private int totalDocuments = -1;
	
	/** Índice del Registro actual */
	private int currentRecord = -1;
	
	/** Utilizado para mapear los campos con las invocaciones de los metodos  */
	HashMap<String, String> methodMapper = new HashMap<String, String>(); 
	
	public RegisteredDocumentsDataSource(Properties ctx, DeclaracionValoresDTO valoresDTO, String filterOption, String trxName) {
		setTrxName(trxName);
		setCtx(ctx);
		setValoresDTO(valoresDTO);
		setFilterOption(filterOption);
		setDocuments(new ArrayList<RegisteredDocumentDTO>());
		initMethodMapper();
	}
	
	protected void initMethodMapper(){
		methodMapper.put("DATETRX", "getDateTrx");
		methodMapper.put("C_POS_ID", "getPosID");
		methodMapper.put("POSNAME", "getPosName");
		methodMapper.put("AD_USER_ID", "getUserID");
		methodMapper.put("USERNAME", "getUserName");
		methodMapper.put("DOCTYPENAME", "getDocTypeName");
		methodMapper.put("DOCUMENTNO", "getDocumentNo");
		methodMapper.put("C_BPARTNER_ID", "getBpartnerID");
		methodMapper.put("NOMBRECLI", "getNombreCli");
		methodMapper.put("FISCALALREADYPRINTED", "getFiscalAlreadyPrinted");
		methodMapper.put("GRANDTOTAL", "getGrandTotal");
		methodMapper.put("DOCSTATUS", "getDocStatus");
		methodMapper.put("ISMISSING", "isMissing");
	}

	protected String getWhereClause(String tableAlias){
		StringBuffer whereClause = new StringBuffer();
		if(!Util.isEmpty(tableAlias, true)){
			tableAlias += ".";
		}
		else{
			tableAlias = "";
		}
		// Agregar las cajas diarias
		whereClause
				.append(tableAlias)
				.append("c_posjournal_id IN ")
				.append(getValoresDTO().getJournalIDs().toString()
						.replaceAll("]", ")").replaceAll("\\[", "("));
		// Estado de documentos
		whereClause.append(" AND ").append(tableAlias)
				.append("docstatus NOT IN ('DR','IP') ");
		// Agregar las condiciones dependiendo la opción de filtrado
		if(getFilterOption().equals("N") || getFilterOption().equals("Y")){
			whereClause.append(" AND ");
			whereClause.append(tableAlias);
			whereClause.append("fiscalalreadyprinted = '")
					.append(getFilterOption()).append("'");
		}
		return whereClause.toString();
	}
	
	@Override
	public void loadData() throws Exception {
		// Cargar las facturas dependiendo de las opciones
		StringBuffer sql = new StringBuffer("select date_trunc('day', pj.datetrx) as datetrx, " +
					 "			p.c_pos_id," +
					 "			p.name as posname, " +
					 "			u.ad_user_id," +
					 "			u.name as username, " +
					 "			dt.c_doctype_id, " +
					 "			dt.name as doctypename, " +
					 "			i.documentno, " +
					 "			i.numerocomprobante, " +
					 "			i.puntodeventa, " +
					 "			lc.letra, " +
					 "			bp.c_bpartner_id, " +
					 "			coalesce(i.nombrecli, bp.name) as nombrecli, " +
					 "			i.fiscalalreadyprinted, " +
					 "			(i.grandtotal * signo_issotrx) as grandtotal, " +
					 "			i.docstatus " +
					 "from c_invoice as i " +
					 "inner join c_doctype as dt on dt.c_doctype_id = i.c_doctypetarget_id " +
					 "inner join c_letra_comprobante as lc on lc.c_letra_comprobante_id = i.c_letra_comprobante_id " +
					 "inner join c_bpartner as bp on bp.c_bpartner_id = i.c_bpartner_id " +
					 "inner join c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id " +
					 "inner join c_pos as p on p.c_pos_id = pj.c_pos_id " +
					 "inner join ad_user as u on u.ad_user_id = pj.ad_user_id " +
					 "where ");
		sql.append(getWhereClause("i"));
		sql.append(" order by datetrx, p.name, dt.c_doctype_id, i.documentno ");
		PreparedStatement ps = DB.prepareStatement(sql.toString(), getTrxName());
		ResultSet rs = ps.executeQuery();
		// Corte de control por documentos faltantes
		int old_doctypeID = -1;
		String old_docTypeName = null;
		int old_comprobante = -1;
		int actual_docTypeID, actual_comprobante;
		boolean showMissing = showMissing();
		boolean onlyMissing = showOnlyMissing();
		while (rs.next()) {
			// Verificar si tengo que agregar el faltante
			actual_docTypeID = rs.getInt("c_doctype_id");
			actual_comprobante = rs.getInt("numerocomprobante");
			if(old_doctypeID != actual_docTypeID){
				old_doctypeID = -1;
				old_comprobante = -1;
				old_docTypeName = null;
			}
			// Si el tipo de documento es el mismo que el anterior y el
			// comprobante es diferente al anterior + 1, significa que se pasó
			// en la numeración
			if (showMissing
					&& actual_docTypeID == old_doctypeID
					&& actual_comprobante > (old_comprobante+1)) {
				// Por cada documento faltante entre el siguiente y el actual,
				// meto los faltantes en la lista de documentos
				for (int aux = (old_comprobante+1); aux < actual_comprobante; aux++) {
					getDocuments()
							.add(new RegisteredDocumentDTO(
									getCtx(),
									rs.getDate("datetrx"),
									rs.getInt("c_pos_id"),
									rs.getString("posname"),
									rs.getInt("ad_user_id"),
									null,
									old_docTypeName,
									CalloutInvoiceExt.GenerarNumeroDeDocumento(
											rs.getInt("puntodeventa"), aux,
											rs.getString("letra"), true, false),
									null, null, null, null, "Faltante", true,
									getTrxName()));
				}
			}
			// Agregar el documento siempre y cuando no sea sólo para faltantes
			// Además si se imprimen los faltantes y los no registrados, sólo
			// hay que imprimir el dato si no es impreso fiscal
			if (!onlyMissing
					&& !(getFilterOption().equals("B") && rs.getString(
							"fiscalalreadyprinted").equals("Y"))) {
				getDocuments().add(
						new RegisteredDocumentDTO(getCtx(), rs
								.getDate("datetrx"), rs.getInt("c_pos_id"), rs
								.getString("posname"), rs.getInt("ad_user_id"),
								rs.getString("username"), rs
										.getString("doctypename"), rs
										.getString("documentno"), rs
										.getInt("c_bpartner_id"), rs
										.getString("nombrecli"), rs
										.getString("fiscalalreadyprinted"), rs
										.getBigDecimal("grandtotal"), rs
										.getString("docstatus"), false,
								getTrxName()));
			}
			old_doctypeID = actual_docTypeID;
			old_comprobante = actual_comprobante;
			old_docTypeName = rs.getString("doctypename");
		}
		ps.close();
		rs.close();
		totalDocuments = documents.size();
	}
	
	
	protected boolean showMissing(){
		return !(getFilterOption().equals("Y") || getFilterOption().equals("N"));
	}
	
	protected boolean showOnlyMissing(){
		return getFilterOption().equals("M");
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		String name = null;
		Class<?> clazz = null;
		Method method = null;
		Object output = null;
		try
		{
			// Invocar al metodo segun el campo correspondiente
			name = field.getName().toUpperCase();
		    clazz = Class.forName("org.openXpertya.JasperReport.DataSource.RegisteredDocumentDTO");
		    method = clazz.getMethod(methodMapper.get(name));
		    output = (Object) method.invoke(getCurrentDocument());
		}
		catch (ClassNotFoundException e) { 
			throw new JRException("No se ha podido obtener el valor del campo " + name); 
		}
		catch (NoSuchMethodException e) { 
			throw new JRException("No se ha podido invocar el metodo " + methodMapper.get(name)); 
		}
		catch (InvocationTargetException e) { 
			throw new JRException("Excepcion al invocar el método " + methodMapper.get(name)); 
		}
		catch (Exception e) { 
			throw new JRException("Excepcion general al acceder al campo " + name); 
		}
		return output;
	}

	@Override
	public boolean next() throws JRException {
		currentRecord++;
		
		if (currentRecord >= totalDocuments)	{
			setDocuments(null);
			return false;
		}
		setCurrentDocument(getDocuments().get(currentRecord));
		return true;
	}

	protected DeclaracionValoresDTO getValoresDTO() {
		return valoresDTO;
	}

	protected void setValoresDTO(DeclaracionValoresDTO valoresDTO) {
		this.valoresDTO = valoresDTO;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	protected String getTrxName() {
		return trxName;
	}

	protected void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	protected String getFilterOption() {
		return filterOption;
	}

	protected void setFilterOption(String filterOption) {
		this.filterOption = filterOption;
	}

	protected List<RegisteredDocumentDTO> getDocuments() {
		return documents;
	}

	protected void setDocuments(List<RegisteredDocumentDTO> documents) {
		this.documents = documents;
	}

	protected RegisteredDocumentDTO getCurrentDocument() {
		return currentDocument;
	}

	protected void setCurrentDocument(RegisteredDocumentDTO currentDocument) {
		this.currentDocument = currentDocument;
	}

}
