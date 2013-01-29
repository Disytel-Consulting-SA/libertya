/*
 * @(#)MPAttributeLookup.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
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
 *      Product Attribute Lookup Model (not Cached)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MPAttributeLookup.java,v 1.6 2005/04/01 22:35:59 jjanke Exp $
 */
public class MPAttributeLookup extends Lookup implements Serializable {

    /** No Instance Value */
    private static KeyNamePair	NO_INSTANCE	= new KeyNamePair(0, "");

    /** Statement */
    private PreparedStatement	m_pstmt	= null;

    /** Properties */
    private Properties	m_ctx;

    /**
     *      Constructor
     *      @param ctx context
     *      @param WindowNo window no
     */
    public MPAttributeLookup(Properties ctx, int WindowNo) {

        super(DisplayType.TableDir, WindowNo);
        m_ctx	= ctx;

    }		// MPAttribute

    /**
     *  The Lookup contains the key (not cached)
     *  @param key Location_ID
     *  @return true if key known
     */
    public boolean containsKey(Object key) {
        return get(key) != null;
    }		// containsKey

    /**
     *      Dispose
     *      @see org.openXpertya.model.Lookup#dispose()
     */
    public void dispose() {

        try {

            if (m_pstmt != null) {
                m_pstmt.close();
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, "dispose", e);
        }

        log.fine("");
        super.dispose();

    }		// dispose

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

        int	M_AttributeSetInstance_ID	= 0;

        if (value instanceof Integer) {
            M_AttributeSetInstance_ID	= ((Integer) value).intValue();
        } else {

            try {
                M_AttributeSetInstance_ID	= Integer.parseInt(value.toString());
            } catch (Exception e) {
                log.log(Level.SEVERE, "Value=" + value, e);
            }
        }

        if (M_AttributeSetInstance_ID == 0) {
            return NO_INSTANCE;
        }

        //
        // Statement
        if (m_pstmt == null) {
            m_pstmt	= DB.prepareStatement("SELECT Description " + "FROM M_AttributeSetInstance " + "WHERE M_AttributeSetInstance_ID=?");
        }

        //
        String	Description	= null;

        try {

            m_pstmt.setInt(1, M_AttributeSetInstance_ID);

            ResultSet	rs	= m_pstmt.executeQuery();

            if (rs.next()) {

                Description	= rs.getString(1);	// Description

                if ((Description == null) || (Description.length() == 0)) {

                    if (CLogMgt.isLevelFine()) {
                        Description	= "{" + M_AttributeSetInstance_ID + "}";
                    } else {
                        Description	= "";
                    }
                }
            }

            rs.close();

        } catch (Exception e) {
            log.log(Level.SEVERE, "get", e);
        }

        if (Description == null) {
            return null;
        }

        return new KeyNamePair(M_AttributeSetInstance_ID, Description);

    }		// get

    /**
     *      Get underlying fully qualified Table.Column Name.
     *      Used for VLookup.actionButton (Zoom)
     *  @return column name
     */
    public String getColumnName() {
        return "M_AttributeSetInstance_ID";
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

        log.log(Level.SEVERE, "Not implemented");

        return null;

    }		// getArray

    /**
     *      Get Display for Value (not cached)
     *  @param value Location_ID
     *  @return String Value
     */
    public String getDisplay(Object value) {

        if (value == null) {
            return "";
        }

        NamePair	pp	= get(value);

        if (pp == null) {
            return "<" + value.toString() + ">";
        }

        return pp.getName();

    }		// getDisplay
}	// MPAttribute



/*
 * @(#)MPAttributeLookup.java   02.jul 2007
 * 
 *  Fin del fichero MPAttributeLookup.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
