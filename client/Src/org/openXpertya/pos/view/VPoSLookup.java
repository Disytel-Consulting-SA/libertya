package org.openXpertya.pos.view;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openXpertya.apps.search.Info;
import org.openXpertya.apps.search.InfoProduct;
import org.openXpertya.apps.search.InfoProductAttribute;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.model.Lookup;
import org.openXpertya.pos.model.Product;
import org.openXpertya.util.Env;
import org.openXpertya.util.MeasurableTask;
import org.openXpertya.util.TimeStatsLogger;

public class VPoSLookup extends VLookup {

	private List<Product> productList = new ArrayList<Product>();
	private int attributeSetInstanceID = 0;
	private boolean isCustom = false;
	
	public VPoSLookup(String columnName, boolean mandatory, boolean isReadOnly, boolean isUpdateable, Lookup lookup) {
		super(columnName, mandatory, isReadOnly, isUpdateable, lookup);
	}

	@Override
	protected void showInfoProduct(String queryValue, String whereClause) {
		whereClause = getWhereClause(whereClause);
		// Reset

        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID","0" );
        Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID","0" );

        // Replace Value with name if no value exists

        if( (queryValue.length() == 0) && (getM_text().getText().length() > 0) ) {
            queryValue = "@" + getM_text().getText() + "@";    // Name indicator - otherwise Value
        }

        int M_Warehouse_ID = Env.getContextAsInt( Env.getCtx(),getM_lookup().getWindowNo(),"M_Warehouse_ID" );
        int M_PriceList_ID = Env.getContextAsInt( Env.getCtx(),getM_lookup().getWindowNo(),"M_PriceList_ID" );

        // Show Info
        InfoProduct ip = Info.factoryProduct( frame,true,getM_lookup().getWindowNo(),M_Warehouse_ID,M_PriceList_ID,queryValue,false,whereClause, true );

        TimeStatsLogger.endTask(MeasurableTask.POS_SEARCH_PRODUCT_SHOW_INFOPRODUCT);
        
        ip.setVisible(true);
        ip.requestFocusInWindow();
        ip.requestFocus();
        cancelled  = ip.isCancelled();
        result     = ip.getSelectedKey();
        resetValue = true;
        if (ip instanceof InfoProductAttribute)
        	attributeSetInstanceID = ((InfoProductAttribute)ip).getAttributeSetInstanceID();
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}
	
	private String getWhereClause(String whereClause) {
		String newWhereClause = whereClause;
		if(whereClause != null && whereClause.length() == 0 && !getProductList().isEmpty()) {
			newWhereClause = "( ";
			for (Iterator products = getProductList().iterator(); products.hasNext();) {
				Product product = (Product) products.next();
				newWhereClause = newWhereClause + "p.M_Product_ID = " + product.getId();
				if(products.hasNext())
					newWhereClause = newWhereClause + " OR ";
			}
			newWhereClause = newWhereClause + ")";
		}
		return newWhereClause;
	}

	public void openInfoProduct(List<Product> productList) {
		setProductList(productList);
		super.actionButton("");
		getProductList().clear();
	}
	
	public void openInfoProduct() {
		getProductList().clear();
		super.actionButton("");
	}
	
	public void openInfoOrder() {
		super.actionButton("");
	}

	@Override
	protected void actionButton(String queryValue) {
		// TODO: BONA Descomentar esta línea para solucionar luego el problema.
		if (!(queryValue != null && queryValue.equals(""))){
			super.actionButton(queryValue);
		}
		else if(!isCustom()){
			super.actionButton(queryValue);
		}		
	}

	@Override
	public void addActionListener(ActionListener listener) {
		super.addActionListener(listener);
		getM_button().addActionListener(listener);
	}
	
	public int getAttributeSetInstanceID() {
		return attributeSetInstanceID;
	}

	public void setCustom(boolean isCustom) {
		this.isCustom = isCustom;
	}

	public boolean isCustom() {
		return isCustom;
	}
	
}
