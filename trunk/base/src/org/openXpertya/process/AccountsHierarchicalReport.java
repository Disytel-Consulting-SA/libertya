package org.openXpertya.process;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.model.MAcctHierarchical;
import org.openXpertya.model.MElementValue;
import org.openXpertya.model.MTree;
import org.openXpertya.model.MTreeNode;
import org.openXpertya.util.DB;

public class AccountsHierarchicalReport extends SvrProcess {

	/** Organización a consultar */
	protected Integer    p_AD_Org_ID = null;
	/** Organización a consultar */
	protected Integer    p_C_ElementValue_ID = null;

	/** Subindice de ordenamiento de los datos del reporte */
	private int subindex = 0;
	/** Nivel del nodo incial del Arbol de Cuentas */
	private int initLevel = 0;
	
	/** Almacena el siguiente código interno a utilizar para un hijo de un nodo */
	private Map<Integer,BigInteger> nodeChildCode = new HashMap<Integer,BigInteger>();
	
	@Override
	protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) ;
        	else if (name.equalsIgnoreCase( "AD_Org_ID" )) {
        		BigDecimal tmp = ( BigDecimal )para[ i ].getParameter();
        		p_AD_Org_ID = tmp == null ? null : tmp.intValue();
        	} else if (name.equalsIgnoreCase( "C_ElementValue_ID" )) {
           		BigDecimal tmp = ( BigDecimal )para[ i ].getParameter();
           		p_C_ElementValue_ID = tmp == null ? null : tmp.intValue();
        	} else if (loadParameter(name, para[i])) ;
        	   // Carga de párametro en una subclase.
        	  else {	
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
	}

	/**
	 * Carga un parámetro. Este método lo deben implementar las subclases
	 * que requieran parámetros extras a los que tiene por defecto este proceso.
	 * @return Retorna <code>true</code> en caso de que el parámetro pertenezca al
	 * proceso específico, <code>false</code> en caso contrario.
	 */
	protected boolean loadParameter(String name, ProcessInfoParameter param) {
		// Los parámetros de esta clase se cargan en el prepare().
		return true;
	}
	
	@Override
	protected String doIt() throws Exception {
		
		// delete all rows older than a week
		DB.executeUpdate("DELETE FROM " + getReportTableName() + " WHERE Created < ('now'::text)::timestamp(6) - interval '7 days'");		
		// delete all rows in table with the given ad_pinstance_id
		DB.executeUpdate("DELETE FROM " + getReportTableName() + " WHERE AD_PInstance_ID = " + getAD_PInstance_ID());

		// Consulta que obtiene el ID del Arbol que le dá estructura a los elementos contables
		// que pertenecen al Elemento configurado en el Esquema Contable Primario de la Compañía.
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT e.AD_Tree_ID ");
		sql.append(" FROM AD_ClientInfo ci ");
		sql.append(" INNER JOIN C_AcctSchema_Element se ON (ci.C_AcctSchema1_ID = se.C_AcctSchema_ID) ");
		sql.append(" INNER JOIN C_Element e ON (se.C_Element_ID = e.C_Element_ID) ");
		sql.append(" WHERE se.ElementType = 'AC' AND ci.AD_Client_ID = ? ");

		int treeID = DB.getSQLValue(get_TrxName(), sql.toString(), getAD_Client_ID());

		// Se carga el árbol completo y se obtiene el nodo raíz.
		MTree mTree = new MTree(getCtx(), treeID, false,true, get_TrxName());
		MTreeNode rootNode = mTree.getRoot();
		// Si se especificó un elemento como filtro, entonces el nodo raíz es el que contiene
		// este elemento parámetro.
		if (p_C_ElementValue_ID != null)
			rootNode = rootNode.findNode(p_C_ElementValue_ID);
		
		// A partir del nodo raíz se efectura un Recorrido en Pre Orden para obtner una
		// enumeración de los nodos adecuada para la creación de las líneas del reporte.
		Enumeration nodes = rootNode.preorderEnumeration(); 
		// Por cada nodo, se crea la línea en la tabla temporal que muestra el reporte.
		subindex = 0;
		initLevel = rootNode.getLevel(); 
		for (; nodes.hasMoreElements() ;) {
			MTreeNode node = (MTreeNode)nodes.nextElement();
			createNodeAccountLine(node);
		}
		
		return null;
	}

	private void createNodeAccountLine(MTreeNode node) throws Exception {
		String acctCode        = null;
		String acctDescription = null;
		MElementValue ev = null;
		
		// Obtengo el ID del Elemento Contable a partir del Nodo.
		int elementValueID = node.getNode_ID();
		// Obtengo la Indentación del Elemento según los datos de Nivel del nodo.
		String indent = getIndentation(node);
		// Si el nodo es la raíz del arbol, entonces se obtiene los datos a partir
		// del nodo dado que el nodo no apunta a ningún elemento contable.
		if (node.getNode_ID() == 0) {
			acctDescription = indent + node.getName();
		// Aquí el nodo es un nodo interno que referencia a un elemento contable.
		} else {
			// Instancio el Elemento Contable para obtener código jerárquico 
			// y la descripción.
			ev = new MElementValue(getCtx(), elementValueID, get_TrxName());
			acctCode = ev.getValue();
			//acctDescription = ev.getDescription(); // Puede ser NULL, mejor el Name
			acctDescription = ev.getName();
		}
		
		// Creo una instancia de la estructura interna que contiene todos los datos
		// del elemento de cuenta.
		AccountElement accountElement = new AccountElement(
				acctCode,
				// Agrego la indentación del nodo a la descripción del elemento contable.
				indent + acctDescription.trim(),
				// Organización del registro de la Temporal para que funcione correctamente
				// el filtro del parámetro Organización.
				p_AD_Org_ID == 0 || ev == null ? 0 : ev.getAD_Org_ID(),
				// Nodo del árbol
				node, 
				// Si existe un filtro por Elemento, debo asignarle a todas las líneas este
				// elemento filtro ya que luego el Engine de reporte efectua nuevamente el filtro
				// sobre la tabla temporal, y de poner el ID del elemento original la línea será
				// filtrada luego por el Engine.
				p_C_ElementValue_ID != null ? p_C_ElementValue_ID : elementValueID,
				// Elemento de Cuenta
				ev, 
				// Subindice de la cuenta.
				subindex,
				// Código jerárquico calculado internamente
				getHierarchicalCode(node)
		);
		
		// Inicia la creación de la línea en la tabla temporal.
		createReportLine(accountElement);
		subindex++;
	}
	
	/**
	 * Crea la línea del reporte en la tabla temporal a partir de los datos del 
	 * Elemento de Cuenta.
	 * @param accountElement
	 * @throws Exception
	 */
	protected void createReportLine(AccountElement accountElement) throws Exception {
		// Se crea la línea del reporte a partir de los datos obtenidos del elemento.
		MAcctHierarchical line = new MAcctHierarchical(getCtx(), 0, get_TrxName());
		line.setAD_PInstance_ID(getAD_PInstance_ID());
		line.setSubindex(accountElement.subindex);
		line.setC_ElementValue_ID(accountElement.elementValueID);
		line.setAcct_Code(accountElement.code);
		line.setAcct_Description(accountElement.description);
		line.setAD_Org_ID(accountElement.orgID);
		if (!line.save()) {
			log.severe("Cannot save X_T_Acct_Hierarchical line. C_ElementValue_ID=" + line.getC_ElementValue_ID());
			throw new Exception("@ProcessRunError@");
		}
	}
	
	protected String getIndentation(MTreeNode node) {
		StringBuffer indent = new StringBuffer();
		for(int i = initLevel; i < node.getLevel(); i++)
			indent.append("---");
		
		return indent.toString() + " ";
	}
	
	/**
	 * Calcula un código con estructura jerárquica y semántica definida para ser
	 * utilizado en los reportes que necesiten este dato para calcular totales
	 * de cuentas carpetas.
	 */
	private String getHierarchicalCode(MTreeNode treeNode) {
		BigInteger code = null;
		int parentID = 0;
		
		if (treeNode.getLevel() == getInitLevel()) { 
			code = new BigInteger("10");
			nodeChildCode.put(treeNode.getNode_ID(), new BigInteger("10000"));	
		} else {
			// Obtengo el ID del nodo padre para obtener el próximo código
			// interno a setear al hijo.
			parentID = treeNode.getParent_ID();
			code = nodeChildCode.get(parentID);
			// Incremento el próximo código para el resto de los hijos.
			nodeChildCode.put(parentID, code.add(BigInteger.ONE));
			// Agrego el primer código interno para los hijos de este nodo.
			nodeChildCode.put(treeNode.getNode_ID(), new BigInteger(code.toString() + "000"));
		}
		
		return code.toString();
	}
	
	/**
	 * Estructura interna para almacenar todos los datos del elemento de cuenta.
	 */
	protected class AccountElement {
		protected String code;
		protected String description;
		protected int orgID;
		protected MTreeNode treeNode;
		protected int elementValueID;
		protected MElementValue elementValue;
		protected int subindex;
		protected String hierarchicalCode;
		
		
		public AccountElement() {
			super();
		}

		public AccountElement(String code, String description, int orgID, MTreeNode treeNode, int elementValueID, MElementValue elementValue, int subindex, String hierarchicalCode) {
			super();
			this.code = code;
			this.description = description;
			this.orgID = orgID;
			this.treeNode = treeNode;
			this.elementValueID = elementValueID;
			this.elementValue = elementValue;
			this.subindex = subindex;
			this.hierarchicalCode = hierarchicalCode; 
		}

		public boolean isRoot() {
			return elementValue == null;
		}
	
	}

	/**
	 * @return Returns the initLevel.
	 */
	protected int getInitLevel() {
		return initLevel;
	}
	
	protected String getReportTableName() {
		return "T_Acct_Hierarchical";
	}
	
	protected void sqlAppend(String clause, Object value, StringBuffer sql) {
		if (value != null)
			sql.append(clause);
	}

	protected void sqlAppend(String clause, boolean condition, StringBuffer sql) {
		if (condition)
			sql.append(clause);
	}
	
	protected int pstmtSetParam(int index, Object value, PreparedStatement pstmt) throws SQLException {
		int i = index;
		if (value != null)
			pstmt.setObject(i++, value);
		return i;
	}
}
