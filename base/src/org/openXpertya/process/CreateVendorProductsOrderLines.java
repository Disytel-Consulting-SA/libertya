package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MPriceList;
import org.openXpertya.model.MPriceListVersion;
import org.openXpertya.model.MProductPricing;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class CreateVendorProductsOrderLines extends SvrProcess {

	/** Cantidad de semanas para las que se calcula el total de ventas
	 * del producto de la linea generada */
	private static int WEEKS_COUNT = 4;
	
	private static final String STR_WEEK = Msg.translate(Env.getCtx(), "ShortWeek"); 
	private static final String STR_SALES = Msg.translate(Env.getCtx(), "Sales");
	private static final String STR_QTY_ORDERED = Msg.translate(Env.getCtx(), "QtyOrdered");
	
	private int p_C_Order_ID = 0;
	private Properties ctx = Env.getCtx();
		
	@Override
	protected void prepare() {
		p_C_Order_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		if(p_C_Order_ID == 0)
			return "";

		// Contadores para armar el mensaje de respuesta.
		int linesCreated = 0;
		int notInPriceList = 0;
		int totalProducts = 0;
		
		// Se obtiene el pedido y la versión de la tarifa.
		MOrder order = new MOrder(ctx, p_C_Order_ID, get_TrxName());
		MPriceListVersion m_PriceList_Version = 
			new MPriceList(ctx, order.getM_PriceList_ID(), get_TrxName()).getPriceListVersion(order.getDateOrdered());
		int m_PriceList_Version_ID = 0;
		
		if (m_PriceList_Version != null)
			m_PriceList_Version_ID = m_PriceList_Version.getM_PriceList_Version_ID();
				
		// Consulta para obtener los productos que tiene configurado el proveedor.
		String sql = 
			" SELECT po.AD_Client_ID, po.AD_Org_ID, po.M_Product_ID, po.pricelist, po.order_min, COALESCE(bomQtyOrdered(po.M_Product_ID,?,0)) AS QtyOrdered " + 
			" FROM M_Product_PO po " + 
			" INNER JOIN M_Product p ON (po.M_Product_ID = p.M_Product_ID) " +
			" WHERE po.IsActive = 'Y' AND po.IsCurrentVendor = 'Y' AND p.IsActive = 'Y' AND po.C_BPartner_ID = ? " +
			" ORDER BY p.name ASC ";
		
        try {
            PreparedStatement pstmt = DB.prepareStatement(sql);
            // El almacén es para determinar que cantidad hay para recibir de cada producto.
            pstmt.setInt(1, order.getM_Warehouse_ID());
            pstmt.setInt(2, order.getC_BPartner_ID());
            ResultSet rs = pstmt.executeQuery();

            MOrderLine ol;
    		int m_Product_ID;
    		MProductPricing pp;
    		int lineNo = 10;
    		
            while (rs.next()) {
            	// Se obtiene el ID del producto y la cantidad a recibir en el almacén
            	// configurado para el pedido.
            	m_Product_ID = rs.getInt("M_Product_ID");
            	BigDecimal qtyOrdered = rs.getBigDecimal("QtyOrdered");
            	totalProducts++;
            	// Se crea la línea del pedido.
            	ol = new MOrderLine(order);
            	ol.setM_Product_ID(m_Product_ID);
            	// El número de línea se asigna manualmente por cuestiones de eficiencia.
            	ol.setLine(lineNo);
            	// Se crea el ProductPricing para obtener los precios del artículo.
            	pp = new MProductPricing( m_Product_ID, order.getC_BPartner_ID(), Env.ZERO, order.isSOTrx());
                pp.setM_PriceList_ID(order.getM_PriceList_ID());
                pp.setM_PriceList_Version_ID(m_PriceList_Version_ID);
                pp.setPriceDate(order.getDateOrdered());
                // Si no se pudo calcular el precio del artículos se incrementa el contador
                // que indica la cantidad de artículos que no estan en la tarifa configurada
                // para el pedido. En este caso la línea del pedido no es guardada.
                if(!pp.calculatePrice())
                	notInPriceList++;
                
                // Se asignan los datos del artículo a la línea del pedido.
                else {
                	ol.setPriceList(pp.getPriceList());
                	ol.setPriceLimit(pp.getPriceLimit());
                	ol.setPriceActual(pp.getPriceList());
                	ol.setPriceEntered(pp.getPriceList());
                	ol.setC_Currency_ID(pp.getC_Currency_ID());
                	ol.setDiscount(pp.getDiscount());
                	ol.setC_UOM_ID(pp.getC_UOM_ID());
                	ol.setQty(Env.ZERO);
                	ol.setTax();
                	// Se crea la descripción a partir de datos históricos.
                	setOrderLineDescription(ol, qtyOrdered);
                	
                	if (ol.save()) {
                		linesCreated++;
                		lineNo += 10;
                	} else
                		log.warning("Could not generate the vendor order line. M_Product_ID="+ m_Product_ID);
                }
            }
            
        } catch (SQLException e) {
			log.severe("Could not get the vendor products. " + e.getMessage());
			e.printStackTrace();
			throw new Exception(Msg.parseTranslation(ctx, "@Error@: @CreateVendorLinesError@ "));
		}

        // Se crea el mensaje de retorno.
        String summary = "@Completed@. " +  Msg.getMsg(ctx, "CreateVendorLinesSummary", new Object[] { totalProducts, linesCreated });
        if(notInPriceList > 0)
        	summary = summary + " (" + Msg.getMsg(ctx, "VendorProductsNotInPriceList", new Object[] { notInPriceList }) +  ")";
        
        return summary;
	}
	
	private void setOrderLineDescription(MOrderLine ol, BigDecimal qtyOrdered) {
		int m_Product_ID = ol.getM_Product_ID();
		Map<Integer, BigDecimal> weekQty = new HashMap<Integer, BigDecimal>();
		
		// Se crea el calendario seteandole la fecha con la fecha del pedido.
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ol.getDateOrdered());
		// Se asigna el primer domingo contando hacia atras a partir de la fecha del pedido.
		// Este domingo será la fecha de fin de la semana 4 de ventas.
		calendar.add(Calendar.DATE, -1 * (calendar.get(Calendar.DAY_OF_WEEK)-Calendar.SUNDAY));

		// Fecha de inicio y fin de cada semana.
		Timestamp fromDate = null;
		Timestamp toDate = new Timestamp(calendar.getTimeInMillis());
		
		// Se crea la consulta que busca las ventas del producto en un rango de fechas.
		String sql =
			" SELECT COALESCE(SUM(l.QtyInvoiced),0) AS QtyInvoiced " +
			" FROM C_Invoice i INNER JOIN C_InvoiceLine l ON (i.C_Invoice_ID = l.C_Invoice_ID) "+
			" WHERE i.IsActive = 'Y' AND l.IsActive = 'Y' AND l.M_Product_ID = ? AND i.DocStatus IN ('CO', 'CL') AND i.IsSOTrx = 'Y' " + 
			" AND (CAST(? as Date) <= i.DateInvoiced AND i.DateInvoiced <= CAST(? as Date)) "; 

		try {
			PreparedStatement pstmt = DB.prepareStatement(sql);
	        ResultSet rs;
	        pstmt.setInt(1, m_Product_ID);
	        
	        for(int i = WEEKS_COUNT; i > 0; i--) {
	        	// Se obtiene la fecha de inicio de la semana.
	        	// El día de inicio es LUNES, el día de fin es DOMINGO.
	        	// La consulta compara por <= en ambos límites.
	        	calendar.add(Calendar.DATE, -6);
	        	fromDate = new Timestamp(calendar.getTimeInMillis());
	        	
	        	log.fine("Searching product's sales quantity. M_Product_ID=" + m_Product_ID + " fromDate=" + fromDate + " toDate=" + toDate);
	        	
	        	// Se asignan los parámetros del rango de fechas.
	        	pstmt.setTimestamp(2, fromDate);
	        	pstmt.setTimestamp(3, toDate);
	        	rs = pstmt.executeQuery();
	        	
	        	BigDecimal qty = Env.ZERO;
	        	// Se obtiene la cantidad vendida a partir del resultado de la consulta.
	        	if(rs.next())
	        		qty = rs.getBigDecimal("QtyInvoiced");

	        	rs.close();
	        	
	        	// Se preparan las fecha de inicio para la proxima semana.
	        	// Dado que el calendario está posicionado en la fecha de inicio de la
	        	// semana anterior, esto es, en un día LUNES, se resta un día para posicionarse
	        	// en el DOMINGO anterior, el cual es la fecha de fin de la semana anterior.
	        	calendar.add(Calendar.DATE, -1);
	        	toDate = new Timestamp(calendar.getTimeInMillis());
	        	
	        	// Se guarda la cantidad del producto.
	        	weekQty.put(i, qty);
	        }
	        
	        pstmt.close();
	        
	        // Se crea la descipción a partir de los datos de venta obtenidos de cada semana
	        // y la cantidad para recibir del producto.
	        StringBuffer desc = new StringBuffer();
	        desc.append(STR_QTY_ORDERED + " [ ");
	        desc.append(qtyOrdered.setScale(2, BigDecimal.ROUND_HALF_UP));
	        desc.append(" ] " + STR_SALES + " [ ");
	        for (int i = 1; i <= WEEKS_COUNT ; i++) {
	        	desc.append(STR_WEEK + " " + i + " = ");
	        	BigDecimal qty = weekQty.get(i); 
	        	desc.append(qty.setScale(2, BigDecimal.ROUND_HALF_UP));
	        	if(i < WEEKS_COUNT)
	        		desc.append(" | ");
			}
	        desc.append(" ]");
	        ol.setDescription(desc.toString());
	        
	        
		} catch (SQLException e) {
			log.warning("Could not get product's sales information." + e.getLocalizedMessage());
		}
	}
}
