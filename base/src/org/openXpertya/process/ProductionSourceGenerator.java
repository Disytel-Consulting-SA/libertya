package org.openXpertya.process;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.model.MAttributeSetInstance;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MProductionOrder;
import org.openXpertya.model.MProductionOrderline;
import org.openXpertya.model.X_M_ProductionLineSource;
import org.openXpertya.model.attribute.RecommendedAtributeInstance;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class ProductionSourceGenerator extends SvrProcess {
	
	private int p_Record_ID;
	private Properties m_ctx = Env.getCtx();
	private String trxName;
  /*  private Timestamp vencimiento = null; */

    /**
     * Descripci�n de M�todo
     *
     * @return
     */
	
	protected void prepare() {
		this.setTrxName(get_TrxName()); 
		setRecord_ID(getRecord_ID());
	} // prepare

    /**
     * Descripci�n de M�todo
     *
     * @return estado en que termina, null significa que termino correctamente
     */
	
	protected String doIt() throws Exception {
		// busco la ProductionOrden en la que voy a trabajar
		MProductionOrder order = new MProductionOrder(getCtx(), p_Record_ID, getTrxName());

		if(order.getDocStatus() != MProductionOrder.ACTION_Complete | order.getDocStatus() != MProductionOrder.DOCACTION_Close) {
			this.eliminarRegistroAnteriores(order.getC_Production_Order_ID());
		//	this.procesarLineasDeProduccion();
		}

		return null;
	} // doIt

    /**
     * Descripci�n de M�todo: Para cada l�nea en la orden de producci�n reserva los art�culos
     * para esa orden, los articulos utilizados se guardan en C_Production_OrderLine_Source.
     * Para cada art�culo, busca stock y lo reserva. Si no hay suficiente stock de ese art�culo
     * busca un sustituto con stock.
     *
     * @return
     */
	
	public void procesarLineasDeProduccion(int line) {
		
		StringBuffer sql = new StringBuffer("SELECT * FROM C_Production_OrderLine WHERE isActive = 'Y' AND C_Production_Orderline_ID = ? ");
		PreparedStatement pstmt = null;

        try {
        	
            pstmt = DB.prepareStatement(sql.toString(),get_TrxName());
            pstmt.setInt(1,line);

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
            	if(rs.getInt("M_Product_ID") != 0) {
            		Timestamp vto = null;
					MAttributeSetInstance atrib = null;
					this.crearFuenteArticulo(rs.getInt("C_Production_OrderLine_ID"), vto, atrib);
					if(atrib != null) {
						MProductionOrderline actual = new MProductionOrderline(getCtx(), line, get_TrxName());
						actual.setM_AttributeSetInstance_ID(atrib.getM_AttributeSetInstance_ID());
						actual.save();
					}
            	}
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log(Level.SEVERE,"ProductionSourceGenerator - ProcesarLineasDeProduccion  ",e );
        }
	
	} // procesarLineasDeProduccion

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
     * Descripción de Método: Si se vuelve a procesar la orden de producción, es necesario liberar los
     * artículos que se reservaron con anterioridad, para que no haya duplicados. El proceso elimina
     * los registro que no son necesarios para volver a crear nuevos registro y reservar el producto
     *
     * @return
     */		
	
	/**
     * Descripción de Método: Para una cantidad de producto, crea una instacia del conjunto de atributo, para asignarlo a la clase
     *      
     * @param instance
     * @param linea
     * @param cantidad
     * 
     * @return retorna el ID de la instancia creada
     */	
	  private int createAttributeInstance(Vector<MAttributeSetInstance> instances, MProductionOrderline linea, BigDecimal cantidad) {
		  MAttributeSetInstance instance = null;
		  if(linea.getM_AttributeSetInstance_ID() == 0){
			  return 0;
		  }
		  MAttributeSetInstance ins_linea = new MAttributeSetInstance(getCtx(),linea.getM_AttributeSetInstance_ID(), get_TrxName());
			try {
				
				instance = MAttributeSetInstance.get(getCtx(), 0, linea.getM_Product_ID());
				instance.setGuaranteeDate(ins_linea.getGuaranteeDate());
				instance.setM_AttributeSet_ID(ins_linea.getM_AttributeSet_ID());
				instance.setSerNo(instance.getSerNo(true));
			   	instance.createAttachment();
				instance.createLot(linea.getM_Product_ID());
				instance.setDescription();	
							
			} catch (Exception e) {
				log.saveError("ProductionSourceGenerator - createAttributeInstance "," No se pudo encontrar un conjunto de atributo recomendado");
			}
			
			if(!instance.save()){} {
				log.saveError("ProductionSourceGenerator - createAttributeInstance","No se pudo guardar la instancia del atributo archivo fuente");
				
			} 
			
			instances.add(instance);
			
			return instance.getM_AttributeSetInstance_ID();
	}
	  
	    /**
	     * Descripci�n de M�todo
	     *
	     * @param ctx
	     * 
	     * @return
	     */
		  
	  
	private void eliminarRegistroAnteriores(int productionline_id) {
		// Este proceso elimina las l�neas de producci�n viejas
		StringBuffer sql = new StringBuffer("DELETE FROM M_ProductionLineSource WHERE C_Production_Order_ID = " + productionline_id); 
	    DB.executeUpdate(sql.toString());		
	} // eliminarRegistroAnteriores

    /**
     * Descripci�n de M�todo
     *
     * @param ctx
     * 
     * @return
     */
	
	public void setCtx(Properties m_ctx) {
		this.m_ctx = m_ctx;
	} // setCtx

    /**
     * Descripci�n de M�todo
     *
     * @return el contexto actual
     */
	
	public Properties getCtx() {
		return m_ctx;
	} // getCtx
	
    /**
     * Descripci�n de M�todo
     *
     * @return
     */
	
	private void setRecord_ID(int p_Record_ID) {
		this.p_Record_ID = p_Record_ID;
	} // setRecord_Id

    /**
     * Descripci�n de M�todo
     *
     * @return
     */
	
	private void setTrxName(String trxName) {
		this.trxName = trxName;
	} // setTrxName

    /**
     * Descripci�n de M�todo
     *
     * @return
     */
	
	private String getTrxName() {
		return trxName;
	} // getTrxName
	
    /**
     * Descripci�n de M�todo: Para un art�culo que viene como par�metro, busca si hay stock,
     *      
     * @param M_Product_ID
     * 
     * @return retorna la cantidad de stock de ese art�culo
     */
	
    private BigDecimal checkProductStock(int M_Product_ID) {
    
    BigDecimal retValue = Env.ZERO;
    StringBuffer sql = new StringBuffer( "SELECT m_product_id, bomQtyAvailable("+M_Product_ID+","+Env.getContext(Env.getCtx(), "#M_Warehouse_ID")+",0) as Cantidad from m_product " +
        		"WHERE m_product_id="+M_Product_ID+" AND ispurchased='N'" );
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
        log.log( Level.SEVERE,"ProductionSourceGenerator - CheckProductStock; " + e );
    }
    return retValue;
    } // checkProductStock
    
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
     * Descripción de Método: para una orden de producción asigna el conjunto de atributo adecuado a las
     * fuentes de las líneas. 
     *      
     * @param instances
     * @return 
     */	

	public void asignarConjuntosAtributos(int production_Order_ID) {
		MProductionOrderline line;
		X_M_ProductionLineSource fuente;
		StringBuffer sql = new StringBuffer("SELECT * FROM m_productionlinesource WHERE isActive = 'Y' AND C_Production_Order = ? ");
		PreparedStatement pstmt = null;
		MAttributeSetInstance atrib;
		MAttributeSetInstance lin_atrib;
		Vector<MAttributeSetInstance> Instances = new Vector<MAttributeSetInstance>();
		Timestamp fecha = null;
		
        try {
        	
            pstmt = DB.prepareStatement(sql.toString(),get_TrxName());
            pstmt.setInt(1,production_Order_ID);

            ResultSet rs = pstmt.executeQuery();
                        
            while( rs.next()) {
            	line = new MProductionOrderline(getCtx(), rs.getInt("c_production_orderline_id"), get_TrxName());
            	fuente = new X_M_ProductionLineSource(getCtx(), rs.getInt("M_ProductionLineSource_ID"), get_TrxName());
            	
            	atrib = new MAttributeSetInstance(getCtx(), fuente.getM_AttributeSetInstance_ID(), get_TrxName());

            	
            	// asignar numero de lote
            	atrib.createLot(fuente.getM_Product_ID());  
            	atrib.setDescription();
            	atrib.save();
            	
            	// asignar el conjunto de atributos 
            	line.setM_AttributeSetInstance_ID(atrib.getM_AttributeSetInstance_ID());
            	line.save();
            	
            	// verificacion de la fecha de vencimiento
            	lin_atrib = new MAttributeSetInstance(getCtx(), line.getM_AttributeSetInstance_ID(), get_TrxName());
            	lin_atrib.setDueDate(this.BuscarVencimiento(Instances));
            	lin_atrib.save();	 
            	            	
            	Instances.add(atrib);
            	                
            }

            rs.close();
            pstmt.close();
            pstmt = null;
           
        } catch( Exception e ) {
            log.log(Level.SEVERE,"ProductionSourceGenerator - asignarConjuntosAtributos  ",e );
        }
	} //asignarConjuntosAtributos
	

} // Fin: ProductionSourceGenerator
