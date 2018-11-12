package org.openXpertya.grid.ed;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class VBPartnerQuick extends VBPartner {

	/** Listado de componentes a ocultar */
	private List<JComponent> hideComponents;
	
	public VBPartnerQuick(Frame frame, int WindowNo) {
		super(frame, WindowNo);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void postInit(){
		List<JComponent> hided = new ArrayList<JComponent>();
		hided.add(fGreetingBP);
		hided.add(fGreetingC);
		hided.add(fValue);
		hided.add(fCIF);
		hided.add(fName2);
		hided.add(fPartnerGroup);
		hided.add(fContact);
		hided.add(fTitle);
		hided.add(fEMail);
		hided.add(fPhone);
		hided.add(fPhone2);
		hided.add(fFax);
		
		setHideComponents(hided);
		doHideComponents();    	
    }

	protected void doHideComponents(){
		for (JComponent c : getHideComponents()) {
			c.setVisible(false);
			if(getCompoLabels().get(c) != null){
				getCompoLabels().get(c).setVisible(false);
			}
		}
	}
	
	protected List<JComponent> getHideComponents() {
		return hideComponents;
	}

	protected void setHideComponents(List<JComponent> hideComponents) {
		this.hideComponents = hideComponents;
	}
	
	

}
