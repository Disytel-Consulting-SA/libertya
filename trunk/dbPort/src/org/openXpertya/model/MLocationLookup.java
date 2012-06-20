/*
 * @(#)MLocationLookup.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import org.openXpertya.util.DisplayType;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.NamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Address Loaction Lookup Model.
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *  @version    $Id: MLocationLookup.java,v 1.10 2005/03/11 20:28:34 jjanke Exp $
 */
public final class MLocationLookup extends Lookup implements Serializable {

    /** Context */
    private Properties	m_ctx;

    /**
     *      Constructor
     *  @param ctx context
     *  @param WindowNo window no (to derive AD_Client/Org for new records)
     */
    public MLocationLookup(Properties ctx, int WindowNo) {

        super(DisplayType.TableDir, WindowNo);
        m_ctx	= ctx;

    }		// MLocation

    /**
     *  The Lookup contains the key
     *  @param key Location_ID
     *  @return true if key known
     */
    public boolean containsKey(Object key) {
        return getLocation(key, null) == null;
    }		// containsKey

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Object of Key Value
     *  @param value value
     *  @return Object or null
     */
    public NamePair get(Object value) {

        if (value == null) {
            return null;
        }

        MLocation	loc	= getLocation(value, null);

        if (loc == null) {
            return null;
        }

        return new KeyNamePair(loc.getC_Location_ID(), loc.toString());

    }		// get

    /**
     *      Get underlying fully qualified Table.Column Name.
     *      Used for VLookup.actionButton (Zoom)
     *  @return column name
     */
    public String getColumnName() {
        return "C_Location_ID";
    }		// getColumnName

    /**
     *      Return data as sorted Array - not implemented
     *  @param mandatory mandatory
     *  @param onlyValidated only validated
     *  @param onlyActive only active
     *      @param temporary force load for temporary display
     *  @return null
     */
    public ArrayList getData(boolean mandatory, boolean onlyValidated, boolean onlyActive, boolean temporary) {

        log.log(Level.SEVERE, "getData - not implemented");

        return null;

    }		// getArray

    /**
     *      Get Display for Value (not cached)
     *  @param value Location_ID
     *  @return String Value
     */
    public String getDisplay(Object value) {

        if (value == null) {
            return null;
        }

        MLocation	loc	= getLocation(value, null);

        if (loc == null) {
            return "<" + value.toString() + ">";
        }

        return loc.toString();

    }		// getDisplay

    /**
     *      Get Location
     *
     * @param C_Location_ID
     * @param trxName
     *      @return Location
     */
    public MLocation getLocation(int C_Location_ID, String trxName) {
        return MLocation.get(m_ctx, C_Location_ID, trxName);
    }		// getC_Location_ID

    /**
     *      Get Location
     *
     * @param key
     * @param trxName
     *      @return Location
     */
    public MLocation getLocation(Object key, String trxName) {

        if (key == null) {
            return null;
        }

        int	C_Location_ID	= 0;

        if (key instanceof Integer) {
            C_Location_ID	= ((Integer) key).intValue();
        } else if (key != null) {
            C_Location_ID	= Integer.parseInt(key.toString());
        }

        //
        return getLocation(C_Location_ID, trxName);

    }		// getLocation
}	// MLocation



/*
 * @(#)MLocationLookup.java   02.jul 2007
 * 
 *  Fin del fichero MLocationLookup.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
