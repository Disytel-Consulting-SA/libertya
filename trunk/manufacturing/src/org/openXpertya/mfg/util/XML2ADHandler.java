/*
 * @(#)XML2ADHandler.java   14.jun 2007  Versión 2.2
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

/*package ;*/   // TODO: add package.
package org.openXpertya.mfg.util;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

import org.openXpertya.model.*;
import org.openXpertya.util.*;
import org.xml.sax.helpers.*;
import org.xml.sax.*;


/**
 * SAX Handler for parsing XML description of the GUI.
 *
 * @author Marco LOMBARDO, lombardo@mayking.com
 */
public class XML2ADHandler extends DefaultHandler {

    /**
     * 	XML2AD Handler
     */
    public XML2ADHandler () {
	m_MenuSeqNo = new int[100];   // TODO: remove this limit of 100 nested menu levels.
	m_Menu = new X_AD_Menu[100];
	m_Tab = new X_AD_Tab[100];
	m_MenuSeqNo[m_MenuIndex+1] = 100;
    }   // XML2ADHandler

    /** Default Entity Type */
    private String m_EntityType = "A"; // Applications, select name,value from ad_ref_list where ad_reference_id =245. TODO: put a param.
    /** Default Access Level */
    private String m_AccessLevel = "3"; // Client+Organization, select name,value from ad_ref_list where ad_reference_id =5. TODO: put a param.
    /** Menu array          */
    private X_AD_Menu[] m_Menu;
    private int m_MenuIndex = -1;
    private int[] m_MenuSeqNo;
    
    private X_AD_Window m_Window = null;
    private X_AD_Process m_Process = null;
    private int m_ProcessSeqNo = 10;
    private X_AD_Table m_Table = null;
    private X_AD_Column m_Column = null;
    private int m_ColumnSeqNo = 10;
    private X_AD_Tab[] m_Tab;
    private int m_TabSeqNo = 0;
    private int m_TabLevelNo = -1;
    private X_AD_Field m_Field = null;

    /** Set this if you want to delete a definition rather than create it. */
    private boolean m_DeleteMode = false;
    /** Switch between OpenXpertya Application Dictionary and Druid parser.*/   // TODO: split the three parser: for view, model and data.
    private boolean openxpertyaAD = false;
    /** Switch to OpenXpertya data.                                        */
    private boolean openxpertyadata = false;
    /** tablename for openxpertyadata                                      */
    private String d_tablename = null;
    private String d_rowname = null;
    private GenericPO genericPO = null;
    private boolean isDefaultData = false;
    private HashMap defaults = new HashMap();
    private int m_AD_Client_ID = 0;
    String m_ColumnList = "";
    /** Switch to Druid.                                                */
    private boolean druid = false;
    /** Constants                            */
    private final static String TABLE_TAG = "table";
    private final static String TABLEDATA_TAG = "table";
    private final static String ROWDATA_TAG = "row";
    private final static String COLUMNDATA_TAG = "column";
    private final static String COLUMN_TAG = "field";
    private final static String FIELD_TAG = "field";
    private final static String REFERENCE_TAG = "Reference";
    private final static String ELEMENT_TAG = "Element name";
    private final static String ATTRIB_TAG = "attrib";
    private final static String TAB_TAG = "tab";
    
    private static CLogger	log	= CLogger.getCLogger (XML2ADHandler.class);
    private final boolean DEBUG = true;   // TODO: remove this way to debug.
    /** Used when an object inherit the name from the contained object   */
    private final String TEMP_NAME = "__TempName__";

    /**
     * 	Receive notification of the start of an element.
     *
     * 	@param uri namespace
     * 	@param localName simple name
     * 	@param qName qualified name
     * 	@param atts attributes
     * 	@throws org.xml.sax.SAXException
     */
    public void startElement (String uri, String localName, String qName, Attributes atts)
	throws org.xml.sax.SAXException {
	// Check namespace.
	String elementValue = null;
	if ("".equals (uri))
	    elementValue = qName;
	else
	    elementValue = uri + localName;
	if (false)
	    log.info("startElement: "+elementValue);
	// default element, openxpertyaAD.
	if (elementValue.equals("default") && openxpertyaAD) {
	    if (atts.getValue("entitytype") != null) {
		m_EntityType = reverseReference("_Entity Type", atts.getValue("entitytype"));
		if (DEBUG)
		    log.info("entitytype is: "+m_EntityType);
	    }
	    // TODO: a better policy to modify things than delete everything and reinsert.
	    if (atts.getValue("firstdelete") != null) {
		m_DeleteMode = atts.getValue("firstdelete").equals("true") ? true : false;
		if (DEBUG)
		    log.info("firstdelete: " + m_DeleteMode);
		if (m_DeleteMode) {
		    // Delete menus.
		    String sql = "delete AD_Menu where EntityType='"+m_EntityType+"'";
		    int no = DB.executeUpdate(sql);
		    if (DEBUG) log.info("Menus deleted:"+no);
		    // Delete window
		    sql = "delete AD_Window where EntityType='"+m_EntityType+"'";
		    no = DB.executeUpdate(sql);
		    if (DEBUG) log.info("Window deleted:"+no);
		    // Delete process instances
		    sql = "delete AD_PInstance where AD_Process_ID in "
			+"(select AD_Process_ID from AD_Process where EntityType='"+m_EntityType+"')";
		    no = DB.executeUpdate(sql);
		    if (DEBUG) log.info("Process Instance deleted:"+no);
		    // Delete process
		    sql = "delete AD_Process where EntityType='"+m_EntityType+"'";
		    no = DB.executeUpdate(sql);
		    if (DEBUG) log.info("Process deleted:"+no);
		    // Delete tab
		    sql = "delete AD_Tab where EntityType='"+m_EntityType+"'";
		    no = DB.executeUpdate(sql);
		    if (DEBUG) log.info("Tab deleted:"+no);
		    // Delete fields
		    sql = "delete AD_Field where EntityType='"+m_EntityType+"'";
		    no = DB.executeUpdate(sql);
		    if (DEBUG) log.info("Fields deleted:"+no);
		}
	    }
	}
	// the "field" element is used by me and druid, and I don't want change name, nor wrote 2 parser.
	else if (elementValue.equals("openxpertyaAD")) {
	    openxpertyaAD = true;
	}
	// openxpertyadata element.
	// some fields i.e. "table" are commons.
	else if (elementValue.equals("openxpertyadata")) {
	    openxpertyadata = true;
	    if (atts.getValue("clientname") != null) {
		m_AD_Client_ID = getID("AD_Client", atts.getValue("clientname"));
		Env.setContext(Env.getCtx(), "#AD_Client_ID", m_AD_Client_ID);
		if (DEBUG) log.info("Client ID is:"+Env.getAD_Client_ID(Env.getCtx()));
	    }
	}
	// database element (Druid).
	else if (elementValue.equals("database")) {
	    druid = true;
	}
	// menu element.
	else if (elementValue.equals("menu")) {
	    // Mandatory.
	    String menuName = atts.getValue("name");
	    if (menuName == null)
		menuName = TEMP_NAME;   // Name from the object contained (window, report...)
	    // SeqNo.
	    m_MenuSeqNo[m_MenuIndex+1] = m_MenuSeqNo[m_MenuIndex+1] + 10;
	    // Get/New menu.
	    ++m_MenuIndex;
	    m_MenuSeqNo[m_MenuIndex+1] = 10;
	    m_Menu[m_MenuIndex] = new X_AD_Menu(Env.getCtx(), getID("AD_Menu", menuName), null);
	    if (m_Menu[m_MenuIndex].getAD_Menu_ID() == 0) {
		if (DEBUG)
		    log.info("Menu: "+menuName+" not found. Create.");
		m_Menu[m_MenuIndex].setEntityType(m_EntityType);
		m_Menu[m_MenuIndex].setIsReadOnly(false);
		m_Menu[m_MenuIndex].setIsSOTrx(false);
		m_Menu[m_MenuIndex].setName(menuName);
	    } else {
		if (DEBUG)
		    log.info("Menu: "+menuName+" founded.");
	    }
	    // Check menu attributes.
	    // Actually IsSummary is calculated.
	    if (atts.getValue("xxxxx") != null)
		m_Menu[m_MenuIndex].setIsSummary(atts.getValue("issummary").equals("true") ? true : false);
	    // This assign the AD_Menu_ID, is necessary for set Parent_ID in children.
	    m_Menu[m_MenuIndex].save();
	}
	// window element.
	else if (elementValue.equals("window")) {
	    // Reset Tab numbering.
	    m_TabSeqNo = 0;
	    m_TabLevelNo = -1;
	    // Mandatory.
	    String windowName = atts.getValue("name");
	    if (windowName == null)
		windowName = m_Menu[m_MenuIndex].getName();   // Take the name from the menu.
	    // Get/New window.
	    m_Window = new X_AD_Window(Env.getCtx(), getID("AD_Window", windowName),null);
	    if (m_Window.getAD_Window_ID() == 0) {
		if (DEBUG)
		    log.info("Window: "+windowName+" not found. Create.");
		m_Window.setEntityType(m_EntityType);
		m_Window.setIsSOTrx(false);
		m_Window.setName(windowName);
		m_Window.setWindowType(reverseReference("AD_Window Types", "Maintain"));
		// This assign the AD_Menu_ID, is necessary for set Parent_ID in children.
		m_Window.save();
	    } else {
		if (DEBUG)
		    log.info("Window: "+windowName+" founded.");
	    }
	}
	// tab element.
	else if (elementValue.equals(TAB_TAG)) {
	    m_TabSeqNo = m_TabSeqNo+10;
	    m_TabLevelNo++;
	    // Mandatory.
	    String tabName = atts.getValue("name");
	    if (DEBUG) log.info("tab:"+tabName+", level:"+m_TabLevelNo);
	    // Get/New window.
	    m_Tab[m_TabLevelNo] = new X_AD_Tab(Env.getCtx(), getIDWithMaster("AD_Tab", tabName, "AD_Window", m_Window.getName()),null);
	    if (DEBUG) log.info("x_ad_tab called");
	    if (m_Tab[m_TabLevelNo].getAD_Tab_ID() == 0) {
		if (DEBUG)
		    log.info("Tab: "+tabName+" not found. Create.");
		m_Tab[m_TabLevelNo].setName(tabName);
		m_Tab[m_TabLevelNo].setAD_Table_ID(getID("AD_Table", atts.getValue("tablename")));
		m_Tab[m_TabLevelNo].setAD_Window_ID(m_Window.getAD_Window_ID());
		m_Tab[m_TabLevelNo].setSeqNo(m_TabSeqNo);
		m_Tab[m_TabLevelNo].setTabLevel(m_TabLevelNo);
		m_Tab[m_TabLevelNo].setIsSingleRow(false);
		m_Tab[m_TabLevelNo].setIsTranslationTab(false);
		m_Tab[m_TabLevelNo].setIsReadOnly(false);
		m_Tab[m_TabLevelNo].setHasTree(false);
		m_Tab[m_TabLevelNo].setIsSortTab(false); 
		m_Tab[m_TabLevelNo].setEntityType(m_EntityType);
	    } else {
		if (DEBUG)
		    log.info("Tab: "+tabName+" founded.");
	    }
	    m_Tab[m_TabLevelNo].setSeqNo(m_TabSeqNo);
	    m_Tab[m_TabLevelNo].setTabLevel(m_TabLevelNo);
	    m_Tab[m_TabLevelNo].save();
	    // syncfields should be controlled after save cause TAB_ID is needed.
	    if (atts.getValue("syncfields") != null && atts.getValue("syncfields").equals("Y")) {
		boolean isSameLine = false;
		String sql = "select coalesce(e.PrintName, c.Name), c.Description, c.AD_Column_ID, c.FieldLength, c.EntityType, c.ColumnName"
		    + ", (select tablename from AD_Table where AD_Table_ID = c.AD_Table_ID) as tablename"
		    + " FROM    AD_Column c left join AD_Element e using(AD_Element_ID)"
		    + " WHERE NOT EXISTS (SELECT * FROM AD_Field f"
		    + " WHERE c.AD_Column_ID=f.AD_Column_ID"
		    + " AND c.AD_Table_ID=? AND f.AD_Tab_ID=?)"
		    + " AND AD_Table_ID=? AND NOT (UPPER(c.Name) LIKE 'CREATED%' OR UPPER(c.Name) LIKE 'UPDATED%')"
		    + " AND c.IsActive='Y'";
		try {
		    PreparedStatement pstmt = DB.prepareStatement(sql);
		    pstmt.setInt(1, m_Tab[m_TabLevelNo].getAD_Table_ID());
		    pstmt.setInt(2, m_Tab[m_TabLevelNo].getAD_Tab_ID());
		    pstmt.setInt(3, m_Tab[m_TabLevelNo].getAD_Table_ID());
		    ResultSet rs = pstmt.executeQuery();
		    while (rs.next()) {
			//m_Column = new X_AD_Column(Env.getCtx(), rs.getInt(3)); // Get the column.
			m_Field = new X_AD_Field(Env.getCtx(), 0 , null);
			m_Field.setAD_Column_ID(rs.getInt(3));
			m_Field.setAD_Tab_ID(m_Tab[m_TabLevelNo].getAD_Tab_ID());
			m_Field.setEntityType(m_EntityType);
			m_Field.setIsCentrallyMaintained(true);
			m_Field.setIsDisplayed(!rs.getString(6).equals(rs.getString(7)+"_ID"));
			m_Field.setIsEncrypted(false);
			m_Field.setIsFieldOnly(false);
			m_Field.setIsHeading(false);
			m_Field.setIsReadOnly(false);
			m_Field.setIsSameLine(isSameLine);
			m_Field.setName(rs.getString(1));
			// Save.
			m_Field.save();
			// Use 2 lines.
			if (m_Field.isDisplayed())
			    isSameLine = !isSameLine;
		    }
		    rs.close();
		    pstmt.close();
		    pstmt = null;
		}
		catch (Exception e) {		    
		    log.log(Level.SEVERE, "syncfields:"+e);
		}
	    }   // syncfields.
		// Adjust parent link informations.
	    // TODO: some convention to be documented.
	    if (m_TabLevelNo > 0) {
		String sql = "update AD_Column"
		    +" set isParent='Y'"
		    +" where ColumnName = (select TableName||'_ID' from AD_Table where AD_Table_ID="+m_Tab[m_TabLevelNo-1].getAD_Table_ID()+")"
		    +" and AD_Table_ID="+m_Tab[m_TabLevelNo].getAD_Table_ID();
		int no = DB.executeUpdate(sql);
		if (DEBUG)
		    log.info("Parent link for "+tabName+":"+no);
	    }
	}

	// field element.
	else if (elementValue.equals(FIELD_TAG) && openxpertyaAD) {
	    // Initialize.
	    String name = atts.getValue("name");
	    // name is mandatory.
	    if (name != null) {
		m_Field = new X_AD_Field(Env.getCtx(),getIDWithMaster("AD_Field", name, "AD_Tab", m_Tab[m_TabLevelNo].getAD_Tab_ID()),null);
		if (m_Field.getAD_Field_ID() == 0) {
		    if (DEBUG)
			log.info(elementValue+": "+name+" not found. Create.");
		    m_Field.setAD_Tab_ID(m_Tab[m_TabLevelNo].getAD_Tab_ID());
		    m_Field.setEntityType (m_EntityType);
		    m_Field.setIsCentrallyMaintained (true);
		    m_Field.setIsDisplayed (true);
		    m_Field.setIsEncrypted (false);
		    m_Field.setIsFieldOnly (false);
		    m_Field.setIsHeading (false);
		    m_Field.setIsReadOnly (false);
		    m_Field.setIsSameLine (false);
		    m_Field.setName(name);
		    m_Field.save();
		}
		else {
		    if (DEBUG)
			log.info(elementValue+": "+name+" found.");
		    // Set Entity Type or it won't be removed in case you remove the Field.
		    m_Field.setEntityType (m_EntityType);
		}
		if (m_Field.getAD_Field_ID() != 0) { // always.
		    // Init the associated column.
		    if (atts.getValue("columnname") != null) {
			if (DEBUG)
			    log.info(elementValue+": "+name+": init column.");
			m_Column = new X_AD_Column(Env.getCtx(),getIDWithMasterAndColumn("AD_Column", "ColumnName", atts.getValue("columnname"),"AD_Table", m_Tab[m_TabLevelNo].getAD_Table_ID()),null);
			m_Field.setAD_Column_ID(m_Column.getAD_Column_ID());
		    }
		    else {
			// no "columnname" so field shoud have a Column already.
			m_Column = new X_AD_Column(Env.getCtx(), m_Field.getAD_Column_ID(),null);
		    }
		    //
		    if (atts.getValue("mandatory") != null && atts.getValue("mandatory").equals("Y"))
			m_Column.setIsMandatory(true);
		    if (atts.getValue("identifierno") != null) {
			m_Column.setIsIdentifier(true);
			m_Column.setSeqNo(Integer.valueOf(atts.getValue("identifierno")).intValue());
		    }
		}
	    }
	}

	// process element.
	else if (elementValue.equals("process")) {
	    // Name.
	    String name = atts.getValue("name");
	    if (name == null) {
		// Is there a field?
		if (m_Field != null)
		    name = m_Field.getName();
		// Is there a menu?
		else if (m_MenuIndex >= 0)
		    name = m_Menu[m_MenuIndex].getName();
	    }
	    // Get/New process.
	    m_Process = new X_AD_Process(Env.getCtx(), getID("AD_Process", name),null);
	    if (m_Process.getAD_Process_ID() == 0) {
		if (DEBUG)
		    log.info(elementValue+": "+name+" not found. Create.");
		m_Process.setEntityType(m_EntityType);
		m_Process.setName(name);
		m_Process.setValue(name);
		m_Process.setAccessLevel(m_AccessLevel);
		m_Process.setIsReport(false);
	    } else {
		if (DEBUG)
		    log.info(elementValue+": "+name+" founded.");
	    }
	    // Check process attributes.
	    //if (atts.getValue("jasperreport") != null) {
		//m_Process.setJasperreport(atts.getValue("jasperreport"));
		//m_Process.setClassname("ru.openxpertya.report.RusReportStarter");
	    //}
	    if (atts.getValue("classname") != null)
		m_Process.setClassname(atts.getValue("classname"));
	    if (atts.getValue("description") != null)
		m_Process.setDescription(atts.getValue("description"));
	    if (atts.getValue("help") != null)
		m_Process.setHelp(atts.getValue("help"));
	    // This assign the AD_Menu_ID, is necessary for set Parent_ID in children.
	    m_Process.save();
	    // Reset SeqNo for parameters.
	    m_ProcessSeqNo = 10;
	}

	// processpara element.
	else if (elementValue.equals("processpara")) {
	    // Name. Mandatory.
	    String name = atts.getValue("name");
	    // Get/New process.
	    X_AD_Process_Para m_ProcessPara =
		new X_AD_Process_Para(Env.getCtx(),getIDWithMaster("AD_Process_Para", name, "AD_Process", m_Process.getName()),null);
	    if (m_ProcessPara.getAD_Process_Para_ID() == 0) {
		if (DEBUG)
		    log.info(elementValue+": "+name+" not found. Create.");
		m_ProcessPara.setEntityType(m_EntityType);
		m_ProcessPara.setName(name);
		m_ProcessPara.setAD_Process_ID(m_Process.getAD_Process_ID());
		m_ProcessPara.setColumnName("to_be_defined");
		m_ProcessPara.setIsCentrallyMaintained (false);
		m_ProcessPara.setIsRange (false);
		m_ProcessPara.setIsMandatory (false);
		m_ProcessPara.setFieldLength(0);
	    } else {
		if (DEBUG)
		    log.info(elementValue+": "+name+" founded.");
	    }
	    // Adjust sequence number.
	    m_ProcessPara.setSeqNo(m_ProcessSeqNo);
	    // Check process attributes.
	    if (atts.getValue("reference") != null)
		m_ProcessPara.setAD_Reference_ID(getID("AD_Reference", atts.getValue("reference")));
	    if (atts.getValue("columnname") != null) {
		m_ProcessPara.setColumnName(atts.getValue("columnname"));
		if (DEBUG)
		    log.info(elementValue+": columnname "+atts.getValue("columnname"));
	    }
	    if (atts.getValue("name") != null)
		m_ProcessPara.setName(atts.getValue("name"));
	    //
	    if (atts.getValue("mandatory") != null)
		m_ProcessPara.setIsMandatory(atts.getValue("mandatory").equals("Y") ? true : false);
	    // This assign the AD_Menu_ID, is necessary for set Parent_ID in children.
	    m_ProcessPara.save();
	    m_ProcessSeqNo = m_ProcessSeqNo + 10;
	}

	// table element.
	else if (elementValue.equals(TABLE_TAG) && druid) {
	    m_ColumnSeqNo = 10; // reset SeqNo.
	    // Mandatory.
	    String tableName = atts.getValue("name");
	    if (DEBUG)
		log.info(tableName+" starts ================================================================================");
	    // Get/New window.
	    m_Table = new X_AD_Table(Env.getCtx(), getID("AD_Table", tableName),null);
	    if (m_Table.getAD_Table_ID() == 0) {
		if (DEBUG)
		    log.info("Table: "+tableName+" not found. Create.");
		m_Table.setEntityType(m_EntityType);
		m_Table.setName(tableName);
		m_Table.setTableName(tableName);
		m_Table.setIsView(false);
		m_Table.setAccessLevel(reverseReference("AD_Table Access Levels", "Client+Organization"));
		m_Table.setIsSecurityEnabled(false);
		m_Table.setIsDeleteable(true);
		m_Table.setIsHighVolume(false);
		m_Table.setIsChangeLog(false);
		m_Table.setReplicationType(reverseReference("AD_Table Replication Type", "Local"));
		m_Table.save();
	    } else {
		if (DEBUG)
		    log.info("Table: "+tableName+" founded.");
	    }
	}

	// column element.
	else if (elementValue.equals(COLUMN_TAG) && druid) {
	    // Mandatory.
	    String columnName = atts.getValue("name");
	    // Get/New window.
	    m_Column = new X_AD_Column(Env.getCtx(),getIDWithMasterAndColumn("AD_Column", "ColumnName", columnName, "AD_Table", m_Table.getAD_Table_ID()),null);
	    if (m_Column.getAD_Column_ID() == 0) {
		if (DEBUG)
		    log.info("Column: "+columnName+" not found. Create.");
		m_Column.setName(columnName);
		m_Column.setVersion(Env.ZERO);
		m_Column.setEntityType(m_EntityType);
		m_Column.setColumnName(columnName);
		m_Column.setAD_Table_ID(m_Table.getAD_Table_ID());
		m_Column.setAD_Reference_ID(10);
		m_Column.setIsKey(false);
		m_Column.setIsParent(false);
		m_Column.setIsMandatory(false);
		m_Column.setIsUpdateable(true);
		m_Column.setIsIdentifier(false);
		m_Column.setSeqNo(m_ColumnSeqNo);
		m_ColumnSeqNo = m_ColumnSeqNo+10;
		m_Column.setIsTranslated(false);
		m_Column.setIsEncrypted(false);
		m_Column.setIsSelectionColumn(false);
		m_Column.setIsAlwaysUpdateable(false);
	    } else {
		if (DEBUG)
		    log.info("Column: "+columnName+" founded.");
		// Set Entity Type or it won't be remove in future when you remove the Column.
		m_Column.setEntityType(m_EntityType);
	    }
	    // TODO: druid is configurable so this sqlType can be parameter.
	    if (atts.getValue("sqlType") != null) {
		if (atts.getValue("sqlType").startsWith("number") || atts.getValue("sqlType").startsWith("numeric")) {
		    m_Column.setAD_Reference_ID(getID("AD_Reference", "Number"));
		    m_Column.setFieldLength(22);
		}
		else if (atts.getValue("sqlType").indexOf("char") != -1 && atts.getValue("sqlType").endsWith("(1)")) {
		    m_Column.setAD_Reference_ID(20 /*getID("AD_Reference", "YesNo")*/); // do not work with YesNo.
		    m_Column.setFieldLength(1);
		}
		else if (atts.getValue("sqlType").indexOf("char") != -1)
		    m_Column.setAD_Reference_ID(getID("AD_Reference", "String"));
		else if (atts.getValue("sqlType").indexOf("date") != -1 || atts.getValue("sqlType").indexOf("time") != -1) {
		    m_Column.setAD_Reference_ID(getID("AD_Reference", "DateTime"));
		    m_Column.setFieldLength(7);
		}
		// FieldLenght
		int i_s = atts.getValue("sqlType").indexOf("(");
		int i_e1 = atts.getValue("sqlType").indexOf(",");
		int i_e2 = atts.getValue("sqlType").indexOf(")");
		if (i_s != -1)
		    m_Column.setFieldLength(Integer.valueOf(atts.getValue("sqlType").substring(i_s+1, i_e1 != -1 ? i_e1 : i_e2)).intValue());
	    }
	    // Boring things that you will be happy to start forgetting about.
	    // AD_Client_ID
	    if (m_Column.getColumnName().toUpperCase().equals("AD_CLIENT_ID")) {
		m_Column.setAD_Reference_ID(19);
		m_Column.setAD_Val_Rule_ID(103);
		m_Column.setFieldLength(22);
		m_Column.setIsUpdateable(false);
		m_Column.setDefaultValue("@AD_Client_ID@");
	    }
	    // AD_Org_ID
	    else if (m_Column.getColumnName().toUpperCase().equals("AD_ORG_ID")) {
		m_Column.setAD_Reference_ID(19);
		m_Column.setAD_Val_Rule_ID(104);
		m_Column.setIsUpdateable(false);
		m_Column.setDefaultValue("@AD_Org_ID@");
		m_Column.setFieldLength(22);
	    }
	    // Created, Updated
	    else if (m_Column.getColumnName().toUpperCase().equals("CREATED") ||
		     m_Column.getColumnName().toUpperCase().equals("UPDATED")) {
		m_Column.setAD_Reference_ID(16);
		m_Column.setIsUpdateable(false);
	    }
	    // CreatedBy, UpdatedBY
	    else if (m_Column.getColumnName().toUpperCase().equals("CREATEDBY") ||
		     m_Column.getColumnName().toUpperCase().equals("UPDATEDBY")) {
		m_Column.setAD_Reference_ID(18);
		m_Column.setAD_Reference_Value_ID(110); // AD_User.
		m_Column.setIsUpdateable(false);
	    }
	    // ID
	    else if (m_Column.getColumnName().toUpperCase().equals(m_Table.getName().toUpperCase()+"_ID")) {
		m_Column.setAD_Reference_ID(13);
		m_Column.setIsUpdateable(false);
		m_Column.setIsKey(true);
		m_Column.setIsMandatory(true);
		m_Column.setFieldLength(22);
	    }
	    // Name
	    else if (m_Column.getColumnName().toUpperCase().equals("NAME")) {
		m_Column.setIsIdentifier(true);
	    }
	    // FK
	    else if (m_Column.getColumnName().endsWith("_ID")) {
		m_Column.setAD_Reference_ID(19);
		m_Column.setIsParent(false);
	    }
	    // Button
	    // TODO: convention for buttons ok?
	    else if (m_Column.getColumnName().endsWith("ING")) {
		m_Column.setAD_Reference_ID(28);
		m_Column.setFieldLength(1);
	    }
	    // Setup Element.
	    X_AD_Element element =
		new X_AD_Element(Env.getCtx(), getIDWithColumn("AD_Element", "ColumnName", m_Column.getColumnName()),null);
	    if (element.getAD_Element_ID() == 0) {
		element.setColumnName(m_Column.getColumnName());
		element.setEntityType(m_EntityType);
		element.setPrintName(m_Column.getColumnName());
		element.setName(m_Column.getColumnName());
		element.save();
	    }
	    m_Column.setAD_Element_ID(element.getAD_Element_ID());
	    m_Column.save();
	    // This is to delete non existent column.
	    if (m_ColumnList.length() == 0)
		m_ColumnList = String.valueOf(m_Column.getAD_Column_ID());
	    else
		m_ColumnList = m_ColumnList+","+String.valueOf(m_Column.getAD_Column_ID());
	} 
	// attrib element.
	// TODO: I add Reference and Element name on druid but in fact they are not Model but View.
	else if (elementValue.equals(ATTRIB_TAG)) {
	    String name = atts.getValue("name");
	    if (name != null) {
		if (name.equals("NotN"))
		    m_Column.setIsMandatory(true);
		else if (name.equals("Reference"))
		    m_Column.setAD_Reference_ID(getID("AD_Reference", atts.getValue("value")));
		else if (name.equals("Element name")) {
		    X_AD_Element m_Element = new X_AD_Element(Env.getCtx(),getIDWithColumn("AD_Element", "ColumnName", m_Column.getColumnName()),null);
		    if (m_Element.getAD_Element_ID() == 0) {
			m_Element.setColumnName(m_Column.getColumnName());
			m_Element.setEntityType(m_EntityType);
		    }
		    m_Element.setName(atts.getValue("value"));
		    m_Element.setPrintName(atts.getValue("value"));
		    m_Element.save();
		    m_Column.setAD_Element_ID(m_Element.getAD_Element_ID());
		}
	    }
	}

	/* ****************************************
	   openxpertyadata Handler.
	   **************************************** */
	// table element, openxpertyadata
	else if (elementValue.equals(TABLEDATA_TAG) && openxpertyadata) {
	    d_tablename = atts.getValue("name");
	    if (DEBUG) {
		log.info("======================================");
		log.info("tabledata name: "+d_tablename);
	    }
	}
	// row element, openxpertyadata
	else if (elementValue.equals(ROWDATA_TAG) && openxpertyadata) {
	    d_rowname = atts.getValue("name");
	    log.info("row name:"+d_rowname);
	    Properties ctx = Env.getCtx();
	    ctx.setProperty("openxpertyadataTable_ID", String.valueOf(getID("AD_Table", d_tablename)));
	    log.info("TableID setted:"+getID("AD_Table", d_tablename));
	    // name can be null if there are keyXname attributes.
	    if (d_rowname != null)
		genericPO = new GenericPO(Env.getCtx(), getID(d_tablename, d_rowname),null);
	    // keyXname and lookupkeyXname.
	    else {
		String sql = "select * from "+d_tablename;
		String whereand = " where";
		String t_tablename = null;
		String CURRENT_KEY = "key1name";
		if (atts.getValue(CURRENT_KEY) != null) {
		    t_tablename = atts.getValue(CURRENT_KEY).substring(0, atts.getValue(CURRENT_KEY).length()-3);
		    sql = sql+whereand+" "+atts.getValue(CURRENT_KEY)+"="+getID(t_tablename, atts.getValue("lookup"+CURRENT_KEY));
		    whereand = " and";
		}
		CURRENT_KEY = "key2name";
		if (atts.getValue(CURRENT_KEY) != null) {
		    t_tablename = atts.getValue(CURRENT_KEY).substring(0, atts.getValue(CURRENT_KEY).length()-3);
		    sql = sql+whereand+" "+atts.getValue(CURRENT_KEY)+"="+getID(t_tablename, atts.getValue("lookup"+CURRENT_KEY));
		    whereand = " and";
		}
		if (DEBUG)
		    log.info("keyXname sql:"+sql);
		// Load GenericPO from rs, in fact ID could not exist e.g. Attribute Value
		try {
		    PreparedStatement pstmt = DB.prepareStatement(sql);
		    ResultSet rs = pstmt.executeQuery();
		    if (rs.next()) {
			if (DEBUG)
			    log.info("init GenericPo from rs.");
			genericPO = new GenericPO(Env.getCtx(), rs,null);
		    }
		    else {
			if (DEBUG)
			    log.info("new GenericPo.");
			genericPO = new GenericPO(Env.getCtx(), 0, null);
			// set keyXname.
			CURRENT_KEY = "key1name";
			if (atts.getValue(CURRENT_KEY) != null) {
			    t_tablename = atts.getValue(CURRENT_KEY).substring(0, atts.getValue(CURRENT_KEY).length()-3);
			    genericPO.setValueNoCheck(atts.getValue(CURRENT_KEY),
						      new Integer(getID(t_tablename, atts.getValue("lookup"+CURRENT_KEY))));
			}
			CURRENT_KEY = "key2name";
			if (atts.getValue(CURRENT_KEY) != null) {
			    t_tablename = atts.getValue(CURRENT_KEY).substring(0, atts.getValue(CURRENT_KEY).length()-3);
			    genericPO.setValueNoCheck(atts.getValue(CURRENT_KEY),
						      new Integer(getID(t_tablename, atts.getValue("lookup"+CURRENT_KEY))));
			}
		    }
		    rs.close();
		    pstmt.close();
		    pstmt = null;
		}
		catch (Exception e) {
		    log.log(Level.SEVERE, "keyXname attribute. init from rs error."+e);
		}
	    }
	    // reset Table ID for GenericPO.
	    ctx.setProperty("openxpertyadataTable_ID", "0");
	    // for debug GenericPO.
	    if (false) {
		POInfo poInfo = POInfo.getPOInfo(Env.getCtx(), getID("AD_Table", d_tablename));
		if (poInfo == null)
		    log.info("poInfo is null.");
		for (int i = 0; i < poInfo.getColumnCount(); i++) {
		    log.info(d_tablename+" column: "+poInfo.getColumnName(i));
		}
	    }
	    // if new. TODO: no defaults for keyXname.
	    if (d_rowname != null && ((Integer)(genericPO.get_Value(d_tablename+"_ID"))).intValue() == 0) {
		log.info("new genericPO, name:"+d_rowname);
		genericPO.setValue("Name", d_rowname);
		// Set defaults.
		HashMap thisDefault = (HashMap)defaults.get(d_tablename);
		if (thisDefault != null) {
		    Iterator iter = thisDefault.values().iterator();
		    ArrayList thisValue = null;
		    while (iter.hasNext()) {
			thisValue = (ArrayList)iter.next();
			if (((String)(thisValue.get(2))).equals("String"))
			    genericPO.setValue((String)thisValue.get(0), (String)thisValue.get(1));
			else if (((String)(thisValue.get(2))).equals("Integer"))
			    genericPO.setValue((String)thisValue.get(0), Integer.valueOf((String)thisValue.get(1)));
			else if (((String)(thisValue.get(2))).equals("Boolean"))
			    genericPO.setValue((String)thisValue.get(0), new Boolean(((String)thisValue.get(1)).equals("true") ? true : false));
		    }
		}
	    }
	    else if (DEBUG)
		log.info("Generic ID is: "+genericPO.get_Value(d_tablename+"_ID"));
	}
	// DEFAULT column element, openxpertyadata
	else if (isDefaultData && elementValue.equals(COLUMNDATA_TAG) && openxpertyadata) {
	    // defaults for current table.
	    HashMap thisDefault = (HashMap)defaults.get(d_tablename);
	    if (thisDefault == null) {
		thisDefault = new HashMap();  // do not exist, create.
		if (DEBUG)
		    log.info("New HashMap for "+d_tablename);
	    }
	    ArrayList thisValue = new ArrayList(3);
	    thisValue.add(atts.getValue("name"));
	    thisValue.add(atts.getValue("value"));
	    thisValue.add(atts.getValue("class"));
	    // store default for this column.
	    thisDefault.put(atts.getValue("name"), thisValue);
	    // store back defaults for current table.
	    defaults.put(d_tablename, thisDefault);
	}
	// column element, openxpertyadata
	else if (elementValue.equals(COLUMNDATA_TAG) && openxpertyadata) {
	    if (atts.getValue("value") != null) {
		if (atts.getValue("class") == null || atts.getValue("class").equals("String")) {
		    if (DEBUG)log.info("value to set: is string");
		    genericPO.setValue(atts.getValue("name"), atts.getValue("value"));
		}
		else if (atts.getValue("class").equals("Integer")) {
		    if (DEBUG) log.info("value to set: is int");
		    genericPO.setValue(atts.getValue("name"), Integer.valueOf(atts.getValue("value")));
		}
		else if (atts.getValue("class").equals("Boolean")) {
		    if (DEBUG) log.info("value to set: is bool");
		    genericPO.setValue(atts.getValue("name"), new Boolean(atts.getValue("value").equals("true") ? true : false));
		}
	    }
	    else if (atts.getValue("lookupname") != null) {
		String m_tablename = atts.getValue("name").substring(0, atts.getValue("name").length()-3);
		if (DEBUG) log.info("tablename for lookup:"+m_tablename);
		genericPO.setValue(atts.getValue("name"), new Integer(getID(m_tablename, atts.getValue("lookupname"))));
	    }
	}
	// default element, openxpertyadata
	if (elementValue.equals("default") && openxpertyadata) {
	    isDefaultData = true;
	}
    }   // startElement

    /**
     * Reverse the value of a Reference
     *
     * @param referenceName
     * @param value
     */
    public String reverseReference (String referenceName, String name) {
	// TODO: substitute with the use of PO.getAllIDs
	String retValue = null;
	String sql = "select Value from ad_ref_list where AD_Reference_ID = "
	    + "(select AD_Reference_ID from AD_Reference where Name=?)"
	    + " and Name=?";
	try {
	    PreparedStatement pstmt = DB.prepareStatement(sql);
	    pstmt.setString(1, referenceName);
	    pstmt.setString(2, name);
	    ResultSet rs = pstmt.executeQuery();
	    if (rs.next())
		retValue = rs.getString(1);
	    rs.close();
	    pstmt.close();
	    pstmt = null;
	}
	catch (Exception e) {
	    log.log(Level.SEVERE, "reverseRefernce:"+e);
	}
	return retValue;
    }

    /**
     * Get ID from Name for a table.
     * TODO: substitute with PO.getAllIDs
     *
     * @param tableName
     * @param name
     */
    public int getID (String tableName, String name) {
	int id = 0;
	String sql = "select "+tableName+"_ID from "+tableName+" where name=?";
	if (!tableName.startsWith("AD_"))
	    sql = sql + " and AD_Client_ID=?";
	try {
	    PreparedStatement pstmt = DB.prepareStatement(sql);
	    pstmt.setString(1, name);
	    if (!tableName.startsWith("AD_"))
		pstmt.setInt(2, m_AD_Client_ID);
	    ResultSet rs = pstmt.executeQuery();
	    if (rs.next())
		id = rs.getInt(1);
	    rs.close();
	    pstmt.close();
	    pstmt = null;
	}
	catch (Exception e) {
	    log.log(Level.SEVERE, "getID:"+e);
	}
	return id;
    }
    
    /**
     * Get ID from column value for a table.
     *
     * @param tableName
     * @param columName
     * @param name
     */
    public int getIDWithColumn (String tableName, String columnName, Object value) {
	int id = 0;
	String sql = "select "+tableName+"_ID from "+tableName+" where "+columnName+"=?";
	if (!tableName.startsWith("AD_"))
	    sql = sql + " and AD_Client_ID=?";
	try {
	    PreparedStatement pstmt = DB.prepareStatement(sql);
	    if (value instanceof String)
		pstmt.setString(1, (String)value);
	    else if (value instanceof Integer)
		pstmt.setInt(1, ((Integer)value).intValue());
	    if (!tableName.startsWith("AD_"))
		pstmt.setInt(2, m_AD_Client_ID);
	    ResultSet rs = pstmt.executeQuery();
	    if (rs.next())
		id = rs.getInt(1);
	    rs.close();
	    pstmt.close();
	    pstmt = null;
	}
	catch (Exception e) {	    
	    log.log(Level.SEVERE, "getID:"+e);
	}
	return id;
    }

    /**
     * Get ID from Name for a table with a Master reference.
     *
     * @param tableName
     * @param name
     * @param tableNameMaster
     * @param nameMaster
     */
    public int getIDWithMaster (String tableName, String name, String tableNameMaster, String nameMaster) {
	int id = 0;
	String sql = "select "+tableName+"_ID from "+tableName+" where name=? and "
	    + tableNameMaster+"_ID = (select "+tableNameMaster+"_ID from "+tableNameMaster+" where name=?)";
	try {
	    PreparedStatement pstmt = DB.prepareStatement(sql);
	    pstmt.setString(1, name);
	    pstmt.setString(2, nameMaster);
	    ResultSet rs = pstmt.executeQuery();
	    if (rs.next())
		id = rs.getInt(1);
	    rs.close();
	    pstmt.close();
	    pstmt = null;
	}
	catch (Exception e) {	   
	    log.log(Level.SEVERE, "getIDWithMaster:"+e);
	}
	return id;
    }

    /**
     * Get ID from Name for a table with a Master reference.
     *
     * @param tableName
     * @param name
     * @param tableNameMaster
     * @param nameMaster
     */
    public int getIDWithMasterAndColumn (String tableName, String columnName, String name, String tableNameMaster, int masterID) {
	int id = 0;
	String sql = "select "+tableName+"_ID from "+tableName+" where "+columnName+"=? and "
	    + tableNameMaster+"_ID =?";
	if (false)
	    log.info("getIDWithMaster: sql:"+sql);
	try {
	    PreparedStatement pstmt = DB.prepareStatement(sql);
	    pstmt.setString(1, name);
	    pstmt.setInt(2, masterID);
	    ResultSet rs = pstmt.executeQuery();
	    if (rs.next())
		id = rs.getInt(1);
	    rs.close();
	    pstmt.close();
	    pstmt = null;
	}
	catch (Exception e) {	   
	    log.log(Level.SEVERE, "getIDWithMaster:"+e);
	}
	return id;
    }

    /**
     * Get ID from Name for a table with a Master reference ID.
     *
     * @param tableName
     * @param name
     * @param tableNameMaster
     * @param masterID
     */
    public int getIDWithMaster (String tableName, String name, String tableNameMaster, int masterID) {
	int id = 0;
	String sql = "select "+tableName+"_ID from "+tableName+" where name=? and "
	    + tableNameMaster+"_ID=?";
	try {
	    PreparedStatement pstmt = DB.prepareStatement(sql);
	    pstmt.setString(1, name);
	    pstmt.setInt(2, masterID);
	    ResultSet rs = pstmt.executeQuery();
	    if (rs.next())
		id = rs.getInt(1);
	    rs.close();
	    pstmt.close();
	    pstmt = null;
	}
	catch (Exception e) {	   
	    log.log(Level.SEVERE, "getIDWithMasterID:"+e);
	}
	return id;
    }

    /**
     *	Receive notification of character data inside an element.
     *
     * 	@param ch buffer
     * 	@param start start
     * 	@param length length
     * 	@throws SAXException
     */
    // Not used right now.
    /*
    public void characters (char ch[], int start, int length)
	throws SAXException {
	log.info("characters: ");
    }   // characters
    */

    /**
     *	Receive notification of the end of an element.
     * 	@param uri namespace
     * 	@param localName simple name
     * 	@param qName qualified name
     * 	@throws SAXException
     */
    public void endElement (String uri, String localName, String qName) throws SAXException {
	// Check namespace.
	String elementValue = null;
	if ("".equals (uri))
	    elementValue = qName;
	else
	    elementValue = uri + localName;
	if (false)
	    log.info("endElement: "+elementValue);

	// menu element.
	if (elementValue.equals("menu")) {
	    // There was a precedent Window.
	    if (m_Window != null) {
		m_Menu[m_MenuIndex].setAD_Window_ID(m_Window.getAD_Window_ID());
		m_Menu[m_MenuIndex].setAction(reverseReference("AD_Menu Action", "Window"));
		m_Menu[m_MenuIndex].setIsSummary(false);
		if (m_Menu[m_MenuIndex].getName().equals(TEMP_NAME)) // Name from the contained object.
		    m_Menu[m_MenuIndex].setName(m_Window.getName());
		m_Window = null;
	    } else if (m_Process != null) {
		// Precedent Process.
		m_Menu[m_MenuIndex].setAD_Process_ID(m_Process.getAD_Process_ID());
		m_Menu[m_MenuIndex].setAction(reverseReference("AD_Menu Action", "Process"));
		m_Menu[m_MenuIndex].setIsSummary(false);
		if (m_Menu[m_MenuIndex].getName().equals(TEMP_NAME)) // Name from the contained object.
		    m_Menu[m_MenuIndex].setName(m_Process.getName());
		m_Process = null;
	    } else {
		m_Menu[m_MenuIndex].setIsSummary(true);
	    }
	    m_Menu[m_MenuIndex].save();
	    MTree_Base m_TreeBase = new MTree_Base(Env.getCtx(), 10, null); // Menu.
	    MTree_NodeMM m_TreeNodeMM = MTree_NodeMM.get(m_TreeBase, m_Menu[m_MenuIndex].getAD_Menu_ID());
	    if (m_TreeNodeMM == null)
		m_TreeNodeMM = new MTree_NodeMM(m_TreeBase, m_Menu[m_MenuIndex].getAD_Menu_ID());
	    // There was an update to zero for the root, but X_AD_Menu transform 0 to null. So no update for roots.
	    // zero is placed by default.
	    if (m_MenuIndex > 0)
		m_TreeNodeMM.setParent_ID(m_Menu[m_MenuIndex-1].getAD_Menu_ID());
	    m_TreeNodeMM.setSeqNo(m_MenuSeqNo[m_MenuIndex]);
	    m_TreeNodeMM.save();
	    m_Menu[m_MenuIndex] = null;
	    --m_MenuIndex;
	}
	else if (elementValue.equals(TAB_TAG))
	    m_TabLevelNo--;

	// field element close.
	else if (elementValue.equals(FIELD_TAG) && openxpertyaAD) {
	    if (DEBUG) log.info (FIELD_TAG+" ends.");
	    boolean modified = false;
	    if (m_Process != null) {
		if (DEBUG)
		    log.info (FIELD_TAG+" ends: set ProcessID:"+ m_Process.getAD_Process_ID() +" for column:"+m_Column.getName());
		m_Column.setAD_Process_ID(m_Process.getAD_Process_ID());
		m_Process = null;
		modified = true;
	    }
	    if (modified)
		m_Column.save();
	    m_Field = null;
	    m_Column = null;
	}

	/* ****************************************
	   Druid Handler.
	   **************************************** */
	else if (elementValue.equals(TABLE_TAG) && druid) {
	    // Remove non existent column and field of the specified entitytype
	    // Dangerous if you use OpenXpertya or Dictionary entity type.
	    if (m_ColumnList.length() > 0) {
		// Delete Fields with the EntityType specified.
		String sql = "delete AD_Field where AD_Column_ID in (select AD_Column_ID"
		    +" from AD_Column where AD_Table_ID="+m_Table.getAD_Table_ID()
		    +" and AD_Column_ID not in ("+m_ColumnList+"))"
		    +" and EntityType='"+m_EntityType+"'";
		int no = DB.executeUpdate(sql);
		if (DEBUG)
		    log.info ("delete old fields for table:"+m_Table.getTableName() +", noColumn:" + no);
		// Delete Columns with the EntityType specified.
		sql = "delete AD_Column where AD_Table_ID="+m_Table.getAD_Table_ID()+" and AD_Column_ID not in ("+m_ColumnList+")"
		    +" and EntityType='"+m_EntityType+"'";
		no = DB.executeUpdate(sql);
		if (DEBUG)
		    log.info ("Delete old columns for table:"+m_Table.getTableName() +", noColumn:" + no);
	    }
	    m_ColumnList = "";
	}
	// column elememt, druid
	else if (elementValue.equals(COLUMN_TAG) && druid)
	    m_Column.save();   // save Column modified by attrib element.

	/* ****************************************
	   openxpertyadata Handler.
	   **************************************** */
	// row element, openxpertyadata
	else if (elementValue.equals(ROWDATA_TAG) && openxpertyadata) {
	    genericPO.save();
	    genericPO = null;
	}
	// default element, openxpertyadata
	if (elementValue.equals("default") && openxpertyadata) {
	    isDefaultData = false;
	}
    }   // endElement
}   // XML2ADHandler

//Marco LOMBARDO, 2004-08-20, Italy.
//lombardo@mayking.com


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007

