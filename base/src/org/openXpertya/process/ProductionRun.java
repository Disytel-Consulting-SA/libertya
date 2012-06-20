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


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.model.MAttributeSetInstance;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MProductionOrder;
import org.openXpertya.model.MProductionOrderline;
import org.openXpertya.model.X_M_ProductionLineSource;
import org.openXpertya.model.attribute.RecommendedAtributeInstance;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProductionRun extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public ProductionRun() {
    	 super();	   	 
    }    // ProductRun

    /** Descripción de Campos */

   // private int AD_Client_ID = 103;

    /** Descripción de Campos */

    private int M_Product_ID;

    /** Descripción de Campos */

 /*   private int AD_Org_ID; */

    /** Descripción de Campos */

/*    private int User; */

    /** Descripción de Campos */

    private StringBuffer infoReturn;
  /*  private MTab m_curTab; */
    
    /** Descripción de Campos */
  //  Timestamp vencimiento = null; 
    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
    	log.info( " currupio Estoy ProductionRun.prepare" );
        ProcessInfoParameter[] para = getParameter();
        
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "M_Product_ID" )) {
                M_Product_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"ProductionRun - prepare - Unknown Parameter: " + name );
            }
        }
         
        infoReturn = new StringBuffer( "" );
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String doIt() {
        CheckProduction();
        return infoReturn.toString();
    }    // doIt

    /**
     * Descripción de Método
     *
     */

    public void CheckProduction() {
    	log.info( "currupio Estoy en CheckProduction de ProductionRun" );
    	
    	Timestamp fecha = null;
    	MAttributeSetInstance atrib = null;
    	try {
        	int cont=0;
        	MAttributeSetInstance instance = null;
        	StringBuffer sql;
        	if(M_Product_ID==0){//Si no hay producto seleccionado..
        		 sql = new StringBuffer( "SELECT m_product_id, bomQtyAvailable(m_product_id,"+Env.getContext(Env.getCtx(), "#M_Warehouse_ID")+",0) from m_product " +
                 		"WHERE bomQtyOnHand(m_product_id,"+Env.getContext(Env.getCtx(), "#M_Warehouse_ID")+",0)<1 AND bomQtyReserved(m_product_id,"+Env.getContext(Env.getCtx(), "#M_Warehouse_ID")+",0)>0 AND ispurchased='N'" );

        	}else{//Solo para el producto seleccionado
        		 sql = new StringBuffer( "SELECT m_product_id, bomQtyAvailable("+M_Product_ID+","+Env.getContext(Env.getCtx(), "#M_Warehouse_ID")+",0) from m_product " +
            		"WHERE m_product_id="+M_Product_ID+" AND ispurchased='N'");
        	}
            PreparedStatement pstmt = null;
            ResultSet rs;
            pstmt = DB.prepareStatement( sql.toString());
            rs    = pstmt.executeQuery();
            MProductionOrder proc=null;
            int Line=10;
            while(rs.next()){
            	cont++;
            	if(cont==1){
            		proc = new MProductionOrder(Env.getCtx(),0,null);
                	proc.save();
            	}
            	BigDecimal cantidad=rs.getBigDecimal(2);
        		cantidad=cantidad.abs();
            	
            	MProductionOrderline lineproc = new MProductionOrderline(proc);
            	lineproc.setM_Product_ID(rs.getInt(1));
            	lineproc.setLine(Line);
            	lineproc.setQty(cantidad);
            	

            	
            	lineproc.save();
            	this.crearFuenteArticulo(lineproc.getC_Production_Orderline_ID(), fecha, atrib);
                 //  asignar lote y fecha
            	   
       	         instance = new MAttributeSetInstance(getCtx(),0, get_TrxName());
       	         if(atrib != null) {
       	        	instance.setM_AttributeSet_ID(atrib.getM_AttributeSet_ID()); 
       	        	// creo el lote
          	        instance.createLot(lineproc.getM_Product_ID());
          	        // numero de serie
          	        instance.setSerNo(instance.getSerNo(true)); 
          	        instance.setDueDate(fecha);
           	        instance.setDescription();       	         
           	         
           	               	         
           	        instance.save();       	         
           	        lineproc.setM_AttributeSetInstance_ID(instance.getM_AttributeSetInstance_ID());
           	        lineproc.save(); 
       	         }        	           	         

            	Line+=10;
            	//Prepare_production(lineproc,cantidad);
            }
            if(proc != null) {
            	BreakLines(proc.getC_Production_Order_ID());
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"ProductionRun - CheckProduction; " + e );
        }
        //JOptionPane.showMessageDialog( null,"deleteMProductPrice, para la version = "+ M_PriceList_Version_ID,null, JOptionPane.INFORMATION_MESSAGE );
    }    // CheckProduction
    
    /**
     * Descripción de Método
     *
     */
    
    public void BreakLines(int ProductionOrder_ID){
 	   log.fine("BreakUpProductionLines - BreakLines");
 	   try {
        	StringBuffer sql,sql_line;
        	if(ProductionOrder_ID==0)//Si no hay orden de produccion seleccionada..
        		return;
        	int max_line=10;
        	sql_line = new StringBuffer("select max(line) from c_production_orderline where c_production_order_id="+ProductionOrder_ID);
        	PreparedStatement pstmtline=null;
        	ResultSet rsline;
        	pstmtline = DB.prepareStatement( sql_line.toString());
         rsline    = pstmtline.executeQuery();
         if(rsline.next()){
         	max_line=rsline.getInt(1);
         }
         rsline.close();
 		pstmtline.close();
 		pstmtline = null;
 		
 		// busca las líneas de la orden de producción
        sql = new StringBuffer( "	SELECT c_production_orderline_id,qtyordered,DateDelivered,DateOrdered, " +
        						" 		DatePromised,Description,m_product_id,m_warehouse_id,c_uom_id,IsDescription" +
        						"	FROM c_production_orderline " +
        						"   WHERE c_production_order_id= ? ");	
        //this.configurarVencimiento(ProductionOrder_ID);
         PreparedStatement pstmt = null;
         
         ResultSet rs;
         pstmt = DB.prepareStatement( sql.toString());
         pstmt.setInt(1,ProductionOrder_ID);
         rs    = pstmt.executeQuery();
         BigDecimal quantity=null;
        
         while(rs.next()){
            	quantity=rs.getBigDecimal(2);
            	int Line =max_line+10;
            	if(quantity.compareTo(BigDecimal.ONE)==1){
            		log.fine("La línea de producción tiene mas de un articulo, quantity = "+quantity);
            		for(int i=1;i<quantity.intValue();i++){
            			MProductionOrderline aux = new MProductionOrderline(Env.getCtx(),0,null);
            			aux.setC_Production_Order_ID(ProductionOrder_ID);
            			aux.setDateDelivered(rs.getTimestamp(3));
            			aux.setDateOrdered(rs.getTimestamp(4));
            			aux.setDatePromised(rs.getTimestamp(5));
            			aux.setDescription(rs.getString(6));
            			aux.setM_Product_ID(rs.getInt(7));
            			aux.setM_Warehouse_ID(rs.getInt(8));
            			aux.setC_UOM_ID(rs.getInt(9));
            			aux.setQtyOrdered(BigDecimal.ONE);
            			aux.setIsDescription(rs.getBoolean(10));
            			aux.setQtyEntered(BigDecimal.ONE);
            			aux.setLine(Line);
            			Line+=10;
            			aux.save();            			
            		}   		
            	}
         }
         int noLine = DB.executeUpdate( "UPDATE C_Production_OrderLine set qtyordered="+BigDecimal.ONE+",qtyentered="+BigDecimal.ONE+" where c_production_order_id="+ProductionOrder_ID ,get_TrxName());
        log.fine("Numero de lineas actualizadas:"+noLine);
         rs.close();
 		pstmt.close();
 		pstmt = null;
        }catch( Exception e ) {
            log.log( Level.SEVERE,"BreakUpProductionLines - BreakLines " + e );
        }
        
 	   return;
 	   }


	/**
     * Descripción de Método: Para una línea genera efectivamente la reserva de artículos y/o sustitutos
     * si fuera necesario.
	 * @param atrib 
     *
     * @return
     */
	
	private void crearFuenteArticulo(int productLine_ID, Timestamp fecha, MAttributeSetInstance atrib) {
	// hace las reservas para cada artículos

		MProductionOrderline linea = new MProductionOrderline(getCtx(), productLine_ID, get_TrxName());
	    BigDecimal asignado = Env.ZERO;		
	    BigDecimal stock = checkProductStock(linea.getM_Product_ID());
	    Vector<MAttributeSetInstance> Instances = new Vector<MAttributeSetInstance>();
	    BigDecimal cantidad = Env.ZERO;
	    
		if(stock.compareTo(Env.ZERO) > 0){
		
			    X_M_ProductionLineSource fuente = new X_M_ProductionLineSource(getCtx(), 0, get_TrxName());
				fuente.setM_Product_ID(linea.getM_Product_ID());
				if(linea.getQtyEntered().compareTo(stock)<0){
					cantidad = linea.getQtyEntered();
				} else {
					cantidad = stock;
				}
				fuente.setDateOrdered(Env.getContextAsDate(getCtx(), "#Date"));
				fuente.setC_UOM_ID(linea.getC_UOM_ID());
				fuente.setM_Warehouse_ID(linea.getM_Warehouse_ID());
				fuente.setC_Production_Orderline_ID(linea.getC_Production_Orderline_ID());
				fuente.setC_Production_Order_ID(linea.getC_Production_Order_ID());
				fuente.setDateDelivered(linea.getDateDelivered());
				fuente.setDateOrdered(linea.getDateOrdered());
				fuente.setDatePromised(linea.getDatePromised());
				fuente.setQtyOrdered(linea.getQtyOrdered());
				fuente.setQtyReserved(linea.getQtyEntered());
				fuente.setQtyDelivered(Env.ZERO);
				fuente.setName(linea.getName());	
				fuente.setQtyOrdered(cantidad);
				try {
					RecommendedAtributeInstance[] recomend = MProduct.getRecommendedAtributeInstance(linea.getM_Product_ID(), cantidad, true, Env.getContextAsInt(getCtx(), "#M_Warehouse_ID"));
					if (recomend.length > 0) {
					    fuente.setM_AttributeSetInstance_ID(recomend[0].getM_AtributeInstance_ID());
					
					    // Para obtener despues en la linea el conjunto de atributos
					    if(atrib == null) {
							atrib = new MAttributeSetInstance(getCtx(), fuente.getM_AttributeSetInstance_ID(), get_TrxName());
						}
					}
				} catch (SQLException e) {
					log.saveError("ProductionRun - CrearFuenteArticulo","No se pudo encontrar un conjuntos de atributo adecuado para el artículo");
				}
				
				if (!fuente.save()) {
					log.saveError("ProductionRun - CrearFuenteArticulo","No se pudo guardar archivo fuente");
				}
				asignado = linea.getQtyEntered();
				}			
		
		
		if (linea.getQtyEntered().compareTo(asignado)>0){
			// Si no se asigno todo el material necesario busco sustituto para el articulo
			
			StringBuffer sql = new StringBuffer(" SELECT m_product_id, substitute_id, name, description " +
												" FROM  M_Substitute " +
												" WHERE isActive = 'Y' AND AD_Client_ID = ? AND AD_Org_ID in (0, ?) AND M_Product_ID = ? ");
				
		    
		    // asignar el stock que hay
			PreparedStatement pstmt = null;
			pstmt = DB.prepareStatement( sql.toString());
			try {
				pstmt.setInt( 1,Env.getAD_Client_ID(getCtx()));
				pstmt.setInt( 2,Env.getAD_Org_ID(getCtx()));
				pstmt.setInt( 3,linea.getM_Product_ID());
				ResultSet rs = pstmt.executeQuery();
            
				while(rs.next() & asignado.compareTo(linea.getQtyEntered()) < 0){
            	// busco cantidad de stock
					stock = checkProductStock(rs.getInt("substitute_id"));
            	// si hay stock asigno lo que puedo
					if (stock.compareTo(Env.ZERO) > 0) {
						X_M_ProductionLineSource fuente = new X_M_ProductionLineSource(getCtx(), 0, get_TrxName());
						fuente.setM_Product_ID(rs.getInt("substitute_id"));
						if(linea.getQtyEntered().compareTo(asignado.add(stock))<0){
							fuente.setQtyOrdered(linea.getQtyEntered().subtract(asignado));
							asignado = asignado.add(linea.getQtyEntered().subtract(asignado));
							cantidad = linea.getQtyOrdered();
					} else {
							fuente.setQtyOrdered(stock);
							asignado = asignado.add(stock);
							cantidad = stock;
					}
					fuente.setDateOrdered(Env.getContextAsDate(getCtx(), "#Date"));
					fuente.setC_UOM_ID(linea.getC_UOM_ID());
					fuente.setC_Production_Orderline_ID(linea.getC_Production_Orderline_ID());
					fuente.setC_Production_Order_ID(linea.getC_Production_Order_ID());
					fuente.setM_Warehouse_ID(linea.getM_Warehouse_ID());
					fuente.setName(linea.getName());
					
					try {
						RecommendedAtributeInstance[] recomend = MProduct.getRecommendedAtributeInstance(fuente.getM_Product_ID(), cantidad, true, Env.getContextAsInt(getCtx(), "#M_Warehouse_ID"));
						fuente.setM_AttributeSetInstance_ID(recomend[0].getM_AtributeInstance_ID());
						
						// Para obtener despues en la linea el conjunto de atributos
						if(atrib == null) {
							atrib = new MAttributeSetInstance(getCtx(), fuente.getM_AttributeSetInstance_ID(), get_TrxName());
						}
						
					} catch (SQLException e) {
						log.saveError("ProductionRun - CrearFuenteArticulo","No se pudo encontrar un conjuntos de atributo adecuado para el artículo");
					}
    				if (!fuente.save()) {
    					log.saveError("ProductionRun - crearFuenteArticulo "," No se pudo guardar archivo fuente");
    				}    				
    			}	            		
            }
				
		    fecha = this.BuscarVencimiento(Instances);
		    
		    Instances.clear();
            rs.close();
            pstmt.close();
            pstmt = null;
            } catch (Exception e){
            	log.saveError("ProductionRun - crearFuenteArticulo ", e.getMessage());
            }
		}
	} // CrearFuenteArticulo  
	
	
	/**
     * Descripción de Método: Para las instancias de conjuntos de atriburos creada, la fecha de vencimiento
     * mas próxima a la actual.
     *      
     * @param instances
     * @return 
     */	
	private Timestamp BuscarVencimiento(Vector<MAttributeSetInstance> instances) {
		Timestamp actual = null;
			
		for(int i = 0; i < instances.size(); i++ ){
				if((actual == null) | (actual.compareTo(instances.get(i).getDueDate()))< 0 ) {
					actual = instances.get(i).getDueDate();
				}
		}
		return actual;
	}

	/**
     * Descripción de Método: Para una cantidad de producto, crea una instacia del conjunto de atributo, para asignarlo a la clase
     *      
     * @param instance
     * @param linea
     * @param cantidad
     * 
     * @return retorna el ID de la instancia creada
     */	
	/*  private int createAttributeInstance(Vector<MAttributeSetInstance> instances, MProductionOrderline linea, BigDecimal cantidad) {
		  MAttributeSetInstance instance = null;
		  if(linea.getM_AttributeSetInstance_ID() == 0){
			  return 0;
		  }
		  MAttributeSetInstance ins_linea = new MAttributeSetInstance(getCtx(),linea.getM_AttributeSetInstance_ID(), get_TrxName());
			try {
				
				instance = MAttributeSetInstance.get(getCtx(), 0, linea.getM_Product_ID());
				instance.setM_AttributeSet_ID(ins_linea.getM_AttributeSet_ID());
				instance.setSerNo(instance.getSerNo(true));
			   	instance.createAttachment();
				instance.createLot(linea.getM_Product_ID());
				instance.setDescription();	
							
			} catch (Exception e) {
				log.saveError("ProductionRun - CrearFuenteArticulo "," No se pudo encontrar un conjunto de atributo recomendado");
			}
			
			if(!instance.save()){} {
				log.saveError("ProductionRun - CrearFuenteArticulo","No se pudo guardar la instancia del atributo archivo fuente");
				
			} 
			
			instances.add(instance);
			
			return instance.getM_AttributeSetInstance_ID();
	} */

	/**
     * Descripción de Método: Para un artículo que viene como parámetro, busca si hay stock,
     *      
     * @param M_Product_ID
     * 
     * @return retorna la cantidad de stock de ese artículo
     */
	
    private BigDecimal checkProductStock(int M_Product_ID) {
    
    BigDecimal retValue = Env.ZERO;
	StringBuffer sql = new StringBuffer("SELECT name, bomQtyAvailable("+M_Product_ID+","+Env.getContext(Env.getCtx(), "#M_Warehouse_ID")+",0) as Cantidad" +
		 		" from m_product where m_product_id="+ M_Product_ID);
    try {
    	
    	PreparedStatement pstmt = null;
        ResultSet rs;
        pstmt = DB.prepareStatement( sql.toString());
        rs    = pstmt.executeQuery();
        if(rs.next()) {       	
        	retValue = rs.getBigDecimal("Cantidad");
        }
        rs.close();
        pstmt.close();
        pstmt = null;
    } catch( Exception e ) {
        log.log( Level.SEVERE,"ProductionRun - CheckProductStock; " + e );
    }
    return retValue;
    } // checkProductStock
	

}    // ProductionRun



/*
 *  @(#)ProductionRun.java   02.07.07
 * 
 *  Fin del fichero ProductionRun.java
 *  
 *  Versión 2.2
 *
 */
