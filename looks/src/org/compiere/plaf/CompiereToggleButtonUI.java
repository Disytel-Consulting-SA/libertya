/*
 * @(#)CompiereToggleButtonUI.java   12.oct 2007  Versión 2.2
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

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalToggleButtonUI;

/**
 *  OpenXpertya Toggle Button UI
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompiereToggleButtonUI.java,v 1.7 2005/03/11 20:34:37 jjanke Exp $
 */
public class CompiereToggleButtonUI extends MetalToggleButtonUI {

    /** UI shared */
    private static CompiereToggleButtonUI	s_toggleButtonUI	= new CompiereToggleButtonUI();

    /**
     *  Static Create UI
     *  @param c Component
     *  @return OpenXpertya ToggleButton UI
     */
    public static ComponentUI createUI(JComponent c) {
        return s_toggleButtonUI;
    }		// createUI

    /**
     * **********************************************************************
     *
     * @param b
     */

    /**
     *  Install Defaults
     *  @param b
     */
    public void installDefaults(AbstractButton b) {

        super.installDefaults(b);
        b.setOpaque(false);

    }		// installDefaults

    /**
     *  Paint 3D Box
     *  @param g Graphics
     *  @param c Component
     */
    public void paint(Graphics g, JComponent c) {

        super.paint(g, c);

        AbstractButton	b	= (AbstractButton) c;
        ButtonModel	model	= b.getModel();
        boolean		in	= model.isPressed() || model.isSelected();

        //
        CompiereUtils.paint3Deffect((Graphics2D) g, c, CompiereLookAndFeel.ROUND, !in);

    }		// paint

    /**
     *  Don't get selected Color - use default (otherwise the pressed button is gray)
     *  @param g
     *  @param b
     */
    protected void paintButtonPressed(Graphics g, AbstractButton b) {

        // if (b.isContentAreaFilled())
        // {
        // Dimension size = b.getSize();
        // g.setColor(getSelectColor());
        // g.fillRect(0, 0, size.width, size.height);
        // }
    }		// paintButtonPressed

    /**
     *  Update -
     *  This method is invoked by <code>JComponent</code> when the specified
     *  component is being painted.
     *
     *  By default this method will fill the specified component with
     *  its background color (if its <code>opaque</code> property is
     *  <code>true</code>) and then immediately call <code>paint</code>.
     *
     *  @param g the <code>Graphics</code> context in which to paint
     *  @param c the component being painted
     *
     *  @see #paint
     *  @see javax.swing.JComponent#paintComponent
     */
    public void update(Graphics g, JComponent c) {

        if (c.isOpaque()) {	// flat background
            CompiereUtils.fillRectange((Graphics2D) g, c, CompiereLookAndFeel.ROUND);
        }

        paint(g, c);

    }		// update
}	// CompiereToggleButton



/*
 * @(#)CompiereToggleButtonUI.java   02.jul 2007
 * 
 *  Fin del fichero CompiereToggleButtonUI.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
