package org.openXpertya.process;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * DocAction status event
 */
public class DocActionStatusEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/** DocActionStatus: Completing the document */
	public static final int ST_COMPLETE_DOCUMENT = 1;
	/** DocActionStatus: Reverse & Correcting document  */
	public static final int ST_REVERSE_CORRECT_DOCUMENT = 2;
	/** DocActionStatus: Printing the document with fiscal printer */
	public static final int ST_FISCAL_PRINT_DOCUMENT = 3;
	
	/** DocAction Status */
	private int docActionStatus;
	/** Additional event's parameters */
	private List<Object> parameters = new ArrayList<Object>();
	
	public DocActionStatusEvent(Object source, int docActionStatus) {
		super(source);
		this.docActionStatus = docActionStatus;
	}
	
	public DocActionStatusEvent(Object source, int docActionStatus, Object[] parameters) {
		this(source, docActionStatus);
		for (int i = 0; i < parameters.length; i++) {
			getParameters().add(parameters[i]);
		}
	}

	/**
	 * @return Returns the docActionStatus.
	 */
	public int getDocActionStatus() {
		return docActionStatus;
	}

	/**
	 * @param docActionStatus The docActionStatus to set.
	 */
	public void setDocActionStatus(int docActionStatus) {
		this.docActionStatus = docActionStatus;
	}

	/**
	 * @return Returns the parameters.
	 */
	public List<Object> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters The parameters to set.
	 */
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * Adds an event's parameter. 
	 * @param index Parameter index number.
	 * @param parameter Object to add as parameter.
	 */
	public void addParameter(int index, Object parameter) {
		getParameters().add(index, parameter);
	}
	
	/**
	 * Returns an event's parameter.
	 * @param index Parameter index number.
	 * @return the parameter's object.
	 */
	public Object getParameter(int index) {
		return getParameters().get(index);
	}
}
