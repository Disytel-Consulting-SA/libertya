package org.openXpertya.JasperReport.DataSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Properties;

import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MShipper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class InOutDataSource implements JRDataSource {

	/**	Properties				*/
	private Properties p_ctx = null;	
	
	/** Inout	& Lines			*/
	private MInOut inout = null;
	private MInOutLine inoutLine = null;
	int m_currentRecord = -1;
	int total_lines = -1;

	/** Utilizado para mapear los campos con las invocaciones de los metodos  */
	HashMap<String, String> methodMapper = new HashMap<String, String>(); 
	
	public InOutDataSource(Properties ctx, MInOut inout)
	{
		this.p_ctx = ctx;
		this.inout = inout;
		
		methodMapper.put("ITEM", "getLineStr");
		methodMapper.put("CODARTICULO", "getProductValue");
		methodMapper.put("CANTIDAD", "getQtyEntered");
		methodMapper.put("DESCRIPCION", "getProductName");
		methodMapper.put("UNIDAD", "getUOMName");
		
		methodMapper.put("PRECIO_UNITARIO", "getPriceEnteredNet");
		methodMapper.put("IMPORTE", "getTotalPriceEnteredNet");
		
		methodMapper.put("CHARGE", "getChargeName");
		methodMapper.put("COUNTRY", "getCountryName");
		methodMapper.put("PROJECT", "getProjectName");
		
		methodMapper.put("CONFIRMED_QTY", "getConfirmedQty");
		methodMapper.put("DECLARATIONNO", "getdeclarationno");
		methodMapper.put("LOCATOR", "getLocatorDescription");
		methodMapper.put("WAREHOUSE", "getWarehouseName");
		methodMapper.put("MOVEMENT_QTY", "getMovementQty");
		methodMapper.put("PICKED_QTY", "getPickedQty");
		methodMapper.put("REF_INOUTLINE", "getRefInOutLineDescription");
		methodMapper.put("SCRAPPED_QTY", "getScrappedQty");
		methodMapper.put("TARGET_QTY", "getTargetQty");
		
		methodMapper.put("IS_INVOICED", "isInvoiced");
		methodMapper.put("IS_SELECTED", "isSelected");
		methodMapper.put("IS_TAXINCLUDED", "isTaxIncluded");
		
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
		
		methodMapper.put("PRICESTDCOST", "getPriceStdCost");
		methodMapper.put("PRICESTDSALES", "getPriceStdSales");

		methodMapper.put("CLEARANCENUMBER", "getClearanceNumber");
		methodMapper.put("COSTPRICE", "getCostPrice");
		
		methodMapper.put("LINE", "getLine");
		methodMapper.put("REF_ORDERLINE", "getC_OrderLine_ID");
		methodMapper.put("QTYDELIVERED", "getQtyEntered");
		
	}
	
	public void loadData() throws RuntimeException {
		total_lines = inout.getLines().length;
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
			if(name.equals("FECHA_ENTREGA")) {
				output = inout.getDateReceived()!=null ? inout.getDateReceived() : inout.getMovementDate();
			} else if (name.equals("DESCUENTO")) {
				if(inout.getC_Invoice_ID() > 0) {
					MInvoice invoice = new MInvoice(p_ctx, inout.getC_Invoice_ID(), null);
					output = invoice.getDiscountsAmt();
				}else
					output = new BigDecimal(0);
			} else if (name.equals("TAX_RATE")) {
				if(inout.getC_Invoice_ID() > 0) {
					MInvoice invoice = new MInvoice(p_ctx, inout.getC_Invoice_ID(), null);
					output = invoice.getTaxRateFromProduct();
				}else
					output = new BigDecimal(0);
			} else if (name.equals("TAX_AMT")) {
				if(inout.getC_Invoice_ID() > 0) {
					MInvoice invoice = new MInvoice(p_ctx, inout.getC_Invoice_ID(), null);
					output = invoice.getTaxesAmt();
				}else
					output = new BigDecimal(0);
			}else if (name.equals("DATE_DELIVERED")) {
				output = inout.getInOutReceptionDate()!=null ? inout.getInOutReceptionDate() : null;
			}else if (name.equals("DATE_INVOICED")) {
				if(inout.getC_Invoice_ID() > 0) {
					MInvoice invoice = new MInvoice(p_ctx, inout.getC_Invoice_ID(), null);
					output = invoice.getDateInvoiced();
				}else
					output = null;
			}else if (name.equals("DATE_ORDERED")) {
				if(inout.getC_Order_ID() > 0) {
					MOrder order = new MOrder(p_ctx, inout.getC_Order_ID(), null);
					output = order.getDateOrdered();
				}else
					output = null;
			}else if (name.equals("DATE_PROMISED")) {
				if(inout.getC_Order_ID() > 0) {
					MOrder order = new MOrder(p_ctx, inout.getC_Order_ID(), null);
					output = order.getDatePromised();
				}else
					output = null;
			}else if (name.equals("FREIGHT_AMT")) {
				if(inout.getC_Order_ID() > 0) {
					MOrder order = new MOrder(p_ctx, inout.getC_Order_ID(), null);
					output = order.getFreightAmt();
				}else
					output = inout.getFreightAmt(); 
			}else if (name.equals("SHIPPER")) {
					int id = inout.getM_Shipper_ID();
					if(id > 0) {
						MShipper s = new MShipper(p_ctx, id, null);
						output = s.getName();
					}
			}else if (name.equals("QTYORDERED")) {
				if(inoutLine.getC_OrderLine_ID() > 0) {
					MOrderLine orderL = new MOrderLine(p_ctx, inoutLine.getC_OrderLine_ID(), null);
					output = orderL.getQtyOrdered();
				}
			}else if (name.equals("QTYRESERVED")) {
				if(inoutLine.getC_OrderLine_ID() > 0) {
					MOrderLine orderL = new MOrderLine(p_ctx, inoutLine.getC_OrderLine_ID(), null);
					output = orderL.getQtyReserved();
				}
			}else if (name.equals("QTYINVOICED")) {
				if(inoutLine.getC_OrderLine_ID() > 0) {
					MOrderLine orderL = new MOrderLine(p_ctx, inoutLine.getC_OrderLine_ID(), null);
					output = orderL.getQtyInvoiced();
				}
			}else if (name.equals("X12DE355")) {
				;// Campo reservado para compatibilidad con informes X12
			}else {
				clazz = Class.forName("org.openXpertya.model.MInOutLine");
				method = clazz.getMethod(methodMapper.get(name));
				output = (Object) method.invoke(inoutLine);
			}
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
		
		inoutLine = inout.getLines()[m_currentRecord]; 
		return true;
	}	
}
