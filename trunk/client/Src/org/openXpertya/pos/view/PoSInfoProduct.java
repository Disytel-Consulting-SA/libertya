package org.openXpertya.pos.view;

import java.awt.Frame;
import java.math.BigDecimal;
import java.util.HashMap;

import org.openXpertya.apps.search.InfoProduct;
import org.openXpertya.apps.search.Info_Column;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class PoSInfoProduct extends InfoProduct {

	// cache directa de PosInfoProduct (evita cuantiosas invocaciones dentro de Msg)
	protected static HashMap<String, String> columnLabels = new HashMap<String, String>();
	
	public PoSInfoProduct(Frame frame, boolean modal, int WindowNo, int M_Warehouse_ID, int M_PriceList_ID, String value, boolean multiSelection, String whereClause) {
		super(frame, modal, WindowNo, M_Warehouse_ID, M_PriceList_ID, value,
				multiSelection, whereClause);
		getPickPriceList().setEnabled(false);
		getPickWarehouse().setEnabled(false);
		if (whereClause != null && whereClause.length() > 0)
			executeQuery();
	}

	/**
	 * Redefinición: reducción de columnas a fin de lograr menores tiempos de respuesta,
	 * dado que el uso de sqlj consume una cantidad considerable de tiempo en los casos
	 * que el número de artículos a procesar sea demasiado grande.  Quitando columnas
	 * que no son completamente necesarias, se reduce al menos a la tercera parte.
	 */
	protected Info_Column[] getInfoColumns()
	{
		Info_Column[] col =
		{
	        new Info_Column( " ","DISTINCT(p.M_Product_ID)",IDColumn.class ),
	        //new Info_Column( Msg.translate( Env.getCtx(),"Discontinued" ).substring( 0,1 ),"p.Discontinued",Boolean.class ),
	        new Info_Column( getFromCache( "Value" ),"p.Value",String.class ),
	        new Info_Column( getFromCache( "Name" ),"p.Name",String.class ),
	        new Info_Column( getFromCache( "QtyAvailable" ),"infoproductbomqty('bomQtyOnHand',p.M_Product_ID,?,0) AS QtyOnHand",Double.class,true,true,null ),
	        new Info_Column( getFromCache( "PriceStd" ),"bomPriceStd(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceStd",BigDecimal.class ),
	        //new Info_Column( Msg.translate( Env.getCtx(),"Unconfirmed" ),"(SELECT SUM(c.TargetQty) FROM M_InOutLineConfirm c INNER JOIN M_InOutLine il ON (c.M_InOutLine_ID=il.M_InOutLine_ID) INNER JOIN M_InOut i ON (il.M_InOut_ID=i.M_InOut_ID) WHERE c.Processed='N' AND i.M_Warehouse_ID=? AND il.M_Product_ID=p.M_Product_ID) AS Unconfirmed",Double.class ),
	        //new Info_Column( Msg.translate( Env.getCtx(),"Margin" ),"bomPriceStd(p.M_Product_ID, pr.M_PriceList_Version_ID)-bomPriceLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS Margin",BigDecimal.class ),new Info_Column( Msg.translate( Env.getCtx(),"PriceLimit" ),"bomPriceLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceLimit",BigDecimal.class ),
	        //new Info_Column( Msg.translate( Env.getCtx(),"IsInstanceAttribute" ),"pa.IsInstanceAttribute",Boolean.class )
		};	
		return col;
	}
	
	/** Busqueda en Cache */
	protected String getFromCache(String colName)
	{
		String retValue = columnLabels.get(colName);
		if (retValue == null)
		{
			retValue = Msg.translate( Env.getCtx(),colName );
			columnLabels.put(colName, retValue);
		}
		return retValue;
	}
	
	
}
