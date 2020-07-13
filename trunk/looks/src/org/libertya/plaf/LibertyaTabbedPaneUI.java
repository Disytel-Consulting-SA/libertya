package org.libertya.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CPanel;


import sun.swing.SwingUtilities2;

public class LibertyaTabbedPaneUI extends BasicTabbedPaneUI {
	
	protected static final String LEVEL_SEPARATOR = "  ";
	
	  /**
	  *  Static Create UI
	  *  @param c Component
	  *  @return OpenXpertya TabbedPaneUI
	  */
	 public static ComponentUI createUI(JComponent c) {
	     return new LibertyaTabbedPaneUI();
	 }		// createUI
 
 	@Override
	protected void paintContentBorder(Graphics g, int tabPlacement,
			int selectedIndex) {
		// TODO Auto-generated method stub
		// super.paintContentBorder(g, tabPlacement, selectedIndex);
	}
 
 	@Override
 	protected void paintTabBorder(Graphics g, int tabPlacement,
            int tabIndex,
            int x, int y, int w, int h, 
            boolean isSelected ) {
		g.setColor(LibertyaLookAndFeel.blowColor);  
		
		switch (tabPlacement) {
		case LEFT:
			g.drawLine(x+1, y+h-2, x+1, y+h-2); // bottom-left highlight
			g.drawLine(x, y+2, x, y+h-3); // left highlight
			g.drawLine(x+1, y+1, x+1, y+1); // top-left highlight
			g.drawLine(x+2, y, x+w-1, y); // top highlight
//			g.drawLine(x+2, y+h-2, x+w-1, y+h-2); // bottom shadow
//			g.drawLine(x+2, y+h-1, x+w-1, y+h-1); // bottom dark shadow
			break;
		case RIGHT:
			g.drawLine(x, y, x+w-3, y); // top highlight
			g.drawLine(x, y+h-2, x+w-3, y+h-2); // bottom shadow
			g.drawLine(x+w-2, y+2, x+w-2, y+h-3); // right shadow
			g.drawLine(x+w-2, y+1, x+w-2, y+1); // top-right dark shadow
			g.drawLine(x+w-2, y+h-2, x+w-2, y+h-2); // bottom-right dark shadow
			g.drawLine(x+w-1, y+2, x+w-1, y+h-3); // right dark shadow
			g.drawLine(x, y+h-1, x+w-3, y+h-1); // bottom dark shadow
		break;              
		case BOTTOM:
			g.drawLine(x, y, x, y+h-3); // left highlight
			g.drawLine(x+1, y+h-2, x+1, y+h-2); // bottom-left highlight
			g.drawLine(x+2, y+h-2, x+w-3, y+h-2); // bottom shadow
			g.drawLine(x+w-2, y, x+w-2, y+h-3); // right shadow
			g.drawLine(x+2, y+h-1, x+w-3, y+h-1); // bottom dark shadow
			g.drawLine(x+w-2, y+h-2, x+w-2, y+h-2); // bottom-right dark shadow
			g.drawLine(x+w-1, y, x+w-1, y+h-3); // right dark shadow
		break;
		case TOP:
		default:           
			g.drawLine(x, y+2, x, y+h-1); // left highlight
			g.drawLine(x+1, y+1, x+1, y+1); // top-left highlight
//			g.drawLine(x+2, y, x+w-3, y); // top highlight              
//			g.drawLine(x+w-2, y+2, x+w-2, y+h-1); // right shadow
//			g.drawLine(x+w-1, y+2, x+w-1, y+h-1); // right dark-shadow
//			g.drawLine(x+w-2, y+1, x+w-2, y+1); // top-right shadow
	}
}

@Override
	protected void paintTabBackground(Graphics g, int tabPlacement,
			int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		// TODO Auto-generated method stub
		if (isSelected)
			g.setColor(LibertyaLookAndFeel.baseColor);
		else 
			g.setColor(LibertyaLookAndFeel.altColor);
	 switch(tabPlacement) {
	   case LEFT:
	       g.fillRect(x+1, y+1, w-1, h-3);
	       break;
	   case RIGHT:
	       g.fillRect(x, y+1, w-2, h-3);
	       break;
	   case BOTTOM:
	       g.fillRect(x+1, y, w-3, h-1);
	       break;
	   case TOP:
	   default:
	       g.fillRect(x+1, y+1, w-3, h-1);
 	}
 
 
	} 	
 	@Override
    protected void layoutLabel(int tabPlacement, 
            FontMetrics metrics, int tabIndex,
            String title, Icon icon,
            Rectangle tabRect, Rectangle iconRect, 
            Rectangle textRect, boolean isSelected ) {
		textRect.x = textRect.y = iconRect.x = iconRect.y = 0;
		
		View v = getTextViewForTab(tabIndex);
		if (v != null) {
			tabPane.putClientProperty("html", v);
		}
		
		SwingUtilities.layoutCompoundLabel((JComponent) tabPane,
		                        metrics, title, icon,
		                        SwingUtilities.CENTER,
		                        SwingUtilities.LEFT,
		                        SwingUtilities.CENTER,
		                        SwingUtilities.TRAILING,
		                        tabRect,
		                        iconRect,
		                        textRect,
		                        textIconGap);
		
		tabPane.putClientProperty("html", null);
		int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
		int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
		iconRect.x += xNudge;
		iconRect.y += yNudge;
		textRect.x += xNudge;
		textRect.y += yNudge;
		
		tabPane.setOpaque(true);
}
 	
 	
 	@Override
 	protected void paintText(Graphics g, int tabPlacement,
         Font font, FontMetrics metrics, int tabIndex,
         String title, Rectangle textRect, 
         boolean isSelected) {

	 	g.setFont(font);

	 	String newTitle = getTitleWithTabLevel(tabIndex);

	 	View v = getTextViewForTab(tabIndex);
	 	if (v != null) {
	 		// html
	 		v.paint(g, textRect);
	 	} else {
	 		// plain text
	 		int mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);
	 		
	 		if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
	 			Color fg = tabPane.getForegroundAt(tabIndex);
	 			if (isSelected && (fg instanceof UIResource)) {
	 				Color selectedFG = UIManager.getColor("TabbedPane.selectedForeground");
	 				if (selectedFG != null) {
	 					fg = selectedFG;
	 				}
	 			}
	 			g.setColor(fg);
	 			SwingUtilities2.drawStringUnderlineCharAt(tabPane, g, newTitle, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
	 			
	 		} else { // tab disabled
	 			g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
	 			SwingUtilities2.drawStringUnderlineCharAt(tabPane, g, newTitle, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
	 			g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
	 			SwingUtilities2.drawStringUnderlineCharAt(tabPane, g, newTitle, mnemIndex, textRect.x - 1, textRect.y + metrics.getAscent() - 1);
	 			
	 		}
	 	}
 	}  
 
 
	 @Override
		protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
			// TODO Auto-generated method stub
			return (new Float(1.5f * fontHeight)).intValue();
		}
	 
	    protected LayoutManager createLayoutManager() {
	    /*    if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
	            return new TabbedPaneScrollLayout();
	        } else {*/
	            return new TabbedPaneLayout();
	    /*    }*/
	    }	 
	    
	    @Override
    	protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
    		String newTitle = getTitleWithTabLevel(tabIndex);
    		return metrics.stringWidth(newTitle);
    		
    	}
	    
	    protected String getTitleWithTabLevel(int tabIndex) {
	    	String spaces = LEVEL_SEPARATOR;
	    	int level = getTabLevel(tabIndex);
            for (int i = 0; i < level; i++) {
            	spaces = spaces + LEVEL_SEPARATOR;
            }
            return spaces + tabPane.getTitleAt(tabIndex) + LEVEL_SEPARATOR;
	    }
	    
	    protected int getTabLevel(int tabIndex) {
	    	int level = 0;
		 	try {  
		 		 //((GridController)(tabPane.getComponents()[0])).getComponents()
			 	Component	comp	= tabPane.getComponentAt(tabIndex);
			 	JComponent	jc	= (JComponent) comp;
			 	Integer	ll	= (Integer) jc.getClientProperty(CompierePLAF.TABLEVEL);
		        if (ll != null) {
		            level	= ll.intValue();
		        }
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
		 	return level;
	    }
	    
	    
}
















//import java.awt.Color;
//import java.awt.Graphics;
//
//import javax.swing.JComponent;
//import javax.swing.plaf.ComponentUI;
//
//import org.compiere.plaf.CompiereTabbedPaneUI;
//
//public class LibertyaTabbedPaneUI extends CompiereTabbedPaneUI {
//	
//    /**
//     *  Static Create UI
//     *  @param c Component
//     *  @return OpenXpertya TabbedPaneUI
//     */
//    public static ComponentUI createUI(JComponent c) {
//        return new LibertyaTabbedPaneUI();
//    }		// createUI
//	
//    /**
//     *  Install Defaults
//     */
//    protected void installDefaults() {
//
//        super.installDefaults();
//        tabAreaBackground = new Color(255,0,0);
//        selectColor = new Color(255,0,0);
//        selectHighlight = new Color(255,0,0);
////        tabsOpaque = new Color(255,0,0);
////        unselectedBackground = new Color(255,0,0);
//        tabPane.setOpaque(true);
//
//    }		// installDefaults
//    
//	@Override
//	protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
//		System.out.println("paintContentBorder");
//		// super.paintContentBorder(g, tabPlacement, selectedIndex);
//	}
//
//	@Override
//	protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
//			int selectedIndex, int x, int y, int w, int h) {
//		System.out.println("paintContentBorderBottomEdge");
//		//super.paintContentBorderBottomEdge(g, tabPlacement, selectedIndex, x, y, w, h);
//	}
//
//	@Override
//	protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
//			int selectedIndex, int x, int y, int w, int h) {
//		System.out.println("paintContentBorderTopEdge");
//		//super.paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
//	}
//	
//	@Override
//	protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
//			int selectedIndex, int x, int y, int w, int h) {
//		System.out.println("paintContentBorderLeftEdge");
//		//super.paintContentBorderLeftEdge(g, tabPlacement, selectedIndex, x, y, w, h);
//	}
//	
//	@Override
//	protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
//			int selectedIndex, int x, int y, int w, int h) {
//		// TODO Auto-generated method stub
//		System.out.println("paintContentBorderRightEdge");
//		//super.paintContentBorderRightEdge(g, tabPlacement, selectedIndex, x, y, w, h);
//	}
//	
//	
//	
//	@Override
//	protected void paintTopTabBorder(int tabIndex, Graphics g, int x, int y,
//			int w, int h, int btm, int rght, boolean isSelected) {
//		System.out.println("paintTopTabBorder");
//		//super.paintTopTabBorder(tabIndex, g, x, y, w, h, btm, rght, isSelected);
//	}
//	
//	@Override
//	protected void paintLeftTabBorder(int tabIndex, Graphics g, int x, int y,
//			int w, int h, int btm, int rght, boolean isSelected) {
//		//super.paintLeftTabBorder(tabIndex, g, x, y, w, h, btm, rght, isSelected);
//	}
//
//	@Override
//	protected void paintBottomTabBorder(int tabIndex, Graphics g, int x, int y,
//			int w, int h, int btm, int rght, boolean isSelected) {
//		//super.paintBottomTabBorder(tabIndex, g, x, y, w, h, btm, rght, isSelected);
//	}
//	
//	@Override
//	protected void paintRightTabBorder(int tabIndex, Graphics g, int x, int y,
//			int w, int h, int btm, int rght, boolean isSelected) {
//		//super.paintRightTabBorder(tabIndex, g, x, y, w, h, btm, rght, isSelected);
//	}
//
///*	
//	@Override
//	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
//			int x, int y, int w, int h, boolean isSelected) {
//		super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
//	}
//*/	
//
//    protected void paintTabBackground(Graphics g, int tabPlacement,
//                                      int tabIndex,
//                                      int x, int y, int w, int h, 
//                                      boolean isSelected ) {
//        g.setColor(new Color(255,0,0));
//        switch(tabPlacement) {
//          case LEFT:
//              g.fillRect(x+1, y+1, w-1, h-3);
//              break;
//          case RIGHT:
//              g.fillRect(x, y+1, w-2, h-3);
//              break;
//          case BOTTOM:
//              g.fillRect(x+1, y, w-3, h-1);
//              break;
//          case TOP:
//          default:
//              g.fillRect(x+1, y+1, w-3, h-1);
//        }
//    }
//
//}



