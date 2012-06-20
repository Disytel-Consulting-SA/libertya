/*
 * @(#)CPanel.java   12.oct 2007  Versión 2.2
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
import org.compiere.plaf.CompierePLAF;
import org.compiere.plaf.CompierePanelUI;

import org.openXpertya.util.Trace;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 *  OpenXpertya Panel supporting colored Backgrounds
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CPanel.java,v 1.15 2005/03/11 20:34:38 jjanke Exp $
 */
public class CPanel extends JPanel {

    /**
     * Creates a new <code>CPanel</code> with a double buffer and a flow layout.
     */
    public CPanel() {

        super();
        init();

    }		// CPanel

    /**
     * Creates a new <code>CPanel</code> with <code>FlowLayout</code>
     * and the specified buffering strategy.
     * If <code>isDoubleBuffered</code> is true, the <code>CPanel</code>
     * will use a double buffer.
     * @param isDoubleBuffered  a boolean, true for double-buffering, which
     *        uses additional memory space to achieve fast, flicker-free updates
     */
    public CPanel(boolean isDoubleBuffered) {

        super(isDoubleBuffered);
        init();

    }		// CPanel

    /**
     * Creates a new <code>CPanel</code> with a double buffer and a flow layout.
     * @param bc Initial Background Color
     */
    public CPanel(CompiereColor bc) {

        this();
        init();
        setBackgroundColor(bc);

    }		// CPanel

    /**
     * Create a new buffered CPanel with the specified layout manager
     * @param layout  the LayoutManager to use
     */
    public CPanel(LayoutManager layout) {

        super(layout);
        init();

    }		// CPanel

    /**
     * Creates a new CompierePanel with the specified layout manager
     * and buffering strategy.
     * @param layout  the LayoutManager to use
     * @param isDoubleBuffered  a boolean, true for double-buffering, which
     *        uses additional memory space to achieve fast, flicker-free updates
     */
    public CPanel(LayoutManager layout, boolean isDoubleBuffered) {

        super(layout, isDoubleBuffered);
        init();

    }		// CPanel

    /**
     *  Common init.
     *  OpenXpertya backround requires that for the base, background is set explictily.
     *  The additional panels should be transparent.
     */
    private void init() {
        setOpaque(false);	// transparent
    }				// init

    /**
     * **********************************************************************
     *
     * @return
     */

    /**
     *  String representation
     *  @return String representation
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("CPanel [");

        sb.append(super.toString());

        CompiereColor	bg	= getBackgroundColor();

        if (bg != null) {
            sb.append(bg.toString());
        }

        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *  Get Background
     *  @return Color for Background
     */
    public CompiereColor getBackgroundColor() {

        try {
            return (CompiereColor) getClientProperty(CompierePLAF.BACKGROUND);
        } catch (Exception e) {
            System.err.println("CPanel - ClientProperty: " + e.getMessage());
        }

        return null;

    }		// getBackgroundColor

    /**
     *  Get Tab Hierarchy Level
     *  @return Tab Level
     */
    public int getTabLevel() {

        try {

            Integer	ll	= (Integer) getClientProperty(CompierePLAF.TABLEVEL);

            if (ll != null) {
                return ll.intValue();
            }

        } catch (Exception e) {
            System.err.println("CPanel - ClientProperty: " + e.getMessage());
        }

        return 0;

    }		// getTabLevel

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Background - ignored by UI -
     *  @param bg ignored
     */
    public void setBackground(Color bg) {

        if (bg.equals(getBackground())) {
            return;
        }

        super.setBackground(bg);

        // ignore calls from javax.swing.LookAndFeel.installColors(LookAndFeel.java:61)
        if (!Trace.getCallerClass(1).startsWith("javax")) {
            setBackgroundColor(new CompiereColor(bg));
        }

    }		// setBackground

    /**
     *  Set Background
     *  @param bg CompiereColor for Background, if null set standard background
     */
    public void setBackgroundColor(CompiereColor bg) {

        if (bg == null) {
            bg	= CompierePanelUI.getDefaultBackground();
        }

        setOpaque(true);	// not transparent
        putClientProperty(CompierePLAF.BACKGROUND, bg);
        super.setBackground(bg.getFlatColor());

    }				// setBackground

    /**
     * **********************************************************************
     *
     * @param level
     */

    /**
     *  Set Tab Hierarchy Level.
     *  Has only effect, if tabs are on left or right side
     *
     *  @param level
     */
    public void setTabLevel(int level) {

        if (level == 0) {
            putClientProperty(CompierePLAF.TABLEVEL, null);
        } else {
            putClientProperty(CompierePLAF.TABLEVEL, new Integer(level));
        }

    }		// setTabLevel
}	// CPanel



/*
 * @(#)CPanel.java   02.jul 2007
 * 
 *  Fin del fichero CPanel.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
