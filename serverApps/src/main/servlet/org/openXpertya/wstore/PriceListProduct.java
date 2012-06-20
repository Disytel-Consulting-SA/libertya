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



package org.openXpertya.wstore;

import java.math.BigDecimal;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PriceListProduct {

    /**
     * Constructor de la clase ...
     *
     *
     * @param M_Product_ID
     * @param value
     * @param name
     * @param description
     * @param help
     * @param documentNote
     * @param imageURL
     * @param descriptionURL
     * @param price
     * @param uomName
     * @param uomSymbol
     */

    public PriceListProduct( int M_Product_ID,String value,String name,String description,String help,String documentNote,String imageURL,String descriptionURL,BigDecimal price,String uomName,String uomSymbol ) {

        //

        m_Product_ID  = M_Product_ID;
        m_value       = value;
        m_name        = name;
        m_description = description;

        // Help, DocumentNote, ImageURL, DescriptionURL,

        m_help           = help;
        m_documentNote   = documentNote;
        m_imageURL       = imageURL;
        m_descriptionURL = descriptionURL;

        // PriceStd, UOMName, UOMSymbol

        m_price     = price;
        m_uomName   = uomName;
        m_uomSymbol = uomSymbol;
    }    // PriceListProduct

    /** Descripción de Campos */

    public static final String NAME = "PriceListProduct";

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private int m_Product_ID;

    /** Descripción de Campos */

    private String m_value;

    /** Descripción de Campos */

    private String m_name;

    /** Descripción de Campos */

    private String m_description;

    /** Descripción de Campos */

    private String m_help;

    /** Descripción de Campos */

    private String m_documentNote;

    /** Descripción de Campos */

    private String m_imageURL;

    /** Descripción de Campos */

    private String m_descriptionURL;

    /** Descripción de Campos */

    private BigDecimal m_price;

    /** Descripción de Campos */

    private String m_uomName;

    /** Descripción de Campos */

    private String m_uomSymbol;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "PriceListProduct[" );

        sb.append( m_Product_ID ).append( "-" ).append( m_name ).append( "-" ).append( m_price ).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getId() {
        return m_Product_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getValue() {
        return m_value;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_name;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_description;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getHelp() {
        return m_help;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDocumentNote() {
        return m_documentNote;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getImageURL() {
        return m_imageURL;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescriptionURL() {
        return m_descriptionURL;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getPrice() {
        return m_price;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getUomName() {
        return m_uomName;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getUomSymbol() {
        return m_uomSymbol;
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

	public BigDecimal getDisponible() {
        //	Almacén central del polígono de ASIPO.
		// int Almacen_ID = 1000000; // no puede ser cero, tiene que ser el almacen. 
		BigDecimal disponible = org.openXpertya.model.MStorage.getQtyAvailable(0,m_Product_ID);
		BigDecimal futura = org.openXpertya.model.MStorage.getQtyVirtual(0,m_Product_ID);
		// Empezamos a calcular
		BigDecimal minimo = new BigDecimal("+1");
		BigDecimal cero = new BigDecimal("0");
		BigDecimal negativo = new BigDecimal("-1");
		if (disponible != null){
        //			 si disponible no es nulo	
		if (disponible.compareTo(minimo)<0){
        //			 si es menor que cero hay que hacer cosillas
		//           le sumo las entradas futuras
			//log.info("GetDisponible - Disponibilidad antes de sumar: " + disponible +".");
			disponible = disponible.add(futura);
			//log.info("GetDisponible - Disponibilidad futura sumada: " + disponible +".");
			// si sigue saliendo menor que el minimo (o sea uno)
			if (disponible.compareTo(minimo)<0){
				disponible = cero;
			    //log.info("GetDisponible - Sigue sin haber bastante: " + disponible +".");
			    }
			    else {
			    	// hay disponibilidad futura
			    	//log.info("GetDisponible - Teniendo en cuenta lo que entra: " + disponible +".");
			    	disponible = disponible.negate();
			    	//log.info("GetDisponible - Cambio de signo del valor: " + disponible +".");
			    	}
			    }	
		    }
		// disponible es nulo, luego es cero
		else disponible = cero;
		return disponible;
	}
}    // PriceListProduct



/*
 *  @(#)PriceListProduct.java   12.10.07
 * 
 *  Fin del fichero PriceListProduct.java
 *  
 *  Versión 2.2
 *
 */
