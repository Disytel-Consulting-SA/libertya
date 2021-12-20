package org.openXpertya.process;

import java.util.List;
import java.util.Map;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLetraComprobante;
import org.openXpertya.model.MPOS;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MPOSLetter;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class RejectedCheckSalesTrx extends RejectedCheckTrxBuilder {

	public RejectedCheckSalesTrx() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getDocTypePreferenceName() {
		return REJECTED_CHECK_DOCTYPEKEY_PREFIX_PREFERENCE_NAME + "_Sales";
	}
	
	@Override
	public MDocType getDocType(MInvoice invoice) throws Exception {
		MDocType documentType = null;
		if(LOCALE_AR_ACTIVE){
			String docTypeKey = MDocType.DOCTYPE_CustomerDebitNote;
			// Letra
			MLetraComprobante letra = getLetraComprobante(invoice);
			invoice.setC_Letra_Comprobante_ID(letra.getID());
			// Punto de Venta
			// Se obtiene el tipo de documento a crear
			// Obtener el punto de venta:
			// 1) Desde la caja diaria, priorizando la personalización de
			// punto de venta por letra de la config del tpv asociada a ella
			// 2) Desde la config de TPV, si es que posee una sola,
			// priorizando la personalización de punto de venta por letra
			Integer ptoVenta = null;
			// 1)
			if(MPOSJournal.isActivated()){
				ptoVenta = MPOSJournal.getCurrentPOSNumber(letra.getLetra());
			}
			// 2)
			if(Util.isEmpty(ptoVenta, true)){
				List<MPOS> pos = MPOS.get(invoice.getCtx(),
						Env.getAD_Org_ID(invoice.getCtx()),
						Env.getAD_User_ID(invoice.getCtx()), invoice.get_TrxName());
				if(pos.size() == 1){
					Map<String, Integer> letters = MPOSLetter.getPOSLetters(pos.get(0).getID(), invoice.get_TrxName());
					ptoVenta = letters.get(letra.getLetra()) != null ? letters.get(letra.getLetra())
							: pos.get(0).getPOSNumber();
				}
			}
			// Se obtiene el tipo de documento para la factura.
			if(Util.isEmpty(ptoVenta, true)){
				throw new Exception(Msg.getMsg(invoice.getCtx(), "CanGetPOSNumber"));
			}
			documentType = MDocType.getDocType(invoice.getCtx(),
					invoice.getAD_Org_ID(), docTypeKey, letra.getLetra(),
					ptoVenta, invoice.get_TrxName());
			if (documentType == null) {
				throw new Exception(Msg.getMsg(invoice.getCtx(),
						"NonexistentPOSDocType", new Object[] { letra,
						ptoVenta }));
			}
			invoice.setPuntoDeVenta(ptoVenta);
		}
		return documentType;
	}
	
	/**
	 * Obtener la letra del comprobante en base a la EC y la compañía
	 * 
	 * @param débito
	 * @return la letra del comprobante
	 * @throws Exception
	 */
	protected MLetraComprobante getLetraComprobante(MInvoice invoice) throws Exception{
		MBPartner bp = new MBPartner(invoice.getCtx(), invoice.getC_BPartner_ID(), invoice.get_TrxName());
		Integer categoriaIVAclient = CalloutInvoiceExt.darCategoriaIvaClient();
		Integer categoriaIVACustomer = bp.getC_Categoria_Iva_ID();
		// Se validan las categorias de IVA de la compañia y el cliente.
		if (categoriaIVAclient == null || categoriaIVAclient == 0) {
			throw new Exception(Msg.getMsg(invoice.getCtx(), "ClientWithoutIVAError"));
		} else if (categoriaIVACustomer == null || categoriaIVACustomer == 0) {
			throw new Exception(Msg.getMsg(invoice.getCtx(), "BPartnerWithoutIVAError"));
		}
		// Se obtiene el ID de la letra del comprobante a partir de las categorias de IVA.
		Integer letraID = CalloutInvoiceExt.darLetraComprobante(categoriaIVACustomer, categoriaIVAclient);
		if (letraID == null || letraID == 0){
			throw new Exception(Msg.getMsg(invoice.getCtx(), "LetraCalculationError"));
		}
		// Se obtiene el PO de letra del comprobante.
		return new MLetraComprobante(invoice.getCtx(), letraID, invoice.get_TrxName());
	}

}
