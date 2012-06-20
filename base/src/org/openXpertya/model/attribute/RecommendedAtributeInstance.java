package org.openXpertya.model.attribute;

import java.math.BigDecimal;

public class RecommendedAtributeInstance {

	public RecommendedAtributeInstance() {
		
	}

	public RecommendedAtributeInstance(int m_AtributeInstance_ID, BigDecimal qtyOnHand, Integer M_Locator_ID) {
		setM_AtributeInstance_ID(m_AtributeInstance_ID);
		setQtyOnHand(qtyOnHand);
		setM_Locator_ID(M_Locator_ID);
	}

	/**
	 * @return the m_AtributeInstance_ID
	 */
	public int getM_AtributeInstance_ID() {
		return M_AtributeInstance_ID;
	}
	/**
	 * @param atributeInstance_ID the m_AtributeInstance_ID to set
	 */
	public void setM_AtributeInstance_ID(int atributeInstance_ID) {
		M_AtributeInstance_ID = atributeInstance_ID;
	}
	/**
	 * @return the qtyOnHand
	 */
	public BigDecimal getQtyOnHand() {
		return QtyOnHand;
	}
	/**
	 * @param qtyOnHand the qtyOnHand to set
	 */
	public void setQtyOnHand(BigDecimal qtyOnHand) {
		QtyOnHand = qtyOnHand;
	}

	/**
	 * @return the m_Locator_ID
	 */
	public Integer getM_Locator_ID() {
		return M_Locator_ID;
	}

	/**
	 * @param locator_ID the m_Locator_ID to set
	 */
	public void setM_Locator_ID(Integer locator_ID) {
		M_Locator_ID = locator_ID;
	}
	
	private int M_AtributeInstance_ID = 0;
	private BigDecimal QtyOnHand = BigDecimal.ZERO;
	private Integer M_Locator_ID = null;
	
}
