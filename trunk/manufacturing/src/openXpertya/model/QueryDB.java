/*
 * @(#)QueryDB.java   13.jun 2007  Versión 2.2
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



package openXpertya.model;

import java.lang.reflect.*;

import java.util.*;

import java.sql.*;

import java.math.*;

import java.util.logging.*;

import org.openXpertya.util.*;

/**
 *
 * @author  vpj-cd
 */
public class QueryDB {

    /** Descripción de Campo */
    private String	classname;

    /** Descripción de Campo */
    private String	trxName;

    /** Descripción de Campo */
    private static CLogger	log	= CLogger.getCLogger(QueryDB.class);

    /**
     * Creates a new instance of POQuery 
     *
     * @param classname
     */
    public QueryDB(String classname) {
        this.classname	= classname;
    }

    /**
     * Descripción de Método
     *
     *
     * @param classname
     * @param id
     * @param trxName
     *
     * @return
     */
    public static Object newInstance(String classname, int id, String trxName) {

        Object	result	= null;
        Class	args;
        int	begin		= classname.indexOf("X_") + 2;
        String	table		= classname.substring(begin);
        Class[]	intArgsClass	= new Class[] { Properties.class, int.class, String.class };

        // Integer height = new Integer(12);
        Integer		ID	= new Integer(id);
        Object[]	intArgs	= new Object[] { Env.getCtx(), ID, table };
        Constructor	intArgsConstructor;

        try {

            args		= Class.forName(classname);
            intArgsConstructor	= args.getConstructor(intArgsClass);
            result		= createObject(intArgsConstructor, intArgs);

            return result;

        } catch (ClassNotFoundException e) {

            System.out.println(e);

            return result;

        } catch (NoSuchMethodException e) {

            System.out.println(e);

            return result;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param constructor
     * @param arguments
     *
     * @return
     */
    public static Object createObject(Constructor constructor, Object[] arguments) {

        // System.out.println ("Constructor: " + constructor.toString());
        Object	object	= null;

        try {

            object	= constructor.newInstance(arguments);

            // System.out.println ("Object: " + object.toString());
            return object;

        } catch (InstantiationException e) {
            log.log(Level.SEVERE, "InstantiationException:" + e);
        } catch (IllegalAccessException e) {
            log.log(Level.SEVERE, "IllegalAccessException:" + e);
        } catch (IllegalArgumentException e) {
            log.log(Level.SEVERE, "IllegalArgumentExceptio:" + e);
        } catch (InvocationTargetException e) {
            log.log(Level.SEVERE, "InvocationTargetException:" + e);
        }

        return object;
    }

    /**
     * Descripción de Método
     *
     *
     * @param filter
     *
     * @return
     */
    public List execute(String filter) {

        // String tablename = POClass.getName();
        // System.out.print(classname.indexOf("X_"));
        int		begin	= classname.indexOf("X_") + 2;
        String		table	= classname.substring(begin);
        StringBuffer	sql	= new StringBuffer("SELECT ").append(table).append("_ID FROM " + table);

        if (filter.equals("")) {
            System.out.println("not exist filter");
        } else {
            sql.append(" WHERE ").append(filter);
        }

        // System.out.println("Query " + sql.toString());
        List	results	= new ArrayList();

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql.toString());

            // pstmt.setInt(1, C_BPartner_ID);
            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                int	id	= rs.getInt(1);
                Object	element	= newInstance(classname, id, table);

                results.add(element);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            log.log(Level.SEVERE, "VCreateFrom.initIOS - Order\nSQL=" + sql.toString(), e);
        }

        return results;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public List execute() {

        // String tablename = POClass.getName();
        // System.out.print(classname.indexOf("X_"));
        int		begin	= classname.indexOf("X_") + 2;
        String		table	= classname.substring(begin);
        StringBuffer	sql	= new StringBuffer("SELECT ").append(table).append("_ID FROM " + table);

        // if (filter.equals(""))
        // System.out.println("not exist filter");
        // else
        // sql.append(" WHERE ").append(filter);

        // System.out.println("Query " + sql.toString());
        List	results	= new ArrayList();

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql.toString());

            // pstmt.setInt(1, C_BPartner_ID);
            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                int	id	= rs.getInt(1);
                Object	element	= newInstance(classname, id, table);

                results.add(element);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            log.log(Level.SEVERE, "VCreateFrom.initIOS - Order\nSQL=" + sql.toString(), e);
        }

        return results;
    }
}



/*
 * @(#)QueryDB.java   13.jun 2007
 * 
 *  Fin del fichero QueryDB.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
