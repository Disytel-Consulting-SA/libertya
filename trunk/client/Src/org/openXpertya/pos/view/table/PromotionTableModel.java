package org.openXpertya.pos.view.table;

import java.util.ArrayList;
import java.util.List;

import org.openXpertya.pos.model.Promotion;

public class PromotionTableModel extends AbstractPoSTableModel {

	private List<Promotion> promotions;
	
	public PromotionTableModel() {
		setPromotions(new ArrayList<Promotion>());
	}

	public PromotionTableModel(List<Promotion> promos) {
		setPromotions(promos);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Promotion promo = getPromotions().get(rowIndex);
		switch (columnIndex) {
		case 0:
			return promo.getName();
		case 1:
			return promo.getCode() == null?"":promo.getCode();
		default:
			return null;
		}
	}

	@Override
	public List getObjects() {
		return getPromotions();
	}

	public List<Promotion> getPromotions() {
		return promotions;
	}

	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}

}
