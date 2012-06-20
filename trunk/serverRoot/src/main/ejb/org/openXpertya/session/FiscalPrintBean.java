package org.openXpertya.session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.PO;
import org.openXpertya.model.RemoteFiscalDocumentPrint;
import org.openXpertya.print.fiscal.document.CreditNote;
import org.openXpertya.print.fiscal.document.DebitNote;
import org.openXpertya.print.fiscal.document.Document;
import org.openXpertya.print.fiscal.document.Invoice;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;

/**
 * Bean de Impresión Fiscal Remota de Libertya. Encargada de imprimir a una
 * impresora fiscal de forma remota.
 * 
 * @ejb:bean name="openXpertya/FiscalPrint"
 *           display-name="Libertya Fiscal Print Bean" type="Stateless"
 *           transaction-type="Bean" jndi-name="ejb/openXpertya/FiscalPrint"
 * 
 * @ejb:ejb-ref ejb-name="openXpertya/FiscalPrint"
 *              ref-name="openXpertya/FiscalPrint"
 * 
 * @author Equipo de Desarrollo de Libertya
 * 
 */
public class FiscalPrintBean implements SessionBean {

	/** Serial Default */
	private static final long serialVersionUID = 1L;
	/**	Context	*/
	private SessionContext 	m_Context;
	/**	Logger */
	private static CLogger log = CLogger.getCLogger(ServerBean.class);
	
	public FiscalPrintBean() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void ejbActivate() throws EJBException, RemoteException {
		if (log == null)
			log = CLogger.getCLogger(getClass());
		log.info ("ejbActivate ");
	}

	@Override
	public void ejbPassivate() throws EJBException, RemoteException {
		log.info ("ejbActivate ");		
	}

	@Override
	public void ejbRemove() throws EJBException, RemoteException {
		log.info ("ejbActivate ");		
	}

	@Override
	public void setSessionContext(SessionContext arg0) throws EJBException,
			RemoteException {
		m_Context = arg0;		
	}
	
	/**************************************************************************
	 * 	Create the Session Bean - Obligatorio implementar
	 * 	@throws CreateException
	 *  @ejb:create-method view-type="remote"
	 */
	public void ejbCreate() throws CreateException{}
	
	/**************************************************************************/
	
	public SessionContext getSessionContext(){
		return m_Context;
	}
	
	
	/**
	 * Obtengo el PO a partir de la tabla, nombre de la columna que identifica
	 * unívocamente a los registros y su valor.
	 * 
	 * @param ctx
	 *            contexto
	 * @param tableName
	 *            nombre de la tabla
	 * @param columnNameUID
	 *            nombre de la columna que identifica unívocamente la entidad
	 *            comercial
	 * @param valueUID
	 *            valor de la columna descrita
	 * @param trxName
	 *            nombre de la transacción
	 * @return PO de esa tabla con el valor de esa columna única, null caso
	 *         contrario
	 */
	private PO getPO(Properties ctx, String tableName, String columnNameUID,
			Object valueUID, String trxName) {
		return PO.findFirst(ctx, tableName, columnNameUID + " = ?",
				new Object[] { valueUID }, null, trxName);
	}

	/**
	 * @param ctx
	 *            contexto
	 * @param columnNameUID
	 *            nombre de la columna que identifica unívocamente el tipo de
	 *            documento
	 * @param valueUID
	 *            valor de la columna que identifica unívocamente el tipo de
	 *            documento
	 * @param trxName
	 *            nombre de la transacción actual
	 * @return tipo de documento con esos criterios o null si no existe
	 */
	private MDocType getDocType(Properties ctx, String columnNameUID,
			Object valueUID, String trxName) {
		return (MDocType) getPO(ctx, MDocType.Table_Name, columnNameUID,
				valueUID, trxName);
	}
	
	/*
	 * **********************************************************
	 * 					MÉTODOS REMOTOS
	 * **********************************************************
	 */

	/**
	 * Imprime una factura sobre una impresora fiscal
	 * 
	 * @param ctx
	 *            contexto
	 * @param invoice
	 *            factura oxp
	 * @param document
	 *            factura imprimible por una impresora fiscal, creada a partir
	 *            de la factura oxp parámetro
	 * @param originalInvoice
	 *            factura original obtenida de la factura oxp parámetro, usada
	 *            para la impresión de notas de crédito
	 * @param docTypeColumnNameUID
	 *            nombre de la columna de clave única del tipo de documento de
	 *            la factura oxp parámetro
	 * @param docTypeColumnValueUID
	 *            valor de la columna de clave única del tipo de documento de la
	 *            factura oxp parámetro
	 * @return resultado de la llamada, si el resultado está seteado como error
	 *         significa que no se pudo imprimir correctamente y dentro de ésta
	 *         se encuentra el mensaje de error.
	 * @throws RemoteException
	 * @ejb:interface-method view-type="remote"
	 */
	public CallResult printDocument(Properties ctx, PO invoice, Document document, MInvoice originalInvoice, String docTypeColumnNameUID, Object docTypeColumnValueUID) throws RemoteException{
		CallResult result = new CallResult();
		// Obtener el tipo de documento a partir de la información parámetro
		MDocType docType = getDocType(ctx, docTypeColumnNameUID,
				docTypeColumnValueUID, null);
		// Creo la gestión de impresión fiscal remota
		RemoteFiscalDocumentPrint rfdp = new RemoteFiscalDocumentPrint();
		// Impresión del documento
		boolean printOK = rfdp.printDocument(invoice, document, docType, originalInvoice);
		// Seteo si fue error y el mensaje
		result.setMsg(rfdp.getErrorMsg(), !printOK);
		// TODO El resultado va una lista con la factura y el documento
		// imprimible, en el caso que RMI no soporta la modificación de los
		// objetos parámetros
		List<Object> data = new ArrayList<Object>();
		data.add(invoice);
		data.add(document);
		result.setResult(data);
		return result;
	}
}
