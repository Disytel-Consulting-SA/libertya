package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.model.AbstractRetencionProcessor;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MRetSchemaConfig;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class RetencionHonorariosProfesionales extends AbstractRetencionProcessor {

	/** ID de la tasa de impuesto exenta */
	private static Integer taxExenc = 0;
	
	/** [INI] Importe No Imponible. */
	private BigDecimal importeNoImponible = Env.ZERO;
	/** [MR] Importe mínimo a retener en cada pago. */
	private BigDecimal importeMinimoRetencion = Env.ZERO;
	/** [PAA] Importe de pagos anteriores acumulados en el mes. */
	private BigDecimal pagosAnteriores = Env.ZERO;
	/** [RAA] Retenciones anteriores acumuladas (en el mes). */
	private BigDecimal retencionesAnteriores = Env.ZERO;
	/** [BI] Base imponible. */
	private BigDecimal baseImponible = Env.ZERO;      
	/** [ID] Importe determinado. */
	private BigDecimal importeDeterminado = Env.ZERO;
	/** [E] Excedente */
	private BigDecimal excedente = Env.ZERO;
	/** [PE] Porcentaje de Excedente */
	private BigDecimal porcentajeExcedente = Env.ZERO;
	/** [IF] Importe Fijo */
	private BigDecimal importeFijo = Env.ZERO;

	private X_M_Retencion_Invoice retencion = null;
	private List<X_M_Retencion_Invoice> retenciones = null;
	
	@Override
	public void loadConfig(MRetencionSchema retSchema) {
		// Se asigna el esquema de retención a utilizar.
		setRetencionSchema(retSchema);		
		
		// Se obtiene la tasa de impuesto exento para la creación de la nota
		// de crédito.
//		Se comenta esta opción para recuperar el ID del Tax exento, dado que el uso de roles desde acceso WS lo imposibilita.  De todas maneras, no pareciera ser necesario pasar por accesos de perfil para obtener el C_Tax_ID		
//		String sql = MRole.getDefault().addAccessSQL(" SELECT C_Tax_ID FROM C_Tax WHERE isactive = 'Y' AND istaxexempt = 'Y' AND to_country_id IS NULL AND rate = 0.0 ", "C_Tax", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);;
		String sql = " SELECT C_Tax_ID FROM C_Tax WHERE isactive = 'Y' AND istaxexempt = 'Y' AND to_country_id IS NULL AND rate = 0.0 AND AD_Client_ID = " + Env.getContextAsInt(Env.getCtx(), "#AD_Client_ID");
		taxExenc = DB.getSQLValue(null, sql);
		
		// Se obtiene el valor del parámetro Importe No Imponible (INI)
		setImporteNoImponible(
				getParamValueBigDecimal(MRetSchemaConfig.NAME_ImporteNoImponible,
				Env.ZERO));
		
		// Se obtiene el valor del parámetro Mínimo a Retener (MR)
		setImporteMinimoRetencion( 
				getParamValueBigDecimal(MRetSchemaConfig.NAME_MinimoARetener,
				Env.ZERO));
	}

	@Override
	protected BigDecimal calculateAmount() {
		BigDecimal baseImponible = Env.ZERO;		// [BI] Base imponible
		BigDecimal importeDeterminado = Env.ZERO;	// [ID] Importe determinado
		BigDecimal importeRetenido = Env.ZERO;    	// [IR] Importe a retener.
		
		// Se calculan los pagos acumulados y las retenciones aplicadas en el mes.
		calculatePagosMensualAcumulados();
		calculateRetencionesMensualAcumuladas();
		
		// Si los pagos anteriores acumulados son mayores al importe no
		// imponible, entonces no lo tomo a este último
		if(getPagosAnteriores().compareTo(getImporteNoImponible()) > 0){
			setImporteNoImponible(Env.ZERO);
		}
		
		// Se calcula la base imponible. (el monto sujeto a la aplicación de la retención).
		// BI = PAA + EP - INI
		baseImponible = getPagosAnteriores().add(getPayNetAmt()).subtract(getImporteNoImponible());
		
		// Si la base imponible es menor que cero, entonces no hay retención que aplicar y
		// se asigna la base imponible a cero.
		baseImponible = baseImponible.compareTo(Env.ZERO) < 0 ? Env.ZERO : baseImponible;
		
		// Obtener nuevamente las variables a utilizar en base a los posibles rangos
		BigDecimal excedente = getExcedente(baseImponible);
		BigDecimal porcentajeExcedente = getPorcentajeExcedente(baseImponible);
		BigDecimal importeFijo = getImporteFijo(baseImponible);
		BigDecimal minimo = getImporteMinimoRetencion(baseImponible);
		
		// El importe determinado es:
		// ID = IF + ((BI-E)*PE/100)
		importeDeterminado = importeFijo;
		
		importeDeterminado = importeDeterminado.add((baseImponible
				.subtract(excedente)).multiply(porcentajeExcedente).divide(
				Env.ONEHUNDRED, 2, BigDecimal.ROUND_HALF_EVEN));
		
		// Se determina el importe a retener decrementando las retenciones anteriores
		// IR = ID - RAA
		importeRetenido = importeDeterminado.subtract(getRetencionesAnteriores());
		
		// Una vez calculado el importe a retener, se compara con el importe mínimo de
		// retención. Si el IR es menor que el mínimo de retención, entonces no se 
		// debe retener nada.
		// if IR < MR then IR = 0 
		if (importeRetenido.compareTo(minimo) < 0)
			importeRetenido = Env.ZERO;
		
		// Se guardan los montos calculados.
		setImporteDeterminado(importeDeterminado);
		setBaseImponible(baseImponible);
		
		return importeRetenido;
	}
	
	public List<X_M_Retencion_Invoice> save(MAllocationHdr alloc, boolean save) throws Exception {
		// Si el monto de retención es menor o igual que cero, no se debe guardar
		// la retención ya que no se retuvo nada.
		if (getAmount().compareTo(Env.ZERO) <= 0)
			return null;
		
		// Se asigna el allocation header como el actual.
		setAllocationHrd(alloc);
		
		retencion = new X_M_Retencion_Invoice(Env.getCtx(),0,getTrxName());
				
		if(alloc != null){
			retencion.setC_AllocationHdr_ID(getAllocationHrd().getC_AllocationHdr_ID());
		}
	
		MInvoice factura_Recaudador = crearFacturaRecaudador();
		MInvoice credito_proveedor = crearCreditoProveedor();

		retencion.setamt_retenc(getAmount());			
		retencion.setC_RetencionSchema_ID(getRetencionSchema().getC_RetencionSchema_ID());
		retencion.setC_Currency_ID(getCurrency().getC_Currency_ID());
		retencion.setC_Invoice_ID(credito_proveedor.getC_Invoice_ID());			
		retencion.setC_Invoice_Retenc_ID(factura_Recaudador.getC_Invoice_ID());
		retencion.setpagos_ant_acumulados_amt(getPagosAnteriores());
		retencion.setretenciones_ant_acumuladas_amt(getRetencionesAnteriores()); 
		retencion.setpago_actual_amt(getPayNetAmt());
		retencion.setimporte_no_imponible_amt(getImporteNoImponible());
		retencion.setimporte_determinado_amt(getImporteDeterminado());
		retencion.setbaseimponible_amt(getBaseImponible());
		retencion.setIsSOTrx(isSOTrx());
		if (save)
			retencion.save();
	
		retenciones = new ArrayList<X_M_Retencion_Invoice>();
		retenciones.add(retencion);
		
		return retenciones;
	}

	@Override
	public boolean save(MAllocationHdr alloc) throws Exception {
		List<X_M_Retencion_Invoice> retList = save(alloc, false);
		if(Util.isEmpty(retList)){
			return false;
		}
		for (X_M_Retencion_Invoice retInvoice : retList) {
			if(!retInvoice.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
		return true;
	} // save 

	private MInvoice crearFacturaRecaudador() throws Exception {
		/*  Factura */
		MInvoice recaudador_fac = new MInvoice(Env.getCtx(),0,getTrxName());
		Integer nrolinea = 10;

		int locationID = DB.getSQLValue(getTrxName(), " select C_BPartner_Location_ID from C_BPartner_Location where C_BPartner_id = ? ", getRetencionSchema().getC_BPartner_Recaudador_ID());
		
		if (locationID == -1)
            throw new Exception( "@NoCollectorLocation@" );
        
		// Se obtiene el tipo de documento de factura de recaudador
		int docTypeID = getRetencionSchema().getCollectorInvoiceDocType(); 
		if (docTypeID > 0)
			recaudador_fac.setC_DocTypeTarget_ID(docTypeID);
		// Si no existe el tipo de doc específico asigno Factura de Proveedor.
		else
			recaudador_fac.setC_DocTypeTarget_ID(MDocType.DOCBASETYPE_APInvoice);		

		recaudador_fac.setC_BPartner_ID(getRetencionSchema().getC_BPartner_Recaudador_ID());
		recaudador_fac.setDateInvoiced(getDateTrx());
		recaudador_fac.setDateAcct(getDateTrx());
		recaudador_fac.setC_Currency_ID(getCurrency().getC_Currency_ID());
		recaudador_fac.setIsSOTrx(isSOTrx());
		recaudador_fac.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		recaudador_fac.setDocAction(MInvoice.DOCACTION_Complete);
		recaudador_fac.setC_BPartner_Location_ID(locationID);
		recaudador_fac.setCUIT(null);
		recaudador_fac.setPaymentRule(getPaymentRule());
		recaudador_fac.setCurrentAccountVerified(true);
		recaudador_fac.setC_Project_ID(getProjectID());
		recaudador_fac.setC_Campaign_ID(getCampaignID());
		
		char issotrx='N';
		if (recaudador_fac.isSOTrx())
			issotrx = 'Y';
		//Settear M_PriceList
		int priceListID = DB.getSQLValue(getTrxName(), "SELECT M_PriceList_ID FROM M_PriceList pl WHERE pl.issopricelist = '" + issotrx
				+ "' AND (pl.AD_Org_ID = " + recaudador_fac.getAD_Org_ID() + " OR pl.AD_Org_ID = 0) AND pl.C_Currency_ID = " + Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" )
				+ " AND pl.AD_Client_ID = " + getAD_Client_ID() + " AND pl.isActive = 'Y'"
				+ " ORDER BY pl.AD_Org_ID desc,pl.isDefault desc");
		
		if (priceListID <= 0) {
			String iso_code =DB.getSQLValueString(getTrxName(), "SELECT iso_Code FROM C_Currency WHERE C_Currency_ID = ?" , Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" ));
			throw new Exception(Msg.getMsg(Env.getCtx(), "ErrorCreatingCreditDebit", new Object[]{getMsg((recaudador_fac.isSOTrx()?"Purchase":"Sales")), iso_code}));
		}
		recaudador_fac.setM_PriceList_ID(priceListID);
		
		if (!recaudador_fac.save())  
			 throw new Exception( "@CollectorInvoiceSaveError@");
		
		/* Linea de la factura */
		MInvoiceLine fac_linea = new MInvoiceLine(Env.getCtx(),0,getTrxName());
		fac_linea.setC_Invoice_ID(recaudador_fac.getC_Invoice_ID());
		fac_linea.setM_Product_ID(getRetencionSchema().getProduct());
		fac_linea.setLineNetAmt(getPayNetAmt());
		fac_linea.setC_Tax_ID(taxExenc);
		fac_linea.setLine(nrolinea);
		fac_linea.setQty(1);
		fac_linea.setPriceEntered(getAmount());
		fac_linea.setPriceActual(getAmount());
		fac_linea.setC_Project_ID(recaudador_fac.getC_Project_ID());
		if(! fac_linea.save())  
			 throw new Exception( "@CollectorInvoiceLineSaveError@:" + CLogger.retrieveErrorAsString());
		
		/*Completo la factura*/
		recaudador_fac.processIt( DocAction.ACTION_Complete );
		recaudador_fac.save();
		
		return recaudador_fac;
	}
	
	private MInvoice crearCreditoProveedor() throws Exception {
		/*  Nota de Credito al proveedor por el dinero retenido  */
		
		MInvoice credito_prov = new MInvoice(Env.getCtx(),0,getTrxName());
		Integer nrolinea = 10;

		int locationID = DB.getSQLValue(null, " select C_BPartner_Location_ID from C_BPartner_Location where C_BPartner_id = ? ", getBPartner().getC_BPartner_ID());
		if(locationID == -1){
            throw new Exception( "@NoVendorLocation@" );
        }
		
		// Se obtiene el tipo de documento de comprobante de retencion a provvedor
		int docTypeID = getRetencionSchema().getRetencionCreditDocType();
		if (docTypeID > 0)
			credito_prov.setC_DocTypeTarget_ID(docTypeID);
		// Si no existe el tipo de doc específico asigno Abono de Proveedor.
		else
			credito_prov.setC_DocTypeTarget_ID(MDocType.DOCBASETYPE_APCreditMemo);		
		
		credito_prov.setC_BPartner_ID(getBPartner().getC_BPartner_ID());
		credito_prov.setDateInvoiced(getDateTrx());
		credito_prov.setDateAcct(getDateTrx());
		credito_prov.setC_Currency_ID(getCurrency().getC_Currency_ID());
		credito_prov.setIsSOTrx(isSOTrx());
		credito_prov.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		credito_prov.setDocAction(MInvoice.DOCACTION_Complete);
		credito_prov.setC_BPartner_Location_ID(locationID);
		credito_prov.setCUIT(getBPartner().getTaxID());
		credito_prov.setPaymentRule(getPaymentRule());
		credito_prov.setCurrentAccountVerified(true);
		credito_prov.setC_Project_ID(getProjectID());
		credito_prov.setC_Campaign_ID(getCampaignID());
		
		if (getRetencionNumber() != null &&  !getRetencionNumber().trim().equals(""))
			credito_prov.setDocumentNo(getRetencionNumber());
		
		char issotrx='N';
		if (credito_prov.isSOTrx())
			issotrx = 'Y';
		//Settear M_PriceList
		int priceListID = DB.getSQLValue(getTrxName(), "SELECT M_PriceList_ID FROM M_PriceList pl WHERE pl.issopricelist = '" + issotrx
				+ "' AND (pl.AD_Org_ID = " + credito_prov.getAD_Org_ID() + " OR pl.AD_Org_ID = 0) AND pl.C_Currency_ID = " + Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" )
				+ " AND pl.AD_Client_ID = " + getAD_Client_ID() + " AND pl.isActive = 'Y'"
				+ " ORDER BY pl.AD_Org_ID desc,pl.isDefault desc");
		
		if (priceListID <= 0) {
			String iso_code =DB.getSQLValueString(getTrxName(), "SELECT iso_Code FROM C_Currency WHERE C_Currency_ID = ?" , Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" ));
			throw new Exception(Msg.getMsg(Env.getCtx(), "ErrorCreatingCreditDebit", new Object[]{getMsg((credito_prov.isSOTrx()?"Purchase":"Sales")), iso_code}));
		}
		credito_prov.setM_PriceList_ID(priceListID);
		
		if(!credito_prov.save())		   
			throw new Exception("@VendorRetencionDocSaveError@");

		/* Linea de la nota de credito */
		MInvoiceLine cred_linea = new MInvoiceLine(Env.getCtx(),0,getTrxName());
		cred_linea.setC_Invoice_ID(credito_prov.getC_Invoice_ID());
		cred_linea.setM_Product_ID(getRetencionSchema().getProduct());
		cred_linea.setLineNetAmt(getAmount());
		cred_linea.setC_Tax_ID(taxExenc);
		cred_linea.setLine(nrolinea);
		cred_linea.setQty(1);
		cred_linea.setPriceEntered(getAmount());
		cred_linea.setPriceActual(getAmount());
		cred_linea.setC_Project_ID(credito_prov.getC_Project_ID());
		if(!cred_linea.save())		   
			 throw new Exception( "@VendorRetencionDocLineSaveError@");
		
		/*Completo la factura*/
		credito_prov.processIt(DocAction.ACTION_Complete );
		credito_prov.save();
		retencion.setC_InvoiceLine_ID(cred_linea.getC_InvoiceLine_ID());
		
		return credito_prov;
				
	}
	
	
	/**
	 * Calcula el importe total del pagos realizados en el mes al proveedor, teniendo
	 * en cuenta los períodos de excepción. Asigna el resultado al atributo 
	 * <code>pagoAnteriores</code>.	 
	 * @return Retorna un <code>BigDecimal</code> con el importe total pagado.
	 */
	private BigDecimal calculatePagosMensualAcumulados(){
		Timestamp vFecha = Env.getContextAsDate(Env.getCtx(), "#Date");
		Timestamp vDesde = (Timestamp) DB.getSQLObject(getTrxName(),
				"select date_trunc('month',?::timestamp)", new Object[] { vFecha });
		BigDecimal total = getTotalPagosAnteriores(getBPartner(),
				getAD_Client_ID(), vDesde, vFecha, getRetencionSchema());
			
		setPagosAnteriores(total);
		return total;
	}

	/**
	 * Calcula el importe total del retenciones realizadas en el mes al proveedor, 
	 * Asigna el resultado al atributo <code>retencionesAnteriores</code>.	 
	 * @return Retorna un <code>BigDecimal</code> con el importe total retenido.
	 */
	private BigDecimal calculateRetencionesMensualAcumuladas(){
		Timestamp vFecha = Env.getContextAsDate(Env.getCtx(), "#Date");
		Timestamp vDesde = (Timestamp) DB.getSQLObject(getTrxName(),
				"select date_trunc('month',?::timestamp)", new Object[] { vFecha });
		
		BigDecimal total = Env.ZERO;
        String sql;
        
        sql = " SELECT SUM(amt_retenc) as total " + 
              " FROM m_retencion_invoice mri " +
       	      " WHERE EXISTS( SELECT c_invoice_id " +
       	      "               FROM c_invoice ci " +
       	      "               WHERE mri.c_invoice_id = ci.c_invoice_id AND " +
       	      "                     c_bpartner_id = ? AND " +
       	      "                     ci.DocStatus IN ('CO','CL') AND " +
       	      "                     date_trunc('day',dateInvoiced) BETWEEN ?::date AND ?::date)";
		
        PreparedStatement pstmt = null;
        ResultSet rs = null;
		try {
		
			pstmt = DB.prepareStatement(sql, null, true);
			pstmt.setInt(1,getBPartner().getC_BPartner_ID());
			pstmt.setTimestamp(2,vDesde);
			pstmt.setTimestamp(3,vFecha);
			rs = pstmt.executeQuery();
			if (rs.next()){
				if (rs.getBigDecimal("total") != null ){
					total = rs.getBigDecimal("total");
				}
			}
			if(pstmt != null) pstmt.close();
			if(rs != null) rs.close();
		
		} catch (Exception ex) {
			log.info("Error al buscar el total de retenciones acumuladas en el mes !!!! ");
			ex.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
			} catch (SQLException e) {
				log.log( Level.SEVERE,"Cannot close statement or resultset" );
			} 
		}

		
		setRetencionesAnteriores(total);
		return total;
	}
	
	/**
	 * @return Returns the importeMinimoRetencion.
	 */
	public BigDecimal getImporteMinimoRetencion() {
		return importeMinimoRetencion;
	}

	/**
	 * @param importeMinimoRetencion The importeMinimoRetencion to set.
	 */
	protected void setImporteMinimoRetencion(BigDecimal importeMinimoRetencion) {
		this.importeMinimoRetencion = importeMinimoRetencion;
	}
	
	private BigDecimal getImporteMinimoRetencion(BigDecimal baseImponible) {
		if (getImporteMinimoRetencion() == null
				|| getImporteMinimoRetencion().compareTo(Env.ZERO) == 0) {
			setImporteMinimoRetencion(getParamBigDecimal(
					MRetSchemaConfig.NAME_MinimoARetener, Env.ZERO,
					baseImponible));
		}
		return getImporteMinimoRetencion();
	}

	/**
	 * @return Returns the importeNoImponible.
	 */
	public BigDecimal getImporteNoImponible() {
		return importeNoImponible;
	}

	/**
	 * @param importeNoImponible The importeNoImponible to set.
	 */
	protected void setImporteNoImponible(BigDecimal importeNoImponible) {
		this.importeNoImponible = importeNoImponible;
	}

	/**
	 * @return Returns the pagosAnteriores.
	 */
	public BigDecimal getPagosAnteriores() {
		return pagosAnteriores;
	}

	/**
	 * @param pagosAnteriores The pagosAnteriores to set.
	 */
	protected void setPagosAnteriores(BigDecimal pagosAnteriores) {
		this.pagosAnteriores = pagosAnteriores;
	}
	
	/**
	 * @return Returns the retencionesAnteriores.
	 */
	public BigDecimal getRetencionesAnteriores() {
		return retencionesAnteriores;
	}

	/**
	 * @param retencionesAnteriores The retencionesAnteriores to set.
	 */
	protected void setRetencionesAnteriores(BigDecimal retencionesAnteriores) {
		this.retencionesAnteriores = retencionesAnteriores;
	}

	/**
	 * @return Returns the baseImponible.
	 */
	public BigDecimal getBaseImponible() {
		return baseImponible;
	}

	/**
	 * @param baseImponible The baseImponible to set.
	 */
	protected void setBaseImponible(BigDecimal baseImponible) {
		this.baseImponible = baseImponible;
	}

	/**
	 * @return Returns the importeDeterminado.
	 */
	public BigDecimal getImporteDeterminado() {
		return importeDeterminado;
	}

	/**
	 * @param importeDeterminado The importeDeterminado to set.
	 */
	protected void setImporteDeterminado(BigDecimal importeDeterminado) {
		this.importeDeterminado = importeDeterminado;
	}

	private void setExcedente(BigDecimal excedente) {
		this.excedente = excedente;
	}

	private BigDecimal getExcedente() {
		return excedente;
	}
	
	private BigDecimal getExcedente(BigDecimal baseImponible) {
		if (getExcedente() == null || getExcedente().compareTo(Env.ZERO) == 0) {
			setExcedente(getParamBigDecimal(MRetSchemaConfig.NAME_ExcedenteDe,
					Env.ZERO, baseImponible));
		}
		return getExcedente();
	}

	private void setImporteFijo(BigDecimal importeFijo) {
		this.importeFijo = importeFijo;
	}

	private BigDecimal getImporteFijo() {
		return importeFijo;
	}
	
	private BigDecimal getImporteFijo(BigDecimal baseImponible) {
		if (getImporteFijo() == null
				|| getImporteFijo().compareTo(Env.ZERO) == 0) {
			setImporteFijo(getParamBigDecimal(
					MRetSchemaConfig.NAME_ImporteFijo, Env.ZERO, baseImponible));
		}
		return getImporteFijo();
	}

	private void setPorcentajeExcedente(BigDecimal porcentajeExcedente) {
		this.porcentajeExcedente = porcentajeExcedente;
	}

	private BigDecimal getPorcentajeExcedente() {
		return porcentajeExcedente;
	}
	
	private BigDecimal getPorcentajeExcedente(BigDecimal baseImponible) {
		if (getPorcentajeExcedente() == null || getPorcentajeExcedente().compareTo(Env.ZERO) == 0) {
			setPorcentajeExcedente(getParamBigDecimal(
					MRetSchemaConfig.NAME_PorcentajeDelExcedente, Env.ZERO,
					baseImponible));
		}
		return getPorcentajeExcedente();
	}
	
	protected String getMsg(String msg) {
		return Msg.translate(Env.getCtx(), msg);
	}
}
