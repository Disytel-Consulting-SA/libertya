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
import java.util.logging.Level;

import javax.swing.JOptionPane;


import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MProjectProduct;
import org.openXpertya.model.MProjectTask;
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

public class CProjectCreatedocument extends SvrProcess{
	private int m_C_ProjectTask_ID = 0;
	private int m_C_Project_ID = 0;
	private int m_C_DocType_ID = 0;
	private int m_C_Project_Document_ID=0;
	
	 protected void prepare() {		 
		 	ProcessInfoParameter[] para = getParameter();
	        for( int i = 0;i < para.length;i++ ) {
	            String name = para[ i ].getParameterName();
	            log.fine("En CprojecCreatedocument con el parametro"+ i +" que es + name= "+ para[ i ].getParameterName());
	            if( para[ i ].getParameter() == null ) {
	                ;
	            } else if( name.equals( "C_DocType_ID" )) {
	            	log.fine("En name.equals( 'C_DocType_ID' )");
	            	m_C_DocType_ID = para[ i ].getParameterAsInt();
	            }
	            else {
	                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
	            }
	        }
	 }
	 
	 protected String doIt() throws Exception {
		 	ProcessInfo pi = getProcessInfo();
		 	int ventana =pi.getWindowNo();
		 	m_C_ProjectTask_ID = getRecord_ID();
		 	m_C_Project_Document_ID=getRecord_ID();
		 	log.fine("fdsfadsfasdfdsfdsafsda"+m_C_Project_Document_ID);
		 	m_C_Project_ID =Env.getContextAsInt(getCtx(),ventana,"C_Project_ID");
		 	String mens;
	        //Comprobacion para saber si se ha creado ya ese tipo de documento.
	        //Se mira en la tabla de m_project_document, si hay una entrada para esa tarea y ese tipo de documento.
	        String  exist ="select c_order_id from c_project_document where c_projecttask_id="+ m_C_ProjectTask_ID+" and "+ "C_DocType_ID= "+ m_C_DocType_ID;
	        PreparedStatement pstmt = null;
	        pstmt = DB.prepareStatement( exist);
	        ResultSet rs = pstmt.executeQuery();
	        if( rs.next()) 
	        {  
	        	JOptionPane.showMessageDialog( null,"Documento ya creado en la orden Numero: "+rs.getInt("C_Order_ID"),"Creacion De Documentos", JOptionPane.INFORMATION_MESSAGE );
	        	return "Orden Numero "+rs.getInt("C_Order_ID")+" ya creada para esa tarea";
	        }
	        
	        MProjectTask fromTask = new MProjectTask( getCtx(), m_C_ProjectTask_ID,get_TrxName());
	        MProjectProduct[] ProducTasks = fromTask.getProduct();
	        //Tendriamos que crear un orden nueva.
	        
	        MProject fromProject = ProjectGenOrder.getProject( getCtx(),m_C_Project_ID,get_TrxName());
	        MOrder order;
	        if (m_C_DocType_ID== 1000325)// Pedido a proveedor
	        {	
	        	log.info("En m_C_DocType_ID== 1000325");
	        	mens = "Proveedores";
	        	order = new MOrder(fromProject,false,MOrder.DocSubTypeSO_Standard); //Proveedores
	        }
	        else
	        {	
	        	log.info("En m_C_DocType_ID== 1000338");
	        	mens = "Clientes";
	        	order = new MOrder( fromProject,true,MOrder.DocSubTypeSO_Standard); //Clientes
	        }	
	        int            count = 0;
	        if( !order.save()) {
	            throw new Exception( "Could not create Order" );
	        }
	        order.setDescription( order.getDescription() + " - " + fromTask.getName());	  	        
	        if( !order.save()) {
	            throw new Exception( "Could not create Order" );
	        }
	        //Tendriamos que crera las lineas de la orden con cada uno de los productos.
	        for( int i = 0;i < ProducTasks.length;i++ ) {
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
	            
	        }    // for all lines
	        if( ProducTasks.length != count ) {
	            log.log( Level.SEVERE,"doIt - Lines differenceee - ProjectTasks=" + ProducTasks.length + " <> Saved=" + count );
	        
	        }
	        //Guardamos el documento que se ha  generado, para no volver a generarlo.
	        X_C_Project_Document newreg = new X_C_Project_Document(getCtx(),0,get_TrxName());
	        newreg.getC_Project_Document_ID();
	        newreg.setC_DocType_ID(m_C_DocType_ID);
	        newreg.setC_ProjectTask_ID(m_C_ProjectTask_ID);
	        //Tenia Documento No - JorgeV Disytel
	        newreg.setC_Order_ID(Integer.valueOf(order.getC_Order_ID()).intValue());
	        newreg.save();
	        //String  sql ="Insert into c_project_document (C_Project_Document_ID,C_Order_ID,C_Projecttask_ID,C_Doctype_ID) VALUES ("+aux.getC_Project_Document_ID()+","+order.getDocumentNo()+","+m_C_ProjectTask_ID+","+ m_C_DocType_ID+")";
	        //int no = DB.executeUpdate(sql);
	        //if( no != 1 ) {
              //  log.log( Level.SEVERE,"Insert records=" + no + "; SQL=" + sql.toString());
           // }
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
