package org.openXpertya.apps.form;

/**
 * Beta - Ventana independiente para instalacion de plugins
 */

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALogin;
import org.openXpertya.util.Splash;

public class VPluginInstallerFrame extends FormFrame {

	
	
	public static void main(String[] args)
	{
		org.openXpertya.OpenXpertya.startupEnvironment( true );
		
		Splash splash = Splash.getSplash();
		ALogin login = new ALogin( splash );
		
        if( !login.initLogin())                    
        {
            try {
                AEnv.showCenterScreen( login );    
            } catch( Exception ex ) {}

            if( !login.isConnected() ||!login.isOKpressed()) {
                AEnv.exit( 1 );
            }
        }
        splash.setVisible(false);
        
		new VPluginInstallerFrame();
	}
	
	
	
	public VPluginInstallerFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Instalador de Componentes");
		
		VPluginInstaller installer = new VPluginInstaller();
		installer.init(0, this);
		
		JPanel contentPane	= (JPanel) this.getContentPane();
		contentPane.add(installer);
		
		 AEnv.showCenterScreen(this);
	}
	
}
