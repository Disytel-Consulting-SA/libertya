package org.openXpertya.JasperReport.DataSource;

import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ElementListDataSource implements OXPJasperDataSource {

	/** Lista de elementos */
	private List<Object> elements;
	
	/** Elemento actual */
	private Object currentElement;
	
	/** Índice del elemento actual */
	private int currentIndex;
	
	public ElementListDataSource(List<Object> elements){
		setElements(elements);
		setCurrentIndex(0);
	}
	
	@Override
	public Object getFieldValue(JRField arg0) throws JRException {
		return getCurrentElement();
	}

	@Override
	public boolean next() throws JRException {
		boolean hasNext = getElements().size() > getCurrentIndex();
		
		if(hasNext){
			setCurrentElement(getElements().get(getCurrentIndex()));
			setCurrentIndex(getCurrentIndex()+1);
		}
		
		return hasNext;
	}

	public List<Object> getElements() {
		return elements;
	}

	public void setElements(List<Object> elements) {
		this.elements = elements;
	}

	protected Object getCurrentElement() {
		return currentElement;
	}

	protected void setCurrentElement(Object currentElement) {
		this.currentElement = currentElement;
	}

	protected int getCurrentIndex() {
		return currentIndex;
	}

	protected void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	@Override
	public void loadData() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
