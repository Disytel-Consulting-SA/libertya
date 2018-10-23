/*
 * @(#)MLocator.java   12.oct 2007  Versión 2.2
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Properties;
import java.util.logging.Level;

/**
 *      Warehouse Locator Object
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *  @version    $Id: MLocator.java,v 1.7 2005/05/30 02:33:05 jjanke Exp $
 */
public class MLocator extends X_M_Locator {

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MLocator.class);

    /** Cache */
    private static CCache	s_cache;

    /**
     *      New Locator Constructor with XYZ=000
     *      @param warehouse parent
     *      @param Value value
     */
    public MLocator(MWarehouse warehouse, String Value) {

        this(warehouse.getCtx(), 0, warehouse.get_TrxName());
        setClientOrg(warehouse);
        setM_Warehouse_ID(warehouse.getM_Warehouse_ID());	// Parent
        setValue(Value);
        setXYZ("0", "0", "0");

    }								// MLocator

    /**
     *      Standard Locator Constructor
     *      @param ctx Context
     *      @param M_Locator_ID id
     * @param trxName
     */
    public MLocator(Properties ctx, int M_Locator_ID, String trxName) {

        super(ctx, M_Locator_ID, trxName);

        if (M_Locator_ID == 0) {

            // setM_Locator_ID (0);            //      PK
            // setM_Warehouse_ID (0);          //      Parent
            setIsDefault(false);
            setPriorityNo(50);

            // setValue (null);
            // setX (null);
            // setY (null);
            // setZ (null);
        }

    }		// MLocator

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MLocator(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MLocator

    /**
     *      Get String Representation
     *      @return Value
     */
    public String toString() {
        return getValue();
    }		// getValue

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Locator from Cache
     *      @param ctx context
     *      @param M_Locator_ID id
     *      @return MLocator
     */
    public static MLocator get(Properties ctx, int M_Locator_ID) {

        if (s_cache == null) {
            s_cache	= new CCache("M_Locator", 20);
        }

        Integer		key		= new Integer(M_Locator_ID);
        MLocator	retValue	= (MLocator) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new MLocator(ctx, M_Locator_ID, null);

        if (retValue.getID() != 0) {
            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get

    /**
     *      Get the Locator with the combination or create new one
     *      @param ctx Context
     *      @param M_Warehouse_ID warehouse
     *      @param Value value
     *      @param X x
     *      @param Y y
     *      @param Z z
     *      @return locator
     */
    public static MLocator get(Properties ctx, int M_Warehouse_ID, String Value, String X, String Y, String Z) {

        MLocator	retValue	= null;
        String		sql		= "SELECT * FROM M_Locator WHERE M_Warehouse_ID=? AND X=? AND Y=? AND Z=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, M_Warehouse_ID);
            pstmt.setString(2, X);
            pstmt.setString(3, Y);
            pstmt.setString(4, Z);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new MLocator(ctx, rs, null);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {
            s_log.log(Level.SEVERE, "get", ex);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException ex1) {}

        pstmt	= null;

        //
        if (retValue == null) {

            MWarehouse	wh	= MWarehouse.get(ctx, M_Warehouse_ID);

            retValue	= new MLocator(wh, Value);
            retValue.setXYZ(X, Y, Z);
            retValue.save();
        }

        return retValue;

    }		// get

    /**
     *      Get oldest Default Locator of warehouse with locator
     *      @param ctx context
     *      @param M_Locator_ID locator
     *      @return locator or null
     */
    public static MLocator getDefault(Properties ctx, int M_Locator_ID) {

        String		trxName		= null;
        MLocator	retValue	= null;
        String		sql		= "SELECT * FROM M_Locator l " + "WHERE IsDefault='Y'" + " AND EXISTS (SELECT * FROM M_Locator lx " + "WHERE l.M_Warehouse_ID=lx.M_Warehouse_ID AND lx.M_Locator_ID=?) " + "ORDER BY Created";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, trxName);
            pstmt.setInt(1, M_Locator_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                retValue	= new MLocator(ctx, rs, trxName);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return retValue;

    }		// getDefault

	/**
	 * Obtener la ubicación del depósito parámetro. Dependiendo del parámetro
	 * onlyDefault, solo se verificará los predeterminados o entre todos las
	 * ubicaciones ordenadas por la fecha de modificación más pronta, aunque
	 * primero ordena por isDefault
	 * 
	 * @param ctx
	 *            contexto
	 * @param warehouseID
	 *            depósito
	 * @param onlyDefault
	 *            true si solamente se debe verificar las ubicaciones por
	 *            defecto, false si se debe verificar todas ordenando
	 *            descendientemente por isDefault y updated
	 * @param trxName
	 *            transacción en curso
	 * @return la ubicación por defecto o actualizada más recientemente, null si
	 *         no existe ninguna que cumpla con las condiciones planteadas
	 * @throws Exception
	 */
    public static MLocator getDefault(Properties ctx, Integer warehouseID, boolean onlyDefault, String trxName) throws Exception{
		StringBuffer sql = new StringBuffer(
				"SELECT * FROM m_locator WHERE isactive = 'Y' AND m_warehouse_id = ?");
    	if(onlyDefault){
    		sql.append(" AND isDefault = 'Y' ");
    	}
    	sql.append(" ORDER BY isDefault DESC, updated DESC");
    	PreparedStatement ps = DB.prepareStatement(sql.toString(), trxName);
    	ps.setInt(1, warehouseID);
    	ResultSet rs = ps.executeQuery();
    	MLocator locator = null;
    	if(rs.next()){
    		locator = new MLocator(ctx, rs, trxName);
    	}
    	rs.close();
    	ps.close();
    	return locator;
    }

    /**
     *      Get Warehouse Name
     *      @return name
     */
    public String getWarehouseName() {

        MWarehouse	wh	= MWarehouse.get(getCtx(), getM_Warehouse_ID());

        if (wh.getID() == 0) {
            return "<" + getM_Warehouse_ID() + ">";
        }

        return wh.getName();

    }		// getWarehouseName

    //~--- set methods --------------------------------------------------------

    /**
     *      Set Location
     *      @param X x
     *      @param Y y
     *      @param Z z
     */
    public void setXYZ(String X, String Y, String Z) {

        setX(X);
        setY(Y);
        setZ(Z);

    }		// setXYZ
}	// MLocator



/*
 * @(#)MLocator.java   02.jul 2007
 * 
 *  Fin del fichero MLocator.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
