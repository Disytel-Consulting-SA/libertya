/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */


package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openXpertya.model.*;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.*;

/**
 *	Create Package from Shipment for Shipper
 *	
 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Jose A. Gonzalez, Conserti.
 * 
 *  @version $Id: EnvioCreate.java,v 0.9 $
 * 
 *  @Colaborador $Id: Consultoria y Soporte en Redes y Tecnologias de la Informacion S.L.
 * 
 */
public class EnvioCreate extends SvrProcess
{
	/**	Shipper				*/
	private int		p_M_Shipper_ID = 0;
	/** Numero de paquetes    */
	private int 	p_No_Paquetes = 0;
	/**	Manual Selection		*/
	private boolean 	p_Selection = false;
	
	/** Numero de envios (debe ser 1 siempre)*/
	private int			m_created = 0;

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
			else if (name.equals("M_Shipper_ID"))
				p_M_Shipper_ID = para[i].getParameterAsInt();
			else if (name.equals("No_Paquetes"))
				p_No_Paquetes = para[i].getParameterAsInt();
			else if (name.equals("Selection"))
				p_Selection = "Y".equals(para[i].getParameter());
			else
				log.fine("prepare - Unknown Parameter: " + name);
				//log.error("prepare - Unknown Parameter: " + name);
		}
		
	}	//	prepare

	/**
	 * 	Process
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		log.info("doIt - M_Shipper_ID=" + p_M_Shipper_ID);
		if (p_M_Shipper_ID == 0)
			throw new IllegalArgumentException("No Shipper");
		//Modificado
		MShipper shipper = new MShipper (getCtx(), p_M_Shipper_ID,null);
		if (shipper.getID() != p_M_Shipper_ID)
			throw new IllegalArgumentException("Cannot find Shipper ID=" + p_M_Shipper_ID);

		String sql = null;
		if (p_Selection)	
		{
			sql = "SELECT * FROM M_InOutLine l "
				+ "WHERE l.IsSelected='Y' "
				+ "ORDER BY l.M_InOut_ID, l.M_InOutLine_ID";
		}
		
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sql);
			int index = 1;
		}
		catch (Exception e)
		{
			//log.error ("doIt - " + sql, e);
			log.saveError("doIt - " + sql, e);
		}
		return generarPaquetes(pstmt);
		
	}	//	doIt
	
	/**
	 *  genera un envio con tantos paquetes como albaranes pertenecen las 
	 * @param pstmt
	 * @return
	 */
	private String generarPaquetes (PreparedStatement pstmt)
	{
		try
		{
			ResultSet rs = pstmt.executeQuery ();

			MInOut alba = null;
			MEnvio envio = null;
			MPackage paquete = null;
			while(rs.next())
			{
				//Modificado
				MInOutLine lineaAlba = new MInOutLine(Env.getCtx(), rs,null);
				//si es el primero
				if (alba == null)
				{
					//Modificado
					alba = new MInOut(Env.getCtx(), lineaAlba.getM_InOut_ID(),null);
					envio = crearEnvio(alba);
					addLog(envio.getID(), envio.getCreated(), null, envio.getDocumentNo());
					paquete = crearPaquete(envio, alba);
					m_created++;
				}
				//si es una linea de un nuevo albaran
				if (lineaAlba.getM_InOut_ID() != alba.getM_InOut_ID())
				{
					completarAlbaran(alba);
					//Modificado
					alba = new MInOut(Env.getCtx(), lineaAlba.getM_InOut_ID(),null);
					paquete = crearPaquete(envio, alba);
				}
							
				crearLineaPaquete(paquete, lineaAlba);					
			}	
			completarAlbaran(alba);
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			//log.error ("Crear Envio", e);
			log.saveError("Crear Envio", e);
		}
	
		return "@Created@ = " + m_created;
		
	}	//	Generar Paquetes

	/**
	 * Comprueba si un albara est� completo viendo si todas sus lineas est�n en lineas de paquete
	 * 
	 * @param alba (el abar�n a comprobar)
	 * @return true si est� completo
	 */
	private boolean albaranCompleto(MInOut alba)
	{
		StringBuffer sql = new StringBuffer(
				"SELECT count(l.M_InOutLine_ID) "
				+ "FROM M_InOutLine l "
				+ "INNER JOIN M_InOut a ON (l.M_InOut_ID=a.M_InOut_ID AND l.M_InOut_ID=");
		sql.append(alba.getM_InOut_ID()).append(") ");
		sql.append("WHERE l.M_InOutLine_ID NOT IN (SELECT p.M_InOutLine_ID FROM M_PackageLine p)");

		boolean completo = true;
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			//
			if (rs.next())
				completo = 0 == rs.getInt(1);
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			//Log.error("VPackGen.executeQueryLineas", e);
			log.saveError("VPackGen.executeQueryLineas", e);
		}
		return completo;	
	}
	
	/**
	 * Completa el albar�n llamando a las funciones ya creadas para tal fin en 
	 * MInOut
	 *  
	 */
	private void completarAlbaran(MInOut alba)
	{
		if (alba != null)
		{ 
			if (albaranCompleto(alba))
			{		
				//se completa
				alba.setDocAction(DocAction.ACTION_Complete);
				alba.processIt(DocAction.ACTION_Complete);
				//se guarda
				alba.save();			
				alba = null;
			}		
		}
	}
	
	/**
	 * Crea un Envio con los datos de fecha sacados de la l�nea de albaran
	 * 
	 * @param albaran
	 * @return un nuevo envio
	 */
	private MEnvio crearEnvio(MInOut albaran)
	{
		//Modificado
		MShipper trans = new MShipper(Env.getCtx(), p_M_Shipper_ID,null);

		return MEnvio.create(albaran, trans, p_No_Paquetes, null);
	}
	
	/**
	 * Crea un paquete de un envio para un albar�n
	 * 
	 * @param envio (envio en que se incluye el paquete)
	 * @param albaran (albar�n al que referencia el paquete)
	 * @return el nuevo paquete
	 */
	private MPackage crearPaquete(MEnvio envio, MInOut albaran)
	{
		//Modificado
		MPackage paquete = new MPackage(envio, albaran); 
		paquete.setM_Shipper_ID(envio.getM_Shipper_ID());
		paquete.save();
		return paquete;
	}
	
	/**
	 * Crea una linea de paquete
	 * @param paquete
	 * @param lineaAlb
	 * @return la nueva linea de paquete
	 */
	private MPackageLine crearLineaPaquete(MPackage paquete, MInOutLine lineaAlba)
	{
		MPackageLine linea = new MPackageLine(paquete);
		linea.setInOutLine(lineaAlba);
		linea.save();
		return linea;
	}
	
}	//	PackageCreate
