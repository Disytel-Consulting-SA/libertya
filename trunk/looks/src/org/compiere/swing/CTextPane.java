/*
 * @(#)CTextPane.java   12.oct 2007  Versión 2.2
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
import java.awt.event.FocusListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.im.InputMethodRequests;

import javax.swing.InputVerifier;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

/**
 *      OpenXpertya TextPane - A ScrollPane with a JTextPane.
 *  Manages visibility, opaque and color consistently *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: CTextPane.java,v 1.6 2005/05/28 21:03:44 jjanke Exp $
 */
public class CTextPane extends JScrollPane implements CEditor {

    /** Descripción de Campo */
    private JTextPane	m_textPane	= null;

    /** ********************************************************************* */

    /** Mandatory (default false) */
    private boolean	m_mandatory	= false;

    /**
     *      Constructs a new TextPane (HTML)
     */
    public CTextPane() {
        this(new JTextPane());
    }		// CTextPane

    /**
     *  Create a JScrollArea with a JTextEditor
     *  @param textPane
     */
    public CTextPane(JTextPane textPane) {

        super(textPane);
        m_textPane	= textPane;
        super.setOpaque(false);
        super.getViewport().setOpaque(false);
        m_textPane.setContentType("text/html");
        m_textPane.setFont(CompierePLAF.getFont_Field());
        m_textPane.setForeground(CompierePLAF.getTextColor_Normal());

    }		// CTextPane

    /**
     *      Constructs a new JTextPane with the given document
     *      @param doc  the model to use
     */
    public CTextPane(StyledDocument doc) {
        this(new JTextPane(doc));
    }		// CTextPane

    /**
     * Descripción de Método
     *
     *
     * @param l
     */
    public void addFocusListener(FocusListener l) {

        if (m_textPane == null) {	// during init
            super.addFocusListener(l);
        } else {
            m_textPane.addFocusListener(l);
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */
    public void addInputMethodListener(InputMethodListener l) {
        m_textPane.addInputMethodListener(l);
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */
    public void addKeyListener(KeyListener l) {
        m_textPane.addKeyListener(l);
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */
    public void addMouseListener(MouseListener l) {
        m_textPane.addMouseListener(l);
    }

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Background
     *      @return color
     */
    public Color getBackground() {

        if (m_textPane == null) {	// during init
            return super.getBackground();
        } else {
            return m_textPane.getBackground();
        }

    }					// getBackground

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getCaretPosition() {
        return m_textPane.getCaretPosition();
    }

    /**
     *  Return Display Value
     *  @return displayed String value
     */
    public String getDisplay() {
        return m_textPane.getText();
    }		// getDisplay

    /**
     *      Get Foreground
     *      @return color
     */
    public Color getForeground() {

        if (m_textPane == null) {	// during init
            return super.getForeground();
        } else {
            return m_textPane.getForeground();
        }

    }					// getForeground

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public InputMethodRequests getInputMethodRequests() {
        return m_textPane.getInputMethodRequests();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getText() {
        return m_textPane.getText();
    }

    /**
     *      Return Editor value
     *  @return current value
     */
    public Object getValue() {
        return m_textPane.getText();
    }		// getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isEditable() {
        return m_textPane.isEditable();
    }

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
        return m_textPane.isEditable();
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
     *      Set Background
     *      @param color color
     */
    public void setBackground(Color color) {

        if (color.equals(getBackground())) {
            return;
        }

        if (m_textPane == null) {	// during init
            super.setBackground(color);
        } else {
            m_textPane.setBackground(color);
        }

    }					// setBackground

    /**
     * Descripción de Método
     *
     *
     * @param pos
     */
    public void setCaretPosition(int pos) {
        m_textPane.setCaretPosition(pos);
    }

    /**
     *      Set Content Type
     *      @param type e.g. text/html
     */
    public void setContentType(String type) {

        if (m_textPane != null) {	// during init
            m_textPane.setContentType(type);
        }

    }					// setContentType

    /**
     * Descripción de Método
     *
     *
     * @param edit
     */
    public void setEditable(boolean edit) {
        m_textPane.setEditable(edit);
    }

    /**
     *      Set Foreground
     *      @param color color
     */
    public void setForeground(Color color) {

        if (m_textPane == null) {	// during init
            super.setForeground(color);
        } else {
            m_textPane.setForeground(color);
        }

    }					// setForeground

    /**
     * Descripción de Método
     *
     *
     * @param l
     */
    public void setInputVerifier(InputVerifier l) {
        m_textPane.setInputVerifier(l);
    }

    /**
     *      Set Editor Mandatory
     *  @param mandatory true, if you have to enter data
     */
    public void setMandatory(boolean mandatory) {

        m_mandatory	= mandatory;
        setBackground(false);

    }		// setMandatory

    /**
     * Descripción de Método
     *
     *
     * @param isOpaque
     */
    public void setOpaque(boolean isOpaque) {

        // JScrollPane & Viewport is always not Opaque
        if (m_textPane == null) {	// during init of JScrollPane
            super.setOpaque(isOpaque);
        } else {
            m_textPane.setOpaque(isOpaque);
        }
    }					// setOpaque

    /**
     *      Enable Editor
     *  @param rw true, if you can enter/select data
     */
    public void setReadWrite(boolean rw) {

        if (m_textPane.isEditable() != rw) {
            m_textPane.setEditable(rw);
        }

        setBackground(false);

    }		// setReadWrite

    /**
     *  Set Text and position top
     *  @param text
     */
    public void setText(String text) {

        m_textPane.setText(text);
        m_textPane.setCaretPosition(0);
    }

    /**
     *  Set Text only
     *  @param text
     */
    public void setTextNoCaret(String text) {
        m_textPane.setText(text);
    }
    
    /**
     *      Set Editor to value
     *  @param value value of the editor
     */
    public void setValue(Object value) {

        if (value == null) {
            m_textPane.setText("");
        } else {
            m_textPane.setText(value.toString());
        }

    }		// setValue
}	// CTextPane



/*
 * @(#)CTextPane.java   02.jul 2007
 * 
 *  Fin del fichero CTextPane.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
