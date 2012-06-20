/*
 * @(#)CDialog.java   12.oct 2007  Versión 2.2
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

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 *      Conveniance Dialog Class.
 *      OpenXpertya Background + Dispose on Close
 *  Implementing empty Action and Mouse Listener
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: CDialog.java,v 1.5 2005/03/11 20:34:38 jjanke Exp $
 */
public class CDialog extends JDialog implements ActionListener, MouseListener {

    /**
     * Constructor ...
     *
     *
     * @throws HeadlessException
     */
    public CDialog() throws HeadlessException {
        this((Frame) null, false);
    }

    /**
     * Constructor ...
     *
     *
     * @param owner
     *
     * @throws HeadlessException
     */
    public CDialog(Dialog owner) throws HeadlessException {
        this(owner, false);
    }

    /**
     * Constructor ...
     *
     *
     * @param owner
     *
     * @throws HeadlessException
     */
    public CDialog(Frame owner) throws HeadlessException {
        this(owner, false);
    }

    /**
     * Constructor ...
     *
     *
     * @param owner
     * @param modal
     *
     * @throws HeadlessException
     */
    public CDialog(Dialog owner, boolean modal) throws HeadlessException {
        this(owner, null, modal);
    }

    /**
     * Constructor ...
     *
     *
     * @param owner
     * @param title
     *
     * @throws HeadlessException
     */
    public CDialog(Dialog owner, String title) throws HeadlessException {
        this(owner, title, false);
    }

    /**
     * Constructor ...
     *
     *
     * @param owner
     * @param modal
     *
     * @throws HeadlessException
     */
    public CDialog(Frame owner, boolean modal) throws HeadlessException {
        this(owner, null, modal);
    }

    /**
     * Constructor ...
     *
     *
     * @param owner
     * @param title
     *
     * @throws HeadlessException
     */
    public CDialog(Frame owner, String title) throws HeadlessException {
        this(owner, title, false);
    }

    /**
     * Constructor ...
     *
     *
     * @param owner
     * @param title
     * @param modal
     *
     * @throws HeadlessException
     */
    public CDialog(Dialog owner, String title, boolean modal) throws HeadlessException {

        super(owner, title, modal);
        init();
    }

    /**
     * Constructor ...
     *
     *
     * @param owner
     * @param title
     * @param modal
     *
     * @throws HeadlessException
     */
    public CDialog(Frame owner, String title, boolean modal) throws HeadlessException {

        super(owner, title, modal);
        init();
    }

    /**
     * Constructor ...
     *
     *
     * @param owner
     * @param title
     * @param modal
     * @param gc
     *
     * @throws HeadlessException
     */
    public CDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) throws HeadlessException {

        super(owner, title, modal, gc);
        init();
    }

    /**
     * Constructor ...
     *
     *
     * @param owner
     * @param title
     * @param modal
     * @param gc
     */
    public CDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {

        super(owner, title, modal, gc);
        init();
    }

    /**
     * **********************************************************************
     *
     * @param e
     */

    /**
     *      @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     *      @param e
     */
    public void actionPerformed(ActionEvent e) {}

    /**
     *      Initialize
     */
    private void init() {

        CompiereColor.setBackground(this);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"disposeDialog");
        getRootPane().getActionMap().put("disposeDialog", new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				dispose();
			}
        	
        });
    }		// init

    /**
     *      @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     *      @param e
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     *      @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     *      @param e
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     *      @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     *      @param e
     */
    public void mouseExited(MouseEvent e) {}

    /**
     *      @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     *      @param e
     */
    public void mousePressed(MouseEvent e) {}

    /**
     *      @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     *      @param e
     */
    public void mouseReleased(MouseEvent e) {}
}	// CDialog



/*
 * @(#)CDialog.java   02.jul 2007
 * 
 *  Fin del fichero CDialog.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
