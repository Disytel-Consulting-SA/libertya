/*
 * @(#)PruebaExport.java   12.oct 2007  Versión 2.2
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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

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
public class PruebaExport {

    /**
     * Maneja la profundidad del arbol
     */
    public String[]	ResultadoArray	= new String[1];

    /** Descripción de Campo */
    private String	MyUrl	= "null";

    /** Descripción de Campo */
    public String[]	IdArray	= new String[1];

    /** Descripción de Campo */
    public int	llamada;

    /** Descripción de Campo */
    public String	rol_id;

    /**
     * Descripción de Método
     * Retorna si el usuario especÃ­fico tiene o no acceso a la columna
     *
     *
     * @param action
     * @param id_tipo
     *
     * @return String
     */
    public String GetUsers(String action, String id_tipo) {

        String	Resultado	= "";
        String	Nombre		= "";
        String	sql		= "select ad_role_id, name";

        sql	= sql + " from ad_role order by name";

        PreparedStatement	stmt	= DB.prepareStatement(sql);

        try {

            ResultSet	rs	= stmt.executeQuery();
            int		i	= 1;

            while (rs.next()) {

                // aqui se pinta rol = -1
                Nombre	= rs.getString("name").replace(" ", "_");

                if (isPrint(action, id_tipo, rs.getString("ad_role_id")).equals("Y")) {
                    Resultado	= Resultado + "<" + Nombre + ">Si</" + Nombre + ">";
                } else {
                    Resultado	= Resultado + "<" + Nombre + ">No</" + Nombre + ">";
                }

                i++;
            }

            rs.close();

        } catch (SQLException e) {
            return "Error;";
        }

        return Resultado;
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

        String	Resultado	= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Menu>";
        String	Nombre		= "";
        String	id_tipo		= "";

        // String sql= "select M.ad_menu_id, M.name, T.seqno from ad_treenodemm T, ad_menu_trl M where  T.parent_id='0' and T.node_id=M.ad_menu_id order by T.seqno";
        String	sql	= "select M.ad_menu_id, M.name, T.seqno, A.issummary, A.action, A.isactive ,A.ad_window_id, A.ad_workflow_id, A.ad_task_id, A.ad_process_id, A.ad_form_id, A.ad_workbench_id";

        sql	= sql + " from ad_treenodemm T, ad_menu_trl M, ad_menu A";
        sql	= sql + " where  T.parent_id='0'";
        sql	= sql + " and T.node_id=M.ad_menu_id";
        sql	= sql + " and A.ad_menu_id=M.ad_menu_id";
        sql	= sql + " order by T.seqno";

        PreparedStatement	stmt	= DB.prepareStatement(sql);

        // stmt.setint( 1, parent_id );
        try {

            ResultSet	rs	= stmt.executeQuery();
            int		i	= 1;

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

                if (rs.getString("isactive").equals("Y")) {

                    // a isPrint faltan por mandarle los parametros
                    if (id_tipo.equals("carpeta")) {

                        if (Printhijos(rs.getInt(1)).equals("")) {}
                        else {

                            // Pintamos
                            Resultado	= Resultado + "<Fila>";
                            Resultado	= Resultado + PrintUsuarios();
                            Resultado	= Resultado + "<Titulo1>" + rs.getString(2) + "</Titulo1></Fila>";
                            Resultado	= Resultado + Printhijos(Integer.parseInt(rs.getString(1)));
                        }
                    }

                    // a isPrint faltan por mandarle los parametros
                    else if (isPrint(rs.getString("action"), id_tipo, rol_id).equals("Y")) {
                        Resultado	= Resultado + "<Titulo1>" + rs.getString(2) + "</Titulo1></Fila>";
                    }
                }

                i++;
            }

            rs.close();

            if (rol_id.equals("-1")) {

                Nombre	= "Todos";

                /*
                 * DataOutputStream salida=new DataOutputStream(new FileOutputStream("C:\\MenuTodos.csv"));
                 * salida.flush();
                 * salida.writeBytes(Resultado + "</Menu>");
                 * salida.close();
                 * /*FileOutputStream fichero=new FileOutputStream("C:\\Menu" + Nombre + ".xml");
                 * BufferedOutputStream buffer=new BufferedOutputStream(fichero);
                 * DataOutputStream salida=new DataOutputStream(buffer);
                 * salida.writeChars(Resultado);
                 * salida.close();
                 */

            } else {

                sql	= "select name";
                sql	= sql + " from ad_role where ad_role_id='" + rol_id + "'";
                stmt	= DB.prepareStatement(sql);

                try {

                    rs	= stmt.executeQuery();

                    while (rs.next()) {
                        Nombre	= rs.getString(1);
                    }

                } catch (Exception e) {
                    return "Error;";
                }

                rs.close();

                /*
                 * FileOutputStream fichero=new FileOutputStream("C:\\Menu" + Nombre + ".xml");
                 * BufferedOutputStream buffer=new BufferedOutputStream(fichero);
                 * DataOutputStream salida=new DataOutputStream(buffer);
                 * Resultado = Resultado.replace("&","y");
                 */

                // salida.writeChars(Resultado);
                // salida.close();
            }

//          ______________________________________________________________________________
            Nombre	= Nombre.replace(" ", "_");

            // FileOutputStream fichero=new FileOutputStream("C:\\Menu" + Nombre + ".txt");
            // BufferedOutputStream buffer=new BufferedOutputStream(fichero);
            // ObjectOutputStream salida=new ObjectOutputStream(buffer);
            // Este if controla la url de destino del xml
            if (MyUrl.equals("null")) {
                Nombre	= "C:\\Menu" + Nombre + ".xml";
            } else {
                Nombre	= MyUrl.replace("\\", "\\\\") + Nombre + ".xml";
            }

            BufferedWriter	salida	= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Nombre), "UTF-8"));

            /*
             * BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Menu" + Nombre + ".txt"),"UTF-8"));
             */
            Resultado	= Resultado.replace("&", "&amp;");
            salida.write(Resultado + "</Menu>");

            // salida.writeObject(Resultado + "</Menu>");
            salida.close();

            /*
             * ------------------------------------------------------------------
             * Esto era para crear un xml a partir de un txt
             * DocumentBuilder docBuilder;
             * Document doc = null;
             * DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
             * docBuilderFactory.setIgnoringElementContentWhitespace(true);
             *
             * //Aqui Transformamos nuestro string a XML:
             *
             * try {docBuilder = docBuilderFactory.newDocumentBuilder();}
             *
             * catch (ParserConfigurationException e)
             *
             * {return "Error Linea 171: " + e.toString();}
             *
             * try {doc = docBuilder.parse("C:\\Menu" + Nombre + ".txt");}
             *
             * catch (SAXException e){return "SAX Error Linea 174: " + e.toString();}
             *
             * catch (IOException e){return "IO Error Linea 174: " + e.toString();}
             *
             * try {
             * //Aqui salvamos el XML(doc)
             * //creamos el archivo
             * File xmlOutputFile = new File("C:\\Menu" + Nombre + ".xml");
             * //Usamos el OutputStream con XML:
             * FileOutputStream fos=new FileOutputStream(xmlOutputFile);
             * //Creamos una nueva instancia de Transformer
             * TransformerFactory transformerFactory = TransformerFactory.newInstance();
             * Transformer transformer = transformerFactory.newTransformer();
             * //Instanciamos el origen del archivo
             * DOMSource source = new DOMSource(doc);
             * //Instanciamos el resultado
             * StreamResult result = new StreamResult(fos);
             * //guardamos
             * transformer.transform(source, result);
             * }
             * catch (Exception e){return "Error General: " + e.toString();}
             */

            // ______________________________________________________________________________

        } catch (Exception e) {
            return "Error Final: " + e.toString();
        }

        return "Se ha exportado con exito " + "<b>C:\\Menu" + Nombre + ".xml" + "</b>";

    }		//

    /**
     * Descripción de Método
     *
     */
    public void PrintUsers() {

        String	sql	= "select ad_role_id, name";

        sql	= sql + " from ad_role order by name";

        PreparedStatement	stmt	= DB.prepareStatement(sql);

        try {

            ResultSet	rs	= stmt.executeQuery();
            int		a	= 1;

            while (rs.next()) {
                a++;
            }

            rs.close();
            ResultadoArray	= new String[a];
            IdArray		= new String[a];
            rs			= stmt.executeQuery();

            int	i	= 1;

            IdArray[0]		= "-1";
            ResultadoArray[0]	= "0-Exportar Todo";

            while (rs.next()) {

                IdArray[i]		= rs.getString("ad_role_id");
                ResultadoArray[i]	= i + "-" + rs.getString("name").replace(" ", "_");
                i++;
            }

            rs.close();

        } catch (SQLException e) {}
    }

    /**
     * Descripción de Método
     * Devuelve las columnas de froma numÃ©rica
     *
     *
     * @return String
     */
    public String PrintUsuarios() {

        String	Resultado	= "";
        String	Nombre		= "";
        String	sql		= "select ad_role_id, name";

        sql	= sql + " from ad_role order by name";

        PreparedStatement	stmt	= DB.prepareStatement(sql);
        int			i	= 1;

        try {

            ResultSet	rs	= stmt.executeQuery();

            while (rs.next()) {

                Nombre		= rs.getString("name").replace(" ", "_");
                Resultado	= Resultado + "<" + Nombre + "></" + Nombre + ">";
                i++;
            }

            return Resultado;

        } catch (SQLException e) {
            return "Error;";
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

                            Resultado	= Resultado + "<Fila>";
                            Resultado	= Resultado + PrintUsuarios();
                            Resultado	= Resultado + "<Titulo" + (llamada - (-1)) + ">" + rs.getString(2) + "</Titulo" + (llamada - (-1)) + "></Fila>";
                            Resultado	= Resultado + Printhijos(rs.getInt(1));
                        }

                        llamada	= llamada - 1;
                    }

                    // a isPrint faltan por mandarle los parametros
                    else if (isPrint(rs.getString("action"), id_tipo, rol_id).equals("Y")) {

                        // Si entra en este IF es que la carpeta no esta vacia
                        // Resultado=Resultado + ResultadoAux;
                        // ResultadoAux="";
                        llamada		= llamada + 1;
                        Resultado	= Resultado + "<Fila>";
                        Resultado	= Resultado + GetUsers(rs.getString("action"), id_tipo);
                        Resultado	= Resultado + "<Titulo" + (llamada - (-1)) + ">" + rs.getString(2) + "</Titulo" + (llamada - (-1)) + "></Fila>";
                        llamada	= llamada - 1;
                    }
                }
            }

            rs.close();

            return Resultado;

        } catch (Exception e) {
            return "error;";
        }
    }

    // Vamos a crear un XML mediante JDOM
    // Inicializamos un elemento principal
    // Element root = new Element("Menu");
    // Creamos un documento dentro del root
    // Document documento = new Document(root);
    // Element fila = new Element("fila");

    /**
     * Descripción de Método
     *
     *
     * @param a
     */
    public void SetMyUrl(String a) {
        MyUrl	= a;
    }

    /**
     * Descripción de Método
     *
     *
     * @param name
     *
     * @return
     */
    public String getid(String name) {

        String	id	= "";
        int	i	= 0;

        while (i < ResultadoArray.length) {

            if (ResultadoArray[i].equals(name)) {
                return IdArray[i];
            }

            i++;
        }

        return id;
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

            while (rs.next()) {

                if (rs.getString("isactive").equals("Y")) {
                    return "Y";
                }
            }

            rs.close();

        } catch (SQLException e) {
            return "Error;";
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
 * @(#)PruebaExport.java   02.jul 2007
 * 
 *  Fin del fichero PruebaExport.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
