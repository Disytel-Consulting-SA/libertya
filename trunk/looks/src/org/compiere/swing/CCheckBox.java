/*
 * @(#)CCheckBox.java   12.oct 2007  Versión 2.2
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

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

/**
 *  OpenXpertya CheckBox
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CCheckBox.java,v 1.8 2005/03/11 20:34:38 jjanke Exp $
 */
public class CCheckBox extends JCheckBox implements CEditor {

    /** ********************************************************************* */

    /** Mandatory (default false) */
    private boolean	m_mandatory	= false;

    /** Read-Write */
    private boolean	m_readWrite	= true;

    /** Retain value */
    private Object	m_value	= null;

    /**
     * Creates an initially unselected check box button with no text, no icon.
     */
    public CCheckBox() {

        super();
        init();
    }

    /**
     * Creates a check box where properties are taken from the
     * Action supplied.
     *  @param a
     */
    public CCheckBox(Action a) {

        super(a);
        init();
    }

    /**
     * Creates an initially unselected check box with an icon.
     *
     * @param icon  the Icon image to display
     */
    public CCheckBox(Icon icon) {

        super(icon);
        init();
    }

    /**
     * Creates an initially unselected check box with text.
     *
     * @param text the text of the check box.
     */
    public CCheckBox(String text) {

        super(text);
        init();
    }

    /**
     * Creates a check box with an icon and specifies whether
     * or not it is initially selected.
     *
     * @param icon  the Icon image to display
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the check box is selected
     */
    public CCheckBox(Icon icon, boolean selected) {

        super(icon, selected);
        init();
    }

    /**
     * Creates a check box with text and specifies whether
     * or not it is initially selected.
     *
     * @param text the text of the check box.
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the check box is selected
     */
    public CCheckBox(String text, boolean selected) {

        super(text, selected);
        init();
    }

    /**
     * Creates an initially unselected check box with
     * the specified text and icon.
     *
     * @param text the text of the check box.
     * @param icon  the Icon image to display
     */
    public CCheckBox(String text, Icon icon) {

        super(text, icon, false);
        init();
    }

    /**
     * Creates a check box with text and icon,
     * and specifies whether or not it is initially selected.
     *
     * @param text the text of the check box.
     * @param icon  the Icon image to display
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the check box is selected
     */
    public CCheckBox(String text, Icon icon, boolean selected) {

        super(text, icon, selected);
        init();
    }

    /**
     *  Common Init
     */
    private void init() {

        setFont(CompierePLAF.getFont_Label());
        setForeground(CompierePLAF.getTextColor_Label());

    }		// init

    //~--- get methods --------------------------------------------------------

    /**
     *  Return Display Value
     *  @return displayed String value
     */
    public String getDisplay() {

        if (m_value instanceof String) {

            return super.isSelected()
                   ? "Y"
                   : "N";
        }

        return Boolean.toString(super.isSelected());

    }		// getDisplay

    /**
     *      Return Editor value
     *  @return current value as String or Boolean
     */
    public Object getValue() {

        if (m_value instanceof String) {

            return super.isSelected()
                   ? "Y"
                   : "N";
        }

        return new Boolean(isSelected());

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
        return m_readWrite;
    }		// isEditable

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Background based on editable/mandatory/error - ignored -
     *  @param error if true, set background to error color, otherwise mandatory/editable
     */
    public void setBackground(boolean error) {}		// setBackground

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
        m_readWrite	= rw;

    }		// setEditable

    /**
     *      Set Editor to value. Interpret Y/N and Boolean
     *  @param value value of the editor
     */
    public void setValue(Object value) {

        m_value	= value;

        boolean	sel	= false;

        if (value == null) {
            sel	= false;
        } else if (value.toString().equals("Y")) {
            sel	= true;
        } else if (value.toString().equals("N")) {
            sel	= false;
        } else if (value instanceof Boolean) {
            sel	= ((Boolean) value).booleanValue();
        } else {

            try {
                sel	= Boolean.getBoolean(value.toString());
            } catch (Exception e) {}
        }

        this.setSelected(sel);

    }		// setValue
}	// CCheckBox



/*
 * @(#)CCheckBox.java   02.jul 2007
 * 
 *  Fin del fichero CCheckBox.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
