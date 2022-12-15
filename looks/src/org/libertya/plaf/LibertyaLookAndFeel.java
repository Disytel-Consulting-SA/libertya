package org.libertya.plaf;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.InsetsUIResource;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

public class LibertyaLookAndFeel extends NimbusLookAndFeel {

	public static final Color baseColor = new Color(255,255,255);
	
	public static final Color altColor = new Color(242,242,242);
	
	public static final Color blowColor = new Color(200,200,200);
	
	public static final Color mandatFieldLabelColor = new Color(150,0,0);
	
	public static final Color readOnlyFieldLabelColor = new Color(100,100,100);
	
	public static boolean isActive() {
		return ("Libertya".equals(UIManager.getLookAndFeel().getName()));
	}
	
    public String getDescription() {
        return "Libertya Look&Feel";
    }
    
    @Override
    public String getName() {
    	return "Libertya";
    }
    
    @Override
    public UIDefaults getDefaults() {
    	int margin = 5;
    	InsetsUIResource insets = new InsetsUIResource(margin, margin, margin, margin); // t l b r     	
        UIDefaults	defaults	= super.getDefaults();		// calls init..Defaults

        // Manera custom 1: definiendo aqui los defaults
        // Ej: Tabpane title (cosas menores)
        defaults.put("nimbusBase",baseColor);
        // Ej: Barra de titulo (cosas menores)
        defaults.put("nimbusBlueGrey",baseColor);
        // Ej: Paneles (cosas ppales)
        defaults.put("control",altColor);

        // Entradas de menu
        Color edmColor = new Color(150,150,150);
        defaults.put("MenuItem[MouseOver].textForeground", edmColor);
        defaults.put("Menu[Enabled+Selected].textForeground", edmColor);
        defaults.put("Menu:MenuItemAccelerator[MouseOver].textForeground", edmColor);
        defaults.put("MenuBar:Menu[Selected].textForeground", edmColor);
     
        // Campos de texto
        defaults.put("TextField.contentMargins", insets);
        defaults.put("TextField[Enabled].borderPainter", null);
        //defaults.put("TextField[Focused].borderPainter", null);        
        
        // Campos de password
        defaults.put("PasswordField.contentMargins", insets);
        defaults.put("PasswordField[Enabled].borderPainter", null);
        //defaults.put("PasswordField[Focused].borderPainter", null);
        
        // Comboboxes
        defaults.put("ComboBox[Pressed].backgroundPainter", null);
        defaults.put("ComboBox[Focused+MouseOver].backgroundPainter", null);
        //defaults.put("ComboBox[Focused].backgroundPainter", null);
        
        // BugFix: checkboxes seleccionados pero deshabilitados no se visualizaban seleccionados
        defaults.put("CheckBox[Disabled+Selected].iconPainter", null);
        
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
