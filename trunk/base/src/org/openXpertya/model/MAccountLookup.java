/*
 * @(#)MAccountLookup.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.NamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Account Model Lookup - Maintains ValidCombination Info for Display & Edit - not cached
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *  @version    $Id: MAccountLookup.java,v 1.4 2005/03/11 20:28:36 jjanke Exp $
 */
public final class MAccountLookup extends Lookup implements Serializable {

    /** Descripción de Campo */
    public int	C_ValidCombination_ID;

    /** Descripción de Campo */
    private String	Combination;

    /** Descripción de Campo */
    private String	Description;

    /** Context */
    private Properties	m_ctx;

    /**
     *      Constructor
     *  @parameter ctx context
     *  @parameter WindowNo window no
     *
     * @param ctx
     * @param WindowNo
     */
    public MAccountLookup(Properties ctx, int WindowNo) {

        super(DisplayType.TableDir, WindowNo);
        m_ctx	= ctx;

    }		// MAccountLookup

    /**
     *  The Lookup contains the key
     *  @param key key
     *  @return true if exists
     */
    public boolean containsKey(Object key) {

        int	intValue	= 0;

        if (key instanceof Integer) {
            intValue	= ((Integer) key).intValue();
        } else if (key != null) {
            intValue	= Integer.parseInt(key.toString());
        }

        //
        return load(intValue);

    }		// containsKey

    /**
     *      Load C_ValidCombination with ID
     *  @param ID C_ValidCombination_ID
     *  @return true if found
     */
    public boolean load(int ID) {

        if (ID == 0)	// new
        {

            C_ValidCombination_ID	= 0;
            Combination			= "";
            Description			= "";

            return true;
        }

        if (ID == C_ValidCombination_ID) {	// already loaded
            return true;
        }

        String	SQL	= "SELECT C_ValidCombination_ID, Combination, Description " + "FROM C_ValidCombination WHERE C_ValidCombination_ID=?";

        try {

            // Prepare Statement
            PreparedStatement	pstmt	= DB.prepareStatement(SQL);

            pstmt.setInt(1, ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (!rs.next()) {

                rs.close();
                pstmt.close();

                return false;
            }

            //
            C_ValidCombination_ID	= rs.getInt(1);
            Combination			= rs.getString(2);
            Description			= rs.getString(3);

            //
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            return false;
        }

        return true;

    }		// load

    /**
     *      Return String representation
     *  @return Combination
     */
    public String toString() {

        if (C_ValidCombination_ID == 0) {
            return "";
        }

        return Combination;

    }		// toString

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

        if (!containsKey(value)) {
            return null;
        }

        return new KeyNamePair(C_ValidCombination_ID, toString());

    }		// get

    /**
     *      Get underlying fully qualified Table.Column Name
     *  @return ""
     */
    public String getColumnName() {
        return "";
    }		// getColumnName

    /**
     *      Return data as sorted Array.
     *  Used in Web Interface
     *  @param mandatory mandatory
     *  @param onlyValidated only valid
     *  @param onlyActive only active
     *      @param temporary force load for temporary display
     *  @return ArrayList with KeyNamePair
     */
    public ArrayList getData(boolean mandatory, boolean onlyValidated, boolean onlyActive, boolean temporary) {

        ArrayList	list	= new ArrayList();

        if (!mandatory) {
            list.add(new KeyNamePair(-1, ""));
        }

        //
        StringBuffer	sql	= new StringBuffer("SELECT C_ValidCombination_ID, Combination, Description " + "FROM C_ValidCombination WHERE AD_Client_ID=?");

        if (onlyActive) {
            sql.append(" AND IsActive='Y'");
        }

        sql.append(" ORDER BY 2");

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql.toString());

            pstmt.setInt(1, Env.getAD_Client_ID(m_ctx));

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new KeyNamePair(rs.getInt(1), rs.getString(2) + " - " + rs.getString(3)));
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            log.log(Level.SEVERE, "getData", e);
        }

        // Sort & return
        return list;

    }		// getData

    /**
     *  Get Description
     *  @return Description
     */
    public String getDescription() {
        return Description;
    }		// getDescription

    /**
     *      Get Display for Value
     *  @param value value
     *  @return String
     */
    public String getDisplay(Object value) {

        if (!containsKey(value)) {
            return "<" + value.toString() + ">";
        }

        return toString();

    }		// getDisplay
}	// MAccountLookup



/*
 * @(#)MAccountLookup.java   02.jul 2007
 * 
 *  Fin del fichero MAccountLookup.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
