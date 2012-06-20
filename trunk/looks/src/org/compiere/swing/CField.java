/*
 * @(#)CField.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.compiere.swing;

import org.compiere.plaf.CompierePLAF;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.reflect.Constructor;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;

/**
 *  OpenXpertya Colored Field with external popup editor.
 *  It extends ComboBox for UI consistency purposes
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CField.java,v 1.8 2005/03/11 20:34:38 jjanke Exp $
 */
public class CField extends JComboBox implements CEditor, ActionListener {

    /** Descripción de Campo */
    private CFieldEditor	m_editor	= null;

    /** Descripción de Campo */
    private Class	m_popupClass	= null;

    /** Descripción de Campo */
    private String	m_title	= null;

    /** Descripción de Campo */
    private Object	m_oldValue	= null;

    /** ********************************************************************* */

    /** Mandatory (default false) */
    private boolean	m_mandatory	= false;

    /** ********************************************************************* */

    /** Icon */
    private Icon	m_icon	= null;

    /** ********************************************************************* */
    public CField() {
        this(null, null, "");
    }

    /**
     *  Construct OpenXpertya Field with external popup editor
     *
     *  @param editor   the (validating) editor
     *  @param cFieldPopup  the popup dialog
     *  @param title    title for popup
     */
    public CField(CFieldEditor editor, Class cFieldPopup, String title) {

        super(new Object[] { "1", "2" });

        if (editor != null) {

//          setEditor (editor);
            setEditable(true);
        }

        m_title	= title;

        // Check popup
        if (cFieldPopup != null) {

            Class[]	interfaces	= cFieldPopup.getInterfaces();
            boolean	found		= false;

            for (int i = 0; i < interfaces.length; i++) {

                if (interfaces[i].equals(CFieldPopup.class)) {

                    found	= true;

                    break;
                }
            }

            if (!found) {
                throw new IllegalArgumentException("CField - Popup class must be CFieldPopup");
            }
        }

        super.addActionListener(this);

    }		// CField

    /**
     * **********************************************************************
     *
     * @param e
     */

    /**
     *  Action Listener
     *  @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {

        // Do er have a change?
        Object	newValue	= getValue();

        if (((newValue != null) && newValue.equals(m_oldValue)) || ((newValue == null) && (m_oldValue == null))) {
            return;
        }

        super.firePropertyChange("DataChanged", m_oldValue, newValue);
        m_oldValue	= newValue;
    }		// //  actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static CField createDate() {
        return createDate(new SimpleDateFormat());
    }

    /**
     * Descripción de Método
     *
     *
     * @param format
     *
     * @return
     */
    public static CField createDate(DateFormat format) {
        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static CField createNumeric() {
        return createNumeric(new DecimalFormat());
    }

    /**
     * Descripción de Método
     *
     *
     * @param format
     *
     * @return
     */
    public static CField createNumeric(NumberFormat format) {
        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param length
     *
     * @return
     */
    public static CField createText(int length) {
        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param p
     *
     * @return
     */
    public static CField createText(Pattern p) {
        return null;
    }

    /**
     *  Display Popup.
     *  Called from CompiereComboPopup and allows to implement
     *  alternative actions than showing the popup
     *  @return if true, the popup should be displayed
     */
    public boolean displayPopup() {

        if (m_popupClass == null) {
            return false;
        }

        //
        try {

            // Get Owner & Create Popup Instance
            Window	win	= SwingUtilities.getWindowAncestor(this);
            CFieldPopup	popup	= null;

            if (win instanceof Dialog) {

                Constructor	constructor	= m_popupClass.getConstructor(new Class[] { Dialog.class, String.class, Boolean.class });

                popup	= (CFieldPopup) constructor.newInstance(new Object[] { (Dialog) win, m_title, new Boolean(true) });

            } else if (win instanceof Frame) {

                Constructor	constructor	= m_popupClass.getConstructor(new Class[] { Frame.class, String.class, Boolean.class });

                popup	= (CFieldPopup) constructor.newInstance(new Object[] { (Frame) win, m_title, new Boolean(true) });
            }

            if (popup == null) {
                return false;
            }

            // Start Popup
            popup.setValue(m_editor.getItem());
            popup.setFormat(m_editor.getFormat());
            popup.show();
            m_editor.setItem(popup.getValue());
            popup	= null;
        } catch (Exception e) {
            notifyUser(e);
        }

        //
        return false;

    }		// displayPopup

    /**
     *  Notify User of a Ptoblem with starting popup
     *  @param e Exception
     */
    public void notifyUser(Exception e) {
        JOptionPane.showMessageDialog(this, e.toString(), "Field Error", JOptionPane.ERROR_MESSAGE);
    }		// notify User

    //~--- get methods --------------------------------------------------------

    /**
     *  Return Display Value
     *  @return displayed String value
     */
    public String getDisplay() {

        // if (super.getSelectedItem() == null)
        return "";

        // return super.getSelectedItem().toString();
    }		// getDisplay

    /**
     *  Get Icon of arrow button to icon
     *  @return defaultIcon Icon to be displayed
     */
    public Icon getIcon() {
        return m_icon;
    }		// getIcon

    /**
     *      Return Editor value
     *  @return current value
     */
    public Object getValue() {
        return null;	// super.getSelectedItem();
    }			// getValue

    /**
     *      Is Field mandatory
     *  @return true, if mandatory
     */
    public boolean isMandatory() {
        return m_mandatory;
    }		// isMandatory

    /**
     *      Is it possible to edit
     *  @return true, if editable
     */
    public boolean isReadWrite() {
        return super.isEnabled();
    }		// isReadWrite

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Background based on editable / mandatory / error
     *  @param error if true, set background to error color, otherwise mandatory/editable
     */
    public void setBackground(boolean error) {

        Color	bg	= null;

        if (error) {
            bg	= CompierePLAF.getFieldBackground_Error();
        } else if (!isReadWrite()) {
            bg	= CompierePLAF.getFieldBackground_Inactive();
        } else if (m_mandatory) {
            bg	= CompierePLAF.getFieldBackground_Mandatory();
        } else {
            bg	= CompierePLAF.getFieldBackground_Normal();
        }

        if (bg.equals(m_editor.getBackground())) {
            return;
        }

        m_editor.setBackground(bg);

    }		// setBackground

    /**
     *  Set Icon of arrow button to icon
     *  @param defaultIcon Icon to be displayed
     */
    public void setIcon(Icon defaultIcon) {
        m_icon	= defaultIcon;
    }		// setIcon

    /**
     *      Set Editor Mandatory
     *  @param mandatory true, if you have to enter data
     */
    public void setMandatory(boolean mandatory) {

        m_mandatory	= mandatory;
        setBackground(false);

    }		// setMandatory

    /**
     *      Enable Editor
     *  @param rw true, if you can enter/select data
     */
    public void setReadWrite(boolean rw) {

        if (super.isEnabled() != rw) {
            super.setEnabled(rw);
        }

        setBackground(false);

    }		// setReadWrite

    /**
     *  Set UI and re-set Icon for arrow button
     *  @param ui
     */
    public void setUI(TextUI ui) {
        super.setUI(ui);
    }		// setUI

    /**
     *      Set Editor to value
     *  @param value value of the editor
     */
    public void setValue(Object value) {

        m_oldValue	= value;

        // super.setSelectedItem(value);

    }		// setValue
}	// CField



/*
 * @(#)CField.java   02.jul 2007
 * 
 *  Fin del fichero CField.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
