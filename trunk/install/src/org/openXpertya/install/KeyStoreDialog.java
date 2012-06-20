/*
 * @(#)KeyStoreDialog.java   21.abr 2007  Versión 2.2
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



package org.openXpertya.install;

import org.compiere.swing.CButton;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;

import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.apps.ConfirmPanel;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 21.04.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class KeyStoreDialog extends CDialog {

    /** Descripción de Campo */
    private CLabel	lCN	= new CLabel("(CN) Nombre com" + "\u00fa" + "n");

    /** Descripción de Campo */
    private CTextField	fCN	= new CTextField(20);

    /** Descripción de Campo */
    private CLabel	lOU	= new CLabel("(OU) Unidad organizativa");

    /** Descripción de Campo */
    private CLabel	lO	= new CLabel("(O) Organizaci" + "\u00f3" + "n");

    /** Descripción de Campo */
    private CLabel	lL	= new CLabel("(L) Localidad");

    /** Descripción de Campo */
    private CTextField	fOU	= new CTextField(20);

    /** Descripción de Campo */
    private CTextField	fO	= new CTextField(20);

    /** Descripción de Campo */
    private CTextField	fL	= new CTextField(20);

    /** Descripción de Campo */
    private CLabel	lS	= new CLabel("(S) Estado/Provincia");

    /** Descripción de Campo */
    private CLabel	lC	= new CLabel("(C) Pa" + "\u00ed" + "s (2 Car.)");

    /** Descripción de Campo */
    private CTextField	fS	= new CTextField(20);

    /** Descripción de Campo */
    private CTextField	fC	= new CTextField(2);

    /** Descripción de Campo */
    private CButton	bOK	= ConfirmPanel.createOKButton("OK");

    /** Descripción de Campo */
    private CButton	bCancel	= ConfirmPanel.createCancelButton("Cancelar");

    /** Descripción de Campo */
    private boolean	m_ok	= false;

    /**
     * Constructor ...
     *
     *
     * @param owner
     * @param cn
     * @param ou
     * @param o
     * @param l
     * @param s
     * @param c
     *
     * @throws HeadlessException
     */
    public KeyStoreDialog(JFrame owner, String cn, String ou, String o, String l, String s, String c) throws HeadlessException {

        super(owner, true);
        setTitle("Di"+"\u00e1"+"logo de creaci" + "\u00f3" + "n del certificado (KeyStore)");

        //
        jbInit();
        setValues(cn, ou, o, l, s, c);

        //
        AEnv.showCenterWindow(owner, this);

    }		// KeyStoreDialog

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == bOK) {
            m_ok	= true;
        }

        dispose();

    }		// actionPerformed

    /**
     * Descripción de Método
     *
     */
    private void jbInit() {

        CPanel	panel	= new CPanel(new ALayout());

        panel.add(lCN, new ALayoutConstraint(0, 0));
        panel.add(fCN, null);
        panel.add(lOU, new ALayoutConstraint(1, 0));
        panel.add(fOU, null);
        panel.add(lO, new ALayoutConstraint(2, 0));
        panel.add(fO, null);
        panel.add(lL, new ALayoutConstraint(3, 0));
        panel.add(fL, null);
        panel.add(lS, new ALayoutConstraint(4, 0));
        panel.add(fS, null);
        panel.add(lC, new ALayoutConstraint(5, 0));
        panel.add(fC, null);
        panel.setPreferredSize(new Dimension(400, 150));

        //
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);

        //
        CPanel	confirmPanel	= new CPanel(new FlowLayout(FlowLayout.RIGHT));

        confirmPanel.add(bCancel);
        confirmPanel.add(bOK);
        getContentPane().add(confirmPanel, BorderLayout.SOUTH);

        //
        bCancel.addActionListener(this);
        bOK.addActionListener(this);

    }		// jbInit

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getC() {
        return fC.getText();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getCN() {
        return fCN.getText();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getL() {
        return fL.getText();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getO() {
        return fO.getText();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getOU() {
        return fOU.getText();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getS() {
        return fS.getText();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isOK() {
        return m_ok;
    }		// isOK

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param cn
     * @param ou
     * @param o
     * @param l
     * @param s
     * @param c
     */
    public void setValues(String cn, String ou, String o, String l, String s, String c) {

        fCN.setText(cn);
        fOU.setText(ou);
        fO.setText(o);
        fL.setText(l);
        fS.setText(s);
        fC.setText(c);

    }		// setValues
}	// KeyStoreDialog



/*
 * @(#)KeyStoreDialog.java   21.feb 2007
 * 
 *  Fin del fichero KeyStoreDialog.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 21.feb 2007
