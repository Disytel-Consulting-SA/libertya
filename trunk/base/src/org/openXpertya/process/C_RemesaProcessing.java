/*
 * @(#)C_RemesaProcessing.java   12.oct 2007  Versión 2.2
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


package org.openXpertya.process;

import java.math.*;
import java.sql.*;
import java.util.*;

import org.openXpertya.model.*;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.*;

/**
 *	Procesador de Remesas
 *
 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Jose A. Gonzalez, Conserti.
 * 
 *  @version $Id: C_RemesaProcessing.java,v 0.9 $
 * 
 *  @Colaborador $Id: Consultoria y Soporte en Redes y Tecnologias de la Informacion S.L.
 * 
 */

public class C_RemesaProcessing extends SvrProcess
{
	/**
	 * 	C_RemesaProcessing Constructor
	 */
	public C_RemesaProcessing()
	{
		super();
		log.fine("C_RemesaProcessing");
	}	//	ImportBPartner

	/**	Export record id to perform the export	*/
	private int				m_C_Remesa_ID = 0;
	/** MRemesa */
	private MRemesa 		remesa;
	/** Window No */
	private int 			m_WindowNo = 0;
	/** Context */
	private Properties 		m_ctx;
	/** Calendar */
	private Calendar 		c;
	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		m_ctx = getCtx();
		m_WindowNo = Env.createWindowNo(null); 
		
		m_C_Remesa_ID = getRecord_ID();
		remesa = new MRemesa(m_ctx, m_C_Remesa_ID,null);
	}	//	prepare


	/**
	 *  Perrform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		introducirPagos();
		return "";
	}	//	doIt
	
	private void introducirPagos()
	{
		MRemesaLine[] lines = MRemesaLine.getRemesaLines(m_ctx, m_C_Remesa_ID);
		log.fine("Entro en introducir Pagos de C_RemesaProcessing, con lines="+lines.length);
		int C_BankAccount_ID = 0;
		Timestamp fechaActual = Env.getContextAsDate(m_ctx, "#Date");
		Timestamp fechaEjecucion = remesa.getExecuteDate();
		int C_Currency_ID = 0;
		
		String sql = "SELECT bac.C_BankAccount_ID FROM C_Remesa rem";
		sql += " INNER JOIN C_Bank ban ON (rem.AD_Org_ID=ban.AD_Org_ID AND ban.IsOwnBank='Y')";
		sql += " INNER JOIN C_BankAccount bac ON (ban.C_Bank_ID=bac.C_Bank_ID)";
		sql += " WHERE rem.C_Remesa_ID=" + m_C_Remesa_ID;
		//
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				C_BankAccount_ID = rs.getInt(1);

			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{ 
			log.saveError("C_RemesaProcessing - consulta de cuenta de banco", e);
		}

		for (int nLinea = 0; nLinea < lines.length; nLinea++)
		{
			log.fine("Entro en el primer for, lines.length="+lines.length);
			MInvoicePaySchedule[] ips = MInvoicePaySchedule.getIPSRemesados(m_ctx, lines[nLinea]);
			/*for (int nIps = 0; nIps < ips.length; nIps++)
			{
				log.fine("Entro en el segundo for, con ips.length="+ips.length);
				BigDecimal payAmt = ips[nIps].getDueAmt();
				
				MPayment payment = new MPayment (m_ctx, 0, null);
				payment.setC_BankAccount_ID(C_BankAccount_ID);
				payment.setTenderType(MPayment.TENDERTYPE_DirectDebit);
				payment.setDocStatus(MPayment.DOCSTATUS_Drafted);
				payment.setDocAction(MPayment.ACTION_Complete);
				if (fechaActual != null)
					payment.setDateTrx(fechaActual);
				else if (fechaEjecucion != null)
					payment.setDateTrx(fechaEjecucion);
				if (fechaEjecucion != null)
					payment.setDateAcct(fechaEjecucion);
				else
					payment.setDateAcct(payment.getDateTrx());
				payment.setDescription(lines[nLinea].getDescription());
				//
				if (ips[nIps].getC_Invoice_ID() != 0)
				{
					log.fine("Entra en el if ips[nIps].getC_Invoice_ID con el valor="+ips[nIps].getC_Invoice_ID());
					MInvoice invoice = new MInvoice (getCtx(), ips[nIps].getC_Invoice_ID(),null);
					payment.setC_DocType_ID(invoice.isSOTrx());		//	Receipt
					payment.setC_Invoice_ID(invoice.getC_Invoice_ID());
					payment.setC_BPartner_ID (invoice.getC_BPartner_ID());
					if (payAmt.compareTo(Env.ZERO) != 0)	//	explicit Amount
					{
						payment.setC_Currency_ID(invoice.getC_Currency_ID());
						if (invoice.isSOTrx()){
							payment.setPayAmt(payAmt);
							log.fine("Entro en el if de invoice.isSOTrx() y payAmt="+payAmt);
						}else	//	payment is likely to be negative
							payment.setPayAmt(payAmt.negate());
						payment.setOverUnderAmt(payAmt.subtract(payment.getPayAmt()));
					}
					else	// set Pay Amout from Invoice
					{
						log.fine("Entro en el else de payAmt.CompareTo(0), payAmt="+invoice.getGrandTotal(true)+", y currency_id="+invoice.getC_Currency_ID());
						payment.setC_Currency_ID(invoice.getC_Currency_ID());
						payment.setPayAmt(invoice.getGrandTotal(true));
					}
				}

				payment.save();		
			}*/
			
			log.fine("Entro en el segundo for, con ips.length="+ips.length);
			BigDecimal payAmt = ips[nLinea].getDueAmt();
			
			MPayment payment = new MPayment (m_ctx, 0, null);
			payment.setC_BankAccount_ID(C_BankAccount_ID);
			payment.setTenderType(MPayment.TENDERTYPE_DirectDebit);
			payment.setDocStatus(MPayment.DOCSTATUS_Drafted);
			payment.setDocAction(MPayment.ACTION_Complete);
			if (fechaActual != null)
				payment.setDateTrx(fechaActual);
			else if (fechaEjecucion != null)
				payment.setDateTrx(fechaEjecucion);
			if (fechaEjecucion != null)
				payment.setDateAcct(fechaEjecucion);
			else
				payment.setDateAcct(payment.getDateTrx());
			payment.setDescription(lines[nLinea].getDescription());
			//
			if (ips[nLinea].getC_Invoice_ID() != 0)
			{
				log.fine("Entra en el if ips[nIps].getC_Invoice_ID con el valor="+ips[nLinea].getC_Invoice_ID());
				MInvoice invoice = new MInvoice (getCtx(), ips[nLinea].getC_Invoice_ID(),null);
				payment.setC_DocType_ID(invoice.isSOTrx());		//	Receipt
				payment.setC_Invoice_ID(invoice.getC_Invoice_ID());
				payment.setC_BPartner_ID (invoice.getC_BPartner_ID());
				if (payAmt.compareTo(Env.ZERO) != 0)	//	explicit Amount
				{
					payment.setC_Currency_ID(invoice.getC_Currency_ID());
					if (invoice.isSOTrx()){
						payment.setPayAmt(payAmt);
						log.fine("Entro en el if de invoice.isSOTrx() y payAmt="+payAmt);
					}else	//	payment is likely to be negative
						payment.setPayAmt(payAmt.negate());
					payment.setOverUnderAmt(payAmt.subtract(payment.getPayAmt()));
				}
				else	// set Pay Amout from Invoice
				{
					log.fine("Entro en el else de payAmt.CompareTo(0), payAmt="+invoice.getGrandTotal(true)+", y currency_id="+invoice.getC_Currency_ID());
					payment.setC_Currency_ID(invoice.getC_Currency_ID());
					payment.setPayAmt(invoice.getGrandTotal(true));
				}
			}

			payment.save();		
		
			
		}
	}
}	//	ExportData
