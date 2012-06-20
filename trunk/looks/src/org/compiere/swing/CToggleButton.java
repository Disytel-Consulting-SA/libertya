/*
 * @(#)CToggleButton.java   12.oct 2007  Versión 2.2
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

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;

import org.openXpertya.util.Trace;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Color;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 *  OpenXpertya Color Taggle Button
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CToggleButton.java,v 1.8 2005/03/11 20:34:38 jjanke Exp $
 */
public class CToggleButton extends JToggleButton implements CEditor {

    /** Mandatory (default false) */
    private boolean	m_mandatory	= false;

    /**
     * Creates an initially unselected toggle button
     * without setting the text or image.
     */
    public CToggleButton() {
        this(null, null, false);
    }

    /**
     * Creates a toggle button where properties are taken from the
     * Action supplied.
     *
     * @param a
     */
    public CToggleButton(Action a) {

        this();
        setAction(a);
    }

    /**
     * Creates an initially unselected toggle button
     * with the specified image but no text.
     *
     * @param icon  the image that the button should display
     */
    public CToggleButton(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates an unselected toggle button with the specified text.
     *
     * @param text  the string displayed on the toggle button
     */
    public CToggleButton(String text) {
        this(text, null, false);
    }

    /**
     * Creates a toggle button with the specified image
     * and selection state, but no text.
     *
     * @param icon  the image that the button should display
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public CToggleButton(Icon icon, boolean selected) {
        this(null, icon, selected);
    }

    /**
     * Creates a toggle button with the specified text
     * and selection state.
     *
     * @param text  the string displayed on the toggle button
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public CToggleButton(String text, boolean selected) {
        this(text, null, selected);
    }

    /**
     * Creates a toggle button that has the specified text and image,
     * and that is initially unselected.
     *
     * @param text the string displayed on the button
     * @param icon  the image that the button should display
     */
    public CToggleButton(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a toggle button with the specified text, image, and
     * selection state.
     *
     * @param text the text of the toggle button
     * @param icon  the image that the button should display
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public CToggleButton(String text, Icon icon, boolean selected) {

        super(text, icon, selected);
        setContentAreaFilled(false);
        setOpaque(false);

        //
        setFont(CompierePLAF.getFont_Label());
        setForeground(CompierePLAF.getTextColor_Label());
    }

    //~--- get methods --------------------------------------------------------

    /**
     *  Get Background
     *  @return Color for Background
     */
    public CompiereColor getBackgroundColor() {

        try {
            return (CompiereColor) getClientProperty(CompierePLAF.BACKGROUND);
        } catch (Exception e) {
            System.err.println("CButton - ClientProperty: " + e.getMessage());
        }

        return null;

    }		// getBackgroundColor

    /**
     *  Return Display Value
     *  @return displayed String value
     */
    public String getDisplay() {
        return getText();
    }		// getDisplay

    /**
     *      Return Editor value
     *  @return current value
     */
    public Object getValue() {
        return getText();
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
     *  Set Background - NOP
     *  @param error
     */
    public void setBackground(boolean error) {}		// setBackground

    /**
     * **********************************************************************
     *
     * @param bg
     */

    /**
     *  Set Background - Differentiates between system & user call.
     *  If User Call, sets Opaque & ContextAreaFilled to true
     *  @param bg
     */
    public void setBackground(Color bg) {

        if (bg.equals(getBackground())) {
            return;
        }

        super.setBackground(bg);

        // ignore calls from javax.swing.LookAndFeel.installColors(LookAndFeel.java:61)
        if (!Trace.getCallerClass(1).startsWith("javax")) {

            setOpaque(true);
            setContentAreaFilled(true);
        }

    }		// setBackground

    /**
     *  Set Standard Background
     */
    public void setBackgroundColor() {
        setBackgroundColor(null);
    }		// setBackground

    /**
     *  Set Background
     *  @param bg CompiereColor for Background, if null set standard background
     */
    public void setBackgroundColor(CompiereColor bg) {

        if (bg == null) {
            bg	= CompiereColor.getDefaultBackground();
        }

        setOpaque(true);
        putClientProperty(CompierePLAF.BACKGROUND, bg);
        super.setBackground(bg.getFlatColor());

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

    }		// setReadWrite

    /**
     *      Set Editor to value
     *  @param value value of the editor
     */
    public void setValue(Object value) {

        if (value == null) {
            setText("");
        } else {
            setText(value.toString());
        }

    }		// setValue
}	// CToggleButton



/*
 * @(#)CToggleButton.java   02.jul 2007
 * 
 *  Fin del fichero CToggleButton.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
