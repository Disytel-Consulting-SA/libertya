/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.grid.tree;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JTree;

import org.openXpertya.model.MClient;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MyTree extends JTree {

    /**
     * Descripción de Método
     *
     *
     * @param g
     */

	private ImageIcon backgroundImg = null;
	
	private ImageIcon getBackgroundImg() {
		
		if (backgroundImg != null)
			return backgroundImg;
		
		MClient client = MClient.get(Env.getCtx());
		
		if (client.getLogoImg() != null) {
			try {
				ImageIcon ii = new ImageIcon(client.getLogoImg());
				if (ii.getImageLoadStatus() == MediaTracker.COMPLETE) {
					backgroundImg = ii;
					return ii;
				}
			} catch (Exception e) {
				CLogger.get().log(Level.SEVERE, "getBackgroundImg", e);
			}
		}
		
		backgroundImg = Env.getImageIcon( "Background.jpg" );
		
		return backgroundImg;
	}
	
    public void paint( Graphics g ) {
        // ImageIcon image  = Env.getImageIcon( "OXP10030.jpg" );
        ImageIcon image1 = getBackgroundImg();
        ImageIcon image2 = Env.getImageIcon( "empty.jpg" );

        setOpaque( false );

        // First draw the background image - tiled

        Dimension d = getSize();

        /*
         * 
         * Muestra la imagen en mosaico:
         * 
         * if( image1 != null ) {
         * for (int x = 0; x < d.width; x += image1.getIconWidth()) {
         * for (int y = 0; y < d.height; y += image1.getIconHeight()) {
         * g.drawImage(image1.getImage(), x, y, null, null);
         * }
         * }
         * }
         * 
         */
        
        if( image1 != null ) {
        	if( image2 != null ) {
        		g.drawImage(image2.getImage(), 0, 0, d.width, d.height, null, null);
        	}
        	g.drawImage(image1.getImage(), d.width - image1.getIconWidth() - 20, 20, null, null);
        }

        super.paint( g );
    }    // paint
}



/*
 *  @(#)MyTree.java   02.07.07
 * 
 *  Fin del fichero MyTree.java
 *  
 *  Versión 2.2
 *
 */
