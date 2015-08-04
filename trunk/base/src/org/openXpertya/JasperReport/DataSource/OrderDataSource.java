package org.openXpertya.JasperReport.DataSource;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;


public class OrderDataSource implements JRDataSource  {

	/**	Properties				*/
	private Properties p_ctx = null;	
	
	/** Order	& Lines			*/
	private MOrder order = null;
	private MOrderLine orderLine = null;
	int m_currentRecord = -1;
	int total_lines = -1;

	/** Utilizado para mapear los campos con las invocaciones de los metodos  */
	HashMap<String, String> methodMapper = new HashMap<String, String>(); 
	
	public OrderDataSource(Properties ctx, MOrder order)
	{
		this.p_ctx = ctx;
		this.order = order;
		
		methodMapper.put("FECHA_ENTREGA", "getDatePromised");
		methodMapper.put("CODARTICULO", "getProductValue");
		methodMapper.put("CANTIDAD", "getQtyEntered");
		methodMapper.put("DESCRIPCION", "getProductName");
		methodMapper.put("INSTANCENAME", "getInstanceName");
		methodMapper.put("PRECIO_UNITARIO", "getPriceEnteredNet");
		methodMapper.put("DESCUENTO", "getDiscount");
		methodMapper.put("IMPORTE", "getTotalPriceEnteredNet");
		methodMapper.put("VENDOR_PRODUCT_NO", "getVendorProductNo");
		
		methodMapper.put("ITEM", "getLineStr");
		methodMapper.put("UNIDAD", "getUOMName");
		methodMapper.put("X12DE355", "getX12DE355");
		methodMapper.put("UPC", "getProductUPC");
		
		methodMapper.put("CHARGE", "getChargeName");
		methodMapper.put("TAX_RATE", "getTaxRate");
		methodMapper.put("PROJECT", "getProjectName");
		methodMapper.put("TAX_AMT", "getTaxAmt");
		
		methodMapper.put("DATE_DELIVERED", "getDateDelivered");
		methodMapper.put("DATE_INVOICED", "getDateInvoiced");
		methodMapper.put("DATE_ORDERED", "getDateOrdered");
		methodMapper.put("DATE_PROMISED", "getDatePromised");
		methodMapper.put("FREIGHT_AMT", "getFreightAmt");
		methodMapper.put("IS_TAXINCLUDED", "isTaxIncluded");
		methodMapper.put("LINE", "getLine");
		methodMapper.put("SHIPPER", "getShipperName");
		methodMapper.put("WAREHOUSE", "getWarehouseName");
		methodMapper.put("REF_ORDERLINE", "getRefOrderLineDescription");
		
		methodMapper.put("QTYDELIVERED", "getQtyDelivered");
		methodMapper.put("QTYINVOICED", "getQtyInvoiced");
		methodMapper.put("QTYORDERED", "getQtyOrdered");
		methodMapper.put("QTYRESERVED", "getQtyReserved");
		
		methodMapper.put("PRICELIST", "getPriceListWithTax");
		methodMapper.put("PRICELIST_NET", "getPriceListNet");
		methodMapper.put("TOTAL_PRICELIST", "getTotalPriceListWithTax");
		methodMapper.put("TOTAL_PRICELIST_NET", "getTotalPriceListNet");
		
		methodMapper.put("PRICEENTERED", "getPriceEnteredWithTax");
		methodMapper.put("PRICEENTERED_NET", "getPriceEnteredNet");
		methodMapper.put("TOTAL_PRICEENTERED", "getTotalPriceEnteredWithTax");
		methodMapper.put("TOTAL_PRICEENTERED_NET", "getTotalPriceEnteredNet");
		
		methodMapper.put("PRICEACTUAL", "getPriceActualWithTax");
		methodMapper.put("PRICEACTUAL_NET", "getPriceActualNet");
		methodMapper.put("TOTAL_PRICEACTUAL", "getTotalPriceActualWithTax");
		methodMapper.put("TOTAL_PRICEACTUAL_NET", "getTotalPriceActualNet");
		
		methodMapper.put("BONUS_UNITY", "getBonusUnityAmtWithTax");
		methodMapper.put("BONUS_UNITY_NET", "getBonusUnityAmtNet");
		methodMapper.put("TOTAL_BONUS", "getTotalBonusUnityAmtWithTax");
		methodMapper.put("TOTAL_BONUS_NET", "getTotalBonusUnityAmtNet");
		
		methodMapper.put("LINEDISCOUNT_UNITY", "getLineDiscountUnityAmtWithTax");
		methodMapper.put("LINEDISCOUNT_UNITY_NET", "getLineDiscountUnityAmtNet");
		methodMapper.put("TOTAL_LINEDISCOUNT", "getTotalLineDiscountUnityAmtWithTax");
		methodMapper.put("TOTAL_LINEDISCOUNT_NET", "getTotalLineDiscountUnityAmtNet");
		
		methodMapper.put("DOCUMENTDISCOUNT_UNITY", "getDocumentDiscountUnityAmtWithTax");
		methodMapper.put("DOCUMENTDISCOUNT_UNITY_NET", "getDocumentDiscountUnityAmtNet");
		methodMapper.put("TOTAL_DOCUMENTDISCOUNT", "getTotalDocumentDiscountUnityAmtWithTax");
		methodMapper.put("TOTAL_DOCUMENTDISCOUNT_NET", "getTotalDocumentDiscountUnityAmtNet");
		
	}
	
	public void loadData() throws RuntimeException {
		total_lines = order.getLines().length;
	}
	
	/* Retorna el valor correspondiente al campo indicado */
	public Object getFieldValue(JRField field) throws JRException {
		
		String name = null;
		Class<?> clazz = null;
		Method method = null;
		Object output = null;
		try
		{
			// Invocar al metodo segun el campo correspondiente
			name = field.getName().toUpperCase();
		    clazz = Class.forName("org.openXpertya.model.MOrderLine");
		    method = clazz.getMethod(methodMapper.get(name));
		    output = (Object) method.invoke(orderLine);
		}
		catch (ClassNotFoundException e) { 
			throw new JRException("No se ha podido obtener el valor del campo " + name); 
		}
		catch (NoSuchMethodException e) { 
			throw new JRException("No se ha podido invocar el metodo " + methodMapper.get(name)); 
		}
		catch (InvocationTargetException e) { 
			throw new JRException("Excepcion al invocar el método " + methodMapper.get(name)); 
		}
		catch (Exception e) { 
			throw new JRException("Excepcion general al acceder al campo " + name); 
		}
		return output;
		
	}

	public boolean next() throws JRException {
		m_currentRecord++;
		
		if (m_currentRecord >= total_lines )	{
			return false;
		}
		
		orderLine = order.getLines()[m_currentRecord]; 
		return true;
	}	
}

