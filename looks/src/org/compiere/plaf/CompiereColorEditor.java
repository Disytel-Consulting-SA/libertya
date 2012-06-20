/*
 * @(#)CompiereColorEditor.java   12.oct 2007  Versión 2.2
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
import org.compiere.swing.CTextField;

import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.ValueNamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JDialog;

/**
 *  OpenXpertya Color Editor
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompiereColorEditor.java,v 1.12 2005/03/11 20:34:36 jjanke Exp $
 */
public class CompiereColorEditor extends JDialog implements ActionListener, PropertyEditor {

    /** Descripción de Campo */
    private static ResourceBundle	res	= ResourceBundle.getBundle("org.compiere.plaf.PlafRes");

    /** Descripción de Campo */
    private CompiereColor	m_cc	= null;

    /** Descripción de Campo */
    private boolean	m_saved	= false;

    /** Descripción de Campo */
    private boolean	m_setting	= false;

    //

    /** Descripción de Campo */
    private CPanel	northPanel	= new CPanel();

    /** Descripción de Campo */
    private CPanel	southPanel	= new CPanel();

    /** Descripción de Campo */
    private FlowLayout	southLayout	= new FlowLayout();

    /** Descripción de Campo */
    private GridBagLayout	northLayout	= new GridBagLayout();

    /** Descripción de Campo */
    private CButton	bOK	= CompierePLAF.getOKButton();

    /** Descripción de Campo */
    private CButton	bCancel	= CompierePLAF.getCancelButton();

    /** Descripción de Campo */
    private CLabel	typeLabel	= new CLabel();

    /** Descripción de Campo */
    private CComboBox	typeField	= new CComboBox(CompiereColor.TYPES);

    /** Descripción de Campo */
    private CButton	gradientUpper	= new CButton();

    /** Descripción de Campo */
    private CButton	gradientLower	= new CButton();

    /** Descripción de Campo */
    private CLabel	urlLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	urlField	= new CTextField(30);

    /** Descripción de Campo */
    private CButton	taintColor	= new CButton();

    /** Descripción de Campo */
    private CButton	lineColor	= new CButton();

    /** Descripción de Campo */
    private CButton	backColor	= new CButton();

    /** Descripción de Campo */
    private CLabel	alphaLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	alphaField	= new CTextField(10);

    /** Descripción de Campo */
    private CLabel	widthLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	widthField	= new CTextField(10);

    /** Descripción de Campo */
    private CLabel	gradientStartLabel	= new CLabel();

    /** Descripción de Campo */
    private CComboBox	gradientStartField	= new CComboBox(CompiereColor.GRADIENT_SP);

    /** Descripción de Campo */
    private CLabel	gradientDistanceLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	gradientDistanceField	= new CTextField(10);

    /** Descripción de Campo */
    private CButton	flatField	= new CButton();

    /** Descripción de Campo */
    private CLabel	distanceLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	distanceField	= new CTextField(10);

    /** Descripción de Campo */
    private CPanel	centerPanel	= new CPanel();

    /**
     *  Create CompiereColor Dialog with color
     *  @param owner owner
     *  @param color Start Color
     */
    public CompiereColorEditor(Dialog owner, CompiereColor color) {

        super(owner, "", true);
        init(color);

    }		// CompiereColorEditor

    /**
     * **********************************************************************
     *
     * @param owner
     * @param color
     */

    /**
     *  Create CompiereColor Dialog with color
     *  @param owner owner
     *  @param color Start Color
     */
    public CompiereColorEditor(Frame owner, CompiereColor color) {

        super(owner, "", true);
        init(color);

    }		// CompiereColorEditor

    /**
     *  Action Listener
     *  @param e event
     */
    public void actionPerformed(ActionEvent e) {

        if (m_setting) {
            return;
        }

        if (e.getSource() == bOK) {

            m_saved	= true;
            dispose();

            return;

        } else if (e.getSource() == bCancel) {

            dispose();

            return;
        }

        /** ** Field Changes  ** */
        try {

            // Type
            if (e.getSource() == typeField) {

                cmd_type();

                // Flat

            } else if (e.getSource() == flatField) {

                m_cc.setFlatColor(JColorChooser.showDialog(this, flatField.getText(), m_cc.getFlatColor()));

                // Gradient

            } else if (e.getSource() == gradientUpper) {
                m_cc.setGradientUpperColor(JColorChooser.showDialog(this, gradientUpper.getText(), m_cc.getGradientUpperColor()));
            } else if (e.getSource() == gradientLower) {
                m_cc.setGradientLowerColor(JColorChooser.showDialog(this, gradientLower.getText(), m_cc.getGradientLowerColor()));
            } else if (e.getSource() == gradientStartField) {
                m_cc.setGradientStartPoint(((KeyNamePair) gradientStartField.getSelectedItem()).getKey());
            } else if (e.getSource() == gradientDistanceField) {

                m_cc.setGradientRepeatDistance(gradientDistanceField.getText());

                // Texture

            } else if (e.getSource() == urlField) {
                m_cc.setTextureURL(urlField.getText());
            } else if (e.getSource() == alphaField) {
                m_cc.setTextureCompositeAlpha(alphaField.getText());
            } else if (e.getSource() == taintColor) {

                m_cc.setTextureTaintColor(JColorChooser.showDialog(this, taintColor.getText(), m_cc.getTextureTaintColor()));

                // Lines

            } else if (e.getSource() == lineColor) {
                m_cc.setLineColor(JColorChooser.showDialog(this, lineColor.getText(), m_cc.getLineColor()));
            } else if (e.getSource() == backColor) {
                m_cc.setLineBackColor(JColorChooser.showDialog(this, backColor.getText(), m_cc.getLineBackColor()));
            } else if (e.getSource() == widthField) {
                m_cc.setLineWidth(widthField.getText());
            } else if (e.getSource() == distanceField) {
                m_cc.setLineDistance(distanceField.getText());
            }
        } catch (Exception ee) {}

        setColor(m_cc);

    }		// actionPerformed

    /**
     * Register a listener for the PropertyChange event.  When a
     * PropertyEditor changes its value it should fire a PropertyChange
     * event on all registered PropertyChangeListeners, specifying the
     * null value for the property name and itself as the source.
     *
     * @param listener  An object to be invoked when a PropertyChange
     *              event is fired.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
    }		// addPropertyChangeListener

    /**
     *  Set Type with default values
     */
    private void cmd_type() {

        ValueNamePair	vp	= (ValueNamePair) typeField.getSelectedItem();

        if (vp.getValue().equals(CompiereColor.TYPE_FLAT)) {
            m_cc	= new CompiereColor(CompiereColor.TYPE_FLAT);
        } else if (vp.getValue().equals(CompiereColor.TYPE_GRADIENT)) {
            m_cc	= new CompiereColor(CompiereColor.TYPE_GRADIENT);
        } else if (vp.getValue().equals(CompiereColor.TYPE_TEXTURE)) {
            m_cc	= new CompiereColor(CompiereColor.TYPE_TEXTURE);
        } else if (vp.getValue().equals(CompiereColor.TYPE_LINES)) {
            m_cc	= new CompiereColor(CompiereColor.TYPE_LINES);
        }

        setColor(m_cc);

    }		// cmd_type

    /**
     *  Init Dialog
     *  @param color Start Color
     */
    private void init(CompiereColor color) {

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bOK.addActionListener(this);
        bCancel.addActionListener(this);
        typeField.addActionListener(this);
        flatField.addActionListener(this);
        gradientUpper.addActionListener(this);
        gradientLower.addActionListener(this);
        urlField.addActionListener(this);
        alphaField.addActionListener(this);
        taintColor.addActionListener(this);
        lineColor.addActionListener(this);
        backColor.addActionListener(this);
        widthField.addActionListener(this);
        distanceField.addActionListener(this);
        gradientStartField.addActionListener(this);
        gradientDistanceField.addActionListener(this);

        if (color == null) {
            setColor(m_cc);
        } else {
            setColor(new CompiereColor(color));
        }

        CompierePLAF.showCenterScreen(this);

    }		// init

    /**
     *  Static Layout.
     *  <pre>
     *      - northPanel
     *          - labels & fields
     *      - centerPanel
     *      - southPanel
     *  </pre>
     *  @throws Exception
     */
    private void jbInit() throws Exception {

        this.setTitle(res.getString("CompiereColorEditor"));
        CompiereColor.setBackground(this);
        southPanel.setLayout(southLayout);
        southLayout.setAlignment(FlowLayout.RIGHT);
        northPanel.setLayout(northLayout);
        typeLabel.setText(res.getString("CompiereType"));
        gradientUpper.setText(res.getString("GradientUpperColor"));
        gradientLower.setText(res.getString("GradientLowerColor"));
        gradientStartLabel.setText(res.getString("GradientStart"));
        gradientDistanceLabel.setText(res.getString("GradientDistance"));
        urlLabel.setText(res.getString("TextureURL"));
        alphaLabel.setText(res.getString("TextureAlpha"));
        taintColor.setText(res.getString("TextureTaintColor"));
        lineColor.setText(res.getString("LineColor"));
        backColor.setText(res.getString("LineBackColor"));
        widthLabel.setText(res.getString("LineWidth"));
        distanceLabel.setText(res.getString("LineDistance"));
        flatField.setText(res.getString("FlatColor"));
        centerPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        centerPanel.setPreferredSize(new Dimension(400, 200));
        centerPanel.setOpaque(true);
        northPanel.setPreferredSize(new Dimension(400, 150));
        southPanel.add(bCancel, null);
        this.getContentPane().add(northPanel, BorderLayout.NORTH);
        southPanel.add(bOK, null);
        this.getContentPane().add(southPanel, BorderLayout.SOUTH);
        this.getContentPane().add(centerPanel, BorderLayout.CENTER);
        northPanel.add(typeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(typeField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        northPanel.add(gradientLower, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(urlField, new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(alphaLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(alphaField, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(taintColor, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(backColor, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(widthLabel, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(widthField, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(distanceLabel, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(distanceField, new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(flatField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(gradientStartField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(gradientDistanceField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(urlLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(gradientStartLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(gradientDistanceLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(gradientUpper, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(lineColor, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

    }		// jbInit

    /**
     * Paint a representation of the value into a given area of screen
     * real estate.  Note that the propertyEditor is responsible for doing
     * its own clipping so that it fits into the given rectangle.
     * <p>
     * If the PropertyEditor doesn't honor paint requests (see isPaintable)
     * this method should be a silent noop.
     * <p>
     * The given Graphics object will have the default font, color, etc of
     * the parent container.  The PropertyEditor may change graphics attributes
     * such as font and color and doesn't need to restore the old values.
     *
     * @param gfx  Graphics object to paint into.
     * @param box  Rectangle within graphics object into which we should paint.
     */
    public void paintValue(Graphics gfx, Rectangle box) {

        /** @todo: Implement this java.beans.PropertyEditor method */
        throw new java.lang.UnsupportedOperationException("Method paintValue() not yet implemented.");
    }		// paintValue

    /**
     * Remove a listener for the PropertyChange event.
     * @param listener  The PropertyChange listener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(listener);
    }		// removePropertyChangeListener

    /**
     *  Get Background CompiereColor
     *  @param owner owner
     *  @param color optional initial color
     *  @return CompiereColor
     */
    public static CompiereColor showDialog(Dialog owner, CompiereColor color) {

        CompiereColorEditor	cce	= new CompiereColorEditor(owner, color);

        if (cce.isSaved()) {
            return cce.getColor();
        }

        return color;

    }		// showIt

    /**
     *  Get Background CompiereColor
     *  @param owner owner
     *  @param color optional initial color
     *  @return CompiereColor
     */
    public static CompiereColor showDialog(Frame owner, CompiereColor color) {

        CompiereColorEditor	cce	= new CompiereColorEditor(owner, color);

        if (cce.isSaved()) {
            return cce.getColor();
        }

        return color;

    }		// showDialog

    /**
     * Determines whether this property editor supports a custom editor.
     * @return  True if the propertyEditor can provide a custom editor.
     */
    public boolean supportsCustomEditor() {
        return true;
    }		// supportsCustomEditor

    /**
     *  UpdateField from CompiereColor
     */
    private void updateFields() {

        m_setting	= true;

        // Type
        for (int i = 0; i < CompiereColor.TYPES.length; i++) {

            if (m_cc.getType().equals(CompiereColor.TYPE_VALUES[i])) {

                typeField.setSelectedItem(CompiereColor.TYPES[i]);

                break;
            }
        }

        //
        if (m_cc.isFlat()) {

            flatField.setVisible(true);
            gradientUpper.setVisible(false);
            gradientLower.setVisible(false);
            gradientStartLabel.setVisible(false);
            gradientDistanceLabel.setVisible(false);
            gradientStartField.setVisible(false);
            gradientDistanceField.setVisible(false);
            urlLabel.setVisible(false);
            urlField.setVisible(false);
            alphaLabel.setVisible(false);
            alphaField.setVisible(false);
            taintColor.setVisible(false);
            lineColor.setVisible(false);
            backColor.setVisible(false);
            widthLabel.setVisible(false);
            widthField.setVisible(false);
            distanceLabel.setVisible(false);
            distanceField.setVisible(false);

            //
            flatField.setBackground(m_cc.getFlatColor());

        } else if (m_cc.isGradient()) {

            flatField.setVisible(false);
            gradientUpper.setVisible(true);
            gradientLower.setVisible(true);
            gradientStartLabel.setVisible(true);
            gradientDistanceLabel.setVisible(true);
            gradientStartField.setVisible(true);
            gradientDistanceField.setVisible(true);
            urlLabel.setVisible(false);
            urlField.setVisible(false);
            alphaLabel.setVisible(false);
            alphaField.setVisible(false);
            taintColor.setVisible(false);
            lineColor.setVisible(false);
            backColor.setVisible(false);
            widthLabel.setVisible(false);
            widthField.setVisible(false);
            distanceLabel.setVisible(false);
            distanceField.setVisible(false);

            //
            gradientUpper.setBackground(m_cc.getGradientUpperColor());
            gradientLower.setBackground(m_cc.getGradientLowerColor());
            gradientDistanceField.setText(String.valueOf(m_cc.getGradientRepeatDistance()));

            for (int i = 0; i < CompiereColor.GRADIENT_SP.length; i++) {

                if (m_cc.getGradientStartPoint() == CompiereColor.GRADIENT_SP_VALUES[i]) {

                    gradientStartField.setSelectedItem(CompiereColor.GRADIENT_SP[i]);

                    break;
                }
            }

        } else if (m_cc.isTexture()) {

            flatField.setVisible(false);
            gradientUpper.setVisible(false);
            gradientLower.setVisible(false);
            gradientStartLabel.setVisible(false);
            gradientDistanceLabel.setVisible(false);
            gradientStartField.setVisible(false);
            gradientDistanceField.setVisible(false);
            urlLabel.setVisible(true);
            urlField.setVisible(true);
            alphaLabel.setVisible(true);
            alphaField.setVisible(true);
            taintColor.setVisible(true);
            lineColor.setVisible(false);
            backColor.setVisible(false);
            widthLabel.setVisible(false);
            widthField.setVisible(false);
            distanceLabel.setVisible(false);
            distanceField.setVisible(false);

            //
            urlField.setText(m_cc.getTextureURL().toString());
            alphaField.setText(String.valueOf(m_cc.getTextureCompositeAlpha()));
            taintColor.setBackground(m_cc.getTextureTaintColor());

        } else if (m_cc.isLine()) {

            flatField.setVisible(false);
            gradientUpper.setVisible(false);
            gradientLower.setVisible(false);
            gradientStartLabel.setVisible(false);
            gradientDistanceLabel.setVisible(false);
            gradientStartField.setVisible(false);
            gradientDistanceField.setVisible(false);
            urlLabel.setVisible(false);
            urlField.setVisible(false);
            alphaLabel.setVisible(false);
            alphaField.setVisible(false);
            taintColor.setVisible(false);
            lineColor.setVisible(true);
            backColor.setVisible(true);
            widthLabel.setVisible(true);
            widthField.setVisible(true);
            distanceLabel.setVisible(true);
            distanceField.setVisible(true);

            //
            lineColor.setBackground(m_cc.getLineColor());
            backColor.setBackground(m_cc.getLineBackColor());
            widthField.setText(String.valueOf(m_cc.getLineWidth()));
            distanceField.setText(String.valueOf(m_cc.getLineDistance()));
        }

        m_setting	= false;

    }		// updateFields

    //~--- get methods --------------------------------------------------------

    /**
     * Gets the property value as text.
     *
     * @return The property value as a human editable string.
     * <p>   Returns null if the value can't be expressed as an editable string.
     * <p>   If a non-null value is returned, then the PropertyEditor should
     *           be prepared to parse that string back in setAsText().
     */
    public String getAsText() {
        return m_cc.toString();
    }		// getAsText

    /**
     *  Get Color
     *  @return Color, when saved - else null
     */
    public CompiereColor getColor() {
        return m_cc;
    }		// getColor

    /**
     * A PropertyEditor may choose to make available a full custom Component
     * that edits its property value.  It is the responsibility of the
     * PropertyEditor to hook itself up to its editor Component itself and
     * to report property value changes by firing a PropertyChange event.
     * <P>
     * The higher-level code that calls getCustomEditor may either embed
     * the Component in some larger property sheet, or it may put it in
     * its own individual dialog, or ...
     *
     * @return A java.awt.Component that will allow a human to directly
     *      edit the current property value.  May be null if this is
     *          not supported.
     */
    public Component getCustomEditor() {
        return this;
    }		// getCustomEditor

    /**
     * This method is intended for use when generating Java code to set
     * the value of the property.  It should return a fragment of Java code
     * that can be used to initialize a variable with the current property
     * value.
     * <p>
     * Example results are "2", "new Color(127,127,34)", "Color.orange", etc.
     *
     * @return A fragment of Java code representing an initializer for the
     *      current value.
     */
    public String getJavaInitializationString() {
        return "new CompiereColor()";
    }		// String getJavaInitializationString

    /**
     * If the property value must be one of a set of known tagged values,
     * then this method should return an array of the tags.  This can
     * be used to represent (for example) enum values.  If a PropertyEditor
     * supports tags, then it should support the use of setAsText with
     * a tag value as a way of setting the value and the use of getAsText
     * to identify the current value.
     *
     * @return The tag values for this property.  May be null if this
     *   property cannot be represented as a tagged value.
     */
    public String[] getTags() {
        return null;
    }		// getTags

    /**
     * Gets the property value.
     *
     * @return The value of the property.  Primitive types such as "int" will
     * be wrapped as the corresponding object type such as "java.lang.Integer".
     */
    public Object getValue() {
        return getColor();
    }		// getColor

    /**
     * Determines whether this property editor is paintable.
     * @return  True if the class will honor the paintValue method.
     */
    public boolean isPaintable() {
        return false;
    }

    /**
     *  Was the selection saved
     *  @return true if saved
     */
    public boolean isSaved() {
        return m_saved;
    }		// m_saved

    //~--- set methods --------------------------------------------------------

    /**
     * Set the property value by parsing a given String.  May raise
     * java.lang.IllegalArgumentException if either the String is
     * badly formatted or if this kind of property can't be expressed
     * as text.
     * @param text  The string to be parsed.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        throw new java.lang.IllegalArgumentException("CompiereColorEditor.setAsText not supported");
    }		// setAsText

    /**
     *  Set Color and update UI
     *  @param color color
     */
    public void setColor(CompiereColor color) {

        if ((color == null) && (m_cc != null)) {
            return;
        }

        //
        // System.out.println("CompiereColorEditor.setColor " + color);
        m_cc	= color;

        if (m_cc == null) {
            m_cc	= CompierePanelUI.getDefaultBackground();
        }

        // update display
        updateFields();
        centerPanel.setBackgroundColor(m_cc);
        centerPanel.repaint();

    }		// setColor

    /**
     * **********************************************************************
     *
     * @param value
     */

    /**
     * Set (or change) the object that is to be edited.  Primitive types such
     * as "int" must be wrapped as the corresponding object type such as
     * "java.lang.Integer".
     *
     * @param value The new target object to be edited.  Note that this
     *     object should not be modified by the PropertyEditor, rather
     *     the PropertyEditor should create a new object to hold any
     *     modified value.
     */
    public void setValue(Object value) {

        if ((value != null) && (value instanceof CompiereColor)) {
            setColor(new CompiereColor((CompiereColor) value));
        } else {
            throw new IllegalArgumentException("CompiereColorEditor.setValue requires CompiereColor");
        }

    }		// setValue
}	// CompiereColorEditor



/*
 * @(#)CompiereColorEditor.java   02.jul 2007
 * 
 *  Fin del fichero CompiereColorEditor.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
