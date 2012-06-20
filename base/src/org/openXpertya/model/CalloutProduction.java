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



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;



import org.openXpertya.model.attribute.RecommendedAtributeInstance;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.Quarter_Process;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutProduction extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String product( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer M_Product_ID = ( Integer )value;

        if( (M_Product_ID == null) || (M_Product_ID.intValue() == 0) ) {
            return "";
        }

        setCalloutActive( true );

        // Production Order Warehouse
        int M_Warehouse_ID = Env.getContextAsInt( ctx,WindowNo,"M_Warehouse_ID" );
        
        // Set Attribute
        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
            mTab.setValue( "M_AttributeSetInstance_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
        } else {
            mTab.setValue( "M_AttributeSetInstance_ID",null );
        }
        
        // Set UOM/Locator

        MProduct product = MProduct.get( ctx,M_Product_ID.intValue());

        mTab.setValue( "C_UOM_ID",new Integer( product.getC_UOM_ID()));

        if( product.getM_Locator_ID() != 0 ) {
            MLocator loc = MLocator.get( ctx,product.getM_Locator_ID());

            if( M_Warehouse_ID == loc.getM_Warehouse_ID()) {
                mTab.setValue( "M_Locator_ID",new Integer( product.getM_Locator_ID()));
            } else {
                log.fine( "No Locator for M_Product_ID=" + M_Product_ID + " and M_Warehouse_ID=" + M_Warehouse_ID );
            }
        } else {
            log.fine( "No Locator for M_Product_ID=" + M_Product_ID );
        }

        setCalloutActive( false );

        return "";
    }    // product
    
   /* public String replenish( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer ReplenishType_ID = ( Integer )value;

        if( (ReplenishType_ID == null) || (ReplenishType_ID.intValue() == 0) ) {
            return "";
        }

        // Set Attribute
        log.fine("El replenishhhh que me pasa essss="+ReplenishType_ID.intValue());
        replenish_generate();

        return "";
    }    // product
    
    public void replenish_generate(){
//   	 Prepare Process
    	Quarter_Process a = new Quarter_Process();
   	
       int AD_Process_ID = 1000115;  
       ProcessInfo pi = new ProcessInfo( "",AD_Process_ID );
// HARDCODED    C_RemesaGenerate
       MPInstance instance      = new MPInstance( Env.getCtx(),AD_Process_ID,0,null );
       log.fine("En replenish_generate()"+pi.getAD_PInstance_ID());
       if( !instance.save()) {
           return;
       }
      
       pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());

       // Add Parameters

       MPInstancePara para = new MPInstancePara( instance,10 );

       

       if( !para.save()) {
           String msg = "No Selection Parameter added";    // not translated
           log.log( Level.SEVERE,msg );

           return;
       }

       para = new MPInstancePara( instance,20 );
       

       if( !para.save()) {
           String msg = "No DocAction Parameter added";    // not translated
           log.log( Level.SEVERE,msg );

           return;
       }

       // Execute Process
       a.startProcess(Env.getCtx(), pi, null);
       replenish_generate2();
       ProcessCtl worker = new ProcessCtl( this,pi,null );

       worker.start();  
   }
    
    public void replenish_generate2(){
//  	 Prepare Process
   	Quarter_Process a = new Quarter_Process();
  	
      int AD_Process_ID = 1000116;  
      ProcessInfo pi = new ProcessInfo( "",AD_Process_ID );
//HARDCODED    C_RemesaGenerate
      MPInstance instance      = new MPInstance( Env.getCtx(),AD_Process_ID,0,null );
      log.fine("En replenish_generate()"+pi.getAD_PInstance_ID());
      if( !instance.save()) {
          return;
      }
     
      pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());

      // Add Parameters

      MPInstancePara para = new MPInstancePara( instance,10 );

      

      if( !para.save()) {
          String msg = "No Selection Parameter added";    // not translated
          log.log( Level.SEVERE,msg );

          return;
      }

      para = new MPInstancePara( instance,20 );
      

      if( !para.save()) {
          String msg = "No DocAction Parameter added";    // not translated
          log.log( Level.SEVERE,msg );

          return;
      }

      // Execute Process
      a.startProcess(Env.getCtx(), pi, null);
      
      /*ProcessCtl worker = new ProcessCtl( this,pi,null );

      worker.start();  
  }
    */
    
}    // CalloutProduction



/*
 *  @(#)CalloutProduction.java   02.07.07
 * 
 *  Fin del fichero CalloutProduction.java
 *  
 *  Versión 2.2
 *
 */
