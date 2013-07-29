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



package org.openXpertya.model;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openXpertya.util.CCache;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MImage extends X_AD_Image {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Image_ID
     *
     * @return
     */

    public static MImage get( Properties ctx,int AD_Image_ID ) {
        Integer key      = new Integer( AD_Image_ID );
        MImage  retValue = ( MImage )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MImage( ctx,AD_Image_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "AD_Image",20 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Image_ID
     * @param trxName
     */

    public MImage( Properties ctx,int AD_Image_ID,String trxName ) {
        super( ctx,AD_Image_ID,trxName );

        if( AD_Image_ID < 1 ) {
            setName( "-/-" );
        }
    }    // MImage

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MImage( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MImage

    /** Descripción de Campos */

    private ImageIcon m_image = null;

    /** Descripción de Campos */

    private File m_file = null;

    /**
     * Descripción de Método
     *  Retorna la imagen del adjunto y si no la de la URL
     *
     * @return
     */

    public Image getImage() {
    	
    	
		Image aImage= null;
      	MAttachment attach = getAttachment(true);
      	
      	// Si el attach tiene una imagen, la creamos desde el mismo
      	// sino desde la url que tiene el jar
      	if (attach!=null)
      		{
      		ImageIcon aIcon=null;
      		MAttachmentEntry entry = attach.getEntry(0);
      		byte[] data =  entry.getData();
      		if (data!=null)
      			aIcon = new ImageIcon(data);
      			aImage = aIcon.getImage();
      		}
      	if (aImage==null)
      		{  		
      		URL url = getURL();
            if( url != null ) {
            	Toolkit tk = Toolkit.getDefaultToolkit();
                aImage= tk.getImage( url );    
            }
      }
	return aImage;
	
}    // getImage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Icon getIcon() {
        URL url = getURL();

        if( url == null ) {
            return null;
        }

        return new ImageIcon( url );
    }    // getIcon

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private URL getURL() {
        String str = getImageURL();

        if( (str == null) || (str.length() == 0) ) {
            return null;
        }

        URL url = null;

        try {

            // Try URL directly

            if( str.indexOf( "://" ) != -1 ) {
                url = new URL( str );
            } else {    // Try Resource
                url = getClass().getResource( str );
            }

            //

            if( url == null ) {
                log.warning( "Not found: " + str );
            }
        } catch( Exception e ) {
            log.warning( "Not found: " + str + " - " + e.getMessage());
        }

        return url;
    }    // getURL

	/**
	 * 	Get Data 
	 *	@return data
	 */
	public byte[] getData()
	{
		byte[] data = super.getBinaryData ();
		if (data != null)
			return data;
		//	From URL
		String str = getImageURL();
		if (str == null || str.length() == 0)
		{
			log.config("No Image URL");
			return null;
		}
		//	Get from URL
		URL url = getURL();
		if (url == null)
		{
			log.config("No URL");
			return null;
		}
		try
		{
			URLConnection conn = url.openConnection();
		    conn.setUseCaches(false);
		    InputStream is = conn.getInputStream();
			byte[] buffer = new byte[1024*8];   //  8kB
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int length = -1;
			while ((length = is.read(buffer)) != -1)
				os.write(buffer, 0, length);
			is.close();
			data = os.toByteArray();
			os.close();
		    
		}
		catch (Exception e)
		{
			log.config (e.toString());
		}
		return data;
	}	//	getData

    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "MImage[ID=" + getID() + ",Name=" + getName() + "]";
    }    // toString
}    // MImage



/*
 *  @(#)MImage.java   02.07.07
 * 
 *  Fin del fichero MImage.java
 *  
 *  Versión 2.2
 *
 */
