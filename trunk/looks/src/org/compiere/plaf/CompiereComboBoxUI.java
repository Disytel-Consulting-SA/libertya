/*
 * @(#)CompiereComboBoxUI.java   12.oct 2007  Versión 2.2
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

import org.compiere.swing.CComboBox;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxButton;
import javax.swing.plaf.metal.MetalComboBoxUI;

/**
 *  OpenXpertya ComboBox UI.
 *  The ComboBox is opaque - with opaque arrow button and textfield background
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompiereComboBoxUI.java,v 1.8 2005/03/11 20:34:37 jjanke Exp $
 */
public class CompiereComboBoxUI extends MetalComboBoxUI {

    /** ********************************************************************* */
    static int	s_no	= 0;

    /**
     * **********************************************************************
     *
     * @return
     */

    /**
     *  Create opaque button
     *  @return opaque button
     */
    protected JButton createArrowButton() {

        JButton	button	= super.createArrowButton();

        button.setContentAreaFilled(false);
        button.setOpaque(false);

        return button;

    }		// createArrowButton

    /**
     * **********************************************************************
     *
     * @return
     */

    /**
     *  Create Popup
     *  @return CompiereComboPopup
     */
    protected ComboPopup createPopup() {

        CompiereComboPopup	newPopup	= new CompiereComboPopup(comboBox);

        newPopup.getAccessibleContext().setAccessibleParent(comboBox);

        return newPopup;

    }		// createPopup

    /**
     *  Create UI
     *  @param c
     *  @return new instance of CompiereComboBoxUI
     */
    public static ComponentUI createUI(JComponent c) {
        return new CompiereComboBoxUI();
    }		// CreateUI

    /**
     *  Install UI - Set ComboBox opaque.
     *  Bug in Metal: arrowButton gets Mouse Events, so add the JComboBox
     *  MouseListeners to the arrowButton
     *  @see CComboBox#addMouseListener(MouseListener)
     *  @param c componrnt
     */
    public void installUI(JComponent c) {

        MouseListener[]	ml	= c.getMouseListeners();

        super.installUI(c);
        c.setOpaque(false);

        //
        for (int i = 0; i < ml.length; i++) {

            // System.out.println("adding " + c.getClass().getName());
            arrowButton.addMouseListener(ml[i]);
        }

    }		// installUI

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public JButton getArrowButton() {
        return arrowButton;
    }

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Icon  of arrow button
     *  @param defaultIcon
     */
    public void setIcon(Icon defaultIcon) {
        ((MetalComboBoxButton) arrowButton).setComboIcon(defaultIcon);
    }		// setIcon
}	// CompiereComboBoxUI



/*
 * @(#)CompiereComboBoxUI.java   02.jul 2007
 * 
 *  Fin del fichero CompiereComboBoxUI.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
