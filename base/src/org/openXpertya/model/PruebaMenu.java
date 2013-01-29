/*
 * @(#)PruebaMenu.java   12.oct 2007  Versión 2.2
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

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 02.jul 2007
 * @autor     Fundesle    
 */
public class PruebaMenu {

    /**
     * Maneja la profundidad del arbol
     */
    public int	llamada;

    /** Descripción de Campo */
    public String	rol_id;

    /**
     * Descripción de Método
     *
     *
     * @param action
     * @param id_tipo
     *
     * @return
     */
    public String GetUsers(String action, String id_tipo) {

        String	Resultado	= "";
        String	sql		= "select ad_role_id, name";

        sql	= sql + " from ad_role order by name";

        PreparedStatement	stmt	= DB.prepareStatement(sql);

        try {

            ResultSet	rs	= stmt.executeQuery();

            while (rs.next()) {

                // aqui se pinta rol = -1
                if (isPrint(action, id_tipo, rs.getString("ad_role_id")).equals("Y")) {
                    Resultado	= Resultado + "<span class=\"valido\">&nbsp;</span>";
                } else {
                    Resultado	= Resultado + "<span class=\"novalido\">&nbsp;</span>";
                }
            }

            rs.close();

        } catch (SQLException e) {
            return "<span class=\"error\"> -Error Java 5- " + e.toString() + "</span>";
        }

        return Resultado;
    }

    /**
     * Descripción de Método
     * Nos rellena un array de String con el contenido de nuestros datos
     *
     * @param MiSql
     * @param cols
     *
     * @return array
     */
    public String[][] Misresultados(String MiSql, int cols) {

        String[][]		MiResultado	= new String[1][1];
        PreparedStatement	stmt		= DB.prepareStatement(MiSql);

        try {

            ResultSet	rs	= stmt.executeQuery();
            int		i	= 0;

            while (rs.next()) {
                i++;
            }

            rs.close();
            rs		= stmt.executeQuery();
            MiResultado	= new String[i][cols + 1];
            i		= 0;

            while (rs.next()) {

                for (int j = 1; j < cols; j++) {

                    // j+1 pq el rs empieza en 1
                    MiResultado[i][j - 1]	= rs.getString(j);
                }

                i++;
            }

            rs.close();

        } catch (SQLException e) {

            MiResultado[0][0]	= "Error " + e.toString();

            return MiResultado;
        }

        return MiResultado;
    }

    /**
     * Descripción de Método
     * Es nuestra 1Âº funcion y la constructora de todo el arbol
     *
     *
     * @return String : Todo el arbol
     */
    public final String PrintMenu() {

        llamada	= 0;

        String	Resultado	= "";
        String	id_tipo		= "";

        // String sql= "select M.ad_menu_id, M.name, T.seqno from ad_treenodemm T, ad_menu_trl M where  T.parent_id='0' and T.node_id=M.ad_menu_id order by T.seqno";
        String	sql	= "select M.ad_menu_id, M.name, T.seqno, A.issummary, A.action, A.isactive ,A.ad_window_id, A.ad_workflow_id, A.ad_task_id, A.ad_process_id, A.ad_form_id, A.ad_workbench_id";

        sql	= sql + " from ad_treenodemm T, ad_menu_trl M, ad_menu A";
        sql	= sql + " where  T.parent_id='0'";
        sql	= sql + " and T.node_id=M.ad_menu_id";
        sql	= sql + " and A.ad_menu_id=M.ad_menu_id";
        sql	= sql + " order by T.seqno";

        String[][]	MiArray	= Misresultados(sql, 11);

        // stmt.setint( 1, parent_id );
        try {

            int	i	= 0;

            while (i < MiArray.length) {

                // aqui debemos verificar que tipo es y si no es carpeta almacenamos la
                // id necesaria
                if (MiArray[i][3].equals("N")) {

                    if (MiArray[i][4].equals("W")) {
                        id_tipo	= MiArray[i][6];
                    } else if (MiArray[i][4].equals("F")) {
                        id_tipo	= MiArray[i][7];
                    } else if (MiArray[i][4].equals("T")) {
                        id_tipo	= MiArray[i][8];
                    } else if (MiArray[i][4].equals("P") || MiArray[i][4].equals("R")) {
                        id_tipo	= MiArray[i][9];
                    } else if (MiArray[i][4].equals("X")) {
                        id_tipo	= MiArray[i][10];
                    } else if (MiArray[i][4].equals("B")) {
                        id_tipo	= MiArray[i][11];
                    }

                } else {
                    id_tipo	= "carpeta";
                }

                if (MiArray[i][5].equals("Y")) {

                    // Si es una carpeta
                    if (id_tipo.equals("carpeta")) {

                        if (Printhijos(Integer.parseInt(MiArray[i][0])).equals("")) {}

                        // Es que es una carpeta vacia, no la imprimo.
                        else {

                            // Como es una carpeta con contenido pinto los usuarios:
                            Resultado	= Resultado + PrintUsuarios("", "");

                            // Pinto la carpeta y su nombre
                            Resultado	= Resultado + "<p class=\"level1\"><span class=\"carpeta\">" + MiArray[i][1] + "</span></p>";
                            Resultado	= Resultado + "<div class=\"clear\"></div>";
                            Resultado	= Resultado + Printhijos(Integer.parseInt(MiArray[i][0]));
                        }
                    }

                    // Si no es una carpeta, miro a ver el que es. y lo aÃ±ado al resultado.
                    else if (isPrint(MiArray[i][4], id_tipo, rol_id).equals("Y")) {

                        Resultado	= Resultado + "<p class=\"level1\">";

                        if (MiArray[i][4].equals("W")) {
                            Resultado	= Resultado + "<span class=\"ventana\">";
                        } else if (MiArray[i][4].equals("F")) {
                            Resultado	= Resultado + "<span class=\"workflow\">";
                        } else if (MiArray[i][4].equals("T")) {
                            Resultado	= Resultado + "<span class=\"tarea\">";
                        } else if (MiArray[i][4].equals("P")) {
                            Resultado	= Resultado + "<span class=\"proceso\">";
                        } else if (MiArray[i][4].equals("X")) {
                            Resultado	= Resultado + "<span class=\"formulario\">";
                        } else if (MiArray[i][4].equals("B")) {
                            Resultado	= Resultado + "<span class=\"workbench\">";
                        } else if (MiArray[i][4].equals("R")) {
                            Resultado	= Resultado + "<span class=\"informe\">";
                        }

                        Resultado	= Resultado + MiArray[i][1] + "</span></p><div class=\"clear\"></div>";
                    }
                }

                i++;
            }

            return Resultado;

        } catch (Exception e) {
            return "<span class=\"error\"> -Error Java 1- " + e.toString() + "</span>";
        }

    }		//

    /**
     * Descripción de Método
     *
     *
     * @param A
     * @param valor
     *
     * @return
     */
    public String PrintUsuarios(String A, String valor) {

        String	Resultado	= "";
        String	sql		= "select ad_role_id, name";

        sql	= sql + " from ad_role order by name";

        PreparedStatement	stmt	= DB.prepareStatement(sql);
        int			i	= 1;

        try {

            ResultSet	rs	= stmt.executeQuery();

            while (rs.next()) {

                if (A.equals("combo")) {

                    if (valor.equals(rs.getString("ad_role_id"))) {
                        Resultado	= Resultado + "<option value=\"" + rs.getString("ad_role_id") + "\" selected> " + i + "- " + rs.getString("name") + "</option>";
                    } else {
                        Resultado	= Resultado + "<option value=\"" + rs.getString("ad_role_id") + "\"> " + i + "- " + rs.getString("name") + "</option>";
                    }

                } else {
                    Resultado	= Resultado + "<span class=\"titulo\"><a href=\"#\" title=\"" + rs.getString("name") + "\">" + i + "</a></span>";
                }

                i++;
            }

            rs.close();

            return Resultado;

        } catch (SQLException e) {
            return "<span class=\"error\"> -Error Java 4- " + e.toString() + "</span>";
        }
    }

    /**
     * Descripción de Método
     * Le mandamos parent id y nos imprime sus hijos
     * La funciÃ³n es recursiva
     *
     * @param parent_id
     *
     * @return hijos
     */
    public String Printhijos(int parent_id) {

        String	id_tipo		= "";
        String	Resultado	= "";

        // 1 id menu
        // 2 nombre
        // 3 sequencia de despliegue
        // ---------------------
        // 4 si es carpeta o no
        // 5 tipo de accion
        // 6 Si esta activado o no, si no no se muestra para nadie
        // 7, 8, 9... id de las acciones
        // String sql= "select M.ad_menu_id, M.name, T.seqno from ad_treenodemm T, ad_menu_trl M where  T.parent_id='" + parent_id + "' and T.node_id=M.ad_menu_id order by T.seqno";
        String	sql	= "select M.ad_menu_id, M.name, T.seqno, A.issummary, A.action, A.isactive ,A.ad_window_id, A.ad_workflow_id, A.ad_task_id, A.ad_process_id, A.ad_form_id, A.ad_workbench_id";

        sql	= sql + " from ad_treenodemm T, ad_menu_trl M, ad_menu A";
        sql	= sql + " where  T.parent_id='" + parent_id + "' and";
        sql	= sql + " T.node_id=M.ad_menu_id";
        sql	= sql + " and A.ad_menu_id=M.ad_menu_id";
        sql	= sql + " order by T.seqno";

        PreparedStatement	stmt	= DB.prepareStatement(sql);

        try {

            ResultSet	rs	= stmt.executeQuery();

            while (rs.next()) {

                // aqui debemos verificar que tipo es y si no es carpeta almacenamos la
                // id necesaria
                if (rs.getString("issummary").equals("N")) {

                    if (rs.getString("action").equals("W")) {
                        id_tipo	= rs.getString("ad_window_id");
                    } else if (rs.getString("action").equals("F")) {
                        id_tipo	= rs.getString("ad_workflow_id");
                    } else if (rs.getString("action").equals("T")) {
                        id_tipo	= rs.getString("ad_task_id");
                    } else if (rs.getString("action").equals("P") || rs.getString("action").equals("R")) {
                        id_tipo	= rs.getString("ad_process_id");
                    } else if (rs.getString("action").equals("X")) {
                        id_tipo	= rs.getString("ad_form_id");
                    } else if (rs.getString("action").equals("B")) {
                        id_tipo	= rs.getString("ad_workbench_id");
                    }

                } else {
                    id_tipo	= "carpeta";
                }

                /*
                 * aqui es donde debo saber si pinto o no
                 * necesitamos una funcion que nos diga si pintamos o no
                 *
                 * La funcion depende del rol del id del elemento y del tipo de elemento
                 * W -> ventana
                 * F -> workflow
                 * T -> tarea
                 * P -> proceso
                 * X -> formulario
                 * B -> workbench
                 *
                 * a partir de aqui imprimo
                 */
                if (rs.getString("isactive").equals("Y")) {

                    /*
                     * if (id_tipo.equals("carpeta")){}
                     * else
                     *       Resultado=Resultado + "<br> isPrint(" + rs.getString("action") + ", " + id_tipo + ", " + rol_id + ")=" + isPrint(rs.getString("action"), id_tipo , rol_id).equals("Y") + "<br>";
                     */
                    if (id_tipo.equals("carpeta")) {

                        llamada	= llamada + 1;

                        // para que no imprima las carpetas vacias
                        if (Printhijos(rs.getInt(1)).equals("")) {}
                        else {

                            Resultado	= Resultado + PrintUsuarios("", "");
                            Resultado	= Resultado + "<p class=\"level" + (llamada - (-1)) + "\">";
                            Resultado	= Resultado + "<span class=\"carpeta\">" + rs.getString(2) + "</span></p>";
                            Resultado	= Resultado + "<div class=\"clear\"></div>";
                            Resultado	= Resultado + Printhijos(rs.getInt(1));
                        }

                        llamada	= llamada - 1;
                    }

                    // a isPrint faltan por mandarle los parametros
                    else if (isPrint(rs.getString("action"), id_tipo, rol_id).equals("Y")) {

                        // Si entra en este IF es que la carpeta no esta vacia
                        // Resultado=Resultado + ResultadoAux;
                        // ResultadoAux="";
                        //
                        llamada		= llamada + 1;
                        Resultado	= Resultado + GetUsers(rs.getString("action"), id_tipo);
                        Resultado	= Resultado + "<p class=\"level" + (llamada - (-1)) + "\">";

                        if (rs.getString("action").equals("W")) {
                            Resultado	= Resultado + "<span class=\"ventana\">";
                        } else if (rs.getString("action").equals("F")) {
                            Resultado	= Resultado + "<span class=\"workflow\">";
                        } else if (rs.getString("action").equals("T")) {
                            Resultado	= Resultado + "<span class=\"tarea\">";
                        } else if (rs.getString("action").equals("P")) {
                            Resultado	= Resultado + "<span class=\"proceso\">";
                        } else if (rs.getString("action").equals("X")) {
                            Resultado	= Resultado + "<span class=\"formulario\">";
                        } else if (rs.getString("action").equals("B")) {
                            Resultado	= Resultado + "<span class=\"workbench\">";
                        } else if (rs.getString("action").equals("R")) {
                            Resultado	= Resultado + "<span class=\"informe\">";
                        }

                        Resultado	= Resultado + rs.getString(2) + "</span></p><div class=\"clear\"></div>";

                        // Resultado= Resultado + Printhijos(rs.getInt(1));
                        llamada	= llamada - 1;
                    }
                }
            }

            rs.close();

            return Resultado;

        } catch (Exception e) {
            return "<span class=\"error\"> -Error Java 2- " + e.toString() + "</span>";
        }
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     * Nos indica si el menu ha de imprimirse o no
     * para un rol especÃ­fico
     *
     * @param tipo, id_tipo, rol
     * @param id_tipo
     * @param rol
     *
     * @return Y/N
     */
    public String isPrint(String tipo, String id_tipo, String rol) {

        String	sql	= "";

        if (rol.equals("-1")) {
            return "Y";
        }

        if (tipo.equals("W")) {
            sql	= "select isactive from ad_window_access where ad_role_id='" + rol + "' and ad_window_id='" + id_tipo + "'";
        } else if (tipo.equals("F")) {
            sql	= "select isactive from ad_workflow_access where ad_role_id='" + rol + "' and ad_workflow_id='" + id_tipo + "'";
        } else if (tipo.equals("T")) {
            sql	= "select isactive from ad_task_access where ad_role_id='" + rol + "' and ad_task_id='" + id_tipo + "'";
        } else if (tipo.equals("P") || tipo.equals("R")) {
            sql	= "select isactive from ad_process_access where ad_role_id='" + rol + "' and ad_process_id='" + id_tipo + "'";
        } else if (tipo.equals("X")) {
            sql	= "select isactive from ad_form_access where ad_role_id='" + rol + "' and ad_form_id='" + id_tipo + "'";
        } else if (tipo.equals("B")) {
            sql	= "select isactive from ad_workbench_access where ad_role_id='" + rol + "' and ad_workbench_id='" + id_tipo + "'";
        }

        PreparedStatement	stmt	= DB.prepareStatement(sql);

        try {

            ResultSet	rs	= stmt.executeQuery();

            if (rs.next()) {

                if (rs.getString("isactive").equals("Y")) {

                    rs.close();

                    return "Y";
                }
            }

            rs.close();

        } catch (SQLException e) {
            return "<span class=\"error\"> -Error Java 3- " + e.toString() + "</span>";
        }

        return "N";
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param rol
     */
    public void setRol(String rol) {
        rol_id	= rol;
    }
}



/*
 * @(#)PruebaMenu.java   02.jul 2007
 * 
 *  Fin del fichero PruebaMenu.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
