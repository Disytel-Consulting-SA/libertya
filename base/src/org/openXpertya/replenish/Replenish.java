package org.openXpertya.replenish;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openXpertya.model.MLocator;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MReplenish;
import org.openXpertya.model.MStorage;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.model.X_M_ReplenishSystem;
import org.openXpertya.replenish.replenishFunc.ReplenishFunc;
import org.openXpertya.replenish.replenishFunc.ReplenishFuncException;
import org.openXpertya.report.jcalc.Calculator;
import org.openXpertya.report.jcalc.CalculatorException;
import org.openXpertya.util.Env;

public class Replenish extends MReplenish {

	private BigDecimal qtyToOrder; 
	
	public Replenish(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public Replenish(Properties ctx, int M_Replenish_ID, String trxName) {
		super(ctx, M_Replenish_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	
	private BigDecimal doCalc(String func)
	{
		String decoded = decodeFunc(func);
		return calc(decoded); 
	}
	
	private String decodeFunc(String func)
	{
		
		log.info("Decoding " + func);

		// Ahora las sustuciones por nombre 
		String regExp = "@(\\w+)@";
		Pattern pattern= Pattern.compile(regExp,Pattern.MULTILINE);
		Matcher matcher=pattern.matcher(func);

		while(matcher.find())	{	
			for(int i=1;i<=matcher.groupCount();i++)	{
				String group = matcher.group(i);  // CS, CP, T, R, SS...

				BigDecimal res = new BigDecimal(0);
				if (group.equalsIgnoreCase("CS"))
					res = this.getCS();
				else if (group.equalsIgnoreCase("CP"))
					res = this.getCP();
				else if (group.equalsIgnoreCase("T"))
					res = this.getT();
				else if (group.equalsIgnoreCase("R"))
					res = this.getR();
				else if (group.equalsIgnoreCase("SS"))
					res = this.getSS();
				else
					log.severe("No replacement found");
				
				func = matcher.replaceFirst(res.toString());
				matcher=pattern.matcher(func);
			}

		}
		
		
		// Ejecutamos la invocaci�n a funciones que hayan metido
		regExp = "[a-zA-Z_-]+ ?\\([a-zA-Z0-9\",' ]*\\)";
		pattern= Pattern.compile(regExp,Pattern.MULTILINE);
		matcher=pattern.matcher(func);

		while(matcher.find())	{
			
			for(int i=0;i<=matcher.groupCount();i++)	{
				String cmdLine = matcher.group(i);
		
				String res = execFunc(cmdLine);
				
				func = matcher.replaceFirst(res);
				matcher=pattern.matcher(func);
			}

		}		
		
		return func;
		
	}
	
	private BigDecimal calc(String exp)	
	{
		log.info("calc: " + exp);
		
		String res = "0";
		
		// Creamos la calculadora
		Calculator cal = new Calculator();
		try	{
			// Y calculamos la expresion.
			res = cal.evaluate_equation(exp);
		}
		catch (CalculatorException e)	{
			log.severe("Fallo al calcular la expresion: " + exp + " | " + e.toString());
			e.printStackTrace();
			return Env.ZERO;
		}
		return new BigDecimal(res);
	}

	private String execFunc(String cmdLine)	{
		ReplenishFunc func = ReplenishFuncFactory.getReplenishFunc(this, cmdLine);
		BigDecimal res = Env.ZERO;
		
		try {
			func.prepare();
		}
		catch (ReplenishFuncException e)	{
			log.severe("Error preparando la funcion: " + e);
			return null;
		}
		
		try	{
			res = func.doIt();
		}
		catch (ReplenishFuncException e)	{
			log.severe("Error ejecutando la funcion: " + e);
			return null;
		}		
		
		
		return res.toString();
	}
	
	public boolean needReplenish()
	{
		// stock actual?
		MProduct product = new MProduct(getCtx(), this.getM_Product_ID(), get_TrxName());
		MWarehouse warehouse = new MWarehouse(getCtx(), this.getM_Warehouse_ID(), get_TrxName());
		BigDecimal currentStock = MStorage.getQtyAvailable(warehouse.getM_Warehouse_ID(), product.getM_Product_ID() );
		if (currentStock == null) 
			currentStock = new BigDecimal(0);
		
		// deben pedirse?
		X_M_ReplenishSystem replenishSystem = new X_M_ReplenishSystem(getCtx(), this.getM_ReplenishSystem_ID(), get_TrxName());		
		BigDecimal minStock = doCalc(replenishSystem.getminstockfunc());
		if (minStock == null) 
			minStock = new BigDecimal(0);		
		boolean needReplenish = minStock.compareTo(currentStock) < 0;

		// cuantas deben pedirse?		
		if (needReplenish)
			qtyToOrder = doCalc(replenishSystem.getqtyfunc());

		return needReplenish;  
	}
	
	public BigDecimal getQtyToOrder()
	{
		return qtyToOrder;
	}
	
}
