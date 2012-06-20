/*
 * @(#)FontChooser.java   12.oct 2007  Versión 2.2
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



package org.compiere.plaf;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JTextArea;

/**
 *  Font Chooser Dialog
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: FontChooser.java,v 1.7 2005/03/11 20:34:37 jjanke Exp $
 */
public class FontChooser extends JDialog implements ActionListener {

    /** Descripción de Campo */
    static ResourceBundle	s_res	= ResourceBundle.getBundle("org.compiere.plaf.PlafRes");

    /** Static list of Styles */
    public static FontStyle[]	s_list	= { new FontStyle(s_res.getString("Plain"), Font.PLAIN), new FontStyle(s_res.getString("Italic"), Font.ITALIC), new FontStyle(s_res.getString("Bold"), Font.BOLD), new FontStyle(s_res.getString("BoldItalic"), Font.BOLD | Font.ITALIC) };

    /** Descripción de Campo */
    private Font	m_font	= super.getFont();

    /** Descripción de Campo */
    private Font	m_retFont	= null;

    /** Descripción de Campo */
    private boolean	m_setting	= false;

    /** Descripción de Campo */
    private CPanel	mainPanel	= new CPanel();

    /** Descripción de Campo */
    private BorderLayout	mainLayout	= new BorderLayout();

    /** Descripción de Campo */
    private CPanel	selectPanel	= new CPanel();

    /** Descripción de Campo */
    private CLabel	nameLabel	= new CLabel();

    /** Descripción de Campo */
    private CComboBox	fontName	= new CComboBox();

    /** Descripción de Campo */
    private CLabel	sizeLabel	= new CLabel();

    /** Descripción de Campo */
    private CLabel	styleLabel	= new CLabel();

    /** Descripción de Campo */
    private GridBagLayout	selectLayout	= new GridBagLayout();

    /** Descripción de Campo */
    private JTextArea	fontTest	= new JTextArea();

    /** Descripción de Campo */
    private CComboBox	fontStyle	= new CComboBox();

    /** Descripción de Campo */
    private CComboBox	fontSize	= new CComboBox();

    /** Descripción de Campo */
    private JTextArea	fontInfo	= new JTextArea();

    /** Descripción de Campo */
    private CPanel	confirmPanel	= new CPanel();

    /** Descripción de Campo */
    private FlowLayout	confirmLayout	= new FlowLayout();

    /** Descripción de Campo */
    private CButton	bOK	= CompierePLAF.getOKButton();

    /** Descripción de Campo */
    private CButton	bCancel	= CompierePLAF.getCancelButton();

    /**
     *  IDE Constructor
     */
    public FontChooser() {
        this(null, s_res.getString("FontChooser"), null);
    }		// FontChooser

    /**
     * **********************************************************************
     *
     * @param owner
     * @param title
     * @param initFont
     */

    /**
     *  Constructor
     *
     *  @param owner Base window
     *  @param title Chooser Title
     *  @param initFont Initial Font
     */
    public FontChooser(Dialog owner, String title, Font initFont) {

        super(owner, title, true);

        try {

            jbInit();
            dynInit();
            setFont(initFont);
            CompierePLAF.showCenterScreen(this);

        } catch (Exception ex) {

            System.err.println("FontChooser");
            ex.printStackTrace();
        }

    }		// FontChooser

    /**
     *  ActionListener
     *  @param e
     */
    public void actionPerformed(ActionEvent e) {

        if (m_setting) {
            return;
        }

        if (e.getSource() == bOK) {

            m_retFont	= m_font;
            dispose();

        } else if (e.getSource() == bCancel) {
            dispose();
        } else if (e.getSource() == fontName) {

            String	s	= fontName.getSelectedItem().toString();

            m_font	= new Font(s, m_font.getStyle(), m_font.getSize());

        } else if (e.getSource() == fontSize) {

            String	s	= fontSize.getSelectedItem().toString();

            m_font	= new Font(m_font.getName(), m_font.getStyle(), Integer.parseInt(s));

        } else if (e.getSource() == fontStyle) {

            FontStyle	fs	= (FontStyle) fontStyle.getSelectedItem();

            m_font	= new Font(m_font.getName(), fs.getID(), m_font.getSize());
        }

        // System.out.println("NewFont - " + m_font.toString());
        setFont(m_font);

    }		// actionPerformed

    /**
     *  Dynamic Init
     */
    private void dynInit() {

        String[]	names	= GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        Arrays.sort(names);

        for (int i = 0; i < names.length; i++) {
            fontName.addItem(names[i]);
        }

        fontName.addActionListener(this);

        //
        for (int i = 6; i < 32; i++) {
            fontSize.addItem(String.valueOf(i));
        }

        fontSize.addActionListener(this);

        //
        for (int i = 0; i < s_list.length; i++) {
            fontStyle.addItem(s_list[i]);
        }

        fontStyle.addActionListener(this);

    }		// dynInit

    /**
     *  Static Layout
     *  @throws Exception
     */
    private void jbInit() throws Exception {

        CompiereColor.setBackground(this);
        mainPanel.setLayout(mainLayout);
        nameLabel.setText(s_res.getString("Name"));
        selectPanel.setLayout(selectLayout);
        sizeLabel.setText(s_res.getString("Size"));
        styleLabel.setText(s_res.getString("Style"));
        fontTest.setText(s_res.getString("TestString"));
        fontTest.setLineWrap(true);
        fontTest.setWrapStyleWord(true);
        fontTest.setBackground(CompierePLAF.getFieldBackground_Inactive());
        fontTest.setBorder(BorderFactory.createLoweredBevelBorder());
        fontTest.setPreferredSize(new Dimension(220, 100));
        fontInfo.setText(s_res.getString("FontString"));
        fontInfo.setLineWrap(true);
        fontInfo.setWrapStyleWord(true);
        fontInfo.setBackground(CompierePLAF.getFieldBackground_Inactive());
        fontInfo.setOpaque(false);
        fontInfo.setEditable(false);
        confirmPanel.setLayout(confirmLayout);
        confirmLayout.setAlignment(FlowLayout.RIGHT);
        confirmPanel.setOpaque(false);
        selectPanel.setOpaque(false);
        getContentPane().add(mainPanel);
        mainPanel.add(selectPanel, BorderLayout.CENTER);
        selectPanel.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        selectPanel.add(fontName, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        selectPanel.add(sizeLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        selectPanel.add(styleLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        selectPanel.add(fontStyle, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        selectPanel.add(fontSize, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        selectPanel.add(fontTest, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(20, 5, 5, 5), 0, 0));
        selectPanel.add(fontInfo, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 10, 5), 0, 0));

        //
        mainPanel.add(confirmPanel, BorderLayout.SOUTH);
        confirmPanel.add(bCancel, null);
        confirmPanel.add(bOK, null);
        bCancel.addActionListener(this);
        bOK.addActionListener(this);

    }		// jbInit

    /**
     *  Show Dialog with initial font and return selected font
     *  @param owner Base window
     *  @param title Chooser Title
     *  @param initFont initial font
     *  @return selected font
     */
    public static Font showDialog(Dialog owner, String title, Font initFont) {

        Font		retValue	= initFont;
        FontChooser	fc		= new FontChooser(owner, title, initFont);

        retValue	= fc.getFont();
        fc		= null;

        return retValue;

    }		// showDialog

    //~--- get methods --------------------------------------------------------

    /**
     *  Return selected font
     *  @return font
     */
    public Font getFont() {
        return m_retFont;
    }		// getFont

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Font - sets font for chooser - not the component font
     *  @param font
     */
    public void setFont(Font font) {

        if (font == null) {
            return;
        }

        // Log.trace("FontChooser.setFont - " + font.toString());
        if (m_retFont == null) {
            m_retFont	= font;
        }

        //
        fontTest.setFont(font);
        fontInfo.setFont(font);
        fontInfo.setText(font.toString());

        //
        m_setting	= true;
        fontName.setSelectedItem(font.getName());

        if (!fontName.getSelectedItem().equals(font.getName())) {
            System.err.println("FontChooser.setFont" + fontName.getSelectedItem().toString() + " <> " + font.getName());
        }

        //
        fontSize.setSelectedItem(String.valueOf(font.getSize()));

        if (!fontSize.getSelectedItem().equals(String.valueOf(font.getSize()))) {
            System.err.println("FontChooser.setFont" + fontSize.getSelectedItem() + " <> " + font.getSize());
        }

        // find style
        for (int i = 0; i < s_list.length; i++) {

            if (s_list[i].getID() == font.getStyle()) {
                fontStyle.setSelectedItem(s_list[i]);
            }
        }

        if (((FontStyle) fontStyle.getSelectedItem()).getID() != font.getStyle()) {
            System.err.println("FontChooser.setFont" + ((FontStyle) fontStyle.getSelectedItem()).getID() + " <> " + font.getStyle());
        }

        //
        m_font	= font;
        this.pack();
        m_setting	= false;

    }		// setFont
}	// FontChooser


/**
 *  Font Style Value Object
 */
class FontStyle {

    /** Descripción de Campo */
    private int	m_id;

    /** Descripción de Campo */
    private String	m_name;

    /**
     *  Create FontStyle
     *  @param name
     *  @param id
     */
    public FontStyle(String name, int id) {

        m_name	= name;
        m_id	= id;

    }		// FontStyle

    /**
     *  Get Name
     *  @return name
     */
    public String toString() {
        return m_name;
    }		// getName

    //~--- get methods --------------------------------------------------------

    /**
     *  Get int value of Font Style
     *  @return id
     */
    public int getID() {
        return m_id;
    }		// getID
}	// FontStyle



/*
 * @(#)FontChooser.java   02.jul 2007
 * 
 *  Fin del fichero FontChooser.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
