package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class RetencionManual extends SvrProcess {

	/** ID de la tasa de impuesto exenta */
	private static Integer taxExenc = 0;

	private int AD_Org_ID;
	private int c_bpartner_id;
	private int retencionSchema;
	private BigDecimal montoDeRetencion;
	private BigDecimal baseImponible;
	private BigDecimal porcentajeRetencion;
	private X_M_Retencion_Invoice retencion = null;
	private String documentno;

	@Override
	protected void prepare() {
		String sql = " SELECT C_Tax_ID FROM C_Tax WHERE isactive = 'Y' AND istaxexempt = 'Y' AND to_country_id IS NULL AND rate = 0.0 AND AD_Client_ID = "
				+ Env.getContextAsInt(Env.getCtx(), "#AD_Client_ID");
		taxExenc = DB.getSQLValue(null, sql);
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("AD_Org_ID")) {
				AD_Org_ID = para[i].getParameterAsInt();
			} else if (name.equals("C_BPartner_ID")) {
				c_bpartner_id = para[i].getParameterAsInt();
			} else if (name.equals("C_RetencionSchema_ID")) {
				retencionSchema = para[i].getParameterAsInt();
			} else if (name.equals("MontoDeRetencion")) {
				montoDeRetencion = (BigDecimal) para[i].getParameter();
			} else if (name.equals("MontoBaseImponible")) {
				baseImponible = (BigDecimal) para[i].getParameter();
			} else if (name.equals("PorcentajeDeRetencion")) {
				porcentajeRetencion = (BigDecimal) para[i].getParameter();
			}
		}

	}

	@Override
	protected String doIt() throws Exception {
		if (save())
			return getMsg("RetentionCreated") + " : " + getDocumentNo();
		else
			return getMsg("RetentionNotCreated");
	}

	protected String getMsg(String msg) {
		return Msg.translate(getCtx(), msg);
	}

	/*
	 * private int getNroRetencion() { return DB .getSQLValue(null,
	 * "select c_invoice_id from m_retencion_invoice  order by created DESC LIMIT 1"
	 * ); }
	 */

	public boolean save() throws Exception {
		// Si el monto de retención es menor o igual que cero, no se debe
		// guardar
		// la retención ya que no se retuvo nada.
		if (getMontoDeRetencion().compareTo(Env.ZERO) <= 0)
			return false;

		retencion = new X_M_Retencion_Invoice(Env.getCtx(), 0, get_TrxName());

		MInvoice factura_Recaudador = crearFacturaRecaudador();
		MInvoice credito_proveedor = crearCreditoProveedor();

		retencion.setamt_retenc(getMontoDeRetencion());
		retencion.setC_RetencionSchema_ID(getRetencionSchema()
				.getC_RetencionSchema_ID());
		retencion.setpagos_ant_acumulados_amt(Env.ZERO);
		retencion.setretenciones_ant_acumuladas_amt(Env.ZERO);
		retencion.setpago_actual_amt(Env.ZERO);
		retencion.setimporte_no_imponible_amt(Env.ZERO);
		retencion.setretencion_percent(getPorcentajeRetencion());
		retencion.setimporte_determinado_amt(Env.ZERO);
		retencion.setbaseimponible_amt(getBaseImponible());
		retencion.setIsSOTrx(false);
		retencion.setC_Invoice_ID(credito_proveedor.getC_Invoice_ID());
		retencion.setC_Invoice_Retenc_ID(factura_Recaudador.getC_Invoice_ID());
		retencion.setC_Currency_ID(Env.getContextAsInt(Env.getCtx(),
				("$C_Currency_ID")));
		retencion.setimporte_determinado_amt(getImporteDeterminado());
		retencion.setimporte_no_imponible_amt(getMontoDeRetencion());

		setDocumentNo(factura_Recaudador.getDocumentNo());

		return retencion.save();

	} // save

	private BigDecimal getImporteDeterminado() {
		// Se calcula el importe determinado.
		// ID = BI * T / 100
		return baseImponible.multiply(getPorcentajeRetencion()).divide(
				Env.ONEHUNDRED, 2, BigDecimal.ROUND_HALF_EVEN);
	}

	private void setDocumentNo(String documentNo) {
		this.documentno = documentNo;
	}

	private String getDocumentNo() {
		return this.documentno;
	}

	private BigDecimal getBaseImponible() {
		return baseImponible;
	}

	private BigDecimal getPorcentajeRetencion() {
		return porcentajeRetencion;
	}

	private MRetencionSchema getRetencionSchema() {
		return new MRetencionSchema(getCtx(), retencionSchema, get_TrxName());
	}

	private BigDecimal getMontoDeRetencion() {
		return montoDeRetencion;
	}

	private MInvoice crearFacturaRecaudador() throws Exception {
		/* Factura */
		MInvoice recaudador_fac = new MInvoice(Env.getCtx(), 0, get_TrxName());
		Integer nrolinea = 10;

		int locationID = DB
				.getSQLValue(
						get_TrxName(),
						" select C_BPartner_Location_ID from C_BPartner_Location where C_BPartner_id = ? ",
						getRetencionSchema().getC_BPartner_Recaudador_ID());

		if (locationID == -1)
			throw new Exception("@NoCollectorLocation@");

		int docTypeID = getRetencionSchema().getCollectorInvoiceDocType();
		if (docTypeID > 0)
			recaudador_fac.setC_DocTypeTarget_ID(docTypeID);
		// Si no existe el tipo de doc específico asigno Factura de Proveedor.
		else
			recaudador_fac
					.setC_DocTypeTarget_ID(MDocType.DOCBASETYPE_APInvoice);

		recaudador_fac.setC_BPartner_ID(getRetencionSchema()
				.getC_BPartner_Recaudador_ID());
		recaudador_fac.setDateInvoiced(getDateTrx());
		recaudador_fac.setDateAcct(getDateTrx());
		recaudador_fac.setIsSOTrx(false);
		recaudador_fac.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		recaudador_fac.setDocAction(MInvoice.DOCACTION_Complete);
		recaudador_fac.setC_BPartner_Location_ID(locationID);
		recaudador_fac.setCUIT(null);
		recaudador_fac.setPaymentRule(MInvoice.PAYMENTRULE_Check);
		recaudador_fac.setC_Project_ID(0);
		recaudador_fac.setC_Campaign_ID(0);
		recaudador_fac.setAD_Org_ID(AD_Org_ID);
		
		char issotrx='N';
		if (recaudador_fac.isSOTrx())
			issotrx = 'Y';
		//Settear M_PriceList
		int priceListID = DB.getSQLValue(get_TrxName(), "SELECT M_PriceList_ID FROM M_PriceList pl WHERE pl.issopricelist = '" + issotrx
				+ "' AND (pl.AD_Org_ID = " + recaudador_fac.getAD_Org_ID() + " OR pl.AD_Org_ID = 0) AND pl.C_Currency_ID = " + Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" )
				+ " AND pl.AD_Client_ID = " + getAD_Client_ID() + " AND pl.isActive = 'Y'"
				+ " ORDER BY pl.AD_Org_ID desc,pl.isDefault desc");
		
		if (priceListID <= 0) {
			String iso_code =DB.getSQLValueString(get_TrxName(), "SELECT iso_Code FROM C_Currency WHERE C_Currency_ID = ?" , Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" ));
			throw new Exception(Msg.getMsg(Env.getCtx(), "ErrorCreatingCreditDebit", new Object[]{getMsg((recaudador_fac.isSOTrx()?"Purchase":"Sales")), iso_code}));
		}
		recaudador_fac.setM_PriceList_ID(priceListID);

		if (!recaudador_fac.save())
			throw new Exception("@CollectorInvoiceSaveError@");

		/* Linea de la factura */
		MInvoiceLine fac_linea = new MInvoiceLine(Env.getCtx(), 0,
				get_TrxName());
		fac_linea.setInvoice(recaudador_fac);
		fac_linea.setC_Invoice_ID(recaudador_fac.getC_Invoice_ID());
		fac_linea.setM_Product_ID(getRetencionSchema().getProduct());
		fac_linea.setLineNetAmt(Env.ZERO);
		fac_linea.setC_Tax_ID(0);
		fac_linea.setLine(nrolinea);
		fac_linea.setQty(1);
		fac_linea.setPriceEntered(getMontoDeRetencion());
		fac_linea.setPriceActual(getMontoDeRetencion());
		fac_linea.setC_Project_ID(recaudador_fac.getC_Project_ID());
		fac_linea.setAD_Org_ID(AD_Org_ID);

		if (!fac_linea.save())
			throw new Exception("@CollectorInvoiceLineSaveError@:"
					+ CLogger.retrieveErrorAsString());

		/* Completo la factura */
		recaudador_fac.processIt(DocAction.ACTION_Complete);
		recaudador_fac.save();

		return recaudador_fac;
	}

	private MInvoice crearCreditoProveedor() throws Exception {
		/* Nota de Credito al proveedor por el dinero retenido */

		MInvoice credito_prov = new MInvoice(Env.getCtx(), 0, get_TrxName());
		Integer nrolinea = 10;

		int locationID = DB
				.getSQLValue(
						null,
						" select C_BPartner_Location_ID from C_BPartner_Location where C_BPartner_id = ? ",
						getC_BPartner_ID());
		if (locationID == -1) {
			throw new Exception("@NoVendorLocation@");
		}

		// Se obtiene el tipo de documento de comprobante de retencion a
		// proveedor
		int docTypeID = getRetencionSchema().getRetencionCreditDocType();
		if (docTypeID > 0)
			credito_prov.setC_DocTypeTarget_ID(docTypeID);
		// Si no existe el tipo de doc específico asigno Abono de Proveedor.
		else
			credito_prov
					.setC_DocTypeTarget_ID(MDocType.DOCBASETYPE_APCreditMemo);

		credito_prov.setC_BPartner_ID(getC_BPartner_ID());
		credito_prov.setDateInvoiced(getDateTrx());
		credito_prov.setDateAcct(getDateTrx());
		credito_prov.setIsSOTrx(false);
		credito_prov.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		credito_prov.setDocAction(MInvoice.DOCACTION_Complete);
		credito_prov.setC_BPartner_Location_ID(locationID);
		credito_prov.setPaymentRule(MInvoice.PAYMENTRULE_Check);
		credito_prov.setC_Project_ID(0);
		credito_prov.setC_Campaign_ID(0);
		credito_prov.setAD_Org_ID(AD_Org_ID);
		
		char issotrx='N';
		if (credito_prov.isSOTrx())
			issotrx = 'Y';
		//Settear M_PriceList
		int priceListID = DB.getSQLValue(get_TrxName(), "SELECT M_PriceList_ID FROM M_PriceList pl WHERE pl.issopricelist = '" + issotrx
				+ "' AND (pl.AD_Org_ID = " + credito_prov.getAD_Org_ID() + " OR pl.AD_Org_ID = 0) AND pl.C_Currency_ID = " + Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" )
				+ " AND pl.AD_Client_ID = " + getAD_Client_ID() + " AND pl.isActive = 'Y'"
				+ " ORDER BY pl.AD_Org_ID desc,pl.isDefault desc");
		
		if (priceListID <= 0) {
			String iso_code =DB.getSQLValueString(get_TrxName(), "SELECT iso_Code FROM C_Currency WHERE C_Currency_ID = ?" , Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" ));
			//throw new Exception(Msg.getMsg(Env.getCtx(), "ErrorCreatingCreditDebit", new Object[]{getMsg((recaudador_fac.isSOTrx()?"Purchase":"Sales")), iso_code}));
		}
		credito_prov.setM_PriceList_ID(priceListID);

		if (!credito_prov.save())
			throw new Exception("@VendorRetencionDocSaveError@");

		/* Linea de la nota de credito */
		MInvoiceLine cred_linea = new MInvoiceLine(Env.getCtx(), 0,
				get_TrxName());
		cred_linea.setC_Invoice_ID(credito_prov.getC_Invoice_ID());
		cred_linea.setInvoice(credito_prov);
		cred_linea.setM_Product_ID(getRetencionSchema().getProduct());
		cred_linea.setLineNetAmt(getMontoDeRetencion());
		cred_linea.setC_Tax_ID(taxExenc);
		cred_linea.setLine(nrolinea);
		cred_linea.setQty(1);
		cred_linea.setPriceEntered(getMontoDeRetencion());
		cred_linea.setPriceActual(getMontoDeRetencion());
		cred_linea.setC_Project_ID(credito_prov.getC_Project_ID());
		cred_linea.setAD_Org_ID(AD_Org_ID);
		if (!cred_linea.save())
			throw new Exception("@VendorRetencionDocLineSaveError@");

		/* Completo la factura */
		credito_prov.processIt(DocAction.ACTION_Complete);
		credito_prov.save();
		retencion.setC_InvoiceLine_ID(cred_linea.getC_InvoiceLine_ID());

		return credito_prov;

	}

	private Timestamp getDateTrx() {
		return Env.getContextAsDate(Env.getCtx(), "#Date");
	}

	private int getC_BPartner_ID() {
		return c_bpartner_id;
	}
}
