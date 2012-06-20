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


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import java.lang.Integer;
import java.math.BigDecimal;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutMovement extends CalloutEngine {

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

        // Set Attribute

        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
            mTab.setValue( "M_AttributeSetInstance_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
        } else {
            mTab.setValue( "M_AttributeSetInstance_ID",null );
        }
        return "";
        
    }    // product
    public String locator(Properties ctx,int WindowNo,MTab mTab,MField mField,Object value)
    {
    	//Añadido por ConSerTi. Comprueba que no se pueda mover matarial en la misma ubicacion.
    	log.log(Level.FINE,"estamos en callout.locator");
    	if( isCalloutActive() || (value == null) )
    	{  
    		return "";
        }
    	Integer locator1 = (Integer)mTab.getValue( "M_locator_ID");
    	Integer locator2 = (Integer)mTab.getValue( "M_locatorTo_ID");
    	if (locator1.equals(locator2))
    	{
    		JOptionPane.showMessageDialog( null,"No se puede Mover material a la misma ubicacion","Movimiento erroneo", JOptionPane.INFORMATION_MESSAGE );
    		//Ponemos las segunda ubicaciones a null.
    		mTab.setValue("M_locatorTo_ID",null);
    	}//if
    	return "";
    		
    }//locator
    public String stock( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value)
    {
    	log.log(Level.FINE,"En Callout.stock");
    	 if( isCalloutActive() || (value == null) )
    	 {  
             return "";
         }
    	
    	
    	//Añadida por ConSerti para realizar los movimientos de Almacen.
    	//Se activa a la perdida del foco de la casilla  "Cantidad de Movimiento".
    	BigDecimal MovementQty = ( BigDecimal )value;
    	if ((MovementQty==null)||(MovementQty.intValue()==0))
    	{
    		setCalloutActive( false );
    		return "";
    	}	
    	
    	Integer M_Product_ID = (Integer)mTab.getValue( "M_Product_ID");
    	//BigDecimal MovementQty = ( BigDecimal )value;
    	Integer locator1 = (Integer)mTab.getValue( "M_locator_ID");


    	//Calculamos si hay material suficiente para mover.
    	 String sql = "SELECT qtyonhand,qtyreserved,* FROM m_storage where m_product_id=? and m_locator_id=?";
    	 try {
    		 log.log(Level.FINE,"entramos en el try");
             PreparedStatement pstmt = DB.prepareStatement( sql );
             pstmt.setInt( 1,M_Product_ID.intValue());
             pstmt.setInt( 2,locator1.intValue());
             
             ResultSet rs = pstmt.executeQuery();
             if( rs.next()) {
            	 //Calculamos la cantidad disponible. Existencias - Reservadas
                  BigDecimal Qty =rs.getBigDecimal(1).subtract(rs.getBigDecimal(2));
                  if (Qty.compareTo(MovementQty)==1 || Qty.compareTo(MovementQty)==0)
                  {
                	  //Hay suficiente Stock para mover
                	  mTab.setValue("TargetQty",MovementQty);
                	  mTab.setValue("ConfirmedQty",MovementQty);
                  }//if
                  else
                  {
                	  JOptionPane.showMessageDialog( null,"No hay stock disponible.\n La maxima cantidad disponible es "+ Qty,"No hay Stock para mover", JOptionPane.INFORMATION_MESSAGE );
                	  //Asignamo el valor maximo disponible
                	  mTab.setValue("MovementQty",Qty);
                	  mTab.setValue("TargetQty",Qty);
                	  mTab.setValue("ConfirmedQty",Qty);
              		return "";
                	  
                  }//else
                   
             }//if
    	 }//Try
    	 catch( SQLException e ) {
             log.log( Level.SEVERE,"Error al ejecutar la consulta",e );
         }//Catch  		
    	 return "";
    }//Stock
}    // CalloutMove



/*
 *  @(#)CalloutMovement.java   02.07.07
 * 
 *  Fin del fichero CalloutMovement.java
 *  
 *  Versión 2.2
 *
 */
