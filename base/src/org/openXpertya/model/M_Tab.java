/*
 * @(#)M_Tab.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Tab Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: M_Tab.java,v 1.5 2005/05/17 21:48:35 jjanke Exp $
 */
public class M_Tab extends X_AD_Tab {

	/** Application Logger */
	static CLogger s_log = CLogger.getCLogger(M_Tab.class);
    
	/** The Fields */
    private M_Field[]	m_fields	= null;

    /**
     *      Parent Constructor
     *      @param parent parent
     */
    public M_Tab(M_Window parent) {

        this(parent.getCtx(), 0, parent.get_TrxName());
        setClientOrg(parent);
        setAD_Window_ID(parent.getAD_Window_ID());
        setEntityType(parent.getEntityType());

    }		// M_Tab

    /**
     *      Parent Constructor
     *      @param parent parent
     *      @param from copy from
     */
    public M_Tab(M_Window parent, M_Tab from) {

        this(parent.getCtx(), 0, parent.get_TrxName());
        copyValues(from, this);
        setClientOrg(parent);
        setAD_Window_ID(parent.getAD_Window_ID());
        setEntityType(parent.getEntityType());

    }		// M_Tab

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Tab_ID id
     * @param trxName
     */
    public M_Tab(Properties ctx, int AD_Tab_ID, String trxName) {

        super(ctx, AD_Tab_ID, trxName);

        if (AD_Tab_ID == 0) {

            // setAD_Window_ID (0);
            // setAD_Table_ID (0);
            // setName (null);
            setEntityType(ENTITYTYPE_UserMaintained);		// U
            setHasTree(false);
            setIsReadOnly(false);
            setIsSingleRow(false);
            setIsSortTab(false);				// N
            setIsTranslationTab(false);
            setSeqNo(0);
            setTabLevel(0);
            setIsInsertRecord(true);
            setIsAdvancedTab(false);
        }

    }								// M_Tab

    
    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public M_Tab(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// M_Tab

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Fields
     *      @param reload reload data
     * @param trxName
     *      @return array of lines
     */
    public M_Field[] getFields(boolean reload, String trxName) {

        if ((m_fields != null) &&!reload) {
            return m_fields;
        }

        String			sql	= "SELECT * FROM AD_Field WHERE AD_Tab_ID=? ORDER BY SeqNo";
        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, trxName);
            pstmt.setInt(1, getAD_Tab_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new M_Field(getCtx(), rs, trxName));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "getFields", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        //
        m_fields	= new M_Field[list.size()];
        list.toArray(m_fields);

        return m_fields;

    }		// getFields
    
    /**
     * Retorna una lista con los IDs de las pestañas que son padre de una pestaña.
     * Ej.
     * <code><br>
     * +- Entidad Comercial<br>
     * ...+- Proveedor<br> 
     * ...+- Cliente<br>
     * ......+- Contabilidad de Cliente<br>
     * </code>
     * Si se invoca con el ID de la pestaña Contabilidad de Cliente, el método retorna
     * una lista con los IDs de las pestañas Cliente y Entidad Comercial. 
     * @param tabID ID de la pestaña de la cual se quieren obtener los padres.
     * @param trxName Nombre de la transacción de BD a usar en las consultas.
     * @return <code>List<<Integer>></code> con los ID de todos los padres en el arbol.
     * @throws Exception Cuando se produce algún error en las consultas a la BD.
     */
    public static List<Integer> getParentTabIDs(int tabID, String trxName) throws Exception {
    	PreparedStatement pstmt	= null;
		ResultSet rs = null;
        
		// Se obtiene la pestaña a la que se le intenta configurar el acceso.
		M_Tab mTab = new M_Tab(Env.getCtx(), tabID, trxName);
		// Se obtiene el nivel de pestaña para validar las dependencias.
		int curTabLevel = mTab.getTabLevel() - 1;

        // Lista de IDs de pestañas que son padres de la pestaña original.
        ArrayList<Integer> parentTabs = new ArrayList<Integer>();

		try {
            // Se ejecuta la consulta que devuelve las dependencias (en crudo) de la pestaña.
			pstmt = DB.prepareStatement(getTabDependenciesSQL(), trxName);
            pstmt.setInt(1, tabID);
            rs	= pstmt.executeQuery();
           
            // Se recorre cada registro de pestaña resultante para ver si realmente es una
            // dependencia directa de la pestaña actual (dependiendo del valor del tabLevel).
            while (rs.next()) {
            	// Se obtiene el ID y Nivel de la potencial pestaña padre.
            	int pTabID = rs.getInt("AD_Tab_ID");
            	int tabLevel = rs.getInt("TabLevel");
                // Si la pestaña contiene el nivel que se está buscando, entonces es el
            	// padre directo y la pestaña original depende de esta pestaña.
            	if (tabLevel == curTabLevel) {
            		parentTabs.add(0, pTabID);
            		// Ahora se busca una pestaña con un nivel menos.
            		curTabLevel--;
            		
            		// Llamado recursivo para obtener la lista de padres de la pestaña
            		// padre directa.
            		List<Integer> auxParentTabs = getParentTabIDs(pTabID, trxName);
            		for (Integer parentTabID : auxParentTabs) {
						if (!parentTabs.contains(parentTabID))
							parentTabs.add(0, parentTabID);
					}
            	}
            }
            
        } catch (Exception e) {
			s_log.severe("Error while validating tab dependencies. " + e.getMessage());
        	s_log.saveError("SaveError",e.getMessage());
        	throw e;
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (SQLException e) {}
		}
		
		return parentTabs;
	}
	
	private static String getTabDependenciesSQL() {
		String sql =
			" SELECT tb.AD_Tab_ID, tb.SeqNo, tb.TabLevel, tb.Name " +
			//-- Vista de columnas de las que depende la pestaña.
			" FROM ( " +
			//	-- Columna Primaria definida en la pestaña
			"	SELECT tb.AD_Window_ID, tb.AD_Tab_ID, tb.SeqNo, ctb.ColumnName " +
			"	FROM AD_Tab tb " + 
			"	INNER JOIN AD_Column ctb ON (ctb.AD_Column_ID = tb.AD_Column_ID) " +

			"	UNION " +

			//	-- Columnas de enlace a tabla principal relacionadas con los campos de la pestaña.
			"	SELECT tb.AD_Window_ID, tb.AD_Tab_ID, tb.SeqNo, c.ColumnName " +
			"	FROM AD_Tab tb " +
			" 	INNER JOIN AD_Table ta ON (tb.AD_Table_ID = ta.AD_Table_ID) " +
			"	INNER JOIN AD_Column c ON (c.AD_Table_ID = tb.AD_Table_ID) " +
			"	WHERE ta.IsActive = 'Y' AND " +
			"	      c.IsParent = 'Y' " +
			" ) v, " +
			" AD_Table ta " +
			" INNER JOIN AD_Tab tb ON (tb.AD_Table_ID = ta.AD_Table_ID) " +
			" WHERE v.AD_Tab_ID = ? AND " +
			//     -- Obtencion de la tabla de la cual depende la pestaña
			"      ta.TableName = replace(v.ColumnName,'_ID','') AND " +
			//     -- Solo se permiten pestañas contenidas en la ventana a la que
			//     -- pertenece la pestaña original.
			"      tb.AD_Window_ID = v.AD_Window_ID AND " +
			//     -- Solo depende de pestañas que tengan secuencia menor a la suya.
			//     -- (Esto es una convención que se toma como verdadera. Se deben
			//     -- configurar correctamente las secuencias de pestañas en las tablas).
			"      tb.SeqNo <= v.SeqNo " +
			" ORDER BY tb.SeqNo DESC ";
		return sql;
	}
}	// M_Tab



/*
 * @(#)M_Tab.java   02.jul 2007
 * 
 *  Fin del fichero M_Tab.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
