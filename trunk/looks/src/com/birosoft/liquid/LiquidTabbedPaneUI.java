/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *        Liquid Look and Feel                                                   *
 *                                                                              *
 *  Author, Miroslav Lazarevic                                                  *
 *                                                                              *
 *   For licensing information and credits, please refer to the                 *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
 *                                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import javax.swing.text.View;

import org.compiere.plaf.CompierePLAF;
import org.compiere.plaf.CompiereTabbedPaneUI.TabbedPaneLayout;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.skin.SkinSimpleButtonIndexModel;
import com.birosoft.liquid.util.Colors;


/**
 * This class represents the UI delegate for the JTabbedPane component.
 *
 * @author Taoufik Romdhane
 */
public class LiquidTabbedPaneUI extends BasicTabbedPaneUI {
    static Skin skinTop;
    static Skin skinLeft;
    static Skin skinRight;
    static Skin skinBottom;
    static Skin skinBorder;
    static Skin skinBorderRight;
    SkinSimpleButtonIndexModel indexModel = new SkinSimpleButtonIndexModel();

    /**
     * The outer highlight color of the border.
     */

    //private Color outerHighlight = LiquidDefaultTheme.tabbedPaneBorderColor;

    /**
     * The inner highlight color of the border.
     */

    //private Color innerHighlight = Color.green;

    /**
     * The outer shadow color of the border.
     */

    //private Color outerShadow = Color.blue;

    /**
     * The inner shadow color of the border.
     */
    int rollover = -1;

    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(JComponent c) {
        return new LiquidTabbedPaneUI();
    }

    protected void installListeners() {
        super.installListeners();
        tabPane.addMouseMotionListener((MouseMotionListener) mouseListener);
    }

    protected MouseListener createMouseListener() {
        return new MyMouseHandler();
    }

    
    /**
     *  Create Layout Manager to size & position tabs
     *  @return Layout Manager
     */
    protected LayoutManager createLayoutManager() {
        return new TTabbedPaneLayout();
    }
    
    
    
    private void ensureCurrentLayout() {
        if (!tabPane.isValid()) {
            tabPane.validate();
        }

        /* If tabPane doesn't have a peer yet, the validate() call will
         * silently fail.  We handle that by forcing a layout if tabPane
         * is still invalid.  See bug 4237677.
         */
        if (!tabPane.isValid()) {
            TabbedPaneLayout layout = (TabbedPaneLayout) tabPane.getLayout();
            layout.calculateLayoutInfo();
        }
    }

    private int getTabAtLocation(int x, int y) {
        ensureCurrentLayout();

        int tabCount = tabPane.getTabCount();

        for (int i = 0; i < tabCount; i++) {
            if (rects[i].contains(x, y)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Paints the backround of a given tab.
     *
     * @param g The graphics context.
     * @param tabPlacement The placement of the tab to paint.
     * @param tabIndex The index of the tab to paint.
     * @param x The x coordinate of the top left corner.
     * @param y The y coordinate of the top left corner.
     * @param w The width.
     * @param h The height.
     * @param isSelected True if the tab to paint is selected otherwise false.
     */
    protected void paintTabBackground(Graphics g, int tabPlacement,
        int tabIndex, int x, int y, int w, int h, boolean isSelected) {
    }

    protected void paintFocusIndicator(Graphics g, int tabPlacement,
        Rectangle[] rects, int tabIndex, Rectangle iconRect,
        Rectangle textRect, boolean isSelected) {
    }

    /**
     * Paints the border of a given tab.
     *
     * @param g The graphics context.
     * @param tabPlacement The placement of the tab to paint.
     * @param selectedIndex The index of the selected tab.
     */
    protected void paintContentBorder(Graphics g, int tabPlacement,
        int selectedIndex) {
        /*
        int width = tabPane.getWidth();
        int height = tabPane.getHeight();
        Insets insets = tabPane.getInsets();

        int x = insets.left;
        int y = insets.top;
        int w = width - insets.right - insets.left;
        int h = height - insets.top - insets.bottom;

        // Trick: Need to paint one pixel in the corners!
        //g.setColor(outerHighlight);

        switch (tabPlacement)
        {
           case LEFT :
               x += calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
               w -= (x - insets.left);
               g.drawLine(x + 1, y, x + 1, y);
               break;
           case RIGHT :
               w -= calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
               g.drawLine(x + w - 4, y, x + w - 4, y);
               break;
           case BOTTOM :
           case TOP :
           default :
               y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
               h -= (y - insets.top);
               g.drawLine(x, y + 1, x, y + 1);
        }
        */
    }

    /**
     * Draws the border around each tab.
     *
     * @param g The graphics context.
     * @param tabPlacement The placement of the tabs.
     * @param tabIndex The index of the tab to paint.
     * @param x The x coordinate of the top left corner.
     * @param y The y coordinate of the top left corner.
     * @param w The width.
     * @param h The height.
     * @param isSelected True if the tab to paint is selected otherwise false.
     */
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
        int x, int y, int w, int h, boolean isSelected) {
        Insets contentBorderInsets = getContentBorderInsets(tabPlacement);

        if ((tabPane.getTabPlacement() == BOTTOM) &&
                (contentBorderInsets.top == 5)) {
            contentBorderInsets.top = 0;
            contentBorderInsets.bottom = 5;
            tabPane.revalidate();
        } else if ((tabPane.getTabPlacement() == TOP) &&
                (contentBorderInsets.top == 0)) {
            contentBorderInsets.top = 5;
            contentBorderInsets.bottom = 0;
            tabPane.revalidate();
        }

        //g.setColor(outerHighlight);
        int index = indexModel.getIndexForState(tabPane.isEnabledAt(tabIndex),
                rollover == tabIndex, isSelected);

        switch (tabPlacement) {
        case LEFT:
            getSkinLeft().draw(g, index, x, y, w, h - 1);

            break;

        case RIGHT:
            getSkinRight().draw(g, index, x - 2, y, w, h - 1);

            break;

        case BOTTOM:
            getSkinBottom().draw(g, index, x, y, w, h);

            break;

        case TOP:default:
            getSkinTop().draw(g, index, x, y, w, h);
        }
    }

    protected void paintText(Graphics g, int tabPlacement, Font font,
        FontMetrics metrics, int tabIndex, String title, Rectangle textRect,
        boolean isSelected) {
        int yOffset = 0;

        if ((tabPlacement == TOP) && isSelected) {
            yOffset = 1;
        }

        if (tabPlacement == BOTTOM) {
            yOffset = isSelected ? (-2) : (-1);
        }

        g.setFont(font);

        View v = getTextViewForTab(tabIndex);

        if (v != null) {
            // html
            textRect.y += yOffset;
            v.paint(g, textRect);
        } else {
            // plain text
            int mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);

            if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
                g.setColor(tabPane.getForegroundAt(tabIndex));
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, title,
                    mnemIndex, textRect.x,
                    textRect.y + metrics.getAscent() + yOffset);
            } else { // tab disabled
                g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, title,
                    mnemIndex, textRect.x, textRect.y + metrics.getAscent());
                g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, title,
                    mnemIndex, textRect.x - 1,
                    (textRect.y + metrics.getAscent()) - 1);
            }
        }
    }

    public void paint(Graphics g, JComponent c) {
        Insets contentBorderInsets = getContentBorderInsets(tabPane.getTabPlacement());

        if ((tabPane.getTabPlacement() == BOTTOM) &&
                (contentBorderInsets.top == 5)) {
            contentBorderInsets.top = 0;
            contentBorderInsets.bottom = 5;
            tabPane.revalidate();
        } else if ((tabPane.getTabPlacement() == TOP) &&
                (contentBorderInsets.top == 0)) {
            contentBorderInsets.top = 5;
            contentBorderInsets.bottom = 0;
            tabPane.revalidate();
        }

        int width = tabPane.getWidth();
        int height = tabPane.getHeight();
        Insets insets = tabPane.getInsets();

        int x = insets.left;
        int y = insets.top;
        int w = width - insets.right - insets.left;
        int h = height - insets.top - insets.bottom;

        int tabPlacement = tabPane.getTabPlacement();

        if (tabPlacement == BOTTOM) {
            Color bg = LiquidLookAndFeel.getBackgroundColor();

            if (c.isOpaque()) {
                g.setColor(bg);
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
            }

            if (LiquidLookAndFeel.areStipplesUsed()) {
                Colors.drawStipples(g, c, bg);
            }
        }

        if (tabPlacement == TOP) {
            if (LiquidLookAndFeel.areStipplesUsed()) {
                c.setOpaque(false);
            }
        }

        switch (tabPlacement) {
        case LEFT:
            x += calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
            w -= (x - insets.left);

            break;

        case RIGHT:
            w -= calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);

            break;

        case BOTTOM:
        case TOP:default:
            y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
            h -= (y - insets.top);
        }

        g.drawLine(x, y, x, (y + h) - 3); // left
        g.drawLine(x, y, (x + w) - 3, y); // top

        if (tabPlacement == BOTTOM) {
            getSkinBorder().draw(g, 0, x, h - 5, w, 5);
        }

        if (tabPlacement == TOP) {
            getSkinBorder().draw(g, 0, x, y, w, 5);
        }

        super.paint(g, c);
    }

    public void update(Graphics g, JComponent c) {
        paint(g, c);
    }

    protected int getTabLabelShiftX(int tabPlacement, int tabIndex,
        boolean isSelected) {
        Rectangle tabRect = rects[tabIndex];
        int nudge = 0;

        switch (tabPlacement) {
        case LEFT:
            nudge = isSelected ? (-1) : 1;

            break;

        case RIGHT:
            nudge = isSelected ? 1 : (-1);

            break;

        case BOTTOM:
        case TOP:default:
            nudge = 0;
        }

        return nudge;
    }

    protected int getTabLabelShiftY(int tabPlacement, int tabIndex,
        boolean isSelected) {
        Rectangle tabRect = rects[tabIndex];
        int nudge = 0;

        switch (tabPlacement) {
        case BOTTOM:
            nudge = isSelected ? 1 : (-1);

            break;

        case LEFT:
        case RIGHT:
            nudge = tabRect.height % 2;

            break;

        case TOP:default:
            nudge = isSelected ? (-1) : 1;
        }

        return nudge;
    }

    public Skin getSkinTop() {
        if (skinTop == null) {
            skinTop = new Skin("tabtop.png", 4, 7, 6, 7, 2);
        }

        return skinTop;
    }

    public Skin getSkinLeft() {
        if (skinLeft == null) {
            skinLeft = new Skin("tableft.png", 4, 6, 7, 2, 7);
        }

        return skinLeft;
    }

    public Skin getSkinRight() {
        if (skinRight == null) {
            skinRight = new Skin("tabright.png", 4, 2, 7, 6, 7);
        }

        return skinRight;
    }

    public Skin getSkinBottom() {
        if (skinBottom == null) {
            //skinBottom = new Skin("tabbottom.png", 4, 6, 7, 6, 7);
            skinBottom = new Skin("tabbottom.png", 4, 6, 7, 6, 2);
        }

        return skinBottom;
    }

    public Skin getSkinBorder() {
        if (skinBorder == null) {
            skinBorder = new Skin("tabborderh.png", 1, 5, 2, 5, 2);
        }

        return skinBorder;
    }

    public Skin getSkinBorderRight() {
        if (skinBorderRight == null) {
            skinBorderRight = new Skin("tabborderright.png", 1, 0, 5, 0, 5);
        }

        return skinBorderRight;
    }

    public class MyMouseHandler implements MouseListener, MouseMotionListener {
        public void mousePressed(MouseEvent e) {
            if (!tabPane.isEnabled()) {
                return;
            }

            int tabIndex = getTabAtLocation(e.getX(), e.getY());

            if ((tabIndex >= 0) && tabPane.isEnabledAt(tabIndex)) {
                if (tabIndex == tabPane.getSelectedIndex()) {
                    if (tabPane.isRequestFocusEnabled()) {
                        tabPane.requestFocus();
                        tabPane.repaint(getTabBounds(tabPane, tabIndex));
                    }
                } else {
                    tabPane.setSelectedIndex(tabIndex);
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
            if ((rollover != -1) && (rollover < tabPane.getTabCount())) {
                tabPane.repaint(getTabBounds(tabPane, rollover));
                rollover = -1;
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
            if (tabPane == null) {
                return;
            }

            if (!tabPane.isEnabled()) {
                return;
            }

            int tabIndex = getTabAtLocation(e.getX(), e.getY());

            if ((tabIndex >= 0) && (tabIndex != rollover) && (rollover != -1)) { // Update old rollover

                if ((rollover >= 0) && (rollover < tabPane.getTabCount())) {
                    tabPane.repaint(getTabBounds(tabPane, rollover));
                }

                if (tabIndex == -1) {
                    rollover = -1;
                }
            }

            if ((tabIndex >= 0) && tabPane.isEnabledAt(tabIndex) &&
                    (tabIndex < tabPane.getTabCount())) {
                if (tabIndex == rollover) { // Paint new rollover
                } else {
                    rollover = tabIndex;
                    tabPane.repaint(getTabBounds(tabPane, tabIndex));
                }
            }
        }
    }
    
    /**
     *  Layout Manager to overwrite TabRect size
     */
    public class TTabbedPaneLayout extends LiquidTabbedPaneUI.TabbedPaneLayout {

        /**
         *  Calculate Tab Rectangle Size
         *  @param tabPlacement tab placement
         *  @param tabCount no of tabs
         */
        protected void calculateTabRects(int tabPlacement, int tabCount) {

            super.calculateTabRects(tabPlacement, tabCount);

            if ((tabPlacement == TOP) || (tabPlacement == BOTTOM)) {
                return;
            }

            // System.out.println("calculateTabRects " + tabCount);
            int	tabHeight	= calculateMaxTabHeight(tabPlacement);

            for (int i = 0; i < rects.length; i++) {

                int		level	= 0;
                Component	comp	= tabPane.getComponentAt(i);

                if (comp instanceof JComponent) {

                    JComponent	jc	= (JComponent) comp;

                    try {

                        Integer	ll	= (Integer) jc.getClientProperty(CompierePLAF.TABLEVEL);

                        if (ll != null) {
                            level	= ll.intValue();
                        }

                    } catch (Exception e) {
                        System.err.println("CompiereTabbedPaneUI - ClientProperty: " + e.getMessage());
                    }
                }

                if (level != 0) {

                    if (tabPlacement == LEFT) {
                        rects[i].x	+= level * 5;
                    }

                    rects[i].width	-= level * 5;
                }

                // Height
                rects[i].height	= tabHeight;

                if (i > 0) {
                    rects[i].y	= rects[i - 1].y + tabHeight;		// rects[i-1].height;
                }

            }		// for all rects

        }		// calculate TabRects
    }		// TabbedPaneLayout
}		// CompiereTabbedPaneUI


    

