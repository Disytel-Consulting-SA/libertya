package org.libertya.plaf;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

import javax.swing.UIDefaults;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

public class LibertyaLookAndFeel extends NimbusLookAndFeel {

	public static final Color baseColor = new Color(255,255,255);
	
	public static final Color altColor = new Color(245,245,245);
	
	public static final Color blowColor = new Color(200,200,200);
	
    public String getDescription() {
        return "Libertya Look&Feel";
    }
    
    @Override
    public String getName() {
    	return "Libertya";
    }
    
    @Override
    public UIDefaults getDefaults() {
        UIDefaults	defaults	= super.getDefaults();		// calls init..Defaults

        // Manera custom 1: definiendo aqui los defaults
        // Ej: Tabpane title (cosas menores)
        defaults.put("nimbusBase",baseColor);
        // Ej: Barra de titulo (cosas menores)
        defaults.put("nimbusBlueGrey",baseColor);
        // Ej: Paneles (cosas ppales)
        defaults.put("control",altColor);

        // Entradas de menu
        defaults.put("MenuItem[MouseOver].textForeground", new Color(150,150,150));
        defaults.put("Menu[Enabled+Selected].textForeground", new Color(150,150,150));
        defaults.put("Menu:MenuItemAccelerator[MouseOver].textForeground", new Color(150,150,150));
        defaults.put("MenuBar:Menu[Selected].textForeground", new Color(150,150,150));
     
        // Campos de texto
        defaults.put("TextField[Enabled].borderPainter", null);
        defaults.put("TextField[Focused].borderPainter", null);
        // Campos de password
        defaults.put("PasswordField[Enabled].borderPainter", null);
        defaults.put("PasswordField[Focused].borderPainter", null);
        // Comboboxes
        //defaults.put("ComboBox[Enabled].backgroundPainter", null);
        defaults.put("ComboBox[Focused].backgroundPainter", null);
        defaults.put("ComboBox[Pressed].backgroundPainter", null);
        defaults.put("ComboBox[Focused+MouseOver].backgroundPainter", null);
      
        // Font por defecto
        Font laFont = null; 
        try {
        	// Cargar font desde file
        	InputStream in = getClass().getResourceAsStream("/Montserrat-Regular.ttf"); 
        	laFont = Font.createFont(Font.TRUETYPE_FONT, in);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(laFont);
            defaults.put("defaultFont", new Font(laFont.getName(), Font.PLAIN, 12));
       } catch (Exception e) {
            e.printStackTrace();
       }
        
        defaults.put("TabbedPaneUI", "org.libertya.plaf.LibertyaTabbedPaneUI");
        defaults.put("ProgressBarUI", "com.birosoft.liquid.LiquidProgressBarUI");
                
        return defaults;
    }
    
    
}
