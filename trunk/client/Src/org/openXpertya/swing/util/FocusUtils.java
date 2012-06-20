package org.openXpertya.swing.util;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.compiere.plaf.CompierePLAF;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;

public class FocusUtils {

	private static final Color BORDER_COLOR_1 = new Color(195, 0, 0);
	private static final Color BORDER_COLOR_2 = new Color(255, 158, 158);
	private static final Color BORDER_COLOR_COMBO = new Color(212, 0, 0);
	private static FocusUtils instance = new FocusUtils();
	
	private FocusUtils() {
		
	}
	
	/**
	 * @return La instancia única de esta clase.
	 */
	private static FocusUtils getInstance() {
		return instance;
	}

	/**
	 * Agrega un listener de foco a fin de resaltar el borde del componente
	 * cuando el mismo toma el foco
	 * 
	 * @param textField
	 *            Componente de campo de texto
	 */
	public static void addFocusHighlight(JTextField textField) {
		textField.addFocusListener(getInstance().createFocusListener(textField));
	}
	
	/**
	 * Agrega un listener de foco a fin de resaltar el borde del componente
	 * cuando el mismo toma el foco
	 * 
	 * @param lookup
	 *            Componente de Lookup
	 */
	public static void addFocusHighlight(VLookup lookup) {
		if (lookup.isComboActive()) {
			addFocusHighlight(lookup.getComboBox());
		} else {
			lookup.getM_text().addFocusListener(
				getInstance().createFocusListener(lookup));
		}
	}
	
	/**
	 * Agrega un listener de foco a fin de resaltar el borde del componente
	 * cuando el mismo toma el foco
	 * 
	 * @param button
	 *            Componente de Botón
	 */
	public static void addFocusHighlight(JButton button) {
		Border highlightBorder = getButtonHighlightBorder(button);
		if (highlightBorder != null) {
			button.addFocusListener(getInstance().createFocusListener(button,
					highlightBorder));
			button.setFocusPainted(false);
		} else {
			button.setFocusPainted(true);
		}
	}
	
	/**
	 * Agrega un listener de foco a fin de resaltar el borde del componente
	 * cuando el mismo toma el foco
	 * 
	 * @param vNumber
	 *            Componente de campo numérico
	 */
	public static void addFocusHighlight(VNumber vNumber) {
		vNumber.getTextField().addFocusListener(
				getInstance().createFocusListener(vNumber));
	}
	
	/**
	 * Agrega un listener de foco a fin de resaltar el borde del componente
	 * cuando el mismo toma el foco
	 * 
	 * @param comboBox
	 *            Componente de Combo
	 */
	public static void addFocusHighlight(JComboBox comboBox) {
		comboBox.addFocusListener(getInstance().createFocusListener(comboBox,
				getComboHighlightBorder()));
	}
	
	/**
	 * Agrega un listener de foco a fin de resaltar el borde del componente
	 * cuando el mismo toma el foco
	 * 
	 * @param vDate
	 *            Componente de campo numérico
	 */
	public static void addFocusHighlight(VDate vDate) {
		vDate.getTextField().addFocusListener(
				getInstance().createFocusListener(vDate));
	}
	
	/**
	 * Agrega un listener de foco a fin de resaltar el borde del componente
	 * cuando el mismo toma el foco
	 * 
	 * @param component
	 *            Componente
	 */
	public static void addFocusHighlight(JComponent component) {
		component.addFocusListener(getInstance().createFocusListener(component,
				getStandardHighlightBorder()));
	}
	
	/**
	 * Agrega un listener de foco a fin de resaltar el borde del componente
	 * cuando el mismo toma el foco
	 * 
	 * @param component
	 *            Componente
	 */
	public static void addFocusHighlight(JRadioButton radioButton) {
		radioButton.setBorder(null);
		radioButton.addFocusListener(getInstance().createFocusListener(radioButton,
				getStandardHighlightBorder()));
		radioButton.setBorderPainted(true);
		radioButton.setFocusPainted(false);
	}

	/**
	 * Crea el listener de foco que se encarga de modificar la apariencia del
	 * componente que toma o deja el foco
	 * 
	 * @param highlightComponent
	 * @return
	 */
	private FocusListener createFocusListener(JComponent highlightComponent) {
		return createFocusListener(highlightComponent,
				getStandardHighlightBorder());
	}

	/**
	 * Crea el listener de foco que se encarga de modificar la apariencia del
	 * componente que toma o deja el foco
	 * 
	 * @param highlightComponent
	 * @param border
	 * @return
	 */
	private FocusListener createFocusListener(JComponent highlightComponent, Border border) {
		return new HighlightBorderFocusListener(
				highlightComponent, border);
	}

	/**
	 * @return Borde que se asigna cuando un componente toma el foco.
	 */
	private static Border getStandardHighlightBorder() {
		return BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR_1, 1),
				BorderFactory.createLineBorder(BORDER_COLOR_2, 1)
		);
		
	}
	
	/**
	 * @return Borde que se asigna cuando un boton toma el foco.
	 */
	private static Border getButtonHighlightBorder(JButton button) {
		Border sourceBorder = null;
		if (button.getBorder() instanceof CompoundBorder) {
			CompoundBorder cBorder = (CompoundBorder)button.getBorder();
			sourceBorder = cBorder.getInsideBorder();
		}
		if (sourceBorder != null) {
			return BorderFactory.createCompoundBorder(
				getStandardHighlightBorder(),
				sourceBorder
			);
		} else {
			return null;
		}
	}
	
	/**
	 * @return Borde que se asigna cuando un combo toma el foco.
	 */
	private static Border getComboHighlightBorder() {
		return BorderFactory.createLineBorder(new Color(212, 0, 0), 1);
	}

	/**
	 * Agrega un border al componente cuando toma el foco. Asigna el borde
	 * original cuando el componente pierde el foco.
	 */
	private class HighlightBorderFocusListener implements FocusListener {

		private Border originalBorder = null;
		private Border highlightedBorder = null;
		private JComponent component = null;
		
		/**
		 * Constructor por defecto.
		 * @param component
		 */
		public HighlightBorderFocusListener(JComponent component, Border highlightedBorder) {
			super();
			this.component = component;
			this.highlightedBorder = highlightedBorder;
			this.originalBorder = component.getBorder();
		}

		@Override
		public void focusGained(FocusEvent e) {
			getComponent().setBorder(highlightedBorder);
			
		}

		@Override
		public void focusLost(FocusEvent e) {
			getComponent().setBorder(originalBorder);
		}

		/**
		 * @return El componente al cual se le cambia el borde al tomar o perder
		 *         el foco.
		 */
		public JComponent getComponent() {
			return component;
		}
		
	}
	 
	
}
