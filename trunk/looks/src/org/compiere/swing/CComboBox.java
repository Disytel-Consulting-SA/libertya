/*
 * @(#)CComboBox.java   12.oct 2007  Versión 2.2
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

import org.compiere.plaf.CompiereComboBoxUI;
import org.compiere.plaf.CompierePLAF;

import org.openXpertya.util.Trace;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Color;
import java.awt.event.MouseListener;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.ComboBoxUI;

/**
 *  OpenXpertya Colored Combo Box.
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CComboBox.java,v 1.19 2005/03/11 20:34:38 jjanke Exp $
 */
public class CComboBox extends JComboBox implements CEditor {

    /** Field Height */
    public static int	FIELD_HIGHT	= 0;

    /** ********************************************************************* */

    /** Icon */
    private Icon	m_icon	= null;

    /** ********************************************************************* */

    /** Mandatory (default false) */
    private boolean	m_mandatory	= false;

    /**
     * Creates a <code>JComboBox</code> with a default data model.
     * The default data model is an empty list of objects.
     * Use <code>addItem</code> to add items.  By default the first item
     * in the data model becomes selected.
     *
     * @see DefaultComboBoxModel
     */
    public CComboBox() {

        super();
        init();

    }		// CComboBox

    /**
     * Creates a <code>JComboBox</code> that takes it's items from an
     * existing <code>ComboBoxModel</code>.  Since the
     * <code>ComboBoxModel</code> is provided, a combo box created using
     * this constructor does not create a default combo box model and
     * may impact how the insert, remove and add methods behave.
     *
     * @param aModel the <code>ComboBoxModel</code> that provides the
     *              displayed list of items
     * @see DefaultComboBoxModel
     */
    public CComboBox(ComboBoxModel aModel) {

        super(aModel);
        init();

    }		// CComboBox

    /**
     * Creates a <code>JComboBox</code> that contains the elements
     * in the specified array.  By default the first item in the array
     * (and therefore the data model) becomes selected.
     *
     * @param items  an array of objects to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public CComboBox(final Object items[]) {

        super(items);
        init();

    }		// CComboBox

    /**
     * Creates a <code>JComboBox</code> that contains the elements
     * in the specified Vector.  By default the first item in the vector
     * and therefore the data model) becomes selected.
     *
     * @param items  an array of vectors to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public CComboBox(Vector items) {

        super(items);
        init();

    }		// CComboBox

    /**
     *  Add Mouse Listener - 1-4-0 Bug.
     *  Bug in 1.4.0 Metal: arrowButton gets Mouse Events, so add the JComboBox
     *  MouseListeners to the arrowButton - No context menu if right-click
     *  @see also CompiereComboBoxUI#installUI()
     *  @param ml
     */
    public void addMouseListener(MouseListener ml) {

        super.addMouseListener(ml);

        // ignore calls from javax.swing.plaf.basic.BasicComboBoxUI.installListeners(BasicComboBoxUI.java:271)
        if ((getUI() instanceof CompiereComboBoxUI) &&!Trace.getCallerClass(1).startsWith("javax")) {

            JButton	b	= ((CompiereComboBoxUI) getUI()).getArrowButton();

            if (b != null) {
                b.addMouseListener(ml);
            }
        }

    }		// addMouseListener

    /**
     *  Display Popup.
     *  Called from CompiereComboPopup and allows to implement
     *  alternative actions than showing the popup
     *  @return if true, the popup should be displayed
     */
    public boolean displayPopup() {
        return true;
    }		// displayPopup

    /**
     *  Common Init
     */
    private void init() {

        // overwrite - otherwise Label Font
        setFont(CompierePLAF.getFont_Field());
        setForeground(CompierePLAF.getTextColor_Normal());
        setBackground(false);
        FIELD_HIGHT	= getPreferredSize().height;
    }		// init

    /**
     *  Remove Mouse Listener.
     *  @param ml
     */
    public void removeMouseListener(MouseListener ml) {

        super.removeMouseListener(ml);

        if (getUI() instanceof CompiereComboBoxUI) {

            JButton	b	= ((CompiereComboBoxUI) getUI()).getArrowButton();

            if (b != null) {
                b.removeMouseListener(ml);
            }
        }

    }		// removeMouseListener

    //~--- get methods --------------------------------------------------------

    /**
     *  Return Display Value
     *  @return displayed String value
     */
    public String getDisplay() {

        Object	o	= super.getSelectedItem();

        if (o == null) {
            return "";
        }

        return o.toString();

    }		// getDisplay

    /**
     *      Return Editor value
     *  @return current value
     */
    public Object getValue() {
        return super.getSelectedItem();
    }		// getValue

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

        if (error) {
            setBackground(CompierePLAF.getFieldBackground_Error());
        } else if (!isReadWrite()) {
            setBackground(CompierePLAF.getFieldBackground_Inactive());
        } else if (m_mandatory) {
            setBackground(CompierePLAF.getFieldBackground_Mandatory());
        } else {
            setBackground(CompierePLAF.getFieldBackground_Normal());
        }

    }		// setBackground

    /**
     *  Set Background
     *  @param bg
     */
    public void setBackground(Color bg) {

        if (bg.equals(getBackground())) {
            return;
        }

        super.setBackground(bg);

    }		// setBackground

    /**
     *  Set Icon of arrow button to icon
     *  @param defaultIcon Icon to be displayed
     */
    public void setIcon(Icon defaultIcon) {

        if (getUI() instanceof CompiereComboBoxUI) {
            ((CompiereComboBoxUI) getUI()).setIcon(defaultIcon);
        }

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
    public void setUI(ComboBoxUI ui) {

        super.setUI(ui);

        if ((m_icon != null) && (ui instanceof CompiereComboBoxUI)) {
            ((CompiereComboBoxUI) getUI()).setIcon(m_icon);
        }

    }		// setUI

    /**
     *      Set Editor to value
     *  @param value value of the editor
     */
    public void setValue(Object value) {
        super.setSelectedItem(value);
    }		// setValue
}	// CComboBox



/*
 * @(#)CComboBox.java   02.jul 2007
 * 
 *  Fin del fichero CComboBox.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
