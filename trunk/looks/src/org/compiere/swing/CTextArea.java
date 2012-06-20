/*
 * @(#)CTextArea.java   12.oct 2007  Versión 2.2
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
import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 *  OpenXpertya TextArea - A ScrollPane with a JTextArea.
 *  Manages visibility, opaque and color consistently
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CTextArea.java,v 1.10 2005/03/11 20:34:38 jjanke Exp $
 */
public class CTextArea extends JScrollPane implements CEditor {

    /** Descripción de Campo */
    private JTextArea	m_textArea	= null;

    /** ********************************************************************* */

    /** Mandatory (default false) */
    private boolean	m_mandatory	= false;

    /**
     * Constructs a new TextArea.  A default model is set, the initial string
     * is null, and rows/columns are set to 0.
     */
    public CTextArea() {
        this(new JTextArea());
    }		// CText

    /**
     * Constructs a new JTextArea with the given document model, and defaults
     * for all of the other arguments (null, 0, 0).
     *
     * @param doc  the model to use
     */
    public CTextArea(Document doc) {
        this(new JTextArea(doc));
    }		// CText

    /**
     *  Create a JScrollArea with a JTextArea.
     *  (use Cpmpiere Colors, Line wrap)
     *  @param textArea
     */
    public CTextArea(JTextArea textArea) {

        super(textArea);
        m_textArea	= textArea;
        super.setOpaque(false);
        super.getViewport().setOpaque(false);
        m_textArea.setFont(CompierePLAF.getFont_Field());
        m_textArea.setForeground(CompierePLAF.getTextColor_Normal());
        m_textArea.setLineWrap(true);
        m_textArea.setWrapStyleWord(true);

    }		// CTextArea

    /**
     * Constructs a new TextArea with the specified text displayed.
     * A default model is created and rows/columns are set to 0.
     *
     * @param text the text to be displayed, or null
     */
    public CTextArea(String text) {
        this(new JTextArea(text));
    }		// CText

    /**
     * Constructs a new empty TextArea with the specified number of
     * rows and columns.  A default model is created, and the initial
     * string is null.
     *
     * @param rows the number of rows >= 0
     * @param columns the number of columns >= 0
     */
    public CTextArea(int rows, int columns) {
        this(new JTextArea(rows, columns));
    }		// CText

    /**
     * Constructs a new TextArea with the specified text and number
     * of rows and columns.  A default model is created.
     *
     * @param text the text to be displayed, or null
     * @param rows the number of rows >= 0
     * @param columns the number of columns >= 0
     */
    public CTextArea(String text, int rows, int columns) {
        this(new JTextArea(text, rows, columns));
    }		// CText

    /**
     * Constructs a new JTextArea with the specified number of rows
     * and columns, and the given model.  All of the constructors
     * feed through this constructor.
     *
     * @param doc the model to use, or create a default one if null
     * @param text the text to be displayed, null if none
     * @param rows the number of rows >= 0
     * @param columns the number of columns >= 0
     */
    public CTextArea(Document doc, String text, int rows, int columns) {
        this(new JTextArea(doc, text, rows, columns));
    }		// CTextArea

    /**
     * Descripción de Método
     *
     *
     * @param l
     */
    public void addFocusListener(FocusListener l) {

        if (m_textArea == null) {	// during init
            super.addFocusListener(l);
        } else {
            m_textArea.addFocusListener(l);
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */
    public void addInputMethodListener(InputMethodListener l) {
        m_textArea.addInputMethodListener(l);
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */
    public void addKeyListener(KeyListener l) {
        m_textArea.addKeyListener(l);
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */
    public void addMouseListener(MouseListener l) {
        m_textArea.addMouseListener(l);
    }

    /**
     * Descripción de Método
     *
     *
     * @param text
     */
    public void append(String text) {
        m_textArea.append(text);
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public Color getBackground() {

        if (m_textArea == null) {	// during init
            return super.getBackground();
        }

        return m_textArea.getBackground();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getCaretPosition() {
        return m_textArea.getCaretPosition();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getColumns() {
        return m_textArea.getColumns();
    }

    /**
     *  Return Display Value
     *  @return displayed String value
     */
    public String getDisplay() {
        return m_textArea.getText();
    }		// getDisplay

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public Color getForeground() {

        if (m_textArea == null) {	// during init
            return super.getForeground();
        }

        return m_textArea.getForeground();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public InputMethodRequests getInputMethodRequests() {
        return m_textArea.getInputMethodRequests();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getRows() {
        return m_textArea.getRows();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getText() {
        return m_textArea.getText();
    }

    /**
     *      Return Editor value
     *  @return current value
     */
    public Object getValue() {
        return m_textArea.getText();
    }		// getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isEditable() {
        return m_textArea.isEditable();
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
        return m_textArea.isEditable();
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
     * Descripción de Método
     *
     *
     * @param color
     */
    public void setBackground(Color color) {

        if (color.equals(getBackground())) {
            return;
        }

        if (m_textArea == null) {	// during init
            super.setBackground(color);
        } else {
            m_textArea.setBackground(color);
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param pos
     */
    public void setCaretPosition(int pos) {
        m_textArea.setCaretPosition(pos);
    }

    /**
     * Descripción de Método
     *
     *
     * @param cols
     */
    public void setColumns(int cols) {
        m_textArea.setColumns(cols);
    }

    /**
     * Descripción de Método
     *
     *
     * @param edit
     */
    public void setEditable(boolean edit) {
        m_textArea.setEditable(edit);
    }

    /**
     * Descripción de Método
     *
     *
     * @param color
     */
    public void setForeground(Color color) {

        if (m_textArea == null) {	// during init
            super.setForeground(color);
        } else {
            m_textArea.setForeground(color);
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */
    public void setInputVerifier(InputVerifier l) {
        m_textArea.setInputVerifier(l);
    }

    /**
     * Descripción de Método
     *
     *
     * @param wrap
     */
    public void setLineWrap(boolean wrap) {
        m_textArea.setLineWrap(wrap);
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
        if (m_textArea == null) {	// during init of JScrollPane
            super.setOpaque(isOpaque);
        } else {
            m_textArea.setOpaque(isOpaque);
        }
    }					// setOpaque

    /**
     *      Enable Editor
     *  @param rw true, if you can enter/select data
     */
    public void setReadWrite(boolean rw) {

        if (m_textArea.isEditable() != rw) {
            m_textArea.setEditable(rw);
        }

        setBackground(false);

    }		// setReadWrite

    /**
     * Descripción de Método
     *
     *
     * @param rows
     */
    public void setRows(int rows) {
        m_textArea.setRows(rows);
    }

    /**
     *  Set Text and position top
     *  @param text
     */
    public void setText(String text) {

        m_textArea.setText(text);
        m_textArea.setCaretPosition(0);
    }

    /**
     *      Set Editor to value
     *  @param value value of the editor
     */
    public void setValue(Object value) {

        if (value == null) {
            m_textArea.setText("");
        } else {
            m_textArea.setText(value.toString());
        }

    }		// setValue

    /**
     * Descripción de Método
     *
     *
     * @param word
     */
    public void setWrapStyleWord(boolean word) {
        m_textArea.setWrapStyleWord(word);
    }
}	// CTextArea



/*
 * @(#)CTextArea.java   02.jul 2007
 * 
 *  Fin del fichero CTextArea.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
