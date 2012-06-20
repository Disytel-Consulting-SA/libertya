/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*	Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;

public class LiquidRadioButtonUI extends BasicRadioButtonUI
{
    /** the only instance of the radiobuttonUI */
    private static final LiquidRadioButtonUI metouiaRadioButtonUI = new LiquidRadioButtonUI();
    /* the only instance of the stroke for the focus */
    private static BasicStroke focusStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[]
    { 1.0f, 1.0f }, 0.0f);
    /* the only instance of the radiobutton icon*/
    private static LiquidRadioButtonIcon skinnedIcon;
    
    /**
     * Creates the singleton for the UI
     * @see javax.swing.plaf.ComponentUI#createUI(JComponent)
     */
    public static ComponentUI createUI(JComponent c)
    {
        if (c instanceof JRadioButton)
        {
            final JRadioButton jb = (JRadioButton) c;
            jb.setRolloverEnabled(true);
            jb.setOpaque(false);
            jb.addPropertyChangeListener("opaque",new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent evt)
                {
                    jb.setOpaque(false);
                }
            });
        }
        return metouiaRadioButtonUI;
    }
    
    /**
     * Installs the icon for the UI
     * @see javax.swing.plaf.ComponentUI#installUI(JComponent)
     */
    public void installUI(JComponent arg0)
    {
        super.installUI(arg0);
        icon = getSkinnedIcon();
    }
    
    /**
     * Returns the skinned Icon
     * @return LiquidRadioButtonIcon
     */
    protected LiquidRadioButtonIcon getSkinnedIcon()
    {
        if (skinnedIcon==null)
            skinnedIcon = new LiquidRadioButtonIcon();
        return skinnedIcon;
    }
    
    
    /**
     * Paints the focus for the radiobutton
     * @see javax.swing.plaf.basic.BasicRadioButtonUI#paintFocus(java.awt.Graphics, java.awt.Rectangle, java.awt.Dimension)
     */
    protected void paintFocus(Graphics g, Rectangle t, Dimension arg2)
    {
        /*
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        
        g2d.setStroke(focusStroke);
        g2d.drawLine(t.x -1, 			 t.y -1, 		       t.x -1 + t.width+1,  t.y -1);
        g2d.drawLine(t.x -1, 			 t.y -1 + t.height+1,    t.x -1 + t.width+1,  t.y -1 + t.height+1);
        g2d.drawLine(t.x -1,   t.y -1, 			   t.x -1, 				 t.y -1 + t.height+1);
        g2d.drawLine(t.x -1 + t.width+1,   t.y -1, 			   t.x -1 + t.width+1, 	 t.y -1 + t.height+1);
         */
    }
}