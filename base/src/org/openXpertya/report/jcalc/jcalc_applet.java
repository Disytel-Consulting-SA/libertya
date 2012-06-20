package org.openXpertya.report.jcalc;


import java.applet.Applet;
import java.awt.Graphics;

public class jcalc_applet extends Applet {
    boolean started = false;
    
    public void paint(Graphics g){
        if(!started){
            jcalc console = new jcalc(true);
            console.setSize(400,400);
            console.setLocation(300,200);
            console.setVisible(true);
            started = true;
        }
            
    }
}
 