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

import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MProjectProduct;
import org.openXpertya.model.MProjectTask;
import org.openXpertya.model.MProjectPhase;
import org.openXpertya.model.X_C_Project_Document;
import org.openXpertya.util.DB;


/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 08.03.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CProjectCreatedocumentProject extends SvrProcess{
	private int m_C_Project_ID = 0;
	private int m_C_DocType_ID = 0;
	
	 protected void prepare() {
		 ProcessInfoParameter[] para = getParameter();
	        for( int i = 0;i < para.length;i++ ) {
	            String name = para[ i ].getParameterName();
	            if( para[ i ].getParameter() == null ) {
	                ;
	            } else if( name.equals( "C_DocType_ID" )) {
	            	m_C_DocType_ID = para[ i ].getParameterAsInt();
	            }else {
	                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
	            }
	        }
	 }
	 protected String doIt() throws Exception {
		 	m_C_Project_ID = getRecord_ID();
		
	        //Cogemos las tareas para esa fase.
	        int TotalPhase=0;
	        String mens;
	        MProject fromProject= new MProject(getCtx(),m_C_Project_ID,get_TrxName());
	        
	        //Seleccionamos todas las fases para ese proyecto.
	        MProjectPhase[] PhaseFromProject = fromProject.getPhases();
	        
	        //Antes de generar la nueva orden, tendremo que verificar que esa orden tenga alguna linea.
	        int totalTask=0;
	        for(int k=0; k<PhaseFromProject.length;k++)
	        {
	        	MProjectTask[] TaskfromPhase = PhaseFromProject[k].getTasks(m_C_DocType_ID);
	        	totalTask=totalTask + TaskfromPhase.length;
	        }
	        if (totalTask==0)
	        {
	        	//Ya estaria generadas las ordenes para cada una de las fases de esas tarea y ese tipo de documento
	        	JOptionPane.showMessageDialog( null,"Ya se han generado todas las ordenes para este proyecto","Generacion De Documentos", JOptionPane.INFORMATION_MESSAGE );
	        	return "Ya se han generado todas las ordnes para este proyecto";
	        }
	        
	        //Generamos una orden nueva
	        MProject fromAll = ProjectGenOrder.getProject( getCtx(),m_C_Project_ID,get_TrxName());
	        MOrder order;
	        if (m_C_DocType_ID== 1000325)// Pedido a proveedor
	        {	
	        	mens = "Proveedores";
	        	order = new MOrder(fromAll,false,MOrder.DocSubTypeSO_Standard); //Proveedores
	        }
	        else
	        {	
	        	mens = "Clientes";
	        	order = new MOrder( fromAll,true,MOrder.DocSubTypeSO_Standard); //Clientes
	        }	
	        int            count = 0;
	        if( !order.save()) {
	            throw new Exception( "Could not create Order" );
	        }
	        
	        //Para cada Fase...
	        for(int k=0; k<PhaseFromProject.length;k++)
	        {	
	        	//Seleccionar todas las tareas para esa fase
	        	MProjectTask[] TaskfromPhase = PhaseFromProject[k].getTasks(m_C_DocType_ID);
	        	
	        	//Parcial de tareas para cada fase.
	        	totalTask=totalTask + TaskfromPhase.length;
	        	for (int j=0;j<TaskfromPhase.length; j++) //Para cada tarea.
	        	{
	        		//Creamos las lineas de la orden
	        		//JOptionPane.showMessageDialog( null,"En En la fase  getC_ProjectPhase()= "+ k,"..Fin", JOptionPane.INFORMATION_MESSAGE );
	        		//JOptionPane.showMessageDialog( null,"Con la tarea "+TaskfromPhase[j].getName(),"..Fin", JOptionPane.INFORMATION_MESSAGE );
	        		MProjectProduct[] ProducTasks =TaskfromPhase[j].getProduct();
	        		TotalPhase = TotalPhase + ProducTasks.length;
	        		for( int i = 0;i < ProducTasks.length;i++ )
	        		{
	        			MOrderLine ol = new MOrderLine( order );
	        			ol.setLine( ProducTasks[ i ].getSeqNo());
	        			StringBuffer sb = new StringBuffer( ProducTasks[ i ].getName());
	        			if( (ProducTasks[ i ].getDescription() != null) && (ProducTasks[ i ].getDescription().length() > 0) ) {
	        				sb.append( " - " ).append( ProducTasks[ i ].getDescription());
	        			}
	        			ol.setDescription( sb.toString());
	        			ol.setM_Product_ID( ProducTasks[ i ].getM_Product_ID(),true );
	        			ol.setQty( ProducTasks[ i ].getQty());
	        			ol.setPrice();
	        			log.fine("El ProducTasks[ i ].getC_Tax_ID()= "+ProducTasks[ i ].getC_Tax_ID());
	        			ol.setC_Tax_ID(ProducTasks[ i ].getC_Tax_ID());

	        			if( ol.save()) {
	        				count++;
	        			}			            

	        		}//for
	      	
	        		if( TotalPhase != count ) {
	        			log.log( Level.SEVERE,"doIt - Lines differenceee - ProjectTasks=" + ProducTasks.length + " <> Saved=" + count );
	        		}
	        		X_C_Project_Document newreg = new X_C_Project_Document(getCtx(),0,get_TrxName());
	        		newreg.getC_Project_Document_ID();
			        newreg.setC_DocType_ID(m_C_DocType_ID);
			        newreg.setC_ProjectTask_ID(TaskfromPhase[j].getC_ProjectTask_ID());
			        newreg.setC_Order_ID(Integer.valueOf(order.getDocumentNo()).intValue());
			        newreg.setC_ProjectPhase_ID(TaskfromPhase[j].getC_ProjectPhase_ID());
			        newreg.setC_Project_ID(m_C_Project_ID);
			        newreg.save();
	        		//String  sql ="Insert into c_project_document (
			        //C_Order_ID,
			        //C_Projecttask_ID,
			        //C_ProjectPhase_ID,
			        //C_Project_ID,
			        //C_Doctype_ID) VALUES ("+order.getDocumentNo()+","+TaskfromPhase[j].getC_ProjectTask_ID()+","+TaskfromPhase[j].getC_ProjectPhase_ID()+","+ m_C_Project_ID+","+ m_C_DocType_ID+")";
	    	        //int no = DB.executeUpdate(sql);
	    	        //if( no != 1 ) {
	                  //  log.log( Level.SEVERE,"Insert records=" + no + "; SQL=" + sql.toString());
	                //}
	        	}//for.. Para cada tarea      
	        	
	        }//for.. cada fase
	        JOptionPane.showMessageDialog( null,"Debera completar la orden en Pedido de "+ mens+" para que se haga efectiva."+"\n","Completar orden", JOptionPane.INFORMATION_MESSAGE );
        	return "@C_Order_ID@ " + order.getDocumentNo() + " (" + count + ")";
	     }	
}
/*
*  @(#)CProjectCreatedocument.java   08.03.07
* 
*  Fin del fichero CProjectCreatedocument.java
*  
*  Versión 2.2
*
*/
