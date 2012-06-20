/*
 * @(#)CTabbedPane.java   12.oct 2007  Versión 2.2
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
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *  OpenXpertya Color Tabbed Pane
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CTabbedPane.java,v 1.13 2005/03/11 20:34:38 jjanke Exp $
 */
public class CTabbedPane extends JTabbedPane {

    /**
     * Creates an empty <code>TabbedPane</code> with a default
     * tab placement of <code>JTabbedPane.TOP</code> and default
     * tab layout policy of <code>JTabbedPane.WRAP_TAB_LAYOUT</code>.
     * @see #addTab
     */
    public CTabbedPane() {

        super();
        init();

    }		// CTabbedPane

    /**
     * Creates an empty <code>TabbedPane</code> with a defaults and Color
     * @param bg Color
     */
    public CTabbedPane(CompiereColor bg) {

        super();
        init();
        setBackgroundColor(bg);

    }		// CTabbedPane

    /**
     * Creates an empty <code>TabbedPane</code> with the specified tab placement
     * of either: <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
     * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>, and a
     * default tab layout policy of <code>JTabbedPane.WRAP_TAB_LAYOUT</code>.
     *
     * @param tabPlacement the placement for the tabs relative to the content
     */
    public CTabbedPane(int tabPlacement) {

        super(tabPlacement);
        init();

    }		// CTabbedPane

    /**
     * Creates an empty <code>TabbedPane</code> with the specified tab placement
     * and tab layout policy.  Tab placement may be either:
     * <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
     * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>.
     * Tab layout policy may be either: <code>JTabbedPane.WRAP_TAB_LAYOUT</code>
     * or <code>JTabbedPane.SCROLL_TAB_LAYOUT</code>.
     *
     * @param tabPlacement the placement for the tabs relative to the content
     * @param tabLayoutPolicy the policy for laying out tabs when all tabs will not fit on one run
     */
    public CTabbedPane(int tabPlacement, int tabLayoutPolicy) {

        super(tabPlacement, tabLayoutPolicy);
        init();

    }		// CTabbedPane

    /**
     *  Common Init
     */
    private void init() {

        setOpaque(false);
        setFont(CompierePLAF.getFont_Label());
        setForeground(CompierePLAF.getTextColor_Label());

    }		// init

    /**
     * **********************************************************************
     *
     * @param title
     * @param icon
     * @param component
     * @param tip
     * @param index
     */

    /**
     * Insert tab.
     * If the component is a JPanel, the backround is set to the default
     * CompiereColor (and Opaque) if nothing was defined.
     * Redquired as otherwise a gray background would be pained.
     * <p>
     * Inserts a <code>component</code>, at <code>index</code>,
     * represented by a <code>title</code> and/or <code>icon</code>,
     * either of which may be <code>null</code>. If <code>icon</code>
     * is non-<code>null</code> and it implements
     * <code>ImageIcon</code> a corresponding disabled icon will automatically
     * be created and set on the tabbedpane.
     * Uses java.util.Vector internally, see <code>insertElementAt</code>
     * for details of insertion conventions.
     *
     * @param title the title to be displayed in this tab
     * @param icon the icon to be displayed in this tab
     * @param component The component to be displayed when this tab is clicked.
     * @param tip the tooltip to be displayed for this tab
     * @param index the position to insert this new tab
     */
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {

        super.insertTab(title, icon, component, tip, index);

        // Set component background
        if (component instanceof JPanel) {

            JPanel	p	= (JPanel) component;

            if (p.getClientProperty(CompierePLAF.BACKGROUND) == null) {

                CompiereColor.setBackground(p);
                p.setOpaque(true);
            }
        }

    }		// insertTab

    /**
     *  String representation
     *  @return String representation
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("CTabbedPane [");

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

    //~--- set methods --------------------------------------------------------

    /**
     * **********************************************************************
     *
     * @param bg
     */

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
     *  Set Standard Background
     */
    public void setBackgroundColor() {
        setBackgroundColor(null);
    }		// setBackground

    /**
     *  Set Background
     *  @param bg CompiereColor for Background, if null set standard background
     */
    public void setBackgroundColor(CompiereColor bg) {

        if (bg == null) {
            bg	= CompierePanelUI.getDefaultBackground();
        }

        setOpaque(true);
        putClientProperty(CompierePLAF.BACKGROUND, bg);
        super.setBackground(bg.getFlatColor());

        //
        repaint();

    }		// setBackground
}	// CTabbedPane



/*
 * @(#)CTabbedPane.java   02.jul 2007
 * 
 *  Fin del fichero CTabbedPane.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
