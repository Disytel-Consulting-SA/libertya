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

import java.math.*;
import java.sql.*;

import org.openXpertya.model.*;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.*;

/**
 *	Import Order from I_Order
 *
 * 	@author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � 	Jorg Janke
 * 	@version 	$Id: ImportOrder.java,v 0.9 $
 */
public class ImportListaGalicia extends SvrProcess
{
	/**
	 * 	Import BPartner Constructor
	 */
	public ImportListaGalicia()
	{
		super();
	}	//	ImportOrder

	/**	Client to be imported to		*/
	private int				m_AD_Client_ID = 0;
	/**	Organization to be imported to		*/
	private int				m_AD_Org_ID = 0;
	/**	Delete old Imported				*/
	private boolean			m_deleteOldImported = false;


	/** Effective						*/
	private Timestamp		m_DateValue = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("DeleteOldImported"))
				m_deleteOldImported = "Y".equals(para[i].getParameter());
		}
		if (m_DateValue == null)
			m_DateValue = Env.getDate();
	}	//	prepare


	/**
	 *  Perrform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		StringBuffer sql = null;
		int no = 0;
		String clientCheck = " AND AD_Client_ID=" + m_AD_Client_ID;

		//	****	Prepare	****

		//	Delete Old Imported
		if (m_deleteOldImported)
		{
			sql = new StringBuffer ("DELETE I_Lista_Galicia "
				  + "WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate (sql.toString());
		}

		//	Set Client, Org, IsActive, Created/Updated
		sql = new StringBuffer ("UPDATE I_Lista_Galicia "
			  + "SET AD_Client_ID = COALESCE (AD_Client_ID,").append (m_AD_Client_ID).append ("),"
			  + " AD_Org_ID = COALESCE (AD_Org_ID,").append (m_AD_Org_ID).append ("),"
			  + " IsActive = COALESCE (IsActive, 'Y'),"
			  + " Created = COALESCE (Created, SysDate),"
			  + " CreatedBy = COALESCE (CreatedBy, 0),"
			  + " Updated = COALESCE (Updated, SysDate),"
			  + " UpdatedBy = COALESCE (UpdatedBy, 0),"
			  + " I_ErrorMsg = NULL,"
			  + " I_IsImported = 'N' "
			  + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL");
		no = DB.executeUpdate (sql.toString ());
		log.info ("doIt - Reset=" + no);
		
		
		/* 
		 * **************************************************
		 * SE PROCEDE A ACTUALIZAR LOS NROS DE LOS CHEQUES
		 ***************************************************/
		
		//BUSCO LOS REGISTROS NO "IMPORTADOS" TODAVIA
		sql = new StringBuffer("SELECT I_LISTA_GALICIA_ID, ");
		sql.append("ESTADO, ");
		sql.append("NVL(NUMERO,'') AS NUMERO, ");
		sql.append("NVL(LISTA,'') AS LISTA, ");
		sql.append("NVL(POSICION,'') AS POSICION, ");
		sql.append("NVL(CUIT,'') AS CUIT, ");
		sql.append("NVL(CUENTA_ESPECIFICA,'') AS CUENTA_ESPECIFICA, ");
		sql.append("NVL(ORDEN_DE_PAGO,'') AS ORDEN_DE_PAGO, ");
		sql.append("FECHA_PAGO, ");
		sql.append("NVL(MONTO,0)/100) AS MONTO ");
		sql.append("FROM I_LISTA_GALICIA ");
		sql.append("WHERE I_ISIMPORTED = 'N' AND CODIGO = 'PD' AND ESTADO IN ('N','P','S','T') ");
		
		String numero,cuit,ordenDePago,lista, posicion, cuentaEspecifica;
		Timestamp fechaPago;
		BigDecimal monto = Env.ZERO;
		int I_Lista_Galicia_ID = 0;
		int C_Lista_Galicia_ID = 0;
		int cuentaEspecificaInt = 0;
		int chequesActualizados = 0;
		int chequesNoEncontrados = 0;
		
		MDocType lgdt = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_Lista_Galicia, get_TrxName());
		
		PreparedStatement pstmt = DB.prepareStatement(sql.toString());
		ResultSet rs = pstmt.executeQuery();
		while(rs.next())
		{
			I_Lista_Galicia_ID = rs.getInt("I_LISTA_GALICIA_ID");
			numero = rs.getString("NUMERO");
			lista = rs.getString("LISTA");
			posicion = rs.getString("POSICION");
			cuit = rs.getString("CUIT");
			cuentaEspecifica = rs.getString("CUENTA_ESPECIFICA");
			try
			{
			   cuentaEspecificaInt = Integer.parseInt(cuentaEspecifica);
			}
			catch(NumberFormatException ex)
			{
				cuentaEspecificaInt = 0;
			}
			
			ordenDePago = rs.getString("ORDEN_DE_PAGO");
			// Confeccionar nro de OP
			MDocType opDocType = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_Orden_De_Pago, get_TrxName());
			if(opDocType == null){
				throw new Exception();
			}
			String opPrefix = MSequence.getPrefix(opDocType.getDocNoSequence_ID(), get_TrxName());
			String opSuffix = MSequence.getSuffix(opDocType.getDocNoSequence_ID(), get_TrxName());
			
			ordenDePago = opPrefix+ordenDePago+opSuffix; 
			fechaPago = rs.getTimestamp("FECHA_PAGO");
			monto = rs.getBigDecimal("MONTO");				
			
			// BUSCO EN LA TABLA C_PAYMENT LOS PAYMENTS QUE MATCHEEN CON LOS
			// DATOS IMPORTADOS EN I_LISTA_GALICIA
			sql = new StringBuffer("select p.C_Payment_ID, coalesce(p.c_banklist_id,0) as c_lista_galicia_id ");
			sql.append("from c_electronic_payments p ");
			sql.append("inner join c_bpartner b on p.c_bpartner_id = b.c_bpartner_id ");
			sql.append("inner join c_bankaccount ba on ba.c_bankaccount_id = p.c_bankaccount_id ");
			sql.append("left join c_allocationhdr ah on ah.c_allocationhdr_id = p.c_allocationhdr_id ");
			sql.append("where ah.documentno = ? ");
			sql.append("and p.payamt = ? ");
			sql.append("and p.dateacct::date = ?::date ");
			sql.append("and ba.accountno = ").append(cuentaEspecificaInt);
			sql.append("and ba.IsActive = 'Y' ");
			sql.append("and replace(b.taxid,'-','') = '").append(cuit).append("'");
			sql.append(" and p.c_doctype_id = ? ");
			
			pstmt = DB.prepareStatement(sql.toString(), null, true);
			try{
				pstmt.setString(1,ordenDePago);
				pstmt.setBigDecimal(2,monto);
				pstmt.setTimestamp(3,fechaPago);
				pstmt.setInt(4, lgdt.getID());
				
				ResultSet rsPayment = pstmt.executeQuery();
				if(rsPayment.next()) {
					//PROCEDO A ACTUALIZAR LOS NROS DE CHEQUES.
					MPayment payment = new MPayment(getCtx(), rsPayment.getInt("C_Payment_ID"), null);
					C_Lista_Galicia_ID = rsPayment.getInt("C_Lista_Galicia_ID");
					
					if(C_Lista_Galicia_ID == 0) {
						sql = new StringBuffer("UPDATE I_LISTA_GALICIA ");
						sql.append("SET I_ISIMPORTED = 'N', I_ERRORMSG = ? ");
						sql.append("WHERE I_LISTA_GALICIA_ID = ? ");
						pstmt = DB.prepareStatement(sql.toString());
						pstmt.setString(1,"EL CHEQUE CORRESPONDIENTE PERTENECE A UNA OP QUE NO POSEE UNA LISTA GALICIA ASOCIADA");
						pstmt.setInt(2,I_Lista_Galicia_ID);
						pstmt.executeUpdate();
						pstmt.close();
					}
					else {
					    payment.setCheckNo(numero);
					    payment.setA_Ident_DL(lista+"-"+posicion);
					    //SI EL SAVE = FALSE, SETEO EL FLAG I_ISIMPORTED = 'N' 
					    if(!payment.save()) {
						    sql = new StringBuffer("UPDATE I_LISTA_GALICIA ");
						    sql.append("SET I_ISIMPORTED = 'N', I_ERRORMSG = ? ");
						    sql.append("WHERE I_LISTA_GALICIA_ID = ? ");
						    pstmt = DB.prepareStatement(sql.toString());
						    pstmt.setString(1,CLogger.retrieveErrorAsString());
						    pstmt.setInt(2,I_Lista_Galicia_ID);
						    pstmt.executeUpdate();
						    pstmt.close();
					    }
					    chequesActualizados++;
					}
				}
				//SI NO ENCUENTRO EL PAYMENT "ASOCIADO" SETEO EL MSJ CORRESPONDIENTE.
				else {
					sql = new StringBuffer("UPDATE I_LISTA_GALICIA ");
					sql.append("SET I_ISIMPORTED = 'N', I_ERRORMSG = ? ");
					sql.append("WHERE I_LISTA_GALICIA_ID = ? ");
					pstmt = DB.prepareStatement(sql.toString());
					pstmt.setString(1,"NO SE ENCONTRO EL CHEQUE ASOCIADO, VER FECHA DE PAGO, IMPORTE O CUENTA BANCARIA");
					pstmt.setInt(2,I_Lista_Galicia_ID);
					pstmt.executeUpdate();
					pstmt.close();
					chequesNoEncontrados++;
				}
				
			}
			catch(SQLException ex) {				
				throw ex;
			}
		}
		
		sql = new StringBuffer("UPDATE I_LISTA_GALICIA ");
		sql.append("SET I_ISIMPORTED = 'N', I_ERRORMSG = ? ");
		sql.append("WHERE ESTADO LIKE 'R%' ");
		pstmt = DB.prepareStatement(sql.toString());
		pstmt.setString(1,"EL CHEQUE FUE RECHAZADO, VER TABLA DE ESTADOS");
		pstmt.executeUpdate();		
		pstmt.close();
		
		return "Proceso finalizado satisfactoriamente. Cheques Actualizados: " + chequesActualizados
				+ ". Cheques no encontrados: " + chequesNoEncontrados;
	}	//	doIt

}	//	ImportOrder
