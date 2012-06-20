/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c)  2003-2005 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
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
 *	Accounts Control
 *	
 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *A. Gonzalez, Conserti.
 * 
 *  @version $Id: ControlAcctProcess.java,v 0.9 $
 * 
 *  @Colaborador $Id: Consultoria y Soporte en Redes y Tecnologias de la Informacion S.L.
 * 
 */
public class ControlAcctProcess extends SvrProcess
{
	/**	Shipper				*/
	private int		gl_JournalBatch_ID = 0;

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
			else if (name.equals("GL_JournalBatch_ID"))
				gl_JournalBatch_ID = para[i].getParameterAsInt();
			else
				log.severe("prepare - Unknown Parameter: " + name);
		}
		
	}	//	prepare

	/**
	 * 	Process
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		log.info("doIt - M_Shipper_ID=" + gl_JournalBatch_ID);
		if (gl_JournalBatch_ID == 0)
			throw new IllegalArgumentException("No Shipper");
		//Modificado
		MJournalBatch journalbatch = new MJournalBatch (getCtx(), gl_JournalBatch_ID,null);
		if (journalbatch.getID() != gl_JournalBatch_ID)
			throw new IllegalArgumentException("Cannot find gl_JournalBatch_ID=" + gl_JournalBatch_ID);

		String sql = null;
		sql="SELECT gl_journalline_id FROM gl_journalline " +
				"WHERE gl_journal_id IN(SELECT gl_journal_id FROM gl_journal " +
				"WHERE gl_journalbatch_id=?)";
		
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt( 1,gl_JournalBatch_ID );
			ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MJournalLine linea = new MJournalLine(getCtx(),rs.getInt(1),null);
                X_C_ValidCombination validc = new X_C_ValidCombination(getCtx(),linea.getC_ValidCombination_ID(),null);
                MElementValue element = new MElementValue(getCtx(),validc.getAccount_ID(),null);  
                char valor;
                if(element.isDocControlled()){
                	log.fine("es doccontrolled");
                	valor='Y';
                }else{
                	log.fine(" no es doccontrolled");
                	valor='N';
                }
                String sql2 = "INSERT INTO acct_temp values("+element.getC_ElementValue_ID()+",'"+valor+"')";
                int no = DB.executeUpdate(sql2);
                if(element.isDocControlled()){
                	element.setIsDocControlled(false);
                	element.save();
                }
                
                //Campo a llegar IsDocControlled
            }

            rs.close();
            pstmt.close();
            pstmt = null;
		}
		catch (Exception e)
		{
			//log.error ("doIt - " + sql, e);
			log.saveError("doIt - " + sql, e);
		}
		return "";
		
	}	//	doIt
	
	
}	//	ControlAcctProcess
