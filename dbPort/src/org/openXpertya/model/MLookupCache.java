/*
 * @(#)MLookupCache.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;

//~--- Importaciones JDK ------------------------------------------------------

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *  MLookup Data Cache.
 *  - not synchronized on purpose -
 *  Called from MLookup.
 *  Only caches multiple use for a single window!
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version  $Id: MLookupCache.java,v 1.7 2005/03/11 20:28:38 jjanke Exp $
 */
public class MLookupCache {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MLookupCache.class);

    /** Static Lookup data with MLookupInfo -> HashMap */
    private static CCache	s_loadedLookups	= new CCache("MLookupCache", 50);

    /** ********************************************************************* */

    /**
     *  Private constructor
     */
    private MLookupCache() {}		// MLookupCache

    /**
     *      Clear Static Lookup Cache for Window
     *  @param WindowNo WindowNo of Cache entries to delete
     */
    public static void cacheReset(int WindowNo) {

        String	key	= String.valueOf(WindowNo) + ":";
        int	startNo	= s_loadedLookups.size();

        // find keys of Lookups to delete
        ArrayList	toDelete	= new ArrayList();
        Iterator	iterator	= s_loadedLookups.keySet().iterator();

        while (iterator.hasNext()) {

            String	info	= (String) iterator.next();

            if ((info != null) && info.startsWith(key)) {
                toDelete.add(info);
            }
        }

        // Do the actual delete
        for (int i = 0; i < toDelete.size(); i++) {
            s_loadedLookups.remove(toDelete.get(i));
        }

        int	endNo	= s_loadedLookups.size();

        s_log.fine("cacheReset - WindowNo=" + WindowNo + " - " + startNo + " -> " + endNo);

    }		// cacheReset

    /**
     *  MLookup Loader ends loading, so add it to cache
     *
     *  @param info
     *  @param lookup
     */
    protected static void loadEnd(MLookupInfo info, HashMap lookup) {

        if (info.IsValidated && (lookup.size() > 0)) {
            s_loadedLookups.put(getKey(info), lookup);
        }

    }		// loadEnd

    /**
     *  Load from Cache if applicable
     *  Called from MLookup constructor
     *
     * @param info  MLookupInfo to search
     * @param lookupTarget Target HashMap
     * @return true, if lookup found
     */
    protected static boolean loadFromCache(MLookupInfo info, HashMap lookupTarget) {

        String	key	= getKey(info);
        HashMap	cache	= (HashMap) s_loadedLookups.get(key);

        if (cache == null) {
            return false;
        }

        // Nothing cached
        if (cache.size() == 0) {

            s_loadedLookups.remove(key);

            return false;
        }

        // Copy Asynchronously to speed things up
        // if (cache.size() > ?) copyAsync
        // copy cache
        // we can use iterator, as the lookup loading is complete (i.e. no additional entries)
        Iterator	iterator	= cache.keySet().iterator();

        while (iterator.hasNext()) {

            Object	cacheKey	= iterator.next();
            Object	cacheData	= cache.get(cacheKey);

            lookupTarget.put(cacheKey, cacheData);
        }

        s_log.fine("loadFromCache - #" + lookupTarget.size());

        return true;

    }		// copyLookup

    /**
     *  MLookup Loader starts loading - ignore for now
     *
     *  @param info MLookupInfo
     */
    protected static void loadStart(MLookupInfo info) {}	// loadStart

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Storage Key
     *      @param info lookup info
     *      @return key
     */
    private static String getKey(MLookupInfo info) {

        if (info == null) {
            return String.valueOf(System.currentTimeMillis());
        }

        //
        StringBuffer	sb	= new StringBuffer();

        sb.append(info.WindowNo).append(":")

        // .append(info.Column_ID)
        .append(info.KeyColumn).append(info.AD_Reference_Value_ID).append(info.Query).append(info.ValidationCode);

        // does not include ctx
        return sb.toString();

    }		// getKey
}	// MLookupCache



/*
 * @(#)MLookupCache.java   02.jul 2007
 * 
 *  Fin del fichero MLookupCache.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
