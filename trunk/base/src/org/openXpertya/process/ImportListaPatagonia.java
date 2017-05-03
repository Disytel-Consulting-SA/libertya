/******************************************************************************
 *     El contenido de este fichero est� sujeto a la Licencia P�blica openXpertya versi�n 1.0 (LPO) en
 * tanto cuanto forme parte �ntegra del total del producto denominado:     openXpertya, soluci�n 
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *     Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *     Partes del c�digo son CopyRight � 2002-2005 de Ingenier�a Inform�tica Integrada S.L., otras 
 * partes son CopyRight  � 2003-2005 de Consultor�a y Soporte en Redes y  Tecnolog�as de 
 * la  Informaci�n S.L., otras partes son adaptadas, ampliadas o mejoradas a partir de c�digo original
 * de terceros, recogidos en el ADDENDUM A, secci�n 3 (A.3) de dicha licencia LPO, y si dicho c�digo
 * es extraido como parte del total del producto, estar� sujeto a sus respectiva licencia original.  
 *     M�s informaci�n en http://www.openxpertya.org/licencia.html
 ******************************************************************************/

package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MSequence;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/**
 *	Import GL Journal Batch/JournalLine from I_Journal
 *
 * 	@author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � 	Jorg Janke
 * 	@version 	$Id: ImportGLJournal.java,v 0.9 $
 */
public class ImportListaPatagonia extends SvrProcess
{
	/**	Client to be imported to		*/
	private int 			m_AD_Client_ID = 0;
	/**	Organization to be imported to	*/
	private int 			m_AD_Org_ID = 0;
	/**	Acct Schema to be imported to	*/
	private int				m_C_AcctSchema_ID = 0;
	/** Default Date					*/
	private Timestamp		m_DateAcct = null;
	/**	Delete old Imported				*/
	private boolean			m_DeleteOldImported = false;
	/**	Don't import					*/
	private boolean			m_IsValidateOnly = false;
	/** Import if no Errors				*/
	private boolean			m_IsImportOnlyNoErrors = true;


	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Client_ID"))
				m_AD_Client_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("AD_Org_ID"))
				m_AD_Org_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("C_AcctSchema_ID"))
				m_C_AcctSchema_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("DateAcct"))
				m_DateAcct = (Timestamp)para[i].getParameter();
			else if (name.equals("IsValidateOnly"))
				m_IsValidateOnly = "Y".equals(para[i].getParameter());
			else if (name.equals("IsImportOnlyNoErrors"))
				m_IsImportOnlyNoErrors = "Y".equals(para[i].getParameter());
			else if (name.equals("DeleteOldImported"))
				m_DeleteOldImported = "Y".equals(para[i].getParameter());
		}
	}	//	prepare


	/**
	 *  Perrform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		int updatedPayments = 0;
		String clientCheck = " AND AD_Client_ID=" + Env.getAD_Client_ID(getCtx());
		
		String nroCheque;
		Date fechaEmision;
		Date fechaVto;
		Integer C_Payment_ID, iListaPatagoniaID;
		String isImported = "N";
		String errorMsg = "";
		int errors = 0;
		
		if (m_DeleteOldImported){
			StringBuffer sql = new StringBuffer ("DELETE I_LISTA_PATAGONIA "
				  + "WHERE I_IsImported='Y'").append (clientCheck);
			int no = DB.executeUpdate (sql.toString());
		}
		
		MDocType lpdt = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_Lista_Patagonia, get_TrxName());
		
		MDocType opDocType = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_Orden_De_Pago, get_TrxName());
		if(opDocType == null){
			throw new Exception("No existe el tipo de documento Orden de Pago");
		}
		
		// Obtener prefijo y sufijo de la secuencia de la OP
		String opPrefix = MSequence.getPrefix(opDocType.getDocNoSequence_ID(), get_TrxName());
		opPrefix = opPrefix == null?"":opPrefix;
		String opSuffix = MSequence.getSuffix(opDocType.getDocNoSequence_ID(), get_TrxName());
		opSuffix = opSuffix == null?"":opSuffix;
		
		StringBuffer sql = new StringBuffer ("UPDATE I_LISTA_PATAGONIA "
				+ "SET C_Payment_ID = ( SELECT distinct lpp.C_Payment_ID " +
										"FROM c_electronic_payments lpp " +
										"INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = lpp.c_allocationhdr_id " +
									   "WHERE op_ref = translate(translate(ah.documentno,'" + opPrefix + "',''), '" + opSuffix+ "', '') "
									   		+ " and lpp.c_doctype_id = "+lpdt.getID()+""
									   		+ " limit 1)");
		int no = DB.executeUpdate (sql.toString ());
			log.info ("doIt - Cheques Encontrados = " + no);
			
		sql = new StringBuffer ("UPDATE I_LISTA_PATAGONIA "
				+ "SET I_ErrorMsg = 'No se encontró la referencia al medio de pago original' "
		        + "WHERE C_PAYMENT_ID is null " );
		no = DB.executeUpdate (sql.toString ());
				log.info ("doIt - Cheques No Encontrados = " + no);			
			

		sql = new StringBuffer ("SELECT I_LISTA_PATAGONIA_ID, Op_Ref, Nro_Chq_Usado," +
				" Beneficiario, F_Emision, F_Vto_Cpd, C_Payment_ID "
			+	" FROM I_LISTA_PATAGONIA "
			+ "WHERE I_IsImported = 'N'").append (clientCheck);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			pstmt = DB.prepareStatement (sql.toString ());
			rs = pstmt.executeQuery ();
			//
			while (rs.next()) {
				iListaPatagoniaID = rs.getInt("I_LISTA_PATAGONIA_ID");
				nroCheque = rs.getString("Nro_Chq_Usado");
				fechaEmision = rs.getDate("F_Emision");
				fechaVto = rs.getDate("F_Vto_Cpd");
				C_Payment_ID = rs.getInt("C_Payment_ID");
				
				if(!Util.isEmpty(C_Payment_ID, true)){
					MPayment payment = new MPayment(this.getCtx(),C_Payment_ID.intValue(), null);
					if(fechaEmision != null){
						payment.setDateTrx(new Timestamp(fechaEmision.getTime()));
						payment.setDateEmissionCheck(new Timestamp(fechaEmision.getTime()));
					}
					if(fechaVto != null){
						payment.setDateAcct(new Timestamp(fechaVto.getTime()));
						payment.setDueDate(new Timestamp(fechaVto.getTime()));
					}
					if(!Util.isEmpty(nroCheque, true)){
						payment.setCheckNo(nroCheque.toString());
					}
					if(payment.save()){
						updatedPayments++;
						isImported = "Y";
						errorMsg = "Nro. de cheque actualizado satisfactoriamente";
					}else{
						errors++;
						isImported = "N";
						errorMsg = "Procesado, pero el nro. de cheque no fue actualizado";
					}
					sql = new StringBuffer ("UPDATE I_LISTA_PATAGONIA "
							+ "SET I_IsImported = '"+isImported+"', I_ErrorMsg = '"+errorMsg+"' "
							+ "WHERE I_LISTA_PATAGONIA_ID = "+iListaPatagoniaID);
					
					no = DB.executeUpdate (sql.toString ());
				}
			}
		}
		catch (Exception e)	{
			throw e;
		} finally {
			try{
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				throw e2;
			}
		}		

		addLog (0, null, new BigDecimal (errors), "@Errors@");
		addLog (0, null, new BigDecimal (updatedPayments), "@C_Payment_ID@: @Inserted@");
		return "";
	}	//	doIt

}	//	ImportGLJournal
