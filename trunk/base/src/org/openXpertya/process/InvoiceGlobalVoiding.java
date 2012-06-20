package org.openXpertya.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.PO;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.HTMLMsg.HTMLList;
import org.openXpertya.util.HTMLMsg.HTMLListHeader;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class InvoiceGlobalVoiding extends SvrProcess {

	/** ID de Factura a anular junto con todas sus relaciones creadas */
	private Integer invoiceID = 0;
	
	/** Factura a anular */
	private MInvoice invoice;
	
	/* DEPRECATED: No se anulan los débitos/créditos de los allocation de la factura parámetro
	/**
	 * Débitos y Créditos imputados dentro de los allocation hdrs relacionados
	 * con la factura
	 *
	private Set<MInvoice> debitsCredits;
	*/
	
	/** Lista de los allocation hdrs donde se encuentra imputada esta factura */
	private List<MAllocationHdr> allocHdrs;
	
	/** Pedido relacionado a la factura */
	private MOrder order;
	
	/** Remito */
	private MInOut inOut;
	
	/** Tareas adicionales de cuenta corriente */
	private Map<PO, Object> aditionalWorks = new HashMap<PO, Object>();
	
	/** Mensaje final de proceso */
	private HTMLMsg msg = new HTMLMsg();
	
	/** Transaccción para uso externo a SvrProcess (instanciación por constructor) */
	private String localTrxName = null;
	
	/** Contexto para uso externo a SvrProcess (instanciación por constructor) */
	private Properties localCtx = null;
	
	/**
	 * Booleano que determina si se debe controlar que los allocations
	 * relacionados se encuentren con otros débitos
	 */
	private boolean controlMoreDebitsInAllocation = true;

	public InvoiceGlobalVoiding() {
		super();
	}

	/**
	 * Constructor del proceso para anulación completa de una factura
	 * 
	 * @param invoiceID
	 *            ID de factura a anular
	 * @param ctx
	 *            Contexto de la aplicación
	 * @param trxName
	 *            Nombre de transacción a utilizar.
	 */
	public InvoiceGlobalVoiding(int invoiceID, Properties ctx, String trxName) {
		super();
		this.invoiceID = invoiceID;
		localTrxName = trxName;
		if (ctx == null) {
			ctx = Env.getCtx();
		}
		localCtx = ctx;
	}

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		String name = null;
        for( int i = 0;i < para.length;i++ ) {
            name = para[ i ].getParameterName();
            if( name.equalsIgnoreCase( "C_Invoice_ID" )) {
            	invoiceID = para[ i ].getParameterAsInt();
            }
        }
	}
	
	@Override
	protected String doIt() throws Exception {
		// Realizar las inicializaciones de las relaciones de la factura
		initialize();
		
		// Anular los allocations
		voidAllocations();	
		
		// Anular el remito
		// Se hace antes que la factura ya que para locale_ar, al anular una
		// factura se crea una nota de crédito y al crear esta NC se valida si
		// es posible hacer la nota según las cantidades no entregadas del
		// pedido. Por eso es que al anular primero el remito, se actualizan las
		// cantidades de entrega del pedido y luego se puede proceder a anular
		// la factura tranquilamente.
		voidInOut();
		
		// Anular la factura
		voidInvoice();
		
		// DEPRECATED: No se anulan los débitos/créditos de los allocation de la factura parámetro
		// Anular los débitos/créditos que se encuentran referenciadas por los
		// allocations de la factura parámetro
//		voidDebitsCredits();
		
		// Anular el pedido
		voidOrder();
		
		// Realización de operaciones luego de anular todas las transacciones
		// y/o comprobantes
		afterVoid();
		
		return getFinalMsg();
	}

	/**
	 * Inicializa la factura y sus relaciones, pedido, remito y allocations.
	 * 
	 * @throws Exception
	 *             en caso de error al inicializar
	 */
	protected void initialize() throws Exception{
		// Factura
        initInvoice();
        // Pedido
        initOrder();
        // Allocation Hdrs
        initAllocationHdrs();
        // Remito
        initInOut();
	}
	
	/**
	 * Anula los allocations
	 * 
	 * @throws Exception
	 *             en caso de error
	 */
	protected void voidAllocations() throws Exception{
		String allocationAction = MAllocationHdr.ALLOCATIONACTION_VoidPaymentsRetentions;
		// Obtener la acción a realizar a partir del tipo de acción del
		// allocation
		String allocationDocAction = MAllocationHdr
				.getDocActionByAllocationAction(allocationAction);
		HTMLList msgList = null;
		if (getAllocHdrs() != null && !getAllocHdrs().isEmpty()) {
			msgList = createHTMLList(null, Msg.translate(getCtx(),
					"C_AllocationHdr_ID"));
		}
		// Itero por los allocations y los anulo junto con todos los payments,
		// cashlines, etc. relacionados
		for (MAllocationHdr mAllocationHdr : getAllocHdrs()) {
			// Seteo el allocation action para que anule todo
			mAllocationHdr.setAllocationAction(allocationAction);
			mAllocationHdr.setConfirmAditionalWorks(false);
			// Anular el allocation junto con todos sus pagos y retenciones
			if (!DocumentEngine.processAndSave(mAllocationHdr,
					allocationDocAction, false)) {
				throw new Exception("@AllocationVoidError@ # "
						+ mAllocationHdr.getDocumentNo() + ": "
						+ mAllocationHdr.getProcessMsg());
			}
			getAditionalWorks().putAll(mAllocationHdr.getAditionalWorks());
			// Registro este allocation en el mensaje final
			getMsg().createAndAddListElement(null,
					mAllocationHdr.getDocumentNo(), msgList);
		}
		getMsg().addList(msgList);
	}

	/**
	 * Anula la factura parámetro
	 * 
	 * @param invoice
	 *            factura a anular
	 * @param makeMsg
	 *            true si se debe armar el mensaje con esta factura, false si no
	 *            hay que crear nada de mensaje por esta factura
	 * @throws Exception
	 *             en caso de error
	 */
	protected void voidInvoice(MInvoice invoice, boolean makeMsg) throws Exception{
		// No debe confirmar las operaciones adicionales de cuenta corriente 
		getInvoice().setConfirmAditionalWorks(false);
		// Anulo la factura
		if (!DocumentEngine.processAndSave(invoice, MInvoice.DOCACTION_Void, false)) {
			throw new Exception("@InvoiceVoidError@ # "
					+ invoice.getDocumentNo() + ": " + invoice.getProcessMsg());
		}
		// Agrego los trabajos adicionales de cuenta corriente para después
		// confirmarlos todos juntos
		getAditionalWorks().putAll(getInvoice().getAditionalWorkResult());
		// Si debo armar el mensaje, lo armo para esta factura en particular
		if(makeMsg){
			// Armo el mensaje para esta factura
			HTMLList list = createHTMLList(null, Msg.translate(getCtx(),
					"C_Invoice_ID"));
			getMsg().createAndAddListElement(null, invoice.getDocumentNo(), list);
			getMsg().addList(list);
		}
	}	
	
	/**
	 * Anula la factura
	 * 
	 * @throws Exception
	 *             en caso de error
	 */
	protected void voidInvoice() throws Exception{
		// Anulo la factura
		voidInvoice(getInvoice(), true);
	}

	/* DEPRECATED: No se anulan los débitos/créditos de los allocation de la factura parámetro
	/**
	 * Anular los débitos y créditos que están referenciados e imputados dentro
	 * de los allocations que referencian a la factura parámetro (NO SE ANULAN
	 * LOS PEDIDOS Y REMITOS DE LOS DÉBITOS/CRÉDITOS INDICADOS)
	 * 
	 * @throws Exception en caso de errores
	 *
	protected void voidDebitsCredits() throws Exception{
		// Iterar por los débitos/créditos y anularlos
		for (MInvoice invoice : getDebitsCredits()) {
			voidInvoice(invoice);
		}
	}*/
	
	/**
	 * Anula el pedido relacionado a la factura si es que existe alguno
	 * 
	 * @throws Exception
	 *             en caso de error
	 */
	protected void voidOrder() throws Exception{
		// Anular el pedido si existe alguno
		if(getOrder() != null){
			// Bypass para que no anule los remitos y facturas relacionadas al
			// pedido
			getOrder().setVoidInOuts(false);
			getOrder().setVoidInvoices(false);
			// Anulo el pedido
			if (!DocumentEngine.processAndSave(getOrder(), MOrder.DOCACTION_Void,
					false)) {
				throw new Exception("@OrderVoidError@ # "
						+ getOrder().getDocumentNo() + ": " + getOrder().getProcessMsg());
			}
			// Armo el mensaje para esta factura
			HTMLList list = createHTMLList(null, Msg.translate(getCtx(),
					"C_Order_ID"));
			getMsg().createAndAddListElement(null, getOrder().getDocumentNo(), list);
			getMsg().addList(list);
		}
	}

	/**
	 * Anula el remito o los remitos relacionados con la factura o el pedido de
	 * la factura.
	 * 
	 * @throws Exception
	 *             en caso de error
	 */
	protected void voidInOut() throws Exception{
		if(getInOut() != null){
			if (!DocumentEngine.processAndSave(getInOut(), MInOut.DOCACTION_Void,
					false)) {
				throw new Exception("@InOutVoidError@ # "
						+ getInOut().getDocumentNo() + ": " + getInOut().getProcessMsg());
			}
			// Armo el mensaje para esta factura
			HTMLList list = createHTMLList(null, Msg.translate(getCtx(),
					"M_InOut_ID"));
			getMsg().createAndAddListElement(null, getInOut().getDocumentNo(), list);
			getMsg().addList(list);
		}
	}

	/**
	 * Realización de operaciones luego de anular todos los
	 * comprobantes/transacciones en este proceso
	 * 
	 * @throws Exception
	 *             en caso de error
	 */
	protected void afterVoid() throws Exception{
		// Actualizar el crédito de la entidad comercial
		confirmCurrentAccountAditionaWorks();
	}

	/**
	 * Actualización del crédito de la entidad comercial, confirmación de los
	 * trabajos adicionales
	 * 
	 * @throws Exception en caso de error
	 */
	protected void confirmCurrentAccountAditionaWorks() throws Exception{
		// Obtengo el manager actual
		CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
		// Confirmo las transacciones creadas
		CallResult result = manager.afterProcessDocument(getCtx(), new MOrg(
				getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName()),
				new MBPartner(getCtx(), getInvoice().getC_BPartner_ID(),
						get_TrxName()), getAditionalWorks(), get_TrxName());
		if(result.isError()){
			throw new Exception(result.getMsg());
		}
	}
	
	/**
	 * Inicializa la factura con la de la BD
	 */
	protected void initInvoice(){
		setInvoice(new MInvoice(getCtx(), invoiceID, get_TrxName()));
	}

	/**
	 * Inicialización de los allocations hdrs de la factura. Si alguno de ellos
	 * contiene más de un débito excluyendo la factura actual, entonces error,
	 * no es correcto anular débitos o créditos creados anteriormente a la
	 * factura. Para débitos y créditos creados en el momento de la imputación,
	 * como por ejemplo por descuentos/recargos, esta funcionalidad de anulación
	 * no es consistente ya que esos comprobantes no se anulan junto con la
	 * factura. Otro caso de error puede ser que esta factura se encuentre
	 * imputada como un crédito dentro de alguún allocation.
	 * 
	 * @throws Exception
	 *             en caso que se cumpla lo indicado
	 */
	protected void initAllocationHdrs() throws Exception{
		// Verificar si la factura parámetro se encuentra como crédito dentro de
		// algún allocation, un caso de error es este
		if (DB.getSQLValue(get_TrxName(), getInvoiceCreditAllocationHdrQuery(),
				getInvoice().getID()) > 0) {
			throw new Exception(getMsg("VoidingInvoiceCreditAllocation"));
		}
		// Obtener los allocation hdrs donde se encuentra esta factura
		MAllocationHdr[] allocHdrs = MAllocationHdr.getOfInvoice(getCtx(),
				invoiceID, get_TrxName());
		List<MAllocationHdr> realAllocHdrs = new ArrayList<MAllocationHdr>();
		// Itero por los allocations hdrs y verifico que no contengan más de un
		// débito, si es que se debe realizar
		int debitsCount;
		for (MAllocationHdr mAllocationHdr : allocHdrs) {
			if (mAllocationHdr.getDocStatus().equals(
					MAllocationHdr.DOCSTATUS_Completed)
					|| mAllocationHdr.getDocStatus().equals(
							MAllocationHdr.DOCSTATUS_Closed)) {
				if(isControlMoreDebitsInAllocation()){
					// Obtener la cantidad de allocations hdrs donde se encuentran cada
					// una de las facturas de débito y crédito
					debitsCount = DB.getSQLValue(get_TrxName(),
							getInvoiceDebitAllocationHdrQuery(), mAllocationHdr.getID());
					// Si es mayor a 1 significa que ese allocation contiene más de 1
					// débito, o sea, otro débito excluyendo a la factura a anular
					// parámetro al proceso
					if(debitsCount > 1){
						throw new Exception(getMsg("ExistsAnotherDebitsInAllocation"));
					}
				}
				realAllocHdrs.add(mAllocationHdr);
			}
		}
		setAllocHdrs(realAllocHdrs);
	}
	
	/* DEPRECATED: No anular ni los débitos ni créditos de los allocations de la factura
	/**
	 * Inicialización de los allocations hdrs de la factura y de los débitos y
	 * créditos que poseen cada una de ellas. Si alguno de esos débitos o
	 * créditos se encuentra imputada en otro allocation distinto a los que
	 * posee la factura parámetro, entonces se tira una excepción con un mensaje
	 * de error indicando tal motivo.
	 * 
	 * @throws Exception
	 *             en caso que se cumpla lo indicado
	 *
	protected void initAllocationHdrs() throws Exception{
		// Obtener los allocation hdrs donde se encuentra esta factura
		MAllocationHdr[] allocHdrs = MAllocationHdr.getOfInvoice(getCtx(),
				invoiceID, get_TrxName());
		// Obtener la lista de ids de allocation hdrs
		Set<Integer> allocHdrsIDs = new HashSet<Integer>();
		for (MAllocationHdr mAllocationHdr : allocHdrs) {
			allocHdrsIDs.add(mAllocationHdr.getID());
		}
		// Obtengo los ids de débitos y créditos 
		Set<Integer> debsCreds = getDebitsCreditsIDs(allocHdrsIDs);
		Set<MInvoice> debsCredsInvoices = new HashSet<MInvoice>();
		MInvoice debCred;
		int allocHdrCount;
		for (Integer debCredID : debsCreds) {
			debCred = new MInvoice(getCtx(), debCredID, get_TrxName());
			// Obtener la cantidad de allocations hdrs donde se encuentran cada
			// una de las facturas de débito y crédito
			allocHdrCount = DB.getSQLValue(get_TrxName(),
					getAllocationHdrInvoicesQuery(allocHdrsIDs), debCredID,
					debCredID);
			// Si es mayor a 0 significa que no solo los allocation de la
			// factura parámetro contienen a ese débito/crédito sino que
			// pertenece a otros, entonces error
			if(allocHdrCount > 0){
				throw new Exception(
						"La factura de debito o credito con nro "
								+ debCred.getDocumentNo()
								+ ", que la referencia una imputacion de la factura parametro, contiene otras imputaciones que no referencian a la factura parametro.");
			}
			debsCredsInvoices.add(debCred);
		}
		setAllocHdrs(Arrays.asList(allocHdrs));
		setDebitsCredits(debsCredsInvoices);
	}*/
	
	/**
	 * Inicializo el pedido relacionado a esta factura
	 * 
	 * @throws Exception
	 *             en caso que el pedido relacionado a la factura se encuentre
	 *             también relacionado en otras
	 */
	protected void initOrder() throws Exception{
		// Si existe pedido relacionado a la factura
		if(!Util.isEmpty(getInvoice().getC_Order_ID(), true)){
			// Determino la cantidad de facturas que se relacionan con este
			// pedido
			int distinctInvoices = DB.getSQLValue(get_TrxName(),
					getOrderInvoicesQuery(), getInvoice().getC_Order_ID());
			// Si la cantidad de facturas es mayor a 1 significa que existen más
			// de una factura relacionado al pedido, entonces error
			if(distinctInvoices > 1){
				throw new Exception(getMsg("InvoiceOrderRelateWithAnotherInvoices"));
			}
			setOrder(new MOrder(getCtx(), getInvoice().getC_Order_ID(),get_TrxName()));
		}	
	}

	/**
	 * Inicializa los remitos
	 * 
	 * @throws Exception
	 */
	protected void initInOut() throws Exception{
		// Puede haber más de un remito para una factura, como así
		// también varios pedidos por factura y/o remito, entonces si existe mas
		// de un remito tirar mensaje de error
		// TODO Anular TODOS los remitos relacionados
		Integer inoutID = 0;
		// Si es issotrx = 'N' se debe verificar en los MMatchInv y MMatchPO,
		// como último el remito puede estar relacionado al pedido de la factura
		// o a la factura misma 
		if(!getInvoice().isSOTrx()){
			// Verifico si puedo sacarlo por match PO
			if(getOrder() != null){
				inoutID = inspectInOut(
						getInOutMatchPOQuery(),
						getInOutIDMatchPOQuery(),
						getMatchPOInOutQuery(),
						getOrder().getID(),
						getMsg("ExistsAnotherInOutsForInvoiceOrder"),
						getMsg("InvoiceOrderInOutRelateWithAnotherOrders"));
			}
			// Si no se pudo, verificar por match INV
			if(inoutID == 0){
				inoutID = inspectInOut(
						getInOutMatchInvQuery(),
						getInOutIDMatchInvQuery(),
						getMatchINVInOutQuery(),
						getInvoice().getID(),
						getMsg("ExistsAnotherInOutsForInvoice"),
						getMsg("InvoiceInOutRelateWithAnotherInvoices"));
			}
		}
		// Si es issotrx = 'Y' o todavía no pudieron encontrarlo vía match PO o
		// INV entonces verificar las relaciones directas 
		if(inoutID == 0){
			// Primero buscar si lo tiene el pedido de la factura, ya que para
			// crear remitos de cliente es obligatorio un pedido, sino buscar
			// por la factura. En caso que existan 2 remitos asociados al pedido
			// o la factura, se tira error.
			if(getOrder() != null){
				inoutID = inspectInOut(
						getInOutOrderQuery(),
						"SELECT m_inout_id FROM m_inout WHERE c_order_id = ?",
						null,
						getOrder().getID(),
						getMsg("ExistsAnotherInOutsForInvoiceOrder"),
						null);
			}
			// Si no pudo sacarlo por el pedido veo por la factura
			if(inoutID == 0){
				inoutID = inspectInOut(
						getInOutInvoiceQuery(),
						"SELECT m_inout_id FROM m_inout WHERE c_invoice_id = ?",
						null, getInvoice().getID(),
						getMsg("ExistsAnotherInOutsForInvoice"),
						null);
			}
		}
		// Si se encontró entonces lo obtengo y lo seteo localmente
		if(inoutID > 0){
			setInOut(new MInOut(getCtx(), inoutID, get_TrxName()));
		}
	}

	/**
	 * Verificar existencias de remitos. Primero se verifica la cantidad de
	 * remitos que existen para los criterios actuales, si existe mas de uno
	 * entonces se tira una excepción con el error parámetro. En el caso que
	 * haya uno y solo uno se retorna ese id de remito con el sql para obtener
	 * el remito.
	 * 
	 * @param sqlQueryForCount
	 *            sql para determinar la cantidad de remitos para los criterios
	 *            actuales
	 * @param sqlQueryForGetInOut
	 *            sql para obtener el id del remito encontrado
	 * @param sqlQueryForInOutRelations
	 *            sql para determinar la cantidad de relaciones (pedidos o
	 *            facturas) que tiene el remito único encontrado relacionado ya
	 *            que 1 remito puede corresponder a más de 1 factura y/o a más
	 *            de un pedido.
	 * @param sqlParam
	 *            parámetro para las query sql parámetro
	 * @param errorMsg
	 *            mensaje de error en caso que la cantidad de remitos sea > a 1
	 * @param errorMsg2
	 *            mensaje de error en caso que la cantidad de relaciones del
	 *            único remito sea > a 1
	 * @return id de inout encontrado, 0 en caso que no se haya encontrado
	 *         ninguno
	 * @throws Exception
	 *             en caso que la cantidad de remitos sea > a 1 y que la
	 *             cantidad de transacciones relacionadas con el único remito
	 *             encontrado sea > a 1
	 */
	protected int inspectInOut(String sqlQueryForCount, String sqlQueryForGetInOut, String sqlQueryForInOutRelations, Integer sqlParam, String errorMsg, String errorMsg2) throws Exception{
		// Ejecuto el sql para obtener la cantidad de remitos existentes
		int cant = DB.getSQLValue(get_TrxName(), sqlQueryForCount, sqlParam);
		Integer inoutID = 0;
		// Si hay mas de uno entonces error
		if(cant > 1){
			throw new Exception(errorMsg);
		}
		// Si hay uno solo entonces lo encontré y obtengo su id
		else if(cant == 1){
			// Obtener el id del remito correspondiente
			inoutID = DB.getSQLValue(get_TrxName(), sqlQueryForGetInOut,
					sqlParam);
			// Verificar que el remito no esté en otros pedidos o facturas, ese
			// sería otro caso de error
			if(!Util.isEmpty(sqlQueryForInOutRelations)){
				cant = DB.getSQLValue(get_TrxName(), sqlQueryForInOutRelations,
						inoutID);
				if(cant > 1){
					throw new Exception(errorMsg2);
				}
			}			
		}
		return inoutID;
	}

	/* DEPRECTED: No se anulan los débitos/créditos de los allocation de la factura parámetro
	/**
	 * Obtener los débitos y créditos imputados dentro de los allocation
	 * parámetro, se excluye la factura parámetro al proceso
	 * 
	 * @param allocationHdrIDs
	 *            conjunto de id de allocation hdrs distintos
	 * @return lista distinta de ids de facturas
	 * @throws Exception
	 *             en caso de error
	 *
	protected Set<Integer> getDebitsCreditsIDs(Set<Integer> allocationHdrIDs) throws Exception{
		Set<Integer> ids = new HashSet<Integer>();
		if(allocationHdrIDs == null || allocationHdrIDs.size() <= 0) return ids;
		String allocationHdrIDsSet = allocationHdrIDs.toString().replace('[',
				'(').replace(']', ')');
		String sql = "SELECT c_invoice_id, c_invoice_credit_id " +
					 "FROM c_allocationhdr as ah " +
					 "INNER JOIN c_allocationline as al ON (al.c_allocationhdr_id = ah.c_allocationhdr_id) " +
					 "WHERE (docstatus IN ('CO','CL')) AND (al.c_invoice_id <> ?) AND (al.c_invoice_credit_id <> ?) AND ah.c_allocationhdr_id IN "+allocationHdrIDsSet; 
		PreparedStatement ps = DB.prepareStatement(sql, get_TrxName());
		ps.setInt(1, getInvoice().getID());
		ps.setInt(2, getInvoice().getID());
		ResultSet rs = ps.executeQuery();
		Integer debitID, creditID;
		while(rs.next()){
			debitID = rs.getInt("c_invoice_id");
			creditID = rs.getInt("c_invoice_credit_id");
			if(!Util.isEmpty(debitID, true)){
				ids.add(debitID);
			}
			if(!Util.isEmpty(creditID, true)){
				ids.add(creditID);
			}
		}
		ps.close();
		rs.close();
		return ids;
	}*/

	/**
	 * @return query que determina la cantidad de débitos que contiene un
	 *         allocation hdr particular que se le debe pasar como parámetro
	 */
	protected String getInvoiceCreditAllocationHdrQuery(){
		String sql = "SELECT count(distinct ah.c_allocationhdr_id) as cant " +
					 "FROM c_allocationhdr as ah " +
					 "INNER JOIN c_allocationline as al ON (ah.c_allocationhdr_id = al.c_allocationhdr_id) " +
					 "WHERE (ah.docstatus IN ('CO','CL')) AND (al.c_invoice_credit_id = ?)";
		return sql;
	}
	
	/**
	 * @return query que determina la cantidad de débitos que contiene un
	 *         allocation hdr particular que se le debe pasar como parámetro
	 */
	protected String getInvoiceDebitAllocationHdrQuery(){
		String sql = "SELECT count(distinct al.c_invoice_id) as cant " +
					 "FROM c_allocationhdr as ah " +
					 "INNER JOIN c_allocationline as al ON (ah.c_allocationhdr_id = al.c_allocationhdr_id) " +
					 "WHERE ah.c_allocationhdr_id = ?";
		return sql;
	} 
	
	
	/**
	 * @return query sql con la consulta que devuelve la cantidad de facturas
	 *         que tiene relacionado un pedido particular que se le debe pasar
	 *         como parámetro
	 */
	protected String getOrderInvoicesQuery(){
		String sql = "SELECT count(distinct il.c_invoice_id) as cant " +
					 "FROM c_order AS o " +
					 "INNER JOIN c_orderline AS ol ON (ol.c_order_id = o.c_order_id) " +
					 "INNER JOIN c_invoiceline AS il ON (il.c_orderline_id = ol.c_orderline_id) " +
					 "INNER JOIN c_invoice as i ON (i.c_invoice_id = il.c_invoice_id) " +
					 "WHERE (i.docstatus IN ('CO','CL')) AND (o.c_order_id = ?)";
		return sql;
	}

	
	/* DEPRECATED: No se anulan los débitos/créditos de los allocation de la factura parámetro
	/**
	 * @param lista
	 *            de exclusión de ids de allocation hdrs
	 * @return query sql que obtiene la cantidad de allocation hdrs que imputan
	 *         a una factura particular como débito o crédito que se debe pasar
	 *         como parámetro. Se excluyen los allocation hdrs parámetro.
	 *
	protected String getAllocationHdrInvoicesQuery(Set<Integer> excludedAllocationHdrIds){
		StringBuffer sql = new StringBuffer(
				"SELECT count(distinct ah.c_allocationhdr_id) as cant FROM c_allocationhdr as ah INNER JOIN c_allocationline as al ON (al.c_allocationhdr_id = ah.c_allocationhdr_id) WHERE (docstatus IN ('CO','CL')) AND ((al.c_invoice_id = ?) OR (al.c_invoice_credit_id = ?))");
		// Agregar el filtro de exclusión de allocation hdrs ids
		if (excludedAllocationHdrIds != null
				&& excludedAllocationHdrIds.size() > 0) {
			String allocHdrSet = excludedAllocationHdrIds.toString().replace('[', '(')
			.replace(']', ')');
			sql.append(allocHdrSet);
		}
		return sql.toString();
	}*/

	
	
	
	/**
	 * @return query sql con la consulta que devuelve la cantidad de remitos que
	 *         se relacionan con un pedido particular que debe pasarse como
	 *         parámetro
	 */
	protected String getInOutOrderQuery(){
		String sql = "SELECT count(distinct m_inout_id) as cant " +
					 "FROM m_inout as io " +
					 "WHERE (docstatus IN ('CO','CL')) AND (c_order_id = ?)";
		return sql;
	}

	/**
	 * @return query sql con la consulta que devuelve la cantidad de remitos que
	 *         se relacionan con una factura particular que debe pasarse como
	 *         parámetro
	 */
	protected String getInOutInvoiceQuery(){
		String sql = "SELECT count(distinct m_inout_id) as cant " +
					 "FROM m_inout as io " +
					 "WHERE (docstatus IN ('CO','CL')) AND (c_invoice_id = ?)";
		return sql;
	}
	
	/**
	 * @return query sql con la consulta que devuelve la cantidad de remitos que
	 *         se relacionan vía match PO con un pedido particular parámetro 
	 */
	protected String getInOutMatchPOQuery(){
		String sql = "SELECT count(distinct iol.m_inout_id) as cant " +
					 "FROM m_matchpo as mpo " +
					 "INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = mpo.m_inoutline_id) " +
					 "INNER JOIN c_orderline as ol ON (ol.c_orderline_id = mpo.c_orderline_id) " +
					 "WHERE ol.c_order_id = ?";
		return sql;
	}
	
	/**
	 * @return query sql con la consulta que devuelve la cantidad de remitos que
	 *         se relacionan vía match INV con la factura particular parámetro 
	 */
	protected String getInOutMatchInvQuery(){
		String sql = "SELECT count(distinct iol.m_inout_id) as cant " +
					 "FROM m_matchinv as minv " +
					 "INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = minv.m_inoutline_id) " +
					 "INNER JOIN c_invoiceline as il ON (il.c_invoiceline_id = minv.c_invoiceline_id) " +
					 "WHERE il.c_invoice_id = ?";
		return sql;
	}

	/**
	 * @return query sql con la consulta que devuelve el id del remito que se
	 *         relaciona vía match PO con el pedido de la factura
	 */
	protected String getInOutIDMatchPOQuery(){
		String sql = "SELECT distinct iol.m_inout_id " +
					 "FROM m_matchpo as mpo " +
					 "INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = mpo.m_inoutline_id) " +
					 "INNER JOIN c_orderline as ol ON (ol.c_orderline_id = mpo.c_orderline_id) " +
					 "WHERE ol.c_order_id = ?";
		return sql;
	}
	
	/**
	 * @return query sql con la consulta que devuelve el id del remito que se
	 *         relaciona vía match INV con la factura
	 */
	protected String getInOutIDMatchInvQuery(){
		String sql = "SELECT distinct iol.m_inout_id " +
		 			 "FROM m_matchinv as minv " +
		 			 "INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = minv.m_inoutline_id) " +
		 			 "INNER JOIN c_invoiceline as il ON (il.c_invoiceline_id = minv.c_invoiceline_id) " +
		 			 "WHERE il.c_invoice_id = ?";
		return sql;
	}

	/**
	 * @return query sql con la consulta que devuelve la cantidad de pedidos
	 *         relacionados vía Match PO con un remito particular que se debe
	 *         pasar como parámetro
	 */
	protected String getMatchPOInOutQuery(){
		String sql = "SELECT count(distinct ol.c_order_id) as cant " +
					 "FROM m_matchpo as mpo " +
					 "INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = mpo.m_inoutline_id) " +
					 "INNER JOIN c_orderline as ol ON (ol.c_orderline_id = mpo.c_orderline_id) " +
					 "WHERE iol.m_inout_id = ?";
		return sql;
	}

	
	/**
	 * @return query sql con la consulta que devuelve la cantidad de facturas
	 *         relacionadas vía Match INV con un remito particular que se debe
	 *         pasar como parámetro
	 */
	protected String getMatchINVInOutQuery(){
		String sql = "SELECT count(distinct il.c_invoice_id) as cant " +
					 "FROM m_matchinv as minv " +
					 "INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = minv.m_inoutline_id) " +
					 "INNER JOIN c_invoiceline as il ON (il.c_invoiceline_id = minv.c_invoiceline_id) " +
					 "WHERE iol.m_inout_id = ?";
		return sql;
	}

	/**
	 * Crea una lista html para el mensaje final con un id parámetro
	 * 
	 * @param id
	 *            id de la lista
	 * @return nueva lista html
	 */
	protected HTMLList createHTMLList(String id, String msg){
		return getMsg().createList(id, HTMLListHeader.UL_LIST_TYPE, msg);
	}

	/**
	 * Recupero un mensaje de la BD a partir del ad_message
	 * 
	 * @param adMessage
	 *            clave de mensaje
	 * @return descripción del mensaje
	 */
	protected String getMsg(String adMessage){
		return Msg.getMsg(getCtx(), adMessage);
	}
	
	/**
	 * @return mensaje final del proceso
	 */
	protected String getFinalMsg(){
		getMsg().setHeaderMsg(
				Msg.parseTranslation(getCtx(),
						"@ProcessOK@. @TransactionsVoidedAre@"));
		return getMsg().toString();
	}
	
	
	// Getters y Setters
	
	protected void setAllocHdrs(List<MAllocationHdr> allocHdrs) {
		this.allocHdrs = allocHdrs;
	}

	protected List<MAllocationHdr> getAllocHdrs() {
		return allocHdrs;
	}

	protected void setInvoice(MInvoice invoice) {
		this.invoice = invoice;
	}

	protected MInvoice getInvoice() {
		return invoice;
	}

	protected void setOrder(MOrder order) {
		this.order = order;
	}

	protected MOrder getOrder() {
		return order;
	}

	protected void setInOut(MInOut inOut) {
		this.inOut = inOut;
	}

	protected MInOut getInOut() {
		return inOut;
	}

	/* DEPRECATED: No se anulan los débitos/créditos de los allocation de la factura parámetro
	protected void setDebitsCredits(Set<MInvoice> debitsCredits) {
		this.debitsCredits = debitsCredits;
	}

	protected Set<MInvoice> getDebitsCredits() {
		return debitsCredits;
	}
	*/
	
	protected void setAditionalWorks(Map<PO, Object> aditionalWorks) {
		this.aditionalWorks = aditionalWorks;
	}

	protected Map<PO, Object> getAditionalWorks() {
		return aditionalWorks;
	}

	protected void setMsg(HTMLMsg msg) {
		this.msg = msg;
	}

	protected HTMLMsg getMsg(){
		return this.msg;
	}

	@Override
	protected String get_TrxName() {
		if (localTrxName != null) {
			return localTrxName;
		} else {
			return super.get_TrxName();
		}
	}
	
	@Override
	public Properties getCtx() {
		if (localCtx != null) {
			return localCtx;
		} else {
			return super.getCtx();
		}
	}

	/**
	 * Comienza la ejecución del proceso.
	 * 
	 * @return Mesaje HTML con los documentos anulados
	 * @throws Exception
	 *             cuando se produce un error en la anulación de alguno de los
	 *             documentos.
	 */
	public String start() throws Exception {
		return doIt();
	}

	public void setControlMoreDebitsInAllocation(
			boolean controlMoreDebitsInAllocation) {
		this.controlMoreDebitsInAllocation = controlMoreDebitsInAllocation;
	}

	public boolean isControlMoreDebitsInAllocation() {
		return controlMoreDebitsInAllocation;
	}
}
