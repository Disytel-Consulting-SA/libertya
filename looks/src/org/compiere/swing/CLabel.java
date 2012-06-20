/*
 * @(#)CLabel.java   12.oct 2007  Versión 2.2
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
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *  Label with Mnemonics interpretation
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CLabel.java,v 1.10 2005/03/11 20:34:38 jjanke Exp $
 */
public class CLabel extends JLabel {

    /** Descripción de Campo */
    public static int	DEFAULT_ALIGNMENT	= JLabel.TRAILING;

    /**
     * Creates a <code>JLabel</code> instance with
     * no image and with an empty string for the title.
     * The label is centered vertically
     * in its display area.
     * The label's contents, once set, will be displayed on the leading edge
     * of the label's display area.
     */
    public CLabel() {

        super("", DEFAULT_ALIGNMENT);
        init();
    }

    /**
     * Creates a <code>Label</code> instance with the specified image.
     * The label is centered vertically and horizontally
     * in its display area.
     *
     * @param image  The image to be displayed by the label.
     */
    public CLabel(Icon image) {

        super(image, DEFAULT_ALIGNMENT);
        init();
    }

    /**
     * Creates a <code>Label</code> instance with the specified text.
     * The label is aligned against the leading edge of its display area,
     * and centered vertically.
     *
     * @param text  The text to be displayed by the label.
     */
    public CLabel(String text) {

        super(text, DEFAULT_ALIGNMENT);
        init();
    }

    /**
     * Creates a <code>Label</code> instance with the specified
     * image and horizontal alignment.
     * The label is centered vertically in its display area.
     *
     * @param image  The image to be displayed by the label.
     * @param horizontalAlignment  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>,
     *           <code>RIGHT</code>,
     *           <code>LEADING</code> or
     *           <code>TRAILING</code>.
     */
    public CLabel(Icon image, int horizontalAlignment) {

        super(image, horizontalAlignment);
        init();
    }

    /**
     * Creates a <code>Label</code> instance with the specified
     * text and horizontal alignment.
     * The label is centered vertically in its display area.
     *
     * @param text  The text to be displayed by the label.
     * @param horizontalAlignment  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>,
     *           <code>RIGHT</code>,
     *           <code>LEADING</code> or
     *           <code>TRAILING</code>.
     */
    public CLabel(String text, int horizontalAlignment) {

        super(text, horizontalAlignment);
        init();
    }

    /**
     * Creates a <code>Label</code> instance with the specified text.
     * The label is aligned against the leading edge of its display area,
     * and centered vertically.
     *
     * @param label  The text to be displayed by the label.
     * @param toolTip   The optional Tooltip text
     */
    public CLabel(String label, String toolTip) {

        super(label, DEFAULT_ALIGNMENT);

        if ((toolTip != null) && (toolTip.length() > 0)) {
            super.setToolTipText(toolTip);
        }

        init();

    }		// CLabel

    /**
     * Creates a <code>Label</code> instance with the specified
     * text, image, and horizontal alignment.
     * The label is centered vertically in its display area.
     * The text is on the trailing edge of the image.
     *
     * @param text  The text to be displayed by the label.
     * @param icon  The image to be displayed by the label.
     * @param horizontalAlignment  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>,
     *           <code>RIGHT</code>,
     *           <code>LEADING</code> or
     *           <code>TRAILING</code>.
     */
    public CLabel(String text, Icon icon, int horizontalAlignment) {

        super(text, icon, horizontalAlignment);
        init();
    }

    /**
     *  Create Mnemonics of text containing "&".
     *      Based on MS notation of &Help => H is Mnemonics
     *  @param text test with Mnemonics
     *  @return text w/o &
     *  @see #setLabelFor
     */
    private String createMnemonic(String text) {

        if (text == null) {
            return text;
        }

        int	pos	= text.indexOf("&");

        if (pos != -1)		// We have a nemonic
        {

            char	ch	= text.charAt(pos + 1);

            if (ch != ' ')	// &_ - is the & character
            {

                setDisplayedMnemonic(ch);

                return text.substring(0, pos) + text.substring(pos + 1);
            }
        }

        return text;

    }		// createMnemonic

    /**
     *  Common init
     */
    private void init() {

        setFocusable(false);
        setOpaque(false);

        //
        setForeground(CompierePLAF.getTextColor_Label());
        setFont(CompierePLAF.getFont_Label());

    }		// init

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Background
     *  @param bg background
     */
    public void setBackground(Color bg) {

        if (bg.equals(getBackground())) {
            return;
        }

        super.setBackground(bg);

    }		// setBackground

    /**
     *      Set Font to Bold
     *      @param bold true bold false normal
     */
    public void setFontBold(boolean bold) {

        Font	font	= getFont();

        if (bold != font.isBold()) {

            font	= new Font(font.getName(), bold
                    ? Font.BOLD
                    : Font.PLAIN, font.getSize());
            setFont(font);
        }

    }		// setFontBold

    /**
     *  Set ReadWrite
     *  @param rw enabled
     */
    public void setReadWrite(boolean rw) {
        this.setEnabled(rw);
    }		// setReadWrite

    /**
     *  Set label text - if it includes &, the next character is the Mnemonic
     *  @param mnemonicLabel Label containing Mnemonic
     */
    public void setText(String mnemonicLabel) {
        super.setText(createMnemonic(mnemonicLabel));
    }		// setText

    /**
     *  Set label text directly (w/o mnemonics)
     *  @param label    Label
     */
    public void setTextDirect(String label) {
        super.setText(label);
    }		// setTextDirect
}	// CLabel



/*
 * @(#)CLabel.java   02.jul 2007
 * 
 *  Fin del fichero CLabel.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
