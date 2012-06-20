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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.skin.SkinImageCache;

/**
 * This class represents the UI delegate for the JProgressBar component.
 *
 * @author Taoufik Romdhane
 */
public class LiquidProgressBarUI extends BasicProgressBarUI {
    /**
     * The skin that paint the progress bar if it's a horizontal one
     */
    static Skin skinHorizontal;
    /**
     * The skin that paint the progress bar if it's a vertical one
     */
    static Skin skinVertical;
    
        /*
         * The offset of the filled bar. This amount of space will be added on the left and right of the progress bar
         * to its borders.
         */
    int offset=3;
    
    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(JComponent c) {
        return new LiquidProgressBarUI();
    }
    
    protected void paintDeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }
        
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);
        
        int cellLength = getCellLength();
        int cellSpacing = getCellSpacing();
        
        Graphics2D g2 = (Graphics2D) g;
        
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            int amountFull = getAmountFull(b, barRectWidth - offset * 2 , barRectHeight);
            
            getSkinHorizontal().draw(g, 0, barRectWidth, barRectHeight); // draw border
            
            g.translate(offset-1,0);
            BufferedImage img=SkinImageCache.getInstance().getBufferedImage("hprogressbar.png");
            TexturePaint tp = new TexturePaint(img,new Rectangle2D.Float(0.0f,0.0f,img.getWidth(),img.getHeight()));
            
            Paint p=g2.getPaint();
            
            g2.setPaint(tp);
            g2.fillRect(1, 3, amountFull, barRectHeight-6);
            
            g2.setPaint(p);
            if (amountFull > 0) {
                g2.setColor(new Color(179,192,207));
                g2.drawLine(0, 2, 0, barRectHeight-4);
                g2.drawLine(0, 2, amountFull, 2);
                g2.setColor(new Color(195,209,226));
                g2.drawLine(amountFull+1, 2, amountFull+1, barRectHeight-3);
                g2.drawLine(0, barRectHeight-3, amountFull+1, barRectHeight-3);
            }
            
            g.translate(-offset+1,0);
            
            // Deal with possible text painting
            if (progressBar.isStringPainted()) {
                g.setColor(Color.black);
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
            }
            
        } else { // VERTICAL
            int amountFull = getAmountFull(b, barRectWidth, barRectHeight- 2 * offset);
            
            getSkinVertical().draw(g, 0, barRectWidth, barRectHeight);
            BufferedImage img=SkinImageCache.getInstance().getBufferedImage("XPProgressIndicatorVert.res");
            amountFull = (amountFull /img.getHeight() ) * img.getHeight();
            g.translate(0,barRectHeight-offset);
            
            BufferedImage imgL=SkinImageCache.getInstance().getBufferedImage("XPLeftProgressBar.res");
            BufferedImage imgM=SkinImageCache.getInstance().getBufferedImage("XPCenterProgressBar.res");
            BufferedImage imgR=SkinImageCache.getInstance().getBufferedImage("XPRightProgressBar.res");
            
            TexturePaint tpL=new TexturePaint(imgL,new Rectangle2D.Float(0.0f,0.0f,imgL.getWidth(),imgL.getHeight())); //50)); //barRectHeight-1) //50)); //barRectHeight-1) );
            TexturePaint tpM=new TexturePaint(imgM,new Rectangle2D.Float(imgL.getWidth(),0, imgM.getWidth(), imgM.getHeight() )); //50)); //barRectHeight-1) );
            TexturePaint tpR=new TexturePaint(imgR,new Rectangle2D.Float(barRectWidth - imgR.getWidth(), 0, imgR.getWidth(),imgR.getHeight() )); //barRectHeight-imgL.getHeight(),imgM.getWidth(),barRectHeight-imgL.getHeight()));
            
            Paint p=g2.getPaint();
            g2.setPaint(tpL);
            g2.fillRect(0,-amountFull,imgL.getWidth(),amountFull);
            
            g2.setPaint(tpM);
            g2.fillRect(imgL.getWidth(),-amountFull,barRectWidth-imgR.getWidth()-imgL.getWidth(),amountFull);
            
            g2.setPaint(tpR);
            g2.fillRect(barRectWidth-imgR.getWidth(),-amountFull, imgR.getWidth(),amountFull);
            
            g2.setPaint(p);
            g.translate(0,-(barRectHeight-offset) );
            
            // Deal with possible text painting
            if (progressBar.isStringPainted()) {
                g.setColor(Color.black);
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
            }
        }
    }
    
    protected void paintIndeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);
        
        Rectangle boxRect = getBox(null);
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            
            getSkinHorizontal().draw(g, 0, barRectWidth, barRectHeight);
            g.translate(boxRect.x + offset,boxRect.y);
            BufferedImage img = SkinImageCache.getInstance().getBufferedImage("hprogressbar.png");
            TexturePaint tp = new TexturePaint(img,new Rectangle2D.Float(0.0f,0.0f,img.getWidth(),img.getHeight()));
            
            Paint p=g2.getPaint();
            
            g2.setPaint(tp);
            g2.fillRect(1, 3, 20, barRectHeight-6);
            
            g2.setPaint(p);
            g2.setColor(new Color(195,209,226));
            g2.drawLine(20, 2, 20, barRectHeight-4);
            g2.drawLine(0, barRectHeight-3, 20, barRectHeight-3);
            g2.setColor(new Color(179,192,207));
            g2.drawLine(0, 2, 0, barRectHeight-4);
            g2.drawLine(0, 2, 20, 2);

            g.translate(-boxRect.x-offset, -boxRect.y);
            
        } else {
            getSkinVertical().draw(g, 0, barRectWidth, barRectHeight);
            
            
            g.translate(boxRect.x,boxRect.y+offset);
            
            BufferedImage imgL=SkinImageCache.getInstance().getBufferedImage("XPLeftProgressBar.res");
            BufferedImage imgM=SkinImageCache.getInstance().getBufferedImage("XPCenterProgressBar.res");
            BufferedImage imgR=SkinImageCache.getInstance().getBufferedImage("XPRightProgressBar.res");
            
            TexturePaint tpL=new TexturePaint(imgL,new Rectangle2D.Float(0.0f,0.0f,imgL.getWidth(),imgL.getHeight())); //50)); //barRectHeight-1) //50)); //barRectHeight-1) );
            TexturePaint tpM=new TexturePaint(imgM,new Rectangle2D.Float(imgL.getWidth(),0, imgM.getWidth(), imgM.getHeight() )); //50)); //barRectHeight-1) );
            TexturePaint tpR=new TexturePaint(imgR,new Rectangle2D.Float(barRectWidth - imgR.getWidth(), 0, imgR.getWidth(),imgR.getHeight() )); //barRectHeight-imgL.getHeight(),imgM.getWidth(),barRectHeight-imgL.getHeight()));
            
            int h=(boxRect.height - 2* offset)/ imgM.getHeight() * imgM.getHeight();
            
            Paint p=g2.getPaint();
            g2.setPaint(tpL);
            g2.fillRect(0,0, imgL.getWidth(),h);
            
            g2.setPaint(tpM);
            g2.fillRect(imgL.getWidth(),0,barRectWidth-imgR.getWidth()-imgL.getWidth(),h);
            
            g2.setPaint(tpR);
            g2.fillRect(barRectWidth-imgR.getWidth(),0, imgR.getWidth(),h);
            
            g2.setPaint(p);
            
            g.translate(-boxRect.x, -boxRect.y - offset);
        }
        
    }
    
    /**
     * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics, javax.swing.JComponent)
     */
    public void update(Graphics g, JComponent c) {
        paint(g, c);
    }
    
    protected void installDefaults() {
    }
    /**
     * Returns the skinHorizontal.
     * @return SkinGenericButton
     */
    public static Skin getSkinHorizontal() {
        if (skinHorizontal == null) {
            skinHorizontal = new Skin("progressborderhoriz.png", 1, 1, 1, 1, 1);
        }
        
        return skinHorizontal;
    }
    
    /**
     * Returns the skinVerticale.
     * @return Skin
     */
    public static Skin getSkinVertical() {
        if (skinVertical == null) {
            skinVertical = new Skin("progressbordervert.png", 1, 1, 1, 1, 1);
        }
        
        return skinVertical;
    }
    
}
