package org.openXpertya.util;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.model.MOrderLine;

public class ReservedUtil {

	public ReservedUtil() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Obtiene la suma de las columnas en formato sql, mediante el alias de la
	 * tabla parámetro, de aquellas que son parte de la cantidad real entregada,
	 * teniendo en cuenta la cantidad entregada (qtydelivered), cantidad
	 * transferida, etc. Esta suma retornada se puede tomar para el cálculo de
	 * pendientes ya que si tomamos la cantidad pedida de una línea de pedido y
	 * le restamos esta suma de columnas, obtenemos el pendiente.
	 * 
	 * @param ctx
	 * @param tableAlias
	 * @return
	 */
	public static String getSQLRealDeliveredQtyByColumns(Properties ctx, String tableAlias){
		return " ( " + 
					tableAlias + ".QtyDelivered + " + 
					tableAlias + ".QtyTransferred + " + 
					(Env.isAllowDeliveryReturn(ctx)? " 0 ":tableAlias+".QtyReturned" ) + 
				" ) ";
	}
	
	/**
	 * Obtiene el pendiente en formato columnas sql, mediante el alias de la
	 * tabla parámetro.
	 * 
	 * @param ctx
	 * @param tableAlias
	 * @return
	 */
	public static String getSQLPendingQtyByColumns(Properties ctx, String tableAlias){
		return tableAlias+".QtyOrdered-"+getSQLRealDeliveredQtyByColumns(ctx, tableAlias);
	}
	
	/**
	 * @return la cantidad devuelta a tener en cuenta para el cálculo del
	 *         pendiente del pedido, no se debe tener en cuenta si se permite
	 *         entregar devoluciones
	 */
    public static BigDecimal getQtyReturnedForPending(MOrderLine orderLine){
		return Env.isAllowDeliveryReturn(orderLine.getCtx()) ? BigDecimal.ZERO : orderLine.getQtyReturned();
    }
    
    /**
	 * @return la cantidad devuelta a tener en cuenta para el cálculo del
	 *         pendiente del pedido, no se debe tener en cuenta si se permite
	 *         entregar devoluciones
	 */
    public static BigDecimal getQtyReturnedForPending(Properties ctx, BigDecimal qtyReturned){
		return Env.isAllowDeliveryReturn(ctx) ? BigDecimal.ZERO : qtyReturned;
    }
	
	/**
	 * @return la cantidad real entregada de la línea de pedido. La cantidad
	 *         pendiente de una línea de pedido se compone justamente de la
	 *         cantidad pedida - esta cantidad real entregada
	 */	
	public static BigDecimal getOrderLineRealDelivered(MOrderLine orderLine){
		return orderLine.getQtyDelivered().add(orderLine.getQtyTransferred()).add(getQtyReturnedForPending(orderLine));
	}
	
	/**
	 * @return la cantidad pendiente de la línea del pedido
	 */	
	public static BigDecimal getOrderLinePending(MOrderLine orderLine){
		return orderLine.getQtyOrdered().subtract(getOrderLineRealDelivered(orderLine));
	}
	
	/**
	 * Obtiene la cantidad real entregada de la línea de pedido. La cantidad
	 * pendiente de una línea de pedido se compone justamente de la cantidad
	 * pedida - esta cantidad real entregada
	 * 
	 * @param ctx
	 * @param qtyDelivered
	 *            cantidad entregada
	 * @param qtyTransferred
	 *            cantidad transferida
	 * @param qtyReturned
	 *            cantidad devuelta
	 * @return
	 */
	public static BigDecimal getOrderLineRealDelivered(Properties ctx, BigDecimal qtyDelivered, BigDecimal qtyTransferred, BigDecimal qtyReturned){
		return qtyDelivered.add(qtyTransferred).add(getQtyReturnedForPending(ctx, qtyReturned));
	}
	
	/**
	 * Obtiene la pendiente del pedido, en base a la cantidad objetivo a evaluar
	 * contra la cantidad real entregada, basados en las cantidades parámetros
	 * 
	 * @param ctx
	 * @param target
	 *            cantidad objetivo a evaluar, generalmente es qtyOrdered, pero
	 *            se puede pasar cualquier valor necesario base del pendiente
	 * @param qtyDelivered
	 *            cantidad entregada
	 * @param qtyTransferred
	 *            cantidad transferida
	 * @param qtyReturned
	 *            cantidad devuelta
	 * @return
	 */
	public static BigDecimal getOrderLinePending(Properties ctx, BigDecimal target, BigDecimal qtyDelivered, BigDecimal qtyTransferred, BigDecimal qtyReturned){
		return target.subtract(getOrderLineRealDelivered(ctx, qtyDelivered, qtyTransferred, qtyReturned));
	}
}
