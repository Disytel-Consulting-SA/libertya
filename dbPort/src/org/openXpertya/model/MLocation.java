/*
 * @(#)MLocation.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Loaction (Address)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MLocation.java,v 1.31 2005/05/08 15:17:13 jjanke Exp $
 */
public class MLocation extends X_C_Location implements Comparator {

    /** Cache */
    private static CCache	s_cache	= new CCache("C_Location", 100, 30);

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MLocation.class);

    /** Descripción de Campo */
    private MCountry	m_c	= null;

    /** Descripción de Campo */
    private MRegion	m_r	= null;

    /**
     *      Parent Constructor
     *      @param country mandatory country
     *      @param region optional region
     */
    public MLocation(MCountry country, MRegion region) {

        super(country.getCtx(), 0, country.get_TrxName());
        setCountry(country);
        setRegion(region);

    }		// MLocation

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param C_Location_ID id
     * @param trxName
     */
    public MLocation(Properties ctx, int C_Location_ID, String trxName) {

        super(ctx, C_Location_ID, trxName);

        if (C_Location_ID == 0) {

            MCountry	defaultCountry	= MCountry.getDefault(getCtx());

            setCountry(defaultCountry);

            MRegion	defaultRegion	= MRegion.getDefault(getCtx());

            if ((defaultRegion != null) && (defaultRegion.getC_Country_ID() == defaultCountry.getC_Country_ID())) {
                setRegion(defaultRegion);
            }
        }

    }		// MLocation

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MLocation(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MLocation

    /**
     *      Full Constructor
     *      @param ctx context
     *      @param C_Country_ID country
     *      @param C_Region_ID region
     *      @param city city
     * @param trxName
     */
    public MLocation(Properties ctx, int C_Country_ID, int C_Region_ID, String city, String trxName) {

        super(ctx, 0, trxName);
        setC_Country_ID(C_Country_ID);
        setC_Region_ID(C_Region_ID);
        setCity(city);

    }		// MLocation
    
    /**
     * Get locations of client
     * @param ctx
     * @param trxName
     * @return
     */
    public static List<MLocation> getOfClient(Properties ctx,String trxName){
    	//script sql
    	String sql = "SELECT * FROM c_location WHERE ad_client_id = ? "; 
    		
    	List<MLocation> list = new ArrayList<MLocation>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MLocation(ctx,rs,trxName));	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
    }

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        // Value/Name change in Account
        if (!newRecord && ("Y".equals(Env.getContext(getCtx(), "$Element_LF")) || "Y".equals(Env.getContext(getCtx(), "$Element_LT"))) && (is_ValueChanged("Postal") || is_ValueChanged("City"))) {
            MAccount.updateValueDescription(getCtx(), "(C_LocFrom_ID=" + getC_Location_ID() + " OR C_LocTo_ID=" + getC_Location_ID() + ")", get_TrxName());
        }

        return success;
    }		// afterSave

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true
     */
    protected boolean beforeSave(boolean newRecord) {

        // Region Check
        if (getC_Region_ID() != 0) {

            if ((m_c == null) || (m_c.getC_Country_ID() != getC_Country_ID())) {
                getCountry();
            }

            if (!m_c.isHasRegion()) {
                setC_Region_ID(0);
            }
        }

        return true;
    }		// geforeSave

    /**
     *      Equals
     *      @param cmp comperator
     *      @return true if ID the same
     */
    public boolean equals(Object cmp) {

        if (cmp == null) {
            return false;
        }

        if (cmp.getClass().equals(this.getClass())) {
            return ((PO) cmp).getID() == getID();
        }

        return equals(cmp);

    }		// equals

    /**
     *      Compares to current record
     *      @param C_Country_ID if 0 ignored
     *      @param C_Region_ID if 0 ignored
     *      @param Postal match postal
     *      @param Postal_Add match postal add
     *      @param City match city
     *      @param Address1 match address 1
     *      @param Address2 match addtess 2
     *      @return true if equals
     */
    public boolean equals(int C_Country_ID, int C_Region_ID, String Postal, String Postal_Add, String City, String Address1, String Address2) {

        if ((C_Country_ID != 0) && (getC_Country_ID() != C_Country_ID)) {
            return false;
        }

        if ((C_Region_ID != 0) && (getC_Region_ID() != C_Region_ID)) {
            return false;
        }

        // must match
        if (!equalsNull(Postal, getPostal())) {
            return false;
        }

        if (!equalsNull(Postal_Add, getPostal_Add())) {
            return false;
        }

        if (!equalsNull(City, getCity())) {
            return false;
        }

        if (!equalsNull(Address1, getAddress1())) {
            return false;
        }

        if (!equalsNull(Address2, getAddress2())) {
            return false;
        }

        return true;

    }		// equals

    /**
     *      Equals if "" or Null
     *      @param c1 c1
     *      @param c2 c2
     *      @return true if equal (ignore case)
     */
    private boolean equalsNull(String c1, String c2) {

        if (c1 == null) {
            c1	= "";
        }

        if (c2 == null) {
            c2	= "";
        }

        return c1.equalsIgnoreCase(c2);

    }		// equalsNull

    /**
     *      Parse according Ctiy/Postal/Region according to displaySequence.
     *      @C@ - City              @R@ - Region    @P@ - Postal  @A@ - PostalAdd
     *  @param c country
     *  @return parsed String
     */
    private String parseCRP(MCountry c) {

        if (c == null) {
            return "CountryNotFound";
        }

        boolean	local	= getC_Country_ID() == MCountry.getDefault(getCtx()).getC_Country_ID();
        String		inStr	= local
                                  ? c.getDisplaySequenceLocal()
                                  : c.getDisplaySequence();
        StringBuffer	outStr	= new StringBuffer();
        String		token;
        int		i	= inStr.indexOf("@");

        while (i != -1) {

            outStr.append(inStr.substring(0, i));			// up to @
            inStr	= inStr.substring(i + 1, inStr.length());	// from first @

            int	j	= inStr.indexOf("@");				// next @

            if (j < 0) {

                token	= "";						// no second tag
                j	= i + 1;

            } else {
                token	= inStr.substring(0, j);
            }

            // Tokens
            if (token.equals("C")) {

                if (getCity() != null) {
                    outStr.append(getCity());
                }

            } else if (token.equals("R")) {

                if (getRegion() != null) {				// we have a region
                    outStr.append(getRegion().getName());
                } else if ((super.getRegionName() != null) && (super.getRegionName().length() > 0)) {
                    outStr.append(super.getRegionName());		// local region name
                }

            } else if (token.equals("P")) {

                if (getPostal() != null) {
                    outStr.append(getPostal());
                }

            } else if (token.equals("A")) {

                String	add	= getPostal_Add();

                if ((add != null) && (add.length() > 0)) {
                    outStr.append("-").append(add);
                }

            } else {
                outStr.append("@").append(token).append("@");
            }

            inStr	= inStr.substring(j + 1, inStr.length());	// from second @
            i		= inStr.indexOf("@");
        }

        outStr.append(inStr);		// add the rest of the string

        // Print Region Name if entered and not part of pattern
        if ((c.getDisplaySequence().indexOf("@R@") == -1) && (super.getRegionName() != null) && (super.getRegionName().length() > 0)) {
            outStr.append(" ").append(super.getRegionName());
        }

        String	retValue	= Util.replace(outStr.toString(), "\\n", "\n");

        log.finest("parseCRP - " + c.getDisplaySequence() + " -> " + retValue);

        return retValue;

    }		// parseContext

    /**
     *      Return String representation
     *  @return String
     */
    public String toString() {

        StringBuffer	retStr	= new StringBuffer();

        if (getAddress1() != null) {
            retStr.append(getAddress1());
        }

        if ((getAddress2() != null) && (getAddress2().length() > 0)) {
            retStr.append(" ").append(getAddress2());
        }

        // City, Region, Postal
        retStr.append(", ").append(parseCRP(getCountry()));

        //
        return retStr.toString();

    }		// toString

    /**
     *      Return String representation with CR at line end
     *  @return String
     */
    public String toStringCR() {

        StringBuffer	retStr	= new StringBuffer();

        if (getAddress1() != null) {
            retStr.append(getAddress1());
        }

        if ((getAddress2() != null) && (getAddress2().length() > 0)) {
            retStr.append("\n").append(getAddress2());
        }

        if ((getAddress3() != null) && (getAddress3().length() > 0)) {
            retStr.append("\n").append(getAddress3());
        }

        if ((getAddress4() != null) && (getAddress4().length() > 0)) {
            retStr.append("\n").append(getAddress4());
        }

        // City, Region, Postal
        retStr.append("\n").append(parseCRP(getCountry()));

        // Add Country would come here
        return retStr.toString();

    }		// toStringCR

    /**
     *      Return detailed String representation
     *  @return String
     */
    public String toStringX() {

        StringBuffer	sb	= new StringBuffer("MLocation=[");

        sb.append(getID()).append(",C_Country_ID=").append(getC_Country_ID()).append(",C_Region_ID=").append(getC_Region_ID()).append(",Postal=").append(getPostal()).append("]");

        return sb.toString();

    }		// toStringX

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Location from Cache
     *      @param ctx context
     *      @param C_Location_ID id
     * @param trxName
     *      @return MLocation
     */
    public static MLocation get(Properties ctx, int C_Location_ID, String trxName) {

        // New
        if (C_Location_ID == 0) {
            return new MLocation(ctx, C_Location_ID, trxName);
        }

        //
        Integer		key		= new Integer(C_Location_ID);
        MLocation	retValue	= (MLocation) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new MLocation(ctx, C_Location_ID, trxName);

        if (retValue.getID() != 0)	// found
        {

            s_cache.put(key, retValue);

            return retValue;
        }

        return null;	// not found
    }			// get

    /**
     *      Load Location with ID if Business Partner Location
     *
     * @param ctx
     *  @param C_BPartner_Location_ID Business Partner Location
     * @param trxName
     *  @return loaction or null
     */
    public static MLocation getBPLocation(Properties ctx, int C_BPartner_Location_ID, String trxName) {

        if (C_BPartner_Location_ID == 0) {	// load default
            return null;
        }

        MLocation	loc	= null;
        String		sql	= "SELECT * FROM C_Location l " + "WHERE C_Location_ID=(SELECT C_Location_ID FROM C_BPartner_Location WHERE C_BPartner_Location_ID=?)";

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql, trxName);

            pstmt.setInt(1, C_BPartner_Location_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                loc	= new MLocation(ctx, rs, trxName);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {

            s_log.log(Level.SEVERE, "getBPLocation - " + C_BPartner_Location_ID, e);
            loc	= null;
        }

        return loc;

    }		// getBPLocation

    /**
     *      Get formatted City Region Postal line
     *      @return City, Region Postal
     */
    public String getCityRegionPostal() {
        return parseCRP(getCountry());
    }		// getCityRegionPostal

    /**
     *      Get Country
     *      @return country
     */
    public MCountry getCountry() {

        if (m_c == null) {

            if (getC_Country_ID() != 0) {
                m_c	= MCountry.get(getCtx(), getC_Country_ID());
            } else {
                m_c	= MCountry.getDefault(getCtx());
            }
        }

        return m_c;

    }		// getCountry

    /**
     *      Get Country Line
     *      @param local if true only foreign country is returned
     *      @return country or null
     */
    public String getCountry(boolean local) {

        if (local && (getC_Country_ID() == MCountry.getDefault(getCtx()).getC_Country_ID())) {
            return null;
        }

        return getCountryName();

    }		// getCountry

    /**
     *      Get Country Name
     *      @return Country Name
     */
    public String getCountryName() {
        return getCountry().getName();
    }		// getCountryName

    /**
     *      Get Region
     *      @return region
     */
    public MRegion getRegion() {

        if ((m_r == null) && (getC_Region_ID() != 0)) {
            m_r	= MRegion.get(getCtx(), getC_Region_ID());
        }

        return m_r;

    }		// getRegion

    /**
     *      Get (local) Region Name
     *      @return region Name or ""
     */
    public String getRegionName() {
        return getRegionName(false);
    }		// getRegionName

    /**
     *      Get Region Name
     *      @param getFromRegion get from region (not locally)
     *      @return region Name or ""
     */
    public String getRegionName(boolean getFromRegion) {

        if (getFromRegion && getCountry().isHasRegion() && (getRegion() != null)) {

            super.setRegionName("");	// avoid duplicates

            return getRegion().getName();
        }

        //
        String	regionName	= super.getRegionName();

        if (regionName == null) {
            regionName	= "";
        }

        return regionName;

    }		// getRegionName

    /**
     *      Print Address Reverse Order
     *      @return true if reverse depending on country
     */
    public boolean isAddressLinesReverse() {

        // Local
        if (getC_Country_ID() == MCountry.getDefault(getCtx()).getC_Country_ID()) {
            return getCountry().isAddressLinesLocalReverse();
        }

        return getCountry().isAddressLinesReverse();
    }		// isAddressLinesReverse

    //~--- set methods --------------------------------------------------------

    /**
     *      Set C_Country_ID
     *      @param C_Country_ID id
     */
    public void setC_Country_ID(int C_Country_ID) {
        setCountry(MCountry.get(getCtx(), C_Country_ID));
    }		// setCountry

    /**
     *      Set C_Region_ID
     *      @param C_Region_ID region
     */
    public void setC_Region_ID(int C_Region_ID) {

        if (C_Region_ID == 0) {
            setRegion(null);
        } else {
            setRegion(MRegion.get(getCtx(), C_Region_ID));
        }

    }		// setC_Region_ID

    /**
     *      Set Country
     *      @param country
     */
    public void setCountry(MCountry country) {

        if (country != null) {
            m_c	= country;
        } else {
            m_c	= MCountry.getDefault(getCtx());
        }

        super.setC_Country_ID(m_c.getC_Country_ID());

    }		// setCountry

    /**
     *      Set Region
     *      @param region
     */
    public void setRegion(MRegion region) {

        m_r	= region;

        if (region == null) {
            super.setC_Region_ID(0);
        } else {

            super.setC_Region_ID(m_r.getC_Region_ID());

            if (m_r.getC_Country_ID() != getC_Country_ID()) {

                log.warning("setRegion - Region(" + region + ") C_Country_ID=" + region.getC_Country_ID() + " overwriting Location.C_Country_ID=" + getC_Country_ID());
                setC_Country_ID(region.getC_Country_ID());
            }
        }

    }		// setRegion
    
    public String toStringShort() {
		StringBuffer loc = new StringBuffer();
		boolean needSep = false;
		if (getAddress1() != null && !getAddress1().isEmpty()) {
			loc.append(getAddress1());
			needSep = true;
		} else if (getAddress2() != null && !getAddress2().isEmpty()) {
			loc.append(getAddress2());
			needSep = true;
		}
		
		if (getC_City_ID() > 0) {
			
			String cityName = DB.getSQLValueString(get_TrxName(), "SELECT Name FROM C_City WHERE C_City_ID = ?", getC_City_ID());
			if (cityName != null) {
				loc.append(needSep ? ", ":"").append(cityName);
				needSep = true;
			}
		} else if (getCity() != null && !getCity().isEmpty()) {
			loc.append(needSep ?", ":"").append(getCity());
		} else if (getC_Region_ID() > 0) {
			loc.append(needSep ? ", ":"").append(MRegion.get(getCtx(), getC_Region_ID()).getName());
		}
		
		return loc.toString();
    }
}	// MLocation



/*
 * @(#)MLocation.java   02.jul 2007
 * 
 *  Fin del fichero MLocation.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
