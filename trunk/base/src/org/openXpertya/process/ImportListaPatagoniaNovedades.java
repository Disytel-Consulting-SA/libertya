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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.X_I_Lista_Patagonia_Novedades;
import org.openXpertya.util.DB;

/**
 *	Import GL Journal Batch/JournalLine from I_Journal
 *
 * 	@author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � 	Jorg Janke
 * 	@version 	$Id: ImportGLJournal.java,v 0.9 $
 */
public class ImportListaPatagoniaNovedades extends SvrProcess
{
	/**	Delete old Imported				*/
	private boolean			m_DeleteOldImported = false;


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
		int noInsertados = 0;
		int insertados = 0;
		
		if(m_DeleteOldImported){
			String query = "DELETE FROM I_Lista_Patagonia_Novedades WHERE I_ISIMPORTED = 'Y'";
			int no = DB.executeUpdate(query, get_TrxName());
			log.info("Registros Eliminados: "+no);
		}
		
		String registro;
		
		StringBuffer sql = new StringBuffer ("");
		sql.append("SELECT * FROM I_Lista_Patagonia_Novedades WHERE I_ISIMPORTED <> 'Y' ORDER BY FILENAME, I_PATAGONIA_NOVEDADES_ID");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			rs = pstmt.executeQuery ();
			while(rs.next()){
				X_I_Lista_Patagonia_Novedades reg = new X_I_Lista_Patagonia_Novedades(getCtx(), rs, get_TrxName());
				registro = reg.getRegistro();
				
				if(reg.getConstante().equals("FH")){//CABECERA
						
					reg.setFh_IDArchivo(registro.substring(2,22));
					reg.setFh_HoraCreacion(registro.substring(22, 28));
					reg.setFh_NroSecuencial(registro.substring(28, 35));
					reg.setFh_Identificacion(registro.substring(35, 46));
					reg.setFh_FechaProceso(registro.substring(53, 61));
					reg.setFh_AInformante(registro.substring(82, 85));
					reg.setFh_NroInformante(registro.substring(85, 92));
				}
				if(reg.getConstante().equals("N1")){//CABECERA
					
					reg.setN1_IDArchivoRecibido(registro.substring(2,22));
					reg.setN1_ReferenciaPago(registro.substring(22,47));
					reg.setN1_NroPagoSistema(registro.substring(47,55));
					reg.setN1_SubNroPago(registro.substring(55,58));
					reg.setN1_NroInstrumento(registro.substring(58,73));
					
				}
				if(reg.getConstante().equals("QN")){//CABECERA
					
					reg.setQn_NroPagoSistema(registro.substring(2,10));
					reg.setQn_SubNroPago(registro.substring(10,13));
					reg.setQn_EstadoPago(registro.substring(16,76));
					reg.setQn_Eventos(registro.substring(79,139));
				}
				if(reg.getConstante().equals("FT")){//CABECERA
					
					reg.setFt_TotalArchivo(registro.substring(27,37));
				}								
				if(reg.save()){
					insertados++;
				}
				else{
					noInsertados++;
				}
			}
		}catch(Exception ex){
			throw ex;
		} finally {
			try{
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				throw e2;
			}
		}	
		

		addLog (0, null, new BigDecimal (insertados), "Insertados");
		addLog (0, null, new BigDecimal (noInsertados), "No Insertados");
		return "";
	}	//	doIt

}	//	ImportGLJournal
