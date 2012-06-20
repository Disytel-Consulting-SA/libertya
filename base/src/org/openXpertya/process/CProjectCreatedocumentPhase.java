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
import org.openXpertya.util.Env;


/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 08.03.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CProjectCreatedocumentPhase extends SvrProcess{
		private int m_C_ProjectTask_ID = 0;
		private int m_C_Project_ID = 0;
		private int m_C_ProjectPhase_ID =0;
		private int m_C_DocType_ID =0;
		
		 protected void prepare() {
			 ProcessInfoParameter[] para = getParameter();
		        for( int i = 0;i < para.length;i++ ) {
		            String name = para[ i ].getParameterName();
		            log.fine("En CprojecCreatedocument con el parametro .."+ para[i] +" que es + name= "+ para[ i ].getParameterName());
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
			 	ProcessInfo pi = getProcessInfo();
			 	int ventana =pi.getWindowNo();
			 	m_C_ProjectPhase_ID = getRecord_ID();
			 	m_C_Project_ID =Env.getContextAsInt(getCtx(),ventana,"C_Project_ID");
			 	String mens;
		        //Comprobacion para saber si se ha creado ya ese tipo de documento.
		        //Se mira en la tabla de m_project_document, y se cojen las tareas que para esa fase aun no esten generadas.
		        
		        int TotalPhase=0;
		        MProjectPhase fromPhase= new MProjectPhase(getCtx(),m_C_ProjectPhase_ID,get_TrxName());
		        MProjectTask[] TaskfromPhase = fromPhase.getTasks(m_C_DocType_ID);		     
		        if (TaskfromPhase.length==0)
		        {
		        	//Ya estaria generadas las ordenes para cada una de las fases de esas tarea y ese tipo de documento
		        	JOptionPane.showMessageDialog( null,"Todas las tareas de la fase, ya tienen una orden asociada ","Creacion De Documentos", JOptionPane.INFORMATION_MESSAGE );
		        	return "Todas las tareas de la fase, ya tienen una orden asociada";
		        }
		        //Generamos una orden nueva
		        MProject fromProject = ProjectGenOrder.getProject( getCtx(),m_C_Project_ID,get_TrxName());
		        MOrder order;
		        if (m_C_DocType_ID== 1000325)// Pedido a proveedor
		        {	
		        	mens = "Proveedores";
		        	order = new MOrder( fromProject,false,MOrder.DocSubTypeSO_Standard); //Proveedores
		        }
		        else
		        {	
		        	mens = "Clientes";
		        	order = new MOrder( fromProject,true,MOrder.DocSubTypeSO_Standard); //Clientes
		        }	
		        int            count = 0;
		        if( !order.save()) {
		            throw new Exception( "Could not create Order" );
		        }
		    
		        if( !order.save()) {
		            throw new Exception( "Could not create Order" );
		        }
		        
		        //Cogemos las productos de todas las tareas de esa fase
		        for (int j=0;j<TaskfromPhase.length; j++)
		        {
		        	//Creamos las lineas de la orden
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
			            ol.setC_Tax_ID(ProducTasks[ i ].getC_Tax_ID());

			            if( ol.save()) {
			                count++;
			            }	
			            

		        	}//for
		        	//Guardamos la tarea generada con el tipo de orden en la tabla c_project_document,
		        	X_C_Project_Document newreg = new X_C_Project_Document(getCtx(),0,get_TrxName());
			        newreg.getC_Project_Document_ID();
			        newreg.setC_DocType_ID(m_C_DocType_ID);
			        newreg.setC_ProjectTask_ID(TaskfromPhase[ j ].getC_ProjectTask_ID());
			        newreg.setC_Order_ID(Integer.valueOf(order.getC_Order_ID()).intValue());
			        newreg.setC_ProjectPhase_ID(m_C_ProjectPhase_ID);
			        newreg.save();
			        //String  sql ="Insert into c_project_document (
			        //C_Order_ID,
			        //C_Projecttask_ID,
			        //C_Doctype_ID,
			        //C_Projectphase_ID ) VALUES ("+order.getDocumentNo()+","+TaskfromPhase[ j ].getC_ProjectTask_ID()+","+ m_C_DocType_ID+","+m_C_ProjectPhase_ID+")";
			        //int no = DB.executeUpdate(sql);
			        //if( no != 1 ) {
		              //  log.log( Level.SEVERE,"Insert records=" + no + "; SQL=" + sql.toString());
		            //}  
		        	
		            if( TotalPhase != count ) {
		            log.log( Level.SEVERE,"doIt - Lines differenceee - ProjectTasks=" + ProducTasks.length + " <> Saved=" + count );
		            }
		        }//for      
		        JOptionPane.showMessageDialog( null,"Debera completar la orden en Pedido de "+ mens+" para que se haga efectiva."+"\n","Completar orden", JOptionPane.INFORMATION_MESSAGE );
		        //AEnv.zoom( 259,order.getC_Order_ID());
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
