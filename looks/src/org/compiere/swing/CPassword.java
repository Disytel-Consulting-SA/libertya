/*
 * @(#)CPassword.java   12.oct 2007  Versión 2.2
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPasswordField;
import javax.swing.text.Document;

/**
 *  Password Field
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CPassword.java,v 1.7 2005/03/11 20:34:38 jjanke Exp $
 */
public class CPassword extends JPasswordField implements CEditor, KeyListener {

    /** ********************************************************************* */

    /** Mandatory (default false) */
    private boolean	m_mandatory	= false;

    /**
     * Constructs a new <code>JPasswordField</code>,
     * with a default document, <code>null</code> starting
     * text string, and 0 column width.
     */
    public CPassword() {

        super();
        init();
    }

    /**
     * Constructs a new empty <code>JPasswordField</code> with the specified
     * number of columns.  A default model is created, and the initial string
     * is set to <code>null</code>.
     *
     * @param columns the number of columns >= 0
     */
    public CPassword(int columns) {

        super(columns);
        init();
    }

    /**
     * Constructs a new <code>JPasswordField</code> initialized
     * with the specified text.  The document model is set to the
     * default, and the number of columns to 0.
     *
     * @param text the text to be displayed, <code>null</code> if none
     */
    public CPassword(String text) {

        super(text);
        init();
    }

    /**
     * Constructs a new <code>JPasswordField</code> initialized with
     * the specified text and columns.  The document model is set to
     * the default.
     *
     * @param text the text to be displayed, <code>null</code> if none
     * @param columns the number of columns >= 0
     */
    public CPassword(String text, int columns) {

        super(text, columns);
        init();
    }

    /**
     * Constructs a new <code>JPasswordField</code> that uses the
     * given text storage model and the given number of columns.
     * This is the constructor through which the other constructors feed.
     * The echo character is set to '*'.  If the document model is
     * <code>null</code>, a default one will be created.
     *
     * @param doc  the text storage to use
     * @param txt the text to be displayed, <code>null</code> if none
     * @param columns  the number of columns to use to calculate
     *   the preferred width >= 0; if columns is set to zero, the
     *   preferred width will be whatever naturally results from
     *   the component implementation
     */
    public CPassword(Document doc, String txt, int columns) {

        super(doc, txt, columns);
        init();
    }

    /**
     *  Common Init
     */
    private void init() {

        setFont(CompierePLAF.getFont_Field());
        setForeground(CompierePLAF.getTextColor_Normal());

    }		// init

    //~--- get methods --------------------------------------------------------

    /**
     *  Return Display Value
     *  @return displayed String value
     */
    public String getDisplay() {
        return new String(super.getPassword());
    }		// getDisplay

    /**
     *      Return Editor value
     *  @return current value
     */
    public Object getValue() {
        return new String(super.getPassword());
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
        return super.isEditable();
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

        if (super.isEditable() != rw) {
            super.setEditable(rw);
        }

        setBackground(false);

    }		// setEditable

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

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}



/*
 * @(#)CPassword.java   02.jul 2007
 * 
 *  Fin del fichero CPassword.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
