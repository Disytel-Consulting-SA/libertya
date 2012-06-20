/*
 * @(#)MRegion.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.DB;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Localtion Region Model (Value Object)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *  @version    $Id: MRegion.java,v 1.12 2005/03/11 20:28:33 jjanke Exp $
 */
public final class MRegion extends X_C_Region implements Comparator, Serializable {

    /** Region Cache */
    private static CCache	s_regions	= null;

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MRegion.class);

    /** Default Region */
    private static MRegion	s_default	= null;

    /**
     *      Create empty Region
     *      @param ctx context
     *      @param C_Region_ID id
     * @param trxName
     */
    public MRegion(Properties ctx, int C_Region_ID, String trxName) {

        super(ctx, C_Region_ID, trxName);

        if (C_Region_ID == 0) {}

    }		// MRegion

    /**
     *      Create Region from current row in ResultSet
     *      @param ctx context
     *  @param rs result set
     * @param trxName
     */
    public MRegion(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MRegion

    /**
     *  Compare
     *  @param o1 object 1
     *  @param o2 object 2
     *  @return -1,0, 1
     */
    public int compare(Object o1, Object o2) {

        String	s1	= o1.toString();

        if (s1 == null) {
            s1	= "";
        }

        String	s2	= o2.toString();

        if (s2 == null) {
            s2	= "";
        }

        return s1.compareTo(s2);

    }		// compare

    /**
     *      Load Regions (cached)
     *      @param ctx context
     */
    private static void loadAllRegions(Properties ctx) {

        s_regions	= new CCache("C_Region", 100);

        String	sql	= "SELECT * FROM C_Region WHERE IsActive='Y'";

        try {

            Statement	stmt	= DB.createStatement();
            ResultSet	rs	= stmt.executeQuery(sql);

            while (rs.next()) {

                MRegion	r	= new MRegion(ctx, rs, null);

                s_regions.put(String.valueOf(r.getC_Region_ID()), r);

                if (r.isDefault()) {
                    s_default	= r;
                }
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            s_log.log(Level.SEVERE, "loadAllRegions", e);
        }

        s_log.fine("loadAllRegions=" + s_regions.size() + " - default=" + s_default);

    }		// get

    /**
     *      Return Name
     *  @return Name
     */
    public String toString() {
        return getName();
    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Country (cached)
     *      @param ctx context
     *      @param C_Region_ID ID
     *      @return Country
     */
    public static MRegion get(Properties ctx, int C_Region_ID) {

        if ((s_regions == null) || (s_regions.size() == 0)) {
            loadAllRegions(ctx);
        }

        String	key	= String.valueOf(C_Region_ID);
        MRegion	r	= (MRegion) s_regions.get(key);

        if (r != null) {
            return r;
        }

        r	= new MRegion(ctx, C_Region_ID, null);

        if (r.getC_Region_ID() == C_Region_ID) {

            s_regions.put(key, r);

            return r;
        }

        return null;

    }		// get

    /**
     *      Get Default Region
     *      @param ctx context
     *      @return Region or null
     */
    public static MRegion getDefault(Properties ctx) {

        if ((s_regions == null) || (s_regions.size() == 0)) {
            loadAllRegions(ctx);
        }

        return s_default;

    }		// get

    /**
     *      Return Regions as Array
     *      @param ctx context
     *  @return MCountry Array
     */
    public static MRegion[] getRegions(Properties ctx) {

        if ((s_regions == null) || (s_regions.size() == 0)) {
            loadAllRegions(ctx);
        }

        MRegion[]	retValue	= new MRegion[s_regions.size()];

        s_regions.values().toArray(retValue);
        Arrays.sort(retValue, new MRegion(ctx, 0, null));

        return retValue;

    }		// getRegions

    /**
     *      Return Array of Regions of Country
     *      @param ctx context
     *  @param C_Country_ID country
     *  @return MRegion Array
     */
    public static MRegion[] getRegions(Properties ctx, int C_Country_ID) {

        if ((s_regions == null) || (s_regions.size() == 0)) {
            loadAllRegions(ctx);
        }

        ArrayList	list	= new ArrayList();
        Iterator	it	= s_regions.values().iterator();

        while (it.hasNext()) {

            MRegion	r	= (MRegion) it.next();

            if (r.getC_Country_ID() == C_Country_ID) {
                list.add(r);
            }
        }

        // Sort it
        MRegion[]	retValue	= new MRegion[list.size()];

        list.toArray(retValue);
        Arrays.sort(retValue, new MRegion(ctx, 0, null));

        return retValue;

    }		// getRegions
}	// MRegion



/*
 * @(#)MRegion.java   02.jul 2007
 * 
 *  Fin del fichero MRegion.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
