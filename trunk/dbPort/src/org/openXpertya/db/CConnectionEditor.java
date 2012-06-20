/*
 * @(#)CConnectionEditor.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.db;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CEditor;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;

/**
 *  Connection Editor.
 *  A combo box and a button
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CConnectionEditor.java,v 1.6 2005/03/11 20:29:01 jjanke Exp $
 */
public class CConnectionEditor extends JComponent implements CEditor {

    /** Text Element */
    private JTextField	m_text	= new JTextField(10);

    /** Host Button Element */
    private JLabel	m_server	= new JLabel();

    /** DB Button Element */
    private JLabel	m_db	= new JLabel();

    /** The Value */
    private CConnection	m_value	= null;

    /** ReadWrite */
    private boolean	m_rw	= true;

    /** Mandatory */
    private boolean	m_mandatory	= false;

    /** Action Listeners */
    transient private Vector	m_actionListeners;

    /**
     *  Connection Editor creating new Connection
     */
    public CConnectionEditor() {

        super();

        CConnectionExitor_MouseListener	ml	= new CConnectionExitor_MouseListener();

        // Layout
        m_text.setEditable(false);
        m_text.setBorder(null);
        m_text.addMouseListener(ml);
        m_server.setIcon(new ImageIcon(getClass().getResource("Server16.gif")));
        m_server.setFocusable(false);
        m_server.setBorder(null);
        m_server.setOpaque(true);
        m_server.addMouseListener(ml);
        m_db.setIcon(new ImageIcon(getClass().getResource("Database16.gif")));
        m_db.setFocusable(false);
        m_db.setBorder(null);
        m_db.setOpaque(true);
        m_db.addMouseListener(ml);
        LookAndFeel.installBorder(this, "TextField.border");

        //
        setLayout(new BorderLayout(0, 0));
        add(m_server, BorderLayout.WEST);
        add(m_text, BorderLayout.CENTER);
        add(m_db, BorderLayout.EAST);

    }		// CConnectionEditor

    /**
     *  Add Action Listener
     *  @param l
     */
    public synchronized void addActionListener(ActionListener l) {

        Vector	v	= (m_actionListeners == null)
                          ? new Vector(2)
                          : (Vector) m_actionListeners.clone();

        if (!v.contains(l)) {

            v.addElement(l);
            m_actionListeners	= v;
        }

    }		// addActionListener

    /**
     *  Fire Action Performed
     */
    private void fireActionPerformed() {

        if ((m_actionListeners == null) || (m_actionListeners.size() == 0)) {
            return;
        }

        ActionEvent	e	= new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "actionPerformed");

        for (int i = 0; i < m_actionListeners.size(); i++) {
            ((ActionListener) m_actionListeners.get(i)).actionPerformed(e);
        }

    }		// fireActionPerformed

    /**
     * **********************************************************************
     *
     * @param args
     */

    /**
     *  Test Method
     *  @param args
     */
    public static void main(String[] args) {

        // System.out.println("CConnectionEditor");
        JFrame	frame	= new JFrame("CConnectionEditor");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getRootPane().getContentPane().add(new CConnectionEditor());
        CompierePLAF.showCenterScreen(frame);
    }		// main

    /**
     * **********************************************************************
     *
     * @param l
     */

    /**
     *  Remove Action Listener
     *  @param l
     */
    public synchronized void removeActionListener(ActionListener l) {

        if ((m_actionListeners != null) && m_actionListeners.contains(l)) {

            Vector	v	= (Vector) m_actionListeners.clone();

            v.removeElement(l);
            m_actionListeners	= v;
        }

    }		// removeActionListener

    //~--- get methods --------------------------------------------------------

    /**
     *  Return Display Value
     *  @return displayed String value
     */
    public String getDisplay() {

        if (m_value == null) {
            return "";
        }

        return m_value.getName();

    }		// getDisplay

    /**
     *      Return Editor value
     *  @return current value
     */
    public Object getValue() {
        return m_value;
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
        return m_rw;
    }		// isReadWrite

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Background based on editable / mandatory / error
     *  @param error if true, set background to error color, otherwise mandatory/editable
     */
    public void setBackground(boolean error) {

        Color	c	= null;

        if (error) {
            c	= CompierePLAF.getFieldBackground_Error();
        } else if (!m_rw) {
            c	= CompierePLAF.getFieldBackground_Inactive();
        } else if (m_mandatory) {
            c	= CompierePLAF.getFieldBackground_Mandatory();
        } else {
            c	= CompierePLAF.getFieldBackground_Normal();
        }

        setBackground(c);

    }		// setBackground

    /**
     *  Set Background color
     *  @param color
     */
    public void setBackground(Color color) {

        m_server.setBackground(color);
        m_text.setBackground(color);
        m_db.setBackground(color);

    }		// setBackground

    /**
     *  Update Display with Connection info
     */
    public void setDisplay() {

        m_text.setText(getDisplay());

        if (m_value == null) {
            return;
        }

        // Text
        if (m_value.isAppsServerOK(false) || m_value.isDatabaseOK()) {

            m_text.setForeground(CompierePLAF.getTextColor_OK());
            setBackground(false);

            if (!m_value.isAppsServerOK(false)) {
                m_server.setBackground(CompierePLAF.getFieldBackground_Error());
            }

            if (!m_value.isDatabaseOK()) {
                m_db.setBackground(CompierePLAF.getFieldBackground_Error());
            }

        } else {

            m_text.setForeground(CompierePLAF.getTextColor_Issue());
            setBackground(true);
        }

    }		// setDisplay

    /**
     *      Set Editor Mandatory
     *  @param mandatory true, if you have to enter data
     */
    public void setMandatory(boolean mandatory) {
        m_mandatory	= mandatory;
    }		// setMandatory

    /**
     *      Enable Editor
     *  @param rw true, if you can enter/select data
     */
    public void setReadWrite(boolean rw) {

        m_rw	= rw;
        setBackground(false);

    }		// setReadWrite

    /**
     *      Set Editor to value
     *  @param value value of the editor
     */
    public void setValue(Object value) {

        if ((value != null) && (value instanceof CConnection)) {
            m_value	= (CConnection) value;
        }

        setDisplay();

    }		// setValue

    /**
     *  Set Visible
     *  @param visible true if field is to be shown
     */
    public void setVisible(boolean visible) {
        this.setVisible(visible);
    }

    /**
     *  MouseListener
     */
    public class CConnectionExitor_MouseListener extends MouseAdapter {

        /** Descripción de Campo */
        private boolean	m_active	= false;

        /**
         *  Mouse Clicked - Open Dialog
         *  @param e
         */
        public void mouseClicked(MouseEvent e) {

            if (!isEnabled() ||!m_rw || m_active) {
                return;
            }

            m_active	= true;
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            //
            if (m_value == null) {
                m_value	= new CConnection();
            }

            CConnectionDialog	cd	= new CConnectionDialog(m_value);

            setValue(cd.getConnection());
            fireActionPerformed();

            //
            setCursor(Cursor.getDefaultCursor());
            m_active	= false;

        }	// mouseClicked
    }		// CConnectionExitor_MouseListener
}		// CConnectionEditor



/*
 * @(#)CConnectionEditor.java   02.jul 2007
 * 
 *  Fin del fichero CConnectionEditor.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
