package org.openXpertya.pos.view;

import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeyUtils {

	/**
	 * Asigna las teclas por defecto para un botón Aceptar.
	 * @param button
	 */
	public static void setOkButtonKeys(JButton button) {
		setDefaultKey(button);
		setButtonKey(button, false, KeyEvent.VK_ENTER);
		setButtonKey(button, true, KeyEvent.VK_F12);
		setButtonKey(button, true, KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK);
	}
	
	/**
	 * Asigna las teclas por defecto para un botón Eliminar.
	 * @param button
	 */
	public static void setRemoveButtonKeys(JButton button) {
		setDefaultKey(button);
		setButtonKey(button, false, KeyEvent.VK_ENTER);
		setButtonKey(button, true, KeyEvent.VK_F10);
	}
	
	/**
	 * Asigna las teclas por defecto para un botón Cancelar.
	 * @param button
	 */
	public static void setCancelButtonKeys(JButton button) {
		setDefaultKey(button);
		setButtonKey(button, false, KeyEvent.VK_ENTER);
		setButtonKey(button, true, KeyEvent.VK_ESCAPE);
	}

	/**
	 * Asigna una tecla a la acción de un botón
	 * 
	 * @param button
	 *            Botón
	 * @param global
	 *            <code>true</code> si la tecla se captura cuando la ventana que
	 *            contiene el botón tiene el foco, <code>false</code> para que
	 *            el botón deba tener el foco cuando se presiona la tecla.
	 * @param keyCode
	 *            Código de tecla
	 * @param modifiers
	 *            Modificadores de tecla.
	 */
	public static void setButtonKey(JButton button, boolean global, int keyCode, int modifiers) {
		KeyStroke pressed = KeyStroke.getKeyStroke(keyCode, modifiers);
		KeyStroke released = KeyStroke.getKeyStroke(keyCode, modifiers, true);
		int condition = global ? JComponent.WHEN_IN_FOCUSED_WINDOW
				: JComponent.WHEN_FOCUSED;
		button.getInputMap(condition).put(pressed, "pressed");
		button.getInputMap(condition).put(released, "released");
	}
	
	/**
	 * Asigna una tecla a la acción de un botón
	 * 
	 * @param button
	 *            Botón
	 * @param global
	 *            <code>true</code> si la tecla se captura cuando la ventana que
	 *            contiene el botón tiene el foco, <code>false</code> para que
	 *            el botón deba tener el foco cuando se presiona la tecla.
	 * @param keyCode
	 *            Código de tecla
	 */
	public static void setButtonKey(JButton button, boolean global, int keyCode) {
		setButtonKey(button, global, keyCode, 0);
	}
	
	/**
	 * Asigna la tecla de activación por defecto para un botón.
	 * @param button
	 */
	public static void setDefaultKey(JButton button) {
		setButtonKey(button, false, KeyEvent.VK_ENTER);	
	}

	/**
	 * Asigna el label de un botón indicando la tecla de acceso rápido si la
	 * tuviese
	 * 
	 * @param button
	 * @param text
	 */
	public static void setButtonText(JButton button, String text) {
		if (text == null) {
			text = "";
		}
		
		KeyStroke[] keys = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).keys();
		String keyStr = "";
		if (keys.length > 0) {
			keyStr = " " + getKeyStr(keys[0]);
		}
		
		button.setText(text + keyStr);
	}
	
	/**
	 * Devuelve la representación en String del KeyStroke.
	 * @param keyStroke
	 * @return
	 */
	public static String getKeyStr(KeyStroke keyStroke) {
		StringBuffer str = new StringBuffer("(");
		int modifiers = keyStroke.getModifiers();
		int keyCode = keyStroke.getKeyCode();
		if (modifiers > 0) {
			str.append(KeyEvent.getKeyModifiersText(modifiers)).append("+");
		}
		String keyCodeStr = KeyEvent.getKeyText(keyStroke.getKeyCode());
		if (keyCode == KeyEvent.VK_ESCAPE) {
			keyCodeStr = "ESC";
		}
		return str.append(keyCodeStr).append(")").toString();
	}
}
