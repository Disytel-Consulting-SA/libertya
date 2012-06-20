/*
 * @(#)CompierePanelUI.java   12.oct 2007  Versión 2.2
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

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 *  Panel UI.
 *  The default properties can be set via
 *  <pre>
 *  CompierePanelUI.setDefaultBackground (new CompiereColor());
 *  </pre>
 *  The individual Panel can set the background type by setting the
 *  parameter via
 *  <pre>
 *  putClientProperty(CompierePanelUI.BACKGROUND, new CompiereColor());
 *  </pre>
 *  @see org.openXpertya.swing.CPanel
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompierePanelUI.java,v 1.17 2005/03/11 20:34:36 jjanke Exp $
 */
public class CompierePanelUI extends BasicPanelUI {

    /** UI */
    private static CompierePanelUI	s_panelUI	= new CompierePanelUI();

    /** ********************************************************************* */

    /** Default Background */
    private static CompiereColor	s_default	= new CompiereColor();

    /** Set Background to default setting */
    private static boolean	s_setDefault	= false;

    /**
     *  Static Create UI
     *  @param c Vomponent
     *  @return OpenXpertya Panel UI
     */
    public static ComponentUI createUI(JComponent c) {

        // return new CompierePanelUI();
        return s_panelUI;
    }		// createUI

    /**
     * Install Defaults
     * @param p Panel
     */
    protected void installDefaults(JPanel p) {

        super.installDefaults(p);

        /**
         * If enabled, all windows are with OpenXpertya Background,
         *      but Sun dialogs (print ..) are "patchy" as they are opaque              
         * //      System.out.println ("BG=" + p.getClientProperty(CompierePLAF.BACKGROUND));
         * if (s_setDefault || p.getClientProperty(CompierePLAF.BACKGROUND) == null)
         *       p.putClientProperty (CompierePLAF.BACKGROUND, s_default);
         * /** *
         */

    }		// installDefaults

    /**
     *  Update.
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

        // CompiereUtils.printParents (c);
        if (c.isOpaque()) {
            updateIt(g, c);
        }

        paint(g, c);	// does nothing
    }			// update

    /**
     *  Print background based on CompiereColor or flat background if not found
     *  @param g
     *  @param c
     */
    static void updateIt(Graphics g, JComponent c) {

        // System.out.print("Panel " + c.getName());
        // System.out.print(" Bounds=" + c.getBounds().toString());
        // System.out.print(" - Background: ");
        // Get CompiereColor
        CompiereColor	bg	= null;

        try {
            bg	= (CompiereColor) c.getClientProperty(CompierePLAF.BACKGROUND);
        } catch (Exception e) {
            System.err.println("CompierePanelUI - ClientProperty: " + e.getMessage());
        }

        // paint compiere background
        if (bg != null) {

            // System.out.print(bg);
            bg.paint(g, c);
        } else {

            // System.out.print(c.getBackground());
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

        // System.out.println();
    }		// updateIt

    //~--- get methods --------------------------------------------------------

    /**
     *  Get Default Background
     *  @return Background
     */
    public static CompiereColor getDefaultBackground() {
        return s_default;
    }		// getBackground

    /**
     *  Is the Default Background set by default
     *  @return true if default background is set
     */
    public static boolean isSetDefault() {
        return s_setDefault;
    }		// isSetDefault

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Default Background
     *  @param bg Background Color
     */
    public static void setDefaultBackground(CompiereColor bg) {

        if (bg == null) {
            return;
        }

        s_default.setColor(bg);

    }		// setBackground

    /**
     *  Set Default Background
     *  @param setDefault if true, the background will be set to the default color
     */
    public static void setSetDefault(boolean setDefault) {
        s_setDefault	= setDefault;
    }		// setSetDefault
}	// CompierePanel



/*
 * @(#)CompierePanelUI.java   02.jul 2007
 * 
 *  Fin del fichero CompierePanelUI.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
