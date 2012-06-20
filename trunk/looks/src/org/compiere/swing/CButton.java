/*
 * @(#)CButton.java   12.oct 2007  Versión 2.2
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
import javax.swing.JButton;

/**
 *  OpenXpertya Button supporting colored Background
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CButton.java,v 1.14 2005/03/11 20:34:38 jjanke Exp $
 */
public class CButton extends JButton implements CEditor {

    /** Mandatory (default false) */
    private boolean	m_mandatory	= false;

    /** Read-Write */
    private boolean	m_readWrite	= true;

    /**
     * Creates a button with no set text or icon.
     */
    public CButton() {
        this(null, null);
    }

    /**
     * Creates a button where properties are taken from the
     * <code>Action</code> supplied.
     *
     * @param a the <code>Action</code> used to specify the new button
     *
     * @since 1.3
     */
    public CButton(Action a) {

        super(a);
        setContentAreaFilled(false);
        setOpaque(false);
    }

    /**
     * Creates a button with an icon.
     *
     * @param icon  the Icon image to display on the button
     */
    public CButton(Icon icon) {
        this(null, icon);
    }

    /**
     * Creates a button with text.
     *
     * @param text  the text of the button
     */
    public CButton(String text) {
        this(text, null);
    }

    /**
     * Creates a button with initial text and an icon.
     *
     * @param text  the text of the button
     * @param icon  the Icon image to display on the button
     */
    public CButton(String text, Icon icon) {

        super(text, icon);
        setContentAreaFilled(false);
        setOpaque(false);

        //
        setFont(CompierePLAF.getFont_Label());
        setForeground(CompierePLAF.getTextColor_Label());

    }		// CButton

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
        return m_readWrite;
    }		// isReadWrite

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Background - NOP
     *  @param error error
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
     *  @param bg background color
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

        this.repaint();

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
        this.repaint();

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

        m_readWrite	= rw;

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
}	// CButton



/*
 * @(#)CButton.java   02.jul 2007
 * 
 *  Fin del fichero CButton.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
