package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocumentTypeVerify;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class MFTSetup extends MSetup {

	
	
	public MFTSetup(Properties ctx, int WindowNo) {
		super(ctx, WindowNo);
	}
	
	
    /**
     * Setea el lenguaje Argentino
     */
    public void setLanguageClient(){
    	this.getM_client().setAD_Language("es_AR");
    }
	
	/**
	 * Crea las entidades con la org *
	 */
	public boolean createEntities( int C_Country_ID,String City,int C_Region_ID,int C_Currency_ID ) {
		Env.setContext(this.getM_ctx(), "#AD_Org_ID", 0);
		return super.createEntities(C_Country_ID, City, C_Region_ID, C_Currency_ID);
	}
	
	/**
     * No aplica
     * @param C_Country_ID
     * @param C_Region_ID
     * @param City
     * @param defaultEntry
     * @param defaultName
     * @param bpg
     * @param bp
     * @param C_Currency_ID
     * @return
     */
    public boolean createData(int C_Country_ID,int C_Region_ID,String City,String defaultEntry,String defaultName,MBPGroup bpg,MBPartner bp,int C_Currency_ID){
        return true;
    }
    
    
	/**
	 * No implementa. Solo lo realiza MSetup
	 */
	public boolean createUsersFeatures(String userClient, String userOrg){
		return true;
	}
	
	/**
	 * Seteo los std values de org con la organización * 
	 */
	
	public boolean createOrg(String orgName){
		this.setM_stdValuesOrg(this.getM_client().getAD_Client_ID() + ",0,'Y',SysDate,0,SysDate,0");
		return true;
	}
	
	
	/**
	 * No implementa. Solo lo realiza MSetup. El que cierra la transacción es el Fast-Tracker
	 */
	protected void commitAndCloseTrx(){
		
	}
	
	/**
	 * Retorno la organización *
	 */
	public int getAD_Org_ID(){
		return 0;
	}
	
	
	/**
     * Crea los tipos de documento
     * @param GL_None
     * @param GL_GL
     * @param GL_ARI
     * @param GL_ARR
     * @param GL_MM
     * @param GL_API
     * @param GL_APP
     * @param GL_CASH
     * @param GL_M
     * @param GL_SignoPositivo
     * @param GL_SignoNegativo
     * @return éxito del procedimiento
     */
    public boolean createAllDocTypes(int GL_None,int GL_GL,int GL_ARI,int GL_ARR,int GL_MM,int GL_API,int GL_APP,int GL_CASH,int GL_M,int GL_SignoPositivo,int GL_SignoNegativo){
    	//Crea los tipos de documento de la versión fast-track
    	//createDocType( "Pedido de Mantenimiento",Msg.getElement( this.getM_ctx(),"MPC_Order_ID",false ),MDocType.DOCBASETYPE_MaintenanceOrder,null,0,0,910000,GL_M,GL_SignoPositivo, MDocType.DOCTYPE_MaintenanceOrder);
        //createDocType( "Asunto de Pedido de Manufactura",Msg.getElement( this.getM_ctx(),"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrderIssue,null,0,0,920000,GL_M, GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderIssue);
        //createDocType( "Variacion de Metodo de Pedido de Manufactura",Msg.getElement( this.getM_ctx(),"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrderMethodVariation,null,0,0,930000,GL_M,GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderMethodVariation);
        //createDocType( "Pedido de Manufactura",Msg.getElement( this.getM_ctx(),"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrder,null,0,0,940000,GL_M,GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrder);
        //createDocType( "Planificacion de pedido de Manufactura",Msg.getElement( this.getM_ctx(),"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrder,null,0,0,950000,GL_M,GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderPlanning);
        //createDocType( "Albaran de Pedido de Manufactura",Msg.getElement( this.getM_ctx(),"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrderReceipt,null,0,0,960000,GL_M, GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderReceipt);
        //createDocType( "Variacion de uso de Pedido de Manufactura",Msg.getElement( this.getM_ctx(),"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrderUseVariation,null,0,0,970000,GL_M, GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderUseVariation);
        //createDocType( "Variacion de Tasa de Pedido de Manufactura",Msg.getElement( this.getM_ctx(),"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrderRateVariation,null,0,0,980000,GL_M,GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderRateVariation);
        //createDocType( "Planificacion de Aviso de Pedido de Material",Msg.getElement( this.getM_ctx(),"M_Requisition_ID",false ),MDocType.DOCBASETYPE_PurchaseRequisition,null,0,0,910000,GL_None, GL_SignoPositivo, MDocType.DOCTYPE_PurchaseRequisitionPlanning);

        // end e-evolution vpj-cd 06/03/2005 CMPCS

        // Base DocumentTypes

        int ii = createDocType( "Diario del Mayor",Msg.getElement( this.getM_ctx(),"GL_Journal_ID" ),MDocType.DOCBASETYPE_GLJournal,null,0,0,1000,GL_GL,GL_SignoPositivo, MDocType.DOCTYPE_GLJournal);

        if( ii == 0 ) {
            String err = "Document Type not created";

            this.getM_info().append( err );
            this.getM_trx().rollback();
            this.getM_trx().close();

            return false;
        }

        createDocType( "Lote de Asientos",Msg.getElement( this.getM_ctx(),"GL_JournalBatch_ID" ),MDocType.DOCBASETYPE_GLJournal,null,0,0,100,GL_GL,GL_SignoPositivo, MDocType.DOCTYPE_GLJournalBatch);

        // MDocType.DOCBASETYPE_GLDocument
        
        createDocType("Documento de GL","Documento de GL",MDocType.DOCTYPE_GLDocument,null,0,0,0,GL_GL,GL_SignoPositivo,MDocType.DOCTYPE_GLDocument);
        createDocType("Asiento","Asiento",MDocType.DOCBASETYPE_GLJournal,null,0,0,0,GL_GL,GL_SignoPositivo,null);
        //

        int DT_I = createDocType( "Factura de Cliente",Msg.getElement( this.getM_ctx(),"C_Invoice_ID",true ),MDocType.DOCBASETYPE_ARInvoice,null,0,0,100000,GL_ARI,GL_SignoPositivo, MDocType.DOCTYPE_CustomerInvoice);
        //int DT_II = createDocType( "Factura de Cliente Indirecta",Msg.getElement( this.getM_ctx(),"C_Invoice_ID",true ),MDocType.DOCBASETYPE_ARInvoice,null,0,0,150000,GL_ARI,GL_SignoPositivo, MDocType.DOCTYPE_CustomerIndirectInvoice);
        int DT_IC = createDocType( "Abono de Cliente",Msg.getMsg( this.getM_ctx(),"CreditMemo" ),MDocType.DOCBASETYPE_ARCreditMemo,null,0,0,170000,GL_ARI,GL_SignoNegativo, MDocType.DOCTYPE_CustomerCreditMemo);

        // MDocType.DOCBASETYPE_ARProFormaInvoice

        createDocType( "Factura de Proveedor",Msg.getElement( this.getM_ctx(),"C_Invoice_ID",false ),MDocType.DOCBASETYPE_APInvoice,null,0,0,0,GL_API,GL_SignoNegativo, MDocType.DOCTYPE_VendorInvoice, true);
        createDocType( "Abono de Proveedor",Msg.getMsg( this.getM_ctx(),"CreditMemo" ),MDocType.DOCBASETYPE_APCreditMemo,null,0,0,0,GL_API, GL_SignoPositivo, MDocType.DOCTYPE_VendorCreditMemo, true);
        createDocType( "Corresponder Factura",Msg.getElement( this.getM_ctx(),"M_MatchInv_ID",false ),MDocType.DOCBASETYPE_MatchInvoice,null,0,0,390000,GL_API,GL_SignoPositivo, MDocType.DOCTYPE_MatchInvoice);
        createDocType( "Cobro a Cliente",Msg.getElement( this.getM_ctx(),"C_Payment_ID",true ),MDocType.DOCBASETYPE_ARReceipt,null,0,0,0,GL_ARR,GL_SignoNegativo, MDocType.DOCTYPE_CustomerReceipt);
        createDocType( "Pago a Proveedor",Msg.getElement( this.getM_ctx(),"C_Payment_ID",false ),MDocType.DOCBASETYPE_APPayment,null,0,0,0,GL_APP,GL_SignoPositivo, MDocType.DOCTYPE_VendorPayment);
        createDocType( "Asignacion","Asignacion",MDocType.DOCBASETYPE_PaymentAllocation,null,0,0,490000,GL_CASH,GL_SignoPositivo, MDocType.DOCTYPE_PaymentAllocation);
        int outTrf_id = createDocType( "Transferencia Saliente",Msg.getElement( this.getM_ctx(),"C_BankTransfer_ID",false ),MDocType.DOCBASETYPE_APPayment,null,0,0,0,GL_APP,GL_SignoPositivo, MDocType.DOCTYPE_OutgoingBankTransfer);
        int inTrf_id = createDocType( "Transferencia Entrante",Msg.getElement( this.getM_ctx(),"C_BankTransfer_ID",true ),MDocType.DOCBASETYPE_ARReceipt,null,0,0,0,GL_ARR,GL_SignoNegativo, MDocType.DOCTYPE_IncomingBankTransfer);
        // Se asignan los tipos de transferencias a la compañía.
        setClientTransferDocTypes(inTrf_id, outTrf_id);

        
        // Tipos de documentos para retenciones
        createDocType( "Comprobante de Retencion (Proveedor)","Comprobante de Retencion",MDocType.DOCBASETYPE_APCreditMemo,null,0,0,0,GL_API,GL_SignoPositivo, MDocType.DOCTYPE_Retencion_Receipt);
        createDocType( "Factura de Retencion (Proveedor)", "Factura de Retencion", MDocType.DOCBASETYPE_APInvoice,null,0,0,0,GL_API,GL_SignoNegativo, MDocType.DOCTYPE_Retencion_Invoice);
        createDocType( "Comprobante de Retencion (Cliente)","Comprobante de Retencion",MDocType.DOCBASETYPE_ARCreditMemo,null,0,0,0,GL_ARI,GL_SignoNegativo, MDocType.DOCTYPE_Retencion_ReceiptCustomer);
        createDocType( "Factura de Retencion (Cliente)","Factura de Retencion",MDocType.DOCBASETYPE_ARInvoice,null,0,0,0,GL_ARI,GL_SignoPositivo, MDocType.DOCTYPE_Retencion_InvoiceCustomer);
        
        int DT_S = createDocType( "Remito de Salida","Remito",MDocType.DOCBASETYPE_MaterialDelivery,null,0,0,500000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialDelivery);
        //int DT_SI = createDocType( "Albaran de Salida Indirecto","Albaran de Salida",MDocType.DOCBASETYPE_MaterialDelivery,null,0,0,550000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialIndirectDelivery);
        int DT_RM = createDocType( "Devolucion de Cliente","Devolucion de Cliente",MDocType.DOCBASETYPE_MaterialDelivery,null,0,0,570000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_CustomerReturn);

        createDocType( "Remito de Entrada","Remito",MDocType.DOCBASETYPE_MaterialReceipt,null,0,0,0,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialReceipt);
        createDocType( "Devolucion de Proveedor","Devolucion de Proveedor",MDocType.DOCBASETYPE_MaterialReceipt,null,0,0,870000,GL_MM,GL_SignoNegativo, MDocType.DOCTYPE_VendorReturn);
        
//        int DT_RM = createDocType( "Devolucion de Material de Cliente","Devolucion de Material de Cliente",MDocType.DOCBASETYPE_MaterialReceipt,null,0,0,570000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_CustomerMaterialReturn);

        createDocType( "Pedido a Proveedor",Msg.getElement( this.getM_ctx(),"C_Order_ID",false ),MDocType.DOCBASETYPE_PurchaseOrder,null,0,0,800000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_PurchaseOrder);
        createDocType( "Corresponder PP",Msg.getElement( this.getM_ctx(),"M_MatchPO_ID",false ),MDocType.DOCBASETYPE_MatchPO,null,0,0,890000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_MatchPO);
        //createDocType( "Aviso de Pedido de Material",Msg.getElement( this.getM_ctx(),"M_Requisition_ID",false ),MDocType.DOCBASETYPE_PurchaseRequisition,null,0,0,900000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_PurchaseRequisition);
        createDocType( "Extracto Bancario",Msg.getElement( this.getM_ctx(),"C_BankStatemet_ID",true ),MDocType.DOCBASETYPE_BankStatement,null,0,0,700000,GL_CASH,GL_SignoPositivo, MDocType.DOCTYPE_BankStatement);
        createDocType( "Diario de Caja",Msg.getElement( this.getM_ctx(),"C_Cash_ID",true ),MDocType.DOCBASETYPE_CashJournal,null,0,0,750000,GL_CASH,GL_SignoPositivo, MDocType.DOCTYPE_CashJournal);
        createDocType( "Movimiento de Material",Msg.getElement( this.getM_ctx(),"M_Movement_ID",false ),MDocType.DOCBASETYPE_MaterialMovement,null,0,0,610000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialMovement);
        createDocType( "Inventario Fisico",Msg.getElement( this.getM_ctx(),"M_Inventory_ID",false ),MDocType.DOCBASETYPE_MaterialPhysicalInventory,null,0,0,620000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialPhysicalInventory);
        //createDocType( "Produccion",Msg.getElement( this.getM_ctx(),"M_Production_ID",false ),MDocType.DOCBASETYPE_MaterialProduction,null,0,0,630000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialProduction);
        //createDocType( "Asunto de Proyecto",Msg.getElement( this.getM_ctx(),"C_ProjectIssue_ID",false ),MDocType.DOCBASETYPE_ProjectIssue,null,0,0,640000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_ProjectIssue);
        createDocType( "Ingreso/Egreso Simple","Ingreso/Egreso Simple",MDocType.DOCBASETYPE_MaterialPhysicalInventory,null,0,0,650000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_SimpleMaterialInOut);
        createDocType( "Parte de Movimientos","Parte de Movimientos",MDocType.DOCBASETYPE_MaterialDelivery,null,0,0,700000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_ParteDeMovimientos);
        createDocType( "Parte de Movimientos Valorizados","Parte de Movimientos Valorizados",MDocType.DOCBASETYPE_APInvoice,null,0,0,0,GL_API,GL_SignoNegativo, MDocType.DOCTYPE_ParteDeMovimientosValorizados);
        
        // Order Entry

        //createDocType( "Presupuesto en Firme","Presupuesto en Firme",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_Quotation,0,0,10000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_Quotation);
        createDocType( "Presupuesto","Presupuesto",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_Proposal,0,0,20000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_Proposal);
        //createDocType( "Pedido Prepago","Pedido Prepago",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_PrepayOrder,DT_S,DT_I,30000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_PrepayOrder);
        //createDocType( "RMA","RMA",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_ReturnMaterial,DT_RM,DT_IC,30000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_ReturnMaterial);
        createDocType( "Pedido","Pedido",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_StandardOrder,DT_S,DT_I,50000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_StandarOrder);
        //createDocType( "Pedido a Credito","Pedido a Credito",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_OnCreditOrder,DT_SI,DT_I,60000,GL_None, GL_SignoPositivo, MDocType.DOCTYPE_OnCreditOrder);    // RE
        //createDocType( "Pedido de Almacen","Pedido de Almacen",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_WarehouseOrder,DT_S,DT_I,70000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_WarehouseOrder);    // LS

        // Boleta de Depósito
        createDocType( "Boleta de Deposito", "Boleta de Deposito",MDocType.DOCBASETYPE_ARReceipt,null,0,0,0,GL_ARR,GL_SignoNegativo, MDocType.DOCTYPE_DepositReceipt);
        
        //int DT = createDocType( "Ticket TPV","Ticket TPV",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_POSOrder,DT_SI,DT_II,80000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_POSOrder);    // Bar

        // POS As Default for window SO

        //createPreference( "C_DocTypeTarget_ID",String.valueOf( DT ),143 );

        // Update ClientInfo

        StringBuffer sqlCmd = new StringBuffer( "UPDATE AD_ClientInfo SET " );
        sqlCmd.append( "C_AcctSchema1_ID=" ).append( this.getM_as().getC_AcctSchema_ID()).append( ", C_Calendar_ID=" ).append( this.getM_calendar().getC_Calendar_ID()).append( " WHERE AD_Client_ID=" ).append( this.getM_client().getAD_Client_ID());
        int no = DB.executeUpdate( sqlCmd.toString(),this.getM_trx().getTrxName());

        if( no != 1 ) {
            String err = "ClientInfo not updated";

            log.log( Level.SEVERE,err );
            this.getM_info().append( err );
            this.getM_trx().rollback();
            this.getM_trx().close();

            return false;
        }

        // Validate Completeness

        //DocumentTypeVerify.createDocumentTypes( this.getM_ctx(),getAD_Client_ID(),null,this.getM_trx().getTrxName());
        //DocumentTypeVerify.createPeriodControls( this.getM_ctx(),getAD_Client_ID(),null,this.getM_trx().getTrxName());

        //

        log.info( "fini" );
        
        return true;

    }
    
    /**
     * No aplica en este momento, se crearán dentro de FTClient
     * @param C_Country_ID id del país para verificación
     * @param defaultEntry nombre por defecto de la categoría de impuesto (en esta clase no se usa porque se deben crear impuestos concretos, no con valores por defecto)
     * @return la última categoría de impuesto creada
     */
    
    public int createTaxFeatures(int C_Country_ID,String defaultEntry){
    	return 1;
    }
    	
	/**
     * Crea las entidades correspondientes (para esta clase no es necesario)
     * @param C_BP_Group_ID id del grupo de entidad comercial
     * @param C_Country_ID id del país
     * @param C_Region_ID id de la región
     * @param City nombre de la ciudad
     */
    public void createPartners(int C_BP_Group_ID, int C_Country_ID,int C_Region_ID,String City){
    	
    }
}
