/*
 * @(#)MDocType.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

// import org.apache.catalina.startup.SetContextPropertiesRule;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 *      Document Type Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MDocType.java,v 1.15 2005/03/11 20:28:34 jjanke Exp $
 */
public class MDocType extends X_C_DocType {

    /** Cache */
    static private CCache	s_cache	= new CCache("C_DocType", 20);

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MDocType.class);
    
    /** Maintenance Order = MOF (Pedido de Mantenimiento) */
    public static final String DOCTYPE_MaintenanceOrder = "MOF";
    /** Manufacturing Order Issue = MOI (Asunto de Pedido de Manufactura) */
    public static final String DOCTYPE_ManufacturingOrderIssue = "MOI";
    /** Manufacturing Order Method Variation  = MOM (Variacion de Metodo de Pedido de Manufactura) */
    public static final String DOCTYPE_ManufacturingOrderMethodVariation = "MOM";
    /** Manufacturing Order = MO (Pedido de Manufactura) */
    public static final String DOCTYPE_ManufacturingOrder = "MO";
    /** Manufacturing Order Planning = MOP (Planificacion de pedido de Manufactura) */
    public static final String DOCTYPE_ManufacturingOrderPlanning = "MOP";
    /** Manufacturing Order Receipt = MOR (Albaran de Pedido de Manufactura)*/
    public static final String DOCTYPE_ManufacturingOrderReceipt = "MOR";
    /** Manufacturing Order Use Variation  = MOU (Variacion de uso de Pedido de Manufactura) */
    public static final String DOCTYPE_ManufacturingOrderUseVariation = "MOU";
    /** Manufacturing Order Rate Variation  = MOV (Variacion de Tasa de Pedido de Manufactura) */
    public static final String DOCTYPE_ManufacturingOrderRateVariation = "MOV";
    /** Purchase Requisition Planning = PRP (Planificacion de Aviso de Pedido de Material) */
    public static final String DOCTYPE_PurchaseRequisitionPlanning = "PRP";
    /** GL Journal = GLJ (Diario del Mayor) */
    public static final String DOCTYPE_GLJournal = "GLJ";
    /** GL Journal Batch = GLJB (Lote de Asientos) */
    public static final String DOCTYPE_GLJournalBatch = "GLJB";
    /** GL Document = GLD (Documento GL) */
    public static final String DOCTYPE_GLDocument = "GLD";
    /** Customer Invoice = CI (Factura de Cliente) */
    public static final String DOCTYPE_CustomerInvoice = "CI";
    /** Customer Indirect Invoice = CII (Factura de Cliente Indirecta) */
    public static final String DOCTYPE_CustomerIndirectInvoice = "CII";
    /** Customer ProForma Invoice = ARF (Factura ProForma de Cliente) */
    public static final String DOCTYPE_CustomerProFormaInvoice = "ARF";
    /** Customer Credit Memo = CCM (Abono de Cliente) */
    public static final String DOCTYPE_CustomerCreditMemo = "CCM";
    /** Customer Debit Note = CDN (Nota de Débito de Cliente) */
    public static final String DOCTYPE_CustomerDebitNote = "CDN";
    /** Customer Credit Note = CCN (Nota de Crédito de Cliente) */
    public static final String DOCTYPE_CustomerCreditNote = "CCN";
    /** Vendor Invoice = VI (Factura de Proveedor) */
    public static final String DOCTYPE_VendorInvoice = "VI";
    /** Vendor Credit Memo = VCM (Abono de Proveedor) */
    public static final String DOCTYPE_VendorCreditMemo = "VCM";
    /** Match Invoice = MXI (Corresponder Factura) */
    public static final String DOCTYPE_MatchInvoice = "MXI";
    /** Customer Receipt = CR (Cobro a Cliente) */
    public static final String DOCTYPE_CustomerReceipt = "CR";
    /** Vendor Payment = VP (Pago a Proveedor) */
    public static final String DOCTYPE_VendorPayment = "VP";
    /** Payment Allocation = PAL (Asignacíon) */
    public static final String DOCTYPE_PaymentAllocation = "PAL";
    /** Material Delivery: MMS (Albarán de Salida) */
    public static final String DOCTYPE_MaterialDelivery = "MMS";
    /** Material Indirect Delivery: MIS (Albarán de Salida Indirecto) */
    public static final String DOCTYPE_MaterialIndirectDelivery = "MIS";
    /** Material Receipt: MMR (Albarán de Entrada) */
    public static final String DOCTYPE_MaterialReceipt = "MMR";
    /** Customer Material Return: CMR (Devolución de Cliente) */
    public static final String DOCTYPE_CustomerMaterialReturn = "CMR";
    /** Purchase Order: POO (Pedido a Proveedor) */
    public static final String DOCTYPE_PurchaseOrder = "POO";
    /** Match PO: MXP (Corresponer PP) */
    public static final String DOCTYPE_MatchPO = "MXP";
    /** Purchase Requisition = POR (Aviso de Pedido de Material) */
    public static final String DOCTYPE_PurchaseRequisition = "POR";
    /** Bank Statement = CMB (Extracto Bancario) */
    public static final String DOCTYPE_BankStatement = "CMB";
    /** Cash Journal = CMC (Diario de Caja) */
    public static final String DOCTYPE_CashJournal = "CMC";
    /** Material Movement = MMM (Movimiento de Material) */
    public static final String DOCTYPE_MaterialMovement = "MMM";
    /** Material Physical Inventory = MMI (Inventario Fisico) */
    public static final String DOCTYPE_MaterialPhysicalInventory = "MMI";
    /** Simple Material InOut = SMIO (Ingreso/Egreso Simple) */
    public static final String DOCTYPE_SimpleMaterialInOut = "SMIO";
    /** Material Production = MMP (Producción) */
    public static final String DOCTYPE_MaterialProduction = "MMP";
    /** Project Issue = PJI (Asunto de Proyecto)*/
    public static final String DOCTYPE_ProjectIssue = "PJI";
    /** Quotation: SOOB (Presupuesto en Firme) */
    public static final String DOCTYPE_Quotation = "SOOB";
    /** Proposal: SOON (Presupuesto) */
    public static final String DOCTYPE_Proposal = "SOON";
    /** Prepay Order: SOPR (Pedido Prepago) */
    public static final String DOCTYPE_PrepayOrder = "SOPR";
    /** Return Material: SORM (RMA) */
    public static final String DOCTYPE_ReturnMaterial = "SORM";
    /** Standar Order: SOSO (Pedido) */
    public static final String DOCTYPE_StandarOrder = "SOSO";
    /** On Credit Order: SOWI (Pedido a Credito) */
    public static final String DOCTYPE_OnCreditOrder = "SOWI";
    /** Warehouse Order: SOWP (Pedido de Almacén) */
    public static final String DOCTYPE_WarehouseOrder = "SOWP";
    /** POS Order: SOWR (Ticket TPV) */
    public static final String DOCTYPE_POSOrder = "SOWR";
    /** Retencion Receipt: RTR (Comprobante de Retención a proveedor) */
    public static final String DOCTYPE_Retencion_Receipt = "RTR";
    /** Retencion Invoice: RTI (Factura de Retención del Fisco) */
    public static final String DOCTYPE_Retencion_Invoice = "RTI";
    /** Retencion Receipt (Customer): RCR (Comprobante de Retención de Cliente) */
    public static final String DOCTYPE_Retencion_ReceiptCustomer = "RCR";
    /** Retencion Invoice (Customer): RCI (Factura de Retención del Fisco (Cliente)) */
    public static final String DOCTYPE_Retencion_InvoiceCustomer = "RCI";

    /** Incoming Bank Transfer = IBT (Transferencia Entrante) */
    public static final String DOCTYPE_IncomingBankTransfer = "IBT";
    /** Outgoing Bank Transfer = OBT (Transferencia Saliente) */
    public static final String DOCTYPE_OutgoingBankTransfer = "OBT";
    
    /** Deposit Receipt = DRC (Boleta de Depósito) */
    public static final String DOCTYPE_DepositReceipt = "DRC";
    
    
    /** Parte de Movimientos */
    public static final String DOCTYPE_ParteDeMovimientos = "PDM";
    /** Parte de Movimientos Valorizados */
    public static final String DOCTYPE_ParteDeMovimientosValorizados = "PDMV";
    
    /** Customer Return = DC (Devolución de Cliente) */
    public static final String DOCTYPE_CustomerReturn = "DC";
    
    /** Vendor Return = VR (Devolución de Proveedor) */
    public static final String DOCTYPE_VendorReturn = "VR";
    
    /** Customer Tender = SOTR (Devolución de Proveedor) */
    public static final String DOCTYPE_CustomerTender = "SOTR";
    
    /** Amortization = AMO (Amortización) */
    public static final String DOCTYPE_Amortization = "AMO";
    
    /** Saldo Inicial Proveedor = SIP (Saldo Inicial Proveedor) */
    public static final String DOCTYPE_Saldo_Inicial_Proveedor = "SIP";
    
    /** Saldo Inicial Cliente = SIC (Saldo Inicial Cliente) */
    public static final String DOCTYPE_Saldo_Inicial_Cliente = "SIC";
    
    /** Saldo Inicial Proveedor = SIP (Saldo Inicial Proveedor Crédito) */
    public static final String DOCTYPE_Saldo_Inicial_Proveedor_Credito = "SIPC";
    
    /** Saldo Inicial Cliente = SIC (Saldo Inicial Cliente Crédito) */
    public static final String DOCTYPE_Saldo_Inicial_Cliente_Credito = "SICC";
    
    /** Pedido Transferible = SOSOT (Pedido Transferible) */
    public static final String DOCTYPE_Pedido_Transferible = "SOSOT";
    
    /** Pedido Transferido = SOSOTD (Pedido Transferido) */
    public static final String DOCTYPE_Pedido_Transferido = "SOSOTD";
    
    /** Recibo de Cliente = CRSEC01 (Recibo de Cliente) */
    public static final String DOCTYPE_Recibo_De_Cliente = "CRSEC01";
    
    /** Orden de Pago = POSEC01 (Orden de Pago) */
    public static final String DOCTYPE_Orden_De_Pago = "POSEC01";
    
    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param C_DocType_ID id
     * @param trxName
     */
    public MDocType(Properties ctx, int C_DocType_ID, String trxName) {

        super(ctx, C_DocType_ID, trxName);

        if (C_DocType_ID == 0) {

            // setName (null);
            // setPrintName (null);
            // setDocBaseType (null);
            // setGL_Category_ID (0);
            setDocumentCopies(0);
            setHasCharges(false);
            setIsDefault(false);
            setIsDocNoControlled(false);
            setIsSOTrx(false);
            setIsPickQAConfirm(false);
            setIsShipConfirm(false);
            setIsSplitWhenDifference(false);
            
            //
            setIsCreateCounter(true);
            setIsDefaultCounterDoc(false);
        }

    }		// MDocType

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MDocType(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MDocType

    /**
     *      New Constructor
     *      @param ctx context
     *      @param DocBaseType document base type
     *      @param Name name
     * @param trxName
     */
    public MDocType(Properties ctx, String DocBaseType, String Name, String trxName) {

        this(ctx, 0, trxName);
        setAD_Org_ID(0);
        setDocBaseType(DocBaseType);
        setName(Name);
        setPrintName(Name);
        setGL_Category_ID();

    }		// MDocType

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MDocType[");

        sb.append(getID()).append("-").append(getName()).append(",DocNoSequence_ID=").append(getDocNoSequence_ID()).append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Document Type (cached)
     *      @param ctx context
     *      @param C_DocType_ID id
     *      @return document type
     */
    static public MDocType get(Properties ctx, int C_DocType_ID) {
    	return get(ctx, C_DocType_ID, null);
    }		// get

    
    /**
     *      Get Document Type (cached)
     *      @param ctx context
     *      @param C_DocType_ID id
     *      @return document type
     */
    static public MDocType get(Properties ctx, int C_DocType_ID, String trxName) {
        Integer		key		= new Integer(C_DocType_ID);
        MDocType	retValue	= (MDocType) s_cache.get(key);
        if (retValue == null) {
            retValue	= new MDocType(ctx, C_DocType_ID, trxName);
            s_cache.put(key, retValue);
        }
        return retValue;
    }		// get
    
    /**
     *      Get Client Document Types
     *      @param ctx context
     *      @return array of doc types
     */
    static public MDocType[] getOfClient(Properties ctx, String trxName) {

        ArrayList		list	= new ArrayList();
        String			sql	= "SELECT * FROM C_DocType WHERE AD_Client_ID=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, trxName);
            pstmt.setInt(1, Env.getAD_Client_ID(ctx));

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MDocType(ctx, rs, trxName));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getOfClient", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        MDocType[]	retValue	= new MDocType[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getOfClient

    static public MDocType[] getOfClient(Properties ctx) {
    	return getOfClient(ctx, null);
    }
    /**
     *      Get Client Document Type with DocBaseType
     *      @param ctx context
     *      @param DocBaseType base document type
     *      @return array of doc types
     */
    static public MDocType[] getOfDocBaseType(Properties ctx, String DocBaseType) {

        ArrayList	list	= new ArrayList();
        String		sql	= "SELECT * FROM C_DocType WHERE AD_Client_ID=? AND DocBaseType=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, Env.getAD_Client_ID(ctx));
            pstmt.setString(2, DocBaseType);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MDocType(ctx, rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getOfDocBaseType", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        MDocType[]	retValue	= new MDocType[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getOfDocBaseType
    
    /**
     * Obtiene un tipo de documento de la compañia a partir de su 
     * clave única (<code>MDocType.DOCTYPE_XXX</code>).
     * @param ctx Conntexto de la aplicación.
     * @param docTypeKey Clave única del tipo de documento
     * @param trxName Nombre de la transacción.
     * @return El <code>MDocType</code> en caso de que exista la clave,
     * <code>null</code> en caso contrario.
     */
    public static MDocType getDocType(Properties ctx, String docTypeKey, String trxName) {
    	MDocType mDocType = null;
    	String sql = "SELECT C_DocType_ID " +
    			     "FROM C_DocType " +
    			     "WHERE IsActive = 'Y' AND AD_Client_ID = ? AND DocTypeKey = ?";
    	Integer docTypeID = (Integer)DB.getSQLObject(trxName, sql, new Object[] {Env.getAD_Client_ID(ctx), docTypeKey});
    	if(docTypeID != null) {
    		mDocType = new MDocType(ctx, docTypeID, trxName);
    	}
    	return mDocType;
    }
    
    /**
     * Obtiene un tipo de documento de la compañia a partir de su clave única, 
     * letra de comprobante y número de punto de venta. Genera la clave única 
     * utilizando estos valores e intenta instanciar un tipo de documento
     * con dicha clave única.
     * @param ctx Contexto de la aplicación.
     * @param docTypeBaseKey Clave base del tipo de documento (<code>MDocType.DOCTYPE_XXX</code>).
     * @param letter Letra del comprobante (A,B,C,E,etc.)
     * @param posNumber Número del punto de venta (<code>1 - 9999</code>).
     * @param trxName Nombre de la transacción
     * @return El <code>MDocType</code> en caso de que exista la clave,
     * <code>null</code> en caso contrario.
     */
    public static MDocType getDocType(Properties ctx, String docTypeBaseKey, String letter, int posNumber, String trxName) {
    	String sPosNumber = formatPosNumber(posNumber);
    	String docTypeKey = docTypeBaseKey + letter + sPosNumber;
    	return getDocType(ctx, docTypeKey, trxName);
    }

	/**
	 * Verifica si un tipo de documento requiere ser impreso mediante una
	 * impresora fiscal.
	 * @param docType_ID ID del tipo de documento a verificar
	 * @return Verdadero en caso de que el tipo de documento requiera ser
	 * impreso mediante una impresora fiscal.
	 */
	public static boolean isFiscalDocType(int docType_ID) {
		boolean result = false;
		if(docType_ID != 0) {
			MDocType docType = new MDocType(Env.getCtx(), docType_ID, null);
			// HACK. No puedo acceder al metodo que me dice si los comprobantes
			// fiscales estan activos dado que se produce un error en la compilación.
			// Esto es porque el CalloutInvoiceExt se compila luego de MDocType.
			// result = (CalloutInvoiceExt.ComprobantesFiscalesActivos() && docType.isFiscal());
			result = docType.isFiscal(); 
		}
		return result;
	}
	
	/**
	 * @return El número de punto de venta formateado a 4 dígitos:
	 * Ej: punto de venta 11 -> 0011.
	 */
	public static String formatPosNumber(int posNumber) {
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumIntegerDigits(4);
		format.setMaximumIntegerDigits(4);
		format.setGroupingUsed(false);
		return format.format(posNumber);
	}

    /**
     *      Get Print Name
     *
     * @param AD_Language
     *      @return print Name if available translated
     */
    public String getPrintName(String AD_Language) {

        if ((AD_Language == null) || (AD_Language.length() == 0)) {
            return super.getPrintName();
        }

        String	retValue	= get_Translation("PrintName", AD_Language);

        if (retValue != null) {
            return retValue;
        }

        return super.getPrintName();

    }		// getPrintName

    /**
     *      Is this a Proposal or Quotation
     *      @return true if proposal or quotation
     */
    public boolean isOffer() {
        return (DOCSUBTYPESO_Proposal.equals(getDocSubTypeSO()) || DOCSUBTYPESO_Quotation.equals(getDocSubTypeSO())) && DOCBASETYPE_SalesOrder.equals(getDocBaseType());
    }		// isOffer

    /**
     *      Is this a Proposal (Not binding)
     *      @return true if proposal
     */
    public boolean isProposal() {
        return DOCSUBTYPESO_Proposal.equals(getDocSubTypeSO()) && DOCBASETYPE_SalesOrder.equals(getDocBaseType());
    }		// isProposal

    /**
     *      Is this a Quotation (Binding)
     *      @return true if Quotation
     */
    public boolean isQuotation() {
        return DOCSUBTYPESO_Quotation.equals(getDocSubTypeSO()) && DOCBASETYPE_SalesOrder.equals(getDocBaseType());
    }		// isQuotation

    //~--- set methods --------------------------------------------------------

    /**
     *      Set Default GL Category
     */
    public void setGL_Category_ID() {

        String	sql	= "SELECT * FROM GL_Category WHERE AD_Client_ID=? AND IsDefault='Y'";
        int	GL_Category_ID	= DB.getSQLValue(get_TrxName(), sql, getAD_Client_ID());

        if (GL_Category_ID == 0) {

            sql			= "SELECT * FROM GL_Category WHERE AD_Client_ID=?";
            GL_Category_ID	= DB.getSQLValue(get_TrxName(), sql, getAD_Client_ID());
        }

        setGL_Category_ID(GL_Category_ID);

    }		// setGL_Category_ID

    /**
     *      Set SOTrx based on document base type
     */
    public void setIsSOTrx() {

        boolean	isSOTrx	= DOCBASETYPE_SalesOrder.equals(getDocBaseType()) || DOCBASETYPE_MaterialDelivery.equals(getDocBaseType()) || getDocBaseType().startsWith("AR");

        super.setIsSOTrx(isSOTrx);

    }		// setIsSOTrx

	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		// Verificar que el del documento al revertir especificado no tenga igual signo que el documento que se está definiendo
		if (getC_ReverseDocType_ID()>0) {
			X_C_DocType revDocType = new X_C_DocType(getCtx(), getC_ReverseDocType_ID(), get_TrxName());
			if (getsigno_issotrx().equals(revDocType.getsigno_issotrx())) {
				log.saveError("El Tipo de Doc. al revertir posee igual signo de transaccion de ventas que el tipo de documento que se esta definiendo", "");
				return false;
			}
		}
		
		// Agregador por Disytel - Franco Bonafine
		// Validación de la clave única del tipo de documento.
		if(newRecord) {
			Integer docTypeID = (Integer) 
				DB.getSQLObject(get_TrxName(), 
						"SELECT C_DocType_ID FROM C_DocType WHERE AD_Client_ID = ? AND DocTypeKey = ?",
						new Object[] { getAD_Client_ID(), getDocTypeKey() });
			if(docTypeID != null) {
				log.saveError(Msg.parseTranslation(getCtx(),"@SaveErrorNotUnique@: @DocTypeKey@"), "");
				return false;
			}
		}
		
		/**
		 * Se codifica siguiendo la siguiente definicion:
		 *                | Libro de IVA    | Impres.Fiscal |              |
		 *-----------------------------------------------------------------|
		 *                |isFiscalDocument |   isFiscal    | isElectronico|
		 * NO FISCAL      |       X         |       X       |      X       |
		 * FISCAL         |       Y         |       X       |      X       |
		 * ELECTRONICO    |       Y         |       X       |      Y       |
		 * IMPRESO FISCAL |       Y         |       Y       |      X       |
		 */
		
		if(DOCSUBTYPEINV_NoFiscal.equals(getdocsubtypeinv())){
			setIsFiscalDocument(false);
			setIsFiscal(false);//fiscal printed
			setiselectronic(false);
			setC_Controlador_Fiscal_ID(0);
		}
		else if(DOCSUBTYPEINV_Fiscal.equals(getdocsubtypeinv())){
			setIsFiscalDocument(true);
			setIsFiscal(false);//fiscal printed
			setiselectronic(false);
			setC_Controlador_Fiscal_ID(0);
			
		}
		else if(DOCSUBTYPEINV_Electronico.equals(getdocsubtypeinv())){
			setIsFiscalDocument(true);
			setIsFiscal(false);//fiscal printed
			setiselectronic(true);
			setC_Controlador_Fiscal_ID(0);
		}
		else if(DOCSUBTYPEINV_ImpresoFiscal.equals(getdocsubtypeinv())){
			setIsFiscalDocument(true);
			setIsFiscal(true);//fiscal printed
			setiselectronic(false);
		}		
		
/*		// Si no es un documento fiscal, se limpian los campos que son especificos
		// de documentos fiscales.
		if (!isFiscalDocument()) {
			setIsFiscal(false);
			setC_Controlador_Fiscal_ID(0);
			//setFiscalDocument(""); NOT NULL
		}

		// Si no se debe imprimir mediante impresora fiscal, se limpian los campos
		// específicos de documentos que se imprimen con impresora fiscal.
		if (!isFiscal()) {
			setC_Controlador_Fiscal_ID(0);
			//setFiscalDocument(""); NOT NULL
		}
		//
		*/
		
		// Si el tipo de doc no está habilitado para el TPV, entonces tampoco su
		// vencimiento de habilitación
		if(!isEnabledInPOS()){
			setPOSEnableDue(false);
		}
		
		// Setear a 0 los días de habilitación para TPV al tipo de documento
		// cuando no posee vencimiento en su habilitación
		if(!isPOSEnableDue()){
			setPOSEnableDueDays(0);
		}
		
		
		return true;
	}
    
	/**
	 * Indica si el tipo de documento esta identificado por una clave única.
	 * @param docTypeBaseKey Clave única del tipo de documento.
	 * @return Verdadero o falso segun corresponda.
	 */
	public boolean isDocType(String docTypeBaseKey) {
		//return getDocTypeKey().equals(docTypeKey)
		
		// Se obtiene la letra de comprobante en caso de que exista.
		String letter = getLetter();
		// Se obtiene el nro de punto de venta en caso de que exista.
		Integer posNumber = getPosNumber();
		// Se genera la nueva clave unica del tipo de documento a partir
		// de la clave parámetro, letra y punto de venta.
		// En caso de que la letra de comprobante y el punto de venta
		// sean null, docTypeKey queda sin modificaciones.
		docTypeBaseKey = docTypeBaseKey + 
			(letter == null?"":letter) + 
			(posNumber == null?"":formatPosNumber(posNumber));
		
		return getDocTypeKey().equals(docTypeBaseKey);
	}
	
	/**
	 * @return Retorna la clave base del tipo de documento. La clave base del tipo
	 * de documento es alguna de las que se encuentran en <code>MDocType.DOCTYPE_XXX</code>
	 * En caso de que el tipo de documento no contenga Nro. de Punto de Venta y Letra
	 * de Comprobante, <code>getBaseKey()</code> retorna el mismo valor 
	 * que <code>getDocTypeKey()</code>.
	 */
	public String getBaseKey() {
		String key = getDocTypeKey();
		// Se obtiene la letra de comprobante en caso de que exista.
		String letter = getLetter();
		// Se obtiene el nro de punto de venta en caso de que exista.
		Integer posNumber = getPosNumber();
		// Se quita el número de punto de venta y la letra en caso de existir.
		key = (posNumber != null? key.substring(0, key.length() - 4) : key);
		key = (letter != null? key.substring(0, key.length() - 1) : key);
		return key;
	}
	
	/**
	 * @return Retorna el número del punto de venta asociado al tipo de documento. 
	 * En caso de que el tipo de documento no tenga un punto de venta asignado,
	 * retorna <code>null</code>. 
	 */
	public Integer getPosNumber() {
		MSequence seq = new MSequence(getCtx(), getDocNoSequence_ID(), get_TrxName());
		String docNo = seq.getPrefix() + seq.getCurrentNext();
		Integer posNumber = null;
		// Validación hardcode. Fruto de poner el punto de venta y la letra del
		// comprobante en la secuencia del documento.
		// Número de comprobante fiscal esta compuesto por 13 caracteres:
		// Ej -> A001500000012
		if(docNo.length() == 13) {
			try {
				posNumber = Integer.parseInt(docNo.substring(1,5));
			} catch (Exception e) {
				posNumber = null;			
			}
		}
		return posNumber;
	}
	
	/**
	 * @return Retorna la letra de comprobante asociada al tipo de documento. 
	 * En caso de que el tipo de documento no tenga una letra de comprobante asignada,
	 * retorna <code>null</code>. 
	 */
	public String getLetter() {
		MSequence seq = new MSequence(getCtx(), getDocNoSequence_ID(), get_TrxName());
		String docNo = seq.getPrefix() + seq.getCurrentNext();
		String letter = null;
		// Validación hardcode. Fruto de poner el punto de venta y la letra del
		// comprobante en la secuencia del documento.
		// Número de comprobante fiscal esta compuesto por 13 caracteres:
		// Ej -> A001500000012
		if(docNo.length() == 13) {
			try {
				letter = docNo.substring(0,1);
			} catch (Exception e) {
				letter = null;			
			}
		}
		return letter;
	}
	
	/**
	 * @autor: Horacio Alvarez - Servicios Digitales S.A.
	 * @fecha: 2009-06-16
	 * @descripcion: Devuelve true en caso que el tipo de documento sea del tipo electronico,
	 * osea que obtiene el CAE mediante los WebServices de la AFIP.
	 */
	
	public static boolean isElectronicDocType(int docType_ID) {
		boolean result = false;
		if(docType_ID != 0) {
			MDocType docType = new MDocType(Env.getCtx(), docType_ID, null);
			result = docType.iselectronic(); 
		}
		return result;
	}
	
	
}	// MDocType



/*
 * @(#)MDocType.java   02.jul 2007
 * 
 *  Fin del fichero MDocType.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
