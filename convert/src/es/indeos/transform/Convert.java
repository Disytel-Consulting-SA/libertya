/**
 * Herramienta para importar y exportar metadatos de OpenXpertya 
 *
 * Dise�o y desarrollo por Indeos Consultoria S.L.
 * 
 * Modificado por TecnoXP
 * 
 */

/*
 * La herramienta Convert se utiliza para importar y exportar metadatos de 
 * OpenXpertya. La misma actualmente exporta: Ventanas, Procesos y Tablas 
 * independientes. El punto de inicio de la exportaci�n es el m�todo export
 * de la clase Convert (es.indeos.transform.Convert).
 * 
 * La forma de exportaci�n es muy simple: dentro de la clase ADMap se cargan 
 * las instancias de los objetos a exportar, todos ellos instancias de alguna 
 * subclase de PO. Para la exportaci�n, cada objeto posee su propia subclase 
 * espec�fica. Por ejemplo, Una tabla con un registro en AD_Table se instancia
 * como un objeto TMTable.
 * 
 * Luego, utilizando la libreria XStream se serializa el objeto ADMap, junto 
 * con todos sus datos y de all� sale el archivo XML.
 * 
 * La importaci�n es un poco m�s delicada. Utilizando la misma libreria se 
 * cargan desde el archivo XML las instancias de los objetos por medio de la 
 * clase ADMap. Se crea una nueva instancia de dicha clase con las variables
 * de instancia con los mismos valores al momento de realizar la exportaci�n. 
 * 
 * El problema aparece ya que dichas instancias luego de ser deserializadas, 
 * son de otra base de datos y las referencias (claves) a otras tablas son 
 * incorrectas. 
 * 
 * La importaci�n, entonces, se encarga de corregir las referencias adapt�ndo-
 * las a la nueva base de datos. Dentro del m�todo doImport de ADMap se reliza
 * el proceso de importaci�n.
 * 
 * Concretamente, la importaci�n busca registros existentes para modificar, o 
 * nuevos en caso de que no exista alguno adecuado. Cada tabla tiene su propia
 * l�gica de importaci�n. En el caso de la tabla de tablas (AD_Table), por 
 * ejemplo, la importaci�n busca si existe algun registro con un mismo valor de 
 * "TableName" que el objeto que se est� importando. Para saber m�s al respecto
 * se puede ver las clases concretas TM* que se encuentran dentro del paquete:
 * es.indeos.transform.model
 * 
 * La herramienta original sufri� modificaciones para ampliar su funcionamiento
 * y poder incluir soporte de exportaci�n de procesos (AD_Process), junto con
 * sus datos referenciados (parametros, formato de impresion, vista de 
 * reporte). 
 * 
 * Durante la adaptaci�n se hizo cierto Refactoring del c�digo a fin de eliminar
 * grandes bloques de c�digo repetidos encontrados en las clases TM existentes.
 * Espec�ficamente se organizaron dichos bloques de codigo dentro de una sola
 * clase (ImpExPoCommon) a fin de eliminar todo el c�digo duplicado. Dicho 
 * c�digo se encargaba de exportar/importar las traducciones a partir de las 
 * tablas _Trl adecuadas.
 * 
 * Actualmente la clase ImpExPoCommon se encarga de:
 * 
 *  * importar/exportar traducciones 
 *  
 *  * instanciaci�n gen�rica de objetos PO (mirar los m�todos FactoryPO de 
 *    dicha clase). 
 * 
 *  * manejo b�sico de los metadatos de AD_Table y AD_Column. 
 * 
 * Adicionalmente, se adaptaron las clases TM* bajo una interfaz en com�n a fin
 * de establecer un marco de importaci�n y exportaci�n mas coherente entre 
 * todas las clases. Dicha interfaz (ImpExPoAdapter) es la que define los metodos
 * para buscar las instancias actuales al momento de importar, junto con la
 * inicializaci�n de los valores adecuados. 
 * 
 * Para obtener m�s informaci�n sobre la clase ImpExPoAdapter mirar la docu-
 * mentaci�n existente en la misma. Adicionalmente se recomienda mirar la forma
 * en la que actualmente se la utiliza dentro de ImpExPoCommon. Concretamente, se
 * asume que toda clase que implemente la interfaz ImpExPoAdapter es subclase 
 * de PO.
 * 
 * La importaci�n de las clases bajo la interfaz ImpExPoAdapter se realiza en dos
 * etapas. La primer etapa crea las nueva instancias a impactar en la base de 
 * datos destino, y la segunda etapa inicializa las referencias pendientes. Esto
 * es as� ya que existen referencias circulares dentro de una misma tabla o 
 * referencias a registros que a�n no fueron importados. 
 * 
 * Dicha inicializacion de nuevos valores se realiza con el m�todo setCustomDataFrom
 * de las clases que implementan ImpExPoAdapter, y con el �ltimo par�metro 
 * (passNo) se indica en cual etapa se encuentra.
 * 
 * Las nuevas modificaciones a la herramienta no fueron reflejadas en todas las
 * clases, y no todas las clases subclases de PO implementan la interfaz 
 * ImpExPoAdapter. 
 * 
 * Una futura ampliaci�n de la herramienta ser� terminar la migraci�n de las 
 * clases pendientes bajo la interfaz ImpExPoAdapter (eso es, implementarla en
 * aquellas clases que no), y eliminar la dependencia de la clases ReferenceMap
 * y TableMap para reemplazarlas por los m�todos getNewId y setNewId de la 
 * clase Convert que son m�s simples y sencillos de usar y entender.
 * 
 * Otra mejora pendiente ser� la de simplificar a�n m�s la interfaz ImpExPoAdapter,
 * ya que la mayor�a de los metodos se implementan con el mismo c�digo.
 * 
 * Por �ltimo, la compatibilidad con la version anterior de la herramienta est�
 * rota. Si alguien necesita imperiosamente importar ventanas creadas con la 
 * versi�n anterior de la herramienta, deber� utilizar la misma versi�n o 
 * corregir los errores que se puedan presentar. En particular, los errores
 * conocidos hasta el momento son nuevos campos en los objetos (y en los que
 * xstream dispara una excepcion) o NullPointerException en las instancias de 
 * ImpExPoCommon al momento de realizar la importaci�n. No se realizaron 
 * mayores pruebas al respecto.
 * 
 */

package es.indeos.transform;

import es.indeos.transform.*;
import es.indeos.transform.controller.*;
import es.indeos.transform.model.*;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Env;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Msg;
import org.openXpertya.model.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.util.*;
import java.awt.image.DataBufferUShort;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Convert
 * 
 * Clase empleada para exportar o importar un Exporter.
 *
 */
public class Convert {

	/**	CLogger											*/
	protected CLogger			log = CLogger.getCLogger (getClass());
	
	/** Idiomas que deben exportarse	*/
	private static String[]	languages = {"es_ES"};	
	
	
	/**	XStream para la importacion/exportacion de XML	*/
	private XStream xstream;
	
	
	public Convert ()	{
		xstream = new XStream(new DomDriver());
		
		// Los log no funcionan demasiado bien..
		xstream.registerConverter(new LogConverter());
		xstream.alias("CLogger", CLogger.class);
		
		// Registramos los alias de las clases para permitir migracion
		// entre versiones
		xstream.alias("ADMap", ADMap.class);
		xstream.alias("ReferenceMap", ReferenceMap.class);
		xstream.alias("TableMap", TableMap.class);
		xstream.alias("TMWindow", TMWindow.class);
		xstream.alias("TMReference", TMReference.class);
		xstream.alias("TMRefTable", TMRefTable.class);
		xstream.alias("TMTable", TMTable.class);
		xstream.alias("TMColumn", TMColumn.class);
		xstream.alias("TMField", TMField.class);
		xstream.alias("TMRefList", TMRefList.class);
		xstream.alias("TMTab", TMTab.class);
		xstream.alias("TMValRule", TMValRule.class);
		
		xstream.alias("TMProcess", TMProcess.class);
		xstream.alias("TMProcessPara", TMProcessPara.class);
		xstream.alias("TMPrintFormat", TMPrintFormat.class);
		xstream.alias("TMPrintFormatItem", TMPrintFormatItem.class);
		xstream.alias("TMPrintTableFormat", TMPrintTableFormat.class);
		
		xstream.alias("ImpExPoCommon", ImpExPoCommon.class);
	}
	
	public boolean export(Properties ctx, int what, int obj_ID, String fname)	{
		ADMap exp = new ADMap();
		
		boolean ret = false ;
		
		if (what == 0) {
			ret = exp.exportWindow(ctx, obj_ID, null);
		} else if (what == 1) {
			ret = exp.exportProcess(ctx, obj_ID, null);
		} else if (what == 2) {
			ret = exp.exportTable(ctx, obj_ID, null);
		}
		
		if (ret == false)
			return false;
		
		try	{
			// FileWriter fw = new FileWriter(fname);
			FileOutputStream fos = new FileOutputStream(fname);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			
			osw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			
			xstream.toXML(exp, osw);
			
			osw.close();
			fos.flush();
			fos.close();
			
			log.warning("*** EXPORTACION REALIZADA SIN ERRORES ***");
			return true;
		}
		catch (IOException e)	{
			log.warning("*** EXPORTACION ERRORENEA ***");
			log.warning("No se ha podido guardar el fichero de exportacion.");
			return false;
		}
	}
	
	public boolean importXML(Properties ctx, String fname)	{
		
		ADMap map = null;
		try {
			FileInputStream fff = new FileInputStream(fname);
			String encoding = "UTF-8"; // Codificacion por defecto
			
			// Buscar cual es la codificacion que hay dentro de la cabecera del XML
			
			try {
				DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = docBuild.parse(fff);
				String x = doc.getXmlEncoding();
				
				if (x != null)
					encoding = x;
			} catch (ParserConfigurationException e) {
				// Ignored
			} catch (SAXException e) {
				// Ignored
			}
			
			fff.close();
			fff = new FileInputStream(fname);
			
			InputStreamReader rrr = new InputStreamReader(fff, encoding);

			map = (ADMap)xstream.fromXML(rrr);
			// fr.close();
			
			rrr.close();
			fff.close();
		}
		catch (IOException e)	{
			System.out.println("No se ha podido abrir el fichero " + fname + "\n" + e.toString());
			return false;
		}

		// Creamos la transaccion
		String trx = Trx.createTrxName();
		
		if (map.doImport(ctx, trx) == true)	{
			//	 Si no han habido problemas, completamos la transaccion.
			try {
				
				DB.commit(true, trx);
				log.severe("*** IMPORTACION REALIZADA SIN ERRORES ***");
			}
			catch (SQLException e)	{
				log.severe("*** IMPORTACION ERRONEA ERRORENEA ***");
				log.severe("No se han podido guardar los cambios: "+ e.toString());
				return false;
			}
		
		}
		else {
			try	{
				DB.rollback(true,trx);
				log.info("*** IMPORTACION ERRORENEA ***");
				log.info("Desaciendo Transacion.");
			}
			catch (SQLException e)	{
				log.fine("No se han podido hacer rollback de la transaccion: "+ e.toString());
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length < 2)	{
			System.out.println("Numero de argumentos erroneo");
			System.out.println("Uso: convert operacion fichero [AD_Window_ID]");
			System.out.println("La accion debe ser importar o exportar.");
		}
		String action = null;
		String filename=null;
		
		if (args.length >= 1)	{
			action = (String)args[0];
		}
		else {
			System.out.println("La accion debe ser importar o exportar.");
			return;
		}
		if (args.length >= 2)	{
			filename = (String) args[1];	
		}
		else {
			System.out.println("Debe indicar el nombre del fichero y el ID ");
			return;
		}
		
		if ((action.toLowerCase().startsWith("exportar-", 0)) && args.length < 3)	{
		
			System.out.println("Debe indicar el ID de la ventana a exportar.");
			System.out.println("Long: " + args.length);
			return;
		}
		
		//	Inicializamos OpenXpertya
		org.openXpertya.OpenXpertya.startup(true);
		Properties ctx = initEnv();

		Convert conv = new Convert();

		// En caso de querer importar un fichero
		if (action.compareToIgnoreCase("importar") == 0)	{
			conv.importXML(ctx, filename);
		}

		// Si deseamos exportar
		else if (action.compareToIgnoreCase("exportar-wind") == 0)	{
			// Creamos un mapa de conversion.
			conv.export(ctx, 0, Integer.parseInt((String) args[2]), filename);
		}
		else if (action.compareToIgnoreCase("exportar-proc") == 0)	{
			// Creamos un mapa de conversion.
			conv.export(ctx, 1, Integer.parseInt((String) args[2]), filename);
		}
		else if (action.compareToIgnoreCase("exportar-table") == 0)	{
			// Creamos un mapa de conversion.
			conv.export(ctx, 2, Integer.parseInt((String) args[2]), filename);
		}
		else {
			System.out.println("La accion debe ser importar o exportar.");
			return;
		}
	}

	
	public static Properties initEnv()	{
		Properties ctx = Env.getCtx();
		ctx.setProperty("#AD_User_ID", "100");
		ctx.setProperty("#AD_Client_ID", "0");
		ctx.setProperty("#AD_Org_ID", "0");
		Env.setCtx(ctx);
		return ctx;
	}

	/**
	 * @return Devuelve languages.
	 */
	public static String[] getLanguages() {
		return languages;
	}

	/**
	 * @param languages Fija o asigna languages.
	 */
	private static void setLanguages(String[] languages) {
		Convert.languages = languages;
	}
	
	private static Map<String, Map<Integer, Integer> > s_old2new = new TreeMap<String, Map<Integer, Integer>>();
	private static List<ImpExPoCommon.ImportedObj> s_importedObjects = new ArrayList<ImpExPoCommon.ImportedObj>();
	
	public static int getNewId(Class c, int oldID) {
		if (s_old2new.containsKey(c.getName())) {
			Map<Integer,Integer> m = s_old2new.get(c.getName());
			if (m.containsKey(oldID))
				return m.get(oldID);
		}
		return 0;
	}
	
	public static int setNewId(Class c, int oldID, int newID) {
		if (!s_old2new.containsKey(c.getName())) 
			s_old2new.put(c.getName(), new TreeMap<Integer, Integer>());
		
		Map<Integer,Integer> m = s_old2new.get(c.getName());
			
		m.put(oldID, newID);
		
		return 0;
	}
	
	public static List<ImpExPoCommon.ImportedObj> getImportedObjects() {
		return s_importedObjects;
	}
}

/**
 * la clase CLogger da problemas a la hora de exportar e importar y no es necesaria.
 * por lo que la ignoraremos
 *
 */
class LogConverter implements Converter {

    public boolean canConvert(Class clazz) {
    	return clazz.equals(CLogger.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer,
                    MarshallingContext context) {
    }

    public Object unmarshal(HierarchicalStreamReader reader,
                    UnmarshallingContext context) {
    	return CLogger.get ();
    }
}




